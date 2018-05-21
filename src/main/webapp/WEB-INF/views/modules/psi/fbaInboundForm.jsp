<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊FBA贴编辑</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" type="text/css" />
	<script type="text/javascript" src="${ctxStatic}/x-editable/js/bootstrap-editable.js"></script>
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
		$(document).ready(function() {
			var addIndex =${fn:length(fbaInbound.items)};
			$('#add-row').on('click', function(e){
			   e.preventDefault();
			   var tableBody = $('.table > tbody'), 
			   lastRowClone = $('tr:last-child', tableBody).clone();
			   $('input[type=text][class!=Wdate]', lastRowClone).val('');  
			   lastRowClone.removeAttr("index");
			   lastRowClone.attr("addindex",addIndex);
			   addIndex++;
			   lastRowClone.find(".canUsed").text("");
			   
			   lastRowClone.find("input[name=pageDelFlag]").val("0");
			   lastRowClone.find("input[name=oldPackQuantity]").val("0");
			    var html = "<option value=''></option>";
				// <c:forEach items="${skus}" var="item"> 
				 		html = html+"<option value='${item.key}'>${item.value}</option>";
				 //</c:forEach>
			   lastRowClone.find(".sku").html("<select class=\"required\" name=\"sku\" style=\"width: 90%\">"+html+"</select>");
			   lastRowClone.find("select").select2();
			   tableBody.append(lastRowClone);
			});
			
			$('#contentTable').on('click', '.remove-row', function(e){
			  e.preventDefault();
			  if($('#contentTable tr').size()>2){
				  var row = $(this).parent().parent();
				  row.remove();
				  if(row.attr("addindex")){
					  addIndex--;
				  }
			  }
			});
			
			$("#country").change(function(){
				var params = {};
				params.country = $(this).val();
				window.location.href = "${ctx}/amazoninfo/fbaInbound/form?"+$.param(params);
			});
			
			$("#inputForm").on("change","select[name='sku']",function(data){
				var td = $(this).parent().parent();
				if(!(data.val)){
					td.find(".canUsed").text("");
					td.find(".pack").text("");
				}else{
					//改装箱数和可用数		
					var param = {};
					param.sku = data.val;
					param.stockCode = '${fbaInbound.shipFromAddress}';
					param.country = '${fbaInbound.country}';
					param.inboundId=${fbaInbound.id};
					$.get("${ctx}/psi/fbaInbound/getShipddQuantityAvailableBySku?"+$.param(param),function(rdata){
						eval("rdata = "+rdata);
						td.find(".canUsed").text(rdata.canUsed);
						td.find("input[name='packQuantity']").val(rdata.pack);
					});
					//不能变动，要记住改变前产品的sku  td.find("input[name=pageDelFlag]").val(data.val);
				}
			});
			var valid = false;
			$("#inputForm").validate({
				submitHandler: function(form){
						var flag = false;
						var skus = [];
						var totalBox = 0;
						$(form).find("tbody tr").each(function(i,j){
							var number = $(this).find("input[name='quantityShipped']").val();
							if($(this).find(".canUsed").text() && parseInt(number) >parseInt($(this).find(".canUsed").text())){
								flag = true;
							};
							skus[skus.length]= $(this).find("select[name='sku']").val();
							var box = parseInt($(this).find("input[name='packQuantity']").val());
							var num = parseInt(parseInt(number));
							if(box > 0){
								totalBox += (num/box);
							}
						});
						var country = '${fbaInbound.country}';
						if(totalBox > 200 && ('de'==country || 'fr'==country || 'uk'==country || 'it'==country || 'es'==country)){
							$.jBox.tip("产品总箱数("+totalBox+")已大于200,请调整数量!!", 'error',{timeout:5000});	
							return false;
						}
						if(unique(skus).length!=skus.length){
							$.jBox.tip("产品有重复，请合并产品!!", 'error',{timeout:2000});	
							return false;
						}
						if(valid&&flag&&!'CN'=='${fbaInbound.shipFromAddress}'){
							$.jBox.tip("发货量不能大于可用库存值!!", 'error',{timeout:2000});	
							return false;
						}
						loading('正在提交，请稍等...');
						$("#contentTable tbody tr").each(function(i,j){
							var index = $(j).attr("index");
							if(!index){
								index = $(j).attr("addindex");
							}
							$(j).find("select").attr("name","items"+"["+index+"]."+$(j).find("select").attr("name"));
							$(j).find("input[name='quantityShipped']").attr("name","items"+"["+index+"]."+$(j).find("input[name='quantityShipped']").attr("name"));
							$(j).find("input[name='packQuantity']").attr("name","items"+"["+index+"]."+$(j).find("input[name='packQuantity']").attr("name"));
							$(j).find("input[name='oldPackQuantity']").attr("name","items"+"["+index+"]."+$(j).find("input[name='oldPackQuantity']").attr("name"));
							$(j).find("input[name='pageDelFlag']").attr("name","items"+"["+index+"]."+$(j).find("input[name='pageDelFlag']").attr("name"));	
						});
						form.submit();
						$("#btnSubmit").attr("disabled","disabled");
						top.$('.jbox-body .jbox-icon').css('top','55px');
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		
		function unique(arr) {
		    var result = [], hash = {};
		    for (var i = 0, elem; (elem = arr[i]) != null; i++) {
		        if (!hash[elem]) {
		            result.push(elem);
		            hash[elem] = true;
		        }
		    }
		    return result;
		}
	</script>
</head>
<body>
	<form id="inputForm"  action="${ctx}/psi/fbaInbound/save" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<div align="center">
			<blockquote style="width: 80%" >
				<p style="font-size: 16px;text-align: left;">FBA贴发货产品列表</p>
			</blockquote>
			<input value="${fbaInbound.id}" name="id" type="hidden"/>
			<input value="${fbaInbound.shipFromAddress}" name="shipFromAddress" type="hidden"/>
			<input value="${fbaInbound.pdfFile}" name="pdfFile" type="hidden"/>
			<input value="${fbaInbound.destinationFulfillmentCenterId}" name="destinationFulfillmentCenterId" type="hidden"/>
			<input value="${fbaInbound.supplier}" name="supplier" type="hidden"/>
			<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px;width: 80%"><a href="#" id="add-row"><span class="icon-plus"></span>新增产品</a></div>
			<table id="contentTable" style="width: 80%" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th style="width: 40%">产品名(Sku)</th>
						<th style="width: 10%">装箱数</th>
						<th style="width: 10%">可用数</th>
						<th style="width: 10%">数量</th>
						<th style="width: 30px">操作</th>
					</tr>
				</thead>
				<tbody>
					<!-- 不能排序，需要记住数据库items的位置 -->
					<c:forEach var="itemEntry" items="${fbaInbound.items}" varStatus="i">
						<tr index= "${i.index}">
							<td class="sku">
								<select style="width:90%" name="sku" class="required">
									<option value=""></option>
									<c:forEach items="${skus}" var="item">
										<option ${itemEntry.sku eq item.key ?'selected':''} value="${item.key}">${item.value}</option>									
									</c:forEach>
								</select>
							</td>
							<c:set var="pack" value="${empty itemEntry.packQuantity?(packs[itemEntry.sku]==null?0:packs[itemEntry.sku]):itemEntry.packQuantity}" />
							<td class="pack"><input type="text" style="width: 90%" name="packQuantity" value="${pack}" class="packQuantity required"/></td>
							<td class="canUsed">${inStock[itemEntry.sku]==null?0:inStock[itemEntry.sku]}</td>
							<td>
								<input type="text" style="width: 90%" name="quantityShipped" value="${itemEntry.quantityShipped}" class="quantityShipped required"/>
								<input type="hidden" name="pageDelFlag" value="${itemEntry.sku}" />
								<input type="hidden" name="oldPackQuantity" value="${pack}" />
							</td>
							<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div class="form-actions"  style="width: 78%;text-align:center" >
				<input class="btn btn-primary"  type="submit" value="保 存"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</div>
	</form>
</body>
</html>