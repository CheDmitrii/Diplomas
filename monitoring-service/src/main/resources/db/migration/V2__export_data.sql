-- add role
INSERT INTO system.role (id, description, name) VALUES ('8739f3fc-0403-4c85-9b90-1cbf5956bd1e', 'desc', 'ADMIN');
INSERT INTO system.role (id, description, name) VALUES ('5aeb8f94-71ab-4d5f-b1d0-a5cdd5afc417', 'qwerty', 'USER');

-- add user
INSERT INTO system."user" (id, role_id, first_name, last_name, login, password) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '5aeb8f94-71ab-4d5f-b1d0-a5cdd5afc417', 'qwe', 'rty', 'pfp', 'ddd');

-- add machine
INSERT INTO system.machine (id, name, description) VALUES ('17de18e9-a451-4abd-b96c-85750938bf2f', 'Гамогенизатор', null);
INSERT INTO system.machine (id, name, description) VALUES ('5ad70b01-2560-4df3-b8ad-659aab30c638', 'Резервуар сквашивания', null);
INSERT INTO system.machine (id, name, description) VALUES ('22cc325d-8b46-4958-ae5b-d0d87fa14d85', 'Резервуар смешивания', null);

-- add sensor
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('976b3741-2f02-48fc-988c-53d86e3338fe', 'Сквашивание.температура', '°t', null, '5ad70b01-2560-4df3-b8ad-659aab30c638');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('36574620-1278-49cb-9dcf-fd2a34148aa2', 'Гомогенизация.давление', 'МПа', null, '17de18e9-a451-4abd-b96c-85750938bf2f');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 'Гомогенизация.температура', '°t', null, '17de18e9-a451-4abd-b96c-85750938bf2f');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('3c703a09-0889-44d6-949b-0996732470d2', 'Смешивание2.температура', '°t', null, '22cc325d-8b46-4958-ae5b-d0d87fa14d85');

-- add sensor_journal
INSERT INTO system.sensor_journal (id, sensor_id, value, time) VALUES ('83e4d909-df46-469c-9a95-48293a4a35bc', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 1, '2025-05-09 11:25:14.223000 +00:00');
INSERT INTO system.sensor_journal (id, sensor_id, value, time) VALUES ('34f9c3b0-d0ac-487a-a170-bdefd1581d1e', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 2, '2025-05-08 11:25:33.138000 +00:00');
INSERT INTO system.sensor_journal (id, sensor_id, value, time) VALUES ('23c7013c-5088-46dd-be85-508ac05fa88c', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 3, '2025-05-07 11:25:43.766000 +00:00');
INSERT INTO system.sensor_journal (id, sensor_id, value, time) VALUES ('c80decdf-efac-44b8-8f1e-bdc996bdab2f', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 4, '2025-05-06 11:25:50.492000 +00:00');
INSERT INTO system.sensor_journal (id, sensor_id, value, time) VALUES ('ee02f63e-06c8-4367-924b-d81ea432f208', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 5, '2025-05-03 11:25:57.176000 +00:00');

-- add sensor_permission
INSERT INTO system.sensor_permission (user_id, sensor_id) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '976b3741-2f02-48fc-988c-53d86e3338fe');
INSERT INTO system.sensor_permission (user_id, sensor_id) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd');
INSERT INTO system.sensor_permission (user_id, sensor_id) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '3c703a09-0889-44d6-949b-0996732470d2');
INSERT INTO system.sensor_permission (user_id, sensor_id) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '36574620-1278-49cb-9dcf-fd2a34148aa2');

-- add reference
INSERT INTO system.reference (id, name, value, sensor_id) VALUES ('72ee569d-5f59-486e-97b1-d4f02aeff86b', 'Сквашивание.температура', 20, '976b3741-2f02-48fc-988c-53d86e3338fe');
INSERT INTO system.reference (id, name, value, sensor_id) VALUES ('9e089214-f2ad-4666-9ba3-f20f6e695e41', 'Гомогенизация.температура', 65, '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd');
INSERT INTO system.reference (id, name, value, sensor_id) VALUES ('c61f5384-444b-4e88-9c73-e41c35408e60', 'Гомогенизация.давление', 15, '36574620-1278-49cb-9dcf-fd2a34148aa2');
INSERT INTO system.reference (id, name, value, sensor_id) VALUES ('6a5c9b87-ceff-4d92-abad-cbacaaa44ba9', 'Смешивание2.температура', 14, '3c703a09-0889-44d6-949b-0996732470d2');