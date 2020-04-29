/**
 * Author: Jingxian Bao
 * Last Modified: Feb 6, 2020
 *
 * This application read user input to transform
 * a string into a MD5 or SHA-256 hash according
 * to user selection.
 */

package cmu.edu.jingxiab;

import javax.servlet.RequestDispatcher;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ComputeHashes extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    // actions performed when a GET request is received
    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        // get the input text if it exists
        String inputText = request.getParameter("inputText");
        // get the hash type
        String hashType = request.getParameter("hashType");
        MessageDigest md = null;
        // if user selects md5
        if (hashType.equals("md5")){
            try{
                md = MessageDigest.getInstance("MD5");

            }
            catch (NoSuchAlgorithmException e){
                System.out.println("No such algorithm found.");
                System.exit(0);
            }
        }
        // if user selects SHA-256
        else{
            try{
                md = MessageDigest.getInstance("SHA-256");

            }
            catch (NoSuchAlgorithmException e){
                System.out.println("No such algorithm found.");
                System.exit(0);
            }
        }

        //get the hash according to the hash type selected
        md.update(inputText.getBytes());
        byte[] digest = md.digest();
        String base64 = DatatypeConverter.printBase64Binary(digest);
        String hexadecimal = DatatypeConverter.printHexBinary(digest);

        //produce the result string

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<h4 style=\"overflow-wrap: break-word\">%s Hashes of %s:</h4>\n",hashType, inputText));
        sb.append(String.format("<p style=\"overflow-wrap: break-word\"> Base64 encoding: %s </p>\n", base64));
        sb.append(String.format("<p style=\"overflow-wrap: break-word\"> Hexadecimal encoding: %s </p>\n", hexadecimal));
        String output = sb.toString();
        request.setAttribute("output",output);

        // Transfer control over the the correct "view"
        RequestDispatcher view = request.getRequestDispatcher("index.jsp");
        view.forward(request, response);
    }
}
