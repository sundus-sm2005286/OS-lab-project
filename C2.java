import java.io.*;
import java.net.*;
import java.util.*;

public class Client2 {
    private static final String SERVER_ADDRESS = "192.168.177.128"; // Server IP address
    private static final int SERVER_PORT = 1300;
    private static final String SEARCH_SCRIPT = "/home/client2/Desktop/client2/search.sh";
    private static final String CLIENTINFO_SCRIPT = "/home/client2/Desktop/client2/client_info.sh";
    private static final String SYSTEM_INFO_REQUEST = "GET_SYSTEM_INFO";

    public static void main(String[] args) {
        while (true) {
            try (Socket socket = new Socket()) {

                System.out.println("Connecting to the server...");
                socket.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT), 5000);
                System.out.println("Connected to the server!");

                // Run search.sh and clientinfo.sh
                System.out.println("Running search.sh...");
                runScript(SEARCH_SCRIPT, false);

                System.out.println("Running client_info.sh...");
                runScript(CLIENTINFO_SCRIPT, true);

                // Request system info from the server every 5 minutes
                while (true) {
                    requestSystemInfo(socket);
                    Thread.sleep(300000); // Sleep for 5 minutes
                }
            } catch (SocketTimeoutException e) {
                System.err.println("Connection timed out. Retrying...");
            } catch (IOException | InterruptedException e) {
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
                System.out.println("Reconnecting in 5 seconds...");
                try {
                    Thread.sleep(5000); 
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    private static void runScript(String scriptPath, boolean allowPasswordInput) {
        try {
            System.out.println("Attempting to run script: " + scriptPath);


            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                System.err.println("Script not found: " + scriptPath);
                return;
            }


            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", scriptPath);
            Process process = processBuilder.start();


            if (allowPasswordInput) {
                System.out.print("Enter server password for SCP: ");
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                String password = userInput.readLine();

                // Write the password to the script's input
                PrintWriter scriptInput = new PrintWriter(process.getOutputStream());
                scriptInput.println(password);
                scriptInput.flush();
            }


            BufferedReader scriptReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = scriptReader.readLine()) != null) {
                System.out.println(line); 
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error running script: " + scriptPath + ", Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error running script: " + scriptPath);
            e.printStackTrace();
        }
    }

    // request system info from the server
    private static void requestSystemInfo(Socket socket) {
        try {

            if (socket.isClosed() || !socket.isConnected()) {
                System.out.println("Socket is closed or not connected.");
                return;
            }


            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(SYSTEM_INFO_REQUEST);
            System.out.println("Sent request: " + SYSTEM_INFO_REQUEST);

            // Receive system info
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response;
            boolean fileStarted = false;
            while ((response = in.readLine()) != null) {
                if (response.equals("FILE_START")) {
                    System.out.println("Server: Generating system info...");
                    fileStarted = true;
                    continue;
                }
                if (fileStarted) {
                    if (response.equals("FILE_END")) {
                        System.out.println("System info received.");
                        break;
                    }
                    // Print system info
                    System.out.println("System Info: " + response);
                }
            }
        } catch (SocketException e) {
            System.err.println("Lost connection to the server. Reconnecting...");
        } catch (IOException e) {
            System.err.println("Error requesting system info from the server.");
            e.printStackTrace();
        }
    }
}
