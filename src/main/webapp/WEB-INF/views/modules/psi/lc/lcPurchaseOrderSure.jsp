<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			if(!(top)){
				top = self; 
			}
			
			new tabTableInput("inputForm","text");
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
				});
			$("#inputForm").validate({
				rules:{
					"itemPrice":{
						"required":true
					}
				},
				messages:{
					"itemPrice":{"required":'价格不能为空'}
				},
				submitHandler: function(form){
					
					var flag = true;
					$("input[name='itemPrice']").each(function(){
						if(flag){
							if($(this).val()!=''){
								if(!$.isNumeric($(this).val())){
									flag = false;
								}
								if(parseFloat($(this).val())<=0){
									flag = false;
								}
							}else{
								flag=false;
							} 
						}
					});
					
					if(flag){
						top.$.jBox.confirm('请确认产品价格是否与PI里的价格相等!','系统提示',function(v,h,f){
							if(v=='ok'){
								$("#contentTable tbody tr").each(function(i,j){
									$(j).find("select").each(function(){
										if($(this).attr("name")){
											$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
										}
									});
									
									$(j).find("input[type!='']").each(function(){
										if($(this).attr("name")){
											$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
										}
									});
								});
								
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
							}
							},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}else{
						top.$.jBox.error("价钱不能为空或<=0的数","系统提示");
					}
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
		<li ><a href="${ctx}/psi/lcPurchaseOrder/">采购订单列表</a></li>
		<li class="active"><a href="#">采购订单确认</a></li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="purchaseOrder" action="${ctx}/psi/lcPurchaseOrder/sureSave" method="post" class="form-horizontal" enctype="multipart/form-data" >
		<input type="hidden" name="id"     				value="${purchaseOrder.id}" />
		<input type="hidden" name="orderNo" 			value="${purchaseOrder.orderNo}" />
	    <input type="hidden" name="oldItemIds"			value="${purchaseOrder.oldItemIds}" />
	    <input type="hidden" name="delFlag"				value="${purchaseOrder.delFlag}" />
	    <input type="hidden" name="createUser.id"		value="${purchaseOrder.createUser.id}" />
	    <input type="hidden" name="totalAmount" 		value="${purchaseOrder.totalAmount}" />
	    <input type="hidden" name="orderSta" 			value="${purchaseOrder.orderSta}" />
	    <input type="hidden" name="supplier.id" 		value="${purchaseOrder.supplier.id}" />
	    
	    <input type="hidden" name="updateUser.id" 		value="${purchaseOrder.updateUser.id}" />
	    <input type="hidden" name="updateDate" 			value="<fmt:formatDate pattern='yyyy-MM-dd hh:mm:ss' value='${purchaseOrder.updateDate}'/>" />
	    
	    <input type="hidden" name="depositAmount" 		value="${purchaseOrder.depositAmount}" />
	    <input type="hidden" name="depositPreAmount" 	value="${purchaseOrder.depositPreAmount}" />
	    <input type="hidden" name="piFilePath" 	        value="${purchaseOrder.piFilePath}" />
	    
	    <input type="hidden" name="paymentAmount" 	    value="${purchaseOrder.paymentAmount}" />
	    <input type="hidden" name="paySta" 	            value="${purchaseOrder.paySta}" />
	    <input type="hidden" name="merchandiser.id" 	value="${purchaseOrder.merchandiser.id}" />
	    <input type="hidden" name="sendEmailFlag"       value="${purchaseOrder.sendEmailFlag}"/>
	    
	    <input type="hidden" name="toReview" 			value="${purchaseOrder.toReview}" />
	    <input type="hidden" name="toPartsOrder" 		value="${purchaseOrder.toPartsOrder}" />
	    <input type="hidden" name="isOverInventory" 	value="${purchaseOrder.isOverInventory}" />
	    <input type="hidden" name="overRemark" 		    value="${purchaseOrder.overRemark}" />
	    
	    
	    <input type="hidden" name="receivedStore" 		 value="${purchaseOrder.receivedStore}" />
	    <input type="hidden" name="offlineSta" 		     value="${purchaseOrder.offlineSta}" />
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
					<input  type="text" name="purchaseDate" style="width:60%" class="Wdate" value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd" />" />
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
				   <th style="width: 15%">产品名称</th>
				   <th style="width: 8%">装箱数量</th>
				   <th style="width: 10%">PO交期</th>
				   <th style="width: 10%">预计交期</th>
				   <th style="width: 8%">颜色</th>
				   <th style="width: 10%">国家</th>
				   <th style="width: 8%">总数量</th>
				   <th style="width: 8%">线下数</th>
				   <th style="width: 10%">单价</th>
				   <th style="width: 10%">备注</th>
				   
			</tr>
		</thead>
		<c:if test="${not empty purchaseOrder.items}" >
			<c:forEach items="${purchaseOrder.showItems}" var="item" varStatus="i">
				<tbody>
					<tr>
						<td class="product" >
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
						<td><input  style="width: 80%" readonly="readonly"  type="text"  name="deliveryDate"  value="<fmt:formatDate value="${item.deliveryDate}" pattern="yyyy-MM-dd"/>"/> </td>
						<td><input  style="width: 80%" readonly="readonly"  type="text"  name="actualDeliveryDate"   value="<fmt:formatDate value="${item.actualDeliveryDate}" pattern="yyyy-MM-dd"/>"/> </td>
						<td>
						<input type="text" readonly  name="colorCode" id="colorCode" value="${item.colorCode}" style="width:80%">
						</td>
						<td>
						<input type="text" readonly  value="${fns:getDictLabel(item.countryCode, 'platform', '')}" style="width:80%">
						</td>
						<td> <input type="text" readonly style="width: 80%" name="quantityOrdered" class="number" value="${item.quantityOrdered}"/></td>
						<td> <input type="text" readonly style="width: 80%" name="quantityOffOrdered" class="number" value="${item.quantityOffOrdered}"/></td>
						<td>
						<c:set value="${item.product.id}_${item.colorCode }" var="proColorKey" />
						<input type="text"  class="price itemPrice" required="required" style="width: 80%" name="itemPrice"  value="${not empty item.itemPrice?item.itemPrice:productPrices[proColorKey]}" tabindex="${i.count}"/></td>
						<td><input type="text" readonly style="width: 80%" name="remark" value="${item.remark}"/></td>
					</tr>
				</tbody>
			</c:forEach>
		</c:if>
		
	</table>
	<div style="float:left;width:100%">
		  <blockquote>
			<p style="font-size: 14px">上传PI</p>
		</blockquote>
			<div class="control-group" style="float:left;width:30%">
				<label class="control-label"></label>
				<div class="controls">
				<input name="piFile" type="file" id="myfileupload" required="required"/>
				</div>
			</div>
		</div>
		
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="确认"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
