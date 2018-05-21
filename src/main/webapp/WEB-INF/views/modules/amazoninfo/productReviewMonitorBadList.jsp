<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>对手评论检测管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/raty-master/lib/jquery.raty.js" ></script>    
	<script type="text/javascript">
		$(document).ready(function() {
			$(".star").each(function(){
				var starNum = $(this).find(":hidden").val();
				$(this).raty({ readOnly: true, score:starNum,path:'${ctxStatic}/raty-master/lib/images' });
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
				}
			});
		});
	</script>
	<style>
	a:link { 
	color: #000000; 
	text-decoration: none; 
	} 
	a:visited { 
	color: #000000; 
	text-decoration: none; 
	} 
	a:hover { 
	color: #000000; 
	text-decoration: underline; 
	} 
	
	.reviews-content {  
	  min-height: 300px;
	  word-wrap: break-word;
	}
	.a-section{
	  width:1040px;
	  margin-bottom: 22px;
	}
	
	.a-spacing-mini, .a-ws .a-ws-spacing-mini {
  		margin-bottom: 6px!important;
	}
	.a-row {
	  width: 100%;
	}
	
	.a-star-5 {
	  background-position: -5px -368px;
	}
	.a-icon-star {
	  width: 80px;
	  height: 18px;
	}
	.a-icon-star, .a-icon-star-medium, .a-icon-star-mini, .a-icon-star-small {
	  position: relative;
	  vertical-align: text-top;
	}
	.a-icon, .a-link-emphasis:after {
	  background-image: url(http://g-ecx.images-amazon.com/images/G/01/AUIClients/AmazonUIBaseCSS-sprite_1x-769b217e87bb50e13cac4cddab5ced142197bb43._V2_.png);
	  background-repeat: no-repeat;
	  -webkit-background-size: 400px 650px;
	  background-size: 400px 650px;
	  display: inline-block;
	  vertical-align: top;
	}
	
	.a-size-base {
	  font-size: 13px!important;
	  line-height: 19px!important;
	}
	.a-color-base {
	  color: #111!important;
	}
	.a-text-bold {
	  font-weight: 700!important;
	}
	.a-color-secondary {
	  color: #555!important;
	}
	
		
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/productReviewMonitor/">对手产品评论监控列表</a></li>
		<li class="active"><a href="#">评论list</a></li>
	</ul><br/>
	<c:forEach items="${productReviewMonitor.reviews}" var="review">
		<div  class="a-section review">
			<div class="a-row star"><input type="hidden" name="star" value="${review.star}"/></div>
			<div class="a-row  a-size-base a-color-base a-text-bold" ><a target="_blank" href="${review.reviewLink}">${review.subjectShow}</a></div>
			<div class="a-row a-size-base a-color-secondary" ><a target="_blank" href="${review.customerLink }">${review.customerName}</a>&nbsp;&nbsp;&nbsp;&nbsp;<fmt:formatDate value="${review.reviewDate }" pattern="MM-dd-yyyy"/>&nbsp;&nbsp;${review.state eq '0'?'被删除或改好评':'' }</div>
			<div class="a-row">
			<span class="a-size-base review-text">${review.contentShow}</span>
			</div>
		</div>
		<hr/>
	</c:forEach>
</body>
</html>
