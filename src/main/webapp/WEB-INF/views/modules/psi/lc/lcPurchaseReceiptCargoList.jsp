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
			$("#lessCargo").click(function(e){
				var supplierId=$("#supplier").val();
				$("#searchForm").attr("action","${ctx}/psi/lcPurchaseOrder/lessCargoList?supplier.id="+supplierId+"");
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
				$("#searchForm").attr("action","${ctx}/psi/lcPsiLadingBill/receiptCargoList?supplier.id="+$(this).val()+"");
				$("#searchForm").submit();
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
		<li ><a href="#" id="lessCargo">欠货明细</a></li>
		<li class="active"><a href="#">收货明细</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiLadingBill" action="${ctx}/psi/lcPsiLadingBill/receiptCargoList" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
	<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}'>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			<script type="text/javascript">
			$("option[value='${psiLadingBill.supplier.id}']").attr("selected","selected");	
			</script>
			&nbsp;&nbsp;&nbsp;&nbsp;  
	<label>确认日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiLadingBill.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="sureDate" value="<fmt:formatDate value="${psiLadingBill.sureDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;<input name="billSta" type="hidden" value="${psiLadingBill.billSta}" />
		<input id="count" class="btn btn-primary" type="button" value="计算勾选货品装箱体积和毛重"/>
	</form:form>
	
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr>
		<th width="1%"><input type="checkbox" id="checkAll" /></th>
		<th width="2%">序号</th>
		<th  colspan="2" width="10%">产品名称</th><th width="5%">收货数量</th><th width="5%">装箱体积(m³)</th><th width="5%">毛重(kg)</th><th colspan="3" width="20%">操作</th></tr></thead>
		<tbody>
		<c:forEach items="${productMap}" var ="billEntry" varStatus="i">
			<tr>
				<td><input type="checkbox" class="checkPros" /></td>
				<td>${i.index+1}</td>
				<td  colspan="2">${billEntry.key}</td>
				<td class="totalAmount">${billEntry.value.tempQuantity}</td>
				<td class="volume"><fmt:formatNumber value="${billEntry.value.volume }" minFractionDigits="3"/></td>
				<td class="weight"><fmt:formatNumber value="${billEntry.value.weight }" minFractionDigits="3"/></td>
				<td colspan="3" align="center">
				<input type="hidden" value="${billEntry.key}"/>
				<a class="btn btn-small btn-info open">概要</a></td>
			</tr>
			<tr style="background-color:#D2E9FF;display: none" name="${billEntry.key}"><td></td><td></td><td>国家</td><td>颜色</td><td>收货数量</td><td>装箱体积(m³)</td><td>毛重(kg)</td><td>提单号</td><td>确认日期</td>	</tr>
			<c:forEach items="${billEntry.value.items}" var="item">
				<tr style="background-color:#D2E9FF;display: none" name="${item.productName }">
				<td><input type="checkbox" class="checkPro" /></td><td></td>
				<td>${fns:getDictLabel(item.countryCode, 'platform', '')}</td><td>${item.colorCode }</td>
				<td>${item.quantityLading }</td>
				<td class="itemVolume"><fmt:formatNumber value="${item.volume}" minFractionDigits="3"/></td>
				<td class="itemWeight"><fmt:formatNumber value="${item.weight}" minFractionDigits="3"/></td>
				<td><a target="_blank" href="${ctx}/psi/lcPsiLadingBill/view?id=${item.ladingBill.id}">${item.ladingBill.billNo }</a></td>	
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${item.ladingBill.sureDate}"/></td>
				</tr>
			</c:forEach>
		</c:forEach>
		<tr>
			<td></td>
			<td colspan="4">合计</td>
			<td id="totleV"></td>
			<td id="totleW"></td>
			<td colspan="5"></td>
		</tr>
		</tbody>
	</table>
</body>
</html>
