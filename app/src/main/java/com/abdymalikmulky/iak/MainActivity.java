package com.abdymalikmulky.iak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String URL_API = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Brazil&mode=json&units=metric&cnt=7&appid=352d697da89a30abe1f993dd58ad2e6b";

    private static final String ON_START = "onStart";
    private static final String ON_RESUME = "onResume";
    private static final String ON_CREATE = "onCreate";
    private static final String ON_PAUSE = "onPause";
    private static final String ON_STOP = "onStop";
    private static final String ON_DESTROY = "onDestroy";


    public static final int WEATHER_TASK_ID = 15;

    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;
    String cuacaJsonString = null;


    ListView listCuaca;
    ProgressBar pbLoading;

    CuacaAdapter cuacaAdapter;
    ArrayList<Cuaca> cuacas;


    String lokasi="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG,ON_CREATE);


        setContentView(R.layout.activity_main);

        cuacas = new ArrayList<>();

        listCuaca = (ListView) findViewById(R.id.listCuaca);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

        cuacaAdapter = new CuacaAdapter(MainActivity.this,cuacas);
        listCuaca.setAdapter(cuacaAdapter);

        listCuaca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cuaca cuaca = new Cuaca();
                cuaca = cuacas.get(position);


                Intent detailIntent = new Intent(MainActivity.this,DetailActivty.class);

                detailIntent.putExtra("hari",cuaca.getHari());
                detailIntent.putExtra("tanggal",cuaca.getTanggal());
                detailIntent.putExtra("bulan",cuaca.getBulan());
                detailIntent.putExtra("jenis",cuaca.getJenis());
                detailIntent.putExtra("humadity",cuaca.getCurahHujan());

                startActivity(detailIntent);

            }
        });


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        lokasi = sharedPreferences.getString("pref_lokasi","Brazil");

        //register
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        setTitle(lokasi);
        Log.d(LOG_TAG,"Lokasi : "+lokasi);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> weatherLoader = loaderManager.getLoader(WEATHER_TASK_ID);
        if(weatherLoader==null){
            loaderManager.initLoader(WEATHER_TASK_ID,null,this);
        }else {
            loaderManager.restartLoader(WEATHER_TASK_ID,null,this);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_setting :
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG,ON_START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG,ON_RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG,ON_PAUSE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG,ON_STOP);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG,ON_DESTROY);
        PreferenceManager.getDefaultSharedPreferences(this).
                unregisterOnSharedPreferenceChangeListener(this);
    }



    //LOADER CALLBACK
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<String>(this) {

            String dataCache;



            private String getReadableDateString(long time){
                long timestamp = time * 1000; //ini di conversi ke unix timestamp
                SimpleDateFormat sdf = new SimpleDateFormat("EEE-d-MMMM-yyyy");
                String date = sdf.format(timestamp);
                return date;
            }
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
            protected void onStartLoading() {
                super.onStartLoading();
                cuacas.clear();

                URL_API = "http://api.openweathermap.org/data/2.5/forecast/daily?q="+lokasi+"&mode=json&units=metric&cnt=7&appid=352d697da89a30abe1f993dd58ad2e6b";
                if(dataCache!=null) {
                    deliverResult(dataCache);
                }else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                Log.d(LOG_TAG+"data","loadInBackground");
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
                    return cuacaJsonString;

                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG,e.toString());
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    Log.e(LOG_TAG,e.toString());

                    e.printStackTrace();
                    return null;
                } catch (JSONException e){
                    Log.e(LOG_TAG,e.toString());

                    e.printStackTrace();
                    return null;
                } finally{
                    httpURLConnection.disconnect();
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG,e.toString());

                        e.printStackTrace();
                        return null;
                    }
                }



            }

            @Override
            public void deliverResult(String data) {
                dataCache=data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(LOG_TAG+"data","onLoadFinish");


        cuacaAdapter.update(cuacas);


        //hide loading
        hideAndShowProgressBar(true);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        lokasi = sharedPreferences.getString(key,"Brazil");
        setTitle(lokasi);

        Log.d(LOG_TAG,"Lokasi shared"+lokasi);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> weatherLoader = loaderManager.getLoader(WEATHER_TASK_ID);
        if(weatherLoader==null){
            loaderManager.initLoader(WEATHER_TASK_ID,null,this);
        }else {
            loaderManager.restartLoader(WEATHER_TASK_ID,null,this);
        }
    }


    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        private String getReadableDateString(long time){
            long timestamp = time * 1000; //ini di conversi ke unix timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("EEE-d-MMMM-yyyy");
            String date = sdf.format(timestamp);
            return date;
        }
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
