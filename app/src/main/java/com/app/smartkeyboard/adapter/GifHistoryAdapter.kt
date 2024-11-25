package com.app.smartkeyboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkeyboard.R
import com.app.smartkeyboard.bean.GifHistoryBean
import com.app.smartkeyboard.utils.BikeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import timber.log.Timber

/**
 * 传图的历史记录
 */
class GifHistoryAdapter constructor(private val context: Context,val list : MutableList<GifHistoryBean>) : RecyclerView.Adapter<GifHistoryAdapter.GifHistoryViewHolder>(){

    private var onClick : OnCommMenuClickListener ?=null

    fun setOnItemClick(c : OnCommMenuClickListener){
        this.onClick = c
    }

    class GifHistoryViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val itemGifHistoryImg : ImageView = view.findViewById(R.id.itemGifHistoryImg)
        val itemGifDeleteImg : ImageView = view.findViewById(R.id.itemGifDeleteImg)
        val itemGifHistoryTimeTv : TextView = view.findViewById(R.id.itemGifHistoryTimeTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifHistoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_gif_show_item_layout,parent,false)
        return GifHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GifHistoryViewHolder, position: Int) {
        val bean = list[position]
        holder.itemGifDeleteImg.setOnClickListener {
            onClick?.onChildItemClick(holder.layoutPosition)
        }

        holder.itemGifHistoryImg.setOnClickListener {
            onClick?.onItemClick(holder.layoutPosition)
        }

        //判断是一代还是二代
        val type = bean.deviceType
        val filePath = bean.fileUrl
        if(bean.isDefaultAni){
            holder.itemGifDeleteImg.visibility = View.GONE
            holder.itemGifHistoryTimeTv.text = context.resources.getString(R.string.string_default_anim)
            Glide.with(context).load(R.drawable.gif_preview)
                .transform(MultiTransformation(CenterCrop(), CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.gif_preview)
                .into(holder.itemGifHistoryImg)
            return
        }

        Timber.e("------------type="+type+" filePath="+filePath)

        if(filePath == "+"){
            holder.itemGifDeleteImg.visibility = View.GONE
            holder.itemGifHistoryTimeTv.text = context.resources.getString(R.string.string_selector_cus)
            if(type == 1){  //一代
                Glide.with(context).load(R.mipmap.ic_second_add_gif)
                    .into(holder.itemGifHistoryImg)
            }else{  //二代
                Glide.with(context).load(R.mipmap.ic_second_add_gif)
                    .transform(MultiTransformation(CenterCrop(), CircleCrop())).skipMemoryCache(false).into(holder.itemGifHistoryImg)

            }
        }else{
            holder.itemGifDeleteImg.visibility = View.VISIBLE
            holder.itemGifHistoryTimeTv.text = BikeUtils.getFormatDate(bean.saveTime,"MM/dd :HH:mm:ss")

            if(type == 1){  //一代
                Glide.with(context).load(filePath)
                    .into(holder.itemGifHistoryImg)
            }else{  //二代
                Glide.with(context).load(filePath)
                    .transform(MultiTransformation(CenterCrop(), CircleCrop())).skipMemoryCache(false).into(holder.itemGifHistoryImg)

            }
        }



    }
}