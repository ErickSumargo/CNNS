package com.app.cnns.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.app.cnns.R;
import com.app.cnns.helper.Constant;
import com.app.cnns.helper.Session;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.model.Comment;
import com.app.cnns.server.model.News;
import com.app.cnns.server.model.User;
import com.app.cnns.view.activity.CommentActivity;
import com.app.cnns.view.activity.FullScreenVideoActivity;
import com.app.cnns.view.activity.NewsDetailActivity;
import com.glide.slider.library.svg.GlideApp;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Erick Sumargo on 8/31/2016.
 */
public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    private RecyclerView.ViewHolder mainHolder;
    private View itemView;

    private List<News> news;

    public VideoListAdapter(List<News> news) {
        this.news = news;
    }

    public void setData(List<News> news) {
        this.news = news;
        notifyDataSetChanged();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private View item;
        private ImageView thumbnail;
        private CircleImageView photo;
        private TextView name, ups, downs, comments, views;

        public ItemViewHolder(View view) {
            super(view);

            context = view.getContext();
            item = view.findViewById(R.id.item);
            thumbnail = view.findViewById(R.id.thumbnail);
            photo = view.findViewById(R.id.photo);
            name = view.findViewById(R.id.name);
            ups = view.findViewById(R.id.ups);
            downs = view.findViewById(R.id.downs);
            comments = view.findViewById(R.id.comments);
            views = view.findViewById(R.id.views);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_video, parent, false);
        mainHolder = new ItemViewHolder(itemView);

        return mainHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final News n = news.get(position);

        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        Picasso.with(context).load(Utils.with(context).getURLMediaImage(n.getUser().getPhoto(), "user"))
                .placeholder(R.drawable.avatar)
                .fit()
                .centerCrop()
                .into(itemHolder.photo);
        itemHolder.name.setText(n.getUser().getName());

        itemHolder.ups.setText(String.valueOf(n.getUps()));
        itemHolder.downs.setText(String.valueOf(n.getDowns()));
        itemHolder.comments.setText(String.valueOf(n.getComments().size()));
        itemHolder.views.setText(String.valueOf(n.getViews()));

        final String url = Constant.URL_MEDIA_VIDEO + n.getVideo();
        GlideApp.with(context)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.black)
                .centerCrop()
                .into(itemHolder.thumbnail);

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

        itemHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenVideoActivity.class);
                intent.putExtra("url", url);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }
}