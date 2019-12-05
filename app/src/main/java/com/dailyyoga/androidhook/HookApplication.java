package com.dailyyoga.androidhook;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

import java.lang.reflect.Field;

/**
 * @author: ZhaoJiaXing@gmail.com
 * @created on: 2019/12/5 11:10
 * @description:
 */
public class HookApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        hookActivityThreadInstrumentation();
    }

    private void hookActivityThreadInstrumentation() {
        try {
            // 获取ActivityThread类实例
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            // 获取变量 sCurrentActivityThread 设置可访问
            Field activityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            activityThreadField.setAccessible(true);
            //获取ActivityThread对象sCurrentActivityThread
            Object activityThread;
            activityThread = activityThreadField.get(null);
            // 获取变量 mInstrumentation 设置可访问
            Field instrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            instrumentationField.setAccessible(true);
            //从sCurrentActivityThread中获取成员变量mInstrumentation
            Instrumentation instrumentation = (Instrumentation) instrumentationField.get(activityThread);
            //创建代理对象InstrumentationProxy
            InstrumentationProxy proxy = new InstrumentationProxy(instrumentation, getPackageManager());
            //将sCurrentActivityThread中成员变量mInstrumentation替换成代理类InstrumentationProxy
            instrumentationField.set(activityThread, proxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {

        }
    }
}
