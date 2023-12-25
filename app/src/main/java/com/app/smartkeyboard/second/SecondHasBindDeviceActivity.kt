package com.app.smartkeyboard.second

import android.graphics.Color
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.bean.DbManager
import com.app.smartkeyboard.ble.ConnStatus
import com.app.smartkeyboard.dialog.DeleteDeviceDialog
import com.app.smartkeyboard.utils.BikeUtils
import com.app.smartkeyboard.utils.MmkvUtils
import com.bonlala.widget.layout.SettingBar
import com.hjq.shape.layout.ShapeConstraintLayout
import com.hjq.shape.view.ShapeTextView

class SecondHasBindDeviceActivity :AppActivity() {

    private var hasBindDeviceNameBar : SettingBar ?= null

    private var mac : String ?= null

    private var bindDeviceUnBindTv : ShapeTextView ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_has_bind_device_layout
    }

    override fun initView() {
        bindDeviceUnBindTv = findViewById(R.id.bindDeviceUnBindTv)
        hasBindDeviceNameBar = findViewById(R.id.hasBindDeviceNameBar)

        //解除绑定
        findViewById<ShapeTextView>(R.id.bindDeviceUnBindTv)?.setOnClickListener {
            showUnBindDialog(true)
        }

        findViewById<ShapeConstraintLayout>(R.id.secondShowNameLayout)?.setOnClickListener {
            if(mac != null){
                startActivity(SecondEditBindNameActivity::class.java, arrayOf("bind_mac"),
                    arrayOf(mac)
                )
            }
        }

    }


    override fun onRightClick(view: View?) {
        super.onRightClick(view)


    }

    override fun initData() {
        //mac
        mac = intent.getStringExtra("bind_mac")
        //是否是移除设备
        val isRemove = intent.getStringExtra("remove_device")
        bindDeviceUnBindTv?.text = if(isRemove=="1") resources.getString(R.string.string_remove_device) else resources.getString(R.string.string_unbind)

    }

    override fun onResume() {
        super.onResume()

        //查询
        val bindBean = DbManager.getInstance().getBindDevice(mac)

        if(bindBean != null){
            hasBindDeviceNameBar?.rightText = bindBean.deviceName
        }
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
            if (position == 0x01) {   //解绑
                if(isUnBind){
                    DbManager.getInstance().deleteBindDevice(mac)
                    val conBleMac = MmkvUtils.getConnDeviceMac()
                    MmkvUtils.setDeviceType(1)
                    if(!BikeUtils.isEmpty(conBleMac) && mac == conBleMac){
                        BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
                        BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                        MmkvUtils.saveConnDeviceName("")
                        MmkvUtils.saveConnDeviceMac("")
                    }
                    finish()
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

}