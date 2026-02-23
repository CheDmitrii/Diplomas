//
// Created by Dmitrii Chebykin on 11.05.2025.
//
#include "sensor.h"

#include <math.h>
#include <stdio.h>
#include <yaml.h>
#include <cyaml/cyaml.h>
#include <uuid/uuid.h>

#include "ua_util_internal.h"


#define MAX_SENSOR_COUNT 500



int parse_uuid(const char *uuid_str, uuid_t uuid_out);

Sensor Sensor_new(char *uuid, char *name, Status status, double value, double max_value) {
    Sensor sensor;
    // uuid_t uuid;
    // parse_uuid(uuid_str, uuid);
    // memcpy(sensor.id, uuid, sizeof(uuid_t));
    sensor.id = uuid;
    sensor.name = name;
    sensor.status = status;
    sensor.value = value;
    sensor.max_value = max_value;
    return sensor;
}

void addSensor(const Sensor *sensor, UA_UInt16 nsIndex,
               UA_Server *server, UA_DataType dataType,
               UA_ServerCallback callback, UA_Double interval) {
    // UA_NodeId currunetNode = UA_NODEID_STRING(nsIndex, sensor->id);

    UA_NodeId currunetNode = UA_NODEID_STRING_ALLOC(nsIndex, sensor->id);//UA_NODEID_STRING(nsIndex, sensor->id);
    UA_VariableAttributes attr = UA_VariableAttributes_default;
    attr.displayName = UA_LOCALIZEDTEXT("en-US", sensor->id);
    attr.dataType = dataType.typeId;
    attr.accessLevel = UA_ACCESSLEVELMASK_READ | UA_ACCESSLEVELMASK_WRITE;


    UA_Server_addVariableNode(server, currunetNode,
                              UA_NODEID_NUMERIC(0, UA_NS0ID_OBJECTSFOLDER),
                              UA_NODEID_NUMERIC(0, UA_NS0ID_ORGANIZES),
                              UA_QUALIFIEDNAME(nsIndex, "Sensor"),
                              UA_NODEID_NUMERIC(0, UA_NS0ID_BASEDATAVARIABLETYPE),
                              attr, NULL, NULL);



    UA_NodeId *nodeIdCopy = malloc(sizeof(UA_NodeId));
    if(!nodeIdCopy) {
        fprintf(stderr, "Memory allocation failed\n");
        UA_NodeId_clear(&currunetNode);
        return;
    }
    *nodeIdCopy = currunetNode;
    // UA_NodeId_copy(&currunetNode, nodeIdCopy);

    UA_Server_addRepeatedCallback(server, callback, nodeIdCopy, interval, NULL);
}

double generateSensorValue(const Sensor sensor) {
    double res, delta = sensor.max_value == -1 ? 7 : sensor.max_value / 3,
            scale = rand() % 11, prob = rand() % 101;
    int sign;
    if (sensor.max_value != -1 && sensor.value > sensor.max_value) {
        sign = prob < 75 ? -1 : 1;
    } else {
        sign = prob < 23 ? -1 : 1;
    }
    res = scale * sign * delta / 10 + sensor.value;
    if (res < 0) {
        return 0.0;
    }
    return round(res * 100.0) / 100.0;
}

char* getSensorStatus_string(Sensor sensor, char* result) {
    char* status;
    if (sensor.status == GOOD) {
        status = "GOOD";
        result = strdup(status);
        // strncpy(result, status, sizeof(status) - 1);
        // strcpy(result, status);
        return "GOOD";
    }
    if (sensor.status == BAD) {
        status = "BAD";
        result = strdup(status);
        // strncpy(result, status, sizeof(status) - 1);
        // strcpy(result, status);
        return "BAD";
    }
    status = "NOT_ACTIVE";
    result = strdup(status);
    // strncpy(result, status, sizeof(status) - 1);
    // strcpy(result, status);
    return "NOT_ACTIVE";
}

// Хеш-функция djb2 для строк
static unsigned long hash_string(const char *str) {
    unsigned long hash = 5381;
    int c;
    while ((c = *str++))
        hash = ((hash << 5) + hash) + c; // hash * 33 + c
    return hash;
}

// Создаёт новую хеш-таблицу с заданным числом корзин
Map *map_create(size_t bucket_count) {
    Map *map = malloc(sizeof(Map));
    if (!map) return NULL;
    map->bucket_count = bucket_count;
    map->buckets = calloc(bucket_count, sizeof(MapNode *));
    if (!map->buckets) {
        free(map);
        return NULL;
    }
    return map;
}

