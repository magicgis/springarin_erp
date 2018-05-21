<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单付款管理</title>
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
			$("#paymentSta,#supplier").change(function(){
				$("#searchForm").submit();
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.createDate=$("input[name='createDate']").val();
				params.updateDate=$("input[name='updateDate']").val();
				window.location.href = "${ctx}/psi/lcPsiTransportPayment/expPaymentTotal?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
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
			
		});f
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
		<li ><a href="${ctx}/psi/psiTransportPayment/">运单付款列表</a></li>
		<shiro:hasPermission name="psi:tranPayment:edit">
		<li><a href="${ctx}/psi/psiTransportPayment/add">新建运单付款</a></li>
		</shiro:hasPermission>
		
		<li class="active"><a href="${ctx}/psi/lcPsiTransportPayment/">(理诚)运单付款列表</a></li>
		<shiro:hasPermission name="psi:tranPayment:edit">
		<li><a href="${ctx}/psi/lcPsiTransportPayment/add">(理诚)新建运单付款</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPsiTransportPayment" action="${ctx}/psi/lcPsiTransportPayment/" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>     
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${lcPsiTransportPayment.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${lcPsiTransportPayment.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				
		<label>付款单号 ：</label> <form:input path="paymentNo" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;&nbsp;&nbsp;<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
		</div>
			<div style="height: 40px;">
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<option value="" ${lcPsiTransportPayment.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}'>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			<script type="text/javascript">
			$("option[value='${psiTransportPayment.supplier.id}']").attr("selected","selected");	
			</script>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>付款状态：</label>
			<form:select path="paymentSta" style="width: 200px" id="paymentSta">
				<option value="" >全部(非取消)</option>
				<option value="0" ${lcPsiTransportPayment.paymentSta eq '0' ?'selected':''} >草稿</option>
				<option value="1" ${lcPsiTransportPayment.paymentSta eq '1' ?'selected':''} >已申请</option>
				<option value="3" ${lcPsiTransportPayment.paymentSta eq '3' ?'selected':''} >主管已审核</option>
				<option value="4" ${lcPsiTransportPayment.paymentSta eq '4' ?'selected':''} >财务已审核</option>
				<option value="5" ${lcPsiTransportPayment.paymentSta eq '5' ?'selected':''} >已付款</option>
				<option value="8" ${lcPsiTransportPayment.paymentSta eq '8' ?'selected':''} >已取消</option>
			</form:select>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">序号</th><th style="width:10%">运单付款单号</th><th style="width:8%">付款总额</th><th style="width:8%">货币类型</th><th style="width:8%">创建人</th><th style="width:8%">创建时间</th><th style="width:15%">备注</th><th style="width:8%">状态</th><th >操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiTransportPayment">
			<tr>
				<td>${psiTransportPayment.id}</td>
				<td><a href="${ctx}/psi/lcPsiTransportPayment/view?id=${psiTransportPayment.id}">${psiTransportPayment.paymentNo}</a></td>
				<td><fmt:formatNumber value="${psiTransportPayment.paymentAmount}" pattern=",###.00"/> </td>
				<td>${psiTransportPayment.currency}</td>
				<td>${psiTransportPayment.createUser.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${psiTransportPayment.createDate }"/></td>
				<td>${psiTransportPayment.remark}</td>
				<td>
				<c:if test="${psiTransportPayment.paymentSta eq '0' }"><span class="label  label-important">草稿</span></c:if>
				<c:if test="${psiTransportPayment.paymentSta eq '1' }"><span class="label  label-warning">申请</span></c:if>
				<c:if test="${psiTransportPayment.paymentSta eq '3' }"><span class="label  label-info">主管已审核</span></c:if>
				<c:if test="${psiTransportPayment.paymentSta eq '4' }"><span class="label  label-info">财务已审核</span></c:if>
				<c:if test="${psiTransportPayment.paymentSta eq '5' }"><span class="label  label-success">已付款</span></c:if>
				<c:if test="${psiTransportPayment.paymentSta eq '8' }"><span class="label  label-inverse">取消</span></c:if>
				</td>
				<td>
				<input type="hidden" value="${psiTransportPayment.id }"/>
				<a class="btn btn-small btn-info open">概要</a>
				<c:if test="${psiTransportPayment.paymentSta eq '0' }" >
					<shiro:hasPermission name="psi:tranPayment:edit">
						<a  class="btn btn-small"  href="${ctx}/psi/lcPsiTransportPayment/edit?id=${psiTransportPayment.id}">编辑</a>
						<a  class="btn btn-small"  href="${ctx}/psi/lcPsiTransportPayment/cancel?id=${psiTransportPayment.id}" onclick="return confirmx('确认要取消该付款吗？', this.href)">取消</a>
					</shiro:hasPermission>
				</c:if>
				
				<c:if test="${psiTransportPayment.paymentSta eq '1' }" >
					<shiro:hasPermission name="psi:tranPayment:review">
						<a  class="btn btn-small"  href="${ctx}/psi/lcPsiTransportPayment/review?id=${psiTransportPayment.id}">付款审核</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="psi:tranPayment:edit">
						<a  class="btn btn-small"  href="${ctx}/psi/lcPsiTransportPayment/toDraft?id=${psiTransportPayment.id}" onclick="return confirmx('确认要取消成草稿吗？', this.href)">取消成草稿</a>
					</shiro:hasPermission>
				</c:if>
				
				<c:if test="${psiTransportPayment.paymentSta eq '3' }" >
					<shiro:hasPermission name="psi:tranPayment:financeReview">
						<a  class="btn btn-small"  href="${ctx}/psi/lcPsiTransportPayment/financeReview?id=${psiTransportPayment.id}">财务审核</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="psi:tranPayment:financeReview">
						<a  class="btn btn-small"  href="${ctx}/psi/lcPsiTransportPayment/toDraft?id=${psiTransportPayment.id}" onclick="return confirmx('确认要取消成草稿吗？', this.href)">取消成草稿</a>
					</shiro:hasPermission>
				</c:if>
				
				<c:if test="${psiTransportPayment.paymentSta eq '4' }" >
					<shiro:hasPermission name="psi:tranPayment:sure">
						<a  class="btn btn-small"  href="${ctx}/psi/lcPsiTransportPayment/sure?id=${psiTransportPayment.id}">付款确认</a>
					</shiro:hasPermission>
				</c:if>
				
				<c:if test="${psiTransportPayment.paymentSta eq '5' && not empty  psiTransportPayment.supplierAttchmentPath   }" >
					<shiro:hasPermission name="psi:tranPayment:edit">
						<a  class="btn btn-small"  href="${ctx}/psi/lcPsiTransportPayment/uploadBill?id=${psiTransportPayment.id}">上传供应商税务发票</a>
					</shiro:hasPermission>
				</c:if>
				<%--<shiro:hasPermission name="psi:tranPayment:edit"> --%>
						<a  class="btn btn-small"  href="${ctx}/psi/lcPsiTransportPayment/printPayment?id=${psiTransportPayment.id}">付款申请单</a>
					<%-- </shiro:hasPermission>  --%>
				</td>
			</tr>
			
			<c:if test="${fn:length(psiTransportPayment.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${psiTransportPayment.id}"><td></td><td>运单号</td><td>付款类型</td><td>付款金额</td><td>货币类型</td><td colspan="7"></td></tr>
				<c:forEach items="${psiTransportPayment.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${psiTransportPayment.id}">
					<td></td><td>${item.transportNo}</td><td>${item.paymentType}</td>
					<td><fmt:formatNumber value="${item.paymentAmount}" pattern=",###.##"/></td><td>${item.currency}</td><td colspan="7"></td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
