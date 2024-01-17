package com.app.smartkeyboard.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.hjq.toast.ToastUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import timber.log.Timber;

/**
 * Created by Admin
 * Date 2019/9/18
 */
public class GetJsonDataUtil {

    public String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public  void byteToFile(byte[] bytes, String path)
    {
        try
        {
            // 根据绝对路径初始化文件
            File localFile = new File(path);
            if (!localFile.exists())
            {
                localFile.createNewFile();
            }
            // 输出流
            OutputStream os = new FileOutputStream(localFile);
            os.write(bytes);
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TestFile", "Error on write File:" + e);
        }
    }


    private File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
        }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }


    /**
     * 调用系统应用打开文件（系统分享）
     *
     * @param context
     * @param filePath 文件路径
     */
    public void openFileThirdApp(Context context, String filePath) {
        Timber.e("--------分享的路径="+filePath);
        if (TextUtils.isEmpty(filePath)) {
           ToastUtils.show("文件不存在!");
            return;
        }
        File file = new File(filePath);
        checkFileUriExposure();
        Uri uri;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          uri =  FileProvider.getUriForFile(context, context.getPackageName()+".provider",new  File(filePath));
        } else {
          uri =  Uri.fromFile(new File(filePath));
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("text/plain");   //分享文件类型
        context.startActivity(Intent.createChooser(intent, "分享"));
    }

    /**
     * 分享前必须执行本代码，主要用于兼容SDK18以上的系统
     * 否则会报android.os.FileUriExposedException: file:///xxx.pdf exposed beyond app through ClipData.Item.getUri()
     */
    private void checkFileUriExposure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
    }

}
