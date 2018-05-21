<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>缺口</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
			
			$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 15,
				"aLengthMenu":[[10, 20, 60,100,-1], [10, 20, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			     "aaSorting": [[0, "asc" ]]
			});
			
			var cnt="<form  id='inputForm' action='${ctx}/psi/psiOutOfStockInfo/findGapInfo' method='post' > "+
			   
			   " &nbsp;&nbsp;&nbsp;<select name='weekNum' id='weekNum' style='width: 100px' onchange='changeWeekNum()'> "+
			   " <option value='0' ${'0' eq weekNum?'selected':''}>4周</option>"+
			   " <option value='1' ${'1' eq weekNum?'selected':''}>8周</option>"+
			   " <option value='2' ${'2' eq weekNum?'selected':''}>12周</option>"+
			   " <option value='3' ${'3' eq weekNum?'selected':''}>16周</option></select> &nbsp;&nbsp;<input type='hidden' name='country' id='country' value='${country}'/>"+
			   " <a class='btn btn-primary'  id='expExcel'>销售预测导出</a>&nbsp;&nbsp;"+
			   " <a class='btn btn-primary'  id='expExcel2'>周日销导出</a></form>";
			 $(".row:first").append(cnt);
			
			 
			 $("#expExcel").click(function(){
				 $("#expExcel").attr("href","${ctx}/psi/psiOutOfStockInfo/exportDetail?forecastType=0&country="+$("#country").val());
			});
				
			 $("#expExcel2").click(function(){
				 $("#expExcel2").attr("href","${ctx}/psi/psiOutOfStockInfo/exportDetail?forecastType=1&country="+$("#country").val());
			});
			 
			 $(".countryHref").click(function(){
					$("input[name='country']").val($(this).attr("key"));
					$("#inputForm").submit();
			});
			 
		});
		
		function changeType(){
			$("#inputForm").submit();
			// window.location.href="${ctx}/psi/psiOutOfStockInfo/exportDetail?forecastType="+$("#type").val();
		}
		
		function changeWeekNum(){
			$("#inputForm").submit();
		}
	</script>
</head>
<body>
	
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<li><a href="${ctx}/psi/psiOutOfStockInfo/findEuGapInfo">欧洲</a></li>	
		<li><a href="${ctx}/psi/psiOutOfStockInfo/findAllGapInfo">总计</a></li>	
	</ul>

	
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
		   <tr>
		        <th style="text-align: center;vertical-align: middle;">依据</th>
				<th style="text-align: center;vertical-align: middle;width:25%;">产品</th>
				<th style="text-align: center;vertical-align: middle;">FBA库存</th>
				<th style="text-align: center;vertical-align: middle;">预计断货日期</th>
				<th style="text-align: center;vertical-align: middle;">断货天数</th>
				<th style="text-align: center;vertical-align: middle;">缺口量</th>
				<th style="text-align: center;vertical-align: middle;">类型</th>
				<th style="text-align: center;vertical-align: middle;">提示</th>
			</tr>
		</thead>
		<tbody>
		  
			 <c:forEach items="${gapMap}" var="gap"> 
			  <c:forEach items="${gapMap[gap.key]}" var="gapType"> 
			     <c:forEach items="${gapMap[gap.key][gapType.key]}" var="nameType"> 
			        <c:if test="${'de' eq country &&'1' eq powerMap[gapType.key] }">
				      <tr> <c:set value='${gapType.key }_${country }' var="name"/>
				           <td style="text-align: center;vertical-align: middle;">
				            <span style="visibility:hidden;">${'0' eq gap.key?'0':('1' eq gap.key?'1':('2' eq gap.key?'3':'2')) }</span> 
				           ${'0' eq gap.key?'销售预测':('1' eq gap.key?'周日销':('2' eq gap.key?'周日销(安)':'销售预测(安)')) }</td>
			               <td style="text-align: center;vertical-align: middle;"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${gapType.key}" target="_blank">${gapType.key}</a>
			                   <span>${fns:getDictLabel(positionMap[gapType.key],'product_position','')}</span>
			                   <c:if test="${'2' eq tranMap[gapType.key][country]}"><span class="icon-plane"></span></c:if>
			               </td>
			               <td style="text-align: center;vertical-align: middle;">${fbaStock[name].fulfillableQuantity }</td>
			               <td style="text-align: center;vertical-align: middle;">${nameType.value.time }</td> 
			               <td style="text-align: center;vertical-align: middle;">${nameType.value.day }周</td>
			               <td style="text-align: center;vertical-align: middle;">${nameType.value.gap }</td>
			               <td style="text-align: center;vertical-align: middle;">${nameType.value.gapType }</td> 
			               <td style="text-align: center;vertical-align: middle;">${nameType.value.desc }
			               </td> 
		             </tr> 
			         </c:if>
			        </c:forEach> 
			     </c:forEach>
				<c:if test="${'de' ne country }">
				   <c:forEach items="${gapMap[gap.key]}" var="gapType"> 
				    <c:forEach items="${gapMap[gap.key][gapType.key]}" var="nameType"> 
				       <tr> <c:set value='${gapType.key }_${country }' var="name"/>
				           <td style="text-align: center;vertical-align: middle;">${'0' eq gap.key?'销售预测':('1' eq gap.key?'周日销':('2' eq gap.key?'周日销(安)':'销售预测(安)')) }</td>
			               <td style="text-align: center;vertical-align: middle;"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${gapType.key}" target="_blank">${gapType.key}</a>
			                   <span>${fns:getDictLabel(positionMap[gapType.key],'product_position','')}</span>
			                   <c:if test="${'2' eq tranMap[gapType.key][country]}"><span class="icon-plane"></span></c:if>
			               </td>
			               <td style="text-align: center;vertical-align: middle;">${fbaStock[name].fulfillableQuantity }</td>
			               <td style="text-align: center;vertical-align: middle;">${nameType.value.time }</td> 
			               <td style="text-align: center;vertical-align: middle;">${nameType.value.day }周</td>
			               <td style="text-align: center;vertical-align: middle;">${nameType.value.gap }</td>
			               <td style="text-align: center;vertical-align: middle;">${nameType.value.gapType }</td> 
			               <td style="text-align: center;vertical-align: middle;">${nameType.value.desc }</td> 
		             </tr> 
		             </c:forEach>
				   </c:forEach>
				</c:if>
			</c:forEach>
		</tbody>
	</table>
	
</body>
</html>
