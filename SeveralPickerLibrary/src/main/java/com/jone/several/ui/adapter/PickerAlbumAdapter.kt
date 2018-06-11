package com.jone.several.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jone.several.R
import com.jone.several.model.MediaEntity
import com.jone.several.model.MediaFolder
import com.jone.several.utils.loadImage
import kotlinx.android.synthetic.main.picker_item_album_folder.view.*
import java.util.ArrayList

/**
 * @fileName PickerAlbumAdapter
 * Created by YiangJone on 2018/6/6.
 * @describe
 */


class PickerAlbumAdapter(private val mContext: Context) : RecyclerView.Adapter<PickerAlbumAdapter.ViewHolder>() {

    private var folders: List<MediaFolder> = ArrayList()

    fun bindFolderData(folders: List<MediaFolder>) {
        this.folders = folders
        notifyDataSetChanged()
    }


    val folderData: List<MediaFolder>
        get() {
            if (folders == null) {
                folders = ArrayList<MediaFolder>()
            }
            return folders
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.picker_item_album_folder, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]
        val name = folder.name
        val imageNum = folder.imageNumber
        val imagePath = folder.firstImagePath
        val isChecked = folder.isChecked
        val checkedNum = folder.checkedNumber
        holder.tv_sign.visibility = if (checkedNum > 0) View.VISIBLE else View.INVISIBLE
        holder.itemView.isSelected = isChecked
        mContext.loadImage(imagePath,holder.itemView.first_image)
        holder.image_num.text = "($imageNum)"
        holder.tv_folder_name.text = name
        holder.itemView.setOnClickListener {
            if (onItemClickListener != null) {
                for (mediaFolder in folders) {
                    mediaFolder.isChecked = false
                }
                folder.isChecked = true
                notifyDataSetChanged()
                onItemClickListener!!.onItemClick(folder.name, folder.images)
            }
        }
    }

    override fun getItemCount(): Int {
        return folders!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var first_image: ImageView
        var tv_folder_name: TextView
        var image_num: TextView
        var tv_sign: TextView

        init {
            first_image = itemView.findViewById(R.id.first_image)
            tv_folder_name = itemView.findViewById(R.id.tv_folder_name)
            image_num = itemView.findViewById(R.id.image_num)
            tv_sign = itemView.findViewById(R.id.tv_sign)
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(folderName: String, images: MutableList<MediaEntity>)
    }
}
