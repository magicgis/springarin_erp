SET SESSION FOREIGN_KEY_CHECKS=0;


DROP TABLE IF EXISTS `amazoninfo_posts_relationship_change`;

CREATE TABLE `amazoninfo_posts_relationship_change` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_sku` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sku` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `size` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='亚马逊帖子关系改变详情' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_posts_relationship_feed`;

CREATE TABLE `amazoninfo_posts_relationship_feed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `result` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `result_file` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `country` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `request_id` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `parent_sku` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `operat` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
   `check_user` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `check_date`  DATE  COLLATE utf8_unicode_ci DEFAULT NULL,

  PRIMARY KEY (`id`)
) COMMENT='亚马逊帖子关系' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_posts_feed`;
CREATE TABLE `amazoninfo_posts_feed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `result` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `result_file` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `country` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `request_id` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
   operate_type char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='亚马逊帖子' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_posts_change`;
CREATE TABLE `amazoninfo_posts_change` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ASIN` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `binding` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `brand` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `label` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `manufacturer` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `publisher` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `studio` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `title` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `package_quantity` int(11) DEFAULT NULL,
  `package_height` decimal(11,2) DEFAULT NULL,
  `package_length` decimal(11,2) DEFAULT NULL,
  `package_width` decimal(11,2) DEFAULT NULL,
  `package_weight` decimal(11,2) DEFAULT NULL,
  `product_group` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_type_name` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `feature1` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `feature2` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `feature3` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `feature4` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `feature5` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `part_number` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `size` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword1` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword2` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword3` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword4` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword5` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `feed_id` int(11) DEFAULT NULL,
  `ean` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sku` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `catalog1` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `catalog2` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
   price decimal(11,2) DEFAULT NULL,
   sale_price decimal(11,2) DEFAULT NULL,
   variation_theme varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
   cross_sku varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
   is_fba char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
   reason varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
   cross_country  varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
   quantity int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='亚马逊帖子改变详情' CHARSET utf8 COLLATE utf8_unicode_ci;






DROP TABLE IF EXISTS `amazoninfo_refund`;

CREATE TABLE `amazoninfo_refund` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `result` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `result_file` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `refund_total` decimal(11,2) DEFAULT NULL,
  `amazon_order_id` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  request_id varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  refund_state char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  oper_user varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  is_tax char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='亚马逊订单退款' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_refund_item`;

CREATE TABLE `amazoninfo_refund_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_item_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sku` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `money` decimal(11,2) DEFAULT NULL,
  `refund_type` varchar(100) DEFAULT NULL,
  `refund_reason` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  refund_id  int(11) DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='亚马逊订单退款明细' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS amazoninfo_catalog_rank;
CREATE TABLE amazoninfo_catalog_rank (
  id INT(11) NOT NULL AUTO_INCREMENT,
  ASIN VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  country VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  product_name VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  query_time DATETIME DEFAULT NULL,
  catalog  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL, 
  catalog_name  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL, 
  rank INT(11) DEFAULT NULL,
  path VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  ports_id INT(11) DEFAULT NULL,
  path_name VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (id)
) COMMENT='亚马逊帖子目录排行' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS amazoninfo_posts_detail;
CREATE TABLE amazoninfo_posts_detail (
  id INT(11) NOT NULL AUTO_INCREMENT,
  ASIN VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  country VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  product_name VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  query_time DATETIME DEFAULT NULL,
  binding VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  brand VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  label VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,  
  manufacturer   VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,  
  publisher VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL, 
  studio VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL, 
  title  VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  package_quantity   INT(11) DEFAULT NULL,
  package_height DECIMAL(11,2) DEFAULT NULL,
  package_length DECIMAL(11,2) DEFAULT NULL,
  package_width DECIMAL(11,2) DEFAULT NULL,
  package_weight DECIMAL(11,2) DEFAULT NULL,
  product_group  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  product_type_name VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  feature1  VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL, 
  feature2  VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL, 
  feature3  VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL, 
  feature4  VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL, 
  feature5  VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL, 
  by_size   char(1) DEFAULT NULL,
  by_color  char(1) DEFAULT NULL,
  parent_id  INT(11) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  part_number  VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  size VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  color VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  ean  VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  sku  VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  keyword1 VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  keyword2 VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  keyword3 VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL, 
  keyword4 VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  keyword5 VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  description  VARCHAR(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  picture1  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  picture2  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  picture3  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  picture4  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  picture5  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  picture6  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  picture7  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  picture8  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  picture9  VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  init_path  VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,        
  PRIMARY KEY (id)
) COMMENT='亚马逊帖子详细信息' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_fba_health_report`;

CREATE TABLE `amazoninfo_fba_health_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `snapshot_date` datetime DEFAULT NULL,
  `sku` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fnsku` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `condition` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sales_rank` int(11) DEFAULT NULL,
  `product_group` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `total_quantity` int(11) DEFAULT NULL,
  `sellable_quantity` int(11) DEFAULT NULL,
  `unsellable_quantity` int(11) DEFAULT NULL,
  `age_days90` int(11) DEFAULT NULL,
  `age_days180` int(11) DEFAULT NULL,
  `age_days270` int(11) DEFAULT NULL,
  `age_days365` int(11) DEFAULT NULL,
  `age_plus_days365` int(11) DEFAULT NULL,
  `shipped_hrs24` int(11) DEFAULT NULL,
  `shipped_days7` int(11) DEFAULT NULL,
  `shipped_days30` int(11) DEFAULT NULL,
  `shipped_days90` int(11) DEFAULT NULL,
  `shipped_days180` int(11) DEFAULT NULL,
  `shipped_days365` int(11) DEFAULT NULL,
  `weeks_cover7` decimal(11,2) DEFAULT NULL,
  `weeks_cover30` decimal(11,2) DEFAULT NULL,
  `weeks_cover90` decimal(11,2) DEFAULT NULL,
  `weeks_cover180` decimal(11,2) DEFAULT NULL,
  `weeks_cover365` decimal(11,2) DEFAULT NULL,
  `afn_new_sellers` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `afn_used_sellers` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `your_price` decimal(11,2) DEFAULT NULL,
  `sales_price` decimal(11,2) DEFAULT NULL,
  `afn_new_price` decimal(11,2) DEFAULT NULL,
  `afn_used_price` decimal(11,2) DEFAULT NULL,
  `mfn_new_price` decimal(11,2) DEFAULT NULL,
  `mfn_used_price` decimal(11,2) DEFAULT NULL,
  `qty_charged` int(11) DEFAULT NULL,
  `qty_long_term_storage` int(11) DEFAULT NULL,
  `qty_with_removals` int(11) DEFAULT NULL,
  `projected_mo12` decimal(11,2) DEFAULT NULL,
  `per_unit_volume` decimal(11,2) DEFAULT NULL,
  `is_hazmat` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `in_bound_quantity` int(11) DEFAULT NULL,
  `asin_limit` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `inbound_recommend_quantity` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT='FBA健康报表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_enterprise_goal`;

CREATE TABLE `amazoninfo_enterprise_goal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `month` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `goal` decimal(11,2) DEFAULT NULL,
  `profit_goal` decimal(11,2) DEFAULT NULL,
  create_date datetime default null,
  create_user varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  product_line  varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='产品线月目标' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_enterprise_total_goal`;

CREATE TABLE `amazoninfo_enterprise_total_goal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `month` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `goal` decimal(11,2) DEFAULT NULL,
  `profit_goal` decimal(11,2) DEFAULT NULL,
  create_date datetime default null,
  create_user varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='国家月目标' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_enterprise_type_goal`;

CREATE TABLE `amazoninfo_enterprise_type_goal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `month` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '月份',
  `product_type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品类型',
  `line` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品线',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `sales_goal` decimal(11,2) DEFAULT NULL COMMENT '销售额目标',
  `profit_goal` decimal(11,2) DEFAULT NULL COMMENT '利润目标',
  PRIMARY KEY (`id`)
) COMMENT='分产品类型月目标' CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_enterprise_week`;

CREATE TABLE `amazoninfo_enterprise_week` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `week` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `monday` decimal(11,2) DEFAULT NULL,
  `tuesday` decimal(11,2) DEFAULT NULL,
  `wednesday` decimal(11,2) DEFAULT NULL,
  `thursday` decimal(11,2) DEFAULT NULL,
  `friday` decimal(11,2) DEFAULT NULL,
  `saturday` decimal(11,2) DEFAULT NULL,
  `sunday` decimal(11,2) DEFAULT NULL,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='企业权重' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_enterprise_weight`;

CREATE TABLE `amazoninfo_enterprise_weight` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `monday` decimal(5,2) DEFAULT NULL,
  `tuesday` decimal(5,2) DEFAULT NULL,
  `wednesday` decimal(5,2) DEFAULT NULL,
  `thursday` decimal(5,2) DEFAULT NULL,
  `friday` decimal(5,2) DEFAULT NULL,
  `saturday` decimal(5,2) DEFAULT NULL,
  `sunday` decimal(5,2) DEFAULT NULL,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `flag` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='站点销售数据' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_unline_address`;

CREATE TABLE `amazoninfo_unline_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address_line1` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address_line2` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address_line3` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `city` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `county` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `district` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `state_or_region` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `postal_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='线下订单联系地址'CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_unline_order`;

CREATE TABLE `amazoninfo_unline_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `create_date` datetime DEFAULT NULL,
  `amazon_order_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `seller_order_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `purchase_date` datetime DEFAULT NULL,
  `last_update_date` datetime DEFAULT NULL,
  `order_status` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fulfillment_channel` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sales_channel` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_channel` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ship_service_level` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shipping_address` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `invoice_address` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `invoice_flag` char(5) COLLATE utf8_unicode_ci DEFAULT '000',
  `order_total` decimal(11,2) DEFAULT NULL,
  `number_of_items_shipped` int(11) DEFAULT NULL,
  `number_of_items_unshipped` int(11) DEFAULT NULL,
  `payment_method` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `marketplace_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `buyer_email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `buyer_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shipment_service_level_category` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cba_displayable_shipping_label` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `earliest_ship_date` datetime DEFAULT NULL,
  `latest_ship_date` datetime DEFAULT NULL,
  `earliest_delivery_date` datetime DEFAULT NULL,
  `latest_delivery_date` datetime DEFAULT NULL,
  `rate_sn` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `comment_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `custom_id` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `out_bound` varchar(100) COLLATE utf8_unicode_ci DEFAULT '0',
  `supplier` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `bill_no` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  cancel_date datetime default null,
  cancel_user varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  origin char(1)  COLLATE utf8_unicode_ci DEFAULT NULL,
  out_bound_no varchar(300)  COLLATE utf8_unicode_ci DEFAULT NULL,
   `invoice_no` VARCHAR(50)  COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderID` (`amazon_order_id`)
) COMMENT='线下订单'CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_unline_orderitem`;

CREATE TABLE `amazoninfo_unline_orderitem` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `asin` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sellersku` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_item_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `title` varchar(1500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `quantity_ordered` int(11) DEFAULT NULL,
  `quantity_shipped` int(11) DEFAULT NULL,
  `item_price` decimal(11,2) DEFAULT NULL,
  `item_tax` decimal(11,2) DEFAULT NULL,
  `shipping_price` decimal(11,2) DEFAULT NULL,
  `gift_wrap_price` decimal(11,2) DEFAULT NULL,
  `shipping_tax` decimal(11,2) DEFAULT NULL,
  `gift_wrap_tax` decimal(11,2) DEFAULT NULL,
  `shipping_discount` decimal(11,2) DEFAULT NULL,
  `promotion_discount` decimal(11,2) DEFAULT NULL,
  `promotion_ids` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cod_fee` decimal(11,2) DEFAULT NULL,
  `cod_fee_discount` decimal(11,2) DEFAULT NULL,
  `gift_message_text` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `gift_wrap_level` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `condition_note` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `condition_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `condition_subtype_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `scheduled_delivery_start_date` datetime DEFAULT NULL,
  `scheduled_delivery_end_date` datetime DEFAULT NULL,
  `order_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
   `quantity_out` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT='线下订单商品信息' CHARSET utf8 COLLATE utf8_unicode_ci;



/* Drop Tables */

DROP TABLE amazoninfo_business_report;

CREATE TABLE amazoninfo_business_report
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	country varchar(20),
	data_date datetime,
	create_date datetime,
	p_asin varchar(100),	
	child_asin varchar(100),
	title	varchar(1000),
	sessions int	,
	session_percentage DECIMAL(5,2),
	page_views int,
	page_views_percentage DECIMAL(5,2),
	buy_box_percentage int,
	units_ordered	int,
	unit_session_percentage DECIMAL(5,2),
	gross_product_sales DECIMAL(11,2),
	orders_placed int ,
	conversion DECIMAL(5,2),
	b2b_units_ordered int	,
	b2b_unit_session_percentage  DECIMAL(5,2),
	b2b_ordered_product_sales DECIMAL(11,2),
	b2b_orders_placed int	,
	
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
    account_name VARCHAR(50) DEFAULT NULL,
	PRIMARY KEY (id)
) COMMENT = '亚马逊商业报表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE amazoninfo_product;

CREATE TABLE amazoninfo_product
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	create_date datetime,
	name varchar(500),
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	active char(1) DEFAULT '1' NOT NULL COMMENT '激活标记',
	ean varchar(200),
	parent_asin varchar(200),
	sku varchar(200),
	asin varchar(200),
	country varchar(20),
	PRIMARY KEY (id)
) COMMENT = '亚马逊产品' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE amazoninfo_product2
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	update_date datetime,
	active char(1) DEFAULT '1' NOT NULL COMMENT '激活标记',
	ean varchar(200),
	sku varchar(200),
	asin varchar(200),
	fnsku varchar(200),
	country varchar(20),
	price DECIMAL(11,2),
	sale_price DECIMAL(11,2),
	high_warn_price decimal(11,2) DEFAULT NULL,
	warn_price decimal(11,2) DEFAULT NULL,
    warn_price_by_user varchar(50),
   	last_warn_price_update datetime DEFAULT NULL,
   	is_fba char(1),
   	quantity int,
   	open_date date,
   	open_cycle int,
  `business_price` DECIMAL(11,2) NULL  COMMENT 'B2B价格',
  `quantity1` INT(11) NULL  COMMENT 'B2B阶梯数量1',
  `price1` DECIMAL(11,2) NULL  COMMENT 'B2B阶梯价格1',
  `quantity2` INT(11) NULL  COMMENT 'B2B阶梯数量2',
  `price2` DECIMAL(11,2) NULL  COMMENT 'B2B阶梯价格2',
  `quantity3` INT(11) NULL  COMMENT 'B2B阶梯数量3',
  `price3` DECIMAL(11,2) NULL  COMMENT 'B2B阶梯价格3',
  `quantity4` INT(11) NULL  COMMENT 'B2B阶梯数量4',
  `price4` DECIMAL(11,2) NULL  COMMENT 'B2B阶梯价格4',
  `quantity5` INT(11) NULL  COMMENT 'B2B阶梯数量5',
  `price5` DECIMAL(11,2) NULL  COMMENT 'B2B阶梯价格5'
	PRIMARY KEY (id)
) COMMENT = '亚马逊产品2' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE amazoninfo_address;

