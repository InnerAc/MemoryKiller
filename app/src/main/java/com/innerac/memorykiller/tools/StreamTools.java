package com.innerac.memorykiller.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by InnerAce on 2015/2/11.
 */
public class StreamTools {
    public static String readFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = is.read(buffer))!=-1){
            baos.write(buffer, 0, len);
        }
        is.close();
        String result = baos.toString();
        baos.close();
        return result;
    }
}
