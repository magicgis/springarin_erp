<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Custom Email manager</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="refresh" content="120"/>
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
			$("#expExcel").click(function(){
				var params = {};
				params.answerDate=$("input[name='answerDate']").val();
				params.endDate=$("input[name='endDate']").val();
				params.productName=$("input[name='productName']").val();
				window.location.href = "${ctx}/custom/emailManager/expProblem?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$("#dataSource").change(function(){
				if($(this).val()=="2"){
					window.location.href = "${ctx}/custom/event/problemList";
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
	<form:form id="searchForm" modelAttribute="customEmail" action="${ctx}/custom/emailManager/problemList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height:40px;line-height: 40px">
			<div >
			<label>数据来源：</label>
			<select id="dataSource" style="width:100px">
				<option value="1" selected="selected">邮件</option>
				<option value="2">事件</option>
			</select>
			<label>回复日期：</label>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="answerDate" value="<fmt:formatDate value="${customEmail.answerDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${customEmail.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>Product Name：</label>
			<input type="text" name="productName"  value="${customEmail.productName}"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<th style="width: 100px">Product Line</th>
			<th style="width: 190px">Product Name</th>
			<th style="width: 190px">Problem Type</th>
			<th style="width: 190px">Problem Detail</th>
			<th style="width: 190px">Product Manager</th>
			<th style="width: 160px">Order No</th>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="email">
			<tr>
				<td>${productMap[email.productName]}</td>
				<td>${email.productName}</td>
				<td>${email.problemType}</td>
				<td>${email.problem}</td>
				<td>${mangerMap[productMap[email.productName]]}</td>
				<td>${email.orderNos}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
