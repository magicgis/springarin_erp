<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
<title>亚马逊产品线运营分析报告</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
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
				$("#country").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			 $("#totalTb").dataTable({
			    	"searching":false,
					"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 10,
					"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
							[ 10, 20, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"ordering" : true,
					 "aaSorting": [[ 0, "desc" ]]
				});
			
		});
		
		function doSubmit(){
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
  	<ul class="nav nav-tabs">
		<li class="${'total' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="total">总计</a></li>
		<li class="${'noUs' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="noUs">总计(不含美国)</a></li>
		<li class="${'eu' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="eu">欧洲</a></li>
		<li class="${'en' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="en">英语国家</a></li>
		<li class="${'nonEn' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="nonEn">非英语国家</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${saleProfit.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
	</ul>

   <form:form id="searchForm" modelAttribute="saleReport" action="${ctx}/amazoninfo/salesAnalysisReport" method="post" class="form-search">
		<input type="hidden" id="country" name="country" value="${saleProfit.country}"></input>
		<input id="searchType" name="searchType" type="hidden" value="${saleReport.searchType}" />
		
		<label>类型:</label>
		<select name="typeFlag" id="typeFlag" style="width:180px" onchange="doSubmit()">
			<c:forEach var="dic" items="${typeMap }">
				<option value="${dic.key }" ${typeFlag eq dic.key?'selected':''}>${dic.value }</option>
			</c:forEach>
		</select>
		
		<label>产品线:</label>
		<select name="line" id="line" style="width:120px" onchange="doSubmit()">
			<c:forEach var="line" items="${lines}">
				<option value="${line }" ${saleProfit.line eq line?'selected':''}>${line }线</option>
			</c:forEach>
		</select>
		
		<label>月份:</label>
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',isShowClear:false,maxDate:new Date(),onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${startDate}" pattern="yyyyMM"/>" class="input-small" id="day"/>
		-
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',isShowClear:false,maxDate:new Date(),onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${lastDate}" pattern="yyyyMM"/>" class="input-small" id="end"/>
		<%-- &nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/> --%> 
	</form:form>
	<tags:message content="${message}"/>
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;" rowspan='2'>月份</th>
				<c:forEach items='${starMap}' var='star'>
				     <th style="text-align: center;" colspan='2'>${star.key}</th>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach items='${starMap}' var='star'>
				     <th style="text-align: center;"><a title='第一个新品评价是否是差评'>是否差评</a></th>
				     <th style="text-align: center;"><a title='前五个评价新品差评比'>差评比(%)</a></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		    <c:forEach items='${countryList}' var='country'>
		         <tr>
					<td style="text-align: center;vertical-align: middle;">${fns:getDictLabel(country,'platform','')}</td>
					<c:forEach items='${starMap}' var='star'>
					       <td style="text-align: center;vertical-align: middle;">${starMap[star.key][country][saleProfit.line][0]}</td>
					       <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value='${starMap[star.key][country][saleProfit.line][1]}' maxFractionDigits='2' /></td>
					</c:forEach>
			   </tr>
		    </c:forEach>
		</tbody>
	</table>
	<br/>
	
	<table id="totalTb" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th style="text-align: center;">月份</th>
					<th style="text-align: center;">国家</th>
					<th style="text-align: center;">产品</th>
					<th style="text-align: center;"><a title='前五个评价'>星级</a></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items='${starMap}' var='star'>
				    <c:forEach items='${countryList}' var='country'>
				        <c:forEach items='${starMap[star.key][country]}' var='temp'>
				            <c:if test="${temp.key ne saleProfit.line}">
				                 <tr>
									<td style="text-align: center;vertical-align: middle">${star.key}</td>
									<td style="text-align: center;vertical-align: middle">${fns:getDictLabel(country,'platform','')}</td>
									<td style="text-align: center;vertical-align: middle">${temp.key}</td>
									<td style="text-align: center;vertical-align: middle">${starMap[star.key][country][temp.key][2] }</td>
						         </tr>
				            </c:if>
				        </c:forEach>
				    </c:forEach>
				</c:forEach>
			</tbody>
		</table>
</body>
</html>
