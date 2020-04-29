//******************* Server Side Code ********************
// 95-702 HTTP Lab exercise
// Working server handling POST, PUT, GET, and DELETE

// Server side code

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

// This example demonstrates Java servlets and HTTP
// This web service operates on string keys mapped to string values.


public class VariableMemory extends HttpServlet {

    // This map holds key value pairs
    private static Map memory = new TreeMap();

    // GET returns a value given a key
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Console: doGET visited");

        String result = "";

        // The name is on the path /name so skip over the '/'
        String name = (request.getPathInfo()).substring(1);

        // if name not provided, return variable list
        String value;
        if(name.equals("")) {
            Set variableList = memory.keySet();
            value =  String.join(", ", variableList);
        }
        else{
            // Look up the name from variable memory
            value = (String)memory.get(name);
            // return 401 if name not in map
            if(value == null || value.equals("")) {
                // no variable name found in map
                response.setStatus(401);
                return;
            }
        }

        // Things went well so set the HTTP response code to 200 OK
        response.setStatus(200);
        // tell the client the type of the response
        response.setContentType("text/plain;charset=UTF-8");

        // return the value from a GET request
        result = value;
        PrintWriter out = response.getWriter();
        out.println(result);
    }

    // Delete an existing variable from memory. If no such variable then return a 401
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        System.out.println("Console: doDelete visited");

        String result = "";

        // The name is on the path /name so skip over the '/'
        String name = (request.getPathInfo()).substring(1);

        if(name.equals("")) {
            // no variable name return 401
            response.setStatus(401);
            return;
        }

        // Look up the name from variable memory
        String value = (String)memory.get(name);

        if(value == null || value.equals("")) {
            // no variable name found in map so return 401
            response.setStatus(401);
            return;
        }

        // delete the name
        memory.remove(name);

        // Set HTTP response code to 200 OK
        response.setStatus(200);

    }

    // POST is used to create a new variable
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Console: doPost visited");

        // To look at what the client accepts examine request.getHeader("Accept")
        // We are not using the accept header here.

        // Read what the client has placed in the POST data area
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String data = br.readLine();

        // extract variable name from request data (variable names are a single character)
        String variableName = "" + data.charAt(0);

        // extract value after the equals sign

        String valString = data.substring(2);

        if(variableName.equals("") || valString.equals("")) {
            // missing input return 401
            response.setStatus(401);
            return;
        }

        String result = "";

        // If the variable is already in memory, let's return an error
        if(memory.get(variableName) != null) {
            response.setStatus(409);
            return;
        }
        else {
            // Not in memory so store name and value in the map
            memory.put(variableName, valString);

            // prepare response code
            response.setStatus(200);
            return;

        }
    }
    /* In this example, we use Put to update an existing variable.  */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("Console: doPut visited");
        // Read what the client has placed in the PUT data area
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String data = br.readLine();

        // extract variable name from request data (variable names are a single character)
        String variableName = "" + data.charAt(0);

        // extract value after the equals sign

        String valString = data.substring(2);

        if(variableName.equals("") || valString.equals("")) {
            // missing input return 401
            response.setStatus(401);
            return;
        }

        String result = "";

        // If the variable is not already in memory, let's return an error
        if(memory.get(variableName) == null) {
            response.setStatus(409);
            return;
        }
        else {
            // The name is in memory so store the new name and value in the map
            memory.put(variableName, valString);
            // prepare response code
            response.setStatus(200);
            return;

        }
    }

}
