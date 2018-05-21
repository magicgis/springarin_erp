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
			       <c:if test="${!fns:endsWith(single,'LOCAL')&&!fn:contains(fn:toLowerCase(single),'old') }">
	                      html+="<option asinVal='${temp.asin}' eanVal='${temp.ean}' value='${single}'>${single}[${temp.partNumber}]</option>";
	                  
	               </c:if>
			     </c:forEach>
			   </c:forEach>  
			   
			   var catalogType="";
			   <c:forEach  items='${catalogMap}' var='catalogMap'>
			     catalogType+='<optgroup label="${catalogMap.key }">';
                   <c:forEach  items='${catalogMap.value}' var='catalog'>
                     catalogType+='<option value="${catalogMap.key },${catalog}"  ${catalog eq "Electrical"?"selected":""}>${catalog }</option>';
	               </c:forEach>
	               catalogType+='</optgroup>';
              </c:forEach> 
			 
				tr.append("<td ><select onchange='setAsin(this)' name='crossSku' style='width:90%;'  class='required'><option value='' selected='selected'>-请选择Sku-</option>"+html+"</select></td>");
				tr.append("<td class='CatalogType'><input type='hidden' name='catalogType1' value='HomeImprovement'/><input type='hidden' name='catalogType2' value='Electrical'/><select  style='width:90%' onchange='setCatalogTypeValue(this)' class='js-states form-control'>"+catalogType+"</select></td>");
				
				tr.append("<td><select name='sku' onchange='setQuantity(this)'><option value='' >-请选择Cross Sku-</option></select></td>");
				
				tr.append("<td class='crossQuantity'></td>");
				tr.append("	<td class='crossPartNumber'></td>");
				tr.append("	<td class='oldPrice'></td>");
				
				tr.append("<td><input type='text' style='width: 80%' name='price' class='price required'/></td>");
				//tr.append("<td><input type='text' style='width: 80%' name='salePrice' class='price required'/></td>");
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
				params.addType=3;
				window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?"+$.param(params);
			});
			
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
					 var info="";
					 var crossFlag=false;
					 $("#contentTable tbody tr").each(function(i,j){
						$(j).find(".crossQuantity").each(function(){
							 if($(this).text()=="0"){
								 crossFlag=true;
							 }
						});
					 });
					 if(crossFlag){
						 info+="没有库存,";
					 }
					 info+="确定要Cross吗?";
						top.$.jBox.confirm(info,'系统提示',function(v,h,f){
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
			//var ean=$(c).find("option:selected").attr("eanVal");
			var sku=$(c).val();
			$(c).parent().parent().find("[name='asin']").val(asin);
			
			$(c).parent().parent().find("[name='sku']").empty();   
        	var option = "<option value=''>--请选择Cross Sku--</option>";   
        
            <c:forEach items='${allSku }' var='temp'>
               if(asin=='${temp.asin}'){
            	   option += "<option  quantityVal='${temp.quantity}' asinVal='${temp.asin}' countryVal='${temp.accountName}' value='${temp.sku}'>${temp.sku}</option>"; 
               }
			</c:forEach>
			
            $(c).parent().parent().find("[name='sku']").append(option);
            $(c).parent().parent().find("[name='sku']").select2("val","");
		}
		
		function setQuantity(c){
			var quantity=$(c).find("option:selected").attr("quantityVal");
			$(c).parent().parent().find(".crossQuantity").text(quantity);
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/amazonPortsDetail/getAllContent',  
		        dataType:"json",
		        data : "selCountry=${amazonPostsDetail.country}&accountName="+ $(c).find("option:selected").attr("countryVal")+"&asin="+$(c).find("option:selected").attr("asinVal")+"&isPrice=1",  
		        async: true,
		        success : function(msg){
		        	$(c).parent().parent().find(".crossPartNumber").text(msg.partNumber);
		        	$(c).parent().parent().find(".oldPrice").text(msg.oldPrice);
		        	$(c).parent().parent().find("[name='price']").val(msg.price);
		        	$(c).parent().parent().find("[name='salePrice']").val(msg.salePrice);
		        }
			});   
		}
		function setCatalogTypeValue(c){
			var catalogType=$(c).val();
			var arr=catalogType.split(",");
			$(c).parent().parent().find("[name='catalogType1']").val(arr[0]);
			$(c).parent().parent().find("[name='catalogType2']").val(arr[1]);
			
		}
	</script>
