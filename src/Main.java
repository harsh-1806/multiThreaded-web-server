public class Main {
    public static void main(String[] args) {
        String body = """
                    {
                      "message": "Hello, World!",
                      "status": "success"
                    }
                    """;

        System.out.println(String.format("""
                    HTTP/1.1 200 OK
                    Connection: keep-alive
                    Content-Type: application/json
                    Content-Length: %d
                    Server: Kattapa/0.0.1 (Ubuntu)
                    %s
                    """, body.length(), body));
    }
}