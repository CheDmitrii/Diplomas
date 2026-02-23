#include <stdio.h>
#include <unistd.h>
#include <open62541/server_config_default.h>
#include <open62541/server.h>
#include <open62541/plugin/log_stdout.h>
#include <open62541/common.h>
#include <open62541/config.h>
#include <open62541/types.h>
#include <netinet/in.h>
#include "server/ua_server_internal.h"
#include <yaml.h>
#include <cyaml/cyaml.h>
#include <jansson.h>
#include <uuid/uuid.h>
#include "../lib/sensor.h"




UA_Server *server;
static const char* sensorNameSpace = "http://sensors";
UA_UInt16 nsIndex;
static const int defaultInterval = 2000;
Map *sensor_map;
json_t *root;
struct timespec ts;
static UA_Boolean running = true;



static void updateVariableCallback(UA_Server *server, void *data) {
    // srand((unsigned int)time(NULL));
    UA_NodeId *nodeId = (UA_NodeId*)data;
    // map_get(sensor_map, );

    clock_gettime(CLOCK_REALTIME, &ts);

    time_t now = ts.tv_sec;
    char timeStr[64];
    char number[20];
    int randomNumber = rand() % 10000;
    strftime(timeStr, sizeof(timeStr), "%Y-%m-%d %H:%M:%S", localtime(&now));

    // Преобразуем число в строку
    sprintf(number, " %d", randomNumber);

    // UA_String uaStr = UA_STRING(strcat(timeStr, number));
    UA_String uaStr = UA_STRING(timeStr);

    if(nodeId == NULL) {
        fprintf(stderr, "nodeId is NULL\n");
        return;
    }
    if(nodeId->identifierType != UA_NODEIDTYPE_STRING) {
        fprintf(stderr, "NodeId identifier is not a string\n");
        return;
    }
    if(nodeId->identifier.string.data == NULL) {
        fprintf(stderr, "NodeId string data is NULL\n");
        return;
    }

    size_t len = nodeId->identifier.string.length;
    char *cstr = malloc(len + 1);
    if(cstr) {
        memcpy(cstr, nodeId->identifier.string.data, len);
        // cstr[len] = '\0'; // добавить null-терминатор
        // теперь cstr - это корректная C-строка
    }

    printf("id: %s\n", cstr);
    printf("%s\n", timeStr);
    UA_Variant value;
    UA_Variant_setScalar(&value, &uaStr, &UA_TYPES[UA_TYPES_STRING]);

    printf("data_write\n");
    UA_Server_writeValue(server, *nodeId, value);
}



static void stopHandler(int sig) {
    UA_LOG_INFO(UA_Log_Stdout, UA_LOGCATEGORY_USERLAND, "Received Ctrl-C");
    running = false;
}





static void sensorCallback(UA_Server *server, void *data) {
    // srand((unsigned int)time(NULL));
    UA_NodeId *nodeId = (UA_NodeId*)data;
    if(nodeId == NULL) {
        fprintf(stderr, "nodeId is NULL\n");
        return;
    }
    if(nodeId->identifierType != UA_NODEIDTYPE_STRING) {
        fprintf(stderr, "NodeId identifier is not a string\n");
        return;
    }
    if(nodeId->identifier.string.data == NULL) {
        fprintf(stderr, "NodeId string data is NULL\n");
        return;
    }
    size_t len = nodeId->identifier.string.length;
    char *cstr = malloc(len + 1);
    if(cstr) {memcpy(cstr, nodeId->identifier.string.data, len);}
    // printf("id: %s\n", cstr);
    Sensor *sensor = map_get(sensor_map, cstr);
    if (sensor->status != GOOD) {return;}



    // struct timespec ts;
    clock_gettime(CLOCK_REALTIME, &ts);

    time_t now = ts.tv_sec;
    struct tm *lt = localtime(&now);
    char timeStr[64];
    char full_time[64];
    strftime(timeStr, sizeof(timeStr), "%Y-%m-%d %H:%M:%S", lt);
    long milliseconds = ts.tv_nsec / 1000000;
    // printf("%s.%03ld\n", timeStr, milliseconds);

    snprintf(full_time, sizeof(full_time), "%s.%03ld", timeStr, milliseconds);




    // time_t now = time(NULL);
    // char timeStr[64];
    // char number[20];
    // int randomNumber = rand() % 10000;
    // strftime(timeStr, sizeof(timeStr), "%Y-%m-%d %H:%M:%S", localtime(&now));
    // Преобразуем число в строку
    // sprintf(number, " %d", randomNumber);




    // UA_String uaStr = UA_STRING(strcat(timeStr, number));
    UA_String uaStr = UA_STRING(timeStr);


    sensor->value = generateSensorValue(*sensor);

    json_object_set_new(root, "id", json_string(cstr));
    json_object_set_new(root, "value", json_real(sensor->value));
    json_object_set_new(root, "time", json_string(full_time));

    char* json_res = json_dumps(root, JSON_INDENT(4));
    printf("%s\n", json_res);

    // printf("%f\n", sensor->value);
    UA_String ua_res = UA_STRING(json_res);
    UA_Variant value;
    UA_Variant_setScalar(&value, &ua_res, &UA_TYPES[UA_TYPES_STRING]);

    UA_Server_writeValue(server, *nodeId, value);
}

