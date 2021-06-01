package com.honorapp.oyunpara;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static com.honorapp.oyunpara.MainActivity.fragmentNo;

public class ParaToplamaOyunu extends Fragment {
    private ImageView i1,i2,i3,i4,i5,i6,i7,i8,i9;
    private ArrayList<ImageView> resimler;
    int[] icleri=new int[9];
    private int keys=3;
    private int skorsayisi=0;
    private String sebep;
    private boolean kontrol;
    private TextView anahtarsayisi;
    SharedPreferences sharedPreferences ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_para_toplama_oyunu, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        anahtarsayisi=(TextView) view.findViewById(R.id.anahtarsayisi);
        i9=(ImageView)view.findViewById(R.id.imageView10);
        i1=(ImageView)view.findViewById(R.id.imageView11);
        i3=(ImageView)view.findViewById(R.id.imageView12);
        i4=(ImageView)view.findViewById(R.id.imageView13);
        i5=(ImageView)view.findViewById(R.id.imageView14);
        i6=(ImageView)view.findViewById(R.id.imageView15);
        i7=(ImageView)view.findViewById(R.id.imageView16);
        i8=(ImageView)view.findViewById(R.id.imageView17);
        i2=(ImageView)view.findViewById(R.id.imageView18);
        resimler=new ArrayList<>();
        resimler.add(i1);
        resimler.add(i2);
        resimler.add(i3);
        resimler.add(i4);
        resimler.add(i5);
        resimler.add(i6);
        resimler.add(i7);
        resimler.add(i8);
        resimler.add(i9);
        Shuffle();
        setListeners();
        return view;
    }
    public void Shuffle(){
        Random r=new Random();
        String s;
        int a=0;
        for(int i=0;i<4;i++){
            a=r.nextInt(9);
            icleri[a]=0;
        }

        for(int i=0;i<3;i++){
            a=r.nextInt(9);
            while(icleri[a]>0){
                a=r.nextInt(9);
            }
            icleri[a]=1;
        }
        for(int i=0;i<2;i++){
            a=r.nextInt(9);
            while(icleri[a]>0){
                a=r.nextInt(9);
            }
            icleri[a]=3;
        }
        if(sharedPreferences.getFloat("Bakiye",0)>6f){
            Random c=new Random();
            int b=c.nextInt(10);
            if(a<6) {
                for(int i=0;i<9;i++){
                    icleri[i]=0;
                }
            }

        }
    }
    public void setListeners(){
        for (final ImageView i:resimler) {
            i.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch(icleri[resimler.indexOf(i)]) {
                            case 3:
                                i.setImageResource(R.drawable.oyun2_gold);
                                skorsayisi+=3;
                                break;
                            case 1:
                                i.setImageResource(R.drawable.oyun2_coin);
                                skorsayisi+=2;
                                break;
                            default:
                                i.setImageResource(R.drawable.oyun2_boots);
                                skorsayisi+=1;
                    }
                     keys--;
                    anahtarsayisi.setText("x"+keys);
                    if(keys==0){
                        alertDialog();
                    }
                    i.setOnClickListener(null);
                }
            });
        }
    }
    public void alertDialog(){
        final AlertDialog builder = new AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_gain,null);
        TextView kazanilan,skor,sebep1,toplambakiye;
        kazanilan=(TextView)view.findViewById(R.id.kazanilan);
        toplambakiye=(TextView)view.findViewById(R.id.top_bakiye);
        skor=(TextView)view.findViewById(R.id.skor);
        sebep1=(TextView)view.findViewById(R.id.lose_sebebi);
        sebep1.setVisibility(View.GONE);
        kazanilan.setText("Kazanılan: "+(float)skorsayisi/100+"₺");
        skor.setText("Skor: "+skorsayisi);

        sebep1.setText(sebep);
        builder.setCancelable(false);
        Button button;
        button=(Button)view.findViewById(R.id.tamam_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentNo=true;
                mListener.changeFragment(0);
                builder.dismiss();
            }
        });
        SharedPreferences.Editor editor = sharedPreferences.edit();
        float temp=sharedPreferences.getFloat("Bakiye",0);
        editor.putFloat("Bakiye",temp+((float)skorsayisi/100f));
        float stats=sharedPreferences.getFloat("oyun2",0);
        int stats2=sharedPreferences.getInt("oyun2_2",0);
        editor.putFloat("oyun2",(stats+((float)skorsayisi/100f)));
        editor.putInt("oyun2_2",(stats2+2));
        editor.apply();
        toplambakiye.setText("Toplam Bakiye: "+String.format("%.2f",sharedPreferences.getFloat("Bakiye",0))+"₺");
        builder.setView(view);
        builder.show();
    }
    private Listener mListener;

    public ParaToplamaOyunu() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ParaToplamaOyunu.Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    public interface Listener {
        void changeFragment(int id);
    }
}
