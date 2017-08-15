package com.unlp.tesis.steer;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
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

/**
 * Task to call one service to start, end o getStatus of user
 *
 * Created by mirrorlink on 6/17/17.
 */

class RequestPositionTask extends AsyncTask<String, String, String> {

    private Exception exception;
    private Context context;
    private JSONObject objectResponse = null;
    private Location location;

    public RequestPositionTask(Context context, Location location){
        this.context = context;
        this.location = location;
    }

    protected String doInBackground(String... services) {
        try {
            String position = this.location.getLatitude() + "," + this.location.getLongitude();
            String urlToParse = "https://roads.googleapis.com/v1/nearestRoads?points=" + position + "&key=" + this.context.getString(R.string.google_maps_web_services_key);
            URL url = new URL(urlToParse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            // read the reponse
            //send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
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
        MessagesUtils.updateMapCamera(this.getContext(), this.getObjectResponse(), this.location);
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

}