CREATE TABLE amazoninfo_address
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	
    name varchar(255),

    address_line1 varchar(255),

    address_line2 varchar(255),

    address_line3 varchar(255),

    city varchar(255),

    county varchar(255),

    district varchar(255),

    state_or_region varchar(255),

    postal_code varchar(255),

    country_code varchar(255),

    phone varchar(255),
    
    order_id varchar(255),
	
	PRIMARY KEY (id)
) COMMENT = '亚马逊订单联系地址' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE amazoninfo_orderitem;
CREATE TABLE amazoninfo_orderitem
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	
    asin varchar(255),

    sellersku varchar(255),

    order_item_id varchar(255),

    title varchar(255),

    quantity_ordered int,

    quantity_shipped int,

    item_price DECIMAL(11,2),
    
    item_tax DECIMAL(11,2),

    shipping_price DECIMAL(11,2),

    gift_wrap_price DECIMAL(11,2),

    shipping_tax DECIMAL(11,2),

    gift_wrap_tax DECIMAL(11,2),

    shipping_discount DECIMAL(11,2),

    promotion_discount DECIMAL(11,2),

    promotion_ids varchar(1000),

    cod_fee DECIMAL(11,2),

    cod_fee_discount DECIMAL(11,2),

    gift_message_text varchar(255),

    gift_wrap_level varchar(255),

    condition_note varchar(255),

    condition_id varchar(255),

    condition_subtype_id varchar(255),

    scheduled_delivery_start_date datetime,

    scheduled_delivery_end_date datetime,
       
    order_id varchar(255),
    
    color varchar(255),

    product_name varchar(255),
	promotion_code varchar(50),
	buyer_customized_info varchar(50),
	price_designation varchar(50),
	PRIMARY KEY (id)
) COMMENT = '亚马逊订单商品信息' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE amazoninfo_order;

CREATE TABLE amazoninfo_order
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	
	create_date datetime,
	
	amazon_order_id varchar(255),

    seller_order_id varchar(255),

    purchase_date datetime,

    last_update_date datetime,

    order_status varchar(255),

    fulfillment_channel varchar(255),

    sales_channel varchar(255),

    order_channel varchar(255),

    ship_service_level varchar(255),

    shipping_address varchar(255),
    
    invoice_flag varchar(2),
    
    invoice_address varchar(255),

    order_total DECIMAL(11,2),

    number_of_items_shipped int,

    number_of_items_unshipped int,

    payment_method varchar(255),

    marketplace_id varchar(255),

    buyer_email varchar(255),

    buyer_name varchar(255),

    shipment_service_level_category varchar(255),

    cba_displayable_shipping_label varchar(255),

    order_type varchar(255),

    earliest_ship_date datetime,

    latest_ship_date datetime,

    earliest_delivery_date datetime,

    latest_delivery_date datetime,
    
    rate_sn varchar(100),

    comment_url varchar(255),
    
    is_business_order char(1),
    is_replacement_order char(1),
    is_prime char(1),
    is_premium_order char(1),
    replaced_order_id varchar(255),
    buyer_tax_info varchar(1000),
    purchase_order_number varchar(100), 
	
	PRIMARY KEY (id)
) COMMENT = '亚马逊订单' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE amazoninfo_feed_submission;

CREATE TABLE amazoninfo_feed_submission
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	feed_submission_req_id varchar(20),
	state char(1),
	excel_file varchar(100),
	country varchar(20),
	result_file varchar(500),
	result varchar(500),
	create_date datetime,
	create_by varchar(64) COMMENT '创建者',
	del_flag char(1) default '0',
	PRIMARY KEY (id)
) COMMENT = '帖子上架' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE amazoninfo_feed;

CREATE TABLE amazoninfo_feed
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	feed_submission_id int NOT NULL,
	sku varchar(64),
	ean varchar(64),
	price DECIMAL(11,2),
	sale_price DECIMAL(11,2),
	sale_start_date datetime,
	sale_end_date datetime,
	parent_child varchar(50),
	relationship_type varchar(50),
	parent_sku varchar(64),
	subject varchar(2000),
	description varchar(2000),
	bullet_point1 varchar(2000),
	bullet_point2 varchar(2000),
	bullet_point3 varchar(2000),
	bullet_point4 varchar(2000),
	bullet_point5 varchar(2000),
	generic_keywords1 varchar(1000),
	generic_keywords2 varchar(1000),
	generic_keywords3 varchar(1000),
	generic_keywords4 varchar(1000),
	generic_keywords5 varchar(1000),
	PRIMARY KEY (id)
) COMMENT = '帖子类容' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE amazoninfo_price_feed;

CREATE TABLE amazoninfo_price_feed
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	request_id varchar(20),
	state char(1),
	country varchar(20),
	result_file varchar(500),
	result varchar(500),
	request_date datetime,
	create_by varchar(64) COMMENT '创建者',
	reason varchar(255),
	PRIMARY KEY (id)
) COMMENT = '价格修改' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE amazoninfo_price;

CREATE TABLE amazoninfo_price
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	feed_price_feed_id int NOT NULL,
	sku varchar(64),
	price DECIMAL(11,2),
	sale_price DECIMAL(11,2),
	sale_start_date datetime,
	sale_end_date datetime,
  `business_price` DECIMAL(11,2) NULL  COMMENT 'B2B价格',
  `quantity_lower_bound1` INT(11) NULL  COMMENT 'B2B阶梯数量1',
  `quantity_price1` DECIMAL(11,2) NULL  COMMENT 'B2B阶梯价格1',
  `quantity_lower_bound2` INT(11) NULL  COMMENT 'B2B阶梯数量2',
  `quantity_price2` DECIMAL(11,2) NULL  COMMENT 'B2B阶梯价格2',
  `quantity_lower_bound3` INT(11) NULL  COMMENT 'B2B阶梯数量3',
  `quantity_price3` DECIMAL(11,2) NULL  COMMENT 'B2B阶梯价格3',
  `quantity_lower_bound4` INT(11) NULL  COMMENT 'B2B阶梯数量4',
  `quantity_price4` DECIMAL(11,2) NULL  COMMENT 'B2B阶梯价格4',
  `quantity_lower_bound5` INT(11) NULL  COMMENT 'B2B阶梯数量5',
  `quantity_price5` DECIMAL(11,2) NULL  COMMENT 'B2B阶梯价格5',
  `delete_b2b` CHAR(1) NULL  COMMENT '0:不删除 1：删除',
	PRIMARY KEY (id)
) COMMENT = '价格' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE amazoninfo_mfn_inventory_feed;

CREATE TABLE amazoninfo_mfn_inventory_feed
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	request_id varchar(20),
	state char(1),
	country varchar(20),
	result_file varchar(500),
	result varchar(500),
	request_date datetime,
	create_by varchar(64) COMMENT '创建者',
	PRIMARY KEY (id)
) COMMENT = '自发货库存修改' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE amazoninfo_mfn_item;

CREATE TABLE amazoninfo_mfn_item
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	mfn_inventory_id int NOT NULL,
	sku varchar(64),
	quantity int,
	PRIMARY KEY (id)
) COMMENT = '自发货库存项修改' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE settlementreport_order;
CREATE TABLE settlementreport_order 
(
   id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   amazon_order_id      VARCHAR(255)                   NULL,
   merchant_order_id    VARCHAR(255)                   NULL,
   adjustment_id		VARCHAR(255)				   NULL,	
   shipment_id          VARCHAR(255)                   NULL,
   marketplace_name     VARCHAR(255)                   NULL,
   merchant_fulfillment_id VARCHAR(255)                NULL,
   posted_date          DATETIME                       NULL,
   country				CHAR(5)						  NULL,
   add_time             DATETIME                       NULL,
   account_name     VARCHAR(50)                   NULL,
   PRIMARY KEY (id)
)COMMENT = 'SettlementReport_Order' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE settlementreport_item;
CREATE TABLE settlementreport_item 
(
   id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   amazon_order_item_code VARCHAR(255)                   NULL,
   merchant_adjustment_item_id VARCHAR(255)			   NULL,
   sku                  VARCHAR(255)                   NULL,
   quantity             INT(11)                        NULL,
   principal            DECIMAL(11,2)                  NULL,
   shipping             DECIMAL(11,2)                  NULL,
   cross_border_fulfillment_fee DECIMAL(11,2)                  NULL,
   fba_per_unit_fulfillment_fee DECIMAL(11,2)                  NULL,
   fba_weight_based_fee DECIMAL(11,2)                  NULL,
   commission           DECIMAL(11,2)                  NULL,
   shipping_chargeback  DECIMAL(11,2)                  NULL,
   giftwrap_chargeback  DECIMAL(11,2)                  NULL,
   refund_commission	DECIMAL(11,2)		       NULL,
   order_id             VARCHAR(255)                   NULL,
   add_time             DATETIME                       NULL,
   PRIMARY KEY (id)
)COMMENT = 'SettlementReport_Item' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE amazoninfo_image_feed;

CREATE TABLE amazoninfo_image_feed
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	request_id varchar(20),
	state char(1),
	country varchar(20),
	result_file varchar(500),
	result varchar(500),
	request_date datetime,
	create_by varchar(64) COMMENT '创建者',
	sku varchar(64),
	PRIMARY KEY (id)
) COMMENT = '图片修改' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE amazoninfo_image;

CREATE TABLE amazoninfo_image
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	feed_image_feed_id int NOT NULL,
	type varchar(20),
	location varchar(500),
	is_delete char(1),
	PRIMARY KEY (id)
) COMMENT = '图片' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS amazoninfo_return_goods;
CREATE TABLE amazoninfo_return_goods(
  id INT(10)  NOT NULL AUTO_INCREMENT,
  sku varchar(100) ,	 
  asin varchar(20) ,
  fnsku varchar(20) ,
  quantity int,
  country varchar(5),
  return_date  DATETIME,
  order_id varchar(100) ,
  fulfillment_center_id varchar(20) ,
  reason varchar(100) ,
  disposition varchar(100) ,
  PRIMARY KEY (id)              
)COMMENT = '退货信息表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS amazoninfo_amazon_account;
CREATE TABLE amazoninfo_amazon_account(
  id INT(10)  NOT NULL AUTO_INCREMENT,
  country varchar(20),
  account_email varchar(200),
  account_id varchar(100),
  last_update_user varchar(64),
  create_user VARCHAR(64),
  last_update_date datetime,
  code varchar(20),
  statu varchar(100),
  right varchar(3000),
  del_flag char(1),
  PRIMARY KEY (id)              
)COMMENT = '退货信息表' CHARSET utf8 COLLATE utf8_unicode_ci;
	

CREATE TABLE amazoninfo_session_monitor(
  id INT(10)  NOT NULL AUTO_INCREMENT,
  country varchar(20),
  month datetime,
  product_id int(10), 
  product_name VARCHAR(200),
  color VARCHAR(100),
  sessions int(10),
  sessions_by_date int(10),
  conver DECIMAL(11,2),
  last_update_user varchar(64),
  create_user VARCHAR(64),
  create_date datetime,
  last_update_date datetime,
  PRIMARY KEY (id)              
)COMMENT = 'session监控表' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE amazoninfo_sales_forecast(
  id INT(10)  NOT NULL AUTO_INCREMENT,
  country varchar(20),
  product_name VARCHAR(200),
  last_update_by varchar(64),
  create_by VARCHAR(64),
  last_update_date datetime,
  data_date datetime,
  quantity_forecast int(10),
  del_flag char(1),
  PRIMARY KEY (id)              
)COMMENT = '销售预测表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE amazoninfo_sales_forecast_month(
  id INT(10)  NOT NULL AUTO_INCREMENT,
  country varchar(20),
  product_name VARCHAR(200),
  product_id int,
  last_update_by varchar(64),
  create_by VARCHAR(64),
  last_update_date datetime,
  data_date datetime,
  quantity_forecast int(10),
  PRIMARY KEY (id)              
)COMMENT = '销售按月预测表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE amazoninfo_advertising(
	id INT(10)  NOT NULL AUTO_INCREMENT,
	country varchar(20),
	name VARCHAR(1000),
	group_name VARCHAR(1000),
	sku VARCHAR(100),
	type varchar(100),
	keyword VARCHAR(1000),
	data_date datetime,
	create_date datetime,
	impressions int,
	clicks      int,
	conversion DECIMAL(11,2),
	total_spend DECIMAL(11,2),
	same_sku_order_sales DECIMAL(11,2),
	other_sku_order_sales DECIMAL(11,2),
	same_sku_orders_placed int,
	other_sku_orders_placed int,
	max_cpc_bid DECIMAL(11,2),
	one_page_bid DECIMAL(11,2),
	day_same_sku_units_ordered int,
	week_same_sku_units_ordered int,
	month_same_sku_units_ordered int,
	week_same_sku_units_sales DECIMAL(11,2),
	
	PRIMARY KEY (id)             
)COMMENT = '广告报表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE amazoninfo_advertising_week(
	id INT(10)  NOT NULL AUTO_INCREMENT,
	country varchar(20),
	name VARCHAR(200),
	group_name VARCHAR(200),
	sku VARCHAR(100),
	keyword VARCHAR(200),
	week varchar(6),
	type varchar(100),
	update_date date,
	impressions int,
	clicks      int,
	total_spend DECIMAL(11,2),
	
	week_same_sku_units_sales DECIMAL(11,2),
	week_same_sku_units_ordered int,
	week_same_sku_units_lirun DECIMAL(11,2),
	
	week_other_sku_units_sales DECIMAL(11,2),
	week_other_sku_units_ordered int,
	week_other_sku_units_lirun DECIMAL(11,2),
	
	week_parent_sku_units_sales DECIMAL(11,2),
	week_parent_sku_units_ordered int,
	week_parent_sku_units_lirun DECIMAL(11,2),
	UNIQUE KEY `unique` (`country`,`name`,`sku`,group_name,keyword,week,type),
	PRIMARY KEY (id)             
)COMMENT = '广告报表' CHARSET utf8 COLLATE utf8_unicode_ci;



