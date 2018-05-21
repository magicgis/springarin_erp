<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>

<body>
	<ul class="nav nav-tabs">
		<li class="active"><a class="typeHref" href="#" key="">客户汇总</a></li>
		<li><a class="typeHref" href="#" key="0">站内客户</a></li>
		<li><a class="typeHref" href="#" key="1">站外客户</a></li>
	</ul>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${reviewer.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
	</ul>
	<form:form id="searchForm" modelAttribute="reviewer" action="${ctx}/amazoninfo/reviewer/totalList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input name="country" type="hidden" value="${reviewer.country}"/>
		<div style="line-height: 40px">
			<div >
			<label>评测人类型:</label>
			<form:select path="reviewerType" onchange="changeReviewerType()">
				<form:option value="" label="---All---"/>
				<form:option value="0" label="站内"/>
				<form:option value="1" label="站外"/>
			</form:select>
			<label>客户来源:</label>
			<form:select path="sourcePlatform" onchange="changeReviewerType()">
				<form:option value="" label="---All---"/>
				<form:option value="Amazon" label="Amazon"/>
				<form:option value="Youtube" label="Youtube"/>
				<form:option value="Twitter" label="Twitter"/>
				<form:option value="Facebook" label="Facebook"/>
				<form:option value="other" label="other"/>
			</form:select>
			</div>
			<label>姓名:</label>
			<form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
			<label>邮箱:</label>
			<form:input path="reviewEmail" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;
			<input type="checkbox" name="aboutMe" id="aboutMe" value="1" ${aboutMe eq '1'?'checked':''}/><spring:message code='custom_email_btn3'/>
			&nbsp;
			<input type="checkbox" name="isVineVoice" id="isVineVoice" value="1" ${isVineVoice eq '1'?'checked':''}/>Vine Voice
			&nbsp;
			<input class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			&nbsp;
			<input id="addReviewer" class="btn btn-primary" type="button" value="添加评测人"/>
			&nbsp;
			<input id="bathSendEmail" class="btn btn-primary" type="button" value="群发邮件"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<th style="width: 20px">
			<span>
				<input type="checkbox">
				</span>
		</th>
		<th class="sort name">姓名</th>
		<th class="sort reviewerType">类型</th>
		<th class="sort star">客户评分</th>
		<th class="sort reviewEmail">客户邮箱</th>
		<th class="sort sourcePlatform">客户来源</th>
		<th class="sort updateDate">更新时间</th>
		<th class="sort contactBy">最后联系人</th>
		<th>最后联系时间</th>
		<th class="sort comments">联系次数</th>
		<th ><spring:message code="sys_label_tips_operate"/></th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="reviewer">
			<tr>
				<td>
					<div class="checker">
					<span>
						<c:if test="${not empty reviewer.reviewEmail}">
						  <input type="checkbox"/>
						  <input type="hidden" value="${reviewer.reviewEmail}" class="reviewerId"/>
						</c:if>
					</span>
					</div>
				</td>
				<td>${reviewer.name} 
				<c:if test="${reviewer.isVineVoice eq '1'}"><span class="pr-c7y-badge a-text-bold a-color-link">Vine Voice</span></c:if>
				</td>
				<td>
					<c:if test="${reviewer.reviewerType eq '0' }">站内</c:if>
					<c:if test="${reviewer.reviewerType eq '1' }">站外</c:if>
				</td>
				<td>
					<input type="hidden" value="${reviewer.id}" />
					<a href="#" class="dateEditor"  data-type="number" data-length="1" data-pk="1" data-title="Enter">
					<c:if test="${not empty reviewer.star}">${reviewer.star}</c:if></a>
				 </td>
				<td><a href="${ctx}/amazoninfo/reviewer/sendEmail?email=${reviewer.reviewEmail}">${reviewer.reviewEmail}</a></td>
				<td>${empty reviewer.sourcePlatform?'Amazon':reviewer.sourcePlatform}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${reviewer.updateDate}"/></td>
				<td>${reviewer.contactBy.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${reviewer.comments[fn:length(reviewer.comments)-1].updateDate}"/></td>
				<td>${fn:length(reviewer.comments)}</td>
				<td>
					<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/reviewer/form?id=${reviewer.id}">编辑</a>
					<a class="btn btn-warning btn-small" href="${ctx}/amazoninfo/reviewer/sendEmail?email=${reviewer.reviewEmail}">发送邮件</a>
					<c:if test="${'1' eq reviewer.reviewerType }">
						<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/reviewer/records?id=${reviewer.id}">联系记录</a>
					</c:if>
					<c:if test="${'0' eq reviewer.reviewerType }">
						<div class="btn-group">
							<button type="button" class="btn btn-success" >记录</button>
							<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
								<span class="caret"></span>
								<span class="sr-only"></span>
							</button>
							<ul class="dropdown-menu" id="allExport">
								<li><a href="${ctx}/amazoninfo/reviewer/records?id=${reviewer.id}">联系记录</a></li>
								<li><a href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${reviewer.id}">评测记录</a></li>
							</ul>
						</div>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
<head>
	<title>Reviewer manager</title>
	<meta name="decorator" content="default">
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<link href="${ctxStatic}/common/mailstate.css" type="text/css" rel="stylesheet">
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
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
			$(".countryHref").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/reviewer/totalList?');
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$(".typeHref").click(function(){
				var key = $(this).attr("key");
				if(key == '0' || key == '1'){
					$('#searchForm').attr('action','${ctx}/amazoninfo/reviewer?');
					$("#reviewerType").val(key);
					$("#searchForm").submit();
				} else {
					$('#searchForm').attr('action','${ctx}/amazoninfo/reviewer/totalList?');
					$("#reviewerType").val('');
					$("#searchForm").submit();
				}
			});
			
			$(".dateEditor").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response, newValue){
					if(newValue != '1' && newValue != '2' && newValue != '3' &&
							newValue != '4' && newValue != '5'){
						$.jBox.tip("客户评分只能为整数,范围为1~5！", 'info',{timeout:3000});
						return false;
					}
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(":hidden").val();
					param.star = newValue;
					$.get("${ctx}/amazoninfo/reviewer/addStar?"+$.param(param),function(data){
						if(!(data)){
							$this.text(oldVal);
						}else{
							$.jBox.tip("保存评分成功！", 'info',{timeout:2000});
						}
					});
					return true;
				}
			});
			
			$("#addReviewer").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/reviewer/form');
				$("#searchForm").submit();
			});
			
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
			
			$("#aboutMe,#isVineVoice").change(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/reviewer/totalList");
				$("#searchForm").submit();
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
	
			$("#bathSendEmail").click(function(){
				if($(".checked :hidden").size()){
					var params = {};
					params.emails = [];
					$(".checked :hidden").each(function(){
						params.emails[params.emails.length] = $(this).val();
					});
					window.open("${ctx}/amazoninfo/reviewer/batchSendEmail?"+$.param(params)); 
					//window.location.href = "${ctx}/amazoninfo/reviewer/batchSendEmail?"+$.param(params);
				}else{
					top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
				}
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
		
		function changeReviewerType(){
			$("#searchForm").submit();
		}
		
	</script>
	<style>
		.pr-c7y-badge {
		    border-bottom: 1px dashed #a9a9a9;
		    border-top: 1px dashed #a9a9a9;
		    color: #004b91;
		    display: inline-block;
		    font-size: 9px;
		    font-weight: bold;
		    letter-spacing: 0.5px;
		    line-height: 1.6;
		    margin-bottom: 6px;
		    margin-right: 8px;
		    padding: 0;
		    text-transform: uppercase;
		    white-space: nowrap;
		}
		.a-text-bold {
		    font-weight: 700 !important;
		}
		.a-color-link {
		    color: #0066c0 !important;
		}
	</style>
</head></body>
</html>
