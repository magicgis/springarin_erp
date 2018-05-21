<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>增值税发票管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
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
			
			$("#supplier").change(function(){
				$("#searchForm").submit();
			});
			
			$(".inRemark").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.id = $(this).parent().parent().find(".inId").val();
					param.remark = encodeURI(newValue);
					$.get("${ctx}/psi/psiVatInvoiceInfo/updateRemark?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("success！", 'info',{timeout:2000});
						}
					});
					return true;
				}
			});
			
			$("#btnExport").click(function(){
				$("#searchForm").attr("action","${ctx}/psi/psiVatInvoiceInfo/expVatInfo");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/psi/psiVatInvoiceInfo/");
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
		<li class="active"><a href="">增值税发票列表</a></li>
		<shiro:hasPermission name="psi:psiVatInvoiceInfo:edit">
			<li ><a href="${ctx}/psi/psiVatInvoiceInfo/form">新增增值税发票</a></li>
		</shiro:hasPermission>
		<li ><a href="${ctx}/psi/psiVatInvoiceInfo/received">增值税开票详情</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiVatInvoiceInfo" action="${ctx}/psi/psiVatInvoiceInfo/" method="post" class="breadcrumb form-search" >
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>供应商/发票号/产品名：</label><input type="text" name="productName" value="${psiVatInvoiceInfo.productName}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>备注：</label><input type="text" name="remark" value="${psiVatInvoiceInfo.remark}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<br/><br/><label>开票时间：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiVatInvoiceInfo.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="invoiceDate" value="<fmt:formatDate value="${psiVatInvoiceInfo.invoiceDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;
		<input id="btnExport" class="btn btn-info" type="button" value="导出"/>&nbsp;&nbsp;
		<a href="${ctx}/psi/psiVatInvoiceInfo/readExcel"><input  class="btn btn-warning" type="button" value="上传excel"/></a>
		&nbsp;&nbsp;
		<a href="#updateExcel" role="button" class="btn  btn-primary" data-toggle="modal" id="uploadFile">更新数量</a> 
	</form:form>
	<tags:message content="${message}"/>
	<!-- 未开票的数量（含在产），未开票的数量（不含在产） -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:3%">序号</th><th style="width:5%">开票时间</th><th style="width:5%">发票号</th><th style="width:10%">供应商</th><th style="width:10%">产品</th><th style="width:5%">数量</th><th style="width:5%">已用数量</th><th style="width:5%">金额(含税)</th>
		<th style="width:5%">金额(不含税)</th><th style="width:5%">税额</th><th style="width:5%">备注</th><th style="width:10%">操作</th></tr></thead>   
		<tbody>
		<c:forEach items="${page.list}" var="psiVatInvoiceInfo">
			<tr>
				<td>${psiVatInvoiceInfo.id}</td>
				<td><fmt:formatDate value="${psiVatInvoiceInfo.invoiceDate}" pattern="yyyy-MM-dd"/> </td>
				<td>${psiVatInvoiceInfo.invoiceNo}</td>
				<td>${psiVatInvoiceInfo.supplierName}</td>
				<td>${psiVatInvoiceInfo.productName}</td>
				<td>${psiVatInvoiceInfo.quantity}</td>	
				<td> 
				  <c:if test="${psiVatInvoiceInfo.remainingQuantity!=psiVatInvoiceInfo.quantity}">
				    <a href="${ctx}/psi/psiVatInvoiceInfo/detailList?id=${psiVatInvoiceInfo.id}">${psiVatInvoiceInfo.quantity-psiVatInvoiceInfo.remainingQuantity}</a>
				  </c:if>
				  <c:if test="${psiVatInvoiceInfo.remainingQuantity==psiVatInvoiceInfo.quantity}">
				   0
				  </c:if>
				</a>
				</td>	
				
				<td><fmt:formatNumber value="${psiVatInvoiceInfo.totalAmount}" pattern="###,###.00"/></td>
				<td><fmt:formatNumber value="${psiVatInvoiceInfo.totalAmount/1.17}" pattern="###,###.00"/> </td>
				<td><fmt:formatNumber value="${psiVatInvoiceInfo.totalAmount-psiVatInvoiceInfo.totalAmount/1.17}" pattern="###,###.00"/> </td>
				<td>
					<shiro:hasPermission name="psi:product:viewPrice">
						<input type="hidden" class="inId" value="${psiVatInvoiceInfo.id}" />
						<a href="#" class="inRemark"  data-type="text" data-pk="1" data-title="Enter Remark" data-value="${psiVatInvoiceInfo.remark}">${psiVatInvoiceInfo.remark}</a>
					</shiro:hasPermission>
					<shiro:lacksPermission name="psi:product:viewPrice">
						${psiVatInvoiceInfo.remark}
					</shiro:lacksPermission>
				</td>
				<td>
				<shiro:hasPermission name="psi:psiVatInvoiceInfo:edit">
					<c:if test="${psiVatInvoiceInfo.createUser.id eq fns:getUser().id}">
						<a class="btn btn-small"  href="${ctx}/psi/psiVatInvoiceInfo/delete?id=${psiVatInvoiceInfo.id}" onclick="return confirmx('确认要删除该条增值税发票信息？', this.href)">删除</a>
					</c:if>
				</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	<div id="updateExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm" action="${ctx}/psi/productEliminate/uploadFile" method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">文件上传</h3>
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
