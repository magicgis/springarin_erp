<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购付款管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">查看采购付款</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="purchasePayment" action="${ctx}/psi/purchasePayment/editSave" method="post" class="form-horizontal">
		 <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:28%">
				<label class="control-label" style="width:100px"><b>供应商</b>:</label>
				<div class="controls" style="margin-left:120px">
				<span>
					<input type='text' style="width:95%"  name="supplier.name" readonly value="${purchasePayment.supplier.nikename} (${purchasePayment.supplier.payMark})"/>
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:35%" >
				<label class="control-label" style="width:100px"><b>需要支付金额</b>:</label>
				<div class="controls" style="margin-left:120px" >
				<input type='text' style="width:80%" readonly value="${purchasePayment.paymentAmountTotal}"/> 
				</div>
			</div>
			<div class="control-group"  style="float:left;width:35%" >
				<label class="control-label" style="width:100px"><b>实际支付金额</b>:</label>
				<div class="controls" style="margin-left:120px" >
				<input type='text' style="width:80%" readonly value="${purchasePayment.realPaymentAmount}"/> 
				</div>
			</div>  
		</div>
		<div style="float:left;width:100%;">
			<div class="control-group"  style="float:left;width:98%" >
				<label class="control-label" style="width:100px"><b>供应商账号</b>:</label>
				<div class="controls" style="margin-left:120px" >
				${purchasePayment.account}
				</div>
			</div>
		</div>
		<div style="float:left;width:100%;">
			<div class="control-group">
				<label class="control-label" style="width:98px"><b>备注:</b></label>
				<div class="controls" style="margin-left:120px">
					<textarea  readonly="readonly"  maxlength="255" style="height:50px;width:98%" name="remark" >${purchasePayment.remark}</textarea>
				</div>
			</div>
		</div>
		<div style="float:left;width:100%;">
			<div class="control-group">
				<label class="control-label" style="width:98px"><b>水单:</b></label>
				<div class="controls" style="margin-left:120px">
					<c:forEach items="${fn:split(purchasePayment.attchmentPath,',')}" var="att" varStatus="i" >
						<a target="_blank" href="<c:url value='/data/site${att}'/>" >查看水单${i.index+1} &nbsp;&nbsp;</a>
					</c:forEach>
				</div>
			</div>
		</div>
		
		
				
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 10%">付款类型</th>
				   <th style="width: 30%">单号</th>
				   <th style="width: 10%">本次付款金额</th>
				   <th style="width: 30%">备注</th>
				   
			</tr>
		</thead>
		<tbody>
			<c:if test="${not empty purchasePayment.items}" >
			<c:forEach items="${purchasePayment.items}" var="item" >
				<tr>
					<td>
					<input type="hidden" name="id" value="${item.id}" />
						<c:if test="${item.paymentType eq '0'}" >定金	</c:if>
						<c:if test="${item.paymentType eq '1'}" >尾款	</c:if>
					</td>
					<td>
					<span id="masters">
					<c:if test="${item.paymentType eq '0'}">
					<a target="_blank" href="${ctx}/psi/purchaseOrder/view?id=${item.order.id}">${item.billNo}</a>
					</c:if>
					<c:if test="${item.paymentType eq '1'}">
					<a target="_blank" href="${ctx}/psi/psiLadingBill/view?id=${item.ladingBill.id}">${item.billNo}</a>&nbsp;&nbsp;&nbsp;&nbsp;
					(${fn:toUpperCase(item.ladingBillItem.countryCode eq 'com' ?'us':item.ladingBillItem.countryCode)}平台&nbsp;&nbsp;${item.ladingBillItem.quantityLading}个&nbsp;&nbsp;单价：${item.ladingBillItem.itemPrice}&nbsp;&nbsp;定金比例：${item.ladingBillItem.deposit}&nbsp;&nbsp;${item.ladingBillItem.productName})
					</c:if>
						
					</span></td>
					<td ><input type="text" name="paymentAmount"  readonly="readonly" value="${item.paymentAmount}" style="width: 90%"/></td>
				    <td ><input type="text" name="remark"  readonly="readonly" style="width: 90%" value="${item.remark}"/></td>
				</tr>
			</c:forEach>
			</c:if>
		</tbody>
	</table>
		
			<c:if test="${not empty adjusts}">
			<div style="float: left"><blockquote><p style="font-size: 14px">额外付款项信息</p></blockquote></div>
				<table id="adjustTable" class="table table-striped table-bordered table-condensed" >
					<thead>
						<tr>
						   <th style="width: 10%">选项</th>
						   <th style="width: 20%">调整原由</th>
						   <th style="width: 10%">金额</th>
						   <th style="width: 10%">货币类型</th>
						   <th style="width: 40%">备注</th>
						</tr>
					</thead>
					<tbody>	
					<c:forEach items="${adjusts}" var="adjust">
						<tr>
							<td><input type="checkbox" class="isPayment" checked="checked" disabled="disabled"/><input type="hidden" name="id" value="${adjust.id}"/></td>
							<td>${adjust.subject}</td>
							<td><input type="text" readonly="readonly" name="adjustAmount" style="width:90%" value="${adjust.adjustAmount}" /></td>
							<td><input type="text" readonly="readonly" style="width:90%" value="${adjust.currency}" /></td>
							<td>${adjust.remark}</td>
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
