<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品价格审批管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			if(!(top)){
				top = self; 
			}
			
			$("#inputForm").validate();
			
			//显示标准价格区间
			var rs = "";
			$.ajax({
     			   type: "POST",
     			   url: "${ctx}/amazoninfo/productPriceApproval/getPrice?country=${productPriceApproval.country}&sku=${productPriceApproval.sku}",
     			   async: false,
     			   success: function(msg){
     				   rs = msg;		
     			   }
         		});
			var tip = "";
			if(rs.warnPrice){
			 	tip += "<span style='color:green'>最低价:"+rs.warnPrice +"</span>&nbsp;&nbsp;" ;
				$("#minPrice").val(rs.warnPrice);
			}
			if(rs.highWarnPrice){
				 tip += "<span style='color:red'>最高价:"+rs.highWarnPrice +"</span>";
				 $("#maxPrice").val(rs.highWarnPrice);
			}
			/** 不显示价格信息
			if(tip){
				$(".tip").html("</br>标准价格区间为："+tip);
			}*/
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/productPriceApproval/">价格审批列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/productPriceApproval/form?id=${productPriceApproval.id}">修改</a></li>
	</ul>
	<div class="form-actions">
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
	<form:form id="inputForm" modelAttribute="productPriceApproval" action="${ctx}/amazoninfo/productPriceApproval/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" id="productName" name="productName" value="${productPriceApproval.productName}"/>
		<input type="hidden" id="sku" name="sku" value="${productPriceApproval.sku}"/>
		<input type="hidden" id="minPrice" name="minPrice"/>
		<input type="hidden" id="maxPrice" name="maxPrice"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label"><spring:message code='amazon_order_form4'/>:</label><!--平台-->
			<div class="controls">
				${fns:getDictLabel(productPriceApproval.country,'platform','other')} ${productPriceApproval.accountName }
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form5'/></label>
			<div class="controls">
				${productPriceApproval.productName }[${productPriceApproval.sku }]
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">定价:</label>
			<div class="controls">
				<form:input path="price" htmlEscape="false" maxlength="10" min="" max="" class="required price"/>
				<span class="tip"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">起售时间:</label>
			<div class="controls">
				<input style="width: 150px" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'saleEndDate\')}',dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate required" type="text" name="saleStartDate" value="<fmt:formatDate value="${productPriceApproval.saleStartDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="saleStartDate"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">截止时间:</label>
			<div class="controls">
				<input style="width: 150px" onclick="WdatePicker({minDate:'#F{$dp.$D(\'saleStartDate\')}',dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate required" type="text" name="saleEndDate" value="<fmt:formatDate value="${productPriceApproval.saleEndDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="saleEndDate"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型:</label>
			<div class="controls">
				<form:select path="type" style="width: 220px" class="required">
					<form:option value="" label=""/>
					<form:option value="1" label="降价" />
					<form:option value="2" label="涨价" />
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">定价原因:</label>
			<div class="controls">
				<form:select path="reason" style="width: 220px" class="required">
					<form:option value="" label=""/>
					<form:option value="防御性降价" label="防御性降价" />
					<form:option value="积压降价" label="积压降价" />
					<form:option value="断货升价" label="断货升价" />
					<form:option value="促销调价" label="促销调价" />
					<form:option value="计划调价" label="计划调价" />
					<form:option value="汇率改价" label="汇率改价" />
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">预警销量:</label>
			<div class="controls">
				<form:input path="warnQty" htmlEscape="false" maxlength="10" min="" max="" class="number"/>
				<span class="tip"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">自动改价销量:</label>
			<div class="controls">
				<form:input path="changeQty" htmlEscape="false" maxlength="10" min="" max="" class="number"/>
				<span class="tip"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">自动改价价格:</label>
			<div class="controls">
				<form:input path="changePrice" htmlEscape="false" maxlength="10" min="" max="" class="price"/>
				<span class="tip"></span>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>