// Освобождает память, занятую хеш-таблицей
void map_destroy(Map *map) {
    if (!map) return;
    for (size_t i = 0; i < map->bucket_count; i++) {
        MapNode *node = map->buckets[i];
        while (node) {
            MapNode *next = node->next;
            free(node->key);
            // Если в Sensor есть динамическая память (например, name), её тоже надо освободить
            free(node->value.name);
            free(node);
            node = next;
        }
    }
    free(map->buckets);
    free(map);
}

// Добавляет или обновляет значение по ключу
int map_put(Map *map, const char *key, Sensor value) {
    if (!map || !key) return 0;
    unsigned long hash = hash_string(key);
    size_t index = hash % map->bucket_count;

    MapNode *node = map->buckets[index];
    while (node) {
        if (strcmp(node->key, key) == 0) {
            // Обновляем существующее значение
            // Освобождаем старое имя и копируем новое
            free(node->value.name);
            node->value = value;
            node->value.name = strdup(value.name);
            return 1;
        }
        node = node->next;
    }

    // Создаём новый узел
    MapNode *new_node = malloc(sizeof(MapNode));
    if (!new_node) return 0;
    new_node->key = strdup(key);
    if (!new_node->key) {
        free(new_node);
        return 0;
    }
    new_node->value = value;
    new_node->value.name = strdup(value.name);
    new_node->next = map->buckets[index];
    map->buckets[index] = new_node;
    return 1;
}

// Получает указатель на значение по ключу, или NULL если нет
Sensor *map_get(Map *map, const char *key) {
    if (!map || !key) return NULL;
    unsigned long hash = hash_string(key);
    size_t index = hash % map->bucket_count;

    MapNode *node = map->buckets[index];
    while (node) {
        if (strcmp(node->key, key) == 0) {
            return &node->value;
        }
        node = node->next;
    }
    return NULL;
}

// Удаляет элемент по ключу
int map_remove(Map *map, const char *key) {
    if (!map || !key) return 0;
    unsigned long hash = hash_string(key);
    size_t index = hash % map->bucket_count;

    MapNode *node = map->buckets[index];
    MapNode *prev = NULL;
    while (node) {
        if (strcmp(node->key, key) == 0) {
            if (prev) prev->next = node->next;
            else map->buckets[index] = node->next;
            free(node->key);
            free(node->value.name);
            free(node);
            return 1;
        }
        prev = node;
        node = node->next;
    }
    return 0;
}

// Функция для подсчёта количества элементов в мапе
size_t map_size(Map *map) {
    if (!map) return 0;
    size_t count = 0;
    for (size_t i = 0; i < map->bucket_count; i++) {
        MapNode *node = map->buckets[i];
        while (node) {
            count++;
            node = node->next;
        }
    }
    return count;
}

// Функция, которая возвращает динамический массив значений и записывает размер в out_count
Sensor* map_values_array(Map *map, size_t *out_count) {
    if (!map || !out_count) return NULL;

    size_t count = map_size(map);
    *out_count = count;
    if (count == 0) return NULL;

    Sensor *array = malloc(sizeof(Sensor) * count);
    if (!array) {
        *out_count = 0;
        return NULL;
    }

    size_t idx = 0;
    for (size_t i = 0; i < map->bucket_count; i++) {
        MapNode *node = map->buckets[i];
        while (node) {
            // Копируем значение
            array[idx] = node->value;

            // Если в Sensor есть динамические поля (например, name), нужно скопировать их отдельно
            // Предположим, что name - строка, сделаем strdup
            if (node->value.name) {
                array[idx].name = strdup(node->value.name);
                if (!array[idx].name) {
                    // Если strdup не удался, нужно освободить уже выделенные строки и память
                    for (size_t j = 0; j < idx; j++) {
                        free(array[j].name);
                    }
                    free(array);
                    *out_count = 0;
                    return NULL;
                }
            }
            idx++;
            node = node->next;
        }
    }

    return array;
}

// Вспомогательная функция для преобразования одного HEX символа в число 0..15
int hexCharToInt(char c) {
    if ('0' <= c && c <= '9') return c - '0';
    if ('a' <= c && c <= 'f') return c - 'a' + 10;
    if ('A' <= c && c <= 'F') return c - 'A' + 10;
    return -1; // ошибка
}

