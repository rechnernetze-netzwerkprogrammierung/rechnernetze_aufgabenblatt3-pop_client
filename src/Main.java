import client.Session;
import ui.CommandLineInterface;

public class Main {
    public static void main(String[] args) {
        new Session();
        new CommandLineInterface(new Session());
    }
}