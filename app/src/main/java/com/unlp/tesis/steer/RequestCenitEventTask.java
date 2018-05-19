package com.unlp.tesis.steer;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

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
import java.util.List;
import java.util.Locale;

import static com.unlp.tesis.steer.Constants.EVENT_CREATE_ALERT_POTHOLE;

/**
 * Task to call one service to start, end o getStatus of user
 *
 * Created by mirrorlink on 6/17/17.
 */

class RequestCenitEventTask extends AsyncTask<String, String, String> {

    private Exception exception;
    private Context context;
    private JSONObject objectResponse = null;
    private String event;

    public RequestCenitEventTask(Context context){
        this.context = context;
    }

    protected String doInBackground(String... services) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());
            String lat = services[1];
            String lon = services[2];

            addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String streetNumber = addresses.get(0).getFeatureName();
            String street = addresses.get(0).getThoroughfare();
            this.setEvent("crearEvento");
            URL url = new URL("http://163.10.181.26/MN_Cenit_Service30/services/wssph.aspx");
            String param=//"&agente="+URLEncoder.encode("8","UTF-8")+
                    "version="+URLEncoder.encode("1.0","UTF-8")+
                    "&idUsuario="+URLEncoder.encode("1","UTF-8")+
                    "&nombre="+URLEncoder.encode("joaquin","UTF-8")+
                    "&apellido="+URLEncoder.encode("joaquin","UTF-8")+
                    //"&dni="+URLEncoder.encode("34403963","UTF-8")+
                    "&email="+URLEncoder.encode("joaquin547@gmail.com", "UTF-8") +
                    //"&email=joaquin547@gmail.com"+
                    "&clave="+URLEncoder.encode("7b5e","UTF-8")+
                    //"&claveActual="+URLEncoder.encode("","UTF-8")+
                    //"&claveNueva="+URLEncoder.encode("","UTF-8")+
                    //"&municipio="+URLEncoder.encode("1","UTF-8")+
                    "&direccion="+URLEncoder.encode(address,"UTF-8")+
                    "&calle="+URLEncoder.encode(street,"UTF-8")+
                    "&numero="+URLEncoder.encode(streetNumber,"UTF-8")+
                    "&tipo="+URLEncoder.encode(services[3],"UTF-8")+
                    "&descripcion="+URLEncoder.encode(services[3],"UTF-8")+
                    "&observacion=" + URLEncoder.encode("","UTF-8")+
                    "&fhInicio="+URLEncoder.encode("2017-10-07","UTF-8")+
                    //"&idEvento="+URLEncoder.encode("1","UTF-8")+
                    //"&tipo="+URLEncoder.encode("1","UTF-8")+
                    //"&descripcion="+URLEncoder.encode("descripshon","UTF-8")+
                    "&nombreMunicipio="+URLEncoder.encode("LaPlata","UTF-8")+
                    //"&codigoMunicipio="+URLEncoder.encode("La Plata","UTF-8")+
                    //"&municipioHabilitado="+URLEncoder.encode("La Plata","UTF-8")+
                    "&latitud="+URLEncoder.encode(lat,"UTF-8")+
                    "&longitud="+URLEncoder.encode(lon,"UTF-8")+
                    "&op="+URLEncoder.encode(this.getEvent(),"UTF-8");
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
                jObject.put("name", services[0]);
                jObject.put("lat", lat);
                jObject.put("lon", lon);
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
        //MessagesUtils.generteAlertMessage(this.getContext(), this.getObjectResponse());
        /*if (this.getEvent() == EVENT_CREATE_ALERT_POTHOLE){
            MessagesUtils.generteAlertMessage(this.getContext(), this.getObjectResponse());
        }*/
        try {
            if (this.getObjectResponse().getString("errorCode").equals("11")) {
                MessagesUtils.generteEventMessage(this.getContext(), this.getObjectResponse());
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
