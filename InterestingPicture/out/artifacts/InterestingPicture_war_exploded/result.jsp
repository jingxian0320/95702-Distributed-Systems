<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>

<html>
    <head>
        <title>Interesting Picture</title>
    </head>
    <body>
        <% if (request.getParameter("searchWord").equals("Andy")) { %>
            <h1>Here is an interesting picture of a <%= request.getParameter("searchWord")%></h1><br>
            <% for (int i = 0; i < 10; i++) { %>
                <img src="http://www.andrew.cmu.edu/course/95-702/Images/AndrewCarnegie.jpg"><br><br>
            <% } %>
        <% } else { %>
            <% if (request.getAttribute("pictureURL") != null) { %>
                <h1>Here is an interesting picture of a <%= request.getParameter("searchWord")%></h1><br>
                <img src="<%= request.getAttribute("pictureURL")%>"><br><br>
            <% } else { %>
                <h1>An interesting picture of a <%= request.getParameter("searchWord")%> could not be found</h1><br>
            <% } %>
        <% } %>
         <form action="getAnInterestingPicture" method="GET">
            <label for="letter">Type another word.</label>
            <input type="text" name="searchWord" value="" /><br>
            <input type="submit" value="Submit" />
        </form>
    </body>
</html>

