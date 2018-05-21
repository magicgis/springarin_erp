<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Inventory Sku Change</title>
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
			eval('var inventoryMaps =${inventoryMaps}');
			eval('var fnskuMap =${fnskuMap}');
			$("#add-row").on('click',function(e){
				e.preventDefault();
				var tbody = $("#contentTable tbody");
				var tr = $("<tr></tr>");
				var td="<td><select name='countryCode' class='skuOther' style='width:90%'>";
				var selfSku=$("#skuSelf").val();
				<c:forEach items='${others}' var='other'>
					if('${other}'!=selfSku){
						td=td.concat("<option value='"+'${other}'+"'>"+'${other}${fn:contains(other,"com2")?"(USNEW)":""}${fn:contains(other,"com3")?"(USTOMONS)":""}'+"["+fnskuMap['${other}']+"]"+"</option>");
					}
				</c:forEach>
				td=td.concat("</select></td>");
				tr.append(td);
				tr.append("<td><select name = 'operationType'  class='operationType' style='width:90%'><c:forEach items='${typeMap}' var='map'><option value='${map.key}'>${map.value}</option></c:forEach></select></td>");
				tr.append("<td><input name='quantity' class='number quantity' type='text' style='width:90%' /></td>");
				tr.append("<td><input name='remark' class='remark' type='text' style='width:90%' /></td>");
				tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>Delete</a></td>");
				
				tr.find("select.skuOther").select2();
				tr.find("select.operationType").select2();
				tbody.append(tr);
				
				
				 $("#changeAfter").css("display","none");
				 $(".btn-primary").css("display","none");
				 if($("#viewAfter").text()=='Re-watch'){
					$("#viewAfter").text('Preview');
				}
			});
			
			$("#add-row").click();
			
			$("select#skuSelf").on("change",function(){
				 $("#contentTable tbody tr").each(function(){
					 $(this).remove();
				 });
				 
				$(".newQuantity").text(inventoryMaps[$(this).val()][0]);
				$(".oldQuantity").text(inventoryMaps[$(this).val()][1]);
				$(".brokenQuantity").text(inventoryMaps[$(this).val()][2]);
				$(".renewQuantity").text(inventoryMaps[$(this).val()][3]);
				$(".usableQuantity").text(inventoryMaps[$(this).val()][4]);
				
				 $("#add-row").click();
			});
			
			 $(".remove-row").live('click',function(e){
				 if($('#contentTable tbody tr').size()>1){
					 var tr = $(this).parent().parent();
					 tr.remove();
					 
					 $("#changeAfter").css("display","none");
					 $(".btn-primary").css("display","none");
					 if($("#viewAfter").text()=='Re-watch'){
						$("#viewAfter").text('Preview');
					}
				 }
			});
			 
			 $(".operationType,.skuOther").live('change',function(){
				 $("#changeAfter").css("display","none");
				 $(".btn-primary").css("display","none");
				 if($("#viewAfter").text()=='Re-watch'){
					$("#viewAfter").text('Preview');
				}
					 
			 });
			 $(".quantity").live('input',function(){
				 $("#changeAfter").css("display","none");
				 $(".btn-primary").css("display","none");
				 if($("#viewAfter").text()=='Re-watch'){
						$("#viewAfter").text('Preview');
				}
					 
			 });
			 
			 
			 $("#viewAfter").on('click',function(){ 
				 
				 if(!$("#inputForm").valid()){
					 return false;
				 };
				 var keyStr="";
				 var flag = 1;
				 var zeroFlag=1;
				 var totalQuantity =0;
				 var twoStr="";
				 
				$("#contentTable tbody tr").each(function(){
					var curkeyStr=$(this).find("select.skuOther").children("option:selected").text()+$(this).find("select[name='operationType']").children("option:selected").text();
					if(keyStr.indexOf(curkeyStr+",")>=0){
						twoStr=curkeyStr;
						flag = 2;
						return false;
					}else{
						keyStr=keyStr+curkeyStr+",";
					};
					var quantity = $(this).find(".quantity").val();
					if(quantity==0){
						zeroFlag=2;
						return false;
					}
					totalQuantity=parseInt(totalQuantity)+parseInt(quantity);
				});
				
				
				if(flag==2){
					top.$.jBox.tip("Same sku and type only has one record ！"+twoStr, 'info',{timeout:3000});
					return false;
				}
				
				if(zeroFlag==2){
					top.$.jBox.tip("Quantity must be >0", 'info',{timeout:3000});
					return false;
				}
					
				var usableQuantity =$(".usableQuantity").text();
				if(usableQuantity<totalQuantity){
					top.$.jBox.tip("Change total quantity must be <= usable quantity", 'info',{timeout:3000});
					return false;
				}
				
				
				 $("#changeAfter").css("display","block");
				 $(".btn-primary").css("display","block");
				 if($(this).text()=='Preview'){
					$(this).text('Re-watch');
				}
					 
				 var dataStr="";
				 $("#contentTable tbody tr").each(function(){
					 dataStr=dataStr+$(this).find("select.skuOther").val()+","+$(this).find("select[name='operationType']").val()+","+$(this).find("input[name='quantity']").val()+";";
				 }); 
				 
				 $("#dataStr").val(dataStr);
				 
				 $('#changeTable tbody tr').each(function(){
					 $(this).remove();
				 });
				 
				    var formParam = $("#inputForm").serialize();//序列化表格内容为字符串    
				    $.ajax({    
				        type:'post',        
				        url:'${ctx}/psi/psiInventory/ajaxView',    
				        data:formParam,    
				        cache:false,    
				        dataType:'json',    
				        success : function(data) {  
	                        if(data.msg =="true" ){  
	                      	 createData(data);
	                      	 $("#saveMsg").val(data.saveMsg);
	                        }else{  
	                        	 alert("Search error！");
	                        }  
	                    },  
	                    error : function() {  
	                        alert("Excetion！");  
	                    }     
				    });    
			 }); 
			 
			 $("#btnSubmit").on('click',function(){
				 if($("#inputForm").valid()){
					 $("#inputForm").submit();
				 }else{
					 return false;
				 };
				 
			 });
			 
			 $("#inputForm").validate({
				 rules:{
					 "quantity":{"required":true}
				 },messages:{
					"quantity":{"required":'Quantity is not empty!'}
				 },
					submitHandler: function(form){
						var keyStr="";
						var flag = 1;
						var zeroFlag=1;
						$("#contentTable tbody tr").each(function(){
							var curkeyStr=$(this).find("select.skuOther").children("option:selected").text()+$(this).find("select[name='operationType']").children("option:selected").text();
							if(keyStr.indexOf(curkeyStr+",")>=0){
								twoStr=curkeyStr;
								flag = 2;
								return false;
							}else{
								keyStr=keyStr+curkeyStr+",";
							};
									
							var quantity = $(this).find(".quantity").val();
							if(quantity<=0){
								zeroFlag=2;
								return false;
							}
						});
						
						if(flag==2){
							top.$.jBox.tip("Same sku and type only has one record ！"+twoStr, 'info',{timeout:3000});
							return false;
						}
						
						if(zeroFlag==2){
							top.$.jBox.tip("Quantity must be >0", 'info',{timeout:3000});
							return false;
						}
						if($("#saveMsg").val()!=""){
							top.$.jBox.tip($("#saveMsg").val(), 'info',{timeout:3000});
							return false;
						}
						$("#contentTable tbody tr").each(function(i,j){
							$(j).find("select").each(function(){
								if($(this).attr("name")){
									$(this).attr("name","changeItems"+"["+i+"]."+$(this).attr("name"));
								}
							});
							$(j).find("input[type!='']").each(function(){
								if($(this).attr("name")){
									$(this).attr("name","changeItems"+"["+i+"]."+$(this).attr("name"));
								}
							});
						}); 
						
						form.submit();
						$("#btnSubmit").attr("disabled","disabled");
					},
					errorContainer: "#messageBox",
					errorPlacement: function(error, element) {
						$("#messageBox").text("输入有误，请先更正。");
						error.appendTo($("#errorsShow"));
					}
				});
				
		});
		
		
		 
		 function createData(data){
			 var tbody =$("#changeTable tbody");
             for(var i=0;i<data.inventory.length;i++){
             	var inventory = data.inventory[i];
             	var tr;
             	if(i==data.inventory.length-1){
             		 tr=$("<tr style='color:red'></tr>");
             	}else{
             		tr=$("<tr style='color:green'></tr>");
             	}
             	
             	var wareStr = inventory.warehouseName;
             	var wareArr =wareStr.split(";");
             	//if(inventory.countryCode=='com'){
             	//	inventory.countryCode='us';
             	//}
             	tr.append("<td>"+inventory.sku+"</td>");
             	tr.append("<td>"+inventory.newQuantity+"&nbsp;"+wareArr[0]+"</td>");
             	tr.append("<td>"+inventory.oldQuantity+"&nbsp;"+wareArr[1]+"</td>");
             	tr.append("<td>"+inventory.brokenQuantity+"&nbsp;"+wareArr[2]+"</td>");
             	tr.append("<td>"+inventory.renewQuantity+"&nbsp;"+wareArr[3]+"</td>");
             	tbody.append(tr);
             }
             if('${psiInventory.countryCode}'=='de'&&'${psiInventory.warehouse.countrycode}'=='DE'){
            	 //判断剩余数量是不是小于库存预留 
                 $("#errorsShow").text("");
                 var totalQuantity =0;
                 $("#contentTable tbody tr").each(function(){
                	 totalQuantity=parseInt(totalQuantity)+parseInt($(this).find(".quantity").val());
                 });
                 var reduce = parseInt($(".usableQuantity").text())-parseInt(totalQuantity);
                 if(reduce<=parseInt('${residue}')){
                	$("#errorsShow").text("Surplus useable quantity:"+reduce+" < obligate quantity:"+'${residue}'+",Please note!");
                	$("#overResidue").val("Surplus useable quantity:"+reduce+" < obligate quantity:"+'${residue}'+",Please note!");
                 }
             }
            
		 }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a  href="#">Sku Change</a></li>
		<li><a href="${ctx}/psi/psiInventory/newOldChange?productId=${psiInventory.productId}&productName=${psiInventory.productName}&warehouse.id=${psiInventory.warehouse.id}&warehouseName=${psiInventory.warehouse.stockSign}&colorCode=${psiInventory.colorCode}&countryCode=${psiInventory.countryCode}">QualityType Change</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="psiInventory" action="${ctx}/psi/psiInventory/countryChangeSave" method="post" class="form-horizontal">
		<input type ="hidden" name="id"  		   value="${psiInventory.id}"/>
		<input type ="hidden" name="warehouse.id"  value="${psiInventory.warehouse.id}"/>
		<input type ="hidden" name="warehouseName" id="dataStr"/>
		<input type ="hidden" id="saveMsg" value=""/>
		<input type ="hidden" name="overResidue" value="" id="overResidue"/>
		<tags:message content="${message}"/>
		<blockquote>
			<p style="font-size: 14px">Base Info.</p>
		</blockquote>
		
		<table id="headTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				   <th style="width: 8%">Warehouse</th>
				   <th style="width: 8%">Product</th>
				   <th style="width: 5%">Country</th>
				   <th style="width: 5%">Color</th>
				   <th style="width: 5%">Pack Quantity</th>
				   <th style="width: 20%">Choice Sku</th>
				   <th style="width: 5%">usable</th>
				   <th style="width: 5%">new</th>
				   <th style="width: 5%">old</th>
				   <th style="width: 5%">broken</th>
				   <th style="width: 5%">renew</th>
				   <th style="width: 5%">spares</th>
				   <th style="width: 5%">offline</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>${psiInventory.warehouse.stockSign}</td>
					<td>${psiInventory.productName}</td>
					<td>${fns:getDictLabel(psiInventory.countryCode, 'platform', '')}</td>
					<td>${psiInventory.colorCode}</td>
					<td>${packQuantity}</td>
					<td>
						<select name="sku" id="skuSelf" style="width:90%">
							<c:forEach items="${selfs}" var="self">
								<option value="${self}"${self eq psiInventory.sku?'selected':''}>${self}[${fnskuMap[self]}]</option>
							</c:forEach>
						</select>
					</td>
					<td><span class="usableQuantity">${psiInventory.usableQuantity}</span></td>
					<td><span class="newQuantity">${psiInventory.newQuantity}</span></td>
					<td><span class="oldQuantity">${psiInventory.oldQuantity}</span></td>
					<td><span class="brokenQuantity">${psiInventory.brokenQuantity}</span></td>
					<td><span class="renewQuantity">${psiInventory.renewQuantity}</span></td>
					<td><span class="sparesQuantity">${psiInventory.sparesQuantity}</span></td>
					<td><span class="offlineQuantity">${psiInventory.offlineQuantity}</span></td>
				</tr>
			</tbody>
		</table>
		<div style="float: left"><blockquote><p style="font-size: 14px">Sku Change Info.</p></blockquote></div><div style="float: left;color:red" id=errorsShow></div>
			<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;">
			<a id="viewAfter" class="btn btn-small btn-info " style="font-size: 14px;margin: 5px 200px 5px 0px;">Before Save Preview</a>
			<a href="#" id="add-row"><span class="icon-plus"></span>Add</a></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
					   <th style="width: 30%">sku</th>
					   <th style="width: 15%">type</th>
					   <th style="width: 10%">Quantity</th>
					   <th style="width: 30%">Remark</th>
					   <th >Operate</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			
			<div id="changeAfter" style="display: none">
			<blockquote>
				<p style="font-size: 14px">After Change Info.</p>
			</blockquote>
			
			<table id="changeTable" class="table table-striped table-bordered table-condensed" >
				<thead>
					<tr>
					   <th style="width: 20%">sku</th>
					   <th style="width: 20%">new</th>
					   <th style="width: 20%">old</th>
					   <th style="width: 20%">broken</th>
					   <th style="width: 20%">renew</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			
		</div>
		
		
		<div class="form-actions" >
			<input id="btnSubmit" class="btn btn-primary" type="button" style="display:none;float:left" value="Save"/> &nbsp;&nbsp;
		 	<span style="padding-left:200px; text-align:center;float:left"><input id="btnCancel"  class="btn" type="button" value="Back" style="" onclick="history.go(-1)"/></span>
		</div>
			
	</form:form>
			
		
</body>
</html>
