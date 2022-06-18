package org.netrix;

import java.util.HashMap;

public class Config {

    public String replicaID;

    public String netrixAddr;
    public String clientServerAddr;
    public int clientServerPort;
    public String clientAdvAddr;

    public HashMap<String, String> info;

    public Config(String id, String nAddr, String cSAddr, int cSPort, String cAAddr, HashMap<String, String> info) {
        this.replicaID = id;
        this.netrixAddr = nAddr;
        this.clientAdvAddr = cAAddr;
        this.clientServerAddr = cSAddr;
        this.clientServerPort = cSPort;
        this.info = info;
    }

    public Config(String id, String nAddr, String cSAddr, HashMap<String, String> info) {
        this.replicaID = id;
        this.netrixAddr = nAddr;
        this.clientServerAddr = cSAddr;
        this.clientAdvAddr = cSAddr;
        this.info = info;
    }
}
