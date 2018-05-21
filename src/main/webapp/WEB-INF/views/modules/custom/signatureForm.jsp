<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		$(document).ready(function() {
			if(CKEDITOR.instances.signatureContent){
				delete CKEDITOR.instances.signatureContent.config;
				delete CKEDITOR.instances.signatureContent;
			}
			var editor = CKEDITOR.replace("signatureContent",{width:'500px',height:'200px',toolbarStartupExpanded:false,startupFocus:true});
			$("#signatureContent").data("editor",editor);
		});
	</script>
	<br/>
	<br/>
	<div id="signature" style="width: 510px;margin: auto;">
		<textarea id="signatureContent"  name="signatureContent">${signature.signatureContent}</textarea>
		<span class="help-inline">Signed into effect when the next generation email content</span>
	</div>
	<br/>
	<br/>