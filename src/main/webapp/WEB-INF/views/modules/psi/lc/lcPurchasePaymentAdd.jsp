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
			eval('var testAmountMap=${testAmountMap}');
			new tabTableInput("inputForm","text");
			$("#supplier").on("change",function(e){
				var params = {};
				params['supplier.id'] = $(this).val();
				window.location.href = "${ctx}/psi/lcPurchasePayment/add?"+$.param(params);
			});
			
			$("#btnExp").click(function(){
				window.location.href = "${ctx}/psi/lcPurchasePayment/expPayment?supplierId="+$("#supplier").val();
				
			});
		

			$("#add-row").on('click',function(e){
				e.preventDefault();
			    var table = $('#contentTable');
	            var tr = $("<tr></tr>");
	            tr.append("<td> <select style='width: 90%'  class='paymentType' name='paymentType' ><option value='1' >尾款</option><option value='0' >定金</option></select></td>");
	            tr.append("<td> <span id='masters'><input  type='hidden' name='unknowId'/><select name='billNo' style='width:100%' class='billNo' ></select></span></td>");
	            tr.append("<td class='updateTips'></td>");
	            tr.append("<td> <div class='input-prepend input-append'><input  type='text' class='number paymentRatio' style='width:40%;'  value='100' /><span class='add-on'>%</span></div></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' class='needPaymentAmount' readonly='readonly' /></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' name='paymentAmount' /></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' class='itemCurrencyType' readonly /></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' name='remark' /></td>");
	            tr.append("<td style='text-align:center'> <a href='' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除付款项</a></td>");
	            tr.find(".paymentType").select2();
				tr.find(".billNo").select2();  
	            table.append(tr);
	            tr.find("select.paymentType").change();
			});
			
			$("#adjust-search").on('click',function(e){
				e.preventDefault();
				var typeAndIds="";
				$("#contentTable tbody tr ").each(function(i){
					var type =$(this).find("select.paymentType").val();
					var id =$(this).find("input[name='unknowId']").val();
					typeAndIds = type+":"+id+",";
				}); 
				
				//ajax查询额外订单项
				$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/psi/lcPurchasePayment/ajaxOrderAjust' ,
				    data: {
				    	"typeAndIds":typeAndIds
				    },
				    dataType: 'json',
				    success:function(data){
				    	if(data.items.length>0){
				    		createAdjustData(data.items);
				    	}
				    		
			        }
				});
			});
			
			
			
			
			$("#contentTable").on("blur","input[name='paymentAmount']",function(e){
				countAmount();
			});
			
			$("select.paymentType").change();
			
			$("#selectBillNo").change(function(){
				var billNoStr=$(this).val();
				var rateStr=$("#selectRate").val();
				var monthStr=$("#selectMonth").val();
				$("#contentTable tbody tr").find(".isPayment").each(function(){
					if(!monthStr&&!rateStr&&!billNoStr){
						$(this).removeAttr("checked");
					}else{
						if((!monthStr||monthStr.indexOf($(this).parent().parent().find(".month").val())>=0)
								&&(!rateStr||rateStr.indexOf($(this).parent().parent().find(".rate").val())>=0)
								&&(!billNoStr||billNoStr.indexOf($(this).parent().parent().find(".billNo").val())>=0)){
							$(this).attr("checked","checked");
						}else{
							$(this).removeAttr("checked");
						}
					}
				});
				countAmount();
			});
			
			$("#selectMonth").change(function(){
				var monthStr=$(this).val();
				var rateStr=$("#selectRate").val();
				var billNoStr=$("#selectBillNo").val();
				$("#contentTable tbody tr").find(".isPayment").each(function(){
					if(!monthStr&&!rateStr&&!billNoStr){
						$(this).removeAttr("checked");
					}else{
						if((!monthStr||monthStr.indexOf($(this).parent().parent().find(".month").val())>=0)
								&&(!rateStr||rateStr.indexOf($(this).parent().parent().find(".rate").val())>=0)
								&&(!billNoStr||billNoStr.indexOf($(this).parent().parent().find(".billNo").val())>=0)){
							$(this).attr("checked","checked");
						}else{
							$(this).removeAttr("checked");
						}
					}
				});
				countAmount();
			});
			
			
			$("#selectRate").change(function(){
				var rateStr=$(this).val();
				var monthStr=$("#selectMonth").val();
				var billNoStr=$("#selectBillNo").val();
				$("#contentTable tbody tr").find(".isPayment").each(function(){
					if(!monthStr&&!rateStr&&!billNoStr){
						$(this).removeAttr("checked");
					}else{
						if((!monthStr||monthStr.indexOf($(this).parent().parent().find(".month").val())>=0)
								&&(!rateStr||rateStr.indexOf($(this).parent().parent().find(".rate").val())>=0)
								&&(!billNoStr||billNoStr.indexOf($(this).parent().parent().find(".billNo").val())>=0)){
							$(this).attr("checked","checked");
						}else{
							$(this).removeAttr("checked");
						}
					}
				});
				countAmount();
			});
			
			$(".testAmount").on("blur",function() { 
				if($(this).val()){
					checkedAmount($(this).val());
				}
			}); 
			
			$("#isPaymentAll").click(function(e){
				var checkedStatus = this.checked;
				$("*.isPayment:checkbox").each(function(){
					this.checked = checkedStatus;
				});
				countAmount();
			});
			
			$("#adjustTable .isPayment").click(function() { 
				countAmount();
			}); 
			
			$("#contentTable .isPayment").click(function() { 
				countAmount();
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
					
					var currencyStr="";
					var currencyFlag=1;
					var adjustFlag=1;
					var noCheck=1;
					var numberflag  =1;
					
					var overTest=0;
					var curTestMap ={};
					for(var key in testAmountMap){
						curTestMap[key]=testAmountMap[key];
					}
					
					var conKey="";
					$("#contentTable").find(".isPayment").each(function(i){
						var tr =$(this).parent().parent();
						if(this.checked){
							if(currencyStr&&currencyStr.indexOf(tr.find(".itemCurrencyType").val())<0){
								currencyFlag=2;
								return false;
							}else{
								currencyStr  = tr.find(".itemCurrencyType").val();
							}	
							
							if(noCheck==1){
								noCheck=0;
							}
							if(parseInt(tr.find(".needPaymentAmount").val())-parseInt(tr.find("input[name='paymentAmount']").val())<0){
								numberflag = 2;
								return false;
							}
							var billSta = tr.find(".billSta").val();
							//对质检数据进行校验   
							conKey=tr.find(".conKey").val();
							if(conKey&&billSta!="1"){//过滤掉定金和已完成收货提单
								if(curTestMap[conKey]&&curTestMap[conKey]!='undefined'){
									var curPayAmount=tr.find("input[name='paymentAmount']").val();
									var reduce = parseFloat(parseFloat(curTestMap[conKey])-parseFloat(curPayAmount)).toFixed(2);
									curTestMap[conKey]=reduce;
									if(parseFloat(reduce)<0){
										overTest=1;
										return false;
									}
								}else{
									overTest=1;
									return false;
								}
							}
						}
					});
					
				
					if(overTest==1){
						top.$.jBox.tip(conKey+"可付金额为"+(testAmountMap[conKey]==null?"0":testAmountMap[conKey])+",<br/>如需扩大可付金额，请尽快完成该产品的质检流程！","info",{timeout:3000});
						return false;
					}
					
				
					
					if(currencyFlag==2){
						top.$.jBox.tip("尾款或订单必需有统一的货币类型", 'info',{timeout:3000});
						return false;
					};
					
					
					if(numberflag==2){
						top.$.jBox.tip("支付金额不能大于可支付金额！","info",{timeout:3000});
						return false;
					}
					
					if(noCheck==1){
						top.$.jBox.tip("必须选中一条尾款或定金进行付款", 'info',{timeout:3000});
						return false;
					};
					
					
					$("#currencyType").val(currencyStr);
					
					$("#adjustTable tbody tr").find(".isPayment").each(function(){
						if(this.checked){
							var adjustCurrency = $(this).parent().parent().find(".currency").val();
							if(adjustCurrency!=currencyStr){
								adjustFlag=2;
								return false;
							};
						}
					});
					
					if(adjustFlag==2){
						top.$.jBox.tip("额外支付金额的货币类型，和其他付款项货的币类型不同！","info",{timeout:3000});
						return false;
					}
					     
					$("#contentTable tbody tr").find(".isPayment").each(function(){
						if(!this.checked){
							var tr=$(this).parent().parent();
							tr.remove();
						}
					});
					
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
					
					$("#adjustTable tbody tr").find(".isPayment").each(function(){
						if(!this.checked){
							var tr=$(this).parent().parent();
							tr.remove();
						}
					});
					
					$("#adjustTable tbody tr").each(function(i,j){
						$(j).find("select").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","adjusts"+"["+i+"]."+$(this).attr("name"));
							}
						});
						$(j).find("input[type!='']").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","adjusts"+"["+i+"]."+$(this).attr("name"));
							}
						});
					});
					
				  $(".btn-primary").attr("disabled","disabled");
				  $("#paymentSta").val("1");
				  form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
					
				}
			});
		});
		
		
		
		function checkedAmount(total){
			$("#adjustTable tbody tr").find(".isPayment").each(function(){
				var adjustAmount=$(this).parent().parent().find("input[name='adjustAmount']").val();
				if(parseFloat(adjustAmount)>0){
					total=parseFloat(total)+parseFloat(adjustAmount);
				}else{
					total=parseFloat(total)-parseFloat(adjustAmount);
				}
				if(total>0){
					$(this).attr("checked","checked");
				}else{
					$(this).removeAttr("checked");
				}
			});
			
			$("#contentTable tbody tr").find("input[name='paymentAmount']").each(function(){
				var tr =$(this).parent().parent();
				total=parseFloat(total)-parseFloat($(this).val());
				if(total>0){
					tr.find(".isPayment").attr("checked","checked");
				}else{
					tr.find(".isPayment").removeAttr("checked");
				}
			});
			
			countAmount();
		}
		
		
		function countAmount(){
			var total=0;
			$("#contentTable tbody tr").find("input[name='paymentAmount']").each(function(){
				var tr=$(this).parent().parent();
				if(tr.find(".isPayment").attr("checked")=="checked"){
					total=parseFloat(total)+parseFloat($(this).val());
				}
			});
			
			if($("#adjustTable tbody tr")){
				$("#adjustTable tbody tr").find(".isPayment").each(function(){
					if(this.checked){
						var adjustAmount=$(this).parent().parent().find("input[name='adjustAmount']").val();
						total=parseFloat(total)+parseFloat(adjustAmount);
					}
				});
			}
			
			$(".totalAmount").text(total.toFixed(2));
		}
		
		function createAdjustData(items){
			$("#adjustDiv").css("display","block");
			var tbody = $("#adjustTable tbody");
			
			tbody.find("tr").each(function(){
				$(this).remove();
			});
			
			for(var x in items){
				var item = items[x];
				var tr= $("<tr></tr>");
				tr.append("<td><input type='checkBox' class='isPayment'/><input type='hidden' name='id' value="+item.id+"></td>");
				tr.append("<td><input type='text' style='width:90%' readonly  value='"+item.orderNo+"'></td>");
				tr.append("<td><input type='text' style='width:90%' readonly name='adjustAmount' value='"+item.amount+"'></td>");
				tr.append("<td><input type='text' style='width:90%' readonly  value='"+item.remark+"'></td>");
				tbody.append(tr);
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/lcPurchasePayment">(理诚)采购付款列表</a></li>
		<li class="active"><a href="#">(理诚)新建采购付款</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/moneyView">(理诚)采购订单资金列表</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="purchasePayment" action="${ctx}/psi/lcPurchasePayment/addSave" method="post" class="form-horizontal">
		<input type='hidden' id="paymentSta" name="paymentSta" value="0"/>
		<input type='hidden' id="currencyType"  name="currencyType"  />
		 <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div class="control-group" style="float:left;width:98%">
			<label class="control-label" style="width:100px"><b>供应商</b>:</label>
			<div class="controls" style="margin-left:120px">
			<span>
				<select id="supplier" name="supplier.id" style="width:98%">
					<c:forEach items="${suppliers}" var="supplier" varStatus="i">
						 <option value='${supplier.id}' ${purchasePayment.supplier.id eq supplier.id ?'selected':''}>${supplier.nikename} &nbsp;&nbsp;&nbsp;&nbsp;${supplier.payMark}
					</c:forEach>
				</select>
			</span>
			</div>
		</div>
		<div class="control-group"  style="float:left;width:98%" >
			<label class="control-label"  style="width:100px"><b>供应商账号</b>:</label>
			<div class="controls" style="margin-left:120px">
			<span>
				<select style="width:98%;" id="accountMaps" name="accountType" class="required">
					<c:forEach items="${accountMaps}" var="account" varStatus="i">
						 <option value='${account.key}' ${account.key eq '0' ?'selected':''}>${account.value}</option>;
					</c:forEach>
				</select>
			</span>
			
			</div>
		</div>
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label" style="width:100px"><b>备注</b>:</label>
			<div class="controls" style="height:40px;margin-left:120px">
				<textarea  maxlength="255" style="width:98%" name="remark"  ></textarea>
			</div>
		</div>
		
		 <blockquote style="float:left;width:98%">
			<p style="font-size: 14px">快速选择付款项</p>
		</blockquote>
		<div class="control-group" style="float:left;width:98%">
				 &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;<span>
					<b>单号</b>:
					 <select id="selectBillNo" multiple="multiple" style="width:400px">
						<c:forEach items="${billNos}" var="billNo">
							<option value="${billNo}">${billNo}</option>
						</c:forEach>
					</select>
					
					<b>尾款分档</b>:
					 <select id="selectRate" multiple="multiple" style="width:150px">
						<c:forEach items="${rates}" var="rate">
							<option value="${rate}">${rate}</option>
						</c:forEach>
					</select>
					<b>月份</b>:
					 <select id="selectMonth" multiple="multiple">
						<c:forEach items="${months}" var="month">
							<option value="${month}">${month}</option>
						</c:forEach>
					</select>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				   <b>金额</b>: <input type='text' class="testAmount" style="width:80px"/>
				</span>
			</div>
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
	   <div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;">
	  		  &nbsp;&nbsp;&nbsp;&nbsp; <b>实付总额 :</b><span class="totalAmount" style="color:green;font-weight: bold;"></span>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input class="btn btn-primary" type="submit" value="申请" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnExp" class="btn" type="button" value="导出" />
	   </div>
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				   <th style="width: 5%"><input type="checkbox" id="isPaymentAll" /></th>
				   <th style="width: 5%">付款类型</th>
				   <th style="width: 15%">单号</th>
				   <th style="width: 20%">提示信息</th>
				   <th style="width: 10%">未付款金额</th>
				   <th style="width: 10%">本次付款金额</th>
				   <th style="width: 5%">币种</th>
				   <th style="width: 10%">备注</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${orders}" var="order">
					<tr>
						<td><input type="checkbox" class="isPayment" /></td>
						<td><input type="hidden" name="paymentType"  value="0"/>定金</td>
						<td >
							<input  type="hidden" class="month" value="${fn:substring(order[0],0,6)}"/>
							<input  type="hidden" name="order.id" value="${order[1]}">
							<input type="hidden" name="billNo" value="${order[0]}" class="billNo"/>						
						<a target="_blank" href="${ctx}/psi/lcPurchaseOrder/view?id=${order[1]}">${order[0]}</a></td>
						<td class="updateTips">订单总额${order[5]}&nbsp;&nbsp;定金比例${order[3]}%</td>
						<td ><input type="text" class="needPaymentAmount"  readonly="readonly" style="width: 90%" value="<fmt:formatNumber value='${order[2]}' pattern='0.##'/>"  /></td>
						<td ><input type="text" name="paymentAmount"  style="width: 90%" value="<fmt:formatNumber value='${order[2]}' pattern='0.##'/>"  /> </td>
						<td ><input type="text" class="itemCurrencyType"  readonly="readonly" value="${order[4]}" style="width: 80%"/></td>
					    <td ><input type="text" name="remark" style="width: 90%"/></td>
					</tr>
				</c:forEach>
				<c:forEach items="${ladingItemDtos}" var="dto">
					<tr>
						<td><input type="checkbox" class="isPayment" /></td>
						<td><input type="hidden" name="paymentType"  value="1"/>尾款</td>
						<td >
							<input  type="hidden" class="month" value="${fn:substring(dto.billNo,0,6)}"/>
							<input  type="hidden" class="rate" value="${dto.rate}"/>
							<input type="hidden" name="ladingBill.id" value="${dto.ladingBillId}"/>
							<input type="hidden" name="ladingBillItem.id" value="${dto.ladingBillItemId}"/>
							<input type="hidden" name="billNo" value="${dto.billNo}" class="billNo"/>
							<input type="hidden" class="conKey" value="${dto.conKey}" />
							<input type="hidden" class="billSta" value="${dto.ladingSta}" />
						<a target="_blank" href="${ctx}/psi/lcPsiLadingBill/view?id=${dto.ladingBillId}">${dto.billNo}</a></td>
						<td class="updateTips">逾期${dto.delayDays}天${dto.rate}%档${fn:toUpperCase(dto.country eq 'com' ?'us':dto.country)}平台&nbsp;&nbsp;${dto.quantity}个&nbsp;&nbsp;单价  ${dto.itemPrice}&nbsp;&nbsp;定金比例 ${dto.deposit}%&nbsp;&nbsp;${dto.productName}&nbsp;&nbsp;已付${dto.totalPaymentAmount}</td>
						<td ><input type="text" class="needPaymentAmount"  readonly="readonly" style="width: 90%" value="${dto.needPay}"/></td>
						<td ><input type="text" name="paymentAmount"  style="width: 90%" value="${dto.needPay}"  /> </td>
						<td ><input type="text" class="itemCurrencyType"  readonly="readonly" value="${dto.currency}" style="width: 80%"/></td>
					    <td ><input type="text" name="remark" style="width: 90%"/></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<c:if test="${not empty adjusts}">
			<div style="float: left"><blockquote><p style="font-size: 14px">额外付款项信息</p></blockquote></div>
				<table id="adjustTable" class="table table-striped table-bordered table-condensed" >
					<thead>
						<tr>
						   <th style="width: 10%">选项</th>
						   <th style="width: 20%">调整原由</th>
						   <th style="width: 10%">金额</th>
						   <th style="width: 10%">货币类型</th>
						   <th style="width: 40%">备注</th>
						</tr>
					</thead>
					<tbody>	
					<c:forEach items="${adjusts}" var="adjust">
						<tr>
							<td><input type="checkbox" class="isPayment" /><input type="hidden" name="id" value="${adjust.id}"/></td>
							<td><input type="text" readonly="readonly" name="subject" style="width:90%" value="${adjust.subject}" /></td>
							<td><input type="text" readonly="readonly" name="adjustAmount" style="width:90%" value="${adjust.adjustAmount}" /></td>
							<td><input type="text" readonly="readonly" style="width:90%" class="currency" value="${adjust.currency}" /></td>
							<td><input type="text" readonly="readonly" name="remark" style="width:90%" value="${adjust.remark}" /></td>
						</tr>
					</c:forEach>
					
					</tbody>
				</table>
		</c:if>
		<div class="form-actions">
			<input class="btn btn-primary" type="submit" value="申请" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
