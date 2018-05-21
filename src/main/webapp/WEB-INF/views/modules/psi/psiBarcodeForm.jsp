<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>BarcodeManagement</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp"%>
<%@include file="/WEB-INF/views/include/treetable.jsp"%>
<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	$(function(){
		$("select[name='id']").change(function(){
			$("#inputForm").submit();		
		});
		//校验条码
		$("#barcodeForm").validate({
			submitHandler: function(form){
				var flag = false;
				$(form).find("tbody tr").each(function(){
					var value = $(this).find(".bVal").val();
					$(this).find(".bVal").css("border-color","");
					if(value){
						if($(this).find("select.bType").val()=='FNSKU'){
							if(value.length!=10){
								flag = true;
								$(this).find(".bVal").css("border-color","red");
							}
						}else{
							if(value.length!=13){
								flag = true;
								$(this).find(".bVal").css("border-color","red");
							}
						}
					}
				});
				if(flag){
					$.jBox.tip("条码格式有误,请确认条码!!", 'error',{timeout:2000});	
					return false;
				}
				loading('Please wait a moment!');
				form.submit();
			},
			errorContainer: "#messageBox",
			errorPlacement: function(error, element) {
				$("#messageBox").text("Entered incorrectly, please correct");
				if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				};
			}
		});
		
		
		
		
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product/barcodeslist">条码列表</a></li>
		<li class="active"><a href="${ctx}/psi/product/updateBarcodeByUser?id=${product.id}">编辑产品条码</a></li>
	</ul>
	<div align="center">
		<form:form id="inputForm" modelAttribute="psiBarcode" action="${ctx}/psi/product/updateBarcodeByUser" method="post">
			<table class="table table-striped table-bordered table-condensed" style="width: 60%">
				<tr style="height: 50px">
					<th style="text-align: center;vertical-align: middle;font-size: 16px">产品</th>
					<th style="vertical-align: middle;"> &nbsp;&nbsp;&nbsp;&nbsp;
						<select style="width:200px"  class="required" name="id">
							<c:forEach items="${products}" var="item">
								<option value="${item.id}" ${item.id eq product.id?'selected':'' }>${item.name}</option>									
							</c:forEach>
						</select>
					</th>
				</tr>
			</table>
		</form:form> 	
		<form:form modelAttribute="product" id="barcodeForm" action="${ctx}/psi/product/saveBarcodes" method="post">
			<table class="table table-striped table-bordered table-condensed" style="width:60%">
				<thead>
					<tr>
						<th colspan="5" style="text-align: center;"><h5>条码清单</h5></th>
					</tr>
					<tr>
						<td>序号</td>
						<td>平台</td>
						<td>颜色</td>
						<td>账号</td>
						<td>条码类型</td>
						<td>条码</td>
					</tr>
				</thead>
				
				<tbody>
					<c:set var="map" value="${product.barcodeMap2ByAccount}"/>
					<c:forEach items="${fns:getDictList('platform')}" var="plat" varStatus="i">
						<c:if test="${plat.value != 'com.unitek' && not empty map[plat.value]}">
							<shiro:hasPermission name="amazoninfo:feedSubmission:${plat.value}">
								<c:forEach items="${map[plat.value]}" var="countryMap"  varStatus="k">
								   <c:forEach items="${map[plat.value][countryMap.key]}" var="map1"  varStatus="j">
										<tr>
											<c:if test="${j.count==1}">
												<td style="text-align: center;vertical-align: middle" rowspan="${fn:length(map[plat.value][countryMap.key])}">${i.count}</td>
												<td style="text-align: center;vertical-align: middle" rowspan="${fn:length(map[plat.value][countryMap.key])}">${plat.label}</td>
												<td style="text-align: center;vertical-align: middle" rowspan="${fn:length(map[plat.value][countryMap.key])}">${countryMap.key}</td>
												
											</c:if>
											<td style="text-align: center;vertical-align: middle">${map1.accountName}</td>
											<td style="text-align: center;vertical-align: middle">
												<select name="barcodes[${map1.index}].barcodeType" class="bType">
													<option ${map1.barcodeType eq 'FNSKU'?'selected':''}>FNSKU</option>
													<option ${map1.barcodeType eq 'EAN'?'selected':''}>EAN</option>
												</select>
											</td>
											<td style="text-align: center;vertical-align: middle">
												<input class="bVal" type="text" name="barcodes[${map1.index}].barcode"  value="${map1.barcode}"/>
												<input type="hidden" name="barcodes[${map1.index}].id"  value="${map1.id}"/>
												<input type="hidden" name="barcodes[${map1.index}].psiProduct.id"  value="${product.id}"/>
												<input type='hidden' name='barcodes[${map1.index}].accountName' value='${map1.accountName}' />
											</td>
										</tr>
									</c:forEach>
								</c:forEach>
							</shiro:hasPermission>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
			<div class="form-actions" style="width: 60%" align="right">
			<input type="hidden" value="${product.id}" name="id"/>
			<input id="btnSubmit" class="btn btn-primary" type="submit"
				value="保存" />
			</div>
			</form:form> 
		</div>
</body>
</html>
