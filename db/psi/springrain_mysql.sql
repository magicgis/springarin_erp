DROP TABLE IF EXISTS psi_supplier;
CREATE TABLE psi_supplier (
  id 			INT(10)  NOT NULL AUTO_INCREMENT,
  NAME 			VARCHAR(100) 	 NULL,
  nikename 		VARCHAR(100) 	 NULL,
  address 		VARCHAR(500)	 NULL,
  site 			VARCHAR(500) 	 NULL,
  account1 		VARCHAR(500)	 NULL,
  account2 		VARCHAR(500)	 NULL,
  account3 		VARCHAR(500)	 NULL,
  account4 		VARCHAR(500)	 NULL,
  public_account VARCHAR(500)    NULL,
  deposit 		INT(5) 			 NULL,
  contact 		VARCHAR(100)	 NULL,
  phone 		VARCHAR(50) 	 NULL,
  mail 			VARCHAR(100)	 NULL,
  qq 			VARCHAR(50)  	 NULL,
  memo 							 TEXT,
  del_flag 		CHAR(1)          NULL,
  ADDTIME 		INT(10)     	 NULL,
  uptime 		INT(10)     	 NULL,
  userid	    varchar(50) 	 NULL,
  upuserid	    varchar(50) 	 NULL,
  currency_type varchar(10) 	 NULL,
  type          char(1)     	 NULL,
  suffix_name   varchar(200)     NULL,
  short_name    varchar(50)      COMMENT '中文简称',
  pay_mark      varchar(100)     COMMENT '付款备注',
  pay_remark    varchar(100)     COMMENT '付款条款备注',
  pay_type      varchar(2)       COMMENT '付款类型',
  balance_rate1  INT(11)           NULL,
  balance_rate2  INT(11)           NULL,
  balance_delay1 INT(11)           NULL,
  balance_delay2 INT(11)           NULL,
  tax_rate       INT(2)     NULL,
  eliminate char(1)     	 NULL,
  contract_no         varchar(1000)      NULL COMMENT '合同号',
  attchment_path      varchar(1000)      NULL COMMENT '合同附件',
  review_path         varchar(1000)      NULL COMMENT '考核附件',
  PRIMARY KEY (id)
)COMMENT = '供应商' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_supplier_potential;
CREATE TABLE psi_supplier_potential (
  id 			INT(10)  NOT NULL AUTO_INCREMENT,
  NAME 			VARCHAR(100) 	 NULL,
  nikename 		VARCHAR(100) 	 NULL,
  address 		VARCHAR(500)	 NULL,
  site 			VARCHAR(500) 	 NULL,
  account1 		VARCHAR(500)	 NULL,
  account2 		VARCHAR(500)	 NULL,
  account3 		VARCHAR(500)	 NULL,
  account4 		VARCHAR(500)	 NULL,
  deposit 		INT(5) 			 NULL,
  contact 		VARCHAR(100)	 NULL,
  phone 		VARCHAR(50) 	 NULL,
  mail 			VARCHAR(100)	 NULL,
  qq 			VARCHAR(50)  	 NULL,
  memo 							 TEXT,
  del_flag 		CHAR(1)          NULL,
  ADDTIME 		INT(10)     	 NULL,
  uptime 		INT(10)     	 NULL,
  userid	    varchar(50) 	 NULL,
  upuserid	    varchar(50) 	 NULL,
  currency_type varchar(10) 	 NULL,
  type          char(1)     	 NULL,
  suffix_name   varchar(200)     NULL,
  short_name    varchar(50)      COMMENT '中文简称',
  pay_mark      varchar(100)     COMMENT '付款备注',
  create_regular_flag char(1)    COMMENT '生成正式供应商标记',
  PRIMARY KEY (id)
)COMMENT = '潜在供应商' CHARSET utf8 COLLATE utf8_unicode_ci;


 

DROP TABLE IF EXISTS psi_product;
 CREATE TABLE psi_product (
   id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   brand                VARCHAR(255)                   NULL,
   model                VARCHAR(255)                   NULL,
   TYPE                 VARCHAR(255)                   NULL,
   color                VARCHAR(255)                   NULL,
   del_color            VARCHAR(100)                   NULL,
   platform             VARCHAR(255)                   NULL,
   image                VARCHAR(255)                   NULL,
   LENGTH               DECIMAL(11,2)                  NULL,
   width                DECIMAL(11,2)                  NULL,
   height               DECIMAL(11,2)                  NULL,
   weight               DECIMAL(11,2)                  NULL,
   pack_length          DECIMAL(11,2)                  NULL,
   pack_width           DECIMAL(11,2)                  NULL,
   pack_height          DECIMAL(11,2)                  NULL,
   pack_quantity        INT(10)                        NULL,
   box_volume           DECIMAL(11,2)                  NULL,
   gw                   DECIMAL(11,2)                  NULL,
   volume_ratio         DECIMAL(11,2)                  NULL,
   is_sale              CHAR(1)                        NULL,
   is_main              CHAR(1)                        NULL,
   is_new               CHAR(1)                        NULL,
   create_user          VARCHAR(50)                    NULL,
   create_user1         VARCHAR(50)                    NULL,
   create_time          DATETIME                       NULL,
   update_user          VARCHAR(50)                    NULL,
   update_time          DATETIME                       NULL,
   del_flag             CHAR(1)                        NULL,
   description          varchar(5000)                  NULL,
   combination          varchar(255)                   NULL,
   transport_type       int                            NULL,
   produce_period       int                            NULL,
   min_order_placed     int                            NULL,
   product_pack_length  DECIMAL(11,2)                  NULL,
   product_pack_width   DECIMAL(11,2)                  NULL,
   product_pack_height  DECIMAL(11,2)                  NULL,
   product_pack_weight  DECIMAL(11,2)                  NULL,
   remark               varchar(1000)                  NULL,
   file_path            varchar(500)                   NULL,
   added_month          varchar(100)                   NULL,
   eu_hscode 			varchar(100)                   NULL,
   ca_hscode 			varchar(100)                   NULL,
   jp_hscode 			varchar(100)                   NULL,
   us_hscode 			varchar(100)                   NULL,
   hk_hscode 			varchar(100)                   NULL, 
   cn_hscode 			varchar(100)                   NULL, 
   chinese_name 		varchar(200)                   NULL, 
   product_list   		varchar(5000)                  NULL,
   
   has_electric         char(1) 					   NULL,
   has_power char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '是否带电源 0：不带电源 1：带电源',
   tran_report_file     varchar(100)                   NULL,
   certification        varchar(100)                   NULL,
   
   factory_certification  varchar(100)                 NULL,
   inateck_certification  varchar(100)                 NULL,
   
   certification_file   varchar(100)                   NULL,
   price_change_log     varchar(5000)                  NULL,
   eu_custom_duty           DECIMAL(11,3)                  NULL,
   eu_import_duty           DECIMAL(11,3)                  NULL,
   ca_custom_duty           DECIMAL(11,3)                  NULL,
   ca_import_duty           DECIMAL(11,3)                  NULL,
   jp_custom_duty           DECIMAL(11,3)                  NULL,
   jp_import_duty           DECIMAL(11,3)                  NULL,
   us_custom_duty           DECIMAL(11,3)                  NULL,
   us_import_duty           DECIMAL(11,3)                  NULL,
   
   contract_no              varchar(100)                   NULL,
   declare_point   			varchar(100)                   NULL,
   material   			    varchar(2000)                  NULL,
   tax_refund   			    int                        NULL,
   signed_sample            varchar(100)                   NULL,
   check_list               varchar(200)                   NULL,
   improve_remark           varchar(200)                   NULL,
   tech_file                varchar(200)                   NULL,
   has_magnetic             CHAR(1)                        NULL,
   review_sta               CHAR(1)                        NULL,
   model_short                VARCHAR(50)                   NULL,
   components               CHAR(1)                        NULL,
   PRIMARY KEY (id)
)COMMENT = '产品' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_product_tiered_price;
 CREATE TABLE psi_product_tiered_price (
   id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   product_id           INT(11)                  	   NULL,
   color                VARCHAR(255)                   NULL,
   supplier_id          INT(11)                        NULL,
   level                INT(11)                        NULL,
   price                DECIMAL(11,2)                  NULL,
   currency_type        VARCHAR(10)                    NULL,
   create_user			VARCHAR(50)                    NULL,
   create_time          DATETIME                       NULL,
   update_user          VARCHAR(50)                    NULL,
   change_log           VARCHAR(5000)                  NULL,
   update_time          DATETIME                       NULL,
   del_flag             CHAR(1)                        NULL,
   PRIMARY KEY (id)
)COMMENT = '产品阶梯价格' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_product_tiered_price_log;
 CREATE TABLE psi_product_tiered_price_log (
   id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   product_id           INT(11)                  	   NULL,
   supplier_id          INT(11)                        NULL,
   product_name_color   VARCHAR(100)                   NULL,
   content              VARCHAR(1000)                  NULL,
   remark               VARCHAR(1000)                  NULL,
   color                VARCHAR(50)                    NULL,
   create_user			VARCHAR(50)                    NULL,
   create_time          DATETIME                       NULL,
   
   tiered_type          VARCHAR(100)                   NULL,
   currency_type        VARCHAR(100)                   NULL,
   price 				DECIMAL(11,2)              	   NULL,
   old_price 		    DECIMAL(11,2)              	   NULL,
   sure_user			VARCHAR(50)                    NULL,
   sure_time            DATETIME                       NULL,
   PRIMARY KEY (id)
)COMMENT = '产品阶梯价格日志' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_product_tiered_price_review;
 CREATE TABLE psi_product_tiered_price_review (
   id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   product_id           INT(11)                  	   NULL,
   pro_name_color       VARCHAR(100)                   NULL,
   color                VARCHAR(50)                    NULL,
   supplier_id          INT(11)                        NULL,
   nike_name            VARCHAR(100)                   NULL,
   currency_type         VARCHAR(50)                    NULL,
   
   leval500usd 		    DECIMAL(11,2)              	   NULL,
   leval1000usd 		DECIMAL(11,2)              	   NULL,
   leval2000usd 	    DECIMAL(11,2)              	   NULL,
   leval3000usd 		DECIMAL(11,2)              	   NULL,
   leval5000usd 		DECIMAL(11,2)              	   NULL,
   leval10000usd 		DECIMAL(11,2)              	   NULL,
   leval15000usd 		DECIMAL(11,2)              	   NULL,
   leval500cny 		    DECIMAL(11,2)              	   NULL,
   leval1000cny 		DECIMAL(11,2)              	   NULL,
   leval2000cny 	    DECIMAL(11,2)              	   NULL,
   leval3000cny 		DECIMAL(11,2)              	   NULL,
   leval5000cny 		DECIMAL(11,2)              	   NULL,
   leval10000cny 		DECIMAL(11,2)              	   NULL,
   leval15000cny 		DECIMAL(11,2)              	   NULL,
   
   before500usd 		DECIMAL(11,2)              	   NULL,
   before1000usd 		DECIMAL(11,2)              	   NULL,
   before2000usd 	    DECIMAL(11,2)              	   NULL,
   before3000usd 		DECIMAL(11,2)              	   NULL,
   before5000usd 		DECIMAL(11,2)              	   NULL,
   before10000usd 		DECIMAL(11,2)              	   NULL,
   before15000usd 		DECIMAL(11,2)              	   NULL,
   before500cny 		DECIMAL(11,2)              	   NULL,
   before1000cny 		DECIMAL(11,2)              	   NULL,
   before2000cny 	    DECIMAL(11,2)              	   NULL,
   before3000cny 		DECIMAL(11,2)              	   NULL,
   before5000cny 		DECIMAL(11,2)              	   NULL,
   before10000cny 		DECIMAL(11,2)              	   NULL,
   before15000cny 		DECIMAL(11,2)              	   NULL,
   
   content              VARCHAR(1000)                  NULL,
   remark               VARCHAR(1000)                  NULL,
   has_color             CHAR(1)                        NULL,
   review_sta            CHAR(1)                        NULL,
   create_user			VARCHAR(50)                    NULL,
   create_date          DATETIME                       NULL,
   review_user			VARCHAR(50)                    NULL,
   review_date          DATETIME                       NULL,
   cancel_user			VARCHAR(50)                    NULL,
   cancel_date          DATETIME                       NULL,
   file_path             VARCHAR(200)                  NULL,
   PRIMARY KEY (id)
)COMMENT = '产品阶梯价格审核表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE psi_product_supplier (
	 id                   INT(11)                        NOT NULL AUTO_INCREMENT,	
 	 product_id           INT(11)                       ,
 	 supplier_id          INT(11)                       ,
 	 price 				 DECIMAL(11,2)              	NULL,
 	 rmb_price			 DECIMAL(11,2)              	NULL,
 	 remarks             varchar(5000)  ,
 	 PRIMARY KEY (id)
)COMMENT = '产品供应商关联表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_barcode;
CREATE TABLE psi_barcode (
   id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   product_name         VARCHAR(255)                   NULL,
   psi_product        	INT(11)                        NULL,
   product_color        VARCHAR(255)                   NULL,
   product_platform     VARCHAR(255)                   NULL,
   barcode              VARCHAR(255)                   NULL,
   barcode_type         VARCHAR(255)                   NULL,
   last_update_time     DATETIME                       NULL,
   last_update_by       varchar(255)                   null,
   del_flag             char(1)    DEFAULT '0' NOT NULL COMMENT '删除标记',               
   PRIMARY KEY (id)
)COMMENT = '条形码' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_parts;
CREATE TABLE psi_parts (
	  id			    INT(10)   NOT NULL AUTO_INCREMENT,
	  parts_name        VARCHAR(100)      COMMENT '配件名称',
	  parts_type        VARCHAR(50)       COMMENT '配件类型',
	  supplier_id       VARCHAR(100)      COMMENT '配件供应商id',
	  remark            VARCHAR(225)      COMMENT '备注',
	  del_flag			CHAR(1)           COMMENT '删除状态',
	  create_user       VARCHAR(32)       COMMENT '创建人',
	  create_date       DATE              COMMENT '创建时间',
	  update_user       VARCHAR(32)       COMMENT '修改人',
	  update_date       DATE              COMMENT '修改时间',
	  attchment_path    VARCHAR(1000)     COMMENT '合同',
	  image             VARCHAR(200)      COMMENT '图片路径',
	  description       VARCHAR(500)      COMMENT '描述',
	  produce_period    INT               COMMENT '生产周期',
	  mixture_ratio     INT               COMMENT '配比',
	  price             DECIMAL(11,2)     COMMENT '美元价格',
	  rmb_price			DECIMAL(11,2)     COMMENT '人民币价格',
	  moq               INT               COMMENT '最小下单数',
	  price_change_log  VARCHAR(5000)     COMMENT '价格修改备注',
	  PRIMARY KEY (id)
)COMMENT = '配件信息表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_question_barcode;
CREATE TABLE psi_question_barcode (
	  id			    INT(10)   NOT NULL AUTO_INCREMENT,
	  product_id        INT               COMMENT '产品id',
	  product_name      VARCHAR(100)      COMMENT '产品名称',
	  quantity          INT               COMMENT '数量',
	  wrong_side        VARCHAR(100)      COMMENT '错误方',
	  transport_order_no VARCHAR(100)     COMMENT '运单号',
	  reason            VARCHAR(500)      COMMENT '原因',
	  question_date     DATE              COMMENT '问题时间',
	  del_flag			CHAR(1)           COMMENT '删除状态',
	  create_user       VARCHAR(32)       COMMENT '创建人',
	  create_date       DATE              COMMENT '创建时间',
	  update_user       VARCHAR(32)       COMMENT '修改人',
	  update_date       DATE              COMMENT '修改时间',
	  PRIMARY KEY (id)
)COMMENT = '条码贴错问题记录表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_question_supplier;
CREATE TABLE psi_question_supplier (
	  id			    INT(10)   NOT NULL AUTO_INCREMENT,
	  product_id        INT               COMMENT '产品id',
	  supplier_id       INT               COMMENT 'supplierId',
	  question_type     VARCHAR(100)      COMMENT '问题类型',
	  order_no          VARCHAR(100)      COMMENT '采购批次号',
	  event             VARCHAR(500)      COMMENT '事件',
	  consequence       VARCHAR(500)      COMMENT '后果',
	  deal              VARCHAR(500)      COMMENT '处理',
	  punishment        VARCHAR(500)      COMMENT '惩罚',
	  file_path         VARCHAR(500)      COMMENT '附件',
	  product_name 		VARCHAR(1000)     COMMENT '产品名字',
	  question_date     DATE              COMMENT '问题时间',
	  del_flag			CHAR(1)           COMMENT '删除状态',
	  create_user       VARCHAR(32)       COMMENT '创建人',
	  create_date       DATE              COMMENT '创建时间',
	  update_user       VARCHAR(32)       COMMENT '修改人',
	  update_date       DATE              COMMENT '修改时间',
	  result        	VARCHAR(100)      COMMENT '质检结果',
	  PRIMARY KEY (id)
)COMMENT = '供应商异常记录' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_product_parts;
 CREATE TABLE psi_product_parts (
   id                   INT(11)           NOT NULL AUTO_INCREMENT,
   product_id           INT(11)           COMMENT '产品id',
   color                varchar(20)       COMMENT '产品颜色',
   parts_id             INT(11)           COMMENT '配件id', 
   mixture_ratio        INT(11)           COMMENT '配比',
   PRIMARY KEY (id)
)COMMENT = '产品配件关系表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_parts_inventory;
CREATE TABLE psi_parts_inventory (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_id          INT              COMMENT '配件id',
  parts_name        VARCHAR(100)     COMMENT '配件名字',
  po_frozen         INT              COMMENT '冻结数量',
  po_not_frozen     INT              COMMENT '未冻结数量',
  stock_frozen      INT              COMMENT '库存冻结数量',
  stock_not_frozen  INT              COMMENT '库存未冻结数量',
  PRIMARY KEY (id)              
)COMMENT = '配件库存表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_parts_inventory_taking;
CREATE TABLE psi_parts_inventory_taking (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  taking_no         VARCHAR(100)     COMMENT '盘点No',
  taking_type       VARCHAR(100)     COMMENT '盘点类型（盘入或盘出）',
  operate_type      VARCHAR(100)     COMMENT '操作类型',
  remark            VARCHAR(1000)    COMMENT '备注',   
  data_file         VARCHAR(255)     COMMENT 'csv或excel文件',
  origin_name       VARCHAR(100)     COMMENT '原始文件名',
  create_user       VARCHAR(50)      COMMENT '创建人',
  create_date       DATE             COMMENT '创建时间',
  PRIMARY KEY (id)              
)COMMENT = '配件库存盘点表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_parts_inventory_taking_item;
CREATE TABLE psi_parts_inventory_taking_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  taking_id         INT              COMMENT '盘点主id',
  parts_id          INT              COMMENT '配件id',
  parts_name        VARCHAR(100)     COMMENT '配件名字',
  stock_type        VARCHAR(100)     COMMENT '库存类型',
  po_frozen         INT              COMMENT 'poFrozen',
  po_not_frozen     INT              COMMENT 'poNotFrozen',
  stock_frozen      INT              COMMENT 'stockFrozen',
  stock_not_frozen  INT              COMMENT 'stockNotFrozen',
  remark            VARCHAR(1000)    COMMENT '备注',     
  PRIMARY KEY (id)              
)COMMENT = '配件库存盘点明细表' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE IF EXISTS psi_parts_inventory_log;
CREATE TABLE psi_parts_inventory_log (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_id          INT              COMMENT '配件id',
  parts_name        VARCHAR(100)     COMMENT '配件名字',
  quantity          INT              COMMENT '数量',
  data_type         VARCHAR(100)     COMMENT '数据类型',
  operate_type      VARCHAR(100)     COMMENT '操作类型',
  relative_number   VARCHAR(100)     COMMENT '相关单号',
  remark            VARCHAR(100)     COMMENT '备注',
  create_user       VARCHAR(50)      COMMENT '创建人',
  create_date       DATE             COMMENT '创建日期',
  PRIMARY KEY (id)              
)COMMENT = '配件库存日志表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_parts_inventory_out;
CREATE TABLE psi_parts_inventory_out (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  bill_no             VARCHAR(50)    COMMENT  '配件出库单NO',
  product_id          INT            COMMENT  '产品id',
  product_name        VARCHAR(100)   COMMENT  '产品名',
  color               VARCHAR(20)    COMMENT  '产品颜色',
  quantity            INT            COMMENT  '产品数',
  remark              VARCHAR(500)   COMMENT  '备注',
  create_user         VARCHAR(50)    COMMENT  '创建人',
  create_date         DATE           COMMENT  '创建时间',
  PRIMARY KEY (id)              
)COMMENT = '配件出库表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_parts_inventory_out_order;
CREATE TABLE psi_parts_inventory_out_order (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_inventory_out_id  INT        COMMENT  '配件出库单ID',
  purchase_order_id   INT            COMMENT  '采购订单ID',
  purchase_order_no   VARCHAR(100)   COMMENT  '采购订单NO',
  quantity            INT            COMMENT  '产品数',
  PRIMARY KEY (id)              
)COMMENT = '配件出库关联产品订单表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_parts_inventory_out_item;
CREATE TABLE psi_parts_inventory_out_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_inventory_out_id  INT            COMMENT  '配件出库单ID',
  parts_id                INT            COMMENT  '配件id',
  parts_name              VARCHAR(100)   COMMENT  '配件名称',
  quantity                INT            COMMENT  '出库数量',
  PRIMARY KEY (id)              
)COMMENT = '配件库存出库明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_parts_delivery_check;
 CREATE TABLE psi_parts_delivery_check (
   id                     INT(11)       NOT NULL AUTO_INCREMENT,
   purchase_order_id      INT(11)       COMMENT  '采购订单id' ,
   product_id             INT(11)       COMMENT  '产品id',    
   color                  VARCHAR(20)   COMMENT  '产品颜色',
   can_lading_quantity    INT(11)       COMMENT  '可提货数',
   PRIMARY KEY (id)
)COMMENT = '产品配件出库校验表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_parts_order_basis;
CREATE TABLE psi_parts_order_basis (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_id          	  INT              COMMENT '配件id',
  parts_name        	  VARCHAR(100)     COMMENT '配件名字',
  purchase_order_id 	  INT              COMMENT '产品采购订单id',
  purchase_order_no 	  VARCHAR(100)     COMMENT '产品采购订单no',
  need_quantity     	  INT              COMMENT '需要数量',
  order_quantity    	  INT              COMMENT '订单数量',
  supplier_id       	  INT              COMMENT '供应商id',
  moq               	  INT              COMMENT '最小下单数',
  delivery_date    		  DATE             COMMENT '收货日期',
  remark            	  VARCHAR(500)     COMMENT '备注',
  po_frozen         	  INT              COMMENT '冻结数量',
  po_not_frozen     	  INT              COMMENT '未冻结数量',
  stock_frozen      	  INT              COMMENT '库存冻结数量',
  stock_not_frozen  	  INT              COMMENT '库存未冻结数量',
  
  after_po_frozen         INT        	   COMMENT '冻结数量',
  after_po_not_frozen     INT              COMMENT '未冻结数量',
  after_stock_frozen      INT       	   COMMENT '库存冻结数量',
  after_stock_not_frozen  INT        	   COMMENT '库存未冻结数量',
  cancel_sta           	  CHAR(1)          COMMENT '取消状态',
  PRIMARY KEY (id)              
)COMMENT = '配件订单生成依据表' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE IF EXISTS psi_parts_order;
CREATE TABLE psi_parts_order (
	  id 					 INT(10)     NOT NULL AUTO_INCREMENT,
	  parts_order_no   		 VARCHAR(100)      COMMENT '配件订单号',
	  purchase_order_id 	 INT               COMMENT '采购订单id',
	  purchase_order_no		 VARCHAR(50)       COMMENT '采购订单号',
	  supplier_id      		 VARCHAR(100)      COMMENT '供应商id',
	  purchase_date          DATE              COMMENT '采购下单日期',
	  total_amount     		 DECIMAL(11,2)     COMMENT '总金额',
	  payment_amount   		 DECIMAL(11,2)     COMMENT '支付总额',
	  pre_payment_amount     DECIMAL(11,2)     COMMENT '预支付总额',
	  deposit_pre_amount     DECIMAL(11,2)     COMMENT '预支付定金总额',
	  deposit_amount         DECIMAL(11,2)     COMMENT '支付定金总额',
	  currency_type    		 VARCHAR(20)       COMMENT '货币类型',
	  deposit          		 INT               COMMENT '定金比例',
	  order_sta        		 CHAR(1)           COMMENT '订单状态',
	  payment_sta      		 CHAR(1)           COMMENT '支付状态',
	  remark            	 VARCHAR(225)      COMMENT '备注',
	  create_user     		 VARCHAR(32)       COMMENT '创建人',
	  create_date      		 DATE              COMMENT '创建时间',
	  update_user      		 VARCHAR(32)       COMMENT '修改人',
	  update_date      		 DATE              COMMENT '修改时间',
	  sure_user      		 VARCHAR(32)       COMMENT '确认人',
	  sure_date      		 DATE              COMMENT '确认时间',
	  cancel_user      		 VARCHAR(32)       COMMENT '取消人',
	  cancel_date      		 DATE              COMMENT '取消时间',
	  send_eamil             CHAR(1)           COMMENT '是否发送邮件',
	  receive_finished_date  DATE              COMMENT '收货完成时间',
	  pi_file_path           VARCHAR(500)      COMMENT '附件',
	  is_product_receive     CHAR(1)           COMMENT '是否产品收货',
	  pay_item_id            INT               COMMENT '付款明细项' ,
	  PRIMARY KEY (id)
)COMMENT = '配件订单表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_parts_order_item;
CREATE TABLE psi_parts_order_item (
	  id 					INT(10)   NOT NULL AUTO_INCREMENT,
	  parts_id      		INT               COMMENT '配件id',
	  parts_name          	VARCHAR(100)      COMMENT '配件名字',
	  parts_order_id        VARCHAR(100)      COMMENT '配件订单id',
	  quantity_ordered      INT               COMMENT '订单数量',
	  quantity_received     INT               COMMENT '已接收数量',
	  quantity_pre_received INT               COMMENT '预收货数',
	  delivery_date         DATE              COMMENT 'PO交期',
	  actual_delivery_date  DATE              COMMENT '预计交期',
	  remark                VARCHAR(500)      COMMENT '备注',
	  item_price            DECIMAL(11,2)     COMMENT '单价',
	  del_flag              CHAR(1)           COMMENT '删除标记',
	  payment_amount        DECIMAL(11,2)     COMMENT '已付款金额',
  	  quantity_payment      INT               COMMENT '已付款数量',
      PRIMARY KEY (id)                
)COMMENT = '配件订单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_parts_delivery;
CREATE TABLE psi_parts_delivery (
  id INT(10)      			NOT NULL AUTO_INCREMENT,
  bill_no        			VARCHAR(100)      COMMENT '提单编号',
  supplier_id     			VARCHAR(100)      COMMENT '供应商id',
  bill_sta        			VARCHAR(20)       COMMENT '提单状态',
  create_date    			DATETIME          COMMENT '创建时间',
  create_user    			VARCHAR(100)      COMMENT '创建人',
  del_flag        			CHAR(1)           COMMENT '删除标记',
  attchment_path   			VARCHAR(1000)     COMMENT '附件地址',
  remark           			VARCHAR(255)      COMMENT '备注',
  currency_type             VARCHAR(10)       COMMENT '货币类型',
  sure_date       			DATETIME          COMMENT '确认时间',
  sure_user       			VARCHAR(100)      COMMENT '确认人',
  update_date      			DATETIME          COMMENT '修改时间',
  update_user      			VARCHAR(100)      COMMENT '修改人',
  total_payment_amount      DECIMAL(11,2)     COMMENT '已付款金额',
  total_payment_pre_amount  DECIMAL(11,2)     COMMENT '已申请金额',
  total_amount     			DECIMAL(11,2)     COMMENT '总金额',
  cancel_user				VARCHAR(100) 	  COMMENT '取消人',
  cancel_date               DATETIME          COMMENT '取消日期',
  tran_supplier_id          VARCHAR(100)      COMMENT '承运供应商id',
  delivery_date             DATETIME          COMMENT '收货日期',
  PRIMARY KEY (id)
)COMMENT = '提单表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_parts_delivery_item;
CREATE TABLE psi_parts_delivery_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_delivery_id     VARCHAR(100)     COMMENT '提单id',
  parts_order_item_id   INT              COMMENT '订单itemId',
  parts_id              VARCHAR(100)     COMMENT '配件id',
  parts_name        	VARCHAR(100)     COMMENT '配件名字',
  quantity_lading       INT              COMMENT '提单数量',
  item_price            DECIMAL(11,2)    COMMENT '单价',
  del_flag              CHAR(1)          COMMENT '删除标记',
  remark                VARCHAR(255)     COMMENT '备注', 
  PRIMARY KEY (id)                
)COMMENT = '提单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;

 



