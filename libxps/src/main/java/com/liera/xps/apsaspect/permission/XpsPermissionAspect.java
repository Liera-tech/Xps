package com.liera.xps.apsaspect.permission;

import android.content.Context;
import android.os.Build;
import androidx.fragment.app.Fragment;
import com.liera.xps.activitys.XpsPermissionRequestActivity;
import com.liera.xps.interfaces.XpsCallback;
import com.liera.xps.utils.XpsUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class XpsPermissionAspect {

    @Pointcut("execution(@XpsPermissionRequest * * (..)) && @annotation(permissions)")
    private void getXpsPermission(XpsPermissionRequest permissions) {}

    @Around("getXpsPermission(permissions)")
    public void getXpsPermissionJoinPoint(final ProceedingJoinPoint joinPoint, XpsPermissionRequest permissions) throws Exception {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return;
        }

        final Object aThis = joinPoint.getThis();
        final Context context;
        if (aThis instanceof Context) {
            context = (Context) aThis;
        } else if (aThis instanceof Fragment) {
            context = ((Fragment)aThis).getActivity();
        } else
            throw new Exception("注解@XpsRequest不在Activity或Fragment中,请确认代码设计是否规范,error:" + aThis.getClass().getSimpleName());

        //判断申请了哪些权限
        String[] values = permissions.permissions();
        if (values.length == 0)
            throw new Exception("注解@XpsRequest中无可申请的权限,error:" + aThis.getClass().getSimpleName());

        final int requestCode = permissions.requestCode();

        //权限申请前需要处理的事务
        XpsUtil.invokeAnnotation(aThis, XpsPermissionBeforeEvent.class, requestCode);

        XpsPermissionRequestActivity.launchActivity(context, values, requestCode, new XpsCallback() {
            @Override
            public void permissionXSuccess() {
                try {
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void permissionXCancel(String[] cancelPermissions) {
                XpsUtil.invokeAnnotation(aThis, XpsPermissionCancel.class, requestCode);
            }

            @Override
            public void permissionXDenied(String[] cancelPermissions, String[] deniedPermissions) {
                XpsUtil.invokeAnnotation(aThis, XpsPermissionDenied.class, requestCode);
            }
        });
    }
}
