<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>ean</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		if(!(top)){
			top = self; 
		}
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					var filepath = $("#excel").val();
					var extStart = filepath.lastIndexOf(".");

					var ext = filepath.substring(extStart, filepath.length).toUpperCase();
					if (ext != ".TXT" && ext != ".CSV") {
						alert("文件必须是txt或者csv格式!");
						return;
					}
					top.$.jBox.confirm('确定要提交吗!','系统提示',function(v,h,f){
						if(v=='ok'){
							loading('正在提交，请稍等...');
							form.submit();
							$("#btnSubmit").attr("disabled","disabled");
						}
					},{buttonsFocus:1,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			$("#active,#country").change(function(){
				$("#searchForm").submit();
			});
			
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
		                	if(responseStr=='1'){
		                		$.jBox.tip('文件上传失败'); 
		                	}else{
		                		$.jBox.tip('文件上传成功'); 
		                	}
		                	$("#searchForm").submit();
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
	    <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">新增管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addParentsPostFrom">新建母帖</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom">新建普通帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=8">新建本地帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=2">复制帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=3">Cross帖</a></li>
		    </ul>
	   </li>
	     <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
			       <li><a href="${ctx}/amazoninfo/amazonPortsDetail/form">编辑帖子信息</a></li>	
			       <li><a href="${ctx}/amazoninfo/amazonPortsDetail/commonForm">编辑帖子信息(英语国家)</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=4">帖子类型转换</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=5">帖子一键还原</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/deletePostsForm">删除帖子</a></li>	
		    </ul>
	   </li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/postsRelationList">组合帖管理列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/formRelation">修改绑定关系</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/findEanList">Ean列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/findProductTypeCodeList">品类代码</a></li>
	</ul>
	
	
	<form:form id="searchForm"  action="${ctx}/amazoninfo/amazonPortsDetail/findEanList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 30px;line-height: 30px">
			<div> 
				类型：<select name="active" id="active" style="width: 120px">
				    <!--   <option value="">All</option> -->
				      <shiro:hasPermission name="amazoninfo:productTypeGroup:edit">
				         <option value="0" ${amazonEan.active eq  '0'?'selected':''}>有效</option>
				      </shiro:hasPermission>   
					  <option value="1" ${amazonEan.active eq  '1'?'selected':''} >失效</option>		
					  <option value="2" ${amazonEan.active eq  '2'?'selected':''} >已使用</option>		
				</select>&nbsp;&nbsp;
				国家：<select name="country" id="country" style="width: 120px">
						<option value="">-All-</option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${dic.value eq amazonEan.country?'selected':''}  >${dic.label}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;
				Ean：<input name="ean" maxlength="50" class="input-small" value='${amazonEan.ean }'/>
				 
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<shiro:hasPermission name="amazoninfo:productTypeGroup:edit">
				     	&nbsp;&nbsp;<a href="#updateExcel" role="button" class="btn" data-toggle="modal" id="uploadFile"><spring:message code="sys_but_upload"/></a> 
				</shiro:hasPermission>
			</div> 
		</div>
	</form:form>
	
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width:5%">编号</th>
				<th style="width:10%">Ean</th>
				<th style="width:10%">创建时间</th>
				<th style="width:10%">状态</th>
				<th style="width:30%">产品名</th>
				<th style="width:20%">国家</th>
				<th style="width:10%">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="amazonEan" varStatus="i">
			<tr>
				<td style="text-align: center;vertical-align: middle;">${amazonEan.id}</td>
				<td>${amazonEan.ean}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${amazonEan.createDate}"/></td>
				<td>${amazonEan.state}</td>
				<td>${amazonEan.productName}</td>
				<td>${'com' eq amazonEan.country?'us':amazonEan.country}</td>
				<td><a class='btn' href="${ctx}/amazoninfo/amazonPortsDetail/updateActive?id=${amazonEan.id}&active=1">失效</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	
	<div id="updateExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm"  method="post" action='${ctx}/amazoninfo/amazonPortsDetail/uploadEanFile'>
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">Ean文件上传</h3>
						  </div>
						  <div class="modal-body">
							<label >Ean文件：</label>
							<input id="uploadFileName" name="excel" type="file" class="required"/>
						  </div>
						   <div class="modal-footer">
						   <button class="btn btn-primary"  type="button" id="uploadTypeFile"><spring:message code="sys_but_upload"/></button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
   </div>

</body>
</html>
