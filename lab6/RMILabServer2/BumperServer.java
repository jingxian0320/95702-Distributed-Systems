//**************************************************************
// CalculatorServer.java             Serve remote Calculator
// Creates a calculator object and gives
// it a name in the registry.
import java.rmi.*;
public class BumperServer {
	public static void main(String args[]){
          System.out.println("Bumper Server Running");
          try{
            // create the servant
            Bumper b = new BumperServant();
            System.out.println("Created Bumper object");
            System.out.println("Placing in registry");
            // publish to registry
	         Naming.rebind("CoolBumper", b);
            System.out.println("BumperServant object ready");
           }catch(Exception e) {
            System.out.println("BumperServer error main " + e.getMessage());
        }
    }
}
