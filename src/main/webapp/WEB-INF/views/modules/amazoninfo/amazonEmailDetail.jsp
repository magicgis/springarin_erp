<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>客户Email详情</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<%@include file="/WEB-INF/views/include/datatables.jsp" %>  
<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctxStatic}/raty-master/lib/jquery.raty.js" ></script>
<link href="${ctxStatic}/common/mailstate.css" type="text/css" rel="stylesheet" />
<style>
.rating-star {
width: 0;
margin: 0;
padding: 0;
border: 0;

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
		$("a[rel='popover']").popover({
			trigger : 'hover'
		});
		
		$("#emailTable").dataTable({
			"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
			"sPaginationType" : "bootstrap",
			"iDisplayLength" : 10,
			"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
					[ 10, 20, 60, 100, "All" ] ],
			"bScrollCollapse" : true,
			"oLanguage" : {
				"sLengthMenu" : "_MENU_ 条/页"
			},
			"ordering" : true,
			"aoColumns": [
				          null,
				          null,
					      null
					],
			 "aaSorting": [[ 0, "desc" ]]
		});
		
		//<c:if test="${not empty customer.amzEmail}">
		$("#star").raty({ score:${empty customer.star?0:customer.star},path:'${ctxStatic}/raty-master/lib/images',click: function(score, evt) {
		    var param = {};
			var customerId = '${customer.customerId}';
			param.customId = customerId;
			param.star = score;
			if(customerId){
				$.get("${ctx}/amazoninfo/customers/save?"+$.param(param),function(data){
					if($.isNumeric(data)){
						$.jBox.tip("设置客户星级成功！", 'info',{timeout:1000});
					}
				});
			}
		}});
		
		$(".editor").editable({mode:'inline',success:function(response,newValue){
    			var param = {};
   				var customerId = '${customer.customerId}';
   				param.customId = customerId;
   				param.email = newValue;
   				if(newValue.match(/[\uff00-\uffff]/g)){
					$.jBox.tip("Email不能输入全角字符,请切换到英文输入模式");
					return false;
				}
   				if(newValue.substr(newValue.length-1,1)==","){
   					$.jBox.tip("Email字符串最后不用加逗号");
   					return false;
   				}
   				if(customerId){
	   				$.get("${ctx}/amazoninfo/customers/save?"+$.param(param),function(data){
	   					if($.isNumeric(data)){
	   						$.jBox.tip("保存邮箱成功！", 'info',{timeout:1000});
	   					}
	   				});	
   				}
   				return true;
		}});
		//</c:if>
		
	});
</script>

</head>
<body>
	<br/>
	<div class="container" id="page">
		<div id="content">
			<div class="container">
				
				
				<table class="table table-striped table-bordered table-condensed " id="emailTable">
					<thead>
							<tr class="warning" style="background-color: #fcf8e3;"><td colspan='3'>邮件</td>	</tr>  
							<tr><td>时间</td>	<td>主题</td><td>负责人</td></tr>
					</thead>
					<c:if test="${not empty emailMap }">
					    <c:forEach items="${emailMap}" var="email" varStatus="i">
					     <tr class="success">
						    <td><div class="cij${not empty emailMap[email.key].attchmentPath || not empty emailMap[email.key].inlineAttchmentPath ?' Ju':''}"></div>${emailMap[email.key].createDate }</td>
							<td><a target='_blank' href="${ctx}/custom/emailManager/view?id=${email.key}">${emailMap[email.key].subject }</a></td>
							<td>${emailMap[email.key].customId }</td>
						 </tr>
						 <c:if test="${not empty emailMap[email.key].sendEmails}">
						       <c:forEach items="${emailMap[email.key].sendEmails}" var="sendEmail">
						             <tr>
									    <td><div class="cir Rh"></div><div class="cij${not empty sendEmail.sendAttchmentPath?' Ju':''}"></div>${sendEmail.sentDate }</td>
										<td><a target='_blank' href="${ctx}/custom/sendEmail/view?id=${sendEmail.id}">${sendEmail.sendSubject }</a></td>
										<td>${sendEmail.sendContent }</td>
									 </tr>
						       </c:forEach>
						 </c:if>	
                        </c:forEach>
					</c:if>
				</tbody>
				</table>
				
				

			</div>
		</div>
		<!-- content -->
		<div class="clear"></div>
	</div>

</body>
</html>
