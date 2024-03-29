package id.co.millennial.ahooi.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.adapter.NewsAdapter;
import id.co.millennial.ahooi.app.AppConfig;
import id.co.millennial.ahooi.app.AppController;
import id.co.millennial.ahooi.helper.SQLiteHandler;
import id.co.millennial.ahooi.helper.SessionManager;
import id.co.millennial.ahooi.model.Berita;
import id.co.millennial.ahooi.question.QuestionOne;
import io.sentry.Sentry;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button main, hadiah, keluar, iya, tidak, share;
    private RelativeLayout login;
    private Dialog dialog, interact;
    private TextView title, nama, label;
    private ImageView notifikasi;
    private MediaPlayer click, menu;

    private SQLiteHandler db;
    private SessionManager session;

    private RecyclerView recyclerView;
    private NewsAdapter beritaAdapter;
    private List<Berita> beritaList;

    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        checkUpdate();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        nama = (TextView) findViewById(R.id.masok);
        login = (RelativeLayout) findViewById(R.id.login);
        notifikasi = (ImageView) findViewById(R.id.notifikasi);
        main = (Button) findViewById(R.id.main);
        hadiah = (Button) findViewById(R.id.hadiah);
        keluar = (Button) findViewById(R.id.keluar);
        share = (Button) findViewById(R.id.share);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.isLoggedIn()) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String shareBodyText = "Main game dapat hadiah? Download aplikasi Ahooi Medan sekarang juga!\n\n" +
                            "https://play.google.com/store/apps/details?id=id.co.millennial.ahooi";
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                    startActivityForResult(Intent.createChooser(intent, "Choose sharing method"), 100);
                } else {
                    Toast.makeText(getApplicationContext(), "Login dulu wak, baru nge share", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!session.isLoggedIn()) {
            logoutUser();
        } else {
            HashMap<String, String> user = db.getUserDetails();
            String name = user.get("name");
            nama.setText(name);
        }

        click = MediaPlayer.create(this, R.raw.click);
        menu = MediaPlayer.create(this, R.raw.gamemenu);
        menu.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                menu.start();
            }
        });
        menu.setLooping(true);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.start();
                flag = true;
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        main.setTypeface(typeface);
        hadiah.setTypeface(typeface);
        keluar.setTypeface(typeface);

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.pop_up);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        beritaList = new ArrayList<>();
        beritaAdapter = new NewsAdapter(this, beritaList);
        recyclerView = dialog.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(beritaAdapter);

        fetchNews();

        title = dialog.findViewById(R.id.title);
        title.setTypeface(typeface);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.start();
                //startActivity(new Intent(getApplicationContext(), QuestionActivity.class));

                int[] array = getRandom(40);

                Intent intent = new Intent(getApplicationContext(), QuestionOne.class);
                intent.putExtra("index", array);
                startActivity(intent);

                menu.release();
                flag = true;
                finish();
            }
        });

        hadiah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.start();
                flag = true;
                startActivity(new Intent(getApplicationContext(), HadiahActivity.class));
            }
        });

        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/GOODDC_.TTF");

                click.start();
                interact = new Dialog(MainActivity.this);
                interact.setContentView(R.layout.dialog);
                interact.setCancelable(true);
                interact.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                interact.show();

                label = interact.findViewById(R.id.label);
                iya = interact.findViewById(R.id.iya);
                tidak = interact.findViewById(R.id.nggak);

                label.setTypeface(tf);
                iya.setTypeface(tf);
                tidak.setTypeface(tf);

                iya.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        interact.dismiss();
                        menu.release();
                        finish();
                    }
                });

                tidak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        interact.dismiss();
                    }
                });
            }
        });

        notifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                click.start();
            }
        });

    }

    private int[] getRandom(int size) {
        int array[] = new int[10];
        ArrayList<Integer> list = new ArrayList<>();

        for(int i=0; i<size; i++) {
            list.add(new Integer(i)+1);
        }

        Collections.shuffle(list);

        for(int i=0; i<10; i++) {
            array[i] = list.get(i);
            Log.d("TES", String.valueOf(array[i]));
        }

        return array;
    }

    private void checkUpdate() {
        String tag_string_req = "req_cek_update";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VERSION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Cek Update Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String version = jObj.getString("version");

                    PackageInfo packageInfo = null;
                    try {
                        packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        Sentry.capture(e);
                    }

                    String versionApk = packageInfo.versionName;

                    if(version.equals(versionApk)){
                        storeApkVersion(versionApk);
                    } else {
                        startActivity(new Intent(getApplicationContext(), UpdateActivity.class));
                        finish();
                    }


                } catch (JSONException e) {
                    Sentry.capture(e);
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Sentry.capture(error);
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void storeApkVersion(final String versionApk) {
        String tag_string_req = "req_store_apk";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_STORE_APK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "APK Response: " + response.toString());

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "APK Error: " + error.getMessage());
                Sentry.capture(error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apk", versionApk);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void fetchNews() {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_NEWS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray jsonArray = jObj.getJSONArray("berita");
                    JSONObject news;
                    String judul, url;

                    for(int i=0; i<jsonArray.length(); i++){
                        news = jsonArray.getJSONObject(i);
                        judul = news.getString("judul");
                        url = news.getString("url");

                        beritaList.add(new Berita(judul, url, true));
                    }

                    beritaAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Sentry.capture(e);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Error: " + error.getMessage());
                Sentry.capture(error);
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100) {
            if(resultCode == RESULT_OK) {
                HashMap<String, String> credentials = db.getUserDetails();
                String id = credentials.get("id");
                int sharePoint = Integer.valueOf(credentials.get("share")) + 1;

                updateSharePoint(id);
                db.updateValue("share", Integer.toString(sharePoint));

            } else if(resultCode == RESULT_CANCELED) {

            }
        }
    }

    private void updateSharePoint(final String id) {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_SHARE_POINT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if(!error) {
                        String msg = jObj.getString("error_msg");
                        Log.d("ERROR", msg);
                    } else {
                        String msg = jObj.getString("error_msg");
                        Log.d("ERROR", msg);
                    }

                } catch (JSONException e) {
                    Sentry.capture(e);
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Error: " + error.getMessage());
                Sentry.capture(error);
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("id", id);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!session.isLoggedIn()){
            nama = (TextView) findViewById(R.id.masok);
            nama.setText("Masok");
        } else {
            HashMap<String, String> user = db.getUserDetails();
            String name = user.get("name");

            nama = (TextView) findViewById(R.id.masok);
            nama.setText(name);
        }

        if(flag){
           flag = false;
        } else {
            menu = MediaPlayer.create(this, R.raw.gamemenu);
            menu.setLooping(true);
            menu.start();
        }
    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();

        nama = (TextView) findViewById(R.id.masok);
        nama.setText("Masok");
    }

    @Override
    protected void onPause() {
        super.onPause();

        dialog.dismiss();

        if(!flag){
            menu.release();
        }

    }

    @Override
    public void onBackPressed() {

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDC_.TTF");

        click.start();
        interact = new Dialog(this);
        interact.setContentView(R.layout.dialog);
        interact.setCancelable(true);
        interact.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        interact.show();

        label = interact.findViewById(R.id.label);
        iya = interact.findViewById(R.id.iya);
        tidak = interact.findViewById(R.id.nggak);

        label.setTypeface(typeface);
        iya.setTypeface(typeface);
        tidak.setTypeface(typeface);

        iya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interact.dismiss();
                menu.release();
                finish();
            }
        });

        tidak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interact.dismiss();
            }
        });

    }
}
