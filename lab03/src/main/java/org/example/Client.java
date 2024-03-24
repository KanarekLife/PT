package org.example;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);

            var output = new ObjectOutputStream(socket.getOutputStream());
            var input = new ObjectInputStream(socket.getInputStream());

            System.out.printf("Connected to server at %s:%d%n", socket.getInetAddress(), socket.getPort());
            System.out.printf("Server says: %s%n", input.readObject());

            System.out.print("Enter the number of messages to send: ");
            var n = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
            output.writeObject(n);

            System.out.printf("Server says: %s%n", input.readObject());

            for (int i = 0; i < n; i++) {
                System.out.print("Enter a message: ");
                var message = new Message(i, new BufferedReader(new InputStreamReader(System.in)).readLine());
                output.writeObject(message);
            }

            System.out.printf("Server says: %s%n", input.readObject());
            System.out.printf("Closing connection to server at %s:%d%n", socket.getInetAddress(), socket.getPort());

            input.close();
            output.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}