CREATE TABLE amazoninfo_product_history_price
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	data_date datetime,
	sku varchar(200),
	country varchar(20),
	sale_price DECIMAL(11,2),
	PRIMARY KEY (id)
) COMMENT = '产品历史价格' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_sale_report`;
CREATE TABLE `amazoninfo_sale_report` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date` DATETIME DEFAULT NULL,
  `product_name` VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sales_volume` INT(11) DEFAULT NULL,
  `sales` DECIMAL(11,2) DEFAULT NULL,
  `sure_sales_volume` INT(11) DEFAULT NULL,
  `sure_sales` DECIMAL(11,2) DEFAULT NULL,
  `sku` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `real_sales_volume` INT(11) DEFAULT NULL,
  `real_sales` DECIMAL(11,2) DEFAULT NULL,
  order_type  char(1),
  max_order INT(11) DEFAULT NULL,
  promotions_order INT(11) DEFAULT NULL,
  flash_sales_order INT(11) DEFAULT NULL,
  price DECIMAL(11,2) DEFAULT NULL COMMENT '保本价格',
  return_num INTEGER DEFAULT NULL COMMENT '退货数量',
  ads_order INT(11) DEFAULT NULL,
  ams_order INT(11) DEFAULT NULL,
  outside_order INT(11) DEFAULT NULL,
  real_order INT(11) DEFAULT NULL,
  business_order int(11)  DEFAULT NULL,
  `product_attr` VARCHAR(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  pack_num INT(11) DEFAULT NULL COMMENT 'sku对应的产品数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`country`,`date`,`sku`,order_type)
) COMMENT='产品销量和销售额' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_sale_report_type`;
CREATE TABLE `amazoninfo_sale_report_type` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date` DATETIME DEFAULT NULL,
  `sure_sales_volume` INT(11) DEFAULT NULL,
  `sure_sales` DECIMAL(11,2) DEFAULT NULL,
  `sales_volume` INT(11) DEFAULT NULL,
  `sales` DECIMAL(11,2) DEFAULT NULL,
  `type` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `real_sales` DECIMAL(11,2) DEFAULT NULL,
  `real_sales_volume` INT(11) DEFAULT NULL,
  order_type  char(1),
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`country`,`date`,`type`,order_type)
) COMMENT='按类型产品销量和销售额' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE amazoninfo_posts_health
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	sku varchar(100),
	asin varchar(100),
	product_name varchar(1000),
	field_name varchar(1000),
	alert_type varchar(1000),
	current_value varchar(1000),
	last_updated varchar(100),
	explanation varchar(2000),
	country varchar(10),
	date datetime,
	PRIMARY KEY (id)
) COMMENT = '帖子质量报告' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE exchange_rate
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	name char(7),
	rate float,
	date datetime,
	PRIMARY KEY (id)
) COMMENT = '汇率表' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE account_balance
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	country char(7),
	balance DECIMAL(11,2),,
	update_time datetime,
	PRIMARY KEY (id)
) COMMENT = '账号余额表' CHARSET utf8 COLLATE utf8_unicode_ci;
	

DROP TABLE IF EXISTS `amazoninfo_customer`;
CREATE TABLE amazoninfo_customer
(
 
	 customer_id varchar(50) NOT NULL,

     name varchar(200),
    
     country  varchar(10),
    
     amz_email  varchar(200),
    
     email  varchar(200),
    
     star char(1),
    
     buy_times int ,
    
     buy_quantity int,
    
     return_quantity int,
    
     first_buy_date datetime,
    
     last_buy_date datetime,
     
     days int,
     
     refund_money decimal(11,2),
     
     support_quantity int,
     
     event_id  varchar(200),
     `message_state` CHAR(1),
	
	PRIMARY KEY (customer_id)
) COMMENT = '客户表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_review_comment`;

CREATE TABLE amazoninfo_review_comment
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	
	create_date datetime,
    
    review_asin varchar(50),
    
    asin varchar(50),

    country varchar(10),
    
    review_date datetime,
    
    star char(1),
    
    subject varchar(500),
    
    customer_id varchar(50),
	
	PRIMARY KEY (id)
) COMMENT = '客户评论表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_buy_comment`;
CREATE TABLE amazoninfo_buy_comment
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	
	create_date datetime,
    
    type_date datetime,
    
    type char(1),
    
    order_id varchar(100),
    
    asin varchar(100),
    
    sku varchar(100),
    
    product_name varchar(100),
    
    quantity int,
    
    item_id int,
    
    remark varchar(200),
    
    customer_id varchar(50),
    
    money decimal(11,2),
	
	PRIMARY KEY (id)
) COMMENT = '客户购买记录表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_comment`;
CREATE TABLE amazoninfo_comment
(
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `create_date` datetime DEFAULT NULL,
  `content` mediumtext COLLATE utf8_unicode_ci COMMENT '发送内容',
  `customer_id` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `task_id` int(11) DEFAULT NULL COMMENT '任务编号',
  `sent_date` date DEFAULT NULL COMMENT '发送日期',
  `send_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '发送标记 0：未发送 1：已发送',
  `template_id` int(11) DEFAULT NULL COMMENT '客服邮件模板',
  `send_email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '发送邮箱',
  `send_subject` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '主题',
  `custom_email` VARCHAR(64) NULL  COMMENT '客户回复邮件',
  `reivew_id` INT(11) NULL  COMMENT '客户评论',
  `order_id` VARCHAR(100) NULL  COMMENT '亚马逊订单号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`customer_id`,`template_id`)
) COMMENT = '售后邮件发送记录表' CHARSET utf8 COLLATE utf8_unicode_ci;
	
