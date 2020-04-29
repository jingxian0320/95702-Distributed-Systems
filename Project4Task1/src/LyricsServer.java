import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@WebServlet(name = "LyricsServlet",
        urlPatterns = {"/*"})
public class LyricsServer extends HttpServlet {
    final static String APIKEY = "de797bb5ce6c68f4206acb3e47212784";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Console: doGET visited");

        JSONObject json = new JSONObject();


        // The name is on the path /name so skip over the '/'
        String search = (request.getPathInfo()).substring(1);
        System.out.println(search);
        String singer = search.split("/")[0];
        String track = search.split("/")[1];

        String requestString = "http://api.musixmatch.com/ws/1.1/matcher.lyrics.get?apikey=" + APIKEY;;
        try{
            singer = URLEncoder.encode(singer.toLowerCase(), "UTF-8");
            track = URLEncoder.encode(track.toLowerCase(), "UTF-8");
        }
        catch (UnsupportedEncodingException e){
            System.out.println("UnsupportedEncodingException");
        }

        requestString += "&q_track=";
        requestString += track;
        requestString += "&q_artist=";
        requestString += singer;
        requestString += "&page_size=10&page=1&s_track_rating=desc";
        System.out.println(requestString);
        String lyrics = this.fetch(requestString);
        try {
            json.put("lyrics", lyrics);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = json.toString();

        // Things went well so set the HTTP response code to 200 OK
        response.setStatus(200);
        // tell the client the type of the response
        response.setContentType("text/plain;charset=UTF-8");

        // return the value from a GET request
        PrintWriter out = response.getWriter();
        out.println(result);
    }

    private String fetch(String urlString) {
        String response = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            in.close();
            JSONObject obj = new JSONObject(response);
            String result = (String) obj.getJSONObject("message").getJSONObject("body").getJSONObject("lyrics").get("lyrics_body");
            System.out.println(result);
            return result;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
