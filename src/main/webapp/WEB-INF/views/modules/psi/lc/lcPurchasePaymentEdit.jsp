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
			new tabTableInput("inputForm","text");
			eval('var orderMaps=${orderMaps}');
			eval('var ladingMaps=${ladingMaps}');
			eval('var orderKeys = ${orderKeys}');
			eval('var ladingKeys =${ladingKeys}');
			
			var ordersMap = {};
			var ladingMap = {};
			//编辑进来渲染需付款金额项
			$("#contentTable  tr").each(function(i,j){
				var paymentType=$(this).find("select[name='paymentType']").val();
				var billNo=$(this).find("select[name='billNo']").val();
				if("0"==paymentType){
					ordersMap[billNo]=1;
				}else if("1"==paymentType){
					ladingMap[billNo]=1;
				}
				if(paymentType&&paymentType!=null&&billNo!=null){
					var hrefStr="";
					if(paymentType=="0"){
						var bbb=orderMaps[billNo];
						if(bbb&&bbb.length>0){
							$(this).find(".needPaymentAmount").val(bbb[2]);
							$(this).find("input[name='unknowId']").val(bbb[1]);
							hrefStr="${ctx}/psi/lcPurchaseOrder/view?id="+bbb[1];
						};
						
					}else if(paymentType=="1"){
						var bbb=ladingMaps[billNo];
						if(bbb&&bbb.length>0){
							$(this).find(".needPaymentAmount").val(bbb[2]);
							$(this).find("input[name='unknowId']").val(bbb[1]);
							hrefStr="${ctx}/psi/lcPsiLadingBill/view?id="+bbb[1];
						};
					};
					var params = {};
					params.type = paymentType;
					params.order = billNo;
					var tr = $(this);
					$.get("${ctx}/psi/lcPurchaseOrder/orderInfo?"+$.param(params),function(data){
						var dataStr= data.substring(0,8);
						tr.find(".updateTips").html("");
						tr.find(".updateTips").append("<a target=\"_blank\" rel=\"popover\" href=\""+hrefStr+"\" data-content=\""+data+"\">"+dataStr+"</a>");
						tr.find("a[rel='popover']").popover({trigger:'hover'});
					});
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
					for (var key in ladingMap) {
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
							tr.find("select.billNo").append("<option value='"+data+"' >"+data+"</option>");
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
				var aaa=[];
				if(payType!=""){
					if(payType=="0"){
						aaa=orderMaps[billNo];
						if(aaa&&aaa.length>0){
							tr.find(".needPaymentAmount").val(aaa[2]);
							tr.find("input[name='unknowId']").val(aaa[1]);
							tr.find("input[name='paymentAmount']").val(aaa[2]);
							tr.find("input[name='paymentAmount']").attr("readonly","readonly");
							tr.find(".paymentRatio").attr("readonly","readonly");
							tr.find(".itemCurrencyType").val(aaa[4]);
						};
						
					}else if(payType=="1"){
						aaa=ladingMaps[billNo];
						if(aaa&&aaa.length>0){
							tr.find(".needPaymentAmount").val(aaa[2]);
							tr.find("input[name='unknowId']").val(aaa[1]);
							tr.find("input[name='paymentAmount']").val(aaa[2]);
							tr.find("input[name='paymentAmount']").removeAttr("readonly");
							tr.find(".paymentRatio").removeAttr("readonly");
							tr.find(".itemCurrencyType").val(aaa[4]);
						};
					}
				}
				var params = {};
				params.type = payType;
				params.order = billNo;
				$.get("${ctx}/psi/lcPurchaseOrder/orderInfo?"+$.param(params),function(data){
					var dataStr= data.substring(0,8);
					tr.find(".updateTips").html("");
					tr.find(".updateTips").append("<a rel=\"popover\" data-content=\""+data+"\">"+dataStr+"</a>");
					tr.find("a[rel='popover']").popover({trigger:'hover'});
				});
				
					countAmount();
			});

			$("#add-row").on('click',function(e){
				e.preventDefault();
			    var table = $('#contentTable');
	            var tr = $("<tr></tr>");
	            tr.append("<td> <select style='width: 90%'  class='paymentType' name='paymentType' ><option value='1' >尾款</option><option value='0' >定金</option></select></td>");
	            tr.append("<td> <span id='masters'><input  type='hidden' name='unknowId'/><select name='billNo' style='width:100%' class='billNo' ></select></span></td>");
	            tr.append("<td class='updateTips'></td>");
	            tr.append("<td> <div class='input-prepend input-append'><input  type='text' class='number paymentRatio' style='width:40%;' value='100' /><span class='add-on'>%</span></div></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' class='needPaymentAmount' readonly='readonly' /></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' name='paymentAmount' /></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' class='itemCurrencyType' readonly/></td>");
	            tr.append("<td> <input  style='width: 90%' type='text' name='remark' /></td>");
	            tr.append("<td style='text-align:center'> <a href='' id='remove-row' class='remove-row'><span class='icon-minus'></span>删除付款项</a></td>");
	            tr.find(".paymentType").select2();
				tr.find(".billNo").select2();  
	            table.append(tr);
	            tr.find(".paymentType").change();
	        	 
			});
			
			$('#contentTable').on('click', '.remove-row', function(e){
				 e.preventDefault();
				  if($('#contentTable tbody tr').size()>1){
					  var row = $(this).parent().parent();
					  var id = row.find("select.billNo").select2("val");
					  var type = row.find("select.paymentType").select2("val");
					  row.remove();
					  if(id){
						  $("select.billNo").each(function(){
								if(type==$(this).parent().parent().parent().find("select.paymentType").select2("val")){
		          					$(this).append("<option value='"+id+"'>"+id+"</option>");
								}
		          		  });
					  }
				 }
				  countAmount();
			});
			
			
			$("#contentTable").on('blur','.paymentRatio',function(e){
				var tr=$(this).parent().parent().parent();
				var ratio =$(this).val();
				var amount = tr.find(".needPaymentAmount").val();
				tr.find("input[name='paymentAmount']").val(amount*ratio/100);
				e.preventDefault();
				countAmount();
			});
			
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
			
			$("#contentTable").on("blur","input[name='paymentAmount']",function(e){
				countAmount();
			});
			
			$("#adjustTable .isPayment").click(function() { 
				countAmount();
			}); 
			
			//进来先算一下total
			countAmount();
			
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
					var billNoStr="";
					var twoStr="";
					var flag = 1;
					$("select.billNo").each(function(i){
						console.log($(this).val());
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
					
					
					var numberflag  =1;
					$("#contentTable tbody tr").each(function(i,j){
						if(numberflag==1){
							if(parseInt($(this).find(".needPaymentAmount").val())-parseInt($(this).find("input[name='paymentAmount']").val())<0){
								numberflag = 2;
							}
						}
					});
					
					if(numberflag==2){
						top.$.jBox.tip("支付金额不能大于可支付金额！","info",{timeout:3000});
						return false;
					}
					//添加货币类型不同校验
					
					
					
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
					
					
				  $("#btnSureSubmit").attr("disabled","disabled");
				  $("#btnSubmit").attr("disabled","disabled");
				  form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
					
				}
			});
		});
		
		function countAmount(){
			var total=0;
			$("#contentTable tbody tr").find("input[name='paymentAmount']").each(function(){
				total=parseFloat(total)+parseFloat($(this).val());
			});
			
			if($("#adjustTable tbody tr")){
				$("#adjustTable tbody tr").find(".isPayment").each(function(){
					if(this.checked){
						var adjustAmount=$(this).parent().parent().find("input[name='adjustAmount']").val();
						total=parseFloat(total)+parseFloat(adjustAmount);
					}
				});
			}
			
			$("#contentTable tfoot").find(".totalAmount").text(total);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/lcPurchasePayment">采购付款列表</a></li>
		<li class="active"><a href="#">编辑采购付款</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/moneyView">采购订单资金列表</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="purchasePayment" action="${ctx}/psi/lcPurchasePayment/editSave" method="post" class="form-horizontal">
		<input type='hidden' name="id"               value="${purchasePayment.id}" />
		<input type='hidden' name="createDate"       value="${purchasePayment.createDate}" />
		<input type='hidden' name="createUser.id"    value="${purchasePayment.createUser.id}" />
		<input type='hidden' name="paymentSta" 		 value="${purchasePayment.paymentSta}" id="paymentSta"/>
		<input type='hidden' name="delFlag" 		 value="${purchasePayment.delFlag}" />
		<input type='hidden' name="attchmentPath" 	 value="${purchasePayment.attchmentPath}" />
		<input type='hidden' name="paymentNo" 	     value="${purchasePayment.paymentNo}" />
		<input type='hidden' name="oldItemIds" 	     value="${purchasePayment.oldItemIds}" />
		
		<input type='hidden' name="currencyType"     value="${purchasePayment.currencyType}"/>
		
		
		<input type='hidden' name="realPaymentAmount" value="${purchasePayment.realPaymentAmount}" />
		
		 <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:28%">
				<label class="control-label" style="width:100px"><b>供应商</b>:</label>
				<div class="controls" style="margin-left:120px">
				<span >
					<input type='text' style="width:95%"  name="supplier.name" readonly value="${purchasePayment.supplier.nikename} (${purchasePayment.supplier.payMark})"/>
					<input type='hidden' name="supplier.id" value="${purchasePayment.supplier.id }"/>
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:70%" >
					<label class="control-label" style="width:98px"><b>供应商账号</b>:</label>
					<div class="controls" style="margin-left:120px">
					<span>
						<select style="width:100%;" id="accountMaps" name="accountType">
							<c:forEach items="${accountMaps}" var="account" varStatus="i">
								 <option value='${account.key}' ${purchasePayment.accountType eq account.key ?'selected':''}>${account.value}</option>;
							</c:forEach>
						</select>
					</span>
					
					</div>
				</div>
		</div>
			
		<div class="control-group">
			<label class="control-label" style="width:100px"><b>备注:</b></label>
			<div class="controls" style="margin-left:120px">
				<textarea  maxlength="255" style="height:50px;width:98%" name="remark"  > ${purchasePayment.remark}</textarea>
			</div>
		</div>
				
	   <div style="float: left"><blockquote><p style="font-size: 14px">付款项信息</p></blockquote></div><div style="float: left" id=errorsShow></div>
		
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span>增加付款项</a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 10%">付款类型</th>
				   <th style="width: 15%">单号</th>
				   <th style="width: 10%">提示信息</th>
				   <th style="width: 8%">未付金额比例</th>
				   <th style="width: 8%">未付款金额</th>
				   <th style="width: 8%">本次付款金额</th>
				   <th style="width: 8%">货币类型</th>
				   <th style="width: 15%">备注</th>
				   <th style="width: 20%">操作</th>
				   
			</tr>
		</thead>
		<tbody>
			<c:if test="${not empty purchasePayment.items}" >
			<c:forEach items="${purchasePayment.items}" var="item" >
				<tr>
					<td>
					<input type="hidden" name="id" value="${item.id}" />
					<input type="hidden" name="unknowId" />
					<select style="width: 90%"  class='paymentType' name="paymentType" >
						<option value="1" ${item.paymentType eq '1' ?'selected':'' }>尾款</option>
						<option value="0" ${item.paymentType eq '0' ?'selected':'' }>定金</option>
					</select>
					</td>
					<td ><span id="masters">
					
					<c:if test="${item.paymentType eq '0' }">
						<select name="billNo" style="width:100%" class="billNo" >
							<c:forEach items="${orderSet}" var="order" varStatus="i">
									 <option value="${order}" ${order eq item.billNo ?'selected':''}>${order}</option>;
							</c:forEach>
						</select>
					</c:if>
					<c:if test="${item.paymentType eq '1' }">
						<select name="billNo" style="width:100%" class="billNo" >
							<c:forEach items="${ladingSet}" var="lading" varStatus="i">
									 <option value="${lading}" ${lading eq item.billNo ? 'selected':'' }>${lading}</option>;
							</c:forEach>
						</select>
					</c:if>
					
					</span></td>
					<td class="updateTips"></td>
					<td>
					<div class="input-prepend input-append"><input  type="text" class="number paymentRatio" style="width:40%;"  value="100" /><span class="add-on">%</span></div>
					</td>
					<td ><input type="text" class="needPaymentAmount"  readonly="readonly" style="width: 90%"/></td>
					<td ><input type="text" name="paymentAmount"  value="${item.paymentAmount}" style="width: 90%"/></td>
					<td ><input type="text" class="itemCurrencyType"  value="${purchasePayment.currencyType }" readonly="readonly" style="width: 90%"/></td>
				    <td ><input type="text" name="remark"  style="width: 90%" value="${item.remark}"/></td>
					<td style="text-align:center"><a href="#"  class="remove-row"><span class="icon-minus"></span>删除付款项</a></td> 
				</tr>
			</c:forEach>
			</c:if>
		</tbody>
		<tfoot><tr><td>合计</td><td colspan="4"/><td><span class="totalAmount"></span></td><td colspan="3"/></tr>	</tfoot>
	</table>
		
		<c:if test="${not empty adjusts}">
			<div style="float: left"><blockquote><p style="font-size: 14px">额外付款项信息</p></blockquote></div>
				<table id="adjustTable" class="table table-striped table-bordered table-condensed" >
					<thead>
						<tr>
						   <th style="width: 10%">选项</th>
						   <th style="width: 20%">调整主题</th>
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
							<td><input type="text" readonly="readonly" style="width:90%" value="${adjust.currency}" /></td>
							<td><input type="text" readonly="readonly" name="remark" style="width:90%" value="${adjust.remark}" /></td>
						</tr>
					</c:forEach>
					
					</tbody>
				</table>
		</c:if>
		
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSureSubmit" class="btn btn-primary" type="button" value="申请"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
