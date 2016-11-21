package com.abdymalikmulky.iak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivty extends AppCompatActivity {


    TextView tvHari,tvTanggal,tvJenis,tvHumidity;
    ImageView ivJenis;

    String hari,tanggal,bulan,jenis, humadity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activty);

        hari = getIntent().getStringExtra("hari");
        tanggal = getIntent().getStringExtra("tanggal");
        bulan = getIntent().getStringExtra("bulan");
        jenis = getIntent().getStringExtra("jenis");
        humadity = getIntent().getStringExtra("humadity");


        tvHari = (TextView) findViewById(R.id.detail_hari);
        tvTanggal = (TextView) findViewById(R.id.detail_tanggal);
        tvHumidity = (TextView) findViewById(R.id.detail_humidity);
        tvJenis = (TextView) findViewById(R.id.detail_jenis);
        ivJenis = (ImageView) findViewById(R.id.detail_image);


        tvHari.setText(hari);
        tvTanggal.setText(tanggal+" - "+bulan);
        tvHumidity.setText(humadity);
        tvJenis.setText(jenis);
        switch (jenis){
            case "Rain" :
                ivJenis.setImageDrawable(getResources().getDrawable(R.drawable.rain));
                break;
            case "Clear" :
                ivJenis.setImageDrawable(getResources().getDrawable(R.drawable.sunshine));
                break;
            default:
                break;
        }



    }
}
