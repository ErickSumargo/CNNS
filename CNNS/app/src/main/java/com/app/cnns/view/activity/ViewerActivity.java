package com.app.cnns.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.presenter.BasePresenter;
import com.app.cnns.R;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.model.User;
import com.app.cnns.server.presenter.BasePresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.UserResponse;
import com.app.cnns.view.adapter.ViewerListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewerActivity extends AppCompatActivity implements Response {
    private Toolbar toolbar;
    private RecyclerView userListView;

    private View noDataView;
    private ProgressBar loadingView;

    private ViewerListAdapter viewerListAdapter;

    private BasePresenter basePresenter;

    private int newsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        initView();
        setEvent();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userListView = findViewById(R.id.user_list_view);
        noDataView = findViewById(R.id.no_data_view);
        loadingView = findViewById(R.id.loading_view);

        userListView.setLayoutManager(new LinearLayoutManager(this));

        viewerListAdapter = new ViewerListAdapter(new ArrayList<User>());
        userListView.setAdapter(viewerListAdapter);

        Bundle extras = getIntent().getExtras();
        newsId = extras.getInt("news_id");

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadViewers(newsId);
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
        UserResponse r = (UserResponse) base;
        List<User> users = r.getData().getUsers();

        viewerListAdapter.setData(users);
        if (users.size() == 0) {
            noDataView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        } else {
            userListView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(String message) {
        basePresenter.loadViewers(newsId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}