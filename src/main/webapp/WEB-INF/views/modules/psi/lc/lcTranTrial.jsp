<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单管理</title>
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
				
			eval('var proMap = ${proMap}');
			
			$("#contentTable").on("change",".productId",function(){
				var tr = $(this).parent().parent();
				var productId = $(this).val();
				var product = proMap[productId];
				tr.find(".packQuantity").val(product.packQuantity);
				tr.find(".volume").val(product.boxVolume);
				tr.find(".weight").val(product.gw);
			});
			
			$("#contentTable").on("change",".productQuantity",function(){
				var tr =$(this).parent().parent();
				var quantity =$(this).val();
				var packQuantity=tr.find(".packQuantity").val();
				var box=quantity/packQuantity;
				tr.find(".boxQuantity").val(box);
				var singleVolume =parseFloat(tr.find(".volume").val())*box;
				var singleWeight =parseFloat(tr.find(".weight").val())*box;
				tr.find(".singleVolume").val(toDecimal(singleVolume));
				tr.find(".singleWeight").val(toDecimal(singleWeight));
				//重新算重量体积
				var tbody = tr.parent();
				var totalBox =0;
				var totalVolume=0;
				var totalWeight=0;
				tbody.find(".boxQuantity").each(function(){
					var tr =$(this).parent().parent();
					totalBox=totalBox+parseFloat($(this).val());
					totalVolume=parseFloat(totalVolume)+parseFloat(tr.find(".singleVolume").val());
					totalWeight=parseFloat(totalWeight)+parseFloat(tr.find(".singleWeight").val());
				});
				
				$(".totalBox").text(toDecimal(totalBox));
				$(".totalVolume").text(toDecimal(totalVolume));
				$(".totalWeight").text(toDecimal(totalWeight));
				
			});
			
			
			$(".remove-row").live("click",function(){
				 if($('#contentTable tbody tr').size()>1){
					var tr = $(this).parent().parent();
					tr.remove();
				}
				 
				//重新算重量体积
				var totalBox =0;
				var totalVolume=0;
				var totalWeight=0;
				$("#contentTable tbody tr").find(".boxQuantity").each(function(){
					var tr =$(this).parent().parent();
					totalBox=totalBox+parseFloat($(this).val());
					totalVolume=parseFloat(totalVolume)+parseFloat(tr.find(".singleVolume").val());
					totalWeight=parseFloat(totalWeight)+parseFloat(tr.find(".singleWeight").val());
				});
				
				$(".totalBox").text(toDecimal(totalBox));
				$(".totalVolume").text(toDecimal(totalVolume));
				$(".totalWeight").text(toDecimal(totalWeight));
			});
			
			$("#add-row").on("click",function(e){
				e.preventDefault();
				var tbody=$("#contentTable tbody");
				var opts="";
				for(var key in proMap){
					opts+="<option value='"+proMap[key].productId+"'>"+proMap[key].productName+"</option>";
				}
				var tr=$("<tr></tr>");
				tr.append("<td><select class='productId' style='width:90%'>"+opts+"</select></td>");
				tr.append("<td><input type='text' maxlength='11' style='width: 80%'  class='number productQuantity' /></td>");
				tr.append("<td><input type='text' maxlength='11' style='width: 80%' readonly class='packQuantity'/></td>");
				tr.append("<td><input type='text' maxlength='50' style='width: 80%' readonly class='volume'  /></td>");
		        tr.append("<td><input type='text' maxlength='50' style='width: 80%' readonly class='weight' /></td>");
				tr.append("<td><input type='text' maxlength='11' style='width: 80%' readonly class='boxQuantity'/></td>");
	            tr.append("<td><input type='text' maxlength='50' style='width: 80%' readonly class='singleVolume'  /></td>");
	            tr.append("<td><input type='text' maxlength='50' style='width: 80%' readonly class='singleWeight' /></td>");
	            tr.append("<td><a href='#' class='remove-row'><span class='icon-minus'></span>删除产品</a></td>");
				tbody.append(tr);
				tr.find("select.productId").select2();
				tr.find("select.productId").change();
			});
			$("#add-row").click();
		});
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*100)/100;  
	            return f;  
	     };
			
	</script>
</head>
<body>
<div style="text-align:center;height:50px;font-size: 20px;" class="alert alert-info">
	<span style="font-weight: bold;"> 总箱数：</span><span class="totalBox"></span>
	<span style="font-weight: bold;"> 总体积：</span><span class="totalVolume"></span>
	<span style="font-weight: bold;"> 总重量：</span><span class="totalWeight"></span>

</div>
		
		<blockquote  style="float:left;">
			<div style="float: left"><p style="font-size: 14px">试算产品信息</p></div>
		</blockquote>
		
		<div  style="font-size: 14px;margin: 5px 100px 5px 0px;float:right">
		<div id="add-flag"><a href="#" id="add-row"><span class="icon-plus"></span>增加</a></div></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">产品名</th>
				   <th style="width: 10%">试算数量</th>
				   <th style="width: 10%">装箱数</th>
				   <th style="width: 10%">单个体积</th>
				   <th style="width: 10%">单个重量</th>
				   <th style="width: 10%">大箱数</th>
				   <th style="width: 10%">总体积</th>
				   <th style="width: 10%">总重量</th>
				   <th>操作</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
		</table>
</body>
</html>
