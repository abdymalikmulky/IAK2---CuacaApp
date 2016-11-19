package com.abdymalikmulky.iak;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String URL_API = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Bandung,ID&mode=json&units=metric&cnt=7&appid=352d697da89a30abe1f993dd58ad2e6b";


    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;
    String cuacaJsonString = null;


    ListView listCuaca;
    ProgressBar pbLoading;

    CuacaAdapter cuacaAdapter;
    ArrayList<Cuaca> cuacas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cuacas = new ArrayList<>();

        listCuaca = (ListView) findViewById(R.id.listCuaca);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

        cuacaAdapter = new CuacaAdapter(MainActivity.this,cuacas);
        listCuaca.setAdapter(cuacaAdapter);


        new FetchWeatherTask().execute();
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String getReadableDateString(long time){
            long timestamp = time * 1000; //ini di conversi ke unix timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("EEE-d-MMMM-yyyy");
            String date = sdf.format(timestamp);
            return date;
        }

        //[0] = hari, [1] = tanggal, [2] = bulan, [3] = tahun
        private String[] getSeparateDate(String dateReadable){
            //split data
            return dateReadable.split("-");
        }

        private ArrayList<Cuaca> getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {


            // LIST json index yang akan di ambil
            final String API_LIST = "list";
            final String API_WEATHER = "weather";
            final String API_DATETIME = "dt";
            final String API_HUMIDITY = "humidity";
            final String API_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(API_LIST);

            for(int i = 0; i < weatherArray.length(); i++) {
                String date;
                String[] separateDate = new String[4];
                String humidity;
                String description;


                JSONObject dayForecast = weatherArray.getJSONObject(i);

                date = getReadableDateString(dayForecast.getLong(API_DATETIME));
                humidity = "( "+ dayForecast.getString(API_HUMIDITY)+" )";


                JSONObject weatherObject = dayForecast.getJSONArray(API_WEATHER).getJSONObject(0);
                description = weatherObject.getString(API_DESCRIPTION);


                //[0] = hari, [1] = tanggal, [2] = bulan, [3] = tahun
                separateDate = getSeparateDate(date);

                cuacas.add(new Cuaca(separateDate[0],separateDate[2],separateDate[1],description,humidity));
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            ArrayList<Cuaca> dataCuaca = new ArrayList<>();
            try {
                url = new URL(URL_API);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null){
                    buffer.append(line+"\n");
                }
                cuacaJsonString = buffer.toString();
                dataCuaca = getWeatherDataFromJson(cuacaJsonString,7);

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG,e.toString());


                e.printStackTrace();

            } catch (IOException e) {
                Log.e(LOG_TAG,e.toString());

                e.printStackTrace();
            } catch (JSONException e){
                Log.e(LOG_TAG,e.toString());

                e.printStackTrace();
            } finally{
                httpURLConnection.disconnect();
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG,e.toString());

                    e.printStackTrace();
                }
            }


            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(LOG_TAG+"data",cuacas.toString());
            cuacaAdapter.update(cuacas);

            //hide loading
            hideAndShowProgressBar(true);
        }
    }
//loading UI
    public void hideAndShowProgressBar(boolean hide){
        if(hide){
            pbLoading.setVisibility(View.GONE);
        }else{
            pbLoading.setVisibility(View.VISIBLE);
        }
    }
//data dummy
    public void mockingCuaca(){
        cuacas.add(new Cuaca("Minggu","November","13","Mendung","12/45"));
        cuacas.add(new Cuaca("Minggu","November","11","Mendung","12/45"));
        cuacas.add(new Cuaca("Minggu","November","12","Hujan Deras","12/45"));
        cuacas.add(new Cuaca("Minggu","November","14","Panas","12/45"));
        cuacas.add(new Cuaca("Minggu","November","15","Mendung","12/45"));
    }
}
