package com.unlp.tesis.steer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Task to log the user, it do when the app start
 *
 * Created by mirrorlink on 6/17/17.
 */

class RequestLoginTask extends AsyncTask<String, String, String> {

    private Exception exception;
    private Context context;

    public RequestLoginTask(Context context){
        this.context = context;
    }

    protected String doInBackground(String... urls) {
        try {
            URL url = new URL("http://163.10.20.80/universe-core/mobile/login");
            String param="celular=" + URLEncoder.encode("2215407348","UTF-8")+
                    "&version="+URLEncoder.encode("1","UTF-8")+
                    "&password="+URLEncoder.encode("123abc123","UTF-8")+
                    "&agente="+URLEncoder.encode("8","UTF-8");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setFixedLengthStreamingMode(param.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            // read the reponse
            //send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.close();
            System.out.println("Response Code: " + conn.getResponseCode());
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            System.out.println(response);
            try {
                JSONObject jObject = new JSONObject(response);
                if (jObject.getString("errorCode").equals("15")){
                    MainActivity.singletonVo.setTokenAthentication(jObject.getString("token"));
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (MalformedURLException e) {
            System.out.println("The URL is not valid.");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException!");
            System.out.println(e.getMessage());
        }
        return "";
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }


    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}