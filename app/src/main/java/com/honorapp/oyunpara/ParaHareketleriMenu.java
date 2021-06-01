package com.honorapp.oyunpara;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ParaHareketleriMenu extends Fragment {
    private Listener mListener;
    private TextView o11,o12,o13,o21,o22,o23,o31,o32,o33;
    private Button geri;
    private TextView hidden_button;
    private SharedPreferences sharedPreferences;
    int i=0;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_para_hareketleri_menu, container, false);
         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        geri=(Button)view.findViewById(R.id.geri1);

        o11=(TextView)view.findViewById(R.id.oyun1_1);
        o12=(TextView)view.findViewById(R.id.oyun1_2);
        o13=(TextView)view.findViewById(R.id.oyun1_3);
        o21=(TextView)view.findViewById(R.id.oyun2_1);
        o22=(TextView)view.findViewById(R.id.oyun2_2);
        o23=(TextView)view.findViewById(R.id.oyun2_3);
        o31=(TextView)view.findViewById(R.id.oyun3_1);
        o32=(TextView)view.findViewById(R.id.oyun3_2);
        o33=(TextView)view.findViewById(R.id.oyun3_3);

        o11.setText("Kazanılan: "+String.format("%.2f",sharedPreferences.getFloat("oyun1",0))+"₺");
        o12.setText("Atılan Jeton: xx"+sharedPreferences.getInt("oyun1_2",0));
        setIconInText(o12);
        o13.setText("Jeton Başına Kazanç: "+String.format("%.3f",((float)sharedPreferences.getFloat("oyun1",0)/sharedPreferences.getInt("oyun1_2",0)))+"₺");

        o21.setText("Kazanılan: "+String.format("%.2f",sharedPreferences.getFloat("oyun2",0))+"₺");
        o22.setText("Atılan Jeton: xx"+sharedPreferences.getInt("oyun2_2",0));
        setIconInText(o22);
        o23.setText("Jeton Başına Kazanç: "+String.format("%.3f",((float)sharedPreferences.getFloat("oyun2",0)/sharedPreferences.getInt("oyun2_2",0)))+"₺");

        o31.setText("Kazanılan: "+String.format("%.2f",sharedPreferences.getFloat("oyun3",0))+"₺");
        o32.setText("Atılan Jeton: xx"+sharedPreferences.getInt("oyun3_2",0));
        setIconInText(o32);
        o33.setText("Jeton Başına Kazanç: "+String.format("%.3f",((float)sharedPreferences.getFloat("oyun3",0)/sharedPreferences.getInt("oyun3_2",0)))+"₺");


        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeFragment(0);
            }
        });
        // Inflate the layout for this fragment


        hidden_button=(TextView)view.findViewById(R.id.stats_hidden);
        hidden_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if(i>4) {
                    Toast.makeText(view.getContext(), sharedPreferences.getString("KEY", "-"), Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ParaHareketleriMenu.Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public interface Listener {
        void changeFragment(int id);
    }
    public void setIconInText(TextView b){
        b.setTransformationMethod(null);
        int konum=b.getText().toString().indexOf("x");
        SpannableString ss = new SpannableString(b.getText());
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_like);
        d.setBounds(0, 10, b.getLineHeight(),b.getLineHeight()+10);
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        ss.setSpan(span, konum, konum+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        b.setText(ss);
    }
}
