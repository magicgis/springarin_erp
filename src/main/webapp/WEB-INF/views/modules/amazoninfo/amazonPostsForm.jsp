<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊帖子上架</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
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
		var setting = {check:{enable:true,nocheckInherit:false,chkStyle:'radio',radioType:'all' },view:{selectedMulti:false,fontCss: setHighlight},
				data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
						tree.checkNode(node, !node.checked, true, true);
					    return false;
				},onCheck: zTreeOnCheck}};
		
		var setting2 = {check:{enable:true,nocheckInherit:false,chkStyle:'radio',radioType:'all' },view:{selectedMulti:false,fontCss: setHighlight},
				data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
						tree.checkNode(node, !node.checked, true, true);
					    return false;
				},onCheck: zTreeOnCheck2}};
		
		var nodes1=[];
		var nodes2=[];
		
		$(document).ready(function() {
			$("#dataBtn").keydown(function (e) {
			      var curKey = e.which;
			      if (curKey == 13) {
			         $("#dataBtnSearch").click();
			         return false;
			      }
			});
			$("#dataBtn2").keydown(function (e) {
			      var curKey = e.which;
			      if (curKey == 13) {
			         $("#dataBtnSearch2").click();
			         return false;
			      }
			});
			
			<c:if test="${'1' eq flag}" >
			   $("#asin").select2("val",'${amazonPostsDetail.asin}');
			   var clickSku=$("#asin").find("option:selected").attr("skuValue");
		       if(clickSku.indexOf(",")<0){
		    		  $("#sku").empty();
		    		  var cnt=" <a  title='点击添加帖子修改' class='add-row' onclick=showSku('"+clickSku+"')><span class='icon-plus'></span>"+clickSku+"</a>";
		    		  $("#sku").append(cnt);
		    	}else{
		    		  var singleSku=clickSku.split(",");
		    		  var cnt='<a href="#" class="add-row" title="点击添加帖子修改"  onclick=showSku("'+clickSku+'")><span class="icon-plus"></span>全部Sku</a>&nbsp;&nbsp;';
		    		  for(var i=0;i<singleSku.length;i++){
		    			  cnt+='<a href="#" class="add-row" title="点击添加帖子修改" onclick=showSku("'+singleSku[i]+'")><span class="icon-plus"></span>'+singleSku[i]+'</a>&nbsp;&nbsp;';
		    		  }
		    		  $("#sku").empty();
		    		  $("#sku").append(cnt); 
		    	}
			   showSku(clickSku);
			</c:if>
			
			<c:if test="${'2' eq flag}" >
			    <c:forEach items="${amazonPostsFeed.items}" var="amazonPostsDetail" varStatus="i">
			       $("#asin").select2("val",'${amazonPostsDetail.asin}');
				   var clickSku=$("#asin").find("option:selected").attr("skuValue");
				   
				//   $("#"+id+" .sku").val(clickSku);
				  // $("#"+id+" .asin").val($("#asin").val());
				  // $("#"+id+" .productName").val($("#asin").find("option:selected").text());
				 //  $("#"+id+" .country").val($("#country").val());
				   var tabId="";
			       if(clickSku.indexOf(",")<0){
			    	      tabId=clickSku;
			    		  $("#sku").empty();
			    		  var cnt=" <a  title='点击添加帖子修改' class='add-row' onclick=showSku('"+clickSku+"')><span class='icon-plus'></span>"+clickSku+"</a>";
			    		  $("#sku").append(cnt);
			    	}else{
			    		  tabId=$("#asin").find("option:selected").text().replace(/\s/g,'_')+"_ALL_SKU";
			    		  var singleSku=clickSku.split(",");
			    		  var cnt='<a href="#" class="add-row" title="点击添加帖子修改"  onclick=showSku("'+clickSku+'")><span class="icon-plus"></span>全部Sku</a>&nbsp;&nbsp;';
			    		  for(var i=0;i<singleSku.length;i++){
			    			  cnt+='<a href="#" class="add-row" title="点击添加帖子修改" onclick=showSku("'+singleSku[i]+'")><span class="icon-plus"></span>'+singleSku[i]+'</a>&nbsp;&nbsp;';
			    		  }
			    		  $("#sku").empty();
			    		  $("#sku").append(cnt); 
			    	}
				   showSku("${amazonPostsDetail.sku}");
				   
				    var id = "tab_" + "${amazonPostsDetail.sku}";
				    var selectStr="";
			       <c:if test="${not empty amazonPostsDetail.description}">selectStr+="description,"</c:if>
			       <c:if test="${not empty amazonPostsDetail.feature1}">selectStr+="feature,"</c:if>
			       <c:if test="${not empty amazonPostsDetail.keyword1}">selectStr+="keyword,"</c:if>
			       <c:if test="${not empty amazonPostsDetail.brand}">selectStr+="brand,"</c:if>
				   <c:if test="${not empty amazonPostsDetail.manufacturer}">selectStr+="manufacturer,"</c:if>
				   <c:if test="${not empty amazonPostsDetail.packageLength}">selectStr+="packageDimensions,"</c:if>
				   <c:if test="${not empty amazonPostsDetail.partNumber}">selectStr+="partNumber,"</c:if>
				   <c:if test="${not empty amazonPostsDetail.catalog1}">selectStr+="catalog,"</c:if>
				   if(selectStr!=''){
					    var arr=selectStr.split(",");
					    for(var k=0;k<arr.length-1;k++){
						    var showName="";
						    if(arr[k]=='description'){
						    	showName="Description";
						    }else if(arr[k]=='feature'){
						    	showName="BulletPoint";
						    }else if(arr[k]=='keyword'){
						    	showName="SearchTerms";
						    }else if(arr[k]=='brand'){
						    	showName="Brand";
						    }else if(arr[k]=='manufacturer'){
						    	showName="Manufacturer";
						    }else if(arr[k]=='packageDimensions'){
						    	showName="PackageDimensions";
						    }else if(arr[k]=='partNumber'){
						    	showName="MfrPartNumber";
						    }else if(arr[k]=='catalog'){
						    	showName="RecommendedBrowseNode";
						    }
						    
						    editAdd(id,showName,arr[k],'${amazonPostsDetail.country}',$("#asin").val());
					    }
				   }
				    
			    </c:forEach>
			</c:if>
			
			$("#add-row").click(function () {
		        addTabs({id: $("#asin").val(), title:$("#asin").find("option:selected").text(),channel:'${amazonPostsDetail.country}', close: true});
		    });
		
		    
		    $("#navTabs").on("click", ".close-tab", function () {
		        id = $(this).parent("a").attr("aria-controls");
		        closeTab(id);
		    });
		    
		    $("#accountName").change(function(){    
		    	$("#asin").empty();  
		    	if($("#accountName").val()!=""){
		    		$.ajax({  
				        type : 'POST', 
				        url : '${ctx}/amazoninfo/amazonPortsDetail/getAsinByCountry1',  
				        dataType:"json",
				        data : "accountName="+$("#accountName").val(),  
				        async: true,
				        success : function(msg){ 
				        	
				        	var option = "";  
				            for(var i=0;i<msg.length;i++){
	 							option += "<option  value=\"" + msg[i].asin + "\"  skuValue=\"" + msg[i].sku + "\">" + msg[i].productName+ "</option>"; 
	 	                    }
				            $("#asin").append(option);
				            $("#asin").select2("val",msg[0].asin);
				           
							$("#tabContent .tab-pane .addType").each(function(i,j){
								$(j).find("textarea").each(function(){
									if($(this).attr("name")=='description'){
										$(this).data("editor").destroy();
										$(this).remove();
									}
								});
								
						   });
				            $("#navTabs").empty();
				            $("#tabContent").empty();
				            $("#asin").change();
				        }
				  });
		    	}else{
		    		$("#navTabs").empty();
		            $("#tabContent").empty();
		    		$("#sku").empty();
		    		$("#asin").select2('val','');
		    	}
				   
				  
	    	  }); 
		    
		    $("#asin").change(function(){
		    	  var skuValue=$("#asin").find("option:selected").attr("skuValue");
		    	  console.log(skuValue);
		    	  // unescape() 对 escape() .replace(/\s/g,'_')
		    	  console.log(escape(skuValue));
		    	  if(skuValue.indexOf(",")<0){
		    		  $("#sku").empty();
		    		  var cnt=" <a  title='点击添加帖子修改' class='add-row' onclick=showSku('"+skuValue.replace(/\s/g,"RRR")+"')><span class='icon-plus'></span>"+skuValue+"</a>";
		    		  $("#sku").append(cnt);
		    	  }else{
		    		  var singleSku=skuValue.split(",");
		    		  var cnt='<a href="#" class="add-row" title="点击添加帖子修改"  onclick=showSku("'+skuValue.replace(/\s/g,"RRR")+'")><span class="icon-plus"></span>全部Sku</a>&nbsp;&nbsp;';
		    		  for(var i=0;i<singleSku.length;i++){
		    			  cnt+='<a href="#" class="add-row" title="点击添加帖子修改" onclick=showSku("'+singleSku[i].replace(/\s/g,"RRR")+'")><span class="icon-plus"></span>'+singleSku[i]+'</a>&nbsp;&nbsp;';
		    		  }
		    		  $("#sku").empty();
		    		  $("#sku").append(cnt); 
		    	  }
		    });
		    
		/*     $('#navTabs a').click(function (e) {  
	            e.preventDefault();
	            $(this).tab('show');
	        });
	        
		    $("#btnSubmit").click(function(){
		    	
		    });$("#tabContent").on('input',function(e){  
		    	$(this).parent().find(".showMsg").html("字符长度："+$(this).val().length);
		    }); 
		    */
		    
		    
		    
           $("#inputForm").validate({
				
				submitHandler: function(form){
					var flag="0";
					$("#tabContent .tab-pane .addType").each(function(i,j){
						$(j).find("select").each(function(){
							if($(this).attr("name")){
								flag="1";
							}
						});
						$(j).find("textarea").each(function(){
							if($(this).attr("name")){
                                flag="1";
							}
						});
						$(j).find("input[type!='']").each(function(){
							if($(this).attr("name")){
								flag="1";
							}
						});
				    });
					if(flag=="1"){
						$("#tabContent .tab-pane .addType").each(function(i,j){
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
								if($(this).attr("name")){
									$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
								}
							});
					    });
						$("#btnSubmit").attr("disabled","disabled");
			    		form.submit();
			    		$("#btnSubmit").removeAttr("disabled");
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
		
		
		var addTabs = function (obj) {
		    id = "tab_" + obj.id;
		    $("#navTabs .active").removeClass("active");
		    $(".tab-content .active").removeClass("active");

		    if(obj.skuVal.indexOf(",")<0){
		    	//var compTitle=obj.title.replace(" ","_")+"_ALL_SKU";
		    	var compTitle=obj.title.replace(/\s/g,'_')+"_ALL_SKU";
		    	if (!$("#" + id)[0]&&!$("#tab_" + compTitle)[0]) {//title 必需
			        mainHeight = $(document.body).height();
			        title = '<li class="asinTab" role="presentation" id="tab_' + id + '"><a title="'+obj.id+'" href="#' + id + '" aria-controls="' + id + '" role="tab" data-toggle="tab">' + obj.id+'<i class="close-tab icon-remove"></i></a></li>';
			        var content = '<div role="tabpanel" class="tab-pane" id="' + id + '">';
			        

			        
			        
			        content+='<div class="control-group"><div class="controls"><select onchange=addContent("'+id+'") class="selectType"><option value="">--请选择修改内容--</option><option value="all">--All--</option><option value="description">Description</option><option value="feature">BulletPoint</option><option value="keyword">SearchTerms</option> ';
			        content+='<option value="brand">Brand</option><option value="manufacturer">Manufacturer</option> ';
			        content+='<option value="packageDimensions">PackageDimensions</option>';
			        content+='<option value="partNumber">MfrPartNumber</option><option value="catalog">RecommendedBrowseNode</option><option value="price">Price</option>';
			        content+='</select></div></div><br/><br/><div class="addType"><input name="sku" class="sku" type="hidden" value="'+obj.skuVal+'" /><input name="asin" class="asin" type="hidden" value="'+obj.asinVal+'" /><input name="productName"  class="productName" type="hidden" value="'+obj.title+'"/><input name="country" class="country" type="hidden" value="'+obj.channel+'"/>';
			       
			        content+='<div class="control-group"><label class="control-label"><b>帖子优化:</b></label><div class="controls"><select name="studio">';
			        content+='<option value="1">否</option><option value="0">是</option>';
			        content+='</select></div></div>';
			        
			        
			        content+='<div class="control-group title"><label class="control-label"><b>Title:</b></label>';
			        content+='<div class="controls"><textarea class="required"  name="title" onkeyup="getLen(this)" style="width:668.063px;height:108px;">'+obj.objTitle+'</textarea><div class="showMsg"></div></div></div>';
			       
			       
			        content+='</div>';
			        content+='</div>';
			        $("#navTabs").append(title);
			        $(".tab-content").append(content);
			        $("#tab_" + id).addClass('active');
				    $("#" + id).addClass('active');
			    }else if(!$("#tab_" + compTitle)[0]==false){
			    	 $("#tab_tab_" + compTitle).addClass('active');
					 $("#tab_" + compTitle).addClass('active');
			    }else{
			    	 $("#tab_" + id).addClass('active');
					 $("#" + id).addClass('active');
			    }
		    }else{
		    	var arr=obj.skuVal.split(",");
		    	for(var i=0;i<arr.length;i++){
		    		if(!$("#tab_" + arr[i])[0]==false){
		    			 $("#tab_tab_" + arr[i]).remove();
		    			 $("#tab_" + arr[i]).remove();
		    		}
		    	}
		    	if (!$("#" + id)[0]) {//title 必需
			        mainHeight = $(document.body).height();
			        title = '<li class="asinTab" role="presentation" id="tab_' + id + '"><a name="studio" title="'+obj.id+'" href="#' + id + '" aria-controls="' + id + '" role="tab" data-toggle="tab">' + obj.id+'<i class="close-tab icon-remove"></i></a></li>';
			        var content = '<div role="tabpanel" class="tab-pane" id="' + id + '">';
			        
			       
			        
			        content+='<div class="control-group"><div class="controls"><select onchange=addContent("'+id+'") class="selectType"><option value="">--请选择修改内容--</option><option value="all">--All--</option><option value="description">Description</option><option value="feature">BulletPoint</option><option value="keyword">SearchTerms</option> ';
			        content+='<option value="brand">Brand</option><option value="manufacturer">Manufacturer</option> ';
			        content+='<option value="packageDimensions">PackageDimensions</option>';
			        content+='<option value="partNumber">MfrPartNumber</option><option value="catalog">RecommendedBrowseNode</option><option value="price">Price</option>';
			        content+='</select></div></div><br/><br/><div class="addType"><input name="sku" type="hidden" value="'+obj.skuVal+'" /><input name="asin" class="asin" type="hidden" value="'+obj.asinVal+'" /><input name="productName"  type="hidden" value="'+obj.title+'"/><input name="country" class="country" type="hidden" value="'+obj.channel+'"/>';
			       
			        content+='<div class="control-group"><label class="control-label"><b>帖子优化:</b></label><div class="controls"><select name="studio">';
			        content+='<option value="1">否</option><option value="0">是</option>';
			        content+='</select></div></div>';
			        
			        content+='<div class="control-group title"><label class="control-label"><b>Title:</b></label>';
			        content+='<div class="controls"><textarea class="required"   onkeyup="getLen(this)" name="title"  style="width:668.063px;height:108px;">'+obj.objTitle+'</textarea><div class="showMsg"></div></div></div>';
			       
			     
			        
			        content+='</div>';
			        content+='</div>';
			        
			        $("#navTabs").append(title);
			        $(".tab-content").append(content);
			    }
		    	$("#tab_" + id).addClass('active');
			    $("#" + id).addClass('active');
		    }
		     
		   
		};

		var closeTab = function (id) {
			if($("#tab_" + id).prev().attr("class")=="asinTab"){
				 $("#tab_" + id).prev().addClass('active');
				 $("#" + id).prev().addClass('active');
			}else{
				 $("#tab_" + id).next().addClass('active');
				 $("#" + id).next().addClass('active');
			}
		   
		    $("#tab_" + id).remove();
		    $("#" + id).remove();
		};

		function addContent(id){
			var name=$("#"+id+" .selectType").val();
			if(name=="all"){
				var skuVal=$("#"+id+" .sku").val();
				var asinVal=$("#"+id+" .asin").val();
				var nameVal=$("#"+id+" .productName").val();
				var countryVal=$("#"+id+" .country").val();
				
				var temp="<input name='sku' type='hidden' class='sku' value='"+skuVal+"'/><input name='asin' class='asin' type='hidden' value='"+asinVal+"'/>";
				temp+="<input name='productName' value='"+nameVal+"' type='hidden' class='productName'/><input name='country' value='"+countryVal+"' class='country' type='hidden' /> ";
				
				var editor=$("#description"+id).data("editor");
				if(editor){
					editor.destroy();
					$("#"+id+" .addType .description").remove();
				}
				
				$("#"+id+" .addType" ).empty();
				$("#"+id+" .addType").append(temp); 
				
				$.ajax({  
			        type : 'POST', 
			        url : '${ctx}/amazoninfo/amazonPortsDetail/getAllContent',  
			        dataType:"json",
			        data : "accountName="+$("#accountName").val()+"&asin="+$("#"+id+" .asin").val(),  
			        async: true,
			        success : function(msg){
			        	  var cnt="<div class='control-group title'><label class='control-label'><b>Title:</b></label>";
					      cnt+="<div class='controls'><textarea class='required' name='title'  onkeyup='getLen(this)'  style='width:668.063px;height:108px;'>"+msg.title+"</textarea><div class='showMsg'></div></div></div>";
				          
					      cnt+="<div style='width:900px;border:1px solid #EE3B3B' class='feature'><div class='control-group feature1' ><label class='control-label'><b>BulletPoint1:</b></label>";
		        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' value='"+msg.feature1+"' name='feature1'  class='required' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','feature1')><span class='icon-minus'></span></a><div class='showMsg'></div></div></div>";
		        		  cnt+="<div class='control-group feature2' ><label class='control-label'><b>BulletPoint2:</b></label>";
		        		  cnt+="<div class='controls'><input type='text' onkeyup='getLen(this)' value='"+msg.feature2+"' name='feature2'  class='required' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
		        		  cnt+="<div class='control-group feature3' ><label class='control-label'><b>BulletPoint3:</b></label>";
		        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' value='"+msg.feature3+"' name='feature3'  class='required' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
		        		  cnt+="<div class='control-group feature4' ><label class='control-label'><b>BulletPoint4:</b></label>";
		        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' value='"+msg.feature4+"' name='feature4'  class='required' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
		        		  cnt+="<div class='control-group feature5' ><label class='control-label'><b>BulletPoint5:</b></label>";
		        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' value='"+msg.feature5+"' name='feature5' class='required' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div></div>";
						   
		        		  cnt+="<br/><div style='width:900px;border:1px solid #EE3B3B' class='keyword'><div class='control-group keyword1' ><label class='control-label'><b>SearchTerms1:</b></label>";
		        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' value='"+msg.keyword1+"' name='keyword1' class='required' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','keyword1')><span class='icon-minus'></span></a><div class='showMsg'></div></div></div>";
		        		  cnt+="<div class='control-group keyword2' ><label class='control-label'><b>SearchTerms2:</b></label>";
		        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' value='"+msg.keyword2+"' name='keyword2' class='required' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
		        		  cnt+="<div class='control-group keyword3' ><label class='control-label'><b>SearchTerms3:</b></label>";
		        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' value='"+msg.keyword3+"' name='keyword3' class='required' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
		        		  cnt+="<div class='control-group keyword4' ><label class='control-label'><b>SearchTerms4:</b></label>";
		        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' value='"+msg.keyword4+"' name='keyword4' class='required' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
		        		  cnt+="<div class='control-group keyword5' ><label class='control-label'><b>SearchTerms5:</b></label>";
		        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' value='"+msg.keyword5+"' name='keyword5' class='required' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div></div>";
						
		        		  cnt+="<br/><div class='control-group description' ><label class='control-label'><b>Description:</b></label>";
						  cnt+="<div class='controls'><textarea class='destroyArea' id=description"+id+" name='description' class='required' style='float:left;margin: 0px; width:90%; height:407px;'>"+msg.description+"</textarea>&nbsp;&nbsp;<div class='showMsg'></div>";
						  cnt+="<a  class='remove-row' onclick=removecontent('"+id+"','description')><span class='icon-minus' style='float:right;'></span></a></div></div>";
						
						  cnt+="<div style='width:900px;border:1px solid #EE3B3B' class='package'><div class='control-group packageLength' ><label class='control-label'><b>PackageLength:</b></label>";
			        	  cnt+="<div class='controls'><div class='input-prepend input-append'><input class='required price' type='text' value='"+msg.packageLength+"' name='packageLength'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','packageLength')><span class='icon-minus'></span></a></div></div>";
			        	  cnt+="<div class='control-group packageWidth' ><label class='control-label'><b>PackageWidth:</b></label>";
			        	  cnt+="<div class='controls'><div class='input-prepend input-append'><input class='required price' type='text' value='"+msg.packageWidth+"' name='packageWidth'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
			        	  cnt+="<div class='control-group packageHeight' ><label class='control-label'><b>PackageHeight:</b></label>";
			        	  cnt+="<div class='controls'><div class='input-prepend input-append'><input class='required price' type='text' value='"+msg.packageHeight+"' name='packageHeight'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
			        	  cnt+="<div class='control-group packageWeight' ><label class='control-label'><b>PackageWeight:</b></label>";
			        	  cnt+="<div class='controls'><div class='input-prepend input-append'><input class='required price' type='text' value='"+msg.packageWeight+"' name='packageWeight'  style='width:100px;'/><span class='add-on'>pounds</span></div>&nbsp;&nbsp;</div></div></div>";
			        	
			        	  cnt+="<br/><div class='control-group brand' ><label class='control-label'><b>Brand:</b></label>";
			        	  cnt+="<div class='controls'><input  type='text' value='"+msg.brand+"' name='brand'  style='width:200px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','brand')><span class='icon-minus'></span></a></div></div>";
			        	  cnt+="<div class='control-group manufacturer' ><label class='control-label'><b>Manufacturer:</b></label>";
			        	  cnt+="<div class='controls'><input  type='text' value='"+msg.manufacturer+"' name='manufacturer'  style='width:200px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','manufacturer')><span class='icon-minus'></span></a></div></div>";
			        	  cnt+="<div class='control-group partNumber' ><label class='control-label'><b>MfrPartNumber:</b></label>";
			        	  cnt+="<div class='controls'><input type='text' value='"+msg.partNumber+"' class='required' name='partNumber'  style='width:200px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','partNumber')><span class='icon-minus'></span></a></div></div>";
			        	  
			        	 
				        	  cnt+=" <div class='control-group catalog1' ><label class='control-label'><b>RecommendedBrowseNode1:</b></label>";
	                          cnt+="<div class='controls'><input  type='text' class='required'  name='catalog1'  id='catalog1"+id+"'  style='width:200px;' />&nbsp;&nbsp;<input type='button' onclick=openTree('catalog1"+id+"','"+countryVal+"','"+id+"') value='目录搜索'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','catalog1')><span class='icon-minus'></span></a></div></div>";
						       
				        	 if(countryVal.indexOf('com')<0){
						        cnt+=" <div class='control-group catalog2' ><label class='control-label'><b>RecommendedBrowseNode2:</b></label>";
						        cnt+="<div class='controls'><input  type='text' class='required'  name='catalog2'  id='catalog2"+id+"'  style='width:200px;' />&nbsp;&nbsp;<input type='button' onclick=openTree2('catalog2"+id+"','"+countryVal+"','"+id+"') value='目录搜索'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','catalog2')><span class='icon-minus'></span></a></div></div>";
							     
				        	 }
			        	 
			        	  cnt+="<div style='width:900px;border:1px solid #EE3B3B' class='salePrice'><div class='control-group price' ><label class='control-label'><b>Price:</b></label>";
			        	  cnt+="<div class='controls'><div class='input-prepend input-append'><input class='required price' type='text'  name='price'  style='width:100px;'/></div>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','salePrice')><span class='icon-minus'></span></a></div></div>";
			        	 // cnt+="<div class='control-group ' ><label class='control-label'><b>SalePrice:</b></label>";
			        	//  cnt+="<div class='controls'><div class='input-prepend input-append'><input class='required price' type='text'  name='salePrice'  style='width:100px;'/></div>&nbsp;&nbsp;</div></div>";
			        	  cnt+="<div class='control-group ' ><label class='control-label'><b>Reason:</b></label>";
			        	  cnt+="<div class='controls'><div class='input-prepend input-append'><select  name='reason' style='width: 220px' class='required'><option value='计划改价'>计划调价</option><option value='汇率改价'>汇率改价</option><option value='防御性降价'>防御性降价</option><option value='积压降价'>积压降价</option><option value='断货升价'>断货升价</option><option value='促销调价'>促销调价</option><option value='包邮调价'>包邮调价(15分钟自动改回原价)</option><option value='其它'>其它</option></select></div>&nbsp;&nbsp;</div></div></div>";
			        	  
			        	  
			        	  $("#"+id+" .addType").append(cnt);  
	                      var editor =  CKEDITOR.replace("description"+id,{height:'440px',width:'90%',toolbarStartupExpanded:true,startupFocus:true});
						  $("#description"+id).data("editor",editor); 
						  editor.on("instanceReady", function () {  
						        this.document.on("keyup", function(e){
						       	 $("#description"+id).parent().find(".showMsg").html("<font color='red'>字符长度："+editor.getData().length+"</font>");
						       });  
						  });  
			        }
			  });
			}else{
				var showName=$("#"+id+" .selectType").find("option:selected").text();
				if(name!=''){
					var obj="";
					if(name=="feature"||name=="keyword"){
						obj=$("#"+id+" .addType").find("[name='"+name+"1']");
					}else if(name=="packageDimensions"){
						obj=$("#"+id+" .addType").find("[name='packageLength']");
					}else{
						obj=$("#"+id+" .addType").find("[name='"+name+"']");
					}
					if(obj.length == 0&&name!='price'){
						$.ajax({  
					        type : 'POST', 
					        url : '${ctx}/amazoninfo/amazonPortsDetail/getContent',  
					        dataType:"json",
					        data : "accountName="+$("#accountName").val()+"&asin="+$("#"+id+" .asin").val()+"&name="+name,  
					        async: true,
					        success : function(msg){
					        	if(name=='description'){
						        	  var cnt="<br/><div class='control-group "+name+"' ><label class='control-label'><b>"+showName+":</b></label>";
									  cnt+="<div class='controls'><textarea class='destroyArea'  id="+name+id+" name="+name+" style='float: left;margin: 0px; width:90%; height:407px;'>"+msg[0]+"</textarea>";
									  cnt+="<a class='remove-row' onclick=removecontent('"+id+"','"+name+"')><span class='icon-minus' style='float:right;vertical-align:middle;'></span></a><div class='showMsg'></div></div></div>";
									  $("#"+id+" .addType").append(cnt);
									  var editor = CKEDITOR.replace(name+id,{height:'440px',width:'90%',toolbarStartupExpanded:true,startupFocus:true});
									   editor.on("instanceReady", function () {  
									        this.document.on("keyup", function(e){
									        	$("#"+name+id).parent().find(".showMsg").html("<font color='red'>字符长度："+editor.getData().length+"</font>");
									        });  
									  }); 
									  $("#"+name+id).data("editor",editor);
					        	}else if(name=="feature"){
					        		  var cnt="<br/><div style='width:900px;border:1px solid #EE3B3B' class='"+name+"'>";
						        	  for(var i=1;i<=5;i++){
						        		  cnt+="<div class='control-group "+name+i+"' ><label class='control-label'><b>"+showName+i+":</b></label>";
						        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' class='required'  name="+name+i+"  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;";
										  if(i==1){
											  cnt+="<a class='remove-row' onclick=removecontent('"+id+"','"+name+i+"');><span class='icon-minus'></span></a>";
										  }
						        		  cnt+="<div class='showMsg'></div></div></div>";
						        	  }
					        		  cnt+="</div>";
					        		  $("#"+id+" .addType").append(cnt);
					        		  for(var i=1;i<=5;i++){
					        			
					        				  var uri_encoded = html_decode(html_decode(html_decode(html_decode(msg[i-1]))));
						        			  //var encoded=uri_encoded.replace(/%([^\d].)/, "%25$1");
						        			   console.log(uri_encoded);
						        			  encoded =htmlDecode(uri_encoded);
						        			   console.log(encoded);
						        			   console.log(html_decode(encoded));
						        			  $("#"+id+" .addType ."+name+i).find("[name='"+name+i+"']").val(html_decode(encoded));
					        			
					        		  }
						         }else if(name=="keyword"){
					        		  var cnt="<br/><div style='width:1000px;border:1px solid #EE3B3B' class='"+name+"'>";
						        	  for(var i=1;i<=5;i++){
						        		  cnt+="<div class='control-group "+name+i+"' ><label class='control-label'><b>"+showName+i+":</b></label>";
						        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' class='required'  name="+name+i+"  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;";
										  cnt+="<a class='btn' onclick=validKeyword('"+$("#"+id+" .country").val()+"','"+$("#"+id+" .asin").val()+"','"+id+"','"+name+i+"');>检查</a>";
										  if(i==1){
											  cnt+="&nbsp;&nbsp;<a class='remove-row' onclick=removecontent('"+id+"','"+name+i+"');><span class='icon-minus'></span></a>";
										  }
										  cnt+="<div class='showMsg'></div></div></div>";
						        	  }
					        		  cnt+="</div>";
					        		  $("#"+id+" .addType").append(cnt);
					        		  for(var i=1;i<=5;i++){
					        			
					        				  var uri_encoded = html_decode(html_decode(html_decode(html_decode(msg[i-1]))));
						        			  //var encoded=uri_encoded.replace(/%([^\d].)/, "%25$1");
						        			   console.log(uri_encoded);
						        			  encoded =htmlDecode(uri_encoded);
						        			   console.log(encoded);
						        			   console.log(html_decode(encoded));
						        			  $("#"+id+" .addType ."+name+i).find("[name='"+name+i+"']").val(html_decode(encoded));
					        			
					        		  }
						         }else if(name=="catalog"){
						        	 var cnt="";
						        	 var tempCountry=$("#"+id+" .country").val();
						        	 cnt+=" <div class='control-group catalog1' ><label class='control-label'><b>RecommendedBrowseNode1:</b></label>";
			                          cnt+="<div class='controls'><input  type='text' class='required'  name='catalog1'  id='catalog1"+id+"'  style='width:200px;' />&nbsp;&nbsp;<input type='button' onclick=openTree('catalog1"+id+"','"+tempCountry+"','"+id+"') value='目录搜索'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','catalog1')><span class='icon-minus'></span></a></div></div>";
								       
						        	 if(tempCountry.indexOf('com')<0){
								        cnt+=" <div class='control-group catalog2' ><label class='control-label'><b>RecommendedBrowseNode2:</b></label>";
								        cnt+="<div class='controls'><input  type='text' class='required'  name='catalog2'  id='catalog2"+id+"'  style='width:200px;' />&nbsp;&nbsp;<input type='button' onclick=openTree2('catalog2"+id+"','"+tempCountry+"','"+id+"') value='目录搜索'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','catalog2')><span class='icon-minus'></span></a></div></div>";
									     
						        	 }
						        	 $("#"+id+" .addType").append(cnt);
						         }else if(name=="packageDimensions"){
						        	 var cnt="<br/><div style='width:900px;border:1px solid #EE3B3B' class='package'><div class='control-group packageLength' ><label class='control-label'><b>PackageLength:</b></label>";
					        		 cnt+="<div class='controls'><div class='input-prepend input-append'><input type='text' class='required price' value='"+msg[0]+"' name='packageLength'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','packageLength')><span class='icon-minus'></span></a></div></div>";
					        		 
					        		 cnt+="<div class='control-group packageWidth' ><label class='control-label'><b>PackageWidth:</b></label>";
					        		 cnt+="<div class='controls'><div class='input-prepend input-append'><input type='text' class='required price' value='"+msg[1]+"' name='packageWidth'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
					        		
					        		 cnt+="<div class='control-group packageHeight' ><label class='control-label'><b>PackageHeight:</b></label>";
					        		 cnt+="<div class='controls'><div class='input-prepend input-append'><input type='text' class='required price' value='"+msg[2]+"' name='packageHeight'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
					        		 
					        		 cnt+="<div class='control-group packageWeight' ><label class='control-label'><b>PackageWeight:</b></label>";
					        		 cnt+="<div class='controls'><div class='input-prepend input-append'><input type='text' class='required price' value='"+msg[3]+"' name='packageWeight'  style='width:100px;'/><span class='add-on'>pounds</span></div>&nbsp;&nbsp;</div></div></div>";
					        		
					        		 $("#"+id+" .addType").append(cnt);
						         }else{
						        	 var cnt="<div class='control-group "+name+"' ><label class='control-label'><b>"+showName+":</b></label>";
					        		 cnt+="<div class='controls'><input class='required'  type='text' value='"+msg[0]+"' name="+name+"  style='width:200px;'/>&nbsp;&nbsp;<a class='remove-row' onclick=removecontent('"+id+"','"+name+"')><span class='icon-minus'></span></a></div></div>";
									 $("#"+id+" .addType").append(cnt);
						         }
					        }
					  });
					}else if(name=='price'){
						 var cnt="<div style='width:900px;border:1px solid #EE3B3B' class='salePrice'><div class='control-group price' ><label class='control-label'><b>Price:</b></label>";
			        	  cnt+="<div class='controls'><div class='input-prepend input-append'><input class='required price' type='text'  name='price'  style='width:100px;'/></div>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','salePrice')><span class='icon-minus'></span></a></div></div>";
			        	  //cnt+="<div class='control-group ' ><label class='control-label'><b>SalePrice:</b></label>";
			        	  //cnt+="<div class='controls'><div class='input-prepend input-append'><input class='required price' type='text'  name='salePrice'  style='width:100px;'/></div>&nbsp;&nbsp;</div></div>";
			        	  cnt+="<div class='control-group ' ><label class='control-label'><b>Reason:</b></label>";
			        	  cnt+="<div class='controls'><div class='input-prepend input-append'><select  name='reason' style='width: 220px' class='required'><option value='计划改价'>计划调价</option><option value='汇率改价'>汇率改价</option><option value='防御性降价'>防御性降价</option><option value='积压降价'>积压降价</option><option value='断货升价'>断货升价</option><option value='促销调价'>促销调价</option><option value='包邮调价'>包邮调价(15分钟自动改回原价)</option><option value='其它'>其它</option></select></div>&nbsp;&nbsp;</div></div></div>";
			        	  $("#"+id+" .addType").append(cnt);
					}else{
						$.jBox.tip('修改内容已存在');
					}
				}
			}
			
		}
		
	
		
		function openTree(catalog,country,id){
			if(nodes1!=null&&nodes1.length>0){
				console.log("init");
				var tree = $.fn.zTree.init($("#menuTree"), setting,nodes1);
	            tree.setting.check.chkboxType = { "Y" : "", "N" : "" };
			}else{
				$.ajax({  
			        type : 'POST', 
			        url : '${ctx}/amazoninfo/amazonPortsDetail/treeData',  
			        dataType:"json",
			        data : "country="+country,  
			        async: true,
			        success : function(msg){
			        	var nodes="[ ";
			        	for(var i=0;i<msg.length;i++){
			        		
							nodes+="{\"id\":\""+msg[i]['id']+"\",\"pId\":\""+(msg[i]['pId']==null?0:msg[i]['pId'])+"\",\"name\":\""+html_decode(html_decode(html_decode(html_decode(msg[i]['name']))))+"\",\"title\":\""+htmlEncode(msg[i]['title'])+"\",\"chkDisabled\":"+((msg[i]['title']==null||msg[i]['title']=='')?true:false)+"},";
			        	} 
			        	nodes=nodes.substring(0,nodes.length-1);
			        	nodes+="]";
			        	var treeObj=eval('('+nodes+')');
			        	nodes1=treeObj;
			        	var tree = $.fn.zTree.init($("#menuTree"), setting,treeObj);
			            tree.setting.check.chkboxType = { "Y" : "", "N" : "" };
			        }
			   });
			}
		
		   $("#selectTree").modal();
		}
		
		function openTree2(catalog,country,id){
			if(nodes2!=null&&nodes2.length>0){
				console.log("init2");
				var tree = $.fn.zTree.init($("#menuTree2"), setting2,nodes2);
	            tree.setting.check.chkboxType = { "Y" : "", "N" : "" };
			}else{

				$.ajax({  
			        type : 'POST', 
			        url : '${ctx}/amazoninfo/amazonPortsDetail/treeData',  
			        dataType:"json",
			        data : "country="+country,  
			        async: true,
			        success : function(msg){
			        	var nodes="[ ";
			        	for(var i=0;i<msg.length;i++){
			        		
							nodes+="{\"id\":\""+msg[i]['id']+"\",\"pId\":\""+(msg[i]['pId']==null?0:msg[i]['pId'])+"\",\"name\":\""+html_decode(html_decode(html_decode(html_decode(msg[i]['name']))))+"\",\"title\":\""+htmlEncode(msg[i]['title'])+"\",\"chkDisabled\":"+((msg[i]['title']==null||msg[i]['title']=='')?true:false)+"},";
			        	} 
			        	nodes=nodes.substring(0,nodes.length-1);
			        	nodes+="]";
			        	
			        	var treeObj=eval('('+nodes+')');
			        	nodes2=treeObj;
			        	var tree = $.fn.zTree.init($("#menuTree2"), setting2,treeObj);
			            tree.setting.check.chkboxType = { "Y" : "", "N" : "" };
			        }
			   });
			}
		   $("#selectTree2").modal();
		}
		
		function zTreeOnCheck(event, treeId, treeNode) {
			
			$("#navTabs .asinTab ").each(function(i,j){
				if($(this).attr("class").indexOf("active")>=0){
					var cntId=$(this).attr("id").substring(4);
					console.log(cntId+"=="+treeId+"=="+treeNode.id+"=="+treeNode.name+"=="+treeNode.title);
					$("#"+cntId).find("input[name='catalog1']").val(treeNode.title);
				}
			});
			
		}
		
		function zTreeOnCheck2(event, treeId, treeNode) {
			console.log(treeNode);
			$("#navTabs .asinTab ").each(function(i,j){
				if($(this).attr("class").indexOf("active")>=0){
					var cntId=$(this).attr("id").substring(4);
					console.log(cntId+"=="+treeId+"=="+treeNode.id+"=="+treeNode.name+"=="+treeNode.title);
					$("#"+cntId).find("input[name='catalog2']").val(treeNode.title);
				}
			});
			
		}
		
		function setHighlight(treeId, treeNode) {
	          return (treeNode.highlight) ? {color:"green", "font-weight":"bold", "background-color": "#ddd"} : {color:"#000", "font-weight":"normal"};
	    }
		var timeoutId = null;
		function searchNodeLazy(value) {
			if (timeoutId) {
				clearTimeout(timeoutId);
			}
			timeoutId = setTimeout(function(){
				searchNode(value);	
			}, 500);
		}
		
		function editAdd(id,showName,name,country,asin){
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/amazonPortsDetail/getContent',  
		        dataType:"json",
		        data : "accountName="+$("#accountName").val()+"&asin="+asin+"&name="+name,  
		        async: true,
		        success : function(msg){
		        	if(name=='description'){
			        	  var cnt="<br/><div class='control-group "+name+"' ><label class='control-label'><b>"+showName+":</b></label>";
						  cnt+="<div class='controls'><textarea class='destroyArea'  id="+name+id+" name="+name+" style='float: left;margin: 0px; width:90%; height:407px;'>"+msg[0]+"</textarea>";
						  cnt+="<a class='remove-row' onclick=removecontent('"+id+"','"+name+"')><span class='icon-minus' style='float:right;vertical-align:middle;'></span></a><div class='showMsg'></div></div></div>";
						  $("#"+id+" .addType").append(cnt);
						  var editor = CKEDITOR.replace(name+id,{height:'440px',width:'90%',toolbarStartupExpanded:true,startupFocus:true});
						   editor.on("instanceReady", function () {  
						        this.document.on("keyup", function(e){
						        	$("#"+name+id).parent().find(".showMsg").html("<font color='red'>字符长度："+editor.getData().length+"</font>");
						        });  
						  }); 
						  $("#"+name+id).data("editor",editor);
		        	}else if(name=="feature"||name=="keyword"){
		        		  var cnt="<br/><div style='width:900px;border:1px solid #EE3B3B' class='"+name+"'>";
			        	  for(var i=1;i<=5;i++){
			        		  cnt+="<div class='control-group "+name+i+"' ><label class='control-label'><b>"+showName+i+":</b></label>";
			        		  cnt+="<div class='controls'><input  type='text' onkeyup='getLen(this)' class='required'  name="+name+i+"  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;";
							  if(i==1){
								  cnt+="<a class='remove-row' onclick=removecontent('"+id+"','"+name+i+"');><span class='icon-minus'></span></a>";
							  }
			        		  cnt+="<div class='showMsg'></div></div></div>";
			        	  }
		        		  cnt+="</div>";
		        		  $("#"+id+" .addType").append(cnt);
		        		  for(var i=1;i<=5;i++){
		        				  var uri_encoded = html_decode(html_decode(html_decode(html_decode(msg[i-1]))));
			        			  //var encoded=uri_encoded.replace(/%([^\d].)/, "%25$1");
			        			  console.log(uri_encoded);
			        			  encoded =htmlDecode(uri_encoded);
			        			  $("#"+id+" .addType ."+name+i).find("[name='"+name+i+"']").val(html_decode(encoded));
		        		  }
			         }else if(name=="catalog"){
			        	 var idx=1;
			        	 if(country.indexOf('com')<0){
			        		  idx=2;
			        	 }
			        	 for(var i=1;i<=idx;i++){
			        		  var cnt="<div class='control-group "+name+i+"' ><label class='control-label'><b>"+showName+i+":</b></label>";
			        		  cnt+="<div class='controls'><input class='required'  type='text' value='"+(msg[i-1]==undefined?"":msg[i-1])+"' name="+name+i+"  style='width:200px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','"+name+i+"')><span class='icon-minus'></span></a></div></div>";
							  $("#"+id+" .addType").append(cnt);
			        	  }
			         }else if(name=="packageDimensions"){
			        	 var cnt="<br/><div style='width:900px;border:1px solid #EE3B3B' class='package'><div class='control-group packageLength' ><label class='control-label'><b>PackageLength:</b></label>";
		        		 cnt+="<div class='controls'><div class='input-prepend input-append'><input type='text' class='required price' value='"+msg[0]+"' name='packageLength'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','packageLength')><span class='icon-minus'></span></a></div></div>";
		        		 
		        		 cnt+="<div class='control-group packageWidth' ><label class='control-label'><b>PackageWidth:</b></label>";
		        		 cnt+="<div class='controls'><div class='input-prepend input-append'><input type='text' class='required price' value='"+msg[1]+"' name='packageWidth'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
		        		
		        		 cnt+="<div class='control-group packageHeight' ><label class='control-label'><b>PackageHeight:</b></label>";
		        		 cnt+="<div class='controls'><div class='input-prepend input-append'><input type='text' class='required price' value='"+msg[2]+"' name='packageHeight'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
		        		 
		        		 cnt+="<div class='control-group packageWeight' ><label class='control-label'><b>PackageWeight:</b></label>";
		        		 cnt+="<div class='controls'><div class='input-prepend input-append'><input type='text' class='required price' value='"+msg[3]+"' name='packageWeight'  style='width:100px;'/><span class='add-on'>pounds</span></div>&nbsp;&nbsp;</div></div></div>";
		        		
		        		 $("#"+id+" .addType").append(cnt);
			         }else{
			        	 var cnt="<div class='control-group "+name+"' ><label class='control-label'><b>"+showName+":</b></label>";
		        		 cnt+="<div class='controls'><input class='required'  type='text' value='"+msg[0]+"' name="+name+"  style='width:200px;'/>&nbsp;&nbsp;<a class='remove-row' onclick=removecontent('"+id+"','"+name+"')><span class='icon-minus'></span></a></div></div>";
						 $("#"+id+" .addType").append(cnt);
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
		
	    
		function validKeyword(country,asin,id,cntName){
			var param = {};
			param.keyword=$("#"+id).find("input[name='"+cntName+"']").val();
			param.accountName=$("#accountName").val();
			param.asin=asin;
			$.get("${ctx}/amazoninfo/amazonPortsDetail/findCompareContent?"+$.param(param),function(data){
				if(data!=''){ 
					top.$.jBox.open(data,"查看明细",500,250,{buttons: {'关闭': true}});  
				}
		    });
		}
	
		function removecontent(id,cntName){
			
			top.$.jBox.confirm("Are you sure give up?",'<spring:message code="sys_label_tips_msg"/>',function(v,h,f){
				if(v=='ok'){
					if(cntName.substring(0,cntName.length-1)=='feature'||cntName.substring(0,cntName.length-1)=='keyword'){
						 for(var i=1;i<=5;i++){
							 $("#"+id+" .addType ."+cntName.substring(0,cntName.length-1)+i).remove();
						 }
						 $("#"+id+" .addType ."+cntName.substring(0,cntName.length-1)).remove();
					}else if(cntName=='packageLength'||cntName=='packageWidth'||cntName=='packageHeight'||cntName=='packageWeight'){
						$("#"+id+" .addType .packageLength").remove();
						$("#"+id+" .addType .packageWidth").remove();
						$("#"+id+" .addType .packageHeight").remove();
						$("#"+id+" .addType .packageWeight").remove();
						$("#"+id+" .addType .package").remove();
					}else if(cntName=='description'){
						$("#"+cntName+id).data("editor").destroy();
						 $("#"+id+" .addType ."+cntName).remove();
					}else if(cntName=='salePrice'){
						 $("#"+id+" .addType ."+cntName).remove();
					}else{
					   $("#"+id+" .addType ."+cntName).remove();
					}
				}
			},{buttonsFocus:1,persistent: true});
			return false;
		}
		
		function showSku(skuValue){
			 var tabId="";
			var reg=/RRR/g;
			skuValue=skuValue.replace(reg,' ');
			console.log(skuValue);
			if(skuValue.indexOf(",")<0){
				tabId=skuValue;
			}else{
				//tabId=$("#asin").find("option:selected").text().replace(" ","_")+"_ALL_SKU";
				tabId=$("#asin").find("option:selected").text().replace(/\s/g,'_')+"_ALL_SKU";
			} 
			var asin=$("#asin").val();
			var titleText=$("#asin").find("option:selected").text();
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/amazonPortsDetail/getRegularContent',  
		        dataType:"json",
		        data : "accountName="+$("#accountName").val()+"&asin="+$("#asin").val(),  
		        async: true,
		        success : function(msg){
		        	addTabs({id:tabId, asinVal:asin,title:titleText,channel:'${amazonPostsDetail.country}',skuVal:skuValue,objTitle:msg.title});
		        	console.log(asin+"=="+titleText);
		        }
		  });
	   }
		
		function searchNode(){
			var searchCnt=$("#dataBtn").val();
			console.log(searchCnt);
			if(searchCnt==''){
				return;
			}
			var treeObj = $.fn.zTree.getZTreeObj('menuTree'); 
			var nodes = treeObj.getNodesByParamFuzzy("name", searchCnt);
			var allNodes = treeObj.transformToArray(treeObj.getNodes());

			for (var i = 0; i < allNodes.length; i ++) {
				allNodes[i].highlight = false;
				treeObj.updateNode(allNodes[i]);
			}
			treeObj.expandAll(false);
			treeObj.expandNode(treeObj.getNodes()[0],true);
			for (var i = 0; i < nodes.length; i ++) {
				nodes[i].highlight = true;
				treeObj.updateNode(nodes[i]);
				treeObj.expandNode(nodes[i].getParentNode(),true);
			}
		}
		
		function searchNode2(){
			var searchCnt=$("#dataBtn2").val();
			console.log(searchCnt);
			if(searchCnt==''){
				return;
			}
			var treeObj = $.fn.zTree.getZTreeObj('menuTree2'); 
			var nodes = treeObj.getNodesByParamFuzzy("name", searchCnt);
			var allNodes = treeObj.transformToArray(treeObj.getNodes());

			for (var i = 0; i < allNodes.length; i ++) {
				allNodes[i].highlight = false;
				treeObj.updateNode(allNodes[i]);
			}
			treeObj.expandAll(false);
			treeObj.expandNode(treeObj.getNodes()[0],true);
			for (var i = 0; i < nodes.length; i ++) {
				nodes[i].highlight = true;
				treeObj.updateNode(nodes[i]);
				treeObj.expandNode(nodes[i].getParentNode(),true);
			}
		}
		
		function getLen(c){
			$(c).parent().find(".showMsg").html("<font color='red'>字符长度："+$(c).val().length+"</font>");
		}
	</script>
</head>
<body>
	
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
		 <li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/form">修改帖子信息</a></li>	
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
		<c:if test="${'2' eq flag }"><input type="hidden" name="id" value="${amazonPostsFeed.id}" /></c:if>
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
			<label class="control-label">产品名称:</label>
			<div class="controls">
				<select id="asin"  style="width: 200px" class="required">
					<%-- <c:forEach items="${asinList}" var="single" varStatus="i">
						 <option value="${single}">${single}</option>
					</c:forEach>	 --%>
					<c:forEach items="${list}" var="single" varStatus="i">
						 <option value="${single.asin}" skuValue="${single.sku }">${single.productName}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Sku:</label>
			<div class="controls">
			     <span id="sku">
			       <c:if test="${fn:length(fn:split(list[0].sku,','))>1}">
				    <a href="#" class="add-row" title='点击添加帖子修改' onclick="showSku('${list[0].sku }')"><span class="icon-plus"></span>全部Sku</a>&nbsp;&nbsp;
					<c:forEach items="${fn:split(list[0].sku,',')}" var="seltSku" varStatus="i">
					    <a href="#" title='点击添加帖子修改' class="add-row" onclick="showSku('${seltSku }')"><span class="icon-plus"></span>${seltSku }</a>&nbsp;&nbsp;
				    </c:forEach>
			       </c:if>
				    <c:if test="${fn:length(fn:split(list[0].sku,','))==1}">
					    <a href="#" title='点击添加帖子修改' class="add-row" onclick="showSku('${list[0].sku }')"><span class="icon-plus"></span>${list[0].sku}</a>&nbsp;&nbsp;
				    </c:if>
			     </span>
			     
			</div>
		</div>
		<!-- <div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" id="add-row"><span class="icon-plus"></span>新增帖子</a></div>
		 -->
		 <div class="main">
            <div>
                <!-- Nav tabs -->
                <ul class="nav nav-tabs" role="tablist" id="navTabs">
                                   
                </ul>

                <!-- Tab panes -->
                <div class="tab-content" id="tabContent">
                                 
                </div>
            </div>
        </div>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提  交"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form>
	
	<div id="selectTree" class="modal hide fade" tabindex="-1" data-width="350">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3>目录搜索</h3>
			</div>
			<div class="modal-body">
			<!-- <div style="display:inline;"><input type='text' id='dataBtn' placeholder='输入目录名搜索'/> 
			    <input type='button'  onclick="searchNode()" value='search'/>
			    </div> -->
			    
			    <form class="form-search">
				  <input type="text" class="input-medium search-query" id='dataBtn'  placeholder='输入目录名搜索'>
				  <button type="button" class="btn" id="dataBtnSearch" onclick="searchNode()">Search</button>
				</form>
			    
				<div id='menuTree' class='ztree'  style='margin-top:3px;float:center;'></div>
				<div class="modal-footer">
					<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
				</div>
			</div>
	</div>
	
	<div id="selectTree2" class="modal hide fade" tabindex="-1" data-width="350">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3>目录搜索</h3>
			</div>
			<div class="modal-body">
			    <form class="form-search">
				  <input type="text" class="input-medium search-query" id='dataBtn2'  placeholder='输入目录名搜索'>
				  <button type="button" class="btn" id="dataBtnSearch2" onclick="searchNode2()">Search</button>
				</form>
			    
				<div id='menuTree2' class='ztree'  style='margin-top:3px;float:center;'></div>
				<div class="modal-footer">
					<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
				</div>
			</div>
	</div>
</body>
</html>