<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购金额调整管理</title>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			eval('var nameMap=${nameMap}');
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			var proName="${lcPurchaseAmountAdjust.productNameColor}";
			$("#supplierId").on("change",function(){
				var params = {};
				params['supplier.id'] = $(this).val();
				window.location.href = "${ctx}/psi/lcPurchaseAmountAdjust/form?"+$.param(params);
			});
			
			$(".icon-remove").on("click",function(){
				$(this).parent().css("display","none");
			});
			
			$('#myfileupload').MultiFile({
				max : 30,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$("#purchaseOrderId").on("change",function(){
				var purchaseOrderId =$(this).val();
				var opts="";
				var nameArray=nameMap[purchaseOrderId];
				$("input[name='orderNo']").val($(this).children("option:selected").text());
				if(nameArray){
					for(var i in nameArray){
						if(nameArray[i]==proName){
							opts+="<option value='"+nameArray[i]+"' selected >"+nameArray[i]+" </option>";
						}else{
							opts+="<option value='"+nameArray[i]+"'>"+nameArray[i]+" </option>";
						}
					}
					$("#productNameColor").select2().empty();
					$("#productNameColor").append(opts);
					$("#productNameColor").select2().change();
				}
			});
			
			$("#btnSureSubmit").on('click',function(e){
				 if($("#inputForm").valid()){
					 top.$.jBox.confirm('确认要申请审核？申请后将发送邮件通知审核人！','系统提示',function(v,h,f){
							if(v=='ok'){
								$("#toReview").val("a");
								$("#inputForm").submit();
							}
							return true;
							},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				 }else{
					 return false;
				 };
				
			});	
			
			
			$("#purchaseOrderId").change();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					var filePath="";
					$(".icon-remove").each(function(){
						if($(this).parent().css("display")!='none'){
							filePath=filePath+$(this).attr("type")+",";
						};
					});
					
					$("input[name='filePath']").val(filePath.substr(0,filePath.length-1));
					
					form.submit();
					$("#btnSureSubmit").attr("disabled","disabled");
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
		<li><a href="${ctx}/psi/lcPurchaseAmountAdjust/">(理诚)采购金额调整列表</a></li>
		<li class="active"><a href="#">(理诚)${not empty lcPurchaseAmountAdjust.id?'修改':'添加'}采购金额调整</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="lcPurchaseAmountAdjust" action="${ctx}/psi/lcPurchaseAmountAdjust/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id"            value="${lcPurchaseAmountAdjust.id}"/>
		<input type="hidden" name="adjustSta"     value="0" id="toReview"/>
		<input type="hidden" name="createUser.id" value="${lcPurchaseAmountAdjust.createUser.id}"/>
		<input type="hidden" name="orderNo"       value="${lcPurchaseAmountAdjust.orderNo}"/>
		<input type="hidden" name="filePath"       value="${lcPurchaseAmountAdjust.filePath}"/>
		<input type="hidden" name="createDate"    value="<fmt:formatDate pattern='yyyy-MM-dd hh:mm:ss' value='${lcPurchaseAmountAdjust.createDate}'/>"/>
		<div class="control-group">
			<label class="control-label">供应商:</label>
			<div class="controls">
				<select name="supplier.id"  id="supplierId" style="width:41%">
					<c:forEach items="${supplierMap}" var="supplierEntry">
						<option value="${supplierEntry.key}" ${supplierEntry.key eq lcPurchaseAmountAdjust.supplier.id ?'selected':'' } >${supplierEntry.value}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">货币类型:</label>
			<div class="controls">
				<input name="currency"  type="text" readonly="readonly" style="width:40%" value="${lcPurchaseAmountAdjust.currency}" />
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">订单号:</label>
			<div class="controls">
				<select name="purchaseOrderId"  id="purchaseOrderId" style="width:41%" class="required" >
					<c:forEach items="${orderMap}" var="orderEntry">
						<option value="${orderEntry.key}" ${orderEntry.key eq lcPurchaseAmountAdjust.purchaseOrderId ?'selected':'' } >${orderEntry.value}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">产品:</label>
			<div class="controls">
				<select name="productNameColor"  id="productNameColor" style="width:41%" class="required"></select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">主题:</label>
			<div class="controls">
				<input name="subject"  type="text" maxlength="100" style="width:40%" value="${lcPurchaseAmountAdjust.subject}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">调整金额(可输入负号):</label>
			<div class="controls">
				<input name="adjustAmount"  type="text" maxlength="100" class="required price" style="width:40%" value="${lcPurchaseAmountAdjust.adjustAmount}" />
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" />
			</div>
		</div>
		
		
		<c:if test="${not empty lcPurchaseAmountAdjust.filePath}">
		<div class="control-group" >
		<label class="control-label" >已上传附件:</label>
			<div class="controls">
				<c:forEach items="${fn:split(lcPurchaseAmountAdjust.filePath,',')}" var="attchment" varStatus="i">
					<span><a target="_blank" href="<c:url value='/data/site/${attchment}'/>">附件 ${i.index+1}</a><span class="icon-remove" type="${attchment}"></span></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach> 
			</div>  
		</div>
		</c:if>
		
		<div class="control-group" >
		<label class="control-label" style="height:100px">附件:</label>
			<div class="controls">
				<span class="help-inline">支持多附件</span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="attchmentFile" type="file" id="myfileupload" />
			</div>
		</div>
		
		
		<div class="form-actions">
			<shiro:hasPermission name="psi:purchaseAdjust:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
				<input id="btnSureSubmit" class="btn btn-primary" type="button" value="申请审核"/>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</shiro:hasPermission>
		</div>
	</form:form>
</body>
</html>
