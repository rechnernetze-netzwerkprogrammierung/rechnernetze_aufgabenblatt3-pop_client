import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    private static final String REGEX_SEND_COMMAND_N_TIMES = "sendN [\\w]* [\\d]*";
    private static final String REGEX_CREATE_CONNECTION = "create session";
    private static final String REGEX_CHANGE_SESSION = "\\d";
    private static final String REGEX_HELP = "(help)|()";
    private static final String REGEX_EXECUTE_SCRIPT = "execute script";
    private static final String REGEX_EXECUTE_SCRIPT_FILENAME = "execute script [^ ]+";
    private static final String REGEX_REPEAT_SCRIPT = "repeat script [\\d]+";
    private static final String REGEX_REPEAT_SCRIPT_FILENAME = "repeat script [^ ]+ [\\d]+";
    private static final String REGEX_TOGGLE_DIRECT_MODE = "direct";
    private static final String REGEX_EXIT = "exit";

    static final String DEFAULT_SCRIPT_PATH = System.getProperty("user.dir");


    static Socket clientSocket;
    static DataOutputStream sendToServer;
    static DataInputStream response;
    static ClientConnection cc;
    static boolean direktMode = false;

    private static boolean close = false;

    public static void main(String[] args) throws Exception {
        cc = ClientConnection.getInstance();

        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");

        while (!close) {
            Thread.sleep(50);
            String who = cc.whois();
            System.out.print(who);
            parseCommand(scanner.nextLine());
        }
//        ClientConnection.getInstance().close();
    }

    public static void parseCommand(String command) {
        if (command.matches(REGEX_SEND_COMMAND_N_TIMES)) cc.sendCommand_N_times(command);
        else if (command.matches(REGEX_EXIT)) close = true;
        else if (command.matches(REGEX_HELP)) printHelp();
        else if (command.matches(REGEX_CREATE_CONNECTION)) cc.createSession();
        else if (command.matches(REGEX_CHANGE_SESSION)) cc.changeSession(command);
        else if (command.matches(REGEX_EXECUTE_SCRIPT)) executeScript();
        else if (command.matches(REGEX_EXECUTE_SCRIPT_FILENAME)) executeScriptFilename(command);
        else if (command.matches(REGEX_REPEAT_SCRIPT)) repeatScript(command);
        else if (command.matches(REGEX_REPEAT_SCRIPT_FILENAME)) repeatScriptFilename(command);
        else if (command.matches(REGEX_TOGGLE_DIRECT_MODE)) toggleDirectMode();
        else if (direktMode) ClientConnection.getInstance().sendCommand(command);
        else {
            System.out.println("Enter valid command or enable direct mode. Type 'help'.");
        }
    }


    private static void toggleDirectMode() {
        direktMode = !direktMode;
    }


    private static void executeScript() {
        System.out.println("--------------- START SCRIPT ----------------");
        executeCommandList(loadScript(chooseScriptFromList()));
        System.out.println("---------------- END SCRIPT -----------------");
    }

    private static void executeScriptFilename(String command) {
        String words[] = command.split(" ");
        executeCommandList(loadScript(words[2]));
    }

    private static void repeatScript(String command) {
        String words[] = command.split(" ");
        String script = chooseScriptFromList();
        int n = Integer.parseInt(words[2]);
        System.out.println("Executing n times " + script);
        for (int i = 0; i < n; i++) {
            executeCommandList(loadScript(script));
        }
    }

    private static void repeatScriptFilename(String command) {
        String words[] = command.split(" ");
        String script = words[2];
        int n = Integer.parseInt(words[3]);
        System.out.println("Executing n times " + script);
        for (int i = 0; i < n; i++) {
            executeCommandList(loadScript(script));
        }
    }

    private static void printHelp() {
        System.out.println("Anything that is no command is sent by the active connection to the server.");
        System.out.println("create session                       a new Session on the Server");
        System.out.println("[id]                                 marks the session of this id as aktive.");
        System.out.println("sendN [command] [n]                  sends the command n times");
        System.out.println("execute script [opt:filename]        loads script file and executes");
        System.out.println("                                     if no filename, choose dialog");
        System.out.println("repeat script [opt:filename] [N]     loads script file and executes it N times");
        System.out.println("                                     if no filename, choose dialog");
        System.out.println("direct                               toggles direkt mode. DEFAULT=false. ");
        System.out.println("                                     if enabled, commands that are not known are direct sent to the server");
        System.out.println("help                                 view this message");
    }

    public static void executeCommandList(LinkedList<String> lines) {
        if (lines == null) return;
        try {
            boolean oldDirectValue = direktMode;
            if (!direktMode) toggleDirectMode();
            while (!lines.isEmpty()) {
                Thread.sleep(50);
                parseCommand(lines.remove());
            }
            direktMode = oldDirectValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String chooseScriptFromList() {
        String[] folderContent = new File(DEFAULT_SCRIPT_PATH).list();
        LinkedList<String> scriptFileNames = new LinkedList<>();
        for (String content : folderContent) {
            if (content.endsWith(".script")) scriptFileNames.add(content);
        }

        if (scriptFileNames.isEmpty()) {
            System.out.println("No .script files found in " + DEFAULT_SCRIPT_PATH);
            return null;
        }
        String coice;
        if (scriptFileNames.size() > 1) {
            int count = 0;
            for (String file : scriptFileNames) {
                System.out.println(count++ + "\t " + file);
            }
            System.out.print("Which (int)? : ");
            Scanner scanner = new Scanner(System.in);

            int c = scanner.nextInt();
            coice = scriptFileNames.get(c);
        } else coice = scriptFileNames.getFirst();
        return coice;
    }

    public static LinkedList<String> loadScript(String filename) {
        try {
            File scriptFile = new File(DEFAULT_SCRIPT_PATH + "//" + filename);
            Scanner fileReader = new Scanner(scriptFile);

            LinkedList<String> lines = new LinkedList<>();
            while (fileReader.hasNext()) {
                String line = fileReader.nextLine();
                lines.add(line);
            }
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
