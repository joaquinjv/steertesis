package com.unlp.tesis.steer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;
import static android.app.Activity.RESULT_OK;

/**
 * Created by jvillalba on 6/6/17.
 */

public class SpeechTask extends AsyncTask<String, String, String> {

    private Context context;
    private int requestCode;
    private int resultCode;
    private Intent data;

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
            case 100: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result.get(0).equals("estacionar")) {
                        Toast.makeText(context, "Estacionar correcto", Toast.LENGTH_LONG).show();
                    }
                    else {
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

}
