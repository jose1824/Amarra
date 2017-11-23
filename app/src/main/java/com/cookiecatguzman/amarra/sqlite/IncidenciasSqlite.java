package com.cookiecatguzman.amarra.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by jesus on 20/11/17.
 */

public class IncidenciasSqlite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Proxima_estacion.db";
    private static final int DATABASE_VERSION = 1;

    private static final String INCIDENCIAS_TABLA = "solicitud";

    /**
     * TODO relaciones con la BD y conectandose con el servicio
     */

    private static final String INCIENCIA_ID = "_id";
    private static final String INCIDENCIA_TIPO_INCIDENCIA = "tipo_incidencia";
    private static final String INCIDENCIA_LATITUD = "latitud";
    private static final String INCIDENCIA_LONGITUD = "longitud";
    private static final String INCIDENCIA_HORA = "hora";
    private static final String INCIDENCIA_FECHA = "fecha";

    public IncidenciasSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + INCIDENCIAS_TABLA +
                        "(" + INCIENCIA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        INCIDENCIA_TIPO_INCIDENCIA + " TEXT, " +
                        INCIDENCIA_LATITUD + " TEXT, " +
                        INCIDENCIA_LONGITUD + " TEXT, " +
                        INCIDENCIA_HORA + " TEXT, " +
                        INCIDENCIA_FECHA + " TEXT)"
        );
    }

    private ArrayList<String> columnas() {
        ArrayList<String> columnas = new ArrayList<>();

        columnas.add(INCIENCIA_ID);
        columnas.add(INCIDENCIA_TIPO_INCIDENCIA);
        columnas.add(INCIDENCIA_LATITUD);
        columnas.add(INCIDENCIA_LONGITUD);
        columnas.add(INCIDENCIA_HORA);
        columnas.add(INCIDENCIA_FECHA);

        return columnas;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + INCIDENCIAS_TABLA);
        onCreate(db);
    }

    public void resetBD(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ INCIDENCIAS_TABLA);
    }

    public long insertSolicitud(ArrayList<String> datos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        ArrayList<String> columnas = columnas();

        if (datos.size() != columnas.size())
            return -1;

        for (int i = 0; i < columnas.size(); i++) {
            contentValues.put(columnas.get(i), datos.get(i));
        }
        return db.insert(INCIDENCIAS_TABLA, null, contentValues);
    }

    public long updateSolicitud(Integer id, ArrayList<String> datos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ArrayList<String> columnas = columnas();

        if (datos.size() != columnas.size())
            return -1;

        for (int i = 0; i < columnas.size(); i++) {
            contentValues.put(columnas.get(i), datos.get(i));
        }
        return db.update(INCIDENCIAS_TABLA, contentValues, INCIENCIA_ID + " = ? ", new String[] { Integer.toString(id) } );
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, INCIDENCIAS_TABLA);
    }

    public ArrayList<String> getSolicitud(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + INCIDENCIAS_TABLA + " WHERE " +
                INCIENCIA_ID + "=" + id, null);
        ArrayList<String> columnas = columnas();

        ArrayList<String> resultado = new ArrayList<>();
        for(res.moveToFirst(); !res.isAfterLast(); res.moveToNext()) {
            for (int i = 0; i < res.getColumnCount(); i++) {
                resultado.add(res.getString(res.getColumnIndex(columnas.get(i))));
            }
        }
        res.close();
        return resultado;
    }

    public ArrayList<ArrayList<String>> getAllSolicitud() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + INCIDENCIAS_TABLA, null );
        ArrayList<String> columnas = columnas();

        ArrayList<ArrayList<String>> resultado = new ArrayList<>();
        for(res.moveToFirst(); !res.isAfterLast(); res.moveToNext()) {
            ArrayList<String> solicitud = new ArrayList<>();
            for (int i = 0; i < res.getColumnCount(); i++) {
                solicitud.add(res.getString(res.getColumnIndex(columnas.get(i))));
            }
            resultado.add(solicitud);
        }

        res.close();

        return resultado;
    }

    public Integer deleteSolicitud(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(INCIDENCIAS_TABLA,
                INCIENCIA_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

}
