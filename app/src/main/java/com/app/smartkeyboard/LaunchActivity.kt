package com.app.smartkeyboard

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.app.smartkeyboard.action.ActivityManager
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.ble.ConnStatus
import com.app.smartkeyboard.dialog.ShowPrivacyDialogView
import com.app.smartkeyboard.second.SecondHomeActivity
import com.app.smartkeyboard.utils.MmkvUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LaunchActivity : AppActivity() {



    private val handlers : Handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            startActivity(SecondHomeActivity::class.java)
            finish()
        }
    }



    override fun getLayoutId(): Int {
        return R.layout.activity_launch_layout
    }

    override fun initView() {

    }

    override fun initData() {

        val isFirstOpen = MmkvUtils.getPrivacy()
        if (!isFirstOpen) {
            showPrivacyDialog()
        }else{
            val type = MmkvUtils.getDeviceType()
            BaseApplication.getBaseApplication().setDeviceTypeConst(type)
         handlers.sendEmptyMessageDelayed(0x00,3000)
        }
    }



    //显示隐私弹窗
    private fun showPrivacyDialog() {
        val dialog =
            ShowPrivacyDialogView(this, com.bonlala.base.R.style.BaseDialogTheme, this@LaunchActivity)
        dialog.show()
        dialog.setCancelable(false)
        dialog.setOnPrivacyClickListener(object : ShowPrivacyDialogView.OnPrivacyClickListener {
            override fun onCancelClick() {
                dialog.dismiss()
                MmkvUtils.setIsAgreePrivacy(false)
                BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
                BaseApplication.getBaseApplication().connStatus = ConnStatus.NOT_CONNECTED
                ActivityManager.getInstance().finishAllActivities()
                finish()
            }

            override fun onConfirmClick() {
                dialog.dismiss()
                MmkvUtils.setIsAgreePrivacy(true)
                handlers.sendEmptyMessageDelayed(0x00,2000)
            }

        })
    }

}