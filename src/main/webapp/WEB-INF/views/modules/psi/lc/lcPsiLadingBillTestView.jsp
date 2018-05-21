<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单确认</title>
	<meta name="decorator" content="default"/>
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
			
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/lcPsiLadingBill/">收货单列表</a></li>
		<li class="active"><a href="#">品检信息查看</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="test" action="${ctx}/psi/lcPsiLadingBill/qualityTestSave" method="post" class="form-horizontal" enctype="multipart/form-data">
	    <input type='hidden' name="ladingId" value="${id}">
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>提单号</b>:</label>
				<div class="controls" >
				<span>
				<input type="text" readonly   value="${ladingBillNo}" />
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:33%" >
				<label class="control-label"><b>产品</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" readonly   value="${productName}"/>
				</span>
				
				</div>
			</div>
		</div>
		
		<c:forEach items="${tests}" var="test">
		<blockquote style="float:left;width:100%">
			<div style="float:left;display: inline-block;">
				<div style="float:left;">
				<p style="font-size: 14px" style=""><b>${test.createUser.name}于<fmt:formatDate value="${test.createDate}" pattern="yyyy-MM-dd"/>品检产品： ${test.productNameColor},品检单[${test.id}]状态：
					<c:choose>
						<c:when test="${test.testSta eq 0}"><span class="label label-important">预申请<c:if test="${payDate>test.createDate}">(已付款)</c:if></span></c:when>
						<c:when test="${test.testSta eq 3}"><span class="label label-warning">申请审核<c:if test="${payDate>test.createDate}">(已付款)</c:if></span></c:when>
						<c:when test="${test.testSta eq 5}"><span class="label  label-success">品检主管已确认<c:if test="${payDate>test.createDate}">(已付款)</c:if></span></c:when>
						<c:when test="${test.testSta eq 8}"><span class="label  label-inverse">取消</span></c:when>
					</c:choose>
				具体信息如下：</b></p>
				</div>
				<div style="float:left;">
				<shiro:hasPermission name="psi:ladingBill:qualityTest">
				<c:if test="${test.testSta eq '0'}">
					<a  href="${ctx}/psi/lcPsiLadingBill/qualityTest?id=${test.id}" ><input class="btn btn-primary" style="width:80px" value="编辑"/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<a  href="${ctx}/psi/lcPsiLadingBill/applyTest?id=${test.id}" onclick="return confirmx('确认要申请审核吗？', this.href)" ><input class="btn btn-success" style="width:80px" value="申请审核"/></a>
					<c:if test="${payDate>test.createDate}"><a  href="${ctx}/psi/lcPsiLadingBill/cancelTest?id=${test.id}" onclick="return confirmx('确认要取消该吗？', this.href)" ><input class="btn btn-warning" style="width:80px" value="取消"/></a></c:if>
				</c:if>
				<c:if test="${test.testSta eq '3'}">
					<c:if test="${empty payDate || payDate<test.createDate}">
						<a  href="${ctx}/psi/lcPsiLadingBill/cancelTestDraft?id=${test.id}" onclick="return confirmx('确认要取消成预申请吗？', this.href)" ><input class="btn btn-warning" style="width:80px" value="取消成预申请"/></a>
					</c:if>
				</c:if>
					<a  href="${ctx}/psi/lcPsiLadingBill/updateTestFile?id=${test.id}" ><input class="btn btn-primary" style="width:80px" value="更新质检报告"/></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</shiro:hasPermission>
				</div>
			</div>
		</blockquote>
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>AQL</b>:</label>
			<div class="controls" >
			<span>
			${test.aql}
			</span>
			</div>   
		</div>
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:12%;height:23px">
				<label class="control-label"><b>订单数</b>:</label>
				<div class="controls" >
				<span>
				${test.totalQuantity}
				</span>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:12;height:23px%">
				<label class="control-label"><b>抽样数</b>:</label>
				<div class="controls" >
				<span>
				${test.testQuantity}
				</span>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:12%;height:23px">
				<label class="control-label"><b>合格数</b>:</label>
				<div class="controls" >
				<span>
				${test.okQuantity}
				</span>
				</div>
			</div>
			<div class="control-group" style="float:left;width:12%;height:23px">
				<label class="control-label"><b>接收数</b>:</label>
				<div class="controls" >
				<span>
				${test.receivedQuantity}
				</span>
				</div>
			</div>
			<div class="control-group" style="float:left;width:50%;height:23px">
				<label class="control-label"></label>
				<div class="controls" style="heigth:30px" >
				<span>
				</span>
				</div>
			</div>
		</div>
	
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>内观</b>:</label>
			<div class="controls" >
			<span>
			${test.inView}
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>功能</b>:</label>
			<div class="controls" >
			<span>
			${test.function}
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>外观</b>:</label>
			<div class="controls" >
			<span>
			${test.outView}
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>包装</b>:</label>
			<div class="controls" >
			<span>
			${test.packing}
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>检验结果判定</b>:</label>
			<div class="controls" >
			<span>
			   	<c:choose>
					<c:when test="${test.isOk eq '1' }">合格</c:when>
					<c:when test="${test.isOk eq '0' }">不合格</c:when>
					<c:when test="${test.isOk eq '2' }">部分合格</c:when>
				</c:choose>
			</span>
			</div>
		</div>
		
		<c:if test="${not empty test.dealWay }">
			<div class="control-group" style="float:left;width:100%">
				<label class="control-label"><b>不合格处理方式</b>:</label>
				<div class="controls" >
				<span>
				<c:choose>
					<c:when test="${test.dealWay eq '0' }">特采</c:when>
					<c:when test="${test.dealWay eq '1' }">让步接收</c:when>
					<c:when test="${test.dealWay eq '2' }">返工</c:when>
				</c:choose>
				</span>
				</div>
			</div>
		</c:if>
		
		<c:if test="${not empty test.reportFile }">
			<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>品检报告上传</b>:</label>
				<div class="controls">
					<c:forEach items="${fn:split(test.reportFile,',')}" var="attchment">
						<a href="${ctx}/psi/lcPsiLadingBill/download?fileName=/${attchment}">品检报告${i.index+1}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
				</div>
			</div>
		</c:if>
		
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
		
		
		</c:forEach>
		
	</form:form>
</body>
</html>
