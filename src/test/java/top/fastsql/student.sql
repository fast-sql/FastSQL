SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `student`
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` varchar(36) NOT NULL,
  `name` varchar(10) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `home_address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES ('1', '小明', '23', '2017-07-11', null);
INSERT INTO `student` VALUES ('2', '李宇春', '12', '2017-06-06', null);
INSERT INTO `student` VALUES ('3', '人小气', '23', null, null);
INSERT INTO `student` VALUES ('4', '小红', '34', null, '成都');
INSERT INTO `student` VALUES ('5', '杰克', '5', null, '北京');
INSERT INTO `student` VALUES ('6', '大亮', '12', null, null);
INSERT INTO `student` VALUES ('7', '小李', '56', null, null);
INSERT INTO `student` VALUES ('8', '哇塞', '25', null, null);
INSERT INTO `student` VALUES ('9', '好好好', '22', null, null);
