<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品阶梯价格</title>
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
			
			$("#inputForm").validate({
				submitHandler: function(form){
				loading('Please wait a moment!');
				form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("Entered incorrectly, please correct");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>

	<form:form id="inputForm" modelAttribute="priceDto" action="${ctx}/psi/productTieredPrice/reviewSave" method="post" class="form-horizontal">
	    <input type="hidden" name="id" value="${priceDto.id}">
	    <blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">基本信息.</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;">
				<label class="control-label" style="width:125px">供应商:</label>
				<div class="controls" style="margin-left:120px"><input style="width:80%" type="text" readonly="readonly" value="${priceDto.nikeName}" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:30%;">
				<label class="control-label" style="width:125px">产品:</label>
				<div class="controls" style="margin-left:120px"><input style="width:80%" type="text" readonly="readonly" value="${priceDto.proNameColor}" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:35%;">
				<label class="control-label" style="width:125px">是否同步其他颜色:</label>
				<div class="controls" style="margin-left:120px">
				 <input style="width:80%" type="text" readonly="readonly" value="${priceDto.hasColor eq '1'?'同步':'不同步'}"/>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group"  style="float:left;width:30%;height:70px" >
				<label class="control-label">供应商凭证:</label>
				<div class="controls">
					<a  target="_blank" class="btn btn-success" href="<c:url value='${priceDto.filePath}'/>">点击查看</a>
				</div>
			</div>
			<div class="control-group" style="float:left;width:70%;height:70px" >
				<label class="control-label" style="width:125px">改价原因:</label>
				<div class="controls" style="margin-left:120px">
					<textarea style="width:90%"  readonly="readonly">${priceDto.content}&nbsp;&nbsp;&nbsp;&nbsp;${priceDto.remark}</textarea>
				</div>
			</div>
		</div>
		
	  
	    <blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">价格改动信息.(此处为<span style='color:red'>不含税</span>)价格</p>
		</blockquote>
	  
	  	<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:80%;">
				<div class="controls" style="margin-left:120px">
				<b>&nbsp;&nbsp;500档价格</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				改前:<b><c:if test="${not empty priceDto.before500cny}"><fmt:formatNumber value="${priceDto.before500cny}"    pattern="0.##"/></c:if></b> &nbsp;&nbsp;>>>&nbsp;&nbsp;改后:<b>${priceDto.leval500cny}</b>
				</div>
			</div>
		</div>
		
	  	<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:80%;">
				<div class="controls" style="margin-left:120px">
				<b>&nbsp;1000档价格</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				改前:<b><c:if test="${not empty priceDto.before1000cny}"><fmt:formatNumber value="${priceDto.before1000cny}"    pattern="0.##"/></c:if></b> &nbsp;&nbsp;>>>&nbsp;&nbsp;改后:<b>${priceDto.leval1000cny}</b>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:80%;">
				<div class="controls" style="margin-left:120px">
				<b>&nbsp;2000档价格</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				改前:<b><c:if test="${not empty priceDto.before2000cny}"><fmt:formatNumber value="${priceDto.before2000cny}"    pattern="0.##"/></c:if></b> &nbsp;&nbsp;>>>&nbsp;&nbsp;改后:<b>${priceDto.leval2000cny}</b>
				</div>
			</div>
		</div>
		
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:80%;">
				<div class="controls" style="margin-left:120px">
				<b>&nbsp;3000档价格</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				改前:<b><c:if test="${not empty priceDto.before3000cny}"><fmt:formatNumber value="${priceDto.before3000cny}"    pattern="0.##"/></c:if></b> &nbsp;&nbsp;>>>&nbsp;&nbsp;改后:<b>${priceDto.leval3000cny}</b>
				</div>
			</div>
		</div>
		
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:80%;">
				<div class="controls" style="margin-left:120px">
				<b>&nbsp;5000档价格</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				改前:<b><c:if test="${not empty priceDto.before5000cny}"><fmt:formatNumber value="${priceDto.before5000cny}"    pattern="0.##"/></c:if></b> &nbsp;&nbsp;>>>&nbsp;&nbsp;改后:<b>${priceDto.leval5000cny}</b>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:80%;">
				<div class="controls" style="margin-left:120px">
				<b>10000档价格</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				改前:<b><c:if test="${not empty priceDto.before10000cny}"><fmt:formatNumber value="${priceDto.before10000cny}"    pattern="0.##"/></c:if></b> &nbsp;&nbsp;>>>&nbsp;&nbsp;改后:<b>${priceDto.leval10000cny}</b>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:80%;">
				<div class="controls" style="margin-left:120px">
				<b>15000档价格</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				改前:<b><c:if test="${not empty priceDto.before15000cny}"><fmt:formatNumber value="${priceDto.before15000cny}"    pattern="0.##"/></c:if></b> &nbsp;&nbsp;>>>&nbsp;&nbsp;改后:<b>${priceDto.leval15000cny}</b>
				</div>
			</div>
		</div>
		
			
		<div style="float:left;width:100%" class="form-actions">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="审核通过"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				
				<a  class="btn "  href="${ctx}/psi/productTieredPrice/cancelPrice?id=${priceDto.id}" onclick="return confirmx('确认要取消吗？', this.href)">取消</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</form:form>
</body>
</html>
