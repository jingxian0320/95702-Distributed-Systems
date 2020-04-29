/**
 * Author: Jingxian Bao
 * Last Modified: Feb 6, 2020
 *
 * This web application implements a simple desktop
 * and mobile “clicker” for class. The app allows users
 * to submit answers to questions posed in class, and
 * provides a separate URL end point for
 * getting the results of the submitted responses.
 */

package cmu.edu.jingxiab;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

@WebServlet(name = "selectServlet",
        urlPatterns = {"/Project1Task3","/submit","/getResults"})

public class SelectServlet extends HttpServlet {
    ClickerModel cm = null;  // The data model for this app

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        cm = new ClickerModel ();
    }

    // actions performed when a POST request is received
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String option = request.getParameter("option"); //get the value of option
        cm.add(option); //add the option to the model
        request.setAttribute("selected", option); //return the selected option to display on the page

        // Transfer control over the the correct "view"
        RequestDispatcher view = request.getRequestDispatcher("select.jsp");
        view.forward(request, response);
    }


    // actions performed when a GET request is received
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher view;

        // if it is /getResults page
        if (request.getServletPath().contains("/getResults")){

            //Getting Set of keys from model data HashMap
            Set<String> keySet = cm.result.keySet();
            //Creating an ArrayList of keys by passing the keySet
            ArrayList<String> listOfKeys = new ArrayList<String>(keySet);
            //sort the options
            Collections.sort(listOfKeys);
            //return the sorted options that have been selected to display on the webpage
            request.setAttribute("selectedOptions", listOfKeys);
            request.setAttribute("result", cm.result);
            // Transfer control over to result page
            view = request.getRequestDispatcher("result.jsp");
            view.forward(request, response);
            cm.clear(); //clear the model
        }
        else{
            // Transfer control over to select page
            view = request.getRequestDispatcher("select.jsp");
            view.forward(request, response);
        }
    }
}
