package com.app.smartkeyboard.second

import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.viewmodel.SecondHomeViewModel
import com.blala.blalable.Utils
import com.blala.blalable.listener.WriteBackDataListener
import timber.log.Timber

class WeatherLogActivity : AppActivity() {

    private var viewModel : SecondHomeViewModel ?= null


    private var weatherLogTv : TextView ?= null
    private var wLogBtn : Button ?= null

    private var getDialFlashBtn : Button ?= null


    override fun getLayoutId(): Int {
        return R.layout.activity_weather_log_layout
    }

    override fun initView() {
        getDialFlashBtn = findViewById(R.id.getDialFlashBtn)
        weatherLogTv = findViewById(R.id.weatherLogTv)
        wLogBtn = findViewById(R.id.wLogBtn)
        wLogBtn?.setOnClickListener {
            viewModel?.getLocation(this@WeatherLogActivity,this@WeatherLogActivity)
        }

        getDialFlashBtn?.setOnClickListener {
            val array = byteArrayOf(0x09, 0x01, 0x00)
            val resultArray = Utils.getFullPackage(array)
            Timber.e("----------array="+ Utils.formatBtArrayToString(resultArray))
            BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray
            ) { data ->
                Timber.e("-------result=" + Utils.formatBtArrayToString(data))

                weatherLogTv?.text = Utils.formatBtArrayToString(data)
            }
        }


    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[SecondHomeViewModel::class.java]

        viewModel?.weatherStr?.observe(this){
            weatherLogTv?.text = it
        }
    }
}