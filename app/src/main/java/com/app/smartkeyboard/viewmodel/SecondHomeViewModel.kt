package com.app.smartkeyboard.viewmodel

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.bean.WeatherBean
import com.app.smartkeyboard.listeners.LocationAreaListener
import com.app.smartkeyboard.utils.DateUtil
import com.app.smartkeyboard.utils.GsonUtils
import com.app.smartkeyboard.utils.LocationUtils
import com.app.smartkeyboard.utils.TimeUtils
import com.blala.blalable.HexDump
import com.blala.blalable.Utils
import com.google.gson.Gson
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnHttpListener
import org.json.JSONObject
import timber.log.Timber

class SecondHomeViewModel : ViewModel() {


     var weatherStr = SingleLiveEvent<String>()


    //获取所有的设备类型列表
    fun getAllSupportDeviceType(lifecycleOwner: LifecycleOwner){
        EasyHttp.get(lifecycleOwner).api("productNumberList").request(object : OnHttpListener<String>{
            override fun onHttpSuccess(result: String?) {
                val jsonObject = JSONObject(result)
                if(jsonObject.getInt("code")==200){
                    val data = jsonObject.getString("data")
                    val list = GsonUtils.getGsonObject<List<String>>(data)
                    list?.forEach {
                        BaseApplication.supportDeviceTypeMap[it] = it
                    }

                }

            }

            override fun onHttpFail(e: Exception?) {

            }

        })
    }


    //定位
    fun getLocation(context: Context,lifecycleOwner: LifecycleOwner){
        LocationUtils().startLocation(context,object : LocationAreaListener{
            override fun backLatLon(lat: Double, lng: Double) {
                getWeatherInfo(lifecycleOwner,lat, lng)
                LocationUtils().stopLocation()
            }

            override fun backCityStr(area: String?) {

            }

        })
    }


