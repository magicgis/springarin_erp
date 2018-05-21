<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
		$(document).ready(function() {
			$("#supplier").on("click",function(){
				$("#searchForm").submit();
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
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
		<li class="active"><a href="#">采购金额调整列表</a></li>
		<shiro:hasPermission name="psi:purchaseAdjust:edit">
		<li><a href="${ctx}/psi/purchaseAmountAdjust/form">新建采购金额调整</a></li>
		</shiro:hasPermission>
		<li><a href="${ctx}/psi/lcPurchaseAmountAdjust">(理诚)采购金额调整列表</a></li>
		<shiro:hasPermission name="psi:purchaseAdjust:edit">
		<li><a href="${ctx}/psi/lcPurchaseAmountAdjust/form">(理诚)新建采购金额调整</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="purchaseAmountAdjust" action="${ctx}/psi/purchaseAmountAdjust/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>供应商：</label>
		<select style="width:150px;" id="supplier" name="supplier.id">
			<option value="" ${purchaseAmountAdjust.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
			<c:forEach items="${suppliers}" var="supplier" varStatus="i">
				 <option value='${supplier.id}' ${supplier.id eq  purchaseAmountAdjust.supplier.id?'selected':''}>${supplier.nikename}</option>;
			</c:forEach>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>主题：</label>	<input name="subject" type="text" maxlength="50" class="input-small" value="${purchaseAmountAdjust.subject}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${purchaseAmountAdjust.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${purchaseAmountAdjust.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No</th><th style="width:8%">供应商</th><th style="width:20%">主题</th><th style="width:8%">金额</th><th style="width:8%">CreateUser</th><th style="width:8%">CreateDate</th><th style="width:5%">Status</th><th style="width:20%">remark</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="purchaseAmountAdjust">
			<tr>
				<td>${purchaseAmountAdjust.id}</td>
				<td>${purchaseAmountAdjust.supplier.nikename}</td>
				<td>${purchaseAmountAdjust.subject}</td>
				<td>${purchaseAmountAdjust.adjustAmount}&nbsp;&nbsp;${purchaseAmountAdjust.currency}</td>
				<td>${purchaseAmountAdjust.createUser.name}</td>
				<td><fmt:formatDate value="${purchaseAmountAdjust.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${purchaseAmountAdjust.adjustSta eq '0'}"><span class="label label-important">未支付</span></c:if>
					<c:if test="${purchaseAmountAdjust.adjustSta eq '1'}"><span class="label label-warning">已申请</span></c:if>
					<c:if test="${purchaseAmountAdjust.adjustSta eq '2'}"><span class="label label-success">已支付</span></c:if>
					<c:if test="${purchaseAmountAdjust.adjustSta eq '8'}"><span class="label label-inverse">已取消</span></c:if>
				</td>
				<td>${purchaseAmountAdjust.remark}</td>
				<td>
					<shiro:hasPermission name="psi:purchaseAdjust:edit">
					<c:if test="${purchaseAmountAdjust.createUser.id eq fns:getUser().id && purchaseAmountAdjust.adjustSta eq '0'}">
						<a class="btn btn-small" href="${ctx}/psi/purchaseAmountAdjust/form?id=${purchaseAmountAdjust.id}">编辑</a>&nbsp;&nbsp;
					</c:if>
					<c:if test="${purchaseAmountAdjust.createUser.id eq fns:getUser().id && purchaseAmountAdjust.adjustSta eq '0'}">
						<a class="btn btn-small" href="${ctx}/psi/purchaseAmountAdjust/cancel?id=${purchaseAmountAdjust.id}">取消</a>&nbsp;&nbsp;
					</c:if>
					</shiro:hasPermission>
					<c:if test="${ purchaseAmountAdjust.adjustSta eq '1' || purchaseAmountAdjust.adjustSta eq '2'}">
						<a class="btn btn-small" href="${ctx}/psi/purchasePayment/view?id=${purchaseAmountAdjust.paymentId}">查看付款单</a>&nbsp;&nbsp;
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
