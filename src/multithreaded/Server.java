package multithreaded;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class Server {
    private static final int PORT = 8080;

    private Consumer<Socket> getConsumer() {
        return (clientSocket) -> {
            System.out.println("Connected to " + clientSocket.getRemoteSocketAddress());

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

//                simulateHeavyComputation();


                // Send the response
                toClient.write(response);
                toClient.flush();
            }catch (SocketTimeoutException e) {
                System.out.println("Socket timed out while waiting for a connection.");
            }catch (IOException e) {
                System.err.println("Error handling connection: " + e.getMessage());
            }

        };
    }

    public void run() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(20000); // 20-second timeout for accepting connections
            System.out.println("Server is listening on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                Thread thread = new Thread(() -> this.getConsumer().accept(clientSocket));

                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
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
