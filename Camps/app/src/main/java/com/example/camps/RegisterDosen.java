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

public class RegisterDosen extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btn_register, btn_login;
    EditText txt_password, txt_confirm_password, txt_nip, txt_nama, txt_nTugas, txt_bUts, txt_bUas;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "registerDosen.php";

    private static final String TAG = RegisterDosen.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_dosen);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        //menghidupkan objek
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        txt_password = (EditText) findViewById(R.id.txt_password);
        txt_confirm_password = (EditText) findViewById(R.id.txt_confirm_password);
        txt_nama = (EditText) findViewById(R.id.txt_nama);
        txt_nip = (EditText) findViewById(R.id.txt_nip);
        txt_nTugas = (EditText) findViewById(R.id.txt_bTugas);
        txt_bUts = (EditText) findViewById(R.id.txt_bUts);
        txt_bUas = (EditText) findViewById(R.id.txt_bUas);

        //fungsi tombol login
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // membuat intent login dan masuk ke halaman login
                intent = new Intent(RegisterDosen.this, Login.class);
                finish();
                startActivity(intent);
            }
        });

        //fungsi tombol register
        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // mengambil input dari layout register
                String password = txt_password.getText().toString();
                String confirm_password = txt_confirm_password.getText().toString();
                String nama = txt_nama.getText().toString();
                String nip = txt_nip.getText().toString();
                String bTugas = txt_nTugas.getText().toString();
                String bUts = txt_bUts.getText().toString();
                String bUas = txt_bUas.getText().toString();

                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    //masuk ke fungsi cek register untuk melakukan cek register dan melakukan registrasi
                    checkRegister(password, confirm_password, nama, nip, bTugas, bUts, bUas);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //melakukan pengecekan register dan melakukan register
    private void checkRegister(final String password, final String confirm_password, final String nama,
                               final String nip, final String bTugas, final String bUts, final String bUas) {
        //membuat object dialug yang akan ditampilkan saat proses register
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Register ...");
        showDialog();

        //menyimpan data post ke dalam variabel strReq untuk php
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            //merespon proses registrasi
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
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
                        txt_password.setText("");
                        txt_confirm_password.setText("");
                        txt_nama.setText("");
                        txt_nip.setText("");
                        txt_bUas.setText("");
                        txt_nTugas.setText("");
                        txt_bUts.setText("");

                        Intent intent = new Intent(RegisterDosen.this, Login.class);
                        finish();
                        startActivity(intent);
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
                Log.e(TAG, "Login Error: " + error.getMessage()); //menampilkan pesan error
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // memposting parameter ke register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("password", password);
                params.put("confirm_password", confirm_password);
                params.put("nama", nama);
                params.put("nip", nip);
                params.put("bTugas",bTugas);
                params.put("bUts", bUts);
                params.put("bUas", bUas);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    //fungsi untuk menampilkan tampilan dialog
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //fungsi untuk menyembunyikan tampilan dialog
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //fungsi untuk tombol kembali
    @Override
    public void onBackPressed() {
        intent = new Intent(RegisterDosen.this, Login.class);
        finish();
        startActivity(intent);
    }
}