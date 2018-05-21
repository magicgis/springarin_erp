<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>营销计划管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		
			
			
		
			$("#inputForm").validate({
				
				submitHandler: function(form){
					loading('正在提交，请稍等...');
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
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiMarketingPlan/">营销计划列表</a></li>
		<li class="active"><a href="${ctx}/psi/psiMarketingPlan/form?id=${psiMarketingPlan.id}">${psiMarketingPlan.type eq '0'?'促销':'广告'}计划<shiro:hasPermission name="psi:psiMarketingPlan:edit">${not empty psiMarketingPlan.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="psiMarketingPlan" action="${ctx}/psi/psiMarketingPlan/reviewSave" method="post" class="form-horizontal">
		<form:input type="hidden" path="id" />
		
		<input type="hidden" name="createDate"  value="<fmt:formatDate  value='${psiMarketingPlan.createDate}' pattern='yyyy-MM-dd hh:mm:ss'/>"/>
		<input type="hidden" name="reviewDate"  value="<fmt:formatDate  value='${psiMarketingPlan.reviewDate}' pattern='yyyy-MM-dd hh:mm:ss'/>"/>
		<input type="hidden" name="updateDate"  value="<fmt:formatDate  value='${psiMarketingPlan.updateDate}' pattern='yyyy-MM-dd hh:mm:ss'/>"/>
		
	<div style="float:left;width:98%">
		<div class="control-group" style="float:left;width:35%;height:25px">
				<label class="control-label" style="width:100px">平台:</label>
				<div class="controls" style="margin-left:120px">
					${fns:getDictLabel(psiMarketingPlan.countryCode,'platform','')}
				</div>
			</div>
		<c:if test="${psiMarketingPlan.type eq '0'}">
			<div class="control-group" style="float:left;width:35%;height:25px">
				<label class="control-label" style="width:100px">促销周:</label>
				<div class="controls" style="margin-left:120px">
					${psiMarketingPlan.startWeek}(${fns:getWeekStartEnd(psiMarketingPlan.startWeek)})
				</div>
			</div>
		</c:if>
		<c:if test="${psiMarketingPlan.type eq '1'}">
			<div class="control-group" style="float:left;width:30%;height:25px">
				<label class="control-label" style="width:100px">开始周:</label>
				<div class="controls" style="margin-left:120px">
					${psiMarketingPlan.startWeek}(${fns:getWeekStartEnd(psiMarketingPlan.startWeek)})
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:30%;height:25px">
				<label class="control-label" style="width:100px">结束周:</label>
				<div class="controls" style="margin-left:120px">
					${psiMarketingPlan.endWeek}(${fns:getWeekStartEnd(psiMarketingPlan.endWeek)})
				</div>
			</div>
		</c:if>
		
	</div>
	<div style="float:left;width:98%">
		<div class="control-group">
			<label class="control-label" style="width:100px">备注:</label>
			<div class="controls" style="margin-left:120px">
				${psiMarketingPlan.remark}
			</div>
		</div>
	</div>
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">产品信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 30%">产品名</th>
				   <th style="width: 20%">颜色</th>
				   <th style="width: 20%">${psiMarketingPlan.type eq '0'?'促销数':'广告日均数'}</th>
			</tr>
		</thead>
		<tbody>
		<c:if test="${not empty psiMarketingPlan.id }">
		<c:forEach items="${psiMarketingPlan.items}"  var="item">
			<tr>
				<c:set var="proKey" value="${item.product.id},${item.colorCode}"/>
				<td>${item.productName}	</td>
				<td>${item.colorCode}	</td>
				<td>${item.promoQuantity}	</td>
			</tr>
		</c:forEach>
		</c:if>
		</tbody>
		</table>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="审核通过"/>&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
