<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/WEB-INF/views/include/dialog.jsp" %>
<html>
<head>
<meta name="decorator" content="default"/>
<title>psisupplierPotentialView</title>

<script type="text/javascript">
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();

	$(document).ready(function(){
		
	
		$("#inputForm").validate({
			rules:{
				"nikename":{
					"required":true,
					 remote: {
					    url: "${ctx}/psi/supplierPotential/nameIsExist",     //后台处理程序
					    type: "post",               //数据发送方式
					    dataType: "json",           //接受数据格式   
					    data: {                     //要传递的数据
					        "name": function() {
					            return $("input[name='nikename']").val();
					        },
					        "oldName": function() {
					            return '${supplierPotential.nikename}';
					        }
					    }
					}
				},
				"shortName":{
			 		remote: {
			    		url: "${ctx}/psi/supplierPotential/shortNameIsExist",
			    		type: "post",
			    		dataType: "json",
			    		data: {
			        		"name": function() {
			            		return $("input[name='shortName']").val();
			        		},
			        		"oldName": function() {
			            		return '${supplierPotential.shortName}';
			        		}
			    		}
					}
				}
			},
			messages:{
				"nikename":{"remote":'该简称已存在，不能重复！'},
				"shortName":{"remote":'该中文简称已存在，不能重复！'}
			},
			submitHandler: function(form){
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
		
		$("#mail").rules('add', {mutEmail: true});
	});

</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/supplierPotential">潜在供应商列表</a></li>
		<c:choose>
			<c:when test="${not empty supplierPotential.id}">
				<li class="active"><a href="${ctx}/psi/supplierPotential/update?id="
					${supplierPotential.id}>编辑潜在供应商</a></li>
			</c:when>
			<c:otherwise>
				<li class="active"><a href="${ctx}/psi/supplierPotential/add">新增潜在供应商</a></li>
			</c:otherwise>
		</c:choose>
	</ul>
	<br />
	<tags:message content="${message}" />
	<form:form id="inputForm" modelAttribute="supplierPotential"
		action="${ctx}/psi/supplierPotential/save" class="form-horizontal">
		<%-- 		<form:hidden path="id"/> --%>
		<input type="hidden" name="id" value="${supplierPotential.id}" />
		<input type="hidden" name="createRegularFlag" value="${supplierPotential.createRegularFlag}" />
		<input type="hidden" name="suffixName" value="${supplierPotential.suffixName}" />
		<blockquote>
			<p style="font-size: 14px">潜在供应商信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">名称</label>
			<div class="controls">
				<input type="text" id="name" name="name" value="${supplierPotential.name}"	class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型</label>
			<div class="controls">
				<select name="type">
					<option value="0" ${supplierPotential.type eq '0'?'selected':''}>产品供应商</option>
					<option value="1" ${supplierPotential.type eq '1'?'selected':''}>物流服务商</option>
					<option value="2" ${supplierPotential.type eq '2'?'selected':''}>包材供应商</option>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">简称</label>
			<div class="controls">
				<input type="text" id="nikename" name="nikename" value="${supplierPotential.nikename}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">中文简称</label>
			<div class="controls">
				<input type="text" id="shortName" name="shortName" value="${supplierPotential.shortName}" maxlength="15"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址</label>
			<div class="controls">
				<input type="text" name="address" value="${supplierPotential.address}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">网站地址</label>
			<div class="controls">
				<input type="text" name="site" value="${supplierPotential.site}" />
			</div>
		</div>
		<div class="control-group" >
			<label class="control-label"><b>货币类型</b>:</label>
			<div class="controls">
			<form:select path="currencyType" class="required" >
				<option value="USD" ${supplierPotential.currencyType eq 'USD'?'selected':''}>USD</option>
				<option value="CNY" ${supplierPotential.currencyType eq 'CNY'?'selected':''}>CNY</option>
				<option value="EUR" ${supplierPotential.currencyType eq 'EUR'?'selected':''}>EUR</option>
				<option value="JPY" ${supplierPotential.currencyType eq 'JPY'?'selected':''}>JPY</option>
				<option value="CAD" ${supplierPotential.currencyType eq 'CAD'?'selected':''}>CAD</option>
				<option value="GBP" ${supplierPotential.currencyType eq 'GBP'?'selected':''}>GBP</option>
			</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">定金</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<input type="text" name="deposit" value="${empty supplierPotential.deposit?'30':supplierPotential.deposit}" class="number"/>
					<span class="add-on">%</span>
				</div>
			</div>
		</div>
		
		
		<blockquote>
			<p style="font-size: 14px">联系人信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">联系人</label>
			<div class="controls">
				<input type="text" name="contact" value="${supplierPotential.contact}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话</label>
			<div class="controls">
				<input type="text" name="phone" value="${supplierPotential.phone}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱</label>
			<div class="controls">
				<input type="text" name="mail" id="mail" class="required" value="${supplierPotential.mail}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">QQ</label>
			<div class="controls">
				<input type="text" name="qq" value="${supplierPotential.qq}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注</label>
			<div class="controls">
				<textarea name="memo" style="width: 600px" rows="5">${supplierPotential.memo}</textarea>
			</div>
		</div>
		<div class="form-actions">
			<input  class="btn btn-primary" type="submit" value="保存" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>	
		</div>
	</form:form>
</body>
</html>