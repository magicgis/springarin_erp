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
			
			$("#isCheck").on("click",function(){
				if(this.checked){
					$("input[name='isCheck']").val("1");
				}else{
					$("input[name='isCheck']").val("0");
				}
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
			
			var totalQ = 0;
			var totleV = 0 ;
			var totleW = 0 ;
			var totalP =0;
			$(".orderQ").each(function(){
				totalQ =totalQ+parseFloat($(this).text());
			});
			$(".volume").each(function(){
				totleV =totleV+parseFloat($(this).text());
			});
			$(".weight").each(function(){
				totleW =totleW+parseFloat($(this).text());
			});
			$(".itemsAmount").each(function(){
				totalP =totalP+parseFloat($(this).text());
			});
			
			$("#totleV").append("<b>"+toDecimal(totleV)+"</b>");
			$("#totleW").append("<b>"+toDecimal(totleW)+"</b>");
			$("#totalQ").append("<b>"+totalQ+"</b>");
			$("#totalP").append("<b>"+toDecimal(totalP)+"</b>");
			
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
			
			$("#productIdColor,#countryCode").change(function(){
				$("input[name='productName']").val($("#productIdColor").children('option:selected').text());
				$("#searchForm").submit();
			});
			
		});	
			
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = x.toFixed(2);  
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
	
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/lcPurchaseOrder/singleProduct" method="post" class="breadcrumb form-search" cssStyle="height: 40px;text-align:center;margin-top: 10px">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input name="productName" type="hidden" value="${productName}"/>
		<div style="height: 40px;line-height: 40px;float:left">
			<label>&nbsp;&nbsp;&nbsp;国家：</label>
			<select name="countryCode"  style="width:120px" id="countryCode">
					<option value="" >全部</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							 <option value="${dic.value}" ${countryCode eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:if>      
					</c:forEach>	      
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
			<label>&nbsp;&nbsp;&nbsp;产品：</label>
			<select name="productIdColor"  id="productIdColor">
					<c:forEach items="${proColorMap}" var="proEntry">
							 <option value="${proEntry.value}" ${proEntry.key eq productName ?'selected':''}  >${proEntry.key}</option>
					</c:forEach>	
			</select>&nbsp;&nbsp;&nbsp;&nbsp;
			<label>SN：</label>
			<input name="snNo" type="text" maxlength="50" class="input-small" />
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;&nbsp;
			<a target="_blank" href="${ctx}/psi/lcPsiInventory/productInfoDetail?productName=${productName}"><span style="font-size: 18px;font-weight: bold">${productName}</span></a>
		</div>
		
		
		
	</form:form>
	<tags:message content="${message}"/>   
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr>
		<th width="3%">序号</th>
		<th width="10%">订单编号</th><th width="5%">供应商</th>
		<th width="5%">总数量</th><th width="5%">可收货</th><th width="5%">待确认</th><th width="5%">已收货</th>
		<th width="5%">装箱体积(m³)</th><th width="5%">毛重(kg)</th><shiro:hasPermission name="psi:product:viewPrice"><th width="5%">单价&nbsp;($)</th><th width="5%">金额&nbsp;($)</th></shiro:hasPermission>
		<th width="5%">线下订单</th><th width="5%">订单状态</th><th>跟单员</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="purchaseOrder" varStatus="i">
		<c:forEach items="${purchaseOrder.tempOrders}" var="order" varStatus="j">
		<tr>
			<c:choose>
				<c:when test="${j.index eq'0'}">
					<td>${order.id}</td>
					<td>
						<a href="${ctx}/psi/lcPurchaseOrder/view?id=${order.id}">${order.orderNo}&nbsp;&nbsp;(WK<span style="color:red">${fns:getDateByPattern(purchaseOrder.createDate,"w")}</span>)&nbsp;[${order.snCode}]</a>
						<br/>
						<c:if test="${(purchaseOrder.orderSta eq '0'||purchaseOrder.orderSta eq '1'||purchaseOrder.orderSta eq '2') &&order.needParts}">
							<c:if test="${purchaseOrder.toPartsOrder eq '1'}">(<span style="font-weight: bolod;color: black">含配件已下单</span>)</c:if>
							<c:if test="${purchaseOrder.toPartsOrder eq '0'}">(<span style="font-weight: bolod;color: red">含配件未下单</span>)</c:if>
						</c:if>
					</td>
				</c:when>
				<c:otherwise>
					<td></td><td></td>
				</c:otherwise>
			</c:choose>
				
			<td>${order.supplier.nikename}</td>
			<td><span class="orderQ">${order.itemsQuantity}</span></td>
			<td>${order.itemsQuantityCanReceived}</td>
			<td>${order.itemsQuantityPreReceived}</td>
			<td>${order.itemsQuantityReceived}</td>
			<td class="volume"><fmt:formatNumber value="${order.volume}" minFractionDigits="3"/></td><td class="weight"><fmt:formatNumber value="${order.weight}" minFractionDigits="3"/></td>
			<shiro:hasPermission name="psi:product:viewPrice">
				<td><c:if  test="${order.orderSta eq '2'||order.orderSta eq '3'||order.orderSta eq '4'||order.orderSta eq '5'}" ><fmt:formatNumber value="${order.itemsAmount/order.itemsQuantity}" pattern="#.##"/> </c:if></td><td class="itemsAmount"><fmt:formatNumber value="${order.itemsAmount}" minFractionDigits="2" pattern="0.00" /></td>
			</shiro:hasPermission>
			
			<td>${order.offlineSta eq '0'?'线上':'线下'}</td> 
			<c:choose>
				<c:when test="${j.index eq'0'}">
					<td>
					<c:if test="${order.orderSta eq '0'}"><span class="label label-important">草稿</span></c:if>
					<c:if test="${order.orderSta eq '1'}"><span class="label " style="background-color:#DCB5FF">已审核</span></c:if>
					<c:if test="${order.orderSta eq '2'}"><span class="label label-warning">生产</span></c:if>
					<c:if test="${order.orderSta eq '3'}"><span class="label label-info">部分收货</span></c:if>
					<c:if test="${order.orderSta eq '4'}"><span class="label" style="background-color:#00E3E3">已收货</span></c:if>
					<c:if test="${order.orderSta eq '5'}"><span class="label  label-success">已完成</span></c:if>
					<c:if test="${order.orderSta eq '6'}"><span class="label  label-inverse">已取消</span></c:if>
					</td>
					<td>${order.merchandiser.name}</td>
				</c:when>
				<c:otherwise>
					<td></td><td></td>
				</c:otherwise>
			</c:choose>
			<td >
			<input type="hidden" value="${order.orderNo},${order.tempProductName}"/>
			<a class="btn btn-small btn-info open">概要</a>
			<c:if test="${j.index eq '0'}">
				<a class="btn btn-small" target="_blank" href="${ctx}/psi/lcPurchaseOrder/printPdf?id=${order.id}">PDF</a>
			</c:if>
			
			</td>
		</tr>
		<c:if test="${fn:length(order.items)>0}">
			<tr style="background-color:#ECF5FF;display: none" name="${order.orderNo},${order.tempProductName}">
			<td></td><td>国家</td><td>条码</td><td>订单数</td><td>可收货</td><td>待确认</td><td>已收货</td>
			<td>装箱体积(m³)</td><td>毛重(kg)</td><td>订单交期</td><td>预计交期</td><td colspan="6">备注</td></tr>
			<c:forEach items="${order.showItems}" var="item">
				<tr style="background-color:#ECF5FF;display: none" name="${order.orderNo},${order.tempProductName}" >
				<td>
				<input type="hidden" class="orderId" value="${order.id}" />
				<input type="hidden" class="orderItemId" value="${item.id}" />
				<input type="hidden" class="supplierId" value="${order.supplier.id}" />
				<input type="hidden" class="orderSta" value="${order.orderSta}" />
				<input type="hidden" class="currencyType" value="${order.currencyType}" />
				</td>
				<td style="word-break: break-all; word-wrap:break-word;">${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
				<td>
				
				<c:if test="${not empty  item.barcodeInstans.barcode}">
					<a href="${ctx}/psi/lcProduct/genBarcode?country=${item.barcodeInstans.productPlatform}&type=${item.barcodeInstans.barcodeType}&productName=${item.barcodeInstans.barcodeProductName}&barcode=${item.barcodeInstans.barcode}" target="_blank" style="height: 14px" class="btn btn-warning" >${item.barcodeInstans.barcode}</a>
				    <br/>
				    ${fnskuMap[item.barcodeInstans.barcode]}
				</c:if>
				</td>
				<td><span id="quantityOrdered">${item.quantityOrdered}</span></td><td><span id="quantityCanReceived">${item.quantityCanReceived}</span></td><td><span id="quantityPreReceived">${item.quantityPreReceived}</span></td><td><span id="quantityReceived">${item.quantityReceived}</span></td>
				<td class="itemVolume"><fmt:formatNumber value="${item.volume}" minFractionDigits="3"/></td><td class="itemWeight"><fmt:formatNumber value="${item.weight}" minFractionDigits="3"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${item.deliveryDate}"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${item.actualDeliveryDate}"/></td>
				<td colspan="6">${item.remark}</td>
				</tr>
			</c:forEach>   
		</c:if>
		</c:forEach>
		</c:forEach>
		<tr>
			<td>合计</td>
			<td colspan="2"></td>
			<td id="totalQ"></td>
			<td colspan="3"></td>
			<td id="totleV"></td>
			<td id="totleW"></td>
			<shiro:hasPermission name="psi:product:viewPrice">
			<td></td><td ><span id="totalP"></span>&nbsp;<b>($)</b></td>
			</shiro:hasPermission>
			<td colspan="8"></td>
		</tr>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
				  