static UA_StatusCode checkSensors(UA_Server *server,
                 const UA_NodeId *sessionId, void *sessionContext,
                 const UA_NodeId *methodId, void *methodContext,
                 const UA_NodeId *objectId, void *objectContext,
                 size_t inputSize, const UA_Variant *input,
                 size_t outputSize, UA_Variant *output) {
    if(outputSize < 1) {
        return UA_STATUSCODE_BADINVALIDARGUMENT;
    }
    if (input[0].type != &UA_TYPES[UA_TYPES_STRING] || input[0].arrayLength < 1 || input[0].data == NULL) {
        return UA_STATUSCODE_BADINVALIDARGUMENT;
    }

    // Это массив строк
    // Обработка массива строк
    size_t strCount = input[0].arrayLength;
    UA_String *strArray = (UA_String *)input[0].data;
    json_t *json_arr = json_array();
    if(!json_arr) {
        fprintf(stderr, "Ошибка создания JSON массива\n");
        return UA_STATUSCODE_BAD;
    }
    for(size_t i = 0; i < strCount; i++) {
        // Пример: вывести строки в лог
        char* arrayData = strArray[i].data;
        printf("Received string: %.*s\n", (int)strArray[i].length, strArray[i].data);
        // char* status;
        Sensor *curSensor = map_get(sensor_map, arrayData);
        char* status = getSensorStatus_string(*curSensor, &status);

        json_t *sensor_json = json_object();
        json_object_set_new(sensor_json, "id", json_string(arrayData));
        json_object_set_new(sensor_json, "name", json_string(curSensor->name));
        json_object_set_new(sensor_json, "status", json_string(status ? status : ""));

        json_array_append_new(json_arr, sensor_json);
    }

    // todo: add json to answer;
    char* json_str = json_dumps(json_arr, JSON_INDENT(4));
    UA_String result = UA_STRING(json_str);
    UA_Variant_setScalarCopy(&output[0], &result, &UA_TYPES[UA_TYPES_STRING]);
    json_decref(json_arr);
    return UA_STATUSCODE_GOOD;
}

static UA_StatusCode checkAllSensors(UA_Server *server,
                 const UA_NodeId *sessionId, void *sessionContext,
                 const UA_NodeId *methodId, void *methodContext,
                 const UA_NodeId *objectId, void *objectContext,
                 size_t inputSize, const UA_Variant *input,
                 size_t outputSize, UA_Variant *output) {
    if(outputSize < 1) {
        return UA_STATUSCODE_BADINVALIDARGUMENT;
    }
    size_t sensor_map_size;
    Sensor *sensors = map_values_array(sensor_map, &sensor_map_size);
    if (!sensors) {
        return UA_STATUSCODE_GOOD;
    }
    json_t *json_arr = json_array();
    for (int i = 0; i < sensor_map_size; ++i) {
        json_t *sensor_json = json_object();
        // char* status;
        char* status = getSensorStatus_string(sensors[i], status);
        json_object_set_new(sensor_json, "id", json_string(sensors[i].id));
        json_object_set_new(sensor_json, "name", json_string(sensors[i].name));
        json_object_set_new(sensor_json, "status", json_string(status ? status : ""));

        json_array_append_new(json_arr, sensor_json);
        // json_decref(sensor_json);
    }
    // todo: add json to answer;
    char* json_str = json_dumps(json_arr, JSON_INDENT(4));
    UA_String result = UA_STRING(json_str);
    UA_Variant_setScalarCopy(&output[0], &result, &UA_TYPES[UA_TYPES_STRING]);
    json_decref(json_arr);

    return UA_STATUSCODE_GOOD;
}

