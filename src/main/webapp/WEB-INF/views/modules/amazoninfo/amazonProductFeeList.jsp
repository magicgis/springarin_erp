<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊产品费用</title>
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
			
		 	var cnt="<form  id='inputForm' action='${ctx}/amazoninfo/amazonProduct/getProductPrice' method='post' >"+
			"&nbsp;&nbsp;&nbsp;&nbsp;时间: "+
			"	<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'});  readonly='readonly' class='Wdate' type='text' id='addDate' name='date' value='<fmt:formatDate value="${productPrice.date}" pattern="yyyy-MM-dd" />'  class='input-small'/> "+
			"	<input class='btn btn-primary' type='submit' value='查询'/>&nbsp;&nbsp;<input id='expExcel' class='btn btn-primary' type='button' value='导出'/> &nbsp;&nbsp;<input id='commissionExp' class='btn btn-primary' type='button' value='导出异常佣金'/> &nbsp;&nbsp;<span style='color:#ff0033;'>(货币单位为对应平台的货币单位)</span>   "+
			"</form> ";
			 $(".row:first").append(cnt);
			 
			 
			 $("#expExcel").click(function(){
					$("#inputForm").attr("action","${ctx}/amazoninfo/amazonProduct/exportPrice");
					$("#inputForm").submit();
					$("#inputForm").attr("action","${ctx}/amazoninfo/amazonProduct/getProductPrice");
			});
			 
			 $("#commissionExp").click(function(){
				/*  var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' >";
					refundBillHtml=refundBillHtml+"<table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tbody>";
					refundBillHtml=refundBillHtml+"<tr><th>时间:</th><td><input id='createDate' style='width: 130px' value=${fns:getBeforeDate('yyyy-MM-dd')} onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' />-<input id='endDate' style='width: 130px' value=${fns:getBeforeDate('yyyy-MM-dd')} onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' /></td></tr>";
					refundBillHtml=refundBillHtml+"</tbody></table></div>";
					var submitChild = function (v, h, f) {
						$(this).attr("disabled","disabled");
						var params = {};
						params.createDate = $("#createDate").val()
						params.endDate = $("#endDate").val();
					
						$.post("${ctx}/amazoninfo/amazonProduct/exportCommission",$.param(params),function(data){
							top.$.jBox.closeTip(); 
							$("#commissionExp").removeAttr("disabled");
						});
					    return true;
					};

					$.jBox(refundBillHtml, { title: "Export",width:600,submit: submitChild,persistent: true}); */
				    var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' >";
					refundBillHtml+="<form id='searchForm' action='${ctx}/amazoninfo/amazonProduct/exportCommission' method='post' class='form-horizontal' >";
					refundBillHtml+="时间:&nbsp;<input id='createDate' name='createDate' style='width: 130px' value=${fns:getBeforeDate('yyyy-MM-dd')} onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' />-<input id='endDate' name='endDate' style='width: 130px' value=${fns:getBeforeDate('yyyy-MM-dd')} onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' />";
					refundBillHtml+="&nbsp;&nbsp;<input id='opeExp' class='btn btn-primary' type='submit' value='导出'/>";
					refundBillHtml+="</form></div>";
					var submitChild = function (v, h, f) {
					};
					$.jBox(refundBillHtml, { title: "Export",width:600,submit: submitChild,persistent: true});	
				});
				
		});
		
		
	</script>
