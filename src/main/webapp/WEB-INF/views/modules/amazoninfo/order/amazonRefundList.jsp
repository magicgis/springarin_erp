<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊退款</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<meta http-equiv="refresh" content="300"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$("a[rel='popover']").popover({trigger:'hover',placement:'left'});	
			
			 $(".countryHref").click(function(){
					$("input[name='country']").val($(this).attr("key"));
					$("#searchForm").submit();
			});
			 
			$("#refundState,#selectM").change(function(){
				$("#searchForm").submit();
			});
			 
			$("#export").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/refund/export");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/refund/");
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
    <li class="${empty amazonRefund.country?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${amazonRefund.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<form:form id="searchForm"  action="${ctx}/amazoninfo/refund/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 70px;line-height:40px">
			<div>
				<label>Create Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${amazonRefund.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${amazonRefund.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;
				<label>Checked：</label><select name="refundState" style="width:100px" id="refundState">
						<option value=""><spring:message code='custom_event_all'/></option>
						<option value="0" ${'0' eq amazonRefund.refundState?'selected':''}>Waiting for audit</option>
						<option value="1" ${'1' eq amazonRefund.refundState?'selected':''}>Audited</option>
						<option value="2" ${'2' eq amazonRefund.refundState?'selected':''}>Cancel</option>
				</select>&nbsp;&nbsp;
				
				<label>CreateUser：</label><select name="createUser.id" style="width: 120px" id="selectM">
						<option value=""><spring:message code='custom_event_all'/></option>
						<c:forEach items="${all}" var="user">
							<option value="${user.id}" ${amazonRefund.createUser.id eq user.id?'selected':''} >${user.name}</option>
						</c:forEach>		
					</select>&nbsp;&nbsp;
				<label>Reviewer：</label><select name="operUser.id" style="width: 120px" id="selectM">
						<option value=""><spring:message code='custom_event_all'/></option>
						<c:forEach items="${all}" var="user">
							<option value="${user.id}" ${amazonRefund.operUser.id eq user.id?'selected':''} >${user.name}</option>
						</c:forEach>		
					</select>
				<br/>
				<label>OrderId：</label><input name="amazonOrderId" value="${amazonRefund.amazonOrderId }"  class="input-middle"/>&nbsp;&nbsp;
				<label>ProductName：</label><input name="isTax" value="${amazonRefund.isTax }"  class="input-middle"/>
				
				<input type="hidden"  name="country" id="country" value="${amazonRefund.country }"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search"/>
				<input id="export" class="btn btn-primary" type="button" value="Export"/><br/>
						
			</div> 
		</div>
	</form:form>
	
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 15px">NO.</th>
				<th style="width: 60px">Country</th>
				<!-- <th style="width: 80px">退款类型</th> -->
				<th style="width: 150px">Order No</th>
				<th style="width: 80px">Order Total</th>
				<th style="width: 80px">Refund Money</th>
				<th style="width: 60px">Create User</th>
				<th style="width: 100px">Create Date</th>
				<th style="width: 120px">State</th>
				<th style="width: 100px">Reviewer</th>
				<th style="width: 100px">Review State</th>
				<!-- <th style="width: 120px">结果摘要</th> -->
				<th>Result File</th>
				<th>Operate</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="refund" varStatus="i">
			<tr>
			<td rowspan="${fn:length(refund.items)>0?2:1}" style="text-align: center;vertical-align: middle;">${refund.id}</td>
			<td rowspan="${fn:length(refund.items)>0?2:1}" style="text-align: center;vertical-align: middle;">${fns:getDictLabel(refund.country,'platform','')}<br/>${refund.accountName }</td>
		<%-- 	<td rowspan="2" style="vertical-align: middle;"><b>${refund.items[0].refundType}</b></td> --%>
			<td><a target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${refund.amazonOrderId }">${refund.amazonOrderId }</a></td>
			<td>${refund.orderTotal }</td>
			<td>${refund.refundTotal }</td>
			<td>${refund.createUser.name}</td>
			<td><fmt:formatDate value="${refund.createDate}" pattern="yyyy-MM-dd HH:mm" /></td>
			<td>${refund.stateStr}</td>
			<td>${refund.operUser.name}</td>
			<td>${refund.refundStateStr}</td>
			<%-- <td><a rel="popover" data-content="${refund.result}">${fn:substring(refund.result,0,100)}</a></td> --%>
			<td><c:if test="${refund.state eq '3'}"><a href="${ctx}/amazoninfo/refund/download?fileName=${refund.resultFile}/result.xml">Download</a></c:if></td>
			<td>
			    <c:if test="${fn:contains('de,fr,it,es,uk',refund.country)}">
			       <a  class="btn" href="https://sellercentral.amazon.de/gp/orders-v2/details/?orderID=${refund.amazonOrderId}" target='_blank'>AMZ-View</a>
			    </c:if>
			    <c:if test="${'com' eq refund.country}">
			       <a  class="btn" href="https://sellercentral.amazon.com/gp/orders-v2/details/?orderID=${refund.amazonOrderId}" target='_blank'>AMZ-View</a>
			    </c:if>
			    <c:if test="${'ca' eq refund.country}">
			       <a  class="btn" href="https://sellercentral.amazon.de/gp/orders-v2/details/?orderID=${refund.amazonOrderId}" target='_blank'>AMZ-View</a>
			    </c:if>
			     <c:if test="${'jp' eq refund.country}">
			       <a  class="btn" href="https://sellercentral.amazon.co.jp/gp/orders-v2/details/?orderID=${refund.amazonOrderId}" target='_blank'>AMZ-View</a>
			    </c:if>
			      <c:if test="${'mx' eq refund.country}">
			       <a class="btn" href="https://sellercentral.amazon.com.mx/gp/orders-v2/details/?orderID=${refund.amazonOrderId}" target='_blank'>AMZ-View</a>
			    </c:if>
			    
			  <c:if test="${'0' eq refund.refundState}">
			    <shiro:hasPermission name="amazoninfo:refund:all">
			       <c:if test="${'0' ne refund.isTax}">
			          <a class="btn btn-warning" href="${ctx}/amazoninfo/refund/isChecked?id=${refund.id}&checkCountry=${amazonRefund.country}" onclick="return confirmx('Are you sure pass？', this.href)">Pass</a>
			       </c:if>
			       <c:if test="${'0' eq refund.isTax}">
			          <a class="btn btn-warning" href="${ctx}/amazoninfo/refund/isChecked?id=${refund.id}&checkCountry=${amazonRefund.country}" onclick="return confirmx('Are you sure pass,refund successfully sent bill？', this.href)">Pass</a>
			       </c:if>
			        
			        <a class="btn btn-warning" href="${ctx}/amazoninfo/refund/cancel?id=${refund.id}&checkCountry=${amazonRefund.country}" onclick="return confirmx('Are you sure cancel？', this.href)">Cancel</a>
			    </shiro:hasPermission>
			    <shiro:lacksPermission name="amazoninfo:refund:all">  
		            <shiro:hasPermission name="amazoninfo:refund:${amazoninfo.country}">
						  
						   <c:if test="${'0' ne refund.isTax}">
					          <a class="btn btn-warning" href="${ctx}/amazoninfo/refund/isChecked?id=${refund.id}&checkCountry=${amazonRefund.country}" onclick="return confirmx('Are you sure pass？', this.href)">Pass</a>
					       </c:if>
					       <c:if test="${'0' eq refund.isTax}">
					          <a class="btn btn-warning" href="${ctx}/amazoninfo/refund/isChecked?id=${refund.id}&checkCountry=${amazonRefund.country}" onclick="return confirmx('Are you sure pass,refund successfully sent bill？', this.href)">Pass</a>
					       </c:if>
	                      <a class="btn btn-warning" href="${ctx}/amazoninfo/refund/cancel?id=${refund.id}&checkCountry=${amazonRefund.country}" onclick="return confirmx('Are you sure cancel？', this.href)">Cancel</a>
	                </shiro:hasPermission>
				</shiro:lacksPermission>
			  </c:if>
			  <c:if test="${refund.state eq '4'}">
			        <shiro:hasPermission name="amazoninfo:refund:all">
			               <c:if test="${'0' ne refund.isTax}">
					          <a class="btn btn-warning" href="${ctx}/amazoninfo/refund/isChecked?id=${refund.id}&checkCountry=${amazonRefund.country}" onclick="return confirmx('Are you sure resubmit？', this.href)">Resubmit</a>
					       </c:if>
					       <c:if test="${'0' eq refund.isTax}">
					          <a class="btn btn-warning" href="${ctx}/amazoninfo/refund/isChecked?id=${refund.id}&checkCountry=${amazonRefund.country}" onclick="return confirmx('Are you sure resubmit,refund successfully sent bill？', this.href)">Resubmit</a>
					       </c:if>
					</shiro:hasPermission>
				    <shiro:lacksPermission name="amazoninfo:refund:all">  
				            <shiro:hasPermission name="amazoninfo:refund:${refund.country }">
							       <c:if test="${'0' ne refund.isTax}">
							          <a class="btn btn-warning" href="${ctx}/amazoninfo/refund/isChecked?id=${refund.id}&checkCountry=${amazonRefund.country}" onclick="return confirmx('Are you sure resubmit？', this.href)">Resubmit</a>
							       </c:if>
							       <c:if test="${'0' eq refund.isTax}">
							          <a class="btn btn-warning" href="${ctx}/amazoninfo/refund/isChecked?id=${refund.id}&checkCountry=${amazonRefund.country}" onclick="return confirmx('Are you sure resubmit,refund successfully sent bill？', this.href)">Resubmit</a>
							       </c:if>
									</shiro:hasPermission>
				    </shiro:lacksPermission>
			  </c:if> 
			</td>
			</tr>
			<c:if test="${fn:length(refund.items)>0}">
			<tr>
				<td colspan="10">
					<c:forEach items="${refund.items}" var="item">
						<a  href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}" target='_blank'>${item.productName}</a>;
						Type:${item.refundType};Money:${item.money};
						<c:if test="${not empty item.remark }"><b style="font-size: 16px">Reason:${item.remark };</b></c:if>
					    <br/>
					</c:forEach>
				</td>
			</tr>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
