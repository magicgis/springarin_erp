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
				$("#btnSubmit").on("click",function(){
					top.$.jBox.confirm("您确定运单是已经付款完成了吗?,如果确定该运单将无法再进行付款","系统提示",function(v,h,f){
						if(v=="ok"){
							var params = {};
							params.id = $("#id").val();
							window.location.href = "${ctx}/psi/lcPsiTransportOrder/payDoneSave?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
			});
		</script>
	</head>
	<body>
		<ul class="nav nav-tabs">
			<li><a href="${ctx}/psi/lcPsiTransportOrder/">(理诚)运单列表</a></li>
			<li class="active"><a href="#">(理诚)	<c:if test="${psiTransportOrder.model eq '0'}">航空</c:if><c:if test="${psiTransportOrder.model eq '1'}">海运</c:if><c:if test="${psiTransportOrder.model eq '2'}">快递</c:if>运单付款完成确认</a></li>
		</ul><br/>
		<form:form id="inputForm" modelAttribute="psiTransportOrder" action="${ctx}/psi/lcPsiTransportOrder/payDoneSave" method="post" class="form-horizontal">
		<input name="id" type="hidden" id="id" value="${psiTransportOrder.id }"/>
		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
			<c:if test="${psiTransportOrder.model eq '1'}">
				<div style="float:left;width:98%">
					<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:100px"><b>海运模式:</b></label>
						<div class="controls" style="margin-left:120px" >
							<input type="text"  style="width:95%" readonly="readonly" value="${psiTransportOrder.oceanModel}"/>
						</div>
					</div>
					<div class="control-group" style="float:left;width:73%;height:25px">
						<div class="controls"></div>
					</div>
				</div>
			</c:if>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>发货仓库:</b></label>
				<div class="controls" style="margin-left:120px" >
					<input type="text"  style="width:95%" readonly="readonly" value="${psiTransportOrder.fromStore.stockSign}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>目的仓库:</b></label>
				<div class="controls"  style="margin-left:120px" >
					<input type="text"  style="width:95%" readonly="readonly" value="${psiTransportOrder.toStore.stockSign}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>起运机场:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="orgin" type="text" maxlength="10" style="width:95%" readonly="readonly" value="${psiTransportOrder.orgin}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>目的机场:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="destination" type="text" maxlength="10" style="width:90%"  readonly="readonly" class="required" value="${psiTransportOrder.destination}"/>
					</div>
			</div>
		</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>航空公司:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="carrier" type="text"  maxlength="10" style="width:95%"  readonly="readonly" class="required" value="${psiTransportOrder.carrier}"/>
					</div>
			</div>
			
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>离港日期:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="etdDate"  type="text" readonly="readonly" style="width:95%" value="<fmt:formatDate value="${psiTransportOrder.etdDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>预计到港日期:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="preEtaDate" type="text" readonly="readonly" style="width:95%" value="<fmt:formatDate value="${psiTransportOrder.etaDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>实际到港日期:</b></label>
					<div class="controls" style="margin-left:120px" >
						<input name="etaDate" type="text" readonly="readonly" style="width:95%" value="<fmt:formatDate value="${psiTransportOrder.etaDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
		
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>重量:</b></label>
					<div class="controls" style="margin-left:120px" >
						<div class="input-prepend input-append">
						<input name="weight" type="text" style="width:80%"  readonly="readonly" class="required" value="${psiTransportOrder.weight}"/> <span class="add-on">kg</span>
						</div>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>体积:</b></label>
					<div class="controls" style="margin-left:120px" >
						<div class="input-prepend input-append">
						<input type="text" name="volume"  style="width:80%"  readonly="readonly" class="required" value="${psiTransportOrder.volume}"/> <span class="add-on">m³</span>
						</div>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">箱数:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="boxNumber" type="text" readonly style="width:95%" value="${psiTransportOrder.boxNumber}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px"></div>
		
		</div>
		
		<div style="float:left;width:98%">
		<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>运单号:</b></label>
					<div class="controls" style="margin-left:120px" >
						<div class="input-prepend input-append">
						<input  type="text" readonly style="width:100%" value="${psiTransportOrder.transportNo}"/>
						</div>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>运单状态:</b></label>
					<div class="controls" style="margin-left:120px" >
						<div class="input-prepend input-append">
						<c:if test="${psiTransportOrder.transportSta eq '0' }"><span class="label  label-important">草稿</span></c:if>
						<c:if test="${psiTransportOrder.transportSta eq '1' }"><span class="label  label-warning">在途</span></c:if>
						<c:if test="${psiTransportOrder.transportSta eq '2' }"><span class="label  label-success">到达</span></c:if>
						<c:if test="${psiTransportOrder.transportSta eq '8' }"><span class="label  label-inverse">取消</span></c:if>
						</div>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px"><b>付款状态:</b></label>
					<div class="controls" style="margin-left:120px" >
						<div class="input-prepend input-append">
						<c:if test="${psiTransportOrder.paymentSta eq '0' }"><span class="label  label-important">未支付</span></c:if>
						<c:if test="${psiTransportOrder.paymentSta eq '1' }"><span class="label  label-warning">已部分支付</span></c:if>
						<c:if test="${psiTransportOrder.paymentSta eq '2' }"><span class="label  label-success">已完成支付</span></c:if>
						</div>
					</div>
			</div>
		</div>
		
		<c:if test="${psiTransportOrder.model eq '1' && psiTransportOrder.oceanModel eq 'FCL'}">
			<div style="float:left; width:98%;" id="showContainer">
				<blockquote  style="float:left;">
					<div style="float: left"><p style="font-size: 14px">集装箱信息</p></div>
				</blockquote>
				
				
				<table id="containerTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						   <th style="width: 20%">集装箱类型</th>
						   <th style="width: 10%">数量</th>
						   <th style="width: 10%">单价</th>
						   <th style="width: 20%">备注</th>
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
			
		<blockquote  style="float:left;">
			<p style="font-size: 14px">费用信息</p>
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
					<label class="control-label" style="width:80px" ><b>运输费用:</b></label>
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
						<label class="control-label" style="width:80px"><b>目的港费用:</b></label>
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
						<label class="control-label" style="width:80px">其他费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="otherAmount" type="text"  maxlength="10" readonly="readonly" style="width:20%" class="price firstAmount" value="${psiTransportOrder.otherAmount}"/>
							${psiTransportOrder.otherRemark}
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
						<label class="control-label" style="width:80px">其他费用1:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="otherAmount1" type="text"  maxlength="10" readonly="readonly" style="width:20%" class="price firstAmount" value="${psiTransportOrder.otherAmount1}"/>
							${psiTransportOrder.otherRemark1}
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
						<label class="control-label" style="width:80px">保险费用:</label>
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
				<label class="control-label" style="width:60px"><b>进口税:</b></label>
				<div class="controls" style="margin-left:70px" >
					<input name="dutyTaxes" type="text" maxlength="10" readonly="readonly" style="width:95%" class="required price" value="${psiTransportOrder.dutyTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:10%;height:25px">
				<label class="control-label" style="width:60px"><b>关税:</b></label>
				<div class="controls" style="margin-left:70px" >
					<input name="taxTaxes" type="text" maxlength="10" readonly="readonly" style="width:85%" class="required price" value="${psiTransportOrder.taxTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:10%;height:25px">
				<label class="control-label" style="width:60px"><b>其他税:</b></label>
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
				<label class="control-label" style="width:80px"><b>单价:</b></label>
				<div class="controls" style="margin-left:100px" >
					<input name="unitPrice" type="text" maxlength="10" readonly="readonly" style="width:85%" id="unitPrice" class="required price" value="${psiTransportOrder.unitPrice}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px"><b>总额:</b></label>
				<div class="controls" style="margin-left:100px" >
					<input name="totalAmount" type="text" maxlength="10" readonly="readonly" style="width:85%" id="totalAmount" class="required price" value="${psiTransportOrder.totalAmount}"/>
				</div>
			</div>
		</div>
		
		
				
			<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">产品信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">产品名</th>
				   <th style="width: 10%">国家</th>
				   <th style="width: 10%">颜色</th>
				   <th style="width: 10%">单价(*110%)</th>
				   <th style="width: 10%">数量</th>
				   <th style="width: 10%">发货数量</th>
				   <th style="width: 10%">接收数量</th>
				   <th style="width: 20%">备注</th>
			</tr>
		</thead>
		<tbody>
		<c:if test="${ not empty psiTransportOrder.id }">
		<c:forEach items="${psiTransportOrder.items}"  var="item">
			<tr>
				<td>
				<input type='text' style="width:90%" readonly="readonly" value="${item.productName}"/>
				</td>
				<td>
				<input type='text' style="width:90%" readonly="readonly" value="${item.countryCode}"/>
				</td>
				<td>
					<input type='text' style="width:90%" readonly="readonly" value="${item.colorCode}"/>
				</td>
				<td><input type="text" maxlength="100" style="width: 80%" readonly="readonly"  value="${item.itemPrice}"/></td>
				<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.quantity}" /></td>
				<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.shippedQuantity}" /></td>
				<td><input type="text" maxlength="11" style="width: 80%" readonly="readonly" value="${item.receiveQuantity}" /></td>
				<td><input type="text" maxlength="50" style="width: 80%" readonly="readonly" value="${item.remark}" /></td>
			</tr>
		</c:forEach>
		</c:if>
		</tbody>
		</table>
			
			
			<div class="form-actions" style="float:left;width:98%">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="完成付款"/>&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</form:form>
	</body>
	</html>