static UA_StatusCode createSensor(UA_Server *server,
                 const UA_NodeId *sessionId, void *sessionContext,
                 const UA_NodeId *methodId, void *methodContext,
                 const UA_NodeId *objectId, void *objectContext,
                 size_t inputSize, const UA_Variant *input,
                 size_t outputSize, UA_Variant *output) {
    if(outputSize > 0 || inputSize != 1) {
        return UA_STATUSCODE_BADINVALIDARGUMENT;
    }
    if (!UA_Variant_hasScalarType(&input[0], &UA_TYPES[UA_TYPES_STRING])) {
        return UA_STATUSCODE_BADTYPEMISMATCH;
    }
    UA_String *inputStr = (UA_String *)input[0].data;
    if(inputStr == NULL) {
        UA_LOG_ERROR(UA_Log_Stdout, UA_LOGCATEGORY_USERLAND, "Input string is NULL");
        return UA_STATUSCODE_BADINVALIDARGUMENT;
    }

    char *json_str = malloc(inputStr->length + 1);
    if(!json_str) {
        return UA_STATUSCODE_BADOUTOFMEMORY;
    }
    memcpy(json_str, inputStr->data, inputStr->length);
    json_str[inputStr->length] = '\0';



    json_error_t error;
    json_t *root = json_loads(json_str, 0, &error);
    if(!root) {
        fprintf(stderr, "Ошибка парсинга JSON: %s\n", error.text);
        UA_LOG_ERROR(UA_Log_Stdout, UA_LOGCATEGORY_USERLAND, "Parsing error JSON: %s", error.text);
        return UA_STATUSCODE_BADDATALOST;
    }

    Sensor sensor;
    sensor_init_defaults(&sensor);

    json_t *id_json = json_object_get(root, "id");
    if (json_is_string(id_json)) {
        const char *id_str = json_string_value(id_json);
        sensor.id = strdup(id_str);
    } else {
        UA_LOG_ERROR(UA_Log_Stdout, UA_LOGCATEGORY_USERLAND, "Parsing error: id isn't string");
        return UA_STATUSCODE_BADDATALOST;
    }

    json_t *name_json = json_object_get(root, "name");
    if (json_is_string(name_json)) {
        const char *name_str = json_string_value(name_json);
        sensor.name = strdup(name_str);
    }

    json_t *max_value_json = json_object_get(root, "max-value");
    if (json_is_real(max_value_json)) {
        sensor.max_value = json_real_value(max_value_json);
    }

    json_t *status_json = json_object_get(root, "status");
    if (json_is_string(status_json)) {
        const char *status_str = json_string_value(status_json);
        sensor.status = parseStatus(status_str);
    }


    Sensor *get = map_get(sensor_map, sensor.id);
    if (get) {
        UA_LOG_ERROR(UA_Log_Stdout, UA_LOGCATEGORY_USERLAND, "Parsing error: sensor with this id %s already exists", sensor.id);
        return UA_STATUSCODE_BADINVALIDARGUMENT;
    }




    addSensor(&sensor, nsIndex, server, UA_TYPES[UA_TYPES_STRING], sensorCallback, defaultInterval);
    map_put(sensor_map, sensor.id, sensor);
    add_sensor_to_file("../config/sensor.yaml", &sensor);

    json_decref(root);
    free(json_str);
    return UA_STATUSCODE_GOOD;
}




// todo drop method
int write_to_file(const char *filename, const char* data) {
    // Открываем файл для записи ("w" - перезапись файла)
    FILE *file = fopen(filename, "a");
    if (file == NULL) {
        perror("Ошибка открытия файла");
        return 1; // Возвращаем ошибку
    }

    // Записываем строку в файл
    if (fputs(data, file) == EOF) {
        perror("Ошибка записи в файл");
        fclose(file);
        return 2; // Возвращаем ошибку записи
    }

    fprintf(file, "%s, %d\n", data, 12);

    // Закрываем файл
    fclose(file);
    return 0; // Успешно
}

