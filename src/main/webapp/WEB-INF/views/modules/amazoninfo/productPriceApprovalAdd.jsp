<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品价格审批管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" type="text/css" />
	<script type="text/javascript" src="${ctxStatic}/x-editable/js/bootstrap-editable.js"></script>
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
		var iFlag = 0;
		$(document).ready(function() {
			$('#add-row').on('click', function(e){
				iFlag +=1;
			   e.preventDefault();
			   var tableBody = $('.table > tbody'), 
			   lastRowClone = $('tr:last-child', tableBody).clone();
			   $('input[type=text][class!=Wdate]', lastRowClone).val('');  
			 var html = "<option value=''></option>";
			 <c:forEach items="${sku}" var="item">
			 		html = html+"<option value='${item.key}'>${item.value}</option>";
			 </c:forEach>
			 lastRowClone.find(".sku").html("<select class=\"required\" name=\"sku\" style=\"width: 90%\">"+html+"</select><input id=\"productName\" name=\"productName\" type=\"hidden\"/>");
			   
			 	lastRowClone.find("select").select2();
				lastRowClone.find(".salePrice").attr("id","price" + iFlag);
				lastRowClone.find(".saleStartDate").attr("id","saleStartDate" + iFlag);
				lastRowClone.find(".saleEndDate").attr("id","saleEndDate" + iFlag);
				lastRowClone.find(".warnQty").attr("id","warnQty" + iFlag);
				lastRowClone.find(".changeQty").attr("id","changeQty" + iFlag);
				lastRowClone.find(".changePrice").attr("id","changePrice" + iFlag);
			   tableBody.append(lastRowClone);
			});
			$('#contentTable').on('click', '.remove-row', function(e){
			  e.preventDefault();
			  if($('#contentTable tr').size()>2){
				  var row = $(this).parent().parent();
				  row.remove();
			  }
			});
			
			$("#accountName").change(function(){
				var params = {};
				params.accountName = $(this).val();
				params.reason = $("select[name='reason']").val();
				window.location.href = "${ctx}/amazoninfo/productPriceApproval/addForm?"+$.param(params);
			});
			
			$("select[name='sku']").live("change",function(){
				var skuName = $(this).find("option:selected").text();	//当前选中的值
				var productName = skuName.split("[")[0];
				//根据产品名称去拿到skujihe
				var skus = [];
				$.ajax({
	      			   type: "POST",
	      			   url: "${ctx}/amazoninfo/productPriceApproval/getSkus?accountName="+$("#accountName").val()+"&productName="+productName,
	      			   async: false,
	      			   success: function(msg){
	      				 skus = msg;		
	      			   }
	          	});
			 	for(var i=0; i<skus.length; i++){
				 	var newSku = skus[i].key;
					iFlag +=1;
				    var tableBody = $('.table > tbody'), 
				    lastRowClone = $('tr:last-child', tableBody).clone();
				    $('input[type=text][class!=Wdate]', lastRowClone).val('');  
				 	var html = "<option value=''></option>";
				 	<c:forEach items="${sku}" var="item">
			 			<%--html = html+"<option value='${item.key}' ${item.key eq '"+newSku+"' ?'selected':''}>${item.value}</option>";--%>
				 		var sku ='${item.key}';
				 		if(newSku==sku){
				 			html = html+"<option value='${item.key}' selected>${item.value}</option>";
				 		} else {
				 			html = html+"<option value='${item.key}'>${item.value}</option>";
				 		}
				 	</c:forEach>
					lastRowClone.find(".sku").html("<select class=\"required\" name=\"sku\" style=\"width: 90%\">"+html+"</select><input id=\"productName\" name=\"productName\" type=\"hidden\"/>");
					lastRowClone.find(".price").val("");
					lastRowClone.find("input[name='productName']").val(productName);
					lastRowClone.find("select").select2();
					lastRowClone.find(".salePrice").attr("id","price" + iFlag);
					lastRowClone.find(".saleStartDate").attr("id","saleStartDate" + iFlag);
					lastRowClone.find(".saleEndDate").attr("id","saleEndDate" + iFlag);
					lastRowClone.find(".warnQty").attr("id","warnQty" + iFlag);
					lastRowClone.find(".changeQty").attr("id","changeQty" + iFlag);
					lastRowClone.find(".changePrice").attr("id","changePrice" + iFlag);
					tableBody.append(lastRowClone);
                }
			 	if($('#contentTable tr').size()>2){
				  var row = $(this).parent().parent();
				  row.remove();
				}
			});
			
			$("#inputForm").on("change","select[name='sku']",function(){
				if($(this).val()=='sku'){
					$(this).parent().find("input[type='text']").show();
				}else{
					$(this).parent().find("input[type='text']").hide();
					var arr = $(this).find("option:selected").text().split("[");
					$(this).parent().find("input[name='productName']").val(arr[0]);
					//加入参考价格，以及限制价格
					var rs = "";
					var salePrice = $(this).parent().parent().find("input[name='price']");
					salePrice.val('');
				}
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					var flag = true;
					var message = "价格必须为数字";
					$(".price").each(function(){
						if($(this).val()!=''){
							if(!$.isNumeric($(this).val())){
								flag = false;
							}
						} 
					});
					$(".changePrice").each(function(){
						var changePrice = $(this).val();
						var changeQty = $(this).parent().parent().find("input[name='changeQty']").val();
						if((changeQty != '' && changePrice =='') || (changeQty == '' && changePrice !='')){
							message = "改价监控销量和自动改价价格不能只填一个！！";
							flag = false;
						}
					});
					if(flag){
						top.$.jBox.confirm('确定要提交申请？','系统提示',function(v,h,f){
							if(v=='ok'){
								loading('正在提交，请稍等...');
								$("#contentTable tbody tr").each(function(i,j){
									if($(j).find("select").select2("val")!='sku'){
										$(j).find("select").attr("name","prices"+"["+i+"]."+$(j).find("select").attr("name"));
									}
									$(j).find("input[type!='']").each(function(){
											if($(this).attr("name")&&$(this).css("display")!='none'){
												$(this).attr("name","prices"+"["+i+"]."+$(this).attr("name"));
											}
									});
								});
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
							}
						},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}else{
						top.$.jBox.error(message,"错误");
					}
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/priceFeed/">产品价格管理列表</a></li>
		<li><a href="${ctx}/amazoninfo/priceFeed/form">修改产品价格</a></li>
		<li><a href="${ctx}/amazoninfo/productPriceApproval/">价格审批列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/productPriceApproval/form">申请价格审批</a></li>
	</ul><br/>
	<form id="inputForm"  action="${ctx}/amazoninfo/productPriceApproval/batchSave" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label" style="width: 80px">平台:</label>
			<div class="controls" style="margin-left: 90px;">
				<select id="accountName" name="accountName" style="width: 120px" class="required">
					<option value="" selected="selected">-请选择平台-</option>
					 <c:forEach items="${accountMap}" var="account">
				        <option value='${account.key}'>${account.key}</option>
				    </c:forEach>
				</select>
				<script type="text/javascript">
					$("option[value='${productPriceApprovalTemp.accountName}']").attr("selected","selected");				
				</script>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" style="width: 80px">定价原由:</label>
			<div class="controls" style="margin-left: 90px;">
				<select  name="reason" style="width: 220px" class="required">
					<option></option>
					<option value="防御性降价" ${productPriceApprovalTemp.reason eq '防御性降价' ?'selected':''}>防御性降价</option>
					<option value="积压降价" ${productPriceApprovalTemp.reason eq '积压降价' ?'selected':''}>积压降价</option>
					<option value="断货升价" ${productPriceApprovalTemp.reason eq '断货升价' ?'selected':''}>断货升价</option>
					<option value="促销调价" ${productPriceApprovalTemp.reason eq '促销调价' ?'selected':''}>促销调价</option>
					<option value="计划调价" ${productPriceApprovalTemp.reason eq '计划调价' ?'selected':''} >计划调价</option>
					<option value="汇率改价" ${productPriceApprovalTemp.reason eq '汇率改价' ?'selected':''} >汇率改价</option>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" style="width: 80px"></label>
			<div class="controls" style="margin-left: 90px;">
				<span style="color:red">Tips:预警销量、改价监控销量、自动改价价格为可选项,在需要监控时填写。</span>
			</div>
		</div>	
		<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" id="add-row"><span class="icon-plus"></span>新增产品</a></div>
		<div class="control-group">
			<label class="control-label" style="width: 80px">产品价格:</label>
			<div class="controls" style="margin-left: 90px;">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="width: 200px">产品</th>
							<th style="width: 70px">价格</th>
							<th style="width: 110px">开始时间</th>
							<th style="width: 110px">结束时间</th>
							<th style="width: 50px">预警销量</th>
							<th style="width: 200px">改价监控</th>
							<th style="width: 10px">操作</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td class="sku" style="width: 200px">
							<select style="width: 90%" name="sku" class="required">
								<option value=""></option>
								<c:forEach items="${sku}" var="item">
									<option value="${item.key}">${item.value}</option>
								</c:forEach>
							</select>
							<input type="hidden" id="productName" name="productName"/>
							</td>
							<td><input type="text" style="width: 80%" id="price0" name="price" class="price required salePrice"/><span class="tip"></span></td>
							<td><input style="width:80%" id="saleStartDate0" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,minDate:'%y-%M-%d'});" readonly="readonly" class="Wdate required saleStartDate" type="text" name="saleStartDate" class="input-small"/></td>
							<td><input style="width:80%" id="saleEndDate0" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,minDate:'%y-%M-%d'});" readonly="readonly"  class="Wdate required saleEndDate" type="text" name="saleEndDate" class="input-small"/></td>
							<td><input type="text" style="width: 80%" id="warnQty0" name="warnQty" class="number warnQty"/></td>
							<td>
							销量达到<input type="text" style="width: 20%" id="changeQty0" name="changeQty" class="number changeQty"/>
							改价为<input type="text" style="width: 20%" id="changePrice0" name="changePrice" class="price changePrice"/></td>
							<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>	
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提  交"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>