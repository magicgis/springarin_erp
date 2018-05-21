DELIMITER $$

USE `erp_utf`$$

DROP PROCEDURE IF EXISTS `p_mfn_amazon_ebay`$$

CREATE  PROCEDURE `p_mfn_amazon_ebay`()
BEGIN
INSERT INTO amazoninfo_ebay_order (id,order_id,STATUS,buy_time,buyer_user,shipping_address,invoice_address,last_modified_time,rate_sn,country,buyer_user_email,order_total,payment_method,paid_time,order_type,shipped_time,shipping_service_cost)
SELECT CONCAT(id,'_','amazon') id,amazon_order_id,'0' STATUS,purchase_date,buyer_name,
CONCAT(shipping_address,'_','amazon') shipping_address,(CASE WHEN invoice_address!=NULL THEN CONCAT(invoice_address,'_','amazon')  ELSE NULL  END ) invoice_address
,last_update_date last_modified_time,rate_sn,SUBSTRING_INDEX(sales_channel,'.',-1) sales_channel,buyer_email,order_total,payment_method,purchase_date paid_time,'0',earliest_ship_date shipped_time,
(SELECT SUM(shipping_price) FROM amazoninfo_orderitem t WHERE t.order_id=d.id ) shipping_service_cost
 FROM amazoninfo_order d WHERE d.order_status='Unshipped' AND fulfillment_channel='MFN'
 AND d.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -2 MONTH)
 UNION 
 SELECT CONCAT(id,'_','ebay') id,order_id,'0' STATUS,created_time,buyer_user_id,
CONCAT(shipping_address,'_','ebay') shipping_address,(CASE WHEN invoice_address!=NULL THEN CONCAT(invoice_address,'_','ebay')  ELSE NULL  END ) invoice_address,
 last_modified_time,rate_sn,'de',(SELECT t.email FROM ebay_orderitem t WHERE t.order_id=r.id LIMIT 1 ) buyer_email,total,payment_methods,paid_time,'0',shipped_time,shipping_service_cost
 FROM ebay_order r WHERE STATUS='1' AND r.created_time>=DATE_ADD(CURRENT_DATE(),INTERVAL -2 MONTH)
ON DUPLICATE KEY UPDATE `last_modified_time` = VALUES(last_modified_time),`paid_time` =VALUES(paid_time),`shipped_time` = VALUES(shipped_time),shipping_service_cost= VALUES(shipping_service_cost);

INSERT INTO amazoninfo_ebay_orderitem(id,sku,title,order_id,quantity_purchased,quantity_shipped,item_tax,item_price,cod_fee)
SELECT CONCAT(t.id,'_','amazon') id,t.sellersku,
(CASE WHEN (s.NAME='Inateck other' OR s.NAME='Inateck Old' OR s.name='' OR s.name IS NULL) THEN t.title ELSE s.name END) title,
CONCAT(t.order_id,'_','amazon') order_id,t.quantity_ordered,t.quantity_ordered,0,t.item_price,0
FROM amazoninfo_orderitem t
JOIN amazoninfo_ebay_order  r ON CONCAT(t.order_id,'_','amazon')=r.id
LEFT JOIN (SELECT DISTINCT ASIN,country,CONCAT(product_name,CASE  WHEN color='' THEN '' ELSE CONCAT('_',color) END) NAME FROM psi_sku 
WHERE del_flag='0' AND product_name NOT LIKE '%Inateck other%' AND product_name NOT LIKE '%Inateck Old%' )s ON s.asin=t.asin AND s.country=r.country
WHERE CONCAT(t.order_id,'_','amazon') IN(SELECT id FROM amazoninfo_ebay_order WHERE SUBSTRING_INDEX(id,'_',-1)='amazon')
UNION 
SELECT p.id,p.sku,(CASE WHEN (NAME='Inateck other' OR NAME='Inateck Old') THEN p.title ELSE p.name END),p.order_id,p.quantity_purchased,p.quantity_purchased,0,transaction_price,0
 FROM (SELECT CONCAT(id,'_','ebay') id,sku,
IFNULL((SELECT DISTINCT CONCAT(product_name,CASE  WHEN color='' THEN '' ELSE CONCAT('_',color) END) NAME FROM psi_sku s WHERE s.sku=t.sku 
AND s.del_flag='0' AND product_name NOT LIKE '%Inateck other%' AND product_name NOT LIKE '%Inateck Old%' LIMIT 1),title) NAME,title,
CONCAT(order_id,'_','ebay') order_id,quantity_purchased,transaction_price
FROM ebay_orderitem t) p WHERE p.order_id IN (SELECT id FROM amazoninfo_ebay_order WHERE SUBSTRING_INDEX(id,'_',-1)='ebay')
ON DUPLICATE KEY UPDATE `quantity_purchased` = VALUES(quantity_purchased),`quantity_shipped` = VALUES(quantity_shipped);

