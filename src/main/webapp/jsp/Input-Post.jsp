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
            <div style="display: inline-block; text-align: center;">
                <h2>Create Post</h2>

                <div class="form-style-2">
                    <div class="form-style-2-heading">Please enter information</div>
                    <form method="post">

                        <label for="field1"><span>Title <span class="required">*</span></span><input type="text" class="input-field" name="Title" value="" /></label>
                        <label for="field2"><span>Unique_ID <span class="required">*</span></span><input type="text" class="input-field" name="Unique_ID" value="" /></label>
                        <label for="field2"><span>Reddit_Account_ID <span class="required">*</span></span><input type="text" class="input-field" name="Reddit_Account_ID" value="" /></label>
                        <label for="field2"><span>Subreddit_ID <span class="required">*</span></span><input type="text" class="input-field" name="Subreddit_ID" value="" /></label>
                        <label for="field2"><span>Comment_Count <span class="required">*</span></span><input type="text" class="input-field" name="Comment_Count" value="" /></label>
                        <label for="field2"><span>Points <span class="required">*</span></span><input type="text" class="input-field" name="Points" value="" /></label>
                        <label for="field2"><span>Created <span class="required">*</span></span><input type="text" class="input-field" name="Created" value="" /></label>
                        <label><input type="submit" name="add"  value="Add" /> <input type="submit" name="view" value="Add and View" /></label>
                        <!--                        <label><span> </span></label>-->

                    </form>



                </div>
            </div>
        </div>
    </body>
</html>
