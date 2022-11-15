import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ReceivingThread extends Thread {
    private final BufferedReader receive;
    private final ClientConnection client = ClientConnection.getInstance();

    protected ReceivingThread(Socket clientSocket) throws Exception {
        this.receive = new BufferedReader(new InputStreamReader(new DataInputStream(clientSocket.getInputStream())));
    }

    @Override
    public void run() {
        System.out.println("ReceivingThread: has been started and is listening ...");
        boolean isConnected = true;
        while (isConnected) {
            try {
                String line = receive.readLine();
                client.received(line);
            } catch (SocketException e) {
                System.out.println("ReceivingThread: shutting down because connection reset ... ");
                isConnected = false;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ReceivingThread: shutting down because exception ... ");
                break;
            }
        }
    }
}
