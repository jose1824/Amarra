package com.cookiecatguzman.amarra.validas;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cookiecatguzman.amarra.R;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_DESTINO_DIRECCION;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_DESTINO_LONGITUD;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_ORIGEN_DIRECCION;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_ORIGEN_LATITUD;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_TIEMPO_FINAL;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_TIEMPO_INICIAL;
import static com.cookiecatguzman.amarra.validas.DestinoActivity.TAG_TIEMPO_TRANSCURRIDO;

public class ResultadoActivity extends AppCompatActivity {

    @BindView(R.id.txt_tiempo) TextView txtTiempo;
    @BindView(R.id.txt_origen) TextView txtOrigen;
    @BindView(R.id.txt_destino) TextView txtDestino;
    @BindView(R.id.txt_hora_salida) TextView txtHoraSalida;
    @BindView(R.id.txt_hora_llegada) TextView txtHoraLlegada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Resultados del viaje");

        inicializarTextos();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                startActivity(new Intent(ResultadoActivity.this, DestinoActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inicializarTextos(){

        txtOrigen.setText(getIntent().getExtras().getString(TAG_ORIGEN_DIRECCION));
        txtDestino.setText(getIntent().getExtras().getString(TAG_DESTINO_DIRECCION));

        Date date = new Date(getIntent().getExtras().getLong(TAG_TIEMPO_INICIAL));
        Format format = new SimpleDateFormat("HH:mm:ss");
        txtHoraSalida.setText(format.format(date));

        Date date1 = new Date(getIntent().getExtras().getLong(TAG_TIEMPO_FINAL));
        txtHoraLlegada.setText(format.format(date1));

        long millis = getIntent().getExtras().getLong(TAG_TIEMPO_TRANSCURRIDO);
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        txtTiempo.setText(hour + " hrs, " + minute + " min, " + second + " sec");

    }


}
