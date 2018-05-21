<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>按月库存分析</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			
			if(!(top)){
				top = self;
			}
			
			var arr = $(".total");
			var num = 0;
			/**
			var totalInventory = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					totalInventory += parseInt($(this).text());
				}
			});
			if(totalInventory){
				$(".totalInventory").append(totalInventory);
			}*/
			//<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">
				var country	= '${dic.value}';
				arr = $("."+country+"total");
				num = 0;
				arr.each(function() {
					if ($.trim($(this).text())) {
						num += parseInt($(this).text());
					}
				});
				if(num){
					$("#"+country+"total").append(num);
				}
			//</c:if></c:forEach>
			
			arr = $(".eutotal");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#eutotal").append(num);
			}
			
			arr = $(".mztotal");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#mztotal").append(num);
			}
			
			$(".countryHref").click(function(){
				var params ={};
				params.active=  $(".btn-group .active").attr('act');
				$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?'+$.param(params));
				$("input[name='country']").val($(this).attr("key"));
				var country = '${businessReport.country}';
				if((country == null || country.length == 0) || $(this).attr("key") == null){
					$("#date1").val("");
					$("#date2").val("");
					$("#createDate").val("");
					$("#dataDate").val("");
				}
				$("#searchForm").submit();
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			
			
			$("#btnSubmit").click(function(){
				$('#searchForm').attr('action','${ctx}/psi/inventoryAnalysis?');
				$("#searchForm").submit();
			});
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/inventoryAnalysis/exportDetail");
						$("#searchForm").submit();
						$('#searchForm').attr('action','${ctx}/psi/inventoryAnalysis?');
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		
		function timeOnChange(){
			$('#searchForm').attr('action','${ctx}/psi/inventoryAnalysis?');
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="businessReport" action="${ctx}/psi/inventoryAnalysis" method="post" class="breadcrumb form-search">
	<div style="height: 30px;margin-top:10px">
		数据节点：
		<select name="dataFlag" id="dataFlag" style="width: 120px" onchange="timeOnChange()">
			<option value="1" ${dataFlag eq '1'?'selected':''}>月底</option>
			<option value="2" ${dataFlag eq '2'?'selected':''}>月中</option>	
		</select>&nbsp;&nbsp;
		<label>月份：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',maxDate:'%y-{%M-1}-%d',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="monthDate" value="${monthDate}" class="input-small" id="monthDate"/>
		<label>导出截止月份(<span style="color:red">仅导出有效</span>)：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',minDate:'${minDate}',maxDate:'%y-{%M-1}-%d'});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="${monthDate}" class="input-small" id="endDate"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
	</div>
	</form:form>
	<div id="inventory">
		<table id="totalTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th style="text-align: center;vertical-align: middle;" colspan="12">
					${monthDate}月${'1' eq dataFlag?'月底':'月中'}库存分品类统计
				</th></tr>
				<tr>
					<th style="text-align: center;vertical-align: middle;" rowspan="2">品类</th>
					<th style="text-align: center;vertical-align: middle;" rowspan="2">总库存</th>
					<th colspan="6" style="text-align: center;vertical-align: middle;color: #08c;">欧洲总库存</th>
					<th colspan="4" style="text-align: center;vertical-align: middle;color: #08c;">美洲总库存</th>
					<th style="text-align: center;vertical-align: middle;" rowspan="2">日本</th>
				</tr>
				<tr>
				    <th style="text-align: center;vertical-align: middle;">德国</th>
					<th style="text-align: center;vertical-align: middle;">英国</th>
					<th style="text-align: center;vertical-align: middle;">法国</th>
					<th style="text-align: center;vertical-align: middle;">意大利</th>
					<th style="text-align: center;vertical-align: middle;">西班牙</th>
					<th style="text-align: center;vertical-align: middle;color: #08c">欧洲总计</th>
					<th style="text-align: center;vertical-align: middle;">美国</th>
					<th style="text-align: center;vertical-align: middle;">加拿大</th>
					<th style="text-align: center;vertical-align: middle;">墨西哥</th>
					<th style="text-align: center;vertical-align: middle;color: #08c">美洲总计</th>
				</tr>
			</thead>
				<c:set var="total" value="${data[dataFlag]['1']['total']+data[dataFlag]['2']['total']+data[dataFlag]['3']['total']+data[dataFlag]['4']['total']}"/>
				<c:set var="totalSale" value="${data[dataFlag]['1']['total31sale']+data[dataFlag]['2']['total31sale']+data[dataFlag]['3']['total31sale']+data[dataFlag]['4']['total31sale']}"/>
				<c:set var="euTotal" value="${data[dataFlag]['1']['eu']+data[dataFlag]['2']['eu']+data[dataFlag]['3']['eu']+data[dataFlag]['4']['eu']}"/>
				<c:set var="euTotalSale" value="${data[dataFlag]['1']['eu31sale']+data[dataFlag]['2']['eu31sale']+data[dataFlag]['3']['eu31sale']+data[dataFlag]['4']['eu31sale']}"/>
				<c:set var="usTotal" value="${data[dataFlag]['1']['com']+data[dataFlag]['2']['com']+data[dataFlag]['3']['com']+data[dataFlag]['4']['com']}"/>
				<c:set var="usTotalSale" value="${data[dataFlag]['1']['com31sale']+data[dataFlag]['2']['com31sale']+data[dataFlag]['3']['com31sale']+data[dataFlag]['4']['com31sale']}"/>
				<c:set var="caTotal" value="${data[dataFlag]['1']['ca']+data[dataFlag]['2']['ca']+data[dataFlag]['3']['ca']+data[dataFlag]['4']['ca']}"/>
				<c:set var="caTotalSale" value="${data[dataFlag]['1']['ca31sale']+data[dataFlag]['2']['ca31sale']+data[dataFlag]['3']['ca31sale']+data[dataFlag]['4']['ca31sale']}"/>
				<c:set var="mxTotal" value="${data[dataFlag]['1']['mx']+data[dataFlag]['2']['mx']+data[dataFlag]['3']['mx']+data[dataFlag]['4']['mx']}"/>
				<c:set var="mxTotalSale" value="${data[dataFlag]['1']['mx31sale']+data[dataFlag]['2']['mx31sale']+data[dataFlag]['3']['mx31sale']+data[dataFlag]['4']['mx31sale']}"/>
				<c:set var="amTotal" value="${usTotal+caTotal+mxTotal}"/>
				<c:set var="amTotalSale" value="${usTotalSale+caTotalSale+mxTotalSale}"/>
				<c:set var="jpTotal" value="${data[dataFlag]['1']['jp']+data[dataFlag]['2']['jp']+data[dataFlag]['3']['jp']+data[dataFlag]['4']['jp']}"/>
				<c:set var="jpTotalSale" value="${data[dataFlag]['1']['jp31sale']+data[dataFlag]['2']['jp31sale']+data[dataFlag]['3']['jp31sale']+data[dataFlag]['4']['jp31sale']}"/>
				<c:forEach items="${typeList}" var="type">
					<tr>
						<td style="text-align: center;vertical-align: middle;">${typeMap[type] }</td>
							<td style="text-align: center;vertical-align: middle;" class="total">
								${data[dataFlag][type]['total']}<%--31日销示例${data[type]['total31sale']} --%>
							</td>
							<td style="text-align: center;vertical-align: middle;" class="detotal">
								${data[dataFlag][type]['de']}
							</td>
							<td style="text-align: center;vertical-align: middle;" class="uktotal">
								${data[dataFlag][type]['uk']}
							</td>
							<td style="text-align: center;vertical-align: middle;" class="frtotal">
								${data[dataFlag][type]['fr']}
							</td>
							<td style="text-align: center;vertical-align: middle;" class="ittotal">
								${data[dataFlag][type]['it']}
							</td>
							<td style="text-align: center;vertical-align: middle;" class="estotal">
								${data[dataFlag][type]['es']}
							</td>
							<td style="text-align: center;vertical-align: middle;color: #08c" class="eutotal">
								${data[dataFlag][type]['eu']}
							</td>
							<td style="text-align: center;vertical-align: middle;" class="comtotal">
								${data[dataFlag][type]['com']}
							</td>
							<td style="text-align: center;vertical-align: middle;" class="catotal">
								${data[dataFlag][type]['ca']}
							</td>
							<td style="text-align: center;vertical-align: middle;" class="mxtotal">
								${data[dataFlag][type]['mx']}
							</td>
							<td style="text-align: center;vertical-align: middle;color: #08c" class="mztotal">
								${data[dataFlag][type]['com'] + data[dataFlag][type]['ca'] + data[dataFlag][type]['mx']}
							</td>
							<td style="text-align: center;vertical-align: middle;" class="jptotal">
								${data[dataFlag][type]['jp']}
							</td>
					</tr>
				</c:forEach>
				<tr>
					<td style="text-align: center;vertical-align: middle;">总计</td>
					<td class="totalInventory" style="text-align: center;vertical-align: middle;">${total }</td>
					<td id="detotal" style="text-align: center;vertical-align: middle;"></td>
					<td id="uktotal" style="text-align: center;vertical-align: middle;"></td>
					<td id="frtotal" style="text-align: center;vertical-align: middle;"></td>
					<td id="ittotal" style="text-align: center;vertical-align: middle;"></td>
					<td id="estotal" style="text-align: center;vertical-align: middle;"></td>
					<td id="eutotal" style="text-align: center;vertical-align: middle;color: #08c"></td>
					<td id="comtotal" style="text-align: center;vertical-align: middle;"></td>
					<td id="catotal" style="text-align: center;vertical-align: middle;"></td>
					<td id="mxtotal" style="text-align: center;vertical-align: middle;"></td>
					<td id="mztotal" style="text-align: center;vertical-align: middle;color: #08c"></td>
					<td id="jptotal" style="text-align: center;vertical-align: middle;"></td>
				</tr>
			</tbody>
		</table>
	</div>
	<br/>
	<div id="statistical">
		<table id="statisticalTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th style="text-align: center;vertical-align: middle;" colspan="8">
					${monthDate}月库存水平统计
				</th></tr>
				<tr>
					<th style="text-align: center;vertical-align: middle;">平台</th>
					<th style="text-align: center;vertical-align: middle;">品类</th>
					<th style="text-align: center;vertical-align: middle;">计数</th>
					<th style="text-align: center;vertical-align: middle;">31日销</th>
					<th style="text-align: center;vertical-align: middle;">总库存</th>
					<th style="text-align: center;vertical-align: middle;">总库存/31日销</th>
					<th style="text-align: center;vertical-align: middle;">库存百分比</th>
					<th style="text-align: center;vertical-align: middle;">销量百分比</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${typeList}" var="type">
					<tr><td style="text-align: center;vertical-align: middle;">全球</td>
						<td style="text-align: center;vertical-align: middle;">${typeMap[type] }</td>
						<td style="text-align: center;vertical-align: middle;" class="typeNum">
							${typeNum[dataFlag][type]['total']}
						</td>
						<td style="text-align: center;vertical-align: middle;" class="day31sale">
							${data[dataFlag][type]['total31sale']}
						</td>
						<td style="text-align: center;vertical-align: middle;">
							${data[dataFlag][type]['total']}
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data[dataFlag][type]['total31sale']>0}">
								<fmt:formatNumber value="${data[dataFlag][type]['total']/data[dataFlag][type]['total31sale']}" maxFractionDigits="1" />
							</c:if>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${total>0}">
								<fmt:formatNumber value="${data[dataFlag][type]['total']*100/total}" maxFractionDigits="2" />%
							</c:if>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${totalSale>0}">
								<fmt:formatNumber value="${data[dataFlag][type]['total31sale']*100/totalSale}" maxFractionDigits="2" />%
							</c:if>
						</td>
					</tr>
				</c:forEach>
				<tr>
					<td style="text-align: center;vertical-align: middle;color: #08c" colspan="2">全球总计</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${typeNum[dataFlag]['1']['total']+typeNum[dataFlag]['2']['total']+typeNum[dataFlag]['3']['total']+typeNum[dataFlag]['4']['total']}</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${totalSale }</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${total }</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">
						<c:if test="${totalSale>0}">
							<fmt:formatNumber value="${total/totalSale}" maxFractionDigits="1" />
						</c:if>
					</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">100%</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">100%</td>
				</tr>
				<!-- 欧洲 -->
				<c:forEach items="${typeList}" var="type">
					<tr>
						<td style="text-align: center;vertical-align: middle;">欧洲</td>
						<td style="text-align: center;vertical-align: middle;">${typeMap[type] }</td>
						<td style="text-align: center;vertical-align: middle;" class="typeNum">
							${typeNum[dataFlag][type]['de']}
						</td>
						<td style="text-align: center;vertical-align: middle;" class="day31sale">
							${data[dataFlag][type]['eu31sale']}
						</td>
						<td style="text-align: center;vertical-align: middle;">
							${data[dataFlag][type]['eu']}
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data[dataFlag][type]['eu31sale']>0}">
								<fmt:formatNumber value="${data[dataFlag][type]['eu']/data[dataFlag][type]['eu31sale']}" maxFractionDigits="1" />
							</c:if>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${euTotal>0}">
								<fmt:formatNumber value="${data[dataFlag][type]['eu']*100/euTotal}" maxFractionDigits="2" />%
							</c:if>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${euTotalSale>0}">
								<fmt:formatNumber value="${data[dataFlag][type]['eu31sale']*100/euTotalSale}" maxFractionDigits="2" />%
							</c:if>
						</td>
					</tr>
				</c:forEach>
				<tr>
					<td style="text-align: center;vertical-align: middle;color: #08c" colspan="2">欧洲总计</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${typeNum[dataFlag]['1']['de']+typeNum[dataFlag]['2']['de']+typeNum[dataFlag]['3']['de']+typeNum[dataFlag]['4']['de']}</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${euTotalSale }</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${euTotal }</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">
						<c:if test="${euTotalSale>0}">
							<fmt:formatNumber value="${euTotal/euTotalSale}" maxFractionDigits="1" />
						</c:if>
					</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">100%</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">100%</td>
				</tr>
				<!-- 美洲 -->
				<c:forEach items="${typeList}" var="type">
					<tr>
						<td style="text-align: center;vertical-align: middle;">美洲</td>
						<td style="text-align: center;vertical-align: middle;">${typeMap[type] }</td>
						<td style="text-align: center;vertical-align: middle;" class="typeNum">
							${typeNum[dataFlag][type]['com']}
						</td>
						<td style="text-align: center;vertical-align: middle;" class="day31sale">
							${data[dataFlag][type]['com31sale']+data[dataFlag][type]['ca31sale']+data[dataFlag][type]['mx31sale']}
						</td>
						<td style="text-align: center;vertical-align: middle;">
							${data[dataFlag][type]['com']+data[dataFlag][type]['ca']+data[dataFlag][type]['mx']}
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data[dataFlag][type]['eu31sale']>0}">
								<fmt:formatNumber value="${(data[dataFlag][type]['com']+data[dataFlag][type]['ca']+data[dataFlag][type]['mx'])/(data[dataFlag][type]['com31sale']+data[dataFlag][type]['ca31sale']+data[dataFlag][type]['mx31sale'])}" maxFractionDigits="1" />
							</c:if>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${amTotal>0}">
								<fmt:formatNumber value="${(data[dataFlag][type]['com']+data[dataFlag][type]['ca']+data[dataFlag][type]['mx'])*100/amTotal}" maxFractionDigits="2" />%
							</c:if>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${amTotalSale>0}">
								<fmt:formatNumber value="${(data[dataFlag][type]['com31sale']+data[dataFlag][type]['ca31sale']+data[dataFlag][type]['mx31sale'])*100/amTotalSale}" maxFractionDigits="2" />%
							</c:if>
						</td>
					</tr>
				</c:forEach>
				<tr>
					<td style="text-align: center;vertical-align: middle;color: #08c" colspan="2">美洲总计</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${typeNum[dataFlag]['1']['com']+typeNum[dataFlag]['2']['com']+typeNum[dataFlag]['3']['com']+typeNum[dataFlag]['4']['com']}</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${amTotalSale }</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${amTotal }</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">
						<c:if test="${amTotalSale>0}">
							<fmt:formatNumber value="${amTotal/amTotalSale}" maxFractionDigits="1" />
						</c:if>
					</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">100%</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">100%</td>
				</tr>
				<!-- 日本 -->
				<c:forEach items="${typeList}" var="type">
					<tr>
						<td style="text-align: center;vertical-align: middle;">日本</td>
						<td style="text-align: center;vertical-align: middle;">${typeMap[type] }</td>
						<td style="text-align: center;vertical-align: middle;" class="typeNum">
							${typeNum[dataFlag][type]['jp']}
						</td>
						<td style="text-align: center;vertical-align: middle;" class="day31sale">
							${data[dataFlag][type]['jp31sale']}
						</td>
						<td style="text-align: center;vertical-align: middle;">
							${data[dataFlag][type]['jp']}
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data[dataFlag][type]['jp31sale']>0}">
								<fmt:formatNumber value="${data[dataFlag][type]['jp']/data[dataFlag][type]['jp31sale']}" maxFractionDigits="1" />
							</c:if>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${jpTotal>0}">
								<fmt:formatNumber value="${data[dataFlag][type]['jp']*100/jpTotal}" maxFractionDigits="2" />%
							</c:if>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${jpTotalSale>0}">
								<fmt:formatNumber value="${data[dataFlag][type]['jp31sale']*100/jpTotalSale}" maxFractionDigits="2" />%
							</c:if>
						</td>
					</tr>
				</c:forEach>
				<tr>
					<td style="text-align: center;vertical-align: middle;color: #08c" colspan="2">日本总计</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${typeNum[dataFlag]['1']['jp']+typeNum[dataFlag]['2']['jp']+typeNum[dataFlag]['3']['jp']+typeNum[dataFlag]['4']['jp']}</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${jpTotalSale }</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">${jpTotal }</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">
						<c:if test="${jpTotalSale>0}">
							<fmt:formatNumber value="${jpTotal/jpTotalSale}" maxFractionDigits="1" />
						</c:if>
					</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">100%</td>
					<td style="text-align: center;vertical-align: middle;color: #08c">100%</td>
				</tr>
			</tbody>
		</table>
	</div>
	<br/>
	<div id="history">
		<table id="historyTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th style="text-align: center;vertical-align: middle;" colspan="13">
					历史库存统计
				</th></tr>
				<tr>
					<th style="text-align: center;vertical-align: middle;">数据节点</th>
					<c:forEach items="${xList}" var="x">
						<th style="text-align: center;vertical-align: middle;">${x }月</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td style="text-align: center;vertical-align: middle;">月中</td>
					<c:forEach items="${xList}" var="x">
						<th style="text-align: center;vertical-align: middle;">${historyInventory['2'][x] }</th>
					</c:forEach>
				</tr>
				<tr>
					<td style="text-align: center;vertical-align: middle;">月底</td>
					<c:forEach items="${xList}" var="x">
						<th style="text-align: center;vertical-align: middle;">${historyInventory['1'][x] }</th>
					</c:forEach>
				</tr>
			</tbody>
		</table>
	</div>
	<br/>
</body>
</html>
