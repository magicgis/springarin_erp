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

<div align="center" >
<table border="0" cellpadding="0" cellspacing="0" width="951" style='border-collapse:collapse;table-layout:fixed;width:714pt;word-break:keep-all'>
 <tr style='height:102.0pt;'>
  <td style='height:102.0pt' align="left" valign="top">	<img width="389" height="95" src="purpdf_logo.png"/> </td>
  <td align="right" valign="top">  <img width="400" height="115" src="purpdf_2.png"/>  </td>
 </tr>
</table> 
<table border="0" cellpadding="0" cellspacing="0" width="951" style='border-collapse:collapse;width:714pt;word-break:keep-all'>
		 <tr class="trHead" height="55" style='height:54.95pt'><td colspan="10" style="color:black;font-size:24.0pt;font-weight:400;font-style:normal;text-align:center"><b>DELIVERY ORDER</b></td></tr>
		 <tr class="trHead" height="20" style='height:20.95pt'>
		  	<td colspan="2" >厂家:${bill.supplier.name}</td>
		 	<td >送货时间:	<#if bill.deliveryDate??>${bill.deliveryDate!?date?string('yyyy-MM-dd')}</#if>
			</td><td colspan="2" ></td><td colspan="2" >提货单号:${bill.billNo}</td>
		 </tr>
		  <tr class="trBody" height="53" style='height:30.95pt;'>
		  <td height="53">No.</td>  <td>Item/C.Code</td>  <td width="50px">Color</td>  <td width="140px">Distribution</td> <td width="60px">Total Carton</td>  <td width="140px">BarCode</td>  <td>Sku</td> <td>Version</td>
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
			  		 	${(tempItem.sku)!""} <br/>
					  </#list> 
					  </td>
					   <td>
					  	<#list bill.countItems[tempBill] as tempItem>   
			  		 	${(tempItem.skuVersion)!""} <br/>
					  </#list>
					  </td>
				</tr>
		</#list>     
		<tr height="25" class="trHead">	<td height="25" colspan="7"></td></tr>
		<tr height="25" class="trHead"><td colspan="2"/><td colspan="1">总箱数:<span style="color:green"> ${totalPack}</span></td><td colspan="2">制表人: ${cuser.name}</td><td colspan="2">制表时间: ${.now}</td>	</tr>	 
</table>
</div>
</body>
</html>
