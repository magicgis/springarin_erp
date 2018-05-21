<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品价格改动日志</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			float: right;
			min-height: 40px
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
			$("#productId").on("change",function(){
				$("#searchForm").submit();     
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.createTime=$("input[name='createTime']").val();
				params.updateTime=$("input[name='updateTime']").val();
				window.location.href = "${ctx}/psi/productTieredPriceLog/exp?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
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
		
		function goBack(){
			window.location.href="${ctx}/psi/productTieredPrice/";
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/productTieredPrice">产品阶梯价格</a></li>
		<li class="active" ><a href="#">产品价格改动日志</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="priceLog" action="${ctx}/psi/productTieredPriceLog" method="post" class="breadcrumb form-search" cssStyle="height: 40px;">
	    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>  
	     <div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
				<label>产品：</label>
			    	<select id="productId" name="productIdColor" style="width:180px">
			    		<option value="">全部</option>
				    	<c:forEach items="${proColorMap}" var="proEntry">
				    		<option value="${proEntry.value}" ${proEntry.value eq priceLog.productIdColor?'selected':'' }>${proEntry.key}</option>
				    	</c:forEach>
				    </select>
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createTime" value="<fmt:formatDate value="${priceLog.createTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
					&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateTime" value="<fmt:formatDate value="${priceLog.updateTime}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
					&nbsp;&nbsp;&nbsp;&nbsp;	<shiro:hasPermission name="psi:product:viewPrice">	<input id="expExcel" class="btn btn-warning" type="button" value="导出"/></shiro:hasPermission>
					（tips:本页显示的是不含税价，或许有金额对不齐的情况）
				</div>
			</div>
				
	     &nbsp;&nbsp;&nbsp;&nbsp;
	</form:form>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead><tr><th style="width:5%">序号</th><th style="width:10%">产品</th><th style="width:5%">供应商</th><th style="width:5%">阶梯级</th><th style="width:5%">调整前</th><th style="width:5%">调整后</th><th style="width:5%">调整</th><th style="width:5%">调整比例</th><th style="width:20%">原因</th><th style="width:10%">备注</th><th style="width:10%">操作人</th><th style="width:10%" >操作时间</th></tr></thead>
			<tbody>
			<c:forEach items="${page.list}" var="tieredPrice">
				<tr>
					<td>${tieredPrice.id}</td>
					<td>${tieredPrice.productNameColor}</td>
					<td>${tieredPrice.supplier.nikename}</td>
					<td>${tieredPrice.tieredType}</td>
					<td>
						<c:if test="${not empty tieredPrice.oldPrice}">${tieredPrice.currencyType eq 'USD'?'$':'￥'}${tieredPrice.oldPrice}</c:if>
					</td>
					<td>
						<c:if test="${not empty tieredPrice.price}"> ${tieredPrice.currencyType eq 'USD'?'$':'￥'}${tieredPrice.price}</c:if>
					</td>
					<td>
						<c:if test="${not empty tieredPrice.oldPrice && not empty tieredPrice.price }">
						<fmt:formatNumber value="${tieredPrice.oldPrice-tieredPrice.price}" pattern="0.##"/>
						</c:if>
					</td>
					<td>
						<c:if test="${not empty tieredPrice.oldPrice && not empty tieredPrice.price }">
							<fmt:formatNumber value="${(tieredPrice.oldPrice-tieredPrice.price)*100/tieredPrice.oldPrice}" pattern="0.#"/>%
						</c:if>
					</td>
					<td>${tieredPrice.content}</td>
					<td>${tieredPrice.remark}</td>
					<td>${tieredPrice.createUser.name}</td>
					<td><fmt:formatDate value="${tieredPrice.createTime}" pattern="yyyy-MM-dd"/> </td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	<div class="pagination">${page}</div>
</body>
</html>
