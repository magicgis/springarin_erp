<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单预计交货日期</title>
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
			if(!(top)){
				top = self; 
			}
			
			$(".Wdate").live("click", function (){
			 	WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$('#contentTable').on('change','select.remark',function(){
				if($(this).val()=='manutal'){
					$(this).parent().parent().find(".remarkInput").css("display","block");
				}else{
					$(this).parent().parent().find(".remarkInput").css("display","none");
					$(this).parent().parent().find(".remarkInput").val($(this).val());
				}
			});
			
			
			
			  $(".DeliveryDateEnter").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.orderItemId = $this.parent().parent().find(".orderItemId").val();
					param.deliveryDate = newValue;
					$.get("${ctx}/psi/lcPurchaseOrder/updateSingelDeliveryDate?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							top.$.jBox.tip("保存预计收货时间成功！", 'info',{timeout:2000});
						}
					});
					return true;
				}});  
			  
			
			$(".add-row").on('click', function(e){
				var parent=$(this).parent();
			    e.preventDefault();
			    var table = parent.find(".deliveryTable");
			    if(table.find("tr").size()==0){
			    	table.append("<tr><th style='width:10%'>总数</th><th style='width:10%'>线下数</th><th style='width:10%'>已收</th><th style='width:10%'>线下已收</th><th style='width:15%'>预计交期</th><th style='width:25%'>备注</th><th style='width:10%'>操作</th></tr>");
			    }
	            var tr = $("<tr class='deliveryTr'></tr>");
				tr.append("<td> <input type='hidden' class='deliveryDateId' value='${deliveryInfo.id}'/><input type='text'  class='quantity' style='width: 80%' /></td>");
				tr.append("<td> <input type='text'   class='quantityOff' style='width: 80%' value='0'/></td>");
				tr.append("<td> <input style='width: 80%' readonly='readonly'  type='text'  class='quantityReceived' value='0' /></td>");
				tr.append("<td> <input style='width: 80%' readonly='readonly'  type='text'  class='quantityOffReceived' value='0' /></td>");
	            tr.append("<td> <input style='width: 80%'   type='text'  class='Wdate deliveryDate'  pattern='yyyy-MM-dd' /></td>");
	            var options="";
	            options+="<option value='含特定原材料产品，受行业影响，产能受限，交期延长'>含特定原材料产品，受行业影响，产能受限，交期延长</option>";
	            options+="<option value='大订单产品，在不影响销售的情况分批次出货，交期较长'>大订单产品，在不影响销售的情况分批次出货，交期较长</option>";
	            options+="<option value='付款推迟，延后出货'>付款推迟，延后出货</option>";
	            options+="<option value='恰逢法定假期，推迟出货'>恰逢法定假期，推迟出货</option>";
	            options+="<option value='质检排程紧张，推迟出货'>质检排程紧张，推迟出货</option>";
	            options+="<option value='产品制作工艺受限，推迟出货'>产品制作工艺受限，推迟出货</option>";
	            options+="<option value='缓解资金压力，暂缓出货'>缓解资金压力，暂缓出货</option>";
	            options+="<option value='等待运输，推迟出货'>等待运输，推迟出货</option>";
	            options+="<option value='新品，第一次下单，需确认事宜较多'>新品，第一次下单，需确认事宜较多</option>";
	            options+="<option value='装箱数变更，尾数产品不足一箱，需和下批次订单拼货凑箱'>装箱数变更，尾数产品不足一箱，需和下批次订单拼货凑箱</option>";
	            options+="<option value='产品模具改进或替换，交期延长'>产品模具改进或替换，交期延长</option>";
	            options+="<option value='产品设计变更，需确认事宜较多，交期延长'>产品设计变更，需确认事宜较多，交期延长</option>";
	            options+="<option value='供应商出货延后，生产交期跟不上'>供应商出货延后，生产交期跟不上</option>";
	            options+="<option value='供应商出货延后，原材料缺少'>供应商出货延后，原材料缺少</option>";
	            options+="<option value='新品分批次出货,避免仓库贴码工作量大'>新品分批次出货,避免仓库贴码工作量大</option>";
	            options+="<option value='供应商备货，提前出货'>供应商备货，提前出货</option>";
	            tr.append("<td><select class='remark' style='width:90%'><option value=''></option>"+options+"<option value='manutal'>手动填写</option></select><input name='remark' class='remarkInput' type='text' style='margin-top:5px;display: none;'/></td>");
	            tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>删除</a></td>");
	            table.append(tr);
	            tr.find(".remark").select2();
			});
			
			$("#contentTable").on('click', '.save-row', function(e){
				//整体保存单个产品预计收货
				e.preventDefault();
				var purchaseOrderId =$("#purchaseOrderId").val();
				var mainTr=$(this).parent();
				var table=mainTr.find(".deliveryTable");
				if(table.find(".deliveryTr")){
					//检查订单未收货数    和分批预收货的未收货数相等 
					var unReceiveQuantity =0;
					var dateSta=0;
					var quantitySta=0;
					var bigSta=0;
					table.find(".deliveryTr").each(function(){
						if($(this).find(".quantity").val()){
							unReceiveQuantity+=$(this).find(".quantity").val()-$(this).find(".quantityReceived").val();
						}else{
							quantitySta=1;
							return;
						}
						if(!$(this).find(".deliveryDate").val()){
							dateSta=1;
							return;
						}
						if($(this).find(".quantity").val()<$(this).find(".quantityReceived").val()){
							bigSta=1;
							return;
						}
					});
					
					if(bigSta==1){
						top.$.jBox.tip("总数不能小于已收货数！", 'info',{timeout:2000});
						return false;
					}
					
					if(quantitySta==1){
						top.$.jBox.tip("预收货总数不能为空！", 'info',{timeout:2000});
						return false;
					}
					if(dateSta==1){
						top.$.jBox.tip("预收货日期不能为空！", 'info',{timeout:2000});
						return false;
					}
					var orderUnReceivedQuantity=mainTr.parent().find(".quantityUnReceived").value;
					if(parseInt(orderUnReceivedQuantity)!=parseInt(unReceiveQuantity)){
						top.$.jBox.tip("订单未收货总数不等于分批未收货总数，请检查！", 'info',{timeout:2000});
						return false;
					};
				}else{
					return false;
				}
				
				var itemId =mainTr.find(".orderItemId").val();
				
				table.find(".deliveryTr").each(function(){
					//按行保存
					var tr=$(this);
					var deliveryDateId =tr.find(".deliveryDateId").val();
					var deliveryDate=tr.find(".deliveryDate").val();
					var quantityReceived=tr.find(".quantityReceived").val();
					var quantity=tr.find(".quantity").val();
					var quantityOffReceived=tr.find(".quantityOffReceived").val();
					var quantityOff=tr.find(".quantityOff").val();
					var delIds=mainTr.find(".delDateIds").val();
					var remark=encodeURI(tr.find(".remarkInput").val());
					var param = {};
					param.quantity			 = quantity;
					param.quantityReceived   = quantityReceived;
					param.quantityOff		 = quantityOff;
					param.quantityOffReceived = quantityOffReceived;
					param.itemId 			 = itemId;
					param.deliveryDateId     = deliveryDateId;
					param.deliveryDate       = deliveryDate;
					param.delIds             = delIds;
					param.purchaseOrderId    = purchaseOrderId;
					param.remark             = remark;
					$.get("${ctx}/psi/lcPurchaseOrder/updateDeliveryDate?"+$.param(param),function(data){});
				});
				
				top.$.jBox.tip("保存成功！", 'info',{timeout:2000});
				mainTr.find(".remove-row").css("display","none");
				mainTr.find(".add-row").css("display","none");
				mainTr.find(".save-row").css("display","none");
				mainTr.find("input[type='text']").attr("readonly","readonlly");
			});
			
			
			
			$('#contentTable').on('click', '.remove-row', function(e){
				  e.preventDefault();
				  var tr = $(this).parent().parent();
				  var deliveryDateId =tr.find(".deliveryDateId").val();
				  if(deliveryDateId){
					  var mainTr =tr.parent().parent().parent();
					  var delIds=mainTr.find(".delDateIds").val();
					  if(delIds&&delIds!=''){
						  delIds=delIds+","+deliveryDateId;
					  }else{
						  delIds=deliveryDateId;
					  }
					  mainTr.find(".delDateIds").val(delIds);
				  }
				tr.remove(); 
			});
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					var purchaseOrderId =$("#purchaseOrderId").val();
					var unReceiveQuantity =0;
					var unReceiveOffQuantity =0;
					var dateSta=0;
					var quantitySta=0;
					var remarkSta=0;
					var bigSta=0;
					var unSta=0;
					var tips="";
					$("#contentTable tbody tr.mainTr").each(function(){
						var mainTr=$(this);
						//整体保存单个产品预计收货
						var table=mainTr.find(".deliveryTable");
							var orderUnReceivedQuantity=mainTr.find(".quantityUnReceived").val();
							var orderUnReceivedQuantityOff=mainTr.find(".quantityOffUnReceived").val();
							unReceiveQuantity =0;
							unReceiveOffQuantity =0;
							remarkSta=0;
							dateSta=0;
							quantitySta=0;
							bigSta=0;
							unSta=0;
							tips=mainTr.find("td").first().text();
							var poDelivery=mainTr.find(".poDelivery").val();
							//检查订单未收货数    和分批预收货的未收货数相等 
							if(table.find("tr.deliveryTr").size()>0){
									table.find("tr.deliveryTr").each(function(){
										if($(this).find(".quantity").val()){
											unReceiveQuantity+=$(this).find(".quantity").val()-$(this).find(".quantityReceived").val();
										}else{
											quantitySta=1;
											tips+="预收货总数不能为空！";
											return false;
										}
										
										if($(this).find(".quantityOff").val()){
											unReceiveOffQuantity+=$(this).find(".quantityOff").val()-$(this).find(".quantityOffReceived").val();
										}else{
											quantitySta=1;
											tips+="预收货线下数不能为空！";
											return false;
										}
										
										var deliveryDate=parseInt($(this).find(".deliveryDate").val().replace("-","").replace("-",""));
										var remark =$(this).find(".remarkInput").val();
										if(!deliveryDate){
											dateSta=1;
											tips+="预收货日期不能为空！";
											return false;
										}
										if(deliveryDate>parseInt(poDelivery)&&remark==''){
											remarkSta=1;
											tips+="备注不能为空！";
											return false;
										}
										if(parseInt($(this).find(".quantity").val())<parseInt($(this).find(".quantityReceived").val())){
											bigSta=1;
											tips+="总数不能小于已收货数！";
											return false;
										}
										if(parseInt($(this).find(".quantityOff").val())<parseInt($(this).find(".quantityOffReceived").val())){
											bigSta=1;
											tips+="线下数不能小于线下已收货数！";
											return false;
										}
									});
								
									if(bigSta==1||quantitySta==1||dateSta==1||remarkSta==1){
										 return false;
									}
									
									if(parseInt(orderUnReceivedQuantity)!=parseInt(unReceiveQuantity)){
										tips+="订单未收货总数不等于分批未收货总数，请检查！";
										unSta=1;
										return false;
									}
									
									if(parseInt(orderUnReceivedQuantityOff)!=parseInt(unReceiveOffQuantity)){
										tips+="订单未收货线下数不等于分批未收货线下总数，请检查！"; 
										unSta=1;
										return false;
									}
							}else{
								var deliveryDate=parseInt(mainTr.find("input[name='actualDeliveryDate']").val().replace("-","").replace("-",""));
								var remark =mainTr.find("input[name='remark']").val();
								var quantityUnReceived =mainTr.find("input[name='quantityUnReceived']").val();
								if(deliveryDate>parseInt(poDelivery)&&!remark&&parseInt(quantityUnReceived>0)){
									remarkSta=1;
									tips+="备注不能为空！";
									return false;
								}
						}
					});
					
					if(bigSta==1||quantitySta==1||dateSta==1||unSta==1||remarkSta==1){
						top.$.jBox.tip(tips, 'info',{timeout:2000});
						 return false;
					 }
					
					$("#btnSureSubmit").attr("disabled","disabled");
					
					$("#contentTable tbody tr").each(function(){
						var mainTr=$(this);
						//整体保存单个产品预计收货
						var table=mainTr.find(".deliveryTable");
						var itemId =mainTr.find(".orderItemId").val();
						var delIds =mainTr.find(".delDateIds").val();
						if(table.find(".deliveryTr").length){
							table.find(".deliveryTr").each(function(){
								//按行保存
								var tr=$(this);
								var deliveryDateId =tr.find(".deliveryDateId").val();
								var deliveryDate=tr.find(".deliveryDate").val();
								var quantityReceived=tr.find(".quantityReceived").val();
								var quantity=tr.find(".quantity").val();
								var quantityOffReceived=tr.find(".quantityOffReceived").val();
								var quantityOff=tr.find(".quantityOff").val();
								var delIds=mainTr.find(".delDateIds").val();
								var remark=encodeURI(tr.find(".remarkInput").val());
								var param = {};
								param.quantity			 = quantity;
								param.quantityReceived   = quantityReceived;
								param.quantityOff		 = quantityOff;
								param.quantityOffReceived   = quantityOffReceived;
								param.itemId 			 = itemId;
								param.deliveryDateId     = deliveryDateId;
								param.deliveryDate       = deliveryDate;
								param.delIds             = delIds;
								param.purchaseOrderId    = purchaseOrderId;
								param.remark             = remark;
								$.ajax({
									 type: 'get',
									 async:false,
									 url: '${ctx}/psi/lcPurchaseOrder/updateDeliveryDate' ,
									 data:$.param(param),
									 success:function(data){}
								});
							});
						}else{
							if(delIds){
								var param = {};
								param.delIds   = delIds;
								$.ajax({
									 type: 'get',
									 async:false,
									 url: '${ctx}/psi/lcPurchaseOrder/deleteDeliveryDate' ,
									 data:$.param(param),
									 success:function(data){}
								});
							}
						}
						
					});
					
				
					$("#contentTable tbody tr").each(function(i,j){
						$(j).find("select").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
							}
						});
						
						$(j).find("input[type!='']").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
							}
						});
					});
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("Entered incorrectly, please correct");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/lcPurchaseOrder/">(理诚)采购订单列表</a></li>
		<li class="active"><a href="#">(理诚)采购订单预计交期编辑</a></li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="purchaseOrder" action="${ctx}/psi/lcPurchaseOrder/deliveryDateSave" method="post" class="form-horizontal">
		<input type="hidden" id="purchaseOrderId" name="id"  value="${purchaseOrder.id}"/>
	    <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>订单号</b>:</label>
				<div class="controls">
					${purchaseOrder.orderNo}
				</div>
			</div>
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label"><b>供应商</b>:</label>
				<div class="controls" >
					${purchaseOrder.supplier.nikename}
				</div>
			</div>
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<label class="control-label"><b>货币类型</b>:</label>
				<div class="controls" >
					${purchaseOrder.currencyType}
				</div>
			</div>
		</div>	
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 8%">产品名称</th>
				   <th style="width: 3%">国家</th>
				   <th style="width: 5%">PO交期</th>
				   <th style="width: 10%">预计交期</th>
				   <th style="width: 15%">备注</th>
				   <th style="width: 5%">未收(线下)</th>
				   <th style="width: 60%">操作</th>    
			</tr>
		</thead>
		
		<c:if test="${not empty purchaseOrder.items}" >
		<tbody>
			<c:forEach items="${purchaseOrder.items}" var="item" >
				<tr class="mainTr">
					<td>${item.productNameColor}</td>
					<td>${item.countryCode}</td>
					<td><fmt:formatDate value="${item.deliveryDate}" pattern="yyyy-MM-dd"/></td>
					<td><input type="hidden" name="id" value="${item.id}"/>   
					 <input type="text" name="actualDeliveryDate" class="Wdate" style="width:90%" value="<fmt:formatDate value="${item.actualDeliveryDate}" pattern="yyyy-MM-dd"/>" /></td>
					<td>
					 <select  class="remark" style="width: 90%">
				         <option value='' ></option>
						 <option value='含特定原材料产品，受行业影响，产能受限，交期延长'>含特定原材料产品，受行业影响，产能受限，交期延长</option>
						 <option value='大订单产品，在不影响销售的情况分批次出货，交期较长'>大订单产品，在不影响销售的情况分批次出货，交期较长</option>
						 <option value='付款推迟，延后出货'>付款推迟，延后出货</option>
						 <option value='恰逢法定假期，推迟出货'>恰逢法定假期，推迟出货</option>
						 <option value='质检排程紧张，推迟出货'>质检排程紧张，推迟出货</option>
						 <option value='产品制作工艺受限，推迟出货'>产品制作工艺受限，推迟出货</option>
						 <option value='缓解资金压力，暂缓出货'>缓解资金压力，暂缓出货</option>
						 <option value='等待运输，推迟出货'>等待运输，推迟出货</option>
						 <option value='新品，第一次下单，需确认事宜较多'>新品，第一次下单，需确认事宜较多</option>
						 <option value='装箱数变更，尾数产品不足一箱，需和下批次订单拼货凑箱'>装箱数变更，尾数产品不足一箱，需和下批次订单拼货凑箱</option>
						 <option value='产品模具改进或替换，交期延长'>产品模具改进或替换，交期延长</option>
						 <option value='产品设计变更，需确认事宜较多，交期延长'>产品设计变更，需确认事宜较多，交期延长</option>
						 <option value='供应商出货延后，生产交期跟不上'>供应商出货延后，生产交期跟不上</option>
						 <option value='供应商出货延后，原材料缺少'>供应商出货延后，原材料缺少</option>
						 <option value='新品分批次出货,避免仓库贴码工作量大'>新品分批次出货,避免仓库贴码工作量大</option>
						  <option value='供应商备货，提前出货'>供应商备货，提前出货</option>
						 <option value='manutal' ${not empty item.remark?'selected':'' }>手动填写</option>
				    </select>
					<input type="text" name="remark" class="remarkInput" style="width:90%;display:${not empty item.remark?'block':'none'}" value="${item.remark}" />
					</td>
					<td>
					<input class="quantityUnReceived" type="hidden" value="${item.quantityUnReceived}"/>
					<input class="quantityOffUnReceived" type="hidden" value="${item.quantityOffUnReceived}"/>
					${item.quantityUnReceived}(${item.quantityOffUnReceived})</td>
					<td>
						<input class="delDateIds" type='hidden' value="" />
						<a href="#" class="add-row" style="padding-right:30px;float:right;display:${item.quantityUnReceived eq 0 ?'none':''}"   ><span class="icon-plus"></span><span class="label  label-success">增加预收日期</span></a>
						<input type="hidden" value="${item.id}" class="orderItemId" />  
						<input type="hidden" class="poDelivery" value="<fmt:formatDate value='${item.deliveryDate}' pattern='yyyyMMdd'/>"/>
						<table class="deliveryTable table table-striped table-bordered table-condensed">
							<c:if test="${not empty item.deliveryDateList}">
								<tr><th style="width:10%">总数</th><th style="width:10%">线下数</th><th style="width:10%">已收</th><th style="width:10%">线下已收</th><th style="width:15%">交期</th><th style="width:25%">备注</th><th style="width:10%">操作</th></tr>
								<c:forEach var="deliveryInfo" items="${item.deliveryDateList}">
								<tr class="deliveryTr">
									<td> <input type="hidden" class="deliveryDateId" value="${deliveryInfo.id}"/>
									<input type="text" style="width: 80%"  class="quantity"  value="${deliveryInfo.quantity}" ${deliveryInfo.unReceived eq 0 ?'readonly':''} /></td>
									<td><input type="text" style="width: 80%"  class="quantityOff"  value="${deliveryInfo.quantityOff}" ${deliveryInfo.unReceivedOff eq 0 ?'readonly':''} /></td>
									<td> <input type="text"  style="width: 80%"   class="quantityReceived"   value="${deliveryInfo.quantityReceived}" readonly="readonly" /></td>
									<td> <input type="text"  style="width: 80%"   class="quantityOffReceived"   value="${deliveryInfo.quantityOffReceived}" readonly="readonly" /></td> 
	                                <td> <input type="text"   style="width: 80%"   class="Wdate deliveryDate" ${deliveryInfo.unReceived eq 0 ?'readonly':''} value='<fmt:formatDate value="${deliveryInfo.deliveryDate}" pattern="yyyy-MM-dd"/>' /></td>
	                                <td> 
	                                <select class="remark" style="width: 90%">
								         <option value='' ></option>
										 <option value='含特定原材料产品，受行业影响，产能受限，交期延长'>含特定原材料产品，受行业影响，产能受限，交期延长</option>
										 <option value='大订单产品，在不影响销售的情况分批次出货，交期较长'>大订单产品，在不影响销售的情况分批次出货，交期较长</option>
										 <option value='付款推迟，延后出货'>付款推迟，延后出货</option>
										 <option value='恰逢法定假期，推迟出货'>恰逢法定假期，推迟出货</option>
										 <option value='质检排程紧张，推迟出货'>质检排程紧张，推迟出货</option>
										 <option value='产品制作工艺受限，推迟出货'>产品制作工艺受限，推迟出货</option>
										 <option value='缓解资金压力，暂缓出货'>缓解资金压力，暂缓出货</option>
										 <option value='等待运输，推迟出货'>等待运输，推迟出货</option>
										 <option value='新品，第一次下单，需确认事宜较多'>新品，第一次下单，需确认事宜较多</option>
										 <option value='装箱数变更，尾数产品不足一箱，需和下批次订单拼货凑箱'>装箱数变更，尾数产品不足一箱，需和下批次订单拼货凑箱</option>
										 <option value='产品模具改进或替换，交期延长'>产品模具改进或替换，交期延长</option>
										 <option value='产品设计变更，需确认事宜较多，交期延长'>产品设计变更，需确认事宜较多，交期延长</option>
										 <option value='供应商出货延后，生产交期跟不上'>供应商出货延后，生产交期跟不上</option>
										 <option value='供应商出货延后，原材料缺少'>供应商出货延后，原材料缺少</option>
										 <option value='新品分批次出货,避免仓库贴码工作量大'>新品分批次出货,避免仓库贴码工作量大</option>
										  <option value='供应商备货，提前出货'>供应商备货，提前出货</option>
										 <option value='manutal' ${not empty item.remark?'selected':'' }>手动填写</option>
								    </select>
	                                <input type="text" class="remarkInput"  style="width:90%;display:${not empty deliveryInfo.remark?'block':'none'}" value="${deliveryInfo.remark}" />
	                                </td>
	                                <td> <c:if test="${deliveryInfo.quantityReceived==0}"><a href='#' class='remove-row'><span class='icon-minus'></span>删除</a></c:if>&nbsp;&nbsp;&nbsp;&nbsp;</td>
								</tr>
								</c:forEach>
							</c:if>
						</table>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</c:if>
		
	</table>
	
	<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
