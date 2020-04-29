import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;


/**
 * Author: Jingxian Bao
 * Last Modified: Feb 19, 2020
 *
 * This program demonstrates a TCP server.
 * The server will make two checks before servicing any client request. First, does the
 * public key (included with each request) hash to the ID (also provided with each
 * request)? Second, is the request properly signed? If both of these are true, the request
 * is carried out on behalf of the client. The server will add, subtract or view. Otherwise,
 * the server returns the message “Error in request”.
 *
 */


public class EchoServerTCP {
    private static boolean isValid(String msg){
        //check 1: does the public key (included with each request) hash to the ID (also provided with each request)?
        String[] msgArr = msg.split(",");
        String ClientId = msgArr[0];//get client id
        //get public key
        String publicKeyString = msgArr[1];
        BigInteger e = new BigInteger(publicKeyString.split("\\+")[0]);
        BigInteger n = new BigInteger(publicKeyString.split("\\+")[1]);
        //calculate the public key hash
        String publicKeyHash = BabyHash.ComputeSHA_256_as_Hex_String(publicKeyString.toLowerCase());
        //check if the last20Bytes equals to the client id
        if (!publicKeyHash.substring(publicKeyHash.length()-40,publicKeyHash.length()).toUpperCase().equals(ClientId)){
            //System.out.println("Check 1 failed");
            return false;
        }
        //check 2: is the request properly signed?
        // Take the encrypted string and make it a big integer
        BigInteger encryptedHash = new BigInteger(msgArr[msgArr.length-1]);
        // Decrypt it
        BigInteger decryptedHash = encryptedHash.modPow(e, n);
        // Get the bytes from messageToCheck
        String messageToCheck = String.join(",", Arrays.copyOfRange(msgArr, 0, msgArr.length-1));
        byte[] messageToCheckDigest = null;
        try{
            byte[] bytesOfMessageToCheck = messageToCheck.getBytes("UTF-8");
            // compute the digest of the message with SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            messageToCheckDigest = md.digest(bytesOfMessageToCheck);
        }
        catch (NoSuchAlgorithmException ex){
            System.out.println(ex.getMessage());
        }
        catch (UnsupportedEncodingException ex){
            System.out.println(ex.getMessage());
        }

        // we add a 0 byte as the most significant byte to keep
        // the value to be signed non-negative.
        byte[] extraByte = new byte[messageToCheckDigest.length+1];
        extraByte[0] = 0;   // most significant set to 0
        for (int i = 1; i < extraByte.length; i++){
            extraByte[i] = messageToCheckDigest[i-1];
        }

        // Make it a big int
        BigInteger bigIntegerToCheck = new BigInteger(extraByte);

        if(bigIntegerToCheck.compareTo(decryptedHash) != 0) {
            //System.out.println("Check 2 failed");
            return false;
        }
        return true;
    }
    public static void main(String args[]) {
        System.out.println("Server Running"); //indicate start of the server
        Socket clientSocket = null;
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
        try {
            int serverPort = 7777; // the server port we are using

            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);

            /*
             * Forever,
             *   wait for a new connection
             *   read a line from the socket
             */
            while(true){
                /*
                 * Block waiting for a new connection request from a client.
                 * When the request is received, "accept" it, and the rest
                 * the tcp protocol handshake will then take place, making
                 * the socket ready for reading and writing.
                 */
                clientSocket = listenSocket.accept();
                // If we get here, then we are now connected to a client.

                // Set up "in" to read from the client socket
                Scanner in;
                in = new Scanner(clientSocket.getInputStream());


                // Set up "out" to write to the client socket
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                while (in.hasNextLine()){
                    String data = in.nextLine(); //get the request
                    String replyString = "";
                    if (!isValid(data)){ //if check failed
                        replyString = "Error in request";
                    }
                    else{
                        //retrieve id, operation, val from byte arr
                        String[] m = data.split(",");
                        String cal_id = m[0];
                        if (!resultMap.containsKey(cal_id)){
                            resultMap.put(cal_id, 0);
                        }
                        int ops = Integer.valueOf(m[2]);

                        switch(ops){
                            case (1)://add
                                resultMap.put(cal_id,resultMap.get(cal_id)+Integer.valueOf(m[3]));
                                replyString = "OK";
                                break;
                            case (2)://subtract
                                resultMap.put(cal_id,resultMap.get(cal_id)-Integer.valueOf(m[3]));
                                replyString = "OK";
                                break;
                            case (3)://view
                                replyString = String.valueOf(resultMap.get(cal_id));
                                break;
                        }
                    }
                    out.println(replyString);
                    out.flush();
                    //print the reply string
                    System.out.println("Result: "+replyString);
                }
            }



            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());

            // If quitting (typically by you sending quit signal) clean up sockets
        }
        finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }

}
