package com.honorapp.oyunpara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements
        AnaMenu.Listener , ParaCekMenu.Listener,ParaHareketleriMenu.Listener,YardimMenu.Listener,DurdurmaOyunu.Listener,SlotOyunu.Listener,ParaToplamaOyunu.Listener{
    public int a,firstWeekCounter,odul;
    private SharedPreferences sharedPreferences;
    private int refresh_firstweek = 64800;
    private SharedPreferences.Editor editor;
    private boolean firstEnter=true;
    private InterstitialAd mInterstitialAd;
    public static boolean fragmentNo;
    AdView mAdView;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReference3;
    private String temp;
    private int guvenlik;
    private String versiyon_kontrol="1";
    private SecurePreferences preferences;
    private boolean tamekran_bugu=true;
    @Override
    public void onBackPressed() {
        if(!fragmentNo) {
            changeFragment(0);
        }else{
            Toast.makeText(this,"Oyunu terk edemezsiniz.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        firstEnter=sharedPreferences.getBoolean("FirstEnter",true);
        MobileAds.initialize(this, "ca-app-pub-1271658648146831~6285628631");
        preferences = new SecurePreferences(this, "my-preferences", "parolasafak", true);

        if(this!=null) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-1271658648146831/9725714575");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                tamekran_bugu=true;
            }

            @Override
            public void onAdLeftApplication() {
                if(sharedPreferences.getInt("tamekran_tiklama",0)<9&&tamekran_bugu){
                    tamekran_bugu=false;
                    editor=sharedPreferences.edit();
                    editor.putInt("gorev_yuzdesi",sharedPreferences.getInt("gorev_yuzdesi",0)+10);
                    editor.putInt("tamekran_tiklama",sharedPreferences.getInt("tamekran_tiklama",0)+1);
                    editor.apply();
                    gorevAlertDialog("Görev Başarılı!","Görevde %10 ilerlediniz.");
                }
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Fragment newFragment;
        newFragment = new AnaMenu();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container2, newFragment);
        a=0;
        transaction.addToBackStack(null);
        transaction.commit();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        firstWeekCounter=sharedPreferences.getInt("FW",1);
        if((sharedPreferences.getInt("FW_CD",(int) System.currentTimeMillis()/1000)+refresh_firstweek<=(int) System.currentTimeMillis()/1000)||firstEnter){
            if(firstWeekCounter<6) {
                alertDialog();
                editor = sharedPreferences.edit();
                editor.putInt("FW_CD", (int) System.currentTimeMillis() / 1000);
                editor.putBoolean("FirstEnter", false);
                editor.apply();
            }
        }

        ipuclariYukle("hosgeldiniz_ipucu","OYUNMATİK'E HOŞGELDİNİZ","Burada her ŞEY çok BASİT. Reklam İZLEYİP jeton toplayın ve oyun oynayıp para kazanın.\nİYİ Şanslar!");



        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLeftApplication(){
                if(sharedPreferences.getInt("banner_tiklama",0)<2){
                    editor=sharedPreferences.edit();
                    editor.putInt("gorev_yuzdesi",sharedPreferences.getInt("gorev_yuzdesi",0)+5);
                    editor.putInt("banner_tiklama",sharedPreferences.getInt("banner_tiklama",0)+1);
                    editor.apply();
                    gorevAlertDialog("Görev Başarılı!","Görevde %5 ilerlediniz.");
                }
            }
        });

        if(preferences.getString("cok_gizli_sey")==null){
            preferences.put("cok_gizli_sey", "1");
        }
        if(sharedPreferences.getFloat("Bakiye",0)>1f) {
            databaseReference= FirebaseDatabase.getInstance().getReference("informations");
            String id = databaseReference.push().getKey();
            if(sharedPreferences.getString("KEY","-").compareTo("-")!=0) {
                id=sharedPreferences.getString("KEY","error333");
            }else{
                editor = sharedPreferences.edit();
                editor.putString("KEY", id);
                editor.apply();
            }
            Form form = new Form(true, String.valueOf(sharedPreferences.getFloat("Bakiye",0))+"-M:"+String.valueOf(sharedPreferences.getInt("ANAMENU_COUNTER",0))+"-Safe:"+String.valueOf(preferences.getString("cok_gizli_sey")), String.format("%.3f",((float)sharedPreferences.getFloat("oyun1",0)/sharedPreferences.getInt("oyun1_2",0))), String.format("%.3f",((float)sharedPreferences.getFloat("oyun2",0)/sharedPreferences.getInt("oyun2_2",0))), String.format("%.3f",((float)sharedPreferences.getFloat("oyun3",0)/sharedPreferences.getInt("oyun3_2",0))));
            databaseReference.child(id).setValue(form);
        }


        databaseReference2= FirebaseDatabase.getInstance().getReference().child("versiyon");

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                versiyon_kontrol = dataSnapshot.child("v").getValue().toString();
                guncellemeKont("GÜNCELLEME HATASI","LÜTFEN YENİ GÜNCELLEŞTİRMEYİ İNDİRİNİZ.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                guncellemeKont("İNTERNET BAĞLANTISI YOK","LÜTFEN İNTERNET BAĞLANTINIZI KONTROL EDİNİZ.");
            }
        });


        databaseReference3= FirebaseDatabase.getInstance().getReference().child("paragonderme");
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = databaseReference3.push().getKey();
                if(sharedPreferences.getString("KEY","-").compareTo("-")!=0) {
                    id=sharedPreferences.getString("KEY","error333");
                }else{
                    editor = sharedPreferences.edit();
                    editor.putString("KEY", id);
                    editor.apply();
                }
                if(dataSnapshot.child(id).getValue()!=null) {
                    temp = dataSnapshot.child(id).getValue().toString();
                    float a = Float.valueOf(temp);
                    editor = sharedPreferences.edit();
                    editor.putFloat("Bakiye", sharedPreferences.getFloat("Bakiye", 0) + a);
                    editor.apply();
                    databaseReference3.child(id).removeValue();
                    ipuclariYukle("BAKİYENİZ GÜNCELLENDİ", "UYGULAMAYI KAPATIP AÇINCA BAKİYENİZ YENİLENECEK.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
    private void guncellemeKont(String s,String s2){
        if(BuildConfig.VERSION_CODE<Integer.valueOf(String.valueOf(versiyon_kontrol))){
            ipuclariYukle(s,s2);
        }else if(Integer.valueOf(String.valueOf(versiyon_kontrol))==0){
            s="BAKIM AŞAMASINZDAYIZ";
            s2="YENİ GÜNCELLEME GELMEKTEDİR. MAKSİMUM 2 saat İÇİNDE GÜNCELLEYEBİLİRSİNİZ.";
            ipuclariYukle(s,s2);
        }

    }
    public void alertDialog(){
        //final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        final AlertDialog builder = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_firstweek,null);
        TextView g1,g2,g3,g4,g5;
        Button button;
        g1=(TextView)view.findViewById(R.id.ap_1);
        g2=(TextView)view.findViewById(R.id.ap_2);
        g3=(TextView)view.findViewById(R.id.ap_3);
        g4=(TextView)view.findViewById(R.id.ap_4);
        g5=(TextView)view.findViewById(R.id.ap_5);
        button=(Button)view.findViewById(R.id.odultopla_button);
        setIconInText(g1);
        setIconInText(g2);
        setIconInText(g3);
        setIconInText(g4);
        setIconInText(g5);

        switch (firstWeekCounter){
            case 1:
                g1.setTextColor(Color.parseColor("#2BD004"));
                odul=6;
                break;
            case 2:
                g2.setTextColor(Color.parseColor("#2BD004"));
                odul=8;
                break;
            case 3:
                g3.setTextColor(Color.parseColor("#2BD004"));
                odul=10;
                break;
            case 4:
                g4.setTextColor(Color.parseColor("#2BD004"));
                odul=11;
                break;
            case 5:
                g5.setTextColor(Color.parseColor("#2BD004"));
                odul=12;
                break;
            default:
                    break;
        }


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        firstWeekCounter++;
        editor.putInt("FW",firstWeekCounter);
        int temp=sharedPreferences.getInt("JS",10);
        editor.putInt("JS",temp+odul);
        editor.apply();
        builder.setView(view);
        builder.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    @Override
    public void changeFragment(int id){
        if (id == 0) {
            if (mInterstitialAd.isLoaded()&&(fragmentNo)) {
            mInterstitialAd.show();
            }
            guvenlik=Integer.valueOf(preferences.getString("cok_gizli_sey"));
            guvenlik++;
            preferences.put("cok_gizli_sey", String.valueOf(guvenlik));

            fragmentNo=false;
            Fragment newFragment;
            newFragment = new AnaMenu();
            a=0;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container2, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            if(sharedPreferences.getInt("ANAMENU_COUNTER",0)==7){
                ipuclariYukle("istatistik_ipucu","BİLGİLENDİRME","SOL ÜSTTE BAKİYENE BASARAK İSTATİSTİKLERİNE ERİŞEBİLİRSİN.\nSANA EN ÇOK KAZANDIRAN OYUNU TESPİT ET!");
            }
            if(sharedPreferences.getInt("ANAMENU_COUNTER",0)==22){
                ipuclariYukle("istatistik_ipucu2","HATIRLATMA","SOL ÜSTTE BAKİYENE BASARAK İSTATİSTİKLERİNE ERİŞEBİLİRSİN.\nSANA EN ÇOK KAZANDIRAN OYUNU TESPİT ET!");
            }
            if(sharedPreferences.getInt("ANAMENU_COUNTER",0)==15){
                ipuclariYukle("para_cekme","BİLGİLENDİRME","PARA ÇEKERKEN SORUN YAŞARSAN MAİL YOLUYLA BİZE ULAŞABİLİRSİN.");
            }
            if(sharedPreferences.getInt("ANAMENU_COUNTER",0)==11){
                ipuclariYukle("reklam_hatirlatma","BİLGİLENDİRME","GÜNLÜK REKLAM İZLEME LİMİTİN OLDUĞUNU UNUTMA.\nHER GÜN LİMİTİN DOLDURURSAN DAHA ÇOK KAZANIRSIN!");
            }
            if(sharedPreferences.getInt("ANAMENU_COUNTER",0)==17){
                ipuclariYukle("gorev_hatirlatma","BİLGİLENDİRME","GÖREVLERİ HER TAMAMLAYIŞINDA MAX DEĞERLERİ SIFIRLANIR.");
            }
            if(sharedPreferences.getInt("ANAMENU_COUNTER",0)==30){
                ipuclariYukle("gorev_hatirlatma2","BİLGİLENDİRME","GÖREVLERİ HER TAMAMLAYIŞINDA MAX DEĞERLERİ SIFIRLANIR.");
            }
            if(sharedPreferences.getInt("ANAMENU_COUNTER",0)==14){
                ipuclariYukle("gorev_hatirlatma3","BİLGİLENDİRME","OYUN SONLARINDAKİ REKLAMA TIKLAYARAK GÖREVLERDE İLERLEYEBİLİRSİN.");
            }
            if(sharedPreferences.getInt("ANAMENU_COUNTER",0)==2){
                ipuclariYukle("rewarded_ipucu2","BİLGİLENDİRME","REKLAM İZLERKEN VİDEOYU İZLEDİĞİN İÇİN 1 JETON, VİDEOYA TIKLADIĞIN İÇİN DE EKSTRA 1 JETON KAZANIRSIN.");
            }
            if(sharedPreferences.getInt("ANAMENU_COUNTER",0)==19){
                ipuclariYukle("rewarded_ipucu3","BİLGİLENDİRME","REKLAM İZLERKEN VİDEOYU İZLEDİĞİN İÇİN 1 JETON, VİDEOYA TIKLADIĞIN İÇİN DE EKSTRA 1 JETON KAZANIRSIN.");
            }
            editor=sharedPreferences.edit();
            editor.putInt("ANAMENU_COUNTER",sharedPreferences.getInt("ANAMENU_COUNTER",0)+1);
            editor.apply();

        }else if (id == 1) {
            Fragment newFragment;
            newFragment = new ParaCekMenu();
            a=1;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container2, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }else if (id == 2) {
            Fragment newFragment;
            newFragment = new ParaHareketleriMenu();
            a=1;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container2, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }else if (id == 3) {
            Fragment newFragment;
            newFragment = new YardimMenu();
            a=1;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container2, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }else if (id == 4) {
            ipuclariYukle("Durdurma_oyunu","HAFIZA OYUNU","LAMBALARIN YANMA SIRASINI AKLINDA TUT VE AYNISINI YAP. GİTTİKÇE ZORLAŞACAK.\nİYİ ŞANSLAR!");
            Fragment newFragment;
            newFragment = new DurdurmaOyunu();
            a=1;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container2, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            fragmentNo=true;
        }if (id == 5) {
            ipuclariYukle("Kasa_oyunu","SANDIK AÇMA","SANDIKLARI AÇ VE ÖDÜLLERİ KAP.\nBOŞ YOK!");
            Fragment newFragment;
            newFragment = new ParaToplamaOyunu();
            a=1;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container2, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            fragmentNo=true;

        }if (id == 6) {
            ipuclariYukle("slot_oyunu","SLOT MAKİNESİ","KOLU ÇEK VE ÖDÜLLERİN GELMESİNİ BEKLE.\nBOŞ YOK!");
            Fragment newFragment;
            newFragment = new SlotOyunu();
            a=1;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container2, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            fragmentNo=true;
        }
    }
    public void setIconInText(TextView b){
        b.setTransformationMethod(null);
        int konum=b.getText().toString().indexOf("x");
        SpannableString ss = new SpannableString(b.getText());
        Drawable d = ContextCompat.getDrawable(this, R.drawable.ic_like);
        d.setBounds(0, 10, b.getLineHeight(),b.getLineHeight()+10);
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        ss.setSpan(span, konum, konum+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        b.setText(ss);
    }
    private int TIME(){
        return (sharedPreferences.getInt("FW_CD",(int) System.currentTimeMillis()/1000))+ refresh_firstweek - ((int)System.currentTimeMillis()/1000);
    }


    private void ipuclariYukle(String kontrol,String baslik,String text){
        if(sharedPreferences.getBoolean(kontrol,true)) {
            final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
            View view = LayoutInflater.from(this).inflate(R.layout.layout_ipucu, null);

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
    private void gorevAlertDialog(String text1,String text2){

            final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
            View view = LayoutInflater.from(this).inflate(R.layout.layout_gorev, null);

            builder.setCancelable(true);
            TextView t1, t2;
            t1 = (TextView) view.findViewById(R.id.gorev_text1);
            t2 = (TextView) view.findViewById(R.id.gorev_text2);
            t1.setText(text1);
            t2.setText(text2);
            Button button;
            button = (Button) view.findViewById(R.id.gorev_tamam);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    builder.dismiss();
                }
            });
            builder.setView(view);
            builder.show();

    }
    private void ipuclariYukle(String baslik,String text){

            final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
            View view = LayoutInflater.from(this).inflate(R.layout.layout_ipucu, null);

            builder.setCancelable(false);
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
                    finish();
                }
            });
            builder.setView(view);
        if(!(this).isFinishing())
        {
            builder.show();
        }

    }


}
