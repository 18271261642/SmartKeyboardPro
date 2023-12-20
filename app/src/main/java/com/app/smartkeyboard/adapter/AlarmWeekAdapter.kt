package com.app.smartkeyboard.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.app.smartkeyboard.R
import com.app.smartkeyboard.bean.AlarmWeekBean
import com.google.gson.Gson
import com.hjq.shape.view.ShapeTextView
import com.hjq.shape.view.ShapeView
import timber.log.Timber

class AlarmWeekAdapter(private val context: Context,private val list : MutableList<AlarmWeekBean>) : Adapter<AlarmWeekAdapter.WeekViewHolder>(){

    private var onClick : OnCommItemClickListener ?=null

    fun setOnItemClick(c : OnCommItemClickListener){
        this.onClick = c
    }


    class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tv = itemView.findViewById<ShapeView>(R.id.itemEditAlarmWeekTv)
        val wkNameTv = itemView.findViewById<TextView>(R.id.itemAlarmWkTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_edit_alarm_week_layout,parent,false)
        return WeekViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
//            weekBean.isChecked = weekBean.isChecked
//            notifyItemChanged(position)
            onClick?.onItemClick(position)
        }

       val weekBean = list[position]
        Timber.e("------item="+Gson().toJson(weekBean))

        holder.wkNameTv.text = weekBean.weekName

        if(weekBean.isChecked){
            holder.tv.visibility = View.VISIBLE
        }else{
            holder.tv.visibility = View.INVISIBLE
        }

    }
}