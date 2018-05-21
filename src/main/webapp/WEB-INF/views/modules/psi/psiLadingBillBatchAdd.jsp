<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>新建收货单</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			new tabTableInput("inputForm","text");
			
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
				});
			
			$("#supplier").on("change",function(e){
				var params = {};
				params['supplier.id'] = $(this).val();
				window.location.href = "${ctx}/psi/psiLadingBill/add?"+$.param(params);
			});
			    
			eval('var skuMap=${skuMap}');
			eval('var fnskuMap=${fnskuMap}');
			
			eval('var totalMap=${totalMap}');
			eval('var productMap=${productMap}');
			eval('var ladingMap=${ladingMap}');
			eval('var batchTotalMap=${batchTotalMap}');  
			
			// 生成要编辑表格 
			var createTable = $('#contentTable');
			for(var key in ladingMap){
				var ladingArrays=ladingMap[key];
				var total =batchTotalMap[key];
				var tbody =$("<tbody></tbody>");
				if(ladingArrays.length>0){
					for(var i=0;i<ladingArrays.length;i++){
						var ladingDto = ladingArrays[i];
						
						var curSku = "";
						var options ="";
						var disable ="";
						if(skuMap[key]){
							if(skuMap[key].length==1){
								disable="disabled";
							}
							//根据名字查出sku
							for(var a in skuMap[key]){
								var skuStr = skuMap[key][a];
								var isBarcode = skuStr.split('|')[0];
								var sku = skuStr.split('|')[1];
								if(isBarcode=='1'){
									curSku=sku;
								}
								var fnsku = fnskuMap[sku];
								options=options+"<option "+(isBarcode=='1'?'selected':'')+" value='"+sku+"'>"+sku+"["+fnsku+"]</option>";
							}
							
							if(curSku==""){
								curSku=skuMap[key][0].split('|')[1];
							}
						}
							
						if(i==0){
							var tr = $("<tr></tr>");
				            var td = "<td style='text-align:center'> <select style='width: 98%' class='productId'  name='productConName'>";
				            for (var key1 in totalMap) {
				            	if(ladingMap[key1]==null){
				            		td = td.concat("<option value='"+key1+"'>"+key1+"</option>");
				            	}	
							}
				            td = td.concat("<option value='"+key+"'>"+key+"</option>");
				            td = td.concat("</select></td>");
				            tr.append(td);
				            tr.append("<td><input type='hidden' name='id'/><input type='hidden' name='purchaseOrderItem.id'/><input type='hidden' class='balanceOffline'/><input type='hidden' name='itemPrice'/><input  style='width: 80%' type='text' name='canLadingTotal' readonly='readonly' /></td>");
				            tr.append("<td> <input type='text' style='width: 80%' readonly class='packQuantity' /></td>");
				            tr.append("<td><select class='skuS' style='width:98%' "+disable+">"+options+"</select><input type='hidden' name='sku'/></td>");
				            tr.append("<td> <a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
				            tr.append("<td> <input  style='width: 80%' type='text' class='canLadingQuantity' readonly='readonly' /></td>");
				            tr.append("<td> <input  style='width: 80%' type='text' name='quantityLading' /></td>");
				            tr.append("<td> <input  style='width: 80%' type='text' name='quantityOffLading' /></td>");
				            tr.append("<td><input class='poDeliveryDate' value='"+ladingDto.poDeliveryDate+"' type='hidden' /> <input  style='width: 90%' type='text' name='remark' /></td>");
				            tr.append("<td> <input type='text' style='width: 80%' name='quantitySpares' /></td>");
				            tr.append("<td style='text-align:center'> <a href='' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除</a></td>");
				            tr.find("select[name='productConName']").select2().select2("val",key);
				            
				            tr.find("td:first").attr("rowSpan", ladingArrays.length).css("vertical-align","middle").css("text-align","center");
							tr.find("td:first").next("td").attr("rowSpan",ladingArrays.length).css("vertical-align","middle").css("text-align","center");
							tr.find("td:first").next("td").next("td").attr("rowSpan",ladingArrays.length).css("vertical-align","middle").css("text-align","center");
							tr.find("td:last").attr("rowSpan",ladingArrays.length).css("vertical-align","middle").css("text-align","center");
						    tr.find("input[name='purchaseOrderItem.id']").val(ladingDto.purchaseOrderItemId);
						    var hrefs="${ctx}/psi/purchaseOrder/view?id="+ladingDto.purchaseOrderId;   
						    tr.find(".purchaseOrderNo").text(ladingDto.purchaseOrderNo);
					        tr.find(".canLadingQuantity").val(ladingDto.quantityLading);
					        tr.find(".balanceOffline").val(ladingDto.quantityOffLading);
							tr.find(".orderId").attr('href',hrefs); 
							tr.find("input[name='quantityLading']").val(ladingDto.quantityLading);
							tr.find("input[name='quantityOffLading']").val(ladingDto.quantityOffLading);
							tr.find(".packQuantity").val(ladingDto.packQuantity);
							tr.find("input[name='itemPrice']").val(ladingDto.itemPrice);
							
							tr.find("select.skuS").select2();
							tr.find("input[name='sku']").val(curSku);
				            tbody.append(tr);
						}else{
							var tr =$("<tr class='notFirstRow'></tr>");
							tr.append("<td><select class='skuS' style='width:98%' "+disable+">"+options+"</select><input type='hidden' name='sku'/></td>");
				            tr.append("<td><input type='hidden' name='id'/><input type='hidden' name='purchaseOrderItem.id'/><input type='hidden' class='balanceOffline'/><input type='hidden' name='itemPrice'/><input type='hidden' name='oldQuantityLading'/><input type='hidden' name='productConName'/><input type='hidden' name='itemPrice'/><input type='hidden' class='packQuantity'/><a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
				            tr.append("<td> <input type='text' style='width: 80%' class='canLadingQuantity' readonly='readonly' /></td>");
				            tr.append("<td> <input type='text' style='width: 80%' name='quantityLading' /></td>");
				            tr.append("<td> <input type='text' style='width: 80%' name='quantityOffLading' /></td>");
				            tr.append("<td> <input class='poDeliveryDate' value='"+ladingDto.poDeliveryDate+"' type='hidden' /> <input type='text' style='width: 90%' name='remark' /></td>");
				            tr.append("<td> <input type='text' style='width: 80%' name='quantitySpares' /></td>");
						    tr.find("input[name='purchaseOrderItem.id']").val(ladingDto.purchaseOrderItemId);
						    var hrefs="${ctx}/psi/purchaseOrder/view?id="+ladingDto.purchaseOrderId;   
							tr.find(".orderId").attr('href',hrefs); 
							tr.find(".purchaseOrderNo").text(ladingDto.purchaseOrderNo);
					        tr.find(".canLadingQuantity").val(ladingDto.quantityLading);
					        tr.find(".balanceOffline").val(ladingDto.quantityOffLading);
					        tr.find("input[name='quantityLading']").val(ladingDto.quantityLading);
					        tr.find("input[name='quantityOffLading']").val(ladingDto.quantityOffLading);
							tr.find(".packQuantity").val(ladingDto.packQuantity);
							tr.find("input[name='itemPrice']").val(ladingDto.itemPrice);
							tr.find("input[name='productConName']").val(key);
							
							tr.find("select.skuS").select2();
							tr.find("input[name='sku']").val(curSku);
				            tbody.append(tr);
						}
					}
				tbody.find("input[name='canLadingTotal']").val(total);
				createTable.append(tbody);
				}
			}

			$("select.skuS").live("click",function(){
				var tr =$(this).parent().parent();
				tr.find("input[name='sku']").val($(this).val());
			})
			
			
			$("#inputForm").on("change",".productId",function(e){
				var productVal = e.val;
				if(e.removed){
					var removeVal = e.removed.id;
					$("select.productId").each(function(){
	    				if($(this).select2("val")!=productVal){
	    					$(this).find("option[value='"+productVal+"']").remove();    					
	    					$(this).append("<option value='"+removeVal+"'>"+removeVal+"</option>");
	    				}
	    			});
				}
				var tbody =$(this).parent().parent().parent();
				var ii=tbody[0].rows.length;
				if(ii>1){
					tbody.find("tr.notFirstRow").each(function(){
						$(this).remove();
					});
				}
				var canLadingTotal=totalMap[$(this).val()];  
				var aaa =[];
				var key = $(this).val();
				aaa =productMap[key];
				if(aaa&&aaa.length>0){
					for(var i=0;i<aaa.length;i++){
						var curSku = "";
						var options ="";
						var disable ="";
						if(skuMap[key]){
							if(skuMap[key].length==1){
								disable="disabled";
							}
							//根据名字查出sku
							for(var a in skuMap[key]){
								var skuStr = skuMap[key][a];
								var isBarcode = skuStr.split('|')[0];
								var sku = skuStr.split('|')[1];
								if(isBarcode=='1'){
									curSku=sku;
								}
								var fnsku = fnskuMap[sku];
								options=options+"<option "+(isBarcode=='1'?'selected':'')+" value='"+sku+"'>"+sku+"["+fnsku+"]</option>";
							}
							
							if(curSku==""){
								curSku=skuMap[key][0].split('|')[1];
							}
						}
						
						if(i==0){
							var tr=$(this).parent().parent();
							tr.find(".canLadingTotal").val(canLadingTotal);
							tr.find(".purchaseOrderNo").text(aaa[i][1]);
							tr.find(".canLadingQuantity").val(aaa[i][2]);
							tr.find("input[name='quantityLading']").val(aaa[i][2]);
							tr.find("input[name='quantityOffLading']").val(aaa[i][8]);
							tr.find(".balanceOffline").val(aaa[i][8]);
							tr.find("input[name='purchaseOrderItem.id']").val(aaa[i][3]);
							tr.find("input[name='itemPrice']").val(aaa[i][4]);
							var hrefs="${ctx}/psi/purchaseOrder/view?id="+aaa[i][5]; 
							tr.find(".orderId").attr('href',hrefs); 
							tr.find(".packQuantity").val(aaa[i][6]);
							tr.find("select.skuS").select2().empty();
							tr.find("select.skuS").select2("data",[]);
							tr.find("select.skuS").select2().append(options).change();
							tr.find("input[name='sku']").val(curSku);
							if(disable=="disabled"){
								tr.find("select.skuS").attr("disabled","");
							}else{
								tr.find("select.skuS").removeAttr("disabled");
							}
							$(this).parent().parent().parent().each(function(){
							    $(this).find("td:first").attr("rowSpan", aaa.length).css("vertical-align","middle").css("text-align","center");
							    $(this).find("td:first").next("td").attr("rowSpan",aaa.length).css("vertical-align","middle").css("text-align","center");
							    $(this).find("td:first").next("td").next("td").attr("rowSpan",aaa.length).css("vertical-align","middle").css("text-align","center");
							    $(this).find("td:last").attr("rowSpan",aaa.length).css("vertical-align","middle").css("text-align","center");

							});
						}else{
							var tr =$("<tr class='notFirstRow'></tr>");
							tr.append("<td><select class='skuS' style='width:98%' "+disable+">"+options+"</select><input type='hidden' name='sku'/></td>");
				            tr.append("<td> <input type='hidden' name='productConName'/> <input type='hidden' class='balanceOffline'/><input type='hidden' name='purchaseOrderItem.id'/><input type='hidden' name='itemPrice'/> <input type='hidden' class='packQuantity'/> <a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
				            tr.append("<td> <input type='text' style='width: 80%' class='canLadingQuantity' readonly='readonly' /></td>");
				            tr.append("<td> <input type='text' style='width: 80%' name='quantityLading' /></td>");
				            tr.append("<td> <input type='text' style='width: 80%' name='quantityOffLading' /></td>");
				            tr.append("<td> <input type='text' style='width: 90%' name='remark' /></td>");
				            tr.append("<td> <input type='text' style='width: 80%' name='quantitySpares' /></td>");
				            tr.find("input[name='productConName']").val($(this).val());
				            tr.find(".purchaseOrderNo").text(aaa[i][1]);
							tr.find(".canLadingQuantity").val(aaa[i][2]);
							tr.find("input[name='quantityLading']").val(aaa[i][2]);
							tr.find("input[name='quantityOffLading']").val(aaa[i][8]);
							tr.find(".balanceOffline").val(aaa[i][8]);
							tr.find("input[name='purchaseOrderItem.id']").val(aaa[i][3]);
							tr.find("input[name='itemPrice']").val(aaa[i][4]);
							var hrefs="${ctx}/psi/purchaseOrder/view?id="+aaa[i][5];   
							tr.find(".orderId").attr('href',hrefs); 
							tr.find(".packQuantity").val(aaa[i][6]);
							tr.find("select.skuS").select2();
							tr.find("input[name='sku']").val(curSku);
				           	tbody.append(tr);
						}
					}
				}
			});
			
			
			$('#contentTable').on('click', '.remove-row', function(e){
				  e.preventDefault();
				  if($('#contentTable tbody').size()>1){
					  var tbody = $(this).parent().parent().parent();
					  var id = tbody.find(".productId").select2("val");
					  tbody.remove();
					  if(id){
						  $("select.productId").each(function(){
		          				$(this).append("<option value='"+id+"'>"+id+"</option>");
		          		  });
					  }
				  }
			});
			
			$('#add-row').on('click', function(e){
				e.preventDefault();
			    var table1 = $('#contentTable')[0];
	           	var tbody =$("<tbody></tbody>");
	            var tr = $("<tr></tr>");
	            var td = "<td style='text-align:center'> <select style='width: 98%' class='productId'  name='productConName'>"
	            var i = 0 ;
	            for (var key in totalMap) {
	            	var flag = 0;
	            	$("select.productId").each(function(){
        				if(key==$(this).select2("val")){
        					flag =1;
        				}
        			});
	            	if(flag==0){
	            		if(i==0){
	            			$("select.productId").each(function(){
	            				$(this).find("option[value='"+key+"']").remove();
	            			});
	            		}
	            		td = td.concat("<option value='"+key+"'>"+key+"</option>");
	            		i++;
	            	}	
				}
	            td = td.concat("</select></td>");
	            tr.append(td);
	            
	            tr.append("<td><input type='hidden' name='purchaseOrderItem.id'/> <input type='hidden' class='balanceOffline'/> <input type='hidden' name='itemPrice'/><input  style='width: 80%' type='text' class='canLadingTotal' readonly='readonly' /></td>");
	            tr.append("<td> <input  style='width: 80%' type='text' class='packQuantity' readonly='readonly' /></td>");
	            tr.append("<td> <select class='skuS' style='width:98%' ></select><input type='hidden' name='sku'/></td>");
	            tr.append("<td> <a target='_blank' class='orderId purchaseOrderNo' href='#'/></td>");
	            tr.append("<td> <input  style='width: 80%' type='text' class='canLadingQuantity' readonly='readonly' /></td>");
	            tr.append("<td> <input  style='width: 80%' type='text' name='quantityLading' /></td>");
	            tr.append("<td> <input  style='width: 80%' type='text' name='quantityOffLading' /></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' name='remark' /></td>");
	            tr.append("<td> <input  style='width: 80%' type='text' name='quantitySpares' /></td>");
	            tr.append("<td style='text-align:center'> <a href='' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除</a></td>");
	            tr.find(".productId").select2();
	           
	            tbody.append(tr);
	            $(table1).append(tbody);
	            
	            tr.find(".productId").change();
			});
			
			
			$("#inputForm").validate({
				rules:{
					"productConName":{
						"required":true,
					}
				},
				messages:{
					"productConName":{"required":'产品名不能为空'}
				},
				submitHandler: function(form){
					var numberflag  =true;
					var balanceflag  =true;
					var poSta        =true;
					var deliveryDate = $("input[name='deliveryDate']").val();
					$("#contentTable tbody tr").each(function(i,j){
						if($(this).find("input[name='quantityLading']").val()!=''&&$(this).find("input[name='quantityLading']").val()!=0){
							var quantityLading = $(this).find("input[name='quantityLading']").val();
							var quantityOffLading =$(this).find("input[name='quantityOffLading']").val();
							var balanceOff =$(this).find(".balanceOffline").val();
							var canQuantity =$(this).find(".canLadingQuantity").val();
							if((canQuantity-quantityLading<0)||(balanceOff-quantityOffLading<0)||(quantityLading-quantityOffLading<0)){
								numberflag = false;
								return ;
							}
							if((canQuantity-quantityLading)<(balanceOff-quantityOffLading)){
								balanceflag=false;
								return;
							}
							var remark =$(this).find("input[name='remark']").val();
							var poDeliveryDate = $(this).find(".poDeliveryDate").val();
							if(poDeliveryDate&&poDeliveryDate<parseInt(deliveryDate.replace("-","").replace("-",""))&&remark==''){
								poSta=false;
							}
							
						}
					});    
					
					
					if(!numberflag){
						top.$.jBox.tip("收货数不能大于可收货数,线下数不能大于收货总数,线下数不能大于线下可收货数！","info",{timeout:3000});
						return false;
					}
					
					if(!balanceflag){
						top.$.jBox.tip("总的收货剩余数不能小于线下剩余数！","info",{timeout:3000});
						return false;
					}
					
					
					
					var packflag  =true;
					$("#contentTable tbody tr").each(function(i,j){
						var ladingQuantity  = $(this).find("input[name='quantityLading']").val();
						if(packflag&&ladingQuantity!=''&&ladingQuantity!=0){
							var packingQuantity = $(this).find(".packQuantity").val();
							var a=ladingQuantity%packingQuantity;
							if(a!=0&&packflag){
								packflag = false;
							};
						}
					});
					
					
					var oneFlag = 0;
					$("#contentTable tbody tr").each(function(i,j){
						var ladingQuantity  = $(this).find("input[name='quantityLading']").val();
						if(ladingQuantity!=''&&ladingQuantity!=0){
							oneFlag = oneFlag+1;
							return;
						}
					});
					
					if(oneFlag==0){
						top.$.jBox.tip("必须有一行的收货单数为大于0,并且为装箱数的整数倍","info",{timeout:3000});
						return false;
					};
					
					
					if(!packflag){
						top.$.jBox.confirm('装箱数不为整数被,确定要提交吗', '提示', function (v, h, f) {
				            if (v == 'ok') {
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
								
								//把所有disable的select放开
								$("select[disabled]").each(function(){
									$(this).removeAttr("disabled");
								});
								
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
				            }
					  });
					}else{
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
						
						//把所有disable的select放开
						$("select[disabled]").each(function(){
							$(this).removeAttr("disabled");
						});
						
						form.submit();
						$("#btnSubmit").attr("disabled","disabled");
					}
					
					
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"))
					
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiLadingBill/">收货单列表</a></li>
		<li class="active"><a href="${ctx}/psi/psiLadingBill/form?id=${psiLadingBill.id}">新建收货单</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiLadingBill" action="${ctx}/psi/psiLadingBill/addSave" method="post" class="form-horizontal">
	  <input type='hidden' name="billNo" 				value="${psiLadingBill.billNo}">
	  <input type='hidden' name="currencyType" 			value="${psiLadingBill.currencyType}">
	  <input type='hidden' name="supplier.id" 		    value="${psiLadingBill.supplier.id}">
	    <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
			<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:33%">
				
				<label class="control-label"><b>供应商</b>:</label>
				<div class="controls" >   
				<span>
					<select style="width:150px;" id="supplier" disabled>
						
						<c:forEach items="${suppliers}" var="supplier" varStatus="i" >
							 <option value='${supplier.id}' ${psiLadingBill.supplier.id eq supplier.id ?'selected':''}>${supplier.nikename}</option>
							 <c:if test="${(empty psiLadingBill.supplier.id && i.index ==0 )|| psiLadingBill.supplier.id eq supplier.id}">
							 	<c:set value="${supplier.nikename}" var="nikename" />
							</c:if>
						</c:forEach>
					</select>
					<input type="hidden" name="nikeName"  value="${nikename}"/>
				</span>
				</div>
				
			</div>
			<div class="control-group"  style="float:left;width:33%" >
				<label class="control-label"><b>承运商</b>:</label>
				<div class="controls" >
				<span>
					<select style="width:150px;" id="tranSupplier" name="tranSupplier.id" class="required">
						<c:forEach items="${tranSuppliers}" var="tranSupplier" varStatus="i">
							 <option value='${tranSupplier.id}'>${tranSupplier.nikename}</option>;
						</c:forEach>
					</select>
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:33%" >
				<label class="control-label"><b>可验货日期</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" name="deliveryDate" required="required" class="Wdate" value="<fmt:formatDate value="${psiLadingBill.deliveryDate}" pattern="yyyy-MM-dd" />" />
				</span>
				</div>
			</div>
		</div>
		<div style="float: left"><blockquote><p style="font-size: 14px">收货单项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加收货单产品</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					   <th style="width: 20%">产品类型</th>
					   <th style="width: 5%">可提总数</th>
					   <th style="width: 5%">装箱数</th>
					   <th style="width: 20%">sku</th>
					   <th style="width: 8%">订单号</th>
					   <th style="width: 5%">可提总数量</th>
					   <th style="width: 5%">实提总数</th>
					   <th style="width: 5%">线下数</th>
					   <th style="width: 8%">备注</th>
					   <th style="width: 5%">备品数量</th>
					   <th style="width: 8%">操作</th>
				</tr>
			</thead>
		</table>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
