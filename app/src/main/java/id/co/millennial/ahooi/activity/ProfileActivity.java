package id.co.millennial.ahooi.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.helper.SQLiteHandler;
import id.co.millennial.ahooi.helper.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private RelativeLayout back;
    private Button logout;
    private TextView userName, point;
    private MediaPlayer click;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");

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
