<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Fba inventory Manager</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		if(!(top)){
			top = self;			
		}	
		
		$(function(){
			
			$("select[name='country']").change(function(){
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
					sort = (sort[1]&&sort[1].toUpperCase()=="ASC"?"DESC":"ASC");
					$("#orderBy").val(order+" ASC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" DESC");
				}
				page();
			});
			
			$("#export").click(function(){
				top.$.jBox.confirm("导出FBA库存吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/stock/inventoryFba/export");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/psi/stock/inventoryFba");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});	
			
			$("#exportAll").click(function(){
				top.$.jBox.confirm("导出所有国家FBA库存吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/stock/inventoryFba/exportAll");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/psi/stock/inventoryFba");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});	
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
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
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${psiInventoryFba.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<form:form id="searchForm" modelAttribute="psiInventoryFba" action="${ctx}/psi/stock/inventoryFba" method="post" class="breadcrumb form-search">
		<div style="vertical-align: middle;height: 40px;line-height: 40px">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			<input  name="country" type="hidden" value="${psiInventoryFba.country}"/>
			&nbsp;&nbsp;&nbsp;FBA库存日期： <input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${psiInventoryFba.dataDate}" pattern="yyyy-MM-dd"/>" class="input-small"/>
			<label>Sku/Asin：</label><form:input path="sku" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			&nbsp;<input id="export" class="btn btn-primary" type="button" value="导出"/>
			<input id="exportAll" class="btn btn-primary" type="button" value="<spring:message code="sys_but_exportAllCountry"/>"/>
			<c:if test="${fn:length(page.list)>0}">
				&nbsp;&nbsp;&nbsp;&nbsp;<span class="badge badge-success">FBA库存日期数据最后更新时间:<fmt:formatDate value="${page.list[0].lastUpdateDate}" pattern="yyyy-MM-dd HH:mm"/></span>
			</c:if>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
				   <th style="width:160px;">Name</th>
				   <th style="width:150px">Sku</th>
				   <th style="width:70px">Fnsku</th>
				   <th style="width:70px" >Asin</th>
				   <th style="width:70px" >Salechannel<br/>Price</th>
				   <th style="width:60px" class="sort fulfillableQuantity">Fulfillable<br/>Quantity</th>
				   <th style="width:60px"  class="sort unsellableQuantity">Unsellable<br/>Quantity</th>
				   <th style="width:60px"  class="sort reservedQuantity">Reserved<br/>Quantity</th>
				   <th style="width:60px"  class="sort warehouseQuantity">Warehouse<br/>Quantity</th>
				   <th style="width:60px"  class="sort transitQuantity">Transit<br/>Quantity</th>
				   <th style="width:60px"  class="sort totalQuantity">Total<br/>Quantity</th>
			  </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="fba">
			<tr>
				<td><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${nameMap[fba.sku]}">${nameMap[fba.sku]}</a></td>
				<td>${fba.sku}</td>
				<td>${fba.fnsku}</td>
				<td>${fba.asin}</td>
				<td>
					<c:if test="${fba.country eq 'de'}">
						<c:forEach items="${priceMap[fba.sku]}" var="temp">
							<a href="http://www.amazon.${temp.key eq 'jp' || temp.key eq 'uk'?'co.':''}${temp.key eq 'com.unitek'?'com':temp.key}/dp/${fba.asin}" target="_blank">${temp.key}:${temp.value}</a><br/>
						</c:forEach>
					</c:if>
					<c:if test="${fba.country ne 'de'}">
						<a href="http://www.amazon.${fba.country eq 'jp' || fba.country eq 'uk'?'co.':''}${fba.country eq 'com.unitek'||fba.country eq 'com2'?'com':fba.country}/dp/${fba.asin}" target="_blank">${priceMap[fba.sku][fba.country]}</a>
					</c:if>
				</td>
				<td>${fba.fulfillableQuantity}</td>
				<td>${fba.unsellableQuantity}</td>
				<td>${fba.reservedQuantity}</td>
				<td>${fba.warehouseQuantity}</td>
				<td>${fba.transitQuantity}</td>
				<td>
					<c:if test="${empty fba.orrectQuantity || fba.orrectQuantity==0 }">
						${fba.totalQuantity}
					</c:if>
					<c:if test="${not empty fba.orrectQuantity && fba.orrectQuantity>0}">
						<span class="badge badge-warning">
							${fba.fulfillableQuantity+fba.transitQuantity+fba.orrectQuantity}
						</span>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
