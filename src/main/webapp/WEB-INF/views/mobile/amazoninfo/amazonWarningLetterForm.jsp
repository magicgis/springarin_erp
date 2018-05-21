<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Warning Letter</title>
	<%@include file="/WEB-INF/views/mobile/include/head.jsp" %>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
	
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		$(document).ready(function() {
			if(!(top)){
				top = self; 
			}
			
		});
	</script><style>
.ui-shadow-inset{
	width:50%;
	height:35px;
	margin-left: auto !important;
	margin-right: auto !important;
}
</style>
</head>
<body>
<div data-role="page">
	<div data-role="header" data-theme="b" data-position="fixed">
    <a href="${ctx}" data-role="button" class="ui-btn-left">Home</a>
    <h4>${fns:getConfig('productName')}</h4>
  </div>
	<div data-role="content">
		<div class="container-fluid" style="overflow:auto"s>
			<div class="row-fluid">
				<div class="span12">
					<blockquote>
						<p style="font-size: 14px"><spring:message code='custom_event_detail'/></p>
					</blockquote>
					<div class="control-group">
						<label class="control-label">平台:&nbsp;${fns:getDictLabel(warningLetter.country,'platform','')}</label>
					</div>
					<div class="control-group">
						<label class="control-label">主题:&nbsp;${warningLetter.subject }</label>
					</div>
					<div class="control-group">
						<label class="control-label">发信日期:&nbsp;
							<fmt:formatDate pattern="yyyy-MM-dd" value="${warningLetter.letterDate}"/>
						</label>
					</div>
					<div class="control-group">
						<label class="control-label">信件内容:</label>
						</br/>
						<div class="controls">${warningLetter.letterContent}</div>
					</div>
				</div>
			</div>
		</div>
		</div>
		<div data-role="footer" data-theme="b" data-position="fixed" >
			<h4>Copyright &copy; ${fns:getConfig('productName')}&nbsp;</h4>
		</div>
	</div>
</body>
</html>
