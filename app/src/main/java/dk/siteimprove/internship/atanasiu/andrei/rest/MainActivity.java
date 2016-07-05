package dk.siteimprove.internship.atanasiu.andrei.rest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    EditText emailText;
    TextView responseView;
    ProgressBar progressBar;
    static final String API_KEY = "ebd8cdc10745831de07c286a9c6d967d";
    static final String API_URL = "https://api.siteimprove.com/v2/sites/73617/analytics/visitors/browsers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseView = (TextView) findViewById(R.id.responseView);
        emailText = (EditText) findViewById(R.id.emailText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new RetrieveFeedTask().execute();
            }
        });


    }

    public void switchActivity(View view)
    {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }
    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            String email = "andrei.atanasiu1994@gmail.com";
            // emailText.getText().toString();
            // Do some validation here


            try {
                URL url = new URL(API_URL  /*"email=" + email + "&apiKey=" + API_KEY*/);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String credentials = email + ":" + API_KEY;
                String auth = "Basic" + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                urlConnection.setRequestProperty("Authorization",auth);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response)
        {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            //responseView.setText(response);

            // TODO: check this.exception
            // TODO: do something with the feed

            try {


                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("items");
                String allitems = "";
                for(int i = 0; i < items.length(); i++)
                {
                    String browser_name = items.getJSONObject(i).getString("browser_name");
                    String visits = items.getJSONObject(i).getString("visits");
                    allitems = allitems +"Browser name: " + browser_name + "\n" + "Visits: " + visits + "\n\n" ;
                }



                responseView.setText(allitems);


                Log.i("IMPORTANT BBBBLLAAAAAA:",items.toString());


            } catch (JSONException e) {
               e.printStackTrace();
           }

        }
    }
}

