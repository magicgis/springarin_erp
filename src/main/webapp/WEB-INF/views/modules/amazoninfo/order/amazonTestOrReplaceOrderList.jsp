<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="amazon_order_tab1"/></title>
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
			$("#selectT,#fulfillmentAction,#orderType,#flag").change(function(){
				$("#searchForm").submit();
			});
			
			$("#selectAdmin").change(function(){
				$("#searchForm").submit();
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
		    });
			
			
			$("#btnExport").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/export");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace");
			});
			
			
			$("#aboutMe").click(function(){
				if(this.checked){
					$("#aboutMeVal").val('${cuser.id}');
				}else{
					$("#aboutMeVal").val('');
				}
				$("#searchForm").submit();
			});
			
			
			$(".synchronizeOrder").click(function(){
				var tr = $(this).parent().parent().parent().parent().parent();
				top.$.jBox.confirm("Are you sure synchronize order？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						
						var params = {};
						params.id = tr.find(".id").val();
						
						top.$.jBox.tip("Please Waiting for 1 minutes！", 'loading',{timeout:10000});
						$.post("${ctx}/amazoninfo/amazonTestOrReplace/synchronizeOrder",$.param(params),function(date){
							 setTimeout("refreshForm()",1000*60);
					    }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$(".synchronizeMfnOrder").click(function(){
				var tr = $(this).parent().parent().parent().parent().parent();
				top.$.jBox.confirm("Are you sure synchronize mfn order？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						var mfnId= tr.find(".id").val();
						 $.post("${ctx}/amazoninfo/amazonTestOrReplace/synchronizeMfnOrder",{mfnId:mfnId},function(date){
							   if(date=='0'){
								   $.jBox.tip('synchronize failed');
								   $("#searchForm").submit();
							   }else{
								   $.jBox.tip('synchronize success');
								   $("#searchForm").submit();
							   }
							  
					     }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			
			$(".deleteMfnOrder").click(function(){
				var tr = $(this).parent().parent().parent().parent().parent();
				top.$.jBox.confirm("Are you sure delete mfn order？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						var mfnId= tr.find(".id").val();
						 $.post("${ctx}/amazoninfo/amazonTestOrReplace/isShipped",{id:mfnId},function(date){
							   if(date=='0'){
								   $.jBox.tip('Shipped Order can not be deleted');
							   }else{
								   window.location.href='${ctx}/amazoninfo/amazonTestOrReplace/deleteOrder?id='+mfnId;
							   }
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
			
			$("#printPackage").click(function(){
				var ids = $("input:checkbox[name='checkId']:checked");
				var country=$("#country").val();
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
				top.$.jBox.confirm("Are you sure shipping these data？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						   $("#printPackage").attr("disabled","disabled");
						   $.post("${ctx}/amazoninfo/amazonTestOrReplace/shipmentAllDraft",{shipmentIds:idsAll,country:country},function(date){
							   if(date=='0'){
								   $.jBox.tip('shipping success');
								   $("#searchForm").submit();
							   }else{
								   $.jBox.tip(date, 'error');
								   $('#printPackage').removeAttr("disabled");
							   }
					       }); 
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
		
		
		
	</script>
</head>
<body>
	 <ul class="nav nav-tabs">
	    <li class="${empty amazonOutboundOrder.country?'active':''}"><a class="countryHref"  key="">Total</a></li>	
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${amazonOutboundOrder.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<li><a class="countryHref" href="${ctx}/amazoninfo/amazonTestOrReplace/add" key="${dic.value}">Order Add</a></li>	
	</ul>
	<form:form id="searchForm" modelAttribute="amazonOutboundOrder" action="${ctx}/amazoninfo/amazonTestOrReplace" method="post" class="breadcrumb form-search" cssStyle="height:120px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input type='hidden' name='country' id='country' value='${amazonOutboundOrder.country }'/>
		<div style="height: 40px;line-height: 40px">
			<div style="height: 40px;">
				<label><spring:message code="amazon_order_tips3"/>：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${amazonOutboundOrder.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="lastUpdateDate" value="<fmt:formatDate value="${amazonOutboundOrder.lastUpdateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;
				<spring:message code="amazon_order_form35"/>：<select name="orderStatus" style="width: 150px" id="selectT">
								<option value=""><spring:message code='custom_event_all'/></option>
								<option value="Draft" ${amazonOutboundOrder.orderStatus eq 'Draft'?'selected':''}>Draft</option>
								<option value="Pending" ${amazonOutboundOrder.orderStatus eq 'Pending'?'selected':''}>Pending</option>
							    <option value="Planning" ${amazonOutboundOrder.orderStatus eq 'Planning'?'selected':''}>Planning</option>
								<option value="Processing" ${amazonOutboundOrder.orderStatus eq 'Processing'?'selected':''}>Processing</option>
								<option value="Received" ${amazonOutboundOrder.orderStatus eq 'Received'?'selected':''}>Received</option>
								<option value="Complete" ${amazonOutboundOrder.orderStatus eq 'Complete'?'selected':''}>Complete</option>
								<option value="Cancelled" ${amazonOutboundOrder.orderStatus eq 'Cancelled'?'selected':''}>Cancelled</option>
							</select>
			
			    &nbsp;<input id="printPackage" class="btn btn-primary" type="button"  value="AMZ-Fulfillment"/>	
			    &nbsp;
			    <input type="checkbox" id="aboutMe" ${not empty amazonOutboundOrder.createUser.id?'checked':''}/>Related to me
				<input type="hidden" name="createUser.id" id="aboutMeVal" value="${not empty amazonOutboundOrder.createUser.id?cuser.id:''}"><br/>			
							
				<label>OrderType：</label><select name="flag" style="width:100px" id="flag">
								<option value=""><spring:message code='custom_event_all'/></option>
								<option value="Support" ${amazonOutboundOrder.flag eq 'Support'?'selected':''}>Support</option>
							    <option value="Review" ${amazonOutboundOrder.flag eq 'Review'?'selected':''}>Review</option>
							    <option value="Marketing" ${amazonOutboundOrder.flag eq 'Marketing'?'selected':''}>Marketing</option>
								<option value="Ebay" ${amazonOutboundOrder.flag eq 'Ebay'?'selected':''}>Ebay</option>
								<option value="Website" ${amazonOutboundOrder.flag eq 'Website'?'selected':''}>Website</option>
								<option value="Offline" ${amazonOutboundOrder.flag eq 'Offline'?'selected':''}>Offline</option>
								<option value="AmzMfn" ${amazonOutboundOrder.flag eq 'AmzMfn'?'selected':''}>AmzMfn</option>
							</select>
							&nbsp;
				<label>Checked：</label><select name="fulfillmentAction" style="width:100px" id="fulfillmentAction">
								<option value=""><spring:message code='custom_event_all'/></option>
								<option value="Ship" ${amazonOutboundOrder.fulfillmentAction eq 'Ship'?'selected':''}>Checked</option>
							    <option value="Hold" ${amazonOutboundOrder.fulfillmentAction eq 'Hold'?'selected':''}>Non-Checked</option>
								
							</select>
							&nbsp;
				<label>Fulfillment：</label><select name="orderType" id="orderType" style="width:100px" >
								<option value=""><spring:message code='custom_event_all'/></option>
								<option value="0" ${amazonOutboundOrder.orderType eq '0'?'selected':''}>Amazon</option>
							    <option value="1" ${amazonOutboundOrder.orderType eq '1'?'selected':''}>Inateck</option>
								
							</select>
							&nbsp;					
							<br/>
				<label>SellerOrderId/Email/BuyerName/Sku/Product：</label><input name="sellerOrderId"  class="input-xlarge" value='${amazonOutboundOrder.sellerOrderId }'/>
					&nbsp;
			    <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
					&nbsp;
					<input id="btnExport" class="btn btn-primary" type="button" value="export"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		           <th style="width: 3%"><input type="checkBox" id="checkall"></th>
				   <th style="width: 20px">NO.</th>	
				   <th style="width: 70px">Country</th>	
				   <th style="width: 140px">SellerOrderId</th>
				    <th style="width: 80px">OrderStatu</th> 
				   <th style="width: 50px"><spring:message code="amazon_order_form_tips9" /></th>
				   <th style="width: 100px"><spring:message code="amazon_order_form2"/></th>
				   <th style="width: 100px"><spring:message code="amazon_order_form9"/></th>
				   <th style="width: 150px"><spring:message code="amazon_order_form10"/></th> 
				   <th style="width: 80px">ShippingSpeed</th> 
				  
				   <th style="width: 80px">Checked</th> 
				   <!-- <th style="width: 70px">审核人</th>  -->
				   <th><spring:message code="sys_label_tips_operate"/></th>
				   </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="order">
			<tr style="${not empty order.checkUser&&empty order.checkDate&&order.orderStatus ne 'Canceled'&&order.orderStatus ne 'CANCELLED'?'background-color:#ff9900;':''}" >
			    <td rowspan="2" style="text-align: left;">
					<c:if test="${order.orderStatus eq 'Draft'&&'管理员' eq order.createUser.name}">
					    <input type="checkBox" class="chebox"  name="checkId" value="${order.id}"/>
					</c:if> 
				</td>
				<td rowspan="2" style="text-align: center;vertical-align: middle;"><b>${order.id}</b><input class="id" type="hidden" value="${order.id}" /></td>
				<td>${fns:getDictLabel(order.country,'platform','')}<br/>${order.accountName }
				</td>
				<td><a target='_blank' href="${ctx}/amazoninfo/amazonTestOrReplace/view?id=${order.id}">${order.sellerOrderId}</a></td>
				<td>
				  
				  <c:choose>
					<c:when test="${order.orderStatus eq 'Draft'&&fns:startsWith(order.sellerOrderId,'MFN-')}">
						PENDING
					</c:when>
					<c:otherwise>
						${order.orderStatus}
					</c:otherwise>
				</c:choose> 
			
			
				</td>
				<td>${order.createUser.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${order.createDate}"/></td>
				<td>${order.buyerName}</td>
				<td><a href="${ctx}/custom/sendEmail/form?sendEmail=${order.buyerEmail}" >${order.buyerEmail}</a></td>
				<td>${order.shippingSpeedCategory}</td>
				
				<td>${'Hold' eq order.fulfillmentAction?'Non-Checked':'Checked'}</td>
				<%-- <td>${order.checkUser.name}</td> --%>
				<td style="text-align: left;vertical-align: left;">
				  
				    <div class="btn-group">
								   <button type="button" class="btn btn-small"><spring:message code="sys_but_edit"/></button>
								   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
								      <span class="caret"></span>
								      <span class="sr-only"></span>
								   </button>
								   <ul class="dropdown-menu" >
								        <shiro:hasPermission name="amazoninfo:fulfillment:${order.country}">
								             <c:if test="${!fns:startsWith(order.sellerOrderId,'MFN-')&&fns:getUser().name eq order.createUser.name}"> 
											    <li> <a onclick="return confirm('Are you sure cancel?', this.href)" href="${ctx}/amazoninfo/amazonTestOrReplace/cancelOrder?id=${order.id}">Cancel</a></li>
											</c:if> 
								        </shiro:hasPermission>
								        <c:if test="${'Hold' eq order.fulfillmentAction&&order.orderStatus ne 'Canceled'&&order.orderStatus ne 'CANCELLED'&&order.orderStatus ne 'COMPLETE'&& fns:getUser().name eq order.checkUser.name}"> 
											  <li><a onclick="return confirm('Are you sure shipping?', this.href)"href="${ctx}/amazoninfo/amazonTestOrReplace/updateFulfillmentAction?id=${order.id}">Ship</a> </li>
											  <li><a onclick="return confirm('Are you sure cancel?', this.href)" href="${ctx}/amazoninfo/amazonTestOrReplace/cancelOrder?id=${order.id}">Cancel</a></li>
										  </c:if>
								       
									  <c:if test="${order.orderStatus eq 'Draft'&&(fns:getUser().name eq order.createUser.name||'管理员' eq order.createUser.name)}">
									      <c:if test="${!fns:startsWith(order.sellerOrderId,'MFN-')}">
									        <li> <a href='${ctx}/amazoninfo/amazonTestOrReplace/add?id=${order.id}' target='_blank'>Ship</a></li>
									        <li> <a onclick="return confirm('Are you sure delete?', this.href)" href='${ctx}/amazoninfo/amazonTestOrReplace/deleteOrder?id=${order.id}' target='_blank'>Delete</a></li>
									      </c:if>
									  </c:if>
									   <c:if test="${fns:startsWith(order.sellerOrderId,'MFN-')&&order.orderStatus ne 'Canceled'&&order.orderStatus ne 'CANCELLED'&&fns:getUser().name eq order.createUser.name}">
									       <li> <a href="#" class="deleteMfnOrder">Delete</a></li>
									   </c:if>
									 
								   </ul>
				 </div>
				 <c:if test="${not empty order.amazonOrderId}">
					<a href='${order.urlLink }' class="btn btn-small" target='_blank'>Amz-View</a>
				</c:if>
				</td>
			</tr>
			<tr style="${not empty order.checkUser&&empty order.checkDate&&order.orderStatus ne 'Canceled'&&order.orderStatus ne 'CANCELLED'?'background-color:#ff9900;':''}">
				<td colspan="10">
				<c:if test="${not empty order.remark}">
					Remark：${order.remark}
					<br/>
					<c:if test="${not empty order.checkUser}"> 
					CheckUser：${order.checkUser.name}<c:if test="${empty order.checkDate }"><b>&nbsp;&nbsp;(non-checked)</b></c:if>
					 <br/>
					</c:if>
				</c:if>
				<spring:message code="amazon_order_form_tab_tips6"/>：<br/>
					<c:forEach items="${order.items}" var="item">
						Product:${item.name};Asin:<b style="font-size: 14px">${item.asin}</b>;Sku:<b style="font-size: 14px">${item.sellersku}</b>;<spring:message code="amazon_order_form23"/>:${item.quantityOrdered};<br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
