<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊后台账号管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$("#country").change(function(){
				$("#searchForm").submit();
			});
			var countrysel ="<select id='country' style='width: 120px'>";
				countrysel= countrysel+"<option value='com' ${amazonAccount.country eq 'com' ?'selected':''}>美国|US</option>";
				countrysel= countrysel+"<option value='de' ${amazonAccount.country eq 'de' ?'selected':''}>欧洲|EUR</option>";
				countrysel= countrysel+"<option value='jp' ${amazonAccount.country eq 'jp' ?'selected':''}>日本|JP</option>";
				countrysel= countrysel+"<option value='ca' ${amazonAccount.country eq 'ca' ?'selected':''}>加拿大|CA</option>";
				countrysel= countrysel+"<option value='mx' ${amazonAccount.country eq 'mx' ?'selected':''}>墨西哥|MX</option>";
				countrysel= countrysel+"</select>";
				$("#applyBtn").click(function(){
					var html = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;申请平台&nbsp;&nbsp;&nbsp;："+countrysel+"<br/>接收邀请信邮箱：<div class='input-append btn-group btn-input'><input id='mail' value='${fns:getUser().email}' style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>";
					$.jBox.confirm(html,'确认获取邀请信的邮箱',function(v,h,f){
						if(v=='ok'){
							if(validateMail(h.find("#mail").val())){
								loading('<spring:message code="amazon_order_tips13"/>');
								var params = {};
								params.accountEmail = encodeURI(h.find("#mail").val());
								params.country = encodeURI(h.find("#country").val());
								window.location.href = "${ctx}/amazoninfo/amazonAccount/apply?"+$.param(params);
							}else{
								top.$.jBox.tip("<spring:message code="amazon_order_tips14"/>!","error");
								return false;
							}
						}
					},{buttonsFocus:1,width:500,showClose: false,persistent: true});
					$('.jbox-body .jbox-icon').css('top','55px');
				});
			
				
				$(".activeBtn").click(function(){
					var $this = $(this);
					var html ="激 活 码：<div class='input-append btn-group btn-input'><input id='code'  style='height: 25px;width:300px' class='span2' id='t' size='16'/></div>";
					$.jBox.confirm(html,'填写激活码',function(v,h,f){
						if(v=='ok'){
							if(h.find("#code").val()){
								loading('<spring:message code="amazon_order_tips13"/>');
								var params = {};
								params.code = encodeURI(h.find("#code").val());
								params.id = $this.parent().find(":hidden").val();
								window.location.href = "${ctx}/amazoninfo/amazonAccount/active?"+$.param(params);
							}else{
								top.$.jBox.tip("邀请码不能为空!","error");
								return false;
							}
						}
					},{buttonsFocus:1,width:450,showClose: false,persistent: true});
					$('.jbox-body .jbox-icon').css('top','55px');
				});
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		function validateMail(value) {
			var rs = true;
			var temp =  /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i.test(value);
			if(!temp){
				rs = false;
			}
			return rs; 
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/amazoninfo/amazonAccount/">亚马逊后台账号信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonAccount" action="${ctx}/amazoninfo/amazonAccount/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		平台：<select name="country" id="country" style="width: 120px">
				<option value="com" ${amazonAccount.country eq 'com' ?'selected':''}>美国|US</option>
				<option value="de" ${amazonAccount.country eq 'de' ?'selected':''}>欧洲|EUR</option>
				<option value="jp" ${amazonAccount.country eq 'jp' ?'selected':''}>日本|JP</option>
				<option value="ca" ${amazonAccount.country eq 'ca' ?'selected':''}>加拿大|CA</option>
				<option value="mx" ${amazonAccount.country eq 'mx' ?'selected':''}>墨西哥|MX</option>
			</select>&nbsp;&nbsp;
		<label>账号 ：</label><form:input path="accountEmail" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<shiro:hasPermission name="amazoninfo:amazonAccount:apply">
			<input id="applyBtn" style="float: right;" class="btn btn-primary" type="button" value="账号申请"/>&nbsp;&nbsp;&nbsp;
		</shiro:hasPermission>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>平台</th>
					<th>账号</th>
					<th>权限</th>
					<th>账号申请人</th>
					<th>授权人</th>
					<th>状态</th>
					<th>操作</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="amazonAccount">
			<tr>
				<td>
					<c:if test="${amazonAccount.country eq 'de'}">欧洲|EUR</c:if>
					<c:if test="${amazonAccount.country ne 'de'}">${fns:getDictLabel(amazonAccount.country,'platform','')}</c:if>
				</td>
				<td>${amazonAccount.accountEmail}</td>
				<td>
					<c:choose>
						<c:when test="${empty amazonAccount.rights && amazonAccount.createUser.id ne '1'}">未授权</c:when>
						<c:when test="${amazonAccount.rights eq 'case_dashboard_editor,cxm_viewer,no_1_by_1_gui_role,feedback_editor,no_gift_services_role,no_help_content_role,no_icg_role,no_image_uploader_role,no_inventoryplanning_role,no_item_manager_role,manage_returns_editor,no_manage_your_payments_role,no_merchant_shipping_role,no_advertiser_campaign_role,no_campaign_reg_role,no_nemo_report_role,no_nemo_transactions_role,no_promotions_role,no_promotions_performance_role,no_pyop_refund_role,no_seller_coach_role,no_seller_configuration_role,seller_metrics_viewer,seller_ya_viewer,no_site_metrics_role,no_afn_inventory_transfer_role,no_afn_report_viewer_role,no_afn_settings_editor_role,no_tax_lib_viewer_role,no_tax_manager_role,no_transactions_role,no_upgrade_downgrade_role' }">客服</c:when>
						<c:when test="${amazonAccount.rights eq 'case_dashboard_editor,cxm_viewer,1_by_1_gui,feedback_editor,no_gift_services_role,no_help_content_role,ICG_editor,image_editor,no_inventoryplanning_role,item_manager_viewer,no_manage_returns_role,no_manage_your_payments_role,no_merchant_shipping_role,advertiser_campaign_editor,campaign_mgr_reg_viewer,no_nemo_report_role,no_nemo_transactions_role,promotions_editor,no_promotions_performance_role,no_pyop_refund_role,seller_coach_viewer,no_seller_configuration_role,seller_metrics_viewer,seller_ya_viewer,site_metrics_viewer,afn_inventory_transfer_editor,afn_report_viewer,no_afn_settings_editor_role,no_tax_lib_viewer_role,no_tax_manager_role,no_transactions_role,no_upgrade_downgrade_role'}">销售</c:when>
						<c:otherwise>自定义</c:otherwise>
					</c:choose>
				</td>
				<td>${amazonAccount.createUser.name}</td>
				<td>${amazonAccount.lastUpdateUser.name}</td>
				<td>${amazonAccount.statu}</td>
				<td>
    				<shiro:hasPermission name="amazoninfo:amazonAccount:agree">
    					<c:if test="${amazonAccount.statu eq '提出申请' ||amazonAccount.statu eq '审批失败,请重新审批' }">
    						<a class="btn btn-info" href="${ctx}/amazoninfo/amazonAccount/agree?id=${amazonAccount.id}">同意创建</a>
    					</c:if>
    				</shiro:hasPermission>
    				<c:if test="${amazonAccount.createUser.id eq fns:getUser().id}">
    					<c:if test="${amazonAccount.statu eq '邀请信已发送,请按照信件操作获取激活码' ||amazonAccount.statu eq '激活码错误,请重新激活!' }">
	    					<span  class="btn btn-warning activeBtn">账号激活</span>
	    					<input type="hidden" value="${amazonAccount.id}"/>
    					</c:if>
    				</c:if>
    				<c:if test="${!(fn:contains(amazonAccount.accountEmail,'noreply'))&& !(fn:contains(amazonAccount.accountEmail,'amazon'))&& (amazonAccount.accountEmail ne 'support@inateck.com')}">
	    				<shiro:hasPermission name="amazoninfo:amazonAccount:edit">
		    					<c:if test="${not empty amazonAccount.accountId &&amazonAccount.statu ne '正在授权...' && amazonAccount.statu ne '正在删除账号...'}">
		    						<a class="btn btn-danger" href="${ctx}/amazoninfo/amazonAccount/form?id=${amazonAccount.id}">授权</a>
		    					</c:if>
		    					<c:if test="${not empty amazonAccount.accountId &&amazonAccount.statu ne '正在删除账号...' && amazonAccount.statu ne '正在删除账号...'}">
									<a class="btn btn-danger" href="${ctx}/amazoninfo/amazonAccount/delete?id=${amazonAccount.id}" onclick="return confirmx('确认要删除该亚马逊后台账号吗？', this.href)">删除</a>
								</c:if>
						</shiro:hasPermission>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
