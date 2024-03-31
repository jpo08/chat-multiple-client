package clientPac;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {

    public static final int port = 9097;
    private ServerSocket socket;
    private  String serverIp;
    private int serverPort;

    public static void main(String[] args) throws Exception {
        new Client("localhost", 9099);
    }

    public Client(String ip, int port) throws Exception{
        serverIp = ip;
        serverPort = port;
        new Thread(
                new CLI(serverIp, serverPort)
        ).start();
        init();
    }

    public void init() throws Exception{
        socket = new ServerSocket(port);
        while (true){
            Socket servSoc = socket.accept();
            BufferedReader rd = new BufferedReader(new InputStreamReader(servSoc.getInputStream()));
            System.out.println(rd.readLine());
            rd.close();
            servSoc.close();
        }
    }

}
