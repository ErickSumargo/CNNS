package com.app.cnns.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cnns.R;
import com.app.cnns.helper.Session;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.model.News;
import com.app.cnns.server.model.User;
import com.app.cnns.server.presenter.BasePresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.NewsResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewsDetailActivity extends AppCompatActivity implements Response {
    private Toolbar toolbar;
    private ScrollView mainLayout;
    private ImageView image;
    private TextView title, date, city, content;
    private ImageView share, option, thumbUp, thumbDown;
    private Button comment, viewer;
    private ProgressBar loadingView;

    private BasePresenter basePresenter;

    private User user;
    private News news;

    private String dateFormat = "EEEE, dd MMM ''yy HH.mm";
    private int userId, newsId, like = -1, reported;
    private boolean viewed, own, loadingDetail = true, settingViewed = false, settingLike = false, reporting = false;

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

        share = findViewById(R.id.share);
        option = findViewById(R.id.option);
        thumbUp = findViewById(R.id.thumb_up);
        thumbDown = findViewById(R.id.thumb_down);

        comment = findViewById(R.id.comment);
        viewer = findViewById(R.id.viewer);

        user = Session.with(this).getUser();
        userId = user != null ? user.getId() : -1;

        Bundle extras = getIntent().getExtras();
        newsId = extras.getInt("news_id");
        viewed = extras.getBoolean("set_viewed");

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadNewsDetail(userId, newsId);
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewsDetailActivity.this);
                final AlertDialog dialog = builder.create();
                dialog.setView(dialog.getLayoutInflater().inflate(R.layout.custom_news_share, null));
                dialog.show();

                LinearLayout fbCont = dialog.findViewById(R.id.fb_container);
                LinearLayout twitterCont = dialog.findViewById(R.id.twitter_container);
                LinearLayout notepadCont = dialog.findViewById(R.id.notepad_container);

                fbCont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                twitterCont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                notepadCont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            File root = new File(Environment.getExternalStorageDirectory(), "CNNS");
                            if (!root.exists()) {
                                root.mkdirs();
                            }
                            File gpxfile = new File(root, news.getTitle() + "_" + Calendar.getInstance().getTime() + ".txt");
                            FileWriter writer = new FileWriter(gpxfile);
                            writer.append("Judul: " + news.getTitle() + "\n\n");
                            writer.append("Tanggal: " + Utils.with(NewsDetailActivity.this).formatDate(news.getDate(), dateFormat) + " WIB" + "\n\n");
                            writer.append("Lokasi: " + city.getText().toString() + "\n\n");
                            writer.append("Isi:\n" + news.getContent());
                            writer.append(news.getContent());
                            writer.flush();
                            writer.close();

                            Toast.makeText(NewsDetailActivity.this, "Berita berhasil disimpan", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewsDetailActivity.this);
                final AlertDialog dialog = builder.create();
                dialog.setView(dialog.getLayoutInflater().inflate(R.layout.custom_news_option, null));
                dialog.show();

                LinearLayout bookmarkCont = dialog.findViewById(R.id.bookmark_container);
                LinearLayout reportCont = dialog.findViewById(R.id.report_container);

                final TextView bookmark = dialog.findViewById(R.id.bookmark);
                TextView report = dialog.findViewById(R.id.report);

                if (reported == 1) {
                    reportCont.setVisibility(View.GONE);
                }

                ArrayList<Integer> bookmarks = Session.with(getApplicationContext()).getBookmarks();
                final boolean bookmarked = bookmarks.contains(newsId);
                if (bookmarked) {
                    bookmark.setTextColor(getResources().getColor(R.color.colorAccent));
                }

                bookmarkCont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user != null) {
                            Session.with(getApplicationContext()).saveBookmark(newsId, bookmarked);
                            if (bookmarked) {
                                bookmark.setTextColor(getResources().getColor(R.color.black));
                                Toast.makeText(getApplicationContext(), "Berita terhapus dalam bookmark", Toast.LENGTH_SHORT).show();
                            } else {
                                bookmark.setTextColor(getResources().getColor(R.color.colorAccent));
                                Toast.makeText(getApplicationContext(), "Berita tersimpan dalam bookmark", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showErrorDialog("Harap login terlebih dahulu");
                        }
                        dialog.dismiss();
                    }
                });

                reportCont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user != null) {
                            reported = 1;
                            reporting = true;
                            basePresenter.report(newsId);

                            Toast.makeText(getApplicationContext(), "Berita terlaporkan", Toast.LENGTH_SHORT).show();
                        } else {
                            showErrorDialog("Harap login terlebih dahulu");
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        thumbUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    like = 1;
                    thumbUp.setColorFilter(getResources().getColor(R.color.colorAccent));
                    thumbDown.clearColorFilter();

                    settingLike = true;
                    basePresenter.setNewsLike(newsId, like);
                } else {
                    showErrorDialog("Harap login terlebih dahulu");
                }
            }
        });

        thumbDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    like = 0;
                    thumbUp.clearColorFilter();
                    thumbDown.setColorFilter(getResources().getColor(R.color.colorAccent));

                    settingLike = true;
                    basePresenter.setNewsLike(newsId, like);
                } else {
                    showErrorDialog("Harap login terlebih dahulu");
                }
            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra("news_id", newsId);

                startActivity(intent);
            }
        });

        viewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewerActivity.class);
                intent.putExtra("news_id", newsId);

                startActivity(intent);
            }
        });
    }

    private void loadData(News news) {
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

        if (like == 1) {
            thumbUp.setColorFilter(getResources().getColor(R.color.colorAccent));
        } else if (like == 0) {
            thumbDown.setColorFilter(getResources().getColor(R.color.colorAccent));
        }

        if (!own) {
            viewer.setVisibility(View.GONE);
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kesalahan")
                .setMessage(message)
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public void onSuccess(BaseResponse base) {
        if (loadingDetail) {
            NewsResponse r = (NewsResponse) base;
            News news = r.getData().getNews();
            this.news = news;

            like = r.getData().getLike();
            reported = r.getData().getReported();
            own = r.getData().isOwn();

            loadData(news);

            mainLayout.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);

            loadingDetail = false;
            if (viewed) {
                settingViewed = true;
                basePresenter.setViewed(newsId);
            }
        } else if (settingViewed) {
            settingViewed = false;
        } else if (settingLike) {
            if (base.isSuccess()) {
                settingLike = false;
            } else {
                basePresenter.setNewsLike(newsId, like);
            }
        } else if (reporting) {
            if (base.isSuccess()) {
                reporting = false;
            } else {
                basePresenter.report(newsId);
            }
        }
    }

    @Override
    public void onFailure(String message) {
        if (loadingDetail) {
            basePresenter.loadNewsDetail(userId, newsId);
        } else if (settingViewed) {
            basePresenter.setViewed(newsId);
        } else if (settingLike) {
            basePresenter.setNewsLike(newsId, like);
        } else if (reporting) {
            basePresenter.report(newsId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}