DROP TABLE IF EXISTS psi_parts_payment;
CREATE TABLE psi_parts_payment (
  id INT(10)       			NOT NULL AUTO_INCREMENT,
  payment_no       			VARCHAR(100)      COMMENT '付款编号',
  supplier_id      			VARCHAR(100)      COMMENT '供应商id',
  currency_type             VARCHAR(10)       COMMENT '货币类型',
  payment_amount_total   	DECIMAL(11,2)     COMMENT '付款总额',
  payment_sta         		VARCHAR(20)       COMMENT '付款状态',
  account_type              VARCHAR(255)      COMMENT '账号信息',
  attchment_path   			VARCHAR(1000)     COMMENT '付款凭证地址',
  remark           			VARCHAR(255)      COMMENT '备注',
  create_date     			DATETIME          COMMENT '创建时间',
  create_user      			VARCHAR(100)      COMMENT '创建人',
  sure_date       			DATETIME          COMMENT '确认时间',
  sure_user        		    VARCHAR(100)      COMMENT '确认人',
  update_date               DATETIME          COMMENT '修改时间',
  update_user               VARCHAR(100)      COMMENT '修改人',
  apply_user				VARCHAR(100) 	  COMMENT '申请人',
  apply_date                DATETIME          COMMENT '申请日期',
  cancel_user				VARCHAR(100) 	  COMMENT '取消人',
  cancel_date               DATETIME          COMMENT '取消日期',
 
  PRIMARY KEY (id)
)COMMENT = '配件订单付款表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_parts_payment_item;
CREATE TABLE psi_parts_payment_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  payment_id          INT              COMMENT '付款单id',
  parts_order_id      INT              COMMENT '订单id',
  parts_delivery_id   INT              COMMENT '提单id',
  bill_no             VARCHAR(50)      COMMENT '订单或提单单号',
  payment_amount   	  DECIMAL(11,2)    COMMENT '付款金额',
  del_flag            CHAR(1)          COMMENT '删除标记',
  payment_type        CHAR(1)          COMMENT '付款类型',
  remark              VARCHAR(255)     COMMENT '备注', 
  PRIMARY KEY (id)              
)COMMENT = '配件订单付款明细表' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE IF EXISTS psi_purchase_amount_adjust;
CREATE TABLE psi_purchase_amount_adjust (
	id INT (10) NOT NULL AUTO_INCREMENT,
	adjust_amount        DECIMAL(11,2)    COMMENT '调整金额',
	supplier_id          INT              COMMENT '供应商id',
	payment_id           INT              COMMENT '付款单id',
    subject              VARCHAR(100)     COMMENT '付款标题',
    currency             VARCHAR(20)      COMMENT '货币类型',
    remark               VARCHAR(100)     COMMENT '备注',
    adjust_sta           CHAR(1)          COMMENT '状态',
    create_date          DATETIME         COMMENT '创建日期', 
    create_user          VARCHAR(50)      COMMENT '申请人',
    update_date          DATETIME         COMMENT '改变日期', 
    update_user          VARCHAR(50)      COMMENT '改变人',
    cancel_date          DATETIME         COMMENT '取消日期', 
    cancel_user          VARCHAR(50)      COMMENT '取消人',
	PRIMARY KEY (id)
)COMMENT = '采购金额调整表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_forecast_order;
CREATE TABLE psi_forecast_order (
  id INT(10)       NOT NULL AUTO_INCREMENT,
  order_sta        CHAR(1)           COMMENT '订单状态',
  change_sta       CHAR(1)           COMMENT 'change状态',
  remark           VARCHAR(500)      COMMENT '备注',
  create_date      DATETIME          COMMENT '创建时间',
  create_user      VARCHAR(100)      COMMENT '更新人',
  update_date      DATETIME          COMMENT '更新时间',
  update_user      VARCHAR(100)      COMMENT '更新人',
  review_date      DATETIME          COMMENT '审核时间',
  review_user      VARCHAR(100)      COMMENT '审核人',
  cancel_date      DATETIME          COMMENT '取消时间',
  cancel_user      VARCHAR(100)      COMMENT '取消人',
  target_date      DATETIME          COMMENT '备货时间',
  type      	   CHAR(1)           COMMENT '1:新品订单 0:非新品订单',
  PRIMARY KEY (id)
)COMMENT = '预测订单表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_forecast_order_item;
CREATE TABLE psi_forecast_order_item (
  id INT(10)   			NOT NULL AUTO_INCREMENT,
  forecast_order_id     INT			      COMMENT '采购订单id',
  supplier_id           INT               COMMENT '供应商ID',
  product_id      		VARCHAR(100)      COMMENT '产品id',
  product_name     		VARCHAR(100)      COMMENT '产品名称',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  country_code          VARCHAR(25)       COMMENT '国家编号',
  quantity              INT               COMMENT '审核下单数量',
  forecast1week         INT               COMMENT '预测下单数量',
  forecast2week         INT               COMMENT '预测第二周下单数量',
  forecast3week         INT               COMMENT '预测第三周下单数量',
  forecast4week         INT               COMMENT '预测第四周下单数量',
  day31sales            INT               COMMENT '31日销',
  by31sales             CHAR(1)           COMMENT '是否依据31天销量',
  by_week               CHAR(1)           COMMENT '用哪周数据', 
  tips                  VARCHAR(500)      COMMENT '提示', 
  remark                VARCHAR(255)      COMMENT '备注', 
  review_remark         VARCHAR(255)      COMMENT '审核备注', 
  last_order_week       VARCHAR(255)      COMMENT '上次下单周', 
  total_stock           INT               COMMENT '总库存数',
  safe_stock            INT               COMMENT '安全库存数',
  period                INT               COMMENT '生产运输缓冲周期',
  forecast1month        INT               COMMENT '第一月销售预测',
  forecast2month        INT               COMMENT '第二月销售预测',
  forecast3month        INT               COMMENT '第三月销售预测',
  forecast4month        INT               COMMENT '第四月销售预测',
  
  promotion_boss_quantity    INT          COMMENT '终极促销数量',
  promotion_quantity    INT               COMMENT '促销数量',
  sale_quantity         INT               COMMENT '销售人员改的数量',
  review_quantity       INT               COMMENT '审核人员改的数量',
  boss_quantity         INT               COMMENT '终极审核人员改的数量',
  purchase_quantity     INT               COMMENT '最终采购数量',
  boss_remark           VARCHAR(500)      COMMENT '终极审核备注', 
  display_sta           CHAR(1)           COMMENT '显示状态', 
  period_buffer         INT               COMMENT '缓冲周期',
  
   PRIMARY KEY (id)                
)COMMENT = '预测订单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_purchase_order;
CREATE TABLE psi_purchase_order (
  id INT(10)       NOT NULL AUTO_INCREMENT,
  order_no         VARCHAR(100)      COMMENT '订单编号',
  supplier_id      VARCHAR(100)      COMMENT '供应商id',
  order_sta        CHAR(1)           COMMENT '订单状态',
  deposit          INT               COMMENT '定金',
  currency_type    VARCHAR(20)       COMMENT '货币类型',
  order_total      DECIMAL(11,2)     COMMENT '总金额',
  purchase_date    DATE              COMMENT '下单日期',
  merchandiser     VARCHAR(100)      COMMENT '跟单员',
  received_store   VARCHAR(100)      COMMENT '收货仓库',
  pi_file_path     VARCHAR(100)      COMMENT 'PI存放路径',
  modify_memo      VARCHAR(255)      COMMENT '修改备注',
  version_no       VARCHAR(20)       COMMENT '版本',
  create_date      DATETIME          COMMENT '创建时间',
  create_user      VARCHAR(100)      COMMENT '创建人',
  del_flag         CHAR(1)           COMMENT '删除标记',
  update_date      DATETIME          COMMENT '更新时间',
  update_user      VARCHAR(100)      COMMENT '更新人',
  sure_date        DATETIME          COMMENT '确认时间',
  sure_user        VARCHAR(100)      COMMENT '确认人',
  pay_item_id      INT               COMMENT '付款明细项' ,
  deposit_amount   DECIMAL(11,2)     COMMENT '已付定金金额' ,
  cancel_user	   VARCHAR(100)      COMMENT '取消人',
  cancel_date      DATETIME          COMMENT '取消日期',
  deposit_pre_amount   DECIMAL(11,2) COMMENT '已申请定金金额' ,
  pay_sta          CHAR(1)           COMMENT '支付状态',
  payment_amount   DECIMAL(11,2)     COMMENT '已支付尾款金额' ,
  send_email_flag  CHAR(1)           COMMENT '是否向产家发送邮件',
  receive_finished_date DATE         COMMENT '收货完成日期',
  to_review        CHAR(1)           COMMENT '发往审核',
  to_parts_order   CHAR(1)           COMMENT '是否进行配件下单',
  offline_sta      CHAR(1)           COMMENT '线下订单',
  is_over_inventory CHAR(1)          COMMENT '是否超了最大库存数',
  over_remark       VARCHAR(500)     COMMENT '超出备注',
  pi_review_sta    CHAR(1)           COMMENT 'PI审核状态',
  PRIMARY KEY (id)
)COMMENT = '采购订单表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_purchase_order_item;
CREATE TABLE psi_purchase_order_item (
  id INT(10)   			NOT NULL AUTO_INCREMENT,
  product_id      		VARCHAR(100)      COMMENT '产品id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  country_code          VARCHAR(25)       COMMENT '国家编号',
  forecast_item_id      INT               COMMENT '预测订单itemId',
  purchase_order_id     INT			      COMMENT '采购订单id',
  quantity_ordered      INT               COMMENT '订单数量',
  quantity_received     INT               COMMENT '已接收数量',
  quantity_pre_received INT               COMMENT '预接收数量',
  item_price            DECIMAL(11,2)     COMMENT '单价',
  payment_amount        DECIMAL(11,2)     COMMENT '已付款金额',
  quantity_payment      INT               COMMENT '已付款数量',
  delivery_date         DATE              COMMENT '交货日期',
  del_flag              CHAR(1)           COMMENT '删除标记',
  remark                VARCHAR(255)      COMMENT '备注', 
  update_date           DATETIME          COMMENT '价格更改日期', 
  supplier_id           INT               COMMENT '供应商ID',
  actual_delivery_date  DATE              COMMENT '实际交货日期',
  forecast_remark       VARCHAR(255)      COMMENT '销售备注', 
  quantity_off_ordered      INT           COMMENT '线下订单数量',
  quantity_off_received     INT           COMMENT '线下已接收数量',
  quantity_off_pre_received INT           COMMENT '线下预接收数量',
  delivery_date_log    DATE               COMMENT '修改记录',
  PRIMARY KEY (id)                
)COMMENT = '采购订单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_purchase_order_delivery_date;
CREATE TABLE psi_purchase_order_delivery_date (
  id INT(10)   			  NOT NULL AUTO_INCREMENT,
  purchase_order_id       INT			    COMMENT '采购订单id',
  purchase_order_item_id  INT			    COMMENT '采购订单明细id',
  product_id      		  INT               COMMENT '产品id',
  product_name            VARCHAR(100)      COMMENT '产品名字',
  color_code              VARCHAR(25)       COMMENT '颜色编号',
  country_code            VARCHAR(25)       COMMENT '国家编号',
  delivery_date           DATE              COMMENT '交货日期',
  quantity     			  INT           	COMMENT '预计收货数',
  quantity_received       INT           	COMMENT '已收货数',
  remark                  VARCHAR(200)      COMMENT '备注',
  del_flag                CHAR(1)           COMMENT '删除状态',
  quantity_off_received   INT               COMMENT '线下接收数',
  quantity_off            INT               COMMENT '线下数',
  delivery_date_log    DATE               COMMENT '修改记录',
   PRIMARY KEY (id)                
)COMMENT = '采购订单预计收货日期表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_his_purchase_order;
CREATE TABLE psi_his_purchase_order (
  id INT(10)       NOT NULL AUTO_INCREMENT,
  order_no         VARCHAR(100)      COMMENT '订单编号',
  supplier_id      VARCHAR(100)      COMMENT '供应商id',
  order_sta        CHAR(1)           COMMENT '订单状态',
  deposit          INT               COMMENT '定金',
  currency_type    VARCHAR(20)       COMMENT '货币类型',
  order_total      DECIMAL(11,2)     COMMENT '总金额',
  purchase_date    DATE              COMMENT '下单日期',
  merchandiser     VARCHAR(100)      COMMENT '跟单员',
  received_store   VARCHAR(100)      COMMENT '收货仓库',
  pi_file_path     VARCHAR(100)      COMMENT 'PI存放路径',
  modify_memo      VARCHAR(255)      COMMENT '修改备注',
  version_no       VARCHAR(20)       COMMENT '版本',
  create_date      DATETIME          COMMENT '创建时间',
  create_user      VARCHAR(100)      COMMENT '创建人',
  del_flag         CHAR(1)           COMMENT '删除标记',
  update_date      DATETIME          COMMENT '更新时间',
  update_user      VARCHAR(100)      COMMENT '更新人',
  sure_date        DATETIME          COMMENT '确认时间',
  sure_user        VARCHAR(100)      COMMENT '确认人',
  deposit_amount   DECIMAL(11,2)     COMMENT '已付定金金额' ,
  cancel_user	   VARCHAR(100)      COMMENT '取消人',
  cancel_date      DATETIME          COMMENT '取消日期',
  deposit_pre_amount   DECIMAL(11,2) COMMENT '已申请定金金额' ,
  pay_sta          CHAR(1)           COMMENT '支付状态',
  payment_amount   DECIMAL(11,2)     COMMENT '已支付尾款金额' ,
  send_email_flag  CHAR(1)           COMMENT '是否向产家发送邮件',
  PRIMARY KEY (id)
)COMMENT = '采购订单快照表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_his_purchase_order_item;
CREATE TABLE psi_his_purchase_order_item (
  id INT(10)   			NOT NULL AUTO_INCREMENT,
  product_id      		VARCHAR(100)      COMMENT '产品id',
  purchase_order_id     VARCHAR(100)      COMMENT '采购订单id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  quantity_ordered      INT               COMMENT '订单数量',
  quantity_received     INT               COMMENT '已接收数量',
  quantity_pre_received INT               COMMENT '预接收数量',
  item_price            DECIMAL(11,2)     COMMENT '单价',
  payment_amount        DECIMAL(11,2)     COMMENT '已付款金额',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  country_code          VARCHAR(25)       COMMENT '国家编号',
  delivery_date         DATE              COMMENT '交货日期',
  del_flag              CHAR(1)           COMMENT '删除标记',
  remark                VARCHAR(255)      COMMENT '备注', 
  update_date           DATETIME          COMMENT '价格更改日期', 
  quantity_payment      INT               COMMENT '已付款数量',
  supplier_id           INT               COMMENT '供应商ID',
  PRIMARY KEY (id)                
)COMMENT = '采购订单快照明细表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_lading_bill;
CREATE TABLE psi_lading_bill (
	  id INT(10)      			NOT NULL AUTO_INCREMENT,
	  bill_no        			VARCHAR(100)      COMMENT '提单编号',
	  supplier_id     			VARCHAR(100)      COMMENT '供应商id',
	  bill_sta        			VARCHAR(20)       COMMENT '提单状态',
	  create_date    			DATETIME          COMMENT '创建时间',
	  create_user    			VARCHAR(100)      COMMENT '创建人',
	  del_flag        			CHAR(1)           COMMENT '删除标记',
	  attchment_path   			VARCHAR(1000)     COMMENT '附件地址',
	  remark           			VARCHAR(255)      COMMENT '备注',
	  currency_type             VARCHAR(10)       COMMENT '货币类型',
	  sure_date       			DATETIME          COMMENT '确认时间',
	  sure_user       			VARCHAR(100)      COMMENT '确认人',
	  update_date      			DATETIME          COMMENT '修改时间',
	  update_user      			VARCHAR(100)      COMMENT '修改人',
	  total_payment_amount      DECIMAL(11,2)     COMMENT '已付款金额',
	  total_payment_pre_amount  DECIMAL(11,2)     COMMENT '已申请金额',
	  total_amount     			DECIMAL(11,2)     COMMENT '已申请金额',
	  no_deposit_amount         DECIMAL(11,2)     COMMENT '无订金总额',
	  cancel_user				VARCHAR(100) 	  COMMENT '取消人',
	  cancel_date               DATETIME          COMMENT '取消日期',
	  tran_supplier_id          VARCHAR(100)      COMMENT '承运供应商id',
	  delivery_date             DATETIME          COMMENT '发货日期',
	  test_date       			DATETIME          COMMENT '质检时间',
	  test_user       			VARCHAR(100)      COMMENT '质检人',
	  PRIMARY KEY (id)
)COMMENT = '提单表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_lading_bill_item;

