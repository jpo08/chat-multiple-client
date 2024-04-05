package serverPac;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

public class ServerManager implements Services {
    private static final int BUFFER_SIZE = 1028;

    private static volatile boolean isRecording = false;
    private HashMap<String, Address> users;
    private HashMap<String, String[]> groups;

    public ServerManager(){
        users = new HashMap<>();
        groups = new HashMap<>();
    }

    @Override
    public void subscribe(String user, String ip, int port) {
        System.out.println("subscribe "+user+ " "+ ip + " "+ port);
        Address a = new Address();
        a.ip = ip;
        a.port = port;
        users.put(user, a);
    }

    @Override
    public void createGroups(String groupName, String groupUsers) {
        String[] group;
        group = groupUsers.split(":");
        groups.put(groupName, group);
        System.out.println("The group has been created");
    }


    @Override
    public void forwardTextMessageToGroup(String groupName, String message){
        if(groups.containsKey(groupName)){
            Arrays.stream(groups.get(groupName)).forEach((user)->{forwardTextMessage(message,user);});
        }
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
    public void checkAudiosFromUser(String user){
        if(users.containsKey(user)){
            File file = new File(user + "_audio.wav");
            if(file.exists()){
                try {
                    Address usA = users.get(user);
                    //Socket callSocket = new Socket(usA.ip, usA.port);

                    DatagramSocket socket = new DatagramSocket();

                    File audioFile = new File("img/song.wav");
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1028);
                    // Leer los datos de audio y enviarlos en paquetes
                    int count = 0;
                    while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                        byteBuffer.clear();
                        byteBuffer.putInt(count);
                        byteBuffer.put(buffer, 0, bytesRead);
                        byte data[] = byteBuffer.array();
                        sendAudio(usA.ip, usA.port, data, socket);
                        System.out.println("Sent packet " + count++);
                    }
                    byteBuffer.clear();
                    byteBuffer.putInt(-1);
                    byte data[] = byteBuffer.array();
                    sendAudio(usA.ip, usA.port, data, socket);
                    socket.close();

                } catch (SocketException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


//                PrintWriter bw = new PrintWriter(new OutputStreamWriter(callSocket.getOutputStream()));
//                    bw.println(file);
//                    bw.close();
//                    callSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }else {
//                System.out.println("The audio does not exist");
           }
        }
    }


    @Override
    public void forwardVoiceMessage(String user) {
        if(users.containsKey(user)) {
            Address usA = users.get(user);
            try {
                DatagramSocket socket = new DatagramSocket(9099);
                System.out.println("Servidor escuchando en el puerto 9099...");

                while (true) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    byte[] receivedData = packet.getData();
                    new Thread(() -> processAudio(receivedData, user)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void forwardVoiceCall() {

    }

    public static void processAudio(byte[] data, String user) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            int packetNumber = byteBuffer.getInt();

            // Verificar si es el último paquete
            if (packetNumber == -1) {
                isRecording = false;
                System.out.println("Reproducción finalizada.");
                return;
            }

            byte[] audioData = new byte[data.length - 4];
            System.arraycopy(data, 4, audioData, 0, audioData.length);

            if (!isRecording) {
                isRecording = true;
                saveAudioToFile(audioData, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendAudio(String ip, int port, byte[] data, DatagramSocket socket) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveAudioToFile(byte[] audioData, String user) {
        try {
            AudioFormat audioFormat = new AudioFormat(44100.0f, 16, 2, true, true);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(inputStream, audioFormat, audioData.length / audioFormat.getFrameSize());


            File file = new File(user + "_audio.wav");
            file.exists();
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);

            inputStream.close();
            audioInputStream.close();

            System.out.println("Audio grabado y guardado en " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override 
    public void showChatHistory(){
        try (BufferedReader reader = new BufferedReader(new FileReader("chat_history.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

