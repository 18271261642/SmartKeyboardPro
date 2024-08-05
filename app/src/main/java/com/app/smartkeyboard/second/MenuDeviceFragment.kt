package com.app.smartkeyboard.second

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.LogActivity
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.TitleBarFragment
import com.app.smartkeyboard.adapter.OnCommItemClickListener
import com.app.smartkeyboard.bean.DbManager
import com.app.smartkeyboard.ble.ConnStatus
import com.app.smartkeyboard.ble.OnConnStateListener

import com.app.smartkeyboard.dialog.DeleteDeviceDialog
import com.app.smartkeyboard.dialog.UpgradeDialogView
import com.app.smartkeyboard.utils.BikeUtils
import com.app.smartkeyboard.utils.MmkvUtils
import com.app.smartkeyboard.viewmodel.KeyBoardViewModel
import com.hjq.shape.layout.ShapeConstraintLayout
import com.hjq.shape.view.ShapeTextView
import com.hjq.toast.ToastUtils

/**
 * 设备页面
 */
class MenuDeviceFragment : TitleBarFragment<SecondHomeActivity>(){

    private val viewModel by viewModels<KeyBoardViewModel>()

    //设备名称
    private var deviceDeviceNameTv : TextView ?= null

    //连接状态
    private var secondMenuDeviceConnStateTv : TextView ?= null

    private var menuDeviceAboutAppLayout : ShapeConstraintLayout ?= null


    private var menuDeviceAboutAppVersionTv :TextView?=null

    //电量
    private var menuBatteryTv : TextView ?= null

    //手机日程同步
    private var menuScheduleTv : ShapeTextView ?= null
    //关于设备
    private var deviceAboutTv : ShapeTextView ?= null
    //恢复出厂设置
    private var menuDeviceRecyclerLayout : ShapeTextView ?= null


    private var deviceUnBindTv : ShapeTextView ?= null


    companion object{

        fun getInstance() : MenuDeviceFragment{
            return MenuDeviceFragment()
        }
    }

    override fun getLayoutId(): Int {
       return R.layout.fragment_menu_device_layout
    }

