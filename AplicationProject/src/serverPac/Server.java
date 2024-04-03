package serverPac;
import java.net.ServerSocket;
import java.net.Socket;


public class Server { //Se encarga de recibir las peticiones y las envia a ServerManager
    private ServerSocket socket;
    private ServerManager manager;
    public static final int port = 9099;


    public static void main(String[] args) throws Exception {
        new Server();

    }

    public Server() throws Exception{
        manager = new ServerManager();
        init();
    }

    public void init() throws Exception {
        socket = new ServerSocket(port);
        while (true){
            Socket s = socket.accept();
            new Thread(
                    new ClientThread(s, manager)
            ).start();
        }
    }
}
