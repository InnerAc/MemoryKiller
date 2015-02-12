package com.innerac.memorykiller.objs;

import android.graphics.drawable.Drawable;

/**
 * Created by InnerAce on 2015/2/12.
 * 进程信息对象
 */
public class TaskInfo {
    private boolean checked;
    private Drawable icon;
    private String name;
    private String packname;
    private long memsize;
    private boolean isUser;

    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getMemsize() {
        return memsize;
    }
    public void setMemsize(long memsize) {
        this.memsize = memsize;
    }
    public boolean isUserTask() {
        return isUser;
    }
    public void setUserTask(boolean isUser) {
        this.isUser = isUser;
    }
    public String getPackname() {
        return packname;
    }
    public void setPackname(String packname) {
        this.packname = packname;
    }
    public String toString() {
        return "TaskInfo [checked=" + checked + ", name=" + name + ", memsize="
                + memsize + ", userTask=" + isUser + ", packname=" + packname
                + "]";
    }

}
