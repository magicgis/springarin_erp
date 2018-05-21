<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品管理管理</title>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	
	<style type="text/css">
		#uploadPreview {
		    width: 120px;
		    height: 120px;                          
		    background-position: center center;
		    background-size: cover;
		    border: 4px solid #fff;
		    -webkit-box-shadow: 0 0 1px 1px rgba(0, 0, 0, .3);
		    display: inline-block;
		}
		pre{
			border-style: none
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
			
			<c:if test="${product.finish != 7}">
				var ckeditor = CKEDITOR.replace("remarks");
				ckeditor.config.height = "100px";
				ckeditor.config.enterMode = CKEDITOR.ENTER_BR;
				ckeditor.config.shiftEnterMode = CKEDITOR.ENTER_P
			</c:if>
			<c:if test="${product.finish == 7}">
				$("input").attr("disabled","disabled");
				$("textarea").attr("disabled","disabled");
			</c:if>
			
			$("#finish").click(function(){
				top.$.jBox.confirm('确定完成新品发布计划吗？计划完成后将不可再更改任何信息!','系统提示',function(v,h,f){
					if(v=='ok'){
						loading('正在提交，请稍等...');
						$("input[name='finish']").val("7");
						$("#inputForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#save").click(function(){
				var index = 1 ;
				$("input[id^='end']").each(function(i){
					if($(this).val() != ''){
						index = i ;
					}
				});
				$("input[name='finish']").val(index);
			});
			
			$(".decodeHtml").each(function(){
				var str = $(this).text()+"";
				$(this).html(str.decodeHtml());
			});	
			
			$("#uploadImage").on("change", function(){
			    // Get a reference to the fileList
			    var files = !!this.files ? this.files : [];
			    // If no files were selected, or no FileReader support, return
			    if (!files.length || !window.FileReader) return;
			    // Only proceed if the selected file is an image
			    if (/^image/.test( files[0].type)){
			        // Create a new instance of the FileReader
			        var reader = new FileReader();
			        // Read the local file as a DataURL
			        reader.readAsDataURL(files[0]);
			        // When loaded, set image data as background of div
			        reader.onloadend = function(){
			      		 $("#uploadPreview").css("background-image", "url("+this.result+")");
			 		}
			    }
			});
			
			$("#name").focus();
			
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
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
			
			for ( var i = 0; i < 7; i++) {
				$("#end"+i).rules('add', {greaterThan: "#start"+i});
			}
			
			for ( var i = 1; i < 7; i++) {
				$("#start"+i).rules('add', {greaterThan: "#end"+(i-1)});
			}
		});
	</script>
	
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/plan/product/">产品列表</a></li>
		<li class="active"><a href="${ctx}/plan/product/form?id=${product.id}">新品<shiro:hasPermission name="plan:product:edit">${not empty product.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="plan:product:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="product" action="${ctx}/plan/product/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<form:hidden path="id"/>
		<form:hidden path="startDate"/>
		<form:hidden path="endDate"/>
		<input type="hidden" value="${product.finish}" name="finish" />
		
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">产品类型：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">产品型号：</label>
			<div class="controls">
				<form:input path="model" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开发周期：</label>
			<div class="controls">
				<form:input path="period" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">产品图片：</label>
			<div class="controls">
				  <div id="uploadPreview"></div>
				  <c:if test="${not empty product.imgPath}">
						<script type="text/javascript">
						 	$("#uploadPreview").css("background-image", "url('<c:url value="${product.imgPath}"></c:url>')");
						</script>					  	
				  </c:if>
				  <c:if test="${product.finish!=8}">
				  		<input name="imagePeview" id="uploadImage" type="file" onChange="checkImgType(this)" accept="image/gif,image/jpeg,image/x-png"/>
				  </c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">负责人：</label>
			<div class="controls">
				<select id="masterBy" name="masterBy"  class="required" ${product.finish==8?'disabled':''}>
					<option value="">请选择</option>
					<c:forEach var="item" items="${masterItems}">
						<option value="${item.key}" ${item.key==product.masterBy.id?'selected':'' }  >${item.value}</option>
					</c:forEach>		
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">供应商：</label>
			<div class="controls">
				<form:input path="supplier" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">产品功能描述：</label>
			<div class="controls">
				<c:choose>
					<c:when test="${product.finish==8}">
							<pre class="decodeHtml">${product.remarks}</pre>
					</c:when>
					<c:otherwise>
							<textarea name="remarks">${product.remarks}</textarea>
					</c:otherwise>				
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开发流程：</label>
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead><tr><th style="width: 5%">序号</th><th style="width: 10%">步骤</th><th>开始时间</th><th>结束时间</th><th>完成情况</th></tr></thead>
					<tbody>
						<tr><td>1<input type="hidden" value="1"  name="listFlow[0].step"/></td><td>意向产品确定</td><td><input id="start0" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[0].startDate}"/>'  name="listFlow[0].startDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td><input id="end0" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[0].endDate}"/>' name="listFlow[0].endDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td>
					<textarea  name="listFlow[0].remarks" rows="6" maxlength="200">${product.listFlow[0].remarks}</textarea>
					</td></tr>
						
						<tr><td>2<input type="hidden" value="2"  name="listFlow[1].step"/></td><td>产品功能优化</td><td><input id="start1" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[1].startDate}"/>' name="listFlow[1].startDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td><input id="end1" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[1].endDate}"/>' name="listFlow[1].endDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td>
					<textarea  name="listFlow[1].remarks" rows="6" maxlength="200">${product.listFlow[1].remarks}</textarea>
					</td></tr>
						
						<tr><td>3<input type="hidden" value="3"  name="listFlow[2].step"/></td><td>产品外形优化</td><td><input id="start2" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[2].startDate}"/>' name="listFlow[2].startDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td><input id="end2" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[2].endDate}"/>' name="listFlow[2].endDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td>
					<textarea  name="listFlow[2].remarks" rows="6" maxlength="200">${product.listFlow[2].remarks}</textarea>
					</td></tr>
						
						<tr><td>4<input type="hidden" value="4"  name="listFlow[3].step"/></td><td>供应商确定</td><td><input id="start3" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[3].startDate}"/>' name="listFlow[3].startDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td><input id="end3" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[3].endDate}"/>' name="listFlow[3].endDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td>
					<textarea  name="listFlow[3].remarks" rows="6" maxlength="200">${product.listFlow[3].remarks}</textarea>
					</td></tr>
						
						<tr><td>5<input type="hidden" value="5"  name="listFlow[4].step"/></td><td>最终样品确定</td><td><input id="start4" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[4].startDate}"/>' name="listFlow[4].startDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td><input id="end4" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[4].endDate}"/>' name="listFlow[4].endDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td>
					<textarea  name="listFlow[4].remarks" rows="6" maxlength="200">${product.listFlow[4].remarks}</textarea>
					</td></tr>
						
						<tr><td>6<input type="hidden" value="6"  name="listFlow[5].step"/></td><td>申请下单</td><td><input id="start5" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[5].startDate}"/>' name="listFlow[5].startDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td><input id="end5" value='<fmt:formatDate pattern="yyyy-MM-dd" value="${product.listFlow[5].endDate}"/>' name="listFlow[5].endDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/></td><td>
					<textarea  name="listFlow[5].remarks" rows="6" maxlength="200">${product.listFlow[5].remarks}</textarea>
					</td></tr>
					</tbody>
				</table>
			</div>
		</div>
		<c:if test="${product.finish!=7}">
		<div class="form-actions" style="text-align: center;">
			<shiro:hasPermission name="plan:product:edit"><input  id="save" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;&nbsp;&nbsp;&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			<c:if test="${not empty product.id}">			
				<shiro:hasPermission name="plan:product:edit"><input style="float: right;" id="finish" class="btn btn-primary" type="button" value="完成开发"/>&nbsp;</shiro:hasPermission>
			</c:if>
		</div>
		</c:if>
	</form:form>
</body>
</html>
