package com.app.smartkeyboard.second

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.action.SingleClick
import com.bonlala.base.action.ClickAction

class SecondScreenStyleActivity : AppActivity() {


    private var scImg1 : ImageView ?= null
    private var scImg2 : ImageView ?= null
    private var scImg3 : ImageView ?= null
    private var scImg4 : ImageView ?= null

    private var scTxt1 : TextView ?= null
    private var scTxt2 : TextView ?= null
    private var scTxt3 : TextView ?= null
    private var scTxt4 : TextView ?= null


    override fun getLayoutId(): Int {
       return R.layout.activity_second_screen_style_layout
    }

    override fun initView() {
        scImg1 = findViewById(R.id.scImg1)
        scImg2 = findViewById(R.id.scImg2)
        scImg3 = findViewById(R.id.scImg3)
        scImg4 = findViewById(R.id.scImg4)

        scTxt1 = findViewById(R.id.scTxt1)
        scTxt2 = findViewById(R.id.scTxt2)
        scTxt3 = findViewById(R.id.scTxt3)
        scTxt4 = findViewById(R.id.scTxt4)

        scImg1?.setOnClickListener {
            checkIndex(0)
        }
        scImg2?.setOnClickListener {
            checkIndex(1)
        }
        scImg3?.setOnClickListener {
            checkIndex(2)
        }
        scImg4?.setOnClickListener {
            checkIndex(3)
        }
    }

    override fun initData() {
        checkIndex(0)
    }


    @SingleClick
    private fun checkIndex(index : Int){
        scImg1?.setImageResource(if(index == 0) R.mipmap.ic_s_c_1 else R.mipmap.ic_s_1)
        scImg2?.setImageResource(if(index == 1 )R.mipmap.ic_s_c_2 else R.mipmap.ic_s_2)
        scImg3?.setImageResource(if(index == 2) R.mipmap.ic_s_c_3 else R.mipmap.ic_s_3)
        scImg4?.setImageResource(if(index == 3) R.mipmap.ic_s_c_4 else R.mipmap.ic_s_2)

        //#8C8D91
        scTxt1?.setTextColor(if(index == 0)Color.parseColor("#FFFFFF") else Color.parseColor("#8C8D91"))
        scTxt2?.setTextColor(if(index == 1)Color.parseColor("#FFFFFF") else Color.parseColor("#8C8D91"))
        scTxt3?.setTextColor(if(index == 2)Color.parseColor("#FFFFFF") else Color.parseColor("#8C8D91"))
        scTxt4?.setTextColor(if(index == 3)Color.parseColor("#FFFFFF") else Color.parseColor("#8C8D91"))

        BaseApplication.getBaseApplication().bleOperate.setScreenStyleOrClockStyle(false,index)
    }
}