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
			
			
			
			$("#supplier").on("change",function(e){
				var params = {};
				params['supplier.id'] = $(this).val();
				window.location.href = "${ctx}/psi/lcPsiTransportPayment/add?"+$.param(params);
			});
			
			
			$(".rate").on("blur",function(){
				if($(this).val()!=''){
					var tr = $(this).parent().parent();
					var currency=tr.find("input[name='currency']").val();
					var afterAmount;
					if("CNY"==currency){
						afterAmount = tr.find(".paymentAmount").val()/tr.find(".rate").val();
					}else{
						afterAmount = tr.find(".paymentAmount").val()*tr.find(".rate").val();
					}
					tr.find(".afterAmount").val(toDecimal(afterAmount));
					tr.find("input:checkbox").attr("checked",'checked');
					var totalAmount=0;
					var tbody =tr.parent();
					
					tbody.find("input:checkbox:checked").each(function(){
						totalAmount=totalAmount+parseFloat($(this).parent().parent().find(".afterAmount").val());
					})
					$(".totalAmount").val(toDecimal(totalAmount));
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
					$("#btnNoSureSubmit").attr("disabled","disabled");
					$("#btnSureSubmit").attr("disabled","disabled");
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
		<li><a href="${ctx}/psi/lcPsiTransportPayment/">(理诚)运单付款列表</a></li>
		<li class="active"><a href="#">(理诚)新建运单付款</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="lcPsiTransportPayment" action="${ctx}/psi/lcPsiTransportPayment/addSave" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id"/>
		<input type='hidden'  name="paymentSta"  id="paymentSta" value="0"/>
 		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:30%;height:25px">
				<label class="control-label" style="width:80px" ><b>承运商</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					<select style="width:95%" id="supplier" name="supplier.id">
						<c:forEach items="${suppliers}" var="supplier" varStatus="i">
							 <option value='${supplier.id}' ${lcPsiTransportPayment.supplier.id eq supplier.id ?'selected':''}>${supplier.nikename}</option>;
						</c:forEach>
					</select>
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:70%;height:25px" >
					<label class="control-label"><b>承运商账号</b>:</label>
					<div class="controls" >
					<span>
						<select style="width:100%;" id="accountMaps" name="accountType">
							<c:forEach items="${accountMaps}" var="account" varStatus="i">
								 <option value='${account.key}' ${lcPsiTransportPayment.accountType eq account.key ?'selected':''}>${account.value}</option>;
							</c:forEach>
						</select>
					</span>
					</div>
				</div>
		</div>
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed" >
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
			<c:forEach items="${tranMap.value}" var="payEntry" varStatus="i">
			<tr>
				<c:choose>
					<c:when  test="${i.index==0}">
					<td style="text-align: center;vertical-align: middle;" rowspan="${fn:length(tranMap.value)}"><input style="width:90%" type="text" name="transportNo" readonly="readonly" value="${payEntry[1]}" /></td>
						<td><input style="width:90%" type="text" name=paymentType readonly="readonly" value="${payEntry[2]}" /></td>
					</c:when>
					<c:otherwise>
						<td><input type="hidden" name="transportNo" value="${payEntry[1]}"/><input style="width:90%" type="text" name=paymentType readonly="readonly" value="${payEntry[2]}" /></td>
					</c:otherwise>
				</c:choose>

				<td><input name="tranOrderId" type="hidden" value="${payEntry[0]}" /><input style="width:90%" type="text" name="paymentAmount" readonly="readonly" value="${payEntry[3]}" class="paymentAmount"/></td>
				<td><input style="width:90%" type="text" name="currency" readonly="readonly" value="${payEntry[4]}" /></td>
				<td><input style="width:90%" type="text" name="rate" class="rate"/></td>
				<td><input style="width:90%" type="text" name="afterAmount" readonly="readonly" class="afterAmount"/></td>
				<td><input style="width:90%" type="text" name="remark" value="${(payEntry[2] eq 'OtherAmount' || payEntry[2] eq 'OtherAmount1')?payEntry[6]:'' }"/></td>
				<td><input style="width:90%" name="isChecked" type="checkbox" class="isPayment" /></td>
			</tr>
			</c:forEach>
			
		</c:forEach>
		</tbody>
		</table>
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 14px">付款信息</p>
		</blockquote>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px" ><b>总额:</b></label>
				<div class="controls" style="margin-left:100px" >
				<input class="totalAmount" type="text" name="beforeAmount" readonly="readonly"  style="width:100%" />
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
					<input name="paymentAmount" type="text"  class="required" id="paymentAmount"  style="width:100%" />
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:80px" ><b>货币类型:</b></label>
				<div class="controls" style="margin-left:100px" >
				<select name="currency" style="width:95%" class="required">
				       <option value="" ></option>
					<c:forEach items="${currencys}" var="currency">
						<option value="${currency}" ${psiTransportOrder.currency eq currency ?'selected':''}>${currency}</option>
					</c:forEach>
				</select>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:98%;">
				<label class="control-label" style="width:80px">备注:</label>
				<div class="controls" style="margin-left:100px">
					<textarea name="remark"  style="width:100%;height:80px;" ></textarea>
				</div>
			</div>
		</div>
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 14px">查看供应商费用明细</p>
		</blockquote>
		
		<div class="control-group" style="float:left;width:98%;">
			<div class="controls">
			<c:forEach items="${unPaymentTransMap}" var="tranMap">
				<c:forEach items="${tranMap.value}" var="payEntry" varStatus="i">
					<c:if test="${not empty payEntry[5]}">
						<c:forEach items="${fn:split(payEntry[5],',')}" var="attchment" varStatus="j">
							<a target="_blank" href="<c:url value='/data/site/psi/lcPsiTransport/${attchment}'/>">${payEntry[1]}_${payEntry[2]}${j.index+1}</a>
							&nbsp;&nbsp;&nbsp;  
						</c:forEach>
					</c:if>
				</c:forEach>
			</c:forEach>
			</div>
		</div>
		
		
		<div class="form-actions" style="float:left;width:98%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="草 稿"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnNoSureSubmit" class="btn btn-primary" type="button" value="保存"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSureSubmit" class="btn btn-primary" type="button" value="申请" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
