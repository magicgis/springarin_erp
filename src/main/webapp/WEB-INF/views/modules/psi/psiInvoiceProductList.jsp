<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>invoice</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
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
			
				$(".edit").editable({
					mode:'inline',
					showbuttons:'bottom',
					validate:function(data){
						
					},
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().find(".attrId").val();
						param.rate = newValue;
						$.get("${ctx}/psi/psiInvoice/uploadRate?"+$.param(param),function(data){
							if(!data){
								$this.text(oldVal);
							}else{
								$.jBox.tip("修改成功！", 'info',{timeout:2000});
							}
						});
						return true;
			 }});

				
				$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType": "bootstrap",
					"iDisplayLength": 15,
					"aLengthMenu":[[15, 30, 60,100,-1], [15, 30, 60, 100, "All"]],
				 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},
				 	"ordering":true,
				     "aaSorting": [[0, "asc" ]]
				});
				
				$(".row:first").append("&nbsp;&nbsp;<a href='#invoiceExcel' role='button' class='btn  btn-primary' data-toggle='modal' id='uploadinvoiceFile'>上传税率文件</a> ");
				
		});
	
		
	</script> 
</head>
<body>
	 <ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiInvoice">发票列表</a></li>
		<li><a href="${ctx}/psi/psiInvoice/declareList">报关列表</a></li>
		<li  class="active"><a href="${ctx}/psi/psiInvoice/productList">商品税率列表</a></li>
	</ul>
	
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
		   <th>序号</th>
		   <th>商品代码</th>
		   <th>商品名称</th>
		   <th>税率</th>
		   <th>操作</th>
		 </tr>
		 </thead>   
		<tbody>
		<c:forEach items="${productList}" var="product">
			<tr>
			   <td>${product.id }</td>
			   <td>${product.productCode }</td>
			   <td>${product.name }</td>
			   <td>
					    <input type="hidden" class="attrId" value="${product.id}" />
				        <a href="#" class="edit"  data-type="text" data-pk="1" data-title="Enter" >
					     	${product.taxRate}
						</a>
			   </td>
			  <%--  <td><fmt:formatNumber value="${product.taxRate}" maxFractionDigits="2" pattern="#0.00"/></td> --%>
			   <td><a class="btn btn-warning" href="${ctx}/psi/psiInvoice/deleteProduct?id=${product.id}" onclick="return confirm('确认删除吗？', this.href)">删除</a> </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<div id="invoiceExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm" action="${ctx}/psi/psiInvoice/uploadProductFile" method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">税率文件上传</h3>
						  </div>
						  <div class="modal-body">
							<input type="file" name="excel"  id="excel" class="required"/> 
						  </div>
						   <div class="modal-footer">
						    <button class="btn btn-primary"  type="submit" id="uploadTypeFile"><spring:message code="sys_but_upload"/></button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
	</div>
</body>
</html>
