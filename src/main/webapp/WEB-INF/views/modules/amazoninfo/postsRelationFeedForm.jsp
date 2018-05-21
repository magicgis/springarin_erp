<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊帖子上架</title>
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
				 var html = "<option value=''>-请选择产品-</option>";
				 <c:forEach items="${childList}" var="item"> 
				 		html = html+"<option value='${item.sku}'  nameVal='${item.productName}'>${empty item.productName?item.sku:item.productName}[${item.sku }]</option>";
				 </c:forEach>
				 <c:if test="${fn:length(childList)>0}">
				 	html = html+"<option value='sku'>手动输入Sku</option>";
				 </c:if>
				  var catalogType="";
				   <c:forEach  items='${catalogMap}' var='catalogMap'>
				     catalogType+='<optgroup label="${catalogMap.key }">';
	                   <c:forEach  items='${catalogMap.value}' var='catalog'>
	                     catalogType+='<option value="${catalogMap.key },${catalog}"  ${catalog eq "Electrical"?"selected":""}>${catalog }</option>';
		               </c:forEach>
		               catalogType+='</optgroup>';
	              </c:forEach> 
	              
			   lastRowClone.find(".sku").html("<select class='selectName' class=\"required\" name=\"sku\" style=\"width: 90%\">"+html+"</select><input name=\"sku\" type=\"text\" style=\"margin-top:5px;display: none;\"/>");
			   lastRowClone.find("select").select2();
			   lastRowClone.find(".CatalogType").html("<input type='hidden' name='catalogType1' value='HomeImprovement'/><input type='hidden' name='catalogType2' value='Electrical'/><select  style='width:90%' onchange='setCatalogTypeValue(this)' class='js-states form-control'>"+catalogType+"</select>");
			   lastRowClone.find("select").select2();
			  // lastRowClone.find("input[name='childParentSku']").attr("readonly",true); 
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
				window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/formRelation?"+$.param(params);
				
			});
			
			$("#operat").change(function(){
				var params = {};
				params.accountName = $("#accountName").val();
				if($("#operat").val()=='2'){
				  // $("#parentSku").attr("disabled",false); 
				   //$("#banding").show();
					params.type='2';
					
				}else if($("#operat").val()=='1'){
				  // $("#parentSku").attr("disabled",true); 
				  // $("#banding").hide();
					params.type='1';
				}else{
					params.type='3';
				}
				window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/formRelation?"+$.param(params);
			});
			
			$("#parentSku").change(function(){
				if($(this).val()=='newSku'){
					$(this).parent().find("input[type='text']").show();
					$("#showMsg").html("<b>&nbsp;&nbsp;(新绑定父Sku的子产品size和color至少需填一项)</b>");
				}else if($(this).val()!=''){
					$(this).parent().find("input[type='text']").hide();
					$.ajax({
		      			type: "POST",
		      			url: "${ctx}/amazoninfo/amazonPortsDetail/getBySizeOrColor?accountName="+$("#accountName").val()+"&sku="+encodeURIComponent($(this).val()),
		      			async: true,
		      			success: function(msg){
		      			    $("#sizeOrColor").val(msg);
		      			    var showMsg="";
			      			if(msg=="0"){//没查询到
								showMsg="<b>&nbsp;&nbsp;(没查询到新绑定父sku是按照size还是color绑定产品的)</b>";
							}else if(msg=="1"){//size color
								showMsg="<b>&nbsp;&nbsp;(新绑定父Sku是按size-color绑定的帖)</b>";
							}else if(msg=="2"){//size
								showMsg="<b>&nbsp;&nbsp;(新绑定父Sku是按size绑定的帖)</b>";
							}else if(msg=="3"){//color
								showMsg="<b>&nbsp;&nbsp;(新绑定父Sku是按color绑定的帖)</b>";
							}
		      			   $("#showMsg").html(showMsg);
		      			}
		          	});
				}
			});
			
			
			$("#inputForm").on("change","select[name='sku']",function(){
				$(this).parent().parent().find("input[name='color']").val('');
				$(this).parent().parent().find("input[name='size']").val('');
				$(this).parent().parent().find("input[name='compareColor']").val('');
				$(this).parent().parent().find("input[name='compareSize']").val('');
				//$(this).parent().parent().find("input[name='childParentSku']").attr("readonly",true);
				if($(this).val()=='sku'){
					$(this).parent().find("input[type='text']").show();
				}else if($(this).val()!=''){
					$(this).parent().find("input[type='text']").hide();
					var $this = $(this);
					$.ajax({
	      			   type: "POST",
	      			   url: "${ctx}/amazoninfo/amazonPortsDetail/getChangeDetail?accountName="+$("#accountName").val()+"&sku="+$(this).val(),
	      			   dataType:"json",
	      			   async: true,
	      			   success: function(msg){
	      				  $this.parent().parent().find("input[name='color']").val(msg.color);
	      				  $this.parent().parent().find("input[name='size']").val(msg.size);
	      				  $this.parent().parent().find("input[name='compareColor']").val(msg.color);
	     				  $this.parent().parent().find("input[name='compareSize']").val(msg.size);
	     				 
	      				  if(msg.parentSku=="0"){
	      					$this.parent().parent().find("input[name='childParentSku']").val('');
	      				  }else if(msg.parentSku=="1"){
	      					//$this.parent().parent().find("input[name='parentSku']").val(msg.parentSku); 
	      					$this.parent().parent().find("input[name='childParentSku']").attr("readonly",false);  
	      				  }else{
	      					$this.parent().parent().find("input[name='childParentSku']").val(msg.parentSku); 
	      				  }
	      			   }
	          		});
				}
			});
			
			$("#btnSubmit").click(function(){
				if($("#operat").val()=='2'){
					var parentSku="";
					if($("#parentSku").val()!='newSku'){
						parentSku=$("#parentSku").val();
					}else{
						parentSku=$("#parentSku2").val();
					}
					
					$.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/amazoninfo/amazonPortsDetail/findNameByParentSku?accountName="+$("#accountName").val()+"&parentSku="+parentSku,
		      			   dataType:"json",
		      			   async: true,
		      			   success: function(msg){
		      				  var num1=0;
		      				  var num2=0;
		      				  console.log(msg);
		      				$("#contentTable tbody tr").each(function(i,j){
		      					$(j).find("select").each(function(){
									if($(this).attr("name")=='sku'){
										num1=num1+1;
										var nameVal = $(this).find("option:selected").attr("nameVal");
										for(var i=0;i<msg.length;i++){
											console.log(nameVal+"=="+msg[i]);
				 							if(nameVal.indexOf(msg[i])>=0){
				 								 num2=num2+1;
				 							}
				 						 }
									}
								});
		      				});	
		      				  console.log(num1+"=="+num2);
		      				   if(num1==num2||parseInt(num2)>=parseInt(num1)){
		      					 $("#inputForm").submit();
		      				   }else{
		      					 var userHtml="";
		      				     <c:forEach  items='${allUser}' var='user'>
		      	                     userHtml+='<option value="${user.id}">${user.name }</option>';
		      	                 </c:forEach> 
		      	              
		      					 var html = "<div><select id='result'>"+userHtml+"</select></div>";
		      						
		      					 top.$.jBox.confirm(html,"选择审核人",function(v,h,f){
		      						if(v=='ok'){
		      						   $("#state").val("0");
		      						   $("#checkUserId").val(h.find("#result").val());
		      						   $("#inputForm").submit();
		      						}
		      					},{buttonsFocus:1,height:50});
		      					top.$('.jbox-body .jbox-icon').css('top','55px');
		      				   }
		      			   }
		          	});
				}else{
					$("#inputForm").submit();
				}
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					var flag = true;
					var msg="";
					if($("#parentSku").val()!='newSku'){
						$("#parentSku").attr("name","parentSku");
						if($("#operat").val()=='2'){
							$("#contentTable tbody tr").each(function(i,j){
								$(j).find("input[type!='']").each(function(){
									if($(this).attr("name")=='childParentSku'&&$(this).attr("readonly")==false){
										if($(this).val()==''){
											//flag=false;
											msg="原绑定父Sku不能为空";
											return;
										}
									}
									if($(this).attr("name")=='size'||$(this).attr("name")=='color'){
										/* if($("#sizeOrColor").val()=="0"){//没查询到
											flag=false;
											msg="没查询到新绑定sku是按照size还是color绑定产品的";
											return;
										}else  */
										if($("#sizeOrColor").val()=="1"){//size color
											if($(this).val()==''){
												//flag=false;
												msg="新绑定sku的size和color都是必填的";
												return;
											}
										}else if($("#sizeOrColor").val()=="2"){//size
											if($(this).attr("name")=='size'&&$(this).val()==''){
												//flag=false;
												msg="新绑定sku的size是必填的";
												return;
											}
										}else if($("#sizeOrColor").val()=="3"){//color
											if($(this).attr("name")=='color'&&$(this).val()==''){
												//flag=false;
												msg="新绑定sku的color是必填的";
												return;
											}
										}
									}
								});
							});
						}else if($("#operat").val()=='1'){
							$("#contentTable tbody tr").each(function(i,j){
								$(j).find("input[type!='']").each(function(){
									if($(this).attr("name")=='childParentSku'&&$(this).val()==''){
										msg="解绑不能存在无Parent Sku的记录";
										//flag=false;
										return;
									}
								});
							}); 
						}else if($("#operat").val()=='3'){
							var onlySizeOrColor=0;
							$("#contentTable tbody tr").each(function(i,j){
								$(j).find("input[type!='']").each(function(){
									if(($(this).attr("name")=='size'&&$(this).val()!='')||($(this).attr("name")=='color'&&$(this).val()!='')){
										onlySizeOrColor=1;
									}
								});
								if(onlySizeOrColor!=1){
									msg="子产品size和color至少需填一项";
									//flag=false;
									return;
								}
								
							}); 
						}
					}else{
						if($("#parentSku2").val()==''){
							msg="手动填写的父Sku不能为空";
							//flag=false;
						}
						$("#parentSku2").attr("name","parentSku");
						if($("#operat").val()=='2'){
							$("#contentTable tbody tr").each(function(i,j){
								var sizeOrColor=0;
								$(j).find("input[type!='']").each(function(){
									if($(this).attr("name")=='childParentSku'&&$(this).attr("readonly")==false){
										if($(this).val()==''){
											//flag=false;
											msg="原绑定父Sku不能为空";
											return;
										}
									}
									if(($(this).attr("name")=='size'&&$(this).val()!='')||($(this).attr("name")=='color'&&$(this).val()!='')){
										sizeOrColor=1;
									}
								});
								if(sizeOrColor!=1){
									msg="新绑定父Sku的子产品size和color至少需填一项";
									//flag=false;
									return;
								}
							});
						}else if($("#operat").val()=='1'){
							$("#contentTable tbody tr").each(function(i,j){
								$(j).find("input[type!='']").each(function(){
									if($(this).attr("name")=='childParentSku'&&$(this).val()==''){
										msg="解绑不能存在无Parent Sku的记录";
										//flag=false;
										return;
									}
								});
							});
						}else if($("#operat").val()=='3'){
							var onlySizeOrColor=0;
							$("#contentTable tbody tr").each(function(i,j){
								$(j).find("input[type!='']").each(function(){
									if(($(this).attr("name")=='size'&&$(this).val()!='')||($(this).attr("name")=='color'&&$(this).val()!='')){
										onlySizeOrColor=1;
									}
								});
								if(onlySizeOrColor!=1){
									msg="子产品size和color至少需填一项";
									//flag=false;
									return;
								}
								
							}); 
						}
					}
					if(flag){

						top.$.jBox.confirm(msg+' 确定要修改帖子关系吗!','系统提示',function(v,h,f){
							if(v=='ok'){
								loading('正在提交，请稍等...');
								$("#contentTable tbody tr").each(function(i,j){
									$(j).find("select").each(function(){
										if($(this).attr("name")!=undefined){
										    $(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
										}
									});
									$(j).find("input[type!='']").each(function(){
										if($(this).attr("name")&&$(this).css("display")!='none'&&$(this).attr("name")!='compareColor'&&$(this).attr("name")!='compareSize'){
											if($(this).val()!=''){
												if($(this).attr("name")=='childParentSku'){
													$(this).attr("name","items"+"["+i+"].parentSku");
												}else if($(this).attr("name")=='size'&&$(this).val()!=$(this).next().val()){
													$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
												}else if($(this).attr("name")=='color'&&$(this).val()!=$(this).next().val()){
													$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
												}else if($(this).attr("name")=='catalogType1'||$(this).attr("name")=='catalogType2'){
													console.log("==");
													$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
												}else if($(this).attr("name")!='size'&&$(this).attr("name")!='color'){
													$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
												}
											}
										}
										
									});
								});
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
							}
						},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}else{
						top.$.jBox.error(msg,"错误");
					}
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
		
		function queryManually(a){
			if($(a).val!=''){
				$(a).parent().parent().find("input[name='color']").val('');
				$(a).parent().parent().find("input[name='size']").val('');
				$(a).parent().parent().find("input[name='compareColor']").val('');
				$(a).parent().parent().find("input[name='compareSize']").val('');
				$(a).parent().parent().find("input[name='childParentSku']").attr("readonly",true);
				var $this = $(a);
				$.ajax({
      			   type: "POST",
      			   url: "${ctx}/amazoninfo/amazonPortsDetail/getChangeDetail?accountName="+$("#accountName").val()+"&sku="+$(a).val(),
      			   dataType:"json",
      			   async: true,
      			   success: function(msg){
      				  $this.parent().parent().find("input[name='color']").val(msg.color);
      				  $this.parent().parent().find("input[name='size']").val(msg.size);
      				  $this.parent().parent().find("input[name='compareColor']").val(msg.color);
     				  $this.parent().parent().find("input[name='compareSize']").val(msg.size);
      				  if(msg.parentSku=="0"){
      					$this.parent().parent().find("input[name='childParentSku']").val('');
      				  }else if(msg.parentSku=="1"){
      					$this.parent().parent().find("input[name='childParentSku']").attr("readonly",false);  
      				  }else{
      					$this.parent().parent().find("input[name='childParentSku']").val(msg.parentSku); 
      				  }
      			   }
          		});
			}
		}
		function setCatalogTypeValue(c){
			var catalogType=$(c).val();
			var arr=catalogType.split(",");
			$(c).parent().parent().find("[name='catalogType1']").val(arr[0]);
			$(c).parent().parent().find("[name='catalogType2']").val(arr[1]);
			if(arr[0]=='Lighting'||arr[0]=='Wireless'||arr[0]=='Office'||arr[0]=='CE'){
				$(c).parent().parent().find("[name='size']").val('');
				$(c).parent().parent().find("[name='size']").attr("disabled","disabled");
			}else{
				$(c).parent().parent().find("[name='size']").removeAttr('disabled');
			}
		}
	</script>
</head>
<body>
<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li  ><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
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
		<li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/formRelation">修改绑定关系</a></li>
	</ul><br/>
	<form id="inputForm"  action="${ctx}/amazoninfo/amazonPortsDetail/saveRelation" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<input type='hidden' name='state' id='state'/>
		<input type='hidden' name='checkUser.id' id='checkUserId'/>
		
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
		<div class="control-group">
			<label class="control-label">操作:</label>
			<div class="controls">
				<select  name="operat" id="operat" style="width: 220px" class="required">
				    <option value="2" ${'2' eq type?'selected':'' }>绑定</option>
					<option value="1" ${'1' eq type?'selected':'' }>解绑</option>
					<option value="3" ${'3' eq type?'selected':'' }>修改颜色大小</option>
				</select>
			</div>
		</div>	
		<c:if test="${type!='1'&&type!='3' }">
		<div class="control-group" id="banding">
			<label class="control-label">新绑定父Sku:</label>
			<div class="controls">
			    <input type="hidden" id="sizeOrColor" value="${sizeOrColor}">
				<select  name="parentSku1" id="parentSku" style="width: 220px">
				    <c:forEach items="${list}" var="item">
						<option value="${item}">${item}</option>									
				   </c:forEach>
				   <c:if test="${fn:length(list)>0}">
				    <option value="newSku">手动填写Sku</option>
				   </c:if> 
				</select>
				&nbsp;&nbsp;<input name="parentSku2" id="parentSku2"  type="text" style="margin-top:5px;display: none;"/>
				<span id='showMsg'><c:if test="${not empty sizeOrColor }">${'0' eq sizeOrColor?'<b>&nbsp;&nbsp;(没查询到新绑定父sku是按照size还是color绑定产品的)</b>':('1' eq sizeOrColor?'<b>&nbsp;&nbsp;(新绑定父Sku是按size-color绑定的帖)</b>':('2' eq sizeOrColor?'<b>&nbsp;&nbsp;(新绑定父Sku是按size绑定的帖)</b>':'<b>&nbsp;&nbsp;(新绑定父Sku是按color绑定的帖)</b>')) }</c:if></span>
				
				
			</div>
		</div>	
		</c:if>
		<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" id="add-row"><span class="icon-plus"></span>新增</a></div>
		<div class="control-group">
			<label class="control-label">子产品:</label>
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="width: 150px">Product</th>
							<th style="width: 150px">CatalogType</th>
							<th style="width: 90px">Size</th>
							<th style="width: 90px">Color</th>
							<th style="width: 100px">原绑定父Sku</th>
							<th style="width: 10px">Operate</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td class="sku">
							<select style="width: 90%" name="sku" class="required" class='selectName'>
							    <option value="">-请选择产品-</option>
								<c:forEach items="${childList}" var="item">
									<option value="${item.sku}" nameVal='${item.productName }'>${empty item.productName?item.sku:item.productName}[${fns:abbr(item.sku,100)}]</option>									
								</c:forEach>
								<c:if test="${fn:length(childList)>0}">
									<option value="sku">手动填写Sku</option>
								</c:if>
							</select>
							<input name="sku" onblur='queryManually(this)' type="text" style="margin-top:5px;display: none;"/>
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
							<td><input type="text" style="width: 80%" name="size"  /><input type="hidden" name="compareSize" /></td>
							<td><input type="text" style="width: 80%" name="color" /><input type="hidden" name="compareColor" /></td>
							<td><input type="text" style="width: 80%" name="childParentSku"/></td>
							<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>	
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="提  交"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>