package com.cookiecatguzman.amarra.validas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.cookiecatguzman.amarra.MapsActivity;
import com.cookiecatguzman.amarra.R;
import com.cookiecatguzman.amarra.SplashScreenActivity;
import com.cookiecatguzman.amarra.utilidades.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitudInicio;
    private double longitudInicio;
    private static final int RESULT_MAP = 1;
    private LinearLayout linearDetenerViaje;

    public static final long NOTIFY_INTERVAL = 10 * 1000;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    public boolean conMarcador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Amara");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapaActivity.this, IncidenciaActivity.class));
            }
        });

        conMarcador = false;

        linearDetenerViaje = (LinearLayout) findViewById(R.id.detener_viaje);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.empezar: {
                linearDetenerViaje.setVisibility(View.VISIBLE);
                if (mTimer != null) {
                    mTimer.cancel();
                } else {
                    // recreate new
                    mTimer = new Timer();
                }
                mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(this), 0, NOTIFY_INTERVAL);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RESULT_MAP: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    pedirPermiso();
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        pedirPermiso();

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            pedirPermiso();
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitudInicio = location.getLongitude();
        latitudInicio = location.getLatitude();

        // Add a marker in Sydney and move the camera
        LatLng ubicacion = new LatLng(latitudInicio, longitudInicio);
        mMap.addMarker(new MarkerOptions().position(ubicacion).title("Ubicación actual"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));

    }

    private void pedirPermiso() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RESULT_MAP);

            return;
        }
    }

    private class TimeDisplayTimerTask extends TimerTask {

        Context context;

        TimeDisplayTimerTask(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                        try {
                            ProcesoAsincrono asincrono = new ProcesoAsincrono();
                            asincrono.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }

            });
        }
    }


    private class ProcesoAsincrono extends AsyncTask<Void, ArrayList<Double>, ArrayList<String>> {

        public ProcesoAsincrono() {
            linearDetenerViaje.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel(true);
                }
            });
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> data = new ArrayList<>();
                    LocationManager lm = (LocationManager) MapaActivity.this.getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(MapaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(MapaActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    ArrayList<Double> coordenadas = new ArrayList<Double>();
                    coordenadas.add(longitude);
                    coordenadas.add(latitude);
            publishProgress(coordenadas);
            return data;
        }

        @Override
        protected void onProgressUpdate(final ArrayList<Double>... values) {
            super.onProgressUpdate(values);
            LatLng ubicacionActual = new LatLng(values[0].get(1), values[0].get(0));
            Marker markerActual = mMap.addMarker(new MarkerOptions().position(new LatLng(latitudInicio, longitudInicio)).title("Ubicación actual"));

            if (conMarcador){
                Marker origen = mMap.addMarker(new MarkerOptions().position(new LatLng(latitudInicio, longitudInicio)).title("Marker Here"));
            }

            Marker destino = mMap.addMarker(new MarkerOptions().position(ubicacionActual).title("Marker Here"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, 15));
            Route route = new Route();
            route.drawRoute(
                    mMap,
                    MapaActivity.this,
                    new LatLng(latitudInicio, longitudInicio),
                    ubicacionActual,
                    Route.TRANSPORT_DRIVING,
                    true,
                    Route.LANGUAGE_SPANISH
            );
            conMarcador = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println(values[0]);
                    doInBackground();
                }
            }, 1500);

        }

        @Override
        protected void onCancelled(ArrayList<String> strings) {
            super.onCancelled(strings);
            startActivity(new Intent(MapaActivity.this, ResultadoActivity.class));
        }


    }
}
