<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>销量预测管理按月</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
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
			
			$(".report").popover({html:true,trigger:'click',content:function(){
				var country = '${salesForecast.country}';
				var keyAttr = $(this).attr("key");
				var hasPower = keyAttr.split(";")[0];
				if("de" == country && "0" == hasPower){
					country = "eu";
				}
				if("de" == country && "1" == hasPower){
					country = "eunouk";
				}
				var productName = keyAttr.split(";")[1];
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
			
			$(".countryHref").click(function(){
				window.location.href = "${ctx}/amazoninfo/salesForecastByMonth?country="+$(this).attr("key");
			});
			
			
			
			$("#excel").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
			
			
			var oTable = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap","sScrollX": "100%",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
			});
			
			/* new FixedColumns( oTable,{
		 		"iLeftColumns":5,
				"iLeftWidth": 550
		 	} ); */
			
			$("#importForm").live("submit",function(){
				if(!($(this).find("#uploadFile").val())){
					return false;
				}
				loading('正在导入，请稍等...');
				return true;
			});
			
		});
		function ckeckNums(text) {
			var bool= /^(0|[1-9][0-9]*)$/.test(text);
			return bool;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
					<li class="${salesForecast.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		<li><a class="countryHref" href="#" key="total">指标汇总</a></li>
		<%--<li><a href="${ctx}/amazoninfo/salesForecastRecord/">销量预测审批</a></li> --%>
	</ul>
	<div class="alert alert-info"><strong>预测产品为非新品和非淘汰品.</strong></div>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle">序号</th>
				<th style="text-align: center;vertical-align: middle;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;产品名称&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
				<th style="text-align: center;vertical-align: middle">上架日期</th>
				<c:forEach items="${dates}" var="month" varStatus="i">
					<th style="text-align: center;vertical-align: middle;">
						${month}月
					</th>
				</c:forEach>
				<c:forEach items="${dates1}" var="month" varStatus="i">
					<th style="text-align: center;vertical-align: middle${i.index==0?';background-color:#D2E9FF;':''}">
						${month}月
					</th>
				</c:forEach>
				<%--<th style="text-align: center;vertical-align: middle${i.index==0?';background-color:#D2E9FF;':''}">
					操作
				</th> --%>
			</tr>
		</thead>
		<tbody>
		<c:set var="index" value="1" />
		<c:forEach items="${products}" var="product" varStatus="i">
			<c:set var="name" value="${product.colorName}" />
				<tr>
					<td style="text-align: center;vertical-align: middle;">${index}<c:set var="index" value="${index+1}" /></td>
					<td style="text-align: center;vertical-align: middle;">
						<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${name}">${name}</a>
						<a href="#"  key="${hasPower[name]};${name}" class="report"><i class="icon-signal"></i></a>
						<span style="color: gray">
						   <c:if test="${product.country eq 'de' && hasPower[name] eq '2' }">非泛欧</c:if>
						   <c:if test="${product.country eq 'de' && hasPower[name] eq '1' }">四国泛欧</c:if>
						</span>
						<div class="content" style="display: none"></div>
					</td>
					<td style="text-align: center;vertical-align: middle;">${fn:replace(product.addedMonth,'00:00:00','')}</td>
					<c:forEach items="${dates}" var="month" varStatus="i">
						<td style="text-align: center;vertical-align: middle">
							<c:set var="forecast" value="${data[name][month].quantityForecast}" />
							<c:set var="realData" value="${empty saleData[name][month]?0:saleData[name][month]}" />
							<c:if test="${'de' eq product.country  && '0' eq hasPower[name]}">
								<c:set var="realData" value="${saleData[name][month] + totalSaleData[name]['uk'][month] + totalSaleData[name]['fr'][month] + totalSaleData[name]['it'][month] + totalSaleData[name]['es'][month]}" />
							</c:if>
							<c:if test="${'de' eq product.country  && '1' eq hasPower[name]}">
								<c:set var="realData" value="${saleData[name][month] + totalSaleData[name]['fr'][month] + totalSaleData[name]['it'][month] + totalSaleData[name]['es'][month]}" />
							</c:if>
							<c:choose>
								<c:when test="${empty forecast}">
									${realData}
								</c:when>
								<c:otherwise>
									<c:choose>
										<%--<c:when test="${realData eq forecast}">
											<a href="#" style="color:green;font-weight: bold;" rel="popover" data-content="预测销量:${forecast}">${realData}</a>
										</c:when> --%>
										<c:when test="${realData gt forecast*1.2}">
											<a href="#" style="color:green" rel="popover" data-content="预测销量:${forecast}">${realData}</a>
										</c:when>
										<c:when test="${realData lt forecast/1.2}">
											<a href="#" style="color:red" rel="popover" data-content="预测销量:${forecast}">${realData}</a>
										</c:when>
										<c:otherwise>
											<a href="#" rel="popover" data-content="预测销量:${forecast}">${realData}</a>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
							
						</td>
					</c:forEach>
					<c:forEach items="${dates1}" var="month" varStatus="i">
						<c:set var="realData" value="${saleData[name][month]}"/>
						<c:if test="${'de' eq product.country  && '0' eq hasPower[name]}">
							<c:set var="realData" value="${saleData[name][month] + totalSaleData[name]['uk'][month] + totalSaleData[name]['fr'][month] + totalSaleData[name]['it'][month] + totalSaleData[name]['es'][month]}" />
						</c:if>
						<c:if test="${'de' eq product.country  && '1' eq hasPower[name]}">
							<c:set var="realData" value="${saleData[name][month] + totalSaleData[name]['fr'][month] + totalSaleData[name]['it'][month] + totalSaleData[name]['es'][month]}" />
						</c:if>
						<td style="text-align: center;vertical-align: middle${i.index==0?';background-color:#D2E9FF;':''}">
							<c:if test="${not empty tips[name][month] && i.index>0}">
								<a href="#" rel="popover" data-content="${tips[name][month]}" data-html="true"  data-placement="${i.count>2?'left':'right'}" style="color: maroon;">tip&nbsp;&nbsp;</a>
							</c:if>
							<c:if test="${not empty tips[name][month] && i.index==0}">
								<a href="#" rel="popover" data-content="当前月销量:${saleData[name][month]};<br/>${tips[name][month]}" data-html="true"  data-placement="${i.count>2?'left':'right'}" style="color: maroon;">tip&nbsp;&nbsp;</a>
							</c:if>
							<c:if test="${empty tips[name][month] && i.index==0}">
								<a href="#" rel="popover" data-content="当前月销量:${saleData[name][month]};" data-html="true"  data-placement="${i.count>2?'left':'right'}" style="color: maroon;">tip&nbsp;&nbsp;</a>
							</c:if>
							<a href="#"  data-type="text" data-pk="1"  data-title="Fill in SaleForecast" data-original-title="" title="">${data[name][month].quantityAuthentication>0?data[name][month].quantityAuthentication:data[name][month].quantityForecast}</a>
							<c:if test="${data[name][month].quantityAuthentication>0 && data[name][month].quantityAuthentication != data[name][month].quantityForecast}"><br/><span style="color: #08c;">系统预测：${data[name][month].quantityForecast}</span></c:if>
						</td>
					</c:forEach>
					<%--<td><a class="btn btn-success btn-small" href="${ctx}/amazoninfo/salesForecastRecord/goEdit?country=${salesForecast.country}&productName=${name}">修改</a></td> --%>
				</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
