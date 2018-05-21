<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Product Sku Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
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
		$(function(){
			
			$("#skuExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/psi/product/skulistExport");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/psi/product/skulist");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("select[name='type'],#country,#flag").change(function(){
				$("#searchForm").submit();
			});
			
			$("#btnSkuMate").click(function(){
				var params = {};
				params.platform ='${psiProduct.platform}';
				params.brand = '${psiProduct.brand}';
				window.location.href="${ctx}/psi/product/skuMate?"+$.param(params);
			});
			
			$(".countryHref").click(function(){
				$("input[name='platform']").val($(this).attr("key"));
				$("input[name='brand']").val('');
				$("#searchForm").submit();
			});
			
			$("#contentTable").treeTable({expandLevel : 2});
			
			$(".oldrow").editable({source: [
								//<c:forEach items="${skus}" var="sku">
								{id: '${sku}', text: '${sku}'},
								//</c:forEach>
			                             ],validate:function(data){
							                	if(data){
							                		var rs ="";
							                		$.ajax({
						                			   type: "POST",
						                			   url: "${ctx}/psi/product/validateSku",
						                			   data: "sku="+data+"&country=${psiProduct.platform}",
						                			   async: false,
						                			   success: function(msg){
						                				   rs = msg;		
						                			   }
							                		});
							                		if(rs){
							                			return rs;
							                		}
												}else{
													return "sku不能为空!!";
												}
							                },success:function(response,newValue){
				                 				var param = {};
				                				var $this = $(this);
				                				var oldVal = $this.text();
				                				param.barcodeId = $this.parent().parent().parent().find(".barcodeId").val(); 
				                				param.skuId = $this.parent().parent().find(".skuId").val();
				                				param.sku = encodeURIComponent(encodeURIComponent(newValue));
				                				param.country = '${psiProduct.platform}';
				                				param.accountName = '${psiProduct.brand}';
				                				$.get("${ctx}/psi/product/updateSku?"+$.param(param),function(data){
				                					if(!(data)){
				                						$this.text(oldVal);						
				                					}else{
				                						$.jBox.tip("sku保存成功！", 'info',{timeout:2000});
				                					}
				                				});
				                				return true;
			                }});
			
			
			$(".oldrow2").editable({
				showbuttons:'bottom',
				validate:function(data){
                	if(data){
                		var rs ="";
                		$.ajax({
            			   type: "POST",
            			   url: "${ctx}/psi/product/validateSku",
            			   data: "sku="+data+"&country=${psiProduct.platform}",
            			   async: false,
            			   success: function(msg){
            				   rs = msg;		
            			   }
                		});
                		if(rs){
                			return rs;
                		}
					}else{
						return "sku不能为空!!";
					}
                },
				success:function(response,newValue){
					var param = {};
    				var $this = $(this);
    				var oldVal = $this.text();
    				param.barcodeId = $this.parent().parent().parent().find(".barcodeId").val(); 
    				param.skuId = $this.parent().parent().find(".skuId").val();
    				param.sku = encodeURIComponent(encodeURIComponent(newValue));
    				param.country = '${psiProduct.platform}';
    				param.accountName = '${psiProduct.brand}';
    				$.get("${ctx}/psi/product/updateSku?"+$.param(param),function(data){
    					if(!(data)){
    						$this.text(oldVal);						
    					}else{
    						$.jBox.tip("sku保存成功！", 'info',{timeout:2000});
    					}
    				});
    				return true;
			 }});

			$("table :checked").attr("disabled","disabled");
			
		});
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		function addRow(id,btn){
			var tr = "<tr style='background-color: #D2E9FF'><td><input type=\"hidden\"  class=\"skuId\" /></td><td><a href=\"#\"  data-type=\"select2\" data-pk=\"1\"  data-title=\"Select sku\" class=\"newrow\" data-original-title=\"\" title=\"\"></a></td><td><c:if test="${psiProduct.platform ne 'ebay'&&psiProduct.platform ne 'ebay_com' }"><input class=\"newBtn\" type=\"checkbox\"  onchange=\"updateState(this,'"+id+"')\" /></c:if></td><td><span class='btn btn-danger' onclick='deleteRow(this)'>删除</span></td></tr> "; 
			$("#"+id).append(tr);
			$(".newrow").editable({source: [
								//<c:forEach items="${skus}" var="sku">
								{id: '${sku}', text: '${sku}'},
								//</c:forEach>
			                     ],validate:function(data){
					                	if(data){
					                		var rs ="";
					                		$.ajax({
				                			   type: "POST",
				                			   url: "${ctx}/psi/product/validateSku",
				                			   data: "sku="+data+"&country=${psiProduct.platform}",
				                			   async: false,
				                			   success: function(msg){
				                				   rs = msg;		
				                			   }
					                		});
					                		if(rs){
					                			return rs;
					                		}
										}else{
											return "sku不能为空!!";
										}
					                },success:function(response,newValue){
		                 				var param = {};
		                				var $this = $(this);
		                				var oldVal = $this.text();
		                				param.barcodeId = $this.parent().parent().parent().find(".barcodeId").val(); 
		                				param.skuId = $this.parent().parent().find(".skuId").val();
		                				param.sku = encodeURIComponent(encodeURIComponent(newValue));
		                				param.country = '${psiProduct.platform}';
		                				param.accountName='${psiProduct.brand}';
		                				$.get("${ctx}/psi/product/updateSku?"+$.param(param),function(data){
		                					if(!(data)){
		                						$this.text(oldVal);						
		                					}else{
		                						//将id放入隐藏域 上面总数+1
		                						if(!(param.skuId)){
		                							$this.parent().parent().find(".skuId").val(data);
		                							var numTd = $this.parent().parent().parent().parent().find("."+$this.parent().parent().parent().attr("class")+" .number");
		                							numTd.text("已绑定"+$this.parent().parent().parent().find(".skuId[value!='']").size()+"个sku");
		                							$this.parent().parent().parent().find(".newBtn").removeAttr("checked");
		                						}
		                						$.jBox.tip("sku保存成功！", 'info',{timeout:2000});
		                					}
		                				});
		                				return true;
		                }});
			
			var openBtn = $(btn).parent().find(".open");
			if(openBtn.text()=='展开'){
				openBtn.text('关闭');
				$("#"+id).show();
			}
		}
		
		function openOrClose(id,btn){
			if($(btn).text()=='展开'){
				$(btn).text('关闭');
			}else{
				$(btn).text('展开');
			}
			$("#"+id).toggle();
		}
		
		function updateState(btn,id){
			var param = {};
			var skuId = $(btn).parent().parent().find(".skuId").val();
			param.skuId = skuId;
			if(skuId){
				param.checked = btn.checked;
				$.get("${ctx}/psi/product/updateBarcode?"+$.param(param),function(data){
					if(data){
						$(btn).removeAttr("checked");
						$.jBox.tip(data, 'error',{timeout:2000});
					}else{
						if(btn.checked){
							$("#"+id).find(".oldBtn,.newBtn").each(function(){
								if(this!=btn){
									$(this).removeAttr("checked");
								}					
							});
						}
						$.jBox.tip("切换Fnsku成功！", 'info',{timeout:2000});
					}
					$("table :checkbox").removeAttr("disabled");
					$("table :checked").attr("disabled","disabled");
				}); 
			}
		}
		function deleteRow(btn){
			$.jBox.confirm("是否确认删除该sku?",'系统提示',function(v,h,f){
				if(v=='ok'){
					var param = {};
    				param.skuId =$(btn).parent().parent().find(".skuId").val();
    				param.sku  =encodeURIComponent(encodeURIComponent($(btn).parent().parent().find(".skuVal").val()));
    				if(param.skuId){
	    				$.get("${ctx}/psi/product/deleteSku?"+$.param(param),function(data){
	    					if(data){
	    						//将id放入隐藏域 上面总数+1
	    						if(param.skuId){
	    							var numTd =$(btn).parent().parent().parent().parent().find("."+$(btn).parent().parent().parent().attr("class")+" .number");
	    							numTd.text("已绑定"+($(btn).parent().parent().parent().find(".skuId[value!='']").size()-1)+"个sku");
	    						}
	    						$(btn).parent().parent().remove();
	    						$.jBox.tip("sku删除成功！！！", 'info',{timeout:2000});
	    					}
	    				});
    				}else{
						$(btn).parent().parent().remove();
    				}
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
		   <c:if test="${dic.value ne 'com.unitek'}">
			    <c:choose>
			       <c:when test="${fn:length(accountMap[dic.value])>1}">
			           <li class="dropdown ${psiProduct.platform eq dic.value ?'active':''}"  >
						    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">${dic.label}<c:if test="${dic.value eq psiProduct.platform}">${psiProduct.brand}</c:if><b class="caret"></b> </a>
						    <ul class="dropdown-menu" style="min-width:110px">
						         <c:forEach items="${accountMap[dic.value]}" var="account">
						             <li><a href="${ctx}/psi/product/skulist?platform=${dic.value}&brand=${account}">${account}</a></li>	
						         </c:forEach>
						    </ul>
		               </li>
			       </c:when>
			        <c:when test="${fn:length(accountMap[dic.value])==1}">
			           <c:forEach items="${accountMap[dic.value]}" var="account">
						    <li class="${psiProduct.platform eq dic.value ?'active':''}"><a href="${ctx}/psi/product/skulist?platform=${dic.value}&brand=${account}">${account}</a></li>	
						</c:forEach>	
			       </c:when>
			       <c:otherwise>
					    <li class="${psiProduct.platform eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			       </c:otherwise>
			    </c:choose>
		     </c:if>
		</c:forEach>	
		<li class="${psiProduct.platform eq 'ebay'?'active':''}"><a class="countryHref" href="#" key="ebay">德国Ebay</a></li>
		<li class="${psiProduct.platform eq 'ebay_com'?'active':''}"><a class="countryHref" href="#" key="ebay_com">美国Ebay</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiProduct" action="${ctx}/psi/product/skulist" method="post" class="breadcrumb form-search">
		<div style="vertical-align: middle;height: 40px;line-height: 40px">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input  name="platform" type="hidden" value="${psiProduct.platform}"/>
			<input  name="brand" type="hidden" value="${psiProduct.brand}"/>
			<select name="type" style="width: 140px">
				<option value="">产品类型</option>
				<c:forEach items="${fns:getDictList('product_type')}" var="dic">
					<option value="${dic.value}" ${psiProduct.type eq dic.value ?'selected':''}>${dic.label}</option>
				</c:forEach>
				<option value="other" ${psiProduct.type eq 'other' ?'selected':''}>其他</option>
			</select>
			<label>产品型号：</label><form:input path="model" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			<input  class="btn btn-primary" id="skuExport" type="button" value="<spring:message code="sys_but_export"/>"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" value="1" name="delFlag" id="flag" ${psiProduct.delFlag eq '1'?'checked':''} />未匹配产品
			&nbsp;&nbsp;&nbsp;&nbsp;
			<c:if test="${psiProduct.platform ne 'ebay'&&psiProduct.platform ne 'ebay_com'}">
				<shiro:hasPermission name="amazoninfo:feedSubmission:${psiProduct.platform}">
					<input id="btnSkuMate" class="btn btn-success" type="button" value="SKU反向匹配"/>
				</shiro:hasPermission>
			</c:if>
			<c:if test="${psiProduct.platform eq 'ebay'||psiProduct.platform eq 'ebay_com'}">
				<input id="btnSkuMate" class="btn btn-success" type="button" value="SKU反向匹配"/>
			</c:if>
		</div>
	</form:form>
	<div class="alert alert-info"><strong >每天北京时间5点30,8点30,11点30,14点30,17点30,19点30,23点30自动同步亚马逊后台产品Sku!</strong></div>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			  <tr>
				   <th style="width:50px">名称</th>
				   <th style="width:150px;">平台   ${psiProduct.brand }</th>
				   <th style="width:60px;"></th>
				   <th style="width:60px;"></th>
			   </tr>
		</thead>
		<c:choose>
			<c:when test="${'ebay' eq psiProduct.platform}">
				<c:set value="de" var="country" />
				<c:set var='accountKey' value='${country}${accountMap[country][0]}'/>
			</c:when>
			<c:when test="${'ebay_com' eq psiProduct.platform}">
				<c:set value="com" var="country" />
				<c:set var='accountKey' value='${country}${accountMap[country][0]}'/>
			</c:when>			
			<c:otherwise>
				<c:set value="${psiProduct.platform}" var="country" />
				<c:set var='accountKey' value='${psiProduct.platform}${psiProduct.brand}'/>
			</c:otherwise>
		</c:choose>
		<c:forEach items="${page.list}" var="product" varStatus="i">
			<c:if test="${fn:length(product.barcodeMap2ByAccount2[accountKey])>1}">
				<tr id="num${i.index}" >
					<td><b style="font-size: 14px">${product.name}</b></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</c:if>
			
			<c:forEach items="${product.barcodeMap2ByAccount2[accountKey]}" var="barcode" varStatus="j">
				<tr class="trGroup${i.index}row${j.index}" <c:if test="${fn:length(product.barcodeMap2ByAccount2[accountKey])>1}"> pid="num${i.index}" </c:if> >
					 <c:if test="${fn:length(product.barcodeMap2ByAccount2[accountKey])>1}"><td style="text-align: center;vertical-align: middle;"> <b style="font-size: 18px;">${barcode.key}</b></td></c:if>
					 <c:if test="${fn:length(product.barcodeMap2ByAccount2[accountKey])<=1}">
					 	<td><b style="font-size: 18px;"><b style="font-size: 14px">&nbsp;&nbsp;&nbsp;&nbsp;${product.name}</b></b></td>
					 </c:if>
					 <td class="number">已绑定
					 	<c:choose>
					 			 <c:when test="${psiProduct.platform eq 'de'}">${fn:length(barcode.value.deSkus)}<c:set var="skus" value="${barcode.value.deSkus}" /></c:when>
					 			 <c:when test="${psiProduct.platform eq 'ebay'}">${fn:length(barcode.value.ebaySkus)}<c:set var="skus" value="${barcode.value.ebaySkus}" /></c:when>
					 			 <c:when test="${psiProduct.platform eq 'com'}">${fn:length(barcode.value.comSkus)}<c:set var="skus" value="${barcode.value.comSkus}" /></c:when>
					 			 <c:when test="${psiProduct.platform eq 'ebay_com'}">${fn:length(barcode.value.comEbaySkus)}<c:set var="skus" value="${barcode.value.comEbaySkus}" /></c:when>
					             <c:otherwise>${fn:length(barcode.value.accountSkus[psiProduct.brand])}<c:set var="skus" value="${barcode.value.accountSkus[psiProduct.brand]}" /></c:otherwise>
					     </c:choose>个sku</td>
					 <td></td>
					 <td><span class="btn btn-info open" onclick="openOrClose('row${i.index}${j.index}',this)" >展开</span>&nbsp;&nbsp;
					 	<c:if test="${psiProduct.platform ne 'ebay'&&psiProduct.platform ne 'ebay_com'}">
							<shiro:hasPermission name="amazoninfo:feedSubmission:${psiProduct.platform}">
						 		<button class="btn btn-warning" onclick="addRow('row${i.index}${j.index}',this)">新增</button>
						 	</shiro:hasPermission>
					 	</c:if>
					 	<c:if test="${psiProduct.platform eq 'com1'}">
					 		<button class="btn btn-warning" onclick="addRow('row${i.index}${j.index}',this)">新增</button>
					 	</c:if>
					 	<c:if test="${psiProduct.platform eq 'ebay'}">
					 		<button class="btn btn-warning" onclick="addRow('row${i.index}${j.index}',this)">新增</button>
					 	</c:if>
					 	<c:if test="${psiProduct.platform eq 'ebay_com'}">
					 		<button class="btn btn-warning" onclick="addRow('row${i.index}${j.index}',this)">新增</button>
					 	</c:if>
					 </td>
				</tr>
				
				<tbody class="trGroup${i.index}row${j.index}" id="row${i.index}${j.index}" style="display:none">
					<tr style="background-color: #D2E9FF"><td>
					<input type="hidden" value="${barcode.value.id}" class="barcodeId" />
					</td><td>Sku</td><td>是否使用Fnsku</td><td>操作</td></tr>
					<c:forEach items="${skus}" var="sku">
						<tr style="background-color: #D2E9FF">
							 <td>
							 	<input type="hidden" value="${sku.id}" class="skuId" />
							 	<input type="hidden" value="${sku.sku}" class="skuVal" />
							 	${sku.accountName }
							 </td> 
							 <td>
							 	<c:if test="${psiProduct.platform ne 'ebay'&&psiProduct.platform ne 'ebay_com'}">
									<shiro:hasPermission name="amazoninfo:feedSubmission:${psiProduct.platform}">
							 			<a href="#"  data-type="select2" data-pk="1"  data-title="Select sku" class="editable editable-click oldrow" data-original-title="" title="">${sku.sku}</a>
							 		</shiro:hasPermission>
							 		<shiro:lacksPermission name="amazoninfo:feedSubmission:${psiProduct.platform}">
							 			<a href="#"  data-type="select2" data-pk="1"  data-title="Select sku" class="editable editable-click" data-original-title="" title="">${sku.sku}</a>
							 		</shiro:lacksPermission>
							 	</c:if>
							 	<c:if test="${psiProduct.platform eq 'ebay'}">
							 		<a href="#"  data-type="select2" data-pk="1"  data-title="Select sku" class="editable editable-click oldrow" data-original-title="" title="">${sku.sku}</a>
							 	</c:if>	
							 	<c:if test="${psiProduct.platform eq 'com1'}">
							 		<a href="#"  data-type="select2" data-pk="1"  data-title="Select sku" class="editable editable-click oldrow" data-original-title="" title="">${sku.sku}</a>
							 	</c:if>	
							 	
							 	<c:if test="${psiProduct.platform eq 'ebay_com'}">
							 		<a href="#"  data-type="text" data-pk="1"  data-title="Select sku" class="editable editable-click oldrow2" data-original-title="" title="">${sku.sku}</a>
							 	</c:if>	
							 <c:if test="${not empty sku.asin}"><a href="${sku.link}" target="_blank">open</a></c:if>
							 &nbsp;&nbsp;&nbsp;&nbsp;${fnskuMap[sku.sku]}</td>
							 <td>
							 	<c:if test="${psiProduct.platform ne 'ebay' &&psiProduct.platform ne 'ebay_com'}">
							 		<shiro:hasPermission name="amazoninfo:feedSubmission:${psiProduct.platform}">
							 			<input ${sku.useBarcode eq '1'?'checked':''} class="oldBtn" type="checkbox"  onchange="updateState(this,'row${i.index}${j.index}')"/>
							 		</shiro:hasPermission>
							 		<shiro:lacksPermission name="amazoninfo:feedSubmission:${psiProduct.platform}">
							 			${sku.useBarcode eq '1'?'使用条码':'非条码'}
							 		</shiro:lacksPermission>
							 	</c:if>
							 </td>
							 <td>
							 	<c:if test="${psiProduct.platform ne 'ebay'&&psiProduct.platform ne 'ebay_com'}">
									<shiro:hasPermission name="amazoninfo:feedSubmission:${psiProduct.platform}">
							 			<span class="btn btn-danger" onclick='deleteRow(this)'>删除</span>
							 		</shiro:hasPermission>
							 	</c:if>
							 	<c:if test="${psiProduct.platform eq 'ebay'||psiProduct.platform eq 'ebay_com'}">
							 		<span class="btn btn-danger" onclick='deleteRow(this)'>删除</span>
							 	</c:if>	
							 </td>
						</tr> 
					</c:forEach>
				</tbody>
			</c:forEach>
		</c:forEach>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
