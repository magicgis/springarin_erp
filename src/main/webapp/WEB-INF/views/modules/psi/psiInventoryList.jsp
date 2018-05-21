<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Inventory Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
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
	
		if(!(top)){
			top = self;
		}
		$(document).ready(function() {
			$(".open").click(function(e){
				if($(this).text()=='Summary'){
					$(this).text('Closed');
				}else{
					$(this).text('Summary');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			$("#payExport").click(function(){
				var params = {};
				var startDate = $("#startDate").val();
				var endDate = $("#endDate").val();
				if(startDate==''||endDate==''){
					top.$.jBox.error("请选择时间段！", 'loading',{timeout:2000});
					return false;
				}
				params.startDate=startDate;
				params.endDate=endDate;
				params.warehouseId='${psiInventory.warehouse.id}';
				window.location.href = "${ctx}/psi/psiInventory/payExport?"+$.param(params);
			});
			
			$("#paySingleDateExport").click(function(){
				var params = {};
				var endDate = $("#endDate").val();
				if(endDate==''){
					top.$.jBox.error("请选择结束日期，导出该日库存数据！！", 'loading',{timeout:2000});
					return false;
				}
				params.endDate=endDate;
				params.warehouseId='${psiInventory.warehouse.id}';
				window.location.href = "${ctx}/psi/psiInventory/singleDateExport?"+$.param(params);
			});
			
			
			$(".tipsA").popover({html:true,trigger:'hover',content:function(){
				var td=$(this).parent();
				params={};
				params.warehouseId='${psiInventory.warehouse.id}';
				params.productId=td.find(".productId").val();
				params.countryCode=td.find(".countryCode").val();
				params.colorCode=td.find(".colorCode").val();
				var $this = $(this);
				if(!$this.attr("content")){
					if(!$this.attr("data-content")){
						var content="";
						$.ajax({
						    type: 'get',
						    async:false,
						    url: '${ctx}/psi/psiInventory/ajaxSkuQuantity' ,
						    data: $.param(params),
						    success:function(data){ 
						    	content = data;
						    	$this.attr("content",data);
					        }
						});
						return content;
					}
				}
				return $this.attr("content");
			}});
			
			$(".tipsB").popover({html:true,trigger:'hover',content:function(){
				var td=$(this).parent();
				params={};
				params.warehouseId='${psiInventory.warehouse.id}';
				params.productId=td.find(".productId").val();
				params.colorCode=td.find(".colorCode").val();
				var $this = $(this);
				if(!$this.attr("content")){
					if(!$this.attr("data-content")){
						var content="";
						$.ajax({
						    type: 'get',
						    async:false,
						    url: '${ctx}/psi/psiInventory/ajaxSkuTotalQuantity' ,
						    data: $.param(params),
						    success:function(data){ 
						    	content = data;
						    	$this.attr("content",data);
					        }
						});
						return content;
					}
				}
				return $this.attr("content");
			}});
			
			

			$("#aboutMe").click(function(){
				if(this.checked){
					$("#remark").val('1');
				}else{
					$("#remark").val('');
				}
				$("#searchForm").submit();
			});
			
			
			$("#country").change(function(){
				$("#searchForm").attr("action","${ctx}/psi/psiInventory?warehouse.id=${psiInventory.warehouse.id}");
				$("#searchForm").submit();
			});
			
			$("#butOut").click(function(){
				var params = {};
				params.warehouseId='${psiInventory.warehouse.id}';
				params.warehouseName=encodeURI('${psiInventory.warehouse.stockName}');
				window.location.href = "${ctx}/psi/psiInventoryOut/add?"+$.param(params);
			});
			
			$("#butIn").click(function(){
				var params = {};
				params.warehouseId='${psiInventory.warehouse.id}';
				params.warehouseName=encodeURI('${psiInventory.warehouse.stockName}');
				window.location.href = "${ctx}/psi/psiInventoryIn/add?"+$.param(params);
			});
			
			$("#butBatchIn").click(function(){
				var params = {};
				params.warehouseId='${psiInventory.warehouse.id}';
				params.warehouseName=encodeURI('${psiInventory.warehouse.stockName}');
				window.location.href = "${ctx}/psi/psiInventoryIn/batch?"+$.param(params);
			});
			
			$("#butBatch").click(function(){
				var params = {};
				params.warehouseId='${psiInventory.warehouse.id}';
				params.warehouseName=encodeURI('${psiInventory.warehouse.stockName}');
				window.location.href = "${ctx}/psi/psiInventoryIn/batch?"+$.param(params);
			});
			
			$("#butAdjust").click(function(){
				var params = {};
				params.warehouseId='${psiInventory.warehouse.id}';
				params.warehouseName=encodeURI('${psiInventory.warehouse.stockName}');
				window.location.href = "${ctx}/psi/psiInventory/adjust?"+$.param(params);
			});
			
			$("#export").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/psiInventory/exportInventory");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/psi/psiInventory");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#exportSpares").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/psiInventory/exportInventorySpares");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/psi/psiInventory");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#paySingleDateExport").click(function(){
				var params = {};
				var endDate = $("#endDate").val();
				if(endDate==''){
					top.$.jBox.error("请选择结束日期，导出该日库存数据！！", 'loading',{timeout:2000});
					return false;
				}
				params.endDate=endDate;
				params.warehouseId='${psiInventory.warehouse.id}';
				window.location.href = "${ctx}/psi/psiInventory/singleDateExport?"+$.param(params);
			});
			
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
			
			
			$(".editRemark").editable({validate:function(data){
				
			},display:false,success:function(response,newValue){
				var param = {};
				var $this = $(this);
				param.productId=$(this).parent().parent().find(".productId").val();
				param.countryCode=$(this).parent().parent().find(".countryCode").val();
				param.colorCode=$(this).parent().parent().find(".colorCode").val();
				param.warehouseId="${psiInventory.warehouse.id}";
				param.remark = encodeURI(newValue);
				$.get("${ctx}/psi/psiInventory/updateRemark?"+$.param(param),function(date){
					 if(date=='1'){
						 $this.attr("keyVal",newValue);
						 $this.text(newValue);		
						 $.jBox.tip('update success');
					 }else{
						 $.jBox.error('update failed', 'error');
					 }
				});
				return true;
			}});
			
		});
		
		
		function display_by(productName,country){
			var tip= $("#tranTip");
			tip.find("h3").text(productName+"    Transit List");
			var param = {};
			param.type = 1;
			param.country = country;
			param.name = productName;
			tip.find("tbody").html("");
			$.get("${ctx}/psi/psiInventory/getTranTipInfo?"+$.param(param),function(data){
				eval(" var data = "+data);
				var body="";
				for ( var i = 0; i < data.length; i++) {
					var ele = data[i];
					body +="<tr><td style='vertical-align: middle;text-align: center;'> <a target='_blank' href='${ctx}/psi/psiTransportOrder/view?transportNo="+ele.billNo+"'>"+ele.billNo+"</a></td><td style='vertical-align: middle;text-align: center;'>"
						+ele.tranModel+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.sku+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.remark+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.quantity+"</td><td style='vertical-align: middle;text-align: center;'>"
						+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.arriveDate+"</td></tr>";
				}	
				tip.find("tbody").html(body);
			});
			tip.modal();
		}
		
		function display_wait_by(productName,country){
			var tip= $("#waitTranTip");
			tip.find("h3").text(productName+"   Waiting For Transit List");
			var param = {};
			param.type = 1;
			param.country = country;
			param.name = productName;
			tip.find("tbody").html("");
			$.get("${ctx}/psi/psiInventory/getWaitTranTipInfo?"+$.param(param),function(data){
				eval(" var data = "+data);
				var body="";
				for ( var i = 0; i < data.length; i++) {
					var ele = data[i];
					body +="<tr><td style='vertical-align: middle;text-align: center;'> <a target='_blank' href='${ctx}/psi/psiTransportOrder/view?transportNo="+ele.billNo+"'>"+ele.billNo+"</a></td><td style='vertical-align: middle;text-align: center;'>"
						+ele.tranModel+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.sku+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.remark+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.quantity+"</td></tr>";
				}	
				tip.find("tbody").html(body);
			});
			tip.modal();
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
		<c:forEach items="${stocks}" var="stock">
			<li class="${stock.id eq psiInventory.warehouse.id ?'active':'' }" style="font-weight: bold;font-size:15px"><a href="${ctx}/psi/psiInventory?warehouse.id=${stock.id}">
			<c:choose>
				<c:when test="${stock.countrycode eq 'DE' }">Germany</c:when>
				<c:when test="${stock.id eq '21' }">China</c:when>
				<c:when test="${stock.id eq '130' }">China_LC</c:when>
				<c:when test="${stock.countrycode eq 'US' }">American</c:when>
				<c:otherwise>${stock.stockName}</c:otherwise>
			</c:choose>
			</a>
			</li>
		</c:forEach>
	</ul>
	
	<form:form id="searchForm" modelAttribute="psiInventory" action="${ctx}/psi/psiInventory/" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input name="warehouse.id" type="hidden" value="${psiInventory.warehouse.id}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
		<label>Country：</label>
		<select name="countryCode" style="width: 150px" id="country">
				<option value=""  ${psiInventory.countryCode eq '' ?'selected':'' }>All</option>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek'}">
						 <option value="${dic.value}" ${psiInventory.countryCode eq dic.value ?'selected':''}  >${dic.label}</option>
					</c:if>
				</c:forEach>	
		</select>&nbsp;&nbsp;&
		<label>ProductName：</label>
		<form:input path="productName" maxlength="50" class="input-small"/>&nbsp;&nbsp;
		<c:if test="${psiInventory.warehouse.id eq '130' }">
		   <input type="checkbox" id="aboutMe" ${not empty psiInventory.remark?'checked':''}/>Remark
		   <input type="hidden" name="remark" id="remark" value="${not empty psiInventory.remark?'1':''}">&nbsp;&nbsp;
		</c:if>
		
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search"/>
		&nbsp;&nbsp;
		 <shiro:hasPermission name="psi:inventory:edit:${psiInventory.sku}">
			<input id="butOut" class="btn btn-warning" type="button" value="Out Bound"/>
		 </shiro:hasPermission>
		&nbsp;&nbsp;
		<shiro:hasPermission name="psi:inventory:edit:${psiInventory.sku}">
			<input id="butIn" class="btn btn-success" type="button" value="In Bound"/>
			
		</shiro:hasPermission>
		
		<c:if test="${120 eq psiInventory.warehouse.id}">
		   <shiro:hasPermission name="psi:inventory:edit:US">
		    <input id="butBatchIn" class="btn btn-success" type="button" value="Batch In Bound"/>
		   </shiro:hasPermission>
		</c:if>
		
		&nbsp;&nbsp;
		<!-- 
			<shiro:hasPermission name="psi:inventory:batchAdjust">
			<input id="butBatch" class="btn btn-success" type="button" value="Batch leveling"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="butAdjust" class="btn btn-success" type="button" value="Adjust By Log"/>
			</shiro:hasPermission>
		 -->
		
		<input id="export" class="btn btn-success" type="button" value="Export(不含备品)"/>
		<input id="exportSpares" class="btn btn-success" type="button" value="Export(备品)"/>
		</div>
		<div style="height: 40px;">
		<shiro:hasPermission name="psi:order:financeReview">
			<c:if test="${psiInventory.warehouse.id eq 130}">
				<label>startDate：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${startDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="startDate"/>
				-
			</c:if>
			<label>endDate：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM-dd" />" id="endDate" class="input-small"/>
			<c:if test="${psiInventory.warehouse.id eq 130}">	
				<input id="payExport" class="btn btn-warning" type="button" value="导出(财务)"/>
			</c:if>
				<input id="paySingleDateExport" class="btn btn-warning" type="button" value="endDate库存"/>
		</shiro:hasPermission>
			<span class="alert alert-info"><b><spring:message code="psi_stock_timelyCapacity"/>:</b>${capacityTimely},<b><spring:message code="psi_stock_capacity"/>:</b>${psiInventory.warehouse.capacity},<b><spring:message code="psi_stock_rate"/></b>:
				<c:if test="${not empty psiInventory.warehouse.capacity}">
					<c:set value="${capacityTimely*100/psiInventory.warehouse.capacity}" var="caRate"/>
					<span style="color:${caRate>80?'red':'green'}"><b><fmt:formatNumber value="${caRate}" pattern="0.00"/></b> </span>%
				</c:if>
			</span>
		</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th width="18%">Product(Volume)[m³]</th><th width="5%">Total</th><th width="5%"  class="sort newQuantity" >New</th><th width="5%"  class="sort offlineQuantity" >Offline</th>
		<c:if test="${psiInventory.warehouse.countrycode eq 'CN'}">
			<th>Waiting for<br/>Delivery</th>
			<th>Remaining<br/>after delivery</th>
		</c:if>
		<th width="5%">Last Week<br/>Out Quantity</th><th width="5%">Old</th><th width="5%">Broken</th><th width="5%"  class="sort renewQuantity" >Renew</th><th width="5%"  class="sort sparesQuantity" >Spares</th>
		<c:if test="${psiInventory.warehouse.countrycode ne 'CN'}">
			<th width="10%"  >Transit Quantity</th>
		</c:if><th>Remark</th>
		<th>Operate</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="inventoryMaster">
			<tr>
				<td>
					<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${inventoryMaster.productNameColor}"><c:if test="${fn:contains(newProSets,inventoryMaster.productNameColor)}"><b style='color:red'>!</b></c:if>
					<span style="color:${saleMap[inventoryMaster.productNameColor]['total'] eq '4'?'gray':''}">${inventoryMaster.productNameColor}</span></a>
					 (<fmt:formatNumber value="${inventoryMaster.volume}" pattern="0.##" />)
				</td>
				<td>
					<input type="hidden" class="productId" value="${inventoryMaster.productId}"/>
					<input type="hidden" class="colorCode" value="${inventoryMaster.colorCode}"/>
					<c:choose>
						<c:when test="${inventoryMaster.totalQuantity>0}"><a href="#" class="tipsB" rel="popover" content="" >${inventoryMaster.totalQuantity}</a></c:when>
						<c:otherwise>${inventoryMaster.totalQuantity}</c:otherwise>
					</c:choose>
				</td>
				<td>${inventoryMaster.newQuantity}</td>
				<td>${inventoryMaster.offlineQuantity}</td>
				<c:if test="${psiInventory.warehouse.countrycode eq 'CN'}">
					<c:choose>
						<c:when test="${not empty waitTranTotalMap[inventoryMaster.productNameColor]}">
							  <td>
							  	<c:if test="${waitTranTotalMap[inventoryMaster.productNameColor]>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_wait_by('${inventoryMaster.productNameColor}','CN');return false;">${waitTranTotalMap[inventoryMaster.productNameColor]}</a>	</c:if>
							 	<c:if test="${waitTranTotalMap[inventoryMaster.productNameColor] eq 0}">0</c:if>
							  </td>
							  <td>
							  	<c:if test="${inventoryMaster.newQuantity-waitTranTotalMap[inventoryMaster.productNameColor]>0}">
							  		${inventoryMaster.newQuantity-waitTranTotalMap[inventoryMaster.productNameColor]}
							  	</c:if>
							  </td>
						</c:when>
						<c:otherwise> <td></td>
						<td><c:if test="${inventoryMaster.newQuantity>0}">${inventoryMaster.newQuantity}</c:if></td></c:otherwise>
					</c:choose>
				</c:if>
				<td>${not empty lastWeekMap[inventoryMaster.productNameColor]?lastWeekMap[inventoryMaster.productNameColor]['total']:''}</td>
				<td>${inventoryMaster.oldQuantity}</td>
				<td>${inventoryMaster.brokenQuantity}</td>
				<td>${inventoryMaster.renewQuantity}</td>
				<td>${inventoryMaster.sparesQuantity}</td>
				<c:if test="${psiInventory.warehouse.countrycode ne 'CN'}">
					<td>
					 <c:if test="${not empty tranTotalMap[inventoryMaster.productNameColor]}">
						  	<c:if test="${tranTotalMap[inventoryMaster.productNameColor]>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by('${inventoryMaster.productNameColor}','${psiInventory.warehouse.countrycode eq 'DE'?'eu':'am'}');return false;">${tranTotalMap[inventoryMaster.productNameColor]}</a></c:if>
						  	<c:if test="${tranTotalMap[inventoryMaster.productNameColor] eq 0}">0</c:if>
						 </c:if>
						 <c:if test="${empty tranTotalMap[inventoryMaster.productNameColor]}">0</c:if>
					</td>
				</c:if>
				<td></td>
				<td>
				<input type="hidden" value="${inventoryMaster.productNameColor}"/><a class="btn btn-small btn-info open">Summary</a>
				<a class="btn btn-small"  href="${ctx}/psi/psiInventoryRevisionLog?productId=${inventoryMaster.productId}&warehouseId=${inventoryMaster.warehouse.id}&colorCode=${inventoryMaster.colorCode}&showFlag=0">In-Out Details</a>
				&nbsp;&nbsp;&nbsp;&nbsp;<a class="btn btn-small"  href="${ctx}/psi/psiInventoryRevisionLog?productId=${inventoryMaster.productId}&warehouseId=${inventoryMaster.warehouse.id}&colorCode=${inventoryMaster.colorCode}&showFlag=1">Adjust Details</a>
				</td>
			</tr>
			<c:forEach items="${inventoryMaster.inventoryList}" var="inventory">
				<tr name="${inventory.productNameColor}"  style="background-color:#D2E9FF;display: none">
					<td ><span style="color:${saleMap[inventory.productNameColor][inventory.countryCode] eq '1'?'':'gray'}">${fns:getDictLabel(inventory.countryCode, 'platform', '')}</span>
					<c:if test="${fn:contains(newProCountrySets,inventory.productColorCountry)}"><b>(新品无sku)</b></c:if>
					</td>
					<td>
					<input type="hidden" class="productId" value="${inventoryMaster.productId}"/>
					<input type="hidden" class="countryCode" value="${inventory.countryCode}"/>
					<input type="hidden" class="colorCode" value="${inventory.colorCode}"/>
					<a href="#" class="tipsA" rel="popover" content="" >${inventory.totalQuantity}</a></td>
					<td>${inventory.newQuantity}</td>
					<td>${inventory.offlineQuantity}</td>
					<c:if test="${psiInventory.warehouse.countrycode eq 'CN'}">
						<c:set value="${inventory.productNameColor},,${inventory.countryCode}" var="proColorCountry"/>
						<c:choose>
							<c:when test="${not empty waitTranMap[proColorCountry]}">
								  <td>
								  	<c:if test="${waitTranMap[proColorCountry]>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_wait_by('${inventory.productNameColor}','${inventory.countryCode}');return false;">${waitTranMap[proColorCountry]}</a>	</c:if>
								 	<c:if test="${waitTranMap[proColorCountry] eq 0}">0</c:if>
								  </td>
								  <td>
								  	<c:if test="${inventory.newQuantity-waitTranMap[proColorCountry]>0}">
								  		${inventory.newQuantity-waitTranMap[proColorCountry]}
								  	</c:if>
								  </td>
							</c:when>
							<c:otherwise> <td></td>
							<td><c:if test="${inventory.newQuantity>0}">${inventory.newQuantity}</c:if></td>
							</c:otherwise>
						</c:choose>
					</c:if>
					<td>${not empty lastWeekMap[inventory.productNameColor]?lastWeekMap[inventory.productNameColor][inventory.countryCode]:''}</td>
					<td>${inventory.oldQuantity}</td>
					<td>${inventory.brokenQuantity}</td>
					<td>${inventory.renewQuantity}</td>
					<td>${inventory.sparesQuantity}</td>
					<c:if test="${psiInventory.warehouse.countrycode ne 'CN'}">
						<td>
						<c:set value="${inventory.productNameColor},,${inventory.countryCode}" var="proColorCountry"/>
						 <c:if test="${not empty tranMap[proColorCountry]}">
						  	<c:if test="${tranMap[proColorCountry]>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by('${inventory.productNameColor}','${inventory.countryCode}');return false;">${tranMap[proColorCountry]}</a></c:if>
						  	<c:if test="${tranMap[proColorCountry] eq 0}">0</c:if>
						 </c:if>
						 <c:if test="${empty tranMap[proColorCountry]}">0</c:if>
						</td>
					</c:if>
					<td>
					   <a href="#" class="editRemark"  key="remark"  keyVal="${inventory.remark}" data-type="text" data-pk="1" data-title="Enter Remark" data-value="${inventory.remark}">${empty inventory.remark?'edit':inventory.remark}</a>
					</td>
					<td>
					
					<shiro:hasPermission name="psi:inventory:revise:${psiInventory.sku}">
						<div class="btn-group">
						   <button type="button" class="btn btn-small">Modify</button>
						   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						    <ul class="dropdown-menu" >
								<li><a  href="${ctx}/psi/psiInventory/countryChange?productId=${inventory.productId}&productName=${inventory.productName}&warehouse.id=${inventory.warehouse.id}&warehouseName=${inventory.warehouse.stockSign}&colorCode=${inventory.colorCode}&countryCode=${inventory.countryCode}">Sku Change</a></li>
								<li><a  href="${ctx}/psi/psiInventory/newOldChange?productId=${inventory.productId}&productName=${inventory.productName}&warehouse.id=${inventory.warehouse.id}&warehouseName=${inventory.warehouse.stockSign}&colorCode=${inventory.colorCode}&countryCode=${inventory.countryCode}">Type Change</a></li>
							 </ul>
						</div>
					</shiro:hasPermission>
					<a class="btn btn-small"  href="${ctx}/psi/psiInventoryRevisionLog?productId=${inventory.productId}&warehouseId=${inventory.warehouse.id}&colorCode=${inventory.colorCode}&countryCode=${inventory.countryCode}&showFlag=0">In-Out Details</a>
					&nbsp;&nbsp;&nbsp;&nbsp;<a class="btn btn-small"  href="${ctx}/psi/psiInventoryRevisionLog?productId=${inventory.productId}&warehouseId=${inventory.warehouse.id}&showFlag=1">Adjust Details</a>
					</td>
				</tr>
			</c:forEach>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<div id="tranTip" class="modal hide fade" tabindex="-1" data-width="750" style="width:900px;">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable" >
				<thead>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Bill No</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Model</th>
						<th style="width: 200px;text-align: center;vertical-align: middle;">Sku</th>
						<th style="width: 50px;text-align: center;vertical-align: middle;">To Warehouse</th>
						<th style="width: 50px;text-align: center;vertical-align: middle;">Quantity</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">outBound Date</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Forecast inBound Date</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	
	<div id="waitTranTip" class="modal hide fade" tabindex="-1" data-width="750" style="width:900px;">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable" >
				<thead>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Bill No</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Model</th>
						<th style="width: 200px;text-align: center;vertical-align: middle;">Sku</th>
						<th style="width: 50px;text-align: center;vertical-align: middle;">To Warehouse</th>
						<th style="width: 50px;text-align: center;vertical-align: middle;">Quantity</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
</body>
</html>
					   