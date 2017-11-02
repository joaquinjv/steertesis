package com.unlp.tesis.steer;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.unlp.tesis.steer.utils.HelperUtils;
import com.unlp.tesis.steer.utils.MessagesUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

/**
 * Task to call one service to start, end o getStatus of user
 *
 * Created by mirrorlink on 01/11/17.
 */

class RequestCenitListOfEventsTask extends AsyncTask<String, String, String> {

    private Exception exception;
    private Context context;
    private JSONObject objectResponse = null;
    private String event;

    private BufferedReader responseIn;

    public RequestCenitListOfEventsTask(Context context){
        this.context = context;
    }

    protected String doInBackground(String... services) {
        try {
            URL url = new URL("http://163.10.181.26/cenitservicestest/services/wssph.aspx");
            String param="version="+URLEncoder.encode("1.0","UTF-8")+
                    "&idUsuario="+URLEncoder.encode("1","UTF-8")+
                    "&nombre="+URLEncoder.encode("joaquin","UTF-8")+
                    "&apellido="+URLEncoder.encode("joaquin","UTF-8")+
                    "&email="+URLEncoder.encode("joaquin547@gmail.com", "UTF-8") +
                    "&clave="+URLEncoder.encode("4010","UTF-8")+
                    "&tipo="+URLEncoder.encode("8","UTF-8")+
                    "&observacion=" + URLEncoder.encode("","UTF-8")+
                    "&fhInicio="+URLEncoder.encode("2017-10-07","UTF-8")+
                    "&codigoMunicipio="+URLEncoder.encode("8","UTF-8")+
                    "&nombreMunicipio="+URLEncoder.encode("LaPlata","UTF-8")+
                    "&municipioHabilitado="+URLEncoder.encode("true","UTF-8")+
                    "&pagina="+URLEncoder.encode("1","UTF-8")+
                    "&cantidad="+URLEncoder.encode("10","UTF-8")+
                    "&op="+URLEncoder.encode("listarEventos","UTF-8");
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
            this.setResponseIn(new BufferedReader(
                    new InputStreamReader(conn.getInputStream())));
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
        try {
            if (this.getObjectResponse().getString("errorCode").equals("10")) {
                JSONObject extra = (JSONObject) this.getObjectResponse().get("extra");
                JSONArray events = (JSONArray) extra.get("eventos");
                JSONArray directionsArray = new JSONArray();
                for (int i=0; i < events.length(); i++) {
                    JSONObject directions = new JSONObject();
                    String formatterDirection = events.getJSONObject(i).get("direccion").toString().split(",")[0].trim();
                    JSONObject eventType = (JSONObject) events.getJSONObject(i).get("descripcion");
                    directions.accumulate("direction", "La Plata, " + formatterDirection);
                    directions.accumulate("eventType", eventType.get("nombre"));
                    directionsArray.put(i, directions);
                }
                HelperUtils.drawEventsInMap(this.getContext(), directionsArray);
            }
        }  catch (Exception e) {
            System.out.println("IOException!");
            System.out.println(e.getMessage());
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

    public BufferedReader getResponseIn() {
        return responseIn;
    }

    public void setResponseIn(BufferedReader responseIn) {
        this.responseIn = responseIn;
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
}