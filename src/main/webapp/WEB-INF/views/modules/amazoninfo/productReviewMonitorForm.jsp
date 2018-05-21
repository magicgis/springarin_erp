<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>对手评论检测管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					var asin=$(".asin").val();
					if(asin.length!=10){
						top.$.jBox.tip("Asin必须为10位", 'info',{timeout:3000});
						return false;
					}
					//校验是否是自己的sku
					var res=ajaxSelfAsin(asin);   
					if(res){
						top.$.jBox.tip("此asin:"+asin+"是自己产品的asin,请录入竞争对手的asin!", 'info',{timeout:3000});
						return false;
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
		
		 function ajaxSelfAsin(asin){
				var res="";
				$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/amazoninfo/productReviewMonitor/ajaxSelfAsin',
				    data: {
				    	"asin":asin
				    },
				    dataType: 'json',
				    success:function(data){ 
				    	res=data.msg;
			        }
				});
				return res;
			};
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/productReviewMonitor/">对手产品评论监控列表</a></li>
		<li class="active"><a href="#">${not empty productReviewMonitor.id?'修改':'添加'}对手产品评论监控</a></li>
	</ul>
	<div style="float:left;width:98%;height:15px" class="alert alert-info"><strong>请录入竞争对手的asin,如果要看自己的asin情况,请到事件模块或者erp早报中找寻!</strong></div>
	<form:form id="inputForm" modelAttribute="productReviewMonitor" action="${ctx}/amazoninfo/productReviewMonitor/save" method="post" class="form-horizontal">
		<input type="hidden" name="id" 				value="${productReviewMonitor.id}"/>
		<div class="control-group">
			<label class="control-label">Country:</label>
			<div class="controls">
				<select name="country" >
				
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							<option value="${dic.value}" ${dic.value eq 'com' ?'selected':'' } >${dic.label}</option>
						</c:if>
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">Product Name:</label>
			<div class="controls">
				<input name="productName"  type="text" maxlength="100" class="required" style="width:40%" value="${productReviewMonitor.productName}" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">Asin:</label>
			<div class="controls">
				<input name="asin" class="asin" type="text" maxlength="10" class="required" style="width:40%" value="${productReviewMonitor.asin}" />
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
