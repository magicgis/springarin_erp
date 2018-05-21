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
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("#expByCountry").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/purchaseOrder/marketExport");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/psi/purchaseOrder/");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
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
			
			
			$("#expExcel").click(function(){
				var params = {};
				params.createDate=$("input[name='createDate']").val();
				params.purchaseDate=$("input[name='purchaseDate']").val();
				params.orderNo=$("input[name='orderNo']").val();
				params.orderSta=$("#orderSta").val();  
				params.isOverInventory=$("#isOverInventory").val(); 
				params['supplier.id']=$("#supplier").val();
				window.location.href = "${ctx}/psi/purchaseOrder/exp?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$("#expUnReceived").click(function(){
				var params = {};
				params.createDate=$("input[name='createDate']").val();
				params.purchaseDate=$("input[name='purchaseDate']").val();
				params.orderNo=$("input[name='orderNo']").val();
				params.orderSta=$("#orderSta").val();
				params['supplier.id']=$("#supplier").val();
				window.location.href = "${ctx}/psi/purchaseOrder/expUnReceived?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$("#batchReceive").click(function(){
				var orderIds = "";
				var itemIds = "";
				var supplierId ="";
				var currencyType="";
				var flag=1;
				var curFlag=1;
				var staFlag=1;
				var isChecked =false;
				
				$(".checkPro").each(function(){
					if(this.checked){
						isChecked=true;
						return ;
					};
				});
				
				if(!isChecked){
					top.$.jBox.tip("必须选中一种收货项！", 'info',{timeout:3000});
					return false;
				}
				
				$(":checked").parent().parent().find(".orderSta").each(function(){
					if($(this).val()!="2"&&$(this).val()!="3"){
						staFlag=2;
						return;
					}
				});
				
				if(staFlag==2){
					top.$.jBox.tip("只能选生产和部分收货状态的订单","info",{timeout:3000});
					return false;
				}
				
				
				
				//随便取一个供应商
				$(":checked").parent().parent().find(".supplierId").each(function(){
					var $parent =$(this).parent();
					if($(this).val()!=''){
						supplierId=$(this).val();
						currencyType=$parent.find(".currencyType").val();
						return;
					}
				});
				
			
				var canReceived =0;
				
				$(":checked").parent().parent().find(".orderItemId").each(function(){
					var curSupplierId=$(this).parent().find(".supplierId").val();
					var curCurrencyType=$(this).parent().find(".currencyType").val();
					if(curSupplierId!=supplierId){
						flag=2;
						return;
					}
					if(curCurrencyType!=currencyType){
						curFlag=2;
						return;
					}
					var tr =$(this).parent().parent();
					var quantityCanReceived = tr.find("#quantityCanReceived").text();
					canReceived=canReceived+parseInt(quantityCanReceived);
					itemIds=itemIds+$(this).val()+",";
					orderIds=orderIds+$(this).parent().find(".orderId").val()+",";
				});
				
							
				if(flag==2){
					top.$.jBox.tip("只能对一个供应商进行收货","info",{timeout:3000});
					return false;
				}
				
				if(curFlag==2){
					top.$.jBox.tip("只能对同一币种进行收货","info",{timeout:3000});
					return false;
				}
				
				if(canReceived==0){
					top.$.jBox.tip("所选产品可收货数量为0,请重新选择！","info",{timeout:3000});
					return false;
				}
				
				var params = {};
				params['supplierId'] = supplierId;
				params['currencyType']=currencyType;   
				params['orderItemIds'] = itemIds;
				params['orderIds'] = orderIds;
				window.location.href = "${ctx}/psi/psiLadingBill/batchReceive?"+$.param(params);
			});
			
			$("#orderSta,#supplier,#country,#isOverInventory,#productIdColor").change(function(){
				if($("#productIdColor").children('option:selected').val()!=""){
					$("input[name='productName']").val($("#productIdColor").children('option:selected').text());
				}else{
					$("input[name='productName']").val($("#productIdColor").children('option:selected').val());
				}
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
		<li><a href="${ctx}/psi/lcPurchaseOrder/">(理诚)采购订单列表</a></li>
		<shiro:hasPermission name="psi:order:edit">
			<li><a href="${ctx}/psi/lcPurchaseOrder/add">(理诚)新建采购订单</a></li>
		</shiro:hasPermission>
		<li class="active"><a href="${ctx}/psi/purchaseOrder/">采购订单列表</a></li>
		<shiro:hasPermission name="psi:order:edit">
			<li><a href="${ctx}/psi/purchaseOrder/add">新建采购订单</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input name="productName" type="hidden" value="${productName}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			<label>国家：</label>
			<form:select path="versionNo" style="width: 100px" id="country">
				<option value="" >全部</option>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek'}">
						 <option value="${dic.value}" ${purchaseOrder.versionNo eq dic.value ?'selected':''}  >${dic.label}</option>
					</c:if>      
				</c:forEach>	
			</form:select>&nbsp;&nbsp;
			<label>产品：</label>
			<select name="productIdColor" style="width:180px" id="productIdColor">
					<option value="">全部</option>
					<c:forEach items="${proColorMap}" var="proEntry">
							 <option value="${proEntry.value}" ${proEntry.key eq productName ?'selected':''}  >${proEntry.key}</option>
					</c:forEach>	
			</select>&nbsp;&nbsp;
			<label>预收货：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${purchaseOrder.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="purchaseDate" value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				
			<label>订单编号：</label>
			<form:input path="orderNo" type="text" maxlength="50" class="input-small" />
				&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			&nbsp;&nbsp;
			<input id="count" class="btn btn-primary" type="button" value="试算"/>
			</div>
			<div style="height: 40px;">
			<label>供应商：</label>
			<select style="width:100px;" id="supplier" name="supplier.id">
				<option value="" ${purchaseOrder.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}' ${supplier.id eq  purchaseOrder.supplier.id?'selected':''}>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>订单状态：</label>
			<form:select path="orderSta" style="width: 120px" id="orderSta">
				<option value="" >全部(非取消)</option>
				<option value="9" >未收货</option>
				<option value="0" ${purchaseOrder.orderSta eq '0' ?'selected':''} >草稿</option>
				<option value="1" ${purchaseOrder.orderSta eq '1' ?'selected':''} >已审核</option>
				<option value="2" ${purchaseOrder.orderSta eq '2' ?'selected':''} >生产</option>
				<option value="3" ${purchaseOrder.orderSta eq '3' ?'selected':''} >部分收货</option>
				<option value="4" ${purchaseOrder.orderSta eq '4' ?'selected':''} >已收货</option>
				<option value="5" ${purchaseOrder.orderSta eq '5' ?'selected':''} >已完成</option>
				<option value="6" ${purchaseOrder.orderSta eq '6' ?'selected':''} >已取消</option>
			</form:select>
			<label>订单类型：</label>
			<form:select path="isOverInventory" style="width: 120px" id="isOverInventory">
				<option value="">所有</option>
				<option value="0" ${purchaseOrder.isOverInventory eq '0' ?'selected':''} >普通</option>
				<option value="1" ${purchaseOrder.isOverInventory eq '1' ?'selected':''} >特批</option>
			</form:select>
			
			&nbsp;&nbsp;
			<shiro:hasPermission name="psi:order:edit">
			<input id="batchReceive" class="btn btn-success" type="button" value="收货"/>
			</shiro:hasPermission>
			&nbsp;&nbsp;
			<label>与我相关：</label><input type="checkbox"  id="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/>
			<input  name="isCheck" type="hidden" value="${isCheck}"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
			<input id="expByCountry" class="btn btn-warning" type="button" value="导出(销售)"/>
			<input id="expUnReceived" class="btn btn-warning" type="button" value="导出(未收货)"/>
			</div>
		</div>
		
	</form:form>
	<tags:message content="${message}"/>   
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr>
		<th width="2%"><input type="checkbox" id="checkAll" /></th>
		<th width="3%">序号</th>
		<th width="12%">订单编号</th><th width="5%">供应商</th><th width="13%">商品</th>
		<th width="5%">总数量</th><th width="5%">可收货</th><th width="5%">待确认</th><th width="5%">已收货</th>
		<th width="5%">装箱体积(m³)</th><th width="5%">毛重(kg)</th><th width="5%">货币类型</th><th width="5%">订单状态</th><th>跟单员</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="purchaseOrder" varStatus="i">
		<c:forEach items="${purchaseOrder.tempOrders}" var="order" varStatus="j">
		<tr>
			<td><input type="checkbox" class="checkPros" /></td>
			<c:choose>
				<c:when test="${j.index eq'0'}">
					<td>${order.id}</td>
					<td>
						<a href="${ctx}/psi/purchaseOrder/view?id=${order.id}">${order.orderNo}</a>${purchaseOrder.offlineSta eq '0'?'':'(含线下)'}
						<c:if test="${(purchaseOrder.orderSta eq '0'||purchaseOrder.orderSta eq '1'||purchaseOrder.orderSta eq '2') &&order.needParts}">
							<br/>
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
			<td>${order.tempProductName}</td>
			<td>${order.itemsQuantity}</td>
			<td>${order.itemsQuantityCanReceived}</td>
			<td>${order.itemsQuantityPreReceived}</td>
			<td>${order.itemsQuantityReceived}</td>
			<td class="volume"><fmt:formatNumber value="${order.volume}" minFractionDigits="3"/></td><td class="weight"><fmt:formatNumber value="${order.weight}" minFractionDigits="3"/></td>
			<td>${order.currencyType}</td>
			<c:choose>
				<c:when test="${j.index eq'0'}">
					<td>
					<c:if test="${order.isOverInventory eq '1'}">
						<a href="#" class="tipsA" rel="popover" data-content="${order.overRemark}" >
							<c:if test="${order.orderSta eq '0'}"><span class="label label-important">草稿(超标)</span></c:if>
							<c:if test="${order.orderSta eq '1'}"><span class="label "  style="background-color:#DCB5FF">已审核(超标)</span></c:if>
							<c:if test="${order.orderSta eq '2'}"><span class="label label-warning">生产(特批)</span></c:if>
							<c:if test="${order.orderSta eq '3'}"><span class="label label-info">部分收货(特批)</span></c:if>
							<c:if test="${order.orderSta eq '4'}"><span class="label" style="background-color:#00E3E3">已收货(特批)</span></c:if>
							<c:if test="${order.orderSta eq '5'}"><span class="label  label-success">已完成(特批)</span></c:if>
							<c:if test="${order.orderSta eq '6'}"><span class="label  label-inverse">已取消(超标)</span></c:if>
						</a>
					</c:if>
					<c:if test="${order.isOverInventory ne '1'}">
						<c:if test="${order.orderSta eq '0'}"><span class="label label-important">草稿</span></c:if>
							<c:if test="${order.orderSta eq '1'}"><span class="label " style="background-color:#DCB5FF">已审核</span></c:if>
							<c:if test="${order.orderSta eq '2'}"><span class="label label-warning">生产</span></c:if>
							<c:if test="${order.orderSta eq '3'}"><span class="label label-info">部分收货</span></c:if>
							<c:if test="${order.orderSta eq '4'}"><span class="label" style="background-color:#00E3E3">已收货</span></c:if>
							<c:if test="${order.orderSta eq '5'}"><span class="label  label-success">已完成</span></c:if>
							<c:if test="${order.orderSta eq '6'}"><span class="label  label-inverse">已取消</span></c:if>
					</c:if>
					
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
				<a class="btn btn-small" target="_blank" href="${ctx}/psi/purchaseOrder/printPdf?id=${order.id}">PDF</a>
				<!-- 
				<c:if test="${order.orderSta ne '0'}">
					<c:if test="${fns:getUser().id == order.merchandiser.id}">
							<a class="btn btn-small" href="${ctx}/custom/sendEmail/form?sendEmail=${purchaseOrder.supplier.id}&type=2&sendSubject=${order.orderNo}">发送PDF</a>
						</c:if>
				</c:if>
				 -->
					<shiro:hasPermission name="psi:order:edit">
					<c:if test="${ order.orderSta ne '5' && order.orderSta ne '6'}">
						<a class="btn btn-small" href="${ctx}/psi/purchaseOrder/edit?id=${order.id}">编辑</a>
					</c:if>
					<c:if test="${order.orderSta ne '4' && order.orderSta ne '5' && order.orderSta ne '6'}">
						<a class="btn btn-small" href="${ctx}/psi/purchaseOrder/editDeliveryDate?id=${order.id}">编辑交期</a>
					</c:if>
					</shiro:hasPermission>
				<c:if test="${order.orderSta le '5' && order.orderSta ne '1' && order.orderSta ne '0' }">
					<shiro:hasPermission name="psi:upload:pi">
					<a class="btn btn-small"  href="${ctx}/psi/purchaseOrder/uploadPi?id=${order.id}">上传PI</a>
					</shiro:hasPermission>
					<c:if test="${not empty order.piFilePath }">
						<a class="btn btn-small "  target="_blank" href="${ctx}/psi/purchaseOrder/printPi?id=${order.id}">PI</a>
					</c:if>
				</c:if>
				<c:if test="${order.orderSta eq '1' }">
					<shiro:hasPermission name="psi:order:confirm">
						<a class="btn btn-small"  href="${ctx}/psi/purchaseOrder/sure?id=${order.id}">确认</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="psi:order:edit">
						<a class="btn btn-small"  href="${ctx}/psi/purchaseOrder/cancel?id=${order.id}" onclick="return confirmx('确认要取消该采购订单吗？', this.href)">取消</a>
					</shiro:hasPermission>
				</c:if>
				
				<c:if test="${order.orderSta eq '0' && order.toReview eq '1'}">
				<c:if test="${order.isOverInventory eq '1' }">
					<shiro:hasPermission name="psi:order:overReview">
						<a class="btn btn-small"  href="${ctx}/psi/purchaseOrder/overReview?id=${order.id}">审核(超标)</a>
					</shiro:hasPermission>
				</c:if>
				
				<c:if test="${order.isOverInventory eq '0' }">
					<shiro:hasPermission name="psi:order:review">
						<a class="btn btn-small"  href="${ctx}/psi/purchaseOrder/review?id=${order.id}">审核</a>
					</shiro:hasPermission>
				</c:if>
				
				<shiro:hasPermission name="psi:order:edit">
						<a class="btn btn-small"  href="${ctx}/psi/purchaseOrder/cancel?id=${order.id}" onclick="return confirmx('确认要取消该采购订单吗？', this.href)">取消</a>
				</shiro:hasPermission>
				</c:if>
				
				<c:if test="${order.orderSta eq '0' && order.toReview eq '0' && fns:getUser().id eq order.merchandiser.id}">
				<shiro:hasPermission name="psi:order:edit">
					<a class="btn btn-small"  href="${ctx}/psi/purchaseOrder/appRevise?id=${order.id}">申请审核</a>
					<a class="btn btn-small"  href="${ctx}/psi/purchaseOrder/cancel?id=${order.id}" onclick="return confirmx('确认要取消该采购订单吗？', this.href)">取消</a>
				</shiro:hasPermission>
				</c:if>
				
				<c:if test="${order.orderSta eq '1'}">
				<shiro:hasPermission name="psi:order:review">
				<a class="btn btn-small"  href="${ctx}/psi/purchaseOrder/toDraft?id=${order.id}" onclick="return confirmx('确认把该采购订单变成草稿吗？', this.href)" >变成草稿</a>
				</shiro:hasPermission>
				</c:if>
				
				<shiro:hasPermission name="psi:order:edit">
				<c:if test="${(order.orderSta eq '1' || order.orderSta eq '2') && order.toPartsOrder eq '0' && order.needParts}">
				<a class="btn btn-small" href="${ctx}/psi/psiPartsOrderBasis/form?id=${order.id}">配件下单</a>
				</c:if>
				</shiro:hasPermission>
			</c:if>
			
			</td>
		</tr>
		<c:if test="${fn:length(order.items)>0}">
			<tr style="background-color:#ECF5FF;display: none" name="${order.orderNo},${order.tempProductName}">
			<td></td>
			<td></td><td>国家</td><td>颜色</td><td>条码</td><td>订单数</td><td>可收货</td><td>待确认</td><td>已收货</td>
			<td>装箱体积(m³)</td><td>毛重(kg)</td><td>订单交期</td><td>预计交期</td><td >备注</td><td >(销售)备注</td></tr>
			<c:forEach items="${order.showItems}" var="item">
				<tr style="background-color:#ECF5FF;display: none" name="${order.orderNo},${order.tempProductName}" >
				<td>
				<input type="hidden" class="orderId" value="${order.id}" />
				<input type="hidden" class="orderItemId" value="${item.id}" />
				<input type="hidden" class="supplierId" value="${order.supplier.id}" />
				<input type="hidden" class="orderSta" value="${order.orderSta}" />
				<input type="hidden" class="currencyType" value="${order.currencyType}" />
				<input type="checkbox" class="checkPro" /></td>
				<td></td>
				<td style="word-break: break-all; word-wrap:break-word;">${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
				<td><a class="btn btn-warning" style="height:16px;width:20px;padding:0px;" target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}${item.colorCode !=''?'_':''}${item.colorCode}"><span class="icon-search"></span></a>&nbsp;${item.colorCode}</td>
				<td>
				<c:if test="${not empty  item.barcodeInstans.barcode}">
					<a href="${ctx}/psi/product/genBarcode?country=${item.barcodeInstans.productPlatform}&type=${item.barcodeInstans.barcodeType}&productName=${item.barcodeInstans.barcodeProductName}&barcode=${item.barcodeInstans.barcode}" target="_blank" style="height: 14px" class="btn btn-warning" >${item.barcodeInstans.barcode}</a>
				    <br/>
				    ${fnskuMap[item.barcodeInstans.barcode]}
				</c:if>
				</td>
				<td><span id="quantityOrdered">${item.quantityOrdered}</span></td><td><span id="quantityCanReceived">${item.quantityCanReceived}</span></td><td><span id="quantityPreReceived">${item.quantityPreReceived}</span></td><td><span id="quantityReceived">${item.quantityReceived}</span></td>
				<td class="itemVolume"><fmt:formatNumber value="${item.volume}" minFractionDigits="3"/></td><td class="itemWeight"><fmt:formatNumber value="${item.weight}" minFractionDigits="3"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${item.deliveryDate}"/></td>
				<td>
				<c:if test="${not empty item.deliveryDateList}">
				<c:forEach items="${item.deliveryDateList}" var="delivery">
					 <fmt:formatDate pattern="yyyy-MM-dd" value="${delivery.deliveryDate}"/>[<span style="color:red">${delivery.quantity}</span>]<br/>
				</c:forEach>
				</c:if>
				<c:if test="${empty item.deliveryDateList}">
					<fmt:formatDate pattern="yyyy-MM-dd" value="${item.actualDeliveryDate}"/>
				</c:if>
				</td>
				<td >${item.remark}</td>
				<td >${item.forecastRemark}</td>
				</tr>
			</c:forEach>   
		</c:if>
		</c:forEach>
		</c:forEach>
		<tr>
			<td></td>
			<td>合计</td>
			<td colspan="7"></td>
			<td id="totleV"></td>
			<td id="totleW"></td>
			<td colspan="5"></td>
		</tr>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
				  