package com.abdymalikmulky.iak;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActionBar actionBar = getSupportActionBar();
        //tombol back di action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_setting);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //aksi tombol back
        if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
