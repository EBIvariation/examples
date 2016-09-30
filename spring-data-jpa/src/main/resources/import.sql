INSERT INTO `CLIENTS` (`client_id`, `access_token_validity_seconds`, `client_secret`, `refresh_token_validity_seconds`, `secret_required`) VALUES ('acme', '36000', '$2a$12$.W28H3HoNdcoESjkWQPui.1FbJX.Bm0QOA4Vhg60vbN2k1ix1Tq0a', '36000', '1');

INSERT INTO `CLIENT_AUTHORIZED_GRANTS` (`client_client_id`, `authorized_grant_types`) VALUES ('acme', 'authorization_code');
INSERT INTO `CLIENT_AUTHORIZED_GRANTS` (`client_client_id`, `authorized_grant_types`) VALUES ('acme', 'refresh_token');
INSERT INTO `CLIENT_AUTHORIZED_GRANTS` (`client_client_id`, `authorized_grant_types`) VALUES ('acme', 'password');

INSERT INTO `CLIENT_SCOPES` (`client_client_id`, `scope`) VALUES ('acme', 'openId');

INSERT INTO `USERS` (`email`, `password`) VALUES ('user', '$2a$12$vsi5TmluE8YR5iH6bpvXQO9ml1xewy.TKkiFEnCDJj5ONb5HZSY3C');

INSERT INTO `ROLES` (`role_id`, `description`) VALUES ('ROLE_ADMIN', 'God Mode');
INSERT INTO `ROLES` (`role_id`, `description`) VALUES ('ROLE_GROUP_ADMIN', 'Demigod Mode');

INSERT INTO `USER_ROLES` (`role_id`, `user_id`) VALUES ('ROLE_ADMIN', 'user');

INSERT INTO `GROUPS` (`group_id`, `group_description`, `group_name`) VALUES ('variations', 'EBI Variation team', 'Variation Team');

INSERT INTO `GROUP_ROLES` (`role_id`, `group_id`) VALUES ('ROLE_GROUP_ADMIN', 'variations');

INSERT INTO `GROUP_USERS` (`group_id`, `user_id`) VALUES ('variations', 'user');



