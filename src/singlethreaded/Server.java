package singlethreaded;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class Server {
    private static int cnt = 0;
    private static final int PORT = 8080;

    public void run() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(20000); // 20-second timeout for accepting connections
            System.out.println("Server is listening on port: " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    ++cnt;
                    System.out.println("Connected to " + clientSocket.getRemoteSocketAddress() + " " + cnt);

                    // Input and output streams
                    try (
                            BufferedReader fromClient =
                                 new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            BufferedWriter toClient =
                                 new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))
                    ) {

                        // Read and print HTTP request headers
                        StringBuilder request = new StringBuilder();
                        String line;
                        while ((line = fromClient.readLine()) != null && !line.isEmpty()) {
                            request.append(line).append("\n");
                        }

                        System.out.println("Received request:\n" + request);

                        // Prepare the response
                        String body = """
                                {
                                  "message": "Hello, World!",
                                  "status": "success"
                                }
                                """;

                        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
                        int contentLength = bodyBytes.length + 1;

                        String response = String.format("""
                                HTTP/1.1 200 OK\r
                                Connection: close\r
                                Content-Type: application/json\r
                                Content-Length: %d\r
                                Server: Kattapa/0.0.1 (Ubuntu)\r
                                \r
                                %s
                                """, contentLength, body);

//                        Thread.sleep(10000);
//                        simulateHeavyComputation();

                        // Send the response
                        toClient.write(response);
                        toClient.flush();
                    } 
                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timed out while waiting for a connection.");
                } catch (IOException e) {
                    System.err.println("Error handling connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    private void simulateHeavyComputation() throws InterruptedException {
        // Option 1: Simple sleep (similar to original)
//        Thread.sleep(5000);

        // Option 2: Actual CPU-intensive computation
        // Uncomment this and comment out the sleep to use CPU-intensive simulation instead

        long result = 0;
        for (long i = 0; i < 1_000_000_000L; i++) {
            result += Math.sqrt(i) * Math.sin(i);
            if (Thread.interrupted()) {
                throw new InterruptedException("Computation interrupted");
            }
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
