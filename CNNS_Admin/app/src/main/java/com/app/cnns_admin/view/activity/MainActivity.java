package com.app.cnns_admin.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.cnns_admin.R;
import com.app.cnns_admin.helper.Session;
import com.app.cnns_admin.server.interfaces.Response;
import com.app.cnns_admin.server.model.User;
import com.app.cnns_admin.server.presenter.BasePresenter;
import com.app.cnns_admin.server.response.BaseResponse;
import com.app.cnns_admin.server.response.MainResponse;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Response {
    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView views;
    private View navigationHeader;

    private LinearLayout navAppInfoLayout, navUserInfoLayout, mainLayout;
    private ProgressBar loadingView;
    private TextView userName, userEmail;

    private User user;

    private BasePresenter basePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setEvent();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        mainLayout = findViewById(R.id.main_layout);
        loadingView = findViewById(R.id.loading_view);
        views = findViewById(R.id.views);
        navigationView = findViewById(R.id.nav_view);
        navigationHeader = navigationView.getHeaderView(0);

        navAppInfoLayout = navigationHeader.findViewById(R.id.app_info_layout);
        navUserInfoLayout = navigationHeader.findViewById(R.id.user_info_layout);

        userName = navigationHeader.findViewById(R.id.user_name);
        userEmail = navigationHeader.findViewById(R.id.user_email);

        if (Session.with(this).isLogin()) {
            user = Session.with(this).getUser();

            navAppInfoLayout.setVisibility(View.GONE);
            navUserInfoLayout.setVisibility(View.VISIBLE);

            userName.setText(user.getName());
            userEmail.setText(user.getEmail());

            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_user).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_news).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_block_user).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_report).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        }
        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadViews();
    }

    private void setEvent() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;
        if (id == R.id.nav_login) {
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_user) {
            intent = new Intent(this, MemberActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_news) {
            intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_block_user) {
            intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_report) {
            intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Konfirmasi")
                    .setMessage("Keluar dari aplikasi?")
                    .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Keluar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Session.with(MainActivity.this).logout();
                        }
                    })
                    .show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onSuccess(BaseResponse base) {
        loadingView.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);

        views.setText(((MainResponse) base).getData().getViews() + "");
    }

    @Override
    public void onFailure(String message) {
        basePresenter.loadViews();
    }
}