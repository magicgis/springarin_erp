<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊产品图片修改</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
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
			$("#country").change(function(){
				var params = {};
				if($(this).val()){
					var country = $(this).val().join(",");
					var countrys=country.split(",");
					var tmp="";
					for(j = 0; j < countrys.length; j++) {
						tmp+=countrys[j].split("_")[0]+",";
					} 
					params.country = tmp.substring(0,tmp.length-1);
				}
				$.get("${ctx}/amazoninfo/imageFeed/getSku?"+$.param(params),function(data){
					if(data){
						var sel = $("select[name='sku']").select2("data",[]).empty();
						eval("var map = "+data);
						sel.append("<option value=\"\"></option>");
						$(map).each(function(){
							sel.append("<option value='"+this.key+"' >"+this.value+"</option>");
						});
						if(map.length>0){
							sel.append("<option value=\"sku\">手动填写产品名</option>");
						}
					}
				});
				$("#reviewImg").attr("src","");
			});
			
			$("#inputForm").on("change","select[name='sku']",function(){
				if($(this).val()=='sku'){
					$(this).parent().find("input[type='text']").show();
				}else{
					$(this).parent().find("input[type='text']").hide();
					$("#reviewImg").attr("src","");
					var params = {};
					params.sku = $("select[name='sku']").val();
					$.get("${ctx}/amazoninfo/imageFeed/getProductImage?"+$.param(params),function(data){
						$("#reviewImg").attr("src",data);
					});
				}
			});
			
			$("input[name='sku']").focusout(function(){
				$("#reviewImg").attr("src","");
				var params = {};
				params.sku = $(this).val();
				$.get("${ctx}/amazoninfo/imageFeed/getProductImage?"+$.param(params),function(data){
					$("#reviewImg").attr("src",data);
				});
			});
			$("#review").click(function(){
				var params = {};
				var arr=$("#country").val().split("_");
				params.country = (arr[0]+"_"+arr[1]).join(",");
				if($("select[name='sku']").val()!='sku'&& $("select[name='sku']").val()!=''){
					params.sku = $("select[name='sku']").val();
				}else{
					params.sku = $("input[name='sku']").val();
				}
				if(params.sku){
					$.get("${ctx}/amazoninfo/imageFeed/getLink?"+$.param(params),function(link){
						if(link){
							windowOpen(link,'review',800,600);
						}else{
							$.jBox.tip("查找不到sku对应的asin！", 'error',{timeout:2000});
						}
					});
				} else {
					$.jBox.tip("请选择产品！", 'error',{timeout:2000});
				}
			});
			$("#inputForm").validate({
				submitHandler: function(form){
					var flag = true;
					$(".imageInput").each(function(){
						var urls =$(this).val();
						if(urls != null && urls.length > 0){
							var arr = urls.split("|");
							var length = 0;
							for(var i=0;i<arr.length;i++){
								if(arr[i] != null && arr[i].length > 0){
									length += 1;
								}
							}
							if(length > 1){
								flag = false;
								return false;
							}
						}
					});
					if(flag){
						$(".imageInput").each(function(){
							var urls =$(this).val();
							if(urls != null && urls.length > 0){
								var arr = urls.split("|");
								for(var i=0;i<arr.length;i++){
									if(arr[i] != null && arr[i].length > 0){
										$(this).val(arr[i]);
									}
								}
							}
						});
						top.$.jBox.confirm('确定要修改产品图片吗!','系统提示',function(v,h,f){
							if(v=='ok'){
								if($("select[name='sku']").val()=='sku'){
									$("select[name='sku']").removeAttr("name");
								}else{
									$("input[name='sku']").removeAttr("name");
								}
								loading('正在提交，请稍等...');
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
							}
						},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
					} else {
						$.jBox.tip("同一位置存在多张图片", 'error',{timeout:2000});
					}
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/imageFeed/">产品图片管理列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/imageFeed/form">修改产品图片</a></li>
	</ul><br/>
	<form id="inputForm"  action="${ctx}/amazoninfo/imageFeed/save" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select id="country" name="country" style="width: 400px" class="required" multiple class="multiSelect" >
				    <c:forEach items="${accountList}" var="account">
				        <option value='${accountList[account.key]}_${account.key}' >${account.key}</option>
				    </c:forEach>
					<%--去掉权限限制20161116
					<shiro:hasPermission name="amazoninfo:feedSubmission:all"> --%>
						<%-- <c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek'&&dic.value ne 'com2'}">
								<option value="${dic.value}" >${dic.label}</option>
							</c:if>
						</c:forEach>
						<shiro:hasPermission name="amazoninfo:feedSubmission:com2">
							<option value="com2" >美国NEW</option>
						</shiro:hasPermission> --%>
					<%--</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
						<shiro:hasPermission name="amazoninfo:feedSubmission:de">
							<option value="de" >德国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:com">
							<option value="com" >美国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:fr">
							<option value="fr" >法国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:jp">
							<option value="jp" >日本</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:es">
							<option value="es" >西班牙</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:it">
							<option value="it" >意大利</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:uk">
							<option value="uk" >英国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:ca">
							<option value="ca" >加拿大</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:mx">
							<option value="mx" >墨西哥</option>
						</shiro:hasPermission>
					</shiro:lacksPermission> --%>
				</select>
				<script type="text/javascript">
					$("option[value='${imageFeed.country}']").attr("selected","selected");				
				</script>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">选择产品:</label>
			<div class="controls">
				&nbsp;&nbsp;<select name="sku" style="width: 300px" class="required">
					<c:if test="${not empty imageFeed.country}">
						<option value=""></option>	
						<c:forEach items="${sku}" var="item">
							<option value="${item.key}">${item.value}</option>									
						</c:forEach>
						<c:if test="${fn:length(sku)>0}">
							<option value="sku">手动填写产品名</option>
						</c:if>
					</c:if>
				</select>
				<input name="sku" type="text" style="margin-top:5px;display: none;"/>
				<span class="btn btn-info" id="review">查看产品</span>
				<span class="span1">
				    <a href="#" class="thumbnail">
				      	<img id="reviewImg" src="" alt="产品图片">
				    </a>
		  		</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Main:</label>
			<div class="controls">
                <input type="hidden" id="image" name="images[0].location" class="imageInput" />
                 <input type="hidden" name="images[0].type"  value="Main" />
				<tags:ckfinder input="image" type="productImages" uploadPath="/" selectMultiple="true"/>
				<input type="checkbox"  name="images[0].isDelete" value="1" />删除图片
			</div>
		</div>
		<table>
			<tr>
				<td>
					<div class="control-group">
						<label class="control-label">PT1:</label>
						<div class="controls">
			                <input type="hidden" id="image1" name="images[1].location"  class="imageInput" />
			                <input type="hidden" name="images[1].type"  value="PT1" />
							<tags:ckfinder input="image1" type="productImages" uploadPath="/" selectMultiple="true"/>
							<input type="checkbox"  name="images[1].isDelete" value="1" />删除图片
						</div>
					</div>
				</td>			
				<td>
					<div class="control-group">
						<label class="control-label">PT2:</label>
						<div class="controls">
			                <input type="hidden" id="image2" name="images[2].location"  class="imageInput" />
			                <input type="hidden" name="images[2].type"  value="PT2" />
							<tags:ckfinder input="image2" type="productImages" uploadPath="/" selectMultiple="true"/>
							<input type="checkbox"  name="images[2].isDelete" value="1" />删除图片
						</div>
					</div>
				</td>	
			</tr>
			<tr>
				<td>
					<div class="control-group">
						<label class="control-label">PT3:</label>
						<div class="controls">
			                <input type="hidden" id="image3" name="images[3].location"  class="imageInput" />
			                <input type="hidden" name="images[3].type"  value="PT3" />
							<tags:ckfinder input="image3" type="productImages" uploadPath="/" selectMultiple="true"/>
							<input type="checkbox"  name="images[3].isDelete" value="1" />删除图片
						</div>
					</div>
				</td>			
				<td>
					<div class="control-group">
						<label class="control-label">PT4:</label>
						<div class="controls">
			                <input type="hidden" id="image4" name="images[4].location"  class="imageInput" />
			                <input type="hidden" name="images[4].type"  value="PT4" />
							<tags:ckfinder input="image4" type="productImages" uploadPath="/" selectMultiple="true"/>
							<input type="checkbox"  name="images[4].isDelete" value="1" />删除图片
						</div>
					</div>
				</td>	
			</tr>
			<tr>
				<td>
					<div class="control-group">
						<label class="control-label">PT5:</label>
						<div class="controls">
			                <input type="hidden" id="image5" name="images[5].location"  class="imageInput" />
			                <input type="hidden" name="images[5].type"  value="PT5" />
							<tags:ckfinder input="image5" type="productImages" uploadPath="/" selectMultiple="true"/>
							<input type="checkbox"  name="images[5].isDelete" value="1" />删除图片
						</div>
					</div>
				</td>			
				<td>
					<div class="control-group">
						<label class="control-label">PT6:</label>
						<div class="controls">
			                <input type="hidden" id="image6" name="images[6].location"  class="imageInput" />
			                <input type="hidden" name="images[6].type"  value="PT6" />
							<tags:ckfinder input="image6" type="productImages" uploadPath="/" selectMultiple="true"/>
							<input type="checkbox"  name="images[6].isDelete" value="1" />删除图片
						</div>
					</div>
				</td>	
			</tr>
			<tr>
				<td>
					<div class="control-group">
						<label class="control-label">PT7:</label>
						<div class="controls">
			               <input type="hidden" id="image7" name="images[7].location"  class="imageInput" />
			                <input type="hidden" name="images[7].type"  value="PT7" />
							<tags:ckfinder input="image7" type="productImages" uploadPath="/" selectMultiple="true"/>
							<input type="checkbox"  name="images[7].isDelete" value="1" />删除图片
						</div>
					</div>
				</td>			
				<td>
					<div class="control-group">
						<label class="control-label">PT8:</label>
						<div class="controls">
			                <input type="hidden" id="image8" name="images[8].location"  class="imageInput" />
			                <input type="hidden" name="images[8].type"  value="PT8" />
							<tags:ckfinder input="image8" type="productImages" uploadPath="/" selectMultiple="true"/>
							<input type="checkbox"  name="images[8].isDelete" value="1" />删除图片
						</div>
					</div>
				</td>	
			</tr>
		</table>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提  交"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>