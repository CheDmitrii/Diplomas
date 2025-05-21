-- add role
INSERT INTO system.role (id, description, name) VALUES ('8739f3fc-0403-4c85-9b90-1cbf5956bd1e', 'desc', 'ADMIN');
INSERT INTO system.role (id, description, name) VALUES ('5aeb8f94-71ab-4d5f-b1d0-a5cdd5afc417', 'qwerty', 'USER');

-- add user
INSERT INTO system."user" (id, role_id, first_name, last_name, login, password) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '5aeb8f94-71ab-4d5f-b1d0-a5cdd5afc417', 'qwe', 'rty', 'pfp', 'ddd');
INSERT INTO system."user" (id, role_id, first_name, last_name, login, password) VALUES ('15ad4a35-a925-4b92-b54a-1111a412b999', '8739f3fc-0403-4c85-9b90-1cbf5956bd1e', 'qwe', 'rty', 'admin', 'admin');

-- add machine
INSERT INTO system.machine (id, name, description) VALUES ('17de18e9-a451-4abd-b96c-85750938bf2f', 'Гамогенизатор', null);
INSERT INTO system.machine (id, name, description) VALUES ('5ad70b01-2560-4df3-b8ad-659aab30c638', 'Заквасочный аппарат', null);
INSERT INTO system.machine (id, name, description) VALUES ('22cc325d-8b46-4958-ae5b-d0d87fa14d85', 'Распределительный бачок', null);
INSERT INTO system.machine (id, name, description) VALUES ('b0885962-1d48-4269-9b8d-8a7223b468e4', 'Пастеризационно-охладительная установка', null);
INSERT INTO system.machine (id, name, description) VALUES ('5899fc1f-5934-4759-a578-ac08ceb7d143', 'Насос1', null);
INSERT INTO system.machine (id, name, description) VALUES ('22ddefe6-50c4-4a16-9e84-68850faba880', 'Сепаратор молокоочиститель', null);
INSERT INTO system.machine (id, name, description) VALUES ('b64abc24-00b9-4a35-99b3-7ed8f46836bc', 'Резервуар', null);

-- add sensor
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('976b3741-2f02-48fc-988c-53d86e3338fe', 'Распределительный бачок.уровень', 'м2', null, '22cc325d-8b46-4958-ae5b-d0d87fa14d85');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('63ef82f5-86ad-44be-80ec-1afe20d571d7', 'Распределительный бачок.температура', '°t', null, '22cc325d-8b46-4958-ae5b-d0d87fa14d85');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('ea636933-a999-439c-9b0e-3ec36c129f75', 'Пастеризационно-охладительная установка.уровень', 'м2', null, 'b0885962-1d48-4269-9b8d-8a7223b468e4');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('1087010f-5f88-40a9-b7d0-dbaeb3ab3a2a', 'Пастеризационно-охладительная установка.температура', '°t', null, 'b0885962-1d48-4269-9b8d-8a7223b468e4');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('36574620-1278-49cb-9dcf-fd2a34148aa2', 'Гомогенизация.давление', 'МПа', null, '17de18e9-a451-4abd-b96c-85750938bf2f');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 'Гомогенизация.температура', '°t', null, '17de18e9-a451-4abd-b96c-85750938bf2f');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('a47cb5ac-af55-407b-81c5-107c96e95cdc', 'Гомогенизация.уровень', 'м2', null, '17de18e9-a451-4abd-b96c-85750938bf2f');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('a2589d51-4039-49ed-8ec5-156596685c72', 'Заквасочный аппарат.уровень', 'м2', null, '5ad70b01-2560-4df3-b8ad-659aab30c638');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('84dead43-305c-4e35-b6d2-61034fa7fdd9', 'Заквасочный аппарат.температура', '°t', null, '5ad70b01-2560-4df3-b8ad-659aab30c638');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('dd991a28-bf2a-4fe6-907f-4bb5eda632f0', 'Резервуар.температура', '°t', null, 'b64abc24-00b9-4a35-99b3-7ed8f46836bc');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('0b7b42fa-a54e-4713-ba10-f09b059f800d', 'Резервуар.уровень', 'м2', null, 'b64abc24-00b9-4a35-99b3-7ed8f46836bc');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('3c703a09-0889-44d6-949b-0996732470d2', 'Насос1.частота', 'об/мин', null, '5899fc1f-5934-4759-a578-ac08ceb7d143');
INSERT INTO system.sensor (id, name, type, description, machine_id) VALUES ('8d526fcf-73bb-4a48-8f70-0a3a8d88de5b', 'Насос1.потом', 'л/мин', null, '5899fc1f-5934-4759-a578-ac08ceb7d143');

-- add sensor_journal
INSERT INTO system.sensor_journal (id, sensor_id, value, time) VALUES ('83e4d909-df46-469c-9a95-48293a4a35bc', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 1, '2025-05-09 11:25:14.223000 +00:00');
INSERT INTO system.sensor_journal (id, sensor_id, value, time) VALUES ('34f9c3b0-d0ac-487a-a170-bdefd1581d1e', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 2, '2025-05-08 11:25:33.138000 +00:00');
INSERT INTO system.sensor_journal (id, sensor_id, value, time) VALUES ('23c7013c-5088-46dd-be85-508ac05fa88c', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 3, '2025-05-07 11:25:43.766000 +00:00');
INSERT INTO system.sensor_journal (id, sensor_id, value, time) VALUES ('c80decdf-efac-44b8-8f1e-bdc996bdab2f', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 4, '2025-05-06 11:25:50.492000 +00:00');
INSERT INTO system.sensor_journal (id, sensor_id, value, time) VALUES ('ee02f63e-06c8-4367-924b-d81ea432f208', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd', 5, '2025-05-03 11:25:57.176000 +00:00');

-- add sensor_permission
INSERT INTO system.sensor_permission (user_id, sensor_id) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '976b3741-2f02-48fc-988c-53d86e3338fe'); -- pfp
INSERT INTO system.sensor_permission (user_id, sensor_id) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd');
INSERT INTO system.sensor_permission (user_id, sensor_id) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '3c703a09-0889-44d6-949b-0996732470d2');
INSERT INTO system.sensor_permission (user_id, sensor_id) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '36574620-1278-49cb-9dcf-fd2a34148aa2');
INSERT INTO system.sensor_permission (user_id, sensor_id) VALUES ('15ad4a35-a925-4b92-b54a-4030a412b846', '1087010f-5f88-40a9-b7d0-dbaeb3ab3a2a');

-- add reference
INSERT INTO system.reference (id, name, value, sensor_id) VALUES ('72ee569d-5f59-486e-97b1-d4f02aeff86b', 'Сквашивание.температура', 20, '976b3741-2f02-48fc-988c-53d86e3338fe');
INSERT INTO system.reference (id, name, value, sensor_id) VALUES ('9e089214-f2ad-4666-9ba3-f20f6e695e41', 'Гомогенизация.температура', 65, '59f6f71b-3ede-4c63-a162-fbc4dd53ffdd');
INSERT INTO system.reference (id, name, value, sensor_id) VALUES ('c61f5384-444b-4e88-9c73-e41c35408e60', 'Гомогенизация.давление', 15, '36574620-1278-49cb-9dcf-fd2a34148aa2');
INSERT INTO system.reference (id, name, value, sensor_id) VALUES ('6a5c9b87-ceff-4d92-abad-cbacaaa44ba9', 'Смешивание2.температура', 14, '3c703a09-0889-44d6-949b-0996732470d2');