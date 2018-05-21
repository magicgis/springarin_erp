<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运输分析</title>
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
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/exportTransportReport2");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/byMonthTransport");
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
	 <li><a href="${ctx}/psi/lcPsiTransportPayment/byMonthInfo">物流报表(理诚)</a></li>
	  <li  ><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransport">运输分析(理诚)</a></li>
	  <li class="active"><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransport2">运输分析(排除新品)(理诚)</a></li>
	  <li><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransportOffLine">线下运输分析(理诚)</a></li>
	</ul>
	<form id="searchForm" action="${ctx}/psi/lcPsiTransportPayment/byMonthTransport2" method="post"  class="breadcrumb form-search" cssStyle="height: 80px;">
	   &nbsp;&nbsp;&nbsp;&nbsp;统计时间:
		<input style="width: 80px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});"  readonly="readonly"  class="Wdate" type="text" id="sureDate" name="sureDate" value="<fmt:formatDate value="${psiTransportPayment.sureDate}" pattern="yyyyMM" />" class="input-small"/>&nbsp;-&nbsp;
		<input style="width: 80px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});"  readonly="readonly"  class="Wdate" type="text" id="updateDate" name="updateDate" value="<fmt:formatDate value="${psiTransportPayment.updateDate}" pattern="yyyyMM" />" class="input-small"/>
	  <!--   <input id="btnSubmit" class="btn btn-primary" type="button" value="Export"/> -->
	</form>
	<c:set var="total1" value="0" />
	<c:set var="total2" value="0" />
	<c:set var="total3" value="0" />
	<c:set var="total4" value="0" />
	
	<table id="KGS" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">KGS</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${byMonthTransport}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total/KG</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['eu']['0'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['eu']['1'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['eu']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		       <c:set var="total1" value="${euTotal }" />
		     </tr>
		     
		     
		      <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['US']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['US']['0'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['US']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${byMonthTransport[moneyTitle.key]['US']['1'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['US']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['US']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		        <c:set var="total2" value="${euTotal }" />
		     </tr>
		     
		      <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">JP</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['jp']['0'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['jp']['1'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['jp']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td  style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${euTotal }"  maxFractionDigits="1"/></td>
		        <c:set var="total3" value="${euTotal }" />
		     </tr>
		     <tr style="background-color:#cccccc">
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">AE_Total/KG</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="total" value="${total+byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['jp']['0']}" />
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['jp']['0'] }"  maxFractionDigits="1"/></td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${total }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		      <tr style="background-color:#cccccc">
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">OE_Total/KG</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="total" value="${total+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['jp']['1']}" />
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['jp']['1'] }"  maxFractionDigits="1"/></td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${total }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		       <tr style="background-color:#cccccc">
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">EX_Total/KG</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="total" value="${total+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['2']}" />
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['2'] }"  maxFractionDigits="1"/></td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${total }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr style="background-color:#cccccc">
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">Total/KG</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="total" value="${total+byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }"  maxFractionDigits="1"/></td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${total }"  maxFractionDigits="1"/></td>
		          <c:set var="total4" value="${total }" />
		     </tr>
		</tbody>
	</table>
	<br/>
	<table id="percent" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">%</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${byMonthTransport}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total/%</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='3' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		              <c:if test="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']!=0 }">
		                <fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['eu']['0']/(byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2'])}"  maxFractionDigits="0"/>
		              </c:if>
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${total1!=0 }">
		           <fmt:formatNumber type="percent"  value="${euTotal/total1 }"  maxFractionDigits="0"/>
		         </c:if>  
		       </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;"> <c:if test="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']!=0 }">
		            <fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['eu']['1']/(byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2'])}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${total1!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total1 }"  maxFractionDigits="0"/></c:if>
		        </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']!=0 }">
			             <fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['eu']['2']/(byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2'])}"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		          <c:if test="${total1!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total1 }"  maxFractionDigits="0"/></c:if>
		        </td>
		     </tr>
		    <%--  <tr>
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="100"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="100"  maxFractionDigits="1"/></td>
		     </tr> --%>
		     
		     <tr>
		       <td rowspan='3' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['US']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		              <c:if test="${byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']!=0 }"><fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['US']['0']/(byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2'])}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total2!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total2 }"  maxFractionDigits="0"/></c:if></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['US']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;"><c:if test="${byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']!=0 }"><fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['US']['1']/(byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2'])}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total2!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total2 }"  maxFractionDigits="0"/></c:if></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		            <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['US']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><c:if test="${byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']!=0 }"><fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['US']['2']/(byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2'])}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total2!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total2 }"  maxFractionDigits="0"/></c:if></td>
		     </tr>
		     <%-- <tr>
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="100"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="100"  maxFractionDigits="1"/></td>
		     </tr> --%>
		     
		     <tr>
		       <td rowspan='3' style="text-align: center;vertical-align: middle;">JP</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;"><c:if test="${byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2']!=0 }"><fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['jp']['0']/(byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'])}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total3!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total3 }"  maxFractionDigits="0"/></c:if></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;"><c:if test="${byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2']!=0 }"><fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['jp']['1']/(byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'])}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total3!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total3 }"  maxFractionDigits="0"/></c:if></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		            <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><c:if test="${byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2']!=0 }"><fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['jp']['2']/(byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'])}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total3!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total3 }"  maxFractionDigits="0"/></c:if></td>
		     </tr>
		     <%-- <tr>
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="100"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="100"  maxFractionDigits="1"/></td>
		     </tr>  --%>
		     
		     <tr style="background-color:#cccccc">
		       <td rowspan='3' style="text-align: center;vertical-align: middle;">Total</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['US']['0'] }" />
		           <c:set var="totalAE" value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['jp']['0'] }" />
		           <c:set var="totalAll" value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><c:if test="${totalAll!=0 }"><fmt:formatNumber type="percent"  value="${totalAE/totalAll}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total4!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total4 }"  maxFractionDigits="0"/></c:if></td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['US']['1'] }" />
		           <c:set var="totalAE" value="${byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['jp']['1'] }" />
		           <c:set var="totalAll" value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${totalAll!=0 }"><fmt:formatNumber type="percent"  value="${totalAE/totalAll}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total4!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total4 }"  maxFractionDigits="0"/></c:if></td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['2']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['US']['2'] }" />
		           <c:set var="totalAE" value="${byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
		           <c:set var="totalAll" value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><c:if test="${totalAll!=0 }"><fmt:formatNumber type="percent"  value="${totalAE/totalAll}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total4!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total4 }"  maxFractionDigits="0"/></c:if></td>
		     </tr>
		     <%-- <tr>
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="100"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber pattern="#######.#" value="100"  maxFractionDigits="1"/></td>
		     </tr>    --%>   
		</tbody>
	</table>
	
</body>
</html>
