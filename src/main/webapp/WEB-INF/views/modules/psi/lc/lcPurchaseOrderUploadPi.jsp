<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单管理</title>
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
					loading('正在提交，请稍等...');
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("Entered incorrectly, please correct.");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		
		
		function goEdit(orderId){
			var params = {};
			params.id = orderId;
			window.location.href = "${ctx}/psi/lcPurchaseOrder/edit?"+$.param(params);
		}
		
		
	</script>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/lcPurchaseOrder/">采购订单列表</a></li>
		<li class="active"><a href="#">上传PI</a></li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="purchaseOrder" action="${ctx}/psi/lcPurchaseOrder/uploadPiSave" method="post" class="form-horizontal" enctype="multipart/form-data" >
		<input type="hidden" name="id" value="${purchaseOrder.id}" />
	    <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label"><b>供应商</b>:</label>
				<div class="controls" >
				<input type="text" readonly  style="width:60%"   value="${purchaseOrder.supplier.nikename}"/>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>定金</b>:</label>
				<div class="controls" >
					<div class="input-prepend input-append">
						<input  type="text" readonly class="number required" style="width:40%" name="deposit" value="${purchaseOrder.deposit}" /><span class="add-on">%</span>
					</div>
				</div>
			</div>
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<label class="control-label"><b>收货仓库</b>:</label>
				<div class="controls">
						${purchaseOrder.offlineSta eq '0' ?'否':'是'}
				</div>
			</div>
			
			
		</div>	
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>跟单员</b>:</label>
				<div class="controls">
				<input type="text" readonly  style="width:60%" name="merchandiser.name" id="merchandiser.name" value="${purchaseOrder.merchandiser.name}"/>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>下单日期</b>:</label>
				<div class="controls">
					<input  type="text" readonly="readonly" class="Wdate required"  style="width:60%" value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd" />" />
				</div>
			</div>
			
			
			
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<label class="control-label"><b>货币类型</b>:</label>
				<div class="controls">
				<input type="text" readonly  style="width:60%"  value="${purchaseOrder.currencyType}" >
				</div>
			</div>
			
		</div>
		
		
		
		
		<div style="float:left;width:100%">
			 <div style="float: left"> <blockquote><p style="font-size: 14px">产品信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">产品名称</th>
				   <th style="width: 5%">装箱数量</th>
				   <th style="width: 10%">预计交货</th>
				   <th style="width: 10%">颜色</th>
				   <th style="width: 10%">国家</th>
				   <th style="width: 6%">总数量</th>
				   <th style="width: 6%">线下数</th>
				   <th style="width: 6%">单价</th>
				   <th style="width: 15%">备注</th>
				   
			</tr>
		</thead>
		<c:if test="${not empty purchaseOrder.items}" >
			<c:forEach items="${purchaseOrder.items}" var="item" >
				<tbody>
					<tr>
						<td class="product">
						<input type="text" readonly value="${item.product.name}"/>
						</td>
						<td ><input type="text" readonly="readonly" maxlength="11" style="width: 80%" class="zhuangxiangno" value="${item.product.packQuantity}"/></td>
						<td><input  style="width: 80%" readonly="readonly"  type="text"  class="Wdate required"  name="deliveryDate"   id="deliveryDate"  pattern="yyyy-MM-dd" value="<fmt:formatDate value="${item.deliveryDate}" pattern="yyyy-MM-dd"/>"/> </td>
						<td >
						<input  style="width: 80%" type="text" readonly  name="colorCode" id="colorCode" value="${item.colorCode}">
						</td>
						<td >
						<input type="text" readonly  name="countryCode" id="countryCode" value="${fns:getDictLabel(item.countryCode, 'platform', '')}" style="width:80%">
						</td>
						<td > <input type="text" readonly style="width: 80%" name="quantityOrdered" class="number" value="${item.quantityOrdered}"/></td>
						<td > <input type="text" readonly style="width: 80%" name="quantityOffOrdered" class="number" value="${item.quantityOffOrdered}"/></td>
						<td ><input type="text" readonly style="width: 80%" name="itemPrice"  value="${item.itemPrice}"/></td>
						<td ><input type="text" readonly style="width: 80%" name="remark" value="${item.remark}"/></td>
					</tr>
				</tbody>
			</c:forEach>
		</c:if>
		
	</table>
	
		<div style="float:left;width:100%">
		  <blockquote>
			<p style="font-size: 14px">上传PI</p>
		</blockquote>
			<div class="control-group" style="float:left;width:100%">
				<label class="control-label"></label>
				<div class="controls">
				<input name="piFile" type="file" id="myfileupload" class="required"/>
				</div>
			</div>
		</div>
		
		
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="上传PI"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
