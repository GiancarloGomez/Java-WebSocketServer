package com.fusedevelopments.websocket;
import org.glassfish.tyrus.server.Server;

public class Main {
    public static void main(String[] args) {

        // Default values
        String hostName = "localhost";
        int port = 8081;
        String contextPath = "/ws";

        // Parsing command-line arguments
        for (String arg : args) {
            if (arg.startsWith("--hostName="))
                hostName = arg.split("=")[1];
            else if (arg.startsWith("--port="))
                port = Integer.parseInt(arg.split("=")[1]);
            else if (arg.startsWith("--contextPath="))
                contextPath = arg.split("=")[1];
        }

        // Create and start the server
        Server server = new Server(
                hostName,
                port,
                contextPath,
                null,
                WebSocketServer.class
        );

        try {
            String separator = "*".repeat( 80 ) + "\n";
            server.start();
            System.out.print(
                "\n" + separator.repeat( 2 ) +
                "Server started - requests can be made to http://" +
                hostName + ":" + port + contextPath + "/\n" +
                "Please press a key to stop the server.\n" +
                separator.repeat( 2 ) + "\n"
            );
            System.in.read();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            server.stop();
        }
    }
}
