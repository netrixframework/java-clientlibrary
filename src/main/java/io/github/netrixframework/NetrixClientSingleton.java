package io.github.netrixframework;

public class NetrixClientSingleton {
    private static NetrixClient client;

    public static NetrixClient init(NetrixClientConfig c, DirectiveExecutor executor) {
        client = new NetrixClient(c, executor);
        return client;
    }

    public static NetrixClient getClient() {
        return client;
    }
}
