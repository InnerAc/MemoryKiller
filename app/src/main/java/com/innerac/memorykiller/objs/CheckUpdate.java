package com.innerac.memorykiller.objs;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.innerac.memorykiller.R;
import com.innerac.memorykiller.tools.StreamTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * 检查更新
 */
public class CheckUpdate {

    public static final int ENTER_HOME = 0;
    public static final int SHOW_UPDATE_DIALOG = 1;
    public static final int URL_ERRER = 2;
    public static final int NETWORD_ERRER = 3;
    public static final int JSON_ERRER = 4;

    public static String new_version;
    public static String new_description;
    public static String new_apkurl;
    public static boolean is_update;

    /*
    检查升级
     */
    public static void checkUpdate(final String update_url, final String version,final Handler handler){
        new Thread(){
            public void run(){
                Message mes = Message.obtain();

                try {


                    URL url = new URL(update_url);
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

                        if(version.equals(new_version)){
                            //go
                            mes.what=ENTER_HOME;
                            is_update = false;
                        }else{
                            is_update = true;
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
            }
        }.start();
    }
}