CREATE TABLE psi_lading_bill_item (
	 	  id INT(10)  NOT NULL AUTO_INCREMENT,
	  lading_bill_id      		VARCHAR(100)     COMMENT '提单id',
	  purchase_order_item_id    INT       		 COMMENT '订单itemId',
	  product_name        		VARCHAR(100)     COMMENT '产品名字',
	  color_code          		VARCHAR(25)      COMMENT '颜色编号',
	  country_code        		VARCHAR(25)      COMMENT '国家编号',
	  quantity_lading      		INT              COMMENT '提单数量',
	  quantity_sure      		INT              COMMENT '确认数量',
	  quantity_spares      		INT              COMMENT '备品数量',
	  del_flag            		CHAR(1)          COMMENT '删除标记',
	  is_pass            		CHAR(1)          COMMENT '通过标记',
	  remark              		VARCHAR(255)     COMMENT '备注', 
	  item_price          		DECIMAL(11,2)    COMMENT '产品单价',
	  sku                 		VARCHAR(200)     COMMENT 'sku',
	  parts_timely_info  		VARCHAR(500)     COMMENT '配件及时信息查询',
	  quantity_off_lading  		INT              COMMENT '线下提货数量',
	  
	  total_payment_amount      DECIMAL(11,2)    COMMENT '已付款金额',
	  total_payment_pre_amount  DECIMAL(11,2)    COMMENT '已申请金额',
	  total_amount     			DECIMAL(11,2)    COMMENT '总金额',
	  balance_rate1  INT(3)      NULL            COMMENT '尾款比例1',
	  balance_rate2  INT(3)      NULL           COMMENT '尾款比例2',
	  balance_delay1 INT(3)      NULL            COMMENT '延迟天数1',
	  balance_delay2 INT(3)      NULL            COMMENT '延迟天数2',
	  his_record VARCHAR(500) NULL  COMMENT '验货记录',
  	  quantity_goods INT(11) NULL  COMMENT '已验收数量',
	  PRIMARY KEY (id)                  
)COMMENT = '提单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE IF EXISTS psi_purchase_payment;
CREATE TABLE psi_purchase_payment (
  id INT(10)       			NOT NULL AUTO_INCREMENT,
  payment_no       			VARCHAR(100)      COMMENT '付款编号',
  supplier_id      			VARCHAR(100)      COMMENT '供应商id',
  payment_amount_total   	DECIMAL(11,2)     COMMENT '付款总额',
  payment_sta         		VARCHAR(20)       COMMENT '付款状态',
  create_date     			DATETIME          COMMENT '创建时间',
  create_user      			VARCHAR(100)      COMMENT '创建人',
  del_flag         			CHAR(1)           COMMENT '删除标记',
  attchment_path   			VARCHAR(1000)     COMMENT '付款凭证地址',
  remark           			VARCHAR(255)      COMMENT '备注',
  sure_date       			DATETIME          COMMENT '确认时间',
  sure_user        		    VARCHAR(100)      COMMENT '确认人',
  update_date               DATETIME          COMMENT '修改时间',
  update_user               VARCHAR(100)      COMMENT '修改人',
  apply_user				VARCHAR(100) 	  COMMENT '申请人',
  apply_date                DATETIME          COMMENT '申请日期',
  cancel_user				VARCHAR(100) 	  COMMENT '取消人',
  cancel_date               DATETIME          COMMENT '取消日期',
  account_type              VARCHAR(255)      COMMENT '账号信息',
  has_adjust                CHAR(1)           COMMENT '有调整项',
  real_payment_amount       DECIMAL(11,2)     COMMENT '真实付款金额',
  currency_type             VARCHAR(20)       COMMENT '货币类型',
  PRIMARY KEY (id)
)COMMENT = '采购付款表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_purchase_payment_item;
CREATE TABLE psi_purchase_payment_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  payment_id          INT              COMMENT '付款单id',
  purchase_order_id   INT              COMMENT '订单id',
  lading_bill_id      INT              COMMENT '提单id',
  payment_type        CHAR(1)          COMMENT '付款类型',
  payment_amount   	  DECIMAL(11,2)    COMMENT '付款金额',
  del_flag            CHAR(1)          COMMENT '删除标记',
  remark              VARCHAR(255)     COMMENT '备注', 
  bill_no             VARCHAR(100)     COMMENT '提单或订单单号',
  lading_item_bill_id      INT        COMMENT '提单itemId',
  PRIMARY KEY (id)              
)COMMENT = '采购付款明细表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_inventory;
CREATE TABLE psi_inventory (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  sku                 VARCHAR(200)     COMMENT 'sku',
  product_id          INT              COMMENT '产品id',
  product_name        VARCHAR(100)     COMMENT '产品名字',
  color_code          VARCHAR(25)      COMMENT '颜色编号',
  country_code        VARCHAR(25)      COMMENT '国家编号',
  new_quantity        INT              COMMENT '新品数量',
  old_quantity        INT              COMMENT '老品数量',
  broken_quantity     INT              COMMENT 'broken数量',
  renew_quantity      INT              COMMENT 'renew数量',
  spares_quantity     INT              COMMENT '备品数量',
  offline_quantity    INT              COMMENT '线下数量',
  warehouse_id        INT              COMMENT '仓库id',
  warehouse_name      VARCHAR(100)     COMMENT '仓库名称',
  update_date         DATETIME         COMMENT '更新时间',
  avg_price           DECIMAL(11,2)     COMMENT '均价',
  remark      VARCHAR(1000)     COMMENT '备注',
  PRIMARY KEY (id)              
)COMMENT = '库存表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_inventory_revision_log;
CREATE TABLE psi_inventory_revision_log (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  sku                        VARCHAR(200)     COMMENT 'sku',
  product_id       		     INT              COMMENT '产品id',
  product_name       		 VARCHAR(100)     COMMENT '产品名字',
  color_code         		 VARCHAR(25)      COMMENT '颜色编号',
  country_code       		 VARCHAR(25)      COMMENT '国家编号',
  warehouse_id 	 	         INT              COMMENT '仓库id',
  quantity           		 INT              COMMENT '操作数量',
  termini_warehouse_id 	 	 INT              COMMENT '目的仓库id',
  termini_warehouse_name	 VARCHAR(100)     COMMENT '目的仓库名称',
  operation_user_id          VARCHAR(100)     COMMENT '操作人id',
  operation_type             VARCHAR(500)     COMMENT '操作类型',
  operation_sta              CHAR(1)		  COMMENT '操作状态',
  operatin_date				 DATETIME         COMMENT '操作日期',
  data_type                  VARCHAR(10)      COMMENT '数据类型（old、new、broken、renew）',
  remark                     VARCHAR(255)     COMMENT '备注',
  relative_number            VARCHAR(100)     COMMENT '相关号',
  timely_quantity            INT              COMMENT '及时库存',
  PRIMARY KEY (id)              
)COMMENT = '库存变化记录' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_inventory_taking_log;
CREATE TABLE psi_inventory_taking_log (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  file_path                   VARCHAR(200)     COMMENT '文件',
  result                     VARCHAR(500)     COMMENT '结果',
  remark                     VARCHAR(500)     COMMENT '备注',
  taking_date				 DATETIME         COMMENT '盘点日期',
  del_flag                   CHAR(1)          COMMENT 'delete',
  create_user                VARCHAR(100)     COMMENT '操作人id',
  create_date				 DATETIME         COMMENT '操作日期',
  PRIMARY KEY (id)              
)COMMENT = '人工库存盘点记录' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_inventory_in;
CREATE TABLE psi_inventory_in (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  bill_no             VARCHAR(50)      COMMENT '入库单号',
  warehouse_id        INT              COMMENT '入库仓库id',
  warehouse_name      VARCHAR(100)     COMMENT '入库仓库名称',
  add_date            DATETIME         COMMENT '入库时间',
  add_user            VARCHAR(100)     COMMENT '入库人',
  attchment_path   	  VARCHAR(255)     COMMENT '凭证地址',
  remark              VARCHAR(500)     COMMENT '备注',
  operation_type      VARCHAR(100)     COMMENT '操作类型',
  source              VARCHAR(100)     COMMENT '来源',
  tran_local_id       INT              COMMENT '运单id',
  tran_local_no       VARCHAR(50)      COMMENT '运单号',
  data_file           VARCHAR(255)     COMMENT 'csv或excel文件',
  origin_name         VARCHAR(100)     COMMENT '原始文件名',
  data_type           VARCHAR(10)      COMMENT '数据类型（old、new、broken、renew）,批量入库时用到',
  data_date            DATETIME        COMMENT '实际入库时间',
  
  tran_man            VARCHAR(50)      COMMENT '送货人姓名',
  phone               VARCHAR(50)      COMMENT '电话',
  car_no              VARCHAR(50)      COMMENT '车牌号',
  flow_no             VARCHAR(50)      COMMENT '流水号',
  PRIMARY KEY (id)              
)COMMENT = '入库表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_inventory_in_item;
CREATE TABLE psi_inventory_in_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  sku                 VARCHAR(200)     COMMENT 'sku',
  inventory_in_id     INT              COMMENT '入库id',
  product_id          INT              COMMENT '产品id',
  product_name        VARCHAR(100)     COMMENT '产品名字',
  color_code          VARCHAR(25)      COMMENT '颜色编号',
  country_code        VARCHAR(25)      COMMENT '国家编号',
  quantity       	  INT              COMMENT '数量',
  quality_type        VARCHAR(10)      COMMENT '质量类型（old、new、broken、renew）',   
  remark              VARCHAR(500)     COMMENT '备注',
  timely_quantity     INT              COMMENT '及时库存',
  bill_item_id        INT              COMMENT '提单itemId',
  price               DECIMAL(11,2)    COMMENT '入库价格',
  PRIMARY KEY (id)              
)COMMENT = '入库明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_inventory_out;
CREATE TABLE psi_inventory_out (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  bill_no             VARCHAR(50)      COMMENT '出库单号',
  warehouse_id        INT              COMMENT '出库仓库id',
  warehouse_name      VARCHAR(100)     COMMENT '出库仓库名称',
  add_date            DATETIME         COMMENT '出库时间',
  add_user            VARCHAR(100)     COMMENT '出库人',
  operation_type      VARCHAR(50)      COMMENT '出库类型',  
  attchment_path   	  VARCHAR(500)     COMMENT '凭证地址',
  tran_fba_id         INT              COMMENT 'FBA单id',
  tran_fba_no         VARCHAR(200)     COMMENT 'FBA单号',
  tran_local_id       VARCHAR(200)     COMMENT '运单id',
  tran_local_no       VARCHAR(200)     COMMENT '运单号',
  remark              VARCHAR(500)     COMMENT '备注',
  whereabouts         VARCHAR(100)     COMMENT '去向',
  data_file           VARCHAR(255)     COMMENT 'csv或excel文件',
  origin_name         VARCHAR(100)     COMMENT '原始文件名',
  data_type           VARCHAR(10)      COMMENT '数据类型（old、new、broken、renew）,批量出库时用到',
  pallet_quantity     INT              COMMENT '托盘数量',
  lading_date         DATETIME         COMMENT '提货时间',
  track_barcode       VARCHAR(50)      COMMENT '跟踪条码',
  supplier            VARCHAR(50)      COMMENT '物流商',
  unline_order_no     VARCHAR(50)      COMMENT '线下订单号',
  pdf_file            VARCHAR(255)     COMMENT 'fba帖pdf文件凭证',
  data_date           DATETIME         COMMENT '实际出库时间',
  
  tran_man            VARCHAR(50)      COMMENT '提货人姓名',
  phone               VARCHAR(50)      COMMENT '电话',
  car_no              VARCHAR(50)      COMMENT '车牌号',
  id_card             VARCHAR(18)      COMMENT '身份证号',
  box_no              VARCHAR(200)     COMMENT '海运集装箱号',
  flow_no             VARCHAR(50)      COMMENT '流水号',
  PRIMARY KEY (id)              
)COMMENT = '出库表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_inventory_out_item;
CREATE TABLE psi_inventory_out_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  sku                 VARCHAR(200)     COMMENT 'sku',
  inventory_out_id    INT              COMMENT '出库id',
  product_id          INT              COMMENT '产品id',
  product_name        VARCHAR(100)     COMMENT '产品名字',
  color_code          VARCHAR(25)      COMMENT '颜色编号',
  country_code        VARCHAR(25)      COMMENT '国家编号',
  quantity       	  INT              COMMENT '数量',
  quality_type        VARCHAR(10)      COMMENT '质量类型（old、new、broken、renew）',   
  remark              VARCHAR(500)     COMMENT '备注',
  timely_quantity     INT              COMMENT '及时库存',
  avg_price        	  DECIMAL(11,2)    COMMENT '均价',
  PRIMARY KEY (id)              
)COMMENT = '出库明细表' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE IF EXISTS psi_transport_order;
CREATE TABLE psi_transport_order (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  transport_no  		VARCHAR(20)      COMMENT '运单号',
  model         		VARCHAR(10)      COMMENT '运输方式',
  ocean_model           VARCHAR(10)      COMMENT '海运模式',
  orgin        			VARCHAR(10)      COMMENT '起运港口',
  destination   		VARCHAR(10)      COMMENT '目的港口',
  from_store            VARCHAR(50)      COMMENT '发货仓库',
  to_store              VARCHAR(50)      COMMENT '目的仓库',
  big_zone       		VARCHAR(10)      COMMENT '大区', 
  box_number    		INT              COMMENT '箱数',    
  weight        		DECIMAL(11,2)    COMMENT '重量',
  volume        		DECIMAL(11,2)    COMMENT '体积',
  teu           		INT              COMMENT '装箱记数',
  local_amount  		DECIMAL(11,2)    COMMENT '本地花费',
  tran_amount           DECIMAL(11,2)    COMMENT '运输费用',
  dap_amount   			DECIMAL(11,2)    COMMENT '国外花费',
  insurance_amount      DECIMAL(11,2)    COMMENT '保费',
  other_amount          DECIMAL(11,2)    COMMENT '其他费用',
  unit_price    		DECIMAL(11,2)    COMMENT '单价',
  duty_taxes         	DECIMAL(11,2)    COMMENT '进口税税金',
  tax_taxes             DECIMAL(11,2)    COMMENT '关税税金',
  pay_amount1           DECIMAL(11,2)    COMMENT '付款1',
  pay_amount2           DECIMAL(11,2)    COMMENT '付款2',
  pay_amount3           DECIMAL(11,2)    COMMENT '付款3',
  pay_amount4           DECIMAL(11,2)    COMMENT '付款4',	
  pay_amount5           DECIMAL(11,2)    COMMENT '付款5',	
  pay_amount6           DECIMAL(11,2)    COMMENT '付款6',	
  vendor1       		INT              COMMENT '承运商1',
  vendor2       		INT              COMMENT '承运商2',
  vendor3       		INT              COMMENT '承运商3',
  vendor4       		INT              COMMENT '承运商4',
  vendor5       		INT              COMMENT '承运商5',
  vendor6       		INT              COMMENT '承运商6',
  currency1     		VARCHAR(10)      COMMENT '货币种类1',
  currency2     		VARCHAR(10)      COMMENT '货币种类2',
  currency3     		VARCHAR(10)      COMMENT '货币种类3',
  currency4     		VARCHAR(10)      COMMENT '货币种类4',
  currency5     		VARCHAR(10)      COMMENT '货币种类5',
  currency6     		VARCHAR(10)      COMMENT '货币种类6',
  rate1                 DECIMAL(11,3)    COMMENT '汇率1',
  rate2                 DECIMAL(11,3)    COMMENT '汇率2',
  rate3                 DECIMAL(11,3)    COMMENT '汇率3',
  rate4                 DECIMAL(11,3)    COMMENT '汇率4',
  etd_date      		DATETIME         COMMENT '离港日期',
  pre_eta_date          DATETIME         COMMENT '预计到港日期',
  eta_date      		DATETIME         COMMENT '到港日期',
  first_eta_date      	DATETIME         COMMENT '(第一次)到港日期',
  delivery_date 		DATETIME         COMMENT '提货日期',
  arrival_date  		DATETIME         COMMENT '到货日期',
  pick_up_date  		DATETIME         COMMENT '提货日期',
  lading_bill_no 		VARCHAR(100)     COMMENT '物流单号', 
  bill_no       		VARCHAR(100)     COMMENT '(财务付款凭证)水单号',
  carrier    		    VARCHAR(100)     COMMENT '飞机/船舶公司',
  remark        		VARCHAR(250)     COMMENT '备注',
  transport_sta 		CHAR(1)          COMMENT '运单状态',
  create_user  			VARCHAR(64)  	 COMMENT '创建人',
  create_date   		DATETIME	 	 COMMENT '创建时间',
  oper_delivery_user	VARCHAR(64)  	 COMMENT '提货人',
  oper_delivery_date	DATETIME     	 COMMENT '提货时间',
  oper_arrival_user		VARCHAR(64) 	 COMMENT '收货操作人',
  oper_arrival_date		DATETIME    	 COMMENT '收货操作时间',
  cancel_user			VARCHAR(64) 	 COMMENT '取消人',
  cancel_date           DATETIME         COMMENT '取消日期',
  packQuantity          INT              COMMENT '装箱数',
  payment_sta           CHAR(1)          COMMENT '付款状态',
  to_country            VARCHAR(10)      COMMENT '去向国',
  plane_num             INT              COMMENT '飞机数量',
  plane_index           INT              COMMENT '收了几个飞机的货',
  fba_timinal_time      DATETIME         COMMENT '排队时间',
  fba_checking_in_time	DATETIME	     COMMENT '收货开始时间',
  fba_closed_time 		DATETIME		 COMMENT '收货结束时间',
  shipment_id           VARCHAR(30)      COMMENT 'shipmentId',
  local_path 			VARCHAR(500)     COMMENT 'local花费凭证',
  dap_path 			    VARCHAR(500)     COMMENT 'dap花费凭证',
  tran_path 			VARCHAR(500)     COMMENT 'tran花费凭证',
  other_path 			VARCHAR(500)     COMMENT 'other花费凭证',
  insurance_path 		VARCHAR(500)     COMMENT 'insurance花费凭证',
  tax_path 			    VARCHAR(500)     COMMENT 'tax花费凭证',
  suffix_name           VARCHAR(100)     COMMENT '后缀名',
  oper_to_port_user	    VARCHAR(64)  	 COMMENT '到港操作人',
  oper_to_port_date	    DATETIME     	 COMMENT '到港操作时间',
  oper_from_port_user	VARCHAR(64)  	 COMMENT '离港操作人',
  oper_from_port_date	DATETIME     	 COMMENT '离港操作时间',
  transport_type        CHAR(1)          COMMENT '运输类型',
  destination_detail    VARCHAR(500)     COMMENT '目的地详细地址',
  else_path             VARCHAR(500)     COMMENT '其他文件',
  unline_order          int              COMMENT '线下订单号',
  oper_arrival_fixed_date	DATE    	 COMMENT '收货操作时间',
  fba_inbound_id        varchar(100)     COMMENT '生成fba贴id',
  is_count              char(1)          COMMENT '0:统计',
  change_record 		varchar(100) 	 COMMENT '收货时间变更记录',
  confirm_pay           CHAR(1)          COMMENT '确认付款',
  
  other_amount1         DECIMAL(11,2)    COMMENT '其他费用1',
  other_taxes           DECIMAL(11,2)    COMMENT '其他税金',
  pay_amount7           DECIMAL(11,2)    COMMENT '付款7',	
  vendor7       		INT              COMMENT '承运商7',
  currency7     		VARCHAR(10)      COMMENT '货币种类7',
  rate7                 DECIMAL(11,3)    COMMENT '汇率7',
  other_path1 			VARCHAR(500)     COMMENT 'other1花费凭证',
  PRIMARY KEY (id)              
)COMMENT = '运单表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_transport_order_replace;
CREATE TABLE psi_transport_order_replace (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  transport_no  		VARCHAR(20)      COMMENT '运单号',
  replace_sta   		CHAR(1)          COMMENT '待发物流状态',
  shipper_info  		VARCHAR(20)      COMMENT '发货人信息',
  orgin        			VARCHAR(10)      COMMENT '起运港口',
  destination   		VARCHAR(10)      COMMENT '目的港口',
  box_number    		INT              COMMENT '箱数',    
  weight        		DECIMAL(11,2)    COMMENT '重量',
  volume        		DECIMAL(11,2)    COMMENT '体积',
  total_amount			DECIMAL(11,2)    COMMENT 'totalAmount',
  etd_date      		DATETIME         COMMENT '离港日期',
  eta_date      		DATETIME         COMMENT '到港日期',
  arrival_date  		DATETIME         COMMENT '到货日期',
  pick_up_date  		DATETIME         COMMENT '提货日期',
  lading_bill_no 		VARCHAR(100)     COMMENT '物流单号', 
  carrier    		    VARCHAR(100)     COMMENT '飞机/船舶公司',
  remark        		VARCHAR(250)     COMMENT '备注',
  create_user  			VARCHAR(64)  	 COMMENT '创建人',
  create_date   		DATETIME	 	 COMMENT '创建时间',
  cancel_user			VARCHAR(64) 	 COMMENT '取消人',
  cancel_date           DATETIME         COMMENT '取消日期',
  
  local_path 			VARCHAR(500)     COMMENT 'local花费凭证',
  dap_path 			    VARCHAR(500)     COMMENT 'dap花费凭证',
  tran_path 			VARCHAR(500)     COMMENT 'tran花费凭证',
  other_path 			VARCHAR(500)     COMMENT 'other花费凭证',
  insurance_path 		VARCHAR(500)     COMMENT 'insurance花费凭证',
  tax_path 			    VARCHAR(500)     COMMENT 'tax花费凭证',
  suffix_name           VARCHAR(100)     COMMENT '后缀名',
  
  vendor1       		INT              COMMENT '承运商1',
  vendor2       		INT              COMMENT '承运商2',
  vendor3       		INT              COMMENT '承运商3',
  vendor4       		INT              COMMENT '承运商4',
  vendor5       		INT              COMMENT '承运商5',
  vendor6       		INT              COMMENT '承运商6',
  
  local_amount  		DECIMAL(11,2)    COMMENT '本地花费',
  tran_amount           DECIMAL(11,2)    COMMENT '运输费用',
  dap_amount   			DECIMAL(11,2)    COMMENT '国外花费',
  insurance_amount      DECIMAL(11,2)    COMMENT '保费',
  other_amount          DECIMAL(11,2)    COMMENT '其他费用',
  duty_taxes         	DECIMAL(11,2)    COMMENT '进口税税金',
  tax_taxes             DECIMAL(11,2)    COMMENT '关税税金',
  
 
  currency1     		VARCHAR(10)      COMMENT '货币种类1',
  currency2     		VARCHAR(10)      COMMENT '货币种类2',
  currency3     		VARCHAR(10)      COMMENT '货币种类3',
  currency4     		VARCHAR(10)      COMMENT '货币种类4',
  currency5     		VARCHAR(10)      COMMENT '货币种类5',
  currency6     		VARCHAR(10)      COMMENT '货币种类6',

  rate1                 DECIMAL(11,3)    COMMENT '汇率1',
  rate2                 DECIMAL(11,3)    COMMENT '汇率2',
  rate3                 DECIMAL(11,3)    COMMENT '汇率3',
  rate4                 DECIMAL(11,3)    COMMENT '汇率4',
  
  
  local_amount_in  		DECIMAL(11,2)    COMMENT '应收本地花费',
  tran_amount_in        DECIMAL(11,2)    COMMENT '应收运输费用',
  dap_amount_in   		DECIMAL(11,2)    COMMENT '应收国外花费',
  insurance_amount_in   DECIMAL(11,2)    COMMENT '应收保费',
  other_amount_in       DECIMAL(11,2)    COMMENT '应收其他费用',
  duty_taxes_in         DECIMAL(11,2)    COMMENT '应收进口税税金',
  tax_taxes_in          DECIMAL(11,2)    COMMENT '应收关税税金',
  
  currency_in1     		VARCHAR(10)      COMMENT '应收货币种类1',
  currency_in2     		VARCHAR(10)      COMMENT '应收货币种类2',
  currency_in3     		VARCHAR(10)      COMMENT '应收货币种类3',
  currency_in4     		VARCHAR(10)      COMMENT '应收货币种类4',
  currency_in5     		VARCHAR(10)      COMMENT '应收货币种类5',
  currency_in6     		VARCHAR(10)      COMMENT '应收货币种类6',

  rate_in1              DECIMAL(11,3)    COMMENT '应收汇率1',
  rate_in2              DECIMAL(11,3)    COMMENT '应收汇率2',
  rate_in3              DECIMAL(11,3)    COMMENT '应收汇率3',
  rate_in4              DECIMAL(11,3)    COMMENT '应收汇率4',
  
  PRIMARY KEY (id)              
)COMMENT = '代发货物流信息' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_transport_order_item;
CREATE TABLE psi_transport_order_item (
  id INT(10)   			NOT NULL AUTO_INCREMENT,
  transport_order_id    INT               COMMENT '运单id',
  product_id      		INT               COMMENT '产品id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  country_code          VARCHAR(25)       COMMENT '国家编号',
  quantity              INT               COMMENT '数量',
  shipped_quantity      INT               COMMENT '发货数量',
  receive_quantity      INT               COMMENT '接收数量',
  item_price            DECIMAL(11,2)     COMMENT '提高10%单价',
  currency              VARCHAR(10)       COMMENT '货币类型',
  del_flag              CHAR(1)           COMMENT '删除标记',
  offline_sta           CHAR(1)           COMMENT '线上线下标记',
  pack_quantity         INT               COMMENT '装箱数量',
  sku                   VARCHAR(200)      COMMENT 'sku',
  remark                VARCHAR(255)      COMMENT '备注', 
  barcode               VARCHAR(255)      COMMENT '条形码', 
  product_price         DECIMAL(11,2)     COMMENT '金额',
  cn_price              DECIMAL(11,2)     COMMENT '出库价',
  PRIMARY KEY (id)                   
)COMMENT = '运单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE `psi_transport_order_item`   
  ADD COLUMN `fba_flag` CHAR(1) NULL  COMMENT '建FBA贴标记 0：否  1：是',
  ADD COLUMN `fba_inbound_id` INT(11) NULL  COMMENT 'psi_fba_inbound ID' ;


