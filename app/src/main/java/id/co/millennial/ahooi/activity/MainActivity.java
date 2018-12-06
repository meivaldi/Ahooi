package id.co.millennial.ahooi.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.helper.SQLiteHandler;
import id.co.millennial.ahooi.helper.SessionManager;

public class MainActivity extends AppCompatActivity {

    private Button main, hadiah, keluar;
    private RelativeLayout login;
    private Dialog dialog;
    private TextView title, nama;
    private ImageView notifikasi;
    private MediaPlayer click, menu;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        nama = (TextView) findViewById(R.id.masok);
        login = (RelativeLayout) findViewById(R.id.login);
        notifikasi = (ImageView) findViewById(R.id.notifikasi);
        main = (Button) findViewById(R.id.main);
        hadiah = (Button) findViewById(R.id.hadiah);
        keluar = (Button) findViewById(R.id.keluar);

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
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                click.start();
            }
        });

        main.setTypeface(typeface);
        hadiah.setTypeface(typeface);
        keluar.setTypeface(typeface);

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.pop_up);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        title = dialog.findViewById(R.id.title);
        title.setTypeface(typeface);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.start();
                startActivity(new Intent(getApplicationContext(), QuestionActivity.class));
                menu.release();
                finish();
            }
        });

        hadiah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.start();
                startActivity(new Intent(getApplicationContext(), HadiahActivity.class));
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
    }

}
