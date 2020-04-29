import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

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

public class EchoClientTCP {
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
    EchoClientTCP(){
        System.out.println("Client Running"); //indicate start of the client
        try{
            clientSocket = new Socket("localhost", serverPort);
            generateKeys();
            System.out.println("Public Key: ("+publicKeyString+")");
            System.out.println("Private Key: ("+privateKeyString+")");
            String publicKeyHash = BabyHash.ComputeSHA_256_as_Hex_String(publicKeyString.toLowerCase());
            clientId = publicKeyHash.substring(publicKeyHash.length()-40,publicKeyHash.length()).toUpperCase();
            //System.out.println(clientId);

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
        EchoClientTCP client = new EchoClientTCP();
        Scanner s = new Scanner(System.in);
        char running = 'y';
        while(running == 'y'){
            System.out.println("1. add");
            System.out.println("2. subtract");
            System.out.println("3. view");
            System.out.print("Please choose an operation to perform: ");
            int ops = s.nextInt();
            String result = null;
            int val;
            switch (ops){
                case(1):
                    System.out.print("Please provide a number:");
                    val = s.nextInt();
                    result = client.process(client.clientId + "," + client.publicKeyString + ",1,"+val);
                    break;
                case(2):
                    System.out.print("Please provide a number:");
                    val = s.nextInt();
                    result = client.process(client.clientId + "," + client.publicKeyString + ",2,"+val);
                    break;
                case(3):
                    result = client.process(client.clientId + "," +client.publicKeyString + ",3");
                    break;
            }
            System.out.println(result);
            s.nextLine();//clear inputs
            System.out.print("Enter y to continue: ");
            running = s.nextLine().charAt(0);
        }
        client.close();
    }
    private String process(String requestString){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //bufferedReader to read response
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))); //writer to send request
            //sign the msg
            String signedMsg = sign(requestString);
            //send requestString to server
            out.println(signedMsg);
            out.flush();
            String data = in.readLine(); // read a line of data from the stream
            return data;
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());//print SocketException
        }catch (IOException e){System.out.println("IO: " + e.getMessage());//print IOException
        }catch (Exception e) {
            System.out.println("Other exception: " + e.getMessage());//print IOException
        }return null;
    }


    private void generateKeys(){
        Random rnd = new Random();

        // Step 1: Generate two large random primes.
        // We use 400 bits here, but best practice for security is 2048 bits.
        // Change 400 to 2048, recompile, and run the program again and you will
        // notice it takes much longer to do the math with that many bits.
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
     * @param message a sting to be signed
     * @return a string representing a big integer - the encrypted hash.
     * @throws Exception
     */
    private String sign(String message) throws Exception {

        // compute the digest with SHA-256
        byte[] bytesOfMessage = message.getBytes("UTF-8");
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

        // return this as a big integer string
        return message + "," + c.toString();
    }
}