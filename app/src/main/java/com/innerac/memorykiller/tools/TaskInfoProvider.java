package com.innerac.memorykiller.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;

import com.innerac.memorykiller.R;
import com.innerac.memorykiller.objs.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 提供手机里面的进程信息
 */
public class TaskInfoProvider {

    /*
    * 返回当前运行中的进程信息列表
    * */
    public static List<TaskInfo> getTaskInfos(Context context) {

        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();

        PackageManager pm = context.getPackageManager();

        //ActivityManager am = (ActivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos){

            TaskInfo taskInfo = new TaskInfo();

            Debug.MemoryInfo[] meminfos = am.getProcessMemoryInfo(new int[]{processInfo.pid});

            String packname = processInfo.processName;
            taskInfo.setPackname(packname);
            long memsize = meminfos[0].getTotalPrivateDirty()*1024;
            taskInfo.setMemsize(memsize);


            try {
                ApplicationInfo appinfo = pm.getApplicationInfo(packname,0);
                Drawable icon = appinfo.loadIcon(pm);
                String name = appinfo.loadLabel(pm).toString();
//                boolean isuser = false;
//                if((appinfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
//                    isuser = true;
//                }
               // boolean isuser = appinfo.flags != ApplicationInfo.FLAG_SYSTEM;
                boolean isuser = true;
                if((appinfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                    isuser = false;
                }

                taskInfo.setIcon(icon);
                taskInfo.setName(name);
                taskInfo.setUserTask(isuser);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
                taskInfo.setName(packname);
            }



            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
