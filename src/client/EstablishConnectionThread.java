package client;

import java.net.Socket;

public class EstablishConnectionThread extends Thread {

    private Session session;
    protected Socket clientSocket;
    private SendingThread sendingThread;
    private ReceivingThread receivingTread;

    public EstablishConnectionThread(Session session) {
        this.session = session;
    }

    @Override
    public void run() {
        try {
            System.out.println("client.EstablishConnectionThread: connecting ...");
            clientSocket = new Socket("127.0.0.1", 8001);
            System.out.println("client.EstablishConnectionThread: connected");
            System.out.println("client.EstablishConnectionThread: setting up communication threads ...");
            sendingThread = new SendingThread(this.session, this.clientSocket);
            receivingTread = new ReceivingThread(this.session, this.clientSocket);
            sendingThread.start();
            receivingTread.start();
            session.setSendingThread(sendingThread);
            session.setReceivingThread(receivingTread);
            System.out.println("client.EstablishConnectionThread: communication threads running ...");
        } catch (Exception e) {
            System.out.println("client.EstablishConnectionThread: some error occurred");
            e.printStackTrace();
        }
    }
}
