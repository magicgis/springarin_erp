<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊产品关键字搜索</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		if(!top){
			top = self;
		}
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			
			$("#searchForm").validate({
				submitHandler: function(form){
					$("#submit").attr("disabled","disabled");
					loading('正在玩命查询中...');
					var params = {};
					params.country = $("#selectC").val();
					params.key = encodeURI($("#key").val());
					$("#rs").text("");
					$.get("${ctx}/amazoninfo/businessReport/result?"+$.param(params),function(data){
							eval("var map ="+data);
							 for(key in map){
								var temp ="";
								$(map[key]).each(function(i){
									temp = temp+"&nbsp;&nbsp;&nbsp;"+this+"<br/>";
								});
								var td = "<tr><td><h5>"+key+"</h5></td><td>"+temp+"</td></tr>";
								$("#rs").append(td);	
							} 
						top.$.jBox.closeTip(); 
						$("#submit").removeAttr("disabled");
					});
					return false;
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			$("#export").click(function(){
				var params = {};
				params.country = $("#selectC").val();
				params.key = encodeURI($("#key").val());
				window.location.href="${ctx}/amazoninfo/businessReport/exportKeySearch?"+$.param(params);
			})
		});
	</script>
</head>
<body>	
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/amazoninfo/businessReport/search">关键字查询</a></li>
	    <li><a href="${ctx}/amazoninfo/amazonPortsDetail/duplicateRemoval">关键字去重</a></li>
	</ul>
	<form:form id="searchForm" class="breadcrumb form-search">
		<select  style="width: 100px" name="country" id="selectC">
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek'}">
						<option value="${dic.value}">${dic.label}</option>
					</c:if>
				</c:forEach>	
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="text" id="key" name="key" class="required" /> <input id="submit" type="submit" class="btn btn-primary" value="查询"/>
		&nbsp;&nbsp;
		<input id="export" type="button" class="btn btn-primary" value="导出"/>
	</form:form>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
					<th width="200px">关键字</th>
					<th>查找结果</th>
		</tr></thead>
		<tbody id="rs"></tbody>
	</table>
</body>
</html>
