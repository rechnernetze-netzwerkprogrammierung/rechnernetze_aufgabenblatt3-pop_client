package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class SendingThread extends Thread {
    private Session session;
    private DataOutputStream send;

    public SendingThread(Session session, Socket clientSocket) throws Exception {
        this.session = session;
        this.send = new DataOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        while (!interrupted()) {
            byte[] data = session.processData();
            if (data != null) {
                sendData(data);
                sendData("\n".getBytes());
            }

        }
    }

    private void sendData(byte[] data) {
        try {
            this.send.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
