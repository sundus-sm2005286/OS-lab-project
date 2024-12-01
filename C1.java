import java.io.*;
import java.net.*;
import java.util.*;

public class C1 {
    private static final String SERVER_ADDRESS = "192.168.177.128"; // Server IP address
    private static final int SERVER_PORT = 1300;
    private static final String LOGIN_SCRIPT = "/home/client1/Desktop/client1/login.sh";
    private static final String CHECK_SCRIPT = "/home/client1/Desktop/client1/check.sh";
    private static final String SYSTEM_INFO_REQUEST = "GET_SYSTEM_INFO";

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to the server!");

            System.out.println("Running login.sh...");
            runScript(LOGIN_SCRIPT);
            System.out.println("Running check.sh...");
            runScript(CHECK_SCRIPT);

            // Request system info from server every 5 minutes
            while (true) {
                requestSystemInfo(socket);
                Thread.sleep(300000); 
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

   
    private static void runScript(String scriptPath) {
        try {
            System.out.println("Attempting to run script: " + scriptPath);


            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                System.err.println("Script not found: " + scriptPath);
                return;
            }

            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", scriptPath);
            Process process = processBuilder.start();

            // user input for login.sh
            if (scriptPath.equals(LOGIN_SCRIPT)) {
                PrintWriter writer = new PrintWriter(process.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                System.out.print("Enter username: ");
                String username = reader.readLine();
                System.out.print("Enter password: ");
                String password = reader.readLine();

                // Send username and password as input to the script
                writer.println(username);
                writer.println(password);
                writer.flush();
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

    // request system info from server
    private static void requestSystemInfo(Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(SYSTEM_INFO_REQUEST);
            System.out.println("Sent request: " + SYSTEM_INFO_REQUEST);
    
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response;
            boolean fileStarted = false;
            FileWriter fileWriter = new FileWriter("/home/client1/Desktop/client1/system_info.txt");
    
            while ((response = in.readLine()) != null) {
                if (response.equals("FILE_START")) {
                    System.out.println("Server: Generating system info...");
                    fileStarted = true;
                    continue;
                }
                if (fileStarted) {
                    if (response.equals("FILE_END")) {
                        fileWriter.close();
                        System.out.println("System info received and saved.");
                        break;
                    }
                    // Write the system info to the file
                    fileWriter.write(response + "\n");
                    System.out.println("Writing: " + response);  
                }
            }
    
            displayFileContent("/home/client1/Desktop/client1/system_info.txt");
    
        } catch (IOException e) {
            System.err.println("Error requesting system info from the server.");
            e.printStackTrace();
        }
    }
    

    private static void displayFileContent(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            System.out.println("Displaying system info from file:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading system info file.");
            e.printStackTrace();
        }
    }
}
