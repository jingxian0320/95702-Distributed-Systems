<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: jingxianbao
  Date: 2/3/20
  Time: 4:01 AM
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

    <title>DS Clicker</title>
</head>
<body>
<div class="container" style="padding-bottom: 50px; padding-top: 50px">
    <h4>Distributed Systems Class Clicker</h4>
    <% HashMap<String,Integer> result = (HashMap<String,Integer>)(request.getAttribute("result"));%>
    <!-- if no recorded selection -->
    <%if (result.isEmpty()){%>
    <p>There are currently no results</p>
    <!-- if data recorded in the model -->
    <%}else{%>
    <p>Results from the survey are as follows</p>
    <!-- loop over the result -->
    <% ArrayList<String> selectedOptions = (ArrayList<String>)(request.getAttribute("selectedOptions"));%>
    <%for (String key:selectedOptions){%>
    <p> <%=key%> : <%=result.get(key)%></p>
    <%}%>
    <p>These results have now been cleared.</p>
    <%}%>
    <a class="btn btn-primary" href="./" role="button">Return</a>
</div>
<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

</body>
</html>
