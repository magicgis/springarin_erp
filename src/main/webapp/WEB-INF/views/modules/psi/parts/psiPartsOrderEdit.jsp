<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品配件管理</title>
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
			
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			eval('var partsMap=${partsMap}');
			$("select.partsId").live('change', function(e){
				var removeVal = e.removed.id;
				var $this = $(this);  
				var productVal = $this.val();
				$("select.partsId").each(function(){
    				if($(this).val()!=productVal){
    					$(this).find("option[value="+productVal+"]").remove();    					
    					$(this).append("<option value='"+removeVal+"'>"+partsMap[removeVal].partsName+"</option>");
    				}
    			});
				
				var  tr = $this.parent().parent();
				var productName=$this.children('option:selected').text();
				if(productName){
					tr.find("input[name='partsName']").val(productName);
				}
				tr.find("input[name='deliveryDate']").val(partsMap[productVal].deliveryDate);
				tr.find("input[name='itemPrice']").val(partsMap[productVal].price);
			});
			
			$('#add-row').on('click', function(e){
				    var tbody = $('#contentTable tbody');
		            var tr = $("<tr></tr>");
		            var td ="<td> <input type='hidden' name='partsName'/><input type='hidden' name='itemPrice'/><select style='width: 90%' class='partsId'  name='psiParts.id' >";
		            var i = 0 ;
		            for (var key in partsMap) {
		            	if($(".partsId[value="+key+"]").size()==0){
		            		if(i==0){
		            			$("select.partsId").each(function(){
		            				$(this).find("option[value="+key+"]").remove();
		            			});
		            		}
		            		td = td.concat("<option value='"+key+"'>"+partsMap[key].partsName+"</option>");
		            		i++;
		            	}	
					}
		            td = td.concat("</select></td>");
		            tr.append(td);
		            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='quantityOrdered' class='number' /></td>");
		            tr.append("<td> <input type='text' style='width: 90%'  name='deliveryDate' readonly class='Wdate'/></td>");
		            tr.append("<td> <input type='text' style='width: 90%'  name='actualDeliveryDate' class='Wdate'/></td>");
		            tr.append("<td> <input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
		            tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>删除配件</a></td>");
		            tr.find("select.partsId").select2();
		            tbody.append(tr);
		            
		            var productId=tr.find("select.partsId").val();
		            if(partsMap[productId]){
		            	tr.find("input[name='deliveryDate']").val(partsMap[productId].deliveryDate);
		            	tr.find("input[name='partsName']").val(partsMap[productId].partsName);
						tr.find("input[name='itemPrice']").val(partsMap[productId].price);
		            }
			});
			
			
			
			$('#contentTable').on('click', '.remove-row', function(e){
				  if($('#contentTable tbody tr').size()>1){
					  var tr = $(this).parent().parent();
					  var id = tr.find(".partsId").select2("val");
					  tr.remove();
					  if(id){
						  $("select.partsId").each(function(){
		          				$(this).append("<option value='"+id+"'>"+partsMap[id].partsName+"</option>");
		          		  });
					  }
				  }
			});
			
			$("#inputForm").validate({
				rules:{
					"psiParts.id":{
						"required":true
					},
					"deposit":{
						"required":true
					},
					"quantityOrdered":{
						"required":true
					},
					"quantityReceived":{
						"required":true
					}
					
					
				},
				messages:{
					"psiParts.id":{"required":'配件不能为空'},
					"deposit":{"required":'定金不能为空'},
					"quantityOrdered":{"required":'订单数量不能为空'},
					"quantityReceived":{"required":'接收数量不能为空'}
					
				},
				submitHandler: function(form){
										
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
		<li><a href="${ctx}/psi/psiPartsOrder/">配件订单列表</a></li>
		<li class="active"><a href="#">编辑配件订单</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiPartsOrder" action="${ctx}/psi/psiPartsOrder/editSave" method="post" class="form-horizontal">
		<input type="hidden" 	name="partsOrderNo" 		value="${psiPartsOrder.partsOrderNo}" />
		<input type="hidden" 	name="id" 		   			value="${psiPartsOrder.id}" />
		<input type="hidden" 	name="orderSta" 		   	value="${psiPartsOrder.orderSta}" />
		<input type="hidden" 	name="oldItemIds" 			value="${psiPartsOrder.oldItemIds}" />
		<input type="hidden" 	name="paymentSta" 		    value="${psiPartsOrder.paymentSta}" />
		<input type="hidden" 	name="paymentAmount" 		value="${psiPartsOrder.paymentAmount}" />
		<input type="hidden" 	name="prePaymentAmount" 	value="${psiPartsOrder.prePaymentAmount}" />
		<input type="hidden" 	name="depositPreAmount" 	value="${psiPartsOrder.depositPreAmount}" />
		<input type="hidden" 	name="depositAmount" 		value="${psiPartsOrder.depositAmount}" />
	    <input type="hidden" 	name="createUser.id" 		value="${psiPartsOrder.createUser.id}" />
	    <input type="hidden" 	name="createDate" 			value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiPartsOrder.createDate}'/>" />
	    <input type="hidden" 	name="updateUser.id" 		value="${psiPartsOrder.updateUser.id}" />
	    <input type="hidden" 	name="updateDate" 			value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiPartsOrder.updateDate}'/>" />
	    
	    
		 <blockquote>
			<p style="font-size: 14px">基本信息(<b>${psiPartsOrder.partsOrderNo }</b>)</p>
		</blockquote>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><b>供应商</b>:</label>
				<div class="controls" style="margin-left:120px" >
				<span>
					<input type="text" readonly="readonly"  value="${psiPartsOrder.supplier.nikename}"/>
					<input type="hidden" name="supplier.id" value="${psiPartsOrder.supplier.id}"/>
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:25%;height:30px" >
				<label class="control-label" style="width:100px"><b>定金</b>:</label>
				<div class="controls" style="margin-left:120px">
					<div class="input-prepend input-append">
						<input  type="text" class="number required" style="width:80%;" name="deposit" value="${psiPartsOrder.deposit}" /><span class="add-on">%</span>
					</div>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:25%;height:30px" >
				<label class="control-label" style="width:100px"><b>货币类型</b>:</label>
				<div class="controls" style="margin-left:120px">
					<input  type="text" readonly style="width:80%" name="currencyType" value="${psiPartsOrder.currencyType}" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><b>采购日期</b>:</label>
				<div class="controls" style="margin-left:120px">
				<span>
					<input  type="text"  style="width:60%"   class="Wdate required"  name="purchaseDate"   id="purchaseDate" value="<fmt:formatDate value="${psiPartsOrder.purchaseDate}" pattern="yyyy-MM-dd" />" />
				</span>
				</div>
			</div>
		</div>
		
		<div class="control-group"  style="float:left;width:100%">
			<label class="control-label" style="width:100px"><b>备注</b>:</label>
			<div class="controls" style="margin-left:120px">
				<textarea  maxlength="255" style="height:50px;width:98%" name="remark"  >${psiPartsOrder.remark}</textarea>
			</div>
		</div>
		
		
		<div style="float:left;width:100%">
		 <blockquote>
		 <div style="float: left"><p style="font-size: 14px">配件信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;">
			<c:if test="${psiPartsOrder.orderSta eq '0'}">   
				<span class="icon-plus"></span><a href="#" id="add-row">增加配件</a>
			</c:if>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">配件名称</th>
				    <th style="width: 10%">PO数</th>
				   <th style="width: 10%">PO交期</th>
				   <th style="width: 10%">预计交期</th>
				   <th style="width: 20%">备注</th>
				   <th style="">操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${psiPartsOrder.items}" var="item">
				<tr>
					<td>
					<input type="hidden" name="id" 					value="${item.id}"/>
					<input type="hidden" name="itemPrice" 			value="${item.itemPrice}"/>
					<input type="hidden" name="quantityReceived" 	value="${item.quantityReceived}"/>
					<input type="hidden" name="quantityPreReceived" value="${item.quantityPreReceived}"/>
					<input type="hidden" name="quantityPayment" 	value="${item.quantityPayment}" />
					<input type="hidden" name="paymentAmount"       value="${item.paymentAmount}" />
					<c:if test="${item.quantityReceived eq 0}">
						<input type="hidden" name="partsName" value="${item.partsName}"/>
						<select style="width:90%" class="partsId"  name="psiParts.id">
							<c:forEach items="${partsMapEdit}" var="partsEntry">
								<option value="${partsEntry.key}" ${partsEntry.key eq item.psiParts.id ?'selected':''} >${partsEntry.value.partsName}</option>
							</c:forEach>
						</select>
					</c:if>
					<c:if test="${item.quantityReceived ne 0}">
						<input type="hidden" name="psiParts.id" value="${item.psiParts.id}"/>
						<input type="text" maxlength="200" style="width: 80%" name="partsName" value="${item.partsName}" readonly="readonly"/>
					</c:if>
					</td>
					<td><input type="text" maxlength="11" style="width: 80%"  name="quantityOrdered"  class="number" value="${item.quantityOrdered}"  ${(item.quantityReceived+item.quantityPreReceived)>0 ?'readonly':''}/></td>
					<td><input type="text" name="deliveryDate" readonly="readonly"	value="<fmt:formatDate pattern='yyyy-MM-dd' value='${item.deliveryDate}'/>" /></td>
					<td><input type="text" name="actualDeliveryDate"  class="Wdate"	value="<fmt:formatDate pattern='yyyy-MM-dd' value='${item.actualDeliveryDate}'/>" /></td>
					<td><input type="text" maxlength="200" style="width: 80%" name="remark" value="${item.remark}"/></td>
					<td><c:if test="${psiPartsOrder.orderSta eq '0'}"><a href='#' class="remove-row"><span class="icon-minus"></span>删除配件</a></c:if></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
