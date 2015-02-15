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

    /*
    创建handler对象，显示 检查更新 后的数据并且执行相应动作
    Create handler,get the message of CheckUpdate()
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch(msg.what){
                case CheckUpdate.SHOW_UPDATE_DIALOG:
                    showToast("有更新");
                    enterHome();
                    break;
                case CheckUpdate.ENTER_HOME:
                    enterHome();
                    break;
                case CheckUpdate.URL_ERRER:
                    showToast("更新地址错误");
                    enterHome();
                    break;
                case CheckUpdate.NETWORD_ERRER:
                    showToast("网络连接错误");
                    enterHome();
                    break;
                case CheckUpdate.JSON_ERRER:
                    showToast("解析出错");
                    enterHome();
                    break;
            }
        }
    };

    private void init(){
        //获取启动时间
        long startTime = System.currentTimeMillis();
        //获取当前版本信息
        version = (TextView)findViewById(R.id.version);
        version.setText("beta " + getVersionName());
        CheckUpdate.old_version = getVersionName();
        //检测是否有更新
        CheckUpdate.checkUpdate(getString(R.string.update_url), getVersionName(), handler, startTime);
        //启动界面过渡动画
        AlphaAnimation travle = new AlphaAnimation(0.1f,1.0f);
        travle.setDuration(800);
        findViewById(R.id.actvity_welcome).startAnimation(travle);
    }
    /*
    获取版本号
     */
    private String getVersionName(){
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
    /*
    显示Toast
     */
    private void showToast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
}
