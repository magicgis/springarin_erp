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
			if(CKEDITOR.instances.comment){
				delete CKEDITOR.instances.comment.config;
				delete CKEDITOR.instances.comment;
			}
			var editor = CKEDITOR.replace("comment",{width:'500px',height:'200px',toolbarStartupExpanded:false,startupFocus:true});
			$("#comment").data("editor",editor);
			
		});
	</script>
	<br/>
	<br/>
		<textarea id="comment"  name="comment"></textarea>
	<br/>
	<br/>