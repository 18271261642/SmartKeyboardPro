package com.app.smartkeyboard.second

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkeyboard.BaseApplication
import com.app.smartkeyboard.R
import com.app.smartkeyboard.action.AppActivity
import com.app.smartkeyboard.adapter.GifHistoryAdapter
import com.app.smartkeyboard.adapter.OnCommItemClickListener
import com.app.smartkeyboard.adapter.OnCommMenuClickListener
import com.app.smartkeyboard.bean.DbManager
import com.app.smartkeyboard.bean.GifHistoryBean
import com.app.smartkeyboard.ble.ConnStatus
import com.app.smartkeyboard.ble.DeviceTypeConst
import com.app.smartkeyboard.dialog.DeleteDeviceDialog
import com.app.smartkeyboard.dialog.ShowProgressDialog
import com.app.smartkeyboard.dialog.UploadAnimationDialog
import com.app.smartkeyboard.img.CameraActivity
import com.app.smartkeyboard.img.ImageSelectActivity
import com.app.smartkeyboard.utils.BikeUtils
import com.app.smartkeyboard.utils.BitmapAndRgbByteUtil
import com.app.smartkeyboard.utils.CalculateUtils
import com.app.smartkeyboard.utils.FileUtils
import com.app.smartkeyboard.utils.GetJsonDataUtil
import com.app.smartkeyboard.utils.ImageUtils
import com.app.smartkeyboard.utils.ImgUtil
import com.app.smartkeyboard.utils.MmkvUtils
import com.app.smartkeyboard.utils.ThreadUtils
import com.app.smartkeyboard.viewmodel.GifViewModel
import com.blala.blalable.Utils
import com.blala.blalable.keyboard.DialCustomBean
import com.blala.blalable.keyboard.KeyBoardConstant
import com.blala.blalable.listener.OnCommBackDataListener
import com.blala.blalable.listener.WriteBackDataListener
import com.bonlala.widget.layout.SettingBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.Target
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.shape.layout.ShapeConstraintLayout
import com.hjq.toast.ToastUtils
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class SecondGifHomeActivity : AppActivity() {

    val gifStringBuffer = StringBuffer()

    private var gifViewModel : GifViewModel ?= null

    private var gifHomeRecyclerView : RecyclerView ?= null
    private var gifHistoryAdapter : GifHistoryAdapter ?= null
    private var gifHistoryList = arrayListOf<GifHistoryBean>()


    private var gifHomeTitleBar : TitleBar ?= null
    //自定义
    private var secondGifCusLayout: LinearLayout? = null

    //自动锁定
    private var secondGifDormancyLayout: ShapeConstraintLayout? = null
    private var secondGifDormancyBar: SettingBar? = null

    //自定义速度
    private var secondCusSpeedLayout: ShapeConstraintLayout? = null
    private var secondCusSpeedSettingBar: SettingBar? = null

    //自定义的图片
    private var secondCusGifImageView: ImageView? = null
    private var secondDefaultAnimationImgView: ImageView? = null

    //裁剪图片
    private var cropImgPath: String? = null
    private var resultCropUri: Uri? = null

    private var saveCropPath: String? = null

    //对象
    private var dialBean = DialCustomBean()


    private var isSecondDevice = false

    private val handlers: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == -1){
                progressDialog?.dismiss()
                ToastUtils.show("图片解析失败!")
            }


            if (msg.what == 0x00) {
                cancelProgressDialog()
                val array = msg.obj as ByteArray
                // FileU.getFile(array,path,"gif.bin")
                setDialToDevice(array)

            }

            if (msg.what == 0x01) {
                cancelProgressDialog()
                val tempArray = msg.obj as ByteArray
                startDialToDevice(tempArray, false)
            }

            if (msg.what == 0x08) {
//                val log = msg.obj as String
                // gifLogTv?.text = log
            }


            if(msg.what == 0x88){
                hideDialog()
                val logPath = getExternalFilesDir(null)?.path
                val name = BikeUtils.getCurrDate()+".json"
                val file = "$logPath/$name"
                GetJsonDataUtil().openFileThirdApp(this@SecondGifHomeActivity,file)
            }

        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_second_gif_home_layout
    }

    override fun initView() {
        isSecondDevice= BaseApplication.getBaseApplication().deviceTypeConst==DeviceTypeConst.DEVICE_SECOND
        gifHomeRecyclerView = findViewById(R.id.gifHomeRecyclerView)
        val gridLayoutManager = GridLayoutManager(this,2)
        gridLayoutManager.orientation = GridLayoutManager.VERTICAL
        gifHomeRecyclerView?.layoutManager = gridLayoutManager
        val defaultBean = GifHistoryBean()
        defaultBean.isDefaultAni = true
        gifHistoryList.add(defaultBean)
        val selectBean = GifHistoryBean()
        selectBean.fileUrl = "+"
        selectBean.deviceType = if(isSecondDevice) 2 else 1
        selectBean.isDefaultAni = false
        gifHistoryList.add(selectBean)
        gifHistoryAdapter = GifHistoryAdapter(this,gifHistoryList)
        gifHomeRecyclerView?.adapter = gifHistoryAdapter

        gifHomeTitleBar = findViewById(R.id.gifHomeTitleBar)
        secondDefaultAnimationImgView = findViewById(R.id.secondDefaultAnimationImgView)
        secondGifCusLayout = findViewById(R.id.secondGifCusLayout)
        secondGifDormancyLayout = findViewById(R.id.secondGifDormancyLayout)
        secondGifDormancyBar = findViewById(R.id.secondGifDormancyBar)
        secondCusSpeedLayout = findViewById(R.id.secondCusSpeedLayout)
        secondCusSpeedSettingBar = findViewById(R.id.secondCusSpeedSettingBar)
        secondCusGifImageView = findViewById(R.id.secondCusGifImageView)


        secondGifCusLayout?.setOnClickListener(this)
        secondGifDormancyLayout?.setOnClickListener(this)
        secondCusSpeedLayout?.setOnClickListener(this)
        secondDefaultAnimationImgView?.setOnClickListener(this)


        gifHistoryAdapter?.setOnItemClick(object : OnCommMenuClickListener{
            override fun onItemClick(position: Int) { //菜单
               val bean = gifHistoryList[position]
                if(bean.fileUrl == "+"){ //添加
                    showPhotoDialog()
                    return
                }
                if(bean.isDefaultAni){  //默认动画
                    if (BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED) {
                        return
                    }
                    // BaseApplication.getBaseApplication().bleOperate.setLocalKeyBoardDial()
                    val array = byteArrayOf(0x09, 0x01, 0x00)
                    val resultArray = Utils.getFullPackage(array)
                    Timber.e("----------array="+Utils.formatBtArrayToString(resultArray))
                    BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray
                    ) { data -> Timber.e("-------result=" + Utils.formatBtArrayToString(data)) }
                    return
                }
                showUploadDialog(File(bean.fileUrl),position)
            }

            //删除
            override fun onChildItemClick(position: Int) {
                val bean = gifHistoryList[position]
                val file = File(bean.fileUrl)
                showDeleteDialog(file,position)
            }

        })


        //默认动画
        findViewById<LinearLayout>(R.id.secondDefaultAnimationLayout).setOnClickListener {

        }
