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
import com.app.smartkeyboard.adapter.OnCommItemClickListener
import com.app.smartkeyboard.dialog.AddAlarmDialog
import com.app.smartkeyboard.dialog.DeleteDeviceDialog
import com.blala.blalable.Utils
import com.blala.blalable.blebean.AlarmBean
import com.blala.blalable.keyboard.OnAlarmListListener
import com.fasterxml.jackson.databind.ser.Serializers.Base
import okhttp3.internal.toHexString
import timber.log.Timber

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


        adapter?.setOnItemListener{
            val bean = alarmList?.get(it)
            showEditDialog(bean)
        }

        adapter?.setOnItemCheckListener(object : AlarmListAdapter.OnItemCheckListener{
            override fun onItemCheck(index: Int, isCheck: Boolean) {
                alarmList.get(index).isOpen = isCheck
                adapter?.notifyItemChanged(index)
                setAlarmToDevice()
            }

        })

        adapter?.onLongListener{
            deleteItem(it)
        }


        //添加闹钟
        findViewById<ImageView>(R.id.addAlarmImg).setOnClickListener {
            showEditDialog(null)
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
  private fun showEditDialog(bean : AlarmBean?){
      val dialog = AddAlarmDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
      dialog.show()
      if(bean != null){
          dialog.setReBackData(bean)
      }else{
          dialog.setAlarmIndex(alarmList.size)
      }


      dialog.setBackListener{
          Timber.e("-------设置="+it.toString())
          if(it.alarmIndex == alarmList.size){
              alarmList?.add(it)
          }else{
              alarmList?.set(it.alarmIndex,it)
          }

          adapter?.notifyItemChanged(it.alarmIndex)

          setAlarmToDevice()
      }

      val window = dialog.window
      val windowLayout = window?.attributes
      val metrics2: DisplayMetrics = resources.displayMetrics
      val widthW: Int = metrics2.widthPixels
      windowLayout?.height = (metrics2.heightPixels )
      windowLayout?.width = widthW
      windowLayout?.gravity = Gravity.BOTTOM


      window?.attributes = windowLayout
  }


    //设置闹钟
    var sb = StringBuffer()
    private fun setAlarmToDevice(){
        sb.delete(0,sb.length)
        sb.append("0505")
        alarmList?.forEachIndexed { index, alarmBean ->
            //下标
            val index = String.format("%02d",index)
            sb.append(index)
            //开关
            val switchC = if(alarmBean.isOpen) "02" else "01"
            sb.append(switchC)
            sb.append(String.format("%02x",alarmBean.repeat))
            sb.append(String.format("%02x",alarmBean.hour))
            sb.append(String.format("%02x",alarmBean.minute))
        }
        //长度
        val length = Utils.stringToByte(sb.toString()).size
        val lArray = Utils.intToSecondByteArray2(length)
        val resultStr = "040c01"+Utils.getHexString(lArray)+sb.toString()

        val result = Utils.stringToByte(resultStr)
        BaseApplication.getBaseApplication().bleOperate.setAlarmData(Utils.getFullPackage(result))

    }


    private fun deleteItem(index : Int){
        val dialog = DeleteDeviceDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setTitleTxt(resources.getString(R.string.string_delete_alarm_desc))
        dialog.setOnCommClickListener(object : OnCommItemClickListener{
            override fun onItemClick(position: Int) {
                dialog.dismiss()
                if(position == 0x01){
                    alarmList?.removeAt(index)
                    adapter?.notifyDataSetChanged()
                    setAlarmToDevice()
                }
            }

        })
        val window = dialog.window
        val windowLayout = window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = metrics2.widthPixels

        windowLayout?.width = widthW
        windowLayout?.gravity = Gravity.BOTTOM
        window?.attributes = windowLayout
    }
}