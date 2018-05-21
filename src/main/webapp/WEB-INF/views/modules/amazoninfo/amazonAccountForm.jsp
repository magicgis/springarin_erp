<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>亚马逊后台账号信息管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	$(function(){
		var rights1 = "case_dashboard_editor,cxm_viewer,no_1_by_1_gui_role,feedback_editor,no_gift_services_role,no_help_content_role,no_icg_role,no_image_uploader_role,no_inventoryplanning_role,no_item_manager_role,manage_returns_editor,no_manage_your_payments_role,no_merchant_shipping_role,no_advertiser_campaign_role,no_campaign_reg_role,no_nemo_report_role,no_nemo_transactions_role,no_promotions_role,no_promotions_performance_role,no_pyop_refund_role,no_seller_coach_role,no_seller_configuration_role,seller_metrics_viewer,seller_ya_viewer,no_site_metrics_role,no_afn_inventory_transfer_role,no_afn_report_viewer_role,no_afn_settings_editor_role,no_tax_lib_viewer_role,no_tax_manager_role,no_transactions_role,no_upgrade_downgrade_role";
		var rights2 = "case_dashboard_editor,cxm_viewer,1_by_1_gui,feedback_editor,no_gift_services_role,no_help_content_role,ICG_editor,image_editor,no_inventoryplanning_role,item_manager_viewer,no_manage_returns_role,no_manage_your_payments_role,no_merchant_shipping_role,advertiser_campaign_editor,campaign_mgr_reg_viewer,no_nemo_report_role,no_nemo_transactions_role,promotions_editor,no_promotions_performance_role,no_pyop_refund_role,seller_coach_viewer,no_seller_configuration_role,seller_metrics_viewer,seller_ya_viewer,site_metrics_viewer,afn_inventory_transfer_editor,afn_report_viewer,no_afn_settings_editor_role,no_tax_lib_viewer_role,no_tax_manager_role,no_transactions_role,no_upgrade_downgrade_role";
		var rights3 = "${amazonAccount.rights}";
		$("#role").change(function(e){
			console.log(e);
			if(!(e.val))e.val=$("#role").select2("val");
			if(1==e.val){
				$(rights1.split(",")).each(function(){
					$(":radio[value="+this+"]").attr("checked","checked");					
				});
				$(":radio").each(function(){
					$(this).attr("disabled","disabled");
				});
			}else if(2==e.val){
				$(rights2.split(",")).each(function(){
					$(":radio[value="+this+"]").attr("checked","checked");					
				});
				$(":radio").each(function(){
					$(this).attr("disabled","disabled");
				});
			}else if(3==e.val){
				$(":radio").each(function(){
					$(this).removeAttr("disabled");
				});
				$("tr").each(function(){
					$(this).find(":radio:first").attr("checked","checked");
				});
				$(rights3.split(",")).each(function(){
					$(":radio[value="+this+"]").attr("checked","checked");					
				});
			}
		});
		if(rights3){
			if(rights3==rights1){
				$("#role").select2("val","1");
			}else if(rights3==rights2){
				$("#role").select2("val","2");
			}else{
				$("#role").select2("val","3");
			}
		}else{
			$("#role").select2("val","1");
		}
		$("#role").change();
		$("#inputForm").submit(function(){
			$(":radio").each(function(){
				$(this).removeAttr("disabled");
			});
		});
		//联动
		$("#ssof_inventory_manager_role_none").click(function(){
			$("#ssof_report_viewer_role_none,#ssof_settings_editor_role_none").attr("checked","checked");
		});
		$("#ssof_report_viewer_role_view,#ssof_settings_editor_role_edit").click(function(){
			$("#ssof_inventory_manager_role_edit").attr("checked","checked");
		});
	});	
		
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/amazonAccount/">亚马逊后台账号列表</a></li>
		<li class="active"><a href="#">亚马逊后台账号授权</a></li>
	</ul>
	<br />
	<div style="text-align: center;">
		<select id="role">
			<option value="1">客服</option>
			<option value="2">销售</option>
			<option value="3">自定义</option>	
		</select>
	</div>	
	<form:form id="inputForm" modelAttribute="amazonAccount" action="${ctx}/amazoninfo/amazonAccount/authorize" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<table cellspacing="0" cellpadding="0" align="center" bgcolor="#ffffff" border="0">
			<tbody>
				<tr>
					<td>
						<table border="0" cellpadding="6" cellspacing="0">
							<tbody>
								<tr>
									<td height="40" align="left" valign="bottom" class="small"><strong>Inventory</strong></td>
								</tr>
							</tbody>
						</table>
						<table width="100%" cellspacing="0" cellpadding="0" align="center" bgcolor="#dddddd" border="0">
							<tbody>
								<tr>
									<td align="middle" valign="center"><table id="permissions-table" cellspacing="1" cellpadding="4" width="100%" border="0">
											<tbody>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Item Classification Guide:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input  type="radio"  name="rightMap[icg_role]" value="no_icg_role" id="icg_role_none"> <label for="icg_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input  type="radio"   name="rightMap[icg_role]" value="ICG_viewer" id="icg_role_view"> <label for="icg_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input  type="radio"  name="rightMap[icg_role]" value="ICG_editor" id="icg_role_edit"><label for="icg_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Manage FBA Inventory/Shipments:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio" name="rightMap[ssof_inventory_manager_role]" value="no_afn_inventory_transfer_role" id="ssof_inventory_manager_role_none"><label for="ssof_inventory_manager_role_none">None</label><br>
														<div id="no_afn_inventory_transfer_role_blurb" class="perm_blurb">
															<i>This will turn off: Fulfillment Reports and Fulfillment Settings</i>
														</div></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[ssof_inventory_manager_role]" value="afn_inventory_transfer_editor" id="ssof_inventory_manager_role_edit"><label for="ssof_inventory_manager_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Manage Inventory/Add a Product:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[ezdpc_gui_inventory_role]" value="no_1_by_1_gui_role" id="ezdpc_gui_inventory_role_none" ><label for="ezdpc_gui_inventory_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio" name="rightMap[ezdpc_gui_inventory_role]" value="1_by_1_gui" id="ezdpc_gui_inventory_role_edit"><label for="ezdpc_gui_inventory_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Promotions:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[promotion_manager_role]" value="no_promotions_role" id="promotion_manager_role_none"><label for="promotion_manager_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[promotion_manager_role]" value="promotions_viewer" id="promotion_manager_role_view"><label for="promotion_manager_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[promotion_manager_role]" value="promotions_editor" id="promotion_manager_role_edit"><label for="promotion_manager_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Upload Inventory:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[item_manager_role]" value="no_item_manager_role" id="item_manager_role_none"><label for="item_manager_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[item_manager_role]" value="item_manager_viewer" id="item_manager_role_edit"><label for="item_manager_role_edit">View &amp; Edit</label></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td><table border="0" cellpadding="6" cellspacing="0">
							<tbody>
								<tr>
									<td height="40" align="left" valign="bottom" class="small"><strong>Advertising</strong></td>
								</tr>
							</tbody>
						</table>
						<table width="100%" cellspacing="0" cellpadding="0" align="center" bgcolor="#dddddd" border="0">
							<tbody>
								<tr>
									<td align="middle" valign="center"><table id="permissions-table" cellspacing="1" cellpadding="4" width="100%" border="0">
											<tbody>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Campaign Manager:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[nemo_campaign_mgr_role]" value="no_advertiser_campaign_role" id="nemo_campaign_mgr_role_none" ><label for="nemo_campaign_mgr_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[nemo_campaign_mgr_role]" value="advertiser_campaign_editor" id="nemo_campaign_mgr_role_edit"><label for="nemo_campaign_mgr_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Register for Sponsored Products:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio" name="rightMap[nemo_campaign_reg_role]" value="no_campaign_reg_role" id="nemo_campaign_reg_role_none" ><label for="nemo_campaign_reg_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[nemo_campaign_reg_role]" value="campaign_mgr_reg_viewer" id="nemo_campaign_reg_role_view"><label for="nemo_campaign_reg_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%"></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td><table border="0" cellpadding="6" cellspacing="0">
							<tbody>
								<tr>
									<td height="40" align="left" valign="bottom" class="small"><strong>Orders</strong></td>
								</tr>
							</tbody>
						</table>
						<table width="100%" cellspacing="0" cellpadding="0" align="center" bgcolor="#dddddd" border="0">
							<tbody>
								<tr>
									<td align="middle" valign="center"><table id="permissions-table" cellspacing="1" cellpadding="4" width="100%" border="0">
											<tbody>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Manage Orders:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[seller_your_account_role]" value="no_seller_ya_role" id="seller_your_account_role_none" ><label for="seller_your_account_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[seller_your_account_role]" value="seller_ya_viewer" id="seller_your_account_role_view"><label for="seller_your_account_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[seller_your_account_role]" value="seller_ya_editor" id="seller_your_account_role_edit"><label for="seller_your_account_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Manage Returns:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[manage_returns_role]" value="no_manage_returns_role" id="manage_returns_role_none" ><label for="manage_returns_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[manage_returns_role]" value="manage_returns_editor" id="manage_returns_role_edit"><label for="manage_returns_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Transactions:</b><br> <i>Enables scheduling and download of Order Reports, and upload of Adjustments and Shipping Confirmations.</i></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[transactions_role]" value="no_transactions_role" id="transactions_role_none" ><label for="transactions_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[transactions_role]" value="transactions_viewer" id="transactions_role_edit"><label for="transactions_role_edit">View &amp; Edit</label></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td><table border="0" cellpadding="6" cellspacing="0">
							<tbody>
								<tr>
									<td height="40" align="left" valign="bottom" class="small"><strong>Store Design</strong></td>
								</tr>
							</tbody>
						</table>
						<table width="100%" cellspacing="0" cellpadding="0" align="center" bgcolor="#dddddd" border="0">
							<tbody>
								<tr>
									<td align="middle" valign="center"><table id="permissions-table" cellspacing="1" cellpadding="4" width="100%" border="0">
											<tbody>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Your Info &amp; Policies:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[help_content_role]" value="no_help_content_role" id="help_content_role_none" ><label for="help_content_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[help_content_role]" value="help_content" id="help_content_role_edit"><label for="help_content_role_edit">View &amp; Edit</label></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td><table border="0" cellpadding="6" cellspacing="0">
							<tbody>
								<tr>
									<td height="40" align="left" valign="bottom" class="small"><strong>Amazon Payments Advanced</strong></td>
								</tr>
							</tbody>
						</table>
						<table width="100%" cellspacing="0" cellpadding="0" align="center" bgcolor="#dddddd" border="0">
							<tbody>
								<tr>
									<td align="middle" valign="center"><table id="permissions-table" cellspacing="1" cellpadding="4" width="100%" border="0">
											<tbody>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Manage Refunds:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[pyop_manage_refunds_role]" value="no_pyop_refund_role" id="pyop_manage_refunds_role_none" ><label for="pyop_manage_refunds_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[pyop_manage_refunds_role]" value="pyop_refund_editor" id="pyop_manage_refunds_role_edit"><label for="pyop_manage_refunds_role_edit">View &amp; Edit</label></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td><table border="0" cellpadding="6" cellspacing="0">
							<tbody>
								<tr>
									<td height="40" align="left" valign="bottom" class="small"><strong>Reports</strong></td>
								</tr>
							</tbody>
						</table>
						<table width="100%" cellspacing="0" cellpadding="0" align="center" bgcolor="#dddddd" border="0">
							<tbody>
								<tr>
									<td align="middle" valign="center"><table id="permissions-table" cellspacing="1" cellpadding="4" width="100%" border="0">
											<tbody>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Amazon Selling Coach:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[seller_coach_role]" value="no_seller_coach_role" id="seller_coach_role_none" ><label for="seller_coach_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio" name="rightMap[seller_coach_role]" value="seller_coach_viewer" id="seller_coach_role_view"><label for="seller_coach_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%"></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Business Reports, Sales Summary:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[site_metrics_role]" value="no_site_metrics_role" id="site_metrics_role_none" ><label for="site_metrics_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[site_metrics_role]" value="site_metrics_viewer" id="site_metrics_role_edit"><label for="site_metrics_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Customer Metrics:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[customer_experience_role]" value="no_cxm_viewer_role" id="customer_experience_role_none" ><label for="customer_experience_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[customer_experience_role]" value="cxm_viewer" id="customer_experience_role_view"><label for="customer_experience_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%"></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Feedback:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[feedback_manager_role]" value="no_feedback_role" id="feedback_manager_role_none" ><label for="feedback_manager_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[feedback_manager_role]" value="feedback_viewer" id="feedback_manager_role_view"><label for="feedback_manager_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio" name="rightMap[feedback_manager_role]" value="feedback_editor" id="feedback_manager_role_edit"><label for="feedback_manager_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Fulfillment Reports:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[ssof_report_viewer_role]" value="no_afn_report_viewer_role" id="ssof_report_viewer_role_none" ><label for="ssof_report_viewer_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[ssof_report_viewer_role]" value="afn_report_viewer" id="ssof_report_viewer_role_view"><label for="ssof_report_viewer_role_view">View</label><br>
														<div id="afn_report_viewer_blurb" class="perm_blurb">
															<i>This will turn on: Manage FBA Inventory/Shipments</i>
														</div></td>
													<td class="tiny" align="middle" valign="center" width="13%"></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Operations Report:</b><br> <i>A-to-z Guarantee Claims</i></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[seller_trust_metrics_role]" value="no_seller_metrics_role" id="seller_trust_metrics_role_none" ><label for="seller_trust_metrics_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[seller_trust_metrics_role]" value="seller_metrics_viewer" id="seller_trust_metrics_role_view"><label for="seller_trust_metrics_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%"></td>
												</tr>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Payments:</b><br> <i>Enables access to the Payments report and visibility of the home page Payments Summary.</i></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[manage_your_payments_role]" value="no_manage_your_payments_role" id="manage_your_payments_role_none"><label for="manage_your_payments_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[manage_your_payments_role]" value="manage_your_payments_editor" id="manage_your_payments_role_edit"><label for="manage_your_payments_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Product Ads Invoice History:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[nemo_transactions_role]" value="no_nemo_transactions_role" id="nemo_transactions_role_none"><label for="nemo_transactions_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[nemo_transactions_role]" value="nemo_transactions_viewer" id="nemo_transactions_role_view"><label for="nemo_transactions_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[nemo_transactions_role]" value="nemo_transactions_editor" id="nemo_transactions_role_edit"><label for="nemo_transactions_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Product Ads Performance Reports:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[nemo_performance_reports_role]" value="no_nemo_report_role" id="nemo_performance_reports_role_none" ><label for="nemo_performance_reports_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[nemo_performance_reports_role]" value="nemo_report_viewer" id="nemo_performance_reports_role_view"><label for="nemo_performance_reports_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%"></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Promotions Performance:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[promotions_performance_role]" value="no_promotions_performance_role" id="promotions_performance_role_none"><label for="promotions_performance_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[promotions_performance_role]" value="promotions_performance_viewer" id="promotions_performance_role_view"><label for="promotions_performance_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%"></td>
												</tr>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Tax Document Library:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[tax_library_role]" value="no_tax_lib_viewer_role" id="tax_library_role_none" ><label for="tax_library_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[tax_library_role]" value="tax_lib_viewer" id="tax_library_role_edit"><label for="tax_library_role_edit">View &amp; Edit</label></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td><table border="0" cellpadding="6" cellspacing="0">
							<tbody>
								<tr>
									<td height="40" align="left" valign="bottom" class="small"><strong>Settings</strong></td>
								</tr>
							</tbody>
						</table>
						<table width="100%" cellspacing="0" cellpadding="0" align="center" bgcolor="#dddddd" border="0">
							<tbody>
								<tr>
									<td align="middle" valign="center"><table id="permissions-table" cellspacing="1" cellpadding="4" width="100%" border="0">
											<tbody>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Fulfillment Settings:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[ssof_settings_editor_role]" value="no_afn_settings_editor_role" id="ssof_settings_editor_role_none" ><label for="ssof_settings_editor_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[ssof_settings_editor_role]" value="afn_settings_editor" id="ssof_settings_editor_role_edit"><label for="ssof_settings_editor_role_edit">View &amp; Edit</label><br>
														<div id="afn_settings_editor_blurb" class="perm_blurb">
															<i>This will turn on: Manage FBA Inventory/Shipments</i>
														</div></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Gift Options:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[gift_services_role]" value="no_gift_services_role" id="gift_services_role_none" ><label for="gift_services_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[gift_services_role]" value="gift_services_editor" id="gift_services_role_edit"><label for="gift_services_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Manage Your Cases:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[case_dashboard_role]" value="no_case_dashboard_role" id="case_dashboard_role_none" ><label for="case_dashboard_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[case_dashboard_role]" value="case_dashboard_viewer" id="case_dashboard_role_view"><label for="case_dashboard_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[case_dashboard_role]" value="case_dashboard_editor" id="case_dashboard_role_edit"><label for="case_dashboard_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Seller Configuration:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[seller_configuration_role]" value="no_seller_configuration_role" id="seller_configuration_role_none" ><label for="seller_configuration_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio" name="rightMap[seller_configuration_role]" value="seller_configuration_viewer" id="seller_configuration_role_view"><label for="seller_configuration_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio" name="rightMap[seller_configuration_role]" value="seller_configuration_editor" id="seller_configuration_role_edit"><label for="seller_configuration_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Shipping Settings:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio" name="rightMap[merchant_shipping_role]" value="no_merchant_shipping_role" id="merchant_shipping_role_none" ><label for="merchant_shipping_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[merchant_shipping_role]" value="merchant_shipping_viewer" id="merchant_shipping_role_view"><label for="merchant_shipping_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[merchant_shipping_role]" value="merchant_shipping_editor" id="merchant_shipping_role_edit"><label for="merchant_shipping_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Tax Settings:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[tax_manager_role]" value="no_tax_manager_role" id="tax_manager_role_none" ><label for="tax_manager_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio" name="rightMap[tax_manager_role]" value="tax_manager_editor" id="tax_manager_role_edit"><label for="tax_manager_role_edit">View &amp; Edit</label>
														<input type="hidden" value="rightMap[account_manager_role]" name="no_account_manager_role" />
														</td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td><table border="0" cellpadding="6" cellspacing="0">
							<tbody>
								<tr>
									<td height="40" align="left" valign="bottom" class="small"><strong>Media Upload</strong></td>
								</tr>
							</tbody>
						</table>
						<table width="100%" cellspacing="0" cellpadding="0" align="center" bgcolor="#dddddd" border="0">
							<tbody>
								<tr>
									<td align="middle" valign="center"><table id="permissions-table" cellspacing="1" cellpadding="4" width="100%" border="0">
											<tbody>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Image Uploading:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[image_uploader_role]" value="no_image_uploader_role" id="image_uploader_role_none" ><label for="image_uploader_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[image_uploader_role]" value="image_viewer" id="image_uploader_role_view"><label for="image_uploader_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[image_uploader_role]" value="image_editor" id="image_uploader_role_edit"><label for="image_uploader_role_edit">View &amp; Edit</label></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
				<tr>
					<td><table border="0" cellpadding="6" cellspacing="0">
							<tbody>
								<tr>
									<td height="40" align="left" valign="bottom" class="small"><strong>Internal/Administrative Tools</strong></td>
								</tr>
							</tbody>
						</table>
						<table width="100%" cellspacing="0" cellpadding="0" align="center" bgcolor="#dddddd" border="0">
							<tbody>
								<tr>
									<td align="middle" valign="center"><table id="permissions-table" cellspacing="1" cellpadding="4" width="100%" border="0">
											<tbody>
												<tr bgcolor="#f4f4f4">
													<td width="50%" align="right" valign="top"><b>Inventory Planning:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														 <input type="radio"  name="rightMap[inventory_planning_role]" value="no_inventoryplanning_role" id="inventory_planning_role_none"><label for="inventory_planning_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%">
														<input type="radio"  name="rightMap[inventory_planning_role]" value="inventoryplanning_viewer" id="inventory_planning_role_view"><label for="inventory_planning_role_view">View</label></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio" name="rightMap[inventory_planning_role]" value="inventoryplanning_editor" id="inventory_planning_role_edit"><label for="inventory_planning_role_edit">View &amp; Edit</label></td>
												</tr>
												<tr bgcolor="#f8f8f8">
													<td width="50%" align="right" valign="top"><b>Upgrade/Downgrade:</b></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio" name="rightMap[upgrade_downgrade_role]" value="no_upgrade_downgrade_role" id="upgrade_downgrade_role_none" ><label for="upgrade_downgrade_role_none">None</label></td>
													<td class="tiny" align="middle" valign="center" width="12%"></td>
													<td class="tiny" align="middle" valign="center" width="13%">
														<input type="radio"  name="rightMap[upgrade_downgrade_role]" value="upgrade_downgrade_editor" id="upgrade_downgrade_role_edit"><label for="upgrade_downgrade_role_edit">View &amp; Edit</label></td>
												</tr>
											</tbody>
										</table></td>
								</tr>
							</tbody>
						</table></td>
				</tr>
			</tbody>
		</table>
		<div class="form-actions" style="text-align: center;">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="授 权" />&nbsp;&nbsp;&nbsp;&nbsp; 
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>