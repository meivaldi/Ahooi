package id.co.millennial.ahooi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import id.co.millennial.ahooi.R;
import id.co.millennial.ahooi.activity.MainActivity;
import id.co.millennial.ahooi.model.Berita;

/**
 * Created by root on 11/12/18.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private Context context;
    private List<Berita> beritaList;

    public NewsAdapter(Context context, List<Berita> beritaList) {
        this.context = context;
        this.beritaList = beritaList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.berita_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Berita berita = beritaList.get(position);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/GOODDP_.TTF");

        holder.judul.setTypeface(typeface);
        holder.judul.setText(berita.getJudul());
        holder.news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(berita.isClickable()){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(berita.getUrl()));
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return beritaList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView judul;
        private LinearLayout news;

        public MyViewHolder(View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.judul);
            news = itemView.findViewById(R.id.news);
        }
    }

}
