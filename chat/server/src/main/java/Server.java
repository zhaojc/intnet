import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by daniel on 2015-01-23.
 */
public class Server {
    private int port;

    private ServerSocket serverSocket;

    private AtomicInteger clientIdInc;
    private HashMap<Integer, ClientThread> clients;
    private final Object clientsLock;

    private SimpleDateFormat logTimeFormat;

    private static final String SERVER_ALIAS = "server";
    private static Set<String> forbiddenUsernames = new HashSet<String>(Arrays.asList("server", "admin"));

    public Server(int port) {
        this.port = port;
        this.clientIdInc = new AtomicInteger();
        this.clients = new HashMap<Integer, ClientThread>();
        this.clientsLock = new Object();
        this.logTimeFormat = new SimpleDateFormat("yyy-MM-dd 'at' HH:mm:ss z");
    }

    public void start() {
        log("Starting up...");

        try {
            serverSocket = new ServerSocket(port);

            log("Server has started.");

            while(true) {
                Socket socket = serverSocket.accept();
                ClientThread client = new ClientThread(socket, clientIdInc.incrementAndGet());

                log("Accepted connection from " + socket.getInetAddress() + ", client id: " + client.getClientId());

                synchronized (clientsLock) {
                    clients.put(client.getClientId(), client);
                }

                client.start();
            }
        } catch (SocketException e) {
            log("Server socket closed.");
        } catch (IOException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

        for(ClientThread client : clients.values()) {
            client.close();
        }

        log("Server has stopped.");
    }

    public void shutdown() {
        log("Shutting down...");

        try {
            serverSocket.close();
        } catch (IOException e) {
            log("Failed to close server socket: " + e.getMessage());
        }
    }

    private void broadcast(Message message) {
        synchronized (clientsLock) {
            Iterator<ClientThread> iterator = clients.values().iterator();
            while(iterator.hasNext()) {
                ClientThread client = iterator.next();

                if(client.getUsername().equals(message.getSender()) ) {
                    continue;
                }

                if(!client.write(message)) {
                    client.close();
                    iterator.remove();
                }
            }
        }
    }

    private void disconnect(ClientThread client) {
        log("Trying to disconnect client " + client.getClientId() + "...");

        client.close();

        synchronized (clientsLock) {
            clients.remove(client.getClientId());
        }

        log("Client " + client.getClientId() + " has disconnected.");
    }

    private void log(String message) {
        System.out.println(getTimestamp() + ": " + message);
    }

    private String getTimestamp() {
        return logTimeFormat.format(new Date());
    }

    public static void main(String[] args) {
        int port = 1500;

        switch(args.length) {
            case 0:
                break;
            case 1:
                try {
                    port = Integer.parseInt(args[0]);
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid port number.");
                }
            default:
                System.out.println("Usage: java Server <port number>");
                return;
        }

        final Server server = new Server(port);

        /*
            Add a shutdown hook
         */
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    server.shutdown();
                    mainThread.join();
                } catch (InterruptedException e) {
                    System.out.println("Failed to shutdown: " + e.getMessage());
                }
            }
        });

        server.start();
    }

    private class ClientThread extends Thread {
        private int clientId;
        private String username;

        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientThread(Socket socket, int clientId) throws IOException {
            this.socket = socket;
            this.clientId = clientId;

            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public int getClientId() {
            return clientId;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public void run() {
            Message message;
            boolean active = false;
            while(username == null) {
                try {
                    message = Message.deserialize(reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                if(message == null) {
                    break;
                }

                if(message.getType() != Message.Type.LOGIN) {
                    Message out = new Message(Message.Type.ERROR);
                    out.addData("error", "Must login.");
                    write(out);
                } else {
                    try {
                        username = message.getObject("username", String.class);
                        handleCommand(message);
                        active = true;
                    } catch(IllegalArgumentException e) {
                        username = null;
                        Message out = new Message(Message.Type.ERROR);
                        out.addData("error", e.getMessage());
                        write(out);
                    }
                }
            }

            while(active) {
                try {
                    message = Message.deserialize(reader.readLine());
                } catch (SocketException e) {
                    log("Client " + clientId + " closed connection: " + e.getMessage());
                    break;
                } catch (IOException e) {
                    log("Failed to read input from client " + clientId + ": " + e.getMessage());
                    continue;
                }

                switch (message.getType()) {
                    case MESSAGE:
                        message.setSender(username);
                        broadcast(message);
                        break;
                    case LOGOUT:
                        message.setSender(username);
                        broadcast(message);
                        forbiddenUsernames.remove(username);
                        active = false;
                        break;
                    default:
                        try {
                            handleCommand(message);
                        } catch(IllegalArgumentException e) {
                            Message m = new Message(Message.Type.ERROR);
                            m.setSender(SERVER_ALIAS);
                            m.addData("error", e.getMessage());
                            write(m);
                        }
                }
            }

            disconnect(this);
        }

        private void handleCommand(Message message) {
            Message m;
            switch (message.getType()) {
                case LOGIN:
                case USERNAME:
                    String newUsername = message.getObject("username", String.class);
                    if(newUsername == null || forbiddenUsernames.contains(newUsername.toLowerCase())) {
                        throw new IllegalArgumentException("Bad username.");
                    }

                    forbiddenUsernames.remove(username);
                    forbiddenUsernames.add(newUsername);

                    message.setSender(username);
                    username = newUsername;
                    broadcast(message);

                    m = new Message(Message.Type.USERNAME_CHANGED);
                    m.addData("username", message.getObject("username", String.class));
                    break;
                case MEMBERS:
                    String[] membersArray = new String[clients.size()];
                    synchronized (clientsLock) {
                        int i = 0;
                        for(ClientThread client : clients.values()) {
                            membersArray[i] = client.getUsername();
                            i++;
                        }
                    }

                    m = message;
                    m.addData("members", membersArray);
                    break;
                case TIME:
                    message.addData("timestamp", getTimestamp());
                    message.setSender(SERVER_ALIAS);
                    m = message;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command.");
            }

            write(m);
        }

        private void close() {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("An error occurred when trying to disconnect client " + clientId + ": " + e.getMessage());
                }
            }

            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log("An error occurred when trying to disconnect client " + clientId + ": " + e.getMessage());
                }
            }

            if(writer != null) {
                writer.close();
            }
        }

        private boolean write(Message message) {
            if(!socket.isConnected()) {
                return false;
            }

            writer.println(message.serialize());

            return true;
        }
    }
}
