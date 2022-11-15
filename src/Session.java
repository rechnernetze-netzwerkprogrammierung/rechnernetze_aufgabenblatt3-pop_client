public class Session {
    private static int idCounter = 0;

    private int id;
    private ReceivingThread receivingThread;
    private SendingThread sendingThread;

    public Session() {
        id = idCounter++;
        EstablishConnectionThread e = new EstablishConnectionThread(this);
        e.start();
    }

    public void setReceivingThread(ReceivingThread receivingThread) {
        this.receivingThread = receivingThread;
    }

    public void setSendingThread(SendingThread sendingThread) {
        this.sendingThread = sendingThread;
    }

    public void sendCommand(String command) {
        sendingThread.sendCommand(command);
    }

    public int getId() {
        return id;
    }
}
