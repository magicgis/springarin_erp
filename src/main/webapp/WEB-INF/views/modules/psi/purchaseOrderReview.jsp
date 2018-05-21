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
			new tabTableInput("inputForm","text");
			$("#inputForm").validate({
				submitHandler: function(form){
						top.$.jBox.confirm('审核通过会自动给供应商发送PDF,请确认要审核通过吗','系统提示',function(v,h,f){
							if(v=='ok'){
								$("#btnSubmit").attr("disabled","disabled");
								form.submit();
							}
							},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				},
				errorContainer: "#messageBox",
					errorPlacement: function(error, element) {
						$("#messageBox").text("输入有误，请先更正。");
						error.appendTo($("#errorsShow"));
				}
			});
		});
		
	</script>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/purchaseOrder/">采购订单列表</a></li>
		<li class="active"><a href="#">采购订单审核</a></li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/reviewSave" method="post" class="form-horizontal" >
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
				<label class="control-label"><b>线下订单</b>:</label>
				<div class="controls">
					${purchaseOrder.offlineSta eq '0' ?'不包含':'包含'}
				</div>
			</div>
			
			
		</div>	
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>跟单员</b>:</label>
				<div class="controls">
				<input type="text" readonly  style="width:60%" value="${purchaseOrder.merchandiser.name}"/>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>下单日期</b>:</label>
				<div class="controls">
					<input  type="text" readonly="readonly" name="purchaseDate" style="width:60%" value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd" />" />
				</div>
			</div>
			
			
			
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<label class="control-label"><b>货币类型</b>:</label>
				<div class="controls">
				<input type="text" readonly  name="currencyType" style="width:60%"  value="${purchaseOrder.currencyType}" >
				</div>
			</div>
			
		</div>
		
		<div style="float: left"><blockquote><p style="font-size: 14px">产品信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">产品名称</th>
				   <th style="width: 10%">装箱数量</th>
				   <th style="width: 15%">预计交货</th>
				   <th style="width: 10%">颜色</th>
				   <th style="width: 10%">国家</th>
				   <th style="width: 10%">总数量</th>
				   <th style="width: 10%">线下数</th>
				   <th style="width: 10%">单价</th>
				   <th style="width: 20%" >备注</th>
				   
			</tr>
		</thead>
		<c:if test="${not empty purchaseOrder.items}" >
			<c:forEach items="${purchaseOrder.showItems}" var="item" varStatus="i">
				<tbody>
					<tr>
						<td style="width: 20%" class="product" style="width: 90%">
						<input type="hidden" name="product.id" value="${item.product.id}"/>
						<input type="hidden" name="id" value="${item.id}" />
						<input type="hidden" name="quantityPreReceived" value="${item.quantityPreReceived}" />
						<input type="hidden" name="quantityReceived" 	value="${item.quantityReceived}" />
						<input type="hidden" name="quantityOffPreReceived" value="${item.quantityOffPreReceived}" />
						<input type="hidden" name="quantityOffReceived" 	value="${item.quantityOffReceived}" />
						<input type="hidden" name="quantityPayment" 	value="${item.quantityPayment}" />
						<input type="hidden" name="paymentAmount" 		value="${item.paymentAmount}" />
						<input type="hidden" name="updateDate" 			value="${item.updateDate}" />
						<input type="hidden" name="countryCode" 		value="${item.countryCode}" />
						
						<input type="hidden" name="forecastItemId" value="${item.forecastItemId}" />
						<input type="hidden" name="forecastRemark" value="${item.forecastRemark}" />
						<input type="text" name="productName" readonly value="${item.productName}"/>
						</td>
						<td><input type="text" readonly="readonly" maxlength="11" style="width: 80%" class="zhuangxiangno" value="${item.product.packQuantity}"/></td>
						<td><input style="width:100px"  readonly="readonly"  type="text"  class="required"  name="deliveryDate"   id="deliveryDate"   value="<fmt:formatDate value="${item.deliveryDate}" pattern="yyyy-MM-dd"/>"/> </td>
						<td>
						<input type="text" readonly  name="colorCode" id="colorCode" value="${item.colorCode}" style="width:80%">
						</td>
						<td>
						<input type="text" readonly  value="${fns:getDictLabel(item.countryCode, 'platform', '')}" style="width:80%">
						</td>
						<td> <input type="text" readonly style="width: 80%" name="quantityOrdered" class="number" value="${item.quantityOrdered}"/></td>
						<td> <input type="text" readonly style="width: 80%" name="quantityOffOrdered" class="number" value="${item.quantityOffOrdered}"/></td>
						<td> <input type="text" readonly style="width: 80%" name="itemPrice" class="price" value="${item.itemPrice}"/></td>
						<td><input type="text" readonly style="width: 80%" name="remark" value="${item.remark}"/></td>
					</tr>
				</tbody>
			</c:forEach>
		</c:if>
		
	</table>
	
		
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="审核通过"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
