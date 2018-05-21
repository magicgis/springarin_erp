<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {

			$("#sysnBtn").click(function(e){
			
				var p={};
				var proType=$("#proType").val();
				var discount=$("#gets").val();
				var discountNumber=$("#getsNumber").val().trim();
				var purchases=$("#purchases").val();
				var purchasesNumber=$("#purchasesNumber").val().trim();
				var qualifyingItem=$("#qualifyingItem").val();
				p.proId=$("#promotionId").val();
				p.proType=proType;
				p.country='${amazonPromotionsWarning.country}';
				p.accountName='${amazonPromotionsWarning.accountName}';
				p.buyerPurchases=purchases;
				p.buyerGets=discount;
				p.purchaseStr=purchasesNumber;
				p.offPriceStr=discountNumber;
				p.qualifyingItem=qualifyingItem;
				var asin="";
				$("#contentTable tbody tr").each(function(i,j){
					$(j).find("select").each(function(){
						if($(this).attr("name")=='asin'){
							asin+=$(this).val()+",";
						}
					});
					
				}); 
				p.asin=asin.substring(0,asin.length-1);
				if(discountNumber==''||purchasesNumber==''){
					top.$.jBox.tip("Buyer purchases或And gets不能为空");
					return false;
				}
				
				if(discount=='Free items'&&qualifyingItem==''){
					top.$.jBox.tip("Qualifying Item不能为空");
					return false;
				}
				var promotionId=$("#proThreeType").val();
				if(promotionId.indexOf('S-')==0&&discount!='Free items'){
					top.$.jBox.tip("S-折扣只能选择Free items");
					return false;
				}
				if(promotionId.indexOf('S-')!=0&&discount=='Free items'){
					top.$.jBox.tip("只有S-折扣能选择Free items");
					return false;
				}
				$.ajax({
				    type: 'get',
				    async:false,
				    url: '${ctx}/amazoninfo/promotionsWarning/countProfit' ,
				    data: $.param(p),
				    success:function(data){ 
				    	if(data){//not 
				    		if(data.indexOf("<font")<0){
				    			$("#btnSubmit").removeAttr("disabled");
				    		}
				    		top.$.jBox.open(data,"折扣分析",500,220,{buttons: {'关闭': true}});  
				    	}else{
				    		$("#btnSubmit").removeAttr("disabled");
				    		top.$.jBox.tip("折扣正常","info",{timeout:1000});
							return false;
				    	}
			        }
				});
			});
			
			$("#checkBtn").click(function(e){
				
					var p={};
					var proType=$("#proType").val();
					var discount=$("#gets").val();
					var discountNumber=$("#getsNumber").val().trim();
					var purchases=$("#purchases").val();
					var purchasesNumber=$("#purchasesNumber").val().trim();
					
					p.proId=$("#promotionId").val();
					p.proType=proType;
					p.country='${amazonPromotionsWarning.country}';
					p.accountName='${amazonPromotionsWarning.accountName}';
					p.buyerPurchases=purchases;
					p.buyerGets=discount;
					p.purchaseStr=purchasesNumber;
					p.offPriceStr=discountNumber;
					var asin="";
					$("#contentTable tbody tr").each(function(i,j){
						$(j).find("select").each(function(){
							if($(this).attr("name")=='asin'){
								asin+=$(this).val()+",";
							}
						});
						
					}); 
					p.asin=asin.substring(0,asin.length-1);
					if(discountNumber==''||purchasesNumber==''){
						top.$.jBox.tip("Buyer purchases或And gets不能为空");
						return false;
					}
				
					$.ajax({
					    type: 'post',
					    async:false,
					    url: '${ctx}/amazoninfo/promotionsWarning/checkPromotions',
					    data: $.param(p),
					    success:function(data){ 
					    	$("#checkPromotionsTable").find(".modal-body").html(data);
				        }
				   });
				   $("#checkPromotionsTable").modal();
			});
			
			$("#countryAndAccount").on("change",function(e){
				var params = {};
				params['country'] = $(this).find("option:selected").attr("countryVal");
				params['accountName'] = $(this).val();
				window.location.href = "${ctx}/amazoninfo/promotionsWarning/add?"+$.param(params);
			});
			
			$('#add-row').on('click', function(e){
					e.preventDefault();
				    var tbody = $('#contentTable tbody');
		            var tr = $("<tr></tr>");
		            var td ="<td>   <input type='hidden' name='productNameColor' class='productNameColor'/><select style='width: 90%' class='asin'  name='asin' onChange='setQuantity(this);'>";
		            
		            var option="<option value=''>-请选择产品-</option>";
					<c:forEach items="${asinMap}"   var="asinMap">
					    option+="<option  nameVal='${asinMap.value }' value='${asinMap.key}'>${asinMap.value }[${asinMap.key}]</option>	";								
				    </c:forEach>
		            td = td.concat(option+"</select></td>");
		            tr.append(td);
		            tr.append("<td> <input type='text' style='width: 80%' class='attr' readonly /></td>");
		            tr.append("<td> <input type='text' style='width: 80%' class='fbaQuantity' readonly /></td>");
		            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='halfHourQuantity'   class='number' /></td>");
		            tr.append("<td> <input type='text' maxlength='11' style='width: 80%'  name='cumulativeQuantity' class='number' /></td>");
		           
		            tr.append("<td> <input type='text' maxlength='200'style='width: 80%' name='remark' /></td>");
		            tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除产品</a></td>");
		            tr.find("select.asin").select2();
		            tbody.append(tr);
		           
			});
			
			
			$('#contentTable').on('click', '.remove-row', function(e){
				e.preventDefault();
				  if($('#contentTable tbody tr').size()>1){
					  var tr = $(this).parent().parent();
					 // var id = tr.find(".asin").select2("val");
					  tr.remove();
					 /*  if(id){
						  $("select.asin").each(function(){
		          				$(this).append("<option value='"+id+"'>"+asinMap[id]+"</option>");
		          		  });
					  }
					   */
					  $("#model").val('');
						var model="";
						$("#contentTable tbody tr").each(function(i,j){
							$(j).find("select").each(function(){
								if($(this).attr("name")=='asin'){
									var name=$(this).find("option:selected").attr("nameVal");
									if(name.indexOf("_")>=0){
										singleModel=name.substring(name.indexOf(" ")+1,name.lastIndexOf("_"));
									}else{
										singleModel=name.substring(name.indexOf(" ")+1);
									}
									if(model.indexOf(singleModel)<0&&model.split(" ").length<=2){
										if(model==''){
											model=singleModel;
										}else{
											model=model+" "+singleModel;
										}
									}
								}
							});
						}); 
						$("#model").val(model);
						setPromotionsId();
				  }
				  
			});
			
			
			$("#inputForm").validate({
				rules:{
					
				},
				messages:{
					
				},
				submitHandler: function(form){
					var p={};
					p.promotionId=encodeURI(promotionId);
					p.country=$("#country").val();
					$.ajax({
					    type: 'get',
					    async:false,
					    url: '${ctx}/amazoninfo/promotionsWarning/isExistPromotionId' ,
					    data: $.param(p),
					    success:function(data){ 
					    	if(data=='0'){//not 
					    		$("#contentTable tbody tr").each(function(i,j){
									$(j).find("select").each(function(){
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
					    		var proType=$("#proType").val();
								if(proType=='3'){
                                     $("#checkUser").val($("#checkUser2").val());
								}else{
									 $("#checkUser").val($("#checkUser1").val());
								}
								
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
					    	}else{
					    		top.$.jBox.tip("该折扣ID已被使用，未保存成功","info",{timeout:3000});
								return false;
					    	}
				        }
					});
					
					
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
				}
			});
			
			$("#proThreeType,#model,#gets,#platform,#purchases,#qualifyingItem").on("change",function(e){
				setPromotionsId();
			});
			
			$("#gets").on("change",function(e){
				setPromotionsId();
				if($(this).val()=='Post-order benefit'){
					 $("#getsNumber").val(0);
					 $("#getsNumber").hide();
					 $("#itemDiv").css("display","none");
				}else if($(this).val()=='Free items'){
					 $("#getsNumber").val(1);
					 $("#getsNumber").hide();
				     $("#itemDiv").css("display","block");
				}else{
					$("#getsNumber").val('');
					$("#getsNumber").show();
					 $("#itemDiv").css("display","none");
				}
			});
			
		});
		
		function setQuantity(c){
			$("#btnSubmit").attr("disabled","disabled");
			$("#model").val('');
			var model="";
			var name=$(c).find("option:selected").attr("nameVal");
			var attr=$(c).find("option:selected").attr("attrVal");
			$(c).parent().parent().find(".attr").val(attr);
			$("#contentTable tbody tr").each(function(i,j){
				$(j).find("select").each(function(){
					if($(this).attr("name")=='asin'){
						var name=$(this).find("option:selected").attr("nameVal");
						if(name.indexOf("_")>=0){
							singleModel=name.substring(name.indexOf(" ")+1,name.lastIndexOf("_"));
						}else{
							singleModel=name.substring(name.indexOf(" ")+1);
						}
						if(model.indexOf(singleModel)<0&&model.split(" ").length<=2){
							if(model==''){
								model=singleModel;
							}else{
								model=model+" "+singleModel;
							}
						}
					}
				});
			}); 
			$("#model").val(model);
			setPromotionsId();
			$.ajax({  
		        type : 'POST', 
		        url : '${ctx}/amazoninfo/promotionsWarning/findQuantityInfo',  
		        dataType:"json",
		        data : "country="+$("#country").val()+"&name="+name+"&accountName="+$("#accountName").val(),  
		        async: true,
		        success : function(msg){
		        	$(c).parent().parent().find(".fbaQuantity").val(msg.fbaStock);
		        	$(c).parent().parent().find(".productNameColor").val(name);
		        }
			});   
		}
		
		function isShowDiv(){
			var promotionId=$("#proThreeType").val();
			if(promotionId.indexOf('F-')==0){
			    $("#typeDiv").css("display","block");
				$("#proType").empty(); 
				$("#proType").append("<option value='0'>亏本非淘汰品促销 </option>");
				$("#proType").append("<option value='1'>亏本淘汰品促销</option>");
				$("#proType").append("<option value='2'>有利润促销</option>");
				$("#proType").append("<option value='3'>特批</option>");
				$("#proType").select2("val","0");
			}else if(promotionId.indexOf('S-')==0){
				$("#typeDiv").css("display","block");
				$("#proType").empty(); 
				$("#proType").append("<option value='3'>特批</option>");
				$("#proType").append("<option value='4'>常规</option>");
				$("#proType").select2("val","4");
			}else{
			    $("#typeDiv").css("display","none");
			    $("#proType").empty(); 
				$("#proType").append("<option value='0'>亏本非淘汰品促销 </option>");
				$("#proType").append("<option value='1'>亏本淘汰品促销</option>");
				$("#proType").append("<option value='2'>有利润促销</option>");
				$("#proType").append("<option value='3'>特批</option>");
			}
			if(promotionId.indexOf('C-')==0){
			    $("#platformDiv").css("display","none");
			}else{
			    $("#platformDiv").css("display","block");
			}
			
		}
		function showCheckUser(){
			var proType=$("#proType").val();
			if(proType=='3'){
			    $("#allUser").css("display","none");
			    $("#partUser").css("display","block");
			}else{
			    $("#allUser").css("display","block");
			    $("#partUser").css("display","none");
			}
		}
		
		function setPromotionsId(){
			//F- 国家+ 平台 + 产品+自定义折扣力度+创建时间， 如F-US FB PC4001 off 50% 2016-12-23 9:00 
			var proType=$("#proThreeType").val();
			//var country="${'com' eq amazonPromotionsWarning.country?'us':amazonPromotionsWarning.country}".toUpperCase();
			var country="${amazonPromotionsWarning.accountName}";
			var tempCnt=country.split("_");
			tempCnt=(tempCnt[0].substring(0,3)+"_"+tempCnt[1]).toUpperCase();
			var model=$("#model").val();
			var discount=$("#gets").val();
			var platform=$("#platform").val();
			var discountNumber=$("#getsNumber").val().trim();
			var discountInfo="";
			var purchases=$("#purchases").val();
			var purchasesNumber=$("#purchasesNumber").val().trim();
			if(discountNumber==''||purchasesNumber==''){
				//top.$.jBox.tip("Buyer purchases或And gets不能为空");
				return false;
			}
			if("Percent off"==discount){
				discountInfo="off "+discountNumber+"%";
			}else if(discount=='Free items'){
				var name=$("#qualifyingItem").find("option:selected").attr("nameKey");
				if(name.indexOf("_")>=0){
					singleModel=name.substring(name.indexOf(" ")+1,name.lastIndexOf("_"));
				}else{
					singleModel=name.substring(name.indexOf(" ")+1);
				}
				discountInfo="off "+singleModel;
			}else{
				discountInfo="off "+discountNumber;
			}
			var date = new Date();
			var year = date.getFullYear();
			var month = date.getMonth()+1;
			var day = date.getDate();
			var hour = date.getHours();
			var minute = date.getMinutes();
			var second = date.getSeconds();
			var datestr=year+"-"+month+"-"+day+" "+hour+"/"+minute+"/"+second;
			if(proType=='C-'){
				platform='A-Page';
			}
			$("#promotionId").val(proType+tempCnt+" "+platform+" "+model+" "+discountInfo+" "+datestr);
			
			$('#buyerGets').val(discount+" "+discountNumber);
			$('#buyerPurchases').val(purchases+" "+purchasesNumber);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/promotionsWarning/">折扣预警列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/promotionsWarning/add">新建折扣审核</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="amazonPromotionsWarning" action="${ctx}/amazoninfo/promotionsWarning/addSave" method="post" class="form-horizontal">
	  <form:hidden path="id"/>
	 
	  <table >
		   <tr><td>
		<div class="control-group">
				<label class="control-label">国家:</label>
				<div class="controls">
				    <input name='country' type='hidden' id='country' value='${amazonPromotionsWarning.country}'/>
				    <input name='accountName' type='hidden' id='accountName' value='${amazonPromotionsWarning.accountName}'/>
				     
					<select name="countryAndAccount" id="countryAndAccount">
						  <option value="" selected="selected">-请选择国家-</option>
							<shiro:hasPermission name="amazoninfo:feedSubmission:all">
								  <c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
							          <c:forEach items="${accountMap[dic.value]}" var="account">
									      <option value="${account}" countryVal='${dic.value}'>${account}</option>
									  </c:forEach>
							     </c:forEach>
							</shiro:hasPermission>
							<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
							     <c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
							          <shiro:hasPermission name="amazoninfo:feedSubmission:${'com2' eq dic.value||'com3' eq dic.value?'com':dic.value}">
							               <c:forEach items="${accountMap[dic.value]}" var="account">
									           <option value="${account}" countryVal='${dic.value}'>${account}</option>
									       </c:forEach>
							          </shiro:hasPermission>
							     </c:forEach>
							</shiro:lacksPermission>	
					</select>
					<script type="text/javascript">
					  $("option[value='${amazonPromotionsWarning.accountName}']").attr("selected","selected");				
				   </script>
				</div>
			</div>
		</td>
		    <td>
		    <div class="control-group">
				<label class="control-label">折扣类型:</label>
				<div class="controls">
					 <select  id="proThreeType" style="width:150px" onchange="isShowDiv();">
					     <option value="F-">F-</option>
					     <option value="C-">C-</option>
					     <option value="R-">R-</option>
					     <option value="S-">S-</option>
					 </select>
				</div>
			</div>
		  </td>
		  <td></td>
		<%-- <td>
		     <div class="control-group">
				<label class="control-label">产品:</label>
				<div class="controls">
					 <select  id="model">
					    <c:forEach items="${model}"   var="model">
							<option  value="${model.model}">${model.model }</option>									
						</c:forEach>
					 </select>
				</div>
			</div>
		  </td> --%>
		</tr>
		<tr>
		 <td colspan='2'>
		
		   <div class="control-group" >
				<label class="control-label">Buyer purchases:</label>
				<div class="controls">
					 <select  id="purchases">
					     <option value="At least this quantity of items">At least this quantity of items</option>
						 <c:if test="${'uk' eq amazonPromotionsWarning.country}">
						     <option value="At least amount (in £)">At least amount (in £)</option>
						 </c:if>
						 <c:if test="${'com' eq amazonPromotionsWarning.country ||'ca' eq amazonPromotionsWarning.country}">
						     <option value="At least amount (in $)">At least amount (in $)</option>
						 </c:if>   
						 <c:if test="${'jp' eq amazonPromotionsWarning.country}">
						     <option value=" At least amount (in ￥)"> At least amount (in ￥)</option>
						 </c:if>        
						 <c:if test="${fn:contains('de,fr,it,es',amazonPromotionsWarning.country)}">
						     <option value="At least amount (in €)">At least amount (in €)</option>
						  </c:if>                        
					    
					 </select>
					 <input name="pruchasesNumber"  type="text" maxlength="10" class="input-small price required" id="purchasesNumber" onkeyup="setPromotionsId();"/>
				</div>
			</div>
		</td>
		
		
		<td>
		<div class="control-group"  id='typeDiv'>
				<label class="control-label">类型:</label>
				<div class="controls">
					 <select name="proType" id="proType" onchange='showCheckUser();'>
					     <option value="0">亏本非淘汰品促销 </option>
					     <option value="1">亏本淘汰品促销</option>
					     <option value="2">有利润促销</option>
					     <option value="3">特批</option>
					 </select>
				</div>
			</div>
		</td>
		

		</tr>
		<tr>
		 <td colspan='2'>
		
		   <div class="control-group" >
				<label class="control-label">And gets:</label>
				<div class="controls">
				     <input type='hidden' name='buyerGets' id='buyerGets'/>
				     <input type='hidden' name='buyerPurchases' id='buyerPurchases'/>
					 <select  id="gets">
					     <c:if test="${'uk' eq amazonPromotionsWarning.country}">
						     <option value="Amount off (in £)">Amount off (in £)</option>
						 </c:if>
						 <c:if test="${'ca' eq amazonPromotionsWarning.country}">
						      <option value="Amount off (in $)">Amount off (in $)</option>
						 </c:if>
						 <c:if test="${'jp' eq amazonPromotionsWarning.country}">
						     <option value=" Amount off (in ￥)">Amount off (in ￥)</option>
						 </c:if>
						 <c:if test="${fn:contains('de,fr,it,es',amazonPromotionsWarning.country)}">
						    <option value="Amount off (in €)">Amount off (in €)</option>
						  </c:if>
					     <option value="Percent off">Percent off</option>
					     <option value="Post-order benefit">Post-order benefit</option>
					     <option value="Free items">Free items</option> 
					 </select>
					 <input name="getsNumber"  type="text" maxlength="10" class="input-small price required" id="getsNumber"  onkeyup="setPromotionsId();"/>
				</div>
			</div>
		</td>
		  <td>
		    <div class="control-group" id='platformDiv'>
				<label class="control-label">平台:</label>
				<div class="controls">
					 <select  id="platform">
					     <option value="A-Page">Amazon Page Promotion</option>
					     <option value="AMZ">Amazon tracker</option>
					     <option value="TW">Twitter</option>
					     <option value="FB">Facebook</option>
					     <option value="INS">Instagram</option>
					     <option value="PIN">Pinterest</option>
					     <option value="WEB">官网</option>
					     <option value="deal">Deal网站</option>
					 </select>
				</div>
			</div>
		  </td>
		 
		
		</tr>
		
		<tr>
		  <td colspan='3'>
			<div class="control-group" id='itemDiv' style="display:none;">
					<label class="control-label">Qualifying Item:</label>
					<div class="controls">
						  <select style='width:30%' class='qualifyingItem'  name='qualifyingItem' id='qualifyingItem'>
							    <option value=''>-请选择产品-</option>
								<c:forEach items="${asinMap}"   var="asinMap">
									<option  value="${asinMap.key}" nameKey="${asinMap.value}">${asinMap.value }[${asinMap.key}]</option>									
								</c:forEach>
						</select>
					</div>
				</div>
			</td>
		</tr>
		
		
	<tr>
		<td  colspan="2">
		  <div class="control-group" >
				<label class="control-label">日期:</label>
				<div class="controls">
				    <input style="width: 180px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${amazonPromotionsWarning.startDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" class="input-small required" id="start"/>
				    -
				    <input style="width: 180px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${amazonPromotionsWarning.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" class="input-small required" id="end"/>
				</div>
			</div>
		</td>
		<td>
		  <input type='hidden' name="checkUser.id" id='checkUser'/>
		<div class="control-group" id='allUser'>
				<label class="control-label">审核人:</label>
				<div class="controls">
					<select  style="width:150px" id="checkUser1">
						<c:forEach items="${all}" var="user">
						   <c:if test="${fns:getUser().name ne user.name}">
							   <option value="${user.id}">${user.name}</option>
							</c:if>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="control-group" id='partUser' style='display:none'>
				<label class="control-label">审核人:</label>
				<div class="controls">
					<select style="width:150px" id="checkUser2">
						<c:forEach  items="${specialUser}" var="user">
						   <c:if test="${fns:getUser().name ne  user.name}"><option value="${user.id}">${user.name}</option></c:if>
						</c:forEach>
					</select>
				</div>
			</div>
		</td>
		</tr>
		<tr>
		  <td colspan='3'>
			<div class="control-group" >
					<label class="control-label">Tracking Id:</label>
					<div class="controls">
						<input name="promotionId"  type="text" maxlength="100" class="input-xxlarge required" id="promotionId" readonly/>
						<input type="button" onclick="setPromotionsId();" class='btn btn-primary' value="生成折扣ID"/>
						
					</div>
				</div>
			</td>
		</tr>
		
		
		<tr>
		  <td colspan="3">
		   <div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<textarea name="reason" class='required'   maxlength="1000" style="margin: 0px; width: 600px; height: 100px;"></textarea>
			</div>
		  </div>
		  </td>
		</tr>
		
		</table>
		    
		   
		
		<div style="float:left;width:100%">
		 <blockquote>
		 <div style="float: left"><p style="font-size: 14px">折扣信息&nbsp;&nbsp;产品：<input type='text' id='model' onkeyup="setPromotionsId();" class='required'/></p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加产品</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				   <th style="width: 30%">产品</th>
				   <th style="width: 10%">属性</th>
				   <th style="width: 10%">Fba库存数</th>
				   <th style="width: 10%">半小时峰值</th>
				   <th style="width: 10%">累计销量</th>
				   <th style="width: 20%">备注</th>
				   <th style="">操作</th>
				</tr>
			</thead>
			<tbody>
						<tr>
						    <td>
							     <input type='hidden' name='productNameColor' class='productNameColor'/>
							     <select style='width: 90%' class='asin'  name='asin' onChange='setQuantity(this);'>
							        <option value=''>-请选择产品-</option>
									<c:forEach items="${asinMap}"   var="asinMap">
										<option  nameVal='${asinMap.value }' value="${asinMap.key}" attrVal="${'4' eq productPositionMap.get(asinMap.value)?'淘汰':('0' eq productIsNewMap.get(asinMap.value)?'普通':'新品') }">${asinMap.value }[${asinMap.key}]</option>									
									</c:forEach>
							    </select>
						   </td>
						    <td> <input type='text' style='width: 80%' class='attr' readonly /></td>
							<td> <input type='text' style='width: 80%' class='fbaQuantity' readonly /></td>
							<td> <input type='text' maxlength='11' style='width: 80%'  name='halfHourQuantity'   class='number' /></td>
				            <td> <input type='text' maxlength='11' style='width: 80%'  name='cumulativeQuantity' class='number' /></td>
				            <td> <input type='text' maxlength='200'style='width: 80%' name='remark' /></td>
				            <td> <a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除产品</a></td>
						</tr>
					</tbody>
		</table>
		
		<div id="checkPromotionsTable" class="modal hide fade" tabindex="-1"  style="width:530px;">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			</div>
			<div class="modal-body">
				 
			</div>
		</div>
		
		<div class="form-actions">
		    <input type="button"  id='sysnBtn' class='btn btn-primary' value="利润分析"/>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" disabled/>&nbsp;
			 <input type="button" id='checkBtn' class='btn btn-primary' value="折扣检测"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
