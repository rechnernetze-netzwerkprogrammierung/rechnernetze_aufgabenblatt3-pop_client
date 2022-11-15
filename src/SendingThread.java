import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class SendingThread extends Thread {
    protected Socket clientSocket;
    protected DataOutputStream send;
    private final Object lock;

    private LinkedList<String> outputBuffer = new LinkedList<>();

    public SendingThread(Socket clientSocket) throws Exception {
        this.clientSocket = clientSocket;
        send = new DataOutputStream(clientSocket.getOutputStream());
        lock = new Object();
    }

    @Override
    public void run() {
        System.out.println("SendingThread: has been started ...");
        String command = null;
        while (true) {
            try {
                synchronized (lock) {
                    if (outputBuffer.isEmpty()) {
                        System.out.println("SendingThread: Pause sending thread");
                        lock.wait();
                        System.out.println("SendingThread: Continue sending thread");
                    }
                    command = outputBuffer.remove();
                }
                sendCommand(command);
                command = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCommand(String command) {
        synchronized (lock) {
            this.outputBuffer.add(command);
            lock.notify();
        }
    }

    public int sendCommand(String command) {
        try {
            System.out.println("SendingThread: sending " + command);
            send.writeBytes(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