DROP TABLE IF EXISTS psi_transport_order_container;
CREATE TABLE psi_transport_order_container (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  transport_order_id  INT              COMMENT '运单id',
  container_type      VARCHAR(10)      COMMENT '集装箱类型',
  quantity            INT              COMMENT '数量',
  item_price          DECIMAL(11,2)    COMMENT '单价',
  remark              VARCHAR(100)     COMMENT '备注',
  del_flag            CHAR(1)          COMMENT '删除',
  PRIMARY KEY (id)              
)COMMENT = '运单海运集装箱信息表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_transport_payment;
CREATE TABLE psi_transport_payment (
  id INT(10)       			NOT NULL AUTO_INCREMENT,
  payment_no       			VARCHAR(100)      COMMENT '付款编号',
  supplier_id      			VARCHAR(100)      COMMENT '供应商id',
  payment_type				VARCHAR(20)       COMMENT '付款种类',
  payment_amount   			DECIMAL(11,2)     COMMENT '付款总额',
  before_amount             DECIMAL(11,2)     COMMENT '转换前总金额',
  currency                  VARCHAR(10)       COMMENT '货币类型',
  payment_sta         		VARCHAR(20)       COMMENT '付款状态',
  attchment_path   			VARCHAR(500)      COMMENT '付款凭证地址',
  supplier_attchment_path	VARCHAR(500)      COMMENT '供应商发票凭证',
  remark           			VARCHAR(255)      COMMENT '备注',
  create_date     			DATETIME          COMMENT '创建时间',
  create_user      			VARCHAR(100)      COMMENT '创建人',
  sure_date       			DATETIME          COMMENT '确认时间',
  sure_user        		    VARCHAR(100)      COMMENT '确认人',
  update_date               DATETIME          COMMENT '修改时间',
  update_user               VARCHAR(100)      COMMENT '修改人',
  apply_user				VARCHAR(100) 	  COMMENT '申请人',
  apply_date                DATETIME          COMMENT '申请日期',
  cancel_user				VARCHAR(100) 	  COMMENT '取消人',
  cancel_date               DATETIME          COMMENT '取消日期',
  account_type              VARCHAR(255)      COMMENT '账号信息',
  rate                      DECIMAL(11,3)     COMMENT '汇率',
  PRIMARY KEY (id)
)COMMENT = '运单付款表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_transport_payment_item;
CREATE TABLE psi_transport_payment_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  payment_id          INT              COMMENT '付款单id',
  tran_order_id   	  INT              COMMENT '运单id',
  payment_type        VARCHAR(30)      COMMENT '付款类型',
  payment_amount   	  DECIMAL(11,2)    COMMENT '付款金额',
  after_amount		  DECIMAL(11,2)    COMMENT '转化后金额',
  currency            VARCHAR(10)      COMMENT '货币种类',
  del_flag            CHAR(1)          COMMENT '删除标记',
  remark              VARCHAR(255)     COMMENT '备注', 
  rate                DECIMAL(11,3)	   COMMENT '汇率',
  transport_no        VARCHAR(100)     COMMENT '运单号',
  PRIMARY KEY (id)              
)COMMENT = '运单付款明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_transport_revise;
CREATE TABLE psi_transport_revise (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  payment_no          VARCHAR(50)      COMMENT '支付编号',
  tran_order_id   	  INT              COMMENT '运单id',
  tran_order_no       VARCHAR(100)     COMMENT '运单号',
  supplier_id         INT              COMMENT '供应商',
  account_type        VARCHAR(500)     COMMENT '账号类型',
  attchment_path      VARCHAR(500)     COMMENT '凭证地址',
  revise_amount       DECIMAL(11,2)    COMMENT '申请修正金额',
  currency            VARCHAR(10)      COMMENT '货币类型',
  remark              VARCHAR(255)     COMMENT '备注',
  apply_user          VARCHAR(64)      COMMENT '申请人',
  apply_date          DATETIME         COMMENT '申请日期',  
  sure_user           VARCHAR(64)      COMMENT '确认人',
  sure_date           DATETIME         COMMENT '确认日期',
  cancel_user         VARCHAR(64)      COMMENT '取消人',
  cancel_date         DATETIME         COMMENT '取消日期',
  revise_sta          CHAR(1)          COMMENT '修正状态',
  rate                DECIMAL(11,3)    COMMENT '汇率',
  account_path        VARCHAR(500)     COMMENT '账单地址',
  PRIMARY KEY (id)              
)COMMENT = '运单付款修正表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_transport_revise_item;
CREATE TABLE psi_transport_revise_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  transport_revise_id INT              COMMENT '补差主表id',
  old_amount		  DECIMAL(11,2)    COMMENT '原金额',
  revise_type         VARCHAR(20)      COMMENT '修正类型 localAmount、tranAmount',
  revise_amount       DECIMAL(11,2)    COMMENT '修正金额',
  currency            VARCHAR(10)      COMMENT '货币类型',  
  rate                DECIMAL(11,3)    COMMENT '汇率',
  remark              VARCHAR(255)     COMMENT '备注',
  PRIMARY KEY (id)              
)COMMENT = '运单付款修正item表' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE IF EXISTS psi_inventory_fba;
CREATE TABLE psi_inventory_fba(
  id INT(10)  NOT NULL,
  sku varchar(100) ,	 
  asin varchar(20) ,
  fnsku varchar(20) ,
  fulfillable_quantity int,
  unsellable_quantity int,
  reserved_quantity int,
  warehouse_quantity int,
  transit_quantity int,
  total_quantity int,
  orrect_quantity int,
  country varchar(5),
  last_update_date datetime,
  data_date datetime,
  PRIMARY KEY (id)              
)COMMENT = 'FBA库存表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_sku_change_bill;
CREATE TABLE psi_sku_change_bill (
	id INT (10) NOT NULL AUTO_INCREMENT,
	even_name            VARCHAR(100)     COMMENT '事件名字',
    warehouse_id   	     INT              COMMENT '仓库id',
    warehouse_name       VARCHAR(100)     COMMENT '仓库名',
    product_id     		 INT              COMMENT '产品id',
    product_name    	 VARCHAR(100)     COMMENT '产品名',
    product_country      VARCHAR(10)      COMMENT '产品国家',
    product_color        VARCHAR(50)      COMMENT '产品颜色',
    from_sku             VARCHAR(100)     COMMENT '从sku',
    to_sku               VARCHAR(100)     COMMENT '到sku',
    batch_number         VARCHAR(20)      COMMENT '调换批次号',
    quantity             INT              COMMENT '数量',
    remark               VARCHAR(100)     COMMENT '备注',
    change_sta           CHAR(1)          COMMENT '状态',
    shippment_id         VARCHAR(100)     COMMENT 'shippmentId',
    apply_date           DATETIME         COMMENT '申请日期', 
    apply_user           VARCHAR(50)      COMMENT '申请人',
    sure_date            DATETIME         COMMENT '确认日期', 
    sure_user            VARCHAR(50)      COMMENT '确认人',
    cancel_date          DATETIME         COMMENT '取消日期', 
    cancel_user          VARCHAR(50)      COMMENT '取消人',
	PRIMARY KEY (id)
)COMMENT = 'sku调换清单' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_sku_change_bill_item;
CREATE TABLE psi_sku_change_bill_item (
	id INT (10) NOT NULL AUTO_INCREMENT,
	sku_change_bill_id	INT              COMMENT '主表id',
	sku             	VARCHAR(100)     COMMENT 'sku',
	quantity        	INT              COMMENT '数量',
	remark              VARCHAR(100)     COMMENT '备注',
	PRIMARY KEY (id)
)COMMENT = 'sku调换清单确认详情' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_quality_change_bill;
CREATE TABLE psi_quality_change_bill (
	id INT (10) NOT NULL AUTO_INCREMENT,
    warehouse_id   	     INT              COMMENT '仓库id',
    batch_number         VARCHAR(20)      COMMENT '调换批次号',
    change_type          VARCHAR(100)     COMMENT 'changeType',
    sku                  VARCHAR(100)     COMMENT 'sku',
    quantity             INT              COMMENT '数量',
    remark               VARCHAR(100)     COMMENT '备注',
    change_sta           CHAR(1)          COMMENT '状态',
    apply_date           DATETIME         COMMENT '申请日期', 
    apply_user           VARCHAR(50)      COMMENT '申请人',
    sure_date            DATETIME         COMMENT '确认日期', 
    sure_user            VARCHAR(50)      COMMENT '确认人',
    cancel_date          DATETIME         COMMENT '取消日期', 
    cancel_user          VARCHAR(50)      COMMENT '取消人',
    unline_order_id      INT              COMMENT '线下订单Id',
    unline_order_no      VARCHAR(100)     COMMENT '线下订单No',
    product_id           INT              COMMENT '产品Id',
    product_name         VARCHAR(100)     COMMENT '产品名',
    product_country      VARCHAR(100)     COMMENT '产品国家',
    product_color        VARCHAR(100)     COMMENT '产品颜色',
	PRIMARY KEY (id)
)COMMENT = '库存新转线下' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_quality_change_bill_item;
CREATE TABLE psi_quality_change_bill_item (
	id INT (10) NOT NULL AUTO_INCREMENT,
	quality_change_bill_id	INT              COMMENT '主表id',
	sku             		VARCHAR(100)     COMMENT 'sku',
	quantity        		INT              COMMENT '数量',
	remark              	VARCHAR(100)     COMMENT '备注',
	PRIMARY KEY (id)
)COMMENT = '库存新转线下确认详情' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE IF EXISTS psi_purchase_financial_report;
CREATE TABLE psi_purchase_financial_report (
	id INT (10) NOT NULL AUTO_INCREMENT,
	supplier_id			  INT              COMMENT '供应商id',
	product_name_color    VARCHAR(100)     COMMENT '产品名+颜色',
	month                 VARCHAR(10)      COMMENT '月份',
	order_amount          DECIMAL(11,2)    COMMENT '订单金额',
	pay_order_amount      DECIMAL(11,2)    COMMENT '定金支付金额',
	pay_lading_amount     DECIMAL(11,2)    COMMENT '尾款支付金额',
	data_date             DATETIME         COMMENT '数据产生日期',            
	PRIMARY KEY (id)
)COMMENT = '按月按产品统计金额' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_purchase_forecast_report;
CREATE TABLE psi_purchase_forecast_report (
	id INT (10) NOT NULL AUTO_INCREMENT,
	supplier_id			  INT              COMMENT '供应商id',
	product_name_color    VARCHAR(100)     COMMENT '产品名+颜色',
	month                 VARCHAR(10)      COMMENT '月份',
	order_amount          DECIMAL(11,2)    COMMENT '预测订单金额',
	deposit_amount        DECIMAL(11,2)    COMMENT '预测定金金额',
	lading_amount         DECIMAL(11,2)    COMMENT '预测尾款金额',
	balance_lading_amount DECIMAL(11,2)    COMMENT '预测剩余尾款',
	data_date             DATETIME         COMMENT '数据产生日期',            
	PRIMARY KEY (id)
)COMMENT = '采购预测统计金额' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_product_group_user`;
CREATE TABLE `psi_product_group_user` ( 
  id INT (10) 	 NOT NULL   AUTO_INCREMENT,
  product_group_id	    VARCHAR(32)   COMMENT    '产品线组id',
  country	     		VARCHAR(10)   COMMENT    'country',
  responsible	     	VARCHAR(32)   COMMENT    '用户id',
  create_user	     	VARCHAR(32)   COMMENT    '用户id',
  create_time           DATETIME      COMMENT    '创建日期',  
  del_flag	     		CHAR(1)       COMMENT    '删除标记',
  PRIMARY KEY (`id`)
) COMMENT = '产品线用户关系表' CHARSET utf8 COLLATE utf8_unicode_ci;

create table `psi_stock` (
	`id` int (10) NOT NULL AUTO_INCREMENT,
	`name` varchar (150),
	`address_line1` varchar (540),
	`address_line2` varchar (180),
	`city` varchar (90),
	`districtorcounty` varchar (75),
	`stateorprovincecode` varchar (150),
	`countrycode` varchar (60),
	`postalcode` varchar (90),
	`platform` varchar (30),
	`stock_sign` varchar (90),
	`stock_name` varchar (765),
	`type` char (3),
	`remarks` varchar (765),
	`create_by` varchar (150),
	`update_by` varchar (150),
	`create_date` datetime ,
	`update_date` datetime ,
	`del_flag` char (3),
	`capacity` DECIMAL(11,2),
	PRIMARY KEY (id)
)COMMENT = '仓库表' CHARSET utf8 COLLATE utf8_unicode_ci;




CREATE TABLE `psi_sku` (
	id INT (10) NOT NULL AUTO_INCREMENT,
	barcode INT ,
	use_barcode CHAR(1),
	sku VARCHAR(200) ,
	country VARCHAR(10) ,
	color VARCHAR(100),
	product_name VARCHAR(200) ,
	del_flag CHAR(1),
	product_id int,
	update_user    VARCHAR(50) ,
	PRIMARY KEY (id)
)COMMENT = 'sku产品映射表' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE `psi_fba_inbound` (

	id INT (10) NOT NULL AUTO_INCREMENT,
	
	shipment_id VARCHAR(100),

	 shipment_name VARCHAR(200),

	 ship_from_address VARCHAR(100),

	 destination_fulfillment_center_id VARCHAR(100),

	 shipment_status VARCHAR(100),

	label_prep_type VARCHAR(100),

	 are_cases_required char(1),
	
	create_by VARCHAR(100),
	
	last_update_by VARCHAR(100),
	
	create_date datetime,
	
	arrival_date datetime,
	
	last_update_date datetime,

	proess_status VARCHAR(300),
	
	shipped_date datetime,
	
	country VARCHAR(10),

	fee decimal(11,2),
	
	tray  int ,

	dhl_tracking VARCHAR(100),
	
	delivery_date datetime,
	supplier varchar (50),
	file_path varchar (200),
	amz_reference_id varchar (20),
	`count_flag` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL,
	PRIMARY KEY (id)
)COMMENT = 'fba贴' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE `psi_fba_inbound_item` (

	id INT (10) NOT NULL AUTO_INCREMENT,
	
	sku  VARCHAR(200),

	fn_sku  VARCHAR(100),
	
	seller_shipped  int ,

	remark varchar(1000) ,

	quantity_shipped int(10),

	 quantity_received int(10),

	quantity_in_case int(10),
	
	fba_inbound_id int(10),
	
	flag char(1) ,
	
	PRIMARY KEY (id)
)COMMENT = 'fba贴项' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE `psi_product_variance` (

	id INT (10) NOT NULL AUTO_INCREMENT,
	
	product_name varchar(255),
	
	country varchar(10),
	
	day31_sales int ,
	
	day_period_sales int,
	
	
	real_day31_sales int ,
	
	real_day_period_sales int,
	
	variance  DECIMAL(11,2) ,
	
	period_sqrt  DECIMAL(11,2),
	
	period int,
	
	sampling_data varchar(1000),
	
	forecast_preiod_avg DECIMAL(11,2),
	
	forecast_after_preiod_sales_by_month DECIMAL(11,2),
	
	PRIMARY KEY (id)
)COMMENT = '方差数据表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `psi_product_type_dict`;
CREATE TABLE `psi_product_type_dict` (
  `id` VARCHAR(64) COLLATE utf8_unicode_ci NOT NULL,
  `name` VARCHAR(100) COLLATE utf8_unicode_ci NOT NULL,
  `create_by` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `parent_id` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT '0',
  `del_flag` CHAR(1) COLLATE utf8_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) COMMENT = '分组表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `psi_product_type_group`;
CREATE TABLE `psi_product_type_group` (
  `id` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dict_id` VARCHAR(64) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`dict_id`)
) COMMENT = '分组关系表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS `psi_product_in_stock`;
CREATE TABLE `psi_product_in_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) DEFAULT NULL COMMENT '产品名称_颜色',
  `country` varchar(10) DEFAULT NULL COMMENT '国家',
  `producting` int(10) DEFAULT NULL COMMENT '在产数量',
  `cn` int(10) DEFAULT NULL COMMENT '中国仓数量',
  `transit` int(10) DEFAULT NULL COMMENT '在途数量',
  `day31_sales` int(10) DEFAULT NULL COMMENT '31天销量',
  `total_stock` int(10) DEFAULT NULL COMMENT '总库存',
  `day_sales` decimal(11,2) DEFAULT NULL COMMENT '采购期预日销',
  `month_sales` decimal(11,2) DEFAULT NULL COMMENT '采购期预月销',
  `safe_inventory` decimal(11,2) DEFAULT NULL COMMENT '安全库存量',
  `safe_day` int(10) DEFAULT NULL COMMENT '安全库存可销天数',
  `order_point` decimal(11,2) DEFAULT NULL COMMENT '下单点',
  `balance` decimal(11,2) DEFAULT NULL COMMENT '结余数',
  `inventory_day` decimal(11,2) DEFAULT NULL COMMENT '库存可销天数',
  `order_quantity` int(10) DEFAULT NULL COMMENT '下单量',
  `air_replenishment` int(10) DEFAULT NULL COMMENT '空运补货量',
  `period` int(10) DEFAULT NULL COMMENT '周期',
  `data_date` timestamp NULL DEFAULT NULL COMMENT '数据入库时间',
  `overseas` int(10) DEFAULT NULL COMMENT '海外仓(实)',
  `sku` varchar(200) DEFAULT NULL COMMENT 'sku',
  `barcode` varchar(255) DEFAULT NULL,
  `last_update_by` varchar(255) DEFAULT NULL,
  `price` decimal(11,2) DEFAULT NULL COMMENT '美金价格',
  `rmb_price` decimal(11,2) DEFAULT NULL COMMENT '人民币价格',
  `is_sale` char(1) DEFAULT NULL COMMENT '是否在售 0：淘汰 1：在售',
  `is_new` char(1) DEFAULT NULL COMMENT '是否新品 0：普通 1：新品',
  `is_main` char(1) DEFAULT NULL COMMENT '是否主力 0：普通 1：主力',
  `inventory_sale_month` decimal(11,2) DEFAULT 0 COMMENT '库存可销月数',
  `cn_lc` int(10) DEFAULT NULL COMMENT '中国仓(理诚)数量',
  `sale_user` VARCHAR(20) DEFAULT NULL  COMMENT '运营',
  `cameraman` VARCHAR(20) DEFAULT NULL  COMMENT '摄影师',
  `purchase_user` VARCHAR(20) DEFAULT NULL  COMMENT '采购经理',
  `customer` VARCHAR(20) DEFAULT NULL  COMMENT '客服',
  `merchandiser` VARCHAR(20) DEFAULT NULL  COMMENT '跟单员',
  `product_manager` VARCHAR(20) DEFAULT NULL  COMMENT '产品经理',
  `turnover_standard` decimal(11,2) DEFAULT NULL  COMMENT '周转率标准',
  `recall` int(11) DEFAULT NULL  COMMENT '召回在途数',
  PRIMARY KEY (`id`)
) COMMENT='产品历史库存表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_product_attribute`;
CREATE TABLE `psi_product_attribute` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL COMMENT '产品id',
  `product_name` VARCHAR(50) COLLATE utf8_unicode_ci NOT NULL,
  `color` VARCHAR(20) COLLATE utf8_unicode_ci NOT NULL,
  `quantity` int(11) DEFAULT NULL COMMENT '最大库存数',
  `create_user` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` DATETIME DEFAULT NULL,
  `is_main` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '主力 0：普通  1：主力',
  `transport_type` int(11) DEFAULT NULL COMMENT '1:海运 2：空运',
  `buffer_period` int(11) DEFAULT NULL COMMENT '缓冲周期',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记 0：正常 1：删除',
  `inventory_sale_month` decimal(11,2) DEFAULT 0 COMMENT '库存可销月数',
  `unshelve_date` DATE DEFAULT 0 COMMENT '下架时间',
   moq_price           DECIMAL(11,2)     COMMENT '价格',
   stock_rate1         DECIMAL(11,2)     COMMENT '1月库存系数',
   stock_rate2         DECIMAL(11,2)     COMMENT '2月库存系数',
   stock_rate3         DECIMAL(11,2)     COMMENT '3月库存系数',
   stock_rate4         DECIMAL(11,2)     COMMENT '4月库存系数',
   stock_rate5         DECIMAL(11,2)     COMMENT '5月库存系数',
   stock_rate6         DECIMAL(11,2)     COMMENT '6月库存系数',
   stock_rate7         DECIMAL(11,2)     COMMENT '7月库存系数',
   stock_rate8         DECIMAL(11,2)     COMMENT '8月库存系数',
   stock_rate9         DECIMAL(11,2)     COMMENT '9月库存系数',
   stock_rate10        DECIMAL(11,2)     COMMENT '10月库存系数', 
   stock_rate11        DECIMAL(11,2)     COMMENT '11月库存系数',
   stock_rate12        DECIMAL(11,2)     COMMENT '12月库存系数',
   `currency_type` VARCHAR(20) COLLATE utf8_unicode_ci DEFAULT NULL,
   `cameraman` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uinque` (`product_id`,`product_name`,`color`)
) COMMENT = '产品属性表(分颜色不分平台)' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_out_of_stock_info`;
CREATE TABLE `psi_out_of_stock_info` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名字',
  `color` VARCHAR(25) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '颜色编号',
  `country` VARCHAR(25) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家编号',
  `fba_quantity` INT(11) DEFAULT NULL COMMENT 'fba数量',
  `create_date` DATETIME DEFAULT NULL COMMENT '时间',
  `quantity_day31` INT(11) DEFAULT NULL,
  `actual_date` DATETIME DEFAULT NULL,
  `before_price` DECIMAL(11,2) DEFAULT NULL,
  `after_price` DECIMAL(11,2) DEFAULT NULL,
  `sku` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  info1 varchar(1000)  COLLATE utf8_unicode_ci DEFAULT NULL,
  info2 varchar(1000)  COLLATE utf8_unicode_ci DEFAULT NULL,
  info3 varchar(1000)  COLLATE utf8_unicode_ci DEFAULT NULL,
  info4 varchar(1000)  COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT='断货提价表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `psi_product_eliminate`;
CREATE TABLE `psi_product_eliminate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL COMMENT '产品id',
  `product_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名称',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `color` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '颜色',
  `is_sale` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '是否淘汰1：未淘汰 0：已淘汰',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '删除标记',
  `is_main` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '主力',
  `is_new` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '新品',
  `added_month` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '上架时间',
  `sales_forecast_scheme` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '销售预测方案',
  `eliminate_time` date COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '淘汰时间',
  `off_website` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '是否官网自动下架',
  `pi_price` decimal(11,2) DEFAULT NULL COMMENT '进口金额',
  `cnpi_price` decimal(11,2) DEFAULT NULL COMMENT '出口价格($)',
   `fba_fee` decimal(11,2) DEFAULT NULL COMMENT 'fba费用($)',
   `fba_fee_eu` decimal(11,2) DEFAULT NULL COMMENT 'cross费用($)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uinque` (`product_id`,`product_name`,`country`,`color`)
) COMMENT='产品淘汰明细表' CHARSET=utf8 COLLATE=utf8_unicode_ci ;
 <!--初始化产品淘汰明细表数据-->
