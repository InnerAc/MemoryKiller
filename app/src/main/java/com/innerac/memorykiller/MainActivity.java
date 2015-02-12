package com.innerac.memorykiller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.Switch;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;



import com.innerac.memorykiller.tools.StreamTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.LogRecord;

import javax.xml.datatype.Duration;


public class MainActivity extends Activity {

    private static final int ENTER_HOME = 0;
    private static final int SHOW_UPDATE_DIALOG = 1;
    private static final int URL_ERRER = 2;
    private static final int NETWORD_ERRER = 3;
    private static final int JSON_ERRER = 4;
    TextView version;

    private String new_version;
    private String new_description;
    private String new_apkurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        version = (TextView)findViewById(R.id.version);
        version.setText("alpha " + getVersionName());
        checkUpdate();
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch(msg.what){
                case SHOW_UPDATE_DIALOG:
                    Log.i("tag","显示升级对话框");
                    break;
                case ENTER_HOME:
                    Log.i("tag","进入主界面");
                    enterHome();
                    break;
                case URL_ERRER:
                    Log.i("tag", "地址错误");
                    Toast.makeText(getApplicationContext(),"URL ERROR",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case NETWORD_ERRER:
                    Log.i("tag","连接错误");
                    Toast.makeText(getApplicationContext(),"NETWORK ERROR",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case JSON_ERRER:
                    Log.i("tag","解析出错");
                    Toast.makeText(getApplicationContext(),"JSON ERROR",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
            }
        }
    };
    /*
    检查升级
     */
    private void checkUpdate(){
        new Thread(){
            public void run(){
                Message mes = Message.obtain();

                try {


                    URL url = new URL(getString(R.string.update_url));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(400);
                    int code = conn.getResponseCode();
                    if(code == 200){
                        InputStream is = conn.getInputStream();
                        //
                        String result = StreamTools.readFromStream(is);
                        //json
                        JSONObject obj = new JSONObject(result);
                        new_version = (String) obj.get("version");
                        new_description = (String) obj.get("description");
                        new_apkurl = (String) obj.get("apkurl");

                        if(getVersionName().equals(new_version)){
                            //go
                            mes.what=ENTER_HOME;
                        }else{
                            mes.what=SHOW_UPDATE_DIALOG;
                        }
                    }
                } catch (ProtocolException e) {
                    mes.what=URL_ERRER;
                    e.printStackTrace();
                } catch (IOException e) {
                    mes.what=NETWORD_ERRER;
                    e.printStackTrace();
                } catch (JSONException e) {
                    mes.what=JSON_ERRER;
                    e.printStackTrace();
                } finally {
                    handler.sendMessage(mes);
                }
            };
        }.start();
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
