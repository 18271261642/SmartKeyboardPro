package com.app.smartkeyboard.second

import android.view.View
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.bean.DbManager
import com.app.smartkeyboard.utils.BikeUtils
import com.app.smartkeyboard.utils.MmkvUtils
import com.bonlala.widget.view.ClearEditText
import com.hjq.toast.ToastUtils

class SecondEditBindNameActivity : AppActivity() {

    private var secondEditNameEdit : ClearEditText ?= null
    private var mac : String ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_second_edit_name_layout
    }

    override fun initView() {
        secondEditNameEdit = findViewById(R.id.secondEditNameEdit)
    }

    override fun onRightClick(view: View?) {
        super.onRightClick(view)
        //保存
        val inputEdit = secondEditNameEdit?.text.toString()
        if(BikeUtils.isEmpty(inputEdit)){
            ToastUtils.show("请输入名称!")
            return
        }
        if(mac != null){
            //查询
            val bindBean = DbManager.getInstance().getBindDevice(mac)
            if(bindBean != null){
                bindBean.deviceName = inputEdit
                MmkvUtils.saveConnDeviceName(inputEdit)
                DbManager.getInstance().saveUserBindDevice(inputEdit,mac,bindBean.bindTime)

                finish()
            }
        }

    }


    override fun initData() {
       //回填名称
        mac = intent.getStringExtra("bind_mac")
        //查询
        val bindBean = DbManager.getInstance().getBindDevice(mac)
        if(bindBean != null){
            secondEditNameEdit?.setText(bindBean.deviceName)
        }

    }
}