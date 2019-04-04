package com.br.mytasksapp.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.br.mytasksapp.BuildConfig;
import com.br.mytasksapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TermsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private TextView locationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        locationLabel = findViewById(R.id.locationLabel);

        mMapView = findViewById(R.id.maps);
        locationLabel.setVisibility(View.GONE);
        mMapView.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
            toolbarTitle.setText("Sobre");
            locationLabel.setVisibility(View.VISIBLE);
            mMapView.setVisibility(View.VISIBLE);
        }

        TextView version = findViewById(R.id.version);

        String versionText = "Versão " + BuildConfig.VERSION_NAME;

        version.setText(versionText);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this);

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.clear();

        double lat = -23.440649;
        double lng = -46.501294;

        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));

        CameraPosition position = CameraPosition.builder().target(new LatLng(lat, lng)).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }
}