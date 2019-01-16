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
import id.co.millennial.ahooi.adapter.HadiahAdapter;
import id.co.millennial.ahooi.app.AppConfig;
import id.co.millennial.ahooi.app.AppController;
import id.co.millennial.ahooi.helper.SQLiteHandler;
import id.co.millennial.ahooi.model.Answer;
import id.co.millennial.ahooi.model.Hadiah;
import id.co.millennial.ahooi.model.Question;

public class HadiahActivity extends AppCompatActivity {

    private static final String TAG = HadiahActivity.class.getSimpleName();

    private RelativeLayout back;
    private MediaPlayer click;
    private TextView title;

    private RecyclerView recyclerView;
    private HadiahAdapter hadiahAdapter;
    private List<Hadiah> hadiahList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.hadiah);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(typeface);

        hadiahList = new ArrayList<>();
        hadiahAdapter = new HadiahAdapter(this, hadiahList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(hadiahAdapter);

        click = MediaPlayer.create(this, R.raw.click);

        back = (RelativeLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.start();
                finish();
            }
        });

        SQLiteHandler db = new SQLiteHandler(this);
        HashMap<String, String> user = db.getUserDetails();
        String id = user.get("id");

        if(id == null) {
            id = " ";
        }

        getHadiah(id);
    }

    private void getHadiah(final String id) {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_PRIZE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Response: " + response.toString());

                try {
                    JSONArray jArr = new JSONArray(response);
                    int size = jArr.length();

                    JSONObject jObj;
                    String id, hadiah, point, image;
                    for(int i=0; i<size; i++){
                        jObj = jArr.getJSONObject(i);
                        id = jObj.getString("id");
                        hadiah = jObj.getString("hadiah");
                        point = jObj.getString("point");
                        image = jObj.getString("image");

                        hadiahList.add(new Hadiah(id, hadiah, point, image));
                    }

                    hadiahAdapter.notifyDataSetChanged();

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
                Map<String, String> params = new HashMap<>();
                params.put("id", id);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onPause() {
        super.onPause();
        click.release();

        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        click.release();

        finish();
    }
}
