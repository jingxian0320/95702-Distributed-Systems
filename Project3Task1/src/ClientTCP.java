import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Jingxian Bao
 * Last Modified: Feb 19, 2020
 *
 * This program demonstrates a TCP client.
 * Each time the client program runs, it will create new RSA public and private keys and
 * display these keys to the user.
 * The client’s ID will be formed by taking the least significant 20 bytes of the hash of the
 * client’s public key.
 * It will transmit add or subtract
 * or view requests to the server, along with the client ID and an option to exit
 * The client will also transmit its public key with each request.
 * Finally, the client will sign each request using its private key
 */



public class ClientTCP {
    Socket clientSocket;
    int serverPort = 7777;
    String privateKeyString;
    String publicKeyString;
    // Each public and private key consists of an exponent and a modulus
    BigInteger n; // n is the modulus for both the private and public keys
    BigInteger e; // e is the exponent of the public key
    BigInteger d; // d is the exponent of the private key
    String clientId;
    //constructor
    ClientTCP(){
        System.out.println("Client Running"); //indicate start of the client
        try{
            clientSocket = new Socket("localhost", serverPort);
            generateKeys();
            System.out.println("Public Key: ("+publicKeyString+")");
            System.out.println("Private Key: ("+privateKeyString+")");
            String publicKeyHash = Hash.ComputeSHA_256_as_Hex_String(publicKeyString.toLowerCase());
            clientId = publicKeyHash.substring(publicKeyHash.length()-40, publicKeyHash.length()).toUpperCase();

        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());//print SocketException
        }catch (IOException e){System.out.println("IO: " + e.getMessage());//print IOException
        }
    }
    private void close(){
        try {
            if (clientSocket != null) {
                clientSocket.close();
                System.out.println("Socket Closed.");
            }
        } catch (IOException e) {
            // ignore exception on close
        }
    }



    public static void main(String args[]) {
        ClientTCP client = new ClientTCP();

        //scanner to read input
        Scanner s = new Scanner(System.in);
        Long startTime;
        Long endTime;
        String result = "";
        while(true){
            System.out.println("0. View basic blockchain status.\n" +
                    "1. Add a transaction to the blockchain.\n" +
                    "2. Verify the blockchain.\n" +
                    "3. View the blockchain.\n" +
                    "4. Corrupt the chain.\n" +
                    "5. Hide the Corruption by recomputing hashes.\n" +
                    "6. Exit");
            int opt = s.nextInt();
            switch (opt){
                case 0:
                    result = client.process(0, "", "");
                    s.nextLine();//clear inputs
                    break;
                case 1:
                    System.out.println("Enter difficulty > 0");
                    int difficulty = s.nextInt();
                    System.out.println("Enter transaction");
                    s.nextLine();
                    String data = s.nextLine();
                    result = client.process(1, String.valueOf(difficulty), data);
                    break;
                case 2:
                    System.out.println("Verifying entire chain");
                    result = client.process(2, "", "");
                    s.nextLine();//clear inputs
                    break;
                case 3:
                    result = client.process(3, "", "");
                    s.nextLine();//clear inputs
                    break;
                case 4:
                    System.out.println("Corrupt the Blockchain");
                    System.out.print("Enter block ID of block to Corrupt: ");
                    int blockId = s.nextInt();
                    System.out.print("Enter new data for block " + blockId + ":\n");
                    s.nextLine();
                    String corruptData = s.nextLine();
                    result = client.process(4, String.valueOf(blockId), corruptData);
                    break;
                case 5:
                    System.out.println("Repairing the entire chain");
                    result = client.process(5, "", "");
                    s.nextLine();//clear inputs
                    break;
                case 6:
                    client.close();
                    System.exit(0);
            }
            System.out.println(result);
        }
    }

    //the function
    private String process(int operationId, String arg1, String arg2){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //bufferedReader to read response
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))); //writer to send request
            //sign the msg
            String signature = getSignature(operationId, arg1, arg2);

            //{"ClientId":"XXXXX","PublicKey":"XXX+XXXX","OperationId":"X","Arg1":"XXXX","Arg2":"XXXX","Signature":"bigInteger signature"}
            String jsonString = getJsonString(operationId, arg1, arg2, signature);
            //System.out.println(jsonString);
            //send jsonString to server
            out.println(jsonString);
            out.flush();
            String response = "";
            String row = in.readLine();
            while (!row.equals("")) {
                //System.out.println(row.charAt(0));
                response += row;
                response += "\n";
                row = in.readLine();// read a line of data from the stream
            }
            //System.out.println(response);
            //System.out.println(JsonHelper.getMsg(response));
            return JsonHelper.getMsg(response);
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());//print SocketException
        }catch (IOException e){System.out.println("IO: " + e.getMessage());//print IOException
        }catch (Exception e) {
            System.out.println("Other exception from Client: " + e.getMessage());//print Exception
        }
        return null;
    }


    // generate publicKey and privateKey for client
    private void generateKeys(){
        Random rnd = new Random();

        // Step 1: Generate two large random primes.
        BigInteger p = new BigInteger(2048,100,rnd);
        BigInteger q = new BigInteger(2048,100,rnd);

        // Step 2: Compute n by the equation n = p * q.
        n = p.multiply(q);

        // Step 3: Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Step 4: Select a small odd integer e that is relatively prime to phi(n).
        // By convention the prime 65537 is used as the public exponent.
        e = new BigInteger ("65537");

        // Step 5: Compute d as the multiplicative inverse of e modulo phi(n).
        d = e.modInverse(phi);


        publicKeyString = e + "+" + n;
        privateKeyString = d + "+" + n;

    }

    /**
     * Signing proceeds as follows:
     * 1) Get the bytes from the string to be signed.
     * 2) Compute a SHA-1 digest of these bytes.
     * 3) Copy these bytes into a byte array that is one byte longer than needed.
     *    The resulting byte array has its extra byte set to zero. This is because
     *    RSA works only on positive numbers. The most significant byte (in the
     *    new byte array) is the 0'th byte. It must be set to zero.
     * 4) Create a BigInteger from the byte array.
     * 5) Encrypt the BigInteger with RSA d and n.
     * 6) Return to the caller a String representation of this BigInteger.
     * @param operation the operation id
     * @param arg1 first additional argument
     * @param arg2 2nd additional argument
     * @return a String representation of this BigInteger
     * @throws Exception
     */
    private String getSignature(int operation, String arg1, String arg2) throws Exception {
        // concat the string to sign
        String concatString = "";
        concatString += clientId;
        concatString += publicKeyString;
        concatString += String.valueOf(operation);
        concatString += arg1;
        concatString += arg2;
        // compute the digest with SHA-256
        byte[] bytesOfMessage = concatString.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);

        // we add a 0 byte as the most significant byte to keep
        // the value to be signed non-negative.
        byte[] messageDigest = new byte[bigDigest.length+1];
        messageDigest[0] = 0;   // most significant set to 0
        for (int i = 1; i < messageDigest.length; i++){
            messageDigest[i] = bigDigest[i-1];
        }

        // From the digest, create a BigInteger
        BigInteger m = new BigInteger(messageDigest);

        // encrypt the digest with the private key
        BigInteger c = m.modPow(d, n);
        return c.toString();
    }

    //create the json string in the format of {"ClientId":"XXXXX","PublicKey":"XXX+XXXX","OperationId":"X","Arg1":"XXXX","Arg2":"XXXX","Signature":"bigInteger signature"}
    private String getJsonString(int operationId, String arg1, String arg2, String c){
        String result = "";
        result += "{\"ClientId\":\"";
        result += clientId;
        result += "\",\"PublicKey\":\"";
        result += publicKeyString;
        result += "\",\"OperationId\":\"";
        result += String.valueOf(operationId);
        result += "\",\"Arg1\":\"";
        result += arg1;
        result += "\",\"Arg2\":\"";
        result += arg2;
        result += "\",\"Signature\":\"";
        result += c;
        result += "\"}";
        return result;
    }

}