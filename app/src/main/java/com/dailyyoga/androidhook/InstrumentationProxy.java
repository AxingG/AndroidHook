package com.dailyyoga.androidhook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author: ZhaoJiaXing@gmail.com
 * @created on: 2019/12/5 10:53
 * @description:
 */
public class InstrumentationProxy extends Instrumentation {

    private Instrumentation mInstrumentation;
    private PackageManager mPackageManager;

    public static final String REQUEST_TARGET_INTENT_NAME = "request_target_intent_name";

    public InstrumentationProxy(Instrumentation instrumentation, PackageManager packageManager) {
        this.mInstrumentation = instrumentation;
        this.mPackageManager = packageManager;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode, Bundle options) {
        List<ResolveInfo> resolveInfo = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);

        // 判断启动的Activity是否在AndroidManifest.xml中注册过
        if (resolveInfo.isEmpty()) {
            // 没有注册，替换为一个已经注册的activity
            // 保存目标插件
            String targetActivityName = intent.getComponent().getClassName();

            intent.putExtra(REQUEST_TARGET_INTENT_NAME, targetActivityName);
            //设置为占坑Activity
            intent.setClassName(who, "com.dailyyoga.androidhook.StubActivity");
        }
        //
        try {
            Method execStartActivity = Instrumentation.class.getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class,
                    Intent.class, int.class, Bundle.class);
            return (ActivityResult) execStartActivity.invoke(mInstrumentation, who, contextThread, token, target, intent, requestCode, options);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        // 启动activity时，获取之前传入的要替换的className
        String intentName = intent.getStringExtra(REQUEST_TARGET_INTENT_NAME);
        if (!TextUtils.isEmpty(intentName)) {
            return super.newActivity(cl, intentName, intent);
        }
        return super.newActivity(cl, className, intent);
    }
}
