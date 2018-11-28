package id.co.millennial.ahooi;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private float progressStatus = 0;
    private TextView textView;
    private CircularProgressBar circularProgressBar;

    private int time = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.timer);
        circularProgressBar = (CircularProgressBar) findViewById(R.id.custom_progressBar);
        circularProgressBar.setProgress(50.0f);

        /*progressBar = (ProgressBar) findViewById(R.id.progressBar);*/
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 3.3f;
                    handler.post(new Runnable() {
                        public void run() {
                            circularProgressBar.setProgress(progressStatus);
                            textView.setText("" + time--);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (progressStatus >= 99.0f) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Waktu Habis!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).start();

    }
}
