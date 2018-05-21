<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购次数统计</title>
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
			 
			 $(".row:first").append($("#searchContent").html());
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
		
		function queryDetail(lineId,country,week){//国家-采购周-产品线-次数
			var html="<div class='showChildrenHtml' style='text-align:center;margin-left:10px;' ><table style='width:98%;margin-top:10px'  class='table table-striped table-bordered table-condensed'><thead><tr><th style='width:8%;'>产品名</th><th style='width:8%;'>国家</th><th  style='width:5%;'>采购单号</th><th  style='width:5%;'>超出次数</th><th style='width:5%;'>负责人</th></tr></thead><tbody>";
			 <c:forEach items='${detailMap}' var='temp'>
			    <c:forEach items="${detailMap[temp.key]}" var='weekTemp'>
			        if('${weekTemp.key}'==week){
			        	<c:forEach items="${detailMap[temp.key][weekTemp.key]}" var='nameTemp'>
				           if(lineId=='total'){
							   if(country=='en'){
								  if('${temp.key}'=='com'||'${temp.key}'=='uk'||'${temp.key}'=='ca'){
									  html=html+"<tr><td>${nameTemp.key}</td><td>${temp.key}</td><td>${detailMap[temp.key][weekTemp.key][nameTemp.key]}</td><td>${fn:length(fn:split(detailMap[temp.key][weekTemp.key][nameTemp.key],','))-1 }</td><td>${fn:split(groupMap[nameAndLineMap[nameTemp.key]][temp.key],',')[1]}</td></tr>";
								  }
							   }else if(country=='unEn'){
								   if('${temp.key}'=='de'||'${temp.key}'=='fr'||'${temp.key}'=='it'||'${temp.key}'=='es'||'${temp.key}'=='jp'){
										  html=html+"<tr><td>${nameTemp.key}</td><td>${temp.key}</td><td>${detailMap[temp.key][weekTemp.key][nameTemp.key]}</td><td>${fn:length(fn:split(detailMap[temp.key][weekTemp.key][nameTemp.key],','))-1 }</td><td>${fn:split(groupMap[nameAndLineMap[nameTemp.key]][temp.key],',')[1]}</td></tr>";
								   }
							   }else if(country=='total'){
								   html=html+"<tr><td>${nameTemp.key}</td><td>${temp.key}</td><td>${detailMap[temp.key][weekTemp.key][nameTemp.key]}</td><td>${fn:length(fn:split(detailMap[temp.key][weekTemp.key][nameTemp.key],','))-1 }</td><td>${fn:split(groupMap[nameAndLineMap[nameTemp.key]][temp.key],',')[1]}</td></tr>";
							   }
						   }else{
							   if('${nameAndLineMap[nameTemp.key]}'==lineId){
								       if(country=='en'){
										  if('${temp.key}'=='com'||'${temp.key}'=='uk'||'${temp.key}'=='ca'){
											  html=html+"<tr><td>${nameTemp.key}</td><td>${temp.key}</td><td>${detailMap[temp.key][weekTemp.key][nameTemp.key]}</td><td>${fn:length(fn:split(detailMap[temp.key][weekTemp.key][nameTemp.key],','))-1 }</td><td>${fn:split(groupMap[nameAndLineMap[nameTemp.key]][temp.key],',')[1]}</td></tr>";
										  }
									   }else if(country=='unEn'){
										   if('${temp.key}'=='de'||'${temp.key}'=='fr'||'${temp.key}'=='it'||'${temp.key}'=='es'||'${temp.key}'=='jp'){
												  html=html+"<tr><td>${nameTemp.key}</td><td>${temp.key}</td><td>${detailMap[temp.key][weekTemp.key][nameTemp.key]}</td><td>${fn:length(fn:split(detailMap[temp.key][weekTemp.key][nameTemp.key],','))-1 }</td><td>${fn:split(groupMap[nameAndLineMap[nameTemp.key]][temp.key],',')[1]}</td></tr>";
										   }
									   }else if(country=='total'){
										   html=html+"<tr><td>${nameTemp.key}</td><td>${temp.key}</td><td>${detailMap[temp.key][weekTemp.key][nameTemp.key]}</td><td>${fn:length(fn:split(detailMap[temp.key][weekTemp.key][nameTemp.key],','))-1 }</td><td>${fn:split(groupMap[nameAndLineMap[nameTemp.key]][temp.key],',')[1]}</td></tr>";
									   }
							   }
						   }
				        </c:forEach>
			        }
			   </c:forEach>
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
   		<li class='active'><a href="${ctx}/psi/psiInventory/countPurchase">采购次数</a></li>
   		 <li><a href="${ctx}/psi/psiInventory/getTurnoverRate">周转率</a></li>
   		  <li><a href="${ctx}/amazoninfo/amazonFbaHealthReport/findOverFbaProduct">FBA超期库存统计</a></li>
	</ul>
	<div style="display: none" id="searchContent">
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/psiInventory/countPurchase" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='text' name='week' value='${week}'  class='input-mini'/>个采购周&nbsp;&nbsp;&nbsp;
		<input type='submit' value='查询'  class='btn'/>
	</form:form>
	</div>
	<tags:message content="${message}"/>   
	
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead>
		    <tr>
		      <th rowspan='2'>国家</th>
		      <c:forEach items="${weekList}" var="week"> 
		          <th colspan='${lineSize }'>${'0' eq week?'本':'前'}${'0' eq week?'':week}采购周</th>
		      </c:forEach>
		   </tr> 
		   <tr>
		   
		      <c:forEach items="${weekList}" var="week"> 
		           <c:forEach items="${lineList}" var="line"> 
		               <th>${line.name }</th>
		           </c:forEach>
		      </c:forEach>
		   </tr>
		</thead>
		
		<tbody>
		  <tr>
			  <td>英语国家</td>
			   <c:forEach items="${weekList}" var="week"> 
		           <c:forEach items="${lineList}" var="line"> 
		               <td><a onclick='queryDetail("${line.id}","en","${week }")'>${map['en'][week][line.id] }</a><c:if test="${not empty map['en'][week][line.id] }">(${fn:split(groupMap[line.id]['com'],',')[1]})</c:if></td>
		           </c:forEach>
		        </c:forEach> 
		     
		 </tr>
		 <tr>    
		      <td>非英语国家</td>
		      <c:forEach items="${weekList}" var="week"> 
		           <c:forEach items="${lineList}" var="line"> 
		               <td><a onclick='queryDetail("${line.id}","unEn","${week }")'>${map['unEn'][week][line.id] }</a><c:if test="${not empty map['unEn'][week][line.id] }">(${fn:split(groupMap[line.id]['de'],',')[1]})</c:if></td>
		           </c:forEach>
		        </c:forEach> 
		  </tr>    
		</tbody>
		
		<tfoot>
		  <tr>    
		      <td>合计</td>
		      <c:forEach items="${weekList}" var="week"> 
		           <c:forEach items="${lineList}" var="line"> 
		               <td><a onclick='queryDetail("${line.id}","total","${week }")'>${map['total'][week][line.id] }</a></td>
		           </c:forEach>
		      </c:forEach> 
		  </tr>    
		</tfoot>
		
	</table>
</body>
</html>
