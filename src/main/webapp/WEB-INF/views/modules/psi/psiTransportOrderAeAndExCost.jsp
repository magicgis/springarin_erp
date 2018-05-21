<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
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
			  	product.platform ='${product.platform}';
			  	productArgs['${product.id}']=product;
			</c:forEach>
			
			var model =$("#model").val();
			
			$('#localFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#tranFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#dapFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#otherFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#otherFile1').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#insuranceFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#taxFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$(".rate").on("blur",function(){
				if($(this).val()!=''){
					var div = $(this).parent().parent().parent();
					if(div.find(".firstAmount").val()!=''){
						div.find(".afterAmount").val(toDecimal(parseFloat(div.find(".firstAmount").val())*parseFloat($(this).val())));
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
					div.find(".afterAmount").val(toDecimal(parseFloat(rate)*parseFloat($(this).val())));
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
						$("input[name='tranAmount']").val(toDecimal(parseFloat(unitPrice)*$(this).val()));
						changeTranAmount();				
					};
				}
				//是快递的时候算出运输费用
				if(model=='2'){
					var totalAmount =$("#totalAmount").val();
					var localAmount =$("#localAmount").val();
					var otherAmount =$("#otherAmount").val()==""?0:$("#otherAmount").val();
					var otherAmount1 =$("#otherAmount1").val()==""?0:$("#otherAmount1").val();
					if(totalAmount!=''&&localAmount!=''&&$(this).val()!=''){
						$("input[name='tranAmount']").val(toDecimal(parseFloat(totalAmount)-parseFloat(localAmount)-parseFloat(otherAmount)-parseFloat(otherAmount1)));
						$("#unitPrice").val(toDecimal((parseFloat(totalAmount)-parseFloat(localAmount)-parseFloat(otherAmount)-parseFloat(otherAmount1))/parseFloat($(this).val())));
						changeTranAmount();
					}
				}
				
				
				
			})
			
			$(".productQuantity").live("blur",function(){
					var tbody =$(this).parent().parent().parent();
					var boxNumber =0;
					tbody.find("tr").each(function(){
						if($(this).find(".packQuantity").val()!=''&&$(this).find(".productQuantity").val()!=''){
							boxNumber = boxNumber+(parseInt($(this).find(".productQuantity").val())/parseInt($(this).find(".packQuantity").val()));
						}
					});
					
					$("input[name='boxNumber']").val(boxNumber);
				})
			
			$("#unitPrice").on("blur",function(){
				//是空运的时候算出运输费用
				if(model=='0'){
					var weight=$("#weight").val();
					if(weight&&weight!=''&&$(this).val()!=''){
						$("input[name='tranAmount']").val(toDecimal(parseFloat(weight)*$(this).val()));
						//光标移动到tranAmount上
						changeTranAmount();	
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
				  var  tr = $(this).parent().parent().parent();
					//ajax  获取UnitPrice
				  var productId=tr.find("select.productId").val();
				  var countryCode=$(this).val();
				  var colorCode=tr.find("select.colorCode").val()=='No color'?'':tr.find("select[name='colorCode']").val();
				  tr.find(".price").val(ajaxGetPrice(productId,countryCode,colorCode).price);
				});
				
				$("#inputForm").on("change",".colorCode",function(){
				 var  tr = $(this).parent().parent().parent();
					//ajax  获取UnitPrice
				  var productId=tr.find("select.productId").val();
				  var countryCode=tr.find("select.countryCode").val();
				  var colorCode=$(this).val()=='No color'?'':$(this).val();
				  tr.find(".price").val(ajaxGetPrice(productId,countryCode,colorCode).price);
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
				
				tr.find(".productQuantity").val('').focus();
				
				//ajax  获取UnitPrice
				  var productId=$(this).val();
				  var countryCode=tr.find("select[name='countryCode']").val();
				  var colorCode=tr.find("select[name='colorCode']").val()=='No color'?'':tr.find("select[name='colorCode']").val();
				  tr.find(".price").val(ajaxGetPrice(productId,countryCode,colorCode).price);
				  tr.find(".packQuantity").val(ajaxGetPrice(productId,countryCode,colorCode).packQuantity);
			});
			
			//如果是增加就
			//$(".productId").change();
			
			$(".remove-row").live("click",function(){
				 if($('#contentTable tbody tr').size()>1){
					var tr = $(this).parent().parent();
					tr.remove();
				}
			});
			
			
			$("#inputForm").validate({
				rules:{
					"product.id":{
						"required":true,
					},"colorCode":{
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
					"colorCode":{"required":'颜色不能为空'},
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
					
					
					//如果其他费用不为空     currency4和vendor4不能为空
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
					
					
					//var bigBoxNumber = $("input[name='boxNumber']").val();
					//var re = /^[0-9]*[1-9][0-9]*$/ ;
					//if(!re.test(bigBoxNumber)){
					//	top.$.jBox.tip("数量必须为装箱数的整数倍！", 'info',{timeout:3000});
					//	return false;
					//};
					
					
					 var keyStr="";
					 var flag = 1;
					 var zeroFlag=1;
					 var twoStr="";
					
					
					
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
				    	if(data.msg=='true'){
				    		ajaxProduct.price=data.price;
				    		ajaxProduct.packQuantity=data.packQuantity;
				    	}
			        }
			});
			
			return ajaxProduct;
		}
		
		//当运输费用发生变化，总数跟着变化
		function changeTranAmount(){
			var div = $("input[name='tranAmount']").parent().parent().parent();
			var rate =div.find(".rate").val();
			if(rate!=''){
				div.find(".afterAmount").val(toDecimal(parseFloat(rate)*parseFloat($("input[name='tranAmount']").val())));
			}
			//是快递总数要手填，然后算出单价
			if($("#model").val()=='0'){
				var total=0;
				var parentDiv =div.parent();
				console.log(parentDiv);
				parentDiv.find(".afterAmount").each(function(){
					if($(this).val()&&$(this).val()!=''){
						total=total+parseFloat($(this).val());
					}
				});
				$("#totalAmount").val(total);
			}
		}
		
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            return f.toFixed(2);  
	     };
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiTransportOrder/">运单列表</a></li>
		<li class="active"><a href="#">${empty psiTransportOrder.id ?'新建':'编辑'}${psiTransportOrder.model eq '0'? '航空':'快递'}运单</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiTransportOrder" action="${ctx}/psi/psiTransportOrder/editSave" method="post" class="form-horizontal" enctype="multipart/form-data">
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
		
		<input type="hidden" name="planeNum"      value="${psiTransportOrder.planeNum}"/>
		<input type="hidden" name="planeIndex"    value="${psiTransportOrder.planeIndex}"/>
		<input type="hidden" name="shipmentId"      value="${psiTransportOrder.shipmentId}"/>
		
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
		<input type="hidden" name="insurancePath"      value="${psiTransportOrder.insurancePath}"/>
		<input type="hidden" name="taxPath"            value="${psiTransportOrder.taxPath}"/>
		<input type="hidden" name="elsePath"           value="${psiTransportOrder.elsePath}"/>	
		<input type="hidden" name="suffixName"         value="${psiTransportOrder.suffixName}"/>
		<input type="hidden" name="unlineOrder"        value="${psiTransportOrder.unlineOrder}"/>
		<input type="hidden" name="fbaInboundId"       value="${psiTransportOrder.fbaInboundId}"/>
		<input type="hidden" name="confirmPay"         value="${psiTransportOrder.confirmPay}"/>
		
		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">运输类型:</label>
					<div class="controls" style="margin-left:120px" >
						<select name="transportType" style="width: 100%" id="transportType" disabled>
							<option value="0" ${psiTransportOrder.transportType eq '0'?'selected':''}>本地运输</option>	
							<option value="1" ${psiTransportOrder.transportType eq '1'?'selected':''}>FBA运输</option>	
							<option value="2" ${psiTransportOrder.transportType eq '2'?'selected':''}>批发运输</option>	
							<option value="3" ${psiTransportOrder.transportType eq '3'?'selected':''}>线下运输</option>	
						</select>
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
		</div>
		
		<div style="float:left;width:98%">
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">发货仓库:</label>
				<div class="controls" style="margin-left:120px" >
					<select name="fromStore.id" style="width: 100%" disabled >
						<c:forEach items="${toStock}" var="stock">
						<c:if test="${stock.type eq '0' }">
							<option value="${stock.id}" ${psiTransportOrder.fromStore.id eq stock.id ? 'selected':'' }>${stock.stockSign}</option>
						</c:if>
						</c:forEach>		
					</select>
				</div>
			</div>
			<c:if test="${psiTransportOrder.transportType ne '2'&&psiTransportOrder.transportType ne '3'}">
					<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">目的仓库:</label>
					<div class="controls"  style="margin-left:120px" >
						<select name="toStore.id" style="width: 100%" disabled >
							<c:forEach items="${toStock}" var="stock">
								<option value="${stock.id}" ${psiTransportOrder.toStore.id eq stock.id ? 'selected':'' }>${fns:getDictLabel(stock.platform, 'platform', '')}&nbsp;${stock.stockSign}</option>
							</c:forEach>		
						</select>
					</div>
				</div>
			</c:if>
			
			<c:if test="${psiTransportOrder.transportType eq '2'||psiTransportOrder.transportType eq '3'}">
				<div class="control-group" style="float:left;width:25%;height:25px;${psiTransportOrder.transportType eq '0'||psiTransportOrder.transportType eq '1'?'display:none':''}" id="destinationDetailDiv">
				<label class="control-label" style="width:100px">目的地地址:</label>
				<div class="controls"  style="margin-left:120px" >
					<%-- <input name="destinationDetail" type="text" style="width:95%" readonly="readonly" value="${psiTransportOrder.destinationDetail}"/> --%>
					<select name="destinationDetail" style="width: 100%" id="destinationDetail" >
					    <c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek'}">
								<option value="${dic.value}" ${psiTransportOrder.destinationDetail eq dic.value ? 'selected':'' }>${dic.label}</option>
							</c:if>
			             </c:forEach>
		             </select>
				</div>
			</div>
			</c:if>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">起运机场:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="orgin" type="text" readonly maxlength="10" style="width:95%"  value="${psiTransportOrder.orgin}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">目的机场:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="destination" type="text" readonly maxlength="10" style="width:95%"  value="${psiTransportOrder.destination}"/>
					</div>
			</div>
		</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">航空公司:</label>
					<div class="controls" style="margin-left:120px" >
						<%-- <input name="carrier" type="text"  maxlength="10" style="width:95%"  value="${psiTransportOrder.carrier}"/> --%>
					<select name="carrier" style="width:98%">
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
						<input name="etdDate"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${psiTransportOrder.etdDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">预计到港日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="preEtaDate" type="text" class="Wdate"  style="width:95%" value="<fmt:formatDate value="${psiTransportOrder.preEtaDate}" pattern="yyyy-MM-dd" />" />
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
					<input name="weight" type="text" style="width:80%"  id="weight" ${fn:contains(canEditStr,'TranAmount')&&psiTransportOrder.model eq '0'?'readonly':''} value="${psiTransportOrder.weight}"/> <span class="add-on">kg</span>
					</div>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">单价:</label>
				<div class="controls" style="margin-left:120px" >
					<input name="unitPrice" type="text" maxlength="10"  ${fn:contains(canEditStr,'TranAmount')||psiTransportOrder.model eq '2'?'readonly':''} style="width:95%" id="unitPrice" class=" price" value="${psiTransportOrder.unitPrice}"/>
				</div>
			</div>
			
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">体积:</label>
				<div class="controls" style="margin-left:120px" >
					<div class="input-prepend input-append">
					<input type="text" name="volume"  style="width:80%"  value="${psiTransportOrder.volume}"/> <span class="add-on">m³</span>
					</div>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">箱数:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="boxNumber" type="text" readonly style="width:95%" value="${psiTransportOrder.boxNumber}"/>
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
						<input name="localAmount" type="text" maxlength="10" style="width:95%" id="localAmount" ${fn:contains(canEditStr,'LocalAmount')||(fn:contains(canEditStr,'TranAmount')&&psiTransportOrder.model eq '2')?'readonly':''} class=" price firstAmount" value="${psiTransportOrder.localAmount}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency1:</label>
					<div class="controls" style="margin-left:100px" >
					<select name="currency1" style="width:95%"  ${fn:contains(canEditStr,'LocalAmount')||(fn:contains(canEditStr,'TranAmount')&&psiTransportOrder.model eq '2')?'disabled':''}>
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
						<select name="vendor1.id" style="width:95%" ${fn:contains(canEditStr,'LocalAmount')||(fn:contains(canEditStr,'TranAmount')&&psiTransportOrder.model eq '2')?'disabled':''} >
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
						<input  type="text" name="rate1" maxlength="10" style="width:95%" ${fn:contains(canEditStr,'LocalAmount')||(fn:contains(canEditStr,'TranAmount')&&psiTransportOrder.model eq '2')?'readonly':''} class="rate " value="${psiTransportOrder.rate1}"/>
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
						<input name="tranAmount" type="text" maxlength="10" style="width:95%" readonly="readonly" class=" price firstAmount" value="${psiTransportOrder.tranAmount}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Currency2:</label>
					<div class="controls" style="margin-left:100px" >
					<select name="currency2" style="width:95%" ${fn:contains(canEditStr,'TranAmount')?'disabled':''}>
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
						<select name="vendor2.id" style="width:95%"  ${fn:contains(canEditStr,'TranAmount')?'disabled':''}>
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
						<input  type="text" name="rate2" maxlength="10" style="width:95%" ${fn:contains(canEditStr,'TranAmount')?'readonly':''} class="rate " value="${psiTransportOrder.rate2}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >金额2:</label>
					<div class="controls" style="margin-left:100px" >
						<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrder.tranAmount*psiTransportOrder.rate2}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
		<c:if test="${psiTransportOrder.model eq '0' }">
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">目的港费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="dapAmount" type="text"  maxlength="10" ${(fn:contains(canEditStr,'DapAmount')&&psiTransportOrder.vendor3.id ne '18')?'readonly':''} style="width:95%" class=" price firstAmount" value="${psiTransportOrder.dapAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency3:</label>
						<div class="controls" style="margin-left:100px" >
						<select name="currency3" style="width:95%"  ${fn:contains(canEditStr,'DapAmount')?'disabled':''}>
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
							<select name="vendor3.id" style="width:95%"  ${fn:contains(canEditStr,'DapAmount')?'disabled':''}>
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
							<input  type="text" name="rate3" maxlength="10" ${fn:contains(canEditStr,'DapAmount')?'readonly':''} style="width:95%" class="rate " value="${psiTransportOrder.rate3}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >金额3:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrder.dapAmount*psiTransportOrder.rate3}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
		</c:if>
		
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">其他费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="otherAmount" type="text"  ${fn:contains(canEditStr,'OtherAmount')?'readonly':''} maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrder.otherAmount}" id="otherAmount"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency4:</label>
						<div class="controls" style="margin-left:100px" >
							<select name="currency4" style="width:95%"  ${fn:contains(canEditStr,'OtherAmount')?'disabled':''}>
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
							<select name="vendor4.id" style="width:95%"  ${fn:contains(canEditStr,'OtherAmount')?'disabled':''}>
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
							<input  type="text" name="rate4" maxlength="10" ${fn:contains(canEditStr,'OtherAmount')?'readonly':''} style="width:95%" class="rate" value="${psiTransportOrder.rate4}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >金额4:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrder.otherAmount*psiTransportOrder.rate4}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">其他费用1:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="otherAmount1" type="text"  ${fn:contains(canEditStr,'OtherAmount1')?'readonly':''} maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrder.otherAmount1}" id="otherAmount1"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency7:</label>
						<div class="controls" style="margin-left:100px" >
							<select name="currency7" style="width:95%"  ${fn:contains(canEditStr,'OtherAmount1')?'disabled':''}>
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
							<select name="vendor7.id" style="width:95%"  ${fn:contains(canEditStr,'OtherAmount1')?'disabled':''}>
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
							<input  type="text" name="rate7" maxlength="10" ${fn:contains(canEditStr,'OtherAmount1')?'readonly':''} style="width:95%" class="rate" value="${psiTransportOrder.rate7}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >金额7:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrder.otherAmount1*psiTransportOrder.rate7}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">保险费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="insuranceAmount" type="text" ${fn:contains(canEditStr,'InsuranceAmount')?'readonly':''} maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrder.insuranceAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency5:</label>
						<div class="controls" style="margin-left:100px" >
							<select name="currency5" style="width:95%"  ${fn:contains(canEditStr,'InsuranceAmount')?'disabled':''}>
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
							<select name="vendor5.id" style="width:95%" ${fn:contains(canEditStr,'InsuranceAmount')?'disabled':''}>
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
					<input name="dutyTaxes" type="text" maxlength="10" ${(fn:contains(canEditStr,'TaxAmount')&&psiTransportOrder.vendor6.id ne '18')?'readonly':''} style="width:95%" class=" price" value="${psiTransportOrder.dutyTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:10%;height:25px">
				<label class="control-label" style="width:60px">关税:</label>
				<div class="controls" style="margin-left:70px" >
					<input name="taxTaxes" type="text" maxlength="10" ${(fn:contains(canEditStr,'TaxAmount')&&psiTransportOrder.vendor6.id ne '18')?'readonly':''}  style="width:85%" class=" price" value="${psiTransportOrder.taxTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:10%;height:25px">
				<label class="control-label" style="width:60px">other税:</label>
				<div class="controls" style="margin-left:70px" >
					<input name="otherTaxes" type="text" maxlength="10" ${(fn:contains(canEditStr,'TaxAmount')&&psiTransportOrder.vendor6.id ne '18')?'readonly':''}  style="width:85%" class=" price" value="${psiTransportOrder.otherTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency6:</label>
					<div class="controls"  style="margin-left:100px">
						<select name="currency6" style="width:95%" ${fn:contains(canEditStr,'TaxAmount')?'disabled':''}>
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
						<select name="vendor6.id" style="width:95%" ${fn:contains(canEditStr,'TaxAmount')?'disabled':''}>
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
					<input type="text" maxlength="10" style="width:85%" id="totalAmount"  ${fn:contains(canEditStr,'TranAmount')&&psiTransportOrder.model eq '2'?'readonly':''} class=" price" value="${psiTransportOrder.totalAmount}"/>
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
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">local_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>  
					</c:if>
					<c:if test="${not empty psiTransportOrder.tranPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.tranPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">运输费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.dapPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.dapPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">目的港费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.otherPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.otherPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">其他费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.otherPath1}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.otherPath1,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">其他费用1凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.insurancePath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.insurancePath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">保费费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.taxPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.taxPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">税费费用_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>
					</c:if>
					 
			</div>
		</div>
		</c:if>
		
		
		<div style="float:left;width:98%;height:110px;">
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >local凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="localFile" type="file" id="localFile"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >运输费用凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="tranFile" type="file" id="tranFile"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >目的港费用凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="dapFile" type="file" id="dapFile"/>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%;height:110px;">
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >其他费用凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="otherFile" type="file" id="otherFile"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >其他费用1凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="otherFile1" type="file" id="otherFile1"/>
					</div>
			</div>
		</div>
		<div style="float:left;width:98%;height:110px;">
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >保费费用凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="insuranceFile" type="file" id="insuranceFile"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >税费费用凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="taxFile" type="file" id="taxFile"/>
					</div>
			</div>
		</div>
		
		
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
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 15%">产品名</th>
				   <th style="width: 10%">国家</th>
				   <th style="width: 10%">颜色</th>
				   <th style="width: 15%">Sku</th>
				   <th style="width: 5%">线下</th>
				   <th style="width: 5%">装箱数</th>
				   <th style="width: 5%">数量</th>
				   <th style="width: 5%">箱数</th>
				   <th style="width: 5%">单价</th>
				   <th style="width: 5%">备注</th>
			</tr>
		</thead>
		<tbody>
		<c:if test="${ not empty psiTransportOrder.id }">
		<c:forEach items="${psiTransportOrder.items}"  var="item">
			<tr>
				<td>
				<input type="hidden" name="id" value="${item.id}"/>
				<input type="hidden" name="shippedQuantity" value="${item.shippedQuantity}"/>
				<input type="hidden" name="receiveQuantity" value="${item.receiveQuantity}"/>
				<input type="hidden" name="itemPrice" value="${item.itemPrice}"/>
				<input type="hidden" name="currency" value="${item.currency}"/>
				<input type="hidden" name="offlineSta" value="${item.offlineSta}"/>
				<input type="hidden" name="fbaFlag" value="${item.fbaFlag}"/>
				<input type="hidden" name="fbaInboundId" value="${item.fbaInboundId}"/>
				<select style="width: 90%" class="productId" name="product.id" id="productId" disabled>
					<c:forEach items="${products}" var="product" varStatus="i">
							 <option value='${product.id}' ${item.product.id eq product.id ?'selected':''}>${product.name}</option>
					</c:forEach>
				</select>
				<input type='hidden' name="productName" value="${item.productName}"/>
				</td>
				<td>
					<span>
						<select name="countryCode" class="countryCode" style="width:90%" disabled>
							<c:forEach items="${item.countryList}" var="country" varStatus="i">
									 <option value="${country}" ${country eq item.countryCode ?'selected':''}>${fns:getDictLabel(country, 'platform', '')}</option>;
							</c:forEach>
						</select>
					</span>
				</td>
				<td>
					<span>
						<select name="colorCode" class="colorCode"  style="width:90%" disabled>
							<c:if test="${fn:length(item.colorList)>0}">
								<c:forEach items="${item.colorList}" var="color" varStatus="i">
										 <option value='${color}'  ${color eq item.colorCode ?'selected':''}>${color}</option>;
								</c:forEach>
							</c:if>
							<c:if test="${fn:length(item.colorList)==0}">
								 <option value='No color'  selected>No color</option>;
							</c:if>
						</select>
					</span>
				</td>
				<td><input type="text"style="width: 90%" name="sku" readonly="readonly"  value="${item.sku}"/></td>
				<td>${item.offlineSta eq '1'?'是':'否' }</td>
				<td><input type="text" maxlength="11" style="width: 80%" name="packQuantity" readonly="readonly" class="packQuantity" value="${item.packQuantity }"/></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="quantity" readonly  class="number productQuantity" value="${item.quantity}" ${psiTransportOrder.transportSta ne '0'?'readonly':'' }/></td>
				<td><input type="text" maxlength="11" style="width: 80%" class="boxNo" value="${item.quantity/item.packQuantity}" readonly /></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="productPrice" value="${item.productPrice}" class="price" readonly/></td>
				<td><input type="text" maxlength="50" style="width: 80%" name="remark" readonly value="${item.remark}" ${psiTransportOrder.transportSta ne '0'?'readonly':'' }/></td>
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
