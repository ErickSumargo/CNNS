package com.app.cnns_admin.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.app.cnns_admin.view.adapter.UserListAdapter;
import com.app.cnns_admin.view.custom.ProgressDialogCustom;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements Response {
    private Toolbar toolbar;
    private RecyclerView userListView;

    private View noDataView;
    private ProgressBar loadingView;
    private ProgressDialogCustom progressDialog;

    private UserListAdapter userListAdapter;

    private BasePresenter basePresenter;

    private int userId;
    private boolean loadingUser = true;

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

        userListAdapter = new UserListAdapter(new ArrayList<User>());
        userListView.setAdapter(userListAdapter);

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

    public void blockUser(int id) {
        userId = id;

        progressDialog = new ProgressDialogCustom(this);
        progressDialog.setMessage("Memblokir user...");

        if (Internet.isConnected(this)) {
            basePresenter.blockUser(id);
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(BaseResponse base) {
        if (base.getTag().equals(Constant.TAG_LOAD)) {
            MainResponse r = (MainResponse) base;
            List<User> users = r.getData().getUsers();

            userListAdapter.setData(users);
            if (users.size() == 0) {
                noDataView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
            } else {
                userListView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
            }
            loadingUser = false;
        } else if (base.getTag().equals(Constant.TAG_BLOCK)) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Toast.makeText(this, "User berhasil diblokir", Toast.LENGTH_SHORT).show();

            loadingUser = true;
            basePresenter.loadUsers();
        }
    }

    @Override
    public void onFailure(String message) {
        if (loadingUser) {
            basePresenter.loadUsers();
        } else {
            basePresenter.blockUser(userId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}