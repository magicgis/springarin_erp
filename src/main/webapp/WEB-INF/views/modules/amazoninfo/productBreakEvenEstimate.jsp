<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品保本价估算</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<script type="text/javascript">
		
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		
		$(document).ready(function(){
			
			
			$("#cost,#sale,#amz,#fba").focusout(function(){
				var cost = $("#cost").val();
				var sale = $("#sale").val();
				var amz = $("#amz").val();
				var fba = $("#fba").val();
				if(cost&&sale&&amz&&fba){
					var sale1 = sale/1.19;
					sale1 = (sale1-fba-sale*amz/100-cost);
					//sale = (sale-fba-sale*amz/100-cost)
					$("#run").text("德国利润："+sale1.toFixed(2)+"$");					
				}
			});
			
		    $("#selectType").change(function(){
		    	if($("#selectType").val()!=''){
		    	
		    		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			    		<c:if test="${dic.value ne 'com.unitek'&&dic.value ne 'mx'}">
			    		    $(".${dic.value }duty").val(0);
			    		    if("de"=="${dic.value}"||"fr"=="${dic.value}"||"it"=="${dic.value}"||"es"=="${dic.value}"||"uk"=="${dic.value}"){
			    		    	$(".${dic.value }duty").val($("#selectType").find("option:selected").attr("euDuty"));
			    		    }else if("com"=="${dic.value}"){
			    		    	$(".${dic.value }duty").val($("#selectType").find("option:selected").attr("usDuty"));
			    		    }else{
			    		    	$(".${dic.value }duty").val($("#selectType").find("option:selected").attr("${dic.value}Duty"));
			    		    }
			    		    $(".${dic.value }commission").val($("#selectType").find("option:selected").attr("${dic.value }Val"));
			    		</c:if>
		    		</c:forEach>
		    	}
		    });
		    
		    
		    $("#selectProductType").change(function(){
		    	if($("#selectProductType").val()!=''){
		    		  
		    		    $("#duty").val($("#selectProductType").find("option:selected").attr("euDuty"));
		    		    $("#commission").val($("#selectProductType").find("option:selected").attr("deVal"));
		    		 
		    	}
		    });
		    
		    
		    $("#totalCount1").click(function(){
		    	
			    var commission=$("#commission").val();
			    if(commission==''||commission==undefined){
			    	top.$.jBox.tip("佣金不能为空");
					return false;
			    }
		    	
		    	var salesPrice=$("#salesPrice").val();
		    	if(salesPrice==''){
		    		top.$.jBox.tip("售价不能为空");
					return false;
		    	}
		    	var weight=$("#weight1").val();
		    	if(weight==''){
		    		top.$.jBox.tip("产品运输重量不能为空");
					return false;
		    	}
		    	
		    	var length=$("#length1").val();
		    	var width=$("#width1").val();
		    	var height=$("#height1").val();
		    	if(length==''||width==''||height==''){
		    		top.$.jBox.tip("长宽高不能为空");
					return false;
		    	}
		    	

		    	var airW=parseFloat(length)*parseFloat(width)*parseFloat(height)/parseFloat(6000);
		    	if(parseFloat(airW)>parseFloat(weight)){
		    		$("#airWeight1").val(toDecimal(airW));
		    	}else{
		    		$("#airWeight1").val(weight);
		    	}
		    	
		    	var airWeight=$("#airWeight1").val();
		    	if(airWeight==''){
		    		top.$.jBox.tip("产品空运计费重量不能为空");
					return false;
		    	}
		    	
		    	
		    	var weight1=weight;
		    	var length1=length;
		    	var width1=width;
		    	var height1=height;
		    	var fbaFee=0;
		   
			    	if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1){
						weight1=parseFloat(weight1)+parseFloat(0.02);
						$("#desizeInfo").html("<b>小包装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$("#desizeInfo").html("<b>标准小装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$("#.desizeInfo").html("<b>大包装箱</b>");
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26){
						weight1=parseFloat(weight1)+parseFloat(0.1);
						$("#desizeInfo").html("<b>标准大包裹</b>");
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$("#desizeInfo").html("<b>小型超大尺寸</b>");
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$("#desizeInfo").html("<b>标准超大包裹</b>");
					}else{
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$("#desizeInfo").html("<b>大型超大包裹</b>");
					}
					
			    	if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1&&parseFloat(weight1)*parseFloat(1000)<=100){
						fbaFee=1.64;
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5&&parseFloat(weight1)*parseFloat(1000)<=500){
						if(parseFloat(weight1)*parseFloat(1000)<=100){
							fbaFee=1.81;
						}else if(parseFloat(weight1)*parseFloat(1000)>100&&parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=1.82;
						}else{
							fbaFee=1.95;
						}
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5&&parseFloat(weight1)*parseFloat(1000)<=1000){
						fbaFee=2.34;
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26&&parseFloat(weight1)<=12){
						if(parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=2.39;
						}else if(parseFloat(weight1)*parseFloat(1000)>250&&parseFloat(weight1)*parseFloat(1000)<=500){
							fbaFee=2.5;
						}else if(parseFloat(weight1)*parseFloat(1000)>500&&parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=3.08;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=3.62;
						}else if(parseFloat(weight1)*parseFloat(1000)>1500&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=3.66;
						}else if(parseFloat(weight1)*parseFloat(1000)>2000&&parseFloat(weight1)*parseFloat(1000)<=3000){
							fbaFee=4.34;
						}else if(parseFloat(weight1)*parseFloat(1000)>3000&&parseFloat(weight1)*parseFloat(1000)<=4000){
							fbaFee=4.36;
						}else if(parseFloat(weight1)*parseFloat(1000)>4000&&parseFloat(weight1)*parseFloat(1000)<=5000){
							fbaFee=4.37;
						}else if(parseFloat(weight1)*parseFloat(1000)>5000&&parseFloat(weight1)*parseFloat(1000)<=6000){
							fbaFee=4.7;
						}else if(parseFloat(weight1)*parseFloat(1000)>6000&&parseFloat(weight1)*parseFloat(1000)<=7000){
							fbaFee=4.7;
						}else if(parseFloat(weight1)*parseFloat(1000)>7000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=4.83;
						}else if(parseFloat(weight1)*parseFloat(1000)>8000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=4.83;
						}else if(parseFloat(weight1)*parseFloat(1000)>9000&&parseFloat(weight1)*parseFloat(1000)<=10000){
							fbaFee=4.83;
						}else if(parseFloat(weight1)*parseFloat(1000)>10000&&parseFloat(weight1)*parseFloat(1000)<=11000){
							fbaFee=4.99;
						}else if(parseFloat(weight1)*parseFloat(1000)>11000&&parseFloat(weight1)*parseFloat(1000)<=12000){
							fbaFee=5;
						}
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46&&parseFloat(weight1)*parseFloat(1000)<=2000){
						if(parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=5.03;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1250){
							fbaFee=5.14;
						}else if(parseFloat(weight1)*parseFloat(1000)>1250&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=5.18;
						}else if(parseFloat(weight1)*parseFloat(1000)>1250&&parseFloat(weight1)*parseFloat(1000)<=1750){
							fbaFee=5.18;
						}else if(parseFloat(weight1)*parseFloat(1000)>1750&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=5.25;
						}
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60&&parseFloat(weight1)<=30){
						if(parseFloat(weight1)<=1){
							fbaFee=5.01;
						}else if(parseFloat(weight1)<=2){
							fbaFee=5.21;
						}else if(parseFloat(weight1)<=3){
							fbaFee=6.13;
						}else if(parseFloat(weight1)<=4){
							fbaFee=6.18;
						}else if(parseFloat(weight1)<=5){
							fbaFee=6.18;
						}else if(parseFloat(weight1)<=6){
							fbaFee=6.38;
						}else if(parseFloat(weight1)<=7){
							fbaFee=6.47;
						}else if(parseFloat(weight1)<=8){
							fbaFee=6.52;
						}else if(parseFloat(weight1)<=9){
							fbaFee=6.52;
						}else if(parseFloat(weight1)<=10){
							fbaFee=6.55;
						}else if(parseFloat(weight1)<=15){
							fbaFee=7.1;
						}else if(parseFloat(weight1)<=20){
							fbaFee=7.55;
						}else if(parseFloat(weight1)<=25){
							fbaFee=8.55;
						}else if(parseFloat(weight1)<=30){
							fbaFee=8.55;
						}
					}else{
						if(parseFloat(weight1)<=5){
							fbaFee=6.71;
						}else if(parseFloat(weight1)<=10){
							fbaFee=7.74;
						}else if(parseFloat(weight1)<=15){
							fbaFee=8.28;
						}else if(parseFloat(weight1)<=20){
							fbaFee=8.75;
						}else if(parseFloat(weight1)<=25){
							fbaFee=9.69;
						}else if(parseFloat(weight1)<=30){
							fbaFee=9.71;
						}
					}
					$("#defbaFee").val(toDecimal(fbaFee));
				    //A（1+关税）+运费+FBA处理费+B*亚马逊佣金 = B/(1+VAT) A:采购价 B:保本价
			    	var commission=$("#commission").val();
			    	var tranFee=$("#tranFee").val();
			    	var tranFeeAir=$("#airTranFee").val();
			    	var tranFeeSea=$("#seaTranFee").val();
			    	var duty=$("#duty").val();
			    	var vat=1/(1+parseFloat($("#vat").text())/100); 
			    	
			    	var airPrice=(salesPrice*vat-salesPrice*(commission/100)-fbaFee-tranFeeAir*airWeight)/(1+duty/100);
			    	var seaPrice=(salesPrice*vat-salesPrice*(commission/100)-fbaFee-tranFeeSea*weight)/(1+duty/100);
			    	
			    	$("#airPrice").html(toDecimal(parseFloat(airPrice)*7.742));
			    	$("#seaPrice").html(toDecimal(parseFloat(seaPrice)*7.742));
		    });
		    
		    $("#totalCount").click(function(){
		    	var flag="0";
		    	$("#total tbody tr").each(function(i,j){
		    		var $this=$(this);
			    	var commission=$this.find(".commission").val();
			    	if(commission==''||commission==undefined){
			    		flag='1';
			    	}
		    	});	
		    	if(flag=='1'){
		    		top.$.jBox.tip("佣金不能为空");
					return false;
		    	}
		    	
		    	var purchasesPrice=$("#purchasesPrice").val();
		    	if(purchasesPrice==''){
		    		top.$.jBox.tip("采购价不能为空");
					return false;
		    	}
		    	var weight=$("#weight").val();
		    	if(weight==''){
		    		top.$.jBox.tip("产品运输重量不能为空");
					return false;
		    	}
		    	
		    	
		    	var length=$("#length").val();
		    	var width=$("#width").val();
		    	var height=$("#height").val();
		    	if(length==''||width==''||height==''){
		    		top.$.jBox.tip("长宽高不能为空");
					return false;
		    	}
		    	

		    	var airW=parseFloat(length)*parseFloat(width)*parseFloat(height)/parseFloat(6000);
		    	if(parseFloat(airW)>parseFloat(weight)){
		    		$("#airWeight").val(toDecimal(airW));
		    	}else{
		    		$("#airWeight").val(weight);
		    	}
		    	
		    	var airWeight=$("#airWeight").val();
		    	if(airWeight==''){
		    		top.$.jBox.tip("产品空运计费重量不能为空");
					return false;
		    	}
		    	
		    	var tempWeight=parseFloat(weight)*2.2046226;
		    	var tempL=parseFloat(length)*0.3937008;
		    	var tempW=parseFloat(width)*0.3937008;
		    	var tempH=parseFloat(height)*0.3937008;
		    	
		    	var vw=parseFloat(tempL)*parseFloat(tempW)*parseFloat(tempH)/139;
		    	if(parseFloat(vw)<=parseFloat(tempWeight)){
		    		vw=parseFloat(tempWeight);
		    	}
		    	
		    	var girth = 2 * (parseFloat(tempW)+ parseFloat(tempH));
		    	if(parseFloat(tempWeight)<=1&&parseFloat(tempL)<=15&&parseFloat(tempW)<=12&&parseFloat(tempH)<=0.75){//Small standard-size
		    		$(".comsizeInfo").html("<b>Small standard-size</b>");
		    		$(".comfba").val(2.41);
		    	}else if(parseFloat(tempWeight)<=20&&parseFloat(tempL)<=18&&parseFloat(tempW)<=14&&parseFloat(tempH)<=8){//Large standard-size
		    		$(".comsizeInfo").html("<b>Large standard-size</b>");
		    	    vw=parseFloat(vw)+parseFloat(0.25);
		    	    if(parseFloat(vw)<=1){
		    	    	$(".comfba").val(3.19);
		    	    }else if(parseFloat(vw)>1&&parseFloat(vw)<=2){
		    	    	$(".comfba").val(4.71);
		    	    }else{
		    	    	$(".comfba").val(toDecimal(parseFloat(4.71)+parseFloat(0.38)*(parseFloat(vw)-parseFloat(2))));
		    	    }
		    	}else if(parseFloat(tempWeight)<=70&&parseFloat(tempL)<=60&&parseFloat(tempW)<=30&&parseFloat(girth)<=130){//Small oversize
		    		$(".comsizeInfo").html("<span style='color:red;'><b>Small oversize</b></span>");
		    		vw=parseFloat(vw)+parseFloat(1);
		    		$(".comfba").val(toDecimal(parseFloat(8.13)+parseFloat(0.38)*(parseFloat(vw)-parseFloat(2))));
		    	}else if(parseFloat(tempWeight)<=150&&parseFloat(tempL)<=108&&parseFloat(girth)<=130){//Medium oversize
		    		$(".comsizeInfo").html("<span style='color:red;'><b>Medium oversize</b></span>");
		    		vw=parseFloat(vw)+parseFloat(1);
		    		$(".comfba").val(toDecimal(parseFloat(9.44)+parseFloat(0.38)*(parseFloat(vw)-parseFloat(2))));
		    	}else if(parseFloat(tempWeight)<=150&&parseFloat(tempL)<=108&&parseFloat(girth)<=165){//Large oversize
		    		$(".comsizeInfo").html("<span style='color:red;'><b>Large oversize</b></span>");
		    		vw=parseFloat(vw)+parseFloat(1);
		    		$(".comfba").val(toDecimal(parseFloat(73.18)+parseFloat(0.79)*(parseFloat(vw)-parseFloat(90))));
		    	}else if(parseFloat(tempWeight)>150||parseFloat(tempL)>108&&parseFloat(girth)>165){//Special oversize
		    		$(".comsizeInfo").html("<span style='color:red;'><b>Special oversize</b></span>");
		    		//vw=parseFloat(vw)+1;
		    		vw=parseFloat(tempWeight)+parseFloat(1);
		    		$(".comfba").val(toDecimal(parseFloat(137.32)+parseFloat(0.91)*(parseFloat(vw)-parseFloat(90))));
		    	}
		    	$(".comCryfba").val($(".comfba").val());
		    	
		    	var weight1=weight;
		    	var length1=length;
		    	var width1=width;
		    	var height1=height;
		    	var fbaFee=0;
		   
			    	if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1){
						weight1=parseFloat(weight1)+parseFloat(0.02);
						$(".desizeInfo").html("<b>小包装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$(".desizeInfo").html("<b>标准小装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$(".desizeInfo").html("<b>大包装箱</b>");
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26){
						weight1=parseFloat(weight1)+parseFloat(0.1);
						$(".desizeInfo").html("<b>标准大包裹</b>");
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".desizeInfo").html("<b>小型超大尺寸</b>");
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".desizeInfo").html("<b>标准超大包裹</b>");
					}else{
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".desizeInfo").html("<b>大型超大包裹</b>");
					}
					
					if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1&&parseFloat(weight1)*parseFloat(1000)<=100){
						fbaFee=1.64;
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5&&parseFloat(weight1)*parseFloat(1000)<=500){
						if(parseFloat(weight1)*parseFloat(1000)<=100){
							fbaFee=1.81;
						}else if(parseFloat(weight1)*parseFloat(1000)>100&&parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=1.82;
						}else{
							fbaFee=1.95;
						}
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5&&parseFloat(weight1)*parseFloat(1000)<=1000){
						fbaFee=2.34;
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26&&parseFloat(weight1)<=12){
						if(parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=2.39;
						}else if(parseFloat(weight1)*parseFloat(1000)>250&&parseFloat(weight1)*parseFloat(1000)<=500){
							fbaFee=2.5;
						}else if(parseFloat(weight1)*parseFloat(1000)>500&&parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=3.08;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=3.62;
						}else if(parseFloat(weight1)*parseFloat(1000)>1500&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=3.66;
						}else if(parseFloat(weight1)*parseFloat(1000)>2000&&parseFloat(weight1)*parseFloat(1000)<=3000){
							fbaFee=4.34;
						}else if(parseFloat(weight1)*parseFloat(1000)>3000&&parseFloat(weight1)*parseFloat(1000)<=4000){
							fbaFee=4.36;
						}else if(parseFloat(weight1)*parseFloat(1000)>4000&&parseFloat(weight1)*parseFloat(1000)<=5000){
							fbaFee=4.37;
						}else if(parseFloat(weight1)*parseFloat(1000)>5000&&parseFloat(weight1)*parseFloat(1000)<=6000){
							fbaFee=4.7;
						}else if(parseFloat(weight1)*parseFloat(1000)>6000&&parseFloat(weight1)*parseFloat(1000)<=7000){
							fbaFee=4.7;
						}else if(parseFloat(weight1)*parseFloat(1000)>7000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=4.83;
						}else if(parseFloat(weight1)*parseFloat(1000)>8000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=4.83;
						}else if(parseFloat(weight1)*parseFloat(1000)>9000&&parseFloat(weight1)*parseFloat(1000)<=10000){
							fbaFee=4.83;
						}else if(parseFloat(weight1)*parseFloat(1000)>10000&&parseFloat(weight1)*parseFloat(1000)<=11000){
							fbaFee=4.99;
						}else if(parseFloat(weight1)*parseFloat(1000)>11000&&parseFloat(weight1)*parseFloat(1000)<=12000){
							fbaFee=5;
						}
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46&&parseFloat(weight1)*parseFloat(1000)<=2000){
						if(parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=5.03;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1250){
							fbaFee=5.14;
						}else if(parseFloat(weight1)*parseFloat(1000)>1250&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=5.18;
						}else if(parseFloat(weight1)*parseFloat(1000)>1250&&parseFloat(weight1)*parseFloat(1000)<=1750){
							fbaFee=5.18;
						}else if(parseFloat(weight1)*parseFloat(1000)>1750&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=5.25;
						}
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60&&parseFloat(weight1)<=30){
						if(parseFloat(weight1)<=1){
							fbaFee=5.01;
						}else if(parseFloat(weight1)<=2){
							fbaFee=5.21;
						}else if(parseFloat(weight1)<=3){
							fbaFee=6.13;
						}else if(parseFloat(weight1)<=4){
							fbaFee=6.18;
						}else if(parseFloat(weight1)<=5){
							fbaFee=6.18;
						}else if(parseFloat(weight1)<=6){
							fbaFee=6.38;
						}else if(parseFloat(weight1)<=7){
							fbaFee=6.47;
						}else if(parseFloat(weight1)<=8){
							fbaFee=6.52;
						}else if(parseFloat(weight1)<=9){
							fbaFee=6.52;
						}else if(parseFloat(weight1)<=10){
							fbaFee=6.55;
						}else if(parseFloat(weight1)<=15){
							fbaFee=7.1;
						}else if(parseFloat(weight1)<=20){
							fbaFee=7.55;
						}else if(parseFloat(weight1)<=25){
							fbaFee=8.55;
						}else if(parseFloat(weight1)<=30){
							fbaFee=8.55;
						}
					}else{
						if(parseFloat(weight1)<=5){
							fbaFee=6.71;
						}else if(parseFloat(weight1)<=10){
							fbaFee=7.74;
						}else if(parseFloat(weight1)<=15){
							fbaFee=8.28;
						}else if(parseFloat(weight1)<=20){
							fbaFee=8.75;
						}else if(parseFloat(weight1)<=25){
							fbaFee=9.69;
						}else if(parseFloat(weight1)<=30){
							fbaFee=9.71;
						}
					}
					var eurToUsd='${eurToUsd}';
					$(".deCryfba").val(toDecimal(fbaFee));
					fbaFee=parseFloat(fbaFee)*parseFloat(eurToUsd);
					$(".defba").val(toDecimal(fbaFee));
					
			    	
					
					////uk
					weight1=weight;
			    	length1=length;
			    	width1=width;
			    	height1=height;
			    	fbaFee=0;
					
					if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1){
						weight1=parseFloat(weight1)+parseFloat(0.02);
						$(".uksizeInfo").html("<b>小包装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$(".uksizeInfo").html("<b>标准小装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$(".uksizeInfo").html("<b>大包装箱</b>");
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26){
						weight1=parseFloat(weight1)+parseFloat(0.1);
						$(".uksizeInfo").html("<b>标准大包裹</b>");
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".uksizeInfo").html("<b>小型超大尺寸</b>");
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".uksizeInfo").html("<b>标准超大包裹</b>");
					}else{
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".uksizeInfo").html("<b>大型超大包裹</b>");
					}
					
					if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1&&parseFloat(weight1)*parseFloat(1000)<=100){
						fbaFee=1.34;
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5&&parseFloat(weight1)*parseFloat(1000)<=500){
						if(parseFloat(weight1)*(1000)<=100){
							fbaFee=1.47;
						}else if(parseFloat(weight1)*parseFloat(1000)>100&&parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=1.62;
						}else{
							fbaFee=1.72;
						}
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5&&parseFloat(weight1)*parseFloat(1000)<=1000){
						fbaFee=1.97;
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26&&parseFloat(weight1)<=12){
						if(parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=1.98;
						}else if(parseFloat(weight1)*parseFloat(1000)>250&&parseFloat(weight1)*parseFloat(1000)<=500){
							fbaFee=2.09;
						}else if(parseFloat(weight1)*parseFloat(1000)>500&&parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=2.17;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=2.31;
						}else if(parseFloat(weight1)*parseFloat(1000)>1500&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=2.53;
						}else if(parseFloat(weight1)*parseFloat(1000)>2000&&parseFloat(weight1)*parseFloat(1000)<=3000){
							fbaFee=3.61;
						}else if(parseFloat(weight1)*parseFloat(1000)>3000&&parseFloat(weight1)*parseFloat(1000)<=4000){
							fbaFee=3.61;
						}else if(parseFloat(weight1)*parseFloat(1000)>4000&&parseFloat(weight1)*parseFloat(1000)<=5000){
							fbaFee=3.71;
						}else if(parseFloat(weight1)*parseFloat(1000)>5000&&parseFloat(weight1)*parseFloat(1000)<=6000){
							fbaFee=3.76;
						}else if(parseFloat(weight1)*parseFloat(1000)>6000&&parseFloat(weight1)*parseFloat(1000)<=7000){
							fbaFee=3.76;
						}else if(parseFloat(weight1)*parseFloat(1000)>7000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=3.85;
						}else if(parseFloat(weight1)*parseFloat(1000)>8000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=3.85;
						}else if(parseFloat(weight1)*parseFloat(1000)>9000&&parseFloat(weight1)*parseFloat(1000)<=10000){
							fbaFee=3.85;
						}else if(parseFloat(weight1)*parseFloat(1000)>10000&&parseFloat(weight1)*parseFloat(1000)<=11000){
							fbaFee=3.86;
						}else if(parseFloat(weight1)*parseFloat(1000)>11000&&parseFloat(weight1)*parseFloat(1000)<=12000){
							fbaFee=4;
						}
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46&&parseFloat(weight1)*parseFloat(1000)<=2000){
						if(parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=3.66;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1250){
							fbaFee=4.08;
						}else if(parseFloat(weight1)*parseFloat(1000)>1250&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=4.39;
						}else if(parseFloat(weight1)*parseFloat(1000)>1250&&parseFloat(weight1)*parseFloat(1000)<=1750){
							fbaFee=4.48;
						}else if(parseFloat(weight1)*parseFloat(1000)>1750&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=4.54;
						}
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60&&parseFloat(weight1)<=30){
						if(parseFloat(weight1)<=1){
							fbaFee=4.65;
						}else if(parseFloat(weight1)<=2){
							fbaFee=4.96;
						}else if(parseFloat(weight1)<=3){
							fbaFee=5.05;
						}else if(parseFloat(weight1)<=4){
							fbaFee=5.08;
						}else if(parseFloat(weight1)<=5){
							fbaFee=5.12;
						}else if(parseFloat(weight1)<=6){
							fbaFee=6.04;
						}else if(parseFloat(weight1)<=7){
							fbaFee=6.1;
						}else if(parseFloat(weight1)<=8){
							fbaFee=6.13;
						}else if(parseFloat(weight1)<=9){
							fbaFee=6.13;
						}else if(parseFloat(weight1)<=10){
							fbaFee=6.16;
						}else if(parseFloat(weight1)<=15){
							fbaFee=6.55;
						}else if(parseFloat(weight1)<=20){
							fbaFee=6.88;
						}else if(parseFloat(weight1)<=25){
							fbaFee=7.62;
						}else if(parseFloat(weight1)<=30){
							fbaFee=7.62;
						}
					}else{
						if(parseFloat(weight1)<=5){
							fbaFee=5.71;
						}else if(parseFloat(weight1)<=10){
							fbaFee=6.88;
						}else if(parseFloat(weight1)<=15){
							fbaFee=7.27;
						}else if(parseFloat(weight1)<=20){
							fbaFee=7.62;
						}else if(parseFloat(weight1)<=25){
							fbaFee=8.3;
						}else if(parseFloat(weight1)<=30){
							fbaFee=8.32;
						}
					}
					
					var gbpToUsd='${gbpToUsd}';
					$(".ukCryfba").val(toDecimal(fbaFee));
					fbaFee=parseFloat(fbaFee)*parseFloat(gbpToUsd);
					$(".ukfba").val(toDecimal(fbaFee));
					
					
					
					//it
					weight1=weight;
			    	length1=length;
			    	width1=width;
			    	height1=height;
			    	fbaFee=0;
			    	
					
					if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1){
						weight1=parseFloat(weight1)+parseFloat(0.02);
						$(".itsizeInfo").html("<b>小包装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$(".itsizeInfo").html("<b>标准小装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$(".itsizeInfo").html("<b>大包装箱</b>");
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26){
						weight1=parseFloat(weight1)+parseFloat(0.1);
						$(".itsizeInfo").html("<b>标准大包裹</b>");
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".itsizeInfo").html("<b>小型超大尺寸</b>");
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".itsizeInfo").html("<b>标准超大包裹</b>");
					}else{
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".itsizeInfo").html("<b>大型超大包裹</b>");
					}
					
					if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1&&parseFloat(weight1)*parseFloat(1000)<=100){
						fbaFee=2.55;
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5&&parseFloat(weight1)*parseFloat(1000)<=500){
						if(parseFloat(weight1)*parseFloat(1000)<=100){
							fbaFee=2.64;
						}else if(parseFloat(weight1)*parseFloat(1000)>100&&parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=2.89;
						}else{
							fbaFee=3.14;
						}
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5&&parseFloat(weight1)*1000<=1000){
						fbaFee=3.39;
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26&&parseFloat(weight1)<=11){
						if(parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=3.52;
						}else if(parseFloat(weight1)*parseFloat(1000)>250&&parseFloat(weight1)*parseFloat(1000)<=500){
							fbaFee=3.78;
						}else if(parseFloat(weight1)*parseFloat(1000)>500&&parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=3.41;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=4.75;
						}else if(parseFloat(weight1)*parseFloat(1000)>1500&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=4.96;
						}else if(parseFloat(weight1)*parseFloat(1000)>2000&&parseFloat(weight1)*parseFloat(1000)<=3000){
							fbaFee=5.39;
						}else if(parseFloat(weight1)*parseFloat(1000)>3000&&parseFloat(weight1)*parseFloat(1000)<=4000){
							fbaFee=5.92;
						}else if(parseFloat(weight1)*parseFloat(1000)>4000&&parseFloat(weight1)*parseFloat(1000)<=5000){
							fbaFee=6.16;
						}else if(parseFloat(weight1)*parseFloat(1000)>5000&&parseFloat(weight1)*parseFloat(1000)<=6000){
							fbaFee=6.26;
						}else if(parseFloat(weight1)*parseFloat(1000)>6000&&parseFloat(weight1)*parseFloat(1000)<=7000){
							fbaFee=6.26;
						}else if(parseFloat(weight1)*parseFloat(1000)>7000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=6.46;
						}else if(parseFloat(weight1)*parseFloat(1000)>8000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=6.48;
						}else if(parseFloat(weight1)*parseFloat(1000)>9000&&parseFloat(weight1)*parseFloat(1000)<=10000){
							fbaFee=6.62;
						}else if(parseFloat(weight1)*parseFloat(1000)>10000&&parseFloat(weight1)*parseFloat(1000)<=11000){
							fbaFee=6.62;
						}
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46&&parseFloat(weight1)*parseFloat(1000)<=2000){
						if(parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=6.66;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1250){
							fbaFee=6.81;
						}else if(parseFloat(weight1)*parseFloat(1000)>1250&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=7.05;
						}else if(parseFloat(weight1)*parseFloat(1000)>1500&&parseFloat(weight1)*parseFloat(1000)<=1750){
							fbaFee=7.1;
						}else if(parseFloat(weight1)*parseFloat(1000)>1750&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=7.14;
						}
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60&&parseFloat(weight1)<=30){
						if(parseFloat(weight1)<=1){
							fbaFee=7.02;
						}else if(parseFloat(weight1)<=2){
							fbaFee=7.14;
						}else if(parseFloat(weight1)<=3){
							fbaFee=7.15;
						}else if(parseFloat(weight1)<=4){
							fbaFee=7.64;
						}else if(parseFloat(weight1)<=5){
							fbaFee=7.68;
						}else if(parseFloat(weight1)<=6){
							fbaFee=8.52;
						}else if(parseFloat(weight1)<=7){
							fbaFee=8.52;
						}else if(parseFloat(weight1)<=8){
							fbaFee=8.64;
						}else if(parseFloat(weight1)<=9){
							fbaFee=8.69;
						}else if(parseFloat(weight1)<=10){
							fbaFee=8.74;
						}else if(parseFloat(weight1)<=15){
							fbaFee=9.68;
						}else if(parseFloat(weight1)<=20){
							fbaFee=9.98;
						}else if(parseFloat(weight1)<=25){
							fbaFee=10.62;
						}else if(parseFloat(weight1)<=30){
							fbaFee=11.15;
						}
					}else{
						if(parseFloat(weight1)<=5){
							fbaFee=7.68;
						}else if(parseFloat(weight1)<=10){
							fbaFee=8.74;
						}else if(parseFloat(weight1)<=15){
							fbaFee=9.63;
						}else if(parseFloat(weight1)<=20){
							fbaFee=9.94;
						}else if(parseFloat(weight1)<=25){
							fbaFee=11.15;
						}else if(parseFloat(weight1)<=30){
							fbaFee=11.22;
						}
					}
					$(".itCryfba").val(toDecimal(fbaFee));
					fbaFee=parseFloat(fbaFee)*parseFloat(eurToUsd);
					$(".itfba").val(toDecimal(fbaFee));
					
					
					//es
					weight1=weight;
			    	length1=length;
			    	width1=width;
			    	height1=height;
			    	fbaFee=0;

					if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1){
						weight1=parseFloat(weight1)+parseFloat(0.02);
						$(".essizeInfo").html("<b>小装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$(".essizeInfo").html("<b>标准小装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$(".essizeInfo").html("<b>大包装箱</b>");
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26){
						weight1=parseFloat(weight1)+parseFloat(0.1);
						$(".essizeInfo").html("<b>标准大包裹</b>");
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".essizeInfo").html("<b>小型超大尺寸</b>");
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".essizeInfo").html("<b>标准超大包裹</b>");
					}else{
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".essizeInfo").html("<b>大型超大包裹</b>");
					}
					
					if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1&&parseFloat(weight1)*parseFloat(1000)<=100){
						fbaFee=2.07;
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5&&parseFloat(weight1)*parseFloat(1000)<=500){
						if(parseFloat(weight1)*parseFloat(1000)<=100){
							fbaFee=2.4;
						}else if(parseFloat(weight1)*1000>100&&parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=2.61;
						}else{
							fbaFee=2.82;
						}
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5&&parseFloat(weight1)*parseFloat(1000)<=1000){
						fbaFee=2.93;
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26&&parseFloat(weight1)<=11){
						if(parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=2.76;
						}else if(parseFloat(weight1)*parseFloat(1000)>250&&parseFloat(weight1)*parseFloat(1000)<=500){
							fbaFee=3.19;
						}else if(parseFloat(weight1)*parseFloat(1000)>500&&parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=3.41;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=3.82;
						}else if(parseFloat(weight1)*parseFloat(1000)>1500&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=3.88;
						}else if(parseFloat(weight1)*parseFloat(1000)>2000&&parseFloat(weight1)*parseFloat(1000)<=3000){
							fbaFee=4.41;
						}else if(parseFloat(weight1)*parseFloat(1000)>3000&&parseFloat(weight1)*parseFloat(1000)<=4000){
							fbaFee=4.85;
						}else if(parseFloat(weight1)*parseFloat(1000)>4000&&parseFloat(weight1)*parseFloat(1000)<=5000){
							fbaFee=5.16;
						}else if(parseFloat(weight1)*parseFloat(1000)>5000&&parseFloat(weight1)*parseFloat(1000)<=6000){
							fbaFee=5.25;
						}else if(parseFloat(weight1)*parseFloat(1000)>6000&&parseFloat(weight1)*parseFloat(1000)<=7000){
							fbaFee=5.25;
						}else if(parseFloat(weight1)*parseFloat(1000)>7000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=5.38;
						}else if(parseFloat(weight1)*parseFloat(1000)>8000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=5.38;
						}else if(parseFloat(weight1)*parseFloat(1000)>9000&&parseFloat(weight1)*parseFloat(1000)<=10000){
							fbaFee=5.38;
						}else if(parseFloat(weight1)*parseFloat(1000)>10000&&parseFloat(weight1)*parseFloat(1000)<=11000){
							fbaFee=5.38;
						}else if(parseFloat(weight1)*parseFloat(1000)>11000&&parseFloat(weight1)*parseFloat(1000)<=12000){
							fbaFee=5.39;
						}
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46&&parseFloat(weight1)*parseFloat(1000)<=2000){
						if(parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=3.67;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1250){
							fbaFee=3.67;
						}else if(parseFloat(weight1)*parseFloat(1000)>1250&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=3.98;
						}else if(parseFloat(weight1)*parseFloat(1000)>1500&&parseFloat(weight1)*parseFloat(1000)<=1750){
							fbaFee=3.98;
						}else{
							fbaFee=4.23;
						}
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60&&parseFloat(weight1)<=30){
						if(parseFloat(weight1)<=1){
							fbaFee=4.02;
						}else if(parseFloat(weight1)<=2){
							fbaFee=4.49;
						}else if(parseFloat(weight1)<=3){
							fbaFee=4.97;
						}else if(parseFloat(weight1)<=4){
							fbaFee=5.02;
						}else if(parseFloat(weight1)<=5){
							fbaFee=5.18;
						}else if(parseFloat(weight1)<=6){
							fbaFee=6.59;
						}else if(parseFloat(weight1)<=7){
							fbaFee=6.71;
						}else if(parseFloat(weight1)<=8){
							fbaFee=6.91;
						}else if(parseFloat(weight1)<=9){
							fbaFee=7.32;
						}else if(parseFloat(weight1)<=10){
							fbaFee=7.62;
						}else if(parseFloat(weight1)<=15){
							fbaFee=8.2;
						}else if(parseFloat(weight1)<=20){
							fbaFee=8.9;
						}else if(parseFloat(weight1)<=25){
							fbaFee=8.9;
						}else if(parseFloat(weight1)<=30){
							fbaFee=9.89;
						}
					}else{
						if(parseFloat(weight1)<=5){
							fbaFee=5.18;
						}else if(parseFloat(weight1)<=10){
							fbaFee=7.62;
						}else if(parseFloat(weight1)<=15){
							fbaFee=8.24;
						}else if(parseFloat(weight1)<=20){
							fbaFee=8.9;
						}else if(parseFloat(weight1)<=25){
							fbaFee=9.65;
						}else if(parseFloat(weight1)<=30){
							fbaFee=11.07;
						}
					}
					$(".esCryfba").val(toDecimal(fbaFee));
					fbaFee=parseFloat(fbaFee)*parseFloat(eurToUsd);
					$(".esfba").val(toDecimal(fbaFee));
					
					
					//fr
					weight1=weight;
			    	length1=length;
			    	width1=width;
			    	height1=height;
			    	fbaFee=0;

					if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1){
						weight1=parseFloat(weight1)+parseFloat(0.02);
						$(".frsizeInfo").html("<b>小装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$(".frsizeInfo").html("<b>标准小装箱</b>");
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5){
						weight1=parseFloat(weight1)+parseFloat(0.04);
						$(".frsizeInfo").html("<b>大包装箱</b>");
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26){
						weight1=parseFloat(weight1)+parseFloat(0.1);
						$(".frsizeInfo").html("<b>标准大包裹</b>");
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".frsizeInfo").html("<b>小型超大尺寸</b>");
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60){
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".frsizeInfo").html("<b>标准超大包裹</b>");
					}else{
						weight1=parseFloat(weight1)+parseFloat(0.24);
						$(".frsizeInfo").html("<b>大型超大包裹</b>");
					}
					
					if(parseFloat(length1)<=20&&parseFloat(width1)<=15&&parseFloat(height1)<=1&&parseFloat(weight1)*parseFloat(1000)<=100){
						fbaFee=2.11;
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=2.5&&parseFloat(weight1)*parseFloat(1000)<=500){
						if(parseFloat(weight1)*parseFloat(1000)<=100){
							fbaFee=2.24;
						}else if(parseFloat(weight1)*1000>100&&parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=2.83;
						}else{
							fbaFee=3.47;
						}
					}else if(parseFloat(length1)<=33&&parseFloat(width1)<=23&&parseFloat(height1)<=5&&parseFloat(weight1)*parseFloat(1000)<=1000){
						fbaFee=4.15;
					}else if(parseFloat(length1)<=45&&parseFloat(width1)<=34&&parseFloat(height1)<=26&&parseFloat(weight1)<=11){
						if(parseFloat(weight1)*parseFloat(1000)<=250){
							fbaFee=4.39;
						}else if(parseFloat(weight1)*parseFloat(1000)>250&&parseFloat(weight1)*parseFloat(1000)<=500){
							fbaFee=4.98;
						}else if(parseFloat(weight1)*parseFloat(1000)>500&&parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=5.05;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=5.16;
						}else if(parseFloat(weight1)*parseFloat(1000)>1500&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=5.27;
						}else if(parseFloat(weight1)*parseFloat(1000)>2000&&parseFloat(weight1)*parseFloat(1000)<=3000){
							fbaFee=6.52;
						}else if(parseFloat(weight1)*parseFloat(1000)>3000&&parseFloat(weight1)*parseFloat(1000)<=4000){
							fbaFee=6.54;
						}else if(parseFloat(weight1)*parseFloat(1000)>4000&&parseFloat(weight1)*parseFloat(1000)<=5000){
							fbaFee=6.54;
						}else if(parseFloat(weight1)*parseFloat(1000)>5000&&parseFloat(weight1)*parseFloat(1000)<=6000){
							fbaFee=6.65;
						}else if(parseFloat(weight1)*parseFloat(1000)>6000&&parseFloat(weight1)*parseFloat(1000)<=7000){
							fbaFee=6.65;
						}else if(parseFloat(weight1)*parseFloat(1000)>7000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=6.82;
						}else if(parseFloat(weight1)*parseFloat(1000)>8000&&parseFloat(weight1)*parseFloat(1000)<=8000){
							fbaFee=6.82;
						}else if(parseFloat(weight1)*parseFloat(1000)>9000&&parseFloat(weight1)*parseFloat(1000)<=10000){
							fbaFee=6.82;
						}else if(parseFloat(weight1)*parseFloat(1000)>10000&&parseFloat(weight1)*parseFloat(1000)<=11000){
							fbaFee=6.86;
						}else if(parseFloat(weight1)*parseFloat(1000)>11000&&parseFloat(weight1)*parseFloat(1000)<=12000){
							fbaFee=6.87;
						}
					}else if(parseFloat(length1)<=61&&parseFloat(width1)<=46&&parseFloat(height1)<=46&&parseFloat(weight1)*parseFloat(1000)<=2000){
						if(parseFloat(weight1)*parseFloat(1000)<=1000){
							fbaFee=6.64;
						}else if(parseFloat(weight1)*parseFloat(1000)>1000&&parseFloat(weight1)*parseFloat(1000)<=1250){
							fbaFee=6.88;
						}else if(parseFloat(weight1)*parseFloat(1000)>1250&&parseFloat(weight1)*parseFloat(1000)<=1500){
							fbaFee=6.96;
						}else if(parseFloat(weight1)*parseFloat(1000)>1500&&parseFloat(weight1)*parseFloat(1000)<=1750){
							fbaFee=6.96;
						}else if(parseFloat(weight1)*parseFloat(1000)>1750&&parseFloat(weight1)*parseFloat(1000)<=2000){
							fbaFee=7.42;
						}
					}else if(parseFloat(length1)<=120&&parseFloat(width1)<=60&&parseFloat(height1)<=60&&parseFloat(weight1)<=30){
						if(parseFloat(weight1)<=1){
							fbaFee=7.14;
						}else if(parseFloat(weight1)<=2){
							fbaFee=8.15;
						}else if(parseFloat(weight1)<=3){
							fbaFee=8.56;
						}else if(parseFloat(weight1)<=4){
							fbaFee=8.92;
						}else if(parseFloat(weight1)<=5){
							fbaFee=8.98;
						}else if(parseFloat(weight1)<=6){
							fbaFee=9.51;
						}else if(parseFloat(weight1)<=7){
							fbaFee=9.62;
						}else if(parseFloat(weight1)<=8){
							fbaFee=9.67;
						}else if(parseFloat(weight1)<=9){
							fbaFee=9.67;
						}else if(parseFloat(weight1)<=10){
							fbaFee=9.72;
						}else if(parseFloat(weight1)<=15){
							fbaFee=10.39;
						}else if(parseFloat(weight1)<=20){
							fbaFee=10.92;
						}else if(parseFloat(weight1)<=25){
							fbaFee=10.92;
						}else if(parseFloat(weight1)<=30){
							fbaFee=12.16;
						}
					}else{
						if(parseFloat(weight1)<=5){
							fbaFee=9.03;
						}else if(parseFloat(weight1)<=10){
							fbaFee=10.95;
						}else if(parseFloat(weight1)<=15){
							fbaFee=11.59;
						}else if(parseFloat(weight1)<=20){
							fbaFee=12.16;
						}else if(parseFloat(weight1)<=25){
							fbaFee1=13.29;
						}else if(parseFloat(weight1)<=30){
							fbaFee=13.61;
						}
					}
					$(".frCryfba").val(toDecimal(fbaFee));
					fbaFee=parseFloat(fbaFee)*parseFloat(eurToUsd);
					$(".frfba").val(toDecimal(fbaFee));
					
		    	
		    	
				
				//jp
				weight1=weight;
		    	length1=length;
		    	width1=width;
		    	height1=height;
		    	fbaFee=0;

		    	
		    	
				if(parseFloat(length1)<=25&&parseFloat(width1)<=18&&parseFloat(height1)<=2&&parseFloat(weight1)<0.25){
					fbaFee=parseFloat(226);
					$(".jpsizeInfo").html("<b>小装箱</b>");
		    	}else if(parseFloat(length1)<=45&&parseFloat(width1)<=35&&parseFloat(height1)<=20&&parseFloat(weight1)<9){
		    		fbaFee=parseFloat(360)+(parseFloat(weight1)-parseFloat(2))*parseFloat(6);
		    		$(".jpsizeInfo").html("<b>标准装箱</b>");
		    	}else if((parseFloat(length1)>45||parseFloat(width1)>35||parseFloat(height1)>20)&&parseFloat(weight1)<9&&(parseFloat(length1)+parseFloat(width1)+parseFloat(height1))<170){
		    		if(parseFloat(length1)+parseFloat(width1)+parseFloat(height1)<100){
		    			fbaFee=622;
		    		}else if(parseFloat(length1)+parseFloat(width1)+parseFloat(height1)>=100&&parseFloat(length1)+parseFloat(width1)+parseFloat(height1)<140){
		    			fbaFee=676;
		    		}else if(parseFloat(length1)+parseFloat(width1)+parseFloat(height1)>=140&&parseFloat(length1)+parseFloat(width1)+parseFloat(height1)<170){
		    			fbaFee=738;
		    		}
		    		$(".jpsizeInfo").html("<b>大包裹</b>");
		    	}else if((parseFloat(length1)+parseFloat(width1)+parseFloat(height1))>=170&&(parseFloat(length1)+parseFloat(width1)+parseFloat(height1))<200&&parseFloat(length1)<90&&parseFloat(weight1)<40){
		    		fbaFee=1398;
		    		$(".jpsizeInfo").html("<b>超大包裹</b>");
		    	}
				var jpyToUsd='{jpyToUsd}';
				$(".jpCryfba").val(toDecimal(fbaFee));
				$(".jpfba").val(toDecimal(parseFloat(fbaFee)*parseFloat(0.009087769)));
				
				
				//ca
				weight1=weight;
		    	length1=length;
		    	width1=width;
		    	height1=height;
		    	fbaFee=0;

				var vw=parseFloat(length1)*parseFloat(height1)*parseFloat(width1)/parseFloat(6000);
				if(parseFloat(vw)<parseFloat(weight1)){
					vw=weight1;
				}
				if(parseFloat(weight1)<=0.5&&parseFloat(length1)<=38&&parseFloat(width1)<=27&&parseFloat(height1)<=2){//Small standard-size
					vw=parseFloat(vw)+parseFloat(0.025);
				    if(parseFloat(vw)>0.1){
				    	var mod=Math.ceil((parseFloat(vw)-parseFloat(0.1))/parseFloat(0.1));
				    	fbaFee=parseFloat(1.6)+parseFloat(1.9)+parseFloat(mod)*parseFloat(0.25);
				    }else{
				    	fbaFee=parseFloat(1.6)+parseFloat(1.9);
				    }
					
					$(".casizeInfo").html("<b>小装箱</b>");
		    	}else if(parseFloat(weight1)<=9&&parseFloat(length1)<=45&&parseFloat(width1)<=35&&parseFloat(height1)<=20){//Small standard-size
		    		vw=parseFloat(vw)+parseFloat(0.125);
		    		if(parseFloat(vw)>0.5){
		    			var mod=Math.ceil((parseFloat(vw)-parseFloat(0.5))/parseFloat(0.5));
		    			fbaFee=parseFloat(1.6)+parseFloat(4)+parseFloat(mod)*parseFloat(0.4);
					}else{
						fbaFee=parseFloat(1.6)+parseFloat(4);
					}
						
		    		
		    		$(".casizeInfo").html("<b>标准装箱</b>");
		    	}else{
		    		vw=parseFloat(vw)+parseFloat(0.5);
		    		if((parseFloat(length1)+parseFloat(2)*parseFloat(height1)+parseFloat(2)*parseFloat(width1))>419&&parseFloat(vw)>69){
		    			fbaFee=125;
		    			$(".casizeInfo").html("<b>超大包裹</b>");
		    		}else{
		    			if(parseFloat(vw)>0.5){
		    				var mod=Math.ceil((parseFloat(vw)-parseFloat(0.5))/parseFloat(0.5));
		    				fbaFee=parseFloat(2.65)+parseFloat(4)+parseFloat(mod)*parseFloat(0.4);
						}else{
							fbaFee=parseFloat(2.65)+parseFloat(4);
						}
		    			
		    			$(".casizeInfo").html("<b>大包裹</b>");
		    		}
		    		
		    	}
				fbaFee=parseFloat(fbaFee)*1.15;
				$(".caCryfba").val(toDecimal(fbaFee));
				var cadToUsd='${cadToUsd}';
				$(".cafba").val(toDecimal(parseFloat(fbaFee)*parseFloat(cadToUsd)));
				
		    	
		    	$("#total tbody tr").each(function(i,j){
		    		var $this=$(this);
			    	var fba=$this.find(".fba").val();
			    	if(fba==undefined){
			    		fba=2;
			    	}
			    	console.log("=="+fba);
			    	var commission=$this.find(".commission").val();
			    	var tranFee=$this.find(".tranFee").val();
			    	var tranFeeAir=$this.find(".airTranFee").val();
			    	var tranFeeSea=$this.find(".seaTranFee").val();
			    	var duty=$this.find(".duty").val();
			    	var vat=1/(1+parseFloat($this.find(".vat").text())/100); 
			    	//var safePrice=(purchasesPrice*(1+duty/100)+tranFee*weight)/(vat-(commission/100));
			    	var airPrice=(purchasesPrice*(1+duty/100)+tranFeeAir*airWeight+parseFloat(fba))/(vat-(commission/100));
			    	airPrice=airPrice+airPrice*0.1;
			    	var seaPrice=(purchasesPrice*(1+duty/100)+tranFeeSea*weight+parseFloat(fba))/(vat-(commission/100));
			    	seaPrice=seaPrice+seaPrice*0.1;
			    	var country=$this.find(".country").val();
			    	if(country=='de'||country=='fr'||country=='it'||country=='es'){
			    		$this.find(".airPrice").html(toDecimal(airPrice)+"$&nbsp;&nbsp;<b>("+toDecimal(airPrice/'${eurToUsd}')+"€)</b>");
			    		$this.find(".seaPrice").html(toDecimal(seaPrice)+"$&nbsp;&nbsp;<b>("+toDecimal(seaPrice/'${eurToUsd}')+"€)</b>");
			    	}else if(country=='uk'){
			    		$this.find(".airPrice").html(toDecimal(airPrice)+"$&nbsp;&nbsp;<b>("+toDecimal(airPrice/'${gbpToUsd}')+"￡)</b>");
			    		$this.find(".seaPrice").html(toDecimal(seaPrice)+"$&nbsp;&nbsp;<b>("+toDecimal(seaPrice/'${gbpToUsd}')+"￡)</b>");
			    	}else if(country=='ca'){
			    		$this.find(".airPrice").html(toDecimal(airPrice)+"$&nbsp;&nbsp;<b>("+toDecimal(airPrice/'${cadToUsd}')+"C$)</b>");
			    		$this.find(".seaPrice").html(toDecimal(seaPrice)+"$&nbsp;&nbsp;<b>("+toDecimal(seaPrice/'${cadToUsd}')+"C$)</b>");
			    	}else if(country=='jp'){
			    		$this.find(".airPrice").html(toDecimal(airPrice)+"$&nbsp;&nbsp;<b>("+toDecimal(airPrice/'${jpyToUsd}')+"¥)</b>");
			    		$this.find(".seaPrice").html(toDecimal(seaPrice)+"$&nbsp;&nbsp;<b>("+toDecimal(seaPrice/'${jpyToUsd}')+"¥)</b>");
			    	}else{
			    		$this.find(".airPrice").html(toDecimal(airPrice)+"$");
				    	$this.find(".seaPrice").html(toDecimal(seaPrice)+"$");
			    	}
		    	});
		    });
		});
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*100)/100;  
	            return f;  
	     }  
	</script>