</head>
<body>
  <%--  <div  id="searchContent"> 
		<form  id="inputForm" action="${ctx}/amazoninfo/amazonProduct/getProductPrice" method="post" >
			&nbsp;&nbsp;&nbsp;&nbsp;时间:
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM'});"  readonly="readonly" class="Wdate" type="text"  id="addDate" name="date" value="<fmt:formatDate value="${productPrice.date}" pattern="yyyy-MM-dd" />"  class="input-small"/>
			<input  class="btn btn-primary"  type="submit" value="查询"/>
		</form>
	</div> --%>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		   <tr>
				<th  rowspan='2' style="text-align: center;vertical-align: middle;width:26%;">Product</th>
				<th  rowspan='2' style="text-align: center;vertical-align: middle;width:10%;">SKU</th>
				<th  colspan='2' style="text-align: center;vertical-align: middle;width:8%;">DE</th>
				<th colspan='2' style="text-align: center;vertical-align: middle;width:8%;">US</th>
				<th  colspan='2' style="text-align: center;vertical-align: middle;width:8%;">UK</th>
				<th  colspan='2' style="text-align: center;vertical-align: middle;width:8%;">FR</th>
				<th  colspan='2' style="text-align: center;vertical-align: middle;width:8%;">JP</th>
				<th  colspan='2' style="text-align: center;vertical-align: middle;width:8%;">IT</th>
				<th  colspan='2' style="text-align: center;vertical-align: middle;width:8%;">ES</th>
				<th colspan='2' style="text-align: center;vertical-align: middle;width:8%;">CA</th>
				<th colspan='2' style="text-align: center;vertical-align: middle;width:8%;">MX</th>
			</tr>
			<tr>
				<th  style="text-align: center;vertical-align: middle;">FBA</th>
				<th  style="text-align: center;vertical-align: middle;">佣金(%)</th>
				<th  style="text-align: center;vertical-align: middle;">FBA</th>
				<th  style="text-align: center;vertical-align: middle;">佣金(%)</th>
				<th  style="text-align: center;vertical-align: middle;">FBA</th>
				<th  style="text-align: center;vertical-align: middle;">佣金(%)</th>
				<th  style="text-align: center;vertical-align: middle;">FBA</th>
				<th  style="text-align: center;vertical-align: middle;">佣金(%)</th>
			    <th  style="text-align: center;vertical-align: middle;">FBA</th>
				<th  style="text-align: center;vertical-align: middle;">佣金(%)</th>
				<th  style="text-align: center;vertical-align: middle;">FBA</th>
				<th  style="text-align: center;vertical-align: middle;">佣金(%)</th>
				<th  style="text-align: center;vertical-align: middle;">FBA</th>
				<th  style="text-align: center;vertical-align: middle;">佣金(%)</th>
				<th  style="text-align: center;vertical-align: middle;">FBA</th>
				<th  style="text-align: center;vertical-align: middle;">佣金(%)</th>
				<th  style="text-align: center;vertical-align: middle;">FBA</th>
				<th  style="text-align: center;vertical-align: middle;">佣金(%)</th>
			</tr>
		</thead>
		<tbody>
			 <c:forEach items="${price}" var="price" varStatus="i"> 
				<tr>
					<td style="text-align: left;vertical-align: middle;"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${price[0]}" target="_blank">${price[0]}</a></td>
					<td style="text-align: left;vertical-align: middle;">${price[1]}</td>
					<td style="text-align: center;vertical-align: middle;">${'0.00' eq price[10]?'':price[10]}</td>
					<td style="text-align: center;vertical-align: middle;">${price[2]==0?'':price[2]}</td>
					<td style="text-align: center;vertical-align: middle;">${'0.00' eq  price[15]?'':price[15]}</td>
					<td style="text-align: center;vertical-align: middle;">${price[7]==0?'':price[7]}</td>
					<td style="text-align: center;vertical-align: middle;">${'0.00' eq  price[12]?'':price[12]}</td>
					<td style="text-align: center;vertical-align: middle;">${price[4]==0?'':price[4]}</td>
					<td style="text-align: center;vertical-align: middle;">${'0.00' eq  price[11]?'':price[11]}</td>
					<td style="text-align: center;vertical-align: middle;">${price[3]==0?'':price[3]}</td>
					<td style="text-align: center;vertical-align: middle;">${'0.00' eq  price[17]?'':price[17]}</td>
					<td style="text-align: center;vertical-align: middle;">${price[9]==0?'':price[9]}</td>
					<td style="text-align: center;vertical-align: middle;">${'0.00' eq  price[14]?'':price[14]}</td>
					<td style="text-align: center;vertical-align: middle;">${price[6]==0?'':price[6]}</td>
					<td style="text-align: center;vertical-align: middle;">${'0.00' eq  price[13]?'':price[13]}</td>
					<td style="text-align: center;vertical-align: middle;">${price[5]==0?'':price[5]}</td>
					<td style="text-align: center;vertical-align: middle;">${'0.00' eq  price[16]?'':price[16]}</td>
					<td style="text-align: center;vertical-align: middle;">${price[8]==0?'':price[8]}</td>
					
					<td style="text-align: center;vertical-align: middle;">${'0.00' eq  price[20]?'':price[20]}</td>
					<td style="text-align: center;vertical-align: middle;">${price[19]==0?'':price[19]}</td>
					
					
		        </tr> 
			</c:forEach>
		</tbody>
	</table>
	
</body>
</html>
