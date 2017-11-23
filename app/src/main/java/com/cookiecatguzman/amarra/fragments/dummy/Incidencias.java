package com.cookiecatguzman.amarra.fragments.dummy;

import android.content.Context;

import com.cookiecatguzman.amarra.sqlite.IncidenciasSqlite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Incidencias {



    public ArrayList<Incidencias.DummyItem> getITEMS() {
        return ITEMS;
    }

    /**
     * An array of sample (dummy) items.
     */
    public ArrayList<Incidencias.DummyItem> ITEMS = new ArrayList<Incidencias.DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Incidencias.DummyItem> ITEM_MAP = new HashMap<String, Incidencias.DummyItem>();

    public Incidencias(Context context){
        // Add some sample items.
        IncidenciasSqlite sqLite = new IncidenciasSqlite(context);
        ArrayList<ArrayList<String>> datos =  sqLite.getAllSolicitud();
        System.out.println(datos);
        for (int i = 0; i <= sqLite.numberOfRows() - 1; i++) {
            addItem(createDummyItem(i, datos));
        }
    }

    /*
        data.add(arrayIdComentarios);  //0
        data.add(arrayStatus);         //1
        data.add(arrayComentarios);    //2
        data.add(arrayFechas);         //3
     */

    /**
     *
     * @param position
     * @param datos
     * @return
     */
    private static DummyItem createDummyItem(int position, ArrayList<ArrayList<String>> datos) {
        return new DummyItem(
                datos.get(position).get(0),
                datos.get(position).get(1),
                datos.get(position).get(2),
                datos.get(position).get(3),
                datos.get(position).get(4),
                datos.get(position).get(5)
        );
    }

    private void addItem(Incidencias.DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.tipoIncidencia, item);
    }

    public static class DummyItem{
        public final String idIncidencia;
        public final String tipoIncidencia;
        public final String latitud;
        public final String longitud;
        public final String hora;
        public final String fecha;

        public DummyItem(
                String idIncidencia,
                String tipoIncidencia,
                String latitud,
                String longitud,
                String hora,
                String fecha
        ) {
            this.idIncidencia = idIncidencia;
            this.tipoIncidencia = tipoIncidencia;
            this.latitud = latitud;
            this.longitud = longitud;
            this.hora = hora;
            this.fecha = fecha;
        }

        @Override
        public String toString() {
            return tipoIncidencia;
        }
    }

}
