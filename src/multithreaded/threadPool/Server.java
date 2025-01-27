package multithreaded.threadPool;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Server {
    private static final int PORT = 8080;
    private final ExecutorService threadPool;

    public Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    private Consumer<Socket> handleClient() {
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
//                StringBuilder request = new StringBuilder();
//                String line;
//                while ((line = fromClient.readLine()) != null && !line.isEmpty()) {
//                    request.append(line).append("\n");
//                }

//                System.out.println("Received request:\n" + request);

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

    private void simulateHeavyComputation() {

        long result = 0;
        for (long i = 0; i < 1_00_000_000L; i++) {
            result += Math.sqrt(i) * Math.sin(i);
        }

    }

    public void run() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(20000); // 20-second timeout for accepting connections
            System.out.println("Server is listening on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                this.threadPool.execute(() -> this.handleClient().accept(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        final int POOL_SIZE = 10;

        Server server = new Server(POOL_SIZE);
        try {
            server.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            server.threadPool.shutdown();
        }
    }
}
