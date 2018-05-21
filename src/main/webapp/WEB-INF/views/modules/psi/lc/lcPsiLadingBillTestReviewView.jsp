<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>不合格订单处理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
			
		$(document).ready(function() {
			if(!(top)){
				top = self; 
			}
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/psiLadingBill/">收货单列表</a></li>
		<li ><a href="${ctx}/psi/lcPsiLadingBill/">(理诚)收货单列表</a></li>
		<li ><a href="${ctx}/psi/lcPsiLadingBill/testList">不合格质检列表</a></li>
		<li class="active"><a >不合格处理查看</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="test" action="" method="post" class="form-horizontal" enctype="multipart/form-data">
	    <input type='hidden' name="id" value="${test.id}" id="testId">
	    
	    <blockquote style="float:left;width:100%;">
			<p style="font-size: 14px">质检单信息：</p>
		</blockquote>
		
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:25%">
				<label class="control-label" style="width:80px"><b>提单号</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.ladingBillNo}
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:25%" >
				<label class="control-label" style="width:80px"><b>产品</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.productNameColor}
				</span>
				
				</div>
			</div>
			<div class="control-group"  style="float:left;width:25%" >
				<label class="control-label" style="width:80px"><b>品检人</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.createUser.name}
				</span>
				
				</div>
			</div>
			<div class="control-group"  style="float:left;width:25%" >
				<label class="control-label" style="width:80px"><b>品检日期</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span>
						<fmt:formatDate value="${test.createDate}" pattern="yyyy-MM-dd"/>
					</span>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:50%">
				<label class="control-label" style="width:80px"><b>AQL</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.aql}
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:50%" >
				<label class="control-label" style="width:80px"><b>功能</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.function}
				</span>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:50%">
				<label class="control-label" style="width:80px"><b>内观</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.inView}
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:50%" >
				<label class="control-label" style="width:80px"><b>外观</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.outView}
				</span>
				</div>
			</div>
		</div>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:50%">
				<label class="control-label" style="width:80px"><b>包装</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.packing}
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:50%" >
				<label class="control-label" style="width:80px"><b>数量</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.totalQuantity}
				</span>
				</div>
			</div>
		</div>
		
		
		<blockquote style="float:left;width:100%;">
			<p style="font-size: 14px">各方处理意见：</p>
		</blockquote>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:100%;height:100px">
				<label class="control-label" style="width:80px"><b>采购意见</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span>
						<textarea rows="4" cols="5" name="reviewRemark1" readonly="readonly" class="remark" style="width:90%">${test.reviewRemark1}</textarea>
					</span>
				</div>
			</div>
		</div>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:100%;height:100px">
				<label class="control-label" style="width:80px"><b>产品意见</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span>
						<textarea rows="4" cols="5" name="reviewRemark2" readonly="readonly" class="remark" style="width:90%">${test.reviewRemark2}</textarea>
					</span>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:100%;height:100px">
				<label class="control-label" style="width:80px"><b>品检意见</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span>
						<textarea rows="4" cols="5" name="reviewRemark3" readonly="readonly" class="remark"  style="width:90%;">${test.reviewRemark3}</textarea>
					</span>
				</div>
			</div>
		</div>
		
		<blockquote style="float:left;width:100%;">
			<p style="font-size: 14px">处理结果：</p>
		</blockquote>
		
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"  style="width:80px"><b>处理方式</b>:</label>
			<div class="controls" style="margin-left:100px">
			<span>
			<c:if test="${test.dealWay eq '0'}">特采</c:if>
			<c:if test="${test.dealWay eq '2'}">返工</c:if>
			</span>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:90%;height:100px">
				<label class="control-label" style="width:80px"><b>原因</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span>
						<textarea rows="4" cols="5" name="reviewRemark" readonly="readonly" style="width:90%;">${test.reviewRemark}</textarea>
					</span>
				</div>
			</div>
		</div>
		
		<c:if test="${not empty test.giveInFile }">
			<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>供应商特采申请单</b>:</label>
				<div class="controls">
					<c:forEach items="${fn:split(test.giveInFile,',')}" var="attchment">
						<a href="${ctx}/psi/lcPsiLadingBill/download?fileName=/${attchment}">特采申请单${i.index+1}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>  
				</div>
			</div>
		</c:if>
	</form:form>
</body>
</html>
