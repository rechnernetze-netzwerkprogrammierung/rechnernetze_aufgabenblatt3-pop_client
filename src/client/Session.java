package client;

import protocol.Pop3Client;

import java.util.LinkedList;

public class Session {
    private static int idCounter = 0;

    private int id;
    private ReceivingThread receivingThread;
    private SendingThread sendingThread;

    //in here this only prints the data to std out
    private ClientProtocol protocol = new Pop3Client();
    private LinkedList<byte[]> inputBuffer = new LinkedList<>();

    public Session() {
        id = idCounter++;
        EstablishConnectionThread e = new EstablishConnectionThread(this);
        e.start();
    }

    public byte[] processData() {
        try {
            byte[] data;
            synchronized (inputBuffer) {
                while (inputBuffer.size() == 0) inputBuffer.wait();
                data = inputBuffer.removeFirst();
            }
            return protocol.processData(data);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void pushInQueue(byte[] data) {
        synchronized (inputBuffer) {
            inputBuffer.add(data);
            inputBuffer.notify();
        }
    }

    public void setReceivingThread(ReceivingThread receivingThread) {
        this.receivingThread = receivingThread;
    }

    public void setSendingThread(SendingThread sendingThread) {
        this.sendingThread = sendingThread;
    }

    public void kill() {
        receivingThread.interrupt();
        sendingThread.interrupt();
    }
}
