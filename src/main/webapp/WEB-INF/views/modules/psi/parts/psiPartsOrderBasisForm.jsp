<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件订单付款详情管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					var needFlag=0;
					var poFlag=0;
					var stockFlag=0;
					var toFrozen=0;
					var partsName="";
					$("#contentTable tbody tr .needQuantiy").each(function(){
						var tr =$(this).parent().parent();
						var need =$(this).val();
						var po =0;
						var stock=0;
						var order=0;
						
						var poNot =tr.find("input[name='poNotFrozen']").val();
						var stockNot =tr.find("input[name='stockNotFrozen']").val();
						
						if(tr.find("input[name='poFrozen']").val()!=""){
							po=tr.find("input[name='poFrozen']").val();
						}
						if(tr.find("input[name='stockFrozen']").val()!=""){
							stock =tr.find("input[name='stockFrozen']").val();
						}
						if(tr.find("input[name='orderQuantity']").val()!=""){
							order =tr.find("input[name='orderQuantity']").val();
						}
						
						partsName=tr.find("input[name='partsName']").val();
						
						if(need-po-stock-order>0){
							needFlag=1;
							return;
						}
						if(po-poNot>0){
							poFlag=1;
							return;
						}
						
						if(stock-stockNot>0){
							stockFlag=1;
							return;
						}
						
						if(parseInt(stock)+parseInt(po)>need){
							toFrozen=1;
							return;
						}
						
					});
					
					if(needFlag==1){
						top.$.jBox.tip(partsName+",转移到PO冻结数+转移到库存冻结数+下单数>=需要数","info",{timeout:3000});
						return false;
					}
					
					if(poFlag==1){
						top.$jBox.tip(partsName+",PO非冻结数>=转移到PO冻结数","info",{timeout:3000});
						return false;
					}
					
					if(stockFlag==1){
						top.$.jBox.tip(partsName+",库存非冻结数>=转移到库存冻结数","info",{timeout:3000});
						return false;
					}
					
					if(toFrozen==1){
						top.$.jBox.tip(partsName+",库存冻结数和po冻结数不能大于需要数","info",{timeout:3000});
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
	<form:form id="inputForm" modelAttribute="psiPartsOrderBasisTotal" action="${ctx}/psi/psiPartsOrderBasis/save" method="post" class="form-horizontal">
	
	<div style="padding:10px;font-size:20px;font-weight: bold;text-align:center">产品订单号：${basisTotal.purchaseOrderNo}</div>
	 <blockquote style="float: left">
			 <div style="float: left"><p style="font-size: 14px"><b>配件下单信息</b></p></div><div style="float: left" id=errorsShow></div>
	 </blockquote>
	 
	 <div style="float:left;width:100%;">
	 		<div class="control-group"  style="float:left;width:30%" >
				<label class="control-label" style="width:100px">下单日期:</label>
				<div class="controls" style="margin-left:120px">
				<span>
					<input type="text" name="purchaseDate"  class="Wdate" value="<fmt:formatDate value="${basisTotal.purchaseDate}" pattern="yyyy-MM-dd" />" />
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:70%" >
				<label class="control-label" style="width:100px">备注:</label>
				<div class="controls" style="margin-left:120px">
				<span>
					<input type="text" name="remark"  style="width:98%" />
				</span>
				</div>
			</div>
	</div>
		
	 <blockquote style="float: left">
			 <div style="float: left"><p style="font-size: 14px"><b>库存分配信息</b></p></div><div style="float: left" id=errorsShow></div>
	 </blockquote>
	 
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				   <th style="width: 10%">配件名称</th>
				   <th style="width: 15%">订单产品信息<div style="float:right">数量及配比</div></th>
				   <th style="width: 5%">需要数</th>
				   <th style="width: 5%">PO可用数</th>
				   <th style="width: 5%">转移到PO冻结数</th>
				   <th style="width: 5%">库存可用数</th>
				   <th style="width: 5%">转移到库存冻结数</th>
				   <th style="width: 5%">新下单数</th>
				   <th style="width: 10%">收货日期</th>
				   <th style="width: 10%">备注</th>
				</tr>
			</thead>
			<c:if test="${not empty basisTotal.items}" >
				<c:forEach items="${basisTotal.items}" var="basises" >
					<tbody>
						<tr>
							<td>
								<input type="hidden" name="purchaseOrderId" value="${basises.purchaseOrderId}"/>
								<input type="hidden" name="purchaseOrderNo" value="${basises.purchaseOrderNo}"/>
								<input type="hidden" name="partsId" value="${basises.partsId}"/>
								<input type="hidden" name="supplierId" value="${basises.supplierId}"/>
								<a target="_blank"  href="${ctx}/psi/psiParts/view?id=${basises.partsId}"><input type="text" style="width: 80%" name="partsName" value="${basises.partsName}" readonly /></a>
							</td>
							<td>
							<c:forEach items="${partsProMap[basises.partsId]}" var="productEntry">
								<a target="_blank" href="${ctx}/psi/product/view?id=${productEntry.value}">${productEntry.key}</a>&nbsp;<div style="color:red;float:right">
								${productNameColorMap[productEntry.key]}*
								<c:set value="${productEntry.key},${basises.partsId}" var="key"></c:set>
								${proNameColorPartsMap[key]}
								</div><br/>   
							</c:forEach>
							</td>
							<td><input type="text" style="width: 80%" name="needQuantity"   readonly value="${basises.needQuantity}" class="needQuantiy"/></td>
							<td><input type="text" style="width: 80%" name="poNotFrozen"    readonly value="${basises.poNotFrozen}"/></td>
							<td><input type="text" maxlength="11" style="width: 80%" name="poFrozen"  ${basises.poNotFrozen eq '0'?'readonly':''}   class="number" value="${basises.poFrozen}"/></td>
							<td><input type="text" style="width: 80%" name="stockNotFrozen"  readonly value="${basises.stockNotFrozen}"/></td>
							<td><input type="text" maxlength="11" style="width: 80%" name="stockFrozen" ${basises.stockNotFrozen eq '0'?'readonly':''} class="number" value="${basises.stockFrozen}"/></td>
							<td><input type="text" maxlength="11" style="width: 80%" name="orderQuantity" class="number" value="${basises.orderQuantity}"/></td>
							<td><input type="text" style="width: 80%" required="required" name="deliveryDate" class="Wdate" value="<fmt:formatDate value="${basises.deliveryDate}" pattern="yyyy-MM-dd" />"/></td>
							<td><input type="text" style="width: 80%" name="remark" value="${basises.remark}"/></td>
						</tr>
					</tbody>
				</c:forEach>
			</c:if>
		</table>
		
		<div class="form-actions" style="padding:10px 0px 10px 0px;text-align:center">
			<input id="btnSubmit" class="btn btn-primary"  type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
