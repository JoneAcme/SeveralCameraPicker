package com.jone.sevral.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.jone.sevral.R
import com.jone.sevral.config.PickerConstant
import com.jone.sevral.config.PickerOption
import com.jone.sevral.model.EventEntity
import com.jone.sevral.model.MediaEntity
import com.jone.sevral.model.MediaFolder
import com.jone.sevral.model.rxbus.RxBus
import com.jone.sevral.model.rxbus.Subscribe
import com.jone.sevral.model.rxbus.ThreadMode
import com.jone.sevral.ui.adapter.PickerAdapter
import com.jone.sevral.ui.adapter.PickerAlbumAdapter
import com.jone.sevral.ui.widget.FolderPopWindow
import com.jone.sevral.ui.widget.GridSpacingItemDecoration
import com.jone.sevral.utils.*
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_picker.*
import kotlinx.android.synthetic.main.include_title_bar_picker.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class PickerActivity : FragmentActivity(), PickerAdapter.OnPickChangedListener, PickerAlbumAdapter.OnItemClickListener, View.OnClickListener {


    private var mContext: Context by Delegates.notNull()
    private var mAdapter: PickerAdapter by Delegates.notNull()
    private var spanCount = 4
    private var mediaList: MutableList<MediaEntity> = ArrayList()
    private var allMediaList: MutableList<MediaEntity> = ArrayList()
    private var allFolderList: MutableList<MediaFolder> = ArrayList()
    private lateinit var folderWindow: FolderPopWindow
    private var isAnimation = false
    private val pickerOption = PickerOption()
    private val loadingDialog: Dialog by lazy { createLoadingDialog() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)
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

        var itemValue = minOf(screenWidth, screenHeight) / pickerOption.spanCount
        pickerOption.thumbnailWidth = itemValue
        pickerOption.thumbnailHeight = itemValue
        mAdapter = PickerAdapter(mContext, pickerOption)
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
                dismissLoadingDialog()
            }
        })
    }

    private fun ininPop() {
        folderWindow = FolderPopWindow(this)
        folderWindow.setPictureTitleView(pickTvTitle)
        folderWindow.setOnItemClickListener(this)
    }

    override fun onResume() {
        super.onResume()
//        initDatas()
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
                        folderWindow.showAsDropDown(pickRlTitle)
                        val selectedImages = mAdapter.getPickMediaList()
                        folderWindow.notifyDataCheckedStatus(selectedImages)
                    }
                }
            }
            R.id.pickTvPreview -> {
                Navigator.showPreviewView(this, pickerOption, mAdapter.getPickMediaList(), mAdapter.getPickMediaList(), 0)
            }
            R.id.pickLlOk -> {
                val pickMediaList = mAdapter.getPickMediaList()
                if (pickMediaList.isEmpty()) {
                    return
                }
                processMedia(pickMediaList)
            }
        }
    }

    override fun onTakePhoto() {
        RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({ agree ->
                    if (agree) {
                        Navigator.showCameraView(this, pickerOption)
                    }
                }, {

                })
    }

    override fun onChange(selectImages: List<MediaEntity>) {
        changeImageNumber(selectImages)
    }

    override fun onPictureClick(mediaEntity: MediaEntity, position: Int) {
        Navigator.showPreviewView(this, pickerOption, mAdapter.getAllMediaList(), mAdapter.getPickMediaList(), position)
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
                val isExceedMax = selectImages.size >= pickerOption.maxPickNumber && pickerOption.maxPickNumber != 0
                mAdapter.isExceedMax = isExceedMax
                if (isExceedMax || selectImages.size == pickerOption.maxPickNumber - 1) {
                    mAdapter.notifyDataSetChanged()
                } else {
                    mAdapter.notifyItemChanged(position)
                }
            }
            PickerConstant.FLAG_PREVIEW_COMPLETE -> {
                val mediaEntities = obj.mediaEntities
                processMedia(mediaEntities)
            }
        }
    }


    private fun processMedia(mediaList: MutableList<MediaEntity>) {
        compressMedias(pickerOption, mediaList, loadingDialog, { mutableList -> })
    }


    override fun onDestroy() {
        super.onDestroy()
        if (RxBus.default.isRegistered(this)) {
            RxBus.default.unregister(this)
        }
    }


    private fun showLoadingDialog() {
        if (!isFinishing)
            loadingDialog.show()
    }

    private fun dismissLoadingDialog() {
        if (loadingDialog.isShowing)
            loadingDialog.dismiss()
    }
}
