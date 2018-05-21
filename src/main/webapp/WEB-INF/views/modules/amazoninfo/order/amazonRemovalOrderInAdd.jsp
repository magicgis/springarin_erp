<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>召回单入库</title>
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
			
			eval('var psiSku=${skus}');
			
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$("#selectOperationType").on("change",function(e){
				$("#dataFile").css("display","none");
				$("#dataType").css("display","none");
				$("#itemData").css("display","block");
				if(e.removed){
					var oldValue=e.removed.text;
					if(oldValue=="Transport Storing"){
						 //清理运单表头 和数据
						 var table =$("#contentTable");
						 var tbody =table.find("tbody");
						 table.find("thead").remove();
						 var head="<thead><tr><th style='width: 30%'>Product[SKU]</th> <th style='width: 10%'>Quality Tips</th><th style='width: 10%'>Quantity</th><th style='width: 30%'>Remark</th><th>Operate</th></tr></thead>";
						 table.append(head);
						 tbody.find("tr").each(function(){
							 $(this).remove();
						 });
						 
						 $("#tran").css("display","none");
						 $("#isFinished").css("display","none");
						 $("#add-row").click();
					}
					
					if($(this).val()=="Manual Operation"){
						$("#operationType").val("");
						$("#operationType").css("display","block");
					}else{
						$("#operationType").css("display","none");
						$("#operationType").val($(this).val());
						if($(this).val()=="Transport Storing"){
							$("#contentTable tbody tr").each(function(){
								 $(this).remove();
							 });
							$("#tran").css("display","block");
							 $("#isFinished").css("display","block");
							$("#tranSelect").select2().change();
						}
						
						if($(this).val()=="Lot Storing"){
							$("#itemData").css("display","none");
							$("#dataFile").css("display","block");
							$("#dataType").css("display","block");
						}
					}
				}else{
					//第一次进来给操作类型赋值
					$("#operationType").val($(this).val());
				}
			});
			
			$(".sku").live("change",function(){
				var key = $(this).val();
				var tr=$(this).parent().parent();
				tr.find("input[name='productId']").val(psiSku[key][0]);
				tr.find("input[name='productName']").val(psiSku[key][1]);
				tr.find("input[name='countryCode']").val(psiSku[key][2]);
				tr.find("input[name='colorCode']").val(psiSku[key][3]);
				
			});
			
			$("#totalQualityType").on("change",function(){
				var key = $(this).val();
				$(".qualityType").val(key).trigger("change");
			});
			
			
			$(".remove-row").live("click",function(){
				 if($('#contentTable tbody tr').size()>1){
					var tr = $(this).parent().parent();
					tr.remove();
				}
			});
			
			$("#inputForm").validate({
				rules:{
					"quantity":{
						"required":true
					},
					"operationType":{
						"required":true
					},
					"dataDate":{
						"required":true
					},
					"tranLocalId":{
						"required":true
					}
				},
				messages:{
					"quantity":{"required":'Quantity is not emtpy'}
					,"operationType":{"required":'Type is not empty'}
					,"dataDate":{"required":'Date is not empty'}
					,"tranLocalId":{"required":'TransportOrder no. is not empty'}
				
			},
			submitHandler: function(form){
				if($("#selectOperationType").val()!="Lot Storing"){
					 var keyStr="";
					 var flag = 1;
					 var zeroFlag=1;
					 var remarkFlag=1;
					 var twoStr="";
					$("#contentTable tbody tr").each(function(){
						var curkeyStr=$(this).find("select.sku").val()+$(this).find("select[name='qualityType']").children("option:selected").text();
						if(keyStr.indexOf(curkeyStr+",")>=0){
							twoStr=curkeyStr;
							flag = 2;
							return false;
						}else{
							keyStr=keyStr+curkeyStr+",";
						};
						if($("#selectOperationType").val()!="Transport Storing"){
							var quantity = $(this).find("input[name='quantity']").val();
							if(quantity==0){
								zeroFlag=2;
								twoStr=curkeyStr;
								return false;
							};
						}else{
							var quantity = $(this).find("input[name='quantity']").val();
							if($(this).find(".remove-row").text()){
								if(quantity==0){
									zeroFlag=2;
									twoStr=curkeyStr;
									return false;
								};
								
							}else{
								var oldQuantity=$(this).find(".viewQuantity").val();
								var remark =$(this).find("input[name='remark']").val();
								if(quantity!=oldQuantity && remark==''){
									remarkFlag=2;
									twoStr=curkeyStr;
									return false;
								}
							}
						}
						
					});
					
					if(remarkFlag==2){
						top.$.jBox.tip("shippedQuantity not equal receivedQuantity,please write remark "+twoStr, 'info',{timeout:3000});
						return false;
					}
					<%--
					if(flag==2){
						top.$.jBox.tip("Same product and type only has one record !!!  "+twoStr, 'info',{timeout:3000});
						return false;
					}--%>
					
					if(zeroFlag==2){
						top.$.jBox.tip("Quantity must by >0!!! "+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					top.$.jBox.confirm("<span style='color:red;font-weight:bold'>After confirmation will change the number of inventory,Operation will not be reversible,Are you sure?</span>","System Tips",function(v,h,f){
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
						
						//把所有disable的select放开
						$("select[disabled]").each(function(){
							$(this).removeAttr("disabled");
						});
						
						var isFinished = true;
						$(".isFinished").each(function(){
							isFinished=this.checked;
						});
						
						$("input[name='isFinished']").val(isFinished);
						    
						form.submit();
						$("#btnSubmit").attr("disabled","disabled");
					}
					},{buttons: { 'Confirm': 'ok', 'Cancel': ''}},{buttonsFocus:1,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.confirm("<span style='color:red;font-weight:bold'>After confirmation will change the number of inventory,Operation will not be reversible,Are you sure?</span>","System Tips",function(v,h,f){
					if(v=='ok'){
						$("#contentTable").remove();
						form.submit();
						$("#btnSubmit").attr("disabled","disabled");
					}
					},{buttons: { 'Confirm': 'ok', 'Cancel': ''}},{buttonsFocus:1,persistent: true});
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
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/removalOrder"><spring:message code='amazon_recall_order'/></a></li>
		<li class="active"><a href="${ctx}/amazoninfo/removalOrder/stored?id=${amazonRemovalOrder.id}">Create Stock in Entry</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="amazonRemovalOrder" action="${ctx}/amazoninfo/removalOrder/storedSave" method="post" class="form-horizontal" enctype="multipart/form-data" >
		<%--<input type="hidden" name="id" id="id" value="${amazonRemovalOrder.id}" > --%>
		<input type="hidden" name="country" id="country" value="${amazonRemovalOrder.country}" >
		<input type="hidden" name="amazonOrderId" id="amazonOrderId" value="${amazonRemovalOrder.amazonOrderId}" >
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">Base Info.</p>
		</blockquote>
		<div>
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:100px">
					<b>OrderId:</b>
				</label>
				<div class="controls" style="margin-left:120px">
					${amazonRemovalOrder.amazonOrderId }
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><b>Type:</b></label>
				<div class="controls" style="margin-left:120px">
					Recall Storing
				</div>
			</div>
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:100px"><b>Quality Types:</b></label>
				<div class="controls" style="margin-left:120px">
				   	<select id='totalQualityType' name="totalQualityType"  style='width:90%'>
				   		<c:forEach items='${qualityTypes}' var='qualityType'> 
				   			<option value='${qualityType}'>${qualityType}</option>
				   		</c:forEach>
				   	</select>
				   	</div>
		   	</div>
		</div>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:100px"><b>Warehouse:</b></label>
				<div class="controls" style="margin-left:120px">
				<select name="warehouseId" id="selectStock" >
					<c:forEach items="${stocks}" var="stock">
						<option value="${stock.id}" ${psiInventoryIn.warehouseId eq stock.id?'selected':''} >
						<c:choose>
							<c:when test="${stock.countrycode eq 'DE' }">Germany</c:when>
							<c:when test="${stock.id eq '21' }">China</c:when>
							<c:when test="${stock.id eq '130' }">China_LC</c:when>
							<c:when test="${stock.countrycode eq 'US' }">American</c:when>
							<c:otherwise>${stock.stockName}</c:otherwise>
						</c:choose>
						</option>
					</c:forEach>		
				</select>
				 <input type="hidden" name="warehouseName" value="${psiInventoryIn.warehouseName}" />
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><spring:message code='sys_menu_actualTimeIn'/>:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="purchaseDate"  class="Wdate required" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${psiInventoryIn.dataDate}"/>" />
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">From:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="source"  maxlength="100"/>
				</div>
			</div>
		</div>
		
		<div style="display: block" id="itemData">
			 <blockquote style="float:left;width:98%">
			 <div style="float: left"><p style="font-size: 15px;font-weight: bold">Product Info.</p></div><div style="float: left" id=errorsShow></div>
			</blockquote>
			<table id="contentTable" class="table table-striped table-bordered table-condensed" >
			<thead>
				<tr>
					<th style="width: 30%">SKU</th>
					<th style="width: 20%">ProductName</th>
					<th style="width: 10%">Quality Tips</th>
					<th>
						completedQty
						<c:if test="${'com' eq amazonRemovalOrder.country }">
							/deliveredQty
						</c:if>
					</th>
					<th>storedQty</th>
					<th>quantity</th>
					<th>Operate</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${amazonRemovalOrder.items}" var="item">
				<c:if test="${item.completedQty-item.storedQty>0 }">
					<tr>
						<td>
							<c:if test="${not empty item.productName }">
								<input type='text' name="sellersku" value="${item.sellersku }" /> 
							</c:if>
							<c:if test="${empty item.productName }">
								<input type='hidden' name='colorCode' />
								<input type='hidden' name='countryCode' />
								<%--<input type='hidden' name='productId' />
								<input type='hidden' name='productName' />
								 --%>
								<select name='sellersku' class='sku' style='width: 90%' class="required">
									<option value=''>请选择sku</option>
									<c:forEach items='${skus}' var='sku'>
										<option value='${sku.key}'>${sku.value[1]}[${sku.key}]</option>
									</c:forEach>
									</select>
							</c:if>
							</td>
						   <td>
						   	<input type='hidden' name="id" value="${item.id }"/>
						   	<input type='text' name="productName" class="required" value="${not empty item.productName?item.productName:asinNameMap[asin] }"/>
						   	</td>
							<td>
					   	<select name='qualityType' class='qualityType'  style='width:90%'>
					   		<c:forEach items='${qualityTypes}' var='qualityType'> 
					   			<option value='${qualityType}'>${qualityType}</option>
					   		</c:forEach>
					   	</select>
					   	</td>
						<td>
							<font style="color:${empty item.productName?'red':''}">
								${item.completedQty}
								<c:if test="${'com' eq amazonRemovalOrder.country }">&nbsp;/&nbsp;${item.deliveredQty}</c:if>
							</font>
						</td>
						<td>${item.storedQty}</td>
						<td>
						<c:set var="canStoredQty" value="${item.completedQty-item.storedQty}"></c:set>
						<input type='text' maxlength='11' max="${canStoredQty}" 
							style='width: 80%'  name='cancelledQty' class='number' value="${canStoredQty}" /></td>
					   <td><a href='#' class='remove-row'><span class='icon-minus'></span>Delete</a></td>
					</tr>
					</c:if>
				</c:forEach>
			</tbody>
			
		</table>
		</div>
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="Create"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
