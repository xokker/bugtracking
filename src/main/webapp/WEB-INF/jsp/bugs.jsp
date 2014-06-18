<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="/resources/css/bootstrap-theme.min.css" rel="stylesheet">

    <title>Issues</title>
</head>

<body>
<jsp:include page="navigation.jsp"/>
<table style="width:98%; margin:1%">
    <tr>
        <td> <a href="/bugs/add"><button style="margin-bottom: 10px;" type="button" class="btn btn-success">New issue</button></a></td>
        <td>
                <input style="margin-left:2%;margin-bottom:10px;" id="showclosed" type="checkbox" name="Show closed"
                <c:if test="${param.showclosed}"> checked="true"</c:if> title="Show closed"> Show closed</input>
            <div style="margin-left:32%;margin-bottom: 10px;" class="btn-group">
            <button  type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                    Sort by:
                    <span class="caret"></span>
                </button>
                <ul class="dropdown-menu" role="menu">
                    <li><a href="#">Newest</a></li>
                    <li><a href="#">Priority</a></li>
                    <li><a href="#">Status</a></li>
                </ul>
                </div>
        </td>
    </tr>
    <tr>
        <td style=" width:20%; vertical-align: top;" >
            <div class="panel panel-primary">
            <!-- Default panel contents -->
            <div class="panel-heading">Browse issues</div>

            <!-- List group -->
            <ul class="list-group">
                <li class="list-group-item"><a href="#">Everyone's Issues</a></li>
                <li class="list-group-item"><a href="#">Assigned to me</a></li>
                <li class="list-group-item"><a href="#">Created by me</a></li>
                <li class="list-group-item"><a href="#">Viewed by me</a></li>
            </ul>
        </div>
        </td>
        <td rowspan="2" style="vertical-align: top; width:80%;">
        <div style="margin-left:2%; overflow-y:auto;" class="panel panel-primary">
            <div class="panel-heading">Issues</div>
            <table class="table table-hover">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Project</th>
                    <th>Priority</th>
                    <th>Status</th>
                    <th>Type</th>
                    <th>Assignee</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${bugs}" var="bug">
                <tr>
                    <td><a href="../bug/${bug.id}">${bug.title}</a></td>
                    <td>${bug.project.name}</td>
                    <td>${bug.priority}</td>
                    <td>${bug.status}</td>
                    <td>${bug.issueType}</td>
                    <td>${bug.responsible.username}</td>
                </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
</td>
    </tr>
    <tr>
        <td style=" width:20%;">
            <div class="panel panel-primary">
                <!-- Default panel contents -->
                <div class="panel-heading">Projects</div>

                <!-- List group -->
                <ul class="list-group">
                   <c:forEach items="${projects}" var="project">
                    <li class="list-group-item"><a href="?project_id=${project.id}">${project.name}</a></li>
                    </c:forEach>
                    <li class="divider"/>
                    <li class="list-group-item"><a href="?project_id=-1">All Projects</a></li>
                </ul>
            </div>
        </td>
    </tr>
</table>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="/resources/js/bootstrap.min.js"></script>
<script src="/resources/js/bugs.js"></script>
</body>
</html>
