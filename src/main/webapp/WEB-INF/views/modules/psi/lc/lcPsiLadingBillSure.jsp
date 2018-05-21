<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单确认</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			eval('var ladingMap=${ladingMap}');
			eval('var skuMap=${skuMap}');
			eval('var fnskuMap=${fnskuMap}');
			eval('var newProducts=${newProducts}');
			eval('var canLadingMap=${canLadingMap}');
			// 生成要编辑表格 
			var createTable = $('#contentTable');
			for(var key in ladingMap){
				var ladingArrays=ladingMap[key];
				var tbody =$("<tbody></tbody>");
				if(ladingArrays.length>0){
					for(var i=0;i<ladingArrays.length;i++){
						var ladingDto = ladingArrays[i];
						var readOnly="";
						var canSureQ =ladingDto.canSureQuantity;
						var proColor = ladingDto.productColor;
						//如果可质检数里面没有，每次可收货数置0
						if(!canLadingMap[proColor]||canLadingMap[proColor]=='undefined'||canLadingMap[proColor]==0){
							readOnly="readOnly";
							canSureQ=0;
						}
						var curSku = ladingDto.sku;
						var options ="";
						var disable ="";
						if(skuMap[key]){
							if(skuMap[key].length==1){
								disable="disabled";
							}
							//根据名字查出sku
							for(var a in skuMap[key]){
								var skuStr = skuMap[key][a];
								var sku = skuStr.split('|')[1];
								var fnsku = fnskuMap[sku];
								options=options+"<option "+(curSku==sku?'selected':'')+" value='"+sku+"'>"+sku+"["+fnsku+"]</option>";
								if(curSku==""&&a==0){
									curSku=skuMap[key][0].split('|')[1];
								}
							}
							
							if(i==0){
								var tr = $("<tr></tr>");
					            tr.append("<td style='text-align:center'><input type='hidden' class='isNew' value='0'> <input type='text' readOnly style='width:200px' name='productConName'/><input type='hidden' name='id'/><input type='hidden' name='isPass'/>"+
					            		"<input type='hidden' name='balanceDelay1'/><input type='hidden' name='balanceDelay2'/><input type='hidden' name='balanceRate1'/><input type='hidden' name='balanceRate2'/><input type='hidden' name='totalAmount'/><input type='hidden' name='totalPaymentAmount'/><input type='hidden' name='totalPaymentPreAmount'/>"+
					            		"<input type='hidden' name='purchaseOrderItem.id'/><input type='hidden' class='proColor' value='"+proColor+"'/></td>");
					            tr.append("<td><select class='skuS' style='width:90%' "+disable+">"+options+"</select><input type='hidden' name='sku'/></td>");
					            tr.append("<td> <a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantityLading' readonly /></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantityOffLading' readonly /></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantitySure' readonly /></td>");
					            tr.append("<td><input type='hidden' class='canSureQuantity'/> <input style='width: 90%' type='text' name='quantitySureTemp' "+readOnly+"/></td>");
					            tr.append("<td> <input  style='width: 90%' type='text' name='remark' /></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantitySpares'/></td>");
					            tr.find("td:first").attr("rowSpan", ladingArrays.length).css("vertical-align","middle").css("text-align","center");
								tr.find("input[name='productConName']").val(key);
								tr.find(".purchaseOrderNo").text(ladingDto.purchaseOrderNo);
								tr.find("input[name='quantityLading']").val(ladingDto.quantityLading);
								tr.find("input[name='quantityOffLading']").val(ladingDto.quantityOffLading);
								tr.find("input[name='quantitySure']").val(ladingDto.quantitySure);
								tr.find("input[name='quantitySureTemp']").val(canSureQ);
								tr.find("input[name='quantitySpares']").val(ladingDto.quantitySpares);
								
								tr.find("input[name='balanceDelay1']").val(ladingDto.balanceDelay1);
							    tr.find("input[name='balanceDelay2']").val(ladingDto.balanceDelay2);
							    tr.find("input[name='balanceRate1']").val(ladingDto.balanceRate1);
							    tr.find("input[name='balanceRate2']").val(ladingDto.balanceRate2);
							    tr.find("input[name='totalAmount']").val(ladingDto.totalAmount);
							    tr.find("input[name='totalPaymentAmount']").val(ladingDto.totalPaymentAmount);
							    tr.find("input[name='totalPaymentPreAmount']").val(ladingDto.totalPaymentPreAmount);
							    
							    
								tr.find(".canSureQuantity").val(ladingDto.canSureQuantity);
								tr.find("input[name='purchaseOrderItem.id']").val(ladingDto.purchaseOrderItemId);
								tr.find("input[name='remark']").val(ladingDto.remark);
								tr.find("input[name='id']").val(ladingDto.id);
								tr.find("input[name='isPass']").val(ladingDto.isPass);
								tr.find("select.skuS").select2();
								tr.find("input[name='sku']").val(curSku);
								var hrefs="${ctx}/psi/lcPurchaseOrder/view?id="+ladingDto.purchaseOrderId;   
								tr.find(".orderId").attr('href',hrefs); 
					            tbody.append(tr);
							}else{
								var tr =$("<tr class='notFirstRow'></tr>");
								tr.append("<td><input type='hidden' class='isNew' value='0'><select class='skuS' style='width:90%' "+disable+" >"+options+"</select><input type='hidden' name='sku'/><input type='hidden' class='proColor' value='"+proColor+"'/></td>");
					            tr.append("<td><input type='hidden' name='id'/><input type='hidden' name='isPass'/><input type='hidden' name='productConName'/><input type='hidden' name='purchaseOrderItem.id'/>"+
					            		"<input type='hidden' name='balanceDelay1'/><input type='hidden' name='balanceDelay2'/><input type='hidden' name='balanceRate1'/><input type='hidden' name='balanceRate2'/><input type='hidden' name='totalAmount'/><input type='hidden' name='totalPaymentAmount'/><input type='hidden' name='totalPaymentPreAmount'/>"+
					            		"<a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantityLading' readonly /></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantityOffLading' readonly /></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantitySure' readonly /></td>");
					            tr.append("<td><input type='hidden' class='canSureQuantity'/> <input type='text' style='width: 90%' name='quantitySureTemp' "+readOnly+"/></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='remark' /></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantitySpares'  /></td>");
					            tr.find("input[name='quantityLading']").val(ladingDto.quantityLading);
					            tr.find("input[name='quantityOffLading']").val(ladingDto.quantityOffLading);
								tr.find("input[name='quantitySure']").val(ladingDto.quantitySure);
								tr.find("input[name='quantitySureTemp']").val(canSureQ);
								tr.find("input[name='quantitySpares']").val(ladingDto.quantitySpares);
								
								tr.find("input[name='balanceDelay1']").val(ladingDto.balanceDelay1);
							    tr.find("input[name='balanceDelay2']").val(ladingDto.balanceDelay2);
							    tr.find("input[name='balanceRate1']").val(ladingDto.balanceRate1);
							    tr.find("input[name='balanceRate2']").val(ladingDto.balanceRate2);
							    tr.find("input[name='totalAmount']").val(ladingDto.totalAmount);
							    tr.find("input[name='totalPaymentAmount']").val(ladingDto.totalPaymentAmount);
							    tr.find("input[name='totalPaymentPreAmount']").val(ladingDto.totalPaymentPreAmount);
							        
								tr.find(".canSureQuantity").val(ladingDto.canSureQuantity);
								tr.find("input[name='remark']").val(ladingDto.remark);
								tr.find("input[name='id']").val(ladingDto.id);
								tr.find("input[name='isPass']").val(ladingDto.isPass);
								tr.find("select.skuS").select2();
								var hrefs="${ctx}/psi/lcPurchaseOrder/view?id="+ladingDto.purchaseOrderId;   
								tr.find(".orderId").attr('href',hrefs); 
								tr.find(".purchaseOrderNo").text(ladingDto.purchaseOrderNo);
							    tr.find("input[name='purchaseOrderItem.id']").val(ladingDto.purchaseOrderItemId);
								tr.find("input[name='productConName']").val(key);
								tr.find("input[name='sku']").val(curSku);
					            tbody.append(tr);
							}
						}else{
							//没条码匹配的
							var arr=key.split('|');
							var tempProName = arr[0];
							var tempCountry=(arr[1]=="us"?'com':arr[1]);
							var tempColor="";
							var tempCon="";
							if(arr.length>2){
								tempColor=arr[2];
								tempCon=tempProName+"_"+tempColor+"_"+tempCountry;
							}else{
								tempCon=tempProName+"_"+tempCountry;
							}
							var isNew =0;
							if(newProducts.indexOf(tempCon)>=0){
								isNew=1;
								options=options+"<option value=''>(新品)</option>";
							}else{
								options=options+"<option value=''>无绑定条码</option>";
							};
							
							var tr = $("<tr></tr>");
							if(i==0){
					            tr.append("<td style='text-align:center'><input type='hidden' class='isNew' value='"+isNew+"'> <input type='text' readOnly style='width:200px' name='productConName'/><input type='hidden' name='id'/><input type='hidden' name='isPass'/>"+
					            		"<input type='hidden' name='balanceDelay1'/><input type='hidden' name='balanceDelay2'/><input type='hidden' name='balanceRate1'/><input type='hidden' name='balanceRate2'/><input type='hidden' name='totalAmount'/><input type='hidden' name='totalPaymentAmount'/><input type='hidden' name='totalPaymentPreAmount'/>"+
					            		"<input type='hidden' name='purchaseOrderItem.id'/></td>");
					            tr.append("<td><select class='skuS' style='width:90%' "+disable+">"+options+"</select><input type='hidden' name='sku'/></td>");
					            tr.append("<td> <a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
							}else{
								tr.append("<td><input type='hidden' class='isNew' value='"+isNew+"'><select class='skuS' style='width:90%' "+disable+" >"+options+"</select><input type='hidden' name='sku'/></td>");
					            tr.append("<td><input type='hidden' name='id'/><input type='hidden' name='isPass'/><input type='hidden' name='productConName'/><input type='hidden' name='purchaseOrderItem.id'/>"+
					            		"<input type='hidden' name='balanceDelay1'/><input type='hidden' name='balanceDelay2'/><input type='hidden' name='balanceRate1'/><input type='hidden' name='balanceRate2'/><input type='hidden' name='totalAmount'/><input type='hidden' name='totalPaymentAmount'/><input type='hidden' name='totalPaymentPreAmount'/>"+
					            		"<a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
							}
							
						
					           // tr.append("<td style='text-align:center'><input type='hidden' class='isNew' value='"+isNew+"'/> <input type='text' readOnly style='width:200px' name='productConName'/><input type='hidden' name='id'/><input type='hidden' name='purchaseOrderItem.id'/></td>");
					           // tr.append("<td><select class='skuS' style='width:90%' "+disable+">"+options+"</select><input type='hidden' name='sku'/></td>");
					           
					            tr.append("<td> <input readonly style='width: 90%' type='text' name='quantityLading' /><input type='hidden' class='proColor' value='"+proColor+"'/></td>");
					            tr.append("<td> <input readonly style='width: 90%' type='text' name='quantityOffLading' /></td>");
					            tr.append("<td> <input readonly style='width: 90%' type='text' name='quantitySure' /></td>");
					            tr.append("<td> <input style='width: 90%' type='text' name='quantitySureTemp' "+readOnly+"/></td>");
					            tr.append("<td> <input style='width: 90%' type='text' name='remark' /></td>");
					            tr.append("<td> <input style='width: 90%' type='text' name='quantitySpares' /></td>");
					            if(i==0){
					            	tr.find("td:first").attr("rowSpan", ladingArrays.length).css("vertical-align","middle").css("text-align","center");
					            }
								tr.find("input[name='productConName']").val(key);
								tr.find(".purchaseOrderNo").text(ladingDto.purchaseOrderNo);
								tr.find("input[name='quantityLading']").val(ladingDto.quantityLading);
								tr.find("input[name='quantityOffLading']").val(ladingDto.quantityOffLading);
								tr.find("input[name='quantitySure']").val(ladingDto.quantitySure);
								tr.find("input[name='quantitySureTemp']").val(canSureQ);
								tr.find("input[name='quantitySpares']").val(ladingDto.quantitySpares);
								tr.find("input[name='purchaseOrderItem.id']").val(ladingDto.purchaseOrderItemId);
								tr.find("input[name='remark']").val(ladingDto.remark);
								tr.find("input[name='id']").val(ladingDto.id);
								tr.find("select.skuS").select2();
								tr.find("input[name='sku']").val(curSku);
								var hrefs="${ctx}/psi/lcPurchaseOrder/view?id="+ladingDto.purchaseOrderId;   
								tr.find(".orderId").attr('href',hrefs); 
					            tbody.append(tr);
						}
						
					}
				}
				createTable.append(tbody);
			}
			
			$("select.skuS").live("click",function(){
				var tr =$(this).parent().parent();
				tr.find("input[name='sku']").val($(this).val());
			})
			
			
			$('#myfileupload').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
		
			$("#inputForm").validate({
				submitHandler: function(form){
				var flag =1;
				var overTest=0;
				var curTestMap ={};
				for(var key in canLadingMap){
					curTestMap[key]=canLadingMap[key];
				}
				var proColor="";
				$("select.skuS").each(function(){
					var tr = $(this).parent().parent();
					var isNew = tr.find(".isNew").val();
					if(isNew==0&&($(this).val()==null||$(this).val()=='')){
						flag=2;
						return ;
					}
					//对质检数据进行校验   
					proColor=tr.find(".proColor").val();
					if(curTestMap[proColor]!='undefined'){
						var tempSure=tr.find("input[name='quantitySureTemp']").val();
						var reduce = parseInt(curTestMap[proColor])-parseInt(tempSure);
						curTestMap[proColor]=reduce;
						if(parseInt(reduce)<0){
							overTest=1;
							return false;
						}
					}
					
				});
				
				if(overTest==1){
					top.$.jBox.tip(proColor+"质检数为："+Math.abs(canLadingMap[proColor])+",请联系品检或产品经理进行品检登记确认！","info",{timeout:3000});
					return false;
				}
				
				
				
				if(flag==2){
					top.$.jBox.tip("请联系销售绑定sku!","info",{timeout:3000});
					return false;
				}
				
				var canFlag=1;
				$(".canSureQuantity").each(function(){
					var curQuantity = $(this).parent().find("input[name='quantitySureTemp']").val();
					if(parseInt($(this).val())<parseInt(curQuantity)){
						canFlag=2;
						return false;
					}
				});
				
				if(canFlag==2){
					top.$.jBox.tip("确认收货数不能大于可收货数","info",{timeout:3000});
					return false;
				}
				
				top.$.jBox.confirm('您确定要提交吗','系统提示',function(v,h,f){
				if(v=='ok'){
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
					
					//把所有disable的select放开
					$("select[disabled]").each(function(){
						$(this).removeAttr("disabled");
					});
					
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
				}
				},{buttonsFocus:1,persistent: true});
				top.$('.jbox-body .jbox-icon').css('top','55px');
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
		
		
		function goEdit(ladingId){
			var params = {};
			params.id = ladingId;
			window.location.href = "${ctx}/psi/lcPsiLadingBill/edit?"+$.param(params);
		}
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/lcPsiLadingBill/">收货单列表</a></li>
		<li class="active"><a href="#">收货单确认</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="psiLadingBill" action="${ctx}/psi/lcPsiLadingBill/sureSave" method="post" class="form-horizontal" enctype="multipart/form-data">
	    <input type='hidden' name="supplier.id" value="${psiLadingBill.supplier.id}">
	    <input type='hidden' name="billSta" value="${psiLadingBill.billSta}">
	    <input type='hidden' name="createDate" value="${psiLadingBill.createDate}">
	    <input type='hidden' name="delFlag" value="${psiLadingBill.delFlag}">
	    <input type='hidden' name="createUser.id" value="${psiLadingBill.createUser.id}">
	    <input type='hidden' name="oldItemIds" value="${psiLadingBill.oldItemIds}">
	    <input type='hidden' name="billNo" value="${psiLadingBill.billNo}">
	    <input type='hidden' name="id" value="${psiLadingBill.id}">
	    <div  class="alert alert-info"><strong>Tips:1,实际收货日期对于财务对账来说很重要，请根据供应商实际送货日期进行填写！2,如果品检没完成，那么这个产品可确认数为<span style='color:red'>0</span>!!!</strong></div>
	    <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>供应商</b>:</label>
				<div class="controls" >
				<span>
				<input type="text" readonly   value="${psiLadingBill.supplier.nikename}"/>
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:33%" >
				<label class="control-label"><b>承运商</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" readonly   value="${psiLadingBill.tranSupplier.nikename}"/>
				</span>
				
				</div>
			</div>
			<div class="control-group"  style="float:left;width:33%" >
				<label class="control-label"><b>实际收货日期</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" name="actualDeliveryDate"   class="Wdate required"/>
				</span>
				</div>
			</div>
		</div>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>送货人姓名</b>:</label>
				<div class="controls" >
				<span>
				<input type="text" name="tranMan" class="required"/>
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:33%" >
				<label class="control-label"><b>电话</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" name="phone"  class="required"/>
				</span>
				
				</div>
			</div>
			<div class="control-group"  style="float:left;width:33%" >
				<label class="control-label"><b>车牌号</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" name="carNo"  class="required"/>
				</span>
				</div>
			</div>
		</div>
		<blockquote style="float:left;">
			<p style="font-size: 14px">收货项信息</p>
		</blockquote>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 15%">产品信息</th>
				   <th style="width: 20%">sku</th>
				   <th style="width: 10%">订单号</th>
				   <th style="width: 5%">总数量</th>
				   <th style="width: 5%">线下数</th>
				   <th style="width: 5%">已确认数量</th>
				   <th style="width: 5%">本次确认数</th>
				   <th style="width: 10%">备注</th>
				   <th style="width: 5%">备品数</th>
				   
			</tr>
		</thead>
	</table>
		<blockquote>
			<p style="font-size: 14px">凭证</p>
		</blockquote>
		
		<div class="control-group">
			<div class="controls">
				<span class="help-inline">支持多附件</span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="attchmentFile" type="file" id="myfileupload" />
			</div>
		</div>
					
	
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="确认"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<c:if test="${psiLadingBill.totalPaymentPreAmount+psiLadingBill.totalPaymentAmount ==0&&psiLadingBill.billSta eq '0' }">
				<input id="btnEdit" class="btn btn-primary" type="button" value="修改" onclick="goEdit('${psiLadingBill.id}')"/>&nbsp;&nbsp;&nbsp;&nbsp;
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
