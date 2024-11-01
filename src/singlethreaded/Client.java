package singlethreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    final int PORT = 8080;
    public void run() throws IOException, InterruptedException {
        InetAddress address = InetAddress.getByName("localhost");

        while (true) {
            try (Socket socket = new Socket(address, PORT)) {
                PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                toSocket.println("Hello World from socket " + socket.getLocalAddress());
                String lineFromSocket = fromSocket.readLine();
                System.out.println(lineFromSocket);
                toSocket.close();
                fromSocket.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            Thread.sleep(500);
        }

    }
    public static void main(String[] args) {
        Client client = new Client();

        try {
            client.run();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
