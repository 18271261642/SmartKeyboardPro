package com.app.smartkeyboard.second

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.LinearLayout
import android.widget.TextView
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.TitleBarFragment
import com.app.smartkeyboard.adapter.OnCommItemClickListener
import com.app.smartkeyboard.bean.UseTimeBean
import com.app.smartkeyboard.ble.ConnStatus
import com.app.smartkeyboard.ble.OnConnStateListener
import com.app.smartkeyboard.dialog.DeleteDeviceDialog
import com.app.smartkeyboard.utils.BikeUtils
import com.app.smartkeyboard.utils.MmkvUtils
import com.app.smartkeyboard.utils.TimeUtils
import com.app.smartkeyboard.widget.CusHistogramChartView
import com.app.smartkeyboard.widget.SecondHomeTemperatureView
import com.blala.blalable.listener.OnSystemDataListener

/**
 * 数据页面
 */
class MenuDataFragment : TitleBarFragment<SecondHomeActivity>() {


    private var homeTempView: SecondHomeTemperatureView? = null
    private var homeTimeStateTv: TextView? = null
    private var homeDataChartView: CusHistogramChartView? = null

    companion object {

        fun getInstance(): MenuDataFragment {
            return MenuDataFragment()
        }
    }


    private val handlers: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0x00) {
                getDeviceData()
            }

        }
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_menu_data_layout
    }

    override fun initView() {
        homeDataChartView = findViewById(R.id.homeDataChartView)
        homeTempView = findViewById(R.id.homeTempView)
        homeTimeStateTv = findViewById(R.id.homeTimeStateTv)

        findViewById<LinearLayout>(R.id.changeDeviceLayout).setOnLongClickListener(object :
            OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {

//                val h = attachActivity as SecondHomeActivity
//                h?.getWeather()

                startActivity(WeatherLogActivity::class.java)
                return true
            }
        })

        //切换设备
        findViewById<LinearLayout>(R.id.changeDeviceLayout).setOnClickListener {
            if (BikeUtils.isEmpty(MmkvUtils.getConnDeviceMac())) {
                startActivity(SecondScanActivity::class.java)
                return@setOnClickListener
            }
            showConnDialog()
        }

        homeTempView?.setOnClickListener {
            if (BikeUtils.isEmpty(MmkvUtils.getConnDeviceMac())) {
                attachActivity.showConnDialog()
                return@setOnClickListener
            }

        }

        homeDataChartView?.setOnClickListener {
            if (BikeUtils.isEmpty(MmkvUtils.getConnDeviceMac())) {
                attachActivity.showConnDialog()
                return@setOnClickListener
            }

        }


        attachActivity?.setOnConnStateListener(object : OnConnStateListener {
            override fun onConnState(connStatus: ConnStatus?) {
                handlers.sendEmptyMessageDelayed(0x00, 5000)
            }
        })

    }

    override fun initData() {
        //homeTempView?.setTemperatures("--","--","--")

        //homeTempView?.setDefaultValue()

        val list = mutableListOf<UseTimeBean>()
        list.add(UseTimeBean(12, 10))
        list.add(UseTimeBean(13, 10))
        list.add(UseTimeBean(14, 10))
        list.add(UseTimeBean(15, 10))
        list.add(UseTimeBean(16, 10))
        list.add(UseTimeBean(17, 10))
        list.add(UseTimeBean(18, 10))
        homeDataChartView?.setDataSource(list)
    }


    override fun onActivityResume() {
        super.onActivityResume()
        homeTimeStateTv?.text =
            TimeUtils.getTimeByNow(attachActivity) + " " + MmkvUtils.getConnDeviceName()
        getDeviceData()
    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        homeTimeStateTv?.text =
            TimeUtils.getTimeByNow(attachActivity) + " " + MmkvUtils.getConnDeviceName()
        //homeTempView?.setBatteryValue(88)
        getDeviceData()
    }


    private fun getDeviceData() {
        val isConnStatus = BaseApplication.getBaseApplication().connStatus
        if (isConnStatus != ConnStatus.CONNECTED) {
            homeTempView?.setDefaultValue()
            return
        }


        BaseApplication.getBaseApplication().bleOperate.getDeviceSystemData(object :
            OnSystemDataListener {
            override fun onSysData(
                circleSpeed: Int,
                batteryValue: Int,
                cpuTemperatureC: Int,
                cpuTemperatureF: Int,
                gpuTemC: Int,
                gpuTemF: Int,
                hardTemC: Int,
                hardTemF: Int
            ) {
                homeTempView?.setBatteryValue(batteryValue)
                homeTempView?.setTemperatures(
                    (cpuTemperatureC/10).toString(),
                    (gpuTemC/10).toString(),
                    (hardTemC/10).toString()
                )
                homeTempView?.setFanSpeed(circleSpeed)
            }

        })
    }

    //提示请连接的dialog
    private fun showConnDialog() {
        val dialog = DeleteDeviceDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setTitleTxt("是否切换设备?")
        dialog.setOnCommClickListener(object : OnCommItemClickListener {
            override fun onItemClick(position: Int) {
                dialog.dismiss()
                if (position == 0x01) {   //确定
                    startActivity(SecondScanActivity::class.java)
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