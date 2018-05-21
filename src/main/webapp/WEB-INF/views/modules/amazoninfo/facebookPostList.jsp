<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>facebookList</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px;padding-top: 5px}
		.spanexl{ float:left;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
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
			
				 var oTable = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap","sScrollX": "100%",
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
					 	"aaSorting": [[0, "desc" ]]
					});
					/*  new FixedColumns( oTable,{
					 		"iLeftColumns":3,
							"iLeftWidth": 400
					 	} ); */
					/*  $(".row:first").append($("#searchContent").html());
					  */
					$('.chartsShow').mouseover(function(){
				        $(this).css({
				            'backgroundColor':'#df0001',
				            'color':'#63B8FF'
				        });
				    });
				    $('.chartsShow').mouseout(function(){
				        $(this).css({
				            'backgroundColor':'#F2F2F2',
				            'color':'#242424'
				        });
				    });

					 
					 /* $(".chartsShow").popover({placement:'left',html:true,trigger:'click',content:function(){
						
							var adId = $(this).attr("adIdKey");
							var adName = $(this).attr("adNameKey");
							var createDate = $("#start").val();
							var endDate = $("#end").val();
							
							$this =$(this);
							var content = $this.parent().find(".content").html();
						//	console.log(content);
							if(!content){
								$.ajax({
								    type: 'post',
								    async:false,
								    url: '${ctx}/amazoninfo/amazonAndFacebook/amountSpentCharts',
								    data: {
								    	"adId":adId,
								    	"adName":adName,
								    	"createDate":createDate,
								    	"endDate":endDate
								    },
								    success:function(data){ 
								    	content = data;
								    	$this.parent().find(".content").html(data);
							        }
								});
							}
							return content;
						}}); 
					 
					 $(".chartsShow").live("click",function(){
							$this = this;
							$(".chartsShow").each(function(){
								if(this != $this){
									$(this).popover('hide');
								}
							});
							var p = $(this).position();
							$('.fade').css("top",p.top-100).css("left",p.left+100);
							$('.fade').css("max-width","800px");
							$('.fade').css("height","600px");
						}); */
				
		});
		
		function showCharts(adId,adName,sameCr,impressions,trackingId,totalRevenue,pageLikes,relativeRoi,costPerPostShare,postEngagement,costPerPostEngagement,postShares,negativeFeedback,country,
				sameItemsShipped,postComments,costPerPageLike,preView,allItemsShipped,starts,totalCr,linkClicks,totalAffiliateFees,profit,productLine,asinOnAd){
			var createDate = $("#start").val();
			var endDate = $("#end").val();
			$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/amazoninfo/amazonAndFacebook/amountSpentCharts',
				    data: {
				    	"adId":adId,
				    	"adName":adName,
				    	"createDate":createDate,
				    	"endDate":endDate,
				    	"sameCr":sameCr,
				    	"impressions":impressions,
				    	"trackingId":trackingId,
				    	"totalRevenue":totalRevenue,
				    	"pageLikes":pageLikes,
				    	"relativeRoi":relativeRoi,
				    	"costPerPostShare":costPerPostShare,
				    	"postEngagement":postEngagement,
				    	"costPerPostEngagement":costPerPostEngagement,
				    	"postShares":postShares,
				    	"negativeFeedback":negativeFeedback,
				    	"country":country,
				    	"sameItemsShipped":sameItemsShipped,
				    	"postComments":postComments,
				    	"costPerPageLike":costPerPageLike,
				    	"preView":preView,
				    	"allItemsShipped":allItemsShipped,
				    	"adsDate":starts,
				    	"totalCr":totalCr,
				    	"linkClicks":linkClicks,
				    	"totalAffiliateFees":totalAffiliateFees,
				    	"profit":profit,
				    	"productLine":productLine,
				    	"asinOnAd":asinOnAd
				    },
				    success:function(data){ 
				    	$("#chartsType").find(".modal-body").html(data);
			        }
			 });
			$("#chartsType").modal();
		}
		function exportList(){
			$("#searchForm").attr("action","${ctx}/amazoninfo/amazonAndFacebook/exportAll");
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/amazoninfo/amazonAndFacebook/totalList");
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	    <li class="active"><a href="${ctx}/amazoninfo/amazonAndFacebook/totalList">投放数据分析</a></li>
		<li><a href="${ctx}/amazoninfo/amazonAndFacebook/list">Facebook数据</a></li>
		<li><a href="${ctx}/amazoninfo/amazonAndFacebook/amazonFacebookList">Amazon订单数据</a></li>
		<li><a href="${ctx}/amazoninfo/amazonAndFacebook/amazonFacebookRelationship">关联数据</a></li>
	</ul>
<!-- 	<div style="display: none" id="searchContent"> -->
		<form:form id="searchForm" modelAttribute="facebookDto" action="${ctx}/amazoninfo/amazonAndFacebook/totalList" method="post"  class="breadcrumb form-search">
		<label><b>Reporting Starts：</b></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="starts" value="<fmt:formatDate value="${facebookDto.starts}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${facebookDto.end}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
	     &nbsp;<b>Country：</b><select name="country" id="country" style="width: 100px">
						<option value="" ${facebookDto.country eq ''?'selected':''}>ALL</option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${facebookDto.country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;&nbsp;&nbsp;
				<b>Gender：</b><select name="gender" id="gender" style="width:60px">
						<option value="" ${facebookDto.gender eq ''?'selected':''}>ALL</option>
						<option value='FM' ${facebookDto.gender eq 'FM' ?'selected':''}>FM</option>
						<option value="M" ${facebookDto.gender eq 'M' ?'selected':''} >M</option>
				</select>&nbsp;&nbsp; &nbsp;&nbsp;
				<b>Placement：</b><select name="placement" id="placement" style="width:120px">
						<option value="" ${facebookDto.placement eq ''?'selected':''}>ALL</option>
						<option value='Desktop News Feed' ${facebookDto.placement eq 'Desktop News Feed' ?'selected':''}>Desktop News Feed</option>
						<option value="Desktop Right Column" ${facebookDto.placement eq 'Desktop Right Column' ?'selected':''} >Desktop Right Column</option>
						<option value="Mobile News Feed" ${facebookDto.placement eq 'Mobile News Feed' ?'selected':''} >Mobile News Feed</option>
						<option value="Instagram" ${facebookDto.placement eq 'Instagram' ?'selected':''} >Instagram</option>
						<option value="Audience Network" ${facebookDto.placement eq 'Audience Network' ?'selected':''} >Audience Network</option>
				</select>&nbsp;&nbsp; &nbsp;&nbsp;
				<br/><br/>
				<label><b>Age：</b></label><input name='age' value='${facebookDto.age }' class="input-mini"/>&nbsp;&nbsp; &nbsp;&nbsp;
				<label><b>Asin on ad：</b></label><input name='asinOnAd' value='${facebookDto.asinOnAd }' class="input-mini"/>&nbsp;&nbsp; &nbsp;&nbsp;
				<label><b>Product：</b></label><input name='product' value='${facebookDto.product }' class="input-small"/>&nbsp;&nbsp; &nbsp;&nbsp;
				<label><b>Product Line：</b></label><input name='productLine' value='${facebookDto.productLine }' class="input-small"/>&nbsp;&nbsp; &nbsp;&nbsp;
				<br/><br/>
				<label><b>Audience：</b></label><input name='audience' value='${facebookDto.audience }' class="input-small"/>&nbsp;&nbsp; &nbsp;&nbsp; 
				<label><b>Ad Name：</b></label><input name='adName' value='${facebookDto.adName }' class="input-small"/>&nbsp;&nbsp; &nbsp;&nbsp; 
				<label><b>Tracking Id：</b></label><input name='trackingId' value='${facebookDto.trackingId }' class="input-small"/>&nbsp;&nbsp;&nbsp;&nbsp;  
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>	&nbsp;&nbsp;
				<input id="exportSubmit" class="btn btn-primary" type="button" value="导出" onclick="exportList();"/>	&nbsp;&nbsp;
		       <a href="#updateExcel" role="button" class="btn  btn-primary" data-toggle="modal" id="uploadFile"><spring:message code="sys_but_upload"/></a> 
				
				<!--product  product line Audience ad name trackingId-->
		
	</form:form>
<!-- 	</div> -->
	
	<tags:message content="${message}"/>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>Starts</th>
					<th>Product</th>
					<th>Audience</th>
					<th>Age</th>
					<th>Gender</th>
					<th>Country</th>
					<th>Placement</th>
					<th>Ad Name</th>
					<th>Amount<br/>Spend</th>
					<th>Link<br/>Clicks</th>
					<th>CTR(%)</th>
					<th>CPC</th>
					<th>CPM</th>
					<th>Total CR(%)</th>
					<th>Profit</th>
					<th>Total<br/>Advertising<br/>Fees</th>	
					<th>系数ROI</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${faceBookList}" var="report">
			<tr class='chartsShow' adIdKey="${report.adId }" adNameKey="${report.adName}" onclick="showCharts('${report.adId }','${report.adName}','${report.sameCr}','${report.impressions }','${report.trackingId }','${report.totalRevenue }',
			'${report.pageLikes }','${report.relativeRoi }','${report.costPerPostShare }','${report.postEngagement }','${report.costPerPostEngagement }','${report.postShares }','${report.negativeFeedback }','${report.country }','${report.sameItemsShipped }',
			'${report.postComments }','${report.costPerPageLike }','${report.preView }','${report.allItemsShipped }','${report.starts}','${report.totalCr}','${report.linkClicks}','${report.totalAffiliateFees }','${report.profit}','${report.productLine }','${report.asinOnAd }');">
				<td>${report.starts}<div class="content" style="display: none;"></div></td>
				<td>${report.product}</td>
				<td>${report.audience}</td>
				<td>${report.age}</td>
				<td>${report.gender}</td>
				<td>${'com' eq report.country?'us':report.country}</td>
				<td>${report.placement}</td>
				<td>${report.adName}</td>
				<td>${report.amountSpend}</td>
				<td>${report.linkClicks}</td>
				<td>${report.ctr}</td>
				<td>${report.cpc}</td>
				<td>${report.cpm}</td>
				<td><fmt:formatNumber  pattern="#######.##" value="${report.totalCr}"  maxFractionDigits="2" /></td>
				<td><fmt:formatNumber  pattern="#######.##" value="${report.profit}"  maxFractionDigits="2" /></td>
				<td><fmt:formatNumber  pattern="#######.##" value="${report.totalAdvertisingFees}"  maxFractionDigits="2" /></td>
				<td><fmt:formatNumber  pattern="#######.##" value="${report.roi}"  maxFractionDigits="2" /></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<div id="chartsType" class="modal hide fade" tabindex="-1"  style="width: auto;">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		</div>
		<div class="modal-body" >
			 
		</div>
	</div>
	
	<div id="updateExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm" action="${ctx}/amazoninfo/amazonAndFacebook/uploadFile" method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">文件上传</h3>
						  </div>
						  <div class="modal-body">
							<label ><spring:message code="psi_transport_fileType"/> ：</label>
							<select  id="type" name="type">
								<option value="0">Facebook数据文件</option>
								<option value="1">Amazon数据文件</option>
								<option value="2">关联文件</option>
							</select><br/><br/>
							<input type="file" name="excel"  id="excel" accept="application/msexcel" class="required"/> 
						  </div>
						   <div class="modal-footer">
						   <button class="btn btn-primary"  type="submit" id="uploadTypeFile"><spring:message code="sys_but_upload"/></button>
						   <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
	</div>
	
</body>
</html>
