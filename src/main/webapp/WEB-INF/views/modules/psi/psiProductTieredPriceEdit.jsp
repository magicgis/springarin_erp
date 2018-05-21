<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品阶梯价格</title>
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
			
			$("select#productSelect").change(function(){
				var params = {};
				params.productId=$("input[name='productId']").val();
				params.color=$("input[name='color']").val();
				params.productIdColor=$(this).val();
				window.location.href = "${ctx}/psi/productTieredPrice/setPrice?"+$.param(params);
			});
			
			$(".isCheck").on("click",function(){
				if(this.checked){
					$(".isCheck").val("1");
				}else{
					$(".isCheck").val("0");
				}
			});
			
			
			
			$("input[leval]").on("blur",function(){
				var level=$(this).attr("leval");
				var price =$(this).val();
				if(price!=""){
					if(level=="1000usd"){
						if($("input[leval='500usd']").val()==""){
							$("input[leval='500usd']").val(price);
						}
					}else if(level=="1000cny"){
						if($("input[leval='500cny']").val()==""){
							$("input[leval='500cny']").val(price);
						}
					}else if(level=="2000usd"){
						if($("input[leval='500usd']").val()==""){
							$("input[leval='500usd']").val(price);
						}
						if($("input[leval='1000usd']").val()==""){
							$("input[leval='1000usd']").val(price);
						}
					}else if(level=="2000cny"){
						if($("input[leval='500cny']").val()==""){
							$("input[leval='500cny']").val(price);
						}
						if($("input[leval='1000cny']").val()==""){
							$("input[leval='1000cny']").val(price);
						}
					}else if(level=="3000usd"){
						if($("input[leval='500usd']").val()==""){
							$("input[leval='500usd']").val(price);
						}
						if($("input[leval='1000usd']").val()==""){
							$("input[leval='1000usd']").val(price);
						}
						if($("input[leval='2000usd']").val()==""){
							$("input[leval='2000usd']").val(price);
						}
					}else if(level=="3000cny"){
						if($("input[leval='500cny']").val()==""){
							$("input[leval='500cny']").val(price);
						}
						if($("input[leval='1000cny']").val()==""){
							$("input[leval='1000cny']").val(price);
						}
						if($("input[leval='2000cny']").val()==""){
							$("input[leval='2000cny']").val(price);
						}
						
					}else if(level=="5000usd"){
						if($("input[leval='500usd']").val()==""){
							$("input[leval='500usd']").val(price);
						}
						if($("input[leval='1000usd']").val()==""){
							$("input[leval='1000usd']").val(price);
						}
						if($("input[leval='2000usd']").val()==""){
							$("input[leval='2000usd']").val(price);
						}
						if($("input[leval='3000usd']").val()==""){
							$("input[leval='3000usd']").val(price);
						}
					}else if(level=="5000cny"){
						if($("input[leval='500cny']").val()==""){
							$("input[leval='500cny']").val(price);
						}
						if($("input[leval='1000cny']").val()==""){
							$("input[leval='1000cny']").val(price);
						}
						if($("input[leval='2000cny']").val()==""){
							$("input[leval='2000cny']").val(price);
						}
						if($("input[leval='3000cny']").val()==""){
							$("input[leval='3000cny']").val(price);
						}
					}else if(level=="10000usd"){
						if($("input[leval='500usd']").val()==""){
							$("input[leval='500usd']").val(price);
						}
						if($("input[leval='1000usd']").val()==""){
							$("input[leval='1000usd']").val(price);
						}
						if($("input[leval='2000usd']").val()==""){
							$("input[leval='2000usd']").val(price);
						}
						if($("input[leval='3000usd']").val()==""){
							$("input[leval='3000usd']").val(price);
						}
						if($("input[leval='5000usd']").val()==""){
							$("input[leval='5000usd']").val(price);
						}
					}else if(level=="10000cny"){
						if($("input[leval='500cny']").val()==""){
							$("input[leval='500cny']").val(price);
						}
						if($("input[leval='1000cny']").val()==""){
							$("input[leval='1000cny']").val(price);
						}
						if($("input[leval='2000cny']").val()==""){
							$("input[leval='2000cny']").val(price);
						}
						if($("input[leval='3000cny']").val()==""){
							$("input[leval='3000cny']").val(price);
						}
						if($("input[leval='5000cny']").val()==""){
							$("input[leval='5000cny']").val(price);
						}
					}else if(level=="15000usd"){
						if($("input[leval='500usd']").val()==""){
							$("input[leval='500usd']").val(price);
						}
						if($("input[leval='1000usd']").val()==""){
							$("input[leval='1000usd']").val(price);
						}
						if($("input[leval='2000usd']").val()==""){
							$("input[leval='2000usd']").val(price);
						}
						if($("input[leval='3000usd']").val()==""){
							$("input[leval='3000usd']").val(price);
						}
						if($("input[leval='5000usd']").val()==""){
							$("input[leval='5000usd']").val(price);
						}
						if($("input[leval='10000usd']").val()==""){
							$("input[leval='10000usd']").val(price);
						}
					}else if(level=="15000cny"){
						if($("input[leval='500cny']").val()==""){
							$("input[leval='500cny']").val(price);
						}
						if($("input[leval='1000cny']").val()==""){
							$("input[leval='1000cny']").val(price);
						}
						if($("input[leval='2000cny']").val()==""){
							$("input[leval='2000cny']").val(price);
						}
						if($("input[leval='3000cny']").val()==""){
							$("input[leval='3000cny']").val(price);
						}
						if($("input[leval='5000cny']").val()==""){
							$("input[leval='5000cny']").val(price);
						}
						if($("input[leval='10000cny']").val()==""){
							$("input[leval='10000cny']").val(price);
						}
					}
				}
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
				loading('Please wait a moment!');
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

	<form:form id="inputForm" modelAttribute="priceDto" action="${ctx}/psi/productTieredPrice/applyPrice" method="post" class="form-horizontal" enctype="multipart/form-data">
	    <input type="hidden" name="supplierId" value="${priceDto.supplierId}">
	    <input type="hidden" name="productId" value="${priceDto.productId}">
	    <input type="hidden" name="color"      value="${priceDto.color}">
	     <input type="hidden" name="proNameColor"  value="${priceDto.proNameColor}">
	    <blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">Base Info.(此处为<span style='color:red'>不含税</span>价格)</p>
		</blockquote>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:125px">产品名:</label>
				<div class="controls" style="margin-left:120px">
				  <select id="productSelect" name="aa">
				  	<c:forEach items="${productColorMap}" var="proEntry">
				  		<c:set value="${priceDto.productId},${priceDto.color}" var="oldKey"></c:set>
				  		<option value="${proEntry.key}" ${proEntry.key eq oldKey?'selected':''}>${proEntry.value}</option>
				  	</c:forEach>
				  </select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:125px">最小下单数:</label>
				<div class="controls" style="margin-left:120px">
				 <input type="text" readonly="readonly" name="moq" value="${priceDto.moq}">
				</div>
			</div>
			<c:if test="${priceDto.hasMulColor}">
			<div class="control-group" style="float:left;width:40%;height:30px">
				<label class="control-label" style="width:125px">同步其他颜色:</label>
				<div class="controls" style="margin-left:120px">
				 <input type="checkbox" name="hasColor" class="isCheck" value="1" checked/>
				</div>
			</div>
			</c:if>
			<c:if test="${!priceDto.hasMulColor}">
				<div class="control-group" style="float:left;width:40%;height:30px">
				 	<input type="hidden" name="hasColor" class="isCheck" value="0" />
				</div>
			</c:if>
		</div>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:125px">供应商:</label>
				<div class="controls" style="margin-left:120px">
				 <input type="text"  readonly="readonly" name="nikeName" value="${priceDto.nikeName}">
				</div>
			</div>
			<div class="control-group" style="float:left;width:70%;height:30px">
				<label class="control-label" style="width:125px">货币类型:</label>
				<div class="controls" style="margin-left:120px">
				 <input type="text"  readonly="readonly" name="currencyType" value="${priceDto.currencyType}">
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:80%" >
				<label class="control-label" style="width:125px">改价原因:</label>
				<div class="controls" style="margin-left:120px">
					<textarea name="content"  rows="4" cols="4" maxlength="255" style="width:65%" required="required"></textarea>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:80%" >
				<label class="control-label" style="width:125px">备注:</label>
				<div class="controls" style="margin-left:120px">
					<textarea name="remark"  rows="4" cols="4" maxlength="255" style="width:65%"></textarea>
				</div>
			</div>
		</div>
		
		
	  
	    <blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">Price Info.(请录入<span style='color'>不含税价</span>)</p>
		</blockquote>
	  
	  	<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:125px">500档($):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval500usd}">
							 <input type="text"  name="leval500usd"  leval="500usd"  class="${priceDto.currencyType eq 'USD'?'required':''}" value="${priceDto.leval500usd}" class="price">
						</c:when>
						<c:otherwise>
							 <input type="text"  name="leval500usd"  leval="500usd"  class="${priceDto.currencyType eq 'USD'?'required':''}" value="" class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="control-group" style="float:left;width:70%;height:30px">
				<label class="control-label" style="width:125px">500档(￥):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval500cny}">
							  <input type="text"  name="leval500cny" leval="500cny"  class="${priceDto.currencyType eq 'CNY'?'required':''}" value="${priceDto.leval500cny}" class="price">
						</c:when>
						<c:otherwise>
							  <input type="text"  name="leval500cny" leval="500cny"  class="${priceDto.currencyType eq 'CNY'?'required':''}" value="" class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:125px">1000档($):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval1000usd}">
							 <input type="text"  name="leval1000usd" leval="1000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="${priceDto.leval1000usd}" class="price">
						</c:when>
						<c:otherwise>
							  <input type="text"  name="leval1000usd" leval="1000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="" class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="control-group" style="float:left;width:70%;height:30px">
				<label class="control-label" style="width:125px">1000档(￥):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval1000cny}">
							  <input type="text"  name="leval1000cny" leval="1000cny"  class="${priceDto.currencyType eq 'CNY'?'required':''}" value="${priceDto.leval1000cny}" class="price">
						</c:when>
						<c:otherwise>
							   <input type="text"  name="leval1000cny" leval="1000cny"  class="${priceDto.currencyType eq 'CNY'?'required':''}" value=""  class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		
		
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:125px">2000档($):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval2000usd}">
							  <input type="text"  name="leval2000usd" leval="2000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="${priceDto.leval2000usd}" class="price">
						</c:when>
						<c:otherwise>
							  <input type="text"  name="leval2000usd" leval="2000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="" class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="control-group" style="float:left;width:70%;height:30px">
				<label class="control-label" style="width:125px">2000档(￥):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval2000cny}">
							 <input type="text"  name="leval2000cny" leval="2000cny" class="${priceDto.currencyType eq 'CNY'?'required':''}" value="${priceDto.leval1000cny}" class="price">
						</c:when>
						<c:otherwise>
							  <input type="text"  name="leval2000cny" leval="2000cny" class="${priceDto.currencyType eq 'CNY'?'required':''}" value="" class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		
		
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:125px">3000档($):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval3000usd}">
							  <input type="text"  name="leval3000usd" leval="3000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="${priceDto.leval3000usd}" class="price">
						</c:when>
						<c:otherwise>
							   <input type="text"  name="leval3000usd" leval="3000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="" class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="control-group" style="float:left;width:70%;height:30px">
				<label class="control-label" style="width:125px">3000档(￥):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval3000cny}">
							  <input type="text"  name="leval3000cny" leval="3000cny" class="${priceDto.currencyType eq 'CNY'?'required':''}" value="${priceDto.leval3000cny}" class="price">
						</c:when>
						<c:otherwise>
							   <input type="text"  name="leval3000cny" leval="3000cny" class="${priceDto.currencyType eq 'CNY'?'required':''}" value=""  class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:125px">5000档($):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval5000usd}">
							  <input type="text"  name="leval5000usd" leval="5000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="${priceDto.leval5000usd}" class="price">
						</c:when>
						<c:otherwise>
							   <input type="text"  name="leval5000usd" leval="5000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="" class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="control-group" style="float:left;width:70%;height:30px">
				<label class="control-label" style="width:125px">5000档(￥):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval5000cny}">
							  <input type="text"  name="leval5000cny" leval="5000cny" class="${priceDto.currencyType eq 'CNY'?'required':''}" value="${priceDto.leval5000cny}" class="price">
						</c:when>
						<c:otherwise>
							   <input type="text"  name="leval5000cny" leval="5000cny" class="${priceDto.currencyType eq 'CNY'?'required':''}" value="" class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:125px">10000档($):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval10000usd}">
							   <input type="text"  name="leval10000usd" leval="10000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="${priceDto.leval10000usd}" class="price">
						</c:when>
						<c:otherwise>
							    <input type="text"  name="leval10000usd" leval="10000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="" class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="control-group" style="float:left;width:70%;height:30px">
				<label class="control-label" style="width:125px">10000档(￥):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
						<c:when test="${not empty priceDto.leval10000cny}">
							   <input type="text"  name="leval10000cny" leval="10000cny" class="${priceDto.currencyType eq 'CNY'?'required':''}" value="${priceDto.leval10000cny}" class="price">
						</c:when>
						<c:otherwise>
							    <input type="text"  name="leval10000cny" leval="10000cny" class="${priceDto.currencyType eq 'CNY'?'required':''}" value="" class="price">
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		
			
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:125px">15000档($):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
							<c:when test="${not empty priceDto.leval15000usd}">
								    <input type="text"  name="leval15000usd" leval="15000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="${priceDto.leval15000usd}" class="price">
							</c:when>
							<c:otherwise>
								     <input type="text"  name="leval15000usd" leval="15000usd" class="${priceDto.currencyType eq 'USD'?'required':''}" value="" class="price">
							</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="control-group" style="float:left;width:70%;height:30px">
				<label class="control-label" style="width:125px">15000档(￥):</label>
				<div class="controls" style="margin-left:120px">
					<c:choose>
							<c:when test="${not empty priceDto.leval15000cny}">
								    <input type="text"  name="leval15000cny" leval="15000cny" class="${priceDto.currencyType eq 'CNY'?'required':''}" value="${priceDto.leval15000cny}" class="price">
							</c:when>
							<c:otherwise>
								     <input type="text"  name="leval15000cny" leval="15000cny" class="${priceDto.currencyType eq 'CNY'?'required':''}" value="" class="price">
							</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
			
		 <blockquote style="float:left;">
			<p style="font-size: 14px">上传供应商改价凭证</p>
		</blockquote>
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"></label>
			<div class="controls">
			<input name="supplierFile" type="file" id="myfileupload" required="required"/>
			</div>
		</div>
		
		<div style="float:left;width:100%" class="form-actions">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</form:form>
</body>
</html>
