package com.innerac.memorykiller;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.innerac.memorykiller.objs.TaskInfo;
import com.innerac.memorykiller.tools.SystemInfoTools;
import com.innerac.memorykiller.tools.TaskInfoProvider;

import java.util.List;


public class HomeActivity extends Activity {

    private TextView running_process_count;
    private TextView leave_mem_info;
    private LinearLayout ll_loading;
    private ListView lv_task_manager;
    private List<TaskInfo> allTaskInfos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    private void init(){
        running_process_count = (TextView) findViewById(R.id.running_process_count);
        leave_mem_info = (TextView) findViewById(R.id.leave_mem_info);
        running_process_count.setText("运行中进程: "+ SystemInfoTools.getRunningProcessCount(this));
        long avamem = SystemInfoTools.getAvailMem(this);
        long totmem = SystemInfoTools.getTotalMem(this);
        leave_mem_info.setText("剩余/总内存: "+ Formatter.formatFileSize(this,avamem)+"/"+Formatter.formatFileSize(this,totmem));

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_task_manager = (ListView) findViewById(R.id.lv_taskmanager);

        fillDate();
    }
    /*
    填充数据
     */
    private void fillDate() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){
                try {
                    allTaskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);

                    }
                });
            }
        };
    }

    private class TaskManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
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
            return null;
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
}
