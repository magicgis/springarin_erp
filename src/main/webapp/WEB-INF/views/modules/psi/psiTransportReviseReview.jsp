<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单修正付款管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
		<style type="text/css">
		.uploadPreview {
		    height:120px;     
		    width:100%;                     
		}
		.pic{
		    border:0; 
			margin:0; 
			padding:0; 
			max-width:200px; 
			width:expression(this.width>200?"200px":this.width); 
			max-height:120px; 
			height:expression(this.height>120?"120px":this.height); 
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
				
			$("#inputForm").validate({
				submitHandler: function(form){
					form.submit();
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
		});
			

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiTransportRevise/">运单付款修正列表</a></li>
		<li class="active"><a href="#">确认运单付款修正</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiTransportRevise" action="${ctx}/psi/psiTransportRevise/reviewSave" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id" value="${psiTransportRevise.id}"/>
		<input type="hidden" name="paymentNo" value="${psiTransportRevise.paymentNo}"/>
		<input type='hidden'  name="tranOrderId" value="${psiTransportRevise.tranOrderId}"/>
		<input type='hidden'  name="tranOrderNo" value="${psiTransportRevise.tranOrderNo}"/>
		<input type='hidden'  name="accountPath" value="${psiTransportRevise.accountPath}"/>
 		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:20%;height:25px">
				<label class="control-label" style="width:80px" ><b>运单号</b>:</label>
				<div class="controls" style="margin-left:100px">
				<input type="text" readonly name="tranOrderNo" value="${psiTransportRevise.tranOrderNo}" style="width:100%" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
				<label class="control-label" style="width:80px" ><b>承运商</b>:</label>
				<div class="controls" style="margin-left:100px">
				<input type="text" readonly name="supplier.nikename" value="${psiTransportRevise.supplier.nikename}" style="width:100%" />
				</div>
			</div>
			<div class="control-group"  style="float:left;width:60%;height:25px" >
					<label class="control-label" style="width:80px"><b>承运商账号</b>:</label>
					<div class="controls" style="margin-left:100px">
						<input type="text" readonly  value="${psiTransportRevise.account}" style="width:100%" />
					</div>
			</div>
		</div>
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed" >
		<thead>
			<tr>
				   <th style="width: 10%">付款种类</th>
				   <th style="width: 10%">运单金额</th>
				   <th style="width: 10%">币种</th>
				   <th style="width: 10%">差额</th>
				   <th style="width: 10%">汇率</th>
				   <th style="width: 10%">计算后额度</th>
				   <th style="width: 15%">备注</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${psiTransportRevise.items}" var="item">
				<tr>
					<td><input style="width:90%" type='text' name='reviseType' class='reviseType' readonly='readonly' value="${item.reviseType }"/></td>
					<td><input style="width:90%" type='text' name='oldAmount' class='oldAmount' readonly='readonly' value="${item.oldAmount }"/></td>
					<td><input style="width:90%" type='text' name='currency'  readonly='readonly' value="${item.currency }"/></td>
					<td><input style="width:90%" type='text' name='reviseAmount' readonly="readonly" class='reviseAmount' value="${item.reviseAmount }" /></td>
					<td><input style="width:90%" type='text' name='rate' class='rate' readonly="readonly" value="${item.rate }"/></td>
					<td><input style="width:90%" type='text' class='afterAmount' readonly='readonly' value="${item.reviseAmount*item.rate }"/></td>	
					<td><input style="width:90%" type='text' name='remark' readonly="readonly" value="${item.remark }"/></td>
				</tr>
			</c:forEach>
		</tbody>
		</table>
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 14px">付款信息</p>
		</blockquote>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px" ><b>总额:</b></label>
				<div class="controls" style="margin-left:100px" >
				<input class="totalAmount" type="text" readonly  style="width:100%" value="${psiTransportRevise.totalAmount}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label"  style="width:80px" ><b>汇率:</b></label>
				<div class="controls" style="margin-left:100px" >
					<input class="totalRate" name="rate" readonly="readonly" class="required"  type="text" style="width:100%" value="${psiTransportRevise.rate}" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px"><b>付款金额:</b></label>
				<div class="controls" style="margin-left:100px" >
					<input name="reviseAmount" type="text"  readonly="readonly" class="required" id="reviseAmount"  style="width:100%" value="${psiTransportRevise.reviseAmount}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:23%;height:25px">
				<label class="control-label" style="width:80px" ><b>货币类型:</b></label>
				<div class="controls" style="margin-left:100px" >
				<input class="currency" type="text" readonly  style="width:100%" value="${psiTransportRevise.currency}"/>
				</div>
			</div>
		</div>
		
		<div style="${not empty psiTransportRevise.accountPath}">
			<blockquote style="float:left;width:98%;">
				<p style="font-size: 14px">账单凭证</p>
			</blockquote>
			
			<div class="control-group" style="float:left;width:98%;">
				<div class="controls">
					<div>
					<c:forEach items="${fn:split(psiTransportRevise.accountPath,',')}" var="attchment" varStatus="i">
						<a target="_blank" href="<c:url value='/data/site${attchment}'/>">查看账单凭证${i.index+1}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
					</div>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:98%;">
				<label class="control-label" style="width:80px">备注:</label>
				<div class="controls" style="margin-left:100px">
					<textarea name="remark"  readonly="readonly" style="width:100%;height:80px;" >${psiTransportRevise.remark}</textarea>
				</div>
			</div>
		</div>
		
		
		
		<div class="form-actions" style="float:left;width:98%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="审核"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
