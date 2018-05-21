<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Inventory Location Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
    		top: 10%;
    		left: 60%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
		}
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			
			$("#country,#stockLocationId").change(function(){
				//$("#searchForm").attr("action","${ctx}/psi/psiInventoryLocation?stockId=${stockId}");
				$("#searchForm").submit();
			});
			
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
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
		
		//显示库位调整设置框
		function locationAdjust(id, stockId){
			var modal = $("#locationAdjustModal");
			$("#locationAdjustModal tbody").html("");	//清空tbody
			var rs = "";
			$.ajax({
      			 type: "POST",
      			 url: "${ctx}/psi/psiInventoryLocation/getInventoryLocation?id="+id,
      			 async: false,
      			 success: function(msg){
      				 rs = msg;		
      			 }
          	});
			if(rs){
				$("#locationAdjustModal").find("#targetLocationId").val(rs.stockLocation.id);
				$("#locationAdjustModal").find("#targetLocationId").trigger("change");
				modal.find("h4").text(rs.sku+"("+rs.stockLocation.name+")<spring:message code='psi_locationAdjust_adjust'/>");
				modal.find("input[name='sourceId']").val(rs.id);
				modal.find("input[name='sourceLocationId']").val(rs.stockLocation.id);
				var tr = "";
				$("#locationAdjustModal tbody").prepend(tr);
				if(rs.newQuantity>0){
					tr = tr + "<tr><td>"+rs.newQuantity+"</td><td style='width:150px;vertical-align: middle;text-align: center;'><input class='qty' name='newQuantity' max='"+rs.newQuantity+"' value='"+rs.newQuantity+"'/></td>"
					+ "<td class='qtyType'>new</td></tr>";
				}
				if(rs.offlineQuantity>0){
					tr = tr + "<tr><td>"+rs.offlineQuantity+"</td><td style='width:150px;vertical-align: middle;text-align: center;'><input class='qty' name='offlineQuantity' max='"+rs.offlineQuantity+"' value='"+rs.offlineQuantity+"'/></td>"
					+ "<td class='qtyType'>offline</td></tr>";
				}
				if(rs.oldQuantity>0){
					tr = tr + "<tr><td>"+rs.oldQuantity+"</td><td style='width:150px;vertical-align: middle;text-align: center;'><input class='qty' name='oldQuantity' max='"+rs.oldQuantity+"' value='"+rs.oldQuantity+"'/></td>"
					+ "<td class='qtyType'>old</td></tr>";
				}
				if(rs.brokenQuantity>0){
					tr = tr + "<tr><td>"+rs.brokenQuantity+"</td><td style='width:150px;vertical-align: middle;text-align: center;'><input class='qty' name='brokenQuantity' max='"+rs.brokenQuantity+"' value='"+rs.brokenQuantity+"'/></td>"
					+ "<td class='qtyType'>broken</td></tr>";
				}
				if(rs.renewQuantity>0){
					tr = tr + "<tr><td>"+rs.renewQuantity+"</td><td style='width:150px;vertical-align: middle;text-align: center;'><input class='qty' name='renewQuantity' max='"+rs.renewQuantity+"' value='"+rs.renewQuantity+"'/></td>"
					+ "<td class='qtyType'>renew</td></tr>";
				}
				if(rs.sparesQuantity>0){
					tr = tr + "<tr><td>"+rs.sparesQuantity+"</td><td style='width:150px;vertical-align: middle;text-align: center;'><input class='qty' name='sparesQuantity' max='"+rs.sparesQuantity+"' value='"+rs.sparesQuantity+"'/></td>"
					+ "<td class='qtyType'>spares</td></tr>";
				}
				$("#locationAdjustModal tbody").prepend(tr);
			}
			modal.modal();
		}

		//提交之前验证
		function sureSubmit(){
			if(!valiForm()){
				return;
			}
			$("#locationAdjust").submit();
		}
		
		function valiForm(){
			var flag = 0;
			var total = 0;
			$("#locationAdjustModal tbody tr").each(function(){
				var obj = $(this);
				var max = obj.find(".qty").attr("max");
				var qty = obj.find(".qty").val();
				var qtyType = obj.find(".qtyType").text();
				if(qty != ''){
					total += parseInt(qty);
				}
				if(parseInt(max) < parseInt(qty)){
					top.$.jBox.tip('<spring:message code="psi_locationAdjust_numTips"/>', 'error',{timeout:3000});
					flag = 1;
					return;
				}
			});
			if(flag == 0){
				var sourceLocationId = $("#locationAdjustModal").find("input[name='sourceLocationId']").val();
				var targetLocationId = $("#locationAdjustModal").find("#targetLocationId").val();
				if(sourceLocationId == targetLocationId){
					top.$.jBox.tip('<spring:message code="psi_locationAdjust_locationTips"/>', 'error',{timeout:3000});
					flag = 1;
				}
			}
			if(total == 0){
				top.$.jBox.tip('<spring:message code="psi_locationAdjust_adjustmentsTips"/>', 'error',{timeout:3000});
				flag = 1;
			}
			if(flag == 1){
				return false;
			}
			return true;
		}
	</script>
