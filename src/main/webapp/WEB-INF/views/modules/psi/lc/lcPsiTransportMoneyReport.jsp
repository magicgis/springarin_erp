<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>物流报表</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
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
		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/exportTransportReport");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/byMonthInfo");
			});
		});
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*10)/10;  
	            return f;  
	     }  
		
		
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
    <li ><a href="${ctx}/psi/psiTransportPayment/byMonthInfo">物流报表</a></li>
	  <li><a href="${ctx}/psi/psiTransportPayment/byMonthTransport">运输分析</a></li>
	  <li><a href="${ctx}/psi/psiTransportPayment/byMonthTransport2">运输分析(排除新品)</a></li>
	  <li><a href="${ctx}/psi/psiTransportPayment/byMonthTransportOffLine">线下运输分析</a></li>
	   <li class="active"><a href="${ctx}/psi/lcPsiTransportPayment/byMonthInfo">物流报表(理诚)</a></li>
	  <li ><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransport">运输分析(理诚)</a></li>
	  <li ><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransport2">运输分析(排除新品)(理诚)</a></li>
	  <li ><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransportOffLine">线下运输分析(理诚)</a></li>
	</ul>
	<form id="searchForm" action="${ctx}/psi/lcPsiTransportPayment/byMonthInfo" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
	   &nbsp;&nbsp;&nbsp;&nbsp;统计时间:
		<input style="width: 80px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});"  readonly="readonly"  class="Wdate" type="text" id="sureDate" name="sureDate" value="<fmt:formatDate value="${psiTransportPayment.sureDate}" pattern="yyyyMM" />" class="input-small"/>&nbsp;-&nbsp;
		<input style="width: 80px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});"  readonly="readonly"  class="Wdate" type="text" id="updateDate" name="updateDate" value="<fmt:formatDate value="${psiTransportPayment.updateDate}" pattern="yyyyMM" />" class="input-small"/>
	    <input id="btnSubmit" class="btn btn-primary" type="button" value="Export"/>
	</form>
	
	<table id="money" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">总额</th>
				<c:forEach items="${byMonthMoney}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total/RMB</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EU</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthMoney[moneyTitle.key]['eu'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthMoney[moneyTitle.key]['eu'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">US</td>
		       <c:set var="USTotal" value="0" />
		       <c:forEach items="${byMonthMoney}" var="moneyTitle">
		           <c:set var="USTotal" value="${USTotal+byMonthMoney[moneyTitle.key]['US'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthMoney[moneyTitle.key]['US'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${USTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">JP</td>
		       <c:set var="jpTotal" value="0" />
		       <c:forEach items="${byMonthMoney}" var="moneyTitle">
		            <c:set var="jpTotal" value="${jpTotal+byMonthMoney[moneyTitle.key]['jp'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${byMonthMoney[moneyTitle.key]['jp'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${jpTotal }"  maxFractionDigits="1"/></td>
		      </tr>
		       
		     <tr>
		         <td style="text-align: center;vertical-align: middle;">Total/RMB</td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthMoney}" var="moneyTitle">
		             <c:set var="total" value="${total+byMonthMoney[moneyTitle.key]['jp']+byMonthMoney[moneyTitle.key]['US']+byMonthMoney[moneyTitle.key]['eu'] }" />
		             <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthMoney[moneyTitle.key]['jp']+byMonthMoney[moneyTitle.key]['US']+byMonthMoney[moneyTitle.key]['eu'] }"  maxFractionDigits="1"/></td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${total }"  maxFractionDigits="1"/></td>
		     </tr>
		</tbody>
	</table>
	<br/>
	
	<table id="unitPrice" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">单价</th>
				<c:forEach items="${byMonthMoney}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">RMB/KG</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EU</td>
		       <c:set var="USTotal1" value="0" />
		        <c:set var="USTotal2" value="0" />
		       <c:forEach items="${byMonthMoney}" var="moneyTitle">
		           <c:set var="USTotal1" value="${USTotal1+byMonthMoney[moneyTitle.key]['eu']  }" />
		           <c:set var="USTotal2" value="${USTotal2+byMonthOtherInfo[moneyTitle.key]['eu'].weight  }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${byMonthMoney[moneyTitle.key]['eu']/byMonthOtherInfo[moneyTitle.key]['eu'].weight  }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${USTotal1/USTotal2 }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">US</td>
		       <c:set var="USTotal1" value="0" />
		        <c:set var="USTotal2" value="0" />
		       <c:forEach items="${byMonthMoney}" var="moneyTitle">
		           <c:set var="USTotal1" value="${USTotal1+byMonthMoney[moneyTitle.key]['US']  }" />
		           <c:set var="USTotal2" value="${USTotal2+byMonthOtherInfo[moneyTitle.key]['US'].weight  }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${byMonthMoney[moneyTitle.key]['US']/byMonthOtherInfo[moneyTitle.key]['US'].weight  }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${USTotal1/USTotal2 }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">JP</td>
		       <c:set var="USTotal1" value="0" />
		        <c:set var="USTotal2" value="0" />
		       <c:forEach items="${byMonthMoney}" var="moneyTitle">
		           <c:set var="USTotal1" value="${USTotal1+byMonthMoney[moneyTitle.key]['jp']  }" />
		           <c:set var="USTotal2" value="${USTotal2+byMonthOtherInfo[moneyTitle.key]['jp'].weight  }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${byMonthMoney[moneyTitle.key]['jp']/byMonthOtherInfo[moneyTitle.key]['jp'].weight  }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${USTotal1/USTotal2 }"  maxFractionDigits="1"/></td>
		      </tr>
		       
		     <tr>
		         <td style="text-align: center;vertical-align: middle;">RMB/KG</td>
		         <c:set var="USTotal1" value="0" />
		         <c:set var="USTotal2" value="0" />
		         <c:forEach items="${byMonthMoney}" var="moneyTitle">
		             <c:set var="USTotal1" value="${USTotal1+byMonthMoney[moneyTitle.key]['jp']+byMonthMoney[moneyTitle.key]['US'] +byMonthMoney[moneyTitle.key]['eu']   }" />
		             <c:set var="USTotal2" value="${USTotal2+byMonthOtherInfo[moneyTitle.key]['jp'].weight+byMonthOtherInfo[moneyTitle.key]['US'].weight +byMonthOtherInfo[moneyTitle.key]['eu'].weight   }" /> 
		             <td style="text-align: center;vertical-align: middle;">
		                <fmt:formatNumber pattern="#######.#" value=" ${(byMonthMoney[moneyTitle.key]['jp']+byMonthMoney[moneyTitle.key]['US']+byMonthMoney[moneyTitle.key]['eu'])/(byMonthOtherInfo[moneyTitle.key]['jp'].weight+byMonthOtherInfo[moneyTitle.key]['US'].weight+byMonthOtherInfo[moneyTitle.key]['eu'].weight) }"  maxFractionDigits="1"/>
		             </td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${USTotal1/USTotal2 }"  maxFractionDigits="1"/></td>
		     </tr>
		</tbody>
	</table>
	<br/>
	
	<table id="KGS" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">KGS</th>
				<c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total/KG</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EU</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthOtherInfo[moneyTitle.key]['eu'].weight }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthOtherInfo[moneyTitle.key]['eu'].weight }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">US</td>
		       <c:set var="USTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		           <c:set var="USTotal" value="${USTotal+byMonthOtherInfo[moneyTitle.key]['US'].weight  }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${byMonthOtherInfo[moneyTitle.key]['US'].weight  }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${USTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">JP</td>
		       <c:set var="jpTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		            <c:set var="jpTotal" value="${jpTotal+byMonthOtherInfo[moneyTitle.key]['jp'].weight }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthOtherInfo[moneyTitle.key]['jp'].weight  }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${jpTotal }"  maxFractionDigits="1"/></td>
		      </tr>
		       
		     <tr>
		         <td style="text-align: center;vertical-align: middle;">Total/KG</td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		             <c:set var="total" value="${total+byMonthOtherInfo[moneyTitle.key]['jp'].weight +byMonthOtherInfo[moneyTitle.key]['US'].weight +byMonthOtherInfo[moneyTitle.key]['eu'].weight  }" />
		             <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthOtherInfo[moneyTitle.key]['jp'].weight +byMonthOtherInfo[moneyTitle.key]['US'].weight +byMonthOtherInfo[moneyTitle.key]['eu'].weight  }"  maxFractionDigits="1"/></td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${total }"  maxFractionDigits="1"/></td>
		     </tr>
		</tbody>
	</table>
	<br/>
	<table id="CBM" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">CBM</th>
				<c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total/CBM</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EU</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthOtherInfo[moneyTitle.key]['eu'].volume }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${byMonthOtherInfo[moneyTitle.key]['eu'].volume }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">US</td>
		       <c:set var="USTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		           <c:set var="USTotal" value="${USTotal+byMonthOtherInfo[moneyTitle.key]['US'].volume  }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${byMonthOtherInfo[moneyTitle.key]['US'].volume  }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${USTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">JP</td>
		       <c:set var="jpTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		            <c:set var="jpTotal" value="${jpTotal+byMonthOtherInfo[moneyTitle.key]['jp'].volume }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${byMonthOtherInfo[moneyTitle.key]['jp'].volume  }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${jpTotal }"  maxFractionDigits="1"/></td>
		      </tr>
		       
		     <tr>
		         <td style="text-align: center;vertical-align: middle;">Total/CBM</td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		             <c:set var="total" value="${total+byMonthOtherInfo[moneyTitle.key]['jp'].volume +byMonthOtherInfo[moneyTitle.key]['US'].volume +byMonthOtherInfo[moneyTitle.key]['eu'].volume  }" />
		             <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${byMonthOtherInfo[moneyTitle.key]['jp'].volume +byMonthOtherInfo[moneyTitle.key]['US'].volume +byMonthOtherInfo[moneyTitle.key]['eu'].volume  }"  maxFractionDigits="1"/></td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="${total }"  maxFractionDigits="1"/></td>
		     </tr>
		</tbody>
	</table>
	<br/>
	<table id="Shpt" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">Shpt</th>
				<c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total/Shpt</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EU</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthOtherInfo[moneyTitle.key]['eu'].teu }" />
		           <td style="text-align: center;vertical-align: middle;">${byMonthOtherInfo[moneyTitle.key]['eu'].teu }</td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;">${euTotal}</td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">US</td>
		       <c:set var="USTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		           <c:set var="USTotal" value="${USTotal+byMonthOtherInfo[moneyTitle.key]['US'].teu  }" />
		           <td style="text-align: center;vertical-align: middle;">${byMonthOtherInfo[moneyTitle.key]['US'].teu  }</td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;">${USTotal }</td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">JP</td>
		       <c:set var="jpTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		            <c:set var="jpTotal" value="${jpTotal+byMonthOtherInfo[moneyTitle.key]['jp'].teu }" />
		           <td style="text-align: center;vertical-align: middle;">${byMonthOtherInfo[moneyTitle.key]['jp'].teu  }</td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;">${jpTotal }</td>
		      </tr>
		       
		     <tr>
		         <td style="text-align: center;vertical-align: middle;">Total/Shpt</td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		             <c:set var="total" value="${total+byMonthOtherInfo[moneyTitle.key]['jp'].teu +byMonthOtherInfo[moneyTitle.key]['US'].teu +byMonthOtherInfo[moneyTitle.key]['eu'].teu  }" />
		             <td style="text-align: center;vertical-align: middle;">${byMonthOtherInfo[moneyTitle.key]['jp'].teu +byMonthOtherInfo[moneyTitle.key]['US'].teu +byMonthOtherInfo[moneyTitle.key]['eu'].teu  }</td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;">${total }</td>
		     </tr>
		</tbody>
	</table>
	<br/>
	<table id="CTNS" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">CTNS</th>
				<c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total/CTNS</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EU</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthOtherInfo[moneyTitle.key]['eu'].boxNumber }" />
		           <td style="text-align: center;vertical-align: middle;">${byMonthOtherInfo[moneyTitle.key]['eu'].boxNumber }</td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;">${euTotal }</td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">US</td>
		       <c:set var="USTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		           <c:set var="USTotal" value="${USTotal+byMonthOtherInfo[moneyTitle.key]['US'].boxNumber  }" />
		           <td style="text-align: center;vertical-align: middle;">${byMonthOtherInfo[moneyTitle.key]['US'].boxNumber  }</td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;">${USTotal }</td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">JP</td>
		       <c:set var="jpTotal" value="0" />
		       <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		            <c:set var="jpTotal" value="${jpTotal+byMonthOtherInfo[moneyTitle.key]['jp'].boxNumber }" />
		           <td style="text-align: center;vertical-align: middle;">${byMonthOtherInfo[moneyTitle.key]['jp'].boxNumber  }</td>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;">${jpTotal }</td>
		      </tr>
		       
		     <tr>
		         <td style="text-align: center;vertical-align: middle;">Total/CTNS</td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthOtherInfo}" var="moneyTitle">
		             <c:set var="total" value="${total+byMonthOtherInfo[moneyTitle.key]['jp'].boxNumber +byMonthOtherInfo[moneyTitle.key]['US'].boxNumber +byMonthOtherInfo[moneyTitle.key]['eu'].boxNumber  }" />
		             <td style="text-align: center;vertical-align: middle;">${byMonthOtherInfo[moneyTitle.key]['jp'].boxNumber +byMonthOtherInfo[moneyTitle.key]['US'].boxNumber +byMonthOtherInfo[moneyTitle.key]['eu'].boxNumber  }</td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;">${total }</td>
		     </tr>
		</tbody>
	</table>
</body>
</html>
