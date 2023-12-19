package com.app.smartkeyboard.second

import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.viewmodel.SecondHomeViewModel

class WeatherLogActivity : AppActivity() {

    private var viewModel : SecondHomeViewModel ?= null


    private var weatherLogTv : TextView ?= null
    private var wLogBtn : Button ?= null


    override fun getLayoutId(): Int {
        return R.layout.activity_weather_log_layout
    }

    override fun initView() {

        weatherLogTv = findViewById(R.id.weatherLogTv)
        wLogBtn = findViewById(R.id.wLogBtn)
        wLogBtn?.setOnClickListener {
            viewModel?.getLocation(this@WeatherLogActivity,this@WeatherLogActivity)
        }


    }

    override fun initData() {
        viewModel = ViewModelProvider(this)[SecondHomeViewModel::class.java]

        viewModel?.weatherStr?.observe(this){
            weatherLogTv?.text = it
        }
    }
}