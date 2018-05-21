<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Reviewer Records</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/raty-master/lib/jquery.raty.js" ></script>
	<style type="text/css">
		.uploadPreview {
		    height:120px;     
		    width:100%;                     
		}
		.pic{
		    border:0; 
			margin:0; 
			padding:0; 
			max-width:200px; 
			width:expression(this.width>200?"200px":this.width); 
			max-height:120px; 
			height:expression(this.height>120?"120px":this.height); 
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
			if(!(top)){
				top = self; 
			}
			
			$("#star").raty({ 
				readOnly: true,
				score:'${empty reviewer.star?0:reviewer.star}',
				path:'${ctxStatic}/raty-master/lib/images'
			});
		
			$(".back").click(function(){
				history.go(-1);
				//window.location.href = "${ctx}/amazoninfo/reviewer?country=${reviewer.country}&reviewerType=${reviewer.reviewerType}";
			});
			
			$(".comment").click(function(){
				top.$.jBox("get:${ctx}/amazoninfo/reviewer/addComment", {persistent: true,width:505,height:410,title:"增加联系记录", buttons:{"<spring:message code='sys_but_save'/>":1,"<spring:message code='sys_but_closed'/>":2},submit:function(v,h,f){
					if(v==1){
						var param = {};
						param.id = '${reviewer.id}';
						param.comment = h.find("#comment").data("editor").getData();
						$.post("${ctx}/amazoninfo/reviewer/saveComment",param,function(data){
							if(data==1){
								$("#commentDiv").append('<div class="control-group"><div class="controls"><div class="page-header">Just a moment ago</div>'+param.comment+'<hr style="border-bottom-color: blue"/></div></div>');
								top.$.jBox.tip("增加联系记录成功!","success",{persistent:false,opacity:0});
							}
						});
					}
				}});
			});
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/reviewer?country=${reviewer.country}&reviewerType=${reviewer.reviewerType}" >评测信息</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/reviewer/records?id=${reviewer.id}">联系记录</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="reviewer"  class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
				<input  class="btn btn-primary comment" type="button" value="增加联系记录"/>&nbsp;
			<!-- 返回 -->
			<input class="btn back" type="button" value="<spring:message code='sys_but_back'/> "/>
		</div>
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<blockquote>
						<p style="font-size: 14px"><spring:message code='custom_event_detail'/></p>
					</blockquote>
					<div class="control-group">
						<label class="control-label">姓名:</label>
						<div class="controls">
							${reviewer.name }
						</div>
					</div>
					<c:if test="${'1' eq reviewer.reviewerType }">
					<div class="control-group">
						<label class="control-label">客户来源:</label>
						<div class="controls">
							${reviewer.sourcePlatform }
						</div>
					</div>
					</c:if>
					<c:if test="${'0' eq reviewer.reviewerType }">
					<div class="control-group">
						<label class="control-label">客户id:</label>
						<div class="controls">
							<a target="_blank" href="http://www.amazon.com/gp/pdp/profile/${reviewer.reviewerId }/ref=cm_cr_tr_tbl_${reviewer.rank }_name">${reviewer.reviewerId }</a>
						</div>
					</div>
					</c:if>
					<div class="control-group">
						<label class="control-label">客户类型:</label>
						<div class="controls">
							<c:if test="${'0' eq reviewer.reviewerType }">站内</c:if>
							<c:if test="${'1' eq reviewer.reviewerType }">站外</c:if>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">评测邮箱:</label>
						<div class="controls">
							${reviewer.reviewEmail }
							<c:if test="${not empty reviewer.reviewEmail }">
								&nbsp;&nbsp;
								<a href="${ctx}/amazoninfo/reviewer/sendEmail?email=${reviewer.reviewEmail }">联系客户</a>
							</c:if>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">地址:</label>
						<div class="controls">
							${reviewer.address }
						</div>
					</div>
					<c:if test="${'0' eq reviewer.reviewerType }">
					<div class="control-group">
						<label class="control-label">亚马逊排名:</label>
						<div class="controls">
							${reviewer.rank }
						</div>
					</div>
					</c:if>
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
					<div class="control-group">
						<label class="control-label">星级评分:</label>
						<div class="controls">
							<div id="star"></div>
						</div>
					</div>
					
					<blockquote>
						<p style="font-size: 14px;">联系记录</p>
					</blockquote>
					<div id="commentDiv">
					<c:forEach var="comment" items="${reviewer.comments}">
						<div class="control-group">
							<div class="controls">
								<div class="page-header"><fmt:formatDate type="both" value="${comment.createDate}"/>Note By&nbsp;${comment.createBy.name}  </div>
								${comment.comment}
								<hr style="border-bottom-color: blue"/>
							</div>
						</div>
					</c:forEach>
					</div>
				</div>
			</div>
		</div>
		<div class="control-group">
			<!-- 返回 -->
			<input  class="btn back" type="button" value=" <spring:message code='sys_but_back'/> "/>
		</div>
	</form:form>
</body>
</html>
