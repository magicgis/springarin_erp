<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊发货订单详情</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
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
			
			
			$("#print").click(function(){
				if($("#hasTax").val()=='2'){
					showRefundBillDiv();
				}else if($("#hasTax").val()=='4'){
					showPartRefundBillDiv();
				}else{
					var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' >";
					refundBillHtml=refundBillHtml+"<table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tbody>";
					refundBillHtml=refundBillHtml+"<tr><th>order:</th><td><input id='order'  type='text'  style='width:90%'/></td><th>invoice:</th><td><input  type='text'  id='invoice' style='width:90%'/></td><th>paymentMethod:</th><td><input style='width:90%' type='text' id='payment' class='payment'  name='payment'/></td></tr>";
					refundBillHtml=refundBillHtml+"<tr><th>delivery date:</th><td><input id='deliveryDate'  style='width: 130px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' /></td><th>remark:</th><td colspan='3'><input style='width:90%' type='text' id='remark' class='remark'  name='remark'/></td></tr>";
					refundBillHtml=refundBillHtml+"</tbody></table></div>";
					var submitChild = function (v, h, f) {
						if(h.find("#invoice").val()!=''&&isNaN(h.find("#invoice").val())){
							 $.jBox.tip("please input right number", 'error');
							 return false;
						}
						$("#print").attr("disabled","disabled");
						loading('<spring:message code="amazon_order_tips7"/>');
						var params = {};
						params.id = '${amazonUnlineOrder.id}';
						params.hasTax = $("#hasTax").val();
						params.country=$("#countryType").val();
						params.order=h.find("#order").val();
						params.invoice=h.find("#invoice").val();
						params.payment=h.find("#payment").val();
						params.deliveryDate=h.find("#deliveryDate").val();
						params.remark=h.find("#remark").val();
						$.post("${ctx}/amazoninfo/unlineOrder/invoice",$.param(params),function(data){
							top.$.jBox.closeTip(); 
							if(data==1){
								if(params.hasTax==0){
									windowOpen('${ctx}/../data/site/invoice/Unline_${amazonUnlineOrder.amazonOrderId}_nbill.pdf','<spring:message code="amazon_order_form_select1_option1"/>',800,600);
								}else{
									windowOpen('${ctx}/../data/site/invoice/Unline_${amazonUnlineOrder.amazonOrderId}_bill.pdf','<spring:message code="amazon_order_form_select1_option2"/>',800,600);
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
					console.log("===");
					$(this).attr("disabled","disabled");
					var html = "<table><tr><th><spring:message code="amazon_order_tips10"/>：</th><td><div class='input-append btn-group btn-input'><input id='mail' value='${amazonUnlineOrder.buyerEmail}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div></td>"+
					"<tr><th>BCC：</th><td><div class='input-append btn-group btn-input'><input id='bcc' value='' style='height: 25px;width:300px' class='span2' id='t' /></div></td></tr>"+
					"</table>";
					
					top.$.jBox.confirm(html,'<spring:message code="amazon_order_tips12"/>？',function(v,h,f){
						if(v=='ok'){
							
							if(validateMail(h.find("#mail").val())&&validateMail(h.find("#bcc").val())){
								loading('<spring:message code="amazon_order_tips13"/>');
								var params = {};
								params.id = '${amazonUnlineOrder.id}'; 
								params.mail = encodeURI(h.find("#mail").val());
								console.log(h.find("#bcc").val());
								if(h.find("#bcc").val()!=''){
									params.bcc = encodeURI(h.find("#bcc").val());
								}
								
								params.hasTax = $("#hasTax").val();
								params.country=$("#countryType").val();
								//params.order=h.find("#order").val();
							//	params.invoice=h.find("#invoice").val();
								window.location.href = "${ctx}/amazoninfo/unlineOrder/send?"+$.param(params);
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
				window.location.href = "${ctx}/amazoninfo/unlineOrder/edit?id="+id;
			});
			
			$("#saveBtn").click(function(){
				$(this).attr("disabled","disabled");
				var params = {};
				params.id = '${amazonUnlineOrder.id}';
				params['invoiceAddress.order.id']= '${amazonUnlineOrder.id}';
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
				$.post("${ctx}/amazoninfo/unlineOrder/save",$.param(params),function(data){
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
		});
		
		function showSendMailPartDiv(){
			var sendMailHtml="<div class='showSendMailHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund money</th></tr></thead><tbody>";
			<c:forEach items="${amazonUnlineOrder.items}" var="item">
			sendMailHtml=sendMailHtml+"<tr><td>${item.title}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sellersku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityOrdered}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.itemPrice}'/></td></tr>";
			</c:forEach>
			sendMailHtml=sendMailHtml+"<tr><th>refund date:</th><td><input id='refundDate'  style='width: 130px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' /></td><th>paymentMethod:</th><td><input style='width:90%' type='text' id='payment' class='payment'  name='payment'/></td></tr>";
			sendMailHtml=sendMailHtml+"</tbody></table>";
			
			var html = "<spring:message code="amazon_order_tips10"/>：<div class='input-append btn-group btn-input'><input id='mail' value='${amazonUnlineOrder.buyerEmail}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>"+
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
					params.id = '${amazonUnlineOrder.id}'; 
					params.mail = encodeURI(h.find("#mail").val());
					if(h.find("#bcc").val()!=''){
						params.bcc = encodeURI(h.find("#bcc").val());
					}
					
					params.hasTax = $("#hasTax").val();
					params.paymentMethod=ids;
					params.country=$("#countryType").val();
					params.marketplaceId=quantitys;
					params.lastUpdateDate=h.find("#refundDate").val();
					params.payment=h.find("#payment").val();
					window.location.href = "${ctx}/amazoninfo/unlineOrder/send?"+$.param(params);
				}else{
					top.$.jBox.tip("<spring:message code="amazon_order_tips14"/>!","error");
					return false;
				}
			}

			$.jBox(sendMailHtml, { title: "Choose Refund Items",width:600,submit: submitMailChild,persistent: true});
			
			
		}
		
		function validateMail(value) {
			if(value==''){
				return true;
			}
			var rs = true ;
			/* $(value.split(",")).each(function(i,data){
				var temp =  /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i.test(data);
				if(!temp){
					rs = false;
					return 
				}
			}); */
			return rs;
		}
		
		function showRefundBillDiv(){
			var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' >";
			refundBillHtml=refundBillHtml+"<table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund quantity</th></tr></thead><tbody>";
			<c:forEach items="${amazonUnlineOrder.items}" var="item">
				refundBillHtml=refundBillHtml+"<tr><td>${item.title}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sellersku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityOrdered}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.quantityOrdered}' /></td></tr>";
			</c:forEach>
			refundBillHtml=refundBillHtml+"<tr><th>order:</th><td><input id='order'  type='text'  style='width:90%'/></td><th>invoice:</th><td><input  type='text'  id='invoice' style='width:90%'/></td></tr><tr><th>refund date:</th><td><input id='refundDate'  style='width: 130px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' /></td><th>paymentMethod:</th><td><input style='width:90%' type='text' id='payment' class='payment'  name='payment'/></td></tr>";
			refundBillHtml=refundBillHtml+"<tr><th>remark:</th><td colspan='7'><input style='width:90%' type='text' id='remark' class='remark'  name='remark'/></td></tr>";
			
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
				
				if(h.find("#invoice").val()!=''&&isNaN(h.find("#invoice").val())){
					 $.jBox.tip("please input right number", 'error');
					 return false;
				}
				
				if(!isChecked){
					top.$.jBox.tip("please input quantity！", 'info',{timeout:3000});
					return false;
				}
			
				$("#print").attr("disabled","disabled");
				loading('<spring:message code="amazon_order_tips7"/>');
				var params = {};
				params.id = '${amazonUnlineOrder.id}';
				params.hasTax = $("#hasTax").val();
				params.paymentMethod=ids;
				params.marketplaceId=quantitys;
				params.country=$("#countryType").val();
				params.order=h.find("#order").val();
				params.invoice=h.find("#invoice").val();
				params.lastUpdateDate=h.find("#refundDate").val();
				params.payment=h.find("#payment").val();
				params.remark=h.find("#remark").val();
				$.post("${ctx}/amazoninfo/unlineOrder/invoice",$.param(params),function(data){
					top.$.jBox.closeTip(); 
					if(data==1){
						if(params.hasTax==0){
							windowOpen('${ctx}/../data/site/invoice/Unline_${amazonUnlineOrder.amazonOrderId}_nbill.pdf','<spring:message code="amazon_order_form_select1_option1"/>',800,600);
						}else{
							windowOpen('${ctx}/../data/site/invoice/Unline_${amazonUnlineOrder.amazonOrderId}_bill.pdf','<spring:message code="amazon_order_form_select1_option2"/>',800,600);
						}
						top.$.jBox.tip("<spring:message code="amazon_order_tips8"/>！","success",{persistent:false,opacity:0});
					}else{
						top.$.jBox.tip("<spring:message code="amazon_order_tips9"/>！","error",{persistent:false,opacity:0});
					}
					$("#print").removeAttr("disabled");
				});
				
				
			    return true;
			};

			$.jBox(refundBillHtml, { title: "Choose Refund Bill Items",width:600,height: 450,submit: submitChild,persistent: true});
		}
		
		function showPartRefundBillDiv(){
			var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px;' >";
			refundBillHtml=refundBillHtml+"<table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund money</th></tr></thead><tbody>";
			<c:forEach items="${amazonUnlineOrder.items}" var="item">
				refundBillHtml=refundBillHtml+"<tr><td>${item.title}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sellersku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityOrdered}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.itemPrice}' /></td></tr>";
			</c:forEach>
			refundBillHtml=refundBillHtml+"<tr><th>order:</th><td><input id='order'  type='text'  style='width:90%'/></td><th>invoice:</th><td><input  type='text'  id='invoice' style='width:90%'/></td></tr><tr><th>refund date:</th><td><input id='refundDate'  style='width: 130px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' /></td><th>paymentMethod:</th><td><input style='width:90%' type='text' id='payment' class='payment'  name='payment'/></td></tr>";
			refundBillHtml=refundBillHtml+"<tr><th>remark:</th><td colspan='7'><input style='width:90%' type='text' id='remark' class='remark'  name='remark'/></td></tr>";
			
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
							if(quantity==0){
								 quantityFlag=false;
								 return;
							}
							ids=ids+id+",";
							quantitys=quantitys+quantity+",";
						};
					
				});
				
				if(h.find("#invoice").val()!=''&&isNaN(h.find("#invoice").val())){
					 $.jBox.tip("please input right number", 'error');
					 return false;
				}
				
				if(!isChecked){
					top.$.jBox.tip("please input refund money！", 'info',{timeout:3000});
					return false;
				}
			
				$("#print").attr("disabled","disabled");
				loading('<spring:message code="amazon_order_tips7"/>');
				var params = {};
				params.id = '${amazonUnlineOrder.id}';
				params.hasTax = $("#hasTax").val();
				params.paymentMethod=ids;
				params.marketplaceId=quantitys;
				params.country=$("#countryType").val();
				params.order=h.find("#order").val();
				params.invoice=h.find("#invoice").val();
				params.lastUpdateDate=h.find("#refundDate").val();
				params.payment=h.find("#payment").val();
				params.remark=h.find("#remark").val();
				$.post("${ctx}/amazoninfo/unlineOrder/invoice",$.param(params),function(data){
					top.$.jBox.closeTip(); 
					if(data==1){
						if(params.hasTax==0){
							windowOpen('${ctx}/../data/site/invoice/Unline_${amazonUnlineOrder.amazonOrderId}_nbill.pdf','<spring:message code="amazon_order_form_select1_option1"/>',800,600);
						}else{
							windowOpen('${ctx}/../data/site/invoice/Unline_${amazonUnlineOrder.amazonOrderId}_bill.pdf','<spring:message code="amazon_order_form_select1_option2"/>',800,600);
						}
						top.$.jBox.tip("<spring:message code="amazon_order_tips8"/>！","success",{persistent:false,opacity:0});
					}else{
						top.$.jBox.tip("<spring:message code="amazon_order_tips9"/>！","error",{persistent:false,opacity:0});
					}
					$("#print").removeAttr("disabled");
				});
				
				
			    return true;
			};

		    $.jBox(refundBillHtml, { title: "Choose Refund Bill Items",width:600,height: 450,submit: submitChild,persistent: true, showScrolling: true});
		}
		
		function showSendMailDiv(){
			var sendMailHtml="<div class='showSendMailHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund quantity</th></tr></thead><tbody>";
			<c:forEach items="${amazonUnlineOrder.items}" var="item">
			sendMailHtml=sendMailHtml+"<tr><td>${item.title}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sellersku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityOrdered}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.quantityOrdered}'/></td></tr>";
			</c:forEach>
			sendMailHtml=sendMailHtml+"</tbody></table>";
			
		//	var html = "<spring:message code="amazon_order_tips10"/>：<div class='input-append btn-group btn-input'><input id='mail' value='${amazonUnlineOrder.buyerEmail}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>"+
		//	"<br/><spring:message code="amazon_order_tips11"/>：<div class='input-append btn-group btn-input'><input id='bcc' value='' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>";
			
			var html = "<table><tr><th><spring:message code="amazon_order_tips10"/>：</th><td><div class='input-append btn-group btn-input'><input id='mail' value='${amazonUnlineOrder.buyerEmail}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div></td>"+
			"<tr><th><spring:message code="amazon_order_tips11"/>：</th><td><div class='input-append btn-group btn-input'><input id='bcc' value='' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div></td>"+
			"</table>";
			
			
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
					params.id = '${amazonUnlineOrder.id}'; 
					params.mail = encodeURI(h.find("#mail").val());
					if(h.find("#bcc").val()!=''){
						params.bcc = encodeURI(h.find("#bcc").val());
					}
					
					params.hasTax = $("#hasTax").val();
					params.country=$("#countryType").val();
					params.paymentMethod=ids;
					params.marketplaceId=quantitys;
					//params.order=h.find("#order").val();
					//params.invoice=h.find("#invoice").val();
					window.location.href = "${ctx}/amazoninfo/unlineOrder/send?"+$.param(params);
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
		<li><a href="${ctx}/amazoninfo/unlineOrder">Unline Order List</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/unlineOrder/form?id=${amazonUnlineOrder.id}">Unline Order Detail</a></li>
	</ul>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="amazonUnlineOrder"  class="form-horizontal">
		<form:hidden path="id" id="id"/>
		
			<table class="table table-bordered table-condensed">
				<tbody>
					<tr class="info">
						<td colspan="6"><spring:message code="amazon_order_form_tips1"/></td>
					</tr>
					<tr>
					    <td><b><spring:message code="amazon_order_form1"/></b></td>
					    <td colspan='5'><b>${amazonUnlineOrder.amazonOrderId}</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					    <c:if test="${amazonUnlineOrder.orderStatus eq 'Shipped'||amazonUnlineOrder.orderStatus eq 'PaymentPending'||amazonUnlineOrder.orderStatus eq 'Pending'||amazonUnlineOrder.orderStatus eq 'Unshipped'}" >
						<select style="width: 100px" id="hasTax">
							<option value="0"><spring:message code="amazon_order_form_select1_option1"/></option>
							<option value="1"><spring:message code="amazon_order_form_select1_option2"/></option>
							<option value="2"><spring:message code="amazon_order_form_select1_option3"/></option>
							<option value="4"><spring:message code="amazon_order_form_select1_option5"/></option> 
						</select>
						&nbsp;&nbsp;&nbsp;
						<select style="width: 100px" id="countryType">
						    <option value="de">DE模板</option>
							<option value="fr">FR模板</option>
							<option value="it">IT模板</option>
							<option value="es">ES模板</option>
							<option value="uk">UK模板</option>
							
							<option value="com">US模板</option>
						    <option value="ca">CA模板</option>
							<option value="jp">JP模板</option>
							
						</select>
						<input class="btn btn-warning"  type="button" id="print"  value="<spring:message code="sys_but_print"/>"/>
						&nbsp;
						 <input class="btn btn-warning"  type="button" id="send" value="<spring:message code="sys_but_send"/>"/>
						</c:if>
					    <c:if test="${not empty amazonUnlineOrder.orderChannel}">
						  <c:if test="${amazonUnlineOrder.orderStatus ne 'Shipped' && fns:getUser().name eq amazonUnlineOrder.orderChannel}" >
							<input id="btnEdit" class="btn btn-warning" type="button" value="<spring:message code="sys_but_edit"/>" />
						  </c:if>
						</c:if>
					    </td>
					</tr>
					<tr>
					   <td><b><spring:message code="amazon_order_form2"/></b></td>
					   <td>	<fmt:formatDate value="${amazonUnlineOrder.purchaseDate}" pattern="yyyy-MM-dd HH:mm"/></td>
					   <td><b><spring:message code="amazon_order_form3"/></b></td>
					   <td><fmt:formatDate value="${amazonUnlineOrder.lastUpdateDate}" pattern="yyyy-MM-dd HH:mm"/></td>
					   <td><b>WareHouse</b></td>
					   <td>${amazonUnlineOrder.salesChannel.stockName}</td>
				    </tr>
				    
				    <tr>
					   <td><b><spring:message code="amazon_order_form5"/></b></td>
					   <td>	${amazonUnlineOrder.orderTotal}</td>
					   <td><b><spring:message code="amazon_order_form6"/></b></td>
					   <td>${amazonUnlineOrder.fulfillmentChannel}</td>
					   <td><b><spring:message code="amazon_order_form7"/></b></td>
					   <td>${amazonUnlineOrder.numberOfItemsShipped}</td>
				    </tr>
				     <tr>
					   <td><b><spring:message code="amazon_order_form37"/></b></td>
					   <td>	${amazonUnlineOrder.numberOfItemsUnshipped}</td>
					   <td><b><spring:message code="amazon_order_form8"/></b></td>
					   <td><c:choose>
							<c:when test="${fn:startsWith(amazonUnlineOrder.invoiceFlag,'1')}">
								<spring:message code="sys_but_yes"/>
							</c:when>
							<c:otherwise>
								<spring:message code="amazon_order_form_tips8"/>
							</c:otherwise>
						  </c:choose>
			          </td>
					   <td><b>OrderChannel</b></td>
					   <td>${amazonUnlineOrder.orderChannel}</td>
				    </tr>
				      <tr>
				        <td><b><spring:message code="amazon_order_form35"/></b></td>
					   <td>${amazonUnlineOrder.orderStatus}</td>
					   <td></td>
					   <td></td>
					   <td></td>
					   <td></td>
				    </tr>
				    <tr class="success">
						<td colspan="6">Shipment</td>
					</tr>
				  
				     <tr>
					   <td><b>OutBound Status:</b></td>
					   <td><c:if test="${amazonUnlineOrder.outBound eq '0'}">
						     未出库
					    </c:if>
					    <c:if test="${amazonUnlineOrder.outBound ne '0'}">
						   <c:if test="${amazonUnlineOrder.outBound eq '1'}">已出库</c:if>
						   <c:if test="${amazonUnlineOrder.outBound eq '2'}">部分出库</c:if>
						   <c:if test="${not empty amazonUnlineOrder.outBoundNo }">
						        (出库单号：
							        <c:forEach items="${fn:split(amazonUnlineOrder.outBoundNo,',')}" var="billNo">
							            <a href='${ctx}/psi/psiInventoryOut/view?billNo=${billNo}' target='_blank'>${billNo}</a>&nbsp;&nbsp;
							        </c:forEach>
						        )
						   </c:if>
					    </c:if>
			          </td>
					   <td><b>Supplier</b></td>
					   <td>${amazonUnlineOrder.supplier}</td>
					   <td><b>Bill NO.</b></td>
					   <td>${amazonUnlineOrder.billNo}</td>
				    </tr>
				    
				    <tr class="warning">
						<td colspan="6"><spring:message code="amazon_order_form_tips2"/></td>
					</tr>
				    <tr>
					   <td><b><spring:message code="amazon_order_form9"/></b></td>
					   <td>	${amazonUnlineOrder.buyerName}</td>
					   <td><b><spring:message code="amazon_order_form10"/></b></td>
					   <td>${amazonUnlineOrder.buyerEmail}</td>
					   <td></td>
					   <td></td>
				    </tr>
				    
				</tbody>
			</table>
			

		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<tr class="error">
			   <td colspan='9'><spring:message code="amazon_order_form_tips3"/></td>
			</tr>
			<tr>
				   <th style="width: 120px"><spring:message code="amazon_order_form20"/></th>
				   <th style="width: 100px"><spring:message code="amazon_order_form21"/></th>
				   <th style="width: 150px"><spring:message code="amazon_order_form22"/></th>
				   <th style="width: 50px"><spring:message code="amazon_order_form23"/></th>
				   <th style="width: 50px"><spring:message code="amazon_order_form7"/></th>
				   <th style="width: 50px"><spring:message code="amazon_order_form24"/></th>
				   <th style="width: 50px"><spring:message code="amazon_order_form25"/>(%)</th>
				   <th style="width: 50px"><spring:message code="amazon_order_form26"/></th>
				   <th style="width: 50px"><spring:message code="amazon_order_form27"/></th>
			</tr>
		<tbody>
		<c:forEach items="${amazonUnlineOrder.items}" var="item">
			<tr>
				<td><a  href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}${item.color !=''?'_':''}${item.color}" target='_blank'>${item.productName}<c:if test="${not empty item.color }">_${item.color }</c:if></a></td>
				<td>${item.asin}</td>
				<td>${item.sellersku}</td>
				<td>${item.quantityOrdered}</td>
				<td>${item.quantityOut}</td>
				<td>${item.itemPrice}</td>
				<td>${item.itemTax}</td>
				<td>${item.shippingPrice}</td>
				<td>${item.giftWrapPrice}</td>
			</tr>
			<tr>
				<td colspan="9">
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
		</tbody>
	   </table>

		<div style="margin-left: 15px">
		<ul class="nav nav-tabs" id="cNav">
			<li class=active><a href="#panel-0" data-toggle="tab"><spring:message code="amazon_order_form_tab_tips1"/></a></li>
			<li><a href="#panel-1" data-toggle="tab"><spring:message code="amazon_order_form_tab_tips2"/><span class="help-inline">(<spring:message code="amazon_order_form_tab_tips3"/>)</span></a></li>
		</ul>
		<div class="tab-content" id="cTab">
			 <div class="tab-pane active" id="panel-0">
			 	<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form11"/></label>
						<div class="controls">
							${amazonUnlineOrder.shippingAddress.name}
						</div>
				</div>
				<c:if test="${not empty amazonUnlineOrder.shippingAddress.phone}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form12"/></label>
						<div class="controls">
							${amazonUnlineOrder.shippingAddress.phone}
						</div>
					</div>
				</c:if>
				<c:if test="${not empty amazonUnlineOrder.shippingAddress.postalCode}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form13"/></label>
						<div class="controls">
							${amazonUnlineOrder.shippingAddress.postalCode}
						</div>
					</div>
				</c:if>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form14"/></label>
					<div class="controls">
						${amazonUnlineOrder.shippingAddress.countryCode}
					</div>
				</div>
				<c:if test="${not empty amazonUnlineOrder.shippingAddress.stateOrRegion}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form15"/></label>
						<div class="controls">
							${amazonUnlineOrder.shippingAddress.stateOrRegion}
						</div>
					</div>
				</c:if>
				<c:if test="${not empty amazonUnlineOrder.shippingAddress.city}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form16"/></label>
						<div class="controls">
							${amazonUnlineOrder.shippingAddress.city}
						</div>
					</div>
				</c:if>
				<c:if test="${not empty amazonUnlineOrder.shippingAddress.county}">
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form30"/></label>
					<div class="controls">
						${amazonUnlineOrder.shippingAddress.county}
					</div>
				</div>
				</c:if>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form17"/></label>
					<div class="controls">
						${amazonUnlineOrder.shippingAddress.addressLine1}
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form18"/></label>
					<div class="controls">
						${amazonUnlineOrder.shippingAddress.addressLine2}
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form19"/></label>
					<div class="controls">
						${amazonUnlineOrder.shippingAddress.addressLine3}
					</div>
				</div>		
			 </div>
			
			<c:set scope="page" value="${empty amazonUnlineOrder.invoiceAddress?amazonUnlineOrder.shippingAddress:amazonUnlineOrder.invoiceAddress}" var="invoice"/>
  			<input type="hidden" id="invoiceAddressId" value="${amazonUnlineOrder.invoiceAddress.id}" />
  			<!-- 可编辑部分！！！！！！！！！！！！！！！！！！！！！！！ -->
  			 <div class="tab-pane" id="panel-1">
  			 		
  			 		<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr1"/></label>
						<div class="controls">
							<span id="name">${amazonUnlineOrder.rateSn}</span>
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
							<input class="btn btn-primary"  type="button" id="update" value="<spring:message code="amazon_order_form_tips7"/>">
							<input class="btn btn-primary"  type="button" value="<spring:message code="sys_but_save"/>" style="display: none" id="saveBtn" />
							<input class="btn"  type="button" value="<spring:message code="sys_but_reset"/>" style="display: none" id="reset" />
						</div>
					</div>	
  			 </div>
		</div>
		</div>
		
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
