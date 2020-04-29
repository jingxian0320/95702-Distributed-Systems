<%@ page import="org.bson.Document" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: jingxianbao
  Date: 4/3/20
  Time: 1:51 PM
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

    <title>Lyrics Search DashBoard</title>
</head>
<body>
<div class="container" style="padding-top: 50px; padding-bottom: 50px">
    <!-- display the result -->
    <H1>Lyrics Search DashBoard</H1>
    <p>Total # of Searches: <%= request.getAttribute("totalCount") %></p>
    <p># of Searches in 24h: <%= request.getAttribute("todayCount") %></p>
    <p>avg time of response from 3rd API (ms): <%= request.getAttribute("avgTime") %></p>
    <h5>Top 5 searched singers: </h5>
    <% ArrayList<Document> topSingers = (ArrayList<Document>) request.getAttribute("topSingers"); %>
    <table class="table">
        <thead>
            <tr>
                <th>Singer Name</th>
                <th>Search Count</th>
            </tr>
        </thead>
        <tbody>
            <% for(Document d: topSingers) { %>
            <tr>
                <td><%= d.get("_id")%></td>
                <td><%= d.get("searchCount")%></td>
            </tr>
            <%} %>
        </tbody>
    </table>
    <h5>All Logs: </h5>
    <% ArrayList<Document> allLog = (ArrayList<Document>) request.getAttribute("allLog"); %>
    <table class="table">
        <thead>
            <tr>
                <th>Id</th>
                <th>Timestamp</th>
                <th>Singer</th>
                <th>TrackName</th>
                <th>MobileDevice</th>
                <th>3rd API response time (ms)</th>
                <th>Length of Lyrics</th>
            </tr>
        </thead>
        <tbody>
        <% for(Document d: allLog) { %>
            <tr>
                <td><%= d.get("_id")%></td>
                <td><%= d.get("timestamp")%></td>
                <td><%= d.get("singer")%></td>
                <td><%= d.get("track")%></td>
                <td><%= d.get("mobileDevice")%></td>
                <td><%= d.get("timeTaken")%></td>
                <td><%= d.get("responseLen")%></td>
            </tr>
            <%} %>
        </tbody>
    </table>
</div>
<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

</body>
</html>

