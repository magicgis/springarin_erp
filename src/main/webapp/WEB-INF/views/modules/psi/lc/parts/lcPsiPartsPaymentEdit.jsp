<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购付款管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
	
		$(document).ready(function() {
			
			eval('var orderMap=${orderMaps}');
			eval('var ladingMap=${ladingMaps}');
			eval('var orderKeys =${orderKeys}');    
			eval('var ladingKeys =${ladingKeys}');
			var ordersMap = {};
			var ladingsMap = {};
			//编辑进来渲染需付款金额项
			$("#contentTable  tr").each(function(i,j){
				var paymentType=$(this).find("select[name='paymentType']").val();
				var billNo=$(this).find("select[name='billNo']").val();
				if("0"==paymentType){
					ordersMap[billNo]=1;
				}else if("1"==paymentType){
					ladingsMap[billNo]=1;
				}
				if(paymentType&&paymentType!=null&&billNo!=null){
					if(paymentType=="0"){
						var order=orderMap[billNo];
						if(order){
							$(this).find(".needPaymentAmount").val(order.totalAmount);
							$(this).find("input[name='unknowId']").val(order.id);
							hrefStr="${ctx}/psi/lcPsiPartsOrder/view?id="+order.id;
						};
						
					}else if(paymentType=="1"){
						var lading=ladingMap[billNo];
						if(lading){
							$(this).find(".needPaymentAmount").val(lading.totalAmount);
							$(this).find("input[name='unknowId']").val(lading.id);
							hrefStr="${ctx}/psi/lcPsiPartsDelivery/view?id="+lading.id;
						};
					};
				}
			});
			
			
			$("#contentTable  tr").each(function(i,j){
				var paymentType=$(this).find("select[name='paymentType']").val();
				var billNo=$(this).find("select[name='billNo']").val();
				if("0"==paymentType){
					for (var key in ordersMap) {
						if(billNo!=key){
							$(this).find("select.billNo").find("option[value='"+key+"']").remove();
						}
					}
				}else if("1"==paymentType){
					for (var key in ladingsMap) {
						if(billNo!=key){
							$(this).find("select.billNo").find("option[value='"+key+"']").remove();
						}
					}
				}
			});
			
			
			
			$("#inputForm").on("change",".paymentType",function(){
				var tr =$(this).parent().parent();
				if($(this).val()){
					var list = [];
					if($(this).val()=="0"){
						list = orderKeys;
					}else if($(this).val()=="1"){
						list = ladingKeys;
					}
					tr.find("select.billNo").empty();
					tr.find("select.billNo").select2("val","");
			        var k = 0 ;
					$(list).each(function(i,data){
						if($("select.billNo[value="+data+"]").size()==0){
							if(k==0){
								$("select.billNo").each(function(){
		            				$(this).find("option[value="+data+"]").remove();
		            			});
								k=1;													
							}
							tr.find("select.billNo").select2().append("<option value='"+data+"' >"+data+"</option>");
						}
					});
				}
				tr.find("select.billNo").change();   //选了类型，默认调下change方法
			});
			
			
			$("#inputForm").on("change",".billNo",function(e){
				var billNo = e.val;
				var tr =$(this).parent().parent().parent();
				var payType =tr.find(".paymentType").children('option:selected').val();
				if(e.removed){
					var removeVal = e.removed.id;
					$("select.billNo").each(function(){
	    				if($(this).select2("val")!=billNo){
	    					if(payType==$(this).parent().parent().parent().find("select.paymentType").select2("val")){
	    						$(this).find("option[value='"+billNo+"']").remove();    					
		    					$(this).append("<option value='"+removeVal+"'>"+removeVal+"</option>");
							}
	    				}
	    			});
				}
				var billNo=$(this).val();
				if(payType!=""){
					var hrefStr="";
					var firstStyle="";
					if(payType=="0"){
						var order=orderMap[billNo];
						if(order){
							tr.find(".needPaymentAmount").val(order.totalAmount);
							tr.find("input[name='unknowId']").val(order.id);
							tr.find("input[name='paymentAmount']").val(order.totalAmount);
							tr.find("input[name='paymentAmount']").attr("readonly","readonly");
							tr.find(".paymentRatio").attr("readonly","readonly");
							tr.find(".itemCurrencyType").val(order.currencyType);
							hrefStr="${ctx}/psi/purchaseOrder/view?id="+order.id;
						};
					}else if(payType=="1"){
						var lading=ladingMap[billNo];
						if(lading){
							tr.find(".needPaymentAmount").val(lading.totalAmount);
							tr.find("input[name='unknowId']").val(lading.id);
							tr.find("input[name='paymentAmount']").val(lading.totalAmount);
							tr.find("input[name='paymentAmount']").removeAttr("readonly");
							tr.find(".paymentRatio").removeAttr("readonly");
							tr.find(".itemCurrencyType").val(lading.currencyType);
							hrefStr="${ctx}/psi/psiLadingBill/view?id="+lading.id;
							if(lading.totalPaymentAmount==0){
								firstStyle="color:red;font-size:20";
							}
						};
					}
					tr.find(".updateTips").html("<a target=\"_blank\" style=\""+firstStyle+"\" href=\""+hrefStr+"\">查看</a>");
				}
			});
			
			
			$("#add-row").on('click',function(e){
				e.preventDefault();
			    var tbody = $("#contentTable tbody");
			    var tr =$("<tr></tr>");
	            tr.append("<td> <select style='width: 90%'  class='paymentType' name='paymentType' ><option value='1' >尾款</option><option value='0' >定金</option></select> </td>");
	            tr.append("<td> <span id='masters'><input  type='hidden' name='unknowId'/><select name='billNo' style='width:100%' class='billNo' ></select></span><span class='updateTips' style='float:right;padding-right:2px'></span></td>");
	            tr.append("<td> <div class='input-prepend input-append'><input  type='text' class='number paymentRatio' style='width:40%;'  value='100' /><span class='add-on'>%</span></div></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' class='needPaymentAmount' readonly='readonly' /></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' name='paymentAmount' /></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' class='itemCurrencyType' readonly /></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' name='remark' /></td>");
	            tr.append("<td style='text-align:center'> <a href='' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除付款项</a></td>");
	            tr.find(".paymentType").select2();
				tr.find(".billNo").select2();  
	            tbody.append(tr);
	            tr.find(".paymentType").change();
			});
			
			
			$("#contentTable").on('blur','.paymentRatio',function(e){
				var tr=$(this).parent().parent().parent();
				var ratio =$(this).val();
				var amount = tr.find(".needPaymentAmount").val();
				tr.find("input[name='paymentAmount']").val(amount*ratio/100);
				e.preventDefault();
			});
			
			
			$('#contentTable').on('click', '.remove-row', function(e){
				 e.preventDefault();
				  if($('#contentTable tbody tr').size()>1){
					  var tr =$(this).parent().parent();
						var id=tr.find("select.partsOrderId").select2("val");
						tr.remove();
						if(id){
							$("select.partsOrderId").each(function(){
								$(this).append("<option value='"+id+"'>"+id+"</option>");
							});
						}
				  }
			});
			
			
			
			$("#btnSureSubmit").on('click',function(e){
				 if($("#inputForm").valid()){
					 top.$.jBox.confirm('确认要提出付款申请？申请后付款将不允许改动！','系统提示',function(v,h,f){
							if(v=='ok'){
								$("#paymentSta").val("1");
								$("#inputForm").submit();
								$("#btnSureSubmit").attr("disabled","disabled");
							}
							return true;
							},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				 }else{
					 return false;
				 };
				
			});	
			
			
			$("#inputForm").validate({
				rules:{
					"paymentAmount":{
						"required":true
					}
				},
				messages:{
					"paymentAmount":{"required":'付款金额不能为空'}
				},
				
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					var numberflag  =1;
					var billNoStr="";
					var twoStr="";
					var flag = 1;
					$("select.billNo").each(function(i){
						if(flag==1){
							if(billNoStr){
								if(billNoStr.indexOf($(this).val()+",")>=0){
									two=$(this).val();
									flag = 2;
								}else{
									billNoStr=billNoStr+$(this).val()+",";
								}
							}else{
								billNoStr = $(this).val()+",";
							}						
						}
					});
					
					if(flag==2){
						top.$.jBox.tip("有相同的提单或订单号！"+twoStr, 'info',{timeout:3000});
						return false;
					};
					
					
					$("#contentTable tbody tr").each(function(i,j){
						if(parseInt($(this).find(".needPaymentAmount").val())-parseInt($(this).find("input[name='paymentAmount']").val())<0){
							numberflag = 2;
							return;
						}
					});
					
					if(numberflag==2){
						top.$.jBox.tip("支付金额不能大于可支付金额！","info",{timeout:3000});
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/lcPsiPartsPayment/">配件订单付款列表</a></li>
		<li class="active"><a href="#">配件订单付款编辑</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiPartsPayment" action="${ctx}/psi/lcPsiPartsPayment/editSave" method="post" class="form-horizontal">
		<input type='hidden' name="id"               value="${psiPartsPayment.id}" />
		<input type='hidden' name="createDate"       value="${psiPartsPayment.createDate}" />
		<input type='hidden' name="createUser.id"    value="${psiPartsPayment.createUser.id}" />
		<input type='hidden' name="paymentSta" 		 value="${psiPartsPayment.paymentSta}" id="paymentSta"/>
		<input type='hidden' name="attchmentPath" 	 value="${psiPartsPayment.attchmentPath}" />
		<input type='hidden' name="paymentNo" 	     value="${psiPartsPayment.paymentNo}" />
		<input type='hidden' name="oldItemIds" 	     value="${psiPartsPayment.oldItemIds}" />
		<input type='hidden' name="currencyType"     value="${psiPartsPayment.currencyType}"/>
		   
		
		 <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
			<div style="float:left;width:100%;">
				<div class="control-group" style="float:left;width:30%">
					<label class="control-label" style="width:100px"><b>供应商</b>:</label>
					<div class="controls" style="margin-left:120px">
					<span>
						<input type='text'  name="supplier.name" readonly value="${psiPartsPayment.supplier.nikename}"/>
						<input type='hidden' name="supplier.id" value="${psiPartsPayment.supplier.id }"/>
					</span>
					</div>
				</div>
				<div class="control-group"  style="float:left;width:70%" >
					<label class="control-label"  style="width:100px"><b>供应商账号</b>:</label>
					<div class="controls" style="margin-left:120px">
					<span>
						<select style="width:98%;" id="accountMaps" name="accountType" class="required">
							<c:forEach items="${accountMaps}" var="account" varStatus="i">
								 <option value='${account.key}' ${psiPartsPayment.accountType eq account.key ?'selected':''}>${account.value}</option>;
							</c:forEach>
						</select>
					</span>
					
					</div>
				</div>
		</div>
		<div class="control-group"  style="float:left;width:100%">
			<label class="control-label" style="width:100px"><b>备注</b>:</label>
			<div class="controls" style="margin-left:120px">
				<textarea  maxlength="255" style="height:50px;width:98%" name="remark"  ></textarea>
			</div>
		</div>
				
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加付款项</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 8%">付款类型</th>
				   <th style="width: 20%">单号</th>
				   <th style="width: 8%">未付金额比例</th>
				   <th style="width: 8%">未付款金额</th>
				   <th style="width: 8%">本次付款金额</th>
				   <th style="width: 8%">币种</th>
				   <th style="width: 15%">备注</th>
				   <th style="width: 15%">操作</th>
				   
			</tr>
		</thead>
		<tbody>
			<c:if test="${not empty psiPartsPayment.items}" >
			<c:forEach items="${psiPartsPayment.items}" var="item" >
				<tr>
					<td>
					<input type="hidden" name="id" value="${item.id}" />
					<input type="hidden" name="unknowId" />
					<select style="width: 90%"  class='paymentType' name="paymentType" >
						<option value="1" ${item.paymentType eq '1' ?'selected':'' }>尾款</option>
						<option value="0" ${item.paymentType eq '0' ?'selected':'' }>定金</option>
					</select>
					</td>
					<td >
					<span id="masters">
						<c:if test="${item.paymentType eq '0' }">
							<select name="billNo" style="width:80%" class="billNo" >
								<c:forEach items="${orderSet}" var="order" varStatus="i">
										 <option value="${order}" ${order eq item.billNo ?'selected':''}>${order}</option>;
								</c:forEach>
							</select>
						</c:if>
						<c:if test="${item.paymentType eq '1' }">
							<select name="billNo" style="width:80%" class="billNo" >
								<c:forEach items="${ladingSet}" var="lading" varStatus="i">
										 <option value="${lading}" ${lading eq item.billNo ? 'selected':'' }>${lading}</option>;
								</c:forEach>
							</select>
						</c:if>
					</span>
					<span class='updateTips' style='float:right;padding-right:2px;'><a target="_blank" href="${ctx}/psi/${item.paymentType eq '0'?'psiPartsOrder':'psiPartsDelivery' }/view?id=${item.paymentType eq '0'?item.order.id:item.ladingBill.id}">查看</a></span>
					</td>
					<td>
					<div class="input-prepend input-append"><input  type="text" class="number paymentRatio" style="width:40%;"  value="100" /><span class="add-on">%</span></div>
					</td>
					<td ><input type="text" class="needPaymentAmount"  readonly="readonly" style="width: 90%"/></td>
					<td ><input type="text" name="paymentAmount"  value="${item.paymentAmount}" style="width: 90%"/></td>
					<td ><input type="text" class="itemCurrencyType"  value="${psiPartsPayment.currencyType }" readonly="readonly" style="width: 90%"/></td>
				    <td ><input type="text" name="remark"  style="width: 90%" value="${item.remark}"/></td>
					<td style="text-align:center"><a href="#"  class="remove-row"><span class="icon-minus"></span>删除付款项</a></td> 
				</tr>
			</c:forEach>
			</c:if>
		</tbody>
	</table>
		
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSureSubmit" class="btn btn-primary" type="button" value="申请" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
