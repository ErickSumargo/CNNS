package com.app.cnns.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.app.cnns.R;

public class FullScreenVideoActivity extends AppCompatActivity {
    private EasyVideoPlayer video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        initView();
    }

    private void initView() {
        video = findViewById(R.id.video);

        Bundle extras = getIntent().getExtras();
        String url = extras.getString("url");

        Uri uri = Uri.parse(url);
        video.setSource(uri);
    }
}