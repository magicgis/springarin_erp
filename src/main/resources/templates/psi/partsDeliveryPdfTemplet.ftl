<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd ">       
<html xmlns="http://www.w3.org/1999/xhtml">  
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

<style>
<!--
	@page{size:285mm 210mm;}
	body {       
       font-family: SimSun;  
	}  
	table
	{mso-displayed-decimal-separator:"\.";
	mso-displayed-thousand-separator:"\,";}
.font527782
	{color:windowtext;
	font-size:9.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	mso-font-charset:134;}

.xl7827782{
	padding:0px;
	mso-ignore:padding;
	color:black;
	font-size:24.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	mso-font-charset:0;
	mso-number-format:General;
	text-align:center;
	vertical-align:middle;
	border-top:none;
	border-right:none;
	border-bottom:.5pt solid windowtext;
	border-left:none;
	mso-background-source:auto;
	mso-pattern:black none;
	white-space:nowrap;
	}
	
.xl6427782{
	padding:0px;
	mso-ignore:padding;
	color:black;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	mso-font-charset:134;
	mso-number-format:General;
	text-align:center;
	vertical-align:middle;
	mso-background-source:auto;
	mso-pattern:black none;
	white-space:nowrap;
	}
.comTd{
	height:13.95pt;font-family:SimSun;text-align: left;
	}
.PageNext{page-break-before: always;}	
-->
</style>
</head>
<body>

<div>
	<div style="display: inline-block;">
			<img  height="120" src="lichengLogo.png"  />
	</div>
  	<div style="display: inline-block; vertical-align: top; float: right;margin-top:30px" >
  	<table >
  		<tr><td>提货单号:</td><td> ${bill.billNo}</td></tr>
  		<tr><td>送货时间:</td><td><#if bill.deliveryDate??>${bill.deliveryDate!?date?string('yyyy-MM-dd')}</#if></td></tr>
  		<tr><td>供应商名称:</td><td>${bill.supplier.name}</td></tr>
  		<tr><td>制表人:</td><td>${cuser.name}</td></tr>
  		<tr><td>制表时间:</td><td>${.now}</td></tr>
  	</table>
  	</div>
</div>
<div>
	<table style="margin-top:10px">
	  		<tr><td>Add:</td><td> 深圳市龙岗区坂田街道石背路11号德众工业园B栋1楼B仓</td></tr>
	  		<tr><td>Tel：</td><td> 0755-23484029</td></tr>
	  		<tr><td>Web:</td><td>www.inateck.com</td></tr>
  	</table>
  </div>
  
	<div align="center" >
		<table border="0" cellpadding="0" cellspacing="0" width="951" class="xl6327782" style='border-collapse:collapse;width:714pt;word-break:keep-all'>
		 <tr class="xl6427782" height="55" style='height:54.95pt'>
		  <td colspan="7" height="55" class="xl7827782" style='height:54.95pt'><b>DELIVERY ORDER</b></td>
		 </tr>
		 
		 <tr class="xl6427782" height="53" style='height:30.95pt;'>
		  <td class="xl6627782" style='border:2px solid #000000;width:5%'>No.</td>
		  <td class="xl6627782" style='border:2px solid #000000;width:30%'>PartsName</td>
		  <td class="xl6627782" style='width:140px;border:2px solid #000000;width:20%'>Quantity</td>
		  <td class="xl6627782" style='border:2px solid #000000;width:35%' colspan="4">Notes</td>
		 </tr>
		 <#list bill.items as item>
		 		<#if item_index = 7>
		 			<#assign next=' PageNext'/>
		 		 <#elseif (item_index-7)%13=0>
		 			<#assign next=' PageNext'/>
		 		 <#else>
		 		 	<#assign next=''/>
		        </#if>	
				<tr class="xl7027782${next}" style='mso-height-source:userset;height:39.0pt;'>
					  <td class="xl7427782" style='border:2px solid #000000;vertical-align:middle;text-align: center;'>${item_index+1}</td>
					  <td class="xl7127782" style='width:100px;border:2px solid #000000;'>${item.partsName}</td>
					  <td class="xl7627782" style='border:2px solid #000000;'>${item.quantityLading} </td>
					  <td class="xl7627782" style='border:2px solid #000000' colspan="4">${item.remark}</td>
				</tr>
		</#list>   
				<tr height="25" style='mso-height-source:userset;height:18.95pt'>
		  			<td height="25"  colspan="7" style='height:18.95pt;font-family:SimSun;text-align: left;'>备注：</td>
		 		</tr>  
				<tr height="25" style='mso-height-source:userset;height:18.95pt'>
		  			<td height="25"  colspan="7" style='height:18.95pt;font-family:SimSun;text-align: left;'>1、送货地址:
		  			<#if (bill.tranSupplier.address)??>
		  				${bill.tranSupplier.address}
		  			<#else>
		  				深圳市龙岗区坂田街道石背路11号德众工业园B栋1楼B仓
		  			</#if>
		  			</td>
		 		</tr>
		 		
		 		<tr height="25" style='mso-height-source:userset;height:18.95pt'>
		  			<td height="25"  colspan="7" style='height:18.95pt;font-family:SimSun;text-align: left;'>2、交货事宜:交货时请随货附带收货单，收货单包括“理诚”产品型号、件数、发货方，收货单经收货仓库签收后方视为收货完成。</td>
		 		</tr>
		 		
		</table>
	</div>
</body>
</html>
