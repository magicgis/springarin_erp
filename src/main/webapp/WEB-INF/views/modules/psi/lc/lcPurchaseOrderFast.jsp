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
			eval('var receivedMap=${receivedMap}');
			new tabTableInput("inputForm","text");
			var countryMap = {};
			<c:forEach items="${fns:getDictList('platform')}" var="dic">
				countryMap['${dic.value}'] = '${dic.label}';
			</c:forEach>
			
			
			var productArgs ={};
			var colorArgs =[];
			var countryArgs=[];
			var  colorsStr;
			var  countryStr;
			var  product;
			<c:forEach items="${purchaseOrder.supplier.products}" var="productSupplier" varStatus="i">
			  	product={};
			  	<c:if test='${productSupplier.product.isSale ne 4 }'>
				  	product.id='${productSupplier.product.id}';
				  	product.packQuantity='${productSupplier.product.packQuantity}';   
				  	product.color='${productSupplier.product.color}';
				  	product.platform ='${productSupplier.product.platform}';
				  	productArgs['${productSupplier.product.id}']=product;
			  	</c:if>
			</c:forEach>  
			
			
			$("#inputForm").on("change",".productId",function(){
				var  tr = $(this).parent().parent();
				tr.find("select[name='colorCode']").select2("data",[]);
				tr.find("select[name='countryCode']").select2("data",[]);
				tr.find("select[name='colorCode']").select2().empty();
				tr.find("select[name='countryCode']").select2().empty();
				var productVal  = $(this).val();
				//获取选中的text
				var productName = $(this).children('option:selected').text();
			    tr.find("input[name='productName']").val(productName);
				tr.find("input[name='product.id']").val(productVal);
				product=productArgs[productVal];
				
				colorsStr=product.color;
				countryStr=product.platform;
				
				if(colorsStr==null||colorsStr==""){
					tr.find("select[name='colorCode']").select2().append("<option value='No color'>No color</option>").select2("val","No color");
				}else{
					colorArgs=colorsStr.split(',');
					$(colorArgs).each(function(i,data){
			   		  tr.find("select[name='colorCode']").select2().append("<option value='"+data+"' >"+data+"</option>");
					});
				}
				
			    var deliveryDate = receivedMap[productVal];
			    tr.find("input[name='deliveryDate']").val(deliveryDate);
			    tr.find("input[name='actualDeliveryDate']").val(deliveryDate);
			    
			  	//填充装箱数
	            tr.find(".zhuangxiangno").val(product.packQuantity);
			
				countryArgs=countryStr.split(',');
				$(countryArgs).each(function(i,data){
					 tr.find("select[name='countryCode']").select2().append("<option value='"+data+"' >"+countryMap[data]+"</option>");
				});  
				tr.find(".zhuangxiangno").val(product.packQuantity);
				tr.find("input[name='quantityOrdered']").val("");
			});
			
			
			
		$('#add-row').on('click', function(e){
		    e.preventDefault();
		    var tbody = $("#contentTable tbody");
            var tr = $("<tr></tr>");
            tr.append("<td> <input type='hidden' name='productName'/> <select style='width: 90%' class='productId'  name='product.id' ><c:forEach items='${purchaseOrder.supplier.products}' var='productSupplier' varStatus='i'><option value='${productSupplier.product.id}'>${productSupplier.product.name}</option>	</c:forEach></select> </td>");
            tr.append("<td> <input type='text' readonly='readonly' class='zhuangxiangno' maxlength='11' style='width: 80%' /></td>");
            tr.append("<td> <input style='width:100px'  readonly='true'  type='text'  name='deliveryDate' /></td>");
            tr.append("<td> <input style='width:100px'  type='text'  class='Wdate required'  name='actualDeliveryDate'   id='actualDeliveryDate' pattern='yyyy-MM-dd' /></td>");
            tr.append("<td> <span id='masters'><select name='colorCode'   class='multiSelect' style='width:100%'/></span></td>");
            tr.append("<td> <span id='masters'><select name='countryCode' class='multiSelect' style='width:100%'/></span></td>");
            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='quantityOrdered' class='number' /></td>");
            tr.append("<td> <input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
            tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>删除产品</a></td>");
           
            tr.find("select[name='product.id']").select2();
            //获取第一个tr 第一个td的select值 
            var productId=tr.find("td:first>select").val();
		    var productName=tr.find("td:first>select").children('option:selected').text();
		    tr.find("input[name='productName']").val(productName);
            
		    var deliveryDate = receivedMap[productId];
		    tr.find("input[name='deliveryDate']").val(deliveryDate);
		    tr.find("input[name='actualDeliveryDate']").val(deliveryDate);
            
		    product=productArgs[productId];
		  	//填充装箱数
            tr.find(".zhuangxiangno").val(product.packQuantity);
		  
            var colorStr=product.color;
            if(colorStr==null||colorStr==""){
				tr.find("select[name='colorCode']").select2().append("<option value='No color'>No color</option>").select2("val","No color");
			}else{
				$(product.color.split(",")).each(function(i,data){
				   tr.find("select[name='colorCode']").select2().append("<option value='"+data+"' >"+data+"</option>");
				 }); 
			}
		  
			$(product.platform.split(",")).each(function(i,data){
				tr.find("select[name='countryCode']").select2().append("<option value='"+data+"' >"+countryMap[data]+"</option>");
			});  
			
			 tbody.append(tr);
           
			});
			
			$("#contentTable").on('click', '.remove-row', function(e){
				  e.preventDefault();
				  if($("#contentTable tbody tr").size()>1){
					  var tr = $(this).parent().parent();
					  console.log(tr);
					  tr.remove();
				  }
			});
			
			//如果供应商切换，把这些
			
			$("#supplier").on("change",function(){
			 		var paramStr = "";
			 		var i = 0;
			 		$("#contentTable tbody tr").each(function(){
			 			var productId=$(this).find("select.productId").val();
			 			var oldProductId=$("#oldItemIds").val();
			 			var productName = $(this).find("select.productId").children('option:selected').text();
			 			if(oldProductId==productId){
			 				if(i==0){
								paramStr +="?";
							}else{
								paramStr +="&";
							}
			 				paramStr +="items["+i+"].quantityOrdered="+$(this).find("input[name='quantityOrdered']").val();
			 				paramStr +="&items["+i+"].product.id="+productId;
			 				paramStr +="&items["+i+"].countryCode="+$(this).find("select[name='countryCode']").val();
			 				paramStr +="&items["+i+"].colorCode="+$(this).find("select[name='colorCode']").val();
			 				paramStr +="&items["+i+"].productName="+productName;
			 				i++;				
			 			};
			 		});
			 		paramStr=paramStr+"&supplier.id="+$(this).val();
			 		window.location.href = "${ctx}/psi/lcPurchaseOrder/fastCreateOrder"+paramStr;
			 	});
			
			
		
			$(".Wdate").live("click", function (){
			 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			
			$("#btnSureSubmit").on('click',function(e){
				 if($("#inputForm").valid()){
					 top.$.jBox.confirm('确认要申请审核？申请后将发送邮件通知审核人！','系统提示',function(v,h,f){
							if(v=='ok'){
								$("#toReview").val("1");
								$("#inputForm").submit();
								$("#btnSureSubmit").attr("disabled","disabled");
								$("#btnSubmit").attr("disabled","disabled");
							}
							return true;
							},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				 }else{
					 return false;
				 };
				
			});	
			
			
			$("#inputForm").validate({
				rules:{
					"quantityOrdered":{
						"required":true
					}
				},
				messages:{
					"quantityOrdered":{"required":'数量不能为空'}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					
					var keyStr="";
					var twoStr="";
					var flag = 1;
					var flagInt=1;
					$("#contentTable tbody tr").each(function(){
						var curkeyStr=$(this).find("input[name='productName']").val()+$(this).find("select[name='colorCode']").val()+$(this).find("select[name='countryCode']").val();
						if(flag==1){
							if(keyStr){
								if(keyStr.indexOf(curkeyStr+",")>=0){
									twoStr=curkeyStr;
									flag = 2;
								}else{
									keyStr=keyStr+curkeyStr+",";
								};
							}else{
								keyStr = curkeyStr+",";
							};						
						};
					});
					
					if(flag==2){
						top.$.jBox.tip("相同产品颜色国家只能有一条记录 ！"+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					$("input[name='quantityOrdered']").each(function(e){
						var tr = $(this).parent().parent();
						var packingQuantity = tr.find(".zhuangxiangno").val();
						var a=$(this).val()%packingQuantity;
						if(a!=0){
							twoStr=tr.find("input[name='productName']").val()+tr.find("select[name='colorCode']").val()+tr.find("select[name='countryCode']").val();
							flagInt=2;
							return;
						};
					});
					
					if(flagInt==2){
						top.$.jBox.tip("数量必须为装箱数的倍数！"+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					//比hidden name=productName的值，看与现在的时候相同 ；不相同 就把该tr里的已接收数  未接收数 置0，当做新增
					 
					
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
					
					$("option[value='No color']").each(function(){
						$(this).val("");
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
		<li ><a href="${ctx}/psi/lcPurchaseOrder/">采购订单列表</a></li>
		<li class="active"><a href="#">快速新建采购订单</a></li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="purchaseOrder" action="${ctx}/psi/lcPurchaseOrder/fastSave" method="post" class="form-horizontal">
	    <input type="hidden" name="toReview" 			value="${purchaseOrder.toReview}" id="toReview"/>
	    <input type="hidden" name="oldItemIds" 			value="${purchaseOrder.oldItemIds}" id="oldItemIds"/>
	    <input type="hidden" name="isOverInventory" 	value="${purchaseOrder.isOverInventory}" />
	    <input type="hidden" name="overRemark" 		    value="${purchaseOrder.overRemark}" />
	    <input type="hidden" name="receivedStore"       value="中国本地A" />
	    <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:100%">
		
		<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label"><b>供应商</b>:</label>
				<div class="controls" >
				<span>
					<select style="width:150px;" id="supplier" name="supplier.id">
						<c:forEach items="${suppliers}" var="supplier" varStatus="i">
							 <option value='${supplier.id}' ${purchaseOrder.supplier.id eq supplier.id ?'selected' :''}>${supplier.nikename}</option>;
						</c:forEach>
					</select>
				</span>
				
				</div>
			</div>
			<div class="control-group"  style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>定金</b>:</label>
				<div class="controls" >
					<div class="input-prepend input-append">
						<input  type="text" class="number required" style="width:40%" name="deposit" value="${purchaseOrder.deposit}" /><span class="add-on">%</span>
					</div>
				</div>
			</div>
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<div class="controls">
				</div>
			</div>
			
			
		</div>	
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>跟单员</b>:</label>
				<div class="controls">
					<select style="width: 70%" id="merchandiser" class='merchandiser' name="merchandiser.id" >
					<c:forEach items="${users}" var="user" varStatus="i">
							 <option value='${user.id}' ${user.id eq purchaseOrder.merchandiser.id?'selected':'' }>${user.name}</option>;
					</c:forEach>
				</select>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>下单日期</b>:</label>
				<div class="controls">
					<input  type="text" style="width:60%" name="purchaseDate"  class="Wdate"  value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd" />" />
				</div>
			</div>
			
			
			
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<label class="control-label"><b>货币类型</b>:</label>
				<div class="controls">
					<select name="currencyType" class="required">
						<option value="USD" ${purchaseOrder.currencyType eq 'USD'?'selected':''}>USD</option>
						<option value="CNY" ${purchaseOrder.currencyType eq 'CNY'?'selected':''}>CNY</option>
					</select>
				</div>
			</div>
			
		</div>
		<div style="float:left;width:100%">
		 <blockquote>
			 <div style="float: left"><p style="font-size: 14px">产品信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加产品</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">产品名称</th>
				   <th style="width: 5%">装箱数量</th>
				   <th style="width: 10%">PO交货</th>
				   <th style="width: 10%">预计交货</th>
				   <th style="width: 10%">颜色</th>
				   <th style="width: 10%">国家</th>
				   <th style="width: 6%">总数量</th>
				   <th style="width: 6%">线下数量</th>
				   <th style="width: 15%">备注</th>
				   <th style="width: 20%">操作</th>
				   
			</tr>
		</thead>
		
		<c:if test="${not empty purchaseOrder.items}" >
		<tbody>
			<c:forEach items="${purchaseOrder.items}" var="item" >
					<tr>
						<td class="product">
						<input type="hidden" name="id" value="${item.id}" />
						<select style="width: 90%" id="product" class='productId' name="product.id" >
							<c:forEach items="${purchaseOrder.supplier.products}" var="productSupplier" varStatus="i">
								<c:if test="${productSupplier.product.isSale ne '4' }">
									<option value='${productSupplier.product.id}'  ${productSupplier.product.id eq item.product.id ?'selected':''}>${productSupplier.product.name}</option>;
								</c:if>
							</c:forEach>
						</select>
						<input type="hidden" name="productName" value="${item.productName}"/>
						</td>
						<td ><input type="text" readonly="readonly" maxlength="11" style="width: 80%" class="zhuangxiangno" value="${item.product.packQuantity}"/></td>
						<td ><input style="width: 80%"  readonly="readonly" type="text"  name="deliveryDate"   id="deliveryDate"  value="<fmt:formatDate value="${item.deliveryDate}" pattern="yyyy-MM-dd"/>"/> </td>
						<td ><input style="width: 80%"  type="text"  class="Wdate required"  name="actualDeliveryDate"   id="actualDeliveryDate"  pattern="yyyy-MM-dd" value="<fmt:formatDate value="${item.actualDeliveryDate}" pattern="yyyy-MM-dd"/>"/> </td>
						<td ><span id="masters">
						<select name="colorCode" id="colorCode"  class="multiSelect" style="width:100%">
							<c:if test="${fn:length(item.colorList)>0}">
								<c:forEach items="${item.colorList}" var="color" varStatus="i">
										 <option value='${color}'  ${color eq item.colorCode ?'selected':''}>${color}</option>;
								</c:forEach>
							</c:if>
							<c:if test="${fn:length(item.colorList)==0}">
								 <option value='No color'  selected>No color</option>;
							</c:if>
						</select>
						</span></td>
						<td ><span id="masters">
						<select name="countryCode" id="countryCode"  class="multiSelect"  style="width:100%">
						<c:forEach items="${item.countryList}" var="country" varStatus="i">
									 <option value='${country}'  ${country eq item.countryCode ?'selected':''}>${fns:getDictLabel(country, 'platform', '')}</option>;
							</c:forEach>
						</select>
						</span></td>
						<td ><input type="text" maxlength="11" style="width: 80%" name="quantityOrdered" class="number" value="${item.quantityOrdered}"/></td>
						<td ><input type="text" maxlength="11" style="width: 80%" name="quantityOffOrdered" class="number" value="0"/></td>
						<td ><input type="text" maxlength="200" style="width: 80%" name="remark" value="${item.remark}"/></td>
						<td >
						<a href="#" class="remove-row"><span class="icon-minus"></span>删除产品</a>
						</td>
					</tr>
			</c:forEach>
			</tbody>
		</c:if>
		
	</table>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<c:if test="${purchaseOrder.toReview eq'0'}">
				<input id="btnSureSubmit" class="btn btn-primary" type="button" value="申请审核"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
