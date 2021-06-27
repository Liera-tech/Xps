package com.liera.xps.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.os.Bundle;
import android.view.View;
import com.liera.xps.apsaspect.log.XpsLogContent;
import com.liera.xps.apsaspect.log.XpsLogE;
import com.liera.xps.apsaspect.page.XpsPageToAppSetting;
import com.liera.xps.apsaspect.permission.XpsPermissionBeforeEvent;
import com.liera.xps.apsaspect.permission.XpsPermissionCancel;
import com.liera.xps.apsaspect.permission.XpsPermissionDenied;
import com.liera.xps.apsaspect.permission.XpsPermissionRequest;
import com.liera.xps.utils.XpsUtil;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_CODE = 101;
    private static final int REQUEST_STORAGE_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置允许debug日志
        XpsUtil.setDebug(true);
        //不打印Log.w的日志
        XpsUtil.excludeDebug(XpsUtil.TAGW);
    }

    //点击按钮触发事件
    public void buttonClick(View view) {
        openCamera();
    }

    //单个获取权限
    @XpsPermissionRequest(permissions = Manifest.permission.CAMERA, requestCode = REQUEST_CAMERA_CODE)
    public void openCamera(){
        contentE("权限已拿到，随我搞事情");
    }

    @XpsPermissionCancel(requestCodes = REQUEST_CAMERA_CODE)
    public void cancelCamera(){
        contentE("权限被那娘们给取消了");
    }

    @XpsPermissionDenied(requestCodes = REQUEST_CAMERA_CODE)
    public void deniedCamera(){
        contentE("权限被拒绝且勾选了不再访问");
    }



    //批量获取权限
    @XpsPermissionRequest(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.BLUETOOTH_ADMIN}, requestCode = REQUEST_STORAGE_CODE)
    public void openSDCard(){
        contentE("SD卡和蓝牙权限已拿到，随我搞事情");
    }

    @XpsPermissionCancel(requestCodes = REQUEST_STORAGE_CODE)
    public void cancelStorage(){
        contentE("存储和蓝牙权限被那娘们给取消了");
    }

    @XpsPermissionDenied(requestCodes = REQUEST_STORAGE_CODE)
    public void deniedStorage(){
        contentE("存储和蓝牙权限被拒绝且勾选了不再访问");
    }


    //申请权限前可在这个注解的函数内操作
    @XpsPermissionBeforeEvent( requestCodes = {REQUEST_CAMERA_CODE, REQUEST_STORAGE_CODE})
    public void beforeEvent(){
        contentE("在需要申请权限前搞点事情");
    }

    //跳到系统app权限申请页面注解
    @XpsPageToAppSetting
    private void toSettingApp(){}

    //打印日志的函数
    @XpsLogE
    public void contentE(@XpsLogContent String content){

    }
}
