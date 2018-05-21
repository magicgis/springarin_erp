<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>采购订单管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
			$("#receipt").click(function(e){
				var supplierId=$("#supplier").val();
				$("#searchForm").attr("action","${ctx}/psi/psiLadingBill/receiptCargoList?supplier.id="+supplierId+"");
				$("#searchForm").submit();
			});
			
			$(".open").click(function(e){
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			
			$(".checkPros").click(function(e){
				var checkedStatus = this.checked;
				var name = $(this).parent().parent().find("td:last").find("input[type='hidden']").val();
				$("*[name='"+name+"'] :checkbox").each(function(){
					this.checked = checkedStatus;
				});
			});
			
			var totleV = 0 ;
			var totleW = 0 ;
			$(".volume").each(function(){
				totleV =totleV+parseFloat($(this).text());
			});
			$(".weight").each(function(){
				totleW =totleW+parseFloat($(this).text());
			});
			$("#totleV").append("<b>"+toDecimal(totleV)+"</b>");
			$("#totleW").append("<b>"+toDecimal(totleW)+"</b>");
			
			$("#count").click(function(){
				var totleV = 0 ;
				var totleW = 0 ;
				$(":checked").parent().parent().find(".itemVolume").each(function(){
					totleV =totleV+parseFloat($(this).text());
				});
				$(":checked").parent().parent().find(".itemWeight").each(function(){
					totleW =totleW+parseFloat($(this).text());
				});
				$.jBox.alert('你勾选的货品装箱体积为:'+toDecimal(totleV)+'m³;毛重为:'+toDecimal(totleW)+'kg', '计算结果');
			});
			
			$("#supplier").change(function(){
				$("#searchForm").attr("action","${ctx}/psi/purchaseOrder/lessCargoList?supplier.id="+$(this).val()+"");
				$("#searchForm").submit();
			});
			
			
			var headStr ="";
			$(".deliveryDate").each(function(e){
				if(new Date($(this).text())<new Date()){
					var tr =$(this).parent();
					tr.css({"color":"red"});
					var productName=tr.find("td:first").find("input[type='hidden']").val();
					if(headStr.indexOf(productName+",")<0){
						headStr=headStr+productName+",";
					}
					
				};
			});
			
			$(".headTr").each(function(){
				var productName=$(this).find("td:first").find("input[type='hidden']").val();
				if(headStr.indexOf(productName+",")>=0){
					$(this).find(".isExcept").text("是");
					$(this).find(".isExcept").css({"color":"red"});
				}else{
					$(this).find(".isExcept").text("否");
				}
			});
			
		});
		
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*1000)/1000;  
	            return f;  
	     }  
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">欠货明细</a></li>
		<li ><a href="#" id="receipt">收货明细</a></li>
	</ul>
	<form:form id="searchForm" class="breadcrumb form-search" cssStyle="height: 30px;">
	<span style="float:left">
	<label>供应商：</label>
		<select style="width:150px;" id="supplier" name="supplier.id">
			<c:forEach items="${suppliers}" var="supplier" varStatus="i">
				 <option value='${supplier.id}'>${supplier.nikename}</option>;
			</c:forEach>
		</select>
		<script type="text/javascript">
		$("option[value='${supplierId}']").attr("selected","selected");	
		</script>
	</span>
		<span style="float:left; margin-left:200px">	<input id="count" class="btn btn-primary" type="button" value="计算勾选货品装箱体积和毛重"/></span>
	</form:form>
	
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr>
		<th width="1%"><input type="checkbox" id="checkAll" /></th>
		<th width="2%">序号</th>
		<th  colspan="2" width="10%">产品名称</th><th width="5%">欠货数量</th><th width="5%">收货未确认</th><th width="5%">装箱体积(m³)</th><th width="5%">毛重(kg)</th><th width="5%">已逾期</th><th colspan="3" width="20%">操作</th></tr></thead>
		<tbody>
		<c:forEach items="${productMap}" var ="orderEntry" varStatus="i">
			<tr  class="headTr">
				<td><input type="hidden" value="${orderEntry.key}"/><input type="checkbox" class="checkPros" /></td>
				<td>${i.index+1}</td>
				<td  colspan="2">${orderEntry.key}</td>
				<td >${orderEntry.value.tempLessCargoQuantity}</td>
				<td >${orderEntry.value.tempNoSureCargoQuantity}</td>
				<td class="volume"><fmt:formatNumber value="${orderEntry.value.lessCargoVolume }" minFractionDigits="3"/></td>
				<td class="weight"><fmt:formatNumber value="${orderEntry.value.lessCargoWeight }" minFractionDigits="3"/></td>
				<td class="isExcept"></td>
				<td colspan="5" align="center">	<input type="hidden" value="${orderEntry.key}"/><a class="btn btn-small btn-info open">概要</a></td>
			</tr>
			<tr style="background-color:#D2E9FF;display: none" name="${orderEntry.key}"><td></td><td></td><td>国家</td><td>颜色</td><td>欠货数量</td><td>收货未确认</td><td>装箱体积(m³)</td><td>毛重(kg)</td><td width="10%">订单号</td><td width="10%">约定交货时间</td><td width="5%">订单状态</td>	</tr>
			<c:forEach items="${orderEntry.value.items}" var="item">
				<tr style="background-color:#D2E9FF;display: none" name="${item.productName }">
				<td><input type='hidden' value="${item.productName }"/><input type="checkbox" class="checkPro" /></td><td></td>
				<td>${fns:getDictLabel(item.countryCode, 'platform', '')}</td><td>${item.colorCode }</td>
				<td>${item.quantityUnReceived }</td>
				<td>${item.quantityPreReceived }</td>
				<td class="itemVolume"><fmt:formatNumber value="${item.lessCargoVolume}" minFractionDigits="3"/></td>
				<td class="itemWeight"><fmt:formatNumber value="${item.lessCargoWeight}" minFractionDigits="3"/></td>
				<td><a target="_blank" href="${ctx}/psi/purchaseOrder/view?id=${item.purchaseOrder.id}">${item.purchaseOrder.orderNo }</a></td>	
				<td class="deliveryDate"><fmt:formatDate value="${item.deliveryDate}" pattern="yyyy-MM-dd"/></td>
				<td align="center">
					<c:if test="${item.purchaseOrder.orderSta eq '1'}"><span class="label label-important">草稿</span></c:if>
					<c:if test="${item.purchaseOrder.orderSta eq '2'}"><span class="label label-warning">生产</span></c:if>
					<c:if test="${item.purchaseOrder.orderSta eq '3'}"><span class="label label-info">部分收货</span></c:if>
					<c:if test="${item.purchaseOrder.orderSta eq '4'}"><span class="label" style="background-color:#00E3E3">已收货</span></c:if>
					<c:if test="${item.purchaseOrder.orderSta eq '5'}"><span class="label  label-success">已完成</span></c:if>
					<c:if test="${item.purchaseOrder.orderSta eq '6'}"><span class="label  label-inverse">已取消</span></c:if>
				</td>
				</tr>
			</c:forEach>
		</c:forEach>
		<tr>
			<td></td>
			<td colspan="5">合计</td>
			<td id="totleV"></td>
			<td id="totleW"></td>
			<td colspan="5"></td>
		</tr>
		</tbody>
	</table>
</body>
</html>
