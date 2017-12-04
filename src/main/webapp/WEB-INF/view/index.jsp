<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<html lang="en">
<head>
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
</body>
</html>

