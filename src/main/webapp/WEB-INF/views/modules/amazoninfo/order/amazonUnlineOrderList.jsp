<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="amazon_order_tab1"/></title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		$(document).ready(function() {
			$("#selectT").change(function(){
				$("#searchForm").submit();
			});
			
			$("#selectAdmin").change(function(){
				$("#searchForm").submit();
			});
			
			
			$("#country").change(function(){
				$("#searchForm").submit();
			});
			
			$("#btnExport").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/unlineOrder/exportDetail");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/unlineOrder");
			});
			
			$("#btnExport2").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/unlineOrder/exportCount");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/unlineOrder");
			});
			
			$(".ladingBillNo").editable({validate:function(data){
				if(!(data)){
					return "跟踪号不能为空!";
				}
			},display:false,success:function(response,newValue){
				var param = {};
				var $this = $(this);
				var oldVal = $this.attr("keyVal");
				var nikename=$this.attr("keyName");
				param.id = $this.parent().find(":hidden").val();
				param.billNo = newValue;
				$.get("${ctx}/amazoninfo/unlineOrder/updateLadingBillNo?"+$.param(param),function(data){
					var arr=newValue.split("-");
					//eval('var site =${site}');
					var site="${site}";
					var siteArr=site.substring(1,site.length-1).split(",");
					var finalSite="";
					for(var i=0;i<siteArr.length;i++){
						if($.trim(nikename)==$.trim(siteArr[i].split("=http")[0])){
					        finalSite="http"+siteArr[i].split("=http")[1];
				        }
					}
					if(oldVal){
					 	if(finalSite.indexOf("$$")!=-1){
							var billNo1= $this.parent().find(".billNo1");
							billNo1.attr("href",finalSite.replace("$$",newValue));
						}else if(finalSite.indexOf("##")!=-1){
							var billNo2= $this.parent().find(".billNo2");
							billNo2.attr("href",finalSite.replace("##",arr[0])+arr[1]);
						}else{alert(3);
							var billNo3= $this.parent().find(".billNo3");
							billNo3.attr("href",finalSite);
						} 
					}else{
						if(finalSite.indexOf("$$")!=-1){
							$this.parent().append("<a  class='billNo1' target='_blank' href="+finalSite.replace("$$",newValue)+">跟踪</a>");
						}else if(finalSite.indexOf("##")!=-1){
							$this.parent().append("<a  class='billNo2' target='_blank' href="+finalSite.replace("##",arr[0])+arr[1]+">跟踪</a>");
						}else{
							$this.parent().append("<a  class='billNo3' target='_blank' href="+finalSite+">跟踪</a>");
						} 
					}
					$this.attr("keyVal",newValue);
					$.jBox.tip("保存成功！", 'info',{timeout:2000});
				});
				return true;
			}});
			
			
		});
		
		function outBound(button,id){
			  $.ajax({
     			   type: "POST",
     			   url: "${ctx}/amazoninfo/unlineOrder/deleverGoods?id="+id,
     			   async: false,
     			   success: function(msg){
     				  if(msg=="0"){
     					 $.jBox.tip('账单号'+id+'出库失败'); 
     				  }else if(msg=="1"){
     					 $.jBox.tip('账单号'+id+'出库成功'); 
     					 $(button).remove();
     				  }
     			   }
         		});
		}
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		function synchronizeMfnOrder(unlineId){
			top.$.jBox.confirm("Are you sure synchronize data to mfnOrder？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					   $.post("${ctx}/amazonAndEbay/mfnOrder/synchronizeOrder",{unlineId:unlineId},function(date){
						   if(date=='0'){
							   $.jBox.tip('Synchronize failed,Since the order is updated,please try it later(Wait a few minutes)');
							   //$("#searchForm").submit();
						   }else{
							   $.jBox.tip('Synchronize success');
							   $("#searchForm").submit();
						   }
						  
				       }); 
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		function synchronizeMfnOrder(unlineId,c){
			var $this=$(c).parent().parent();
			top.$.jBox.confirm("Are you sure sync data to mfnOrder？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					   $.post("${ctx}/amazonAndEbay/mfnOrder/synchronizeOrderStatus",{unlineId:unlineId},function(date){
						   if(date=='0'){
							   $.jBox.tip('Synchronize failed,Since the order is updated,please try it later(Wait a few minutes)');
						   }else if(date=='1'){
							   $.jBox.tip(unlineId+' Synchronize success');
							   $this.find(".state").text("Waiting for delivery");
							   $(c).remove();
						   }else{
							   $.jBox.tip(unlineId+","+date);
						   }
				       }); 
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		function deleteMfnOrder(unlineId){
			top.$.jBox.confirm("Are you sure delete？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					 window.location.href="${ctx}/amazoninfo/unlineOrder/updateCancelInfo?id="+unlineId;
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		function supportOrder(amazonOrderId,name){
			var params = {};
			params.amazonOrderId = amazonOrderId; 
			if(name=="管理员"){
				params.orderType='website';
			}else{
				params.orderType='offline';
			}
			
			window.open("${ctx}/amazoninfo/amazonTestOrReplace/createSupportEvent?"+$.param(params));
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">Unline Order List</a></li>
		<li><a href="${ctx}/amazoninfo/unlineOrder/add">Unline Order Add</a></li>
		<li><a href="${ctx}/amazoninfo/unlineOrder/otherOrderAdd">订单上传</a></li>	
	</ul>
	<form:form id="searchForm" modelAttribute="amazonUnlineOrder" action="${ctx}/amazoninfo/unlineOrder" method="post" class="breadcrumb form-search" cssStyle="height: 90px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 80px;line-height: 40px">
			<div style="height: 40px;">
				<label><spring:message code="amazon_order_tips3"/>：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="purchaseDate" value="<fmt:formatDate value="${amazonUnlineOrder.purchaseDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="lastUpdateDate" value="<fmt:formatDate value="${amazonUnlineOrder.lastUpdateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				WareHouse：<select name="salesChannel.id" id="country" style="width: 120px">
				<option value="">All</option>
					<c:forEach items="${stocks}" var="stock">
							<option value="${stock.id}">
						 	<c:choose>
								<c:when test="${stock.countrycode eq 'DE' }">Germany</c:when>
								<c:when test="${stock.countrycode eq 'CN'&&'21' eq stock.id}">China</c:when>
								<c:when test="${stock.countrycode eq 'CN'&&'130' eq stock.id}">LC_China</c:when>
								<c:when test="${stock.countrycode eq 'US' }">American</c:when>
								<c:otherwise>${stock.stockName}</c:otherwise>
							</c:choose>
							</option>
					</c:forEach>	
				</select>
				<script type="text/javascript">
					$("option[value='${amazonUnlineOrder.salesChannel.id}']").attr("selected","selected");				
				</script>
				
			    &nbsp;&nbsp;
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
					&nbsp;&nbsp;<input id="btnExport" class="btn btn-primary" type='button' value="<spring:message code="sys_but_export"/>"/>
					&nbsp;&nbsp;<input id="btnExport2" class="btn btn-primary" type='button' value="Summary Export"/>
			</div>
			<div style="height: 40px;">
				<label><spring:message code="amazon_order_tips1"/>：</label>
				<form:input path="sellerOrderId" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;&nbsp;
				<spring:message code="amazon_order_form35"/>：<select name="orderStatus" style="width: 150px" id="selectT">
								<option value=""><spring:message code='custom_event_all'/></option>
								<option value="PaymentPending"  ${amazonUnlineOrder.orderStatus eq 'PaymentPending'?'selected':''}>PaymentPending</option>
								<option value="PendingAvailability" ${amazonUnlineOrder.orderStatus eq 'PendingAvailability'?'selected':''}>PendingAvailability</option>
								<option value="Pending" ${amazonUnlineOrder.orderStatus eq 'Pending'?'selected':''}>Pending</option>
								<option value="Unshipped" ${amazonUnlineOrder.orderStatus eq 'Unshipped'?'selected':''}>Unshipped</option> 
								<option value="PartiallyShipped" ${amazonUnlineOrder.orderStatus eq 'PartiallyShipped'?'selected':''}>PartiallyShipped</option>    
								<option value="Shipped" ${amazonUnlineOrder.orderStatus eq 'Shipped'?'selected':''}>Shipped</option>    
								<option value="Canceled" ${amazonUnlineOrder.orderStatus eq 'Canceled'?'selected':''}>Canceled</option>
								<option value="Unfulfillable" ${amazonUnlineOrder.orderStatus eq 'Unfulfillable'?'selected':''}>Unfulfillable</option>
							</select>
							&nbsp;&nbsp;&nbsp;&nbsp;
					Type：<select name="orderChannel" style="width: 150px" id="selectAdmin">
								<option value=""><spring:message code='custom_event_all'/></option>
								<option value="0"  ${amazonUnlineOrder.orderChannel eq '0'?'selected':''}>Offline Order</option>
								<option value="1" ${amazonUnlineOrder.orderChannel eq '1'?'selected':''}>Official website</option>
								<option value="2" ${amazonUnlineOrder.orderChannel eq '2'?'selected':''}>Check24</option>
								<option value="3" ${amazonUnlineOrder.orderChannel eq '3'?'selected':''}>Other</option>
							</select>
							
							
						
			</div>
			
			
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		           <th style="width: 70px">ID</th>	
				   <th style="width: 70px"><spring:message code="amazon_order_form_tab_tips4"/></th>	
				   <th style="width: 70px">WareHouse</th>	
				   <th style="width: 140px"><spring:message code="amazon_order_form1"/></th>
				   <th style="width: 100px"><spring:message code="amazon_order_form2"/></th>
				   <th style="width: 70px"><spring:message code="amazon_order_form5"/></th>
				   <th style="width: 150px"><spring:message code="amazon_order_form9"/></th>
				   <th style="width: 120px"><spring:message code="amazon_order_form10"/></th> 
				   <th style="width: 80px"><spring:message code="amazon_order_form35"/></th> 
				   <th style="width:80px">Bill NO.</th>
				   <th><spring:message code="sys_label_tips_operate"/></th>
				   </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="amazonUnlineOrder">
			<tr>
			    <td rowspan="2" style="text-align: center;vertical-align: middle;"><b>${amazonUnlineOrder.id}</b></td>
				<td rowspan="2" style="text-align: center;vertical-align: middle;"><b>
				  <c:if test="${not empty amazonUnlineOrder.invoiceNo }">${amazonUnlineOrder.invoiceNo}</c:if>
				</b></td>
				<td>${amazonUnlineOrder.salesChannel.stockName}
				</td>
				<td><a href="${ctx}/amazoninfo/unlineOrder/form?id=${amazonUnlineOrder.id}">${amazonUnlineOrder.amazonOrderId}</a></td>
				<td><fmt:formatDate pattern="yyyy-M-d H:mm" value="${amazonUnlineOrder.purchaseDate}"/></td>
				<td>${amazonUnlineOrder.orderTotal}&nbsp;&nbsp;${amazonUnlineOrder.marketplaceId }</td>
				<td>${'null' eq amazonUnlineOrder.buyerName?'':amazonUnlineOrder.buyerName}</td>
				<td><a href="${ctx}/custom/sendEmail/form?sendEmail=${amazonUnlineOrder.buyerEmail}" >${'null' eq amazonUnlineOrder.buyerEmail?'':amazonUnlineOrder.buyerEmail}</a></td>
				<td class='state'>${amazonUnlineOrder.orderStatus}</td>
				<td>
				        <input type="hidden" value="${amazonUnlineOrder.id }"/>
						<a href="#" class="ladingBillNo" keyName="${amazonUnlineOrder.supplier}" key="billNO" keyVal="${amazonUnlineOrder.billNo}" data-type="text" data-pk="1" data-title="Enter ladingBillNo—${amazonUnlineOrder.supplier}" data-value="${amazonUnlineOrder.billNo}">编辑</a>
						<c:if test="${not empty amazonUnlineOrder.billNo&& not empty amazonUnlineOrder.supplier }">
						<c:choose>
						    <c:when test="${fn:contains(site[amazonUnlineOrder.supplier],'$$')}">
						      <a  class="billNo1" target="_blank" href="${fn:replace(site[amazonUnlineOrder.supplier],'$$',amazonUnlineOrder.billNo) }">跟踪</a>
						    </c:when>
						     <c:when test="${fn:contains(site[amazonUnlineOrder.supplier],'##')}">
						      <a class="billNo2"  target="_blank" href="${fn:replace(site[amazonUnlineOrder.supplier],'##',fn:substringBefore(amazonUnlineOrder.billNo,'-'))}${fn:substringAfter(amazonUnlineOrder.billNo,'-')}">跟踪</a>
						    </c:when>
						    <c:otherwise>
						      <a class="billNo3"  target="_blank" href="${site[amazonUnlineOrder.supplier] }">跟踪</a>
						    </c:otherwise>
						</c:choose> 
						</c:if>    
				</td>
				<td style="text-align: left;vertical-align: left;">
					  <a href="${ctx}/amazoninfo/unlineOrder/form?id=${amazonUnlineOrder.id}">view</a>
				&nbsp;&nbsp;
				
				<c:if test="${'管理员' ne amazonUnlineOrder.orderChannel&&'check24' ne amazonUnlineOrder.orderChannel}">
				  <%--   <shiro:hasPermission name="other:offlineOrder:edit">
				             <c:if test="${'PaymentPending' eq amazonUnlineOrder.orderStatus || 'Pending' eq amazonUnlineOrder.orderStatus ||'PendingAvailability' eq amazonUnlineOrder.orderStatus }">
					           <a href="${ctx}/amazoninfo/unlineOrder/edit?id=${amazonUnlineOrder.id}">edit</a>
							 </c:if>
							 <c:if test="${amazonUnlineOrder.salesChannel.id==19}"> 
							     <c:choose>
									<c:when test="${'0' eq amazonUnlineOrder.outBound}">&nbsp;&nbsp;<a id="synchronizeMfnOrder" onclick="synchronizeMfnOrder('${amazonUnlineOrder.id}',this)">Synchronize_MfnOrder</a>
									    &nbsp;&nbsp;<a id="delete" onclick="deleteMfnOrder('${amazonUnlineOrder.id}')">delete</a>
									 </c:when>
									<c:otherwise></c:otherwise>
							     </c:choose>
				            </c:if>  
				    </shiro:hasPermission> --%>
				 <%--    <shiro:lacksPermission name="other:offlineOrder:edit"> --%>
				          <c:if test="${fns:getUser().name eq amazonUnlineOrder.orderChannel}"> 
							  <c:if test="${'PaymentPending' eq amazonUnlineOrder.orderStatus || 'Pending' eq amazonUnlineOrder.orderStatus ||'PendingAvailability' eq amazonUnlineOrder.orderStatus }">
							      <a href="${ctx}/amazoninfo/unlineOrder/edit?id=${amazonUnlineOrder.id}">edit</a>
							  </c:if>
							  <c:if test="${amazonUnlineOrder.salesChannel.id==19||amazonUnlineOrder.salesChannel.id==120}"> 
							     <c:choose>
									<c:when test="${'0' eq amazonUnlineOrder.outBound}">&nbsp;&nbsp;<a id="synchronizeMfnOrder" onclick="synchronizeMfnOrder('${amazonUnlineOrder.id}',this)">Synchronize_MfnOrder</a>
									    &nbsp;&nbsp;<a id="delete" onclick="deleteMfnOrder('${amazonUnlineOrder.id}')">delete</a>
									 </c:when>
									<c:otherwise></c:otherwise>
							     </c:choose>
						      </c:if>  
				         </c:if>
				  <%--   </shiro:lacksPermission> --%>
				    
				   <%--  <shiro:hasPermission name="psi:inventory:edit:CN"> --%>
				
				     <c:if test="${(amazonUnlineOrder.outBound eq '0'||amazonUnlineOrder.outBound eq '2')&&amazonUnlineOrder.salesChannel.id!=19&&'Canceled' ne amazonUnlineOrder.orderStatus}"> 
					   &nbsp;&nbsp;
					    <a href="${ctx}/amazoninfo/unlineOrder/outbound?id=${amazonUnlineOrder.id}">Out_Bound</a> 
				      </c:if>  
				   <%--  </shiro:hasPermission> --%>
				</c:if>     
				<c:if test="${'管理员' eq amazonUnlineOrder.orderChannel&&((amazonUnlineOrder.salesChannel.id==19&&'Shipped' eq amazonUnlineOrder.orderStatus)||(amazonUnlineOrder.salesChannel.id==120&&'Unshipped' eq amazonUnlineOrder.orderStatus))}">
				      <a  onclick="supportOrder('${amazonUnlineOrder.amazonOrderId}','${amazonUnlineOrder.orderChannel}');">Fulfillment</a>&nbsp;
				</c:if>      
				      
				     <c:if test="${'管理员' eq amazonUnlineOrder.orderChannel&&('Unshipped' eq amazonUnlineOrder.orderStatus||'Pending' eq amazonUnlineOrder.orderStatus) }">
					  <%-- <a href="${ctx}/amazoninfo/unlineOrder/edit?id=${amazonUnlineOrder.id}">edit</a> --%>
					<%--   <c:if test="${'PaymentPending' eq amazonUnlineOrder.orderStatus || 'Pending' eq amazonUnlineOrder.orderStatus ||'PendingAvailability' eq amazonUnlineOrder.orderStatus }">
					     &nbsp;&nbsp;<a id="delete" onclick="deleteMfnOrder('${amazonUnlineOrder.id}')">delete</a> 
					  </c:if> --%>
					  <shiro:hasPermission name="other:offlineOrder:edit">
						  <c:if test="${(amazonUnlineOrder.outBound eq '0'||amazonUnlineOrder.outBound eq '2')&&not empty amazonUnlineOrder.salesChannel}"> 
						      <a id="synchronizeMfnOrder" onclick="synchronizeMfnOrder('${amazonUnlineOrder.id}',this)">Synchronize_MfnOrder</a> 
						   &nbsp;&nbsp;
						   <%--  <c:choose>
						      <c:when test="${amazonUnlineOrder.salesChannel.id==19 }">
						         <a id="synchronizeMfnOrder" onclick="synchronizeMfnOrder('${amazonUnlineOrder.id}',this)">Synchronize_MfnOrder</a> 
						      </c:when>
						      <c:otherwise>
						        <a href="${ctx}/amazoninfo/unlineOrder/outbound?id=${amazonUnlineOrder.id}">Out_Bound</a>  
						      </c:otherwise>
						   </c:choose> --%>
					      </c:if>  
					    </shiro:hasPermission>  
				    </c:if>  
				</td>
			</tr>
			<tr>
				<td colspan="9">
				<c:if test="${not empty amazonUnlineOrder.orderChannel}">
					<spring:message code="amazon_order_form_tips9" />：${amazonUnlineOrder.orderChannel}
					<br/>
				</c:if>
				<c:if test="${not empty amazonUnlineOrder.cbaDisplayableShippingLabel}">
					Remark：${amazonUnlineOrder.cbaDisplayableShippingLabel}
					<br/>
				</c:if>
				<spring:message code="amazon_order_form_tab_tips6"/>：<br/>
					<c:forEach items="${amazonUnlineOrder.items}" var="item">
						Asin:<b style="font-size: 14px">${item.asin}</b>;Sku:<b style="font-size: 14px">${item.sellersku}</b>;<spring:message code="amazon_order_form23"/>:${item.quantityOrdered};<spring:message code="amazon_order_form24"/>:${item.itemPrice};rate:${item.itemTax}%;<br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
