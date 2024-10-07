#
# SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
# SPDX-License-Identifier: Apache-2.0
#

add_library(${CMAKE_PROJECT_NAME}_jni SHARED
        jni_utils.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}_jni
        android
        log)
