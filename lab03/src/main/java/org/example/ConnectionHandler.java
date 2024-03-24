package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public ConnectionHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    @Override
    public void run() {
        try {
            try {
                System.out.printf("Connected to client at %s:%d%n", clientSocket.getInetAddress(), clientSocket.getPort());
                outputStream.writeObject("ready");
                var n = (Integer)inputStream.readObject();
                outputStream.writeObject("ready for messages");
                for (int i = 0; i < n; i++) {
                    Message message = (Message) inputStream.readObject();
                    System.out.println("Received message from %s:%d: ".formatted(clientSocket.getInetAddress(), clientSocket.getPort()) + message);
                }
                outputStream.writeObject("finished");
                System.out.printf("Connection to client at %s:%d closed%n", clientSocket.getInetAddress(), clientSocket.getPort());
                close();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (SocketException e ) {
            if (e.getCause() == null) {
                System.out.printf("Connection to client at %s:%d closed%n", clientSocket.getInetAddress(), clientSocket.getPort());
            } else {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