// Функция парсинга UUID из строки в массив байт
// Возвращает 0 при успехе, -1 при ошибке формата
int parse_uuid(const char *uuid_str, uuid_t uuid_out) {
    int i = 0; // индекс по uuid_out
    int j = 0; // индекс по uuid_str

    while (uuid_str[j] != '\0' && i < 16) {
        if (uuid_str[j] == '-') {
            j++; // пропускаем дефис
            continue;
        }
        int hi = hexCharToInt(uuid_str[j]);
        int lo = hexCharToInt(uuid_str[j + 1]);
        if (hi == -1 || lo == -1) {
            return -1; // неверный символ
        }
        *(uuid_out + i) = (hi << 4) | lo;
        i++;
        j += 2;
    }
    if (i != 16) {
        return -1; // длина не совпала
    }
    return 0;
}

void uuid_to_string(const uuid_t uuid, char* out_str) {
    sprintf(out_str,
        "%02x%02x%02x%02x-"
        "%02x%02x-"
        "%02x%02x-"
        "%02x%02x-"
        "%02x%02x%02x%02x%02x%02x",
        uuid[0], uuid[1], uuid[2], uuid[3],
        uuid[4], uuid[5],
        uuid[6], uuid[7],
        uuid[8], uuid[9],
        uuid[10], uuid[11], uuid[12], uuid[13], uuid[14], uuid[15]
    );
}

Status parseStatus(char* status) { // strcasecmp - compare ignoring case
    if (strcasecmp(status, "GOOD") == 0) {
        return GOOD;
    }
    if (strcasecmp(status, "BAD") == 0) {
        return BAD;
    }
    return NOT_ACTIVE;
}

void sensor_init_defaults(Sensor* s) {
    memset(s, 0, sizeof(Sensor));      // обнуляем всю структуру
    s->status = NOT_ACTIVE;                  // статус по умолчанию
    s->max_value = -1.0;              // пример дефолтного максимального значения
    s->value = 0.0;                   // дефолтное значение
    s->name = "none name";                   // или strdup("default name"), если нужно
    // id можно оставить нулевым или задать дефолтный UUID
}

int add_sensor_to_file(const char* filename, const Sensor *sensor) {
    // Открываем файл для записи ("w" - перезапись файла)
    FILE *file = fopen(filename, "a");
    if (file == NULL) {
        perror("Ошибка открытия файла");
        return 1; // Возвращаем ошибку
    }

    char* result = "\n  - id: ";

    fprintf(file, "\n  - id: %s\n    name: %s", sensor->id, sensor->name);
    // strcpy(result, sensor.id);
    // strcat(result, sensor.id);

    // strcat(result, "\n    name: ");
    // strcat(result, sensor->name);

    if (sensor->max_value != -1) {
        // sprintf(result, "\n    max-value: %.2f", sensor->max_value);
        fprintf(file, "\n    max-value: %.2f", sensor->max_value);
        // strcat(result, "\n    ");
        // strcat(result, sensor.max_value);
    }

    // strcat(result, "\n    status: ");
    char* statusStr = "";
    // getSensorStatus_string(*sensor, statusStr);
    // strcat(result, statusStr);
    fprintf(file, "\n    status: %s", getSensorStatus_string(*sensor, statusStr));




    // fprintf(file, "%s", result);

    // // Записываем строку в файл
    // if (fputs(data, file) == EOF) {
    //     perror("Ошибка записи в файл");
    //     fclose(file);
    //     return 2; // Возвращаем ошибку записи
    // }

    // Закрываем файл
    fclose(file);
    return 0;
}

