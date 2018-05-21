<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品配件管理</title>
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
			$("#partsType,#supplier").on("click",function(){
				$("#searchForm").submit();
			});
			
			$(".image1").mouseover(function(e) { 
				if($(this).is("img")){ 
					var img=$("<img id='tipImg' src='"+$(this).attr("src")+"'>").css({ "height":$(this).height()*10, "width":$(this).width()*10	});
					img.appendTo($("#imgtest"));
				}
			});
			
			$(".image1").mouseout(function() { 
				$("#tipImg").remove();
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
	 <style type="text/css">
	  #imgtest{  position:absolute;
	         top:100px; 
	         left:200px; 
	         z-index:1; 
	         } 
	  </style>  
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="">产品配件列表</a></li>
		<li ><a href="${ctx}/psi/psiParts/form">增加产品配件</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiParts" action="${ctx}/psi/psiParts/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>配件名称 ：</label><input type="text" name="partsName" value="${psiParts.partsName}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>配件类型：</label> 
		<form:select path="partsType" style="width: 150px" id="partsType">
				<option value=""  ${psiParts.partsType eq '' ?'selected':'' }>全部</option>
				<c:forEach items="${fns:getDictList('parts_type')}" var="dic">
						 <option value="${dic.value}" ${psiParts.partsType eq dic.value ?'selected':''}  >${dic.label}</option>
				</c:forEach>	
		</form:select>
		
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>供应商：</label> 
		<select style="width:150px;" id="supplier" name="supplier.id">
			<option value="" ${psiParts.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
			<c:forEach items="${suppliers}" var="supplier" varStatus="i">
				 <option value='${supplier.id}' ${supplier.id eq  psiParts.supplier.id?'selected':''}>${supplier.nikename}</option>;
			</c:forEach>
		</select>
		
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<a href="${ctx}/psi/productParts"><input id="btnMixtureRatio" class="btn btn-success" style="float: right;margin-right:100px" type="button" value="产品配件配比设置"/></a>
	</form:form>
	<tags:message content="${message}"/>
	
	<div id="imgtest"></div> 
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:5%">序号</th><th style="width:60px">图片</th><th style="width:20%">配件型号</th><th style="width:10%" >配件类型</th><th style="width:10%">供应商</th><th style="width:20%">描述</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiParts">
			<tr>
				<td>${psiParts.id}</td>
				<td><img  src="<c:url value="${psiParts.image}"/>"class="image1" style="width:60px;height:50px"/></td>
				<td><a href="${ctx}/psi/psiParts/view?id=${psiParts.id}">${psiParts.partsName}</a></td>
				<td>${psiParts.partsType}</td>
				<td>${psiParts.supplier.nikename}</td>
				<td>${psiParts.description}</td>
				<td>
    				<a class="btn btn-small" href="${ctx}/psi/psiParts/form?id=${psiParts.id}">修改</a>
					<a class="btn btn-small" href="${ctx}/psi/psiParts/delete?id=${psiParts.id}" onclick="return confirmx('确认要删除该产品配件吗？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
