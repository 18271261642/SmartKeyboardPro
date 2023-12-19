package com.app.smartkeyboard.dialog

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.app.smartkeyboard.R
import com.app.smartkeyboard.adapter.OnCommItemClickListener
import com.hjq.shape.view.ShapeTextView

/**
 * Created by Admin
 *Date 2023/6/16
 */
class UpgradeDialogView : AppCompatDialog {


    private var dialogUpgradeCancelTv : ShapeTextView ?= null
    private var dialogUpgradeConfirmTv : ShapeTextView ?= null


    private var upgradeContentTv : TextView ?= null

    private var onClick : OnCommItemClickListener?= null

    fun setOnDialogClickListener(onCommItemClickListener: OnCommItemClickListener){
        this.onClick = onCommItemClickListener
    }


    constructor(context: Context) : super (context){

    }


    constructor(context: Context, theme : Int) : super (context, theme){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_show_upgrade_layout)

        upgradeContentTv = findViewById(R.id.upgradeContentTv)
        dialogUpgradeCancelTv = findViewById(R.id.dialogUpgradeCancelTv)
        dialogUpgradeConfirmTv = findViewById(R.id.dialogUpgradeConfirmTv)

        dialogUpgradeCancelTv?.setOnClickListener {
            onClick?.onItemClick(0x00)
        }

        dialogUpgradeConfirmTv?.setOnClickListener {
            onClick?.onItemClick(0x01)
        }

    }


    fun setContentTxt(txt : String){
        upgradeContentTv?.text = txt
    }
}