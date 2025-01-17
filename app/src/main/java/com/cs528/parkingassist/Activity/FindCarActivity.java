package com.cs528.parkingassist.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cs528.parkingassist.Database.ParkPersistance;
import com.cs528.parkingassist.Model.Parking;
import com.cs528.parkingassist.R;
import com.cs528.parkingassist.Util.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class FindCarActivity extends AppCompatActivity implements OnMapReadyCallback{
    private TextView view_licence;
    private TextView view_location;
    private Button foundButton;
    private ImageView carImage;

    private GoogleMap mMap;
    private LatLng currenLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    List<Parking> parkingList;
    Parking parkingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_car);
        Log.i("car", "find car");
        view_licence = findViewById(R.id.carPlateNumber);
        view_location = findViewById(R.id.parkingLocation);
        foundButton = findViewById(R.id.foundButton);
        foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialogDemo();
            }
        });


        parkingList = ParkPersistance.get_instance(FindCarActivity.this).getParkings();
        parkingInfo = parkingList.get(parkingList.size()-1);
        view_licence.setText(parkingInfo.getLicence());
        view_location.setText(parkingInfo.getLat()+","+parkingInfo.getLon());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.current_location);
        mapFragment.getMapAsync((OnMapReadyCallback) this);


//        view_l.setText(p.getLicence());
//        String lat = String.valueOf(p.getLat());
//        String lon = String.valueOf(p.getLon());
        //TODO: get last location lat long
//        String s = "( " + lat + " " + lon + ")";
//        view_loc.setText(s);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(11);
        showDefaultLocation();

        LatLng parkLatLon = new LatLng(parkingInfo.getLat(), parkingInfo.getLon());
        googleMap.addMarker(new MarkerOptions().position(parkLatLon)
                .title("Your car Position."));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(parkLatLon));

    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }


    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();

        Location lastLocation = Utils.getBestLastKnownLocation(this);
        LatLng last = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        currenLocation = last;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(last));
        mMap.setMinZoomPreference(15);
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(20);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    mMap.setMinZoomPreference(12);

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));
                    currenLocation = new LatLng(location.getLatitude(),location.getLatitude());
                    circleOptions.radius(200);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(6);

                    mMap.addCircle(circleOptions);
                }
            };

    private void confirmDialogDemo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Car found already?");
        builder.setMessage("You are about to delete all records of database. Do you really want to proceed ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You've choosen to delete all records and start over", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FindCarActivity.this, MainActivity.class));
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Continue finding your car.", Toast.LENGTH_SHORT).show();

            }
        });

        builder.show();
    }

}
