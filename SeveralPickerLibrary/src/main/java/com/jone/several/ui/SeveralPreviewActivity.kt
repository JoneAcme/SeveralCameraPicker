package com.jone.several.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.jone.several.R
import com.jone.several.config.PickerConstant
import com.jone.several.config.SeveralPickerOption
import com.jone.several.model.EventEntity
import com.jone.several.model.MediaEntity
import com.jone.several.model.rxbus.RxBus
import com.jone.several.ui.adapter.SimpleFragmentAdapter
import com.jone.several.utils.ScreenUtil
import com.jone.several.utils.showToast
import kotlinx.android.synthetic.main.activity_several_preview.*

class SeveralPreviewActivity : PickerBaseActivity(), View.OnClickListener {

    private lateinit var mVpAdapter: SimpleFragmentAdapter

    private lateinit var allMediaList: MutableList<MediaEntity>
    private lateinit var pickedMediaList: MutableList<MediaEntity>
    private var previewType = PickerConstant.TYPE_PREIVEW_FROM_PICK
    private var animation: Animation? = null
    private var position = 0
    private var index = 0
    private var screenWidth: Int = 0
    private var isFullScreen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_several_preview)
        initViews()

    }

    private fun initViews() {
        screenWidth = ScreenUtil.getScreenWidth(this)
        allMediaList = intent.extras.getSerializable(PickerConstant.KEY_ALL_LIST) as MutableList<MediaEntity>
        pickedMediaList = intent.extras.getSerializable(PickerConstant.KEY_PICK_LIST) as MutableList<MediaEntity>
        previewType = intent.extras.getInt(PickerConstant.KEY_PREVIEW_TYPE, PickerConstant.TYPE_PREIVEW_FROM_PICK)
        position = intent.extras.getInt(PickerConstant.KEY_POSITION, 0)
        if (allMediaList.isNotEmpty()) {
            val mediaEntity = allMediaList[position]
            index = mediaEntity.getPosition()
        }
        pickTvTitle.text = String.format("%d/%d", position + 1, allMediaList.size)
        mVpAdapter = SimpleFragmentAdapter(this, allMediaList)
        preview_pager.adapter = mVpAdapter
        preview_pager.currentItem = position
        preview_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                isPreviewEggs(option.previewEggs, position, positionOffsetPixels)
            }

            override fun onPageSelected(i: Int) {
                position = i
                pickTvTitle.text = String.format("%d/%d", position + 1, allMediaList.size)
                val mediaEntity = allMediaList[position]
                index = mediaEntity.getPosition()
                if (!option.previewEggs) {
                    onImageChecked(position)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        animation = AnimationUtils.loadAnimation(this, R.anim.picker_anim_window_in)
        onPickNumberChange()
        pickTvBack.setOnClickListener(this)
        ll_check.setOnClickListener(this)
        preview_ll_ok.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.pickTvBack -> {
                finish()

            }
            R.id.ll_check -> {
                checkView()
            }
            R.id.preview_ll_ok -> {
                pickerViewPreviewFinish()
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun checkView() {
        if (allMediaList.isEmpty()) return
        val image = allMediaList[preview_pager.currentItem]

        // 刷新图片列表中图片状态
        val isChecked = tv_check.isSelected
        if (pickedMediaList.size >= option.maxPickNumber && !isChecked) {

            showToast(getString(R.string.message_max_number, option.maxPickNumber))
            return
        }

        if (isChecked) {
            tv_check.isSelected = false
            for (mediaEntity in pickedMediaList) {
                if (mediaEntity.localPath == image.localPath) {
                    pickedMediaList.remove(mediaEntity)
                    subSelectPosition()
                    break
                }
            }
        } else {
            tv_check.isSelected = true
            pickedMediaList.add(image)
            image.number = pickedMediaList.size
        }
        onPickNumberChange()
        updatePickerActivity()
    }

    private fun pickerViewPreviewFinish() {
        val images = pickedMediaList
        val size = images.size

        // 如果设置了图片最小选择数量，则判断是否满足条件
        if (option.minPickNumber > 0) {
            @SuppressLint("StringFormatMatches")
            if (size < option.minPickNumber) {
                showToast(getString(R.string.picture_min_img_num, option.minPickNumber))
                return
            }
        }
        complitePreview()
    }


    /**
     * 这里没实际意义，好处是预览图片时 滑动到屏幕一半以上可看到下一张图片是否选中了
     * @param previewEggs          是否显示预览友好体验
     * *
     * @param positionOffsetPixels 滑动偏移量
     */
    private fun isPreviewEggs(previewEggs: Boolean, position: Int, positionOffsetPixels: Int) {
        if (previewEggs) {
            if (allMediaList.size > 0) {
                val mediaEntity: MediaEntity
                val num: Int
                if (positionOffsetPixels < screenWidth / 2) {
                    mediaEntity = allMediaList[position]
                    tv_check.isSelected = isSelected(mediaEntity)
                } else {
                    mediaEntity = allMediaList[position + 1]
                    tv_check.isSelected = isSelected(mediaEntity)
                }
            }
        }
    }

    /**
     * 当前图片是否选中
     * @param image
     * *
     * @return
     */
    fun isSelected(image: MediaEntity): Boolean {
        return pickedMediaList.any { it.localPath == image.localPath }
    }

    /**
     * 判断当前图片是否选中
     * @param position
     */
    fun onImageChecked(position: Int) {
        if (allMediaList.isNotEmpty()) {
            val mediaEntity = allMediaList[position]
            tv_check.isSelected = isSelected(mediaEntity)
        } else {
            tv_check.isSelected = false
        }
    }

    /**
     * 更新选择的顺序
     */
    private fun subSelectPosition() {
        run {
            var index = 0
            val len = pickedMediaList.size
            while (index < len) {
                val mediaEntity = pickedMediaList[index]
                mediaEntity.number = index + 1
                index++
            }
        }
    }


    /**
     * 更新图片选择数量
     */
    private fun onPickNumberChange() {
        val enable = pickedMediaList.size > 0
        if (enable) {
            preview_ll_ok.isEnabled = true
            preview_ll_ok.alpha = 1F

            preview_tv_ok_number.startAnimation(animation)
            preview_tv_ok_number.visibility = View.VISIBLE
            preview_tv_ok_number.text = "(" + pickedMediaList.size.toString() + ")"
            preview_tv_ok_text.text = getString(R.string.picture_completed)
        } else {
            preview_ll_ok.isEnabled = false
            preview_ll_ok.alpha = 0.7F
            preview_tv_ok_number.visibility = View.GONE
            preview_tv_ok_text.text = getString(R.string.picture_please_select)
        }
    }

    /**
     * 更新图片列表选中效果
     * @param isRefresh isRefresh
     */
    private fun updatePickerActivity() {
        val obj = EventEntity(PickerConstant.FLAG_PREVIEW_UPDATE_SELECT, pickedMediaList, index)
        RxBus.default.post(obj)
    }

    private fun complitePreview() {
        completePicker(pickedMediaList)
    }


}
