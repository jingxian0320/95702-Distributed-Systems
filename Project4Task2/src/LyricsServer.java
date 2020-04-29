/**
 * Author: Jingxian Bao
 * Last Modified: Apr 6, 2020
 * The web application searches for the lyrics according to singer & track name. (api: https://developer.musixmatch.com/)
 */

import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.apache.openjpa.persistence.query.Aggregate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Consumer;

@WebServlet(name = "LyricsServlet",
        urlPatterns = {"/getLyrics/*","/dashBoard"})


public class LyricsServer extends HttpServlet {
    // the api to access the 3rd party api
    final static String APIKEY = "de797bb5ce6c68f4206acb3e47212784";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Console: doGET visited");

        JSONObject json = new JSONObject();

        // if it is the dashBoard url
        if (request.getServletPath().contains("/dashBoard")){
            this.getDashBoard(request, response);
        }
        // else search for lyrics
        else{
            // exact singer and track name from url
            String search = (request.getPathInfo()).substring(1);
            System.out.println(search);
            String singer = search.split("/")[0];
            String track = search.split("/")[1];
            singer = URLEncoder.encode(singer.toLowerCase(), "UTF-8");
            track = URLEncoder.encode(track.toLowerCase(), "UTF-8");

            //produce the request string to the api
            String requestString = "http://api.musixmatch.com/ws/1.1/matcher.lyrics.get?apikey=" + APIKEY;;

            requestString += "&q_track=";
            requestString += track;
            requestString += "&q_artist=";
            requestString += singer;
            requestString += "&page_size=10&page=1&s_track_rating=desc";
            System.out.println(requestString);

            // fetch response from the api
            long start = System.currentTimeMillis();
            String lyrics = this.fetch(requestString);
            long end = System.currentTimeMillis();
            long timeTaken = end - start;

            //log the record to MongoDB
            this.saveLog(singer, track, request, timeTaken, lyrics);
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

    }

    // produce the necessary variables for dashboard.jsp
    private void getDashBoard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //set jsp
        String nextView = "dashboard.jsp";
        //connect to the Collection
        MongoClient mongoClient = MongoClients.create("mongodb+srv://admin:00000000@cluster0-oh0ho.mongodb.net/test?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("Project4");
        MongoCollection table = db.getCollection("AppLog");

        // total number of searches
        long totalCount = table.countDocuments();

        // number of searches within 24h
        Document searchQuery = new Document();
        searchQuery.put("timestamp", new Document("$gt", new Date(System.currentTimeMillis()-24*60*60*1000)));
        long todayCount = table.countDocuments(searchQuery);

        // set parameters
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("todayCount", todayCount);

        // top 5 most searched singers
        AggregateIterable<Document> topSingerDocuments = table.aggregate(
                Arrays.asList(
                        Aggregates.group("$singer",Accumulators.sum("searchCount", 1)),
                        Aggregates.sort(Sorts.descending("searchCount")),
                        Aggregates.limit(5)));

        List<Document> topSingers = new ArrayList<Document>();
        for(Document d: topSingerDocuments) {
            topSingers.add(d);
            System.out.println(d);
        }

        request.setAttribute("topSingers", topSingers);

        // avg response time of the api
        AggregateIterable<Document> avgResponseTimeDocument = table.aggregate(
                Arrays.asList(
                        Aggregates.group("_id",Accumulators.avg("avgTime", "$timeTaken"))));
        double avgTime = avgResponseTimeDocument.first().getDouble("avgTime");
        System.out.println(avgTime);
        request.setAttribute("avgTime", avgTime);

        // all application log
        List<Document> allLog = new ArrayList<Document>();

        FindIterable<Document> iterDoc = table.find();
        for(Document d: iterDoc) {
            allLog.add(d);
            System.out.println(d);
        }

        request.setAttribute("allLog", allLog);

        mongoClient.close();

        // Transfer control over the the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);

    }

    // save info to mongoDB
    private void saveLog(String singer, String track, HttpServletRequest request, long timeTaken, String lyrics){
        //connect to collection
        MongoClient mongoClient = MongoClients.create("mongodb+srv://admin:00000000@cluster0-oh0ho.mongodb.net/test?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("Project4");
        MongoCollection table = db.getCollection("AppLog");
        int responseLen;
        if (lyrics == null){
            responseLen = 0;
        }
        else{
            responseLen = lyrics.length();
        }

        //save to DB
        Document document = new Document()
                .append("timestamp",new Date(System.currentTimeMillis()))
                .append("singer",singer.replace('+',' '))
                .append("track",track.replace('+',' '))
                .append("mobileDevice",request.getHeader("User-Agent").split("/")[0])
                .append("timeTaken",timeTaken)
                .append("responseLen",responseLen);
        table.insertOne(document);
        mongoClient.close();
    }

    // get response from API
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

            //get the lyrics from the response
            JSONObject obj = new JSONObject(response);
            String result = (String) obj.getJSONObject("message").getJSONObject("body").getJSONObject("lyrics").get("lyrics_body");
            System.out.println(result);
            return result;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    Block<Document> printBlock = new Block<Document>() {
        @Override
        public void apply(final Document document) {
            System.out.println(document.toJson());
        }
    };
}
