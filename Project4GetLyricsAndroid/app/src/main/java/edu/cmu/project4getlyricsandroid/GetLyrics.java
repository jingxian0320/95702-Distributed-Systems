package edu.cmu.project4getlyricsandroid;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONObject;
import android.os.AsyncTask;

public class GetLyrics {
    AndroidApp aa = null;

    /*
     * search is the public GetPicture method.  Its arguments are the search term, and the InterestingPicture object that called it.  This provides a callback
     * path such that the pictureReady method in that object is called when the picture is available from the search.
     */
    public void search(String singer, String track, AndroidApp aa) {
        this.aa = aa;
        new AsyncFlickrSearch().execute(singer, track);

    }

    /*
     * AsyncTask provides a simple way to use a thread separate from the UI thread in which to do network operations.
     * doInBackground is run in the helper thread.
     * onPostExecute is run in the UI thread, allowing for safe UI updates.
     */
    private class AsyncFlickrSearch extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return search(urls[0], urls[1]);
        }

        protected void onPostExecute(String lyrics) {
            aa.lyricsReady(lyrics);
        }

        /*
         * Search Flickr.com for the searchTerm argument, and return a Bitmap that can be put in an ImageView
         */
        private String search(String singer, String track) {
            String requestString = "https://evening-earth-15918.herokuapp.com/getLyrics";
            //String requestString = "http://localhost:8080/getLyrics/";
            try{
                singer = URLEncoder.encode(singer.toLowerCase(), "UTF-8").replace("+", "%20");
                track = URLEncoder.encode(track.toLowerCase(), "UTF-8").replace("+", "%20");
            }
            catch (UnsupportedEncodingException e){
                System.out.println("UnsupportedEncodingException");
            }
            requestString += "/";
            requestString += singer;
            requestString += "/";
            requestString += track;

            System.out.println(requestString);
            String response = this.fetch(requestString);
            return response;
        }

        private String fetch(String urlString) {
            String response = "";
            int status = 0;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

                // wait for response
                status = connection.getResponseCode();

                // If things went poorly, don't try to read any response, just return.
                if (status != 200) {
                    // not using msg
                    String msg = connection.getResponseMessage();
                    return null;
                }



                String str;
                // Read each line of "in" until done, adding each to "response"
                while ((str = in.readLine()) != null) {
                    // str is one line of text readLine() strips newline characters
                    response += str;
                }
                in.close();
                JSONObject obj = new JSONObject(response);
                String result = (String) obj.get("lyrics");
                System.out.println(result);
                connection.disconnect();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