DROP TABLE IF EXISTS amazoninfo_discount_warning;
CREATE TABLE amazoninfo_discount_warning(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   promotion_id         VARCHAR(100)      COMMENT '折扣id',
   country              VARCHAR(20)       COMMENT '平台',
   remark               VARCHAR(500)      COMMENT '备注',
   warning_sta          VARCHAR(500)      COMMENT '预警状态',
   create_user          VARCHAR(32)       COMMENT '创建人', 
   create_date          DATE              COMMENT '创建时间',
   update_user          VARCHAR(32)       COMMENT '修改人', 
   end_date          	DATE              COMMENT '创建时间',
   update_date          DATE              COMMENT '修改时间',
   PRIMARY KEY (id)      
) COMMENT = '亚马逊折扣预警' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS amazoninfo_discount_warning_item;
CREATE TABLE amazoninfo_discount_warning_item(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   warning_id           VARCHAR(100)      COMMENT '主id',
   sku                  VARCHAR(100)      COMMENT 'sku',
   product_name_color   VARCHAR(100)      COMMENT '产品名颜色',
   half_hour_quantity   INT(11)           COMMENT '半小时销量', 
   cumulative_quantity  INT(11)           COMMENT '累计销量', 
   remark               VARCHAR(500)      COMMENT '备注',
   del_flag             CHAR(1)           COMMENT '删除标记',
   PRIMARY KEY (id)      
) COMMENT = '亚马逊折扣预警明细' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS amazoninfo_evaluate_warning;
CREATE TABLE amazoninfo_evaluate_warning(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   promotion_id         VARCHAR(100)      COMMENT '折扣id',
   country              VARCHAR(20)       COMMENT '平台',
   update_date          DATETIME          COMMENT '修改时间',
   create_user          VARCHAR(32)       COMMENT '创建人', 
   create_date          DATE              COMMENT '创建时间',
   promotion_code       VARCHAR(100)      COMMENT '促销code',
   remark               VARCHAR(500)      COMMENT '备注',
   result               VARCHAR(100)      COMMENT '返回结果',
   warning_sta          VARCHAR(500)      COMMENT '预警状态',
   PRIMARY KEY (id)      
) COMMENT = '评测促销预警' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS amazoninfo_evaluate_warning_log;
CREATE TABLE amazoninfo_evaluate_warning_log(
   id                     INT(11)           NOT NULL AUTO_INCREMENT,
   evaluate_warning_id 	  VARCHAR(100)      COMMENT '折扣id',
   promotion_code         VARCHAR(100)      COMMENT '折扣码',
   relative_order_id      VARCHAR(100)      COMMENT '相关订单id',
   create_date            DATETIME          COMMENT '创建时间',
   PRIMARY KEY (id)      
) COMMENT = '评测促销预警日志' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS amazoninfo_return_test;
CREATE TABLE amazoninfo_return_test(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   product_name         VARCHAR(100)      COMMENT '产品名',  
   sku                  VARCHAR(200)      COMMENT 'sku',
   quantity             INT(11)           COMMENT '检修数量',
   reason               VARCHAR(300)      COMMENT '原因',
   reason_detail        VARCHAR(500)      COMMENT '原因详情',
   warehouse_id         INT(11)      	  COMMENT '仓库id',
   warehouse_name       VARCHAR(100)      COMMENT '仓库名称',
   new_quantity         INT(11)           COMMENT '转成new数量',
   renew_quantity       INT(11)           COMMENT '转成renew数量',
   old_quantity         INT(11)           COMMENT '转成old数量',
   broken_quantity      INT(11)           COMMENT '转成broken数量',
   stock_in_no          VARCHAR(50)       COMMENT '入库No',
   test_sta             VARCHAR(500)      COMMENT '检测状态',
   remark               VARCHAR(500)      COMMENT '备注',
   create_user          VARCHAR(32)       COMMENT '创建人', 
   create_date          DATE              COMMENT '创建时间',
   update_user          VARCHAR(32)       COMMENT '更新人', 
   update_date          DATE              COMMENT '更新时间',
   PRIMARY KEY (id)      
) COMMENT = '退货检修记录' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_reviews_out_monitor`;
CREATE TABLE `amazoninfo_reviews_out_monitor` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
   `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
   `create_date` datetime DEFAULT NULL,
   `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
   `ASIN` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
   `product_name` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
   PRIMARY KEY (`id`)
) COMMENT='对手产品差评监控' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_opponent_asin`;
CREATE TABLE `amazoninfo_opponent_asin` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
   `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
   `create_date` datetime DEFAULT NULL,
   `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
   `asin` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
   `product_name` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
   PRIMARY KEY (`id`)
) COMMENT='对手产品asin' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_reviews_out`;
CREATE TABLE `amazoninfo_reviews_out` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	  
	  `customer_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
	  `customer_id` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
	  `monitor_id` int(11) NOT NULL,
	  `review_id` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
	  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
	  `review_date` datetime DEFAULT NULL,
	  `last_update_date` datetime DEFAULT NULL,
	  
	  `content` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL,
	  `subject` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
	  `star` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
	  `content_show` varchar(10000) COLLATE utf8_unicode_ci DEFAULT NULL,
	  `subject_show` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL,
	  `star_show` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
	   PRIMARY KEY (`id`)
) COMMENT='对手产品差评' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS amazoninfo_directory_comment;
CREATE TABLE amazoninfo_directory_comment(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   title                VARCHAR(500)      COMMENT '产品title',  
   asin                 VARCHAR(100)      COMMENT 'asin',
   country              VARCHAR(10)       COMMENT '国家',
   data_date            DATE              COMMENT '扫描时间',
   star1                INT(11)           COMMENT '1星数量',
   star2                INT(11)           COMMENT '2星数量',
   star3            	INT(11)           COMMENT '3星数量',
   star4                INT(11)           COMMENT '4星数量',
   star5                INT(11)           COMMENT '5星数量',
   star_total           INT(11)           COMMENT '星星总数',
   star                 DECIMAL(11,2)     COMMENT '总得分',
   ranking              INT(3)            COMMENT '排名',  
   sale_price           DECIMAL(11,2)     COMMENT '售价',
   url                  VARCHAR(500)      COMMENT 'url',
   image                VARCHAR(500)      COMMENT '图片url',
   brand                VARCHAR(100)      COMMENT 'brand',  
   update_date          DATE              COMMENT '更新日期',
   shelves_date         DATE              COMMETN '上架日期',
   directory_id         INT(11)           COMMENT '目录id',
   is_shield            CHAR(1)           COMMENT '隐藏',
   PRIMARY KEY (id)      
) COMMENT = '扫描产品目录top100' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS amazoninfo_opponent_stock;
CREATE TABLE amazoninfo_opponent_stock(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   asin                 VARCHAR(100)      COMMENT 'asin',
   country              VARCHAR(10)       COMMENT '国家',
   data_date            DATE              COMMENT '扫描日期',
   quantity             INT(11)           COMMENT '及时库存',
   diff_quantity        INT(11)           COMMENT '每天差值',
   PRIMARY KEY (id)      
) COMMENT = '对手库存监控' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS amazoninfo_directory;
CREATE TABLE amazoninfo_directory(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   subject			    VARCHAR(500)      COMMENT '扫描subject',
   url			        VARCHAR(500)      COMMENT '扫描url',  
   country              VARCHAR(10)       COMMENT '国家',
   directory_sta        CHAR(1)           COMMENT '目录状态',
   remark               VARCHAR(500)      COMMENT '备注',
   create_user          VARCHAR(32)       COMMENT '创建人', 
   create_date          DATE              COMMENT '创建时间',
   update_user          VARCHAR(32)       COMMENT '更新人', 
   update_date          DATE              COMMENT '更新时间',
   lock_sta             CHAR(1)           COMMENT '锁定状态',
   active_date          DATE              COMMENT '激活时间',
   PRIMARY KEY (id)      
) COMMENT = '亚马逊目录' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS amazoninfo_directory_comment_detail;
CREATE TABLE amazoninfo_directory_comment_detail(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   asin                 VARCHAR(100)      COMMENT 'asin',
   country              VARCHAR(10)       COMMENT '国家',
   customer_name        VARCHAR(500)      COMMENT '客户名',
   customer_id          VARCHAR(100)      COMMENT '客户id',
   review_id            VARCHAR(100)      COMMENT '评论id',
   content              VARCHAR(5000)     COMMENT 'content',
   subject              VARCHAR(500)      COMMENT 'subject',
   review_date          DATE              COMMENT '评论时间',
   create_date          DATE              COMMENT '创建时间',
   star                 VARCHAR(10)       COMMENT '星级',
   PRIMARY KEY (id)      
) COMMENT = '目录扫描详细评论' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS amazoninfo_reviewer;
CREATE TABLE amazoninfo_reviewer(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   name                 VARCHAR(100)      COMMENT 'name',
   review_email         VARCHAR(100)      COMMENT '评论email',
   email1               VARCHAR(100)      COMMENT 'email1',
   email2               VARCHAR(100)      COMMENT 'email2',
   country              VARCHAR(10)       COMMENT '平台',
   address              VARCHAR(500)      COMMENT 'address',
   reviewer_type        CHAR(1)           COMMENT '站内、站外',
   ama_url              VARCHAR(100)      COMMENT 'ama个人网址',
   youtube_url          VARCHAR(100)      COMMENT 'youtobu个人网址',
   twitter_url          VARCHAR(100)      COMMENT 'twitter个人网址',
   sitefb_url           VARCHAR(100)      COMMENT 'site Fb个人网址',
   other_url            VARCHAR(100)      COMMENT 'other平台个人网址',
   facebook_url         VARCHAR(100)      COMMENT 'facebook平台个人网址',
   instagram_url        VARCHAR(100)      COMMENT 'instagram平台个人网址',
   
   reviewer_id          VARCHAR(100)      COMMENT '客户id',
   update_date          DATETIME          COMMENT '更新时间',
   star                 VARCHAR(10)       COMMENT '给客户评分',
   rank                 INT               COMMENT '排名',
   source_platform      VARCHAR(100)      COMMENT '客户来源',
   create_by      		VARCHAR(64)      COMMENT '创建人',
   contact_by      		VARCHAR(64)      COMMENT '最后联系人',
   inateck         		INT(11)     COMMENT 'inateck评测数量',
   anker         		INT(11),
   aukey         		INT(11),
   taotronics         	INT(11),
   easyacc         		INT(11),
   mpow         		INT(11),
   ravpower         	INT(11),
   csl         			INT(11),
   other         		INT(11)     COMMENT '其他品牌评测数',
   avg_star decimal(11,2) DEFAULT NULL COMMENT '平均测评分',
   electric_ratio decimal(11,4) DEFAULT NULL COMMENT '电子产品占比',
   is_vine_voice        CHAR(1)           COMMENT '是否为Vine成员',
   PRIMARY KEY (id)      
) COMMENT = '亚马逊评论人信息' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS amazoninfo_reviewer_content;
CREATE TABLE amazoninfo_reviewer_content(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   ama_reviewer_id      VARCHAR(100)      COMMENT '主表id',
   review_id            VARCHAR(100)      COMMENT '评论id',
   review_title         VARCHAR(1000)     COMMENT 'review title',
   product_title        VARCHAR(1000)     COMMENT 'product title',
   star                 INT               COMMENT '评分',
   product_type         VARCHAR(500)      COMMENT '产品类型',
   brand_type           VARCHAR(100)      COMMENT '品牌类型',
   review_date          DATE              COMMENT '评论时间',
   PRIMARY KEY (id)      
) COMMENT = '亚马逊评论人评论详细' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_reviewer_email_manager`;
CREATE TABLE `amazoninfo_reviewer_email_manager` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email_id` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '邮件id',
  `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建者',
  `subject` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '主题',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `custom_send_date` datetime DEFAULT NULL COMMENT '客户发送时间',
  `end_date` datetime DEFAULT NULL COMMENT '结束时间',
  `answer_date` datetime DEFAULT NULL COMMENT '响应时间',
  `update_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `receive_content` mediumtext COLLATE utf8_unicode_ci COMMENT '接收内容',
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态：0:未响应   1：已响应   2：处理完成',
  `master_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '处理人',
  `custom_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '客户id',
  `revert_email` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '发件人邮箱',
  `revert_server_email` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '来至服务器邮箱',
  `remarks` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备用字段',
  `attchment_path` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '附件路径',
  `inline_attchment_path` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '内联附件路径',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记',
  `transmit` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '转发记录',
  `urgent` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '是否紧急',
  `result` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '处理结果',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `order_nos` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '订单号',
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名称',
  `problem_type` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '问题类型',
  `problem` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '问题描述',
  `reviewer_id` int(11) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '评测人id',
  PRIMARY KEY (`id`)
) COMMENT='评测人邮箱管理表' CHARSET=utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS amazoninfo_out_of_product;
CREATE TABLE amazoninfo_out_of_product(
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   product_name_color   VARCHAR(100)      COMMENT '产品名颜色',
   country              VARCHAR(10)       COMMENT 'country',
   data_date            DATE              COMMENT '断货时间',
   PRIMARY KEY (id)      
) COMMENT = '产品每日断货表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_reviewer_send_email`;
CREATE TABLE `amazoninfo_reviewer_send_email` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建者',
  `send_subject` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '主题',
  `send_content` mediumtext COLLATE utf8_unicode_ci COMMENT '发送内容',
  `send_email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '收件人邮箱',
  `send_attchment_path` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '附件路径',
  `cc_to_email` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '抄送人邮箱',
  `bcc_to_email` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '秘密抄送人邮箱',
  `sent_date` datetime DEFAULT NULL COMMENT '发送时间',
  `reviewer_email` int(11) DEFAULT NULL COMMENT '评测邮件ID',
  `type` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '邮件类型',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记',
  `send_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '发送情况',
  PRIMARY KEY (`id`)
) COMMENT='评测发送邮箱表' CHARSET=utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_reviewer_comment`;
CREATE TABLE `amazoninfo_reviewer_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reviewer_id` int(11) DEFAULT NULL COMMENT '评测人id',
  `type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:手动输入的   1：系统生成的',
  `comment` mediumtext COLLATE utf8_unicode_ci COMMENT '记录内容',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记',
  `remarks` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备用字段',
  PRIMARY KEY (`id`)
) COMMENT='评测人联系记录表' CHARSET=utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_product_manage_group`;
CREATE TABLE `psi_product_manage_group` (
  `user_id` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dict_id` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`dict_id`)
) COMMENT='分组关系表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_product_purchase_group`;
CREATE TABLE `psi_product_purchase_group` (
  `user_id` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dict_id` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`dict_id`)
) COMMENT='采购经理产品类型关系表' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE amazoninfo_vendor_order;

CREATE TABLE amazoninfo_vendor_order
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	
	ordered_date datetime,
	
	status varchar(20),
	
	country varchar(10),
	
	order_id varchar(20),
	
	ship_to_location varchar(1000),
	
	delivery_window varchar(255),
	
	freight_terms varchar(255),
	
	payment_method varchar(255),
	
	payment_terms varchar(255),
	
	purchasing_entity varchar(255),
	
	submitted_total_cost DECIMAL(11,2),
	
	accepted_total_cost DECIMAL(11,2),
	
	cancelled_total_cost DECIMAL(11,2),
	
	received_total_cost DECIMAL(11,2),
	
	shipment_id int,
	
	PRIMARY KEY (id)
) COMMENT = 'vendor订单' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE amazoninfo_vendor_orderitem;
CREATE TABLE amazoninfo_vendor_orderitem
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	
    asin varchar(10),

    sku varchar(50),
    
    sku_in_vendor varchar(50),
    
    product_name varchar(50),
    
    title varchar(1000),

    expected_delivery_date datetime,
    
    submitted_quantity int,

    accepted_quantity int,

    received_quantity int,

    outstanding_quantity int,

    item_price DECIMAL(11,2),
    
    unit_price DECIMAL(11,2),
    
    order_id int,
	
	PRIMARY KEY (id)
) COMMENT = 'vendor订单商品信息' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE amazoninfo_vendor_shipment;
CREATE TABLE amazoninfo_vendor_shipment
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	
  	asn varchar(50),
	
	country varchar(10),
	
	shipment_status varchar(50),
	
	type varchar(50),
	
	status varchar(50),
	
	ship_date datetime,
	
	shipped_date datetime,
	
	delivery_date datetime,
	
	freight_terms varchar(200),
	
	carrierSCAC varchar(200),
	
	carrier_tracking varchar(200),
	
	ship_address varchar(1000),
	
	packages int,
	
	stacked_pallets int,
	
	unstacked_pallets int,
	
	check_user varchar(64),
	
	fee DECIMAL(11,2),
	
	delivery_user varchar(64),
	
	bill_statu varchar(20),
	
	check_statu char(1),
	
	PRIMARY KEY (id)
) COMMENT = 'vendor配送单' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_sale_fee`;
CREATE TABLE `amazoninfo_sale_fee` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date` DATETIME DEFAULT NULL,
  `sku` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sales` DECIMAL(11,2) DEFAULT NULL,
  `fee` DECIMAL(11,2) DEFAULT NULL,
   `other_fee` DECIMAL(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`country`,`date`,`sku`)
) COMMENT='产品亚马逊费用表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_product_price`;
CREATE TABLE `amazoninfo_product_price` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date` DATE DEFAULT NULL,
  `sku` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
   product_name VARCHAR(100)	,
   cost DECIMAL(11,2),
   fba DECIMAL(11,2),
   commission_pcent int,
   tran_gw DECIMAL(11,2),
   tariff_pcent DECIMAL(11,2),
   type  char(1),
   amz_price DECIMAL(11,2),
   amz_price_by_sky DECIMAL(11,2),
   amz_price_by_sea  DECIMAL(11,2),
   local_price DECIMAL(11,2),
   PRIMARY KEY (`id`),
   UNIQUE KEY `unique` (`country`,`date`,`sku`)
) COMMENT='产品价格表' CHARSET utf8 COLLATE utf8_unicode_ci;

	
DROP TABLE IF EXISTS `amazoninfo_promotions_warning`;

CREATE TABLE `amazoninfo_promotions_warning` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `promotion_id` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '折扣id',
  `country` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '平台',
  `remark` VARCHAR(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `warning_sta` VARCHAR(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '预警状态',
  `start_date` DATETIME DEFAULT NULL COMMENT '促销折扣开始时间',
  `update_user` VARCHAR(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_date` DATETIME DEFAULT NULL COMMENT '修改时间',
  `end_date` DATETIME DEFAULT NULL COMMENT '促销折扣结束时间',
  `create_date` DATETIME DEFAULT NULL,
   `buyer_purchases` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
   `buyer_gets` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
   `purchased_items` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
   `promotion` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
   `one_redemption` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
   `is_active` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '0:无效 1：有效',
    qualifying_item  VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='折扣' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_promotions_warning_item`;

CREATE TABLE `amazoninfo_promotions_warning_item` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `warning_id` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '主id',
  `asin` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name_color` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名颜色',
  `half_hour_quantity` INT(11) DEFAULT NULL COMMENT '半小时销量',
  `cumulative_quantity` INT(11) DEFAULT NULL COMMENT '累计销量',
  `remark` VARCHAR(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `del_flag` CHAR(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`)
) COMMENT='折扣item' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_product_price_approval`;
CREATE TABLE `amazoninfo_product_price_approval` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名称',
  `sku` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'sku',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `price` decimal(11,2) DEFAULT NULL COMMENT '价格',
  `sale_start_date` datetime DEFAULT NULL COMMENT '起售时间',
  `sale_end_date` datetime DEFAULT NULL COMMENT '截止时间',
  `reason` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '定价原因',
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '审批状态0：未审批 1：审批通过 2：审批未通过',
  `is_active` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '0:无效 1：有效',
  `create_by` varchar(60) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '申请人',
  `create_date` DATETIME COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '申请时间',
  `review_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '审核人',
  PRIMARY KEY (`id`)
) COMMENT='产品特殊定价审批表' CHARSET=utf8 COLLATE=utf8_unicode_ci;
ALTER TABLE `erp`.`amazoninfo_product_price_approval`   
  ADD COLUMN `review_date` DATETIME NULL  COMMENT '审核时间' AFTER `review_user`,
  ADD COLUMN `warn_qty` INT(11) NULL  COMMENT '预警销量' AFTER `review_date`,
  ADD COLUMN `change_qty` INT(11) NULL  COMMENT '自动改价销量' AFTER `warn_qty`,
  ADD COLUMN `change_price` DECIMAL(11,2) NULL  COMMENT '自动改价价格' AFTER `change_qty`, 
  ADD COLUMN `is_monitor` CHAR(1) NULL  COMMENT '是否监控 0：否 1：是' AFTER `change_price`,
  ADD COLUMN `notice_flag` CHAR(1) NULL  COMMENT '是否已发邮件通知  0：否 1：是' AFTER `is_monitor`;

DROP TABLE IF EXISTS `amazoninfo_warning_letter`;
CREATE TABLE `amazoninfo_warning_letter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `letter_id` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '信件ID',
  `letter_content` mediumtext COLLATE utf8_unicode_ci COMMENT '信件内容',
  `letter_date` datetime DEFAULT NULL COMMENT '发信时间',
  `subject` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '标题',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
   account_name VARCHAR(50) DEFAULT NULL,
  `product_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名称',
  PRIMARY KEY (`id`)
) COMMENT='亚马逊警告信件' CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_case`;
CREATE TABLE `amazoninfo_case` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建者',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `subject` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `send_content` mediumtext COLLATE utf8_unicode_ci COMMENT '内容',
  `merchant_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `merchant_id` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `marketplace_id` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  `language` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `callback_country` varchar(5) COLLATE utf8_unicode_ci DEFAULT NULL,
  `case_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'case编号，amazon返回',
  `cc_to_email` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '抄送人邮箱',
  `sent_date` datetime DEFAULT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`)
) COMMENT='亚马逊CASE' CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_sales_forecast_record`;

CREATE TABLE `amazoninfo_sales_forecast_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `forecast1` int(10) DEFAULT NULL COMMENT '预测数量',
  `forecast2` int(10) DEFAULT NULL,
  `forecast3` int(10) DEFAULT NULL,
  `forecast4` int(10) DEFAULT NULL,
  `forecast5` int(10) DEFAULT NULL,
  `forecast6` int(10) DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '修改原因',
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '审核状态 0:待审核 1：通过 2：未通过',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `product_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名称(带颜色)',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `month` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '月份',
  PRIMARY KEY (`id`)
) COMMENT='销售预测修改记录' CHARSET=utf8 COLLATE=utf8_unicode_ci ;

DROP TABLE IF EXISTS `amazoninfo_product_sale_price`;
CREATE TABLE `amazoninfo_product_sale_price` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
   product_name VARCHAR(200),
   de_price DECIMAL(11,2),
   com_price DECIMAL(11,2),
   uk_price DECIMAL(11,2),
   fr_price DECIMAL(11,2),
   it_price DECIMAL(11,2),
   es_price DECIMAL(11,2),
   jp_price DECIMAL(11,2),
   ca_price DECIMAL(11,2),
   mx_price DECIMAL(11,2),
   update_time datetime,
   PRIMARY KEY (`id`),
   UNIQUE KEY `unique` (product_name)
) COMMENT='产品销售保本价格表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_pan_eu`;
CREATE TABLE `amazoninfo_pan_eu` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
   ASIN VARCHAR(200),
   sku VARCHAR(200),
   product_name VARCHAR(200),
   is_pan_eu INT,
   uk varchar(100),
   de varchar(100),
   fr varchar(100),
   it varchar(100),
   es varchar(100),
   fnsku varchar(100),
   PRIMARY KEY (`id`)
) COMMENT='泛欧报表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_operational_report`;
CREATE TABLE `amazoninfo_operational_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `product_name` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `session` int(11) DEFAULT NULL,
  `sales_volume` int(11) DEFAULT NULL COMMENT '销量',
  `return_volume` int(11) DEFAULT NULL COMMENT '退货量',
  `order_volume` int(11) DEFAULT NULL COMMENT '总订单量',
  `sales` decimal(11,2) DEFAULT NULL COMMENT '销售额',
  `bad_review` int(11) DEFAULT NULL COMMENT '差评',
  `total_review` int(11) DEFAULT NULL COMMENT '总评',
   session_order int(11) DEFAULT NULL COMMENT 'session订单',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uinque` (`country`,`create_date`,`product_name`)
) COMMENT='运营数据统计表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_operational_report_type`;
CREATE TABLE `amazoninfo_operational_report_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `session` int(11) DEFAULT NULL,
  `sales_volume` int(11) DEFAULT NULL COMMENT '销量',
  `return_volume` int(11) DEFAULT NULL COMMENT '退货量',
  `order_volume` int(11) DEFAULT NULL COMMENT '总订单量',
  `sales` decimal(11,2) DEFAULT NULL COMMENT '销售额',
  `bad_review` int(11) DEFAULT NULL COMMENT '差评',
  `total_review` int(11) DEFAULT NULL COMMENT '总评',
  session_order int(11) DEFAULT NULL COMMENT 'session订单',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uinque` (`country`,`create_date`,`type`)
) COMMENT='运营数据类型统计表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_operational_report_type`;
CREATE TABLE `amazoninfo_operational_report_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `session` int(11) DEFAULT NULL,
  `sales_volume` int(11) DEFAULT NULL COMMENT '销量',
  `return_volume` int(11) DEFAULT NULL COMMENT '退货量',
  `order_volume` int(11) DEFAULT NULL COMMENT '总订单量',
  `sales` decimal(11,2) DEFAULT NULL COMMENT '销售额',
  `bad_review` int(11) DEFAULT NULL COMMENT '差评',
  `total_review` int(11) DEFAULT NULL COMMENT '总评',
   session_order int(11) DEFAULT NULL COMMENT 'session订单',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uinque` (`country`,`create_date`,`type`)
) COMMENT='运营数据类型统计表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_type_catelog`;
CREATE TABLE `amazoninfo_type_catelog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
   catalog_link_id varchar(100),
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `query_date` date DEFAULT NULL,
  `type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
   catalog_name varchar(400) COLLATE utf8_unicode_ci DEFAULT NULL,
   sales decimal(11,2),
  `sales_volume` int(11) DEFAULT NULL COMMENT '销量',
   market_share decimal(11,2),
   avg30_market_share decimal(11,2),
   avg_price  decimal(11,2),
   yestday_avg_price  decimal(11,2),
   PRIMARY KEY (`id`)
) COMMENT='产品类型目录信息' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_type_catelog_item`;
CREATE TABLE `amazoninfo_type_catelog_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
   catelog_id int(11),
   me char(1),
   product_name varchar(1000),
   `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
   asin  varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
   image_url  varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
   brand varchar(100),
   price decimal(11,2),
   rank int(3),
   sales decimal(11,2),
  `sales_volume` int(11) DEFAULT NULL COMMENT '销量',
   first_to20 char(1),
   PRIMARY KEY (`id`)
) COMMENT='产品类型目录信息子项' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_max_order`;
CREATE TABLE `amazoninfo_max_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date` date DEFAULT NULL,
  `product_name` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`country`,`date`,`product_name`)
)  COMMENT='大订单' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_custom_filter`;
CREATE TABLE `amazoninfo_custom_filter`(  
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `start_date` DATE COMMENT '购买起始日期',
  `end_date` DATE COMMENT '购买截止日期',
  `customer_id` VARCHAR(50) COMMENT '客户ID',
  `email` VARCHAR(200) COMMENT '邮箱(亚马逊或自有邮箱)',
  `name` VARCHAR(100) COMMENT '客户名称',
  `country` VARCHAR(10) COMMENT '平台',
  `buy_times` CHAR(1) COMMENT '购买次数 1：一次 2：多次',
  `return_flag` CHAR(1) COMMENT '退货情况(为空时忽略)0:未退货 1：退过货',
  `pn1` VARCHAR(50) COMMENT '购买1',
  `pn2` VARCHAR(50) COMMENT '购买2',
  `pn3` VARCHAR(50) COMMENT '购买3',
  `pn_and` CHAR(1) COMMENT '购买复合关系 1：且 0：或',
  `pn11` VARCHAR(50) COMMENT '未购买1',
  `pn22` VARCHAR(50) COMMENT '未购买2',
  `pn33` VARCHAR(50) COMMENT '未购买3',
  `pn1and` CHAR(1) COMMENT '未购买复合关系 1：且 0：或',
  `good` CHAR(1) COMMENT '留页面好评情况 1:留过好评 0：未留过好评',
  `error` CHAR(1) COMMENT '留页面差评情况 1:留过差评 0：未留过差评',
  `pl` VARCHAR(10) COMMENT '购买频率 30、90、182分别表示一个月、三个月、半年',
  `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_date` date DEFAULT NULL COMMENT '创建日期',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '删除标记 0：正常 1：删除',
  `template_id` int(11) DEFAULT NULL COMMENT '客服邮件模板',
  `total_customer` int(11) DEFAULT NULL COMMENT '客户总数',
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:未开始  1：进行中 2：已完成',
  `big_order` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:普通单 1:大订单',
  `send_delay` int(11) DEFAULT NULL COMMENT '延迟发送天数(针对发货时间)',
  `reason` VARCHAR(100) NULL  COMMENT '群发邮件原因',
  `send_num` INT(11) NULL  COMMENT '已发送数量',
  `not_send_num` INT(11) NULL  COMMENT '未发送数量',
  `reply_num` INT(11) NULL  COMMENT '回复数量' ,
  `review_num` INT(11) NULL  COMMENT '评论数量',
  `task_type` CHAR(1) NULL  COMMENT '任务类型 1：售后询问 2：邀请评测',
  `audit_state` CHAR(1) NULL  COMMENT '审批状态 0：待审 1：通过 2：否决',
  `file_name` VARCHAR(100) NULL  COMMENT '附件名称',
  `file_path` VARCHAR(500) NULL  COMMENT '附件路径';
  PRIMARY KEY (`id`)
) COMMENT='售后邮件任务过滤条件' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS `amazoninfo_sys_promotions`;

CREATE TABLE `amazoninfo_sys_promotions` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `type` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:官网 1:系统',
  `create_user` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` DATETIME DEFAULT NULL,
  `review_user` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `review_date` DATETIME DEFAULT NULL,
  `remarks` VARCHAR(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `promotions_type` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:八五折 1：-20',
  `country` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `num` INT(11) DEFAULT NULL,
  `status` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:待审核 1：已审核 2；取消',
  PRIMARY KEY (`id`)
) COMMENT='系统折扣' CHARSET utf8 COLLATE utf8_unicode_ci;

