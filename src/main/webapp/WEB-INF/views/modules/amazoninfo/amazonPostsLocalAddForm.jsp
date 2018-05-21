<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>新增本地帖</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" type="text/css" />
	<script type="text/javascript" src="${ctxStatic}/x-editable/js/bootstrap-editable.js"></script>
	<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<style  type="text/css">
	    .close-tab {
		    cursor: hand;
		    cursor: pointer;
		    color: #94A6B0;
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
		if(!(top)){
			top = self; 
		}
		var index=1;
		$(document).ready(function() {
			
			
			$("#add-row").click(function(){
				   $("#navTabs .active").removeClass("active");
				   $(".tab-content .active").removeClass("active");
				   var title = '<li class="asinTab" role="presentation" id="tab_' + index + '"><a title="panel_'+index+'" href="#c_' + index + '" aria-controls="' + index + '" role="tab" data-toggle="tab">panel_' + index+'<i class="close-tab icon-remove"></i></a></li>';
				   $("#navTabs").append(title);
				   
				   
				   var html = "";
					 <c:forEach items="${fns:getDictList('product_brand')}" var="dic"> 
					 		html = html+"<option value='${dic.value}' ${dic.value eq 'Inateck' ?'selected':''}>${dic.label}</option>";
					 </c:forEach>
					 
				   var countryHtml="";
				   <c:forEach items="${fns:getDictList('platform')}" var="dic"> 
				     <c:if test="${dic.value ne 'com.unitek'}">
				        countryHtml = countryHtml+"<option value='${dic.value}'>${dic.label}</option>";
				      </c:if>
				   </c:forEach>
				   var skuHtml="";
				   
				   <c:forEach items='${list}' var='temp'>
				      <c:forEach items="${fn:split(temp.sku,',')}" var="single">
				          <c:if test="${!fns:endsWith(single,'LOCAL')&&!fn:contains(fn:toLowerCase(single),'old') }">
					        skuHtml=skuHtml+"<option  skuVal='${single }'  asinVal='${temp.asin}'  value='${single}'>${single}</option>";
					      </c:if>  
				      </c:forEach>
				  </c:forEach>   
				   
				   var cnt="<div role='tabpanel' class='tab-pane' id='c_"+index+"'>";
				   
				   cnt+="<div class='control-group sku'><label class='control-label'><b>Sku:</b></label><div class='controls'>";
				   cnt+="<select  name='selectSku' style='width:25%;'  onchange='queryAllInfo(this)'>";
					cnt+="<option value='' selected='selected'>-请选择Sku-</option>";
				   cnt+=skuHtml+"</select></div></div>";
				   
				   cnt+="<div class='control-group sku' ><label class='control-label'><b>本地Sku:</b></label>";
				   cnt+="<div class='controls'><input  class='required' type='text' onblur='validSku(this)' name='sku'  style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp; <div class='showMsg'></div></div></div>";
		        	
				 /*   cnt+="<div class='control-group type'><label class='control-label'><b>Type:</b></label>";
				   cnt+="<div class='controls'> <select name='isFba' style='width:10%'><option value='1' >FBA帖</option><option value='0' >本地帖</option></select></div></div>";
		        	  */
		        	cnt+="  <input type='hidden' value='0' name='isFba'/>";
					cnt+=" <div class='control-group eanOrAsin' ><label class='control-label'><b>EanOrAsin:</b></label>";
					cnt+="<div class='controls'> <select onchange='changeName(this)'  style='width:10%'><option value='asin' >Asin</option><option value='ean'>Ean</option></select>&nbsp;&nbsp;";
					cnt+="<input  type='text'  name='asin' class='required'  style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>";
               
				    cnt+="<div class='control-group title'><label class='control-label'><b>Title:</b></label>";
					cnt+="<div class='controls'><textarea class='required' name='title'  onkeyup='getLen(this)'  style='width:668.063px;height:108px;'></textarea>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span><div class='showMsg'></div></div></div>";
				      
					cnt+="	 <div class='control-group brand' ><label class='control-label'><b>Brand:</b></label>";
					cnt+="<div class='controls'><select name='brand'  style='width: 222px' class='required'>";
					cnt+=html+"</select>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>";
					   
					cnt+=" <div class='control-group partNumber' ><label class='control-label'><b>MfrPartNumber:</b></label>";
					cnt+=" <div class='controls'><input type='text' class='required' name='partNumber'  style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>";
			        	
					
					cnt+="  <div class='control-group ' ><label class='control-label'><b>Price:</b></label>";
					cnt+="  <div class='controls'><input class='required price' type='text'  name='price'  style='width:100px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>";
					//cnt+="  <div class='control-group' ><label class='control-label'><b>SalePrice:</b></label>";
					//cnt+="  <div class='controls'><input class='required price' type='text'  name='salePrice'  style='width:100px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>";
		        	
					
		        	
		        	$(".tab-content").append(cnt);
		        	$("#c_" + index).find("select").select2();
		        	
		        	
			        $("#tab_" + index).addClass('active');
				    $("#c_" + index).addClass('active');
				   
				    index++;
			});
			
			 $("#navTabs").on("click", ".close-tab", function () {
			        id = $(this).parent("a").attr("aria-controls");
			        closeTab(id);
			 });
			 
			 $("#accountName").change(function(){
					var params = {};
					params.accountName = $(this).val();
					params.addType=8;
					window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?"+$.param(params);
			 });
			 
			
			 $("#inputForm").validate({
					submitHandler: function(form){
						var flag="0";
						var titleAndBrand=true;
				    	$("#tabContent .tab-pane ").each(function(i,j){
				    		    var brand="";
				    		    var title="";
								$(j).find("select").each(function(){
									if($(this).attr("name")){
										flag="1";
										if($(this).attr("name")=='brand'){
											console.log($(this).val());
											brand=$(this).val();
										}
									}
								});
								$(j).find("textarea").each(function(){
									if($(this).attr("name")){
		                                flag="1";
		                                if($(this).attr("name")=='title'){
		                                	title=$(this).val();
		                                }
									}
								});
								$(j).find("input[type!='']").each(function(){
									if($(this).attr("name")&&$(this).css("display")!='none'){
										flag="1";
									}
								});
								console.log(title+"=="+brand);
								if(title.toLowerCase().indexOf(brand.toLowerCase().substring(1))<0){
									titleAndBrand=false;
								}
						});
				    	    console.log(titleAndBrand);
				    		if(flag!="1"){
					    		top.$.jBox.error("保存内容为空","<spring:message code="sys_label_tips_error"/>");
					    	}	
				    		
				    		if(titleAndBrand){
				    			$("#tabContent .tab-pane ").each(function(i,j){
									$(j).find("select").each(function(){
										if($(this).attr("name")){
											$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
										}
									});
									$(j).find("textarea").each(function(){
										if($(this).attr("name")){
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
			    			}else{
			    				var submit = function (v, h, f) {
								    if (v == 'ok'){
								    	$("#tabContent .tab-pane ").each(function(i,j){
											$(j).find("select").each(function(){
												if($(this).attr("name")){
													$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
												}
											});
											$(j).find("textarea").each(function(){
												if($(this).attr("name")){
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
								    }else if (v == 'cancel'){
								    	top.$.jBox.tip(v, 'info');
								    }    
								    return true; //close
								};
								top.$.jBox.confirm("title和brand不一致,确认继续？", "提示", submit); 
			    			}
					},
					errorContainer: "#messageBox",
					errorPlacement: function(error, element) {
						$("#messageBox").text("<spring:message code="sys_label_tips_input_error"/>");
						if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
							error.appendTo(element.parent().parent());
						} else {
							error.insertAfter(element);
						}
					}
				});
			
			
		});

		function getLen(c){
			$(c).parent().find(".showMsg").html("<font color='red'>字符长度："+$(c).val().length+"</font>");
		}
		
		function validSku(c){
			var sku=$(c).val();
			if(sku!=''){
				var partNumber=sku.substring(sku.indexOf("-")+1,sku.lastIndexOf("-"));
				if(new RegExp("new$").test(partNumber.toLowerCase())){
					partNumber=partNumber.substring(0,partNumber.length-3);
				}

				for(var i=0;i<9;i++){
					if(new RegExp("new"+i+"$").test(partNumber.toLowerCase())){
						partNumber=partNumber.substring(0,partNumber.length-4);
					}
				}
				console.log(partNumber);
				if($('#country').val()=='com'){
					partNumber=partNumber+"-US";
				}else if($('#country').val()=='jp'){
					partNumber=partNumber+"-JP";
				}
				$(c).parent().parent().parent().find("[name='partNumber']").val(partNumber);	
			}
		}
		
		var closeTab = function (id) {
			if($("#tab_" + id).prev().attr("class")=="asinTab"){
				 $("#tab_" + id).prev().addClass('active');
				 $("#c_" + id).prev().addClass('active');
			}else{
				 $("#tab_" + id).next().addClass('active');
				 $("#c_" + id).next().addClass('active');
			}
		   
		    $("#tab_" + id).remove();
		    $("#c_" + id).remove();
		};
		
		function changeName(c){
			//var name=$(c).val();
			//$(c).next().children(":first").attr("name",name);
			var name=$(c).val();
			if(name=='ean'){
				$(c).parent().find("input[name='asin']").attr("name","ean");
			}else{
				$(c).parent().find("input[name='ean']").attr("name","asin");
			}
			
		}
		
		function showQuantity(c){
			var type=$(c).val();
			if(type=='0'){
				$(c).parent().find("input[type='text']").show();
			}else{
				$(c).parent().find("input[type='text']").hide();
			}
		}
		
		function queryAllInfo(c){
			var asin=$(c).find("option:selected").attr("asinVal");
			console.log(asin);
			var accountName= $("#accountName").val();
			var country='${amazonPostsDetail.country}';
			var wareHouseId=19;
			var sku=$(c).val();
			
			if(country.indexOf('com')>=0){
				wareHouseId=120;
			}else if(country.indexOf('jp')>=0){
				wareHouseId=147;
			}
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/amazonPortsDetail/getAllContent',  
		        dataType:"json",
		        data : "accountName="+ accountName+"&asin="+asin,  
		        async: true,
		        success : function(msg){
		        	
		        	$(c).parent().parent().parent().find("[name='sku']").val(sku+"LOCAL");
		        	var uri_encoded = html_decode(html_decode(html_decode(html_decode(msg.title))));
		        	//console.log(uri_encoded);
       			   // encoded =htmlDecode(uri_encoded);
       			   // console.log(encoded);
		        	$(c).parent().parent().parent().find("[name='title']").val(uri_encoded);
		        	//$(c).parent().parent().parent().find("[name='title']").val(msg.title);
		        	$(c).parent().parent().parent().find("[name='asin']").val(msg.asin);
		        	//$(c).parent().parent().parent().find("[name='brand']").val(msg.brand);		
		        	$(c).parent().parent().parent().find("[name='partNumber']").val(msg.partNumber);

					if(sku!=''){
						$.ajax({  
					        type : 'POST', 
					        url : '${ctx}/amazoninfo/amazonPortsDetail/getNewQuantity',  
					        dataType:"json",
					        data : "country="+country+"&sku="+sku+"&wareHouseId="+wareHouseId,  
					        async: true,
					        success : function(msg){
					        	$(c).parent().parent().parent().find("[name='quantity']").val(msg);
					        }
						}); 
					}
		        }
			});    
		}
		
		function html_decode(str) 
		{ 
		    var s = ""; 
		    if (str.length == 0) return ""; 
		    s = str.replace(/&amp;/g, "&"); 
		    s = s.replace(/&lt;/g, "<"); 
		    s = s.replace(/&gt;/g, ">"); 
		    s = s.replace(/&nbsp;/g, " "); 
		    s = s.replace(/&#39;/g, "\'"); 
		    s = s.replace(/&quot;/g, "\""); 
		    s = s.replace(/<br\/>/g, "\n"); 
		    return s; 
		} 

		function htmlEncode(str) {
		    var div = document.createElement("div");
		    div.appendChild(document.createTextNode(str));
		    return div.innerHTML;
		}
		function htmlDecode(str) {
		    var div = document.createElement("div");
		    div.innerHTML = str;
		    return div.innerHTML;
		}
	</script>
</head>
<body>
	
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
		  <li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=8">新建本地帖</a></li>
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
		<input  type='hidden'  name='operateType' value='8'/>
		
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
				&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>
				<script type="text/javascript">
					$("option[value='${amazonPostsDetail.accountName}']").attr("selected","selected");				
				</script>
			</div>
		</div>
		
		 <div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" id="add-row"><span class="icon-plus"></span>新增帖子</a></div>
		 
		 <div class="main">
            <div>
                <!-- Nav tabs -->
                <ul class="nav nav-tabs" role="tablist" id="navTabs">
                    <li class='active asinTab' id="tab_0"><a href="#c_0" data-toggle="tab"><b>panel_0</b></a></li>             
                </ul>

                <!-- Tab panes -->
                <div class="tab-content" id="tabContent">
                     <div class="tab-pane active" id="c_0">
                       
                          <div class='control-group sku'><label class='control-label'><b>Sku:</b></label>
							 <div class='controls'>
							        <select  name='selectSku' style="width:25%;"  onchange="queryAllInfo(this)">
							          <option value="" selected="selected">-请选择Sku-</option>
							          <c:forEach items='${list}' var='temp'>
							             <c:forEach items="${fn:split(temp.sku,',')}" var="single">
							               <c:if test="${!fns:endsWith(single,'LOCAL')&&!fn:contains(fn:toLowerCase(single),'old') }">
							                 <option skuVal='${single }'  asinVal='${temp.asin}'  value="${single}">${single}</option>
							               </c:if> 
							             </c:forEach> 
							          </c:forEach>
							      </select>
							 </div>     
						</div>
							
							
                         <div class='control-group sku'><label class='control-label'><b>本地Sku:</b></label>
			        	  <div class='controls'><input  type='text' onblur='validSku(this)' name='sku'  class="required" style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp; <div class='showMsg'></div></div>
			        	 </div>
			        	 
			        	 <div class='control-group sku'><label class='control-label'><b>库存数:</b></label>
			        	  <div class='controls'><input  type='text'  name='quantity'  style='width:200px;'/>&nbsp;&nbsp; </div>
			        	 </div>
			        	
			        	  <input type='hidden' value='0' name='isFba'/>
			        	 <div class='control-group eanOrAsin' ><label class='control-label'><b>EanOrAsin:</b></label>
			        	 <div class='controls'>
			        	      <select onchange='changeName(this)' style="width:10%">
			        	          <option value="asin" >Asin</option>
							      <option value="ean" >Ean</option>
							   </select>&nbsp;&nbsp;
			        	      <input  type='text'  name='asin' class="required" style='width:200px;'/>
			        	      &nbsp;&nbsp;<span style='color:red;display:inline'>*</span>
			        	      &nbsp;&nbsp;</div></div>
                     
                          <div class='control-group title'><label class='control-label'><b>Title:</b></label>
						      <div class='controls'>
						         <textarea class='required' name='title' class="required" onkeyup='getLen(this)'  style='width:668.063px;height:108px;'></textarea>
						         &nbsp;&nbsp;<span style='color:red;display:inline'>*</span>
						         <div class='showMsg'></div>
						       </div>
					      </div>
					      
					      <div class='control-group brand' ><label class='control-label'><b>Brand:</b></label>
			        	 <div class='controls'>
			        	 <select name="brand"  style="width: 222px" class="required">
							<c:forEach items="${fns:getDictList('product_brand')}" var="dic">
								<option value="${dic.value}"${dic.value eq 'Inateck' ?'selected':''}>${dic.label}</option>
							</c:forEach>
						</select>
			        	 &nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>
			        	
			        	 
			        	 <div class='control-group partNumber' ><label class='control-label'><b>MfrPartNumber:</b></label>
			        	 <div class='controls'><input type='text' class='required' name='partNumber'  style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>
			        	 
			        	 
			        	 <div class='control-group ' ><label class='control-label'><b>Price:</b></label>
			        	 <div class='controls'><input class='required price' type='text'  name='price'  style='width:100px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>
			        	<!--  <div class='control-group' ><label class='control-label'><b>SalePrice:</b></label>
			        	 <div class='controls'><input class='required price' type='text'  name='salePrice'  style='width:100px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>
			        	 -->
					       
			        	 
                     </div>   
                </div>
            </div>
        </div>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提  交"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>