int parseSensors(char *filename, SensorArray *sensors) {
    FILE *fh = fopen(filename, "r");
    if (!fh) {
        perror("fopen");
        return 1;
    }

    yaml_parser_t parser;
    yaml_event_t event;

    if (!yaml_parser_initialize(&parser)) {
        fprintf(stderr, "Failed to initialize parser\n");
        fclose(fh);
        return 1;
    }
    yaml_parser_set_input_file(&parser, fh);

    Sensor parsedSensors[MAX_SENSOR_COUNT];
    int sensor_count = 0;

    enum { NONE, IN_SENSORS_KEY, IN_SEQUENCE, IN_MAPPING, IN_KEY, IN_VALUE }
            state = NONE;
    char current_key[64] = {0};
    char scalar_value[256] = {0};

    while (1) {
        if (!yaml_parser_parse(&parser, &event)) {
            fprintf(stderr, "Parser error\n");
            break;
        }

        switch (event.type) {
            case YAML_SCALAR_EVENT:
                if (state == NONE) {
                    // Ожидаем ключ верхнего уровня
                    if (strcmp((char *) event.data.scalar.value, "sensors") == 0) {
                        state = IN_SENSORS_KEY;
                    }
                } else if (state == IN_KEY) {
                    strncpy(current_key, (char*)event.data.scalar.value, sizeof(current_key)-1);
                    state = IN_VALUE;
                } else if (state == IN_VALUE) {
                    strncpy(scalar_value, (char*)event.data.scalar.value, sizeof(scalar_value)-1);

                    if (sensor_count >= MAX_SENSOR_COUNT) {
                        fprintf(stderr, "Too many sensors\n");
                        break;
                    }

                    Sensor *s = &parsedSensors[sensor_count];

                    if (strcmp(current_key, "id") == 0) {
                        printf("%s\n", scalar_value);
                        // if (parse_uuid(scalar_value, s->id) != 0) {
                        //     fprintf(stderr, "Invalid UUID format: %s\n", scalar_value);
                        // }
                        s->id = strdup(scalar_value);
                        state = IN_KEY;
                        break;
                    }
                    if (strcmp(current_key, "name") == 0) {
                        printf("%s\n", scalar_value);
                        // if (parse_uuid(scalar_value, s->id) != 0) {
                        //     fprintf(stderr, "Invalid UUID format: %s\n", scalar_value);
                        // }
                        s->name = strdup(scalar_value);
                        state = IN_KEY;
                        break;
                    }
                    if (strcmp(current_key, "max-value") == 0) {
                        double rrrr = atof(scalar_value);
                        printf("value %f\n", rrrr);
                        s->max_value = atof(scalar_value);
                        state = IN_KEY;
                        break;
                    }
                    if (strcmp(current_key, "status") == 0) {
                        s->status = parseStatus(scalar_value);
                        state = IN_KEY;
                        break;
                    }
                    if (strcmp(current_key, "value") == 0) {
                        s->value = atof(scalar_value);
                        state = IN_KEY;
                        break;
                    }
                    state = IN_MAPPING; // ждем следующий ключ или конец mapping
                }
                break;

            case YAML_MAPPING_START_EVENT:
                if (state == IN_SEQUENCE) {
                    // Начинается новый элемент sensors
                    if (sensor_count < MAX_SENSOR_COUNT) {
                        sensor_init_defaults(&parsedSensors[sensor_count]);
                        // memset(&parsedSensors[sensor_count], 0, sizeof(Sensor));
                        state = IN_KEY;
                    }
                }
                break;

            case YAML_MAPPING_END_EVENT:
                if (state == IN_KEY || state == IN_MAPPING) {
                    sensor_count++;
                    state = IN_SEQUENCE;
                }
                break;

            case YAML_SEQUENCE_START_EVENT:
                if (state == IN_SENSORS_KEY) {
                    state = IN_SEQUENCE;
                }
                break;

            case YAML_SEQUENCE_END_EVENT:
                if (state == IN_SEQUENCE) {
                    state = NONE;
                }
                break;

            case YAML_STREAM_END_EVENT:
                goto done;

            default:
                break;
        }

        yaml_event_delete(&event);
    }


done:
    yaml_event_delete(&event);
    yaml_parser_delete(&parser);
    fclose(fh);

    printf("Parsed %d sensors:\n", sensor_count);
    for (int i = 0; i < sensor_count; i++) {
        printf("Sensor %d: max_value=%.2f, id=%s", i, parsedSensors[i].max_value, parsedSensors[i].id);
        // for (int b = 0; b < 16; b++) printf("%02x", parsedSensors[i].id[b]);
        printf(", status: %d", parsedSensors[i].status);
        printf("\n");
    }

    // sensors->sensors = parsedSensors;
    // sensors->size = sensor_count;
    // sensors->max_size = MAX_SENSOR_COUNT;

    sensors->sensors = malloc(MAX_SENSOR_COUNT * sizeof(Sensor));
    if(sensors->sensors == NULL) {
        // обработка ошибки
    }
    memcpy(sensors->sensors, parsedSensors, MAX_SENSOR_COUNT * sizeof(Sensor));
    sensors->size = sensor_count;
    sensors->max_size = MAX_SENSOR_COUNT;
    return 0;
}
