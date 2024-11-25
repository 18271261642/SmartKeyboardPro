package com.app.smartkeyboard.viewmodel

import androidx.lifecycle.ViewModel
import com.app.smartkeyboard.bean.DbManager
import com.app.smartkeyboard.bean.GifHistoryBean
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GifViewModel : ViewModel(){

    var gifDbData = SingleLiveEvent<MutableList<GifHistoryBean>?>()


    //查询数据库中保存的图片或GIF数据
    fun queryDeviceTypeGifData(isSecondDevice : Boolean){
        GlobalScope.launch {
            val list = DbManager.getInstance().queryAllGifRecord()
            if(list == null){
                gifDbData.postValue(null)
                return@launch
            }
            val tempList = arrayListOf<GifHistoryBean>()
            list.forEach {
                if(isSecondDevice && (it.deviceType == 2)){
                    tempList.add(it)
                }
                if(!isSecondDevice && it.deviceType == 1){
                    tempList.add(it)
                }
            }
            gifDbData.postValue(tempList)
        }
    }

    //修改
    fun updateGifRecord(bean : GifHistoryBean,speed : Int){
        DbManager.getInstance().updateGifRecordSpeedById(bean,speed)
    }

    //根据时间戳字符串删除一条数据
    fun deleteFileRecord(id : Long){
        DbManager.getInstance().deleteGifRecordByTime(id)
    }
}