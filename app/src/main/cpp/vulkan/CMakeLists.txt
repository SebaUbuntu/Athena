#
# SPDX-FileCopyrightText: 2024 Sebastiano Barezzi
# SPDX-License-Identifier: Apache-2.0
#

add_library(${CMAKE_PROJECT_NAME}_vulkan SHARED
        vulkan_wrapper/vulkan_wrapper.cpp
        VkUtils.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}_vulkan
        ${CMAKE_PROJECT_NAME}_jni
        android
        log)
