/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80042
 Source Host           : localhost:3306
 Source Schema         : es

 Target Server Type    : MySQL
 Target Server Version : 80042
 File Encoding         : 65001

 Date: 13/05/2026 15:06:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码',
  `role` tinyint(1) NOT NULL DEFAULT 0 COMMENT '角色：0普通管理员，1超级管理员',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'super_admin', 'admin123', 1, 1, '2026-05-09 15:53:00');
INSERT INTO `admin` VALUES (2, 'admin', 'admin123', 0, 1, '2026-05-09 15:53:00');
INSERT INTO `admin` VALUES (3, 'pt', '123456', 0, 1, '2026-05-09 17:21:07');

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL,
  `product_id` bigint(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id`, `product_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  CONSTRAINT `favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `favorite_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (16, 1, 2);
INSERT INTO `favorite` VALUES (15, 1, 3);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `from_id` bigint(0) NOT NULL,
  `to_id` bigint(0) NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `send_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `from_id`(`from_id`) USING BTREE,
  INDEX `to_id`(`to_id`) USING BTREE,
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`from_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`to_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(0) NOT NULL,
  `buyer_id` bigint(0) NOT NULL,
  `seller_id` bigint(0) NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '待确认',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  INDEX `buyer_id`(`buyer_id`) USING BTREE,
  INDEX `seller_id`(`seller_id`) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 3, 3, 1, '已完成', '2026-04-23 15:43:34');
INSERT INTO `orders` VALUES (2, 1, 3, 1, '已取消', '2026-04-29 08:44:00');
INSERT INTO `orders` VALUES (3, 2, 3, 1, '待确认', '2026-04-29 10:24:58');
INSERT INTO `orders` VALUES (4, 4, 2, 1, '已完成', '2026-05-11 15:15:57');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL,
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `user_id` bigint(0) NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '在售',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品分类',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '记录更新时间（自动更新）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '1', 1.00, '更新成功', '/uploads/image/1776823857262_哲风壁纸02-粉色.png', 1, '已售', '2026-04-21 09:26:33', '数码产品', '2026-04-22 10:10:57');
INSERT INTO `product` VALUES (2, '1', 1.00, '1', '/uploads/image/1776822178725_哲风壁纸02-粉色.png', 1, '已售', '2026-04-22 09:42:59', '数码产品', '2026-04-22 09:42:58');
INSERT INTO `product` VALUES (3, '4', 3.00, '3', '/uploads/image/1776823779359_哲风壁纸02-粉色.png', 1, '已售', '2026-04-22 10:09:39', '数码产品', '2026-05-08 16:21:10');
INSERT INTO `product` VALUES (4, '1', 1.00, '1', '/uploads/image/1778483196269_哲风壁纸02-粉色.png', 1, '已售', '2026-05-11 15:06:36', '数码产品', '2026-05-11 15:06:36');
INSERT INTO `product` VALUES (8, '555', 5555.00, '5555555', '/uploads/image/1778487187060_哲风壁纸02-粉色.png', 1, '在售', '2026-05-11 16:13:07', '家居用品', '2026-05-11 16:13:07');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'user',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `status` int(0) NULL DEFAULT 1 COMMENT '用户状态: 1-启用, 0-禁用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'wul', '123456', 'user', '2026-04-18 22:54:26', 1);
INSERT INTO `user` VALUES (2, 'wulu', '123456', 'user', '2026-04-19 14:45:51', 1);
INSERT INTO `user` VALUES (3, 'wulu1', '123456', 'user', '2026-04-23 15:35:51', 1);

SET FOREIGN_KEY_CHECKS = 1;
