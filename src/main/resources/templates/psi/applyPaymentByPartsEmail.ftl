<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd ">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<style>
<!--
	
	.comTd{
		border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt;text-align:center
	}
	.comSpan{
		 font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;color: #333333;text-align:center
	}
	.headSpan{
		 font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot; color: #333333;text-align:center
	}
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
				<p class="MsoNormal" style="margin-bottom: 12.0pt">
					<span lang="EN-US"><br></span>${payment.createUser.name}申请支付<span lang="EN-US">${payment.supplier.name}</span>账户配件订单货款<span
						lang="EN-US" style="color: red">${payment.currencyType} ${payment.paymentAmountTotal?string("0.##")}</span>，账户如下，<span
						lang="EN-US"><br></span>银行帐号信息<span lang="EN-US">:${payment.account}<br/></span><span
						style="color: blue">备注：${payment.remark}</span>
				</p>
					<table  border="0" cellspacing="0"	cellpadding="0" style="border-collapse: collapse;width:600px" >
					<tbody>
						<tr>
							<td colspan="5" class="comTd" style="background: #DEDEDE">
									<b><span style="font-size: 8.5pt; color: #333333" >付款清单</span></b></b>
							</td>
						</tr>
						<tr>
							<td	class="comTd">	<span class="headSpan">序号</span>		</td>
							<td	class="comTd">	<span class="headSpan">付款类型</span>		</td>
							<td	class="comTd">	<span class="headSpan">单号</span>	    </td>
							<td	class="comTd">	<span class="headSpan">金额</span>		</td>
							<td	class="comTd">	<span class="headSpan">备注</span>		</td>
						</tr>
						<#list payment.items as bItem>
						<tr>
							<td class="comTd">	<span lang="EN-US"	class="comSpan">${bItem_index+1}</span>		    </td>
							<td class="comTd">	<span lang="EN-US"	class="comSpan">${(bItem.paymentType=='0')?string('定金','尾款')}</span></td>
							<td class="comTd">	<span lang="EN-US"	class="comSpan"> ${bItem.billNo}</span>	</td>
							<td class="comTd">	<span lang="EN-US"	class="comSpan">${bItem.paymentAmount}</span>	</td>
							<td class="comTd">  <span lang="EN-US"	class="comSpan">${bItem.remark}</span>	        </td>
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
