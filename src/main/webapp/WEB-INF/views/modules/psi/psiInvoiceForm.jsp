<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>invoice</title>
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
			
			$("#inputForm").validate({
				
				submitHandler: function(form){
					var flag = true;
					var numberflag = true;
					$(".price").each(function(){
						if($(this).val()!=''){
							if(!$.isNumeric($(this).val())){
								flag = false;
							}
							if($(this).val()<0){
								flag = false;
							}
						} 
					});
					
					$(".digits").each(function(){
						if($(this).val()!=''){
							if(!$.isNumeric($(this).val())){
								flag = false;
							}
							if($(this).val()<=0){
								flag = false;
							}
						} 
					});
					
					if(!numberflag){
						top.$.jBox.error("<spring:message code='amazon_order_tips24'/>！","<spring:message code="sys_label_tips_error"/>");
					}else{
					
						if(flag){
							top.$.jBox.confirm('Are you sure save?','<spring:message code="sys_label_tips_msg"/>',function(v,h,f){
							
							if(v=='ok'){
									loading('<spring:message code="sys_label_tips_submit"/>');
									$("#contentTable tbody tr").each(function(i,j){
										$(j).find("select").attr("name","items"+"["+i+"]."+$(j).find("select").attr("name"));
										$(j).find("input[type!='']").each(function(){
											if($(this).attr("name")){
												$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
											}
										});
									});
									form.submit();
									$("#btnSubmit").attr("disabled","disabled");
								}
							},{buttonsFocus:1,persistent: true});
							top.$('.jbox-body .jbox-icon').css('top','55px');
						}else{
							top.$.jBox.error("<spring:message code="amazon_order_tips5"/>","<spring:message code="sys_label_tips_error"/>");
						}
					}
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("<spring:message code="sys_label_tips_input_error"/>");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		

		
	</script>
</head>
<body>

<ul class="nav nav-tabs">
    <li><a href="${ctx}/psi/psiInvoice">发票列表</a></li>
    <li class="active"><a href="#">发票编辑</a></li>	
</ul>
<br/>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="psiSupplierInvoice"  action="${ctx}/psi/psiInvoice/saveInvoice" method="post" class="form-horizontal">
	    <form:hidden path="id" id="id"/>
		<blockquote>
		  <p style="font-size: 14px">发票信息</p>
		</blockquote>
		<table>
		 <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>发票代码</b>:</label>
					<div class="controls">
						<form:input maxlength="100" class="required" path="invoiceCode" id="invoiceCode"  />
					</div>
				</div>	
		    </td>
		  </tr>
		   <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>发票号码</b>:</label>
					<div class="controls">
						<form:input maxlength="100" class="required" path="invoiceNo" id="invoiceNo"  />
					</div>
				</div>	
		    </td>
		  </tr>
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>公司名称</b>:</label>
					<div class="controls">
						<form:input maxlength="200" class="required" path="companyName" id="companyName"  />
					</div>
				</div>	
		    </td>
		  </tr>
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>纳税识别号</b>:</label>
					<div class="controls">
						<form:input maxlength="200" class="required" path="taxpayerNo" id="taxpayerNo"  />
					</div>
				</div>	
		    </td>
		  </tr>
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>货物名称</b>:</label>
					<div class="controls">
						<form:input maxlength="200" class="required" path="productName" id="productName"  />
					</div>
				</div>	
		    </td>
		  </tr>
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>规格型号</b>:</label>
					<div class="controls">
						<form:input maxlength="200"  path="model" id="model"  />
					</div>
				</div>	
		    </td>
		  </tr>
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>单位</b>:</label>
					<div class="controls">
						<form:input maxlength="200" class="required" path="unit" id="unit"  />
					</div>
				</div>	
		    </td>
		  </tr>
		
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>数量</b>:</label>
					<div class="controls">
						<form:input maxlength="200" class="required" path="quantity" id="quantity"  />
					</div>
				</div>	
		    </td>
		  </tr>
		  
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>剩余数量</b>:</label>
					<div class="controls">
						<form:input maxlength="200" class="required" path="remainingQuantity" id="remainingQuantity"  />
					</div>
				</div>	
		    </td>
		  </tr>
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>总金额</b>:</label>
					<div class="controls">
						<form:input maxlength="200" class="required" path="totalPrice" id="totalPrice"  />
					</div>
				</div>	
		    </td>
		  </tr>
		  
		   <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>税率</b>:</label>
					<div class="controls">
						<form:input maxlength="200" class="required" path="rate" id="rate"  />
					</div>
				</div>	
		    </td>
		  </tr>
		  
		   <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>认证状态</b>:</label>
					<div class="controls">
						<form:select path="state" style="width: 220px" class="required" id="state">
								<option value="0" ${psiSupplierInvoice.state eq '0'?'selected':''}>未认证</option>
								<option value="1" ${psiSupplierInvoice.state eq '1'?'selected':''}>已认证</option>
						</form:select>
					</div>
				</div>	
		    </td>
		  </tr>
		  
		    
		</table>
		 
		 <div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_save"/>"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
