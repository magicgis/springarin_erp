<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>跟卖信息</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
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
		$(document).ready(function() {
			
			$("#supplier").change(function(){
				$("#searchForm").submit();
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.event=$("input[name='event']").val();
				window.location.href = "${ctx}/psi/followSeller/exp?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			
			$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 15,
				"aLengthMenu":[[15, 30, 60,100,-1], [15, 30, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},
				"aoColumns": [
						         null,
							     null,
							     null,
							     null,
							     null
						     ],
			 	"ordering":true,
			     "aaSorting": [[4, "desc" ]]
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
		<li class="active"><a href="">跟卖信息列表</a></li>
		<li ><a href="${ctx}/amazoninfo/followAsin">监控跟卖产品列表</a></li>
		<li ><a href="${ctx}/amazoninfo/followAsin/form">新增监控跟卖产品</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="followSeller" action="${ctx}/amazoninfo/followSeller/" method="post" class="breadcrumb form-search" >
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>  
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>国家：</label>
		<form:select path="country" style="width: 100px" id="country">
			<option value="" >全部</option>
			<c:forEach items="${fns:getDictList('platform')}" var="dic">
				<c:if test="${dic.value ne 'com.unitek'}">
					 <option value="${dic.value}" ${followSeller.country eq dic.value ?'selected':''}  >${dic.label}</option>
				</c:if>      
			</c:forEach>	
		</form:select>&nbsp;&nbsp;
		<label>跟卖卖家：</label><input type="text" name="sellerName" value="${followSeller.sellerName}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>产品名/asin：</label><input type="text" name="productName" value="${followSeller.productName}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
		<label>跟卖时间：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${followSeller.dataDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${followSeller.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:5%">跟卖商家</th><th style="width:5%">产品</th><th style="width:5%">asin</th><th style="width:5%">平台</th><th style="width:5%">抓到次数</th></tr></thead>   
		<tbody>
		<c:forEach items="${follows}" var="followSeller" varStatus="i">
			<tr>
				<td>${fn:substring(followSeller.a,0,2)} target='_blank' ${fn:substring(followSeller.a,2,fn:length(followSeller.a))}</td>
				<c:set var="subffix" value="${followSeller.country }"/>
				<c:choose>
					<c:when test="${fn:contains('uk,jp',followSeller.country)}"><c:set var="subffix" value="co.${followSeller.country}"/></c:when>
					<c:when test="${fn:contains('mx',followSeller.country)}"><c:set var="subffix" value="com.${followSeller.country}"/></c:when>
				</c:choose>
				<td><a target="_blank" href="https://www.amazon.${subffix}/gp/offer-listing/${followSeller.asin}?condition=new">${followSeller.productName}</a></td>
				<td>${followSeller.asin}</td>
				<td>
				<c:choose>
				<c:when test="${fn:contains('de,com,uk,jp',followSeller.country)}"><a target="_blank" href="https://www.amazon.${subffix}/gp/help/reports/infringement">${followSeller.country eq "com"?"us":followSeller.country}</a></c:when>
				<c:otherwise>${followSeller.country eq "com"?"us":followSeller.country}</c:otherwise>
				</c:choose>
				</td>
				<td>${followSeller.quantity}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
