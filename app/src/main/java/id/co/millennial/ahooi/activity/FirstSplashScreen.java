package id.co.millennial.ahooi.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.app.AppConfig;
import id.co.millennial.ahooi.app.AppController;

public class FirstSplashScreen extends AppCompatActivity {

    private static final String TAG = FirstSplashScreen.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first_splash_screen);

        checkUpdate();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), SecondSplashScreen.class));
            }
        }, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
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
                        e.printStackTrace();
                    }

                    String versionApk = packageInfo.versionName;

                    if(version.equals(versionApk)){
                        storeApkVersion(versionApk);
                    } else {
                        startActivity(new Intent(getApplicationContext(), UpdateActivity.class));
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
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
}
