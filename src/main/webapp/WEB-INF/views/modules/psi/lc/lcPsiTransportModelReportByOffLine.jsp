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
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/exportOffLineTransport");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/psi/lcPsiTransportPayment/byMonthTransportOffLine");
			});
			
			
			$("#productId").on("change",function(){
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
	  <li ><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransport">运输分析(理诚)</a></li>
	  <li ><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransport2">运输分析(排除新品)(理诚)</a></li>
	  <li class="active"><a href="${ctx}/psi/lcPsiTransportPayment/byMonthTransportOffLine">线下运输分析(理诚)</a></li>
	</ul>
	<form id="searchForm" action="${ctx}/psi/lcPsiTransportPayment/byMonthTransportOffLine" method="post"  class="breadcrumb form-search" cssStyle="height: 80px;">
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
	     <input id="btnSubmit" class="btn btn-primary" type="button" value="Export"/> 
	     <!--  <div class="btn-group">
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
		 </div> -->
	</form>
	
	<table id="Money" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">总额</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${offLineMap}" var="monthTitle">
				  <c:if test="${'total' ne monthTitle.key }">
				    <th style="text-align: center;vertical-align: middle;">${monthTitle.key}</th>
				  </c:if>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total/RMB</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['0'].money }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['0'].money }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['1'].money }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['1'].money }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['2'].money }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['2'].money }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		      <c:forEach items="${offLineMap}" var="monthTitle">
		       <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${offLineMap[monthTitle.key]['EU']['total'].money}"  maxFractionDigits="1"/></td>
		       </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${offLineMap['total']['EU']['total'].money }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		      <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['0'].money }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['US']['0'].money }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['1'].money }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['US']['1'].money }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['2'].money }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['US']['2'].money }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		      <c:forEach items="${offLineMap}" var="monthTitle">
		       <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${offLineMap[monthTitle.key]['US']['total'].money}"  maxFractionDigits="1"/></td>
		       </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${offLineMap['total']['US']['total'].money }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr style="background-color:#cccccc"> 
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">Total/RMB</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:forEach items="${offLineMap}" var="monthTitle">
		          <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['total'].money+offLineMap[monthTitle.key]['US']['total'].money }"  maxFractionDigits="1"/></td>
		          </c:if>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['total'].money+offLineMap['total']['US']['total'].money }"  maxFractionDigits="1"/></td>
		     </tr>
		</tbody>
	</table>
	
	<c:if test="${not empty productName }">
	<table id="UnitPrice1" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">单价</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${offLineMap}" var="monthTitle">
				  <c:if test="${'total' ne monthTitle.key }">
				    <th style="text-align: center;vertical-align: middle;">${monthTitle.key}</th>
				  </c:if>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">RMB/KG</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['EU']['0'].quantity> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['0'].money/offLineMap[monthTitle.key]['EU']['0'].quantity }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['EU']['0'].quantity> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['EU']['0'].money/offLineMap['total']['EU']['0'].quantity }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['EU']['1'].quantity> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['1'].money/offLineMap[monthTitle.key]['EU']['1'].quantity }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['EU']['1'].quantity> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['EU']['1'].money/offLineMap['total']['EU']['1'].quantity }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['EU']['2'].quantity> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['2'].money/offLineMap[monthTitle.key]['EU']['2'].quantity }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['EU']['2'].quantity> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['EU']['2'].money/offLineMap['total']['EU']['2'].quantity }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		      <c:forEach items="${offLineMap}" var="monthTitle">
		       <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${offLineMap[monthTitle.key]['EU']['total'].quantity> 0}">
		               <fmt:formatNumber value="${offLineMap[monthTitle.key]['EU']['total'].money/offLineMap[monthTitle.key]['EU']['total'].quantity}"  maxFractionDigits="1"/>
		            </c:if> 
		           </td>
		       </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${offLineMap['total']['EU']['total'].quantity> 0}">
		           <fmt:formatNumber  value="${offLineMap['total']['EU']['total'].money/offLineMap['total']['EU']['total'].quantity }"  maxFractionDigits="1"/>
		         </c:if>
		       </td>
		     </tr>
		     
		     
		    <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['US']['0'].quantity> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['0'].money/offLineMap[monthTitle.key]['US']['0'].quantity }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['US']['0'].quantity> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['US']['0'].money/offLineMap['total']['US']['0'].quantity }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['US']['1'].quantity> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['1'].money/offLineMap[monthTitle.key]['US']['1'].quantity }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['US']['1'].quantity> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['US']['1'].money/offLineMap['total']['US']['1'].quantity }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['US']['2'].quantity> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['2'].money/offLineMap[monthTitle.key]['US']['2'].quantity }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['US']['2'].quantity> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['US']['2'].money/offLineMap['total']['US']['2'].quantity }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		      <c:forEach items="${offLineMap}" var="monthTitle">
		       <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${offLineMap[monthTitle.key]['US']['total'].quantity> 0}">
		               <fmt:formatNumber value="${offLineMap[monthTitle.key]['US']['total'].money/offLineMap[monthTitle.key]['US']['total'].quantity}"  maxFractionDigits="1"/>
		            </c:if> 
		           </td>
		       </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${offLineMap['total']['US']['total'].quantity> 0}">
		           <fmt:formatNumber  value="${offLineMap['total']['US']['total'].money/offLineMap['total']['US']['total'].quantity }"  maxFractionDigits="1"/>
		         </c:if>
		       </td>
		     </tr> 
		    
		     
		     <tr style="background-color:#cccccc"> 
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">Total/RMB</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:forEach items="${offLineMap}" var="monthTitle">
		          <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		            <c:if test="${offLineMap[monthTitle.key]['EU']['total'].quantity+offLineMap[monthTitle.key]['US']['total'].quantity> 0}">
		              <fmt:formatNumber  value="${(offLineMap[monthTitle.key]['EU']['total'].money+offLineMap[monthTitle.key]['US']['total'].money)/(offLineMap[monthTitle.key]['EU']['total'].quantity+offLineMap[monthTitle.key]['US']['total'].quantity) }"  maxFractionDigits="1"/>
		            </c:if> 
		           </td>
		          </c:if>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		          <c:if test="${offLineMap['total']['EU']['total'].quantity+offLineMap['total']['US']['total'].quantity> 0}">
		             <fmt:formatNumber value="${(offLineMap['total']['EU']['total'].money+offLineMap['total']['US']['total'].money)/(offLineMap['total']['EU']['total'].quantity+offLineMap['total']['US']['total'].quantity) }"  maxFractionDigits="1"/>
		          </c:if> 
		         </td>
		         
		     </tr>
		</tbody>
	</table>
	
	</c:if>
	
	
	
	<c:if test="${empty productName }">
	<table id="UnitPrice2" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">单价</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${offLineMap}" var="monthTitle">
				  <c:if test="${'total' ne monthTitle.key }">
				    <th style="text-align: center;vertical-align: middle;">${monthTitle.key}</th>
				  </c:if>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">RMB/KG</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['EU']['0'].weight> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['0'].money/offLineMap[monthTitle.key]['EU']['0'].weight }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['EU']['0'].weight> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['EU']['0'].money/offLineMap['total']['EU']['0'].weight }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['EU']['1'].weight> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['1'].money/offLineMap[monthTitle.key]['EU']['1'].weight }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['EU']['1'].weight> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['EU']['1'].money/offLineMap['total']['EU']['1'].weight }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['EU']['2'].weight> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['2'].money/offLineMap[monthTitle.key]['EU']['2'].weight }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['EU']['2'].weight> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['EU']['2'].money/offLineMap['total']['EU']['2'].weight }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		      <c:forEach items="${offLineMap}" var="monthTitle">
		       <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${offLineMap[monthTitle.key]['EU']['total'].weight> 0}">
		               <fmt:formatNumber value="${offLineMap[monthTitle.key]['EU']['total'].money/offLineMap[monthTitle.key]['EU']['total'].weight}"  maxFractionDigits="1"/>
		            </c:if> 
		           </td>
		       </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${offLineMap['total']['EU']['total'].weight> 0}">
		           <fmt:formatNumber  value="${offLineMap['total']['EU']['total'].money/offLineMap['total']['EU']['total'].weight }"  maxFractionDigits="1"/>
		         </c:if>
		       </td>
		     </tr>
		     
		     
		    <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['US']['0'].weight> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['0'].money/offLineMap[monthTitle.key]['US']['0'].weight }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['US']['0'].weight> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['US']['0'].money/offLineMap['total']['US']['0'].weight }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['US']['1'].weight> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['1'].money/offLineMap[monthTitle.key]['US']['1'].weight }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['US']['1'].weight> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['US']['1'].money/offLineMap['total']['US']['1'].weight }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		             <c:if test="${offLineMap[monthTitle.key]['US']['2'].weight> 0}">
		               <fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['2'].money/offLineMap[monthTitle.key]['US']['2'].weight }"  maxFractionDigits="1"/>
		             </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['US']['2'].weight> 0}">
		             <fmt:formatNumber value="${offLineMap['total']['US']['2'].money/offLineMap['total']['US']['2'].weight }"  maxFractionDigits="1"/>
		           </c:if>  
		       </td>
		     </tr>
		     
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		      <c:forEach items="${offLineMap}" var="monthTitle">
		       <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
		            <c:if test="${offLineMap[monthTitle.key]['US']['total'].weight> 0}">
		               <fmt:formatNumber value="${offLineMap[monthTitle.key]['US']['total'].money/offLineMap[monthTitle.key]['US']['total'].weight}"  maxFractionDigits="1"/>
		            </c:if> 
		           </td>
		       </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		         <c:if test="${offLineMap['total']['US']['total'].weight> 0}">
		           <fmt:formatNumber  value="${offLineMap['total']['US']['total'].money/offLineMap['total']['US']['total'].weight }"  maxFractionDigits="1"/>
		         </c:if>
		       </td>
		     </tr> 
		    
		     
		     <tr style="background-color:#cccccc"> 
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">Total/RMB</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:forEach items="${offLineMap}" var="monthTitle">
		          <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		            <c:if test="${offLineMap[monthTitle.key]['EU']['total'].weight+offLineMap[monthTitle.key]['US']['total'].weight> 0}">
		              <fmt:formatNumber  value="${(offLineMap[monthTitle.key]['EU']['total'].money+offLineMap[monthTitle.key]['US']['total'].money)/(offLineMap[monthTitle.key]['EU']['total'].weight+offLineMap[monthTitle.key]['US']['total'].weight) }"  maxFractionDigits="1"/>
		            </c:if> 
		           </td>
		          </c:if>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		          <c:if test="${offLineMap['total']['EU']['total'].weight+offLineMap['total']['US']['total'].weight> 0}">
		             <fmt:formatNumber value="${(offLineMap['total']['EU']['total'].money+offLineMap['total']['US']['total'].money)/(offLineMap['total']['EU']['total'].weight+offLineMap['total']['US']['total'].weight) }"  maxFractionDigits="1"/>
		          </c:if> 
		         </td>
		         
		     </tr>
		</tbody>
	</table>
	
	</c:if>
	
	
	<table id="KGS" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">KGS</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${offLineMap}" var="monthTitle">
				  <c:if test="${'total' ne monthTitle.key }">
				    <th style="text-align: center;vertical-align: middle;">${monthTitle.key}</th>
				  </c:if>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total/KG</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['0'].weight }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['0'].weight }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['1'].weight }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['1'].weight }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['2'].weight }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['2'].weight }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		      <c:forEach items="${offLineMap}" var="monthTitle">
		       <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${offLineMap[monthTitle.key]['EU']['total'].weight}"  maxFractionDigits="1"/></td>
		       </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${offLineMap['total']['EU']['total'].weight }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		      <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['0'].weight }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['US']['0'].weight }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['1'].weight }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['US']['1'].weight }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['2'].weight }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['US']['2'].weight }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		      <c:forEach items="${offLineMap}" var="monthTitle">
		       <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${offLineMap[monthTitle.key]['US']['total'].weight}"  maxFractionDigits="1"/></td>
		       </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${offLineMap['total']['US']['total'].weight }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr style="background-color:#cccccc"> 
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">Total/RMB</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:forEach items="${offLineMap}" var="monthTitle">
		          <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['total'].weight+offLineMap[monthTitle.key]['US']['total'].weight }"  maxFractionDigits="1"/></td>
		          </c:if>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['total'].weight+offLineMap['total']['US']['total'].weight }"  maxFractionDigits="1"/></td>
		     </tr>
		</tbody>
	</table>
	
	
	<table id="quantity" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">数量</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${offLineMap}" var="monthTitle">
				  <c:if test="${'total' ne monthTitle.key }">
				    <th style="text-align: center;vertical-align: middle;">${monthTitle.key}</th>
				  </c:if>
				</c:forEach>
				<th style="text-align: center;vertical-align: middle;">Total</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['0'].quantity }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['0'].quantity }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['1'].quantity }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['1'].quantity }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['2'].quantity }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['2'].quantity }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		      <c:forEach items="${offLineMap}" var="monthTitle">
		       <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${offLineMap[monthTitle.key]['EU']['total'].quantity}"  maxFractionDigits="1"/></td>
		       </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${offLineMap['total']['EU']['total'].quantity }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		      <tr>
		       <td rowspan='4' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['0'].quantity }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['US']['0'].quantity }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['1'].quantity }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['US']['1'].quantity }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['US']['2'].quantity }"  maxFractionDigits="1"/></td>
		        </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['US']['2'].quantity }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     
		     <tr style="background-color:#cccccc">
		       <td style="text-align: center;vertical-align: middle;">Total</td>
		       <c:set var="euTotal" value="0" />
		      <c:forEach items="${offLineMap}" var="monthTitle">
		       <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${offLineMap[monthTitle.key]['US']['total'].quantity}"  maxFractionDigits="1"/></td>
		       </c:if>
		       </c:forEach>
		       <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${offLineMap['total']['US']['total'].quantity }"  maxFractionDigits="1"/></td>
		     </tr>
		     
		     <tr style="background-color:#cccccc"> 
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">Total/RMB</td>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"></td>
		         <c:forEach items="${offLineMap}" var="monthTitle">
		          <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber  value="${offLineMap[monthTitle.key]['EU']['total'].quantity+offLineMap[monthTitle.key]['US']['total'].quantity }"  maxFractionDigits="1"/></td>
		          </c:if>
		         </c:forEach>
		         <td style="text-align: center;vertical-align: middle;background-color:#cccccc;"><fmt:formatNumber value="${offLineMap['total']['EU']['total'].quantity+offLineMap['total']['US']['total'].quantity }"  maxFractionDigits="1"/></td>
		     </tr>
		</tbody>
	</table>
	
	
	<table id="percent" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:5%;">%</th>
				<th style="text-align: center;vertical-align: middle;width:5%;">model</th>
				<c:forEach items="${offLineMap}" var="monthTitle">
				  <c:if test="${'total' ne monthTitle.key }">
				    <th style="text-align: center;vertical-align: middle;">${monthTitle.key}</th>
				  </c:if>
				</c:forEach>
		         <th style="text-align: center;vertical-align: middle;">Total/%</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
		       <td rowspan='3' style="text-align: center;vertical-align: middle;">EU</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${offLineMap[monthTitle.key]['EU']['total'].weight>0 }">
			              <fmt:formatNumber type="percent" value="${offLineMap[monthTitle.key]['EU']['0'].weight/offLineMap[monthTitle.key]['EU']['total'].weight }"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['EU']['total'].weight>0 }">
		           <fmt:formatNumber type="percent"  value="${offLineMap['total']['EU']['0'].weight/offLineMap['total']['EU']['total'].weight }"  maxFractionDigits="0"/>
		         </c:if>  
		       </td>
		     </tr>
		     
		      <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${offLineMap[monthTitle.key]['EU']['total'].weight>0 }">
			              <fmt:formatNumber type="percent" value="${offLineMap[monthTitle.key]['EU']['1'].weight/offLineMap[monthTitle.key]['EU']['total'].weight }"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['EU']['total'].weight>0 }">
		           <fmt:formatNumber type="percent"  value="${offLineMap['total']['EU']['1'].weight/offLineMap['total']['EU']['total'].weight }"  maxFractionDigits="0"/>
		         </c:if>  
		       </td>
		     </tr>
		   <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${offLineMap[monthTitle.key]['EU']['total'].weight>0 }">
			              <fmt:formatNumber type="percent" value="${offLineMap[monthTitle.key]['EU']['2'].weight/offLineMap[monthTitle.key]['EU']['total'].weight }"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['EU']['total'].weight>0 }">
		           <fmt:formatNumber type="percent"  value="${offLineMap['total']['EU']['2'].weight/offLineMap['total']['EU']['total'].weight }"  maxFractionDigits="0"/>
		         </c:if>  
		       </td>
		     </tr>
		   
		     
		      <tr>
		       <td rowspan='3' style="text-align: center;vertical-align: middle;">US</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${offLineMap[monthTitle.key]['US']['total'].weight>0 }">
			              <fmt:formatNumber type="percent" value="${offLineMap[monthTitle.key]['US']['0'].weight/offLineMap[monthTitle.key]['US']['total'].weight }"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['US']['total'].weight>0 }">
		           <fmt:formatNumber type="percent"  value="${offLineMap['total']['US']['0'].weight/offLineMap['total']['US']['total'].weight }"  maxFractionDigits="0"/>
		         </c:if>  
		       </td>
		     </tr>
		     
		      <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${offLineMap[monthTitle.key]['US']['total'].weight>0 }">
			              <fmt:formatNumber type="percent"  value="${offLineMap[monthTitle.key]['US']['1'].weight/offLineMap[monthTitle.key]['US']['total'].weight }"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['US']['total'].weight>0 }">
		           <fmt:formatNumber type="percent"  value="${offLineMap['total']['US']['1'].weight/offLineMap['total']['US']['total'].weight }"  maxFractionDigits="0"/>
		         </c:if>  
		       </td>
		     </tr>
		   <tr>
		       <td style="text-align: center;vertical-align: middle;">EX</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${offLineMap[monthTitle.key]['US']['total'].weight>0 }">
			              <fmt:formatNumber type="percent" value="${offLineMap[monthTitle.key]['US']['2'].weight/offLineMap[monthTitle.key]['US']['total'].weight }"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${offLineMap['total']['US']['total'].weight>0 }">
		           <fmt:formatNumber type="percent"  value="${offLineMap['total']['US']['2'].weight/offLineMap['total']['US']['total'].weight }"  maxFractionDigits="0"/>
		         </c:if>  
		       </td>
		     </tr>
		     
		     
		     
		    <tr>
		       <td rowspan='3' style="text-align: center;vertical-align: middle;">TOTAL</td>
		       <td style="text-align: center;vertical-align: middle;">AE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${(offLineMap[monthTitle.key]['US']['total'].weight+offLineMap[monthTitle.key]['EU']['total'].weight)>0 }">
			              <fmt:formatNumber type="percent"  value="${(offLineMap[monthTitle.key]['US']['0'].weight+offLineMap[monthTitle.key]['EU']['0'].weight)/(offLineMap[monthTitle.key]['US']['total'].weight+offLineMap[monthTitle.key]['EU']['total'].weight) }"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${(offLineMap['total']['US']['total'].weight+offLineMap['total']['EU']['total'].weight)>0 }">
		           <fmt:formatNumber type="percent"  value="${(offLineMap['total']['US']['0'].weight+offLineMap['total']['EU']['0'].weight)/(offLineMap['total']['US']['total'].weight+offLineMap['total']['EU']['total'].weight) }"  maxFractionDigits="0"/>
		         </c:if>  
		       </td>
		     </tr>
		     
		       <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${(offLineMap[monthTitle.key]['US']['total'].weight+offLineMap[monthTitle.key]['EU']['total'].weight)>0 }">
			              <fmt:formatNumber type="percent" value="${(offLineMap[monthTitle.key]['US']['1'].weight+offLineMap[monthTitle.key]['EU']['1'].weight)/(offLineMap[monthTitle.key]['US']['total'].weight+offLineMap[monthTitle.key]['EU']['total'].weight) }"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${(offLineMap['total']['US']['total'].weight+offLineMap['total']['EU']['total'].weight)>0 }">
		           <fmt:formatNumber type="percent"  value="${(offLineMap['total']['US']['1'].weight+offLineMap['total']['EU']['1'].weight)/(offLineMap['total']['US']['total'].weight+offLineMap['total']['EU']['total'].weight) }"  maxFractionDigits="0"/>
		         </c:if>  
		       </td>
		     </tr>
		     
		    <tr>
		       <td style="text-align: center;vertical-align: middle;">OE</td>
		       <c:forEach items="${offLineMap}" var="monthTitle">
		        <c:if test="${'total' ne monthTitle.key }">
		           <td style="text-align: center;vertical-align: middle;">
			           <c:if test="${(offLineMap[monthTitle.key]['US']['total'].weight+offLineMap[monthTitle.key]['EU']['total'].weight)>0 }">
			              <fmt:formatNumber  type="percent" value="${(offLineMap[monthTitle.key]['US']['2'].weight+offLineMap[monthTitle.key]['EU']['2'].weight)/(offLineMap[monthTitle.key]['US']['total'].weight+offLineMap[monthTitle.key]['EU']['total'].weight) }"  maxFractionDigits="0"/>
			           </c:if>
		           </td>
		        </c:if>
		       </c:forEach>
		        <td style="text-align: center;vertical-align: middle;background-color:#cccccc;">
		           <c:if test="${(offLineMap['total']['US']['total'].weight+offLineMap['total']['EU']['total'].weight)>0 }">
		           <fmt:formatNumber type="percent"  value="${(offLineMap['total']['US']['2'].weight+offLineMap['total']['EU']['2'].weight)/(offLineMap['total']['US']['total'].weight+offLineMap['total']['EU']['total'].weight) }"  maxFractionDigits="0"/>
		         </c:if>  
		       </td>
		     </tr>
		</tbody>
	</table>
	
</body>
</html>
