<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>预测下单编辑</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
			if(!(top)){
				top = self; 
			}
			 $(window).scroll(function() {
			        var scrollHeight = $(document).scrollTop();
			        if(scrollHeight>=100){
			        	$("#header").css("top","2px");   
			        }else{
			        	$("#header").css("top","115px");    
			        }     
			    });
			$(".enterQuantity").editable({
			showbuttons:'bottom',
			success:function(response,newValue){
				if(newValue){
					var param = {};
					var $this  =$(this);
					var table = $(this).parent().parent().parent();
					var proName ="."+$this.parent().find(".proName").val();
					var oldVal = $(this).text();
					param.itemId = $(this).parent().find(".itemId").val();
					param.quantity = newValue;
					param.flag="0";
					$.get("${ctx}/psi/forecastOrder/updateQuantity?"+$.param(param),function(data){
						if(data!=''){    
							top.$.jBox.tip("超标："+data, 'info',{timeout:4000});
							table.find(proName).css("display","block");
						}else{
							table.find(proName).css("display","none");
							top.$.jBox.tip("保存数量成功！", 'info',{timeout:2000});
						}
					});
					var remark="ok";
					if(newValue==0){
						remark="不需要下单";
					}
					var oldRemark=$(this).parent().parent().find(".enterRemark");
					oldRemark.text(remark);
					var param = {};
					param.itemId = $(this).parent().find(".itemId").val();
					param.remark = encodeURI(remark);
					param.flag="0";
					$.get("${ctx}/psi/forecastOrder/updateRemark?"+$.param(param),function(data){});
					
				}
				return true;
			}});
			
			
			$(".enterRemark").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.itemId = $(this).parent().find(".itemId").val();
					param.remark = encodeURI(newValue);
					param.flag="0";
					$.get("${ctx}/psi/forecastOrder/updateRemark?"+$.param(param),function(data){
						if(!(data)){
							top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
						}else{
							top.$.jBox.tip("保存备注成功！", 'info',{timeout:2000});
						}
					});
					return true;
				}});
			
			$("#contentTable").on('click', '.save-row', function(e){
			    e.preventDefault();
				var tr =$(this).parent().parent();
				var $this  =$(this);
				var table = tr.parent();
				var proName ="."+tr.find(".proName").val();
				var itemId =$(this).parent().find(".itemId").val();
				var remark=tr.find("input[name='remark']").val();
				if(!remark){
					top.$.jBox.tip("备注不能为空", 'info',{timeout:2000});
					return false;
				}
				var param = {};
				param.quantity = tr.find("input[name='quantity']").val();
				param.itemId = itemId;
				param.remark = encodeURI(remark);
				param.flag="0";
				$.get("${ctx}/psi/forecastOrder/updateRemarkAdd?"+$.param(param),function(data){
					if(data!=''){    
						top.$.jBox.tip("超标："+data, 'info',{timeout:4000});
						table.find(proName).css("display","block");
					}else{
						table.find(proName).css("display","none");
						top.$.jBox.tip("保存数量成功！", 'info',{timeout:2000});
					}
					tr.find("input[name='quantity']").attr("readonly","readonly");
					tr.find("input[name='remark']").attr("readonly","readonly");
					tr.find(".productId").attr("disabled","disabled");
					tr.find(".countryCode").attr("disabled","disabled"); 
				});
				return true;
			});
			
			
			$(".tips").popover({html:true,trigger:'hover'});
			var countryMap = {};
			<c:forEach items="${fns:getDictList('platform')}" var="dic">
				countryMap['${dic.value}'] = "${dic.value eq 'com'?'us':dic.value}";
			</c:forEach>
			eval('var supMap=${supMap}');
			eval('var orderDateJson=${orderDateJson}');
			
			
			eval('var itemMap=${itemMap}');
			eval('var proCountryMap=${proCountryMap}');
			
			
			var productArgs ={};
			var productColorArgs ={};
			var colorArgs =[];
			var countryArgs=[];
			var  colorsStr;
			var  countryStr;
			var  product;
			<c:forEach items="${products}" var="product" varStatus="i">
			  	product={};
			  	product.id='${product.id}';
			  	product.color='${product.color}';
			  	product.name='${product.name}';
			  	product.platform ='${product.platform}';
			  	product.minOrderPlaced ='${product.minOrderPlaced}';
			  	product.packQuantity ='${product.packQuantity}';
				product.productName ='${product.name}';
				product.model ='${product.model}';
				product.brand ='${product.brand}';
			  	productArgs['${product.id}']=product;
			</c:forEach>
			
			
			$(".add-row").on("click",function(e){
				e.preventDefault();
				$(document).scrollTop($(document).height());
				var tbody=$("#contentTable tbody");
				var tr=$("<tr></tr>");
				var options = "<option value=''></option>";
				
				$.each(proCountryMap,function(n,value){
					options+="<option value='"+n+"'>"+n+"</option>";
				});
				tr.append("<td></td>");
				tr.append("<td><span class='' style='color:red;display:none'} ><b>超标</b></span><input type='hidden' class='proName' /><input type='hidden' name='supplier.id'/><input type='hidden' class='productIdTemp'/><input type='hidden' name='productName' class='productName' /><select name='product.id' class='productId' style='width:95%'>"+options+"</select></td>");
				tr.append("<td><span class='orderDate' /></td>");
				tr.append("<td><span style='text-align: center' class='moq'/></td>");
				tr.append("<td/>");
				tr.append("<td class='total'></td>");
				tr.append("<td><select name='countryCode' class='countryCode'  style='width:95%'/></td>");
				tr.append("<td><span class='period'/></td>");
				tr.append("<td><span class='day31sales'/></td>");
		        tr.append("<td></td>");
		        tr.append("<td></td>");
				tr.append("<td><span class='packQuantity'/></td>");
	            tr.append("<td></td>");
	            tr.append("<td></td>");
	            tr.append("<td></td>");
	            tr.append("<td></td>");
	            tr.append("<td></td>");
	            tr.append("<td></td>");
	            tr.append("<td><input type='text' maxlength='11'  style='width: 85%'   name='quantity'  class='number' /></td>");
	            tr.append("<td></td>");
	            tr.append("<td></td>");
	            tr.append("<td><input type='text' maxlength='500' style='width: 85%'   name='remark' /></td>");
	            tr.append("<td><input type='hidden' class='itemId' /> <a class='save-row'>保存</a>&nbsp;&nbsp;</br><a href='#' class='remove-row'>删除</a></td>");
				
				tr.find("select.productId").select2();
				tr.find("select.countryCode").select2();
				tr.find("select.colorCode").select2();
				
				tbody.append(tr);
			});
			
			
			$("#inputForm").on("change",".countryCode",function(){
				  	var  tr = $(this).parent().parent();
				  	var proNoBank =tr.find(".productId").children('option:selected').val();
				  	var proColorCountry =proNoBank+","+$(this).val();
					var productId = itemMap[proColorCountry].split(',')[1];
					var itemId = itemMap[proColorCountry].split(',')[0];
					var po =ajaxItemData(itemId);
					var supId=supMap[productId].split(',')[0];
					var supName=supMap[productId].split(',')[1];
					//获取选中的text
					tr.find(".total").html("<span class='"+proNoBank+"Total'/>");
					tr.find(".productIdTemp").val(proNoBank);
					tr.find("span.moq").text(productArgs[productId].minOrderPlaced);
					
					//获得装箱数：
					var packQuantity=1;
					var country = $(this).val();               
				  	if("217"==productId){
				  		if("com,uk,jp,ca,mx".indexOf(country)<0){
				  			packQuantity=60;
				  		}else{
				  			packQuantity=44;
				  		}
				  	}else if("218"==productId){
				  		if("com,jp,ca,mx".indexOf(country)<0){
				  			packQuantity=32;
				  		}else{
				  			packQuantity=24;
				  		}
				  	}else{
				  		packQuantity=productArgs[productId].packQuantity;
				  	}
				  	
					tr.find("span.packQuantity").text(packQuantity);
					tr.find(".productName").val(productArgs[productId].productName);
					tr.find(".supplierName").text(supName);
					tr.find(".itemId").val(itemId);
					tr.find(".period").text(po.period=="null"?"":po.period);
					tr.find(".day31sales").text(po.day31sales);
					var brand = product.brand+" "+proNoBank;
					tr.find(".orderDate").text(orderDateJson[brand]);
					tr.find("input[name='supplier.id']").val(supId);
			});
			
			
			$("input[name='quantity']").live("blur",function(){
				var tr=$(this).parent().parent();
				var proNoBank = tr.find(".productIdTemp").val();
				//计算现在产品系列的数值
				var total=0;
				tr.parent().find(".productIdTemp").each(function(){
					if($(this).val()==proNoBank){
						var ttr=$(this).parent().parent();
						var qq=ttr.find("input[name='quantity']").val();
						if(!qq){
							qq=ttr.find(".enterQuantity").text();
						}else{
							qq=0;
						}
						
						if(qq){
							total=total+parseInt(qq);
						}
					}
					
				});
				tr.parent().find("."+proNoBank+"Total").text(total);
			});
			
			
				
			$("#inputForm").on("change",".productId",function(){
				var  tr = $(this).parent().parent();
				tr.find("select[name='countryCode']").select2("data",[]);
				tr.find("select[name='countryCode']").select2().empty();
				var proColor  = $(this).val();
				$(this).parent().find("span:first").attr("class",proColor);
				tr.find(".proName").val(proColor);
				countryArgs=proCountryMap[proColor];
				$(countryArgs).each(function(i,data){
					 tr.find("select[name='countryCode']").select2().append("<option value='"+data+"' >"+countryMap[data]+"</option>");
				});
				tr.find("select[name='countryCode']").select2().change();
			});
			
			$("#isCheck,#isMain,#isNew,#isPriceChange,#lineId,#nameColor,#country").on("change",function(){
				var params = {};
				params.id = $("#forecastOrderId").val();
				params.country = $("#country").children("option:selected").val();
				params.lineId = $(this).val();
				params.isCheck = $("#isCheck")[0].checked?"1":"0";
				params.nameColor = $("#nameColor").children("option:selected").val();
				params.isMain = $("#isMain").children("option:selected").val();
				params.isNew = $("#isNew")[0].checked?"1":"0";
				params.isPriceChange = $("#isPriceChange")[0].checked?"1":"0";
				window.location.href="${ctx}/psi/forecastOrder/edit?"+$.param(params);
			});
			
			
			$("#contentTable").on('click', '.remove-row', function(e){
				  e.preventDefault();
					//取消原来输入的数据
					var tr = $(this).parent().parent();
					var table = tr.parent();
					var proName ="."+tr.find(".proName").val();
					var itemId = tr.find(".itemId").val();
					if(itemId){
						var param = {};
						param.itemId = itemId;
						$.get("${ctx}/psi/forecastOrder/updateRemarkDel?"+$.param(param),function(data){
							if(data!=''){    
								top.$.jBox.tip("超标："+data, 'info',{timeout:4000});
								table.find(proName).css("display","block");
							}else{
								table.find(proName).css("display","none");
								top.$.jBox.tip("删除成功！", 'info',{timeout:2000});
							}
						});
						 tr.remove();
					}
			});
			
			
			$(".batchBtn").click(function(){
				var ids="";
				$("#contentTable tbody tr").find(".checkBox").each(function(){
					var tr =$(this).parent().parent();
					if($(this).attr("checked")=="checked"){
						ids+=tr.find("input[name='id']").val()+",";
					};
				});
				var forecastOrderId = $("#forecastOrderId").val();
				if(!ids){
					top.$.jBox.tip("请选中要批量修改的产品", 'info',{timeout:3000});
					return false;
				}
				var html = "1、常规数量:<input id='batchQuantity'  style='height: 25px;width:180px' type='text' class='number' /><br/>2、销售备注:<input id='batchRemark' type='text' style='height: 50px;width:180px' />";
				top.$.jBox.confirm(html,'您确定要批量修改常规数量、备注?',function(v,h,f){
					if(v=='ok'){
						var param = {};
						param.forecastOrderId=forecastOrderId;
						param.itemIds = ids;
						param.batchQuantity = $.trim(h.find("#batchQuantity").val());
						param.batchRemark = encodeURI($.trim(h.find("#batchRemark").val()));
						if(param.batchQuantity==0){
							top.$.jBox.tip("请填写数量！", 'info',{timeout:2000});
							return false;
						}
						if(param.batchRemark==''){
							top.$.jBox.tip("请填写备注！", 'info',{timeout:2000});
							return false;
						}
						$.get("${ctx}/psi/forecastOrder/batchUpdate?"+$.param(param),function(data){
							if(data){
								top.$.jBox.tip("批量修改成功", 'info',{timeout:3000});
								goRefresh();
							}else{
								top.$.jBox.tip("批量修改失败", 'info',{timeout:3000});
							}
						});    
					}
				},{buttonsFocus:1,width:350,showClose: false,persistent: true});
				top.$('.jbox-body .jbox-icon').css('top','55px');
				return false;
			});
			
		
			$(".Wdate").live("click", function (){
			 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			
			
			function ajaxItemData(itemId){
				var po={};
				$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/psi/forecastOrder/ajaxItemData' ,
				    data: {
				    	"tranId":itemId
				    },
				    dataType: 'json',
				    success:function(data){ 
				    	po= data;
			        }
				});
				return  po;
			};
			
			
			
		});
		
		function goBack(){
			window.location.href="${ctx}/psi/forecastOrder";
		}
		
		function goRefresh(){
			var id = $("#forecastOrderId").val();
			var nameColor = $("#nameColor").children("option:selected").val();
			var country = $("#country").children("option:selected").val();
			var isCheck = $("#isCheck")[0].checked?"1":"0";
			window.location.href="${ctx}/psi/forecastOrder/edit?id="+id+"&country="+country+"&nameColor="+nameColor+"&isCheck="+isCheck;
		}
	</script>
