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
    <li><a href="${ctx}/psi/psiInvoice/declareList">报关列表</a></li>
    <li class="active"><a href="#">报关单编辑</a></li>	
</ul>
<br/>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="declare"  action="${ctx}/psi/psiInvoice/saveDeclare" method="post" class="form-horizontal">
	    <form:hidden path="id" id="id"/>
		<blockquote>
		  <p style="font-size: 14px">报关单信息信息</p>
		</blockquote>
		<table>
		 <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>出口日期</b>:</label>
					<div class="controls">
						<input name="declareDate" type="text"  style="width:95%" required="required" class="Wdate" value="<fmt:formatDate value="${declare.declareDate}" pattern="yyyy-MM-dd" />" />
					</div>
				</div>	
		    </td>
		  </tr>
		   <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>报关单号</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="declareNo" id="declareNo"  value='${declare.declareNo }'/>
					</div>
				</div>	
		    </td>
		  </tr> 
		    <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>商品序号</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="declareNum" id="declareNum"  value='${declare.declareNum }'/>
					</div>
				</div>	
		    </td>
		  </tr> 
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>报关单号21位</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="declareCode" id="declareCode"  value='${declare.declareCode }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		  
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>运单号</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="transportNo" id="transportNo"  value='${declare.transportNo }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		  
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>商品编号</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="productNo" id="productNo"  value='${declare.productNo }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		  
		  
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>产品名称</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="productName" id="productName"  value='${declare.productName }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		   
		
		   <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>数量</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="quantity" id="quantity"  value='${declare.quantity }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		   
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>单位</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="unit" id="unit"  value='${declare.unit }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		  
		     <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>法定数量</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="legalQuantity" id="legalQuantity"  value='${declare.legalQuantity }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		   
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>法定单位</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="legalUnit" id="legalUnit"  value='${declare.legalUnit }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		  
		  
		    <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>单价</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="price" id="price"  value='${declare.price }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		  
		  
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>成交总价</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="totalPrice" id="totalPrice"  value='${declare.totalPrice }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		  
		  
		
		    <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>统计美元价</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="usdPrice" id="usdPrice"  value='${declare.usdPrice }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		  
		    <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>美元汇率</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="usdRate" id="usdRate"  value='${declare.usdRate }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		  
		    <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>人民币离岸价</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="cnyPrice" id="cnyPrice"  value='${declare.cnyPrice }'/>
					</div>
				</div>	
		    </td>
		  </tr>  
		  
		  <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>退税率</b>:</label>
					<div class="controls">
						<input maxlength="100" class="required" name="taxRate" id="taxRate"  value='${declare.taxRate }'/>
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
