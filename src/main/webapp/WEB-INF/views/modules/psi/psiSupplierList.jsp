<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>SupplierManagement</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style>
	table {table-layout:fixed}
	td {word-break:break-all;}
	</style>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
			$("#supplierType,#currencyType,#eliminate").change(function(){
				$("#searchForm").submit();
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.name=$("input[name='name']").val();
				params.type=$("input[name='type']").val();
				params.currencyType=$("input[name='currencyType']").val();
				window.location.href = "${ctx}/psi/supplier/exp?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$("#uploadTypeFile").click(function(e){
				   if($("#uploadFileName").val()==""){
						$.jBox.tip('上传文件名为空'); 
						return;
				   }
				   $("#uploadTypeFile").attr("disabled",true);
				   var formdata = new FormData($("#uploadForm")[0]);              
				   $.ajax({  
		                url :$("#uploadForm").attr("action"),  
		                type : 'POST',  
		                data : formdata,  
		                processData : false,  
		                contentType : false,  
		                success : function(responseStr) { 
		                	$.jBox.tip('文件上传成功'); 
		                	$("#uploadFileName").val("");
		                	$("#uploadTypeFile").attr("disabled",false);
		                	$("#buttonClose").click();
		                },  
		                error : function(responseStr) {  
		                	$.jBox.tip('文件上传失败'); 
		                	$("#uploadTypeFile").attr("disabled",false);
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
		 function uploadFile(id){
		     $("#uploadForm").attr("action","${ctx}/psi/supplier/upload?id="+id);
		 }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/psi/supplier/list">供应商列表</a></li>
		<shiro:hasPermission name="psi:supplier:edit"><li><a href="${ctx}/psi/supplier/add">新增供应商</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="psiSupplier" action="${ctx}/psi/supplier" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>供应商名称/简称：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
		<label class="control-label">供应商类型</label>
		<select name="type" id="supplierType" style="width:120px">
			<option value="" >全部</option>
			<option value="0" ${psiSupplier.type eq '0'?'selected':''}>产品供应商</option>
			<option value="1" ${psiSupplier.type eq '1'?'selected':''}>物流服务商</option>
			<option value="2" ${psiSupplier.type eq '2'?'selected':''}>包材供应商</option>
		</select>
		
		<label class="control-label">货币类型</label>
		<select name="currencyType" id="currencyType" style="width:120px">
			<option value="" >全部</option>
			<option value="CNY" ${psiSupplier.currencyType eq 'CNY'?'selected':''}>CNY</option>
			<option value="USD" ${psiSupplier.currencyType eq 'USD'?'selected':''}>USD</option>
		</select>
		&nbsp;
		
		<label class="control-label">淘汰</label>
		<select name="eliminate" id="eliminate" style="width:120px">
			<option value="" >全部</option>
			<option value="0" ${psiSupplier.eliminate eq '0'?'selected':''}>是</option>
			<option value="1" ${psiSupplier.eliminate eq '1'?'selected':''}>否</option>
		</select>
		&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<shiro:hasRole name="总经理"><input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/></shiro:hasRole>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
				   <th style="width:3%;">序号</th>
				   <th style="width:15%">供应商名称</th>
				   <th style="width:7%">供应商类型</th>
				   <th style="width:4%">简称</th>
				   <th style="width:3%">淘汰</th>
				   <th style="width:6%">增值税率%</th>
				   <th style="width:4%">定金%</th>
				   <th style="width:5%">货币类型</th>
				   <th style="width:5%;">联系人</th>
				   <th style="width:8%;">电话</th>
				   <th style="width:15%;">邮箱</th>
				   <th style="width:10%;">QQ</th>
				   <th style="width:15%;">付款方式</th>
				   <th style="width:9%;">操作</th>
				   </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="supplier">
			<tr>
				<td>${supplier.id}</td>
				<td style="word-break:break-all;"><shiro:hasPermission name="psi:supplier:view"><a href="${ctx}/psi/supplier/view?id=${supplier.id}">${supplier.name}</a></shiro:hasPermission></td>
				<td>${supplier.typeName}</td>
				<td>${supplier.nikename}</td>
				<td>${'0' eq supplier.eliminate ?'是':'否'}</td>
				<td>${supplier.taxRate}%</td>
				<td>${supplier.deposit}</td>
				<td>${supplier.currencyType}</td>
				<td >${supplier.contact}</td>
				<td>${supplier.phone}</td>
				<td style="word-break:break-all;">${supplier.mail}</td>
				<td>${supplier.qq}</td>
				<td>${supplier.payMark}</td>
				<td>
				<shiro:hasPermission name="psi:supplier:edit">
				     <div class="btn-group">
						   <button type="button" class="btn btn-small">编辑</button>
						   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" >
						      <li><a href="${ctx}/psi/supplier/update?id=${supplier.id}">编辑</a></li>
						      <li><a href="${ctx}/psi/supplier/delete?id=${supplier.id}" onclick="return confirmx('确认要删除该供应商吗？', this.href)">删除</a></li>
						      <c:if test="${'0' eq supplier.eliminate }">
						         <li><a href="${ctx}/psi/supplier/eliminate?id=${supplier.id}&eliminate=1" onclick="return confirmx('确认要恢复该供应商吗？', this.href)">非淘汰</a></li>
						      </c:if>
						      <c:if test="${'1' eq supplier.eliminate }">
						         <li><a href="${ctx}/psi/supplier/eliminate?id=${supplier.id}&eliminate=0" onclick="return confirmx('确认要淘汰该供应商吗？', this.href)">淘汰</a></li>
						      </c:if>
						      <li><a href="#updateExcel" data-toggle="modal" id="uploadFile" onclick="uploadFile(${supplier.id})">上传</a> </li>
						   </ul>
					</div>
				<%-- 	<a href="${ctx}/psi/supplier/update?id=${supplier.id}">编辑</a>&nbsp;&nbsp;
					<a href="${ctx}/psi/supplier/delete?id=${supplier.id}" onclick="return confirmx('确认要删除该供应商吗？', this.href)">删除</a> --%>
					 
				</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div id="updateExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm"  method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">文件上传</h3>
						  </div>
						  <div class="modal-body">
							<label >文件类型：</label>
							<select  id="uploadType" name="uploadType">
								<option value="0">营业执照复印件</option>
								<option value="1">税务登记复印件</option>
								<option value="2">ISO认证及其他认证复印件</option>
								<option value="3">银行资料</option>
								<option value="4">公司介绍PPT</option>
								<option value="5">基本资料统计</option>
							</select><br/><br/>
							<input id="uploadFileName" name="uploadFile" type="file" />
							 
						  </div>
						   <div class="modal-footer">
						   <button class="btn btn-primary"  type="button" id="uploadTypeFile">上传</button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true">关闭</button>
						  </div> 
					</form>
			 </div>
	<div class="pagination">${page}</div>
</body>
</html>
