package com.app.smartkeyboard.second

import android.widget.TextView
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity

class ScanLogActivity : AppActivity() {


    private var scanLogTv : TextView ?= null


    override fun getLayoutId(): Int {
        return R.layout.activity_scan_log_layout
    }

    override fun initView() {
        scanLogTv = findViewById(R.id.scanLogTv)
    }

    override fun initData() {
        val str =  BaseApplication.supportDeviceTypeMap
        scanLogTv?.text = resources.getString(R.string.string_scan_filter)+": "+str
    }
}