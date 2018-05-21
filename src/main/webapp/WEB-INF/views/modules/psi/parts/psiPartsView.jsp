<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品配件管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	pre {
		border-style: none
		}
	#imgtest{  position:absolute;
	         top:50px; 
	         left:100px; 
	         z-index:1; 
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
			
			$(".image1").mouseover(function(e) { 
				if($(this).is("img")){ 
					var img=$("<img id='tipImg' src='"+$(this).attr("src")+"'>").css({ "height":$(this).height()*3, "width":$(this).width()*3	});
					img.appendTo($("#imgtest"));
				}
			});
			
			$(".image1").mouseout(function() { 
				$("#tipImg").remove();
			}); 
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiParts/">产品配件列表</a></li>
		<li class="active"><a href="${ctx}/psi/psiParts/form?id=${psiParts.id}">产品配件查看</a></li>
	</ul><br/>
	<div id="imgtest"></div> 
	<form:form id="inputForm" modelAttribute="psiParts" action="" method="post" class="form-horizontal">
		<div class="control-group">
				<label class="control-label">图片：</label>
				<div class="controls" style="height:143px">
					<div id="uploadPreview"></div>
					<img  src="<c:url value="${psiParts.image}"/>"class="image1" style="width:150px;height:150px"/>
				</div>
			</div>
		<div class="control-group">
			<label class="control-label">名称:</label>
			<div class="controls">
				<form:input path="partsName"    disabled="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">配件类型:</label>
			<div class="controls">
			<form:input path="partsType"   disabled="true"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">供应商:</label>
			<div class="controls">
			<input type="text"   readonly value="${psiParts.supplier.nikename}"/>
			</div>
		</div>
		
		<shiro:hasPermission name="psi:product:viewPrice">
			<div class="control-group">
				<label class="control-label">美元价格:</label>
				<div class="controls">
					<form:input path="price"   disabled="true" />
				</div>
			</div>
		
			<div class="control-group">
				<label class="control-label">人民币价格:</label>
				<div class="controls">
					<form:input path="rmbPrice"   disabled="true" />
				</div>
			</div>
		</shiro:hasPermission>
		<shiro:lacksPermission name="psi:product:viewPrice">
		<c:if test="${fns:getUser().id eq psiParts.createUser.id}">
			<div class="control-group">
				<label class="control-label">美元价格:</label>
				<div class="controls">
					<form:input path="price"   disabled="true" />
				</div>
			</div>
		
			<div class="control-group">
				<label class="control-label">人民币价格:</label>
				<div class="controls">
					<form:input path="rmbPrice"   disabled="true" />
				</div>
			</div>
		</c:if>
		
		</shiro:lacksPermission>
		
		<div class="control-group">
			<label class="control-label">生产周期</label>
			<div class="controls">
				<form:input  path="producePeriod" disabled="true" />
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">Moq</label>
			<div class="controls">
				 <form:input  path="moq"  disabled="true" />
			</div>
		</div>
		
		<c:if test="${not empty psiParts.attchmentPath}">
		<div class="control-group" >
		<label class="control-label" >合同:</label>
			<div class="controls">
				<c:forEach items="${fn:split(psiParts.attchmentPath,',')}" var="attchment" varStatus="i">
					<span><a target="_blank" href="<c:url value='/data/site/${attchment}'/>">合同 ${i.index+1}</a></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach> 
			</div>  
		</div>
		</c:if>
	
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="4"  class="input-xxlarge"   disabled="true"></form:textarea>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">描述:</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4"  class="input-xxlarge"   disabled="true"></form:textarea>
			</div>
		</div>
		
		<shiro:hasPermission name="psi:product:viewPrice">
			<div class="control-group">
				<label class="control-label">价格变动记录</label>
				<div class="controls">
					 ${psiParts.priceChangeLog}
				</div>
			</div>
		</shiro:hasPermission>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
