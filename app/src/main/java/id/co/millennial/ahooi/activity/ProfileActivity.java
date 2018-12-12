package id.co.millennial.ahooi.activity;

import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.adapter.NewsAdapter;
import id.co.millennial.ahooi.adapter.PrizeAdapter;
import id.co.millennial.ahooi.app.AppConfig;
import id.co.millennial.ahooi.app.AppController;
import id.co.millennial.ahooi.helper.SQLiteHandler;
import id.co.millennial.ahooi.helper.SessionManager;
import id.co.millennial.ahooi.model.Berita;
import id.co.millennial.ahooi.model.Hadiah;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private RelativeLayout back;
    private Button logout;
    private TextView userName, point, title, label;
    private MediaPlayer click;

    private SQLiteHandler db;
    private SessionManager session;

    private RecyclerView recyclerView, history;
    private PrizeAdapter adapter;
    private List<Hadiah> hadiahList;

    private List<Berita> historyList;
    private NewsAdapter prizeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");

        label = (TextView) findViewById(R.id.label);
        label.setTypeface(typeface);

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(typeface);

        userName = (TextView) findViewById(R.id.user_name);
        point = (TextView) findViewById(R.id.point);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if(!session.isLoggedIn()){
            logoutUser();
        }

        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("long_name");
        String poen = user.get("poin");
        String id = user.get("id");

        userName.setText(name);
        userName.setTypeface(typeface);

        point.setText(poen + " Poen");
        point.setTypeface(typeface);

        click = MediaPlayer.create(this, R.raw.click);
        back = (RelativeLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.start();
                finish();
            }
        });

        logout = (Button) findViewById(R.id.logout);
        logout.setTypeface(typeface);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.start();
                logoutUser();
            }
        });

        hadiahList = new ArrayList<>();
        adapter = new PrizeAdapter(this, hadiahList);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        historyList = new ArrayList<>();
        prizeAdapter = new NewsAdapter(this, historyList);

        recyclerView = (RecyclerView) findViewById(R.id.history_list);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(prizeAdapter);

        loadPrize(id);

    }

    private void loadPrize(final String id) {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_USER_PRIZES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray gambar, nama, id_hadiah, history;

                    id_hadiah = jObj.getJSONArray("id_hadiah");
                    gambar = jObj.getJSONArray("gambar_hadiah");
                    nama = jObj.getJSONArray("title_hadiah");
                    history = jObj.getJSONArray("history");

                    String title, image, id;
                    for(int i=0; i<gambar.length(); i++){
                        title = nama.getString(i);
                        image = gambar.getString(i);
                        id = id_hadiah.getString(i);

                        hadiahList.add(new Hadiah(id, title, image));
                    }

                    String hadiah;
                    for(int i=0; i<history.length(); i++){
                        hadiah = history.getString(i);

                        historyList.add(new Berita(hadiah));
                    }

                    adapter.notifyDataSetChanged();
                    prizeAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Error: " + error.getMessage());
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

    private void logoutUser() {
        click.start();
        session.setLogin(false);
        db.deleteUsers();

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        click.release();
        finish();
    }
}
