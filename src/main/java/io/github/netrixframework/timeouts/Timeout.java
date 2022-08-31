package io.github.netrixframework.timeouts;

import com.google.gson.Gson;

import java.time.Duration;

public class Timeout {
    private String type;
    private Duration duration;

    private String replicaID;

    public Timeout(String type, Duration duration) {
        this.type = type;
        this.duration = duration;
    }

    public String key() {
        return this.type;
    }

    public Duration getDuration() {
        return  this.duration;
    }

    public String getReplicaID() {
        return replicaID;
    }

    public void setReplicaID(String replicaID) {
        this.replicaID = replicaID;
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Timeout fromJsonString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Timeout.class);
    }
}
