<%-- 
    Document   : Input-RedditAccount
    Created on : Dec. 6, 2020, 8:26:39 p.m.
    Author     : kiwoong kim
--%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="toDay" class="java.util.Date" />
<c:set var="nowDate"><fmt:formatDate value="${toDay}" pattern="yyyyMMdd" /></c:set> 
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title> ${title}</title>
        <link rel="stylesheet" type="text/css" href="style/tablestyle.css">
        <link rel="stylesheet" type="text/css" href="style/inputform.css">
    </head>
    <body>
        <div style="text-align: center;">
            <div style="display: inline-block; text-align: center;">
                <h2> ${title}</h2>

                <div class="form-style-2">
                    <div class="form-style-2-heading">Please enter information</div>
                    <form method="post">

                        <c:forEach var="code" items="${columnCode}">
                           <c:choose >
                                 <c:when test = "${code=='created'}">
                                     <label for="field2"><span>${code.substring(0, 1).toUpperCase()}${code.substring(1)} <span class="required">*</span></span><input type="text" class="input-field" name="${code}" placeholder="${nowDate}" value="" /></label>
                                </c:when>
                                <c:when test = "${code!='id'}">
                                <label for="field2"><span>${code.substring(0, 1).toUpperCase()}${code.substring(1)} <span class="required">*</span></span><input type="text" class="input-field" name="${code}" value="" /></label>
                                </c:when>
                            </c:choose>
                        </c:forEach>

                        <!--                       
                                                <label for="field2"><span>Unique_ID <span class="required">*</span></span><input type="text" class="input-field" name="unique_id" value="" /></label>
                                                <label for="field2"><span>Reddit_Account_ID <span class="required">*</span></span><input type="text" class="input-field" name="reddit_account_id" value="" /></label>
                                                <label for="field2"><span>Subreddit_ID <span class="required">*</span></span><input type="text" class="input-field" name="subreddit_id" value="" /></label>
                                                <label for="field2"><span>Comment_Count <span class="required">*</span></span><input type="text" class="input-field" name="comment_count" value="" /></label>
                                                <label for="field2"><span>Points <span class="required">*</span></span><input type="text" class="input-field" name="points" value="" /></label>
                                                <label for="field2"><span>Created <span class="required">*</span></span><input type="text" class="input-field" name="created" value="" /></label>-->
                        <label><input type="submit" name="add"  value="Add" /> <input type="submit" name="view" value="Add and View" /></label>
                        <!--                        <label><span> </span></label>-->

                    </form>
                    <p color="red"><font color="red" size="4px">${errorMessage}</font></p>


                </div>
            </div>
        </div>

        <form>
            <table style="vertical-align:middle">
                <tr>
                    <td><input type="text" name="searchText" /></td>
                    <td><input type="submit" value="Search" /></td>
                </tr>
            </table>
        </form>
        <form method="post">
            <table border="1">
                <tr>
                    
                        <c:forEach var="name" items="${columnName}">
                        <th>${name}</th>
                        </c:forEach>
                </tr>
                <c:set var="counter" value="-1"/>
                <c:forEach var="entity" items="${entities}">
                    <tr>
                        <c:set var="counter" value="${counter+1}"/>
                        
                            <c:forEach var="data" items="${entity}">
                                <c:set var="counter" value="${counter+1}"/>
                            <td class="name" id="${counter}" >${data}</td>
                        </c:forEach>
                    </tr>
                </c:forEach>
                <tr>
                   
                        <c:forEach var="name" items="${columnName}">
                        <th>${name}</th>
                        </c:forEach>
                </tr>
            </table>
        </form>
        <div style="text-align: center;">
            <pre>${path}</br>${request}${message}</pre>
        </div>
    </body>
</html>
