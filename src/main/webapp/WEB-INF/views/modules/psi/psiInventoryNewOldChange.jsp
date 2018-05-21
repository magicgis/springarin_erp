<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Inventory QualityType Change</title>
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
			if($("select#skuSelf").val()!=''){
				var sku=$("select#skuSelf").val();
				if(inventoryMaps[sku]){
					$(".newQuantity").text(inventoryMaps[sku][0]);
					$(".oldQuantity").text(inventoryMaps[sku][1]);
					$(".brokenQuantity").text(inventoryMaps[sku][2]);
					$(".renewQuantity").text(inventoryMaps[sku][3]);
					$(".sparesQuantity").text(inventoryMaps[sku][4]);
					$(".offlineQuantity").text(inventoryMaps[sku][5]);
				}
			}
			
			$("#add-row").on('click',function(e){
				e.preventDefault();
				var tbody = $("#contentTable tbody");
				var tr = $("<tr></tr>");
				tr.append("<td><select name = 'operationType'  class='operationType' style='width:90%'><c:forEach items='${typeMap}' var='map'><option value='${map.key}'>${map.value}</option></c:forEach></select></td>");
				tr.append("<td><input name='quantity' class='quantity' type='text' style='width:90%' /></td>");
				tr.append("<td><input name='remark' class='remark' type='text' style='width:90%' /></td>");
				tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>Delete</a></td>");
				tr.find("select.operationType").select2();
				tbody.append(tr);
				 $("#changeAfter").css("display","none");
				 $(".btn-primary").css("display","none");
				 if($(this).text()=='Preview'){
					$(this).text('Recheck');
				}
			});
			
			$("#add-row").click();
			$("select#skuSelf").on("change",function(){
				 $("#contentTable tbody tr").each(function(){
					 $(this).remove();
				 });
				 var sku=$(this).val();
				 if(inventoryMaps[sku]){
					$(".newQuantity").text(inventoryMaps[sku][0]);
					$(".oldQuantity").text(inventoryMaps[sku][1]);
					$(".brokenQuantity").text(inventoryMaps[sku][2]);
					$(".renewQuantity").text(inventoryMaps[sku][3]);
					$(".sparesQuantity").text(inventoryMaps[sku][4]);
					$(".offlineQuantity").text(inventoryMaps[sku][5]);
				 }
				 $("#add-row").click();
			});
			
			 $(".operationType").live('change',function(){
				 $("#changeAfter").css("display","none");
				 $(".btn-primary").css("display","none");
				 if($(this).text()=='Preview'){
					$(this).text('Recheck');
				}
			 });	
			 $(".quantity").live('input',function(){
				 $("#changeAfter").css("display","none");
				 $(".btn-primary").css("display","none");
				 if($("#viewAfter").text()=='Recheck'){
						$("#viewAfter").text('Preview');
				}
					 
			 });
		 
			 $(".remove-row").live('click',function(e){
				 if($('#contentTable tbody tr').size()>1){
					 var tr = $(this).parent().parent();
					 tr.remove();
					 
					 $("#changeAfter").css("display","none");
					 $(".btn-primary").css("display","none");
					 if($("#viewAfter").text()=='Recheck'){
							$("#viewAfter").text('Preview');
					}
				 }
			});
			 
			 $("#viewAfter").on('click',function(){ 
				 if($("#skuSelf").val()=='${psiInventory.productColorCountry}'){
					 top.$.jBox.tip("该sku不能调换！", 'info',{timeout:3000});
					 return false;
				 }
				 
				 if(!$("#inputForm").valid()){
					 return false;
				 };
				 
				    var keyStr="";
					var flag = 1;
					var zeroFlag=1;
					$("#contentTable tbody tr").each(function(){
						var curkeyStr=$(this).find("select[name='operationType']").children("option:selected").text();
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
							
					});
					
					if(flag==2){
						top.$.jBox.tip("同一类型只能有一条记录 ！"+twoStr, 'info',{timeout:3000});
						return false;
					}
					if(zeroFlag==2){
						top.$.jBox.tip("数量必须大于0 ！", 'info',{timeout:3000});
						return false;
					}
					
					$("#changeAfter").css("display","block");
					 $(".btn-primary").css("display","block");
					 if($(this).text()=='Preview'){
						$(this).text('Recheck');
					}
					 $('#changeTable tbody tr').each(function(){
						 $(this).remove();
					 });
					//生成数据	 
					 createData();
						
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
					 "quantity":{"required":true},
					 "remark":{"required":true}
						 
				 },messages:{
					 "quantity":{"required":'数量不能为空'},
					 "remark":{"required":'备注不能为空'},
				 },
					submitHandler: function(form){
						var keyStr="";
						var twoStr="";
						var flag = 1;
						var zeroFlag =1;
						$("#contentTable tbody tr").each(function(){
							var curkeyStr=$(this).find("select[name='operationType']").children("option:selected").text();
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
						});
						
						if(flag==2){
							top.$.jBox.tip("同一类型只能有一条记录 ！"+twoStr, 'info',{timeout:3000});
							return false;
						}
						
						if(zeroFlag==2){
							top.$.jBox.tip("数量必须大于0！", 'info',{timeout:3000});
							return false;
						}
						
						//校验数据
						
						var  newTotal=0;
						var  oldTotal=0;
						var  brokenTotal=0;
						var  renewTotal=0;
						var  sparesTotal=0;
						var  offlineTotal=0;
						$("#contentTable tbody tr").each(function(){
							var operationType=$(this).find("select.operationType").val();
							var quantity=$(this).find(".quantity").val();
							 if(operationType=="1"){//New_To_Old
								newTotal=newTotal-quantity;
								oldTotal=parseInt(oldTotal)+parseInt(quantity);
							}else if(operationType=="2"){//New_To_Broken
								newTotal=newTotal-quantity;
								brokenTotal=parseInt(brokenTotal)+parseInt(quantity);
							}else if(operationType=="3"){//New_To_Renew
								newTotal=newTotal-quantity;
								renewTotal= parseInt(renewTotal)+parseInt(quantity);
							}else if(operationType=="4"){//Old_To_New
								oldTotal = oldTotal-quantity;
								newTotal=parseInt(newTotal)+parseInt(quantity);
							}else if(operationType=="5"){//Old_To_Broken
								oldTotal = oldTotal-quantity;
								brokenTotal=parseInt(brokenTotal)+parseInt(quantity);
							}else if(operationType=="6"){//Old_To_Renew
								oldTotal = oldTotal-quantity;
								renewTotal= parseInt(renewTotal)+parseInt(quantity);
							}else if(operationType=="7"){//Broken_To_New
								brokenTotal=brokenTotal-quantity;
								newTotal=parseInt(newTotal)+parseInt(quantity);
							}else if(operationType=="8"){//Broken_To_Old
								brokenTotal=brokenTotal-quantity;
								oldTotal=parseInt(oldTotal)+parseInt(quantity);
							}else if(operationType=="9"){//Broken_To_Renew
								brokenTotal=brokenTotal-quantity;
								renewTotal= parseInt(renewTotal)+parseInt(quantity);
							}else if(operationType=="10"){//Renew_To_New
								renewTotal= renewTotal-quantity;
								newTotal=parseInt(newTotal)+parseInt(quantity);
							}else if(operationType=="11"){//Renew_To_Old
								renewTotal= renewTotal-quantity;
								oldTotal=parseInt(oldTotal)+parseInt(quantity);
							}else if(operationType=="12"){//Renew_To_Broken
								renewTotal= renewTotal-quantity;
								brokenTotal=parseInt(brokenTotal)+parseInt(quantity);
							}else if(operationType=="13"){//Spares_To_New
								sparesTotal= sparesTotal-quantity;
								newTotal=parseInt(newTotal)+parseInt(quantity);
							}else if(operationType=="14"){//New_To_Offline
								newTotal= newTotal-quantity;
								offlineTotal=parseInt(offlineTotal)+parseInt(quantity);
							}else if(operationType=="15"){//Offline_To_New
								offlineTotal=offlineTotal-quantity;
								newTotal=parseInt(newTotal)+parseInt(quantity);
							}
						});
						if(parseInt($(".newQuantity").text())+parseInt(newTotal)<0){
							top.$.jBox.tip("new数量过大，请检查 ", 'info',{timeout:3000});
							return false;
						}
						
						if(parseInt($(".oldQuantity").text())+parseInt(oldTotal)<0){
							top.$.jBox.tip("old数量过大，请检查 ", 'info',{timeout:3000});
							return false;
						}
						
						if(parseInt($(".brokenQuantity").text())+parseInt(brokenTotal)<0){
							top.$.jBox.tip("broken数量过大，请检查 ", 'info',{timeout:3000});
							return false;
						}
						
						
						if(parseInt($(".renewQuantity").text())+parseInt(renewTotal)<0){
							top.$.jBox.tip("renew数量过大，请检查 ", 'info',{timeout:3000});
							return false;
						}
						
						if(parseInt($(".sparesQuantity").text())+parseInt(sparesTotal)<0){
							top.$.jBox.tip("spares数量过大，请检查 ", 'info',{timeout:3000});
							return false;
						}
						
						if(parseInt($(".offlineQuantity").text())+parseInt(offlineTotal)<0){
							top.$.jBox.tip("offline数量过大，请检查 ", 'info',{timeout:3000});
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
					},
					errorContainer: "#messageBox",
					errorPlacement: function(error, element) {
						$("#messageBox").text("输入有误，请先更正。");
						error.appendTo($("#errorsShow"));
					}
				});
				
		});
			 
			function createData(){
				var  newTotal=0;
				var  oldTotal=0;
				var  brokenTotal=0;
				var  renewTotal=0;
				var  sparesTotal=0;
				var  offlineTotal=0;
				$("#contentTable tbody tr").each(function(){
					var operationType=$(this).find("select.operationType").val();
					var quantity=$(this).find(".quantity").val();
					 if(operationType=="1"){//New_To_Old
						newTotal=newTotal-quantity;
						oldTotal=parseInt(oldTotal)+parseInt(quantity);
					}else if(operationType=="2"){//New_To_Broken
						newTotal=newTotal-quantity;
						brokenTotal=parseInt(brokenTotal)+parseInt(quantity);
					}else if(operationType=="3"){//New_To_Renew
						newTotal=newTotal-quantity;
						renewTotal= parseInt(renewTotal)+parseInt(quantity);
					}else if(operationType=="4"){//Old_To_New
						oldTotal = oldTotal-quantity;
						newTotal=parseInt(newTotal)+parseInt(quantity);
					}else if(operationType=="5"){//Old_To_Broken
						oldTotal = oldTotal-quantity;
						brokenTotal=parseInt(brokenTotal)+parseInt(quantity);
					}else if(operationType=="6"){//Old_To_Renew
						oldTotal = oldTotal-quantity;
						renewTotal= parseInt(renewTotal)+parseInt(quantity);
					}else if(operationType=="7"){//Broken_To_New
						brokenTotal=brokenTotal-quantity;
						newTotal=parseInt(newTotal)+parseInt(quantity);
					}else if(operationType=="8"){//Broken_To_Old
						brokenTotal=brokenTotal-quantity;
						oldTotal=parseInt(oldTotal)+parseInt(quantity);
					}else if(operationType=="9"){//Broken_To_Renew
						brokenTotal=brokenTotal-quantity;
						renewTotal= parseInt(renewTotal)+parseInt(quantity);
					}else if(operationType=="10"){//Renew_To_New
						renewTotal= renewTotal-quantity;
						newTotal=parseInt(newTotal)+parseInt(quantity);
					}else if(operationType=="11"){//Renew_To_Old
						renewTotal= renewTotal-quantity;
						oldTotal=parseInt(oldTotal)+parseInt(quantity);
					}else if(operationType=="12"){//Renew_To_Broken
						renewTotal= renewTotal-quantity;
						brokenTotal=parseInt(brokenTotal)+parseInt(quantity);
					}else if(operationType=="13"){//Renew_To_Broken
						sparesTotal= sparesTotal-quantity;
						newTotal=parseInt(newTotal)+parseInt(quantity);	
					}else if(operationType=="14"){//Renew_To_Broken
						newTotal= newTotal-quantity;
						offlineTotal=parseInt(offlineTotal)+parseInt(quantity);	
					}else if(operationType=="15"){//Renew_To_Broken
						offlineTotal= offlineTotal-quantity;
						newTotal=parseInt(newTotal)+parseInt(quantity);	
					}
				});
				
				 	var tbody =$("#changeTable tbody");
	             	var tr=$("<tr></tr>");
	             	
	             	tr.append(createTd($(".newQuantity").text(),newTotal));
	             	tr.append(createTd($(".oldQuantity").text(),oldTotal));
	             	tr.append(createTd($(".brokenQuantity").text(),brokenTotal));
	             	tr.append(createTd($(".renewQuantity").text(),renewTotal));
	             	tr.append(createTd($(".sparesQuantity").text(),sparesTotal));
	             	tr.append(createTd($(".offlineQuantity").text(),offlineTotal));
	             	tbody.append(tr);
				};
				
				function createTd(quantity,total){
					var tdStr="<td>"+quantity+"&nbsp;";
	             	if(total>0){
	             		tdStr=tdStr+"<span style='color:green'>+"+total+"</span></td>";
	             	}else if(total==0){
	             		tdStr=tdStr+"</td>";
	             	}else{
	             		tdStr=tdStr+"<span style='color:red'>"+total+"</span></td>";
	             	}
					
					return tdStr;
				}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a  href="${ctx}/psi/psiInventory/countryChange?productId=${psiInventory.productId}&productName=${psiInventory.productName}&warehouse.id=${psiInventory.warehouse.id}&warehouseName=${psiInventory.warehouse.stockSign}&colorCode=${psiInventory.colorCode}&countryCode=${psiInventory.countryCode}">Sku Change</a></li>
		<li class="active"><a href="#">QualityType Change</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="psiInventory" action="${ctx}/psi/psiInventory/newOldChangeSave" method="post" class="form-horizontal">
		<input type ="hidden" name="id"  		   value="${psiInventory.id}"/>
		<input type ="hidden" name="warehouse.id"  		   value="${psiInventory.warehouse.id}"/>
		<tags:message content="${message}"/>
		<blockquote>
			<p style="font-size: 14px">Base Info.</p>
		</blockquote>
			<table id="headTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			   <th style="width: 10%">Warehouse</th>
			   <th style="width: 15%">Product</th>
			   <th style="width: 10%">Country</th>
			   <th style="width: 10%">Color</th>
			   <th style="width: 20%">Choice Sku</th>
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
				<td>
				<c:choose>
					<c:when test="${psiInventory.warehouse.id eq '19'}">Germany</c:when>
					<c:when test="${psiInventory.warehouse.id eq '21'}">China</c:when>
					<c:when test="${psiInventory.warehouse.id eq '120'}">American</c:when>
				</c:choose>
				</td>
				<td>${psiInventory.productName}</td>
				<td>${fns:getDictLabel(psiInventory.countryCode, 'platform', '')}</td>
				<td>${psiInventory.colorCode}</td>
				<td>
					<select name="sku" id="skuSelf">
						<c:forEach items="${skuSet}" var="self">
							<option value="${self}">${self}</option>
						</c:forEach>
					</select>
				</td>
				<td><span class="newQuantity">${psiInventory.newQuantity}</span></td>
				<td><span class="oldQuantity">${psiInventory.oldQuantity}</span></td>
				<td><span class="brokenQuantity">${psiInventory.brokenQuantity}</span></td>
				<td><span class="renewQuantity">${psiInventory.renewQuantity}</span></td>
				<td><span class="sparesQuantity">${psiInventory.sparesQuantity}</span></td>
				<td><span class="offlineQuantity">${psiInventory.offlineQuantity}</span></td>
			</tr>
		</tbody>
	</table>
		<div style="float: left"><blockquote><p style="font-size: 14px">Sku QualityType Info.(new、old、broken、renew)</p></blockquote></div><div style="float: left" id=errorsShow></div>
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;">
			<a id="viewAfter" class="btn btn-small btn-info " style="font-size: 14px;margin: 5px 200px 5px 0px;">Before Save Preview</a>
			<a href="#" id="add-row"><span class="icon-plus"></span>Add</a></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
					   <th style="width: 20%">Change Type</th>
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
					   <th style="width: 20%">new</th>      
					   <th style="width: 20%">old</th>
					   <th style="width: 20%">broken</th>   
					   <th style="width: 20%">renew</th>
					   <th style="width: 20%">spares</th>
					   <th style="width: 20%">offline</th>
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
