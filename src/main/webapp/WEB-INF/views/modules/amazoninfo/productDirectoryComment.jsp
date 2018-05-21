<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品目录评论扫描分析</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.red{color:red;}
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		if(!(top)){
			top = self;
		}
		
		$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn ){
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr,i) {
				return $('td:eq('+iColumn+')', tr).text();
			} );
		};
		
		$.fn.dataTableExt.afnSortData['isShield'] = function ( oSettings, iColumn ){
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr,i) {
				return $('td:eq('+iColumn+')', tr).find(".isShield").text();
			} );
		};
		
		
		$(document).ready(function(){
			$("a[rel='popover']").popover({trigger:'hover'});
			
			var selfPercent=0;
			$(".singlePercent").each(function(){
				if($(this).text().trim()){
					selfPercent=parseFloat(selfPercent)+parseFloat($(this).text());
				}
			});
			
			$("#selfPercent").text(selfPercent.toFixed(2));
			
			$("input[name='saleCommRate']").on("blur",function(){
				$("#searchForm").submit();
			});
			
			$("#contentTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"aoColumns": [
					          null,
						      null,
						      null,
						      null,
						      null,
						      null,
						      null
				],
				"ordering" : true,
				"aaSorting": [[ 1, "asc" ]]
			});
		
			
			$("#dataTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 100,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"aoColumns": [
					          { "sSortDataType":"isShield", "sType":"numeric" },
						      null,
						      null,
						      null,
						      null,
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      null,
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      null,
						      null,
						      null
				],
				"ordering" : true,
				"aaSorting": [[ 0, "asc" ],[ 3, "asc" ]]
			});
		});
		
		
		function updateState(btn,directoryId,asin){
			var tr =$(btn).parent().parent();
			if(btn.checked){
				tr.css("background-color","#F0F8FF");
			}else{
				tr.css("background-color","");
			}
			var param = {};
			param.directoryId = directoryId;
			param.asin = asin;
			param.checked = btn.checked;
			$.get("${ctx}/amazoninfo/productDirectoryComment/updateShield?"+$.param(param),function(data){
			
				if(data!=''){
					top.$.jBox.tip(data, 'error',{timeout:2000});
				}else{
					top.$.jBox.tip("切换成功！", 'info',{timeout:2000});
				}
			}); 
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a  href="${ctx}/amazoninfo/productDirectory">产品目录列表</a></li>
		<li class="active"><a href="#">目录扫描结果分析</a></li>
	</ul>
 
	<form:form id="searchForm" modelAttribute="productDirectoryComment" action="${ctx}/amazoninfo/productDirectoryComment/list" method="post" class="breadcrumb form-search" style="float:left;width:100%">
		<input type="hidden" name="directoryId" value="${productDirectoryComment.directoryId }"/>
		 <span style="text-align:center;width:100%;float: left;">
			<span style="font-size: 20px;font-weight: bold">${subject}</span>
			<label style="font-size: 14px;font-weight: bold;padding-left:50px">销量评论比：</label><input type="text" name="saleCommRate" value="${productDirectoryComment.saleCommRate}" style="width:80px"/>
		</span>
	</form:form>
	<tags:message content="${message}"/>
	<div style="float:left;width:98%;height:50px;" class="alert alert-info">
	<strong>
		<span style="padding-left:30px;width: 100%;font-weight: bold;float: left;font-size: 16px;">
		     ${dateEnd}评论总数:<span class="red"><fmt:formatNumber value="${resArr[0]}" pattern=",###" /></span>
			 由销量评论比:<span class="red">${productDirectoryComment.saleCommRate}</span>
			 推算出市场容量:<span class="red"><fmt:formatNumber value="${resArr[1]}" pattern=",###" /></span>
			 销售额:<span class="red"><fmt:formatNumber value="${resArr[2]}" pattern=",###.##" />(${currency})</span>
		            好评率:<span class="red">${resArr[3]}%</span><br/>
		    TOP20前3的品牌 
		 	<c:forEach items="${brandMap}" var="brandEntry" varStatus="j">
		 		<c:if test="${j.index<3}">
		 		${brandEntry.key}(<span class="red"><fmt:formatNumber value="${brandEntry.value*100/20}" pattern="#" />%</span>)
		 		</c:if>
		 	</c:forEach>   
		  	   共计(<span class="red"><fmt:formatNumber value="${total20*100/20}"/>%</span>);
		            日销售额:<span class="red"><fmt:formatNumber value="${resArr[4]}" pattern=",###.##" />(${currency})</span>
		            产品均价:<span class="red"><fmt:formatNumber value="${resArr[5]}" pattern=",###.##" />(${currency})</span>
		          高于均价占比：<span class="red"><fmt:formatNumber value="${resArr[6]}" pattern=",###.##" />%</span>
		    <br/>
		    10百分位(<span style="color:#343831">${percent10Price}</span>);30百分位(<span style="color:#552e89">${percent30Price}</span>); 
		    50百分位(<span style="color:#00834e">${percent50Price}</span>);70百分位(<span style="color:#a97463">(${percent70Price}</span>);
		</span>
	</strong> 
	</div>
	<c:if test="${not empty selfComMap}">
		<blockquote style="float: left;">
		<p style="font-size: 14px;float: left;font-weight: bold">(自家)单品信息:</p>
		</blockquote>
	
		<table id="contentTable" class="table table-bordered table-condensed" style="float:left;width: 100%">
			<thead><tr><th style="width:5%">产品</th><th style="width:5%">排名</th><th style="width:5%">单价(${currency})</th><th style="width:5%">当前市场占有率(%)</th><th style="width:5%">好评率</th><th style="width:5%">评分</th><th style="width:5%">分布图</th></tr></thead>
			<tbody>
				<c:forEach items="${selfComMap}" var="selfEntry">
				<tr>
					<td><a target="_black" href="${selfEntry.value.productLink}">${asinProductNameMap[selfEntry.key]}</a></td>
					<td>${selfEntry.value.ranking}</td>
					<td>${selfEntry.value.salePrice}</td>
					<td>
						<span style="color: green;font-weight: bold;" class="singlePercent">
							<c:if test="${selfEntry.value.ranking<21}">
								<fmt:formatNumber value="${(21-selfEntry.value.ranking)*100/210}" pattern="#.##" />
							</c:if>
						</span>
					</td>
					<td>${selfEntry.value.goodCommentsRate}</td>
					<td>${selfEntry.value.star}</td>
					<td><a href="${ctx}/amazoninfo/productDirectoryComment/showChart?directoryCommentId=${selfEntry.value.id}&title=${asinProductNameMap[asin]}">查看</a></td>
				</tr>
				</c:forEach>
			</tbody>
			<tfoot><tr><td style="font-weight: bold;text-align: center" colspan="3">自家产品占有率:</td><td colspan="4" style="color:green;font-weight: bold"><span id="selfPercent"></span></td></tr></tfoot>
		</table>
	</c:if>
	
	<blockquote style="float: left;width:100%">
		<p style="font-size: 14px;font-weight: bold;">评论增长率排名:</p>
	</blockquote>
	<table id="dataTable" class="table table-bordered table-condensed" style="float:left;width: 100%;">
		<thead><tr><td style="width:3%">屏蔽</td><th style="width:5%">图片</th><th style="width:5%">品牌</th><th style="width:5%">排名</th><th style="width:5%">30天销量<br/>(评论)</th><th style="width:5%">30天销量<br/>(购物车)</th><th style="width:5%">昨日销量<br/>(购物车)</th><th style="width:5%">市场占比(%)</th><th style="width:5%">售价(${currency})</th><th style="width:5%">好评率(%)<br/>评分</th><th style="width:5%">上周评论数</th><th style="width:5%">上架时间</th><th style="width:5%">评论图</th></tr></thead>
		<tbody>
			<c:forEach items="${directoryMap}" var="proMap">
				<tr style="background-color:${not empty selfComMap[proMap.key]?'#7FFFD4':''}">
					<td><span style="display:none" class="isShield">${proMap.value.isShield}</span><input type="checkBox" class="isCheck" ${proMap.value.isShield eq "1"?"checked":""}  onchange="updateState(this,'${proMap.value.directoryId}','${proMap.value.asin}')" /></td>
					<td><img style="width:50px;height:50px" src="${proMap.value.image}" /></td>
					<td>
						<c:if test="${not empty selfComMap[proMap.key]}">
							<a target="_black" href="${proMap.value.productLink}" rel="popover" data-content='${asinProductNameMap[proMap.value.asin]}'>${not empty proMap.value.brand?proMap.value.brand:"无"}</a>
						</c:if>
						<c:if test="${empty selfComMap[proMap.key]}">
							<a target="_black" href="${proMap.value.productLink}" rel="popover" data-content="${proMap.value.title}">${not empty proMap.value.brand?proMap.value.brand:"无"}</a>
						</c:if>
					</td>
					<td>${proMap.value.ranking}</td>
					<td>
						${proMap.value.comm30Days}
					</td>
					<td>
						<c:if test="${not empty asin30SaleMap[proMap.key]}">
							<a class="btn btn-small" href="${ctx}/amazoninfo/opponentStock/list?asin=${proMap.value.asin}&country=${proMap.value.country}">${asin30SaleMap[proMap.value.asin]}</a>
						</c:if>
					</td>
					<td>
						${yestardayMap[proMap.key]}
					</td>
					<td>
					<c:if test="${proMap.value.ranking<21}">
						<fmt:formatNumber value="${(21-proMap.value.ranking)*100/210}" pattern="#.##" />
					</c:if>
					</td>
					<td><span style='color:${proMap.value.displayColor}'>${proMap.value.salePrice}</span></td>
					<td>${proMap.value.goodCommentsRate}<br/>${proMap.value.star}</td>
					<td>
					<c:if test="${not empty proMap.value.weekCompare&&fn:contains(proMap.value.weekCompare,',')}">
						<c:forEach items= "${fn:split(proMap.value.weekCompare,',')}" var="historyWeek" varStatus="j">
							<c:choose>
								<c:when test="${j.index==0}">今年(${historyWeek})<br/></c:when>
								<c:when test="${j.index==1}">去年(${historyWeek})<br/></c:when>
								<c:when test="${j.index==2}">前年(${historyWeek})<br/></c:when>
							</c:choose>
						</c:forEach>
					</c:if>
					</td>
					<td>
					<c:if test="${not empty proMap.value.shelvesDate}">
						<fmt:formatDate value="${proMap.value.shelvesDate}" pattern="yyyy-MM-dd"/>
					</c:if>
					</td>
					<td>
						<a href="${ctx}/amazoninfo/productDirectoryComment/showChart?directoryCommentId=${proMap.value.id}&title=${not empty selfComMap[proMap.key]?'selfComMap[proMap.key]':''}">查看</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
