//
// Created by Dmitrii Chebykin on 11.05.2025.
//

#ifndef SENSOR_H
#define SENSOR_H
// #include <sys/_types/_uuid_t.h>
#include <uuid/uuid.h>
#include <open62541/server.h>

typedef enum Status {
    GOOD,
    NOT_ACTIVE,
    BAD
} Status;

typedef struct Sensor {
    char* id;
    char* name;
    Status status;
    double value;
    double max_value;
}Sensor;


typedef struct SensorArray {
    Sensor* sensors;
    int size;
    int max_size;
    size_t sensors_count;
} SensorArray;


// Элемент связного списка для одной ячейки хеш-таблицы
typedef struct MapNode {
    char* key;          // ключ (строка)
    Sensor value;       // значение
    struct MapNode* next;
} MapNode;

// Хеш-таблица
typedef struct {
    MapNode** buckets;
    size_t bucket_count;
} Map;



// Прототипы
Sensor Sensor_new(char* uuid, char* name, Status status, double value, double max_value);
void sensor_init_defaults(Sensor* s);
void addSensor(const Sensor *sensor, UA_UInt16 nsIndex,
               UA_Server *server, UA_DataType dataType,
               UA_ServerCallback callback, UA_Double interval); // todo: add sensor in server
double generateSensorValue(const Sensor sensor);
char* getSensorStatus_string(Sensor sensor, char* status);
Status parseStatus(char* status);

Map* map_create(size_t bucket_count);
void map_destroy(Map* map);
int map_put(Map* map, const char* key, Sensor value);
Sensor* map_get(Map* map, const char* key);
int map_remove(Map* map, const char* key);
size_t map_size(Map *map);
Sensor* map_values_array(Map *map, size_t *out_count);


int add_sensor_to_file(const char* filename, const Sensor* sensor);
int parseSensors(char* filename, SensorArray *sensors);
void uuid_to_string(const uuid_t uuid, char* out_str);










#endif //SENSOR_H