INSERT INTO `psi_product_eliminate` (product_id,product_name,country,color,is_sale,del_flag) 
SELECT id,p.proName,
SUBSTRING_INDEX(SUBSTRING_INDEX(p.platform,',',c.help_topic_id+1),',',-1) AS country, p.color,p.sale AS is_sale,p.flag AS del_flag FROM (
SELECT id,CONCAT(brand,' ',model) proName,
CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' 
THEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1) ELSE '' END AS color,
 a.platform AS platform,a.is_sale AS sale,a.del_flag AS flag
 FROM psi_product a JOIN mysql.help_topic b 
 ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1)) AS p
 JOIN mysql.help_topic c 
 ON c.help_topic_id < (LENGTH(p.platform) - LENGTH(REPLACE(p.platform,',',''))+1)
 ON DUPLICATE KEY UPDATE is_sale=VALUES(is_sale) AND del_flag=VALUES(del_flag);
 
 
 DROP TABLE IF EXISTS `psi_product_hscode_detail`;

CREATE TABLE `psi_product_hscode_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `eu_hscode` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `ca_hscode` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `jp_hscode` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `us_hscode` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `hk_hscode` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cn_hscode` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='HSCODE历史记录' CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `psi_inventory_gap`;
CREATE TABLE `psi_inventory_gap` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name_color` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `week1` int(11) DEFAULT '0',
  `week2` int(11) DEFAULT '0',
  `week3` int(11) DEFAULT '0',
  `week4` int(11) DEFAULT '0',
  `week5` int(11) DEFAULT '0',
  `week6` int(11) DEFAULT '0',
  `week7` int(11) DEFAULT '0',
  `week8` int(11) DEFAULT '0',
  `week9` int(11) DEFAULT '0',
  `week10` int(11) DEFAULT '0',
  `week11` int(11) DEFAULT '0',
  `week12` int(11) DEFAULT '0',
  `week13` int(11) DEFAULT '0',
  `week14` int(11) DEFAULT '0',
  `week15` int(11) DEFAULT '0',
  `week16` int(11) DEFAULT '0',
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `forecast_type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:销量预测 1:周日销',
  `type` varchar(5) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT='缺口预计表' CHARSET=utf8 COLLATE=utf8_unicode_ci;
 

DROP TABLE IF EXISTS `psi_product_transport_rate`;

CREATE TABLE `psi_product_transport_rate` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名字',
  `country` VARCHAR(25) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家编号',
  `sea_rate` DECIMAL(11,2) DEFAULT NULL,
  `air_rate` DECIMAL(11,2) DEFAULT NULL,
  `express_rate` DECIMAL(11,2) DEFAULT NULL,
  `sea_price` DECIMAL(11,2) DEFAULT NULL,
  `air_price` DECIMAL(11,2) DEFAULT NULL,
  `express_price` DECIMAL(11,2) DEFAULT NULL,
  `update_date` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = '物流比例' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_product_avg_price`;

CREATE TABLE `psi_product_avg_price` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名字',
  `country` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家编号',
  `avg_price` decimal(11,2) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = '物流平均价格' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE IF EXISTS `psi_forecast_transport_order`;
CREATE TABLE `psi_forecast_transport_order` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `order_sta` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '订单状态',
  `remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `cancel_date` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_user` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '取消人',
  `review_date` datetime DEFAULT NULL COMMENT '审核日期',
  `review_user` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '审核人',
  PRIMARY KEY (`id`)
)COMMENT = '运单审核表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_forecast_transport_order_item`;
CREATE TABLE `psi_forecast_transport_order_item` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `forecast_order_id` int(11) DEFAULT NULL,
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color_code` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country_code` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `review_remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '审批备注',
  `safe_stock` int(11) DEFAULT NULL COMMENT '安全库存',
  `amazon_stock` int(11) DEFAULT NULL COMMENT '亚马逊仓',
  `day31sales` int(11) DEFAULT NULL COMMENT '31日销',
  `quantity` int(11) DEFAULT NULL COMMENT '系统数量',
  `check_quantity` int(11) DEFAULT NULL COMMENT '审核数量',
  `model` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '空海运',
  `transport_type` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '运输方式',
  `display_sta` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `box_num` int(11) DEFAULT NULL,
  `detail` varchar(600) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sku` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
   `total_stock` int(11) DEFAULT NULL COMMENT '总库存',
    `sales_day` int(11) DEFAULT NULL COMMENT '可售天',
  PRIMARY KEY (`id`)
)COMMENT = '运单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_product_group_customer`;
CREATE TABLE `psi_product_group_customer` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `line_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品线',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'country',
  `user_id` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '用户id组',
  `create_user` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`)
)COMMENT = '产品线关系客服表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `psi_product_group_customer_email`;
CREATE TABLE `psi_product_group_customer_email` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `line_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品线',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'country',
  `user_id` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '用户id组',
  `create_user` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`)
)COMMENT = '产品线关系客服邮件表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `psi_product_group_photo`;
CREATE TABLE `psi_product_group_photo` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `line_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品线',
  `user_id` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '用户id组',
  `create_user` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`)
)COMMENT = '产品线关系摄影师表' CHARSET utf8 COLLATE utf8_unicode_ci;

//理诚erp切换

CREATE TABLE lc_psi_purchase_order (
  id INT(10)       NOT NULL AUTO_INCREMENT,
  order_no         VARCHAR(100)      COMMENT '订单编号',
  supplier_id      VARCHAR(100)      COMMENT '供应商id',
  order_sta        CHAR(1)           COMMENT '订单状态',
  deposit          INT               COMMENT '定金',
  currency_type    VARCHAR(20)       COMMENT '货币类型',
  order_total      DECIMAL(11,2)     COMMENT '总金额',
  purchase_date    DATE              COMMENT '下单日期',
  merchandiser     VARCHAR(100)      COMMENT '跟单员',
  received_store   VARCHAR(100)      COMMENT '收货仓库',
  pi_file_path     VARCHAR(100)      COMMENT 'PI存放路径',
  modify_memo      VARCHAR(255)      COMMENT '修改备注',
  version_no       VARCHAR(20)       COMMENT '版本',
  create_date      DATETIME          COMMENT '创建时间',
  create_user      VARCHAR(100)      COMMENT '创建人',
  del_flag         CHAR(1)           COMMENT '删除标记',
  update_date      DATETIME          COMMENT '更新时间',
  update_user      VARCHAR(100)      COMMENT '更新人',
  sure_date        DATETIME          COMMENT '确认时间',
  sure_user        VARCHAR(100)      COMMENT '确认人',
  pay_item_id      INT               COMMENT '付款明细项' ,
  deposit_amount   DECIMAL(11,2)     COMMENT '已付定金金额' ,
  cancel_user	   VARCHAR(100)      COMMENT '取消人',
  cancel_date      DATETIME          COMMENT '取消日期',
  deposit_pre_amount   DECIMAL(11,2) COMMENT '已申请定金金额' ,
  pay_sta          CHAR(1)           COMMENT '支付状态',
  payment_amount   DECIMAL(11,2)     COMMENT '已支付尾款金额' ,
  send_email_flag  CHAR(1)           COMMENT '是否向产家发送邮件',
  receive_finished_date DATE         COMMENT '收货完成日期',
  to_review        CHAR(1)           COMMENT '发往审核',
  to_parts_order   CHAR(1)           COMMENT '是否进行配件下单',
  offline_sta      CHAR(1)           COMMENT '线下订单',
  is_over_inventory CHAR(1)          COMMENT '是否超了最大库存数',
  over_remark       VARCHAR(500)      COMMENT '超出备注',
  pi_review_sta    CHAR(1)           COMMENT 'PI审核状态',s
  PRIMARY KEY (id)
)COMMENT = '采购订单表' CHARSET utf8 COLLATE utf8_unicode_ci;