/*Table structure for table `amazoninfo_sys_promotions_inventory` */

DROP TABLE IF EXISTS `amazoninfo_sys_promotions_inventory`;

CREATE TABLE `amazoninfo_sys_promotions_inventory` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `promotions_code` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_active` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:未使用 1:使用',
  `create_date` DATETIME DEFAULT NULL,
  `promotions_id` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `use_date` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='系统折扣库存' CHARSET utf8 COLLATE utf8_unicode_ci;

/*Table structure for table `amazoninfo_sys_promotions_item` */

DROP TABLE IF EXISTS `amazoninfo_sys_promotions_item`;

CREATE TABLE `amazoninfo_sys_promotions_item` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `track_id` INT(11) DEFAULT NULL,
  `promotions_id` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `promotions_code` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '官网注册邮箱',
  `custom_id` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `amazon_order_id` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` VARCHAR(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='系统折扣详情' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_report_month_type`;
CREATE TABLE `amazoninfo_report_month_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `month` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '月份',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名称(带颜色)',
  `type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品类型',
  `line` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品线',
  `sales_volume` int(11) DEFAULT NULL COMMENT '销量',
  `sales` decimal(11,2) DEFAULT NULL COMMENT '销售额',
  `sales_no_tax` decimal(11,2) DEFAULT NULL COMMENT '税后收入',
  `refund` decimal(11,2) DEFAULT NULL COMMENT '退款',
  `amazon_fee` decimal(11,2) DEFAULT NULL COMMENT '亚马逊佣金',
  `other_fee` decimal(11,2) DEFAULT NULL COMMENT '杂费',
  `transport_fee` decimal(11,2) DEFAULT NULL COMMENT '运输费',
  `buy_cost` decimal(11,2) DEFAULT NULL COMMENT '采购成本',
  `profits` decimal(11,2) DEFAULT NULL COMMENT '利润',
  return_num INTEGER DEFAULT NULL COMMENT '退货数量',
  `product_attr` VARCHAR(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`month`,`country`,`product_name`)
) COMMENT='运营利润业绩中间表' CHARSET utf8 COLLATE utf8_unicode_ci;

ALTER TABLE amazoninfo_report_month_type   
  ADD COLUMN support_cost DECIMAL(11,2) NULL  COMMENT '替代货成本' AFTER support_num,
  ADD COLUMN support_amazon_fee DECIMAL(11,2) NULL  COMMENT '替代货亚马逊费用' AFTER support_cost,
  ADD COLUMN review_cost DECIMAL(11,2) NULL  COMMENT '评测单成本' AFTER review_num,
  ADD COLUMN review_amazon_fee DECIMAL(11,2) NULL  COMMENT '评测单亚马逊费用' AFTER review_cost,
  ADD COLUMN ad_in_event_sales DECIMAL(11,2) NULL  COMMENT '站内event广告销售额',
  ADD COLUMN ad_in_event_sales_volume INT(11) NULL  COMMENT '站内event广告销量',
  ADD COLUMN ad_in_event_fee DECIMAL(11,2) NULL  COMMENT '站内event广告费用',
  ADD COLUMN ad_in_profit_sales DECIMAL(11,2) NULL  COMMENT '站内profit广告销售额',
  ADD COLUMN ad_in_profit_sales_volume INT(11) NULL  COMMENT '站内profit广告销量',
  ADD COLUMN ad_in_profit_fee DECIMAL(11,2) NULL  COMMENT '站内profit广告费用',
  ADD COLUMN ad_out_event_sales DECIMAL(11,2) NULL  COMMENT '站外event广告销售额',
  ADD COLUMN ad_out_event_sales_volume INT(11) NULL  COMMENT '站外event广告销量',
  ADD COLUMN ad_out_event_fee DECIMAL(11,2) NULL  COMMENT '站外event广告费用',
  ADD COLUMN ad_out_profit_sales DECIMAL(11,2) NULL  COMMENT '站外profit广告销售额',
  ADD COLUMN ad_out_profit_sales_volume INT(11) NULL  COMMENT '站外profit广告销量',
  ADD COLUMN ad_out_profit_fee DECIMAL(11,2) NULL  COMMENT '站外profit广告费用',
  ADD COLUMN `recall_num` INT(11) NULL  COMMENT '召回数量',
  ADD COLUMN `recall_cost` DECIMAL(11,2) NULL  COMMENT '召回成本';
  ADD COLUMN `recall_fee` INT(11) NULL  COMMENT '召回费用',
  ADD COLUMN `market_num` INT(11) NULL  COMMENT 'B2B销量',
  ADD COLUMN `market_sales` DECIMAL(11,2) NULL  COMMENT 'B2B销售额',
  ADD COLUMN `tariff` DECIMAL(11,2) NULL  COMMENT '关税',
  ADD COLUMN `storage_fee` DECIMAL(11,2) NULL  COMMENT '仓储费',
  ADD COLUMN `long_storage_fee` DECIMAL(11,2) NULL  COMMENT '长期仓储费' AFTER `storage_fee`,
  ADD COLUMN `deal_fee` DECIMAL(11,2) NULL  COMMENT '闪促费' AFTER `long_storage_fee`,
  ADD COLUMN `deal_sales_volume` INT(11) NULL  COMMENT '闪促销量' AFTER `deal_fee`,
  ADD COLUMN `deal_profit` DECIMAL(11,2) NULL  COMMENT '闪促盈亏' AFTER `deal_sales_volume`,
  ADD COLUMN `mold_fee` DECIMAL(11,2) NULL COMMENT '模具费',
  ADD COLUMN `account_name` varchar(30) NULL COMMENT '账号名称';
ALTER TABLE `amazoninfo_report_month_type`   
  ADD COLUMN `express_fee` DECIMAL(11,2) NULL  COMMENT '自发货邮费' AFTER `account_name`,
  ADD COLUMN `vine_fee` DECIMAL(11,2) NULL  COMMENT 'vine fee' AFTER `express_fee`,
  ADD COLUMN `vine_num` INT(11) NULL  COMMENT 'vine数量' AFTER `vine_fee`,
  ADD COLUMN `vine_cost` DECIMAL(11,2) NULL  COMMENT 'vine成本' AFTER `vine_num`;


DROP TABLE IF EXISTS `amazoninfo_promotions_report`;

CREATE TABLE `amazoninfo_promotions_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `purchase_date` date DEFAULT NULL,
  `product_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `promotion_ids` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `discount` decimal(11,2) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `sales` decimal(11,2) DEFAULT NULL,
  `code` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`country`,`purchase_date`,`product_name`,`asin`,`promotion_ids`,`discount`)
) COMMENT='折扣订单表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_sale_profit`;
CREATE TABLE `amazoninfo_sale_profit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `day` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '日期串',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名称(带颜色)',
  `type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品类型',
  `line` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品线',
  `sales` decimal(11,2) DEFAULT NULL COMMENT '销售额',
  `sales_volume` int(11) DEFAULT NULL COMMENT '销量',
  `sales_no_tax` decimal(11,2) DEFAULT NULL COMMENT '税后收入',
  `refund` decimal(11,2) DEFAULT NULL COMMENT '退款',
  `amazon_fee` decimal(11,2) DEFAULT NULL COMMENT '亚马逊费用',
  `other_fee` decimal(11,2) DEFAULT NULL COMMENT '杂费',
  `transport_fee` decimal(11,2) DEFAULT NULL COMMENT '运输费',
  `buy_cost` decimal(11,2) DEFAULT NULL COMMENT '采购成本',
  `profits` decimal(11,2) DEFAULT NULL COMMENT '利润',
  return_num INTEGER DEFAULT NULL COMMENT '退货数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`day`,`country`,`product_name`)
) COMMENT='运营业绩分产品按天统计表' CHARSET utf8 COLLATE utf8_unicode_ci;

