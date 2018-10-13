package com.app.cnns.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.cnns.R;
import com.app.cnns.helper.Session;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.model.News;
import com.app.cnns.view.activity.NewsDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Erick Sumargo on 8/31/2016.
 */
public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    private RecyclerView.ViewHolder mainHolder;
    private View itemView;

    private List<News> news;

    public NewsListAdapter(List<News> news) {
        this.news = news;
    }

    public void setData(List<News> news) {
        this.news = news;
        notifyDataSetChanged();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private View item;

        private ImageView image;
        private TextView title, date, thumbUp, thumbDown, comment, views;

        public ItemViewHolder(View view) {
            super(view);

            context = view.getContext();
            item = view.findViewById(R.id.item);
            image = view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);
            date = view.findViewById(R.id.date);
            thumbUp = view.findViewById(R.id.thumb_up);
            thumbDown = view.findViewById(R.id.thumb_down);
            comment = view.findViewById(R.id.comment);
            views = view.findViewById(R.id.views);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_news, parent, false);
        mainHolder = new ItemViewHolder(itemView);

        return mainHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final News n = news.get(position);
        String dateFormat = "EEEE, dd MMM ''yy HH.mm";

        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        Picasso.with(context).load(Utils.with(context).getURLMediaImage(n.getImage(), "news"))
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(itemHolder.image);

        itemHolder.title.setText(n.getTitle());
        itemHolder.date.setText(Utils.with(context).formatDate(n.getDate(), dateFormat) + " WIB");

        itemHolder.thumbUp.setText(String.valueOf(n.getUps()));
        itemHolder.thumbDown.setText(String.valueOf(n.getDowns()));
        itemHolder.comment.setText(String.valueOf(n.getComments().size()));
        itemHolder.views.setText(String.valueOf(n.getViews()));

        itemHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("news_id", n.getId());
                if (Session.with(context).isLogin()) {
                    intent.putExtra("set_viewed", true);
                } else {
                    intent.putExtra("set_viewed", false);
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }
}