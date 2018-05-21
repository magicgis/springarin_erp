<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>招聘管理</title>
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
			$("#officeId,#workSta").on("click",function(){
				$("#searchForm").submit();
			});
			
			
			

			$("#contentTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"aoColumns": [
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null
				],
				"ordering" : true,
				 "aaSorting": [[ 7, "desc" ]]
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
		<li class="active"><a href="">招聘列表</a></li>
		<shiro:hasPermission name="oa:recruit:edit">
			<li ><a href="${ctx}/oa/recruit/form">新增</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="recruit" action="${ctx}/oa/recruit/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		
		<div style="height: 50px;line-height: 40px">
			<label>面试日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${recruit.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${recruit.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		</div>
	</form:form>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th >序号</th><th >姓名</th><th >性别</th><th >应聘部门</th><th >应聘职位</th><th  >联系方式</th><th  >邮箱</th><th >面试时间</th><th >简历来源</th><th >通知时间</th><th >简历链接</th><th >初试评价</th><th >复试评价</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${recruits}" var="recruit">
			<tr>
				<td>${recruit.id}</td>
				<td>${recruit.name}</td>
				<td>${recruit.sex}</td>
				<td>${recruit.office.name}</td>
				<td>${recruit.position}</td>
				<td>${recruit.phone}</td>
				<td>${recruit.email}</td>
				<td><fmt:formatDate value="${recruit.interviewDate}" pattern="yyyy-MM-dd"/> </td>
				<td>${recruit.origin}</td>
				<td><fmt:formatDate value="${recruit.noticeDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${not empty recruit.origin}">
						<a target="_blank" href="${roster.innovateUrl}">查看</a>
					</c:if>
				</td>
				<td>${recruit.interviewReview1}</td>
				<td>${recruit.interviewReview2}</td>
				<td>
					<shiro:hasPermission name="oa:recruit:edit">
	    				<a class="btn btn-small" href="${ctx}/oa/recruit/form?id=${recruit.id}">修改</a>&nbsp;&nbsp;&nbsp;&nbsp;
    				</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
