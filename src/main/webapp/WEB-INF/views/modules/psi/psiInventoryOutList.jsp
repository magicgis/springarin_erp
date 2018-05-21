<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<html>
<head>
	<title>Stock Out Manager</title>
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
				window.location.href = "${ctx}/psi/psiInventoryOut/payExportLc?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$(".outRemark").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.outId = $(this).parent().parent().find(".outId").val();
					param.content = encodeURI(newValue);
					param.flag="1";
					$.get("${ctx}/psi/psiInventoryOut/updateRemark?"+$.param(param),function(data){
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
					param.outId = $(this).parent().parent().find(".outId").val();
					param.content = encodeURI(newValue);
					param.flag="2";
					$.get("${ctx}/psi/psiInventoryOut/updateRemark?"+$.param(param),function(data){
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
					param.outId = $(this).parent().parent().find(".outId").val();
					param.content = encodeURI(newValue);
					param.flag="3";
					$.get("${ctx}/psi/psiInventoryOut/updateRemark?"+$.param(param),function(data){
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
					param.outId = $(this).parent().parent().find(".outId").val();
					param.content = encodeURI(newValue);
					param.flag="4";
					$.get("${ctx}/psi/psiInventoryOut/updateRemark?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("success！", 'info',{timeout:2000});
						}
					});
					return true;
				}
			});
			
			
			$(".idCard").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.outId = $(this).parent().parent().find(".outId").val();
					param.content = encodeURI(newValue);
					param.flag="5";
					$.get("${ctx}/psi/psiInventoryOut/updateRemark?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("success！", 'info',{timeout:2000});
						}
					});
					return true;
				}
			});
			
			$(".boxNo").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.outId = $(this).parent().parent().find(".outId").val();
					param.content = encodeURI(newValue);
					param.flag="6";
					$.get("${ctx}/psi/psiInventoryOut/updateRemark?"+$.param(param),function(data){
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
	<form:form id="searchForm" modelAttribute="psiInventoryOut" action="${ctx}/psi/psiInventoryOut/" method="post" class="breadcrumb form-search" style="height:80px">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
				<label>Operater：</label>
				<select name="addUser.id" style="width: 120px" id="selectUser">
					<option value="">All</option>
					<c:forEach items="${allUser}" var="user">
						<option value="${user.id}" ${psiInventoryOut.addUser.id eq user.id?'selected':''} >${user.name}</option>
					</c:forEach>		
				</select>&nbsp;&nbsp;
				
				<label>OperationType：</label>
				<select name="operationType" style="width: 220px" id="selectOperation">
					<option value="">All</option>
					<option value="Inventory Taking Delivery" ${psiInventoryOut.operationType eq 'Inventory Taking Delivery'?'selected':''}>Inventory Taking Delivery</option>
					<option value="Off-line-old Delivery" ${psiInventoryOut.operationType eq 'Off-line-old Delivery'?'selected':''}>Off-line-old Delivery</option>	
					<option value="Replacement/Testing Delivery" ${psiInventoryOut.operationType eq 'Replacement/Testing Delivery'?'selected':''}>Replacement/Testing Delivery</option>	
					<option value="Wholesale Delivery" ${psiInventoryOut.operationType eq 'Wholesale Delivery'?'selected':''}>Wholesale Delivery</option>	
					<option value="FBA Delivery" ${psiInventoryOut.operationType eq 'FBA Delivery'?'selected':''}>FBA Delivery</option>	
					<option value="Transport Delivery" ${psiInventoryOut.operationType eq 'Transport Delivery'?'selected':''}>Transport Delivery</option>	
					<option value="Lot Delivery" ${psiInventoryOut.operationType eq 'Lot Delivery'?'selected':''}>Lot Delivery</option>	
					<option value="Offline Delivery" ${psiInventoryOut.operationType eq 'Offline Delivery'?'selected':''}>Offline Delivery</option>
					<option value="track outbound" ${psiInventoryOut.operationType eq 'Offline Delivery'?'selected':''}>track outbound</option>
					<option value="other">Other</option>	
				</select>&nbsp;&nbsp;
			
				<label>Out-bound Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="addDate" value="<fmt:formatDate value="${psiInventoryOut.addDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
					&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="addDateS" value="<fmt:formatDate value="${psiInventoryOut.addDateS}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
						&nbsp;&nbsp;&nbsp;&nbsp;
			</div>
		<div style="height: 40px;">
			<label>Watehouse：</label>
			<select name="warehouseId" style="width: 120px" id="selectStock">
				<option value="">All</option>
				<c:forEach items="${stocks}" var="stock">
					<option value="${stock.id}" ${psiInventoryOut.warehouseId eq stock.id?'selected':''} >
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
			
			
			
			<label>Out-bound No./TransportNo/ShippmentId：</label>
			<input name="billNo" type="text"  maxlength="50" class="input-small" value="${psiInventoryOut.billNo }"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<label>ProductName：</label>
			<input name="tranLocalNo" type="text"  maxlength="50" class="input-small" value="${psiInventoryOut.tranLocalNo }"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<shiro:hasPermission name="psi:order:financeReview">
				<input id="payExport" class="btn btn-warning" type="button" value="导出(财务)"/>
			</shiro:hasPermission>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search"/>
		</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No.</th><th style="width:10%">Out-bound No.</th><th style="width:5%">Transport<br/> Model</th><th style="width:6%">Warehouse</th><th style="width:5%">Operater Type</th><th style="width:8%">Date</th>
		<shiro:hasPermission name="psi:inventory:edit:CN"><th style="width:10%">Remark</th><th style="width:5%">提货人</th><th style="width:10%">电话</th><th style="width:10%">身份证号</th><th style="width:5%">车牌号</th><th style="width:5%">柜号</th></shiro:hasPermission>
		<th style="width:15%">Operate</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiInventoryOut">
			<tr>
				<td>${psiInventoryOut.id}</td><td><a href="${ctx}/psi/psiInventoryOut/view?id=${psiInventoryOut.id}">${psiInventoryOut.billNo}</a></td>
				<td>
					<c:if test="${not empty psiInventoryOut.tranLocalNo}">${psiInventoryOut.tranLocalNo}<br/></c:if>
					<c:if test="${not empty psiInventoryOut.tranFbaNo}">${psiInventoryOut.tranFbaNo}<br/></c:if>
					<c:if test="${not empty localTranModel[psiInventoryOut.id] || not empty fbaTranModel[psiInventoryOut.id]}"></c:if>
				</td>
				<td>
				<c:choose>
					<c:when test="${fn:startsWith(psiInventoryOut.warehouseName, '德国')}">Germany</c:when>
					<c:when test="${fn:startsWith(psiInventoryOut.warehouseName, '中国')}">China</c:when>
					<c:when test="${fn:startsWith(psiInventoryOut.warehouseName, '美国')}">American</c:when>
					<c:otherwise>${psiInventoryOut.warehouseName}</c:otherwise>
				</c:choose>
				</td>
				<td>${psiInventoryOut.operationType}<c:if test="${psiInventoryOut.operationType eq 'Lot Delivery'}">(<font color="red">${psiInventoryOut.originName}</font>)</c:if></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${psiInventoryOut.addDate}"/></td>
				<shiro:hasPermission name="psi:inventory:edit:CN">
					<td>
						<input type="hidden" class="outId" value="${psiInventoryOut.id}" />
						<a href="#" class="outRemark"  data-type="text" data-pk="1" data-title="Enter Remark" data-value="${psiInventoryOut.remark}">${psiInventoryOut.remark}</a>
					</td>
					<td>
						<a href="#" class="tranMan"  data-type="text" data-pk="1" data-title="请填写提货人" data-value="${psiInventoryOut.tranMan}">${psiInventoryOut.tranMan}</a>
					</td>
					<td>
						<a href="#" class="phone"  data-type="text" data-pk="1" data-title="请填写电话" data-value="${psiInventoryOut.phone}">${psiInventoryOut.phone}</a>
					</td>
					<td>
						<a href="#" class="idCard"  data-type="text" data-pk="1" data-title="请填写身份证号" data-value="${psiInventoryOut.idCard}">${psiInventoryOut.idCard}</a>
					</td>
					<td>
						<a href="#" class="carNo"  data-type="text" data-pk="1" data-title="请填写车牌号" data-value="${psiInventoryOut.carNo}">${psiInventoryOut.carNo}</a>
					</td>
					<td>
						<a href="#" class="boxNo"  data-type="text" data-pk="1" data-title="请填写海运柜号" data-value="${psiInventoryOut.boxNo}">${psiInventoryOut.boxNo}</a>
					</td>
				</shiro:hasPermission>
				<td>
					<input type="hidden" value="${psiInventoryOut.id}"/>
					<a class="btn btn-small btn-info open">Summary</a>
					<a class="btn btn-small"  href="${ctx}/psi/psiInventoryOut/view?id=${psiInventoryOut.id}">View</a>
					<c:if test="${not empty psiInventoryOut.pdfFile }">
						<a class="btn btn-small btn-info" href="${ctx}/psi/psiInventoryOut/downloadPdf?id=${psiInventoryOut.id}">Download</a>
					</c:if>
					<shiro:hasPermission name="psi:inventory:edit:CN">
					    <c:if test="${psiInventoryOut.operationType eq 'FBA Delivery'}">
						   <a class="btn btn-small btn-warning" href="${ctx}/psi/psiInventoryOut/goUpdatePdf?id=${psiInventoryOut.id}">Upload PDF</a>
					    </c:if>
					</shiro:hasPermission>
					
					<c:if test="${psiInventoryOut.warehouseId eq 130}">
						<a class="btn btn-small btn-success" href="${ctx}/psi/psiInventoryOut/expOutBound?id=${psiInventoryOut.id}">打印出库单</a>
					</c:if>
				</td>
			</tr>
			<c:if test="${fn:length(psiInventoryOut.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${psiInventoryOut.id}"><td></td><td>Product</td><td>Sku</td><td>Country</td><td>Color</td><td>Type</td><td>Quantity</td><td>TimelyInventoryQuantity</td><td colspan="5"></td></tr>
				<c:forEach items="${psiInventoryOut.viewItems}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${psiInventoryOut.id}">
					<td></td><td>${item.productName}</td><td>${item.sku}</td><td>${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
					<td><a class="btn btn-warning" style="height:16px;width:20px;padding:0px;"  target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}${item.colorCode !=''?'_':''}${item.colorCode}"><span class="icon-search"></span></a>&nbsp;${item.colorCode}</td><td>${item.qualityType}</td>
					<td>${item.quantity}</td><td>${item.timelyQuantity}</td><td colspan="5"></td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
