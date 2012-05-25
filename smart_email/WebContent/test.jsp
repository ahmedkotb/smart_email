<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<sql:query var="rs" dataSource="jdbc/SmartEmailDB">
select email, token from accounts
</sql:query>

<html>
  <head>
    <title>DB Test</title>
  </head>
  <body>

  <h2>Results</h2>
  
<c:forEach var="row" items="${rs.rows}">
    email ${row.email}<br/>
    token ${row.token}<br/>
</c:forEach>

  </body>
</html>