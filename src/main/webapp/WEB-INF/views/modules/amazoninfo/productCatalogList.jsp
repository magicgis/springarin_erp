<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>自有产品目录详情</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
<style type="text/css">
.spanexr {
	float: right;
	min-height: 40px
}

.spanexl {
	float: left;
}

.footer {
	padding: 20px 0;
	margin-top: 20px;
	border-top: 1px solid #e5e5e5;
	background-color: #f5f5f5;
}

.modal.fade.in {
 	top: 0%;
}
.modal{
	 width: auto;
	 margin-left:-500px 
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
	
	
	function display_by(btn){
		tip = $("#tipModel");
		tip.find(".modal-body").html($(btn).parent().find(".body").html());
		tip.modal();
	}
	
</script>
</head>
<body>
	<div style="height:670px;float: left">
		<div style="height:10px;position:fixed;z-index:1;left:0px;right:15px;top:0px">
			<table class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th style="width: 11%">Product Line</th>
						<th style="width: 11%">US</th>
						<th style="width: 11%">DE</th>
						<th style="width: 11%">UK</th>
						<th style="width: 11%">FR</th>
						<th style="width: 11%">IT</th>
						<th style="width: 11%">ES</th>
						<th style="width: 11%">JP</th>
						<th style="width: 11%">CA </th>
					</tr>
				</thead>
			</table>
		 </div>		
		 <div style="margin-top:23px;height:650px;overflow:scroll;width:100%">
		 	<table class="table table-bordered table-condensed">	
		 		 <colgroup>
	               <col style="width: 11%;"/>
	               <col style="width: 11%"/>
	               <col style="width: 11%"/>
	               <col style="width: 11%"/>
	               <col style="width: 11%"/>
	               <col style="width: 11%"/>
	               <col style="width: 11%"/>
	               <col style="width: 11%"/>
	               <col style="width: 11%"/>
	         	 </colgroup>
				<tbody>
					<c:forEach items="${typeTip}" var="item" varStatus="j" >
							<c:set var="num" value="${fn:length(data[item.key]['de'])}" />
							<c:if test="${fn:length(data[item.key]['com'])>num}">
								<c:set var="num" value="${fn:length(data[item.key]['com'])}" />	
							</c:if>	
							<c:if test="${fn:length(data[item.key]['fr'])>num}">
								<c:set var="num" value="${fn:length(data[item.key]['fr'])}" />	
							</c:if>	
							<c:if test="${fn:length(data[item.key]['it'])>num}">
								<c:set var="num" value="${fn:length(data[item.key]['it'])}" />	
							</c:if>	
							<c:if test="${fn:length(data[item.key]['es'])>num}">
								<c:set var="num" value="${fn:length(data[item.key]['es'])}" />	
							</c:if>	
							<c:if test="${fn:length(data[item.key]['uk'])>num}">
								<c:set var="num" value="${fn:length(data[item.key]['uk'])}" />	
							</c:if>	
							<c:if test="${fn:length(data[item.key]['ca'])>num}">
								<c:set var="num" value="${fn:length(data[item.key]['ca'])}" />	
							</c:if>	
							<c:if test="${fn:length(data[item.key]['jp'])>num}">
								<c:set var="num" value="${fn:length(data[item.key]['jp'])}" />	
							</c:if>	
							
						    <c:forEach begin="0" step="1" end="${num}" var="i">
						    	<c:if test="${fn:length(data[item.key]['de'])>i || fn:length(data[item.key]['uk'])>i || fn:length(data[item.key]['com'])>i || fn:length(data[item.key]['fr'])>i || fn:length(data[item.key]['it'])>i || fn:length(data[item.key]['es'])>i || fn:length(data[item.key]['jp'])>i || fn:length(data[item.key]['ca'])>i}">
						    		<tr style="${(j.index+1)%2==0?'':'background-color: #f9f9f9;'}">
										<td style="word-break:break-all">
											<b>${item.key}<br/>${item.value}</b>
										</td>
										<td style="word-break:break-all">
											<c:set var="key" value="com" />
											<c:if test="${fn:length(data[item.key][key])>i}">
												<a target="_blank" href="${data[item.key][key][i].link}">${data[item.key][key][i].catalogName}</a><br/>
												<div style="display: none" class="body">
													${data[item.key][key][i].tip}
													<div class="modal-footer">
														<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
													</div>
												</div>
												<a href="#"  class="btn btn-small btn-info tip"  onclick="display_by(this)">Product</a>
												<br/>
												------------															
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].styleMs}">
													M.S:(${data[item.key][key][i].marketShare}%  ${data[item.key][key][i].styleMs eq 'red'?'↓':''}${data[item.key][key][i].styleMs eq 'green'?'↑':''}  <c:if test="${not empty data[item.key][key][i].avg30MarketShare}">${data[item.key][key][i].avg30MarketShare}%</c:if>) 
												</span>
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].stylePirce}">
												  Avg.P:(${data[item.key][key][i].avgPrice}  ${data[item.key][key][i].stylePirce eq 'red'?'↓':''}${data[item.key][key][i].stylePirce eq 'green'?'↑':''}  <c:if test="${data[item.key][key][i].yestdayAvgPrice >0}">${data[item.key][key][i].yestdayAvgPrice}</c:if>)
												</span>
												<br/>
												 	Sales: ${data[item.key][key][i].sales}€
												 <br/>
													Sales volume: ${data[item.key][key][i].salesVolume}
												  <br/>
												  ------------	
												  <c:if test="${not empty data[item.key][key][i].fistTo20}">
													  First To Top20														
													 <br/>
													 	<b style="color: orange;">${data[item.key][key][i].fistTo20}</b>
													 <br/>
													  ------------		
												  </c:if>													
												 <br/>
												  	${data[item.key][key][i].outMs}
											</c:if>
										</td>
										
										<td style="word-break:break-all">
											<c:set var="key" value="de" />
											<c:if test="${fn:length(data[item.key][key])>i}">
												<a target="_blank" href="${data[item.key][key][i].link}">${data[item.key][key][i].catalogName}</a><br/>
												<div style="display: none" class="body">
													${data[item.key][key][i].tip}
													<div class="modal-footer">
														<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
													</div>
												</div>
												<a href="#"  class="btn btn-small btn-info tip"  onclick="display_by(this)">Product</a>
												<br/>
												------------															
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].styleMs}">
													M.S:(${data[item.key][key][i].marketShare}%  ${data[item.key][key][i].styleMs eq 'red'?'↓':''}${data[item.key][key][i].styleMs eq 'green'?'↑':''}  <c:if test="${not empty data[item.key][key][i].avg30MarketShare}">${data[item.key][key][i].avg30MarketShare}%</c:if>) 
												</span>
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].stylePirce}">
												  Avg.P:(${data[item.key][key][i].avgPrice}  ${data[item.key][key][i].stylePirce eq 'red'?'↓':''}${data[item.key][key][i].stylePirce eq 'green'?'↑':''}  <c:if test="${data[item.key][key][i].yestdayAvgPrice >0}">${data[item.key][key][i].yestdayAvgPrice}</c:if>)
												</span>
												<br/>
												 	Sales: ${data[item.key][key][i].sales}€
												 <br/>
													Sales volume: ${data[item.key][key][i].salesVolume}
												  <br/>
												  ------------	
												  <c:if test="${not empty data[item.key][key][i].fistTo20}">
													  First To Top20														
													 <br/>
													 	<b style="color: orange;">${data[item.key][key][i].fistTo20}</b>
													 <br/>
													  ------------		
												  </c:if>													
												 <br/>
												  	${data[item.key][key][i].outMs}
											</c:if>
										</td>
										
										<td style="word-break:break-all">
											<c:set var="key" value="uk" />
											<c:if test="${fn:length(data[item.key][key])>i}">
												<a target="_blank" href="${data[item.key][key][i].link}">${data[item.key][key][i].catalogName}</a><br/>
												<div style="display: none" class="body">
													${data[item.key][key][i].tip}
													<div class="modal-footer">
														<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
													</div>
												</div>
												<a href="#"  class="btn btn-small btn-info tip"  onclick="display_by(this)">Product</a>
												<br/>
												------------															
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].styleMs}">
													M.S:(${data[item.key][key][i].marketShare}%  ${data[item.key][key][i].styleMs eq 'red'?'↓':''}${data[item.key][key][i].styleMs eq 'green'?'↑':''}  <c:if test="${not empty data[item.key][key][i].avg30MarketShare}">${data[item.key][key][i].avg30MarketShare}%</c:if>) 
												</span>
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].stylePirce}">
												  Avg.P:(${data[item.key][key][i].avgPrice}  ${data[item.key][key][i].stylePirce eq 'red'?'↓':''}${data[item.key][key][i].stylePirce eq 'green'?'↑':''}  <c:if test="${data[item.key][key][i].yestdayAvgPrice >0}">${data[item.key][key][i].yestdayAvgPrice}</c:if>)
												</span>
												<br/>
												 	Sales: ${data[item.key][key][i].sales}€
												 <br/>
													Sales volume: ${data[item.key][key][i].salesVolume}
												  <br/>
												  ------------	
												  <c:if test="${not empty data[item.key][key][i].fistTo20}">
													  First To Top20														
													 <br/>
													 	<b style="color: orange;">${data[item.key][key][i].fistTo20}</b>
													 <br/>
													  ------------		
												  </c:if>													
												 <br/>
												  	${data[item.key][key][i].outMs}
											</c:if>
										</td>
										
										<td style="word-break:break-all">
											<c:set var="key" value="fr" />
											<c:if test="${fn:length(data[item.key][key])>i}">
												<a target="_blank" href="${data[item.key][key][i].link}">${data[item.key][key][i].catalogName}</a><br/>
												<div style="display: none" class="body">
													${data[item.key][key][i].tip}
													<div class="modal-footer">
														<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
													</div>
												</div>
												<a href="#"  class="btn btn-small btn-info tip"  onclick="display_by(this)">Product</a>
												<br/>
												------------															
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].styleMs}">
													M.S:(${data[item.key][key][i].marketShare}%  ${data[item.key][key][i].styleMs eq 'red'?'↓':''}${data[item.key][key][i].styleMs eq 'green'?'↑':''}  <c:if test="${not empty data[item.key][key][i].avg30MarketShare}">${data[item.key][key][i].avg30MarketShare}%</c:if>) 
												</span>
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].stylePirce}">
												  Avg.P:(${data[item.key][key][i].avgPrice}  ${data[item.key][key][i].stylePirce eq 'red'?'↓':''}${data[item.key][key][i].stylePirce eq 'green'?'↑':''}  <c:if test="${data[item.key][key][i].yestdayAvgPrice >0}">${data[item.key][key][i].yestdayAvgPrice}</c:if>)
												</span>
												<br/>
												 	Sales: ${data[item.key][key][i].sales}€
												 <br/>
													Sales volume: ${data[item.key][key][i].salesVolume}
												  <br/>
												  ------------	
												  <c:if test="${not empty data[item.key][key][i].fistTo20}">
													  First To Top20														
													 <br/>
													 	<b style="color: orange;">${data[item.key][key][i].fistTo20}</b>
													 <br/>
													  ------------		
												  </c:if>													
												 <br/>
												  	${data[item.key][key][i].outMs}
											</c:if>
										</td>
										
										<td style="word-break:break-all">
											<c:set var="key" value="it" />
											<c:if test="${fn:length(data[item.key][key])>i}">
												<a target="_blank" href="${data[item.key][key][i].link}">${data[item.key][key][i].catalogName}</a><br/>
												<div style="display: none" class="body">
													${data[item.key][key][i].tip}
													<div class="modal-footer">
														<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
													</div>
												</div>
												<a href="#"  class="btn btn-small btn-info tip"  onclick="display_by(this)">Product</a>
												<br/>
												------------															
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].styleMs}">
													M.S:(${data[item.key][key][i].marketShare}%  ${data[item.key][key][i].styleMs eq 'red'?'↓':''}${data[item.key][key][i].styleMs eq 'green'?'↑':''}  <c:if test="${not empty data[item.key][key][i].avg30MarketShare}">${data[item.key][key][i].avg30MarketShare}%</c:if>) 
												</span>
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].stylePirce}">
												  Avg.P:(${data[item.key][key][i].avgPrice}  ${data[item.key][key][i].stylePirce eq 'red'?'↓':''}${data[item.key][key][i].stylePirce eq 'green'?'↑':''}  <c:if test="${data[item.key][key][i].yestdayAvgPrice >0}">${data[item.key][key][i].yestdayAvgPrice}</c:if>)
												</span>
												<br/>
												 	Sales: ${data[item.key][key][i].sales}€
												 <br/>
													Sales volume: ${data[item.key][key][i].salesVolume}
												  <br/>
												  ------------	
												  <c:if test="${not empty data[item.key][key][i].fistTo20}">
													  First To Top20														
													 <br/>
													 	<b style="color: orange;">${data[item.key][key][i].fistTo20}</b>
													 <br/>
													  ------------		
												  </c:if>													
												 <br/>
												  	${data[item.key][key][i].outMs}
											</c:if>
										</td>
										
										<td style="word-break:break-all">
											<c:set var="key" value="es" />
											<c:if test="${fn:length(data[item.key][key])>i}">
												<a target="_blank" href="${data[item.key][key][i].link}">${data[item.key][key][i].catalogName}</a><br/>
												<div style="display: none" class="body">
													${data[item.key][key][i].tip}
													<div class="modal-footer">
														<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
													</div>
												</div>
												<a href="#"  class="btn btn-small btn-info tip"  onclick="display_by(this)">Product</a>
												<br/>
												------------															
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].styleMs}">
													M.S:(${data[item.key][key][i].marketShare}%  ${data[item.key][key][i].styleMs eq 'red'?'↓':''}${data[item.key][key][i].styleMs eq 'green'?'↑':''}  <c:if test="${not empty data[item.key][key][i].avg30MarketShare}">${data[item.key][key][i].avg30MarketShare}%</c:if>) 
												</span>
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].stylePirce}">
												  Avg.P:(${data[item.key][key][i].avgPrice}  ${data[item.key][key][i].stylePirce eq 'red'?'↓':''}${data[item.key][key][i].stylePirce eq 'green'?'↑':''}  <c:if test="${data[item.key][key][i].yestdayAvgPrice >0}">${data[item.key][key][i].yestdayAvgPrice}</c:if>)
												</span>
												<br/>
												 	Sales: ${data[item.key][key][i].sales}€
												 <br/>
													Sales volume: ${data[item.key][key][i].salesVolume}
												  <br/>
												  ------------	
												  <c:if test="${not empty data[item.key][key][i].fistTo20}">
													  First To Top20														
													 <br/>
													 	<b style="color: orange;">${data[item.key][key][i].fistTo20}</b>
													 <br/>
													  ------------		
												  </c:if>													
												 <br/>
												  	${data[item.key][key][i].outMs}
											</c:if>
										</td>
										
										<td style="word-break:break-all">
											<c:set var="key" value="jp" />
											<c:if test="${fn:length(data[item.key][key])>i}">
												<a target="_blank" href="${data[item.key][key][i].link}">${data[item.key][key][i].catalogName}</a><br/>
												<div style="display: none" class="body">
													${data[item.key][key][i].tip}
													<div class="modal-footer">
														<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
													</div>
												</div>
												<a href="#"  class="btn btn-small btn-info tip"  onclick="display_by(this)">Product</a>
												<br/>
												------------															
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].styleMs}">
													M.S:(${data[item.key][key][i].marketShare}%  ${data[item.key][key][i].styleMs eq 'red'?'↓':''}${data[item.key][key][i].styleMs eq 'green'?'↑':''}  <c:if test="${not empty data[item.key][key][i].avg30MarketShare}">${data[item.key][key][i].avg30MarketShare}%</c:if>) 
												</span>
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].stylePirce}">
												  Avg.P:(${data[item.key][key][i].avgPrice}  ${data[item.key][key][i].stylePirce eq 'red'?'↓':''}${data[item.key][key][i].stylePirce eq 'green'?'↑':''}  <c:if test="${data[item.key][key][i].yestdayAvgPrice >0}">${data[item.key][key][i].yestdayAvgPrice}</c:if>)
												</span>
												<br/>
												 	Sales: ${data[item.key][key][i].sales}€
												 <br/>
													Sales volume: ${data[item.key][key][i].salesVolume}
												  <br/>
												  ------------	
												  <c:if test="${not empty data[item.key][key][i].fistTo20}">
													  First To Top20														
													 <br/>
													 	<b style="color: orange;">${data[item.key][key][i].fistTo20}</b>
													 <br/>
													  ------------		
												  </c:if>													
												 <br/>
												  	${data[item.key][key][i].outMs}
											</c:if>
										</td>
										
										<td style="word-break:break-all">
											<c:set var="key" value="ca" />
											<c:if test="${fn:length(data[item.key][key])>i}">
												<a target="_blank" href="${data[item.key][key][i].link}">${data[item.key][key][i].catalogName}</a><br/>
												<div style="display: none" class="body">
													${data[item.key][key][i].tip}
													<div class="modal-footer">
														<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
													</div>
												</div>
												<a href="#"  class="btn btn-small btn-info tip"  onclick="display_by(this)">Product</a>
												<br/>
												------------															
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].styleMs}">
													M.S:(${data[item.key][key][i].marketShare}%  ${data[item.key][key][i].styleMs eq 'red'?'↓':''}${data[item.key][key][i].styleMs eq 'green'?'↑':''}  <c:if test="${not empty data[item.key][key][i].avg30MarketShare}">${data[item.key][key][i].avg30MarketShare}%</c:if>) 
												</span>
												<br/>
												<span style="font-size: 12px;color:${data[item.key][key][i].stylePirce}">
												  Avg.P:(${data[item.key][key][i].avgPrice}  ${data[item.key][key][i].stylePirce eq 'red'?'↓':''}${data[item.key][key][i].stylePirce eq 'green'?'↑':''}  <c:if test="${data[item.key][key][i].yestdayAvgPrice >0}">${data[item.key][key][i].yestdayAvgPrice}</c:if>)
												</span>
												<br/>
												 	Sales: ${data[item.key][key][i].sales}€
												 <br/>
													Sales volume: ${data[item.key][key][i].salesVolume}
												  <br/>
												  ------------	
												  <c:if test="${not empty data[item.key][key][i].fistTo20}">
													  First To Top20														
													 <br/>
													 	<b style="color: orange;">${data[item.key][key][i].fistTo20}</b>
													 <br/>
													  ------------		
												  </c:if>													
												 <br/>
												  	${data[item.key][key][i].outMs}
											</c:if>
										</td>
										
										
									</tr>
						    	</c:if>
						</c:forEach>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<div id="tipModel" class="modal hide fade" tabindex="-1" data-width="950">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3>Product Info</h3>
		</div>
		<div class="modal-body">
		</div>
	</div>
</body>
</html>
