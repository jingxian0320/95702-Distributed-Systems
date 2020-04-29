
/**
 * Author: Jingxian Bao
 * Last Modified: Apr 16, 2020
 *
 * This program utilize JavaRDD to analyze text file
 */

package com.company;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Scanner;

public class TempestAnalytics {
    final static String fileName = "TheTempest.txt"; //the file to analyze
    public static void main(String[] args){
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("TempestAnalytics"); //set Spark Name
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf); // create new spark context
        JavaRDD<String> inputFile = sparkContext.textFile(fileName); //read input file


        //Task 0. (2 points)
        //=======
        //Using the count method of the JavaRDD class, display the number of lines in "The Tempest".
        //For the display, use System.out.println().

        System.out.println(inputFile.count());

        //Task 1. (2 points)
        //Using the split method of the java String class and the flatMap method of the JavaRDD class,
        //use the count method of the JavaRDD class to display the number of words in The Tempest.

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split(" ")));//tokenize the text
        System.out.println(wordsFromFile.count());

        //Task 2. (2 points)
        //use JavaRDD distinct() and count() methods, to count
        //the number of distinct words in The Tempest.
        System.out.println(wordsFromFile.distinct().count());

        //Task 3. (2 points)
        //Using the JavaPairRDD class and the saveAsTextFile() method along with the JavaRDD class
        //and the mapToPair() method, show each word paired with the digit 1 in the output directory
        //named Project5/Part_2/TheTempestOutputDir1.

        JavaPairRDD WordData = wordsFromFile.mapToPair(t -> new Tuple2(t, 1));
        WordData.saveAsTextFile("Project5/Part_2/TheTempestOutputDir1");

        //Task 4. (2 points)
        //Using work from above and the JavaPairRDD from Task 3, create a new JavaPairRDD with the
        //reduceByKey() method. Save the RDD using the saveAsTextFile() method and place the result in
        //the output directory named Project5/Part_2/TheTempestOutputDir2.
        JavaPairRDD countData = wordsFromFile.mapToPair(t -> new Tuple2(t, 1)).reduceByKey((x, y) -> (int) x + (int) y);
        countData.saveAsTextFile("Project5/Part_2/TheTempestOutputDir2");

        //Task 5. (2 points)
        //Using work from above and the JavaRDD foreach() method, prompt the user for a string and
        //then perform a search on every line of the The Tempest. If any line of The Tempest
        //contains the String entered by the user then display the entire line.
        Scanner s = new Scanner(System.in);//initialize scanner
        String searchTerm = s.nextLine();//get user input
        s.close();
        // loop over the text
        inputFile.foreach(line -> {
            if (line.contains((searchTerm))){// for the line contains the search term
                System.out.println(line);//print out the line
            }
        });
    }
}
