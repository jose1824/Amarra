package com.cookiecatguzman.amarra.validas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.cookiecatguzman.amarra.MapsActivity;
import com.cookiecatguzman.amarra.R;
import com.cookiecatguzman.amarra.SplashScreenActivity;
import com.cookiecatguzman.amarra.utilidades.Route;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_DESTINO_DIRECCION;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_DESTINO_LATITUD;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_DESTINO_LONGITUD;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_ORIGEN_DIRECCION;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_ORIGEN_LATITUD;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_ORIGEN_LONGITUD;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_TIEMPO_FINAL;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_TIEMPO_INICIAL;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_TIEMPO_TRANSCURRIDO;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener {

    /**
     * Constantes
     */
    public final static String TIPO_INUNDACION = "inundacion";
    public final static String TIPO_FALLA = "falla";
    public final static String TIPO_INSEGURIDAD = "inseguridad";
    public final static String TIPO_AGLOMERACION = "aglomeracion";
    public final static String TIPO_RETRASO = "retraso";
    public final static String TIPO_MARCHA = "marcha";
    public final static String TIPO_OTRO = "otro";

    /**
     * Elementos graficos
     */
    private FloatingActionButton fabInundacion;
    private FloatingActionButton fabFalla;
    private FloatingActionButton fabInseguridad;
    private FloatingActionButton fabAglomeracion;
    private FloatingActionButton fabRetraso;
    private FloatingActionButton fabMarcha;
    private FloatingActionButton fabOtro;

    private GoogleMap mMap;
    private double latitudInicio;
    private double longitudInicio;
    private static final int RESULT_MAP = 1;
    private FloatingActionButton fab;
    private boolean viajeIniciado = false;
    private boolean mLocationPermissionGranted;
    private long tiempoInicial;

    public boolean conMarcador;

    public double origenLat;
    public double origenLng;
    public double destinoLat;
    public double destinoLng;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        tiempoInicial = System.currentTimeMillis();

        origenLat = getIntent().getExtras().getDouble(TAG_ORIGEN_LATITUD);
        origenLng = getIntent().getExtras().getDouble(TAG_ORIGEN_LONGITUD);
        destinoLat = getIntent().getExtras().getDouble(TAG_DESTINO_LATITUD);
        destinoLng = getIntent().getExtras().getDouble(TAG_DESTINO_LONGITUD);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Amara - IPN");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        conMarcador = false;

        inicializarComponentes();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viaje_iniciado, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.terminar: {
                long tiempoFinal = System.currentTimeMillis();
                Intent intent = new Intent(MapaActivity.this, ResultadoActivity.class);
                intent.putExtra(TAG_ORIGEN_LATITUD, getIntent().getExtras().getDouble(TAG_ORIGEN_LATITUD));
                intent.putExtra(TAG_ORIGEN_LONGITUD, getIntent().getExtras().getDouble(TAG_ORIGEN_LATITUD));
                intent.putExtra(TAG_DESTINO_LATITUD, getIntent().getExtras().getDouble(TAG_ORIGEN_LATITUD));
                intent.putExtra(TAG_DESTINO_LONGITUD, getIntent().getExtras().getDouble(TAG_ORIGEN_LATITUD));
                intent.putExtra(TAG_ORIGEN_DIRECCION, getIntent().getExtras().getString(TAG_ORIGEN_DIRECCION));
                intent.putExtra(TAG_DESTINO_DIRECCION, getIntent().getExtras().getString(TAG_DESTINO_DIRECCION));
                intent.putExtra(TAG_TIEMPO_INICIAL, tiempoInicial );
                intent.putExtra(TAG_TIEMPO_FINAL, tiempoFinal);
                intent.putExtra(TAG_TIEMPO_TRANSCURRIDO, tiempoFinal - tiempoInicial );
                startActivity(intent);
                finish();
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            pedirPermiso();
        }

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        final LatLng ubicacion = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(ubicacion)
                .title("Ubicación actual")
        ).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));


        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(origenLat, origenLng))
                .title("Origen")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
        ).showInfoWindow();

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(destinoLat, destinoLat))
                .title("Destino")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        ).showInfoWindow();

        Route route = new Route();
        route.drawRoute(
                mMap,
                MapaActivity.this,
                new LatLng(origenLat, origenLng),
                new LatLng(destinoLat, destinoLng),
                Route.TRANSPORT_DRIVING,
                true,
                Route.LANGUAGE_SPANISH
        );

    }

    private void pedirPermiso() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RESULT_MAP);

            return;
        }
    }

    private void inicializarComponentes() {
        fabInundacion = (FloatingActionButton) findViewById(R.id.fab_inundacion);
        fabFalla = (FloatingActionButton) findViewById(R.id.fab_falla);
        fabInseguridad = (FloatingActionButton) findViewById(R.id.fab_inseguridad);
        fabAglomeracion = (FloatingActionButton) findViewById(R.id.fab_aglomeracion);
        fabRetraso = (FloatingActionButton) findViewById(R.id.fab_retraso);
        fabMarcha = (FloatingActionButton) findViewById(R.id.fab_marcha);
        fabOtro = (FloatingActionButton) findViewById(R.id.fab_otra_anomalia);
    }

    @Override
    public void onLocationChanged(Location location) {
        /*if (!viajeIniciado) {
            return;
        }*/
        /*System.out.println("ENTRO");

        mMap.clear();

        MarkerOptions mp = new MarkerOptions();

        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

        mp.title("my position");

        mMap.addMarker(mp);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));
*/
/*
        if (mListener != null) {
            mListener.onLocationChanged(location);
        }


        if (ActivityCompat.checkSelfPermission(MapaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapaActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        LatLng ubicacionActual = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitudInicio, longitudInicio)).title("Ubicación actual"));

        if (conMarcador) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitudInicio, longitudInicio)).title("Marker Here"));
        }

        mMap.addMarker(new MarkerOptions().position(ubicacionActual).title("Marker Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, 15));
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
        conMarcador = true;*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
