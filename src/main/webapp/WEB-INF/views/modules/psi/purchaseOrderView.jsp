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
		<li class="active"><a href="#">查看采购订单</a></li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/sureSave" method="post" class="form-horizontal" enctype="multipart/form-data" >
		<input type="hidden" name="id" value="${purchaseOrder.id}" />
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
						   <th style="width: 8%">确认时间</th>
						   <th style="width: 8%">收货完成时间</th>
						   <shiro:hasPermission name="psi:product:viewPrice">
							   <th style="width: 5%">订单总额</th>
							   <th style="width: 5%">已付总额</th>
							   <th style="width: 5%">已付定金</th>
							   <th style="width: 5%">PI</th>
						   </shiro:hasPermission>
					       <shiro:lacksPermission name="psi:product:viewPrice">
				       		<c:if test="${fns:getUser().id eq purchaseOrder.merchandiser.id}">
				       			<th style="width: 5%">订单总额</th>
							   <th style="width: 5%">已付总额</th>
							   <th style="width: 5%">已付定金</th>
							   <th style="width: 5%">PI</th>
				       		</c:if>
					       </shiro:lacksPermission>
						   <th style="width: 5%">线下订单</th>
						   <th style="width: 5%">状态</th>
						   <th style="width: 5%">SnCode</th>
						   <th style="width: 5%">是否超标</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>${purchaseOrder.orderNo}</td>
						<td>${purchaseOrder.supplier.nikename}</td>
						<td>${purchaseOrder.deposit}<span class="add-on">%</span></td>
						<td>${purchaseOrder.currencyType}</td>
						<td>${purchaseOrder.merchandiser.name}</td>
						<td>${purchaseOrder.sureUser.name}</td>
						<td><fmt:formatDate value="${purchaseOrder.sureDate}" pattern="yyyy-MM-dd" /></td>
						<td><fmt:formatDate value="${purchaseOrder.receiveFinishedDate}" pattern="yyyy-MM-dd" /></td>
						<shiro:hasPermission name="psi:product:viewPrice">
							<td>${purchaseOrder.totalAmount}</td>
							<td>${purchaseOrder.totalPaymentAmount}</td>
							<td>${purchaseOrder.depositAmount}</td>
							<td>
							<c:if test="${not empty purchaseOrder.piFilePath }">
								<a  target="_blank" href="<c:url value='${purchaseOrder.piFilePath}'/>">查看</a>
							</c:if>
							</td>
						</shiro:hasPermission>
						 <shiro:lacksPermission name="psi:product:viewPrice">
		       				<c:if test="${fns:getUser().id eq purchaseOrder.merchandiser.id}">
		       					<td>${purchaseOrder.totalAmount}</td>
								<td>${purchaseOrder.totalPaymentAmount}</td>
								<td>${purchaseOrder.depositAmount}</td>
								<td>
								<c:if test="${not empty purchaseOrder.piFilePath }">
									<a  target="_blank" href="<c:url value='${purchaseOrder.piFilePath}'/>">查看</a>
								</c:if>
								</td>
		       				</c:if>
		       			</shiro:lacksPermission>
						<td style="vertical-align: middle;">${purchaseOrder.offlineSta eq '0' ?'否':'是'}</td>
						<td>
						<c:if test="${purchaseOrder.orderSta eq '0'}"><span class="label label-important">草稿</span></c:if>
						<c:if test="${purchaseOrder.orderSta eq '1'}"><span class="label label-important">已审核</span></c:if>
						<c:if test="${purchaseOrder.orderSta eq '2'}"><span class="label label-warning">生产</span></c:if>
						<c:if test="${purchaseOrder.orderSta eq '3'}"><span class="label label-info">部分收货</span></c:if>
						<c:if test="${purchaseOrder.orderSta eq '4'}"><span class="label" style="background-color:#00E3E3">已收货</span></c:if>
						<c:if test="${purchaseOrder.orderSta eq '5'}"><span class="label  label-success">已完成</span></c:if>
						<c:if test="${purchaseOrder.orderSta eq '6'}"><span class="label  label-inverse">已取消</span></c:if>
						</td>
						<td>${purchaseOrder.snCode}</td>
						<td>${purchaseOrder.isOverInventory eq '1'?'是':'否'}</td>
					</tr>
				</tbody>
			</table>
			<c:if test="${purchaseOrder.isOverInventory eq '1'}">
				<div class="control-group" style="float:left;width:100%" >
					<label class="control-label" style="width:60px"><b>超标原因</b>:</label>
					<div class="controls" style="margin-left:80px">
						<textarea rows="5" cols="5"  style="width:98%" readonly="readonly" >${purchaseOrder.overRemark}</textarea>
					</div>
				</div>
			</c:if>
		
		<div style="float: left"><blockquote><p style="font-size: 14px">产品信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 10%">产品名称</th>
				   <th style="width: 8%">订单交期</th>
				   <th style="width: 8%">预计交期</th>
				   <th style="width: 5%">颜色</th>
				   <th style="width: 8%">国家</th>
				   <th style="width: 5%">总数量</th>
				   <th style="width: 5%">线下数</th>
				   <shiro:hasPermission name="psi:product:viewPrice">
				   		<th style="width: 5%">单价</th>
				   </shiro:hasPermission>
				   <th style="width: 5%">已收(总)</th>
				   <th style="width: 5%">待(总)</th>
				   <th style="width: 5%">已收(线下)</th>
				   <th style="width: 5%">待(线下)</th>
				   <c:if test="${purchaseOrder.toPartsOrder eq '1' }">
					   	<th style="width: 5%">可装配件数</th>
				   </c:if>
				   <shiro:hasPermission name="psi:product:viewPrice">
			   		<th style="width: 5%">已付金额</th>
			   		<th style="width: 5%">已付尾款</th>
			   		<th style="width: 5%">已付定金</th>
				   </shiro:hasPermission>
				    <shiro:lacksPermission name="psi:product:viewPrice">
		       			<c:if test="${fns:getUser().id eq purchaseOrder.merchandiser.id}">
		       				<th style="width: 5%">已付总额</th>
				   			<th style="width: 5%">已付尾款</th>
			   				<th style="width: 5%">已付定金</th>
		       			</c:if>
		       		</shiro:lacksPermission>
				   <th style="width: 5%">条形码</th>
				   <th style="width: 5%">分批收货信息</th>
				   <th style="width: 5%">备注</th>
				   
			</tr>
		</thead>
		<tbody>
		<c:if test="${fn:length(purchaseOrder.items)>0}" >
			<c:forEach items="${purchaseOrder.orderItemsMap}" var="itemEntry" >
				 <c:forEach items="${itemEntry.value.items}" var="item" varStatus="i">
				 	  <tr>
				 	  <c:if test="${i.index==0}">
				 	  	 <td style="text-align: center;vertical-align: middle;" rowspan="${fn:length(itemEntry.value.items)}" >${item.productName}</td>
				 	  </c:if>	
				 	    <td style="vertical-align: middle;"><fmt:formatDate value="${item.deliveryDate}" pattern="yyyy-MM-dd"/></td>
				 	    <td style="vertical-align: middle;"><fmt:formatDate value="${item.actualDeliveryDate}" pattern="yyyy-MM-dd"/></td>
						<td style="vertical-align: middle;">${item.colorCode}</td>
						<td style="vertical-align: middle;">${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
						<td style="vertical-align: middle;">${item.quantityOrdered}</td>
						<td style="vertical-align: middle;">${item.quantityOffOrdered}</td>
						 <shiro:hasPermission name="psi:product:viewPrice">
							<td style="vertical-align: middle;">${item.itemPrice}</td>
						</shiro:hasPermission>
						 <shiro:lacksPermission name="psi:product:viewPrice">
			       			<c:if test="${fns:getUser().id eq purchaseOrder.merchandiser.id}"><td style="vertical-align: middle;">${item.itemPrice}</td></c:if>
			       		</shiro:lacksPermission>
						<td style="vertical-align: middle;">${item.quantityReceived}</td>
						<td style="vertical-align: middle;">${item.quantityPreReceived}</td>
						<td style="vertical-align: middle;">${item.quantityOffReceived}</td>
						<td style="vertical-align: middle;">${item.quantityOffPreReceived}</td>
						<c:if test="${purchaseOrder.toPartsOrder eq '1' }">
							<td style="vertical-align: middle;">
								<c:set value="${item.product.id},${item.colorCode}" var="ladingKey"/>
								<c:if test="${not empty canLadingMap[ladingKey]}">${canLadingMap[ladingKey]}</c:if>
								<c:if test="${empty canLadingMap[ladingKey]}">无配件</c:if>
							</td>
						</c:if>
						<shiro:hasPermission name="psi:product:viewPrice">
						  <td style="vertical-align: middle;">${item.allPaymentAmount}</td>
						  <td style="vertical-align: middle;">${item.paymentAmount}</td>
						  <td style="vertical-align: middle;">${item.depositPaymentAmount}</td>
						</shiro:hasPermission>
						 <shiro:lacksPermission name="psi:product:viewPrice">
			       			<c:if test="${fns:getUser().id eq purchaseOrder.merchandiser.id}">
			       				  <td style="vertical-align: middle;">${item.allPaymentAmount}</td>
								  <td style="vertical-align: middle;">${item.paymentAmount}</td>
								  <td style="vertical-align: middle;">${item.depositPaymentAmount}</td>
			       			</c:if>
			       		</shiro:lacksPermission>
						<td style="vertical-align: middle;">
						<c:if test="${not empty item.barcodeInstans.barcode}">
						<a href="${ctx}/psi/product/genBarcode?country=${item.barcodeInstans.productPlatform}&type=${item.barcodeInstans.barcodeType}&productName=${item.barcodeInstans.barcodeProductName}&barcode=${item.barcodeInstans.barcode}" target="_blank" class="btn btn-warning" >${item.barcodeInstans.barcode}</a>
						</c:if>
						</td>
						<td>
							<c:forEach var="deliveryInfo" items="${item.deliveryDateList}">
								<fmt:formatDate value="${deliveryInfo.deliveryDate}" pattern="MM-dd"/> 总:${deliveryInfo.quantity}已收:${deliveryInfo.quantityReceived}<br/>
							</c:forEach>
						</td>
						<td style="vertical-align: middle;">${item.remark}</td>
				 	  </tr>
				 </c:forEach>
			</c:forEach>
		</c:if>
		</tbody>
	</table>
	
	<c:if test="${fn:length(purchaseOrder.items)>0&&fn:length(purchaseOrder.ladingsMap)>0}" >
	<div style="float: left"><blockquote><p style="font-size: 14px">相关提单信息</p></blockquote></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width:20%">提单号</th>
				   <th style="width: 20%">产品名字</th>
				   <th style="width: 10%">颜色</th>
				   <th style="width: 10%">国家</th>
				   <th style="width: 10%">提货总数</th>
				   <th style="width: 10%">线下数</th>
				   <th style="width: 10%">状态</th>
				   <th style="width: 10%">付款状态</th>
				   <th style="width: 10%">备注</th>
				   
			</tr>
		</thead>
				<tbody>
				<c:forEach items="${purchaseOrder.ladingsMap}" var="itemEntry" >
				<c:forEach items="${itemEntry.value.items}" var="billItem" varStatus="i">
				 	  <tr>
				 	  <c:if test="${i.index==0}">
				 	  <td rowspan="${fn:length(itemEntry.value.items)}" style="text-align: center;vertical-align: middle;"><a href="${ctx}/psi/psiLadingBill/${itemEntry.value.billSta=='0'?'sure':'view'}?id=${billItem.ladingBill.id}">${billItem.ladingBill.billNo}</a></td>
				 	  </c:if>
				 	  <td>${billItem.productName}</td>
						<td>${billItem.colorCode}</td>
						<td>${billItem.country}</td>	
						<td>${billItem.quantityLading}</td>
						<td>${billItem.quantityOffLading}</td>
						<td>
						<c:if test="${billItem.ladingBill.billSta eq '0'}"><span class="label label-important">申请</span></c:if>
						<c:if test="${billItem.ladingBill.billSta eq '1'}"><span class="label  label-success">已确认</span></c:if>
						<c:if test="${billItem.ladingBill.billSta eq '2'}"><span class="label  label-success">已取消</span></c:if>
						</td>
						<td>
						<fmt:parseNumber value='${billItem.ladingBill.totalPaymentAmount}' var="totalPaymentAmount"/>
						<fmt:parseNumber value='${billItem.ladingBill.totalPaymentPreAmount}' var="totalPaymentPreAmount"/>
						<fmt:parseNumber value='${billItem.ladingBill.totalAmount}' var="totalAmount"/>
						<c:choose>
							<c:when test="${totalPaymentAmount==0&&totalPaymentPreAmount==0}"><span class='label label-important'>未申请</span></c:when>
							<c:when test="${totalPaymentAmount==0&&totalPaymentPreAmount>0}"><span class='label label-warning'>已申请</span></c:when>
							<c:when test="${totalPaymentAmount>0&&totalPaymentAmount<totalAmount}"><span class='label label-info'>部分付款</span></c:when>
							<c:otherwise >
							<span class='label label-success'>已付款</span>
							</c:otherwise>
						</c:choose>
						</td>
						<td>${billItem.remark}</td>	
				 	  </tr>
				</c:forEach>
				</c:forEach>
				</tbody>
		</table>
	</c:if>
			
			
	<shiro:hasPermission name="psi:product:viewPrice">	
		<c:if test="${fn:length(purchaseOrder.paymentsMap)>0 }">
		<div style="float: left"><blockquote><p style="font-size: 14px">相关付款信息</p></blockquote></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						   <th style="width:15%">付款单号</th>
						   <th style="width:15%">状态</th>
						   <th style="width:15%">凭证</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${purchaseOrder.paymentsMap}" var="pay" >
					 	  <tr>
					 	  <td><a href="${ctx}/psi/purchasePayment/view?id=${pay.value.id}">${pay.value.paymentNo}</a></td>   
							<td>
								<c:if test="${pay.value.paymentSta eq '0'}"><span class="label  label-important">草稿</span></c:if>
								<c:if test="${pay.value.paymentSta eq '1'}"><span class="label  label-warning">申请</span></c:if>
								<c:if test="${pay.value.paymentSta eq '2'}"><span class="label  label-success">已付款</span></c:if>
								<c:if test="${pay.value.paymentSta eq '3'}"><span class="label  label-inverse">已取消</span></c:if>
							</td>
							<td>
							<c:if test="${pay.value.paymentSta eq '2' }">
								<c:choose>
									<c:when test="${fn:contains(pay.value.attchmentPath,',')}">
										<c:forEach items="${pay.value.attchmentPath}" var="att" varStatus="i">
											<a class="btn btn-small" target="_blank"  href="<c:url value='/data/site${att}' />">查看水单${i.index+1}</a> &nbsp;&nbsp;
										</c:forEach>
									</c:when>
									<c:otherwise>
										<a class="btn btn-small" target="_blank"  href="<c:url value='/data/site${pay.value.attchmentPath}'/>">查看水单</a><br/>
									</c:otherwise>
								</c:choose>
							</c:if>
							</td>
					 	  </tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</shiro:hasPermission>
	<shiro:lacksPermission name="psi:product:viewPrice">
		<c:if test="${fn:length(purchaseOrder.paymentsMap)>0 && fns:getUser().id eq order.merchandiser.id}">
			<div style="float: left"><blockquote><p style="font-size: 14px">相关付款信息</p></blockquote></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						   <th style="width:15%">付款单号</th>
						   <th style="width:15%">状态</th>
						   <th style="width:15%">凭证</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${purchaseOrder.paymentsMap}" var="pay" >
					 	  <tr>
					 	  <td><a href="${ctx}/psi/purchasePayment/view?id=${pay.value.id}">${pay.value.paymentNo}</a></td>   
							<td>
								<c:if test="${pay.value.paymentSta eq '0'}"><span class="label  label-important">草稿</span></c:if>
								<c:if test="${pay.value.paymentSta eq '1'}"><span class="label  label-warning">申请</span></c:if>
								<c:if test="${pay.value.paymentSta eq '2'}"><span class="label  label-success">已付款</span></c:if>
								<c:if test="${pay.value.paymentSta eq '3'}"><span class="label  label-inverse">已取消</span></c:if>
							</td>
							<td>
							<c:if test="${pay.value.paymentSta eq '2' }">
								<c:choose>
									<c:when test="${fn:contains(pay.value.attchmentPath,',')}">
										<c:forEach items="${pay.value.attchmentPath}" var="att" varStatus="i">
											<a class="btn btn-small" target="_blank"  href="<c:url value='/data/site${att}' />">查看水单${i.index+1}</a> &nbsp;&nbsp;
										</c:forEach>
									</c:when>
									<c:otherwise>
										<a class="btn btn-small" target="_blank"  href="<c:url value='/data/site${pay.value.attchmentPath}'/>">查看水单</a><br/>
									</c:otherwise>
								</c:choose>
							</c:if>
							</td>
					 	  </tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		</shiro:lacksPermission>
		
		
	<c:if test="${fn:length(partsOutList)>0 }">
	<div style="float: left"><blockquote><p style="font-size: 14px">配件配送信息</p></blockquote></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				   <th style="width:40%">配送产品</th>
				   <th style="width:30%">数量</th>
				   <th style="width:30%">配送操作日期</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${partsOutList}" var="partsOut" >
			 	  <tr>
				  <td>${partsOut.productName}<c:if test="${not empty partsOut.color}">_${partsOut.color}</c:if></td>
				  <td>
					  <c:forEach items="${partsOut.orders}" var="order">
					  	<c:if test="${order.purchaseOrderId eq  purchaseOrder.id}">
					  	 	${order.quantity}
					  	</c:if>
					  </c:forEach>
				  </td>
				  <td><fmt:formatDate value="${partsOut.createDate}" pattern="yyyy-MM-dd"/></td>
			 	  </tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	
	
	
	
	<c:if test="${not empty versions }">
	<div style="float: left"><blockquote><p style="font-size: 14px">修改版本信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width:1%">序号</th>
				   <th style="width:15%">版本号</th>
				   <th style="width:15%">修改备注</th>
			</tr>
		</thead>
			<tbody>
				<c:forEach items="${versions}" var="version" varStatus="i">
				 	  <tr>
				 	  	<td>${i.index+1}</td>
						<td><a href="${ctx}/psi/lcHisPurchaseOrder/hisView?id=${version[0]}">${version[1]}</a></td>
						<td>${version[2]}</td>   
				 	  </tr>
				</c:forEach>
				</tbody>
		
	</table>
	</c:if>
	
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
