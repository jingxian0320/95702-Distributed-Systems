/**
 * Author: Jingxian Bao
 * Last Modified: Feb 6, 2020
 *
 * The web application searches for images of migratory birds from the
 * Smithsonian's National Zoo & Conservation Biology Institute's bird photo gallery.
 */

package cmu.edu.jingxiab;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.ArrayList;


@WebServlet(name = "SearchBirdServlet",
        urlPatterns = {"/getABirdPicture"})

public class BirdSearchServlet extends javax.servlet.http.HttpServlet {
    BirdSearchModel bsm = null;  // The data model for this app

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        bsm = new BirdSearchModel();
    }

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        // get the search parameter if it exists
        String searchWord = request.getParameter("searchWord");
        String birdName = request.getParameter("birdName");

        // Go to prompt view by default
        String nextView = "prompt.jsp";

        /*
         * Check if the searchwWord parameter is present.
         * If not, then give the user instructions and prompt for a search string.
         * If there is a search parameter, match the search word with the list of bird names
         * and return 10 bird names for the user to select
         */
        if (searchWord != null) {
            // use model to search for the matched bird names
            ArrayList<String> birdNameList = bsm.doBirdNameSearch(searchWord);

            //if there is no match to the search word, go to the error page
            if (birdNameList == null){
                request.setAttribute("errorMessage", String.format("Bird name containing %s cannot be found",searchWord));
                nextView = "error.jsp";
            }
            //else, go to the select bird name page
            else{
                request.setAttribute("birdNameList", birdNameList);
                nextView = "select.jsp";
            }
        }

        /*
         * Check if the birdName parameter is present.
         * If not, then give the user instructions and prompt for a search string.
         * If there is a search parameter, match the search word with the list of bird names
         * and return 10 bird names for the user to select
         */
        if (birdName != null) {
            // use model to do the search and choose the result view
            String[] result = bsm.doBirdImageSearch(birdName);
            if (result == null){
                request.setAttribute("errorMessage", String.format("Image of %s cannot be found",birdName));
                nextView = "error.jsp";
            }
            else{
                request.setAttribute("birdName", birdName);
                request.setAttribute("imageURL", result[0]);
                request.setAttribute("photographer", result[1]);
                nextView = "result.jsp";
            }
        }

        // Transfer control over the the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);

    }
}
