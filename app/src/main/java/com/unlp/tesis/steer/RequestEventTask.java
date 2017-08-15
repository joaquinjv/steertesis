package com.unlp.tesis.steer;

import android.content.Context;
import android.os.AsyncTask;

import com.unlp.tesis.steer.entities.Alert;
import com.unlp.tesis.steer.utils.MessagesUtils;

import org.json.JSONObject;

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
            // Persist the alert in the Android DB
            // TODO
            // We need create the alert and persist this
            /* Example
            JSONObject jObject = new JSONObject();
            jObject.put("lat", String.valueOf(this.getAlert().getLatitude()));
            jObject.put("lon", String.valueOf(this.getAlert().getLongitude()));
            this.setObjectResponse(jObject);
            */
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