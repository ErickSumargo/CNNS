package com.app.cnns.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.app.cnns.R;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.model.News;
import com.app.cnns.server.presenter.BasePresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.NewsResponse;
import com.app.cnns.view.adapter.VideoListAdapter;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity implements Response {
    private Toolbar toolbar;
    private RecyclerView videoListView;

    private View noDataView;
    private ProgressBar loadingView;

    private VideoListAdapter videoListAdapter;

    private BasePresenter basePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initView();
        setEvent();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        videoListView = findViewById(R.id.video_list_view);
        noDataView = findViewById(R.id.no_data_view);
        loadingView = findViewById(R.id.loading_view);

        videoListView.setLayoutManager(new LinearLayoutManager(this));

        videoListAdapter = new VideoListAdapter(new ArrayList<News>());
        videoListView.setAdapter(videoListAdapter);

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadVideos();
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onSuccess(BaseResponse base) {
        NewsResponse r = (NewsResponse) base;
        List<News> news = r.getData().getNewsList();

        videoListAdapter.setData(news);
        if (news.size() == 0) {
            noDataView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        } else {
            videoListView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(String message) {
        basePresenter.loadVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}