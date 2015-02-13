package com.innerac.memorykiller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Message;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;


import com.innerac.memorykiller.objs.CheckUpdate;


public class MainActivity extends Activity {


    TextView version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch(msg.what){
                case CheckUpdate.SHOW_UPDATE_DIALOG:
                    //Log.i("tag","显示升级对话框");
                    Toast.makeText(getApplicationContext(),"有更新",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CheckUpdate.ENTER_HOME:
                    // Log.i("tag","进入主界面");
                    enterHome();
                    break;
                case CheckUpdate.URL_ERRER:
                    //Log.i("tag", "地址错误");
                    Toast.makeText(getApplicationContext(),"URL ERROR",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CheckUpdate.NETWORD_ERRER:
                    // Log.i("tag","连接错误");
                    Toast.makeText(getApplicationContext(),"NETWORK ERROR",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CheckUpdate.JSON_ERRER:
                    // Log.i("tag","解析出错");
                    Toast.makeText(getApplicationContext(),"JSON ERROR",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
            }
        }
    };

    private void init(){
        version = (TextView)findViewById(R.id.version);
        version.setText("beta " + getVersionName());
        CheckUpdate.old_version = getVersionName();
        CheckUpdate.checkUpdate(getString(R.string.update_url), getVersionName(), handler);
        AlphaAnimation travle = new AlphaAnimation(0.1f,1.0f);
        travle.setDuration(800);
        findViewById(R.id.actvity_welcome).startAnimation(travle);
    }
    /*
    获取版本号
     */
    private String getVersionName(){
        //管理手机的APK
        PackageManager pm = getPackageManager();
        PackageInfo pinfo = null;
        try {
            pinfo = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pinfo.versionName;
    }




    /*
    进入主界面
     */
    private void enterHome(){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
