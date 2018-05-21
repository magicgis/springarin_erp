<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊新增Cross帖</title>
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
				   <c:forEach items="${list}" var="temp"> 
				        skuHtml = skuHtml+"<option value='${temp.asin}'}>${temp.sku}</option>";
			       </c:forEach>
			       
			       
			       var tempHtml="";
			       <c:forEach items='${allSku }' var='temp'>
			         <c:if test="${fn:contains(temp.sku,'-') }">
			         tempHtml=tempHtml+"<option value='${temp.sku }' eanVal='${temp.ean }' asinVal='${temp.asin }'>${temp.sku }</option>";
			         </c:if>
			       </c:forEach>
			       
				   var cnt="<div role='tabpanel' class='tab-pane' id='c_"+index+"'>";
				   cnt+="<div class='control-group sku' ><label class='control-label'><b>Cross Sku:</b></label>";
				   cnt+="<div class='controls'> <select name='sku' class='required' onchange='getCountrySku(this)'><option value=''>-请选择sku-</option> "+tempHtml+"</select></div></div>";
		        	
				   cnt+=" <div class='control-group eanOrAsin' ><label class='control-label'><b>EanOrAsin:</b></label>";
					cnt+="<div class='controls'> <select name='eanOrAsin' onchange='changeName(this)'  style='width:10%' disabled><option value='ean'>Ean</option><option value='asin' >Asin</option></select>&nbsp;&nbsp;";
					cnt+="<input  type='text'  name='ean' class='required'  style='width:200px;' readonly/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>";
              
				   
				  /*  cnt+="<div class='control-group type'><label class='control-label'><b>Type:</b></label>";
				   cnt+="<div class='controls'> <select name='isFba' style='width:10%'><option value='0' >FBA帖</option></select></div></div>"; */
				   cnt+="<input type='hidden' value='1' name='isFba'/>";
				   
				  cnt+="<div class='control-group sku'><label class='control-label'><b>Sku:</b></label><div class='controls'>";
				  cnt+="<select  class='required' name='copySku' style='width: 300px;' onchange='queryAllInfo(this)'><option value='' selected='selected'>-请选择Sku-</option>";
				  cnt+=skuHtml;
				  cnt+="</select> &nbsp;&nbsp;<span style='color:red;display:inline'>*</span></div> </div>";
		        	 
		        	 
					
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
				    cnt+=" &nbsp;&nbsp;<div class='showMsg'></div></div></div>";
				    
				    
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
		        	
		        	$("#c_" + index).find("select").select2();
		        	
		        	 var editor =  CKEDITOR.replace("description"+index,{height:'440px',width:'1000px',toolbarStartupExpanded:true,startupFocus:false});
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
					params.addType='3';
					params.country = $("#country").val();
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
			
			 
			 $("#createPostsType").change(function(){
				    var params = {};
					params.country = $("#country").val();
					params.addType='3';
					window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?"+$.param(params);
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
			var ean=$(c).find("option:selected").attr("eanVal");
			if(ean==''){
				ean=$(c).find("option:selected").attr("asinVal");
				$(c).parent().parent().parent().find("[name='eanOrAsin']").select2("val","asin");
				if($(c).parent().parent().parent().find("[name='asin']")==null||$(c).parent().parent().parent().find("[name='asin']")==''){
					$(c).parent().parent().parent().find("[name='ean']").attr("name","asin");
					$(c).parent().parent().parent().find("[name='asin']").val(ean);
				}else{
					$(c).parent().parent().parent().find("[name='ean']").val(ean);
				}
			}else{
				$(c).parent().parent().parent().find("[name='eanOrAsin']").select2("val","ean");
				if($(c).parent().parent().parent().find("[name='ean']")==null||$(c).parent().parent().parent().find("[name='ean']")==''){
					$(c).parent().parent().parent().find("[name='asin']").attr("name","ean");
					$(c).parent().parent().parent().find("[name='ean']").val(ean); 
				}else{
					$(c).parent().parent().parent().find("[name='ean']").val(ean); 
				}
				
			}
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/amazonPortsDetail/getSkuByCountryAsin',  
		        dataType:"json",
		        data : "country="+ $('#country').val()+"&asin="+ean,  
		        async: true,
		        success : function(msg){ 
		        	$(c).parent().parent().parent().find("[name='copySku']").empty();   
		        	var option = "<option value=''>--请选择Sku--</option>";   
		            for(var i=0;i<msg.length;i++){
						option += "<option  value=\"" + msg[i].asin + "\">" + msg[i].sku + "</option>"; 
	                }
		            $(c).parent().parent().parent().find("[name='copySku']").append(option);
		            $(c).parent().parent().parent().find("[name='copySku']").select2("val","");
		        }
		  }); 
		}
		
		function queryAllInfo(c){
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/amazonPortsDetail/getAllContent',  
		        dataType:"json",
		        data : "country="+ $('#country').val()+"&asin="+$(c).val(),  
		        async: true,
		        success : function(msg){
		        	$(c).parent().parent().parent().find("[name='title']").val(msg.title);
		        	//$(c).parent().parent().parent().find("[name='ean']").val(msg.ean);
		        	$(c).parent().parent().parent().find("[name='brand']").val(msg.brand);		
		        	$(c).parent().parent().parent().find("[name='partNumber']").val(msg.partNumber);
		        	$(c).parent().parent().parent().find("[name='description']").data("editor").setData(msg.description);
		        	$(c).parent().parent().parent().find("[name='feature1']").val(msg.feature1);
		        	$(c).parent().parent().parent().find("[name='feature2']").val(msg.feature2);
		        	$(c).parent().parent().parent().find("[name='feature3']").val(msg.feature3);
		        	$(c).parent().parent().parent().find("[name='feature4']").val(msg.feature4);
		        	$(c).parent().parent().parent().find("[name='feature5']").val(msg.feature5);
		        	$(c).parent().parent().parent().find("[name='keyword1']").val(msg.keyword1);
		        	$(c).parent().parent().parent().find("[name='keyword2']").val(msg.keyword2);
		        	$(c).parent().parent().parent().find("[name='keyword3']").val(msg.keyword3);
		        	$(c).parent().parent().parent().find("[name='keyword4']").val(msg.keyword4);
		        	$(c).parent().parent().parent().find("[name='keyword5']").val(msg.keyword5);
		        	$(c).parent().parent().parent().find("[name='manufacturer']").val(msg.manufacturer);
		        	$(c).parent().parent().parent().find("[name='packageLength']").val(msg.packageLength);
		        	$(c).parent().parent().parent().find("[name='packageWidth']").val(msg.packageWidth);
		        	$(c).parent().parent().parent().find("[name='packageHeight']").val(msg.packageHeight);
		        	$(c).parent().parent().parent().find("[name='packageWeight']").val(msg.packageWeight);
		        	$(c).parent().parent().parent().find("[name='catalog1']").val(msg.catalog1);
		        	$(c).parent().parent().parent().find("[name='catalog2']").val(msg.catalog2);
		        }
			});    
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>		
	   <li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=3">新建Cross帖</a></li>
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
	</ul><br/>
	<form id="inputForm"  action="${ctx}/amazoninfo/amazonPortsDetail/savePostsChange" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<input  type='hidden'  name='operateType' value='7'/>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select id="country" name="country" style="width: 200px" class="required">
					<option value="" selected="selected">-请选择平台-</option>
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
						<option value="de" >德国</option>
						<option value="fr" >法国</option>
						<option value="es" >西班牙</option>
						<option value="it" >意大利</option>
						<option value="uk" >英国</option>
					</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
						<shiro:hasPermission name="amazoninfo:feedSubmission:de">
							<option value="de" >德国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:fr">
							<option value="fr" >法国</option>
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
						
					</shiro:lacksPermission>
				</select>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>
				<script type="text/javascript">
					$("option[value='${amazonPostsDetail.country}']").attr("selected","selected");				
				</script>
			</div>
		</div>
	  
	 <%--  <div class="control-group">
			<label class="control-label">新增类型:</label>
			<div class="controls">
			      <select id='createPostsType' style="width: 200px">
					<option value="1" ${'1' eq addType?'selected':'' }>普通帖</option>
					<option value="2" ${'2' eq addType?'selected':'' }>复制贴</option>
					<option value="3" ${'3' eq addType?'selected':'' }>Cross帖</option>
				</select>
			</div>
		</div>	 --%>
		
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
                        
                         <div class='control-group sku'><label class='control-label'><b>Cross Sku:</b></label>
			        	  <div class='controls'>
			        	     <select name='sku' class='required' onchange='getCountrySku(this)'>
			        	         <option value=''>-请选择sku-</option>
			        	         <c:forEach items='${allSku }' var='temp'>
			        	            <c:if test="${fn:contains(temp.sku,'-') }">
			        	                <option value='${temp.sku }' eanVal='${temp.ean }' asinVal='${temp.asin }'>${temp.sku }</option>
			        	            </c:if>
			        	         </c:forEach>
			        	     </select>
			        	  </div>
			        	 </div>
			        	  
			        	 <div class='control-group eanOrAsin' ><label class='control-label'><b>EanOrAsin:</b></label>
			        	   <div class='controls'>
			        	      <select name='eanOrAsin' onchange='changeName(this)' style="width:10%" disabled>
							      <option value="ean" >Ean</option>
							      <option value="asin" >Asin</option>
							   </select>&nbsp;&nbsp;
			        	      <input  type='text'  name='ean' class="required" style='width:200px;' readonly/>
			        	      &nbsp;&nbsp;<span style='color:red;display:inline'>*</span>
			        	      &nbsp;&nbsp;</div></div>
			        	      <input type='hidden' value='1' name='isFba'/>
			        	<!--    <div class='control-group type'><label class='control-label'><b>Type:</b></label>
			        	  <div class='controls'>
			        	      <select name='isFba' style="width:10%">
							      <option value="1" >FBA帖</option>
							      <option value="0" >本地帖</option>
							   </select>
			        	  </div>
			        	 </div> -->
			        	  
			        	 
			        	 <div class='control-group sku'><label class='control-label'><b>Sku:</b></label>
			        	  <div class='controls'>
							      <select  name='copySku' style="width: 300px;" onchange="queryAllInfo(this)" class='required'>
							          <option value="" selected="selected">-请选择Sku-</option>
							      </select> &nbsp;&nbsp;<span style='color:red;display:inline'>*</span>
							</div>      
			        	 </div>
			        	  
			        	
                     
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
						   <div class='controls'><textarea class='destroyArea' id='description0' name='description'  style='float:left;margin: 0px; width:1000px; height:407px;'></textarea>
						   &nbsp;&nbsp;<div class='showMsg'></div>
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
				        	  var editor =  CKEDITOR.replace("description0",{height:'440px',width:'1000px',toolbarStartupExpanded:true,startupFocus:false});
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