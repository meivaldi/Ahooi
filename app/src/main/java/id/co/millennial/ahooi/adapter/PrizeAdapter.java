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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.app.AppConfig;
import id.co.millennial.ahooi.app.AppController;
import id.co.millennial.ahooi.model.Berita;
import id.co.millennial.ahooi.model.Hadiah;

/**
 * Created by root on 10/12/18.
 */

public class PrizeAdapter extends RecyclerView.Adapter<PrizeAdapter.CustomViewHolder> {

    private static final String TAG = PrizeAdapter.class.getSimpleName();

    private Context context;
    private List<Hadiah> hadiahList;

    private Dialog dialog;
    private TextView congrats, title;

    public PrizeAdapter(Context context, List<Hadiah> hadiahList) {
        this.context = context;
        this.hadiahList = hadiahList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        if(hadiahList.size() == 0){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.nothing, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.prize_item, parent, false);
        }

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        final Hadiah hadiah = hadiahList.get(position);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/GOODDP_.TTF");

        holder.prizeTitle.setText(hadiah.getNama());
        holder.prizeTitle.setTypeface(typeface);

        Glide.with(context)
                .load(hadiah.getFoto())
                .override(100, 100)
                .into(holder.prizeImage);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.congratulation);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        congrats = dialog.findViewById(R.id.greetings);
        congrats.setTypeface(typeface);

        title = dialog.findViewById(R.id.title);
        title.setTypeface(typeface);

        holder.confirm.setTypeface(typeface);
        holder.background.setTypeface(typeface);
        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                congrats.setText("Dikonfirmasi");
                dialog.show();
                removeItem(position);
                confirm(hadiah.getId());
            }
        });
    }

    private void confirm(final String id) {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CONFIRM, new Response.Listener<String>() {

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
                params.put("id", id);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public int getItemCount() {
        return hadiahList.size();
    }

    public void removeItem(int position){
        hadiahList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, hadiahList.size());
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private Button confirm, background;
        private ImageView prizeImage;
        private TextView prizeTitle;

        public CustomViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            confirm = itemView.findViewById(R.id.konfirmasi);
            prizeImage = itemView.findViewById(R.id.price_image);
            prizeTitle = itemView.findViewById(R.id.price_name);
        }

    }

}
