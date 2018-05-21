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
			
			$("#byMonth").click(function(){
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/exportAllTransportInfo");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/byMonthTransport");
			});
			
			$("#byTotal").click(function(){
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/exportTotalTransportInfo");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/byMonthTransport");
			});
			
			$("#productId").on("change",function(){
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/byMonthTransport");   
				$("#searchForm").submit();     
			})
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
	  <li class="active"><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransport">运输分析(理诚)</a></li>
	  <li ><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransport2">运输分析(排除新品)(理诚)</a></li>
	  <li ><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransportOffLine">线下运输分析(理诚)</a></li>
	</ul>
	<form id="searchForm" action="${ctx}/psi/lcPsiTransportPayment/byMonthTransport" method="post"  class="breadcrumb form-search" cssStyle="height: 80px;">
	   &nbsp;&nbsp;&nbsp;&nbsp;统计时间:
		<input style="width: 80px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});"  readonly="readonly"  class="Wdate" type="text" id="sureDate" name="sureDate" value="<fmt:formatDate value="${psiTransportPayment.sureDate}" pattern="yyyyMM" />" class="input-small"/>&nbsp;-&nbsp;
		<input style="width: 80px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});"  readonly="readonly"  class="Wdate" type="text" id="updateDate" name="updateDate" value="<fmt:formatDate value="${psiTransportPayment.updateDate}" pattern="yyyyMM" />" class="input-small"/>
	    &nbsp;&nbsp;&nbsp;&nbsp;产品:
	    <select id="productId" name="productName" style="width:220px">
    		<option value="">全部</option>
	    	<c:forEach items="${productList}" var="proEntry">
	    		<option value="${proEntry}" ${proEntry eq productName?'selected':'' }>${proEntry}</option>
	    	</c:forEach>
	    </select>
	     &nbsp;&nbsp;&nbsp;&nbsp;
	   <!--  <input id="btnSubmit" class="btn btn-primary" type="button" value="Export"/> -->
	      <div class="btn-group">
						   <button type="button" class="btn btn-primary">Export</button>
						   <button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" >
						      <li><a id="btnSubmit">页面数据</a></li>
						      <li><a id="byMonth">分月运输统计</a></li>
						      <li><a id="byTotal">运输统计</a></li>
						   </ul>
		 </div>
	</form>
	<c:set var="total1" value="0" />
	<c:set var="total2" value="0" />
	<c:set var="total3" value="0" />
	<c:set var="total4" value="0" />
	<table id="Money" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">总额</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total/RMB</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='5' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['eu']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['eu']['0'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['eu']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['eu']['1'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['eu']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['eu']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">TR</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['eu']['3'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['eu']['3'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['eu']['0']+byMonthTransportMoney[moneyTitle.key]['eu']['1']+byMonthTransportMoney[moneyTitle.key]['eu']['2']+byMonthTransportMoney[moneyTitle.key]['eu']['3'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${byMonthTransportMoney[moneyTitle.key]['eu']['0']+byMonthTransportMoney[moneyTitle.key]['eu']['1']+byMonthTransportMoney[moneyTitle.key]['eu']['2']+byMonthTransportMoney[moneyTitle.key]['eu']['3'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     
		      <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['US']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['US']['0'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['US']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${byMonthTransportMoney[moneyTitle.key]['US']['1'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['US']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['US']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['US']['0']+byMonthTransportMoney[moneyTitle.key]['US']['1']+byMonthTransportMoney[moneyTitle.key]['US']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['US']['0']+byMonthTransportMoney[moneyTitle.key]['US']['1']+byMonthTransportMoney[moneyTitle.key]['US']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		      <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">JP</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['jp']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['jp']['0'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['jp']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['jp']['1'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['jp']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td  style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['jp']['0']+byMonthTransportMoney[moneyTitle.key]['jp']['1']+byMonthTransportMoney[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['jp']['0']+byMonthTransportMoney[moneyTitle.key]['jp']['1']+byMonthTransportMoney[moneyTitle.key]['jp']['2'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr style="background-color:#cccccc"> 
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">Total/RMB</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="total" value="${total+byMonthTransportMoney[moneyTitle.key]['eu']['0']+byMonthTransportMoney[moneyTitle.key]['eu']['1']+byMonthTransportMoney[moneyTitle.key]['eu']['2']+byMonthTransportMoney[moneyTitle.key]['eu']['3']+byMonthTransportMoney[moneyTitle.key]['US']['0']+byMonthTransportMoney[moneyTitle.key]['US']['1']+byMonthTransportMoney[moneyTitle.key]['US']['2']+byMonthTransportMoney[moneyTitle.key]['jp']['0']+byMonthTransportMoney[moneyTitle.key]['jp']['1']+byMonthTransportMoney[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['eu']['0']+byMonthTransportMoney[moneyTitle.key]['eu']['1']+byMonthTransportMoney[moneyTitle.key]['eu']['2']+byMonthTransportMoney[moneyTitle.key]['eu']['3']+byMonthTransportMoney[moneyTitle.key]['US']['0']+byMonthTransportMoney[moneyTitle.key]['US']['1']+byMonthTransportMoney[moneyTitle.key]['US']['2']+byMonthTransportMoney[moneyTitle.key]['jp']['0']+byMonthTransportMoney[moneyTitle.key]['jp']['1']+byMonthTransportMoney[moneyTitle.key]['jp']['2'] }"  maxFractionDigits="1"/></td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${total }"  maxFractionDigits="1"/></td>
		     </tr>
		</tbody>
	</table>
	<br/>
	
	
	<table id="UnitPrice" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">单价</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">RMB/KG</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='5' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['eu']['0'] }" />
		           <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['eu']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${tempQuantityMap[moneyTitle.key]['eu']['0']>0 }"> 
			              <fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['eu']['0']/tempQuantityMap[moneyTitle.key]['eu']['0'] }"  maxFractionDigits="1"/>
			           </c:if> 
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${euTotal1>0 }"> 
		           <fmt:formatNumber value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/>
		         </c:if>   
		       </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['eu']['1'] }" />
		            <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['eu']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['eu']['1']>0 }"> 
		              <fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['eu']['1']/tempQuantityMap[moneyTitle.key]['eu']['1'] }"  maxFractionDigits="1"/>
		             </c:if>  
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal1>0 }"><fmt:formatNumber value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/> </c:if>  </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['eu']['2'] }" />
		           <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['eu']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		               <c:if test="${tempQuantityMap[moneyTitle.key]['eu']['2']>0 }"> 
		                  <fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['eu']['2']/tempQuantityMap[moneyTitle.key]['eu']['2'] }"  maxFractionDigits="1"/>
		               </c:if>   
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal1>0 }"><fmt:formatNumber value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/></c:if> </td>
		     </tr>
		      <tr>
		       <td style="text-align: center;vertical-align: middle;">TR</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['eu']['3'] }" />
		           <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['eu']['3'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		               <c:if test="${tempQuantityMap[moneyTitle.key]['eu']['3']>0 }"> 
		                  <fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['eu']['3']/tempQuantityMap[moneyTitle.key]['eu']['3'] }"  maxFractionDigits="1"/>
		               </c:if>   
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal1>0 }"><fmt:formatNumber value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/></c:if> </td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['eu']['0']+byMonthTransportMoney[moneyTitle.key]['eu']['1']+byMonthTransportMoney[moneyTitle.key]['eu']['2']+byMonthTransportMoney[moneyTitle.key]['eu']['3'] }" />
		           <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['eu']['0']+tempQuantityMap[moneyTitle.key]['eu']['1']+tempQuantityMap[moneyTitle.key]['eu']['2']+tempQuantityMap[moneyTitle.key]['eu']['3'] }" />
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['eu']['0']+tempQuantityMap[moneyTitle.key]['eu']['1']+tempQuantityMap[moneyTitle.key]['eu']['2']+tempQuantityMap[moneyTitle.key]['eu']['3']>0 }"> 
		             <fmt:formatNumber value="${(byMonthTransportMoney[moneyTitle.key]['eu']['0']+byMonthTransportMoney[moneyTitle.key]['eu']['1']+byMonthTransportMoney[moneyTitle.key]['eu']['2']+byMonthTransportMoney[moneyTitle.key]['eu']['3'])/(tempQuantityMap[moneyTitle.key]['eu']['0']+tempQuantityMap[moneyTitle.key]['eu']['1']+tempQuantityMap[moneyTitle.key]['eu']['2']+tempQuantityMap[moneyTitle.key]['eu']['3']) }"  maxFractionDigits="1"/>
		            </c:if>   
		            </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal1>0 }"><fmt:formatNumber  value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/></c:if></td>
		     </tr>
		     
		     
		     <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['US']['0'] }" />
		           <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['US']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${tempQuantityMap[moneyTitle.key]['US']['0']>0 }"> 
			              <fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['US']['0']/tempQuantityMap[moneyTitle.key]['US']['0'] }"  maxFractionDigits="1"/>
			           </c:if> 
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${euTotal1>0 }"> 
		           <fmt:formatNumber value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/>
		         </c:if>   
		       </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['US']['1'] }" />
		            <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['US']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['US']['1']>0 }"> 
		              <fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['US']['1']/tempQuantityMap[moneyTitle.key]['US']['1'] }"  maxFractionDigits="1"/>
		             </c:if>  
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal1>0 }"><fmt:formatNumber value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/> </c:if>  </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['US']['2'] }" />
		           <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['US']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		               <c:if test="${tempQuantityMap[moneyTitle.key]['US']['2']>0 }"> 
		                  <fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['US']['2']/tempQuantityMap[moneyTitle.key]['US']['2'] }"  maxFractionDigits="1"/>
		               </c:if>   
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal1>0 }"><fmt:formatNumber value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/></c:if> </td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['US']['0']+byMonthTransportMoney[moneyTitle.key]['US']['1']+byMonthTransportMoney[moneyTitle.key]['US']['2'] }" />
		           <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['US']['0']+tempQuantityMap[moneyTitle.key]['US']['1']+tempQuantityMap[moneyTitle.key]['US']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['US']['0']+tempQuantityMap[moneyTitle.key]['US']['1']+tempQuantityMap[moneyTitle.key]['US']['2']>0 }"> 
		             <fmt:formatNumber value="${(byMonthTransportMoney[moneyTitle.key]['US']['0']+byMonthTransportMoney[moneyTitle.key]['US']['1']+byMonthTransportMoney[moneyTitle.key]['US']['2'])/(tempQuantityMap[moneyTitle.key]['US']['0']+tempQuantityMap[moneyTitle.key]['US']['1']+tempQuantityMap[moneyTitle.key]['US']['2']) }"  maxFractionDigits="1"/>
		            </c:if>   
		            </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal1>0 }"><fmt:formatNumber  value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/></c:if></td>
		     </tr>
		     
		       <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">JP</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['jp']['0'] }" />
		           <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['jp']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${tempQuantityMap[moneyTitle.key]['jp']['0']>0 }"> 
			              <fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['jp']['0']/tempQuantityMap[moneyTitle.key]['jp']['0'] }"  maxFractionDigits="1"/>
			           </c:if> 
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${euTotal1>0 }"> 
		           <fmt:formatNumber value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/>
		         </c:if>   
		       </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['jp']['1'] }" />
		            <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['jp']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['jp']['1']>0 }"> 
		              <fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['jp']['1']/tempQuantityMap[moneyTitle.key]['jp']['1'] }"  maxFractionDigits="1"/>
		             </c:if>  
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal1>0 }"><fmt:formatNumber value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/> </c:if>  </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['jp']['2'] }" />
		           <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		               <c:if test="${tempQuantityMap[moneyTitle.key]['jp']['2']>0 }"> 
		                  <fmt:formatNumber  value="${byMonthTransportMoney[moneyTitle.key]['jp']['2']/tempQuantityMap[moneyTitle.key]['jp']['2'] }"  maxFractionDigits="1"/>
		               </c:if>   
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal1>0 }"><fmt:formatNumber value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/></c:if> </td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:set var="euTotal1" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransportMoney[moneyTitle.key]['jp']['0']+byMonthTransportMoney[moneyTitle.key]['jp']['1']+byMonthTransportMoney[moneyTitle.key]['jp']['2'] }" />
		           <c:set var="euTotal1" value="${euTotal1+tempQuantityMap[moneyTitle.key]['jp']['0']+tempQuantityMap[moneyTitle.key]['jp']['1']+tempQuantityMap[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['jp']['0']+tempQuantityMap[moneyTitle.key]['jp']['1']+tempQuantityMap[moneyTitle.key]['jp']['2']>0 }"> 
		             <fmt:formatNumber value="${(byMonthTransportMoney[moneyTitle.key]['jp']['0']+byMonthTransportMoney[moneyTitle.key]['jp']['1']+byMonthTransportMoney[moneyTitle.key]['jp']['2'])/(tempQuantityMap[moneyTitle.key]['jp']['0']+tempQuantityMap[moneyTitle.key]['jp']['1']+tempQuantityMap[moneyTitle.key]['jp']['2']) }"  maxFractionDigits="1"/>
		            </c:if>   
		            </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal1>0 }"><fmt:formatNumber  value="${euTotal*1.0/euTotal1 }"  maxFractionDigits="1"/></c:if></td>
		     </tr>
		     
		     <tr style="background-color:#cccccc">
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">RMB/KG</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:set var="total" value="0" />
		         <c:set var="total0" value="0" />
		         <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="total" value="${total+byMonthTransportMoney[moneyTitle.key]['eu']['0']+byMonthTransportMoney[moneyTitle.key]['eu']['1']+byMonthTransportMoney[moneyTitle.key]['eu']['2']+byMonthTransportMoney[moneyTitle.key]['eu']['3']+byMonthTransportMoney[moneyTitle.key]['US']['0']+byMonthTransportMoney[moneyTitle.key]['US']['1']+byMonthTransportMoney[moneyTitle.key]['US']['2']+byMonthTransportMoney[moneyTitle.key]['jp']['0']+byMonthTransportMoney[moneyTitle.key]['jp']['1']+byMonthTransportMoney[moneyTitle.key]['jp']['2'] }" />
		           <c:set var="total0" value="${total0+tempQuantityMap[moneyTitle.key]['eu']['0']+tempQuantityMap[moneyTitle.key]['eu']['1']+tempQuantityMap[moneyTitle.key]['eu']['2']+tempQuantityMap[moneyTitle.key]['eu']['3']+tempQuantityMap[moneyTitle.key]['US']['0']+tempQuantityMap[moneyTitle.key]['US']['1']+tempQuantityMap[moneyTitle.key]['US']['2']+tempQuantityMap[moneyTitle.key]['jp']['0']+tempQuantityMap[moneyTitle.key]['jp']['1']+tempQuantityMap[moneyTitle.key]['jp']['2'] }" />
		          
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		            <c:if test="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2']>0 }">
		              <fmt:formatNumber  value="${(byMonthTransportMoney[moneyTitle.key]['eu']['0']+byMonthTransportMoney[moneyTitle.key]['eu']['1']+byMonthTransportMoney[moneyTitle.key]['eu']['2']+byMonthTransportMoney[moneyTitle.key]['eu']['3']+byMonthTransportMoney[moneyTitle.key]['US']['0']+byMonthTransportMoney[moneyTitle.key]['US']['1']+byMonthTransportMoney[moneyTitle.key]['US']['2']+byMonthTransportMoney[moneyTitle.key]['jp']['0']+byMonthTransportMoney[moneyTitle.key]['jp']['1']+byMonthTransportMoney[moneyTitle.key]['jp']['2'])/(tempQuantityMap[moneyTitle.key]['eu']['0']+tempQuantityMap[moneyTitle.key]['eu']['1']+tempQuantityMap[moneyTitle.key]['eu']['2']+tempQuantityMap[moneyTitle.key]['eu']['3']+tempQuantityMap[moneyTitle.key]['US']['0']+tempQuantityMap[moneyTitle.key]['US']['1']+tempQuantityMap[moneyTitle.key]['US']['2']+tempQuantityMap[moneyTitle.key]['jp']['0']+tempQuantityMap[moneyTitle.key]['jp']['1']+tempQuantityMap[moneyTitle.key]['jp']['2']) }"  maxFractionDigits="1"/>
		            </c:if>
		           </td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total0>0 }"><fmt:formatNumber value="${total*1.0/total0 }"  maxFractionDigits="1"/></c:if></td>
		     </tr>
		</tbody>
	</table>
	<br/>
	
	
	<table id="totalQuantity" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">数量</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
				  <th style="text-align: center;vertical-align: middle;">${moneyTitle.key}</th>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='5' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['eu']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${tempQuantityMap[moneyTitle.key]['eu']['0']>0 }"> 
			             ${tempQuantityMap[moneyTitle.key]['eu']['0'] }
			           </c:if> 
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${euTotal>0 }"> 
		           ${euTotal}
		         </c:if>   
		       </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		            <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['eu']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['eu']['1']>0 }"> 
		              ${tempQuantityMap[moneyTitle.key]['eu']['1'] }
		             </c:if>  
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal>0 }">${euTotal }</c:if></td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['eu']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		               <c:if test="${tempQuantityMap[moneyTitle.key]['eu']['2']>0 }"> 
		                 ${tempQuantityMap[moneyTitle.key]['eu']['2'] }
		               </c:if>   
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal>0 }">${euTotal }</c:if> </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">TR</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['eu']['3'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		               <c:if test="${tempQuantityMap[moneyTitle.key]['eu']['3']>0 }"> 
		                 ${tempQuantityMap[moneyTitle.key]['eu']['3'] }
		               </c:if>   
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal>0 }">${euTotal }</c:if> </td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['eu']['0']+tempQuantityMap[moneyTitle.key]['eu']['1']+tempQuantityMap[moneyTitle.key]['eu']['2']+tempQuantityMap[moneyTitle.key]['eu']['3'] }" />
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['eu']['0']+tempQuantityMap[moneyTitle.key]['eu']['1']+tempQuantityMap[moneyTitle.key]['eu']['2']+tempQuantityMap[moneyTitle.key]['eu']['3']>0 }"> 
		             ${(tempQuantityMap[moneyTitle.key]['eu']['0']+tempQuantityMap[moneyTitle.key]['eu']['1']+tempQuantityMap[moneyTitle.key]['eu']['2']+tempQuantityMap[moneyTitle.key]['eu']['3']) }
		            </c:if>   
		            </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal>0 }">${euTotal }</c:if></td>
		     </tr>
		     
		     
		     <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['US']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${tempQuantityMap[moneyTitle.key]['US']['0']>0 }"> 
			             ${tempQuantityMap[moneyTitle.key]['US']['0'] }
			           </c:if> 
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${euTotal>0 }"> 
		           ${euTotal}
		         </c:if>   
		       </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		            <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['US']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['US']['1']>0 }"> 
		              ${tempQuantityMap[moneyTitle.key]['US']['1'] }
		             </c:if>  
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal>0 }">${euTotal } </c:if>  </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['US']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		               <c:if test="${tempQuantityMap[moneyTitle.key]['US']['2']>0 }"> 
		                 ${tempQuantityMap[moneyTitle.key]['US']['2'] }
		               </c:if>   
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal>0 }">${euTotal }</c:if> </td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['US']['0']+tempQuantityMap[moneyTitle.key]['US']['1']+tempQuantityMap[moneyTitle.key]['US']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['US']['0']+tempQuantityMap[moneyTitle.key]['US']['1']+tempQuantityMap[moneyTitle.key]['US']['2']>0 }"> 
		             ${(tempQuantityMap[moneyTitle.key]['US']['0']+tempQuantityMap[moneyTitle.key]['US']['1']+tempQuantityMap[moneyTitle.key]['US']['2']) }
		            </c:if>   
		            </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal>0 }">${euTotal }</c:if></td>
		     </tr>
		     
		       <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">JP</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal1+tempQuantityMap[moneyTitle.key]['jp']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${tempQuantityMap[moneyTitle.key]['jp']['0']>0 }"> 
			              ${tempQuantityMap[moneyTitle.key]['jp']['0'] }
			           </c:if> 
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${euTotal>0 }"> 
		           ${euTotal }
		         </c:if>   
		       </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		            <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['jp']['1'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['jp']['1']>0 }"> 
		              ${tempQuantityMap[moneyTitle.key]['jp']['1'] }
		             </c:if>  
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal>0 }">${euTotal}</c:if>  </td>
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		               <c:if test="${tempQuantityMap[moneyTitle.key]['jp']['2']>0 }"> 
		                  ${tempQuantityMap[moneyTitle.key]['jp']['2'] }
		               </c:if>   
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal>0 }">${euTotal}</c:if> </td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+tempQuantityMap[moneyTitle.key]['jp']['0']+tempQuantityMap[moneyTitle.key]['jp']['1']+tempQuantityMap[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${tempQuantityMap[moneyTitle.key]['jp']['0']+tempQuantityMap[moneyTitle.key]['jp']['1']+tempQuantityMap[moneyTitle.key]['jp']['2']>0 }"> 
		              ${(tempQuantityMap[moneyTitle.key]['jp']['0']+tempQuantityMap[moneyTitle.key]['jp']['1']+tempQuantityMap[moneyTitle.key]['jp']['2']) }
		            </c:if>   
		            </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${euTotal>0 }">${euTotal}</c:if></td>
		     </tr>
		     
		     <tr style="background-color:#cccccc">
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">总数量</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthTransportMoney}" var="moneyTitle">
		           <c:set var="total" value="${total+tempQuantityMap[moneyTitle.key]['eu']['0']+tempQuantityMap[moneyTitle.key]['eu']['1']+tempQuantityMap[moneyTitle.key]['eu']['2']+tempQuantityMap[moneyTitle.key]['eu']['3']+tempQuantityMap[moneyTitle.key]['US']['0']+tempQuantityMap[moneyTitle.key]['US']['1']+tempQuantityMap[moneyTitle.key]['US']['2']+tempQuantityMap[moneyTitle.key]['jp']['0']+tempQuantityMap[moneyTitle.key]['jp']['1']+tempQuantityMap[moneyTitle.key]['jp']['2'] }" />
		          
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		            <c:if test="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2']>0 }">
		              ${(tempQuantityMap[moneyTitle.key]['eu']['0']+tempQuantityMap[moneyTitle.key]['eu']['1']+tempQuantityMap[moneyTitle.key]['eu']['2']+tempQuantityMap[moneyTitle.key]['eu']['3']+tempQuantityMap[moneyTitle.key]['US']['0']+tempQuantityMap[moneyTitle.key]['US']['1']+tempQuantityMap[moneyTitle.key]['US']['2']+tempQuantityMap[moneyTitle.key]['jp']['0']+tempQuantityMap[moneyTitle.key]['jp']['1']+tempQuantityMap[moneyTitle.key]['jp']['2']) }
		            </c:if>
		           </td>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total>0 }">${total }</c:if></td>
		     </tr>
		</tbody>
	</table>
	<br/>
	
	
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
		       <td rowspan='5' style="text-align: center;vertical-align: middle;">EU</td>
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
		      <tr>
		       <td style="text-align: center;vertical-align: middle;">TR</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['3'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['eu']['3'] }"  maxFractionDigits="1"/></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${euTotal }"  maxFractionDigits="1"/></td>
		     </tr>
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3'] }" />
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3'] }"  maxFractionDigits="1"/></td>
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
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">Total/KG</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:set var="total" value="0" />
		         <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="total" value="${total+byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }"  maxFractionDigits="1"/></td>
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
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['0'] }" />
		           <td style="text-align: center;vertical-align: middle;">
		              <c:if test="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']!=0 }">
		                <fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['eu']['0']/(byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3'])}"  maxFractionDigits="0"/>
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
		           <td style="text-align: center;vertical-align: middle;"> <c:if test="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']!=0 }">
		            <fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['eu']['1']/(byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3'])}"  maxFractionDigits="0"/></c:if></td>
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
			           <c:if test="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']!=0 }">
			             <fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['eu']['2']/(byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3'])}"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		          <c:if test="${total1!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total1 }"  maxFractionDigits="0"/></c:if>
		        </td>
		     </tr>
		      <tr>
		       <td style="text-align: center;vertical-align: middle;">TR</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['3'] }" />
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']!=0 }">
			             <fmt:formatNumber type="percent"  value="${byMonthTransport[moneyTitle.key]['eu']['3']/(byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3'])}"  maxFractionDigits="0"/>
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
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">Total</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['US']['0'] }" />
		           <c:set var="totalAE" value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['jp']['0'] }" />
		           <c:set var="totalAll" value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
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
		           <c:set var="totalAll" value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
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
		           <c:set var="totalAll" value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
		           <td style="text-align: center;vertical-align: middle;"><c:if test="${totalAll!=0 }"><fmt:formatNumber type="percent"  value="${totalAE/totalAll}"  maxFractionDigits="0"/></c:if></td>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><c:if test="${total4!=0 }"><fmt:formatNumber type="percent"  value="${euTotal/total4 }"  maxFractionDigits="0"/></c:if></td>
		     </tr>
		      <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">TR</td>
		       <c:set var="euTotal" value="0" />
		       <c:forEach items="${byMonthTransport}" var="moneyTitle">
		           <c:set var="euTotal" value="${euTotal+byMonthTransport[moneyTitle.key]['eu']['3']}" />
		           <c:set var="totalAE" value="${byMonthTransport[moneyTitle.key]['eu']['3']}" />
		           <c:set var="totalAll" value="${byMonthTransport[moneyTitle.key]['eu']['0']+byMonthTransport[moneyTitle.key]['eu']['1']+byMonthTransport[moneyTitle.key]['eu']['2']+byMonthTransport[moneyTitle.key]['eu']['3']+byMonthTransport[moneyTitle.key]['US']['0']+byMonthTransport[moneyTitle.key]['US']['1']+byMonthTransport[moneyTitle.key]['US']['2']+byMonthTransport[moneyTitle.key]['jp']['0']+byMonthTransport[moneyTitle.key]['jp']['1']+byMonthTransport[moneyTitle.key]['jp']['2'] }" />
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
