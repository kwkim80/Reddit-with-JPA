<%-- 
    Document   : Input-RedditAccount
    Created on : Dec. 6, 2020, 8:26:39 p.m.
    Author     : Jiyeon Choi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create Reddit Account</title>
    </head>
    <body>
<div style=\"text-align: center;\">
<div style=\"display: inline-block; text-align: left;\">
<form method="post">
Name:<br>
<input type="text" name="name" value=""><br> 
<br>Link Points:<br>
<input type="text" name="link-points" value=""><br>
<br>
Comment Points:<br>
<input type="text" name="comment-points" value=""><br>
<br>
<input type="submit" name="view" value="Add and View">
<input type="submit" name="add" value="Add">
</form>

</div>
    </body>
</html>
