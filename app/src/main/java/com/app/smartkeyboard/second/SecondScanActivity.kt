package com.app.smartkeyboard.second

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.adapter.OnCommItemClickListener
import com.app.smartkeyboard.adapter.OnCommMenuClickListener
import com.app.smartkeyboard.adapter.SecondScanAdapter
import com.app.smartkeyboard.bean.BleBean
import com.app.smartkeyboard.bean.DbManager
import com.app.smartkeyboard.ble.ConnStatus
import com.app.smartkeyboard.ble.ConnStatusService
import com.app.smartkeyboard.dialog.DeleteDeviceDialog
import com.app.smartkeyboard.utils.BikeUtils
import com.app.smartkeyboard.utils.BonlalaUtils
import com.app.smartkeyboard.utils.MmkvUtils
import com.app.smartkeyboard.viewmodel.SecondHomeViewModel
import com.blala.blalable.BleConstant
import com.blala.blalable.Utils
import com.blala.blalable.listener.OnCommBackDataListener
import com.google.gson.Gson
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import timber.log.Timber
import java.util.Locale
import kotlin.math.abs


/**
 * Created by Admin
 *Date 2023/7/12
 */
class SecondScanActivity : AppActivity() {


    private var viewModel : SecondHomeViewModel ?= null

    private var secondScanRy: RecyclerView? = null

    private var adapter: SecondScanAdapter? = null
    private var list: MutableList<BleBean>? = null


    private var bindList : MutableList<BleBean>?=null
    private var bindAdapter : SecondScanAdapter ?= null
    //已连接的设备
    private var secondConnRecyclerView : RecyclerView ?= null

    private var scanBleTv : TextView ?= null

    private var scanLeftImageView : ImageView ?= null
    private var scanProgressBar : ProgressBar ?= null


    private val connStatusService = BaseApplication.getBaseApplication().connStatusService

    //用于去重的list
    private var repeatList: MutableList<String>? = null

    private var bluetoothAdapter : BluetoothAdapter ?= null


