package com.honorapp.oyunpara;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;


public class ParaCekMenu extends Fragment {
    private Listener mListener;
    private EditText tutar,eposta,telefon,iban;
    private RadioButton c_kontro,c_banka;
    private Button form_gonder;
    private DatabaseReference databaseReference;
    private TableRow t1,t2;
    private CheckBox onay_butonu;
    private Button geri;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String id;
    private SecurePreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_para_cek_menu, container, false);
        preferences = new SecurePreferences(getContext(), "my-preferences", "parolasafak", true);
        tutar=(EditText)view.findViewById(R.id.edit_tutar);
        eposta=(EditText)view.findViewById(R.id.edit_eposta);
        telefon=(EditText)view.findViewById(R.id.edit_telno);
        iban=(EditText)view.findViewById(R.id.edit_iban);
        c_kontro=(RadioButton)view.findViewById(R.id.check_kontor);
        c_banka=(RadioButton)view.findViewById(R.id.check_banka);
        onay_butonu=(CheckBox) view.findViewById(R.id.onay_butonu);
        form_gonder=(Button)view.findViewById(R.id.form_gonder);
        t1=(TableRow)view.findViewById(R.id.t1);
        t2=(TableRow)view.findViewById(R.id.t2);
        t1.setVisibility(View.GONE);
        t2.setVisibility(View.VISIBLE);
        geri=(Button)view.findViewById(R.id.geri2);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        databaseReference= FirebaseDatabase.getInstance().getReference("formlar");
        id = databaseReference.push().getKey();
        if(sharedPreferences.getString("KEY","-").compareTo("-")!=0) {
            id=sharedPreferences.getString("KEY","error333");
        }else{
            editor = sharedPreferences.edit();
            editor.putString("KEY", id);
            editor.apply();
        }
        form_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (isConnected()) {
                        if (tutar.getText().toString().compareTo("") != 0 && eposta.getText().toString().compareTo("") != 0) {
                            if (onay_butonu.isChecked()) {
                                if (Float.parseFloat(tutar.getText().toString()) >= 20f) {
                                    if ((tutar.getText().toString().compareTo("") != 0) && (tutar.getText().toString().compareTo(".") != 0) && eposta.getText().toString().compareTo("") != 0) {
                                        if (Float.parseFloat(tutar.getText().toString()) <= sharedPreferences.getFloat("Bakiye", 0) && Float.parseFloat(tutar.getText().toString()) >= 20f) {
                                            if(Integer.valueOf(preferences.getString("cok_gizli_sey"))>50) {
                                                Form form = new Form(c_kontro.isChecked(), tutar.getText().toString(), eposta.getText().toString(), iban.getText().toString(), telefon.getText().toString());
                                                databaseReference.child(id).setValue(form);
                                                Toast.makeText(view.getContext(), "Form Gönderildi!", Toast.LENGTH_SHORT).show();
                                                editor = sharedPreferences.edit();
                                                float temp = sharedPreferences.getFloat("Bakiye", 0);
                                                editor.putFloat("Bakiye", temp - Float.valueOf(tutar.getText().toString()));
                                                editor.apply();
                                            }else {
                                                Toast.makeText(view.getContext(), "Hile tespit edildi. Hile olmadığını düşünüyorsanız bu kodu mail atınız: "+sharedPreferences.getString("KEY","000"), Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(view.getContext(), "Bakiyeniz yetersiz. ", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(view.getContext(), "Tüm alanları doldurduğunuzdan emin olun. ", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(view.getContext(), "En az 20₺ çekebilirsiniz.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(view.getContext(), "Bilgilerinizi onaylayın.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(view.getContext(), "E-posta ve Tutarı boş bırakamazsınız.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(view.getContext(), "İnternet Bağlantısı Yok", Toast.LENGTH_SHORT).show();
                    }

            }
        });
        c_kontro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(c_kontro.isChecked()){
                    t1.setVisibility(View.GONE);
                    t2.setVisibility(View.VISIBLE);
                    ipuclariYukle("BİLGİLENDİRME","-KONTÖR ÇEKİMİNDE BAKİYENİZİN OPERATÖRÜNÜZÜN MİNİMUM YÜKLEME ÜCRETİNDE OLMASINA DİKKAT EDİNİZ!\n-PAPARA'YA ÇEKME ÜCRETSİZDİR.");

                }else{
                    t2.setVisibility(View.GONE);
                    t1.setVisibility(View.VISIBLE);
                    ipuclariYukle("BİLGİLENDİRME","-BANKA HESABINA ÇEKERKEN İŞLEM ÜCRETİ UYGULANIR.\n-İNİNAL'A ÇEKME ÜCRETSİZDİR.");
                }
            }
        });
        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeFragment(0);
            }
        });
        // Inflate the layout for this fragment

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ParaCekMenu.Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public interface Listener {
        void changeFragment(int id);
    }
    private boolean isConnected() {
        if(getContext()!=null) {
            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
        }
        return false;
    }

    private void ipuclariYukle(String baslik,String text){
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
}
