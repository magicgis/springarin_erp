<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊帖子删除</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" type="text/css" />
	<script type="text/javascript" src="${ctxStatic}/x-editable/js/bootstrap-editable.js"></script>
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
			$('#add-row').on('click', function(e){
			   e.preventDefault();
			   var tableBody = $('.table > tbody'), 
			   lastRowClone = $('tr:last-child', tableBody).clone();
			   $('input[type=text][class!=Wdate]', lastRowClone).val('');  
			 var html = "<option value=''>-请选择sku-</option>";
			 <c:forEach items="${list}" var="item"> 
			 		html = html+"<option asinVal='${item.asin}' value=${item.sku}>${item.productName}[${item.sku}]</option>";
			 </c:forEach>
			 <c:if test="${fn:length(item)>0}">
			 	html = html+"<option value='sku'>手动输入Sku</option>";
			 </c:if>
			 lastRowClone.find(".sku").html("<select onchange='setAsin(this)' class=\"required\" name=\"sku\" style=\"width: 90%\">"+html+"</select><input name=\"sku\" type=\"text\" style=\"margin-top:5px;display: none;\"/>");
			   
			 	lastRowClone.find("select").select2();
			   tableBody.append(lastRowClone);
			});
			$('#contentTable').on('click', '.remove-row', function(e){
			  e.preventDefault();
			  if($('#contentTable tr').size()>2){
				  var row = $(this).parent().parent();
				  row.remove();
			  }
			});
			
			$("#accountName").change(function(){
				var params = {};
				params.accountName = $(this).val();
				window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/deletePostsForm?"+$.param(params);
			});
			
			
			$("#inputForm").on("change","select[name='sku']",function(){
				if($(this).val()=='sku'){
					$(this).parent().find("input[type='text']").show();
				}else{
					$(this).parent().find("input[type='text']").hide();
				}
			});
		
			
			$("#inputForm").validate({
				submitHandler: function(form){
					
						top.$.jBox.confirm('确定要删除帖子吗!','系统提示',function(v,h,f){
							if(v=='ok'){
								loading('正在提交，请稍等...');
								$("#contentTable tbody tr").each(function(i,j){
									if($(j).find("select").select2("val")!='sku'){
										$(j).find("select").attr("name","items"+"["+i+"]."+$(j).find("select").attr("name"));
									}
									$(j).find("input[type!='']").each(function(){
											if($(this).attr("name")&&$(this).css("display")!='none'){
												$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
											}
									});
								});
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
		});
		
		function setAsin(c){
			var asin=$(c).find("option:selected").attr("asinVal");
			$(c).parent().parent().find("[name='asin']").val(asin);
		}
	</script>
</head>
<body>
	
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
		  <li  class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/deletePostsForm">删除帖子</a></li>		
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
	</ul><br/>
	<form id="inputForm"  action="${ctx}/amazoninfo/amazonPortsDetail/saveOtherPostsChange" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<input  type='hidden'  name='operateType' value='3'/>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select id="accountName" name="accountName" style="width: 120px" class="required">
					<option value="" selected="selected">-请选择平台-</option>
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:forEach items="${accountMap[dic.value]}" var="account">
								<option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							</c:forEach>
						</c:forEach>
					</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
					   <c:forEach items="${fns:getDictList('platform')}" var="dic">
					        <shiro:hasPermission name="amazoninfo:feedSubmission:${dic.value}">
							  <c:forEach items="${accountMap[dic.value]}" var="account">
								  <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							  </c:forEach>
							</shiro:hasPermission>  
						</c:forEach>
					</shiro:lacksPermission>
				</select>
				
				<script type="text/javascript">
					$("option[value='${amazonPostsDetail.accountName}']").attr("selected","selected");				
				</script>
			</div>
		</div>
		
		<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" id="add-row"><span class="icon-plus"></span>新增删除产品</a></div>
		<div class="control-group">
			<label class="control-label">删除帖子:</label>
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="width: 100px">产品</th>
							<th style="width: 10px">操作</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td class="sku">
							<select style="width: 90%" name="sku" class="required" onchange="setAsin(this)">
							    <option value=''>-请选择sku-</option>
								<c:forEach items="${list}"   var="item">
									<option asinVal='${item.asin }' value="${item.sku}">${item.productName }[${item.sku}]</option>									
								</c:forEach>
								<c:if test="${fn:length(list)>0}">
									<option value="sku">手动填写Sku</option>
								</c:if>
							</select>
							<input name="sku" type="text" style="margin-top:5px;display: none;"/>
							</td>
							<td><input type='hidden' name='asin' /><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>	
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提  交"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>