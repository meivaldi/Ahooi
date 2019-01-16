package id.co.millennial.ahooi.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.activity.QuestionActivity;
import id.co.millennial.ahooi.app.AppConfig;
import id.co.millennial.ahooi.app.AppController;
import id.co.millennial.ahooi.helper.SQLiteHandler;
import id.co.millennial.ahooi.helper.SessionManager;
import id.co.millennial.ahooi.model.Answer;
import id.co.millennial.ahooi.model.Hadiah;
import id.co.millennial.ahooi.model.Question;

/**
 * Created by root on 08/12/18.
 */

public class HadiahAdapter extends RecyclerView.Adapter<HadiahAdapter.MyViewHolder> {

    private static final String TAG = HadiahAdapter.class.getSimpleName();

    private Context context;
    private List<Hadiah> hadiahList;

    private SQLiteHandler db;
    private SessionManager session;
    private HashMap<String, String> user;

    private Dialog dialog;
    private TextView congrats, title;

    public HadiahAdapter(Context context, List<Hadiah> hadiahList) {
        this.context = context;
        this.hadiahList = hadiahList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hadiah_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Hadiah hadiah = hadiahList.get(position);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/GOODDP_.TTF");

        holder.ambil.setTypeface(typeface);
        holder.nama.setTypeface(typeface);
        holder.point.setTypeface(typeface);

        holder.nama.setText(hadiah.getNama());
        holder.point.setText(hadiah.getPoint());

        Glide.with(context)
                .load(hadiah.getFoto())
                .override(200, 200)
                .into(holder.foto);

        db = new SQLiteHandler(context);
        session = new SessionManager(context);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.congratulation);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        congrats = dialog.findViewById(R.id.greetings);
        congrats.setTypeface(typeface);

        title = dialog.findViewById(R.id.title);
        title.setTypeface(typeface);

        holder.ambil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.isLoggedIn()){
                    user = db.getUserDetails();
                    String id = user.get("id");
                    String email = user.get("email");

                    int poen = Integer.valueOf(hadiah.getPoint());
                    int pt = Integer.valueOf(user.get("poin"));

                    if(pt >= poen) {
                        congrats.setText("Selamat lah wak ya, dapat " + hadiah.getNama() + " kau!");
                        dialog.show();

                        if(pt > 0){
                            int value = pt-poen;
                            if(value < 0)
                                value = pt;

                            db.updateValue("poin", Integer.toString(value));
                        }

                        getPrize(id, hadiah.getId());
                        removeItem(position);

                    } else {
                        Toast.makeText(context, "Poen kau nggak cukopp!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "Login dulu wak!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeItem(int position){
        hadiahList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, hadiahList.size());
    }

    private void getPrize(final String id_user, final String id_hadiah) {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CLAIM_PRIZE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String message = jObj.getString("error_msg");
                        Log.d(TAG, message);
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Log.d(TAG, errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", id_user);
                params.put("id_hadiah", id_hadiah);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public int getItemCount() {
        return hadiahList.size();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder{
        private Button ambil;
        private ImageView foto;
        private TextView nama, point;

        public MyViewHolder(View itemView) {
            super(itemView);
            ambil = itemView.findViewById(R.id.ambil);
            foto = itemView.findViewById(R.id.price_image);
            nama = itemView.findViewById(R.id.price_name);
            point = itemView.findViewById(R.id.point);
        }
    }

}
