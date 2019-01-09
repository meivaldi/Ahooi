package id.co.millennial.ahooi.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.app.AppConfig;
import id.co.millennial.ahooi.app.AppController;
import id.co.millennial.ahooi.helper.SQLiteHandler;
import id.co.millennial.ahooi.helper.SessionManager;
import id.co.millennial.ahooi.model.Answer;
import id.co.millennial.ahooi.model.Question;

public class QuestionActivity extends AppCompatActivity implements Runnable {

    private static final String TAG = QuestionActivity.class.getSimpleName();

    private TextView poin, point, label, nama, checkpoint, betol, congratulation, congrats_point;
    private ProgressBar progress;
    private Handler handler = new Handler();
    private Dialog dialog, benar, congrats;
    private Button balek, ulangi, kembali;

    private int width = 100;
    private int user_point = 0;
    private boolean isRunning = true;

    private SQLiteHandler db;
    private SessionManager session;
    private MediaPlayer end, click, music;

    private LinearLayout answer1, answer2, answer3, answer4;
    private TextView a, b, c, d;
    private TextView question, question_point;
    private RelativeLayout check;

    private List<Question> questionList;

    private int INDEX = 0;
    private boolean right = false;
    private boolean inQuestion = false;

    private AdView adView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_question);

        MobileAds.initialize(this, "ca-app-pub-3364138612972741~4746456309");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3364138612972741/4921685274");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        loadQuestion();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/GOODDC_.TTF");

        question = (TextView) findViewById(R.id.pertanyaan);
        question_point = (TextView) findViewById(R.id.question_point);
        checkpoint = (TextView) findViewById(R.id.checkpoint);
        checkpoint.setTypeface(typeface);

        check = (RelativeLayout) findViewById(R.id.check);
        check.setVisibility(View.GONE);

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
        music = MediaPlayer.create(this, R.raw.inquestion);
        music.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                music.start();
            }
        });
        music.setLooping(true);

        progress = (ProgressBar) findViewById(R.id.progress);
        poin = (TextView) findViewById(R.id.poin);
        poin.setTypeface(typeface);

        benar = new Dialog(QuestionActivity.this);
        benar.setContentView(R.layout.benar);
        benar.setCancelable(false);
        benar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        betol = benar.findViewById(R.id.benar);
        betol.setTypeface(tf);

        dialog = new Dialog(QuestionActivity.this);
        dialog.setContentView(R.layout.game_over);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        congrats = new Dialog(QuestionActivity.this);
        congrats.setContentView(R.layout.win);
        congrats.setCancelable(false);
        congrats.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        kembali = congrats.findViewById(R.id.kembali);
        kembali.setTypeface(typeface);

        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    startActivity(new Intent(getApplicationContext(), QuestionActivity.class));
                    finish();
                }
            }
        });

        congrats_point = congrats.findViewById(R.id.congrats_point);
        congrats_point.setTypeface(typeface);

        congratulation = congrats.findViewById(R.id.congrats);
        congratulation.setTypeface(tf);

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

        balek = (Button) dialog.findViewById(R.id.back);
        balek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inQuestion = true;
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    startActivity(new Intent(getApplicationContext(), QuestionActivity.class));
                    finish();
                }
            }
        });

        point = (TextView) dialog.findViewById(R.id.point);
        label = (TextView) dialog.findViewById(R.id.label);
        point.setTypeface(typeface);
        label.setTypeface(typeface);

        questionList = new ArrayList<>();

        TextView enceng = (TextView) dialog.findViewById(R.id.enceng);
        enceng.setTypeface(tf);

        ulangi = (Button) dialog.findViewById(R.id.restart);
        ulangi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.isLoggedIn()) {
                    restart();
                } else {
                    Toast.makeText(getApplicationContext(), "Login dulu wak!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("FLAG", true);
                    startActivity(intent);
                }
            }
        });

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer1.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_answer));
                freeze();
                if (questionList.get(INDEX).getAnswerList().get(0).isValue()) {
                    right = true;
                    click = MediaPlayer.create(QuestionActivity.this, R.raw.click);
                    click.start();

                    check.setVisibility(View.GONE);
                } else {
                    right = false;
                }
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer2.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_answer));
                freeze();
                if (questionList.get(INDEX).getAnswerList().get(1).isValue()) {
                    right = true;
                    click = MediaPlayer.create(QuestionActivity.this, R.raw.click);
                    click.start();

                    check.setVisibility(View.GONE);

                } else {
                    right = false;
                }
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer3.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_answer));
                freeze();
                if (questionList.get(INDEX).getAnswerList().get(2).isValue()) {
                    right = true;
                    click = MediaPlayer.create(QuestionActivity.this, R.raw.click);
                    click.start();

                    check.setVisibility(View.GONE);

                } else {
                    right = false;
                }
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer4.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_answer));
                freeze();
                if (questionList.get(INDEX).getAnswerList().get(3).isValue()) {
                    right = true;
                    click = MediaPlayer.create(QuestionActivity.this, R.raw.click);
                    click.start();

                    check.setVisibility(View.GONE);

                } else {
                    right = false;
                }
            }
        });

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                if(session.isLoggedIn()) {
                    HashMap<String, String> user = db.getUserDetails();
                    String email = user.get("email");

                    updatePoint(Integer.toString(user_point), email);
                    music.release();
                    congrats.dismiss();
                    dialog.dismiss();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else if (inQuestion) {
                    music.release();
                    congrats.dismiss();
                    dialog.dismiss();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                }
            }
        });

    }

    private void freeze() {
        answer1.setClickable(false);
        answer2.setClickable(false);
        answer3.setClickable(false);
        answer4.setClickable(false);
    }

    private void unfreeze() {
        answer1.setClickable(true);
        answer2.setClickable(true);
        answer3.setClickable(true);
        answer4.setClickable(true);
    }

    private void updatePoint(final String point, final String email) {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_POINT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        HashMap<String, String> user = db.getUserDetails();
                        String email = user.get("email");
                        int pt = Integer.valueOf(user.get("poin"));
                        int poen = Integer.valueOf(point);

                        db.updateValue("poin", email, Integer.toString((poen) + pt));
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
                params.put("point", point);
                params.put("email", email);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void restart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                user_point -= 30;
                if (user_point < 0)
                    user_point = 0;

                poin.setText(user_point + "");
                check.setVisibility(View.VISIBLE);
            }
        });

        hideDialog();
        width = 100;
        setRunning(true);
        setToDefault();
        unfreeze();
        new Thread(this).start();
    }

    private void startQuestion(int i) {
        Question qs = questionList.get(i);
        List<Answer> jb = qs.getAnswerList();

        question.setText(qs.getQuestion());
        question_point.setText(qs.getPoint());
        a.setText(jb.get(0).getAnswer());
        b.setText(jb.get(1).getAnswer());
        c.setText(jb.get(2).getAnswer());
        d.setText(jb.get(3).getAnswer());

    }

    private void loadQuestion() {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_RANDOM_QUESTION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONArray question = jObj.getJSONArray("pertanyaan");
                        JSONArray answers;

                        String pertanyaan, poin, jawaban;
                        boolean value;

                        JSONObject quest_obj;

                        for (int i = 0; i < question.length(); i++) {
                            quest_obj = question.getJSONObject(i);
                            pertanyaan = quest_obj.getString("pertanyaan");
                            poin = quest_obj.getString("poin");
                            answers = quest_obj.getJSONArray("jawaban");

                            List<Answer> answerList = new ArrayList<>();
                            for (int j = 0; j < answers.length(); j++) {
                                jawaban = answers.getJSONObject(j).getString("jawaban");
                                value = answers.getJSONObject(j).getBoolean("status");
                                answerList.add(new Answer(jawaban, value));
                            }

                            questionList.add(new Question(pertanyaan, poin, answerList));
                        }

                        startQuestion(INDEX);
                        new Thread(QuestionActivity.this).start();
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
        });

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

        end.release();
        click.release();
        music.release();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        music.release();
        end.release();
        click.release();
        isRunning = false;
        congrats.dismiss();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        Toast.makeText(getApplicationContext(), "Ahh lemahla kau!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showDialog() {
        if (!dialog.isShowing())
            dialog.show();

        end = MediaPlayer.create(this, R.raw.game_over);
        end.start();
        music.release();
    }

    private void hideDialog() {
        if (dialog.isShowing())
            dialog.dismiss();

        music = MediaPlayer.create(this, R.raw.inquestion);
        music.start();
        end.release();
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    @Override
    public void run() {
        while (width > 0 && isRunning) {
            width -= 1;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progress.setProgress(width);
                }
            });

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Toast.makeText(getApplicationContext(), "Bentar wak", Toast.LENGTH_SHORT).show();
            }

            if (width == 1 && right) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        benar.show();
                    }
                });

                music.release();
                final MediaPlayer congrat = MediaPlayer.create(this, R.raw.nextquestion);
                congrat.start();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                congrat.release();
                music = MediaPlayer.create(this, R.raw.inquestion);
                music.start();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        benar.dismiss();
                        width = 100;
                        point.setText(Integer.toString(user_point));
                        user_point += Integer.valueOf(questionList.get(INDEX).getPoint());
                        INDEX++;
                        poin.setText("" + user_point);
                        right = false;
                        setToDefault();
                        unfreeze();

                        if (INDEX < 10)
                            startQuestion(INDEX);
                        else {
                            congrats_point.setText(Integer.toString(user_point));
                            isRunning = false;
                            congrats.show();
                        }
                    }
                });
            } else if (width == 1 && !right) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        point.setText(Integer.toString(user_point));
                        showDialog();
                    }
                });
            }

            if (width == 0 && !right) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        point.setText(Integer.toString(user_point));
                        showDialog();
                    }
                });
            }
        }
    }

    private void setToDefault() {
        answer1.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_answer));
        answer2.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_answer));
        answer3.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_answer));
        answer4.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_answer));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!session.isLoggedIn()) {
            nama = (TextView) findViewById(R.id.masok);
            nama.setText("Masok");
        } else {
            HashMap<String, String> user = db.getUserDetails();
            String name = user.get("name");

            nama = (TextView) findViewById(R.id.masok);
            nama.setText(name);
        }
    }

}
