import java.io.*;
import java.net.*;
import java.util.*;

class ChatServer {
    private static final int PORT = 12345;
    private static Map<String, Socket> users = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static synchronized void addUser(String username, Socket socket) {
        users.put(username, socket);
        System.out.println("User " + username + " connected.");
    }

    static synchronized void sendMessage(String recipient, String message) {
        Socket socket = users.get(recipient);
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
