package com.app.cnns_admin.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.cnns_admin.R;
import com.app.cnns_admin.helper.Utils;
import com.app.cnns_admin.server.model.User;
import com.app.cnns_admin.view.activity.UserActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Erick Sumargo on 8/31/2016.
 */
public class ViewerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    private RecyclerView.ViewHolder mainHolder;
    private View itemView;

    private List<User> users;

    public ViewerListAdapter(List<User> users) {
        this.users = users;
    }

    public void setData(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private View item;

        private CircleImageView photo;
        private TextView name, views;

        public ItemViewHolder(View view) {
            super(view);

            context = view.getContext();
            item = view.findViewById(R.id.item);

            photo = view.findViewById(R.id.photo);
            name = view.findViewById(R.id.name);
            views = view.findViewById(R.id.views);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_views, parent, false);
        mainHolder = new ItemViewHolder(itemView);

        return mainHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final User user = users.get(position);

        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        Picasso.with(context).load(Utils.with(context).getURLMediaImage(user.getPhoto(), "user"))
                .placeholder(R.drawable.avatar)
                .fit()
                .centerCrop()
                .into(itemHolder.photo);
        itemHolder.name.setText(user.getName());
        itemHolder.views.setText(String.valueOf(user.getViews()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}