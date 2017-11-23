package com.cookiecatguzman.amarra.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cookiecatguzman.amarra.R;
import com.cookiecatguzman.amarra.fragments.UnidadesFragment.OnListFragmentInteractionListener;
import com.cookiecatguzman.amarra.fragments.dummy.Incidencias;
import com.cookiecatguzman.amarra.validas.MapaActivity;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Incidencias.DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class IncidenciasRecyclerViewAdapter extends RecyclerView.Adapter<IncidenciasRecyclerViewAdapter.ViewHolder> {

    private final List<Incidencias.DummyItem> mValues;
    private final Context context;

    public IncidenciasRecyclerViewAdapter(List<Incidencias.DummyItem> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carditem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tituloCard.setText(holder.mItem.tipoIncidencia);
        holder.descripcionCard.setText(holder.mItem.fecha + "\n" + holder.mItem.hora);

        if (holder.mItem.tipoIncidencia.equals(MapaActivity.TIPO_AGLOMERACION)) {
            holder.imagenCard.setImageDrawable(context.getResources().getDrawable(R.drawable.circulo_aglomeracion));
        }
        if (holder.mItem.tipoIncidencia.equals(MapaActivity.TIPO_FALLA)) {
            holder.imagenCard.setImageDrawable(context.getResources().getDrawable(R.drawable.circulo_falla));
        }
        if (holder.mItem.tipoIncidencia.equals(MapaActivity.TIPO_INSEGURIDAD)) {
            holder.imagenCard.setImageDrawable(context.getResources().getDrawable(R.drawable.circulo_inseguridad));
        }
        if (holder.mItem.tipoIncidencia.equals(MapaActivity.TIPO_INUNDACION)) {
            holder.imagenCard.setImageDrawable(context.getResources().getDrawable(R.drawable.circulo_inundacion));
        }
        if (holder.mItem.tipoIncidencia.equals(MapaActivity.TIPO_MARCHA)) {
            holder.imagenCard.setImageDrawable(context.getResources().getDrawable(R.drawable.circulo_marcha));
        }
        if (holder.mItem.tipoIncidencia.equals(MapaActivity.TIPO_OTRO)) {
            holder.imagenCard.setImageDrawable(context.getResources().getDrawable(R.drawable.circulo_otros));
        }
        if (holder.mItem.tipoIncidencia.equals(MapaActivity.TIPO_RETRASO)) {
            holder.imagenCard.setImageDrawable(context.getResources().getDrawable(R.drawable.circulo_retrazo));
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tituloCard;
        public final TextView descripcionCard;
        public final ImageView imagenCard;
        public Incidencias.DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tituloCard = (TextView) view.findViewById(R.id.card_incidencia);
            descripcionCard = (TextView) view.findViewById(R.id.card_descripcion);
            imagenCard = (ImageView) view.findViewById(R.id.card_imagen);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + descripcionCard.getText() + "'";
        }
    }
}
