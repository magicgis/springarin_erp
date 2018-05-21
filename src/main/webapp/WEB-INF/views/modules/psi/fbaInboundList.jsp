<%@page import="java.util.Date"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>FBA帖子管理</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="pragma" content="no-cache"> 
	<meta http-equiv="cache-control" content="no-cache"> 
	<meta http-equiv="expires" content="0">  
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$(".tips").popover({html:true,trigger:'hover'});
			
			$("#selectT,#country,input[name='tray'],input[name='responseLevel'],#selectAccount").change(function(){
				$("#searchForm").submit();
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$(".sync").click(function(){
				var tr = $(this).parent().parent().parent().parent().parent();
				if(tr.find(".shipmentId").text()){
					var param = {};
					param.id = tr.find(".id").val();
					tr.find(".statu").text("正在同步AMZ...");
					$.get("${ctx}/psi/fbaInbound/sync?"+$.param(param),function(){
						$("#searchForm").submit();
					});
				}else{
					$.jBox.tip("请同步shipmentId", 'error',{timeout:2000});
				}
				return false;
			});
			
			$(".upload").click(function(){
				var tr = $(this).parent().parent().parent().parent().parent();
				if($.trim(tr.find("td:eq(4)").text())=='Plan'){
					var docAddress = "";
					var title = tr.find("td:eq(1)").text();
					if(title.indexOf('[US]')>=0){
						docAddress = "<br/>3.FBA File Address: <select id='docAddress'><option value='US'>US</option><option value='CN'>CN</option> <select><br/>此发货地址与发货仓库无关，仅仅用来欺骗亚马逊";
					}else if(title.indexOf('[IT]')>=0||title.indexOf('[ES]')>=0||title.indexOf('[FR]')>=0||title.indexOf('[DE]')>=0||title.indexOf('[UK]')>=0){
						docAddress = "<br/>3.FBA File Address: <select id='docAddress'><option value='DE'>DE</option><option value='CN'>CN</option> <select><br/>此发货地址与发货仓库无关，仅仅用来欺骗亚马逊";
					}
					var html = "1.将Plan转化成FBA贴,直接点击确认.<br/>2.将Plan产品同步到已有帖子上,请填写ShipmentID后确认<input id='sid'  style='height: 25px;width:100px' class='span2'  size='16'/>"+docAddress;
					top.$.jBox.confirm(html,'Upload To Amz?',function(v,h,f){
						if(v=='ok'){
							var param = {};
							param.id = tr.find(".id").val();
							param.shipmentId = $.trim(h.find("#sid").val());
							if(h.find("#docAddress").val()){
								param.docAddress = $.trim(h.find("#docAddress").val());
							}
							var flag = 0;
							if(param.shipmentId){
								$(".shipmentId").each(function(){
									if($.trim($(this).text())==param.shipmentId){
										flag=1;								
									}
								});
							}
							if(flag==0){
								param.shipmentStatus="WORKING";
								tr.find(".statu").text("正上传到AMZ...");
								$.get("${ctx}/psi/fbaInbound/upload?"+$.param(param),function(){
									$("#searchForm").submit();
								}); 
							}else{
								tr.find(".statu").text("该帖子已经被系统自动同步下来，不能进行Plan上传了，请重新在后台建贴上传!");
							}
						}
					},{buttonsFocus:1,width:350,showClose: false,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					var param = {};
					param.id = tr.find(".id").val();
					tr.find(".statu").text("正上传到AMZ...");
					$.get("${ctx}/psi/fbaInbound/upload?"+$.param(param),function(){
						$("#searchForm").submit();
					});
				}			
				return false;
			});
			
			$("#export").click(function(){
				top.$.jBox.confirm("导出FBA贴汇总信息吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/fbaInbound/export");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/psi/fbaInbound");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});	
			
			
			$("#SendPlansEmail").click(function(){
				if($(".checked :hidden").size()){
						var params = {};
						params.fbaIds = '';
						$(".checked :hidden").each(function(){
							params.fbaIds += ($(this).val()+',');
						}); 
						window.location.href = "${ctx}/psi/fbaInbound/sendPlanEmail?"+$.param(params);
				}else{
					top.$.jBox.tip("No one yet checked!","error",{persistent:false,opacity:0});
				}
			});
			
			$(".genLabelByDiy").click(function(){
				var tr = $(this).parent().parent().parent().parent().parent();
				var shipmentId = $(this).attr("shipmentIdVal");
				if(tr.find(".shipmentId").text()){
					
					if($(this).attr("key")){
						var html1 = "Cartons Info：<form enctype='multipart/form-data'  method='post'><div class='input-append btn-group btn-input'><input  type='file' name='uploadFile'    size='16'/></div><br/>PageType：<div class='input-append btn-group btn-input'><select id='type' style='height: 25px;width:220px'><option value='PackageLabel_Letter_6'>PackageLabel_Letter_6</option><option value='PackageLabel_A4_4'>PackageLabel_A4_4</option><option value='PackageLabel_Plain_Paper'>PackageLabel_Plain_Paper</option></select></div></form>";
						var html = "Cartons Info：<form enctype='multipart/form-data'  method='post'><div class='input-append btn-group btn-input'><input  type='file' name='uploadFile'   size='16'/></div><br/>PageType：<div class='input-append btn-group btn-input'><select id='type' style='height: 25px;width:220px'><option value='PackageLabel_A4_4'>PackageLabel_A4_4</option><option value='PackageLabel_Plain_Paper'>PackageLabel_Plain_Paper</option></select></div></form>";
						if(tr.find("td:eq(1)").text().indexOf("US")>=0||tr.find("td:eq(1)").text().indexOf("CA")>=0){
							html = html1;
						}
						top.$.jBox.confirm(html,'gen Unique PackageLabel?',function(v,h,f){
							if(v=='ok'){
								var param = {};
								param.id = tr.find(".shipmentId :hidden").val();
								param.type = h.find("#type").val();
								param.shipmentId = shipmentId;
								var formData = new FormData(h.find("form")[0]);  
								
								tr.find(".statu").text("Generating...");
								$.ajax({
			         			   type: "POST",
			         			   url:"${ctx}/psi/fbaInbound/genUniqueLabel?"+$.param(param),
			         			   data:formData,
			         			   processData : false,  
					               contentType : false, 
			         			   success: function(msg){
			         				  $("#sid").val(tr.find(".shipmentId").attr("sid")); 
			         				  $("#searchForm").submit();	
			         			   }
				             	});
								var time=10000;
								if(tr.find("td:eq(1)").text().indexOf("US")>=0){
									time = 100000;
								}
								top.$.jBox.tip('正在上传装箱信息，并生成箱单，请耐心等待1-2分钟...生成后页面会自动刷新','info',{persistent:false,'timeout':time});
							}
						},{buttonsFocus:1,width:350,showClose: false,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
						
					}else{
						$.ajax({
	         			   type: "POST",
	         			   url: "${ctx}/psi/fbaInbound/getPacks?id="+tr.find(".shipmentId :hidden").val(),
	         			   async: false,
	         			   success: function(msg){
	         				   rs = msg;		
	         			   }
	             		});
						var html1 = "Cartons Info：<form enctype='multipart/form-data'   method='post'><div class='input-append btn-group btn-input'><input name='uploadFile'  type='file'    size='16'/></div><br/> Number：<div class='input-append btn-group btn-input'><input id='number' type='number' value='"+rs+"' style='height: 25px;width:100px' class='span2'  size='16'/></div>"+
						"<br/>PageType：<div class='input-append btn-group btn-input'><select id='type' style='height: 25px;width:220px'><option value='PackageLabel_Letter_6'>PackageLabel_Letter_6</option><option value='PackageLabel_A4_4'>PackageLabel_A4_4</option><option value='PackageLabel_Plain_Paper'>PackageLabel_Plain_Paper</option></select></div></form>";
						var html = "Cartons Info：<form enctype='multipart/form-data'   method='post'><div class='input-append btn-group btn-input'><input  type='file' name='uploadFile'  size='16'/></div><br/>Number：<div class='input-append btn-group btn-input'><input id='number' type='number' value='"+rs+"' style='height: 25px;width:100px' class='span2'  size='16'/></div>"+
						"<br/>PageType：<div class='input-append btn-group btn-input'><select id='type' style='height: 25px;width:220px'><option value='PackageLabel_A4_4'>PackageLabel_A4_4</option><option value='PackageLabel_Plain_Paper'>PackageLabel_Plain_Paper</option></select></div></form>";
						if(tr.find("td:eq(1)").text().indexOf("US")>=0||tr.find("td:eq(1)").text().indexOf("CA")>=0){
							html = html1;
						}
						top.$.jBox.confirm(html,'gen PackageLabel?',function(v,h,f){
							if(v=='ok'){
								var number = h.find("#number").val();
								if($.isNumeric(number)&&number>0){
									var param = {};
									param.id = tr.find(".shipmentId :hidden").val();
									param.type = h.find("#type").val();
									param.number = h.find("#number").val();
									tr.find(".statu").text("Generating...");
									param.shipmentId = shipmentId;
									var formData = new FormData(h.find("form")[0]);  
									$.ajax({
				         			   type: "POST",
				         			   url:"${ctx}/psi/fbaInbound/genLabel?"+$.param(param),
				         			   data:formData,
				         			   processData : false,  
						               contentType : false, 
				         			   success: function(msg){
				         				  $("#sid").val(tr.find(".shipmentId").attr("sid")); 
				         				  $("#searchForm").submit();	
				         			   }
					             	});
									var time=10000;
									if(tr.find("td:eq(1)").text().indexOf("US")>=0){
										time = 100000;
									}
									top.$.jBox.tip('正在上传装箱信息，并生成箱单，请耐心等待1-2分钟...生成后页面会自动刷新','info',{persistent:false,'timeout':time});
								}else{
									top.$.jBox.tip("Number Must be a positive integer","error");
									return false;
								}
							}
						},{buttonsFocus:1,width:350,showClose: false,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}
				}else{
					$.jBox.tip("请同步shipmentId", 'error',{timeout:2000});
				}	
				return false;
			});
			

			$(".genLabel").click(function(){
				var tr = $(this).parent().parent().parent().parent().parent();
				var shipmentId = $(this).attr("shipmentIdVal");
				console.log(shipmentId);
				if(tr.find(".shipmentId").text()){
					if($(this).attr("key")){
						var html1 = "PageType：<div class='input-append btn-group btn-input'><select id='type' style='height: 25px;width:220px'><option value='PackageLabel_Letter_6'>PackageLabel_Letter_6</option><option value='PackageLabel_A4_4'>PackageLabel_A4_4</option><option value='PackageLabel_Plain_Paper'>PackageLabel_Plain_Paper</option></select></div>";
						var html = "PageType：<div class='input-append btn-group btn-input'><select id='type' style='height: 25px;width:220px'><option value='PackageLabel_A4_4'>PackageLabel_A4_4</option><option value='PackageLabel_Plain_Paper'>PackageLabel_Plain_Paper</option></select></div>";
						if(tr.find("td:eq(1)").text().indexOf("US")>=0||tr.find("td:eq(1)").text().indexOf("CA")>=0){
							html = html1;
						}
						top.$.jBox.confirm(html,'gen Unique PackageLabel?',function(v,h,f){
							if(v=='ok'){
								var param = {};
								param.id = tr.find(".shipmentId :hidden").val();
								param.type = h.find("#type").val();
								tr.find(".statu").text("Generating...");
								param.shipmentId = shipmentId;
								$.ajax({
				         			   type: "POST",
				         			   url:"${ctx}/psi/fbaInbound/genUniqueLabel?"+$.param(param),
				         			   data:new FormData(),
				         			   processData : false,  
						               contentType : false, 
				         			   success: function(msg){
				         				  $("#sid").val(tr.find(".shipmentId").attr("sid")); 
				         				  $("#searchForm").submit();	
				         			   }
					             });
								var time=10000;
								if(tr.find("td:eq(1)").text().indexOf("US")>=0){
									time = 100000;
								}
								top.$.jBox.tip('正在上传装箱信息，并生成箱单，请耐心等待1-2分钟...生成后页面会自动刷新','info',{persistent:false,'timeout':time});
							}
						},{buttonsFocus:1,width:350,showClose: false,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
						
					}else{
						$.ajax({
	         			   type: "POST",
	         			   url: "${ctx}/psi/fbaInbound/getPacks?id="+tr.find(".shipmentId :hidden").val(),
	         			   async: false,
	         			   success: function(msg){
	         				   rs = msg;		
	         			   }
	             		});
						var html1 = "Number：<div class='input-append btn-group btn-input'><input id='number' type='number' value='"+rs+"' style='height: 25px;width:100px' class='span2'  size='16'/></div>"+
						"<br/>PageType：<div class='input-append btn-group btn-input'><select id='type' style='height: 25px;width:220px'><option value='PackageLabel_Letter_6'>PackageLabel_Letter_6</option><option value='PackageLabel_A4_4'>PackageLabel_A4_4</option><option value='PackageLabel_Plain_Paper'>PackageLabel_Plain_Paper</option></select></div>";
						var html = "Number：<div class='input-append btn-group btn-input'><input id='number' type='number' value='"+rs+"' style='height: 25px;width:100px' class='span2'  size='16'/></div>"+
						"<br/>PageType：<div class='input-append btn-group btn-input'><select id='type' style='height: 25px;width:220px'><option value='PackageLabel_A4_4'>PackageLabel_A4_4</option><option value='PackageLabel_Plain_Paper'>PackageLabel_Plain_Paper</option></select></div>";
						
						if(tr.find("td:eq(1)").text().indexOf("US")>=0||tr.find("td:eq(1)").text().indexOf("CA")>=0||tr.find("td:eq(1)").text().indexOf("JP")>=0){
							html = html1;
						}
						top.$.jBox.confirm(html,'gen PackageLabel?',function(v,h,f){
							if(v=='ok'){
								var number = h.find("#number").val();
								if($.isNumeric(number)&&number>0){
									var param = {};
									param.id = tr.find(".shipmentId :hidden").val();
									param.type = h.find("#type").val();
									param.number = h.find("#number").val();
									tr.find(".statu").text("Generating...");
									param.shipmentId = shipmentId;
									$.ajax({
					         			   type: "POST",
					         			   url:"${ctx}/psi/fbaInbound/genLabel?"+$.param(param),
					         			   processData : false,  
							               contentType : false,
					         			   data:new FormData(),
					         			   success: function(msg){
					         				  $("#sid").val(tr.find(".shipmentId").attr("sid")); 
					         				  $("#searchForm").submit();	
					         			   }
						             });
									var time=10000;
									if(tr.find("td:eq(1)").text().indexOf("US")>=0){
										time = 100000;
									}
									top.$.jBox.tip('正在上传装箱信息，并生成箱单，请耐心等待1-2分钟...生成后页面会自动刷新','info',{persistent:false,'timeout':time});
								}else{
									top.$.jBox.tip("Number Must be a positive integer","error");
									return false;
								}
							}
						},{buttonsFocus:1,width:350,showClose: false,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
						
					}
				}else{
					$.jBox.tip("请同步shipmentId", 'error',{timeout:2000});
				}	
				return false;
			});
			
			
			$(".genPalletLabel").click(function(){
				var tr = $(this).parent().parent().parent().parent().parent();
				var shipmentId = $(this).attr("shipmentIdVal");
				if(tr.find(".shipmentId").text()){
					var html1 = "Number：<div class='input-append btn-group btn-input'><input id='number' type='number' value='' style='height: 25px;width:100px' class='span2'  size='16'/></div>"+
					"<br/>PageType：<div class='input-append btn-group btn-input'><select id='type' style='height: 25px;width:220px'><option value='PackageLabel_Letter_6'>PackageLabel_Letter_6</option><option value='PackageLabel_A4_4'>PackageLabel_A4_4</option><option value='PackageLabel_Plain_Paper'>PackageLabel_Plain_Paper</option></select></div>";
					var html = "Number：<div class='input-append btn-group btn-input'><input id='number' type='number' value='' style='height: 25px;width:100px' class='span2'  size='16'/></div>"+
					"<br/>PageType：<div class='input-append btn-group btn-input'><select id='type' style='height: 25px;width:220px'><option value='PackageLabel_A4_4'>PackageLabel_A4_4</option><option value='PackageLabel_Plain_Paper'>PackageLabel_Plain_Paper</option></select></div>";
					if(tr.find("td:eq(1)").text().indexOf("US")>=0||tr.find("td:eq(1)").text().indexOf("CA")>=0){
						html = html1;
					}
					top.$.jBox.confirm(html,'gen PalletLabel?',function(v,h,f){
						if(v=='ok'){
							var number = h.find("#number").val();
							if($.isNumeric(number)&&number>0){
								var param = {};
								param.id = tr.find(".shipmentId :hidden").val();
								param.type = h.find("#type").val();
								param.number = h.find("#number").val();
								param.shipmentId = shipmentId;
								tr.find(".statu").text("Generating...");
								$.ajax({
				         			   type: "POST",
				         			   url:"${ctx}/psi/fbaInbound/genPalletLabels?"+$.param(param),
				         			   processData : false,  
						               contentType : false,
				         			   data:new FormData(),
				         			   success: function(msg){
				         				  $("#sid").val(tr.find(".shipmentId").attr("sid")); 
				         				  $("#searchForm").submit();	
				         			   }
					             });
								var time=10000;
								if(tr.find("td:eq(1)").text().indexOf("US")>=0){
									time = 100000;
								}
								top.$.jBox.tip('正在生成托盘贴,生成后页面会自动刷新','info',{persistent:false,'timeout':time});
							}else{
								top.$.jBox.tip("Number Must be a positive integer","error");
								return false;
							}
						}
					},{buttonsFocus:1,width:350,showClose: false,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
						
				}else{
					$.jBox.tip("请同步shipmentId", 'error',{timeout:2000});
				}	
				return false;
			});
			
			
			
			$(".editable").editable({validate:function(data){
				if($(this).attr("key")!='dhlTracking'){
					if(data){
						if(!$.isNumeric(data)){
							return "请输入数字类型！";
						}else if($(this).attr("key")=='tray'){
							if(!isInteger(data)){
								return "请输入整数！";
							}
						}
					}
				}
			},success:function(response,newValue){
				var param = {};
				var $this = $(this);
				var oldVal = $this.text();
				param.id = $this.parent().find(":hidden").val();
				param[$(this).attr("key")] = newValue;
				$.get("${ctx}/psi/fbaInbound/update?"+$.param(param),function(data){
					if(!(data)){
						$this.text(oldVal);						
					}else{
						$.jBox.tip("保存成功！", 'info',{timeout:2000});
					}
				});
				return true;
			}});
			
			$(".dhlTrackingEditable").editable({validate:function(data){
					if(!(data)){
						return "跟踪号不能为空!";
					}
					if(data.match(/[\uff00-\uffff]/g)){
						return "跟踪号不能输入全角字符，请切换到英文输入模式";
					}
				},
				display:false,success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.attr("keyVal");
						var nikename=$this.attr("keyName");
						param.id = $this.parent().find(":hidden").val();
						param[$(this).attr("key")] = newValue;
						$.get("${ctx}/psi/fbaInbound/update?"+$.param(param),function(data){
							var site="${site}";
							var siteArr=site.substring(1,site.length-1).split(",");
							var finalSite="";
							for(var i=0;i<siteArr.length;i++){
								if($.trim(nikename)==$.trim(siteArr[i].split("=http")[0])){
							        finalSite="http"+siteArr[i].split("=http")[1];
						        }
							}
							
							var arr=newValue.split("-");
							if(oldVal){
								if(finalSite.indexOf("$$")!=-1){
									var billNo1= $this.parent().find(".billNo1");
									//billNo1.attr("href",finalSite.replace("$$",newValue));
									billNo1.attr("onclick","openNewWindow('1','"+finalSite+"','"+newValue+"')");
								}else if(finalSite.indexOf("##")!=-1){
									var billNo2= $this.parent().find(".billNo2");
									//billNo2.attr("href",finalSite.replace("##",arr[0])+arr[1]);
									billNo2.attr("onclick","openNewWindow('2','"+finalSite+"','"+newValue+"')");
								}
							}else{
								if(finalSite.indexOf("$$")!=-1){
									$this.parent().append("<a  class='billNo1' target='_blank' onclick='openNewWindow('1','"+finalSite+"','"+newValue+"')'>Track</a>");
								}else if(finalSite.indexOf("##")!=-1){
									$this.parent().append("<a  class='billNo2' target='_blank' onclick='openNewWindow('2','"+finalSite+"','"+newValue+"')'>Track</a>");
								}else{
									$this.parent().append("<a  class='billNo3' target='_blank' href='"+finalSite+"'>Track</a>");
								} 
							}
							
							
							$this.attr("keyVal",newValue);
							$.jBox.tip("保存成功！", 'info',{timeout:2000});
							
						});
							return true;
					}
			});
			
			$(".notAuto").editable({validate:function(data){
				if(!(data)){
					return "shipmentId不能为空!";
				}
			},
			display:false,success:function(response,newValue){
					var param = {};
					var $this = $(this);
					param.id = $this.parent().find(":hidden").val();
					param.shipmentId = newValue;
					param.shipmentName = $this.attr("keyval");
					window.location.href="${ctx}/psi/fbaInbound/notAuto?"+$.param(param);
				}
			});
			
			$(".editorToDhlDate").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(":hidden").val();
					param.toDhlDate1 = newValue;
					$.get("${ctx}/psi/fbaInbound/update?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("保存日期成功！", 'info',{timeout:2000});
						}
					});
				return true;
			}});
			
			
			$(".dateEditor").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(":hidden").val();
					param.deliveryDate1 = newValue;
					$.get("${ctx}/psi/fbaInbound/update?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("保存提货日期成功！", 'info',{timeout:2000});
						}
					});
					return true;
				}});
			
			
			$(".genTrans").click(function(){
				var $this = $(this);
				var id = $this.attr("keyVal");;
				top.$.jBox.confirm("Generate transport？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=='ok'){
							$.post("${ctx}/psi/lcPsiTransportOrder/genTransFromFba",{id:id},function(date){
								  if(date=='1'){
									  $.jBox.tip('success');
								  }else{
									  $.jBox.tip('failed');
								  }
						     }); 
					}
				},{buttonsFocus:1,width:350,showClose: false,persistent: true});
				top.$('.jbox-body .jbox-icon').css('top','55px');
					
			});
			
			$(".fbaPdf").click(function(){
				var $this = $(this);
				var shipment=$this.attr("keyVal2");
				var html = "<table><tr><th>Amazon-Referenznr.：</th>"+
				"<td><div class='input-append btn-group btn-input'><input id='amazonZnr' style='height: 25px;width:200px' class='span2'  size='16'/></div></td></tr>"+
				"<tr><th>Total-Weight.：</th><td><div class='input-append btn-group btn-input'><input id='totalWeight' style='height: 25px;width:200px' class='span2'  size='16'/></div></td></tr>"+
				
				"</table>";
				top.$.jBox.confirm(html,'Are you sure you print it？',function(v,h,f){
					if(v=='ok'){
							var params = {};
							params.id = $this.attr("keyVal"); 
							params.amazonZnr=h.find("#amazonZnr").val();
							params.totalWeight=h.find("#totalWeight").val();
							$.post("${ctx}/psi/fbaInbound/printFbaInboundPdf",$.param(params),function(data){
							    windowOpen('${ctx}/../data/site/psi/fbaInbound/'+shipment+'/'+shipment+'.pdf','<spring:message code="amazon_order_form_select1_option2"/>',800,600);
							});
					}
				},{buttonsFocus:1,width:450,showClose: false,persistent: true});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		function openOrClose(id,btn){
			if($(btn).text()=='Summary'){
				$(btn).text('Close');
			}else{
				$(btn).text('Summary');
			}
			$("#"+id).toggle();
		}
		
		function isInteger( str ){
			var regu = /^[-]{0,1}[0-9]{1,}$/;
			return regu.test(str);
		}
		
		function openNewWindow(type,href,billNo){
			var arr=billNo.split(",");
			for(var i=0;i<arr.length;i++){
				if(arr[i].trim()!=""){
					if(type=='1'){
						window.open(href.replace("$$",arr[i]));  
					}else if(type=='2'){
					    var noArr=arr[i].split("-");
						window.open(href.replace("##",noArr[0])+noArr[1]);  
					}
				}
			}
		}
		
		function updateFee(FbaId){
            var html="<div class='showChildrenHtml' style='text-align:center;margin-left:10px;' ><table style='width:98%;margin-top:10px'  class='table table-striped table-bordered table-condensed'><thead><tr><th  style='text-align: center;vertical-align: middle;'>Weight</th><th  style='text-align: center;vertical-align: middle;'>Quantity</th></tr></thead><tbody>";
		    html=html+"<tr><th style='text-align: center;vertical-align: middle;'>15kg</th><td><input style='width:90%;' type='text' class='quantity3' value='0'/></td></tr>";
		    html=html+"<tr><th style='text-align: center;vertical-align: middle;'>30kg</th><td><input style='width:90%;' type='text' class='quantity4' value='0'/></td></tr>";
		    html=html+"</tbody></table></div>";
		    
		    top.$.jBox.confirm(html,"Edit Fee", function(v,h,f){
				if(v=='ok'){
						var param = {};
						param.quantity3 = h.find(".quantity3").val();
						param.quantity4 =h.find(".quantity4").val();
						if(h.find(".quantity3").val()==""){
							param.quantity1=0;
						}
						if(h.find(".quantity4").val()==""){
							param.quantity2=0;
						}
						param.id = FbaId;
						$.get("${ctx}/psi/fbaInbound/update?"+$.param(param),function(){
							top.$.jBox.tip("修改成功！","info");
							$("#searchForm").submit();
						});
		            return true;
				}
			},{buttonsFocus:1,width:750,showClose: true,persistent: true});
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty fbaInbound.country ?'active':''}"><a class="countryHref" href="#" key=""><b>总计</b></a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${fbaInbound.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}"><b>${dic.label}</b></a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<form:form id="searchForm" modelAttribute="fbaInbound" action="${ctx}/psi/fbaInbound/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input  name="country" type="hidden" value="${fbaInbound.country}"/>
		<div style="height: 80px;line-height: 40px">
			<div style="height: 80px;">
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${fbaInbound.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="lastUpdateDate" value="<fmt:formatDate value="${fbaInbound.lastUpdateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<label>shipmentId/shipmentName：</label><form:input path="shipmentId" id="sid" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;&nbsp;
				<label>productName：</label><form:input path="shipmentName" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;&nbsp;
				<br/>
				shipmentStatus：<select name="shipmentStatus" style="width: 120px" id="selectT">
									<option value="" ${fbaInbound.shipmentStatus eq ''?'selected':'' }>Plan</option>
									<option value="-1" ${fbaInbound.shipmentStatus eq '-1'?'selected':'' }>EFFECTIVE</option>
									<option value="WORKING" ${fbaInbound.shipmentStatus eq 'WORKING'?'selected':'' }>WORKING</option>
									<option value="SHIPPED" ${fbaInbound.shipmentStatus eq 'SHIPPED'?'selected':'' }>SHIPPED</option>
									<option value="IN_TRANSIT" ${fbaInbound.shipmentStatus eq 'IN_TRANSIT'?'selected':'' }>IN_TRANSIT</option>
									<option value="DELIVERED" ${fbaInbound.shipmentStatus eq 'DELIVERED'?'selected':'' }>DELIVERED</option>
									<option value="CHECKED_IN" ${fbaInbound.shipmentStatus eq 'CHECKED_IN'?'selected':'' }>CHECKED_IN</option>
									<option value="RECEIVING" ${fbaInbound.shipmentStatus eq 'RECEIVING'?'selected':'' }>RECEIVING</option>
									<option value="CLOSED" ${fbaInbound.shipmentStatus eq 'CLOSED'?'selected':'' }>CLOSED</option>
									<option value="CANCELLED" ${fbaInbound.shipmentStatus eq 'CANCELLED'?'selected':'' }>CANCELLED</option>
									<option value="DELETED" ${fbaInbound.shipmentStatus eq 'DELETED'?'selected':'' }>DELETED</option>
									<option value="ERROR" ${fbaInbound.shipmentStatus eq 'ERROR'?'selected':'' }>ERROR</option>
								</select>
				&nbsp;&nbsp;&nbsp;&nbsp;
				
				
				Account: <select name="accountName" style="width: 120px" id="selectAccount">
				                <option value="">-All-</option>
				                <c:forEach items="${accountList}" var="account" >
				                     <option value="${account}" ${fbaInbound.accountName eq account?'selected':'' }>${account}</option>
				                </c:forEach>
				         </select>
				
				<input type="checkbox" value="0" ${fbaInbound.tray==0?'checked':''} name="tray" /> Unfilled Fee &nbsp;&nbsp;
				
				<input type="checkbox" value="0" ${'0' eq fbaInbound.responseLevel?'checked':''} name="responseLevel" /> P0 &nbsp;&nbsp;
				
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
				
				<input id="export" class="btn btn-primary" type="button" value="Export"/>
				
				<input id="SendPlansEmail" class="btn btn-primary" type="button" value="SendPlansEmail"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<div class="alert alert-info"><strong >每天北京时间7:30,12:30,16:30自动同步!</strong></div>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
				<tr>
				   <th style="width: 20px">
						<span>
							<input type="checkbox">
						</span>
				   </th> 	
				   <th>ShipmentName</th>
				   <th>Shipment Id</th>
				   <th>FC</th>
				   <th>Status</th>
				   <th>Outbound</th>
				   <th>Pick Up</th>
				   <th>To AMZ</th>
				   <th>Receiving</th>
				   <th>Finish</th>
				   <th>Shipped</th>
				   <th>Received</th>
				   <th>Track</th>
				   <%--<th>Pallets</th> --%>
				   <shiro:hasPermission name="amazoninfo:fba:feeEdit">
				   		 <th>Fee</th>
				   </shiro:hasPermission>
				   <shiro:lacksPermission name="amazoninfo:fba:feeEdit">
					   <shiro:lacksPermission name="psi:transport:salesView">
					   <th>Fee</th>
					   </shiro:lacksPermission>
				   </shiro:lacksPermission>
				   <th>Status</th>
				   <th>Label</th>
				   <th>Carrier</th>
				   <th>Box Num</th>
				   <th>Operating</th>
				</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="fbaInbound" varStatus="i">
			<tr>
				<td>
					<div class="checker">
					<span>
						<c:if test="${fbaInbound.shipmentStatus eq '' && fbaInbound.areCasesRequired ne '3'}">	
							<input type="checkbox"/>
						  	<input type="hidden" value="${fbaInbound.id}" class="fbaId"/>
						</c:if>  
					</span>
					</div>
				</td>
				<%-- <td>${fns:getDictLabel(fbaInbound.country,'platform','Others')}</td> --%>
				<td>
					<a href="${ctx}/psi/fbaInbound/export/txt?id=${fbaInbound.id}">${fbaInbound.shipmentName}</a>
					<span style="font-size: 14px">${fbaInbound.areCasesRequired eq '3'?'<br/>(PlanSent)':''}</span>
					<span style="font-size: 14px">${fbaInbound.areCasesRequired eq '1'?'<br/>(Sent)':''}</span>
					<span style="color: green;font-size: 14px">${fbaInbound.areCasesRequired eq '2'?'<br/>(Correction)':''}</span>
					<span style="color: red;font-size: 14px">${'0' eq fbaInbound.responseLevel?'<br/>(P0)':''}</span>
					<span style="font-size: 14px"><br/>${fbaInbound.accountName}</span>
				</td>
				<td class="shipmentId" sid="${fbaInbound.shipmentId}">
					<input type="hidden" value="${fbaInbound.id}"/>
					<c:choose>
						<c:when test="${fbaInbound.shipmentStatus eq 'WORKING'}">
							<a  class="notAuto" href="#" data-type="text" data-pk="1" data-title="Enter shipmentId"  keyval="${fbaInbound.shipmentId}" data-value="${fbaInbound.shipmentId}">${empty fbaInbound.shipmentId?'Manual synchronization':fbaInbound.shipmentId}</a>	
						</c:when>
						<c:otherwise>
							${fbaInbound.shipmentId}
						</c:otherwise>
					</c:choose>
					<c:set value="${fbaInbound.id.toString()}" var="fbaId" />
					<c:choose>
						<c:when test="${not empty fn:split(tranMap[fbaId],',')[0]}">
							<br/><span style="color: maroon;font-weight: bold;font-size: 14px">${fn:split(tranMap[fbaId],',')[0]}</span>
							<br/><a target="_blank" href="${ctx}/psi/${fn:contains(fn:split(tranMap[fbaId],',')[1],'_LC_')?'lcPsiTransportOrder':'psiTransportOrder' }/list?transportNo=${fn:split(tranMap[fbaId],',')[1]}">${fn:split(tranMap[fbaId],',')[1]}</a>
						</c:when>
						<c:otherwise>
							<c:if test="${not empty fn:split(tranMap[fbaInbound.shipmentId],',')[0]}">
								<br/><span style="color: maroon;font-weight: bold;font-size: 14px">${fn:split(tranMap[fbaInbound.shipmentId],',')[0]}</span>
								<br/><a target="_blank" href="${ctx}/psi/${fn:contains(fn:split(tranMap[fbaInbound.shipmentId],',')[1],'_LC_')?'lcPsiTransportOrder':'psiTransportOrder' }/list?transportNo=${fn:split(tranMap[fbaInbound.shipmentId],',')[1]}">${fn:split(tranMap[fbaInbound.shipmentId],',')[1]}</a>
							</c:if>
						</c:otherwise>
					</c:choose>
					<br/>${fbaInbound.amzReferenceId}
				</td>
				<td>
					${fbaInbound.destinationFulfillmentCenterId}
				</td>
				<td>
					${empty fbaInbound.shipmentStatus?'Plan':fbaInbound.shipmentStatus}
				</td>
				<td>
					<fmt:formatDate value="${fbaInbound.shippedDate}" pattern="yyyy-MM-dd"/>
				</td>
				<td > 
					<input type="hidden" value="${fbaInbound.id }"/>
					<a href="#" class="dateEditor"  key="deliveryDate" data-type="date" data-pk="1" data-title="Enter DeliveryDate"><fmt:formatDate value="${fbaInbound.deliveryDate}" pattern="yyyy-MM-dd"/></a>
				</td>
				<td >
					<input type="hidden" value="${fbaInbound.id }"/>
					<a href="#" class="editorToDhlDate" key="toDhl" data-type="date" data-pk="1" data-title="Enter toDhl"><fmt:formatDate value="${fbaInbound.toDhl}" pattern="yyyy-MM-dd"/></a>
				</td>
				<td>
					<fmt:formatDate value="${fbaInbound.arrivalDate}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					<fmt:formatDate value="${fbaInbound.finishDate}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					<c:if test="${not empty fbaInbound.weight}">
						<a href="#" data-toggle="tooltip" title="Total Weight : ${fbaInbound.weight}KG   Total Volume : ${fbaInbound.volume}CBM">${fbaInbound.quantityShipped}</a>
					</c:if>	
					<c:if test="${empty fbaInbound.weight}">
						${fbaInbound.quantityShipped}
					</c:if>
				</td>
				<td class="${not empty fbaInbound.shipmentStatus && fbaInbound.error eq '1'?'alert alert-error':''}">${fbaInbound.quantityReceived}</td>
				<td>
					<input type="hidden" value="${fbaInbound.id }"/>
					<c:if test="${fbaInbound.supplier ne 'OTHER' }">
					  <shiro:hasPermission name="psi:fbaInbound:edit">
					        <a  class="dhlTrackingEditable" href="#" keyName="${fbaInbound.supplier}" key="dhlTracking" keyVal="${fbaInbound.dhlTracking}" data-type="text" data-pk="1" data-title="Enter Tracking-${fbaInbound.supplier}" data-value="${fbaInbound.dhlTracking}">edit</a>
					  </shiro:hasPermission>
			           <c:if test="${not empty fbaInbound.dhlTracking}">
							<c:set var="trackNoStr" value=""/>
							<c:forEach items="${fn:split(fbaInbound.dhlTracking,',')}" var="trackNo" varStatus="j">
								 <c:choose>
								    <c:when test="${fn:contains(site[fbaInbound.supplier],'$$')}">
								      <c:set var="trackNoStr" value="${trackNoStr},${trackNo}"/>
								       <c:if test="${j.last}">
								          <a  class="billNo1" target="_blank" onclick="openNewWindow('1','${site[fbaInbound.supplier]}','${trackNoStr }')">Track</a>
								       </c:if>
								    </c:when>
								     <c:when test="${fn:contains(site[fbaInbound.supplier],'##')}">
								      <c:set var="trackNoStr" value="${trackNoStr},${trackNo}"/>
								       <c:if test="${j.last}">
								          <a  class="billNo2" target="_blank" onclick="openNewWindow('2','${site[fbaInbound.supplier]}','${trackNoStr }')">Track</a>
								      </c:if>
								    </c:when>
								    <c:otherwise>
								     <c:if test="${j.last}">
								      <a class="billNo3"  target="_blank" href="${site[fbaInbound.supplier] }">Track</a> </c:if>
								    </c:otherwise>
								</c:choose> 
							</c:forEach>	
					  </c:if>
					</c:if>
					<c:if test="${fbaInbound.supplier eq 'OTHER' }">
					</c:if>
				</td>
				<%--<td>
					<input type="hidden" value="${fbaInbound.id }"/>
					<a class="editable" href="#" key="tray" data-type="text" data-pk="1" data-title="Enter Tray">${fbaInbound.tray}</a>
				</td> --%>
				<shiro:lacksPermission name="amazoninfo:fba:feeEdit">
					<shiro:lacksPermission name="psi:transport:salesView">
					<td>
						${fbaInbound.fee}
					</td>
					</shiro:lacksPermission>
				</shiro:lacksPermission>
				<shiro:hasPermission name="amazoninfo:fba:feeEdit">
				 	<td >
							<input type="hidden" value="${fbaInbound.id }"/>
							 <a class="editable" href="#" key="fee" data-type="text" data-pk="1" data-title="Enter Fee">${fbaInbound.fee}</a>
					</td>
				</shiro:hasPermission>
				<td class="statu"><a href="#" data-toggle="tooltip" title="${empty fbaInbound.proessStatus?'正常':fbaInbound.proessStatus}">${fns:abbr((empty fbaInbound.proessStatus?'正常':fbaInbound.proessStatus),10)}</a></td>
				<td>
					<c:if test="${fbaInbound.hasGenLabel eq '1'}">
						<%double number=Math.random()*100;
							request.setAttribute("number",number);
						%>
						<a href="<c:url value='/data/site/fbaLabel/${fbaInbound.country}/${fbaInbound.shipmentId}/PackageLabels.pdf?num=${number}'/>" target="_blank">Packages</a>
					</c:if>
					<%-- <c:if test="${not empty fn:split(tranMap[fbaInbound.shipmentId],',')[0]}"> --%>
					 	<br/><a href="<c:url value='/data/site/fbaLabel/${fbaInbound.country}/${fbaInbound.shipmentId}/pallet/PackageLabels.pdf?num=${number}'/>" target="_blank">Pallets</a>
					<%-- </c:if>  --%>
				</td>
				<td>${fbaInbound.supplier}</td>
				<td>
					<c:choose>
						<c:when test="${'DPD' eq fbaInbound.supplier}">15kg:${fbaInbound.quantity1}<br/>30kg:${fbaInbound.quantity2}</c:when>
						<c:otherwise>${fbaInbound.quantity1}</c:otherwise>
					</c:choose>
				</td>
				<td>
					<div class="btn-group">
					   <button type="button" class="btn btn-small">Amz</button>
					   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
					      <span class="caret"></span>
					      <span class="sr-only"></span>
					   </button>
					   <ul class="dropdown-menu">
					     
					          <shiro:hasPermission name="psi:fbaInbound:edit">
					           <li><a href="#" class="upload">UpLoad</a></li>
					           <c:if test="${fbaInbound.shipmentStatus ne '' && not empty fbaInbound.shipmentId}">
					     	     <li><a href="#" class="sync">DownLoad</a></li>
					           </c:if>
					         </shiro:hasPermission>
					         <li class="divider"></li>
					          <shiro:hasPermission name="psi:fbaInbound:edit">
					           <c:if test="${not empty fbaInbound.shipmentId}">
					              <li><a href="#" class="genLabel" shipmentIdVal='${fbaInbound.shipmentId}'>Gen Label</a></li>
							      <c:if test="${fbaInbound.country eq 'com'}">
								      <li><a href="#" class="genLabel" key="unique" shipmentIdVal='${fbaInbound.shipmentId}'>Gen UniqueLabel</a></li>
								      <li><a href="#" class="genLabelByDiy" shipmentIdVal='${fbaInbound.shipmentId}'>Gen LabelByDIY</a></li>
								      <li><a href="#" class="genLabelByDiy" key="unique" shipmentIdVal='${fbaInbound.shipmentId}'>Gen UniqueLabelByDIY</a></li>
								      <c:if test="${fbaInbound.hasGenLabel eq '1'}">
								      		<li><a href="${ctx}/psi/fbaInbound/export/xml?id=${fbaInbound.id}" >DownLoad Cartons</a></li>
								      		<li><a href="${ctx}/psi/fbaInbound/export/diy?id=${fbaInbound.id}" >DownLoad DIY Cartons</a></li>
							     	  </c:if>
							      </c:if>
							      <c:if test="${fbaInbound.country eq 'com' || fbaInbound.country eq 'ca' || fbaInbound.country eq 'jp'}">
							     	  		<li><a href="#" class="genPalletLabel" shipmentIdVal='${fbaInbound.shipmentId}'>Gen PalletLabel</a></li>
						     	  </c:if>
						       </c:if>
					        </shiro:hasPermission>
					     
						  <li><a href="${ctx}/psi/fbaInbound/exportDetail?id=${fbaInbound.id}">Export FBA Form</a></li>
					   </ul>
					</div>
					
					<c:if test="${fbaInbound.hasProblem eq '0'}">
						<span class="btn btn-small btn-info open" onclick="openOrClose('row${i.index}',this)" >Summary</span>
					</c:if>
					<c:if test="${fbaInbound.hasProblem eq '1'}">
						<span class="btn btn-small btn-success open" onclick="openOrClose('row${i.index}',this)" >Summary</span>
					</c:if>
					
					<div class="btn-group">
					   <button type="button" class="btn btn-small">Edit</button>
					   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
					      <span class="caret"></span>
					      <span class="sr-only"></span>
					   </button>
					   <ul class="dropdown-menu" >
					   
					  
					       <shiro:hasPermission name="psi:fbaInbound:edit">
					          	 <c:if test="${fbaInbound.areCasesRequired ne '1'}">
							   	  	<li><a href="${ctx}/psi/fbaInbound/cancel?id=${fbaInbound.id}" onclick="return confirmx('真的要取消该帖子吗?', this.href)">Cancel</a></li>	
							     </c:if>
							      <c:if test="${empty fbaInbound.shipmentStatus || fbaInbound.shipmentStatus eq 'WORKING'}">
									 <li><a href="${ctx}/psi/fbaInbound/form?id=${fbaInbound.id}">Edit</a></li>
								  </c:if>
					       </shiro:hasPermission>
					  
						   <c:if test="${not empty fbaInbound.pdfFile}">
							 <li><a href="${ctx}/psi/fbaInbound/viewPdf?id=${fbaInbound.id}">pdf File</a></li>
						  </c:if>
					   </ul>
					</div>
					
						 <shiro:hasPermission name="psi:fbaInbound:edit">
						    <c:if test="${fbaInbound.shipmentStatus eq 'WORKING' && not empty fbaInbound.shipmentId && fbaInbound.areCasesRequired ne '1'}">
							 	<a href="${ctx}/psi/fbaInbound/sendEmail?id=${fbaInbound.id}" class="btn btn-small btn-warning">Send Email</a>
							 </c:if>
							 <c:if test="${not empty fbaInbound.shipmentStatus  &&fbaInbound.error eq '1' && fbaInbound.areCasesRequired ne '2'}">
							 	<a href="${ctx}/psi/fbaInbound/sendEmail?id=${fbaInbound.id}" class="btn btn-small btn-danger">Send Email</a>
							 </c:if>
						 </shiro:hasPermission>
					
					 <%--后台根据库存和状态、国家信息判断是否可以确认 --%>
					 <shiro:hasPermission name="psi:fbaInbound:response">
					 	<c:set var="conKey" value="${fbaInbound.id}${fbaInbound.shipFromAddress}"/>
					 	<c:if test="${'0' eq canConfirmMap[conKey] }">
						  <a href="${ctx}/psi/fbaInbound/response?id=${fbaInbound.id}" class="btn btn-small btn-warning">Confirm</a>
					 	</c:if>
					 </shiro:hasPermission>
					<input class="id" type="hidden" value="${fbaInbound.id}" />
					<c:if test="${'fr' eq fbaInbound.country||'es' eq fbaInbound.country||'it' eq fbaInbound.country||'uk' eq fbaInbound.country}">
						<div class="btn-group">
						   <button type="button" class="btn btn-small">Print</button>
						   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" ><!-- href="${ctx}/psi/fbaInbound/printFbaInboundPdf?id=${fbaInbound.id}"  -->
						   	  <li><a keyVal="${fbaInbound.id}" keyVal2="${fbaInbound.shipmentId}" class="fbaPdf" target="_blank">FBA Delivery Form</a></li>	
							  <li><a href="${ctx}/psi/fbaInbound/exportRegExcel?id=${fbaInbound.id}">Registration Form</a></li>
						   </ul>
						</div>
					</c:if>
					 <c:if test="${fbaInbound.shipmentStatus ne '' && not empty fbaInbound.shipmentId}">
						<shiro:hasPermission name="psi:transport:edit">
						     <a keyVal="${fbaInbound.id}"  class="btn btn-small genTrans">GenTR</a>
						</shiro:hasPermission>
					 </c:if>	
  				</td>
			</tr>
			<tbody id="row${i.index}" style="display:none">
				<tr style="background-color: #D2E9FF">
					<td style="text-align: right;">NO.</td>
					<td colspan="2">产品名</td>
					<td colspan="2">Sku</td>
					<td>FnSku</td>
					<td>装箱数</td>
					<td>建贴数</td>
					<td>发货数</td>
					<td>已收货</td>
					<td>错误数</td>
					<td colspan="8">错误描述</td>
				</tr>
				<c:forEach items="${fbaInbound.itemsByOrder}"  var="item" varStatus="j">
				<tr style="background-color: #D2E9FF">
					<td style="text-align: right;">${j.count}</td>
					<td colspan="2"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${skuMap[item.sku]}">${skuMap[item.sku]}</a></td>
					
					<td colspan="2">${item.sku}</td>
					<td >${item.fnSku}</td>
					<td >${item.packQuantity}</td>
					<td>${empty item.sellerShipped?item.quantityShipped:item.sellerShipped}</td>
					<td>${item.quantityShipped}</td>
					<td>${item.quantityReceived}</td>
					<td>
						<c:if test="${not empty item.problemMap }">
							<a target="_blank" href="${item.fbaInbound.problemUrl }" >${item.problemMap['有问题数量'] }</a>
						</c:if>
					</td>
					<td colspan="8">
						<c:if test="${not empty item.problemMap }">
							${item.problemMap['问题描述'] }
						</c:if>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
