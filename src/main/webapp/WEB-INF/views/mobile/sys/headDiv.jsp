<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>

<script type="text/javascript">
$(document).ready(function() {
	var products="";
	$('#typeahead').typeahead({
		source: function (query, process) {
			if(!(products)){
				$.ajax({
					type: 'post',
					async:false,
					url: '${ctx}/psi/psiInventory/getAllProductNames' ,
					dataType: 'json',
					success:function(data){ 
						products = data;
					}
				});
			}
			process(products);
		},
		updater:function(item){
			var url = "${ctx}/psi/psiInventory/mobileProductInfoDetail?productName=" + encodeURIComponent(item);
			window.location.href = url;
			$.mobile.showPageLoadingMsg("b","加载中...",false);
			return item;
		}
	});
});
</script>
<%--
  <div data-role="header" data-theme="b" data-position="fixed">
    <a href="${ctx}" data-role="button" class="ui-btn-left">Home</a>
    <h4>${fns:getConfig('productName')}</h4>
    <a href="${ctx}/logout" data-role="button" class="ui-btn-right">Logout</a>
  </div> --%>
  
  <div data-role="header" data-theme="b" data-position="fixed">
    <a href="${ctx}" data-role="button">Home</a>
    <div style="width:50%;height:40px;margin-left: auto;margin-right: auto">
	<input id="typeahead" type="text" class="span3 search-query" style="width:100%;" 
			placeholder="查看产品" autocomplete="off"  style="margin: 0 auto;" data-provide="typeahead" data-items="8" />
  	</div>
    <a href="${ctx}/logout" data-role="button" class="ui-btn-right">Logout</a>
  </div>
