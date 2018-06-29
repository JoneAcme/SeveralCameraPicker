package com.jone.several.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.jone.several.R
import com.jone.several.config.PickerConstant
import com.jone.several.config.SeveralPickerOption
import com.jone.several.model.EventEntity
import com.jone.several.model.MediaEntity
import com.jone.several.model.MediaFolder
import com.jone.several.model.rxbus.RxBus
import com.jone.several.model.rxbus.Subscribe
import com.jone.several.model.rxbus.ThreadMode
import com.jone.several.ui.adapter.PickerAdapter
import com.jone.several.ui.adapter.PickerAlbumAdapter
import com.jone.several.ui.widget.FolderPopWindow
import com.jone.several.ui.widget.GridSpacingItemDecoration
import com.jone.several.utils.*
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_several_picker.*
import kotlinx.android.synthetic.main.include_title_bar_several_picker.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class SeveralPickerActivity : PickerBaseActivity(), PickerAdapter.OnPickChangedListener, PickerAlbumAdapter.OnItemClickListener, View.OnClickListener {


    private var mContext: Context by Delegates.notNull()
    private var mAdapter: PickerAdapter by Delegates.notNull()
    private var spanCount = 4
    private var mediaList: MutableList<MediaEntity> = ArrayList()
    private var allMediaList: MutableList<MediaEntity> = ArrayList()
    private var allFolderList: MutableList<MediaFolder> = ArrayList()
    private lateinit var folderWindow: FolderPopWindow
    private var isAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_several_picker)
        mContext = this
        initViews()
        initDatas()
    }

    private fun initViews() {
        pickRecyclerView.setHasFixedSize(true)
        pickRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount,
                ScreenUtil.dip2px(this, 2f), false))
        pickRecyclerView.layoutManager = GridLayoutManager(this, spanCount)
        (pickRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        val screenWidth = ScreenUtil.getScreenWidth(mContext)
        val screenHeight = ScreenUtil.getScreenHeight(mContext)

        var itemValue = minOf(screenWidth, screenHeight) / option.spanCount
        option.thumbnailWidth = itemValue
        option.thumbnailHeight = itemValue

        mAdapter = PickerAdapter(mContext)
        pickRecyclerView.adapter = mAdapter
        mAdapter.setOnPickChangedListener(this)
        mAdapter.setPickMediaList(mediaList)
        changeImageNumber(mediaList)
        pickTvPreview.setOnClickListener(this)
        pickTvBack.setOnClickListener(this)
        pickTvCancel.setOnClickListener(this)
        pickLlOk.setOnClickListener(this)
        pickTvTitle.setOnClickListener(this)
        ininPop()
        if (!RxBus.default.isRegistered(this)) RxBus.default.register(this)
    }

    private fun initDatas() {
        RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(
                        { isAgree ->
                            if (isAgree) getDatas() else
                                showToast(getString(R.string.picture_jurisdiction))
                        }, { eror ->
                    showToast(getString(R.string.picture_jurisdiction))
                })
    }

    private fun getDatas() {
        //todo 异步，需要添加一个Loading

        MediaLoader.loadAllMedia(this, object : MediaLoader.LocalMediaLoadListener {
            override fun loadComplete(folders: MutableList<MediaFolder>) {
                if (folders.size > 0) {
                    allFolderList = folders
                    val folder = folders[0]
                    folder.isChecked = true
                    val localImg = folder.images
                    // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                    // 因为onActivityResult里手动添加拍照后的照片，
                    // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                    if (localImg.size >= allMediaList.size) {
                        allMediaList = localImg
                        folderWindow.bindFolder(folders)
                    }
                }
                mAdapter.setAllMediaList(allMediaList)
                pick_tv_empty.visibility = if (allMediaList.size > 0) View.INVISIBLE else View.VISIBLE
            }
        })
    }

    private fun ininPop() {
        folderWindow = FolderPopWindow(this)
        folderWindow.setPictureTitleView(pickTvTitle)
        folderWindow.setOnItemClickListener(this)
    }


    override fun onItemClick(folderName: String, images: MutableList<MediaEntity>) {
        pickTvTitle.text = folderName
        mAdapter.setAllMediaList(images)
        folderWindow.dismiss()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.pickTvBack, R.id.pickTvCancel -> {
                if (folderWindow.isShowing) {
                    folderWindow.dismiss()
                } else {
                    finish()
                }
            }
            R.id.pickTvTitle -> {
                if (folderWindow.isShowing()) {
                    folderWindow.dismiss()
                } else {
                    if (allMediaList.size > 0) {
                        folderWindow.show(pickRlTitle)
                        val selectedImages = mAdapter.getPickMediaList()
                        folderWindow.notifyDataCheckedStatus(selectedImages)
                    }
                }
            }
            R.id.pickTvPreview -> {
                Jumper.showPreviewView(this, mAdapter.getPickMediaList(), mAdapter.getPickMediaList(), 0)
            }
            R.id.pickLlOk -> {
                val pickMediaList = mAdapter.getPickMediaList()
                if (pickMediaList.isEmpty()) {
                    return
                }
                completePicker(pickMediaList)
            }
        }
    }

    override fun onTakePhoto() {
        RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({ agree ->
                    if (agree) {
                        Jumper.showCameraView(this)
                    }
                }, {

                })
    }

    override fun onChange(selectImages: List<MediaEntity>) {
        changeImageNumber(selectImages)
    }

    override fun onPictureClick(mediaEntity: MediaEntity, position: Int) {
        Jumper.showPreviewView(this, mAdapter.getAllMediaList(), mAdapter.getPickMediaList(), position)
    }


    @SuppressLint("StringFormatMatches")
    private fun changeImageNumber(selectImages: List<MediaEntity>) {
        val enable = selectImages.isNotEmpty()
        if (enable) {
            pickLlOk.isEnabled = true
            pickLlOk.alpha = 1F
            pickTvPreview.isEnabled = true
            pickTvPreview.setTextColor(ContextCompat.getColor(mContext, R.color.green))
            if (!isAnimation) {
                pickTvNumber.startAnimation(getAnim())
            }
            pickTvNumber.visibility = View.VISIBLE
            pickTvNumber.text = String.format("(%d)", selectImages.size)
            pickTvOk.text = getString(R.string.picture_completed)
            isAnimation = false
        } else {
            pickLlOk.isEnabled = false
            pickLlOk.alpha = 0.7F
            pickTvPreview.isEnabled = false
            pickTvPreview.setTextColor(ContextCompat.getColor(mContext, R.color.color_gray_1))
            pickTvNumber.visibility = View.GONE
            pickTvOk.text = getString(R.string.picture_please_select)
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun getAnim(): Animation {
        pickTvOk.text = getString(R.string.picture_please_select)
        return AnimationUtils.loadAnimation(this, R.anim.picker_anim_window_in)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(obj: EventEntity) {
        when (obj.what) {
        //receive the select result from CameraPreviewActivity
            PickerConstant.FLAG_PREVIEW_UPDATE_SELECT -> {
                val selectImages = obj.mediaEntities
                isAnimation = selectImages.size > 0
                val position = obj.position
                mAdapter.setPickMediaList(selectImages)
                //通知点击项发生了改变
                val isExceedMax = selectImages.size >= option.maxPickNumber && option.maxPickNumber != 0
                mAdapter.isExceedMax = isExceedMax
                if (isExceedMax || selectImages.size == option.maxPickNumber - 1) {
                    mAdapter.notifyDataSetChanged()
                } else {
                    mAdapter.notifyItemChanged(position)
                }
            }
            PickerConstant.FLAG_PREVIEW_COMPLETE -> {
               finish()
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (RxBus.default.isRegistered(this)) {
            RxBus.default.unregister(this)
        }
    }

}