int main(void) {
    // signal(SIGINT, stopHandler);
    // signal(SIGTERM, stopHandler);
    root = json_object();
    SensorArray sensor_array;
    int r = parseSensors("../config/sensor.yaml", &sensor_array);
    if (r) {
        printf("Parsing error\n");
        return 1;
    }



    server = UA_Server_new();
    UA_ServerConfig_setDefault(UA_Server_getConfig(server));
    nsIndex = UA_Server_addNamespace(server, sensorNameSpace); // "http://sensors"
    printf("index of namespace %s\n", sensorNameSpace);
    printf("%hu\n", nsIndex);

    // printf("PublishingIntervalLimits min before: %f\n", server->config.publishingIntervalLimits.min);
    // printf("SamplingIntervalLimits min berofe: %f\n", server->config.samplingIntervalLimits.min);

    UA_ServerConfig *config = UA_Server_getConfig(server);

    config->publishingIntervalLimits.min = 10;
    config->publishingIntervalLimits.max = 10000;
    config->samplingIntervalLimits.min = 10;
    config->samplingIntervalLimits.max = 10000;

    // printf("PublishingIntervalLimits min after: %f\n", server->config.publishingIntervalLimits.min);
    // printf("SamplingIntervalLimits min after: %f\n", server->config.samplingIntervalLimits.min);

    sensor_map = map_create(sensor_array.max_size);
    for (int i = 0; i < sensor_array.size; ++i) {
        map_put(sensor_map, sensor_array.sensors[i].id, sensor_array.sensors[i]);
        addSensor(&sensor_array.sensors[i], nsIndex, server, UA_TYPES[UA_TYPES_STRING], sensorCallback, defaultInterval);
    }

    // Добавляем объект (папку) в адресное пространство
    UA_NodeId folderId;
    UA_ObjectAttributes oAttr = UA_ObjectAttributes_default;
    oAttr.displayName = UA_LOCALIZEDTEXT("en-US", "Methods");
    UA_Server_addObjectNode(server,
                           UA_NODEID_STRING(nsIndex, "Methods"),
                           UA_NODEID_NUMERIC(0, UA_NS0ID_OBJECTSFOLDER),
                           UA_NODEID_NUMERIC(0, UA_NS0ID_ORGANIZES),
                           UA_QUALIFIEDNAME(nsIndex, "Methods"),
                           UA_NODEID_NULL,
                           oAttr,
                           NULL,
                           &folderId);

    // входные параметры
    UA_Argument inputArgument;
    UA_Argument_init(&inputArgument);
    inputArgument.description = UA_LOCALIZEDTEXT("en-US", "Check-sensors-Input");
    inputArgument.name = UA_STRING("Input");
    inputArgument.dataType = UA_TYPES[UA_TYPES_STRING].typeId;
    inputArgument.valueRank = UA_VALUERANK_ONE_DIMENSION; // одномерный массив
    inputArgument.arrayDimensionsSize = 1;
    UA_UInt32 arrayDims[1] = {0}; // размер массива может быть переменным
    inputArgument.arrayDimensions = arrayDims;
    // Выходные параметры
    UA_Argument outputArgument;
    UA_Argument_init(&outputArgument);
    outputArgument.description = UA_LOCALIZEDTEXT("en-US", "Check-sensors-Output");
    outputArgument.name = UA_STRING("Result");
    outputArgument.dataType = UA_TYPES[UA_TYPES_STRING].typeId;
    outputArgument.valueRank = UA_VALUERANK_SCALAR;


    UA_MethodAttributes methodAttr = UA_MethodAttributes_default;
    methodAttr.description = UA_LOCALIZEDTEXT("en-US", "Check status of sensors");
    methodAttr.displayName = UA_LOCALIZEDTEXT("en-US", "Check-sensors");
    methodAttr.executable = true;
    methodAttr.userExecutable = true;

    UA_NodeId checkSensorsMethodNodeId = UA_NODEID_STRING(nsIndex, "CheckSensors");
    UA_Server_addMethodNode(server,
                           checkSensorsMethodNodeId,
                           folderId,
                           UA_NODEID_NUMERIC(0, UA_NS0ID_HASCOMPONENT),
                           UA_QUALIFIEDNAME(nsIndex, "CheckSensors"),
                           methodAttr,
                           &checkSensors,
                           1, &inputArgument, // один входной аргумент
                           1, &outputArgument, // один выходной аргумент
                           NULL, NULL);

    UA_MethodAttributes checkAllSensorsMethodAttr = UA_MethodAttributes_default;
    checkAllSensorsMethodAttr.description = UA_LOCALIZEDTEXT("en-US", "Check status of sensors");
    checkAllSensorsMethodAttr.displayName = UA_LOCALIZEDTEXT("en-US", "Check-All-sensors");
    checkAllSensorsMethodAttr.executable = true;
    checkAllSensorsMethodAttr.userExecutable = true;

    UA_NodeId checkAllSensorsMethodNodeId = UA_NODEID_STRING(nsIndex, "CheckAllSensors");
    UA_Server_addMethodNode(server,
                           checkAllSensorsMethodNodeId,
                           folderId,
                           UA_NODEID_NUMERIC(0, UA_NS0ID_HASCOMPONENT),
                           UA_QUALIFIEDNAME(nsIndex, "CheckAllSensors"),
                           checkAllSensorsMethodAttr,
                           &checkAllSensors,
                           0, NULL,
                           1, &outputArgument, // один выходной аргумент
                           NULL, NULL);



    // входные параметры
    UA_Argument createSensorInputArgument;
    UA_Argument_init(&inputArgument);
    createSensorInputArgument.description = UA_LOCALIZEDTEXT("en-US", "Create-Sensor-Input");
    createSensorInputArgument.name = UA_STRING("Input");
    createSensorInputArgument.dataType = UA_TYPES[UA_TYPES_STRING].typeId;
    createSensorInputArgument.valueRank = UA_VALUERANK_SCALAR; // строка
    createSensorInputArgument.arrayDimensionsSize = 0;
    createSensorInputArgument.arrayDimensions = NULL;

    UA_MethodAttributes createSensorMethodAttr = UA_MethodAttributes_default;
    checkAllSensorsMethodAttr.description = UA_LOCALIZEDTEXT("en-US", "Create sensor");
    checkAllSensorsMethodAttr.displayName = UA_LOCALIZEDTEXT("en-US", "Create-sensor");
    checkAllSensorsMethodAttr.executable = true;
    checkAllSensorsMethodAttr.userExecutable = true;

    UA_NodeId createSensorMethodNodeId = UA_NODEID_STRING(nsIndex, "CreateSensor");
    UA_Server_addMethodNode(server,
                           createSensorMethodNodeId,
                           folderId,
                           UA_NODEID_NUMERIC(0, UA_NS0ID_HASCOMPONENT),
                           UA_QUALIFIEDNAME(nsIndex, "CreateSensor"),
                           checkAllSensorsMethodAttr,
                           &createSensor,
                           1, &createSensorInputArgument, // один входной аргумент
                           0, NULL,
                           NULL, NULL);













    // old version
    // // Создаём NodeId для переменной
    // UA_NodeId currentNodeId = UA_NODEID_STRING(1, "data");
    //
    // // Добавляем переменную в адресное пространство
    // UA_VariableAttributes attr = UA_VariableAttributes_default;
    // attr.displayName = UA_LOCALIZEDTEXT("en-US", "Current Time");
    // attr.dataType = UA_TYPES[UA_TYPES_STRING].typeId;
    // attr.accessLevel = UA_ACCESSLEVELMASK_READ | UA_ACCESSLEVELMASK_WRITE;
    //
    // // UA_String initialValue = UA_STRING("Initial value");
    // // UA_Variant_setScalar(&attr.value, &initialValue, &UA_TYPES[UA_TYPES_STRING]);
    //
    // UA_Server_addVariableNode(server, currentNodeId,
    //                           UA_NODEID_NUMERIC(0, UA_NS0ID_OBJECTSFOLDER),
    //                           UA_NODEID_NUMERIC(0, UA_NS0ID_ORGANIZES),
    //                           UA_QUALIFIEDNAME(1, "Current Time"),
    //                           UA_NODEID_NUMERIC(0, UA_NS0ID_BASEDATAVARIABLETYPE),
    //                           attr, NULL, NULL);
    //
    // // Регистрируем callback для периодического обновления (например, каждую секунду)
    // UA_Server_addRepeatedCallback(server, updateVariableCallback, &currentNodeId, 200000.0, NULL);

    UA_Server_runUntilInterrupt(server);

    UA_Server_delete(server);
    json_decref(root);
    return 0;
}