//        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.gif_preview)
//        val circularBitmapDrawable: RoundedBitmapDrawable =
//            RoundedBitmapDrawableFactory.create(context.resources, bitmap)
//        circularBitmapDrawable.isCircular = true
//        secondDefaultAnimationImgView?.setImageDrawable(circularBitmapDrawable)
        Glide.with(context).load(R.drawable.gif_preview)
            .transform(MultiTransformation(CenterCrop(), CircleCrop()))
            .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.gif_preview)
            .into(secondDefaultAnimationImgView!!)


        gifHomeTitleBar?.setOnTitleBarListener(object : OnTitleBarListener{
            override fun onLeftClick(view: View?) {
                finish()
            }

            override fun onTitleClick(view: View?) {

            }

            override fun onRightClick(view: View?) {
                if(gifStringBuffer.isEmpty()){
                    ToastUtils.show("There is no log!")
                    return
                }
                showDialog("Share ...")
                //分享
                val gifLog = gifStringBuffer.toString()

                val logPath = getExternalFilesDir(null)?.path+"/"
                val name = BikeUtils.getCurrDate()+".json"
                GetJsonDataUtil().writeTxtToFile(gifLog,logPath,name)

                handlers.sendEmptyMessageDelayed(0x88,2000)
            }

        })
    }

    override fun initData() {
        gifViewModel = ViewModelProvider(this).get(GifViewModel::class.java)

        cropImgPath = this.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath


        gifViewModel?.gifDbData?.observe(this){
            if(it == null){

                return@observe
            }
            if(it.size==20){
                gifHistoryList.removeAt(1)
                gifHistoryList.addAll(it)
                gifHistoryAdapter?.notifyDataSetChanged()
                return@observe
            }
            it.forEachIndexed { index, gifHistoryBean ->
                gifHistoryList.add(index+1,gifHistoryBean)
            }
            gifHistoryAdapter?.notifyDataSetChanged()
        }

        //查询数据库中的数据
        gifViewModel?.queryDeviceTypeGifData(isSecondDevice)



        //判断SD卡中是否有选择过的图片
//        val saveCropFile = File(cropImgPath)
//        Timber.e("-----目录=" + saveCropFile.path)
//        val array = saveCropFile.listFiles()
//        if (array != null && array.isNotEmpty()) {
//
//            val isSecond = BaseApplication.getBaseApplication().deviceTypeConst == DeviceTypeConst.DEVICE_SECOND
//
//            //判断尺寸
//            val firstFilePath = array[0].path
//            Timber.e("-----firstPath="+firstFilePath)
//            val file = File(firstFilePath)
//            if(file == null || !file.exists()){
//                return
//            }
//           if(firstFilePath.contains("jpg")){
//               val imgBitmap = BitmapFactory.decodeFile(firstFilePath)
//               val width = imgBitmap.width
//               if(isSecond){
//                   if(width!= 390){
//                       file.delete()
//                       loadDefaultImg()
//                   }
//               }else{
//                   if(width != 320){
//                       file.delete()
//                       loadDefaultImg()
//                   }
//               }
//              // return
//           }
//
//            if(firstFilePath.contains("gif")){
//                val width = ImageUtils.getGifWidth(file)
//                if(isSecond){
//                    if(width != 390){
//                        file.delete()
//                        loadDefaultImg()
//                    }
//                }else{
//                    if(width != 320){
//                        file.delete()
//                        loadDefaultImg()
//                    }
//                }
//            }
//
//            if(isSecond){
//                Glide.with(this).load(firstFilePath)
//                    .transform(MultiTransformation(CenterCrop(), CircleCrop())).skipMemoryCache(false).into(secondCusGifImageView!!)
//            }else{
//                Glide.with(this).load(firstFilePath)
//                   .into(secondCusGifImageView!!)
//            }
//
//        }
    }


    private fun loadDefaultImg(){
        Glide.with(this).load(R.mipmap.ic_second_add_gif).into(secondCusGifImageView!!)
    }

    override fun onClick(view: View?) {
        super.onClick(view)

        val id = view?.id

        when (id) {
            R.id.secondDefaultAnimationImgView -> {   //默认动画
                if (BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED) {
                    return
                }
               // BaseApplication.getBaseApplication().bleOperate.setLocalKeyBoardDial()
                val array = byteArrayOf(0x09, 0x01, 0x00)
                val resultArray = Utils.getFullPackage(array)
                Timber.e("----------array="+Utils.formatBtArrayToString(resultArray))
                BaseApplication.getBaseApplication().bleOperate.writeCommonByte(resultArray
                ) { data -> Timber.e("-------result=" + Utils.formatBtArrayToString(data)) }
            }

            R.id.secondGifCusLayout -> {  //自定义
                if (BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED) {
                    ToastUtils.show(resources.getString(R.string.string_device_not_connect))
                    return
                }

                val saveCropFile = File(cropImgPath)
                Timber.e("-----目录=" + saveCropFile.path)
                val array = saveCropFile.listFiles()
                if (array != null && array.size > 0) {
                  //  showUploadDialog(array.get(0))
                } else {
                    showPhotoDialog()
                }
            }

            R.id.secondGifDormancyLayout -> { //自动锁定
                startActivity(SecondAutoLockActivity::class.java)
            }

            R.id.secondCusSpeedLayout -> {    //自定义速度
                val intent = Intent(this@SecondGifHomeActivity, SecondGifSpeedActivity::class.java)
                startActivityForResult(intent, 1001)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        val time = MmkvUtils.getAutoLock()
        if (time != 0) {
            secondGifDormancyBar?.rightText =
                if (time < 60) time.toString() + resources.getString(R.string.string_second_time) else ((time / 60).toString() + resources.getString(
                    R.string.string_minute_time
                ))
        }

        val speed = MmkvUtils.getGifSpeed()
        secondCusSpeedSettingBar?.rightText = speed.toString()
    }


    private fun showUploadDialog(imagFile: File,index : Int) {
        val isGif = imagFile.name.contains("gif")
        val dialog = UploadAnimationDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setVisibility(isGif)
        dialog.setOnCommClickListener { position ->
            dialog.dismiss()
            if (position == 0x01) {   //上传到设备
                dialBean.imgUrl = imagFile.path
                if (isGif) {
                    dealWidthGif(imagFile.path)
                } else {
                    setDialToDevice(byteArrayOf(0x00))
                }
            }
            if (position == 0x02) {   //删除动画
                showDeleteDialog(imagFile,index)
            }
            if (position == 0x03) {   //自定义速度
                val bean = gifHistoryList[index]
                val intent = Intent(this@SecondGifHomeActivity, SecondGifSpeedActivity::class.java)
                intent.putExtra("file_url",bean.fileUrl)
                intent.putExtra("gif_speed",bean.gifSpeed)
                intent.putExtra("is_new_file",false)
                intent.putExtra("save_time",bean.saveTime)
                intent.putExtra("update_speed",true)
                intent.putExtra("gif_speed",bean.gifSpeed)
                intent.putExtra("index",index)
                startActivityForResult(intent, 1001)
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

    //提示删除的弹窗
    private fun showDeleteDialog(file : File,index : Int) {

        val dialog = DeleteDeviceDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setTitleTxt(resources.getString(R.string.string_delete_alert))
        dialog.setOnCommClickListener { position ->
            dialog.dismiss()
            if (position == 0x01) {   //确定
                file.delete()
//                Glide.with(this@SecondGifHomeActivity).load(R.mipmap.ic_second_add_gif)
//                    .into(secondCusGifImageView!!)
                val recordId = gifHistoryList.get(index)._id
                gifViewModel?.deleteFileRecord(recordId.toLong())
                gifHistoryList.removeAt(index)
                if(gifHistoryList.size==20){
                    val selectBean = GifHistoryBean()
                    selectBean.fileUrl = "+"
                    selectBean.deviceType = if(isSecondDevice) 2 else 1
                    selectBean.isDefaultAni = false
                    gifHistoryList.add(selectBean)
                }
                gifHistoryAdapter?.notifyDataSetChanged()
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


    //自定义弹窗
    private fun showPhotoDialog() {
        val dialog = DeleteDeviceDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        dialog.show()
        dialog.setTitleTxt(resources.getString(R.string.string_select_pick))
        dialog.setConfirmAndCancelTxt(
            resources.getString(R.string.string_take_photo),
            resources.getString(R.string.string_album)
        )
        dialog.setConfirmBgColor(Color.parseColor("#FF5733"))
        dialog.setCancelBgColor(Color.parseColor("#FF5733"))
        dialog.setOnCommClickListener { position ->
            dialog.dismiss()
            if (position == 0x01) {   //相机
                checkCamera()
            }
            if (position == 0x00) {   //相册
                showSelectDialog()
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


    //选择图片，展示弹窗
    private fun showSelectDialog() {
        Timber.e("------VERSION="+Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            XXPermissions.with(this).permission(Manifest.permission.READ_MEDIA_IMAGES).request { permissions, allGranted ->
                if(allGranted){
                    choosePick()
                }
            }
            return
        }
        val isGet = XXPermissions.isGranted(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        Timber.e("-----isGet="+isGet)
        if (isGet) {
            choosePick()
            return
        }

      //  ActivityCompat.requestPermissions(this@SecondGifHomeActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1001)


        XXPermissions.with(this).permission(
            arrayOf(
                Permission.READ_MEDIA_IMAGES,Permission.READ_MEDIA_VIDEO

            )
        ).request { permissions, all ->
            if (all) {
                choosePick()
            }
        }
    }


    //判断是否有相机权限
    private fun checkCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            XXPermissions.with(this).permission(Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.CAMERA).request { permissions, allGranted ->
                if(allGranted){
                    openCamera()
                }
            }
            return
        }


        if (XXPermissions.isGranted(this, Manifest.permission.CAMERA)) {
            openCamera()

        } else {
            XXPermissions.with(this).permission(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ).request { permissions, all ->
                if (all) {
                    openCamera()
                }
            }
        }
    }

    //相机拍照
    private fun openCamera() {
        // 点击拍照
        CameraActivity.start(this, object : CameraActivity.OnCameraListener {
            override fun onSelected(file: File) {
                Timber.e("--------xxxx=" + file.path)
                setSelectImg(file.path, 0)
            }

            override fun onError(details: String) {
                toast(details)
            }
        })
    }


    //选择图片
    private fun choosePick() {

        ImageSelectActivity.start(
            this@SecondGifHomeActivity
        ) { data -> setSelectImg(data.get(0), 0) }

    }


    private fun setSelectImg(localUrl: String, code: Int) {
        gifStringBuffer.delete(0,gifStringBuffer.length)
        Timber.e("--------选择图片=$localUrl")
        gifStringBuffer.append("----------->>>>选择图片 url=$localUrl\n")
        if (localUrl.contains(".gif")) {
            // dealWidthGif(localUrl)

            val gifList = ImageUtils.getGifDataBitmap(File(localUrl),isSecondDevice)
            if (gifList.size < 1) {
                ToastUtils.show(resources.getString(R.string.string_gig_small))
                return
            }
            gifStringBuffer.append("----------->>>>GIF的图片数量=${gifList.size}\n")
            val intent = Intent(this@SecondGifHomeActivity, SecondGifSpeedActivity::class.java)
            intent.putExtra("file_url", localUrl)
            startActivityForResult(intent, 1001)
            return
        }

        val uri: Uri
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(this, "$packageName.provider", File(localUrl))
        } else {
            Uri.fromFile(File(localUrl))
        }
        Timber.e("-----uri=$uri")
        gifStringBuffer.append("----------->>>>选择图片的地址 =$uri\n")
        val date = System.currentTimeMillis() / 1000
        val path = "$cropImgPath/$date.jpg"
        gifStringBuffer.append("----------->>>>选择图片裁剪的地址 =$path\n")
        this.saveCropPath = path
        val cropFile = File(path)
        val destinationUri = Uri.fromFile(cropFile)
        val uOPtions = UCrop.Options()

        if(BaseApplication.getBaseApplication().deviceTypeConst==DeviceTypeConst.DEVICE_FIRST){
            uOPtions.withAspectRatio(16F, 9F)
            uOPtions.withMaxResultSize(340, 192)
        }else{
            uOPtions.withMaxResultSize(390, 390)
            uOPtions.withAspectRatio(1F, 1F)
        }


        uOPtions.setFreeStyleCropEnabled(false)
        uOPtions.setHideBottomControls(true)
        uOPtions.setStatusBarColor(resources.getColor(R.color.second_theme_color))
        uOPtions.setRootViewBackgroundColor(resources.getColor(R.color.second_theme_color))
        uOPtions.setToolbarColor(resources.getColor(R.color.second_theme_color))
        uOPtions.setToolbarWidgetColor(resources.getColor(R.color.white))
        UCrop.of(uri, destinationUri)
            .withOptions(uOPtions)
            .start(this)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.e("------onActivityResult="+requestCode+" "+resultCode+" "+UCrop.RESULT_ERROR)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            //裁剪后的图片地址
            val cropFile = File(saveCropPath)
            if (cropFile != null) {
                val isFirst = BaseApplication.getBaseApplication().deviceTypeConst==DeviceTypeConst.DEVICE_FIRST || BaseApplication.getBaseApplication().deviceTypeConst==DeviceTypeConst.DEVICE_FIRST|| BaseApplication.getBaseApplication().deviceTypeConst==DeviceTypeConst.DEVICE_THIRD
                val b = BitmapFactory.decodeFile(cropFile.path) ?: return

                Timber.e("-------xy="+b.width+" h="+b.height)
                //计算偏移量
                val x: Int = if(isFirst) (b.width / 2 - 160) else 0
                val y: Int = if(isFirst) (b.height / 2 - 81) else 0


                if(x<0 || y <0)
                    return

                val resultBitmap = Bitmap.createBitmap(b, x, y, if(isFirst)320 else 390, if(isFirst) 172 else 390)

//                if(isSecondDevice){
//                    Glide.with(this).load(resultBitmap).transform(MultiTransformation(CenterCrop(), CircleCrop())).into(secondCusGifImageView!!)
//                }else{
//                    Glide.with(this).load(resultBitmap).into(secondCusGifImageView!!)
//                }
//
                ImageUtils.saveMyBitmap(resultBitmap, saveCropPath)
//
//                Timber.e("-------裁剪后的图片=" + (File(saveCropPath)).path)
                val url = File(saveCropPath).path
                dialBean.imgUrl = url
                gifStringBuffer.append("----------->>>>裁剪后图片的地址=$url\n")

               val recordBean =  DbManager.getInstance().saveGifHistoryRecord(url,if(isSecondDevice) 2 else 1,1,false,System.currentTimeMillis())
               if(recordBean ==null){
                   return
               }
                gifHistoryList.add(gifHistoryList.size-1,recordBean)
                if(gifHistoryList.size == 21){
                    gifHistoryList.removeAt(20)
                }
                gifHistoryAdapter?.notifyDataSetChanged()

                setDialToDevice(byteArrayOf(0x00))
            }

        }


        if (requestCode == 1001) {
            val bundle = data?.getBundleExtra("gif_bundle")
            if(bundle == null){
                return
            }
            //是否是修改速度
            val isUpdateSpeed = bundle.getBoolean("is_update_speed",false)
            if(isUpdateSpeed){
                val index = bundle.getInt("index",1)
                val speed = bundle.getInt("gif_speed",5)
                val bean = gifHistoryList[index]
                gifViewModel?.updateGifRecord(bean,speed)
                gifHistoryAdapter?.notifyItemChanged(index)

                return
            }

            val url = bundle.getString("url")
            val save_time = bundle.getLong("save_time")

            Timber.e("------url=" + url)
            if (url != null) {
//                if(isSecondDevice){
//                    Glide.with(this).load(url).transform(MultiTransformation(CenterCrop(), CircleCrop())).into(secondCusGifImageView!!)
//                }else{
//                    Glide.with(this).load(url).into(secondCusGifImageView!!)
//                }
                val currentTime = save_time
                val newGifNameFile = url
//                FileUtils.copySdcardFile(url,newGifNameFile)
                val recordBean = DbManager.getInstance().saveGifHistoryRecord(newGifNameFile,if(isSecondDevice) 2 else 1,MmkvUtils.getGifSpeed(),true,currentTime)
              //  gifHistoryList.add(recordBean)

                if(recordBean ==null){
                    return
                }
                gifHistoryList.add(gifHistoryList.size-1,recordBean)
                if(gifHistoryList.size == 21){
                    gifHistoryList.removeAt(20)
                }

                gifHistoryAdapter?.notifyDataSetChanged()

                dealWidthGif(url)
            }

        }
    }


    val dByteStr = StringBuilder()

    val cByteStr = StringBuilder()

    //处理gif的图片
    private fun dealWidthGif(gifPath: String) {
        if (BaseApplication.getBaseApplication().connStatus != ConnStatus.CONNECTED) {
            hideDialog()
            ToastUtils.show(resources.getString(R.string.string_device_not_connect))
            return
        }
        val gifList = ImageUtils.getGifDataBitmap(File(gifPath),isSecondDevice)
        Timber.e("-------gifList=" + gifList.size)
        if (gifList.size == 0) {
            ToastUtils.show(resources.getString(R.string.string_gig_small))
            return
        }
        //将图片转换成byte集合,得到gif D的数据
        dByteStr.delete(0, dByteStr.length)
        cByteStr.delete(0, cByteStr.length)

        // gifLogTv?.text = ""

        var arraySize = 0
        showProgressDialog("Loading...")

        GlobalScope.launch {
            for (i in 0 until gifList.size) {
                val beforeSize = arraySize

                val tempArray = Utils.intToByteArray(beforeSize)
                val tempStr = Utils.getHexString(tempArray)
                cByteStr.append(tempStr)

                val bitmap = gifList[i]
                val bitArray = BitmapAndRgbByteUtil.bitmap2RGBData(bitmap)
                arraySize += bitArray.size
                dByteStr.append(Utils.getHexString(bitArray))


            }

            Timber.e("-----111--c的内容=" + cByteStr)
            //得到D的数组
            val resultDArray = Utils.hexStringToByte(dByteStr.toString())
            //得到C的数组
            val resultCArray = Utils.hexStringToByte(cByteStr.toString())
            //得到B的数组
            val gifSpeed = MmkvUtils.getGifSpeed()

            val isSecond = BaseApplication.getBaseApplication().deviceTypeConst==DeviceTypeConst.DEVICE_SECOND

            val resultBArray = KeyBoardConstant.dealWidthBData(gifList.size, gifSpeed,isSecond)

            val resultAllArray = KeyBoardConstant.getGifAArrayData(
                gifList.size,
                resultBArray,
                resultCArray,
                resultDArray
            )

            // val logStr = KeyBoardConstant.getStringBuffer()

            //Timber.e("-------结果="+resultDArray.size)

            val msg = handlers.obtainMessage()
            msg.what = 0x00
            msg.obj = resultAllArray
            handlers.sendMessageDelayed(msg, 500)

        }

        //得到C的内容
        Timber.e("----222---c的内容=" + cByteStr)

    }


    var grbByte = byteArrayOf()

    private fun setDialToDevice(byteArray: ByteArray) {
        if (BaseApplication.getBaseApplication().connStatus == ConnStatus.NOT_CONNECTED) {
            ToastUtils.show(resources.getString(R.string.string_device_not_connect))
            hideDialog()
            return
        }

        val isSynGif = byteArray.isNotEmpty() && byteArray.size > 10

        showProgressDialog(resources.getString(R.string.string_sync_ing))
        BaseApplication.getBaseApplication().connStatus = ConnStatus.IS_SYNC_DIAL
        //stringBuilder.delete(0,stringBuilder.length)
        //showLogTv()
        handlers.sendEmptyMessageDelayed(-1,5000)
        if (isSynGif) {
            startDialToDevice(byteArray, true)
            return
        }

        ThreadUtils.submit {
            val bitmap = Glide.with(this)
                .asBitmap()
                .load(dialBean.imgUrl)
                .into(
                    Target.SIZE_ORIGINAL,
                    Target.SIZE_ORIGINAL
                ).get()

            val tempBitmap = BitmapAndRgbByteUtil.compressImage(bitmap)
            Timber.e("--------bitmap大小=" + tempBitmap.byteCount + " " + bitmap.byteCount)
            val tempArray = BitmapAndRgbByteUtil.bitmap2RGBData(tempBitmap)
            gifStringBuffer.append("----------->>>>裁剪后图片大小 size=${tempArray.size}"+"\n")
            val msg = handlers.obtainMessage()
            msg.what = 0x01
            msg.obj = tempArray
            handlers.sendMessageDelayed(msg, 100)
            Timber.e("------大小=" + grbByte.size)
            //   ImgUtil.loadMeImgDialCircle(imgRecall, bitmap)
        }

    }


    private var progressDialog: ShowProgressDialog? = null

    //显示弹窗
    private fun showProgressDialog(msg: String) {
        Timber.e("--------Dialog="+(isFinishing))
        if (progressDialog == null) {
            progressDialog = ShowProgressDialog(this, com.bonlala.base.R.style.BaseDialogTheme)
        }
        if (progressDialog?.isShowing == false) {
            if(isFinishing){
                return
            }
            progressDialog?.show()
        }
        progressDialog?.setCancelable(false)
        progressDialog?.setShowMsg(msg)
    }


    //隐藏弹窗
    private fun cancelProgressDialog() {
        if (progressDialog != null && !isFinishing) {
            progressDialog?.dismiss()
        }
    }


    private fun startDialToDevice(imgByteArray: ByteArray, isGIf: Boolean) {
        handlers.removeMessages(-1)
        showProgressDialog("Loading...")
        grbByte = imgByteArray
        Timber.e("--------大小=" + grbByte.size)
        val uiFeature = 65533 //if(BaseApplication.getBaseApplication().deviceTypeConst==DeviceTypeConst.DEVICE_FIRST) 65533 else 65534
        dialBean.uiFeature = uiFeature.toLong()
        dialBean.binSize = grbByte.size.toLong()
        dialBean.name = "12"
        dialBean.type = if (isGIf) 2 else 1

        val resultArray = KeyBoardConstant.getDialByte(dialBean)
        val str = Utils.formatBtArrayToString(resultArray)
        //stringBuilder.append("send 3.11.3 protocol:$str" + "\n" + "fileSize=" + grbByte.size)
        Timber.e("-------表盘指令=" + str)
        //showLogTv()

        gifStringBuffer.append("----------->>>>进入表盘模式=$str"+"\n")
        BaseApplication.getBaseApplication().bleOperate.startFirstDial(
            resultArray
        ) { data -> //880000000000030f0904 02
            /**
             * 0x01：传入非法值。例如 0x00000000
            0x02：等待 APP 端发送表盘 FLASH 数据
            0x03：设备已经有存储这个表盘，设备端调用并显示
            0x04：设备存储空间不够，需要 APP 端调用 3.11.5 处理
            0x05：其他高优先级数据在处理
             */
            /**
             * 0x01：传入非法值。例如 0x00000000
            0x02：等待 APP 端发送表盘 FLASH 数据
            0x03：设备已经有存储这个表盘，设备端调用并显示
            0x04：设备存储空间不够，需要 APP 端调用 3.11.5 处理
            0x05：其他高优先级数据在处理
             */

//            stringBuilder.append("设备端返回指定非固化表盘概要信息状态指令: " + Utils.formatBtArrayToString(data) + "\n")
//            showLogTv()

            if (data.size == 11 && data[8].toInt() == 9 && data[9].toInt() == 4) {
                gifStringBuffer.append("----------->>>>进入表盘模式指令返回=${Utils.formatBtArrayToString(data)}"+"\n")
                val codeStatus = data[10].toInt()
                if (codeStatus == 1) {
                    cancelProgressDialog()
                    ToastUtils.show(resources.getString(R.string.string_invalid_value))
                    return@startFirstDial
                }
                //设备存储空间不够
                if (codeStatus == 4) {
                    BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED

                }

                if (codeStatus == 5) {
                    cancelProgressDialog()
                    ToastUtils.show(resources.getString(R.string.string_device_busy))
                    BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
                    return@startFirstDial
                }

                val array = KeyBoardConstant.getDialStartArray()
                // stringBuilder.append("3.10.3 APP 端设擦写设备端指定的 FLASH 数据块" + Utils.formatBtArrayToString(array)+"\n")
                // showLogTv()


                gifStringBuffer.append("----------->>>>开始擦写设备端制定的flash数据块 = ${Utils.formatBtArrayToString(array)}"+"\n")
                BaseApplication.getBaseApplication().bleOperate.setIndexDialFlash(array) { data ->
                    val st = Utils.formatBtArrayToString(data)
                    Timber.e("-----大塔=" + st)
                    gifStringBuffer.append("----------->>>>开始擦写设备端制定的flash数据块 返回 = $st\n")
                    //880000000000030f090402
                    //88 00 00 00 00 00 03 0e 08 04 02
                    if (data.size == 11 && data[0].toInt() == -120 && data[8].toInt() == 8 && data[9].toInt() == 4 && data[10].toInt() == 2) {

                        /**
                         * 0x01：不支持擦写 FLASH 数据
                         * 0x02：已擦写相应的 FLASH 数据块
                         */

                        //880000000000030e 08 04 02
                        /**
                         * 0x01：不支持擦写 FLASH 数据
                         * 0x02：已擦写相应的 FLASH 数据块
                         */
                        // stringBuilder.append("3.10.4 设备端返回已擦写 FLASH 数据块的状态" + Utils.formatBtArrayToString(data)+"\n")

                        // stringBuilder.append("开始发送flash数据" +"\n")
                        // showLogTv()

                        count = 5
                        //获取下装填，状态是3就继续进行
                        getDeviceStatus()

                    }

                }
            }
        }
    }

    //次数
    var count = 5
    private fun getDeviceStatus() {
        gifStringBuffer.append("----------->>>>获取设备当前状态 \n")
        BaseApplication.getBaseApplication().bleOperate.setClearListener()
        BaseApplication.getBaseApplication().bleOperate.getKeyBoardStatus(object :
            OnCommBackDataListener {
            override fun onIntDataBack(value: IntArray?) {
                val code = value?.get(0)
                Timber.e("-------code=$code" + " " + count)
                if (code == 3) {
                    count = 5
                    toStartWriteDialFlash()
                } else {
                    if (count in 1..6) {
                        handlers.postDelayed(Runnable {
                            count--
                            getDeviceStatus()
                        }, 100)
                    } else {
                        cancelProgressDialog()
                        ToastUtils.show("设备正忙!")
                        gifStringBuffer.append("----------->>>>设备正忙 \n")
                        count = 5
                    }
                }
            }

            override fun onStrDataBack(vararg value: String?) {

            }

        })
    }


    private fun toStartWriteDialFlash() {

        val start = Utils.toByteArrayLength(16777215, 4)
        val end = Utils.toByteArrayLength(16777215, 4)

        val startByte = byteArrayOf(
            0x00, 0xff.toByte(), 0xff.toByte(),
            0xff.toByte()
        )
        gifStringBuffer.append("----------->>>>开始写入flash数据块 \n")

        val resultArray = ImgUtil.getDialContent(startByte, startByte, grbByte, 1000 + 701, -100, 0)
        Timber.e("-------reaulstArray=" + resultArray.size + " " + resultArray[0].size)

        //计算总的包数
        var allPackSize = resultArray.size
        Timber.e("------总的包数=" + allPackSize)
        //记录发送的包数
        var sendPackSize = 0

        gifStringBuffer.append("----------->>>>计算总的包数=$allPackSize\n")

        BaseApplication.getBaseApplication().bleOperate.writeDialFlash(
            resultArray
        ) { statusCode ->
            sendPackSize++

            val conn = BaseApplication.getBaseApplication().connStatus

            //计算百分比
            var percentValue =
                CalculateUtils.div(sendPackSize.toDouble(), allPackSize.toDouble(), 2)
            val showPercent = CalculateUtils.mul(percentValue, 100.0).toInt()
            //gifLogTv?.text = sendPackSize.toString()+"/"+allPackSize+" "+showPercent
            showProgressDialog(resources.getString(R.string.string_sync_ing) + (if (showPercent >= 100) 100 else showPercent) + "%")

            gifStringBuffer.append("----------->>>>写入百分比=$showPercent 状态=$statusCode 连接状态=$conn\n")
           // gifStringBuffer.append("----------->>>>percentValue 状态=$statusCode\n")

            /**
             * 0x01：更新失败
             * 0x02：更新成功
             * 0x03：第 1 个 4K 数据块异常（含 APP 端发擦写和实际写入的数据地址不一致），APP 需要重走流程
             * 0x04：非第 1 个 4K 数据块异常，需要重新发送当前 4K 数据块
             * 0x05：4K 数据块正常，发送下一个 4K 数据
             * 0x06：异常退出（含超时，或若干次 4K 数据错误，设备端处理）
             */

            /**
             * 0x01：更新失败
             * 0x02：更新成功
             * 0x03：第 1 个 4K 数据块异常（含 APP 端发擦写和实际写入的数据地址不一致），APP 需要重走流程
             * 0x04：非第 1 个 4K 数据块异常，需要重新发送当前 4K 数据块
             * 0x05：4K 数据块正常，发送下一个 4K 数据
             * 0x06：异常退出（含超时，或若干次 4K 数据错误，设备端处理）
             */
            if (statusCode == 1) {
                cancelProgressDialog()
                ToastUtils.show(resources.getString(R.string.string_update_failed))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
            }
            if (statusCode == 2) {
                cancelProgressDialog()
                ToastUtils.show(resources.getString(R.string.string_update_success))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
            }
            if (statusCode == 6) {
                cancelProgressDialog()
                ToastUtils.show(resources.getString(R.string.string_error_exit))
                BaseApplication.getBaseApplication().connStatus = ConnStatus.CONNECTED
            }
        }
    }


}