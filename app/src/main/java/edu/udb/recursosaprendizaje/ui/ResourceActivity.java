package edu.udb.recursosaprendizaje.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.udb.recursosaprendizaje.R;
import edu.udb.recursosaprendizaje.model.Recurso;
import edu.udb.recursosaprendizaje.network.ResourceService;
import edu.udb.recursosaprendizaje.network.RetrofitClient;
import edu.udb.recursosaprendizaje.ui.adapter.ResourceAdapter;
import edu.udb.recursosaprendizaje.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourceActivity extends AppCompatActivity implements ResourceAdapter.OnItemClick {

    private RecyclerView rvRecursos;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvVacio;
    private EditText etBuscarId;
    private Button btnBuscar, btnVerTodos;
    private FloatingActionButton fabAgregar;

    private ResourceService service;
    private ResourceAdapter adapter;
    private List<Recurso> listaRecursos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);

        rvRecursos = findViewById(R.id.rvRecursos);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvVacio = findViewById(R.id.tvVacio);
        etBuscarId = findViewById(R.id.etBuscarId);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnVerTodos = findViewById(R.id.btnVerTodos);
        fabAgregar = findViewById(R.id.fabAgregar);

        service = RetrofitClient.getService();

        adapter = new ResourceAdapter(this, listaRecursos, this);
        rvRecursos.setLayoutManager(new LinearLayoutManager(this));
        rvRecursos.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent);
        swipeRefresh.setOnRefreshListener(() -> cargarRecursos());

        btnBuscar.setOnClickListener(v -> buscarPorId());

        btnVerTodos.setOnClickListener(v -> {
            etBuscarId.setText("");
            cargarRecursos();
        });

        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(ResourceActivity.this, ResourceFormActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRecursos();
    }

    private void cargarRecursos() {
        swipeRefresh.setRefreshing(true);

        service.getRecursos().enqueue(new Callback<List<Recurso>>() {
            @Override
            public void onResponse(Call<List<Recurso>> call, Response<List<Recurso>> response) {
                swipeRefresh.setRefreshing(false);
                listaRecursos.clear();

                if (response.isSuccessful() && response.body() != null) {
                    listaRecursos.addAll(response.body());
                    Toast.makeText(ResourceActivity.this,
                            getString(R.string.msg_recursos_cargados, listaRecursos.size()),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResourceActivity.this,
                            getString(R.string.msg_error_servidor, response.code()),
                            Toast.LENGTH_LONG).show();
                }
                actualizarLista();
            }

            @Override
            public void onFailure(Call<List<Recurso>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                listaRecursos.clear();
                actualizarLista();
                Toast.makeText(ResourceActivity.this, R.string.msg_error_conexion, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void buscarPorId() {
        String id = etBuscarId.getText().toString().trim();

        if (id.isEmpty()) {
            etBuscarId.setError(getString(R.string.error_id_requerido));
            return;
        }

        swipeRefresh.setRefreshing(true);

        service.getRecurso(id).enqueue(new Callback<Recurso>() {
            @Override
            public void onResponse(Call<Recurso> call, Response<Recurso> response) {
                swipeRefresh.setRefreshing(false);
                listaRecursos.clear();

                if (response.isSuccessful() && response.body() != null) {
                    listaRecursos.add(response.body());
                    Toast.makeText(ResourceActivity.this,
                            getString(R.string.msg_recurso_encontrado, id),
                            Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(ResourceActivity.this,
                            getString(R.string.msg_recurso_no_encontrado, id),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResourceActivity.this,
                            getString(R.string.msg_error_servidor, response.code()),
                            Toast.LENGTH_LONG).show();
                }
                actualizarLista();
            }

            @Override
            public void onFailure(Call<Recurso> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(ResourceActivity.this, R.string.msg_error_conexion, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarLista() {
        adapter.notifyDataSetChanged();

        if (listaRecursos.isEmpty()) {
            tvVacio.setVisibility(View.VISIBLE);
        } else {
            tvVacio.setVisibility(View.GONE);
        }
    }

    @Override
    public void verDetalle(Recurso recurso) {
        String mensaje = getString(R.string.detalle_formato,
                recurso.getId(),
                recurso.getTipo(),
                recurso.getNivel(),
                recurso.getAutor(),
                recurso.getDescripcion(),
                recurso.getEnlace());

        new AlertDialog.Builder(this)
                .setTitle(recurso.getTitulo())
                .setMessage(mensaje)
                .setPositiveButton(R.string.btn_abrir, (dialog, which) -> abrirEnlace(recurso))
                .setNegativeButton(R.string.btn_cerrar, null)
                .show();
    }

    @Override
    public void abrirEnlace(Recurso recurso) {
        String url = recurso.getEnlace();

        if (url == null || url.isEmpty()) {
            Toast.makeText(this, R.string.msg_sin_enlace, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.msg_no_se_puede_abrir, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void editar(Recurso recurso) {
        if (recurso.getId() == null) {
            Toast.makeText(this, R.string.msg_sin_id, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, ResourceFormActivity.class);
        intent.putExtra(Constants.EXTRA_ID, recurso.getId());
        intent.putExtra("titulo", recurso.getTitulo());
        intent.putExtra("descripcion", recurso.getDescripcion());
        intent.putExtra("tipo", recurso.getTipo());
        intent.putExtra("enlace", recurso.getEnlace());
        intent.putExtra("imagen", recurso.getImagen());
        intent.putExtra("autor", recurso.getAutor());
        intent.putExtra("nivel", recurso.getNivel());
        startActivity(intent);
    }

    @Override
    public void eliminar(Recurso recurso) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_eliminar_titulo)
                .setMessage(getString(R.string.dialog_eliminar_mensaje, recurso.getTitulo()))
                .setPositiveButton(R.string.btn_eliminar, (dialog, which) -> eliminarRecurso(recurso))
                .setNegativeButton(R.string.btn_cancelar, null)
                .show();
    }

    private void eliminarRecurso(Recurso recurso) {
        swipeRefresh.setRefreshing(true);

        service.eliminarRecurso(recurso.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful()) {
                    Toast.makeText(ResourceActivity.this, R.string.msg_recurso_eliminado, Toast.LENGTH_SHORT).show();
                    cargarRecursos();
                } else {
                    Toast.makeText(ResourceActivity.this,
                            getString(R.string.msg_error_servidor, response.code()),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(ResourceActivity.this, R.string.msg_error_conexion, Toast.LENGTH_LONG).show();
            }
        });
    }
}
