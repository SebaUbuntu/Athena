#
# SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
# SPDX-License-Identifier: Apache-2.0
#

add_subdirectory(cpuinfo)

add_library(${CMAKE_PROJECT_NAME}_cpu SHARED
        CpuInfoUtils.cpp
        CpuJni.cpp
        jni_utils.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}_cpu
        android
        log
        cpuinfo)
