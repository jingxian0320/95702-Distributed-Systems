<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: jingxianbao
  Date: 2/2/20
  Time: 9:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">

    <title>Bird Search</title>
</head>
<body>
<div class="container" style="padding-top: 50px; padding-bottom: 50px">
    <h4>Choose a bird from the dropdown</h4>
    <form action="getABirdPicture" method="get" style="margin-bottom: 50px">
        <select class="custom-select" id="birdNameSelect" name="birdName" style="max-width: 300px">
            <!-- populate the dropdown options according to birdNameList -->
            <% ArrayList<String> birdNames = (ArrayList<String>) request.getAttribute("birdNameList"); %>
            <% for (int i = 0; i < birdNames.size(); i++) { %>
            <% String birdName = birdNames.get(i); %>
            <% if (i == 0){ %>
                <option value="<%= birdName %>" selected><%= birdName %></option>
            <% } else {%>
                <option value="<%= birdName %>"><%= birdName %></option>
            <% }%>
            <% }%>
        </select>
        <p>
        <button type="submit" class="btn btn-primary" style="margin-top: 10px">Submit</button>
        </p>
    </form>

</div>
<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

</body>
</html>
