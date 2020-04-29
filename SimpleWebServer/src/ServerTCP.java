import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerTCP {

    private static ArrayList<String> readFileData(File file) throws IOException {
        Scanner inFile1 = new Scanner(file);
        ArrayList<String> result = new ArrayList<String>();
        while(inFile1.hasNextLine()){
            result.add(inFile1.nextLine());
        }
        return result;
    }

    public static void main(String args[]) {
        Socket clientSocket = null;
        ServerSocket listenSocket = null;
        try {
            int serverPort = 7777; // the server port we are using
            System.out.println("Listening to Connection...");

            // Create a new server socket
            listenSocket = new ServerSocket(serverPort);
            while (true) {
                try {
                    clientSocket = listenSocket.accept();
                    Scanner in;
                    in = new Scanner(clientSocket.getInputStream());
                    PrintWriter out;
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

                    while (in.hasNextLine()) {
                        String data = in.nextLine();
                        System.out.println("Echoing: " + data);

                        String file_name = data.split(" ")[1].substring(1) + ".html";
                        System.out.println(file_name);

                        File file = new File(file_name);

                        //read content to return to client
                        try {
                            ArrayList<String> fileData = readFileData(file);
                            out.println(("HTTP/1.1 200 OK\\n\\n"));
                            for (int i = 0; i < fileData.size(); i++){
                                out.println(fileData.get(i));
                            }
                            out.flush();
                            System.out.println("Success");
                            clientSocket.close();
                            break;

                        } catch (FileNotFoundException e){
                            System.out.println("not found");
                            out.println("HTTP/1.1 404 File Not Found\\n\\n");
                            out.flush();
                            clientSocket.close();
                            break;
                        }

                    }
                } catch (IOException e) {
                    System.out.println("IO Exception:" + e.getMessage());
                }
            }
        }
        catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        }
    }

}