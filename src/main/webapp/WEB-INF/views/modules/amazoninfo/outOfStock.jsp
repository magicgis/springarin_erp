<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script type="text/javascript">
	
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
		$(document).ready(function() {
			$("#country").on("click",function(){
				$("#searchForm").submit();
			});
			
			$("#selectLook").on("click",function(){
				if($(this).val()!=""){
					var params = {};
					params.country=$("input[name='country']").val();
					window.location.href = "${ctx}/psi/psiInventory/"+$(this).val()+"?"+$.param(params);
				}
			});
			
			
			$(".isCheck").on("click",function(){
				if(this.checked){
					$(".isCheck").val("1");
				}else{
					$(".isCheck").val("0");
				}
				$("#searchForm").submit();
			});
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#lookOver").click(function(){
				var params = {};
				params.country=$("input[name='country']").val();
				window.location.href = "${ctx}/psi/psiInventory/overStock?"+$.param(params);
			});
			
			$("#lookOverByProduct").click(function(){
				var params = {};
				params.country=$("input[name='country']").val();
				window.location.href = "${ctx}/psi/psiInventory/overStockProduct?"+$.param(params);
			});
			
			$("#lookOutOf").click(function(){
				var params = {};
				params.country=$("input[name='country']").val();
				window.location.href = "${ctx}/psi/psiInventory/outOfStock?"+$.param(params);
			});
			
			$("#lookOutOfByProduct").click(function(){
				var params = {};
				params.country=$("input[name='country']").val();
				window.location.href = "${ctx}/psi/psiInventory/outOfStockByProduct?"+$.param(params);
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.startDate=$("input[name='startDate']").val();
				params.endDate=$("input[name='endDate']").val();
				params.country=$("input[name='country']").val();
				window.location.href = "${ctx}/psi/psiInventory/expOutOfStock?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$("#dataTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 20,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 3, "desc" ]]
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
		<li class="${empty country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<form:form id="searchForm"  action="${ctx}/psi/psiInventory/outOfStock" method="post" class="breadcrumb form-search">
		<input name="country" type="hidden" value="${country}"/>
		<select id="selectLook" style="width:150px">
			<option value="">查看断货(SKU)</option>
			<option value="outOfStockByProduct">查看断货(产品)</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<label>断货日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${startDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<label>断货到今日：</label><input type="checkbox" name="isCheck" class="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/>
	</form:form>
	
	<tags:message content="${message}"/>
	<table id="dataTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:15%">产品名</th><th style="width:15%">SKU</th><th style="width:10%">国家</th><th style="width:10%">断货天数</th><th style="width:10%">近30天销量</th><th style="width:10%">售价($)</th><th style="width:10%">损失($)</th><th >断货日期</th></tr></thead>
		<tbody>
		<c:set value="0" var="total" />
		<c:forEach items="${outOfStockMap}" var="outOfStock" varStatus="i">
		<c:set value="${total+(empty outOfStock.value.delAmount ?0:outOfStock.value.delAmount)}" var="total" />
			<tr>
				<td>
					<c:if test="${not empty skuMap[outOfStock.key]}">
						<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${fn:split(skuMap[outOfStock.key],',')[0]}">${fn:split(skuMap[outOfStock.key],',')[0]}</a>
					</c:if>  
					<c:if test="${empty skuMap[outOfStock.key]}">
					没匹配条码
					</c:if>
				</td>
				<td>${outOfStock.key}</td>
				<td>${fns:getDictLabel(outOfStock.value.country,'platform','')}</td>
				<td>${outOfStock.value.outOffDays}	</td>
				<td>${sale30Map[outOfStock.key]}</td>
				<td>
				<c:if test="${not empty outOfStock.value.price }">
					<fmt:formatNumber maxFractionDigits="2" value="${outOfStock.value.price }"></fmt:formatNumber>
				</c:if>
				</td>
				<td><fmt:formatNumber maxFractionDigits="2" value="${outOfStock.value.delAmount}"></fmt:formatNumber> 	</td>
				<td>${outOfStock.value.outOffDaysStr }	</td>
		</c:forEach>
		</tbody>
		<tfoot>
				<tr><td>Total</td><td colspan="5"></td><td><fmt:formatNumber maxFractionDigits="2" value="${total}"></fmt:formatNumber><b>$</b></td><td></td></tr>
		</tfoot>
	</table>
</body>
</html>
