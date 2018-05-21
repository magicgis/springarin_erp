<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单导出</title>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
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
			$(".countryHref").click(function(){
				$("input[name='platform']").val($(this).attr("key"));
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
		<li class="${'eu' eq amazonSalesSummaryFile.platform ?'active':''}"><a class="countryHref"  key="eu" href="#">欧洲|EU</a></li>
		<shiro:hasPermission name="amazon:order:expall">
			<li class="${'us' eq amazonSalesSummaryFile.platform ?'active':''}"><a class="countryHref"  key="us" href="#">美国|US</a></li>
			<li class="${'ca' eq amazonSalesSummaryFile.platform ?'active':''}"><a class="countryHref"  key="ca" href="#">加拿大|CA</a></li>
			<li class="${'jp' eq amazonSalesSummaryFile.platform ?'active':''}"><a class="countryHref"  key="jp" href="#">日本|JP</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonSalesSummaryFile" action="${ctx}/amazoninfo/salesSummary" method="post" class="breadcrumb form-search" cssStyle="height: 60px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="platform" name="platform" type="hidden" value="${amazonSalesSummaryFile.platform}"/>
		<div style="height: 50px;line-height: 40px">
			<label><spring:message code="amazon_order_tips3"/>：</label>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="month" value="<fmt:formatDate value="${date}" pattern="yyyy-MM"/>" class="input-small" id="month"/>
			&nbsp;&nbsp;-
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM"/>" class="input-small" id="end"/>
			&nbsp;&nbsp;		
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			   <th><spring:message code="amazon_order_tips3"/></th>	
			   <th><spring:message code="amazon_order_form4"/></th>	
			   <th><spring:message code="amazon_product_type"/></th>	
			   <th><spring:message code="sys_label_tips_operate"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="orderFile">
			<tr>
				<td>${orderFile.month}</td>
				<td>${fn:toUpperCase(orderFile.platform)}</td>
				<td>
					<c:if test="${'1' eq orderFile.type }">
						XLS
					</c:if>
					<c:if test="${'2' eq orderFile.type }">
						CSV
					</c:if>
					
				</td>
				<td>
					<c:if test="${'1' eq orderFile.type }">
						&nbsp;&nbsp;<a class="btn" href="${ctx}/amazoninfo/salesSummary/download?id=${orderFile.id}">download</a>
					</c:if>
					<c:if test="${'2' eq orderFile.type }">
						&nbsp;&nbsp;<a class="btn" href="${ctx}/amazoninfo/salesSummary/downloadCsv?id=${orderFile.id}">download</a>
					</c:if>
					
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
