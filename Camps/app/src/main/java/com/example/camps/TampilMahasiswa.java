package com.example.camps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.camps.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TampilMahasiswa extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btn_update, btn_delete, btn_back;
    EditText txt_nim, txt_nama, txt_tugas, txt_uts, txt_uas;
    String nim, nama, tugas, uts, uas, username;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "tampilMahasiswa.php";

    private static final String TAG = TampilMahasiswa.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String TAG_NIM = "nim";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_TUGAS = "tugas";
    public static final String TAG_UTS = "uts";
    public static final String TAG_UAS = "uas";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tampil_mahasiswa);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            }else {
                Toast.makeText(getApplicationContext(), "no internet connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        username = getIntent().getStringExtra(TAG_USERNAME);
        nim = getIntent().getStringExtra(TAG_NIM);
        nama = getIntent().getStringExtra(TAG_NAMA);
        tugas = getIntent().getStringExtra(TAG_TUGAS);
        uts = getIntent().getStringExtra(TAG_UTS);
        uas = getIntent().getStringExtra(TAG_UAS);

        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_update = (Button) findViewById(R.id.btn_update);
        txt_nama = (EditText) findViewById(R.id.txt_nama);
        txt_nim = (EditText) findViewById(R.id.txt_nim);
        txt_uts = (EditText) findViewById(R.id.txt_uts);
        txt_uas = (EditText) findViewById(R.id.txt_uas);
        txt_tugas = (EditText) findViewById(R.id.txt_tugas);

        txt_nim.setText(nim);
        txt_nama.setText(nama);
        txt_tugas.setText(tugas);
        txt_uts.setText(uts);
        txt_uas.setText(uas);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(TampilMahasiswa.this, MainActivityDosen.class);
                intent.putExtra(TAG_USERNAME, username);
                startActivity(intent);
                finish();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nim = txt_nim.getText().toString();
                String nama = txt_nama.getText().toString();
                String tugas = txt_tugas.getText().toString();
                String uts = txt_uts.getText().toString();
                String uas = txt_uas.getText().toString();

                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()
                        && conMgr.getActiveNetworkInfo().isAvailable()) {
                    checkUpdate(nim, nama,tugas, uts, uas);
                } else {
                    Toast.makeText(getApplicationContext(), "no internet connection",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    checkDelete(nim);
                } else {
                    Toast.makeText(getApplicationContext(), "no internet connection",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkDelete(final String nim) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Deleting...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Delete Response : " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.e("Delete Berhasil", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txt_nim.setText("");
                        txt_nama.setText("");
                        txt_tugas.setText("");
                        txt_uas.setText("");
                        txt_uts.setText("");

                        Intent intent = new Intent(TampilMahasiswa.this, MainActivityDosen.class);
                        intent.putExtra(TAG_USERNAME, username);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Delete Error: "+error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("nim", nim);
                params.put("order", "0");
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void checkUpdate(final String nim, final String nama, final String tugas, final String uts, final String uas) {
        //membuat object dialug yang akan ditampilkan saat proses register
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Updating ...");
        showDialog();

        //menyimpan data post ke dalam variabel strReq untuk php
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            //merespon proses update
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Update Response: " + response.toString());
                hideDialog();
                try {
                    //membuat objek json untuk melakukan response register
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // cek error pada node di json
                    if (success == 1) {

                        //menampilkan pesan register sukses
                        Log.e("Successfully Register!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        //mengosongkan kolom di layout
                        txt_nim.setText("");
                        txt_nama.setText("");
                        txt_tugas.setText("");
                        txt_uts.setText("");
                        txt_uas.setText("");

                        //mengembalikan info dosen
                        Intent intent = new Intent(TampilMahasiswa.this, MainActivityDosen.class);
                        intent.putExtra(TAG_USERNAME, username);
                        startActivity(intent);
                        finish();
                    } else {
                        //menampilkan info bile terjadi kesalahan register yang diambil dari php
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // menampilkan error pada json
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { //membuat response baru bila terjadi error

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage()); //menampilkan pesan error
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // memposting parameter ke register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nim", nim);
                params.put("nama", nama);
                params.put("tugas", tugas);
                params.put("uts", uts);
                params.put("uas", uas);
                params.put("username", username);
                params.put("order", "1");

                return params;
            }

        };

        //menambahkan request ke request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void hideDialog() {
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing()){
            pDialog.show();
        }
    }

    @Override
    public void onBackPressed(){
        intent = new Intent(TampilMahasiswa.this, MainActivityDosen.class);
        intent.putExtra(TAG_USERNAME, username);
        startActivity(intent);
        finish();
    }
}
