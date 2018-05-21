<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>inBound or outBound Detail</title>
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
			$("#unlineOrder").click(function(){
				if(this.checked){
					$("#unlineOrderVal").val('yes');
				}else{
					$("#unlineOrderVal").val('');
				}
				$("#searchForm").submit();
			});
			
			$("#operationType,#colorCode,#countryCode,#selectUser").change(function(){
				$("#searchForm").submit();
			});
			
			$("#outerChange").on('click',function(){
				$("#showFlag").val("0");
				$("#searchForm").submit();
			});
			
			$("#innerChange").on('click',function(){
				$("#showFlag").val("1");
				$("#searchForm").submit();
			});
			
			$("#btnGoback").click(function(){
				window.location.href = "${ctx}/psi/psiInventory";
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
	<ul class="nav nav-tabs">
		<li class="${psiInventoryRevisionLog.showFlag eq '0'?'active':'' }"><a href="#" id="${psiInventoryRevisionLog.showFlag eq '0'?'':'outerChange'}">In-Out Details</a></li>
		<li class="${psiInventoryRevisionLog.showFlag eq '1'?'active':'' }"><a href="#" id="${psiInventoryRevisionLog.showFlag eq '1'?'':'innerChange'}">Adjust Details</a></li>
	</ul>
	<div style="text-align:center;font-size: 20px;" class="alert alert-info">
	${product.brand}&nbsp;${product.model}&nbsp;${fns:getDictLabel(psiInventoryRevisionLog.countryCode, 'platform', '')}&nbsp;
	<c:choose>
	<c:when test="${psiInventoryRevisionLog.colorCode eq 'All'}"></c:when>
	<c:when test="${psiInventoryRevisionLog.colorCode eq ''}">No Color</c:when>
	<c:otherwise>${psiInventoryRevisionLog.colorCode}</c:otherwise>
	</c:choose>
	</div>
	<form:form id="searchForm" modelAttribute="psiInventoryRevisionLog" action="${ctx}/psi/psiInventoryRevisionLog/" method="post" class="breadcrumb " cssStyle="height: 40px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input type='hidden' name="warehouseId" value="${psiInventoryRevisionLog.warehouseId}"/>
		<input type='hidden' name="productId" value="${psiInventoryRevisionLog.productId}"/>
		<input type='hidden' name="showFlag" id="showFlag" value="${psiInventoryRevisionLog.showFlag}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			<label>Type：</label>	
			<select name="operationType"  style="width: 150px" id="operationType">
			 <option value="" >All</option>
				 <c:forEach items="${sumArr}" var="sum">
				 	<option value="${sum[0]}" ${psiInventoryRevisionLog.operationType eq sum[0] ?'selected':''} >${sum[0]}</option>
				 </c:forEach>
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
			<label>Operator：</label>
			<select name="operationUser.id" style="width: 120px" id="selectUser">
				<option value=""><spring:message code='custom_event_all'/></option>
				<c:forEach items="${allUser}" var="user">
					<option value="${user.id}" ${psiInventoryRevisionLog.operationUser.id eq user.id?'selected':''} >${user.name}</option>
				</c:forEach>		
			</select>&nbsp;&nbsp;
	
			<label>Country：</label>	
			<select name="countryCode"  style="width: 150px" id="countryCode">
			 <option value="" ${psiInventoryRevisionLog.countryCode eq ''?'selected':'' } >All</option>
			 <c:forEach items="${countryArr}" var="country" >
				 <option value="${country}" ${psiInventoryRevisionLog.countryCode eq country ?'selected':''} >${fns:getDictLabel(country, 'platform', '')}</option>
			 </c:forEach>
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
			
			<label>Color：</label>	
			<select name="colorCode"  style="width: 150px" id="colorCode">
			 <option value="All" ${psiInventoryRevisionLog.colorCode eq 'All'?'selected':'' } >All</option>
			 <c:forEach items="${colorArr}" var="color" >
				 <option value="${color}" ${psiInventoryRevisionLog.colorCode eq color ?'selected':''} >${color eq ''?'No Color':color}</option>
			 </c:forEach>
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
			<form:hidden path="isNewOperation" id="unlineOrderVal" />
				<label>newTypeOperate：</label>	<input type="checkbox" id="unlineOrder"${not empty psiInventoryRevisionLog.isNewOperation?'checked':''}/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	
	<ul class="nav nav-tabs">
		<c:forEach items="${stocks}" var="stock">
				<li class="${stock.id eq psiInventoryRevisionLog.warehouseId ?'active':'' }" style="font-weight: bold;font-size:15px"><a href="${ctx}/psi/psiInventoryRevisionLog?warehouseId=${stock.id}
				&productId=${psiInventoryRevisionLog.productId}&colorCode=${psiInventoryRevisionLog.colorCode}&countryCode=${psiInventoryRevisionLog.countryCode}&showFlag=${psiInventoryRevisionLog.showFlag}">
				<c:choose>
					<c:when test="${stock.id eq '19' }">Germany</c:when>
					<c:when test="${stock.id eq '21' }">China</c:when>
					<c:when test="${stock.id eq '120' }">American</c:when>
					<c:when test="${stock.id eq '130' }">China_LC</c:when>
					<c:otherwise>${stock.stockName}</c:otherwise>
				</c:choose>
				</a></li>
		</c:forEach>
	</ul>
	
	<table id="countTable1" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th>Sku</th><th >New Quantity</th><th >Old Quantity</th><th >Broken Quantity</th><th >Renew Quantity</th><th >Spares Quantity</th><th >Offline Quantity</th></tr>
			</thead>
			<tbody>
				<c:forEach items="${psiInventorys}" var="psiInventory">
					<tr><td>${psiInventory.sku}</td><td>${psiInventory.newQuantity}</td><td>${psiInventory.oldQuantity}</td><td>${psiInventory.brokenQuantity}</td><td>${psiInventory.renewQuantity}</td><td>${psiInventory.sparesQuantity}</td><td>${psiInventory.offlineQuantity}</td></tr>
				</c:forEach>
			</tbody>
	</table>
		
	<c:if test="${psiInventoryRevisionLog.showFlag eq '0'}" >
	<table id="countTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				<c:choose>
					<c:when test="${empty psiInventoryRevisionLog.operationType }">
						<c:forEach items="${sumArr}" var="sum">
							<th width="<fmt:formatNumber type='number' value="${100/fn:length(sumArr)}" maxFractionDigits='0'/>%">${sum[0]}</th>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:forEach items="${sumArr}" var="sum">
							<c:if test="${psiInventoryRevisionLog.operationType eq sum[0]}">
							<th >${sum[0]}</th>
							</c:if>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				</tr></thead>
			<tbody>
				<tr>
				<c:choose>
					<c:when test="${empty psiInventoryRevisionLog.operationType }">
						<c:forEach items="${sumArr}" var="sum" >
							<td>${sum[1]}</td>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:forEach items="${sumArr}" var="sum">
							<c:if test="${psiInventoryRevisionLog.operationType eq sum[0]}">
							<td>${sum[1]}</td>
							</c:if>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				</tr>
			</tbody>
		</table>
	
	</c:if>
	<c:if test="${psiInventoryRevisionLog.showFlag eq '1' && fn:length(noFormArr)>0}" >
		<table id="countTable3" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				<c:choose>
					<c:when test="${empty psiInventoryRevisionLog.operationType }">
						<c:forEach items="${noFormArr}" var="sum">
							<th width="<fmt:formatNumber type='number' value="${100/fn:length(noFormArr)}" maxFractionDigits='0'/>%">${sum[0]}</th>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:forEach items="${noFormArr}" var="sum">
							<c:if test="${psiInventoryRevisionLog.operationType eq sum[0]}">
							<th >${sum[0]}</th>
							</c:if>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				</tr></thead>
			<tbody>
				<tr>
				<c:choose>
					<c:when test="${empty psiInventoryRevisionLog.operationType }">
						<c:forEach items="${noFormArr}" var="sum" >
							<td>${sum[1]}</td>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:forEach items="${noFormArr}" var="sum">
							<c:if test="${psiInventoryRevisionLog.operationType eq sum[0]&&!fns:startsWith(sum[0],'From')}">
							<td>${sum[1]}</td>
							</c:if>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				</tr>
			</tbody>
		</table>
		</c:if>
	<c:if test="${psiInventoryRevisionLog.showFlag eq '1'&&fn:length(formArr)>0}" >
	<table id="countTable4" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				<c:choose>
					<c:when test="${empty psiInventoryRevisionLog.operationType }">
						<c:forEach items="${formArr}" var="sum">
							<th width="<fmt:formatNumber type='number' value="${100/fn:length(formArr)}" maxFractionDigits='0'/>%">${sum[0]}</th>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:forEach items="${formArr}" var="sum">
							<th >${sum[0]}</th>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				</tr></thead>
			<tbody>
				<tr>
				<c:choose>
					<c:when test="${empty psiInventoryRevisionLog.operationType }">
						<c:forEach items="${formArr}" var="sum" >
							<td>${sum[1]}</td>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:forEach items="${formArr}" var="sum">
							<td>${sum[1]}</td>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				</tr>
			</tbody>
		</table>
	</c:if>
		
		
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="text-align:center" colspan="13">Operate Inventory Log</th></tr>
		<tr><th width="5%">No.</th><th width="5%">Country</th><th width="5%">Color</th><th width="10%">sku</th><th width="5%">Quantity</th><th width="5%">Timely Qty</th><th width="5%">Type</th><th width="25%">Operate Type</th><th width="5%">Operator</th><th width="10%">Operate Time</th><th width="8%">To Warehouse</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="revisionLog">
			<tr>
				<td>${revisionLog.id}</td>
				<td>${fns:getDictLabel(revisionLog.countryCode, 'platform', '')}</td>
				<td>${revisionLog.colorCode}</td>
				<td>${revisionLog.productColorCountry eq revisionLog.sku?'新品无SKU':revisionLog.sku}</td>
				<c:choose>
					<c:when test="${revisionLog.quantity ge 0}">
						<td>
						<span style="color:#009100}">+${revisionLog.quantity}</span>
						</td>
						<td><b>${revisionLog.timelyQuantity}</b></td>
						<td><b>${revisionLog.dataType}</b></td>
						<td><span class='label label-success'>${revisionLog.operationType}&nbsp;</span>
						<span style="margin-right:1px">
								<span style="margin-right:2px;float:right">
									<c:if test="${not empty revisionLog.relativeNumber}">
									<a  href="${ctx}/psi/psiInventoryIn/view?billNo=${revisionLog.relativeNumber}">${revisionLog.relativeNumber}</a>
									</c:if>
								</span>
						</span>
						</td>
					</c:when>
					<c:otherwise>
						<td><span style="color:#FF0000">${revisionLog.quantity}</span></td>
						<td><b>${revisionLog.timelyQuantity}</b></td>
						<td><b>${revisionLog.dataType}</b></td>
						<td><span class='label label-important'>${revisionLog.operationType}&nbsp;</span>
						<c:if test="${revisionLog.operationType eq'FBA Delivery'}">
						<a target='_blank' class="nava" style="color:#2828FF;" href="${ctx}/psi/psiInventoryOut/track?billNo=${revisionLog.relativeNumber}"><b>Track</b></a>
						</c:if>
						
						<c:if test="${revisionLog.operationType eq'Lot Delivery'}">
						(<a target="_blank" href="<c:url value='/data/site${revisionLog.lotFileUrl}'/>"><font color="red">${revisionLog.lotFileName}</font></a>)
						</c:if>
						
						
						<span style="margin-right:2px;float:right">
							<c:if test="${not empty revisionLog.relativeNumber}">
							<a  href="${ctx}/psi/psiInventoryOut/view?billNo=${revisionLog.relativeNumber}">${revisionLog.relativeNumber}</a>
							</c:if>
						</span>
						</td>
					</c:otherwise>
				</c:choose>
				<td>${revisionLog.operationUser.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${revisionLog.operatinDate}"/></td>
				<td>${revisionLog.terminiWarehouseName}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<div class="pagination">${page}</div>
	<div class="form-actions" style="text-align: center">
			<input  class="btn" type="button" value="Back" onclick="history.go(-1)"/>
	</div>
	
	
</body>
</html>
