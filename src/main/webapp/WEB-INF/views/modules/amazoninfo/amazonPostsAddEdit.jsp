<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊编辑普通帖</title>
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
				   cnt+="<div class='control-group sku' ><label class='control-label'><b>Sku:</b></label>";
				   cnt+="<div class='controls'><input onblur='validSku(this)' class='required' type='text'  name='sku'  style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp; <div class='showMsg'></div></div></div>";
		        	
				 /*   cnt+="<div class='control-group type'><label class='control-label'><b>Type:</b></label>";
				   cnt+="<div class='controls'> <select name='isFba' style='width:10%'><option value='1' >FBA帖</option><option value='0' >本地帖</option></select></div></div>";
		        	  */
		        	cnt+="  <input type='hidden' value='1' name='isFba'/>";
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
					cnt+="  <div class='controls'><input class='price' type='text'  name='price'  style='width:100px;'/>&nbsp;&nbsp;</div></div>";
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
				    cnt+=" <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageLength'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
				    cnt+=" <div class='control-group packageWidth' ><label class='control-label'><b>PackageWidth:</b></label>";
				    cnt+=" <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageWidth'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
				    cnt+=" <div class='control-group packageHeight' ><label class='control-label'><b>PackageHeight:</b></label>";
				    cnt+=" <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageHeight'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
		        	cnt+=" <div class='control-group packageWeight' ><label class='control-label'><b>PackageWeight:</b></label>";
		        	cnt+=" <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageWeight'  style='width:100px;'/><span class='add-on'>pounds</span></div>&nbsp;&nbsp;</div></div>";
		        	
		            cnt+=" <div class='control-group catalog1' ><label class='control-label'><b>RecommendedBrowseNode1:</b></label>";
		        	cnt+=" <div class='controls'><input  type='text' class='number'  name='catalog1'  style='width:200px;'/>&nbsp;&nbsp;</div></div>";
		        	cnt+=" <div class='control-group catalog2' ><label class='control-label'><b>RecommendedBrowseNode2:</b></label>";
		        	cnt+=" <div class='controls'><input type='text' class='number'  name='catalog2'  style='width:200px;'/>&nbsp;&nbsp;</div></div>";
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
					params.accountName = $(this).val();
					params.addType=$(this).val();
					window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?"+$.param(params);
			 });
			 
			 $("#createPostsType").change(function(){
				    var params = {};
					params.accountName = $("#accountName").val();
					params.addType=$(this).val();
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
			if(sku!=''){
				$(c).parent().parent().parent().find("[name='partNumber']").val(sku.substring(sku.indexOf("-")+1,sku.lastIndexOf("-")));	
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
			var name=$(c).val();
			$(c).next().children(":first").attr("name",name);
		}
		
		function showQuantity(c){
			var type=$(c).val();
			if(type=='0'){
				$(c).parent().find("input[type='text']").show();
			}else{
				$(c).parent().find("input[type='text']").hide();
			}
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
		
		 function setCatalogTypeValue(c){
				var catalogType=$(c).val();
				var arr=catalogType.split(",");
				$(c).parent().parent().parent().find("[name='catalogType1']").val(arr[0]);
				$(c).parent().parent().parent().find("[name='catalogType2']").val(arr[1]);
			}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
		 <li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom">编辑${'1' eq amazonPostsFeed.operateType?'普通帖':'复制贴' }</a></li>
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
	<form id="inputForm"  action="${ctx}/amazoninfo/amazonPortsDetail/savePostsChange" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<input  type='hidden'  name='operateType' value='${amazonPostsFeed.operateType }'/>
		<input  type='hidden'  name='id' value='${amazonPostsFeed.id }'/>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
			   <select id="accountName" name="accountName" style="width: 120px" class="required">
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
				</select>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>
				<script type="text/javascript">
					$("option[value='${amazonPostsFeed.accountName}']").attr("selected","selected");				
				</script>
			</div>
		</div>
		
		
		
		 <div class="main">
            <div>
                <!-- Nav tabs -->
              
                <ul class="nav nav-tabs" role="tablist" id="navTabs">
                	<c:forEach items="${amazonPostsFeed.items}" var="feed" varStatus="i">
                      <li class="${i.index==0?'active':''} asinTab" id="tab_${i.index}"><a href="#c_${i.index}" data-toggle="tab" aria-controls='${i.index}'><b>panel_${i.index}</b><i class="close-tab icon-remove"></i></a></li>  
                    </c:forEach>          
                </ul>
                
                
                <!-- Tab panes -->
                <div class="tab-content" id="tabContent">
                      <c:forEach items="${amazonPostsFeed.items}" var="feed" varStatus="i">
                     <div class="tab-pane ${i.index==0?'active':''}" id="c_${i.index}">
                       
                          <div class='control-group'><label class='control-label'><b>CatalogType:</b></label>
				        	  <div class='controls'>
				        	      <input type='hidden' name='catalogType1' value='HomeImprovement'/>
				        	      <input type='hidden' name='catalogType2' value='Electrical'/>
				        	      <select  style='width:25%' onchange='setCatalogTypeValue(this)' class="js-states form-control">
				        	         <c:forEach  items='${catalogMap}' var='catalogMap'>
				        	               <optgroup label="${catalogMap.key }">
				        	                  <c:forEach  items='${catalogMap.value}' var='catalog'>
								                  <option value="${catalogMap.key },${catalog}"  ${catalog eq 'Electrical' ?'selected':''}>${catalog }</option>
								               </c:forEach>
								           </optgroup>
				        	         </c:forEach>
								    
								  </select>
				        	  </div>
				        </div>
				        
			        	 <div class='control-group eanOrAsin' ><label class='control-label'><b>Sku:</b></label>
			        	   <div class='controls'>
			        	      <input  type='text'  name='sku' class="required" style='width:200px;' value='${feed.sku }'/>
			        	      <input type='hidden' value='1' name='isFba'/>
			        	      &nbsp;&nbsp;</div></div>
			        	  
			        	
			        	 <div class='control-group sku'><label class='control-label'><b>Ean:</b></label>
			        	  <div class='controls'>
							     <input  type='text'  name='ean'  style='width:200px;' value='${feed.ean }'/>
							</div>      
			        	 </div>
			        	  
			        	 <div class='control-group sku'><label class='control-label'><b>Asin:</b></label>
			        	  <div class='controls'>
							     <input  type='text'  name='asin'  style='width:200px;' value='${feed.asin }'/>
							</div>      
			        	 </div>
                     
                          <div class='control-group title'><label class='control-label'><b>Title:</b></label>
						      <div class='controls'>
						         <textarea class='required' name='title' class="required" onkeyup='getLen(this)'  style='width:668.063px;height:108px;'>${feed.title }</textarea>
						         <div class='showMsg'></div>
						       </div>
					      </div>
					      
					      <div class='control-group brand' ><label class='control-label'><b>Brand:</b></label>
			        	 <div class='controls'>
			        	 <select name="brand"  style="width: 222px" class="required">
							<c:forEach items="${fns:getDictList('product_brand')}" var="dic">
								<option value="${dic.value}" ${dic.value eq feed.brand ?'selected':''}>${dic.label}</option>
							</c:forEach>
						</select>
			        	 &nbsp;&nbsp;</div></div>
			        	 
			        	 <div class='control-group partNumber' ><label class='control-label'><b>MfrPartNumber:</b></label>
			        	 <div class='controls'><input type='text' value='${feed.partNumber }' class='required' name='partNumber'  style='width:200px;'/>&nbsp;&nbsp;</div></div>
			        	 
			        	 
			        	 <div class='control-group ' ><label class='control-label'><b>Price:</b></label>
			        	 <div class='controls'><input class='required price' type='text' value='${feed.price }' name='price'  style='width:100px;'/>&nbsp;&nbsp;</div></div>
			        	<%--  <div class='control-group' ><label class='control-label'><b>SalePrice:</b></label>
			        	 <div class='controls'><input class='required price' type='text' value='${feed.salePrice }' name='salePrice'  style='width:100px;'/>&nbsp;&nbsp;</div></div>
			        	 --%>
					      
					      <div class='control-group description' ><label class='control-label'><b>Description:</b></label>
						   <div class='controls'><textarea class='destroyArea' id='description${i.index}' name='description'  style='float:left;margin: 0px; width:1000px; height:407px;'>${feed.description }</textarea>
						   &nbsp;&nbsp;<div class='showMsg'></div>
						 </div></div>
						
						
					      <div class='control-group feature1' ><label class='control-label'><b>BulletPoint1:</b></label>
		        		  <div class='controls'><input  type='text' id="feature1${i.index}"  onkeyup='getLen(this)' name='feature1'   style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature2' ><label class='control-label'><b>BulletPoint2:</b></label>
		        		  <div class='controls'><input type='text' id="feature2${i.index}" onkeyup='getLen(this)'  name='feature2' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature3' ><label class='control-label'><b>BulletPoint3:</b></label>
		        		  <div class='controls'><input  type='text' id="feature3${i.index}" onkeyup='getLen(this)'  name='feature3'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature4' ><label class='control-label'><b>BulletPoint4:</b></label>
		        		  <div class='controls'><input  type='text' id="feature4${i.index}" onkeyup='getLen(this)'  name='feature4'   style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature5' ><label class='control-label'><b>BulletPoint5:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)' id="feature5${i.index}"  name='feature5' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
						 
						 <div class='control-group keyword1' ><label class='control-label'><b>SearchTerms1:</b></label>
		        		  <div class='controls'><input  type='text' id="keyword1${i.index}" onkeyup='getLen(this)'  name='keyword1'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword2' ><label class='control-label'><b>SearchTerms2:</b></label>
		        		  <div class='controls'><input  type='text'  id="keyword2${i.index}" onkeyup='getLen(this)'  name='keyword2'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword3' ><label class='control-label'><b>SearchTerms3:</b></label>
		        		  <div class='controls'><input  type='text'  id="keyword3${i.index}" onkeyup='getLen(this)'  name='keyword3'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword4' ><label class='control-label'><b>SearchTerms4:</b></label>
		        		  <div class='controls'><input  type='text'  id="keyword4${i.index}" onkeyup='getLen(this)'  name='keyword4'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword5' ><label class='control-label'><b>SearchTerms5:</b></label>
		        		  <div class='controls'><input  type='text'  id="keyword5${i.index}" onkeyup='getLen(this)'  name='keyword5'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
						
						
						 
			        	 <div class='control-group manufacturer' ><label class='control-label'><b>Manufacturer:</b></label>
			        	 <div class='controls'><input  type='text' name='manufacturer' value='${feed.manufacturer }' style='width:200px;'/>&nbsp;&nbsp;</div></div>
			        	 
						 <div class='control-group packageLength' ><label class='control-label'><b>PackageLength:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  value='${feed.packageLength }' name='packageLength'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>
			        	 <div class='control-group packageWidth' ><label class='control-label'><b>PackageWidth:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text' value='${feed.packageWidth }' name='packageWidth'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>
			        	 <div class='control-group packageHeight' ><label class='control-label'><b>PackageHeight:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text' value='${feed.packageHeight }' name='packageHeight'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>
			        	 <div class='control-group packageWeight' ><label class='control-label'><b>PackageWeight:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  value='${feed.packageWeight }' name='packageWeight'  style='width:100px;'/><span class='add-on'>pounds</span></div>&nbsp;&nbsp;</div></div>
			        	
			        	 <div class='control-group catalog1' ><label class='control-label'><b>RecommendedBrowseNode1:</b></label>
			        	 <div class='controls'><input  type='text' class='number'   value='${feed.catalog1 }' name='catalog1'  style='width:200px;'/>&nbsp;&nbsp;</div></div>
			        	 <div class='control-group catalog2' ><label class='control-label'><b>RecommendedBrowseNode2:</b></label>
			        	 <div class='controls'><input type='text' class='number'  value='${feed.catalog2 }'  name='catalog2'  style='width:200px;'/>&nbsp;&nbsp;</div></div>
			        	 
			        	  <script type="text/javascript">
				        	  var editor =  CKEDITOR.replace("description${i.index}",{height:'440px',width:'1000px',toolbarStartupExpanded:true,startupFocus:false});
							  $("#description${i.index}").data("editor",editor); 
							  editor.on("instanceReady", function () {  
							        this.document.on("keyup", function(e){
							       	  $("#description${i.index}").parent().find(".showMsg").html("<font color='red'>字符长度："+editor.getData().length+"</font>");
							       });  
							  });  
							  var uri_encoded = html_decode(html_decode(html_decode(html_decode("${feed.feature1}"))));
		        			  encoded =htmlDecode(uri_encoded);
							  $("#feature1${i.index}").val(encoded);
							  uri_encoded = html_decode(html_decode(html_decode(html_decode("${feed.feature2}"))));
		        			  encoded =htmlDecode(uri_encoded);
							  $("#feature2${i.index}").val(encoded);
							  uri_encoded = html_decode(html_decode(html_decode(html_decode("${feed.feature3}"))));
		        			  encoded =htmlDecode(uri_encoded);
							  $("#feature3${i.index}").val(encoded);
							  uri_encoded = html_decode(html_decode(html_decode(html_decode("${feed.feature4}"))));
		        			  encoded =htmlDecode(uri_encoded);
							  $("#feature4${i.index}").val(encoded);
							  uri_encoded = html_decode(html_decode(html_decode(html_decode("${feed.feature5}"))));
		        			  encoded =htmlDecode(uri_encoded);
							  $("#feature5${i.index}").val(encoded);
							  
							  uri_encoded = html_decode(html_decode(html_decode(html_decode("${feed.keyword1}"))));
		        			  encoded =htmlDecode(uri_encoded);
							  $("#keyword1${i.index}").val(encoded);
							  uri_encoded = html_decode(html_decode(html_decode(html_decode("${feed.keyword2}"))));
		        			  encoded =htmlDecode(uri_encoded);
							  $("#keyword2${i.index}").val(encoded);
							  uri_encoded = html_decode(html_decode(html_decode(html_decode("${feed.keyword3}"))));
		        			  encoded =htmlDecode(uri_encoded);
							  $("#keyword3${i.index}").val(encoded);
							  uri_encoded = html_decode(html_decode(html_decode(html_decode("${feed.keyword4}"))));
		        			  encoded =htmlDecode(uri_encoded);
							  $("#keyword4${i.index}").val(encoded);
							  uri_encoded = html_decode(html_decode(html_decode(html_decode("${feed.keyword5}"))));
		        			  encoded =htmlDecode(uri_encoded);
							  $("#keyword5${i.index}").val(encoded);
				         </script>    
			        	 
                     </div>  
                     </c:forEach>  
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