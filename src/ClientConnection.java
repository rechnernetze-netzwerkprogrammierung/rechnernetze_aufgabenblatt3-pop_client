import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class ClientConnection {
    private static ClientConnection clientConnection;

    private LinkedList<String> awaitCommand;
    private ArrayList<Session> sessions = new ArrayList<>();
    private Session activeSession;


    public static ClientConnection getInstance() {
        if (clientConnection == null) clientConnection = new ClientConnection();
        return clientConnection;
    }

    private ClientConnection() {
//        EstablishConnectionThread establishConnectionThread = new EstablishConnectionThread();
//        establishConnectionThread.start();
    }

    public void sendCommand(String command) {
        if (activeSession == null) createSession();
        activeSession.sendCommand(command);
    }

    public void sendCommand_N_times(String command) {
        String words[] = command.split(" ");
        int n = Integer.parseInt(words[2]);
        System.out.println("ClientConnection.sendCommand_N_times n=" + n);
        System.out.print("sendCommand_N_times please enter expected response: ");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.next();
        this.awaitCommand = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            awaitCommand.add(response);
        }
        for (int i = 0; i < n; i++) {
            sendCommand(words[1]);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (awaitCommand == null || awaitCommand.isEmpty()) {
            System.out.println("sendCommand_N_times received " + response + " " + n + " times");
            awaitCommand = null;
            return;
        }
        System.out.println("sendCommand_N_times received " + response + " " + (n-awaitCommand.size()) + " times");
        awaitCommand = null;
    }

    public void received(String command) {
        if (awaitCommand != null) {
            if (awaitCommand.getFirst().equals(command)) awaitCommand.remove();
        }
        else System.out.println("ClientConnection.received(): "+command);
    }

    public void createSession() {
        Session session = new Session();
        System.out.println("Created Session " + session.getId());
        activeSession = session;
        System.out.println("Session " + session.getId() + " active");
        this.sessions.add(session);
    }

    public void changeSession(String command) {
        int id = Integer.parseInt(command);
        for(Session session: sessions) {
            if (session.getId() == id) {
                activeSession = session;
                System.out.println("Session " + session.getId() + " active");
                return;
            }
        }
        System.out.println("no such session");
    }

    public String whois() {
        if (activeSession != null) return "SimpleClient@" + Integer.toString(activeSession.getId()) + ":>";
        return "SimpleClient@null:>";
    }


}
