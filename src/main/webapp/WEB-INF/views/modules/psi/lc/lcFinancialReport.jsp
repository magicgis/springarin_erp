<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购财务报表</title>
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
			
			$("#supplier,#orderNo").click(function(){
				$("#searchForm").submit();
			});
			
			$("#export").click(function(){
				var params = {};
				params.startMonth=$("input[name='startMonth']").val();
				params.endMonth=$("input[name='endMonth']").val();
				params['proNameColor']=$("#orderNo").val();
				window.location.href = "${ctx}/psi/lcPurchaseOrder/fReportExport?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$("#reset").click(function(){
				$("#supplier").val("");
				$("#orderNo").val("");
				$("#searchForm").submit();
			});
			
			var orderAmount = 0 ;
			var orderPayAmount = 0 ;
			var ladingPayAmount = 0 ;
			var bothPayAmount =0;
			var unPayAmount=0;
			$(".orderAmount").each(function(){
				orderAmount =orderAmount+parseFloat($(this).text().replace(/,/g,""));
			});
			
			$(".orderPayAmount").each(function(){
				orderPayAmount =orderPayAmount+parseFloat($(this).text().replace(/,/g,""));
			});
			
			$(".ladingPayAmount").each(function(){
				ladingPayAmount =ladingPayAmount+parseFloat($(this).text().replace(/,/g,""));
			});
			
			$(".bothPayAmount").each(function(){
				bothPayAmount =bothPayAmount+parseFloat($(this).text().replace(/,/g,""));
			});
			
			unPayAmount=parseFloat($(".unPayment:last").text().replace(/,/g,""));
			
			$("#orderAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(orderAmount,2,true)+"</b>");
			$("#orderPayAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(orderPayAmount,2,true)+"</b>");
			$("#ladingPayAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(ladingPayAmount,2,true)+"</b>");
			$("#bothPayAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(bothPayAmount,2,true)+"</b>");
			$("#unPayAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(unPayAmount,2,true)+"</b>");
			
			$("a[rel='popover']").popover({trigger:'hover',container: 'body'});
			
			$("#dataTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"aoColumns": [
			          null,
			          { "sSortDataType":"dom-html", "sType":"numeric" },
				      { "sSortDataType":"dom-html", "sType":"numeric" },
				      { "sSortDataType":"dom-html", "sType":"numeric" },
				      { "sSortDataType":"dom-html", "sType":"numeric" },
				      { "sSortDataType":"dom-html", "sType":"numeric" }
				],
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
		
			
			
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
		
		
		  function formatNumber(num,cent,isThousand) {  
		        num = num.toString().replace(/\$|\,/g,'');  
		  
		        // 检查传入数值为数值类型  
		          if(isNaN(num))  
		            num = "0";  
		  
		        // 获取符号(正/负数)  
		        sign = (num == (num = Math.abs(num)));  
		  
		        num = Math.floor(num*Math.pow(10,cent)+0.50000000001);  // 把指定的小数位先转换成整数.多余的小数位四舍五入  
		        cents = num%Math.pow(10,cent);              // 求出小数位数值  
		        num = Math.floor(num/Math.pow(10,cent)).toString();   // 求出整数位数值  
		        cents = cents.toString();               // 把小数位转换成字符串,以便求小数位长度  
		  
		        // 补足小数位到指定的位数  
		        while(cents.length<cent)  
		          cents = "0" + cents;  
		  
		        if(isThousand) {  
		          // 对整数部分进行千分位格式化.  
		          for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)  
		            num = num.substring(0,num.length-(4*i+3))+','+ num.substring(num.length-(4*i+3));  
		        }  
		  
		        if (cent > 0)  
		          return (((sign)?'':'-') + num + '.' + cents);  
		        else  
		          return (((sign)?'':'-') + num);  
		      } 
		
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
    	<li><a href="${ctx}/psi/purchaseOrder/reconciliation">对账</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/fReport">采购支付报表</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/capitalBudget">资金分配计划</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/overDetail">逾期付款详细</a></li>
		
		
		<li ><a href="${ctx}/psi/lcPurchaseOrder/reconciliation">(理诚)对账</a></li>
		<!-- <li ><a href="${ctx}/psi/lcPurchaseOrder/reconciliation2">资金计划</a></li> <li ><a href="${ctx}/psi/lcPurchaseOrder/forecast">采购资金计划</a></li>-->
		<li class="active"><a href="#">(理诚)采购支付报表</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/capitalBudget">(理诚)资金分配计划</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/overDetail">(理诚)逾期付款详细</a></li>
		<shiro:hasPermission name="amazoninfo:sale:accountBalance">
			<li ><a href="${ctx}/psi/purchaseOrder/accountBalance">亚马逊到款详情</a></li>
		</shiro:hasPermission>
		<li><a href="${ctx}/psi/purchaseOrder/importDutyCount">关税增值税资金计划</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPurchaseOrder" action="${ctx}/psi/lcPurchaseOrder/fReport" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id" ${supplierDisabled eq '1'?'disabled':'' }>
				<option value="">全部</option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}' ${supplier.id eq lcPurchaseOrder.supplier.id?'selected':'' }>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>产品：</label>
			<select style="width:180px;" name="orderNo" id="orderNo" ${productDisabled eq '1'?'disabled':'' }>
				<option value="">全部</option>
				<c:forEach items="${proNameColors}" var="proNameColor">
					<option value="${proNameColor}" ${proNameColor eq lcPurchaseOrder.orderNo?'selected':''}>${proNameColor}</option>
				</c:forEach>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>日期：</label>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startMonth" value="${startMonth}" class="input-small" id="startMonth"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endMonth" value="${endMonth}" class="input-small" id="endMonth"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="reset" class="btn btn-warning" type="reset" value="重置"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="export" class="btn btn-success" type="button" value="导出订单总金额"/>
	</form:form>
	<tags:message content="${message}"/>   
	
	<div style="${mobilCss}">
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead><tr>
			<th width="5%">月份</th>
			<th width="5%">订单总金额</th>
			<th width="5%">实际支付定金</th>
			<th width="5%">实际支付尾款</th>
			<th width="5%"><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="定金+尾款">实际支付总额</a></th>
			<th width="5%"><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="上月未付+本月订单总金额-实际支付金额">未支付(截止到月末)</a></th>
		</tr></thead>
		<tbody>
		<c:forEach items="${financialDtos}" var="dto" varStatus="i">
		<tr>
			<td>${dto.month}</td>
			<td class="orderAmount"><fmt:formatNumber value="${dto.orderAmount}" pattern=",###" maxFractionDigits="2" /> </td>
			<td class="orderPayAmount"><fmt:formatNumber value="${dto.payOrderAmount}" pattern=",###" maxFractionDigits="2" /></td>
			<td class="ladingPayAmount"><fmt:formatNumber value="${dto.payLadingAmount}" pattern=",###" maxFractionDigits="2" /></td>
			<td class="bothPayAmount"><fmt:formatNumber value="${dto.allPayment}" pattern=",###" maxFractionDigits="2" /></td>
			<td class="unPayment"><fmt:formatNumber value="${dto.upPayAmount}" pattern=",###" maxFractionDigits="2" /></td>
		</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td>合计</td>
				<td id="orderAmount"></td>
				<td id="orderPayAmount"></td>
				<td id="ladingPayAmount"></td>
				<td id="bothPayAmount"></td>
				<td id="unPayAmount"></td>   
			</tr>
		</tfoot>
	</table>
	</div>
</body>
</html>
