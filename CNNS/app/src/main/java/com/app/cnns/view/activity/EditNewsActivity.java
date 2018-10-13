package com.app.cnns.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.app.cnns.R;
import com.app.cnns.helper.Constant;
import com.app.cnns.helper.Internet;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.model.News;
import com.app.cnns.server.presenter.BasePresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.NewsResponse;
import com.app.cnns.view.custom.ProgressDialogCustom;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.glide.slider.library.svg.GlideApp;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditNewsActivity extends AppCompatActivity implements Response,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private Toolbar toolbar;
    private NestedScrollView mainLayout;
    private EditText titleField, locationField, dateField, timeField, contentField;

    private TextView uploadImage, uploadVideo;
    private RelativeLayout imageCont, videoCont;
    private ImageView imageThumbnail;
    private VideoView videoThumbnail;
    private ImageView imageRemoval, videoRemoval;

    private Button post;
    private ProgressBar loadingView;
    private ProgressDialogCustom progressDialog;

    private int newsId;
    private String title, date, time, dateTime, content, imagePath = "", videoPath = "";
    private double latitude = -1, longitude = -1;
    private boolean imageChanged = false, videoChanged = false;
    private boolean loadingNews = true, postingNews = false;

    private News news;

    private BasePresenter basePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_news);

        initView();
        setEvent();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainLayout = findViewById(R.id.main_layout);
        loadingView = findViewById(R.id.loading_view);

        titleField = findViewById(R.id.title_field);
        locationField = findViewById(R.id.location_field);
        dateField = findViewById(R.id.date_field);
        timeField = findViewById(R.id.time_field);
        contentField = findViewById(R.id.content_field);

        uploadImage = findViewById(R.id.upload_image);
        uploadVideo = findViewById(R.id.upload_video);

        imageCont = findViewById(R.id.image_container);
        videoCont = findViewById(R.id.video_container);

        imageThumbnail = findViewById(R.id.image_thumbnail);
        videoThumbnail = findViewById(R.id.video_thumbnail);

        imageRemoval = findViewById(R.id.image_removal);
        videoRemoval = findViewById(R.id.video_removal);

        post = findViewById(R.id.post);

        Bundle extras = getIntent().getExtras();
        newsId = extras.getInt("news_id");

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadDraftDetail(newsId);
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        contentField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_SCROLL:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(contentField, InputMethodManager.SHOW_IMPLICIT);
                }
                return false;
            }
        });

        locationField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(EditNewsActivity.this), Constant.PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                    } catch (GooglePlayServicesNotAvailableException e) {
                    }
                }
                return false;
            }
        });

        dateField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            EditNewsActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "");
                }
                return false;
            }
        });

        timeField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog tpd = TimePickerDialog.newInstance(
                            EditNewsActivity.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true
                    );
                    tpd.show(getFragmentManager(), "");
                }
                return false;
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImagePicker.Builder(EditNewsActivity.this)
                        .mode(ImagePicker.Mode.GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.PNG)
                        .scale(600, 600)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .build();
            }
        });

        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VideoPicker.Builder(EditNewsActivity.this)
                        .mode(VideoPicker.Mode.GALLERY)
                        .directory(VideoPicker.Directory.DEFAULT)
                        .extension(VideoPicker.Extension.MP4)
                        .enableDebuggingMode(true)
                        .build();
            }
        });

        imageRemoval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePath = "";
                imageCont.setVisibility(View.GONE);

                imageChanged = true;
            }
        });

        videoRemoval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPath = "";
                videoCont.setVisibility(View.GONE);

                videoChanged = true;
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        dateField.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;

        time = hourString + ":" + minuteString + ":" + secondString;
        timeField.setText(time + " WIB");
    }

    private void loadData() {
        titleField.setText(news.getTitle());

        latitude = news.getLatitude();
        longitude = news.getLongitude();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            locationField.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
        }

        String mParts[] = news.getDate().split(" ");
        String dParts[] = mParts[0].split("-");
        String tParts[] = mParts[1].split(":");
        date = dParts[0] + "-" + dParts[1] + "-" + dParts[2];
        time = tParts[0] + ":" + tParts[1] + ":" + tParts[2];
        dateField.setText(dParts[2] + "/" + dParts[1] + "/" + dParts[0]);
        timeField.setText(time);

        contentField.setText(news.getContent());

        if (!news.getImage().isEmpty()) {
            Picasso.with(this).load(Utils.with(this).getURLMediaImage(news.getImage(), "news"))
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(imageThumbnail);
            imageCont.setVisibility(View.VISIBLE);
            imagePath = news.getImage();
        }
        if (!news.getVideo().isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(Utils.with(this).getURLMediaVideo(news.getVideo()))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            videoThumbnail.setBackgroundDrawable(new BitmapDrawable(getResources(), resource));
                            videoCont.setVisibility(View.VISIBLE);
                        }
                    });
            videoPath = news.getVideo();
        }
        mainLayout.setVisibility(View.VISIBLE);
    }

    private void validate() {
        title = titleField.getText().toString().trim();
        dateTime = date + " " + time;
        content = contentField.getText().toString().trim();

        if (title.length() == 0) {
            showErrorDialog("Masukkan judul berita");
        } else if (latitude == -1 && longitude == -1) {
            showErrorDialog("Masukkan lokasi kejadian");
        } else if (content.length() == 0) {
            showErrorDialog("Masukkan detail berita");
        } else {
            attemptPosting();
        }
    }

    private void attemptPosting() {
        postingNews = true;

        progressDialog = new ProgressDialogCustom(this);
        progressDialog.setMessage("Mengedit...");

        if (Internet.isConnected(this)) {
            basePresenter.editNews(newsId, title, latitude, longitude, dateTime, content, imagePath, videoPath, imageChanged, videoChanged);
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(data, this);
            locationField.setText(place.getAddress());

            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
        } else if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            imagePath = ((List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH)).get(0);
            Bitmap bitmap = null;
            try {
                bitmap = Utils.with(this).getThumbnail(imagePath);
            } catch (Exception e) {

            }
            imageThumbnail.setImageBitmap(bitmap);
            imageCont.setVisibility(View.VISIBLE);

            imageChanged = true;
        } else if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            videoPath = ((List<String>) data.getSerializableExtra(VideoPicker.EXTRA_VIDEO_PATH)).get(0);

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
            videoThumbnail.setBackgroundDrawable(bitmapDrawable);
            videoCont.setVisibility(View.VISIBLE);

            videoChanged = true;
        }
    }

    @Override
    public void onSuccess(BaseResponse base) {
        if (loadingNews) {
            loadingView.setVisibility(View.GONE);
            news = ((NewsResponse) base).getData().getNews();
            loadData();

            loadingNews = false;
        } else if (postingNews) {
            if (base.isSuccess()) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "Berita berhasil diedit", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), DraftActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                basePresenter.editNews(newsId, title, latitude, longitude, dateTime, content, imagePath, videoPath, imageChanged, videoChanged);
            }
        }
    }

    @Override
    public void onFailure(String message) {
        if (loadingNews) {
            basePresenter.loadDraftDetail(newsId);
        } else if (postingNews) {
            basePresenter.editNews(newsId, title, latitude, longitude, dateTime, content, imagePath, videoPath, imageChanged, videoChanged);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}