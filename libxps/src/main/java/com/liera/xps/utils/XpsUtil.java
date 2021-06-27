package com.liera.xps.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.liera.xps.apsaspect.permission.XpsPermissionBeforeEvent;
import com.liera.xps.apsaspect.permission.XpsPermissionCancel;
import com.liera.xps.apsaspect.permission.XpsPermissionDenied;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class XpsUtil {

    public static final String TAGI = "I";
    public static final String TAGD = "D";
    public static final String TAGE = "E";
    public static final String TAGV = "V";
    public static final String TAGW = "W";

    private static boolean debug = false;
    private static boolean exTagI = false;
    private static boolean exTagD = false;
    private static boolean exTagE = false;
    private static boolean exTagW = false;
    private static boolean exTagV = false;

    public static void setDebug(boolean isDebug) {
        if (!(debug = isDebug))
            excludeDebug();
    }

    public static void excludeDebug(String... tag) {
        if (!debug || tag == null || tag.length == 0) {
            exTagI = false;
            exTagD = false;
            exTagE = false;
            exTagW = false;
            exTagV = false;
            return;
        }
        for (String exTag : tag) {
            switch (exTag) {
                case TAGI:
                    exTagI = true;
                    break;
                case TAGD:
                    exTagD = true;
                    break;
                case TAGE:
                    exTagE = true;
                    break;
                case TAGV:
                    exTagV = true;
                    break;
                case TAGW:
                    exTagW = true;
                    break;
            }
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static boolean isExTagI() {
        return exTagI;
    }

    public static boolean isExTagD() {
        return exTagD;
    }

    public static boolean isExTagE() {
        return exTagE;
    }

    public static boolean isExTagW() {
        return exTagW;
    }

    public static boolean isExTagV() {
        return exTagV;
    }

    /**
     * 检查权限
     *
     * @param context
     * @param requestPermissions
     * @return
     */
    public static boolean checkPermissions(Context context, String[] requestPermissions) {
        for (String requestPermission : requestPermissions) {
            if (ContextCompat.checkSelfPermission(context, requestPermission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    /**
     * 检查权限申请结果
     *
     * @param grantResults
     */
    public static boolean checkPermissionResult(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != 0)
                return false;
        }
        return true;
    }

    /**
     * callback被注解的方法
     *
     * @param obj
     * @param annotationClass
     */
    public static void invokeAnnotation(Object obj, Class<? extends Annotation> annotationClass, int requestCode) {
        Class<?> aClass = obj.getClass();

        Method[] declaredMethods = aClass.getDeclaredMethods();

        for (Method declaredMethod : declaredMethods) {
            declaredMethod.setAccessible(true);

            if (declaredMethod.isAnnotationPresent(annotationClass)) {
                Annotation annotation = declaredMethod.getAnnotation(annotationClass);

                int[] codes = null;

                if (annotation instanceof XpsPermissionCancel) {
                    codes = ((XpsPermissionCancel) annotation).requestCodes();
                } else if (annotation instanceof XpsPermissionDenied) {
                    codes = ((XpsPermissionDenied) annotation).requestCodes();
                } else if (annotation instanceof XpsPermissionBeforeEvent) {
                    codes = ((XpsPermissionBeforeEvent) annotation).requestCodes();
                }

                if (codes == null) continue;

                boolean isExit = false;
                for (int code : codes) {
                    if (code != requestCode) continue;
                    isExit = true;
                    break;
                }
                if (!isExit) continue;

                try {
                    declaredMethod.invoke(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Annotation[][] getParameterAnnotations(ProceedingJoinPoint joinPoint, Method method) {
        //参数 和参数的注解数组(一个参数可能有多个注解)
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return null;
        }
        return parameterAnnotations;
    }
}
