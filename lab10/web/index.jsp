<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>JMS Example</title>
</head>
<body>
<h1>Messages entered travel over HTTP to your MyQueueWriter servlet.</h1>
<h1>The servlet writes the message to a queue</h1>
<h1>The onMessage method of an MDB is called by the queue.</h1>
<form action="MyQueueWriter">
  <table>
    <tbody>
    <tr> <td>Enter a message</td> <td>
      <input type="text" name="simpleTextMessage" value="Enter text here" /></td>
    </tr>
    </tbody>
  </table>
  <input type="submit" value="Submit text to servlet" />
</form>
</body>
</html>
