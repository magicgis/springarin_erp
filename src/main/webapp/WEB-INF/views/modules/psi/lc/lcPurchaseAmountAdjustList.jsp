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
		<li class="active"><a href="#">(理诚)采购金额调整列表</a></li>
		<shiro:hasPermission name="psi:purchaseAdjust:edit">
		<li><a href="${ctx}/psi/lcPurchaseAmountAdjust/form">(理诚)新建采购金额调整</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPurchaseAmountAdjust" action="${ctx}/psi/lcPurchaseAmountAdjust/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>供应商：</label>
		<select style="width:150px;" id="supplier" name="supplier.id">
			<option value="" ${lcPurchaseAmountAdjust.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
			<c:forEach items="${suppliers}" var="supplier" varStatus="i">
				 <option value='${supplier.id}' ${supplier.id eq  lcPurchaseAmountAdjust.supplier.id?'selected':''}>${supplier.nikename}</option>;
			</c:forEach>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>主题：</label>	<input name="subject" type="text" maxlength="50" class="input-small" value="${lcPurchaseAmountAdjust.subject}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${lcPurchaseAmountAdjust.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${lcPurchaseAmountAdjust.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No</th><th style="width:5%">供应商</th><th style="width:10%">订单号</th><th style="width:10%">产品</th>
		<th style="width:10%">主题</th><th style="width:5%">金额</th><th style="width:8%">CreateUser</th><th style="width:8%">CreateDate</th>
		<th style="width:5%">Status</th><th style="width:10%">remark</th><th style="width:10%">Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="lcPurchaseAmountAdjust">
			<tr>
				<td>${lcPurchaseAmountAdjust.id}</td>
				<td>${lcPurchaseAmountAdjust.supplier.nikename}</td>
				<td>${lcPurchaseAmountAdjust.orderNo}</td>
				<td>${lcPurchaseAmountAdjust.productNameColor}</td>
				<td>${lcPurchaseAmountAdjust.subject}</td>
				<td>${lcPurchaseAmountAdjust.adjustAmount}&nbsp;&nbsp;${lcPurchaseAmountAdjust.currency}</td>
				<td>${lcPurchaseAmountAdjust.createUser.name}</td>
				<td><fmt:formatDate value="${lcPurchaseAmountAdjust.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${lcPurchaseAmountAdjust.adjustSta eq '0'}"><span class="label label-important">草稿</span></c:if>
					<c:if test="${lcPurchaseAmountAdjust.adjustSta eq 'a'}"><span class="label label-warning">已申请审核</span></c:if>
					<c:if test="${lcPurchaseAmountAdjust.adjustSta eq 'r'}"><span class="label label-info">已审核通过</span></c:if>
					<c:if test="${lcPurchaseAmountAdjust.adjustSta eq '1'}"><span class="label label-warning">已申请付款</span></c:if>
					<c:if test="${lcPurchaseAmountAdjust.adjustSta eq '2'}"><span class="label label-success">已支付</span></c:if>
					<c:if test="${lcPurchaseAmountAdjust.adjustSta eq '8'}"><span class="label label-inverse">已取消</span></c:if>
				</td>
				<td>${lcPurchaseAmountAdjust.remark}</td>
				<td>
					<shiro:hasPermission name="psi:purchaseAdjust:review">
						<c:if test="${lcPurchaseAmountAdjust.adjustSta eq 'a'}">
							<a class="btn btn-small" href="${ctx}/psi/lcPurchaseAmountAdjust/review?id=${lcPurchaseAmountAdjust.id}">去审核</a>&nbsp;&nbsp;
						</c:if>
					</shiro:hasPermission>
					<shiro:hasPermission name="psi:purchaseAdjust:edit">
					<c:if test="${lcPurchaseAmountAdjust.createUser.id eq fns:getUser().id && lcPurchaseAmountAdjust.adjustSta eq '0'}">
						<a class="btn btn-small" href="${ctx}/psi/lcPurchaseAmountAdjust/form?id=${lcPurchaseAmountAdjust.id}">编辑</a>&nbsp;&nbsp;
					</c:if>
					<c:if test="${lcPurchaseAmountAdjust.createUser.id eq fns:getUser().id && lcPurchaseAmountAdjust.adjustSta eq '0'}">
						<a class="btn btn-small" href="${ctx}/psi/lcPurchaseAmountAdjust/apply?id=${lcPurchaseAmountAdjust.id}">申请</a>&nbsp;&nbsp;
					</c:if>
					<c:if test="${lcPurchaseAmountAdjust.createUser.id eq fns:getUser().id && lcPurchaseAmountAdjust.adjustSta eq '0'}">
						<a class="btn btn-small" href="${ctx}/psi/lcPurchaseAmountAdjust/cancel?id=${lcPurchaseAmountAdjust.id}">取消</a>&nbsp;&nbsp;
					</c:if>
					</shiro:hasPermission>
					<c:if test="${ lcPurchaseAmountAdjust.adjustSta eq '1' || lcPurchaseAmountAdjust.adjustSta eq '2'}">
						<a class="btn btn-small" href="${ctx}/psi/lcPurchasePayment/view?id=${lcPurchaseAmountAdjust.paymentId}">查看付款单</a>&nbsp;&nbsp;
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
