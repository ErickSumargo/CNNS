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
import com.app.cnns.view.adapter.NewsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity implements Response {
    private Toolbar toolbar;
    private RecyclerView newsListView;

    private View noDataView;
    private ProgressBar loadingView;

    private NewsListAdapter newsListAdapter;

    private BasePresenter basePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initView();
        setEvent();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newsListView = findViewById(R.id.news_list_view);
        noDataView = findViewById(R.id.no_data_view);
        loadingView = findViewById(R.id.loading_view);

        newsListView.setLayoutManager(new LinearLayoutManager(this));

        newsListAdapter = new NewsListAdapter(new ArrayList<News>());
        newsListView.setAdapter(newsListAdapter);

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadReported();
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

        newsListAdapter.setData(news);
        if (news.size() == 0) {
            noDataView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        } else {
            newsListView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(String message) {
        basePresenter.loadReported();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}