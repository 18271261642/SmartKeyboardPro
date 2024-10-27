package com.app.smartkeyboard.second

import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.TitleBarFragment
import com.app.smartkeyboard.adapter.OnCommItemClickListener
import com.app.smartkeyboard.ble.ConnStatus
import com.app.smartkeyboard.ble.DeviceTypeConst
import com.app.smartkeyboard.dialog.DeleteDeviceDialog
import com.app.smartkeyboard.utils.BikeUtils
import com.app.smartkeyboard.utils.MmkvUtils
import com.app.smartkeyboard.widget.CheckButtonView
import com.fasterxml.jackson.databind.ser.Serializers.Base
import com.hjq.toast.ToastUtils
import timber.log.Timber


/**
 * 设备设置页面
 */
class MenuSettingFragment : TitleBarFragment<SecondHomeActivity>() {

    private var settingNoteLayout : CheckButtonView ?= null
    private var settingAlarmLayout : CheckButtonView ?= null
    //上传动画
    private var secondUploadGifView : CheckButtonView ?= null

    //屏保样式
    private var screenStyleButtonView : CheckButtonView ?= null
    //时钟样式
    private var clockStyleButtonView : CheckButtonView ?= null



    companion object{

        fun getInstance():MenuSettingFragment{
            return MenuSettingFragment()
        }
    }

    override fun getLayoutId(): Int {
       return R.layout.fragment_menu_setting_layout
    }

    override fun initView() {
        settingNoteLayout = findViewById(R.id.settingNoteLayout)
        settingAlarmLayout = findViewById(R.id.settingAlarmLayout)
        secondUploadGifView = findViewById(R.id.secondUploadGifView)
        screenStyleButtonView = findViewById(R.id.screenStyleButtonView)
        clockStyleButtonView = findViewById(R.id.clockStyleButtonView)


        settingNoteLayout?.setOnClickListener(this)
        settingAlarmLayout?.setOnClickListener(this)
        secondUploadGifView?.setOnClickListener(this)
        screenStyleButtonView?.setOnClickListener(this)
        clockStyleButtonView?.setOnClickListener(this)
    }

    override fun initData() {


    }


    override fun onActivityResume() {
        super.onActivityResume()
        showDeviceType()
    }

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)


    }

    override fun onResume() {
        super.onResume()
        showDeviceType()
    }


    private fun showDeviceType(){
        val isConn = BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED


        val mac = MmkvUtils.getConnDeviceMac()
       // screenStyleButtonView?.visibility = View.GONE
       // clockStyleButtonView?.visibility =  View.GONE
       // settingAlarmLayout?.visibility =  View.GONE
        secondUploadGifView?.visibility =  View.VISIBLE


        val isSecond = BaseApplication.getBaseApplication().deviceTypeConst == DeviceTypeConst.DEVICE_SECOND
        Timber.e("-------isSconde="+isSecond)

        if(isSecond){
            screenStyleButtonView?.setIsNormal(true)
            clockStyleButtonView?.setIsNormal(true)
            screenStyleButtonView?.visibility = View.VISIBLE
            clockStyleButtonView?.visibility = View.VISIBLE
            settingAlarmLayout?.visibility = View.VISIBLE

        }else{
            screenStyleButtonView?.visibility = View.GONE
            clockStyleButtonView?.visibility = View.GONE
            settingAlarmLayout?.visibility = View.GONE
        }

        if(BaseApplication.getBaseApplication().deviceTypeConst == DeviceTypeConst.DEVICE_THIRD){
            settingNoteLayout?.visibility = View.GONE
        }


    }

    override fun onClick(view: View?) {
        super.onClick(view)
        val id = view?.id

        val icConn = BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED

        when(id){
            R.id.settingNoteLayout->{  //备忘录

                startActivity(NotePadActivity::class.java)
            }
            R.id.settingAlarmLayout->{  //闹钟
                if(!icConn){
                    ToastUtils.show(resources.getString(R.string.string_not_conn))
                    return
                }
                startActivity(AlarmListActivity::class.java)
            }
            R.id.secondUploadGifView->{ //上传动画
                if(BikeUtils.isEmpty(getMac())){
                    showConnDialog()
                   return
                }
                if(!icConn){
                    ToastUtils.show(resources.getString(R.string.string_not_conn))
                    return
                }
                startActivity(SecondGifHomeActivity::class.java)
            }
            R.id.clockStyleButtonView->{    //时钟样式
                if(BikeUtils.isEmpty(getMac())){
                    showConnDialog()
                    return
                }
                if(!icConn){
                    ToastUtils.show(resources.getString(R.string.string_not_conn))
                    return
                }
                startActivity(ClockStyleActivity::class.java)
            }
            R.id.screenStyleButtonView->{   //屏保样式
                if(BikeUtils.isEmpty(getMac())){
                    showConnDialog()
                    return
                }
                if(!icConn){
                    ToastUtils.show(resources.getString(R.string.string_not_conn))
                    return
                }
                startActivity(SecondScreenStyleActivity::class.java)
            }
        }
    }
    private fun getMac() : String{
        return MmkvUtils.getConnDeviceMac()
    }


    //提示请连接的dialog
    private fun showConnDialog(){
        val dialog = DeleteDeviceDialog(attachActivity, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setTitleTxt(resources.getString(R.string.string_to_conn_prompt))
        dialog.setOnCommClickListener(object : OnCommItemClickListener{
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