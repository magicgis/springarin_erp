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
		$(document).ready(function() {
			
			$("#inputForm").validate({
				submitHandler: function(form){
					top.$.jBox.confirm('您确定要确认付款吗','系统提示',function(v,h,f){
						if(v=='ok'){
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
		<li ><a href="${ctx}/psi/lcPurchasePayment">(理诚)采购付款列表</a></li>
		<li class="active"><a href="#">(理诚)财务审核</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/moneyView">(理诚)采购订单资金列表</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="purchasePayment" action="${ctx}/psi/lcPurchasePayment/reviewSave" method="post" class="form-horizontal" enctype="multipart/form-data" >
		<input type='hidden' name="id"               value="${purchasePayment.id}" />
		<input type='hidden' name="paymentSta"       value="${purchasePayment.paymentSta}" />
		 <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:70%;height:30px">
				<label class="control-label" style="width:100px"><b>供应商</b>:</label>
				<div class="controls" style="margin-left:120px">
				<span>
					${purchasePayment.supplier.name}
				</span>
				</div>
			</div>
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:100px"><b>总金额（${purchasePayment.currencyType}）</b>:</label>
				<div class="controls" style="margin-left:120px">
				<span>
					<input type="text"  class="canPaymentAmount" readonly="readonly"  value="${purchasePayment.canPaymentAmount}"/>
				</span>   
				</div>
			</div>
		 </div>
		 <div style="float:left;width:100%;">
			<div class="control-group"  style="float:left;width:100%" >
					<label class="control-label" style="width:98px"><b>供应商账号</b>:</label>
					<div class="controls" style="margin-left:120px">
					${accountMaps[purchasePayment.accountType]}
					</div>   
				</div>
		</div>
			
		<div class="control-group">
			<label class="control-label" style="width:98px"><b>备注:</b></label>
			<div class="controls" style="margin-left:120px">
				<textarea  maxlength="255" style="height:50px;width:98%" name="remark"  readonly="readonly" > ${purchasePayment.remark}</textarea>
			</div>
		</div>
				
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 10%">付款类型</th>
				   <th style="width: 15%">单号</th>
				   <th style="width: 15%">提示信息</th>
				   <th style="width: 10%">未付款金额</th>
				   <th style="width: 10%">本次付款金额</th>
				   <th style="width: 15%">备注</th>
				   
			</tr>
		</thead>
		<tbody>
			<c:if test="${not empty purchasePayment.items}" >
			<c:forEach items="${purchasePayment.items}" var="item" >
				<tr>
					<td>
					<input type="hidden" name="id" value="${item.id}" />
					<input type="hidden" name="paymentType" value="${item.paymentType}"/>
					<c:if test="${item.paymentType eq '0'}" >定金	</c:if>
					<c:if test="${item.paymentType eq '1'}" >尾款	</c:if>
					</td>
					<td ><span id="masters">
					<input type='text' readonly="readonly" name="billNo" value="${item.billNo }" />
					</span></td>
					<td>
						<c:if test="${item.paymentType eq '1'}">
							${fn:toUpperCase(item.ladingBillItem.countryCode eq 'com' ?'us':item.ladingBillItem.countryCode)}平台&nbsp;&nbsp;${item.ladingBillItem.quantityLading}个&nbsp;&nbsp;${item.ladingBillItem.productName}
						</c:if>
					</td>
					<td>
						<c:if test="${item.paymentType eq '1'}"><fmt:formatNumber value="${item.ladingBillItem.canPayAmount+item.paymentAmount}" pattern="0.##"/>  </c:if>
						<c:if test="${item.paymentType eq '0'}">${item.paymentAmount}</c:if>
					</td>
					<td><input type="text" name="paymentAmount" readonly="readonly" value="${item.paymentAmount}" style="width: 90%"/></td>
				    <td><input type="text" name="remark"  readonly="readonly"  style="width: 90%" value="${item.remark}"/></td>
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
		
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="确认"/>&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<a class="btn btn-inverse" href="${ctx}/psi/lcPurchasePayment/cancel?id=${purchasePayment.id}&paymentSta=5" onclick="return confirmx('取消该采购付款吗？', this.href)">取消</a>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
