package com.unlp.tesis.steer;

import android.content.Context;
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
            /*
            * Input:
            * email: String, clave: String, municipio: Long, direccion: String,
            * tipo: Long, descripcion: Long, observacion: String, fhInicio: String,
            * agente: Integer, version: String, fileName:String, latitud: String, longitud: String
            *
            * consultarTiposAlerta
            * tiposAlerta: [id : 8 , nombre: Vía publica
            * descripciones : [ id: 8 , nombre: Bache] ]
            * */
            //this.setEvent(services[0]);
            this.setEvent("crearEvento");
            URL url = new URL("http://163.10.181.26/MLP_CenIT_Service30/services/wssph.aspx");
            String param=//"&agente="+URLEncoder.encode("8","UTF-8")+
                    "version="+URLEncoder.encode("1.0","UTF-8")+
                    "&idUsuario="+URLEncoder.encode("1","UTF-8")+
                    "&nombre="+URLEncoder.encode("steer","UTF-8")+
                    "&apellido="+URLEncoder.encode("steer","UTF-8")+
                    //"&dni="+URLEncoder.encode("34403963","UTF-8")+
                    "&email="+URLEncoder.encode("joaquin547@gmail.com", "UTF-8") +
                    //"&email=joaquin547@gmail.com"+
                    "&clave="+URLEncoder.encode("4807","UTF-8")+
                    //"&claveActual="+URLEncoder.encode("","UTF-8")+
                    //"&claveNueva="+URLEncoder.encode("","UTF-8")+
                    //"&municipio="+URLEncoder.encode("1","UTF-8")+
                    "&direccion="+URLEncoder.encode("26707","UTF-8")+
                    "&calle="+URLEncoder.encode("Calle 26","UTF-8")+
                    "&numero="+URLEncoder.encode("707","UTF-8")+
                    "&tipo="+URLEncoder.encode("26","UTF-8")+
                    "&descripcion="+URLEncoder.encode("167","UTF-8")+
                    "&observacion=" + URLEncoder.encode("Obsninguna","UTF-8")+
                    "&fhInicio="+URLEncoder.encode("2017-09-12","UTF-8")+
                    //"&idEvento="+URLEncoder.encode("1","UTF-8")+
                    //"&tipo="+URLEncoder.encode("1","UTF-8")+
                    //"&descripcion="+URLEncoder.encode("descripshon","UTF-8")+
                    "&nombreMunicipio="+URLEncoder.encode("LaPlata","UTF-8")+
                    //"&codigoMunicipio="+URLEncoder.encode("La Plata","UTF-8")+
                    //"&municipioHabilitado="+URLEncoder.encode("La Plata","UTF-8")+
                    "&latitud="+URLEncoder.encode("-34.930353","UTF-8")+
                    "&longitud="+URLEncoder.encode("-57.972417","UTF-8")+
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
                jObject.put("name", EVENT_CREATE_ALERT_POTHOLE);
                jObject.put("lat", "-34.930353");
                jObject.put("lon", "-57.972417");
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