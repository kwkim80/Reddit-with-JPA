<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css" />
        

        <title>${title}</title>
    </head>
    <body>
            <h1>${title}</h1>
            <form class="input_form" method="POST">
            <c:forEach var="col" items="${columns}">
              <div id="${col}" class="input_field" >
                <span>${col.toLowerCase()}</span>
                <input type="text" name="${col}" id="">
              </div>
            </c:forEach>
            <c:if test="${error !=''}">
              <h4 class="error_msg"> ${error}<h4>
            </c:if>
                <input type="submit" />
            </form>
    </body>
</html>
