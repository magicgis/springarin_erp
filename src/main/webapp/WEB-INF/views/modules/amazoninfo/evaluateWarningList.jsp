<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
		$(document).ready(function() {
			$("#country").on("click",function(){
				$("#searchForm").submit();
			});
			
			$(".state").on("change",function(){
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
		<li class="${empty evaluateWarning.country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${evaluateWarning.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		<li><a href="${ctx}/amazoninfo/evaluateWarning/form">新建折扣预警</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="evaluateWarning" action="${ctx}/amazoninfo/evaluateWarning/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input name="country" type="hidden" value="${evaluateWarning.country}"/>
		<label>Tracking Id：</label>	<input name="promotionId" type="text" maxlength="50" class="input-small" value="${evaluateWarning.promotionId}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${evaluateWarning.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${evaluateWarning.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<label>状态：</label>
			<select name="warningSta" class="state" style="width:150px" class="state">
				<option value="" >All</option>
				<option value="0" ${evaluateWarning.warningSta eq '0'?'selected':''} >正在监控</option>
				<option value="1" ${evaluateWarning.warningSta eq '1'?'selected':''}>已结束</option>
				<option value="2" ${evaluateWarning.warningSta eq '2'?'selected':''}>取消</option>
			</select> 
			&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No</th><th style="width:15%">Tracking Id</th><th style="width:10%">Country</th><th style="width:10%">promotionCode</th><th style="width:10%">remark</th><th style="width:10%">CreateUser</th><th style="width:10%">CreateDate</th><th style="width:15%">Status</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="evaluateWarning">
			<tr>
				<td>${evaluateWarning.id}</td>
				<td>${evaluateWarning.promotionId}</td>
				<td>${fns:getDictLabel(evaluateWarning.country, 'platform', defaultValue)}</td>
				<td>
					<c:if test="${fns:getUser().id eq evaluateWarning.createUser.id}">${evaluateWarning.promotionCode}</c:if>   
					<c:if test="${fns:getUser().id ne evaluateWarning.createUser.id}">***</c:if>
				</td>
				<td>${evaluateWarning.remark}</td>
				<td>${evaluateWarning.createUser.name}</td>
				<td><fmt:formatDate value="${evaluateWarning.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${evaluateWarning.warningSta eq '0'}"><span class="label label-success">正在监控</span></c:if>
					<c:if test="${evaluateWarning.warningSta eq '2'}"><span class="label label-warning">取消</span></c:if>
					<c:if test="${not empty evaluateWarning.result}">
					<span style="color:red;font-weight: bold;">(${evaluateWarning.result})</span>
					</c:if>
				</td>
				<td><input type="hidden" value="${evaluateWarning.id}"/><a class="btn btn-small btn-info open">概要</a>&nbsp;&nbsp;
					<c:if test="${evaluateWarning.createUser.id eq fns:getUser().id && evaluateWarning.warningSta eq '0'}">
						<a class="btn btn-small" href="${ctx}/amazoninfo/evaluateWarning/form?id=${evaluateWarning.id}">编辑</a>&nbsp;&nbsp;
					</c:if>
					<c:if test="${evaluateWarning.createUser.id eq fns:getUser().id && evaluateWarning.warningSta eq '0'}">
						<a class="btn btn-small" href="${ctx}/amazoninfo/evaluateWarning/cancel?id=${evaluateWarning.id}">取消</a>&nbsp;&nbsp;
					</c:if>
				</td>
			</tr>
			<c:if test="${fn:length(evaluateWarning.logs)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${evaluateWarning.id}"><td></td><td>promotionCode</td><td>createDate</td><td colspan="8">relative amazon order</td></tr>
				<c:forEach items="${evaluateWarning.logs}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${evaluateWarning.id}">
					<td></td><td>${item.promotionCode}</td>
					<td><fmt:formatDate pattern="yyyy-MM-dd hh:mm:ss" value="${item.createDate}"/></td>
					<td colspan="8">
						<c:forEach items="${fn:split(item.relativeOrderId,',')}" var="orderId">
							<a href="${ctx}/amazoninfo/order/form?amazonOrderId=${orderId}">${orderId}</a>&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>
					</td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
