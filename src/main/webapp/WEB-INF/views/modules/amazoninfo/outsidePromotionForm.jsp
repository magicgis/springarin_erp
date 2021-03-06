<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/WEB-INF/views/include/dialog.jsp" %>
<html>
<head>
<meta name="decorator" content="default"/>
<title>outsidePromotionView</title>

<script type="text/javascript">
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();

	$(document).ready(function(){
		
		$("#country").change(function(){
			var params = {};
			if($(this).val()){
				params.country = $(this).val();
			}
			window.location.href = "${ctx}/amazoninfo/outsidePromotion/add?"+$.param(params);
		});
		
		$(".Wdate").live("click", function (){
			 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
		});
		
		
		$("#promotionCode").change(function(){
			$("input[name='trackId']").val("");
	    	$("input[name='promotionCode']").val("");
	    	$("input[name='startDate']").val("");
	    	$("input[name='endDate']").val("");
	    	$("input[name='productName']").val("");
	    	$("input[name='buyerGets']").val("");  
	    	$("input[name='promoWarning.id']").val("");  
			var id=$(this).val();
			if(id){
				$.ajax({
					    type: 'post',
					    async:false,
					    url: '${ctx}/amazoninfo/outsidePromotion/getProInfo' ,
					    data: {
					    	"id":id
					    },
					    dataType: 'json',
					    success:function(data){ 
					    	$("input[name='trackId']").val(data.promotionId);
					    	$("input[name='promotionCode']").val(data.promotionCode);
					    	$("input[name='startDate']").val(data.startDate);
					    	$("input[name='endDate']").val(data.endDate);
					    	$("input[name='productName']").val(data.productName);
					    	$("input[name='buyerGets']").val(data.buyerGets);  
					    	$("input[name='asin']").val(data.asin);  
					    	$("input[name='promoWarning.id']").val(data.id);  
				        }
				});
			}
			
		});
		
		
		$("#add-row").on("click",function(e){
			e.preventDefault();
			var tbody=$("#contentTable tbody");
			var tr=$("<tr></tr>");
			var options = "<option value=''>请选择站点</option>";
			//<c:forEach items="${fns:getDictList('website')}" var="dic">
				options=options+'<option value="${dic.value}" >${dic.label}</option>';
			//</c:forEach>
			tr.append(" <td><select name='website' style='width:90%' class='required'>"+options+"</select></td>");
            tr.append(" <td><input type='text' style='width: 90%'  name='url' class='required' /></td>");
            tr.append(" <td><input type='text' style='width: 90%'  name='promoDate' class='Wdate required' /></td>");
            tr.append(" <td><a href='#' class='remove-row'><span class='icon-minus'></span>删除</a></td>");
			tbody.append(tr);
			tr.find("select[name='website']").select2();
		});
		
		
		$(".remove-row").live("click",function(){
			 if($('#contentTable tbody tr').size()>1){
				var tr = $(this).parent().parent();
				tr.remove();
			}
		});
	
		$("#inputForm").validate({
			submitHandler: function(form){
				loading('Please wait a moment!');
				$("#contentTable tbody tr").each(function(i,j){
					$(j).find("select").each(function(){
						if($(this).attr("name")){
							$(this).attr("name","promoWebsites"+"["+i+"]."+$(this).attr("name"));
						}
					});
					$(j).find("input[type!='']").each(function(){
						if($(this).attr("name")){
							$(this).attr("name","promoWebsites"+"["+i+"]."+$(this).attr("name"));
						}
					});
				});
				
				form.submit();
			},
			errorContainer: "#messageBox",
			errorPlacement: function(error, element) {
				$("#messageBox").text("Entered incorrectly, please correct");
				if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				}
			}
		});
		
	});

</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/outsidePromotion">站外促销分析列表</a></li>
		<li class="active"><a href="#">新增站外促销分析</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="outsidePromotion"	action="${ctx}/amazoninfo/outsidePromotion/save" class="form-horizontal">
		<input type="hidden" name="id" value="${outsidePromotion.id}" />
		<input type="hidden" name="promotionCode" value="${outsidePromotion.promotionCode}" />
		<input type="hidden" name="asin" value="${outsidePromotion.asin}" />
		<input type="hidden" name="promoWarning.id" value="${outsidePromotion.promoWarning.id}" />
		<div class="control-group">
			<label class="control-label">国家</label>
			<div class="controls">
			<select name="country" id="country" class="required">
				<option value=""></option>
				<c:forEach items="${fns:getDictList('platform')}" var="dic" >
					<c:if test="${dic.value ne 'com.unitek'}">
						 <option value="${dic.value}" ${outsidePromotion.country eq dic.value ?'selected':''}  >${dic.label}</option>
					</c:if>      
				</c:forEach>
			</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">折扣码[trackId]</label>
			<div class="controls">
			<select  style="width:50%" id="promotionCode" class="required">
				<option value="">请选择</option>
				<c:forEach items="${promotionMap}" var="proEntry">
					<option  value="${proEntry.key}" ${proEntry.key eq outsidePromotion.promotionCode?'selected':''}>${proEntry.value}</option>
				</c:forEach>
			</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">平台经费</label>
			<div class="controls">
				<input type="text" name="platformFunds" value="${outsidePromotion.platformFunds}"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">样品提供</label>
			<div class="controls">
				<input type="text" name="sampleProvided" value="${outsidePromotion.sampleProvided}"/>
			</div>
		</div>
		
		<div align="right" style="font-size: 14px;margin-top: 5px;margin-bottom: 5px"><a href="#" id="add-row"><span class="icon-plus"></span>增加站点</a></div>
		<div class="control-group">
			<label class="control-label" style="width:100px">推广站点信息:</label>
			<div class="controls" style="margin-left:120px">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="width: 30%">站点</th>
							<th style="width: 40%">链接地址</th>
							<th style="width: 20%">推广时间</th>
							<th style="width: 10%">操作</th>
						</tr>
					</thead>
					<tbody>
					<tr>
						<td>
							<select name="website" class="required" style="width:90%">
								<option value="">请选择站点</option>
								<c:forEach items="${fns:getDictList('website')}" var="dic">
										 <option value="${dic.value}" >${dic.label}</option>
								</c:forEach>
							</select>
						</td>
						<td><input type="text" style="width: 90%"  name="url"  class="required" /></td>
						<td><input type="text"  style="width:90%"  name="promoDate" class="Wdate required"  /></td>
						<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span>删除</a></td>
					</tr>
					</tbody>
				</table>
			</div>
		</div>	
		
	
		<div class="control-group">
			<label class="control-label">TrackId</label>
			<div class="controls">
				<input type="text"  style="width:50%" name="trackId" value="${outsidePromotion.trackId}" readonly/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">产品型号</label>
			<div class="controls">
				<input type="text"  style="width:50%" name="productName" value="${outsidePromotion.productName}" class="required" readonly/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">开始日期</label>
			<div class="controls">
				<input type="text" name="startDate" value="${outsidePromotion.startDate}" readonly/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">结束日期</label>
			<div class="controls">
				<input type="text" name="endDate" value="${outsidePromotion.endDate}" readonly/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">促销形式</label>
			<div class="controls">
				<input type="text" name="buyerGets" value="${outsidePromotion.buyerGets}" readonly="readonly"/>
			</div>
		</div>
		
		
		<div class="form-actions">
			<input  class="btn btn-primary" type="submit" value="保存" />&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>	
		</div>
	</form:form>
</body>
</html>