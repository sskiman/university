<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<html lang="en">
<head>
    <spring:url value="/resources/css/style.css" var="mainCss" />
    <link href="${mainCss}" rel="stylesheet" />
    <spring:url value="/resources/css/menu.css" var="menuCss" />
    <link href="${menuCss}" rel="stylesheet" />
</head>

<body>

<nav>
    <ul>
        <li><a href="home">Home</a></li>
        <li><a href="#">Schedule</a>
            <ul>
                <li><a href="student">Student</a></li>
                <li><a href="teacher">Teacher</a></li>
            </ul>
        </li>

    </ul>
</nav>

<select>
    <c:if test="${!empty listUniversities}">
        <option value="">Select university</option>
        <ul>
            <c:forEach var="listValue" items="${listUniversities}">
                <option value=${listValue.getName()}>${listValue.getName()}</option>
            </c:forEach>
        </ul>
    </c:if>
</select>

<select>
    <c:if test="${!empty listTeachers}">
        <option value="">Select teacher</option>
        <ul>
            <c:forEach var="listValue" items="${listTeachers}">
                <option value=${listValue.getName()}>${listValue.getName()}</option>
            </c:forEach>
        </ul>
    </c:if>
</select>

<form action="handler.php">
    <p><b>period</b></p>
    <p><input name="period" type="radio" value="week"> week</p>
    <p><input name="period" type="radio" value="month" checked> month</p>
</form>
<form action="handler.php">
    <input type="button" value="Get"/>
</form>
</body>

<table class="features-table">
<thead>
	<tr>
        <td class="grey">Date</td>
		<td class="grey">Group</td>
		<td class="grey">Audience</td>
		<td class="grey">Subject</td>
	</tr>
</thead>

<tfoot>
    <c:if test="${!empty listSchedule}">
        <tr>
        <c:forEach var="listValue" items="${listSchedule}">
            <td class="grey">${listValue.Date}</td>
        </c:forEach>
        </tr>
    </c:if>
</tfoot>

</table>

</body>
</html>