CREATE TABLE lc_psi_purchase_order_item (
  id INT(10)   			NOT NULL AUTO_INCREMENT,
  product_id      		VARCHAR(100)      COMMENT '产品id',
  forecast_item_id      INT               COMMENT '预测订单itemId',
  purchase_order_id     INT			      COMMENT '采购订单id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  quantity_ordered      INT               COMMENT '订单数量',
  quantity_received     INT               COMMENT '已接收数量',
  quantity_pre_received INT               COMMENT '预接收数量',
  item_price            DECIMAL(11,2)     COMMENT '单价',
  payment_amount        DECIMAL(11,2)     COMMENT '已付款金额',
  quantity_payment      INT               COMMENT '已付款数量',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  country_code          VARCHAR(25)       COMMENT '国家编号',
  delivery_date         DATE              COMMENT '交货日期',
  del_flag              CHAR(1)           COMMENT '删除标记',
  remark                VARCHAR(255)      COMMENT '备注', 
  update_date           DATETIME          COMMENT '价格更改日期', 
  supplier_id           INT               COMMENT '供应商ID',
  actual_delivery_date  DATE              COMMENT '实际交货日期',
  forecast_remark       VARCHAR(255)      COMMENT '销售备注', 
  quantity_off_ordered      INT           COMMENT '线下订单数量',
  quantity_off_received     INT           COMMENT '线下已接收数量',
  quantity_off_pre_received INT           COMMENT '线下预接收数量',
  delivery_date_log    DATE               COMMENT '修改记录',
  sales_user           VARCHAR(20)        COMMENT '运营人员',
  mold_fee             DECIMAL(11,2)      COMMENT '模具费,返还时为负',
  PRIMARY KEY (id)                
)COMMENT = '采购订单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE lc_psi_purchase_order_delivery_date (
  id INT(10)   			  NOT NULL AUTO_INCREMENT,
  purchase_order_id       INT			    COMMENT '采购订单id',
  purchase_order_item_id  INT			    COMMENT '采购订单明细id',
  product_id      		  INT               COMMENT '产品id',
  product_name            VARCHAR(100)      COMMENT '产品名字',
  color_code              VARCHAR(25)       COMMENT '颜色编号',
  country_code            VARCHAR(25)       COMMENT '国家编号',
  delivery_date           DATE              COMMENT '交货日期',
  quantity     			  INT           	COMMENT '预计收货数',
  quantity_received       INT           	COMMENT '已收货数',
  remark                  VARCHAR(200)      COMMENT '备注',
  del_flag                CHAR(1)           COMMENT '删除状态',
  quantity_off_received   INT               COMMENT '线下接收数',
  quantity_off            INT               COMMENT '线下数',
  delivery_date_log    DATE               COMMENT '修改记录',
   PRIMARY KEY (id)                
)COMMENT = '采购订单预计收货日期表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE lc_psi_lading_bill (
	  id INT(10)      			NOT NULL AUTO_INCREMENT,
	  bill_no        			VARCHAR(100)      COMMENT '提单编号',
	  supplier_id     			VARCHAR(100)      COMMENT '供应商id',
	  bill_sta        			VARCHAR(20)       COMMENT '提单状态',
	  create_date    			DATETIME          COMMENT '创建时间',
	  create_user    			VARCHAR(100)      COMMENT '创建人',
	  del_flag        			CHAR(1)           COMMENT '删除标记',
	  attchment_path   			VARCHAR(1000)     COMMENT '附件地址',
	  remark           			VARCHAR(255)      COMMENT '备注',
	  currency_type             VARCHAR(10)       COMMENT '货币类型',
	  sure_date       			DATETIME          COMMENT '确认时间',
	  sure_user       			VARCHAR(100)      COMMENT '确认人',
	  update_date      			DATETIME          COMMENT '修改时间',
	  update_user      			VARCHAR(100)      COMMENT '修改人',
	  total_payment_amount      DECIMAL(11,2)     COMMENT '已付款金额',
	  total_payment_pre_amount  DECIMAL(11,2)     COMMENT '已申请金额',
	  total_amount     			DECIMAL(11,2)     COMMENT '已申请金额',
	  no_deposit_amount         DECIMAL(11,2)     COMMENT '无订金总额',
	  cancel_user				VARCHAR(100) 	  COMMENT '取消人',
	  cancel_date               DATETIME          COMMENT '取消日期',
	  tran_supplier_id          VARCHAR(100)      COMMENT '承运供应商id',
	  delivery_date             DATETIME          COMMENT '发货日期',   
	  PRIMARY KEY (id)
)COMMENT = '提单表' CHARSET utf8 COLLATE utf8_unicode_ci;



CREATE TABLE lc_psi_lading_bill_item (
	  	  id INT(10)  NOT NULL AUTO_INCREMENT,
	  lading_bill_id      		VARCHAR(100)     COMMENT '提单id',
	  purchase_order_item_id    INT       		 COMMENT '订单itemId',
	  product_name        		VARCHAR(100)     COMMENT '产品名字',
	  color_code          		VARCHAR(25)      COMMENT '颜色编号',
	  country_code        		VARCHAR(25)      COMMENT '国家编号',
	  quantity_lading      		INT              COMMENT '提单数量',
	  quantity_sure      		INT              COMMENT '确认数量',
	  quantity_spares      		INT              COMMENT '备品数量',
	  del_flag            		CHAR(1)          COMMENT '删除标记',
	  is_pass            		CHAR(1)          COMMENT '通过标记',
	  remark              		VARCHAR(255)     COMMENT '备注', 
	  item_price          		DECIMAL(11,2)    COMMENT '产品单价',
	  sku                 		VARCHAR(200)     COMMENT 'sku',
	  parts_timely_info  		VARCHAR(500)     COMMENT '配件及时信息查询',
	  quantity_off_lading  		INT              COMMENT '线下提货数量',
	  
	  total_payment_amount      DECIMAL(11,2)    COMMENT '已付款金额',
	  total_payment_pre_amount  DECIMAL(11,2)    COMMENT '已申请金额',
	  total_amount     			DECIMAL(11,2)    COMMENT '总金额',
	  balance_rate1  INT(3)      NULL            COMMENT '尾款比例1',
	  balance_rate2  INT(3)      NULL           COMMENT '尾款比例2',
	  balance_delay1 INT(3)      NULL            COMMENT '延迟天数1',
	  balance_delay2 INT(3)      NULL            COMMENT '延迟天数2',
	  his_record VARCHAR(500) NULL  COMMENT '验货记录',
  	  quantity_goods INT(11) NULL  COMMENT '已验收数量',
  	  is_test_over            	CHAR(1)          COMMENT '是否质检通过',
	  PRIMARY KEY (id)                
)COMMENT = '提单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE lc_psi_purchase_payment (
  id INT(10)       			NOT NULL AUTO_INCREMENT,
  payment_no       			VARCHAR(100)      COMMENT '付款编号',
  supplier_id      			VARCHAR(100)      COMMENT '供应商id',
  payment_amount_total   	DECIMAL(11,2)     COMMENT '付款总额',
  payment_sta         		VARCHAR(20)       COMMENT '付款状态',
  create_date     			DATETIME          COMMENT '创建时间',
  create_user      			VARCHAR(100)      COMMENT '创建人',
  del_flag         			CHAR(1)           COMMENT '删除标记',
  attchment_path   			VARCHAR(1000)     COMMENT '付款凭证地址',
  remark           			VARCHAR(255)      COMMENT '备注',
  sure_date       			DATETIME          COMMENT '确认时间',
  sure_user        		    VARCHAR(100)      COMMENT '确认人',
  update_date               DATETIME          COMMENT '修改时间',
  update_user               VARCHAR(100)      COMMENT '修改人',
  apply_user				VARCHAR(100) 	  COMMENT '申请人',
  apply_date                DATETIME          COMMENT '申请日期',
  cancel_user				VARCHAR(100) 	  COMMENT '取消人',
  cancel_date               DATETIME          COMMENT '取消日期',
  account_type              VARCHAR(255)      COMMENT '账号信息',
  has_adjust                CHAR(1)           COMMENT '有调整项',
  real_payment_amount       DECIMAL(11,2)     COMMENT '真实付款金额',
  currency_type             VARCHAR(20)       COMMENT '货币类型',
  pay_flow_no               VARCHAR(20)       COMMENT '付款流水号',
  PRIMARY KEY (id)
)COMMENT = '采购付款表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE lc_psi_purchase_payment_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  payment_id          INT              COMMENT '付款单id',
  purchase_order_id   INT              COMMENT '订单id',
  lading_bill_id      INT              COMMENT '提单id',
  payment_type        CHAR(1)          COMMENT '付款类型',
  payment_amount   	  DECIMAL(11,2)    COMMENT '付款金额',
  del_flag            CHAR(1)          COMMENT '删除标记',
  remark              VARCHAR(255)     COMMENT '备注', 
  bill_no             VARCHAR(100)     COMMENT '提单或订单单号',
  lading_item_bill_id      INT        COMMENT '提单itemId',
  PRIMARY KEY (id)              
)COMMENT = '采购付款明细表' CHARSET utf8 COLLATE utf8_unicode_ci;



CREATE TABLE lc_psi_transport_order (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  transport_no  		VARCHAR(20)      COMMENT '运单号',
  model         		VARCHAR(10)      COMMENT '运输方式',
  ocean_model           VARCHAR(10)      COMMENT '海运模式',
  orgin        			VARCHAR(10)      COMMENT '起运港口',
  destination   		VARCHAR(10)      COMMENT '目的港口',
  from_store            VARCHAR(50)      COMMENT '发货仓库',
  to_store              VARCHAR(50)      COMMENT '目的仓库',
  big_zone       		VARCHAR(10)      COMMENT '大区', 
  box_number    		INT              COMMENT '箱数',    
  weight        		DECIMAL(11,2)    COMMENT '重量',
  volume        		DECIMAL(11,2)    COMMENT '体积',
  teu           		INT              COMMENT '装箱记数',
  local_amount  		DECIMAL(11,2)    COMMENT '本地花费',
  tran_amount           DECIMAL(11,2)    COMMENT '运输费用',
  dap_amount   			DECIMAL(11,2)    COMMENT '国外花费',
  insurance_amount      DECIMAL(11,2)    COMMENT '保费',
  other_amount          DECIMAL(11,2)    COMMENT '其他费用',
  unit_price    		DECIMAL(11,2)    COMMENT '单价',
  duty_taxes         	DECIMAL(11,2)    COMMENT '进口税税金',
  tax_taxes             DECIMAL(11,2)    COMMENT '关税税金',
  pay_amount1           DECIMAL(11,2)    COMMENT '付款1',
  pay_amount2           DECIMAL(11,2)    COMMENT '付款2',
  pay_amount3           DECIMAL(11,2)    COMMENT '付款3',
  pay_amount4           DECIMAL(11,2)    COMMENT '付款4',	
  pay_amount5           DECIMAL(11,2)    COMMENT '付款5',	
  pay_amount6           DECIMAL(11,2)    COMMENT '付款6',	
  vendor1       		INT              COMMENT '承运商1',
  vendor2       		INT              COMMENT '承运商2',
  vendor3       		INT              COMMENT '承运商3',
  vendor4       		INT              COMMENT '承运商4',
  vendor5       		INT              COMMENT '承运商5',
  vendor6       		INT              COMMENT '承运商6',
  currency1     		VARCHAR(10)      COMMENT '货币种类1',
  currency2     		VARCHAR(10)      COMMENT '货币种类2',
  currency3     		VARCHAR(10)      COMMENT '货币种类3',
  currency4     		VARCHAR(10)      COMMENT '货币种类4',
  currency5     		VARCHAR(10)      COMMENT '货币种类5',
  currency6     		VARCHAR(10)      COMMENT '货币种类6',
  rate1                 DECIMAL(11,3)    COMMENT '汇率1',
  rate2                 DECIMAL(11,3)    COMMENT '汇率2',
  rate3                 DECIMAL(11,3)    COMMENT '汇率3',
  rate4                 DECIMAL(11,3)    COMMENT '汇率4',
  etd_date      		DATETIME         COMMENT '离港日期',
  pre_eta_date          DATETIME         COMMENT '预计到港日期',
  eta_date      		DATETIME         COMMENT '到港日期',
  first_eta_date      	DATETIME         COMMENT '(第一次)到港日期',
  delivery_date 		DATETIME         COMMENT '提货日期',
  arrival_date  		DATETIME         COMMENT '到货日期',
  pick_up_date  		DATETIME         COMMENT '提货日期',
  lading_bill_no 		VARCHAR(100)     COMMENT '物流单号', 
  bill_no       		VARCHAR(100)     COMMENT '(财务付款凭证)水单号',
  carrier    		    VARCHAR(100)     COMMENT '飞机/船舶公司',
  remark        		VARCHAR(250)     COMMENT '备注',
  transport_sta 		CHAR(1)          COMMENT '运单状态',
  create_user  			VARCHAR(64)  	 COMMENT '创建人',
  create_date   		DATETIME	 	 COMMENT '创建时间',
  oper_delivery_user	VARCHAR(64)  	 COMMENT '提货人',
  oper_delivery_date	DATETIME     	 COMMENT '提货时间',
  oper_arrival_user		VARCHAR(64) 	 COMMENT '收货操作人',
  oper_arrival_date		DATETIME    	 COMMENT '收货操作时间',
  cancel_user			VARCHAR(64) 	 COMMENT '取消人',
  cancel_date           DATETIME         COMMENT '取消日期',
  packQuantity          INT              COMMENT '装箱数',
  payment_sta           CHAR(1)          COMMENT '付款状态',
  to_country            VARCHAR(10)      COMMENT '去向国',
  plane_num             INT              COMMENT '飞机数量',
  plane_index           INT              COMMENT '收了几个飞机的货',
  fba_timinal_time      DATETIME         COMMENT '排队时间',
  fba_checking_in_time	DATETIME	     COMMENT '收货开始时间',
  fba_closed_time 		DATETIME		 COMMENT '收货结束时间',
  shipment_id           VARCHAR(30)      COMMENT 'shipmentId',
  local_path 			VARCHAR(500)     COMMENT 'local花费凭证',
  dap_path 				VARCHAR(500)     COMMENT 'dap花费凭证',
  tran_path 			VARCHAR(500)     COMMENT 'tran花费凭证',
  other_path 			VARCHAR(500)     COMMENT 'other花费凭证',
  insurance_path 		VARCHAR(500)     COMMENT 'insurance花费凭证',
  tax_path 				VARCHAR(500)     COMMENT 'tax花费凭证',
  suffix_name           VARCHAR(100)     COMMENT '后缀名',
  oper_to_port_user	    VARCHAR(64)  	 COMMENT '到港操作人',
  oper_to_port_date	    DATETIME     	 COMMENT '到港操作时间',
  oper_from_port_user	VARCHAR(64)  	 COMMENT '离港操作人',
  oper_from_port_date	DATETIME     	 COMMENT '离港操作时间',
  transport_type        CHAR(1)          COMMENT '运输类型',
  destination_detail    VARCHAR(500)     COMMENT '目的地详细地址',
  else_path             VARCHAR(500)     COMMENT '其他文件',
  unline_order          int              COMMENT '线下订单号',
  oper_arrival_fixed_date	DATE    	 COMMENT '收货操作时间',
  fba_inbound_id        varchar(100)     COMMENT '生成fba贴id',
  is_count              char(1)          COMMENT '0:统计',
  change_record 		varchar(100) 	 COMMENT '收货时间变更记录',
  
  declare_amount        DECIMAL(11,2)    COMMENT '报关金额',
  tax_refund_amount     DECIMAL(11,2)    COMMENT '退税金额',
  declare_no 		    varchar(100) 	 COMMENT '报关单号',
  exportInvoicePath     varchar(100)     COMMENT '出口发票',
  confirm_pay           CHAR(1)          COMMENT '确认付款',
  
  other_amount1         DECIMAL(11,2)    COMMENT '其他费用1',
  other_taxes           DECIMAL(11,2)    COMMENT '其他税金',
  pay_amount7           DECIMAL(11,2)    COMMENT '付款7',	
  vendor7       		INT              COMMENT '承运商7',
  currency7     		VARCHAR(10)      COMMENT '货币种类7',
  other_path1 			VARCHAR(500)     COMMENT 'other1花费凭证',
  rate7                 DECIMAL(11,3)    COMMENT '汇率7',
  mix_file              VARCHAR(500)     COMMENT '混合文件',
  export_date  		    DATE             COMMENT '报关出口日期',
  invoice_flag 			CHAR(1)          COMMENT '发票状态',
  other_remark 			VARCHAR(100)     COMMENT 'otherRemark',
  other_remark1			VARCHAR(100)     COMMENT 'otherRemark1',
  PRIMARY KEY (id)              
)COMMENT = '运单表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE lc_psi_transport_order_item (
  id INT(10)   			NOT NULL AUTO_INCREMENT,
  transport_order_id    INT               COMMENT '运单id',
  product_id      		INT               COMMENT '产品id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  country_code          VARCHAR(25)       COMMENT '国家编号',
  quantity              INT               COMMENT '数量',
  shipped_quantity      INT               COMMENT '发货数量',
  receive_quantity      INT               COMMENT '接收数量',
  item_price            DECIMAL(11,2)     COMMENT '提高10%单价',
  currency              VARCHAR(10)       COMMENT '货币类型',
  del_flag              CHAR(1)           COMMENT '删除标记',
  offline_sta           CHAR(1)           COMMENT '线上线下标记',
  pack_quantity         INT               COMMENT '装箱数量',
  sku                   VARCHAR(200)      COMMENT 'sku',
  remark                VARCHAR(255)      COMMENT '备注', 
  barcode               VARCHAR(255)      COMMENT '条形码', 
  product_price         DECIMAL(11,2)     COMMENT '金额',
  cn_price              DECIMAL(11,2)     COMMENT '出库价',
  lower_price           DECIMAL(11,2)     COMMENT '出口金额',
  import_price          DECIMAL(11,2)     COMMENT '进口金额',
  PRIMARY KEY (id)                   
)COMMENT = '运单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;
ALTER TABLE `lc_psi_transport_order_item`   
  ADD COLUMN `fba_flag` CHAR(1) NULL  COMMENT '建FBA贴标记 0：否  1：是',
  ADD COLUMN `fba_inbound_id` INT(11) NULL  COMMENT 'psi_fba_inbound ID' ;

