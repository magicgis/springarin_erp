<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单付款修正管理</title>
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
			
			//获取付款类型选择框
			eval('var itemMap=${itemMap}');
			
			$('#myfileupload').MultiFile({
				max : 3,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			
			$("#inputForm").on("change",".reviseType",function(e){
				var reviseType = e.val;
				if(e.removed){
					var removeVal = e.removed.id;
					$("select.reviseType").each(function(){
	    				if($(this).select2("val")!=reviseType){
	    					$(this).find("option[value='"+reviseType+"']").remove();    					
	    					$(this).append("<option value='"+removeVal+"'>"+removeVal+"</option>");
	    				}
	    			});
				}
				
				
				var tr = $(this).parent().parent();
				var currSupplierId=$("#supplier").val();
				for(var i=0;i<itemMap[currSupplierId].length;i++){
					var item=itemMap[currSupplierId][i];
					if(item[0]==$(this).val()){
						tr.find("input[name='oldAmount']").val(item[1]);
						tr.find("input[name='currency']").val(item[2]);
						break;
					};
				};
				tr.find(".reviseAmount").val("");
				tr.find(".rate").val("");
				tr.find(".afterreviseAmount").val("");
				tr.find("input[name='remark']").val("");
			});
			
			
			$("#add-row").on("click",function(e){
				e.preventDefault();
				var option ="";
				var currSupplierId=$("#supplier").val();
				
				if($("#contentTable tbody tr").size()==itemMap[currSupplierId].length){
					return;
				}
				
				
				for(var i=0;i<itemMap[currSupplierId].length;i++){
					option=option+"<option value="+itemMap[currSupplierId][i][0]+">"+itemMap[currSupplierId][i][0]+"</option>";
				};
				
				var tbody=$("#contentTable tbody");
				var tr=$("<tr></tr>");
				
				var td="<td><select style='width: 90%' class='reviseType' name='reviseType' >";
				
				var i=0;
				for(var j=0;j<itemMap[currSupplierId].length;j++){
					var flag=0;
					var key=itemMap[currSupplierId][j][0];
					
					$("select.reviseType").each(function(){
						if(key==$(this).select2("val")){
							flag=1;
						}
					});
					
					if(flag==0){
						if(i==0){
							$("select.reviseType").each(function(){
								$(this).find("option[value='"+key+"']").remove();
							});
						}
						td=td.concat("<option value='"+key+"'>"+key+"</option>");
						i++;
					}
				}
				td=td.concat("</select></td>");
				tr.append(td);
				
				
				
				tr.append("<td><input style='width:90%' type='text' name='oldAmount' class='oldAmount' readonly='readonly'/></td>");
				tr.append("<td><input style='width:90%' type='text' name='currency'  readonly='readonly' /></td>");
				tr.append("<td><input style='width:90%' type='text' name='reviseAmount' class='reviseAmount' /></td>");
				tr.append("<td><input style='width:90%' type='text' name='rate' class='rate'/></td>");
				tr.append("<td><input style='width:90%' type='text' class='afterAmount' readonly='readonly' /></td>");	
				tr.append("<td><input style='width:90%' type='text' name='remark'/></td>");
	            tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>删除修正项</a></td>");
				tbody.append(tr);
				//调用change方法，给各项赋值；
				tr.find("select.reviseType").select2().change();
			});
			
			
			$("#supplier").on("change",function(){
				
				var params={};
				params['supplier.id']=$(this).val();
				params['id']=$("#tranOrderId").val();
				window.location.href = "${ctx}/psi/psiTransportOrder/revise?"+$.param(params);
			});
			
			$("#add-row").click();
			
			$(".rate").live("blur",function(){
				var tr = $(this).parent().parent();
				if($(this).val()!=''&&tr.find(".reviseAmount").val()!=''){
					var tr = $(this).parent().parent();
					var afterAmount = tr.find(".reviseAmount").val()*$(this).val();
					tr.find(".afterAmount").val(toDecimal(afterAmount));
					var totalAmount=0;
					var tbody =tr.parent();
					
					tbody.find(".afterAmount").each(function(){
						if($(this).val()!=''){
							totalAmount=totalAmount+parseFloat($(this).val());
						}
					});
					if(totalAmount!=0){
						$(".totalAmount").val(totalAmount).focus();
					}
					
				}
			});
			
			$(".totalRate").on("blur",function(){
				if($(this).val()!=''){
					var totalAmount=$(".totalAmount").val();
					var totalRate=$(this).val();
					var paymentAmount=totalAmount/totalRate;
					$("#reviseAmount").val(toDecimal(paymentAmount));
				}
			});
			
			
			
			$(".totalAmount").on("blur",function(){
				var totalRate=$(".totalRate").val();
				if($(this).val()!=''&&totalRate!=''){
					var totalAmount=$(this).val();
					var totalRate=$(".totalRate").val();
					var paymentAmount=totalAmount/totalRate;
					$("#reviseAmount").val(toDecimal(paymentAmount));
				}
			});
			
			$(".reviseAmount").live("blur",function(){
				var tr = $(this).parent().parent();
				if($(this).val()!=''&&tr.find(".rate").val()!=''){
					var tr = $(this).parent().parent();
					var afterAmount = tr.find(".reviseAmount").val()*tr.find(".rate").val();
					tr.find(".afterAmount").val(toDecimal(afterAmount));
					var totalAmount=0;
					var tbody =tr.parent();
					
					tbody.find(".afterAmount").each(function(){
						if($(this).val()!=''){
							totalAmount=totalAmount+parseFloat($(this).val());
						}
					});
					if(totalAmount!=0){
						$(".totalAmount").val(totalAmount).focus();
					}
					
				}
			});
			
			
		
			
			
			$(".remove-row").live("click",function(){
				 if($('#contentTable tbody tr').size()>1){
					var tr = $(this).parent().parent();
					var id=tr.find("select.reviseType").select2("val");
					tr.remove();
					if(id){
						$("select.reviseType").each(function(){
							$(this).append("<option value='"+id+"'>"+id+"</option>");
						});
					}
				}
			});
			
			
			
			$("#inputForm").validate({
				rules:{
					"rate":{
						"required":true
					},"reviseAmount":{
						"required":true
					}
				},
				messages:{
					"rate":{"required":'汇率不能为空'},
					"reviseAmount":{"required":'修正额不能为空'}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					//判断都没选，点击保存的
					var isAdd =false;
					
					$(".reviseAmount").each(function(){
						isAdd=true;
						return;
					});
					
					if(!isAdd){
						top.$.jBox.tip("请添加修正项！", 'info',{timeout:3000});
						return false;
					}
					
					var numberflag  =true;
					$("#contentTable tbody tr").each(function(i,j){
						if(numberflag){
							if(parseFloat($(this).find(".oldAmount").val())+parseFloat($(this).find(".reviseAmount").val())<0){
								numberflag = false;
							}
						}
					});
					
					if(!numberflag){
						top.$.jBox.tip("修正金额不能大于原金额！","info",{timeout:3000});
						return false;
					}
					
					
					var keyStr="";
					var twoStr="";
					var flag = 1;
					$("#contentTable tbody tr").each(function(){
						var curkeyStr=$(this).find("select.reviseType").val();
						if(keyStr.indexOf(curkeyStr+",")>=0){
							twoStr=curkeyStr;
							flag = 2;
							return;
						}else{
							keyStr=keyStr+curkeyStr+",";
						};
					});
					
					if(flag==2){
						top.$.jBox.tip("相同付款种类只能有一条数据！"+twoStr, 'info',{timeout:3000});
						return false;
					}
					
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
					
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
				}
			});
		});
		

		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*100)/100;  
	            return f;  
	     };
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiTransportRevise/">运单付款修正列表</a></li>
		<li class="active"><a href="#">新建运单付款修正</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiTransportRevise" action="${ctx}/psi/psiTransportRevise/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id" />
		<input type='hidden'  name="tranOrderId" value="${psiTransportRevise.tranOrderId}" id="tranOrderId"/>
 		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:20%;height:25px">
				<label class="control-label" style="width:80px" ><b>运单号</b>:</label>
				<div class="controls" style="margin-left:100px">
				<input type="text" readonly name="tranOrderNo" value="${psiTransportRevise.tranOrderNo}" style="width:100%" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
				<label class="control-label" style="width:80px" ><b>承运商</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					<select style="width:100%" id="supplier" name="supplier.id" class="required">
						<c:forEach items="${supplierMap}" var="supplierMap" varStatus="i">
							 <option value='${supplierMap.key}' ${psiTransportRevise.supplier.id eq supplierMap.key ?'selected':'' }>${supplierMap.value.nikename}</option>;
						</c:forEach>
					</select>
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:60%;height:25px" >
					<label class="control-label" style="width:80px"><b>承运商账号</b>:</label>
					<div class="controls" style="margin-left:100px">
					<span>
						<select style="width:100%;"  name="accountType" class="required">
						<c:forEach items="${accountMaps}" var="account">
							<option value="${account.key}">${account.value}</option>
						</c:forEach>
						</select>
					</span>
					</div>
				</div>
		</div>
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		<div  style="font-size: 14px;margin: 5px 100px 5px 0px;float:right"><a href="#" id="add-row"><span class="icon-plus"></span>增加修正项</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed" >
		<thead>
			<tr>
				   <th style="width: 10%">付款种类</th>
				   <th style="width: 10%">运单金额</th>
				   <th style="width: 10%">币种</th>
				   <th style="width: 10%">修正额</th>
				   <th style="width: 10%">汇率</th>
				   <th style="width: 10%">计算后额度</th>
				   <th style="width: 15%">备注</th>
				   <th style="width: 15%">操作</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
		</table>
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 14px">付款信息</p>
		</blockquote>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px" ><b>总额:</b></label>
				<div class="controls" style="margin-left:100px" >
				<input class="totalAmount" type="text" readonly  style="width:100%" />
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label"  style="width:80px" ><b>汇率:</b></label>
				<div class="controls" style="margin-left:100px" >
					<input class="totalRate" name="rate"  class="required"  type="text" style="width:100%" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px"><b>付款金额:</b></label>
				<div class="controls" style="margin-left:100px" >
					<input name="reviseAmount" type="text"  class="required" id="reviseAmount"  style="width:100%" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:23%;height:25px">
					<label class="control-label" style="width:80px" ><b>货币类型:</b></label>
					<div class="controls" style="margin-left:100px" >
					<select name="currency" style="width:95%" class="required">
					       <option value="" ></option>
						<c:forEach items="${currencys}" var="currency">
							<option value="${currency}" ${psiTransportRevise.currency eq currency ?'selected':''}>${currency}</option>
						</c:forEach>
					</select>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:98%;">
				<label class="control-label" style="width:80px"><b>备注:</b></label>
				<div class="controls" style="margin-left:100px">
					<textarea name="remark"  style="width:100%;height:80px;" ></textarea>
				</div>
			</div>
		</div>
		
		
		<div class="control-group" style="float:left;width:100%">
		<label class="control-label" style="width:80px"><b>附件</b>:</label>
			<div class="controls" style="margin-left:100px">
				<span class="help-inline">支持多附件</span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="filePath" type="file" id="myfileupload"/>
			</div>
		</div>
		
		
		<div class="form-actions" style="float:left;width:98%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="申请" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
