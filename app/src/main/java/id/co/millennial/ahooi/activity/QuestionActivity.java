package id.co.millennial.ahooi.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.helper.SQLiteHandler;
import id.co.millennial.ahooi.helper.SessionManager;

public class QuestionActivity extends AppCompatActivity {

    private TextView poin, point, label, nama;
    private ProgressBar progress;
    private Handler handler = new Handler();
    private Dialog dialog;
    private Button balek;

    private int width = 100;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_question);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");

        progress = (ProgressBar) findViewById(R.id.progress);
        poin = (TextView) findViewById(R.id.poin);
        poin.setTypeface(typeface);

        dialog = new Dialog(QuestionActivity.this);
        dialog.setContentView(R.layout.game_over);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        balek = (Button) dialog.findViewById(R.id.back);
        balek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        point = (TextView) dialog.findViewById(R.id.point);
        label = (TextView) dialog.findViewById(R.id.label);
        point.setTypeface(typeface);
        label.setTypeface(typeface);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/GOODDC_.TTF");

        TextView enceng = (TextView) dialog.findViewById(R.id.enceng);
        enceng.setTypeface(tf);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");

        nama = (TextView) findViewById(R.id.masok);
        nama.setText(name);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(width > 0){
                    width -= 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progress.setProgress(width);
                        }
                    });

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(width == 0){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();

        nama = (TextView) findViewById(R.id.masok);
        nama.setText("Masok");
    }
}
