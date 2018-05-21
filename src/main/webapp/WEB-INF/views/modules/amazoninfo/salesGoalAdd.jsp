<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>enterprise_week</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
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
			$("#btnSubmit").click(function(){
				   if($("#month").val()==""||$("#goal").val()==""){
						$.jBox.tip('月份和目标销售额不能为空'); 
						return;
				   }else{
					   $.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/amazoninfo/enterpriseGoal/isExist?month="+$("#month").val()+"&country="+$("#country").val(),
		      			   async: false,
		      			   success: function(msg){
		      				  if(msg=="1"){
		      					$.jBox.tip('此条件下已确定目标销售额'); 
		    					return;
		      				  }else if(msg=="0"){
		      					$("#inputForm").submit();
		      				  }
		      			   }
		          		});
				   }
				   
			   });
		});
		
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/enterpriseGoal">月目标列表</a></li>
		<li  class="active"><a href="#"><c:if test="${empty enterpriseGoal.id }">添加</c:if><c:if test="${not empty enterpriseGoal.id }">修改</c:if>月目标</a></li>
	</ul>
	<form id="inputForm"  action="${ctx}/amazoninfo/enterpriseGoal/saveOrEdit" method="post" class="form-horizontal">
		<input type='hidden' name='id' value="${enterpriseGoal.id }">
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">月份:</label>
			<div class="controls">
			    <fmt:parseDate value="${enterpriseGoal.month}" var="date1"  pattern="yyyyMM"></fmt:parseDate> 
				<input style="width: 180px" onclick="WdatePicker({dateFmt:'yyyyMM'});"  readonly="readonly"  class="Wdate" type="text" name="month" id="month" value="<fmt:formatDate  value="${date1}" pattern="yyyyMM" />" class="input-small"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">国家:</label>
			<div class="controls">
				<select name='country' style="width: 200px"  id="country">
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
					   <c:if test="${dic.value ne 'com.unitek'}">
						  <option value="${dic.value}" ${enterpriseGoal.country eq dic.value?'selected':''}>${dic.label}</option>
						</c:if>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">目标销售额:</label>
			<div class="controls">
				<input id="goal"  style="width: 185px"  name="goal" type="text" value="${enterpriseGoal.goal }"/>
			</div>
		</div>
		<div class="form-actions">
		    <c:if test="${empty enterpriseGoal.id }">
		        <input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>
		    </c:if>
			 <c:if test="${not empty enterpriseGoal.id }">
		        <input  class="btn btn-primary" type="submit" value="保 存"/>
		    </c:if>
		</div>
	</form>
</body>
</html>
