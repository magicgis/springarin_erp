<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>逾期提单明细</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
<style type="text/css">
.spanexr {
	float: right;
	min-height: 40px
}

.spanexl {
	float: left;
}

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
	
	$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn ){
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return parseFloat($('td:eq('+iColumn+')', tr).text().split(',').join(''));
		} );
	};
	
	$(function() {
		
		$("#supplier").click(function(){
			$("#searchForm").submit();
		});
		
		var totalAmount=0;
		var total1=0;
		var total2=0;
		var overTotal=0;
		$("#dataTable tbody tr").each(function(){
			if($(this).find(".overTotal").text().trim()!=""){
				totalAmount =totalAmount+parseFloat($(this).find(".totalAmount").text().replace(/,/g,""));
				total1 =total1+parseFloat($(this).find(".total1").text().replace(/,/g,""));
				total2 =total2+parseFloat($(this).find(".total2").text().replace(/,/g,""));
				overTotal =overTotal+parseFloat($(this).find(".overTotal").text().replace(/,/g,""));
				
			}
		});
		
		$("#totalAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(totalAmount,0,true)+"</b>");
		$("#total1").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(total1,0,true)+"</b>");
		$("#total2").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(total2,0,true)+"</b>");
		$("#overTotal").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(overTotal,0,true)+"</b>");


		$("#dataTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
								 null,
						         null,	
						         null,
							     { "sSortDataType":"dom-html", "sType":"numeric" },
						         null,
							     { "sSortDataType":"dom-html", "sType":"numeric" },
						         null,
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
						         null
							],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
					if(iDisplayIndex==0){
						totalAmountPage=0;
						totalOver1Page=0;
						totalOver2Page=0;
						overTotalPage=0;
					}
					if($.isNumeric(aData[3])){
						totalAmountPage += parseFloat(aData[3]);//第几列
		            } else {
		            	totalAmountPage += parseFloat(aData[3].split(',').join(''));//第几列
		            }
					if($.isNumeric(aData[5])){
						totalOver1Page += parseFloat(aData[5]);//第几列
		            } else {
		            	totalOver1Page += parseFloat(aData[5].split(',').join(''));//第几列
		            }
					if($.isNumeric(aData[7])){
						totalOver2Page += parseFloat(aData[7]);//第几列
		            } else {
		            	totalOver2Page += parseFloat(aData[7].split(',').join(''));//第几列
		            }
					if($.isNumeric(aData[8])){
						overTotalPage += parseFloat(aData[8]);//第几列
		            } else {
		            	overTotalPage += parseFloat(aData[8].split(',').join(''));//第几列
		            }
					$("#totalAmountPage").html("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(totalAmountPage, 0, true));
					$("#totalOver1Page").html("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(totalOver1Page, 0, true));
					$("#totalOver2Page").html("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(totalOver2Page, 0, true));
					$("#overTotalPage").html("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>"+formatNumber(overTotalPage, 0, true));
				},
				"fnPreDrawCallback": function( oSettings ) { 
					$("#totalAmountPage").html(0);
					$("#totalOver1Page").html(0);
					$("#totalOver2Page").html(0);
					$("#overTotalPage").html(0);
				}
		});
	});
	
	function formatNumber(num, cent, isThousand) {
		num = num.toString().replace(/\$|\,/g, '');

		// 检查传入数值为数值类型  
		if (isNaN(num))
			num = "0";

		// 获取符号(正/负数)  
		sign = (num == (num = Math.abs(num)));

		num = Math.floor(num * Math.pow(10, cent) + 0.50000000001); // 把指定的小数位先转换成整数.多余的小数位四舍五入  
		cents = num % Math.pow(10, cent); // 求出小数位数值  
		num = Math.floor(num / Math.pow(10, cent)).toString(); // 求出整数位数值  
		cents = cents.toString(); // 把小数位转换成字符串,以便求小数位长度  

		// 补足小数位到指定的位数  
		while (cents.length < cent)
			cents = "0" + cents;

		if (isThousand) {
			// 对整数部分进行千分位格式化.  
			for ( var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ','
						+ num.substring(num.length - (4 * i + 3));
		}

		if (cent > 0)
			return (((sign) ? '' : '-') + num + '.' + cents);
		else
			return (((sign) ? '' : '-') + num);
	}
</script>
</head>
<body>
    <ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/lcPurchaseOrder/capitalBudget">(理诚)资金分配计划</a></li>
		<li class="active"><a href="${ctx}/psi/lcPurchaseOrder/overLadDetail">(理诚)逾期付款收货单明细</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPurchaseOrder" action="${ctx}/psi/lcPurchaseOrder/overLadDetail" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
		<label>供应商：</label>
		<select style="width:150px;" id="supplier" name="supplierId" >
			<c:forEach items="${allSupplierMap}" var="supplierEntry" varStatus="i">
				 <option value="${supplierEntry.key}" ${supplierEntry.key eq supplierId?'selected':'' }>${supplierEntry.value.nikename}</option>;
			</c:forEach>
		</select>
		<c:if test="${not empty depositMap[supplierId] }">
			&nbsp;&nbsp;
			<span style="color:red">定金逾期：
				<b>
					<span style='color:#EAC100;padding-left:5px;font-size:16px'>¥</span>
					<fmt:formatNumber value="${depositMap[supplierId] }" pattern=",###.00"  />
				</b>
			</span>
		</c:if>
	</form:form>
	<div id="contentTbDiv" style="margin: auto">
		<div>
			<table id="dataTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>No.</th>
						<th>收货单号</th>
						<th>SKU</th>
						<th>总金额</th>
						<th>尾款一期比例</th>
						<th>尾款一期逾期金额</th>
						<th>尾款二期比例</th>
						<th>尾款二期逾期金额</th>
						<th>逾期总金额</th>
						<th>应付款时间</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${overList}" var="op" varStatus="i">
						<tr>
							<c:set var="total1" value="0"/>
							<c:set var="total2" value="0"/>
							<c:set var="totalPay" value="${op[8] }"/>
							<td>${i.count}</td>
							<td><a target="_blank" href="/springrain-erp/a/psi/lcPsiLadingBill/view?id=${op[0]}">${op[1]}</a></td>
							<td>${op[11]}</td>
							<td class="totalAmount"><fmt:formatNumber value="${op[7]}" pattern=",###.##"  /></td>
							<td>${op[5]}%</td>
							<c:if test="${op[3] > 0}">
								<c:set var="total1" value="${op[7] * op[5]/100}"/>
								<c:if test="${totalPay > total1 }">
									<c:set var="totalPay" value="${totalPay - total1 }"/>
									<c:set var="total1" value="0"/>
								</c:if>
								<c:if test="${totalPay <= total1 }">
									<c:set var="total1" value="${total1 - totalPay }"/>
								</c:if>
							</c:if>
							<td class="total1">
								<fmt:formatNumber value="${total1}" pattern=",###.##"  />
							</td>
							<td>${op[6]}%</td>
							<c:if test="${op[4] > 0}">
								<c:set var="total2" value="${op[7] * op[6]/100 - totalPay}"/>
							</c:if>
							<td class="total2">
								<fmt:formatNumber value="${total2}" pattern=",###.##"  />
							</td>
							<td class="overTotal">
								<fmt:formatNumber value="${total1+total2}" pattern=",###.##"  />
							</td>
							<td><fmt:formatDate value="${op[2]}" pattern="yyyy-MM-dd"  /></td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr >
						<td style="font-weight: bold;">Page Total</td>
						<td></td>
						<td></td>
						<td id="totalAmountPage"></td>
						<td></td>
						<td id="totalOver1Page"></td>
						<td></td>
						<td id="totalOver2Page"></td>
						<td id="overTotalPage"></td>
						<td></td>
					</tr>
					<tr>
						<td>总计</td>
						<td></td>
						<td></td>
						<td id="totalAmount"></td>
						<td></td>
						<td id="total1"></td>
						<td></td>
						<td id="total2"></td>
						<td id="overTotal"></td>
						<td></td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
</body>
</html>
