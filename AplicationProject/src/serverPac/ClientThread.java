package serverPac;

import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import javax.sound.sampled.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

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
                    
                } else if (methodName.equals("createGroup")) {
                    String methodNameGroup = reader.readLine();
                    String usersGroup = reader.readLine();
                    manager.createGroups(methodNameGroup, usersGroup);
                    
                } else if (methodName.equals("forwardMessageToGroup")) {
                    String methodNameGroup = reader.readLine();
                    String message = reader.readLine();
                    manager.forwardTextMessageToGroup(methodNameGroup, message);


                } else if (methodName.equals("forwardVoiceCall")) {



                }else if (methodName.equals("forwardVoiceMessage")){
                    String audioUser = reader.readLine();
                    new Thread(()-> manager.forwardVoiceMessage(audioUser)).start();
                }

            }
            reader.close();
            socketClient.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
