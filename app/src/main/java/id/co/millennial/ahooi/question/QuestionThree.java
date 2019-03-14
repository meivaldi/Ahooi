package id.co.millennial.ahooi.question;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
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
import id.co.millennial.ahooi.activity.LoginActivity;
import id.co.millennial.ahooi.activity.MainActivity;
import id.co.millennial.ahooi.activity.RegisterActivity;
import id.co.millennial.ahooi.app.AppConfig;
import id.co.millennial.ahooi.app.AppController;
import id.co.millennial.ahooi.helper.SQLiteHandler;
import id.co.millennial.ahooi.helper.SessionManager;
import id.co.millennial.ahooi.model.Answer;
import id.co.millennial.ahooi.model.Question;
import io.sentry.Sentry;

public class QuestionThree extends AppCompatActivity implements Runnable {

    private TextView checkpoint_label, user_point, question, a, b, c, d, betol, nama;
    private RelativeLayout checkpoint;
    private ProgressBar progressBar;
    private ProgressDialog pDialog;
    private Dialog dialog, benar;

    private LinearLayout answer1, answer2, answer3, answer4;
    private Handler handler;

    private Typeface typeface;
    private Typeface tf;

    private Question questionModel;

    private int width = 100;
    private int usr_point;
    private int[] array;
    private boolean right;
    private boolean isRunning;
    private boolean inQuestion;

    private SQLiteHandler db;
    private SessionManager session;

    private Button balek, ulangi;
    private TextView dialog_point, label, final_point;

