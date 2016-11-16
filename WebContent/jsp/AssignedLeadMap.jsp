<%-- <%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<script language="javascript"  src="<%=request.getContextPath()+"/jScripts/ajaxCall.js"%>"></script>
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=AIzaSyBekW9n7B9nJZhQn2QwvWH7de9miY9uvjE" type="text/javascript"></script>
<bean:define id="recordslimitdays" name="recordslimitdays" scope="session"></bean:define>
<script type="text/javascript">

    var map = null;
    var geocoder = null;
var OlmId =null;
var Name  = null;
var LeadId =null;
    function initialize() {
      if (GBrowserIsCompatible()) {
        map = new GMap2(document.getElementById("map_canvas"));
        map.setCenter(new GLatLng(28.4595,77.0266), 1);
        map.setUIToDefault();
        geocoder = new GClientGeocoder();
      }
       OlmId = '<%= request.getAttribute("OlmId")%>'
       Name = '<%=request.getAttribute("UserName")%>'
       LeadId = '<%=request.getAttribute("LeadId")%>'
    }

    function showAddress(address) {
      if (geocoder) {
        geocoder.getLatLng(
          address,
          function(point) {
            if (!point) {
              alert(address + " not found");
            } else {
              map.setCenter(point, 9);
              var marker = new GMarker(point, {draggable: true});
              map.addOverlay(marker);
              GEvent.addListener(marker, "dragend", function() {
                marker.openInfoWindowHtml(marker.getLatLng().toUrlValue(6));
              });
              GEvent.addListener(marker, "onclick", function() {
                marker.openInfoWindowHtml(Name+'('+OlmId+')');
              });
              GEvent.addListener(marker, "click", function() {
             if(OlmId !=null && OlmId !="") {
              if(confirm("Do you want to assgined this lead to "+ Name+' ('+OlmId+')')){
				LeadForwardToFSC(LeadId,OlmId);
                marker.openInfoWindowHtml("Lead has been Assigned to " +Name+'('+OlmId+')');
                }else {
                 marker.openInfoWindowHtml("Lead can not be Assign.");
                }
                }
              });
	      GEvent.trigger(marker, "onclick");
            }
          }
        );
      }
    }
    
    function LeadForwardToFSC(LeadId,OlmId){
    //alert(LeadId+"alert"+OlmId)
    document.forms[0].methodName.value= "LeadForwardToMap";
	//alert(document.forms[0].methodName.value);
	if(OlmId !=null && OlmId !="") {
	var data = "methodName=LeadForwardToMap&leadID="+LeadId+"&OlmId="+OlmId;
	doAjax("qualifiedLeads.do", "GET", false,leadMapResult,data);
	}
    }
    function leadMapResult(){
    }
    </script>


<body onload="initialize();showAddress('<%out.print(request.getAttribute("Pincode"));%>')">

<html:form action="qualifiedLeads.do">
<html:hidden name="LeadForm" property="methodName"/>
<center>
 <div id="map_canvas" style="width: 750px; height: 500px"></div>
<!--<html:submit styleClass="red-btn" value="Submit" onclick="return validateForm()"/>
--></center>
</html:form>
<body> --%>



<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ page import="com.ibm.nas.wf.dto.LeadDetailDTO"%>

<%@page import="com.ibm.nas.services.MasterService"%>
<%@page import="com.ibm.nas.services.impl.MasterServiceImpl"%>
<%@page import="com.ibm.nas.dto.UserMstr"%>
<%@page import="com.ibm.nas.common.PropertyReader"%><LINK href="./theme/css.css" rel="stylesheet" type="text/css">

<LINK href="./jsp/theme/css.css" rel="stylesheet" type="text/css">

<LINK href="theme/text.css" rel="stylesheet" type="text/css">





<script language="javascript"  src="<%=request.getContextPath()+"/jScripts/ajaxCall.js"%>"></script>
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=AIzaSyBekW9n7B9nJZhQn2QwvWH7de9miY9uvjE" type="text/javascript"></script>
<bean:define id="recordslimitdays" name="recordslimitdays" scope="session"></bean:define>
<script type="text/javascript">

    var map = null;
    var geocoder = null;
