package com.innerac.memorykiller.tools;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 获取系统信息
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
//4.0API以上
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(outInfo);
//        return outInfo.totalMem;
//4.0API以下
        try{
            File file = new File("/proc/meminfo");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c : line.toCharArray()) {
                if(c >= '0' && c <= '9'){
                    sb.append(c);
                }
            }
            return Integer.parseInt(sb.toString())*1024l;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
