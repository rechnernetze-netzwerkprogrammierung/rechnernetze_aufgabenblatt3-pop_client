package ui;

import client.Session;

import java.util.Scanner;

public class CommandLineInterface implements Runnable {
    private Session session;
    private Thread commandPrompt;
    private boolean kill = false;
    private Scanner scanner = new Scanner(System.in);

    public CommandLineInterface(Session session) {
        this.session = session;
        this.commandPrompt = new Thread(this);
        this.commandPrompt.start();
    }

    @Override
    public void run() {
        while (!this.kill) parseCommand();
    }

    private void parseCommand() {
        try {
        System.out.print("Simple client: ");
        String input = scanner.nextLine();

            if (input == null || input.length() == 0 || input.matches("-h") || input.matches("help")) {
                this.printHelp();
            }
            String words[] = input.split(" ");

            for (int i = 0; i < words.length; i++) {
                switch (words[i]) {
                    case "PASS":
                        if (!hasNextWord(words, i)) return;
                        session.pushInQueue(("PASS " + words[++i] + "\n").getBytes());
                        break;
                    case "STAT":
                        session.pushInQueue("STAT".getBytes());
                        break;
                    case "LIST":
                        if (i + 1 < words.length && words[i + 1].matches("[\\d]+"))
                            session.pushInQueue(("LIST " + words[++i]).getBytes());
                        else session.pushInQueue("LIST".getBytes());
                        break;
                    case "TOP":
                        if (!hasNextNumber(words, i)) return;
                        session.pushInQueue(("TOP " + words[++i]).getBytes());
                        break;
                    case "RETR":
                        if (!hasNextNumber(words, i)) return;
                        session.pushInQueue(("RETR " + words[++i]).getBytes());
                        break;
                    case "DELE":
                        if (!hasNextNumber(words, i)) return;
                        session.pushInQueue(("DELE " + words[++i]).getBytes());
                        break;
                    case "QUIT":
                        session.pushInQueue("QUIT".getBytes());
                        break;
                    default: printHelp();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Input exception");
        }
    }

    private boolean hasNextNumber(String[] words, int i) {
        return hasNextWord(words, i) && words[i + 1].matches("[\\d]+");
    }

    private boolean hasNextWord(String[] words, int i) {
        if (i + 1 < words.length) return true;
        System.out.println("Command needs argument!");
        printHelp();
        return false;
    }

    private void printHelp() {
        System.out.println("Synopsis command [arg] \n"
                + "PASS <your password>     deliver the password to the server\n"
                + "STAT                     request num and size messages\n"
                + "TOP n                    request header of mail n\n"
                + "RETR n                   request mail n\n"
                + "DELE n                   mark msg n for delete\n"
                + "QUIT                     end session and do deletes");
    }
}
