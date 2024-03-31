package serverPac;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable{

    private Socket socketClient;
    private ServerManager manager;

    public ClientThread(Socket s, ServerManager sm){
        socketClient = s;
        manager = sm;
    }

    @Override //Se difine el protocolo
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));

            while (true){
                String methodName = reader.readLine();
                if(methodName.equals("exit")){
                    break;
                }
                if (methodName.equals("forwardMessage")){
                    String meString = reader.readLine();
                    String uString = reader.readLine();

                    manager.forwardTextMessage(meString, uString);
                }else if(methodName.equals("subscribe")){
                    String user = reader.readLine();
                    String ip = reader.readLine();
                    String port = reader.readLine();
                    manager.subscribe(user, ip, Integer.parseInt(port));
                }
            }
            reader.close();
            socketClient.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
