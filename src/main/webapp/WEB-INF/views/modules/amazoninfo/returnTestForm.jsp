<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>退货检测登记管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			eval('var skuMap=${skuMap}');
			$("#selectStock").on("change",function(e){
				var params = {};
				params['warehouseId'] = $(this).val();
				params['id'] = $("input[name='id']").val();
				window.location.href = "${ctx}/amazoninfo/returnTest/form?"+$.param(params);
			});
			
			$("#selectSku").on("change",function(e){
				var sku = $(this).val();
				var skuPo = skuMap[sku];
				$("input[name='productName']").val(skuPo.productName);
			});
			
			$("#selectSku").change();
			
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$("#btnSureSubmit").on('click',function(e){
				var total = 0;
				if($(".newQuantity").val()!=''){
					total=total+parseInt($(".newQuantity").val());
				}
				if($(".renewQuantity").val()!=''){
					total=total+parseInt($(".renewQuantity").val());
				}
				if($(".oldQuantity").val()!=''){
					total=total+parseInt($(".oldQuantity").val());
				}
				if($(".brokenQuantity").val()!=''){
					total=total+parseInt($(".brokenQuantity").val());
				}
				if($(".quantity").val()<total){
					top.$.jBox.tip("(new + old +renew +broken) cannot >quantity","info",{timeout:3000});
					return false;
				}
				 if($("#inputForm").valid()){
					 top.$.jBox.confirm('Are you sure to in-bound ？','System Tips',function(v,h,f){
							if(v=='ok'){
								$("#inStockSta").val("1");
								$("#inputForm").submit();
							}
							return true;
							},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				 }else{
					 return false;
				 };
				
			});	
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
					var total = 0;
					if($(".newQuantity").val()!=''){
						total=total+parseInt($(".newQuantity").val());
					}
					if($(".renewQuantity").val()!=''){
						total=total+parseInt($(".renewQuantity").val());
					}
					if($(".oldQuantity").val()!=''){
						total=total+parseInt($(".oldQuantity").val());
					}
					if($(".brokenQuantity").val()!=''){
						total=total+parseInt($(".brokenQuantity").val());
					}
					if($(".quantity").val()<total){
						top.$.jBox.tip("(new + old +renew +broken) cannot >quantity","info",{timeout:3000});
						return false;
					}
					
					$("input[name='warehouseName']").val($("#selectStock").find("option:selected").text());
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
					$("#btnSureSubmit").attr("disabled","disabled");
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
		<li><a href="${ctx}/amazoninfo/returnTest/">ReturnTestList</a></li>
		<li class="active"><a href="#">${not empty returnTest.id?'Edit':'Add'} ReturnTest </a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="returnTest" action="${ctx}/amazoninfo/returnTest/save" method="post" class="form-horizontal">
		<input type="hidden" name="id"			  value="${returnTest.id}"/>
		<input type="hidden" name="testSta" 	  value="${returnTest.testSta}"/>
		<input type="hidden" name="productName"   value="${returnTest.productName}"/>
		<input type="hidden" name="warehouseName" value="${returnTest.warehouseName}"/>
		<input type="hidden" name="createUser.id" value="${returnTest.createUser.id}"/>
		<input type="hidden" name="inStockSta" id="inStockSta" value="0"/>
		<input type="hidden" name="createDate"    value="<fmt:formatDate pattern='yyyy-MM-dd hh:mm:ss' value='${returnTest.createDate}'/>"/>
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">Base Info.</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:100px">WareHouse:</label>
				<div class="controls" style="margin-left:120px">
					<select name="warehouseId" id="selectStock" >
						<c:forEach items="${stocks}" var="stock">
							<option value="${stock.id}" ${returnTest.warehouseId eq stock.id?'selected':''} >
							<c:choose>
								<c:when test="${stock.countrycode eq 'DE' }">Germany</c:when>
								<c:when test="${stock.countrycode eq 'CN' }">China</c:when>
								<c:when test="${stock.countrycode eq 'US' }">American</c:when>
								<c:otherwise>${stock.stockName}</c:otherwise>
							</c:choose>
							</option>
						</c:forEach>		
					</select>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:100px">Product:</label>
				<div class="controls" style="margin-left:120px;">
					<select name="sku" style="width:100%" id="selectSku">
						<c:forEach items="${skus}" var="skuEntry">
							<option value="${skuEntry.key}" ${skuEntry.key eq returnTest.sku?'selected':''}> ${skuEntry.value.productName}[${skuEntry.value.sku}]</option>
						</c:forEach>
					</select>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:40%;height:30px">
				<label class="control-label" style="width:100px">Quantity:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="quantity" value="${returnTest.quantity}" class="required number quantity"/>   
				</div>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%;">
			<label class="control-label" style="width:100px">ReasonDetail:</label>
			<div class="controls" style="margin-left:120px;">
				<form:textarea path="reasonDetail" htmlEscape="false" rows="4" maxlength="200" style="width:98%; height: 60px;" />
			</div>
		</div>
		
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">Stock-In Info.</p><div style="float: left" id=errorsShow></div>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">New:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="newQuantity" value="${returnTest.newQuantity}" class="number newQuantity"/>   
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">Renew:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="renewQuantity" value="${returnTest.renewQuantity}" class="number renewQuantity"/>   
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">Old:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="oldQuantity" value="${returnTest.oldQuantity}" class="number oldQuantity"/>   
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">Broken:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="brokenQuantity" value="${returnTest.brokenQuantity}" class="number brokenQuantity"/>   
				</div>
			</div>
		</div>
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSureSubmit" class="btn btn-primary" type="button" value="Save And In-bound" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
