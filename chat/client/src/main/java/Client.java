import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by daniel on 2015-01-24.
 */
public class Client {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private String server;
    private int port;

    private SimpleDateFormat dateFormat;

    private String username;

    public Client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.dateFormat = new SimpleDateFormat("HH:mm:ss");
    }

    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            return false;
        }

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        } catch (IOException e) {
            displaySystemMessage("error", "[1] Failed to establish a connection.");
            return false;
        }

        try {
            Message message = new Message(Message.Type.LOGIN);
            message.addData("username", username);
            write(message);

            message = Message.deserialize(reader.readLine());
            if(message.getType() != Message.Type.USERNAME_CHANGED) {
                displaySystemMessage("error", "Username unavailable.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            displaySystemMessage("error", "[2] Failed to establish a connection.");
            return false;
        }

        new ServerListener().start();

        return true;
    }

    private void display(String str) {
        System.out.println(str);
        System.out.print("> ");
    }

    private void displayMessage(String from, String message, Date date) {
        display("[" + dateFormat.format(date) + "] " + from + ": " + message);
    }

    private void displaySystemMessage(String type, String message, Date date) {
        display("[" + dateFormat.format(date) + "] " + "[" + type + "] " + message);
    }

    private void displaySystemMessage(String type, String message) {
        System.out.println("[" + type + "] " + message);
    }

    public void write(Message message) {
        writer.println(message.serialize());
    }

    public boolean isDisconnected() {
        return socket.isClosed();
    }

    private void disconnect() {
        if(reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                displaySystemMessage("error", "An error occurred when trying to disconnect: " + e.getMessage());
            }
        }

        if(writer != null) {
            writer.close();
        }

        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                displaySystemMessage("error", "An error occurred when trying to disconnect: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        int port = 1500;
        String server = "localhost";
        String username;

        switch (args.length) {
            case 3:
                server = args[2];
            case 2:
                try {
                    port = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    System.out.println("Invalid port number.");
                }
            case 1:
                username = args[0];
                break;
            default:
                System.out.println("Usage: java Client <username> <port> <server>");
                return;
        }

        final Client client = new Client(server, port, username);
        if(!client.start()) {
            System.out.println("Failed to connect.");
            return;
        }

        /*
            Add a shutdown hook
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if(!client.isDisconnected()) {
                    client.write(new Message(Message.Type.LOGOUT));
                    client.disconnect();
                }
            }
        });

        Scanner scanner = new Scanner(System.in);
        Message message;
        while(true) {
            System.out.print("> ");
            try {
                message = MessageBuilder.create(scanner.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }

            client.write(message);

            if(message.getType() == Message.Type.LOGOUT) {
                break;
            }
        }

        client.disconnect();
    }

    class ServerListener extends Thread {
        @Override
        public void run() {
            while(true) {
                Message message;
                try {
                    message = Message.deserialize(reader.readLine());
                } catch (IOException e) {
                    displaySystemMessage("error", "Failed to message.");
                    continue;
                }

                if(message == null) {
                    displaySystemMessage("info", "Connection closed.");
                    System.exit(0);
                }

                String str,
                    type = "info";
                switch (message.getType()) {
                    case MESSAGE:
                        displayMessage(message.getSender(), message.getObject("message", String.class), message.getDate());
                        continue;
                    case LOGIN:
                        str = message.getObject("username", String.class) + " has joined.";
                        break;
                    case LOGOUT:
                        str = message.getSender() + " has left.";
                        break;
                    case USERNAME:
                        str = message.getSender() + " has changed username to " + message.getObject("username", String.class) + ".";
                        break;
                    case USERNAME_CHANGED:
                        username = message.getObject("username", String.class);
                        str = "Username set to " + username + ".";
                        break;
                    case TIME:
                        str = "The current time is " + message.getObject("timestamp", String.class) + ".";
                        break;
                    case MEMBERS:
                        StringBuilder sb = new StringBuilder();
                        sb.append("Current members:\n");
                        for(String member : (ArrayList<String>) message.getObject("members", ArrayList.class)) {
                            sb.append("\t -");
                            sb.append(member);
                            sb.append('\n');
                        }

                        str = sb.toString();
                        break;
                    case ERROR:
                        type = "error";
                        str = message.getObject("error", String.class);
                        break;
                    default:
                        continue;
                }

                displaySystemMessage(type, str, message.getDate());
            }
        }
    }
}
