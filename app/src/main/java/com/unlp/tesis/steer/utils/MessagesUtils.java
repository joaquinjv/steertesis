package com.unlp.tesis.steer.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unlp.tesis.steer.MainActivity;
import com.unlp.tesis.steer.R;
import com.unlp.tesis.steer.entities.PointOfSale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.unlp.tesis.steer.Constants.EVENT_CREATE_ALERT_COURT;
import static com.unlp.tesis.steer.Constants.EVENT_CREATE_ALERT_POTHOLE;
import static com.unlp.tesis.steer.MainActivity.microphone;
import static com.unlp.tesis.steer.MainActivity.mCurrLocation;
import static com.unlp.tesis.steer.MainActivity.mMap;
import static com.unlp.tesis.steer.MainActivity.parkingButton;
import static com.unlp.tesis.steer.MainActivity.pointOfSalesMarkers;

/**
 * Simple utils for messages to user
 *
 * Created by mirrorlink on 6/20/17.
 */

public class MessagesUtils {

    private static TextToSpeech tts;
    /**
     * It's used for message to start and end the parking
     * @param context
     * @param response
     */
    public static void generteAlertMessage(Context context, JSONObject response) {
        try {
            if ((response.getString("errorCode") == "6") || (response.getString("errorCode") == "10")){
                if (!MainActivity.getParkingStarted()){
                    MainActivity.setParkingStarted(Boolean.TRUE);
                    parkingButton.setImageResource(R.drawable.ic_logo_parking_green);
                } else {
                    MainActivity.setParkingStarted(Boolean.FALSE);
                    parkingButton.setImageResource(R.drawable.ic_logo_parking);
                }
            }

            final AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                    .setMessage("Saldo: $" + response.getString("saldo"));

            final String messageError = response.getString("messageError");
            TextView title = new TextView(context);
            title.setText(messageError);
            title.setGravity(Gravity.CENTER);
            title.setTextSize(40);
            title.setBackgroundColor(Color.parseColor("#8bc34a"));
            title.setTextColor(Color.BLACK);
            dialog.setCustomTitle(title);
            final AlertDialog alert = dialog.create();
            alert.show();

            tts = (new TextToSpeech(context, new TextToSpeech.OnInitListener(){
                @Override
                public void onInit(int status) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.setLanguage(new Locale("ar"));
                        tts.speak(messageError, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }));

            TextView textMessageView = (TextView) alert.findViewById(android.R.id.message);
            textMessageView.setTextSize(30);

            // Hide after some seconds
            final Handler handler  = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (alert.isShowing()) {
                        alert.dismiss();
                    }
                }
            };

            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(runnable);
                }
            });

            handler.postDelayed(runnable, 7000);
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    /**
     * It's used for message to start and end the parking
     * @param context
     * @param response
     */
    public static void chargePointsOfSales(Context context, JSONObject response) {
        try {
            JSONArray jsonArray = (JSONArray) (new JSONObject(response.getString("extra")).get("comercios"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                if (!(explrObject.get("latitud").equals("")) && !(explrObject.get("longitud").equals(""))){
                    PointOfSale pos = new PointOfSale(
                            explrObject.get("nombre").toString(),explrObject.get("direccion").toString(),
                            explrObject.get("latitud").toString(),explrObject.get("longitud").toString());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(pos.getLatitude(), pos.getLongitude()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title(pos.getName())
                            .snippet(pos.getDetails());
                    if (mMap != null) {
                        // Remove last geoFenceMarker
                        //if (geoFenceMarker != null)
                        //    geoFenceMarker.remove();
                        pointOfSalesMarkers.add(mMap.addMarker(markerOptions));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            String a;
        }
    }

    /**
     * It's used for message to start and end the parking
     * @param context
     * @param response
     */
    public static void updateMapCamera(Context context, JSONObject response, Location location) {
        JSONArray json;
        Circle accuracyCircle = null;
        try {
            json = (JSONArray) response.get("snappedPoints");
            Double lat = (Double)((JSONObject)(((JSONObject)(json).get(0)).get("location"))).get("latitude");
            Double lon = (Double)((JSONObject)(((JSONObject)(json).get(0)).get("location"))).get("longitude");
            LatLng latLng = new LatLng(lat,lon);
            location.setLatitude(lat);
            location.setLongitude(lon);
            /*
            CameraPosition camPos = CameraPosition
                    .builder(
                            mMap.getCameraPosition() // current Camera
                    )
//                    .bearing(location.getBearing())
                    .target(latLng)   //Centramos en mi ubicacion
                    .zoom(19)         //Establecemos el zoom en 19
                    //.bearing(45)      //Establecemos la orientación con el noreste arriba
//                    .tilt(70)         //Bajamos el punto de vista de la cámara 70 grados
                    .build();
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(camPos);//newLatLngZoom(latLng, zoom);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 19);
            mMap.animateCamera(cameraUpdate);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    */
//            CameraPosition camPos = CameraPosition
//                    .builder(
//                            mMap.getCameraPosition() // current Camera
//                    )
//                    //.bearing(location.getBearing())
//                    .target(latLng)   //Centramos en mi ubicacion
//                    .zoom(15)         //Establecemos el zoom en 19
//                    //.bearing(45)      //Establecemos la orientación con el noreste arriba
//                    //.tilt(70)         //Bajamos el punto de vista de la cámara 70 grados
//                    .build();
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(camPos);//newLatLngZoom(latLng, zoom);
//            mMap.animateCamera(cameraUpdate);

            if (mCurrLocation != null) {
                mCurrLocation.remove();
            }

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mCurrLocation = mMap.addMarker(markerOptions);
            /*Toast.makeText(context, "Updated.!",
                    Toast.LENGTH_SHORT).show();*/
            /*
            if (accuracyCircle != null) {
                accuracyCircle.remove();
            }
            final CircleOptions accuracyCircleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(location.getAccuracy())
                    .fillColor(Color.argb(100, 130, 182, 228))
                    .strokeWidth(0.01f);
            mMap.addCircle(accuracyCircleOptions);
            */
            /*
            if (mMap != null) {
                // Remove last geoFenceMarker
                //if (geoFenceMarker != null)
                //    geoFenceMarker.remove();
                mMap.addMarker(markerOptions);
            }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    /**
     * It's used for message to confirm that the alert was created successfully
     * @param context
     * @param response
     */
    public static void generteEventMessage(Context context, JSONObject response) {
        try {
            String eventName = response.getString("name");
            final String messageError = "El evento " + eventName + " fue creado correctamente";

            LatLng latLng = new LatLng(response.getDouble("lat"),response.getDouble("lon"));
            Bitmap origBitmap = null;
            if (mMap != null){
                if (eventName == EVENT_CREATE_ALERT_POTHOLE){
                    origBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_court);
                } else if (eventName == EVENT_CREATE_ALERT_COURT){
                    origBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_gate);
                }
                int newWidth = 100;
                int newHeight = 100;
                Bitmap bitmap = Bitmap.createScaledBitmap(origBitmap, newWidth, newHeight, true);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.anchor(0.5f, 0.5f);
                markerOptions.position(latLng);
                markerOptions.title(response.getString("name"));
                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                mMap.addMarker(markerOptions);
            }

            tts = (new TextToSpeech(context, new TextToSpeech.OnInitListener(){
                @Override
                public void onInit(int status) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.setLanguage(new Locale("ar"));
                        tts.speak(messageError, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }));

        } catch (Exception e) {
            System.out.print(e);
        }
    }

}
