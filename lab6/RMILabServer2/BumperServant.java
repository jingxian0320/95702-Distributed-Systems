//*************************************************************
    // The class extends UnicastRemoteObject and implements Bumper

    // The server calls rebind on the rmiregistry giving the remote
    // object the name "bumper".
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.math.BigInteger;
public class BumperServant extends UnicastRemoteObject implements Bumper {
	     BigInteger val;

       BumperServant() throws RemoteException {}
       public boolean bump(BigInteger n) throws RemoteException {
        // A call on bump() adds 1 to a BigInteger held by the service.
      	// It then returns true on completion.
      	// The BigInteger is changed by the call on bump(). That is,
     	// 1 is added to the BigInteger and that value persists until
      	// another call on bump occurs.
      	val = n.add(new BigInteger("1"));
        return true;

   	 }
   	 public BigInteger get() throws RemoteException {
      // a call on get returns the BigInteger held by the service
   	 	return val;
    }
}
