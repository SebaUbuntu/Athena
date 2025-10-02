/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "VkSession"

#include <stdexcept>
#include "VkSession.h"
#include "../logging.h"

VkSession::VkSession(const VkInstanceCreateInfo *pCreateInfo,
                     const VkAllocationCallbacks *pAllocator) {
    if (!IsVulkanSupported()) {
        throw std::runtime_error("Vulkan not supported");
    }

    if (vkCreateInstance(pCreateInfo, pAllocator, &mInstance) != VK_SUCCESS) {
        throw std::runtime_error("Failed to create Vulkan instance");
    }
}

VkSession::~VkSession() {
    vkDestroyInstance(mInstance, nullptr);
}

std::vector<VkPhysicalDevice> VkSession::vkEnumeratePhysicalDevices() {
    VkResult result;

    uint32_t deviceCount = 0;
    result = ::vkEnumeratePhysicalDevices(mInstance, &deviceCount, nullptr);
    if (result != VK_SUCCESS) {
        LOGE("Failed to enumerate Vulkan devices: %d", result);
        return {};
    }

    if (deviceCount <= 0) {
        LOGI("No Vulkan device found");
        return {};
    }

    std::vector<VkPhysicalDevice> devices(deviceCount);
    result = ::vkEnumeratePhysicalDevices(mInstance, &deviceCount, devices.data());
    if (result != VK_SUCCESS) {
        LOGE("Failed to enumerate Vulkan devices: %d", result);
        return {};
    }

    return devices;
}

VkPhysicalDeviceProperties VkSession::vkGetPhysicalDeviceProperties(VkPhysicalDevice device) {
    VkPhysicalDeviceProperties properties;
    ::vkGetPhysicalDeviceProperties(device, &properties);
    return properties;
}

std::unique_ptr<VkSession> VkSession::create(const VkInstanceCreateInfo *pCreateInfo,
                                             const VkAllocationCallbacks *pAllocator) {
    try {
        return std::unique_ptr<VkSession>(new VkSession(pCreateInfo, pAllocator));
    } catch (std::runtime_error &error) {
        LOGE("Failed to create Vulkan session: %s", error.what());
        return nullptr;
    }
}
