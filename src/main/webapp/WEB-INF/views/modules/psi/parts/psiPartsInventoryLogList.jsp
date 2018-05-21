<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件库存管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	
	
	<script type="text/javascript">
		
		$(document).ready(function() {
			$("#dataType,#partsId").on("click",function(){
				$("#searchForm").submit();
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
<div style="text-align:center;font-size: 20px;" class="alert alert-info">${empty partsInventory.partsName ?'全部':partsInventory.partsName} </div>
	<form:form id="searchForm" modelAttribute="psiPartsInventoryLog" action="${ctx}/psi/psiPartsInventoryLog" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>配件名称：</label>
			<select name ="partsId" id ="partsId" style="width:300px">
				<option value="">全部</option>
				<c:forEach items="${partsList}" var="parts">
					<option value="${parts.id}" ${parts.id eq psiPartsInventoryLog.partsId ?'selected':'' }>${parts.partsName}</option>
				</c:forEach>
			</select> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>数据类型 ：</label>
			<select name ="dataType" id ="dataType" style="width:100px">
				<option value="">全部</option>
				<option value="poFrozen"       ${psiPartsInventoryLog.dataType    eq 'poFrozen'?'selected':'' }>po冻结</option>
				<option value="poNotFrozen"    ${psiPartsInventoryLog.dataType    eq 'poNotFrozen'?'selected':'' }>po可用</option>
				<option value="stockFrozen"    ${psiPartsInventoryLog.dataType    eq 'stockFrozen'?'selected':'' }>stock冻结</option>
				<option value="stockNotFrozen" ${psiPartsInventoryLog.dataType 	  eq 'stockNotFrozen'?'selected':'' }>stock可用</option>
			</select>
		<label>操作类型 ：</label>
			<select name ="operateType" id ="operateType" style="width:100px">
				<option value="">全部</option>
				<option value="Purchase BY ORDER"   ${psiPartsInventoryLog.operateType     eq 'Purchase BY ORDER'?'selected':'' }>配件下单</option>
				<option value="Purchase Self"       ${psiPartsInventoryLog.operateType     eq 'Purchase Self'?'selected':'' }    >额外下单</option>
				<option value="Purchase Storing"    ${psiPartsInventoryLog.operateType     eq 'Purchase Storing'?'selected':'' } >收货入仓</option>
				<option value="stock可用To冻结"        ${psiPartsInventoryLog.operateType     eq 'stock可用To冻结'?'selected':'' }    >stock可用转stock冻结</option>
				<option value="taking-out"          ${psiPartsInventoryLog.operateType 	   eq 'taking-out'?'selected':'' }       >出库配送</option>
				<option value="noChangeData" 		${psiPartsInventoryLog.operateType     eq 'noChangeData'?'selected':'' }     >非转换的</option>
			</select>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/> &nbsp;&nbsp;&nbsp;&nbsp;
	</form:form>
	
	<tags:message content="${message}"/>
	<c:if test="${not empty psiPartsInventoryLog.partsId }">
		<table id="countTable1" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th >po冻结数</th><th >po可用数</th><th>stock冻结数</th><th>stock可用数</th></tr>
			</thead>
			<tbody>
			   <tr><td>${partsInventory.poFrozen}</td><td>${partsInventory.poNotFrozen}</td><td>${partsInventory.stockFrozen}</td><td>${partsInventory.stockNotFrozen}</td></tr>
			</tbody>
		</table>
	</c:if>
	  
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:20%">配件名称</th><th>数量</th><th>数据类型</th><th>操作类型</th><th>操作人</th><th>备注</th><th>操作时间</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="log">
			<tr>
				<td>${log.partsName}</td>
				<td>
				<c:if test="${log.quantity ge 0}"><span style="color:red;font-weight: bold;font-size: 15px;vertical-align: middle;">+${log.absQuantity}</span></c:if>
				<c:if test="${log.quantity le 0}"><span style="color:green;font-weight: bold;font-size: 15px;vertical-align: middle;">-${log.absQuantity}</span></c:if>
				</td>
				<td>
				<c:choose>
					<c:when test="${log.dataType eq 'poNotFrozen'}">po可用</c:when>
					<c:when test="${log.dataType eq 'poFrozen'}">po冻结</c:when>
					<c:when test="${log.dataType eq 'stockNotFrozen'}">stock可用</c:when>
					<c:when test="${log.dataType eq 'stockFrozen'}">stock冻结</c:when>
				</c:choose>
				</td>
				<td>
				<b>${log.operateType}</b>
				（
				<c:choose>
					<c:when test="${log.operateType  eq 'Purchase BY ORDER'}">配件下单</c:when>
					<c:when test="${log.operateType  eq 'Purchase Self'}">配件额外下单确认</c:when>
					<c:when test="${log.operateType  eq 'Purchase Storing'}">配件收货</c:when>
					<c:when test="${log.operateType  eq 'taking-out'}">配件出库配送</c:when>
					<c:otherwise>${log.operateType}</c:otherwise>
				</c:choose>
				）
				 <span style="float:right;padding-left:2px">${log.relativeNumber}</span></td>
				<td>${log.createUser.name}</td>
				<td>${log.remark}</td>
				<td><fmt:formatDate value="${log.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
