package com.example.transporteinteligenteapp;


import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Modelo.Paradero;
import Modelo.Ruta;
import Modelo.TipoParadero;
import logic.DBConnection;

public class MapaActivity extends AppCompatActivity {

    private static final int CODIGO_COARSE = 500;
    private TextView tvCoordenadas, tvDireccion;
    private EditText buscadorDestino;
    private Button buscarParadero;

    private FloatingActionButton btnMiUbicacion;





    private  final String API_KEY="eyJvcmciOiI1YjNjZTM1OTc4NTExMTAwMDFjZjYyNDgiLCJpZCI6IjE4M2JmOTljOGU1NWQ4YWEzY2VkMTE1Y2U3NDE1YWEwNmI2MGM3Y2MxZDEwMzk3YmM5YzA3ZDkwIiwiaCI6Im11cm11cjY0In0=";
    // Fused location
    FusedLocationProviderClient fuselocationClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    List<GeoPoint> tramo1 = new ArrayList<>();
    List<GeoPoint> tramo2 = new ArrayList<>();


    // Ubicaciones
    private Location ubicacionInicial = new Location("");
    private Location ubicacionFinal = new Location("");

    private MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().setOsmdroidBasePath(new File(getCacheDir().getAbsolutePath(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(getCacheDir().getAbsolutePath(), "osmdroid/tiles"));

        setContentView(R.layout.activity_mapa);

        // Vistas
        tvCoordenadas = findViewById(R.id.tv_coordenadas);
        tvDireccion = findViewById(R.id.tv_direccion);
        mapView = findViewById(R.id.map_view);
        buscarParadero = findViewById(R.id.btn_buscar);
        buscadorDestino = findViewById(R.id.search_paradero);
        btnMiUbicacion = (FloatingActionButton)findViewById(R.id.fab_location);

        // Location client
        fuselocationClient = LocationServices.getFusedLocationProviderClient(MapaActivity.this);

        // Mapa
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(19.0);

        // Inicializa listeners y componentes

        Registrar();
        recorrerLista();

        SeleccionarDestinoAltoque();

        // Configuración de actualizaciones de ubicación
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMinUpdateDistanceMeters(1.0f)
                .build();





        buscarParadero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscador_destino();
            }
        });

        btnMiUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PedirPermisosDeUbicacion();
            }
        });

        // Registrar paraderos y marcadores en el mapa (usa la lista definida en Paraderos())



    }

    public void recorrerLista(){

        Ruta rutas=  crearRutasParaderos();

        for (Paradero p:rutas.getParaderos()){
            Log.d("RUTA", "paradero: " + p);

        }
    }


    // ------------------ Permisos y mapa ------------------
    public void PedirPermisosDeUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapaActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, CODIGO_COARSE);
        } else {
            fuselocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location ubicacion) {
                    if (ubicacion != null) {
                        ubicacionInicial = ubicacion;
                        Log.d("RUTA", "ubicacionInicial: " + ubicacionInicial.getLatitude() + "," + ubicacionInicial.getLongitude());

                        double latitud = ubicacion.getLatitude();
                        double longitud = ubicacion.getLongitude();
                        configurarMapaUsuario(latitud, longitud);

                    }
                }
            });
        }
    }



    // ------------------ Registro de paraderos ------------------
    public void Registrar() {
        DBConnection adminDB = new DBConnection( this);
        SQLiteDatabase baseDeDatos = adminDB.getWritableDatabase();
        Ruta rutas = crearRutasParaderos();

        for (Paradero p : rutas.getParaderos()) {
            agregarMarcador(p.getLat(), p.getLon(), p.getNombre(), "paradero");
            String nombre = p.getNombre();
            String tipo = p.getTipo().toString().trim();
            double lon = p.getLon();
            double lat = p.getLat();

            ContentValues registro = new ContentValues();
            registro.put("nombre", nombre);
            registro.put("tipo", tipo);
            registro.put("lon", lon);
            registro.put("lat", lat);

            baseDeDatos.insert("Paraderos", null, registro);
        }
        baseDeDatos.close();


    }

    // ------------------ Selección por toque ------------------

    public void SeleccionarDestinoAltoque() {
        mapView.getOverlays().add(new Overlay() {
            @Override
            public boolean onLongPress(MotionEvent e, MapView mapView) {
                GeoPoint punto = (GeoPoint) mapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());

                agregarMarcador(punto.getLatitude(), punto.getLongitude(), "Destino elegido", "destino");

                if (ubicacionFinal == null) ubicacionFinal = new Location("");

                ubicacionFinal.setLatitude(punto.getLatitude());
                ubicacionFinal.setLongitude(punto.getLongitude());
                Log.d("RUTA", "ubicacionFinal: " + ubicacionFinal.getLatitude() + "," + ubicacionFinal.getLongitude());

                CalcularRuta(crearRutasParaderos().getParaderos());
                return true;
            }
        });

    }


    // ------------------ Marcadores y mapa ------------------
    public void agregarMarcador(double lat, double lon, String titulo, String tipo) {
        GeoPoint punto = new GeoPoint(lat, lon);
        Marker marker = new Marker(mapView);
        marker.setPosition(punto);
        marker.setTitle(titulo);

        Drawable d;
        switch (tipo) {
            case "usuario":
                d = ContextCompat.getDrawable(this, android.R.drawable.presence_online);
                if (d != null) {
                    marker.setIcon(d);
                }
                break;
            case "destino":
                d = ContextCompat.getDrawable(this, android.R.drawable.ic_input_add);
                if (d != null) {
                    marker.setIcon(d);
                }
                break;
            case "paradero":
                d = ContextCompat.getDrawable(this, R.drawable.bus_stop);
                if (d != null) {
                    int size = (int) (60); // píxeles reales → prueba con 24, 32 o 48
                    Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    d.draw(canvas);

                    marker.setIcon(new BitmapDrawable(getResources(), bitmap));
                }
                break;
            default:
                d = ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map);
        }


        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    public void configurarMapaUsuario(double latitud, double longitud) {
        GeoPoint puntoInicialfijo = new GeoPoint(latitud, longitud);
        mapView.getController().setCenter(puntoInicialfijo);
        agregarMarcador(latitud, longitud, "Tu", "usuario");
    }

    // ------------------ Buscador (geocoding) ------------------
    public void buscador_destino() {
        String busqueda = buscadorDestino.getText().toString().trim();
        String api_key = API_KEY;
        String URL = "https://api.openrouteservice.org/geocode/search?api_key=" + api_key + "&text="+busqueda;

        RequestQueue queue = Volley.newRequestQueue(MapaActivity.this);

        JsonObjectRequest requestObject = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {
                    try {
                        double lat = response.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getDouble(1);
                        double lon = response.getJSONArray("features").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getDouble(0);
                        mapView.getController().setCenter(new GeoPoint(lat, lon));
                        mapView.getController().setZoom(18.0);
                    } catch (Exception e) {
                        buscadorDestino.setText("Error al obtener el lugar especifico: " + e.getMessage());
                    }
                },
                error -> buscadorDestino.setText("Error Volley " + error.getMessage())
        );

        queue.add(requestObject);
    }

    // ------------------ Lógica de paraderos y rutas ------------------
    public List<Paradero> ParaderoMasCercano(List<Paradero> paraderos, Location location, int k) {
        Log.d("RUTA", "Estoy en PARADEROmASCERCANO");

        List<Paradero> resultado = new ArrayList<>();
        paraderos.sort((p1, p2) -> {
            Location l1 = new Location("");
            l1.setLatitude(p1.getLat());
            l1.setLongitude(p1.getLon());

            Location l2 = new Location("");
            l2.setLatitude(p2.getLat());
            l2.setLongitude(p2.getLon());

            return Float.compare(location.distanceTo(l1), location.distanceTo(l2));
        });

        for (int i = 0; i < Math.min(k, paraderos.size()); i++) {
            resultado.add(paraderos.get(i));
        }
        return resultado;
    }

    // Calcular la mejor ruta entre orígenes y destinos cercanos
    public void CalcularRuta(List<Paradero> r) {
        Log.d("RUTA", "Estoy en Calcular RUta");

        List<Paradero> rutas1 = crearRutasParaderos().getParaderos();

        List<Paradero> OrigenesMasCercanos = ParaderoMasCercano(r, ubicacionInicial, 2);
        List<Paradero> DestinosMasCercanos = ParaderoMasCercano(r, ubicacionFinal, 2);

        for (  int i=0;i<OrigenesMasCercanos.size();i++){
            Log.d("Elementos","tengo # elementos "+ i);

        }


        for (  int i=0;i<DestinosMasCercanos.size();i++){
            Log.d("Elementos","tengo # elementos "+ i);

        }

        Ruta rutas = crearRutasParaderos();

        final int totalLlamadas = OrigenesMasCercanos.size() * DestinosMasCercanos.size();
        final int[] completadas = {0}; // contador mutable
        final ResultadoRuta resultadoRuta = new ResultadoRuta();

        for (Paradero p : OrigenesMasCercanos) {
            for (Paradero m : DestinosMasCercanos) {
                DistanciaTotal(rutas.getParaderos(), p, m, new RutaCallback() {
                    @Override
                    public void onSuccess(double distancia, double tiempo, List<GeoPoint> geopoints) {
                        synchronized (resultadoRuta) {

                            if (distancia< resultadoRuta.mejorDistancia) {
                                resultadoRuta.mejorDistancia = (float) distancia;
                                resultadoRuta.mejorOrigen = p;
                                resultadoRuta.mejorDestino = m;
                                resultadoRuta.distanciaGuardada = (float) distancia;
                                resultadoRuta.mejorGeoPoints = geopoints;
                                Log.d("Resultado", "mejordistancia : " +  resultadoRuta.mejorDistancia +
                                        "\nGeopoints: " +  resultadoRuta.mejorGeoPoints);




                            }
                        }

                        completadas[0]++;
                        if (completadas[0] == totalLlamadas) {
                            // todas terminaron
                            Log.d("Resultado", "estoy para dibujar mapa");

                            DibujarRutaMapaCaminando(resultadoRuta.mejorGeoPoints);

                            DibujarRutaMapaCarro(rutas1,resultadoRuta.mejorOrigen,resultadoRuta.mejorDestino);
                            agregarMarcador(resultadoRuta.mejorOrigen.getLat(), resultadoRuta.mejorOrigen.getLon(),
                                    "Origen óptimo", "paradero");
                            agregarMarcador(resultadoRuta.mejorDestino.getLat(), resultadoRuta.mejorDestino.getLon(),
                                    "Destino óptimo", "paradero");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        completadas[0]++;
                        if (completadas[0] == totalLlamadas) {
                            Toast.makeText(MapaActivity.this,"Error",Toast.LENGTH_LONG);                        }
                    }
                });
            }
        }
    }

    public void DibujarRutaMapaCaminando(List<GeoPoint> geopoints) {
        Polyline ruta = new Polyline();
        ruta.setPoints(geopoints);

        ruta.setColor(Color.CYAN); // color más visible
        ruta.setWidth(10f); // un poco más gruesa
        ruta.getOutlinePaint().setPathEffect(new DashPathEffect(new float[]{20, 20}, 0)); // discontinua
        ruta.getOutlinePaint().setAntiAlias(true);

        mapView.getOverlayManager().add(ruta);
        mapView.invalidate();
    }



    public void DibujarRutaMapaCarro(List<Paradero> ruta, Paradero mejorOrigen, Paradero mejorDestino) {
        String url = "https://api.openrouteservice.org/v2/directions/driving-car";
        JSONArray coords = new JSONArray();

        // Construir coordenadas desde mejorOrigen hasta mejorDestino
        int startIndex = -1;
        for (int i = 0; i < ruta.size(); i++) {
            if (ruta.get(i).getPosicion() == mejorOrigen.getPosicion()) {
                startIndex = i;
                break;
            }
        }

        if (startIndex != -1) {
            boolean seguir = true;
            int i = startIndex;
            while (seguir) {

                try {
                    Paradero p = ruta.get(i);
                    coords.put(new JSONArray(new double[]{p.getLon(), p.getLat()}));

                    if (p.getPosicion() == mejorDestino.getPosicion()) {
                        seguir = false;
                    }
                    i = (i + 1) % ruta.size(); // permite volver al inicio si es necesario
                }catch(Exception e){

                }
            }
        }

        try {
            JSONObject body = new JSONObject();
            body.put("coordinates", coords);

            RequestQueue queue = Volley.newRequestQueue(MapaActivity.this);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> {
                        try {
                            String polyline = response.getJSONArray("routes")
                                    .getJSONObject(0)
                                    .getString("geometry");

                            List<GeoPoint> geoPoints = decodePolyline(polyline);

                            // Dibujar ruta en el mapa
                            Polyline rutaCarro = new Polyline();
                            rutaCarro.setPoints(geoPoints);
                            rutaCarro.setColor(Color.RED);
                            rutaCarro.setWidth(8f);
                            rutaCarro.setGeodesic(true); // mejor suavizado sobre el mapa
                            rutaCarro.getOutlinePaint().setAntiAlias(true);

                            mapView.getOverlayManager().add(rutaCarro);
                            mapView.invalidate();




                        } catch (Exception e) {
                            Log.e("CARRO", "Error parseando respuesta: " + e.getMessage());
                        }
                    },
                    error -> Log.e("CARRO", "Error en petición: " + error.getMessage())
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", API_KEY);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(request);

        } catch (Exception e) {
            Log.e("CARRO", "Error construyendo JSON: " + e.getMessage());
        }
    }





    // Callback para rutas
    public interface RutaCallback {
        void onSuccess(double distancia, double tiempo, List<GeoPoint> geopoint);

        void onError(String error);
    }


    //callback final



    // Llamada a OpenRouteService para obtener distancia caminando (asíncrona)
    public void distanciaCaminando(Location uno, Location dos, RutaCallback callback) {
        Log.d("RUTA", "Estoy en Distancia Caminando");

        String apiKey = API_KEY; // reemplaza



        double lonUsuario = uno.getLongitude();
        double latUsuario = uno.getLatitude();

        double lonParadero = dos.getLongitude();
        double latParadero = dos.getLatitude();

        String url = "https://api.openrouteservice.org/v2/directions/foot-walking";

        try {
            JSONObject body = new JSONObject();
            JSONArray coords = new JSONArray();
            coords.put(new JSONArray(new double[]{lonUsuario, latUsuario}));
            coords.put(new JSONArray(new double[]{lonParadero, latParadero}));
            body.put("coordinates", coords);

            RequestQueue queue = Volley.newRequestQueue(MapaActivity.this);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> {
                        Log.d("VOLLEY", "Respuesta recibida: " + response.toString());
                        try {
                            double distancia = response
                                    .getJSONArray("routes")
                                    .getJSONObject(0)
                                    .getJSONObject("summary")
                                    .getDouble("distance");

                            double tiempo = response
                                    .getJSONArray("routes")
                                    .getJSONObject(0)
                                    .getJSONObject("summary")
                                    .getDouble("duration");

                            Log.d("VOLLEY", "Distancia: " + distancia + " m, Tiempo: " + tiempo + " s");

                            //dibujar en el mapa de usuario a paradero origen

                            String polyline = response
                                    .getJSONArray("routes")
                                    .getJSONObject(0)
                                    .getString("geometry");

                            // Decodificar polyline a lista de puntos
                            List<GeoPoint> geoPoints = decodePolyline(polyline);
                            for (GeoPoint p : geoPoints) {
                                Log.d("RUTA", "Punto: lat=" + p.getLatitude() + ", lon=" + p.getLongitude());
                            }

                            // Retornar por callback
                            callback.onSuccess(distancia, tiempo, geoPoints);

                        } catch (Exception e) {
                            Log.e("VOLLEY", "Error en la petición: " + e.toString());
                            callback.onError("Error parseando JSON: " + e.getMessage());
                        }
                    },
                    error -> callback.onError("Error en la petición: " + error.getMessage())
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", apiKey); // 👈 aquí va tu API key
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };


            queue.add(request);


        } catch (Exception e) {
            callback.onError("Error creando body JSON: " + e.getMessage());
        }
    }



    // Calcula la distancia total entre paraderos desde origen hasta destino (suma euclidiana + distancia usuario->origen vía API)
    public void DistanciaTotal(List<Paradero> r,
                               Paradero origen, Paradero destino,
                               RutaCallback callback) {

        final float[] DistanciaMaxima = {Float.MAX_VALUE};

        Log.d("RUTA", "Estoy en DistanciaTotal");

        Location origenLocation = new Location("");
        origenLocation.setLatitude(origen.getLat());
        origenLocation.setLongitude(origen.getLon());

        final float[] resultado = {0}; // mutable

        // Buscar índice del origen
        int indexOrigen = -1;
        for (int i = 0; i < r.size(); i++) {
            if (r.get(i).getPosicion() == origen.getPosicion()) {
                indexOrigen = i;
                break;
            }
        }

        if (indexOrigen == -1) {
            Log.e("RUTA", "Origen no encontrado en la lista");
            return;
        }

        // Recorrido circular desde el origen hasta encontrar el destino
        int i = indexOrigen;
        while (true) {
            Paradero actual = r.get(i);
            Paradero siguiente = r.get((i + 1) % r.size());

            Log.d("RUTA", "Evaluando: actual=" + actual.getPosicion() +
                    " -> siguiente=" + siguiente.getPosicion() +
                    " (acumulado=" + resultado[0] + ")");

            Location l1 = new Location("");
            l1.setLatitude(actual.getLat());
            l1.setLongitude(actual.getLon());

            Location l2 = new Location("");
            l2.setLatitude(siguiente.getLat());
            l2.setLongitude(siguiente.getLon());

            resultado[0] += l1.distanceTo(l2);
            Log.d("RUTA", "hola paso por DistanciaTotal ");

            if (siguiente.getPosicion() == destino.getPosicion()) {
                Log.d("RUTA", "DESTINO encontrado en pos=" + destino.getPosicion());

                // Paso 1: caminando de usuario a paradero origen
                distanciaCaminando(ubicacionInicial, origenLocation, new RutaCallback() {
                    @Override
                    public void onSuccess(double distancia1, double tiempo1, List<GeoPoint> geoPoints1) {

                        // Paso 2: caminando de paradero destino al destino final
                        Location destinoLocation = new Location("");
                        destinoLocation.setLatitude(destino.getLat());
                        destinoLocation.setLongitude(destino.getLon());

                        distanciaCaminando( destinoLocation,ubicacionFinal, new RutaCallback() {
                            @Override
                            public void onSuccess(double distancia2, double tiempo2, List<GeoPoint> geoPoints2) {

                                double distanciaTotal = resultado[0] + distancia1 + distancia2;
                                double tiempoTotal = tiempo1 + tiempo2;

                                if (distanciaTotal < DistanciaMaxima[0]) {
                                    DistanciaMaxima[0] = (float) distanciaTotal;
                                    tramo1.clear();
                                    tramo1.addAll(geoPoints1);

                                    tramo2.clear();
                                    tramo2.addAll(geoPoints2);
                                }

                                Log.d("RUTA", "paradero=" +  destino.getNombre()+ "DISTANCIA TOTAL=" + distanciaTotal + ", TIEMPO TOTAL=" + tiempoTotal);
                                callback.onSuccess(distanciaTotal, tiempoTotal, geoPoints1);
                            }

                            @Override
                            public void onError(String error) {
                                callback.onError(error);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
                return;
            }

            i = (i + 1) % r.size();

            // Seguridad: si damos la vuelta completa y no encontramos destino
            if (i == indexOrigen) {
                Log.e("RUTA", "Destino no encontrado en la lista circular");
                break;
            }
        }
    }


    // ------------------ Lista de paraderos (datos) ------------------
    public Ruta crearRutasParaderos() {
        Ruta r = new Ruta();
        r.agregar(new Paradero(1, "Cesar Agudelo", TipoParadero.BAJADA, -75.784556, 5.23325));
        r.agregar(new Paradero(2, "La Criolla", TipoParadero.AMBOS, -75.784430, 5.228571));
        r.agregar(new Paradero(3, "Terpel", TipoParadero.AMBOS, -75.787804, 5.226810));
        r.agregar(new Paradero(4, "Galeria", TipoParadero.SUBIDA, -75.786745, 5.231837));
        r.agregar(new Paradero(5, "Simon Bolivar", TipoParadero.SUBIDA, -75.785400, 5.235963));
        r.agregar(new Paradero(6, "Terminal", TipoParadero.SUBIDA, -75.782432, 5.242028));
        r.agregar(new Paradero(7, "El Barranco", TipoParadero.BAJADA, -75.785755, 5.237309));
        r.agregar(new Paradero(8, "Punto Verde", TipoParadero.BAJADA, -75.787402, 5.231366));





















        return r;
    }

    // Clase contenedora resultado para evitar problemas de variables finales
    private static class ResultadoRuta {
        float mejorDistancia = Float.MAX_VALUE;
        float distanciaGuardada = 0f;
        Paradero mejorOrigen = null;
        Paradero mejorDestino = null;

        List<GeoPoint> mejorGeoPoints=null;
    }

    public static List<GeoPoint> decodePolyline(String encoded) {
        List<GeoPoint> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double latitude = lat / 1E5;
            double longitude = lng / 1E5;
            poly.add(new GeoPoint(latitude, longitude));
        }
        return poly;
    }



}
