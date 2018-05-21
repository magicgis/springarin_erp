<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊帖子上架</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" type="text/css" />
	<script type="text/javascript" src="${ctxStatic}/x-editable/js/bootstrap-editable.js"></script>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
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
		var iFlag = 0;
		$(document).ready(function() {
			jQuery.validator.addMethod("pricegt",function(value, element, param) {
				return this.optional(element) || parseFloat(value) >= parseFloat(param);
			},"请输入更大的值");

			jQuery.validator.addMethod("pricelt",function(value, element, param) {
				return this.optional(element) || parseFloat(value) <= parseFloat(param);
			},"请输入更小的值");

			jQuery.validator.addMethod("priceLimit",function(value, element, param) {
				return this.optional(element) || parseFloat(value) <= parseFloat(param);
			},"涨价不能超过上限值({0})");
			$('#add-row').on('click', function(e){
				iFlag +=1;
			   e.preventDefault();
			   var tableBody = $('.table > tbody'), 
			   lastRowClone = $('tr:last-child', tableBody).clone();
			   $('input[type=text][name!=saleEndDate][name!=saleStartDate]', lastRowClone).val('');  
			 var html = "<option value=''></option>";
			 <c:forEach items="${sku}" var="item"> 
			 		html = html+"<option value='${item.key}'>${item.value}</option>";
			 </c:forEach>
			 <c:if test="${fn:length(sku)>0}">
			 	html = html+"<option value='sku'>手动输入Sku</option>";
			 </c:if>
			 lastRowClone.find(".sku").html("<span style='font-weight:bold;font-size: 18px;padding-right:5px' class='countryTips'></span><select class=\"required\" name=\"sku\" style=\"width: 90%\">"+html+"</select><input name=\"sku\" type=\"text\" style=\"margin-top:5px;display: none;\"/>");
			   
			 	lastRowClone.find("select").select2();
			 	lastRowClone.find(".salePrice").removeAttr("pricegt");
			 	lastRowClone.find(".salePrice").removeAttr("pricelt");
			 	lastRowClone.find(".salePrice").removeAttr("priceLimit");
			 	lastRowClone.find(".salePrice").parent().find("label").remove();
			 	lastRowClone.find(".salePrice").attr("id","salePrice" + iFlag);
			 	lastRowClone.find(".priceInput").attr("id","price" + iFlag);
			 	lastRowClone.find(".deleteB2b").attr("id","deleteB2b" + iFlag);
			 	lastRowClone.find(".businessPrice").attr("id","businessPrice" + iFlag);
			 	lastRowClone.find(".quantityPrice1").attr("id","quantityPrice1" + iFlag);
			 	lastRowClone.find(".quantityLowerBound1").attr("id","quantityLowerBound1" + iFlag);
			 	lastRowClone.find(".quantityPrice2").attr("id","quantityPrice2" + iFlag);
			 	lastRowClone.find(".quantityLowerBound2").attr("id","quantityLowerBound2" + iFlag);
			 	lastRowClone.find(".quantityPrice3").attr("id","quantityPrice3" + iFlag);
			 	lastRowClone.find(".quantityLowerBound3").attr("id","quantityLowerBound3" + iFlag);
			 	lastRowClone.find(".quantityPrice4").attr("id","quantityPrice4" + iFlag);
			 	lastRowClone.find(".quantityLowerBound4").attr("id","quantityLowerBound4" + iFlag);
			 	lastRowClone.find(".quantityPrice5").attr("id","quantityPrice5" + iFlag);
			 	lastRowClone.find(".quantityLowerBound5").attr("id","quantityLowerBound5" + iFlag);
			 	lastRowClone.find(".businessPriceCompare").attr("id","businessPriceCompare" + iFlag);
			   tableBody.append(lastRowClone);
			});
			$('#contentTable').on('click', '.remove-row', function(e){
			  e.preventDefault();
			  if($('#contentTable tr').size()>2){
				  var row = $(this).parent().parent();
				  row.remove();
			  }
			});
			
			$("#accountName").change(function(){
				var params = {};
				if($(this).val()){
					params.accountName = $(this).val().join(",");
				}
				params.reason = encodeURI($("select[name='reason']").val());
				
				$("#contentTable tbody tr").each(function(i,j){
					if($(j).find("select").select2("val")!='sku'){
						params["prices["+i+"]."+$(j).find("select").attr("name")]=$(j).find("select").val();
					}
					$(j).find("input[type!='']").each(function(){
						if($(this).attr("name")&&$(this).css("display")!='none'){
							params["prices["+i+"]."+$(this).attr("name")]=$(this).val();
						}
					});
				});
				window.location.href = "${ctx}/amazoninfo/priceFeed/form?"+$.param(params);
			});
			
			$("select[name='reason']").change(function(){
				if($(this).val()!=''&&$(this).val()=="包邮调价"){
					$("select[name='sku']").each(function(){
						var sku=$(this).val();
						var tr = $(this).parent().parent();
						var skuCountry ="";
						if(sku!=''&& sku.indexOf("@")>=0){
							skuCountry =sku.split("@")[0];
							skuCountry = skuCountry.split("_")[1].toLowerCase();
						}
						if(skuCountry!=''){
							var value="";
							if("fr"==skuCountry){
								value = "25";
							}else if("it"==skuCountry){
								value = "29";
							}else if("ca"==skuCountry){
								value = "25";
							}else if("us"==skuCountry){
								value = "49";
							}else if("uk"==skuCountry){
								value = "20";
							}else if("de"==skuCountry){
								value = "29";
							}else if("jp"==skuCountry){
								value = "2000";
							}
							if(value != ""){
								tr.find(".price").val(value);
							}
						 }
						
					});
				}
				
				
			});
			
			
			$("select[name='sku']").live("change",function(){
				var tr = $(this).parent().parent();
				var skuName = $(this).find("option:selected").text();	//当前选中的值
				var value = "";
				var baoyou = false;
				if("包邮调价"==$("select[name='reason']").val()){
					baoyou = true;
				}
				var accountName =$("#accountName").val().join(",");
				
				if(skuName.split("[").length == 1&&$(this).val()!='sku'){	//选择的是不带sku的产品名称
					var productName = skuName.split("[")[0];
				
					/* $.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/amazoninfo/promotionsWarning/findPromotionsByName?country="+country+"&name="+productName,
		      			   async: true,
		      			   success: function(msg){	    
		      				 $("#showMsg").html("");
		      				   var proMsg="";
		      				   var countryArr=$("#country").val();
		      				   for(var i=0;i<countryArr.length;i++){
		      					    var temp=msg[countryArr[i]];
		      					    if(temp!=undefined){
		      					    	 for(var j=0;j<temp.length;j++){
				      					    	proMsg+=(countryArr[i]=='com'?'us':countryArr[i])+","+temp[j]+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
				      					    }
		      					    }	
		      				   }
		      				   if(proMsg!=''){
		      					 $("#showMsg").html(productName+"折扣:<br/>"+proMsg);
		      				   }
		      			   }
		          	}); */
					//根据产品名称去拿到skujihe
					var skus = [];
					$.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/amazoninfo/priceFeed/getSkus?accountName="+accountName+"&productName="+productName,
		      			   async: false,
		      			   success: function(msg){
		      				 skus = msg;		
		      			   }
		          	});
				 	for(var i=0; i<skus.length; i++){
					 	var newSku = skus[i].key;
						iFlag +=1;
					    var tableBody = $('.table > tbody'), 
					    lastRowClone = $('tr:last-child', tableBody).clone();
					    $('input[type=text][class!=Wdate]', lastRowClone).val('');  
					 	var html = "<option value=''></option>";
					 	<c:forEach items="${sku}" var="item"> 
					 		html = html+"<option value='${item.key}' ${item.key eq '"+newSku+"' ?'selected':''}>${item.value}</option>";
					 	</c:forEach>
					    <c:if test="${fn:length(sku)>0}">
					 	  html = html+"<option value='sku'>手动输入Sku</option>";
					    </c:if>
					    lastRowClone.find(".sku").html("<span style='font-weight:bold;font-size: 18px;padding-right:5px' class='countryTips'></span><select class=\"required\" name=\"sku\" style=\"width: 90%\">"+html+"</select><input name=\"sku\" type=\"text\" style=\"margin-top:5px;display: none;\"/>");
					 	lastRowClone.find(".salePrice").removeAttr("pricelt");
					 	lastRowClone.find(".salePrice").removeAttr("pricegt");
					 	lastRowClone.find(".salePrice").removeAttr("priceLimit");
					 	lastRowClone.find(".salePrice").parent().find("label").remove();
						var rs = "";
						$.ajax({
			      			   type: "POST",
			      			   url: "${ctx}/amazoninfo/priceFeed/getPrice?countrySku="+newSku,
			      			   async: false,
			      			   success: function(msg){
			      				   rs = msg;		
			      			   }
			          	});
						
						if(value != null && value.length >0){
							lastRowClone.find(".price").val(value);
						} else {
							lastRowClone.find(".salePrice").val(rs.salePrice);
							lastRowClone.find(".priceInput").val(rs.price);
							lastRowClone.find(".businessPriceCompare").val(rs.businessPrice);
						}
						lastRowClone.find(".lirun").val(rs.ean);
						if(rs.warnPrice && !baoyou){
							lastRowClone.find(".salePrice").attr("pricegt",rs.warnPrice);
						}
						if(rs.highWarnPrice && !baoyou){
							lastRowClone.find(".salePrice").attr("pricelt",rs.highWarnPrice);
						}
						if(rs.fnsku && !baoyou){
							lastRowClone.find(".salePrice").attr("priceLimit", rs.fnsku);
						}
						
						var skuCountry ="";
						var account ="";
						if(newSku!=''&& newSku.indexOf("@")>=0){
							account =newSku.split("@")[0];
							skuCountry = account.split("_")[1].toLowerCase();
						}
						//逐行对包邮调价进行处理
						if(skuCountry!=''&& baoyou){
							if("fr"==skuCountry){
								value = "25";
							}else if("it"==skuCountry){
								value = "29";
							}else if("ca"==skuCountry){
								value = "25";
							}else if("us"==skuCountry){
								value = "49";
							}else if("uk"==skuCountry){
								value = "20";
							}else if("de"==skuCountry){
								value = "29";
							}else if("jp"==skuCountry){
								value = "2000";
							}
							if(value != ""){
								lastRowClone.find(".price").val(value);
							}
						 }
						 lastRowClone.find(".countryTips").text(account);
						 lastRowClone.find("select").select2();
						 lastRowClone.find(".salePrice").attr("id","salePrice" + iFlag);
						 lastRowClone.find(".priceInput").attr("id","price" + iFlag);
						 lastRowClone.find(".deleteB2b").attr("id","deleteB2b" + iFlag);
					 	lastRowClone.find(".businessPrice").attr("id","businessPrice" + iFlag);
					 	lastRowClone.find(".quantityPrice1").attr("id","quantityPrice1" + iFlag);
					 	lastRowClone.find(".quantityLowerBound1").attr("id","quantityLowerBound1" + iFlag);
					 	lastRowClone.find(".quantityPrice2").attr("id","quantityPrice2" + iFlag);
					 	lastRowClone.find(".quantityLowerBound2").attr("id","quantityLowerBound2" + iFlag);
					 	lastRowClone.find(".quantityPrice3").attr("id","quantityPrice3" + iFlag);
					 	lastRowClone.find(".quantityLowerBound3").attr("id","quantityLowerBound3" + iFlag);
					 	lastRowClone.find(".quantityPrice4").attr("id","quantityPrice4" + iFlag);
					 	lastRowClone.find(".quantityLowerBound4").attr("id","quantityLowerBound4" + iFlag);
					 	lastRowClone.find(".quantityPrice5").attr("id","quantityPrice5" + iFlag);
					 	lastRowClone.find(".quantityLowerBound5").attr("id","quantityLowerBound5" + iFlag);
					 	lastRowClone.find(".businessPriceCompare").attr("id","businessPriceCompare" + iFlag);
						 lastRowClone.find("select").val(newSku).trigger("change");
						 tableBody.append(lastRowClone);
	                }   
				 	if($('#contentTable tr').size()>2){
					  var row = $(this).parent().parent();
					  row.remove();
				 	}
				}else{
					//逐行对包邮调价进行处理
					var skuCountry ="";
					var account ="";
					if($(this).val()!=''&& $(this).val().indexOf("@")>=0){
						account =$(this).val().split("@")[0];
						skuCountry =account.split("_")[1].toLowerCase();
					}
					if(skuCountry!=''&& baoyou){
						if("fr"==skuCountry){
							value = "25";
						}else if("it"==skuCountry){
							value = "29";
						}else if("ca"==skuCountry){
							value = "25";
						}else if("us"==skuCountry){
							value = "49";
						}else if("uk"==skuCountry){
							value = "20";
						}else if("de"==skuCountry){
							value = "29";
						}else if("jp"==skuCountry){
							value = "2000";
						}
						if(value != ""){
							tr.find(".price").val(value);
						}
					 }
					 tr.find(".countryTips").text(account);
				}
				//没有开通的平台隐藏B2B按钮
				$(".btnB2B").each(function(){
					var obj = $(this);
					var parent = obj.parent().parent();
					var country = parent.find("span[class='countryTips']").text();
					if(!(country=='TDKRFSEB_US' || country=='Inateck_DE' || country=='Inateck_UK' || country=='Inateck_JP' || country=='Inateck_FR')){
						$(this).css('display','none');
					} else {
						$(this).css('display','block');
					}
				});
			});
			
			$("#inputForm").on("change","select[name='sku']",function(){
				var baoyou = false;
				if("包邮调价"==$("select[name='reason']").val()){
					baoyou = true;
				}
				if($(this).val()=='sku'){
					$(this).parent().find("input[type='text']").show();
					$(this).parent().find(".countryTips").text("");
				}else{
					$(this).parent().find("input[type='text']").hide();
					//加入参考价格，以及限制价格
					var rs = "";
					var price = $(this).parent().parent().find("input[name='price']");
					price.val('');
					var salePrice = $(this).parent().parent().find("input[name='salePrice']");
					salePrice.val('');
					var lirun = $(this).parent().parent().find("input[name='lirun']");
					var businessPriceCompare = $(this).parent().parent().find("input[name='businessPriceCompare']");
					salePrice.removeAttr("pricegt");
					salePrice.removeAttr("pricelt");
					salePrice.removeAttr("priceLimit");
					$.ajax({
	      			   type: "POST",
	      			   url: "${ctx}/amazoninfo/priceFeed/getPrice?countrySku="+$(this).val(),
	      			   async: false,
	      			   success: function(msg){
	      				   rs = msg;		
	      			   }
	          		});
					price.val(rs.price);
					salePrice.val(rs.salePrice);
					lirun.val(rs.ean);	//用ean属性存放利润率传递到前端
					businessPriceCompare.val(rs.businessPrice);
					var tip = "";
					if(rs.warnPrice && !baoyou){
						 salePrice.attr("pricegt",rs.warnPrice);
						 tip += "<span style='color:green'>最低:"+rs.warnPrice +"</span>;" ;
					}
					if(rs.highWarnPrice && !baoyou){
						 salePrice.attr("pricelt",rs.highWarnPrice);
						 tip += "<span style='color:red'>最高:"+rs.highWarnPrice +"</span>;";
					}
					if(rs.fnsku && !baoyou){
						salePrice.attr("priceLimit", rs.fnsku);
					}
					if(tip){
						//salePrice.parent().find(".tip").html("</br>"+tip);
					}
				}
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					var flag = true;
					var arr = "";
					var twoFlag=1;
					var keyStr="";
					var skuFlag=1;
					var fullFlag=1;
					var businessFlag=1;
					$(".salePrice").each(function(){
						var tr =$(this).parent().parent();
						arr = tr.find("select option:selected").text();
						if($(this).val()!=''){
							var price = tr.find(".priceInput").val();
							var salePrice = $(this).val();
							if(salePrice != null && parseFloat(salePrice) > parseFloat(price)){
								flag = false;
								return false;
							}
							//如果售价不为空，开始和结束时间也不能为空
							if(tr.find("input[name='saleStartDate']").val()==''||tr.find("input[name='saleEndDate']").val()==''){
								fullFlag=2;
								return false;
							}
							var businessPrice = tr.find(".businessPriceCompare").val();
							if(businessPrice!=null && salePrice != null && parseFloat(businessPrice) >= parseFloat(salePrice)){
								businessFlag = 2;
								return false;
							}
						}
						if(keyStr.indexOf(arr+",")>=0){
							keyStr=arr;
							twoFlag = 2;
							return false;
						}else{
							keyStr=keyStr+arr+",";
						};
						
						if(tr.find("input[name='sku']").val()&&tr.find("input[name='sku']").val().indexOf("@")<0){
							skuFlag=2;
							return false;
						};
						arr="";
					});
					
					if(skuFlag=="2"){
						top.$.jBox.error("手填sku必须加上国家，例如:com@87-FE2005-DE","错误");
						return false;
					}
					
					if(fullFlag=="2"){
						top.$.jBox.error("售价不为空，开始和结束时间也不能为空！","错误");
						return false;
					}
					if(twoFlag=="2"){
						top.$.jBox.error(keyStr+"同一sku同一国家只能有一条记录","错误");
						return false;
					}
					if(businessFlag=="2"){
						top.$.jBox.error(arr+"B2B价格必须小于售价,请修改售价或者B2B价格","错误");
						return false;
					}
					if(flag){
						top.$.jBox.confirm('确定要修改产品价格吗!','系统提示',function(v,h,f){
							if(v=='ok'){
								loading('正在提交，请稍等...');
								$("#contentTable tbody tr").each(function(i,j){
									if($(j).find("select").select2("val")!='sku'){
										$(j).find("select").attr("name","prices"+"["+i+"]."+$(j).find("select").attr("name"));
									}
									$(j).find("input[type!='']").each(function(){
											if($(this).attr("name")&&$(this).css("display")!='none'){
												$(this).attr("name","prices"+"["+i+"]."+$(this).attr("name"));
											}
									});
								});
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
							}
						},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}else{
						//top.$.jBox.error("价格必须为数字","错误");
						top.$.jBox.error("产品(" + arr + ")销售价格高于价格","错误");
					}
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		
		//修改售价时计算利润率
		function myChange(obj,type){
			if(obj.value==null || obj.value==""){
				return;
			}
			var parent =$(obj).parent().parent();
			var flag = true;	//是否计算利润率
			if(type==1){	//salePrice为空时price变更才计算
				var salePrice = parent.find("input[name='salePrice']").val();
				if(salePrice){
					flag = false;
				}
			} 
			if(flag){
				var sku = parent.find("select").val();
				$.ajax({
      			   type: "POST",
      			   url: "${ctx}/amazoninfo/priceFeed/getProfitRate?countrySku="+sku+"&salePrice="+obj.value,
      			   async: false,
      			   success: function(msg){
      				 parent.find("input[name='lirun']").val(msg);
      			   }
          		});
			}
		}
		
		//修改售价时计算利润率
		function deleteB2bChange(obj){
			if(obj.checked){
				$(obj).val("1");
			}else{
				$(obj).val("0");
			}
		}

		//显示B2B阶梯价格设置框
		function displayBusinessPrice(obj){
			var parent =$(obj).parent().parent();
			var countrySku = parent.find("select").val();
			if(countrySku==null || countrySku==''){
				return;
			}
			var businessDiv = $("#businessPriceDiv");
			var businessPrice = parent.find(".businessPrice").val();
			if(businessPrice==null || businessPrice==''){
				var rs = "";
				$.ajax({
	      			   type: "POST",
	      			   url: "${ctx}/amazoninfo/priceFeed/getPrice?countrySku="+countrySku,
	      			   async: false,
	      			   success: function(msg){
	      				   rs = msg;		
	      			   }
	          	});
				if(rs){
					$("#businessPriceInput").val(rs.businessPrice);
					$("#quantity1Input").val(rs.quantity1);
					$("#price1Input").val(rs.price1);
					$("#quantity2Input").val(rs.quantity2);
					$("#price2Input").val(rs.price2);
					$("#quantity3Input").val(rs.quantity3);
					$("#price3Input").val(rs.price3);
					$("#quantity4Input").val(rs.quantity4);
					$("#price4Input").val(rs.price4);
					$("#quantity5Input").val(rs.quantity5);
					$("#price5Input").val(rs.price5);
				}
			} else{
				$("#businessPriceInput").val(businessPrice);
				$("#quantity1Input").val(parent.find(".quantityLowerBound1").val());
				$("#price1Input").val(parent.find(".quantityPrice1").val());
				$("#quantity2Input").val(parent.find(".quantityLowerBound2").val());
				$("#price2Input").val(parent.find(".quantityPrice2").val());
				$("#quantity3Input").val(parent.find(".quantityLowerBound3").val());
				$("#price3Input").val(parent.find(".quantityPrice3").val());
				$("#quantity4Input").val(parent.find(".quantityLowerBound4").val());
				$("#price4Input").val(parent.find(".quantityPrice4").val());
				$("#quantity5Input").val(parent.find(".quantityLowerBound5").val());
				$("#price5Input").val(parent.find(".quantityPrice5").val());
			}
			businessDiv.find("h3").text(countrySku+" B2B阶梯价格");
			$("#businessSku").val(countrySku);
			$("#businessWarnPrice").val(parent.find("input[name='salePrice']").attr("pricegt"));
			businessDiv.modal();
		}

		//设置B2B阶梯价格
		function sureBusinessPrice(){
			var countrySku = $("#businessSku").val();
			if(countrySku==null || countrySku==''){
				return;
			}
			if(!valiForm()){
				return;
			}
			$("select[name='sku']").each(function(){
				var obj = $(this);
				if(obj.val()==countrySku){
					var parent = obj.parent().parent();
					parent.find(".businessPrice").val($("#businessPriceInput").val());
					parent.find(".quantityPrice1").val($("#price1Input").val());
					parent.find(".quantityLowerBound1").val($("#quantity1Input").val());
					parent.find(".quantityPrice2").val($("#price2Input").val());
					parent.find(".quantityLowerBound2").val($("#quantity2Input").val());
					parent.find(".quantityPrice3").val($("#price3Input").val());
					parent.find(".quantityLowerBound3").val($("#quantity3Input").val());
					parent.find(".quantityPrice4").val($("#price4Input").val());
					parent.find(".quantityLowerBound4").val($("#quantity4Input").val());
					parent.find(".quantityPrice5").val($("#price5Input").val());
					parent.find(".quantityLowerBound5").val($("#quantity5Input").val());
					parent.find(".businessPriceCompare").val($("#businessPriceInput").val());
					
				}
			});
			//确认之后清空数据框,下次重新加载
			$("#businessSku").val("");
			$("#businessWarnPrice").val("");
			$("#businessPriceInput").val("");
			$("#quantity1Input").val("");
			$("#price1Input").val("");
			$("#quantity2Input").val("");
			$("#price2Input").val("");
			$("#quantity3Input").val("");
			$("#price3Input").val("");
			$("#quantity4Input").val("");
			$("#price4Input").val("");
			$("#quantity5Input").val("");
			$("#price5Input").val("");
			$("#businessPriceDiv").modal("toggle");
		}
		
		function valiForm(){
			var tipTime = 3000;	//提示信息停留时间
			var businessWarnPrice = $("#businessWarnPrice").val();
			//校验阶梯价格设置
			var businessPriceInput = $("#businessPriceInput").val();
			if(!emptyStr(businessWarnPrice) && comparePrice(businessPriceInput, businessWarnPrice,'1')){
				top.$.jBox.tip("B2B价格小于预警价,请调整B2B价格！", 'error',{timeout:tipTime});
				return false;
			}
			var price1Input = $("#price1Input").val();
			var quantity1Input = $("#quantity1Input").val();
			if((emptyStr(price1Input) && !emptyStr(quantity1Input)) || (!emptyStr(price1Input) && emptyStr(quantity1Input))){
				top.$.jBox.tip("阶梯价1数量和价格不能只填一个！", 'error',{timeout:tipTime});
				return false;
			} else if(!emptyStr(price1Input) && !emptyStr(quantity1Input)){
				if(comparePrice(price1Input, businessWarnPrice,'1')){
					top.$.jBox.tip("阶梯价1小于预警价,请调整阶梯价1！", 'error',{timeout:tipTime});
					return false;
				} else if(comparePrice(businessPriceInput, price1Input,'0')){
					top.$.jBox.tip("阶梯价1大于或等于B2B价格或B2B价格为空！", 'error',{timeout:tipTime});
					return false;
				}
			}
			var price2Input = $("#price2Input").val();
			var quantity2Input = $("#quantity2Input").val();
			if((emptyStr(price2Input) && !emptyStr(quantity2Input)) || (!emptyStr(price2Input) && emptyStr(quantity2Input))){
				top.$.jBox.tip("阶梯价2数量和价格不能只填一个！", 'error',{timeout:tipTime});
				return false;
			} else if(!emptyStr(price2Input) && !emptyStr(quantity2Input)){
				if(comparePrice(price2Input, businessWarnPrice,'1')){
					top.$.jBox.tip("阶梯价2小于预警价,请调整阶梯价2！", 'error',{timeout:tipTime});
					return false;
				} else if(comparePrice(price1Input, price2Input,'0')){
					top.$.jBox.tip("阶梯价2大于或等于阶梯价1或阶梯价1为空！", 'error',{timeout:tipTime});
					return false;
				} else if(parseInt(quantity2Input) <= parseInt(quantity1Input)){
					top.$.jBox.tip("阶梯数量2必须大于阶梯数量1,请调整阶梯数量2！", 'error',{timeout:tipTime});
					return false;
				}
			}
			var price3Input = $("#price3Input").val();
			var quantity3Input = $("#quantity3Input").val();
			if((emptyStr(price3Input) && !emptyStr(quantity3Input)) || (!emptyStr(price3Input) && emptyStr(quantity3Input))){
				top.$.jBox.tip("阶梯价3数量和价格不能只填一个！", 'error',{timeout:tipTime});
				return false;
			} else if(!emptyStr(price3Input) && !emptyStr(quantity3Input)){
				if(comparePrice(price3Input, businessWarnPrice,'1')){
					top.$.jBox.tip("阶梯价3小于预警价,请调整阶梯价3！", 'error',{timeout:tipTime});
					return false;
				} else if(comparePrice(price2Input, price3Input,'0')){
					top.$.jBox.tip("阶梯价3大于或等于阶梯价2或阶梯价2为空！", 'error',{timeout:tipTime});
					return false;
				} else if(parseInt(quantity3Input) <= parseInt(quantity2Input)){
					top.$.jBox.tip("阶梯数量3必须大于阶梯数量2,请调整阶梯数量3！", 'error',{timeout:tipTime});
					return false;
				}
			}
			var price4Input = $("#price4Input").val();
			var quantity4Input = $("#quantity4Input").val();
			if((emptyStr(price4Input) && !emptyStr(quantity4Input)) || (!emptyStr(price4Input) && emptyStr(quantity4Input))){
				top.$.jBox.tip("阶梯价4数量和价格不能只填一个！", 'error',{timeout:tipTime});
				return false;
			} else if(!emptyStr(price4Input) && !emptyStr(quantity4Input)){
				if(comparePrice(price4Input, businessWarnPrice,'1')){
					top.$.jBox.tip("阶梯价4小于预警价,请调整阶梯价4！", 'error',{timeout:tipTime});
					return false;
				} else if(comparePrice(price3Input, price4Input,'0')){
					top.$.jBox.tip("阶梯价4大于或等于阶梯价3或阶梯价3为空！", 'error',{timeout:tipTime});
					return false;
				} else if(parseInt(quantity4Input) <= parseInt(quantity3Input)){
					top.$.jBox.tip("阶梯数量4必须大于阶梯数量3,请调整阶梯数量4！", 'error',{timeout:tipTime});
					return false;
				}
			}
			var price5Input = $("#price5Input").val();
			var quantity5Input = $("#quantity5Input").val();
			if((emptyStr(price5Input) && !emptyStr(quantity5Input)) || (!emptyStr(price5Input) && emptyStr(quantity5Input))){
				top.$.jBox.tip("阶梯价5数量和价格不能只填一个！", 'error',{timeout:tipTime});
				return false;
			} else if(!emptyStr(price5Input) && !emptyStr(quantity5Input)){
				if(comparePrice(price5Input, businessWarnPrice,'1')){
					top.$.jBox.tip("阶梯价5小于预警价,请调整阶梯价5！", 'error',{timeout:tipTime});
					return false;
				} else if(comparePrice(price4Input, price5Input,'0')){
					top.$.jBox.tip("阶梯价5大于或等于阶梯价4或阶梯价4为空！", 'error',{timeout:tipTime});
					return false;
				} else if(parseInt(quantity5Input) <= parseInt(quantity4Input)){
					top.$.jBox.tip("阶梯数量5必须大于阶梯数量4,请调整阶梯数量5！", 'error',{timeout:tipTime});
					return false;
				}
			}
			if(!emptyStr(businessWarnPrice) && emptyStr(price1Input)){
				top.$.jBox.tip("请至少设置一个阶梯价！", 'error',{timeout:tipTime});
				return false;
			}
			return true;
		}
		
		//price1是否小于price2
		function comparePrice(price1, price2, type){
			if(emptyStr(price2) && type==1){	//为1根预警价比较,预警价为空不比较
				return false;
			} else if(emptyStr(price1) && type==0) {	//前一阶梯为空,不通过
				return true;
			}else if(parseFloat(price1) < parseFloat(price2) && type==1){ //预警价可以相等
				return true;
			}else if(parseFloat(price1) <= parseFloat(price2) && type!=1){ //阶梯之间价格不可以相等
				return true;
			} else {
				return false;
			}
		}
		
		function emptyStr(s){
			return (s == null || s.trim() == '');
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/priceFeed/">产品价格管理列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/priceFeed/form">修改产品价格</a></li>
		<li><a href="${ctx}/amazoninfo/productPriceApproval">产品价格审批</a></li>
		<li><a href="${ctx}/amazoninfo/productPriceApproval/form">申请价格审批</a></li>
	</ul><br/>
	<form id="inputForm"  action="${ctx}/amazoninfo/priceFeed/save" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label" style="width:100px">平台:</label>
			<div class="controls" style="margin-left:120px">
				<select id="accountName" name="accountName" style="width: 400px" class="required" multiple class="multiSelect" >
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:forEach items="${accountMap[dic.value]}" var="account">
								<option value="${account}"  ${fn:contains(priceFeed.accountName,account)?'selected':''}>${account}</option>
							</c:forEach>
						</c:forEach>
					</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
					   <c:forEach items="${fns:getDictList('platform')}" var="dic">
					        <shiro:hasPermission name="amazoninfo:feedSubmission:${dic.value}">
							  <c:forEach items="${accountMap[dic.value]}" var="account">
								  <option value="${account}"  ${fn:contains(priceFeed.accountName,account)?'selected':''}>${account}</option>
							  </c:forEach>
							</shiro:hasPermission>  
						</c:forEach>
					</shiro:lacksPermission>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" style="width:100px">改价原由:</label>
			<div class="controls" style="margin-left:120px">
				<select  name="reason" style="width: 400px" class="required">
					<option></option>
					<option value="计划改价" ${priceFeed.reason eq '计划改价' ?'selected':''} >计划改价</option>
					<option value="汇率改价" ${priceFeed.reason eq '汇率改价' ?'selected':''} >汇率改价</option>
					<option value="防御性降价" ${priceFeed.reason eq '防御性降价' ?'selected':''}>防御性降价</option>
					<option value="积压降价" ${priceFeed.reason eq '积压降价' ?'selected':''}>积压降价</option>
					<option value="断货升价" ${priceFeed.reason eq '断货升价' ?'selected':''}>断货升价</option>
					<option value="促销调价" ${priceFeed.reason eq '促销调价' ?'selected':''}>促销调价</option>
					<option value="包邮调价" ${priceFeed.reason eq '包邮调价' ?'selected':''}>包邮调价(15分钟自动改回原价)</option>
				</select>
			</div>
		</div>	
		<span id='showMsg' style='color:#EE6363;'></span>
		<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" id="add-row"><span class="icon-plus"></span>新增产品</a></div>
		<div class="control-group">
			<label class="control-label" style="width:100px">产品价格:</label>
			<div class="controls" style="margin-left:120px">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="width: 40%">产品</th>
							<%-- <th style="width: 10%">价格</th>--%>
							<th style="width: 10%">销售价格</th>
							<th style="width: 10%">利润率(%)</th>
							<th style="width: 15%">开始时间</th>
							<th style="width: 15%">结束时间</th>
							<th style="width: 10px">删除B2B</th>
							<th style="width: 10px">操作</th>
						</tr>
					</thead>
					<tbody>
					<c:choose>
						<c:when test="${not empty priceFeed.prices}">
							<c:forEach items="${priceFeed.prices}" var="itemP">
								<tr>
								<td class="sku">
								<span style="font-weight:bold;font-size: 18px;padding-right:5px" class="countryTips">${itemP.countryTips}</span>
								<select style="width: 90%;margin-left:20px" name="sku" class="required">
									<option value=""></option>
									<c:forEach items="${sku}" var="item">
										<option value="${item.key}" ${itemP.sku eq item.key ?'selected':''}>${item.value}</option>									
									</c:forEach>
									<c:if test="${fn:length(sku)>0}">
										<option value="sku">手动填写Sku</option>
									</c:if>
								</select>
								<input name="sku" type="text" style="margin-top:5px;display: none;"/>
								</td>
								<%-- <td><input type="text" style="width: 80%" id="price0" name="price" class="price required priceInput" onchange=myChange(this,1) value="${itemP.price}"/></td>--%>
								<td><input type="text" style="width: 80%" id="salePrice0" name="salePrice" class="price required salePrice" onchange=myChange(this,2)  value="${itemP.salePrice}"/><span class="tip"></span></td>
								<td><input type="text" style="width: 80%" id="lirun0" readonly="true" name="lirun" class="lirun"/></td>
								<td><input style="width:80%" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"  value='<fmt:formatDate value="${saleStartDate}" pattern="yyyy-MM-dd"/>' class="Wdate" readonly="readonly" type="text" name="saleStartDate" class="input-small"/></td>
								<td><input style="width:80%" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"  value='<fmt:formatDate value="${saleEndDate}" pattern="yyyy-MM-dd"/>' class="Wdate" readonly="readonly" type="text" name="saleEndDate" class="input-small"/></td>
								<td><input type="checkbox" ${itemP.deleteB2b eq '1'?'checked':''} value="${itemP.deleteB2b }" name="deleteB2b" class="deleteB2b" id="deleteB2b0" onClick="deleteB2bChange(this)"/></td>
								<td>
								<a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a>
								<input type="button" class="btn btn-small btn-info btnB2B" onclick="displayBusinessPrice(this)" value="B2B"/>
									<input type="hidden" id="businessPrice0" name="businessPrice" value="${itemP.businessPrice}" class="businessPrice"/>
									<input type="hidden" id="quantityPrice10" name="quantityPrice1" value="${itemP.quantityPrice1}" class="quantityPrice1"/>
									<input type="hidden" id="quantityLowerBound10" name="quantityLowerBound1" value="${itemP.quantityLowerBound1}" class="quantityLowerBound1"/>
									<input type="hidden" id="quantityPrice20" name="quantityPrice2" value="${itemP.quantityPrice2}" class="quantityPrice2"/>
									<input type="hidden" id="quantityLowerBound20" name="quantityLowerBound2" value="${itemP.quantityLowerBound2}" class="quantityLowerBound2"/>
									<input type="hidden" id="quantityPrice30" name="quantityPrice3" value="${itemP.quantityPrice3}" class="quantityPrice3"/>
									<input type="hidden" id="quantityLowerBound30" name="quantityLowerBound3" value="${itemP.quantityLowerBound3}" class="quantityLowerBound3"/>
									<input type="hidden" id="quantityPrice40" name="quantityPrice4" value="${itemP.quantityPrice4}" class="quantityPrice4"/>
									<input type="hidden" id="quantityLowerBound40" name="quantityLowerBound4" value="${itemP.quantityLowerBound4}" class="quantityLowerBound4"/>
									<input type="hidden" id="quantityPrice50" name="quantityPrice5" value="${itemP.quantityPrice5}" class="quantityPrice5"/>
									<input type="hidden" id="quantityLowerBound50" name="quantityLowerBound5" value="${itemP.quantityLowerBound5}" class="quantityLowerBound5"/>
								</td>
							</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
							<td class="sku">
							<span style="font-weight:bold;font-size: 18px;padding-right:5px" class="countryTips"></span>
							<select style="width: 90%;margin-left:20px" name="sku" class="required">
								<option value=""></option>
								<c:forEach items="${sku}" var="item">
									<option value="${item.key}">${item.value}</option>									
								</c:forEach>
								<c:if test="${fn:length(sku)>0}">
									<option value="sku">手动填写Sku</option>
								</c:if>
							</select>
							<input name="sku" type="text" style="margin-top:5px;display: none;"/>
							</td>
							<%-- <td><input type="text" style="width: 80%" id="price0" name="price" onchange=myChange(this,1) class="price required priceInput"/></td>--%>
							<td><input type="text" style="width: 80%" id="salePrice0" name="salePrice" onchange=myChange(this,2) class="price required salePrice" /><span class="tip"></span></td>
							<td><input type="text" style="width: 80%" id="lirun0" readonly="true" name="lirun" class="lirun"/></td>
							<td><input style="width:80%" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly" value="<fmt:formatDate value="${saleStartDate}" pattern="yyyy-MM-dd"/>" class="Wdate" type="text" name="saleStartDate" class="input-small"/></td>
							<td><input style="width:80%" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly" value="<fmt:formatDate value="${saleEndDate}" pattern="yyyy-MM-dd"/>" class="Wdate" type="text" name="saleEndDate" class="input-small"/></td>
							<td><input type="checkbox" value="0" name="deleteB2b" class="deleteB2b" id="deleteB2b0" onClick="deleteB2bChange(this)"/></td>
							<td>
								<a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a>
								<input type="button" class="btn btn-small btn-info btnB2B" onclick="displayBusinessPrice(this)" value="B2B"/>
									<input type="hidden" id="businessPrice0" name="businessPrice" class="businessPrice"/>
									<input type="hidden" id="quantityPrice10" name="quantityPrice1" class="quantityPrice1"/>
									<input type="hidden" id="quantityLowerBound10" name="quantityLowerBound1" class="quantityLowerBound1"/>
									<input type="hidden" id="quantityPrice20" name="quantityPrice2" class="quantityPrice2"/>
									<input type="hidden" id="quantityLowerBound20" name="quantityLowerBound2" class="quantityLowerBound2"/>
									<input type="hidden" id="quantityPrice30" name="quantityPrice3" class="quantityPrice3"/>
									<input type="hidden" id="quantityLowerBound30" name="quantityLowerBound3" class="quantityLowerBound3"/>
									<input type="hidden" id="quantityPrice40" name="quantityPrice4" class="quantityPrice4"/>
									<input type="hidden" id="quantityLowerBound40" name="quantityLowerBound4" class="quantityLowerBound4"/>
									<input type="hidden" id="quantityPrice50" name="quantityPrice5" class="quantityPrice5"/>
									<input type="hidden" id="quantityLowerBound50" name="quantityLowerBound5" class="quantityLowerBound5"/>
									<input type="hidden" id="businessPriceCompare0" name="businessPriceCompare" class="businessPriceCompare"/>
							</td>
						</tr>
						</c:otherwise>
					</c:choose>
					</tbody>
				</table>
			</div>
		</div>	
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提  交"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form>
	
	<div id="businessPriceDiv" data-backdrop="static" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3>B2B阶梯价格设置</h3>
		</div>
		<div class="modal-body">
			<table class="table-striped table-bordered table-condensed ajaxtable">
				<tr>
					<td>B2B价格</td>
					<td>
						<input type="text" style="width: 20%" id="businessPriceInput" name="businessPriceInput" class="number"/>
						<input type="hidden" id="businessSku" name="businessSku"/>
						<input type="hidden" id="businessWarnPrice" name="businessWarnPrice"/>
					</td>
				</tr>
				<tr>
					<td>阶梯价1</td>
					<td>
						购买数量：<input type="text" style="width: 20%" id="quantity1Input" name="quantity1Input" class="number"/>
						售价：<input type="text" style="width: 20%" id="price1Input" name="price1Input" class="price"/>
					</td>
				</tr>
				<tr>
					<td>阶梯价2</td>
					<td>
						购买数量：<input type="text" style="width: 20%" id="quantity2Input" name="quantity2Input" class="number"/>
						售价：<input type="text" style="width: 20%" id="price2Input" name="price2Input" class="price"/>
					</td>
				</tr>
				<tr>
					<td>阶梯价3</td>
					<td>
						购买数量：<input type="text" style="width: 20%" id="quantity3Input" name="quantity3Input" class="number"/>
						售价：<input type="text" style="width: 20%" id="price3Input" name="price3Input" class="price"/>
					</td>
				</tr>
				<tr>
					<td>阶梯价4</td>
					<td>
						购买数量：<input type="text" style="width: 20%" id="quantity4Input" name="quantity4Input" class="number"/>
						售价：<input type="text" style="width: 20%" id="price4Input" name="price4Input" class="price"/>
					</td>
				</tr>
				<tr>
					<td>阶梯价5</td>
					<td>
						购买数量：<input type="text" style="width: 20%" id="quantity5Input" name="quantity5Input" class="number"/>
						售价：<input type="text" style="width: 20%" id="price5Input" name="price5Input" class="price"/>
					</td>
				</tr>
			</table>
		</div>
		<div class="modal-footer">
			<input type="button" class="btn btn-primary" onclick="sureBusinessPrice()" value="OK"/>
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
	</div>
</body>
</html>