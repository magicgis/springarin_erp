<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Custom Email manager</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<link href="${ctxStatic }/solrdefault.css" rel="stylesheet" type="text/css">
	<link href="${ctxStatic}/common/mailstate.css" type="text/css" rel="stylesheet" />
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").attr("action","${ctx}/solr/indexQuery/");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
<div style="width:90%;margin-left:80px">
	<form:form id="searchForm" modelAttribute="index" action="${ctx}/solr/indexQuery/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height:80px;line-height: 40px">
			<select style="width:100px" name="country">
				<option value="">全平台</option>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
						<option value="${dic.value }" ${dic.value eq index.country?'selected':'' }>${dic.label }</option>
					</c:if>
				</c:forEach>
			</select>
			&nbsp;&nbsp;
			<form:input path="subject" htmlEscape="false" maxlength="100" class="input-xxlarge"/>
			<input class="btn btn-primary" type="submit" value="搜 索"/>
			<br/>
			<%--<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/solr/indexQuery/');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="customSendDate" value="" class="input-small" id="customSendDate"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/solr/indexQuery/');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="" id="endDate" class="input-small"/>
			 --%>
			<select style="width:100px" name="dataDate">
				<option value="">不限时间</option>
				<option value="1" ${'1' eq index.dataDate?'selected':'' }>近三个月</option>
				<option value="2" ${'2' eq index.dataDate?'selected':'' }>近六个月</option>
				<option value="3" ${'3' eq index.dataDate?'selected':'' }>近一年</option>
			</select>
			&nbsp;&nbsp;
			<input type="radio" name="type" value="" ${empty index.type?'checked':'' }  />ALL
		  	<input type="radio" name="type" value="Product" ${'Product' eq index.type?'checked':'' } />Product
			<input type="radio" name="type" value="Customer" ${'Customer' eq index.type?'checked':'' } />Customer
			<input type="radio" name="type" value="Order" ${'Order' eq index.type?'checked':'' } />Order
			<input type="radio" name="type" value="Event" ${'Event' eq index.type?'checked':'' } />Event
			<input type="radio" name="type" value="CustomEmail" ${'CustomEmail' eq index.type?'checked':'' } />CustomEmail
			<input type="radio" name="type" value="ReviewerEmail" ${'ReviewerEmail' eq index.type?'checked':'' } />ReviewerEmail
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<div id="results" class="content-main" style="">
			<span class="support-text-top">为您找到相关结果${total }个,共耗时${times }</span>
			<c:forEach var="index" items="${page.list}">
				<div class="result f s0">
					<h3 class="c-title" style="line-height:20px">
						<a rpos="" cpos="title" href="${pageContext.request.contextPath}${index.link }"
							target="_blank">${index.subject }</a>
					</h3>
					<div>
						<div class="c-content">
							<div class="c-abstract">
								<c:if test="${'Product' ne index.type}">
									${index.dataDate }&nbsp;&nbsp;
									${fns:getDictLabel(index.country,'platform','')}&nbsp;&nbsp;</c:if>
								<c:if test="${not empty index.status}">${index.status}&nbsp;&nbsp;</c:if>
								<c:if test="${not empty index.userName}">${index.userName}&nbsp;&nbsp;</c:if>
								<c:if test="${'Customer' eq index.type }">购买次数：${index.buyTimes }&nbsp;&nbsp;</c:if>
								${index.describe }
							</div>
							<div>
								<span class="c-showurl">
									DataType:${empty index.typeDetail?index.type:index.typeDetail }
									<c:if test="${not empty index.productName && 'Product' ne index.type}">
										&nbsp;&nbsp;
										<a target="_blank" href="${ctx }/psi/psiInventory/productInfoDetail?productName=${index.productName }">产品</a>
									</c:if>
									<c:if test="${not empty index.orderNo && 'AmazonOrder' ne index.type}">
										&nbsp;&nbsp;
										<c:choose>
											<c:when test="${!fns:startsWith(index.orderNo,'Support-')&&!fns:startsWith(index.orderNo,'Review-')&&!fns:startsWith(index.orderNo,'MFN-')&&!fns:startsWith(index.orderNo,'Ebay-')&&!fns:startsWith(index.orderNo,'DZW-')}">
											    <c:if test="${fn:length(fn:split(index.orderNo, '-'))==3}"><a href="${ctx}/amazoninfo/order/form?amazonOrderId=${index.orderNo}" target="_blank">订单</a></c:if>
											    <c:if test="${fn:length(fn:split(index.orderNo, '-'))!=3}">${index.orderNo}</c:if>
												&nbsp;&nbsp;
											</c:when>
											<c:when test="${fns:startsWith(index.orderNo,'Support-')||fns:startsWith(index.orderNo,'Review-')||fns:startsWith(index.orderNo,'MFN-')||fns:startsWith(index.orderNo,'Ebay-')||fns:startsWith(index.orderNo,'DZW-')}">
												<a href="${ctx}/amazoninfo/amazonTestOrReplace/view?sellerOrderId=${index.orderNo}" target="_blank">订单</a>&nbsp;&nbsp;
											</c:when>
										</c:choose>
										<%--<a target="_blank" href="${ctx }/amazoninfo/order/form?amazonOrderId=${index.orderNo }">订单</a> --%>
									</c:if>
									<c:if test="${not empty index.customId && 'Customer' ne index.type}">
										&nbsp;&nbsp;
										<a target="_blank" href="${ctx }/amazoninfo/customers/view?customId=${index.customId }">客户</a>
									</c:if>
								</span>
							</div>
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	<div class="pagination">${page}</div>
	</div>
</body>
</html>
