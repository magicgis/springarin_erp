<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购付款管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 1});
			$("#paymentSta,#supplier").change(function(){
				$("#searchForm").submit();
			});
			
			var payAmount = 0;
			$(".paymentTotal").each(function(e){
				var re = new RegExp(",","g");
				payAmount=payAmount+parseFloat($(this).text().replace(re,''));
			});
			$("#totleAmount").append("<b>"+commafy(payAmount)+"<span style='color:#EAC100;padding-left:5px;font-size:16px'>¥<span></b>");
			
			
			var realPayAmount = 0;
			$(".realPaymentTotal").each(function(e){
				var re = new RegExp(",","g");
				realPayAmount=realPayAmount+parseFloat($(this).text().replace(re,''));
			});
			$("#realTotleAmount").append("<b>"+commafy(realPayAmount)+"<span style='color:#EAC100;padding-left:5px;font-size:16px'>$<span></b>");
			
			
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		function toDecimal(x) {  
            var f = parseFloat(x);  
            if (isNaN(f)) {  
                return;  
            }  
            f = Math.round(x*100)/100;  
            return f;  
     };
     
     function commafy(num){ 
    	 var curNum = toDecimal(num);
    	 var parts = curNum.toString().split(".");  
    	  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");  
    	  return parts.join(".");  
     } 
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">采购付款列表</a></li>
		<shiro:hasPermission name="psi:payment:edit">
			<li><a href="${ctx}/psi/purchasePayment/add">新建采购付款</a></li>
		</shiro:hasPermission>
		<li ><a href="${ctx}/psi/purchaseOrder/moneyView">采购订单资金列表</a></li>
		<li ><a href="${ctx}/psi/lcPurchasePayment">(理诚)采购付款列表</a></li>
		<shiro:hasPermission name="psi:payment:edit">
			<li><a href="${ctx}/psi/lcPurchasePayment/add">(理诚)新建采购付款</a></li>
		</shiro:hasPermission>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/moneyView">(理诚)采购订单资金列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="purchasePayment" action="${ctx}/psi/purchasePayment/" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${purchasePayment.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${purchasePayment.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				
		<label>付款单号 ：</label> <form:input path="paymentNo" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		</div>
			<div style="height: 40px;">
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<option value="" ${purchasePayment.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}'>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			<script type="text/javascript">
			$("option[value='${purchasePayment.supplier.id}']").attr("selected","selected");	
			</script>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>付款状态：</label>
			<form:select path="paymentSta" style="width: 200px" id="paymentSta">
				<option value="" >全部(非取消)</option>
				<option value="0" ${purchasePayment.paymentSta eq '0' ?'selected':''} >草稿</option>
				<option value="1" ${purchasePayment.paymentSta eq '1' ?'selected':''} >申请</option>
				<option value="2" ${purchasePayment.paymentSta eq '2' ?'selected':''} >已付款</option>
				<option value="3" ${purchasePayment.paymentSta eq '3' ?'selected':''} >已取消</option>
			</form:select>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="treeTable" class="table table-bordered table-condensed">   
		<thead>  
		<tr>
		<th style="width:5%">序号</th><th style="width:15%">付款单号</th><th style="width:5%">应付总金额</th><th style="width:5%">实付总金额</th>
		<th style="width:8%">付款状态</th><th style="width:10%">创建时间</th><th style="width:5%">创建人</th>
		<th style="width:30%">备注<th style="width:10%">操作</th></tr></thead>           
		<tbody>
		<c:forEach items="${page.list}" var="purchasePayment">
			<tr id="${purchasePayment.id}">
				<td style="text-align:center">${purchasePayment.id}</td>
				<td><a href="${ctx}/psi/purchasePayment/view?id=${purchasePayment.id}">${purchasePayment.paymentNo}</a> </td>
				<td class="paymentTotal">${purchasePayment.amountTotal}</td>
				<td class="realPaymentTotal">${purchasePayment.realAmountTotal} </td>
				<td>
					<c:if test="${purchasePayment.paymentSta eq '0'}"><span class="label  label-important">草稿</span></c:if>
					<c:if test="${purchasePayment.paymentSta eq '1'}"><span class="label  label-warning">申请</span></c:if>
					<c:if test="${purchasePayment.paymentSta eq '2'}"><span class="label  label-success">已付款</span></c:if>
					<c:if test="${purchasePayment.paymentSta eq '3'}"><span class="label  label-inverse">已取消</span></c:if>
				</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${purchasePayment.createDate}"/></td>
				<td>${purchasePayment.createUser.name}</td>
				<td>${purchasePayment.remark}</td>
				<td>
				<shiro:hasPermission name="psi:payment:edit">
					<c:if test="${purchasePayment.paymentSta eq '1' }">
					<a class="btn btn-small"  href="${ctx}/psi/purchasePayment/sure?id=${purchasePayment.id}">确认</a>
					<a class="btn btn-small" href="${ctx}/psi/purchasePayment/cancel?id=${purchasePayment.id}&paymentSta=5" onclick="return confirmx('确认要取消该采购付款吗？', this.href)">取消</a>
					</c:if>
					
				</shiro:hasPermission>
				<c:if test="${not empty purchasePayment.attchmentPath }">
					<c:choose>
						<c:when test="${fn:contains(purchasePayment.attchmentPath,',')}">
							<div class="btn-group">
							<button type="button" class="btn btn-small">查看水单</button>
						   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
							 <ul class="dropdown-menu" >
								<c:forEach items="${fn:split(purchasePayment.attchmentPath,',')}" var="att" varStatus="i">
									<li><a class="btn btn-small" target="_blank"  href="<c:url value='/data/site${att}' />">查看水单${i.index+1}</a></li>
								</c:forEach>
								</ul>
							</div>
						</c:when>
						<c:otherwise>
							<a class="btn btn-small" target="_blank"  href="<c:url value='/data/site${purchasePayment.attchmentPath}'/>">查看水单</a><br/>
						</c:otherwise>
					</c:choose>
				</c:if>
				
					
				</td>
			</tr>
			
			<c:if test="${fn:length(purchasePayment.items)>0}">
				<tr style="background-color:#D2E9FF" id="itemHead${purchasePayment.id}"  pid="${purchasePayment.id}"><td></td><td>单号</td><td>付款总额</td><td>付款类型</td><td>备注</td><td colspan="4"></td></tr>
				<c:forEach items="${purchasePayment.items}" var="item">
					<tr style="background-color:#D2E9FF" id="item${item.id}" pid="${purchasePayment.id}" >
					<td></td>
					<td>
					<c:if test="${item.paymentType eq '0'}">
					<a target="_blank"  href="${ctx}/psi/purchaseOrder/view?id=${item.order.id}">${item.billNo}</a>
					</c:if>
					<c:if test="${item.paymentType eq '1'}">
					<a target="_blank"  href="${ctx}/psi/psiLadingBill/view?id=${item.ladingBill.id}">${item.billNo}</a>
					</c:if>
					<td><fmt:formatNumber value="${item.paymentAmount}" pattern=",###.##"/></td>
					<td>
					<c:if test="${item.paymentType eq '0'}">定金</c:if>
					<c:if test="${item.paymentType eq '1'}">尾款</c:if>
					</td>
					
					<td>${item.remark}</td><td colspan="5">${item.ladingBillItem.productName}&nbsp;&nbsp;&nbsp;&nbsp;${item.ladingBillItem.countryCode}</td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		
		<tr>
			<td></td>
			<td>合计</td>
			<td id="totleAmount"/>
			<td id="realTotleAmount"/>
			<td colspan="5"></td>
		</tr>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
