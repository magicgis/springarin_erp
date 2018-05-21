<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>采购订单管理</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
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
			var totalAmount = 0 ;
			var totalPaymentAmount = 0 ;
			var unPayment=0;
			$(".depositAmount").each(function(){
				totalAmount =totalAmount+parseFloat($(this).text()==""?0:$(this).text());
			});
			$(".monthAmount").each(function(){
				totalPaymentAmount =totalPaymentAmount+parseFloat($(this).text()==""?0:$(this).text());
			});
			$(".unPayment").each(function(){
				unPayment =unPayment+parseFloat($(this).text()==""?0:$(this).text());
			});
			
			$("#depositAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$"+toDecimal(totalAmount)+"<span></b>");
			$("#monthAmount").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$"+toDecimal(totalPaymentAmount)+"<span></b></b>");
			$("#unPayment").append("<b><span style='color:#EAC100;padding-left:5px;font-size:16px'>$"+toDecimal(unPayment)+"<span></b></b>");
			
			$("#supplier").change(function(){
				$("#searchForm").submit();
			});
			
			$("#exportCompare").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/purchaseOrder/reconciliationExport");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/psi/purchaseOrder/reconciliation2");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});

		  $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType": "bootstrap",
					"iDisplayLength": 15,
					"aLengthMenu":[[10, 20, 30,100,-1], [10, 20 , 30, 100, "All"]],
				 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
				 	, "aaSorting": [[0,"desc"]]
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
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		
	</script>
</head>
<body>
 <ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/purchaseOrder/reconciliation">对账</a></li>
		<li class="active"><a href="${ctx}/psi/purchaseOrder/reconciliation2">资金计划</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/fReport">采购支付报表</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/forecast">采购资金计划</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/reconciliation2" method="post" class="breadcrumb form-search" >
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<option value="" ${purchaseOrder.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}'>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			<script type="text/javascript">
			$("option[value='${purchaseOrder.supplier.id}']").attr("selected","selected");	
			</script>
		&nbsp;&nbsp;
		<label>日期：<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="receiveFinishedDate" value="<fmt:formatDate value="${purchaseOrder.receiveFinishedDate}" pattern="yyyy-MM-dd" />" id="start" class="input-small"/>
		&nbsp;-&nbsp;
		</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="purchaseDate" value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		
		&nbsp;&nbsp; <a class="btn btn-primary"  id="exportCompare">导出</a>
	</form:form>
	<tags:message content="${message}"/>   
	<div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<th style="text-align: center;vertical-align: middle">序号</th>
		<th style="text-align: center;vertical-align: middle">供应商</th><th style="text-align: center;vertical-align: middle">待付定金额</th><th style="text-align: center;vertical-align: middle">待付尾款金额（已收货）</th><th style="text-align: center;vertical-align: middle">待付尾款金额（未收货）</th></tr></thead>
		<tbody>
		<c:forEach items="${page}" var="purchaseOrder" varStatus="i">
		<tr>
			<td style="text-align: center;vertical-align: middle">${i.index+1}</td>
			<td style="text-align: center;vertical-align: middle">${purchaseOrder.nikename}</td>
			<td style="text-align: center;vertical-align: middle" class="depositAmount">${unPayDeposit[purchaseOrder.id]}</td>
			<td style="text-align: center;vertical-align: middle" class="unPayment">${unPayFinal[purchaseOrder.id]}</td>
			<td style="text-align: center;vertical-align: middle" class="monthAmount">${unReceiving[purchaseOrder.id]}</td>	
		
		</tr>
		</c:forEach>
		<tr><td style="text-align: center;vertical-align: middle" ></td>
			<td style="text-align: center;vertical-align: middle" >合计</td>
			<td style="text-align: center;vertical-align: middle" id="depositAmount"></td>
			<td style="text-align: center;vertical-align: middle" id="unPayment"></td>
			<td style="text-align: center;vertical-align: middle" id="monthAmount"></td>
		</tr> 
		</tbody>
	</table>
	</div>
</body>
</html>
