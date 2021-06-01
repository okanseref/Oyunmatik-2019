package com.honorapp.oyunpara;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnaMenu extends Fragment implements RewardedVideoAdListener {
    private Listener mListener;
    private Button oyun1,oyun2,oyun3,buton_sol,buton_sag,jeton_butonu,mid_button,yardim;
    public AnaMenu() {
        // Required empty public constructor
    }
    private TextView gorev_yuzde;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean cooldown=true;
    private int jetonsayisi=0,reklamStack=8,rewarded_bonus=0;
    private int refresh_suresi=72000;
    private RewardedVideoAd mRewardedVideoAd;
    private boolean rewarded_verimlilik=true;
    private Button switch_oyun,switch_gorev,gorev_topla;
    private TableRow row_oyun,row_gorev;
    private DatabaseReference databaseReference3_antihack;
    private String antihack,id;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_ana_menu, container, false);
        switch_oyun=(Button) view.findViewById(R.id.switch_oyun);
        switch_gorev=(Button) view.findViewById(R.id.switch_gorev);
        gorev_topla=(Button) view.findViewById(R.id.gorev_topla);
        row_oyun=(TableRow) view.findViewById(R.id.row1);
        row_gorev=(TableRow) view.findViewById(R.id.row2);
        oyun1=(Button) view.findViewById(R.id.oyun1);
        oyun2=(Button) view.findViewById(R.id.oyun2);
        oyun3=(Button) view.findViewById(R.id.oyun3);
        yardim=(Button) view.findViewById(R.id.yardim_butonu);
        gorev_yuzde=(TextView) view.findViewById(R.id.gorev_yuzde);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        //sag_yazi=(Button) view.findViewById(R.id.button_sag);
        mid_button=(Button) view.findViewById(R.id.button_mid);
        buton_sag=(Button) view.findViewById(R.id.button_sag);
        buton_sol=(Button) view.findViewById(R.id.butonsol);
        jeton_butonu=(Button) view.findViewById(R.id.jeton_buton);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        jetonsayisi=sharedPreferences.getInt("JS",10);
        reklamStack=sharedPreferences.getInt("Stacks",4);
        jetonSayisiGuncelle();

        cooldownKontrolu();
        hileKontrol();


        //Long tsLong = System.currentTimeMillis()/1000;
        //String ts = tsLong.toString();
        //oyun1.setText(ts);
        //setIconInText(buton_sag);
        setIconInText(oyun1);
        setIconInText(oyun2);
        setIconInText2(oyun2);
        setIconInText(oyun3);
        setIconInText(gorev_topla);

        jeton_butonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ipuclariYukle("rewarded_ipucu_yeni2","BİLGİLENDİRME","REKLAM İZLERKEN VİDEOYU İZLEDİĞİN İÇİN 1 JETON, VİDEONUN LİNKİNE GİTTİĞİN İÇİN DE EKSTRA 1 JETON KAZANIRSIN.");

                if(rewarded_verimlilik){
                    loadRewardedVideoAd();
                    rewarded_verimlilik=false;
                }
                cooldownKontrolu();
                if(reklamStack>0) {
                    if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    }else {
                        Toast.makeText(getContext(),"Reklam Yükleniyor Lütfen Bekleyin.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    alertDialog();
                }


            }
        });

        buton_sag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeFragment(1);
            }
        });
        buton_sol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeFragment(2);
            }
        });
        oyun1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(jetonsayisi>0) {
                    jetonsayisi--;
                    jetonSayisiGuncelle();
                    mListener.changeFragment(4);
                    //startActivity(new Intent(view.getContext(), DurdurmaOyunu.class));
                }else{
                    alertDialog3_yetersizBakiye();
                }
            }
        });
        oyun2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(jetonsayisi>1) {
                    jetonsayisi-=2;
                    jetonSayisiGuncelle();
                    mListener.changeFragment(5);
                    //startActivity(new Intent(view.getContext(), ParaToplamaOyunu.class));
                }else{
                    alertDialog3_yetersizBakiye();
                }
            }
        });

        oyun3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(jetonsayisi>0) {
                    jetonsayisi--;
                    jetonSayisiGuncelle();
                    mListener.changeFragment(6);
                    //startActivity(new Intent(view.getContext(), SlotOyunu.class));
                }else{
                    alertDialog3_yetersizBakiye();
                }
            }
        });

        yardim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeFragment(3);
            }
        });

        if(sharedPreferences.getBoolean("switch",true)){
            row_gorev.setVisibility(View.GONE);
            row_oyun.setVisibility(View.VISIBLE);
            switch_gorev.setEnabled(true);
            switch_oyun.setEnabled(false);
            switch_oyun.setBackgroundResource(R.drawable.yellow_button_press);
            switch_gorev.setBackgroundResource(R.drawable.button_effect);
        }else{
            row_gorev.setVisibility(View.VISIBLE);
            row_oyun.setVisibility(View.GONE);
            switch_gorev.setEnabled(false);
            switch_oyun.setEnabled(true);
            switch_gorev.setBackgroundResource(R.drawable.yellow_button_press);
            switch_oyun.setBackgroundResource(R.drawable.button_effect);
        }

        switch_gorev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_gorev.setVisibility(View.VISIBLE);
                row_oyun.setVisibility(View.GONE);
                switch_gorev.setEnabled(false);
                switch_oyun.setEnabled(true);
                switch_gorev.setBackgroundResource(R.drawable.yellow_button_press);
                switch_oyun.setBackgroundResource(R.drawable.button_effect);
                editor=sharedPreferences.edit();
                editor.putBoolean("switch",false);
                editor.apply();
            }
        });
        switch_oyun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_gorev.setVisibility(View.GONE);
                row_oyun.setVisibility(View.VISIBLE);
                switch_gorev.setEnabled(true);
                switch_oyun.setEnabled(false);
                switch_oyun.setBackgroundResource(R.drawable.yellow_button_press);
                switch_gorev.setBackgroundResource(R.drawable.button_effect);
                editor=sharedPreferences.edit();
                editor.putBoolean("switch",true);
                editor.apply();
            }
        });
        gorev_topla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog2(3);
                editor=sharedPreferences.edit();
                editor.putInt("gorev_yuzdesi",sharedPreferences.getInt("gorev_yuzdesi",0)-100);
                editor.putInt("banner_tiklama",0);
                editor.putInt("tamekran_tiklama",0);
                jetonsayisi += 3;

                if(sharedPreferences.getInt("gorev_yuzdesi",0)<100){
                    gorev_topla.setEnabled(false);
                }else{
                    gorev_topla.setEnabled(true);
                }
                editor.apply();
                jetonSayisiGuncelle();
                gorev_topla.setEnabled(false);
                gorev_yuzde.setText("%"+String.valueOf(sharedPreferences.getInt("gorev_yuzdesi",0)));
            }
        });



        return view;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AnaMenu.Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface Listener {
        void changeFragment(int id);
    }
    private void hileKontrol(){
        if(getContext()!=null) {
            if (!(android.provider.Settings.Global.getInt(getContext().getContentResolver(), android.provider.Settings.Global.AUTO_TIME_ZONE, 0) == 1 && android.provider.Settings.Global.getInt(getContext().getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0) == 1)) {
                editor=sharedPreferences.edit();
                editor.putFloat("Bakiye",0);
                editor.apply();
                new AlertDialog.Builder(getContext())
                        .setTitle("Saat ve Tarih Hatası!")
                        .setMessage("Telefonunuzun otomatik tarih ve saat ayarlarının aktif olduğundan emin olun. Bu hatayı almanız durumunda bakiyeniz sıfırlanır.")
                        .setCancelable(false)
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                             getActivity().finish();
                            }
                        })

                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }
    private void alertDialog(){
        final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_reklamstack,null);
        TextView kalansure;
        kalansure=(TextView)view.findViewById(R.id.kum_saati);
        int saat,dakika,saNiye;
        String s="";
        s+="KALAN SÜRE: ";
        saat=(TIME())/3600;
        dakika=((TIME())%3600)/60;
        saNiye=(TIME())%60;

        if(TIME()>3600){s+=(String.valueOf(saat)+" SAAT ");}
        if(TIME()>60){s+=(String.valueOf(dakika)+" DAKİKA ");}
        s+=(String.valueOf(saNiye)+" SANİYE ");
        kalansure.setText(s);

        builder.setCancelable(true);
        Button button;
        button=(Button)view.findViewById(R.id.tamam_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        builder.setView(view);
        builder.show();
    }
    private void alertDialog3_yetersizBakiye(){
        final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_yetersiz_jeton,null);
        Button jetonbuton;
        jetonbuton=(Button) view.findViewById(R.id.jeton_buton2);
        jetonbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                cooldownKontrolu();
                if(reklamStack>0) {
                    if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    }else {
                        Toast.makeText(getContext(),"Reklam Henüz Yüklenmedi.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //Toast.makeText(getContext(),String.valueOf(reklamStack), Toast.LENGTH_SHORT).show();
                    alertDialog();
                    //Toast.makeText(getContext(),String.valueOf((sharedPreferences.getInt("CD",(int) System.currentTimeMillis()/1000))+5- ((int)System.currentTimeMillis()/1000)), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setCancelable(true);
        Button button;
        button=(Button)view.findViewById(R.id.tamam_buton2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        builder.setView(view);
        builder.show();
    }
    private void alertDialog2(int kazanilan_jeton){
        final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(getContext()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_jetonwin,null);

        builder.setCancelable(true);
        Button button;
        button=(Button)view.findViewById(R.id.tamam_buton2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        TextView textView;
        textView=(TextView) view.findViewById(R.id.xjetonkazandin);
        textView.setText(String.valueOf(kazanilan_jeton)+" JETON KAZANDIN");

        builder.setView(view);
        builder.show();
    }
    @SuppressLint("DefaultLocale")
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        buton_sol.setText("Bakİye: "+String.valueOf(String.format("%.2f", sharedPreferences.getFloat("Bakiye",0))+"₺"));
        gorev_yuzde.setText("%"+String.valueOf(sharedPreferences.getInt("gorev_yuzdesi",0)));
        if(sharedPreferences.getInt("gorev_yuzdesi",0)<100){
            gorev_topla.setEnabled(false);
        }else{
            gorev_topla.setEnabled(true);
        }
    }
    private void cooldownKontrolu(){
        if(sharedPreferences.getInt("CD",(int) System.currentTimeMillis()/1000)+refresh_suresi<(int) System.currentTimeMillis()/1000){
            reklamStack=4;
        }
    }
    private void jetonSayisiGuncelle(){
        editor=sharedPreferences.edit();
        mid_button.setText("x"+jetonsayisi);
        editor.putInt("JS",jetonsayisi);
        editor.apply();
        setIconInText(mid_button);
    }
    private void setIconInText(Button b){
        b.setTransformationMethod(null);
        int konum=b.getText().toString().indexOf("x");
        SpannableString ss = new SpannableString(b.getText());
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_like);
        d.setBounds(0, 10, b.getLineHeight(),b.getLineHeight()+10);
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        ss.setSpan(span, konum, konum+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        b.setText(ss);
    }
    private void setIconInText2(Button b){
        b.setTransformationMethod(null);
        int konum=b.getText().toString().indexOf("?");
        SpannableString ss = new SpannableString(b.getText());
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_like);
        d.setBounds(0, 10, b.getLineHeight(),b.getLineHeight()+10);
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        ss.setSpan(span, konum, konum+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        b.setText(ss);
    }
    private int TIME(){
        return (sharedPreferences.getInt("CD",(int) System.currentTimeMillis()/1000))+refresh_suresi- ((int)System.currentTimeMillis()/1000);
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        if(reklamStack==0){

        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        //ÖDÜL
        editor = sharedPreferences.edit();
        jetonsayisi =jetonsayisi+(1+rewarded_bonus);
        jetonSayisiGuncelle();
        reklamStack--;
        editor.putInt("Stacks", reklamStack);
        editor.putInt("CD", (int) System.currentTimeMillis() / 1000);
        editor.apply();
        alertDialog2(1+rewarded_bonus);
        rewarded_bonus=0;

        antiHackSAVE(1);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        rewarded_bonus=1;
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-1271658648146831/1847224559",
                new AdRequest.Builder().build());
    }

    private void ipuclariYukle(String kontrol,String baslik,String text){
        if(sharedPreferences.getBoolean(kontrol,true)) {
            final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(getContext()).create();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_ipucu, null);

            builder.setCancelable(true);
            TextView t1, t2;
            t1 = (TextView) view.findViewById(R.id.ipucu_text);
            t2 = (TextView) view.findViewById(R.id.ipucu_text2);
            t1.setText(baslik);
            t2.setText(text);
            Button button;
            button = (Button) view.findViewById(R.id.ipucu_tamam);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    builder.dismiss();
                }
            });
            builder.setView(view);
            builder.show();
        }
        editor=sharedPreferences.edit();
        editor.putBoolean(kontrol,false);
        editor.apply();
    }
    private void antiHackSAVE(final int x){
        databaseReference3_antihack= FirebaseDatabase.getInstance().getReference("antihack");
        id = databaseReference3_antihack.push().getKey();
        if(sharedPreferences.getString("KEY","-").compareTo("-")!=0) {
            id=sharedPreferences.getString("KEY","error333");
        }else{
            editor = sharedPreferences.edit();
            editor.putString("KEY", id);
            editor.apply();
        }
        databaseReference3_antihack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(id).getValue()!=null) {
                    antihack = dataSnapshot.child(id).getValue().toString();
                }else{
                    antihack="0";
                }
                int temp=Integer.valueOf(antihack);
                temp+=x;
                antihack=String.valueOf(temp);
                databaseReference3_antihack.child(id).setValue(antihack);
                databaseReference3_antihack.removeEventListener(this);//THİS NE ALAKA ANLAMADIM AMA BU ACTİVİTYDEKİ LİSTENELERINI SİLİYOR OLABİLİR.
                if(sharedPreferences.getFloat("Bakiye",0)>15f&&Integer.valueOf(antihack)<3){
                    editor=sharedPreferences.edit();
                    editor.putFloat("Bakiye",0);
                    editor.apply();
                }
                if(Integer.valueOf(antihack)>29){
                    ipuclariYukle("mudavim","TEŞEKKÜRLER!","AKTİFLİĞİNİZLE GÜVENİLİR OYUNCULAR LİSTESİNE GİRDİNİZ. ÖDEME ALIRKEN İŞLEMLERİNİZ DAHA HIZLI YAPILACAK.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