    private val handlers: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0x00) {
                BaseApplication.getBaseApplication().bleOperate.stopScanDevice()

            }

            if(msg.what == 0x01){
                stopScan()
                BaseApplication.getBaseApplication().bleOperate.disConnYakDevice()
            }

            if(msg.what == 0x08){
                getBindDeviceList()

                repeatList?.clear()
                list?.clear()
             //   adapter?.notifyDataSetChanged()

                circleClear()
            }

            if(msg.what == 0x09){
                startActivity(SecondScanActivity::class.java)
                finish()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_second_scan_layout
    }

    override fun initView() {
        scanProgressBar = findViewById(R.id.scanProgressBar)
        scanLeftImageView = findViewById(R.id.scanLeftImageView)
        scanBleTv = findViewById(R.id.scanBleTv)
        secondConnRecyclerView = findViewById(R.id.secondConnRecyclerView)
        secondScanRy = findViewById(R.id.secondScanRy)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        secondScanRy?.layoutManager = linearLayoutManager
        ((secondScanRy?.itemAnimator) as SimpleItemAnimator).supportsChangeAnimations = false
        (secondScanRy?.itemAnimator as SimpleItemAnimator).changeDuration = 0

        val lm = LinearLayoutManager(this)
        lm.orientation = LinearLayoutManager.VERTICAL
        secondConnRecyclerView?.layoutManager = lm
        bindList = mutableListOf()
        bindAdapter = SecondScanAdapter(this, bindList!!)
        secondConnRecyclerView?.adapter = bindAdapter

        bindAdapter?.setOnCommMenuClick(onBIndMenuClick)


        list = mutableListOf()
        adapter = SecondScanAdapter(context, list!!)
        secondScanRy?.adapter = adapter
        repeatList = mutableListOf()
//        adapter!!.setOnItemClick(onItemClick)

        adapter?.setOnCommMenuClick(onMenuClick)

        scanLeftImageView?.setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.scanTitleTv).setOnLongClickListener {
            startActivity(ScanLogActivity::class.java)

            true
        }
    }


    override fun initData() {
        viewModel = ViewModelProvider(this)[SecondHomeViewModel::class.java]
        val intentFilter = IntentFilter()
        intentFilter.addAction(BleConstant.BLE_CONNECTED_ACTION)
        intentFilter.addAction(BleConstant.BLE_DIS_CONNECT_ACTION)
        intentFilter.addAction(BleConstant.BLE_SCAN_COMPLETE_ACTION)
        intentFilter.addAction(BleConstant.BLE_START_SCAN_ACTION)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(broadcastReceiver,intentFilter,Context.RECEIVER_EXPORTED)
        }else{
            registerReceiver(broadcastReceiver,intentFilter)
        }

        val bleManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bleManager.adapter


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onResume() {
        super.onResume()
        Timber.e("----------onResume")
        dealScanDevice()
    }


    private fun dealScanDevice(){
        BaseApplication.getBaseApplication().isActivityScan = true
        repeatList?.clear()
        list?.clear()

        //  backScanDevice()

        getBindDeviceList()
        verifyScanFun(false)
    }


    //获取绑定的设备
    private fun getBindDeviceList(){
        bindList?.clear()

        val isCOnn = BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED
        val bindUserList = DbManager.getInstance().allBindDevice
        val mac = MmkvUtils.getConnDeviceMac()
        if(bindUserList != null){
            bindUserList.sortByDescending { it ->it.bindTime }
            bindUserList.forEach {
                val bean = BleBean()
                bean.isBind = true
                bean.rssi = 0
                bean.productNumber = ""
                bean.bleMac = it.deviceMac
                bean.bleName = it.deviceName
                if(!BikeUtils.isEmpty(mac) && mac == it.deviceMac){
                    bean.connStatus =  BaseApplication.getBaseApplication().connStatus
                }
                bindList?.add(bean)
            }
            bindAdapter?.notifyDataSetChanged()
        }else{
            bindList?.clear()
            bindAdapter?.notifyDataSetChanged()
        }
    }


    //判断是否有位置权限了，没有请求权限
    private fun verifyScanFun(isReconn: Boolean) {
        Timber.e("------搜索了="+isReconn+" "+BikeUtils.isBleEnable(this))
        //判断蓝牙是否开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            XXPermissions.with(this).permission(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            ).request(object : OnPermissionCallback{
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    Timber.e("--------1111onGranted")
                }
            })
        }

        //判断权限
        val isPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!isPermission) {
            scanProgressBar?.visibility = View.GONE
            XXPermissions.with(this).permission(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ).request(object : OnPermissionCallback{
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    Timber.e("--------2222onGranted")
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    super.onDenied(permissions, doNotAskAgain)
                    Timber.e("----222-------onDenied="+doNotAskAgain)
//                    if(doNotAskAgain){
//                        showPermissionAlert()
//                        return
//                    }
                }

            })

            // ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),0x00)
            return
        }

        //判断蓝牙是否打开
        val isOpenBle = BonlalaUtils.isOpenBlue(this@SecondScanActivity)
        if (!isOpenBle) {
            scanProgressBar?.visibility = View.GONE
            BonlalaUtils.openBluetooth(this)
            return
        }

        showBleState(false)
        startScan()
    }


    private val onBIndMenuClick : OnCommMenuClickListener = object : OnCommMenuClickListener{
        override fun onItemClick(position: Int) {
            val bean = bindList?.get(position)
//            if(BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTING){
//                ToastUtils.show("正在连接中,请稍后!")
//                return
//            }
            if(bean == null)
                return
            if(BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED){
                //判断是否是连接当前的设备
                if(bean?.bleMac == MmkvUtils.getConnDeviceMac()){
                    ToastUtils.show(resources.getString(R.string.string_device_has_conn))
                    return
                }
                val service = BaseApplication.getBaseApplication().connStatusService
                if (bean != null) {
                    showConnDialogView(bean,service,true,position)
                }

                return
            }
            if(Math.abs(bean.rssi)>85){
                return
            }


            bindList?.get(position)?.connStatus = ConnStatus.CONNECTING
            bindAdapter?.notifyItemChanged(position)
            connStatusService.connDeviceBack(
                bean.bleName, bean.bleMac
            ) { mac, status ->
                hideDialog()
                ToastUtils.show(resources.getString(R.string.string_conn_success))
                DbManager.getInstance().saveUserBindDevice(bean.bleName,bean.bleMac,BikeUtils.getFormatDate(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                bindList?.get(position)?.connStatus = ConnStatus.CONNECTED
                bindList?.get(position)?.rssi = 0
                bindAdapter?.notifyItemChanged(position)
                MmkvUtils.saveProductNumberCode(bean.productNumber)
                MmkvUtils.saveConnDeviceMac(mac)
                MmkvUtils.saveConnDeviceName(bean.bleName)
                BaseApplication.getBaseApplication().setOpenAppTime()
                getDeviceType()
                dealScanDevice()
                //initData()
            }

        }

        override fun onChildItemClick(position: Int) {
            val mac = bindList!!.get(position).bleMac
            startActivity(SecondHasBindDeviceActivity::class.java, arrayOf("bind_mac","remove_device"),
                arrayOf(mac,"1")
            )
        }

    }


    private val onMenuClick : OnCommMenuClickListener = object : OnCommMenuClickListener{
        override fun onItemClick(position: Int) {
            val service = BaseApplication.getBaseApplication().connStatusService
            val bean = list?.get(position)
            if (bean != null) {
//                if(BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTING){
//                    ToastUtils.show("正在连接中,请稍后!")
//                    return
//                }
                if(BaseApplication.getBaseApplication().connStatus == ConnStatus.CONNECTED){
                    //判断是否是连接当前的设备
                    if(bean.bleMac == MmkvUtils.getConnDeviceMac()){
                        ToastUtils.show(resources.getString(R.string.string_device_has_conn))
                        return
                    }else{
                        showConnDialogView(bean,service,false,position)
                    }

                    return
                }

                if(Math.abs(bean.rssi)>85){
                    return
                }

               // handlers.sendEmptyMessageDelayed(0x00, 500)
                showDialog(resources.getString(R.string.string_upgrade_conning))
                bean.connStatus = ConnStatus.CONNECTING
                adapter?.notifyItemChanged(position)

                service.connDeviceBack(
                    bean.bleName, bean.bluetoothDevice.address
                ) { mac, status ->
                    hideDialog()
                    DbManager.getInstance().saveUserBindDevice(bean.bleName, bean.bleMac,BikeUtils.getFormatDate(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"))
                    MmkvUtils.saveProductNumberCode(bean.productNumber)
                    MmkvUtils.saveConnDeviceMac(mac)
                    MmkvUtils.saveConnDeviceName(bean.bleName)
                    BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                    getDeviceType()
                    dealScanDevice()
                    //  finish()
                }
            }
        }

        override fun onChildItemClick(position: Int) {


        }

    }

    private fun circleClear(){
        handlers.sendEmptyMessageDelayed(0x08,10 * 1000)
    }

    val stringBuilder = StringBuilder()
    //开始扫描
    private fun startScan() {
        circleClear()
        stringBuilder.delete(0,stringBuilder.length)
        //已经绑定的设备
        val bindUserList = DbManager.getInstance().allBindDevice
        bindUserList?.sortByDescending { it ->it.bindTime }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                scanProgressBar?.visibility = View.GONE
                //showPermissionAlert()
                return
            }
        }



        val bleManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bleManager.adapter

        val settings =
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        val filters: List<ScanFilter> = ArrayList()
        scanProgressBar?.visibility = View.VISIBLE
        bluetoothAdapter?.bluetoothLeScanner?.startScan(filters,settings,scanCallBack)
    }

    override fun onDestroy() {
        super.onDestroy()
        handlers.removeMessages(0x08)
        stopScan()
        BaseApplication.getBaseApplication().isActivityScan = false
        unregisterReceiver(broadcastReceiver)
    }



    //提示请连接的dialog
    private fun showConnDialogView(bean: BleBean,service : ConnStatusService,isBind : Boolean,index : Int){
        val dialog = DeleteDeviceDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setTitleTxt(resources.getString(R.string.string_is_dis_device_conn_other))
        dialog.setOnCommClickListener { position ->
            dialog.dismiss()
            if (position == 0x01) {   //确定
                if (isBind) {
                    bindList?.get(index)?.connStatus = ConnStatus.CONNECTING
                    bindAdapter?.notifyItemChanged(index)
                } else {
                    list?.get(index)?.connStatus = ConnStatus.CONNECTING
                    adapter?.notifyItemChanged(index)
                    showDialog(resources.getString(R.string.string_connecting))
                }

                handlers.sendEmptyMessage(0x01)
                handlers.postDelayed(Runnable {

                    service.connDeviceBack(
                        bean.bleName, bean.bleMac
                    ) { mac, status ->
                        hideDialog()

                        DbManager.getInstance().saveUserBindDevice(
                            bean.bleName,
                            bean.bleMac,
                            BikeUtils.getFormatDate(
                                System.currentTimeMillis(),
                                "yyyy-MM-dd HH:mm:ss"
                            )
                        )
                        MmkvUtils.saveProductNumberCode(bean.productNumber)
                        MmkvUtils.saveConnDeviceMac(mac)
                        MmkvUtils.saveConnDeviceName(bean.bleName)
                        BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                        viewModel?.getLocation(this@SecondScanActivity, this@SecondScanActivity)
                        getDeviceType()
                        dealScanDevice()
                    }
                }, 2000)

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



    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            Timber.e("---------acdtion="+action)
            if(action == BleConstant.BLE_START_SCAN_ACTION){ //开始连接了


            }
            if(action == BleConstant.BLE_CONNECTED_ACTION){
                ToastUtils.show(resources.getString(R.string.string_conn_success))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                val saveMac = MmkvUtils.getConnDeviceMac()
                if(!BikeUtils.isEmpty(saveMac)){
                    bindList?.forEach {
                        if(it.bleMac ==saveMac){
                            it.connStatus = ConnStatus.CONNECTED
                            it.rssi = 0
                        }
                    }
                    bindAdapter?.notifyDataSetChanged()
                }


            }
            if(action == BleConstant.BLE_DIS_CONNECT_ACTION){
                ToastUtils.show(resources.getString(R.string.string_conn_disconn))

            }


            //蓝牙状态
            if(action == BluetoothAdapter.ACTION_STATE_CHANGED){
                val state = p1.getIntExtra(BluetoothAdapter.EXTRA_STATE,0)
                Timber.e("------蓝牙状态="+state)
                if(state == BluetoothAdapter.STATE_ON){ //蓝牙打开
                    showBleState(false)
                    handlers.sendEmptyMessageDelayed(0x01,3000)
                    handlers.postDelayed(Runnable {
                     dealScanDevice()
                    },5000)

                }

                if(state == BluetoothAdapter.STATE_TURNING_OFF){
                    handlers.sendEmptyMessage(0x00)
                }

                if(state == BluetoothAdapter.STATE_OFF){    //关闭

                    bindList?.clear()
                    bindAdapter?.notifyDataSetChanged()
                    repeatList?.clear()
                    list?.clear()
                    adapter?.notifyDataSetChanged()

                    showBleState(true)
                }
            }
        }

    }


    //是否显示蓝牙已关闭的文字
    private fun showBleState(isShow : Boolean){
        scanBleTv?.visibility = if(isShow) View.VISIBLE else View.GONE
    }

    val typeMap = BaseApplication.supportDeviceTypeMap
    private val scanCallBack : ScanCallback = object : ScanCallback(){

        override fun onScanResult(callbackType: Int, p0: ScanResult?) {
            super.onScanResult(callbackType, p0)
            Timber.e("-----------扫描="+p0?.device?.address+" "+p0?.scanRecord?.deviceName+" "+p0?.device?.name)
            stringBuilder.delete(0,stringBuilder.length);
            if (p0?.getScanRecord() == null || p0.scanRecord?.bytes == null)
                return
            val bleMac = p0.device.address
            // Timber.e("----搜索="+bleMac+" "+p0?.address)
            val tempStr = Utils.formatBtArrayToString(p0.scanRecord?.bytes)
            //Timber.e("-------rssi="+tempStr)
            // stringBuilder.append(tempStr)
            val recordStr = tempStr
            val bleName = p0?.scanRecord?.deviceName
            // Timber.e("--------扫描="+p0.name+" "+recordStr)
            if (BikeUtils.isEmpty(bleName) || bleName.equals("NULL") || BikeUtils.isEmpty(bleMac))
                return
            if (repeatList?.contains(bleMac) == true)
                return
            if(BikeUtils.isEmpty(recordStr)){
                return
            }

            if(bleName?.lowercase(Locale.ROOT)?.contains("huawei") == true){
                return
            }
            if (repeatList?.size!! > 40) {
                return
            }
            if(abs(p0.rssi) >85)
                return

            Timber.e("------bidList="+(bindList != null) +" "+Gson().toJson(bindList)+" "+(bindList?.size!! >0))

            if(bindList?.isNotEmpty()==true && bindList?.size!! >0){
                bindList?.forEach {
                    Timber.e("--------bindList="+it.bleName+" bindMac="+it.bleMac+ " rssi="+p0.rssi +" scan="+bleMac)
                    if(it.bleMac == bleMac){
                        it.rssi = p0.rssi
                    }
                }

                bindAdapter?.notifyDataSetChanged()

                val isHas =  bindList?.any {
                    it.bleMac == bleMac
                }

                Timber.e("------是否包含="+isHas)
                if(isHas == true){

                    return
                }

                typeMap.forEach {
                    val keyStr = it.key
                    val tempK = Utils.changeStr(keyStr)
                    val scanRecord = recordStr.lowercase(Locale.ROOT)
                    val front = scanRecord.contains(keyStr.lowercase(Locale.ROOT))
                    val back = scanRecord.contains(tempK.lowercase(Locale.ROOT))
                    Timber.e("----转换="+tempK)
                    if(front || back){
                        //判断少于40个设备就不添加了
                        if (repeatList?.size!! > 40) {
                            return
                        }
                        if(!repeatList!!.contains(bleMac)){
                            bleMac?.let { repeatList?.add(it) }
                            val b = BleBean(p0.device,p0.rssi,keyStr,scanRecord)
                            b.bleMac = bleMac
                            b.bleName = bleName
                            list?.add(b)
                            list?.sortBy {
                                Math.abs(it.rssi)
                            }
                        }

                    }
                }

                adapter?.notifyDataSetChanged()
                return
            }



//            if (repeatList?.size!! > 40) {
//                return
//            }
//            if(!repeatList!!.contains(bleMac)){
//                bleMac?.let { repeatList?.add(it) }
//                val b = BleBean(p0.device,p0.rssi,"",recordStr)
//                b.bleMac = bleMac
//                b.bleName = bleName
//                list?.add(b)
//                list?.sortBy {
//                    Math.abs(it.rssi)
//                }
//            }


            typeMap.forEach {
                val keyStr = it.key
                val tempK = Utils.changeStr(keyStr)
                val scanRecord = recordStr.lowercase(Locale.ROOT)
                val front = scanRecord.contains(keyStr.lowercase(Locale.ROOT))
                val back = scanRecord.contains(tempK.lowercase(Locale.ROOT))
                Timber.e("----转换="+tempK)
                if(front || back){
                    //判断少于40个设备就不添加了
                    if (repeatList?.size!! > 40) {
                        return
                    }
                    if(!repeatList!!.contains(bleMac)){
                        bleMac?.let { repeatList?.add(it) }
                        val b = BleBean(p0.device,p0.rssi,keyStr,scanRecord)
                        b.bleMac = bleMac
                        b.bleName = bleName
                        list?.add(b)
                        list?.sortBy {
                            Math.abs(it.rssi)
                        }
                    }

                }
            }


            adapter?.notifyDataSetChanged()

        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }


    override fun onBackPressed() {
        stopScan()
        super.onBackPressed()
    }

    @SuppressLint("MissingPermission")
    private fun stopScan(){
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallBack)
    }


    private var  dialog : DeleteDeviceDialog ?= null
    private fun showPermissionAlert(){
        if(dialog == null){
            dialog = DeleteDeviceDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        }
       if(dialog?.isShowing == false){
           dialog?.show()
       }

        dialog?.setTitleTxt(resources.getString(R.string.string_location_permission_prompt))
        dialog?.setOnCommClickListener { position ->
            dialog?.dismiss()
            if (position == 0x01) {
                XXPermissions.startPermissionActivity(this@SecondScanActivity)

            }
        }

        val window = dialog!!.window
        val windowLayout = window?.attributes
        val metrics2: DisplayMetrics = resources.displayMetrics
        val widthW: Int = metrics2.widthPixels

        windowLayout?.width = widthW
        windowLayout?.gravity = Gravity.BOTTOM
        window?.attributes = windowLayout
    }



    //获取设备的类型，一代或二代
    private fun getDeviceType(){
        BaseApplication.getBaseApplication().bleOperate.getDeviceVersionData(object :
            OnCommBackDataListener{
            override fun onIntDataBack(value: IntArray?) {
                val type = value?.get(1)
                if (type != null) {
                    Timber.e("-----设备类型=$type")
                    MmkvUtils.setDeviceType(type)
                    BaseApplication.getBaseApplication().setDeviceTypeConst(type)
                }
            }

            override fun onStrDataBack(vararg value: String?) {

            }

        })
    }
}