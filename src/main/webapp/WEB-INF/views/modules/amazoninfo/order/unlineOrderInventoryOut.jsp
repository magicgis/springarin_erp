<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>线下订单出库管理</title>
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
			
			$("#btnSubmit").click(function(){
				 var outInfos="";
				 var exeFlag=1;
				 var quantityFlag=false;
				$("#contentTable tbody tr").each(function(){
					var sku=$(this).find("input[name='sku']").val();
					var quantity = $(this).find("input[name='quantity']").val();
					var offlineQuantity=$(this).find(".offlineQuantity").val();
					var outQuantity=$(this).find(".outQuantity").val();
					var orderQuantity=$(this).find(".orderQuantity").val();
					if(quantity >orderQuantity-outQuantity){
						 quantityFlag=true;
					}
					
					outInfos=outInfos+sku+","+parseInt(parseInt(offlineQuantity)-parseInt(quantity))+";";
					if(offlineQuantity<parseInt(quantity)){
						exeFlag=2;
					}
					
				});
				if(exeFlag==2){
					top.$.jBox.tip("The number of inputs is greater than the current inventory number ! ", 'info',{timeout:3000});
					return false;
				}
				
				if(quantityFlag){
					top.$.jBox.tip("The outbound quantity is greater than the unshipped quantity ! ", 'info',{timeout:3000});
					return false;
				}
				
				var  confirmStr="<span style='color:red;font-weight:bold'>After confirmation will change the quantity of inventory,Are you sure?</span>";
				//德国仓出库需要确认sku转换  
				if($("#warehouseId").val()=='19'){
					var res=ajaxSkuChangeValidate(outInfos,$("#warehouseId").val());   
					if(res){
						top.$.jBox.tip(res, 'info',{timeout:3000});
						return false;
					}
				}
				
				var  widthVar=350;
				top.$.jBox.confirm(confirmStr,'System Tips',function(v,h,f){
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
						$("#inputForm").submit();
						$("#btnSubmit").attr("disabled","disabled");
					}
				},{buttons: { 'Confirm': 'ok', 'Cancel': ''}},{buttonsFocus:1,persistent: true,width: widthVar, showType: 'fade'});
				top.$('.jbox-body .jbox-icon').css('top','55px'); 
			});
    	});
		
		  function ajaxSkuChangeValidate(outInfos,warehouseId){
				var res="";
				$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/psi/psiSkuChangeBill/ajaxQuantityValidate',
				    data: {
				    	"outInfos":outInfos,
				    	"warehouseId":warehouseId
				    },
				    dataType: 'json',
				    success:function(data){ 
				    	res=data.msg;
			        }
				});
				return res;
			}
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/amazoninfo/unlineOrder">Unline Order List</a></li>
		<li class="active"><a href="#" style="font-size: 16px">Create Stock Out Entry</a></li>
	</ul>
	<div class="alert">
	    <button type="button" class="close" data-dismiss="alert">&times;</button>
	    <strong>线下订单支持批量出库</strong>
	</div>
	<form id="inputForm"  action="${ctx}/amazoninfo/unlineOrder/outboundSave" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id" value="${amazonUnlineOrder.id }">
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">Base Info.</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><b>Warehouse:</b></label>
				<div class="controls" style="margin-left:120px">
				 <input type="hidden" name="warehouseId" value="${amazonUnlineOrder.salesChannel.id}" />
				 <input type="text" name="warehouseName" value="${amazonUnlineOrder.salesChannel.stockName}" readonly/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
					<label class="control-label" style="width:100px">Certificate:</label>
					<div class="controls" style="margin-left:120px">
					<input name="memoFile" type="file" id="myfileupload" />
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;;height:30px">
				<label class="control-label" style="width:100px"><b><spring:message code='sys_menu_actualTimeOut'/>:</b></label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="dataDate"  class="Wdate required"  />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;;height:30px">
				<label class="control-label" style="width:100px"><b>To:</b></label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="whereabouts"  style="margin-top:3px" required="required"   value="${amazonUnlineOrder.shippingAddress.addressLine1}" readonly/>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100% ;">
				<div class="control-group" style="float:left;width:30%;height:50px">
					<label class="control-label" style="width:100px"><b>Type:</b></label>
					<div class="controls" style="margin-left:120px">
					<input type="text" name="operationType" id="operationType" value="Offline Delivery" style="margin-top:3px" required="required" readonly/>
					</div>
				</div>
				
				<div class="control-group fbaDiv" style="float:left;width:68%;height:50px;">
					<label class="control-label" style="width:100px"><b>Unline No.:</b></label>
					<div class="controls" style="margin-left:120px;width:65%" >
						<input type="text"  name="tranLocalNo" value="${amazonUnlineOrder.amazonOrderId}" style="margin-top:3px" required="required" readonly/>
						<input type="hidden" name="tranLocalId"  value="${amazonUnlineOrder.id}" style="margin-top:3px" required="required"/>
					</div>
				</div>
		
		</div>
		
		
		<div style="float:left;width:98% ;">
		<div class="control-group" style="float:left;width:98%">
				<label class="control-label" style="width:100px"><b>Remark:</b></label>
				<div class="controls" style="margin-left:120px">
					<textarea  rows="4" maxlength="200" style="width:95%; height: 60px;"  id="bigRemark" name="remark"/></textarea>
				</div>
		</div>
		</div>
		<div style="display: block" id="itemData">
				 <blockquote style="float:left;width:98%">
				 <div style="float: left"><p style="font-size: 15px;font-weight: bold">Product Info.</p></div><div style="float: left" id=errorsShow></div>
				  </blockquote>
				  
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						   <th style="width: 30%">Product[SKU]</th>
						   <th style="width: 5%">Quality Type</th>
						   <th style="width: 10%">Offline Quantity</th>
						   <th style="width: 10%">Order Quantity</th>
						   <th style="width: 10%">Shipped Quantity</th>
						   <th style="width: 10%">Outbound Quantity</th>
						   <th style="width: 20%">Remark</th>
					</tr>
				</thead>
				<tbody>
				  <c:forEach items="${amazonUnlineOrder.items}" var="item">
				      <c:if test="${item.quantityOrdered-item.quantityOut>0 }">
					       <tr>
					         <td><input type="hidden" name="sku" value="${item.sellersku }"><input type='text'  style='width: 90%' value="${productNameSkuMap[item.sellersku] }" readonly/></td>
					         <td><input type="text" name="qualityType" value="offline" style='width:70%' readonly></td>
					         <td><input type='text'  style='width: 80%'  class='offlineQuantity' value="${productQuantityMap[item.sellersku] }" readonly/></td>
					         <td><input type='text'  style='width: 80%' class='orderQuantity' value="${item.quantityOrdered }" readonly/></td>
					         <td><input type='text'  style='width: 80%' class='outQuantity' value="${item.quantityOut }" readonly/></td>
					         <td><input type='text'  style='width: 80%' name="quantity" value="${item.quantityOrdered-item.quantityOut }" class="required"/></td>
					         <td><input type='text'  style='width: 90%' name='remark' /></td>
					      </tr>
				      </c:if>
				  </c:forEach>
				</tbody>
			</table>
		</div>
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="Create"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>
