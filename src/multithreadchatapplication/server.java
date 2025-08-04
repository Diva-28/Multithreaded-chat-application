
package multithreadchatapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    static Vector<ClientHandler> clients = new Vector<>();
    static int clientId = 0;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4321);
        System.out.println("Server started. Waiting for clients...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            ClientHandler clientHandler = new ClientHandler(socket, "Client" + (++clientId));
            clients.add(clientHandler);
            clientHandler.start();
        }
    }
}

class ClientHandler extends Thread {
    Socket socket;
    String name;
    BufferedReader in;
    PrintWriter out;

    public ClientHandler(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.name = name;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void run() {
        try {
            out.println("Welcome " + name + "!");

            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(name + ": " + msg);

                for (ClientHandler client : Server.clients) {
                    if (client != this) {
                        client.out.println(name + ": " + msg);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(name + " disconnected.");
        }
    }
}