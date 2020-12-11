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
        <link rel="stylesheet" type="text/css" href="style/inputform.css">
    </head>
    <body>
        <div style="text-align: center;">
            <div style="display: inline-block; text-align: left;">
                <h2>Create RedditAccount</h2>

                <div class="form-style-2">
                    <div class="form-style-2-heading">Please enter your information</div>
                    <form method="post">

                        <label for="field1"><span>Name <span class="required">*</span></span><input type="text" class="input-field" name="name" value="" /></label>

                        <label for="field2"><span>Link Points <span class="required">*</span></span><input type="text" class="input-field" name="link-points" value="" /></label>
                        <label for="field2"><span>Comment Points <span class="required">*</span></span><input type="text" class="input-field" name="comment-points" value="" /></label>
                        <label><input type="submit" name="add"  value="Add" /> <input type="submit" name="view" value="Add and View" /></label>
<!--                        <label><span> </span></label>-->

                    </form>



                </div>
            </div>
        </div>
    </body>
</html>