ALTER TABLE amazoninfo_sale_profit   
  ADD COLUMN support_cost DECIMAL(11,2) NULL  COMMENT '替代货成本' AFTER support_num,
  ADD COLUMN support_amazon_fee DECIMAL(11,2) NULL  COMMENT '替代货亚马逊费用' AFTER support_cost,
  ADD COLUMN review_cost DECIMAL(11,2) NULL  COMMENT '评测单成本' AFTER review_num,
  ADD COLUMN review_amazon_fee DECIMAL(11,2) NULL  COMMENT '评测单亚马逊费用' AFTER review_cost,
  ADD COLUMN ad_in_event_sales DECIMAL(11,2) NULL  COMMENT '站内event广告销售额',
  ADD COLUMN ad_in_event_sales_volume INT(11) NULL  COMMENT '站内event广告销量',
  ADD COLUMN ad_in_event_fee DECIMAL(11,2) NULL  COMMENT '站内event广告费用',
  ADD COLUMN ad_in_profit_sales DECIMAL(11,2) NULL  COMMENT '站内profit广告销售额',
  ADD COLUMN ad_in_profit_sales_volume INT(11) NULL  COMMENT '站内profit广告销量',
  ADD COLUMN ad_in_profit_fee DECIMAL(11,2) NULL  COMMENT '站内profit广告费用',
  ADD COLUMN ad_out_event_sales DECIMAL(11,2) NULL  COMMENT '站外event广告销售额',
  ADD COLUMN ad_out_event_sales_volume INT(11) NULL  COMMENT '站外event广告销量',
  ADD COLUMN ad_out_event_fee DECIMAL(11,2) NULL  COMMENT '站外event广告费用',
  ADD COLUMN ad_out_profit_sales DECIMAL(11,2) NULL  COMMENT '站外profit广告销售额',
  ADD COLUMN ad_out_profit_sales_volume INT(11) NULL  COMMENT '站外profit广告销量',
  ADD COLUMN ad_out_profit_fee DECIMAL(11,2) NULL  COMMENT '站外profit广告费用',
  ADD COLUMN `recall_num` INT(11) NULL  COMMENT '召回数量',
  ADD COLUMN `recall_cost` DECIMAL(11,2) NULL  COMMENT '召回成本';
  ADD COLUMN `recall_fee` INT(11) NULL  COMMENT '召回费用',
  ADD COLUMN `market_num` INT(11) NULL  COMMENT 'B2B销量',
  ADD COLUMN `market_sales` DECIMAL(11,2) NULL  COMMENT 'B2B销售额',
  ADD COLUMN `tariff` DECIMAL(11,2) NULL  COMMENT '关税',
  ADD COLUMN `storage_fee` DECIMAL(11,2) NULL  COMMENT '仓储费',
  ADD COLUMN `deal_fee` DECIMAL(11,2) NULL  COMMENT '闪促费' AFTER `storage_fee`,
  ADD COLUMN `deal_sales_volume` INT(11) NULL  COMMENT '闪促销量' AFTER `deal_fee`
  ADD COLUMN `deal_profit` DECIMAL(11,2) NULL  COMMENT '闪促盈亏' AFTER `deal_sales_volume`,
  ADD COLUMN `mold_fee` DECIMAL(11,2) NULL COMMENT '模具费',
  ADD COLUMN `account_name` varchar(30) NULL COMMENT '账号名称';
ALTER TABLE `amazoninfo_sale_profit`   
  ADD COLUMN `express_fee` DECIMAL(11,2) NULL  COMMENT '自发货邮费' AFTER `account_name`,
  ADD COLUMN `vine_fee` DECIMAL(11,2) NULL  COMMENT 'vine fee' AFTER `express_fee`,
  ADD COLUMN `vine_num` INT(11) NULL  COMMENT 'vine数量' AFTER `vine_fee`,
  ADD COLUMN `vine_cost` DECIMAL(11,2) NULL  COMMENT 'vine成本' AFTER `vine_num`;


DROP TABLE IF EXISTS `amazoninfo_outside_promotion`;
CREATE TABLE `amazoninfo_outside_promotion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `promo_warning_id`	int           COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'warningId',
  `country`			 	varchar(10)   COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `website` 			varchar(100)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '外部站点',
  `url`     			varchar(200)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'url',
  `promotion_code`     	varchar(200)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '折扣码',
  `platform_funds` 	    decimal(11,2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '平台经费',
  `sample_provided`     varchar(200)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '样品提供',
  `track_id` 			varchar(100)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'trackId',
  `product_name`		varchar(100)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品型号',
  `asin`        		varchar(100)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'asin',
  `del_flag` 			char(1)       COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '删除状态',
   start_date           DATE          COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '开始日期',
   end_date             DATE          COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '结束日期',
   end_real_date        DATE          COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'end日期',
   promotion_date       DATE          COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '宣传开始日期',
   buyer_gets           VARCHAR(200)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '折扣力度',
   create_user          VARCHAR(50)   COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
   create_date          DATE          COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建时间',
   PRIMARY KEY (`id`)
) COMMENT='站外推广分析' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_outside_promotion_website`;
CREATE TABLE `amazoninfo_outside_promotion_website` (
   `id`               int(11)       NOT NULL AUTO_INCREMENT,
   track_id           VARCHAR(200)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'trackId',
   website            VARCHAR(200)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'website',
   url                VARCHAR(500)  COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'url',
   promo_date         DATE          COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '推广日期',
   PRIMARY KEY (`id`)
) COMMENT='站外推广站点信息' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS `amazoninfo_tread_review`;

CREATE TABLE `amazoninfo_tread_review` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `create_date` datetime DEFAULT NULL,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `account` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `account_num` int(11) DEFAULT NULL,
  `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='踩差评' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS `amazoninfo_tread_review_account`;

CREATE TABLE `amazoninfo_tread_review_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `create_date` datetime DEFAULT NULL,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `login_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0',
  `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='踩差评账号' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_vendor_returns`;

CREATE TABLE `amazoninfo_vendor_returns` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `request_id` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `request_type` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `return_reason` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `requested_quantity` int(11) DEFAULT NULL,
  `approved_quantity` int(11) DEFAULT NULL,
  `requested_refund` decimal(11,2) DEFAULT NULL,
  `approved_refund` decimal(11,2) DEFAULT NULL,
  `total_cost` decimal(11,2) DEFAULT NULL,
  `warehouse` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `query_time` date DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='vendor退货单' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_vendor_returns_item`;

CREATE TABLE `amazoninfo_vendor_returns_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `return_id` int(11) DEFAULT NULL,
  `request_item_id` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ean` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `requested_quantity` int(11) DEFAULT NULL,
  `approved_quantity` int(11) DEFAULT NULL,
  `requested_refund` decimal(11,2) DEFAULT NULL,
  `approved_refund` decimal(11,2) DEFAULT NULL,
  `total_cost` decimal(11,2) DEFAULT NULL,
  `product_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='vendor退货单详情' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_attr`;

