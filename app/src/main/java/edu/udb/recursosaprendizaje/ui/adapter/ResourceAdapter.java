package edu.udb.recursosaprendizaje.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import edu.udb.recursosaprendizaje.R;
import edu.udb.recursosaprendizaje.model.Recurso;

import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> {

    public interface OnItemClick {
        void verDetalle(Recurso recurso);
        void abrirEnlace(Recurso recurso);
        void editar(Recurso recurso);
        void eliminar(Recurso recurso);
    }

    private Context context;
    private List<Recurso> lista;
    private OnItemClick listener;

    public ResourceAdapter(Context context, List<Recurso> lista, OnItemClick listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recurso recurso = lista.get(position);

        holder.tvTitulo.setText(recurso.getTitulo());
        holder.tvDescripcion.setText(recurso.getDescripcion());
        holder.tvId.setText(context.getString(R.string.formato_id, recurso.getId()));

        if (recurso.getTipo() == null || recurso.getTipo().isEmpty()) {
            holder.tvTipo.setText(R.string.tipo_desconocido);
        } else {
            holder.tvTipo.setText(recurso.getTipo());
        }

        String autor = recurso.getAutor();
        String nivel = recurso.getNivel();
        boolean hayAutor = autor != null && !autor.isEmpty();
        boolean hayNivel = nivel != null && !nivel.isEmpty();

        if (hayAutor && hayNivel) {
            holder.tvMeta.setText(context.getString(R.string.formato_autor_nivel, autor, nivel));
            holder.tvMeta.setVisibility(View.VISIBLE);
        } else if (hayAutor) {
            holder.tvMeta.setText(context.getString(R.string.formato_autor, autor));
            holder.tvMeta.setVisibility(View.VISIBLE);
        } else if (hayNivel) {
            holder.tvMeta.setText(context.getString(R.string.formato_nivel, nivel));
            holder.tvMeta.setVisibility(View.VISIBLE);
        } else {
            holder.tvMeta.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(recurso.getImagen())
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .centerCrop()
                .into(holder.ivImagen);

        holder.itemView.setOnClickListener(v -> listener.verDetalle(recurso));
        holder.btnAbrir.setOnClickListener(v -> listener.abrirEnlace(recurso));
        holder.btnEditar.setOnClickListener(v -> listener.editar(recurso));
        holder.btnEliminar.setOnClickListener(v -> listener.eliminar(recurso));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImagen;
        TextView tvTipo, tvId, tvTitulo, tvMeta, tvDescripcion;
        Button btnAbrir, btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivImagen);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvId = itemView.findViewById(R.id.tvId);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            btnAbrir = itemView.findViewById(R.id.btnAbrir);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
