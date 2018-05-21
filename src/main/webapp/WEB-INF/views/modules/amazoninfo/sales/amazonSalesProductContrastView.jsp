<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>亚马逊产品销售对比</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
<style type="text/css">
.spanexr {
	float: right;
	min-height: 40px;
	width: 1000px
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
	
	var strdata=new Array();
	$.fn.dataTableExt.afnSortData['dom-html'] = function  ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return $('td:eq('+iColumn+') span', tr).html();
		} );
	}
	
	$.fn.dataTableExt.afnSortData['pecent'] = function  ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return $('td:eq('+iColumn+')', tr).text().replace('%','');
		} );
	}
	
	$(function() {

		$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 20,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [null,null,null,null,
					          { "sSortDataType":"dom-html", "sType":"numeric" },
					          { "sSortDataType":"pecent", "sType":"numeric" },
					          null,null,
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"pecent", "sType":"numeric" },
						      ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
		});
	
		$("#contentTbDiv .spanexr div:first").append($("#searchDiv").html());
		$("#searchDiv").remove();
		
		$("#btnExport").click(function(){
			$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportCompare");
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/contrast");
		});
	});

</script>
</head>
<body>
	<%-- <div id="searchDiv" style="display: none;float:left;" >
		<form id="searchForm" action="${ctx}/amazoninfo/salesReprots/contrast" method="post" >
			<b>区间1:</b>
				<input value="<fmt:formatDate value="${start}" pattern="yyyy-MM-dd"/>" readonly="readonly" name="start" style="width: 100px" id="d4311" class="Wdate" type="text" onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'d4312\')}'})"/>- 
				<input value="<fmt:formatDate value="${end}" pattern="yyyy-MM-dd"/>" readonly="readonly" name="end" style="width: 100px" id="d4312" class="Wdate" type="text" onFocus="WdatePicker({minDate:'#F{$dp.$D(\'d4311\')}'})"/>
			<b>区间2:</b>
				<input value="<fmt:formatDate value="${start1}" pattern="yyyy-MM-dd"/>" readonly="readonly" name="start1" style="width: 100px" id="d4313" class="Wdate" type="text" onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'d4314\')}'})"/>- 
				<input value="<fmt:formatDate value="${end1}" pattern="yyyy-MM-dd"/>" readonly="readonly" name="end1" style="width: 100px" id="d4314" class="Wdate" type="text" onFocus="WdatePicker({minDate:'#F{$dp.$D(\'d4313\')}'})"/> 
			<input name="country" type="hidden" value="${country}"/>
			&nbsp;&nbsp;<input  class="btn btn-primary btn-small" type="submit" value="<spring:message code="sys_but_search"/>"/>&nbsp;
			 <input  class="btn btn-primary btn-small" type="button" id="btnExport"  value="<spring:message code="sys_but_export"/>"/> 
		</form>
	</div>--%>
	<div id="contentTbDiv" style="width:80%;margin: auto">
		<br/>
		<div style="font-size: 25px; font-weight: bold; text-align: center;">
		<c:choose>
			<c:when test="${country eq 'eu'}">欧洲合计</c:when>
			<c:when test="${country eq 'en'}">英语国家合计</c:when>
			<c:when test="${country eq 'unEn'}">非英语国家合计</c:when>
			<c:otherwise>${fns:getDictLabel(country,'platform','各国合计')}</c:otherwise>
		</c:choose>
		
		${'total' eq lineType?'':allLine[lineType] }销售对比报告</div>
		<br/>
		<div style="text-align: center;">
		<form id="searchForm" action="${ctx}/amazoninfo/salesReprots/contrast" method="post" >
		<b> 货币类型:</b><select name="currencyType" id="currencyType" style="width: 100px;margin-left:10px;vertical-align: top;" onchange="changeCurrencyType()">
				<option value="EUR" ${'EUR' eq fn:trim(currencyType)?'selected':''}>EUR</option>
				<option value="USD" ${'USD' eq fn:trim(currencyType)?'selected':''}>USD</option>
		</select>
			<b>区间1:</b>
				<input value="<fmt:formatDate value="${start}" pattern="yyyy-MM-dd"/>" readonly="readonly" name="start" style="width: 100px" id="d4311" class="Wdate" type="text" onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'d4312\')}'})"/>- 
				<input value="<fmt:formatDate value="${end}" pattern="yyyy-MM-dd"/>" readonly="readonly" name="end" style="width: 100px" id="d4312" class="Wdate" type="text" onFocus="WdatePicker({minDate:'#F{$dp.$D(\'d4311\')}'})"/>
			<b>区间2:</b>
				<input value="<fmt:formatDate value="${start1}" pattern="yyyy-MM-dd"/>" readonly="readonly" name="start1" style="width: 100px" id="d4313" class="Wdate" type="text" onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'d4314\')}'})"/>- 
				<input value="<fmt:formatDate value="${end1}" pattern="yyyy-MM-dd"/>" readonly="readonly" name="end1" style="width: 100px" id="d4314" class="Wdate" type="text" onFocus="WdatePicker({minDate:'#F{$dp.$D(\'d4313\')}'})"/> 
			<input name="country" type="hidden" value="${country}"/>
			<input name="lineType" type="hidden" value="${lineType}"/>
			&nbsp;&nbsp;<input  class="btn btn-primary btn-small" type="submit" value="<spring:message code="sys_but_search"/>"/>&nbsp;
			 <input  class="btn btn-primary btn-small" type="button" id="btnExport"  value="<spring:message code="sys_but_export"/>"/> 
		</form>
		</div>
		<div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th style="text-align: center;vertical-align: middle;" rowspan="2">No.</th>
						<th style="vertical-align: middle;" rowspan="2">产品名称</th>
						<th style="text-align: center;vertical-align: middle;">
							<c:if test="${byTime eq '2'}">
								${fns:getDateByPattern(start,'yyyy')}第${fns:getWeekOfYear(start)}周
							</c:if>
							<c:if test="${byTime eq '3'}">
								${fns:getDateByPattern(start,'yyyy-MM')}月
							</c:if>
							<c:if test="${byTime eq '1'}">
								区间1
							</c:if>
						</th>
						<th style="text-align: center;vertical-align: middle;">
							<c:if test="${byTime eq '2'}">
								${fns:getDateByPattern(start1,'yyyy')}第${fns:getWeekOfYear(start1)}周
							</c:if>
							<c:if test="${byTime eq '3'}">
								${fns:getDateByPattern(start1,'yyyy-MM')}月
							</c:if>
							<c:if test="${byTime eq '1'}">
								区间2
							</c:if>
						</th>
						<th colspan="2" style="text-align: center;vertical-align: middle;">波动</th>
						<th style="text-align: center;vertical-align: middle;">
							<c:if test="${byTime eq '2'}">
								${fns:getDateByPattern(start,'yyyy')}第${fns:getWeekOfYear(start)}周
							</c:if>
							<c:if test="${byTime eq '3'}">
								${fns:getDateByPattern(start,'yyyy-MM')}月
							</c:if>
							<c:if test="${byTime eq '1'}">
								区间1
							</c:if>
						</th>
						<th style="text-align: center;vertical-align: middle;">
							<c:if test="${byTime eq '2'}">
								${fns:getDateByPattern(start1,'yyyy')}第${fns:getWeekOfYear(start1)}周
							</c:if>
							<c:if test="${byTime eq '3'}">
								${fns:getDateByPattern(start1,'yyyy-MM')}月
							</c:if>
							<c:if test="${byTime eq '1'}">
								区间2
							</c:if>
						</th>
						<th colspan="2" style="text-align: center;vertical-align: middle;">波动</th>
					</tr>
					<tr>
						<th style="text-align: center;vertical-align: middle;">销量</th>
						<th style="text-align: center;vertical-align: middle;">销量</th>
						<th style="text-align: center;vertical-align: middle;">销量</th>
						<th style="text-align: center;vertical-align: middle;">幅度(%)</th>
						
						<th style="text-align: center;vertical-align: middle;">销售额(${currencySymbol})</th>
						<th style="text-align: center;vertical-align: middle;">销售额(${currencySymbol})</th>
						<th style="text-align: center;vertical-align: middle;">销售额(${currencySymbol})</th>
						<th style="text-align: center;vertical-align: middle;">幅度(%)</th>
					</tr>
					
				</thead>
				<tbody>
					<c:set value="0" var="index" />
					<c:set value="0" var="total1" />
					<c:set value="0" var="total2" />
					<c:set value="0" var="total3" />
					<c:set value="0" var="total4" />
					<c:forEach items="${data1}" var="data" varStatus="i">
						<c:set value="${i.count}" var="index" />
						
						<c:set value="${data.value[1]}" var="sale1" />
						<c:set value="${empty data2[data.key]?0:data2[data.key][1]}" var="sale2" />
						<c:set value="${data.value[2]}" var="volume1" />
						<c:set value="${empty data2[data.key]?0:data2[data.key][2]}" var="volume2" />
						<c:set value="${total1+sale1}" var="total1" />
						<c:set value="${total2+sale2}" var="total2" />
						<c:set value="${total3+volume1}" var="total3" />
						<c:set value="${total4+volume2}" var="total4" />
						<c:choose>
							<c:when test="${volume1>0&&volume2>0}">
								<c:set var="flag" value="${volume2-volume1>0}" />
							</c:when>
							<c:when test="${volume1>0}">
								<c:set var="flag" value="flase" />
							</c:when>
							<c:when test="${volume2>0}">
								<c:set var="flag" value="true" />
							</c:when>
						</c:choose>
						<c:choose>
							<c:when test="${sale1>0&&sale2>0}">
								<c:set var="flag1" value="${sale2-sale1>0}" />
							</c:when>
							<c:when test="${sale1>0}">
								<c:set var="flag1" value="flase" />
							</c:when>
							<c:when test="${sale2>0}">
								<c:set var="flag1" value="true" />
							</c:when>
						</c:choose>
						<tr>
							<td style="text-align: center;vertical-align: middle;">${index}</td>
							<td>
								<a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${data.key}">${data.key}</a>
								<c:if test="${not empty typeLineMap[fn:toLowerCase(nameTypeMap[data.key])] }">(${typeLineMap[fn:toLowerCase(nameTypeMap[data.key])] }线)</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;">${volume1}</td>
							<td style="text-align: center;vertical-align: middle;">${volume2}</td>
							<td style="text-align: center;vertical-align: middle;"><span class="${!flag?'label label-important':'label label-success'}">${volume2-volume1}</span></td>
							<td style="text-align: center;vertical-align: middle;color: ${!flag?'red':'green'}">
								<c:choose>
									<c:when test="${volume1>0&&volume2>0}">
										${(volume2-volume1)*100/volume1}%
									</c:when>
									<c:when test="${volume1>0}">
										-100%
									</c:when>
									<c:when test="${volume2>0}">
										100%
									</c:when>
								</c:choose>
							</td>
							<td style="text-align: center;vertical-align: middle;">${sale1}</td>
							<td style="text-align: center;vertical-align: middle;">${sale2}</td>
							<td style="text-align: center;vertical-align: middle;"><span class="${!flag1?'label label-important':'label label-success'}"><fmt:formatNumber maxFractionDigits="2"  value="${sale2-sale1}" pattern="#0.00"/></span></td>
							<td style="text-align: center;vertical-align: middle;color: ${!flag1?'red':'green'}">
								<c:choose>
									<c:when test="${sale1>0&&sale2>0}">
										<fmt:formatNumber maxFractionDigits="2"  value="${(sale2-sale1)*100/sale1}" pattern="#0.00"/>%
									</c:when>
									<c:when test="${sale1>0}">
										-100%
									</c:when>
									<c:when test="${sale2>0}">
										100%
									</c:when>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
					<c:forEach items="${data2}" var="data" varStatus="i">
						<c:if test="${empty data1[data.key]}">
							<c:set value="${index+1}" var="index" />
							<c:set value="0" var="sale1" />
							<c:set value="${data.value[1]}" var="sale2" />
							<c:set value="0" var="volume1" />
							<c:set value="${data.value[2]}" var="volume2" />
							
							<c:set value="${total2+sale2}" var="total2" />
							<c:set value="${total4+volume2}" var="total4" />
							<tr>
								<td style="text-align: center;vertical-align: middle;">${index}</td>
								<td><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${data.key}">${data.key}</a></td>
								<td style="text-align: center;vertical-align: middle;">0</td>
								<td style="text-align: center;vertical-align: middle;">${volume2}</td>
								<td style="text-align: center;vertical-align: middle;"><span class="label label-success">${volume2}</span></td>
								<td style="text-align: center;vertical-align: middle;color:green">
									100%
								</td>
								<td style="text-align: center;vertical-align: middle;">0</td>
								<td style="text-align: center;vertical-align: middle;">${sale2}</td>
								<td style="text-align: center;vertical-align: middle;"><span class="label label-success">${sale2}</span></td>
								<td style="text-align: center;vertical-align: middle;color:green">
									100%
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<c:choose>
							<c:when test="${total1>0&&total2>0}">
								<c:set var="flag" value="${total2-total1>0}" />
							</c:when>
							<c:when test="${total1>0}">
								<c:set var="flag" value="flase" />
							</c:when>
							<c:when test="${total2>0}">
								<c:set var="flag" value="true" />
							</c:when>
						</c:choose>
						<c:choose>
							<c:when test="${total3>0&&total4>0}">
								<c:set var="flag1" value="${total4-total3>0}" />
							</c:when>
							<c:when test="${total3>0}">
								<c:set var="flag1" value="flase" />
							</c:when>
							<c:when test="${total4>0}">
								<c:set var="flag1" value="true" />
							</c:when>
						</c:choose>
						
						<td colspan="2" style="font-size: 18px; font-weight: bold;text-align: center;vertical-align: middle;">Total</td>
						<td style="text-align: center;vertical-align: middle;">${total3}</td>
						<td style="text-align: center;vertical-align: middle;">${total4}</td>
						<td style="text-align: center;vertical-align: middle;"><span class="${!flag1?'label label-important':'label label-success'}">${total4-total3}</span></td>
						<td style="text-align: center;vertical-align: middle;color: ${!flag1?'red':'green'}">
							<c:choose>
								<c:when test="${total3>0&&total4>0}">
									<fmt:formatNumber maxFractionDigits="2"  value="${(total4-total3)*100/total3}" pattern="#0.00"/>%
								</c:when>
								<c:when test="${total3>0}">
									-100%
								</c:when>
								<c:when test="${total4>0}">
									100%
								</c:when>
							</c:choose>
						</td>
						<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber maxFractionDigits="2"  value="${total1}" pattern="#0.00"/></td>
						<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber maxFractionDigits="2"  value="${total2}" pattern="#0.00"/></td>
						<td style="text-align: center;vertical-align: middle;"><span class="${!flag?'label label-important':'label label-success'}"><fmt:formatNumber maxFractionDigits="2"  value="${total2-total1}" pattern="#0.00"/></span></td>
						<td style="text-align: center;vertical-align: middle;color: ${!flag?'red':'green'}">
								<c:choose>
									<c:when test="${total1>0&&total2>0}">
									<fmt:formatNumber maxFractionDigits="2"  value="${(total2-total1)*100/total1}" pattern="#0.00"/>%
									</c:when>
									<c:when test="${total1>0}">
										-100%
									</c:when>
									<c:when test="${total2>0}">
										100%
									</c:when>
								</c:choose>
						</td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
</body>
</html>
