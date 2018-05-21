<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Post Health Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<style type="text/css">
	.sort {
		color: #0663A2;
		cursor: pointer;
	}
	
	.blue {
		color: #8A2BE2;
	}
	
	.spanexr {
		float: right;
		min-height: 40px;
		width: 1000px
	}
	
	.spanexl {
		float: left;
	}
	
	.footer {
		padding: 20px 0;
		margin-top: 20px;
		border-top: 1px solid #e5e5e5;
		background-color: #f5f5f5;
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
		if(!(top)){
			top = self;			
		}	
		$(function(){
				
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$(".countryHref").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/postsHealth');
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 20,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
			});
			
			$(".spanexr div:first").append($("#searchDiv").html());
			$("#searchDiv").remove();
			
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${postsHealth.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<div id="searchDiv" style="display: none;float:right;" >
		<form:form id="searchForm" modelAttribute="postsHealth" action="${ctx}/amazoninfo/postsHealth" method="post" cssStyle="float:right" >
			<input  name="country" type="hidden" value="${postsHealth.country}"/>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/amazoninfo/postsHealth');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="date" value="<fmt:formatDate value="${postsHealth.date}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		</form:form>
	</div>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
				   <th style="width:150px;">Sku</th>
				   <th style="width:150px;">productName</th>
				   <th style="width:150px;">field-name</th>
				   <th style="width:80px">alert-type</th>
				   <th style="width:70px">current-value</th>
				   <th style="width:120px" >last-updated</th>
				   <th >explanation</th>
			  </tr>
		</thead>
		<tbody>
		<c:forEach items="${data}" var="posts">
			<tr>
				<td><a href="${posts.link}" target="_blank">${posts.sku}</a></td>
				<td><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${posts.productName}">${fns:abbr(posts.productName,15)}</a></td>
				<td>${posts.fieldName}</td>
				<td>${posts.alertType}</td>
				<td>${posts.currentValue}</td>
				<td>${posts.lastUpdated}</td>
				<td>${posts.explanation}</td>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
