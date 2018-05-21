<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="amazon_order_tab1"/></title>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
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
			$("#selectT").change(function(){
				if($(this).val()==''){
					$("#shipped").removeAttr("checked");
					$("#shippedVal").val('');
					$("#shipped").attr("disabled","disabled");
				}else{
					$("#shipped").removeAttr("disabled");
				}
				$("#searchForm").submit();
			});
			
			$("#country").change(function(){
				if($(this).val()==''){
					$("#shipped").removeAttr("checked");
					$("#shippedVal").val('');
					$("#shipped").attr("disabled","disabled");
				}else{
					$("#shipped").removeAttr("disabled");
				}
				$("#selectCountry").val('');
				$("#searchForm").submit();
			});
			
			//<c:if test="${amazonOrder.salesChannel eq ''}">
				$("#shipped").removeAttr("checked");
				$("#shippedVal").val('');
				$("#shipped").attr("disabled","disabled");
			//</c:if>
			
			$("#shipped").click(function(){
				if(this.checked){
					$("#shippedVal").val('1');
				}else{
					$("#shippedVal").val('');
				}
				$("#searchForm").attr("action","${ctx}/amazoninfo/order");
				$("#searchForm").submit();
			});
			
			$("#isBusinessOrder").click(function(){
				if(this.checked){
					$("#isBusinessOrderVal").val('1');
				}else{
					$("#isBusinessOrderVal").val('');
				}
				$("#searchForm").attr("action","${ctx}/amazoninfo/order");
				$("#searchForm").submit();
			});
			
			
			$(".expByExcel").click(function(){
				var params = {};
				params.purchaseDate=$("input[name='purchaseDate']").val();
				params.lastUpdateDate=$("input[name='lastUpdateDate']").val();
				params.salesChannel=$("#country").val();
				params.invoiceFlag=$("#shippedVal").val();
				params.shipServiceLevel = $("#selectCountry").val();
				params.sellerOrderId = $("#sellerOrderId").val();
				window.location.href = "${ctx}/amazoninfo/order/exp?"+$.param(params);
				
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:30000});
				//$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?'+$.param(params));
			});
			
			$(".expByCsv").click(function(){
				var params = {};
				params.purchaseDate=$("input[name='purchaseDate']").val();
				params.lastUpdateDate=$("input[name='lastUpdateDate']").val();
				params.salesChannel=$("#country").val();
				params.invoiceFlag=$("#shippedVal").val();
				params.shipServiceLevel = $("#selectCountry").val();
				params.sellerOrderId = $("#sellerOrderId").val();
				window.location.href = "${ctx}/amazoninfo/order/expByCsv?"+$.param(params);
				
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:30000});
				//$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?'+$.param(params));
			});
			
			$(".totalCsv").click(function(){
				var country = $(this).text();
				$.jBox.confirm("<input style='width: 160px;'  readonly='readonly'  class='Wdate' type='text' onclick=WdatePicker({dateFmt:'yyyy-MM'}); />", "Select Export Month", function(v, h, f){
					  if (v == 'ok'){
						  	var params = {};
						  	params.month = h.find("input").val();
						  	params.country = country;
						  	if(params.month){
						  		window.location.href = "${ctx}/amazoninfo/order/exportBySyn?"+$.param(params);
						  	}else{
						  		return false;
						  	}
					  }
					  return true; //close
				});
			});
			
			$(".btnExpBySales").click(function(){
				var country = $(this).text();
				top.$.jBox.confirm("<input style='width: 160px'  readonly='readonly'  class='Wdate' type='text' onclick=WdatePicker({dateFmt:'yyyy-MM'}); />", "Select Export Month", function(v, h, f){
					  if (v == 'ok'){
						  	var params = {};
						  	params.month = h.find("input").val();
						  	params.country = country;
						  	if(params.month){
						  		window.location.href = "${ctx}/amazoninfo/order/expByEuro?"+$.param(params);
								top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:250000});
						  	}else{
						  		return false;
						  	}
					  }
					  return true; //close
				});
			});
			
			$("#expPhone").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/order/expOrderPhone");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/order");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#expPhone2").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/order/expOrderPhone2");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/order");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#expPhone3").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/order/exportCsv");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/order");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#expPhone4").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/order/exportEbayCsv");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/order");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			
			$("#uploadTypeFile").click(function(e){
				   //$("#uploadForm").submit();
				   if($("#uploadFileName").val()==""){
						$.jBox.tip('上传文件名为空'); 
						return;
				   }
				   $("#uploadTypeFile").attr("disabled",true);
				   var formdata = new FormData($("#uploadForm")[0]);              
				   $.ajax({  
		                url :$("#uploadForm").attr("action"),  
		                type : 'POST',  
		                data : formdata,  
		                processData : false,  
		                contentType : false,  
		                success : function(responseStr) { 
		                	if(responseStr=="0"){
		                		$.jBox.tip('文件上传成功'); 
		                	}else if(responseStr=="3"){
		                		$.jBox.tip('重复订单上传,文件上传失败'); 
		                	}else{
		                		$.jBox.tip('文件上传失败'); 
		                	}
		                	$("#uploadFileName").val("");
		                	$("#uploadTypeFile").attr("disabled",false);
		                	$("#buttonClose").click();
		                },  
		                error : function(responseStr) {  
		                	$.jBox.tip('文件上传失败'); 
		                	$("#uploadTypeFile").attr("disabled",false);
		                }  
		            });  
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
		
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#"><spring:message code="amazon_order_tab1"/></a></li>
	<%-- 	<li><a href="${ctx}/ebay/order/list"><spring:message code="ebay_order_list"/></a></li> --%>
		<li><a href="${ctx}/ebay/order/list">DE Ebay List</a></li>
		<li><a href="${ctx}/ebay/order/list?country=com">US Ebay List</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/order/add"><spring:message code="amazon_order_tab2"/></a></li> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonOrder" action="${ctx}/amazoninfo/order" method="post" class="breadcrumb form-search" cssStyle="height: 150px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 150px;line-height: 40px">
			<div style="height: 40px;">
				<label><spring:message code="amazon_order_tips3"/>：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="purchaseDate" value="<fmt:formatDate value="${amazonOrder.purchaseDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="lastUpdateDate" value="<fmt:formatDate value="${amazonOrder.lastUpdateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<spring:message code="amazon_order_form4"/>：<select name="salesChannel" id="country" style="width: 120px">
						<option value="" ${amazonOrder.salesChannel eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${fn:endsWith(amazonOrder.salesChannel,dic.value) ?'selected':''}  >${dic.label}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;
				&nbsp;&nbsp;<spring:message code="amazon_order_form6"/>：<select name="fulfillmentChannel" style="width: 120px" id="selectT">
						<option value=""><spring:message code='custom_event_all'/></option>
						<option value="AFN" ${amazonOrder.fulfillmentChannel eq 'AFN'?'selected':''}>AFN</option>
						<option value="MFN" ${amazonOrder.fulfillmentChannel eq 'MFN'?'selected':''}>MFN</option>
					</select>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<spring:message code="amazon_order_form35"/>：<select name="orderStatus" style="width: 120px" id="selectT">
								<option value=""><spring:message code='custom_event_all'/></option>
								<option value="PendingAvailability" ${amazonOrder.orderStatus eq 'PendingAvailability'?'selected':''}>PendingAvailability</option>
								<option value="Pending" ${amazonOrder.orderStatus eq 'Pending'?'selected':''}>Pending</option>
								<option value="Unshipped" ${amazonOrder.orderStatus eq 'Unshipped'?'selected':''}>Unshipped</option> 
								<option value="PartiallyShipped" ${amazonOrder.orderStatus eq 'PartiallyShipped'?'selected':''}>PartiallyShipped</option>
								<option value="Shipped" ${amazonOrder.orderStatus eq 'Shipped'?'selected':''}>Shipped</option>    
								<option value="Canceled" ${amazonOrder.orderStatus eq 'Canceled'?'selected':''}>Canceled</option>
								<option value="Unfulfillable" ${amazonOrder.orderStatus eq 'Unfulfillable'?'selected':''}>Unfulfillable</option>
							</select>
				
				
			</div>
			<div style="height: 40px;">
			    <label><spring:message code="amazon_order_form_tab_tips4"/>：</label>
				 <form:input path="invoiceNo" htmlEscape="false" maxlength="20" class="input-small"/>
				  &nbsp;&nbsp;
				  <label><spring:message code="amazon_order_form10"/>：</label>
				 <form:input path="buyerEmail" htmlEscape="false" maxlength="50" class="input-small"/>
				 &nbsp;&nbsp;
				
				 <label><spring:message code="amazon_order_form1"/>：</label>
				 <form:input path="amazonOrderId" htmlEscape="false" maxlength="50" class="input-small"/>
				
			</div>
			<div style="height: 40px;">
			     &nbsp;&nbsp;&nbsp;
				 <label>Sku：</label>
				 <form:input path="sellerOrderId" htmlEscape="false" maxlength="50" class="input-small"/>
				 &nbsp;&nbsp;
			     <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			     &nbsp;&nbsp;
			     <shiro:hasPermission name="amazoninfo:order:emailView">
			       <select style="width:120px" name="productName">
			            <option value="">All</option>	
						<c:forEach items="${nameList}" var="product">
							<option value="${product.name}">${product.name}</option>	
						</c:forEach>
				    </select>
			        
			         <div class="btn-group">
					 <button type="button" class="btn btn-primary">Export Email&Phone</button>
					 <button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
					     <span class="caret"></span>
					     <span class="sr-only"></span>
					 </button>
					 <ul class="dropdown-menu" >
				 		<li><a id="expPhone">All</a></li>
				 		<li><a id="expPhone3">Amazon CSV</a></li>
				 		<li><a id="expPhone4">Ebay CSV</a></li>
				 		<li><a id="expPhone2">UserByProductName</a></li>
					 </ul>
				</div>	
			   </shiro:hasPermission> 
			   <shiro:hasPermission name="psi:product:productGroupCustomerEdit">
			       <a href="#updateExcel" role="button" class="btn btn-info" data-toggle="modal" id="uploadFile"><spring:message code="sys_but_upload"/></a>   				
			   </shiro:hasPermission>
			</div>
			<div style="height: 40px;">
				<%-- &nbsp;&nbsp;Shipped To Non-national Platform:<input type="checkbox" id="shipped" ${not empty amazonOrder.invoiceFlag?'checked':''}/> --%>
				&nbsp;&nbsp;IsBusinessOrder:<input type="checkbox" id="isBusinessOrder" ${not empty amazonOrder.isBusinessOrder?'checked':''}/>
				<form:hidden path="invoiceFlag" id="shippedVal" />
				<form:hidden path="isBusinessOrder" id="isBusinessOrderVal" />
				&nbsp;&nbsp;&nbsp;&nbsp;Shipped To Country :&nbsp;&nbsp;
				 <form:input id="selectCountry" path="shipServiceLevel" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;&nbsp;
				<div class="btn-group">
					 <button type="button" class="btn btn-primary"><spring:message code="sys_but_export_shipped"/></button>
					 <button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
					     <span class="caret"></span>
					     <span class="sr-only"></span>
					 </button>
					 <ul class="dropdown-menu" >
				 		<li><a class="expByExcel">Excel</a></li>
				 		<li><a class="expByCsv">CSV</a></li>
					 </ul>
				</div>	
				&nbsp;&nbsp;
				<div class="btn-group">
								 <button type="button" class="btn">Export Sales Summary</button>
								 <button type="button" class="btn dropdown-toggle"  data-toggle="dropdown">
								     <span class="caret"></span>
								     <span class="sr-only"></span>
								 </button>
								 <ul class="dropdown-menu" >
							 		<li><a class="totalCsv">EU_CSV</a></li>
							 		<li><a class="totalCsv">EU-FilterRefundVat_CSV</a></li>
							 		<li><a class="totalCsv">US_CSV</a></li>
							 		<li><a class="totalCsv">JP_CSV</a></li>
							 		<li><a class="totalCsv">CA_CSV</a></li>
								 </ul>
				</div>	
				
			</div>
		</div>
	</form:form>
	<div class="alert alert-info"><strong><spring:message code="amazon_order_tips2"/></strong></div>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		           <th style="width: 50px">ID</th>
				   <th style="width: 60px"><spring:message code="amazon_order_form_tab_tips4"/></th>	
				   <th style="width: 50px"><spring:message code="amazon_order_form4"/></th>	
				   <th style="width: 150px"><spring:message code="amazon_order_form1"/></th>
				   <th style="width: 100px"><spring:message code="psi_inventory_order_date"/></th>
				   <th style="width: 50px"><spring:message code="amazon_order_form5"/></th>
				   <th style="width: 150px"><spring:message code="amazon_order_form9"/></th>
				   <th style="width: 100px"><spring:message code="amazon_order_form10"/></th> 
				   <th style="width: 50px"><spring:message code="amazon_order_form35"/></th> 
				   <th><spring:message code="sys_label_tips_operate"/></th>
				   </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="amazonOrder">
			<tr>
			    <td rowspan="2" style="text-align: center;vertical-align: middle;"><b>${amazonOrder.id}</b></td>
				<td rowspan="2" style="text-align: center;vertical-align: middle;"><b><c:if test='${not empty amazonOrder.invoiceNo}'>${amazonOrder.invoiceNo}</c:if></b></td>
				<td>${amazonOrder.country} ${amazonOrder.accountName}</td>
				<td><a href="${ctx}/amazoninfo/order/form?id=${amazonOrder.id}">${amazonOrder.amazonOrderId}</a></td>
				<td><fmt:formatDate pattern="yyyy-M-d H:mm" value="${amazonOrder.purchaseDate}"/></td>
				<td>${amazonOrder.orderTotal}</td>
				<td>${amazonOrder.buyerName}</td>
				<td><a href="${ctx}/custom/sendEmail/form?sendEmail=${amazonOrder.buyerEmail}" >${amazonOrder.buyerEmail}</a></td>
				<td>${amazonOrder.orderStatus}</td>
				<td rowspan="2" style="text-align: center;vertical-align: middle;">
				&nbsp;&nbsp;<a href="${ctx}/amazoninfo/order/form?id=${amazonOrder.id}"><spring:message code="sys_but_view"/></a>
				</td>
			</tr>
			<tr>
				<td colspan="7">
				<c:if test="${not empty amazonOrder.orderChannel}">
							<spring:message code="amazon_order_form_tips9" />：${amazonOrder.orderChannel}
							<br/>
					</c:if>
				<spring:message code="amazon_order_form_tab_tips6"/>：<br/>
					<c:forEach items="${amazonOrder.items}" var="item">
						Asin:<b style="font-size: 14px">${item.asin}</b>;Sku:<b style="font-size: 14px">${item.sellersku}</b>;<spring:message code="amazon_order_form23"/>:${item.quantityOrdered};<spring:message code="amazon_order_form24"/>:${item.itemPrice}<br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	 <div id="updateExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm"  method="post" action="${ctx}/amazoninfo/amazonTestOrReplace/upload">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel"><spring:message code="psi_transport_upload"/> </h3>
						  </div>
						  <div class="modal-body">
							<label>:</label>
							<input id="uploadFileName" name="uploadFile" type="file" />
						  </div>
						   <div class="modal-footer">
						   <button class="btn btn-primary"  type="button" id="uploadTypeFile"><spring:message code="sys_but_upload"/></button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
			 </div>
	<div class="pagination">${page}</div>
</body>
</html>