</head>
<body>
    <div class="alert">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	  <strong>Warning!</strong> 
	  公式：A（1+关税）+运费+FBA处理费+B*亚马逊佣金 = B/(1+VAT) A:采购价 B:保本价&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</div>
    <!--    A（1+关税+消费税）+运费+FBA处理费+利润= B/(1+VAT)-B*亚马逊佣金  A:采购价  -->
	   <div id="runDiv" class="tab-pane hideCls">
			<form action="" class="breadcrumb form-search" cssStyle="height: 90px;">
					<div style="height: 90px;">
					    <label class="control-label">采购含税价($):</label>
					    <input  type="text" maxlength="10" class="input-small required" id="purchasesPrice"/>
					
						<label class="control-label">产品类型:</label>
						<select style="width: 180px" id="selectType">
						   <option value="">--请选择产品类型--</option>
						   <c:forEach items="${fns:getDictList('product_type')}" var="dic">
						       <c:set var="deKey" value="${dic.value}_de" />
						       <c:set var="comKey" value="${dic.value}_com" />
						       <c:set var="caKey" value="${dic.value}_ca" />
						       <c:set var="jpKey" value="${dic.value}_jp" />
						       <c:set var="ukKey" value="${dic.value}_uk" />
						       <c:set var="frKey" value="${dic.value}_fr" />
						       <c:set var="itKey" value="${dic.value}_it" />
						       <c:set var="esKey" value="${dic.value}_es" />
						       <option value="${dic.value}"  deVal='${codeMap[deKey].commissionPcent}' comVal='${codeMap[comKey].commissionPcent}' caVal='${codeMap[caKey].commissionPcent}' jpVal='${codeMap[jpKey].commissionPcent}' esVal='${codeMap[esKey].commissionPcent}' ukVal='${codeMap[ukKey].commissionPcent}' frVal='${codeMap[frKey].commissionPcent}' itVal='${codeMap[itKey].commissionPcent}'  usDuty="${dutyMap[dic.value].usCustomDuty}" caDuty="${dutyMap[dic.value].caCustomDuty}" euDuty="${dutyMap[dic.value].euCustomDuty}" jpDuty="${dutyMap[dic.value].jpCustomDuty}">${dic.label}</option>
					      </c:forEach>
						  <%--  <c:forEach items="${dutyMap}" var="duty">
						       	<option value='${duty.key }' usDuty="${dutyMap[duty.key].usCustomDuty}" caDuty="${dutyMap[duty.key].caCustomDuty}" euDuty="${dutyMap[duty.key].euCustomDuty}" jpDuty="${dutyMap[duty.key].jpCustomDuty}">${duty.key}</option>
						   </c:forEach> --%>
						</select>
						
						
						
					   	<label class="control-label">产品海运计费重量(kg):</label>
						<input type="text" maxlength="10" class="input-small required" id="weight"/>
						
					    <label class="control-label">产品空运计费重量(kg):</label>
						<input type="text" maxlength="10" class="input-small required" readonly id="airWeight"/>
						
						
						<br/><br/>
						
						<!-- <label class="control-label">泛欧:</label>
						<select style="width: 180px" id="euType">
						   <option value="0">--是--</option>
						   <option value="1">--否--</option>
						</select> -->
						
					   	<label class="control-label">最长边(cm):</label>
						<input type="text" maxlength="10" class="input-small required" id="length"/>
						
					    <label class="control-label">第二长边(cm):</label>
						<input type="text" maxlength="10" class="input-small required" id="width"/>
						
						<label class="control-label">最短边(cm):</label>
						<input type="text" maxlength="10" class="input-small required" id="height"/>
						<input type='button' id='totalCount' value='计算' class='btn  btn-primary'/>
						<!-- &nbsp;&nbsp;&nbsp;&nbsp;<span  id='sizeInfo'></span> -->
					</div>
			   
			</form>
			<table id="total" class="table table-striped table-bordered table-condensed">
				   <thead>
				        <tr>
				            <th>国家</th>
				            <th>FBA处理费</th>
				            <th>亚马逊佣金(%)</th>
				           <!--  <th><a title='1kg运费'>平均运费($/KG)</a></th> -->
				            <th><a title='1kg运费'>空运运费($/KG)</a></th>
				            <th><a title='1kg运费'>海运运费($/KG)</a></th>
				            <th>关税(%)</th>
				            <th>增值税(%)</th>
				          <!--   <th>平均保本价($)</th> -->
				            <th>空运保本价</th>
				            <th>海运保本价</th>
				            <th>类型</th>
				        </tr>
				   </thead>
				   <tbody>
					       <c:forEach items="${fns:getDictList('platform')}" var="dic">
					             <c:if test="${fn:contains('de,fr,it,es,uk',dic.value)}">
					                <c:set  var='key' value='EU'/>
					             </c:if>
					             <c:if test="${fn:contains('com,ca',dic.value)}">
					                <c:set  var='key' value='US'/>
					             </c:if>
					              <c:if test="${'jp' eq dic.value}">
					                <c:set  var='key' value='JP'/>
					             </c:if>
					             
								<c:if test="${dic.value ne 'com.unitek'&&dic.value ne 'mx'&&dic.value ne 'com2'&&dic.value ne 'com3'}">
								  <tr>
										<td>${dic.label}<input type='hidden' class='country' value='${dic.value }'></td>
										<td>
										    <input disabled class='input-small ${dic.value }Cryfba Cryfba' value='<fmt:formatNumber value="${dealFee[dic.value] }" maxFractionDigits="2"/>'/>
										    <input type='hidden' class='input-small ${dic.value }fba fba' value='<fmt:formatNumber value="${dealFee[dic.value] }" maxFractionDigits="2"/>'/>
										</td>
										<td><input class='input-small ${dic.value }commission commission' value='15'/></td>
										<%-- <td><input class='input-small ${dic.value }tranFee tranFee'  value='<fmt:formatNumber value="${countryTranFee[key].avgPrice/cnyToUsd }" maxFractionDigits="2"/>'/></td>
										 --%><td><input class='input-small ${dic.value }AirTranFee airTranFee'  value='<fmt:formatNumber value="${countryTranFee[key].airPrice/cnyToUsd }" maxFractionDigits="2"/>'/></td>
										<td><input class='input-small ${dic.value }SeaTranFee seaTranFee'  value='<fmt:formatNumber value="${countryTranFee[key].seaPrice/cnyToUsd }" maxFractionDigits="2"/>'/></td>
										<td><input class='input-small ${dic.value }duty duty'/></td>
										<td class='vat'><fmt:formatNumber value="${vatMap[dic.value]*100}" maxFractionDigits="2"/></td>
										<!-- <td class='safePrice'></td> -->
										<td class='airPrice'></td>
										<td class='seaPrice'></td>
										<td class='${dic.value }sizeInfo'></td>
									</tr>
								</c:if>
								 
							</c:forEach>	
				   </tbody>
				</table>
		</div>
		
		<shiro:hasPermission name="amazoninfo:profits:view">
		   <div class="alert">
			  <button type="button" class="close" data-dismiss="alert">&times;</button>
			  <strong>推算采购价</strong> 
	    </div>
		<div id="priceDiv" class="tab-pane hideCls">
			<form action="" class="breadcrumb form-search" cssStyle="height: 90px;">
					<div style="height: 90px;">
					    <label class="control-label">售价(€):</label>
					    <input  type="text" maxlength="10" class="input-small required" id="salesPrice"/>
					
						<label class="control-label">产品类型:</label>
						<select style="width: 180px" id="selectProductType">
						   <option value="">--请选择产品类型--</option>
						   <c:forEach items="${fns:getDictList('product_type')}" var="dic">
						       <c:set var="deKey" value="${dic.value}_de" />
						       <c:set var="comKey" value="${dic.value}_com" />
						       <c:set var="caKey" value="${dic.value}_ca" />
						       <c:set var="jpKey" value="${dic.value}_jp" />
						       <c:set var="ukKey" value="${dic.value}_uk" />
						       <c:set var="frKey" value="${dic.value}_fr" />
						       <c:set var="itKey" value="${dic.value}_it" />
						       <c:set var="esKey" value="${dic.value}_es" />
						       <option value="${dic.value}"  deVal='${codeMap[deKey].commissionPcent}' comVal='${codeMap[comKey].commissionPcent}' caVal='${codeMap[caKey].commissionPcent}' jpVal='${codeMap[jpKey].commissionPcent}' esVal='${codeMap[esKey].commissionPcent}' ukVal='${codeMap[ukKey].commissionPcent}' frVal='${codeMap[frKey].commissionPcent}' itVal='${codeMap[itKey].commissionPcent}'  usDuty="${dutyMap[dic.value].usCustomDuty}" caDuty="${dutyMap[dic.value].caCustomDuty}" euDuty="${dutyMap[dic.value].euCustomDuty}" jpDuty="${dutyMap[dic.value].jpCustomDuty}">${dic.label}</option>
					      </c:forEach>
						</select>
						
						
						
					   	<label class="control-label">产品海运计费重量(kg):</label>
						<input type="text" maxlength="10" class="input-small required" id="weight1"/>
						
					    <label class="control-label">产品空运计费重量(kg):</label>
						<input type="text" maxlength="10" class="input-small required" readonly id="airWeight1"/>
						
						
						<br/><br/>
						
						
					   	<label class="control-label">最长边(cm):</label>
						<input type="text" maxlength="10" class="input-small required" id="length1"/>
						
					    <label class="control-label">第二长边(cm):</label>
						<input type="text" maxlength="10" class="input-small required" id="width1"/>
						
						<label class="control-label">最短边(cm):</label>
						<input type="text" maxlength="10" class="input-small required" id="height1"/>
						<input type='button' id='totalCount1' value='计算' class='btn  btn-primary'/>
					</div>
			   
			</form>
			<table class="table table-striped table-bordered table-condensed">
				   <thead>
				        <tr>
				            <th>国家</th>
				            <th>FBA处理费</th>
				            <th>亚马逊佣金(%)</th>
				            <th><a title='1kg运费'>空运运费(€/KG)</a></th>
				            <th><a title='1kg运费'>海运运费(€/KG)</a></th>
				            <th>关税(%)</th>
				            <th>增值税(%)</th>
				            <th>空运保本采购价(￥)</th>
				            <th>海运保本采购价(￥)</th>
				            <th>类型</th>
				        </tr>
				   </thead>
				   <tbody>
					      
					            <c:set  var='key' value='EU'/>
								  <tr>
										<td>德国|DE<input type='hidden' id='country' value='${de}'></td>
										<td>
										    <input disabled id='defbaFee'  value='<fmt:formatNumber value="${dealFee['de']}"   maxFractionDigits="2"/>'/>
										</td>
										<td><input class='input-small'   id='commission' value='15'/></td>
										<td><input class='input-small'   id='airTranFee' value='<fmt:formatNumber value="${countryTranFee[key].airPrice*0.1292 }" maxFractionDigits="2"/>'/></td>
										<td><input class='input-small'   id='seaTranFee' value='<fmt:formatNumber value="${countryTranFee[key].seaPrice*0.1292 }" maxFractionDigits="2"/>'/></td>
										<td><input class='input-small' id='duty'/></td>
										<td id='vat'><fmt:formatNumber value="${vatMap['de']*100}" maxFractionDigits="2"/></td>
										<td id='airPrice'></td>
										<td id='seaPrice'></td>
										<td id='desizeInfo'></td>
								</tr>
				   </tbody>
				</table>
		</div>
		
		
		
		</shiro:hasPermission>
		 
		
</body>
</html>