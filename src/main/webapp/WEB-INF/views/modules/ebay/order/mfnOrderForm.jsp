<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>mfn订单详情</title>
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
				$(this).attr("disabled","disabled");
				loading('正在生成账单...');
				var params = {};
				params.id = '${mfnOrder.id}';
				params.hasTax = $("#hasTax").val();
				$.post("${ctx}/ebay/order/invoice",$.param(params),function(data){
					top.$.jBox.closeTip(); 
					if(data==1){
						if(params.hasTax==0){
							windowOpen('${ctx}/../data/site/invoice/Ebay_${mfnOrder.orderId}_nbill.pdf','免税账单',800,600);
						}else{
							windowOpen('${ctx}/../data/site/invoice/Ebay_${mfnOrder.orderId}_bill.pdf','含税账单',800,600);
						}
						top.$.jBox.tip("生成账单成功！","success",{persistent:false,opacity:0});
					}else{
						top.$.jBox.tip("生成账单失败！","error",{persistent:false,opacity:0});
					}
					$("#print").removeAttr("disabled");
				});
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
				params.id = '${mfnOrder.id}';
				params['invoiceAddress.order.id']= '${mfnOrder.id}';
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
				$.post("${ctx}/amazonAndEbay/mfnOrder/save",$.param(params),function(data){
						$("#panel-1 input[type != 'button']").each(function(){
							$(this).parent().find("span").text($(this).val().trim());				
						});
						$("#invoiceAddressId").val(data);
						$("#panel-1 input").hide();
						$("#update").show();
						$("#panel-1 span").show();
						top.$.jBox.closeTip(); 
						top.$.jBox.tip("保存账单地址成功！","success",{persistent:false,opacity:0});
					
					$("#saveBtn").removeAttr("disabled");
				});
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazonAndEbay/mfnOrder">Order List</a></li>
		<li class="active"><a href="${ctx}/amazonAndEbay/mfnOrder/form?id=${mfnOrder.id}">Order Detail</a></li>
	</ul><br/>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="mfnOrder"  class="form-horizontal">
		<form:hidden path="id"/>
		<table class="table table-bordered table-condensed">
			<tbody>
				<tr class="info">
					<td colspan="6"><spring:message code="amazon_order_form_tips1"/></td>
				</tr>
				<tr>
				   <td><b><spring:message code="amazon_order_form1"/></b></td>
				   <td><b style="font-size: 14px">${mfnOrder.orderId}</b></td>
				   <td><b>Event</b></td>
				   <td>  <c:forEach items="${fn:split(mfnOrder.eventId,',')}" var="eventId">
						<a href="${ctx}/custom/event/form?id=${eventId}" target="blank">SPR-${eventId}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach></td>
				   <td><b><spring:message code="amazon_order_form2"/></b></td>
				   <td><fmt:formatDate value="${mfnOrder.buyTime}" type="both"/></td>
				</tr>
				
				<tr>
				   <td><b><spring:message code="amazon_order_form3"/></b></td>
				   <td><fmt:formatDate value="${mfnOrder.lastModifiedTime}" type="both"/></td>
				   <td><b>BillNo.</b></td>
				   <td>
				        <c:choose>
							<c:when test="${'DPD' eq mfnOrder.supplier}">
								<a style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DPD:${mfnOrder.trackNumber }" target='_blank' href='https://tracking.dpd.de/parcelstatus?locale=en_D2&query=${mfnOrder.trackNumber}'>
								    ${mfnOrder.groupBillNo}
								</a>
							</c:when>
							<c:when test="${'DHL' eq mfnOrder.supplier}">
								 <a style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DHL:${mfnOrder.trackNumber }" target='_blank' href='https://nolp.dhl.de/nextt-online-public/set_identcodes.do?lang=de&rfn=&extendedSearch=true&idc=${mfnOrder.trackNumber}'>
					                    ${mfnOrder.groupBillNo}
					             </a>
							</c:when>
							<c:when test="${'UPS' eq mfnOrder.supplier}">
								 <a style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="UPS:${mfnOrder.trackNumber }" target='_blank' href='http://www.allinonetracking.com/ups/tracking/?provider=ups&trackingcode=${mfnOrder.trackNumber}'>
					                    ${mfnOrder.groupBillNo}
					             </a>
							</c:when>
								<c:when test="${'USPS' eq mfnOrder.supplier}">
								 <a style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="USPS:${mfnOrder.trackNumber }" target='_blank' href='http://www.allinonetracking.com/usps/tracking/?provider=usps&trackingcode=${mfnOrder.trackNumber}'>
					                    ${mfnOrder.groupBillNo}
					             </a>
							</c:when>
							<c:otherwise>
								 ${mfnOrder.groupBillNo}
							</c:otherwise>
						</c:choose>
				   </td>
				   <td><b><spring:message code="ebay_order_subtotal"/></b></td>
				   <td>${ebayOrder.orderTotal}</td>
				</tr>
				
				<tr>
				   <td><b>Order Type</b></td>
				   <td>${mfnOrder.orderType eq '1'?'Marketing':(mfnOrder.orderType eq '2'||mfnOrder.orderType eq '5'?'Support':'Mfn')}</td>
				   <td><b>Country</b></td>
				   <td>${mfnOrder.country}</td>
				   <td><b>remark</b></td>
				   <td>${mfnOrder.remark }</td>
				</tr>
				
				<tr class="success">
					<td colspan="6"><spring:message code="amazon_order_form_tips2"/></td>
				</tr>
				
				<tr>
				   <td><b><spring:message code="amazon_order_form9"/></b></td>
				   <td>${mfnOrder.buyerUser}</td>
				   <td><b><spring:message code="amazon_order_form10"/></b></td>
				   <td>${mfnOrder.buyerUserEmail}</td>
				   <td></td>
				   <td></td>
				</tr>
			</tbody>
		</table>		


		<table id="contentTable" class="table table-striped table-bordered table-condensed">

		  <tr class="info">
					<td colspan="6"><spring:message code="amazon_order_form_tips3"/></td>
				</tr>
				
			<tr>
				   <th style="width: 120px"><spring:message code="amazon_order_form20"/></th>
				   <th style="width: 150px"><spring:message code="amazon_order_form22"/></th>
				   <th style="width: 50px"><spring:message code="amazon_order_form23"/></th>
				   <th style="width: 50px">quantityShipped</th>
				   <th style="width: 50px">unit price</th>
			</tr>

		<c:forEach items="${mfnOrder.items}" var="item">
			<tr>
				<td>${item.title}</td>
				<td>${item.sku}</td>
				<td>${item.quantityPurchased}</td>
				<td>${item.quantityShipped}</td>
				<td>${item.itemPrice}</td>
			</tr>
			<tr>
				<td colspan="8">
					<h5><spring:message code="custom_event_other"/>：</h5>
					<c:if test="${not empty item.itemTax}">
						<spring:message code="amazon_order_form25"/>:${item.itemTax}%<br/>
					</c:if>
					<c:if test="${not empty item.codFee}">
						FinalValueFee:${item.codFee}<br/>
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
							${mfnOrder.shippingAddress.name}
						</div>
				</div>
