package com.innerac.memorykiller.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.innerac.memorykiller.objs.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by InnerAce on 2015/2/12.
 * 提供手机里面的进程信息
 */
public class TaskInfoProvider {
    public static List<TaskInfo> getTaskInfos(Context context) throws PackageManager.NameNotFoundException {

        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();

        PackageManager pm = context.getPackageManager();

        ActivityManager am = (ActivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos){

            TaskInfo taskInfo = new TaskInfo();

            Debug.MemoryInfo[] meminfos = am.getProcessMemoryInfo(new int[]{processInfo.pid});

            String packname = processInfo.processName;
            ApplicationInfo appinfo = pm.getApplicationInfo(packname, 0);
            Drawable icon = appinfo.loadIcon(pm);
            String name = appinfo.loadLabel(pm).toString();
            long memsize = meminfos[0].getTotalPrivateDirty()*1024;
            boolean isuser = true;
            if(appinfo.flags == ApplicationInfo.FLAG_SYSTEM){
                isuser = false;
            }

            taskInfo.setPackname(packname);
            taskInfo.setIcon(icon);
            taskInfo.setName(name);
            taskInfo.setMemsize(memsize);
            taskInfo.setUserTask(isuser);
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
