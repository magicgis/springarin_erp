<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品经理核对品检</title>
	<%@include file="/WEB-INF/views/mobile/include/head.jsp" %>
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
		
		
		$(document).ready(function() {
			
			$("#inputForm").validate({
				submitHandler: function(form){
					
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
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
			
			
			$("#btnSubmit").on("click",function(){
				var ids ="";
				$(".checkBox").each(function(){
					var id = $(this).parent().parent().find(".testId").val();
					if(this.checked){
						ids=ids+id+",";
					}
				});
				if(ids.length==0){
					top.$.jBox.tip("请选择要提交的质检单","info",{timeout:3000});
					return false;
				}
				$("input[name='ids']").val(ids.substring(0,ids.length-1));
				$("#inputForm").submit();
				$("#btnSubmit").attr("disabled","disabled");
			});
			
			
		});
			
	</script>
</head>
<body>
<div data-role="page" id="home">
	<jsp:include page="../../sys/headDiv.jsp"></jsp:include>
	<div data-role="content">
		<form:form id="inputForm"  action="${ctx}/psi/lcPsiLadingBill/managerReivewSave" method="post" class="form-horizontal" enctype="multipart/form-data">
			<input type="hidden" name="ids" value="${ids}"/>
			
			<c:if test="${empty tests }">
				<blockquote>
					<p style="font-size: 14px"><b>没有需要确认的信息</b></p>
				</blockquote>
			</c:if>
			<c:forEach items="${tests}" var="test">
			<blockquote>
				<p style="font-size: 14px"><input type="checkBox" class="checkBox"/> <input type="hidden" class="testId" value="${test.id}"/><b><fmt:formatDate value="${test.createDate}" pattern="yyyy-MM-dd"/> ${test.createUser.name}品检${test.productNameColor}信息如下：</b></p>
			</blockquote>
			
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
				<label style="width:30%;margin-right:5px" class="control-label"><b>AQL</b>:</label>
				${test.aql}
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
				<label style="width:30%;margin-right:5px" class="control-label"><b>品检报告上传</b>:</label>
				<c:forEach items="${fn:split(test.reportFile,',')}" var="attchment">
					<a href="${ctx}/psi/lcPsiLadingBill/download?fileName=/${attchment}">品检报告${i.index+1}</a>
					&nbsp;&nbsp;&nbsp;  
				</c:forEach>
				</div>
			</c:if>
			
			<c:if test="${not empty test.giveInFile }">
				<div>
					<label style="width:30%;margin-right:5px" class="control-label"><b>供应商特采申请单<br/>让步联络函上传</b>:</label>
					<c:forEach items="${fn:split(test.giveInFile,',')}" var="attchment">
						<a href="${ctx}/psi/lcPsiLadingBill/download?fileName=/${attchment}">特采申请单${i.index+1}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
				</div>
			</c:if>
			</c:forEach>
			
			<div style="text-align:center">
				<c:if test="${not empty tests }">
					<input id="btnSubmit" class="btn btn-primary" type="button" value="确认"/>
				</c:if>
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
			</form:form>
		</div>
		<jsp:include page="../../sys/footDiv.jsp"></jsp:include>
	</div>
</body>
</html>
