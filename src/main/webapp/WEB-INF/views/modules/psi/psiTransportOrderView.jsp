<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Transport Manager</title>
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
	
		if(!(top)){
			top=self;
		}
	
		$(document).ready(function() {
			 $("a[rel='popover']").popover({trigger:'hover',container:"form"});
			 
			 $(".confirmPay").on("click",function(){
				 if($(this).text()=="支付运费"){
						top.$.jBox.confirm('是否支付运费?','支付确认',function(v,h,f){
							if(v=='ok'){
								var params = {};
								params.id = '${psiTransportOrder.id}'; 
								$.get("${ctx}/psi/psiTransportOrder/confirmPay?"+$.param(params),function(data){
									if(data){
										 $(".confirmPay").text("已支付运费");
										 $(".confirmPay").removeClass("btn-warning");
										top.$.jBox.tip("Confirm Success!");
									}else{
										top.$.jBox.tip("Confirm Fail!");
									}
								});
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				 } 
			 });
			 
			 
		});
		</script>
		
		<% 
		response.setHeader("Cache-Control","no-store"); 
		response.setHeader("Pragrma","no-cache"); 
		response.setDateHeader("Expires",0); 
		%> 
	</head>
	<body>
		<ul class="nav nav-tabs">
			<li><a href="${ctx}/psi/psiTransportOrder/"><spring:message code="psi_transport_list"/></a></li>
			<li class="active"><a href="#">	<c:if test="${psiTransportOrder.model eq '0'}"><spring:message code="psi_transport_air"/></c:if><c:if test="${psiTransportOrder.model eq '1'}"><spring:message code="psi_transport_sea"/></c:if><c:if test="${psiTransportOrder.model eq '2'}"><spring:message code="psi_transport_expr"/></c:if></a></li>
		</ul><br/>
		<form:form id="inputForm" modelAttribute="psiTransportOrder" action="${ctx}/psi/psiTransportOrder/save" method="post" class="form-horizontal">
		<blockquote>
			<p style="font-size: 14px">Basic information</p>
		</blockquote>
				
				<div style="float:left;width:98%">
					<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:100px"><b><spring:message code="psi_transport_type"/>:</b></label>
						<div class="controls" style="margin-left:120px" >
							<c:if test="${psiTransportOrder.transportType eq '0'}"><spring:message code="psi_transport_local"/></c:if>
							<c:if test="${psiTransportOrder.transportType eq '1'}"><spring:message code="psi_transport_fba"/></c:if>
							<c:if test="${psiTransportOrder.transportType eq '3'}"><spring:message code="psi_transport_offLine"/></c:if>
						</div>
					</div>
					<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b><spring:message code="psi_transport_actualDate"/>:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="etaDate" type="text" readonly="readonly" style="width:95%" value="<fmt:formatDate value="${psiTransportOrder.etaDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
				</div>
				
			<c:if test="${psiTransportOrder.model eq '1'}">
				<div style="float:left;width:98%">
					<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:100px"><b><spring:message code="psi_transport_seaModel"/>:</b></label>
						<div class="controls" style="margin-left:120px" >
							<input type="text"  style="width:95%" readonly="readonly" value="${psiTransportOrder.oceanModel}"/>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${psiTransportOrder.transportType eq '3'}">
				  <div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b><spring:message code="psi_transport_offLine"/>:</b></label>
					<div class="controls" style="margin-left:120px" >
					    <input type="text" style="width: 95%" readonly="readonly" value="${psiTransportOrder.unlineOrder}" />
					</div>
				 </div>
			</c:if>	
			<c:if test="${psiTransportOrder.transportType eq '1'}">
			<div class="control-group" style="float:left;width:50%;height:25px">
				<label class="control-label" style="width:100px"><b>ShipmentId:</b></label>
				<div class="controls" style="margin-left:120px">
					<c:forEach items="${fn:split(psiTransportOrder.shipmentId,',')}" var="shipmentId">
						<a  target="_blank" href="${ctx}/psi/fbaInbound?shipmentId=${shipmentId}&country=">${shipmentId}</a>
					</c:forEach>
				</div>
			</div>
			
			</c:if>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b><spring:message code="psi_transport_fromHouse"/>:</b></label>
				<div class="controls" style="margin-left:120px" >
					<input type="text"  style="width:95%" readonly="readonly" value="${psiTransportOrder.fromStore.stockSign}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b><spring:message code="psi_transport_toHouse"/>:</b></label>
				<div class="controls"  style="margin-left:120px" >
					<input type="text"  style="width:95%" readonly="readonly" value="${psiTransportOrder.toStore.stockSign}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>Departure ${psiTransportOrder.model eq '1'?'Port':'Airport'}:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="orgin" type="text" maxlength="10" style="width:95%" readonly="readonly" value="${psiTransportOrder.orgin}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>Destination ${psiTransportOrder.model eq '1'?'Port':'Airport'}:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="destination" type="text" maxlength="10" style="width:95%"  readonly="readonly" class="required" value="${psiTransportOrder.destination}"/>
					</div>
			</div>
		</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>${psiTransportOrder.model eq '1'?'Ship':'Airline'}Company:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="carrier" type="text"  maxlength="10" style="width:95%"  readonly="readonly" class="required" value="${psiTransportOrder.carrier}"/>
					</div>
			</div>
			
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b><spring:message code="psi_transport_departured"/>:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="etdDate"  type="text" readonly="readonly" style="width:95%" value="<fmt:formatDate value="${psiTransportOrder.etdDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b><spring:message code="psi_transport_estimateDate"/>:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="preEtaDate" type="text" readonly="readonly" style="width:95%" value="<fmt:formatDate value="${psiTransportOrder.etaDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>pickUp Date:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="pickUpDate" type="text" readonly="readonly" style="width:95%" value="<fmt:formatDate value="${psiTransportOrder.pickUpDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
		
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>Weight:</b></label>
					<div class="controls" style="margin-left:120px" >
						<div class="input-prepend input-append">
						<input name="weight" type="text" style="width:80%"  readonly="readonly" class="required" value="${psiTransportOrder.weight}"/> <span class="add-on">kg</span>
						</div>
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>Volume:</b></label>
					<div class="controls" style="margin-left:120px" >
						<div class="input-prepend input-append">
						<input type="text" name="volume"  style="width:80%"  readonly="readonly" class="required" value="${psiTransportOrder.volume}"/> <span class="add-on">m³</span>
						</div>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">CTNS:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="boxNumber" type="text" readonly style="width:95%" value="${psiTransportOrder.boxNumber}"/>
					</div>
			</div>
			
			
			<shiro:hasPermission name="psi:transport:edit">
				<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>Unit Price:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="unitPrice" type="text" maxlength="10" readonly="readonly" style="width:95%" id="unitPrice" class="required price" value="${psiTransportOrder.unitPrice}"/>
					</div>
				</div>
			</shiro:hasPermission>
		</div>
		
		<div style="float:left;width:98%">
		<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>Transport NO.:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input  type="text" readonly style="width:95%" value="${psiTransportOrder.transportNo}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>Status:</b></label>
					<div class="controls" style="margin-left:120px" >
						<div class="input-prepend input-append">
						<c:if test="${psiTransportOrder.transportSta eq '0' }"><span class="label  label-important"><spring:message code="psi_transport_new"/></span></c:if>
						<c:if test="${psiTransportOrder.transportSta eq '1' }"><span class="label" style="background-color:#DCB5FF"><spring:message code="psi_transport_outbound"/></span></c:if>
						<c:if test="${psiTransportOrder.transportSta eq '2' }"><span class="label  label-warning"><spring:message code="psi_transport_departed"/></span></c:if>
						<c:if test="${psiTransportOrder.transportSta eq '3' }"><span class="label  label-info"><spring:message code="psi_transport_reached"/></span></c:if>
						<c:if test="${psiTransportOrder.transportSta eq '4' }"><span class="label"  style="background-color:#00E3E3"><spring:message code="psi_transport_parts_arrvied"/></span></c:if>
						<c:if test="${psiTransportOrder.transportSta eq '5' }"><span class="label  label-success"><spring:message code="psi_transport_arrived"/></span></c:if>
						<c:if test="${psiTransportOrder.transportSta eq '8' }"><span class="label  label-inverse"><spring:message code="psi_transport_canceled"/></span></c:if>
						</div>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>Pay Status:</b></label>
					<div class="controls" style="margin-left:120px" >
						<div class="input-prepend input-append">
						<c:if test="${psiTransportOrder.paymentSta eq '0' }"><span class="label  label-important">UnPay</span></c:if>
						<c:if test="${psiTransportOrder.paymentSta eq '1' }"><span class="label  label-warning">PartsPay</span></c:if>
						<c:if test="${psiTransportOrder.paymentSta eq '2' }"><span class="label  label-success">OverPay</span></c:if>
						</div>
					</div>
			</div>
			<c:if test="${psiTransportOrder.model eq '0' &&psiTransportOrder.toStore.type eq '0'}">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>Batch:</b></label>
				<div class="controls" style="margin-left:120px" >
				<input type="text" readonly="readonly" value="${psiTransportOrder.planeNum }" style="width:95%"/>
				</div>
			</div>
			</c:if>
		</div>
		
		<c:if test="${psiTransportOrder.model eq '1' && psiTransportOrder.oceanModel eq 'FCL'}">
			<div style="float:left; width:98%;" id="showContainer">
				<blockquote  style="float:left;">
					<div style="float: left"><p style="font-size: 14px"><spring:message code="psi_transport_container"/></p></div>
				</blockquote>
				
				
				<table id="containerTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						   <th style="width: 20%">Container Type</th>
						   <th style="width: 10%">Quantity</th>
						   <th style="width: 10%">Unit Price</th>
						   <th style="width: 20%">Remark</th>
					</tr>
				</thead>
				<tbody>
					<c:if test="${not empty psiTransportOrder.id }">
						<c:forEach items="${psiTransportOrder.containerItems}"  var="item">
							<tr>
							<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.containerType}"/></td>
							<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.quantity}"/></td>
							<td><input type="text" maxlength="100" style="width:80%"  readonly="readonly"  value="${item.itemPrice}"/></td>
							<td><input type="text" maxlength="50" style="width: 80%" readonly="readonly"  value="${item.remark}"/></td>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
				</table>
			</div>
		</c:if>
			
		<shiro:hasPermission name="psi:transport:costView">
		
			
		<blockquote  style="float:left;">
			<p style="font-size: 14px"><spring:message code="psi_transport_outlay"/></p>
		</blockquote>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" ><b>Local:</b></label>
					<div class="controls" style="margin-left:100px" >
						<input name="localAmount" type="text" maxlength="10" style="width:95%"  readonly="readonly" value="${psiTransportOrder.localAmount}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" ><b>Currency1:</b></label>
					<div class="controls" style="margin-left:100px" >
					<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.currency1}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px"><b>Vendor1:</b></label>
					<div class="controls" style="margin-left:100px">
						<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.vendor1.nikename}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:28%;height:25px"></div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" ><b><spring:message code="psi_transport_transportFee"/>:</b></label>
					<div class="controls" style="margin-left:100px" >
						<input name="tranAmount" type="text" maxlength="10" style="width:95%"  readonly="readonly" value="${psiTransportOrder.tranAmount}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px"><b>Currency2:</b></label>
					<div class="controls" style="margin-left:100px" >
					<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.currency2}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px"><b>Vendor2:</b></label>
					<div class="controls" style="margin-left:100px">
						<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.vendor2.nikename}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:28%;height:25px"></div>
		</div>
		<c:if test="${psiTransportOrder.model ne '2' }">
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px"><b><spring:message code="psi_transport_destFee"/>:</b></label>
						<div class="controls" style="margin-left:100px" >
							<input name="dapAmount" type="text"  maxlength="10" style="width:95%"  readonly="readonly" value="${psiTransportOrder.dapAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" ><b>Currency3:</b></label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.currency3}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px"><b>Vendor3:</b></label>
						<div class="controls" style="margin-left:100px">
							<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.vendor3.nikename}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:28%;height:25px"></div>
			</div>
		</c:if>
		
		
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px"><spring:message code="psi_transport_otherFee"/>:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="otherAmount" type="text"  maxlength="10" readonly="readonly" style="width:95%" class="price firstAmount" value="${psiTransportOrder.otherAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency4:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" maxlength="11" style="width: 80%"  readonly="readonly" value="${psiTransportOrder.currency4}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor4:</label>
						<div class="controls" style="margin-left:100px">
							<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.vendor4.nikename}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:28%;height:25px"></div>
			</div>
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px"><spring:message code="psi_transport_otherFee"/>1:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="otherAmount1" type="text"  maxlength="10" readonly="readonly" style="width:95%" class="price firstAmount" value="${psiTransportOrder.otherAmount1}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency7:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" maxlength="11" style="width: 80%"  readonly="readonly" value="${psiTransportOrder.currency7}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor7:</label>
						<div class="controls" style="margin-left:100px">
							<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.vendor7.nikename}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:28%;height:25px"></div>
			</div>
			
		<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px"><spring:message code="psi_transport_insuranceFee"/>:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="insuranceAmount" type="text" readonly="readonly" maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrder.insuranceAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency5:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.currency5}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor5:</label>
						<div class="controls" style="margin-left:100px">
							<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.vendor5.nikename}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:28%;height:25px" ></div>
			</div>
			
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:10%;height:25px">
				<label class="control-label" style="width:60px"><b><spring:message code="psi_transport_taxFee"/>:</b></label>
				<div class="controls" style="margin-left:70px" >
					<input name="dutyTaxes" type="text" maxlength="10" readonly="readonly" style="width:95%" class="required price" value="${psiTransportOrder.dutyTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:10%;height:25px">
				<label class="control-label" style="width:60px"><b><spring:message code="psi_transport_dutyFee"/>:</b></label>
				<div class="controls" style="margin-left:70px" >
					<input name="taxTaxes" type="text" maxlength="10" readonly="readonly" style="width:85%" class="required price" value="${psiTransportOrder.taxTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:10%;height:25px">
				<label class="control-label" style="width:60px"><b>otherTax:</b></label>
				<div class="controls" style="margin-left:70px" >
					<input name="otherTaxes" type="text" maxlength="10" readonly="readonly" style="width:85%" class="required price" value="${psiTransportOrder.otherTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" ><b>Currency6:</b></label>
					<div class="controls"  style="margin-left:100px">
						<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.currency6}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px"><b>Vendor6:</b></label>
					<div class="controls" style="margin-left:100px">
						<input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${psiTransportOrder.vendor6.nikename}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:28%;height:25px"></div>
		</div>
		
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px"><b>Total:</b></label>
				<div class="controls" style="margin-left:100px" >
					<input name="totalAmount" type="text" maxlength="10" readonly="readonly" style="width:85%" id="totalAmount" class="required price" value="${psiTransportOrder.totalAmount}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:83%;height:25px"></div>
		</div>
		
		<c:if test="${not empty psiTransportOrder.localPath || not empty psiTransportOrder.tranPath || not empty psiTransportOrder.dapPath || not empty psiTransportOrder.otherPath || not empty psiTransportOrder.insurancePath || not empty psiTransportOrder.taxPath || not empty psiTransportOrder.otherPath1}">
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px"><spring:message code="psi_transport_voucherInfo"/></p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		<div style="float:left;width:98%;height:50px;">
			<div class="control-group" style="float:left;width:98%;height:40px">
					<b><spring:message code="psi_transport_voucher"/></b>：
					<c:if test="${not empty psiTransportOrder.localPath}">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.localPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">local_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>  
					</c:if>
					<c:if test="${not empty psiTransportOrder.tranPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.tranPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>"><spring:message code="psi_transport_transportFee"/>_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.dapPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.dapPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>"><spring:message code="psi_transport_destFee"/>_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.otherPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.otherPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>"><spring:message code="psi_transport_otherFee"/>_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.otherPath1}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.otherPath1,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>"><spring:message code="psi_transport_otherFee"/>1_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.insurancePath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.insurancePath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>"><spring:message code="psi_transport_insuranceFee"/>_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrder.taxPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrder.taxPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">Tax_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>
					</c:if>
					 
			</div>
		</div>
		</c:if>
		</shiro:hasPermission>
		
		<shiro:hasPermission name="psi:transport:print">
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px"><spring:message code="custom_event_form13"/></p></div>
		</blockquote>
		
		<div style="float:left;width:98%;height:50px;">
			<div class="control-group" style="float:left;width:98%;height:40px">
			<c:forEach items="${fn:split(psiTransportOrder.suffixName,'-')}" var="attFile" varStatus="i">
					<c:choose>
						<c:when test="${fn:contains('.png,.pdf,.jpg,.JPG',attFile)}">
							<c:choose>    
								<c:when test="${i.index eq 0 && attFile ne 'PI' && attFile ne ''}"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_PI${attFile}'/>">PI Download</a>
									<shiro:hasPermission name="psi:transport:confirmPay">
										<c:choose>
											<c:when test="${psiTransportOrder.confirmPay eq '1' }">
												<span class="label">已支付运费</span>
											</c:when>
											<c:otherwise><span class="btn btn-warning confirmPay">支付运费</span></c:otherwise>
										</c:choose>
									</shiro:hasPermission>
								&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 1 && attFile ne 'PL' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_PL${attFile}'/>">PL Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 2 && attFile ne 'WB' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_WB${attFile}'/>">Bill of lading Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 3 && attFile ne 'TI' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_TI${attFile}'/>"><spring:message code="psi_transport_commercial"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 4 && attFile ne 'SO' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_SO${attFile}'/>">SO Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 5 && attFile ne 'WV' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_WV${attFile}'/>"><spring:message code="psi_transport_receipt"/>Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 6 && attFile ne 'IS' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_IS${attFile}'/>"><spring:message code="psi_transport_inspection"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 7 && attFile ne 'CD' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_CD${attFile}'/>"><spring:message code="psi_transport_declaration"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 8 && attFile ne 'CS' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_CS${attFile}'/>"><spring:message code="psi_transport_contract"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 9 && attFile ne 'SP' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_SP${attFile}'/>"><spring:message code="psi_transport_insurance"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 10 && attFile ne 'RS' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_RS${attFile}'/>"><spring:message code="psi_transport_Telex"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 11 && attFile ne 'AN' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_AN${attFile}'/>"><spring:message code="psi_transport_arriveInvoice"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 12 && attFile ne 'IB' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_IB${attFile}'/>"><spring:message code="psi_transport_import"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${i.index eq 0 && attFile ne 'PI' && attFile ne '' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_PI${attFile}">PI Download</a>
									<shiro:hasPermission name="psi:transport:confirmPay">
										<c:choose>
											<c:when test="${psiTransportOrder.confirmPay eq '1' }">
												<span class="label">已支付运费</span>
											</c:when>
											<c:otherwise><span class="btn btn-warning confirmPay">支付运费</span></c:otherwise>
										</c:choose>
									</shiro:hasPermission>
								&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 1 && attFile ne 'PL' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_PL${attFile}">PL Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 2 && attFile ne 'WB' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_WB${attFile}">Bill of lading Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 3 && attFile ne 'TI' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_TI${attFile}"><spring:message code="psi_transport_commercial"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 4 && attFile ne 'SO' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_SO${attFile}">SO Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 5 && attFile ne 'WV' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_WV${attFile}"><spring:message code="psi_transport_receipt"/>Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 6 && attFile ne 'IS' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_IS${attFile}"><spring:message code="psi_transport_inspection"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 7 && attFile ne 'CD' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_CD${attFile}"><spring:message code="psi_transport_declaration"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 8 && attFile ne 'CS' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_CS${attFile}"><spring:message code="psi_transport_contract"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 9 && attFile ne 'SP' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_SP${attFile}"><spring:message code="psi_transport_insurance"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 10 && attFile ne 'RS' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_RS${attFile}"><spring:message code="psi_transport_Telex"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 11 && attFile ne 'AN' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_AN${attFile}"><spring:message code="psi_transport_arriveInvoice"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 12 && attFile ne 'IB' }"><a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/${psiTransportOrder.transportNo}_IB${attFile}"><spring:message code="psi_transport_import"/> Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
							</c:choose>
						</c:otherwise>
				</c:choose>
			</c:forEach>   
			<c:if test="${not empty  psiTransportOrder.elsePath}">
				<c:forEach items="${fn:split(psiTransportOrder.elsePath,',')}" var="attFile" varStatus="i">
					<a href="${ctx}/psi/psiTransportOrder/download?fileName=/${psiTransportOrder.transportNo}/other/${attFile}">Other_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach>
			</c:if>
			</div>
		</div>
		</shiro:hasPermission>
		
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">Remark Infos</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:98%;">
				<label class="control-label" style="width:80px">Remark:</label>
				<div class="controls" style="margin-left:100px">
					<textarea name="remark"  style="width:100%;height:80px;" readonly="readonly">${psiTransportOrder.remark}</textarea>
				</div>
			</div>
		</div>
		
				
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">Product Infos</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 12%">Product Name</th>
				   <th style="width: 6%">Country</th>
				   <th style="width: 6%">Color</th>
				   <th style="width: 12%">Sku</th>
				   <th style="width: 4%">Offline</th>
				   <th style="width: 5%">Pack Nums</th>
				   <th style="width: 5%">Quantity</th>
				   <c:if test="${psiTransportOrder.transportType eq '0'}">
					   <th style="width: 5%">Delivery Quantity</th>
					   <th style="width: 5%">Received Quantity</th>
				   </c:if>
				   <th style="width: 5%">Box Nums</th>
				   <th style="width: 5%">Unit Price</th>
				   <th style="width: 5%">Weight</th>
				   <th style="width: 5%">Volume</th>
				   <th style="width: 5%"><spring:message code="psi_transport_charged"/></th>
				   <th style="width: 7%">Remark</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${psiTransportOrder.items}"  var="item">
			<tr>
				<td>
				<input type='text' style="width:90%" readonly="readonly" value="${item.productName}"/>
				</td>
				<td>
				<input type='text' style="width:90%" readonly="readonly" value="${fns:getDictLabel(item.countryCode,'platform','')}"/>
				</td>
				<td><input type='text' style="width:90%" readonly="readonly" value="${item.colorCode}"/></td>
				<td><input type='text' style="width:90%" readonly="readonly" value="${item.sku}"/></td>
				<td>${item.offlineSta eq '1'?'Yes':'No' }</td>
				<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.packQuantity}" /></td>
				<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.quantity}" /></td>
				<c:if test="${psiTransportOrder.transportType eq '0'}">
					<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.shippedQuantity}" /></td>
					<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.receiveQuantity}" /></td>
				</c:if>
				<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.quantity/item.packQuantity}" /></td>
				<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.productPrice}" /></td>
				<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.quantity*item.product.gw/item.packQuantity}" /></td>
				<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.quantity*item.product.boxVolume/item.packQuantity}" /></td>
				
				<td>
					<span class="hasElectric">
					<c:if test="${item.product.hasElectric eq '1' }"><font style="color:red"><spring:message code="sys_but_yes"/></font></c:if>
					<c:if test="${item.product.hasElectric ne '1' }"><spring:message code="sys_but_no"/></c:if>
					</span>
				</td>
				<td><a style="color: #08c;"  data-placement="left" data-html="true" rel="popover" data-content="${item.remark}">${fns:abbr(item.remark,13)}</a></td>
			</tr>
		</c:forEach>
		</tbody>
		</table>
			
			
			<div class="form-actions" style="float:left;width:98%">
				<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
			</div>
		</form:form>
	</body>
	</html>