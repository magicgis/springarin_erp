<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单管理</title>
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
			eval('var inventorySkuMap = ${inventorySkuMap}');
			eval('var skuQuantityMap  = ${skuQuantityMap}');
			eval('var bangdingSkus   = ${bangdingSkus}');
			
			
			eval('var toCountryMap =${toCountryMap}');
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			var countryMap = {};
			<c:forEach items="${fns:getDictList('platform')}" var="dic">
				countryMap['${dic.value}'] = '${dic.label}';
			</c:forEach>
			
			
			var productArgs ={};
			var colorArgs =[];
			var countryArgs=[];
			var  colorsStr;
			var  countryStr;
			var  product;
			<c:forEach items="${products}" var="product" varStatus="i">
			  	product={};
			  	product.id='${product.id}';
			  	product.color='${product.color}';
			  	product.name='${product.name}';
			  	product.platform ='${product.platform}';
			  	productArgs['${product.id}']=product;
			</c:forEach>
			
			var model =$("#model").val();

			$("#transportType").change(function(){
				var type=$(this).val();
				var opts="";
				if(type=='0'){
					//<c:forEach items="${toStock}" var="stock">
					//  <c:if test='${stock.type eq 0}'>
							opts=opts+"<option value=${stock.id}>${stock.stockSign}</option>"
					//  </c:if>
					//</c:forEach>	
					$("#toStore").select2().empty();
					$("#toStore").select2().select2("data",[]);
					$("#toStore").append(opts).select2();
					$("#toStoreDiv").css("display","block");
					$("#destinationDetailDiv").css("display","none");
					$("#unlineDiv").css("display","none");
					$("#supplierDiv").css("display","none");
					$("#billNoDiv").css("display","none");
					var table =$("#contentTable");
		             var tbody =table.find("tbody");
			    	tbody.find("tr").each(function(){
						 $(this).remove();
					});
			    	$("#add-row").click();
			    	$("#add-flag").css("display","block");
				}else if(type=='1'){
					//<c:forEach items="${toStock}" var="stock">
					//  <c:if test='${stock.type eq 1}'>
							opts=opts+"<option value=${stock.id}>${stock.stockSign}</option>"
					//  </c:if>
					//</c:forEach>
					$("#toStore").select2().empty();
					$("#toStore").select2().select2("data",[]);
					$("#toStore").append(opts).select2();
					$("#toStoreDiv").css("display","block");
					$("#destinationDetailDiv").css("display","none");
					$("#unlineDiv").css("display","none");
					$("#supplierDiv").css("display","none");
					$("#billNoDiv").css("display","none");
					var table =$("#contentTable");
		             var tbody =table.find("tbody");
			    	tbody.find("tr").each(function(){
						 $(this).remove();
					});
					$("#add-row").click();
					$("#add-flag").css("display","block");
				}else if(type=='2'){
					$("#toStoreDiv").css("display","none");
					$("#destinationDetailDiv").css("display","block");
					$("#unlineDiv").css("display","none");
					$("#supplierDiv").css("display","none");
					$("#billNoDiv").css("display","none");
					var table =$("#contentTable");
		             var tbody =table.find("tbody");
			    	tbody.find("tr").each(function(){
						 $(this).remove();
					});
					$("#add-row").click();
					$("#add-flag").css("display","block");
				}else if(type=='3'){
					$("#toStoreDiv").css("display","none");
					$("#destinationDetailDiv").css("display","block");
					$("#unlineDiv").css("display","block");
					$("#unlineOrder").change();
					$("#add-flag").css("display","none");
					$("#supplierDiv").css("display","block");
					$("#billNoDiv").css("display","block");
				}
				
				
			});
			
			
			$("#transportType").change();   
			 
			
			
			$(".productQuantity").live("blur",function(){
					var tr =$(this).parent().parent();
					if(tr.find(".productQuantity").val()){
						var curBoxNo=toDecimal(parseInt(tr.find(".productQuantity").val())/parseInt(tr.find(".packQuantity").val()));
						tr.find(".boxNo").val(curBoxNo);
					}
					
					var tbody=tr.parent();
					var boxNumber =0;
					tbody.find("tr").each(function(){
						if($(this).find(".packQuantity").val()!=''&&$(this).find(".productQuantity").val()!=''){
							var singBoxNo=(parseInt($(this).find(".productQuantity").val())/parseInt($(this).find(".packQuantity").val()));
							var re = /^[0-9]*[1-9][0-9]*$/ ;
							if(!re.test(singBoxNo)){
								singBoxNo=parseInt(singBoxNo+1);
							}
							boxNumber = boxNumber+singBoxNo;
						};
					});
					
					$("input[name='boxNumber']").val(boxNumber);
				})
				
				$(".packQuantity").live("blur",function(){
					var tr =$(this).parent().parent();
					if(tr.find(".productQuantity").val()){
						var curBoxNo=toDecimal(parseInt(tr.find(".productQuantity").val())/parseInt(tr.find(".packQuantity").val()));
						tr.find(".boxNo").val(curBoxNo);
					}
					
					var tbody=tr.parent();
					var boxNumber =0;
					tbody.find("tr").each(function(){
						if($(this).find(".packQuantity").val()!=''&&$(this).find(".productQuantity").val()!=''){
							var singBoxNo=(parseInt($(this).find(".productQuantity").val())/parseInt($(this).find(".packQuantity").val()));
							var re = /^[0-9]*[1-9][0-9]*$/ ;
							if(!re.test(singBoxNo)){
								singBoxNo=parseInt(singBoxNo+1);
							}
							boxNumber = boxNumber+singBoxNo;
						};
					});
					
					$("input[name='boxNumber']").val(boxNumber);
				})
			
			
			
			$("#inputForm").on("change",".countryCode",function(){
				var  tr = $(this).parent().parent();
				tr.find("select[name='colorCode']").select2().change();
				  
				
				//填充装箱数
				var productId = tr.find("select.productId").children("option:selected").val();
				var country = tr.find("select[name='countryCode']").children("option:selected").val();
				var color = tr.find("select[name='colorCode']").children("option:selected").val();
				
				var res=ajaxGetPrice(productId,country,color);
				var packQuantity=1;
					
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
			  		packQuantity=res.packQuantity;
			  	}
	            tr.find(".packQuantity").val(packQuantity);
	            tr.find(".price").val(res.price);
				tr.find(".currency").val(res.currency);
				tr.find(".hasElectric").html(res.hasElectric=='1'?'<span style="color:red">是</span>':'否');
			});
				
			$("#inputForm").on("change",".colorCode",function(){
			 var  tr = $(this).parent().parent();
				//ajax  获取UnitPrice
			  var productId=tr.find("select.productId").val();
			  var countryCode=tr.find("select.countryCode").val();
			  var colorCode=$(this).val();
			 // var temp=ajaxGetPrice(productId,countryCode,colorCode);
			 // tr.find(".price").val(temp.price);
			  tr.find("select.sku").select2("data",[]);
			  tr.find("select.sku").select2().empty();
			  var conKey = productId+","+countryCode+","+colorCode;
			  if(inventorySkuMap[conKey]){
				  var opts="";
				  tr.find("select.sku").removeAttr("disabled");
				  if(inventorySkuMap[conKey].length==1){
					  tr.find("select.sku").attr("disabled","disabled");
				  }
				  for(var i in inventorySkuMap[conKey]){
					  var sku = inventorySkuMap[conKey][i];
					  if($.inArray(sku, bangdingSkus)!=-1){
						  opts=opts+"<option value='"+sku+"' selected >"+skuQuantityMap[sku]+"绑定</option>";
					  }else{
						  opts=opts+"<option value='"+sku+"' >"+skuQuantityMap[sku]+"</option>";
					  }
				  }
				  tr.find("select.sku").append(opts).change();
			  }else{
				  tr.find("select.sku").select2().empty();
			  }
			});
				
				
			$("#inputForm").on("change",".productId",function(){
				var  tr = $(this).parent().parent();
				tr.find("select[name='colorCode']").select2("data",[]);
				tr.find("select[name='colorCode']").select2().empty();
				tr.find("select[name='countryCode']").select2("data",[]);
				tr.find("select[name='countryCode']").select2().empty();
				var productVal  = $(this).val();
				//获取选中的text
				var productName = $(this).children('option:selected').text();
			    tr.find("input[name='productName']").val(productName);
				colorsStr=productArgs[productVal].color;   
				countryStr=productArgs[productVal].platform;
				
				if(colorsStr==null||colorsStr==""){
					tr.find("select[name='colorCode']").select2().append("<option value='' selected>No color</option>");
				}else{
					colorArgs=colorsStr.split(',');
					if(colorArgs.length==1){
						 tr.find("select[name='colorCode']").select2().append("<option value='"+colorArgs[0]+"' >"+colorArgs[0]+"</option>").select2("val",colorArgs[0]);
					}else{
						$(colorArgs).each(function(i,data){
				   		  tr.find("select[name='colorCode']").select2().append("<option value='"+data+"' >"+data+"</option>");
						});
					}
					
				}
				
				
				var toCountry=toCountryMap[$("#toStore").val()];
				if(toCountry=='com'||toCountry=='ca'||toCountry=='jp'){
			 		tr.find("select[name='countryCode']").select2().append("<option value='"+toCountry+"' >"+countryMap[toCountry]+"</option>").select2("val",countryMap[toCountry]);
				}else{
					countryArgs=countryStr.split(',');
					$(countryArgs).each(function(i,data){
						 tr.find("select[name='countryCode']").select2().append("<option value='"+data+"' >"+countryMap[data]+"</option>");
					});
				}
				 
				 tr.find("select[name='countryCode']").select2().change();
				
				tr.find(".productQuantity").val("").focus();
				tr.find(".boxNo").val("");
				//ajax  获取UnitPrice
				  var productId=$(this).val();
				  var countryCode=tr.find("select[name='countryCode']").val();
				  var colorCode=tr.find("select[name='colorCode']").val();
				//  var res=ajaxGetPrice(productId,countryCode,colorCode);
				//  tr.find(".packQuantity").val(res.packQuantity);
				//  tr.find(".price").val(res.price);
				//  tr.find(".currency").val(res.currency);
				//  tr.find(".hasElectric").html(res.hasElectric=='1'?'<span style="color:red">是</span>':'否');
			});
			
			
			$(".remove-row").live("click",function(){
				 if($('#contentTable tbody tr').size()>1){
					var tr = $(this).parent().parent();
					tr.remove();
				}
			});
			
			$("#add-row").on("click",function(e){
				e.preventDefault();
				var tbody=$("#contentTable tbody");
				var tr=$("<tr></tr>");
				var options = "<option value=''></option>";
				for(var i in productArgs){
					var product = productArgs[i];
					var toCountry=toCountryMap[product.id];
					if(toCountry!=''&&product.platform.indexOf(toCountry)){
						options+="<option value="+product.id+">"+product.name+"</option>";
					}else{
						options+="<option value="+product.id+">"+product.name+"</option>";
					};
				}
				tr.append("<td><select name='product.id' class='productId' style='width:90%'>"+options+"</select><input type='hidden' class='boxVolume'/><input type='hidden' class='boxWeight'/><input type='hidden' name='itemPrice' class='price'/><input type='hidden' name='currency' class='currency'/><input type='hidden' name='productName'/></td>");
				tr.append("<td><select name='countryCode' class='countryCode'  style='width:90%'/></td>");
				tr.append("<td><select name='colorCode'   class='colorCode' style='width:90%'/></td>");
				tr.append("<td><select name='sku'   class='sku' style='width:98%'/></td>");
				tr.append("<td><select name='offlineSta' style='width:100%'><option value='0'>否</option><option value='1'>是</option></select></td>");
				tr.append("<td><input type='text' maxlength='11' style='width: 80%' name='packQuantity' class='number packQuantity'/></td>");
	            tr.append("<td><input type='text' maxlength='11' style='width: 80%'  name='quantity' class='number productQuantity' /></td>");
	            tr.append("<td><input type='text' maxlength='50' style='width: 80%'  class='boxNo' readonly /></td>");
	            tr.append(" <td><input type='text' maxlength='11' style='width: 80%' class='price' name='productPrice' /></td>");
	            tr.append("<td><sapn class='hasElectric'><span></td>");
	            tr.append("<td><input type='text' maxlength='50' style='width: 80%'  name='remark' /></td>");
	            tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>删除</a></td>");
				tbody.append(tr);
				tr.find("select.productId").select2();
				tr.find("select.countryCode").select2();
				tr.find("select.colorCode").select2();
				tr.find("select.sku").select2();
				tr.find("select[name='offlineSta']").select2();
			});
			$("#add-row").click();
			

			 $("#unlineOrder").change(function(){
				 if($("#unlineOrder").val()==''){
					 return;
				 }
				  $.ajax({
				    type: 'post',
				    async:true,
				    url: '${ctx}/amazoninfo/unlineOrder/getAmazonUnlineOrder',
				    data: {
				    	"id":$("#unlineOrder").val()
				    },
				    dataType: 'json',
				    success:function(data){ 
				    	$("#fromStoreId").select2("val",data.sales_channel);
			      		$("#destinationDetail").val(data.addressLine);
			      		if(data.ladingBillNo!='null'&&data.ladingBillNo!=null){
			      			$("#ladingBillNo").val(data.ladingBillNo);
			      		}
			      		if(data.carrier!='null'&&data.carrier!=null){
			      			$("#carrier").val(data.carrier);
			      		}
						
			      		 var table =$("#contentTable");
			             var tbody =table.find("tbody");
				    	tbody.find("tr").each(function(){
							 $(this).remove();
						});
				    	var orderItem=data.items;
				    	for(var i =0;i<orderItem.length;i++){
				    		var item=orderItem[i];
				    		var tr=$("<tr></tr>");
							tr.append("<td><select name='product.id' disabled class='productId1'  style='width:90%' ><option value=''></option><c:forEach items='${products}' var='product'><option value='${product.id}'>${product.name}</option></c:forEach></select><input type='hidden' class='boxVolume'/><input type='hidden' class='boxWeight'/><input type='hidden' name='itemPrice' class='price' /><input type='hidden' name='currency' class='currency'/><input type='hidden' name='productName' /></td>");
							tr.append("<td><select name='countryCode' disabled class='countryCode1'  style='width:90%'/></td>");
							tr.append("<td><input type='text' name='colorCode'   class='colorCode1' style='width:90%' readonly='readonly' /></td>");
							tr.append("<td><input type='text' name='sku'   style='width:90%' readonly /></td>");
							tr.append("<td><select name='offlineSta' disabled style='width:100%'><option value='1'>是</option></select></td>");
							tr.append("<td><input type='text' maxlength='11' style='width: 80%' name='packQuantity'  class='packQuantity'/></td>");
				            tr.append("<td><input type='text' maxlength='11' style='width: 80%'  name='quantity'  class='productQuantity' /></td>");
				            tr.append("<td><input type='text' maxlength='50' style='width: 80%'  class='boxNo1' readonly /></td>");
				            tr.append(" <td><input type='text' maxlength='11' style='width: 80%' class='price' name='productPrice'/></td>");
				            tr.append("<td><sapn class='hasElectric'><span></td>");
				            tr.append("<td><input type='text' maxlength='50' style='width: 80%'  name='remark' /></td>");
				            tr.append("<td></td>");
							tbody.append(tr);
							tr.find("select.productId1").select2();
							var pid=tr.find("select.productId1 option:contains('"+item.productName+"')").val();
							tr.find("select.productId1").select2("val",pid);
							tr.find("select.countryCode1").select2();
							tr.find("input.colorCode1").val(item.color);
							tr.find("input.productQuantity").val(item.quantityOrdered);
							countryStr=productArgs[pid].platform;
							countryArgs=countryStr.split(',');
							$(countryArgs).each(function(i,data){
									 tr.find("select[name='countryCode']").select2().append("<option value='"+data+"' >"+countryMap[data]+"</option>");
							}); 
							tr.find("input[name='sku']").val(item.sku);
							tr.find("select.countryCode1").select2("val",item.country);
						    var countryCode=tr.find("select[name='countryCode']").val();
						    var colorCode=tr.find("select[name='colorCode']").val()=='No color'?'':tr.find("select[name='colorCode']").val();
						    var res=ajaxGetPrice(pid,countryCode,colorCode);
						    tr.find(".price").val(res.price);
						    tr.find(".packQuantity").val(res.packQuantity);
						    tr.find(".currency").val(res.currency);
						    tr.find("input[name='productName']").val(item.productName);
						    tr.find(".hasElectric").html(res.hasElectric=='1'?'<span style="color:red">是</span>':'否');
							if(tr.find(".productQuantity").val()){
								var curBoxNo=toDecimal(parseInt(tr.find(".productQuantity").val())/parseInt(tr.find(".packQuantity").val()));
								tr.find(".boxNo1").val(curBoxNo);
							}
							var tbody=tr.parent();
							var boxNumber =0;
							tbody.find("tr").each(function(){
								if($(this).find(".packQuantity").val()!=''&&$(this).find(".productQuantity").val()!=''){
									var singBoxNo=(parseInt($(this).find(".productQuantity").val())/parseInt($(this).find(".packQuantity").val()));
									var re = /^[0-9]*[1-9][0-9]*$/ ;
									if(!re.test(singBoxNo)){
										singBoxNo=parseInt(singBoxNo+1);
									}
									boxNumber = boxNumber+singBoxNo;
								};
							});
							$("input[name='boxNumber']").val(boxNumber);
				    	}
			        }
			   }); 

			}); 

			
			$("#inputForm").validate({
				rules:{
					"product.id":{
						"required":true,
					},"countryCode":{
						"required":true,
					},"quantity":{
						"required":true,
					}
				},
				messages:{
					"product.id":{"required":"产品不能为空"},
					"countryCode":{"required":"国家不能为空"},
					"quantity":{"required":'数量不能为空'},
				},
				submitHandler: function(form){
					//发货仓库和去向仓库不能为同一个
					if($("#transportType").val()!="3"&& $("select[name='fromStore.id']").val()==$("select[name='toStore.id']").val()){
						top.$.jBox.tip("发货仓库和目的仓库不能相同 ", 'info',{timeout:3000});
						return false;
					}
					
					
					 var keyStr="";
					 var flag = 1;
					 var zeroFlag=1;
					 var twoStr="";
					 
					 var volume=0;
					 var weight=0;
					 var noText=0;
					 
					$("#contentTable tbody tr").each(function(){
						var curkeyStr=$(this).find("select.sku").val()+","+$(this).find("select[name='offlineSta']").val();
						if(keyStr.indexOf(curkeyStr+",")>=0){
							twoStr=curkeyStr;
							flag = 2;
							return false;
						}else{
							keyStr=keyStr+curkeyStr+",";
						};
						if($("input[name='transportSta']").val()=='0'){
							var quantity = $(this).find("input[name='quantity']").val();
							if(quantity==0){
								zeroFlag=2;
								twoStr=curkeyStr;
								return false;
							};
						}
						if($(this).find("select.productId1").children("option:selected").text()==''||$(this).find("select.countryCode1").children("option:selected").text()==''){
							noText=1;
						}
						var rate =$(this).find("input[name='quantity']").val()/$(this).find(".packQuantity").val();
						//var itemVolume=$(this).find(".boxVolume").val();
						//var itemWeight=$(this).find(".boxWeight").val();
						//volume=parseFloat(volume+parseFloat(itemVolume)*rate);
						//weight=parseFloat(weight+parseFloat(itemWeight)*rate);  
					});
					if($("#transportType").val()!="3"){
						if(flag==2){
								top.$.jBox.tip("相同sku,相同线别,只能有一条记录!!!  "+twoStr, 'info',{timeout:3000});
								return false;
						}
					}
					if($("#transportType").val()=="3"){
						 if($("#unlineOrder").val()==''){
							 top.$.jBox.tip("线下订单不能为空!!!  ", 'info',{timeout:3000});
							 return false;
						 }
						if(noText==1){
							top.$.jBox.tip("产品和国家不能为空!!!  ", 'info',{timeout:3000});
							return false;
						}
					}
					
					if(zeroFlag==2){
						top.$.jBox.tip("数量必须大于0!!! "+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					//$("input[name='volume']").val(volume);
					//$("input[name='weight']").val(weight);
					
					
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
					
					$("option[value='No color']").each(function(){
						$(this).val("");
					});
					
					//把所有disable的select放开
					$("select[disabled]").each(function(){
						$(this).removeAttr("disabled");
					});
					if($("#transportType").val()!="3"){
						$("#ladingBillNo").val('');
						$("#carrier").val('');
						$("#unlineOrder").select2("val",'');
					}
					
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
		
		function  ajaxGetPrice(productId,countryCode,colorCode){
			var ajaxProduct={};
			$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/psi/psiTransportOrder/ajaxPrice' ,
				    data: {
				    	"productId":productId,
				    	"countryCode":countryCode,
				    	"colorCode":colorCode,
				    },
				    dataType: 'json',
				    success:function(data){ 
				    	console.log(data);
				    	if(data.msg=='true'){
				    		ajaxProduct.price=data.price;
				    		ajaxProduct.packQuantity=data.packQuantity;
				    		ajaxProduct.currency=data.currency;
				    		//ajaxProduct.volume=data.volume;
				    		//ajaxProduct.weight=data.weight;
				    		ajaxProduct.hasElectric= data.hasElectric;
				    	}
			        }
			});
			
			return ajaxProduct;
		}
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*100)/100;  
	            return f;  
	     };
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiTransportOrder/">运单列表</a></li>
		<li class="active"><a href="#">${empty psiTransportOrder.id ?'新建':'编辑'}
		<c:if test="${psiTransportOrder.model eq '0'}">航空运单</c:if>
		<c:if test="${psiTransportOrder.model eq '1'}">海运运单</c:if>
		<c:if test="${psiTransportOrder.model eq '2'}">快递运单</c:if>
		<c:if test="${psiTransportOrder.model eq '3'}">铁路运单</c:if>
		</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiTransportOrder" action="${ctx}/psi/psiTransportOrder/addSave" method="post" class="form-horizontal">
		<input type="hidden" name="transportNo" value="${psiTransportOrder.transportNo}"/>
		<input type="hidden" name="model" value="${psiTransportOrder.model}" id="model"/>
		<input type="hidden" name="volume" />
		<input type="hidden" name="weight"  />
		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:98%">
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">运输类型:</label>
				<div class="controls" style="margin-left:120px" >
					<select name="transportType" style="width: 100%" id="transportType">
						<option value="0" ${psiTransportOrder.transportType eq '0'?'selected':''}>本地运输</option>	
						<option value="1" ${psiTransportOrder.transportType eq '1'?'selected':''}>FBA运输</option>	
						<!-- <option value="2" ${psiTransportOrder.transportType eq '2'?'selected':''}>批发运输</option> -->	
						<option value="3" ${psiTransportOrder.transportType eq '3'?'selected':''}>线下运输</option>	
					</select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;display:none" id="unlineDiv">
				<label class="control-label" style="width:100px">线下订单:</label>
				<div class="controls" style="margin-left:120px" >
			       <select style="width: 100%" name="unlineOrder" id="unlineOrder">
			       <option value=""></option>
					<c:forEach items="${map}" var="order">
						<option value="${order.key}">${order.value}</option>					
					</c:forEach>
				   </select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;display:none" id="supplierDiv">
				<label class="control-label" style="width:100px">物流供应商:</label>
				<div class="controls" style="margin-left:120px" >
			      <input name="carrier" id="carrier" type="text" style="width:95%" readonly/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;display:none" id="billNoDiv">
				<label class="control-label" style="width:100px">物流单号:</label>
				<div class="controls" style="margin-left:120px" >
			      <input name="ladingBillNo" id="ladingBillNo" type="text" style="width:95%"  readonly/>
				</div>
			</div>
		</div>
		<div style="float:left;width:98%">
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">发货仓库:</label>
				<div class="controls" style="margin-left:120px" >
					<select name="fromStore.id" style="width: 100%" id="fromStoreId">
						<c:forEach items="${toStock}" var="stock">
						<c:if test="${stock.type eq '0' }">
							<option value="${stock.id}" ${stock.id eq '21' ? 'selected':'' }>${stock.stockSign}</option>
						</c:if>
						</c:forEach>		
					</select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px" id="toStoreDiv">
				<label class="control-label" style="width:100px">目的仓库:</label>
				<div class="controls"  style="margin-left:120px" >
					<select name="toStore.id" style="width: 100%" id="toStore" >
						<c:forEach items="${toStock}" var="stock">
							<option value="${stock.id}" ${psiTransportOrder.toStore.id eq stock.id ? 'selected':'' }>${fns:getDictLabel(stock.platform, 'platform', '')}&nbsp;${stock.stockSign}</option>
						</c:forEach>		
					</select>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px;display:none" id="destinationDetailDiv">
				<label class="control-label" style="width:100px">目的地地址:</label>
				<div class="controls"  style="margin-left:120px" >
					<%-- <input name="destinationDetail" id="destinationDetail" type="text" style="width:95%" value="${psiTransport.destinationDetail}"/> --%>
					<select name="destinationDetail" style="width: 100%" id="destinationDetail" >
					    <c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek'}">
								<option value="${dic.value}">${dic.label}</option>
							</c:if>
			             </c:forEach>
		             </select>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">
					起运<c:choose>
						<c:when test="${psiTransportOrder.model eq '3'}">火车站</c:when>
						<c:when test="${psiTransportOrder.model eq '1'}">港口</c:when>
						<c:otherwise>机场</c:otherwise>
					</c:choose>:</label>
					<div class="controls" style="margin-left:120px" >
						<select name="orgin" style="width:95%">
							<c:forEach items="${fns:getDictList('transport_pod')}" var="dic">
								 <c:choose>
								 		<c:when test="${psiTransportOrder.model eq '0' ||psiTransportOrder.model eq '1' }">
								 			<option value="${dic.value}" ${dic.value eq 'SZX' ?'selected':''}>${dic.label}</option>
								 		</c:when>
								 		<c:otherwise>
								 			<option value="${dic.value}" ${dic.value eq 'HKG' ?'selected':''}>${dic.label}</option>
								 		</c:otherwise>
								 </c:choose>
					         </c:forEach>
						</select>
					</div>
			</div>   
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">
					目的<c:choose>
						<c:when test="${psiTransportOrder.model eq '3'}">火车站</c:when>
						<c:when test="${psiTransportOrder.model eq '1'}">港口</c:when>
						<c:otherwise>机场</c:otherwise>
					</c:choose>:</label>
					<div class="controls" style="margin-left:120px" >
						<select name="destination" style="width:95%">
							 <c:forEach items="${fns:getDictList('transport_pod')}" var="dic">
								<option value="${dic.value}" ${psiTransportOrder.destination eq dic.value ?'selected':''}>${dic.label}</option>
			             	 </c:forEach>
						</select>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">箱数:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="boxNumber" type="text" readonly style="width:95%" value="${psiTransportOrder.boxNumber}"/>
					</div>
			</div>
			
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">pickUp日期:</label>
				<div class="controls" style="margin-left:120px" >
					<input name="pickUpDate"  type="text" style="width:95%" class="Wdate" required="required"  value="<fmt:formatDate value="${psiTransportOrder.pickUpDate}" pattern="yyyy-MM-dd" />" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:50%;height:25px"></div>
			
			
		</div>
		
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">产品信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		
		<div  style="font-size: 14px;margin: 5px 100px 5px 0px;float:right">
		<c:if test="${psiTransportOrder.transportSta eq '0' }"> <div id="add-flag"><a href="#" id="add-row"><span class="icon-plus"></span>增加产品</a></div></c:if></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 15%">产品名</th>
				   <th style="width: 10%">国家</th>
				   <th style="width: 10%">颜色</th>
				   <th style="width: 25%">Sku[FnSku](Inventory Nums)</th>
				   <th style="width: 5%">线下</th>
				   <th style="width: 5%">装箱数</th>
				   <th style="width: 5%">数量</th>
				   <th style="width: 5%">箱数</th>
				   <th style="width: 5%">金额</th>
				   <th style="width: 5%">带电</th>
				   <th style="width: 5%">备注</th>
				   <th>操作</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
		</table>
		
		
		
		
		<div class="form-actions" style="float:left;width:98%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
