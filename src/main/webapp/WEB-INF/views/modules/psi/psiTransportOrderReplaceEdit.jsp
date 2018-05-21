<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>代发货物流编辑</title>
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
			
			$('#localFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#tranFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#dapFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#otherFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#insuranceFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$('#taxFile').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$(".rate").on("blur",function(){
				if($(this).val()!=''){
					var div = $(this).parent().parent().parent();
					if(div.find(".firstAmount").val()!=''){
						div.find(".afterAmount").val(toDecimal(parseFloat(div.find(".firstAmount").val())*parseFloat($(this).val())));
					}
					var total=0;
					var parentDiv =div.parent();
					parentDiv.find(".afterAmount").each(function(){
						var innerDiv = $(this).parent().parent().parent();
						if(innerDiv.find(".afterAmount").val()!=''&&$(this).val()!=''){
							total=total+parseFloat($(this).val());
						}
					});
					
					$("#totalAmount").val(total);
				}
				
			});
			
			$(".firstAmount").on("blur",function(){
				var div = $(this).parent().parent().parent();
				var rate =div.find(".rate").val();
				if(rate!=''){
					div.find(".afterAmount").val(toDecimal(parseFloat(rate)*parseFloat($(this).val())));
				}
				
				var total=0;
				var parentDiv =div.parent();
				parentDiv.find(".afterAmount").each(function(){
					var innerDiv = $(this).parent().parent().parent();
					if(innerDiv.find(".afterAmount").val()!=''&&$(this).val()!=''){
						total=total+parseFloat($(this).val());
					}
				});
				
				$("#totalAmount").val(total);
			});
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
					
					//如果本地费用不为空     currency1和vendor1不能为空
					if($("input[name='localAmount']").val()!=''){
						if($("select[name='vendor1.id']").val()==''||$("select[name='currency1']").val()==''||$("input[name='rate1']").val()==''){
							top.$.jBox.tip("Currency1和Vendor1和Rate1不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果运输费用不为空     currency2和vendor2不能为空
					if($("input[name='tranAmount']").val()!=''){
						if($("select[name='vendor2.id']").val()==''||$("select[name='currency2']").val()==''||$("input[name='rate2']").val()==''){
							top.$.jBox.tip("Currency2和Vendor2和Rate2不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果其他费用不为空     currency3和vendor3不能为空
					if($("input[name='dapAmount']").val()!=''){
						if($("select[name='vendor3.id']").val()==''||$("select[name='currency3']").val()==''||$("input[name='rate3']").val()==''){
							top.$.jBox.tip("Currency3和Vendor3和Rate3不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果其他费用不为空     currency4和vendor4不能为空
					if($("input[name='otherAmount']").val()!=''){
						if($("select[name='vendor4.id']").val()==''||$("select[name='currency4']").val()==''||$("input[name='rate4']").val()==''){
							top.$.jBox.tip("Currency4和Vendor4和Rate4不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果保险费用不为空     currency5和vendor5不能为空
					if($("input[name='insuranceAmount']").val()!=''){
						if($("select[name='vendor5.id']").val()==''||$("select[name='currency5']").val()==''){
							top.$.jBox.tip("Currency5和Vendor5不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					//如果税费 不为空     currency6和vendor6不能为空
					if($("input[name='dutyTaxes']").val()!=''||$("input[name='taxTaxes']").val()!=''){
						if($("select[name='vendor6.id']").val()==''||$("select[name='currency6']").val()==''){
							top.$.jBox.tip("Currency6和Vendor6不能为空 ", 'info',{timeout:3000});
							return false;
						};
					}
					
					
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
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
	            return f.toFixed(2);  
	     };
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiTransportOrderReplace/">代发货物流列表</a></li>
		<li class="active"><a href="#">${empty psiTransportOrderReplace.id ?'新建':'编辑'}代发货物流</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiTransportOrderReplace" action="${ctx}/psi/psiTransportOrderReplace/editSave" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id" value="${psiTransportOrderReplace.id}"/>
		<input type="hidden" name="createDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrderReplace.createDate}'/>" />
		<input type="hidden" name="createUser.id" value="${psiTransportOrderReplace.cancelUser.id}"/>
		<input type="hidden" name="cancelDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiTransportOrderReplace.cancelDate}'/>" />
		<input type="hidden" name="cancelUser.id" value="${psiTransportOrderReplace.createUser.id}"/>
		<input type="hidden" name="localPath"          value="${psiTransportOrderReplace.localPath}"/>
		<input type="hidden" name="dapPath"            value="${psiTransportOrderReplace.dapPath}"/>
		<input type="hidden" name="tranPath"           value="${psiTransportOrderReplace.tranPath}"/>
		<input type="hidden" name="otherPath"          value="${psiTransportOrderReplace.otherPath}"/>
		<input type="hidden" name="insurancePath"      value="${psiTransportOrderReplace.insurancePath}"/>
		<input type="hidden" name="taxPath"            value="${psiTransportOrderReplace.taxPath}"/>
		
		<input type="hidden" name="suffixName"         value="${psiTransportOrderReplace.suffixName}"/>
		
		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		
		<div style="float:left;width:98%">
		<div class="control-group" style="float:left;width:50%;height:25px">
					<label class="control-label" style="width:100px">发货人信息:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="shipperInfo" type="text"  maxlength="10" style="width:95%"  value="${psiTransportOrderReplace.shipperInfo}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">起运地:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="orgin" type="text"  maxlength="10" style="width:95%"  value="${psiTransportOrderReplace.orgin}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">目的地:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="destination" type="text"  maxlength="10" style="width:95%"  value="${psiTransportOrderReplace.destination}"/>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">航空/轮船公司:</label>
				<div class="controls" style="margin-left:120px" >
				<input name="carrier" type="text"  maxlength="10" style="width:95%"  value="${psiTransportOrderReplace.carrier}"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">离港日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="etdDate"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${psiTransportOrderReplace.etdDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">到港日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="etaDate" type="text" class="Wdate"  style="width:95%" value="<fmt:formatDate value="${psiTransportOrderReplace.etaDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">入仓日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="arrivalDate" type="text"  style="width:95%" class="Wdate" value="<fmt:formatDate value="${psiTransportOrderReplace.arrivalDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">重量:</label>
				<div class="controls" style="margin-left:120px" >
					<div class="input-prepend input-append">
					<input name="weight" type="text" style="width:80%"  id="weight" value="${psiTransportOrderReplace.weight}"/> <span class="add-on">kg</span>
					</div>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">体积:</label>
				<div class="controls" style="margin-left:120px" >
					<div class="input-prepend input-append">
					<input type="text" name="volume"  style="width:80%"  value="${psiTransportOrderReplace.volume}"/> <span class="add-on">m³</span>
					</div>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">箱数:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="boxNumber" type="text"  style="width:95%" value="${psiTransportOrderReplace.boxNumber}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">物流单号:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="ladingBillNo" type="text"  style="width:95%" value="${psiTransportOrderReplace.ladingBillNo}"/>
					</div>
			</div>
		</div>
		
		<blockquote  style="float:left;width:98%;height:25px">
			<div style="float: left; width:8%;height:15px"><p style="font-size: 14px">应付费用信息</p></div>
		</blockquote>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" >Local:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="localAmount" type="text" maxlength="10" style="width:95%" id="localAmount"  class=" price firstAmount" value="${psiTransportOrderReplace.localAmount}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency1:</label>
					<div class="controls" style="margin-left:100px" >
					<select name="currency1" style="width:95%" >
						<option value=""></option>
						<c:forEach items="${currencys}" var="currency">
							<option value="${currency}" ${psiTransportOrderReplace.currency1 eq currency ?'selected':''}>${currency}</option>
						</c:forEach>
					</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Vendor1:</label>
					<div class="controls" style="margin-left:100px">
						<select name="vendor1.id" style="width:95%"  >
							<option value=""></option>
							<c:forEach items="${tranSuppliers}" var="tranSupplier">
								<option value="${tranSupplier.id}"  ${psiTransportOrderReplace.vendor1.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
							</c:forEach>
						</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >汇率1:</label>
					<div class="controls" style="margin-left:100px">
						<input  type="text" name="rate1" maxlength="10" style="width:95%"  class="rate " value="${psiTransportOrderReplace.rate1}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >金额1:</label>
					<div class="controls" style="margin-left:100px" >
						<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.localAmount*psiTransportOrderReplace.rate1}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" >运输费用:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="tranAmount" type="text" maxlength="10" style="width:95%"  class=" price firstAmount" value="${psiTransportOrderReplace.tranAmount}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Currency2:</label>
					<div class="controls" style="margin-left:100px" >
					<select name="currency2" style="width:95%" >
						<option value=""></option>
						<c:forEach items="${currencys}" var="currency">
							<option value="${currency}" ${psiTransportOrderReplace.currency2 eq currency ?'selected':'' }>${currency}</option>
						</c:forEach>
					</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Vendor2:</label>
					<div class="controls" style="margin-left:100px">
						<select name="vendor2.id" style="width:95%"  >
						<option value=""></option>
							<c:forEach items="${tranSuppliers}" var="tranSupplier">
								<option value="${tranSupplier.id}"  ${psiTransportOrderReplace.vendor2.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
							</c:forEach>
						</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >汇率2:</label>
					<div class="controls" style="margin-left:100px" >
						<input  type="text" name="rate2" maxlength="10" style="width:95%"class="rate " value="${psiTransportOrderReplace.rate2}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >金额2:</label>
					<div class="controls" style="margin-left:100px" >
						<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.tranAmount*psiTransportOrderReplace.rate2}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">目的港费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="dapAmount" type="text"  maxlength="10" ${(fn:contains(canEditStr,'DapAmount')&&psiTransportOrderReplace.vendor3.id ne '18')?'readonly':''} style="width:95%" class=" price firstAmount" value="${psiTransportOrderReplace.dapAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency3:</label>
						<div class="controls" style="margin-left:100px" >
						<select name="currency3" style="width:95%"  >
							<option value=""></option>
							<c:forEach items="${currencys}" var="currency">
								<option value="${currency}" ${psiTransportOrderReplace.currency3 eq currency ?'selected':'' }>${currency}</option>
							</c:forEach>
						</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor3:</label>
						<div class="controls" style="margin-left:100px">
							<select name="vendor3.id" style="width:95%"  >
							<option value=""></option>
								<c:forEach items="${tranSuppliers}" var="tranSupplier">
									<option value="${tranSupplier.id}"  ${psiTransportOrderReplace.vendor3.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
								</c:forEach>
							</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >汇率3:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" name="rate3" maxlength="10" style="width:95%" class="rate " value="${psiTransportOrderReplace.rate3}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >金额3:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.dapAmount*psiTransportOrderReplace.rate3}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
		
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">其他费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="otherAmount" type="text"   maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrderReplace.otherAmount}" id="otherAmount"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency4:</label>
						<div class="controls" style="margin-left:100px" >
							<select name="currency4" style="width:95%"  >
							<option value=""></option>
							<c:forEach items="${currencys}" var="currency">
								<option value="${currency}" ${psiTransportOrderReplace.currency4 eq currency ?'selected':'' }>${currency}</option>
							</c:forEach>
						</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor4:</label>
						<div class="controls" style="margin-left:100px">
							<select name="vendor4.id" style="width:95%"  >
								<option value="" ></option>
								<c:forEach items="${tranSuppliers}" var="tranSupplier">
									<option value="${tranSupplier.id}"  ${psiTransportOrderReplace.vendor4.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
								</c:forEach>
							</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >汇率4:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" name="rate4" maxlength="10"  style="width:95%" class="rate" value="${psiTransportOrderReplace.rate4}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >金额4:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.otherAmount*psiTransportOrderReplace.rate4}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">保险费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="insuranceAmount" type="text"  maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrderReplace.insuranceAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency5:</label>
						<div class="controls" style="margin-left:100px" >
							<select name="currency5" style="width:95%"  >
							<option value="" ></option>
							<c:forEach items="${currencys}" var="currency">
								<option value="${currency}" ${psiTransportOrderReplace.currency5 eq currency ?'selected':'' }>${currency}</option>
							</c:forEach>
						</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor5:</label>
						<div class="controls" style="margin-left:100px">
							<select name="vendor5.id" style="width:95%" >
								<option value="" ></option>
								<c:forEach items="${tranSuppliers}" var="tranSupplier">
									<option value="${tranSupplier.id}"  ${psiTransportOrderReplace.vendor5.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
								</c:forEach>
							</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:28%;height:25px"></div>
			</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">进口税:</label>
				<div class="controls" style="margin-left:100px" >
					<input name="dutyTaxes" type="text" maxlength="10"  style="width:95%" class=" price" value="${psiTransportOrderReplace.dutyTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">关税:</label>
				<div class="controls" style="margin-left:100px" >
					<input name="taxTaxes" type="text" maxlength="10"  style="width:85%" class=" price" value="${psiTransportOrderReplace.taxTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency6:</label>
					<div class="controls"  style="margin-left:100px">
						<select name="currency6" style="width:95%" >
						<option value=""></option>
						<c:forEach items="${currencys}" var="currency" >
							<option value="${currency}" ${psiTransportOrderReplace.currency6 eq currency ?'selected':''}>${currency}</option>
						</c:forEach>
					</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Vendor6:</label>
					<div class="controls" style="margin-left:100px">
						<select name="vendor6.id" style="width:95%">
						<option value=""></option>
							<c:forEach items="${tranSuppliers}" var="tranSupplier">
								<option value="${tranSupplier.id}" ${psiTransportOrderReplace.vendor6.id eq tranSupplier.id ?'selected':''}>${tranSupplier.nikename}</option>
							</c:forEach>
						</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:28%;height:25px"></div>
		</div>
		
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">总额:</label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" maxlength="10" style="width:85%" id="totalAmount"  class=" price" value="${psiTransportOrderReplace.totalAmount}"/>
				</div>
			</div>
		</div>
		
		<blockquote  style="float:left;width:98%;height:25px">
			<div style="float: left; width:8%;height:15px"><p style="font-size: 14px">应收费用信息</p></div>
		</blockquote>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" >Local:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="localAmountIn" type="text" maxlength="10" style="width:95%"  class=" price firstAmount" value="${psiTransportOrderReplace.localAmountIn}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency1:</label>
					<div class="controls" style="margin-left:100px" >
					<select name="currencyIn1" style="width:95%" >
						<option value=""></option>
						<c:forEach items="${currencys}" var="currency">
							<option value="${currency}" ${psiTransportOrderReplace.currencyIn1 eq currency ?'selected':''}>${currency}</option>
						</c:forEach>
					</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:80px" >汇率1:</label>
					<div class="controls" style="margin-left:100px">
						<input  type="text" name="rate1_in" maxlength="10" style="width:95%"  class="rate " value="${psiTransportOrderReplace.rateIn1}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:80px" >金额1:</label>
					<div class="controls" style="margin-left:100px" >
						<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.localAmountIn*psiTransportOrderReplace.rateIn1}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" >运输费用:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="tranAmountIn" type="text" maxlength="10" style="width:95%"  class=" price firstAmount" value="${psiTransportOrderReplace.tranAmountIn}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Currency2:</label>
					<div class="controls" style="margin-left:100px" >
					<select name="currencyIn2" style="width:95%" >
						<option value=""></option>
						<c:forEach items="${currencys}" var="currency">
							<option value="${currency}" ${psiTransportOrderReplace.currencyIn2 eq currency ?'selected':'' }>${currency}</option>
						</c:forEach>
					</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:80px" >汇率2:</label>
					<div class="controls" style="margin-left:100px" >
						<input  type="text" name="rateIn2" maxlength="10" style="width:95%"class="rate " value="${psiTransportOrderReplace.rateIn2}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:80px" >金额2:</label>
					<div class="controls" style="margin-left:100px" >
						<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.tranAmountIn*psiTransportOrderReplace.rateIn2}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">目的港费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="dapAmountIn" type="text"  maxlength="10"  style="width:95%" class=" price firstAmount" value="${psiTransportOrderReplace.dapAmountIn}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency3:</label>
						<div class="controls" style="margin-left:100px" >
						<select name="currencyIn3" style="width:95%"  >
							<option value=""></option>
							<c:forEach items="${currencys}" var="currency">
								<option value="${currency}" ${psiTransportOrderReplace.currencyIn3 eq currency ?'selected':'' }>${currency}</option>
							</c:forEach>
						</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:80px" >汇率3:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" name="rateIn3" maxlength="10" style="width:95%" class="rate " value="${psiTransportOrderReplace.rateIn3}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:80px" >金额3:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.dapAmountIn*psiTransportOrderReplace.rateIn3}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
		
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">其他费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="otherAmountIn" type="text"   maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrderReplace.otherAmountIn}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency4:</label>
						<div class="controls" style="margin-left:100px" >
							<select name="currencyIn4" style="width:95%"  >
							<option value=""></option>
							<c:forEach items="${currencys}" var="currency">
								<option value="${currency}" ${psiTransportOrderReplace.currencyIn4 eq currency ?'selected':'' }>${currency}</option>
							</c:forEach>
						</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:80px" >汇率4:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" name="rateIn4" maxlength="10"  style="width:95%" class="rate" value="${psiTransportOrderReplace.rateIn4}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:80px" >金额4:</label>
						<div class="controls" style="margin-left:100px" >
							<input  type="text" maxlength="10" readonly="readonly" style="width:95%" class="afterAmount" value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.otherAmountIn*psiTransportOrderReplace.rateIn4}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">保险费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input name="insuranceAmountIn" type="text"  maxlength="10" style="width:95%" class="price firstAmount" value="${psiTransportOrderReplace.insuranceAmountIn}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency5:</label>
						<div class="controls" style="margin-left:100px" >
							<select name="currencyIn5" style="width:95%"  >
							<option value="" ></option>
							<c:forEach items="${currencys}" var="currency">
								<option value="${currency}" ${psiTransportOrderReplace.currencyIn5 eq currency ?'selected':'' }>${currency}</option>
							</c:forEach>
						</select>
						</div>
				</div>
				<div class="control-group" style="float:left;width:50%;height:25px"></div>
			</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">进口税:</label>
				<div class="controls" style="margin-left:100px" >
					<input name="dutyTaxesIn" type="text" maxlength="10"  style="width:95%" class=" price" value="${psiTransportOrderReplace.dutyTaxesIn}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">关税:</label>
				<div class="controls" style="margin-left:100px" >
					<input name="taxTaxesIn" type="text" maxlength="10"  style="width:85%" class=" price" value="${psiTransportOrderReplace.taxTaxesIn}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency6:</label>
					<div class="controls"  style="margin-left:100px">
						<select name="currencyIn6" style="width:95%" >
						<option value=""></option>
						<c:forEach items="${currencys}" var="currency" >
							<option value="${currency}" ${psiTransportOrderReplace.currencyIn6 eq currency ?'selected':''}>${currency}</option>
						</c:forEach>
					</select>
					</div>
			</div>
			<div class="control-group" style="float:left;width:50%;height:25px"></div>
		</div>
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">费用凭证信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		
		<c:if test="${not empty psiTransportOrderReplace.localPath || not empty psiTransportOrderReplace.tranPath || not empty psiTransportOrderReplace.dapPath || not empty psiTransportOrderReplace.otherPath || not empty psiTransportOrderReplace.insurancePath || not empty psiTransportOrderReplace.taxPath}">
		<div style="float:left;width:98%;height:50px;">
			<div class="control-group" style="float:left;width:98%;height:40px">
					<b>已上传凭证</b>：
					<c:if test="${not empty psiTransportOrderReplace.localPath}">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.localPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">local_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>  
					</c:if>
					<c:if test="${not empty psiTransportOrderReplace.tranPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.tranPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">运输费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrderReplace.dapPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.dapPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">目的港费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrderReplace.otherPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.otherPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">其他费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrderReplace.insurancePath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.insurancePath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">保费费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrderReplace.taxPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.taxPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">税费费用_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>
					</c:if>
					 
			</div>
		</div>
		</c:if>
		
		
		<div style="float:left;width:98%;height:110px;">
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >local凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="localFile" type="file" id="localFile"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >运输费用凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="tranFile" type="file" id="tranFile"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >目的港费用凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="dapFile" type="file" id="dapFile"/>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%;height:110px;">
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >其他费用凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="otherFile" type="file" id="otherFile"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >保费费用凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="insuranceFile" type="file" id="insuranceFile"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:33%;height:100px">
					<label class="control-label" style="width:90px" >税费费用凭证:</label>
					<div class="controls" style="margin-left:100px" >
						<input name="taxFile" type="file" id="taxFile"/>
					</div>
			</div>
		</div>
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">备注信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:98%;">
				<label class="control-label" style="width:80px">备注:</label>
				<div class="controls" style="margin-left:100px">
					<textarea name="remark"  style="width:100%;height:80px;" >${psiTransportOrderReplace.remark}</textarea>
				</div>
			</div>
		</div>
		
		
		<div class="form-actions" style="float:left;width:98%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
