package com.fusedevelopments.websocket;

import jakarta.websocket.OnOpen;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Set;

@ServerEndpoint("/ws")
public class WebSocketServer {

    private static final NamespaceManager namespaceManager = new NamespaceManager();
    private static final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("New session opened: " + session.getId());

        JsonObject response = new JsonObject();
        response.addProperty("type", "welcome");
        response.addProperty("sessionId", session.getId());
        response.addProperty("status", true);

        session.getBasicRemote().sendText( gson.toJson(response) );

    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("Message received: " + message);
        try{
            JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);
            String type      = jsonMessage.get("type").getAsString();
            String namespace = jsonMessage.has("namespace") ?
                                jsonMessage.get("namespace").getAsString() :
                                "";
            String channel   = jsonMessage.has("channel") ?
                                jsonMessage.get("channel").getAsString() :
                                null;

            JsonObject response = new JsonObject();
            response.addProperty("type", type);
            response.addProperty("sessionId", session.getId());
            response.addProperty("status", true);

            ChannelManager channelManager = namespaceManager.getNamespace(namespace);

            // only add back if sent in
            if ( !channel.isEmpty() )
                response.addProperty("channel", channel);

            switch ( type ) {
                case "subscribe":
                    channelManager.subscribe(channel, session);
                    session.getBasicRemote().sendText( gson.toJson(response) );
                    break;
                case "unsubscribe":
                    channelManager.unsubscribe(channel, session);
                    session.getBasicRemote().sendText( gson.toJson(response) );
                    break;
                case "publish":
                    String content = jsonMessage.get("content").getAsString();
                    session.getBasicRemote().sendText( gson.toJson(response) );
                    channelManager.publish(channel, content, session);
                    break;
                case "getSubscriberCount":
                    int subscriberCount = channelManager.getSubscriberCount(channel);
                    response.addProperty("count", subscriberCount);
                    session.getBasicRemote().sendText( gson.toJson(response) );
                    break;
                case "getSubscriptions":
                    Set<String> subscriptions = channelManager.getSubscriptions(session);
                    response.add("channels", gson.toJsonTree(subscriptions));
                    session.getBasicRemote().sendText(gson.toJson(response));
                    break;
                default:
                    response.addProperty("status", false);
                    response.addProperty("message", "unknown command");
                    session.getBasicRemote().sendText( gson.toJson(response) );
            }
        }
        catch (JsonSyntaxException | NullPointerException e) {
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("type", "error");
            errorResponse.addProperty("status", false);
            errorResponse.addProperty("message", "Invalid message format");
            session.getBasicRemote().sendText( gson.toJson(errorResponse) );
        }
    }

    @OnClose
    public void onClose(Session session) {
        // Remove the session from all channels in all namespaces
        for ( String namespace : namespaceManager.getNamespaces() ) {
            ChannelManager channelManager = namespaceManager.getNamespace(namespace);
            channelManager.unsubscribeAll(session);
        }
        System.out.println("Session closed: " + session.getId());
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }
}
