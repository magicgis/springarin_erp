<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>当天自发货订单</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$(".editQuantity").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(":hidden").val();
					param.quantity = newValue;
					$.get("${ctx}/amazonAndEbay/mfnOrder/updateQuantity?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("Update quantity success！", 'info',{timeout:2000});
							$("#inputForm").submit();
						}
					});
					return true;
				}});
			
			$("#btnSubmit").click(function(){
				$("#inputForm").attr("action","${ctx}/amazonAndEbay/mfnOrder/createCurrentOrder");
				$("#inputForm").submit();
				$("#inputForm").attr("action","${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder");
			});
			
			$("#isOld").change(function(){
				$("#inputForm").submit();
			});
		});
		
		
	</script>
</head>
<body>
<ul  class="nav nav-tabs">
	 <li  class="active"><a href="#">${fns:getDictLabel(mfnOrder.country,'platform','')} Delivery List</a></li>	

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
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="mfnOrder" action="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder" method="post" class="breadcrumb form-search" cssStyle="height: 40px;">
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
				<label><strong>QueryTime：</strong></label>
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#inputForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="buyTime" value="<fmt:formatDate value="${mfnOrder.buyTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			    &nbsp;-&nbsp;
			    <input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#inputForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="lastModifiedTime" value="<fmt:formatDate value="${mfnOrder.lastModifiedTime}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				<label><strong>ProductName/SKU：</strong></label>
				<input type="text" name="orderId" value="${mfnOrder.orderId}" class="input-small"/>
				&nbsp;&nbsp;
				<select name='isOld' id='isOld'>
				   <option value=''>Exclude Old Products</option>
				   <option value='0' ${'0' eq mfnOrder.isOld?'selected':'' }>Only Old Products</option>
				</select>
				<input type="hidden" name="country" value="${mfnOrder.country}"/>
				&nbsp;&nbsp;&nbsp;<input  class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
				&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="export"/>
			</div>
		</div>
   </form:form>		
	    <table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">product_name</th>
				   <th style="width: 8%">total_quantity</th>
				   <th style="width: 20%">order_no</th>
				   <th style="width: 10%">bill_no</th>
				   <th style="width: 8%">order_quantity</th>
				   <th style="width: 20%">print_time</th>
			</tr>
		</thead>
		<tbody>
		  <c:forEach items="${totalMap}" var="map">
		    <c:forEach items="${itemMap[map.key]}" var="list" varStatus="i">
				   <tr>
				     <c:if test="${i.count==1}">
				       <td rowspan="${fn:length(itemMap[map.key])}" style="vertical-align: middle;">${map.key}</td>
				       <td rowspan="${fn:length(itemMap[map.key])}" style="vertical-align: middle;">${totalMap[map.key]}</td>
				     </c:if>  
					   <td style="vertical-align: middle;">${list.orderId }</td>
					   <td style="vertical-align: middle;">${list.billNo }</td>
					   <td style="vertical-align: middle;">${list.quantityShipped}
						<%-- <input type="hidden" value="${list.id }" />
						<a href="#" class="editQuantity"  data-type="text"  data-pk="1" data-title="Enter Quantity" >${list.quantityShipped}</a> --%>
				       </td>
				        <td style="vertical-align: middle;"><fmt:formatDate value="${list.printTime }" pattern="yyyy-MM-dd HH:mm" /></td>
				   </tr>
			  </c:forEach>
		 </c:forEach>
		</tbody>
	</table>
</body>
</html>
