<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单付款管理</title>
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
			
			$("#btnSureSubmit").on('click',function(e){
				top.$.jBox.confirm('确认要提出付款申请？申请后付款将不允许改动！','系统提示',function(v,h,f){
				if(v=='ok'){
					$("#paymentSta").val("1");
					$("#inputForm").submit();
					$("#btnSubmit").attr("disabled","disabled");
					$("#btnNoSureSubmit").attr("disabled","disabled");
					$("#btnSureSubmit").attr("disabled","disabled");
				}
				return true;
				},{buttonsFocus:1,persistent: true});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#btnNoSureSubmit").on('click',function(e){
				top.$.jBox.confirm('确认要跳过付款，直接进入财务已审核状态？','系统提示',function(v,h,f){
				if(v=='ok'){
					$("#paymentSta").val("4");
					$("#inputForm").submit();
				}
				return true;
				},{buttonsFocus:1,persistent: true});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});	
			
			$(".rate").on("blur",function(){
				if($(this).val()!=''){
					var tr = $(this).parent().parent();
					var afterAmount = tr.find(".paymentAmount").val()*tr.find(".rate").val()
					tr.find(".afterAmount").val(toDecimal(afterAmount));
					tr.find("input:checkbox").attr("checked",'checked');
					
					var totalAmount=0;
					var tbody =tr.parent();
					
					tbody.find("input:checkbox:checked").each(function(){
						totalAmount=totalAmount+parseFloat($(this).parent().parent().find(".afterAmount").val());
					})
					$(".totalAmount").val(totalAmount);
				}
			});
			
			
			
			$(".totalRate").on("blur",function(){
				if($(this).val()!=''){
					var totalAmount=$(".totalAmount").val();
					var totalRate=$(this).val();
					var paymentAmount=totalAmount/totalRate;
					$("#paymentAmount").val(toDecimal(paymentAmount));
				}
			});
			
			$(".isPayment").on("click",function(){
				var tr =$(this).parent().parent();
				var totalAmount=$(".totalAmount").val();
				var curAmount = tr.find(".afterAmount").val();
				if(this.checked){
					if(curAmount!=''){
						$(".totalAmount").val(parseFloat(totalAmount)+parseFloat(curAmount)).focus();
					}
				}else{
					if(curAmount!=''){
						$(".totalAmount").val(totalAmount-parseFloat(curAmount)).focus();
					}
				};
			});
			
			$(".totalAmount").on("blur",function(){
				var totalRate=$(".totalRate").val();
				if($(this).val()!=''&&totalRate!=''){
					var totalAmount=$(this).val();
					var totalRate=$(".totalRate").val();
					var paymentAmount=totalAmount/totalRate;
					$("#paymentAmount").val(toDecimal(paymentAmount));
				}
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					//判断都没选，点击保存的
					var isChecked =false;
					if(!$("a[target='_blank']").text()){
						top.$.jBox.tip("没附件，请到运单那里添加相应附件！", 'info',{timeout:3000});
						return false;
					}
					
					$(".isPayment").each(function(){
						if(this.checked){
							isChecked=true;
							return ;
						};
						
					});
					
					if(!isChecked){
						top.$.jBox.tip("必须选中一项付款项！", 'info',{timeout:3000});
						return false;
					}
					
					$(".isPayment").each(function(){
						if(!this.checked){
							var tr=$(this).parent().parent();
							tr.remove();
						};
						
					});
					
					$("#contentTable tbody tr").each(function(i,j){
						$(j).find("input[type!='']").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
							}
						});
					}); 
					
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
					$("#btnSureSubmit").attr("disabled","disabled");
					$("#btnNoSureSubmit").attr("disabled","disabled");
					
					
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
		<li><a href="${ctx}/psi/lcPsiTransportPayment/">运单付款列表</a></li>
		<li class="active"><a href="#">编辑运单付款</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiTransportPayment" action="${ctx}/psi/lcPsiTransportPayment/editSave" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input name="id" 		 type="hidden" value="${psiTransportPayment.id}"/>
		<input name="supplier.id" type="hidden" value="${psiTransportPayment.supplier.id}"/>
		<input name="createUser" type="hidden" value="${psiTransportPayment.createUser.id}"/>
		<input name="createDate" type="hidden" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportPayment.createDate}'/>"/>
		<input name="paymentNo"  type="hidden" value="${psiTransportPayment.paymentNo}"/>
		<input name="paymentSta" type="hidden" value="${psiTransportPayment.paymentSta}" id="paymentSta"/>
		<input name="oldItemIds" type="hidden" value="${psiTransportPayment.oldItemIds}"/>
		
		<input name="supplierCostPath" type="hidden" value="${psiTransportPayment.supplierCostPath}"/>
		
		
		
 		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:30%;height:25px">
				<label class="control-label" style="width:80px" ><b>承运商</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					<input readonly="readonly" value="${psiTransportPayment.supplier.nikename}" type="text" />
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:70%;height:25px" >
					<label class="control-label"><b>承运商账号</b>:</label>
					<div class="controls" >
					<span>
						<select style="width:100%;" id="accountMaps" name="accountType">
							<c:forEach items="${accountMaps}" var="account" varStatus="i">
								 <option value='${account.key}' ${psiTransportPayment.accountType eq account.key ?'selected':''}>${account.value}</option>;
							</c:forEach>
						</select>
					</span>
					</div>
				</div>
		</div>
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 15%">运单号</th>
				   <th style="width: 10%">付款种类</th>
				   <th style="width: 10%">应付金额</th>
				   <th style="width: 10%">币种</th>
				   <th style="width: 10%">汇率</th>
				   <th style="width: 10%">转换后金额</th>
				   <th style="width: 15%">备注</th>
				   <th style="width: 10%">是否付款</th>
				   
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${unPaymentTransMap}" var="tranMap">
			<c:forEach items="${tranMap.value}" var="item" varStatus="i">
			<tr>
				<c:choose>
					<c:when  test="${i.index==0}">
					<td style="text-align: center;vertical-align: middle;" rowspan="${fn:length(tranMap.value)}"><input style="width:90%" type="text" name="transportNo" readonly="readonly" value="${item.transportNo}" /></td>
						<td><input style="width:90%" type="text" name=paymentType readonly="readonly" value="${item.paymentType}" /></td>
					</c:when>
					<c:otherwise>
						<td><input type="hidden" name="transportNo" value="${item.transportNo}"/><input style="width:90%" type="text" name=paymentType readonly="readonly" value="${item.paymentType}" /></td>
					</c:otherwise>
				</c:choose>

				<td><input type="hidden" name="id" value="${item.id}" /><input type="hidden" name="tranOrderId" value="${item.tranOrderId}" /><input style="width:90%" type="text" name="paymentAmount" readonly="readonly" value="${item.paymentAmount}" class="paymentAmount"/></td>
				<td><input style="width:90%" type="text" name="currency" readonly="readonly" value="${item.currency}" /></td>
				<td><input style="width:90%" type="text" name="rate" class="rate" value="${item.rate}"/></td>
				<td><input style="width:90%" type="text" name="afterAmount" readonly="readonly" class="afterAmount" value="${item.afterAmount}"/></td>
				<td><input style="width:90%" type="text" name="remark" value="${item.remark}"/></td>
				<td><input style="width:90%" name="isChecked" type="checkbox" ${not empty item.id ?'checked':''} class="isPayment" /></td>
			</tr>
			</c:forEach>
			
		</c:forEach>
		</tbody>
		</table>
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px" ><b>总额:</b></label>
				<div class="controls" style="margin-left:100px" >
				<input class="totalAmount" name="beforeAmount" type="text" readonly="readonly"  style="width:100%" value="${psiTransportPayment.beforeAmount}" />
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label"  style="width:80px" >汇率:</label>
				<div class="controls" style="margin-left:100px" >
					<input class="totalRate" name="rate" type="text" style="width:100%" value="${psiTransportPayment.rate}" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px"><b>付款金额:</b></label>
				<div class="controls" style="margin-left:100px" >
					<input name="paymentAmount" type="text" id="paymentAmount" value="${psiTransportPayment.paymentAmount}" style="width:100%" />
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:23%;height:25px">
				<label class="control-label" style="width:80px" ><b>货币类型:</b></label>
				<div class="controls" style="margin-left:100px" >
				<select name="currency" style="width:95%">
					<c:forEach items="${currencys}" var="currency">
						<option value="${currency}" ${psiTransportPayment.currency eq currency ?'selected':''}>${currency}</option>
					</c:forEach>
				</select>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:98%;">
				<label class="control-label" style="width:80px">备注:</label>
				<div class="controls" style="margin-left:100px">
					<textarea name="remark"  style="width:100%;height:80px;" >${psiTransportPayment.remark}</textarea>
				</div>
			</div>
		</div>
		
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 14px">查看供应商费用明细</p>
		</blockquote>
		
		<div class="control-group" style="float:left;width:98%;">
			<div class="controls">
			<c:forEach items="${supplierFilePath}" var="fileMap">
					<c:forEach items="${fn:split(fileMap.value,',')}" var="attchment" varStatus="j">
						<a target="_blank" href="<c:url value='/data/site/psi/lcPsiTransport/${attchment}'/>">${fileMap.key} ${j.index+1}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
			</c:forEach>
			</div>
		</div>
		
		<div class="form-actions" style="float:left;width:98%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="草稿"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnNoSureSubmit" class="btn btn-primary" type="button" value="保存"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSureSubmit" class="btn btn-primary" type="button" value="申请"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
