<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>批发价格</title>
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
			
			var cnt="&nbsp;&nbsp;货币类型:&nbsp;<select name='type' id='currencyType' style='width: 100px' onchange='changeCurrecyType()'> "+
			   " <option value='' ${empty type?'selected':''}>EUR</option>"+
			   "   <option value='1' ${not empty type?'selected':''}>USD</option>"+
			   "  </select> &nbsp;&nbsp;<a class='btn btn-primary'  id='expExcel'>导出</a><br/>"+
			   "<div class='alert'>&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;1.Price less than cost price default set to cost price, marking by red font.<br/> "+
			   " &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;2.Products whose cost price could not be detected by the system are highlighted in orange background.</div> ";

			 $(".row:first").append(cnt);
			
			 
			 $("#expExcel").click(function(){
				 $("#expExcel").attr("href","${ctx}/amazoninfo/amazonProduct/exportWholeSalePrice?type="+$("#currencyType").val());
			});
				
		});
		
		function changeCurrecyType(){
			window.location.href="${ctx}/amazoninfo/amazonProduct/getWholeSalePrice?type="+$("#currencyType").val();
		}
		
	</script>
</head>
<body>
    <%-- <div  id="searchContent"> 
		<form  id="inputForm" action="${ctx}/amazoninfo/amazonProduct/getProductPrice" method="post" >
			&nbsp;&nbsp;&nbsp;&nbsp;时间:
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM'});"  readonly="readonly" class="Wdate" type="text"  id="addDate" name="date" value="<fmt:formatDate value="${productPrice.date}" pattern="yyyy-MM-dd" />"  class="input-small"/>
			<input  class="btn btn-primary"  type="submit" value="查询"/>
		</form>
	</div> --%>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
		   <tr>
				<th style="text-align: center;vertical-align: middle;width:25%;">产品</th>
				<th style="text-align: center;vertical-align: middle;width:7%;">在售</th>
				<th style="text-align: center;vertical-align: middle;width:17%;">30天平均价</th>
				<th style="text-align: center;vertical-align: middle;width:17%;">九折价</th>
				<th style="text-align: center;vertical-align: middle;width:17%;">八折价</th>
				<th style="text-align: center;vertical-align: middle;width:17%;">七折价</th>
				
			</tr>
			
		</thead>
		<tbody>
			 <c:forEach items="${wholePrice}" var="price" varStatus="i"> 
			 <tr style="${empty safePrice[price.key]?'background-color:#ff9900;':'' }">
			     <c:set value='${price.key }_de' var="key"/>
			      <td style="text-align: center;vertical-align: middle;"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${price.key}" target="_blank">${price.key}</a></td>
			      <td style="text-align: center;vertical-align: middle;">${'4' eq productPositionMap[key]?'不可售':'可销售'}</td>
			      <c:if test="${empty safePrice[price.key] }">
			          <td style="text-align: center;vertical-align: middle;color:red;}">
			             <fmt:formatNumber value=" ${wholePrice[price.key]}" maxFractionDigits="2" minFractionDigits="2" />
			          </td>
			          <td></td><td></td><td></td>
			      </c:if>
				  <c:if test="${not empty safePrice[price.key]}" >
				    <td style="text-align: center;vertical-align: middle;${wholePrice[price.key]<safePrice[price.key]?'color:red;':''}">
					    <c:if test="${wholePrice[price.key]>=safePrice[price.key]}"><fmt:formatNumber value=" ${wholePrice[price.key]}" maxFractionDigits="2" minFractionDigits="2" /></c:if>
					    <c:if test="${wholePrice[price.key]<safePrice[price.key]}"><fmt:formatNumber value=" ${safePrice[price.key]}" maxFractionDigits="2" minFractionDigits="2" /></c:if>
					</td>
					<td style="text-align: center;vertical-align: middle;${wholePrice[price.key]*0.9<safePrice[price.key]?'color:red;':''}">
					    <c:if test="${wholePrice[price.key]*0.9>=safePrice[price.key]}"><fmt:formatNumber value="${wholePrice[price.key]*0.9}" maxFractionDigits="2" minFractionDigits="2" /> </c:if>
					    <c:if test="${wholePrice[price.key]*0.9<safePrice[price.key]}"><fmt:formatNumber value="${safePrice[price.key]}" maxFractionDigits="2" minFractionDigits="2" /> </c:if>
					</td>
					<td style="text-align: center;vertical-align: middle;${wholePrice[price.key]*0.8<safePrice[price.key]?'color:red;':''}">
					    <c:if test="${wholePrice[price.key]*0.8>=safePrice[price.key]}"><fmt:formatNumber value="${wholePrice[price.key]*0.8}" maxFractionDigits="2" minFractionDigits="2" /> </c:if>
					    <c:if test="${wholePrice[price.key]*0.8<safePrice[price.key]}"><fmt:formatNumber value="${safePrice[price.key]}" maxFractionDigits="2" minFractionDigits="2" /> </c:if>
					</td>
					<td style="text-align: center;vertical-align: middle;${wholePrice[price.key]*0.7<safePrice[price.key]?'color:red;':''}">
					    <c:if test="${wholePrice[price.key]*0.7>=safePrice[price.key]}"><fmt:formatNumber value=" ${wholePrice[price.key]*0.7}" maxFractionDigits="2" minFractionDigits="2" /></c:if>
					    <c:if test="${wholePrice[price.key]*0.7<safePrice[price.key]}"><fmt:formatNumber value="${safePrice[price.key]}" maxFractionDigits="2" minFractionDigits="2" /> </c:if>
					</td>
				  </c:if>
		        </tr> 
			</c:forEach>
		</tbody>
	</table>
	
</body>
</html>
