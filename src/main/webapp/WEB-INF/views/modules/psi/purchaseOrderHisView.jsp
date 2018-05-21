<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	</script>	
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">查看采购订单快照</a></li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="hisOrder" >
		<input type="hidden" name="id" value="${hisOrder.id}" />
	    <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:100%">
		
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 10%">订单号</th>
				   <th style="width: 8%">供应商</th>
				   <th style="width: 10%">定金比例</th>
				   <th style="width: 5%">货币类型</th>
				   <th style="width: 5%">跟单员</th>
				   <th style="width: 5%">确认人</th>
				   <th style="width: 10%">确认时间</th>
				   <th style="width: 5%">订单总额</th>
				   <th style="width: 5%">已支付金额</th>
				   <th style="width: 5%">已支付定金</th>
				   <th style="width: 5%">状态</th>
				   <th style="width: 5%">PI</th>
				   
			</tr>
		</thead>
				<tbody>
					<tr>
						<td>${hisOrder.orderNo}</td>
						<td>${hisOrder.supplier.nikename}</td>
						<td>${hisOrder.deposit}<span class="add-on">%</span></td>
						<td>${hisOrder.currencyType}</td>
						<td>${hisOrder.merchandiser.name}</td>
						<td>${hisOrder.sureUser.name}</td>
						<td><fmt:formatDate value="${hisOrder.sureDate}" pattern="yyyy-MM-dd" /></td>
						<td>${hisOrder.totalAmount}</td>
						<td>${hisOrder.totalPaymentAmount}</td>
						<td>${hisOrder.depositAmount}</td>
						<td>
						<c:if test="${hisOrder.orderSta eq '1'}"><span class="label label-important">草稿</span></c:if>
						<c:if test="${hisOrder.orderSta eq '2'}"><span class="label label-warning">生产</span></c:if>
						<c:if test="${hisOrder.orderSta eq '3'}"><span class="label label-info">部分收货</span></c:if>
						<c:if test="${hisOrder.orderSta eq '4'}"><span class="label" style="background-color:#00E3E3">已收货</span></c:if>
						<c:if test="${hisOrder.orderSta eq '5'}"><span class="label  label-success">已完成</span></c:if>
						<c:if test="${hisOrder.orderSta eq '6'}"><span class="label  label-inverse">已取消</span></c:if>
						</td>
						<td>
						<c:if test="${not empty hisOrder.piFilePath }">
							<a  target="_blank" href="${ctx}/psi/hisOrder/printPi?id=${hisOrder.id}">查看</a>
						</c:if>
						</td>
					</tr>
				</tbody>
	</table>
			
		</div>
		
		<div style="float: left"><blockquote><p style="font-size: 14px">产品信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 15%">产品名称</th>
				   <th style="width: 10%">预计交货</th>
				   <th style="width: 5%">颜色</th>
				   <th style="width: 5%">国家</th>
				   <th style="width: 10%">数量</th>
				   <th style="width: 5%">单价</th>
				   <th style="width: 5%">已收货数量</th>
				   <th style="width: 5%">收货待确认数量</th>
				   <th style="width: 5%">已付款数量</th>
				   <th style="width: 5%">已付款金额</th>
				   <th style="width: 5%">条形码</th>
				   <th style="width: 15%">备注</th>
				   
			</tr>
		</thead>
		<tbody>
		<c:if test="${fn:length(hisOrder.items)>0}" >
			<c:forEach items="${hisOrder.orderItemsMap}" var="itemEntry" >
				 <c:forEach items="${itemEntry.value.items}" var="item" varStatus="i">
				 	  <tr>
				 	  <c:if test="${i.index==0}">
				 	  	 <td style="text-align: center;vertical-align: middle;" rowspan="${fn:length(itemEntry.value.items)}" >${item.productName}</td>
				 	  </c:if>	
				 	    <td style="width: 10% ;vertical-align: middle;"><fmt:formatDate value="${item.deliveryDate}" pattern="yyyy-MM-dd"/></td>
						<td style="width: 10% ;vertical-align: middle;">${item.colorCode}</td>
						<td style="width: 10% ;vertical-align: middle;">${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
						<td style="width: 6% ;vertical-align: middle;">${item.quantityOrdered}</td>
						<td style="width: 6%;vertical-align: middle;">${item.itemPrice}</td>
						<td style="width: 6%;vertical-align: middle;">${item.quantityReceived}</td>
						<td style="width: 6%;vertical-align: middle;">${item.quantityPreReceived}</td>
						<td style="width: 6%;vertical-align: middle;">${item.quantityPayment}</td>
						<td style="width: 6%;vertical-align: middle;">${item.paymentAmount}</td>
						<td style="width: 6%;vertical-align: middle;">
						<c:if test="${not empty item.barcodeInstans.barcode}">
						<a href="${ctx}/psi/product/genBarcode?country=${item.barcodeInstans.productPlatform}&type=${item.barcodeInstans.barcodeType}&productName=${item.barcodeInstans.barcodeProductName}&barcode=${item.barcodeInstans.barcode}" target="_blank" class="btn btn-warning" >${item.barcodeInstans.barcode}</a>
						</c:if>
						</td>
						<td style="width: 15%;vertical-align: middle;">${item.remark}</td>
				 	  </tr>
				 </c:forEach>
			</c:forEach>
		</c:if>
		</tbody>
	</table>
	
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
