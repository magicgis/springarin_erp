<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品销售预测编辑</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			float: right;
			min-height: 40px
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
			$("#inputForm").validate();
			$(".report").popover({html:true,trigger:'click',content:function(){
				var nameCountry = $(this).attr("key");
				var productName = nameCountry.split(";")[0];
				var country = nameCountry.split(";")[1];
				$this =$(this);
				var content = $this.parent().find(".content").html();
				if(!content){
					$.ajax({
					    type: 'post',
					    async:false,
					    url: '${ctx}/amazoninfo/salesForecast/saleReport',
					    data: {
					    	"country":country,
					    	"productName":productName
					    },
					    success:function(data){ 
					    	content = data;
					    	$this.parent().find(".content").html(data);
				        }
					});
				}
				return content;
			}});
			
			$(".report").live("click",function(){
				$this = this;
				$(".report").each(function(){
					if(this != $this){
						$(this).popover('hide');
					}
				});
				var p = $(this).position();
				$('.fade').css("max-width","700px");
				while($("form .fade").size()>1){
					$(".fade:first").remove();
				}
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("a[rel='popover']").live("mouseover",function(){
				$(".report").popover('hide');
				var p = $(this).position();
				$('.fade').css("max-width","700px");
			});
			
			$(".back").on("click",function(){
				var country = '${country }';
				window.location.href = "${ctx}/amazoninfo/salesForecastByMonth/?country=" + country;
			});
			
			$("#btnSave").on("click",function(){
				var flag = false;
				$(".quantity").each(function(){
					if($(this).val() != null && $(this).val()!=''){
						flag = true;
						return false;
					} 
				});
				if(flag){
					$("#inputForm").submit();
				} else {
					top.$.jBox.tip("请至少填写一个预测数量！");
				}
				
			});
		});
		
		function changeProduct(obj){
			var country = '${country}';
			window.location.href = "${ctx}/amazoninfo/salesForecastRecord/goEdit?productName="+obj.value+"&country="+country;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/amazoninfo/salesForecastRecord/goEdit?productName=${productName}&country=${country}">销售预测编辑</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="salesForecastRecord" action="${ctx}/amazoninfo/salesForecastRecord/save" method="post" class="form-horizontal">
		<input type="hidden" id="country" name="country" value="${country }"/>
		<input type="hidden" id="id" name="id" value="${salesForecastRecord.id }"/>
		<br/>
	    <blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">产品信息</p>
		</blockquote>
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:30%" >
				<label class="control-label" style="width:125px">平台:</label>
				<div class="controls" style="margin-left:135px">
					${fns:getDictLabel(country,'platform','')}
				</div>
			</div>
		</div>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:35%;height:30px">
				<label class="control-label" style="width:125px">产品名:</label>
				<div class="controls" style="margin-left:120px">
					<select style="width:200px;" id="productName" name="productName" onchange="changeProduct(this)">
						<c:forEach items="${productNames}" var="name" varStatus="i" >
							 <option value="${name}" ${name eq productName ?'selected':''}>${name}</option>
						</c:forEach>
					</select>
					&nbsp;&nbsp;
					<a href="#" key="${productName};${country}" class="report">
						<button type="button" class="btn btn-success" >历史销量</button>
						<%--<i class="icon-signal"></i> --%>
					</a>
					<div class="content" style="display: none"></div>
				</div>
			</div>
		</div>
	  	
	    <blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">预测销量</p>
		</blockquote>
	
	  	<c:forEach items="${dates}" var="month" varStatus="status">
	  		<c:if test="${status.count==1 }"><c:set var="quantityForecast" value="${salesForecastRecord.forecast1>0?salesForecastRecord.forecast1:data[productName][month].quantityForecast}"></c:set></c:if>
			<c:if test="${status.count==2 }"><c:set var="quantityForecast" value="${salesForecastRecord.forecast2>0?salesForecastRecord.forecast2:data[productName][month].quantityForecast}"></c:set></c:if>
			<c:if test="${status.count==3 }"><c:set var="quantityForecast" value="${salesForecastRecord.forecast3>0?salesForecastRecord.forecast3:data[productName][month].quantityForecast}"></c:set></c:if>
			<c:if test="${status.count==4 }"><c:set var="quantityForecast" value="${salesForecastRecord.forecast4>0?salesForecastRecord.forecast4:data[productName][month].quantityForecast}"></c:set></c:if>
			<c:if test="${status.count==5 }"><c:set var="quantityForecast" value="${salesForecastRecord.forecast5>0?salesForecastRecord.forecast5:data[productName][month].quantityForecast}"></c:set></c:if>
			<c:if test="${status.count==6 }"><c:set var="quantityForecast" value="${salesForecastRecord.forecast6>0?salesForecastRecord.forecast6:data[productName][month].quantityForecast}"></c:set></c:if>
		  	<c:choose>
			  	<c:when test="${hiddenCurrentMonth && status.count==1}">
			  		<input id="forecast${status.count }" name="forecast${status.count }" type="hidden" maxlength="5" value="${quantityForecast }"/>
			  	</c:when>
			  	<c:otherwise>
			  		<div class="control-group">
						<label class="control-label" style="width:125px">${month}月:&nbsp;&nbsp;</label>
						<div class="controls" style="margin-left:120px">
							<input id="forecast${status.count }" name="forecast${status.count }" type="text" class="number quantity" maxlength="5" value="${quantityForecast }"/>
							<span>(系统预测销量:${data[productName][month].quantityForecast })</span>
						</div>
					</div>
			  	</c:otherwise>
		  	</c:choose>
	  	</c:forEach>
	  	<br/>
		<div style="float:left;width:98% ;">
		<div class="control-group" style="float:left;width:98%">
			<label class="control-label" style="width:100px"><b>修改原因:</b></label>
			<div class="controls" style="margin-left:120px">
				<textarea rows="6" maxlength="200" style="width:45%; height: 80px;" id="remark" name="remark" class="required" />${salesForecastRecord.remark }</textarea>
			</div>
		</div>
		</div>
		<div style="float:left;width:100%" class="form-actions">
			<input id="btnSave" class="btn btn-primary" type="button" value="保存"/>
			&nbsp;&nbsp;
			<input id="btnCancel" class="btn back" type="button" value="返 回"/>
		</div>
		</form:form>
</body>
</html>
