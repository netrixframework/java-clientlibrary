package io.github.netrixframework.comm;

import okhttp3.*;
import io.github.netrixframework.NetrixClientConfig;
import io.github.netrixframework.Event;

import java.io.IOException;
import java.util.Map.Entry;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

public class NetrixCaller {
    private OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private NetrixClientConfig netrixClientConfig;
    private JsonObject replicaJson;

    public NetrixCaller(NetrixClientConfig netrixClientConfig) {
        this.netrixClientConfig = netrixClientConfig;
        JsonObject infoJson = new JsonObject();
        for(Entry<String, String> entry: netrixClientConfig.info.entrySet()) {
            infoJson.addProperty(entry.getKey(), entry.getValue());
        }
        JsonObject replicaJson = new JsonObject();
        replicaJson.addProperty("id", netrixClientConfig.replicaID);
        replicaJson.addProperty("ready", false);
        replicaJson.add("info", infoJson);
        replicaJson.addProperty("addr", netrixClientConfig.clientAdvAddr);
        this.replicaJson = replicaJson;
    }

    public void sendMessage(Message message) throws IOException {
        sendRequest(netrixClientConfig.netrixAddr+"/message", RequestBody.create(message.toJsonString(), JSON));
    }

    public void sendEvent(Event event) throws IOException {
        sendRequest(netrixClientConfig.netrixAddr+"/event", RequestBody.create(event.toJsonString(), JSON));
    }

    public void register() throws IOException {
        replicaJson.addProperty("ready", false);
        Gson gson = new Gson();
        String replicaJsonString = gson.toJson(replicaJson);

        sendRequest(netrixClientConfig.netrixAddr+"/replica", RequestBody.create(replicaJsonString, JSON));
    }

    public void setReady() throws IOException {
        replicaJson.addProperty("ready", true);
        Gson gson = new Gson();
        String replicaJsonString = gson.toJson(replicaJson);

        sendRequest(netrixClientConfig.netrixAddr+"/replica", RequestBody.create(replicaJsonString, JSON));
    }

    public void unsetReady() throws IOException {
        replicaJson.addProperty("ready", false);
        Gson gson = new Gson();
        String replicaJsonString = gson.toJson(replicaJson);

        sendRequest(netrixClientConfig.netrixAddr+"/replica", RequestBody.create(replicaJsonString, JSON));
    }

    public void sendRequest(String url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).execute();
    }

}
