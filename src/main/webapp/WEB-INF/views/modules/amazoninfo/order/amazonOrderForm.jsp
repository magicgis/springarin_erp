<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊发货订单详情</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
			if(!(top)){
				top = self; 
			}
			$("#checkall").click(function(){
				 $('input[name=checkId]:checkbox').each(function(){
					 if($(this).attr("disabled")!='disabled'){
						 if(this.checked){
					    	 this.checked=false;
					     }else{
					    	 this.checked=true;
					     }
					 }
				     console.log(this.checked);
				 });
			});
			
			$("#print").click(function(){
				if($("#hasTax").val()=='2'){
					showRefundBillDiv();
				}else if($("#hasTax").val()=='3'){
					showRefundTaxDiv();
				}else if($("#hasTax").val()=='4'){
					showPartRefundBillDiv();
				}else{
					
					var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' >";
					refundBillHtml=refundBillHtml+"<table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'>invoice quantity</th></tr></thead><tbody>";
					
					<c:forEach items="${amazonOrder.items}" var="item">
					   refundBillHtml=refundBillHtml+"<tr><td>${item.name}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sellersku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityOrdered}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.quantityOrdered}' /></td></tr>";
				    </c:forEach>
				
					refundBillHtml=refundBillHtml+"<tr><th>paymentMethod:</th><td><input style='width:90%' type='text' id='payment' class='payment'  name='payment'/></td><th>delivery date:</th><td><input id='deliveryDate'  style='width: 130px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' /></td></tr>";
					refundBillHtml=refundBillHtml+"<tr><th>remark:</th><td colspan='3'><input style='width:90%' type='text' id='remark' class='remark'  name='remark'/></td></tr>";
					refundBillHtml=refundBillHtml+"</tbody></table></div>";
					var submitChild = function (v, h, f) {
						var ids="";
						var quantitys="";
						var changeFlag="0";
						h.find("input[name='quantity']").each(function(){
							    var quantity=$(this).val();
							    var tr  =$(this).parent().parent();
							    var id      =tr.find(".itemId").val();
								if(quantity!=''){
									var  oldQuantity = tr.find(".oldQuantity").val();
									if(parseInt(oldQuantity)!=parseInt(quantity)){
										changeFlag='1';
									}
									ids=ids+id+",";
									quantitys=quantitys+quantity+",";
								}else{
									changeFlag='1';
									ids=ids+id+",";
									quantitys=quantitys+0+",";
								}
						});
						if(changeFlag=="0"){
							ids="";
							quantitys="";
						}
						$(this).attr("disabled","disabled");
						loading('<spring:message code="amazon_order_tips7"/>');
						var params = {};
						params.id = '${amazonOrder.id}';
						params.hasTax = $("#hasTax").val();
						params.payment=h.find("#payment").val();
						params.deliveryDate=h.find("#deliveryDate").val();
						params.remark=h.find("#remark").val();
						params.itemStr=ids;
						params.quantityStr=quantitys;
						
						$.post("${ctx}/amazoninfo/order/invoice",$.param(params),function(data){
							top.$.jBox.closeTip(); 
							if(data==1){
								if(params.hasTax==0){
									windowOpen('${ctx}/../data/site/invoice/Amazon_${amazonOrder.amazonOrderId}_nbill.pdf','<spring:message code="amazon_order_form_select1_option1"/>',800,600);
								}else{
									windowOpen('${ctx}/../data/site/invoice/Amazon_${amazonOrder.amazonOrderId}_bill.pdf','<spring:message code="amazon_order_form_select1_option2"/>',800,600);
								}
								top.$.jBox.tip("<spring:message code="amazon_order_tips8"/>！","success",{persistent:false,opacity:0});
							}else{
								top.$.jBox.tip("<spring:message code="amazon_order_tips9"/>！","error",{persistent:false,opacity:0});
							}
							$("#print").removeAttr("disabled");
						});
					    return true;
					};

					$.jBox(refundBillHtml, { title: "Choose Bill Items",width:600,submit: submitChild,persistent: true});
				}
				
				
				
			});
			
			$("#send").click(function(){
				if($("#hasTax").val()=='2'){
					showSendMailDiv();
				}else if($("#hasTax").val()=='4'){
					showSendMailPartDiv();
				}else{
					$(this).attr("disabled","disabled");
					var html = "<spring:message code="amazon_order_tips10"/>：<div class='input-append btn-group btn-input'><input id='mail' value='${amazonOrder.buyerEmail}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>"+
					"<br/><spring:message code="amazon_order_tips11"/>：<div class='input-append btn-group btn-input'><input id='bcc' value='' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>";
					top.$.jBox.confirm(html,'<spring:message code="amazon_order_tips12"/>？',function(v,h,f){
						if(v=='ok'){
							if(validateMail(h.find("#mail").val())&&validateMail(h.find("#bcc").val())){
								loading('<spring:message code="amazon_order_tips13"/>');
								var params = {};
								params.id = '${amazonOrder.id}'; 
								params.mail = encodeURI(h.find("#mail").val());
								params.bcc = encodeURI(h.find("#bcc").val());
								params.hasTax = $("#hasTax").val();
								window.location.href = "${ctx}/amazoninfo/order/send?"+$.param(params);
							}else{
								top.$.jBox.tip("<spring:message code="amazon_order_tips14"/>!","error");
								return false;
							}
						}
						$("#send").removeAttr("disabled");
					},{buttonsFocus:1,width:450,showClose: false,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}
			});
			
			
			$("#update").click(function(){
				$("#reset,#save").show();
				$("#panel-1 span").hide();
				$("#panel-1 input").show();
				$("#panel-1 input[type != 'button']").each(function(){
					$(this).val($(this).parent().find("span").text().trim());				
				});
				$(this).hide();
			});
			
			$("#reset").click(function(){
				$("#panel-1 input[type != 'button']").each(function(){
					$(this).val($(this).parent().find("span").text().trim());				
				});
			});
			
			$("#btnEdit").click(function(){
				var id =$("#id").val();
				window.location.href = "${ctx}/amazoninfo/order/edit?id="+id;
			});
			
			$("#saveBtn").click(function(){
				$(this).attr("disabled","disabled");
				var params = {};
				params.id = '${amazonOrder.id}';
				params['invoiceAddress.order.id']= '${amazonOrder.id}';
				if($("#invoiceAddressId").val()){
					params['invoiceAddress.id'] = $("#invoiceAddressId").val();
				}
				var flag = false;
				$("#panel-1 input[type!='button']").each(function(){
					if($(this).val().length>255){
						flag = true;
						return;
					}
					params[$(this).attr('name')] = $(this).val(); 
				});
				if(flag){
					top.$.jBox.tip("<spring:message code="amazon_order_form_tips6"/>！","error",{persistent:false,opacity:0});
					$("#saveBtn").removeAttr("disabled");
					return;
				}
				loading('<spring:message code="amazon_order_tips15"/>...');
				$.post("${ctx}/amazoninfo/order/save",$.param(params),function(data){
					if($.isNumeric(data)){
						$("#panel-1 input[type != 'button']").each(function(){
							$(this).parent().find("span").text($(this).val().trim());				
						});
						$("#invoiceAddressId").val(data);
						$("#panel-1 input").hide();
						$("#update").show();
						$("#panel-1 span").show();
						top.$.jBox.closeTip(); 
						top.$.jBox.tip("<spring:message code="amazon_order_tips16"/>！","success",{persistent:false,opacity:0});
					}else{
						top.$.jBox.tip("<spring:message code="amazon_order_tips17"/>！","error",{persistent:false,opacity:0});
					}
					$("#saveBtn").removeAttr("disabled");
				});
			});
			
			
			$("#refund").click(function(){
				
					var ids = $("input:checkbox[name='checkId']:checked");
					if(!ids.length){
				    	$.jBox.tip('Please select data ！');
						return;
					}		
					var arr = new Array();
					for(var i=0;i<ids.length; i++){
						var id = ids[i].value;
						arr.push(id);
					}
					var idsAll = arr.join(',');
					$("#selectItems").val(idsAll);
					$("#inputForm").submit();
			 });
			
			$("#otherRefund").click(function(){
				var ids = $("input:checkbox[name='checkId']:checked");
				if(!ids.length){
			    	$.jBox.tip('Please select data ！');
					return;
				}		
				var arr = new Array();
				for(var i=0;i<ids.length; i++){
					var id = ids[i].value;
					arr.push(id);
				}
				var idsAll = arr.join(',');
				$("#selectItems").val(idsAll);
				$("#inputForm").attr("action","${ctx}/amazoninfo/refund/add?type=1");
				$("#inputForm").submit();
				$("#inputForm").attr("action","${ctx}/amazoninfo/refund/add");
		 });
			
		});
		function validateMail(value) {
			if(value==''){
				return true;
			}
			var rs = true ;
			$(value.split(",")).each(function(i,data){
				var temp =  /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i.test(data);
				if(!temp){
					rs = false;
					return 
				}
			});
			return rs;
		}
		
		function showPartRefundBillDiv(){
			var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund money</th></tr></thead><tbody>";
			<c:forEach items="${amazonOrder.items}" var="item">
				refundBillHtml=refundBillHtml+"<tr><td>${item.name}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sellersku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityOrdered}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.itemPrice}' /></td></tr>";
			</c:forEach>
			refundBillHtml=refundBillHtml+"<tr><th>refund date:</th><td><input id='refundDate' value='${fns:getDate('yyyy-MM-dd HH:mm:ss')}' style='width: 130px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' /></td><th>paymentMethod:</th><td><input style='width:90%' type='text' id='payment' class='payment'  name='payment'/></td></tr>";
			refundBillHtml=refundBillHtml+"<tr><th>invoice no.:</th><td><input style='width:90%' type='text' id='amazonOrderId' class='amazonOrderId'  name='amazonOrderId' value='${amazonOrder.id}_1'/></td><th>remark:</th><td><input style='width:90%' type='text' id='remark' class='remark'  name='remark'/></td></tr>";
			
			refundBillHtml=refundBillHtml+"</tbody></table></div>";
			
			var submitChild = function (v, h, f) {
				var isChecked =false;
				h.find("input[name='quantity']").each(function(){
					if($(this).val()!=''){
						isChecked=true;
						return ;
					};
					
				});
				
				if(!isChecked){
					top.$.jBox.tip("please input refund money！", 'info',{timeout:3000});
					return false;
				}
				
				var ids="";
				var quantitys="";
				var quantityFlag=true;
				h.find("input[name='quantity']").each(function(){
					var quantity=$(this).val();
						if(quantity!=''){
							var tr  =$(this).parent().parent();
							var  oldQuantity = tr.find(".oldQuantity").val();
							var id      =tr.find(".itemId").val();
							/* if(quantity==0){
								 quantityFlag=false;
								 return;
							}  */
							ids=ids+id+",";
							quantitys=quantitys+quantity+",";
						};
					
				});
				
				if(!quantityFlag){
					 $.jBox.tip("please input right refund money", 'error');
					 return false;
				}
				
				
				$("#print").attr("disabled","disabled");
				loading('<spring:message code="amazon_order_tips7"/>');
				var params = {};
				params.id = '${amazonOrder.id}';
				params.hasTax = $("#hasTax").val();
				params.paymentMethod=ids;
				params.marketplaceId=quantitys;
				params.lastUpdateDate=h.find("#refundDate").val();
				params.payment=h.find("#payment").val();
				params.remark=h.find("#remark").val();
				params.amazonOrderId=h.find("#amazonOrderId").val();
				$.post("${ctx}/amazoninfo/order/invoice",$.param(params),function(data){
					top.$.jBox.closeTip(); 
					if(data==1){
						if(params.hasTax==0){
							windowOpen('${ctx}/../data/site/invoice/Amazon_${amazonOrder.amazonOrderId}_nbill.pdf','<spring:message code="amazon_order_form_select1_option1"/>',800,600);
						}else{
							windowOpen('${ctx}/../data/site/invoice/Amazon_${amazonOrder.amazonOrderId}_bill.pdf','<spring:message code="amazon_order_form_select1_option2"/>',800,600);
						}
						top.$.jBox.tip("<spring:message code="amazon_order_tips8"/>！","success",{persistent:false,opacity:0});
					}else{
						top.$.jBox.tip("<spring:message code="amazon_order_tips9"/>！","error",{persistent:false,opacity:0});
					}
					$("#print").removeAttr("disabled");
				});
				
				
			    return true;
			};

			$.jBox(refundBillHtml, { title: "Choose Refund Bill Items",width:600,submit: submitChild,persistent: true});
		}
		
		function showRefundBillDiv(){
			var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund quantity</th></tr></thead><tbody>";
			<c:forEach items="${amazonOrder.items}" var="item">
				refundBillHtml=refundBillHtml+"<tr><td>${item.name}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sellersku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityOrdered}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.quantityOrdered}' /></td></tr>";
			</c:forEach>
			refundBillHtml=refundBillHtml+"<tr><th>refund date:</th><td><input id='refundDate' value='${fns:getDate('yyyy-MM-dd HH:mm:ss')}' style='width: 130px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' /></td><th>paymentMethod:</th><td><input style='width:90%' type='text' id='payment' class='payment'  name='payment'/></td></tr>";
			refundBillHtml=refundBillHtml+"<tr><th>remark:</th><td colspan='3'><input style='width:90%' type='text' id='remark' class='remark'  name='remark'/></td></tr>";
			
			refundBillHtml=refundBillHtml+"</tbody></table></div>";
			
			var submitChild = function (v, h, f) {
				var isChecked =false;
				h.find("input[name='quantity']").each(function(){
					if($(this).val()!=''){
						isChecked=true;
						return ;
					};
					
				});
				
				if(!isChecked){
					top.$.jBox.tip("please input quantity！", 'info',{timeout:3000});
					return false;
				}
				
				var ids="";
				var quantitys="";
				var quantityFlag=true;
				h.find("input[name='quantity']").each(function(){
					var quantity=$(this).val();
						if(quantity!=''){
							var tr  =$(this).parent().parent();
							var  oldQuantity = tr.find(".oldQuantity").val();
							var id      =tr.find(".itemId").val();
							if(quantity==0||parseInt(oldQuantity)<parseInt(quantity)){
								 quantityFlag=false;
								 return;
							}
							ids=ids+id+",";
							quantitys=quantitys+quantity+",";
						};
					
				});
				
				if(!quantityFlag){
					 $.jBox.tip("please input right quantity", 'error');
					 return false;
				}
				
				
				$("#print").attr("disabled","disabled");
				loading('<spring:message code="amazon_order_tips7"/>');
				var params = {};
				params.id = '${amazonOrder.id}';
				params.hasTax = $("#hasTax").val();
				params.paymentMethod=ids;
				params.marketplaceId=quantitys;
				params.lastUpdateDate=h.find("#refundDate").val();
				params.payment=h.find("#payment").val();
				params.remark=h.find("#remark").val();
				$.post("${ctx}/amazoninfo/order/invoice",$.param(params),function(data){
					top.$.jBox.closeTip(); 
					if(data==1){
						if(params.hasTax==0){
							windowOpen('${ctx}/../data/site/invoice/Amazon_${amazonOrder.amazonOrderId}_nbill.pdf','<spring:message code="amazon_order_form_select1_option1"/>',800,600);
						}else{
							windowOpen('${ctx}/../data/site/invoice/Amazon_${amazonOrder.amazonOrderId}_bill.pdf','<spring:message code="amazon_order_form_select1_option2"/>',800,600);
						}
						top.$.jBox.tip("<spring:message code="amazon_order_tips8"/>！","success",{persistent:false,opacity:0});
					}else{
						top.$.jBox.tip("<spring:message code="amazon_order_tips9"/>！","error",{persistent:false,opacity:0});
					}
					$("#print").removeAttr("disabled");
				});
				
				
			    return true;
			};

			$.jBox(refundBillHtml, { title: "Choose Refund Bill Items",width:600,submit: submitChild,persistent: true});
		}
		
		
		function showRefundTaxDiv(){
			var refundTaxHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund quantity</th></tr></thead><tbody>";
			<c:forEach items="${amazonOrder.items}" var="item">
				refundTaxHtml=refundTaxHtml+"<tr><td>${item.name}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sellersku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityOrdered}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.quantityOrdered}'/></td></tr>";
			</c:forEach>
			refundTaxHtml=refundTaxHtml+"<tr><th>paymentMethod:</th><td><input style='width:90%' type='text' id='payment' class='payment'  name='payment'/></td><th>remark:</th><td><input style='width:90%' type='text' id='remark' class='remark'  name='remark'/></td></tr></tbody></table></div>";
			
			var submitChild = function (v, h, f) {
				var isChecked =false;
				h.find("input[name='quantity']").each(function(){
					if($(this).val()!=''){
						isChecked=true;
						return ;
					};
					
				});
				
				if(!isChecked){
					top.$.jBox.tip("please input quantity！", 'info',{timeout:3000});
					return false;
				}
				
				var ids="";
				var quantitys="";
				var quantityFlag=true;
				h.find("input[name='quantity']").each(function(){
					var quantity=$(this).val();
						if(quantity!=''){
							var tr  =$(this).parent().parent();
							var  oldQuantity = tr.find(".oldQuantity").val();
							var id      =tr.find(".itemId").val();
							if(quantity==0||parseInt(oldQuantity)<parseInt(quantity)){
								 quantityFlag=false;
								 return;
							}
							ids=ids+id+",";
							quantitys=quantitys+quantity+",";
						};
					
				});
				
				if(!quantityFlag){
					 $.jBox.tip("please input right quantity", 'error');
					 return false;
				}
				
				
				$("#print").attr("disabled","disabled");
				loading('<spring:message code="amazon_order_tips7"/>');
				var params = {};
				params.id = '${amazonOrder.id}';
				params.hasTax = $("#hasTax").val();
				params.paymentMethod=ids;
				params.marketplaceId=quantitys;
				params.payment=h.find("#payment").val();
				params.remark=h.find("#remark").val();
				$.post("${ctx}/amazoninfo/order/invoice",$.param(params),function(data){
					top.$.jBox.closeTip(); 
					if(data==1){
						if(params.hasTax==0){
							windowOpen('${ctx}/../data/site/invoice/Amazon_${amazonOrder.amazonOrderId}_nbill.pdf','<spring:message code="amazon_order_form_select1_option1"/>',800,600);
						}else{
							windowOpen('${ctx}/../data/site/invoice/Amazon_${amazonOrder.amazonOrderId}_bill.pdf','<spring:message code="amazon_order_form_select1_option2"/>',800,600);
						}
						top.$.jBox.tip("<spring:message code="amazon_order_tips8"/>！","success",{persistent:false,opacity:0});
					}else{
						top.$.jBox.tip("<spring:message code="amazon_order_tips9"/>！","error",{persistent:false,opacity:0});
					}
					$("#print").removeAttr("disabled");
				});
				
				
			    return true;
			};

			$.jBox(refundTaxHtml, { title: "Choose Refund Tax Items",width:600,submit: submitChild,persistent: true});
		}
		
		function showSendMailPartDiv(){
			var sendMailHtml="<div class='showSendMailHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund money</th></tr></thead><tbody>";
			<c:forEach items="${amazonOrder.items}" var="item">
			sendMailHtml=sendMailHtml+"<tr><td>${item.name}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sellersku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityOrdered}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.itemPrice}'/></td></tr>";
			</c:forEach>
			sendMailHtml=sendMailHtml+"<tr><th>refund date:</th><td><input id='refundDate' value='${fns:getDate('yyyy-MM-dd HH:mm:ss')}' style='width: 130px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' /></td><th>paymentMethod:</th><td><input style='width:90%' type='text' id='payment' class='payment'  name='payment'/></td></tr>";
			sendMailHtml=sendMailHtml+"<tr><th>invoice no.:</th><td colspan='3'><input style='width:90%' type='text' id='amazonOrderId' class='amazonOrderId'  name='amazonOrderId' value='${amazonOrder.id}_1'/></td></tr>";
			sendMailHtml=sendMailHtml+"</tbody></table>";
			
			var html = "<spring:message code="amazon_order_tips10"/>：<div class='input-append btn-group btn-input'><input id='mail' value='${amazonOrder.buyerEmail}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>"+
			"<br/><spring:message code="amazon_order_tips11"/>：<div class='input-append btn-group btn-input'><input id='bcc' value='' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>";
		
			sendMailHtml=sendMailHtml+html+"</div>";
			
			var submitMailChild = function (v, h, f) {
				var isChecked =false;
				h.find("input[name='quantity']").each(function(){
					if($(this).val()!=''){
						isChecked=true;
						return ;
					};
					
				});
				
				if(!isChecked){
					top.$.jBox.tip("please input refund money！", 'info',{timeout:3000});
					return false;
				}
				
				var ids="";
				var quantitys="";
				var quantityFlag=true;
				h.find("input[name='quantity']").each(function(){
					var quantity=$(this).val();
						if(quantity!=''){
							var tr  =$(this).parent().parent();
							var  oldQuantity = tr.find(".oldQuantity").val();
							var id      =tr.find(".itemId").val();
							if(quantity==0){
								 quantityFlag=false;
								 return;
							}
							ids=ids+id+",";
							quantitys=quantitys+quantity+",";
						};
					
				});
				
				if(!quantityFlag){
					 $.jBox.tip("please input right refund money", 'error');
					 return false;
				}
				
				if(validateMail(h.find("#mail").val())&&validateMail(h.find("#bcc").val())){
					loading('<spring:message code="amazon_order_tips13"/>');
					var params = {};
					params.id = '${amazonOrder.id}'; 
					params.mail = encodeURI(h.find("#mail").val());
					params.bcc = encodeURI(h.find("#bcc").val());
					params.hasTax = $("#hasTax").val();
					params.paymentMethod=ids;
					params.marketplaceId=quantitys;
					params.lastUpdateDate=h.find("#refundDate").val();
					params.payment=h.find("#payment").val();
					params.amazonOrderId=h.find("#amazonOrderId").val();
					window.location.href = "${ctx}/amazoninfo/order/send?"+$.param(params);
				}else{
					top.$.jBox.tip("<spring:message code="amazon_order_tips14"/>!","error");
					return false;
				}
			}

			$.jBox(sendMailHtml, { title: "Choose Refund Items",width:600,submit: submitMailChild,persistent: true});
			
			
		}
		
		function showSendMailDiv(){
			var sendMailHtml="<div class='showSendMailHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund quantity</th></tr></thead><tbody>";
			<c:forEach items="${amazonOrder.items}" var="item">
			sendMailHtml=sendMailHtml+"<tr><td>${item.name}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sellersku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityOrdered}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.quantityOrdered}'/></td></tr>";
			</c:forEach>
			sendMailHtml=sendMailHtml+"<tr><th>refund date:</th><td><input id='refundDate' value='${fns:getDate('yyyy-MM-dd HH:mm:ss')}' style='width: 130px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' /></td><th>paymentMethod:</th><td><input style='width:90%' type='text' id='payment' class='payment'  name='payment'/></td></tr>";
			sendMailHtml=sendMailHtml+"</tbody></table>";
			
			var html = "<spring:message code="amazon_order_tips10"/>：<div class='input-append btn-group btn-input'><input id='mail' value='${amazonOrder.buyerEmail}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>"+
			"<br/><spring:message code="amazon_order_tips11"/>：<div class='input-append btn-group btn-input'><input id='bcc' value='' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>";
		
			sendMailHtml=sendMailHtml+html+"</div>";
			
			var submitMailChild = function (v, h, f) {
				var isChecked =false;
				h.find("input[name='quantity']").each(function(){
					if($(this).val()!=''){
						isChecked=true;
						return ;
					};
					
				});
				
				if(!isChecked){
					top.$.jBox.tip("please input quantity！", 'info',{timeout:3000});
					return false;
				}
				
				var ids="";
				var quantitys="";
				var quantityFlag=true;
				h.find("input[name='quantity']").each(function(){
					var quantity=$(this).val();
						if(quantity!=''){
							var tr  =$(this).parent().parent();
							var  oldQuantity = tr.find(".oldQuantity").val();
							var id      =tr.find(".itemId").val();
							if(quantity==0||parseInt(oldQuantity)<parseInt(quantity)){
								 quantityFlag=false;
								 return;
							}
							ids=ids+id+",";
							quantitys=quantitys+quantity+",";
						};
					
				});
				
				if(!quantityFlag){
					 $.jBox.tip("please input right quantity", 'error');
					 return false;
				}
				
				if(validateMail(h.find("#mail").val())&&validateMail(h.find("#bcc").val())){
					loading('<spring:message code="amazon_order_tips13"/>');
					var params = {};
					params.id = '${amazonOrder.id}'; 
					params.mail = encodeURI(h.find("#mail").val());
					params.bcc = encodeURI(h.find("#bcc").val());
					params.hasTax = $("#hasTax").val();
					params.paymentMethod=ids;
					params.marketplaceId=quantitys;
					params.lastUpdateDate=h.find("#refundDate").val();
					params.payment=h.find("#payment").val();
					window.location.href = "${ctx}/amazoninfo/order/send?"+$.param(params);
				}else{
					top.$.jBox.tip("<spring:message code="amazon_order_tips14"/>!","error");
					return false;
				}
			}

			$.jBox(sendMailHtml, { title: "Choose Refund Items",width:600,submit: submitMailChild,persistent: true});
			
			
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/order"><spring:message code="amazon_order_tab1"/></a></li>
		<li class="active"><a href="${ctx}/amazoninfo/order/form?id=${amazonOrder.id}"><spring:message code="amazon_order_tab3"/></a></li>
	</ul><br/>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="amazonOrder"  class="form-horizontal" method="post" action="${ctx}/amazoninfo/refund/add">
		<c:set var="totalMoney" value="0"/>
		<c:set var="showButton" value="0"/>
		<form:hidden path="id" id="id"/>
		<input type="hidden" id="selectItems" name="selectItems"/>
		
		<div style="float: left;width:100%;margin-left: 15px">
	
		<table class="table table-bordered table-condensed">
			<tbody>
				<tr class="info">
					<td colspan="6"><spring:message code="amazon_order_form_tips1"/></td>
				</tr>
				<tr>
					<td><b><spring:message code="amazon_order_form1"/></b></td>
					<td colspan='5'><b style="font-size: 14px">${amazonOrder.amazonOrderId}</b>
					   
					    <b style="font-size:14px;color:red;">[${amazonOrder.accountName}]</b>
					    &nbsp;&nbsp;
						<c:if test="${amazonOrder.orderStatus eq 'Shipped' || not empty amazonOrder.orderChannel}" >
							<select style="width: 100px" id="hasTax">
								<option value="0"><spring:message code="amazon_order_form_select1_option1"/></option>
								<option value="1"><spring:message code="amazon_order_form_select1_option2"/></option>
								<option value="2"><spring:message code="amazon_order_form_select1_option31"/></option>
								<c:if  test="${amazonOrder.salesChannel eq 'Amazon.de'||amazonOrder.salesChannel eq 'Amazon.fr' || amazonOrder.salesChannel eq 'Amazon.it' || amazonOrder.salesChannel eq 'Amazon.es' || amazonOrder.salesChannel eq 'Amazon.co.uk'}">
								  <option value="3"><spring:message code="amazon_order_form_select1_option4"/></option>
								</c:if>
								<option value="4"><spring:message code="amazon_order_form_select1_option32"/></option>
							</select>
							&nbsp;&nbsp;&nbsp;
							<input class="btn btn-warning"  type="button" id="print"  value="<spring:message code="sys_but_print"/>"/>
							&nbsp;
							<input class="btn btn-warning"  type="button" id="send" value="<spring:message code="sys_but_send"/>"/>
							&nbsp;&nbsp;
							<span style="width: 50px;color:#ff0033;">
		                        Refund Bill 1 refers to the refund based on product quantity. 
		                        Refund Bill 2 refers to the refund based on amount.
	                        </span>
					  </c:if>
				     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<c:if test="${not empty amazonOrder.orderChannel}">
						<c:if test="${amazonOrder.orderStatus ne 'Shipped' && fns:getUser().name eq amazonOrder.orderChannel}" >
						<input id="btnEdit" class="btn btn-warning" type="button" value="<spring:message code="sys_but_edit"/>" />
					   </c:if>
					</c:if>
				</td>
				</tr>
				<tr>
				   <td><b><spring:message code="amazon_order_form4"/></b></td>
				   <td>	${amazonOrder.country}</td>
				   <td><b><spring:message code="psi_inventory_order_date"/></b></td>
				   <td><fmt:formatDate value="${amazonOrder.purchaseDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				   <td><b><spring:message code="amazon_order_form3"/></b></td>
				   <td><fmt:formatDate value="${amazonOrder.lastUpdateDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				</tr>
				
				<tr>
				   <td><b><spring:message code="amazon_order_form5"/></b></td>
				   <td>	${amazonOrder.orderTotal}</td>
				   <td><b><spring:message code="amazon_order_form6"/></b></td>
				   <td>${amazonOrder.fulfillmentChannel}</td>
				   <td><b><spring:message code="amazon_order_form8"/></b></td>
				   <td><c:choose>
						<c:when test="${fn:startsWith(orderExtract.invoiceFlag,'1')}">
							<spring:message code="sys_but_yes"/>
						</c:when>
						<c:otherwise>
							<spring:message code="amazon_order_form_tips8"/>
						</c:otherwise>
					</c:choose>
					</td>
				</tr>
				
				<tr>
				   <td><b><spring:message code="amazon_order_form7"/></b></td>
				   <td>${amazonOrder.numberOfItemsShipped}</td>
				   <td><b><spring:message code="amazon_order_form37"/></b></td>
				   <td>${amazonOrder.numberOfItemsUnshipped}</td>
				   <td><b><spring:message code="amazon_order_form35"/></b></td>
				   <td>${amazonOrder.orderStatus}</td>
				</tr>
				
				<tr>
				   <td><b>IsPrime</b></td>
				   <td>
				     <c:if test="${not empty amazonOrder.isPrime }">
				      <c:choose>
						<c:when test="${'1' eq amazonOrder.isPrime}">
							<spring:message code="sys_but_yes"/>
						</c:when>
						<c:otherwise>
							<spring:message code="sys_but_no"/>
						</c:otherwise>
					  </c:choose>
					 </c:if> 
				   </td>
				   <td><b>IsBusinessOrder</b></td>
				   <td>
				     <c:if test="${not empty amazonOrder.isBusinessOrder }">
				       <c:choose>
						<c:when test="${'1' eq amazonOrder.isBusinessOrder}">
							<spring:message code="sys_but_yes"/>
						</c:when>
						<c:otherwise>
							<spring:message code="sys_but_no"/>
						</c:otherwise>
					  </c:choose>
				   </c:if>
				   </td>
				   <td><b>PurchaseOrderNumber</b></td>
				   <td>${amazonOrder.purchaseOrderNumber}</td>
				  
				</tr>
				
				<tr>
				 <td><b>IsPremiumOrder</b></td>
				   <td>
				     <c:if test="${not empty amazonOrder.isPremiumOrder }">
				      <c:choose>
						<c:when test="${'1' eq amazonOrder.isPremiumOrder}">
							<spring:message code="sys_but_yes"/>
						</c:when>
						<c:otherwise>
							<spring:message code="sys_but_no"/>
						</c:otherwise>
					  </c:choose>
					  </c:if>
				   </td>
				   <td><b>IsReplacementOrder</b></td>
				   <td>
				     <c:if test="${not empty amazonOrder.isReplacementOrder }">
				      <c:choose>
						<c:when test="${'1' eq amazonOrder.isReplacementOrder}">
							<spring:message code="sys_but_yes"/>(${amazonOrder.replacedOrderId})
						</c:when>
						<c:otherwise>
							<spring:message code="sys_but_no"/>
						</c:otherwise>
					  </c:choose>
					  </c:if>
				   </td>
				   <td><b>BuyerTaxInfo</b></td>
				   <td>${amazonOrder.buyerTaxInfo}</td>
				</tr>
				
				<tr class="warning">
					<td colspan="6"><spring:message code="amazon_order_form_tips2"/></td>
				</tr>
				<tr>
				   <td><b><spring:message code="amazon_order_form9"/></b></td>
				   <td>	${amazonOrder.buyerName}</td>
				   <td><b><spring:message code="amazon_order_form10"/></b></td>
				   <td><a target="_blank" href="${ctx}/amazoninfo/order?buyerEmail=${amazonOrder.buyerEmail}&purchaseDate=2014-1-1">${amazonOrder.buyerEmail}</a></td>
				   <td><b>Customer Info</b></td>
				   <td>
					    <c:if test="${not empty orderExtract.customId}">
					      <a target="_blank" href="${ctx}/amazoninfo/customers/view?customId=${orderExtract.customId}">ERP CustomerInfo</a>&nbsp;&nbsp;<b>|</b>&nbsp;&nbsp;<a target="_blank" href="${amazonOrder.link}${orderExtract.customId}">Amazon</a>
					   </c:if>
				   </td>
				</tr>
				
				<tr  class="success">
					<td colspan="6">Payment</td>
				</tr>
				
				<tr>
				   <td><b>Payment Method</b></td>
				   <td>	${amazonOrder.paymentMethod}</td>
				   <td><b>earliest ShipDate</b></td>
				   <td>${amazonOrder.earliestShipDate}</td>
				   <td><b>latest ShipDate</b></td>
				   <td>${amazonOrder.latestShipDate}</td>
				</tr>
				
			</tbody>
		</table>
		</div>
		
		
	
	<div style="float: left;width:100%;margin-left: 15px">
	<%-- 	<blockquote>
			<p style="font-size: 14px"><spring:message code="amazon_order_form_tips3"/></p>
		</blockquote> --%>
		<table id="contentTable" class="table table-bordered table-condensed">
		<tbody>
			<tr class="error">
				<c:if test="${not empty returnGoods }">
				  <td colspan='11'><spring:message code="amazon_order_form_tips3"/></td>
				</c:if>
				<c:if test="${empty returnGoods }">
				  <td colspan='10'><spring:message code="amazon_order_form_tips3"/></td>
				</c:if>
			</tr>
			<tr>
			       <td style="width: 3%"><input type="checkBox" id="checkall" checked></td>
				   <td style="width: 120px"><b><spring:message code="amazon_order_form20"/></b></td>
				   <td style="width: 100px"><b><spring:message code="amazon_order_form21"/></b></td>
				   <td style="width: 150px"><b><spring:message code="amazon_order_form22"/></b></td>
				   <td style="width: 50px"><b><spring:message code="amazon_order_form23"/></b></td>
				   <td style="width: 50px"><b><spring:message code="amazon_order_form7"/></b></td>
				   <c:if test="${not empty returnGoods }">
				       <td style="width: 50px;color:#ff0033;"><b><spring:message code="amazon_return_order_quantity"/></b></td>
				   </c:if>
				   <td style="width: 50px"><b>Unit Price</b></td>
				   <td style="width: 50px"><b><spring:message code="amazon_order_form25"/></b></td>
				   <td style="width: 50px"><b><spring:message code="amazon_order_form26"/></b></td>
				   <td style="width: 50px"><b><spring:message code="amazon_order_form27"/></b></td>
			</tr>
		
		
		<c:forEach items="${amazonOrder.items}" var="item">
			<tr>
			    <td>
			      <c:if test="${empty returnGoods}">
			        <input type="checkBox" class="chebox"  name="checkId" value="${item.id}" checked/>
			        <c:set var="showButton" value="1"/>
			      </c:if>
			       <c:if test="${not empty returnGoods}">
			         <c:set  var='isEidt' value='0'/>
			         
			          <c:if test="${returnGoods[item.sellersku].quantity eq item.quantityShipped &&returnGoods[item.sellersku].productName eq item.name && isEidt!='1'}">
			                <c:set  var='isEidt' value='1'/>
			          </c:if>
			          <c:if test="${(returnGoods[item.sellersku].quantity ne item.quantityShipped &&returnGoods[item.sellersku].productName eq item.name)||returnGoods[item.sellersku].productName ne item.name}">
			             <c:set var="showButton" value="1"/>
			          </c:if>  
			           
			         <c:if test="${'0' eq isEidt }"><input type="checkBox" class="chebox"  name="checkId" value="${item.id}" checked/></c:if>
			      </c:if>
			    </td>
				<td><a  href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.name}" target='_blank'>${item.name}</a></td>
				<td>${item.asin}</td>
				<td>${item.sellersku}</td>
				<td>${item.quantityOrdered}</td>
				<td>${item.quantityShipped}</td>
				<c:if test="${not empty returnGoods }">
				  <td style="color:#ff0033;"><b><a title='${returnGoods[item.sellersku].reason}'>${returnGoods[item.sellersku].quantity}</a></b></td>
				</c:if>
				<td>
				   <c:choose>
				      <c:when test="${item.quantityShipped>0 }"><fmt:formatNumber value="${item.itemPrice/item.quantityShipped}" /></c:when>
				      <c:when test="${item.quantityOrdered>0 }"><fmt:formatNumber value="${item.itemPrice/item.quantityOrdered}" /></c:when>
				      <c:otherwise></c:otherwise>
				   </c:choose>
				   
				</td>
				<td>${item.itemTax}</td>
				<td>${item.shippingPrice}</td>
				<td>${item.giftWrapPrice}</td>
			</tr>
			<tr>
				<td colspan="11">
					<h5><spring:message code="amazon_order_form_tips4"/>：</h5>
					<c:if test="${not empty item.shippingTax}">
						<spring:message code="amazon_order_form31"/>:${item.shippingTax}<br/>
					</c:if>
					<c:if test="${not empty item.giftWrapTax}">
						<spring:message code="amazon_order_form32"/>:${item.giftWrapTax}<br/>
					</c:if>
					<c:if test="${not empty item.shippingDiscount}">
						<spring:message code="amazon_order_form33"/>:${item.shippingDiscount}<br/>
					</c:if>
					<c:if test="${not empty item.promotionDiscount}">
						<spring:message code="amazon_order_form28"/>:${item.promotionDiscount}<br/>
					</c:if>
					<c:if test="${not empty item.promotionIds}">
						<spring:message code="amazon_order_form29"/> :${item.promotionIds}<br/>
					</c:if>
					<c:if test="${not empty item.giftMessageText}">
						<spring:message code="amazon_order_form34"/>:${item.giftMessageText}<br/>
					</c:if>
					<c:if test="${not empty item.conditionNote}">
						conditionNote:${item.conditionNote}<br/>
					</c:if>
					<c:if test="${not empty item.conditionId}">
						conditionId:${item.conditionId}<br/>
					</c:if>
					<c:if test="${not empty item.conditionSubtypeId}">
						conditionSubtypeId:${item.conditionSubtypeId}<br/>
					</c:if>
				</td>
			</tr>
			
		 </c:forEach>
		 <tr>
			  <td colspan="11"  style="text-align: right;vertical-align: right">
			      <c:if test="${'Shipped' eq amazonOrder.orderStatus&& '1' eq showButton}">
					    <c:if test="${totalMoney < amazonOrder.orderTotal }">
						   <input id="refund" class="btn btn-primary" type="button" value="Refund" onclick="refund()"/>
						</c:if>
						<c:if test="${totalMoney >= amazonOrder.orderTotal }">
						   <input id="otherRefund" class="btn btn-primary" type="button" value="Other Refund" onclick="otherRefund()"/>		
						</c:if>   
				  </c:if>	
			   </td>
		 </tr>
		</tbody>
	  </table>
	  </div>
	
	
		<div style="float: left;width:100%;margin-left: 15px">
		<c:if test="${not empty records}">
		<table class="table  table-bordered table-condensed">
		<tbody>
		   <tr class="warning">
			   <td colspan='6'>Refund Record
			   (
			    <c:if test="${fn:contains('de,fr,it,es,uk',amazonOrder.countryChar)}">
			       <a href="https://sellercentral.amazon.de/gp/orders-v2/details/?orderID=${amazonOrder.amazonOrderId}" target='_blank'>Amazon Seller Central Link</a>
			    </c:if>
			    <c:if test="${'com' eq amazonOrder.countryChar}">
			       <a href="https://sellercentral.amazon.com/gp/orders-v2/details/?orderID=${amazonOrder.amazonOrderId}" target='_blank'>Amazon Seller Central Link</a>
			    </c:if>
			    <c:if test="${'ca' eq amazonOrder.countryChar}">
			       <a href="https://sellercentral.amazon.ca/gp/orders-v2/details/?orderID=${amazonOrder.amazonOrderId}" target='_blank'>Amazon Seller Central Link</a>
			    </c:if>
			     <c:if test="${'jp' eq amazonOrder.countryChar}">
			       <a href="https://sellercentral.amazon.co.jp/gp/orders-v2/details/?orderID=${amazonOrder.amazonOrderId}" target='_blank'>Amazon Seller Central Link</a>
			    </c:if>
			      <c:if test="${'mx' eq amazonOrder.countryChar}">
			       <a href="https://sellercentral.amazon.com.mx/gp/orders-v2/details/?orderID=${amazonOrder.amazonOrderId}" target='_blank'>Amazon Seller Central Link</a>
			    </c:if>
			   )
			   </td>
			</tr>
			<tr>
				   <td><b>Product Name</b></td>
				   <td><b>Refund Date</b></td>
				   <td><b>Principal</b></td>
				   <td><b>Shipping</b></td>
				   <td><b>Total</b></td>
			</tr>
		
			<c:forEach items="${records}" var="rd" >
			       <c:set var="totalMoney" value="${totalMoney+rd.money+rd.shippingMoney}"/>
					<tr>
						<td>${rd.productName}</td>  
						<td>${rd.remark}</td>
						<td>${rd.money}</td> 
						<td>${rd.shippingMoney}</td> 
						<td><fmt:formatNumber value="${rd.money+rd.shippingMoney}" maxFractionDigits="2"/></td>
					</tr>
			</c:forEach>
		
		</tbody>
		<tfoot>
		  <tr>
		    <td colspan="4"><b>total</b></td>
		    <td><b><fmt:formatNumber value="${totalMoney}" maxFractionDigits="2"/></b></td>
		  </tr> 
		</tfoot>
	 </table>
		</c:if>
	</div>
	
	<div style="float: left;width:100%;margin-left: 15px">	 
		<c:if test="${not empty settlements }">
		   <!--  <blockquote>
			   <p style="font-size: 14px">Amazon Fee</p>
			</blockquote> -->
			<table class="table  table-bordered table-condensed">
			<tbody>
			  <tr class="info">
			   <td colspan='13'>Amazon Fee</td>
			  </tr> 
				<tr>
				       <td><b>ProductName</b></td>
				       <td><b>Quantity</b></td>
					   <td><b>sales</b></td>
					   <td><b>sales_no_tax</b></td>
					   <c:if test="${fn:contains(amazonOrder.countryChar,'de')||fn:contains(amazonOrder.countryChar,'fr')||fn:contains(amazonOrder.countryChar,'it')||fn:contains(amazonOrder.countryChar,'es')||fn:contains(amazonOrder.countryChar,'uk')}">
					     <td><b>cross_border</b></td>
					   </c:if>
					   <td><b>fba_per_unit</b></td>
					   <td><b>fba_weight_based</b></td>
					   <td><b>commission</b></td>
					   <td><b>cost price(退税价)</b></td>
					   <td><b>Transportation Fee</b></td>
					   <td><b>custom duty</b></td>
					   <c:if test="${reviewFee<0}"><td><b>Review Fee</b></td></c:if>
					   <td><b>profit</b></td>
				</tr>
			
			
		     <c:set var="totalProfit" value="0"/>
		     <c:set var="currency" value="€"/>
			<c:forEach items="${settlements}" var="settlement" >
				   <c:if test="${fn:contains(amazonOrder.countryChar,'de')||fn:contains(amazonOrder.countryChar,'fr')||fn:contains(amazonOrder.countryChar,'it')||fn:contains(amazonOrder.countryChar,'es')||fn:contains(amazonOrder.countryChar,'uk')}">
			             <c:set var="countryKey" value="${settlement[6] }_eu"/>
			             <c:if test="${fn:contains(amazonOrder.countryChar,'uk')}">
			                <c:set var="currency" value="￡"/>
			             </c:if>
				    </c:if>
				    <c:if test="${fn:contains(amazonOrder.countryChar,'com')}">
				       <c:set var="countryKey" value="${settlement[6] }_us"/>
				       <c:set var="currency" value="$"/>
				    </c:if>
				    <c:if test="${fn:contains(amazonOrder.countryChar,'ca')}">
				      <c:set var="countryKey" value="${settlement[6] }_ca"/>
				      <c:set var="currency" value="C$"/>
				    </c:if>
				     <c:if test="${fn:contains(amazonOrder.countryChar,'jp')}">
				        <c:set var="countryKey" value="${settlement[6] }_jp"/>
				        <c:set var="currency" value="¥"/>
				    </c:if>
				    <c:if test="${fn:contains(amazonOrder.countryChar,'mx')}">
				      <c:set var="countryKey" value="${item.name }_mx"/>
				      <c:set var="currency" value="M$"/>
				    </c:if>
					<tr>
					    <td>${settlement[6] }</td>
					    <td>${settlement[1] }</td>
					    
						<td>${settlement[12] }<c:if test="${settlement[11]<0}"><span style="color:#ff0033;">(${settlement[11]})</span></c:if></td>  
						
						
						<td>${settlement[8] }</td>
						<c:if test="${fn:contains(amazonOrder.countryChar,'de')||fn:contains(amazonOrder.countryChar,'fr')||fn:contains(amazonOrder.countryChar,'it')||fn:contains(amazonOrder.countryChar,'es')||fn:contains(amazonOrder.countryChar,'uk')}">
						 <td>${settlement[2] }</td>
						</c:if>  
						<td>${settlement[3] }</td> 
						<td>${settlement[4] }</td>
						<td>${settlement[5] }<c:if test="${settlement[11]<0}"><span style="color:#ff0033;">(${settlement[13]})</span></c:if></td>
						
						<td><fmt:formatNumber value="-${purchaseTaxPrice[settlement[6]]/(1+(dutyMap[settlement[6]]/100))*settlement[1] }" maxFractionDigits="2"/></td>
						<c:set  var='tranFee'  value='${empty transFeeMap[settlement[6]]||transFeeMap[settlement[6]]==0?tranAvgFee*gwMap[settlement[6]]:transFeeMap[settlement[6]]}'/>
						<td><fmt:formatNumber value="-${tranFee*settlement[1]}" maxFractionDigits="2"/></td>
						<td><fmt:formatNumber value="-${purchaseTaxPrice[settlement[6]]/(1+(supplierTaxMap[settlement[6]]/100))*dutyMap[countryKey]/100*settlement[1]}" maxFractionDigits="2"/></td>
						<c:set var="sProfit" value="${settlement[8]+settlement[2]+settlement[3]+settlement[4]+settlement[5]+reviewFee-(purchaseTaxPrice[settlement[6]]/(1+(dutyMap[settlement[6]]/100))+tranFee+purchaseTaxPrice[settlement[6]]/(1+(supplierTaxMap[settlement[6]]/100))*dutyMap[countryKey]/100 )*settlement[1]}"/>
						<c:set var="totalProfit" value="${totalProfit+sProfit}"/>
						 <c:if test="${reviewFee<0}"><td>${reviewFee }</td></c:if>
						<td><fmt:formatNumber value="${sProfit}" maxFractionDigits="2"/></td>
					 </tr>
			</c:forEach>
			<c:if test="${fn:contains(amazonOrder.countryChar,'com')&& fns:getDateByPattern(amazonOrder.purchaseDate,'yyyyMMdd')<'20170222'}">
		      <tr>
			    <td colspan="${(fn:contains(amazonOrder.countryChar,'de')||fn:contains(amazonOrder.countryChar,'fr')||fn:contains(amazonOrder.countryChar,'it')||fn:contains(amazonOrder.countryChar,'es')||fn:contains(amazonOrder.countryChar,'uk'))?(reviewFee<0?12:11):(reviewFee<0?11:10)}" style="text-align: right;vertical-align: right"><b>FBA Order Handling Fee</b></td>
			    <td>-1</td>
 			  </tr> 
 			  <tr>
			    <td colspan="${(fn:contains(amazonOrder.countryChar,'de')||fn:contains(amazonOrder.countryChar,'fr')||fn:contains(amazonOrder.countryChar,'it')||fn:contains(amazonOrder.countryChar,'es')||fn:contains(amazonOrder.countryChar,'uk'))?(reviewFee<0?12:11):(reviewFee<0?11:10)}" style="text-align: right;vertical-align: right"><b>Total</b></td>
			    <td><span style="${totalProfit-1>0?'color:#7CCD7C':'color:#FF3030'}"><b>
			    ${currency }<fmt:formatNumber value="${totalProfit-1}" maxFractionDigits="2"/>
			    </b></span></td>
 			  </tr>
 		    </c:if>
 		    <c:if test="${!fn:contains(amazonOrder.countryChar,'com') || (fn:contains(amazonOrder.countryChar,'com')&& fns:getDateByPattern(amazonOrder.purchaseDate,'yyyyMMdd')>='20170222')}">
		      <tr>
			    <td colspan="${(fn:contains(amazonOrder.countryChar,'de')||fn:contains(amazonOrder.countryChar,'fr')||fn:contains(amazonOrder.countryChar,'it')||fn:contains(amazonOrder.countryChar,'es')||fn:contains(amazonOrder.countryChar,'uk'))?(reviewFee<0?12:11):(reviewFee<0?11:10)}" style="text-align: right;vertical-align: right"><b>Total</b></td>
			    <td ><span style="${totalProfit>0?'color:#7CCD7C':'color:#FF3030'}"><b>${currency }<fmt:formatNumber value="${totalProfit}" maxFractionDigits="2"/></b></span></td>
 			  </tr> 
 		    </c:if>
			</tbody>
		
	      </table>
		</c:if>
		
		
		
		
		<c:if test="${empty settlements &&not empty purchaseTaxPrice&&'Canceled' ne amazonOrder.orderStatus}">
			<table class="table  table-bordered table-condensed">
			<tbody>
			  <tr class="info">
			   <td colspan='10'>Amazon Fee<span style="color:#ff0033;">(亚马逊结算报告未出,以下费用均为估算,仅供参考)</span></td>
			  </td> 
				<tr>
				       <td><b>ProductName</b></td>
					   <td><b>Price</b></td>
					   <td><b>Quantity</b></td>
					   <td><b>sales_no_tax</b></td>
					   <td><b>amz_fee</b></td>
					   <td><b>commission</b></td>
					   <td><b>cost price(退税价)</b></td>
					   <td><b>Transportation Fee</b></td>
					   <td><b>custom duty</b></td>
					   <td><b>profit</b></td>
				</tr>
			
			
		     <c:set var="totalProfit" value="0"/>
		     <c:set var="currency" value="€"/>

		     
		    
		    <c:set  var='showFeeFlog' value='false'/>
				<c:forEach items="${amazonOrder.items}" var="item">
				<c:if test="${item.quantityShipped!=0||item.quantityOrdered!=0 }">
				 <c:if test="${fn:contains(amazonOrder.countryChar,'de')}">
			       <c:set  var='dealFee' value='2.5'/>
			       <c:set  var='commissionFee' value='0.5'/>
			     </c:if>
			       <c:if test="${fn:contains(amazonOrder.countryChar,'fr')}">
			       <c:set  var='dealFee' value='2.5'/>
			        <c:set  var='commissionFee' value='0.5'/>
			     </c:if>
			       <c:if test="${fn:contains(amazonOrder.countryChar,'it')}">
			       <c:set  var='dealFee' value='2.5'/>
			        <c:set  var='commissionFee' value='0.5'/>
			     </c:if>
			       <c:if test="${fn:contains(amazonOrder.countryChar,'es')}">
			       <c:set  var='dealFee' value='2.5'/>
			        <c:set  var='commissionFee' value='0'/>
			     </c:if>
			       <c:if test="${fn:contains(amazonOrder.countryChar,'uk')}">
			       <c:set  var='dealFee' value='2'/>
			        <c:set  var='commissionFee' value='0.4'/>
			     </c:if>
			       <c:if test="${fn:contains(amazonOrder.countryChar,'com')}">
			       <c:set  var='dealFee' value='2.5'/>
			        <c:set  var='commissionFee' value='1'/>
			     </c:if>
			       <c:if test="${fn:contains(amazonOrder.countryChar,'ca')}">
			       <c:set  var='dealFee' value='3.5'/>
			        <c:set  var='commissionFee' value='1'/>
			     </c:if>
			       <c:if test="${fn:contains(amazonOrder.countryChar,'jp')}">
			       <c:set  var='dealFee' value='400'/>
			        <c:set  var='commissionFee' value='50'/>
			     </c:if>
			     <c:if test="${fn:contains(amazonOrder.countryChar,'mx')}">
			       <c:set  var='dealFee' value='20'/>
			        <c:set  var='commissionFee' value='18'/>
			     </c:if>
				   <c:if test="${fn:contains(amazonOrder.countryChar,'de')||fn:contains(amazonOrder.countryChar,'fr')||fn:contains(amazonOrder.countryChar,'it')||fn:contains(amazonOrder.countryChar,'es')||fn:contains(amazonOrder.countryChar,'uk')}">
			             <c:set var="countryKey" value="${item.name }_eu"/>
			             <c:if test="${fn:contains(amazonOrder.countryChar,'uk')}">
			                <c:set var="currency" value="￡"/>
			             </c:if>
				    </c:if>
				    <c:if test="${fn:contains(amazonOrder.countryChar,'com')}">
				       <c:set var="countryKey" value="${item.name }_us"/>
				       <c:set var="currency" value="$"/>
				    </c:if>
				    <c:if test="${fn:contains(amazonOrder.countryChar,'ca')}">
				      <c:set var="countryKey" value="${item.name }_ca"/>
				      <c:set var="currency" value="C$"/>
				    </c:if>
				      <c:if test="${fn:contains(amazonOrder.countryChar,'mx')}">
				      <c:set var="countryKey" value="${item.name }_mx"/>
				      <c:set var="currency" value="M$"/>
				    </c:if>
				     <c:if test="${fn:contains(amazonOrder.countryChar,'jp')}">
				        <c:set var="countryKey" value="${item.name }_jp"/>
				        <c:set var="currency" value="¥"/>
				    </c:if>
				    
					<tr> <c:set var="tempPrice" value="${item.itemPrice-(empty item.promotionDiscount?0:item.promotionDiscount)}"/>
					    <c:if test="${empty skuMap[item.sellersku][3] }"><c:set  var='showFeeFlog' value='true'/></c:if>
					    <c:if test="${empty skuMap[item.sellersku][4] }"><c:set  var='showFeeFlog' value='true'/></c:if>
					    <td>${item.name}</td>
						<td><fmt:formatNumber value="${tempPrice}" maxFractionDigits="2"/></td>
						<c:set  var='selectQuantity' value='0'/>
						<td>
						  <c:if test="${item.quantityShipped>0 }"><c:set  var='selectQuantity' value='${item.quantityShipped}'/>${item.quantityShipped}</c:if>
						  <c:if test="${item.quantityShipped==0 }"><c:set  var='selectQuantity' value='${item.quantityOrdered}'/> ${item.quantityOrdered}</c:if>
						</td>
						<td><c:set var="salesNotax" value="${(item.itemPrice-(empty item.promotionDiscount?0:item.promotionDiscount))/(1+vat)}"/><fmt:formatNumber value="${salesNotax}" maxFractionDigits="2"/></td>
						<c:if test="${'com' eq amazonOrder.countryChar}">
						  
						   <td ${empty skuMap[item.sellersku][3]?'style="color:#7EC0EE;"':''}><c:set var="dealFee" value="${(empty skuMap[item.sellersku][3]?dealFee:(skuMap[item.sellersku][3]>dealFee?(skuMap[item.sellersku][3]):dealFee) )*selectQuantity }"/>-<fmt:formatNumber value="${dealFee}" maxFractionDigits="2"/></td>
						</c:if>
						<c:if test="${'com' ne amazonOrder.countryChar}">
						  
						   <td ${empty skuMap[item.sellersku][3]?'style="color:#7EC0EE;"':''}><c:set var="dealFee" value="${(empty skuMap[item.sellersku][3]?dealFee:skuMap[item.sellersku][3] )*selectQuantity }"/>-<fmt:formatNumber value="${dealFee}" maxFractionDigits="2"/></td>
						</c:if>
						
						<td ${empty skuMap[item.sellersku][4]?'style="color:#7EC0EE;"':''}><c:set var="commission" value="${(empty skuMap[item.sellersku][4]?(tempPrice*0.15>commissionFee?tempPrice*0.15:commissionFee):(tempPrice*skuMap[item.sellersku][4]/100>commissionFee?(tempPrice*skuMap[item.sellersku][4]/100):commissionFee) ) }"/>-<fmt:formatNumber value="${commission }" maxFractionDigits="2"/></td>
						
						<td><fmt:formatNumber value="-${purchaseTaxPrice[item.name]/(1+(dutyMap[item.name]/100))*selectQuantity }" maxFractionDigits="2"/></td>
						<c:set  var='tranFee'  value='${empty transFeeMap[item.name]||transFeeMap[item.name]==0?tranAvgFee*gwMap[item.name]:transFeeMap[item.name]}'/>
						<td><fmt:formatNumber value="-${tranFee*selectQuantity}" maxFractionDigits="2"/></td>
						<td><fmt:formatNumber value="-${purchaseTaxPrice[item.name]/(1+(supplierTaxMap[item.name]/100))*dutyMap[countryKey]/100*selectQuantity}" maxFractionDigits="2"/></td>
						
						<c:set var="sProfit" value="${salesNotax-dealFee-commission-(purchaseTaxPrice[item.name]/(1+(dutyMap[item.name]/100))+tranFee+purchaseTaxPrice[item.name]/(1+(supplierTaxMap[item.name]/100))*dutyMap[countryKey]/100)*selectQuantity}"/>
						<c:set var="totalProfit" value="${totalProfit+sProfit}"/>
						<td><fmt:formatNumber value="${sProfit}" maxFractionDigits="2"/></td>
					 </tr>
				</c:if>	 
			</c:forEach>
			<c:if test="${fn:contains(amazonOrder.countryChar,'com')&& fns:getDateByPattern(amazonOrder.purchaseDate,'yyyyMMdd')<'20170222'}">
		      <tr>
			    <td colspan="9" style="text-align: right;vertical-align: right"><b>FBA Order Handling Fee</b></td>
			    <td>-1</td>
 			  </tr> 
 			  <tr>
 			    <c:if test="${showFeeFlog}">
 			       <td colspan="2" style="text-align: left;vertical-align: left">蓝色标注佣金和处理费按均价计算</td>
			       <td colspan="7" style="text-align: right;vertical-align: right"><b>Total</b></td>
			    </c:if>
 			    <c:if test="${!showFeeFlog}">
			       <td colspan="9" style="text-align: right;vertical-align: right"><b>Total</b></td>
			    </c:if>
			    <td><span style="${totalProfit-1>0?'color:#7CCD7C':'color:#FF3030'}"><b>
			    ${currency }<fmt:formatNumber value="${totalProfit-1}" maxFractionDigits="2"/>
			    </b></span></td>
 			  </tr>
 		    </c:if>
 		    <c:if test="${!fn:contains(amazonOrder.countryChar,'com') || (fn:contains(amazonOrder.countryChar,'com')&& fns:getDateByPattern(amazonOrder.purchaseDate,'yyyyMMdd')>='20170222')}">
		      <tr>
		        <c:if test="${showFeeFlog}">
 			       <td colspan="2" style="text-align: left;vertical-align: left">蓝色标注佣金和处理费按均价计算</td>
			       <td colspan="7" style="text-align: right;vertical-align: right"><b>Total</b></td>
			    </c:if>
 			    <c:if test="${!showFeeFlog}">
			       <td colspan="9" style="text-align: right;vertical-align: right"><b>Total</b></td>
			    </c:if>
			    <td ><span style="${totalProfit>0?'color:#7CCD7C':'color:#FF3030'}"><b>${currency }<fmt:formatNumber value="${totalProfit}" maxFractionDigits="2"/></b></span></td>
 			  </tr> 
 		    </c:if>
			</tbody>
		
	      </table>
		</c:if>
		</div>
		
		<div style="float: left;width:100%;margin-left: 15px">
		<ul class="nav nav-tabs" id="cNav">
			<li class=active><a href="#panel-0" data-toggle="tab"><spring:message code="amazon_order_form_tab_tips1"/></a></li>
			<li><a href="#panel-1" data-toggle="tab"><spring:message code="amazon_order_form_tab_tips2"/><span class="help-inline">(<spring:message code="amazon_order_form_tab_tips3"/>)</span></a></li>
		</ul>
		<div class="tab-content" id="cTab">
			 <div class="tab-pane active" id="panel-0">
			 	<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form11"/></label>
						<div class="controls">
							${amazonOrder.shippingAddress.name}
						</div>
				</div>
				<c:if test="${not empty amazonOrder.shippingAddress.phone}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form12"/></label>
						<div class="controls">
							${amazonOrder.shippingAddress.phone}
						</div>
					</div>
				</c:if>
				<c:if test="${not empty amazonOrder.shippingAddress.postalCode}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form13"/></label>
						<div class="controls">
							${amazonOrder.shippingAddress.postalCode}
						</div>
					</div>
				</c:if>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form14"/></label>
					<div class="controls">
						${amazonOrder.shippingAddress.countryCode}
					</div>
				</div>
				<c:if test="${not empty amazonOrder.shippingAddress.stateOrRegion}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form15"/></label>
						<div class="controls">
							${amazonOrder.shippingAddress.stateOrRegion}
						</div>
					</div>
				</c:if>
				<c:if test="${not empty amazonOrder.shippingAddress.city}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form16"/></label>
						<div class="controls">
							${amazonOrder.shippingAddress.city}
						</div>
					</div>
				</c:if>
				<c:if test="${not empty amazonOrder.shippingAddress.county}">
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form30"/></label>
					<div class="controls">
						${amazonOrder.shippingAddress.county}
					</div>
				</div>
				</c:if>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form17"/></label>
					<div class="controls">
						${amazonOrder.shippingAddress.addressLine1}
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form18"/></label>
					<div class="controls">
						${amazonOrder.shippingAddress.addressLine2}
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form19"/></label>
					<div class="controls">
						${amazonOrder.shippingAddress.addressLine3}
					</div>
				</div>		
			 </div>
			
			<c:set scope="page" value="${empty amazonOrder.invoiceAddress?amazonOrder.shippingAddress:amazonOrder.invoiceAddress}" var="invoice"/>
  			<input type="hidden" id="invoiceAddressId" value="${amazonOrder.invoiceAddress.id}" />
  			<!-- 可编辑部分！！！！！！！！！！！！！！！！！！！！！！！ -->
  			 <div class="tab-pane" id="panel-1">
  			 		
  			 		<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr1"/></label>
						<div class="controls">
							<span id="name">${orderExtract.rateSn}</span>
							<input style="display: none" name="rateSn" />
						</div>
					</div>
  			 		
 					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr2"/></label>
						<div class="controls">
							<span id="name">${invoice.name}</span>
							<input style="display: none" name="invoiceAddress.name" />
						</div>
					</div>
					
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr3"/></label>
						<div class="controls">
							<span id="phone">${invoice.phone}</span>
							<input style="display: none" name="invoiceAddress.phone" />
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr4"/></label>
						<div class="controls">
							<span id="postalCode">${invoice.postalCode}</span>
							<input style="display: none" name="invoiceAddress.postalCode" />
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr5"/></label>
						<div class="controls">
							<span id="countryCode">${invoice.countryCode}</span>
							<input style="display: none" name="invoiceAddress.countryCode" />
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr6"/></label>
						<div class="controls">
							<span id="stateOrRegion">${invoice.stateOrRegion}</span>
							<input name="invoiceAddress.stateOrRegion"  style="display: none"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr7"/></label>
						<div class="controls">
							<span id="city">${invoice.city}</span>
							<input name="invoiceAddress.city"  style="display: none"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr9"/></label>
						<div class="controls">
							<span id="addressLine1">
							${invoice.addressLine1}
							</span>
							<input name="invoiceAddress.addressLine1"  style="display: none;width: 400px" />
							<b class="help-inline"><spring:message code="amazon_order_form_tips6"/></b>
						</div>
					</div>	
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr10"/></label>
						<div class="controls">
							<span id="addressLine2">
							${invoice.addressLine2}
							</span>
							<input name="invoiceAddress.addressLine2"  style="display: none;width: 400px" />
							<b class="help-inline"><spring:message code="amazon_order_form_tips6"/></b>
						</div>
					</div>	
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr11"/></label>
						<div class="controls">
							<span id="addressLine3">
							${invoice.addressLine3}
							</span>
							<input name="invoiceAddress.addressLine3"  style="display: none;width: 400px" />
							<b class="help-inline"><spring:message code="amazon_order_form_tips6"/></b>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<shiro:hasPermission name="amazoninfo:order:editAddress">
								<input class="btn btn-primary"  type="button" id="update" value="<spring:message code="amazon_order_form_tips7"/>">
							</shiro:hasPermission>
							<input class="btn btn-primary"  type="button" value="<spring:message code="sys_but_save"/>" style="display: none" id="saveBtn" />
							<input class="btn"  type="button" value="<spring:message code="sys_but_reset"/>" style="display: none" id="reset" />
						</div>
					</div>	
  			 </div>
		</div>
		
		<%-- <div class="form-actions">
		  <c:if test="${'Shipped' eq amazonOrder.orderStatus&& '1' eq showButton}">
		    <c:if test="${totalMoney < amazonOrder.orderTotal }">
			   <input id="refund" class="btn btn-primary" type="button" value="Refund" onclick="refund()"/>
			</c:if>
			<c:if test="${totalMoney >= amazonOrder.orderTotal }">
			   <input id="otherRefund" class="btn btn-primary" type="button" value="Other Refund" onclick="otherRefund()"/>		
			</c:if>   
		  </c:if>	
			<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
		</div> --%>
		
		</div>
		
		
	</form:form>
</body>
</html>
