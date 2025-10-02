/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

#include <memory>
#include <vector>
#include "../vulkan_wrapper/vulkan_wrapper.h"

class VkSession {
public:
    VkSession(const VkSession &) = delete;

    ~VkSession();

    VkSession &operator=(const VkSession &) = delete;

    std::vector<VkPhysicalDevice> vkEnumeratePhysicalDevices();

    static std::unique_ptr<VkSession>
    create(const VkInstanceCreateInfo *pCreateInfo, const VkAllocationCallbacks *pAllocator);

    VkPhysicalDeviceProperties vkGetPhysicalDeviceProperties(VkPhysicalDevice device);

private:
    VkSession(const VkInstanceCreateInfo *pCreateInfo, const VkAllocationCallbacks *pAllocator);

    VkInstance mInstance = nullptr;
};
