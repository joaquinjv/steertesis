package com.unlp.tesis.steer.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.unlp.tesis.steer.MainActivity;
import com.unlp.tesis.steer.R;

import org.json.JSONObject;

import java.util.Locale;

import static com.unlp.tesis.steer.MainActivity.fab;

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

            if (!MainActivity.getParkingStarted()){
                MainActivity.setParkingStarted(Boolean.TRUE);
                fab.setImageResource(R.drawable.ic_logo_parking_green);
            } else {
                MainActivity.setParkingStarted(Boolean.FALSE);
                fab.setImageResource(R.drawable.ic_logo_parking);
            }

            handler.postDelayed(runnable, 7000);
        } catch (Exception e) {
            System.out.print(e);
        }
    }

}
