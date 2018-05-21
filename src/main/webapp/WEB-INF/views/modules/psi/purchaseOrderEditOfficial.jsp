<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单管理</title>
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
			eval('var receivedMap=${receivedMap}');
			eval('var poPrice=${poPrice}');
			new tabTableInput("inputForm","text");
			var countryMap = {};
			<c:forEach items="${fns:getDictList('platform')}" var="dic">
				countryMap['${dic.value}'] = '${dic.label}';
			</c:forEach>
			
			var productArgs =[];
			var colorArgs =[];
			var countryArgs=[];
			var  colorsStr;
			var  countryStr;
			var  product;
			<c:forEach items="${products}" var="product" varStatus="i">
			  	product={};
			  	product.id='${product.id}';
			  	product.packQuantity='${product.packQuantity}';   
			  	product.color='${product.color}';
			  	product.platform ='${product.platform}';
			  	product.colorPlatform ='${product.colorPlatform}';
			  	productArgs['${product.id}']=product;
			</c:forEach>
			
			$("select.colors").each(function(){
				var $this = $(this);
				var tbody = $this.parent().parent().parent();
				var productVal = tbody.find("select[name='product.id']").select2().val();
				var colorPlatformStr = productArgs[productVal].colorPlatform;
				
				var country = tbody.find("input[name='itemCountry']").val();
				var selectColor = tbody.find("input[name='itemColor']").val();
				
				if(selectColor != "" && selectColor != "No color"){
					var countryStr = "";
					var colorPlatform = colorPlatformStr.split(';');
					var colorFlag = false;
					for(var j=0;j<colorPlatform.length;j++){
						if(selectColor == colorPlatform[j].split('_')[0]){
							colorFlag = true;
							countryStr = colorPlatform[j].split('_')[1];
							break;
						}
					}
					if(!colorFlag){
						tbody.find("select[name='colorCode']").select2().append("<option value='"+selectColor+"'>"+selectColor+"</option>");
						tbody.find("select[name='colorCode']").val(selectColor).trigger("change");	
					}
					tbody.find("select[name='countryCode']").select2("data",[]);
					tbody.find("select[name='countryCode']").select2().empty();
					tbody.find("select[name='countryCode']").select2().val("");

					var flag = false;
					if(countryStr != null && countryStr.length >0){
						countryArgs=countryStr.split(',');
						$(countryArgs).each(function(i,data){
							if(data == country){
								flag = true;
							}
							tbody.find("select[name='countryCode']").select2().append("<option value='"+data+"'>"+countryMap[data]+"</option>");
						});
					}
					if(!flag){
						tbody.find("select[name='countryCode']").select2().append("<option value='"+country+"'>"+countryMap[country]+"</option>");
					}
					tbody.find("select[name='countryCode']").val(country).trigger("change");
				} else {
					tbody.find("select[name='countryCode']").select2("data",[]);
					tbody.find("select[name='countryCode']").select2().empty();
					tbody.find("select[name='countryCode']").select2().val("");
					var countryStr = productArgs[productVal].platform;
					var flag = false;
					countryArgs=countryStr.split(',');
					$(countryArgs).each(function(i,data){
						if(data == country){
							flag = true;
						}
						tbody.find("select[name='countryCode']").select2().append("<option value='"+data+"'>"+countryMap[data]+"</option>");
					});
					if(!flag){
						tbody.find("select[name='countryCode']").select2().append("<option value='"+country+"'>"+countryMap[country]+"</option>");
					}
					tbody.find("select[name='countryCode']").val(country).trigger("change");
				}
			});
			
			$("#inputForm").on("change",".colors",function(e){
				var $this = $(this);
				var selectColor = $(this).val();
				
				if(selectColor != "" && selectColor != "No color"){
					var tbody = $this.parent().parent().parent();
					var productVal = tbody.find("select[name='product.id']").select2().val();
					var colorPlatformStr = productArgs[productVal].colorPlatform;
					var countryStr = "";
					var colorPlatform = colorPlatformStr.split(';');
					if(colorPlatform.length > 0){
						for(var j=0;j<colorPlatform.length;j++){
							if(selectColor == colorPlatform[j].split('_')[0]){
								countryStr = colorPlatform[j].split('_')[1];
								break;
							}
						}
						tbody.find("select[name='countryCode']").select2("data",[]);
						tbody.find("select[name='countryCode']").select2().empty();
						tbody.find("select[name='countryCode']").select2().text("");
						
						if(countryStr.length > 0){
							countryArgs=countryStr.split(',');
							$(countryArgs).each(function(i,data){
								tbody.find("select[name='countryCode']").select2().append("<option value='"+data+"'>"+countryMap[data]+"</option>");
							});
						}
					}
				}
			}); 
			
			
			$("#inputForm").on("change",".productId",function(){
				var  tr = $(this).parent().parent().parent();
				tr.find("select[name='colorCode']").select2("data",[]);
				tr.find("select[name='countryCode']").select2("data",[]);
				tr.find("select[name='colorCode']").select2().empty();
				tr.find("select[name='countryCode']").select2().empty();
				var productVal  = $(this).val();
	            
	            var deliveryDate = receivedMap[productVal];
		    	tr.find("input[name='deliveryDate']").val(deliveryDate);
		    	tr.find("input[name='actualDeliveryDate']").val(deliveryDate);
				//获取选中的text
				var productName = $(this).children('option:selected').text();
			    tr.find("input[name='productName']").val(productName);
				tr.find("input[name='product.id']").val(productVal);
				colorsStr=productArgs[productVal].color;
				countryStr=productArgs[productVal].platform;
				
				if(colorsStr==null||colorsStr==""){
					tr.find("select[name='colorCode']").select2().append("<option value='No color'>No color</option>").select2("val","No color");
				}else{
					colorArgs=colorsStr.split(',');
					$(colorArgs).each(function(i,data){
			   		  tr.find("select[name='colorCode']").select2().append("<option value='"+data+"' >"+data+"</option>");
					});
				}
				countryArgs=countryStr.split(',');
				$(countryArgs).each(function(i,data){
					 tr.find("select[name='countryCode']").select2().append("<option value='"+data+"' >"+countryMap[data]+"</option>");
				});  
				
				tr.find("select[name='countryCode']").change();
				tr.find("select[name='colorCode']").change();    
				//tr.find(".zhuangxiangno").val(productArgs[productVal].packQuantity);
				
				tr.find("input[name='itemPrice']").val(poPrice[productVal]);
			});
			
		
			
			$("#inputForm").on("change","select[name='countryCode']",function(e){
				var tr = $(this).parent().parent().parent();
				//填充装箱数
			  	var packQuantity=1;
				var country = $(this).val();               
				var productId = tr.find("select[name='product.id']").children("option:selected").val();
				var product=productArgs[productId];
			  	if("217"==productId){
			  		if("com,uk,jp,ca,mx".indexOf(country)<0){
			  			packQuantity=60;
			  		}else{
			  			packQuantity=44;
			  		}
			  	}else if("218"==productId){
			  		if("com,jp,ca,mx".indexOf(country)<0){
			  			packQuantity=32;
			  		}else{
			  			packQuantity=24;
			  		}
			  	}else{
			  		packQuantity=product.packQuantity;
			  	}
	            tr.find(".zhuangxiangno").val(packQuantity);
			})
			
			
			
		$('#add-row').on('click', function(e){
		    e.preventDefault();
		    var table = $("#contentTable");
           	var tbody =$("<tbody></tbody>");
            var tr = $("<tr></tr>");
            tr.append("<td> <input type='hidden' name='productName'/> <input type='hidden' name='itemPrice'/><input type='hidden' class='itemId' value='noItemId'> <select style='width: 90%' class='productId'  name='product.id' ><c:forEach items='${products}' var='product' varStatus='i'><option value='${product.id}'>${product.name}</option>	</c:forEach></select> </td>");
            tr.append("<td> <input type='text' readonly='readonly' class='zhuangxiangno' maxlength='11' style='width:90%' /></td>");
            tr.append("<td> <input style='width:90%' readonly type='text' name='deliveryDate'   id='deliveryDate' /></td>");
            tr.append("<td> <input  style='width:90%' type='text'  class='Wdate'  name='actualDeliveryDate'  pattern='yyyy-MM-dd' /></td>");
            tr.append("<td> <span id='masters'><select name='colorCode'   class='multiSelect colors' style='width:90%'/></span></td>");
            tr.append("<td> <span id='masters'><select name='countryCode' class='multiSelect' style='width:90%'/></span></td>");
            tr.append("<td> <input type='text' maxlength='11' style='width:90%'  readonly /></td>");
            tr.append("<td> <input type='text' maxlength='11' style='width:90%'  readonly /></td>");
            tr.append("<td> <input type='text' maxlength='11' style='width:90%'  name='quantityBalance' class='number' /></td>");
            tr.append("<td> <input type='text' maxlength='11' style='width:90%'  name='quantityOffBalance' class='number' value='0' /></td>");
            tr.append("<td><select name='remark' class='remark' style='width:90%'><option value=''></option><option value='新品，第一次下单'>新品，第一次下单</option><option value='付款延后，推迟出货'>付款延后，推迟出货</option><option value='订单暂缓，推迟出货'>订单暂缓，推迟出货</option><option value='包材延误，推迟出货'>包材延误，推迟出货</option><option value='配件交期延误，推迟出货'>配件交期延误，推迟出货</option><option value='恰逢法定假期，推迟出货'>恰逢法定假期，推迟出货</option><option value='等待运输，推迟出货'>等待运输，推迟出货</option><option value='供应商出货延后，原材缺少'>供应商出货延后，原材缺少</option><option value='供应商出货延后，模具问题'>供应商出货延后，模具问题</option><option value='供应商出货延后，芯片问题'>供应商出货延后，芯片问题</option><option value='供应商出货延后，生产交期跟不上'>供应商出货延后，生产交期跟不上</option><option value='调节市场分配'>调节市场分配</option><option value='更新预计交期'>更新预计交期</option><option value='供应商备货，提前出货'>供应商备货，提前出货</option><option value='manutal'>手动填写</option></select><input name='remark'  type='text' style='margin-top:5px;display: none;'/></td>");
            tr.append("<td><div style='float:left'><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除</a></td>");
           
            tr.find("select[name='product.id']").select2();
            tr.find("select[name='remark']").select2();
            tbody.append(tr);
            
            var productId=tr.find("select[name='product.id']").val();
            
            var deliveryDate = receivedMap[productId];
	    	tr.find("input[name='deliveryDate']").val(deliveryDate);
	    	tr.find("input[name='actualDeliveryDate']").val(deliveryDate);
          //获取第一个tr 第一个td的select值 
		    product=productArgs[productId];
            
		    var productName=$(tbody[0]).find("tr:first>td:first>select").children('option:selected').text();
		    tr.find("input[name='productName']").val(productName);
            
		    tr.find("input[name='itemPrice']").val(poPrice[productId]);
            
            var colorStr=product.color;
            if(colorStr==null||colorStr==""){
				tr.find("select[name='colorCode']").select2().append("<option value='No color'>No color</option>").select2("val","No color");
			}else{
				$(product.color.split(",")).each(function(i,data){
				   tr.find("select[name='colorCode']").select2().append("<option value='"+data+"' >"+data+"</option>");
				 }); 
			}
		  
			$(product.platform.split(",")).each(function(i,data){
				tr.find("select[name='countryCode']").select2().append("<option value='"+data+"' >"+countryMap[data]+"</option>");
			});  
			
			
			//填充装箱数
		  	var packQuantity=1;
			var country = tr.find("select[name='countryCode']").children("option:selected").val();
		  	if("217"==productId){
		  		if("com,uk,jp,ca,mx".indexOf(country)<0){
		  			packQuantity=60;
		  		}else{
		  			packQuantity=44;
		  		}
		  	}else if("218"==productId){
		  		if("com,jp,ca,mx".indexOf(country)<0){
		  			packQuantity=32;
		  		}else{
		  			packQuantity=24;
		  		}
		  	}else{
		  		packQuantity=product.packQuantity;
		  	}
		  	tr.find(".zhuangxiangno").val(packQuantity);
			tr.find("select[name='colorCode']").change();
            $(table).append(tbody);
            
            
			});
			
			$('#contentTable').on('click', '.remove-row', function(e){
				  e.preventDefault();
				  if($('#contentTable tbody').size()>1){
					  var tbody = $(this).parent().parent().parent();
					  tbody.remove();
				  }
			});
			
		
			$(".remark").change(function(){
				if($(this).val()=='manutal'){
					$(this).parent().find("input[type='text']").show();
				}else{
					$(this).parent().find("input[type='text']").hide();
				}
			});
			
			$(".Wdate").live("click", function (){
			 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$("#inputForm").validate({
				rules:{
					"quantityBalance":{
						"required":true,
					},
					"quantityOffBalance":{
						"required":true,
					}
				},
				messages:{
					"quantityBalance":{"required":'数量不能为空'},
					"quantityOffBalance":{"required":'线下数量不能为空'}
				},
				submitHandler: function(form){
					var keyStr="";
					var twoStr="";
					var flagInt=1;
					var flagNums =1;
					var flag = 1;
					var noZeroFlag=1;
					var off=1;
					
					$("#contentTable tbody tr").each(function(){
					var curkeyStr=$(this).find("input[name='productName']").val()+$(this).find("select[name='colorCode']").val()+$(this).find("select[name='countryCode']").val();
					if(flag==1){
						if(keyStr){
							if(keyStr.indexOf(curkeyStr+",")>=0){
								twoStr=curkeyStr;
								flag = 2;
							}else{
								keyStr=keyStr+curkeyStr+",";
							};
						}else{
							keyStr = curkeyStr+",";
						};						
					};
				});
				
				if(flag==2){
					top.$.jBox.tip("相同产品颜色国家只能有一条记录 ！"+twoStr, 'info',{timeout:3000});
					return false;
				}
					
				var curTotalQuantity=0;
				var totalAmount=0;
				var totalQuantity=0;
				
					$("input[name='quantityBalance']").each(function(e){
						var tr = $(this).parent().parent();
						var productId=tr.find("select.productId").val();
						var packingQuantity = tr.find(".zhuangxiangno").val();
						var oldBlance = tr.find(".quantityBalance").val();
						var quantityOrdered = tr.find("input[name='quantityOrdered']").val();
						//控制不让订单数为0
						if((oldBlance==quantityOrdered)&$(this).val()==0){
							twoStr=tr.find("input[name='productName']").val()+","+tr.find("select[name='colorCode']").val()+","+tr.find("select[name='countryCode']").val();
							noZeroFlag=2;
							return false;
						}
						
						
						if($(this).val()!='0'){
							var a=$(this).val()%packingQuantity;
							if(a!=0){
								twoStr=tr.find("input[name='productName']").val()+","+tr.find("select[name='colorCode']").val()+","+tr.find("select[name='countryCode']").val();
								if(flagNums==1){
									flagNums=2;
								}
							};
						}
						
						if((tr.find("input[name='quantityOffBalance']").val()>tr.find("input[name='quantityBalance']").val())||(tr.find("input[name='quantityOffBalance']").val()>tr.find(".quantityOffBalance").val())){
							twoStr=tr.find("input[name='productName']").val()+","+tr.find("select[name='colorCode']").val()+","+tr.find("select[name='countryCode']").val();
							off=2;
							return false; 
						}
						
						totalQuantity=parseInt(totalQuantity)+parseInt($(this).val());
					});
					
					if(noZeroFlag==2){
						top.$.jBox.tip("订单数量不能为0,请删除该项!!! "+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					
					if(off==2){
						top.$.jBox.tip("未收货线下数不能大于未收货总数，未收货线下数不能大于线下可收货数！"+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					
					if(totalQuantity!=$(".itemsQuantity").val()){
						top.$.jBox.tip("产品平台调整,balance总数必须相等！"+twoStr, 'info',{timeout:3000});
						return false;
					}
					if(flagNums==2){
						top.$.jBox.confirm('装箱数不为整数被,确定要提交吗', '提示', function (v, h, f) {
				            if (v == 'ok') {
				            	$("select[name='colorCode']").attr("disabled",false);
								$("select[name='countryCode']").attr("disabled",false);
								
								$("#contentTable tbody tr").each(function(i,j){
									$(j).find("select").each(function(){
										if($(this).attr("name")&&$(this).val()!='manutal'){
											$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
										}
									});
									
									$(j).find("input[type!='']").each(function(){
										if($(this).attr("name")&&$(this).css("display")!='none'){
											$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
										}
									});
								});
								
								$("option[value='No color']").each(function(){
									$(this).val("");
								});
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
				            }
					  });
					}else{
						$("select[name='colorCode']").attr("disabled",false);
						$("select[name='countryCode']").attr("disabled",false);
						
						$("#contentTable tbody tr").each(function(i,j){
							$(j).find("select").each(function(){
								if($(this).attr("name")&&$(this).val()!='manutal'){
									$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
								}
							});
							
							$(j).find("input[type!='']").each(function(){
								if($(this).attr("name")&&$(this).css("display")!='none'){
									$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
								}
							});
						});
						
						$("option[value='No color']").each(function(){
							$(this).val("");
						});
						form.submit();
						$("#btnSubmit").attr("disabled","disabled");
					}
					
					
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
				}
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/purchaseOrder/">采购订单列表</a></li>
		<li class="active"><a href="#">采购订单编辑</a></li>
	</ul>
	<br/>
	<form:form id="inputForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/editSave" method="post" class="form-horizontal">
		<input type="hidden" name="id" 					value="${purchaseOrder.id}" />
		<input type="hidden" name="orderNo" 			value="${purchaseOrder.orderNo}" />
	    <input type="hidden" name="oldItemIds" 			value="${purchaseOrder.oldItemIds}" />
	    <input type="hidden" name="delFlag" 			value="${purchaseOrder.delFlag}" />
	    <input type="hidden" name="createUser.id" 		value="${purchaseOrder.createUser.id}" />
	    <input type="hidden" name="totalAmount"		 	value="${purchaseOrder.totalAmount}" />
	    <input type="hidden" name="orderSta"		 	value="${purchaseOrder.orderSta}" />
	    <input type="hidden" name="supplier.id" 		value="${purchaseOrder.supplier.id}" />
	    
	    <input type="hidden" name="sureUser.id"			value="${purchaseOrder.sureUser.id}" />
	    <input type="hidden" name="sureDate" 			value="<fmt:formatDate pattern='yyyy-MM-dd' value='${purchaseOrder.sureDate}'/>" />
	    
	    <input type="hidden" name="depositAmount" 		value="${purchaseOrder.depositAmount}" />
	    <input type="hidden" name="depositPreAmount" 	value="${purchaseOrder.depositPreAmount}" />
	    <input type="hidden" name="piFilePath" 	        value="${purchaseOrder.piFilePath}" />
	    
	    <input type="hidden" name="paymentAmount" 	    value="${purchaseOrder.paymentAmount}" />
	    <input type="hidden" name="paySta" 	            value="${purchaseOrder.paySta}" />
	    <input type="hidden" name="sendEmailFlag"       value="${purchaseOrder.sendEmailFlag}"/>
	    <input type="hidden" name="oldDeposit"       	value="${purchaseOrder.oldDeposit}"/>
	    
	    <input type="hidden" class="itemsQuantity"      value="${purchaseOrder.itemsQuantityCanReceived}"/>
	    
	    
	    <input type="hidden" class="isPaymentDeposit"   value="${purchaseOrder.depositAmount+purchaseOrder.depositPreAmount+purchaseOrder.paymentAmount>0?'1':'0'}"/>
	    
	    <input type="hidden" name="receiveFinishedDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${purchaseOrder.receiveFinishedDate}'/>" />
	    
	    <input type="hidden" name="toReview" 			value="${purchaseOrder.toReview}" />
	    <input type="hidden" name="toPartsOrder" 		value="${purchaseOrder.toPartsOrder}" />
	    
	    <input type="hidden" name="isOverInventory" 	value="${purchaseOrder.isOverInventory}" />
	    <input type="hidden" name="overRemark" 		    value="${purchaseOrder.overRemark}" />
	     
	    <input type="hidden" name="receivedStore" 		value="${purchaseOrder.receivedStore}" />
	    <input type="hidden" name="offlineSta" 		    value="${purchaseOrder.offlineSta}" />
	    <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label"><b>供应商</b>:</label>
				<div class="controls" >
				<input type="text" readonly  style="width:60%"   value="${purchaseOrder.supplier.nikename}"/>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>定金</b>:</label>
				<div class="controls" >
					<div class="input-prepend input-append">
						<input  type="text" class="number required" ${purchaseOrder.canEditDeposit eq '1' ? '':'readonly' } style="width:40%" name="deposit" value="${purchaseOrder.deposit}" /><span class="add-on">%</span>
					</div>
				</div>
			</div>
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<label class="control-label"><b>线下订单</b>:</label>
				<div class="controls">
					${purchaseOrder.offlineSta eq '0' ?'不包含':'包含'}
				</div>
			</div>
			
			
		</div>	
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>跟单员</b>:</label>
				<div class="controls">
					<select style="width: 70%" id="merchandiser" class='merchandiser' name="merchandiser.id" >
					<c:forEach items="${users}" var="user" varStatus="i">
							 <option value='${user.id}' ${user.id eq purchaseOrder.merchandiser.id?'selected':'' }>${user.name}</option>;
					</c:forEach>
				</select>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>下单日期</b>:</label>
				<div class="controls">
					<input  type="text" readonly="readonly"   style="width:60%" name="purchaseDate"  value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd" />" />
				</div>
			</div>
			
			
			
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<label class="control-label"><b>货币类型</b>:</label>
				<div class="controls">
				<input  type="text" readonly="readonly" name="currencyType" value="${purchaseOrder.currencyType}"/>
				</div>
			</div>
			
		</div>
		<c:if test="${not empty purchaseOrder.modifyMemo}">
			<div style="float:left;width:100%;display:inline;">
				<div class="control-group" style="float:left;width:100%" >
					<label class="control-label"><b>上次修改原由</b>:</label>
					<div class="controls">
						${purchaseOrder.modifyMemo}
					</div>
				</div>
			</div>
		</c:if>
		
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:100%" >
				<label class="control-label"><b>修改原由</b>:</label>
				<div class="controls">
					<textarea name="modifyMemo"  rows="4" cols="4" maxlength="255" style="width:90%" required="required"  ></textarea>
				</div>
			</div>
		</div>
			
		<div style="float:left;width:100%">
		 <blockquote>
			 <div style="float: left"><p style="font-size: 14px">产品信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加产品</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 15%">产品名称</th>
				   <th style="width: 5%">装箱数量</th>
				   <th style="width: 8%">PO交期</th>
				   <th style="width: 10%">预计交期</th>
				   <th style="width: 8%">颜色</th>
				   <th style="width: 10%">国家</th>
				   <th style="width: 6%">PO总数</th>
				   <th style="width: 6%">线下数</th>
				   <th style="width: 6%">总未收货</th>
				   <th style="width: 6%">线下未收货</th>
				   <th style="width: 15%">备注</th>
				   <th style="width: 5%">操作</th>
				   
			</tr>
		</thead>
		<c:if test="${not empty purchaseOrder.items}" >
			<c:forEach items="${purchaseOrder.items}" var="item" >
				<tbody>
					<tr>
						<td>
						<select style="width: 90%" id="product" class='productId' name="product.id" ${item.preAndReceivedQuantity>0 ?'disabled':''}>
							<c:forEach items="${products}" var="pro" varStatus="i">
									 <option value='${pro.id}'  ${pro.id eq item.product.id ?'selected':''}>${pro.name}</option>;
							</c:forEach>
						</select>
						<input type="hidden" name="itemColor" value="${item.colorCode}"/>
						<input type="hidden" name="itemCountry" value="${item.countryCode}"/>
						<input type="hidden" name="productName" value="${item.productName}"/>
						<input type="hidden" name="product.id"  value="${item.product.id}"/>
						<input type="hidden" name="itemPrice"   value="${item.itemPrice}"/>
						<input type="hidden" name="forecastItemId" value="${item.forecastItemId}" />
						<input type="hidden" name="forecastRemark" value="${item.forecastRemark}" />
						</td>
						<td>
						<c:choose>
							<c:when test="${item.product.id eq '217'}">
								<c:choose>
									<c:when test="${fn:contains('com,uk,jp,ca,mx',item.countryCode)}"><c:set value="60" var="productPackQuantity"/></c:when>
									<c:otherwise><c:set value="44" var="productPackQuantity"/></c:otherwise>
								</c:choose> 
							</c:when>
							<c:when test="${item.product.id eq '218'}">
								<c:choose>
									<c:when test="${fn:contains('com,jp,ca,mx',item.countryCode)}"><c:set value="32" var="productPackQuantity"/></c:when>
									<c:otherwise><c:set value="24" var="productPackQuantity"/></c:otherwise>
								</c:choose> 
							</c:when>
							<c:otherwise><c:set value="${item.product.packQuantity}" var="productPackQuantity"/></c:otherwise>
						</c:choose>
						<input type="text" readonly="readonly" maxlength="11" style="width: 80%" class="zhuangxiangno" value="${productPackQuantity}"/>
						
						</td>
						<td><input style="width:90%"  readonly="readonly"  type="text"   name="deliveryDate"        value="<fmt:formatDate value="${item.deliveryDate}" pattern="yyyy-MM-dd"/>"/> </td>
						<td><input style="width:90%"  readonly="readonly"  type="text"   name="actualDeliveryDate"  value="<fmt:formatDate value="${item.actualDeliveryDate}" pattern="yyyy-MM-dd"/>"/> </td>
						<td><span id="masters">
						<select name="colorCode" id="colorCode"  class="multiSelect colors" style="width:90%" ${item.preAndReceivedQuantity>0 ?'disabled':''}>
							<c:if test="${fn:length(item.colorList)>0}">
								<c:forEach items="${item.colorList}" var="color" varStatus="i">
										 <option value='${color}'  ${color eq item.colorCode ?'selected':''}>${color}</option>;
								</c:forEach>
							</c:if>
							<c:if test="${fn:length(item.colorList)==0}">
								 <option value='No color'  selected>No color</option>;
							</c:if>
						</select>
						</span></td>
						<td><span id="masters">
						 <select name="countryCode" id="countryCode"  ${item.preAndReceivedQuantity>0 ?'disabled':''} style="width: 90%">
						<c:forEach items="${item.countryList}" var="country" varStatus="i">
									 <option value='${country}'  ${country eq item.countryCode ?'selected':''}>${fns:getDictLabel(country, 'platform', '')}</option>;
							</c:forEach>
						</select>
						</span></td>
						<td>
						<input type="hidden" name="id"  value="${item.id}" />
						<input type='hidden' class="itemId" value="itemId"/>
						<input type="hidden" name="quantityPreReceived" value="${item.quantityPreReceived}" />
						<input type="hidden" name="quantityReceived" 	value="${item.quantityReceived}" />
						<input type="hidden" name="quantityOffPreReceived" value="${item.quantityOffPreReceived}" />
						<input type="hidden" name="quantityOffReceived" 	value="${item.quantityOffReceived}" />
						<input type="hidden" name="quantityPayment" 	value="${item.quantityPayment}" />
						<input type="hidden" name="paymentAmount" value="${item.paymentAmount}" />
						<input type="hidden" name="updateDate" value="<fmt:formatDate value="${item.updateDate}" pattern="yyyy-MM-dd" />"/>
						<input type="hidden" name="deliveryDateLog" value="<fmt:formatDate value="${item.deliveryDateLog}" pattern="yyyy-MM-dd" />" />
						<input type="hidden" class="preAndReceivedQuantity" value ="${item.preAndReceivedQuantity}" />
						<input type="text" maxlength="11" style="width: 80%" readonly="readonly" name="quantityOrdered" value="${item.quantityOrdered}"/>
						</td>
						<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" name="quantityOffOrdered" value="${item.quantityOffOrdered}"/></td>
						<td><input type="hidden" class="quantityBalance" value="${item.quantityCanReceived}" /><input type="text" maxlength="11" style="width: 80%" name="quantityBalance" class="number" value="${item.quantityCanReceived}"/></td>
						<td><input type="hidden" class="quantityOffBalance" value="${item.quantityOffCanReceived}" /><input type="text" maxlength="11" style="width: 80%" name="quantityOffBalance" class="number" value="${item.quantityOffCanReceived}"/></td>
						<td>
						   <%--  <input type="text" maxlength="200" style="width: 80%" name="remark" value="${item.remark}"/> --%>
						    <select name='remark' class="remark" style="width: 90%">
						        <option value='' ></option>
								<option value='新品，第一次下单'>新品，第一次下单</option>
								<option value='付款延后，推迟出货'>付款延后，推迟出货</option>
								<option value='订单暂缓，推迟出货'>订单暂缓，推迟出货</option>
								<option value='包材延误，推迟出货'>包材延误，推迟出货</option>
								<option value='配件交期延误，推迟出货'>配件交期延误，推迟出货</option>
								<option value='恰逢法定假期，推迟出货'>恰逢法定假期，推迟出货</option>
								<option value='等待运输，推迟出货'>等待运输，推迟出货</option>
								<option value='供应商出货延后，原材缺少'>供应商出货延后，原材缺少</option>
								<option value='供应商出货延后，模具问题'>供应商出货延后，模具问题</option>
								<option value='供应商出货延后，芯片问题'>供应商出货延后，芯片问题</option>
								<option value='供应商出货延后，生产交期跟不上'>供应商出货延后，生产交期跟不上</option>
								<option value='调节市场分配'>调节市场分配</option>
								<option value='更新预计交期'>更新预计交期</option>
								<option value='供应商备货，提前出货'>供应商备货，提前出货</option>
								<option value='manutal' ${not empty item.remark?'selected':'' }>手动填写</option>
						    </select>
						    <input name="remark"  type="text" style="margin-top:5px;${not empty item.remark?'':'display: none;' }" value="${item.remark }"/>
						 </td>
						<td>
						<c:if test="${item.preAndReceivedQuantity==0}"><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span>删除</a></c:if>
						</td>
					</tr>
				</tbody>
			</c:forEach>
		</c:if>
		
	</table>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
