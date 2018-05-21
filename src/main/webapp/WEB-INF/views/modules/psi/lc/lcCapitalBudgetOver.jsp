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
			
			$("#supplier").click(function(){
				$("#searchForm").submit();
			});
			
			var overTotal=0;
			var fourTotal=0;
			var threeTotal=0;
			var twoTotal=0;
			var oneTotal=0;
			
			$("#dataTable tbody tr").each(function(){
				if($(this).find(".overTotal").text().trim()!=""){
					overTotal =overTotal+parseFloat($(this).find(".overTotal").text().replace(/,/g,""));
				}
				var td =$(this).find(".overTotal").next();
				if(td.text().trim()!=""){
					fourTotal =fourTotal+parseFloat(td.text().replace(/,/g,""));
				}
				var td1=td.next();
				if(td1.text().trim()!=""){
					threeTotal =threeTotal+parseFloat(td1.text().replace(/,/g,""));
				}
				
				var td2=td1.next();
				if(td2.text().trim()!=""){
					twoTotal =twoTotal+parseFloat(td2.text().replace(/,/g,""));
				}
				
				var td3=td2.next();
				if(td3.text().trim()!=""){
					oneTotal =oneTotal+parseFloat(td3.text().replace(/,/g,""));
				}
			});
			
			$("#overTotal").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(overTotal,0,true)+"</b>");
			$("#fourTotal").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(fourTotal,0,true)+"</b>");
			$("#threeTotal").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(threeTotal,0,true)+"</b>");
			$("#twoTotal").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(twoTotal,0,true)+"</b>");
			$("#oneTotal").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(oneTotal,0,true)+"</b>");
		});
		
		
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
			
			<li ><a href="${ctx}/psi/lcPurchaseOrder/reconciliation">(理诚)对账</a></li>
			<li ><a href="${ctx}/psi/lcPurchaseOrder/fReport">(理诚)采购支付报表</a></li>
		</shiro:hasPermission>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/capitalBudget">(理诚)资金分配计划</a></li>
		<li class="active"><a href="${ctx}/psi/lcPurchaseOrder/overDetail">(理诚)逾期付款详细</a></li>
		<shiro:hasPermission name="amazoninfo:sale:accountBalance">
			<li ><a href="${ctx}/psi/purchaseOrder/accountBalance">亚马逊到款详情</a></li>
		</shiro:hasPermission>
		<li><a href="${ctx}/psi/purchaseOrder/importDutyCount">关税增值税资金计划</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPurchaseOrder" action="${ctx}/psi/lcPurchaseOrder/overDetail" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplierId" >
				<option value="">全部</option>
				<c:forEach items="${allSupplierMap}" var="supplierEntry" varStatus="i">
					 <option value="${supplierEntry.key}" ${supplierEntry.key eq supplierId?'selected':'' }>${supplierEntry.value.nikename}</option>;
				</c:forEach>
			</select>
	</form:form>
	<div style="${mobilCss}">
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th width="5%" >供应商</th>
				<th width="5%" >逾期Total</th>
				<c:forEach items="${months}" var="month">
					<th width="5%">${month}</th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${overMap}" var="overEntry" varStatus="i">
			<tr style="background-color:${supplierId eq overEntry.key?'#f5f5f5':''}">
				<td >${allSupplierMap[overEntry.key].nikename}</td>
				<td class="overTotal"><fmt:formatNumber value="${overEntry.value['total']}" pattern=",###" /></td>
				<c:set var="monthTotal" value="0"/>
				<c:forEach items="${months}" var="month">
					<c:if test="${firstMonth ne month}">
						<c:set var="monthTotal" value="${monthTotal+overEntry.value[month]}"/>
					</c:if>
				</c:forEach>
				<c:forEach items="${months}" var="month">
				<c:choose>
					<c:when test="${firstMonth eq month}">
						<td>
							<c:if test="${(overEntry.value['total']-monthTotal)>=1}">
								<fmt:formatNumber value="${overEntry.value['total']-monthTotal}" pattern=",###" />
							</c:if>
						</td>
					</c:when>
					<c:otherwise>
						<td><fmt:formatNumber value="${overEntry.value[month]}" pattern=",###" /></td>
					</c:otherwise>
				</c:choose>
				</c:forEach>
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td rowspan="2">合计</td>
				<td id="overTotal"></td>
				<td id="fourTotal"></td>
				<td id="threeTotal"></td>
				<td id="twoTotal"></td>
				<td id="oneTotal"></td>
			</tr>
		</tfoot>
	</table>
	</div>
</body>
</html>
