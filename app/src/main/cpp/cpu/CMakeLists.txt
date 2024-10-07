#
# SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
# SPDX-License-Identifier: Apache-2.0
#

add_subdirectory(cpuinfo)

add_library(${CMAKE_PROJECT_NAME}_cpu SHARED
        CpuInfoUtils.cpp
        CpuJni.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}_cpu
        ${CMAKE_PROJECT_NAME}_jni
        android
        log
        cpuinfo)
