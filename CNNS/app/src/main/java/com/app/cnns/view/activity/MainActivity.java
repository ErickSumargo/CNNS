package com.app.cnns.view.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

import com.app.cnns.R;
import com.app.cnns.helper.Constant;
import com.app.cnns.helper.Session;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.model.News;
import com.app.cnns.server.model.User;
import com.app.cnns.server.presenter.BasePresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.SearchResponse;
import com.app.cnns.server.response.wrapper.BaseWrapper;
import com.app.cnns.view.adapter.NewsListAdapter;
import com.app.cnns.view.adapter.SearchAdapter;
import com.glide.slider.library.SliderLayout;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.TextSliderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener, Response {
    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navigationHeader;

    private LinearLayout navAppInfoLayout, navUserInfoLayout;
    private CircleImageView userPhoto;
    private TextView userName, userEmail;

    private SliderLayout sliderLayout;
    private LinearLayout baseView;
    private RecyclerView newsListView;
    private NewsListAdapter newsListAdapter;

    private View noDataView;
    private ProgressBar loadingView;

    private User user;
    private boolean loadingBase = true;

    private SearchAdapter searchAdapter;
    private List<News> searchResults;

    private Timer timer = new Timer();
    private String query = "";
    private boolean searching = false;

    private BasePresenter basePresenter;

    private static String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private void setPermission() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 0);
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPermission();
        initView();
        initMainView();
        setEvent();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationHeader = navigationView.getHeaderView(0);

        navAppInfoLayout = navigationHeader.findViewById(R.id.app_info_layout);
        navUserInfoLayout = navigationHeader.findViewById(R.id.user_info_layout);

        userPhoto = navigationHeader.findViewById(R.id.user_photo);
        userName = navigationHeader.findViewById(R.id.user_name);
        userEmail = navigationHeader.findViewById(R.id.user_email);

        if (Session.with(this).isLogin()) {
            user = Session.with(this).getUser();

            navAppInfoLayout.setVisibility(View.GONE);
            navUserInfoLayout.setVisibility(View.VISIBLE);

            Picasso.with(this).load(Utils.with(this).getURLMediaImage(user.getPhoto(), user.getType()))
                    .placeholder(R.drawable.avatar)
                    .fit()
                    .centerCrop()
                    .into(userPhoto);
            userName.setText(user.getName());
            userEmail.setText(user.getEmail());

            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_post_news).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        }
    }

    private void initMainView() {
        baseView = findViewById(R.id.base_view);
        noDataView = findViewById(R.id.no_data_view);
        loadingView = findViewById(R.id.loading_view);

        sliderLayout = findViewById(R.id.slider);

        newsListView = findViewById(R.id.news_list_view);
        newsListView.setLayoutManager(new LinearLayoutManager(this));
        newsListView.setNestedScrollingEnabled(false);

        newsListAdapter = new NewsListAdapter(new ArrayList<News>());
        newsListView.setAdapter(newsListAdapter);

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadBase();
    }

    private void loadTopData(List<News> tops) {
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<String> urls = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        for (int i = 0; i < tops.size(); i++) {
            ids.add(tops.get(i).getId());
            urls.add(Utils.with(this).getURLMediaImage(tops.get(i).getImage(), "news"));
            titles.add(tops.get(i).getTitle());
        }

        sliderLayout.removeAllSliders();
        for (int i = 0; i < urls.size(); i++) {
            TextSliderView sliderView = new TextSliderView(this);
            sliderView
                    .image(urls.get(i))
                    .description(titles.get(i))
                    .setRequestOption((new RequestOptions()).centerCrop())
                    .setBackgroundColor(Color.WHITE)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this);

            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", String.valueOf(ids.get(i)));
            sliderLayout.addSlider(sliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
    }

    private void loadDailyData(List<News> daily) {
        newsListAdapter.setData(daily);
    }

    private void setEvent() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra("news_id", Integer.valueOf(slider.getBundle().get("extra").toString()));
        if (Session.with(this).isLogin()) {
            intent.putExtra("set_viewed", true);
        } else {
            intent.putExtra("set_viewed", false);
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            loadingBase = true;
            basePresenter.loadBase();
        }
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
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        searchResults = new ArrayList<>();
        searchAdapter = new SearchAdapter(this, getCursorData(searchResults), searchResults);
        search.setSuggestionsAdapter(searchAdapter);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                query = newText;
                timer.cancel();
                if (!newText.isEmpty()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (newText.length() > 0) {
                                if (!searching) {
                                    searching = true;
                                    basePresenter.search(newText);
                                }
                            }
                        }
                    }, 300);
                } else {
                    searchResults.clear();
                    updateSearchList(searchResults);
                }
                return true;
            }
        });

        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Intent intent = new Intent(getApplicationContext(), NewsDetailActivity.class);
                intent.putExtra("news_id", searchResults.get(position).getId());
                if (Session.with(MainActivity.this).isLogin()) {
                    intent.putExtra("set_viewed", true);
                } else {
                    intent.putExtra("set_viewed", false);
                }

                startActivity(intent);
                return false;
            }
        });
        return true;
    }

    private void updateSearchList(List<News> news) {
        searchAdapter.update(news);
        searchAdapter.swapCursor(getCursorData(news));
        searchAdapter.notifyDataSetChanged();
    }

    private MatrixCursor getCursorData(List<News> news) {
        MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "photo", "title"});
        for (int i = 0; i < news.size(); i++) {
            News n = news.get(i);
            cursor.addRow(new Object[]{i, n.getUser().getPhoto(), n.getTitle()});
        }
        return cursor;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }

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
        } else if (id == R.id.nav_profile) {
            intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_post_news) {
            intent = new Intent(this, PostNewsActivity.class);
            startActivityForResult(intent, 0);
        } else if (id == R.id.nav_most_popular) {
            intent = new Intent(this, MostPopularActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_location) {
            intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_report) {
            intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_video) {
            intent = new Intent(this, VideoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            intent = new Intent(this, AboutActivity.class);
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
        if (base.getTag().equals(Constant.TAG_LOAD_BASE)) {
            BaseWrapper w = (BaseWrapper) base;

            List<News> tops = w.getData().getTops();
            List<News> daily = w.getData().getDaily();
            if (tops.size() > 0) {
                loadTopData(tops);
                loadDailyData(daily);

                baseView.setVisibility(View.VISIBLE);
                noDataView.setVisibility(View.GONE);
            } else {
                noDataView.setVisibility(View.VISIBLE);
            }
            loadingView.setVisibility(View.GONE);
            loadingBase = false;
        } else if (base.getTag().equals(Constant.TAG_SEARCH)) {
            SearchResponse r = (SearchResponse) base;

            searchResults = r.getData().getSearchResults();
            if (!query.isEmpty()) {
                updateSearchList(searchResults);
            }
            searching = false;
        }
    }

    @Override
    public void onFailure(String message) {
        if (loadingBase) {
            basePresenter.loadBase();
        } else {
            if (searching) {
                if (searchResults != null) {
                    searchResults.clear();
                }
                searching = false;
            }
        }
    }
}