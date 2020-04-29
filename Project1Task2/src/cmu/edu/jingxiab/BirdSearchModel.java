/**
 * Author: Jingxian Bao
 * Last Modified: Feb 6, 2020
 * This file is the Model component of the MVC, and it models the business
 * logic for the web application.  In this case, the business logic involves
 * screen scraping the list of bird names, making a request to
 * https://nationalzoo.si.edu and then screen scraping the HTML that is
 * returned in order to fabricate an image URL.
 */

package cmu.edu.jingxiab;

import java.io.*;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class BirdSearchModel {
    final String rootURL = "https://nationalzoo.si.edu/scbi/migratorybirds/featured_photo/";
    final String birdNameFileName = "birdName.txt";
    final int matchLimit = 10;
    private ArrayList<String> allBirdNames;

    // initiation: fetch a list of all bird names
    BirdSearchModel(){
        allBirdNames = fetchBirdNames();
        System.out.println(allBirdNames.size());
    }

    /**
     * If birdName.txt file does not exist,
     * fetch all the bird names from the national zoo website
     * else, read from the txt file
     */
    public ArrayList<String> fetchBirdNames() {
        File birdNameFile = new File(this.getClass().getClassLoader().getResource("").getPath()+birdNameFileName);
        allBirdNames = new ArrayList<String>();
        try{
            // read from birdName.txt
            Scanner s = new Scanner(birdNameFile);
            while (s.hasNextLine()){
                allBirdNames.add(s.nextLine());
            }
            s.close();
            System.out.println("File Found.");
            return allBirdNames;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            // scrape from the web page
            try {
                Document doc = Jsoup.connect(rootURL).get();
                //find the HTML elements <pix> which we want to extract data
                Elements options = doc.getElementById("pix").children();
                for (Element option : options) {
                    String birdName = option.text();
                    allBirdNames.add(birdName);
                }

                // write to the file
                FileWriter writer = new FileWriter(birdNameFile);
                for (String birdName : allBirdNames) {
                    writer.write(birdName + System.lineSeparator());
                }
                writer.close();
                System.out.println("File written.");
                return allBirdNames;

            } catch (IOException ex) {
                ex.printStackTrace();
                return allBirdNames;
            }
        }
    }

    /**
     * Arguments.
     *
     * @param searchTag The tag of the birdname to be searched for.
     */
    public ArrayList<String> doBirdNameSearch(String searchTag) throws IndexOutOfBoundsException{
        ArrayList<String> matchedBirdNames = new ArrayList<String>();

        // search for the bird names containing the search tag
        for (String birdName : allBirdNames) {
            if (birdName.toLowerCase().contains(searchTag.toLowerCase())){
                matchedBirdNames.add(birdName);
            }
        }

        // return null if no result found
        if (matchedBirdNames.size() == 0){
            return null;
        }

        // shuffle the results and return the first 10 elements
        Collections.shuffle(matchedBirdNames);
        if (matchedBirdNames.size() <= matchLimit) {
            return matchedBirdNames;
        }
        return new ArrayList<String>(matchedBirdNames.subList(0, matchLimit));
    }

    /**
     * Arguments.
     *
     * @param birdName The name of the bird to be searched for.
     */

    public String[] doBirdImageSearch(String birdName) throws UnsupportedEncodingException{
        /*
         * URL encode the birdName, e.g. to encode spaces as %20
         *
         * There is no reason that UTF-8 would be unsupported.  It is the
         * standard encoding today.  So if it is not supported, we have
         * big problems, so don't catch the exception.
         */

        birdName = URLEncoder.encode(birdName, "UTF-8");
        String imageURL = "";
        String photographer = "";

        // Create a URL for the page to be screen scraped
        String pageURL = rootURL + "bird.cfm?pix="+birdName;
        System.out.println(pageURL);

        /*
         * Search the page to find the picture URL
         */
        try {
            Document doc = Jsoup.connect(pageURL).get();
            //First find the HTML elements <picgrid> which we want to extract data from
            Element content = doc.getElementById("picgrid");
            Elements divElements = content.children();
            //If size=0, then no picture is found
            if (divElements.size() == 0) {
                return null;
            }

            //Instead of choosing the first image, let's randomly choose an image
            // Because jsoup will return an array of matched elements, it is
            // easier than the substring method to randomly choose one element
            Random r = new Random();
            int i = r.nextInt(divElements.size());

            // return the imageURL and photographer to servlet
            imageURL = divElements.get(i).getElementsByTag("img").attr("src");
            photographer = divElements.get(i).getElementsByTag("a").text().substring(2);

            String[] result = {imageURL,photographer};
            return result;
        } catch (IOException ex) {
            System.out.println("pictureURL= null");
            return null;
        }
    }
}