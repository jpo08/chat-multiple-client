package clientPac;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class PlayerThread extends Thread {
    private static int MAX_ITEMS_IN_QUEUE = 3;
    private int secondsBuffer = 280;
    BlockingQueue<byte[]> buffer;
    private SourceDataLine sourceDataLine;
    private int count = 0;
    private int packes = 0;

    public PlayerThread(AudioFormat audioFormat) {
        try {
            MAX_ITEMS_IN_QUEUE = (int) audioFormat.getSampleRate() * secondsBuffer *
                    audioFormat.getFrameSize()
                    / CLI.BUFFER_SIZE;

            System.out.println("Max items in queue: " + MAX_ITEMS_IN_QUEUE);
            buffer = new ArrayBlockingQueue<>(MAX_ITEMS_IN_QUEUE, true);
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBytes(byte[] bytes) {
        try {
            count++;
            buffer.put(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                if (buffer.isEmpty()) {
                    if (packes > 0) {
                        System.out.println("Packets: write " + packes + " add Count: " + count);
                        packes = 0;
                        count = 0;
                    }
                    Thread.yield();
                    continue;
                }
                byte[] bytes = buffer.take();
                packes++;
                sourceDataLine.write(bytes, 0, bytes.length);
                // System.out.println("Written " + w + " bytes to sound card. " +
                // buffer.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
