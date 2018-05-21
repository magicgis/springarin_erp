<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收货单查看</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.uploadPreview {
		    height:120px;     
		    width:100%;                     
		}
		.pic{
		    border:0; 
			margin:0; 
			padding:0; 
			max-width:200px; 
			width:expression(this.width>200?"200px":this.width); 
			max-height:120px; 
			height:expression(this.height>120?"120px":this.height); 
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
		$(document).ready(function() {
			
			eval('var ladingMap=${ladingMap}');
			
			// 生成要编辑表格 
			var createTable = $('#contentTable');
			for(var key in ladingMap){
				var ladingArrays=ladingMap[key];
				var tbody =$("<tbody></tbody>");
				if(ladingArrays.length>0){
					for(var i=0;i<ladingArrays.length;i++){
						var ladingDto = ladingArrays[i];
						var pass ="";
						if(ladingDto.isPass=='0'){
							pass="不合格";
						}else if(ladingDto.isPass=='1'){
							pass="合格";
						}
						if(i==0){
							var tr = $("<tr></tr>");
				            tr.append("<td style='text-align:center;vertical-align:middle' rowspan="+ladingArrays.length+"> <input type='text' readOnly style='width:90%' name='productConName'></td>");
				            tr.append("<td><a href='${ctx}/psi/purchaseOrder/view?id="+ladingDto.purchaseOrderId+"'</a>"+ladingDto.purchaseOrderNo+" </td>");
				            tr.append("<td> <input readonly style='width:90%' type='text' name='quantityLading' /></td>");
				            tr.append("<td> <input readonly style='width:90%' type='text' name='quantityOffLading' /></td>");
				            tr.append("<td> <input type='text' style='width:90%' name='quantitySure' readonly /></td>");
				            tr.append("<td> <input type='text' readonly style='width:90%' name='quantitySpares' /></td>");
				            tr.append("<shiro:hasPermission name='psi:product:viewPrice'><td><input readonly style='width:90%' type='text' class='itemPrice' /></td>");
				            tr.append("<td><input readonly style='width:90%' type='text' class='totalAmount' /></td>");
				            tr.append("<td><input readonly style='width:90%' type='text' class='paymentAmount' /></td>");
				            tr.append("<td><input readonly style='width:90%' type='text' class='unPaymentAmount' /></td>");
				            tr.append("<td><input readonly style='width:90%' type='text' class='deposit' /></td>");
				            tr.append("<td><input readonly style='width:90%' type='text' class='payDepositAmount' /></td></shiro:hasPermission>");
				            tr.append("<td><input type='text' style='width:90%' readonly value='"+pass+"'/></td>");
				            tr.append("<td> <input type='text' readonly style='width:90%' class='partsTimely' /></td>");
				            tr.append("<td class='remark'> </td>");
							tr.find("input[name='productConName']").val(key);
							tr.find("input[name='quantityLading']").val(ladingDto.quantityLading);
							tr.find("input[name='quantityOffLading']").val(ladingDto.quantityOffLading);
							tr.find("input[name='quantitySpares']").val(ladingDto.quantitySpares);
							tr.find("input[name='quantitySure']").val(ladingDto.canSureQuantity);
							tr.find(".remark").text(ladingDto.remark);
							tr.find(".itemPrice").val(ladingDto.itemPrice);
							tr.find(".totalAmount").val(ladingDto.totalAmount);
							tr.find(".paymentAmount").val(ladingDto.totalPaymentAmount);
							tr.find(".unPaymentAmount").val(ladingDto.totalAmount-ladingDto.totalPaymentAmount);
							tr.find(".deposit").val(ladingDto.deposit+"%");
							tr.find(".payDepositAmount").val(ladingDto.payDepositAmount);
							tr.find(".partsTimely").val(ladingDto.partsTimelyInfo);
				            tbody.append(tr);
						}else{
							var tr =$("<tr class='notFirstRow'></tr>");
				            tr.append("<td><input type='hidden' name='id'/><input type='hidden' name='productConName'/><a href='${ctx}/psi/purchaseOrder/view?id="+ladingDto.purchaseOrderId+"'</a>"+ladingDto.purchaseOrderNo+"</td>");
				            tr.append("<td> <input type='text' readonly style='width:90%' name='quantityLading' /></td>");
				            tr.append("<td> <input type='text' readonly style='width:90%' name='quantityOffLading' /></td>");
				            tr.append("<td> <input type='text' style='width:90%' name='quantitySure' readonly /></td>");
				            tr.append("<td> <input type='text' readonly style='width:90%' name='quantitySpares' /></td>");
				            tr.append("<shiro:hasPermission name='psi:product:viewPrice'><td><input readonly style='width:90%' type='text' class='itemPrice' /></td>");
				            tr.append("<td><input readonly style='width:90%' type='text' class='totalAmount' /></td>");
				            tr.append("<td><input readonly style='width:90%' type='text' class='paymentAmount' /></td>");
				            tr.append("<td><input readonly style='width:90%' type='text' class='unPaymentAmount' /></td>");
				            tr.append("<td><input readonly style='width:90%' type='text' class='deposit' /></td>");
				            tr.append("<td><input readonly style='width:90%' type='text' class='payDepositAmount' /></td></shiro:hasPermission>");
				            tr.append("<td><input type='text' readonly style='width:90%' value='"+pass+"'/></td>");
				            tr.append("<td> <input type='text' readonly style='width:90%' class='partsTimely' /></td>");
				            tr.append("<td class='remark'></td>");
				            tr.find("input[name='quantityLading']").val(ladingDto.quantityLading);
				            tr.find("input[name='quantityOffLading']").val(ladingDto.quantityOffLading);
				            tr.find("input[name='quantitySpares']").val(ladingDto.quantitySpares);
				            tr.find("input[name='quantitySure']").val(ladingDto.canSureQuantity);
							tr.find(".remark").text(ladingDto.remark);
							tr.find("input[name='productConName']").val(key);
							tr.find(".itemPrice").val(ladingDto.itemPrice);
							tr.find(".totalAmount").val(ladingDto.totalAmount);
							tr.find(".paymentAmount").val(ladingDto.totalPaymentAmount);
							tr.find(".unPaymentAmount").val(ladingDto.totalAmount-ladingDto.totalPaymentAmount);
							tr.find(".deposit").val(ladingDto.deposit+"%");
							tr.find(".payDepositAmount").val(ladingDto.payDepositAmount);
							tr.find(".partsTimely").val(ladingDto.partsTimelyInfo);
				            tbody.append(tr);
						}
					}
				}
				createTable.append(tbody);
			}
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">查看收货单</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiLadingBill" action="${ctx}/psi/psiLadingBill/sureSave" method="post" class="form-horizontal" enctype="multipart/form-data">
	    <input type='hidden' name="supplier.id" value="${psiLadingBill.supplier.id}">
	    <input type='hidden' name="billSta" value="${psiLadingBill.billSta}">
	    <input type='hidden' name="createDate" value="${psiLadingBill.createDate}">
	    <input type='hidden' name="delFlag" value="${psiLadingBill.delFlag}">
	    <input type='hidden' name="createUser.id" value="${psiLadingBill.createUser.id}">
	    <input type='hidden' name="oldItemIds" value="${psiLadingBill.oldItemIds}">
	    <input type='hidden' name="billNo" value="${psiLadingBill.billNo}">
	    <input type='hidden' name="id" value="${psiLadingBill.id}">
	    <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>供应商</b>:</label>
				<div class="controls" >
				<span>
				<input type="text" readonly  value="${psiLadingBill.supplier.nikename}"/>
				</span>
				</div>
			</div>
			<div class="control-group" style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>收货单状态</b>:</label>
				<div class="controls" >
					<span class='label label-success'>${psiLadingBill.statusName}</span>&nbsp;&nbsp;&nbsp;&nbsp;
				</div>
			</div>
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<label class="control-label"><b>付款状态</b>:</label>
				<div class="controls" >
				<span>
					<fmt:parseNumber value='${psiLadingBill.totalPaymentAmount}' var="totalPaymentAmount"/>
					<fmt:parseNumber value='${psiLadingBill.totalPaymentPreAmount}' var="totalPaymentPreAmount"/>
					<fmt:parseNumber value='${psiLadingBill.totalAmount}' var="totalAmount"/>
			
					<c:choose>
					<c:when test="${totalPaymentAmount==0&&totalPaymentPreAmount==0}"><span class='label label-important'>未申请</span></c:when>
					<c:when test="${totalPaymentAmount==0&&totalPaymentPreAmount>0}"><span class='label label-warning'>已申请</span></c:when>
					<c:when test="${totalPaymentAmount>0&&totalPaymentAmount<totalAmount}"><span class='label label-info'>部分付款</span></c:when>
					<c:otherwise >
					<span class='label label-success'>已付款</span>
					</c:otherwise>
					</c:choose>
				</span>
				${psiLadingBill.currencyType}
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:30%" >
				<label class="control-label"><b>收货单号</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" readonly    value="${psiLadingBill.billNo}"/>
				</span>
				</div>
			</div>
			<c:choose>
				<c:when test="${psiLadingBill.billSta eq '1'}">
					<div class="control-group" style="float:left;width:30%" >
						<label class="control-label"><b>确认人</b>:</label>
						<div class="controls" >
						<span>
							<input type="text" readonly    value="${psiLadingBill.sureUser.name}"/>
						</span>
						</div>
					</div>
					<div class="control-group" style="float:left;width:40%" >
						<label class="control-label"><b>确认时间</b>:</label>
						<div class="controls" >
						<span>
							<input type="text" readonly    value="${psiLadingBill.sureDate}"/>
						</span>
						</div>
					</div>
				</c:when>
				<c:when test="${psiLadingBill.billSta eq '2'}">
					<div class="control-group" style="float:left;width:30%" >
						<label class="control-label"><b>取消人</b>:</label>
						<div class="controls" >
						<span>
							<input type="text" readonly   value="${psiLadingBill.cancelUser.name}"/>
						</span>
						</div>
					</div>
					<div class="control-group" style="float:left;width:40%"  >
						<label class="control-label"><b>取消时间</b>:</label>
						<div class="controls" >
						<span>
							<input type="text" readonly    value="${psiLadingBill.cancelDate}"/>
						</span>
						</div>
					</div>
				</c:when>			
				<c:otherwise>
					<div class="control-group" style="float:left;width:30%" >
						<label class="control-label"><b>最后修改人</b>:</label>
						<div class="controls" >
						<span>
							<input type="text" readonly    value="${psiLadingBill.updateUser.name}"/>
						</span>
						</div>
					</div>
					<div class="control-group" style="float:left;width:40%" >
						<label class="control-label"><b>最后修改时间</b>:</label>
						<div class="controls" >
						<span>
							<input type="text" readonly    value="${psiLadingBill.updateDate}"/>
						</span>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		<blockquote style="float: left">
			<p style="font-size: 14px ;">收货单项信息</p>
		</blockquote>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width:15%">产品名</th>
				   <th style="width:10%">订单号</th>
				   <th style="width: 6%">收货总数</th>
				   <th style="width: 6%">线下数</th>
				   <th style="width: 6%">未确认数</th>
				   <th style="width: 6%">备品数量</th>
				   <shiro:hasPermission name="psi:product:viewPrice">
				   <th style="width: 5%">单价</th>
				   <th style="width: 5%">尾款总额</th>
				   <th style="width: 5%">尾款已付</th>
				   <th style="width: 5%">尾款未付</th>
				   <th style="width: 5%">定金比例</th>
				   <th style="width: 5%">已付定金</th>
				   </shiro:hasPermission>
				   <th style="width:6%">质检状态</th>
				   <th style="width:8%">配件信息</th>
				   <th style="width: 8%">备注</th>
				   
			</tr>
		</thead>
	</table>
	
	<c:if test="${not empty psiLadingBill.attchmentPath }">
		<blockquote>
			<p style="font-size: 14px">凭证</p>
		</blockquote>
		<div class="control-group">
			<div class="controls">
				<c:forEach items="${fn:split(psiLadingBill.attchmentPath,',')}" var="attchment">
					<a target="_blank" href="<c:url value='/data/site${attchment}'/>">查看收货单凭证${i.index+1}</a>
					
					&nbsp;&nbsp;&nbsp;  
				</c:forEach>
				</div>
		</div>
	</c:if>
	
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
