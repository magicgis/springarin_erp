<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊英语国家帖子编辑</title>
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
		$(document).ready(function() {
			$("#navTabs").on("click", ".close-tab", function () {
			        id = $(this).parent("a").attr("aria-controls");
			        closeTab(id);
			 });
			
			$("#accountName").change(function(){
				var params = {};
				params.accountName = $(this).val().join(",");;
				window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/commonForm?"+$.param(params);
			});
			
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
		
		function showTab(){
			if($("#accountName").val()==null){//["com", "ca", "uk"]
				$.jBox.tip("请选择平台");
				return false;
			}
			var asin=$("#pname").find("option:selected").attr("asinVal");
			var accountName=$("#accountName").val()[0];
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/amazonPortsDetail/getRegularContent',  
		        dataType:"json",
		        data : "accountName="+accountName+"&asin="+asin,  
		        async: true,
		        success : function(msg){
		        	
		        	addTabs({id:$("#pname").val().replace(/\s/g,'_'), title:$("#pname").val(),channel:$("#accountName").val(),objTitle:msg.title, close: true
		        		});
		        }
	       });
		}
		var addTabs = function (obj) {
			    id = "tab_" + obj.id;
			    $("#navTabs .active").removeClass("active");
			    $(".tab-content .active").removeClass("active");

		    	var compTitle=obj.title.replace(/\s/g,'_');
		    	if (!$("#" + id)[0]&&!$("#tab_" + compTitle)[0]) {//title 必需
			        mainHeight = $(document.body).height();
			        title = '<li class="asinTab" role="presentation" id="tab_' + id + '"><a title="'+obj.id+'" href="#' + id + '" aria-controls="' + id + '" role="tab" data-toggle="tab">' + obj.id+'<i class="close-tab icon-remove"></i></a></li>';
			        var content = '<div role="tabpanel" class="tab-pane" id="' + id + '">';
			        content+='<div class="control-group"><div class="controls"><select onchange=addContent("'+id+'") class="selectType"><option value="">--请选择修改内容--</option><option value="all">--All--</option><option value="description">Description</option><option value="feature">BulletPoint</option><option value="keyword">SearchTerms</option> ';
			        content+='<option value="brand">Brand</option><option value="manufacturer">Manufacturer</option> ';
			        content+='<option value="packageDimensions">PackageDimensions</option>';
			        content+='</select></div></div><br/><br/><div class="addType"><input name="comAsin" class="comAsin" type="hidden"  value="'+obj.comAsin+'"/><input name="caAsin" class="caAsin" type="hidden"  value="'+obj.caAsin+'"/><input name="ukAsin" class="ukAsin" type="hidden"  value="'+obj.ukAsin+'"/><input name="productName"  class="productName" type="hidden" value="'+obj.title+'"/>';
			       
			        content+='<div class="control-group"><label class="control-label"><b>帖子优化:</b></label><div class="controls"><select name="studio">';
			        content+='<option value="1">否</option><option value="0">是</option>';
			        content+='</select></div></div>';
			        
			        content+='<div class="control-group title"><label class="control-label"><b>Title:</b></label>';
			        content+='<div class="controls"><textarea class="required"  name="title" onkeyup="getLen(this)" style="width:668.063px;height:108px;">'+obj.objTitle+'</textarea><div class="showMsg"></div></div></div><br/>';
			        
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
		};
		
		function removeSku(id,skuName,skuType,sku){
			
			 var oldSku=$("#"+id+" .addType ."+skuType).parent().find("input[name='"+skuName+"']").val();
             if(sku==oldSku){
            	 $("#"+id+" .addType ."+skuType).parent().find("input[name='"+skuName+"']").val('');
			 }else if(oldSku.indexOf(sku+",")>=0){
				 $("#"+id+" .addType ."+skuType).parent().find("input[name='"+skuName+"']").val(oldSku.split(sku+",").join(""));
			 }else if(oldSku.indexOf(","+sku+",")>=0){
				 $("#"+id+" .addType ."+skuType).parent().find("input[name='"+skuName+"']").val(oldSku.split(","+sku+",").join(""));
			 }else if(oldSku.indexOf(","+sku)>=0){
				 $("#"+id+" .addType ."+skuType).parent().find("input[name='"+skuName+"']").val(oldSku.split(","+sku).join(""));
			 }
             $("#"+id+" .addType ."+skuType).remove();
		}
		
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
			var asin=$("#pname").find("option:selected").attr("asinVal");
			var accountName=$("#accountName").val()[0];
			
			if(name=="all"){
			
				
                var title=$("#pname").val();
				var editor=$("#description"+id).data("editor");
				if(editor){
					editor.destroy();
					$("#"+id+" .addType .description").remove();
				}
				
				var temp='<input name="comAsin" class="comAsin" type="hidden"  value="'+comAsin+'"/><input name="caAsin" class="caAsin" type="hidden"  value="'+caAsin+'"/><input name="ukAsin" class="ukAsin" type="hidden"  value="'+ukAsin+'"/><input name="productName"  class="productName" type="hidden" value="'+title+'"/>';
				$("#"+id+" .addType" ).empty();
				$("#"+id+" .addType").append(temp); 

				$.ajax({  
			        type : 'POST', 
			        url : '${ctx}/amazoninfo/amazonPortsDetail/getAllContent',  
			        dataType:"json",
			        data : "accountName="+accountName+"&asin="+asin,  
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
			        	  
		        	  cnt+="<div class='control-group catalog1' ><label class='control-label'><b>RecommendedBrowseNode1:</b></label>";
			        	  cnt+="<div class='controls'><input  type='text' class='required number' value='"+(msg.catalog1==undefined?"":msg.catalog1)+"' name='catalog1'  style='width:200px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','catalog1')><span class='icon-minus'></span></a></div></div>";
			        	  cnt+="<div class='control-group catalog2' ><label class='control-label'><b>RecommendedBrowseNode2:</b></label>";
			        	  cnt+="<div class='controls'><input type='text' class='required number' value='"+(msg.catalog2==undefined?"":msg.catalog2)+"' name='catalog2'  style='width:200px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','catalog2')><span class='icon-minus'></span></a></div></div>";
			        	 
			        	  cnt+="<div style='width:900px;border:1px solid #EE3B3B' class='salePrice'><div class='control-group price' ><label class='control-label'><b>Price:</b></label>";
			        	  cnt+="<div class='controls'><div class='input-prepend input-append'><input class='required price' type='text'  name='price'  style='width:100px;'/></div>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','salePrice')><span class='icon-minus'></span></a></div></div>";
			        	 // cnt+="<div class='control-group ' ><label class='control-label'><b>SalePrice:</b></label>";
			        	  //cnt+="<div class='controls'><div class='input-prepend input-append'><input class='required price' type='text'  name='salePrice'  style='width:100px;'/></div>&nbsp;&nbsp;</div></div>";
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
					if(obj.length == 0){
						$.ajax({  
					        type : 'POST', 
					        url : '${ctx}/amazoninfo/amazonPortsDetail/getContent',  
					        dataType:"json",
					        data : "accountName="+accountName+"&asin="+asin+"&name="+name,  
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
					        			 // if(name=="feature"){
					        				  var uri_encoded = html_decode(html_decode(html_decode(html_decode(msg[i-1]))));
						        			  //var encoded=uri_encoded.replace(/%([^\d].)/, "%25$1");
						        			  encoded =htmlDecode(uri_encoded);
						        			  $("#"+id+" .addType ."+name+i).find("[name='"+name+i+"']").val(encoded);
					        			//  }else{
					        			//	  $("#"+id+" .addType ."+name+i).find("[name='"+name+i+"']").val(msg[i-1]);
					        			 // }
					        		  }
						         }else if(name=="catalog"){
						        	 for(var i=1;i<=2;i++){
						        		  var cnt="<div class='control-group "+name+i+"' ><label class='control-label'><b>"+showName+i+":</b></label>";
						        		  cnt+="<div class='controls'><input class='required number'  type='text' value='"+(msg[i-1]==undefined?"":msg[i-1])+"' name="+name+i+"  style='width:200px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('"+id+"','"+name+i+"')><span class='icon-minus'></span></a></div></div>";
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
					}else{
						$.jBox.tip('修改内容已存在');
					}
				}
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
		<li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/commonForm">修改帖子信息(英语国家)</a></li>	
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
	<form id="inputForm"  action="${ctx}/amazoninfo/amazonPortsDetail/saveEnglishPostsChange" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">平台:</label>
				<div class="controls">
				<select id="accountName" name="accountName" style="width:250px" class="required" multiple class="multiSelect" >
				  
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
						    <c:forEach items="${accountMap['com']}" var="account">
								<option value="${account}" ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							</c:forEach>
							<c:forEach items="${accountMap['ca']}" var="account">
								<option value="${account}" ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							</c:forEach>
							<c:forEach items="${accountMap['uk']}" var="account">
								<option value="${account}" ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							</c:forEach>
					</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
						<shiro:hasPermission name="amazoninfo:feedSubmission:com">
							 <c:forEach items="${accountMap['com']}" var="account">
								<option value="${account}" ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							</c:forEach>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:uk">
							<c:forEach items="${accountMap['uk']}" var="account">
								<option value="${account}" ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							</c:forEach>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:ca">
							<c:forEach items="${accountMap['ca']}" var="account">
								<option value="${account}" ${fn:contains(amazonPostsDetail.accountName,account)?'selected':''}>${account}</option>
							</c:forEach>
						</shiro:hasPermission>
					</shiro:lacksPermission>
				</select>
			</div>
		</div>
	   <div class="control-group">
			<label class="control-label">产品名称:</label>
			<div class="controls">
				<select id="pname"  style="width: 250px" class="required">
				    <c:set  value="${fn:split(amazonPostsDetail.accountName,',')[0]}" var='accountKey'/>
					<c:forEach items="${postsMap}" var="posts" varStatus="i">
					      
						 <option value="${posts.key}" asinVal='${postsMap[posts.key][accountKey].asin}' >${posts.key}</option>
					</c:forEach>
				</select>&nbsp;&nbsp;
			</div>
		</div>

		<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" title='点击添加帖子修改' onclick='showTab();'><span class="icon-plus"></span>新增帖子</a></div>
		
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
</body>
</html>