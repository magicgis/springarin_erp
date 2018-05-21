<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>帖子类型转换</title>
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
				var tbody=$("#contentTable tbody");
				var tr=$("<tr></tr>");
			    var html="";
			   <c:forEach items='${list}' var='temp'>
			     <c:forEach items="${fn:split(temp.sku,',')}" var="single"> 
			       html+="<option asinVal='${temp.asin}' value='${single}'>${single}[${postsType[amazonPostsDetail.country][single] }]</option>";
			      </c:forEach>
			   </c:forEach>  
				tr.append("<td ><select onchange='setAsin(this)' name='sku' style='width:90%;'  class='required'><option value='' selected='selected'>-请选择Sku-</option>"+html+"</select></td>");
				tr.append("<td ><select name='isFba'><option value='1' >FBA帖</option><option value='0' >本地帖</option></select></td>");
				tr.append("<td><input type='hidden' name='asin' /><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span></a></td>");
				tbody.append(tr);
				tr.find("select").select2();
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
				params.addType=4;
				window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?"+$.param(params);
			});
			
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
					
						top.$.jBox.confirm('确定要转换类型吗!','系统提示',function(v,h,f){
							if(v=='ok'){
								loading('正在提交，请稍等...');
								$("#contentTable tbody tr").each(function(i,j){
									$(j).find("select").each(function(){
										$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
									});
									
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
			var type=$(c).find("option:selected").attr("typeVal");
			if(type=='FBA帖'){
				$(c).parent().parent().find("[name='isFba']").select2("val","0");
			}else if(type=='本地帖'){
				$(c).parent().parent().find("[name='isFba']").select2("val","1");
			}
			showQuantity($(c).parent().parent().find("[name='isFba']"));
		}
		
		function showQuantity(c){
			var type=$(c).val();
			if(type=='0'){
				$(c).parent().find("input[type='text']").show();
				var sku=$(c).parent().parent().find("[name='sku']").val();
				var country= '${amazonPostsDetail.country}';
				var wareHouseId=19;
				if(country.indexOf('com')>=0){
					wareHouseId=120;
				}else if(country.indexOf('jp')>=0){
					wareHouseId=147;
				}
				if(sku!=''){
					$.ajax({  
				        type : 'POST', 
				        url : '${ctx}/amazoninfo/amazonPortsDetail/getNewQuantity',  
				        dataType:"json",
				        data : "country="+country+"&sku="+sku+"&wareHouseId="+wareHouseId,  
				        async: true,
				        success : function(msg){
				        	$(c).parent().find("input[type='text']").val(msg);
				        }
					}); 
				}
			}else{
				$(c).parent().find("input[type='text']").hide();
			}
		}
	</script>
</head>
<body>
	
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
		  <li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=4">帖子类型转换</a></li>	
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
		<input  type='hidden'  name='operateType' value='4'/>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select id="accountName" name="accountName" style="width: 120px" class="required">
					<option value="" selected="selected">-请选择平台-</option>
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
						  <c:forEach items="${accountMap['de']}" var="account">
							     <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							  </c:forEach>
							  <c:forEach items="${accountMap['com']}" var="account">
							       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
						     <c:forEach items="${accountMap['jp']}" var="account">
						       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
					</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
						<shiro:hasPermission name="amazoninfo:feedSubmission:de">
							 <c:forEach items="${accountMap['de']}" var="account">
							     <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							  </c:forEach>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:com">
							 <c:forEach items="${accountMap['com']}" var="account">
							     <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							  </c:forEach>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:jp">
							 <c:forEach items="${accountMap['jp']}" var="account">
							     <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							  </c:forEach>
						</shiro:hasPermission>
					</shiro:lacksPermission>
				</select>
				<script type="text/javascript">
					$("option[value='${amazonPostsDetail.accountName}']").attr("selected","selected");				
				</script>
			</div>
		</div>
		
		<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" id="add-row"><span class="icon-plus"></span>新增</a></div>
		<div class="control-group">
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="width: 100px">Sku</th>
							<th style="width: 30px">Type</th>
							<th style="width: 10px">操作</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
							       <select  name='sku' style="width:90%;"  class='required' onchange="setAsin(this)">
							          <option value="" selected="selected">-请选择Sku-</option>
							          <c:forEach items='${list}' var='temp'>
							             <c:forEach items="${fn:split(temp.sku,',')}" var="single">
							                <option asinVal='${temp.asin }' typeVal='${postsType[amazonPostsDetail.accountName][single] }' value="${single}">${single}[${postsType[amazonPostsDetail.accountName][single] }]</option>
							             </c:forEach> 
							          </c:forEach>
							      </select>
							</td>
							<td >
							   <select name="isFba" onchange='showQuantity(this)'>
							      <option value="1" >FBA帖</option>
							      <option value="0" >本地帖</option>
							   </select>
							     &nbsp;&nbsp;
							   <input type='text' style='display:none' name='quantity' />
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