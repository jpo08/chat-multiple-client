package serverPac;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class ServerManager implements Services {

    private HashMap<String, Address> users;

    public ServerManager(){
        users = new HashMap<>();
    }

    @Override
    public void subscribe(String user, String ip, int port) {
        System.out.println("subscribe "+user+ " "+ ip + " "+ port);
        Address a = new Address(ip , port);
        a.ip = ip;
        a.port = port;
        users.put(user, a);
    }

    @Override
    public void createGroups() {


    }

    @Override
    public void forwardTextMessage(String m, String user) {
        System.out.println("forward "+m+" "+user);
        if(users.containsKey(user)){
            try {
                Address usA = users.get(user);
                Socket callSocket = new Socket(usA.ip, usA.port);
                PrintWriter bw = new PrintWriter(new OutputStreamWriter(callSocket.getOutputStream()));
                bw.println(m);
                bw.close();
                callSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void forwardVoiceMessage() {

    }

    @Override
    public void forwardVoiceCall() {

    }

    @Override 

    public void showChatHistory(){


    }
}
