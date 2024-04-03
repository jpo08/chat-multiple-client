package clientPac;
import javax.sound.sampled.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class CLI implements Runnable{

    public static final int BUFFER_SIZE = 1024;
    private Socket socket;
    private static final int PORT = 9099;
    private static PlayerThread playerThread;

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

            System.out.println("What would you like to do?");
            System.out.println("(1) Create a group chat" + "\n" +
            "(2) Add user to group" + "\n" +
            "(3) Send a message" + "\n" +
            "(4) Send an audio" + "\n" +
            "(5) Start a voice call" + "\n" +
            "(6) Review audios" + "\n" +
            "(7) Review the history of messages" + "\n" +
            "(exit) Exit the application" + "\n");

            //System.out.println("Write message user:message");

            while(!(line = rd.readLine()).equals("exit")){
                //String data[] = line.split(":");

                switch (line){
                    case "1":
                        String methodNameGroup;
                        String users;
                        System.out.println("Write the name of the new group");
                        methodNameGroup = rd.readLine();
                        System.out.println("Write users to include user1:user2:user3");
                        users = rd.readLine();
                        serverW.println("createGroup");
                        serverW.println(methodNameGroup);
                        serverW.println(users);
                        serverW.flush();
                        break;
                    case "2":
                        System.out.println("");
                        break;
                    case "3":
                        String message;
                        String answer;
                        System.out.println("Send the message to a");
                        System.out.println("(1) Specific person \n" +
                                "(2) Group chat ");
                        answer = rd.readLine();
                        if (answer.equals("1")){
                            System.out.println("Write message user:message");
                            message = rd.readLine();
                            String data[] = message.split(":");
                            serverW.println("forwardMessage");
                            serverW.println(data[1]);
                            serverW.println(data[0]);
                            serverW.flush();
                        } else if (answer.equals("2")) {
                            System.out.println("Write message groupName:message");
                            message = rd.readLine();
                            serverW.println("forwardMessageToGroup");
                            serverW.println(message);
                        }else {
                            System.out.println("The option is not available");
                        }
                        break;
                    case "4":
                        String audioAnswer;
                        String startRecord;
                        String audioUser;
                        System.out.println("Send the audio to a");
                        System.out.println("(1) Specific person \n" +
                                "(2) Group chat ");
                        audioAnswer = rd.readLine();
                        if (audioAnswer.equals("1")){
                            serverW.println("forwardVoiceMessage");
                            System.out.println("Write the user");
                            audioUser = rd.readLine();
                            System.out.println("Type start to begin the recording");
                            startRecord = rd.readLine();
                            if (startRecord.equals("start")){
                                serverW.println("forwardVoiceMessage");
                                serverW.println(audioUser);
                                startRecordingAndSend();
                            } else {
                                System.out.println("Command not recognize");
                            }
                        }


                        break;
                    case "5":
                        String callMessage = null;
                        String callAnswer;
                        String ip;
                        String port;
                        System.out.println("Send the audio message to a");
                        System.out.println("(1) Specific person \n" +
                                "(2) Group chat ");
                        callAnswer = rd.readLine();
                        if (callAnswer.equals("1")){
                            callMessage = rd.readLine();
                            ip = rd.readLine();
                            port = rd.readLine();
                            if(callMessage != null) {
                                AudioFormat format = new AudioFormat(44100, 16, 2, true, false);

                                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                                TargetDataLine callLine = (TargetDataLine) AudioSystem.getLine(info);

                                callLine.open(format);
                                callLine.start();
                                System.out.println("Receiving audio from microphone");

                                SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(format);
                                sourceDataLine.open(format);
                                sourceDataLine.start();

                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1028);

                                DatagramSocket socketD = new DatagramSocket();
                                while (true) {
                                    byteBuffer.clear();
                                    bytesRead = callLine.read(buffer, 0, buffer.length);
                                    if (bytesRead > 0) {
                                        byteBuffer.putInt(bytesRead);
                                        byteBuffer.put(buffer, 0, bytesRead);
                                        sendAudio(ip,Integer.parseInt(port), byteBuffer.array(), socketD);
                                    }
                                }
                            }

                        }
                        break;
                    case "6":
                        new Thread(() -> startAudio()).start();
                        break;
                    default:
                        System.out.println("Incorrect method name");
                }

//
//                System.out.println("Write message user:message");
            }
            serverW.println("exit");
            rd.close();
            serverW.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startRecordingAndSend() throws Exception {
        DatagramSocket socket = new DatagramSocket();
        AudioFormat audioFormat = new AudioFormat(44100.0f, 16, 2, true, true);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        if (!AudioSystem.isLineSupported(dataLineInfo)) {
            System.err.println("Audio line not supported!");
            System.exit(1);
        }

        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        line.open(audioFormat);
        line.start();

        System.out.println("Grabación iniciada. Escribe 'stop' para detener.");

        byte[] buffer = new byte[BUFFER_SIZE];
        ByteBuffer byteBuffer = ByteBuffer.allocate(1028);
        int count = 0;

        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine().trim();
            if (input.equalsIgnoreCase("stop")) {
                break;
            }

            int bytesRead = line.read(buffer, 0, buffer.length);
            byteBuffer.clear();
            byteBuffer.putInt(count);
            byteBuffer.put(buffer, 0, bytesRead);
            byte[] data = byteBuffer.array();
            sendAudio("localhost", 9099, data, socket);
            System.out.println("Sent packet " + count++);
        }

        line.stop();
        line.close();
        socket.close();
        System.out.println("Grabación detenida.");
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

    public static void startAudio(){
        try {
            // Configurar el socket
            DatagramSocket socket = new DatagramSocket(PORT);
            byte[] buffer = new byte[BUFFER_SIZE];

            // Configurar el reproductor de audio
            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
            SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            // Recibir los paquetes y reproducir el audio
            int count = 0;
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                buffer = packet.getData();
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                int packetCount = byteBuffer.getInt();
                if (packetCount == -1) {
                    System.out.println("Received last packet " + count);
                    break;
                } else {
                    byte[] data = new byte[1024];
                    byteBuffer.get(data, 0, data.length);
                    // System.arraycopy(buffer, 0, data, 0, data.length);
                    playerThread.addBytes(data);
                    System.out.println("Received packet " + packetCount + " current: " + count);

                }
                count++;
            }

            playerThread = new PlayerThread(audioFormat);
            playerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
