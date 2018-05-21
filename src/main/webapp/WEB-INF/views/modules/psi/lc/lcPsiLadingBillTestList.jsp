<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>不合格质检单处理列表</title>
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
			$("#dealWay").change(function(){
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
		<li ><a href="${ctx}/psi/psiLadingBill/">收货单列表</a></li>
		<li ><a href="${ctx}/psi/lcPsiLadingBill/">(理诚)收货单列表</a></li>
		<li class="active"><a href="${ctx}/psi/lcPsiLadingBill/testList">不合格质检列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="test" action="${ctx}/psi/lcPsiLadingBill/testList" method="post" class="breadcrumb form-search" cssStyle="height:50px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>    
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			
			<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${test.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="sureDate" value="<fmt:formatDate value="${test.sureDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>处理状态：</label>
			<form:select path="dealWay" style="width: 200px" id="dealWay">
				<option value="" >全部</option>
				<option value="5" ${test.dealWay eq '5' ?'selected':''} >未处理</option>
				<option value="4" ${test.dealWay eq '4' ?'selected':''} >已处理</option>
			</form:select>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>收货单编号/产品名称：</label>
			<form:input path="productName" htmlEscape="false" maxlength="50" class="input-small" value="${test.productName }"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</div>
		</div>
		
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">   
		<thead>
		<tr>
		<th style="width:3%">序号</th><th style="width:10%">收货单号</th><th style="width:10%">产品名称</th><th style="width:5%">不合格数</th>
		<th style="width:5%">品检日期</th><th style="width:5%">品检人</th><th style="width:5%">处理备注</th><th style="width:5%">处理结果</th><th style="width:15%">操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="test">
			<tr>
				<td>${test.id}</td>
				<td><a href="${ctx}/psi/lcPsiLadingBill/view?id=${test.ladingId}">${test.ladingBillNo}</a></td>
				<td>${test.productNameColor}</td>
				<td>${test.totalQuantity}</td>
				<td><fmt:formatDate value="${test.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>${test.createUser.name}</td>
				<td>${test.reviewRemark}</td>
				<td>
					<c:if test="${test.testSta eq '8' }"><span class="label label-inverse">已取消</span></c:if>
					<c:if test="${test.testSta ne '8' }">
						<c:if test="${'0' eq test.dealWay}"><span class="label label-success">特采</span></c:if>
						<c:if test="${'2' eq test.dealWay}"><span class="label label-inverse">返工</span></c:if>
					</c:if>
				</td>
				<td>
					<c:if test="${test.testSta ne '8' }">
						<c:if test="${empty test.dealWay}"><a  class="btn btn-small"   href="${ctx}/psi/lcPsiLadingBill/testReview?id=${test.id}">处理</a></c:if>
						<a   class="btn btn-small" href="${ctx}/psi/lcPsiLadingBill/testReviewView?id=${test.id}">查看</a>
					</c:if>
					
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>   
	<div class="pagination">${page}</div>
</body>
</html>
