package com.example.beanthere.network;

import android.content.ContentProviderOperation;

import com.example.beanthere.BuildConfig;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.*;

public class routesApi {

    private static final String routesURL =
            "https://routes.googleapis.com/directions/v2:computeRoutes";
    private static final String routesApiKey = BuildConfig.ROUTES_API_KEY;
    private static final OkHttpClient routeClient = new OkHttpClient();

    public static void generateRoute(LatLng origin, String destination, String travelMode, Callback callback){

        //creating json object for route origin
        JsonObject json = new JsonObject();

        JsonObject originObj = createOriginObject(origin);

        //json for route destination
        JsonObject destObj = new JsonObject();
        destObj.addProperty("placeId",destination );

        //json with origin and destination and travel mode
        json.add("origin", originObj);
        json.add("destination",destObj);
        json.addProperty("travelMode", travelMode);
        json.addProperty("polylineQuality","HIGH_QUALITY");

        RequestBody body = RequestBody.create(new Gson().toJson(json),
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(routesURL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Goog-Api-Key", routesApiKey)
                .addHeader("X-Goog-FieldMask","routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline")
                .build();

        routeClient.newCall(request).enqueue(callback);
    }

    public static JsonObject createOriginObject(LatLng origin){
        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("latitude", origin.latitude);
        coordinates.addProperty("longitude", origin.longitude);

        JsonObject latLng = new JsonObject();
        latLng.add("latLng", coordinates);

        JsonObject location = new JsonObject();
        location.add("location", latLng);

        return location;
    }
}
