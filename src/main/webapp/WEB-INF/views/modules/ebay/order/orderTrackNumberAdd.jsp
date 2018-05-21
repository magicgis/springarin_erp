<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>track number</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		if(!(top)){
			top = self; 
		}
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					var filepath = $("#excel").val();
					var extStart = filepath.lastIndexOf(".");

					var ext = filepath.substring(extStart, filepath.length).toUpperCase();
					if (ext != ".TXT" && ext != ".CSV"&& ext != ".XLS"&& ext != ".XLSX") {
						alert("文件必须是txt或者csv格式!");
						return;
					}
					top.$.jBox.confirm('确定要提交吗!','系统提示',function(v,h,f){
						if(v=='ok'){
							loading('正在提交，请稍等...');
							form.submit();
							$("#btnSubmit").attr("disabled","disabled");
						}
					},{buttonsFocus:1,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>

<ul class="nav nav-tabs">
    <li  class="active"><a href="#">${fns:getDictLabel(country,'platform','')} Track Number</a></li>	

         <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">DE Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=de">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=de">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:de">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=de">Track Number</a></li>
				   </shiro:hasPermission>
				    <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=de">Delivery List</a></li>	
		    </ul>
	    </li>


         <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">US Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=com">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=com">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:com">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=com">Track Number</a></li>
				   </shiro:hasPermission>
				    <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=com">Delivery List</a></li>	
		    </ul>
	    </li>
	    
	     <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">JP Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=jp">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=jp">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:jp">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=jp">Track Number</a></li>
				   </shiro:hasPermission>
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=jp">Delivery List</a></li>	
		    </ul>
	    </li>
	    
	      <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">CN Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=cn">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=cn">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:cn">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=cn">Track Number</a></li>
				   </shiro:hasPermission>
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=cn">Delivery List</a></li>	
		    </ul>
	    </li>
</ul>
	<form id="inputForm"  action="${ctx}/amazonAndEbay/mfnOrder/${(fn:startsWith(country,'com')||'jp' eq  country)?'readTrackNumberFile2':'readTrackNumberFile'}" enctype="multipart/form-data" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<!-- <div class="alert alert-info"><strong>文件请按照</strong></div> -->
		<input name='country'  type="hidden" value="${country}"/>
		<div class="control-group">
			<label class="control-label"><strong>Type:</strong></label>
			<div class="controls">
				<select name="type" style="width: 120px" class="required">
				    <c:if test="${fn:startsWith(country,'com')}">
				        <option value="UPS" >UPS</option>
						<option value="USPS" >USPS</option>
				    </c:if>
				    <c:if test="${'de' eq country }">
				        <option value="DHL" >DHL</option>
						<option value="DPD" >DPD</option>
				    </c:if>
				    <c:if test="${'jp' eq country }">
				       <!--  <option value="YAMATO TRANSPORT">YAMATO TRANSPORT</option>
		                <option value="SAGAWA EXPRESS">SAGAWA EXPRESS</option>
		                <option value="Nippon Express">Nippon Express</option>
		                <option value="SEINO TRANSPORTATION">SEINO TRANSPORTATION</option> -->
		                <option value="Japan Post">Japan Post</option>
		                <option value="佐川急便">佐川急便</option>
		               <!--  <option value="DHL eCommerce">DHL eCommerce</option>
						<option value="Other">Other</option> -->
				    </c:if>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><strong>File:</strong></label>
			<div class="controls">
				<input type="hidden" value="0" name="delFlag" />
				<input type="file" name="excel"  id="excel"  class="required"/> 
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="Submit"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>
