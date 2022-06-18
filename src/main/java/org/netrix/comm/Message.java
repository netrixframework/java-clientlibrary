package org.netrix.comm;

import com.google.gson.Gson;

public class Message {
    private String from;
    private String to;
    private byte[] data;
    private String type;

    private String id;

    private Boolean intercept = true;

    public Message(String to, String type, byte[] data) {
        this.to = to;
        this.data = data;
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Message fromJsonString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Message.class);
    }
}
