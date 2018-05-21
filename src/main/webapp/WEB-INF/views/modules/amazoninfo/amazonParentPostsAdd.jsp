<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊新增母帖</title>
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
				var catalogType="";
				   <c:forEach  items='${catalogMap}' var='catalogMap'>
				     catalogType+='<optgroup label="${catalogMap.key }">';
	                   <c:forEach  items='${catalogMap.value}' var='catalog'>
	                     catalogType+='<option value="${catalogMap.key },${catalog}"  ${catalog eq "Electrical"?"selected":""}>${catalog }</option>';
		               </c:forEach>
		               catalogType+='</optgroup>';
	              </c:forEach> 
	              
	            var oldSku="<option value=''>新增</option>";  
	            
	            <c:forEach items='${parentList}' var='temp'>
	              oldSku+="<option value='${temp}'>${temp}</option>";
		        </c:forEach>
		        
		        tr.append("<td class='existSku'><input name='flag' type='hidden'/><select  style='width:90%' onchange='setNewSku(this)' class='js-states form-control'>"+oldSku+"</select></td>"); 
				tr.append("<td class='sku'><input name='sku' type='text' style='width:80%' class='required' onkeyup='isExistSku(this)'/>&nbsp;&nbsp;<div class='showMsg'></div></td>");
				tr.append("<td class='CatalogType'><input type='hidden' name='catalogType1' value='HomeImprovement'/><input type='hidden' name='catalogType2' value='Electrical'/><select  style='width:90%' onchange='setCatalogTypeValue(this)' class='js-states form-control'>"+catalogType+"</select></td>");
				tr.append("<td class='themeType'><select name='variationTheme'  style='width:90%' ><option value='Size' >Size</option><option value='Color' >Color</option><option value='Size-Color' >Size-Color</option></select></td>");
				tr.append("<td class='title'><input name='title' type='text' style='width:90%'/></td>");
				tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span></a></td>");
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
				window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/addParentsPostFrom?"+$.param(params);
			});
			
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
					
						top.$.jBox.confirm('确定要新增母帖吗!','系统提示',function(v,h,f){
							if(v=='ok'){
								loading('正在提交，请稍等...');
								$("#contentTable tbody tr").each(function(i,j){
									
									$(j).find("select").each(function(){
										if($(this).val()!='sku'&&$(this).attr("name")!=undefined){
											console.log($(this).attr("name")+"=="+$(this).val());
											//$(j).find("select").attr("name","items"+"["+i+"]."+$(j).find("select").attr("name"));
											$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
										}
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
		
		function isExistSku(obj){
			var parentSku=$(obj).val();
			$(obj).parent().find(".showMsg").html("");
			<c:forEach items='${parentList}' var='temp'>
			  if(parentSku=='${temp}'){
				  $(obj).parent().find(".showMsg").html("<font color='red'>已存在相同的母Sku!!!</font>");
				  return;
			  }
			</c:forEach>
		}
		
		function setNewSku(obj){
			var parentSku=$(obj).val();
			$(obj).parent().parent().find("[name='sku']").val(parentSku);
			if(parentSku!=''){
				$(obj).parent().parent().find("[name='flag']").val('1');
				$(obj).parent().parent().find("[name='sku']").attr("readonly",true);
			}else{
				$(obj).parent().parent().find("[name='flag']").val('0');
				$(obj).parent().parent().find("[name='sku']").attr("readonly",false);
			}
		}
		function setCatalogTypeValue(c){
			var catalogType=$(c).val();
			var arr=catalogType.split(",");
			$(c).parent().parent().find("[name='catalogType1']").val(arr[0]);
			$(c).parent().parent().find("[name='catalogType2']").val(arr[1]);
			var options="";
			$(c).parent().parent().find("[name='variationTheme']").empty(); 
			if(arr[0]=='Lighting'||arr[0]=='Wireless'||arr[0]=='Office'||arr[0]=='CE'){
				  options+='<option value="Color" >Color</option>';
			}else{
				options+='<option value="Size" >Size</option>';
				options+='<option value="Color" >Color</option>';
			    options+=' <option value="Size-Color" >Size-Color</option>';
			}
			$(c).parent().parent().find("[name='variationTheme']").append(options);
			$(c).parent().parent().find("[name='variationTheme']").select2("val",'Color'); 
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
	<%-- 	<li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
		<li  class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/addParentsPostFrom">新建母帖</a></li>	
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
		<input  type='hidden'  name='operateType' value='2'/>
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
		
		<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" id="add-row"><span class="icon-plus"></span>新增父帖</a></div>
		<div class="control-group">
			<label class="control-label">新增母帖:</label>
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
						    <th>母SKU</th>
							<th>Sku</th>
							<th>CatalogType</th>
							<th>VariationTheme</th>
							<th>Title</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<tr>
						    <td class="existSku"><input name='flag' type='hidden'/>
						      <select  style='width:90%' onchange='setNewSku(this)' class="js-states form-control">
						         <option value="">新增</option>
							     <c:forEach items='${parentList}' var='temp'>
							         <option value="${temp}">${temp}</option>
							     </c:forEach>
							   </select>
							</td>
							<td class="sku">
							   <input name="sku" type="text" style="width:80%" class="required"/>
							   &nbsp;&nbsp;<div class='showMsg'></div>
							</td>
							<td class='CatalogType'>
							      <input type='hidden' name='catalogType1' value='HomeImprovement'/>
				        	      <input type='hidden' name='catalogType2' value='Electrical'/>
				        	      <select  style='width:90%' onchange='setCatalogTypeValue(this)' class="js-states form-control">
				        	         <c:forEach  items='${catalogMap}' var='catalogMap'>
				        	               <optgroup label="${catalogMap.key }">
				        	                  <c:forEach  items='${catalogMap.value}' var='catalog'>
								                  <option value="${catalogMap.key },${catalog}"  ${catalog eq 'Electrical' ?'selected':''}>${catalog }</option>
								               </c:forEach>
								           </optgroup>
				        	         </c:forEach>
								    
								  </select>
							</td>
							<td class='themeType'>
							   <select name="variationTheme"  style='width:90%' class="js-states form-control">
							      <option value="Size" >Size</option>
							      <option value="Color" >Color</option>
							      <option value="Size-Color" >Size-Color</option>
							   </select>
							</td>
							<td class='title'><input name="title" type="text" style="width:90%" /></td>
							<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></td>
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