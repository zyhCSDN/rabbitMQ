
CREATE TABLE `goods_info` (
  `id` varchar(64) NOT NULL COMMENT '主键id',
  `goods_name` varchar(500) DEFAULT NULL COMMENT '商品名称',
  `goods_stock` int(8) DEFAULT NULL COMMENT '商品剩余库存',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `goods_info` VALUES ('1d0bf8eea9f041c1a5890b227bcca59d', '荣耀20', '100', '2019-06-12 15:17:32', '2019-06-12 15:17:36');
INSERT INTO `goods_info` VALUES ('3b84588790d14518bbdc5835cceb8526', '华为Mate20Pro', '100', '2019-06-12 11:42:49', '2019-06-12 11:42:54');
INSERT INTO `goods_info` VALUES ('a4de145764b54b798fde31289f8743f0', '华为P30', '100', '2019-06-12 15:17:57', '2019-06-12 15:18:00');
