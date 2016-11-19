package com.abdymalikmulky.iak;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by abdymalikmulky on 11/13/16.
 */

public class CuacaAdapter extends ArrayAdapter<Object>{


    private Activity mActivity;
    private ArrayList<Cuaca> mCuaca;


    public CuacaAdapter(Activity activity,ArrayList<Cuaca> cuacaData){
        super(activity.getApplicationContext(),R.layout.list_cuaca,cuacaData.toArray());

        mActivity = activity;
        mCuaca = cuacaData;
    }
    @Override
    public int getCount() {
        return mCuaca.size();
    }
    public void update(ArrayList<Cuaca> listCuaca){
        mCuaca = listCuaca;
        notifyDataSetChanged();
    }
    public View getView(int position, View view, ViewGroup viewGroup){
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View listCuacaView = inflater.inflate(R.layout.list_cuaca,null,true);


        TextView cuacaHariTv = (TextView) listCuacaView.findViewById(R.id.cuacaHari);
        TextView cuacaTanggalBulanTv = (TextView) listCuacaView.findViewById(R.id.cuacaTanggalBulan);
        TextView cuacaJenis = (TextView) listCuacaView.findViewById(R.id.cuacaJenis);
        TextView cuacaCurahHujan = (TextView) listCuacaView.findViewById(R.id.cuacaCurahHujan);


        String hari = mCuaca.get(position).getHari();
        String tanggal = mCuaca.get(position).getTanggal();
        String bulan = mCuaca.get(position).getBulan();
        String jenis = mCuaca.get(position).getJenis();
        String curahHujan = mCuaca.get(position).getCurahHujan();


        cuacaHariTv.setText(hari);
        cuacaTanggalBulanTv.setText(tanggal+" "+bulan);
        cuacaJenis.setText(jenis);
        cuacaCurahHujan.setText(curahHujan);


        return listCuacaView;

    }

}
