package com.cookiecatguzman.amarra.validas;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cookiecatguzman.amarra.R;
import com.cookiecatguzman.amarra.adapters.IncidenciasRecyclerViewAdapter;
import com.cookiecatguzman.amarra.fragments.dummy.Incidencias;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    @BindView(R.id.list) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Resultados del viaje");

        inicializarTextos();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Incidencias incidencias = new Incidencias(this);
        ArrayList<Incidencias.DummyItem> datos = incidencias.getITEMS();
        IncidenciasRecyclerViewAdapter adapter = new IncidenciasRecyclerViewAdapter(datos, this);
        recyclerView.setAdapter(adapter);

        if (datos.size() <= 0) {
            recyclerView.setVisibility(View.GONE);
            LinearLayout linear = (LinearLayout) findViewById(R.id.resultado_linear_no_resultados);
            linear.setVisibility(View.VISIBLE);
        }

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
