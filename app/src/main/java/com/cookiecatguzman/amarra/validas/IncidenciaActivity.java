package com.cookiecatguzman.amarra.validas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cookiecatguzman.amarra.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IncidenciaActivity extends AppCompatActivity {

    private double latitudInicio;
    private double longitudInicio;

    private TextView txtDireccion;
    private TextView txtCoordenadas;
    private Spinner spnTipoIncidencia;
    private EditText edtComentario;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inicializarCompoentes();

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitudInicio = location.getLongitude();
        latitudInicio = location.getLatitude();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitudInicio, longitudInicio, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            txtDireccion.setText(
                    "Dirección: " + address + "\n" +
                            city + "\n" +
                            state + "\n" +
                            country + "\n" +
                            postalCode + "\n" +
                            knownName + "."

            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtCoordenadas.setText("Lat: " + latitudInicio + " Long: " + longitudInicio);

        ArrayList<String> listaSpinner = new ArrayList<>();
        listaSpinner.add("Seleccione su percance");
        listaSpinner.add("Averia mecanica");
        listaSpinner.add("Desastre natural");
        listaSpinner.add("Accidente");
        listaSpinner.add("Robo o acto de delincuencia");
        listaSpinner.add("Retraso");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_item,
                listaSpinner
        );
        spnTipoIncidencia.setAdapter(adapter);
        spnTipoIncidencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spnTipoIncidencia.getSelectedItemPosition() <= 0 ) {
                    Toast.makeText(IncidenciaActivity.this, "Seleccione su tipo de percance", Toast.LENGTH_SHORT).show();
                } else if (edtComentario.getText().toString().equals("")) {
                    Toast.makeText(IncidenciaActivity.this, "Introduzca un comentario con más información.", Toast.LENGTH_SHORT).show();
                } else {
                    mandarAlerta();
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                startActivity(new Intent(IncidenciaActivity.this, MapaActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inicializarCompoentes(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reportar alerta");

        txtDireccion = (TextView) findViewById(R.id.incidencia_txt_dirección);
        txtCoordenadas = (TextView) findViewById(R.id.incidencia_txt_coordenadas);
        spnTipoIncidencia = (Spinner) findViewById(R.id.incidencia_spn_tipo);
        edtComentario = (EditText) findViewById(R.id.incidencia_edt_comentario);
        btnEnviar = (Button) findViewById(R.id.incidencia_btn_enviar);
    }

    private void mandarAlerta(){
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/plain");
        i.setData(Uri.parse("mailto:sanreyor@hotmail.com"));
        i.putExtra(Intent.EXTRA_SUBJECT, "Reporte de incidencia Amara");
        i.putExtra(Intent.EXTRA_TEXT   ,
                "La unidad ha presentado un(a) " + spnTipoIncidencia.getSelectedItem().toString() + "\n\n" +
                        "Comentario:" + edtComentario.getText().toString().trim() +
                        txtDireccion.getText().toString() + "\n\n" +
                        txtCoordenadas.getText().toString()
                );
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            Toast.makeText(IncidenciaActivity.this, "Alerta enviada.", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(IncidenciaActivity.this, "No hay cliente de correo electrónico instalado.", Toast.LENGTH_SHORT).show();
        }
    }

}
