<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>采购订单资金视图</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
			
			$(".open").click(function(e){
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			$("#createForecastData").click(function(){
				if('${endMonthFlag}'=="1"){   
					top.$.jBox.confirm("<span style='color:red'><b>月底三天内预测数据浮动较大，是否生成预测数据</b></span>", "Tips", function(v, h, f){
						  if (v == 'ok'){   
								window.location.href="${ctx}/psi/forecastOrder/generateOrder";
								top.$.jBox.tip("正在生成订单请耐心等待", 'loading');
						  }
					});
				}else{
					top.$.jBox.confirm('您确定要申请下单吗','系统提示',function(v,h,f){
						if(v=='ok'){
							window.location.href="${ctx}/psi/forecastOrder/generateOrder";
							top.$.jBox.tip("正在生成订单请耐心等待", 'loading');
						}
					});
				}
			});
			
			//备货下单
			$("#createStockUpData").click(function(){
				if('${endMonthFlag}'=="1"){
					top.$.jBox.confirm("<span style='color:red'><b>月底三天内预测数据浮动较大，是否生成预测数据</b></span>", "Warning", function(v, h, f){
						  if (v == 'ok'){
							  top.$.jBox.confirm("<input style='width: 160px' readonly='readonly' class='Wdate' type='text' onclick=WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'%y-{%M+1}-%d',maxDate:'%y-{%M+11}-30'}); />", "选择备货时间", function(v, h, f){
								  if (v == 'ok'){
									  	var date = h.find("input").val();
									  	if(date){
									  		window.location.href="${ctx}/psi/forecastOrder/generateOrder?date="+date;
											top.$.jBox.tip("正在生成订单请耐心等待", 'loading');
									  	}else{
									  		top.$.jBox.tip("请选择备货时间", 'info',{timeout:1500});
									  		return false;
									  	}
								  }
							});
						  }
					});
				}else{
					 top.$.jBox.confirm("<input style='width: 160px' readonly='readonly' class='Wdate' type='text' onclick=WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'%y-{%M+1}-%d',maxDate:'%y-{%M+11}-30'}); />", "选择备货时间", function(v, h, f){
						  if (v == 'ok'){
							  	var date = h.find("input").val();
							  	if(date){
							  		window.location.href="${ctx}/psi/forecastOrder/generateOrder?date="+date;
									top.$.jBox.tip("正在生成订单请耐心等待", 'loading');
							  	}else{
							  		top.$.jBox.tip("请选择备货时间", 'info',{timeout:1500});
							  		return false;
							  	}
						  }
					});
				  }
			});
			
			
			
			$("#orderSta").change(function(){
				$("#searchForm").submit();
			})
		});
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">预测生成订单数据列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="forecastOrder" action="${ctx}/psi/forecastOrder/list" method="post" class="breadcrumb form-search" cssStyle="height: 40px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${forecastOrder.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${forecastOrder.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				
			<label>订单状态：</label>
			<form:select path="orderSta" style="width: 200px" id="orderSta">
				<option value="" >全部(非取消)</option>
				<option value="1" ${forecastOrder.orderSta eq '1' ?'selected':''} >草稿</option>
				<option value="3" ${forecastOrder.orderSta eq '3' ?'selected':''} >待超标审核</option>
				<option value="4" ${forecastOrder.orderSta eq '4' ?'selected':''} >待终极审核</option>
				<option value="5" ${forecastOrder.orderSta eq '5' ?'selected':''} >已终极审核</option>
				<option value="8" ${forecastOrder.orderSta eq '8' ?'selected':''} >已取消</option>
			</form:select>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			<shiro:hasAnyPermissions name="psi:forecastOrder:edit,psi:forecastOrder:review,psi:forecastOrder:salesReview">
			&nbsp;&nbsp;&nbsp;&nbsp;
				<input id="createForecastData" class="btn btn-warning" type="button" value="申请下单"/>
				<input id="createStockUpData" class="btn btn-success" type="button" value="备货下单"/>
				</shiro:hasAnyPermissions>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>   
	
	
	
	<table id="treeTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th width="5%">序号</th>
				<th width="10%">订单状态</th>
				<th width="10%">创建日期</th>
				<th width="10%">预测总数</th>
				<th width="10%">下单总数</th>
				<th width="10%">类型</th>
				<th width="100px">审核人</th>
				<th width="100px">审核日期</th>
				<th width="100px">备货日期</th>
				<th >操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="forecastOrder" varStatus="i">
			<tr >
				<td>${forecastOrder.id}</td>
				<td align="center">
					<c:if test="${forecastOrder.orderSta eq '1'}"><span class="label label-important">草稿</span></c:if>
					<c:if test="${forecastOrder.orderSta eq '2'}"><span class="label label-important">待供应链初审</span></c:if>
					<c:if test="${forecastOrder.orderSta eq '3'}"><span class="label label-warning">待超标审核</span></c:if>
					<c:if test="${forecastOrder.orderSta eq '4'}"><span class="label label-warning">待终极审核</span></c:if>
					<c:if test="${forecastOrder.orderSta eq '5'}"><span class="label  label-success">已审核</span></c:if>
					<c:if test="${forecastOrder.orderSta eq '8'}"><span class="label  label-inverse">已取消</span></c:if>
				</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${forecastOrder.createDate}"/>(WK${fns:getDateByPattern(forecastOrder.createDate,"w")})</td>
				<td>${forecastOrder.forecastTotal}</td><td>${forecastOrder.orderTotal}</td>
				<td>
					<c:if test="${'0' eq forecastOrder.type }">非新品</c:if>
					<c:if test="${'1' eq forecastOrder.type }">新品</c:if>
				</td>
				<td>${forecastOrder.reviewUser.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${forecastOrder.reviewDate}"/></td>
				<td style="color:red"><fmt:formatDate pattern="yyyy-MM-dd" value="${forecastOrder.targetDate}"/></td>
				<td>
					<c:if test="${forecastOrder.orderSta eq '1' && '1' eq forecastOrder.type}">
						<shiro:hasPermission name="psi:forecastOrder:salesReview">
						<a class="btn btn-small"  href="${ctx}/psi/forecastOrder/review?id=${forecastOrder.id}">审核</a>
						</shiro:hasPermission>
					</c:if>
					<c:if test="${forecastOrder.orderSta eq '1' && '0' eq forecastOrder.type}">
						<shiro:hasPermission name="psi:forecastOrder:review">
						<a class="btn btn-small"  href="${ctx}/psi/forecastOrder/review?id=${forecastOrder.id}">初级审核</a>
						</shiro:hasPermission>
					</c:if>
					<c:if test="${forecastOrder.orderSta eq '3'}">
						<shiro:hasPermission name="psi:forecastOrder:overReview">
						<a class="btn btn-small"  href="${ctx}/psi/forecastOrder/overReview?id=${forecastOrder.id}">超标审核</a>
						</shiro:hasPermission>
					</c:if>
					<c:if test="${forecastOrder.orderSta eq '4'}">
						<shiro:hasPermission name="psi:forecastOrder:bossReview">
						<a class="btn btn-small"  href="${ctx}/psi/forecastOrder/bossReview?id=${forecastOrder.id}">终极审核</a>
						</shiro:hasPermission>
					</c:if>
					<c:if test="${forecastOrder.orderSta eq '1'}">
						<shiro:hasPermission name="psi:forecastOrder:edit">
						<a class="btn btn-small"  href="${ctx}/psi/forecastOrder/edit?id=${forecastOrder.id}">编辑</a>
						</shiro:hasPermission>
					</c:if>
					<c:if test="${forecastOrder.orderSta ne '3'&&forecastOrder.orderSta ne '5'&&forecastOrder.orderSta ne '8'}">
						<shiro:hasPermission name="psi:forecastOrder:review">
						<a class="btn btn-small"  href="${ctx}/psi/forecastOrder/cancel?id=${forecastOrder.id}" onclick="return confirmx('确认要取消该预测数据吗？', this.href)">取消</a>
						</shiro:hasPermission>
					</c:if>
					<shiro:hasPermission name="psi:forecastOrder:view">
					<a class="btn btn-small"  href="${ctx}/psi/forecastOrder/view?id=${forecastOrder.id}">查看</a>
					<a class="btn btn-small"  href="${ctx}/psi/forecastOrder/exportSingle?forecastOrderId=${forecastOrder.id}">导出</a>
					</shiro:hasPermission>
				</td>
			</tr>
			<c:if test="${fn:length(forecastOrder.items)>0}">
				<tr style="background-color:#ECF5FF;display: none" name="${order.id}">
				<td></td><td>产品名</td><td>国家</td><td>颜色</td><td>预测数</td><td>下单数</td><td>备注</td><td>审核备注</td><td></td></tr>
				<c:forEach items="${forecastOrder.items}" var="item">
					<tr style="background-color:#ECF5FF;display: none" name="${order.id}" >
					<td></td>
					<td>${item.productName}</td>
					<td>${fns:getDictLabel(item.countryCode,'platform', '')}</td>
					<td>${item.colorCode}</td>
					<td>${item.forecast1week eq '0'?item.forecast4week:item.forecast1week}</td>
					<td>${item.quantity}</td>
					<td>${item.remark}</td>
					<td>${item.reviewRemark}</td>
					<td></td>
					</tr>
				</c:forEach>   
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
