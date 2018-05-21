<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>待发货物流管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	
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
			$("#uploadTypeFile").click(function(e){
				   //$("#uploadForm").submit();
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
			
			
			$("#expExcel").click(function(){
				var params = {};
				window.location.href = "${ctx}/psi/psiTransportOrderReplace/expTransport?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
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
		     $("#uploadForm").attr("action","${ctx}/psi/psiTransportOrderReplace/upload?psiTransportId="+id);
		  }
		 
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	  <li class="active"><a href="${ctx}/psi/psiTransportOrderReplace/">代发货物流管理</a></li>
	  <li ><a href="${ctx}/psi/psiTransportOrderReplace/edit">新建</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiTransportOrderReplace" action="${ctx}/psi/psiTransportOrderReplace/" method="post" class="breadcrumb form-search" cssStyle="height:50px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 40px;">
			<label>Create Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiTransportOrderReplace.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="etdDate" value="<fmt:formatDate value="${psiTransportOrderReplace.etdDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
			<label>CNTS：</label>
			<form:input path="boxNumber" id="boxNumber" htmlEscape="false" maxlength="5" class="input-small" style="width:50px"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<input class="btn btn-success" id="expExcel" type="button" value="导出"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr><th>NO.</th><th>POL</th><th>POD</th><th>CTNS</th><th>C.W</th><th>CBM</th><th>ETD&nbsp;&nbsp;</th><th>ETA&nbsp;&nbsp;</th>
		<th>Arrival</th><th>Bill NO.</th><th>Status</th><th>CreateDate</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiTransportOrderReplace">
			<tr>
				<td><a href="${ctx}/psi/psiTransportOrderReplace/view?id=${psiTransportOrderReplace.id}">${psiTransportOrderReplace.id}</a></td>
				<td>${psiTransportOrderReplace.orgin }</td>
				<td>${psiTransportOrderReplace.destination }</td>
				<td>${psiTransportOrderReplace.boxNumber }</td>
				<td>${psiTransportOrderReplace.weight }</td>
				<td>${psiTransportOrderReplace.volume }</td>  
				<td><fmt:formatDate pattern="M-dd" value="${psiTransportOrderReplace.etdDate }"/></td>
				<td><fmt:formatDate pattern="M-dd" value="${psiTransportOrderReplace.etaDate }"/></td>
				<td><fmt:formatDate pattern="M-dd" value="${psiTransportOrderReplace.arrivalDate }"/></td>
				<td>${psiTransportOrderReplace.ladingBillNo }</td>  
				<td>${psiTransportOrderReplace.replaceSta}</td>  
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${psiTransportOrderReplace.createDate}"/></td>
				<td>
					<shiro:hasPermission name="psi:transportReplace:edit">
						<a  class="btn btn-small"  href="${ctx}/psi/psiTransportOrderReplace/edit?id=${psiTransportOrderReplace.id}">编辑</a>
						<a  class="btn btn-small"  href="${ctx}/psi/psiTransportOrderReplace/cancel?id=${psiTransportOrderReplace.id}">取消</a>
						<a href="#updateExcel" role="button" class="btn btn-small" data-toggle="modal" id="uploadFile" onclick="uploadFile(${psiTransportOrderReplace.id})"><spring:message code="sys_but_upload"/></a>
					</shiro:hasPermission>
					<a  class="btn btn-small"  href="${ctx}/psi/psiTransportOrderReplace/view?id=${psiTransportOrderReplace.id}">查看</a>
				</td>
			</tr>
			
		</c:forEach>
		</tbody>
	</table>
	
	        <div id="updateExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm"  method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel"><spring:message code="psi_transport_upload"/> </h3>
						  </div>
						  <div class="modal-body">
							<label ><spring:message code="psi_transport_fileType"/> ：</label>
							<select  id="uploadType" name="uploadType">
								<option value="0">Bill of lading</option>
								<option value="1">PI</option>
								<option value="2">PL</option>
								<option value="3">Other</option> 
							</select><br/><br/>
							<input id="uploadFileName" name="uploadFile" type="file" />
							 
						  </div>
						   <div class="modal-footer">
						   <button class="btn btn-primary"  type="button" id="uploadTypeFile"><spring:message code="sys_but_upload"/></button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
			 </div>
	<div class="pagination">${page}</div>
</body>
</html>
