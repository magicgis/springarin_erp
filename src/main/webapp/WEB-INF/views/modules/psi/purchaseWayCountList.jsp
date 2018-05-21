<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>运单试算</title>
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
			
			$(".countryHref").click(function(){
				var params ={};
				params.active=  $(".btn-group .active").attr('act');
				$('#searchForm').attr('action','${ctx}/psi/psiLadingBill/wayBillList?'+$.param(params));
				$("input[name='billNo']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$(".open").click(function(e){
				if($(this).text()=='查看订单信息'){
					$(this).text('关闭');
				}else{    
					$(this).text('查看订单信息');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			var totleV = 0 ;
			var totleW = 0 ;
			$(".itemVolume").each(function(){
				totleV =totleV+parseFloat($(this).text());
			});
			$(".itemWeight").each(function(){
				totleW =totleW+parseFloat($(this).text());
			});
			$("#totleV").append("<b>"+toDecimal(totleV)+"</b>");
			$("#totleW").append("<b>"+toDecimal(totleW)+"</b>");
			
			$("#count").click(function(){
				var totleV = 0 ;
				var totleW = 0 ;
				$(":checked").parent().parent().find(".itemVolume").each(function(){
					var rate =$(this).parent().find(".quantityLading").val()/$(this).parent().find(".oldQuantityLading").val();
					totleV =totleV+parseFloat($(this).text()*rate);
				});
				$(":checked").parent().parent().find(".itemWeight").each(function(){
					var rate =$(this).parent().find(".quantityLading").val()/$(this).parent().find(".oldQuantityLading").val();
					totleW =totleW+parseFloat($(this).text()*rate);
				});
				
				$.jBox.alert('你勾选的货品装箱体积为:'+toDecimal(totleV)+'m³;毛重为:'+toDecimal(totleW)+'kg', '计算结果');
			});
			
			
			
			$("#supplier").change(function(){
				$("#searchForm").attr("action","${ctx}/psi/psiLadingBill/wayBillList?supplier.id="+$(this).val());
				$("#searchForm").submit();
			});
			
			
			$("#country").change(function(){
				var supplierId = $("#supplier").val();
				$("#searchForm").attr("action","${ctx}/psi/psiLadingBill/wayBillList?supplier.id="+supplierId+"&billNo="+$(this).val());
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
		 
		 function CurentTime() {
	            var now = new Date();
	            var year = now.getFullYear();      
	            var month = now.getMonth() + 1;    
	            var day = now.getDate();          
	            var hh = now.getHours();           
	            var mm = now.getMinutes();         
	            var clock = year + "-";
	            if (month < 10)
	                clock += "0";
	            clock += month + "-";
	            if (day < 10)
	                clock += "0";
	            clock += day + " ";
	            if (hh < 10)
	                clock += "0";
	            clock += hh + ":";
	            if (mm < 10) clock += '0';
	            clock += mm;
	            return (clock);
	        }
	</script>
</head>
<body>

	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${psiLadingBill.billNo eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		<li class="${psiLadingBill.billNo eq 'eu' ?'active':''}"><a class="countryHref" href="#" key="eu">欧洲</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiLadingBill" action="${ctx}/psi/psiLadingBill/wayBillList" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
	<input name="billNo" type="hidden" value="${psiLadingBill.billNo}"/>
	<label>供应商：</label>
		<select style="width:150px;" id="supplier" name="supplier.id">
		<option value=""  ${psiLadingBill.supplier.id eq '' ?'selected':'' }>全部</option>
			<c:forEach items="${suppliers}" var="supplier" varStatus="i">
				 <option value="${supplier.id}" ${supplier.id eq psiLadingBill.supplier.id ?'selected':'' }>${supplier.nikename}</option>;
			</c:forEach>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;  
	<label>预计收货日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:CurentTime(),isShowClear:true,onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiLadingBill.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start" />
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="sureDate" value="<fmt:formatDate value="${psiLadingBill.sureDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;<input name="billSta" type="hidden" value="${psiLadingBill.billSta}" />
		<input id="count" class="btn btn-primary" type="button" value="计算勾选货品装箱体积和毛重"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<a href="${ctx}/psi/psiLadingBill/trial"> <input  class="btn btn-warning" type="button" value="全产品试算"/></a>
		
	</form:form>
	
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr>
		<th width="3%"><input type="checkbox" id="checkAll" /></th>
		<th width="5%">序号</th>
		<th  width="20%">产品名称</th><th width="15%">数量</th><th width="15%">试算数量</th><th width="15%">装箱体积(m³)</th><th width="15%">毛重(kg)</th><th  >操作</thead>
		<tbody>
		<c:forEach items="${productMap}" var ="billEntry" varStatus="i">
			<tr>
				<td><input type="checkbox" class="checkPros" /></td>
				<td>${i.index+1}</td>
				<td >${billEntry.key}</td>
				<td class="totalAmount"><input type="text" readonly="readonly" style="width:80%" class="oldQuantityLading" value="${billEntry.value[0]}"/></td>
				<td><input type="text" style="width:80%" class="quantityLading" value="${billEntry.value[0]}"/></td>
				<td class="itemVolume"><fmt:formatNumber value="${billEntry.value[1]}" pattern="#0.00" minFractionDigits="3"/></td>
				<td class="itemWeight"><fmt:formatNumber value="${billEntry.value[2]}" pattern="#0.00" minFractionDigits="3"/></td>
				<c:set value="${billEntry.key}" var="productNameKey"></c:set>
				<td><input type="hidden" value="${productNameKey}"/><c:if test="${not empty orderMap[productNameKey]}"><a class="btn btn-small btn-info open">查看订单信息</a></c:if>&nbsp;&nbsp;</td>
			</tr>
			<c:if test="${not empty orderMap[productNameKey]}">
			<tr style="background-color:#D2E9FF;display: none" name="${productNameKey}"><td/><td>国家</td><td>颜色</td><td>订单号</td><td>未收货数量</td><td>po交期</td><td>预计交期</td><td/></tr>
				<c:forEach items="${orderMap[productNameKey]}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${productNameKey}"><td/><td>${fns:getDictLabel(item.countryCode,'platform','')}</td><td>${item.colorCode}</td>
					<td><a href="${ctx}/psi/purchaseOrder/view?id=${item.purchaseOrder.id}">${item.purchaseOrder.orderNo}</a></td><td>${item.quantityUnReceived}</td>
					<td><fmt:formatDate value="${item.deliveryDate}" pattern="yyyy-MM-dd"/> </td><td><fmt:formatDate value="${item.actualDeliveryDate}" pattern="yyyy-MM-dd"/></td><td/></tr>
				</c:forEach>
			</c:if>
			
		</c:forEach>
		<tr>
			<td></td>
			<td colspan="4">合计</td>
			<td id="totleV"></td>
			<td id="totleW"></td>
			<td></td>
		</tr>
		</tbody>
	</table>
</body>
</html>