// int main(void) {
//     signal(SIGINT, stopHandler);
//     signal(SIGTERM, stopHandler);
//
//     UA_Server *server = UA_Server_new();
//     UA_ServerConfig_setDefault(UA_Server_getConfig(server));
//
//     // --- PubSub Connection ---
//     UA_PubSubConnectionConfig connectionConfig;
//     memset(&connectionConfig, 0, sizeof(connectionConfig));
//     connectionConfig.name = UA_STRING("UDP Connection");
//     connectionConfig.transportProfileUri =
//         UA_STRING("http://opcfoundation.org/UA-Profile/Transport/pubsub-udp-uadp");
//     // connectionConfig.address = UA_NETWORKADDRESSURLDATATYPE_ALLOC("udp://239.0.0.1:4840");
//     connectionConfig.enabled = UA_TRUE;
//
//
//
//     UA_NetworkAddressUrlDataType networkAddressUrl;
//     memset(&networkAddressUrl, 0, sizeof(networkAddressUrl));
//     networkAddressUrl.networkInterface = UA_STRING("lo0"); // UA_STRING_NULL; // или UA_STRING("")
//     networkAddressUrl.url = UA_STRING("opc.tcp://localhost:4840"); // opc.udp://239.0.0.1:4840  224.0.0.22
//     // Теперь помещаем в Variant с правильным типом
//     UA_Variant_setScalar(&connectionConfig.address, &networkAddressUrl,
//                          &UA_TYPES[UA_TYPES_NETWORKADDRESSURLDATATYPE]);
//
//
//     // UA_NetworkAddressUrlDataType networkAddressUrl = {
//     //     UA_STRING_NULL,
//     //     UA_STRING("udp://224.0.0.22:4840")
//     // };
//     // UA_Variant_setScalar(&connectionConfig.address, &networkAddressUrl, &UA_TYPES[UA_TYPES_NETWORKADDRESSURLDATATYPE]);
//
//
//
//
//     // UA_String multicastAddress = UA_STRING("udp://239.0.0.1:4840");
//     // UA_Variant_setScalar(&connectionConfig.address, &multicastAddress, &UA_TYPES[UA_TYPES_STRING]);
//
//     UA_NodeId connectionId;
//     UA_StatusCode retval = UA_Server_addPubSubConnection(server, &connectionConfig, &connectionId);
//     if(retval != UA_STATUSCODE_GOOD) {
//         UA_LOG_ERROR(UA_Log_Stdout, UA_LOGCATEGORY_SERVER, "Failed to add PubSubConnection");
//         UA_Server_delete(server);
//         return (int)retval;
//     }
//
//     // --- PublishedDataSet ---
//     UA_PublishedDataSetConfig pdsConfig;
//     // UA_PublishedDataSetConfig_init(&pdsConfig);
//     memset(&pdsConfig, 0, sizeof(pdsConfig));
//     pdsConfig.name = UA_STRING("PublishedDataSet1");
//     UA_NodeId pdsId;
//     retval = UA_Server_addPublishedDataSet(server, &pdsConfig, &pdsId).addResult;
//     if(retval != UA_STATUSCODE_GOOD) {
//         UA_LOG_ERROR(UA_Log_Stdout, UA_LOGCATEGORY_SERVER, "Failed to add PublishedDataSet");
//         UA_Server_delete(server);
//         return (int)retval;
//     }
//
//     // --- Variable Node ---
//     UA_NodeId variableNodeId = UA_NODEID_STRING(1, "data");
//     UA_VariableAttributes attr = UA_VariableAttributes_default;
//     UA_String initialValue = UA_STRING("Initial");
//     UA_Variant_setScalar(&attr.value, &initialValue, &UA_TYPES[UA_TYPES_STRING]);
//     attr.displayName = UA_LOCALIZEDTEXT("en-US", "data");
//     attr.dataType = UA_TYPES[UA_TYPES_STRING].typeId;
//     attr.accessLevel = UA_ACCESSLEVELMASK_READ;
//
//     UA_Server_addVariableNode(server, variableNodeId,
//                               UA_NODEID_NUMERIC(0, UA_NS0ID_OBJECTSFOLDER),
//                               UA_NODEID_NUMERIC(0, UA_NS0ID_ORGANIZES),
//                               UA_QUALIFIEDNAME(1, "data"),
//                               UA_NODEID_NUMERIC(0, UA_NS0ID_BASEDATAVARIABLETYPE),
//                               attr, NULL, NULL);
//
//     // --- DataSetField ---
//     UA_DataSetFieldConfig dsfConfig;
//     // UA_DataSetFieldConfig_init(&dsfConfig);
//     memset(&dsfConfig, 0, sizeof(dsfConfig));
//     dsfConfig.dataSetFieldType = UA_PUBSUB_DATASETFIELD_VARIABLE;
//     dsfConfig.field.variable.publishParameters.publishedVariable = variableNodeId;
//     dsfConfig.field.variable.publishParameters.attributeId = UA_ATTRIBUTEID_VALUE;
//     UA_NodeId dsfId;
//     UA_DataSetFieldResult dsfResult = UA_Server_addDataSetField(server, pdsId, &dsfConfig, &dsfId);
//     if(dsfResult.result != UA_STATUSCODE_GOOD) {
//         UA_LOG_ERROR(UA_Log_Stdout, UA_LOGCATEGORY_SERVER, "Failed to add DataSetField");
//         UA_Server_delete(server);
//         return (int)dsfResult.result;
//     }
//
//
//
//     // --- WriterGroup ---
//     UA_WriterGroupConfig wgConfig;
//     // UA_WriterGroupConfig_init(&wgConfig);
//     memset(&wgConfig, 0, sizeof(wgConfig));
//     wgConfig.name = UA_STRING("WriterGroup1");
//     wgConfig.publishingInterval = 100.0; // 1 секунда
//     wgConfig.enabled = UA_TRUE;
//
//     UA_NodeId writerGroupId;
//     retval = UA_Server_addWriterGroup(server, connectionId, &wgConfig, &writerGroupId);
//     if(retval != UA_STATUSCODE_GOOD) {
//         UA_LOG_ERROR(UA_Log_Stdout, UA_LOGCATEGORY_SERVER, "Failed to add WriterGroup");
//         UA_Server_delete(server);
//         return (int)retval;
//     }
//
//     // --- DataSetWriter ---
//     UA_DataSetWriterConfig dswConfig;
//     // UA_DataSetWriterConfig_init(&dswConfig);
//     memset(&dswConfig, 0, sizeof(dswConfig));
//     dswConfig.name = UA_STRING("DataSetWriter1");
//     dswConfig.dataSetWriterId = 1;
//
//     UA_NodeId dataSetWriterId;
//     retval = UA_Server_addDataSetWriter(server, writerGroupId, pdsId, &dswConfig, &dataSetWriterId);
//     if(retval != UA_STATUSCODE_GOOD) {
//         UA_LOG_ERROR(UA_Log_Stdout, UA_LOGCATEGORY_SERVER, "Failed to add DataSetWriter");
//         UA_Server_delete(server);
//         return (int)retval;
//     }
//
//
//     UA_Variant value;
//     UA_String uaStr;
//     // --- Main loop ---
//     while(running) {
//         // Обновляем значение переменной
//         time_t now = time(NULL);
//         char timeStr[64];
//         strftime(timeStr, sizeof(timeStr), "%Y-%m-%d %H:%M:%S", localtime(&now));
//         uaStr = UA_STRING_ALLOC(timeStr);
//
//         printf("send_data %s\n", uaStr.data);
//
//         UA_Variant_setScalar(&value, &uaStr, &UA_TYPES[UA_TYPES_STRING]);
//         UA_StatusCode res = UA_Server_writeValue(server, variableNodeId, value);
//         printf("res %d\n", res == UA_STATUSCODE_GOOD);
//         UA_LOG_INFO(UA_Log_Stdout, UA_LOGCATEGORY_SERVER, "Сообщение отправлено: %s", uaStr.data);
//         // UA_Variant_clear(&value);
//         printf("send_data\n");
//         // UA_String_clear(&uaStr);
//
//
//         UA_Server_run_iterate(server, true);
//         sleep(0.1); // 1000 мс
//     }
//
//     UA_Server_delete(server);
//     return EXIT_SUCCESS;
// }