    private InterstitialAd mInterstitialAd;
    private MediaPlayer play, congrats, click, game_over;
    private boolean isPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_question_three);

        MobileAds.initialize(this, "ca-app-pub-3364138612972741~4746456309");
        handler = new Handler();

        init();
    }

    private void init() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/GOODDP_.TTF");
        tf = Typeface.createFromAsset(getAssets(), "fonts/GOODDC_.TTF");

        click = MediaPlayer.create(this, R.raw.click);
        congrats = MediaPlayer.create(this, R.raw.nextquestion);
        play = MediaPlayer.create(this, R.raw.inquestion);
        game_over = MediaPlayer.create(this, R.raw.game_over);

        play.setLooping(true);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3364138612972741/4921685274");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                if(session.isLoggedIn()) {
                    HashMap<String, String> user = db.getUserDetails();
                    String email = user.get("email");

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else if (inQuestion) {
                    dialog.dismiss();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                }
            }
        });

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

        isRunning = true;

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        dialog = new Dialog(QuestionThree.this);
        dialog.setContentView(R.layout.game_over);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog_point = (TextView) dialog.findViewById(R.id.point);
        label = (TextView) dialog.findViewById(R.id.label);
        final_point = (TextView) dialog.findViewById(R.id.point);

        dialog_point.setTypeface(typeface);
        label.setTypeface(typeface);
        final_point.setTypeface(typeface);

        TextView enceng = (TextView) dialog.findViewById(R.id.enceng);
        enceng.setTypeface(tf);

        ulangi = (Button) dialog.findViewById(R.id.restart);
        balek = (Button) dialog.findViewById(R.id.back);

        ulangi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.isLoggedIn()) {
                    usr_point -= 30;
                    if (usr_point < 0)
                        usr_point = 0;

                    Intent intent = new Intent(getApplicationContext(), QuestionThree.class);
                    intent.putExtra("index", array);
                    intent.putExtra("checkpoint", true);
                    intent.putExtra("user_point", usr_point);
                    startActivity(intent);
                    if(dialog.isShowing())
                        dialog.dismiss();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Login dulu wak!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("FLAG", true);
                    startActivity(intent);
                }
            }
        });

        balek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inQuestion = true;
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();

                    HashMap<String, String> user = db.getUserDetails();
                    String email = user.get("email");

                    updatePoint(Integer.toString(usr_point), email);
                    dialog.dismiss();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    HashMap<String, String> user = db.getUserDetails();
                    String email = user.get("email");

                    updatePoint(Integer.toString(usr_point), email);
                    dialog.dismiss();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });

        benar = new Dialog(QuestionThree.this);
        benar.setContentView(R.layout.benar);
        benar.setCancelable(false);
        benar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        betol = benar.findViewById(R.id.benar);
        betol.setTypeface(tf);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        checkpoint_label = (TextView) findViewById(R.id.checkpoint);
        user_point = (TextView) findViewById(R.id.poin);

        usr_point = getIntent().getIntExtra("user_point", 0);
        user_point.setText(String.valueOf(usr_point));

        answer1 = (LinearLayout) findViewById(R.id.answer1);
        answer2 = (LinearLayout) findViewById(R.id.answer2);
        answer3 = (LinearLayout) findViewById(R.id.answer3);
        answer4 = (LinearLayout) findViewById(R.id.answer4);

        a = (TextView) findViewById(R.id.a);
        b = (TextView) findViewById(R.id.b);
        c = (TextView) findViewById(R.id.c);
        d = (TextView) findViewById(R.id.d);

        question = (TextView) findViewById(R.id.pertanyaan);

        checkpoint = (RelativeLayout) findViewById(R.id.check);
        checkpoint.setVisibility(View.GONE);

        checkpoint_label.setTypeface(tf);
        user_point.setTypeface(tf);

        boolean chk = getIntent().getBooleanExtra("checkpoint", false);

        if(chk) {
            checkpoint.setVisibility(View.VISIBLE);
        } else {
            checkpoint.setVisibility(View.GONE);
        }

        progressBar.setProgress(width);

        array = getIntent().getIntArrayExtra("index");
        String index = String.valueOf(array[2]);

        loadQuestion(index);
    }

    private void updatePoint(final String point, final String email) {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_POINT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Update", "Get Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        HashMap<String, String> user = db.getUserDetails();
                        int pt = Integer.valueOf(user.get("poin"));
                        int poen = Integer.valueOf(point);

                        db.updateValue("poin", Integer.toString((poen) + pt));
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Sentry.capture(e);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Update", "Get Error: " + error.getMessage());
                Sentry.capture(error);
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

    private void startQuestion() {
        question.setText(questionModel.getQuestion());

        final List<Answer> answerList = questionModel.getAnswerList();
        a.setText(answerList.get(0).getAnswer());
        b.setText(answerList.get(1).getAnswer());
        c.setText(answerList.get(2).getAnswer());
        d.setText(answerList.get(3).getAnswer());

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer1.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_answer));
                click.start();
                freeze();
                if (answerList.get(0).isValue()) {
                    right = true;
                } else {
                    right = false;
                }
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer2.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_answer));
                click.start();
                freeze();
                if (answerList.get(1).isValue()) {
                    right = true;
                } else {
                    right = false;
                }
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer3.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_answer));
                click.start();
                freeze();
                if (answerList.get(2).isValue()) {
                    right = true;
                } else {
                    right = false;
                }
            }
        });

        answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer4.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_answer));
                click.start();
                freeze();
                if (answerList.get(3).isValue()) {
                    right = true;
                } else {
                    right = false;
                }
            }
        });

        new Thread(this).start();
    }

    private void freeze() {
        answer1.setClickable(false);
        answer2.setClickable(false);
        answer3.setClickable(false);
        answer4.setClickable(false);
    }

    private void loadQuestion(final String index) {
        String tag_string_req = "req_login";

        pDialog.setMessage("Tunggu Sebentar...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_QUESTION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Question", "Get Response: " + response.toString());
                pDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String pertanyaan = jObj.getString("pertanyaan");
                        String point = jObj.getString("point");
                        JSONArray answers = jObj.getJSONArray("jawaban");
                        JSONArray status = jObj.getJSONArray("status");

                        List<Answer> answerList = new ArrayList<>();

                        for(int i=0; i<answers.length(); i++) {
                            answerList.add(new Answer(answers.getString(i), status.getBoolean(i)));
                        }

                        questionModel = new Question(pertanyaan, point, answerList);
                        startQuestion();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Sentry.capture(e);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Question", "Get Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Tidak ada koneksi internet!", Toast.LENGTH_LONG).show();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                pDialog.dismiss();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("id", index);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isRunning = false;
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        Toast.makeText(getApplicationContext(), "Ahh lemahla kau!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void run() {
        while (width > 0) {
            width -= 1;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(width);
                }
            });

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Bentar wak", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if(width == 0 && isRunning) {
                if(right) {
                    play.release();

                    if (isPlaying)
                        congrats.start();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            benar.show();
                        }
                    });

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Sentry.capture(e);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            benar.dismiss();
                        }
                    });

                    usr_point += 10;

                    Intent intent = new Intent(getApplicationContext(), QuestionFour.class);
                    intent.putExtra("user_point", usr_point);
                    intent.putExtra("index", array);
                    startActivity(intent);

                    finish();
                } else {
                    play.release();

                    if(isPlaying)
                        game_over.start();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final_point.setText(String.valueOf(usr_point));
                            dialog.show();
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            play.pause();
        } catch (IllegalStateException e) {
            Sentry.capture(e);
            play.release();
        }

        isPlaying = false;

        click.release();
        congrats.release();
        game_over.release();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            play.start();
        } catch (IllegalStateException e) {
            Sentry.capture(e);
            play.release();
        }

        isPlaying = true;

        click = MediaPlayer.create(this, R.raw.click);
        congrats = MediaPlayer.create(this, R.raw.nextquestion);
        game_over = MediaPlayer.create(this, R.raw.game_over);
    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();

        nama = (TextView) findViewById(R.id.masok);
        nama.setText("Masok");
    }
}