</head>
<body style="overflow-y:hidden">
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/forecastOrder/">预测下单列表</a></li>
		<li class="active"><a href="#">预测下单编辑</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="forecastOrder" action="" method="post" class="form-horizontal">
		<input type="hidden" name="id" id="forecastOrderId"		value="${forecastOrder.id}" />
	    <input type="hidden" name="orderSta" 			value="${forecastOrder.orderSta}" />
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:98%;height:30px" >
				<c:if test="${not empty forecastOrder.targetDate}">
					<b>备货日期:</b><fmt:formatDate value="${forecastOrder.targetDate}" pattern="yyyy-MM-dd" />
				</c:if>
				&nbsp;&nbsp;
				产品线:<select name="lineId" id="lineId" style="width:80px">
				<option value="">--All--</option>
				<c:forEach items="${lineList}" var="lineList">
					<option value="${lineList.id}" ${lineList.id eq lineId?'selected':''}>${lineList.name}</option>			
				</c:forEach>
			   </select>
				&nbsp;&nbsp;
				<b>产品:</b>
					<select id="nameColor" style="width:150px">
						<option value="" >全部</option>
						<c:forEach items="${productAttr}" var="productEntry">
							<option value="${productEntry.key}" ${nameColor eq productEntry.key ?'selected':''}  >${productEntry.key}</option>
						</c:forEach>
					</select>
				&nbsp;&nbsp;
				<b>国家:</b>
				<select id="country" style="width:80px">
					<option value="" >全部</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							 <option value="${dic.value}" ${country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:if>      
					</c:forEach>
				</select>
				&nbsp;&nbsp;
				<b>定位:</b>
				<select id="isMain" style="width:80px">
					<option value="" >全部</option>
					<c:forEach items="${fns:getDictList('product_position')}" var="dic">
						<c:if test="${dic.value ne '4'}">
							 <option value="${dic.value}" ${isMain eq dic.value ?'selected':''}>${dic.label}</option>
						</c:if>      
					</c:forEach>
				</select>
				&nbsp;&nbsp;
				<input type="checkbox"  id="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/>
				<label>非0</label>
				
				<input type="checkbox"  id="isNew" value="${isNew}" ${isNew eq '1' ?'checked':'' }/>
				<label>新品</label>
				
				<input type="checkbox"  id="isPriceChange" value="${isPriceChange}" ${isPriceChange eq '1' ?'checked':'' }/>
				<label><a class="tips" rel="popover" data-content="最近一个月价格浮动超过20%" href="#">调价</a></label>
				&nbsp;&nbsp;&nbsp;<input class="btn batchBtn" type="button" value="批量修改"/>
				&nbsp;&nbsp;&nbsp;<a class="btn btn-small"  href="${ctx}/psi/forecastOrder/exportSingle?forecastOrderId=${forecastOrder.id}">导出</a>
				&nbsp;&nbsp;&nbsp;<input id="btnRefresh" class="btn" type="button" value="刷新" onclick="goRefresh()"/>
				&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="goBack()"/>
					
			</div>
			
		</div>
		<div style="float:left;width:100%">
		 <blockquote style="float:left;">
			 <div style="margin-bottom:20px"><p style="font-size: 14px;height:35px">产品信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		<div align="right" style="font-size: 14px;margin: 0px 100px 0px 0px;">
			<c:if test="${forecastOrder.orderSta ne '3'&&forecastOrder.orderSta ne '5'&&forecastOrder.orderSta ne '8' && canEdit}"><a href="#" class="add-row">
				<span class="icon-plus"></span>增加产品</a>
			</c:if>
			</div>
		</div>
		
		<div style="height:30px;position:fixed;z-index:1;left:20px;right:20px;top:115px" id="header">
		<table id="contentTable11" class="table table-bordered table-condensed" style="">
          	<thead >
				<tr >
				 <th style="width: 3%">序号</th>
				   <th style="width: 12%">产品</th>
				   <th style="width: 5%">最近<br/>下单周</th>
				   <th style="width: 4%">MOQ</th>
				   <th style="width: 4%">库销比</th>
				   <th style="width: 4%">总下<br/>单量</th>
				   <th style="width: 6%">国家</th>
				   <th style="width: 4%">周期</th>
				   <th style="width: 4%">31<br/>天销</th>
				   <th style="width: 4%">总库存</th>
				   <th style="width: 3%">依据</th>
				   <th style="width: 4%">装箱<br/>数</th>
				   <th style="width: 4%">下单<br/>周</th>
				   <c:if test="${empty forecastOrder.targetDate }">
					   <th style="width: 4%">(${fns:getDateByPattern(forecastOrder.createDate,"w")})周</th>
					   <th style="width: 4%">(${fns:getDateByPattern(fns:addDays(forecastOrder.createDate,7),"w")})周</th>
					   <th style="width: 4%">(${fns:getDateByPattern(fns:addDays(forecastOrder.createDate,14),"w")})周</th>
					   <th style="width: 4%">(${fns:getDateByPattern(fns:addDays(forecastOrder.createDate,21),"w")})周</th>
				   </c:if>
				   <c:if test="${not empty forecastOrder.targetDate }">
					   <th style="width: 4%">(${fns:getDateByPattern(fns:addDays(forecastOrder.targetDate,-21),"w")})周</th>
					   <th style="width: 4%">(${fns:getDateByPattern(fns:addDays(forecastOrder.targetDate,-14),"w")})周</th>
					   <th style="width: 4%">(${fns:getDateByPattern(fns:addDays(forecastOrder.targetDate,-7),"w")})周</th>
					   <th style="width: 4%">(${fns:getDateByPattern(forecastOrder.targetDate,"w")})周</th>
				   </c:if>
				   <th style="width: 4%">系统<br/>下单量</th>
				   <th style="width: 4%">常规<br/>数量</th>
				   <th style="width: 4%">促销<br/>数量</th>
				   <th style="width: 4%">广告<br/>数量</th>
				   <th style="width: 8%;" >备注</th>
				   <th style="width: 18%">提示</th>
				</tr>
		</thead>
		</table>
		</div>
		<table id="contentTable" class="table table-bordered table-condensed">
		<colgroup>
               <col style="width: 3%"/><col style="width: 12%"/><col style="width: 5%"/><col style="width: 4%"/><col style="width: 4%"/><col style="width: 4%"/><col style="width: 6%"/><col style="width: 4%"/><col style="width: 4%"/>
               <col style="width: 3%"/><col style="width: 4%"/><col style="width: 4%"/><col style="width: 4%"/><col style="width: 4%"/><col style="width: 4%"/><col style="width: 4%"/><col style="width: 4%"/><col style="width: 4%"/>
       		   <col style="width: 4%"/><col style="width: 4%"/><col style="width: 4%"/><col style="width: 8%"/><col style="width: 18%"/>
         	 </colgroup>
		<tbody>
		<c:if test="${not empty forecastOrder.items}" >
			<c:forEach items="${forecastOrder.productMap}" var="productMap" varStatus="j">
			    <c:forEach items="${productMap.value}" var="item" varStatus="i">
			    <c:choose>
			    	<c:when test="${i.index==0}">
			    		<tr style="${(j.index+1)%2==0?'':'background-color: #f9f9f9;'}">
			    		<td style="vertical-align: middle;text-align: center"  rowspan="${fn:length(productMap.value)}">${j.index+1}</td>
						<td style="vertical-align: middle;text-align: center"  rowspan="${fn:length(productMap.value)}">
							<input type="hidden" name="id" value="${item.id}" />
							<input type="hidden" class="productIdTemp" value="${item.productNoBank}" />
							<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productNameColor}">${item.productNoBank}</a>
							<c:if test="${productTranTypeAndBP[item.productNameColor]['total'].transportType eq '2'}">
								<span class="icon-plane"></span>
							</c:if>
							<c:if test="${not empty alertPriceMap[item.productIdColor]}">
								<a  class="tips" rel="popover" data-content="${alertPriceMap[item.productIdColor]}" data-placement="right" href="#"><span style="background: url(/springrain-erp/static/jquery-ztree/3.5.12/css/zTreeStyle/img/diy/3.png) no-repeat scroll 0 0 transparent">&nbsp;&nbsp;&nbsp;&nbsp;</span></a>
							</c:if>
							<span class='${item.productNoBank}' style="color:red;display:${not empty item.maxStock?'block':'none'}" ><b>超标</b></span>
						</td>
						<td style="vertical-align: middle;text-align: center" rowspan="${fn:length(productMap.value)}" >${orderDateMap[item.productNameColor]}</td>
						<td style="vertical-align: middle;text-align: center" rowspan="${fn:length(productMap.value)}" > ${item.product.minOrderPlaced}</td>
						<td style="vertical-align: middle;text-align: center" rowspan="${fn:length(productMap.value)}" > <fmt:formatNumber value="${saleMonth[item.productNameColor]}" pattern="0.#"/></td>
						<td style="vertical-align: middle;text-align: center" rowspan="${fn:length(productMap.value)}" ><span class="${item.productNoBank}Total"> ${forecastOrder.productTotalMap[item.productNameColor]}</span></td>
						<td>
							<c:choose>
								<c:when test="${'1' eq fanOuFlag[item.productNameColor] && item.countryCode eq 'de'}">
									<a class="tips" rel="popover" data-content="欧洲四国(de,fr,it,es)泛欧" href="#">eu4</a>
								</c:when>
								<c:when test="${'0' eq fanOuFlag[item.productNameColor] && item.countryCode eq 'de'}">eu</c:when>
								<c:otherwise>${item.countryCode eq 'com'?'us':item.countryCode}</c:otherwise>
							</c:choose>
							<input type="checkBox" class="checkBox"/>
							<span>${fns:getDictLabel(item.isMain,'product_position','')}</span>
						</td>
						<td>${item.period}</td>
						<td>
							<c:choose>
								<c:when test="${item.day31sales>0 && item.realDay31sales>0 && (item.day31sales-item.realDay31sales)/item.day31sales>0.2}">
									<a style="color:red" class="tips" rel="popover" data-content="去除营销数后滚动31日销：${item.realDay31sales }<br/>滚动31日营销数为：${item.day31sales-item.realDay31sales },占比：${item.day31SalesPercent}" href="#">${item.day31sales}</a>
								</c:when>
								<c:when test="${empty item.realDay31sales}">
									${item.day31sales}
								</c:when>
								<c:otherwise>
									<a class="tips" rel="popover" data-content="去除营销数后滚动31日销：${item.realDay31sales }" href="#">${item.day31sales}</a>
								</c:otherwise>
							</c:choose>
						</td>
						<td>${item.totalStock}</td>
						<td>	 
							<c:choose >
								<c:when test="${item.by31sales eq '0' }">预销</c:when>
								<c:when test="${item.by31sales eq '1' }">31销</c:when>
								<c:when test="${item.by31sales eq '2' }"><span style="color: ${item.byWeek eq '3'?'#00CED1':'#32CD32'}">预销</span></c:when>
								<c:when test="${item.by31sales eq '3' }"><span style="color: ${item.byWeek eq '3'?'#00CED1':'#32CD32'}">31销</span></c:when>
								<c:otherwise>手动增加</c:otherwise>
							</c:choose>
						</td>
						<td>	<span class="packQuantity">${item.packQuantity}</span></td>
						<td>
							<c:if test="${item.by31sales eq '2' || item.by31sales eq '3'}">
								<c:choose>
								 <c:when test="${item.byWeek eq '3'}">WK${fns:getDateByPattern(forecastOrder.createDate,"w")}</c:when>
								<c:otherwise>
									 <c:if test="${item.byWeek ne 9}" >
									 	 WK${fns:getDateByPattern(fns:addDays(forecastOrder.createDate,7*(item.byWeek+1)),"w")}
									 </c:if>  
								 </c:otherwise>  
								</c:choose>
							</c:if>
						</td>
						<td><span style="${item.byWeek eq '0'?'color:red':'' }">  ${item.forecast1week!=0?(-item.forecast1week):0}</span></td>
						<td><span style="${item.byWeek eq '1'?'color:red':'' }">  ${item.forecast2week!=0?(-item.forecast2week):0}</span></td>
						<td><span style="${item.byWeek eq '2'?'color:red':'' }">  ${item.forecast3week!=0?(-item.forecast3week):0}</span></td>
						<td><span style="${item.byWeek eq '3'?'color:red':'' }">  ${item.forecast4week!=0?(-item.forecast4week):0}</span></td>
						<td>
							<span style="color:#00BFFF">
								<c:choose>
									<c:when test="${item.byWeek eq '0'}">${item.forecast1week>0?item.forecast1week:0}</c:when>
									<c:when test="${item.byWeek eq '1'}">${item.forecast2week>0?item.forecast2week:0}</c:when>
									<c:when test="${item.byWeek eq '2'}">${item.forecast3week>0?item.forecast3week:0}</c:when>
									<c:when test="${item.byWeek eq '3'}">${item.forecast4week>0?item.forecast4week:0}</c:when>
								</c:choose>
							</span>
						</td>
						<td >
						<input type='hidden' class="proName" value="${item.productNoBank}">
							<c:choose>
								<c:when test="${forecastOrder.orderSta ne '3'&&forecastOrder.orderSta ne '5'&&forecastOrder.orderSta ne '8' && canEdit}">
									<input type="hidden" class="itemId" value="${item.id}" /> <a href="#" class="enterQuantity"  data-type="number" data-pk="1" data-title="Enter Quantity" data-value="${item.quantity}">${item.quantity}</a>
								</c:when>
								<c:otherwise>${item.quantity}</c:otherwise>
							</c:choose>
						</td>
						<td><span class="proQuantity">${item.promotionQuantity}</span></td>
						<td><span class="adQuantity">${item.promotionBossQuantity}</span></td>
						<td  ><input type="hidden" class="itemId" value="${item.id}" /> <a href="#" class="enterRemark"  data-type="text" data-pk="1" data-title="Enter Remark" data-value="${item.remark}">${item.remark}</a></td>
						<td >
							<c:set value="${item.product.id},${item.colorCode},${item.countryCode}" var="proKey"/>
							<c:if test="${not empty item.tips || not empty promotionTips[proKey]}">${item.tips}</c:if>
							<c:if test="${not empty item.priceChange}"><br/>${item.priceChange}</c:if>
						</td>
					</tr>
				</c:when>
		    	<c:otherwise>
		    		<tr style="${(j.index+1)%2==0?'':'background-color: #f9f9f9;'}">
		    			<td>	
							<input type="hidden" name="id" value="${item.id}" />
							<input type="hidden" name="product.id" value="${item.product.id}" />
						 <c:choose>
								<c:when test="${'1' eq fanOuFlag[item.productNameColor] && item.countryCode eq 'de'}">
									<a class="tips" rel="popover" data-content="欧洲四国(de,fr,it,es)泛欧" href="#">eu4</a>
								</c:when>
								<c:when test="${'0' eq fanOuFlag[item.productNameColor] && item.countryCode eq 'de'}">eu</c:when>
								<c:otherwise>${item.countryCode eq 'com'?'us':item.countryCode}</c:otherwise>
							</c:choose>
							<input type="checkBox" class="checkBox"/>
						 </td>
						 <td>${item.period}</td>
						<td>
							<c:choose>
								<c:when test="${item.day31sales>0 && item.realDay31sales>0 && (item.day31sales-item.realDay31sales)/item.day31sales>0.2}">
									<a style="color:red" class="tips" rel="popover" data-content="去除营销数后滚动31日销：${item.realDay31sales }<br/>滚动31日营销数为：${item.day31sales-item.realDay31sales },占比：${item.day31SalesPercent}" href="#">${item.day31sales}</a>
								</c:when>
								<c:when test="${empty item.realDay31sales}">
									${item.day31sales}
								</c:when>
								<c:otherwise>
									<a class="tips" rel="popover" data-content="去除营销数后滚动31日销：${item.realDay31sales }" href="#">${item.day31sales}</a>
								</c:otherwise>
							</c:choose>
						</td>
						<td>${item.totalStock}</td>
						<td>	 
							<c:choose >
								<c:when test="${item.by31sales eq '0' }">预销</c:when>
								<c:when test="${item.by31sales eq '1' }">31销</c:when>
								<c:when test="${item.by31sales eq '2' }"><span style="color:${item.byWeek eq '3'?'#00CED1':'#32CD32'}"> 预销</span></c:when>
								<c:when test="${item.by31sales eq '3' }"><span style="color: ${item.byWeek eq '3'?'#00CED1':'#32CD32'}">31销</span></c:when>
								<c:otherwise>手动增加</c:otherwise>
							</c:choose>
						</td>
						<td><span class="packQuantity">${item.packQuantity}</span></td>
						<td>
							<c:if test="${item.by31sales eq '2' || item.by31sales eq '3'}">
								<c:choose>
								 <c:when test="${item.byWeek eq '3'}">WK${fns:getDateByPattern(forecastOrder.createDate,"w")}</c:when>
								 <c:otherwise>
									 <c:if test="${item.byWeek ne 9}" >
									 	 WK${fns:getDateByPattern(fns:addDays(forecastOrder.createDate,7*(item.byWeek+1)),"w")}
									 </c:if>  
								 </c:otherwise>  
								</c:choose>
							</c:if>
						</td>
						<td><span style="${item.byWeek eq '0'?'color:red':'' }">  ${item.forecast1week!=0?(-item.forecast1week):0}</span></td>
						<td><span style="${item.byWeek eq '1'?'color:red':'' }">  ${item.forecast2week!=0?(-item.forecast2week):0}</span></td>
						<td><span style="${item.byWeek eq '2'?'color:red':'' }">  ${item.forecast3week!=0?(-item.forecast3week):0}</span></td>
						<td><span style="${item.byWeek eq '3'?'color:red':'' }">  ${item.forecast4week!=0?(-item.forecast4week):0}</span></td>
						<td>
							<span style="color:#00BFFF">
								<c:choose>
									<c:when test="${item.byWeek eq '0'}">${item.forecast1week>0?item.forecast1week:0}</c:when>
									<c:when test="${item.byWeek eq '1'}">${item.forecast2week>0?item.forecast2week:0}</c:when>
									<c:when test="${item.byWeek eq '2'}">${item.forecast3week>0?item.forecast3week:0}</c:when>
									<c:when test="${item.byWeek eq '3'}">${item.forecast4week>0?item.forecast4week:0}</c:when>
								</c:choose>
							</span>
						</td>
						<td >
						<input type='hidden' class="proName" value="${item.productNoBank}">
							<c:choose>
								<c:when test="${forecastOrder.orderSta ne '3'&&forecastOrder.orderSta ne '5'&&forecastOrder.orderSta ne '8' && canEdit}">
									<input type="hidden" class="itemId" value="${item.id}" /> <a href="#" class="enterQuantity"  data-type="number" data-pk="1" data-title="Enter Quantity" data-value="${item.quantity}">${item.quantity}</a>
								</c:when>
								<c:otherwise>${item.quantity}</c:otherwise>
							</c:choose>
						</td>
						<td><span class="proQuantity">${item.promotionQuantity}</span></td>
						<td><span class="adQuantity">${item.promotionBossQuantity}</span></td>
						<td >
						<input type="hidden" class="itemId" value="${item.id}" /> <a href="#" class="enterRemark"  data-type="text" data-pk="1" data-title="Enter Remark" data-value="${item.remark}">${item.remark}</a></td>
						<td >
							<c:set value="${item.product.id},${item.colorCode},${item.countryCode}" var="proKey"/>
							<c:if test="${not empty item.tips || not empty promotionTips[proKey]}">${item.tips}</c:if>
							<c:if test="${not empty item.priceChange}"><br/>${item.priceChange}</c:if>
						</td>
		    		</tr>
			    	</c:otherwise>
			    </c:choose>
			    </c:forEach>
			</c:forEach>
		</c:if>
		</tbody>
	</table>
		
		<div class="form-actions" style="height:60px">
		<input id="btnRefresh" class="btn" type="button" value="刷新" onclick="goRefresh()"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="goBack()"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<c:if test="${canEdit }">
			<input  class="btn add-row" type="button" value="增加产品" />
		</c:if>
		</div>
	</form:form>
</body>
</html>
