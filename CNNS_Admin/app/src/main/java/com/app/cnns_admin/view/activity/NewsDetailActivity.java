package com.app.cnns_admin.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cnns_admin.R;
import com.app.cnns_admin.helper.Session;
import com.app.cnns_admin.helper.Utils;
import com.app.cnns_admin.server.interfaces.Response;
import com.app.cnns_admin.server.model.News;
import com.app.cnns_admin.server.presenter.BasePresenter;
import com.app.cnns_admin.server.response.BaseResponse;
import com.app.cnns_admin.server.response.NewsResponse;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewsDetailActivity extends AppCompatActivity implements Response {
    private Toolbar toolbar;
    private ScrollView mainLayout;
    private ImageView image;
    private TextView title, date, city, content;
    private TextView setStatus, views, edit;
    private ProgressBar loadingView;

    private BasePresenter basePresenter;

    private String dateFormat = "EEEE, dd MMM ''yy HH.mm";
    private int newsId;

    private News news;
    private boolean loadingNews = true, settingStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initView();
        setEvent();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = findViewById(R.id.main_layout);
        loadingView = findViewById(R.id.loading_view);

        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        city = findViewById(R.id.city);
        content = findViewById(R.id.content);

        setStatus = findViewById(R.id.set_status);
        views = findViewById(R.id.views);
        edit = findViewById(R.id.edit);

        Bundle extras = getIntent().getExtras();
        newsId = extras.getInt("news_id");

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadNewsDetail(newsId);
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "";
                if (news.getStatus() == 1) {
                    message = "Post berita?";
                } else if (news.getStatus() == 2) {
                    message = "Blokir berita?";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(NewsDetailActivity.this);
                builder.setTitle("Konfirmasi")
                        .setMessage(message)
                        .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Lanjut", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                settingStatus = true;
                                if (news.getStatus() == 1) {
                                    basePresenter.postNews(newsId);
                                } else if (news.getStatus() == 2) {
                                    basePresenter.blockNews(newsId);
                                }
                            }
                        })
                        .show();
            }
        });

        views.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsDetailActivity.this, ViewerActivity.class);
                intent.putExtra("news_id", newsId);

                startActivity(intent);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsDetailActivity.this, EditNewsActivity.class);
                intent.putExtra("news_id", newsId);

                startActivity(intent);
            }
        });
    }

    private void loadData() {
        Picasso.with(this).load(Utils.with(this).getURLMediaImage(news.getImage(), "news"))
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(image);
        title.setText(news.getTitle());
        date.setText(Utils.with(this).formatDate(news.getDate(), dateFormat) + " WIB");

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(news.getLatitude(), news.getLongitude(), 1);

            city.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
        }
        content.setText(news.getContent());

        if (news.getStatus() == 1) {
            setStatus.setText("Post");
            setStatus.setTextColor(getResources().getColor(R.color.green));
        } else if (news.getStatus() == 2) {
            setStatus.setText("Blokir");
            setStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            setStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(BaseResponse base) {
        if (loadingNews) {
            NewsResponse r = (NewsResponse) base;
            news = r.getData().getNews();

            loadData();

            mainLayout.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);

            loadingNews = false;
        } else if (settingStatus) {
            if (news.getStatus() == 1) {
                setStatus.setText("Blokir");
                setStatus.setTextColor(getResources().getColor(R.color.colorPrimary));

                news.setStatus(2);
                Toast.makeText(this, "Berita berhasil diposting", Toast.LENGTH_SHORT).show();
            } else if (news.getStatus() == 2) {
                setStatus.setVisibility(View.GONE);

                news.setStatus(0);
                Toast.makeText(this, "Berita berhasil diblokir", Toast.LENGTH_SHORT).show();
            }
            settingStatus = false;
        }
    }

    @Override
    public void onFailure(String message) {
        if (loadingNews) {
            basePresenter.loadNewsDetail(newsId);
        } else if (settingStatus) {
            if (news.getStatus() == 1) {
                basePresenter.postNews(newsId);
            } else if (news.getStatus() == 2) {
                basePresenter.blockNews(newsId);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}