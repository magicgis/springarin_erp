<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊新增普通帖</title>
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
				   
				   var cnt="<div role='tabpanel' class='tab-pane' id='c_"+index+"'>";
				 //  cnt+="<div class='control-group sku' ><label class='control-label'><b>Sku:</b></label>";
				 //  cnt+="<div class='controls'><input onblur='validSku(this)' class='required' type='text'  name='sku'  style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp; <div class='showMsg'></div></div></div>";
		        	
					cnt+=" <div class='control-group' ><label class='control-label'><b>Type:</b></label><div class='controls'>";
					cnt+="<select onchange='selectSku(this)' style='width:10%'><option value='0' >手填Sku</option><option value='1' >Cross Sku</option></select>&nbsp;&nbsp;";
					cnt+="<input  type='text'  name='sku' onblur='validSku(this)'  style='width:200px;'/><div style='display:none' class='cross'>";
					cnt+="<select  name='crossCountry' onchange='getCountrySku(this)' style='width: 200px;' disabled ><option value='' selected='selected'>-请选择平台-</option>";
					cnt+=countryHtml+"</select> &nbsp;&nbsp;";
					cnt+="<select  name='sku' style='width: 200px;' disabled><option value='' selected='selected'>-请选择Sku-</option></select> &nbsp;&nbsp;";
					cnt+="<b>Cross Quantity:</b><input  type='text'  name='crossQuantity' style='width:200px;display:none;'/>";
					cnt+="</div>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp; <div class='showMsg'></div></div></div>";
				 
					cnt+=" <div class='control-group eanOrAsin' ><label class='control-label'><b>EanOrAsin:</b></label>";
					cnt+="<div class='controls'> <select onchange='changeName(this)'  style='width:10%'><option value='ean'>Ean</option><option value='asin' >Asin</option></select>&nbsp;&nbsp;";
					cnt+="<input  type='text'  name='ean' class='required'  style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>";
               
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
		        	
					cnt+=" <div class='control-group description' ><label class='control-label'><b>Description:</b></label>";
					cnt+="   <div class='controls'><textarea class='destroyArea' id='description"+index+"' name='description'  style='float:left;margin: 0px; width:1000px; height:407px;'></textarea>";
				    cnt+=" </div></div>";
				    
				    
					cnt+="<div class='control-group feature1' ><label class='control-label'><b>BulletPoint1:</b></label>";
					cnt+=" <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature1'   style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
					cnt+=" <div class='control-group feature2' ><label class='control-label'><b>BulletPoint2:</b></label>";
					cnt+=" <div class='controls'><input type='text' onkeyup='getLen(this)'  name='feature2' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
					cnt+="  <div class='control-group feature3' ><label class='control-label'><b>BulletPoint3:</b></label>";
					cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature3'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
					cnt+="  <div class='control-group feature4' ><label class='control-label'><b>BulletPoint4:</b></label>";
					cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature4'   style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
					 cnt+="  <div class='control-group feature5' ><label class='control-label'><b>BulletPoint5:</b></label>";
					cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature5' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
						
			         
				    cnt+="<div class='control-group keyword1' ><label class='control-label'><b>SearchTerms1:</b></label>";
				    cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword1'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
				    cnt+=" <div class='control-group keyword2' ><label class='control-label'><b>SearchTerms2:</b></label>";
				    cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword2'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
				    cnt+="  <div class='control-group keyword3' ><label class='control-label'><b>SearchTerms3:</b></label>";
				    cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword3'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
				    cnt+="  <div class='control-group keyword4' ><label class='control-label'><b>SearchTerms4:</b></label>";
				    cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword4'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
				    cnt+="  <div class='control-group keyword5' ><label class='control-label'><b>SearchTerms5:</b></label>";
				    cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword5'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
					
				   
				    cnt+=" <div class='control-group manufacturer' ><label class='control-label'><b>Manufacturer:</b></label>";
				    cnt+=" <div class='controls'><input  type='text'  name='manufacturer'  style='width:200px;'/>&nbsp;&nbsp;</div></div>";
				    
				    cnt+=" <div class='control-group packageLength' ><label class='control-label'><b>PackageLength:</b></label>";
				    cnt+=" <div class='controls'><div class='input-prepend input-append'><input class='required price' type='text'  name='packageLength'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
				    cnt+=" <div class='control-group packageWidth' ><label class='control-label'><b>PackageWidth:</b></label>";
				    cnt+=" <div class='controls'><div class='input-prepend input-append'><input class='required price' type='text'  name='packageWidth'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
				    cnt+=" <div class='control-group packageHeight' ><label class='control-label'><b>PackageHeight:</b></label>";
				    cnt+=" <div class='controls'><div class='input-prepend input-append'><input class='required price' type='text'  name='packageHeight'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
		        	cnt+=" <div class='control-group packageWeight' ><label class='control-label'><b>PackageWeight:</b></label>";
		        	cnt+=" <div class='controls'><div class='input-prepend input-append'><input class='required price' type='text'  name='packageWeight'  style='width:100px;'/><span class='add-on'>pounds</span></div>&nbsp;&nbsp;</div></div>";
		        	
		            cnt+=" <div class='control-group catalog1' ><label class='control-label'><b>RecommendedBrowseNode1:</b></label>";
		        	cnt+=" <div class='controls'><input  type='text' class='required number'  name='catalog1'  style='width:200px;'/>&nbsp;&nbsp;</div></div>";
		        	cnt+=" <div class='control-group catalog2' ><label class='control-label'><b>RecommendedBrowseNode2:</b></label>";
		        	cnt+=" <div class='controls'><input type='text' class='required number'  name='catalog2'  style='width:200px;'/>&nbsp;&nbsp;</div></div>";
		        	cnt+="</div>";
		        	
		        	$(".tab-content").append(cnt);
		        	
		        	 var editor =  CKEDITOR.replace("description"+index,{height:'440px',width:'1000px',toolbarStartupExpanded:true,startupFocus:true});
					  $("#description"+index).data("editor",editor); 
					  editor.on("instanceReady", function () {  
					        this.document.on("keyup", function(e){
					       	 $("#description"+index).parent().find(".showMsg").html("<font color='red'>字符长度："+editor.getData().length+"</font>");
					       });  
					  });  
			        $("#tab_" + index).addClass('active');
				    $("#c_" + index).addClass('active');
				   
				    index++;
			});
			
			 $("#navTabs").on("click", ".close-tab", function () {
			        id = $(this).parent("a").attr("aria-controls");
			        closeTab(id);
			 });
			 
			 $("#country").change(function(){
					var params = {};
					params.country = $(this).val();
					window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?"+$.param(params);
			 });
			 
			 
			 $("#inputForm").validate({
					submitHandler: function(form){
						var flag="0";
				    	$("#tabContent .tab-pane ").each(function(i,j){
								$(j).find("select").each(function(){
									if($(this).attr("name")){
										flag="1";
										$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
									}
								});
								$(j).find("textarea").each(function(){
									if($(this).attr("name")){
		                                flag="1";
		                                $(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
		                                
									}
								});
								$(j).find("input[type!='']").each(function(){
									if($(this).attr("name")&&$(this).css("display")!='none'){
										flag="1";
										$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
									}
								});
						});
				    	
				    		if(flag=="1"){
					    		form.submit();
					    	}else{
					    		top.$.jBox.error("保存内容为空","<spring:message code="sys_label_tips_error"/>");
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
			$(c).parent().find(".showMsg").html("");
			var reg=new RegExp("^[A-Za-z0-9]+-[A-Za-z0-9]+-[A-Za-z0-9]+$");
			if(!reg.test(sku)){
				$(c).parent().find(".showMsg").html("<font color='red'>sku不合法!</font>");
				return;
			}
			$(c).parent().parent().parent().find("[name='partNumber']").val(sku.split('-')[1]);	
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
			var name=$(c).val();
			$(c).next().children(":first").attr("name",name);
		}
		
		function getCountrySku(c){
			var country=$(c).val();
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/amazonPortsDetail/getSkuByCountry',  
		        dataType:"json",
		        data : 'country=' +country,  
		        async: false,
		        success : function(msg){ 
		        	$(c).parent().find("[name='sku']").empty();   
		        	var option = "<option value=''>--请选择Sku--</option>";   
		            for(var i=0;i<msg.length;i++){
						option += "<option  value=\"" + msg[i].sku + "\">" + msg[i].sku + "</option>"; 
	                }
		            $(c).parent().find("[name='sku']").append(option);
		            $(c).parent().find("[name='sku']").select2("val","");
		        }
		  }); 
		}
		
		function selectSku(c){
			var type=$(c).val();
			if(type=='1'){
				$(c).parent().find("[name='sku']").hide();
				$(c).parent().find(".cross").show();
				$(c).parent().find(".cross").find("[name='crossCountry']").removeAttr("disabled");
				$(c).parent().find(".cross").find("[name='sku']").removeAttr("disabled");
				$(c).parent().find(".cross").find("[name='crossQuantity']").show();
			}else{
				$(c).parent().find("[name='sku']").show();
				$(c).parent().find(".cross").hide();
				$(c).parent().find(".cross").find("[name='crossCountry']").attr("disabled","disabled");
				$(c).parent().find(".cross").find("[name='sku']").attr("disabled","disabled");
				$(c).parent().find(".cross").find("[name='crossQuantity']").hide();
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
	   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/form">修改帖子信息</a></li>	
	   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/deletePostsForm">删除帖子</a></li>	
	   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addParentsPostFrom">新建父帖</a></li>	
	   <li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom">新建普通帖</a></li>		
	</ul><br/>
	<form id="inputForm"  action="${ctx}/amazoninfo/amazonPortsDetail/savePostsChange" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<input  type='hidden'  name='operateType' value='1'/>
		<c:if test="${'2' eq flag }"><input type="hidden" name="id" value="${amazonPostsFeed.id}" /></c:if>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select id="country" name="country" style="width: 200px" class="required">
					<option value="" selected="selected">-请选择平台-</option>
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek'}">
								<option value="${dic.value}" >${dic.label}</option>
							</c:if>
						</c:forEach>
					</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
						<shiro:hasPermission name="amazoninfo:feedSubmission:de">
							<option value="de" >德国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:com">
							<option value="com" >美国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:fr">
							<option value="fr" >法国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:jp">
							<option value="jp" >日本</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:es">
							<option value="es" >西班牙</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:it">
							<option value="it" >意大利</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:uk">
							<option value="uk" >英国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:ca">
							<option value="ca" >加拿大</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:mx">
							<option value="mx" >墨西哥</option>
						</shiro:hasPermission>
					</shiro:lacksPermission>
				</select>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>
				<script type="text/javascript">
					$("option[value='${amazonPostsDetail.country}']").attr("selected","selected");				
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
                       
                        <div class='control-group' ><label class='control-label'><b>Type:</b></label>
			        	 <div class='controls'>
			        	      <select onchange='selectSku(this)' style="width:10%">
							      <option value="0" >手填Sku</option>
							      <option value="1" >Cross Sku</option>
							   </select>
							   &nbsp;&nbsp;
							     <input  type='text'  name='sku' onblur="validSku(this)" class="required" style='width:200px;'/>
							     <div style='display:none' class='cross'>
							       <select  name="crossCountry" onchange='getCountrySku(this)' style="width: 200px;" disabled >
							       <option value="" selected="selected">-请选择平台-</option>
							       <c:forEach items="${fns:getDictList('platform')}" var="dic">
							         <c:if test="${dic.value ne 'com.unitek'}">
								        <option value="${dic.value}" >${dic.label}</option>
							        </c:if>
						            </c:forEach>
							      </select> &nbsp;&nbsp;
							      <select  name="sku" style="width: 200px;" disabled>
							         <option value="" selected="selected">-请选择Sku-</option>
							      </select> &nbsp;&nbsp;
							      <b>Cross Quantity:</b><input  type='text'  name='crossQuantity' style='width:200px;display:none;'/>
							     </div>
							     
							     &nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp; <div class='showMsg'></div>
							  
			        	 </div>
			        	</div>
                     
                      <!--    <div class='control-group sku' style="display: none;"><label class='control-label'><b>Sku:</b></label>
			        	  <div class='controls'><input  type='text'  name='sku' onblur="validSku(this)" class="required" style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp; <div class='showMsg'></div></div>
			        	 </div>
			        	  -->
			        	 <div class='control-group eanOrAsin' ><label class='control-label'><b>EanOrAsin:</b></label>
			        	 <div class='controls'>
			        	      <select onchange='changeName(this)' style="width:10%">
							      <option value="ean" >Ean</option>
							      <option value="asin" >Asin</option>
							   </select>&nbsp;&nbsp;
			        	      <input  type='text'  name='ean' class="required" style='width:200px;'/>
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
					      
					      <div class='control-group description' ><label class='control-label'><b>Description:</b></label>
						   <div class='controls'><textarea class='destroyArea' id='description0' name='description' class='required' style='float:left;margin: 0px; width:1000px; height:407px;'></textarea>
						 </div></div>
						
						
					      <div class='control-group feature1' ><label class='control-label'><b>BulletPoint1:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature1'   style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature2' ><label class='control-label'><b>BulletPoint2:</b></label>
		        		  <div class='controls'><input type='text' onkeyup='getLen(this)'  name='feature2' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature3' ><label class='control-label'><b>BulletPoint3:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature3'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature4' ><label class='control-label'><b>BulletPoint4:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature4'   style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature5' ><label class='control-label'><b>BulletPoint5:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature5' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
						   
				         
						
						 <div class='control-group keyword1' ><label class='control-label'><b>SearchTerms1:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword1'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword2' ><label class='control-label'><b>SearchTerms2:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword2'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword3' ><label class='control-label'><b>SearchTerms3:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword3'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword4' ><label class='control-label'><b>SearchTerms4:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword4'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword5' ><label class='control-label'><b>SearchTerms5:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword5'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
						
						
						 
			        	 <div class='control-group manufacturer' ><label class='control-label'><b>Manufacturer:</b></label>
			        	 <div class='controls'><input  type='text' name='manufacturer'  style='width:200px;'/>&nbsp;&nbsp;</div></div>
			        	 
						 <div class='control-group packageLength' ><label class='control-label'><b>PackageLength:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageLength'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>
			        	 <div class='control-group packageWidth' ><label class='control-label'><b>PackageWidth:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageWidth'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>
			        	 <div class='control-group packageHeight' ><label class='control-label'><b>PackageHeight:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageHeight'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>
			        	 <div class='control-group packageWeight' ><label class='control-label'><b>PackageWeight:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageWeight'  style='width:100px;'/><span class='add-on'>pounds</span></div>&nbsp;&nbsp;</div></div>
			        	
			        	 <div class='control-group catalog1' ><label class='control-label'><b>RecommendedBrowseNode1:</b></label>
			        	 <div class='controls'><input  type='text' class='number'  name='catalog1'  style='width:200px;'/>&nbsp;&nbsp;</div></div>
			        	 <div class='control-group catalog2' ><label class='control-label'><b>RecommendedBrowseNode2:</b></label>
			        	 <div class='controls'><input type='text' class='number'  name='catalog2'  style='width:200px;'/>&nbsp;&nbsp;</div></div>
			        	 
			        	  <script type="text/javascript">
				        	  var editor =  CKEDITOR.replace("description0",{height:'440px',width:'1000px',toolbarStartupExpanded:true,startupFocus:true});
							  $("#description0").data("editor",editor); 
							  editor.on("instanceReady", function () {  
							        this.document.on("keyup", function(e){
							       	  $("#description0").parent().find(".showMsg").html("<font color='red'>字符长度："+editor.getData().length+"</font>");
							       });  
							  });  
				         </script>    
			        	 
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