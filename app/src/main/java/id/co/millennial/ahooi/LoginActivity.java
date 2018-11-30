package id.co.millennial.ahooi;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private TextView label1, label2;
    private Button login, daftar;
    private EditText emailET, passwordET;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/GOODDC_.TTF");

        label1 = (TextView) findViewById(R.id.label1);
        label2 = (TextView) findViewById(R.id.label2);

        daftar = (Button) findViewById(R.id.daftar);
        login = (Button) findViewById(R.id.login);

        emailET = (EditText) findViewById(R.id.email);
        passwordET = (EditText) findViewById(R.id.password);

        label1.setTypeface(typeface);
        label2.setTypeface(typeface);

        daftar.setTypeface(typeface);
        login.setTypeface(typeface);

        emailET.setTypeface(typeface);

        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.game_over);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView enceng = (TextView) dialog.findViewById(R.id.enceng);
        TextView point = (TextView) dialog.findViewById(R.id.point);
        Button ulangi = (Button) dialog.findViewById(R.id.restart);
        Button balek = (Button) dialog.findViewById(R.id.back);

        enceng.setTypeface(tf);
        point.setTypeface(typeface);
        ulangi.setTypeface(typeface);
        balek.setTypeface(typeface);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QuestionActivity.class));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        dialog.dismiss();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        dialog.dismiss();
        finish();
    }
}
