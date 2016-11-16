
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<title><header>NAS Logout</header></title>
<link href="./jsp/theme/airtel.css" rel="stylesheet" type="text/css" />

<body onload="setTimeout(function(){window.location = './NASLogin.do';},5000)">

<table width="778" border="0" align="center" cellpadding="0" cellspacing="0" style="background-image:url(./images/bg_1.jpg); background-repeat:repeat-y; background-position:center top;">
  <tr>
    <td width="177" height="91" align="right"><img src="common/images/NAS.png" alt="LMS" width="158" height="44" /></td>
    <td width="470">&nbsp;</td>
    <td width="131"><img src="./images/airtel_logo.jpg" alt="Airtel" width="107" height="80" /></td>
  </tr>
  <tr>
    <td colspan="3" align="center"><img src="./images/logedout.png" alt="" width="770" height="250" /></td>
  </tr>
  <tr>
    <td height="36" colspan="3"><table width="770" border="0" align="center" cellpadding="0" cellspacing="0">
 <!--   <tr>
    <td height="36" align="center" style="background-image:url(./images/top_heading_heading.jpg); background-repeat:repeat-x; background-position:left top;"></td>
  </tr>-->
</table></td>
  </tr>
  <tr>
    <td height="265" colspan="3" align="center"><table width="400" border="0" cellpadding="0" cellspacing="0" bgcolor="#D9EEF6">
      <h3><strong><center><font color="red" size='5'>User logged out,please visit NAS Login Page</font></center> </strong></h3><br>
      <h2><center><font color="blue" size='3'>if Automatically not redirected,Please click to </font><a href="./NASLogin.do" size="1"><font color="red" size='2'>Login page</font></a><font color="blue" size='3'> reach NAS login page</font></center></h2>
      <h5><center><font color="blue" size='3'>Window will be redirect to login Page</font></center></h5>
    </table></td>
  </tr>
  <tr>
    <td height="55" colspan="3" align="center"><table width="771" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="55" align="left" valign="top" class="whttext" style="background-image:url(./images/bottom_bg.jpg); background-repeat:repeat-x; background-position:left top; padding-left:8px; padding-top:8px;">&copy; 2016 IBM Bharti, All Right Reserved.</td>
  </tr>
</table></td>
  </tr>
</table>
</body>