import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class LyricsAPITest {
    final static String APIKEY = "de797bb5ce6c68f4206acb3e47212784";
    public static void main(String [] args) {
        LyricsAPITest apiT = new LyricsAPITest();
        String requestString = "http://api.musixmatch.com/ws/1.1/matcher.lyrics.get?apikey=" + APIKEY;
        String singer = "LMFAO";
        String track = "Sexy and I know it";
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
        String response = apiT.fetch(requestString);
        //System.out.println(response);

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
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }
}
