import java.io.*;
import java.net.*;
import java.util.*;

public class S {
    private static final int PORT = 1300;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static List<ClientInfo> clientList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Server started, listening on port " + PORT);

        ServerSocket serverSocket = new ServerSocket(PORT);

        // Accept clients continuously
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // Create a new thread to handle the client
            new ClientHandler(clientSocket).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                clientList.add(new ClientInfo(socket.getInetAddress().toString(), new Date().toString()));

                System.out.println("Connected Clients:");
                for (ClientInfo client : clientList) {
                    System.out.println(client);
                }

                // Run the Network.sh script after connection
                runScript("./network.sh");

                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Received request: " + request); 
                    if (request.equals("GET_SYSTEM_INFO")) {

                        System.out.println("Received GET_SYSTEM_INFO request from client.");
                        
                        // Run the system.sh script to get system info
                        out.println("FILE_START");
                        runScript("./system.sh");  // this generates the system_info.txt

                        // Send system info to the client
                        sendSystemInfoToClient("/home/sondus/Desktop/server/system_info.txt");

                        out.println("FILE_END");

                        System.out.println("System info sent to the client.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }

        //  run shell scripts
        private void runScript(String scriptPath) {
            try {
                Process process = new ProcessBuilder("bash", scriptPath).start();
                process.waitFor();


                File systemInfoFile = new File("/home/sondus/Desktop/server/system_info.txt");
                if (!systemInfoFile.exists()) {
                    System.out.println("Error: system_info.txt was not created.");
                    out.println("ERROR: system_info.txt was not created.");
                    return;
                }
                if (systemInfoFile.length() == 0) {
                    System.out.println("Error: system_info.txt is empty.");
                    out.println("ERROR: system_info.txt is empty.");
                    return;
                }


                BufferedReader fileReader = new BufferedReader(new FileReader(systemInfoFile));
                String line;
                while ((line = fileReader.readLine()) != null) {
                    System.out.println("Generated System Info: " + line);  
                }
                fileReader.close();

            } catch (IOException | InterruptedException e) {
                System.out.println("Error running script " + scriptPath + ": " + e.getMessage());
            }
        }

        // send the system info file to the client
        private void sendSystemInfoToClient(String filePath) {
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
                String line;
                while ((line = fileReader.readLine()) != null) {
                    System.out.println("Sending line: " + line);  // Log the lines being sent
                    out.println(line);  // Send each line of the system info to the client
                }
                fileReader.close();
            } catch (IOException e) {
                System.out.println("Error sending system info: " + e.getMessage());
            }
        }
    }


    private static class ClientInfo {
        private String clientIP;
        private String connectedAt;

        public ClientInfo(String clientIP, String connectedAt) {
            this.clientIP = clientIP;
            this.connectedAt = connectedAt;
        }

        @Override
        public String toString() {
            return "ClientIP: " + clientIP + ", Connected At: " + connectedAt;
        }
    }
}
