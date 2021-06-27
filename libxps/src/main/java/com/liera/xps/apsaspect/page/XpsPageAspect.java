package com.liera.xps.apsaspect.page;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class XpsPageAspect {

    @Pointcut("execution(@XpsPageToAppSetting * * (..))")
    private void getXpsPageToAppSetting() {}

    @Around("getXpsPageToAppSetting()")
    public void getXpsPageToAppSettingJoinPoint(ProceedingJoinPoint joinPoint) throws Exception {
        final Object aThis = joinPoint.getThis();
        final Context context;
        if (aThis instanceof Context) {
            context = (Context) aThis;
        } else if (aThis instanceof Fragment) {
            context = ((Fragment)aThis).getActivity();
        } else
            throw new Exception("注解@XpsPageToAppSetting不在Activity或Fragment中,请确认代码设计是否规范,error:" + aThis.getClass().getSimpleName());

        Uri parse = Uri.parse("package:" + context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, parse);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
