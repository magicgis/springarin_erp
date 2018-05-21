<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收货单质检编辑</title>
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
			
			eval('var ladingMap=${ladingMap}');
			eval('var skuMap=${skuMap}');
			eval('var fnskuMap=${fnskuMap}');
			// 生成要编辑表格 
			var createTable = $('#contentTable');
			for(var key in ladingMap){
				var ladingArrays=ladingMap[key];
				var tbody =$("<tbody></tbody>");
				if(ladingArrays.length>0){
					for(var i=0;i<ladingArrays.length;i++){
						var ladingDto = ladingArrays[i];
						var curSku = ladingDto.sku;
						var options ="";
						var disable ="";
						var pass = "<option value='1' "+(ladingDto.isPass=="1"?"selected":"")+">合格</option><option value='0' "+(ladingDto.isPass=="0"?"selected":"")+">不合格</option>";

						var temp = "";
						if(ladingDto.hisRecord){
							temp = "<a data-placement='left' href='#'  rel='popover' data-content='"+ladingDto.hisRecord+"'>查看</a>";
						}
						var dateStr = "<input style='width: 90%' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}); value='<fmt:formatDate value="${now}" pattern="yyyy-MM-dd" />' readonly='readonly' class='Wdate' type='text' id='qualityDate' name='qualityDate' class='input-small'/> ";
						if(skuMap[key]){
							//if(skuMap[key].length==1){
								disable="disabled";
							//}
							//根据名字查出sku
							for(var a in skuMap[key]){
								var skuStr = skuMap[key][a];
								var sku = skuStr.split('|')[1];
								var fnsku = fnskuMap[sku];
								options=options+"<option "+(curSku==sku?'selected':'')+" value='"+sku+"'>"+sku+"["+fnsku+"]</option>";
							}
							if(i==0){
								var tr = $("<tr></tr>");
					            tr.append("<td style='text-align:center'> <input type='text' readOnly style='width:200px' name='productConName'/><input type='hidden' name='id'/><input type='hidden' name='purchaseOrderItem.id'/></td>");
					            tr.append("<td><select class='skuS' style='width:90%' "+disable+">"+options+"</select><input type='hidden' name='sku'/></td>");
					            tr.append("<td> <a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantityLading' readonly /></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantitySure' readonly /></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantityGoods' readonly/></td>");	<%--已验货总数--%>
					            tr.append("<td> <input type='text' style='width: 90%' name='quantityActual' class='number'/></td>");	<%--本次验货数--%>
					            tr.append("<td>"+dateStr+"</td>");	<%--验货时间--%>
					            tr.append("<td>"+temp+"</td>");	<%--验货记录--%>
					            tr.append("<td> <select name='isPass' style='width:90%'>"+pass+"</select></td>");
					            tr.find("td:first").attr("rowSpan", ladingArrays.length).css("vertical-align","middle").css("text-align","center");
								tr.find("input[name='productConName']").val(key);
								tr.find(".purchaseOrderNo").text(ladingDto.purchaseOrderNo);
								tr.find("input[name='quantityLading']").val(ladingDto.quantityLading);
								tr.find("input[name='quantitySure']").val(ladingDto.quantitySure);
								tr.find("input[name='quantityGoods']").val(ladingDto.quantityGoods);
								tr.find("input[name='quantityActual']").val(ladingDto.quantityLading-ladingDto.quantityGoods);
								tr.find("input[name='purchaseOrderItem.id']").val(ladingDto.purchaseOrderItemId);
								tr.find("input[name='remark']").val(ladingDto.remark);
								tr.find("input[name='id']").val(ladingDto.id);
								tr.find("select.skuS").select2();
								tr.find("select[name='isPass']").select2();
								tr.find("input[name='sku']").val(curSku);
								var hrefs="${ctx}/psi/purchaseOrder/view?id="+ladingDto.purchaseOrderId;   
								tr.find(".orderId").attr('href',hrefs); 
					            tbody.append(tr);
							}else{
								var tr =$("<tr class='notFirstRow'></tr>");
								tr.append("<td><select class='skuS' style='width:90%' "+disable+" >"+options+"</select><input type='hidden' name='sku'/></td>");
					            tr.append("<td><input type='hidden' name='id'/><input type='hidden' name='productConName'/><input type='hidden' name='purchaseOrderItem.id'/><a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantityLading' readonly /></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantitySure' readonly /></td>");
					            tr.append("<td> <input type='text' style='width: 90%' name='quantityGoods' readonly/></td>");	<%--已验货总数--%>
					            tr.append("<td> <input type='text' style='width: 90%' name='quantityActual' class='number'/></td>");	<%--本次验货数--%>
					            tr.append("<td>"+dateStr+"</td>");	<%--验货时间--%>
					            tr.append("<td>"+temp+"</td>");	<%--验货记录--%>
					            
					            tr.append("<td> <select name='isPass' style='width:90%'>"+pass+"</select></td>");
					            tr.find("input[name='quantityLading']").val(ladingDto.quantityLading);
								tr.find("input[name='quantitySure']").val(ladingDto.quantitySure);
								tr.find("input[name='quantityGoods']").val(ladingDto.quantityGoods);
								tr.find("input[name='quantityActual']").val(ladingDto.quantityLading-ladingDto.quantityGoods);
								tr.find("input[name='record']").val(ladingDto.record);
								tr.find("input[name='remark']").val(ladingDto.remark);
								tr.find("input[name='id']").val(ladingDto.id);
								tr.find("select.skuS").select2();
								tr.find("select[name='isPass']").select2();
								var hrefs="${ctx}/psi/purchaseOrder/view?id="+ladingDto.purchaseOrderId;   
								tr.find(".orderId").attr('href',hrefs); 
								tr.find(".purchaseOrderNo").text(ladingDto.purchaseOrderNo);
							    tr.find("input[name='purchaseOrderItem.id']").val(ladingDto.purchaseOrderItemId);
								tr.find("input[name='productConName']").val(key);
								tr.find("input[name='sku']").val(curSku);
					            tbody.append(tr);
							}
						}else{
							//没条码匹配的
							options=options+"<option value=''>无绑定条码</option>";
							var tr = $("<tr></tr>");
				            tr.append("<td style='text-align:center'> <input type='text' readOnly style='width:200px' name='productConName'/><input type='hidden' name='id'/><input type='hidden' name='purchaseOrderItem.id'/></td>");
				            tr.append("<td><select class='skuS' style='width:90%' "+disable+">"+options+"</select><input type='hidden' name='sku'/></td>");
				            tr.append("<td> <a target='_blank'  class='orderId purchaseOrderNo' href='#'/></td>");
				            tr.append("<td> <input readonly style='width: 90%' type='text' name='quantityLading' /></td>");
				            tr.append("<td> <input readonly style='width: 90%' type='text' name='quantitySure' /></td>");
				            tr.append("<td> <input type='text' style='width: 90%' name='quantityGoods' readonly/></td>");	<%--已验货总数--%>
				            tr.append("<td> <input type='text' style='width: 90%' name='quantityActual' class='number'/></td>");	<%--本次验货数--%>
				            tr.append("<td>"+dateStr+"</td>");	<%--验货时间--%>
				            tr.append("<td>"+temp+"</td>");	<%--验货记录--%>
				            tr.append("<td> <select name='isPath' style='width:90%'>"+pass+"</select></td>");
				            tr.find("td:first").attr("rowSpan", ladingArrays.length).css("vertical-align","middle").css("text-align","center");
							tr.find("input[name='productConName']").val(key);
							tr.find(".purchaseOrderNo").text(ladingDto.purchaseOrderNo);
							tr.find("input[name='quantityLading']").val(ladingDto.quantityLading);
							tr.find("input[name='quantitySure']").val(ladingDto.quantitySure);
							tr.find("input[name='quantityGoods']").val(ladingDto.quantityGoods);
							tr.find("input[name='quantityActual']").val(ladingDto.quantityLading-ladingDto.quantityGoods);
							tr.find("input[name='quantitySureTemp']").val(ladingDto.canSureQuantity);
							tr.find("input[name='quantitySpares']").val(ladingDto.quantitySpares);
							tr.find("input[name='purchaseOrderItem.id']").val(ladingDto.purchaseOrderItemId);
							tr.find("input[name='remark']").val(ladingDto.remark);
							tr.find("input[name='id']").val(ladingDto.id);
							tr.find("select.skuS").select2();
							tr.find("select[name='isPath']").select2();
							tr.find("input[name='sku']").val(curSku);
							var hrefs="${ctx}/psi/purchaseOrder/view?id="+ladingDto.purchaseOrderId;   
							tr.find(".orderId").attr('href',hrefs); 
				            tbody.append(tr);
						}
						
					}
				}
				createTable.append(tbody);
			}
			
			$("a[rel='popover']").popover({trigger:'hover',html:true});
			
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
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiLadingBill/">收货单列表</a></li>
		<li class="active"><a href="#">收货单质检编辑</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiLadingBill" action="${ctx}/psi/psiLadingBill/passSave" method="post" class="form-horizontal">
	    <input type='hidden' name="id" value="${psiLadingBill.id}">
	    <blockquote style="float:left;">
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
				<label class="control-label"><b>送货日期</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" name="deliveryDate"   readonly="readonly" value="<fmt:formatDate value="${psiLadingBill.deliveryDate}" pattern="yyyy-MM-dd" />" />
				</span>
				</div>
			</div>
		</div>
		<blockquote style="float:left;">
			<p style="font-size: 14px">收货单项信息</p>
		</blockquote>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			   <th style="width: 12%">产品型号</th>
			   <th style="width: 18%">sku</th>
			   <th style="width: 10%">订单号</th>
			   <th style="width: 6%">总数量</th>
			   <th style="width: 8%">已确认数量</th>
			   <th style="width: 8%">已验收数量</th>
			   <th style="width: 8%">本次验货数量</th>
			   <th style="width: 10%">本次验货时间</th>
			   <th style="width: 8%">验货记录</th>
			   <th style="width: 8%">是否合格</th>
			</tr>
		</thead>
	</table>
	
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
