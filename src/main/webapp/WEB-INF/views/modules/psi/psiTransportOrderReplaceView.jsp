<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>代发货物流编辑</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
		$(document).ready(function() {
			
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			
			$(".rate").on("blur",function(){
				if($(this).val()!=''){
					var div = $(this).parent().parent().parent();
					if(div.find(".firstAmount").val()!=''){
						div.find(".afterAmount").val(toDecimal(parseFloat(div.find(".firstAmount").val())*parseFloat($(this).val())));
					}
					var total=0;
					var parentDiv =div.parent();
					parentDiv.find(".afterAmount").each(function(){
						var innerDiv = $(this).parent().parent().parent();
						if(innerDiv.find(".afterAmount").val()!=''&&$(this).val()!=''){
							total=total+parseFloat($(this).val());
						}
					});
					
					$("#totalAmount").val(total);
				}
				
			});
			
			$(".firstAmount").on("blur",function(){
				var div = $(this).parent().parent().parent();
				var rate =div.find(".rate").val();
				if(rate!=''){
					div.find(".afterAmount").val(toDecimal(parseFloat(rate)*parseFloat($(this).val())));
				}
				
				var total=0;
				var parentDiv =div.parent();
				parentDiv.find(".afterAmount").each(function(){
					var innerDiv = $(this).parent().parent().parent();
					if(innerDiv.find(".afterAmount").val()!=''&&$(this).val()!=''){
						total=total+parseFloat($(this).val());
					}
				});
				
				$("#totalAmount").val(total);
			});
			
			
			
		});
		
		
		
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            return f.toFixed(2);  
	     };
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiTransportOrderReplace/">代发货物流列表</a></li>
		<li class="active"><a href="#">查看代发货物流</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiTransportOrderReplace" action="#" method="post" class="form-horizontal" enctype="multipart/form-data">
		
		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
		
		<div style="float:left;width:98%">
		<div class="control-group" style="float:left;width:50%;height:25px">
					<label class="control-label" style="width:100px">发货人信息:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="shipperInfo" type="text"  maxlength="10" readonly style="width:95%"  value="${psiTransportOrderReplace.shipperInfo}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">起运地:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="orgin" type="text"  maxlength="10" style="width:95%" readonly value="${psiTransportOrderReplace.orgin}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">目的地:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="destination" type="text"  maxlength="10" style="width:95%" readonly value="${psiTransportOrderReplace.destination}"/>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">航空/轮船公司:</label>
				<div class="controls" style="margin-left:120px" >
				<input name="carrier" type="text"  maxlength="10" style="width:95%" readonly value="${psiTransportOrderReplace.carrier}"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">离港日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="etdDate"  type="text" class="Wdate" readonly style="width:95%" value="<fmt:formatDate value="${psiTransportOrderReplace.etdDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">到港日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="etaDate" type="text" class="Wdate"  readonly style="width:95%" value="<fmt:formatDate value="${psiTransportOrderReplace.etaDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">入仓日期:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="arrivalDate" type="text" readonly style="width:95%" class="Wdate" value="<fmt:formatDate value="${psiTransportOrderReplace.arrivalDate}" pattern="yyyy-MM-dd" />" />
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">重量:</label>
				<div class="controls" style="margin-left:120px" >
					<div class="input-prepend input-append">
					<input name="weight" type="text" style="width:80%"  readonly id="weight" value="${psiTransportOrderReplace.weight}"/> <span class="add-on">kg</span>
					</div>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px">体积:</label>
				<div class="controls" style="margin-left:120px" >
					<div class="input-prepend input-append">
					<input type="text" name="volume"  style="width:80%" readonly value="${psiTransportOrderReplace.volume}"/> <span class="add-on">m³</span>
					</div>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">箱数:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="boxNumber" type="text" readonly style="width:95%" value="${psiTransportOrderReplace.boxNumber}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:100px">物流单号:</label>
					<div class="controls" style="margin-left:120px" >
						<input name="boxNumber" type="text"  readonly style="width:95%" value="${psiTransportOrderReplace.ladingBillNo}"/>
					</div>
			</div>
		</div>
		
		<blockquote  style="float:left;width:98%;height:25px">
			<div style="float: left; width:8%;height:15px"><p style="font-size: 14px">应付费用信息</p></div>
		</blockquote>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" >Local:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.localAmount}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency1:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.currency1}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Vendor1:</label>
					<div class="controls" style="margin-left:100px">
						 <input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.vendor1.nikename}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >汇率1:</label>
					<div class="controls" style="margin-left:100px">
						<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.rate1}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >金额1:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" style="width:90%" readonly  value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.localAmount*psiTransportOrderReplace.rate1}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" >运输费用:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.tranAmount}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency2:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.currency1}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Vendor2:</label>
					<div class="controls" style="margin-left:100px">
						 <input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.vendor2.nikename}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >汇率2:</label>
					<div class="controls" style="margin-left:100px">
						<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.rate2}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
					<label class="control-label" style="width:80px" >金额2:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" style="width:90%" readonly  value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.tranAmount*psiTransportOrderReplace.rate2}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">目的港费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.dapAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency3:</label>
						<div class="controls" style="margin-left:100px" >
						 <input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.currency3}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor3:</label>
						<div class="controls" style="margin-left:100px">
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.vendor3.nikename}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >汇率3:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.rate3}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >金额3:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.dapAmount*psiTransportOrderReplace.rate3}"/>
						</div>
				</div>
			</div>
		
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">其他费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.otherAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency4:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.currency4}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor4:</label>
						<div class="controls" style="margin-left:100px">
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.vendor4.nikename}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >汇率4:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.rate4}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:15%;height:25px">
						<label class="control-label" style="width:80px" >金额4:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" style="width:90%" readonly  value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.otherAmount*psiTransportOrderReplace.rate4}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">保险费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.insuranceAmount}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency5:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.currency5}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px">Vendor5:</label>
						<div class="controls" style="margin-left:100px">
							<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.vendor5.nikename}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:28%;height:25px"></div>
			</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">进口税:</label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.dutyTaxes}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">关税:</label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.taxTaxes}">
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency6:</label>
					<div class="controls"  style="margin-left:100px">
						<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.currency6}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Vendor6:</label>
					<div class="controls" style="margin-left:100px">
						<input type="text" style="width:90%" readonly  value="${psiTransportOrderReplace.vendor6.nikename}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:28%;height:25px"></div>
		</div>
		
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">总额:</label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.totalAmount}" />
				</div>
			</div>
		</div>
		
		<blockquote  style="float:left;width:98%;height:25px">
			<div style="float: left; width:8%;height:15px"><p style="font-size: 14px">应收费用信息</p></div>
		</blockquote>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" >Local:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.localAmountIn}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency1:</label>
					<div class="controls" style="margin-left:100px" >
					<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.currencyIn1}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:80px" >汇率1:</label>
					<div class="controls" style="margin-left:100px">
						<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.rateIn1}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:80px" >金额1:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" readonly style="width:90%"  value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.localAmountIn*psiTransportOrderReplace.rateIn1}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:25px">
					<label class="control-label" style="width:80px" >运输费用:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.tranAmountIn}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px">Currency2:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.currencyIn2}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:80px" >汇率2:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.rateIn2}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
					<label class="control-label" style="width:80px" >金额2:</label>
					<div class="controls" style="margin-left:100px" >
						<input type="text" readonly style="width:90%"  value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.tranAmountIn*psiTransportOrderReplace.rateIn2}" pattern="#.##" />"/>
					</div>
			</div>
		</div>
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">目的港费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.dapAmountIn}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency3:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.currencyIn3}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:80px" >汇率3:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.rateIn3}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:80px" >金额3:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" readonly style="width:90%"  value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.dapAmountIn*psiTransportOrderReplace.rateIn3}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
		
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">其他费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.otherAmountIn}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency4:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.currencyIn4}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:80px" >汇率4:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.rateIn4}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:25%;height:25px">
						<label class="control-label" style="width:80px" >金额4:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" readonly style="width:90%"  value="<fmt:formatNumber maxFractionDigits="2" value="${psiTransportOrderReplace.otherAmountIn*psiTransportOrderReplace.rateIn4}" pattern="#.##" />"/>
						</div>
				</div>
			</div>
			
			<div style="float:left;width:98%">
				<div class="control-group" style="float:left;width:30%;height:25px">
						<label class="control-label" style="width:80px">保险费用:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.insuranceAmountIn}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:20%;height:25px">
						<label class="control-label" style="width:80px" >Currency5:</label>
						<div class="controls" style="margin-left:100px" >
							<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.currencyIn5}"/>
						</div>
				</div>
				<div class="control-group" style="float:left;width:50%;height:25px"></div>
			</div>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">进口税:</label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.dutyTaxesIn}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:15%;height:25px">
				<label class="control-label" style="width:80px">关税:</label>
				<div class="controls" style="margin-left:100px" >
					<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.taxTaxesIn}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:20%;height:25px">
					<label class="control-label" style="width:80px" >Currency6:</label>
					<div class="controls"  style="margin-left:100px">
						<input type="text" readonly style="width:90%"  value="${psiTransportOrderReplace.currencyIn6}"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:50%;height:25px"></div>
		</div>
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">费用凭证信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		
		<c:if test="${not empty psiTransportOrderReplace.localPath || not empty psiTransportOrderReplace.tranPath || not empty psiTransportOrderReplace.dapPath || not empty psiTransportOrderReplace.otherPath || not empty psiTransportOrderReplace.insurancePath || not empty psiTransportOrderReplace.taxPath}">
		<div style="float:left;width:98%;height:50px;">
			<div class="control-group" style="float:left;width:98%;height:40px">
					<b>已上传凭证</b>：
					<c:if test="${not empty psiTransportOrderReplace.localPath}">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.localPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">local_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>  
					</c:if>
					<c:if test="${not empty psiTransportOrderReplace.tranPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.tranPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">运输费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrderReplace.dapPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.dapPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">目的港费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrderReplace.otherPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.otherPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">其他费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrderReplace.insurancePath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.insurancePath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">保费费用凭证_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach> 
					</c:if>
					
					<c:if test="${not empty psiTransportOrderReplace.taxPath}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<c:forEach items="${fn:split(psiTransportOrderReplace.taxPath,',')}" var="attchment" varStatus="i">
							<a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${attchment}'/>">税费费用_${i.index+1}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</c:forEach>
					</c:if>
					 
			</div>
		</div>
		</c:if>
		
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">下载PI/PL</p></div>
		</blockquote>
		
		<div style="float:left;width:98%;height:50px;">
			<div class="control-group" style="float:left;width:98%;height:40px">
			<c:forEach items="${fn:split(psiTransportOrderReplace.suffixName,'-')}" var="attFile" varStatus="i">
					<c:choose>
						<c:when test="${fn:contains('.png,.pdf,.jpg,.JPG',attFile)}">
							<c:choose>    
								<c:when test="${i.index eq 0 && attFile ne 'PI' && attFile ne ''}"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrderReplace.id}/${psiTransportOrderReplace.id}_PI${attFile}'/>">PI Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 1 && attFile ne 'PL' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrderReplace.id}/${psiTransportOrderReplace.id}_PL${attFile}'/>">PL Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 2 && attFile ne 'WB' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrderReplace.id}/${psiTransportOrderReplace.id}_WB${attFile}'/>">Bill of lading Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 3 && attFile ne 'TI' }"><a target="_blank" href="<c:url value='/data/site/psi/psiTransport/${psiTransportOrderReplace.id}/${psiTransportOrderReplace.id}_TI${attFile}'/>">Other Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${i.index eq 0 && attFile ne 'PI' && attFile ne '' }"><a href="${ctx}/psi/psiTransportOrderReplace/download?fileName=/${psiTransportOrderReplace.id}/${psiTransportOrderReplace.id}_PI${attFile}">PI Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 1 && attFile ne 'PL' }"><a href="${ctx}/psi/psiTransportOrderReplace/download?fileName=/${psiTransportOrderReplace.id}/${psiTransportOrderReplace.id}_PL${attFile}">PL Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 2 && attFile ne 'WB' }"><a href="${ctx}/psi/psiTransportOrderReplace/download?fileName=/${psiTransportOrderReplace.id}/${psiTransportOrderReplace.id}_WB${attFile}">Bill of lading Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
								<c:when test="${i.index eq 3 && attFile ne 'TI' }"><a href="${ctx}/psi/psiTransportOrderReplace/download?fileName=/${psiTransportOrderReplace.id}/${psiTransportOrderReplace.id}_TI${attFile}">Other Download</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
							</c:choose>
						</c:otherwise>
				</c:choose>
			</c:forEach>   
			</div>
		</div>
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">备注信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		<div style="float:left;width:98%;">
			<div class="control-group" style="float:left;width:98%;">
				<label class="control-label" style="width:80px">备注:</label>
				<div class="controls" style="margin-left:100px">
					<textarea name="remark"  style="width:100%;height:80px;" >${psiTransportOrderReplace.remark}</textarea>
				</div>
			</div>
		</div>
		
		
		<div class="form-actions" style="float:left;width:98%">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
