package com.app.smartkeyboard.second

import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.bean.DbManager
import com.app.smartkeyboard.ble.ConnStatus
import com.app.smartkeyboard.ble.ota.OtaDialogView
import com.app.smartkeyboard.dialog.DeleteDeviceDialog
import com.app.smartkeyboard.dialog.UpgradeDialogView
import com.app.smartkeyboard.utils.MmkvUtils
import com.app.smartkeyboard.viewmodel.KeyBoardViewModel
import com.blala.blalable.BleConstant
import com.blala.blalable.listener.OnCommBackDataListener
import com.google.gson.Gson
import com.hjq.shape.layout.ShapeConstraintLayout
import com.hjq.toast.ToastUtils
import timber.log.Timber

class AboutDeviceActivity : AppActivity() {

    private val viewModel by viewModels<KeyBoardViewModel>()


    //名称
    private var aboutDeviceNameTv : TextView ?= null
    //型号
    private var aboutDeviceModelTv : TextView ?= null
    //版本
    private var aboutDeviceVersionTv : TextView ?= null

    private var aboutEnjoyTv : TextView ?= null
    private var aboutDeviceMacTv : TextView ?= null


    //恢复出厂设置
    private var aboutRecyclerLayout : ConstraintLayout ?= null



    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x00){
                val bundle = msg.obj as Bundle
                val url = bundle.getString("url")
                val name = bundle.getString("name")
                val mac = bundle.getString("mac")

                if (url != null) {
                    showOtaDialog(url,name!!,mac!!)
                }
            }

            if(msg.what == 0x02){
                dialog?.dismiss()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_about_device_layout
    }




    override fun initView() {
        aboutRecyclerLayout = findViewById(R.id.aboutRecyclerLayout)
        aboutDeviceMacTv = findViewById(R.id.aboutDeviceMacTv)
        aboutEnjoyTv = findViewById(R.id.aboutEnjoyTv)
        aboutDeviceNameTv = findViewById(R.id.aboutDeviceNameTv)
        aboutDeviceModelTv = findViewById(R.id.aboutDeviceModelTv)
        aboutDeviceVersionTv = findViewById(R.id.aboutDeviceVersionTv)

        findViewById<ShapeConstraintLayout>(R.id.aboutUpdateLayout).setOnLongClickListener(object : OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                showVersion(true)
                return true
            }

        })

        findViewById<ShapeConstraintLayout>(R.id.aboutUpdateLayout).setOnClickListener {
            showVersion(false)
        }

        findViewById<ShapeConstraintLayout>(R.id.aboutDeviceNameLayout).setOnClickListener {
            startActivity(SecondHasBindDeviceActivity::class.java, arrayOf("bind_mac"),
                arrayOf(mac)
            )
        }

        aboutEnjoyTv?.setText(String.format(resources.getString(R.string.string_enter_website),"https://wuquestudio.cn"))

        aboutDeviceMacTv?.text = MmkvUtils.getConnDeviceMac()

        aboutRecyclerLayout?.setOnClickListener {
            showUnBindDialog(false)
        }
    }


    private fun register(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(BleConstant.BLE_CONNECTED_ACTION)
        intentFilter.addAction(BleConstant.BLE_DIS_CONNECT_ACTION)
        intentFilter.addAction(BleConstant.BLE_SCAN_COMPLETE_ACTION)
        intentFilter.addAction(BleConstant.BLE_START_SCAN_ACTION)
        registerReceiver(broadcastReceiver,intentFilter)
    }


    override fun initData() {
        register()

        //查询
        val bindBean = DbManager.getInstance().getBindDevice(mac)
        if(bindBean != null){
            aboutDeviceNameTv?.text = bindBean.deviceName
        }

        viewModel.firmwareData.observe(this){
            Timber.e("------code="+it)
            if(it != null){
                if(it.isError){
                    ToastUtils.show(resources.getString(R.string.string_has_last_version))
                    BaseApplication.getBaseApplication().logStr = it.errorMsg
                }else{
                    BaseApplication.getBaseApplication().logStr = Gson().toJson(it)
                    showUpgradeDialog(it.ota,it.fileName)
                }
            }else{
                ToastUtils.show(resources.getString(R.string.string_has_last_version))
            }
        }

//        val name = MmkvUtils.getConnDeviceName()
//        val productName = MmkvUtils.getSaveProductNumber()
//        aboutDeviceNameTv?.text = name
//        aboutDeviceModelTv?.text = name+" "+productName

        showVersion(false)
    }


    val stringBuffer = StringBuilder()
    private fun showVersion(showLog : Boolean){
        stringBuffer.delete(0,stringBuffer.length)
        BaseApplication.getBaseApplication().bleOperate.getDeviceVersionData(object :
            OnCommBackDataListener {
            override fun onIntDataBack(value: IntArray?) {
                Timber.e("------版本好="+ (value?.get(0) ?: 0) )
                val code = value?.get(0)
              //  stringBuffer.append(" "+code.toString()+" ")
                BaseApplication.getBaseApplication().setLogStr("VersionCode="+code.toString())
                if(code != null){
                  //  viewModel.checkVersion(this@AboutDeviceActivity,code)
                }
               // aboutDeviceVersionTv?.text =  stringBuffer.toString()
            }

            override fun onStrDataBack(vararg value: String?) {
                stringBuffer.append(" "+value[0]+" ")
                aboutDeviceVersionTv?.text =  stringBuffer.toString()
                val name = MmkvUtils.getConnDeviceName()
                aboutDeviceModelTv?.text = value[1].toString()
                if(value[1] != null && value[2] != null){
                    viewModel.checkVersion(this@AboutDeviceActivity,value[2]!!.toInt(),value[1].toString())
                }

                if(showLog){
                    value[3]?.let { showLogDialog(it) }
                }

            }

        })
    }


    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            Timber.e("---------acdtion="+action)
            if(action == BleConstant.BLE_CONNECTED_ACTION){
                ToastUtils.show(resources.getString(R.string.string_conn_success))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                if(!BaseApplication.getBaseApplication().isActivityScan){
                    BaseApplication.getBaseApplication().bleOperate.stopScanDevice()
                }
                showVersion(false)

                setDialogTxtShow(resources.getString(R.string.string_upgrade_success))
            }
            if(action == BleConstant.BLE_DIS_CONNECT_ACTION){
                ToastUtils.show(resources.getString(R.string.string_conn_disconn))
                showVersion(false)
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(broadcastReceiver)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }



    private var upgradeDialogView : UpgradeDialogView?= null


    private fun showUpgradeDialog(url : String ,name : String){
        if(upgradeDialogView == null){
            upgradeDialogView = UpgradeDialogView(this, com.bonlala.base.R.style.BaseDialogTheme)
        }
        if(!upgradeDialogView!!.isShowing){
            upgradeDialogView?.show()
        }
        upgradeDialogView?.setContentTxt(resources.getString(R.string.string_has_new_ota))
        upgradeDialogView?.setOnDialogClickListener { position ->
            upgradeDialogView?.dismiss()
            if (position == 0x01) {
                val mac = MmkvUtils.getConnDeviceMac()
                MmkvUtils.saveConnDeviceMac(null)
                BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()

                val msg = handlers.obtainMessage()
                val bundle = Bundle()
                bundle.putString("url", url)
                bundle.putString("name", name)
                bundle.putString("mac", mac)
                msg.what = 0x00
                msg.obj = bundle
                handlers.sendMessageDelayed(msg, 1000)
            }
        }


    }


    //设置弹窗显示的文字
    private fun setDialogTxtShow(txt : String){
        if(dialog != null && dialog!!.isShowing){
            dialog?.setStateShow(txt)
            dialog?.visibilityOrGone(false)
            handlers.sendEmptyMessageDelayed(0x02,3000)
        }
    }


    private var dialog : OtaDialogView?= null

    //显示升级的弹窗
    private fun showOtaDialog(url : String ,fileName :String,mac : String){
        if(dialog == null){
            dialog = OtaDialogView(this, com.bonlala.base.R.style.BaseDialogTheme)
        }

        dialog?.show()
        dialog?.downloadFile(url,fileName,mac)
        //  dialog?.startScanDevice(mac)

        val window = dialog?.window
        val windowLayout = window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = (metrics2.widthPixels * 0.9f).toInt()
        val height : Int = (metrics2.heightPixels * 0.6f).toInt()
        windowLayout?.width = widthW
        windowLayout?.height = height
        window?.attributes = windowLayout
    }



    private fun showUnBindDialog(isUnBind : Boolean){
        val dialog = DeleteDeviceDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        if(!isUnBind){
            dialog.setTitleTxt(resources.getString(R.string.string_recycler_yes_or_not))
            dialog.setConfirmBgColor(Color.parseColor("#16AEA0"))
        }else{
            dialog.setTitleTxt(resources.getString(R.string.string_unbind_alert))
        }
        dialog.setOnCommClickListener { position ->
            dialog.dismiss()
            if (position == 0x01) {
                BaseApplication.getBaseApplication().bleOperate.setRecyclerDevice()

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


    private fun showLogDialog(log : String){
        val dialog = AlertDialog.Builder(this)
            .setMessage(log)
            .setPositiveButton(resources.getString(R.string.string_cancel),object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }

            })
            .setNegativeButton(resources.getString(R.string.string_confirm),object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }

            })
        dialog.create().show()
    }

}