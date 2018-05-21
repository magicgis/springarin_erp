<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购付款管理</title>
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
					top.$.jBox.confirm('您确定要确认付款吗','系统提示',function(v,h,f){
						if(v=='ok'){
							form.submit();
							$("#btnSubmit").attr("disabled","disabled");
						}
						},{buttonsFocus:1,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
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
		<li ><a href="${ctx}/psi/lcPsiPartsPayment/">配件订单付款列表</a></li>
		<li class="active"><a href="#">配件订单付款确认</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiPartsPayment" action="${ctx}/psi/lcPsiPartsPayment/sureSave" method="post" class="form-horizontal" enctype="multipart/form-data"  >
		<input type='hidden'  name="id" value="${psiPartsPayment.id}"/>
		 <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
			<div style="float:left;width:100%;">
				<div class="control-group" style="float:left;width:30%">
					<label class="control-label" style="width:100px"><b>供应商</b>:</label>
					<div class="controls" style="margin-left:120px">
						<input type='text'   readonly="readonly"  value="${psiPartsPayment.supplier.nikename}"/>
					</div>
				</div>
				<div class="control-group"  style="float:left;width:70%" >
					<label class="control-label"  style="width:100px"><b>供应商账号</b>:</label>
					<div class="controls" style="margin-left:120px">
						<input type='text'   readonly="readonly"  style="width:100%"  value="${psiPartsPayment.account}"/>
					</div>
				</div>
		</div>
		<div class="control-group"  style="float:left;width:100%">
			<label class="control-label" style="width:100px"><b>备注</b>:</label>
			<div class="controls" style="margin-left:120px">
				<textarea  maxlength="255" style="height:50px;width:98%" name="remark"  >${psiPartsOrder.remark}</textarea>
			</div>
		</div>
				
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			   <th style="width: 10%">付款类型</th>
			   <th style="width: 20%">订单号</th>
			   <th style="width: 10%">付款金额</th>
			   <th style="width: 30%">备注</th>
			</tr>
			<c:forEach items="${psiPartsPayment.items}" var="item">
			<tr>
			<td><c:if test="${item.paymentType eq '0'}">订金</c:if><c:if test="${item.paymentType eq '1'}">尾款</c:if></td>
			<td>${item.billNo}</td>
			<td>${item.paymentAmount}</td>
			<td>${item.remark}</td>
			</tr>
			</c:forEach>
		</thead>
		<tbody>
		</tbody>
	</table>
		
		<div style="float:left;width:100%">
		  <blockquote>
			<p style="font-size: 14px">上传水单</p>
		</blockquote>
			<div class="control-group" style="float:left;width:100%">
				<label class="control-label"></label>
				<div class="controls">
				<input name="memoFile" type="file" id="myfileupload" required="required"/>
				</div>
			</div>
		</div>
		
		
		<div class="form-actions" style="float: left ;width:100%">
			<input id="btnSureSubmit" class="btn btn-primary" type="submit" value="确认" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
