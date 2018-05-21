<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品问题详细页面</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="refresh" content="120"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<link href="${ctxStatic}/common/mailstate.css" type="text/css" rel="stylesheet" />
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
		
		$(document).ready(function(){
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#dataTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
		});
		
		
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="productProblem" action="${ctx}/custom/productProblem/detail" method="post" class="breadcrumb form-search">
		<label>创建日期：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${productProblem.dataDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${productProblem.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		&nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<input type="hidden" name="problemType" value="${productProblem.problemType }"/>
		<input type="hidden" name="productName" value="${productProblem.productName }"/>
		<input type="hidden" name="country" value="${productProblem.country }"/>
	</form:form>
	
	
	<table id="dataTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<th sytle="10%">Date source</th>
			<th sytle="10%">Country</th>
			<th sytle="10%">Product Name</th>
			<th sytle="10%">Problem Type</th>
			<th sytle="20%">Problem Deatil</th>
			<th sytle="10%">Order Nos</th>
			<th sytle="10%">Problem Editor</th>
			<th sytle="10%">Create Date</th>
		</thead>
		<tbody>
		<c:forEach items="${problems}" var="pro">
			<tr>
				<td>
				<c:if test="${pro.pKey.dataType eq '2'}"><a href="${ctx}/custom/event/form?id=${pro.pKey.dataId}" target="_blank">事件</a></c:if>
				<c:if test="${pro.pKey.dataType ne '2'}"><a href="${ctx}/custom/emailManager/view?id=${pro.pKey.dataId}" target="_blank">邮件</a></c:if>
				</td>
				<td>${fns:getDictLabel(pro.country, 'platform', '')}</td>
				<td>${pro.productName}</td>
				<td>${pro.problemType}</td>
				<td>${pro.problem}</td>
				<td>
				<c:forEach items="${fn:split(pro.orderNos,',')}" var ="orderNo">
				<a href="${ctx}/amazoninfo/order/form?amazonOrderId=${orderNo}">${orderNo}</a>
				</c:forEach>
				</td>
				<td>${masterMap[pro.pKey.dataId] }</td>
				<td><fmt:formatDate value="${pro.dataDate}" pattern="yyyy-MM-dd"/> </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
