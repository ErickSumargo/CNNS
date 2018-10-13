package com.app.cnns_admin.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.app.cnns_admin.R;
import com.app.cnns_admin.server.interfaces.Response;
import com.app.cnns_admin.server.model.News;
import com.app.cnns_admin.server.presenter.BasePresenter;
import com.app.cnns_admin.server.response.BaseResponse;
import com.app.cnns_admin.server.response.NewsResponse;
import com.app.cnns_admin.view.adapter.NewsListAdapter;
import com.app.cnns_admin.view.adapter.ReporterListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity implements Response {
    private Toolbar toolbar;
    private RecyclerView newsListView;

    private View noDataView;
    private ProgressBar loadingView;

    private ReporterListAdapter reporterListAdapter;

    private BasePresenter basePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

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

        reporterListAdapter = new ReporterListAdapter(new ArrayList<News>());
        newsListView.setAdapter(reporterListAdapter);

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadReportedNews();
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

        reporterListAdapter.setData(news);
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
        basePresenter.loadNews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}