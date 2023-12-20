package com.app.smartkeyboard.second

import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.adapter.AlarmListAdapter
import com.app.smartkeyboard.dialog.AddAlarmDialog
import com.blala.blalable.blebean.AlarmBean
import com.blala.blalable.keyboard.OnAlarmListListener

/**
 * 闹钟页面
 */
class AlarmListActivity : AppActivity(){


    private var alarmListRy : RecyclerView ?= null
    private var alarmList = mutableListOf<AlarmBean>()
    private var adapter : AlarmListAdapter ?= null

    override fun getLayoutId(): Int {
       return R.layout.activity_alarm_list_layout
    }

    override fun initView() {

        alarmListRy = findViewById(R.id.alarmListRy)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        alarmListRy?.layoutManager = linearLayoutManager
        adapter = AlarmListAdapter(this,alarmList)
        alarmListRy?.adapter = adapter

        //添加闹钟
        findViewById<ImageView>(R.id.addAlarmImg).setOnClickListener {
            showEditDialog()
        }
    }

    override fun initData() {

    }


    override fun onResume() {
        super.onResume()

        readAlarm()
    }



    //读取闹钟
    private fun readAlarm(){
        BaseApplication.getBaseApplication().bleOperate.readAlarm(object : OnAlarmListListener{
            override fun backAlarmList(list: MutableList<AlarmBean>?) {
                if(list == null){
                    alarmList?.clear()
                    adapter?.notifyDataSetChanged()
                    return
                }

                alarmList?.clear()
                alarmList.addAll(list)
                adapter?.notifyDataSetChanged()

            }

        })
    }


  //显示弹窗
  private fun showEditDialog(){
      val dialog = AddAlarmDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
      dialog.show()

      val window = dialog.window
      val windowLayout = window?.attributes
      val metrics2: DisplayMetrics = resources.displayMetrics
      val widthW: Int = metrics2.widthPixels
      windowLayout?.height = (metrics2.heightPixels )
      windowLayout?.width = widthW
      windowLayout?.gravity = Gravity.BOTTOM


      window?.attributes = windowLayout
  }
}