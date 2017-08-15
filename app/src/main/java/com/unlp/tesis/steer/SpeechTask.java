package com.unlp.tesis.steer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import static android.app.Activity.RESULT_OK;
import static com.unlp.tesis.steer.Constants.E_END_PARKING;
import static com.unlp.tesis.steer.Constants.E_SPEECH_TO_END_PARKING;
import static com.unlp.tesis.steer.Constants.E_SPEECH_TO_REPORT_EVENT;
import static com.unlp.tesis.steer.Constants.E_SPEECH_TO_START_PARKING;
import static com.unlp.tesis.steer.Constants.E_SPEECH_TO_STATE_PARKING;
import static com.unlp.tesis.steer.Constants.E_START_PARKING;
import static com.unlp.tesis.steer.Constants.E_STATE_OF_PARKING;

/**
 * Created by jvillalba on 6/6/17.
 */

public class SpeechTask extends AsyncTask<String, String, String> {

    private Context context;
    private int requestCode;
    private int resultCode;
    private Intent data;
    private TextToSpeech tts;

    public SpeechTask(Context context){
        this.context = context;
    }

    public SpeechTask(Context context, int requestCode, int resultCode, Intent data) {
        this.context = context;
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    @Override
    protected String doInBackground(String... params) {
        //--- do nothing yet ---//
        return null;
    }

    /**
     * El método está siendo desarrollado por lo cuál no se trata de una versión final
     * @param resultExcecute
     */
    @Override
    protected void onPostExecute(String resultExcecute) {

        switch (requestCode) {
            // Always the result come from the Speech is 100
            case 100: {
                Boolean matchSem = Boolean.FALSE;
                Boolean matchCenit = Boolean.FALSE;
                String serviceString = null;
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // If the user try to said something for start the parking in Voice Recognizer
                    for (String s : E_SPEECH_TO_START_PARKING) {
                        int i = s.indexOf(result.get(0));
                        if (i >= 0) {
                            // We set service to call start parking
                            matchSem = Boolean.TRUE;
                            serviceString = E_START_PARKING;
                        }
                    }
                    // If the user try to said something for end the parking in Voice Recognizer
                    for (String s : E_SPEECH_TO_END_PARKING) {
                        int i = s.indexOf(result.get(0));
                        if (i >= 0) {
                            // We set service to call start parking
                            matchSem = Boolean.TRUE;
                            serviceString = E_END_PARKING;
                        }
                    }
                    // If the user try to said something for consult the state parking in Voice Recognizer
                    for (String s : E_SPEECH_TO_STATE_PARKING) {
                        int i = s.indexOf(result.get(0));
                        if (i >= 0) {
                            // We set service to call start parking
                            matchSem = Boolean.TRUE;
                            serviceString = E_STATE_OF_PARKING;
                        }
                    }

                    // If the user try to report an event
                    for (String s : E_SPEECH_TO_REPORT_EVENT) {
                        int i = s.indexOf(result.get(0));
                        if (i >= 0) {
                            // We set service to call start parking
                            matchCenit = Boolean.TRUE;
                            serviceString = E_STATE_OF_PARKING;
                        }
                    }

                    if (matchSem){
                        new RequestServiceTask(this.context).execute(serviceString);
                    } else if (matchCenit){
                        new RequestEventTask(this.context).execute(serviceString);
                    } else {
                        Toast.makeText(context, "Comando " + result.get(0) + " incorrecto", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }

        }
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }

    public TextToSpeech getTts() {
        return tts;
    }

    public void setTts(TextToSpeech tts) {
        this.tts = tts;
    }
}
