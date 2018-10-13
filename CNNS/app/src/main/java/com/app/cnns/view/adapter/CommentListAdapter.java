package com.app.cnns.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.cnns.R;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.model.Comment;
import com.app.cnns.server.model.News;
import com.app.cnns.server.model.User;
import com.app.cnns.view.activity.CommentActivity;
import com.app.cnns.view.activity.NewsDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Erick Sumargo on 8/31/2016.
 */
public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    private RecyclerView.ViewHolder mainHolder;
    private View itemView;

    private User user;
    private List<Comment> comments;

    public CommentListAdapter(User user, List<Comment> comments) {
        this.user = user;
        this.comments = comments;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView photo;
        private TextView name, date, content;

        private LinearLayout likeCont;
        private ImageView thumbUp, thumbDown;
        private TextView ups, downs;

        private int like;

        public ItemViewHolder(View view) {
            super(view);

            context = view.getContext();
            photo = view.findViewById(R.id.photo);
            name = view.findViewById(R.id.name);
            date = view.findViewById(R.id.date);
            content = view.findViewById(R.id.content);

            likeCont = view.findViewById(R.id.like_container);
            thumbUp = view.findViewById(R.id.thumb_up);
            thumbDown = view.findViewById(R.id.thumb_down);
            ups = view.findViewById(R.id.ups);
            downs = view.findViewById(R.id.downs);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_comment, parent, false);
        mainHolder = new ItemViewHolder(itemView);

        return mainHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        String dateFormat = "EEEE, dd MMM ''yy HH.mm";

        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        Picasso.with(context).load(Utils.with(context).getURLMediaImage(comment.getUser().getPhoto(), "user"))
                .placeholder(R.drawable.avatar)
                .fit()
                .centerCrop()
                .into(itemHolder.photo);

        itemHolder.name.setText(comment.getUser().getName());
        itemHolder.date.setText(Utils.with(context).formatDate(comment.getCreatedAt(), dateFormat) + " WIB");
        itemHolder.content.setText(comment.getContent());

        if (comment.getId() == -1) {
            itemHolder.likeCont.setVisibility(View.GONE);
        } else {
            itemHolder.likeCont.setVisibility(View.VISIBLE);

            itemHolder.like = comment.getLike();
            if (itemHolder.like != -1) {
                if (itemHolder.like == 1) {
                    itemHolder.thumbUp.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                    itemHolder.thumbDown.clearColorFilter();
                } else {
                    itemHolder.thumbUp.clearColorFilter();
                    itemHolder.thumbDown.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                }
            } else {
                itemHolder.thumbUp.clearColorFilter();
                itemHolder.thumbDown.clearColorFilter();
            }
            itemHolder.ups.setText(String.valueOf(comment.getUps()));
            itemHolder.downs.setText(String.valueOf(comment.getDowns()));

            itemHolder.thumbUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user != null) {
                        if (itemHolder.like == -1) {
                            itemHolder.thumbUp.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                            itemHolder.ups.setText(String.valueOf(Integer.valueOf(itemHolder.ups.getText().toString()) + 1));

                            itemHolder.like = 1;
                        } else if (itemHolder.like == 0) {
                            itemHolder.thumbUp.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                            itemHolder.ups.setText(String.valueOf(Integer.valueOf(itemHolder.ups.getText().toString()) + 1));

                            itemHolder.thumbDown.clearColorFilter();
                            itemHolder.downs.setText(String.valueOf(Integer.valueOf(itemHolder.downs.getText().toString()) - 1));

                            itemHolder.like = 1;
                        }
                        ((CommentActivity) context).setCommentLike(comment.getId(), 1);
                    } else {
                        showErrorDialog("Harap login terlebih dahulu");
                    }
                }
            });

            itemHolder.thumbDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user != null) {
                        if (itemHolder.like == -1) {
                            itemHolder.thumbDown.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                            itemHolder.downs.setText(String.valueOf(Integer.valueOf(itemHolder.downs.getText().toString()) + 1));

                            itemHolder.like = 0;
                        } else if (itemHolder.like == 1) {
                            itemHolder.thumbUp.clearColorFilter();
                            itemHolder.ups.setText(String.valueOf(Integer.valueOf(itemHolder.ups.getText().toString()) - 1));

                            itemHolder.thumbDown.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                            itemHolder.downs.setText(String.valueOf(Integer.valueOf(itemHolder.downs.getText().toString()) + 1));

                            itemHolder.like = 0;
                        }
                        ((CommentActivity) context).setCommentLike(comment.getId(), 0);
                    } else {
                        showErrorDialog("Harap login terlebih dahulu");
                    }
                }
            });
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Kesalahan")
                .setMessage(message)
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}