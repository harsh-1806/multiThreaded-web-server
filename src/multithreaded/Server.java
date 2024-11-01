package multithreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server {
    static final int PORT = 8080;

    public Consumer<Socket> getConsumer() {
        return (clientSocket) -> {
            try {
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
                toClient.println("Hello, from the server");
//                String lineFromClient = fient.readLine();
//                System.out.println(lineFromClient);
                toClient.close();
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static void main(String[] args) {
        Server server = new Server();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(10000);
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket acceptedSocket = serverSocket.accept();
                serverSocket.setSoTimeout(10000);
                Thread thread = new Thread(() -> server.getConsumer().accept(acceptedSocket));
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
