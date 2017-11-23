package com.cookiecatguzman.amarra.validas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cookiecatguzman.amarra.R;
import com.cookiecatguzman.amarra.sqlite.IncidenciasSqlite;
import com.cookiecatguzman.amarra.utilidades.Route;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        LocationListener, View.OnClickListener {

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
    private static final int RESULT_MAP = 1;
    public boolean conMarcador;
    public double origenLat;
    public double origenLng;
    public double destinoLat;
    public double destinoLng;
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
    private FloatingActionButton fab;
    private boolean viajeIniciado = false;
    private boolean mLocationPermissionGranted;
    private long tiempoInicial;
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
                intent.putExtra(TAG_TIEMPO_INICIAL, tiempoInicial);
                intent.putExtra(TAG_TIEMPO_FINAL, tiempoFinal);
                intent.putExtra(TAG_TIEMPO_TRANSCURRIDO, tiempoFinal - tiempoInicial);
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

    @Override
    public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String tipoIncidencia = "";

        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(latitude, longitude));

        switch (v.getId()) {
            case R.id.fab_inundacion: {
                tipoIncidencia = TIPO_INUNDACION;
                marker.title(TIPO_INUNDACION);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.circulo_inundacion));
                break;
            }
            case R.id.fab_falla: {
                tipoIncidencia = TIPO_FALLA;
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.circulo_falla));
                break;
            }
            case R.id.fab_inseguridad: {
                tipoIncidencia = TIPO_INSEGURIDAD;
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.circulo_inseguridad));
                break;
            }
            case R.id.fab_aglomeracion: {
                tipoIncidencia = TIPO_AGLOMERACION;
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.circulo_aglomeracion));
                break;
            }
            case R.id.fab_retraso: {
                tipoIncidencia = TIPO_RETRASO;
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.circulo_retrazo));
                break;
            }
            case R.id.fab_marcha: {
                tipoIncidencia = TIPO_MARCHA;
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.circulo_marcha));
                break;
            }
            case R.id.fab_otra_anomalia: {
                tipoIncidencia = TIPO_OTRO;
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.circulo_otros));
                break;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = sdf.format(new Date());
        sdf = new SimpleDateFormat("HH:mm:ss");
        String hora = sdf.format(new Date());

        IncidenciasSqlite sqLite = new IncidenciasSqlite(this);

        ArrayList<String> datosSqlite = new ArrayList<>();
        datosSqlite.add(sqLite.numberOfRows() + 1 + "");
        datosSqlite.add(tipoIncidencia);
        datosSqlite.add(latitude + "");
        datosSqlite.add(longitude + "");
        datosSqlite.add(hora);
        datosSqlite.add(fecha);


        if (sqLite.insertSolicitud(datosSqlite) == -1) {
            Log.e("Error", "ERROR");
            sqLite.close();
            return;
        }
        sqLite.close();

        Toast.makeText(this, "Incidencia enviada.", Toast.LENGTH_SHORT).show();

        mMap.addMarker(marker);
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

        fabInundacion.setOnClickListener(this);
        fabFalla.setOnClickListener(this);
        fabInseguridad.setOnClickListener(this);
        fabAglomeracion.setOnClickListener(this);
        fabRetraso.setOnClickListener(this);
        fabMarcha.setOnClickListener(this);
        fabOtro.setOnClickListener(this);
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
