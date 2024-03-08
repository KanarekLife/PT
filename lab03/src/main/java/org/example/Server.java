package org.example;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    public static void main(String[] args) {
        try (var serverSocket = new ServerSocket(1234)) {
            System.out.println("Server started on localhost:1234. Waiting for connections...");

            while (true) {
                var clientSocket = serverSocket.accept();
                var connectionHandler = new Thread(new ConnectionHandler(clientSocket));
                connectionHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
