package com.innerac.memorykiller;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.innerac.memorykiller.tools.SystemInfoTools;


public class HomeActivity extends Activity {

    private TextView running_process_count;
    private TextView leave_mem_info;


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