var OlmId =null;
var Name  = null;
var LeadId =null;
    function initialize() {
      if (GBrowserIsCompatible()) {
        map = new GMap2(document.getElementById("map_canvas"));
        map.setCenter(new GLatLng(28.4595,77.0266), 1);
        map.setUIToDefault();
        geocoder = new GClientGeocoder();
      }
       OlmId = '<%= request.getAttribute("OlmId")%>'
       Name = '<%=request.getAttribute("UserName")%>'
       LeadId = '<%=request.getAttribute("LeadId")%>'
    }

    function showAddress(address) {
      if (geocoder) {
        geocoder.getLatLng(
          address,
          function(point) {
            if (!point) {
              alert(address + " not found");
            } else {
              map.setCenter(point, 9);
              var marker = new GMarker(point, {draggable: true});
              map.addOverlay(marker);
              GEvent.addListener(marker, "dragend", function() {
                marker.openInfoWindowHtml(marker.getLatLng().toUrlValue(6));
              });
              GEvent.addListener(marker, "onclick", function() {
                marker.openInfoWindowHtml(Name+'('+OlmId+')');
              });
              GEvent.addListener(marker, "click", function() {
             if(OlmId !=null && OlmId !="") {
              if(confirm("Do you want to assgined this lead to "+ Name+' ('+OlmId+')')){
				LeadForwardToFSC(LeadId,OlmId);
                marker.openInfoWindowHtml("Lead has been Assigned to " +Name+'('+OlmId+')');
                }else {
                 marker.openInfoWindowHtml("Lead can not be Assign.");
                }
                }
              });
	      GEvent.trigger(marker, "onclick");
            }
          }
        );
      }
    }
    
    function LeadForwardToFSC(LeadId,OlmId){
    //alert(LeadId+"alert"+OlmId)
    document.forms[0].methodName.value= "LeadForwardToMap";
	//alert(document.forms[0].methodName.value);
	if(OlmId !=null && OlmId !="") {
	var data = "methodName=LeadForwardToMap&leadID="+LeadId+"&OlmId="+OlmId;
	doAjax("qualifiedLeads.do", "GET", false,leadMapResult,data);
	}
    }
    function leadMapResult(){
    }
    </script>


<body onload="initialize();showAddress('<%out.print(request.getAttribute("Pincode"));%>')">

<html:form action="qualifiedLeads.do">
<html:hidden name="LeadForm" property="methodName"/>

<html:hidden name="LeadForm" property="leadID" />
	<div class="box2">                            
		<div class="content-upload" style="margin-bottom: 0px;">
          <h1><bean:message key="view.leadDetail" /></h1>
        </div>
         
			<fieldset>
							<legend><h5>Lead Detail</h5></legend>
							<logic:present name="detail" scope="request">
							<logic:notEmpty name="detail" scope="request">
							<table border="0" width="100%" cellpadding="1" cellspacing="1" cellspacing="1">
							<tr>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>Lead ID &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="LeadForm"	property="leadID" /></td>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>Customer Name &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="customer_name" /></td>
							</tr>
							<tr>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>Circle Name &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="circle_name" /></td>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>State Name &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="state_name" /></td>
							</tr>
							<tr>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>City Name &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="city_name" /></td>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>Zone Name &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="zone_name" /></td>
							</tr>
							<tr>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>Email ID &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="email" /></td>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>RSU ID &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="rsu_id" /></td>
							</tr>
							<tr>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>Pin code &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="pincode" /></td>
								 <% UserMstr userBean= (UserMstr) session.getAttribute("USER_INFO");
			 					String actorId =userBean.getKmActorId();
								  if("N".equalsIgnoreCase(request.getAttribute("Mobile_FLAG").toString()) || PropertyReader.getAppValue("channel.partner").equalsIgnoreCase(actorId)){
								 %>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>Mobile Number &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="prospect_mobile_number" /></td>
						
							</tr>
							<tr>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>Landline Number &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="landline_number" /></td>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>Alternate Contact Number &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="alternate_contact_number" /></td>
								<%} %>
							</tr>
							<tr>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>Address &nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left"><bean:write name="detail"	property="address1" /></td>
								<td width="25%" class="text" align="right"><font color="#4b0013"><b>&nbsp;&nbsp;</b></font></td>
								<td width="25%" align="left">&nbsp;</td>
							</tr>
							</table>
							</logic:notEmpty>
							</logic:present>
						</fieldset><br/>
						
 </div>





<center>
 <div id="map_canvas" style="width: 750px; height: 500px"></div>
<!--<html:submit styleClass="red-btn" value="Submit" onclick="return validateForm()"/>
--></center>
</html:form>
<body>


