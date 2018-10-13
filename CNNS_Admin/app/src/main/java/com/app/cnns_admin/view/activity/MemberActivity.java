package com.app.cnns_admin.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.cnns_admin.R;
import com.app.cnns_admin.helper.Constant;
import com.app.cnns_admin.helper.Internet;
import com.app.cnns_admin.server.interfaces.Response;
import com.app.cnns_admin.server.model.User;
import com.app.cnns_admin.server.presenter.BasePresenter;
import com.app.cnns_admin.server.response.BaseResponse;
import com.app.cnns_admin.server.response.MainResponse;
import com.app.cnns_admin.view.adapter.MemberListAdapter;
import com.app.cnns_admin.view.adapter.UserListAdapter;
import com.app.cnns_admin.view.custom.ProgressDialogCustom;

import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends AppCompatActivity implements Response {
    private Toolbar toolbar;
    private RecyclerView userListView;

    private View noDataView;
    private ProgressBar loadingView;

    private MemberListAdapter memberListAdapter;

    private BasePresenter basePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

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

        memberListAdapter = new MemberListAdapter(new ArrayList<User>());
        userListView.setAdapter(memberListAdapter);

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadUsers();
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
        MainResponse r = (MainResponse) base;
        List<User> users = r.getData().getUsers();

        memberListAdapter.setData(users);
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
        basePresenter.loadMembers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}