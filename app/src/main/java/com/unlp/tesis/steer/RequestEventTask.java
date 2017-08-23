package com.unlp.tesis.steer;

import android.content.Context;
import android.os.AsyncTask;

import com.unlp.tesis.steer.entities.Alert;
import com.unlp.tesis.steer.utils.MessagesUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Task to create an alert and persist this in the Android DB
 *
 * Created by mirrorlink on 6/17/17.
 */

class RequestEventTask extends AsyncTask<String, String, String> {

    private Exception exception;
    private Context context;
    private JSONObject objectResponse = null;
    private String event;
    private Alert alert;

    public RequestEventTask(Context context){
        this.context = context;
    }

    protected String doInBackground(String... services) {
        try {
            this.setEvent(services[0]);
            Double lat = Double.parseDouble(services[1]);
            Double lon = Double.parseDouble(services[2]);

            // Center the event in the middle of the route
            String position = lat + "," + lon;
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
            JSONObject jObjectResponse = new JSONObject(response);
            JSONArray json = (JSONArray) jObjectResponse.get("snappedPoints");
            Double latCentered = (Double)((JSONObject)(((JSONObject)(json).get(0)).get("location"))).get("latitude");
            Double lonCentered = (Double)((JSONObject)(((JSONObject)(json).get(0)).get("location"))).get("longitude");
            // Persist the alert in the Android DB
            // TODO
            // We need create the alert and persist this
            // Example
            alert = new Alert();
            alert.setLatitude(latCentered);
            alert.setLongitude(lonCentered);
            //alert.setLatitude(-34.931707);
            //alert.setLongitude(-57.968201);
            JSONObject jObject = new JSONObject();
            jObject.put("name", this.getEvent());
            jObject.put("lat", String.valueOf(this.getAlert().getLatitude()));
            jObject.put("lon", String.valueOf(this.getAlert().getLongitude()));
            this.setObjectResponse(jObject);

        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }

    protected void onPostExecute(String feed) {
        // We call the response for the user when try to get any service
        MessagesUtils.generteEventMessage(this.getContext(), this.getObjectResponse());
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

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }
}