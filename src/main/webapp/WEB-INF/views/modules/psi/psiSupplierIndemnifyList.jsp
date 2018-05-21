<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商退款处理记录</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
	
		$(document).ready(function() {
			$("#supplier").on("click",function(){
				$("#searchForm").submit();
			});
			
		
			
			$(".icon-remove").on("click",function(){
		        var id = $(this).attr("aria-controls");
		        var nameVal = $(this).attr("nameVal");
		        console.log(id);
		        $(this).parent().css("display","none");
		        $.ajax({
	      			   type: "POST",
	      			   url: "${ctx}/psi/purchaseOrder/delFile?id="+id+"&fileName="+encodeURI(nameVal),
	      			   async: false,
	      			   success: function(msg){
	      				   if(msg=="0"){
	      					 $.jBox.tip('删除成功'); 
	      				   }
	      			   }
	          		});
		    });
		    
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/purchaseOrder/deliveryRate">交货延期率</a></li>
    	<li><a href="${ctx}/psi/lcPsiLadingBill/testCount">产品合格率</a></li>
    	<li><a href="${ctx}/psi/lcPsiLadingBill/testCountSupplier">供应商合格率</a></li>
    	<li class="active"><a href="${ctx}/psi/purchaseOrder/supplierIndemnifyList">供应商赔偿记录</a></li>
    	<li><a href="${ctx}/psi/purchaseOrder/supplierIndemnifyForm">新增供应商赔偿记录</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiSupplierIndemnify" action="${ctx}/psi/purchaseOrder/supplierIndemnifyList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>供应商：</label>
		<select style="width:150px;" id="supplier" name="supplier.id">
			<option value="" ${psiSupplierIndemnify.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
			<c:forEach items="${suppliers}" var="supplier" varStatus="i">
				 <option value='${supplier.id}' ${supplier.id eq  psiSupplierIndemnify.supplier.id?'selected':''}>${supplier.nikename}</option>;
			</c:forEach>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>主题：</label>	<input name="subject" type="text" maxlength="50" class="input-small" value="${psiSupplierIndemnify.subject}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiSupplierIndemnify.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${psiSupplierIndemnify.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:2%">No</th><th style="width:5%">供应商</th><th style="width:20%">主题</th><th style="width:5%">金额</th><th style="width:5%">CreateUser</th><th style="width:7%">CreateDate</th><th style="width:6%">State</th><th style="width:20%">remark</th><th>附件</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="indemnify">
			<tr>
				<td>${indemnify.id}</td>
				<td>${indemnify.supplier.nikename}</td>
				<td>${indemnify.subject}</td>
				<td>${indemnify.money}</td>
				<td>${indemnify.createUser.name}</td>
				<td><fmt:formatDate value="${indemnify.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${indemnify.state eq '0'}"><span class="label label-important">未支付</span></c:if>
					<c:if test="${indemnify.state eq '1'}"><span class="label label-success">已支付</span></c:if>
					<c:if test="${indemnify.state eq '8'}"><span class="label label-inverse">已取消</span></c:if>
				</td>
				<td>${indemnify.remark}</td>
				<td>
				   <c:if test="${not empty indemnify.attchmentPath }">
						<c:forEach items="${fn:split(indemnify.attchmentPath,',')}" var="attFile" varStatus="i">
							<span><a title='${attFile}' href="${ctx}/psi/purchaseOrder/indemnifyDownload?fileName=${attFile}" >Download${i.index }</a><c:if test="${indemnify.createUser.id eq fns:getUser().id}"><i aria-controls='${indemnify.id}' nameVal='${attFile}' class="close-tab icon-remove"></i></c:if></span>
						</c:forEach> 
					</c:if>
				</td>
				<td>
					<c:if test="${indemnify.createUser.id eq fns:getUser().id && indemnify.state eq '0'}">
						<a class="btn btn-small" href="${ctx}/psi/purchaseOrder/supplierIndemnifyForm?id=${indemnify.id}">编辑</a>&nbsp;&nbsp;
						<a class="btn btn-small" href="${ctx}/psi/purchaseOrder/deleteIndemnify?id=${indemnify.id}">删除</a>&nbsp;&nbsp;
					</c:if>
					
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
