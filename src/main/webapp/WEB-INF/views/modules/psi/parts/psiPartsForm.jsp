<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品配件管理</title>
	<meta name="decorator" content="default"/>
		<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
		<style type="text/css">
		.uploadPreview {
		    height:120px;     
		    width:100%;                     
		}
		.pic{
		    border:0; 
			margin:0; 
			padding:0; 
			max-width:200px; 
			width:expression(this.width>200?"200px":this.width); 
			max-height:120px; 
			height:expression(this.height>120?"120px":this.height); 
		}
		
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			
			$(".icon-remove").on("click",function(){
				$(this).parent().css("display","none");
			});
			
			
			$('#myfileupload').MultiFile({
				max : 30,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			
			$("#inputForm").validate({
				rules:{
					"partsName":{
						"required":true,
						 remote: {
						    url: "${ctx}/psi/psiParts/isExistName",     //后台处理程序
						    type: "post",               //数据发送方式
						    dataType: "json",           //接受数据格式   
						    data: {                     //要传递的数据
						        "partsName": function() {
						            return $("input[name='partsName']").val();
						        },
						        "id": $("input[name='id']").val()
						    }
						}
					}
				},
				messages:{
					"partsName":{"remote":'该名称以前已经录入，请更名!'}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					
					var filePath="";
					$(".icon-remove").each(function(){
						if($(this).parent().css("display")!='none'){
							filePath=filePath+$(this).attr("type")+",";
						};
					});
					
					$("input[name='attchmentPath']").val(filePath.substr(0,filePath.length-1));
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiParts/">产品配件列表</a></li>
		<li class="active"><a href="${ctx}/psi/psiParts/form?id=${psiParts.id}">产品配件${not empty psiParts.id?'编辑':'添加'}</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiParts" action="${ctx}/psi/psiParts/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<form:hidden path="id"/>
		<form:hidden path="delFlag"/>
		<form:hidden path="createUser.id" />
		<form:hidden path="updateUser.id" />
		<form:hidden path="image"/>
		<form:hidden path="oldPrice"/>
		<form:hidden path="oldRmbPrice"/>
		<form:hidden path="priceChangeLog" htmlEscape="false"/>
		<form:hidden path="attchmentPath" id="attchmentPath"/>
		<shiro:lacksPermission name="psi:product:viewPrice">
			<form:hidden path="price" />
			<form:hidden path="rmbPrice" />
		</shiro:lacksPermission>
		
		<input type="hidden" name="createDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiParts.createDate}'/>"/>
		<input type="hidden" name="updateDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiParts.updateDate}'/>"/>
		
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">配件型号:</label>    
			<div class="controls">
				<input name="partsName" type="text" value="${psiParts.partsName}" class="required"/>
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
			<label class="control-label">配件类型:</label>
			<div class="controls">
			<form:select path="partsType" style="width: 220px" id="country" >
				<option value="" ${psiParts.partsType eq '' ?'selected':''}></option>
				<c:forEach items="${fns:getDictList('parts_type')}" var="partsTypes">
						 <option value="${partsTypes.value}" ${psiParts.partsType eq partsTypes.value ?'selected':''}  >${partsTypes.label}</option>
				</c:forEach>	
			</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">配件供应商:</label>
			<div class="controls">
			<select style="width:220px;" id="supplier" name="supplier.id" class="required">
				<option value="" ${psiParts.supplier eq '' ?'selected':''}></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}' ${supplier.id eq  psiParts.supplier.id?'selected':''}>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			</div>
		</div>
		
		<shiro:hasPermission name="psi:product:viewPrice">
			<div class="control-group">
				<label class="control-label">美元价格:</label>
				<div class="controls">
					<form:input path="price"  maxlength="11" class="price"  />
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label">人民币价格:</label>
				<div class="controls">
					<form:input path="rmbPrice"  maxlength="11" class="price"  />
				</div>
			</div>
		</shiro:hasPermission>
		
		<shiro:lacksPermission name="psi:product:viewPrice">
			<c:if test="${fns:getUser().id eq psiParts.createUser.id}">
				<div class="control-group">
					<label class="control-label">美元价格:</label>
					<div class="controls">
						<form:input path="price"  maxlength="11" class="price"  />
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label">人民币价格:</label>
					<div class="controls">
						<form:input path="rmbPrice"  maxlength="11" class="price"  />
					</div>
				</div>
			</c:if>
		</shiro:lacksPermission>
		
		<div class="control-group">
			<label class="control-label">生产周期</label>
			<div class="controls">
				<form:input  path="producePeriod" class="number required"   maxlength="10" />
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">Moq</label>
			<div class="controls">
				 <form:input  path="moq" class="number"    maxlength="10" />
			</div>
		</div>
		
		
		<c:if test="${not empty psiParts.attchmentPath}">
		<div class="control-group" >
		<label class="control-label" >已上传合同:</label>
			<div class="controls">
				<c:forEach items="${fn:split(psiParts.attchmentPath,',')}" var="attchment" varStatus="i">
					<span><a target="_blank" href="<c:url value='/data/site/${attchment}'/>">合同 ${i.index+1}</a><span class="icon-remove" type="${attchment}"></span></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach> 
			</div>  
		</div>
		</c:if>
		
		<div class="control-group" >
		<label class="control-label" style="height:100px">合同:</label>
			<div class="controls">
				<span class="help-inline">支持多附件</span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="attchmentFile" type="file" id="myfileupload" />
			</div>
		</div>
		
		
		
		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" ></form:textarea>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">描述:</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" ></form:textarea>
			</div>
		</div>
		
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