    //获取天气信息
    fun getWeatherInfo(lifecycleOwner: LifecycleOwner,lat : Double,lng : Double){
        EasyHttp.get(lifecycleOwner).api("weather/info?location=${String.format("%.2f",lng)},${String.format("%.2f",lat)}").request(object : OnHttpListener<String>{
            override fun onHttpSuccess(result: String?) {
                try {
                    val jsonObject = JSONObject(result)
                    if(jsonObject.getInt("code") == 200){
                        val data = jsonObject.getString("data")
                        val weatherBean = GsonUtils.getGsonObject<WeatherBean>(data)
                        if(weatherBean != null){
                            dealWithWeather(weatherBean)
                        }
                    }
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }

            override fun onHttpFail(e: Exception?) {

            }

        })
    }


    private fun dealWithWeather(weatherBean : WeatherBean){
        Timber.e("--------bbb="+Gson().toJson(weatherBean))
        val logSb = StringBuffer()
        logSb.append(Gson().toJson(weatherBean)+"\n\n\n")
        val stringBuffer = StringBuffer()
        stringBuffer.append("010016")
        //当天的
        var todayArray = ByteArray(22)
        //时间戳 4个byte
        val currTime: Long = weatherBean.dateTimeStamp
        val constanceMils = 946656000L
        val currTimeLong = currTime - constanceMils
        //Log.e("111","-----相差="+currTimeLong)
        todayArray = HexDump.toByteArray(currTimeLong)
        stringBuffer.append(HexDump.bytesToString(todayArray))

        //天气类型
        stringBuffer.append(String.format("%02d",weatherBean.statusCode))

        //当前时刻的温度 2 个byte Integer.parseInt(weatherBean.getTemp()) * 10
        val currTempByte = HexDump.toByteArrayTwo(weatherBean.temp.toInt() * 10)
        val tmpStr = HexDump.bytesToString(currTempByte)

        stringBuffer.append(tmpStr)

        //   stringBuffer.append("00FA")
        //当天最高温度 2 个byte

        //   stringBuffer.append("00FA")
        //当天最高温度 2 个byte
        val currMaxTempByte = HexDump.toByteArrayTwo((weatherBean.getTempMax().toFloat() * 10).toInt())
        stringBuffer.append(HexDump.bytesToString(currMaxTempByte))
        //当天最低温度 2 个byte
        val currMinTempByte = HexDump.toByteArrayTwo((weatherBean.getTempMin().toFloat() * 10).toInt())
        stringBuffer.append(HexDump.bytesToString(currMinTempByte))
        //空气质量指数 2 个byte weatherBean.getAirAqi() as Int
        val airAqiByte = HexDump.toByteArrayTwo(50)
        stringBuffer.append(HexDump.bytesToString(airAqiByte))
        //相对湿度 2 个byte
        val humidityByte = HexDump.toByteArrayTwo(weatherBean.getHumidity().toInt())
        stringBuffer.append(HexDump.bytesToString(humidityByte))
        //紫外线指数 1个byte
        val uvIndexByte: Byte = Integer.valueOf(weatherBean.getUvIndex()).toByte()
        stringBuffer.append(String.format("%02x", uvIndexByte))
        //日出时间
        val sunriseTime: String = weatherBean.getSunrise()
        //日出时 一个byte
        stringBuffer.append(String.format("%02x", DateUtil.getHHmmForHour(sunriseTime)))
        //日出分 一个byte
        stringBuffer.append(String.format("%02x", DateUtil.getHHmmForMinute(sunriseTime)))
        //日落时间
        val sunsetTime: String = weatherBean.getSunset()
        //日落时 一个byte
        stringBuffer.append(String.format("%02x", DateUtil.getHHmmForHour(sunsetTime)))
        //日落分 一个byte
        stringBuffer.append(String.format("%02x", DateUtil.getHHmmForMinute(sunsetTime)))
        //风速
        val wind = HexDump.toByteArrayTwo((weatherBean.windKph.toFloat()*10).toInt())
        val windStr = HexDump.bytesToString(wind)
        stringBuffer.append(windStr)



        //24小时的天气

        val hourList = weatherBean.hourly
        val hourSb = StringBuilder()
        hourList.forEach {
            //时间
            val time = it.dateTime
            val timeLong = TimeUtils.formatDateToLong(time,"yyyy-MM-dd HH:mm:ss")
            val constanceMils = 946656000L
            val currTimeLong = timeLong - constanceMils
            //Log.e("111","-----相差="+currTimeLong)
            val hourTimeArray = HexDump.toByteArray(currTimeLong)
            hourSb.append(HexDump.bytesToString(hourTimeArray))
            //持续时间+元数组长度
            hourSb.append("0103")
            //天气类型
            hourSb.append(String.format("%02x",it.statusCode))
            //温度
            //温度两个byte
            val byteTem = HexDump.toByteArrayTwo((it.temp.toFloat() * 10).toInt())
            hourSb.append(HexDump.bytesToString(byteTem))
        }

        //24小时的总长度
        val hourLength = hourSb.toString().length/2
        val hourLenghtArray = HexDump.toByteArrayTwo(hourLength)
        val hourStr = HexDump.bytesToString(hourLenghtArray)
        stringBuffer.append("02").append(hourStr).append(hourSb.toString())

        val contentStr = stringBuffer.toString()
        val result = Utils.getFullPackage(Utils.getPlayer("04", "07", Utils.hexStringToByte(contentStr)))

        logSb.append(HexDump.bytesToString(result))

        BaseApplication.getBaseApplication().bleOperate.writeWeatherData(result)

        weatherStr.postValue(logSb.toString())
    }


    /**
     * 0x01：白天晴
    0x02: 白天多云
    0x03：阴
    0x04：雨
    0x05：雪
    0x06：夜晴
    0x07：夜多云
    0xFF ：晴天,获取不到或者有异常
     */
    private fun changeWeatherState(stateStr : String) : Byte{
        if(stateStr == "白天晴")
            return 0x01
        if(stateStr == "白天多云")
            return 0x02
        if(stateStr == "阴")
            return 0x03
        if(stateStr == "雨")
            return 0x04
        if(stateStr == "雪")
            return 0x05
        if(stateStr == "夜晴")
            return 0x06
        if(stateStr == "夜多云")
            return 0x07
        return 0xFF.toByte()
    }
}