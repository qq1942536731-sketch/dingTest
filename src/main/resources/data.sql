DELETE FROM sys_user_role;
DELETE FROM sys_role_menu;
DELETE FROM coupon_claim;
DELETE FROM coupon_activity;
DELETE FROM sys_user;
DELETE FROM sys_role;
DELETE FROM sys_menu;

INSERT INTO sys_menu(id, name, path, permission, sort_order) VALUES
(1, '活动管理', '/activities', 'activity:view', 1),
(2, '用户管理', '/users', 'user:view', 2),
(3, '角色管理', '/roles', 'role:view', 3),
(4, '菜单权限', '/menus', 'menu:view', 4),
(5, '活动编辑', '/activities', 'activity:edit', 5),
(6, '手动发券', '/activities', 'activity:issue', 6);

INSERT INTO sys_role(id, code, name) VALUES
(1, 'ADMIN', '超级管理员'),
(2, 'OPS', '运营人员'),
(3, 'AUDITOR', '审计人员');

INSERT INTO sys_role_menu(role_id, menu_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),
(2,1),(2,5),(2,6),
(3,1),(3,2),(3,3),(3,4);

INSERT INTO sys_user(id, username, password, display_name, enabled) VALUES
(1, 'admin', '$2b$10$KkaolGYmeBPtRA0Vo.TWEeBKUfa9yULFty//ZA6.ejFqZ7Jk3meTy', '超级管理员', b'1'),
(2, 'ops', '$2b$10$KkaolGYmeBPtRA0Vo.TWEeBKUfa9yULFty//ZA6.ejFqZ7Jk3meTy', '运营同学', b'1'),
(3, 'auditor', '$2b$10$KkaolGYmeBPtRA0Vo.TWEeBKUfa9yULFty//ZA6.ejFqZ7Jk3meTy', '审计同学', b'1');

INSERT INTO sys_user_role(user_id, role_id) VALUES
(1,1),(2,2),(3,3);

INSERT INTO coupon_activity(id, name, description, status, start_time, end_time, total_stock, issued_stock, version) VALUES
(1, '春季满减券', '用于春季促销的 50 元优惠券', 'READY', '2026-03-18 00:00:00', '2026-03-31 23:59:59', 100, 0, 0),
(2, '新人首单券', '新用户首单专享券', 'RUNNING', '2026-03-01 00:00:00', '2026-03-31 23:59:59', 50, 12, 0);