INSERT INTO amazoninfo_ebay_address(id,NAME,street,street1,street2,city_name,country,state_or_province,country_code,postal_code,phone,order_id)
SELECT CONCAT(id,'_','amazon') id,NAME,address_line1,address_line2,address_line3,city,county,state_or_region,country_code,postal_code,phone,CONCAT(order_id,'_','amazon') order_id
FROM amazoninfo_address WHERE CONCAT(id,'_','amazon') IN(SELECT shipping_address FROM amazoninfo_ebay_order WHERE SUBSTRING_INDEX(id,'_',-1)='amazon') 
UNION
SELECT CONCAT(id,'_','amazon') id,NAME,address_line1,address_line2,address_line3,city,county,state_or_region,country_code,postal_code,phone,CONCAT(order_id,'_','amazon') order_id
FROM amazoninfo_address WHERE CONCAT(id,'_','amazon') IN(SELECT invoice_address FROM amazoninfo_ebay_order WHERE  invoice_address IS NOT NULL AND SUBSTRING_INDEX(id,'_',-1)='amazon') 
UNION
SELECT CONCAT(id,'_','ebay') id,NAME,street,street1,street2,city_name,county,state_or_province,country_code,postal_code,phone,CONCAT(order_id,'_','ebay') order_id
FROM ebay_address WHERE CONCAT(id,'_','ebay') IN(SELECT shipping_address FROM amazoninfo_ebay_order WHERE SUBSTRING_INDEX(id,'_',-1)='ebay') 
OR CONCAT(id,'_','ebay') IN(SELECT invoice_address FROM amazoninfo_ebay_order WHERE invoice_address IS NOT NULL AND SUBSTRING_INDEX(id,'_',-1)='ebay')
ON DUPLICATE KEY UPDATE `street` = VALUES(street),`street1` = VALUES(street1),phone=VALUES(phone);
INSERT INTO amazoninfo_ebay_order (id,STATUS,last_modified_time,shipped_time)
SELECT CONCAT(r.id,'_','amazon') id,(CASE WHEN r.order_status='Shipped' THEN '1' ELSE '9' END) STATUS,r.last_update_date last_modified_time,r.earliest_ship_date shipped_time
 FROM amazoninfo_order r WHERE r.id IN(SELECT SUBSTRING_INDEX(o.`id`,'_',1) FROM amazoninfo_ebay_order o 
WHERE  o.`status`='0' AND SUBSTRING_INDEX(o.`id`,'_',-1)='amazon') AND r.order_status !='Unshipped'
UNION 
SELECT CONCAT(r.id,'_','ebay') id,(CASE WHEN r.status='2' THEN '1' ELSE '9' END) STATUS,r.last_modified_time,r.shipped_time
 FROM ebay_order r WHERE r.id IN(SELECT SUBSTRING_INDEX(o.`id`,'_',1) FROM amazoninfo_ebay_order o 
WHERE  o.`status`='0' AND SUBSTRING_INDEX(o.`id`,'_',-1)='ebay') AND r.status !='1'
ON DUPLICATE KEY UPDATE `last_modified_time` = VALUES(last_modified_time),`shipped_time` = VALUES(shipped_time),STATUS= VALUES(STATUS);
    END$$

DELIMITER ;
  