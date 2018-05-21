<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>vendor订单</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
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
			 $("a[rel='popover']").popover({trigger:'hover'});
			 
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			$(".open").click(function(e){
				if($(this).text()=='Summary'){
					$(this).text('close');
				}else{
					$(this).text('Summary');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			$(".singleOutBound").click(function(e){
				var idsAll = $(this).parent().find("input[type='hidden']").val();
				top.$.jBox.confirm("Are you sure out bound？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						   $("#outBound").attr("disabled","disabled");
						   $.post("${ctx}/amazoninfo/vendorOrder/outBound",{ids:idsAll},function(date){
							   if(date=='0'){
								   $.jBox.tip('outBound failed');
								   $("#searchForm").submit();
							   }else{
								   $.jBox.tip('outBound success');
								   $("#searchForm").submit();
							   }
					       }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$(".btnClose").click(function(e){
				var id= $(this).parent().find("input[type='hidden']").val();
				top.$.jBox.confirm("Are you sure close？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						   $.post("${ctx}/amazoninfo/vendorOrder/updateCheckFlag",{id:id},function(date){
							   if(date=='0'){
								   $.jBox.tip('close failed');
								   $("#searchForm").submit();
							   }else{
								   $.jBox.tip('close success');
								   $("#searchForm").submit();
							   }
					       }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$(".btnOpen").click(function(e){
				var id= $(this).parent().find("input[type='hidden']").val();
				top.$.jBox.confirm("Are you sure open？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						   $.post("${ctx}/amazoninfo/vendorOrder/updateExceptionCheckFlag",{id:id},function(date){
							   if(date=='0'){
								   $.jBox.tip('Open failed');
								   $("#searchForm").submit();
							   }else{
								   $.jBox.tip('Open success');
								   $("#searchForm").submit();
							   }
					       }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#outBound").click(function(){
				var ids = $("input:checkbox[name='checkId']:checked");
				if(!ids.length){
			    	$.jBox.tip('Please select data ！');
					return;
				}		
				var arr = new Array();
				for(var i=0;i<ids.length; i++){
					var id = ids[i].value;
					arr.push(id);
				}
				var idsAll = arr.join(',');
				top.$.jBox.confirm("Are you sure out bound？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						   $("#outBound").attr("disabled","disabled");
						   $.post("${ctx}/amazoninfo/vendorOrder/outBound",{ids:idsAll},function(date){
							   if(date=='0'){
								   $.jBox.tip('outBound failed');
								   $("#searchForm").submit();
							   }else{
								   $.jBox.tip('outBound success');
								   $("#searchForm").submit();
							   }
					       }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
				
			});
			
			$("#synchronizeOrder").click(function(){
				var params = {};
				params['country'] = $("#country").val();
				top.$.jBox.confirm("Are you sure synchronize order？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						top.$.jBox.tip("Please Waiting for 5 minutes！", 'loading',{timeout:10000});
						$.post("${ctx}/amazoninfo/vendorOrder/synchronizeVendorOrder",$.param(params),function(date){
							 setTimeout("refreshForm()",1000*60*5);
					    }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#checkall").click(function(){
				 $('[name=checkId]:checkbox').each(function(){
				     if($(this).attr("disabled")!='disabled'){
				    	 this.checked=this.checked;
				     }else{
				    	 this.checked=false;
				     }
				 });
			});
			
			$("#deliveryList").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/showDeliveryOrder");
				$("#start").val('');
				$("#end").val('');
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/list");
			});
			
			$(".countryHref").click(function(){
				$("#country").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$(".feeEditable").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(":hidden").val();
					param[$(this).attr("key")] = newValue;
					$.get("${ctx}/amazoninfo/vendorOrder/update?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("save success！", 'info',{timeout:2000});
						}
					});
					return true;
				}});
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/expVendorOrderByCsv");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/list");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#exceptionExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/exportExceptionAsn");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/list");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
		});	
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
	    	return false;
	    }
		
		function refreshForm(){
			$("#searchForm").submit();
		}
		function openNewWindow(type,billNo){
			var arr=billNo.split(",");
			for(var i=0;i<arr.length;i++){
				if(arr[i].trim()!=""){
					if(type=='1'){
						window.open("https://tracking.dpd.de/parcelstatus?locale=en_D2&query="+arr[i]);  
					}else if(type=='2'){
						window.open("https://activetracing.dhl.com/DatPublic/search.do?search=consignmentId&autoSearch=true&l=DE&at=package&a="+arr[i]);
					}else if(type=='3'){
						window.open("http://wwwapps.ups.com/WebTracking/track?track=yes&trackNums="+arr[i]);
					}
				}
			}
		}
		
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
		<li  class="active"><a href="#">${fns:getDictLabel(vendorShipment.country,'platform','')} Vendor List</a></li>	
		<li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">DE Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a  class="countryHref" href="#" key="de" >DE Order List</a></li>	
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/returnList?country=de?">DE Return List</a></li> 
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/showDeliveryOrder?country=de">DE Delivery List</a></li> 
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/unconfirmedOrder?country=de">DE Unconfirmed Order List</a></li> 
		    </ul>
	    </li>
	    
	    <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">US Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a  class="countryHref" href="#" key="com" >US Order List</a></li>	
				  <%--  <li><a  href="${ctx}/amazoninfo/vendorOrder/returnList?country=com?">US Return List</a></li>  --%>
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/showDeliveryOrder?country=com">US Delivery List</a></li> 
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/unconfirmedOrder?country=com">US Unconfirmed Order List</a></li> 
		    </ul>
	    </li>
	    
	    <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">UK Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a  class="countryHref" href="#" key="uk" >UK Order List</a></li>	
				  <%--  <li><a  href="${ctx}/amazoninfo/vendorOrder/returnList?country=uk?">UK Return List</a></li>  --%>
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/showDeliveryOrder?country=uk">UK Delivery List</a></li> 
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/unconfirmedOrder?country=uk">UK Unconfirmed Order List</a></li> 
		    </ul>
	    </li>
	    
	<%-- 	<li  class="${vendorShipment.country  eq 'de' ?'active':''}"><a class="countryHref" href="#" key="de" >DE Vendor List</a></li>
		<li  class="${vendorShipment.country  eq 'com' ?'active':''}"><a class="countryHref" href="#" key="com">US Vendor List</a></li>
		<li  class="${vendorShipment.country  eq 'uk' ?'active':''}"><a class="countryHref" href="#" key="uk">UK Vendor List</a></li>
	    
		 --%>
	</ul> 
	<form:form id="searchForm" modelAttribute="vendorShipment" action="${ctx}/amazoninfo/vendorOrder/list" method="post" class="breadcrumb form-search" cssStyle="height: 40px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="country" name="country" type="hidden" value="${vendorShipment.country}" />
		<div style="height: 30px;line-height: 30px">
			<div>
			<label><strong>ShipDate：</strong></label>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="shipDate" value="<fmt:formatDate value="${vendorShipment.shipDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		    &nbsp;-&nbsp;
		    <input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="deliveryDate" value="<fmt:formatDate value="${vendorShipment.deliveryDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;
			<label><strong>OrderId/ASN：</strong></label><form:input path="asn" htmlEscape="false" maxlength="50" class="input-small"/>
			<input  class="btn btn-primary" type="submit" value="Search"/> 
			<!-- <input id="deliveryList" class="btn btn-primary" type="button" value="Delivery List"/>  -->
			<shiro:hasPermission name="amazoninfo:vendorOrder:edit">
				<input id="outBound" class="btn btn-primary" type="button" value="Out Bound"/>
				<input id="synchronizeOrder" class="btn btn-primary" type="button" value="Synchronize Order"/>
		 	</shiro:hasPermission>
		 	<div class="btn-group">
						   <button type="button" class="btn"><spring:message code="sys_but_export"/></button>
						   <button type="button" class="btn dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" >
						       <li><a id="btnExport"><spring:message code="sys_but_export"/></a></li>
						       <li><a id="exceptionExport"><spring:message code="Exception Export"/></a></li>
						   </ul>
			</div>
			</div>
		</div>
		
	</form:form>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr>
				   <th style="width: 5%"><input type="checkBox" id="checkall"></th>	
				   <th style="width: 7%">ASN</th>
				   <th style="width: 8%">BillStatus</th>
				   <th style="width: 8%">Status</th>
				   <th style="width: 6%">ShipDate</th>
				   <th style="width: 6%">DeliveryDate</th>
				   <th style="width: 12%">ShippedDate</th>
				   <th style="width: 5%">SCAC</th>
				   <th style="width: 7%">CarrierTracking</th>
				   <th style="width: 5%">Packages</th>
				   <th style="width: 5%">Fee</th>
				   <th style="width: 18%">Operate</th>
			 </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="vendor"  varStatus="k">
		   
		   <tr style="${'1' eq vendor.checkStatu&& empty vendor.checkUser ?'background-color:#ff9900;':'' }">
		     <td  style="text-align: left;">
				<input type="checkBox" class="chebox" ${'Shipped' eq vendor.status?'disabled':''}  name="checkId" value="${vendor.id}"/>
			 </td>
			 <td><a target='_blank' href="${ctx}/amazoninfo/vendorOrder/form?id=${vendor.id}">${vendor.asn}</a></td>
			 <td><%-- ${vendor.shipmentStatus} --%>
			    <c:choose>
					<c:when test="${vendor.billStatu eq '2' }">Can generate</c:when>
					<c:when test="${not empty vendor.billStatu}">${vendor.billStatu}</c:when>
					<c:otherwise>Can not generate</c:otherwise>
				</c:choose>
			 
			 </td>
			 <td>${vendor.status}</td>
			 <td><fmt:formatDate value="${vendor.shipDate}" pattern="yyyy-MM-dd" /></td>
			 <td><fmt:formatDate value="${vendor.deliveryDate}" pattern="yyyy-MM-dd" /></td>
			 <td><fmt:formatDate value="${vendor.shippedDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			 <td>${vendor.carrierSCAC}</td>
			 <td>
			   <c:if test="${not empty vendor.carrierTracking}">
				     <c:if test="${'DPD' eq vendor.carrierSCAC}">  
				          <a onclick="openNewWindow('1','${vendor.carrierTracking}')" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${vendor.carrierTracking}">${fns:abbr(vendor.carrierTracking,18)}</a>
					 </c:if>
					 <c:if test="${'DHL' eq vendor.carrierSCAC}">  
					   <a onclick="openNewWindow('2','${vendor.carrierTracking}')" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${vendor.carrierTracking}">${fns:abbr(vendor.carrierTracking,18)}</a>
					 </c:if>
					 <c:if test="${'UNITED PARCEL SERVICE INC' eq vendor.carrierSCAC}">  
					   <a onclick="openNewWindow('3','${vendor.carrierTracking}')" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${vendor.carrierTracking}">${fns:abbr(vendor.carrierTracking,18)}</a>
					 </c:if>
				</c:if>	
			 </td>
			 <td>${vendor.packages}</td>
			 <td>
			    <input type="hidden" value="${vendor.id }"/>
			    <shiro:hasPermission name="amazoninfo:vendorOrder:edit">
			 	   <a class="feeEditable" href="#"  key="fee" keyVal="${vendor.fee}" data-type="text" data-pk="1" data-title="Enter Fee" data-value="${vendor.fee}">${vendor.fee}</a>
			    </shiro:hasPermission>
			    <shiro:lacksPermission name="amazoninfo:vendorOrder:edit">${vendor.fee}</shiro:lacksPermission>
			 </td> 
			 <td>
			    <input type="hidden" value="${vendor.id }"/>
				<a class="btn btn-small btn-info open">Summary</a>
				<shiro:hasPermission name="amazoninfo:vendorOrder:edit"> 
				     <c:if test="${vendor.status!='Shipped' }"> 
				        <input id="btnSubmit"  class="btn btn-small singleOutBound" type="button" value="Out Bound"/>
				     </c:if> 
				    <%--  <c:if test="${fn:contains(shipmentIdList,vendor.id)&& empty vendor.checkUser}"> --%>
				     <c:if test="${'1' eq vendor.checkStatu&& empty vendor.checkUser}"> 
				       &nbsp;&nbsp;<input  class="btn btn-small btnClose" type="button" value="Close"/>
				     </c:if>  
				     <c:if test="${empty vendor.checkStatu}"> 
				       &nbsp;&nbsp;<input  class="btn btn-small btnOpen" type="button" value="Open"/>
				     </c:if>  
				</shiro:hasPermission> 
			 </td>
			</tr>
			
			<c:if test="${fn:length(vendor.orders)>0}">
				   <tr style="background-color:#D2E9FF;display: none" name="${vendor.id}">
					    <td>orderId</td><td>status</td><td>deliveryWindow</td><td>submittedCost</td><td>acceptedCost</td><td>receivedCost</td>
					    <td>productName</td><td>submitted</td><td>accepted</td><td>received</td><td>unitPrice</td><td>itemPrice</td>
	               </tr>
				  <c:forEach items="${vendor.orders}" var="vendorOrder"  varStatus="k">
					<c:forEach items="${vendorOrder.items}" var="item" varStatus="i">
						<tr style="background-color:#D2E9FF;display: none" name="${vendor.id}">
							<c:if test="${i.count==1}">
								<td rowspan="${fn:length(vendorOrder.items)}" style="text-align: left;"><a target='_blank' href="${ctx}/amazoninfo/vendorOrder/vendorForm?id=${vendorOrder.id}">${vendorOrder.orderId}</a></td>
								<td rowspan="${fn:length(vendorOrder.items)}" style="text-align: left;">${vendorOrder.status}</td>
								<td rowspan="${fn:length(vendorOrder.items)}" style="text-align: left;">${vendorOrder.deliveryWindow}</td>
								<td rowspan="${fn:length(vendorOrder.items)}" style="text-align: left;">${vendorOrder.submittedTotalCost}</td>
								<td rowspan="${fn:length(vendorOrder.items)}" style="text-align: left;">${vendorOrder.acceptedTotalCost}</td>
								<td rowspan="${fn:length(vendorOrder.items)}" style="text-align: left;">${vendorOrder.receivedTotalCost}</td>
							</c:if>
							<td style="text-align:left;">
							    <c:if test="${not empty item.productName}"><a  href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}" target='_blank'>${item.productName}</a></c:if>
							    <c:if test="${empty item.productName}"><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${item.title}">${fns:abbr(item.title,15)}</a></c:if>
							</td>
							<td style="text-align:left;${'1' eq vendor.checkStatu&& empty vendor.checkUser&&item.acceptedQuantity!=item.submittedQuantity&&item.acceptedQuantity!=0 ?'color:#ff0033;':'' }">${item.submittedQuantity}</td>
							<td style="text-align:left;${'1' eq vendor.checkStatu&& empty vendor.checkUser&&item.acceptedQuantity!=item.submittedQuantity&&item.acceptedQuantity!=0 ?'color:#ff0033;':'' }">${item.acceptedQuantity}</td>
							<td style="text-align:left;${'1' eq vendor.checkStatu&& empty vendor.checkUser&&item.acceptedQuantity!=item.submittedQuantity&&item.acceptedQuantity!=0 ?'color:#ff0033;':'' }">${item.receivedQuantity}</td>
							<td style="text-align:left;">${item.unitPrice}</td>
							<td style="text-align:left;">${item.itemPrice}</td>
						</tr> 
					</c:forEach> 
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
