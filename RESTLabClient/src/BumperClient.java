import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class BumperClient {
    public String doGet() {
        String response = "";
        try {
            URL url = new URL("http://localhost:8080/bumper");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/plain");
            if (conn.getResponseCode() != 200) {
                System.out.println(conn.getResponseMessage());
                return "-1";
            }
            String output = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                response += output;
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    public String doPost(int value) {

        //System.out.println("Posting");

        String response = "";
        try {
            // Make call to a particular URL
            URL url = new URL("http://localhost:8080/bumper");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // set request method to POST and send name value pair
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","text/plain");
            conn.setDoOutput(true);
            // write to POST data area
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            String payload = "val=" + value;
            out.write(payload);
            out.close();
            String output = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                response += output;
            }
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
        return response;
    }
    public static void main(String[] args) {
        System.out.println("Running BumperClient.java");
        long start = System.currentTimeMillis();
        BumperClient client = new BumperClient();
        for (int i = 0; i < 10000; i++) {
            client.doPost(1);
        }
        long stop = System.currentTimeMillis();
        System.out.println(client.doGet());
        System.out.println("Time to complete:" + (stop - start));
    }
}
