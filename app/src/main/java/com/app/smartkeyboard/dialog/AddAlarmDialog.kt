package com.app.smartkeyboard.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkeyboard.R
import com.app.smartkeyboard.adapter.AlarmWeekAdapter
import com.app.smartkeyboard.bean.AlarmWeekBean
import com.app.smartkeyboard.listeners.OnBackAlarmListener
import com.blala.blalable.Utils
import com.blala.blalable.blebean.AlarmBean
import com.bonlala.widget.view.StringScrollPicker
import com.google.gson.Gson
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import okhttp3.internal.toHexString
import okio.Utf8
import timber.log.Timber
import java.util.Calendar

class AddAlarmDialog : AppCompatDialog {

    private var backAlarmListener : OnBackAlarmListener?= null


    fun setBackListener(c : OnBackAlarmListener){
        this.backAlarmListener = c
    }

    private var alarmHourScrollPicker : StringScrollPicker ?= null
    private var alarmMinuteScrollPicker : StringScrollPicker ?= null

    private var defaultHourIndex = 0
    private var defaultMinuteIndex = 0

    private var editAlarmTitleBar : TitleBar ? = null

    private var editAlarmWeekRy : RecyclerView ?= null
    private var adapter : AlarmWeekAdapter ?= null
    private var weekList : MutableList<AlarmWeekBean> ?= null

    private var tempWeekList = mutableListOf<AlarmWeekBean>()

    private var editBean : AlarmBean ?= null

    constructor(context: Context) : super (context){

    }


    constructor(context: Context, theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_alarm_layout)

        initViews()
        initData()
    }

    private fun initViews(){
        alarmHourScrollPicker  = findViewById(R.id.alarmHourScrollPicker)
        alarmMinuteScrollPicker = findViewById(R.id.alarmMinuteScrollPicker)
        editAlarmTitleBar = findViewById(R.id.editAlarmTitleBar)

        editAlarmWeekRy = findViewById(R.id.editAlarmWeekRy)
        val gridLayoutManager = GridLayoutManager(context,7)
        editAlarmWeekRy?.layoutManager = gridLayoutManager
        weekList = ArrayList<AlarmWeekBean>()
        adapter = AlarmWeekAdapter(context, weekList as ArrayList<AlarmWeekBean>)
        editAlarmWeekRy?.adapter = adapter

        adapter?.setOnItemClick{
            val bean = weekList?.get(it)
            bean?.isChecked = !bean!!.isChecked
            adapter?.notifyItemChanged(it)
        }


        editAlarmTitleBar?.setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(view: View?) {


                dismiss()
            }

            override fun onTitleClick(view: View?) {

            }

            override fun onRightClick(view: View?) {
                //小时
                val hour = alarmHourScrollPicker?.data?.get(defaultHourIndex)
                val minute = alarmMinuteScrollPicker?.data?.get(defaultMinuteIndex)

                var repeatCount = 0
                weekList?.forEach {
                    if(it.isChecked){
                        repeatCount+=it.weekNumber
                    }
                }

                //  val r = Integer.toHexString(repeatCount).toByte()
                editBean?.hour = (hour as String).toInt()
                editBean?.minute = (minute as String).toInt()
                editBean?.repeat = repeatCount.toByte()

                backAlarmListener?.backAlarmData(editBean)

                dismiss()
            }

        })

    }


    private fun initData(){
        editBean = AlarmBean()

        //当前的小时 分钟
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val houList = ArrayList<String>()
        for(i in 0 until 24){
            houList.add(String.format("%02d",i))
        }

        alarmHourScrollPicker?.data = houList as List<CharSequence>?
        alarmHourScrollPicker?.selectedPosition = currentHour

        val minuteList = ArrayList<String>()

        for(k in 0 until 60){
            minuteList.add(String.format("%02d",k))
        }
        alarmMinuteScrollPicker?.data = minuteList as List<CharSequence>?

        alarmMinuteScrollPicker?.selectedPosition = currentMinute

        weekList?.clear()
        weekList?.addAll(getWeekList())
        adapter?.notifyDataSetChanged()

        alarmHourScrollPicker?.setOnSelectedListener { scrollPickerView, position ->
            this.defaultHourIndex = position
        }

        alarmMinuteScrollPicker?.setOnSelectedListener { scrollPickerView, position ->
            this.defaultMinuteIndex = position
        }

    }

    //设置下标，修改闹钟时回填回去，新增时+1
    fun setAlarmIndex(index : Int){
        editBean?.alarmIndex = index
    }

    //回填数据
    fun setReBackData(bean : AlarmBean){
        editBean?.alarmIndex = bean.alarmIndex
        val hour = bean.hour
        val minute = bean.minute
        var hourIndex = 0
        var minuteIndex = 0
        for(i in 0..23){
            if(i == hour){
                defaultHourIndex  =i
                hourIndex = i
            }
        }

        for(i in 0..59){
            if(minute == i){
                defaultMinuteIndex = i
                minuteIndex  = i
            }
        }

        alarmHourScrollPicker?.selectedPosition = hourIndex
        alarmMinuteScrollPicker?.selectedPosition = minuteIndex

        //周
        val repeat = bean.repeat

        val str = Utils.byteToBit(repeat)
        val chartArray = str.toCharArray()


        //周日
        if(chartArray[0].toString() == "1"){
            weekList?.get(6)?.isChecked = true
        }
        //周一
        if(chartArray[1].toString() == "1"){
            weekList?.get(0)?.isChecked = true
        }
        //周二
        if(chartArray[2].toString() == "1"){
            weekList?.get(1)?.isChecked = true
        }
        //周三
        if(chartArray[3].toString() == "1"){
            weekList?.get(2)?.isChecked = true
        }
        //周四
        if(chartArray[4].toString() == "1"){
            weekList?.get(3)?.isChecked = true
        }
        //周五
        if(chartArray[5].toString() == "1"){
            weekList?.get(4)?.isChecked = true
        }
        //周六
        if(chartArray[6].toString() == "1"){
            weekList?.get(5)?.isChecked = true
        }
        adapter?.notifyDataSetChanged()
    }


    private fun getWeekList() : MutableList<AlarmWeekBean>{
        val list = ArrayList<AlarmWeekBean>()
        val w1 = AlarmWeekBean(context.resources.getString(R.string.string_monday),2)
        val w2 = AlarmWeekBean(context.resources.getString(R.string.string_tuesday),4)

        val w3 = AlarmWeekBean(context.resources.getString(R.string.string_wednesday),8)
        val w4 = AlarmWeekBean(context.resources.getString(R.string.string_thursday),16)
        val w5 = AlarmWeekBean(context.resources.getString(R.string.string_friday),32)
        val w6 = AlarmWeekBean(context.resources.getString(R.string.string_saturday),64)
        val w7 = AlarmWeekBean(context.resources.getString(R.string.string_sunday),1)



        list.add(w1)
        list.add(w2)
        list.add(w3)
        list.add(w4)
        list.add(w5)
        list.add(w6)
        list.add(w7)
        return list
    }
}