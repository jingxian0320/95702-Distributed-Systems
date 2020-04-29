<%--
  Created by IntelliJ IDEA.
  User: jingxianbao
  Date: 2/2/20
  Time: 4:14 PM
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

  <title>Compute Hashes</title>
</head>
<body>
<div class="container">
    <h4 style="margin-top: 50px">Please input the text for hash computation</h4>
        <!-- The form to record user input -->
        <form action="ComputeHashes" method="get" style="margin-bottom: 50px">
          <div class="form-group">
            <input type="text" name="inputText" class="form-control" id="inputText" placeholder="Enter text", style="max-width: 200px">
          </div>
          <div class="custom-control custom-radio">
            <input type="radio" id="md5" name="hashType" value="md5" class="custom-control-input" checked>
            <label class="custom-control-label" for="md5">MD5</label>
          </div>
          <div class="custom-control custom-radio">
            <input type="radio" id="sha256" name="hashType" value="sha256" class="custom-control-input">
            <label class="custom-control-label" for="sha256">SHA256</label>
          </div>
          <button type="submit" class="btn btn-primary" style="margin-top: 10px">Submit</button>
        </form>
      <!-- Display results if any -->
      <% if (request.getAttribute("output") != null){ %>
        <%= request.getAttribute("output")%>
      <% } %>
    </div>
<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js" integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

</body>
</html>