</head>
<body>
	
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
		  <li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=3">Cross帖</a></li>	
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
		<input  type='hidden'  name='operateType' value='7'/>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select id="accountName" name="accountName" style="width: 200px" class="required">
					<option value="" selected="selected">-请选择平台-</option>
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
							  <c:forEach items="${accountMap['de']}" var="account">
							     <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							  </c:forEach>
							  <c:forEach items="${accountMap['fr']}" var="account">
							       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
						     <c:forEach items="${accountMap['es']}" var="account">
						       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
						     <c:forEach items="${accountMap['uk']}" var="account">
						       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
						     <c:forEach items="${accountMap['it']}" var="account">
						       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
					</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
						<shiro:hasPermission name="amazoninfo:feedSubmission:de">
							 <c:forEach items="${accountMap['de']}" var="account">
						       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:fr">
							 <c:forEach items="${accountMap['fr']}" var="account">
						       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:es">
							<c:forEach items="${accountMap['es']}" var="account">
						       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:it">
							<c:forEach items="${accountMap['it']}" var="account">
						       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:uk">
							<c:forEach items="${accountMap['uk']}" var="account">
						       <option value="${account}"  ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
						     </c:forEach>
						</shiro:hasPermission>
						
					</shiro:lacksPermission>
				</select>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>
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
							<th style="width:20%">Sku</th>
							<th style="width:20%">CatalogType</th>
							<th style="width:20%">Cross Sku</th>
							<th style="width:10%">Quantity</th>
							<th style="width:10%">partNumber</th>
							<th style="width:5%">原价</th>
							<th style="width:10%">Price</th>
							<!-- <th style="width:10%">Sale Price</th> -->
							<th style="width: 10%">操作</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
							       <select  name='crossSku' style="width:90%;"  class='required' onchange="setAsin(this)">
							          <option value="" selected="selected">-请选择Sku-</option>
							          <c:forEach items='${list}' var='temp'>
							             <c:forEach items="${fn:split(temp.sku,',')}" var="single">
							               <c:if test="${!fns:endsWith(single,'LOCAL')&&!fn:contains(fn:toLowerCase(single),'old') }">
							                 <%--  <c:if test="${'de' eq amazonPostsDetail.country&&(fn:contains(fn:toLowerCase(single),'de')||(!fn:contains(fn:toLowerCase(single),'uk')&&!fn:contains(fn:toLowerCase(single),'fr')&&!fn:contains(fn:toLowerCase(single),'it')&&!fn:contains(fn:toLowerCase(single),'es'))) }">
							                      <option asinVal='${temp.asin }' eanVal='${temp.ean }' value="${single}">${single}[${temp.partNumber}]</option>
							                  </c:if>
							                  <c:if test="${'de' ne amazonPostsDetail.country}"><!-- &&fn:contains(fn:toLowerCase(single),fn:toLowerCase(amazonPostsDetail.country)) -->
							                    --%>   
							                    <option asinVal='${temp.asin }' eanVal='${temp.ean }' value="${single}">${single}[${temp.partNumber}]</option>
							                 <%--  </c:if> --%>
							               </c:if>
							             </c:forEach> 
							          </c:forEach>
							      </select>
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
							<td><select name="sku" onchange='setQuantity(this)'><option value="" >-请选择Cross Sku-</option></select></td>
							
							<td class='crossQuantity'></td>
							<td class='crossPartNumber'></td>
							<td class='oldPrice'></td>
							<td><input type="text" style="width: 80%" name="price" class="price required"/></td>
							<!-- <td><input type="text" style="width: 80%" name="salePrice" class="price required" /></td> -->
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