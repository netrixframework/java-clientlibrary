package org.netrix.comm;

import okhttp3.*;
import org.netrix.Config;
import org.netrix.Event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

public class NetrixClient {
    private OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private Config config;
    private JsonObject replicaJson;

    public NetrixClient(Config config) {
        this.config = config;
        JsonObject infoJson = new JsonObject();
        for(Entry<String, String> entry: config.info.entrySet()) {
            infoJson.addProperty(entry.getKey(), entry.getValue());
        }
        JsonObject replicaJson = new JsonObject();
        replicaJson.addProperty("id", config.replicaID);
        replicaJson.addProperty("ready", false);
        replicaJson.add("info", infoJson);
        replicaJson.addProperty("addr", config.clientAdvAddr);
        this.replicaJson = replicaJson;
    }

    public void sendMessage(Message message) throws IOException {
        sendRequest(config.netrixAddr+"/message", RequestBody.create(message.toJsonString(), JSON));
    }

    public void sendEvent(Event event) throws IOException {
        sendRequest(config.netrixAddr+"/event", RequestBody.create(event.toJsonString(), JSON));
    }

    public void register() throws IOException {
        replicaJson.addProperty("ready", false);
        Gson gson = new Gson();
        String replicaJsonString = gson.toJson(replicaJson);

        sendRequest(config.netrixAddr+"/replica", RequestBody.create(replicaJsonString, JSON));
    }

    public void setReady() throws IOException {
        replicaJson.addProperty("ready", true);
        Gson gson = new Gson();
        String replicaJsonString = gson.toJson(replicaJson);

        sendRequest(config.netrixAddr+"/replica", RequestBody.create(replicaJsonString, JSON));
    }

    public void unsetReady() throws IOException {
        replicaJson.addProperty("ready", false);
        Gson gson = new Gson();
        String replicaJsonString = gson.toJson(replicaJson);

        sendRequest(config.netrixAddr+"/replica", RequestBody.create(replicaJsonString, JSON));
    }

    public void sendRequest(String url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).execute();
    }

}
