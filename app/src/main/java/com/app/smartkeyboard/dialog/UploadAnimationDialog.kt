package com.app.smartkeyboard.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatDialog
import com.app.smartkeyboard.R
import com.app.smartkeyboard.adapter.OnCommItemClickListener
import com.hjq.shape.view.ShapeTextView

/**
 * 删除设备的dialog
 */
class UploadAnimationDialog : AppCompatDialog,OnClickListener{



    //item点击
    private var onItemClickListener : OnCommItemClickListener?= null

    fun setOnCommClickListener(onclick : OnCommItemClickListener){
        this.onItemClickListener = onclick
    }

    //上传到设备
    private var uploadDialogUploadTv : ShapeTextView ?= null
    //删除设备
    private var uploadDialogDeleteTv : ShapeTextView ?= null
    //自定义速度并上传
    private var uploadDialogCusSpeedUpTv : ShapeTextView ?= null
    //取消
    private var uploadDialogCancelTv : ShapeTextView ?= null



    constructor(context: Context) : super (context){

    }


    constructor(context: Context, theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_upload_animation_layout)

        uploadDialogDeleteTv = findViewById(R.id.uploadDialogDeleteTv)
        uploadDialogUploadTv = findViewById(R.id.uploadDialogUploadTv)
        uploadDialogCusSpeedUpTv = findViewById(R.id.uploadDialogCusSpeedUpTv)
        uploadDialogCancelTv = findViewById(R.id.uploadDialogCancelTv)

        uploadDialogDeleteTv?.setOnClickListener(this)
        uploadDialogUploadTv?.setOnClickListener(this)
        uploadDialogCusSpeedUpTv?.setOnClickListener(this)
        uploadDialogCancelTv?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
       val id = v?.id
        //取消
        if(id == R.id.uploadDialogCancelTv){
            onItemClickListener?.onItemClick(0x00)
        }
        //上传到设备
        if(id == R.id.uploadDialogUploadTv){
            onItemClickListener?.onItemClick(0x01)
        }
        //自定义速度
        if(id == R.id.uploadDialogCusSpeedUpTv){
            onItemClickListener?.onItemClick(0x03)
        }
        //删除动画
        if(id == R.id.uploadDialogDeleteTv){
            onItemClickListener?.onItemClick(0x02)
        }
    }


    //判断是否是GIF，图片没有自定义速度上传
    fun setVisibility(isGif : Boolean){
        uploadDialogCusSpeedUpTv?.visibility = if(isGif) View.VISIBLE else View.GONE
    }

}