package com.unlp.tesis.steer.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unlp.tesis.steer.MainActivity;
import com.unlp.tesis.steer.R;

import org.json.JSONObject;

import java.util.Locale;

import static com.unlp.tesis.steer.Constants.EVENT_CREATE_ALERT_COURT;
import static com.unlp.tesis.steer.Constants.EVENT_CREATE_ALERT_POTHOLE;
import static com.unlp.tesis.steer.MainActivity.mMap;
import static com.unlp.tesis.steer.MainActivity.parkingButton;

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
                } else if (!MainActivity.getEndParkingForced()){
                    MainActivity.setParkingStarted(Boolean.FALSE);
                    parkingButton.setImageResource(R.drawable.ic_logo_parking);
                } else {
                    MainActivity.setParkingStarted(Boolean.FALSE);
                    MainActivity.setEndParkingForced(Boolean.FALSE);
                    parkingButton.setImageResource(R.drawable.ic_logo_parking_disabled);
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

            playAudioMessage(context, messageError);

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
     * It's used for init the parking button
     * @param context
     * @param response
     */
    public static void initButtonToPark(Context context, JSONObject response) {
        try {
            if (response.getString("errorCode") == "2") {
                MainActivity.setParkingStarted(Boolean.TRUE);
                parkingButton.setImageResource(R.drawable.ic_logo_parking_green);
            } else if (response.getString("errorCode") == "8"){
                MainActivity.setParkingStarted(Boolean.FALSE);
                parkingButton.setImageResource(R.drawable.ic_logo_parking);
            }
        } catch (Exception e) {
            System.out.print(e);
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
                markerOptions.title(response.getString("name").toUpperCase());
                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                mMap.addMarker(markerOptions);

            }

            playAudioMessage(context, messageError);

        } catch (Exception e) {
            System.out.print(e);
        }
    }

    /**
     * It's used for message to start and end the parking
     * @param context
     * @param response
     */
    public static void generteGeofenceAlert(Context context, String response) {
        try {

            final AlertDialog.Builder dialog =
                    new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);

            final String titulo = "Ingresó a zona de estacionamiento medido";
            TextView title = new TextView(context);
            title.setText(titulo);
            title.setGravity(Gravity.CENTER);
            title.setTextSize(30);
            dialog.setCustomTitle(title);
            final AlertDialog alert = dialog.create();
            alert.show();

            playAudioMessage(context, titulo);

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
            Log.e("s", e.toString());
            System.out.print(e);
        }
    }

    private static void playAudioMessage(Context context, final String messageError){
        if (Preferences.getAudioPreference(context)){
            tts = (new TextToSpeech(context, new TextToSpeech.OnInitListener(){
                @Override
                public void onInit(int status) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.setLanguage(new Locale("ar"));
                        tts.speak(messageError, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }));
        }
    }

}
