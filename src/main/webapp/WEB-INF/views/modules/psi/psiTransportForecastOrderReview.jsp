<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>查看运单</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style>
	table {table-layout:fixed}
	td th {word-wrap:break-word;word-break:break-all;}
	</style>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
	
		$(document).ready(function() {
			
			 $(window).scroll(function() {
			        var scrollHeight = $(document).scrollTop();
			        if(scrollHeight>=100){
			        	$("#header").css("top","2px");   
			        }else{
			        	$("#header").css("top","115px");    
			        }     
			    });
			 

			$("#country").on("change",function(){
				var id = "${psiTransportForecastOrder.id}";
				var nameColor = $("#nameColor").children("option:selected").val();
				var model = $("#model").children("option:selected").val();
				var transSta=$("#lcOrSp").children("option:selected").val();
				window.location.href="${ctx}/psi/transportForecastOrder/review?id="+id+"&country="+$(this).val()+"&name="+nameColor+"&transModel="+model+"&transSta="+transSta;
			});
			
			$("#nameColor").on("change",function(){
				var id = "${psiTransportForecastOrder.id}";
				var country = $("#country").children("option:selected").val();
				var model = $("#model").children("option:selected").val();
				var transSta=$("#lcOrSp").children("option:selected").val();
				window.location.href="${ctx}/psi/transportForecastOrder/review?id="+id+"&country="+country+"&name="+$(this).val()+"&transModel="+model+"&transSta="+transSta;
			});
			
			$("#model").on("change",function(){
				var id = "${psiTransportForecastOrder.id}";
				var nameColor = $("#nameColor").children("option:selected").val();
				var country = $("#country").children("option:selected").val();
				var transSta=$("#lcOrSp").children("option:selected").val();
				window.location.href="${ctx}/psi/transportForecastOrder/review?id="+id+"&country="+country+"&name="+nameColor+"&transModel="+$(this).val()+"&transSta="+transSta;
			});
			
			$("#lcOrSp").on("change",function(){
				var id = "${psiTransportForecastOrder.id}";
				var nameColor = $("#nameColor").children("option:selected").val();
				var country = $("#country").children("option:selected").val();
				var model = $("#model").children("option:selected").val();
				window.location.href="${ctx}/psi/transportForecastOrder/edit?id="+id+"&country="+country+"&name="+nameColor+"&transModel="+model+"&transSta="+$(this).val();
			});
			
			$(".enterQuantity").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					if(newValue){
						var param = {};
						var oldVal = $(this).text();
						param.itemId = $(this).parent().find(".itemId").val();
						param.content = newValue;
						param.flag="0";
						$.get("${ctx}/psi/transportForecastOrder/updateInfo?"+$.param(param),function(data){
							if(!(data)){    
								$this.text(oldVal);	
								top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
							}else{
								top.$.jBox.tip("保存数量成功！", 'info',{timeout:2000});
							}
						});
					}
					return true;
				}});
			
			  
			  $(".enterRemark").editable({
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var oldVal = $(this).text();
						param.itemId = $(this).parent().find(".itemId").val();
						param.content = encodeURI(newValue);
						param.flag="1";
						$.get("${ctx}/psi/transportForecastOrder/updateInfo?"+$.param(param),function(data){
							if(!(data)){    
								$this.text(oldVal);	
								top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
							}else{
								top.$.jBox.tip("保存备注成功！", 'info',{timeout:2000});
							}
						});
						return true;
					}});
			  
			  $(".enterReviewRemark").editable({
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var oldVal = $(this).text();
						param.itemId = $(this).parent().find(".itemId").val();
						param.content = encodeURI(newValue);
						param.flag="2";
						$.get("${ctx}/psi/transportForecastOrder/updateInfo?"+$.param(param),function(data){
							if(!(data)){    
								$this.text(oldVal);	
								top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
							}else{
								top.$.jBox.tip("保存审批备注成功！", 'info',{timeout:2000});
							}
						});
						return true;
					}});
			  
			  $("#inputForm").on("change",".countryCode",function(){
				  var  tr = $(this).parent().parent();
				  var name=tr.find(".nameColor").children('option:selected').val();
				  var country=tr.find(".countryCode").children('option:selected').val();
				  tr.find(".amazonStock").text("");
                  tr.find(".safeStock").text("");
                  tr.find(".day31sales").text("");
				  $.ajax({  
				        type : 'POST', 
				        url : '${ctx}/psi/transportForecastOrder/findQuantityInfo',  
				        dataType:"json",
				        data : "country="+ country+"&name="+name,  
				        async: true,
				        success : function(msg){
                            tr.find(".amazonStock").text(msg.fbaStock);
                            tr.find(".safeStock").text(msg.safeStock);
                            tr.find(".day31sales").text(msg.daySales);
                            var option = "";   
                            var skuList=msg.skuList;
    			            for(var i=0;i<skuList.length;i++){
     							option += "<option  value=\"" + skuList[i]+ "\">" + skuList[i] + "</option>"; 
     	                    }
    			            tr.find("[name='sku']").append(option);
				        }
					});
			  });
			  
			  $("#inputForm").on("change",".nameColor",function(){
				  var  tr = $(this).parent().parent();
				  var name=tr.find(".nameColor").children('option:selected').val();
				  var country=tr.find(".countryCode").children('option:selected').val();
				  tr.find(".amazonStock").text("");
                  tr.find(".safeStock").text("");
                  tr.find(".day31sales").text("");
				  $.ajax({  
				        type : 'POST', 
				        url : '${ctx}/psi/transportForecastOrder/findQuantityInfo',  
				        dataType:"json",
				        data : "country="+ country+"&name="+name,  
				        async: true,
				        success : function(msg){
                            tr.find(".amazonStock").text(msg.fbaStock);
                            tr.find(".safeStock").text(msg.safeStock);
                            tr.find(".day31sales").text(msg.daySales);
                            var option = "";   
                            var skuList=msg.skuList;
    			            for(var i=0;i<skuList.length;i++){
     							option += "<option  value=\"" + skuList[i]+ "\">" + skuList[i] + "</option>"; 
     	                    }
    			            tr.find("[name='sku']").append(option);
				        }
					});
			  });
			  
			  
			  $("#contentTable").on('click', '.remove-row', function(e){
				  e.preventDefault();
					//取消原来输入的数据
					var tr = $(this).parent().parent();
					var itemId = tr.find(".itemId").val();
					if(itemId){
						var param = {};
						param.itemId = itemId;
						$.get("${ctx}/psi/transportForecastOrder/deleteItem?"+$.param(param),function(data){
							if(!(data)){    
								top.$.jBox.tip("删除失败！", 'info',{timeout:2000});
							}else{
								top.$.jBox.tip("删除成功！", 'info',{timeout:2000});
								 tr.remove();
							}
						});
					}else{
						 tr.remove();
					}
					
			});
			  
			  $(".add-row").on("click",function(e){
					e.preventDefault();
					$(document).scrollTop($(document).height());
					var tbody=$("#contentTable tbody");
					var tr=$("<tr></tr>");
					var options="";
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek'}">
					   options+=' <option value="${dic.value}" >${dic.label}</option>';
					</c:if>      
				   </c:forEach>
					var nameOptions="";
				   <c:forEach items="${productAttr}" var="productEntry">
				       nameOptions+='<option value="${productEntry.key}">${productEntry.key}</option>';
				    </c:forEach>
				
					tr.append("<td><select class='countryCode' name='countryCode' style='width:95%'>"+options+"</select></td>");
					tr.append("<td><select class='transportType' name='transportType' style='width:95%'><option value='0'>本地运输</option><option value='1'>FBA运输</option></select></td>");
					tr.append("<td><select class='model' name='model' style='width:95%'><option value='0'>空运</option><option value='1'>海运</option><option value='2'>快递</option></select></td>");
		            tr.append("<td><select class='nameColor' name='nameColor' style='width:95%'>"+nameOptions+"</select></td>");
		            tr.append("<td style='vertical-align: middle;text-align: center' class='amazonStock'></td>");
		            tr.append("<td style='vertical-align: middle;text-align: center' class='safeStock'></td>");
		            tr.append("<td style='vertical-align: middle;text-align: center' class='day31sales'></td>");
		            tr.append("<td><select class='required sku' name='sku' style='width:95%'></select></td>");
		            tr.append("<td></td>");
		            tr.append("<td><input type='text' maxlength='11'  style='width: 70%'  name='boxNum'  class='number' /></td>");
		            tr.append("<td><input type='text' maxlength='11'  style='width: 85%'  name='checkQuantity'  class='required number' /></td>");
		            tr.append("<td><input type='text' maxlength='500' style='width: 85%'  name='remark' /></td>");
		            tr.append("<td></td>");
		            tr.append("<td><input type='hidden' class='itemId' /> <a class='save-row'>保存</a>&nbsp;&nbsp;</br><a href='#' class='remove-row'>删除</a></td>");
					tbody.append(tr);
					tr.find("select.transportType").select2();
					tr.find("select.countryCode").select2();
					tr.find("select.nameColor").select2();
					tr.find("select.model").select2();
					tr.find("select.sku").select2();
				});
				  
			  $("#contentTable").on('click', '.save-row', function(e){
				    e.preventDefault();
					var tr =$(this).parent().parent();
					var itemId =$(this).parent().find(".itemId").val();
					var $this=$(this);
					var remark=tr.find("input[name='remark']").val();
					var nameColor=tr.find("select[name='nameColor']").val();
					if("${nameStr}".indexOf(nameColor)>=0&&tr.find(".transportType").children('option:selected').val()=='1'){
						top.$.jBox.tip(nameColor+"配件不能选择FBA运输", 'info',{timeout:2000});
						return false;
					}
					var param = {};
					param.quantity = tr.find("input[name='quantity']").val();
					param.id = itemId;
					param.forecastOrderId="${psiTransportForecastOrder.id}";
					param.countryCode=tr.find("select[name='countryCode']").val();
					console.log(nameColor);
					if(nameColor.indexOf("_") != -1){
						var arr=nameColor.split("_");
						param.productName=arr[0];
						param.colorCode=arr[1];
					}else{
						param.productName=nameColor;
						param.colorCode="";
					}
					param.safeStock= tr.find(".safeStock").text();
					param.amazonStock=tr.find(".amazonStock").text();
					param.day31sales=tr.find(".day31sales").text();
					param.quantity=0;
					param.checkQuantity=tr.find("input[name='checkQuantity']").val();
					param.model=tr.find(".model").children('option:selected').val();
					param.transportType=tr.find(".transportType").children('option:selected').val();
					param.displaySta="2";
					param.boxNum=tr.find("input[name='boxNum']").val();
					param.sku=tr.find(".sku").children('option:selected').val();
					param.remark = encodeURI(remark);
					
					$.get("${ctx}/psi/transportForecastOrder/saveOrderItem?"+$.param(param),function(data){
						if(!(data)){    
							top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
						}else{
							top.$.jBox.tip("保存成功！", 'info',{timeout:2000});
							$this.parent().find(".itemId").val(data);
						}
					});
					return true;
				});
			  
				/* $("#passBtn").click(function(){
					var param = {};
					param.id="${psiTransportForecastOrder.id}";
					$.get("${ctx}/psi/transportForecastOrder/checkOrderQuantity?"+$.param(param),function(data){
						if(data=='0'){ 
							var submit = function (v, h, f) {
							    if (v == 'ok'){
							        $("#inputForm").attr("action","${ctx}/psi/transportForecastOrder/generateTransportOrder?id=${psiTransportForecastOrder.id}");
							        $("#inputForm").submit(); 
							        $("#inputForm").attr("action","");
							    }else if (v == 'cancel'){
							        jBox.tip(v, 'info');
							    }    
							    return true; //close
							};
							$.jBox.confirm("确认审核通过并拆分生成运单吗？", "提示", submit);
							//window.location.href="${ctx}/psi/transportForecastOrder/generateTransportOrder?id=${psiTransportForecastOrder.id}";
						}else{
							$.jBox.error(data, 'jBox');
						}
					});
					
				}); */
				$("#passBtn").click(function(){
					var param = {};
					param.id="${psiTransportForecastOrder.id}";
					$.get("${ctx}/psi/transportForecastOrder/checkOrderQuantity?"+$.param(param),function(data){
						  
							var submit = function (v, h, f) {
							    if (v == 'ok'){
							        $("#inputForm").attr("action","${ctx}/psi/transportForecastOrder/generateTransportOrder?id=${psiTransportForecastOrder.id}");
							        $("#inputForm").submit(); 
							        $("#inputForm").attr("action","");
							    }else if (v == 'cancel'){
							        jBox.tip(v, 'info');
							    }    
							    return true; //close
							};
							
							if(data=='0'){ 
								$.jBox.confirm("确认审核通过并拆分生成运单吗？", "提示", submit); 
							}else{
								$.jBox.confirm(data+"<br/>确认审核通过并拆分生成运单吗？", "提示", submit); 
							}
							//window.location.href="${ctx}/psi/transportForecastOrder/generateTransportOrder?id=${psiTransportForecastOrder.id}";
						
					});
					
				});
				
				$("#passBtn2").click(function(){
					var param = {};
					param.id="${psiTransportForecastOrder.id}";
					$.get("${ctx}/psi/transportForecastOrder/checkOrderQuantity?"+$.param(param),function(data){
						  
							var submit = function (v, h, f) {
							    if (v == 'ok'){
							        $("#inputForm").attr("action","${ctx}/psi/transportForecastOrder/generateTransportOrder?id=${psiTransportForecastOrder.id}");
							        $("#inputForm").submit(); 
							        $("#inputForm").attr("action","");
							    }else if (v == 'cancel'){
							        jBox.tip(v, 'info');
							    }    
							    return true; //close
							};
							
							if(data=='0'){ 
								$.jBox.confirm("确认审核通过并拆分生成运单吗？", "提示", submit); 
							}else{
								$.jBox.confirm(data+"<br/>确认审核通过并拆分生成运单吗？", "提示", submit); 
							}
							//window.location.href="${ctx}/psi/transportForecastOrder/generateTransportOrder?id=${psiTransportForecastOrder.id}";
						
					});
					
				});
				$(".tips").popover({html:true,trigger:'hover'});
		});
		
		function goBack(){
			window.location.href="${ctx}/psi/transportForecastOrder";
		}
		
		function goRefresh(){
			var id = "${psiTransportForecastOrder.id}";
			var nameColor = $("#nameColor").children("option:selected").val();
			var country = $("#country").children("option:selected").val();
			window.location.href="${ctx}/psi/transportForecastOrder/review?id="+id+"&country="+country+"&name="+nameColor;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/transportForecastOrder/list">预测运单列表</a></li>
		<li class="active"><a href="#">预测运单审核</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="psiTransportForecastOrder" action="" method="post" class="form-horizontal">
		<c:set var='createDate' value='<fmt:formatDate value="${psiTransportForecastOrder.createDate}" pattern="yyyy-MM-dd" />'/>
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:98%;height:30px" >
			&nbsp;&nbsp;
				<b>申请人:</b>	${psiTransportForecastOrder.createUser.name}
				&nbsp;&nbsp;
				<b>日期:</b>	<fmt:formatDate value="${psiTransportForecastOrder.createDate}" pattern="yyyy-MM-dd" />
				&nbsp;&nbsp;
				<b>产品:</b>
					<select id="nameColor" style="width:180px">
						<option value="" >全部</option>
						<c:forEach items="${productAttr}" var="productEntry">
							<option value="${productEntry.key}" ${name eq productEntry.key ?'selected':''}  >${productEntry.key}</option>
						</c:forEach>
					</select>
				&nbsp;&nbsp;
				<b>国家:</b>
				<select id="country" style="width:120px">
					<option value="" >全部</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							 <option value="${dic.value}" ${country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:if>      
					</c:forEach>
				</select>
				&nbsp;&nbsp;&nbsp;
				&nbsp;&nbsp;
				<b>Model:</b>
				<select class='model' id='model' style="width:80px">
				   <option value=''>全部</option>
				   <option value='0' ${transModel eq '0' ?'selected':''}>空运</option>
				   <option value='1' ${transModel eq '1' ?'selected':''}>海运</option>
				   <option value='2' ${transModel eq '2' ?'selected':''}>快递</option>
				   <option value='3' ${transModel eq '3' ?'selected':''}>铁路</option>
				</select>
				&nbsp;&nbsp;
				<b>运单:</b>
				<select class='lcOrSp' id='lcOrSp' style="width:80px">
				   <option value=''>全部</option>
				   <option value='0' ${transSta eq '0' ?'selected':''}>春雨</option>
				   <option value='1' ${transSta eq '1' ?'selected':''}>理诚</option>
				</select>
		      
		<%-- 		<a class="btn btn-small"  href="${ctx}/psi/psiTransportForecastOrder/exportSingle?psiTransportForecastOrderId=${psiTransportForecastOrder.id}">导出</a> --%>
		&nbsp;&nbsp;&nbsp;<a class="btn" id='passBtn'>通过</a>
				
				&nbsp;&nbsp;
				<div class="btn-group">
						   <button type="button" class="btn">导出</button>
						   <button type="button" class="btn dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" >
						      <li> <a  href="${ctx}/psi/transportForecastOrder/export?id=${psiTransportForecastOrder.id}&selCountry=${country}&name=${name}&transModel=${transModel}">导出</a></li>
						      <li><a href="${ctx}/psi/transportForecastOrder/exportEU?id=${psiTransportForecastOrder.id}&name=${name}&transModel=${transModel}">EU导出</a></li>
						     
						       <li><a href="${ctx}/psi/transportForecastOrder/exportAllCountry?id=${psiTransportForecastOrder.id}&name=${name}&transModel=${transModel}">所有国家导出1</a></li>
						      <li><a href="${ctx}/psi/transportForecastOrder/exportPackQuantity?id=${psiTransportForecastOrder.id}">所有国家导出2(春雨)</a></li>
						      <li><a href="${ctx}/psi/transportForecastOrder/exportPackQuantity2?id=${psiTransportForecastOrder.id}&createDate=${psiTransportForecastOrder.createDate}">所有国家导出2(理诚)</a></li>
						   </ul>
			</div>
 
				
				<!-- &nbsp;&nbsp;&nbsp;<input id="btnRefresh" class="btn" type="button" value="刷新" onclick="goRefresh()"/> 
				&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="goBack()"/>
				<input id="btnUpdateModel" class="btn" type="button" value="EU海运改铁路" onclick="goBack()"/> -->
				<a class="btn btn-warning" href="${ctx}/psi/transportForecastOrder/updateEuModel?id=${psiTransportForecastOrder.id}" onclick="return confirm('确认要将欧洲海运改成铁路运输吗？', this.href)">EU海运改铁路</a> 
			</div>
		</div>
	   <div style="float:left;width:100%">
		 <blockquote style="float:left;">
			 <div style="margin-bottom:5px"><p style="font-size: 14px;height:35px">产品信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		<!-- <div align="right" style="font-size: 14px;margin: 0px 100px 0px 0px;"><a href="#" class="add-row"><span class="icon-plus"></span>增加产品</a></div>
		 --></div>
		
		<div style="height:30px;position:fixed;z-index:1;left:20px;right:20px;top:115px" id="header">
		<table id="contentTable11" class="table table-bordered table-condensed" style="">
          	<thead >
				<tr >
			 	     <th style="width: 14%">产品</th>
				   <th style="width: 6%">运输类型</th>
				   <th style="width: 3%">国家</th>
				 
				  <th style="width: 4%">Model</th> 
				   <th style="width: 5%">FBA库存</th>
				   <th style="width: 4%">海外仓</th>
				     <th style="width: 3%">在途</th>
				   <th style="width: 3%">日销</th>
				   <th style="width: 4%">可销天</th>
				   <th style="width: 3%">空运</th>
				 <th style="width: 3%">缺口</th>
				   <th style="width: 11%">SKU</th>
				    <th style="width: 4%">运单</th>
				     <th style="width: 3%"><a title='中国仓+本周PO收货-新建运单'>库存</a></th>
				     <th style="width: 5%">装箱数</th>
				   <th style="width: 5%">系统数量</th>
				   
				   <th style="width: 5%">审核数量</th>
				   <th style="width: 10%">备注</th>
				<!--    <th style="width: 10%">审批备注</th> -->
				</tr>
			</thead>
		</table>
		</div>

			<table id="contentTable" class="table table-bordered table-condensed" style="overflow-y:scroll;">
<c:set var='totalBox' value='0'/>
			<c:set var='totalVolume' value='0'/>
			<c:set var='totalWeight' value='0'/>
		   <colgroup>
               <col style="width: 14%"/>
               <col style="width: 6%"/>
                 <col style="width:3%"/>
               <col style="width: 4%"/>
               <col style="width: 5%"/>
               <col style="width: 4%"/>
               <col style="width: 3%"/>
               <col style="width: 3%"/>
               <col style="width: 4%"/>
             <col style="width:3%"/>
               <col style="width:3%"/>
               <col style="width: 11%"/>
                <col style="width: 4%"/>
                <col style="width:3%"/>
               <col style="width: 5%"/>
               <col style="width: 5%"/>
               <col style="width: 5%"/>
               <col style="width: 10%"/>
             <!--   <col style="width: 10%"/> -->
         	 </colgroup>
		   <tbody>
			 <c:forEach items="${map}" var="temp" varStatus="i">
			                <c:set var='nameCount' value='0'/>
					        <c:forEach items="${map[temp.key]}" var="tranType0">
					             <c:forEach items="${map[temp.key][tranType0.key]}" var="model0">
					                <c:forEach items="${map[temp.key][tranType0.key][model0.key]}" var="order0">
					                      <c:set var='nameCount' value='${nameCount+1}'/>
					                </c:forEach>
					              </c:forEach> 
					        </c:forEach> 
					        <c:set var='nameFlag' value='0'/>      
			     <c:forEach items="${map[temp.key]}" var="tranType" varStatus="j">
			               <c:set var='typeCount' value='0'/>
			               <c:forEach items="${map[temp.key][tranType.key]}" var="model1">
			                <c:forEach items="${map[temp.key][tranType.key][model1.key]}" var="order1">
			                      <c:set var='typeCount' value='${typeCount+1}'/>
			                </c:forEach>
			              </c:forEach>
			               <c:set var='typeFlag' value='0'/> 
			        <c:forEach items="${map[temp.key][tranType.key]}" var="model" varStatus="k">
			           <c:forEach items="${map[temp.key][tranType.key][model.key]}" var="order" varStatus="m">
			              <c:set var='totalBox' value='${totalBox+order.boxQuantity }'/>
						<c:set var='totalVolume' value='${totalVolume+order.volume }'/>
						<c:set var='totalWeight' value='${totalWeight+order.weight }'/>
			              <tr style="${order.checkQuantity!=order.quantity?'background-color: #f9f9f9;':'' }">    
			               <c:if test="${nameFlag=='0'}">
				                <td style="vertical-align: middle;text-align: left" rowspan="${nameCount}">
						             <a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${order.productNameColor }">${order.productNameColor }<c:if test="${'1' eq powerMap[order.productNameColor] }"><span style="font-size:12px;color:red">(带电)</span></c:if></a>
						        </td>
					             <c:set var='nameFlag' value='1'/> 
				           </c:if>   
				           <c:if test="${typeFlag=='0'}">
						     <td style="vertical-align: middle;text-align: left" rowspan="${typeCount}">${order.transportTypeName }</td>
						     <c:set var='typeFlag' value='1'/> 
				           </c:if>
				           <c:if test="${m.count==1}">
					          <td style="vertical-align: middle;text-align: left" rowspan="${fn:length(model.value)}">${'com' eq model.key?'us':model.key}</td> 
					        </c:if>  
				           
				            <td style="vertical-align: middle;text-align: left" >${order.modelName }</td>
				            <td style="vertical-align: middle;text-align: center" >${order.amazonStock }</td>
				            <td style="vertical-align: middle;text-align: center" >${order.overseaStock }</td>
				                <td style="vertical-align: middle;text-align: left" >
				               <c:if test="${not empty order.detail}"><a  title="${order.detail}" href="#">查看</a></c:if>
				             </td>
				            <td style="vertical-align: middle;text-align: center" >${order.day31sales }</td>
				            <td style="vertical-align: middle;text-align: center" ><c:if test="${'1' ne  order.salesDay}">${order.salesDay }</c:if></td>
				             <td style="vertical-align: middle;text-align: left" > <c:if test="${'2' ne order.displaySta }"> <span style="${order.totalAir+order.totalExp>0?'color:#32CD32;':'' }">${order.totalAir+order.totalExp}</span></c:if></td>
				            
				           <td style="vertical-align: middle;text-align: left" >${order.gap }</td>
				            <td style="vertical-align: middle;text-align: left" >${order.sku }</td>
				            <td style="vertical-align: middle;text-align: left" >${'1' eq order.transSta?'理诚':'春雨' }</td>
				             <td style="vertical-align: middle;text-align: center" >
				             <c:if test="${'2' ne order.displaySta }">
				             <a  class="tips" rel="popover" data-placement="left" data-content="PO:${order.poStock}<br/>NEW:${order.transStock};<br/>CN:${not empty order.reviewRemark?order.reviewRemark:((order.totalStock-order.poStock+order.transStock)>0?(order.totalStock-order.poStock+order.transStock):0) }" href="#">${order.totalStock }</a>
				             </c:if>
				             </td>
				               <td style="vertical-align: middle;text-align: center" >${order.boxNum }</td>
				            <td style="vertical-align: middle;text-align: center" >${order.quantity }</td>
				           
				            <td style="vertical-align: middle;text-align: center" >
				               <input type="hidden" class="itemId" value="${order.id}" /> 
				               <a href="#" class="enterQuantity"  data-type="number" data-pk="1" data-title="Enter Quantity" data-value="${order.checkQuantity}">${order.checkQuantity}</a>
				            </td>
				            <td style="vertical-align: middle;text-align: left" >
				              <input type="hidden" class="itemId" value="${order.id}" /> 
				              <a href="#" class="enterRemark"  data-type="text" data-pk="1" data-title="Enter Remark" data-value="${order.remark}">${order.remark}</a>
				            </td> 
				          <%--   <td style="vertical-align: middle;text-align: left" >
				               <input type="hidden" class="itemId" value="${order.id}" /> 
				               <a href="#" class="enterReviewRemark"  data-type="text" data-pk="1" data-title="Enter ReviewRemark" data-value="${order.reviewRemark}">${order.reviewRemark}</a>
				            </td>   --%>
				            
				         </tr>
			          </c:forEach>
			        </c:forEach>
				 </c:forEach>	
			  </c:forEach> 
			</tbody>
		</table>
		
		<div class="form-actions" style="height:60px">
		&nbsp;&nbsp;&nbsp;<a class="btn" id='passBtn2' >通过</a>
		<input id="btnRefresh" class="btn" type="button" value="刷新" onclick="goRefresh()"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="goBack()"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		箱数：${totalBox } &nbsp;&nbsp;&nbsp;&nbsp;
		<c:if test="${totalVolume>0 }">体积：<fmt:formatNumber value=" ${totalVolume }" maxFractionDigits="2"/>&nbsp;&nbsp;&nbsp;&nbsp;</c:if>
		<c:if test="${totalWeight>0 }"> 重量：<fmt:formatNumber value="${totalWeight } " maxFractionDigits="2"/></c:if>
		
		</div>
	</form:form>
</body>
</html>
