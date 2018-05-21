<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			//var flag=0;
			
			$(".state,#claimCode").on("change",function(){
				$("#searchForm").submit();   
			});
			
			$(".proType").on("change",function(){
				$("#searchForm").submit();   
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#accountName").val('');
				$("#searchForm").submit();
			});
			
			 $("#contentTable").on('click', '.remove-row', function(e){
				  e.preventDefault();
					//取消原来输入的数据
					var tr = $(this).parent().parent();
					var itemId = tr.find(".itemId").val();
					if(itemId){
						var param = {};
						param.itemId = itemId;
						$.get("${ctx}/amazoninfo/promotionsWarning/deleteItem?"+$.param(param),function(data){
							if(!(data)){    
								top.$.jBox.tip("删除失败！", 'info',{timeout:2000});
							}else{
								top.$.jBox.tip("删除成功！", 'info',{timeout:2000});
								tr.remove();
							}
						});
					}else{
						 tr.remove();
					}
					
			});
			 
			 $("#contentTable").on('click', '.save-row', function(e){
					e.preventDefault();
					var tr =$(this).parent().parent();
					var itemId =tr.find(".itemId").val();
					var $this=$(this);
					var productNameColor=tr.find("select[name='productNameColor']").val();
					var param = {};
					param.productNameColor=productNameColor;
					param.asin = tr.find("input[name='asin']").val();
					param.halfHourQuantity = tr.find("input[name='halfHourQuantity']").val();
					param.cumulativeQuantity = tr.find("input[name='cumulativeQuantity']").val();
					param.orderId = tr.find("input[name='orderId']").val();
					param.id = itemId;
					
					$.get("${ctx}/amazoninfo/promotionsWarning/saveOrderItem?"+$.param(param),function(data){
						if(!(data)){    
							top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
						}else{
							$this.remove();
							top.$.jBox.tip("保存成功！", 'info',{timeout:2000});
							$this.parent().find(".itemId").val(data);
							console.log($this.parent().find(".itemId").val());
						}
					});
					return true;
			  });		
			  
			  $(".add-row").on("click",function(e){
					e.preventDefault();
					var tbody=$(this).parent().parent();
					var id=$(this).attr('idVal');
					var tr=$("<tr></tr>");
					var options="<option value=''>--请选择--</option>";
					<c:forEach items="${asinMap}" var="asinMap">
                         options+='<option value="${asinMap.value}" asinVal="${asinMap.key}">${asinMap.value}</option>';   
				   </c:forEach>
					tr.append("<td><select style='width: 90%'  class='productNameColor required' name='productNameColor' onChange='setAsin(this);'>"+options+"</select></td>");
					tr.append("<td><input type='text' maxlength='50' style='width: 85%'  name='asin' /></td>");
					tr.append("<td><input type='hidden' value="+id+"  name='orderId' /></td>");
					tr.append("<td></td>");
		            tr.append("<td><input type='text' maxlength='11'  style='width: 85%'  name='halfHourQuantity'  class='number' /></td>");
		            tr.append("<td><input type='text' maxlength='11'  style='width: 85%'  name='cumulativeQuantity'  class='number' /></td>");
		            tr.append("<td></td>");
		            tr.append("<td colspan='6'><input type='hidden' class='itemId' /> <a class='save-row'>保存</a>&nbsp;&nbsp;</br><a href='#' class='remove-row'>删除</a></td>");
		            tr.find("select.productNameColor").select2();
		            tbody.after(tr);
					
				});
			
			$(".open").click(function(e){
				var className = $(this).parent().find("input[type='hidden']").val();
				var promotionId = $(this).parent().parent().find(".promotionId").text();
				var promotionCountry = $(this).parent().parent().find(".promotionCountry").text();
				var statu=$(this).parent().find(".warningSta").text();
				var startDate=$(this).attr("startVal");
				var endDate=$(this).attr("endVal");
				if($(this).text()=='概要'){
					$(this).text('关闭');
					if(statu!='0'){
						var hasFlag=0;
						$("#contentTable tbody tr."+className).each(function(){
							var temp=$(this).find(".timelyCumulative").text();
							if(temp!=''){
								hasFlag=1;
							}
						});
						if(hasFlag==0){
							$("#contentTable tbody tr."+className).each(function(){
								//如果有值就不重新查,及时累计销量
								var params={};
								params.asin=$(this).find(".asin").text();
								params.promId=promotionId;
								params.country=promotionCountry;
								params.start=startDate;
								params.end=endDate;
								
								var cumulativeQuantity =0;
								$.ajax({
								    type: 'get',
								    async:false,
								    url: '${ctx}/amazoninfo/order/ajaxCumulativeByAsin' ,
								    data: $.param(params),
								    success:function(data){ 
								    	cumulativeQuantity=data;
							        }
								});
								$(this).find(".timelyCumulative").text(cumulativeQuantity);
							});
						}
					}
				}else{
					$(this).text('概要');
				}
				
				$("*[name='"+className+"']").toggle();
			});
			
			$(".edit").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(":hidden").val();
					param.quantity = newValue;
					$.get("${ctx}/amazoninfo/promotionsWarning/halfHourQuantity?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("更新半小时销量限制成功！", 'info',{timeout:2000});
						}
					});
					return true;
		    }});
			
			$(".edit2").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(":hidden").val();
					param.quantity = newValue;
					$.get("${ctx}/amazoninfo/promotionsWarning/cumulativeQuantity?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("更新累计销量限制成功！", 'info',{timeout:2000});
						}
					});
					return true;
		    }});
			
			$(".editRemark").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(":hidden").val();
					param.remark = newValue;
					$.get("${ctx}/amazoninfo/promotionsWarning/updateRemark?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("更新备注成功！", 'info',{timeout:2000});
						}
					});
					return true;
		    }});
			
			$("#synchronizePromotions").click(function(){
				var params = {};
				var accountName=$("#accountName").val();
				params['accountName'] =accountName;
				top.$.jBox.confirm("Are you sure synchronize promotions code？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						top.$.jBox.tip("Please Waiting for 5 minutes！", 'loading',{timeout:10000});
						$.post("${ctx}/amazoninfo/promotionsWarning/synchronizePromotions",$.param(params),function(date){
							 setTimeout("refreshForm()",1000*60*5);
					    }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#export").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/promotionsWarning/export");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/promotionsWarning/");
			});
			
			$("#aboutMe").click(function(){
				if(this.checked){
					$("#aboutMeVal").val('1');
				}else{
					$("#aboutMeVal").val('');
				}
				$("#searchForm").submit();
			});
			
			$("#aboutMe2").click(function(){
				if(this.checked){
					$("#aboutMeVal2").val('${cuser.id}');
				}else{
					$("#aboutMeVal2").val('');
				}
				$("#searchForm").submit();
			});
			
			$(".synBtn").click(function(e){
				var className = $(this).parent().parent().parent().parent().find("input[type='hidden']").val();
				var p={};
				p.id=className;
				$.ajax({
				    type: 'get',
				    async:false,
				    url: '${ctx}/amazoninfo/promotionsWarning/promotionsProfit' ,
				    data: $.param(p),
				    success:function(data){ 
				    	if(data){//not 
				    		console.log(data);
				    		top.$.jBox.open(data,"折扣分析",500,150,{buttons: {'关闭': true}});  
				    	}else{
				    		top.$.jBox.tip("折扣正常","info",{timeout:1000});
							return false;
				    	}
			        }
				});
			
				
			});
			
			$(".warn").click(function(e){
				var className = $(this).parent().find("input[type='hidden']").val();
				var promotionId = $(this).parent().parent().find(".promotionId").text();
				var promotionCountry = $(this).parent().parent().find(".promotionCountry").text();
				var statu=$(this).parent().find(".warningSta").text();
				var p={};
				p.id=className;
				$.ajax({
				    type: 'get',
				    async:false,
				    url: '${ctx}/amazoninfo/promotionsWarning/isExistItem' ,
				    data: $.param(p),
				    success:function(data){ 
				    	if(data=='0'){//监控
				    		window.location.href="${ctx}/amazoninfo/promotionsWarning/updateState?id="+className+"&state=1&country="+promotionCountry;
				    	}else{
							if(statu!='0'){
									if(flag==0){
										$("#contentTable tbody tr."+className).each(function(){
											//如果有值就不重新查,及时累计销量
											var params={};
											params.asin=$(this).find(".asin").text();
											params.promId=promotionId;
											params.country=promotionCountry;
											var cumulativeQuantity =0;
											$.ajax({
											    type: 'get',
											    async:false,
											    url: '${ctx}/amazoninfo/order/ajaxCumulativeByAsin' ,
											    data: $.param(params),
											    success:function(data){ 
											    	cumulativeQuantity=data;
										        }
											});
											$(this).find(".timelyCumulative").text(cumulativeQuantity);
										});
									}
									flag=1;
								}
				    		$("*[name='"+className+"']").toggle();
				    	}
			        }
				});
				
				
			});
			
			
			$(".passBtn").click(function(e){
				var className = $(this).parent().parent().parent().parent().find("input[type='hidden']").val();
				var p={};
				p.id=className;
				p.state='1';
				var $this=$(this);
				top.$.jBox.confirm("确认折扣审核通过吗？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$.ajax({
						    type: 'get',
						    async:false,
						    url: '${ctx}/amazoninfo/promotionsWarning/checkState' ,
						    data: $.param(p),
						    success:function(data){ 
						    	if(data){//not 
						    		//setTimeout("alert('折扣审核通过成功！')",2000);
						    		top.$.jBox.tip("折扣审核通过成功！", 'info',{timeout:2000}); 
						    		$this.parent().parent().parent().parent().find('.passBtn').remove();
						    	}else{
						    		top.$.jBox.tip("折扣审核通过失败！", 'info',{timeout:2000});
									return false;
						    	}
					        }
						});
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			//
			$(".noPassBtn").click(function(e){
				var className = $(this).parent().parent().parent().parent().find("input[type='hidden']").val();
				var p={};
				p.id=className;
				p.state='4';
				var $this=$(this);
				top.$.jBox.confirm("驳回折扣同时会关闭亚马逊后台折扣，确认驳回吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$.ajax({
						    type: 'get',
						    async:false,
						    url: '${ctx}/amazoninfo/promotionsWarning/noPassPromotion' ,
						    data: $.param(p),
						    success:function(data){ 
						    	if(data){//not 
						    		top.$.jBox.tip("折扣驳回成功！", 'info',{timeout:2000}); 
						    		$this.parent().parent().parent().parent().find('.noPassBtn').remove();
						    	}else{
						    		top.$.jBox.tip("折扣驳回失败！", 'info',{timeout:2000});
									return false;
						    	}
					        }
						});
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
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
					sort = (sort[1]&&sort[1].toUpperCase()=="ASC"?"DESC":"ASC");
					$("#orderBy").val(order+" ASC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" DESC");
				}
				page();
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
		
		function setAsin(c){
			var asin=$(c).find("option:selected").attr("asinVal");
			$(c).parent().parent().find("[name='asin']").val(asin);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty amazonPromotionsWarning.country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<%-- <c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${amazonPromotionsWarning.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach> --%>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
		 <c:if test="${dic.value ne 'com.unitek'}">
			    <c:choose>
			       <c:when test="${fn:length(accountMap[dic.value])>1}">
			           <li class="dropdown ${amazonPromotionsWarning.country eq dic.value ?'active':''}"  >
						    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">${dic.label}<c:if test="${dic.value eq amazonPromotionsWarning.country}">${amazonPromotionsWarning.accountName}</c:if><b class="caret"></b> </a>
						    <ul class="dropdown-menu" style="min-width:110px">
						         <c:forEach items="${accountMap[dic.value]}" var="account">
						             <li><a href="${ctx}/amazoninfo/promotionsWarning/?country=${dic.value}&accountName=${account}">${account}</a></li>	
						         </c:forEach>
						    </ul>
		               </li>
			       </c:when>
			        <c:when test="${fn:length(accountMap[dic.value])==1}">
			           <c:forEach items="${accountMap[dic.value]}" var="account">
						    <li class="${amazonPromotionsWarning.country eq dic.value ?'active':''}"><a href="${ctx}/amazoninfo/promotionsWarning/?country=${dic.value}&accountName=${account}">${account}</a></li>	
						</c:forEach>	
			       </c:when>
			       <c:otherwise>
					    <li class="${amazonPromotionsWarning.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			       </c:otherwise>
			    </c:choose>
		  </c:if> 
		</c:forEach>
		<li><a href="${ctx}/amazoninfo/promotionsWarning/add">新建折扣审核</a></li>	
	</ul>
	<form id="searchForm"  action="${ctx}/amazoninfo/promotionsWarning/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input name="country" id="country" type="hidden" value="${amazonPromotionsWarning.country}"/>
		<input name="accountName" id="accountName" type="hidden" value="${amazonPromotionsWarning.accountName}"/>
		<label>TrackingId/Code：</label>	<input name="promotionId" type="text"  class="input-small" value="${amazonPromotionsWarning.promotionId}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<label>产品名：</label>	<input name="remark" type="text" maxlength="50" class="input-small" value="${amazonPromotionsWarning.remark}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		
		<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${amazonPromotionsWarning.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${amazonPromotionsWarning.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;

			<label>类型：</label>
			<select name="proType"  style="width:150px" class="proType">
				<option value="" >All</option>
				<option value="0" ${amazonPromotionsWarning.proType eq '0'?'selected':''}>亏本非淘汰品促销</option>
				<option value="1" ${amazonPromotionsWarning.proType eq '1'?'selected':''}>亏本淘汰品促销</option>
				<option value="2" ${amazonPromotionsWarning.proType eq '2'?'selected':''}>有利润促销</option>
				<option value="3" ${amazonPromotionsWarning.proType eq '3'?'selected':''}>特批</option>
				<option value="4" ${amazonPromotionsWarning.proType eq '4'?'selected':''}>常规</option>
			</select> 
			&nbsp;&nbsp;	   
				   
				   
			<br/><br/><label>状态：</label>
			<select name="warningSta" class="state" style="width:150px" class="state">
				<option value="8" >All</option>
				<option value="" ${empty amazonPromotionsWarning.warningSta?'selected':''}>新建和正在监控</option>
				<option value="0" ${amazonPromotionsWarning.warningSta eq '0'?'selected':''}>新建</option>
				<option value="1" ${amazonPromotionsWarning.warningSta eq '1'?'selected':''}>正在监控</option>
				<option value="2" ${amazonPromotionsWarning.warningSta eq '2'?'selected':''}>已结束</option>
				<option value="3" ${amazonPromotionsWarning.warningSta eq '3'?'selected':''}>草稿</option>
				<option value="4" ${amazonPromotionsWarning.warningSta eq '4'?'selected':''}>已取消</option>
			</select> 
			&nbsp;&nbsp;
			<spring:message code="amazon_sales_product_line"/>:<select id="claimCode" style="width: 100px" name="claimCode">
				<option value="">--All--</option>
				<c:forEach items="${groupType}" var="groupType">
					<option value="${groupType.id}" ${groupType.id eq amazonPromotionsWarning.claimCode?'selected':''}>${groupType.name}</option>			
				</c:forEach>
			</select>
			&nbsp;&nbsp;
			<input type="checkbox" id="aboutMe" ${empty amazonPromotionsWarning.oneRedemption?'':'checked'}/>只含F-折扣
			<input type="hidden" name="oneRedemption" id="aboutMeVal" value="${empty amazonPromotionsWarning.oneRedemption?'':'1'}">
			&nbsp;&nbsp;
			<input type="checkbox" id="aboutMe2" ${not empty amazonPromotionsWarning.createUser.id?'checked':''}/>与我相关	&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="hidden" name="createUser.id" id="aboutMeVal2" value="${not empty amazonPromotionsWarning.createUser.id?cuser.id:''}">
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<input id="export" class="btn btn-primary" type="button" value="导出"/>
	<%-- 	<c:if test="${not empty amazonPromotionsWarning.country }">
		   <input id="synchronizePromotions" class="btn btn-primary" type="button" value="同步折扣"/>
		</c:if>  --%>
			<div class="btn-group">
						<button type="button" class="btn btn-primary">折扣率导出</button>
						<button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
							<span class="caret"></span>
							<span class="sr-only"></span>
						</button>
						<ul class="dropdown-menu" >
							<li><a href="${ctx}/amazoninfo/promotionsWarning/exportPromotionsByDate?type=1&country=${amazonPromotionsWarning.country }">按日</a></li>
							<li><a href="${ctx}/amazoninfo/promotionsWarning/exportPromotionsByDate?type=2&country=${amazonPromotionsWarning.country }">按周</a></li>
							<li><a href="${ctx}/amazoninfo/promotionsWarning/exportPromotionsByDate?type=3&country=${amazonPromotionsWarning.country }">按月</a></li>
						</ul>
				</div>
	</form>
	<div class="alert">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	    ERP每天6:00,13:00,17:00自动扫描折扣信息,未经审核的折扣一律关闭   (红色Tracking Id表示特批折扣)
	</div>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:13%">Tracking Id</th><th style="width:4%">Country</th><th style="width:8%" class="sort startDate">StartDate</th><th class="sort endDate" style="width:8%">EndDate</th><th style="width:8%">Purchased Items</th><th style="width:8%">Buyer Gets</th><th style="width:3%"><a title='One redemption per customer'>ORPC</a></th><th style="width:4%">CreateUser</th><th style="width:4%">CheckUser</th><th style="width:6%">CreateDate</th><th style="width:5%">Status</th><th style="width:8%">Result</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="discountWarning">
			<tr>
				<td><span class="promotionId"><a style="${'3' eq discountWarning.proType?'color: red;':''}" href='${discountWarning.promotionLink}' target="_blank">${discountWarning.promotionId}</a></span><span style="display:none;" class="promotionCountry">${discountWarning.country}</span></td>
				<td>${fns:getDictLabel(discountWarning.country, 'platform', defaultValue)}</td>
				<td><fmt:formatDate value="${discountWarning.startDate}" pattern="yyyy-MM-dd HH:mm"/> </td>
				<td><fmt:formatDate value="${discountWarning.endDate}" pattern="yyyy-MM-dd HH:mm"/> </td>
				<td>${discountWarning.purchasedItems}</td>
				<td>${discountWarning.buyerGets}</td>
				<td>${discountWarning.oneRedemption}</td>
				<td>${discountWarning.createUser.name}</td>
				<td>${discountWarning.checkUser.name}<c:if test="${empty discountWarning.checkDate||not empty discountWarning.checkFlag }"><span style='color:#FF3030;'>(未审核)</span></c:if></td>
				<td><fmt:formatDate value="${discountWarning.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
				    <c:if test="${discountWarning.warningSta eq '0'}"><span class="label label-success">新建</span></c:if>
					<c:if test="${discountWarning.warningSta eq '1'}"><span class="label label-success">正在监控</span></c:if>
					<c:if test="${discountWarning.warningSta eq '3'}"><span class="label label-success">草稿</span></c:if>
					<c:if test="${discountWarning.warningSta eq '2'}">
					<span class="label label-important">已结束</span>
					</c:if>
					<c:if test="${discountWarning.warningSta eq '4'}">
					<span class="label label-important">已取消</span>
					</c:if>
				</td>
				<td>
				 <%--  <c:if test="${discountWarning.warningSta eq '0' }">
					   <input type="hidden" value="${discountWarning.id}" /> 
					   <a href="#" class="editRemark"  data-type="text" keyVal="${discountWarning.id}" data-pk="1" data-title="Enter Remark" >
						     ${discountWarning.remark}
					   </a>
				  </c:if> 
				   <c:if test="${discountWarning.warningSta ne '0' }"> --%>
				       ${discountWarning.remark}
				  <%--  </c:if> --%>
				</td>
				<td><input type="hidden" value="${discountWarning.id}"/>
				    <span class="warningSta" style="display: none">${discountWarning.warningSta}</span>
				    <a class="btn btn-small btn-info open" startVal='${discountWarning.startDate}'  endVal='${discountWarning.endDate}'>概要</a>&nbsp;&nbsp;
				    
				<div class="btn-group">
			
					  <button type="button" class="btn btn-small"><spring:message code="sys_but_edit"/></button>
						   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
					</button>
					<ul class="dropdown-menu" >
						<c:if test="${discountWarning.warningSta eq '0'}"> 	
						  <shiro:hasPermission name="amazoninfo:feedSubmission:all">
						    <li> <a >监控</a></li>
						  </shiro:hasPermission>
						  <shiro:lacksPermission name="amazoninfo:feedSubmission:all">
						    <shiro:hasPermission name="amazoninfo:feedSubmission:${amazonPromotionsWarning.country }">
						      <li> <a >监控</a></li>
						    </shiro:hasPermission>
						  </shiro:lacksPermission>
					    </c:if>
					    
					    <c:if test="${discountWarning.warningSta eq '3'}">
					    <shiro:hasPermission name="amazon:promotionsWarning:review">
					       <c:if test="${fns:getUser().name ne discountWarning.createUser.name }">
					          <li> <a  class='passBtn'>通过</a></li>
					         <li>  <a  class='noPassBtn'>驳回</a></li>
					       </c:if>
					    </shiro:hasPermission>
					    <shiro:lacksPermission name="amazon:promotionsWarning:review">
					        <c:if test="${fns:getUser().name eq discountWarning.checkUser.name }">
					           <li>  <a  class='passBtn'>通过</a></li>
					           <li>  <a  class='noPassBtn'>驳回</a></li>
					        </c:if>
					    </shiro:lacksPermission>
					  </c:if>
					  
					  	<c:if test="${(fns:getUser().name eq discountWarning.checkUser.name||fns:getUser().name eq discountWarning.createUser.name||fns:getUser().name eq 'eileen'||fns:getUser().name eq '管理员')&&not empty discountWarning.buyerGets}">
				             <li>   <a class="synBtn">分析</a>  </li>  
				       </c:if>  
				       
				       <c:if test="${not empty discountWarning.checkFlag}">
				        <shiro:hasPermission name="amazon:promotionsWarning:review">
				           <c:if test="${fns:getUser().name ne discountWarning.createUser.name }">
					        <li>  <a  onclick="return confirm('再次确认折扣审核通过吗?', this.href)" href="${ctx}/amazoninfo/promotionsWarning/nextCheckState?id=${discountWarning.id}&country=${discountWarning.country}">再次通过</a>  </li>  
					        <li>  <a  onclick="return confirm('驳回折扣同时会关闭亚马逊后台折扣，确认驳回吗?', this.href)" href="${ctx}/amazoninfo/promotionsWarning/updateState?id=${discountWarning.id}&state=4&country=${discountWarning.country}">驳回</a>  </li>  
				           </c:if>
				        </shiro:hasPermission>
				      </c:if>
				      
				       <c:if test="${discountWarning.warningSta ne '2'&&discountWarning.warningSta ne '4'}">
				             <li><a  onclick="return confirm('确认关闭亚马逊后台折扣?', this.href)" href="${ctx}/amazoninfo/promotionsWarning/closePromotion?id=${discountWarning.id}&country=${discountWarning.country}">即时关闭</a></li> 
				       </c:if>
				       
				       <c:if test="${(empty discountWarning.specialCheckUser || (not discountWarning.specialCheckUser&&!fn:contains(discountWarning.specialCheckUser,fns:getUser().name)))&&fns:getUser().name ne discountWarning.createUser.name&&fns:getUser().name ne discountWarning.checkUser.name&&not empty discountWarning.checkRate&&discountWarning.warningSta eq '1'&&'3' eq discountWarning.proType }">
				             <li><a  onclick="return confirm('确认审核通过特批折扣?', this.href)" href="${ctx}/amazoninfo/promotionsWarning/specialCheck?id=${discountWarning.id}&country=${discountWarning.country}">特批审核</a></li> 
				       </c:if>
				      
					</ul>
				</div>
				
				
					<%-- <c:if test="${discountWarning.warningSta eq '0'&&fn:contains(discountWarning.promotionId,'F-')}"> --%>
				   
				  
				
				</td>
			</tr>
			<tr style="background-color:#D2E9FF;display: none" name="${discountWarning.id}"><td>ProductName<c:if test="${fns:getUser().name eq discountWarning.createUser.name}"> <a href="#" class="add-row" idVal='${discountWarning.id }'><span class="icon-plus"></span></a> </c:if></td><td>Asin</td><td>最低价格</td><td>库存数</td>
				<%-- <c:if test="${fns:startsWith(discountWarning.promotionId,'F-') }"> --%>
				  <td>半小时销量限制</td><td>累计销量限制</td>
				  <td colspan='3'>即时累计销量</td>
				<%--   <c:if test="${fns:getUser().name eq discountWarning.checkUser.name||fns:getUser().name eq discountWarning.createUser.name}">
				    <td>利润</td>
				  </c:if>
				   --%>
				  <td>备注</td>
				  <td colspan="3">
				    <c:if test="${not empty discountWarning.proType&&!fns:startsWith(discountWarning.promotionId,'C-')&&!fns:startsWith(discountWarning.promotionId,'R-')}">
				       <b> ${"0" eq discountWarning.proType?"亏本非淘汰品促销":("1" eq discountWarning.proType?"亏本淘汰品促销":("2" eq discountWarning.proType?"有利润促销":("3" eq discountWarning.proType?"特批":"常规")))}:</b>
				    </c:if> 
				  ${discountWarning.reason }</td>
				<%-- </c:if>
				<c:if test="${!fns:startsWith(discountWarning.promotionId,'F-') }">
				   <td colspan="7"></td>
				</c:if> --%>
			</tr>
			<c:if test="${fn:length(discountWarning.items)>0}">
			   
				
				<c:forEach items="${discountWarning.items}" var="item">
				        
						<c:if test="${'0' eq item.delFlag}">
						        <c:set var='key' value='${discountWarning.country }_${item.asin}'/>
							     <c:set var='safeKey' value='${item.productNameColor}_${discountWarning.country }'/>
								<tr style="background-color:#D2E9FF;display: none" name="${discountWarning.id}" class="${discountWarning.id}">
								<td>
								<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productNameColor}">${item.productNameColor}</a>
								</td><td><span class="asin">${item.asin}</span></td>
								<td>${minPrice[key]}</td>
								<td>
									<c:choose>
										<c:when test="${fn:contains(asinList,item.asin)&&fn:contains('de,fr,it,es,uk',discountWarning.country)}">
										    <c:set var='euFbaKey' value="${fn:split('discountWarning.accountName','_')[0]}_eu"/> 
										    ${fbaMap[euFbaKey][item.asin]}
										</c:when>
										<c:otherwise>${fbaMap[discountWarning.accountName][item.asin]}</c:otherwise>
									</c:choose>
								
								</td>
								
								<%-- <c:if test="${fns:startsWith(discountWarning.promotionId,'F-') }"> --%>
								<td>
								    <c:if test="${discountWarning.warningSta ne '2' }">
								      <input type="hidden" value="${item.id}" /> 
									  
								       <shiro:hasPermission name="amazoninfo:feedSubmission:all">
								         <a href="#" class="edit"  data-type="text" keyVal="${item.id}" data-pk="1" data-title="Enter halfHourQuantity" >
								            ${item.halfHourQuantity}
								         </a>
								 	  </shiro:hasPermission>
								      <shiro:lacksPermission name="amazoninfo:feedSubmission:all">
									      <shiro:hasPermission name="amazoninfo:feedSubmission:${amazonPromotionsWarning.country }">
									        <a href="#" class="edit"  data-type="text" keyVal="${item.id}" data-pk="1" data-title="Enter halfHourQuantity" >
								               ${item.halfHourQuantity}
								            </a>
									      </shiro:hasPermission>
									        <shiro:lacksPermission name="amazoninfo:feedSubmission:${amazonPromotionsWarning.country }">
									          ${item.halfHourQuantity}
									      </shiro:lacksPermission>
								      </shiro:lacksPermission>
								    </c:if>
								    <c:if test="${discountWarning.warningSta eq '2' }">
								            ${item.halfHourQuantity}
								    </c:if> 
								</td>
								<td>
								     <c:if test="${discountWarning.warningSta ne '2' }">
								       <input type="hidden" value="${item.id}" /> 
									   
								        <shiro:hasPermission name="amazoninfo:feedSubmission:all">
								          <a href="#" class="edit2"  data-type="text" keyVal="${item.id}" data-pk="1" data-title="Enter cumulativeQuantity" >
								           ${item.cumulativeQuantity}
								         </a>
								 	  </shiro:hasPermission>
								      <shiro:lacksPermission name="amazoninfo:feedSubmission:all">
									      <shiro:hasPermission name="amazoninfo:feedSubmission:${amazonPromotionsWarning.country }">
									        <a href="#" class="edit2"  data-type="text" keyVal="${item.id}" data-pk="1" data-title="Enter cumulativeQuantity" >
								               ${item.cumulativeQuantity}
								            </a>
									      </shiro:hasPermission>
									      <shiro:lacksPermission name="amazoninfo:feedSubmission:${amazonPromotionsWarning.country }">
									          ${item.cumulativeQuantity}
									      </shiro:lacksPermission>
								      </shiro:lacksPermission>
								      </c:if>
								    <c:if test="${discountWarning.warningSta eq '2' }">
								            ${item.cumulativeQuantity}
								    </c:if> 
								</td>
								<td colspan='3'><span class="timelyCumulative"></span></td>
								<%-- <c:if test="${fns:getUser().name eq discountWarning.checkUser.name||fns:getUser().name eq discountWarning.createUser.name}">
								  <td><c:if test='${not empty safePriceMap[safeKey]}'><fmt:formatNumber  pattern="#######.##" value="${safePriceMap[safeKey]-fn:split(minPrice[key],',')[1]}"  maxFractionDigits="2" /></c:if></td>
								</c:if> --%>
								<td>${item.remark}</td>
								<td colspan="3"> 
						              <c:if test="${fns:getUser().name eq discountWarning.createUser.name}"> 
						                 <input type='hidden' class='itemId' value="${item.id}"/><a href='#' class='remove-row'>删除</a>
						            </c:if> 
				                </td> 
								<%-- </c:if>
								<c:if test="${!fns:startsWith(discountWarning.promotionId,'F-') }">
								 <td colspan="7"></td>
								</c:if> --%>
								
								</tr>
						</c:if>
				    
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