    override fun initView() {
        deviceUnBindTv = findViewById(R.id.deviceUnBindTv)
        menuDeviceRecyclerLayout = findViewById(R.id.menuDeviceRecyclerLayout)
        deviceAboutTv= findViewById(R.id.deviceAboutTv)
        menuScheduleTv= findViewById(R.id.menuScheduleTv)
        menuBatteryTv = findViewById(R.id.menuBatteryTv)
        menuDeviceAboutAppVersionTv = findViewById(R.id.menuDeviceAboutAppVersionTv)
        menuDeviceAboutAppLayout = findViewById(R.id.menuDeviceAboutAppLayout)
        secondMenuDeviceConnStateTv= findViewById(R.id.secondMenuDeviceConnStateTv)
        deviceDeviceNameTv = findViewById(R.id.deviceDeviceNameTv)
        findViewById<ShapeTextView>(R.id.deviceNotifyTv).setOnClickListener {
//            if(BikeUtils.isEmpty(getMac())){
//                return@setOnClickListener
//            }
            startActivity(NotifyOpenActivity::class.java)
        }

//
//        findViewById<LinearLayout>(R.id.menuConnDeviceLayout).setOnClickListener {
//            if(BikeUtils.isEmpty(getMac())){
//                showConnDialog()
//                return@setOnClickListener
//            }
//            startActivity(SecondScanActivity::class.java)
//        }

        findViewById<ShapeConstraintLayout>(R.id.menuDeviceAboutAppLayout).setOnLongClickListener(object : OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                startActivity(LogActivity::class.java)
                return true
            }

        })


        findViewById<ShapeTextView>(R.id.deviceUnBindTv).setOnClickListener {
//            if(BikeUtils.isEmpty(getMac())){
//                return@setOnClickListener
//            }
            showUnBindDialog(true)
        }
        //关于设备
        deviceAboutTv?.setOnClickListener {
            if(BikeUtils.isEmpty(getMac())){
                showConnDialog()
                return@setOnClickListener
            }
            startActivity(AboutDeviceActivity::class.java)
        }
        //恢复出厂设置
        findViewById<ShapeTextView>(R.id.menuDeviceRecyclerLayout).setOnClickListener {
            if(BikeUtils.isEmpty(getMac())){
                return@setOnClickListener
            }
            showUnBindDialog(false)
        }

        menuBatteryTv?.text = String.format(resources.getString(R.string.string_battery),"--")

        //手机日程同步
        menuScheduleTv?.setOnClickListener {
            if(BikeUtils.isEmpty(getMac()))
            {
                showConnDialog()
                return@setOnClickListener
            }
        }

        secondMenuDeviceConnStateTv?.setOnClickListener {
            val connState = BaseApplication.getBaseApplication().connStatus
            if(connState == ConnStatus.CONNECTED || connState == ConnStatus.CONNECTING){
                return@setOnClickListener
            }
            attachActivity.retryConn()
        }
    }



    private fun getMac() : String{
        return MmkvUtils.getConnDeviceMac()
    }


    override fun initData() {


        viewModel.appVersionData.observe(this){
            if(it?.isError == false){
                showAppUpgradeDialog(it.ota)
            }else{
                ToastUtils.show(resources.getString(R.string.string_has_last_version))
            }
        }

        attachActivity.setOnStateListener{
            showConnState()
        }

        attachActivity?.setOnConnStateListener(object : OnConnStateListener{
            override fun onConnState(connStatus: ConnStatus?) {
                secondMenuDeviceConnStateTv?.text =  if(connStatus == ConnStatus.CONNECTED) resources.getString(R.string.string_connected) else (if( connStatus == ConnStatus.CONNECTING) resources.getString(R.string.string_connecting) else resources.getString(R.string.string_retry_conn))
                if(connStatus == ConnStatus.CONNECTED){
                    getBattery()

                }else{
                    menuBatteryTv?.text = String.format(resources.getString(R.string.string_battery),"--")
                }
                showConnState()
            }
        })

        try {
            val packManager = attachActivity.packageManager
            val packInfo = packManager.getPackageInfo(attachActivity.packageName,0)
            val versioiName = packInfo.versionName
            menuDeviceAboutAppVersionTv?.text = versioiName
        }catch (e : Exception){
            e.printStackTrace()
        }


        //app版本更新
        menuDeviceAboutAppLayout?.setOnClickListener {
//            val packManager = attachActivity.packageManager
//            val packInfo = packManager.getPackageInfo(attachActivity.packageName,0)
//            viewModel.checkAppVersion(packInfo.versionCode)

            startActivity(AppVersionActivity::class.java)
        }
    }


    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        showConnState()
    }


    override fun onActivityResume() {
        super.onActivityResume()
        showConnState()
    }

    private fun showConnState(){
        val bleName =  MmkvUtils.getConnDeviceName()
        deviceDeviceNameTv?.text = if(BikeUtils.isEmpty(bleName)) resources.getString(R.string.string_no_device) else bleName


        if(BikeUtils.isEmpty(bleName)){
            deviceUnBindTv?.visibility = View.INVISIBLE
            secondMenuDeviceConnStateTv?.text = resources.getString(R.string.string_not_conn)
          //  deviceAboutTv!!.setBackgroundResource(R.drawable.no_conn_shape)
            menuScheduleTv?.setBackgroundResource(R.drawable.no_conn_shape)
            menuDeviceRecyclerLayout?.visibility = View.GONE
            menuBatteryTv?.text =  String.format(resources.getString(R.string.string_battery),"--")
        }else{
            deviceUnBindTv?.visibility = View.VISIBLE
            val isConnStatus = BaseApplication.getBaseApplication().connStatus
            secondMenuDeviceConnStateTv?.text =  if(isConnStatus == ConnStatus.CONNECTED) resources.getString(R.string.string_connected) else (if( isConnStatus == ConnStatus.CONNECTING) resources.getString(R.string.string_connecting) else resources.getString(R.string.string_retry_conn))
            if(isConnStatus == ConnStatus.CONNECTED){
                deviceAboutTv!!.shapeDrawableBuilder.setSolidGradientColors(intArrayOf(Color.parseColor("#343348"),Color.parseColor("#262D38"))).intoBackground()
                menuScheduleTv!!.shapeDrawableBuilder.setSolidGradientColors(intArrayOf(Color.parseColor("#343348"),Color.parseColor("#262D38"))).intoBackground()
                menuDeviceRecyclerLayout?.visibility = View.GONE
                //menuScheduleTv?.visibility = View.GONE
                getBattery()
            }else{
                menuDeviceRecyclerLayout?.visibility = View.GONE
                menuBatteryTv?.text =  String.format(resources.getString(R.string.string_battery),"--")
            }

        }

        menuScheduleTv?.visibility = View.GONE
      //  deviceAboutTv!!.shapeDrawableBuilder.setSolidGradientColors(intArrayOf(Color.parseColor("#343348"),Color.parseColor("#262D38"))).intoBackground()
    }


    //获取电量
    private fun getBattery(){
        BaseApplication.getBaseApplication().bleOperate.getDeviceSystemData { circleSpeed, batteryValue, cpuTemperatureC, cpuTemperatureF, gpuTemC, gpuTemF, hardTemC, hardTemF ->
            menuBatteryTv?.text = String.format(resources.getString(R.string.string_battery),batteryValue.toString()+"%")

        }
    }

    private fun showUnBindDialog(isUnBind : Boolean){
        val dialog = DeleteDeviceDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        if(!isUnBind){
            dialog.setTitleTxt(resources.getString(R.string.string_recycler_yes_or_not))
            dialog.setConfirmBgColor(Color.parseColor("#16AEA0"))
        }else{
            dialog.setTitleTxt(resources.getString(R.string.string_unbind_alert))
        }
        dialog.setOnCommClickListener { position ->
            dialog.dismiss()
            if (position == 0x01) {   //解绑
                if(isUnBind){
                    DbManager.getInstance().deleteBindDevice(MmkvUtils.getConnDeviceMac())
                    BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
                    BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                    MmkvUtils.saveConnDeviceName("")
                    MmkvUtils.saveConnDeviceMac("")
                    MmkvUtils.setDeviceType(1)
                    attachActivity.showIsAddDevice()

                    showConnState()
                }else{
                    BaseApplication.getBaseApplication().bleOperate.setRecyclerDevice()

                }

            }
        }

        val window = dialog.window
        val windowLayout = window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = metrics2.widthPixels

        windowLayout?.width = widthW
        windowLayout?.gravity = Gravity.BOTTOM
        window?.attributes = windowLayout
    }


    private fun showAppUpgradeDialog(url : String){
        val upgradeDialogView = UpgradeDialogView(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        upgradeDialogView.show()
        upgradeDialogView.setContentTxt(resources.getString(R.string.string_app_new_version))
        upgradeDialogView.setOnDialogClickListener{
            upgradeDialogView.dismiss()
            if(it == 0x01){
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW,uri)
                attachActivity.startActivity(intent)
            }
        }

    }


    //提示请连接的dialog
    private fun showConnDialog(){
        val dialog = DeleteDeviceDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setTitleTxt(resources.getString(R.string.string_to_conn_prompt))
        dialog.setOnCommClickListener(object : OnCommItemClickListener {
            override fun onItemClick(position: Int) {
                dialog.dismiss()
                if(position == 0x01){   //确定
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