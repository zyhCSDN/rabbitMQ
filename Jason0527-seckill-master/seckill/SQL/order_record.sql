CREATE TABLE `order_record` (
  `id` varchar(64) NOT NULL COMMENT '主键id',
  `user_id` varchar(64) DEFAULT NULL COMMENT '订单所属用户id',
  `goods_id` varchar(64) DEFAULT NULL COMMENT '商品id',
  `pay_status` int(1) DEFAULT NULL COMMENT '支付状态 0-超时未支付  1-已支付  2-待支付',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;