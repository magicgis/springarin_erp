<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd ">       
<html xmlns="http://www.w3.org/1999/xhtml">  
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<style>
<!--
	@page{size:285mm 210mm;}
	table {       
       font-family: SimSun;  
       font-weight:400;
       color:black;
       text-decoration:none;
	   vertical-align:middle;
	   white-space:nowrap;
	}  
	.trHead	{
		font-size:11.0pt;
		text-align:left;
		}
	.trBody	{
		font-size:10.0pt;
		text-align:center;
		}
	.trBody td {
		border:2px solid #000000;
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
  		<tr><td>总箱数：</td><td>${totalPack}</td></tr>
  		<tr><td>送货时间:</td><td><#if bill.deliveryDate??>${bill.deliveryDate!?date?string('yyyy-MM-dd')}</#if></td></tr>
  		<tr><td>供应商名称:</td><td>${bill.supplier.name}</td></tr>
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
<table border="0" cellpadding="0" cellspacing="0" width="951" style='border-collapse:collapse;width:714pt;word-break:keep-all'>
		 <tr class="trHead" height="55" style='height:54.95pt'><td colspan="10" style="color:black;font-size:24.0pt;font-weight:400;font-style:normal;text-align:center"><b>DELIVERY ORDER</b></td></tr>
		 <tr class="trHead" style='height:5.95pt'>	<td colspan="6" /> </tr>
		  <tr class="trBody" height="53" style='height:30.95pt;'>
		  <td height="53">No.</td>  <td>Item/C.Code</td>  <td width="50px">Color</td>  <td width="140px">Distribution</td> <td width="60px">Total Carton</td>  <td width="140px">BarCode</td>  <td>Sku</td> <td>Version</td>
		  <td>接收数</td><td>品检备注</td>
		 </tr>
		 <#list bill.countItems?keys as tempBill>
		 		<#if tempBill_index = 6>
		 			<#assign next=' PageNext'/>
		 		 <#elseif (tempBill_index-6)%13=0>
		 			<#assign next=' PageNext'/>
		 		 <#else>
		 		 	<#assign next=''/>
		        </#if>	
				<tr class="trBody${next}" style='height:39.0pt;'>
					  <td >${tempBill_index+1}</td>
					  <td class="trBody" style='word-wrap: break-word;'>${bill.countItems[tempBill][0].productName}</td>
					  <td>${bill.countItems[tempBill][0].colorCode}</td>
					  <td>
					  <#assign  proColor="${bill.countItems[tempBill][0].productName}${bill.countItems[tempBill][0].colorCode}"/>
					  <#list bill.countItems[tempBill] as tempItem>   
			  		 	 ${tempItem.countryStr?upper_case} <span style="color:red">${tempItem.quantityLading}</span> 箱数:<span style="color:green">${singleCountryMap[proColor][tempItem.countryCode]}</span> <br/>
					  </#list> 
					  </td>
					  
					  <td>
					    <span style="color:green">${singleProductMap[proColor]}</span>
					  </td>
					  
					  <td>
					  <#list bill.countItems[tempBill] as tempItem>   
			  		 	${tempItem.countryStr?upper_case} ${(fnSkuMap[tempItem.sku])!""} <br/>
					  </#list> 
					  </td>
					  <td>
					  	<#list bill.countItems[tempBill] as tempItem>   
			  		 	${(tempItem.sku)!""}<br/>
					  </#list> 
					  </td>
					   <td>
					  	<#list bill.countItems[tempBill] as tempItem>   
			  		 	${(tempItem.skuVersion)!""} <br/>
					  </#list>
					  </td>
					  <td>
						 	<#if (canSendMap[bill.countItems[tempBill][0].productNameColor])??>
						 	${canSendMap[bill.countItems[tempBill][0].productNameColor]}
						 	</#if>
					  </td>
					  <td>
						    <#if (remarkMap[bill.countItems[tempBill][0].productNameColor])??>
						 	${remarkMap[bill.countItems[tempBill][0].productNameColor]}
						 	</#if>
					  </td>
				</tr>
		</#list>     
		<tr height="25" style='mso-height-source:userset;height:18.95pt'>
		  <td height="25"  colspan="10" style='height:18.95pt;font-family:SimSun;text-align: left;'>备注：</td>
		 </tr>
		<tr height="25" style='mso-height-source:userset;height:18.95pt'>
		  <td height="25"  colspan="10" style='height:18.95pt;font-family:SimSun;text-align: left;'>1、送货地址:深圳市龙岗区坂田街道石背路11号德众工业园钢构栋1楼B号房<br/>
		  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;联系人：彭海18033426096。</td>
		 </tr>	
		 <tr height="25" style='mso-height-source:userset;height:18.95pt'>
		  <td height="25"  colspan="10" style='height:18.95pt;font-family:SimSun;text-align: left;'>2、收货时间:周一至周五 上午9:00-12:00、下午13:30-17:30。</td>
		 </tr> 
		 <tr height="25" style='mso-height-source:userset;height:18.95pt'>
		  <td height="25"  colspan="10" style='height:18.95pt;font-family:SimSun;text-align: left;'>3、交货事宜:交货时请随货附带收货单，收货单包括“理诚”产品型号、件数、装箱数量、发货方，<br/>
		  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;收货单经收货仓库签收后方视为收货完成。</td>
		 </tr> 
</table>
</div>
</body>
</html>
