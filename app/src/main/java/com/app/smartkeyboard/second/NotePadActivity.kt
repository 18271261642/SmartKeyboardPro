package com.app.smartkeyboard.second

import android.graphics.Color
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.adapter.OnClickLongListener
import com.app.smartkeyboard.adapter.OnCommItemClickListener
import com.app.smartkeyboard.adapter.SecondNotePadAdapter
import com.app.smartkeyboard.bean.DbManager
import com.app.smartkeyboard.bean.NoteBookBean
import com.app.smartkeyboard.ble.ConnStatus
import com.app.smartkeyboard.dialog.DeleteDeviceDialog
import com.app.smartkeyboard.utils.BikeUtils
import com.app.smartkeyboard.viewmodel.NoteBookViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar


/**
 * Created by Admin
 *Date 2023/7/4
 */
class NotePadActivity : AppActivity() {

    private val viewModel by viewModels<NoteBookViewModel>()

    private var secondNoteRecyclerView : RecyclerView ?= null
    private var adapter : SecondNotePadAdapter ?= null
    private var list : MutableList<NoteBookBean> ?= null



    override fun getLayoutId(): Int {
       return R.layout.activity_note_pad_layout
    }

    override fun initView() {
        secondNoteRecyclerView = findViewById(R.id.secondNoteRecyclerView)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        secondNoteRecyclerView?.layoutManager = linearLayoutManager
        list = ArrayList<NoteBookBean>()

        adapter = SecondNotePadAdapter(this, list as ArrayList<NoteBookBean>)
        secondNoteRecyclerView?.adapter = adapter


        adapter?.setOnEditItemClickListener(object : OnCommItemClickListener{
            override fun onItemClick(position: Int) {
                list?.get(position)?.let { showDeleteOrUpdate(it) }
            }

        })

        //点击
        adapter?.setOnCommClickListener(object : OnCommItemClickListener{
            override fun onItemClick(position: Int) {
                if(BikeUtils.isEmpty(mac)){
                    showConnDialog()
                    return
                }
                list?.get(position)?.let { showSyncDialog(it) }
            }

        })
        //长按
        adapter?.setOnLongClickListener(object : OnClickLongListener{
            override fun onLongClick(position: Int) {

                list?.get(position)?.let { showDeleteOrUpdate(it) }
            }

        })




        findViewById<ImageView>(R.id.secondAddNoteImg).setOnClickListener {
            startActivity(SecondAddEditActivity::class.java)
        }


    }

    override fun initData() {
        viewModel.allNoteBookData.observe(this){

            list?.clear()
            if(it != null && it.isNotEmpty()){
                list?.addAll(it)
                list?.sortByDescending { it.noteTimeLong }
            }

            adapter?.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        getAllDbData()
    }

    //查询所有的数据
    private fun getAllDbData(){
        viewModel.getAllDbData()
    }


    private fun showSyncDialog(noteB : NoteBookBean){
        val dialog = DeleteDeviceDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setTitleTxt(resources.getString(R.string.string_set_to_device))
        dialog.setConfirmBgColor(Color.parseColor("#F86849"))
        dialog.setOnCommClickListener(object : OnCommItemClickListener{
            override fun onItemClick(position: Int) {
                dialog.dismiss()
                if(position == 0x01){
                    sendNotToDevice(noteB.noteTitle,noteB.noteTimeLong,noteB.noteContent)
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


    private fun showDeleteOrUpdate(noteB : NoteBookBean){
        val dialog = DeleteDeviceDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setTitleTxt(resources.getString(R.string.string_choose_delete_or_edit))
        dialog.setConfirmAndCancelTxt(resources.getString(R.string.string_edit), resources.getString(R.string.string_delete_txt))
        dialog.setCancelBgColor(Color.parseColor("#F86849"))
        dialog.setConfirmBgColor(Color.parseColor("#16AEA0"))
        dialog.setOnCommClickListener { position ->
            dialog.dismiss()
            if (position == 0x01) {   //编辑
                startActivity(
                    SecondAddEditActivity::class.java,
                    arrayOf("timeKey"),
                    arrayOf(noteB?.saveTime)
                )
            }
            if (position == 0) {  //删除
                deleteDialog(noteB)
//                val timeLong = noteB.noteTimeLong
//                val deviceTimeLong = timeLong / 1000 - 946656000L
//                if (deviceTimeLong != null) {
//                    BaseApplication.getBaseApplication().bleOperate.deleteIndexNote(deviceTimeLong)
//                }
//
//                DbManager.getInstance().deleteNotebook(noteB.saveTime)
//                GlobalScope.launch {
//                    delay(500)
//                    getAllDbData()
//                }
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


    private fun deleteDialog(bean : NoteBookBean){
        val dialog = DeleteDeviceDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setTitleTxt(resources.getString(R.string.string_is_delete_note))
        dialog.setConfirmAndCancelTxt(resources.getString(R.string.common_confirm), resources.getString(R.string.common_cancel))
        dialog.setCancelBgColor(Color.parseColor("#16AEA0"))
        dialog.setConfirmBgColor(Color.parseColor("#F86849"))
        dialog.setOnCommClickListener { position ->
            dialog.dismiss()
            if (position == 0x01) {   //确定
                val timeLong = bean.noteTimeLong
                val deviceTimeLong = timeLong / 1000 - 946656000L
                if (deviceTimeLong != null) {
                    BaseApplication.getBaseApplication().bleOperate.deleteIndexNote(deviceTimeLong)
                }

                DbManager.getInstance().deleteNotebook(bean.saveTime)
                GlobalScope.launch {
                    delay(500)
                    getAllDbData()
                }
            }
            if (position == 0) {  //取消

              dialog.dismiss()
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



    //发送数据
    private fun sendNotToDevice(title : String,timeLong : Long,contentStr : String){
        if(BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED){
            return
        }

        //时间戳
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeLong
        BaseApplication.getBaseApplication().bleOperate.sendKeyBoardNoteBook(title,contentStr,calendar)
    }
}