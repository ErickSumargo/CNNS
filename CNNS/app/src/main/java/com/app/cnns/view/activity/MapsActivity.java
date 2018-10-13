package com.app.cnns.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.cnns.R;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.model.News;
import com.app.cnns.server.presenter.BasePresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.NewsResponse;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import io.reactivex.annotations.NonNull;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Response {
    private SupportMapFragment map;
    private ProgressBar loadingView;

    private GoogleMap mMap;

    private BasePresenter basePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initView();
    }

    private void initView() {
        map = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map.getView().setVisibility(View.GONE);
        map.getMapAsync(this);

        loadingView = findViewById(R.id.loading_view);

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadLocations();
    }

    private void loadData(List<News> news) {
        for (int i = 0; i < news.size(); i++) {
            addMarker(news.get(i).getTitle(), news.get(i).getLatitude(), news.get(i).getLongitude());
        }
    }

    private void addMarker(String title, double latitude, double longitude) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_error)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f));

                mMap.setOnMyLocationChangeListener(null);
            }
        });
    }

    @Override
    public void onSuccess(BaseResponse base) {
        NewsResponse r = (NewsResponse) base;
        List<News> news = r.getData().getNewsList();
        loadData(news);

        loadingView.setVisibility(View.GONE);
        map.getView().setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(String message) {
        basePresenter.loadLocations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}