<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>ebay订单详情</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
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
			
			$("#print").click(function(){
				if($("#hasTax").val()=='2'){
					showRefundBillDiv();
				}else if($("#hasTax").val()=='4'){
					showPartRefundBillDiv();
				}else{
					$(this).attr("disabled","disabled");
					loading('正在生成账单...');
					var params = {};
					params.id = '${ebayOrder.id}';
					params.hasTax = $("#hasTax").val();
					$.post("${ctx}/ebay/order/invoice",$.param(params),function(data){
						top.$.jBox.closeTip(); 
						if(data==1){
							if(params.hasTax==0){
								windowOpen('${ctx}/../data/site/invoice/Ebay_${ebayOrder.orderId}_nbill.pdf','免税账单',800,600);
							}else{
								windowOpen('${ctx}/../data/site/invoice/Ebay_${ebayOrder.orderId}_bill.pdf','含税账单',800,600);
							}
							top.$.jBox.tip("生成账单成功！","success",{persistent:false,opacity:0});
						}else{
							top.$.jBox.tip("生成账单失败！","error",{persistent:false,opacity:0});
						}
						$("#print").removeAttr("disabled");
					});
				}
				
			});
			
			$("#send").click(function(){
				if($("#hasTax").val()=='2'){
					showSendMailDiv();
				}else if($("#hasTax").val()=='4'){
					showSendMailPartDiv();
				}else{
					$(this).attr("disabled","disabled");
					var html = "发往邮箱：<div class='input-append btn-group btn-input'><input id='mail' value='${ebayOrder.encryptionBuyerEmail}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>"+
					"<br/>秘密抄送：<div class='input-append btn-group btn-input'><input id='bcc' value='' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>";
					top.$.jBox.confirm(html,'确定要发送账单吗？',function(v,h,f){
						if(v=='ok'){
							if(validateMail(h.find("#mail").val())&&validateMail(h.find("#bcc").val())){
								loading('正在发送账单，请稍等...');
								var params = {};
								params.id = '${ebayOrder.id}'; 
								params.mail = encodeURI(h.find("#mail").val());
								params.bcc = encodeURI(h.find("#bcc").val());
								params.hasTax = $("#hasTax").val();
								window.location.href = "${ctx}/ebay/order/send?"+$.param(params);
							}else{
								top.$.jBox.tip("请输入正确格式的邮箱地址!","error");
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
			
			$("#saveBtn").click(function(){
				$(this).attr("disabled","disabled");
				var params = {};
				params.id = '${ebayOrder.id}';
				params['invoiceAddress.order.id']= '${ebayOrder.id}';
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
					top.$.jBox.tip("地址最多支持255个字符！","error",{persistent:false,opacity:0});
					$("#saveBtn").removeAttr("disabled");
					return;
				}
				loading('正在保存账单地址...');
				$.post("${ctx}/ebay/order/save",$.param(params),function(data){
					if($.isNumeric(data)){
						$("#panel-1 input[type != 'button']").each(function(){
							$(this).parent().find("span").text($(this).val().trim());				
						});
						$("#invoiceAddressId").val(data);
						$("#panel-1 input").hide();
						$("#update").show();
						$("#panel-1 span").show();
						top.$.jBox.closeTip(); 
						top.$.jBox.tip("保存账单地址成功！","success",{persistent:false,opacity:0});
					}else{
						top.$.jBox.tip("保存账单地址失败！","error",{persistent:false,opacity:0});
					}
					$("#saveBtn").removeAttr("disabled");
				});
			});
		});
		
		
		function showRefundBillDiv(){
			var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund quantity</th></tr></thead><tbody>";
			<c:forEach items="${ebayOrder.items}" var="item">
				refundBillHtml=refundBillHtml+"<tr><td>${fn:replace(item.title,'"','')}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityPurchased}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.quantityPurchased}' /></td></tr>";
			</c:forEach>
			
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
				params.id = '${ebayOrder.id}';
				params.hasTax = $("#hasTax").val();
				params.itemIds=ids;
				params.quantitys=quantitys;
			
				$.post("${ctx}/ebay/order/invoice",$.param(params),function(data){
					top.$.jBox.closeTip(); 
					if(data==1){
						if(params.hasTax==0){
							windowOpen('${ctx}/../data/site/invoice/Ebay_${ebayOrder.orderId}_nbill.pdf','Refund Bill',800,600);
						}else{
							windowOpen('${ctx}/../data/site/invoice/Ebay_${ebayOrder.orderId}_bill.pdf','Refund Bill',800,600);
						}
						top.$.jBox.tip("生成账单成功！","success",{persistent:false,opacity:0});
					}else{
						top.$.jBox.tip("生成账单失败！","error",{persistent:false,opacity:0});
					}
					$("#print").removeAttr("disabled");
				});
			    return true;
			};

			$.jBox(refundBillHtml, { title: "Choose Refund Bill Items",width:600,submit: submitChild,persistent: true});
		}
		
		
		function showPartRefundBillDiv(){
			var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund money</th></tr></thead><tbody>";
			<c:forEach items="${ebayOrder.items}" var="item">
				refundBillHtml=refundBillHtml+"<tr><td>${fn:replace(item.title,'"','')}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityPurchased}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.transactionPrice}' /></td></tr>";
			</c:forEach>
			
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
				params.id = '${ebayOrder.id}';
				params.hasTax = $("#hasTax").val();
				params.itemIds=ids;
				params.quantitys=quantitys;
				params.lastUpdateDate=h.find("#refundDate").val();
				params.payment=h.find("#payment").val();
			
				$.post("${ctx}/ebay/order/invoice",$.param(params),function(data){
					top.$.jBox.closeTip(); 
					if(data==1){
						if(params.hasTax==0){
							windowOpen('${ctx}/../data/site/invoice/Ebay_${ebayOrder.orderId}_nbill.pdf','Refund Bill',800,600);
						}else{
							windowOpen('${ctx}/../data/site/invoice/Ebay_${ebayOrder.orderId}_bill.pdf','Refund Bill',800,600);
						}
						top.$.jBox.tip("生成账单成功！","success",{persistent:false,opacity:0});
					}else{
						top.$.jBox.tip("生成账单失败！","error",{persistent:false,opacity:0});
					}
					$("#print").removeAttr("disabled");
				});
				
			    return true;
			};

			$.jBox(refundBillHtml, { title: "Choose Refund Bill Items",width:600,submit: submitChild,persistent: true});
		}
		
		
		function showSendMailDiv(){
			var sendMailHtml="<div class='showSendMailHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund quantity</th></tr></thead><tbody>";
			<c:forEach items="${ebayOrder.items}" var="item">
			sendMailHtml=sendMailHtml+"<tr><td>${fn:replace(item.title,'"','')}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityPurchased}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.quantityPurchased}'/></td></tr>";
			</c:forEach>
			sendMailHtml=sendMailHtml+"</tbody></table>";
			
			var html = "<spring:message code="amazon_order_tips10"/>：<div class='input-append btn-group btn-input'><input id='mail' value='${ebayOrder.encryptionBuyerEmail}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>"+
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
					params.id = '${ebayOrder.id}'; 
					params.mail = encodeURI(h.find("#mail").val());
					params.bcc = encodeURI(h.find("#bcc").val());
					params.hasTax = $("#hasTax").val();
					params.itemIds=ids;
					params.quantitys=quantitys;
					window.location.href = "${ctx}/ebay/order/send?"+$.param(params);
				}else{
					top.$.jBox.tip("<spring:message code="amazon_order_tips14"/>!","error");
					return false;
				}
			}

			$.jBox(sendMailHtml, { title: "Choose Refund Items",width:600,submit: submitMailChild,persistent: true});
		}
		
		
		function showSendMailPartDiv(){
			var sendMailHtml="<div class='showSendMailHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 100px'>title</th><th style='width: 100px'>sku</th><th style='width: 50px'>order quantity </th><th style='width: 50px'> refund money</th></tr></thead><tbody>";
			<c:forEach items="${ebayOrder.items}" var="item">
			sendMailHtml=sendMailHtml+"<tr><td>${fn:replace(item.title,'"','')}</td><td><input type='hidden' class='itemId' value='${item.id}'/>${item.sku}</td><td><input style='width:90%' type='text' readonly='readonly' class='oldQuantity' value='${item.quantityPurchased}'/></td><td><input style='width:90%' name='quantity' type='text' value='${item.transactionPrice}'/></td></tr>";
			</c:forEach>
			sendMailHtml=sendMailHtml+"</tbody></table>";
			
			var html = "<spring:message code="amazon_order_tips10"/>：<div class='input-append btn-group btn-input'><input id='mail' value='${ebayOrder.encryptionBuyerEmail}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>"+
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
					params.id = '${ebayOrder.id}'; 
					params.mail = encodeURI(h.find("#mail").val());
					params.bcc = encodeURI(h.find("#bcc").val());
					params.hasTax = $("#hasTax").val();
					params.itemIds=ids;
					params.quantitys=quantitys;
					
					window.location.href = "${ctx}/ebay/order/send?"+$.param(params);
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
			$(value.split(",")).each(function(i,data){
				var temp =  /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i.test(data);
				if(!temp){
					rs = false;
					return 
				}
			});
			return rs;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/ebay/order"><spring:message code="ebay_order_list"/></a></li>
		<li class="active"><a href="${ctx}/ebay/order/form?id=${ebayOrder.id}"><spring:message code="ebay_order_details"/></a></li>
	</ul>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="ebayOrder"  class="form-horizontal">
		<form:hidden path="id"/>
		<table class="table table-bordered table-condensed">
			<tbody>
				<tr class="info">
					<td colspan="6"><spring:message code="amazon_order_form_tips1"/></td>
				</tr>
				<tr>
				    <td>OrderId</td>
					<td colspan="5">
					   <b style="font-size: 14px">${ebayOrder.orderId}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b>
					      <c:if test="${not empty ebayOrder.paidTime||'0' ne ebayOrder.status}" >
					         <select style="width: 100px" id="hasTax">
								<option value="0"><spring:message code="amazon_order_form_select1_option1"/></option>
								<option value="1"><spring:message code="amazon_order_form_select1_option2"/></option>
							<%-- 	<option value="2"><spring:message code="amazon_order_form_select1_option3"/></option> --%>
							    <option value="2"><spring:message code="amazon_order_form_select1_option31"/></option>
								<option value="4"><spring:message code="amazon_order_form_select1_option32"/></option>
						    </select>
						    
						    &nbsp;&nbsp;&nbsp;
						<input class="btn btn-warning"  type="button" id="print"  value="<spring:message code="sys_but_print"/>"/>
						&nbsp;
						<input class="btn btn-warning"  type="button" id="send" value="<spring:message code="sys_but_send"/>"/>
					      </c:if>
					 
						
					</td>
				</tr>
				<tr>
				   <td><b><spring:message code="amazon_order_form2"/></b></td>
				   <td><fmt:formatDate value="${ebayOrder.createdTime}" type="both"/></td>
				   <td><b><spring:message code="amazon_order_form3"/></b></td>
				   <td><fmt:formatDate value="${ebayOrder.lastModifiedTime}" type="both"/></td>
				   <td><b>ShippedTime</b></td>
				   <td>${ebayOrder.shippedTime}</td>
				</tr>
				
				<tr>
				   <td><b><spring:message code="amazon_order_form26"/></b></td>
				   <td>${ebayOrder.shippingServiceCost}</td>
				   <td><b><spring:message code="ebay_order_subtotal"/></b></td>
				   <td>${ebayOrder.subtotal}</td>
				   <td><b><spring:message code="ebay_order_adjustmentAmount"/></b></td>
				   <td>${ebayOrder.adjustmentAmount}</td>
				</tr>
				
				<tr>
				   <td><b><spring:message code="amazon_order_form5"/></b></td>
				   <td>${ebayOrder.total}</td>
				   <td><b><spring:message code="ebay_order_amountPaid"/></b></td>
				   <td>${ebayOrder.amountPaid}</td>
				   <td><b>AmountSaved</b></td>
				   <td>${ebayOrder.amountSaved}</td>
				</tr>
				
				<tr>
				   <td><b><spring:message code="ebay_order_shippinginsuranceCost"/></b></td>
				   <td>${ebayOrder.shippinginsuranceCost}</td>
				   <td><b>FeeorcreditAmount</b></td>
				   <td>${ebayOrder.feeorcreditAmount}</td>
				   <td><b>PaymentAmount</b></td>
				   <td>${ebayOrder.paymentorrefundAmount}</td>
				</tr>
				
				<tr>
				   <td><b><spring:message code="amazon_order_form23"/></b></td>
				   <td>${ebayOrder.sellerEmail}</td>
				   <td><b><spring:message code="ebay_order_paymentStatus"/></b></td>
				   <td>${ebayOrder.paymentStatus}</td>
				   <td></td>
				   <td></td>
				</tr>
		
				<tr class="success">
					<td colspan="6">Payment</td>
				</tr>
				<tr>
				   <td><b>Payment Method</b></td>
				   <td>${ebayOrder.paymentMethods}</td>
				   <td><b>Payment No.</b></td>
				   <td>${ebayOrder.externaltransactionId}</td>
				   <td>Payment Time</td>
				   <td><fmt:formatDate value="${ebayOrder.paidTime}" type="both"/></td>
				</tr>
				<tr class="warning">
					<td colspan="6"><spring:message code="amazon_order_form_tips2"/></td>
				</tr>
				<tr>
				   <td><b><spring:message code="amazon_order_form9"/></b></td>
				   <td>${ebayOrder.buyerUserId}</td>
				   <td><b><spring:message code="amazon_order_form10"/></b></td>
				   <td>${ebayOrder.encryptionBuyerEmail}</td>
				   <td><spring:message code="amazon_order_form8"/></td>
				   <td><c:choose>
					<c:when test="${fn:startsWith(ebayOrder.invoiceFlag,'1')}">
						<spring:message code="sys_but_yes"/>
					</c:when>
					<c:otherwise>
						<spring:message code="amazon_order_form_tips8"/>
					</c:otherwise>
				</c:choose></td>
				</tr>
				
			</tbody>
		</table>	
	
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
	        <tr class="error">
					<td colspan="6"><spring:message code="amazon_order_form_tips3"/></td>
			</tr>
			<tr>
				   <th style="width: 120px"><spring:message code="amazon_order_form20"/></th>
				   <th style="width: 150px"><spring:message code="amazon_order_form22"/></th>
				   <th style="width: 50px"><spring:message code="amazon_order_form23"/></th>
				   <th style="width: 50px"><spring:message code="amazon_order_form24"/></th>
				   <th style="width: 50px"><spring:message code="ebay_order_sellingmanagersalesrecordNumber"/></th>
				   <th style="width: 50px">ItemId</th>
			</tr>

		<c:forEach items="${ebayOrder.items}" var="item">
			<tr>
				<td>${item.title}</td>
				<td>${item.sku}</td>
				<td>${item.quantityPurchased}</td>
				<td>${item.transactionPrice}</td>
				<td>${item.sellingmanagersalesrecordNumber}</td>
				<td><a target='_blank' href='https://www.ebay.${ebayOrder.country}/itm/${item.itemId}'>${item.itemId}</a></td>
			</tr>
			<tr>
				<td colspan="8">
					<h5><spring:message code="custom_event_other"/>：</h5>
					<c:if test="${not empty item.taxes}">
						<spring:message code="amazon_order_form25"/>:${item.taxes}<br/>
					</c:if>
					<c:if test="${not empty item.vatPercent}">
						<spring:message code="ebay_order_vatPercent"/>:${item.vatPercent}<br/>
					</c:if>
					<c:if test="${not empty item.paypalEmailAddress}">
						PaypalEmailAddress:${item.paypalEmailAddress}<br/>
					</c:if>
					<c:if test="${not empty item.paisapayId}">
						PaisapayId:${item.paisapayId}<br/>
					</c:if>
					<c:if test="${not empty item.invoiceSentTime}">
						<spring:message code="ebay_order_invoiceSentTime"/> :${item.invoiceSentTime}<br/>
					</c:if>
					<c:if test="${not empty item.commentText}">
						<spring:message code="ebay_order_commentText"/>:${item.commentText}<br/>
					</c:if>
					<c:if test="${not empty item.commentType}">
						<spring:message code="ebay_order_commentType"/>:${item.commentType}<br/>
					</c:if>
					<c:if test="${not empty item.targetUser}">
						<spring:message code="ebay_order_targetUser"/>:${item.targetUser}<br/>
					</c:if>
					<c:if test="${not empty item.finalValueFee}">
						FinalValueFee:${item.finalValueFee}<br/>
					</c:if>
				</td>
			</tr>
		</c:forEach>

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
							${ebayOrder.shippingAddress.name}
						</div>
				</div>
<%-- 				<c:if test="${not empty ebayOrder.shippingAddress.phone}"> --%>
<!-- 					<div class="control-group"> -->
<!-- 						<label class="control-label">联系电话</label> -->
<!-- 						<div class="controls"> -->
<%-- 							${ebayOrder.shippingAddress.phone} --%>
<!-- 						</div> -->
<!-- 					</div> -->
<%-- 				</c:if> --%>
				<c:if test="${not empty ebayOrder.shippingAddress.postalCode}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form13"/></label>
						<div class="controls">
							${ebayOrder.shippingAddress.postalCode}
						</div>
					</div>
				</c:if>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form14"/></label>
					<div class="controls">
						${ebayOrder.shippingAddress.countryCode}
					</div>
				</div>
				<c:if test="${not empty ebayOrder.shippingAddress.stateOrProvince}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form15"/></label>
						<div class="controls">
							${ebayOrder.shippingAddress.stateOrProvince}
						</div>
					</div>
				</c:if>
				<c:if test="${not empty ebayOrder.shippingAddress.county}">
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form30"/></label>
					<div class="controls">
						${ebayOrder.shippingAddress.county}
					</div>
				</div>
				</c:if>
				<c:if test="${not empty ebayOrder.shippingAddress.cityName}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form16"/></label>
						<div class="controls">
							${ebayOrder.shippingAddress.cityName}
						</div>
					</div>
				</c:if>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form_addr9"/></label>
					<div class="controls">
						${ebayOrder.shippingAddress.street}
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form_addr10"/></label>
					<div class="controls">
						${ebayOrder.shippingAddress.street1}
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form_addr11"/></label>
					<div class="controls">
						${ebayOrder.shippingAddress.street2}
					</div>
				</div>		
			 </div>
			
			<c:set scope="page" value="${empty ebayOrder.invoiceAddress?ebayOrder.shippingAddress:ebayOrder.invoiceAddress}" var="invoice"/>
  			<input type="hidden" id="invoiceAddressId" value="${ebayOrder.invoiceAddress.id}" />
  			<!-- 可编辑部分！！！！！！！！！！！！！！！！！！！！！！！ -->
  			 <div class="tab-pane" id="panel-1">
  			 		
  			 		<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr1"/></label>
						<div class="controls">
							<span id="name">${ebayOrder.rateSn}</span>
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
					
<!-- 					<div class="control-group"> -->
<!-- 						<label class="control-label">联系电话</label> -->
<!-- 						<div class="controls"> -->
<%-- 							<span id="phone">${invoice.phone}</span> --%>
<!-- 							<input style="display: none" name="invoiceAddress.phone" /> -->
<!-- 						</div> -->
<!-- 					</div> -->
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
							<span id="stateOrRegion">${invoice.stateOrProvince}</span>
							<input name="invoiceAddress.stateOrProvince"  style="display: none"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr7"/></label>
						<div class="controls">
							<span id="city">${invoice.cityName}</span>
							<input name="invoiceAddress.cityName"  style="display: none"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr9"/></label>
						<div class="controls">
							<span id="addressLine1">
							${invoice.street}
							</span>
							<input name="invoiceAddress.street"  style="display: none;width: 400px" />
							<b class="help-inline"><spring:message code="amazon_order_form_tips6"/></b>
						</div>
					</div>	
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr10"/></label>
						<div class="controls">
							<span id="addressLine2">
							${invoice.street1}
							</span>
							<input name="invoiceAddress.street1"  style="display: none;width: 400px" />
							<b class="help-inline"><spring:message code="amazon_order_form_tips6"/></b>
						</div>
					</div>	
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr11"/></label>
						<div class="controls">
							<span id="addressLine3">
							${invoice.street2}
							</span>
							<input name="invoiceAddress.street2"  style="display: none;width: 400px" />
							<b class="help-inline"><spring:message code="amazon_order_form_tips6"/></b>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<input class="btn btn-primary"  type="button" id="update" value="<spring:message code="sys_but_edit"/>">
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
