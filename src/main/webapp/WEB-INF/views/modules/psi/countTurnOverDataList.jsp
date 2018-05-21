<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>周转率</title>
<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
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
			 var oTable = $("#dataTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType": "bootstrap","sScrollX": "100%",
				 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
				 	"aaSorting": [[0, "desc" ]]
				});
			
			 new FixedColumns(oTable,{
			 		"iLeftColumns":1,
					"iLeftWidth":100
			 });
			 
			// $(".row:first").append($("#searchContent").html());
			 
		});
		
		function exportData(){
			$("#searchForm").attr("action","${ctx}/psi/psiInventory/exportTurnoverRate");
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/psi/psiInventory/getTurnoverRate");
		}
		

		function exportData2(){
			$("#searchForm").attr("action","${ctx}/psi/psiInventory/exportTurnoverRateByLine");
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/psi/psiInventory/getTurnoverRate");
		}
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            return x.toFixed(2);  
	     }  
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		function timeOnChange(){
			$("#searchForm").submit();
		}
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*100)/100;  
	            return f;  
	     }  
		
		 function queryDetail(lineId,country,month,lineName){
				var html="<div class='showChildrenHtml' style='text-align:center;margin-left:10px;' ><table style='width:98%;margin-top:10px'  class='table table-striped table-bordered table-condensed'><thead><tr><th style='width:8%;'>国家</th><th style='width:5%;'>周转率</th></tr></thead><tbody>";
				 <c:forEach items='${map}' var='temp'>
				    if('${temp.key}'==month){
				    	<c:forEach items="${map[temp.key]}" var='countryTemp'>
				        	<c:forEach items="${map[temp.key][countryTemp.key]}" var='lineTemp'>
					           if(lineId=='total'){
								   if(country=='en'){
									  if('${countryTemp.key}'=='com'||'${countryTemp.key}'=='uk'||'${countryTemp.key}'=='ca'){
										  html=html+"<tr><td>${countryTemp.key}</td><td>"+toDecimal('${map[temp.key][countryTemp.key][lineTemp.key].sPrice/map[temp.key][countryTemp.key][lineTemp.key].ePrice}')+"</td></tr>";
									  }
								   }else if(country=='unEn'){
									   if('${countryTemp.key}'=='de'||'${countryTemp.key}'=='fr'||'${countryTemp.key}'=='it'||'${countryTemp.key}'=='es'||'${countryTemp.key}'=='jp'){
											  html=html+"<tr><td>${countryTemp.key}</td><td>"+toDecimal('${map[temp.key][countryTemp.key][lineTemp.key].sPrice/map[temp.key][countryTemp.key][lineTemp.key].ePrice}')+"</td></tr>";
									   }
								   }else if(country=='total'){
									   html=html+"<tr><td>${countryTemp.key}</td><td>"+toDecimal('${map[temp.key][countryTemp.key][lineTemp.key].sPrice/map[temp.key][countryTemp.key][lineTemp.key].ePrice}')+"</td></tr>";
								   }
							   }else{
								   if('${lineTemp.key}'==lineName){
									       if(country=='en'){
											  if('${countryTemp.key}'=='com'||'${countryTemp.key}'=='uk'||'${countryTemp.key}'=='ca'){
												  html=html+"<tr><td>${countryTemp.key}</td><td>"+toDecimal('${map[temp.key][countryTemp.key][lineTemp.key].sPrice/map[temp.key][countryTemp.key][lineTemp.key].ePrice}')+"</td></tr>";
											  }
										   }else if(country=='unEn'){
											   if('${countryTemp.key}'=='de'||'${countryTemp.key}'=='fr'||'${countryTemp.key}'=='it'||'${countryTemp.key}'=='es'||'${countryTemp.key}'=='jp'){
													  html=html+"<tr><td>${countryTemp.key}</td><td>"+toDecimal('${map[temp.key][countryTemp.key][lineTemp.key].sPrice/map[temp.key][countryTemp.key][lineTemp.key].ePrice}')+"</td></tr>";
											   }
										   }else if(country=='total'){
											   html=html+"<tr><td>${countryTemp.key}</td><td>"+toDecimal('${map[temp.key][countryTemp.key][lineTemp.key].sPrice/map[temp.key][countryTemp.key][lineTemp.key].ePrice}')+"</td></tr>";
										   }
								   }
							   }
					        </c:forEach>
				        </c:forEach>
				    }
				</c:forEach>
				html=html+"</tbody></table></div>";
				top.$.jBox.open(html,"查看明细",1000,350,{buttons: {'关闭': true}});  
			}
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
       <li><a class="countryHref" href="${ctx}/psi/psiInventory/outOfStockByProduct" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'&&dic.value ne 'it'&&dic.value ne 'es'}">
				<li class="${country eq dic.value ?'active':''}"><a class="countryHref" href="${ctx}/psi/psiInventory/outOfStockByProduct?country=${dic.value}" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
   		<li><a href="${ctx}/psi/psiInventory/findOutOfStock">断货次数</a></li>
   		<li><a href="${ctx}/psi/psiInventory/countPurchase">采购次数</a></li>
   		<li class='active'><a href="${ctx}/psi/psiInventory/getTurnoverRate">周转率</a></li>
   		 <li><a href="${ctx}/amazoninfo/amazonFbaHealthReport/findOverFbaProduct">FBA超期库存统计</a></li>
	</ul>
	<!-- <div style="display: none" id="searchContent"> -->
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/psiInventory/getTurnoverRate" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
		<label>统计月份：</label>
		<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyyMM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${start}" pattern="yyyyMM" />" id="start" class="input-small"/>
			&nbsp;至&nbsp;
		<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyyMM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${end}" pattern="yyyyMM" />" id="end" class="input-small"/>
		<input id='export' type='button' value='分产品导出'  class='btn' onclick='exportData();'/>
		<shiro:hasPermission name="amazon:promotionsWarning:review">
		   <input id='export' type='button' value='分线导出'  class='btn' onclick='exportData2();'/>
		</shiro:hasPermission>
	</form:form>
	<!-- </div> -->
	<tags:message content="${message}"/>   
	
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead>
		    <tr>
		      <th rowspan='2'>国家</th>
		      <c:forEach items="${monthList}" var="month"> 
		          <th colspan='${lineSize }'>${month}</th>
		      </c:forEach>
		   </tr> 
		   <tr>
		      <c:forEach items="${monthList}" var="month"> 
		           <c:forEach items="${lineList}" var="line"> 
		               <th>${line.name }</th>
		           </c:forEach>
		      </c:forEach>
		   </tr>
		</thead>
		
		<tbody>
		  <tr>
			  <td>英语国家</td>
			   <c:forEach items="${monthList}" var="month"> 
		           <c:forEach items="${lineList}" var="line"> 
		               <c:set var='lineName' value='${fns:getSubString(line.name,0,1)}'/>
		               <td><c:if test="${map[month]['en'][lineName].ePrice>0 }"><a onclick='queryDetail("${lineId}","en","${month }","${lineName}")'><fmt:formatNumber value="${map[month]['en'][lineName].sPrice/map[month]['en'][lineName].ePrice }" maxFractionDigits="2"/></a>(${fn:split(groupMap[line.id]['com'],',')[1]})</c:if></td>
		           </c:forEach>
		        </c:forEach> 
		     
		 </tr>
		 <tr>    
		      <td>非英语国家</td>
		      <c:forEach items="${monthList}" var="month"> 
		           <c:forEach items="${lineList}" var="line"> 
		                <c:set var='lineName' value='${fns:getSubString(line.name,0,1)}'/>
		               <td><c:if test="${map[month]['nonEn'][lineName].ePrice>0 }"><a onclick='queryDetail("${lineId}","unEn","${month }","${lineName}")'><fmt:formatNumber value="${map[month]['nonEn'][lineName].sPrice/map[month]['nonEn'][lineName].ePrice }" maxFractionDigits="2"/></a>(${fn:split(groupMap[line.id]['de'],',')[1]})</c:if></td>
		           </c:forEach>
		        </c:forEach> 
		  </tr>    
		</tbody>
		
		<tfoot>
		  <tr>    
		      <td>平均值</td>
		    <c:forEach items="${monthList}" var="month"> 
		           <c:forEach items="${lineList}" var="line"> 
		               <c:set var='lineName' value='${fns:getSubString(line.name,0,1)}'/>
		               <td><c:if test="${map[month]['total'][lineName].ePrice>0 }"><a onclick='queryDetail("${lineId}","total","${month }","${lineName}")'><fmt:formatNumber value="${map[month]['total'][lineName].sPrice/map[month]['total'][lineName].ePrice }" maxFractionDigits="2"/></a></c:if></td>
		           </c:forEach>
		      </c:forEach> 
		  </tr>    
		</tfoot>
		
	</table>
</body>
</html>
