package com.innerac.memorykiller;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.innerac.memorykiller.objs.TaskInfo;
import com.innerac.memorykiller.tools.SystemInfoTools;
import com.innerac.memorykiller.tools.TaskInfoProvider;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Activity {

    private TextView running_process_count;
    private TextView leave_mem_info;
    private LinearLayout ll_loading;
    private ListView lv_task_manager;
    private List<TaskInfo> allTaskInfos;
    private List<TaskInfo> useTaskInfos;
    private List<TaskInfo> sysTaskInfos;
    private TextView tv_status;
    private TaskManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    private void init(){
        running_process_count = (TextView) findViewById(R.id.running_process_count);
        leave_mem_info = (TextView) findViewById(R.id.leave_mem_info);
        tv_status = (TextView) findViewById(R.id.tv_status);

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_task_manager = (ListView) findViewById(R.id.lv_taskmanager);

        fillDate();

        lv_task_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(useTaskInfos!=null&&sysTaskInfos!=null){
                    if(firstVisibleItem > useTaskInfos.size()){
                        tv_status.setText("系统进程: "+sysTaskInfos.size()+"个");
                    }else{
                        tv_status.setText("用户进程: "+useTaskInfos.size()+"个");
                    }
                }
            }
        });
        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo;
                if(position == 0){
                    return;
                }else if(position == (useTaskInfos.size()+1)){
                    return;
                }else if(position <= useTaskInfos.size()){
                    taskInfo = useTaskInfos.get(position-1);
                }else{
                    taskInfo = sysTaskInfos.get(position-2-useTaskInfos.size());
                }
                ViewHolder holder = (ViewHolder) view.getTag();

                if(taskInfo.isChecked()){
                    taskInfo.setChecked(false);
                    holder.cb_status.setChecked(false);
                }else{
                    taskInfo.setChecked(true);
                    holder.cb_status.setChecked(true);
                }
            }
        });

    }



    /*
    填充数据
     */
    private void fillDate() {

        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){

                allTaskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
                useTaskInfos = new ArrayList<TaskInfo>();
                sysTaskInfos = new ArrayList<TaskInfo>();
                for(TaskInfo info:allTaskInfos){
                    if(info.isUserTask()){
                        useTaskInfos.add(info);
                    }else{
                        sysTaskInfos.add(info);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        if(adapter == null){
                            adapter = new TaskManagerAdapter();
                            lv_task_manager.setAdapter(adapter);
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();
        setTitle();

    }

    private class TaskManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {

            return allTaskInfos.size()+2;
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskInfo tmpTaskInfo;
            if(position == 0){
                //用户进程标签
                TextView user_title = new TextView(getApplicationContext());
                user_title.setText("用户进程"+useTaskInfos.size()+"个");
                user_title.setBackgroundColor(Color.GRAY);
                user_title.setTextColor(Color.WHITE);
                return user_title;
            }else if(position == useTaskInfos.size()+1){
                TextView sys_title = new TextView(getApplicationContext());
                sys_title.setText("系统进程"+sysTaskInfos.size()+"个");
                sys_title.setBackgroundColor(Color.GRAY);
                sys_title.setTextColor(Color.WHITE);
                return sys_title;
            }else if(position <= useTaskInfos.size()){
                tmpTaskInfo = useTaskInfos.get(position-1);
            }else{
                tmpTaskInfo = sysTaskInfos.get(position-2-useTaskInfos.size());
            }

            View view;
            ViewHolder holder;
            if(convertView != null && convertView instanceof RelativeLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                view = View.inflate(getApplicationContext(),R.layout.list_task_item,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_memsize = (TextView) view.findViewById(R.id.tv_memsize);
                holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(tmpTaskInfo.getIcon());
            holder.tv_name.setText(tmpTaskInfo.getName());
            holder.tv_memsize.setText("内存占用:"+Formatter.formatFileSize(getApplicationContext(),tmpTaskInfo.getMemsize()));
            holder.cb_status.setChecked(tmpTaskInfo.isChecked());
            return view;
        }
    }

    public void setTitle(){
        running_process_count.setText("运行中进程: "+ SystemInfoTools.getRunningProcessCount(this));
        long avamem = SystemInfoTools.getAvailMem(this);
        long totmem = SystemInfoTools.getTotalMem(this);
        leave_mem_info.setText("剩余/总内存: "+ Formatter.formatFileSize(this,avamem)+"/"+Formatter.formatFileSize(this,totmem));

    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memsize;
        CheckBox cb_status;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectAll(View view){
        for (TaskInfo info:useTaskInfos){
            info.setChecked(true);
        }
        for (TaskInfo info:sysTaskInfos){
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();

    }
    public void unSelectAll(View view){
        for (TaskInfo info:useTaskInfos){
            info.setChecked(!info.isChecked());
        }
        for (TaskInfo info:sysTaskInfos){
            info.setChecked(!info.isChecked());
        }
        adapter.notifyDataSetChanged();
    }
    public void killAll(View view){
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (TaskInfo info:useTaskInfos){
            if(info.isChecked()){
                am.killBackgroundProcesses(info.getPackname());
            }
        }
        for (TaskInfo info:sysTaskInfos){
            if(info.isChecked()){
                am.killBackgroundProcesses(info.getPackname());
            }
        }
        fillDate();
    }
    public void enterSetting(View view){
        Toast.makeText(getApplicationContext(), "SORRY 暂时还没此功能", Toast.LENGTH_SHORT).show();

    }
}
