package com.honorapp.oyunpara;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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

import java.util.Random;

import static com.honorapp.oyunpara.MainActivity.fragmentNo;

public class SlotOyunu extends Fragment {
    private CountDownTimer timer,timer2;
    private ImageView i,slot_butonu;
    private int c=0,temp,skorsayisi;
    SharedPreferences sharedPreferences ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_slot_oyunu, container, false);
        i=(ImageView)view.findViewById(R.id.slotmachine);
        slot_butonu=(ImageView)view.findViewById(R.id.slot_button);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        slot_butonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationGambler();
                Roll();
                slot_butonu.setOnClickListener(null);
            }
        });

        return view;
    }
    public void Roll(){
        final Random r =new Random();
        timer = new CountDownTimer(2600, 160) {
            public void onTick(long millisUntilFinished) {
                temp=c;
                c=r.nextInt(4);
                while(c==temp){
                    c=r.nextInt(4);
                }
                switch(c) {
                    case 3:
                        i.setImageResource(R.drawable.elmas);
                        break;
                    case 2:
                        i.setImageResource(R.drawable.oyun2_gold);
                        break;
                    case 1:
                        i.setImageResource(R.drawable.oyun2_coin);
                        break;
                    default:
                        i.setImageResource(R.drawable.oyun2_boots);
                }
            }

            public void onFinish() {
                if(c==3){
                    Random r=new Random();
                    int a=r.nextInt(10);
                    if(a<7) {
                        c = 1;
                        i.setImageResource(R.drawable.oyun2_coin);
                    }
                }
                if(sharedPreferences.getFloat("Bakiye",0)>15f){
                    Random r=new Random();
                    int a=r.nextInt(10);
                    if(a<8) {
                        c = 0;
                        i.setImageResource(R.drawable.oyun2_boots);
                    }
                }

                switch(c) {
                    case 3:
                       skorsayisi=7;
                        break;
                    case 2:
                        skorsayisi=3;
                        break;
                    case 1:
                        skorsayisi=2;
                        break;
                    default:
                        skorsayisi=1;
                }
                alertDialog();
            }
        }.start();
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
        kazanilan.setText("Kazanılan: "+String.format("%.2f",(float)skorsayisi/100)+"₺");
        skor.setText("Skor: "+skorsayisi);

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
        float stats=sharedPreferences.getFloat("oyun3",0);
        int stats2=sharedPreferences.getInt("oyun3_2",0);
        editor.putFloat("oyun3",(stats+((float)skorsayisi/100f)));
        editor.putInt("oyun3_2",(stats2+1));

        editor.apply();
        toplambakiye.setText("Toplam Bakiye: "+String.format("%.2f",sharedPreferences.getFloat("Bakiye",0))+"₺");
        builder.setView(view);
        builder.show();
    }
    public void animationGambler(){
        slot_butonu.setImageResource(R.drawable.gambling2);

        timer2 = new CountDownTimer(150, 150) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if(slot_butonu!=null) {
                    slot_butonu.setImageResource(R.drawable.gambling);
                }
            }
        }.start();
    }
    private Listener mListener;

    public SlotOyunu() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SlotOyunu.Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    public interface Listener {
        void changeFragment(int id);
    }

}
