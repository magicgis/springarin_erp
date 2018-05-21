<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>FBA库存超期统计</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.red{color:red;}
	</style>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
	$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn ){
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr,i) {
			return $('td:eq('+iColumn+')', tr).text().replace(/,/g, "");
		} );
	};
	
		$(document).ready(function() {
			
			
			
		});
		
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
		
		function queryDetail(lineId,country){
		   var productPositionMap = new Map();
		   productPositionMap.set('1',"${fns:getDictLabel('1','product_position','')}");
		   productPositionMap.set('2',"${fns:getDictLabel('2','product_position','')}");
		   productPositionMap.set('3',"${fns:getDictLabel('3','product_position','')}");
		   productPositionMap.set('4',"${fns:getDictLabel('4','product_position','')}");
			var html="<div class='showChildrenHtml' style='text-align:center;margin-left:10px;' ><table style='width:98%;margin-top:10px'  class='table table-striped table-bordered table-condensed'><thead><tr><th style='width:8%;'>产品名</th><th style='width:8%;'>国家</th><th style='width:8%;'>新品</th><th style='width:8%;'>产品定位</th><th  style='width:5%;'>6-12月库存</th><th  style='width:5%;'>12月以上库存</th><th style='width:5%;'>负责人</th></tr></thead><tbody>";
			 <c:forEach items='${detailMap}' var='nameTemp'>
			    <c:forEach items="${detailMap[nameTemp.key]}" var='temp'>
			           var temp="<tr><td>${temp.key}</td><td>${nameTemp.key}</td><td>${isNewMap[temp.key][nameTemp.key].isNew>0?'新品':'普通'}</td><td>"+productPositionMap.get('${isNewMap[temp.key][nameTemp.key].isSale}')+"</td><td>${detailMap[nameTemp.key][temp.key].ageDays365}</td><td>${detailMap[nameTemp.key][temp.key].agePlusDays365}</td><td>${fn:split(groupMap[nameAndLineMap[temp.key]][nameTemp.key],',')[1]}</td></tr>";
					   if(lineId=='total'){
						   if(country=='en'){
							  if('${nameTemp.key}'=='com'||'${nameTemp.key}'=='uk'||'${nameTemp.key}'=='ca'){
								  html=html+temp;
							  }
						   }else if(country=='nonEn'){
							   if('${nameTemp.key}'=='de'||'${nameTemp.key}'=='fr'||'${nameTemp.key}'=='it'||'${nameTemp.key}'=='es'||'${nameTemp.key}'=='jp'){
								   html=html+temp;
							   }
						   }else if(country=='total'){
							   if('${nameTemp.key}'!='en'&&'${nameTemp.key}'!='nonEn'){
								   html=html+temp;
							   }
							 }
					   }else{
						   if('${nameAndLineMap[temp.key]}'==lineId){
							       if(country=='en'){
									  if('${nameTemp.key}'=='com'||'${nameTemp.key}'=='uk'||'${nameTemp.key}'=='ca'){
										  html=html+temp;
									  }
								   }else if(country=='nonEn'){
									   if('${nameTemp.key}'=='de'||'${nameTemp.key}'=='fr'||'${nameTemp.key}'=='it'||'${nameTemp.key}'=='es'||'${nameTemp.key}'=='jp'){
										   html=html+temp;
									   }
								   }else if(country=='total'){
									   if('${nameTemp.key}'!='en'&&'${nameTemp.key}'!='nonEn'){
										   html=html+temp;
									   }
								   }
						   }
					   }
			  
			   </c:forEach>
			</c:forEach>
			html=html+"</tbody></table></div>";
			top.$.jBox.open(html,"查看明细",1000,350,{buttons: {'关闭': true}});  
		}
		
		function exportData(){
			$("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport/expOverFbaProduct");
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport/findOverFbaProduct");
		}
		
		function exportData2(){
			$("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport/expOverFbaProduct2");
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport/findOverFbaProduct");
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
   		<li ><a href="${ctx}/psi/psiInventory/findOutOfStock">断货次数</a></li>
   		<li><a href="${ctx}/psi/psiInventory/countPurchase">采购次数</a></li>
   	    <li><a href="${ctx}/psi/psiInventory/getTurnoverRate">周转率</a></li>
   	    <li class='active'><a href="${ctx}/amazoninfo/amazonFbaHealthReport/findOverFbaProduct">FBA超期库存统计</a></li>
	</ul>
	<form:form id="searchForm"  action="${ctx}/amazoninfo/amazonFbaHealthReport/findOverFbaProduct" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
		    <input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			<label>日期：</label>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createTime" value="<fmt:formatDate value="${amazonFbaHealthReport.createTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="startMonth"/>
	        <input id='export' type='button' value='分产品导出'  class='btn' onclick='exportData();'/> 
	        <!-- <input id='export' type='button' value='分产品线导出'  class='btn' onclick='exportData2();'/>  -->
	</form:form>
	<tags:message content="${message}"/>   
	
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead>
		   <tr>
		      <th>国家</th>
		      <th>类型</th>
		      <c:forEach items="${lineList}" var="line"> <th>${line.name }</th></c:forEach>
		      <th>合计</th>
		   </tr>
		</thead>
		
		<tbody>
		  <tr>
			  <td rowspan='2'>英语国家</td>
			  <td>6-12月</td>
		      <c:forEach items="${lineList}" var="line"> 
		        <td><a onclick='queryDetail("${line.id}","en")'>${lineMap['en'][line.id].ageDays365 }</a><c:if test="${not empty lineMap['en'][line.id] }">(${fn:split(groupMap[line.id]['com'],',')[1]})(<fmt:formatNumber value="${lineMap['en'][line.id].ageDays365*100/lineMap['en']['total'].ageDays365}" maxFractionDigits="0"/>%)</c:if></td>
		      </c:forEach>
		      <td><a onclick='queryDetail("total","en")'>${lineMap['en']['total'].ageDays365 }</a></td>
		 </tr>
		   <tr>
		      <td>12月以上</td>
		      <c:forEach items="${lineList}" var="line"> 
		        <td><a onclick='queryDetail("${line.id}","en")'>${lineMap['en'][line.id].agePlusDays365 }</a><c:if test="${not empty lineMap['en'][line.id] }">(${fn:split(groupMap[line.id]['com'],',')[1]})(<fmt:formatNumber value="${lineMap['en'][line.id].agePlusDays365 *100/lineMap['en']['total'].agePlusDays365 }" maxFractionDigits="0"/>%)</c:if></td>
		      </c:forEach>
		      <td><a onclick='queryDetail("total","en")'>${lineMap['en']['total'].agePlusDays365 }</a></td>
		 </tr>
		 
		   <tr>
			  <td rowspan='2'>非英语国家</td>
			  <td>6-12月</td>
		      <c:forEach items="${lineList}" var="line"> 
		        <td><a onclick='queryDetail("${line.id}","nonEn")'>${lineMap['nonEn'][line.id].ageDays365 }</a><c:if test="${not empty lineMap['nonEn'][line.id] }">(${fn:split(groupMap[line.id]['de'],',')[1]})(<fmt:formatNumber value="${lineMap['nonEn'][line.id].ageDays365*100/lineMap['nonEn']['total'].ageDays365}" maxFractionDigits="0"/>%)</c:if></td>
		      </c:forEach>
		      <td><a onclick='queryDetail("total","nonEn")'>${lineMap['nonEn']['total'].ageDays365 }</a></td>
		 </tr>
		   <tr>
		      <td>12月以上</td>
		      <c:forEach items="${lineList}" var="line"> 
		        <td><a onclick='queryDetail("${line.id}","nonEn")'>${lineMap['nonEn'][line.id].agePlusDays365 }</a><c:if test="${not empty lineMap['nonEn'][line.id] }">(${fn:split(groupMap[line.id]['de'],',')[1]})(<fmt:formatNumber value="${lineMap['nonEn'][line.id].agePlusDays365 *100/lineMap['nonEn']['total'].agePlusDays365 }" maxFractionDigits="0"/>%)</c:if></td>
		      </c:forEach>
		      <td><a onclick='queryDetail("total","nonEn")'>${lineMap['nonEn']['total'].agePlusDays365 }</a></td>
		 </tr>
		
		</tbody>
		
		<tfoot>
		  <tr>   
		      <td rowspan='2'>合计</td> 
		      <td>6-12月</td>
		      <c:forEach items="${lineList}" var="line"> 
		         <td><a onclick='queryDetail("${line.id}","total")'>${lineMap['total'][line.id].ageDays365 }</a><c:if test="${not empty lineMap['total'][line.id].ageDays365  }">(<fmt:formatNumber value="${lineMap['total'][line.id].ageDays365*100/lineMap['total']['total'].ageDays365}" maxFractionDigits="0"/>%)</c:if></td>
		      </c:forEach>
		      <td><a onclick='queryDetail("total","total")'>${lineMap['total']['total'].ageDays365 }</a></td>
		  </tr>  
		  <tr>    
		    <td>12月以上</td>
		      <c:forEach items="${lineList}" var="line"> 
		         <td><a onclick='queryDetail("${line.id}","total")'>${lineMap['total'][line.id].agePlusDays365 }</a><c:if test="${not empty lineMap['total'][line.id].agePlusDays365 }">(<fmt:formatNumber value="${lineMap[line.id]['total'].agePlusDays365*100/lineMap['total']['total'].agePlusDays365 }" maxFractionDigits="0"/>%)</c:if></td>
		      </c:forEach>
		      <td><a onclick='queryDetail("total","total")'>${lineMap['total']['total'].agePlusDays365 }</a></td>
		  </tr>    
		</tfoot>
		
	</table>
</body>
</html>
