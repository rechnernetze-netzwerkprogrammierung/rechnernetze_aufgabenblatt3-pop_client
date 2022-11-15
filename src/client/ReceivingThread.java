package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ReceivingThread extends Thread {
    private final InputStream receive;
    private final Session session;

    protected ReceivingThread(Session session, Socket clientSocket) throws Exception {
        this.receive = clientSocket.getInputStream();
        this.session = session;
    }

    @Override
    public void run() {
        System.out.println("client.ReceivingThread: has been started and is listening ...");
        while (!interrupted()) {
            try {
                char c;
                StringBuilder builder = new StringBuilder();
                while ((c= (char) receive.read())!= '\n') builder.append(c);
                System.out.println("Received" + builder.toString());
            }
            catch (SocketException e) {
                System.out.println("client.ReceivingThread: shutting down because connection reset ... ");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("client.ReceivingThread: shutting down because exception ... ");
                break;
            }
        }
    }
}
