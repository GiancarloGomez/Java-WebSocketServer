package com.fusedevelopments.websocket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NamespaceManager {
    private final Map<String, ChannelManager> namespaces = new ConcurrentHashMap<>();

    public ChannelManager getNamespace(String namespace) {
        return namespaces.computeIfAbsent(namespace, k -> new ChannelManager());
    }

    public Set<String> getNamespaces() {
        return namespaces.keySet();
    }

    public void removeNamespace(String namespace) {
        namespaces.remove(namespace);
    }
}
