<%@page import="it.tredi.dw4.acl.beans.UserBean"%>
<% 
String redirectPage = "init.jsf";
//if (session != null && session.getAttribute("userBean") != null && session.getAttribute("aclInit") != null) {
//	UserBean userBean = (UserBean) session.getAttribute("userBean");
//	if (userBean.getUserInfo() != null)
		redirectPage = "home.jsf";
//}
response.sendRedirect(redirectPage); 
%>