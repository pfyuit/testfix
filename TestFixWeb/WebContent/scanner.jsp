<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<table>
		<tr>
			<td><%@include file="navigator.jsp"%></td>
			<td>Scanner Status: <font color="green"><s:property
						value="scannerRunningStatus" /></font>
			</td>
			<td><s:form action="startscanner.action">
					<s:submit value="Start Scanner"></s:submit>
				</s:form> <s:form action="stopscanner.action">
					<s:submit value="Stop Scanner"></s:submit>
				</s:form> <s:form action="scannow.action">
					<s:submit value="Scan Now"></s:submit>
				</s:form></td>
		</tr>
	</table>



</body>
</html>