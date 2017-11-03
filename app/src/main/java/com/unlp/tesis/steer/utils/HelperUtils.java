package com.unlp.tesis.steer.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
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

import java.util.List;
import java.util.Locale;

import static com.unlp.tesis.steer.Constants.EVENT_CREATE_ALERT_COURT;
import static com.unlp.tesis.steer.Constants.EVENT_CREATE_ALERT_POTHOLE;
import static com.unlp.tesis.steer.Constants.E_STATE_OF_PARKING;
import static com.unlp.tesis.steer.MainActivity.mCurrLocation;
import static com.unlp.tesis.steer.MainActivity.mMap;
import static com.unlp.tesis.steer.MainActivity.parkingButton;
import static com.unlp.tesis.steer.MainActivity.pointOfSalesMarkers;

/**
 * Simple utils for messages to user
 *
 * Created by mirrorlink on 9/30/17.
 */

public class HelperUtils {

    private static TextToSpeech tts;

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
            if (response != null){
                json = (JSONArray) response.get("snappedPoints");
                Double lat = (Double)((JSONObject)(((JSONObject)(json).get(0)).get("location"))).get("latitude");
                Double lon = (Double)((JSONObject)(((JSONObject)(json).get(0)).get("location"))).get("longitude");
                LatLng latLng = new LatLng(lat,lon);
                location.setLatitude(lat);
                location.setLongitude(lon);

                if (mCurrLocation != null) {
                    mCurrLocation.remove();
                }

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mCurrLocation = mMap.addMarker(markerOptions);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * It's used for message to start and end the parking
     * @param context
     * @param directionsArray
     */
    public static void drawEventsInMap(Context context, JSONArray directionsArray) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            for (int i = 0; i < directionsArray.length(); i++) {
                Object dir = directionsArray.get(i);
                addresses = geocoder.getFromLocationName(((JSONObject) dir).getString("direction"), 1);
                LatLng latLng = new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
                Bitmap origBitmap = null;
                if (mMap != null){
                    if (((JSONObject) dir).getString("eventType").equals(EVENT_CREATE_ALERT_POTHOLE)){
                        origBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_court);
                    } else if (((JSONObject) dir).getString("eventType").equals(EVENT_CREATE_ALERT_COURT)){
                        origBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_gate);
                    }
                    int newWidth = 100;
                    int newHeight = 100;
                    Bitmap bitmap = Bitmap.createScaledBitmap(origBitmap, newWidth, newHeight, true);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.anchor(0.5f, 0.5f);
                    markerOptions.position(latLng);
                    markerOptions.title(((JSONObject) dir).getString("eventType"));
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    mMap.addMarker(markerOptions);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            String a;
        }
    }

}
