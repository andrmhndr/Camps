package com.example.camps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class Login extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btn_registerMahasiswa, btn_login, btn_registerDosen;
    EditText txt_username, txt_password;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "login.php";

    private static final String TAG = Login.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public final static String TAG_USERNAME = "username";
    public final static String TAG_ID = "id";
    public final static String TAG_NAMA = "nama";
    public final static String TAG_STATUS = "status";
    public final static String TAG_DOSEN = "namaDosen";
    public final static String TAG_TUGAS = "tugas";
    public final static String TAG_UTS = "uts";
    public final static String TAG_UAS = "uas";
    public final static String TAG_NILAI = "nilai";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedPreferences;
    boolean session = false;
    String id, username, status, nama, nilai, tugas, uts, uas, namaDosen;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //tes koneksi
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()){
            } else {
                Toast.makeText(getApplicationContext(), "no internet connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        //mengaktifkan objek
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_registerMahasiswa = (Button) findViewById(R.id.btn_registerMahasiswa);
        btn_registerDosen = (Button) findViewById(R.id.btn_registerDosen);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_password = (EditText) findViewById(R.id.txt_password);

        //cek session login apabila true maka akan langsung masuk ke MainActivity
        sharedPreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedPreferences.getBoolean(session_status,false);
        id = sharedPreferences.getString(TAG_ID,null);
        username = sharedPreferences.getString(TAG_USERNAME,null);
        status = sharedPreferences.getString(TAG_STATUS,null);

        if (session) {
            if(status == "Mahasiswa") {
                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra(TAG_ID, id);
                intent.putExtra(TAG_USERNAME, username);
                intent.putExtra(TAG_NAMA, nama);
                intent.putExtra(TAG_STATUS, status);
                intent.putExtra(TAG_DOSEN, namaDosen);
                intent.putExtra(TAG_TUGAS, tugas);
                intent.putExtra(TAG_UTS, uts);
                intent.putExtra(TAG_UAS, uas);
                intent.putExtra(TAG_NILAI, nilai);
                finish();
                startActivity(intent);
            }else if(status == "Dosen") {
                Intent intent = new Intent(Login.this, MainActivityDosen.class);
                intent.putExtra(TAG_ID, id);
                intent.putExtra(TAG_USERNAME, username);
                intent.putExtra(TAG_NAMA, nama);
                intent.putExtra(TAG_STATUS, status);
                finish();
                startActivity(intent);
            }
        }

        //fungsi button login
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String username = txt_username.getText().toString();
                String password = txt_password.getText().toString();

                //melakukan cek pada kolom yang kosong
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null &&
                            conMgr.getActiveNetworkInfo().isAvailable() &&
                            conMgr.getActiveNetworkInfo().isConnected()) {
                        checkLogin(username, password); //memanggil fungsi check login
                    } else  {
                        Toast.makeText(getApplicationContext(), "no internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                }else {
                    //membuat user mnengisi kolom yang kosong
                    Toast.makeText(getApplicationContext(), "kolom tidak boleh kosong",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //fungsi button register mahasiswa
        btn_registerMahasiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                intent = new Intent(Login.this, Register.class);
                finish();
                startActivity(intent);
            }
        });

        btn_registerDosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Login.this, RegisterDosen.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void checkLogin(final String username, final String password) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response : " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    //cek untuk error node di json
                    if (success == 1) { //terbaca sebagai Mahasiswa
                        String username = jObj.getString(TAG_USERNAME);
                        String id = jObj.getString(TAG_ID);
                        String nama = jObj.getString(TAG_NAMA);
                        String status = jObj.getString(TAG_STATUS);
                        String namaDosen = jObj.getString(TAG_DOSEN);
                        String tugas = jObj.getString(TAG_TUGAS);
                        String uts = jObj.getString(TAG_UTS);
                        String uas = jObj.getString(TAG_UAS);
                        String nilai = jObj.getString(TAG_NILAI);

                        Log.e("Login Sukses", jObj.toString());
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        //menyimpan login ke session
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id);
                        editor.putString(TAG_USERNAME, username);
                        editor.putString(TAG_NAMA, nama);
                        editor.putString(TAG_STATUS, status);
                        editor.putString(TAG_DOSEN, namaDosen);
                        editor.putString(TAG_TUGAS, tugas);
                        editor.putString(TAG_UTS, uts);
                        editor.putString(TAG_UAS, uas);
                        editor.putString(TAG_NILAI, nilai);
                        editor.commit();

                        //memanggil main activity
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra(TAG_ID, id);
                        intent.putExtra(TAG_USERNAME, username);
                        intent.putExtra(TAG_NAMA, nama);
                        intent.putExtra(TAG_STATUS, status);
                        intent.putExtra(TAG_DOSEN, namaDosen);
                        intent.putExtra(TAG_TUGAS, tugas);
                        intent.putExtra(TAG_UTS, uts);
                        intent.putExtra(TAG_UAS, uas);
                        intent.putExtra(TAG_NILAI, nilai);
                        finish();
                        startActivity(intent);
                    } else if(success == 2){    //terbaca sebagai Dosen
                        String username = jObj.getString(TAG_USERNAME);

                        Log.e("Login Sukses",jObj.toString());
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_USERNAME, username);
                        editor.commit();

                        Intent intent = new Intent(Login.this, MainActivityDosen.class);
                        intent.putExtra(TAG_USERNAME, username);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //json error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "login error" + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter untuk login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

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

}
