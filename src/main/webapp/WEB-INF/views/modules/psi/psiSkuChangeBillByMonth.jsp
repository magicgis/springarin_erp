<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>sku调换清单-按月报表</title>
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
			$("#selectWarehouseId").change(function(){
				$("#searchForm").submit();
			});
			
		});	
			
		
		
	</script>
	
</head>
<body>
	<form:form id="searchForm" modelAttribute="psiSkuChangeBill" action="${ctx}/psi/psiSkuChangeBill/byMonth" method="post" class="breadcrumb form-search" style="height:50px">
		<div style="height: 100px;line-height: 40px">
				<div style="height: 40px;">
				<label>Stock：</label>
				<select name="warehouseId" style="width:100px" id="selectWarehouseId">
					<option value="19"  ${psiSkuChangeBill.warehouseId eq '19'  ?'selected':'' }>Germany</option>
					<option value="21"  ${psiSkuChangeBill.warehouseId eq '21'  ?'selected':'' }>China</option>
					<option value="130"  ${psiSkuChangeBill.warehouseId eq '130'  ?'selected':'' }>China_LC</option>
					<option value="120" ${psiSkuChangeBill.warehouseId eq '120' ?'selected':'' }>American</option>
				</select>
				
				<label>Create Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="applyDate" value="<fmt:formatDate value="${psiSkuChangeBill.applyDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="sureDate" value="<fmt:formatDate value="${psiSkuChangeBill.sureDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search" style="width:80px"/>
			</div>
			
		</div>
		
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">月份</th><th style="width:8%">产品</th><th style="width:5%">国家</th><th style="width:5%">转出数</th></tr></thead>
		<tbody>
			<c:forEach items="${monthMap}" var="productMap">
			
				<c:forEach items="${productMap.value}" var="productEntry" varStatus="i">
					<tr style="background-color:${i.index<3?'#FFB5B5':''}">
						<td>${productMap.key}</td><td>${fn:split(productEntry.key,',')[0]}</td>
						<td>${fn:split(productEntry.key,',')[1] eq 'com'?'us':fn:split(productEntry.key,',')[1]}</td><td>${productEntry.value}</td>
					</tr>
				</c:forEach>
				
			</c:forEach>
		</tbody>
	</table>
	
	
</body>
</html>
