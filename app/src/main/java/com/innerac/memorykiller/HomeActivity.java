package com.innerac.memorykiller;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
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
import com.innerac.memorykiller.objs.CheckUpdate;
import com.innerac.memorykiller.tools.SystemInfoTools;
import com.innerac.memorykiller.tools.TaskInfoProvider;
import com.innerac.memorykiller.update.UpdateManager;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Activity {

    private TextView running_process_count;     //显示运行时进程数
    private TextView leave_mem_info;            //显示剩余内存
    private LinearLayout ll_loading;            //布局控件,显示加载图片
    private ListView lv_task_manager;           //显示进程列表
    private List<TaskInfo> allTaskInfos;        //所有的进程列表
    private List<TaskInfo> useTaskInfos;        //用户进程列表
    private List<TaskInfo> sysTaskInfos;        //系统进程列表
    private TextView tv_status;                 //当前显示列表状态
    private TaskManagerAdapter adapter;         //进程列表适配器

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

        /*
        列表滚动监听
        在tv_stauts中显示当前显示列表的状态
         */
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

        /*
        列表Item点击事件监听
        点击后选中,再次点击取消选择
         */
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

        //显示加载动画
        ll_loading.setVisibility(View.VISIBLE);
        //开启线程用于更新运行程序列表
        new Thread(){
            public void run(){

                //获取所有运行中的进程
                allTaskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
                useTaskInfos = new ArrayList<TaskInfo>();
                sysTaskInfos = new ArrayList<TaskInfo>();
                //进程分类(系统进程,用户进程)
                for(TaskInfo info:allTaskInfos){
                    if(info.isUserTask()){
                        useTaskInfos.add(info);
                    }else{
                        sysTaskInfos.add(info);
                    }
                }
                //开启UI线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭加载动画
                        ll_loading.setVisibility(View.INVISIBLE);
                        //适配列表
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



    /*
    设置标头
     */
    public void setTitle(){
        running_process_count.setText("运行中进程: "+ SystemInfoTools.getRunningProcessCount(this));
        long avamem = SystemInfoTools.getAvailMem(this);
        long totmem = SystemInfoTools.getTotalMem(this);
        leave_mem_info.setText("剩余/总内存: "+ Formatter.formatFileSize(this,avamem)+"/"+Formatter.formatFileSize(this,totmem));

    }
    /*
    静态类,用于作用设置Item信息
     */
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memsize;
        CheckBox cb_status;
    }

    /*
    自定义适配器类
     */
    private class TaskManagerAdapter extends BaseAdapter {

        public int getCount() {

            return allTaskInfos.size()+2;
        }
        public Object getItem(int position) {

            return null;
        }
        public long getItemId(int position) {

            return 0;
        }
        //适配器规则
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
                //系统进程标签
                TextView sys_title = new TextView(getApplicationContext());
                sys_title.setText("系统进程"+sysTaskInfos.size()+"个");
                sys_title.setBackgroundColor(Color.GRAY);
                sys_title.setTextColor(Color.WHITE);
                return sys_title;
            }else if(position <= useTaskInfos.size()){
                //显示用户进程
                tmpTaskInfo = useTaskInfos.get(position-1);
            }else{
                //显示系统进程
                tmpTaskInfo = sysTaskInfos.get(position-2-useTaskInfos.size());
            }

            View view;
            ViewHolder holder;
            if(convertView != null && convertView instanceof RelativeLayout){
                //该条目不是进程信息而是标题
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                //获取定义好的布局
                view = View.inflate(getApplicationContext(), R.layout.list_task_item,null);
                //实例化holder
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_memsize = (TextView) view.findViewById(R.id.tv_memsize);
                holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }
            //填充信息
            holder.iv_icon.setImageDrawable(tmpTaskInfo.getIcon());
            holder.tv_name.setText(tmpTaskInfo.getName());
            holder.tv_memsize.setText("内存占用:"+ Formatter.formatFileSize(getApplicationContext(), tmpTaskInfo.getMemsize()));
            holder.cb_status.setChecked(tmpTaskInfo.isChecked());
            return view;
        }
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
    /*
    全选事件
     */
    public void selectAll(View view){
        for (TaskInfo info:useTaskInfos){
            info.setChecked(true);
        }
        for (TaskInfo info:sysTaskInfos){
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();

    }
    /*
    反选事件
     */
    public void unSelectAll(View view){
        for (TaskInfo info:useTaskInfos){
            info.setChecked(!info.isChecked());
        }
        for (TaskInfo info:sysTaskInfos){
            info.setChecked(!info.isChecked());
        }
        adapter.notifyDataSetChanged();
    }
    /*
    清理事件
     */
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
    /*
    更新事件
     */
    public void updateCheck(View view){
        if(CheckUpdate.is_update){
            showUpdateDialog();
        }else{
            showNotUpdateDialog();
        }
    }
    /*
    复写返回键,点击后结束应用
     */
    public void onBackPressed(){
        //Log.i("tag","onBackPressed");
        //结束进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /*
    显示升级对话框
     */
    protected void showUpdateDialog() {
        UpdateManager um = new UpdateManager(this);
        um.checkUpdateInfo();
    }
    /*
    显示不用升级对话框
     */
    protected void showNotUpdateDialog(){
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("应用更新");
        build.setMessage("您的应用已经是最新版本\n当前版本: "+CheckUpdate.old_version);
        build.setPositiveButton("返回", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        build.show();
    }
}