<%-- 				<c:if test="${not empty mfnOrder.shippingAddress.phone}"> --%>
<!-- 					<div class="control-group"> -->
<!-- 						<label class="control-label">联系电话</label> -->
<!-- 						<div class="controls"> -->
<%-- 							${mfnOrder.shippingAddress.phone} --%>
<!-- 						</div> -->
<!-- 					</div> -->
<%-- 				</c:if> --%>
				<c:if test="${not empty mfnOrder.shippingAddress.postalCode}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form13"/></label>
						<div class="controls">
							${mfnOrder.shippingAddress.postalCode}
						</div>
					</div>
				</c:if>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form14"/></label>
					<div class="controls">
						${mfnOrder.shippingAddress.countryCode}
					</div>
				</div>
				<c:if test="${not empty mfnOrder.shippingAddress.stateOrProvince}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form15"/></label>
						<div class="controls">
							${mfnOrder.shippingAddress.stateOrProvince}
						</div>
					</div>
				</c:if>
				<c:if test="${not empty mfnOrder.shippingAddress.country}">
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form30"/></label>
					<div class="controls">
						${mfnOrder.shippingAddress.country}
					</div>
				</div>
				</c:if>
				<c:if test="${not empty mfnOrder.shippingAddress.cityName}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form16"/></label>
						<div class="controls">
							${mfnOrder.shippingAddress.cityName}
						</div>
					</div>
				</c:if>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form_addr9"/></label>
					<div class="controls">
						${mfnOrder.shippingAddress.street}
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form_addr10"/></label>
					<div class="controls">
						${mfnOrder.shippingAddress.street1}
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form_addr11"/></label>
					<div class="controls">
						${mfnOrder.shippingAddress.street2}
					</div>
				</div>		
			 </div>
			
			<c:set scope="page" value="${empty mfnOrder.invoiceAddress?mfnOrder.shippingAddress:mfnOrder.invoiceAddress}" var="invoice"/>
  			<input type="hidden" id="invoiceAddressId" value="${mfnOrder.invoiceAddress.id}" />
  			<!-- 可编辑部分！！！！！！！！！！！！！！！！！！！！！！！ -->
  			 <div class="tab-pane" id="panel-1">
  			 		
  			 		<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form_addr1"/></label>
						<div class="controls">
							<span id="name">${mfnOrder.rateSn}</span>
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
