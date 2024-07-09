package com.fusedevelopments.websocket;

import jakarta.websocket.Session;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {

    private final Map<String, Set<Session>> channels = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    public int getSubscriberCount(String channel) {
        Set<Session> channelSessions = channels.get(channel);
        return channelSessions != null ? channelSessions.size() : 0;
    }

    public Set<String> getSubscriptions(Session session) {
        Set<String> subscribedChannels = ConcurrentHashMap.newKeySet();

        for (Map.Entry<String, Set<Session>> entry : channels.entrySet()) {
            if (entry.getValue().contains(session)) {
                subscribedChannels.add(entry.getKey());
            }
        }

        return subscribedChannels;
    }

    public void publish(String channel, String message, Session publisherSession) {
        Set<Session> channelSessions = channels.get(channel);

        JsonObject response = new JsonObject();
        response.addProperty("channel", channel);

        // only allow publishing to subscribers
        if ( channelSessions != null && channelSessions.contains(publisherSession) ) {
            response.addProperty("type", "data");
            response.addProperty("publisherId", publisherSession.getId());
            response.addProperty("data", message);

            synchronized (channelSessions) {
                for (Session s : channelSessions) {
                    if ( s.isOpen() ) {
                        try {
                            s.getBasicRemote().sendText( gson.toJson(response) );
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        else {
            response.addProperty("type", "error");
            response.addProperty("message", "You are not subscribed to this channel.");
            try {
                publisherSession.getBasicRemote().sendText(gson.toJson(response));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void subscribe(String channel, Session session) {
        channels.computeIfAbsent(channel, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void unsubscribe(String channel, Session session) {
        Set<Session> channelSessions = channels.get(channel);
        if (channelSessions != null) {
            channelSessions.remove(session);
            if (channelSessions.isEmpty()) {
                channels.remove(channel);
            }
        }
    }

    public void unsubscribeAll(Session session) {
        for (String channel : channels.keySet()) {
            unsubscribe(channel, session);
        }
    }
}
