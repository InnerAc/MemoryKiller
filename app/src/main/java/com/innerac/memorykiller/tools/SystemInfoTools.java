package com.innerac.memorykiller.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by InnerAce on 2015/2/12.
 */
public class SystemInfoTools {

    public static int getRunningProcessCount(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        return info.size();
    }
    /*
    获取剩余内存
     */
    public static long getAvailMem(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }
    /*
    获取总内存
     */
    public static long getTotalMem(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.totalMem;
    }
}
