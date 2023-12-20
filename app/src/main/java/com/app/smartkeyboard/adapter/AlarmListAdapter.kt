package com.app.smartkeyboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.app.smartkeyboard.R
import com.blala.blalable.Utils
import com.blala.blalable.blebean.AlarmBean
import timber.log.Timber

class AlarmListAdapter(val context : Context,val list : MutableList<AlarmBean>) : RecyclerView.Adapter<AlarmListAdapter.AlarmListViewHolder>(){


    class AlarmListViewHolder(val view : View) : RecyclerView.ViewHolder(view){
         val timeTv = view.findViewById<TextView>(R.id.itemAlarmListTimeTv)
         val checkBox = view.findViewById<CheckBox>(R.id.itemAlarmListCheckBox)
        val itemAlarmListRepeatTv = view.findViewById<TextView>(R.id.itemAlarmListRepeatTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmListViewHolder {
       val v = LayoutInflater.from(context).inflate(R.layout.item_alarm_list_layout,parent,false)
        return AlarmListViewHolder(v)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: AlarmListViewHolder, position: Int) {
        val bean = list[position]
        val repeat = bean.repeat

        holder.timeTv.text = String.format("%02d",bean.hour)+":"+String.format("%02d",bean.minute)
        holder.checkBox.isChecked = bean.isOpen

        val str = Utils.byteToBit(repeat)
        Timber.e("-----str="+str)
        holder.itemAlarmListRepeatTv.text = getRepeat(str)
    }


    var sb = StringBuffer()
    private fun getRepeat(repeatStr : String) : String{
        sb.delete(0,sb.length)
        val chartArray = repeatStr.toCharArray()
        //周日
        if(chartArray[0].toString() == "1"){
            sb.append(context.resources.getString(R.string.string_sunday))
        }
        //周一
        if(chartArray[1].toString() == "1"){
            sb.append(context.resources.getString(R.string.string_monday))
        }
        //周二
        if(chartArray[2].toString() == "1"){
            sb.append(context.resources.getString(R.string.string_tuesday))
        }
        //周三
        if(chartArray[3].toString() == "1"){
            sb.append(context.resources.getString(R.string.string_wednesday))
        }
        //周四
        if(chartArray[4].toString() == "1"){
            sb.append(context.resources.getString(R.string.string_thursday))
        }
        //周五
        if(chartArray[5].toString() == "1"){
            sb.append(context.resources.getString(R.string.string_friday))
        }
        //周六
        if(chartArray[6].toString() == "1"){
            sb.append(context.resources.getString(R.string.string_saturday))
        }
        return sb.toString()
    }
}