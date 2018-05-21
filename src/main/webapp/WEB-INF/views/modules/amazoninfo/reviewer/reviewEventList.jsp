<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Review Event List</title>
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
				"aaSorting": [[ 6, "asc" ]]
			});
			
			<%-- 站内站外选择 --%>
			var html = " 评测进度:<select name=\"selectType\" id=\"selectType\" style=\"width: 100px\" onchange=\"changeSelectType()\">"+
				"<option value=\"0\" ${'0' eq type?'selected':''}>正在评测</option>"+
				"<option value=\"1\" ${'1' eq type?'selected':''}>评测完毕</option></select> &nbsp;&nbsp;&nbsp;";
			
			
			var html1 = " 创建时间: <input style=\"width: 100px\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:doSubmit});\" "+
				"readonly=\"readonly\"  class=\"Wdate\" type=\"text\" name=\"start\"  "+
				"value=\"<fmt:formatDate value='${event.createDate}' pattern='yyyy-MM-dd'/>\" class=\"input-small\" id=\"start\"/>"+
				"&nbsp; -&nbsp; <input style=\"width: 100px\" onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:doSubmit});\" "+
				"readonly=\"readonly\"  class=\"Wdate\" type=\"text\" name=\"end\"  "+
				"value=\"<fmt:formatDate value='${event.endDate}' pattern='yyyy-MM-dd'/>\" class=\"input-small\" id=\"end\"/>"+
				"&nbsp;&nbsp;&nbsp;<input id='export' class='btn btn-primary' type='button' value='导出'/>&nbsp;&nbsp;&nbsp;&nbsp;";
			$("#contentTbDiv .spanexr div:first").append(html + html1); 
			 
			$("#export").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/reviewer/exportReviewEvent");
				$("#createDate").val($("#start").val());
				$("#endDate").val($("#end").val());
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/reviewer/reviewEventList");
			});
		});
		
		function productDetail(productName){
			var url = "${ctx}/psi/psiInventory/productInfoDetail?productName=" + encodeURIComponent(productName);
			window.location.href = url;
		}
		
		function changeSelectType(){
			var reviewType = $("#selectType").val();
			$("#reviewType").val(reviewType);
			$("#searchForm").submit();
		}
		
		function doSubmit(){
			$("#createDate").val($("#start").val());
			$("#endDate").val($("#end").val());
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<%-- 
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/amazoninfo/reviewer/reviewEventList">评测进度</a></li>
	</ul> --%>
	<form:form id="searchForm" modelAttribute="event" action="${ctx}/amazoninfo/reviewer/reviewEventList" method="post" class="breadcrumb form-search">
		<input id="createDate" name="createDate" type="hidden" value=""/>
		<input id="endDate" name="endDate" type="hidden" value=""/>
		<input id="reviewType" name="reviewType" type="hidden" value="${type }"/>
	</form:form>
	<div id="contentTbDiv" style="width:100%;margin: auto">
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<!-- 国家 -->
		<th><spring:message code='sys_label_country'/></th>
		<!-- 产品名称 -->
		<th><spring:message code='amaInfo_businessReport_productName'/></th>
		<!-- 主题 -->
		<th><spring:message code='custom_event_form'/></th>
		<!-- 订单号 -->
		<th><spring:message code='custom_event_form6'/></th>
		<!-- 客户邮箱
		<th><spring:message code='custom_event_form8'/></th>
		 -->
		<!-- 客户名 -->
		<th><spring:message code='custom_event_form7'/></th>
		<!-- 负责人 -->
		<th><spring:message code='custom_event_colName'/></th>
		<!-- 创建时间 -->
		<th>&nbsp;<spring:message code='custom_event_form24'/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
		<!-- 更新时间 -->
		<th><spring:message code='custom_event_form25'/></th>
		<th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${eventList}" var="event" varStatus="i">
			<tr>
				<td>${fns:getDictLabel(event.country,'platform','Others')}</td>
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
				<td><a href="${ctx}/custom/event/form?id=${event.id}" rel="popover" data-content="${event.subjectStr2}">${event.subjectStr}<span style='color: orange;'>${countComment[i.index]}</span></a></td>
				<td>
					<c:forEach items="${fn:split(event.invoiceNumber, ',')}" var="link">
						<c:if test="${not empty link }">
							<c:choose>
								<c:when test="${!fns:startsWith(link,'Support-')&&!fns:startsWith(link,'Review-')&&!fns:startsWith(link,'MFN-')&&!fns:startsWith(link,'Ebay-')&&!fns:startsWith(link,'DZW-')}">
								    <c:if test="${fn:length(fn:split(link, '-'))==3}"><a href="${ctx}/amazoninfo/order/form?amazonOrderId=${link}" target="_blank">${link}</a></c:if>
								    <c:if test="${fn:length(fn:split(link, '-'))!=3}">${link}</c:if>
									&nbsp;&nbsp;
								</c:when>
								<c:when test="${fns:startsWith(link,'Support-')||fns:startsWith(link,'Review-')||fns:startsWith(link,'MFN-')||fns:startsWith(link,'Ebay-')||fns:startsWith(link,'DZW-')}">
									<a href="${ctx}/amazoninfo/amazonTestOrReplace/view?sellerOrderId=${link}" target="_blank">${link}</a>&nbsp;&nbsp;
								</c:when>
							</c:choose>
						</c:if>
					</c:forEach>
					<%--<a target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${fn:substring(event.invoiceNumber,0,19)}" rel="popover" data-content="${event.invoiceNumber}">${not empty event.invoiceNumber?fns:abbr(event.invoiceNumber,22):''}</a> --%>
				</td>
				<td>${event.customName}</td>
				<%--<td>${event.customEmail}</td>--%>
				<td>${event.masterBy.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${event.createDate}"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${event.updateDate}"/></td>
				<td>
					<a href="${ctx}/custom/event/form?id=${event.id}">view</a>&nbsp;&nbsp;
					<c:if test="${not empty event.customEmail }">
						<a href="${ctx}/amazoninfo/reviewer/reviewEventHis?email=${event.customEmail}">评测记录</a>&nbsp;&nbsp;
					</c:if>
					<c:if test="${reviewerEmails[i.index] > 0 }">
						<a href="${ctx}/amazoninfo/reviewer/viewEmail?email=${event.customEmail}">
							待处理邮件(<span style="color:red">${reviewerEmails[i.index] }</span>)
						</a>&nbsp;&nbsp;
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table></div>
</body>
</html>