CREATE TABLE lc_psi_transport_order_container (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  transport_order_id  INT              COMMENT '运单id',
  container_type      VARCHAR(10)      COMMENT '集装箱类型',
  quantity            INT              COMMENT '数量',
  item_price          DECIMAL(11,2)    COMMENT '单价',
  remark              VARCHAR(100)     COMMENT '备注',
  del_flag            CHAR(1)          COMMENT '删除',
  PRIMARY KEY (id)              
)COMMENT = '运单海运集装箱信息表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS lc_psi_parts_inventory_out_order;
CREATE TABLE lc_psi_parts_inventory_out_order (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_inventory_out_id  INT        COMMENT  '配件出库单ID',
  purchase_order_id   INT            COMMENT  '采购订单ID',
  purchase_order_no   VARCHAR(100)   COMMENT  '采购订单NO',
  quantity            INT            COMMENT  '产品数',
  PRIMARY KEY (id)              
)COMMENT = '配件出库关联产品订单表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS lc_psi_parts_inventory_out_item;
CREATE TABLE lc_psi_parts_inventory_out_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_inventory_out_id  INT            COMMENT  '配件出库单ID',
  parts_id                INT            COMMENT  '配件id',
  parts_name              VARCHAR(100)   COMMENT  '配件名称',
  quantity                INT            COMMENT  '出库数量',
  PRIMARY KEY (id)              
)COMMENT = '配件库存出库明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS lc_psi_parts_delivery_check;
 CREATE TABLE lc_psi_parts_delivery_check (
   id                     INT(11)       NOT NULL AUTO_INCREMENT,
   purchase_order_id      INT(11)       COMMENT  '采购订单id' ,
   product_id             INT(11)       COMMENT  '产品id',    
   color                  VARCHAR(20)   COMMENT  '产品颜色',
   can_lading_quantity    INT(11)       COMMENT  '可提货数',
   PRIMARY KEY (id)
)COMMENT = '产品配件出库校验表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS lc_psi_parts_order_basis;
CREATE TABLE lc_psi_parts_order_basis (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_id          	  INT              COMMENT '配件id',
  parts_name        	  VARCHAR(100)     COMMENT '配件名字',
  purchase_order_id 	  INT              COMMENT '产品采购订单id',
  purchase_order_no 	  VARCHAR(100)     COMMENT '产品采购订单no',
  need_quantity     	  INT              COMMENT '需要数量',
  order_quantity    	  INT              COMMENT '订单数量',
  supplier_id       	  INT              COMMENT '供应商id',
  moq               	  INT              COMMENT '最小下单数',
  delivery_date    		  DATE             COMMENT '收货日期',
  remark            	  VARCHAR(500)     COMMENT '备注',
  po_frozen         	  INT              COMMENT '冻结数量',
  po_not_frozen     	  INT              COMMENT '未冻结数量',
  stock_frozen      	  INT              COMMENT '库存冻结数量',
  stock_not_frozen  	  INT              COMMENT '库存未冻结数量',
  
  after_po_frozen         INT        	   COMMENT '冻结数量',
  after_po_not_frozen     INT              COMMENT '未冻结数量',
  after_stock_frozen      INT       	   COMMENT '库存冻结数量',
  after_stock_not_frozen  INT        	   COMMENT '库存未冻结数量',
  cancelSta           	  CHAR(1)          COMMENT '取消状态',
  PRIMARY KEY (id)              
)COMMENT = '配件订单生成依据表' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE IF EXISTS lc_psi_parts_order;
CREATE TABLE lc_psi_parts_order (
	  id 					 INT(10)     NOT NULL AUTO_INCREMENT,
	  parts_order_no   		 VARCHAR(100)      COMMENT '配件订单号',
	  purchase_order_id 	 INT               COMMENT '采购订单id',
	  purchase_order_no		 VARCHAR(50)       COMMENT '采购订单号',
	  supplier_id      		 VARCHAR(100)      COMMENT '供应商id',
	  purchase_date          DATE              COMMENT '采购下单日期',
	  total_amount     		 DECIMAL(11,2)     COMMENT '总金额',
	  payment_amount   		 DECIMAL(11,2)     COMMENT '支付总额',
	  pre_payment_amount     DECIMAL(11,2)     COMMENT '预支付总额',
	  deposit_pre_amount     DECIMAL(11,2)     COMMENT '预支付定金总额',
	  deposit_amount         DECIMAL(11,2)     COMMENT '支付定金总额',
	  currency_type    		 VARCHAR(20)       COMMENT '货币类型',
	  deposit          		 INT               COMMENT '定金比例',
	  order_sta        		 CHAR(1)           COMMENT '订单状态',
	  payment_sta      		 CHAR(1)           COMMENT '支付状态',
	  remark            	 VARCHAR(225)      COMMENT '备注',
	  create_user     		 VARCHAR(32)       COMMENT '创建人',
	  create_date      		 DATE              COMMENT '创建时间',
	  update_user      		 VARCHAR(32)       COMMENT '修改人',
	  update_date      		 DATE              COMMENT '修改时间',
	  sure_user      		 VARCHAR(32)       COMMENT '确认人',
	  sure_date      		 DATE              COMMENT '确认时间',
	  cancel_user      		 VARCHAR(32)       COMMENT '取消人',
	  cancel_date      		 DATE              COMMENT '取消时间',
	  send_eamil             CHAR(1)           COMMENT '是否发送邮件',
	  receive_finished_date  DATE              COMMENT '收货完成时间',
	  pi_file_path           VARCHAR(500)      COMMENT '附件',
	  is_product_receive     CHAR(1)           COMMENT '是否产品收货',
	  pay_item_id            INT               COMMENT '付款明细项' ,
	  PRIMARY KEY (id)
)COMMENT = '配件订单表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS lc_psi_parts_order_item;
CREATE TABLE lc_psi_parts_order_item (
	  id 					INT(10)   NOT NULL AUTO_INCREMENT,
	  parts_id      		INT               COMMENT '配件id',
	  parts_name          	VARCHAR(100)      COMMENT '配件名字',
	  parts_order_id        VARCHAR(100)      COMMENT '配件订单id',
	  quantity_ordered      INT               COMMENT '订单数量',
	  quantity_received     INT               COMMENT '已接收数量',
	  quantity_pre_received INT               COMMENT '预收货数',
	  delivery_date         DATE              COMMENT 'PO交期',
	  actual_delivery_date  DATE              COMMENT '预计交期',
	  remark                VARCHAR(500)      COMMENT '备注',
	  item_price            DECIMAL(11,2)     COMMENT '单价',
	  del_flag              CHAR(1)           COMMENT '删除标记',
	  payment_amount        DECIMAL(11,2)     COMMENT '已付款金额',
  	  quantity_payment      INT               COMMENT '已付款数量',
      PRIMARY KEY (id)                
)COMMENT = '配件订单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS lc_psi_parts_delivery;
CREATE TABLE lc_psi_parts_delivery (
  id INT(10)      			NOT NULL AUTO_INCREMENT,
  bill_no        			VARCHAR(100)      COMMENT '提单编号',
  supplier_id     			VARCHAR(100)      COMMENT '供应商id',
  bill_sta        			VARCHAR(20)       COMMENT '提单状态',
  create_date    			DATETIME          COMMENT '创建时间',
  create_user    			VARCHAR(100)      COMMENT '创建人',
  del_flag        			CHAR(1)           COMMENT '删除标记',
  attchment_path   			VARCHAR(1000)     COMMENT '附件地址',
  remark           			VARCHAR(255)      COMMENT '备注',
  currency_type             VARCHAR(10)       COMMENT '货币类型',
  sure_date       			DATETIME          COMMENT '确认时间',
  sure_user       			VARCHAR(100)      COMMENT '确认人',
  update_date      			DATETIME          COMMENT '修改时间',
  update_user      			VARCHAR(100)      COMMENT '修改人',
  total_payment_amount      DECIMAL(11,2)     COMMENT '已付款金额',
  total_payment_pre_amount  DECIMAL(11,2)     COMMENT '已申请金额',
  total_amount     			DECIMAL(11,2)     COMMENT '总金额',
  cancel_user				VARCHAR(100) 	  COMMENT '取消人',
  cancel_date               DATETIME          COMMENT '取消日期',
  tran_supplier_id          VARCHAR(100)      COMMENT '承运供应商id',
  delivery_date             DATETIME          COMMENT '收货日期',
  PRIMARY KEY (id)
)COMMENT = '提单表' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS lc_psi_parts_delivery_item;
CREATE TABLE lc_psi_parts_delivery_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_delivery_id     VARCHAR(100)     COMMENT '提单id',
  parts_order_item_id   INT              COMMENT '订单itemId',
  parts_id              VARCHAR(100)     COMMENT '配件id',
  parts_name        	VARCHAR(100)     COMMENT '配件名字',
  quantity_lading       INT              COMMENT '提单数量',
  item_price            DECIMAL(11,2)    COMMENT '单价',
  del_flag              CHAR(1)          COMMENT '删除标记',
  remark                VARCHAR(255)     COMMENT '备注', 
  PRIMARY KEY (id)                
)COMMENT = '提单明细表' CHARSET utf8 COLLATE utf8_unicode_ci;

 



DROP TABLE IF EXISTS lc_psi_parts_payment;
CREATE TABLE lc_psi_parts_payment (
  id INT(10)       			NOT NULL AUTO_INCREMENT,
  payment_no       			VARCHAR(100)      COMMENT '付款编号',
  supplier_id      			VARCHAR(100)      COMMENT '供应商id',
  currency_type             VARCHAR(10)       COMMENT '货币类型',
  payment_amount_total   	DECIMAL(11,2)     COMMENT '付款总额',
  payment_sta         		VARCHAR(20)       COMMENT '付款状态',
  account_type              VARCHAR(255)      COMMENT '账号信息',
  attchment_path   			VARCHAR(1000)     COMMENT '付款凭证地址',
  remark           			VARCHAR(255)      COMMENT '备注',
  create_date     			DATETIME          COMMENT '创建时间',
  create_user      			VARCHAR(100)      COMMENT '创建人',
  sure_date       			DATETIME          COMMENT '确认时间',
  sure_user        		    VARCHAR(100)      COMMENT '确认人',
  update_date               DATETIME          COMMENT '修改时间',
  update_user               VARCHAR(100)      COMMENT '修改人',
  apply_user				VARCHAR(100) 	  COMMENT '申请人',
  apply_date                DATETIME          COMMENT '申请日期',
  cancel_user				VARCHAR(100) 	  COMMENT '取消人',
  cancel_date               DATETIME          COMMENT '取消日期',
  pay_flow_no               VARCHAR(20)       COMMENT '付款流水号',
  PRIMARY KEY (id)
)COMMENT = '配件订单付款表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS lc_psi_parts_payment_item;
CREATE TABLE lc_psi_parts_payment_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  payment_id          INT              COMMENT '付款单id',
  parts_order_id      INT              COMMENT '订单id',
  parts_delivery_id   INT              COMMENT '提单id',
  bill_no             VARCHAR(50)      COMMENT '订单或提单单号',
  payment_amount   	  DECIMAL(11,2)    COMMENT '付款金额',
  del_flag            CHAR(1)          COMMENT '删除标记',
  payment_type        CHAR(1)          COMMENT '付款类型',
  remark              VARCHAR(255)     COMMENT '备注', 
  PRIMARY KEY (id)              
)COMMENT = '配件订单付款明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS lc_psi_parts_inventory_out;
CREATE TABLE lc_psi_parts_inventory_out (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  bill_no             VARCHAR(50)    COMMENT  '配件出库单NO',
  product_id          INT            COMMENT  '产品id',
  product_name        VARCHAR(100)   COMMENT  '产品名',
  color               VARCHAR(20)    COMMENT  '产品颜色',
  quantity            INT            COMMENT  '产品数',
  remark              VARCHAR(500)   COMMENT  '备注',
  create_user         VARCHAR(50)    COMMENT  '创建人',
  create_date         DATE           COMMENT  '创建时间',
  PRIMARY KEY (id)              
)COMMENT = '配件出库表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_parts_inventory_out_order;
CREATE TABLE psi_parts_inventory_out_order (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_inventory_out_id  INT        COMMENT  '配件出库单ID',
  purchase_order_id   INT            COMMENT  '采购订单ID',
  purchase_order_no   VARCHAR(100)   COMMENT  '采购订单NO',
  quantity            INT            COMMENT  '产品数',
  PRIMARY KEY (id)              
)COMMENT = '配件出库关联产品订单表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS lc_psi_purchase_amount_adjust;
CREATE TABLE lc_psi_purchase_amount_adjust (
	id INT (10) NOT NULL AUTO_INCREMENT,
	adjust_amount        DECIMAL(11,2)    COMMENT '调整金额',
	supplier_id          INT              COMMENT '供应商id',
	payment_id           INT              COMMENT '付款单id',
    subject              VARCHAR(100)     COMMENT '付款标题',
    currency             VARCHAR(20)      COMMENT '货币类型',
    remark               VARCHAR(100)     COMMENT '备注',
    adjust_sta           CHAR(1)          COMMENT '状态',
    create_date          DATETIME         COMMENT '创建日期', 
    create_user          VARCHAR(50)      COMMENT '申请人',
    update_date          DATETIME         COMMENT '改变日期', 
    update_user          VARCHAR(50)      COMMENT '改变人',
    cancel_date          DATETIME         COMMENT '取消日期', 
    cancel_user          VARCHAR(50)      COMMENT '取消人',
    
    purchase_order_id    INT              COMMENT '订单id',
    order_no             VARCHAR(50)      COMMENT '订单号',
    product_name_color   VARCHAR(50)      COMMENT '产品名带颜色',
    file_path            VARCHAR(500)     COMMENT '附件',
	PRIMARY KEY (id)
)COMMENT = '采购金额调整表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE lc_psi_parts_inventory (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_id          INT              COMMENT '配件id',
  parts_name        VARCHAR(100)     COMMENT '配件名字',
  po_frozen         INT              COMMENT '冻结数量',
  po_not_frozen     INT              COMMENT '未冻结数量',
  stock_frozen      INT              COMMENT '库存冻结数量',
  stock_not_frozen  INT              COMMENT '库存未冻结数量',
  PRIMARY KEY (id)              
)COMMENT = '配件库存表' CHARSET utf8 COLLATE utf8_unicode_ci;



CREATE TABLE lc_psi_parts_inventory_taking (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  taking_no         VARCHAR(100)     COMMENT '盘点No',
  taking_type       VARCHAR(100)     COMMENT '盘点类型（盘入或盘出）',
  operate_type      VARCHAR(100)     COMMENT '操作类型',
  remark            VARCHAR(1000)    COMMENT '备注',   
  data_file         VARCHAR(255)     COMMENT 'csv或excel文件',
  origin_name       VARCHAR(100)     COMMENT '原始文件名',
  create_user       VARCHAR(50)      COMMENT '创建人',
  create_date       DATE             COMMENT '创建时间',
  PRIMARY KEY (id)              
)COMMENT = '配件库存盘点表' CHARSET utf8 COLLATE utf8_unicode_ci;



CREATE TABLE lc_psi_parts_inventory_taking_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  taking_id         INT              COMMENT '盘点主id',
  parts_id          INT              COMMENT '配件id',
  parts_name        VARCHAR(100)     COMMENT '配件名字',
  stock_type        VARCHAR(100)     COMMENT '库存类型',
  po_frozen         INT              COMMENT 'poFrozen',
  po_not_frozen     INT              COMMENT 'poNotFrozen',
  stock_frozen      INT              COMMENT 'stockFrozen',
  stock_not_frozen  INT              COMMENT 'stockNotFrozen',
  remark            VARCHAR(1000)    COMMENT '备注',     
  PRIMARY KEY (id)              
)COMMENT = '配件库存盘点明细表' CHARSET utf8 COLLATE utf8_unicode_ci;




CREATE TABLE lc_psi_parts_inventory_log (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  parts_id          INT              COMMENT '配件id',
  parts_name        VARCHAR(100)     COMMENT '配件名字',
  quantity          INT              COMMENT '数量',
  data_type         VARCHAR(100)     COMMENT '数据类型',
  operate_type      VARCHAR(100)     COMMENT '操作类型',
  relative_number   VARCHAR(100)     COMMENT '相关单号',
  remark            VARCHAR(100)     COMMENT '备注',
  create_user       VARCHAR(50)      COMMENT '创建人',
  create_date       DATE             COMMENT '创建日期',
  PRIMARY KEY (id)              
)COMMENT = '配件库存日志表' CHARSET utf8 COLLATE utf8_unicode_ci;



CREATE TABLE lc_psi_transport_payment (
  id INT(10)       			NOT NULL AUTO_INCREMENT,
  payment_no       			VARCHAR(100)      COMMENT '付款编号',
  supplier_id      			VARCHAR(100)      COMMENT '供应商id',
  payment_type				VARCHAR(20)       COMMENT '付款种类',
  payment_amount   			DECIMAL(11,2)     COMMENT '付款总额',
  before_amount             DECIMAL(11,2)     COMMENT '转换前总金额',
  currency                  VARCHAR(10)       COMMENT '货币类型',
  payment_sta         		VARCHAR(20)       COMMENT '付款状态',
  attchment_path   			VARCHAR(500)      COMMENT '付款凭证地址',
  supplier_attchment_path	VARCHAR(500)      COMMENT '供应商发票凭证',
  remark           			VARCHAR(255)      COMMENT '备注',
  create_date     			DATETIME          COMMENT '创建时间',
  create_user      			VARCHAR(100)      COMMENT '创建人',
  sure_date       			DATETIME          COMMENT '确认时间',
  sure_user        		    VARCHAR(100)      COMMENT '确认人',
  update_date               DATETIME          COMMENT '修改时间',
  update_user               VARCHAR(100)      COMMENT '修改人',
  apply_user				VARCHAR(100) 	  COMMENT '申请人',
  apply_date                DATETIME          COMMENT '申请日期',
  cancel_user				VARCHAR(100) 	  COMMENT '取消人',
  cancel_date               DATETIME          COMMENT '取消日期',
  account_type              VARCHAR(255)      COMMENT '账号信息',
  rate                      DECIMAL(11,3)     COMMENT '汇率',
  pay_flow_no               VARCHAR(20)       COMMENT '付款流水号',
  PRIMARY KEY (id)
)COMMENT = '运单付款表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE lc_psi_transport_payment_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  payment_id          INT              COMMENT '付款单id',
  tran_order_id   	  INT              COMMENT '运单id',
  payment_type        VARCHAR(30)      COMMENT '付款类型',
  payment_amount   	  DECIMAL(11,2)    COMMENT '付款金额',
  after_amount		  DECIMAL(11,2)    COMMENT '转化后金额',
  currency            VARCHAR(10)      COMMENT '货币种类',
  del_flag            CHAR(1)          COMMENT '删除标记',
  remark              VARCHAR(255)     COMMENT '备注', 
  rate                DECIMAL(11,3)	   COMMENT '汇率',
  transport_no        VARCHAR(100)     COMMENT '运单号',
  PRIMARY KEY (id)              
)COMMENT = '运单付款明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE lc_psi_transport_revise (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  payment_no          VARCHAR(50)      COMMENT '支付编号',
  tran_order_id   	  INT              COMMENT '运单id',
  tran_order_no       VARCHAR(100)     COMMENT '运单号',
  supplier_id         INT              COMMENT '供应商',
  account_type        VARCHAR(500)     COMMENT '账号类型',
  attchment_path      VARCHAR(500)     COMMENT '凭证地址',
  revise_amount       DECIMAL(11,2)    COMMENT '申请修正金额',
  currency            VARCHAR(10)      COMMENT '货币类型',
  remark              VARCHAR(255)     COMMENT '备注',
  apply_user          VARCHAR(64)      COMMENT '申请人',
  apply_date          DATETIME         COMMENT '申请日期',  
  sure_user           VARCHAR(64)      COMMENT '确认人',
  sure_date           DATETIME         COMMENT '确认日期',
  cancel_user         VARCHAR(64)      COMMENT '取消人',
  cancel_date         DATETIME         COMMENT '取消日期',
  revise_sta          CHAR(1)          COMMENT '修正状态',
  rate                DECIMAL(11,3)    COMMENT '汇率',
  account_path        VARCHAR(500)     COMMENT '账单地址',
  PRIMARY KEY (id)              
)COMMENT = '运单付款修正表' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE lc_psi_transport_revise_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  transport_revise_id INT              COMMENT '补差主表id',
  old_amount		  DECIMAL(11,2)    COMMENT '原金额',
  revise_type         VARCHAR(20)      COMMENT '修正类型 localAmount、tranAmount',
  revise_amount       DECIMAL(11,2)    COMMENT '修正金额',
  currency            VARCHAR(10)      COMMENT '货币类型',  
  rate                DECIMAL(11,3)    COMMENT '汇率',
  remark              VARCHAR(255)     COMMENT '备注',
  PRIMARY KEY (id)              
)COMMENT = '运单付款修正item表' CHARSET utf8 COLLATE utf8_unicode_ci;




CREATE TABLE lc_psi_his_purchase_order (
  id INT(10)       NOT NULL AUTO_INCREMENT,
  order_no         VARCHAR(100)      COMMENT '订单编号',
  supplier_id      VARCHAR(100)      COMMENT '供应商id',
  order_sta        CHAR(1)           COMMENT '订单状态',
  deposit          INT               COMMENT '定金',
  currency_type    VARCHAR(20)       COMMENT '货币类型',
  order_total      DECIMAL(11,2)     COMMENT '总金额',
  purchase_date    DATE              COMMENT '下单日期',
  merchandiser     VARCHAR(100)      COMMENT '跟单员',
  received_store   VARCHAR(100)      COMMENT '收货仓库',
  pi_file_path     VARCHAR(100)      COMMENT 'PI存放路径',
  modify_memo      VARCHAR(255)      COMMENT '修改备注',
  version_no       VARCHAR(20)       COMMENT '版本',
  create_date      DATETIME          COMMENT '创建时间',
  create_user      VARCHAR(100)      COMMENT '创建人',
  del_flag         CHAR(1)           COMMENT '删除标记',
  update_date      DATETIME          COMMENT '更新时间',
  update_user      VARCHAR(100)      COMMENT '更新人',
  sure_date        DATETIME          COMMENT '确认时间',
  sure_user        VARCHAR(100)      COMMENT '确认人',
  deposit_amount   DECIMAL(11,2)     COMMENT '已付定金金额' ,
  cancel_user	   VARCHAR(100)      COMMENT '取消人',
  cancel_date      DATETIME          COMMENT '取消日期',
  deposit_pre_amount   DECIMAL(11,2) COMMENT '已申请定金金额' ,
  pay_sta          CHAR(1)           COMMENT '支付状态',
  payment_amount   DECIMAL(11,2)     COMMENT '已支付尾款金额' ,
  send_email_flag  CHAR(1)           COMMENT '是否向产家发送邮件',
  PRIMARY KEY (id)
)COMMENT = '采购订单快照表' CHARSET utf8 COLLATE utf8_unicode_ci;



