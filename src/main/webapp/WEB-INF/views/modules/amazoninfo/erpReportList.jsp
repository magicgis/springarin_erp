<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统报表指南</title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			<%--float: right;--%>
			min-height: 40px
		}
		
		.spanexl {
			float: left;
		 }
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
			$("a[rel='popover']").popover({html:true,trigger:'hover'});
			
			$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : -1,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/amazoninfo/erpReport">系统报表指南</a></li>
	</ul>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>分类</th>
				<th>报表明细</th>
			</tr>
		</thead>
		<tbody>
			<%-- fba库存--%>
			<tr>
				<td>库存</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/stock/inventoryFba">FBA库存导出</a>
				</td>
			</tr>
			<tr>
				<td>库存</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/psiInventory/overStockProduct">产品积压导出</a>
				</td>
			</tr>
			<tr>
				<td>库存</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/psiInventory/outOfStockByProduct">产品断货导出</a>
				</td>
			</tr>
			<tr>
				<td>库存</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/psiInventory/inventoryWarn">库存预警导出(包含库存预警各维度统计、库存金额表、海外库存金额表以及历史库存金额表导出)</a>
				</td>
			</tr>
			<tr>
				<td>库存</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/inventoryAnalysis">按月库存分析导出</a>
				</td>
			</tr>
			<shiro:hasPermission name="psi:fbaInbound:view">
			<tr>
				<td>库存</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/fbaInbound">FBA贴导出</a>
				</td>
			</tr>
			</shiro:hasPermission>
			<shiro:hasPermission name="psi:all:view">
			<tr>
				<td>库存</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/psiInventory">本地仓库存导出(可区分是否含备品以及指定日期导出库存报表)</a>
				</td>
			</tr>
			<tr>
				<td>库存</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/amazonFbaHealthReport">FBA库存健康报表</a>
				</td>
			</tr>
			</shiro:hasPermission>
			<tr>
				<td>销量销售额</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/salesReprots">销量销售额导出(包含销售数据各维度统计、按年以及美国按州统计等)</a>
				</td>
			</tr>
			<tr>
				<td>销量销售额</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/salesForecastByMonth">预测销量导出(含普通导出和供应链导出)</a>
				</td>
			</tr>
			<shiro:hasPermission name="amazoninfo:results:export">
			<tr>
				<td>销量销售额</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/salesReprots/results">运营业绩报表</a>
				</td>
			</tr>
			</shiro:hasPermission>
			<%-- 采购订单报表--%>
			<shiro:hasPermission name="psi:order:view">
			<tr>
				<td>采购</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/lcPurchaseOrder">理诚采购订单导出</a>
				</td>
			</tr>
			</shiro:hasPermission>
			<shiro:hasPermission name="psi:ladingBill:view">
			<tr>
				<td>采购</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/lcPsiLadingBill">收货单导出</a>
				</td>
			</tr>
			</shiro:hasPermission>
			<shiro:hasPermission name="psi:rate:view">
			<tr>
				<td>采购</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/purchaseOrder/deliveryRate">供应商评估导出(包含交货延期率、产品合格率以及供应商合格率)</a>
				</td>
			</tr>
			</shiro:hasPermission>
			<tr>
				<td>采购</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/psiQuestionSupplier">供应商异常记录报表</a>
				</td>
			</tr>
			<tr>
				<td>邮件&事件</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/custom/emailManager/count">客邮统计导出</a>
				</td>
			</tr>
			<tr>
				<td>邮件&事件</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/custom/event">事件&TaxBill导出(税单以及按条件导出事件)</a>
				</td>
			</tr>
			<tr>
				<td>邮件&事件</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/custom/event/count">事件统计导出</a>
				</td>
			</tr>
			<tr>
				<td>邮件&事件</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/custom/productProblem">产品问题统计导出</a>
				</td>
			</tr>
			<tr>
				<td>产品信息</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/product">产品基本信息导出(含分平台属性、Check List、技术规格书)</a>
				</td>
			</tr>
			<tr>
				<td>产品信息</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/psiProductAttribute">产品分颜色属性导出(缓冲周期、运输方式、主力属性、采购周等)</a>
				</td>
			</tr>
			<tr>
				<td>产品信息</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/product/barcodeslist">产品条码导出</a>
				</td>
			</tr>
			<tr>
				<td>产品信息</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/salesReprots/panEuList">泛欧产品导出</a>
				</td>
			</tr>
			<tr>
				<td>产品信息</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/amazonProduct/list2">Amz产品信息导出(包含sku、asin、Fnsku、Ean、产品售价等)</a>
				</td>
			</tr>
			<shiro:hasPermission name="amazoninfo:order:view">
			<tr>
				<td>订单</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/order">亚马逊&Ebay订单导出</a>
				</td>
			</tr>
			<tr>
				<td>订单</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/unlineOrder">线下订单导出</a>
				</td>
			</tr>
			<tr>
				<td>订单</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/vendorOrder/list">Vendor订单导出</a>
				</td>
			</tr>
			<tr>
				<td>订单</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/returnGoods">退货订单导出</a>
				</td>
			</tr>
			<tr>
				<td>订单</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }	/amazoninfo/order/promotions">折扣订单导出</a>
				</td>
			</tr>
			<tr>
				<td>订单</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/returnGoods/differentialReturnCountList">退货&差评统计(统计产品退货和差评情况)</a>
				</td>
			</tr>
			<tr>
				<td>订单</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/salesReprots/getMaxOrder">大订单统计</a>
				</td>
			</tr>
			</shiro:hasPermission>
			<c:set var="flag" value="0"></c:set>
			<shiro:hasPermission name="amazon:order:expeu">
				<c:set var="flag" value="1"></c:set>
			</shiro:hasPermission>
			<shiro:hasPermission name="amazon:order:expall">
				<c:set var="flag" value="1"></c:set>
			</shiro:hasPermission>
			<c:if test="${'1' eq flag }">
			<tr>
				<td>订单</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/salesSummary">亚马逊订单按月汇总</a>
				</td>
			</tr>
			</c:if>
			<shiro:hasPermission name="amazon:spreadReport:view">
			<tr>
				<td>广告</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/amazonOperationalReport/spreadReportList">运营数据导出</a>
				</td>
			</tr>
			</shiro:hasPermission>
			<tr>
				<td>广告</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/advertising/adsAnalyse">广告关键词分析报表</a>
				</td>
			</tr>
			<tr>
				<td>广告</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/advertising">广告报表(支持按天、按周、按月导出)</a>
				</td>
			</tr>
			<tr>
				<td>广告</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/amazoninfo/advertising/listByDate">广告费用统计报表</a>
				</td>
			</tr>
			<shiro:hasPermission name="psi:transport:view">
			<tr>
				<td>物流</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/lcPsiTransportOrder">运单报表</a>
				</td>
			</tr>
			</shiro:hasPermission>
			<tr>
				<td>物流</td>
				<td>
					&nbsp;<a target="_blank" href="${ctx }/psi/psiTransportPayment/byMonthInfo">物流报表&运输分析</a>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>
