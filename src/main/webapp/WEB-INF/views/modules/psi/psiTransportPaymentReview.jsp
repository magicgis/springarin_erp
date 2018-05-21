<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单付款管理</title>
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
				top.$.jBox.confirm('您确定要审核通过该运单付款吗','系统提示',function(v,h,f){
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
		<li><a href="${ctx}/psi/psiTransportPayment/">运单付款列表</a></li>
		<li class="active"><a href="#">审核运单付款</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiTransportPayment" action="${ctx}/psi/psiTransportPayment/reviewSave" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input name="id" 		 type="hidden" value="${psiTransportPayment.id}"/>
		<input name="paymentNo"  type="hidden" value="${psiTransportPayment.paymentNo}"/>
		<input name="supplierCostPath"  type="hidden" value="${psiTransportPayment.supplierCostPath}"/>
 		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:30%;height:25px">
				<label class="control-label" style="width:80px" ><b>承运商</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					<input readonly="readonly" value="${psiTransportPayment.supplier.nikename}" type="text" />
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:70%;height:25px" >
					<label class="control-label" style="width:80px"><b>承运商账号</b>:</label>
					<div class="controls" style="margin-left:100px">
						<input readonly="readonly" style="width:98%" value="${psiTransportPayment.account}" type="text"  />
					</div>
			</div>
		</div>
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 15%">运单号</th>
				   <th style="width: 10%">付款种类</th>
				   <th style="width: 10%">应付金额</th>
				   <th style="width: 10%">币种</th>
				   <th style="width: 10%">汇率</th>
				   <th style="width: 10%">转换后金额</th>
				   <th style="width: 15%">备注</th>
				   
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${ItemMap}" var="tranMap">
			<c:forEach items="${tranMap.value}" var="item" varStatus="i">
			<tr>
				<c:choose>
					<c:when  test="${i.index==0}">
					<td style="text-align: center;vertical-align: middle;" rowspan="${fn:length(tranMap.value)}"><input style="width:90%" type="text"  readonly="readonly" value="${item.transportNo}" /></td>
						<td><input style="width:90%" type="text"  readonly="readonly" value="${item.paymentType}" /></td>
					</c:when>
					<c:otherwise>
						<td><input type="hidden" name="transportNo" value="${item.transportNo}"/><input style="width:90%" type="text" readonly="readonly" value="${item.paymentType}" /></td>
					</c:otherwise>
				</c:choose>

				<td><input type="hidden" name="id" value="${item.id}" /><input style="width:90%" type="text"  readonly="readonly" value="${item.paymentAmount}" class="paymentAmount"/></td>
				<td><input style="width:90%" type="text"  readonly="readonly" value="${item.currency}" /></td>
				<td><input style="width:90%" type="text" readonly="readonly" class="rate" value="${item.rate}"/></td>
				<td><input style="width:90%" type="text" readonly="readonly" class="afterAmount" value="${item.afterAmount}"/></td>
				<td><input style="width:90%" type="text" readonly="readonly" value="${item.remark}"/></td>
			</tr>
			</c:forEach>
			
		</c:forEach>
		</tbody>
		</table>
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px" ><b>总额:</b></label>
				<div class="controls" style="margin-left:100px" >
				<input class="totalAmount" type="text" readonly="readonly"  style="width:100%" value="${psiTransportPayment.beforeAmount}" />
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label"  style="width:80px" >汇率:</label>
				<div class="controls" style="margin-left:100px" >
					<input class="totalRate" readonly="readonly" type="text" style="width:100%" value="${psiTransportPayment.rate}" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px"><b>付款金额:</b></label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" readonly="readonly" id="paymentAmount" value="${psiTransportPayment.paymentAmount}" style="width:100%" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:23%;height:25px">
				<label class="control-label" style="width:80px" ><b>货币类型:</b></label>
				<div class="controls" style="margin-left:100px" >
				<input class="currency" readonly="readonly"  type="text" style="width:100%" value="${psiTransportPayment.currency}" />
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:98%;">
				<label class="control-label" style="width:80px">备注:</label>
				<div class="controls" style="margin-left:100px">
					<textarea readonly="readonly"  style="width:100%;height:80px;" >${psiTransportPayment.remark}</textarea>
				</div>
			</div>
		</div>
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 14px">查看供应商费用明细</p>
		</blockquote>
		
		<div class="control-group" style="float:left;width:98%;">
			<div class="controls">
			<c:forEach items="${fileMap}" var="fileMap">
					<c:forEach items="${fn:split(fileMap.value,',')}" var="attchment" varStatus="j">
						<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">${fileMap.key}_${j.index+1}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
			</c:forEach>
			</div>
		</div>
		
		<div class="form-actions" style="float:left;width:98%">
			<c:if test="${psiTransportPayment.paymentSta eq '1'}">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="审核"/>&nbsp;&nbsp;&nbsp;&nbsp;
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
