<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<meta name="decorator" content="default" />
<title>psisupplierView</title>
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<script type="text/javascript">

var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();
	$(document).ready(function() {
		$('#add-row').on('click', function(e){
			 e.preventDefault();
			if($('#contentTable').css("display")=='none'){
				$('#contentTable').show();
			}else{
				var tableBody = $('.table > tbody'), 
			    lastRowClone = $('tr:last-child', tableBody).clone();
			    $('input[type=text]', lastRowClone).val('1');  
			    var html = "";
				 <c:forEach items="${products}" var="item"> 
				 		html = html+"<option value='${item.name}' >${item.name}</option>";
				 </c:forEach>
				 lastRowClone.find(".product").html("<select class=\"required\" style=\"width: 100%\">"+html+"</select>");
				 lastRowClone.find("select").select2();
				 tableBody.append(lastRowClone);
			}
		});
		$('#contentTable').on('click', '.remove-row', function(e){
		  e.preventDefault();
		  if($('#contentTable tr').size()>2){
			  var row = $(this).parent().parent();
			  row.remove();
		  }else{
			  $('#contentTable').hide(); 
		  }
		});
		
		$("#colorAll").click(function(){
			if($(this).attr("checked")){
				$(".ckColor").attr("checked",$(this).attr("checked"));
			}else{
				$(".ckColor").removeAttr("checked");
			}
		});
		
		
		$("#certificationAll").click(function(){
			if($(this).attr("checked")){
				$(".ckCertification").attr("checked",true);
			}else{
				$(".ckCertification").removeAttr("checked");
			}
		});
		
		
		$("#inateckCertAll").click(function(){
			if($(this).attr("checked")){
				$(".inateckCert").attr("checked",true);
			}else{
				$(".inateckCert").removeAttr("checked");
			}
		});
		
		$("#factoryCertAll").click(function(){
			if($(this).attr("checked")){
				$(".factoryCert").attr("checked",true);
			}else{
				$(".factoryCert").removeAttr("checked");
			}
		});
		
		
		
		$("#platformAll").click(function(){
			if($(this).attr("checked")){
				$(".ckPlatform[value!='com.unitek']").attr("checked",$(this).attr("checked"));
			}else{
				$(".ckPlatform").removeAttr("checked");
			}
		});
		
		<c:if test="${empty product.id}">
			$("#platformAll").click();
		</c:if>
		
		<c:if test="${not empty product.id}">
			var combination = '${product.combination}';	
			if(combination){
				$('#contentTable').show();	
				var array = combination.split(";");
				for(var i=0;i<array.length-1;i++){
					var temp = array[i].split(":");
					if(i==0){
						$(".product").find("select").select2("val",temp[0]);						
						$(".product").parent().find(".number").val(temp[1]);
					}else{
						var tableBody = $('.table > tbody'), 
					    lastRowClone = $('tr:last-child', tableBody).clone();
					    $('input[type=text]', lastRowClone).val(temp[1]);  
					    var html = "";
						 <c:forEach items="${products}" var="item"> 
						 	html = html+"<option value='${item.name}' >${item.name}</option>";
						 </c:forEach>
						 lastRowClone.find(".product").html("<select class=\"required\" style=\"width: 100%\">"+html+"</select>");
						 lastRowClone.find("select").select2();
						 lastRowClone.find("select").select2("val",temp[0]);
						 tableBody.append(lastRowClone);
					}				
				}
			}
		</c:if>
		
		$("#inputForm").validate({
			rules:{
				"model":{
					"required":true,
					 remote: {
					    url: "${ctx}/psi/product/isExistName",     //后台处理程序
					    type: "post",               //数据发送方式
					    dataType: "json",           //接受数据格式   
					    data: {                     //要传递的数据
					        "model": function() {
					            return $("input[name='model']").val();
					        },
					        "brand": function() {
					            return $("select[name='brand']").val();
					        },
					        "id": function() {
					            return $("input[name='id']").val();
					        }
					        
					    }
					}
				}
			},
			messages:{
				"model":{"remote":'同一品牌的产品型号不能相同！'}
			},
			submitHandler: function(form){
				var colorStr ="";
				var platformStr ="";
				$(".ckPlatform:checked").each(function(){
					platformStr = platformStr+","+$(this).val();
				});
				
				$(".ckColor:checked").each(function(){
					colorStr = colorStr+","+$(this).val();
				});
				
				//减少颜色校验start
				var oldColorStr = '${product.color}'+",";
				var delColorFlag =0;
				if('${product.id}'!=''){
					var colors =oldColorStr.split(",");
					for(var i in colors){
						if((colorStr+",").indexOf(colors[i]+",")<0){
							delColorFlag=1;
							break;
						}
					}
				}
				//减少颜色校验end
				if(delColorFlag==1){
					$.jBox.tip("只能新增颜色");   
					return false;
				}
				
				var certificationStr="";
				$(".ckCertification:checked").each(function(){
					certificationStr = certificationStr+","+$(this).val();
				});
				if(certificationStr){
					$("input[name='certification']").val(certificationStr.substring(1,certificationStr.length));
				}else{
					$("input[name='certification']").val("");
				}
				
				var certificationStr="";
				$(".factoryCert:checked").each(function(){
					certificationStr = certificationStr+","+$(this).val();
				});
				if(certificationStr){
					$("input[name='factoryCertification']").val(certificationStr.substring(1,certificationStr.length));
				}else{
					$("input[name='factoryCertification']").val("");
				}
				
				
				var certificationStr="";
				$(".inateckCert:checked").each(function(){
					certificationStr = certificationStr+","+$(this).val();
				});
				if(certificationStr){
					$("input[name='inateckCertification']").val(certificationStr.substring(1,certificationStr.length));
				}else{
					$("input[name='inateckCertification']").val("");
				}
				
				
				if(colorStr){
					$("input[name='color']").val(colorStr.substring(1,colorStr.length));
				}else{
					if($("input[name='id']").val()==""){
						$("input[name='color']").val("");
						$.jBox.tip("至少选择一个颜色");   
						return false;
					}
				}
				
				if(platformStr){
					$("input[name='platform']").val(platformStr.substring(1,platformStr.length));
				}else{
					$.jBox.tip("至少选择一个销售平台");
					return false;
				}
				
				if($("#declarePoint").val()){
					var content="";
					var params ={};
					params.declarePoint=encodeURI($("#declarePoint").val());
					$.ajax({
					    type: 'get',   
					    async:false,
					    url: '${ctx}/psi/product/decalreV' ,
					    data: $.param(params),
					    success:function(data){ 
					    	content = data;
				        }
					});
					
					if(content!=''){
						$.jBox.tip(content);
						return false;
					}
				}
				
				//拼接组合关系
				if($('#contentTable').css("display")!='none'){
					var str="" ;
					$(".product").each(function(){
						str = str+($(this).find("select").select2('val')+":"+$(this).parent().find(".number").val()+";");						
					});
					$("input[name='combination']").val(str);
				}else{
					$("input[name='combination']").val('');
				}
				
				if($("#chineseName").val().match(/[\uff00-\uffff]/g)){
					$.jBox.tip("中英文名称不能输入全角字符，请切换到英文输入模式");
					return false;
				}
				
				if($("#description").val().match(/[\uff00-\uffff]/g)){
					$.jBox.tip("描述不能输入全角字符，请切换到英文输入模式");
					return false;
				}
				
				if($("input[name='model']").val().indexOf("_") >= 0 ){
					$.jBox.tip("模型不能包含下划线'_'符号");
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
				}
			}
		});
		
		
		$(".deletePath").click(function(){
			var $this = $(this);
			var param = {};
			param.productId = $this.attr("keyVal2");
			param.deletePath =$this.attr("keyVal");
			$.get("${ctx}/psi/product/deleteFilePath?"+$.param(param),function(){
				window.location.href="${ctx}/psi/product/update?id="+$this.attr("keyVal2");
			});
		});
		
	});
	
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product">产品列表</a></li>
		<c:choose>
			<c:when test="${not empty product.id}">
				<li class="active"><a>编辑产品</a></li>
			</c:when>
			<c:otherwise>
				<li class="active"><a>新增产品</a></li>
			</c:otherwise>
		</c:choose>
	</ul>
	<br />
	<tags:message content="${message}" />
	<div class="alert alert-info">注意：产品编辑时颜色只能增加不能减少，请新增产品时选好颜色！</div>
	<form:form id="inputForm" modelAttribute="product"
		action="${ctx}/psi/product/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id" value="${product.id}" />
		<input type="hidden" name="combination" value="${product.combination}" />
		<input type="hidden" name="packVolumeWeight" value="${packVolumeWeight}" />
		
		<input type="hidden" name="certification" 			value="${product.certification}"/>
		<input type="hidden" name="inateckCertification" 	value="${product.inateckCertification}"/>
		<input type="hidden" name="factoryCertification" 	value="${product.factoryCertification}"/>
		<input type="hidden" name="certificationFile" 		value="${product.certificationFile}"/>
		<input type="hidden" name="tranReportFile" 			value="${product.tranReportFile}"/>
		<input type="hidden" name="priceChangeLog" 			value="${product.priceChangeLog}"/>
		<input type="hidden" name="signedSample" 			value="${product.signedSample}"/>
		<input type="hidden" name="checkList" 			    value="${product.checkList}"/>
		<input type="hidden" name="techFile" 			    value="${product.techFile}"/>
		<input type="hidden" name="reviewSta" 			    value="${product.reviewSta}"/>
		<blockquote>
			<p style="font-size: 14px">产品信息 <span style="float: right;font-size: 12px"> <a href="${ctx}/psi/product/listDict">产品基本属性维护</a></span></p> 
		</blockquote>
		<div class="control-group">
			<label class="control-label">品牌</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty product.id}">
						${product.brand}
					</c:when>
					<c:otherwise>
						<select name="brand" id="brand" style="width: 222px">
							<c:forEach items="${fns:getDictList('product_brand')}" var="dic">
								<option value="${dic.value}"${dic.value eq 'Inateck' ?'selected':''}>${dic.label}</option>
							</c:forEach>
						</select>
						<span class="help-inline">注意：新增以后不可修改</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><b>型号</b></label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty product.id}">
						${product.model}
					</c:when>
					<c:otherwise>
						<form:input path="model" class="english" htmlEscape="false" maxlength="200" />
						<span class="help-inline">注意：新增以后不可修改</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">中英文名称 </label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input path="chineseName" id="chineseName" htmlEscape="false" maxlength="200" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><b>供应商</b></label>
			<div class="controls">
				<select name="supplier" style="width: 222px" class="required" multiple="multiple">
					<c:forEach items="${suppliers}" var="supplier">
						<option value="${supplier.id}">${supplier.name}</option>
					</c:forEach>
				</select>
				<script type="text/javascript">
					//<c:forEach items="${product.psiSuppliers}" var="supplier">
						 $("select[name='supplier'] option[value='${supplier.supplier.id}']").attr("selected","selected");	
					//</c:forEach>
				</script>
			</div>
		</div>
		 
		 <div class="control-group" >
				<label class="control-label"><b>跟单员</b>:</label>
				<div class="controls">
					<select style="width: 222px"   name="createUser.id" >
					<c:forEach items="${users}" var="user" varStatus="i">
							 <option value='${user.id}' ${user.id eq product.createUser.id?'selected':'' }>${user.name}</option>;
					</c:forEach>
				</select>
				</div>
			</div>
			
			
		<div class="control-group">
			<label class="control-label">类型</label>
			<div class="controls">
				<select name="type" id="product_type" style="width: 222px" class="required">
					<option value=""></option>
					<c:forEach items="${fns:getDictList('product_type')}" var="dic">
						<option value="${dic.value}"
							${product.type eq dic.value?'selected':''}>${dic.label}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px;width: 570px"><a href="#" id="add-row"><span class="icon-plus"></span>新增组合产品</a></div>
		<div class="control-group">
			<label class="control-label">组合:</label>
			<div class="controls">
				<table style="width: 400px;display:none" id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th>产品</th>
							<th style="width: 170px">个数</th>
							<th style="width: 10px">操作</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td class="product">
								<select style="width: 100%"  class="required">
									<c:forEach items="${products}" var="item">
										<option value="${item.name}">${item.name}</option>									
									</c:forEach>
								</select>
							</td>
							<td><input type="text" style="width: 30%" value="1" class="number required" name="count"/></td>
							<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">图片</label>
			<div class="controls">
				<input type="file" id="uploadImage" name="imagePeview"
					onChange="checkImgType(this)"
					accept="image/gif,image/jpeg,image/x-png" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">颜色</label>
			<div class="controls">
				<form:hidden path="color" htmlEscape="false" maxlength="200"/>
				<label><input type="checkbox" id="colorAll"/>全选</label>&nbsp;
				<c:set  var="colors" value=",${product.color},"/>
				<c:forEach items="${fns:getDictList('product_color')}" var="dic">
					<c:set  var="color" value=",${dic.value},"/>
					<label><input type="checkbox" value="${dic.value}" class="ckColor" ${fn:contains(colors,color)?'checked':'' } />${dic.label}</label>&nbsp;
				</c:forEach>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><b>是否配件</b></label>
			<div class="controls">
				<select name="components" class="components">
					<option ${product.components eq '0'?'selected':''} value="0">否</option>
					<option ${product.components eq '1'?'selected':''} value="1">是</option>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">是否带电池</label>
			<div class="controls">
				<select name="hasElectric" class="required">
					<option value="">请选择</option>
					<option ${product.hasElectric eq '0'?'selected':''} value="0">否</option>
					<option ${product.hasElectric eq '1'?'selected':''} value="1">是</option>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">是否带电源</label>
			<div class="controls">
				<select name="hasPower" class="required">
					<option value="">请选择</option>
					<option ${product.hasPower eq '0'?'selected':''} value="0">否</option>
					<option ${product.hasPower eq '1'?'selected':''} value="1">是</option>
					<option ${product.hasPower eq '2'?'selected':''} value="2">是(全规格)</option>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">是否带磁</label>
			<div class="controls">
				<select name="hasMagnetic" class="required">
					<option value="">请选择</option>
					<option ${product.hasMagnetic eq '0'?'selected':''} value="0">否</option>
					<option ${product.hasMagnetic eq '1'?'selected':''} value="1">是</option>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">产品证书</label>
			<div class="controls">
				<label><input type="checkbox" id="certificationAll"/>全选</label>&nbsp;
				<label><input type="checkbox" value="CE" class="ckCertification" ${fn:contains(product.certification,'CE')?'checked':'' } />CE</label>&nbsp;
				<label><input type="checkbox" value="ROHS" class="ckCertification" ${fn:contains(product.certification,'ROHS')?'checked':'' } />ROHS</label>&nbsp;
				<label><input type="checkbox" value="FCC" class="ckCertification" ${fn:contains(product.certification,'FCC')?'checked':'' } />FCC</label>&nbsp;
				<label><input type="checkbox" value="FDA" class="ckCertification" ${fn:contains(product.certification,'FDA')?'checked':'' } />FDA</label>&nbsp;
				<label><input type="checkbox" value="BQB" class="ckCertification" ${fn:contains(product.certification,'BQB')?'checked':'' } />BQB</label>&nbsp;
				<label><input type="checkbox" value="UL" class="ckCertification" ${fn:contains(product.certification,'UL')?'checked':'' } />UL</label>&nbsp;
				<label><input type="checkbox" value="PSE" class="ckCertification" ${fn:contains(product.certification,'PSE')?'checked':'' } />PSE</label>&nbsp;
				<label><input type="checkbox" value="TELEC" class="ckCertification" ${fn:contains(product.certification,'TELEC')?'checked':'' } />TELEC</label>&nbsp;
				<label><input type="checkbox" value="ETL" class="ckCertification" ${fn:contains(product.certification,'ETL')?'checked':'' } />ETL</label>&nbsp;
				
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">Inateck认证</label>
			<div class="controls">
				<label><input type="checkbox" id="inateckCertAll"/>全选</label>&nbsp;
				<label><input type="checkbox" value="CE" class="inateckCert" ${fn:contains(product.inateckCertification,'CE')?'checked':'' } />CE</label>&nbsp;
				<label><input type="checkbox" value="ROHS" class="inateckCert" ${fn:contains(product.inateckCertification,'ROHS')?'checked':'' } />ROHS</label>&nbsp;
				<label><input type="checkbox" value="FCC" class="inateckCert" ${fn:contains(product.inateckCertification,'FCC')?'checked':'' } />FCC</label>&nbsp;
				<label><input type="checkbox" value="FDA" class="inateckCert" ${fn:contains(product.inateckCertification,'FDA')?'checked':'' } />FDA</label>&nbsp;
				<label><input type="checkbox" value="BQB" class="inateckCert" ${fn:contains(product.inateckCertification,'BQB')?'checked':'' } />BQB</label>&nbsp;
				<label><input type="checkbox" value="UL" class="inateckCert" ${fn:contains(product.inateckCertification,'UL')?'checked':'' } />UL</label>&nbsp;
				<label><input type="checkbox" value="PSE" class="inateckCert" ${fn:contains(product.inateckCertification,'PSE')?'checked':'' } />PSE</label>&nbsp;
				<label><input type="checkbox" value="TELEC" class="inateckCert" ${fn:contains(product.inateckCertification,'TELEC')?'checked':'' } />TELEC</label>&nbsp;
				<label><input type="checkbox" value="ETL" class="inateckCert" ${fn:contains(product.inateckCertification,'ETL')?'checked':'' } />ETL</label>&nbsp;
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">工厂认证</label>
			<div class="controls">
				<label><input type="checkbox" id="factoryCertAll"/>全选</label>&nbsp;
				<label><input type="checkbox" value="CE" class="factoryCert" ${fn:contains(product.factoryCertification,'CE')?'checked':'' } />CE</label>&nbsp;
				<label><input type="checkbox" value="ROHS" class="factoryCert" ${fn:contains(product.factoryCertification,'ROHS')?'checked':'' } />ROHS</label>&nbsp;
				<label><input type="checkbox" value="FCC" class="factoryCert" ${fn:contains(product.factoryCertification,'FCC')?'checked':'' } />FCC</label>&nbsp;
				<label><input type="checkbox" value="FDA" class="factoryCert" ${fn:contains(product.factoryCertification,'FDA')?'checked':'' } />FDA</label>&nbsp;
				<label><input type="checkbox" value="BQB" class="factoryCert" ${fn:contains(product.factoryCertification,'BQB')?'checked':'' } />BQB</label>&nbsp;
				<label><input type="checkbox" value="UL" class="factoryCert" ${fn:contains(product.factoryCertification,'UL')?'checked':'' } />UL</label>&nbsp;
				<label><input type="checkbox" value="PSE" class="factoryCert" ${fn:contains(product.factoryCertification,'PSE')?'checked':'' } />PSE</label>&nbsp;
				<label><input type="checkbox" value="TELEC" class="factoryCert" ${fn:contains(product.factoryCertification,'TELEC')?'checked':'' } />TELEC</label>&nbsp;
				<label><input type="checkbox" value="ETL" class="factoryCert" ${fn:contains(product.factoryCertification,'ETL')?'checked':'' } />ETL</label>&nbsp;
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">产品备注</label>
			<div class="controls">
				<form:textarea path="remark"  htmlEscape="false" maxlength="5000" style="margin: 0px; width: 600px; height: 100px;"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">产品清单</label>
			<div class="controls">
				<form:textarea path="productList"  htmlEscape="false" maxlength="5000" style="margin: 0px; width: 600px; height: 100px;"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><b>销售平台</b></label>
			<div class="controls">
				<form:hidden path="platform" htmlEscape="false" maxlength="200" />
				<label><input type="checkbox" id="platformAll" />全选</label>&nbsp;
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'com1'}">
						<label><input type="checkbox" value="${dic.value}" class="ckPlatform"  ${fn:contains(product.platform,dic.value)?'checked':'' }/>${dic.label}</label>&nbsp;
					</c:if>
				</c:forEach>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">长</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input path="length" class="price" htmlEscape="false" maxlength="200" />
					<span class="add-on">cm</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">宽</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input path="width" class="price" htmlEscape="false" maxlength="200" />
					<span class="add-on">cm</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">高</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input path="height" class="price" htmlEscape="false" maxlength="200" />
					<span class="add-on">cm</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">重</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input path="weight" class="price" htmlEscape="false" maxlength="200" />
					<span class="add-on">g</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述</label>
			<div class="controls">
				<form:textarea path="description"  htmlEscape="false" maxlength="5000" style="margin: 0px; width: 600px; height: 100px;"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">申报要素</label>
			<div class="controls">
				<form:textarea path="declarePoint" id="declarePoint" htmlEscape="false" maxlength="500" style="margin: 0px; width: 600px; height: 100px;" />
				 <span class="help-inline">注意：每个要素+空格+英文“分号”，如：用途 保护电脑;电压 5v;</span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">优化备注</label>
			<div class="controls">
				<form:textarea path="improveRemark"  htmlEscape="false" maxlength="5000" style="margin: 0px; width: 600px; height: 100px;"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">材质</label>
			<div class="controls">
			<select name="material"  style="width: 400px" class="required">
					<option value=""></option>
					<c:forEach items="${fns:getDictList('product_material')}" var="dic">
						<option value="${dic.value}"
							${product.material eq dic.value?'selected':''}>${dic.label}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">退税率</label>
			<div class="controls" >
				<div class="input-prepend input-append">
					<form:input path="taxRefund"  htmlEscape="false" maxlength="2" style="width:30%" /><span class="add-on">%</span>
				</div>
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">合同编号</label>
			<div class="controls" >
				<form:input path="contractNo"  htmlEscape="false" maxlength="100" style="width:30%" />
			</div>
		</div>
		<blockquote>
			<p style="font-size: 14px">产品包装信息 </p> 
		</blockquote>
		<div class="control-group">
			<label class="control-label">长</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input path="productPackLength" class="price" htmlEscape="false" maxlength="200" />
					<span class="add-on">cm</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">宽</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input path="productPackWidth" class="price" htmlEscape="false" maxlength="200" />
					<span class="add-on">cm</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">高</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input path="productPackHeight" class="price" htmlEscape="false" maxlength="200" />
					<span class="add-on">cm</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">产品重量(含包装)</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input path="productPackWeight" class="price" htmlEscape="false" maxlength="200" />
					<span class="add-on">g</span>
				</div>
			</div>
		</div>
		
		<blockquote>
			<p style="font-size: 14px">装箱信息 </p> 
		</blockquote>
		<div class="control-group">
			<label class="control-label">长</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<input name="packLength" value="${not empty product.packLength?product.packLength:'1'}"  class="price required"  maxlength="200" />
					<span class="add-on">cm</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">宽</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<input name="packWidth" value="${not empty product.packWidth?product.packWidth:'1'}" class="price required" maxlength="200" />
					<span class="add-on">cm</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">高</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<input name="packHeight" value="${not empty product.packHeight?product.packHeight:'1'}" class="price required" maxlength="200" />
					<span class="add-on">cm</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">毛重</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<input name="gw" value="${not empty product.gw?product.gw:'1'}"  class="price required" maxlength="200" />
					<span class="add-on">kg</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">装箱个数</label>
			<div class="controls">
				<input name="packQuantity" value="${not empty product.packQuantity?product.packQuantity:'1'}"  class="number required"  maxlength="200" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">生产周期</label>
			<div class="controls">
				<form:input  path="producePeriod" class="number required"  htmlEscape="false" maxlength="200"  />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">运输方式</label>
			<div class="controls">
				<select name="transportType">
					<option></option>
					<option ${'1' eq product.transportType?'selected':'' } value="1">海运</option>
					<option ${'2' eq product.transportType?'selected':'' } value="2">空运</option>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">最小下单数</label>
			<div class="controls">
				<input  name="minOrderPlaced" class="number required"   maxlength="200" value="${not empty product.minOrderPlaced ?product.minOrderPlaced:1}"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn btn-primary" type="submit" value="保 存" />&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="window.location.href ='${ctx}/psi/product'"/>
		</div>
	</form:form>
</body>
</html>