<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品配件订单管理</title>
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
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/lcPsiPartsOrder/">配件订单列表</a></li>
		<li class="active"><a href="#">配件订单查看</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiPartsOrder" action="" method="post" class="form-horizontal">
		<input type="hidden" 	name="id" 		   			value="${psiPartsOrder.id}" />
		 <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					   <th style="width: 10%">配件订单号</th>
					   <th style="width: 8%">供应商</th>
					   <th style="width: 10%">定金比例</th>
					   <th style="width: 5%">货币类型</th>
					   <th style="width: 8%">采购时间</th>
					   <th style="width: 5%">确认人</th>
					   <th style="width: 8%">确认时间</th>
					   <th style="width: 8%">收货完成时间</th>
					   <shiro:hasPermission name="psi:product:viewPrice">
						   <th style="width: 5%">订单总额</th>
						   <th style="width: 5%">已支付定金</th>
					   </shiro:hasPermission>
					   <th style="width: 5%">状态</th>
				</tr>
			</thead>
				<tbody>
					<tr>
						<td>${psiPartsOrder.partsOrderNo}</td>
						<td>${psiPartsOrder.supplier.nikename}</td>
						<td>${psiPartsOrder.deposit}<span class="add-on">%</span></td>
						<td>${psiPartsOrder.currencyType}</td>
						<td><fmt:formatDate value="${psiPartsOrder.purchaseDate}" pattern="yyyy-MM-dd" /></td>
						<td>${psiPartsOrder.sureUser.name}</td>
						<td><fmt:formatDate value="${psiPartsOrder.sureDate}" pattern="yyyy-MM-dd" /></td>
						<td><fmt:formatDate value="${psiPartsOrder.receiveFinishedDate}" pattern="yyyy-MM-dd" /></td>
						<shiro:hasPermission name="psi:product:viewPrice">
							<td>${psiPartsOrder.totalAmount}</td>
							<td>${psiPartsOrder.depositAmount}</td>
						</shiro:hasPermission>
						<td>
							<c:if test="${psiPartsOrder.orderSta eq '0'}"><span class="label label-important">草稿</span></c:if>
							<c:if test="${psiPartsOrder.orderSta eq '1'}"><span class="label label-warning">生产</span></c:if>
							<c:if test="${psiPartsOrder.orderSta eq '3'}"><span class="label label-info">部分收货</span></c:if>
							<c:if test="${psiPartsOrder.orderSta eq '5'}"><span class="label" style="background-color:#00E3E3">已收货</span></c:if>
							<c:if test="${psiPartsOrder.orderSta eq '7'}"><span class="label  label-success">已完成</span></c:if>
							<c:if test="${psiPartsOrder.orderSta eq '8'}"><span class="label  label-inverse">已取消</span></c:if>
						</td>
						
					</tr>
			</tbody>
		</table>
	
		
		<div class="control-group"  style="float:left;width:100%">
			<label class="control-label" style="width:50px"><b>备注</b>:</label>
			<div class="controls" style="margin-left:60px">
				<textarea  maxlength="255" style="height:50px;width:98%" name="remark" readonly="readonly" >${psiPartsOrder.remark}</textarea>
			</div>
		</div>
		
		
		<div style="float:left;width:100%">
		 <blockquote style="float:left">
		 <div style="float: left"><p style="font-size: 14px">订单信息</p></div>
		</blockquote>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 30%">配件名称</th>
				   <th style="width: 10%">订单数量</th>
				   <th style="width: 10%">接收数量</th>
				   <th style="width: 10%">预接收数量</th>
				   <th style="width: 20%">备注</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${psiPartsOrder.items}" var="item">
				<tr>
					<td><input type="text" style="width: 80%"  readonly="readonly" value="${item.partsName}" />	</td>
					<td><input type="text" style="width: 80%"  readonly="readonly" value="${item.quantityOrdered}" /></td>
					<td><input type="text" style="width: 80%"  readonly="readonly" value="${item.quantityReceived}" /></td>
					<td><input type="text" style="width: 80%"  readonly="readonly" value="${item.quantityPreReceived}" /></td>
					<td><input type="text" style="width: 80%"  readonly="readonly" value="${item.remark}"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
		<c:if test="${fn:length(psiPartsOrder.ladingsMap)>0}" >
			<div style="float: left"><blockquote><p style="font-size: 14px">收货单信息</p></blockquote></div>
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						   <th style="width:20%">收货单号</th>
						   <th style="width: 20%">配件名字</th>
						   <th style="width: 10%">提单数量</th>
						   <th style="width: 15%">状态</th>
						   <th style="width: 15%">备注</th>
						   
					</tr>
				</thead>
						<tbody>
						<c:forEach items="${psiPartsOrder.ladingsMap}" var="itemEntry" >
						<c:forEach items="${itemEntry.value.items}" var="billItem" varStatus="i">
						 	  <tr>
						 	  <c:if test="${i.index==0}">
						 	  <td rowspan="${fn:length(itemEntry.value.items)}" style="text-align: center;vertical-align: middle;"><a href="${ctx}/psi/lcPsiPartsDelivery/${itemEntry.value.billSta=='0'?'sure':'view'}?id=${billItem.partsDelivery.id}">${billItem.partsDelivery.billNo}</a></td>
						 	  </c:if>
						 	  	<td>${billItem.partsName}</td>
								<td>${billItem.quantityLading}</td>
								<td>
								<c:if test="${billItem.partsDelivery.billSta eq '0'}"><span class="label label-important">申请</span></c:if>
								<c:if test="${billItem.partsDelivery.billSta eq '1'}"><span class="label  label-success">已确认</span></c:if>
								<c:if test="${billItem.partsDelivery.billSta eq '2'}"><span class="label  label-success">已取消</span></c:if>
								</td>
								<td>${billItem.remark}</td>	
						 	  </tr>
						</c:forEach>
						</c:forEach>
						</tbody>
				</table>
		</c:if>
			
	<c:if test="${fn:length(psiPartsOrder.paymentsMap)>0 }">
		<div style="float: left"><blockquote><p style="font-size: 14px">相关付款信息</p></blockquote></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				   <th style="width:15%">付款单号</th>
				   <th style="width:15%">状态</th>
				   <th style="width:15%">凭证</th>
				</tr>
			</thead>
				<tbody>
					<c:forEach items="${psiPartsOrder.paymentsMap}" var="pay" >
					 	  <tr>
					 	  <td><a href="${ctx}/psi/lcPsiPartsPayment/view?id=${pay.value.id}">${pay.value.paymentNo}</a></td>   
							<td>
								<c:if test="${pay.value.paymentSta eq '0'}"><span class="label  label-important">草稿</span></c:if>
								<c:if test="${pay.value.paymentSta eq '1'}"><span class="label  label-warning">申请</span></c:if>
								<c:if test="${pay.value.paymentSta eq '2'}"><span class="label  label-success">已付款</span></c:if>
								<c:if test="${pay.value.paymentSta eq '3'}"><span class="label  label-inverse">已取消</span></c:if>
							</td>
							<td><a target="_blank" href="<c:url value='/data/site${pay.value.attchmentPath}'/>">查看凭证</a></td>
					 	  </tr>
					</c:forEach>
					</tbody>
			
		</table>
	</c:if>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
