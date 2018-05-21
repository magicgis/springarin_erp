<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件收货</title>
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
		      
		eval('var totalMap=${totalMap}');
		eval('var productMap=${productMap}');
		eval('var ladingMap=${ladingMap}');
		eval('var batchTotalMap=${batchTotalMap}');  
		
		// 生成要编辑表格 
		var createTable = $('#contentTable');
		for(var key in ladingMap){
			var deliveryDtos=ladingMap[key];
			var total =batchTotalMap[key];
			var tbody =$("<tbody></tbody>");
			if(deliveryDtos.length>0){
				var dataSize=deliveryDtos.length;
				for(var i=0;i<dataSize;i++){
					var deliveryDto =deliveryDtos[i];
					var tr ="";
					if(i==0){
						tr = $("<tr></tr>");
			            var td = "<td style='text-align:center'> <select style='width: 90%' class='partsName'  name='partsName'>";
			            for (var key1 in totalMap) {
			            	if(ladingMap[key1]==null){
			            		td = td.concat("<option value='"+key1+"'>"+key1+"</option>");
			            	}	
						}
			            td = td.concat("<option value='"+key+"'>"+key+"</option>");
			            td = td.concat("</select></td>");
			            tr.append(td);
			            tr.append("<td><input type='hidden' name='partsOrderItem.id'/><input type='hidden' name='partsId'/><input type='hidden' name='itemPrice'/><input  style='width: 90%' type='text' name='canLadingTotal' readonly='readonly' /></td>");
			            tr.append("<td> <a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
			            tr.append("<td> <input  style='width: 90%' type='text' class='canLadingQuantity' readonly='readonly' /></td>");
			            tr.append("<td> <input  style='width: 90%' type='text' class='number' name='quantityLading' /></td>");
			            tr.append("<td> <input  style='width: 90%' type='text' name='remark' /></td>");
			            tr.append("<td style='text-align:center'> <a href='' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除产品</a></td>");
			            tr.find("select[name='partsName']").select2().select2("val",key);
			            
			            tr.find("td:first").attr("rowSpan", dataSize).css("vertical-align","middle").css("text-align","center");
						tr.find("td:first").next("td").attr("rowSpan",dataSize).css("vertical-align","middle").css("text-align","center");
						tr.find("td:first").next("td").next("td").attr("rowSpan",dataSize).css("vertical-align","middle").css("text-align","center");
						tr.find("td:last").attr("rowSpan",dataSize).css("vertical-align","middle").css("text-align","center");
					}else{
						tr =$("<tr class='notFirstRow'></tr>");
			            tr.append("<td><input type='hidden' name='partsOrderItem.id'/><input type='hidden' name='itemPrice'/><input type='hidden' name='partsName'/><input type='hidden' name='partsId'/> <input type='text' style='width: 90%' class='canLadingQuantity' readonly='readonly' /></td>");
			            tr.append("<td> <input type='text' style='width: 90%' class='number' name='quantityLading' /></td>");
			            tr.append("<td> <input type='text' style='width: 90%' name='remark' /></td>");
						tr.find("input[name='partsName']").val(key);
					}
					
					tr.find("input[name='partsOrderItem.id']").val(deliveryDto.partsOrderItemId);
				    var hrefs="${ctx}/psi/purchaseOrder/view?id="+deliveryDto.partsOrderId;   
				    tr.find(".purchaseOrderNo").text(deliveryDto.partsOrderNo);
			        tr.find(".canLadingQuantity").val(deliveryDto.canLadingQuantity);
					tr.find(".orderId").attr('href',hrefs); 
					tr.find("input[name='quantityLading']").val(deliveryDto.canLadingQuantity);
					tr.find("input[name='partsId']").val(deliveryDto.partsId);
					tr.find("input[name='partsName']").val(deliveryDto.partsName);
					tr.find("input[name='itemPrice']").val(deliveryDto.price);
					tr.find("select.partsName").select2();
					tbody.append(tr);
				}
			tbody.find("input[name='canLadingTotal']").val(total);
			createTable.append(tbody);
			}
		}

		$("#inputForm").on("change",".partsName",function(e){
			var productVal = e.val;
			if(e.removed){
				var removeVal = e.removed.id;
				$("select.partsName").each(function(){
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
			var changeDtos =productMap[$(this).val()];
				if(changeDtos&&changeDtos.length>0){
					var dtoSize=changeDtos.length;
					for(var i=0;i<dtoSize;i++){
						var changeDto = changeDtos[i];
						if(i==0){
							var tr=$(this).parent().parent();
							tr.find("input[name='partsOrderItem.id']").val(changeDto.partsOrderItemId);
						    tr.find(".purchaseOrderNo").text(changeDto.partsOrderNo);
					        tr.find(".canLadingQuantity").val(changeDto.canLadingQuantity);
					        tr.find(".canLadingTotal").val(canLadingTotal);
							tr.find("input[name='quantityLading']").val(changeDto.canLadingQuantity);
							tr.find("input[name='itemPrice']").val(changeDto.price);
							tr.find("input[name='partsId']").val(changeDto.partsId);  
							var hrefs="${ctx}/psi/purchaseOrder/view?id="+changeDto.partsOrderId;  
							tr.find(".orderId").attr('href',hrefs); 
							tr.parent().each(function(){
							    $(this).find("td:first").attr("rowSpan", dtoSize).css("vertical-align","middle").css("text-align","center");
							    $(this).find("td:first").next("td").attr("rowSpan",dtoSize).css("vertical-align","middle").css("text-align","center");
							    $(this).find("td:first").next("td").next("td").attr("rowSpan",dtoSize).css("vertical-align","middle").css("text-align","center");
							    $(this).find("td:last").attr("rowSpan",dtoSize).css("vertical-align","middle").css("text-align","center");

							});
							tbody.append(tr);
						}else{
							var tr =$("<tr class='notFirstRow'></tr>");
				            tr.append("<td><input type='hidden' name='partsName'/><input type='hidden' name='partsId'/><input type='hidden' name='partsOrderItem.id'/><input type='hidden' name='itemPrice'/> <input type='text' style='width: 90%' class='canLadingQuantity' readonly='readonly' /></td>");
				            tr.append("<td> <input type='text' style='width: 90%' class='number' name='quantityLading' /></td>");
				            tr.append("<td> <input type='text' style='width: 90%' name='remark' /></td>");
				            tr.find("input[name='partsName']").val($(this).val());
							tr.find("input[name='partsOrderItem.id']").val(changeDto.partsOrderItemId);
						    tr.find(".purchaseOrderNo").text(changeDto.partsOrderNo);
					        tr.find(".canLadingQuantity").val(changeDto.canLadingQuantity);
					        tr.find(".canLadingTotal").val(canLadingTotal);
							tr.find("input[name='quantityLading']").val(changeDto.canLadingQuantity);
							tr.find("input[name='itemPrice']").val(changeDto.price);
							tr.find("input[name='partsId']").val(changeDto.partsId);   
							var hrefs="${ctx}/psi/purchaseOrder/view?id="+changeDto.partsOrderId;  
							tr.find(".orderId").attr('href',hrefs); 
				           	tbody.append(tr);
						}
					}
				}
		});
		
		
		$('#contentTable').on('click', '.remove-row', function(e){
			  e.preventDefault();
			  if($('#contentTable tbody').size()>1){
				  var tbody = $(this).parent().parent().parent();
				  var id = tbody.find(".partsName").select2("val");
				  tbody.remove();
				  if(id){
					  $("select.partsName").each(function(){
	          				$(this).append("<option value='"+id+"'>"+id+"</option>");
	          		  });
				  }
			  }
		});
		
		$('#add-row').on('click', function(e){
			e.preventDefault();
		    var table = $("#contentTable");
           	var tbody =$("<tbody></tbody>");
            var tr = $("<tr></tr>");
            var td = "<td style='text-align:center'> <select style='width: 90%' class='partsName'  name='partsName'>"
            var i = 0 ;
            for (var key in totalMap) {
            	var flag = 0;
            	$("select.partsName").each(function(){
    				if(key==$(this).select2("val")){
    					flag =1;
    				}
    			});
            	if(flag==0){
            		if(i==0){
            			$("select.partsName").each(function(){
            				$(this).find("option[value='"+key+"']").remove();
            			});
            		}
            		td = td.concat("<option value='"+key+"'>"+key+"</option>");
            		i++;
            	}	
			}
            td = td.concat("</select></td>");
            tr.append(td);
            
            tr.append("<td><input type='hidden' name='partsOrderItem.id'/><input type='hidden' name='partsId'/> <input type='hidden' name='itemPrice'/><input  style='width: 90%' type='text' class='canLadingTotal' readonly='readonly' /></td>");
            tr.append("<td> <a target='_blank' class='orderId purchaseOrderNo' href='#'/></td>");
            tr.append("<td> <input  style='width: 90%' type='text' class='canLadingQuantity' readonly='readonly' /></td>");
            tr.append("<td> <input  style='width: 90%' type='text' name='quantityLading' class='number' /></td>");
            tr.append("<td> <input  style='width: 90%' type='text' name='remark' /></td>");
            tr.append("<td style='text-align:center'> <a href='' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除产品</a></td>");
            tr.find(".partsName").select2();
           
            tbody.append(tr);
            $(table).append(tbody);
            
            tr.find(".partsName").change();
		});
			
			
			
			
			$("#inputForm").validate({
				rules:{
					"partsName":{
						"required":true
					},
					"quantityLading":{
						"required":true
					}
				},
				messages:{
					"partsName":{"required":'配件名不能为空'},
					"quantityLading":{"required":'提货数量不能为空'}
				},
				submitHandler: function(form){
					
					//提货数必须>0,并且
					var numberflag  =true;
					$("#contentTable tbody tr").each(function(i,j){
						if($(this).find("input[name='quantityLading']").val()!=''&&$(this).find("input[name='quantityLading']").val()!=0){
							if($(this).find(".canLadingQuantity").val()-$(this).find("input[name='quantityLading']").val()<0){
								numberflag = false;
								return ;
							}
						}
					});    
					
					if(!numberflag){
						top.$.jBox.tip("收货数量不能大于可收货数量！","info",{timeout:3000});
						return false;
					}
					
					
					var oneFlag = 0;
					$("#contentTable tbody tr").each(function(i,j){
						var ladingQuantity  = $(this).find("input[name='quantityLading']").val();
						if(ladingQuantity!=''&&parseInt(ladingQuantity)!=0){
							oneFlag = oneFlag+1;
							return;
						}
					});
					
					if(oneFlag==0){
						top.$.jBox.tip("必须有一行的收货数为大于0","info",{timeout:3000});
						return false;
					};
					
					 top.$.jBox.confirm('您确认要提交收货单，提交后系统自动给收货厂商发送收货邮件','系统提示',function(v,h,f){
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
							
							form.submit();
							$("#btnSubmit").attr("disabled","disabled");
						}
						return true;
						},{buttonsFocus:1,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/lcPsiPartsDelivery/">配件收货单列表</a></li>
		<li class="active"><a href="#">创建配件收货单</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiPartsDelivery" action="${ctx}/psi/lcPsiPartsDelivery/save" method="post" class="form-horizontal">
		<tags:message content="${message}"/>
		<input type='hidden' name="billNo" 					value="${psiLadingBill.billNo}">
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
				<label class="control-label"><b>送货日期</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" name="deliveryDate" required="required" class="Wdate" value="<fmt:formatDate value="${psiLadingBill.deliveryDate}" pattern="yyyy-MM-dd" />" />
				</span>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>收货厂商</b>:</label>
				<div class="controls" >   
					<select style="width:150px;" id="supplier" name="tranSupplier.id">
						<option value="" >深圳办事处</option>
						<c:forEach items="${suppliers}" var="supplier" varStatus="i" >
							 <option value="${supplier.id}">${supplier.nikename}</option>
						</c:forEach>
					</select>
				</div>
			</div>
		</div>
		<div style="float: left"><blockquote><p style="font-size: 14px">配件收货单项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加收货配件</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					   <th style="width: 20%">配件名称</th>
					   <th style="width: 8%">可提总数</th>
					   <th style="width: 20%">订单号</th>
					   <th style="width: 8%">可提数量</th>
					   <th style="width: 8%">实提数量</th>
					   <th style="width: 15%">备注</th>
					   <th style="width: 19%">操作</th>
				</tr>
			</thead>
		</table>
	
	
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
