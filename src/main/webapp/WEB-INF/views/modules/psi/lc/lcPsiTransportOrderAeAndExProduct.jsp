<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单管理</title>
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
			eval('var inventorySkuMap = ${inventorySkuMap}');
			eval('var skuQuantityMap = ${skuQuantityMap}');
			eval('var bangdingSkus   = ${bangdingSkus}');
			
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
			var selCty="${psiTransportOrder.items[0].countryCode}";
			<c:forEach items="${products}" var="product" varStatus="i">
			  	product={};
			  	product.id='${product.id}';
			  	product.color='${product.color}';
			  	product.platform ='${product.platform}';
			  	productArgs['${product.id}']=product;
			</c:forEach>
			
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
					if(selCty.indexOf('jp')>=0){
						$("#toStore").select2("val",147);
					}else if(selCty.indexOf('com')>=0){
						$("#toStore").select2("val",120);
					}
				}else if(type=='1'){
					var tempStockId="";
					//<c:forEach items="${toStock}" var="stock">
					//  <c:if test='${stock.type eq 1}'>
							opts=opts+"<option value=${stock.id}>${stock.stockSign}</option>"
							tempStockId="${stock.id}";
					//  </c:if>
					//</c:forEach>
					$("#toStore").select2().empty();
					$("#toStore").select2().select2("data",[]);
					$("#toStore").append(opts).select2();
					$("#toStoreDiv").css("display","block");
					$("#destinationDetailDiv").css("display","none");
					$("#toStore").select2("val",tempStockId);
				}else{
					$("#toStoreDiv").css("display","none");
					$("#destinationDetailDiv").css("display","block");
				}
			});
			
			
			var model =$("#model").val();
			
			$(".rate").on("blur",function(){
				if($(this).val()!=''){
					var div = $(this).parent().parent().parent();
					if(div.find(".firstAmount").val()!=''){
						div.find(".afterAmount").val(parseFloat(div.find(".firstAmount").val())*parseFloat($(this).val()));
						//是快递总数要手填，然后算出单价
						if(model=='0'){
							var div = $(this).parent().parent().parent().parent();
							var total=0;
							var parentDiv =div.parent();
							parentDiv.find(".afterAmount").each(function(){
								if($(this).val()&&$(this).val()!=''){
									total=total+parseFloat($(this).val());
								}
							});
							
							$("#totalAmount").val(total);
						}
					}
				}
				
			});
			
			$(".firstAmount").on("blur",function(){
				var div = $(this).parent().parent().parent();
				var rate =div.find(".rate").val();
				if(rate!=''){
					div.find(".afterAmount").val(parseFloat(rate)*parseFloat($(this).val()));
				}
				
				//是快递总数要手填，然后算出单价
				if(model=='0'){
					var div = $(this).parent().parent().parent().parent();
					var total=0;
					var parentDiv =div.parent();
					parentDiv.find(".afterAmount").each(function(){
						if($(this).val()&&$(this).val()!=''){
							total=total+parseFloat($(this).val());
						}
					});
					
					$("#totalAmount").val(total);
				}
			});
			
			$("#weight").on("blur",function(){
				//是空运的时候算出运输费用
				if(model=='0'){
					var unitPrice=$("#unitPrice").val();
					if(unitPrice&&unitPrice!=''&&$(this).val()!=''){
						$("input[name='tranAmount']").val(parseFloat(unitPrice)*$(this).val());
						//光标移动到tranAmount上
						$("input[name='tranAmount']").focus();
					}
				}
				
				if(model=='2'){
					var totalAmount =$("#totalAmount").val();
					var localAmount =$("#localAmount").val();
					var otherAmount =$("#otherAmount").val()==""?0:$("#otherAmount").val();
					var otherAmount1 =$("#otherAmount1").val()==""?0:$("#otherAmount1").val();
					if(totalAmount!=''&&localAmount!=''&&$(this).val()!=''){
						$("input[name='tranAmount']").val(parseFloat(totalAmount)-parseFloat(localAmount)-parseFloat(otherAmount)-parseFloat(otherAmount1));
						$("#unitPrice").val(toDecimal((parseFloat(totalAmount)-parseFloat(localAmount)-parseFloat(otherAmount)-parseFloat(otherAmount1))/parseFloat($(this).val())));
						//光标移动到tranAmount上
						$("input[name='tranAmount']").focus();
					}
				}
				
				
				
			})
			
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
							var name=$(this).find("select.productId").children("option:selected").text();
							var color=$(this).find("select[name='colorCode']").children("option:selected").val();
							
							if(color!=''){
								name=name+"_"+color;
							}
							if("${components}".indexOf(name)<0){
								var singBoxNo=parseInt($(this).find(".productQuantity").val())/parseInt($(this).find(".packQuantity").val());
								var re = /^[0-9]*[1-9][0-9]*$/ ;
								if(!re.test(singBoxNo)){
									singBoxNo=parseInt(singBoxNo+1);
								}
								boxNumber = boxNumber+singBoxNo;
							}
							
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
							var name=$(this).find("select.productId").children("option:selected").text();
							var color=$(this).find("select[name='colorCode']").children("option:selected").val();
							
							if(color!=''){
								name=name+"_"+color;
							}
							if("${components}".indexOf(name)<0){
								var singBoxNo=(parseInt($(this).find(".productQuantity").val())/parseInt($(this).find(".packQuantity").val()));
								var re = /^[0-9]*[1-9][0-9]*$/ ;
								if(!re.test(singBoxNo)){
									singBoxNo=parseInt(singBoxNo+1);
								}
								boxNumber = boxNumber+singBoxNo;
							}
							
						};
					});
					
					$("input[name='boxNumber']").val(boxNumber);
				});
			
			$(".chdQuantity").live("blur",function(){
				var tr =$(this).parent().parent();
				var chdQuantity = tr.find(".chdQuantity").val();
				if(chdQuantity){
					if(parseInt(chdQuantity)%parseInt(tr.find(".packQuantity").val())!=0){
						top.$.jBox.tip("拆单数必须是装箱数的整数倍", 'info',{timeout:2000});
						$(this).focus();
					}
				}
			});
			
			$("#unitPrice").on("blur",function(){
				//是空运的时候算出运输费用
				if(model=='0'){
					var weight=$("#weight").val();
					if(weight&&weight!=''&&$(this).val()!=''){
						$("input[name='tranAmount']").val(parseFloat(weight)*$(this).val());
						//光标移动到tranAmount上
						$("input[name='tranAmount']").focus();
					}
				}
			})
			
			$("#localAmount,#totalAmount,#otherAmount,#otherAmount1").on("blur",function(){
				//是快递的时候算出单价和运输费用
				if(model=='2'){
					var totalAmount =$("#totalAmount").val();
					var localAmount =$("#localAmount").val();
					var otherAmount =$("#otherAmount").val()==""?0:$("#otherAmount").val();
					var otherAmount1 =$("#otherAmount1").val()==""?0:$("#otherAmount1").val();
					var weight      =$("#weight").val();
					if(totalAmount!=''&&localAmount!=''&&weight!=''){
						$("input[name='tranAmount']").val(toDecimal(parseFloat(totalAmount)-parseFloat(localAmount)-parseFloat(otherAmount)-parseFloat(otherAmount1)));
						$("#unitPrice").val(toDecimal((parseFloat(totalAmount)-parseFloat(localAmount)-parseFloat(otherAmount)-parseFloat(otherAmount1))/parseFloat(weight)));
						//光标移动到tranAmount上
						$("input[name='tranAmount']").focus();
					}
				}
			});
			
			
			
			$("#inputForm").on("change",".countryCode",function(){
				    var  tr = $(this).parent().parent();
				    tr.find("select.colorCode").change();
				  
					//填充装箱数
					var productId = tr.find("select.productId").children("option:selected").val();
					var country = tr.find("select[name='countryCode']").children("option:selected").val();
					var color = tr.find("select[name='colorCode']").children("option:selected").val();
					
					var res=ajaxGetPrice(productId,country,color);
					if(selCty==''||selCty!=country){
						selCty=country;
					}
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
		            tr.find(".price").val(res.price);//
		            tr.find(".lowerPrice").val(res.lowerPrice);
		            tr.find(".importPrice").val(res.importPrice);
		            tr.find(".productPrice").val(res.productPrice);
					tr.find(".currency").val(res.currency);
					tr.find(".hasElectric").html(res.hasElectric=='1'?'<span style="color:red">是</span>':'否');
					tr.find(".hasMagnetic").html(res.hasMagnetic=='1'?'<span style="color:red">是</span>':'否');
				});
				
				$("#inputForm").on("change",".colorCode",function(){
				 var  tr = $(this).parent().parent();
					//ajax  获取UnitPrice
				  var productId=tr.find("select.productId").val();
				  var countryCode=tr.find("select.countryCode").val();
				  var colorCode=$(this).val();
				 // tr.find(".price").val(ajaxGetPrice(productId,countryCode,colorCode).price);
				  var res=ajaxGetPrice(productId,countryCode,colorCode);
				  tr.find(".price").val(res.price);
		          tr.find(".productPrice").val(res.productPrice);
		          tr.find(".lowerPrice").val(res.lowerPrice);
		          tr.find(".importPrice").val(res.importPrice);
			      tr.find(".currency").val(res.currency);
				 
				  tr.find("select[name='sku']").select2("data",[]);
				  tr.find("select[name='sku']").select2().empty();
				  var conKey = productId+","+countryCode+","+colorCode;
				  tr.find("select.sku").removeAttr("disabled");
				  if(inventorySkuMap[conKey]){
					  var opts="";
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
					  tr.find("select[name='sku']").select2().empty();
				  }
				  
				});
				
				
			$("#inputForm").on("change",".productId",function(){
				var  tr = $(this).parent().parent();
				tr.find("select[name='colorCode']").select2("data",[]);
				tr.find("select[name='countryCode']").select2("data",[]);
				tr.find("select[name='colorCode']").select2().empty();
				tr.find("select[name='countryCode']").select2().empty();
				var productVal  = $(this).val();
				//获取选中的text
				var productName = $(this).children('option:selected').text();
			    tr.find("input[name='productName']").val(productName);
				colorsStr=productArgs[productVal].color;   
				countryStr=productArgs[productVal].platform;
				
				if(colorsStr==null||colorsStr==""){
					tr.find("select[name='colorCode']").select2().append("<option value=''>No color</option>");
				}else{
					colorArgs=colorsStr.split(',');
					$(colorArgs).each(function(i,data){
			   		  tr.find("select[name='colorCode']").select2().append("<option value='"+data+"' >"+data+"</option>");
					});
				}
				
				var toCountry=selCty;
				countryArgs=countryStr.split(',');
				$(countryArgs).each(function(i,data){
					 tr.find("select[name='countryCode']").select2().append("<option value='"+data+"' >"+countryMap[data]+"</option>");
				});
				if(toCountry=='com'||toCountry=='ca'||toCountry=='jp'||toCountry=='mx'){
			 		tr.find("select[name='countryCode']").select2("val",toCountry);
				}
				
				tr.find("select[name='countryCode']").select2().change();
				 
				tr.find(".productQuantity").val('').focus();
				tr.find(".boxNo").val("");
				
				var productId=$(this).val();
				  var countryCode=tr.find("select[name='countryCode']").val();
				  var colorCode=tr.find("select[name='colorCode']").val()=='No color'?'':tr.find("select[name='colorCode']").val();
				  var res=ajaxGetPrice(productId,countryCode,colorCode);
				  tr.find(".price").val(res.price);
				  tr.find(".productPrice").val(res.productPrice);
				  tr.find(".packQuantity").val(res.packQuantity);
				  tr.find(".lowerPrice").val(res.lowerPrice);
				  tr.find(".importPrice").val(res.importPrice);
				  
			});
			
			$(".remove-row").live("click",function(){
				 if($('#contentTable tbody tr').size()>1){
					var tr = $(this).parent().parent();
					tr.remove();
					
					var tbody =$('#contentTable tbody');
					var boxNumber =0;
					tbody.find("tr").each(function(){
						if($(this).find(".packQuantity").val()!=''&&$(this).find(".productQuantity").val()!=''){
							var name=$(this).find("select.productId").children("option:selected").text();
							var color=$(this).find("select[name='colorCode']").children("option:selected").val();
							
							if(color!=''){
								name=name+"_"+color;
							}
							if("${components}".indexOf(name)<0){
								var singBoxNo=parseInt($(this).find(".productQuantity").val())/parseInt($(this).find(".packQuantity").val());
								var re = /^[0-9]*[1-9][0-9]*$/ ;
								if(!re.test(singBoxNo)){
									singBoxNo=parseInt(singBoxNo+1);
								}
								boxNumber = boxNumber+singBoxNo;
							}
						};
					});
					
					$("input[name='boxNumber']").val(boxNumber);
				}
			});
			
			$("#add-row").on("click",function(e){
				e.preventDefault();
				var tbody=$("#contentTable tbody");
				var tr=$("<tr></tr>");
				tr.append("<td><select name='product.id' class='productId' style='width:90%'><option value=''></option><c:forEach items='${products}' var='product'><option value='${product.id}'>${product.name}</option></c:forEach></select><input type='hidden' name='itemPrice' class='price'/><input type='hidden' name='currency' class='currency'/><input type='hidden' name='productName'/></td>");
				tr.append("<td><select name='countryCode' class='countryCode'  style='width:90%'/></td>");
				tr.append("<td><select name='colorCode'   class='colorCode' style='width:90%'/></td>");
				tr.append("<td><select name='sku'   class='sku' style='width:98%'/></td>");
				tr.append("<td></td>");
				tr.append("<td><select name='offlineSta' style='width:100%'><option value='0'>否</option><option value='1'>是</option></select></td>");
				tr.append("<td><input type='text' maxlength='11' style='width: 80%' name='packQuantity'  class='packQuantity'/></td>");
	            tr.append("<td><input type='text' maxlength='11' style='width: 80%'  name='quantity' class='number productQuantity' /></td>");
	            tr.append("<td><input type='text' maxlength='50' style='width: 80%'  class='boxNo' readonly /></td>");
	            tr.append("<td></td>");
	            <c:if test="${'1' eq canSplit && psiTransportOrder.transportSta eq '0' }">
	            	tr.append("<td></td>");
			    </c:if>
			    <c:if test="${psiTransportOrder.transportSta eq '0'}">
			         tr.append("<td></td>");
			    </c:if>
			    <c:if test="${'1' eq canCreateFba &&(psiTransportOrder.transportSta eq '0' && psiTransportOrder.transportType eq '1')}">
			   		tr.append("<td></td>");
			    </c:if>
	            tr.append("<td><input type='text' maxlength='11' style='width: 80%' value='${item.lowerPrice}' name='lowerPrice' class='lowerPrice'/><input type='hidden' maxlength='11' style='width: 80%' name='productPrice' value='${item.productPrice}' class='productPrice'/></td>");
	            tr.append("<td><input type='text' maxlength='11' style='width: 80%' value='${item.importPrice}' name='importPrice' class='importPrice'/></td>");
	            tr.append("<td><sapn class='hasElectric'><span></td>");
	            tr.append("<td><sapn class='hasMagnetic'><span></td>");
	            tr.append("<td><input type='text' maxlength='50' style='width: 80%'  name='remark' /></td>");
	            tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>删除</a></td>");
				tbody.append(tr);
				tr.find("select.productId").select2();
				tr.find("select.countryCode").select2();
				tr.find("select.colorCode").select2();
				tr.find("select.sku").select2();
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
					if($("select[name='fromStore.id']").val()==$("select[name='toStore.id']").val()){
						top.$.jBox.tip("发货仓库和目的仓库不能相同 ", 'info',{timeout:3000});
						return false;
					}
					
					//如果本地费用不为空     currency1和vendor1不能为空
					if($("input[name='localAmount']").val()!=''){
						if($("select[name='vendor1.id']").val()==''||$("select[name='currency1']").val()==''||$("input[name='rate1']").val()==''){
							top.$.jBox.tip("Currency1和Vendor1和Rate1不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果运输费用不为空     currency2和vendor2不能为空
					if($("input[name='tranAmount']").val()!=''){
						if($("select[name='vendor2.id']").val()==''||$("select[name='currency2']").val()==''||$("input[name='rate2']").val()==''){
							top.$.jBox.tip("Currency2和Vendor2和Rate2不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果其他费用不为空     currency3和vendor3不能为空
					if($("input[name='dapAmount']").val()!=''){
						if($("select[name='vendor3.id']").val()==''||$("select[name='currency3']").val()==''||$("input[name='rate3']").val()==''){
							top.$.jBox.tip("Currency3和Vendor3和Rate3不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果其他费用不为空     currency4和vendor4不能为空
					if($("input[name='otherAmount']").val()!=''){
						if($("select[name='vendor4.id']").val()==''||$("select[name='currency4']").val()==''||$("input[name='rate4']").val()==''){
							top.$.jBox.tip("Currency4和Vendor4和Rate4不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果其他费用不为空     currency7和vendor7不能为空
					if($("input[name='otherAmount1']").val()!=''){
						if($("select[name='vendor7.id']").val()==''||$("select[name='currency7']").val()==''||$("input[name='rate7']").val()==''){
							top.$.jBox.tip("Currency7和Vendor7和Rate7不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果保险费用不为空     currency5和vendor5不能为空
					if($("input[name='insuranceAmount']").val()!=''){
						if($("select[name='vendor5.id']").val()==''||$("select[name='currency5']").val()==''){
							top.$.jBox.tip("Currency5和Vendor5不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果税费 不为空     currency6和vendor6不能为空
					if($("input[name='dutyTaxes']").val()!=''||$("input[name='taxTaxes']").val()!=''||$("input[name='otherTaxes']").val()!=''){
						if($("select[name='vendor6.id']").val()==''||$("select[name='currency6']").val()==''){
							top.$.jBox.tip("Currency6和Vendor6不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					
					 var keyStr="";
					 var flag = 1;
					 var zeroFlag=1;
					 var twoStr="";
					 var components='';
					$("#contentTable tbody tr").each(function(){
						var curkeyStr= $(this).find("input[name='productName']").val()+ $(this).find("select[name='countryCode']").val()+$(this).find("select[name='colorCode']").val()+$(this).find("select.sku").val()+","+$(this).find("select[name='offlineSta']").val()+","+$(this).find("input[name='packQuantity']").val();
						if(curkeyStr){
							if(keyStr.indexOf(curkeyStr+",")>=0){
								twoStr=curkeyStr;
								flag = 2;
								return false;
							}else{
								keyStr=keyStr+curkeyStr+",";
							};
						}
						
						if($("input[name='transportSta']").val()=='0'){
							var quantity = $(this).find("input[name='quantity']").val();
							if(quantity==0){
								zeroFlag=2;
								twoStr=curkeyStr;
								return false;
							};
						}
						
						var name=$(this).find("select.productId").children("option:selected").text();
						var color=$(this).find("select[name='colorCode']").children("option:selected").val();
						
						if(color!=''){
							name=name+"_"+color;
						}
						if("${components}".indexOf(name)>=0){
							components+=name+";";
						}
						
					});
					
					if(flag==2){
						top.$.jBox.tip("相同sku,装箱数,相同线别,只能有一条记录!!!  "+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					if(zeroFlag==2){
						top.$.jBox.tip("数量必须大于0!!! "+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					if($("#transportType").val()=="1"&&components!=''){//
	                    	top.$.jBox.tip('FBA运输不能含配件'+components, 'info',{timeout:3000});
							return false;
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
					
					$("option[value='No color']").each(function(){
						$(this).val("");
					});
					
					//把所有disable的select放开
					$("select[disabled]").each(function(){
						$(this).removeAttr("disabled");
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
			
			//拆分运单
			$("#splitOrder").on("click",function(e){
				top.$.jBox.confirm("确认要拆分运单吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok") {
						 $("#inputForm").attr("action","${ctx}/psi/lcPsiTransportOrder/splitEditSave");
						 $("#inputForm").submit();
						 $("#inputForm").attr("action","${ctx}/psi/lcPsiTransportOrder/productEditSave");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			//拆分创建FBA贴
			$("#splitFba").on("click",function(e){
				var skuCountry="";
				$("select[name='countryCode']").each(function(){
					skuCountry=$(this).val();
					return false;
				});
				var option="";
				<c:forEach items='${accountList}' var='account'>
			       option+="<option value='${account.key}_${accountList[account.key]}' "+(skuCountry=='${accountList[account.key]}'?'selected':'')+">${account.key}</option>";
	            </c:forEach>
	          
				top.$.jBox.confirm("确认要创建FBA贴吗?<br/>FBA目的地: <select id='country'>"+   
						option+
				    " <select><br/>","<spring:message code='sys_label_tips_msg'/>",function(v,h,f){
					if(v=="ok") {
						if(h.find("#country").val()){
							$("input[name='country']").val(h.find("#country").val());
						}
						 $("#inputForm").attr("action","${ctx}/psi/lcPsiTransportOrder/splitGenFba");
						 $("#inputForm").submit();
						 $("#inputForm").attr("action","${ctx}/psi/lcPsiTransportOrder/productEditSave");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		
		function  ajaxGetPrice(productId,countryCode,colorCode){
			var ajaxProduct={};
			$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/psi/lcPsiTransportOrder/ajaxPrice' ,
				    data: {
				    	"productId":productId,
				    	"countryCode":countryCode,
				    	"colorCode":colorCode,
				    	"model":'${psiTransportOrder.model}',
				    	"tranId":'${psiTransportOrder.id}'
				    },
				    dataType: 'json',
				    success:function(data){ 
				    	if(data.msg=='true'){
				    		ajaxProduct.price=data.price;
				    		ajaxProduct.productPrice=data.productPrice;
				    		ajaxProduct.packQuantity=data.packQuantity;
				    		ajaxProduct.currency=data.currency;
				    		ajaxProduct.hasElectric= data.hasElectric;
				    		ajaxProduct.hasMagnetic= data.hasMagnetic;
				    		ajaxProduct.lowerPrice=data.lowerPrice;
				    		ajaxProduct.importPrice=data.importPrice;
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
		<li><a href="${ctx}/psi/lcPsiTransportOrder/">(理诚)运单列表</a></li>
		<li class="active"><a href="#">(理诚)${empty psiTransportOrder.id ?'新建':'编辑'}${psiTransportOrder.model eq '0'? '航空':'快递'}运单</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiTransportOrder" action="${ctx}/psi/lcPsiTransportOrder/productEditSave" method="post" class="form-horizontal">
		<input type="hidden" name="id" value="${psiTransportOrder.id}"/>
		<input type="hidden" name="model" value="${psiTransportOrder.model}" id="model"/>
		<input type="hidden" name="ladingBillNo" value="${psiTransportOrder.ladingBillNo}"/>
		<input type="hidden" name="oldItemIds" value="${psiTransportOrder.oldItemIds}"/>
		<input type="hidden" name="transportSta" value="${psiTransportOrder.transportSta}"/>
		<input type="hidden" name="transportNo" value="${psiTransportOrder.transportNo}"/>
		<input type="hidden" name="createDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.createDate}'/>" />
		<input type="hidden" name="createUser.id" value="${psiTransportOrder.createUser.id}"/>
		
		<input type="hidden" name="operDeliveryDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.operDeliveryDate}'/>" />
		<input type="hidden" name="operDeliveryUser.id" value="${psiTransportOrder.operDeliveryUser.id}"/>
		<input type="hidden" name="operArrivalDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.operArrivalDate}'/>" />
		<input type="hidden" name="operArrivalUser.id" value="${psiTransportOrder.operArrivalUser.id}"/>
		<input type="hidden" name="operToPortDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.operToPortDate}'/>" />
		<input type="hidden" name="operToPortUser.id" value="${psiTransportOrder.operToPortUser.id}"/>
		<input type="hidden" name="operFromPortDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.operFromPortDate}'/>" />
		<input type="hidden" name="operFromPortUser.id" value="${psiTransportOrder.operFromPortUser.id}"/>
		<input type="hidden" name="paymentSta" value="${psiTransportOrder.paymentSta}"/>
		
		<input type="hidden" name="payAmount1"    value="${psiTransportOrder.payAmount1}"/>
		<input type="hidden" name="payAmount2"    value="${psiTransportOrder.payAmount2}"/>
		<input type="hidden" name="payAmount3"    value="${psiTransportOrder.payAmount3}"/>
		<input type="hidden" name="payAmount4"    value="${psiTransportOrder.payAmount4}"/>
		<input type="hidden" name="payAmount5"    value="${psiTransportOrder.payAmount5}"/>
		<input type="hidden" name="payAmount6"    value="${psiTransportOrder.payAmount6}"/>
		<input type="hidden" name="payAmount7"    value="${psiTransportOrder.payAmount7}"/>
		
		<input type="hidden" name="planeIndex"    value="${psiTransportOrder.planeIndex}"/>
		
		
		<input type="hidden" name="fbaTiminalTime"    value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.fbaTiminalTime}'/>" />
		<input type="hidden" name="fbaCheckingInTime" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.fbaCheckingInTime}'/>" />
		<input type="hidden" name="fbaClosedTime"     value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.fbaClosedTime}'/>" />
		
		<input type="hidden" name="etaDate"     value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.etaDate}'/>" />
		<input type="hidden" name="firstEtaDate"     value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.firstEtaDate}'/>" />
		
		<input type="hidden" name="operArrivalFixedDate"     value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrder.operArrivalFixedDate}'/>" />
		
		<input type="hidden" name="localPath"          value="${psiTransportOrder.localPath}"/>
		<input type="hidden" name="dapPath"            value="${psiTransportOrder.dapPath}"/>
		<input type="hidden" name="tranPath"           value="${psiTransportOrder.tranPath}"/>
		<input type="hidden" name="otherPath"          value="${psiTransportOrder.otherPath}"/>
		<input type="hidden" name="otherPath1"          value="${psiTransportOrder.otherPath1}"/>
		<input type="hidden" name="insurancePath"      value="${psiTransportOrder.insurancePath}"/>
		<input type="hidden" name="taxPath"            value="${psiTransportOrder.taxPath}"/>
		<input type="hidden" name="elsePath"           value="${psiTransportOrder.elsePath}"/>	
		<input type="hidden" name="suffixName"         value="${psiTransportOrder.suffixName}"/>
		  <input type="hidden" name="mixFile"         value="${psiTransportOrder.mixFile}"/>
		<input type="hidden" name="unlineOrder"        value="${psiTransportOrder.unlineOrder}"/>
		<input type="hidden" name="fbaInboundId"       value="${psiTransportOrder.fbaInboundId}"/>
		<input type="hidden" name="toCountry"          value="${psiTransportOrder.toCountry}"/>
		<input type="hidden" name="exportInvoicePath"  value="${psiTransportOrder.exportInvoicePath}"/>
		<input type="hidden" name="confirmPay"         value="${psiTransportOrder.confirmPay}"/>
		<input type="hidden" name="invoiceFlag"         value="${psiTransportOrder.invoiceFlag}"/>
		<input type="hidden" name="isCount"         value="${psiTransportOrder.isCount}"/> 
		<input type="hidden" name="country" />
		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		<div style="float:left;width:98%">
		
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">运输类型:</label>
				<div class="controls" style="margin-left:120px" >
					<c:choose>
						<c:when test="${psiTransportOrder.transportSta eq '0'}">
							<select name="transportType" style="width: 100%" id="transportType">
								<option value="0" ${psiTransportOrder.transportType eq '0'?'selected':''}>本地运输</option>	
								<option value="1" ${psiTransportOrder.transportType eq '1'?'selected':''}>FBA运输</option>	
								<option value="2" ${psiTransportOrder.transportType eq '2'?'selected':''}>批发运输</option>	
								<option value="3" ${psiTransportOrder.transportType eq '3'?'selected':''}>线下运输</option>	
							</select>
						</c:when>
						<c:otherwise>
							<input type="hidden" name="transportType"  value="${psiTransportOrder.transportType}"/>
							<c:if test="${psiTransportOrder.transportType eq '0'}">本地运输</c:if>	
							<c:if test="${psiTransportOrder.transportType eq '1'}">FBA运输</c:if>
							<c:if test="${psiTransportOrder.transportType eq '2'}">批发运输</c:if>
							<c:if test="${psiTransportOrder.transportType eq '3'}">线下运输</c:if>   
						</c:otherwise>
					</c:choose>
				</div>
			</div>
				<c:if test="${psiTransportOrder.transportType eq '3'}">
				  <div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>线下订单:</b></label>
					<div class="controls" style="margin-left:120px" >
					    <input type="text" style="width: 95%" readonly="readonly" value="${psiTransportOrder.unlineOrder}" />
					</div>
				 </div>
			</c:if>
			<c:if test="${psiTransportOrder.model eq '0' && psiTransportOrder.toStore.type eq '0'}">
				<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>分批:</b></label>
					<div class="controls" style="margin-left:120px" >
					<select name="planeNum" style="width: 100%">
						<option value="1" ${psiTransportOrder.planeNum eq'1'?'selected':'' }>1</option>
						<option value="2" ${psiTransportOrder.planeNum eq'2'?'selected':'' }>2</option>
						<option value="3" ${psiTransportOrder.planeNum eq'3'?'selected':'' }>3</option>
					</select>
					</div>
				</div>
			</c:if>
			
			<c:choose>
				<c:when test="${psiTransportOrder.transportType eq '1' && psiTransportOrder.transportSta eq '0'}">
					<div class="control-group" style="float:left;width:75%;height:25px">
						<label class="control-label" style="width:100px">关联FBA贴:</label>
						<div class="controls" style="margin-left:120px">
							<select name="shipmentId" class="multiSelect" multiple="multiple" style="width: 99%"  >
								<c:forEach items="${fbaList}" var="fba">
								  <c:if test="${not empty fba.shipmentId}">
									<option value="${fba.shipmentId}"  ${fn:contains(psiTransportOrder.shipmentId,fba.shipmentId)?'selected':'' }>${fba.shipmentId},${fba.shipmentName}</option>
								  </c:if>
								</c:forEach>
							</select>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<input type="hidden" name="shipmentId"  value="${psiTransportOrder.shipmentId}"/>
				</c:otherwise>
			</c:choose>
			<c:if test="${not empty psiTransportOrder.shipmentId}">
			<div class="control-group" style="float:left;width:75%;height:25px">
				<label class="control-label" style="width:100px">关联FBA贴:</label>
				<div class="controls" style="margin-left:120px">
					<c:forEach items="${fn:split(psiTransportOrder.shipmentId,',')}" var="shipmentId">
						<a  target="_blank" href="${ctx}/psi/fbaInbound?shipmentId=${shipmentId}&country=">${shipmentId}</a>
					</c:forEach>
				</div>
			</div>
			</c:if>
			
		</div>
		
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">发货仓库:</label>
				<div class="controls" style="margin-left:120px" >
					<select name="fromStore.id" style="width: 100%" >
						<c:forEach items="${toStock}" var="stock">
						<c:if test="${stock.type eq '0' }">
							<option value="${stock.id}" ${psiTransportOrder.fromStore.id eq stock.id ? 'selected':'' }>${stock.stockSign}</option>
						</c:if>
						</c:forEach>		
					</select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;${psiTransportOrder.transportType eq '2'||psiTransportOrder.transportType eq '3'?'display:none':''}" id="toStoreDiv">
				<label class="control-label" style="width:100px">目的仓库:</label>
				<div class="controls"  style="margin-left:120px" >
					<select name="toStore.id" style="width: 100%"  id="toStore">
						<c:forEach items="${toStock}" var="stock">
							<c:if test="${psiTransportOrder.transportType eq '0' }">
								<option value="${stock.id}" ${psiTransportOrder.toStore.id eq stock.id ? 'selected':'' }>${fns:getDictLabel(stock.platform, 'platform', '')}&nbsp;${stock.stockSign}</option>
							</c:if>
							<c:if test="${psiTransportOrder.transportType eq '1' }">
								<option value="${stock.id}" ${psiTransportOrder.toStore.id eq stock.id ? 'selected':'' }>${fns:getDictLabel(stock.platform, 'platform', '')}&nbsp;${stock.stockSign}</option>
							</c:if>
						</c:forEach>		
					</select>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px;${psiTransportOrder.transportType eq '0'||psiTransportOrder.transportType eq '1'?'display:none':''}" id="destinationDetailDiv">
				<label class="control-label" style="width:100px">目的地地址:</label>
				<div class="controls"  style="margin-left:120px" >
					<%-- <input name="destinationDetail" type="text" style="width:95%" value="${psiTransportOrder.destinationDetail}"/> --%>
					<select name="destinationDetail" style="width: 100%" id="destinationDetail" >
					    <c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek'}">
								<option value="${dic.value}" ${psiTransportOrder.destinationDetail eq dic.value ? 'selected':'' }>${dic.label}</option>
							</c:if>
			             </c:forEach>
		             </select>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">起运机场:</label>
				<div class="controls" style="margin-left:120px" >
					<select name="orgin" style="width:100%" >
						 <c:forEach items="${fns:getDictList('transport_pod')}" var="dic">
									<option value="${dic.value}" ${psiTransportOrder.orgin eq dic.value ?'selected':''}>${dic.label}</option>
				         </c:forEach>
					</select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">目的机场:</label>
				<div class="controls" style="margin-left:120px" >
					<select name="destination" style="width:100%" >
						 <c:forEach items="${fns:getDictList('transport_pod')}" var="dic">
							<option value="${dic.value}" ${psiTransportOrder.destination eq dic.value ?'selected':''}>${dic.label}</option>
		             	 </c:forEach>
					</select>
				</div>
			</div>
		</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">航空公司:</label>
					<div class="controls" style="margin-left:120px" >
						<%-- <input name="carrier" type="text"  maxlength="10" style="width:95%" readonly  value="${psiTransportOrder.carrier.nikename}"/> --%>
						<select name="carrier" style="width:98%" disabled="disabled">
							<option value=""></option>
							<c:forEach items="${typeSupplier}" var="typeSupplier">
								<option value="${typeSupplier}" ${psiTransportOrder.carrier eq typeSupplier ?'selected':''}>${typeSupplier}</option>
							</c:forEach>
					   </select>
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">离港日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="etdDate"  type="text" style="width:95%" readonly value="<fmt:formatDate value="${psiTransportOrder.etdDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">预计到港日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="preEtaDate" type="text" style="width:95%" readonly value="<fmt:formatDate value="${psiTransportOrder.preEtaDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">pickUp日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="pickUpDate" type="text"  style="width:95%" required="required" class="Wdate" value="<fmt:formatDate value="${psiTransportOrder.pickUpDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
		
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">重量:</label>
				<div class="controls" style="margin-left:120px" >
					<div class="input-prepend input-append">
					<input name="weight" type="text" style="width:80%" readonly id="weight" ${fn:contains(canEditStr,'TranAmount')&&psiTransportOrder.model eq '0'?'readonly':''} ${fn:contains(canEditStr,'TranAmount')&&psiTransportOrder.model eq '2'?'readonly':''} value="${psiTransportOrder.weight}"/> <span class="add-on">kg</span>
					</div>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">单价:</label>
				<div class="controls" style="margin-left:120px" >
					<input name="unitPrice" type="text" maxlength="10" readonly style="width:95%"  id="unitPrice" class=" price" value="${psiTransportOrder.unitPrice}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">体积:</label>
				<div class="controls" style="margin-left:120px" >
					<div class="input-prepend input-append">
					<input type="text" name="volume"  style="width:80%" readonly value="${psiTransportOrder.volume}"/> <span class="add-on">m³</span>
					</div>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">箱数:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="boxNumber" type="text"  style="width:95%" value="${psiTransportOrder.boxNumber}"/>
					</div>
			</div>
		
		</div>
		<blockquote  style="float:left;width:98%;height:25px">
			<div style="float: left; width:8%;height:15px"><p style="font-size: 14px">费用信息</p></div><div style="float:left;width:87%;height:15px" class="alert alert-info">
			<strong >${psiTransportOrder.model eq '0'? '空运Tips：运输运费=重量x单价;;;转换后金额=费用x汇率;;;总金额=各转换后金额累加;;;箱数=数量÷装箱数':'快递Tips：运输费用=总运费-本地费用-其他费用;单价=运输费用÷重量;箱数=数量÷装箱数'}</strong></div>   
		</blockquote>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" >Local:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="localAmount" type="text" maxlength="10" readonly style="width:95%" id="localAmount" ${fn:contains(canEditStr,'LocalAmount')||(fn:contains(canEditStr,'TranAmount')&&psiTransportOrder.model eq '2')?'readonly':''} class=" price firstAmount" value="${psiTransportOrder.localAmount}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency1:</label>
					<div class="controls" style="margin-left:100px" >
					<select name="currency1" style="width:95%" disabled>
						<option value=""></option>
						<c:forEach items="${currencys}" var="currency">
							<option value="${currency}" ${psiTransportOrder.currency1 eq currency ?'selected':''}>${currency}</option>
						</c:forEach>
					</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Vendor1:</label>
					<div class="controls" style="margin-left:100px">
						<select name="vendor1.id" style="width:95%" disabled >
							<option value=""></option>
							<c:forEach items="${tranSuppliers}" var="tranSupplier">
								<option value="${tranSupplier.id}"  ${psiTransportOrder.vendor1.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
							</c:forEach>
						</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >汇率1:</label>
					<div class="controls" style="margin-left:100px">
						<input  type="text" name="rate1" maxlength="10"  readonly style="width:95%" ${fn:contains(canEditStr,'LocalAmount')||(fn:contains(canEditStr,'TranAmount')&&psiTransportOrder.model eq '2')?'readonly':''} class="rate " value="${psiTransportOrder.rate1}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >金额1:</label>
					<div class="controls" style="margin-left:100px" >
						<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrder.localAmount*psiTransportOrder.rate1}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" >运输费用:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="tranAmount" type="text" maxlength="10" readonly style="width:95%" readonly="readonly" class=" price firstAmount" value="${psiTransportOrder.tranAmount}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Currency2:</label>
					<div class="controls" style="margin-left:100px" >
					<select name="currency2" style="width:95%" disabled>
						<option value=""></option>
						<c:forEach items="${currencys}" var="currency">
							<option value="${currency}" ${psiTransportOrder.currency2 eq currency ?'selected':'' }>${currency}</option>
						</c:forEach>
					</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Vendor2:</label>
					<div class="controls" style="margin-left:100px">
						<select name="vendor2.id" style="width:95%" disabled>
						<option value=""></option>
							<c:forEach items="${tranSuppliers}" var="tranSupplier">
								<option value="${tranSupplier.id}"  ${psiTransportOrder.vendor2.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
							</c:forEach>
						</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >汇率2:</label>
					<div class="controls" style="margin-left:100px" >
						<input  type="text" name="rate2" maxlength="10" readonly style="width:95%" ${fn:contains(canEditStr,'TranAmount')?'readonly':''} class="rate " value="${psiTransportOrder.rate2}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >金额2:</label>
					<div class="controls" style="margin-left:100px" >
						<input  type="text" maxlength="10" readonly style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrder.tranAmount*psiTransportOrder.rate2}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
		<c:if test="${psiTransportOrder.model eq '0' }">
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">目的港费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="dapAmount" type="text"  maxlength="10" readonly style="width:95%" class=" price firstAmount" value="${psiTransportOrder.dapAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency3:</label>
						<div class="controls" style="margin-left:100px" >
						<select name="currency3" style="width:95%"  disabled>
							<option value=""></option>
							<c:forEach items="${currencys}" var="currency">
								<option value="${currency}" ${psiTransportOrder.currency3 eq currency ?'selected':'' }>${currency}</option>
							</c:forEach>
						</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor3:</label>
						<div class="controls" style="margin-left:100px">
							<select name="vendor3.id" style="width:95%" disabled>
							<option value=""></option>
								<c:forEach items="${tranSuppliers}" var="tranSupplier">
									<option value="${tranSupplier.id}"  ${psiTransportOrder.vendor3.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
								</c:forEach>
							</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >汇率3:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" name="rate3" maxlength="10" readonly style="width:95%" class="rate " value="${psiTransportOrder.rate3}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >金额3:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" maxlength="10" readonly style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrder.dapAmount*psiTransportOrder.rate3}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
		</c:if>
		
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">其他费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="otherAmount" type="text"  readonly maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrder.otherAmount}" id="otherAmount"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency4:</label>
						<div class="controls" style="margin-left:100px" >
							<select name="currency4" style="width:95%" disabled>
							<option value=""></option>
							<c:forEach items="${currencys}" var="currency">
								<option value="${currency}" ${psiTransportOrder.currency4 eq currency ?'selected':'' }>${currency}</option>
							</c:forEach>
						</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor4:</label>
						<div class="controls" style="margin-left:100px">
							<select name="vendor4.id" style="width:95%" disabled>
								<option value="" ></option>
								<c:forEach items="${tranSuppliers}" var="tranSupplier">
									<option value="${tranSupplier.id}"  ${psiTransportOrder.vendor4.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
								</c:forEach>
							</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >汇率4:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" name="rate4" maxlength="10" readonly style="width:95%" class="rate" value="${psiTransportOrder.rate4}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >金额4:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" maxlength="10" readonly style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrder.otherAmount*psiTransportOrder.rate4}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
			
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">其他费用1:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="otherAmount1" type="text"  readonly maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrder.otherAmount1}" id="otherAmount1"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency7:</label>
						<div class="controls" style="margin-left:100px" >
							<select name="currency7" style="width:95%" disabled>
							<option value=""></option>
							<c:forEach items="${currencys}" var="currency">
								<option value="${currency}" ${psiTransportOrder.currency7 eq currency ?'selected':'' }>${currency}</option>
							</c:forEach>
						</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor7:</label>
						<div class="controls" style="margin-left:100px">
							<select name="vendor4.id" style="width:95%" disabled>
								<option value="" ></option>
								<c:forEach items="${tranSuppliers}" var="tranSupplier">
									<option value="${tranSupplier.id}"  ${psiTransportOrder.vendor7.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
								</c:forEach>
							</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >汇率7:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" name="rate7" maxlength="10" readonly style="width:95%" class="rate" value="${psiTransportOrder.rate7}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >金额7:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" maxlength="10" readonly style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrder.otherAmount1*psiTransportOrder.rate7}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">保险费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="insuranceAmount" type="text" readonly maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrder.insuranceAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency5:</label>
						<div class="controls" style="margin-left:100px" >
							<select name="currency5" style="width:95%" disabled>
							<option value="" ></option>
							<c:forEach items="${currencys}" var="currency">
								<option value="${currency}" ${psiTransportOrder.currency5 eq currency ?'selected':'' }>${currency}</option>
							</c:forEach>
						</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor5:</label>
						<div class="controls" style="margin-left:100px">
							<select name="vendor5.id" style="width:95%" disabled>
								<option value="" ></option>
								<c:forEach items="${tranSuppliers}" var="tranSupplier">
									<option value="${tranSupplier.id}"  ${psiTransportOrder.vendor5.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
								</c:forEach>
							</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:28%;height:25px"></div>
			</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:10%;height:25px">
				<label class="control-label" style="width:60px">进口税:</label>
				<div class="controls" style="margin-left:70px" >
					<input name="dutyTaxes" type="text" maxlength="10" readonly style="width:95%" class=" price" value="${psiTransportOrder.dutyTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:10%;height:25px">
				<label class="control-label" style="width:60px">关税:</label>
				<div class="controls" style="margin-left:70px" >
					<input name="taxTaxes" type="text" maxlength="10" readonly  style="width:85%" class=" price" value="${psiTransportOrder.taxTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:10%;height:25px">
				<label class="control-label" style="width:60px">other税:</label>
				<div class="controls" style="margin-left:70px" >
					<input name="otherTaxes" type="text" maxlength="10" readonly  style="width:85%" class=" price" value="${psiTransportOrder.otherTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency6:</label>
					<div class="controls"  style="margin-left:100px">
						<select name="currency6" style="width:95%" disabled>
						<option value=""></option>
						<c:forEach items="${currencys}" var="currency" >
							<option value="${currency}" ${psiTransportOrder.currency6 eq currency ?'selected':''}>${currency}</option>
						</c:forEach>
					</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Vendor6:</label>
					<div class="controls" style="margin-left:100px">
						<select name="vendor6.id" style="width:95%" disabled>
						<option value=""></option>
							<c:forEach items="${tranSuppliers}" var="tranSupplier">
								<option value="${tranSupplier.id}" ${psiTransportOrder.vendor6.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
							</c:forEach>
						</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:28%;height:25px"></div>
		</div>
		
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">总额:</label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" maxlength="10" style="width:85%" id="totalAmount" readonly class=" price" value="${psiTransportOrder.totalAmount}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
				<label class="control-label" style="width:80px">报关金额:</label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" maxlength="10" style="width:85%"  class="price" name="declareAmount" value="${psiTransportOrder.declareAmount}" readonly="readonly"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
				<label class="control-label" style="width:80px">退税金额:</label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" maxlength="10" style="width:85%"  class="price" name="taxRefundAmount" value="${psiTransportOrder.taxRefundAmount}" readonly="readonly"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px">报关单号:</label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" style="width:90%"   name="declareNo" value="${psiTransportOrder.declareNo}" readonly="readonly"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:100px">报关出口日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="exportDate" type="text"  readonly="readonly" style="width:95%" value="<fmt:formatDate value="${psiTransportOrder.exportDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
		</div>
		
		<c:if test="${not empty psiTransportOrder.localPath || not empty psiTransportOrder.tranPath || not empty psiTransportOrder.dapPath || not empty psiTransportOrder.otherPath || not empty psiTransportOrder.insurancePath || not empty psiTransportOrder.taxPath|| not empty psiTransportOrder.otherPath1}">
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">费用凭证信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		
		<div style="float:left;width:98%;height:50px;">
			<div class="control-group" style="float:left;width:98%;height:40px">
					<b>已上传凭证</b>：
					<c:if test="${not empty psiTransportOrder.localPath}">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.localPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/lcPsiTransport/${attchment}'/>">local_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>  
					</c:if>
					<c:if test="${not empty psiTransportOrder.tranPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.tranPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/lcPsiTransport/${attchment}'/>">运输费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.dapPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.dapPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/lcPsiTransport/${attchment}'/>">目的港费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.otherPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.otherPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/lcPsiTransport/${attchment}'/>">其他费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.otherPath1}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.otherPath1,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/lcPsiTransport/${attchment}'/>">其他费用1凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.insurancePath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.insurancePath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/lcPsiTransport/${attchment}'/>">保费费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.taxPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.taxPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/lcPsiTransport/${attchment}'/>">税费费用_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>
					</c:if>
					 
			</div>
		</div>
		</c:if>
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">备注信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:98%;">
				<label class="control-label" style="width:80px">备注:</label>
				<div class="controls" style="margin-left:100px">
					<textarea name="remark"  style="width:100%;height:80px;" >${psiTransportOrder.remark}</textarea>
				</div>
			</div>
		</div>
		
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">产品信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		
		<div  style="font-size: 14px;margin: 5px 100px 5px 0px;float:right">
			<c:if test="${psiTransportOrder.transportSta eq '0' }"><%--新建状态下运输增加产品和拆单 --%>
				<a href="#" id="add-row"><span class="icon-plus"></span>增加产品</a>
				&nbsp;&nbsp;
				<c:if test="${'1' eq canSplit }"><%--如果有建FBA贴的item，不再允许拆单 --%>
					<input id="splitOrder" class='btn btn-primary' type='button' value='拆分运单'/>
				</c:if>
			</c:if>
		   <c:if test="${'1' eq canCreateFba &&(psiTransportOrder.transportSta eq '0' && psiTransportOrder.transportType eq '1')}">
		   		&nbsp;&nbsp;
				<input id="splitFba" class='btn btn-primary' type='button' value='Create Fba'/>
		   </c:if>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				    <th style="width: 15%">产品名</th>
				   <th style="width: 8%">国家</th>
				   <th style="width: 8%">颜色</th>
				   <th style="width: 25%">Sku[FnSku](Inventory Nums)</th>
				   <th style="width: 5%">Account</th>
				   <th style="width: 3%">线下</th>
				   <th style="width: 3%">装箱<br/>数</th>
				   <th style="width: 5%">数量</th>
				   <th style="width: 5%">箱数</th>
				   <c:if test="${'1' eq canSplit && psiTransportOrder.transportSta eq '0' }">
				   	<th style="width: 3%">拆单<br/>数</th>
				   </c:if>
				    <c:if test="${psiTransportOrder.transportSta eq '0'}">
				   		<th style="width:3%">在库</th>
				   	</c:if>
				   <c:if test="${'1' eq canCreateFba &&(psiTransportOrder.transportSta eq '0' && psiTransportOrder.transportType eq '1')}">
				   	<th style="width: 3%">建贴</th>
				   </c:if>
				   <th style="width: 5%">出口<br/>金额(￥)</th>
				   <th style="width: 5%">进口<br/>金额</th>
				   <th style="width: 3%">带电</th>
				   <th style="width: 3%">带磁</th>
				   <th style="width: 5%">备注</th>
				   <th style="width: 5%">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:if test="${ not empty psiTransportOrder.id }">
		<c:forEach items="${psiTransportOrder.items}"  var="item">
			<tr>
				<c:set var="proKey" value="${item.product.id},${item.countryCode },${item.colorCode}"/>
				<td>
				<input type="hidden" name="id" value="${item.id}"/>
				<input type="hidden" name="shippedQuantity" value="${item.shippedQuantity}"/>
				<input type="hidden" name="receiveQuantity" value="${item.receiveQuantity}"/>
				<input type="hidden" name="itemPrice" value="${item.itemPrice}" class="price"/>
				<input type="hidden" name="currency" value="${item.currency}" class="currency"/>
				<input type="hidden" name="fbaFlag" value="${item.fbaFlag}"/>
				<input type="hidden" name="fbaInboundId" value="${item.fbaInboundId}"/>
				<select style="width: 90%" class="productId" name="product.id" id="productId" ${psiTransportOrder.transportSta ne '0'?'disabled':'' }>
					<c:forEach items="${products}" var="product" varStatus="i">
							 <option value='${product.id}' ${item.product.id eq product.id ?'selected':''}>${product.name}</option>
					</c:forEach>
				</select>
				<input type='hidden' name="productName" value="${item.productName}"/>
				</td>
				<td>
					<select name="countryCode" class="countryCode" style="width:90%" ${psiTransportOrder.transportSta ne '0'?'disabled':'' }>
						<c:forEach items="${item.countryList}" var="country" varStatus="i">
								 <option value="${country}" ${country eq item.countryCode ?'selected':''}>${fns:getDictLabel(country, 'platform', '')}</option>;
						</c:forEach>
					</select>
				</td>
				<td>
					<select name="colorCode" class="colorCode"  style="width:90%" ${psiTransportOrder.transportSta ne '0'?'disabled':'' }>
						<c:if test="${fn:length(item.colorList)>0}">
							<c:forEach items="${item.colorList}" var="color" varStatus="i">
									 <option value='${color}'  ${color eq item.colorCode ?'selected':''}>${color}</option>;
							</c:forEach>
						</c:if>
						<c:if test="${fn:length(item.colorList)==0}">
							 <option value=""  selected>No color</option>;
						</c:if>
					</select>
				</td>
				
				<td>
					<select name="sku" class="sku" ${(fn:length(inventorySkus[proKey]) eq 1 && fn:contains(inventorySkus[proKey],item.sku))?'disabled':''} style="width:98%" ${psiTransportOrder.transportSta ne '0'?'disabled':'' }>
						<c:if test="${empty inventorySkus[proKey]||!(fn:contains(inventorySkus[proKey],item.sku))}">
							<option value="${item.sku}"  selected >${not empty skuQuantitys[item.sku]?skuQuantitys[item.sku]:item.sku}</option>
						</c:if>
						<c:forEach items="${inventorySkus[proKey]}" var="sku" varStatus="i">
						 	<option value="${sku}"  ${sku eq item.sku ?'selected':''}>${skuQuantitys[sku]} ${not empty bangdingMap[sku] ?'绑定':''}</option>
						</c:forEach>
					</select>
				</td>
				<td><c:set var='skuKey' value='${item.sku}_${item.countryCode}'/>${accountMap[skuKey]}</td>
				<td>
					<select name="offlineSta" style="width:95%">
						<option value="1" ${item.offlineSta eq '1'?'selected':''}>是</option>
						<option value="0" ${item.offlineSta eq '0'?'selected':''}>否</option>
					</select>
				</td>
				<td>
				
				   <input type="text" maxlength="11" style="width: 80%" name="packQuantity"  class="packQuantity" value="${item.packQuantity }"  ${not empty item.fbaInboundId?'readonly':'' }/>
				
				</td>
				<td><input type="text" maxlength="11" style="width: 80%" name="quantity" class="number productQuantity" value="${item.quantity}" ${psiTransportOrder.transportSta ne '0'?'readonly':'' }/></td>
				<td><input type="text" maxlength="11" style="width: 80%" class="boxNo" value="${item.quantity/item.packQuantity}" readonly /></td>
				<c:if test="${'1' eq canSplit && psiTransportOrder.transportSta eq '0' }">
					<td><input type="text" maxlength="11" style="width: 80%" name="chdQuantity" class="number chdQuantity" value="0" max="${item.quantity}"/></td><!-- 拆单数 -->
				</c:if>
				<c:if test="${psiTransportOrder.transportSta eq '0'}">
			   		<td><span style="color:${(inventory[item.countryCode][item.sku]<item.packQuantity||inventory[item.countryCode][item.sku]<item.quantity)?'red':''}">${inventory[item.countryCode][item.sku] }</span></td>
			   	</c:if>
			   <c:if test="${'1' eq canCreateFba &&(psiTransportOrder.transportSta eq '0' && psiTransportOrder.transportType eq '1')}">
			   		<td>
			   			<c:if test="${'0' eq item.fbaFlag }">
							<select name="isFba" style="width:95%">
								<option value="0">否</option>
								<c:if test="${item.sku ne item.productColorCountry}"> <option value="1" ${inventory[item.countryCode][item.sku]>=item.quantity?'selected':'' }>是</option></c:if>
							</select>
						</c:if>
						<c:if test="${'1' eq item.fbaFlag}">
							已建贴
						</c:if>
					</td>
			   </c:if>
				<td><input type='text' maxlength='11' style='width: 80%'  name='lowerPrice' value='${item.lowerPrice }' class='lowerPrice'/><input type="hidden" maxlength="11" style="width: 80%" name="productPrice" value="${item.productPrice}" class="productPrice"/> </td>
				<td><input type='text' maxlength='11' style='width: 80%'  name='importPrice' value='${item.importPrice }' class='importPrice'/></td>
				<td><span class="hasElectric">
			 	<c:if test="${item.product.hasElectric eq '1' }"><font style="color:red">是</font></c:if>
				<c:if test="${item.product.hasElectric ne '1' }">否</c:if>
				</span></td>
				<td><span class="hasMagnetic">
				 	<c:if test="${item.product.hasMagnetic eq '1' }"><font style="color:red">是</font></c:if>
					<c:if test="${item.product.hasMagnetic ne '1' }">否</c:if>
				</span></td>
				<td><input type="text"  style="width: 80%" name="remark" value="${item.remark}" ${psiTransportOrder.transportSta ne '0'?'readonly':'' }/></td>
				<td><c:if test="${psiTransportOrder.transportSta eq '0' }"><a href="#" class="remove-row"><span class="icon-minus"></span>删除</a></c:if>
				</td>
			</tr>
		</c:forEach>
		</c:if>
		</tbody>
		</table>
		
		
		
		
		<div class="form-actions" style="float:left;width:98%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
