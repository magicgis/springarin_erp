<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd ">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<style>
<!--
.headTd{border: solid #666666 1.0pt; border-top: none; background: #DEDEDE; padding: 3.0pt 3.0pt 3.0pt 3.0pt;text-align:center}
.bodyTd{border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt;text-align:center}
.conSpan{font-size: 8.5pt; color: #333333;text-align:center};
-->
</style>
</head>
<body>
	<div>
		<span style="line-height: 1.5;">Hi all,</span>
	</div>
	<div>
		<div style="font: Verdana normal 14px; color: #000;">
			<div class="WordSection1">
				<p  style="margin-bottom: 12.0pt">
					<span >Transport NO.：${inventoryIn.tranLocalNo}${inventoryIn.isNew?string('<span style="color:red">(本票货含新品未贴码的产品，请尽快绑sku并联系仓库贴码！)</span>','')}</span>
				</p>
			
				
				<table class="MsoNormalTable" border="0" cellspacing="0"cellpadding="0" style="border-collapse: collapse;width:800px" id="ex0">
					<tbody>
						
						<tr>
							<td colspan="8"class="headTd"><p  align="center" style="text-align: center"><b><span class="conSpan">Detail List（${inventoryIn.billNo}）</span></b></p></td>
						</tr>
						<tr>
							<td	class="bodyTd">	<span class="conSpan">NO.</span>	</td>
							<td	class="bodyTd"> <span class="conSpan">Product Name</span></td>
							<td	class="bodyTd">	<span class="conSpan">Country</span>	</td>
							<td	class="bodyTd">	<span class="conSpan">Color</span>	</td>
							<td	class="bodyTd">	<span class="conSpan">SKU</span></td>
							<td	class="bodyTd">	<span class="conSpan">Delivery Quantity</span></td>
							<td	class="bodyTd">	<span class="conSpan">Received Quantity</span></td>
							<td	class="bodyTd">	<span class="conSpan">Memo</span>	</td>
						</tr>
						<#list inventoryIn.viewItems as bItem>
						<tr>
							<td class="bodyTd">	<span 	class="conSpan">${bItem_index+1}</span></td>
							<td class="bodyTd">	<span 	class="conSpan">${bItem.productName}</span>	</td>
							<td class="bodyTd">	<span 	class="conSpan">${bItem.countryCode}</span>	</td>
							<td class="bodyTd">	<span 	class="conSpan">${bItem.colorCode}</span></td>
							<td class="bodyTd">	<span 	class="conSpan" style="color:${bItem.isNew?string('red','')}"> ${bItem.sku}</span>	</td>
							<td class="bodyTd">	<span 	class="conSpan">${bItem.tranQuantity}</span></td>
							<td class="bodyTd">	<span 	class="conSpan">${bItem.quantity}</span></td>
							<td class="bodyTd">	<span 	class="conSpan">${bItem.remark}</span></td>
						</tr>
						</#list>
					</tbody>
				</table>
				
			</div>
		</div>
	</div>
	<style></style>

</body>
</html>