CREATE TABLE lc_psi_his_purchase_order_item (
  id INT(10)   			NOT NULL AUTO_INCREMENT,
  product_id      		VARCHAR(100)      COMMENT '产品id',
  purchase_order_id     VARCHAR(100)      COMMENT '采购订单id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  quantity_ordered      INT               COMMENT '订单数量',
  quantity_received     INT               COMMENT '已接收数量',
  quantity_pre_received INT               COMMENT '预接收数量',
  item_price            DECIMAL(11,2)     COMMENT '单价',
  payment_amount        DECIMAL(11,2)     COMMENT '已付款金额',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  country_code          VARCHAR(25)       COMMENT '国家编号',
  delivery_date         DATE              COMMENT '交货日期',
  del_flag              CHAR(1)           COMMENT '删除标记',
  remark                VARCHAR(255)      COMMENT '备注', 
  update_date           DATETIME          COMMENT '价格更改日期', 
  quantity_payment      INT               COMMENT '已付款数量',
  supplier_id           INT               COMMENT '供应商ID',
  PRIMARY KEY (id)                
)COMMENT = '采购订单快照明细表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS lc_psi_purchase_financial_report;
CREATE TABLE lc_psi_purchase_financial_report (
	id INT (10) NOT NULL AUTO_INCREMENT,
	supplier_id			  INT              COMMENT '供应商id',
	product_name_color    VARCHAR(100)     COMMENT '产品名+颜色',
	month                 VARCHAR(10)      COMMENT '月份',
	order_amount          DECIMAL(11,2)    COMMENT '订单金额',
	pay_order_amount      DECIMAL(11,2)    COMMENT '定金支付金额',
	pay_lading_amount     DECIMAL(11,2)    COMMENT '尾款支付金额',
	data_date             DATETIME         COMMENT '数据产生日期',            
	PRIMARY KEY (id)
)COMMENT = '按月按产品统计金额' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_product_type_line;
CREATE TABLE `psi_product_type_line` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品类型',
  `line` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品线',
  `month` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '月份',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type` (`type`,`month`)
) COMMENT='产品类型和产品线对应关系表' CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS psi_supplier_tax_adjust;
CREATE TABLE psi_supplier_tax_adjust (
	id INT (10) NOT NULL AUTO_INCREMENT,
	supplier_id          INT              COMMENT '供应商id',
	old_tax              INT              COMMENT '原税率',
	tax                  INT              COMMENT '税率',
	file_path            VARCHAR(100)     COMMENT '凭证',
    remark               VARCHAR(100)     COMMENT '备注',
    adjust_sta           CHAR(1)          COMMENT '状态',
    
    create_date          DATETIME         COMMENT '创建日期', 
    create_user          VARCHAR(50)      COMMENT '申请人',
    cancel_date          DATETIME         COMMENT '取消日期', 
    cancel_user          VARCHAR(50)      COMMENT '取消人',
    review_date          DATETIME         COMMENT '审核日期', 
    review_user          VARCHAR(50)      COMMENT '审核人',
	PRIMARY KEY (id)
)COMMENT = '供应商税点调整' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS lc_psi_quality_test;
CREATE TABLE lc_psi_quality_test (
	id INT (10) NOT NULL AUTO_INCREMENT,
	lading_id         		INT           ,
	lading_bill_no          VARCHAR(100)  ,
	product_id          	INT           ,
	test_quantity           INT           ,
	ok_quantity             INT           ,
	received_quantity       INT           ,
	total_quantity          INT           ,
	aql            			VARCHAR(1000) ,  
	in_view            		VARCHAR(1000) ,  
	out_view                VARCHAR(1000) ,
	packing                 VARCHAR(1000) ,
	function                VARCHAR(1000) ,
	reason                  VARCHAR(1000) ,
	report_file             VARCHAR(1000) ,
	give_in_file            VARCHAR(1000) ,
	product_name            VARCHAR(100) ,
	color                   VARCHAR(100) ,
    is_ok                   CHAR(1)      ,
    deal_way                CHAR(1)      ,
    create_date             DATETIME     , 
    create_user             VARCHAR(50)  ,
    sure_date               DATETIME     , 
    sure_user               VARCHAR(50)  ,
    test_sta                CHAR(1)      ,
    cancel_date             DATETIME     , 
    cancel_user             VARCHAR(50)  ,
    supplier_id          	INT          ,
    
    review_date             DATETIME     , 
    review_user             VARCHAR(50)  ,
    review_remark           VARCHAR(500) ,
    review_date1            DATETIME     , 
    review_user1            VARCHAR(50)  ,
    review_remark1          VARCHAR(500) ,
    review_date2            DATETIME     , 
    review_user2            VARCHAR(50)  ,
    review_remark2          VARCHAR(500) ,
    review_date3            DATETIME     , 
    review_user3            VARCHAR(50)  ,
    review_remark3          VARCHAR(500) ,
	PRIMARY KEY (id)
)COMMENT = '品检信息登记' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_inventory_turnover_data;
CREATE TABLE `psi_inventory_turnover_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `month` varchar(10) DEFAULT NULL COMMENT '月份',
  `data_type` char(1) DEFAULT NULL COMMENT '0:月份 1：年',
  `country` varchar(10) DEFAULT NULL COMMENT '国家',
  `product_name` varchar(100) DEFAULT NULL COMMENT '产品名称',
  `product_type` varchar(100) DEFAULT NULL COMMENT '产品类型',
  `line` varchar(10) DEFAULT NULL COMMENT '产品线',
  `sales_volume` int(10) DEFAULT NULL COMMENT '销量',
  `s_quantity` int(11) DEFAULT NULL COMMENT '期初库存',
  `e_quantity` int(11) DEFAULT NULL COMMENT '期末库存',
  `s_price` decimal(11,2) DEFAULT NULL COMMENT '期初价格(不含税CNY)',
  `e_price` decimal(11,2) DEFAULT NULL COMMENT '期末价格(不含税CNY)',
  `rate` decimal(11,2) DEFAULT NULL COMMENT '存货周转率',
  PRIMARY KEY (`id`)
) COMMENT='库存周转率基础数据' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_vat_invoice_info;
CREATE TABLE psi_vat_invoice_info (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  invoice_date				 DATE             COMMENT '开票日期',
  supplier_id                int              COMMENT '供应商id',
  supplier_name              VARCHAR(100)     COMMENT '供应商名称',
  product_name               VARCHAR(100)     COMMENT '产品名',
  quantity                   int              COMMENT '数量',
  `remaining_quantity`       INT              COMMENT '剩余数量',
  total_amount               decimal(11,2)    COMMENT '总金额',
  invoice_no                 VARCHAR(100)     COMMENT '发票号',
  remark                     VARCHAR(500)     COMMENT '备注',
  del_flag                   CHAR(1)          COMMENT 'delete',
  create_user                VARCHAR(100)     COMMENT '操作人id',
  create_date				 DATETIME         COMMENT '操作日期',
  PRIMARY KEY (id)              
)COMMENT = '增值税发票信息' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS psi_product_improve;
CREATE TABLE psi_product_improve (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  order_no                   VARCHAR(100)     COMMENT '订单号',
  product_name_color         VARCHAR(100)     COMMENT '产品名',
  improve_date				 DATE             COMMENT '优化日期',
  is_change_sku              CHAR(1)          COMMENT '是否更换sku',
  improve_content            VARCHAR(100)     COMMENT '优化情况',
  del_flag                   CHAR(1)          COMMENT 'delete',
  create_user                VARCHAR(100)     COMMENT '创建人id',
  create_date				 DATETIME         COMMENT '创建日期',
  delete_user                VARCHAR(100)     COMMENT '删除人id',
  delete_date				 DATETIME         COMMENT '删除人日期',
  PRIMARY KEY (id)              
)COMMENT = '产品改进信息' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_vat_invoice_use_info`;
CREATE TABLE `psi_vat_invoice_use_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `invoice_id` int(10) DEFAULT NULL COMMENT '发票ID',
  `order_item_id` int(10) DEFAULT NULL,
  `product_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country_code` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `quantity` int(10) DEFAULT NULL,
  `create_user` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `invoice_no` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = '增值税发票使用信息' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `psi_product_post_mail_info`;
CREATE TABLE `psi_product_post_mail_info` ( 
  `id`           int(10)      NOT NULL       AUTO_INCREMENT,
  `product_name` varchar(50)  COLLATE utf8_unicode_ci DEFAULT NULL,
  `country`      varchar(25)  COLLATE utf8_unicode_ci DEFAULT NULL,
  `type`         varchar(25)  COLLATE utf8_unicode_ci DEFAULT NULL,
  `status`       varchar(25)  COLLATE utf8_unicode_ci DEFAULT NULL,
  `remark`       varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_user`  varchar(64)  COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_date`  date DEFAULT NULL,
  `add_date`     date DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = '产品售后邮件发送提醒记录' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_marketing_plan;
CREATE TABLE psi_marketing_plan (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  product_id      		INT(10)      COMMENT '产品id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  country_code          VARCHAR(25)       COMMENT '国家编号',
  start_week            VARCHAR(25)       COMMENT '开始周',
  end_week              VARCHAR(25)       COMMENT '结束周',
  promo_quantity      	INT(10)           COMMENT '促销数',
  real_quantity      	INT(10)           COMMENT '实际促销数',
  remark       			VARCHAR(500)      COMMENT '备注',
  type       			CHAR(1)           COMMENT '类型',
  sta       			CHAR(1)           COMMENT '状态',
  update_user           VARCHAR(100)      COMMENT '更新人id',
  update_date			DATETIME          COMMENT '更新日期',
  review_user           VARCHAR(100)      COMMENT '审核人id',
  review_date			DATETIME          COMMENT '审核日期',
  create_user           VARCHAR(100)      COMMENT '创建人id',
  create_date			DATETIME          COMMENT '创建日期',
  cancel_user           VARCHAR(100)      COMMENT '取消人id',
  cancel_date			DATETIME          COMMENT '取消人日期',
  PRIMARY KEY (id)              
)COMMENT = '营销计划' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_marketing_plan_item;
CREATE TABLE psi_marketing_plan_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  marketing_plan_id    	INT(10)      	  COMMENT '营销计划id',
  product_id      		INT(10)      	  COMMENT '产品id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  promo_quantity      	INT(10)           COMMENT '促销数',
  real_quantity      	INT(10)           COMMENT '实际促销数',
  del_flag              CHAR(1)           COMMENT '删除标示',
  warn       			VARCHAR(200)      COMMENT '预警信息',
  ready_quantity     	INT(10)           COMMENT '备货数',
  ready_remark      	VARCHAR(5000)     COMMENT '备货备注',
  PRIMARY KEY (id)              
)COMMENT = '营销计划item' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS his_psi_marketing_plan;
CREATE TABLE his_psi_marketing_plan (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  product_id      		INT(10)     	  COMMENT '产品id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  country_code          VARCHAR(25)       COMMENT '国家编号',
  start_week            VARCHAR(25)       COMMENT '开始周',
  end_week              VARCHAR(25)       COMMENT '结束周',
  promo_quantity      	INT(10)           COMMENT '促销数',
  remark       			VARCHAR(500)      COMMENT '备注',
  type       			CHAR(1)           COMMENT '类型',
  sta       			CHAR(1)           COMMENT '状态',
  update_user           VARCHAR(100)      COMMENT '更新人id',
  update_date			DATETIME          COMMENT '更新日期',
  marketing_plan_id     INT(10)     	  COMMENT '原计划id',
  PRIMARY KEY (id)              
)COMMENT = 'his营销计划' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS his_psi_marketing_plan_item;
CREATE TABLE his_psi_marketing_plan_item (
  id INT(10)  NOT NULL AUTO_INCREMENT,
  marketing_plan_id     INT(10)      	  COMMENT '营销计划id',
  product_id      		INT(10)      	  COMMENT '产品id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  promo_quantity      	INT(10)           COMMENT '促销数',
  real_quantity      	INT(10)           COMMENT '实际促销数',
  del_flag              CHAR(1)           COMMENT '删除标示',
  PRIMARY KEY (id)              
)COMMENT = 'his营销计划item' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `psi_product_avg_price_local`;

CREATE TABLE `psi_product_avg_price_local` (
  `id` int(10) NOT NULL auto_increment,
  `product_name` varchar(100) collate utf8_unicode_ci default NULL COMMENT '产品名字',
  `country` varchar(25) collate utf8_unicode_ci default NULL COMMENT '国家编号',
  `avg_price` decimal(11,2) default NULL,
  `update_date` datetime default NULL,
  `stock` int(11) default NULL,
  PRIMARY KEY  (`id`)
)COMMENT = '本地仓运费平均价格' CHARSET utf8 COLLATE utf8_unicode_ci;





DROP TABLE IF EXISTS psi_purchase_plan;
CREATE TABLE psi_purchase_plan (
  id INT(10)         NOT NULL AUTO_INCREMENT,
  plan_sta           CHAR(1)           COMMENT '计划状态',
  create_date        DATETIME          COMMENT '创建时间',
  create_user        VARCHAR(100)      COMMENT '创建人',
  review_date        DATETIME          COMMENT '审核时间',
  review_user        VARCHAR(100)      COMMENT '审核人',
  boss_review_date   DATETIME          COMMENT '终极审核时间',
  boss_review_user   VARCHAR(100)      COMMENT '终极审核人',
  cancel_user	     VARCHAR(100)      COMMENT '取消人',
  cancel_date        DATETIME          COMMENT '取消日期',
  remark             VARCHAR(500)      COMMENT '备注',
  product_position   VARCHAR(50)       COMMENT '产品定位',
  att_file_path	     VARCHAR(200)      COMMENT '附件路径',
  PRIMARY KEY (id)
)COMMENT = '新品采购计划' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE IF EXISTS psi_purchase_plan_item;
CREATE TABLE psi_purchase_plan_item (
  id INT(10)   			NOT NULL AUTO_INCREMENT,
  product_id      		INT               COMMENT '产品id',
  product_name          VARCHAR(100)      COMMENT '产品名字',
  color_code            VARCHAR(25)       COMMENT '颜色编号',
  country_code          VARCHAR(25)       COMMENT '国家编号',
  plan_id               INT			      COMMENT 'planId',
  quantity              INT               COMMENT '订单数量',
  quantity_review       INT               COMMENT '审核数量',
  quantity_boss_review  INT               COMMENT '终极审核数量',
  remark         		VARCHAR(500)      COMMENT '备注',
  remark_review         VARCHAR(500)      COMMENT '审核备注',
  remark_boss_review    VARCHAR(500)      COMMENT '终极备注',
  del_flag              CHAR(1)    		  COMMENT '删除状态',
  create_sta            CHAR(1)    		  COMMENT '生成状态',
  PRIMARY KEY (id)                
)COMMENT = '新品采购计划Item' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_product_hidden;
CREATE TABLE `psi_product_hidden` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '带颜色产品名',
  `data_date` DATETIME DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='淘汰无库存且31日销为0产品记录';



CREATE TABLE `psi_supplier_invoice` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `invoice_date` DATE DEFAULT NULL COMMENT '开票日期',
  `invoice_code` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '发票代码',
  `invoice_no` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '发票号码',
  `company_name` VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '公司名称',
  `taxpayer_no` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '纳税识别号',
  `product_name` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '货物名称',
  `model` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '规格型号',
  `unit` VARCHAR(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '单位',
  `quantity` INT(11) DEFAULT NULL COMMENT '数量',
  `price` DECIMAL(11,2) DEFAULT NULL COMMENT '单价',
  `total_price` DECIMAL(11,2) DEFAULT NULL COMMENT '总金额',
  `rate` DECIMAL(11,2) DEFAULT NULL COMMENT '税率',
  `state` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:未认证 1:已认证',
  `return_date` DATE DEFAULT NULL COMMENT '归还日期',
  `use_date` DATETIME DEFAULT NULL COMMENT '使用日期',
  `del_flag` CHAR(1) COLLATE utf8_unicode_ci DEFAULT '0',
  `remaining_quantity` INT(11) DEFAULT NULL COMMENT '剩余数量',
  `create_date` DATETIME DEFAULT NULL,
  `create_user` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `use_quantity` INT(11) DEFAULT NULL,
  `tax_rate` DECIMAL(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE` (`invoice_no`,`product_name`,`price`)
)COMMENT = '发票' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE `psi_transport_declare` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `declare_date` DATE DEFAULT NULL,
  `declare_no` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `declare_num` VARCHAR(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `declare_code` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `transport_no` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_no` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_name` VARCHAR(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `product_model` VARCHAR(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `quantity` INT(11) DEFAULT NULL,
  `price` DECIMAL(11,2) DEFAULT NULL,
  `del_flag` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `invoice_id` INT(11) DEFAULT NULL,
  `arrange_date` DATE DEFAULT NULL,
  `create_user` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `arrange_user` VARCHAR(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` DATE DEFAULT NULL,
  `state` CHAR(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `legal_quantity` DECIMAL(11,2) DEFAULT NULL,
  `legal_unit` VARCHAR(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `unit` VARCHAR(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `total_price` DECIMAL(11,2) DEFAULT NULL,
  `usd_price` DECIMAL(11,2) DEFAULT NULL,
  `usd_rate` DECIMAL(11,2) DEFAULT NULL,
  `cny_price` DECIMAL(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE` (`declare_no`,`declare_num`)
)COMMENT = '报关单' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS psi_product_improvement;
CREATE TABLE `psi_product_improvement` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `line` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品线',
  `product_name` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品',
  `reason` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '变更原因',
  `per_remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '变更前说明',
  `after_remark` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '变更后说明',
  `order_no` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '变更起始订单号',
  `improve_date` date DEFAULT NULL COMMENT '变更时间',
  `file_path` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '变更涉及附件',
  `type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '紧急程度 1：普通 2：紧急 3：特急',
  `status` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `approval_date` datetime DEFAULT NULL COMMENT '审核时间',
  `approval_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '审核人',
  `approval_content` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '审核意见',
  `permission` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '处理意见权限',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS psi_product_improvement_item;
CREATE TABLE `psi_product_improvement_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `improvement_id` int(11) DEFAULT NULL COMMENT '变更编号',
  `sort` int(11) DEFAULT NULL COMMENT '处理序号',
  `department` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '部门',
  `permission` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '处理权限',
  `content` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '处理意见',
  `create_date` datetime DEFAULT NULL COMMENT '处理意见填写时间',
  `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '处理意见填写人',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



DROP TABLE IF EXISTS `psi_invoice_product`;

CREATE TABLE `psi_invoice_product` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `product_code` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tax_rate` decimal(11,2) DEFAULT NULL,
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_stock_area`;

CREATE TABLE `psi_stock_area` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stock_id` int(11) DEFAULT NULL COMMENT '仓库id',
  `name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '分区名称',
  `remarks` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注信息',
  `del_flag` varchar(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记',
  `create_by` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_by` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `update_date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='仓库分区表';

DROP TABLE IF EXISTS `psi_stock_location`;

CREATE TABLE `psi_stock_location` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `area_id` INT(11) DEFAULT NULL COMMENT '分区ID',
  `name` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '库位名称',
  `remarks` VARCHAR(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `del_flag` VARCHAR(1) COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '删除标记',
  `create_by` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_by` VARCHAR(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `create_date` DATE DEFAULT NULL,
  `update_date` DATE DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='库位表';

DROP TABLE IF EXISTS `psi_inventory_location`;

CREATE TABLE `psi_inventory_location` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `sku` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'sku',
  `product_id` int(11) DEFAULT NULL COMMENT '产品id',
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名字',
  `color_code` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '颜色编号',
  `country_code` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家编号',
  `sn_code` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '批次号',
  `new_quantity` int(11) DEFAULT NULL COMMENT '新品数量',
  `old_quantity` int(11) DEFAULT NULL COMMENT '老品数量',
  `broken_quantity` int(11) DEFAULT NULL COMMENT 'broken数量',
  `renew_quantity` int(11) DEFAULT NULL COMMENT 'renew数量',
  `location_id` int(11) DEFAULT NULL COMMENT '库位id',
  `create_date` datetime DEFAULT NULL COMMENT '入库时间',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `spares_quantity` int(11) DEFAULT '0' COMMENT '备品数量',
  `offline_quantity` int(11) DEFAULT '0' COMMENT '线下数量',
  `fba_lock_quantity` int(11) DEFAULT '0' COMMENT 'FBA锁定数量',
  `offline_lock_quantity` int(11) DEFAULT '0' COMMENT '线下锁定数量',
  `remark` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='库存库位明细表';


DROP TABLE IF EXISTS `psi_forecast_transport_analyse_order`;

CREATE TABLE `psi_forecast_transport_analyse_order` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country_code` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `peirod` int(11) DEFAULT NULL,
  `fba_stock` int(11) DEFAULT NULL,
  `oversea` int(11) DEFAULT NULL,
  `safe_inventory` int(11) DEFAULT NULL,
  `tip` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fill_up_tip` varchar(5000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_date` date DEFAULT NULL,
  `po_info` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cn_stock` int(11) DEFAULT NULL,
  `sales_info` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `trans_info` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  `promotions` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `air_gap` int(11) DEFAULT NULL,
  `sea_gap` int(11) DEFAULT NULL,
  `po_gap` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = '缺口分析表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE IF EXISTS `psi_forecast_transport_plan_order`;

CREATE TABLE `psi_forecast_transport_plan_order` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sku` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country_code` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `model` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '空海运',
  `transport_type` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '运输方式',
  `quantity` int(11) DEFAULT NULL COMMENT '系统数量',
  `state` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:运单 1：采购',
  `order_id` int(11) DEFAULT NULL,
  `other_desc` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `box_num` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = '计划表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `psi_transport_days`;

CREATE TABLE `psi_transport_days` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `country` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家编号',
  `model` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `month` varchar(5) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fba` int(11) DEFAULT NULL,
  `local` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
)COMMENT = '运输天表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE IF EXISTS `psi_product_mold_fee`;

CREATE TABLE `psi_product_mold_fee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `supplier_id` int(11) DEFAULT NULL COMMENT '供应商',
  `product_name` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名称',
  `mold_fee` decimal(11,2) DEFAULT NULL COMMENT '总模具费CNY',
  `return_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '0:返还 1:不返还',
  `return_num` int(11) DEFAULT NULL COMMENT '返还数量',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `purchase_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sale_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='模具费用信息';



ALTER TABLE `erp`.`lc_psi_purchase_order`   
  ADD COLUMN `invoice_flag` CHAR(1) DEFAULT '0'   NULL  COMMENT '开票' AFTER `pi_review_sta`;
  
  DROP TABLE IF EXISTS `lc_psi_purchase_order_invoice`;

CREATE TABLE `lc_psi_purchase_order_invoice` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `purchase_order_id` int(11) DEFAULT NULL COMMENT '采购订单id',
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名字',
  `quantity_ordered` int(11) DEFAULT NULL COMMENT '订单数量',
  `quantity_matched` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='采购增值发票';


DROP TABLE IF EXISTS `lc_psi_purchase_order_invoice_item`;

CREATE TABLE `lc_psi_purchase_order_invoice_item` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `invoice_id` int(11) DEFAULT NULL COMMENT '采购详情id',
  `invoice_no` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '发票号',
  `invoice_quantity` int(11) DEFAULT NULL COMMENT '发票数量',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='采购增值发票';

  

