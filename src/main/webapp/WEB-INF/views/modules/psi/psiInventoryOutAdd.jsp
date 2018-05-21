<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<html>
<head>
	<title>出库管理管理</title>
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
			
			eval('var noSkus =${noSkus}');
			
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			var storeId=$("#warehouseId").val();
			var fbaOpers = "";
		
			//<c:forEach items="${fns:getDictList('platform')}" var="dic">
				fbaOpers=fbaOpers+"<option value='${dic.value}'>${dic.label}</option>";
			//</c:forEach>
	
			
			var inventoryArgs =[];
			<c:forEach items="${inventorys}" var="inventory">
				var inventory={};
				inventory.newQuantity="${inventory.newQuantity}";
				inventory.oldQuantity="${inventory.oldQuantity}";
				inventory.brokenQuantity="${inventory.brokenQuantity}";
				inventory.renewQuantity="${inventory.renewQuantity}";
				inventory.sparesQuantity="${inventory.sparesQuantity}";
				inventory.offlineQuantity="${inventory.offlineQuantity}";
				inventory.productName ="${inventory.productName}";
				inventoryArgs["${inventory.sku}"]=inventory;
			</c:forEach>
			
			eval('var fbaMap=${fbaMap}');
			eval('var tranMap=${tranMap}');
			
			//eval('var productNameSkuMap=${productNameSkuMap}');
			
			
			//eval('var inventorySkuMap=${inventorySkuMap}');
			eval('var packQuantityMap=${packQuantityMap}');
			eval('var fnSkuMap=${fnSkuMap}'); 
			
			
			$("#selectStock").on("change",function(){
				var params = {};
				params.warehouseId=$(this).val();
				params.warehouseName=encodeURI($(this).children('option:selected').text());
				window.location.href="${ctx}/psi/psiInventoryOut/add?"+$.param(params);
			});
			
			
			$("#exportFbaDetail").on("click",function(){
				var id = $(this).attr("key");
				window.location.href="${ctx}/psi/fbaInbound/exportDetail?id=" + id;
			});
			
			
			$("#fba").on("change",function(e){
				$("#showFbaShipmentName").text("");
				var shipmentIds=$(this).val();
				if(shipmentIds&&shipmentIds!=''){
					ajaxGetFbaData(shipmentIds);
				}
				var key=$("#whereabouts").val();
				if(fbaMap[key]&&fbaMap[key]!=''){
					for(var i=0;i<fbaMap[key].length;i++){
						if($(this).val()==fbaMap[key][i][0]){
							var shipmentId=fbaMap[key][i][0];
							var id = fbaMap[key][i][2];
							$("#showFbaShipmentName").text(shipmentId);
							$("#exportFbaDetail").attr("key", id);
							break;
						}
					}
				}
				
			});
			
			$("#myDatafileupload").on("change",function(){
				var filePath=$("#myDatafileupload").val();
				var fileSuffix=filePath.substr(filePath.lastIndexOf(".")).toLowerCase();
				if(fileSuffix.indexOf(".")!=0||".csv,.xls,.xlsx,".indexOf(fileSuffix+",")==-1){
					top.$.jBox.tip("请选择csv或excel文件 ", 'info',{timeout:3000});
					$("#myDatafileupload").val("").focus();
				}
			});
			
			$("#pdfFileupload").on("change",function(){
				var filePath=$("#pdfFileupload").val();
				var fileSuffix=filePath.substr(filePath.lastIndexOf(".")).toLowerCase();
				if(fileSuffix.indexOf(".")!=0||".pdf,".indexOf(fileSuffix+",")==-1){
					top.$.jBox.tip("please select a pdf file", 'info',{timeout:2000});
					$("#pdfFileupload").val("").focus();
				}
			});
			
			$("#tran").on("change",function(){
				var tranNo = $(this).children('option:selected').attr("key");
				$(".tranLocalNo").val(tranNo);
				if($("#selectOperationType").val()=="Transport Delivery"){
					var tranId=$(this).val();
					if(tranId&&tranId!=''){
						ajaxGetTranData(tranId);
					}
				}
			});
			
			
			
			$("#whereabouts").on("change",function(){
				if($("#selectOperationType").val()=="FBA Delivery"){
					//处理费用
					var where =$(this).val();
					var supplier =$("#supplierSelect").children("option:selected").val();
					if(supplier=="DPD"){
						$(".dpdDe").css("display","none");
						$(".noDpdDe").css("display","none");
						if(where=="de"){
							$(".dpdDe").css("display","block");
						}else{
							$(".noDpdDe").css("display","block");
						}
						doFee();
					}
					
					
					$("#contentTable tbody tr").each(function(){
						 $(this).remove();
					 });
					
					$("#showFbaShipmentName").text("");
					//清空
					$("#fba").select2().empty();
					$("#fba").select2().select2("data",[]);
					$("#tran").select2().empty();
					$("#tran").select2().select2("data",[]);
					
					//给fba赋值
					var key=$("#whereabouts").val();
					if(fbaMap[key]&&fbaMap[key]!=''){
						var tranFba=$("#fba");
						var options = "";
						for(var i=0;i<fbaMap[key].length;i++){
							//中国发德国fba或者中国发美国fba 过滤
							var fbaLabel=fbaMap[key][i][3];
							if(storeId=='19'&&fbaLabel=='DE'){
									options=options+"<option value="+fbaMap[key][i][0]+">"+fbaMap[key][i][0]+"   ;     "+fbaMap[key][i][1]+"</option>";
							}else if(storeId=='21'&&fbaLabel=='CN'){
									options=options+"<option value="+fbaMap[key][i][0]+">"+fbaMap[key][i][0]+"    ;    "+fbaMap[key][i][1]+"</option>";
							}else if(storeId=='120'&&fbaLabel=='US'){
									options=options+"<option value="+fbaMap[key][i][0]+">"+fbaMap[key][i][0]+"   ;   "+fbaMap[key][i][1]+"</option>";
							}else if(storeId=='130'&&fbaLabel=='CN'){
									options=options+"<option value="+fbaMap[key][i][0]+">"+fbaMap[key][i][0]+"   ;   "+fbaMap[key][i][1]+"</option>";
							}else{
								options=options+"<option value="+fbaMap[key][i][0]+">"+fbaMap[key][i][0]+"    ;   "+fbaMap[key][i][1]+"</option>";
							}
						}
						tranFba.append(options).select2();   
						$("#fba").change();
					}
				}
				
				if($("#selectOperationType").val()=="Transport Delivery"){
					$("#contentTable tbody tr").each(function(){
						 $(this).remove();
					 });
					//清空
					$("#tran").select2().empty();
					$("#tran").select2().select2("data",[]);
					//给fba赋值
					var key=$("#whereabouts").val();
					if(tranMap[key]){
						var tran=$("#tran");
						var options = "";
						for(var i=0;i<tranMap[key].length;i++){
							options=options+"<option value="+tranMap[key][i][0]+" key="+tranMap[key][i][1]+">"+tranMap[key][i][1]+"["+tranMap[key][i][2]+"]"+"</option>";
						}
						tran.append(options).select2();
						$("#tran").change();
					}else{
						key=key.toLocaleLowerCase();
						if(tranMap[key]){
							var tran=$("#tran");
							var options = "";
							for(var i=0;i<tranMap[key].length;i++){
								options=options+"<option value="+tranMap[key][i][0]+" key="+tranMap[key][i][1]+">"+tranMap[key][i][1]+"["+tranMap[key][i][2]+"]"+"</option>";
							}
							tran.append(options).select2();
							$("#tran").change();
						}
					}
				}
				
				$("input[name='whereabouts']").val($(this).val());
			});
			
			$(".quantity1,.quantity2,.quantity3").blur(function(){
				if(storeId=='19'){
					doFee();
				}
				
			});
			
			$("#supplierSelect").on("change",function(e){
				if(storeId=='19'){
					$(".dpdDe").css("display","none");
					$(".noDpdDe").css("display","block");
					$(".fee").removeAttr("readonly");
					$(".fee").val("");
					$(".quantity1,.quantity2,.quantity3").val("");
					
					var supplier =$(this).val();
					if(supplier=="DPD"){
						var where = $("#whereabouts").children("option:selected").val(); 
						if(where=="de"){
							$(".dpdDe").css("display","block");
							$(".noDpdDe").css("display","none");
						}
						doFee();
					}else if(supplier=="DHL-FREE"){
						$(".fee").attr("readonly","readonly");
						$(".fee").val(0);
					}else if(supplier=="DHL"){
						$(".fee").attr("readonly","readonly");
					}
				}
			})
			
			
			$("#selectOperationType").on("change",function(e){
				$("#dataFile").css("display","none");
				$("#dataType").css("display","none");
				$("#itemData").css("display","block");
				if(e.removed){
					var oldValue=e.removed.text;
					if(oldValue=="FBA Delivery"||oldValue=="Transport Delivery"){
						// $("#contentTable tbody tr").each(function(){
						//	 $(this).remove();
						// });
						 //清理成正常表头
						 var table =$("#contentTable");
						 var tbody =table.find("tbody");
						 table.find("thead").remove();
						 var head="<thead><th style='width: 25%'>Product[SKU]</th><th style='width: 20%'>Quantity Tips</th><th style='width: 10%'>Quality Type</th><th style='width: 10%'>Quantity</th> <th style='width: 15%'>Remark</th><th>Operate</th></thead>";
						 table.append(head);
						 tbody.find("tr").each(function(){
							 $(this).remove();
						 });
						 
						 
						$("input[name='whereabouts']").val("");
						 $("#add-row").click();
					}
					
					//如果是运单发货隐藏增加按钮
					if($(this).val()=="Transport Delivery"||($(this).val()=="FBA Delivery"&&storeId=="21")){
						$("#add-row").css("display","none");
					}else{
						$("#add-row").css("display","block");
					}
					
					//把select隐藏   text显示 
					$("#whereaboutSelect").css("display","none");
					$("#whereabountText").css("display","block");
					$(".fbaDiv").css("display","none");
					$(".fbaNoDiv").css("display","none");
					$(".tranDiv").css("display","none");
					
					if($(this).val()=="Manual Operation"){
						$("#operationType").val("");
						$("#operationType").css("display","block");
					}else{
						if($(this).val()=="FBA Delivery"){
							$("#supplierSelect").change();
							$("#showFbaShipmentName").text("");
							//把select显示   text隐藏 
							$("#whereaboutSelect").css("display","block");
							$("#whereabountText").css("display","none");
							$("#whereabouts").select2().empty();
							$("#whereabouts").select2().select2("data",[]);
							$("#whereabouts").append(fbaOpers).select2();
							$("input[name='whereabouts']").val($("#whereabouts").val());
							var key =$("#whereabouts").val();
							
							 var table =$("#contentTable");
							 var tbody =table.find("tbody");
							 table.find("thead").remove();
							 var head="<thead><tr><th style='width: 25%'>Product[SKU]</th> <th style='width: 15%'>Quality Tips</th><th style='width: 10%'>Fba Quantity</th><th style='width: 10%'>Delivery Quantity</th><th style='width: 5%'>pack Quantity</th><th style='width: 5%'>Box quantity</th><th style='width: 15%'>Remark</th><th>Operate</th></tr></thead>";
							 table.append(head);
							 tbody.find("tr").each(function(){
								 $(this).remove();
							 });
							 
							
							$(".fbaDiv").css("display","block");
							$(".fbaNoDiv").css("display","block");
							//清空
							$("#tran").select2().empty();
							$("#tran").select2().select2("data",[]);
							$("#fba").select2().empty();
							$("#fba").select2().select2("data",[]);
							//给fba赋值
							if(fbaMap[key]){
								var tranFba=$("#fba");
								var options = "";
								for(var i=0;i<fbaMap[key].length;i++){
									//中国发德国fba或者中国发美国fba 过滤
									var fbaLabel=fbaMap[key][i][3];
									if(storeId=='19'&&fbaLabel=='DE'){
											options=options+"<option value="+fbaMap[key][i][0]+">"+fbaMap[key][i][0]+"   ;    "+fbaMap[key][i][1]+"</option>";
									}else if(storeId=='21'&&fbaLabel=='CN'){
											options=options+"<option value="+fbaMap[key][i][0]+">"+fbaMap[key][i][0]+"   ;    "+fbaMap[key][i][1]+"</option>";
									}else if(storeId=='120'&&fbaLabel=='US'){
											options=options+"<option value="+fbaMap[key][i][0]+">"+fbaMap[key][i][0]+"   ;   "+fbaMap[key][i][1]+"</option>";
									}else if(storeId=='130'&&fbaLabel=='CN'&&fbaMap[key][i][0]!=''){
											options=options+"<option value="+fbaMap[key][i][0]+">"+fbaMap[key][i][0]+"   ;   "+fbaMap[key][i][1]+"</option>";
									}else{
										options=options+"<option value="+fbaMap[key][i][0]+">"+fbaMap[key][i][0]+"    ;   "+fbaMap[key][i][1]+"</option>";
									}
								}
								tranFba.append(options).select2();
								$("#fba").change();
							}
						}
						
						
						if($(this).val()=="Transport Delivery"){
							 var table =$("#contentTable");
							 var tbody =table.find("tbody");
							 table.find("thead").remove();
							 var head="<thead><tr><th style='width: 25%'>Product[SKU]</th> <th style='width: 15%'>Quality Tips</th><th style='width: 10%'>Quantity</th><th style='width: 5%'>pack Quantity</th><th style='width: 5%'>Box quantity</th><th style='width: 15%'>Remark</th><th>Operate</th></tr></thead>";
							 table.append(head);
							 
							 tbody.find("tr").each(function(){
								 $(this).remove();
							 });
							
							 
							//把select显示   text隐藏 
							$("#whereaboutSelect").css("display","block");
							$("#whereabountText").css("display","none");
							$("#whereabouts").select2().empty();
							$("#whereabouts").select2().select2("data",[]);
							var ops="<c:forEach items='${viewStocks}' var='stock'><c:if test='${stock.id ne psiInventoryOut.warehouseId}'><option value='${stock.countrycode}''>${stock.stockName }</option></c:if></c:forEach>";
							if($("#warehouseId").val()=='21'||$("#warehouseId").val()=='130'){
								ops=ops+"<option value='wholesaleAddress'>批发客户地址</option>";
								 $(".fbaDiv").css("display","block");
							}
							$("#whereabouts").append(ops).select2().change();
							$("input[name='whereabouts']").val($("#whereabouts").val());
							
							var key =$("#whereabouts").val();
							
							$(".tranDiv").css("display","block");
							//清空    
							$("#tran").select2().empty();
							$("#tran").select2().select2("data",[]);
							$("#fba").select2().empty();
							$("#fba").select2().select2("data",[]);
							
							if(tranMap[key]){
								var tran=$("#tran");
								var options = "";
								for(var i=0;i<tranMap[key].length;i++){
									options=options+"<option value="+tranMap[key][i][0]+" key="+tranMap[key][i][1]+" >"+tranMap[key][i][1]+"["+tranMap[key][i][2]+"]"+"</option>";
								}
								tran.append(options).select2();
								$("#tran").change();
							}
						}
						
						if($(this).val()=="Lot Delivery"){
							$("#itemData").css("display","none");
							$("#dataFile").css("display","block");
						}
						
						
						$("#operationType").css("display","none");
						$("#operationType").val($(this).val());
					}
				}else{
					//第一次进来给操作类型赋值
					$("#operationType").val($(this).val());
				}
			});
			
			
			$("#selectOperationType").change();
			
			$("select.sku").live("change",function(){
				var tr =$(this).parent().parent();
				var inventory=inventoryArgs[$(this).val()];
				if(inventory){
					var packStr="";
					//if($("#selectOperationType").val()=='FBA Delivery'||$("#selectOperationType").val()=='Transport Delivery'){
					//	packStr=";...packQuantity:"+packQuantityMap[$(this).val()];
					//}
					
					//if($("#selectOperationType").val()=='FBA Delivery'){
					//	tr.find(".packQuantity").val(packQuantityMap[$(this).val()]);
					//	var boxNum =fbaInbound.quantity/packQuantityMap[$(this).val()];
					//	tr.find(".boxQuantity").val(toDecimal(boxNum));
						
					//}
					var subTips="new:"+inventory.newQuantity+"; offline:"+inventory.offlineQuantity;
					var allTips="new:"+inventory.newQuantity+"; old:"+inventory.oldQuantity+"; broken:"+inventory.brokenQuantity+"; renew:"+inventory.renewQuantity+"; spares:"+inventory.sparesQuantity+"; offline:"+inventory.offlineQuantity+packStr;	
					tr.find(".tips").text("");
					tr.find(".packQuantity").val(packQuantityMap[$(this).val()]);
					if($("#selectOperationType").val()=='FBA Delivery'||$("#selectOperationType").val()=='Transport Delivery'){
						tr.find(".tips").append("newQuantity:"+inventory.newQuantity+",offlineQuantity:"+inventory.offlineQuantity);
						var quantity=tr.find("input[name='quantity']").val();
						tr.find(".packQuantity").val(packQuantityMap[$(this).val()]);
						var boxNum =quantity/packQuantityMap[$(this).val()];
						tr.find(".boxQuantity").val(toDecimal(boxNum));
					}else{
						tr.find(".tips").append("<a href='#' rel='popover' data-content='"+allTips+"'>"+subTips+"</a>");
					}
					tr.find("a[rel='popover']").popover({trigger:'hover'});
				};
			});
			
			$("#add-row").on("click",function(e){
				e.preventDefault();
				var tbody=$("#contentTable tbody");
				var tr=$("<tr></tr>");
				var options = "";
				
				tr.append("<td><input type='hidden' class='packQuantity'/><select style='width: 98%' class='sku' name='sku'><c:forEach items='${inventorys}' var='inventory'><option value='${inventory.sku}'>${inventory.productName}[${inventory.sku}][${skuFnskuMap[inventory.sku]}]</option></c:forEach></select></td>");
				tr.append("<td><span readonly  style='width: 95%'  class='tips' /></td>");
	           	if($("#selectOperationType").val()!='FBA Delivery'){
					options="<c:forEach items='${qualityTypes}' var='qualityType'> <option value='${qualityType}'>${qualityType}</option></c:forEach>";
					tr.append("<td><select name='qualityType' class='qualityType'  style='width:98%' >"+options+"</select></td>");
				}else{
					tr.append("<td></td>");
				}
	            tr.append("<td><input type='text' maxlength='11' style='width: 80%'  name='quantity' class='number' /></td>");
	            
	        	if($("#selectOperationType").val()=='FBA Delivery'){
	        		tr.append("<td><input type='text'  style='width: 80%'  class='packQuantity' readonly/></td>");
	        		tr.append("<td><input type='text'  style='width: 80%'  class='boxQuantity' readonly/></td>");
	        	}
	            tr.append("<td><input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
	            tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>Delete</a></td>");
				tbody.append(tr);
				tr.find("select.sku").select2();
				if($("#selectOperationType").val()!='FBA Delivery'){
					tr.find("select.qualityType").select2();
				}
				tr.find("select.sku").change();
				
			});
			
			$("#add-row").click();
			
			
			$(".remove-row").live("click",function(){
				 if($('#contentTable tbody tr').size()>1){
					var tr = $(this).parent().parent();
					tr.remove();
				}
			});
			
			
			$(".boxQuantity").live("blur",function(){
				getTotalVolumeAndWeight();
			});
			
			
			$("#inputForm").validate({
				rules:{
					"quantity":{
						"required":true
					},
					"sku":{
						"required":true
					},
					"tranFbaNo":{
						"required":true
					},
					"whereabouts":{
						"required":true
					},
					"dataDate":{
						"required":true
					},
					"supplier":{
						"required":true
					}
				},
				messages:{
					"sku":{"required":'Sku cannot not empty!'},
					"tranFbaNo":{"required":'FBA cannot not empty!'},
					"quantity":{"required":'Quantity cannot not empty!'},
					"whereabouts":{"required":'To cannot not empty!'},
					"dataDate":{"required":'Date cannot not empty!'},
					"supplier":{"required":'Supplier cannot not empty!'}
			},
				submitHandler: function(form){
					if(!$("#bigRemark").val()){
						 top.$.jBox.tip("Remark cannot empty!!!", 'info',{timeout:3000});
							return false;
					 }
					
					if($("#selectOperationType").val()!="Lot Delivery"){
						 var keyStr="";
						 var flag = 1;
						 var zeroFlag=1;
						 var plusFlag=1;
						 var hasStoreFlag=1;
						 var tipsStr="";
						 var outInfos="";
						 if($("#selectOperationType").val()=="Transport Delivery"){
								if($("select#tran").val()==null){
									top.$.jBox.tip("Transport order cannot empty!!!"+tipsStr, 'info',{timeout:3000});
									return false;
								}
							}
						 
						 
						 
						$("#contentTable tbody tr").each(function(){
							if($("#selectOperationType").val()=="FBA Delivery"){
								if($(this).find(".tips").text()=="No Data In Stock"){
									hasStoreFlag=2;
									return false;
								};
								
							}
							
							
							var sku=$(this).find("select.sku").val();
							
							var qualityType=$(this).find("select.qualityType").val();
							if(!qualityType){
								qualityType=$(this).find("input[name='qualityType']").val();
							}
							var quantity = $(this).find("input[name='quantity']").val();
							
							if($("#selectOperationType").val()=="FBA Delivery"||$("#selectOperationType").val()=="Transport Delivery"){
								if($(this).find(".remove-row").text()){
									if(quantity==0){
										zeroFlag=2;
										return false;
									}
								}
							}else{
								if(quantity==0){
									zeroFlag=2;
									return false;
								}
							}
							
							var curkeyStr=sku+" "+qualityType;
							if(keyStr.indexOf(curkeyStr+",")>=0||sku==''){
								tipsStr=curkeyStr;
								flag = 2;
								return false;
							}else{
								keyStr=keyStr+curkeyStr+",";
							};
							
							
							var inventory=inventoryArgs[sku];
							//整合sku、剩余数对换码进行确认校验 
							outInfos=outInfos+sku+","+parseInt(parseInt(inventory.newQuantity)-parseInt(quantity))+";"
							var exeFlag=1;
							if(qualityType=="new"&&inventory.newQuantity<parseInt(quantity)){
								exeFlag=2;
							}else if(qualityType=="old"&&inventory.oldQuantity<parseInt(quantity)){
								exeFlag=2;
							}else if(qualityType=="broken"&&inventory.brokenQuantity<parseInt(quantity)){
								exeFlag=2;
							}else if(qualityType=="renew"&&inventory.renewQuantity<parseInt(quantity)){
								exeFlag=2;
							}else if(qualityType=="spares"&&inventory.sparesQuantity<parseInt(quantity)){
								exeFlag=2;
							}else if(qualityType=="offline"&&inventory.offlineQuantity<parseInt(quantity)){
								exeFlag=2;
							}
							if(exeFlag==2){
								tipsStr=curkeyStr;
								plusFlag=2;
								return false;
							}
						});
						
						if(hasStoreFlag==2){
							top.$.jBox.tip("Has some product no data in stock!", 'info',{timeout:3000});
							return false;
						}
						
						if(flag==2){
							top.$.jBox.tip("the same sku & type has only one info,and sku is not empty!  "+tipsStr, 'info',{timeout:3000});
							return false;
						}
						
						if(zeroFlag==2){
							top.$.jBox.tip("quantity must be greater than 0! "+tipsStr, 'info',{timeout:3000});
							return false;
						}
						
						if(plusFlag==2){
							top.$.jBox.tip("The number of inputs is greater than the current inventory number ! "+tipsStr, 'info',{timeout:3000});
							return false;
						}
						
						
						
						var  confirmStr="<span style='color:red;font-weight:bold'>After confirmation will change the number of inventory,Operation will not be reversible,Are you sure?</span>";
						//德国仓出库需要确认sku转换  
						if($("#warehouseId").val()=='19'){
							var res=ajaxSkuChangeValidate(outInfos,$("#warehouseId").val());   
							if(res){
								top.$.jBox.tip(res, 'info',{timeout:3000});
								return false;
							}
						}
						
						var  widthVar=350;
						top.$.jBox.confirm(confirmStr,'System Tips',function(v,h,f){
							if(v=='ok'){
								var supplier =$("#supplierSelect").children("option:selected").val();
								if(supplier=="P0-DPD"){
									$("#supplierSelect").val("DPD");
								}
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
						},{buttons: { 'Confirm': 'ok', 'Cancel': ''}},{buttonsFocus:1,persistent: true,width: widthVar, showType: 'fade'});
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}else{
						top.$.jBox.confirm("<span style='color:red;font-weight:bold'>After confirm inventory quantity will be change,Operation will not be reversible,Are you sure to submit the out-stock bill?</span>","System Tips",function(v,h,f){
							if(v=='ok'){
								$("#contentTable").remove();
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
							 }
							},{buttons: { 'Confirm': 'ok', 'Cancel': ''}},{buttonsFocus:1,persistent: true});
					}
			},
			errorContainer: "#messageBox",
			errorPlacement: function(error, element) {
				$("#messageBox").text("输入有误，请先更正。");
				error.appendTo($("#errorsShow"));
			}
		});
			
			
		function ajaxGetFbaData(shippmentId){
			$.ajax({
			    type: 'post',
			    async:true,
			    url: '${ctx}/psi/psiInventoryOut/ajaxFbaData' ,
			    data: {
			    	"shippmentId":shippmentId
			    },
			    dataType: 'json',
			    success:function(data){ 
			    	if(data.msg=="true"){
			    		createFbaTbody(data.items);
			    	}
		        }
			});
		};
		
		/* 德国包裹，基础运费，15kg为3.48欧， 31.5kg以下为3.89欧
		法国，英国基础运费为9.35欧。意大利为10.39欧，西班牙为13.51欧 */
		function doFee(){
			if(storeId=='19'){
				var supplier =$("#supplierSelect").children("option:selected").val();
				if(supplier=="DPD"){
					var fee=0;
					
					var where=$("#whereabouts").children("option:selected").val();
					if(where=="de"){
						var quantity1=$(".quantity1").val();
						if(quantity1){
							fee=fee+(3.48+0.1)*1.07*parseInt(quantity1);
						}
						var quantity2=$(".quantity2").val();
						if(quantity2){
							fee=fee+(3.89+0.1)*1.07*parseInt(quantity2);
						}
					}else{
						var quantity3=$(".quantity3").val();
						if(quantity3){
							if(where=="uk"||where=="fr"){
								fee=(9.35+0.1)*1.07*parseInt(quantity3);
							}else if(where="it"){
								fee=(10.39+0.1)*1.07*parseInt(quantity3);
							}else if(where=="es"){
								fee=(13.51+0.1)*1.07*parseInt(quantity3);
							}
							
						}
					}
					$(".fee").val(fee.toFixed(2));
				}else if(supplier=="P0-DPD"){//boxNum*13.64
					var where=$("#whereabouts").children("option:selected").val();
					var quantity=$(".quantity3").val();
					var fee=13.64*parseInt(quantity);
					$(".fee").val(fee.toFixed(2));
				}
			}
		}
		
		function ajaxGetTranData(tranId){
			var isLiCheng="0";
			if(storeId=='130'){
				isLiCheng="1";
			}
			$.ajax({
			    type: 'post',
			    async:true,
			    url: '${ctx}/psi/psiInventoryOut/ajaxTranData' ,
			    data: {
			    	"tranId":tranId,
			    	"isLiCheng":isLiCheng
			    },
			    dataType: 'json',
			    success:function(data){ 
			    	if(data.msg=="true"){
			    		createTranTbody(data.items);
			    	}
		        }
			});
		};
			
			
		function createFbaTbody(data){
			 var table =$("#contentTable");
			 var tbody =table.find("tbody");
			 table.find("thead").remove();
			 var head="<thead><tr><th style='width: 25%'>Product[SKU]</th> <th style='width: 15%'>Quality Tips</th><th style='width: 10%'>Fba Quantity</th><th style='width: 10%'>Delivery Quantity</th><th style='width: 5%'>pack Quantity</th><th style='width: 5%'>Box quantity</th><th style='width: 15%'>Remark</th><th>Operate</th></tr></thead>";
			 table.append(head);
			 
			 tbody.find("tr").each(function(){
				 $(this).remove();
			 });
			 
			 
			for(var i =0;i<data.length;i++){
				var fbaInbound=data[i];
				var inventory=inventoryArgs[fbaInbound.sku];
				
				var tr=$("<tr></tr>");
				var topRedLine="";
				var endRedLine="";
				var leftRedLine="border-left: 1px solid red;";
				var rightRedLine="border-right: 1px solid red;";
				if(i==0){
					topRedLine="border-top: 1px solid red;";
				}		
				if(i==(data.length-1)){
					endRedLine="border-bottom: 1px solid red;";
				}
				if(inventoryArgs[fbaInbound.sku]){
					tr.append("<td style='"+topRedLine+endRedLine+endRedLine+leftRedLine+"'><input type='hidden' name='qualityType' value='new'/><select style='width: 98%' class='sku' name='sku' disabled ><option value='"+fbaInbound.sku+"'>"+inventoryArgs[fbaInbound.sku].productName+'['+fbaInbound.sku+']'+"</option></select></td>");
				}else{
					tr.append("<td style='"+topRedLine+endRedLine+endRedLine+leftRedLine+"'><input type='hidden' name='qualityType' value='new'/><select style='width: 98%' class='sku' name='sku' disabled ><option value='"+fbaInbound.sku+"'>"+fbaInbound.sku+"</option></select></td>");
				}
				
				tr.append("<td style='"+topRedLine+endRedLine+"'><span  style='width: 95%'  class='tips' /></td>");
				tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%'  class='oldQuantity' readonly /></td>");
				tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%'  name='quantity' class='number' /></td>");
				tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%'  class='packQuantity' readonly/></td>");
				tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%'  class='boxQuantity' /></td>");
				tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
				tr.append("<td style='"+topRedLine+endRedLine+rightRedLine+"'><input type='hidden' class='volume' /><input type='hidden' class='weight' /></td>");
				tr.find(".packQuantity").val(packQuantityMap[fbaInbound.sku]);
				tr.find("input[name='quantity']").val(fbaInbound.quantity);
				tr.find("select.sku").select2();
				tr.find("select.qualityType").select2();
				if(inventory){
					//如果fba贴里的sku   
					var subTips="newQuantity:"+inventory.newQuantity;
					tr.find(".tips").append(subTips);
					tr.find(".oldQuantity").val(fbaInbound.quantity);
					tr.find("input[name='quantity']").val(fbaInbound.quantity);
					var pack=1;
					if(fbaInbound.pack==0){
						pack=packQuantityMap[fbaInbound.sku];
					}else{
						pack=fbaInbound.pack;
					}
					tr.find(".packQuantity").val(pack);
					var boxNum =fbaInbound.quantity/pack;
					tr.find(".boxQuantity").val(toDecimal(boxNum));
				}else{
					//如果fba贴里的sku    库里没有
					tr.find(".tips").append("No Data In Stock");	
				}
				
				tr.find(".volume").val(fbaInbound.volume);
				tr.find(".weight").val(fbaInbound.weight);
				tbody.append(tr);
				
				//重新算下体积、重量
				getTotalVolumeAndWeight();
			}
			
		};
		
		function createTranTbody(data){
			 var table =$("#contentTable");
			 var tbody =table.find("tbody");
			 table.find("thead").remove();
			 var head="<thead><tr><th style='width: 25%'>Product[SKU]</th> <th style='width: 15%'>Quality Tips</th><th style='width: 10%'>Quantity</th><th style='width: 5%'>pack Quantity</th><th style='width: 5%'>Box quantity</th><th style='width: 15%'>Remark</th><th>Operate</th></tr></thead>";
			 table.append(head);
			 
			 tbody.find("tr").each(function(){
				 $(this).remove();
			 });
			 
			for(var j =0;j<data.length;j++){
				var tr=$("<tr></tr>");
				var topRedLine="";
				var endRedLine="";
				var leftRedLine="border-left: 1px solid red;";
				var rightRedLine="border-right: 1px solid red;";
				if(j==0){
					topRedLine="border-top: 1px solid red;";
				}		
				if(j==(data.length-1)){
					endRedLine="border-bottom: 1px solid red;";
				}
				var tranInfo=data[j];
				var tips = tranInfo.productName+"["+sku+"]";
				//根据产品  国家  颜色  获得sku 选择框的值
				//var key=tranInfo.productId+","+tranInfo.countryCode+","+tranInfo.colorCode;
				var options = "";
				//var disable="";
				var sku =tranInfo.sku;
				var boxNum =tranInfo.quantity/tranInfo.packQuantity;
				if(inventoryArgs[sku]){
					var type="new";
					if(tranInfo.offline&&tranInfo.offline=='1'){
						type="offline";
					}
					
					
					options="<option value='"+sku+"' selected >"+tranInfo.productName+"["+sku+"]"+"</option>";
					tr.append("<td style='"+topRedLine+endRedLine+endRedLine+leftRedLine+"'><input type='hidden' name='qualityType' value='"+type+"'/> <select style='width: 98%' class='sku' name='sku' disabled >"+options+"</select></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><span style='width: 95%'  class='tips' /></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%'  name='quantity' readonly/></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%'  class='packQuantity' readonly/></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%'  class='boxQuantity' readonly/></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%' name='remark' /></td>");
					tr.append("<td style='"+topRedLine+endRedLine+rightRedLine+"'></td>");
					tr.find("input[name='quantity']").val(tranInfo.quantity);
					tr.find(".packQuantity").val(tranInfo.packQuantity);
					tr.find(".boxQuantity").val(toDecimal(boxNum));
					var subTips="";
					if(type=="offline"){
						subTips="new:"+inventoryArgs[sku].newQuantity+",<span style='color:red'>offline</span>:"+inventoryArgs[sku].offlineQuantity;
					}else{
						subTips="new:"+inventoryArgs[sku].newQuantity+",offline:"+inventoryArgs[sku].offlineQuantity;
					}
					
					tr.find(".tips").append(subTips);
				}else{
					//库里没这条数据   也要显示下该运单 
					tr.append("<td style='"+topRedLine+endRedLine+endRedLine+leftRedLine+"'><select style='width: 98%' class='sku' name='sku'></select></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><span  style='width: 95%'  class='tips' /></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%'  name='quantity' readonly class='number' /></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%'  class='packQuantity' readonly/></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%'  class='boxQuantity' readonly/></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text'  style='width: 80%' name='remark' /></td>");
					tr.append("<td style='"+topRedLine+endRedLine+rightRedLine+"'></td>");
					tr.find("input[name='quantity']").val(tranInfo.quantity);
					tr.find(".packQuantity").val(tranInfo.packQuantity);
					tr.find(".boxQuantity").val(toDecimal(boxNum));
					tr.find(".tips").append(tranInfo.productName+"["+sku+"]"+" No Data In Stock");
				}
				tbody.append(tr);
			}
			
		};
		
	
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*100)/100;  
	            return f;  
	     };
     
	     function ajaxSkuChangeValidate(outInfos,warehouseId){
				var res="";
				$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/psi/psiSkuChangeBill/ajaxQuantityValidate',
				    data: {
				    	"outInfos":outInfos,
				    	"warehouseId":warehouseId
				    },
				    dataType: 'json',
				    success:function(data){ 
				    	res=data.msg;
			        }
				});
				return res;
			};
			
		function getTotalVolumeAndWeight(){
			var volume =0;
			var weight =0;
			
			$("#contentTable tbody tr").each(function(){
				var boxQ=$(this).find(".boxQuantity").val();
				if($(this).find(".volume").val()&&boxQ!=''){
					volume=parseFloat(volume)+parseFloat($(this).find(".volume").val())*boxQ;
				}
				if($(this).find(".weight").val()&&boxQ!=''){
					weight=parseFloat(weight)+parseFloat($(this).find(".weight").val())*boxQ;
				}
			});
			
			$("input[name='tranVolume']").val(toDecimal(volume));
			$("input[name='tranWeight']").val(toDecimal(weight));
		}
		
	});
		
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- <li><a href="${ctx}/psi/psiInventoryOut/">出库管理列表</a></li> -->
		<li class="active"><a href="#" style="font-size: 16px">Create Stock Out Entry</a></li>
	</ul>
	<div style="float:left;width:98%;height:15px;margin-left:20px" class="alert alert-info">
			<strong >If you choice type "Lot Delivery",file type :(csv or excel) when file is excel file，system can read the first tab data!<br/>
			</strong>
	</div> 
	
	<div class="alert alert-danger" id="showError" style="float:left;width:98%;height:15px;margin-left:20px;display:none"></div>
	<form:form id="inputForm" name="inputForm" modelAttribute="psiInventoryOut" action="${ctx}/psi/psiInventoryOut/addSave" method="post" class="form-horizontal" enctype="multipart/form-data">
	<input type="hidden" name="warehouseId" value="${psiInventoryOut.warehouseId}" id="warehouseId" />
	<input type="hidden" name="tranLocalNo"    class="tranLocalNo" />
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold"">Base Info.</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><b>Warehouse:</b></label>
				<div class="controls" style="margin-left:120px">
				 <select name="warehouseId"  id="selectStock" style="width:80%"  >
					<c:forEach items="${stocks}" var="stock">
						<option value="${stock.id}" ${psiInventoryOut.warehouseId eq stock.id?'selected':''} >
						<c:choose>
							<c:when test="${stock.countrycode eq 'DE' }">Germany</c:when>
							<c:when test="${stock.id eq '21' }">China</c:when>
							<c:when test="${stock.id eq '130' }">China_LC</c:when>
							<c:when test="${stock.countrycode eq 'US' }">American</c:when>
							<c:otherwise>${stock.stockName}</c:otherwise>
						</c:choose>
						</option>
					</c:forEach>		
				</select>
				 <input type="hidden" name="warehouseName" value="${psiInventoryOut.warehouseName}" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
					<label class="control-label" style="width:100px">Certificate:</label>
					<div class="controls" style="margin-left:120px">
					<input name="memoFile" type="file" id="myfileupload" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><spring:message code='sys_menu_actualTimeOut'/>:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="dataDate" style="width:90%"  class="Wdate required" value="${psiInventoryOut.dataDate}" />
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;;height:30px">
				<label class="control-label" style="width:100px"><b>To:</b></label>
				<div class="controls" style="margin-left:120px">
					<span style="display:none" id="whereaboutSelect">
					<select  style="width: 90%" id="whereabouts" ></select></span>
					<span style="display:block" id="whereabountText"><input type="text" name="whereabouts"  style="margin-top:3px;width:70%" required="required"/></span>
					
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100% ;">
				<div class="control-group" style="float:left;width:30%;height:50px">
					<label class="control-label" style="width:100px"><b>Type:</b></label>
					<div class="controls" style="margin-left:120px">
					<select  id="selectOperationType" style="width:80%" >
						<c:forEach items="${operationTypes}" var="operationType">
							<option value="${operationType}" ${psiInventoryOut.operationType eq operationType ?'selected':''}>${operationType}</option>
						</c:forEach>		
					</select>
					<input type="text" name="operationType" id="operationType"  style="display:none;margin-top:3px" required="required"/>
					</div>
				</div>
				
				<div class="control-group fbaNoDiv" style="float:left;width:68%;height:50px;display: none">
					<label class="control-label" style="width:100px"><b>FBA No.:</b></label>
					<div class="controls" style="margin-left:120px;width:90%" >
						<select name="tranFbaNo" style="width: 90%" id="fba" class="required"></select>
						<span  id="showFbaShipmentName"  style="margin:0px auto;width:15%;margin-top:5px;color:green;font-weight:bold;float:left" ></span>
						<a id="exportFbaDetail" class="btn btn-success btn-small" href="#" style="margin-top:5px;width:100px;height:20px">Export FBA Form</a>
						<div class="control-group" style="float:right;width:20%;height:30px;margin-top:5px;margin-right:250px;">
							<label class="control-label" style="width:100px">PDF upload：<%--<a id="uploadPdf" class="btn btn-success btn-small" href="#">upload</a> --%></label>
							<div class="controls" style="margin-left:120px">
							<input name="pdfOutboundFile" type="file" id="pdfFileupload" ${(psiInventoryOut.warehouseId ne '21' && psiInventoryOut.warehouseId ne '130' && psiInventoryOut.warehouseId ne '147'&& psiInventoryOut.warehouseId ne '120' )?'required':''}/>
							</div>   
						</div>
					</div>
				</div>
				
				
				<div class="control-group tranDiv" style="float:left;width:33%;;height:50px;display: none">
					<label class="control-label" style="width:100px"><b>Transport No.:</b></label>
					<div class="controls" style="margin-left:120px">
						<select name="tranLocalId" style="width: 90%"  id="tran">
						</select>&nbsp;&nbsp;&nbsp;&nbsp;
					</div>
				</div>
				
				<div class="control-group" id="dataFile" style="float:left;width:35%;height:50px;">
					<label class="control-label" style="width:100px"><b>Data File:</b></label>
					<div class="controls" style="margin-left:120px">
					<input name="excelFile" type="file" id="myDatafileupload" class="required"/><span class="help-inline">(.csv/.xls/.xlsx)</span>
					</div>
				</div>
				
				<div class="control-group" id="dataType" style="float:left;width:33%;height:50px">
					<label class="control-label" style="width:100px"><b>Quality Type:</b></label>
					<div class="controls" style="margin-left:120px">
						<select  name="dataType" >
							<option value="new">new</option>
							<option value="old">old</option>
							<option value="broken">broken</option>    
							<option value="renew">renew</option>
						</select>
					</div>
				</div>
		</div>
		
		<div style="float:left;width:98%;display:none" class="fbaDiv">
			<div class="control-group" style="float:left;width:${(psiInventoryOut.warehouseId eq '21'||psiInventoryOut.warehouseId eq '130')?35:25}%">
				<label class="control-label" style="width:100px"><b>Supplier:</b></label>
				<div class="controls" style="margin-left:120px">
					 <select  style="width: 80%" id="supplierSelect" name="supplier" class="required" >
					 <c:choose>
					 	<c:when test="${psiInventoryOut.warehouseId eq '21'||psiInventoryOut.warehouseId eq '130'}">
					 		<option value="">请选择物流</option>
				   			<option value="DHL-FREE">DHL-FREE</option>
				   			<option value="DPD">DPD</option>
				   			<option value="UPS">UPS</option>
				   			<option value="FEDEX">FEDEX</option>
				   			<option value="OTHER">OTHER</option>
					 	</c:when>
					 	<c:otherwise>
					 		 <c:forEach items="${logisticsSupplier}" var="logisticsSupplier">
						   		<option value="${logisticsSupplier}" ${psiInventoryOut.supplier eq logisticsSupplier ?'selected':''}>${logisticsSupplier}</option>
						     </c:forEach>	
						        <option value="P0-DPD">P0-DPD</option>
					 	</c:otherwise>
					 </c:choose>
					 </select>
				</div>
			</div>
			<c:choose>
			 	<c:when test="${psiInventoryOut.warehouseId eq '21'||psiInventoryOut.warehouseId eq '130'}">
					<div class="control-group" style="float:left;width:35%">
						<label class="control-label" style="width:100px">提货人姓名</label>
						<div class="controls" style="margin-left:120px">
							<input name="tranMan" type="text" required />
						</div>
					</div>
					<div class="control-group" style="float:left;width:30%">
						<label class="control-label" style="width:100px">电话</label>
						<div class="controls" style="margin-left:120px">
							<input name="phone" type="text" required />
						</div>
					</div>
					<div class="control-group" style="float:left;width:35%">
						<label class="control-label" style="width:100px">身份证号:</label>
						<div class="controls" style="margin-left:120px">
							<input name="idCard" type="text" required />
						</div>
					</div>
					<div class="control-group" style="float:left;width:35%">
						<label class="control-label" style="width:100px">车牌号:</label>
						<div class="controls" style="margin-left:120px">
							<input name="carNo"  type="text" required />
						</div>
					</div>
					<div class="control-group" style="float:left;width:30%">
						<label class="control-label" style="width:100px">海运柜号:</label>
						<div class="controls" style="margin-left:120px">
							<input name="boxNo" type="text" required />
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="control-group dpdDe" style="float:left;width:25%;display:none">
							<label class="control-label" style="width:100px">Box Num.(15kg):</label>
							<div class="controls" style="margin-left:120px">
								<input name="quantity1" type="text"  class="number quantity1"  style="width:90%"/>
							</div>
					</div>
					<div class="control-group dpdDe" style="float:left;width:25%;display:none">
							<label class="control-label" style="width:100px">Box Num.(30kg):</label>
							<div class="controls" style="margin-left:120px">
								<input name="quantity2" type="text"  class="number quantity2" style="width:90%"/>
							</div>
					</div>
					
					<div class="control-group noDpdDe" style="float:left;width:25%;display:none">
						<label class="control-label" style="width:100px">Box Num.:</label>
						<div class="controls" style="margin-left:120px">
							<input name="quantity3" type="text"  class="number required quantity3" style="width:90%"/>
						</div>
					</div>
					
					<div class="control-group" style="float:left;width:25%">
						<label class="control-label" style="width:100px">Fee:</label>
						<div class="controls" style="margin-left:120px">
							<input name="fee" type="text"  class="price fee"  style="width:60%"/>
							<b>
							<c:choose>
								<c:when test="${psiInventoryOut.warehouseId eq '120'}">(USD)</c:when>
								<c:when test="${psiInventoryOut.warehouseId eq '19'}">(EUR)</c:when>
								<c:when test="${psiInventoryOut.warehouseId eq '147'}">(JPY)</c:when>
								<c:otherwise></c:otherwise>
							</c:choose>
							</b>
						</div>
					</div>
					
					
					<div class="control-group" style="float:left;width:25%">
						<label class="control-label" style="width:100px">Weight/KG:</label>
						<div class="controls" style="margin-left:120px">
							<input name="tranWeight" type="text"  class="price" style="width:90%"/>
						</div>
					</div>
					<div class="control-group" style="float:left;width:25%">
						<label class="control-label" style="width:100px">Volume/CBM :</label>
						<div class="controls" style="margin-left:120px">
							<input name="tranVolume" type="text"  class="price"  style="width:90%"/>
						</div>
					</div>
					<div class="control-group" style="float:left;width:25%">
							<label class="control-label" style="width:100px">Pickup Date:</label>
							<div class="controls" style="margin-left:120px">
								<input name="ladingDate" type="text" class="Wdate required" style="width:90%"/>
							</div>
					</div>
					<div class="control-group" style="float:left;width:25%">
							<label class="control-label" style="width:100px">Track No.:</label>
							<div class="controls" style="margin-left:120px">
								<input name="trackBarcode"   type="text" style="width:90%" class="required"/>
							</div>
					</div>
					
				</c:otherwise>
			</c:choose>
		</div>
		
		
		<div style="float:left;width:98% ;">
		<div class="control-group" style="float:left;width:98%">
				<label class="control-label" style="width:100px"><b>Remark:</b></label>
				<div class="controls" style="margin-left:120px">
					<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" style="width:95%; height: 60px;"  id="bigRemark"/>
				</div>
		</div>
		</div>
		<div style="display: block" id="itemData">
				 <blockquote style="float:left;width:98%">
				 <div style="float: left"><p style="font-size: 15px;font-weight: bold">Product Info.</p></div><div style="float: left" id=errorsShow></div>
				  </blockquote>
				
				<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row" style="width:100px"><span class="icon-plus"></span>Add</a></div>
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						   <th>Product[SKU]</th>
						   <th>Quantity Tips</th>
						   <th>Quality Type</th>
						   <th>Quantity</th>
						   <th>Remark</th>
						   <th>Operate</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="Create"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
