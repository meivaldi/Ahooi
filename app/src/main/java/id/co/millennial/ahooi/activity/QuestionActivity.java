package id.co.millennial.ahooi.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.app.AppConfig;
import id.co.millennial.ahooi.app.AppController;
import id.co.millennial.ahooi.helper.SQLiteHandler;
import id.co.millennial.ahooi.helper.SessionManager;
import id.co.millennial.ahooi.model.Answer;
import id.co.millennial.ahooi.model.Question;

public class QuestionActivity extends AppCompatActivity {

    private static final String TAG = QuestionActivity.class.getSimpleName();

    private TextView poin, point, label, nama;
    private ProgressBar progress;
    private Handler handler = new Handler();
    private Dialog dialog;
    private Button balek;

    private int width = 100;

    private SQLiteHandler db;
    private SessionManager session;
    private MediaPlayer end, click;

    private int MIN = 1;
    private int MAX = 10;
    private int user_point = 0;

    private LinearLayout answer1, answer2, answer3, answer4;
    private TextView a, b, c, d;
    private TextView question, question_point;

    private Question pertanyaan;
    private Answer[] answer;
    private Thread tr;
    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_question);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");

        question = (TextView) findViewById(R.id.pertanyaan);
        question_point = (TextView) findViewById(R.id.question_point);

        answer1 = (LinearLayout) findViewById(R.id.answer1);
        answer2 = (LinearLayout) findViewById(R.id.answer2);
        answer3 = (LinearLayout) findViewById(R.id.answer3);
        answer4 = (LinearLayout) findViewById(R.id.answer4);

        a = (TextView) findViewById(R.id.a);
        b = (TextView) findViewById(R.id.b);
        c = (TextView) findViewById(R.id.c);
        d = (TextView) findViewById(R.id.d);

        end = MediaPlayer.create(this, R.raw.game_over);
        click = MediaPlayer.create(this, R.raw.click);

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
        } else {
            HashMap<String, String> user = db.getUserDetails();
            String name = user.get("name");

            nama = (TextView) findViewById(R.id.masok);
            nama.setText(name);
        }

        tr = new Thread(new Runnable() {
            @Override
            public void run() {
                while(width > 0 && isRunning){
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
                        Toast.makeText(getApplicationContext(), "Bentar wak", Toast.LENGTH_SHORT).show();
                    }

                    if(width == 0){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                end.start();
                                point.setText(Integer.toString(user_point));
                                dialog.show();
                            }
                        });
                    }
                }
            }
        });

        Random r = new Random();
        int id = r.nextInt(MAX - MIN + 1) + MIN;

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;
                click.start();

                if(answer[0].isValue()){
                    benar(answer1);
                    refresh(answer1);
                } else {
                    answer1.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.false_answer));
                    point.setText(Integer.toString(user_point));
                    dialog.show();
                }
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;
                click.start();

                if(answer[1].isValue()){
                    benar(answer2);
                    refresh(answer2);
                } else {
                    answer2.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.false_answer));
                    point.setText(Integer.toString(user_point));
                    dialog.show();
                }
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;
                click.start();

                if(answer[2].isValue()){
                    benar(answer3);
                    refresh(answer3);
                } else {
                    answer3.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.false_answer));
                    point.setText(Integer.toString(user_point));
                    dialog.show();
                }
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;
                click.start();

                if(answer[3].isValue()){
                    benar(answer4);
                    refresh(answer4);
                } else {
                    answer4.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.false_answer));
                    point.setText(Integer.toString(user_point));
                    dialog.show();
                }
            }
        });

        loadQuestion(Integer.toString(id));
    }

    private void refresh(LinearLayout answer) {
        answer.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_answer));
        answer1.setClickable(true);
        answer2.setClickable(true);
        answer3.setClickable(true);
        answer4.setClickable(true);
        isRunning = true;
        width = 100;
    }

    private void benar(LinearLayout answer) {
        user_point += Integer.valueOf(pertanyaan.getPoint());
        poin.setText(Integer.toString(user_point));
        answer.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.right_answer));
        answer1.setClickable(false);
        answer2.setClickable(false);
        answer3.setClickable(false);
        answer4.setClickable(false);
    }

    private void loadQuestion(final String id) {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_QUESTION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        answer = new Answer[4];
                        JSONArray jawaban = jObj.getJSONArray("jawaban");
                        JSONArray status = jObj.getJSONArray("status");
                        int size = jawaban.length();

                        for(int i=0; i<size; i++){
                            answer[i] = new Answer(jawaban.getString(i), status.getBoolean(i));
                        }

                        pertanyaan = new Question(jObj.getString("pertanyaan"), jObj.getString("point"), answer);

                        question.setText(pertanyaan.getQuestion());
                        question_point.setText(pertanyaan.getPoint());
                        a.setText(pertanyaan.getAnswer(0).getAnswer());
                        b.setText(pertanyaan.getAnswer(1).getAnswer());
                        c.setText(pertanyaan.getAnswer(2).getAnswer());
                        d.setText(pertanyaan.getAnswer(3).getAnswer());

                        tr.start();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
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
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
        end.stop();
        click.stop();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        dialog.dismiss();
        end.stop();
        click.stop();
        finish();
    }
}
