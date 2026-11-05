package edu.udb.recursosaprendizaje.network;

import edu.udb.recursosaprendizaje.model.Recurso;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ResourceService {

    @GET("recursos")
    Call<List<Recurso>> getRecursos();

    @GET("recursos/{id}")
    Call<Recurso> getRecurso(@Path("id") String id);

    @POST("recursos")
    Call<Recurso> agregarRecurso(@Body Recurso recurso);

    @PUT("recursos/{id}")
    Call<Recurso> modificarRecurso(@Path("id") String id, @Body Recurso recurso);

    @DELETE("recursos/{id}")
    Call<Void> eliminarRecurso(@Path("id") String id);
}
