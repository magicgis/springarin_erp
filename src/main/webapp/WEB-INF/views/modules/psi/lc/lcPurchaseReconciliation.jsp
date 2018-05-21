<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>对账</title>
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
			
			var totalAmount = 0 ;
			var totalPaymentAmount = 0 ;
			var totalDepositAmount=0;
			var unPayment=0;
			$(".totalAmount").each(function(){
				totalAmount =totalAmount+parseFloat($(this).text().replace(/,/g,""));
			});
			
			$(".totalDepositAmount").each(function(){
				totalDepositAmount =totalDepositAmount+parseFloat($(this).text().replace(/,/g,""));
			});
			
			$(".totalPaymentAmount").each(function(){
				totalPaymentAmount =totalPaymentAmount+parseFloat($(this).text().replace(/,/g,""));
			});
			$(".unPayment").each(function(){
				unPayment =unPayment+parseFloat($(this).text().replace(/,/g,""));
			});   
			
			$("#totalAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(totalAmount,2,true)+"</b>");
			$("#totalDepositAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(totalDepositAmount,2,true)+"</b>");
			$("#totalPaymentAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(totalPaymentAmount,2,true)+"</b>");
			$("#unPayment").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(unPayment,2,true)+"</b>");
			
			$("#supplier").change(function(){
				$("#searchForm").submit();
			});
			
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
			          null,
			          { "sSortDataType":"dom-html", "sType":"numeric" },
				      { "sSortDataType":"dom-html", "sType":"numeric" },
				      { "sSortDataType":"dom-html", "sType":"numeric" },
				      { "sSortDataType":"dom-html", "sType":"numeric" },
				      null
				],
				"ordering" : true,
				 "aaSorting": [[ 3, "desc" ]]
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
     <shiro:hasPermission name="psi:purchaseOrder:accounts">
    	<li ><a href="${ctx}/psi/purchaseOrder/reconciliation">对账</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/fReport">采购支付报表</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/capitalBudget">资金分配计划</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/overDetail">逾期付款详细</a></li>
		  
		<li class="active"><a href="${ctx}/psi/lcPurchaseOrder/reconciliation">(理诚)对账</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/fReport">(理诚)采购支付报表</a></li>
	</shiro:hasPermission>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/capitalBudget">(理诚)资金分配计划</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/overDetail">(理诚)逾期付款详细</a></li>
		<shiro:hasPermission name="amazoninfo:sale:accountBalance">
			<li ><a href="${ctx}/psi/purchaseOrder/accountBalance">亚马逊到款详情</a></li>
		</shiro:hasPermission>
		<li><a href="${ctx}/psi/purchaseOrder/importDutyCount">关税增值税资金计划</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/lcPurchaseOrder/reconciliation" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<option value="" ${purchaseOrder.supplier.id eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}' ${purchaseOrder.supplier.id eq supplier.id ?'selected':'' }>${supplier.nikename}</option>;
				</c:forEach>
			</select>
		<label>下单时间：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${purchaseOrder.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="purchaseDate" value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
					<label class="alert alert-info" >Tips：由于本页只统计“生产”状态以后的订单</label>
	</form:form>
	<tags:message content="${message}"/>   
	
	
	<div style="${mobilCss}">
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead><tr>
		<th width="2%">序号</th>
		<th width="5%">供应商</th><th width="5%">采购总金额</th><th width="5%">已支付定金</th><th width="5%">已支付总金额</th><th width="5%">未支付金额</th><th width="20%">操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="purchaseOrder" varStatus="i">
		<tr>
			<td>${i.index+1}</td>
			<td><a href="${ctx}/psi/lcPurchaseOrder/lessCargoList?supplier.id=${purchaseOrder.supplier.id}">${purchaseOrder.supplier.nikename}</a></td>
			<td class="totalAmount"><fmt:formatNumber value="${purchaseOrder.totalAmount}" pattern=",###" maxFractionDigits="2" /> </td>
			<td class="totalDepositAmount"><fmt:formatNumber value="${purchaseOrder.depositAmount}" pattern=",###" maxFractionDigits="2" /> </td>
			<td class="totalPaymentAmount"><fmt:formatNumber value="${purchaseOrder.totalPaymentAmount}" pattern=",###" maxFractionDigits="2" /></td>
			<td class="unPayment"><fmt:formatNumber value="${purchaseOrder.unPayment}" pattern="#,##0"  /></td>
			<td><a class="btn btn-small" href="${ctx}/psi/lcPurchaseOrder/lessCargoList?supplier.id=${purchaseOrder.supplier.id}">欠货明细</a>
			&nbsp;&nbsp;&nbsp;&nbsp;
			 <a class="btn btn-small" href="${ctx}/psi/lcPsiLadingBill/receiptCargoList?supplier.id=${purchaseOrder.supplier.id}">收货明细</a>
		</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td></td>
				<td>合计</td>
				<td id="totalAmount"></td>
				<td id="totalDepositAmount"></td>
				<td id="totalPaymentAmount"></td>
				<td id="unPayment"></td>
				<td colspan="1"></td>
			</tr>
		</tfoot>
	</table>
	</div>
</body>
</html>
