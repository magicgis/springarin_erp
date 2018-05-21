<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>跟卖监控管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
				}
			});
			
			$("#country").on("change",function(){
				if($(this).val()){
					window.location.href="${ctx}/amazoninfo/followAsin/form?country="+$(this).val();
				}
			});
			
			$('#add-row').on('click', function(e){
				e.preventDefault();
				var tbody=$("#contentTable tbody");
				var tr=$("<tr></tr>");
				tr.append("<td><input type='text' style='width: 80%' name='asin' class='required'/></td>");
				tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span></a></td>");
				tbody.append(tr);
				
			});
		});
		
		 function ajaxSelfAsin(asin){
				var res="";
				$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/amazoninfo/followAsin/ajaxSelfAsin',
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
		<li><a href="${ctx}/amazoninfo/followSeller/">跟卖信息列表</a></li>
		<li><a href="${ctx}/amazoninfo/followAsin/">跟卖产品监控列表</a></li>
		<li class="active"><a href="#">${not empty followAsin.id?'修改':'添加'}跟卖产品监控</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="followAsin" action="${ctx}/amazoninfo/followAsin/save" method="post" class="form-horizontal">
	<%-- 	<input type="hidden" name="id" 				value="${followAsin.id}"/> --%>
		<div class="control-group">
			<label class="control-label">Country:</label>
			<div class="controls">
				<select name="country" class="required" id="country">
					<option value="" >请选择平台</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							<option value="${dic.value}" ${dic.value eq followAsin.country ?'selected':'' } >${dic.label}</option>
						</c:if>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">ASIN:</label>
			<div class="controls">
				<select name="asin" multiple="multiple" class="required" style="width: 80%">
						<c:forEach items="${asinMap}" var="entry">
							<option value="${entry.key}">${entry.value}[${entry.key}]</option>
						</c:forEach>
				</select>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