</head>
<body>

	<ul class="nav nav-tabs"> 
		<c:forEach items="${stocks}" var="stock">
			<li class="${stock.id eq stockId ?'active':'' }" style="font-weight: bold;font-size:15px"><a href="${ctx}/psi/psiInventoryLocation?stockId=${stock.id}">
			<c:choose>
				<c:when test="${stock.countrycode eq 'DE' }">Germany</c:when>
				<c:when test="${stock.id eq '21' }">China</c:when>
				<c:when test="${stock.id eq '130' }">China_LC</c:when>
				<c:when test="${stock.countrycode eq 'US' }">American</c:when>
				<c:otherwise>${stock.stockName}</c:otherwise>
			</c:choose>
			</a>
			</li>
		</c:forEach>
	</ul>
	
	<form:form id="searchForm" modelAttribute="psiInventoryLocation" action="${ctx}/psi/psiInventoryLocation/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input name="stockId" type="hidden" value="${stockId}"/>
		<div style="height: 40px;line-height: 40px">
			<div style="height: 40px;">
				<label>Country：</label>
				<select name="countryCode" style="width: 150px" id="country">
					<option value=""  ${psiInventoryLocation.countryCode eq '' ?'selected':'' }>All</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							 <option value="${dic.value}" ${psiInventoryLocation.countryCode eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:if>
					</c:forEach>	
				</select>&nbsp;&nbsp;
				<label><spring:message code="psi_locationAdjust_location"/>：</label>
				<select name="stockLocation.id" style="width: 150px" id="stockLocationId">
					<option value="">All</option>
					<c:forEach items="${locationMap}" var="location">
						<option value="${location.key }" ${psiInventoryLocation.stockLocation.id eq location.key ?'selected':''}>${location.value }</option>
					</c:forEach>
				</select>&nbsp;&nbsp;
				<label>Sku/ProductName：</label>
				<form:input path="productName" maxlength="50" class="input-small"/>&nbsp;&nbsp;
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th>Product</th>
				<th>Sku</th>
				<th>Total</th>
				<th class="sort newQuantity">New</th>
				<th class="sort offlineQuantity">Offline</th>
				<th class="sort oldQuantity">Old</th>
				<th class="sort brokenQuantity">Broken</th>
				<th class="sort renewQuantity">Renew</th>
				<th class="sort sparesQuantity">Spares</th>
				<th>Area</th>
				<th>Location</th>
				<th>SnCode</th>
				<th>Remark</th>
				<shiro:hasPermission name="psi:location:edit">
					<th>Operate</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="inventoryLocation">
			<tr>
				<td>
					<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${inventoryLocation.productNameColor}">
					${inventoryLocation.productNameColor}</a>
				</td>
				<td>${inventoryLocation.sku}</td>
				<td>${inventoryLocation.totalQuantity}</td>
				<td>${inventoryLocation.newQuantity}</td>
				<td>${inventoryLocation.offlineQuantity}</td>
				<td>${inventoryLocation.oldQuantity}</td>
				<td>${inventoryLocation.brokenQuantity}</td>
				<td>${inventoryLocation.renewQuantity}</td>
				<td>${inventoryLocation.sparesQuantity}</td>
				<td>${inventoryLocation.stockLocation.stockArea.name}</td>
				<td>${inventoryLocation.stockLocation.name}</td>
				<td>${inventoryLocation.snCode}</td>
				<td>${inventoryLocation.remark}</td>
				<shiro:hasPermission name="psi:location:edit">
					<td>
						<input type="button" class="btn btn-small btn-info" onclick="locationAdjust('${inventoryLocation.id}','${stockId}')" value="Location Adjustment"/>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	<div id="locationAdjustModal" data-backdrop="static" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-dialog" style="width: 500px;height:300px;">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h4><spring:message code="psi_locationAdjust_adjust"/></h4>
		</div>
		<form id="locationAdjust" action="${ctx}/psi/psiInventoryLocation/locationAdjustSave" method="post" class="adjustForm"  onkeydown="if(event.keyCode==13)return false;" >
			<input type="hidden" value="${stockId }" name="stockId"/>
			<input type="hidden" value="" name="sourceId"/>
			<input type="hidden" value="" name="sourceLocationId"/>
			<div class="modal-body">
				<spring:message code="psi_locationAdjust_target"/>：
				<select name="stockLocation.id" id="targetLocationId">
					<c:forEach items="${locationMap}" var="location">
						<option value="${location.key }">${location.value }</option>
					</c:forEach>
				</select>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th><spring:message code="psi_locationAdjust_total"/></th>
							<th><spring:message code="psi_locationAdjust_adjustments"/></th>
							<th><spring:message code="psi_locationAdjust_type"/></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="button" class="btn btn-primary" onclick="sureSubmit()" value="Submit"/>
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
		</div>
	</div>
</body>
</html>
					   