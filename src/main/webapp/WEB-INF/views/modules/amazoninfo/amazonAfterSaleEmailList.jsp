<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>售后邮件发送记录</title>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
	<meta name="decorator" content="default"/>
	<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
			
			$("#country").change(function(){
				$("#searchForm").submit();
			});
			$("#sendFlag").change(function(){
				$("#searchForm").submit();
			});
			
			$(".aboutMe").change(function(){
				$("#searchForm").submit();
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
		<li><a href="${ctx}/amazoninfo/afterSale/list">售后邮件任务列表</a></li>
		<li><a href="${ctx}/amazoninfo/afterSale/form">添加任务</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/afterSale/sendList">邮件发送记录</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonComment" action="${ctx}/amazoninfo/afterSale/sendList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 115px;line-height: 40px">
			<div style="height: 40px;">
				<label>${'1' eq amazonComment.sendFlag?'发送':'创建' }时间：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true;}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${amazonComment.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="createDate"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true;}});" readonly="readonly"  class="Wdate" type="text" name="sentDate" value="<fmt:formatDate value="${amazonComment.sentDate}" pattern="yyyy-MM-dd" />" id="sentDate" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				国家：<select name="task.country" id="country" style="width: 120px">
						<option value="" ${amazonComment.task.country eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek'}">
								<option value="${dic.value}" ${amazonComment.task.country eq dic.value?'selected':''}  >${dic.label}</option>
							</c:if>
						</c:forEach>
				</select>&nbsp;&nbsp;
				发送状态：<select name="sendFlag" id="sendFlag" style="width: 120px">
						<option value="" ${amazonComment.sendFlag eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="0" ${amazonComment.sendFlag eq '0'?'selected':''}  >未发送</option>
						<option value="1" ${amazonComment.sendFlag eq '1'?'selected':''}  >已发送</option>
						<option value="2" ${amazonComment.sendFlag eq '2'?'selected':''}  >已取消</option>
				</select>&nbsp;&nbsp;
			</div>
			<div style="height: 40px;">
				&nbsp;&nbsp;review产品：<input name="task.pn2" type="text" class="input-medium" value="${amazonComment.task.pn2}"/>
				&nbsp;&nbsp;产品名称：<input name="task.pn1" type="text" class="input-medium" value="${amazonComment.task.pn1}"/><span style="color:red">发送售后邮件后客户购买过的产品</span>
			</div>
				&nbsp;&nbsp;任务编号：<input name="task.id" type="text" class="input-small number" value="${amazonComment.task.id}"/>
				&nbsp;&nbsp;客户ID：<input name="customer.customerId" type="text" class="input-small" value="${amazonComment.customer.customerId}"/>
				&nbsp;&nbsp;
				评论状态：<select name="isReview" id="isReview" style="width: 120px" class="aboutMe">
						<option value="" ${empty isReview?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="1" ${isReview eq '1'?'selected':''}  >已评论</option>
						<option value="2" ${isReview eq '2'?'selected':''}  >未评论</option>
				</select>
				&nbsp;&nbsp;
				评论等级：<select name="star" id="star" style="width: 120px" class="aboutMe">
						<option value="" ${empty amazonComment.star?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="0" ${amazonComment.star eq '0'?'selected':''}  >差评</option>
						<option value="1" ${amazonComment.star eq '1'?'selected':''}  >好评</option>
				</select>&nbsp;&nbsp;
				<input type="checkbox" class="aboutMe" name="isReply" id="isReply" value="1" ${isReply eq '1'?'checked':''}/>已回复
				&nbsp;
				<%--<input type="checkbox" class="aboutMe" name="isReview" id="isReview" value="1" ${isReview eq '1'?'checked':''}/>已追加评论 --%>
				 &nbsp;&nbsp;&nbsp;&nbsp;
			     <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>		
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			   <th style="width: 20px" class="sort task">任务编号</th>
			   <th style="width: 80px" class="sort createDate">创建时间</th>
			   <th style="width: 70px">平台</th>
			   <th style="width: 150px">客户名称</th>
			   <c:if test="${'1' eq isReview }">
			   	<th style="width: 60px">评分</th>
			   	<th style="width: 60px">产品名称</th>
			   </c:if>
			   <th style="width: 70px" class="sort sendFlag">发送状态</th>
			   <th style="width: 80px" class="sort sentDate">发送时间</th>
			   <th style="width: 80px">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="amazonComment">
			<tr>
				<td>${amazonComment.task.id}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${amazonComment.createDate}"/></td>
				<td>${fns:getDictLabel(amazonComment.task.country,'platform','')}</td>
				<td>
					<a target="_blank" href="${ctx}/amazoninfo/customers/view?customId=${amazonComment.customer.customerId}">${amazonComment.customer.name}</a>
				</td>
			   <c:if test="${'1' eq isReview }">
			   		<td><a target="_blank" href="${amazonComment.reviewComment.link}">${amazonComment.reviewComment.star}</a></td>
			   		<td><a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${asinProduct[amazonComment.reviewComment.asin]}">${asinProduct[amazonComment.reviewComment.asin]}</a></td>
			   </c:if>
				<td>
					<c:if test="${'0' eq amazonComment.sendFlag }">未发送</c:if>
					<c:if test="${'1' eq amazonComment.sendFlag }">已发送</c:if>
					<c:if test="${'2' eq amazonComment.sendFlag }">已取消</c:if>
				</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${amazonComment.sentDate}"/></td>
				<td>
					<a class="btn btn-info btn-small" href="${ctx}/amazoninfo/afterSale/viewSendInfo?id=${amazonComment.id}">查看</a>
					<c:if test="${not empty amazonComment.customEmail }">
						<a class="btn btn-success btn-small" href="${ctx}/custom/emailManager/view?id=${amazonComment.customEmail.id}">查看回复邮件</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
