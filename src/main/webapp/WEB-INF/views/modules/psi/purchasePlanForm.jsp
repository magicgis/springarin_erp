<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='psi_purchase_order'/>--<spring:message code='sys_but_add'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	 
		
		$(document).ready(function() {
			new tabTableInput("inputForm","text");
		
			eval('var saleMap = ${saleMap}');
			eval('var packInfoMap = ${packInfoMap}');
			
			
			$("#inputForm").on("change",".productNameColor",function(e){
				var  tr = $(this).parent().parent();
				var productNameColor =$(this).val();
				tr.find("select[name='countryCode']").select2("data",[]);
				tr.find("select[name='countryCode']").select2().empty();
				var countrys =saleMap[productNameColor];
				if(packInfoMap[productNameColor]){
					var proInfo = packInfoMap[productNameColor].split(",");
					var color="";
					if(productNameColor.indexOf("_")>0){
						var arr =productNameColor.split("_");
						productNameColor=arr[0];
						color = arr[1];
					}     
					
					tr.find("input[name='productName']").val(productNameColor);
					tr.find("input[name='colorCode']").val(color);
					tr.find("input[name='product.id']").val(proInfo[0]);
					
					var options ="";
					$(countrys).each(function(i,data){
						options=options+("<option value='"+data+"'>"+data+"</option>");
					}); 
					tr.find("select[name='countryCode']").select2().append(options).change();
					tr.find(".packQuantity").val(proInfo[1]);
					tr.find(".moq").val(proInfo[2]);
				}
			});
			
			$(".remove-row").live('click',function(e){
				  e.preventDefault();
				  var tr = $(this).parent().parent();
				  tr.remove();
			});
			
			$('#add-row').on('click', function(e){
			    e.preventDefault();
			    var table = $('#contentTable');
	            var tr = $("<tr></tr>");
	            var options = "" ;
	            for (var key in saleMap) {
	            	options = options.concat("<option value='"+key+"'>"+key+"</option>");
				}
	            tr.append("<td><input type='hidden' name='productName'/><input type='hidden' name='colorCode'/><input type='hidden' name='product.id'/><select style='width: 90%' class='productNameColor'  >"+options+ "</select></td>");
	            tr.append("<td> <span><select name='countryCode'   style='width:100%'/></span></td>");
	            tr.append("<td> <input type='text' readonly='readonly' class='moq' style='width: 80%' /></td>");
	            tr.append("<td> <input type='text' readonly='readonly' class='packQuantity' style='width: 80%' /></td>");
	            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='quantity' class='number' /></td>");
	            tr.append("<td> <input name='attFile' type='file'  /></td>"); 
	            tr.append("<td> <input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");   
	            tr.append("<td> <a href='#'  class='remove-row'><span class='icon-minus'></span><spring:message code='sys_but_delete'/></a></td>");
	            table.append(tr);
	            tr.find("select.productNameColor").select2().change();
			});
			
			if('${purchasePlan.id}'==''){
				$('#add-row').click();
			}
			
			
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			
			$("#btnSureSubmit").on('click',function(e){
				 if($("#inputForm").valid()){
					 top.$.jBox.confirm('确认要申请审核？申请后将发送邮件通知审核人！','系统提示',function(v,h,f){
							if(v=='ok'){
								$("#planSta").val("2");
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
				rules:{
					"countryCode":{
						"required":true,
					},
					"quantity":{
						"required":true
					}
				},
				messages:{
					"countryCode":{"required":"国家不能为空"},
					"quantity":{"required":"数量不能为空"}
				},
				submitHandler: function(form){
					
					var total=0;
					
					$("input[name='quantity']").each(function(e){
						total=parseInt(total)+parseInt($(this).val());
					});
					
					if(total==0){
						top.$.jBox.tip("总采购数量必须大于0", 'info',{timeout:3000});
						return false;
					}
					
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
					$("#btnSureSubmit").attr("disabled","disabled");
					
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("error");
					error.appendTo($("#errorsShow"));
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/purchasePlan/">新品采购计划列表</a></li>
		<li class="active"><a href="#">${empty purchasePlan.id?'新增':'编辑'}新品采购计划</a></li>
	</ul>
	<br/>
	
	<form:form id="inputForm" modelAttribute="purchasePlan" action="${ctx}/psi/purchasePlan/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id"     				value="${purchasePlan.id}" />
	    <input type="hidden" name="oldItemIds"			value="${purchasePlan.oldItemIds}" />
	    <input type="hidden" name="createUser.id"		value="${purchasePlan.createUser.id}" />
	    <input type="hidden" name="planSta" id="planSta" value="${purchasePlan.planSta}" />
	    <input type="hidden" name="attFilePath"  		value="${purchasePlan.attFilePath}" />
	    <input type="hidden" name="createDate" 			value="<fmt:formatDate pattern='yyyy-MM-dd' value='${purchasePlan.createDate}'/>" />
	    <blockquote>
			<p style="font-size: 14px"><b>备注：</b></p>
		</blockquote>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:100%" >
				<label class="control-label" style="width:80px">备注:</label>
				<div class="controls" style="margin-left:100px">
					<textarea name="remark"  rows="4" cols="4" maxlength="255" style="width:90%" >${purchasePlan.remark}</textarea>
				</div>
			</div>
		</div>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:100%" >
				<label class="control-label" style="width:80px">产品定位:</label>
				<div class="controls" style="margin-left:100px">
				<form:select path="productPosition" class="required">
					<form:option value="" label="请选择"/>
					<c:forEach items="${fns:getDictList('product_position')}" var="dic">
						<c:if test="${dic.value ne '4'}">
							 <form:option value="${dic.value}">${dic.label}</form:option>
						</c:if>      
					</c:forEach>
					<%--<form:options items="${fns:getDictList('product_position')}" itemLabel="label" itemValue="value" htmlEscape="false"/> --%>
				</form:select>
				<%--<select name="productPosition" class="required">
					<option value="" >请选择</option>
					<option value="爆款" 		${purchasePlan.productPosition eq '爆款'?'selected':'' }>爆款</option>
					<option value="走量款"    ${purchasePlan.productPosition eq '走量款'?'selected':'' }>走量款</option>
					<option value="利润款"    ${purchasePlan.productPosition eq '利润款'?'selected':'' }>利润款</option>
					<option value="品类补充款"  ${purchasePlan.productPosition eq '品类补充款'?'selected':'' }>品类补充款</option>
				</select> --%>
				</div>
			</div>
		</div>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:100%" >
				<label class="control-label" style="width:80px">销售计划:</label>
				<div class="controls" style="margin-left:100px">
					<c:if test="${not empty purchasePlan.attFilePath}"><a href="${ctx}/psi/purchasePlan/download?fileName=/${purchasePlan.attFilePath}&productName=${purchasePlan.productName}">查看</a></c:if>
						<input name="attFile" type="file"  />
				</div>
			</div>
		</div>
						
		<div style="float:left;width:100%">
		 <blockquote>
		 <div style="float: left"><p style="font-size: 14px"><b><spring:message code='psi_product_productInfo'/>:</b></p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 15%"><spring:message code='psi_product_name'/></th>
				   <th style="width: 5%"><spring:message code='sys_label_country'/></th>
				   <th style="width: 5%">MOQ</th>
				   <th style="width: 5%">装箱数</th>
				   <th style="width: 10%">采购数量</th>
				   <th style="width: 20%">备注</th>
				   <th style="width: 10%"><spring:message code='sys_label_tips_operate'/></th>
				   
			</tr>
		</thead>
			<c:if test="${not empty purchasePlan.id}">
				<c:forEach items="${purchasePlan.items}" var="item" >
					<tr>
						<td>
							<input type="hidden" name="id" value="${item.id}" />
							<input type="hidden" name="plan.id" value="${item.plan.id}" />
							<input type="hidden" name="colorCode" value="${item.colorCode}"/>
							<input type="hidden" name="countryCode" value="${item.countryCode}"/>
							<input type="hidden" name="productName" value="${item.productName}"/>
							<input type="hidden" name="product.id" value="${item.product.id}"/>
							<input type="hidden" name="delFlag" value="${item.delFlag}"/>
							${item.productNameColor}
						</td>
						<td >
							${fns:getDictLabel(item.countryCode, 'platform', '')}
						</td>
						<td>${item.product.minOrderPlaced}</td>
						<td>${item.product.packQuantity}</td>
						<td><input type="text" maxlength="11" style="width: 80%" name="quantity" class="number" value="${item.quantity}"/></td>
						<td><input name="remark"  type="text"  value="${item.remark}" style="width: 80%"/></td>
						<td><a href="#" class="remove-row"><span class="icon-minus"></span><spring:message code='sys_but_delete'/></a></td>
					</tr>
			</c:forEach>
		</c:if>
	</table>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-info" type="submit" value="<spring:message code='sys_but_save'/>"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSureSubmit" class="btn btn-primary" type="button" value="申请审核"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
