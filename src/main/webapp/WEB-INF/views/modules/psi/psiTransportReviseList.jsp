<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单付款修正管理</title>
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
			$("#reviseSta,#supplier").change(function(){
				$("#searchForm").submit();
			});
			
			$(".open").click(function(e){
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/psiTransportOrder/">运单列表</a></li>
		<li class="active"><a href="${ctx}/psi/psiTransportRevise/">运单付款修正列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiTransportRevise" action="${ctx}/psi/psiTransportRevise/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			<label>申请日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="applyDate" value="<fmt:formatDate value="${psiTransportRevise.applyDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="sureDate" value="<fmt:formatDate value="${psiTransportRevise.sureDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				
		<label>修正付款单号 ：</label> <form:input path="paymentNo" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		</div>
			<div style="height: 40px;">
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<option value="" ${psiTransportRevise.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}'>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			<script type="text/javascript">
			$("option[value='${psiTransportRevise.supplier.id}']").attr("selected","selected");	
			</script>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>付款状态：</label>
			<form:select path="reviseSta" style="width: 200px" id="reviseSta">
				<option value="" >全部(非取消)</option>
				<option value="0" ${psiTransportRevise.reviseSta eq '0' ?'selected':''} >申请</option>
				<option value="3" ${psiTransportRevise.reviseSta eq '3' ?'selected':''} >已审核</option>
				<option value="5" ${psiTransportRevise.reviseSta eq '5' ?'selected':''} >完成</option>
				<option value="8" ${psiTransportRevise.reviseSta eq '8' ?'selected':''} >取消</option>
			</form:select>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">序号</th><th style="width:15%">运单付款单号</th><th style="width:15%">运单号</th><th style="width:10%">承运商</th><th style="width:5%">付款总额</th><th style="width:5%">货币类型</th><th style="width:10%">申请人</th><th style="width:10%">申请时间</th><th style="width:10%">状态</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiTransportRevise">
			<tr>
				<td>${psiTransportRevise.id}</td>
				<td><a href="${ctx}/psi/psiTransportRevise/view?id=${psiTransportRevise.id}">${psiTransportRevise.paymentNo}</a></td>
				<td>${psiTransportRevise.tranOrderNo}</td>
				<td>${psiTransportRevise.supplier.nikename}</td>
				<td>${psiTransportRevise.reviseAmount}</td>
				<td>${psiTransportRevise.currency}</td>
				<td>${psiTransportRevise.applyUser.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${psiTransportRevise.applyDate }"/></td>
				<td>
				<c:if test="${psiTransportRevise.reviseSta eq '0' }"><span class="label  label-warning">申请</span></c:if>
				<c:if test="${psiTransportRevise.reviseSta eq '3' }"><span class="label  label-info">已审核</span></c:if>
				<c:if test="${psiTransportRevise.reviseSta eq '5' }"><span class="label  label-success">完成</span></c:if>
				<c:if test="${psiTransportRevise.reviseSta eq '8' }"><span class="label  label-inverse">取消</span></c:if>
				</td>
				<td>
				<input type="hidden" value="${psiTransportRevise.id }"/>
				<a class="btn btn-small btn-info open">概要</a>
				<c:if test="${psiTransportRevise.reviseSta eq '0' }" >
					<shiro:hasPermission name="psi:tranRevise:review">
						<a  class="btn btn-small"  href="${ctx}/psi/psiTransportRevise/review?id=${psiTransportRevise.id}">审核修正付款</a>
					</shiro:hasPermission>
					<a  class="btn btn-small"  href="${ctx}/psi/psiTransportRevise/cancel?id=${psiTransportRevise.id}" onclick="return confirmx('确认要取消该修正付款项吗？', this.href)">取消</a>
				</c:if>
				
				<c:if test="${psiTransportRevise.reviseSta eq '3' }" >
					<shiro:hasPermission name="psi:tranRevise:sure">
						<a  class="btn btn-small"  href="${ctx}/psi/psiTransportRevise/sure?id=${psiTransportRevise.id}">确认修正付款</a>
					</shiro:hasPermission>
				</c:if>
				<c:if test="${psiTransportRevise.totalAmount>0}">
					<a  class="btn btn-small"  href="${ctx}/psi/psiTransportRevise/printPayment?id=${psiTransportRevise.id}">付款申请单</a>
				</c:if>
					
				</td>
			</tr>
			
			<c:if test="${fn:length(psiTransportRevise.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${psiTransportRevise.id}"><td></td><td>付款类型</td><td>付款金额</td><td>货币类型</td><td colspan="7"></td></tr>
				<c:forEach items="${psiTransportRevise.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${psiTransportRevise.id}">
					<td></td><td>${item.reviseType}</td><td>${item.reviseAmount}</td><td>${item.currency}</td><td colspan="7"></td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
