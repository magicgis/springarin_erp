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
			
			var overAmount=0;
			var unOver1=0;
			var unReceived1=0;
			var unOver2=0;
			var unReceived2=0;
			var unOver3=0;
			var unReceived3=0;
			var unOver4=0;
			var unReceived4=0;
			var unOver5=0;
			var unReceived5=0;
			var unOver6=0;
			var unReceived6=0;
			var unOver7=0;
			var unReceived7=0;
			var unOver8=0;
			var unReceived8=0;
			var total=0;
			$("#dataTable tbody tr").each(function(){
				if($(this).find(".overAmount").text().trim()!=""){
					overAmount =overAmount+parseFloat($(this).find(".overAmount").text().replace(/,/g,""));
				}
				var td =$(this).find(".overAmount").next();
				if(td.text().trim()!=""){
					unOver1 =unOver1+parseFloat(td.text().replace(/,/g,""));
				}
				var td1=td.next();
				if(td1.text().trim()!=""){
					unReceived1 =unReceived1+parseFloat(td1.text().replace(/,/g,""));
				}
				
				var td2=td1.next();
				if(td2.text().trim()!=""){
					unOver2 =unOver2+parseFloat(td2.text().replace(/,/g,""));
				}
				
				var td3=td2.next();
				if(td3.text().trim()!=""){
					unReceived2 =unReceived2+parseFloat(td3.text().replace(/,/g,""));
				}
				
				var td4=td3.next();
				if(td4.text().trim()!=""){
					unOver3 =unOver3+parseFloat(td4.text().replace(/,/g,""));
				}
				
				var td5=td4.next();
				if(td5.text().trim()!=""){
					unReceived3 =unReceived3+parseFloat(td5.text().replace(/,/g,""));
				}
				
				var td6=td5.next();
				if(td6.text().trim()!=""){
					unOver4 =unOver4+parseFloat(td6.text().replace(/,/g,""));
				}
				
				var td7=td6.next();
				if(td7.text().trim()!=""){
					unReceived4 =unReceived4+parseFloat(td7.text().replace(/,/g,""));
				}
				
				var td8=td7.next();
				if(td8.text().trim()!=""){
					unOver5 =unOver5+parseFloat(td8.text().replace(/,/g,""));
				}
				
				var td9=td8.next();
				if(td9.text().trim()!=""){
					unReceived5 =unReceived5+parseFloat(td9.text().replace(/,/g,""));
				}
				
				var td10=td9.next();
				if(td10.text().trim()!=""){
					unOver6 =unOver6+parseFloat(td10.text().replace(/,/g,""));
				}
				
				var td11=td10.next();
				if(td11.text().trim()!=""){
					unReceived6 =unReceived6+parseFloat(td11.text().replace(/,/g,""));
				}
				
				var td12=td11.next();
				if(td12.text().trim()!=""){
					unOver7 =unOver7+parseFloat(td12.text().replace(/,/g,""));
				}
				
				var td13=td12.next();
				if(td13.text().trim()!=""){
					unReceived7 =unReceived7+parseFloat(td13.text().replace(/,/g,""));
				}
				
				var td14=td13.next();
				if(td14.text().trim()!=""){
					unOver8 =unOver8+parseFloat(td14.text().replace(/,/g,""));
				}
				
				var td15=td14.next();
				if(td15.text().trim()!=""){
					unReceived8 =unReceived8+parseFloat(td15.text().replace(/,/g,""));
				}
				
				var td16=td15.next();
				if(td16.text().trim()!=""){
					total =total+parseFloat(td16.text().replace(/,/g,""));
				}
			});
			
			$("#overAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(overAmount,0,true)+"</b>");
			$("#unOver1").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unOver1,0,true)+"</b>");
			$("#unReceived1").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unReceived1,0,true)+"</b>");
			$("#unOver2").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unOver2,0,true)+"</b>");
			$("#unReceived2").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unReceived2,0,true)+"</b>");
			$("#unOver3").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unOver3,0,true)+"</b>");
			$("#unReceived3").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unReceived3,0,true)+"</b>");
			$("#unOver4").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unOver4,0,true)+"</b>");
			$("#unReceived4").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unReceived4,0,true)+"</b>");
			$("#unOver5").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unOver5,0,true)+"</b>");
			$("#unReceived5").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unReceived5,0,true)+"</b>");
			$("#unOver6").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unOver6,0,true)+"</b>");
			$("#unReceived6").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unReceived6,0,true)+"</b>");
			$("#unOver7").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unOver7,0,true)+"</b>");
			$("#unReceived7").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unReceived7,0,true)+"</b>");
			$("#unOver8").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unOver8,0,true)+"</b>");
			$("#unReceived8").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(unReceived8,0,true)+"</b>");
			$("#total").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$</span>"+formatNumber(total,0,true)+"</b>");
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
		
		function overDetail(supplierId){
			window.location.href="${ctx}/psi/purchaseOrder/overDetail?supplierId="+supplierId;
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
		<li ><a href="${ctx}/psi/purchaseOrder/reconciliation">对账</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/fReport">采购支付报表</a></li>
		<li class="active"><a href="${ctx}/psi/purchaseOrder/capitalBudget">资金分配计划</a></li>
		<li><a href="${ctx}/psi/purchaseOrder/overDetail">逾期付款详细</a></li>
		
		<li ><a href="${ctx}/psi/lcPurchaseOrder/reconciliation">(理诚)对账</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/fReport">(理诚)采购支付报表</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/capitalBudget">(理诚)资金分配计划</a></li>
		<li><a href="${ctx}/psi/lcPurchaseOrder/overDetail">(理诚)逾期付款详细</a></li>
		
		<shiro:hasPermission name="amazoninfo:sale:accountBalance">
			<li ><a href="${ctx}/psi/purchaseOrder/accountBalance">亚马逊到款详情</a></li>
		</shiro:hasPermission>
		<li><a href="${ctx}/psi/purchaseOrder/importDutyCount">关税增值税资金计划</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/capitalBudget" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplierId" >
				<option value="">全部</option>
				<c:forEach items="${allSupplierMap}" var="supplierEntry" varStatus="i">
					 <option value="${supplierEntry.key}" ${supplierEntry.key eq supplierId?'selected':'' }>${supplierEntry.value.nikename}</option>;
				</c:forEach>
			</select>
	</form:form>
	<tags:message content="${message}"/>   
	
		<div style="${mobilCss}">
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead>
		<tr>
			<th width="5%" rowspan="2">供应商</th><th width="5%"  rowspan="2">已逾期金额</th>
			<c:forEach items="${weeks}" var="week">
				<th width="10%" colspan="2">${weekStartMap[week]}W</th>
			</c:forEach>
			<th width="10%"  rowspan="2">Total</th>
		</tr>
		<tr>
			<c:forEach items="${weeks}" var="week">
				<th width="5%">尾款</th><th width="5%">未收货</th>
			</c:forEach>
		</tr>
		</thead>
			<tr>
				<td>合计</td>
				<td id="overAmount"></td>
				<td id="unOver1"></td>
				<td id="unReceived1"></td>
					<td id="unOver2"></td>
				<td id="unReceived2"></td>
					<td id="unOver3"></td>
				<td id="unReceived3"></td>
					<td id="unOver4"></td>
				<td id="unReceived4"></td>
					<td id="unOver5"></td>
				<td id="unReceived5"></td>
					<td id="unOver6"></td>
				<td id="unReceived6"></td>
				<td id="unOver7"></td>
				<td id="unReceived7"></td>
				<td id="unOver8"></td>
				<td id="unReceived8"></td>
				<td id="total"></td>
			</tr>
		<tbody>
		<c:forEach items="${supplierMap}" var="supplierEntry" varStatus="i">
			<tr>
				<td >${supplierEntry.value.nikename}</td>
				<c:set var="supplierTotal" value="0" />
				<c:if test="${not empty ladingMap[supplierEntry.key] || not empty orderMap[supplierEntry.key]}">
					<td class="overAmount">
					<a href="#" onclick="return overDetail(${supplierEntry.key})"><fmt:formatNumber value="${ladingMap[supplierEntry.key]['over']}" pattern=",###"  /></a>
					<c:set var="supplierTotal" value="${supplierTotal+ladingMap[supplierEntry.key]['over']}" />
					</td>
					<c:forEach items="${weeks}" var="week">
						<td>
								<fmt:formatNumber value="${ladingMap[supplierEntry.key][week]}" pattern=",###"  />
								<c:set var="supplierTotal" value="${supplierTotal+ladingMap[supplierEntry.key][week]}" />
						</td>
						<td>
								<fmt:formatNumber value="${orderMap[supplierEntry.key][week]}" pattern=",###"  />
								<c:set var="supplierTotal" value="${supplierTotal+orderMap[supplierEntry.key][week]}" />
						</td>
					</c:forEach>
				</c:if>
				<td><fmt:formatNumber value="${supplierTotal}" pattern=",###"  /></td>   
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
</body>
</html>
