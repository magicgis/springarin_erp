<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>增值税发票管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
			
			
			$("#createData").click(function(){
				$("#returnMsg").html('');
				var params = {};
				top.$.jBox.confirm('您确定分配发票吗','系统提示',function(v,h,f){
					if(v=='ok'){
						$("#createData").attr("disabled","disabled");
						$.post("${ctx}/psi/psiInvoice/arrangeInvoice",$.param(params),function(data){
							if(data!=null&&data!=''){
								$.jBox.tip(data,'info',{timeout:2000});
								$("#returnMsg").html("<div class='alert alert-info'>"+data+"</div>");
							}else{
								$.jBox.tip('未分配','info',{timeout:2000});
							}
							
							 $("#createData").removeAttr("disabled");
					    }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#exportData").click(function(){
				$("#searchForm").attr("action","${ctx}/psi/psiInvoice/expDeclare");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/psi/psiInvoice/declareList");
			});
			
			$("#exportOutData").click(function(){
				$("#searchForm").attr("action","${ctx}/psi/psiInvoice/expOutputDeclare");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/psi/psiInvoice/declareList");
			});
			
			
			$("#checkall").click(function(){
				 $('[name=checkId]:checkbox').each(function(){
				     if($(this).attr("disabled")!='disabled'){
				    	 this.checked=this.checked;
				     }else{
				    	 this.checked=false;
				     }
				 });
			});
			
			$("#delInvoice").click(function(){
				var ids = $("input:checkbox[name='checkId']:checked");
				if(!ids.length){
			    	$.jBox.tip('Please select data ！');
					return;
				}		
				var arr = new Array();
				for(var i=0;i<ids.length; i++){
					var id = ids[i].value;
					arr.push(id);
				}
				var idsAll = arr.join(',');
				top.$.jBox.confirm("确定删除这些发票？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						   $("#delInvoice").attr("disabled","disabled");
						   $.post("${ctx}/psi/psiInvoice/deleteDeclare",{delIds:idsAll},function(date){
							   if(date=='0'){
								   $.jBox.tip('删除成功');
								   $("#searchForm").submit();
							   }else{
								   $.jBox.tip('删除失败');
							   }
							  
					       }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
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
		<li><a href="${ctx}/psi/psiInvoice">发票列表</a></li>
		<li  class="active"><a href="${ctx}/psi/psiInvoice/declareList">报关列表</a></li>
		<li><a href="${ctx}/psi/psiInvoice/productList">商品税率列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiInvoiceTransportDeclare" action="${ctx}/psi/psiInvoice/declareList" method="post" class="breadcrumb form-search" >
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>报关单号/运单号：</label><input type="text" name="productName" value="${psiInvoiceTransportDeclare.productName}" style="width:150px"/>
		<label>创建时间：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiInvoiceTransportDeclare.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="arrangeDate" value="<fmt:formatDate value="${psiInvoiceTransportDeclare.arrangeDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<input id="delInvoice" class="btn btn-primary" type="button"  value="删除"/>
		<a href="#declarationExcel" role="button" class="btn  btn-primary" data-toggle="modal" id="uploaddeclarationFile">上传报关单</a> 
		<input id="createData" class="btn btn-warning" type="button" value="分配发票"/>
		<input id="exportData" class="btn btn-info" type="button" value="导出"/>
		<input id="exportOutData" class="btn btn-info" type="button" value="导出出口明细"/>
	</form:form>
	<tags:message content="${message}"/>
	<div id='returnMsg'></div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
		   <th style="width: 3%"><input type="checkBox" id="checkall"></th>
		   <th>序号</th>
		   <th>出口日期</th>
		   <th>报关单号</th>
		   <th>项号</th>
		   <th>合同号</th>
		   <th>品名</th>
		   <th>数量</th>
		   <th>单价</th>
		   <th>创建时间</th>
		   <th>发票号</th>
		   <th>单价</th>
		   <th>分配时间</th>
		   <th>操作</th>
		 </tr>
		 </thead>   
		<tbody>
		<c:forEach items="${page.list}" var="declare">
			<tr>
			    <td><c:if test="${empty declare.arrangeDate}"><input type="checkBox" class="chebox" name="checkId" value="${declare.id}"/></c:if></td>
				<td>${declare.id}</td>
				<td><fmt:formatDate value="${declare.declareDate}" pattern="yyyy-MM-dd"/></td>
				<td>${declare.declareNo}</td>
				<td>${declare.declareNum}</td>
				<td>${declare.transportNo}</td>
				<td>${declare.productName}</td>
				<td>${declare.quantity}</td>	
				<td>${declare.price}</td>	
				<td><fmt:formatDate value="${declare.createDate}" pattern="yyyy-MM-dd"/></td>	
				<td>${declare.invoice.invoiceNo}</td>	
				<td>${declare.invoice.price}</td>	
				<td><fmt:formatDate value="${declare.arrangeDate}" pattern="yyyy-MM-dd"/></td>	
				
				<td>
				  <c:if test="${'1' ne declare.state}">
				            <div class="btn-group">
								   <button type="button" class="btn btn-small">编辑</button>
								   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
								      <span class="caret"></span>
								      <span class="sr-only"></span>
								   </button>
								   <ul class="dropdown-menu" >
								      <li><a  href="${ctx}/psi/psiInvoice/editDeclare?declareNo=${declare.declareNo}">分配</a></li>
								      <c:if test="${empty declare.arrangeDate}">
								         <li><a href="${ctx}/psi/psiInvoice/editDeclareInfo?id=${declare.id}">报关单</a></li>
								      </c:if>
								   </ul>
							 </div>
				    
				  </c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	<div id="declarationExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm" action="${ctx}/psi/psiInvoice/uploadDeclareFile" method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">报关单文件上传</h3>
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
