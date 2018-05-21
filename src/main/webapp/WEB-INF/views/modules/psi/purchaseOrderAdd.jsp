<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>新建采购订单</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
	function jiaoji(str1, str2){
		var a1 = str1.split(','),a2 = str2.split(','),l1 = a1.length,l2 = a2.length;
		var ret = [],tmp = {},ta,tb;
		 
		for(var i=0;i<l1;i++){
		    ta = a1[i];
		    if(ta in tmp){if(tmp[ta]==2){ret.push(ta);}continue;}
		    tmp[ta] = 1;
		}
		 
		for(i=0;i<l2;i++){
		    tb = a2[i];
		    if(tb in tmp){if(tmp[tb]==1){ret.push(tb);}continue;}
		    tmp[tb] = 2;
		}
		tmp = null;
		return ret.join(",");
	}
	 
		
		$(document).ready(function() {
			eval('var receivedMap=${receivedMap}');
			new tabTableInput("inputForm","text");
			var countryMap = [];
			<c:forEach items="${fns:getDictList('platform')}" var="dic">
				countryMap['${dic.value}'] = '${dic.label}';
			</c:forEach>
			
			var productArgs ={};
			var colorArgs =[];
			var countryArgs=[];
			var colorsStr;
			var countryStr;
			var product;
		<c:forEach items="${products}" var="product" varStatus="i">
		  	product={};
		  	product.id='${product.id}';
		  	product.packQuantity='${product.packQuantity}';   
		  	product.color='${product.color}';
		  	product.platform ='${product.platform}';
		  	product.name = '${product.name}';   
		  	product.colorPlatform ='${product.colorPlatform}';
		  	productArgs['${product.id}']=product;
		</c:forEach>
		
		
			$("#supplier").on("change",function(e){
				var params = {};
				params['supplier.id'] = $(this).val();
				window.location.href = "${ctx}/psi/purchaseOrder/add?"+$.param(params);
			});
			
			$("#inputForm").on("change",".colors",function(e){
				var $this = $(this);
				var selectColor = "";
				$this.children("option:selected").each(function(){
					selectColor = selectColor + $(this).text() + ",";
		        });
				
				if(selectColor != "" && selectColor != "No color,"){
					var tbody = $this.parent().parent().parent();
					var productVal = tbody.find("select[name='product.id']").select2().val();
					var colorPlatformStr = productArgs[productVal].colorPlatform;
					var countryStr = "";
					var colorPlatform = colorPlatformStr.split(';');
					if(colorPlatform.length > 1){
						var selectArgs = selectColor.split(',');
						$(selectArgs).each(function(i,data){
							if(i == 0) {
								for(var j=0;j<colorPlatform.length;j++){
									if(data == colorPlatform[j].split('_')[0]){
										countryStr = colorPlatform[j].split('_')[1];
										break;
									}
								}
							} else {
								for(var j=0;j<colorPlatform.length;j++){
									if(data == colorPlatform[j].split('_')[0]){
										var dataPlatform = colorPlatform[j].split('_')[1];
										countryStr = jiaoji(countryStr,dataPlatform);
									}
								}
							}
						});
						tbody.find("select[name='countryCode']").select2("data",[]);
						tbody.find("select[name='countryCode']").select2().empty();
						tbody.find("select[name='countryCode']").select2().text("");
						
						if(countryStr.length > 0){
							countryArgs=countryStr.split(',');
							$(countryArgs).each(function(i,data){
								tbody.find("select[name='countryCode']").select2().append("<option value='"+data+"'>"+countryMap[data]+"</option>");
							});
						} else {
							top.$.jBox.tip("该颜色组合没有共同的在售平台,请分开下单！", 'info',{timeout:1000});
						}
					}
				}
			});
			
			$("#inputForm").on("change",".newcolors",function(e){
				var $this = $(this);
				var selectColor = "";
				$this.children("option:selected").each(function(){
					selectColor = selectColor + $(this).text() + ",";
		        });
				
				if(selectColor != "" && selectColor != "No color,"){
					var tbody = $this.parent().parent().parent();
					var productVal = tbody.find("select[name='product.id']").select2().val();
					var colorPlatformStr = productArgs[productVal].colorPlatform;
					var countryStr = "";
					var colorPlatform = colorPlatformStr.split(';');
					if(colorPlatform.length > 1){
						var selectArgs = selectColor.split(',');
						$(selectArgs).each(function(i,data){
							if(i == 0) {
								for(var j=0;j<colorPlatform.length;j++){
									if(data == colorPlatform[j].split('_')[0]){
										countryStr = colorPlatform[j].split('_')[1];
										break;
									}
								}
							} else {
								for(var j=0;j<colorPlatform.length;j++){
									if(data == colorPlatform[j].split('_')[0]){
										var dataPlatform = colorPlatform[j].split('_')[1];
										countryStr = jiaoji(countryStr,dataPlatform);
									}
								}
							}
						});
						var tr = $this.parent().parent();
						tr.find("select[name='countryCode']").select2("data",[]);
						tr.find("select[name='countryCode']").select2().empty();
						tr.find("select[name='countryCode']").select2().text("");
						
						if(countryStr.length > 0){
							countryArgs=countryStr.split(',');
							$(countryArgs).each(function(i,data){
								tr.find("select[name='countryCode']").select2().append("<option value='"+data+"'>"+countryMap[data]+"</option>");
							});
						} else {
							top.$.jBox.tip("该颜色组合没有共同的在售平台,请分开下单！", 'info',{timeout:1000});
						}
					}
				}
			});
			
			$("#inputForm").on("change",".productId",function(e){
				var removeVal = e.removed.id;
				var $this = $(this);
				var productVal = $this.val();
				$("select.productId").each(function(){
    				if($(this).val()!=productVal){
    					$(this).find("option[value="+productVal+"]").remove();    					
    					$(this).append("<option value='"+removeVal+"'>"+productArgs[removeVal].name+"</option>");
    				}
    			});
				
				var  tbody = $this.parent().parent().parent();
				tbody.find("select[name='colorCode']").select2("data",[]);
				tbody.find("select[name='countryCode']").select2("data",[]);
				tbody.find("select[name='colorCode']").select2().empty();
				tbody.find("select[name='countryCode']").select2().empty();
				
				var productName =$this.children('option:selected').text();
				tbody.find("input[name='productName']").val(productName);
				tbody.find("input[name='product.id']").val(productVal);
				colorsStr=productArgs[productVal].color;
				countryStr=productArgs[productVal].platform;
				if(colorsStr==null||colorsStr==""){
					tbody.find("select[name='colorCode']").each(function(){
						$(this).select2().append("<option value='No color'>No color</option>").select2("val","No color");
					});
				}else{
					colorArgs=colorsStr.split(',');
					$(colorArgs).each(function(i,data){
						tbody.find("select[name='colorCode']").select2().append("<option value='"+data+"' >"+data+"</option>");
					});
				}
				countryArgs=countryStr.split(',');
				$(countryArgs).each(function(i,data){
					tbody.find("select[name='countryCode']").select2().append("<option value='"+data+"'>"+countryMap[data]+"</option>");
				}); 
				
			    var actualDeliveryDate = receivedMap[productVal];
			    tbody.find("input[name='actualDeliveryDate']").val(actualDeliveryDate);
			    tbody.find("input[name='deliveryDate']").val(actualDeliveryDate);
			    
				tbody.find(".packQuantity").val(productArgs[productVal].packQuantity);
				
	            
			});
			
			
			$("#inputForm").on("blur",".Wdate1",function(){
				var  tbody = $(this).parent().parent().parent();
				var dateVal = $(this).val();
				tbody.find("input[name='actualDeliveryDate']").val(dateVal);
			});
			
			$('#add-row').on('click', function(e){
				//如果当前添加的个数大于  产品个数  则不让添加
				//if("${fn:length(products)}"<=$("tbody").length){
				//	top.$.jBox.tip("产品类型数已达最大，请追加单品 ！", 'info',{timeout:3000});
				//	return false;
				//};
			    e.preventDefault();
			    var table = $('#contentTable');
	           	var tbody =$("<tbody></tbody>");
	            var tr = $("<tr></tr>");
	            var td ="<td> <input type='hidden' name='productName'/><select style='width: 90%' class='productId'  name='product.id' >";
	            var i = 0 ;
	            for (var key in productArgs) {
	            	if($(".productId[value="+key+"]").size()==0){
	            		if(i==0){
	            			$("select.productId").each(function(){
	            				$(this).find("option[value="+key+"]").remove();
	            			});
	            		}
	            		td = td.concat("<option value='"+key+"'>"+productArgs[key].name+"</option>");
	            		i++;
	            	}	
				}
	            td = td.concat("</select></td>");
	            tr.append(td);
				tr.append("<td> <input type='text' readonly='readonly' class='packQuantity' style='width: 80%' /></td>");
				tr.append("<td> <input style='width:100px'  readonly='true'  type='text'   name='deliveryDate'  /></td>");
	            tr.append("<td> <input style='width:100px'   type='text'  class='Wdate1 Wdate required'  name='actualDeliveryDate'   id='actualDeliveryDate' pattern='yyyy-MM-dd' /></td>");
	            tr.append("<td> <span><select name='colorCode' multiple=true class='multiSelect colors' style='width:100%'/></span></td>");
	            tr.append("<td> <span><select name='countryCode'   multiple=true class='multiSelect' style='width:100%'/></span></td>");
	            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='quantityOrdered' class='number' /></td>");
	            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='quantityOffOrdered' class='number' value='0' /></td>");
	            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='itemPrice' class='price' /></td>");
	            tr.append("<td> <input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
	            tr.append("<td> </td>");
	            tr.append("<td><div style='float:left'><a href='#' id='add-row-single'><span class='icon-plus'></span>追加单品</a></div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除产品</a></td>");
	           
	            tr.find("select[name='product.id']").select2();
	            tbody.append(tr);
	            
			 
	            //获取选中的text值
			    var productName=tbody.find("tr:first>td:first>select").children('option:selected').text();
			    tbody.find("input[name='productName']").val(productName);
			    
			    //获取第一个tr 第一个td的select值 
	            var productId=tbody.find("tr:first>td:first>select").val();
			    var actualDeliveryDate = receivedMap[productId];
			    tbody.find("input[name='deliveryDate']").val(actualDeliveryDate);
			    tbody.find("input[name='actualDeliveryDate']").val(actualDeliveryDate);
			    
			    product=productArgs[productId];
	            //填充装箱数
	            tbody.find(".packQuantity").val(product.packQuantity);
	            
	        
				
			   var colorStr=product.color;
	           if(colorStr==null||colorStr==""){
	        	   tbody.find("select[name='colorCode']").select2().append("<option value=''>No color</option>").select2("val","No color");
			   }else{
				$(product.color.split(",")).each(function(i,data){
					tbody.find("select[name='colorCode']").select2().append("<option value='"+data+"' >"+data+"</option>");
				 }); 
			  }
			   
	        			   
				$(product.platform.split(",")).each(function(i,data){
					tbody.find("select[name='countryCode']").select2().append("<option value='"+data+"' >"+countryMap[data]+"</option>");
				});  
	           
	            table.append(tbody);
			});
			
			$('#add-row').click();
			
			$('#contentTable').on('click', '.remove-row', function(e){
				  e.preventDefault();
				  if($('#contentTable tbody').size()>1){
					  var tbody = $(this).parent().parent().parent();
					  var id = tbody.find(".productId").select2("val");
					  tbody.remove();
					  if(id){
						  $("select.productId").each(function(){
		          				$(this).append("<option value='"+id+"'>"+productArgs[id].name+"</option>");
		          		  });
					  }
				  }
			});
			
			$('#remove-row-single').live('click',function(e){
				  e.preventDefault();
				  var tr = $(this).parent().parent();
				  tr.remove();
			});
			
			
			$("input[name='quantityOrdered']").live("blur",function(){
				var tr = $(this).parent().parent();
				var supplierId = $("select[name='supplier.id']").val();
				var currencyType = $("select[name='currencyType']").val();
				var productId = tr.find("select[name='product.id']").children("option:selected").val();
				var color =""  ;
				if(tr.find("select[name='colorCode']").val()){
					var arr=tr.find("select[name='colorCode']").val();
					if(arr[0]){
						color =arr[0];
					}
				};
				var tbody=tr.parent();
				var productQ = 0;
				tbody.find("input[name='quantityOrdered']").each(function(){
					if($(this).val()){
						productQ=productQ+parseInt($(this).val());
					}
				});
				var price=ajaxGetPrice(supplierId,currencyType,productId,color,productQ);
				tbody.find("input[name='quantityOrdered']").each(function(){
					$(this).parent().parent().find("input[name='itemPrice']").val(price);
				});
			});
			
			$('#add-row-single').live('click',function(e){
				  e.preventDefault();
				  var tbody =$(this).parent().parent().parent().parent();
				   var newTr =$("<tr class='newTr'></tr>");
				   newTr.append("<td><input type='hidden' name='product.id'/> <input type='hidden' name='productName' /><input type='hidden' name='deliveryDate'/><input type='hidden' name='actualDeliveryDate'/><input type='hidden' class='packQuantity'/><select name='colorCode'   multiple=true style='width:100%' class='multiSelect newcolors'/></td>");
				   newTr.append("<td><select name='countryCode' multiple=true style='width:100%' class='multiSelect' /></td>");
				   newTr.append("<td><input type='text' maxlength='11' style='width: 80%'  name='quantityOrdered' class='number' /></td>");
				   newTr.append("<td><input type='text' maxlength='11' style='width: 80%'  name='quantityOffOrdered' class='number' value='0'/></td>");
				   newTr.append("<td><input type='text' maxlength='11' style='width: 80%'  name='itemPrice' class='price' /></td>");
				   newTr.append("<td><input type='text' maxlength='200' style='width: 80%' name='remark' /></td>");
				   newTr.append("<td><a href='#' id='remove-row-single'><span class='icon-minus'></span>删除</a></td>");
				   tbody.append(newTr);
				   //当前td合并行
				   $(this).parent().parent().attr("rowSpan", tbody[0].rows.length).css("vertical-align","middle");
				   //合并第一第二列的行
				   $(this).parent().parent().parent().each(function(){
					    $(this).find("td:first").attr("rowSpan", tbody[0].rows.length).css("vertical-align","middle");
					    $(this).find("td:first").next("td").attr("rowSpan", tbody[0].rows.length).css("vertical-align","middle");
					    $(this).find("td:first").next("td").next("td").attr("rowSpan", tbody[0].rows.length).css("vertical-align","middle");
					    $(this).find("td:first").next("td").next("td").next("td").attr("rowSpan", tbody[0].rows.length).css("vertical-align","middle");
					});
				   
				   //获取第一个tr 第一个td的select值 
				   var productId=tbody.find("tr:first>td:first>select").val();
				   var packQuantity = tbody.find("tr:first>td:first").next("td").find(".packQuantity").val();
				   var productName=tbody.find("tr:first>td:first>select").children('option:selected').text();
				   
				   
		            
				    //获取第一个tr的时间值
				    var dayStrPo =tbody.find("tr:first>td:eq(2)>input").val();
				    var dayStr =tbody.find("tr:first>td:eq(3)>input").val();
				    newTr.find("input[name='product.id']").val(productId);
				    newTr.find("input[name='deliveryDate']").val(dayStrPo);
				    newTr.find("input[name='actualDeliveryDate']").val(dayStr);
				    newTr.find("input[name='productName']").val(productName);
				    newTr.find(".packQuantity").val(packQuantity);
				    
				    product=productArgs[productId];
				    
				   var colorStr=product.color;
		           if(colorStr==null||colorStr==""){
		        	   newTr.find("select[name='colorCode']").select2().append("<option value='No color'>No color</option>").select2("val","No color");
					}else{
						$(product.color.split(",")).each(function(i,data){
					   newTr.find("select[name='colorCode']").select2().append("<option value='"+data+"' >"+data+"</option>");
						}); 
					}
				   
					$(product.platform.split(",")).each(function(i,data){
						newTr.find("select[name='countryCode']").select2().append("<option value='"+data+"' >"+countryMap[data]+"</option>");
					});  
				  
			});
			
			$(".Wdate1").live("click", function (){
			 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			
			$("#btnSureSubmit").on('click',function(e){
				 if($("#inputForm").valid()){
					 top.$.jBox.confirm('确认要申请审核？申请后将发送邮件通知审核人！','系统提示',function(v,h,f){
							if(v=='ok'){
								$("#toReview").val("1");
								$("#inputForm").submit();
							}
							return true;
							},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				 }else{
					 return false;
				 };
				
			});	
			
			
			$("#inputForm").validate({
				rules:{
					"colorCode":{
						"required":true,
					},
					"countryCode":{
						"required":true,
					},
					"quantityOrdered":{
						"required":true,
					},
					"quantityOffOrdered":{
						"required":true,
					},
					"actualDeliveryDate":{
						"required":true,
					}
				},
				messages:{
					"actualDeliveryDate":{"required":'预计交货不能为空'},
					"colorCode":{"required":'颜色不能为空'},
					"countryCode":{"required":'国家不能为空'},
					"quantityOrdered":{"required":'总数量不能为空'},
					"quantityOffOrdered":{"required":'线下数不能为空'}
				},
				submitHandler: function(form){
					var productStr="";
					var twoStr="";
					var flagInt=1;
					var off=1;
					$("input[name='quantityOrdered']").each(function(e){
						var tr=$(this).parent().parent();
						var packingQuantity = tr.find(".packQuantity").val();
						var a=$(this).val()%packingQuantity;
						if(a!=0){
							twoStr=tr.find("select.productId").children('option:selected').text();
							if(flagInt==1){
								flagInt=2;
							}
						};
						if(parseInt($(this).val())<parseInt(tr.find("input[name='quantityOffOrdered']").val())){
							twoStr=tr.find("select.productId").children('option:selected').text();
							off=2;
							return false;
						}
					});
					
					if(off==2){
						top.$.jBox.tip("线下数不能大于总数量"+twoStr, 'info',{timeout:3000});
						return false;
					}
					
					if(flagInt==2){
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
								
								$("option[value='No color']").each(function(){
									$(this).val("");
								});
								
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
								$("#btnSureSubmit").attr("disabled","disabled");
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
						
						$("option[value='No color']").each(function(){
							$(this).val("");
						});
						
						form.submit();
						$("#btnSubmit").attr("disabled","disabled");
						$("#btnSureSubmit").attr("disabled","disabled");
					}
					
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
				}
			});
		});
		
		function  ajaxGetPrice(supplierId,currency,productId,color,productQ){
			var price="";
			$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/psi/purchaseOrder/ajaxPrice' ,
				    data: {
				    	"productId":productId,
				    	"supplierId":supplierId,
				    	"currency":currency,
				    	"quantity":productQ,
				    	"color":color
				    },
				    dataType: 'json',
				    success:function(data){ 
				    	price=data.price;
			        }
			});
			return price;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/purchaseOrder/">采购订单列表</a></li>
		<li class="active"><a href="#">新建采购订单</a></li>
	</ul>
	<br/>
	
	<form:form id="inputForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/addSave" method="post" class="form-horizontal">
	<input type="hidden" name="toReview" value="0" id="toReview"/>
	<input type='hidden' name="receivedStore" value="中国本地A" />
	    <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label"><b>供应商</b>:</label>
				<div class="controls" >
				<span>
					<select style="width:150px;" id="supplier" name="supplier.id">
						<c:forEach items="${suppliers}" var="supplier" varStatus="i">
							 <option value='${supplier.id}' ${purchaseOrder.supplier.id eq supplier.id ?'selected' :''}>${supplier.nikename}</option>;
						</c:forEach>
					</select>
				</span>
				
				</div>
			</div>
			<div class="control-group"  style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>定金</b>:</label>
				<div class="controls" >
					<div class="input-prepend input-append">
						<input  type="text" class="number required" style="width:40%;" name="deposit" value="${purchaseOrder.deposit}" /><span class="add-on">%</span>
					</div>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<div class="controls">
				</div>
			</div>
			
		</div>	
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>跟单员</b>:</label>
				<div class="controls">
					<select style="width: 90%" id="merchandiser" class='merchandiser' name="merchandiser.id" >
					<c:forEach items="${users}" var="user" varStatus="i">
						<option value='${user.id}' ${fns:getUser().id==user.id?'selected':''}>${user.name}</option>;
					</c:forEach>
				</select>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:30%;height:30px" >
				<label class="control-label"><b>下单日期</b>:</label>
				<div class="controls">
					<input  type="text"  style="width:60%"   class="Wdate required"  name="purchaseDate"   id="purchaseDate" value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd" />" />
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:40%;height:30px" >
				<label class="control-label"><b>货币类型</b>:</label>
				<div class="controls">
					<select name="currencyType" class="required">
						<option value="USD" ${purchaseOrder.currencyType eq 'USD'?'selected':''}>USD</option>
						<option value="CNY" ${purchaseOrder.currencyType eq 'CNY'?'selected':''}>CNY</option>
					</select>
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
				   <th style="width: 5%">装箱数</th>
				   <th style="width: 8%">PO交期</th>
				   <th style="width: 8%">预计交货</th>
				   <th style="width: 10%">颜色</th>
				   <th style="width: 10%">国家</th>
				   <th style="width: 6%">总数量</th>
				   <th style="width: 6%">线下数</th>
				   <th style="width: 6%">价格</th>
				   <th style="width: 8%">备注</th>
				   <th style="width: 5%">单品操作</th>
				   <th style="width: 10%">操作</th>
				   
			</tr>
		</thead>
		
	</table>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSureSubmit" class="btn btn-primary" type="button" value="申请审核"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
