package com.app.smartkeyboard.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.app.smartkeyboard.R
import com.app.smartkeyboard.utils.BikeUtils
import com.blala.blalable.blebean.UseTimeBean

import com.bonlala.widget.utils.MiscUtil
import com.google.gson.Gson
import timber.log.Timber


class CusHistogramChartView : View {


    private var mWidth : Float ?= null
    private var mHeight : Float ?= null
    //绘制背景柱子的画笔
    private var bgColumnarPaint : Paint ?= null
    //绘制进度的画笔
    private var valueColumnarPaint : Paint ?= null
    //x坐标轴的画笔
    private var xPaint : Paint ?= null

    private var noDataPaint : Paint ?= null

    private var borderPaint : Paint ?= null

    //数据源
    private var sourceList = mutableListOf<UseTimeBean>()


    private val singleBorder = MiscUtil.dipToPx(context,35F)


    constructor(context: Context) : super (context){

    }

    constructor(context: Context, attributeSet: AttributeSet) : super (context,attributeSet){
        initAttribute(context,attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defaultValue : Int) : super (context,attributeSet,defaultValue){
        initAttribute(context,attributeSet)
    }


    private fun initAttribute(context: Context,attributeSet: AttributeSet){
        initPaint()
    }


    private fun initPaint(){
        bgColumnarPaint  = Paint(Paint.ANTI_ALIAS_FLAG)
        bgColumnarPaint?.color = Color.parseColor("#343348")
        bgColumnarPaint?.strokeWidth = MiscUtil.dipToPx(context,2F).toFloat()
        bgColumnarPaint?.isAntiAlias = true
        bgColumnarPaint?.style = Paint.Style.FILL


        valueColumnarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        valueColumnarPaint?.style = Paint.Style.FILL
        valueColumnarPaint?.isAntiAlias = true
        valueColumnarPaint?.strokeWidth = 2F



        xPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        xPaint?.style = Paint.Style.FILL_AND_STROKE
        xPaint?.strokeWidth = 2F
        xPaint?.color = Color.WHITE
        xPaint?.textSize = MiscUtil.dipToPxFloat(context,10F)

        noDataPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        noDataPaint?.style = Paint.Style.FILL_AND_STROKE
        noDataPaint?.color = Color.WHITE
        noDataPaint?.strokeWidth = 0.5F
        noDataPaint?.textSize = MiscUtil.dipToPxFloat(context,15F)

        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint?.style = Paint.Style.FILL
        borderPaint?.color = Color.parseColor("#343348")
        borderPaint?.strokeWidth = 1F

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth.toFloat()
        mHeight = measuredHeight.toFloat()

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

      //  canvasChart(canvas)

        canvasLineChart(canvas)
    }


    private fun canvasLineChart(canvas: Canvas?){


        //最大值
        if(sourceList.size == 0){

            val noData = resources.getString(R.string.string_no_data)
            canvas?.drawText(noData, (mWidth?.div(2) ?: 0F) -MiscUtil.getTextWidth(noDataPaint!!,noData)/2, (mHeight?.div(2) ?: 0F) -MiscUtil.measureTextHeight(noDataPaint)/2,noDataPaint!!)
            return
        }

        //最大7个
        val avgWidth = mWidth!! / sourceList.size
        var maxTime = 0
        //获取最大值
        sourceList.forEach {
            if(it.time>maxTime){
                maxTime = it.time
            }
        }

        //单个的柱子
        val singleWidth = avgWidth / 2

        val tmpTxt = "22"
        val tmpH = MiscUtil.measureTextHeight(xPaint!!)
        val model = (mHeight!!- tmpH)/(if(maxTime>200) maxTime else 200)

        sourceList.forEachIndexed { index, useTimeBean ->



            //文字
            val xTxt = String.format("%02d",useTimeBean.day)
            val txtWidth = MiscUtil.getTextWidth(xPaint!!,xTxt)
            val txtHeight = MiscUtil.measureTextHeight(xPaint)

            val x = index * avgWidth+singleWidth
            canvas?.drawText(xTxt,x-txtWidth/2,mHeight!!-txtHeight,xPaint!!)

            val timeTxt = BikeUtils.formatMinuteToHour(useTimeBean.time)



            val itemValueHeight = useTimeBean.time*model

            val itemRectF = RectF()
            itemRectF.left = x-singleBorder/2
            itemRectF.top = mHeight!!-itemValueHeight
            itemRectF.right = x+singleBorder/2
            itemRectF.bottom =(mHeight!!- tmpH*3)

            val tXW = MiscUtil.getTextWidth(xPaint,timeTxt)
            canvas?.drawText(timeTxt,x-tXW/2,itemRectF.top-txtHeight,xPaint!!)

            val intA = IntArray(2)
            intA[0] = Color.parseColor("#00BAAD")
            intA[1] = Color.parseColor("#FF5733")
            val shader = LinearGradient(itemRectF.right,itemRectF.bottom,itemRectF.left,itemRectF.top,
                intA,null,Shader.TileMode.CLAMP)
            valueColumnarPaint?.setShader(shader)

            canvas?.drawRoundRect(itemRectF,
                (singleBorder/2).toFloat(), (singleBorder/2).toFloat(),valueColumnarPaint!!)


        }


    }

    //绘制图表
    private fun canvasChart(canvas: Canvas?){

        //最大值
        if(sourceList.size == 0){

            val noData = resources.getString(R.string.string_no_data)
            canvas?.drawText(noData, (mWidth?.div(2) ?: 0F) -MiscUtil.getTextWidth(noDataPaint!!,noData)/2, (mHeight?.div(2) ?: 0F) -MiscUtil.measureTextHeight(noDataPaint)/2,noDataPaint!!)
            return
        }

        val avgWidth = mWidth?.div(sourceList.size)
        val singleWidth = avgWidth?.minus(avgWidth/3)

        val radius = singleWidth?.div(2)
        //间隔
        val intervalWidth = (avgWidth?.div(2) ?: 0F) - (singleWidth?.div(2) ?: 0F)

        val model = mHeight?.div(200)

        sourceList.forEachIndexed { index, useTimeBean ->


            val xTxt = String.format("%02d",useTimeBean.day)
            val xTxtWidth = MiscUtil.getTextWidth(xPaint!!,xTxt)

            val x = (avgWidth?.div(2) ?: 0F) + (avgWidth?.div(1) ?: 0F) *index-xTxtWidth/2
            canvas?.drawText(xTxt,x, mHeight?.minus(MiscUtil.measureTextHeight(xPaint!!)) ?: 0F,xPaint!!)

            //绘制柱子
            val topLeft = intervalWidth+ (avgWidth?.times(index) ?: 0F)
            val topRight = mHeight!!- (model?.times(useTimeBean.time))?.toFloat()!!
            val bottomX = (avgWidth?.times(index) ?: 0F) -intervalWidth+avgWidth!!
            val rectf = topRight.let {
                RectF(topLeft,
                    it,bottomX, mHeight?.minus(MiscUtil.measureTextHeight(xPaint)*3) ?: 0F
                )
            }


            val bPath = Path()
            bPath.addRoundRect(RectF(topLeft,topRight,singleWidth!!,(mHeight!!-MiscUtil.measureTextHeight(xPaint)*3)),singleWidth/2,singleWidth/2,Path.Direction.CCW)
            //   canvas?.clipPath(bPath)
            canvas?.drawPath(bPath,borderPaint!!)

         //   canvas?.drawRect(rectf,bgColumnarPaint!!)

           // canvas?.drawRoundRect(rectf, arrayOf(20F, 20F, 0F, 0F, 0F, 0F, 20F, 20F),bgColumnarPaint!!)
            val path = android.graphics.Path()


            path.addRoundRect(rectf,radius!!, radius,android.graphics.Path.Direction.CCW)
//            if (singleWidth != null) {
//                path.moveTo(topLeft,topRight-singleWidth/2)
//                path.moveTo(topLeft+singleWidth/2,topRight)
//                path.moveTo(bottomX,topRight-singleWidth/2)
//            }
//            canvas?.clipPath(path)
            val intA = IntArray(2)
            intA[0] = Color.parseColor("#00BAAD")
            intA[1] = Color.parseColor("#FF5733")
            val shader = LinearGradient(rectf.right,rectf.bottom,rectf.left,rectf.top,
                intA,null,Shader.TileMode.CLAMP)
            bgColumnarPaint?.setShader(shader)
            canvas?.drawPath(path,bgColumnarPaint!!)


//            valueColumnarPaint?.shader = shader
//            canvas?.drawRect()

        }

    }


    fun setDataSource(list : MutableList<UseTimeBean>){
        Timber.e("-----source="+Gson().toJson(list))
        sourceList.clear()
        sourceList.addAll(list)
        invalidate()
    }
}