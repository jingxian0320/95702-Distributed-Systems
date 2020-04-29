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


public class ServerTCP {

    // check if the msg match with the sigature
    private static boolean isValid(String msg){
        // check variable value from json
        String clientId = JsonHelper.getField(msg,"ClientId"); //get client id
        String publicKeyString = JsonHelper.getField(msg,"PublicKey"); //get public key
        String signature = JsonHelper.getField(msg,"Signature"); //get signature
        int operationId = Integer.valueOf(JsonHelper.getField(msg,"OperationId"));
        String arg1 = JsonHelper.getField(msg,"Arg1");
        String arg2 = JsonHelper.getField(msg,"Arg2");

        //check 1: does the public key (included with each request) hash to the ID (also provided with each request)?
        BigInteger e = new BigInteger(publicKeyString.split("\\+")[0]);
        BigInteger n = new BigInteger(publicKeyString.split("\\+")[1]);
        //calculate the public key hash
        String publicKeyHash = Hash.ComputeSHA_256_as_Hex_String(publicKeyString.toLowerCase());
        //check if the last20Bytes equals to the client id
        if (!publicKeyHash.substring(publicKeyHash.length()-40,publicKeyHash.length()).toUpperCase().equals(clientId)){
            System.out.println("Check 1 failed");
            return false;
        }
        //check 2: is the request properly signed?
        // concat the string to verfiy
        String messageToCheck = "";
        messageToCheck += clientId;
        messageToCheck += publicKeyString;
        messageToCheck += String.valueOf(operationId);
        messageToCheck += arg1;
        messageToCheck += arg2;

        // Take the encrypted string and make it a big integer
        BigInteger encryptedHash = new BigInteger(signature);
        // Decrypt it
        BigInteger decryptedHash = encryptedHash.modPow(e, n);
        // Get the bytes from messageToCheck
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
            System.out.println("Check 2 failed");
            return false;
        }
        return true;
    }


    public static void main(String args[]) {
        System.out.println("Server Running"); //indicate start of the server
        Socket clientSocket = null;
        BlockChain blockChain = new BlockChain();
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
                        String clientId = JsonHelper.getField(data,"ClientId"); //get client id
                        String publicKeyString = JsonHelper.getField(data,"PublicKey"); //get public key
                        String signature = JsonHelper.getField(data,"Signature"); //get signature
                        int operationId = Integer.valueOf(JsonHelper.getField(data,"OperationId"));
                        String arg1 = JsonHelper.getField(data,"Arg1");
                        String arg2 = JsonHelper.getField(data,"Arg2");
                        if (blockChain.getChainSize() == 0){
                            // create a genesis Block
                            Block genesisBlock = new Block(0, blockChain.getTime(), "Genesis", 2);
                            blockChain.addBlock(genesisBlock);
                        }

                        long startTime;
                        long endTime;
                        switch(operationId){
                            case (0):
                                replyString = "";
                                replyString += ("Current size of chain: " + blockChain.getChainSize());
                                replyString += "\n";
                                replyString += ("Current hashes per second by this machine: " + blockChain.hashesPerSecond());
                                replyString += "\n";
                                replyString += ("Difficulty of most recent block: " + blockChain.getLatestBlock().getDifficulty());
                                replyString += "\n";
                                replyString += ("Nonce for most recent block: " + blockChain.getLatestBlock().getNonce());
                                replyString += "\n";
                                replyString += ("Chain hash: " + blockChain.chainHash);
                                replyString += "\n";
                                break;
                            case (1):
                                replyString = "";
                                Block newBlock = new Block(blockChain.getChainSize(), blockChain.getTime(), arg2, Integer.valueOf(arg1));
                                startTime = System.currentTimeMillis();
                                blockChain.addBlock(newBlock);
                                endTime = System.currentTimeMillis();
                                replyString += ("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
                                replyString += "\n";
                                break;
                            case (2):
                                replyString = "";
                                startTime = System.currentTimeMillis();
                                boolean isValid = blockChain.isChainValid();
                                replyString += ("Chain verification: " + isValid);
                                replyString += "\n";
                                endTime = System.currentTimeMillis();
                                if (!isValid){
                                    replyString += (blockChain.errorMessage());
                                    replyString += "\n";
                                }
                                replyString += ("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                                replyString += "\n";
                                break;
                            case (3):
                                replyString = (blockChain.toString());

                                replyString += "\n";
                                break;
                            case(4):
                                Block corruptBlock = blockChain.chain.get(Integer.valueOf(arg1));
                                corruptBlock.setData(arg2);
                                replyString = (String.format("Block %d now holds %s",Integer.valueOf(arg1),arg2));
                                replyString += "\n";
                                break;
                            case(5):
                                startTime = System.currentTimeMillis();
                                blockChain.repairChain();
                                endTime = System.currentTimeMillis();
                                replyString = ("Total execution time required to repair the chain was " + (endTime - startTime) + " milliseconds");
                                replyString += "\n";
                                break;
                        }
                    }
                    out.println(getJsonString(replyString));
                    out.flush();
                    //print the reply string
                    System.out.println(getJsonString(replyString));
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
    //create the json string in the format of {"Msg":"XXXXX"}
    private static String getJsonString(String reply){
        String result = "";
        result += "{\"Msg\":\"";
        result += reply;
        result += "\"}";
        result += "\n";
        return result;
    }


}
