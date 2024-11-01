package singlethreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    final int PORT = 8080;
    public void run() throws IOException {
        ServerSocket socket = new ServerSocket(PORT);
        socket.setSoTimeout(20000);

        while (true) {
            System.out.println("Server is listening on port: " + PORT);

            Socket acceptedConnection = socket.accept();
            System.out.println("Connected to " + acceptedConnection.getRemoteSocketAddress());

            PrintWriter toClient = new PrintWriter(acceptedConnection.getOutputStream(), true);
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(acceptedConnection.getInputStream()));

            String lineFromClient = fromClient.readLine();
            System.out.println(lineFromClient);
            toClient.println("Hello from the Server!");

            toClient.close();
        }
    }
    public static void main(String[] args) {
        Server server = new Server();

        try {
            server.run();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
