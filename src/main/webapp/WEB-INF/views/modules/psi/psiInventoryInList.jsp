<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<html>
<head>
	<title>Stock In Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$("#selectUser,#selectStock,#selectOperation").change(function(){
				$("#searchForm").submit();
			});
			$(".open").click(function(e){
				if($(this).text()=='Summary'){
					$(this).text('Closed');
				}else{
					$(this).text('Summary');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			$("#payExport").click(function(){
				var params = {};
				params.addDate=$("input[name='addDate']").val();
				params.addDateS=$("input[name='addDateS']").val();
				params.operationType=$("input[name='operationType']").val();
				window.location.href = "${ctx}/psi/psiInventoryIn/payExportLc?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$(".inRemark").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.inId = $(this).parent().parent().find(".inId").val();
					param.remark = newValue;
					param.flag="1";
					$.get("${ctx}/psi/psiInventoryIn/updateRemark?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("success！", 'info',{timeout:2000});
						}
					});
					return true;
				}
			});
			
			$(".tranMan").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.inId = $(this).parent().parent().find(".inId").val();
					param.content = encodeURI(newValue);
					param.flag="2";
					$.get("${ctx}/psi/psiInventoryIn/updateRemark?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("success！", 'info',{timeout:2000});
						}
					});
					return true;
				}
			});
			
			$(".carNo").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.inId = $(this).parent().parent().find(".inId").val();
					param.content = encodeURI(newValue);
					param.flag="3";
					$.get("${ctx}/psi/psiInventoryIn/updateRemark?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("success！", 'info',{timeout:2000});
						}
					});
					return true;
				}
			});
			
			$(".phone").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.inId = $(this).parent().parent().find(".inId").val();
					param.content = encodeURI(newValue);
					param.flag="4";
					$.get("${ctx}/psi/psiInventoryIn/updateRemark?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("success！", 'info',{timeout:2000});
						}
					});
					return true;
				}
			});
			
			
		});
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="psiInventoryIn" action="${ctx}/psi/psiInventoryIn/" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
				<label>Operator：</label>
				<select name="addUser.id" style="width: 120px" id="selectUser">
					<option value="">All</option>
					<c:forEach items="${allUser}" var="user">
						<option value="${user.id}" ${psiInventoryIn.addUser.id eq user.id?'selected':''} >${user.name}</option>
					</c:forEach>		
				</select>&nbsp;&nbsp;
				<label>OperationType：</label>
				<select name="operationType" style="width: 220px" id="selectOperation">
					<option value="">All</option>
					<option value="Purchase Storing" ${psiInventoryIn.operationType eq 'Purchase Storing'?'selected':''}>Purchase Storing</option>	
					<option value="Inventory Taking Storing" ${psiInventoryIn.operationType eq 'Inventory Taking Storing'?'selected':''}>Inventory Taking Storing</option>
					<option value="Return Storing" ${psiInventoryIn.operationType eq 'Return Storing'?'selected':''}>Return Storing</option>	
					<option value="Recall Storing" ${psiInventoryIn.operationType eq 'Recall Storing'?'selected':''}>Recall Storing</option>	
					<option value="Transport Storing" ${psiInventoryIn.operationType eq 'Transport Storing'?'selected':''}>Transport Storing</option>	
					<option value="Lot Storing" ${psiInventoryIn.operationType eq 'Lot Storing'?'selected':''}>Lot Storing</option>	
					<option value="other">Other</option>	
				</select>&nbsp;&nbsp;
				
				<label>In-bound Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="addDate" value="<fmt:formatDate value="${psiInventoryIn.addDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
					&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="addDateS" value="<fmt:formatDate value="${psiInventoryIn.addDateS}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
						&nbsp;&nbsp;&nbsp;&nbsp;
			</div>
		<div style="height: 40px;">
			<label>Warehouse：</label>
			<select name="warehouseId" style="width: 120px" id="selectStock">
				<option value="">All</option>
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
			</select>&nbsp;&nbsp;
			<label>In-bound No./TransportNo：</label>
			<input name="billNo"  maxlength="50" type="text" class="input-small" value="${psiInventoryIn.billNo }"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<label>ProductName：</label>
			<input name="tranLocalNo"  maxlength="50" type="text" class="input-small" value="${psiInventoryIn.tranLocalNo }"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<c:if test="${psiInventoryIn.warehouseId eq 130}"></c:if>
			<shiro:hasPermission name="psi:order:financeReview">
				<input id="payExport" class="btn btn-warning" type="button" value="导出(财务)"/>
			</shiro:hasPermission>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search"/>
		</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">   
		<thead><tr><th style="width:5%" >No.</th><th style="width:10%">In-bound No.</th><th style="width:6%">Transport<br/> Model</th>
		<th style="width:6%">Warehouse</th><th style="width:10%">Operator Type</th><th style="width:6%">In-bound Date</th>
		<shiro:hasPermission name="psi:inventory:edit:CN"><th style="width:10%">Remark</th><th style="width:5%">送货人</th><th style="width:10%">电话</th><th style="width:5%">车牌号</th></shiro:hasPermission>
		<th style="width:10%">Operate</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiInventoryIn">
			<tr>
				<td>${psiInventoryIn.id}</td><td><a href="${ctx}/psi/psiInventoryIn/view?id=${psiInventoryIn.id}">${psiInventoryIn.billNo}</a></td>
				<td>${localTranModel[psiInventoryIn.id]}</td>
				<td>
				<c:choose>
					<c:when test="${fn:startsWith(psiInventoryIn.warehouseName, '德国')}">Germany</c:when>
					<c:when test="${fn:startsWith(psiInventoryIn.warehouseName, '中国')}">China</c:when>
					<c:when test="${fn:startsWith(psiInventoryIn.warehouseName, '美国')}">American</c:when>
					<c:otherwise>${psiInventoryIn.warehouseName}</c:otherwise>
				</c:choose>
				</td>
				<td>${psiInventoryIn.operationType}
				<c:if test="${psiInventoryIn.operationType eq 'Lot Storing'}">(<font color="red">${psiInventoryIn.originName}</font>)</c:if>
				</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${psiInventoryIn.addDate}"/></td>
				<shiro:hasPermission name="psi:inventory:edit:CN">
					<td>
						<input type="hidden" class="inId" value="${psiInventoryIn.id}" />
						<a href="#" class="inRemark"  data-type="text" data-pk="1" data-title="Enter Remark" data-value="${psiInventoryIn.remark}">${psiInventoryIn.remark}</a>
					</td>
					<td>
						<a href="#" class="tranMan"  data-type="text" data-pk="1" data-title="请填写送货人姓名" data-value="${psiInventoryIn.tranMan}">${psiInventoryIn.tranMan}</a>
					</td>
					<td>
						<a href="#" class="carNo"  data-type="text" data-pk="1" data-title="请填写车牌号" data-value="${psiInventoryIn.carNo}">${psiInventoryIn.carNo}</a>
					</td>
					<td>
						<a href="#" class="phone"  data-type="text" data-pk="1" data-title="请填写送货人电话" data-value="${psiInventoryIn.phone}">${psiInventoryIn.phone}</a>
					</td>
				</shiro:hasPermission>
				
				
				<td>
					<input type="hidden" value="${psiInventoryIn.id}"/>
					<a class="btn btn-small btn-info open">Summary</a>
					<a class="btn btn-small"  href="${ctx}/psi/psiInventoryIn/view?id=${psiInventoryIn.id}">View</a>
					<c:if test="${psiInventoryIn.warehouseId eq 130 && psiInventoryIn.operationType eq 'Purchase Storing'}">
						<a class="btn btn-small btn-success" href="${ctx}/psi/psiInventoryIn/expInBound?id=${psiInventoryIn.id}">打印入库单</a>
					</c:if>
				</td>
			
			</tr>
			<c:if test="${fn:length(psiInventoryIn.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${psiInventoryIn.id}"><td></td><td>Product</td><td>Sku</td><td>Country</td><td>Color</td><td>Type</td><td>Quantity</td><td>TimelyInventoryQuantity</td><td colspan="5"></td></tr>
				<c:forEach items="${psiInventoryIn.viewItems}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${psiInventoryIn.id}">
					<td></td><td>${item.productName}</td><td>${item.sku}</td><td>${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
					<td><a class="btn btn-warning" style="height:16px;width:20px;padding:0px;"  target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}${item.colorCode !=''?'_':''}${item.colorCode}"><span class="icon-search"></span></a>&nbsp;${item.colorCode}</td>
					<td>${item.qualityType}</td><td>${item.quantity}</td><td>${item.timelyQuantity}</td><td colspan="5"></td>
					</tr>
				</c:forEach>
			</c:if>
					
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
