package com.example.camps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.DatagramSocketImpl;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    Button btn_logout;
    TextView txt_id, txt_username, txt_nama, txt_status, txt_dosen, txt_tugas, txt_uts, txt_uas, txt_nilai;
    String id, username, nama, status, namaDosen, tugas, uts, uas, nilai;
    SharedPreferences sharedPreferences;

    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_STATUS = "status";
    public static final String TAG_DOSEN = "namaDosen";
    public static final String TAG_TUGAS = "tugas";
    public static final String TAG_UTS = "uts";
    public static final String TAG_UAS = "uas";
    public static final String TAG_NILAI = "nilai";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_id = (TextView) findViewById(R.id.txt_id);
        txt_nama = (TextView) findViewById(R.id.txt_nama);
        txt_status = (TextView) findViewById(R.id.txt_status);
        txt_username = (TextView) findViewById(R.id.txt_username);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        txt_dosen = (TextView) findViewById(R.id.txt_dosen);
        txt_tugas = (TextView) findViewById(R.id.txt_tugas);
        txt_uts = (TextView) findViewById(R.id.txt_uts);
        txt_uas = (TextView) findViewById(R.id.txt_uas);
        txt_nilai = (TextView) findViewById(R.id.txt_nilai);

        sharedPreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        id = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);
        nama = getIntent().getStringExtra(TAG_NAMA);
        status = getIntent().getStringExtra(TAG_STATUS);
        namaDosen = getIntent().getStringExtra(TAG_DOSEN);
        tugas = getIntent().getStringExtra(TAG_TUGAS);
        uts = getIntent().getStringExtra(TAG_UTS);
        uas = getIntent().getStringExtra(TAG_UAS);
        nilai = getIntent().getStringExtra(TAG_NILAI);

        txt_id.setText("ID : " + id);
        txt_username.setText("NIM : " + username);
        txt_nama.setText("Nama : " + nama);
        txt_status.setText("Camps : " + status);
        txt_dosen.setText("Dosen : " + namaDosen);
        txt_tugas.setText(tugas);
        txt_uts.setText(uts);
        txt_uas.setText(uas);
        txt_nilai.setText(nilai);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update login session ke false dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_USERNAME, null);
                editor.putString(TAG_NAMA, null);
                editor.putString(TAG_STATUS, null);
                editor.putString(TAG_DOSEN, null);
                editor.putString(TAG_TUGAS, null);
                editor.putString(TAG_UTS, null);
                editor.putString(TAG_UAS, null);
                editor.putString(TAG_NILAI, null);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, Login.class);
                finish();
                startActivity(intent);
            }
        });
    }
}