<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Product</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			float: right;
			min-height: 40px
		}
		
		.spanexl {
			float: left;
		 }
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
		}
		#editPartsModal table td {
		     text-align:center;
		     height:40px; line-height:40px; 
		}
		#editPartsModal table th {
		     text-align:center;
		      height:30px; line-height:30px; 
		}
		 #imgtest{  position:absolute;
	         top:100px; 
	         left:200px; 
	         z-index:1; 
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
		
		$(function(){

			$(".img-thumbnail").mouseover(function(e) { 
				if($(this).is("img")){ 
					var imgSrc =$(this).attr("src").replace("/compressPic","");
					var img=$("<img id='tipImg' src='"+imgSrc+"'>").css({ "height":$(this).height()*10, "width":$(this).width()*10	});
					img.appendTo($("#imgtest"));
				}
			});
			
			$(".img-thumbnail").mouseout(function() { 
				$("#tipImg").remove();
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$(".signedSample").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.productId = $(this).parent().find(".productId").val();
					if(newValue){
						param.signedSample =  encodeURI(newValue);
						$.get("${ctx}/psi/product/signedSample?"+$.param(param),function(data){
							if(!(data)){    
								$this.text(oldVal);						
							}else{
								$.jBox.tip("保存签样成功！", 'info',{timeout:2000});
							}
						});
					}
					return true;
				}
			});
			
			$("#contentTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				<c:if test="${'1' eq isNewAdd}">
				 "aaSorting": [[ 9, "desc" ]]
				</c:if>
				<c:if test="${'1' ne isNewAdd}">
				 "aaSorting": [[ 0, "desc" ]]
				</c:if>
			});
			
			$("#contentTbDiv div div:first").append($("#searchDiv").html());
			
			
			$("#btnExport").live("click",function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						var isNewAdd = $(".isNewAdd").val();
						window.location.href="${ctx}/psi/product/exportProduct?isNewAdd="+isNewAdd;
						//$("#searchForm").attr("action","${ctx}/psi/product/exportProduct?isNewAdd="+isNewAdd);
						//$("#searchForm").submit();
						//$("#searchForm").attr("action","${ctx}/psi/product");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#export2").live("click",function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/productEliminate/exportProductDetail");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/psi/product");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#exportPrice").live("click",function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/product/exportPrice");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/psi/product");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#saveParts").on("click",function(){
				$("#editPartsTable tbody tr ").each(function(){
					var param = {};
					param.productId=$(this).find("input[name='productId']").val();
					param.color=$(this).find(".color").text();
					param.parts=$(this).find("select[name='parts']").val();
					$.get("${ctx}/psi/product/editParts?"+$.param(param,true),function(){});
					top.$.jBox.tip("保存成功！","info");
					$(".close").click();
				});
				
			});
			
			$("#uploadTypeFile").click(function(e){
				   var filePath = $("#uploadFileName").val();
				   var type=$("select#uploadType").val();
				   if(filePath==""){
						$.jBox.error('上传文件名为空'); 
						return;
				   }
				   
				   if((type=="30"||type=="31")&&escape(filePath).indexOf( "%u" )>=0){
						$.jBox.error("文件名不能包含中文！");
						return;
				   }
				   
				   $("#uploadTypeFile").attr("disabled",true);
				   var formdata = new FormData($("#uploadForm")[0]);              
				   $.ajax({  
		                url :$("#uploadForm").attr("action"),  
		                type : 'post',  
		                data : formdata,  
		                processData : false,  
		                contentType : false,  
		                success : function(responseStr) { 
		                	$.jBox.tip('文件上传成功'); 
		                	$("#uploadFileName").val("");
		                	$("#uploadTypeFile").attr("disabled",false);
		                	$("#buttonClose").click();
		                },  
		                error : function(responseStr) {  
		                	$.jBox.tip('文件上传失败'); 
		                	$("#uploadTypeFile").attr("disabled",false);
		                }  
		            });  
			});
			
			$(".ExpSummary").click(function(){
				var type = $(this).attr("typeVal");
				var options="";
				<c:forEach items='${lineList}' var='line'>
				   options+="<option value='${line.id}'>${line.name}</option>";
				</c:forEach>
				
				top.$.jBox.confirm("<select name='line' id='line' ><option value=''>-All-</option>"+options+"</select>", "Select ProductLine", function(v, h, f){
					  if (v == 'ok'){
						  	var params = {};
						  	console.log(1);
						  	params.line = h.find("#line").val();
						  	params.type = type;
						
						  	window.location.href = "${ctx}/psi/product/downloadZipFile?"+$.param(params);
							top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:5000});
					  }
					  return true; //close
				});
				
			});
			
			$(".isNewAdd").on("click",function(){
				if(this.checked){
					$(".isNewAdd").val("1");
				}else{
					$(".isNewAdd").val("0");
				}
				var value = $(".isNewAdd").val();
				window.location.href="${ctx}/psi/product?isNewAdd=" + value;
			});
			

			
		});
		
		function editParts(productId,productName,colorStr){
			//通过ajax获取sku选择框及库存数据
			var colors = {};
			$.ajax({
			    type: 'post',
			    async:false,
			    url: '${ctx}/psi/product/ajaxPartsData',
			    data: {
			    	"productId":productId,
			    	"colorStr":colorStr,
			    },
			    dataType: 'json',
			    success:function(data){ 
			    	if(data.msg=="true"){
			    		colors=data.colors;
			    	}
		        }
			});
			
			var table =$("#editPartsModal table");
			table.find("tbody tr").each(function(){
				$(this).remove();
			});
			$("#title").text(productName);
			for(var j in colors){
				console.log(colors[j]);
				var partsData=colors[j].items;
				var selectedPartsData=colors[j].selectedIds;
				var partsOpts="";
				for(var i=0;i<partsData.length;i++){
					partsOpts=partsOpts+"<option value='"+partsData[i].partsId+"'>"+partsData[i].partsName+"</option>";
				}
				var tr=$("<tr><td><input type='hidden' name='productId' value='"+productId+"'/><span class='color'>"+colors[j].color+"<span></td><td><select name='parts' multiple=true style='width:90%' class='multiSelect partsInfo'>"+partsOpts+"</select></td></tr>");
				for(var i =0;i<selectedPartsData.length;i++){
					 tr.find("select[name='parts'] option[value='"+selectedPartsData[i].partsId+"']").attr("selected","selected");	
				}
				tr.find(".partsInfo").select2();  
				table.find("tbody").append(tr);
			}
			
			$("#editPartsModal").modal();
		};
		
		
		
				
		function updateState(obj,id,stateStr){
			if($(obj).attr("checked")){
				stateStr = stateStr+"=1";
			}else{
				stateStr = stateStr+"=0";
			}
			stateStr = stateStr+"&id="+id;
			$.get("${ctx}/psi/product/updateProductState?"+stateStr,function(data){
				if(data){
					top.$.jBox.tip(data);
				}
			});
		}
		
	
		function updateHscode(productId,productName,euHscode,usHscode,caHscode,jpHscode,mxHscode,hkHscode,cnHscode,euImportDuty,usImportDuty,caImportDuty,jpImportDuty,mxImportDuty,euCustomDuty,usCustomDuty,caCustomDuty,jpCustomDuty,mxCustomDuty,taxRefund){
			var html="<div class='showChildrenHtml' style='text-align:left;margin-left:5px;' ><table style='width:98%;margin-top:10px'  class='table table-bordered'><thead><tr><th style='width: 150px'>product_name</th><th style='width: 150px'>eu_hscode</th><th style='width: 150px'>ca_hscode</th><th style='width: 150px'>jp_hscode</th><th style='width: 150px'>mx_hscode</th><th style='width: 150px'>us_hscode</th><th style='width: 150px'>hk_hscode</th><th style='width: 150px'>cn_hscode</th></tr></thead><tbody>";
			
		    html=html+"<tr><td style='text-align: center;vertical-align: middle;'>"+productName+"</td><td ><input style='width:90%;' type='text' class='euHscode'  value='"+euHscode+"'/></td><td ><input style='width:90%;' type='text'  class='caHscode'  value='"+caHscode+"'/></td><td><input style='width:90%;' type='text'  class='jpHscode'  value='"+jpHscode+"'/></td><td><input style='width:90%;' type='text'  class='mxHscode'  value='"+mxHscode+"'/></td><td><input style='width:90%;' type='text'  class='usHscode'  value='"+usHscode+"'/></td><td><input style='width:90%;' type='text'  class='hkHscode'  value='"+hkHscode+"'/></td><td><input style='width:90%;' type='text'  class='cnHscode'  value='"+cnHscode+"'/></td></tr>";
		    html=html+"<tr><td style='text-align: center;vertical-align: middle;'>进口税率</td><td ><input style='width:90%;' type='text' class='euImportDuty'  value='"+euImportDuty+"'/></td><td><input style='width:90%;' type='text'  class='caImportDuty'  value='"+caImportDuty+"'/><td ><input style='width:90%;' type='text'  class='jpImportDuty'  value='"+jpImportDuty+"'/></td><td ><input style='width:90%;' type='text'  class='mxImportDuty'  value='"+mxImportDuty+"'/></td></td><td><input style='width:90%;' type='text'  class='usImportDuty'  value='"+usImportDuty+"'/></td><td><input style='width:90%;' type='text' readonly /></td><td><input style='width:90%;' type='text' readonly/></td></tr>";
		    html=html+"<tr><td style='text-align: center;vertical-align: middle;'>关税税率</td><td ><input style='width:90%;' type='text' class='euCustomDuty'  value='"+euCustomDuty+"'/></td><td><input style='width:90%;' type='text'  class='caCustomDuty'  value='"+caCustomDuty+"'/></td><td ><input style='width:90%;' type='text'  class='jpCustomDuty'  value='"+jpCustomDuty+"'/></td><td ><input style='width:90%;' type='text'  class='mxCustomDuty'  value='"+mxCustomDuty+"'/></td><td><input style='width:90%;' type='text'  class='usCustomDuty'  value='"+usCustomDuty+"'/></td><td><input style='width:90%;' type='text' readonly /></td><td><input style='width:90%;' type='text' readonly/></td></tr>";
		    html=html+"<tr><td style='text-align: center;vertical-align: middle;'>退税税率</td><td style='background-color: #f3f3f3'/><td style='background-color: #f3f3f3'/><td style='background-color: #f3f3f3'/><td style='background-color: #f3f3f3'/><td style='background-color: #f3f3f3'/><td style='background-color: #f3f3f3'/><td><input style='width:90%;' type='text' class='taxRefund'  value='"+taxRefund+"'/></td></tr>";
			
			html=html+"</tbody></table></div><br/>";
			

			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/psi/product/getHistoryHscodeById',  
		        dataType:"json",
		        data : 'productId='+productId,  
		        async: false,
		        success : function(msg){
		        	if(msg!=null&&msg!=''){
		        		html+= "<div class='showChildrenHtml' style='text-align:center;margin-left:10px;' ><table style='width:98%;margin-top:10px'  class='table table-striped table-bordered table-condensed'><thead><tr><th style='width: 150px'>update_date</th><th style='width: 150px'>eu_hscode</th><th style='width: 150px'>ca_hscode</th><th style='width: 150px'>jp_hscode</th><th style='width: 150px'>us_hscode</th><th style='width: 150px'>hk_hscode</th><th style='width: 150px'>cn_hscode</th></tr></thead><tbody>"; 
			            for(var i=0;i<msg.length;i++){
		            	   html=html+"<tr><td style='text-align: center;vertical-align: middle;'>"+msg[i].formatDate+"</td><td>"+msg[i].euHscode+"</td><td >"+msg[i].caHscode+"</td><td>"+msg[i].jpHscode+"</td><td>"+msg[i].usHscode+"</td><td>"+msg[i].hkHscode+"</td><td>"+msg[i].cnHscode+"</td></tr>";
		                }
			            html=html+"</tbody></table></div>";
		        	}
		        }
		    }); 
			
			top.$.jBox.confirm(html,"编辑HSCODE", function(v,h,f){
				if(v=='ok'){
						var param = {};
						param.euHscode = h.find(".euHscode").val();
						param.usHscode =h.find(".usHscode").val();
						param.jpHscode =h.find(".jpHscode").val();
						param.caHscode = h.find(".caHscode").val();
						param.hkHscode = h.find(".hkHscode").val();
						param.cnHscode = h.find(".cnHscode").val();
						param.mxHscode = h.find(".mxHscode").val();
						
						param.euImportDuty = h.find(".euImportDuty").val();
						param.usImportDuty =h.find(".usImportDuty").val();
						param.caImportDuty =h.find(".caImportDuty").val();
						param.jpImportDuty = h.find(".jpImportDuty").val();
						param.mxImportDuty = h.find(".mxImportDuty").val();
						param.euCustomDuty = h.find(".euCustomDuty").val();
						param.usCustomDuty =h.find(".usCustomDuty").val();
						param.caCustomDuty =h.find(".caCustomDuty").val();
						param.jpCustomDuty = h.find(".jpCustomDuty").val();
						param.mxCustomDuty = h.find(".mxCustomDuty").val();
						param.taxRefund = h.find(".taxRefund").val();
						param.productId = productId;
						$.get("${ctx}/psi/product/updateHscode?"+$.param(param),function(){
							top.$.jBox.tip("保存成功！","info");
							$("#searchForm").submit();
						});
		            return true;
				}
			},{buttonsFocus:1,width:980,showClose: true,persistent: true});
		}
		
		function updateProductName(productId,productName){
			var html="<div class='showChildrenHtml' style='text-align:center;margin-left:10px;' ><table style='width:98%;margin-top:10px'  class='table table-striped table-bordered table-condensed'><thead><tr><th style='text-align: center;vertical-align: middle;'>product_name</th><th>中英文名称</th></tr></thead><tbody>";
		    html=html+"<tr><td style='text-align: center;vertical-align: middle;'>"+productName+"</td><td ><input style='width:90%;' type='text' class='name'/></td></tr>";
			
			html=html+"</tbody></table></div>";
			top.$.jBox.confirm(html,"编辑产品中英文名称", function(v,h,f){
				if(v=='ok'){
						var param = {};
						param.chineseName=encodeURI(h.find(".name").val());
						param.productId = productId;
						if(h.find(".name").val()==''){
							top.$.jBox.tip("产品中英文名称不能为空","info");
							return;
						}
						if(h.find(".name").val().match(/[\uff00-\uffff]/g)){
							$.jBox.tip("中英文名称不能输入全角字符，请切换到英文输入模式");
							return;
						}else{
							$.get("${ctx}/psi/product/updateChineseName?"+$.param(param),function(){
								top.$.jBox.tip("保存成功！","info");
								//$("#searchForm").submit();
							});
						}
		            return true;
				}
			},{buttonsFocus:1,width:750,showClose: true,persistent: true});
		};
		
		 function uploadFile(id,hasElec){
			 $("#productId").val(id);
		     $("#uploadForm").attr("action","${ctx}/psi/product/upload");
		     var optionStr = "";
		     optionStr = "<option value='0'>CE</option><option value='1>ROHS</option><option value='2'>FCC</option><option value='3'>FDA</option><option value='4'>BQB</option><option value='5'>UL</option><option value='6'>PSE</option><option value='20'>TELEC</option><option value='21'>ETL</option>";
		     if(hasElec=='1'){
		    	 optionStr=optionStr+"<option value='7'>MSDS</option><option value='8'>UN38.3</option><option value='9'>1.2米跌落测试报告</option><option value='10'>空运报告</option><option value='11'>海运报告</option><option value='12'>SP188</option>";
		     }
		     optionStr=optionStr+"<option value='31'>技术规格书</option>";
		     optionStr=optionStr+"<option value='32'>BOM LIST</option>";
		     $("#uploadType").empty();
		     $("#uploadType").append(optionStr).select2();
		  };
		 
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		function downloadFile(fileName){
			var name=encodeURI(encodeURI(fileName));
			window.location.href= "${ctx}/psi/product/download1?fileName="+name;
			
		}
	</script>
	
	<meta http-equiv="pragma" content="no-cache"> 
     <meta http-equiv="cache-control" content="no-cache"> 
     <meta http-equiv="expires" content="0">   
	
	<% 
		response.setHeader("Cache-Control","no-store"); 
		response.setHeader("Pragrma","no-cache"); 
		response.setDateHeader("Expires",0); 
		%> 
		
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/psi/product/list"><spring:message code="psi_product_list"/></a></li>
		<shiro:hasPermission name="psi:product:edit"><li><a href="${ctx}/psi/product/add">新增产品</a></li></shiro:hasPermission>
		<li><a  href="${ctx}/psi/product/showHscodes"><spring:message code="psi_product_hscode"/></a></li>
		<shiro:hasPermission name="psi:transport:view"><li><a  href="${ctx}/psi/product/showTranMoney"><spring:message code="psi_product_transportFee"/></a></li></shiro:hasPermission>
		<shiro:hasAnyPermissions name="psi:product:viewPrice,psi:product:tieredPrice">
			<li><a  href="${ctx}/psi/productTieredPrice/">产品阶梯价格</a></li>
		</shiro:hasAnyPermissions>
		<li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#"><spring:message code="psi_product_otherManger"/><b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
		       <li><a href="${ctx}/psi/productEliminate">产品定位管理</a></li>
		       <li><a href="${ctx}/psi/productEliminate/isNewlist">新品明细</a></li>
		       <li><a href="${ctx}/psi/productEliminate/addedMonthlist"><spring:message code="psi_product_shelvesTime"/></a></li>
		       <li><a href="${ctx}/psi/productEliminate/forecastlist">销售预测方案</a></li>
		       <li><a href="${ctx}/psi/psiProductAttribute">产品分颜色属性</a></li>
		       <shiro:hasPermission name="psi:transport:edit">
		          <li><a href="${ctx}/psi/productEliminate/piPrice">产品运单PI价格明细</a></li>
		       </shiro:hasPermission>
		       <li><a href="${ctx}/amazoninfo/amazonPortsDetail/findProductTypeChargeList">品类佣金</a></li>
		    </ul>
	   </li>
	</ul>
	<div id="searchDiv" style="display: none">
	<form:form id="searchForm" modelAttribute="psiProduct" action="${ctx}/psi/product" method="post" >
		<div style="vertical-align: middle;">
			 <div class="btn-group">
			   <button type="button" class="btn btn-primary"><spring:message code="sys_but_export"/></button>
			   <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
			      <span class="caret"></span>
			      <span class="sr-only"></span>
			   </button>
			   <ul class="dropdown-menu">
			   	  <li><a id="btnExport">导出产品信息</a></li>	
			   	  <li><a id="export2">分平台属性导出</a></li>
			   	  <shiro:hasPermission name="psi:product:viewPrice">
			   	      <li><a id="exportPrice">导出价格</a></li>
			   	  </shiro:hasPermission>
			   	  <li><a class="ExpSummary"  typeVal='0'>Check List</a></li>
			   	  <li><a class="ExpSummary"  typeVal='1'>技术规格书</a></li>
			   	  <li><a class="ExpSummary"  typeVal='2'>Bom List</a></li>
			   </ul>
			</div>
			 <span ><shiro:hasPermission name="psi:product:dictionaryEdit"> 
			 <a class="btn btn-primary" href="${ctx}/psi/product/listDict">产品基本属性维护</a>
			 </shiro:hasPermission></span>
			 <input type="checkbox" class="isNewAdd" name="isNewAdd" id="isNewAdd" value="${isNewAdd}" ${isNewAdd eq '1'?'checked':''}/>最新上架产品
		</div>
	</form:form>
	</div>
	<tags:message content="${message}"/>
	<div id="contentTbDiv">
	<div id="imgtest"></div> 
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr>
				   <th style="width:3%;">No.</th>
				   <th style="width:5%"><spring:message code="psi_product_image"/></th>
				   <th style="width:5%"><spring:message code="psi_product_model"/></th>
				   <th style="width:5%"><spring:message code="psi_product_supplier"/></th>
				   <th style="width:5%"><spring:message code="psi_product_type"/></th>
				   <th style="width:5%;">MOQ</th>
				   <th style="width:5%;"><spring:message code="psi_transport_type"/></th>
				   <th style="width:5%"><spring:message code="purchase_manager"/></th>
				   <th style="width:5%;"><spring:message code="psi_product_merchandiser"/></th>
				   <th style="width:5%;"><spring:message code="psi_product_shelvesTime"/></th>
				   <th style="width:5%;"><spring:message code="psi_product_signedSample"/></th>
				   <th style="width:5%;"><spring:message code="psi_product_technicalSpecification"/></th>
				   <th style="width:5%;" class="sort isNew"><spring:message code="psi_product_new"/>  </th>
				   <th style="width:5%;" ><spring:message code="custom_event_form21"/>  </th> 
				   <th style="width:10%"><spring:message code="sys_label_tips_operate"/></th>
				   </tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="product">
			<tr  ${product.isSale eq '4'?'style=background-color:#cccccc':''}>
			<c:set value="${product.id}," var="productStr" />
				<td>${product.id}</td>
				<td>
					<c:if test="${not empty product.image}">
						<%		
								double number=Math.random()*100;
								request.setAttribute("number",number);
						%>
						<img style="width: 50px;height: 50px"  class="img-thumbnail"  src="<c:url value="${product.link}?num=${number}"></c:url>"> 
					</c:if>
				</td>	
				<td><a href="${ctx}/psi/product/view?id=${product.id}">${product.name}</a></td>
				<td>
					${modelAndSupplierMap[product.name]}
				</td>
				<td>${product.type}</td>
				<td>${product.minOrderPlaced}</td>
				<td>
					<c:if test="${'1' eq transportTypeMap[product.name]}">Sea</c:if>
					<c:if test="${'2' eq transportTypeMap[product.name]}">Air</c:if>
					<c:if test="${'3' eq transportTypeMap[product.name]}">Sea/Air</c:if>
					<c:if test="${empty transportTypeMap[product.name]}">Air</c:if>
				</td>
				<td>${purchaseManagerMap[product.type]}</td>
				<td>${product.createUser.name}</td>
				<td>
					<c:if test="${not empty  product.addedMonth}">${fn:replace(product.addedMonth,' 00:00:00','')}</c:if>
				</td>
				<td>
				<input type="hidden" class="productId" value="${product.id}" />
					<shiro:hasPermission name="psi:product:edit">
							<c:choose>
								<c:when test="${(fns:getUser().id eq product.createUser.id)||(fns:getUser().id eq product.createUser1.id)||fn:contains(canEditIds,productStr)}">
									<a href="#" class="signedSample"  data-type="text" data-pk="1" data-title="Enter Signed Simple" data-value="${product.signedSample}">${product.signedSample}</a>
								</c:when>
								<c:otherwise>
									${product.signedSample}
								</c:otherwise>
							</c:choose>
					</shiro:hasPermission>
				</td>
				<td>
					<c:if test="${not empty product.techFile }">
						<a href="#" onclick="downloadFile('/${product.techFile}')">downLoad</a>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${product.isNew eq '0'}"><spring:message code="psi_product_noNew"/></c:when>
						<c:otherwise><spring:message code="psi_product_new"/></c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${product.reviewSta eq '0'}">
							未审核<br/>
							<shiro:hasPermission name="psi:product:review">
							 <a href="${ctx}/psi/product/review?id=${product.id}"  class="btn btn-small btn-success">审核</a>
							</shiro:hasPermission> 
						</c:when>
						<c:otherwise>
							已审核 
						</c:otherwise>
					</c:choose>
				</td>
				<td>
				<div class="btn-group">
					<shiro:hasPermission name="psi:product:edit">
					   <button type="button" class="btn btn-small">更改</button>
					   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
					      <span class="caret"></span>
					      <span class="sr-only"></span>
					   </button>
					    <ul class="dropdown-menu" >
					    	<li><a onclick="updateHscode(${product.id},'${product.name}','${product.euHscode}','${product.usHscode}','${product.caHscode}','${product.jpHscode}','${product.mxHscode}','${product.hkHscode}','${product.cnHscode}','${product.euImportDuty}','${product.usImportDuty}','${product.caImportDuty}','${product.jpImportDuty}','${product.mxImportDuty}','${product.euCustomDuty}','${product.usCustomDuty}','${product.caCustomDuty}','${product.jpCustomDuty}','${product.mxCustomDuty}','${product.taxRefund}');">编辑HSCODE</a></li>
						
							<c:if test="${(fns:getUser().id eq '1')||(fns:getUser().id eq product.createUser.id)||(fns:getUser().id eq product.createUser1.id)||fn:contains(canEditIds,productStr)}">
								 <li><a href="${ctx}/psi/product/update?id=${product.id}">编辑</a></li>
								 <li><a onclick="editParts('${product.id}','${product.model}','${product.color}')" href="#" >编辑配件</a></li>
								 <li><a href="${ctx}/psi/product/delete?id=${product.id}" onclick="return confirmx('确定要删除该产品吗？', this.href)">删除</a></li>
								 <li><a onclick="updateProductName(${product.id},'${product.name}');">编辑中英文名称</a></li>
							</c:if>
						</ul>
					
					</shiro:hasPermission>
					<shiro:hasPermission name="psi:product:edit">
						<c:if test="${(fns:getUser().id eq product.createUser.id)||(fns:getUser().id eq product.createUser1.id)||fn:contains(canEditIds,productStr)}">
						 	<a href="#updateExcel"  class="btn btn-small" data-toggle="modal" id="uploadFile" onclick="uploadFile('${product.id}','${product.hasElectric}')">上传附件</a>
						</c:if>
					 </shiro:hasPermission>
				</div>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	
		<div id="editPartsModal" class="modal hide fade" style="margin:5x;width:850px;" tabindex="-1" >
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><span id="title"></span>关联配件</h3>
		</div>
		<form id="inputForm" action="${ctx}/psi/product/editParts" method="post">
		<input name="id" type="hidden" id="id"/>
		<div class="modal-body" style="min-height:110px">
			<table class="table table-striped table-bordered table-condensed " id="editPartsTable">
				<thead>
					<tr>
						<th style="width:20%">颜色</th>
						<th style="width:80%">配件</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		<div class="modal-footer">
			<input type="button" class="btn btn-primary " value="保存" id="saveParts">
			<button type="button" data-dismiss="modal" class="btn btn-primary" id="cancelParts">Close</button>
		</div>
		</form>
	</div>
	
	
	  <div id="updateExcel" class="modal hide fade" tabindex="-1" style="width:600px"> 
				 <form  enctype="multipart/form-data" id="uploadForm"  method="post" >
				 	<input type="hidden" name="id" id="productId"/> 
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" >×</button>
						    <h3 id="myModalLabel">文件上传</h3>
						  </div>
						  <div class="modal-body" style="height:60px">
							<label >文件类型：</label>
							<select  id="uploadType" name="uploadType" style="width:200px"></select>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<input id="uploadFileName" name="uploadFile" type="file" />
						  </div>
						   <div class="modal-footer">
						   <button class="btn btn-primary"  type="button" id="uploadTypeFile">上传</button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true">关闭</button>
						  </div> 
					</form>
			 </div>
	
</body>
</html>
