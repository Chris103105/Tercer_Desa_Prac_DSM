package edu.udb.recursosaprendizaje.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import edu.udb.recursosaprendizaje.R;
import edu.udb.recursosaprendizaje.model.Recurso;
import edu.udb.recursosaprendizaje.network.ResourceService;
import edu.udb.recursosaprendizaje.network.RetrofitClient;
import edu.udb.recursosaprendizaje.util.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourceFormActivity extends AppCompatActivity {

    private EditText etTitulo, etDescripcion, etEnlace, etImagen, etAutor;
    private AutoCompleteTextView actvTipo, actvNivel;
    private ImageView ivPreview;
    private ProgressBar progressBar;
    private Button btnGuardar, btnPreview;

    private ResourceService service;
    private String idRecurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_form);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etEnlace = findViewById(R.id.etEnlace);
        etImagen = findViewById(R.id.etImagen);
        etAutor = findViewById(R.id.etAutor);
        actvTipo = findViewById(R.id.actvTipo);
        actvNivel = findViewById(R.id.actvNivel);
        ivPreview = findViewById(R.id.ivPreview);
        progressBar = findViewById(R.id.progressBar);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnPreview = findViewById(R.id.btnPreview);

        service = RetrofitClient.getService();

        String[] tipos = getResources().getStringArray(R.array.tipos_recurso);
        String[] niveles = getResources().getStringArray(R.array.niveles_recurso);
        actvTipo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tipos));
        actvNivel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, niveles));

        idRecurso = getIntent().getStringExtra(Constants.EXTRA_ID);

        if (idRecurso != null) {
            setTitle(R.string.titulo_editar);
            btnGuardar.setText(R.string.btn_actualizar);
            cargarDatos();
        } else {
            setTitle(R.string.titulo_agregar);
            btnGuardar.setText(R.string.btn_guardar);
        }

        btnPreview.setOnClickListener(v -> mostrarImagen(etImagen.getText().toString().trim()));
        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void cargarDatos() {
        Intent intent = getIntent();

        etTitulo.setText(intent.getStringExtra("titulo"));
        etDescripcion.setText(intent.getStringExtra("descripcion"));
        actvTipo.setText(intent.getStringExtra("tipo"), false);
        etEnlace.setText(intent.getStringExtra("enlace"));
        etImagen.setText(intent.getStringExtra("imagen"));
        etAutor.setText(intent.getStringExtra("autor"));
        actvNivel.setText(intent.getStringExtra("nivel"), false);

        String imagen = intent.getStringExtra("imagen");
        if (imagen != null && !imagen.isEmpty()) {
            mostrarImagen(imagen);
        }
    }

    private void guardar() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String tipo = actvTipo.getText().toString().trim();
        String enlace = etEnlace.getText().toString().trim();
        String imagen = etImagen.getText().toString().trim();
        String autor = etAutor.getText().toString().trim();
        String nivel = actvNivel.getText().toString().trim();

        boolean valido = true;

        if (titulo.isEmpty()) {
            etTitulo.setError(getString(R.string.error_campo_requerido));
            valido = false;
        }
        if (descripcion.isEmpty()) {
            etDescripcion.setError(getString(R.string.error_campo_requerido));
            valido = false;
        }
        if (tipo.isEmpty()) {
            actvTipo.setError(getString(R.string.error_campo_requerido));
            valido = false;
        }
        if (enlace.isEmpty()) {
            etEnlace.setError(getString(R.string.error_campo_requerido));
            valido = false;
        } else if (!Patterns.WEB_URL.matcher(enlace).matches()) {
            etEnlace.setError(getString(R.string.error_url_invalida));
            valido = false;
        }
        if (!imagen.isEmpty() && !Patterns.WEB_URL.matcher(imagen).matches()) {
            etImagen.setError(getString(R.string.error_url_invalida));
            valido = false;
        }

        if (!valido) {
            Toast.makeText(this, R.string.msg_revisa_campos, Toast.LENGTH_SHORT).show();
            return;
        }

        Recurso recurso = new Recurso();
        recurso.setTitulo(titulo);
        recurso.setDescripcion(descripcion);
        recurso.setTipo(tipo);
        recurso.setEnlace(enlace);
        recurso.setImagen(imagen);
        recurso.setAutor(autor);
        recurso.setNivel(nivel);

        Call<Recurso> call;
        if (idRecurso != null) {
            call = service.modificarRecurso(idRecurso, recurso);
        } else {
            call = service.agregarRecurso(recurso);
        }

        progressBar.setVisibility(View.VISIBLE);
        btnGuardar.setEnabled(false);

        call.enqueue(new Callback<Recurso>() {
            @Override
            public void onResponse(Call<Recurso> call, Response<Recurso> response) {
                progressBar.setVisibility(View.GONE);
                btnGuardar.setEnabled(true);

                if (response.isSuccessful()) {
                    if (idRecurso != null) {
                        Toast.makeText(ResourceFormActivity.this, R.string.msg_recurso_actualizado, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ResourceFormActivity.this, R.string.msg_recurso_creado, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } else {
                    Toast.makeText(ResourceFormActivity.this,
                            getString(R.string.msg_error_servidor, response.code()),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Recurso> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnGuardar.setEnabled(true);
                Toast.makeText(ResourceFormActivity.this, R.string.msg_error_conexion, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarImagen(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, R.string.msg_sin_imagen, Toast.LENGTH_SHORT).show();
            return;
        }

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .centerCrop()
                .into(ivPreview);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