CREATE TABLE `amazoninfo_attr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(10) COLLATE utf8_unicode_ci NOT NULL COMMENT '国家',
  `email` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '登录账户',
  `password` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '账户密码',
  `access_key` varchar(200) COLLATE utf8_unicode_ci NOT NULL COMMENT '亚马逊接口key',
  `secret_key` varchar(200) COLLATE utf8_unicode_ci NOT NULL COMMENT '亚马逊接口key',
  `seller_id` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '亚马逊账户标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='亚马逊账户信息表'


DROP TABLE IF EXISTS `amazoninfo_sales_summary`;

CREATE TABLE `amazoninfo_sales_summary` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `order_id` int(11) DEFAULT NULL COMMENT '订单编号id',
  `amazon_order_id` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '亚马逊订单号',
  `shipped_date` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '发货时间',
  `address` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '订单收货地址',
  `order_total` decimal(11,2) DEFAULT NULL COMMENT '订单销售额',
  `country_code` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `sales_channel` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '销售平台',
  `invoice_no` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '账单号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='订单发货统计表'


DROP TABLE IF EXISTS `amazoninfo_sales_summary_file`;

CREATE TABLE `amazoninfo_sales_summary_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `month` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '月份',
  `platform` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '平台(分欧洲、美国、日本、加拿大)',
  `type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '文件类型',
  `file_path` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '文件存放路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='订单报表记录表'


DROP TABLE IF EXISTS `amazoninfo_spread_report`;
CREATE TABLE `amazoninfo_spread_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `product_name` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `session` int(11) DEFAULT NULL,
  `sales_volume` int(11) DEFAULT NULL COMMENT '销量',
  `sales` decimal(11,2) DEFAULT NULL COMMENT '销售额',
  `sku` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `price` decimal(11,2) DEFAULT NULL,
  `cost` decimal(11,2) DEFAULT NULL,
  `order` int(11) DEFAULT NULL,
  `conversion` decimal(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE` (`country`,`create_date`,`product_name`,`asin`,`sku`)
) COMMENT='运营数据表' CHARSET utf8 COLLATE utf8_unicode_ci; 


DROP TABLE IF EXISTS `amazoninfo_news_subscribe`;
CREATE TABLE `amazoninfo_news_subscribe` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `platform` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '平台',
  `product_name` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '订阅条件',
  `type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '1：按产品  2：按产品类型  3：按产品线  4：按产品属性(新品、主力、淘汰)',
  `email` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '接收消息的email,默认为创建人的邮箱',
  `email_type` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '订阅的邮件类型',
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '生效标记 0:未生效 1：生效',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记 1:删除',
  `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='消息订阅表'

DROP TABLE IF EXISTS `amazoninfo_news_type`;
CREATE TABLE `amazoninfo_news_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `number` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '编号,从10开始，递增唯一',
  `name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '消息名称',
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:停用 1:启用',
  `remark` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '消息说明',
  `type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '类型',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `auto` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '默认勾选0:不选 1:勾选',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '删除标记0:正常 1:删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `number` (`number`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='订阅消息对象'



DROP TABLE IF EXISTS `amazoninfo_facebook_relationship`;
CREATE TABLE `amazoninfo_facebook_relationship` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `market` char(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date` date DEFAULT NULL,
  `product_line` varchar(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `gender` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `audience` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `age` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `placement` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin_on_ad` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ad_id` bigint(20) DEFAULT NULL,
  `tracking_id` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pre_view` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='关联关系表' CHARSET utf8 COLLATE utf8_unicode_ci; 



DROP TABLE IF EXISTS `amazoninfo_facebook_report`;

CREATE TABLE `amazoninfo_facebook_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `market` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_line` varchar(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `item_name` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `seller` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tracking_id` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date_shipped` date DEFAULT NULL,
  `price` decimal(11,2) DEFAULT NULL,
  `advertising_fee_rate` decimal(11,2) DEFAULT NULL,
  `items_shipped` int(11) DEFAULT NULL,
  `revenue` decimal(11,2) DEFAULT NULL,
  `advertising_fees` decimal(11,2) DEFAULT NULL,
  `device_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `profit` decimal(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='amazon数据表' CHARSET utf8 COLLATE utf8_unicode_ci; 

DROP TABLE IF EXISTS `facebook_report`;

CREATE TABLE `facebook_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `start` date DEFAULT NULL,
  `end` date DEFAULT NULL,
  `account_id` bigint(20) DEFAULT NULL,
  `campaign_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `campaign_id` bigint(20) DEFAULT NULL,
  `ad_set_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ad_set_id` bigint(20) DEFAULT NULL,
  `ad_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ad_id` bigint(20) DEFAULT NULL,
  `delivery` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `amount_spent` decimal(11,2) DEFAULT NULL,
  `impressions` decimal(11,0) DEFAULT NULL,
  `link_clicks` decimal(11,0) DEFAULT NULL,
  `frequency` decimal(11,6) DEFAULT NULL,
  `relevance_score` decimal(11,2) DEFAULT NULL,
  `negative_feedback` decimal(11,2) DEFAULT NULL,
  `post_shares` decimal(11,2) DEFAULT NULL,
  `post_comments` decimal(11,2) DEFAULT NULL,
  `page_likes` decimal(11,2) DEFAULT NULL,
  `post_engagement` decimal(11,2) DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='facebook广告表' CHARSET utf8 COLLATE utf8_unicode_ci; 

DROP TABLE IF EXISTS `amazoninfo_mfn_replace`;

CREATE TABLE `amazoninfo_mfn_replace` (
  `email` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `total` decimal(11,2) DEFAULT NULL,
  `name_quantity` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `order_id` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL
) COMMENT='mfn表' CHARSET utf8 COLLATE utf8_unicode_ci; 

<<<<<<< .mine

DROP TABLE IF EXISTS `temp_email_money_compare`;

CREATE TABLE `temp_email_money_compare` (
  `email` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `total` decimal(11,2) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mfn_total` decimal(11,2) DEFAULT NULL,
  `rate` decimal(11,2) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='compare表' CHARSET utf8 COLLATE utf8_unicode_ci; 


DROP TABLE IF EXISTS `amazon_user`;
CREATE TABLE `amazon_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '账号',
  `password` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '密码',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '平台',
  `role_name` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '归属角色信息',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `update_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '修改人',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `auth_code` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '亚马逊google校验code',
  `ip` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '指定IP上登录',
  PRIMARY KEY (`id`)
) COMMENT='亚马逊后台账户表' CHARSET utf8 COLLATE utf8_unicode_ci

DROP TABLE IF EXISTS `amazon_login_log`;
CREATE TABLE `amazon_login_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '操作人',
  `data_date` datetime DEFAULT NULL COMMENT '操作时间',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '登录平台简称',
  PRIMARY KEY (`id`)
) COMMENT='亚马逊后台登录记录' CHARSET utf8 COLLATE utf8_unicode_ci




CREATE TABLE `amazoninfo_outbound_address` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address_line1` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address_line2` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address_line3` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `city` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `state_or_region` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country_code` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `postal_code` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_id` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `district` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='评测地址' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE `amazoninfo_outbound_order` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `amazon_order_id` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `seller_order_id` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_update_date` DATETIME DEFAULT NULL,
  `order_status` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shipping_speed_category` VARCHAR(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_type` VARCHAR(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `displayable_order_comment` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shipping_address` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` VARCHAR(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `buyer_email` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `buyer_name` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `earliest_ship_date` DATETIME DEFAULT NULL,
  `latest_ship_date` DATETIME DEFAULT NULL,
  `earliest_delivery_date` DATETIME DEFAULT NULL,
  `latest_delivery_date` DATETIME DEFAULT NULL,
  `custom_id` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cancel_date` DATETIME DEFAULT NULL,
  `cancel_user` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` DATETIME DEFAULT NULL,
  `create_user` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `check_date` DATETIME DEFAULT NULL,
  `check_user` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `weight` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fba_per_unit_fulfillment_fee` DECIMAL(11,2) DEFAULT NULL,
  `fba_transportation_fee` DECIMAL(11,2) DEFAULT NULL,
  `remark` VARCHAR(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `old_order_id` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `event_id` INT(11) DEFAULT NULL,
  `fulfillment_action` VARCHAR(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fba_per_order_fulfillment_fee` DECIMAL(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='评测订单' CHARSET utf8 COLLATE utf8_unicode_ci;



CREATE TABLE `amazoninfo_outbound_orderitem` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `sellersku` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_id` INT(11) DEFAULT NULL,
  `quantity_ordered` INT(11) DEFAULT NULL,
  `color` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='评测详情' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE `amazoninfo_removal_order` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `amazon_order_id` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_update_date` DATETIME DEFAULT NULL,
  `purchase_date` DATETIME DEFAULT NULL,
  `order_status` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_type` VARCHAR(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` VARCHAR(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` DATETIME DEFAULT NULL,
   service_speed VARCHAR(30) COLLATE utf8_unicode_ci DEFAULT NULL,
   account_name     VARCHAR(50)                   NULL,
   PRIMARY KEY (`id`)
) COMMENT='召回订单' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE `amazoninfo_removal_orderitem` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `sellersku` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fnsku` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_id` INT(11) DEFAULT NULL,
  
  `requested_qty` INT(11) DEFAULT NULL,
  `completed_qty` INT(11) DEFAULT NULL,
  `cancelled_qty` INT(11) DEFAULT NULL,
  `in_process_qty` INT(11) DEFAULT NULL,
  
   disposition varchar(255)COLLATE utf8_unicode_ci DEFAULT NULL,
   removal_fee DECIMAL(11,2) DEFAULT NULL,
  `stored_qty` INT(11) DEFAULT NULL COMMENT '已入库数',
  
   PRIMARY KEY (`id`)
) COMMENT='召回订单详情' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE `amazoninfo_session_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `event_type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
   event_data  date,
  
  PRIMARY KEY (`id`)
) COMMENT='产品session影响事件' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_product_catalogues`;
CREATE TABLE `amazoninfo_product_catalogues` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `catalog_name` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_id` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_name` varchar(1000) COLLATE utf8_unicode_ci NOT NULL,
  `item_type` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0',
  PRIMARY KEY (`id`)
) COMMENT='亚马逊目录表' CHARSET utf8 COLLATE utf8_unicode_ci;



CREATE TABLE `amazoninfo_outbound_shipment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sellersku` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `color` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `track_number` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `track_supplier` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shipment_id` INT(11) DEFAULT NULL,
  `estimated_arrival_date` DATE DEFAULT NULL
  PRIMARY KEY (`id`)
) COMMENT='物流' CHARSET utf8 COLLATE utf8_unicode_ci;

 CREATE TABLE `amazoninfo_search_term_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `campaign_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ad_group_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `customer_search_term` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `match_type` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `impressions` int(11) DEFAULT NULL,
  `clicks` int(11) DEFAULT NULL,
  `ctr` decimal(11,2) DEFAULT NULL,
  `total_spend` decimal(11,2) DEFAULT NULL,
  `average_cpc` decimal(11,2) DEFAULT NULL,
  `acos` decimal(11,2) DEFAULT NULL,
  `orders_placed` int(11) DEFAULT NULL,
  `product_sales` decimal(11,2) DEFAULT NULL,
  `conversion_rate` decimal(11,2) DEFAULT NULL,
  `same_sku` int(11) DEFAULT NULL,
  `other_sku` int(11) DEFAULT NULL,
  `same_sku_sale` decimal(11,2) DEFAULT NULL,
  `other_sku_sale` decimal(11,2) DEFAULT NULL,
  `update_time` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='search_term' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_keyword`;

CREATE TABLE `amazoninfo_keyword` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `asin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `title` varchar(2000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0',
  `search_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='keyword' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_keyword_search`;

CREATE TABLE `amazoninfo_keyword_search` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='keyword' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_new_releases_rank`;

CREATE TABLE `amazoninfo_new_releases_rank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ASIN` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `query_time` date DEFAULT NULL,
  `catalog` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `catalog_name` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `path` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_name` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='新品top100' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE `psi_supplier_indemnify` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `supplier_id` int(11) DEFAULT NULL COMMENT '供应商id',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记',
  `attchment_path` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `remark` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `subject` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `money` decimal(11,2) DEFAULT NULL,
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='供应商赔偿记录' CHARSET utf8 COLLATE utf8_unicode_ci;


/*Table structure for table `amazoninfo_ean` */

DROP TABLE IF EXISTS `amazoninfo_ean`;

CREATE TABLE `amazoninfo_ean` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ean` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `active` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:有效 1:失效 2：已使用',
  `create_date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='ean' CHARSET utf8 COLLATE utf8_unicode_ci;

/*Table structure for table `amazoninfo_product_type_code` */

DROP TABLE IF EXISTS `amazoninfo_product_type_code`;

CREATE TABLE `amazoninfo_product_type_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `code` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='类型code' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_pi_price`;

CREATE TABLE `amazoninfo_pi_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `model` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
   datadate varchar(20), 
   `create_date` datetime DEFAULT NULL,
   price decimal(11,2) DEFAULT NULL,
   PRIMARY KEY (`id`)
) COMMENT='PI_price' CHARSET utf8 COLLATE utf8_unicode_ci; 


CREATE TABLE `amazoninfo_aws_adversting` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `country`		  varchar(20)  	DEFAULT NULL,
  `status`		  varchar(20)  	DEFAULT NULL,
  `campaign_name` varchar(200)  DEFAULT NULL,
  `product_name`  varchar(100)  DEFAULT NULL,
  `campaign_type` varchar(100)  DEFAULT NULL,
  `campaign_id`   varchar(100)  DEFAULT NULL,
  `start_date`    date          DEFAULT NULL,
  `end_date`      date          DEFAULT NULL,
  `data_date`     date          DEFAULT NULL,
  `impressions`   int           DEFAULT NULL, 
  `dpv`           int           DEFAULT NULL, 
  `clicks`        int           DEFAULT NULL, 
  `units_sold`    int           DEFAULT NULL, 
  `ctr`        	decimal(11,3)    DEFAULT NULL,
  `spend`      	decimal(11,2)    DEFAULT NULL,
  `acpc`       	decimal(11,2)    DEFAULT NULL,
  `total_sales` decimal(11,2)    DEFAULT NULL,
  `acos`       	decimal(11,2)    DEFAULT NULL,
   PRIMARY KEY (`id`)
) COMMENT='aws广告数据' CHARSET utf8 COLLATE utf8_unicode_ci; 


DROP TABLE IF EXISTS `amazoninfo_lightning_deals`;

CREATE TABLE `amazoninfo_lightning_deals` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` char(7) COLLATE utf8_unicode_ci DEFAULT NULL,
  `internal_desc` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `start` datetime DEFAULT NULL,
  `end` datetime DEFAULT NULL,
  `status` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sku` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sale_price` decimal(11,2) DEFAULT NULL,
  `deal_price` decimal(11,2) DEFAULT NULL,
  `deal_quantity` int(11) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `actual_quantity` int(11) DEFAULT NULL,
  `safe_price` decimal(11,2) DEFAULT NULL,
  `deal_fee` decimal(11,2) DEFAULT NULL,
  `sale1` int(11) DEFAULT NULL,
  `sale2` int(11) DEFAULT NULL,
  `sale3` int(11) DEFAULT NULL,
  `sale4` int(11) DEFAULT NULL,
  `sale5` int(11) DEFAULT NULL,
  `rank1` int(11) DEFAULT NULL,
  `rank2` int(11) DEFAULT NULL,
  `rank3` int(11) DEFAULT NULL,
  `rank4` int(11) DEFAULT NULL,
  `rank5` int(11) DEFAULT NULL,
  `session1` int(11) DEFAULT NULL,
  `session2` int(11) DEFAULT NULL,
  `conv1` decimal(11,2) DEFAULT NULL,
  `conv2` decimal(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='闪促' CHARSET utf8 COLLATE utf8_unicode_ci; 

DROP TABLE IF EXISTS `amazoninfo_follow_seller`;
CREATE TABLE `amazoninfo_follow_seller` (
  `id` int(11) NOT NULL AUTO_INCREMENT ,
  `country` 		varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `seller_name` 	varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `a` 				varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_title` 	varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` 			varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` 	varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `quantity` 		int(11) 	 DEFAULT NULL,
  `data_date` 		date 		 DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='跟帖卖家' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS `amazoninfo_product_type_charge`;

CREATE TABLE `amazoninfo_product_type_charge` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `commission_pcent` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='佣金表' CHARSET utf8 COLLATE utf8_unicode_ci; 


DROP TABLE IF EXISTS `amazoninfo_follow_asin`;
CREATE TABLE `amazoninfo_follow_asin` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
   `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
   `create_date` datetime DEFAULT NULL,
   `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
   `asin` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
   `product_name` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
   PRIMARY KEY (`id`)
) COMMENT='跟卖监控asin' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_financial`;
CREATE TABLE `amazoninfo_financial` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amazon_order_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `merchant_order_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `marketplace_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `posted_date` timestamp NULL DEFAULT NULL,
  `country` char(5) COLLATE utf8_unicode_ci DEFAULT NULL,
  `add_time` datetime DEFAULT NULL,
  `type` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
   account_name     VARCHAR(50)                   NULL,
  PRIMARY KEY (`id`),
  KEY `posted_date` (`posted_date`),
  KEY `amz_ord` (`amazon_order_id`)
) COMMENT='订单财务报表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_financial_item`;
CREATE TABLE `amazoninfo_financial_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amazon_order_item_code` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `merchant_adjustment_item_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sku` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `principal` decimal(11,2) DEFAULT NULL,
  `shipping` decimal(11,2) DEFAULT NULL,
  `cross_border_fulfillment_fee` decimal(11,2) DEFAULT NULL,
  `fba_per_unit_fulfillment_fee` decimal(11,2) DEFAULT NULL,
  `fba_weight_based_fee` decimal(11,2) DEFAULT NULL,
  `commission` decimal(11,2) DEFAULT NULL,
  `shipping_chargeback` decimal(11,2) DEFAULT NULL,
  `giftwrap_chargeback` decimal(11,2) DEFAULT NULL,
  `refund_commission` decimal(11,2) DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `add_time` datetime DEFAULT NULL,
  `restocking_fee` decimal(11,2) DEFAULT NULL,
  `promotion` decimal(11,2) DEFAULT NULL,
  `cod` decimal(11,2) DEFAULT NULL,
  `cod_fee` decimal(11,2) DEFAULT NULL,
  `other_fee` decimal(11,2) DEFAULT NULL,
  `shipping_hb` decimal(11,2) DEFAULT NULL,
  `shipment_fee` decimal(11,2) DEFAULT NULL,
  `fba_per_order_fulfillment_fee` decimal(11,2) DEFAULT NULL,
  `gift_wrap` decimal(11,2) DEFAULT NULL,
  `goodwill` decimal(11,2) DEFAULT NULL,
  `tax` decimal(11,2) DEFAULT NULL,
  `sales_tax_service_fee` decimal(11,2) DEFAULT NULL,
  `shipping_tax` decimal(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `item_id` (`amazon_order_item_code`),
  KEY `order_id` (`order_id`)
) COMMENT='订单财务报表明细' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE `amazoninfo_order_extract` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `amazon_order_id` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `rate_sn` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `custom_id` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `invoice_flag` CHAR(5) COLLATE utf8_unicode_ci DEFAULT '000',
  `comment_url` VARCHAR(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `invoice_no` VARCHAR(50)  COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique` (`amazon_order_id`)
)COMMENT = '订单额外字段表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_long_term_storage_fees`;

CREATE TABLE `amazoninfo_long_term_storage_fees` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` char(7) COLLATE utf8_unicode_ci DEFAULT NULL,
  `snapshot_date` date DEFAULT NULL,
  `sku` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fnsku` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `condition_type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `qty_charged_twelfth_mo_long_term_storage_fee` int(11) DEFAULT NULL,
  `per_unit_volume` decimal(11,4) DEFAULT NULL,
  `currency` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `twelfth_mo_long_terms_storage_fee` decimal(11,2) DEFAULT NULL,
  `qty_charged_six_mo_long_term_storage_fee` int(11) DEFAULT NULL,
  `six_mo_long_terms_storage_fee` decimal(11,2) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
   account_name VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = '长期仓储费' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS `amazoninfo_monthly_storage_fees`;

CREATE TABLE `amazoninfo_monthly_storage_fees` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` char(7) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fnsku` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fulfillment_center` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country_code` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `longest_side` decimal(11,2) DEFAULT NULL,
  `median_side` decimal(11,2) DEFAULT NULL,
  `shortest_side` decimal(11,2) DEFAULT NULL,
  `measurement_units` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `weight` decimal(11,2) DEFAULT NULL,
  `weight_units` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `item_volume` decimal(11,4) DEFAULT NULL,
  `volume_units` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `average_quantity_on_hand` decimal(11,2) DEFAULT NULL,
  `average_quantity_pending_removal` decimal(11,2) DEFAULT NULL,
  `estimated_total_item_volume` decimal(11,2) DEFAULT NULL,
  `month` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `storage_rate` decimal(11,2) DEFAULT NULL,
  `currency` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `estimated_monthly_storage_fee` decimal(11,4) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `product_size_tier` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
   account_name VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = '月仓储费' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_return_order_shipment`;

CREATE TABLE `amazoninfo_return_order_shipment` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shipment_id` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `shipped_date` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tracking_number` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tracking_state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '1:Delivered',
  PRIMARY KEY (`id`)
)COMMENT = '召回订单' CHARSET utf8 COLLATE utf8_unicode_ci;

/*Table structure for table `amazoninfo_return_order_shipment_item` */

DROP TABLE IF EXISTS `amazoninfo_return_order_shipment_item`;

CREATE TABLE `amazoninfo_return_order_shipment_item` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `sku` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `quantity_shipped` int(11) DEFAULT NULL,
  `shipment` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = '召回订单' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_search_term_day_report`;
CREATE TABLE `amazoninfo_search_term_day_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `campaign_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ad_group_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `customer_search_term` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `match_type` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `impressions` int(11) DEFAULT NULL,
  `clicks` int(11) DEFAULT NULL,
  `total_spend` decimal(11,2) DEFAULT NULL,
  `same_sku` int(11) DEFAULT NULL,
  `update_time` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ct_idx` (`country`,`update_time`)
)COMMENT = '日searchTerm' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `amazoninfo_advertising_negative`;

CREATE TABLE `amazoninfo_advertising_negative` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `group_name` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sku` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `data_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),

)COMMENT = '关键字' CHARSET utf8 COLLATE utf8_unicode_ci;


<<<<<<< .mine
CREATE TABLE `amazoninfo_account_config` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `country` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `account_name` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `access_key` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `secret_key` VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `seller_id` VARCHAR(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `user_name` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `account_secret_key` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_date` DATE DEFAULT NULL,
  `del_flag` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='WS表';

=======

ALTER TABLE `erp`.`amazoninfo_sale_report`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `pack_num`;

ALTER TABLE `erp`.` amazoninfo_sale_report_type `   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER ` order_type `;


ALTER TABLE `erp`.`amazoninfo_order`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `payment_method_detail`;

ALTER TABLE `erp`.`amazoninfo_return_goods`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `customer_comment`;

ALTER TABLE `erp`.`amazoninfo_order_extract`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `invoice_no`;

ALTER TABLE `erp`.`amazoninfo_product2`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `price5`;

ALTER TABLE `erp`.`psi_sku`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `update_user`;

ALTER TABLE `erp`.`psi_barcode`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `del_flag`;

ALTER TABLE `erp`.`psi_inventory_fba`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `data_date`;

ALTER TABLE `erp`.`amazoninfo_ebay_order`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `create_user`;

ALTER TABLE `erp`.`amazoninfo_advertising`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `keyword_status`;

ALTER TABLE `erp`.`amazoninfo_advertising_week`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `week_parent_sku_units_lirun`;

ALTER TABLE `erp`.`amazoninfo_outbound_order`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `flag`;

ALTER TABLE `erp`.`psi_fba_inbound`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `count_flag`;

ALTER TABLE `erp`.`amazoninfo_refund`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `order_total`;

ALTER TABLE `erp`.`amazoninfo_promotions_warning`   
  ADD COLUMN `account_name` VARCHAR(20) NULL AFTER `qualifying_item`;

ALTER TABLE `erp`.`amazoninfo_promotions_report`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `code`;

  
  
CREATE TABLE `amazoninfo_account_config` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `country` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `account_name` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '账号唯一标识',
  `access_key` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'AWS访问键',
  `secret_key` VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'AWS密钥',
  `seller_id` VARCHAR(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '卖家编号',
  `mws_auth_token` VARCHAR(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `user_name` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '账号邮箱',
  `password` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '账号密码',
  `account_secret_key` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '账号密钥',
  `server_id` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `server_ip` VARCHAR(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '调用地址',
  `state` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:开启 1:关闭',
  `del_flag` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `invoice_type` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '账单号类型',
  `vendor_name` VARCHAR(50) NULL  COMMENT 'vendor账号',
  `vendor_pwd` VARCHAR(50) NULL  COMMENT 'vendor密码',
  `ams_name` VARCHAR(50) NULL  COMMENT 'ams账号',
  `ams_pwd` VARCHAR(50) NULL  COMMENT 'ams密码',
  `ams_entity_id` VARCHAR(50) NULL  COMMENT 'amsEntityId',
  `ams_code` VARCHAR(100) NULL  COMMENT 'ams账号密钥',
  PRIMARY KEY (`id`)
) COMMENT='账号配置' CHARSET utf8 COLLATE utf8_unicode_ci;



ALTER TABLE `erp`.`amazoninfo_sale_report`   
  DROP INDEX `unique`,
  ADD  UNIQUE INDEX `unique` (`order_type`, `country`, `sku`, `date`, `account_name`);
ALTER TABLE `erp`.`amazoninfo_sale_report_type`   
  DROP INDEX `unique`,
  ADD  UNIQUE INDEX `unique` (`country`, `date`, `type`, `order_type`, `account_name`);
ALTER TABLE `erp`.`custom_event_manager`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `product_attribute`;
ALTER TABLE `erp`.`amazoninfo_account_config`   
  ADD COLUMN `fba_addr` VARCHAR(500) NULL  COMMENT 'FBA帖地址' AFTER `invoice_type`;
ALTER TABLE `erp`.`amazoninfo_fba_health_report`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `create_time`;
ALTER TABLE `erp`.`amazoninfo_posts_health`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `date`;
ALTER TABLE `erp`.`amazoninfo_image_feed`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `sku`;
  
  ALTER TABLE `erp`.`amazoninfo_posts_feed`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `operate_type`;
ALTER TABLE `erp`.`amazoninfo_posts_relationship_feed`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `check_date`;
ALTER TABLE `erp`.`amazoninfo_feed_submission`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `del_flag`;
ALTER TABLE `erp`.`amazoninfo_lightning_deals`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `conv2`;
ALTER TABLE `erp`.`amazoninfo_aws_adversting`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `acos`;
  
  
  ALTER TABLE `erp`.`amazoninfo_price_feed`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `reason`;
ALTER TABLE `erp`.`amazoninfo_product_price_approval`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `type`;
    ALTER TABLE `erp`.`amazoninfo_mfn_inventory_feed`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `create_by`;
  ALTER TABLE `erp`.`amazoninfo_account_config`   
  ADD COLUMN `customer_email` VARCHAR(50) NULL AFTER `fba_addr`,
  ADD COLUMN `customer_email_password` VARCHAR(20) NULL AFTER `customer_email`,
  ADD COLUMN `email_type` CHAR(1) NULL  COMMENT '0:gmail 1:qq' AFTER `customer_email_password`;
  
  
 ALTER TABLE `erp`.`amazoninfo_ebay_order`   
  ADD COLUMN `length` DECIMAL(11,2) NULL AFTER `account_name`,
  ADD COLUMN `width` DECIMAL(11,2) NULL AFTER `length`,
  ADD COLUMN `height` DECIMAL(11,2) NULL AFTER `width`,
  ADD COLUMN `weight` INT(11) NULL AFTER `height`,
  ADD COLUMN `tracking_flag` CHAR(1) NULL AFTER `weight`, 
  ADD COLUMN `label_image` VARCHAR(100) NULL AFTER `tracking_flag`,
  ADD COLUMN `fee` INT(11,2) NULL AFTER `label_image`;

  
  CREATE TABLE `amazoninfo_ads_month_report` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `month` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
  `product_name` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `country` VARCHAR(20) COLLATE utf8_unicode_ci NOT NULL,
  `spa_avg_click` DECIMAL(11,2) DEFAULT '0.00',
  `ams_avg_click` DECIMAL(11,2) DEFAULT '0.00',
  `ams_flag` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `spa_flag` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pc_idx` (`month`,`country`,`product_name`)
) COMMENT='广告统计表' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE `amazoninfo_rank_sales_month_report` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `month` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
  `country` VARCHAR(20) COLLATE utf8_unicode_ci NOT NULL,
  `line` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
  `rank` INT(11) NOT NULL,
  `sales_volume` INT(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pc_idx` (`month`,`country`,`rank`,`line`)
) COMMENT='排名统计表' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE `amazoninfo_catalog_month_report` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `month` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
  `country` VARCHAR(20) COLLATE utf8_unicode_ci NOT NULL,
  `line` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
  `path_name` VARCHAR(200) COLLATE utf8_unicode_ci NOT NULL,
  `bestseller` VARCHAR(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pc_idx` (`month`,`country`,`line`,`path_name`)
) COMMENT='目录统计表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE `amazoninfo_review_month_report` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `month` VARCHAR(10) COLLATE utf8_unicode_ci NOT NULL,
  `country` VARCHAR(20) COLLATE utf8_unicode_ci NOT NULL,
  `product_name` VARCHAR(50) COLLATE utf8_unicode_ci NOT NULL,
  `star` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pc_idx` (`month`,`country`,`product_name`)
) COMMENT='评论统计表' CHARSET utf8 COLLATE utf8_unicode_ci;

ALTER TABLE `erp`.`amazoninfo_review_month_report`   
  ADD COLUMN `review_asin` VARCHAR(200) NULL AFTER `star`;

ALTER TABLE `erp`.`amazoninfo_ean`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `create_date`;
ALTER TABLE `erp`.`amazoninfo_account_config`   
  ADD COLUMN `sku_index` CHAR(1) NULL AFTER `ams_code`;
  
  
  ALTER TABLE `erp`.`account_balance`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `remark`;
ALTER TABLE `erp`.`amazoninfo_advertising_week`   
  DROP INDEX `unique`,
  ADD  UNIQUE INDEX `unique` (`country`, `name`, `sku`, `group_name`, `keyword`, `week`, `type`, `account_name`);
ALTER TABLE `erp`.`amazoninfo_advertising_negative`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `data_date`;
ALTER TABLE `erp`.`amazoninfo_search_term_report`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `update_time`;
ALTER TABLE `erp`.`amazoninfo_search_term_day_report`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `update_time`;
ALTER TABLE `erp`.`amazoninfo_pan_eu`   
  ADD COLUMN `account_name` VARCHAR(50) NULL AFTER `fnsku`; 
  
  
DROP TABLE IF EXISTS `amazoninfo_tiled_catalog`;
CREATE TABLE `amazoninfo_tiled_catalog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `catalog_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_id1` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_id2` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_id3` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_id4` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_id5` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_id6` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_id7` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path_id8` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `is_use` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '1:未使用',
  PRIMARY KEY (`id`)
) COMMENT='目录表' CHARSET utf8 COLLATE utf8_unicode_ci;


ALTER TABLE `erp`.`amazoninfo_ebay_order`   
  CHANGE `fee` `fee` DECIMAL(11,2) NULL;

  
  DROP TABLE IF EXISTS `amazoninfo_vine_fee`;

CREATE TABLE `amazoninfo_vine_fee` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` char(7) COLLATE utf8_unicode_ci DEFAULT NULL,
  `account_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `asin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date_date` date DEFAULT NULL,
  `fee` decimal(11,2) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='Vine FEE表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `amazoninfo_advertising_report`;

CREATE TABLE `amazoninfo_advertising_report` (
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `account_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `data_date` date DEFAULT NULL,
  `currency` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `campaign_name` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ad_group_name` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `advertised_sku` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `advertised_asin` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `impressions` int(11) DEFAULT NULL,
  `clicks` int(11) DEFAULT NULL,
  `ctr` decimal(11,4) DEFAULT NULL,
  `cpc` decimal(11,2) DEFAULT NULL,
  `spend` decimal(11,2) DEFAULT NULL,
  `day_total_sales` decimal(11,2) DEFAULT NULL COMMENT '7',
  `acos` decimal(11,4) DEFAULT NULL,
  `roas` decimal(11,2) DEFAULT NULL,
  `day_total_orders` int(11) DEFAULT NULL COMMENT '7',
  `day_total_units` int(11) DEFAULT NULL COMMENT '7',
  `day_conversion_rate` decimal(11,4) DEFAULT NULL COMMENT '7',
  `day_advertised_sku_units` int(11) DEFAULT NULL COMMENT '7',
  `day_other_sku_units` int(11) DEFAULT NULL COMMENT '7',
  `day_advertised_sku_sales` decimal(11,2) DEFAULT NULL COMMENT '7',
  `day_other_sku_sales` decimal(11,2) DEFAULT NULL COMMENT '7',
  PRIMARY KEY (`id`),
  KEY `dc_idx` (`data_date`,`account_name`)
) COMMENT='adv表' CHARSET utf8 COLLATE utf8_unicode_ci;

/*Data for the table `amazoninfo_advertising_report` */

/*Table structure for table `amazoninfo_searchterms_report` */

DROP TABLE IF EXISTS `amazoninfo_searchterms_report`;

CREATE TABLE `amazoninfo_searchterms_report` (
   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `account_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `data_date` date DEFAULT NULL,
  `currency` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `campaign_name` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ad_group_name` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `customer_search_term` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `keyword` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `match_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `impressions` int(11) DEFAULT NULL,
  `clicks` int(11) DEFAULT NULL,
  `ctr` decimal(11,4) DEFAULT NULL,
  `cpc` decimal(11,2) DEFAULT NULL,
  `spend` decimal(11,2) DEFAULT NULL,
  `day_total_sales` decimal(11,2) DEFAULT NULL COMMENT '7',
  `acos` decimal(11,4) DEFAULT NULL,
  `roas` decimal(11,2) DEFAULT NULL,
  `day_total_orders` int(11) DEFAULT NULL COMMENT '7',
  `day_total_units` int(11) DEFAULT NULL COMMENT '7',
  `day_conversion_rate` decimal(11,4) DEFAULT NULL COMMENT '7',
  `day_advertised_sku_units` int(11) DEFAULT NULL COMMENT '7',
  `day_other_sku_units` int(11) DEFAULT NULL COMMENT '7',
  `day_advertised_sku_sales` decimal(11,2) DEFAULT NULL COMMENT '7',
  `day_other_sku_sales` decimal(11,2) DEFAULT NULL COMMENT '7',
  PRIMARY KEY (`id`),
  KEY `dc_idx` (`data_date`,`account_name`)
) COMMENT='searchterms' CHARSET utf8 COLLATE utf8_unicode_ci;