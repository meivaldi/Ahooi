package id.co.millennial.ahooi.activity;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import id.co.millennial.ahooi.R;

public class HadiahActivity extends AppCompatActivity {

    private Button ambil;
    private TextView point, title, price_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.hadiah);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");

        price_name = (TextView) findViewById(R.id.price_name);
        ambil = (Button) findViewById(R.id.ambil);
        point = (TextView) findViewById(R.id.point);
        title = (TextView) findViewById(R.id.title);

        ambil.setTypeface(typeface);
        point.setTypeface(typeface);
        title.setTypeface(typeface);
        price_name.setTypeface(typeface);
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
}
