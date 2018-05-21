<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Review Event History</title>
	<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	.spanexr {
		float: right;
		min-height: 40px
	}
	
	.spanexl {
		float: left;
	}
	
	.footer {
		padding: 20px 0;
		margin-top: 20px;
		border-top: 1px solid #e5e5e5;
		background-color: #f5f5f5;
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
		
		if(!(top)){
			top = self; 
		}
	
		$(document).ready(function() {
			$(".back").click(function(){
				history.go(-1);	
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 20,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"ordering" : true,
				"aaSorting": [[ 8, "asc" ]]
			});
			
		});
		
		function productDetail(productName){
			var url = "${ctx}/psi/psiInventory/productInfoDetail?productName=" + encodeURIComponent(productName);
			window.location.href = url;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/reviewer/reviewEventList">评测进度</a></li>
		<li class="active"><a href="#">评测记录</a></li>
	</ul>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<!-- 评测人姓名 -->
		<th style="width:100px">评测人</th>
		<!-- 产品型号 -->
		<th><spring:message code='amaInfo_businessReport_productName'/></th>
		<!-- 国家 -->
		<th><spring:message code='sys_label_country'/></th>
		<!-- 主题 -->
		<th><spring:message code='custom_event_form'/></th>
		<!-- 订单号 -->
		<th><spring:message code='custom_event_form6'/></th>
		<!-- 客户邮箱
		<th><spring:message code='custom_event_form8'/></th> -->
		<!-- 负责人 -->
		<th><spring:message code='custom_event_colName'/></th>
		<!-- 状态 -->
		<th><spring:message code='custom_event_form21'/></th>
		<!-- 创建时间 -->
		<th>&nbsp;<spring:message code='custom_event_form24'/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
		<!-- 更新时间 -->
		<th><spring:message code='custom_event_form25'/></th>
		<th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${eventList}" var="event" varStatus="i">
			<tr>
				<td>
				<c:if test="${not empty reviewer }">
					<c:if test="${'0' eq  reviewer.reviewerType}">
						<a target="_blank" href="http://www.amazon.${key}/gp/pdp/profile/${reviewer.reviewerId}">${reviewer.name}</a>
					</c:if>
					<c:if test="${'0' ne  reviewer.reviewerType}">
						<a href="${ctx}/amazoninfo/reviewer/form?id=${reviewer.id}">${reviewer.name}</a>
					</c:if>
				</c:if>
				<c:if test="${empty reviewer }">${event.customName}</c:if>
				</td>
				<td><c:choose>
				<c:when test="${empty event.remarks}">
					<spring:message code='custom_event_note' />
				</c:when>
				<c:otherwise>
					<c:if test="${'other' eq products[i.index]}">${products[i.index]}</c:if>
					<c:if test="${'other' ne products[i.index]}">
						<a onclick="productDetail('${products[i.index]}')" href="#">${products[i.index]}</a>
					</c:if>
				</c:otherwise></c:choose></td>
				<td>${fns:getDictLabel(event.country,'platform','Others')}</td>
				<td><a href="${ctx}/custom/event/form?id=${event.id}" rel="popover" data-content="${event.subjectStr2}">${event.subjectStr}<span style='color: orange;'>${countComment[i.index]}</span></a></td>
				<td>
					<c:forEach items="${fn:split(event.invoiceNumber, ',')}" var="link">
						<c:if test="${not empty link }">
							<a href="${ctx}/amazoninfo/order/form?amazonOrderId=${link}" target="_blank">${link}</a><br/>
						</c:if>
					</c:forEach>
					<%--<a target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${fn:substring(event.invoiceNumber,0,19)}" rel="popover" data-content="${event.invoiceNumber}">${not empty event.invoiceNumber?fns:abbr(event.invoiceNumber,22):''}</a> --%>
				</td>
				<%--<td>${event.customEmail}</td>--%>
				<td>${event.masterBy.name}</td>
				<td>${event.stateStr}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${event.createDate}"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${event.updateDate}"/></td>
				<td>
					<a href="${ctx}/custom/event/form?id=${event.id}">view</a>&nbsp;&nbsp;
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
