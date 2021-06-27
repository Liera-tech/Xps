package com.liera.xps.apsaspect.log;

import android.text.TextUtils;
import android.util.Log;
import com.liera.xps.utils.XpsUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
public class XpsLogAspect {

    @Pointcut("execution(@XpsLog * * (..))")
    private void getXpsLog() {
    }

    @Around("getXpsLog()")
    public void getXpsLogJoinPoint(ProceedingJoinPoint joinPoint) {
        //判断系统是否需要打印日志
        if (!XpsUtil.isDebug()) {
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return;
        }

        Object[] args = joinPoint.getArgs();
        if (args == null) {
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        //参数 和参数的注解数组(一个参数可能有多个注解)
        Annotation[][] parameterAnnotations = XpsUtil.getParameterAnnotations(joinPoint, method);
        if (parameterAnnotations == null) return;

        String level = null;
        String tag = null;

        //方法上的注解
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            //如果存在注解在方法上,先使用方法上的
            if (annotation instanceof XpsLogLevel) {
                level = ((XpsLogLevel) annotation).level();
            } else if (annotation instanceof XpsLogTag) {
                tag = ((XpsLogTag) annotation).tag();
            }
        }

        //内容
        Object content = null;

        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation1 = parameterAnnotations[i];

            for (Annotation annotation : parameterAnnotation1) {
                if (annotation instanceof XpsLogLevel) {
                    String arg = (String) args[i];
                    if (arg != null)
                        level = arg;
                    else
                        level = ((XpsLogLevel) annotation).level();
                } else if (annotation instanceof XpsLogTag) {
                    String arg = (String) args[i];
                    if (arg != null)
                        tag = arg;
                    else
                        tag = ((XpsLogTag) annotation).tag();
                } else if (annotation instanceof XpsLogContent) {
                    content = args[i];
                }
            }
        }

        if (content == null || TextUtils.isEmpty(content.toString())) {
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return;
        }

        if (level == null) {
            level = XpsUtil.TAGI;
        }
        level = level.toUpperCase();

        if (TextUtils.isEmpty(tag)) {
            tag = joinPoint.getThis().getClass().getName();
        }

        switch (level) {
            case XpsUtil.TAGV:
                if (XpsUtil.isExTagV()) break;
                Log.v(tag, content.toString());
                break;
            case XpsUtil.TAGD:
                if (XpsUtil.isExTagD()) break;
                ;
                Log.d(tag, content.toString());
                break;
            case XpsUtil.TAGE:
                if (XpsUtil.isExTagE()) break;
                ;
                Log.e(tag, content.toString());
                break;
            case XpsUtil.TAGW:
                if (XpsUtil.isExTagW()) break;
                ;
                Log.w(tag, content.toString());
                break;
            case XpsUtil.TAGI:
                if (XpsUtil.isExTagI()) break;
                Log.i(tag, content.toString());
                break;
            default:
                break;
        }

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Pointcut("execution(@XpsLogI * * (..))")
    private void getXpsLogI() {
    }

    @Around("getXpsLogI()")
    public void getXpsLogIJoinPoint(ProceedingJoinPoint joinPoint) {
        Pps pps = new Pps(joinPoint).invoke();
        if (pps.is()) return;

        if (!XpsUtil.isExTagI())
            Log.e(pps.getTag(), pps.getContent().toString());

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Pointcut("execution(@XpsLogW * * (..))")
    private void getXpsLogW() {
    }

    @Around("getXpsLogW()")
    public void getXpsLogWJoinPoint(ProceedingJoinPoint joinPoint) {
        Pps pps = new Pps(joinPoint).invoke();
        if (pps.is()) return;
        if (!XpsUtil.isExTagW())
            Log.w(pps.getTag(), pps.getContent().toString());

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Pointcut("execution(@XpsLogV * * (..))")
    private void getXpsLogV() {
    }

    @Around("getXpsLogV()")
    public void getXpsLogVJoinPoint(ProceedingJoinPoint joinPoint) {
        Pps pps = new Pps(joinPoint).invoke();
        if (pps.is()) return;
        if (!XpsUtil.isExTagV())
            Log.v(pps.getTag(), pps.getContent().toString());

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Pointcut("execution(@XpsLogD * * (..))")
    private void getXpsLogD() {
    }

    @Around("getXpsLogD()")
    public void getXpsLogDJoinPoint(ProceedingJoinPoint joinPoint) {
        Pps pps = new Pps(joinPoint).invoke();
        if (pps.is()) return;
        if (!XpsUtil.isExTagD())
            Log.d(pps.getTag(), pps.getContent().toString());

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Pointcut("execution(@XpsLogE * * (..))")
    private void getXpsLogE() {
    }

    @Around("getXpsLogE()")
    public void getXpsLogEJoinPoint(ProceedingJoinPoint joinPoint) {
        Pps pps = new Pps(joinPoint).invoke();
        if (pps.is()) return;
        if (!XpsUtil.isExTagE())
            Log.e(pps.getTag(), pps.getContent().toString());

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static class Pps {
        private boolean myResult;
        private ProceedingJoinPoint joinPoint;

        private String tag;
        private Object content;

        public Pps(ProceedingJoinPoint joinPoint) {
            this.joinPoint = joinPoint;
        }

        boolean is() {
            return myResult;
        }

        public String getTag() {
            return tag;
        }

        public Object getContent() {
            return content;
        }

        public Pps invoke() {
            if (!XpsUtil.isDebug()) {
                try {
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                myResult = true;
                return this;
            }
            Object[] args = joinPoint.getArgs();
            if (args == null) {
                try {
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                myResult = true;
                return this;
            }

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Annotation[][] parameterAnnotations = XpsUtil.getParameterAnnotations(joinPoint, method);
            if (parameterAnnotations == null) {
                myResult = true;
                return this;
            }

            //方法上的注解
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                //如果存在注解在方法上,先使用方法上的
                if (annotation instanceof XpsLogTag) {
                    tag = ((XpsLogTag) annotation).tag();
                }
            }

            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] parameterAnnotation1 = parameterAnnotations[i];

                for (Annotation annotation : parameterAnnotation1) {
                    if (annotation instanceof XpsLogTag) {
                        String arg = (String) args[i];
                        if (arg != null)
                            tag = arg;
                        else
                            tag = ((XpsLogTag) annotation).tag();
                    } else if (annotation instanceof XpsLogContent) {
                        content = args[i];
                    }
                }
            }

            if (content == null || TextUtils.isEmpty(content.toString())) {
                try {
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                myResult = true;
                return this;
            }

            if (TextUtils.isEmpty(tag)) {
                tag = joinPoint.getThis().getClass().getName();
            }
            myResult = false;
            return this;
        }
    }
}
