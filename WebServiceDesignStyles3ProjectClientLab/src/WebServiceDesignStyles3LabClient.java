// ****************  Client side code  *********************
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// A simple class to wrap a result.
class Result {
    String value;

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
public class WebServiceDesignStyles3LabClient {


    public static void main(String[] args) throws Exception{

        System.out.println("Begin main of REST lab.");
        System.out.println("Assign 100 to the variable named x");
        assign("x","100");
        System.out.println("Assign 199 to the variable named x");
        assign("x","199");
        System.out.println("Sending a GET request for x");
        // Get the value associated with a name on the server
        System.out.println(read("x"));

        System.out.println("Sending a DELETE request for x");
        clear("x");
        System.out.println("x is deleted but let's try to read it");
        System.out.println(read("x"));

        assign("a","It is interesting to use HTTP\n ");
        assign("b","in an RPC\n");
        assign("c","kind of way. Welcome to REST!\n");

        System.out.println(read("a"));
        System.out.println(read("b"));
        System.out.println(read("c"));

        System.out.println(getVariableList());

        System.out.println("End main of REST lab");
    }

    // makes a call to doGetList()
    // returns a list of all variable defined on the server.
    public static String getVariableList(){
        Result r = new Result();
        int status = 0;
        if((status = doGetList(r)) != 200) return "Error from server "+ status;
        return r.getValue();
    }

    // Makes an HTTP GET request to the server. This is similar to the doGet provided on the client
    // but this one uses a different URL.
    // This method makes a call to the HTTP GET method using
    // http://localhost:8080/WebServiceDesignStyles3ProjectServerLab/VariableMemory/"

    public static int doGetList(Result r){
        r.setValue("");
        String response = "";
        HttpURLConnection conn;
        int status = 0;

        try {
            // pass the name on the URL line
            URL url = new URL("http://localhost:8080/WebServiceDesignStyles3ProjectServerLab/VariableMemory" + "//");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");

            // wait for response
            status = conn.getResponseCode();

            // If things went poorly, don't try to read any response, just return.
            if (status != 200) {
                // not using msg
                String msg = conn.getResponseMessage();
                return conn.getResponseCode();
            }
            String output = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            while ((output = br.readLine()) != null) {
                response += output;

            }

            conn.disconnect();

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }   catch (IOException e) {
            e.printStackTrace();
        }

        // return value from server
        // set the response object
        r.setValue(response);
        // return HTTP status to caller
        return status;
    }


    // assign a string value to a string name (One character names for demo)
    public static boolean assign(String name, String value) {
        // We always want to be able to assign so we may need to PUT or POST.
        // Try to PUT, if that fails then try to POST
        if(doPut(name,value) == 200) {
            return true;
        }
        else {
            if(doPost(name,value) == 200) {
                return true;
            }
        }
        return false;
    }

    // read a value associated with a name from the server
    // return either the value read or an error message
    public static String read(String name) {
        Result r = new Result();
        int status = 0;
        if((status = doGet(name,r)) != 200) return "Error from server "+ status;
        return r.getValue();
    }
    // delete a variable on the server
    // if the server sends an error return false to the caller
    public static boolean clear(String name) {
        if(doDelete(name) == 200) return true;
        else return false;
    }

    // Low level routine to make an HTTP POST request
    // Note, POST does not use the URL line for its message to the server
    public static int doPost(String name, String value) {

        int status = 0;
        String output;

        try {
            // Make call to a particular URL
            URL url = new URL("http://localhost:8080/WebServiceDesignStyles3ProjectServerLab/VariableMemory/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to POST and send name value pair
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            // write to POST data area
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(name + "=" + value);
            out.close();

            // get HTTP response code sent by server
            status = conn.getResponseCode();

            //close the connection
            conn.disconnect();
        }
        // handle exceptions
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // return HTTP status

        return status;
    }

    public static int doGet(String name, Result r) {

        // Make an HTTP GET passing the name on the URL line

        r.setValue("");
        String response = "";
        HttpURLConnection conn;
        int status = 0;

        try {

            // pass the name on the URL line
            URL url = new URL("http://localhost:8080/WebServiceDesignStyles3ProjectServerLab/VariableMemory" + "//"+name);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");

            // wait for response
            status = conn.getResponseCode();

            // If things went poorly, don't try to read any response, just return.
            if (status != 200) {
                // not using msg
                String msg = conn.getResponseMessage();
                return conn.getResponseCode();
            }
            String output = "";
            // things went well so let's read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            while ((output = br.readLine()) != null) {
                response += output;

            }

            conn.disconnect();

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }   catch (IOException e) {
            e.printStackTrace();
        }

        // return value from server
        // set the response object
        r.setValue(response);
        // return HTTP status to caller
        return status;
    }

    // Low level routine to make an HTTP PUT request
    // Note, PUT does not use the URL line for its message to the server
    public static int doPut(String name, String value) {


        int status = 0;
        try {
            URL url = new URL("http://localhost:8080/WebServiceDesignStyles3ProjectServerLab/VariableMemory/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(name + "=" + value);
            out.close();
            status = conn.getResponseCode();

            conn.disconnect();

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    // Send an HTTP DELETE to server along with name on the URL line
    // We need not read the response, we are only interested in the HTTP status
    // code.
    public static int doDelete(String name) {

        int status = 0;

        try {
            URL url = new URL("http://localhost:8080/WebServiceDesignStyles3ProjectServerLab/VariableMemory" + "//"+name);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            status = conn.getResponseCode();
            conn.disconnect();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

}
