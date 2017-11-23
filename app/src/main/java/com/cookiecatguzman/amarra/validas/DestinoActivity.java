package com.cookiecatguzman.amarra.validas;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.cookiecatguzman.amarra.R;
import com.cookiecatguzman.amarra.sqlite.IncidenciasSqlite;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class DestinoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = "Google Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
    private static final String PLACES_DETAILS_API_BASE = "https://maps.googleapis.com/maps/api/place/details/json";
    private static final String API_KEY = "AIzaSyBvs7LbKnLuICsO9Re4kq-_875QzIfA0e4";

    public static final String TAG_ORIGEN_LATITUD = "TAG_ORIGEN_LATITUD";
    public static final String TAG_ORIGEN_LONGITUD = "TAG_ORIGEN_LONGITUD";
    public static final String TAG_DESTINO_LATITUD = "TAG_DESTINO_LATITUD";
    public static final String TAG_DESTINO_LONGITUD = "TAG_DESTINO_LONGITUD";
    public static final String TAG_ORIGEN_DIRECCION = "TAG_ORIGEN_DIRECCION";
    public static final String TAG_DESTINO_DIRECCION = "TAG_DESTINO_DIRECCION";
    public static final String TAG_TIEMPO_TRANSCURRIDO = "TAG_TIEMPO_TRANSCURRIDO";
    public static final String TAG_TIEMPO_INICIAL = "TAG_TIEMPO_INICIAL";
    public static final String TAG_TIEMPO_FINAL = "TAG_TIEMPO_FINAL";

    private ArrayList placeIDs;

    private GoogleApiClient mGoogleApiClient;

    AutoCompleteTextView autoCompView;
    AutoCompleteTextView autoCompViewDestino;

    public double origenLat;
    public double origenLng;
    public double destinoLat;
    public double destinoLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destino);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         autoCompView = (AutoCompleteTextView) findViewById(R.id.autoc_origen);
         autoCompViewDestino = (AutoCompleteTextView) findViewById(R.id.autoc_destino);

        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.spinner_item));
        autoCompView.setOnItemClickListener(this);

        autoCompViewDestino.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.spinner_item));
        autoCompViewDestino.setOnItemClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi( Places.GEO_DATA_API )
                .addApi( Places.PLACE_DETECTION_API )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConexionAsincrona asincrona = new ConexionAsincrona(placeIDs.get(position).toString());
                ArrayList<Double> origen = null;
                try {
                    origen = asincrona.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                System.out.println(origen);
                origenLat = origen.get(0);
                origenLng = origen.get(1);
            }
        });

        autoCompViewDestino.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConexionAsincrona asincrona = new ConexionAsincrona(placeIDs.get(position).toString());
                ArrayList<Double> destino = null;
                try {
                    destino = asincrona.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                destinoLat = destino.get(0);
                destinoLng = destino.get(1);
            }
        });

        try{
            IncidenciasSqlite sqLite = new IncidenciasSqlite(this);
            sqLite.resetBD();
        } catch (Exception e) {

        }


    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (autoCompView.getText().toString().length() <= 0) {
            Toast.makeText(this, "Primero elija su origen", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (autoCompView.getText().toString().length() <= 0) {
            Toast.makeText(this, "Primero elija su destino", Toast.LENGTH_SHORT).show();
            return false;
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.empezar: {
                Intent intent = new Intent(DestinoActivity.this, MapaActivity.class);
                intent.putExtra(TAG_ORIGEN_LATITUD, origenLat);
                intent.putExtra(TAG_ORIGEN_LONGITUD, origenLng);
                intent.putExtra(TAG_DESTINO_LATITUD, destinoLat);
                intent.putExtra(TAG_DESTINO_LONGITUD, destinoLng);
                intent.putExtra(TAG_ORIGEN_DIRECCION, autoCompView.getText().toString());
                System.out.println(autoCompView.getText().toString());
                intent.putExtra(TAG_DESTINO_DIRECCION, autoCompViewDestino.getText().toString());
                startActivity(intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public static ArrayList<ArrayList> autocomplete(String input) {
        ArrayList<ArrayList> resultList = null;


        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        // https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Vict&types=geocode&language=fr&key=YOUR_API_KEY
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append("?key=" + API_KEY);
            sb.append("&types=address");
            //sb.append("&strictbounds");
            //sb.append("&radius=" + 500 * 1000);
            sb.append("&components=country:mx");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }


        ArrayList descripciones = new ArrayList();
        ArrayList ids = new ArrayList();

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                descripciones.add(predsJsonArray.getJSONObject(i).getString("description"));
                ids.add(predsJsonArray.getJSONObject(i).getString("place_id"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        resultList.add(descripciones);
        resultList.add(ids);
        return resultList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("onItemClick");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("onConnectionFailed");
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        ArrayList<ArrayList> contenedor;
                        contenedor = autocomplete(constraint.toString());
                        // Retrieve the autocomplete results.
                        resultList = contenedor.get(0);
                        placeIDs =  contenedor.get(1);

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    class ConexionAsincrona extends AsyncTask<Void, Void, ArrayList<Double>> {

        String lugarID;

        public ConexionAsincrona(String lugarID) {
            this.lugarID = lugarID;
        }

        @Override
        protected ArrayList<Double> doInBackground(Void... voids) {
            ArrayList<Double> resultado = new ArrayList<>();

            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            // https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Vict&types=geocode&language=fr&key=YOUR_API_KEY
            try {
                StringBuilder sb = new StringBuilder(PLACES_DETAILS_API_BASE);
                sb.append("?key=" + API_KEY);
                sb.append("&placeid=" + lugarID);
                //sb.append("&strictbounds");
                //sb.append("&radius=" + 500 * 1000);


                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error processing Places API URL", e);
                return null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to Places API", e);
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            try {
                JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                resultado.add(jsonObj.getDouble("lat"));
                resultado.add(jsonObj.getDouble("lng"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return resultado;
        }


    }

}
