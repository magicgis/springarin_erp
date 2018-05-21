<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Reviewer Form</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/raty-master/lib/jquery.raty.js" ></script>
	<style>
		.rating-star {
			width: 0;
			margin: 0;
			padding: 0;
			border: 0;
		}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			if(!(top)){
				top = self; 
			}
			$("#inputForm").validate();
			
			$("#reviewerId").change(function() {
				var id = $("#reviewerId").val();
				var key = '${reviewer.country}';
				if(key == "jp"){
					key = "co.jp";
				}
				if(key == "mx"){
					key = "com.mx";
				}
				$("#reviewerHome").attr("href","http://www.amazon."+key+"/gp/pdp/profile/"+id);
			});
			
			$("#star1").raty({ score:'${empty reviewer.star?0:reviewer.star}',
				path:'${ctxStatic}/raty-master/lib/images',
				click: function(score, evt) {
			    var param = {};
				var customerId = '${reviewer.id}';
				if(customerId == null || customerId == ""){
					$("#star").val(score);
					return;
				}
				param.id = customerId;
				param.star = score;
				if(customerId){
					$.get("${ctx}/amazoninfo/reviewer/saveStar?"+$.param(param),function(data){
						if($.isNumeric(data)){
							$.jBox.tip("设置客户星级成功！", 'info',{timeout:1000});
						}
					});
				}
			}});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/reviewer/">评测客户汇总</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/reviewer/form?id=${reviewer.id}">${empty reviewer.id?"添加":"修改"}评测人</a></li>
	</ul>
	<div class="form-actions">
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
	<form:form id="inputForm" modelAttribute="reviewer" action="${ctx}/amazoninfo/reviewer/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">姓名:</label>
			<div class="controls">
				<c:if test="${not empty reviewer.id }">
					${reviewer.name }
				</c:if>
				<c:if test="${empty reviewer.id }">
					<form:input path="name" htmlEscape="false" maxlength="30" class="required name"/>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">国家:</label>
			<div class="controls">
				<c:if test="${'0' eq reviewer.reviewerType }">
					${fns:getDictLabel(reviewer.country,'platform','other')}
				</c:if>
				<c:if test="${'1' eq reviewer.reviewerType }">
					<form:select path="country">
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek'}">
								<option value="${dic.value}">${dic.label}</option>
							</c:if>
						</c:forEach>	
					</form:select>
				</c:if>
			</div>
		</div>
		<c:if test="${'1' eq reviewer.reviewerType }">
			<div class="control-group">
			<label class="control-label">客户来源:</label>
			<div class="controls">
				<form:select path="sourcePlatform" class="required">
					<form:option value="" label=""/>
					<form:option value="Amazon" label="Amazon"/>
					<form:option value="Youtube" label="Youtube"/>
					<form:option value="Twitter" label="Twitter"/>
					<form:option value="Facebook" label="Facebook"/>
					<form:option value="other" label="other"/>
				</form:select>
			</div>
		</div>
		</c:if>
		<c:if test="${'0' eq reviewer.reviewerType && not empty reviewer.id}">
		<c:set var="key" value="${reviewer.country }"/>
		<c:if test="${'jp' eq reviewer.country || 'uk' eq reviewer.country }">
			<c:set var="key" value="co.${reviewer.country }"/>
		</c:if>
		<c:if test="${'mx' eq reviewer.country}">
			<c:set var="key" value="com.${reviewer.country }"/>
		</c:if>
		<div class="control-group">
			<label class="control-label">客户ID:</label>
			<div class="controls">
				<form:input path="reviewerId" htmlEscape="false" maxlength="50"/>
				&nbsp;&nbsp;
				<a id="reviewerHome" target="_blank" href="http://www.amazon.${key}/gp/pdp/profile/${reviewer.reviewerId }">查看</a>
			</div>
		</div>
		</c:if>
		<c:if test="${empty reviewer.id }">
			<div class="control-group">
				<label class="control-label">客户ID:</label>
				<div class="controls">
					<form:input path="reviewerId" htmlEscape="false" maxlength="50"/><span style="color:red">&nbsp;(亚马逊客户ID,选填)</span>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">客户类型:</label>
			<div class="controls">
				<c:if test="${not empty reviewer.id }">
					<c:if test="${'0' eq reviewer.reviewerType }">站内</c:if>
					<c:if test="${'1' eq reviewer.reviewerType }">站外</c:if>
				</c:if>
				<c:if test="${empty reviewer.id }">
					<form:select path="reviewerType">
						<form:option value="0" label="站内"/>
						<form:option value="1" label="站外"/>
					</form:select>
				</c:if>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">VineVoice群体:</label>
			<div class="controls">
				<form:select path="isVineVoice">
					<form:option value="0" label="否"/>
					<form:option value="1" label="是"/>
				</form:select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">评测邮箱:</label>
			<div class="controls">
				<c:if test="${not empty reviewer.id }">
					<form:input path="reviewEmail" />
					<c:if test="${not empty reviewer.reviewEmail }">
						&nbsp;&nbsp;
						<a href="${ctx}/amazoninfo/reviewer/sendEmail?email=${reviewer.reviewEmail }">联系客户</a>
					</c:if>
				</c:if>
				<c:if test="${empty reviewer.id }">
					<form:input path="reviewEmail" htmlEscape="false" maxlength="100" style="width: 60%" class="email"/>
				</c:if>
			</div>
		</div>
		<c:if test="${'0' eq reviewer.reviewerType }">
		<div class="control-group">
			<label class="control-label">亚马逊排名:</label>
			<div class="controls">
				<form:input path="rank" htmlEscape="false" maxlength="100" style="width: 60%"/>
			</div>
		</div>
		</c:if>
		<c:if test="${not empty reviewer.id }">
		<div class="control-group">
			<label class="control-label">联系次数:</label>
			<div class="controls">
				${fn:length(reviewer.comments)}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">最后联系时间:</label>
			<div class="controls">
				<fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${reviewer.comments[fn:length(reviewer.comments)-1].updateDate}"/>
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">客户评分:</label>
			<div class="controls">
				<c:if test="${empty reviewer.id }">
					<form:input path="star" type="hidden" value=""/>
				</c:if>
				<div id="star1"></div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址:</label>
			<div class="controls">
				<form:input path="address" htmlEscape="false" maxlength="100" style="width: 60%"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">个人邮箱1:</label>
			<div class="controls">
				<form:input path="email1" htmlEscape="false" maxlength="100" style="width: 60%" class="email"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">个人邮箱2:</label>
			<div class="controls">
				<form:input path="email2" htmlEscape="false" maxlength="100" style="width: 60%" class="email"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Amazon:</label>
			<div class="controls">
				<form:input path="amaUrl" htmlEscape="false" maxlength="100" style="width: 60%" class="url"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Youtube:</label>
			<div class="controls">
				<form:input path="youtubeUrl" htmlEscape="false" maxlength="100" style="width: 60%" class="url"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Twitter:</label>
			<div class="controls">
				<form:input path="twitterUrl" htmlEscape="false" maxlength="100" style="width: 60%" class="url"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Facebook:</label>
			<div class="controls">
				<form:input path="sitefbUrl" htmlEscape="false" maxlength="100" style="width: 60%" class="url"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">其他网址:</label>
			<div class="controls">
				<form:input path="otherUrl" htmlEscape="false" maxlength="100" style="width: 60%" class="url"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>