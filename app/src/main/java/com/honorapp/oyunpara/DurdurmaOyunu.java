package com.honorapp.oyunpara;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class DurdurmaOyunu extends Fragment {
    private ImageView i1,i2,i3,i4,i5,i6,i7,i8,i9;
    private ArrayList<ImageView> resimler;
    private CountDownTimer timer,sayac;
    private boolean kontrol=true;
    private int i=1,follower=0,follower2=0,skorsayisi=0,diff;
    private String sebep;
    private Button durdurma;
    private TextView skor,suresayaci;
    private ArrayList<Integer> path=new ArrayList<>();
    private SharedPreferences sharedPreferences ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_durdurma_oyunu, container, false);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        i1=(ImageView)view.findViewById(R.id.imageView);
        i2=(ImageView)view.findViewById(R.id.imageView2);
        i3=(ImageView)view.findViewById(R.id.imageView3);
        i4=(ImageView)view.findViewById(R.id.imageView4);
        i5=(ImageView)view.findViewById(R.id.imageView5);
        i6=(ImageView)view.findViewById(R.id.imageView6);
        i7=(ImageView)view.findViewById(R.id.imageView7);
        i8=(ImageView)view.findViewById(R.id.imageView8);
        i9=(ImageView)view.findViewById(R.id.imageView9);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        durdurma=(Button) view.findViewById(R.id.durdur);
        skor=(TextView) view.findViewById(R.id.skor_text_durdurma);
        suresayaci=(TextView) view.findViewById(R.id.sure_sayaci_1);

        diff=4;
        if(sharedPreferences.getFloat("Bakiye",0)>13f){
            diff=6;
        }
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
        durdurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAim();
                durdurma.setOnClickListener(null);
                durdurma.setEnabled(false);
            }
        });
        //filtre.setBackgroundResource(R.drawable.buttonshape2);
        return view;
    }
    public void newAim(){
        removeListeners();
        hepsiniSiyahYap();
        Random r=new Random();
        int b =r.nextInt(9);
        path.add(b);
        while (path.size()<diff){
            int a =r.nextInt(9);
            while (a==path.get(path.size()-1)){
                a =r.nextInt(9);
            }
            path.add(a);
        }
         //Toast.makeText(getContext(), path.toString(), Toast.LENGTH_SHORT).show();

        follower=0;
        timer = new CountDownTimer(3600, 3000/diff) {
            public void onTick(long millisUntilFinished) {
                if(follower<diff) {
                    if (follower > 0) {
                        resimler.get(path.get(follower - 1)).setImageResource(R.drawable.normal);
                    }
                    resimler.get(path.get(follower)).setImageResource(R.drawable.green);
                    follower++;
                }
            }

            public void onFinish() {
                resimler.get(path.get(follower-1)).setImageResource(R.drawable.normal);
                follower2=0;
                setListeners();
                setSuresayaci();


            }
        }.start();
    }
    public void hepsiniSiyahYap(){
        for (ImageView i:resimler) {
            i.setImageResource(R.drawable.normal);
        }
    }
    public void setListeners(){
        for (final ImageView i:resimler) {
            i.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(path.get(follower2)!= resimler.indexOf(i)){
                        sebep="Hata Yaptın!";
                        alertDialog();
                        if(sayac!=null){sayac.cancel();}
                        //finish();
                    }
                    hepsiniSiyahYap();
                    i.setImageResource(R.drawable.green);
                    follower2++;
                    if(follower2==path.size()){
                        skorsayisi++;
                        skor.setText("SKOR: "+String.valueOf(skorsayisi));
                        diff++;
                        sayac.cancel();
                        suresayaci.setText("-");
                        path.clear();
                        if(skorsayisi>7){
                            sebep="Maksimum Ödüle Ulaştın!";
                            alertDialog();
                        }
                        hepsiniSiyahYap();
                        CountDownTimer temp = new CountDownTimer(600, 500) {
                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                newAim();
                            }
                        }.start();
                        //newAim();
                    }
                }
            });
        }
    }
    public void removeListeners(){
        for (final ImageView i:resimler) {
            i.setOnClickListener(null);
        }
    }

    public void timerNull(){
        if(timer!=null){timer.cancel();}timer = null;
    }
    public void alertDialog(){
        final AlertDialog builder = new AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_gain,null);
        TextView kazanilan,skor,sebep1,toplambakiye;
        kazanilan=(TextView)view.findViewById(R.id.kazanilan);
        toplambakiye=(TextView)view.findViewById(R.id.top_bakiye);
        skor=(TextView)view.findViewById(R.id.skor);
        sebep1=(TextView)view.findViewById(R.id.lose_sebebi);
        kazanilan.setText("Kazanılan: "+(float)skorsayisi/100+"₺");
        skor.setText("Skor: "+skorsayisi);
        sebep1.setText(sebep);
        builder.setCancelable(false);

        Button button;
        button=(Button)view.findViewById(R.id.tamam_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sayac != null) {
                    sayac.cancel();
                    sayac = null;
                }
                timerNull();
                fragmentNo=true;
                mListener.changeFragment(0);
                builder.dismiss();
            }
        });
        SharedPreferences.Editor editor = sharedPreferences.edit();
        float temp=sharedPreferences.getFloat("Bakiye",0);
        editor.putFloat("Bakiye",temp+((float)skorsayisi/100f));
        float stats=sharedPreferences.getFloat("oyun1",0);
        int stats2=sharedPreferences.getInt("oyun1_2",0);
        editor.putFloat("oyun1",(stats+((float)skorsayisi/100f)));
        editor.putInt("oyun1_2",(stats2+1));
        editor.apply();
        toplambakiye.setText("Toplam Bakiye: "+String.format("%.2f",sharedPreferences.getFloat("Bakiye",0))+"₺");
        builder.setView(view);
        builder.show();
        kontrol=false;
    }
    private void setSuresayaci(){
        sayac = new CountDownTimer(7000, 950) {
            int a=7;
            public void onTick(long millisUntilFinished) {
                a--;
                suresayaci.setText(String.valueOf(a));
            }

            public void onFinish() {
                sebep="Süre Doldu";
                timerNull();
                alertDialog();
            }
        }.start();
    }


    private Listener mListener;

    public DurdurmaOyunu() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DurdurmaOyunu.Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    public interface Listener {
        void changeFragment(int id);
    }

}
