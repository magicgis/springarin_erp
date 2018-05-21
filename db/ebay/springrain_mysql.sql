DROP TABLE IF EXISTS `amazoninfo_ebay_address`;

CREATE TABLE `amazoninfo_ebay_address` (
  `id` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `street` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `street1` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `street2` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `city_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `state_or_province` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `postal_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT='自发货地址' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_ebay_order`;

CREATE TABLE `amazoninfo_ebay_order` (
  `id` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `order_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `buy_time` datetime DEFAULT NULL,
  `buyer_user` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shipping_address` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `invoice_address` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_modified_time` datetime DEFAULT NULL,
  `rate_sn` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `buyer_user_email` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_total` decimal(11,2) DEFAULT NULL,
  `payment_method` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `paid_time` datetime DEFAULT NULL,
  `order_type` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shipped_time` datetime DEFAULT NULL,
  `shipping_service_cost` decimal(11,2) DEFAULT NULL,
  `package_id` int(11) DEFAULT NULL,
  `event_id` int(11) DEFAULT NULL,
   bill_no int(11) DEFAULT NULL,
   remark  varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
   supplier  varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
   track_number  varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='自发货订单' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_ebay_orderitem`;

CREATE TABLE `amazoninfo_ebay_orderitem` (
  `id` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `sku` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_id` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `quantity_purchased` int(11) DEFAULT NULL,
  `quantity_shipped` int(11) DEFAULT NULL,
  `item_tax` decimal(11,2) DEFAULT NULL,
  `item_price` decimal(11,2) DEFAULT NULL,
  `cod_fee` decimal(11,2) DEFAULT NULL,
   asin   varchar(255)  COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='自发货订单item' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_ebay_package`;

CREATE TABLE `amazoninfo_ebay_package` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `package_no` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `print_time` datetime DEFAULT NULL,
  `status` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  print_user   varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='自发货包货单' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE ebay_address;
CREATE TABLE ebay_address 
(
   id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   NAME                 VARCHAR(255)                  NULL,
   street               VARCHAR(255)                  NULL,
   street1              VARCHAR(255)                  NULL,
   street2              VARCHAR(255)                  NULL,
   city_name            VARCHAR(255)                  NULL,
   county               VARCHAR(255)                  NULL,
   state_or_province    VARCHAR(255)                  NULL,
   country_code         VARCHAR(255)                  NULL,
   postal_code          VARCHAR(255)                  NULL,
   phone                VARCHAR(255)                  NULL,
   order_id             VARCHAR(255)                  NULL,
   PRIMARY KEY (id)
)COMMENT = 'ebay地址' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE ebay_orderitem;
CREATE TABLE ebay_orderitem 
(
   id                   INT(11)                       NOT NULL AUTO_INCREMENT,
   sku                  VARCHAR(255)                  NULL,
   title                VARCHAR(255)                  NULL,
   item_id              VARCHAR(255)                  NULL,
   taxes                DECIMAL(11,2)                  NULL,
   order_id             VARCHAR(255)                  NULL,
   transaction_id       VARCHAR(255)                  NULL,
   transaction_price    DECIMAL(11,2)                  NULL,
   vat_percent          DECIMAL(11,2)                  NULL,
   paid_time            DATETIME                       NULL,
   shipped_time         DATETIME                       NULL,
   paypal_email_address VARCHAR(255)                  NULL,
   paisapay_id          VARCHAR(255)                  NULL,
   invoice_sent_time    DATETIME                       NULL,
   quantity_purchased   INT(11)                        NULL,
   comment_text			VARCHAR(255)					NULL,
   comment_type			VARCHAR(255)					NULL,
   target_user			VARCHAR(255)				NULL,
   final_value_fee		DECIMAL(11,2)				null,
   email 				varchar(255) 				NULL,
   sellingmanagersalesrecord_number int(11)  		NULL,
   orderlineitem_id 	varchar(255) 			 	NULL,
   PRIMARY KEY (id)
)COMMENT = 'ebay订单item' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE ebay_order;
CREATE TABLE ebay_order 
(
   id                   INT(20)                       NOT NULL AUTO_INCREMENT,
   order_id             VARCHAR(255)                  NULL,
   order_status         VARCHAR(255)                  NULL,
   checkout_status      VARCHAR(255)                  NULL,
   created_time         VARCHAR(255)                  NULL,
   payment_methods      VARCHAR(255)                  NULL,
   seller_email         VARCHAR(255)                  NULL,
   total                DECIMAL(11,2)                 NULL,
   buyer_user_id        VARCHAR(255)                  NULL,
   paid_time            VARCHAR(255)                  NULL,
   shipped_time         VARCHAR(255)                  NULL,
   shipping_address		VARCHAR(255) 			NULL,
   invoice_address		VARCHAR(255) 			NULL,
   shipping_service_cost DECIMAL(11,2)			NULL,
   last_modified_time   DATETIME			NULL,
   status               CHAR(1)                        NULL,
   rate_sn				varchar(100)		null,
   adjustment_amount 	decimal(11,2)  NULL,
   amount_paid 			decimal(11,2)  NULL,
   amount_saved 		decimal(11,2)  NULL,
   payment_status 		varchar(50)  NULL,
   subtotal 			decimal(11,2)  NULL,
   shippinginsurance_cost decimal(11,2)  NULL,
   shipping_service 	varchar(255)  NULL,
   externaltransaction_id varchar(255)  NULL,
   externaltransaction_time datetime  NULL,
   feeorcredit_amount 	decimal(11,2)  NULL,
   paymentorrefund_amount decimal(11,2) NULL,
   PRIMARY KEY (id)
)COMMENT = 'ebay订单' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `ebay_product_price`;
CREATE TABLE `ebay_product_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `product_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '新品型号',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tran_fee` decimal(11,2) DEFAULT NULL,
  `safe_price` decimal(11,2) DEFAULT NULL,
  `tran_gw` decimal(11,2) DEFAULT NULL,
  `update_date` date DEFAULT NULL,
  `sky_fee` decimal(11,2) DEFAULT NULL,
  `sea_fee` decimal(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = 'ebay价格' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE `ebay_product_profit` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `product_name` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '新品型号',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `day` date DEFAULT NULL,
  `sales_volume` int(11) DEFAULT NULL,
  `sales` decimal(11,2) DEFAULT NULL,
  `sales_no_tax` decimal(11,2) DEFAULT NULL,
  `transport_fee` decimal(11,2) DEFAULT NULL,
  `buy_cost` decimal(11,2) DEFAULT NULL,
  `ebay_fee` decimal(11,2) DEFAULT NULL,
  `profits` decimal(11,2) DEFAULT NULL,
  `price` decimal(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = 'ebay利润' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE EVENT IF NOT EXISTS eventJob2    
ON SCHEDULE EVERY 30 MINUTE  
ON COMPLETION PRESERVE ENABLE  
DO CALL p_mfn_amazon_ebay();  
ALTER EVENT eventJob2 ON  COMPLETION PRESERVE ENABLE; 
