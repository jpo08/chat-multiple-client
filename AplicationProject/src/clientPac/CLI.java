package clientPac;
import java.io.*;
import java.net.Socket;
public class CLI implements Runnable{

    private Socket socket;

    public CLI(String ip, int port) throws Exception{
        socket = new Socket(ip,port);
    }

    @Override
    public void run() {

        try {
            PrintWriter serverW = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
            String line;

            System.out.println("user name:");
            line = rd.readLine();
            serverW.println("subscribe");
            serverW.println(line);
            serverW.println("localhost");
            serverW.println(""+Client.port);
            serverW.flush();
            System.out.println("Write message user:message");

            while(!(line = rd.readLine()).equals("exit")){
                String data[] = line.split(":");
                serverW.println("forwardMessage");
                serverW.println(data[1]);
                serverW.println(data[0]);
                serverW.flush();
                System.out.println("Write message user:message");
            }
            serverW.println("exit");
            rd.close();
            serverW.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
