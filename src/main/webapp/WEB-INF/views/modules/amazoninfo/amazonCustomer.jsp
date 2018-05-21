<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>客户详情</title>
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
			"ordering": false
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
				<table class="table table-striped table-bordered table-condensed">
					<tbody>
						<tr>
							<td>头像</td>
							<td>姓名</td>
							<td>Amz_Email</td>
						</tr>
						<tr>
							<td rowspan="6" style="text-align: center;vertical-align: middle;"><img src="${ctxStatic}/common/customer.png" width="75" height="75"></td>
							<td>${customer.name}</td>
							<td>${customer.amzEmail}<a href="${ctx}/custom/sendEmail/form?sendEmail=${customer.amzEmail}" >联系客户</a></td>
						</tr>
						<tr>
							<td>平台</td>
							<td>Email(格式:A,B,C)</td>
						</tr>
						<tr>
							<td>${fns:getDictLabel(customer.country,'platform','')}</td>
							<td>
								<a href="#"  data-type="text" data-pk="1"  data-title="Fill in Email" class="editable editable-click editor" data-original-title="" title="">${customer.encryptionEmail}</a>
								<c:if test="${not empty customer.email}">
									<a href="${ctx}/custom/sendEmail/form?sendEmail=${customer.email}" >联系客户</a>
								</c:if>
							</td>
						</tr>
					</tbody>
				</table>
				<table class="table table-striped table-bordered table-condensed">
					<tbody>
						<tr class="info">
							<td colspan="4">客户足迹</td>
						</tr>
						<tr>
							<td>客户星级</td>
							<td colspan="3">
								<div id="star"></div>
							</td>
						</tr>
						<tr>
							<td>BuyerID</td>
							<td><a target="_blank" href="${customer.link}">${customer.customerId}</a></td>
							<td>购买次数</td>
							<td>${customer.buyTimes}</td>
						</tr>
						<tr>
							<td>购买数</td>
							<td>${customer.buyQuantity}</td>
							<td>退货数</td>
							<td>${customer.returnQuantity}</td>
						</tr>
						<tr>
							<td>第一次购买</td>
							<td>${customer.firstBuyDate}</td>
							<td>最近购买</td>
							<td>${customer.lastBuyDate}</td>
						</tr>
						<tr>
							<td>事件</td>
							<td colspan='3'>
                                 <c:if test="${not empty customer.eventId }">
                                      <c:forEach items="${fn:split(customer.eventId,',')}" var="event" varStatus="i">
                                           <a target='_blank' href="${ctx}/custom/event/form?id=${event}">SPR-${event }</a>&nbsp;&nbsp;
                                      </c:forEach>
                                 </c:if>
							</td>
						</tr>
						<%-- <tr>
							<td>邮件</td>
							<td colspan='3'>
                                 <c:if test="${not empty emailMap }">
                                      <c:forEach items="${emailMap}" var="email" varStatus="i">
                                           <a target='_blank' href="${ctx}/custom/emailManager/view?id=${email.key}">${emailMap[email.key]}</a><br/>
                                      </c:forEach>
                                 </c:if>
							</td>
						</tr> --%>
					</tbody>
				</table>
				
					<table class="table table-striped table-bordered table-condensed " id="emailTable">
					<thead>
							<tr class="warning" style="background-color: #fcf8e3;"><td colspan='3'>邮件</td>	</tr>  
							<tr><td>时间</td>	<td>主题</td><td>负责人</td></tr>
					</thead>
					<c:if test="${not empty sendEmailMap}">
					     <tr class="warning" style="background-color: #fcf8e3;"><td colspan='3'>主动发送邮件</td>	</tr>  
					     <c:forEach items="${sendEmailMap}" var="sendEmail" varStatus="i">
					          <tr>
							     <td><div class="cir Rh"></div><div class="cij${not empty sendEmailMap[sendEmail.key].sendAttchmentPath?' Ju':''}"></div>${sendEmailMap[sendEmail.key].sentDate }</td>
								 <td><a target='_blank' href="${ctx}/custom/sendEmail/view?id=${sendEmail.key}">${sendEmailMap[sendEmail.key].sendSubject }</a></td>
								 <td>${sendEmailMap[sendEmail.key].sendContent }</td>
						     </tr>
					     </c:forEach>
					</c:if>
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
				
				<table class="table table-striped table-bordered table-condensed">
				<tbody>
					<tr class="warning">
						<td colspan='10'>客户相关评论</td>
					</tr>
					<tr>
						<td width="40px">序号</td>
						<td width="150px">评论产品</td>
						<td width="100px">评分</td>
						<td width="100px">评论链接</td>
						<td>评论主题</td>
						<td width="180px">评论时间</td>
					</tr>
					<c:set var="temp" value="${customer.reviewProductName}" />
					<c:forEach var="review" items="${customer.reviewComments}" varStatus="i">
						<tr>
							<td width="40px">${i.count}</td>
							<td width="150px">${empty customer.reviewProductName[review.asin]?review.asin:customer.reviewProductName[review.asin]}</td>
							<td width="100px">${review.star}</td>
							<td width="100px"><a href="${review.link}" target="_blank">${review.link}</a></td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${review.subject}">
									${fns:abbr(review.subject,30) }	
								</a>
							</td>
							<td width="180px">${fns:getDateByPattern(review.reviewDate,'yyyy-MM-dd')}</td>
						</tr>
					</c:forEach>
				</tbody>
				</table>
				<table class="table table-striped table-bordered table-condensed example">
					<tbody>
						<tr class="success">
							<td colspan="8">购买记录</td>
						</tr>
						<tr>
							<td>序号</td>
							<td>Order ID</td>
							<td>下单时间</td>
							<td>产品名</td>
							<td>Asin</td>
							<td>Sku</td>
							<td colspan="2">数量</td>
						</tr>
						<c:forEach var="comment" items="${customer.buyCommentTemp}" varStatus="i">
						<tr>
							<td>${i.count}</td>
							<td><a target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${comment.orderId}">${comment.orderId}</a></td>
							<td>${comment.typeDate}</td>
							<td>${comment.productName}</td>
							<td>${comment.asin}</td>
							<td>${comment.sku}</td>
							<td colspan="2">${comment.quantity}</td>
						</tr>	
						</c:forEach>
						<tr class="error">
							<td colspan="8">退货记录</td>
						</tr>
						<tr>
							<td>序号</td>
							<td>Order ID</td>
							<td>退货时间</td>
							<td>产品名</td>
							<td>Asin</td>
							<td>Sku</td>
							<td>数量</td>
							<td>原因</td>
						</tr>
						<c:forEach var="comment" items="${customer.returnCommentTemp}" varStatus="i">
						<tr>
							<td>${i.count}</td>	
							<td><a target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${comment.orderId}">${comment.orderId}</a></td>
							<td>${comment.typeDate}</td>
							<td>${comment.productName}</td>
							<td>${comment.asin}</td>
							<td>${comment.sku}</td>
							<td>${comment.quantity}</td>
							<td>${comment.remark}</td>
						</tr>	
						</c:forEach>
						<tr class="success">
							<td colspan="8">售后邮件记录</td>
						</tr>
						<tr>
							<td>序号</td>
							<td>发送时间</td>
							<td colspan="3">主题</td>
							<td colspan="3">发送邮箱</td>
						</tr>
						<c:forEach var="comment" items="${customer.comments}" varStatus="i">
						<c:if test="${'1' eq comment.sendFlag}">
							<tr>
								<td>${i.count}</td>
								<td><fmt:formatDate pattern="yyyy-MM-dd" value="${comment.sentDate}"/></td>
								<td colspan="3"><a target="_blank" href="${ctx}/amazoninfo/afterSale/viewSendInfo?id=${comment.id}">${comment.sendSubject}</a></td>
								<td colspan="3">${comment.sendEmail}</td>
							</tr>
						</c:if>	
						</c:forEach>
						
						<tr class="success">
							<td colspan="8">退款记录</td>
						</tr>
						<tr>
							<td>序号</td>
							<td>Order ID</td>
							<td>退款时间</td>
							<td>产品名</td>
							<td>Asin</td>
							<td>Sku</td>
							<td>金额</td>
							<td>原因</td>
						</tr>
						<c:forEach var="comment" items="${customer.refundCommentTemp}" varStatus="i">
						<tr>
							<td>${i.count}</td>	
							<td><a target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${comment.orderId}">${comment.orderId}</a></td>
							<td>${comment.typeDate}</td>
							<td>${comment.productName}</td>
							<td>${comment.asin}</td>
							<td>${comment.sku}</td>
							<td>${comment.money}</td>
							<td><a title='${comment.remark}'>${fns:abbr(comment.remark,20)}</a></td>
						</tr>	
						</c:forEach>
						
						<tr class="error">
							<td colspan="8">替代记录</td>
						</tr>
						<tr>
							<td>序号</td>
							<td>Order ID</td>
							<td>替代时间</td>
							<td>产品名</td>
							<td>Asin</td>
							<td>Sku</td>
							<td>数量</td>
							<td>原因</td>
						</tr>
						<c:forEach var="comment" items="${customer.supportCommentTemp}" varStatus="i">
						<tr>
							<td>${i.count}</td>	
							<td>
							  <a href="${ctx}/amazoninfo/amazonTestOrReplace/view?sellerOrderId=${comment.orderId}" target="_blank">${comment.orderId}</a>
							</td>
							<td>${comment.typeDate}</td>
							<td>${comment.productName}</td>
							<td>${comment.asin}</td>
							<td>${comment.sku}</td>
							<td>${comment.quantity}</td>
							<td><a title='${comment.remark}'>${fns:abbr(comment.remark,20)}</a></td>
						</tr>	
						</c:forEach>
					</tbody>
				</table>

			</div>
		</div>
		<!-- content -->
		<div class="clear"></div>
	</div>

</body>
</html>
