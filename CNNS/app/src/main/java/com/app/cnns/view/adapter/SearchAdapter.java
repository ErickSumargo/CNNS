package com.app.cnns.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.cnns.R;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.model.News;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends CursorAdapter {
    private View item;
    private CircleImageView photo;
    private TextView title;

    private List<News> news;

    public SearchAdapter(Context context, Cursor cursor, List<News> news) {
        super(context, cursor, false);

        this.news = news;
    }

    public void update(List<News> news) {
        this.news = news;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final News n = news.get(cursor.getPosition());

        Picasso.with(context).load(Utils.with(context).getURLMediaImage(n.getUser().getPhoto(), "user"))
                .placeholder(R.drawable.avatar)
                .fit()
                .centerCrop()
                .into(photo);
        title.setText(news.get(cursor.getPosition()).getTitle());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_search, parent, false);

        item = view.findViewById(R.id.item);
        photo = view.findViewById(R.id.photo);
        title = view.findViewById(R.id.title);

        return view;
    }
}