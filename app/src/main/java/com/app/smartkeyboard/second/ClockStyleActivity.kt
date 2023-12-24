package com.app.smartkeyboard.second

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.hjq.shape.view.ShapeTextView
import timber.log.Timber


/**
 * 时钟样式
 */
class ClockStyleActivity : AppActivity(){


    private var cbImg1 : ImageView ?= null
    private var cbImg2 : ImageView ?= null
    private var cbImg3 : ImageView ?= null
    private var cbImg4 : ImageView ?= null

    private var clockShowImg :ImageView ?= null

    private var defaultIndex = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_clock_style_layout
    }

    override fun initView() {

        clockShowImg = findViewById(R.id.clockShowImg)
        cbImg1 = findViewById(R.id.cbImg1)
        cbImg2 = findViewById(R.id.cbImg2)
        cbImg3 = findViewById(R.id.cbImg3)
        cbImg4 = findViewById(R.id.cbImg4)



        findViewById<FrameLayout>(R.id.cSLy1).setOnClickListener {
            showClick(0)
        }
        findViewById<FrameLayout>(R.id.cSLy2).setOnClickListener {
            showClick(1)
        }
        findViewById<FrameLayout>(R.id.cSLy3).setOnClickListener {
            showClick(2)
        }
        findViewById<FrameLayout>(R.id.cSLy4).setOnClickListener {
            showClick(3)
        }

        findViewById<ShapeTextView>(R.id.clockSubmitTv).setOnClickListener {
            BaseApplication.getBaseApplication().bleOperate.setScreenStyleOrClockStyle(true,defaultIndex)
        }

    }

    override fun initData() {
        showClick(0)
    }


    private val imgArray = arrayListOf<Int>(R.mipmap.ic_c_b1,R.mipmap.ic_c_b2,R.mipmap.ic_c_b3,R.mipmap.ic_c_b4)
    private fun showClick(index : Int){
        this.defaultIndex = index
        Timber.e("------------index="+index)
        cbImg1?.visibility = if(index == 0) View.VISIBLE else View.INVISIBLE
        cbImg2?.visibility = if(index == 1) View.VISIBLE else View.INVISIBLE
        cbImg3?.visibility = if(index == 2) View.VISIBLE else View.INVISIBLE
        cbImg4?.visibility = if(index == 3) View.VISIBLE else View.INVISIBLE
        clockShowImg?.setImageResource(imgArray[index])


    }

}