cmake_minimum_required(VERSION 3.28)
project(simulation_service C)

set(CMAKE_C_STANDARD 23)




#add_library(open62541 gen_lib/open62541.c)
## Подключаем заголовки библиотеки
#target_include_directories(open62541 PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/gen_lib)
#
#add_executable(simulation_service src/main.c gen_lib/open62541.c gen_lib/open62541.h)
#
#target_link_libraries(simulation_service PRIVATE open62541)



##from AI
#add_library(open62541 gen_lib/open62541.c)
#target_include_directories(open62541 PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/gen_lib)
#
#add_executable(simulation_service src/main.c)
#target_include_directories(simulation_service PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/gen_lib)
#target_link_libraries(simulation_service PRIVATE open62541)
#message("CMAKE_OSX_SYSROOT = ${CMAKE_OSX_SYSROOT}")







add_executable(${PROJECT_NAME} src/main.c
        lib/sensor.h
        lib/sensor.c)


set(UA_BUILD_EXAMPLES OFF CACHE BOOL "No examples")
set(UA_BUILD_UNIT_TESTS OFF CACHE BOOL "No tests")
set(UA_ENABLE_SUBSCRIPTIONS ON CACHE BOOL "Enable subscriptions")

# Установка директории с open62541
add_subdirectory(lib/open62541-1.4.10)

# Добавление директории с заголовками open62541
target_include_directories(${PROJECT_NAME} PRIVATE ${CMAKE_SOURCE_DIR}/lib/open62541-1.4.10/include)
target_include_directories(${PROJECT_NAME} PRIVATE ${CMAKE_SOURCE_DIR}/lib/open62541-1.4.10/src)

message(${CMAKE_SOURCE_DIR})

# Связывание с библиотекой open62541
target_link_libraries(${PROJECT_NAME} PRIVATE open62541) # because in open62541-1.4.10 CMake.txt file project cals open62541



# add 'libcyaml' from working with yaml file
# get path throw brew brew --prefix libcyaml
find_path(CYAML_INCLUDE_DIR cyaml.h PATHS /usr/local/opt/libcyaml/include/cyaml)
find_library(CYAML_LIBRARY cyaml PATHS /usr/local/opt/libcyaml/lib)

target_include_directories(${PROJECT_NAME} PRIVATE ${CYAML_INCLUDE_DIR})
target_link_libraries(${PROJECT_NAME} PRIVATE ${CYAML_LIBRARY})

#include_directories(${YAML_INCLUDE_DIR})
#target_link_libraries(${PROJECT_NAME} ${YAML_LIBRARY})


# add 'libyaml' library that use 'libcyaml'
find_path(YAML_INCLUDE_DIR yaml.h PATHS /usr/local/opt/libcyaml/include/cyaml)
find_library(YAML_LIBRARY yaml PATHS /usr/local/opt/libcyaml/lib)

target_include_directories(${PROJECT_NAME} PRIVATE ${YAML_INCLUDE_DIR})
target_link_libraries(${PROJECT_NAME} PRIVATE ${YAML_LIBRARY})



# /usr/local/opt/jansson
# add 'jansson' from working with json
# get path throw brew brew --prefix jansson
find_path(JANSSON_INCLUDE_DIR jansson.h PATHS /usr/local/opt/jansson/include)
find_library(JANSSON_LIBRARY jansson PATHS /usr/local/opt/jansson/lib)

if (NOT JANSSON_INCLUDE_DIR)
    message(FATAL_ERROR "Jansson include directory not found")
endif()

if (NOT JANSSON_LIBRARY)
    message(FATAL_ERROR "Jansson library not found")
endif()

target_include_directories(${PROJECT_NAME} PRIVATE ${JANSSON_INCLUDE_DIR}) # ai
target_link_libraries(${PROJECT_NAME} PRIVATE ${JANSSON_LIBRARY})

#include_directories(${JANSSON_INCLUDE_DIR})
#target_link_libraries(${PROJECT_NAME} ${JANSSON_LIBRARY})