<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单确认</title>
	<%@include file="/WEB-INF/views/mobile/include/head.jsp" %>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		function doChange(info, href){
	 		top.$.jBox.confirm(info,'系统提示',function(v,h,f){
				if(v=='ok'){
					window.location.href=href;
				}
				return true;
			},
				{buttonsFocus:1,persistent: true}
			);
		}
	</script>
</head>
<body>
<div data-role="page" id="home">
	<jsp:include page="../../sys/headDiv.jsp"></jsp:include>
	<div data-role="content">
	<form:form id="inputForm" modelAttribute="test" action="${ctx}/psi/lcPsiLadingBill/qualityTestSave" method="post" class="form-horizontal" enctype="multipart/form-data">
	    <input type='hidden' name="ladingId" value="${id}">
		<div data-role="fieldcontain">
			<label for="ladingBillNo">提单号：</label> 
			<input type="text" value="${ladingBillNo}" class="required" readonly/>
		</div>
		<div data-role="fieldcontain">
			<label for="productName">产品名称：</label> 
			<input type="text" value="${productName}" class="required" readonly/>
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
					<input id="btnEdit" class="btn btn-primary" type="button" value="编辑" onclick="doChange('确认要重新编辑吗？', '${ctx}/psi/lcPsiLadingBill/qualityTest?id=${test.id}')"/>
					<input id="btnSave" class="btn btn-info" type="button" value="申请审核" onclick="doChange('确认要申请审核吗？', '${ctx}/psi/lcPsiLadingBill/applyTest?id=${test.id}')"/>
					<c:if test="${payDate>test.createDate}">
						<input id="btnCancelTest" class="btn btn-warning" type="button" value="取消" onclick="doChange('确认要取消吗？', '${ctx}/psi/lcPsiLadingBill/cancelTest?id=${test.id}')"/>
					</c:if>
				</c:if>
				<c:if test="${test.testSta eq '3'}">
					<c:if test="${empty payDate || payDate<test.createDate}">
						<input id="btnCancel" class="btn btn-warning" type="button" value="取消成预申请" onclick="doChange('确认要取消成预申请吗？', '${ctx}/psi/lcPsiLadingBill/cancelTestDraft?id=${test.id}')"/>
					</c:if>
				</c:if>
				</shiro:hasPermission>
				</div>
			</div>
		</blockquote>
		
			<div class="control-group">
				<label style="width:30%;margin-right:5px;" class="control-label"><b>AQL</b>:</label>
				<div>${test.aql}</div>
			</div>
			<div class="control-group" >
				<label style="width:30%;margin-right:5px" class="control-label"><b>抽样数</b>:</label>
				${test.testQuantity}
			</div>
			<div class="control-group" >
				<label style="width:30%;margin-right:5px" class="control-label"><b>订单数</b>:</label>
				${test.totalQuantity}
			</div>
			<div class="control-group" >
				<label style="width:30%;margin-right:5px" class="control-label"><b>合格数</b>:</label>
				${test.okQuantity}
			</div>
			<div class="control-group" >
				<label style="width:30%;margin-right:5px" class="control-label"><b>接收数</b>:</label>
				${test.receivedQuantity}
			</div>
			<div class="control-group">
				<label style="width:30%;margin-right:5px" class="control-label"><b>内观</b>:</label>
				${test.inView}
			</div>
			<div class="control-group">
				<label style="width:30%;margin-right:5px" class="control-label"><b>功能</b>:</label>
				${test.function}
			</div>
			<div class="control-group">
				<label style="width:30%;margin-right:5px" class="control-label"><b>外观</b>:</label>
				${test.outView}
			</div>
			<div class="control-group">
				<label style="width:30%;margin-right:5px" class="control-label"><b>包装</b>:</label>
				<div>${test.packing}</div>
			</div>
			<div class="control-group">
				<label style="width:30%;margin-right:5px" class="control-label"><b>检验结果判定</b>:</label>
			   	<c:choose>
					<c:when test="${test.isOk eq '1' }">合格</c:when>
					<c:when test="${test.isOk eq '0' }">不合格</c:when>
					<c:when test="${test.isOk eq '2' }">部分合格</c:when>
				</c:choose>
			</div>
			
			<c:if test="${not empty test.dealWay}">
				<div class="control-group">
					<label style="width:30%;margin-right:5px" class="control-label"><b>处理方式</b>:</label>
					<c:choose>
						<c:when test="${test.dealWay eq '0' }">特采</c:when>
						<c:when test="${test.dealWay eq '1' }">让步接收</c:when>
						<c:when test="${test.dealWay eq '2' }">返工</c:when>
					</c:choose>
				</div>
			</c:if>
			<c:if test="${not empty test.reason }">
				<div class="control-group">
					<label style="width:30%;margin-right:5px" class="control-label"><b>特采、让步接收的<br/>原因、条件</b>:</label>
					${test.reason}
				</div>  
			</c:if>
			
			<c:if test="${not empty test.reportFile }">
				<div class="control-group">
				<label style="width:30%;margin-right:5px" class="control-label"><b>品检报告</b>:</label>
				<c:forEach items="${fn:split(test.reportFile,',')}" var="attchment">
					<a href="${ctx}/psi/lcPsiLadingBill/download?fileName=/${attchment}">品检报告${i.index+1}</a>
					&nbsp;&nbsp;&nbsp;  
				</c:forEach>
				</div>
			</c:if>
		</c:forEach>
			
		<div style="text-align:center">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	</div>
		<jsp:include page="../../sys/footDiv.jsp"></jsp:include>
	</div>
</body>
</html>
