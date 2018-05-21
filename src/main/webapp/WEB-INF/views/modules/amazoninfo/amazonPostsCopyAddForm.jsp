<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊新增复制帖</title>
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
		
		var index=1;
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
				   
				 
		          var currentHtml="";
				   <c:if test="${!fn:contains('com,ca,uk,mx,es',amazonPostsDetail.country)}">
				   currentHtml = "<option value='${amazonPostsDetail.accountName }'>${amazonPostsDetail.accountName}</option>";
			      </c:if>
			      <c:if test="${fn:contains('com,ca,uk',amazonPostsDetail.country) }">
			        <c:forEach items="${accountMap['com']}" var="account">
			           currentHtml += "<option value='${account }' >${account }</option>";
			        </c:forEach>
			        <c:forEach items="${accountMap['com']}" var="account">
			           currentHtml += "<option value='${account }' >${account }</option>";
			        </c:forEach>
			        <c:forEach items="${accountMap['com']}" var="account">
			           currentHtml += "<option value='${account }' >${account }</option>";
			        </c:forEach>
			      </c:if>
			      <c:if test="${fn:contains('mx,es',amazonPostsDetail.country)}">
			         <c:forEach items="${accountMap['es']}" var="account">
		              currentHtml += "<option value='${account }' >${account }</option>";
		            </c:forEach>
		            <c:forEach items="${accountMap['mx']}" var="account">
		              currentHtml += "<option value='${account }' >${account }</option>";
		            </c:forEach>
		          </c:if>
		          
		          
			      var sameHtml="";
			      
			      <c:forEach items='${amazonProductList}' var='product'>
			         sameHtml+="<option value='${product.sku }' eanVal='${product.ean }' asinVal='${product.asin }'>${product.sku }</option>";
			       </c:forEach>
			      
		          var selectCountry='${amazonPostsDetail.country}';
				   var cnt="<div role='tabpanel' class='tab-pane' id='c_"+index+"'>";
				   
				
				   var catalogType="";
				   <c:forEach  items='${catalogMap}' var='catalogMap'>
				     catalogType+='<optgroup label="${catalogMap.key }">';
	                   <c:forEach  items='${catalogMap.value}' var='catalog'>
	                     catalogType+='<option value="${catalogMap.key },${catalog}"  ${catalog eq "Electrical"?"selected":""}>${catalog }</option>';
		               </c:forEach>
		               catalogType+='</optgroup>';
	              </c:forEach> 
	         
				   cnt+="<div class='control-group'><label class='control-label'><b>CatalogType:</b></label><div class='controls'><input type='hidden' name='catalogType1' value='HomeImprovement'/><input type='hidden' name='catalogType2' value='Electrical'/> <select  style='width:25%' onchange='setCatalogTypeValue(this)' class='js-states form-control'>";
				   cnt+=catalogType+"</select></div> </div> ";  
				   
				   <c:if test="${'uk' eq amazonPostsDetail.country}">
					cnt+="  <div class='control-group brand' ><label class='control-label'><b>Power:</b></label> ";
					cnt+=" <div class='controls'><select onchange='changePowerType(this)' style='width:10%' name='power'><option value=''>请选择</option> <option value='0'>带电源&键盘</option><option value='1'>不带电源</option></select>&nbsp;&nbsp; ";
					cnt+=" <span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div> ";
	        	  </c:if>
					
				   
				   if(selectCountry=='de'||selectCountry=='fr'||selectCountry=='it'||selectCountry=='es'||selectCountry=='uk'){
					   cnt+=" <div class='control-group'><label class='control-label'><b>源Asin:</b></label><div class='controls'><select  style='width:25%' onchange='setSameValue(this)'><option value='' selected='selected'>-可选择Sku,快捷生成sku和asin-</option>";
					   cnt+=sameHtml+"</select></div> </div>";
				   }
				   
				   cnt+="<div class='control-group sku' ><label class='control-label'><b>Sku:</b></label>";
				   cnt+="<div class='controls'><input  class='required' type='text' onblur='validSku(this)' name='sku'  style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp; <div class='showMsg'></div></div></div>";
		        	
				 
				   
				  cnt+=" <div class='control-group eanOrAsin' ><label class='control-label'><b>EanOrAsin:</b></label>";
			      cnt+="<div class='controls'> <select onchange='changeName(this)'  style='width:10%'><option value='asin' >Asin</option><option value='ean'>Ean</option></select>&nbsp;&nbsp;";
				  cnt+="<input  type='text'  name='asin' class='required'  style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>";
             
				  cnt+="<div class='control-group type'><label class='control-label'><b>Type:</b></label>";
				   cnt+="<div class='controls'> <select onchange='showQuantity(this)' name='isFba' style='width:10%'><option value='1' >FBA帖</option>";
				   <c:if test="${'com' eq amazonPostsDetail.country||'de' eq amazonPostsDetail.country }">
				     cnt+="<option value='0' >本地帖</option>";
				   </c:if>
				  cnt+=" </select>&nbsp;&nbsp;</div></div>";
				   
				  cnt+="<div style='display:none;' class='control-group localQuantity'><label class='control-label'><b>库存数:</b></label>";
				  cnt+="<div class='controls'><input type='text' style='display:none' name='quantity' class='number'/></div></div>";
					
				  cnt+="<div class='control-group sku'><label class='control-label'><b>复制Sku:</b></label><div class='controls'>";
				  cnt+="<select name='copyCountry' onchange='getCountrySku(this)' style='width:10%;'  ><option value='' selected='selected'>-请选择平台-</option>";
				  cnt+=currentHtml;
				  cnt+="</select> &nbsp;&nbsp;";
				  cnt+="<select  name='copySku' style='width: 220px;' onchange='queryAllInfo(this)'><option value='' selected='selected'>-请选择Sku-</option>";
				  cnt+="</select> &nbsp;&nbsp;</div> </div>";
		        	 
		        	 
					
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
				//	cnt+="  <div class='controls'><input class='required price' type='text'  name='salePrice'  style='width:100px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>";
		        	
					cnt+=" <div class='control-group description' ><label class='control-label'><b>Description:</b></label>";
					cnt+="   <div class='controls'><textarea class='destroyArea' id='description"+index+"' name='description'  style='float:left;margin: 0px; width:90%; height:407px;'></textarea>";
				    cnt+=" &nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('c_"+index+"','description')><span class='icon-minus'></span></a><div class='showMsg'></div></div></div>";
				    
				    
					cnt+="<div class='control-group feature1' ><label class='control-label'><b>BulletPoint1:</b></label>";
					cnt+=" <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature1'   style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('c_"+index+"','feature1')><span class='icon-minus'></span></a><div class='showMsg'></div></div></div>";
					cnt+=" <div class='control-group feature2' ><label class='control-label'><b>BulletPoint2:</b></label>";
					cnt+=" <div class='controls'><input type='text' onkeyup='getLen(this)'  name='feature2' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
					cnt+="  <div class='control-group feature3' ><label class='control-label'><b>BulletPoint3:</b></label>";
					cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature3'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
					cnt+="  <div class='control-group feature4' ><label class='control-label'><b>BulletPoint4:</b></label>";
					cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature4'   style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
					 cnt+="  <div class='control-group feature5' ><label class='control-label'><b>BulletPoint5:</b></label>";
					cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature5' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
						
			         
				    cnt+="<div class='control-group keyword1' ><label class='control-label'><b>SearchTerms1:</b></label>";
				    cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword1'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('c_"+index+"','keyword1')><span class='icon-minus'></span></a><div class='showMsg'></div></div></div>";
				    cnt+=" <div class='control-group keyword2' ><label class='control-label'><b>SearchTerms2:</b></label>";
				    cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword2'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
				    cnt+="  <div class='control-group keyword3' ><label class='control-label'><b>SearchTerms3:</b></label>";
				    cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword3'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
				    cnt+="  <div class='control-group keyword4' ><label class='control-label'><b>SearchTerms4:</b></label>";
				    cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword4'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
				    cnt+="  <div class='control-group keyword5' ><label class='control-label'><b>SearchTerms5:</b></label>";
				    cnt+="  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword5'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>";
					
				   
				    cnt+=" <div class='control-group manufacturer' ><label class='control-label'><b>Manufacturer:</b></label>";
				    cnt+=" <div class='controls'><input  type='text'  name='manufacturer'  style='width:200px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('c_"+index+"','manufacturer')><span class='icon-minus'></span></a></div></div>";
				    
				    cnt+=" <div class='control-group packageLength' ><label class='control-label'><b>PackageLength:</b></label>";
				    cnt+=" <div class='controls'><div class='input-prepend input-append'><input class=' price' type='text'  name='packageLength'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('c_"+index+"','packageLength')><span class='icon-minus'></span></a></div></div>";
				    cnt+=" <div class='control-group packageWidth' ><label class='control-label'><b>PackageWidth:</b></label>";
				    cnt+=" <div class='controls'><div class='input-prepend input-append'><input class=' price' type='text'  name='packageWidth'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
				    cnt+=" <div class='control-group packageHeight' ><label class='control-label'><b>PackageHeight:</b></label>";
				    cnt+=" <div class='controls'><div class='input-prepend input-append'><input class=' price' type='text'  name='packageHeight'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>";
		        	cnt+=" <div class='control-group packageWeight' ><label class='control-label'><b>PackageWeight:</b></label>";
		        	cnt+=" <div class='controls'><div class='input-prepend input-append'><input class=' price' type='text'  name='packageWeight'  style='width:100px;'/><span class='add-on'>pounds</span></div>&nbsp;&nbsp;</div></div>";
		        	
		          
		          var countryVal='${amazonPostsDetail.country}';	
		      	  cnt+=" <div class='control-group catalog1' ><label class='control-label'><b>RecommendedBrowseNode1:</b></label>";
                  cnt+="<div class='controls'><input  type='text' class='required'  name='catalog1'  id='catalog1c_"+index+"'  style='width:200px;' />&nbsp;&nbsp;<input type='button' onclick=openTree('catalog1c_"+index+"','"+countryVal+"','c_"+index+"') value='目录搜索'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('c_"+index+"','catalog1')><span class='icon-minus'></span></a></div></div>";
			       
                  
                  <c:if test="${!fn:startsWith(amazonPostsDetail.country,'com')}">
			        cnt+=" <div class='control-group catalog2' ><label class='control-label'><b>RecommendedBrowseNode2:</b></label>";
			        cnt+="<div class='controls'><input  type='text' class='required'  name='catalog2'  id='catalog2c_"+index+"'  style='width:200px;' />&nbsp;&nbsp;<input type='button' onclick=openTree2('catalog2c_"+index+"','"+countryVal+"','c_"+index+"') value='目录搜索'/>&nbsp;&nbsp;<a  class='remove-row' onclick=removecontent('c_"+index+"','catalog2')><span class='icon-minus'></span></a></div></div>";
			      </c:if>
        	 
		        	
		        	cnt+="</div>";
		        	
		        	$(".tab-content").append(cnt);
		        	
		        	$("#c_" + index).find("select").select2();
		        	
		        	 var editor =  CKEDITOR.replace("description"+index,{height:'440px',width:'90%',toolbarStartupExpanded:true,startupFocus:false});
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
			 
			 $("#accountName").change(function(){
					var params = {};
					params.addType='2';
					params.accountName = $("#accountName").val();
					window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?"+$.param(params);
			 });
			 
			 
			 $("#inputForm").validate({
					submitHandler: function(form){
						var flag="0";
						var partNumberFlag=true;
						var titleAndBrand=true;
						$("#tabContent .tab-pane ").each(function(i,j){
							var brand="";
			    		    var title="";
								$(j).find("select").each(function(){
									if($(this).attr("name")){
										flag="1";
										if($(this).attr("name")=='brand'){
											brand=$(this).val();
										}
										if('${amazonPostsDetail.country}'=='uk'&&$(this).attr("name")=='power'&&$(this).val()==''){
											partNumberFlag=false;
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
								if(title.toLowerCase().indexOf(brand.toLowerCase().substring(1))<0){
									titleAndBrand=false;
								}
						});
						
						if(!partNumberFlag){
							top.$.jBox.error("英国需选择电源类型","<spring:message code="sys_label_tips_error"/>");
							return;
						}
						if(flag!="1"){
							top.$.jBox.error("保存内容为空","<spring:message code="sys_label_tips_error"/>");
							return;
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
			
			 
			 $("#createPostsType").change(function(){
				    var params = {};
					params.accountName = $("#accountName").val();
					params.addType='2';
					window.location.href = "${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?"+$.param(params);
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
				var country='${amazonPostsDetail.country}';
				if(country.indexOf('com')>0||country=='ca'){
					partNumber=partNumber+"-US";
				}else if(country=='jp'){
					partNumber=partNumber+"-JP";
				}else if(country=='mx'){
					partNumber=partNumber+"-MX";
				}
				if(country=='uk'){
					var type=$(c).parent().parent().parent().find("[name='power']").val();
					if(type=='0'){
						partNumber=partNumber+"-UK";
					}
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
					$("#c_"+cntId).find("input[name='catalog1']").val(treeNode.title);
				}
			});
			
		}
		
		function zTreeOnCheck2(event, treeId, treeNode) {
			console.log(treeNode);
			$("#navTabs .asinTab ").each(function(i,j){
				if($(this).attr("class").indexOf("active")>=0){
					var cntId=$(this).attr("id").substring(4);
					console.log(cntId+"=="+treeId+"=="+treeNode.id+"=="+treeNode.name+"=="+treeNode.title);
					$("#c_"+cntId).find("input[name='catalog2']").val(treeNode.title);
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
		function getCountrySku(c){
			var accountName=$(c).val();
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/amazonPortsDetail/getSkuByCountry',  
		        dataType:"json",
		        data : 'accountName=' +accountName,  
		        async: false,
		        success : function(msg){ 
		        	$(c).parent().find("[name='copySku']").empty();   
		        	var option = "<option value=''>--请选择Sku--</option>";   
		            for(var i=0;i<msg.length;i++){
						option += "<option  value=\"" + msg[i].asin + "\">" + msg[i].sku + "</option>"; 
	                }
		            $(c).parent().find("[name='copySku']").append(option);
		            $(c).parent().find("[name='copySku']").select2("val","");
		        }
		  }); 
		}
		
		function queryAllInfo(c){
			var obj=$(c).parent().parent().parent();
			var selCty='${amazonPostsDetail.country}';
			
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/amazonPortsDetail/getAllContent',  
		        dataType:"json",
		        data : "accountName="+ $(c).parent().find("[name='copyCountry']").val()+"&asin="+$(c).val(),  
		        async: true,
		        success : function(msg){
		        	var uri_encoded = html_decode(html_decode(html_decode(html_decode(msg.title))));
       			    encoded =htmlDecode(uri_encoded);
       			  
		        	//obj.find("[name='title']").val(msg.title);
		        	obj.find("[name='title']").val(encoded);
		        	obj.find("[name='title']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.title.length+"</font>");
		        	
		         	if(obj.find("[name='asin']").val()==''&&(selCty=='de'||selCty=='fr'||selCty=='es'||selCty=='it'||selCty=='uk')){
		        		obj.find("[name='asin']").val(msg.asin);
		        	} 
		        	
		        	//obj.find("[name='brand']").val(msg.brand);		
		        	//obj.find("[name='partNumber']").val(msg.partNumber);
		        	
		        	var desc = html_decode(html_decode(html_decode(html_decode(msg.description))));
       			    obj.find("[name='description']").data("editor").setData(htmlDecode(desc));
		        	//obj.find("[name='description']").data("editor").setData(msg.description);
		        	
		        	var editor = obj.find("[name='description']").data("editor");
					editor.document.on("keyup", function(e){
		        		  obj.find("[name='description']").parent().find(".showMsg").html("<font color='red'>字符长度："+editor.getData().length+"</font>");
					});  
					obj.find("[name='description']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.description.length+"</font>");
						
					desc = html_decode(html_decode(html_decode(html_decode(msg.feature1))));
					obj.find("[name='feature1']").val(htmlDecode(desc));
					obj.find("[name='feature1']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.feature1.length+"</font>");
					desc = html_decode(html_decode(html_decode(html_decode(msg.feature2))));
					obj.find("[name='feature2']").val(htmlDecode(desc));
					obj.find("[name='feature2']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.feature2.length+"</font>");
					desc = html_decode(html_decode(html_decode(html_decode(msg.feature3))));
					obj.find("[name='feature3']").val(htmlDecode(desc));
					obj.find("[name='feature3']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.feature3.length+"</font>");
					desc = html_decode(html_decode(html_decode(html_decode(msg.feature4))));
					obj.find("[name='feature4']").val(htmlDecode(desc));
					obj.find("[name='feature4']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.feature4.length+"</font>");
					desc = html_decode(html_decode(html_decode(html_decode(msg.feature5))));
					obj.find("[name='feature5']").val(htmlDecode(desc));
					obj.find("[name='feature5']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.feature5.length+"</font>");
					desc = html_decode(html_decode(html_decode(html_decode(msg.keyword1))));
					obj.find("[name='keyword1']").val(htmlDecode(desc));
					obj.find("[name='keyword1']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.keyword1.length+"</font>");
					desc = html_decode(html_decode(html_decode(html_decode(msg.keyword2))));
					obj.find("[name='keyword2']").val(htmlDecode(desc));
					obj.find("[name='keyword2']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.keyword2.length+"</font>");
					desc = html_decode(html_decode(html_decode(html_decode(msg.keyword3))));
					obj.find("[name='keyword3']").val(htmlDecode(desc));
					obj.find("[name='keyword3']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.keyword3.length+"</font>");
					desc = html_decode(html_decode(html_decode(html_decode(msg.keyword4))));
					obj.find("[name='keyword4']").val(htmlDecode(desc));
					obj.find("[name='keyword4']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.keyword4.length+"</font>");
					desc = html_decode(html_decode(html_decode(html_decode(msg.keyword5))));
					obj.find("[name='keyword5']").val(htmlDecode(desc));
					obj.find("[name='keyword5']").parent().find(".showMsg").html("<font color='red'>字符长度："+msg.keyword5.length+"</font>");
					obj.find("[name='manufacturer']").val(msg.manufacturer);
					obj.find("[name='packageLength']").val(msg.packageLength);
					obj.find("[name='packageWidth']").val(msg.packageWidth);
					obj.find("[name='packageHeight']").val(msg.packageHeight);
					obj.find("[name='packageWeight']").val(msg.packageWeight);
					//obj.find("[name='catalog1']").val(msg.catalog1);
					//obj.find("[name='catalog2']").val(msg.catalog2);
		        }
			});    
		}
		
		function showQuantity(c){
			var type=$(c).val();
			if(type=='0'){
				$(c).parent().parent().parent().find(".localQuantity").show();
				$(c).parent().parent().parent().find("input[name='quantity']").show();
			}else{
				$(c).parent().parent().parent().find(".localQuantity").hide();
				$(c).parent().parent().parent().find("input[name='quantity']").hide();
			}
		}
		
		function setSameValue(obj){
			var sku=$(obj).val();
			var asin=$(obj).find("option:selected").attr("asinVal");
			var country='${amazonPostsDetail.country}';
			$(obj).parent().parent().parent().find("[name='asin']").val(asin);
			if(country=='uk'){
				var type=$(obj).parent().parent().parent().find("[name='power']").val();
				if(type=='0'){
					$(obj).parent().parent().parent().find("[name='sku']").val(sku.substring(0,sku.lastIndexOf("-"))+"-UK");
				}else{
					$(obj).parent().parent().parent().find("[name='sku']").val(sku);
				}
			}else{
				if(country=='de'||country=='es'||country=='it'||country=='fr'){
					$(obj).parent().parent().parent().find("[name='sku']").val(sku);
				}else{
					$(obj).parent().parent().parent().find("[name='sku']").val(sku.substring(0,sku.lastIndexOf("-"))+"-"+country.toUpperCase());
				}
			}
			
			//$(obj).parent().parent().parent().find("[name='partNumber']").val(sku.substring(sku.indexOf("-")+1,sku.lastIndexOf("-")));
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
				var country='${amazonPostsDetail.country}';
				if(country.indexOf('com')>0||country=='ca'){
					partNumber=partNumber+"-US";
				}else if(country=='jp'){
					partNumber=partNumber+"-JP";
				}else if(country=='mx'){
					partNumber=partNumber+"-MX";
				}
				if(country=='uk'){
					var type=$(obj).parent().parent().parent().find("[name='power']").val();
					if(type=='0'){
						partNumber=partNumber+"-UK";
					}
				}	
				$(obj).parent().parent().parent().find("[name='partNumber']").val(partNumber);	
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
							 $("#"+id+"  ."+cntName.substring(0,cntName.length-1)+i).remove();
						 }
						 $("#"+id+"  ."+cntName.substring(0,cntName.length-1)).remove();
					}else if(cntName=='packageLength'||cntName=='packageWidth'||cntName=='packageHeight'||cntName=='packageWeight'){
						$("#"+id+"  .packageLength").remove();
						$("#"+id+"  .packageWidth").remove();
						$("#"+id+"  .packageHeight").remove();
						$("#"+id+"  .packageWeight").remove();
						$("#"+id+"  .package").remove();
					}else if(cntName=='description'){
						$("#"+cntName+id.split("_")[1]).data("editor").destroy();
						 $("#"+id+"  ."+cntName).remove();
					}else if(cntName=='salePrice'){
						 $("#"+id+"  ."+cntName).remove();
					}else{
					   $("#"+id+"  ."+cntName).remove();
					}
				}
			},{buttonsFocus:1,persistent: true});
			return false;
		}
       
       function changePowerType(c){
			var type=$(c).val();//<option value="0">带电源</option>
			var sku=$(c).parent().parent().parent().find("[name='sku']").val();
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
				if(type=='0'){
					$(c).parent().parent().parent().find("[name='asin']").val('');
					$(c).parent().parent().parent().find("[name='partNumber']").val(partNumber+"-UK");
					$(c).parent().parent().parent().find("[name='sku']").val(sku.substring(0,sku.lastIndexOf("-"))+"-UK");
				}else{
					$(c).parent().parent().parent().find("[name='partNumber']").val(partNumber);
					$(c).parent().parent().parent().find("[name='sku']").val(sku.substring(0,sku.lastIndexOf("-"))+"-DE");
					$(c).parent().parent().parent().find("[name='asin']").val('');
				}
			}
			
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
		  <li  class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=2">复制帖</a></li>
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
		<input  type='hidden'  name='operateType' value='6'/>
	
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
	  
	<%--   <div class="control-group">
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
				        
                         <c:if test="${'uk' eq amazonPostsDetail.country}">
			        	     <div class='control-group brand' ><label class='control-label'><b>Power:</b></label>
				        	 <div class='controls'>
				        	 <select onchange='changePowerType(this)' style="width:50%" name='power'>
				        	      <option value="">请选择</option>
							      <option value="0">带电源&键盘</option>
							      <option value="1">不带电源</option>
							  </select>&nbsp;&nbsp;
				        	 &nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp;</div></div>
			        	 </c:if>
                         <c:if test="${fn:contains('de,fr,it,es,uk',amazonPostsDetail.country) }">
                            <div class='control-group'><label class='control-label'><b>源Asin:</b></label>
				        	  <div class='controls'>
				        	      <select  style='width:25%' onchange='setSameValue(this)'>
				        	           <option value="" selected="selected">-可选择Sku,快捷生成sku和asin-</option>
				        	           
								       <c:forEach items='${amazonProductList}' var='product'>
								            
								              <option value='${product.sku }' eanVal='${product.ean }' asinVal='${product.asin }'>${product.sku }</option>
								            
								       </c:forEach>
								  </select>
				        	  </div>
				        	 </div>
                         </c:if>
                       
                         <div class='control-group sku'><label class='control-label'><b>Sku:</b></label>
			        	  <div class='controls'><input  type='text'  name='sku'  onblur='validSku(this)' class="required" style='width:200px;'/>&nbsp;&nbsp;<span style='color:red;display:inline'>*</span>&nbsp;&nbsp; <div class='showMsg'></div></div>
			        	 </div>
			        	  
			        	 
			        	  
			        	  <div class='control-group eanOrAsin' ><label class='control-label'><b>EanOrAsin:</b></label>
			        	 <div class='controls'>
			        	      <select onchange='changeName(this)' style="width:10%">
			        	          <option value="asin" >Asin</option>
							      <option value="ean" >Ean</option>
							     
							   </select>&nbsp;&nbsp;
			        	      <input  type='text'  name='asin' class="required" style='width:200px;'/>
			        	      &nbsp;&nbsp;<span style='color:red;display:inline'>*</span>
			        	      &nbsp;&nbsp;</div></div>
			        	      
			        	  <div class='control-group sku'><label class='control-label'><b>Type:</b></label>
			        	  <div class='controls'>
			        	      <select name='isFba' style="width:10%" onchange='showQuantity(this)'>
			        	      
							      <option value="1" >FBA帖</option>
							      <c:if test="${'de' eq amazonPostsDetail.country||'com' eq amazonPostsDetail.country}">
							         <option value="0" >本地帖</option>
							      </c:if>
							   </select>
							  
			        	  </div>
			        	 </div>
			        	 
			        	  <div style='display:none;' class='control-group localQuantity'><label class='control-label'><b>库存数:</b></label>
			        	    <div class='controls'>
			        	      <input type='text' style='display:none' name='quantity' class='number'/>
			        	    </div>
			        	 </div>     
			        	      
			        	 <div class='control-group sku'><label class='control-label'><b>复制Sku:</b></label>
			        	  <div class='controls'>
							      <select name='copyCountry' onchange='getCountrySku(this)' style="width:10%;"  >
								       <option value="" selected="selected">-请选择平台-</option>
								       <c:if test="${!fn:contains('com,ca,uk,mx,es',amazonPostsDetail.country)}">
								          <option value="${amazonPostsDetail.accountName }">${amazonPostsDetail.accountName}</option>
								       </c:if>
								        <c:if test="${fn:contains('com,ca,uk',amazonPostsDetail.country)}">
									       <c:forEach items="${accountMap['com']}" var="account">
									            <option value="${account }" >${account }</option>
									       </c:forEach>
									        <c:forEach items="${accountMap['uk']}" var="account">
									            <option value="${account }" >${account }</option>
									       </c:forEach>
									        <c:forEach items="${accountMap['ca']}" var="account">
									            <option value="${account }" >${account }</option>
									       </c:forEach>
								       </c:if>
								       <c:if test="${fn:contains('mx,es',amazonPostsDetail.country)}">
									        <c:forEach items="${accountMap['es']}" var="account">
									            <option value="${account }" >${account }</option>
									       </c:forEach>
									        <c:forEach items="${accountMap['mx']}" var="account">
									            <option value="${account }" >${account }</option>
									       </c:forEach>
								       </c:if>
								       
							      </select> 
							      
							      <select  name='copySku' style="width: 220px;" onchange="queryAllInfo(this)">
							          <option value="" selected="selected">-请选择Sku-</option>
							      </select> &nbsp;&nbsp;
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
						   <div class='controls'><textarea class='destroyArea' id='description0' name='description'  style='float:left;margin: 0px; width:90%; height:407px;'></textarea>
						   &nbsp;&nbsp;<a  class='remove-row' onclick="removecontent('c_0','description')"><span class='icon-minus'></span></a><div class='showMsg'></div>
						   
						 </div></div>
						
						
					      <div class='control-group feature1' ><label class='control-label'><b>BulletPoint1:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature1'   style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick="removecontent('c_0','feature1')"><span class='icon-minus'></span></a><div class='showMsg'></div></div></div>
		        		  <div class='control-group feature2' ><label class='control-label'><b>BulletPoint2:</b></label>
		        		  <div class='controls'><input type='text' onkeyup='getLen(this)'  name='feature2' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature3' ><label class='control-label'><b>BulletPoint3:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature3'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature4' ><label class='control-label'><b>BulletPoint4:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature4'   style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group feature5' ><label class='control-label'><b>BulletPoint5:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='feature5' style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
						   
				         
						
						 <div class='control-group keyword1' ><label class='control-label'><b>SearchTerms1:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword1'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick="removecontent('c_0','keyword1')"><span class='icon-minus'></span></a><div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword2' ><label class='control-label'><b>SearchTerms2:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword2'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword3' ><label class='control-label'><b>SearchTerms3:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword3'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword4' ><label class='control-label'><b>SearchTerms4:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword4'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
		        		  <div class='control-group keyword5' ><label class='control-label'><b>SearchTerms5:</b></label>
		        		  <div class='controls'><input  type='text' onkeyup='getLen(this)'  name='keyword5'  style='width:650.063px;height:19px;'/>&nbsp;&nbsp;<div class='showMsg'></div></div></div>
						
						
						 
			        	 <div class='control-group manufacturer' ><label class='control-label'><b>Manufacturer:</b></label>
			        	 <div class='controls'><input  type='text' name='manufacturer'  style='width:200px;'/>&nbsp;&nbsp;<a  class='remove-row' onclick="removecontent('c_0','manufacturer')"><span class='icon-minus'></span></a></div></div>
			        	 
						 <div class='control-group packageLength' ><label class='control-label'><b>PackageLength:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageLength'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;<a  class='remove-row' onclick="removecontent('c_0','packageLength')"><span class='icon-minus'></span></a></div></div>
			        	 <div class='control-group packageWidth' ><label class='control-label'><b>PackageWidth:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageWidth'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>
			        	 <div class='control-group packageHeight' ><label class='control-label'><b>PackageHeight:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageHeight'  style='width:100px;'/><span class='add-on'>inches</span></div>&nbsp;&nbsp;</div></div>
			        	 <div class='control-group packageWeight' ><label class='control-label'><b>PackageWeight:</b></label>
			        	 <div class='controls'><div class='input-prepend input-append'><input class='price' type='text'  name='packageWeight'  style='width:100px;'/><span class='add-on'>pounds</span></div>&nbsp;&nbsp;</div></div>
			        	
			        	 <div class='control-group catalog1' ><label class='control-label'><b>RecommendedBrowseNode1:</b></label>
			        	 <div class='controls'><input  type='text' class='required'  name='catalog1'  id='catalog1c_0'  style='width:200px;' />&nbsp;&nbsp;<input type='button' onclick="openTree('catalog1c_0','${amazonPostsDetail.country}','c_0')" value='目录搜索'/>&nbsp;&nbsp;<a  class='remove-row' onclick="removecontent('c_0','catalog1')"><span class='icon-minus'></span></a></div></div>
			        	<c:if test="${!fn:startsWith(amazonPostsDetail.country,'com')}">
			        	 <div class='control-group catalog2' ><label class='control-label'><b>RecommendedBrowseNode2:</b></label>
			        	<div class='controls'><input  type='text' class='required'  name='catalog2'  id='catalog2c_0'  style='width:200px;' />&nbsp;&nbsp;<input type='button' onclick="openTree2('catalog2c_0','${amazonPostsDetail.country}','c_0')" value='目录搜索'/>&nbsp;&nbsp;<a  class='remove-row' onclick="removecontent('c_0','catalog2')"><span class='icon-minus'></span></a></div></div>
			        	
			        	</c:if> 
			        	  <script type="text/javascript">
				        	  var editor =  CKEDITOR.replace("description0",{height:'440px',width:'90%',toolbarStartupExpanded:true,startupFocus:false});
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