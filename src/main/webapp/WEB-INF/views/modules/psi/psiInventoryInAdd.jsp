<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Outbound order</title>
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
	
		if(!(top)){
			top = self;
		}
		$(document).ready(function() {
			
			eval('var psiSku=${skus}');
			eval('var noSkus=${noSkus}');
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$("#selectStock").on("change",function(){
				var params = {};
				params.warehouseId=$(this).val();
				params.warehouseName=encodeURI($(this).children('option:selected').text());
				window.location.href="${ctx}/psi/psiInventoryIn/add?"+$.param(params);
			});
			
			
			$("#tranSelect").on("change",function(){
				var tranNo=$(this).val();
				if(tranNo){
					var table =$("#contentTable");
					 var tbody =table.find("tbody");
					 table.find("thead").remove();
					 var head="<thead><tr><th style='width: 30%'>Product[SKU]</th> <th style='width: 10%'>Quality Tips</th><th style='width: 10%'>Quantity</th><th style='width: 10%'>Received Quantity</th><th style='width: 5%'>pack Quantity</th><th style='width: 5%'>Box quantity</th><th style='width: 20%'>Remark</th><th>Operate</th></tr></thead>";
					 table.append(head);
					 tbody.find("tr").each(function(){
						 $(this).remove();
					 });
					 
					ajaxGetTranData(tranNo);
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
			
			
			$("#selectOperationType").on("change",function(e){
				$("#dataFile").css("display","none");
				$("#dataType").css("display","none");
				$("#recallOrder").css("display","none");
				$("#itemData").css("display","block");
				$("#tranRemarkDiv").css("display","none");
				if(e.removed){
					var oldValue=e.removed.text;
					if(oldValue=="Transport Storing"){
						 //清理运单表头 和数据
						 var table =$("#contentTable");
						 var tbody =table.find("tbody");
						 table.find("thead").remove();
						 var head="<thead><tr><th style='width: 30%'>Product[SKU]</th> <th style='width: 10%'>Quality Tips</th><th style='width: 10%'>Quantity</th><th style='width: 30%'>Remark</th><th>Operate</th></tr></thead>";
						 table.append(head);
						 tbody.find("tr").each(function(){
							 $(this).remove();
						 });
						 
						 $("#tran").css("display","none");
						 $("#isFinished").css("display","none");
						 $("#add-row").click();
					}
					
					if($(this).val()=="Manual Operation"){
						$("#operationType").val("");
						$("#operationType").css("display","block");
					}else{
						$("#operationType").css("display","none");
						$("#operationType").val($(this).val());
						if($(this).val()=="Transport Storing"){
							$("#contentTable tbody tr").each(function(){
								 $(this).remove();
							 });
							$("#tran").css("display","block");
							$("#isFinished").css("display","block");
							$("#tranRemarkDiv").css("display","block");
							$("#tranSelect").select2().change();
						}
						
						if($(this).val()=="Lot Storing"){
							$("#itemData").css("display","none");
							$("#dataFile").css("display","block");
							$("#dataType").css("display","block");
						}
						<%--召回订单入库--%>
						if($(this).val()=="Recall Storing"){
							$("#itemData").css("display","none");
							$("#recallOrder").css("display","block");
						}
					}
				}else{
					//第一次进来给操作类型赋值
					$("#operationType").val($(this).val());
				}
			});
			
			$("#recallId").on("change",function(e){
				if($(this).val()){
					window.location.href="${ctx}/amazoninfo/removalOrder/stored?id=" + $(this).val();
				}
			});
			
			
			
			$(".sku").live("change",function(){
				var key = $(this).val();
				var tr=$(this).parent().parent();
				tr.find("input[name='productId']").val(psiSku[key][0]);
				tr.find("input[name='productName']").val(psiSku[key][1]);
				tr.find("input[name='countryCode']").val(psiSku[key][2]);
				tr.find("input[name='colorCode']").val(psiSku[key][3]);
				
			});
			
			//$("#selectStock").change();
			$("#selectOperationType").change();
			
			
			$(".remove-row").live("click",function(){
				 if($('#contentTable tbody tr').size()>1){
					var tr = $(this).parent().parent();
					tr.remove();
				}
			});
			
			$("#add-row").on("click",function(e){
				e.preventDefault();
				var tbody=$("#contentTable tbody");
				var typeTd="";
				if($("#selectOperationType").val()=="Transport Storing"){
					typeTd="<td><select name='qualityType' class='qualityType'  style='width:90%' disabled ><option value='new'>new</option></select></td>";
				}else{
					typeTd="<td><select name='qualityType' class='qualityType'  style='width:90%'><c:forEach items='${qualityTypes}' var='qualityType'> <option value='${qualityType}'>${qualityType}</option>	</c:forEach></select></td>";
				}
				var tr=$("<tr></tr>");
				tr.append("<td><input type='hidden' name='productId'/><input type='hidden' name='productName'/><input type='hidden' name='colorCode'/><input type='hidden' name='countryCode'/><select name='sku' class='sku' style='width:90%'><c:forEach items='${skus}' var='sku'><option value='${sku.key}'>${sku.value[1]}[${sku.key}][${skuFnskuMap[sku.key]}]</option></c:forEach></select></td>");
	            tr.append(typeTd);
	            if($("#selectOperationType").val()=="Transport Storing"){
	            	 tr.append("<t></td>");
	            }
	            tr.append("<td><input type='text' maxlength='11' style='width: 80%'  name='quantity' class='number' /></td>");
	            if($("#selectOperationType").val()=="Transport Storing"){
	            	 tr.append("<td></td>");
	            	 tr.append("<td></td>");
	            }
	            tr.append("<td><input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
	            tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>Delete</a></td>");
				tbody.append(tr);
				tr.find("select.sku").select2();
				tr.find("select.qualityType").select2();
				var key=tr.find("select.sku").val();
				tr.find("input[name='productId']").val(psiSku[key][0]);
				tr.find("input[name='productName']").val(psiSku[key][1]);
				tr.find("input[name='countryCode']").val(psiSku[key][2]);
				tr.find("input[name='colorCode']").val(psiSku[key][3]);
			});
			
			$("#add-row").click();
			$("#inputForm").validate({
				rules:{
					"quantity":{
						"required":true
					},
					"operationType":{
						"required":true
					},
					"dataDate":{
						"required":true
					},
					"tranLocalId":{
						"required":true
					}
				},
				messages:{
					"quantity":{"required":'Quantity is not emtpy'}
					,"operationType":{"required":'Type is not empty'}
					,"dataDate":{"required":'Date is not empty'}
					,"tranLocalId":{"required":'TransportOrder no. is not empty'}
				
			},
			submitHandler: function(form){
				if(!$("#bigRemark").val()){
					 top.$.jBox.tip("Remark cannot empty!!!", 'info',{timeout:3000});
						return false;
				 }
				
				if($("#selectOperationType").val()!="Lot Storing"){
					 var keyStr="";
					 var flag = 1;
					 var zeroFlag=1;
					 var remarkFlag=1;
					 var twoStr="";
					 var isNew=0;
					$("#contentTable tbody tr").each(function(){
						var sku =$(this).find("select.sku").val();
						if($("#selectOperationType").val()!="Transport Storing"){//运输入库不算这个
							if(noSkus.length>0&&noSkus.indexOf(sku)>=0){
								isNew=1;
								return false;
							}
						}
						var curkeyStr=$(this).find("select.sku").val()+$(this).find("select[name='qualityType']").children("option:selected").text();
						if(keyStr.indexOf(curkeyStr+",")>=0){
							twoStr=curkeyStr;
							flag = 2;
							return false;
						}else{
							keyStr=keyStr+curkeyStr+",";
						};
						if($("#selectOperationType").val()!="Transport Storing"){
							var quantity = $(this).find("input[name='quantity']").val();
							if(quantity==0){
								zeroFlag=2;
								twoStr=curkeyStr;
								return false;
							};
						}else{
							var quantity = $(this).find("input[name='quantity']").val();
							if($(this).find(".remove-row").text()){
								if(quantity==0){
									zeroFlag=2;
									twoStr=curkeyStr;
									return false;
								};
								
							}else{
								var oldQuantity=$(this).find(".viewQuantity").val();
								var remark =$(this).find("input[name='remark']").val();
								if(quantity!=oldQuantity && remark==''){
									remarkFlag=2;
									twoStr=curkeyStr;
									return false;
								}
							}
						}
						
					});
					
					if(isNew==1){
						top.$.jBox.tip("请联系销售绑定sku!!", 'info',{timeout:3000});
						return false;
					}
					
					if(remarkFlag==2){
						top.$.jBox.tip("shippedQuantity not equal receivedQuantity,please write remark "+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					if(flag==2){
						top.$.jBox.tip("Same product and type only has one record !!!  "+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					if(zeroFlag==2){
						top.$.jBox.tip("Quantity must by >0!!! "+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					top.$.jBox.confirm("<span style='color:red;font-weight:bold'>After confirmation will change the number of inventory,Operation will not be reversible,Are you sure?</span>","System Tips",function(v,h,f){
					if(v=='ok'){
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
						
						var isFinished = true;
						$(".isFinished").each(function(){
							isFinished=this.checked;
						});
						
						$("input[name='isFinished']").val(isFinished);
						    
						form.submit();
						$("#btnSubmit").attr("disabled","disabled");
					}
					},{buttons: { 'Confirm': 'ok', 'Cancel': ''}},{buttonsFocus:1,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.confirm("<span style='color:red;font-weight:bold'>After confirmation will change the number of inventory,Operation will not be reversible,Are you sure?</span>","System Tips",function(v,h,f){
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
			
			
			
			function ajaxGetTranData(tranNo){
				$.ajax({
				    type: 'post',
				    async:true,
				    url: '${ctx}/psi/psiInventoryIn/ajaxTranData' ,
				    data: {
				    	"tranNo":tranNo
				    },
				    dataType: 'json',
				    success:function(data){ 
				    	if(data.msg=="true"){
				    		$("#showTranDelivaryDate").text('ArrivedDate:'+data.arrDate+' BoxNumber:'+data.boxNumber);
				    		if(data.remark){
				    			 $("#tranRemarkDiv").css("display","block");
				    			$("#tranRemark").text(data.remark);
				    		}else{
				    			 $("#tranRemarkDiv").css("display","none");
				    		}
				    		createTranTbody(data.items);
				    	}
			        }
				});
			};
			
			function createTranTbody(data){
				var table =$("#contentTable");
				 var tbody =table.find("tbody");
				for(var i =0;i<data.length;i++){
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
					
					var tranInfo=data[i];
					var quantity;
					var receivedQuantity = tranInfo.receiveQuantity;
					if(receivedQuantity=='null'){
						receivedQuantity=0;
					}
					var shippedQuantity=tranInfo.shippedQuantity;
					if(shippedQuantity=='null'||shippedQuantity==0){
						//如果第一次收货时有产品发货时没有，第二次收货提示可收数为0
						quantity=0;
					}else{
						quantity=parseInt(parseInt(tranInfo.shippedQuantity)-parseInt(receivedQuantity));
					}
					var type="new";
					if(tranInfo.offline&&tranInfo.offline=='1'){
						type="offline";
					}
					tr.append("<td style='"+topRedLine+endRedLine+endRedLine+leftRedLine+"'><input type='hidden' name='productId'/><input type='hidden' name='productName'/><input type='hidden' name='colorCode'/><input type='hidden' name='countryCode'/><select name='sku' class='sku' style='width:90%' disabled><option value='"+tranInfo.sku+"'>"+tranInfo.productName+"["+tranInfo.sku+"]"+"</option></select></td>");
				 	tr.append("<td style='"+topRedLine+endRedLine+"'><select name='qualityType' class='qualityType'  style='width:90%' disabled ><option value='"+type+"'>"+type+"</option></select></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text' maxlength='11' style='width: 80%' name='tranQuantity' class='viewQuantity' readonly/></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text' maxlength='11' style='width: 80%'  name='quantity' class='number' /></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text' maxlength='200'style='width: 80%' readonly class='viewPackQuantity' /></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><input type='text' maxlength='200'style='width: 80%' readonly class='viewBoxQuantity' /></td>");
					tr.append("<td style='"+topRedLine+endRedLine+"'><span style='color:red'>"+(tranInfo.remark==""?"":(tranInfo.remark+"<br/></span>"))+"<input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
					tr.append("<td style='"+topRedLine+endRedLine+rightRedLine+"'></td>");
					tr.find("input[name='quantity']").val(quantity); 
					tr.find(".viewQuantity").val(quantity);
					tr.find(".viewPackQuantity").val(tranInfo.packQuantity);
					tr.find(".viewBoxQuantity").val(tranInfo.boxNum);
					tbody.append(tr);
					tr.find("select.sku").select2();
					tr.find("select.qualityType").select2();
					tr.find("input[name='productId']").val(tranInfo.productId);
					tr.find("input[name='productName']").val(tranInfo.productName);
					tr.find("input[name='countryCode']").val(tranInfo.countryCode);
					tr.find("input[name='colorCode']").val(tranInfo.colorCode);
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
	});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- <li><a href="${ctx}/psi/psiInventoryIn/">入库管理列表</a></li> -->
		<li class="active"><a href="#" style="font-size: 16px">Create Stock in Entry</a></li>
	</ul>
	<div style="float:left;width:98%;height:15px;margin-left:20px" class="alert alert-info">
			<strong >If you choice type "Lot Storing",file type :(csv or excel) when file is excel file，system can read the first tab data!</strong>
	</div> 
	<form:form id="inputForm" modelAttribute="psiInventoryIn" action="${ctx}/psi/psiInventoryIn/addSave" method="post" class="form-horizontal" enctype="multipart/form-data" >
		<input type="hidden" name="warehouseId" value="${psiInventoryIn.warehouseId}" >
		
		<input type="hidden" name="billNo" value="${psiInventoryIn.billNo}" >
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">Base Info.</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><b>Warehouse:</b></label>
				<div class="controls" style="margin-left:120px">
				<select name="warehouseId" id="selectStock" >
					<c:forEach items="${stocks}" var="stock">
						<option value="${stock.id}" ${psiInventoryIn.warehouseId eq stock.id?'selected':''} >
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
				 <input type="hidden" name="warehouseName" value="${psiInventoryIn.warehouseName}" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
					<label class="control-label" style="width:100px">Certificate:</label>
					<div class="controls" style="margin-left:120px">
					<input name="memoFile" type="file" id="myfileupload" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><spring:message code='sys_menu_actualTimeIn'/>:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="dataDate"  class="Wdate required" value="${psiInventoryIn.dataDate}" style="width:90%"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">From:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="source"  maxlength="100" style="width:90%"/>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100% ;">
				<div class="control-group" style="float:left;width:30%;height:50px">
					<label class="control-label" style="width:100px"><b>Type:</b></label>
					<div class="controls" style="margin-left:120px">
					<select   id="selectOperationType" >
						<c:forEach items="${operationTypes}" var="operationType">
							<option value="${operationType}">${operationType}</option>
						</c:forEach>		
					</select>
					<input type="text" name="operationType" id="operationType" class="required" style="display:none;margin-top:3px"/>
					</div>
				</div>
				
				<div class="control-group" id="dataFile" style="float:left;width:35%;height:50px;">
					<label class="control-label" style="width:100px"><b>Data File:</b></label>
					<div class="controls" style="margin-left:120px">
					<input name="excelFile" type="file" id="myDatafileupload" class="required"/><span class="help-inline">(.csv/.xls/.xlsx)</span>
					</div>
				</div>
				
				<div class="control-group" id="dataType" style="float:left;width:35%;height:50px">
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
				
				<div class="control-group" id="recallOrder" style="float:left;width:35%;height:50px">
					<label class="control-label" style="width:100px"><b>Recall Order ID:</b></label>
					<div class="controls" style="margin-left:120px">
						<select  name="recallId" id="recallId" class="required" style="width:90%" >
							<option value="">---<spring:message code='custom_email_template_select'/>---</option>
							<c:forEach items="${recallMap}" var="recall">
								<option value="${recall.key}">${recall.value}</option>
							</c:forEach>
						</select>
					</div>
				</div>
				
				<div class="control-group" id="tran" style="float:left;width:40%;height:50px;display:none">
					<label class="control-label" style="width:100px"><b>Transport No.:</b></label>
					<div class="controls" style="margin-left:120px" >
					<select  name="tranLocalNo" id="tranSelect" class="required" style="width:90%" >
						<c:forEach items="${tranMap}" var="tran">
							<option value="${tran.key}">${tran.key}[${tran.value}]</option>
						</c:forEach>		
					</select>
					<span  id="showTranDelivaryDate"  style="display:block;margin:0px auto;width:90%;margin-top:5px;color:green;font-weight:bold;float:left" ></span>
					</div>
				</div>
				
		</div>
		
		<div style="float:left;width:100% ;">
			<div class="control-group" style="float:left;width:98%">
					<label class="control-label" style="width:100px"><b>Remark:</b></label>
					<div class="controls" style="margin-left:120px" >
						<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" style="width:98%; height: 60px;" id="bigRemark"/>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;color:red ;" id="tranRemarkDiv">
			<div class="control-group" style="float:left;width:98%">
					<label class="control-label" style="width:100px;"><b>TranRemark:</b></label>
					<div class="controls" style="margin-left:120px" >
						<span id="tranRemark"></span>
					</div>
			</div>
		</div>
		
		<div style="display: block" id="itemData">
			 <blockquote style="float:left;width:98%">
			 <div style="float: left"><p style="font-size: 15px;font-weight: bold">Product Info.</p></div><div style="float: left" id=errorsShow></div>
			</blockquote>
			
			<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>Add</a></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed" >
			<thead>
				<tr>
					   <th>Product[SKU]</th>
					   <th>Quality Tips</th>
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
