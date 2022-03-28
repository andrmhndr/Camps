package com.example.camps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.camps.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class MainActivityDosen extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ProgressDialog pDialog;
    Button btn_logout;
    TextView txt_id, txt_username, txt_nama, txt_status;
    ListView listView;
    String id, username, nama, status;
    JSONArray dataMahasiswa;
    SharedPreferences sharedPreferences;
    int success;

    ConnectivityManager conMgr;

    private String url = Server.URL + "daftarMahasiswa.php";

    private static final String TAG = Login.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    public static final String TAG_DAFTARMAHASISWA = "daftarMahasiswa";
    public static final String TAG_NIM = "nim";
    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_NAMA = "namaDosen";
    public static final String TAG_NAMAMAHASISWA = "nama";
    public static final String TAG_STATUS = "status";
    public static final String TAG_TUGAS = "tugas";
    public static final String TAG_UTS = "uts";
    public static final String TAG_UAS = "uas";
    public static final String TAG_NILAI = "nilai";

    String tag_json_jobj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dosen);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()){}
            else {
                    Toast.makeText(getApplicationContext(), "no internet connection",
                            Toast.LENGTH_LONG).show();
            }
        }

        username = getIntent().getStringExtra(TAG_USERNAME); //mengambil data username
        tampil(username); //memanggi fungsi tampil

        txt_id = (TextView) findViewById(R.id.txt_id);
        txt_nama = (TextView) findViewById(R.id.txt_nama);
        txt_status = (TextView) findViewById(R.id.txt_status);
        txt_username = (TextView) findViewById(R.id.txt_username);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        sharedPreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update login session ke false dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Login.session_status,false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_USERNAME, null);
                editor.putString(TAG_NAMA,null);
                editor.putString(TAG_STATUS,null);
                editor.commit();

                Intent intent = new Intent(MainActivityDosen.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void tampil(String username) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("wait a sec...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "daftar mahasiswa response : " + response.toString());
                hideDialog();

                ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        nama = jObj.getString(TAG_NAMA);
                        id = jObj.getString(TAG_ID);
                        status = jObj.getString(TAG_STATUS);

                        txt_id.setText("ID : " + id);
                        txt_username.setText("NIP : " + username);
                        txt_nama.setText("Nama : "+ nama);
                        txt_status.setText("Camps : " + status);

                        dataMahasiswa = jObj.getJSONArray(TAG_DAFTARMAHASISWA);
                        for (int i = 0; i < dataMahasiswa.length(); i++) {
                            JSONObject jo = dataMahasiswa.getJSONObject(i);
                            String nim = jo.getString(TAG_NIM);
                            String namaMahasiswa = jo.getString(TAG_NAMAMAHASISWA);
                            String tugas = jo.getString(TAG_TUGAS);
                            String uts = jo.getString(TAG_UTS);
                            String uas = jo.getString(TAG_UAS);
                            String nilai = jo.getString(TAG_NILAI);

                            HashMap<String, String> mahasiswa = new HashMap<>();
                            mahasiswa.put(TAG_NIM, nim);
                            mahasiswa.put(TAG_NAMAMAHASISWA, namaMahasiswa);
                            mahasiswa.put(TAG_TUGAS, tugas);
                            mahasiswa.put(TAG_UTS, uts);
                            mahasiswa.put(TAG_UAS, uas);
                            mahasiswa.put(TAG_NILAI, nilai);
                            list.add(mahasiswa);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(
                        MainActivityDosen.this, list, R.layout.list_mahasiswa,
                        new String[]{TAG_NIM, TAG_NAMAMAHASISWA, TAG_NILAI},
                        new int[]{R.id.txt_nim, R.id.txt_nama, R.id.txt_nilai});

                listView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Tampil Mahasiswa Error "+ error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_jobj);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, TampilMahasiswa.class);
        HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
        String nim = map.get(TAG_NIM).toString();
        String nama = map.get(TAG_NAMAMAHASISWA).toString();
        String tugas = map.get(TAG_TUGAS).toString();
        String uts = map.get(TAG_UTS).toString();
        String uas = map.get(TAG_UAS).toString();
        intent.putExtra(TAG_NIM,nim);
        intent.putExtra(TAG_NAMAMAHASISWA, nama);
        intent.putExtra(TAG_TUGAS, tugas);
        intent.putExtra(TAG_UTS, uts);
        intent.putExtra(TAG_UAS,uas);
        intent.putExtra(TAG_USERNAME, username);
        startActivity(intent);
        finish();
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