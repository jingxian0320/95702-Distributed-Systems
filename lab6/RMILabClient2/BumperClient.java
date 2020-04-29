//*******************************************************
// CalculatorClient.java
// This client gets a remote reference from the rmiregistry
// that is listening on the default port of 1099.
// It allows the client to quit with a "!".
// Otherwise, it computes the sum of two integers
// using the remote calculator.

import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.util.StringTokenizer;
import java.math.BigInteger;

public class BumperClient {
   public static void main(String args[]) throws Exception {
        
        // connect to the rmiregistry and get a remote reference to the Calculator
        // object.
        Bumper b = (Bumper) Naming.lookup("//localhost/CoolBumper");
        BigInteger ctr = new BigInteger("0");
        BigInteger n = new BigInteger("10000");
        long start = System.currentTimeMillis();

        while (!ctr.equals(n)){
            try{
              b.bump(ctr);
              ctr = b.get();
            }
            catch(RemoteException e) {
                   System.out.println("allComments: " + e.getMessage());
              }
        }
        System.out.println("Value of the BigInteger held on the server: " + b.get());
        long stop = System.currentTimeMillis();
        System.out.println("Time to complete: " + (stop - start));


	      
	   }
}
