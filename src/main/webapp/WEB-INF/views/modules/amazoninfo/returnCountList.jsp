<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Return Count</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
		.spanexr{ margin-right:125px;float:right;min-height:40px}
		.spanexl{ margin-left:125px;float:left;}
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
			var returnAmount = 0 ;
			var orderAmount = 0 ;
			$(".returnAmount").each(function(){
				returnAmount =returnAmount+parseFloat($(this).text()==""?0:$(this).text());
			});
			$(".orderAmount").each(function(){
				orderAmount =orderAmount+parseFloat($(this).text()==""?0:$(this).text());
			});
		
			$("#returnAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>"+returnAmount+"<span></b>");
			$("#orderAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>"+orderAmount+"<span></b></b>");
			
			   $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType": "bootstrap",
					"iDisplayLength": 15,
					"aLengthMenu":[[10, 20, 30,100,-1], [10, 20 , 30, 100, "All"]],
				 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
				 	, "aaSorting": [[3,"desc"]]
				}); 
		});
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").attr("action","${ctx}/amazoninfo/returnGoods/returnCountList");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<br/>
	<h3 style="text-align: center;vertical-align: middle">订单信息详细列表</h3>
	<table  id="contentTable" class="table table-striped table-bordered table-condensed" style="margin-left:110px;width:85%;" >
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle" width="30%">订单号</th>
				<th style="text-align: center;vertical-align: middle" width="30%">产品名</th>
				<th style="text-align: center;vertical-align: middle" width="10%">订购数量</th>
				<th style="text-align: center;vertical-align: middle" width="10%">退货数量</th>
				<th style="text-align: center;vertical-align: middle" width="20%">Reason</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page}" var="order">
			<tr>
				<td style="text-align: center;vertical-align: middle"><a href="${ctx}/amazoninfo/order/form?amazonOrderId=${order[0]}" target="_blank">${order[0]}</a></td>
				<td style="text-align: center;vertical-align: middle">${order[1]}</td>
				<td style="text-align: center;vertical-align: middle" class="orderAmount">${order[2]}</td>
			    <td style="text-align: center;vertical-align: middle" class="returnAmount">${order[3]}</td>
			    <td style="text-align: center;vertical-align: middle">${order[4]}</td>
			</tr>
			</c:forEach>
			
		</tbody>
		<tfoot>
			<tr><td style="text-align: center;vertical-align: middle" ></td>
				<td style="text-align: center;vertical-align: middle" >合计</td>
				<td style="text-align: center;vertical-align: middle" id="orderAmount"></td>
				<td style="text-align: center;vertical-align: middle" id="returnAmount"></td>
			    <td></td>
		    </tr> 	
			</tfoot>
		
	</table>
	
</body>
</html>
