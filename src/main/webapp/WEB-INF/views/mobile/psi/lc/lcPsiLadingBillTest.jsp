<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>质检单填写</title>
	<%@include file="/WEB-INF/views/mobile/include/head.jsp" %>
	<meta name="decorator" content="default"/>
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
			if(!(top)){
				top = self; 
			}
			
			$("input[name=isOk]").click(function(){
				if($(this).is(":checked")){
					if($(this).val()=="1"){
						$("input[name=dealWay]").each(function(){
							$(this).attr("disabled","disabled");
						});
					}else{
						$("input[name=dealWay]").each(function(){
							$(this).removeAttr("disabled");  
						});
					}
					$(this).attr("checked",true).siblings().attr("checked",false);
				}else{
					$(this).attr("checked",false).siblings().attr("checked",false);
				}
			});
			
			if('${test.isOk}'=='1'){
				$("input[name=dealWay]").each(function(){
					$(this).attr("disabled","disabled");
				});
			}
			
			$("input[name=dealWay]").click(function(){
				if($(this).is(":checked")){
					$(this).attr("checked",true).siblings().attr("checked",false);
				}else{
					$(this).attr("checked",false).siblings().attr("checked",false);
				}
			});
			
			$("#btnSureSubmit").on('click',function(e){
				 if($("#inputForm").valid()){
					 
					 if($("input[name='okQuantity']").val()&&(parseInt($("input[name='okQuantity']").val())>parseInt($("input[name='totalQuantity']").val()))){
							top.$.jBox.tip("合格数不能大于订单数","info",{timeout:3000});
							return false;
						}
						
						var totalQ=$(".totalQuantity").val();
						if(parseInt($("input[name='totalQuantity']").val())>parseInt(totalQ)){
							top.$.jBox.tip("订单数不能大于"+totalQ,"info",{timeout:3000});
							return false;
						}
						
						var isOk="";
						
						$("input[name=isOk]").each(function(){
							if($(this).is(":checked")){
								isOk=$(this).val();
								return false;
							}
						});
						
						if(isOk == null || isOk==""){
							top.$.jBox.tip("请选择检验结果！","info",{timeout:3000});
							return false;
						}
						
						if(isOk==2){
							if($("input[name='okQuantity']").val()==''){
								top.$.jBox.tip("部分合格必须填写合格数！","info",{timeout:3000});
								return false;
							}
						}
						
					 top.$.jBox.confirm('确认要申请审核？申请后将发送邮件通知品质主管进行审核！','系统提示',function(v,h,f){
							if(v=='ok'){
								$("#toReview").val("3");
								$("#inputForm").submit();
								$("#btnSubmit").attr("disabled","disabled");
								$("#btnSureSubmit").attr("disabled","disabled");
							}
							return true;
							},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				 }else{
					 return false;
				 };
			});	
			

			$("#inputForm").validate({
				submitHandler: function(form){
					if($("input[name='okQuantity']").val()&&($("input[name='okQuantity']").val()>$("input[name='receivedQuantity']").val())){
						top.$.jBox.tip("合格数不能大于订单数","info",{timeout:3000});
						return false;
					}
					
					var receivedQ=$(".receivedQuantity").val();
					if($("input[name='receivedQuantity']").val()>receivedQ){
						top.$.jBox.tip("订单数不能大于"+receivedQ,"info",{timeout:3000});
						return false;
					}
					
					var isOk="";
					
					$("input[name=isOk]").each(function(){
						if($(this).is(":checked")){
							isOk=$(this).val();
							return false;
						}
					});
					
					if(isOk==2){
						if($("input[name='okQuantity']").val()==''){
							top.$.jBox.tip("部分合格必须填写合格数！","info",{timeout:3000});
							return false;
						}
					}
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
					$("#btnSureSubmit").attr("disabled","disabled");
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
<div data-role="page" id="home">
	<jsp:include page="../../sys/headDiv.jsp"></jsp:include>
	<%--<div data-role="header" data-theme="b" data-position="fixed" style="height:45px">
	    <a href="${ctx}" data-role="button">Home</a>
		<h1>填写质检单</h1>
	    <a href="${ctx}/logout" data-role="button" class="ui-btn-right">Logout</a>
	</div> --%>
	<div data-role="content">
	<form:form id="inputForm" modelAttribute="test" action="${ctx}/psi/lcPsiLadingBill/qualityTestSave" method="post" class="form-horizontal">
	    <input type='hidden' name="id" value="${test.id}">
	    <input type='hidden' name="ladingId" value="${test.ladingId}">
	    <input type='hidden' name="ladingBillNo" value="${test.ladingBillNo}">
	    <input type='hidden' name="productName" value="${test.productName}">
	     <input type='hidden' name="supplierId" value="${test.supplierId}">
	    <input type='hidden' name="color" value="${test.color}">
	    <input type='hidden' class="receivedQuantity" value="${test.receivedQuantity}">
	     <input type="hidden" name="testSta" 	value="0" id="toReview" />
	       <div style="width:87%;min-height:100px;height:auto" class="alert alert-info">1，"订单数"为本次“质检数”，如果本次只质检了一部分，请修改该数量！！！
	       <br/>2，如果选择“部分合格”，会拆成一个“合格”单（数量为所填写的合格数），一个“不合格”单（数量为订单数-合格数），“不合格处理方式”会继承到不合格单里面</div>
		<div data-role="fieldcontain">
			<label for="ladingBillNo">提单号：</label> 
			<input type="text" value="${test.ladingBillNo}" class="required" readonly/>
		</div>
		<div data-role="fieldcontain">
			<label for="productName">产品名称：</label> 
			<input type="text" value="${test.productName}" class="required" readonly/>
		</div>
		<div data-role="fieldcontain">
			<label for="aql">AQL:</label>
			<textarea rows="2" cols="2" name="aql">${test.aql}</textarea>
		</div>
		<div data-role="fieldcontain">
			<label for="inView">内观:</label>
			<textarea rows="2" cols="2" name="inView">${test.inView}</textarea>
		</div>
		<div data-role="fieldcontain">
			<label for="function">功能:</label>
			<textarea rows="2" cols="2" name="function">${test.function}</textarea>
		</div>
		<div data-role="fieldcontain">
			<label for="outView">外观:</label>
			<textarea rows="2" cols="2" name="outView">${test.outView}</textarea>
		</div>
		<div data-role="fieldcontain">
			<label for="packing">包装:</label>
			<textarea rows="2" cols="2" name="packing">${test.packing}</textarea>
		</div>
		<div data-role="fieldcontain">
		 	<label for="isOk">检验结果判定:</label>
			<c:choose>
				<c:when test="${canEdit eq '0' }">
					<input name="isOk" type="hidden" value="${test.isOk}"  />
					<c:if test="${test.isOk eq '1'}">合格</c:if>
					<c:if test="${test.isOk eq '0'}">不合格</c:if>
					<c:if test="${test.isOk eq '2'}">部分合格</c:if>
				</c:when>
				<c:otherwise>
		 			<fieldset data-role="controlgroup" data-type="horizontal">
		         	<input type="radio" name="isOk" id="radio-view-a" ${test.isOk eq '1'?'checked':''} value="1"  />
		         	<label for="radio-view-a">合格</label>
		         	<input type="radio" name="isOk" id="radio-view-b" ${test.isOk eq '0'?'checked':''} value="0"  />
		         	<label for="radio-view-b">不合格</label>
		         	<input type="radio" name="isOk" id="radio-view-c" ${test.isOk eq '2'?'checked':''} value="2"  />
		         	<label for="radio-view-c">部分合格</label>
	    			</fieldset>
				</c:otherwise>
			</c:choose>
	    </div>
		<div data-role="fieldcontain">
		 	<label for="dealWay">不合格处理方式:</label>
			<c:choose>
				<c:when test="${canEdit eq '0' }">
					<input name="dealWay" type="hidden" value="${test.dealWay}"  />
					<c:if test="${test.isOk ne '1'}">
						${test.dealWay eq '2'?'直接返工':'各方协商'}
					</c:if>
				</c:when>
				<c:otherwise>
		 			<fieldset data-role="controlgroup" data-type="horizontal">
		         	<input type="radio" name="dealWay" id="radio-view-a" ${test.dealWay eq ''?'checked':''} value=""  />
		         	<label for="radio-view-a">各方协商</label>
		         	<input type="radio" name="dealWay" id="radio-view-b" ${test.dealWay eq '2'?'checked':''} value="2"  />
		         	<label for="radio-view-b">直接返工</label>
	    			</fieldset>
				</c:otherwise>
			</c:choose>
	    </div>
		<div data-role="fieldcontain">
			<label for="testQuantity">抽样数：</label> 
			<input name="testQuantity" type="text" value="${test.testQuantity}" class="required number"/>
		</div>
		<div data-role="fieldcontain">
			<label for="totalQuantity">订单数：</label>
			<c:choose>
				<c:when test="${canEdit eq '0' }">
					<input name="totalQuantity" type="hidden" value="${test.totalQuantity}"  />
					${test.totalQuantity}
				</c:when>
				<c:otherwise>
					<input name="totalQuantity" type="text" value="${not empty test.totalQuantity?test.totalQuantity:totalQuantity}" class="required number"/>
				</c:otherwise>
			</c:choose>
		</div>
		<div data-role="fieldcontain">
			<label for="okQuantity">合格数(<span style='color:red'>部分合格时必填</span>)：</label>
			<c:choose>
				<c:when test="${canEdit eq '0' }">
					<input name="okQuantity" type="hidden" value="${test.okQuantity}"  />
					${test.okQuantity}
				</c:when>
				<c:otherwise>
					<input name="okQuantity" type="text" value="${test.okQuantity}" class="number"/>
				</c:otherwise>
			</c:choose>
		</div>
		<div style="text-align:center">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存预申请"/>
			<input id="btnSureSubmit" class="btn btn-info" type="button" value="申请审核"/>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	</div>
		<jsp:include page="../../sys/footDiv.jsp"></jsp:include>
</div>
</body>
</html>
