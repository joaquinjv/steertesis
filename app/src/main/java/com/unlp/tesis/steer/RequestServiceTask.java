package com.unlp.tesis.steer;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.unlp.tesis.steer.utils.HelperUtils;
import com.unlp.tesis.steer.utils.MessagesUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static com.unlp.tesis.steer.Constants.E_CHARGE_POINT_OF_SALES;

/**
 * Task to call one service to start, end o getStatus of user
 *
 * Created by mirrorlink on 6/17/17.
 */

class RequestServiceTask extends AsyncTask<String, String, String> {

    private Exception exception;
    private Context context;
    private JSONObject objectResponse = null;

    private String service;

    public RequestServiceTask(Context context){
        this.context = context;
    }

    protected String doInBackground(String... services) {
        try {
            this.setService(services[0]);
            URL url = new URL("http://163.10.20.80/universe-core/mobile/" + this.getService());
            String param="celular=" + URLEncoder.encode("2215407348","UTF-8")+
                    "&token="+URLEncoder.encode(MainActivity.singletonVo.getTokenAthentication(),"UTF-8")+
                    "&version="+URLEncoder.encode("1","UTF-8")+
                    "&agente="+URLEncoder.encode("8","UTF-8")+
                    "&matricula="+URLEncoder.encode("ZIO989","UTF-8");
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
                this.setObjectResponse(jObject);
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
        // We call the response for the user when try to get any service
        if (this.getService()==E_CHARGE_POINT_OF_SALES){
            HelperUtils.chargePointsOfSales(this.getContext(), this.getObjectResponse());
        } else {
            MessagesUtils.generteAlertMessage(this.getContext(), this.getObjectResponse());
        }

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

    public JSONObject getObjectResponse() {
        return objectResponse;
    }

    public void setObjectResponse(JSONObject objectResponse) {
        this.objectResponse = objectResponse;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}