package com.jone.several.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.jone.several.R
import com.jone.several.model.MediaEntity
import com.jone.several.model.MediaFolder
import com.jone.several.ui.adapter.PickerAlbumAdapter
import com.jone.several.utils.ScreenUtil
import com.jone.several.utils.StringUtils

/**
 * @fileName FolderPopWindow
 * Created by YiangJone on 2018/6/6.
 * @describe
 */


class FolderPopWindow(private val context: Context) : PopupWindow(), View.OnClickListener {
    private val window: View = LayoutInflater.from(context).inflate(R.layout.pop_several_window_folder, null)
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PickerAlbumAdapter
    private val animationIn: Animation
    private val animationOut: Animation
    private var isDismiss = false
    private var id_ll_root: LinearLayout? = null
    private var picture_title: TextView? = null
    private val drawableUp: Drawable
    private val drawableDown: Drawable

    init {
        this.contentView = window
        this.width = ScreenUtil.getScreenWidth(context)
        this.height = ScreenUtil.getScreenHeight(context)
        this.animationStyle = R.style.picker_style_window
        this.isFocusable = true
        this.isOutsideTouchable = true
        this.update()
        this.setBackgroundDrawable(ColorDrawable(Color.argb(123, 0, 0, 0)))
        drawableUp = ContextCompat.getDrawable(context, R.drawable.picker_arrow_up)!!
        drawableDown = ContextCompat.getDrawable(context, R.drawable.picker_arrow_down)!!
        animationIn = AnimationUtils.loadAnimation(context, R.anim.picker_anim_album_show)
        animationOut = AnimationUtils.loadAnimation(context, R.anim.picker_anim_album_dismiss)
        initView()
    }

    fun initView() {
        id_ll_root = window.findViewById<LinearLayout>(R.id.id_ll_root)
        adapter = PickerAlbumAdapter(context)
        recyclerView = window.findViewById<RecyclerView>(R.id.folder_list)
        recyclerView.layoutParams.height = (ScreenUtil.getScreenHeight(context) * 0.6).toInt()
        recyclerView.addItemDecoration(RecycleViewDivider(
                context, LinearLayoutManager.HORIZONTAL, ScreenUtil.dip2px(context, 0f), ContextCompat.getColor(context, R.color.transparent)))
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        id_ll_root!!.setOnClickListener(this)
    }

    fun bindFolder(folders: List<MediaFolder>) {
        adapter!!.bindFolderData(folders)
    }

    fun setPictureTitleView(picture_title: TextView) {
        this.picture_title = picture_title
    }


    fun show(anchor: View, xoff: Int=0, yoff: Int=0) {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                val visibleFrame = Rect()
                anchor.getGlobalVisibleRect(visibleFrame)
                val height = anchor.resources.displayMetrics.heightPixels - visibleFrame.bottom
                this.height = height
                this.showAsDropDown(anchor, xoff, yoff)
            } else {
                this.showAsDropDown(anchor, xoff, yoff)
            }

            isDismiss = false
            recyclerView.startAnimation(animationIn)
            StringUtils.modifyTextViewDrawable(picture_title!!, drawableUp, 2)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setOnItemClickListener(onItemClickListener: PickerAlbumAdapter.OnItemClickListener) {
        adapter!!.setOnItemClickListener(onItemClickListener)
    }

    override fun dismiss() {
        if (isDismiss) {
            return
        }
        StringUtils.modifyTextViewDrawable(picture_title!!, drawableDown, 2)
        isDismiss = true
        recyclerView.startAnimation(animationOut)
        dismiss()
        animationOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                isDismiss = false
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                    dismiss4Pop()
                } else {
                    super@FolderPopWindow.dismiss()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    /**
     * 在android4.1.1和4.1.2版本关闭PopWindow
     */
    private fun dismiss4Pop() {
        Handler().post { super@FolderPopWindow.dismiss() }
    }


    /**
     * 设置选中状态
     */
    fun notifyDataCheckedStatus(mediaEntities: List<MediaEntity>) {
        try {
            // 获取选中图片
            val folders = adapter!!.folderData
            for (folder in folders) {
                folder.checkedNumber = 0
            }
            if (mediaEntities.size > 0) {
                for (folder in folders) {
                    var num = 0// 记录当前相册下有多少张是选中的
                    val images = folder.images
                    for (mediaEntity in images!!) {
                        val path = mediaEntity.localPath
                        for (m in mediaEntities) {
                            if (path == m.localPath) {
                                num++
                                folder.checkedNumber = num
                            }
                        }
                    }
                }
            }
            adapter!!.bindFolderData(folders)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.id_ll_root) {
            dismiss()
        }
    }

}
