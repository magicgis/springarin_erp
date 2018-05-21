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
			eval('var partsOutMap=${partsOutMap}');
			eval('var proColorQuantityMap=${proColorQuantityMap}');  
			eval('var productOrderMap=${productOrderMap}');     
			eval('var productQuantityMap=${productQuantityMap}');     
			
			$("#productIdColor").on("change",function(){
				if($("#productIdColor").val()){
					var key =$("#productIdColor").val();
					var tbody = $("#contentTable tbody");
					tbody.find("tr").each(function(){
						$(this).remove();
					});
					
					
					var tbodyOrder = $("#orderTable tbody");
					tbodyOrder.find("tr").each(function(){
						$(this).remove();
					});
					
					for(var i in productOrderMap[key]){
						var orderInfoArr = productOrderMap[key][i];
						var orderKey=orderInfoArr[0]+"_"+key;
						var tr =$("<tr></tr>");
						tr.append("<td><input type='hidden'  name='purchaseOrderId' class='purchaseOrderId' value='"+orderInfoArr[0]+"'/><input type='text' readonly style='width:80%' name='purchaseOrderNo' value='"+orderInfoArr[1]+"'/></td>");
						tr.append("<td><input class='orderQuantity' type='text' readonly value='"+productQuantityMap[orderKey]+"' </td>");
						tr.append("<td><input class='number orderReQuantity' type='text' name='quantity' /> </td>");
						tbodyOrder.append(tr);
					};
					
					
					var outQuantiy =proColorQuantityMap[key];
					$(".refQuantity").val(outQuantiy);
					$("#productName").val($(this).children('option:selected').text());
					for(var i in partsOutMap[key]){
						var partsOut = partsOutMap[key][i];
						var tr =$("<tr></tr>");
						tr.append("<td><input type='hidden'  name='partsId' value='"+partsOut.partsId+"'/><input type='text' readonly style='width:80%' name='partsName' value='"+partsOut.partsName+"'/></td>");
						tr.append("<td><input type='text'  readonly style='width:80%' name='mixtureRatio' value='"+partsOut.mixtureRatio+"'/></td>");
						tr.append("<td><input type='text'  readonly style='width:80%' name='stockQuantity' value='"+partsOut.stockQuantity+"'/></td>");
						tr.append("<td><input type='text'  readonly style='width:80%;color:red' name='quantity' class='outQuantity'  value='"+outQuantiy*partsOut.mixtureRatio+"'/></td>");
						tr.append("<td><input type='text'  readonly style='width:80%'  value='"+partsOut.maxCanQuantity+"'/></td>");
						tbody.append(tr);
					};
				}
			});
			
			$("#productIdColor").change();
			
			$(".orderReQuantity").live("blur",function(){
				var tbody =$(this).parent().parent().parent();
				var total=0;
				tbody.find(".orderReQuantity").each(function(){
					if($(this).val()){
						total=parseInt(total)+ parseInt($(this).val());
					}
				});
				
				$("#contentTable tbody tr").each(function(){
					$(this).find("input[name='quantity']").val($(this).find("input[name='mixtureRatio']").val()*total);
					
				});
			});
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
					var orderQuantityFlag=0;
					var totalQuantity=0;
					$("#orderTable tbody tr").find(".orderReQuantity").each(function(){
						var tr =$(this).parent().parent();
						var orderQuantity =tr.find(".orderQuantity").val();
						var orderReQuantity=tr.find(".orderReQuantity").val();
						if(orderReQuantity){
							if(parseInt(orderReQuantity)>parseInt(orderQuantity)){
								orderQuantityFlag=1;
								return ;
							}
							totalQuantity=totalQuantity+parseInt(orderReQuantity);
						}
						
					});
					
					
					if(orderQuantityFlag==1){
						top.$.jBox.tip("本次配送数必须<最大可配送数", 'info',{timeout:3000});
						return false;
					}
					
					if(totalQuantity==0){
						top.$.jBox.tip("本次配送总数不能为0", 'info',{timeout:3000});
						return false;
					}
					
					if(totalQuantity>parseInt($(".refQuantity").val())){
						top.$.jBox.tip("本次配送总数必须<参考套数", 'info',{timeout:3000});
						return false;
					}
					
					
					$("input[name='productName']").val($("select#productIdColor").children("option:selected").text());
					
					$("#orderTable tbody tr").each(function(i,j){
						$(j).find("select").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","orders"+"["+i+"]."+$(this).attr("name"));
							}
						});
						$(j).find("input[type!='']").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","orders"+"["+i+"]."+$(this).attr("name"));
							}
						});
					});
					
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
		<li><a href="${ctx}/psi/psiPartsInventoryOut/">配件出库列表</a></li>
		<li class="active"><a href="#">配件出库单</a></li>
	</ul>
	<div style="float:left;width:87%;height:15px" class="alert alert-info"><strong>
	Tips:"参考套数"为多个配件“最大可出套数”的最小套数和订单“最大可配送套数”两者的的最小值
	</strong></div>
	<form:form id="inputForm" modelAttribute="psiPartsInventoryOut" action="${ctx}/psi/psiPartsInventoryOut/save" method="post" class="form-horizontal">
	<input type="hidden" name="productName" />
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:35%">
				<label class="control-label" style="width:100px"><b>产品名</b>:</label>
				<div class="controls" style="margin-left:120px;height:30px">   
				<span>
					<select style="width:90%;" name="productIdColor" class="required" id="productIdColor">
						<c:forEach items="${productMap}" var="product">
							<option value="${product.key}">${product.value}</option>
						</c:forEach>
					</select>
				</span>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%">
				<label class="control-label" style="width:100px"><b>参考套数：</b>:</label>
				<div class="controls" style="margin-left:120px;height:30px">   
				<span>
					<input class="refQuantity" type="text" readonly style="width:80%"/>
				</span>
				</div>
			</div>
		</div>
		
		<blockquote style="float: left;width:98%"><p style="font-size: 14px">订单信息</p></blockquote>
		<div style="font-size: 14px;margin: 0px 100px 5px 100px;float:left;width:98%"></div>   
		<table id="orderTable" class="table table-striped table-bordered table-condensed" style="width:98%;margin-left:45px">
			<thead>
				<tr>
				   <th style="width: 30%" >订单名称</th>
				   <th style="width: 18%" >最大可配送数量</th>
				   <th style="width: 18%" >本次配送数量</th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
		
		<blockquote style="float: left;width:98%"><p style="font-size: 14px">配件出库信息</p></blockquote>
		<div style="font-size: 14px;margin: 0px 100px 5px 100px;float:left;width:98%"></div>   
		<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width:98%;margin-left:45px">
			<thead>
				<tr>
				   <th style="width: 30%">配件名称</th>
				   <th style="width: 18%" >产品配件比</th>
				   <th style="width: 18%" >库存锁定数量</th>
				   <th style="width: 18%" >出库数量</th>
				   <th style="width: 18%" >最大可出套数</th>
				</tr>
			</thead>
			<tbody>
			
			</tbody>
		</table>
		
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
