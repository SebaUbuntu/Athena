/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "VkUtils"

#include <android/log.h>
#include <cassert>
#include <vector>
#include <jni.h>
#include "../jni/jni_utils.h"

#include "vulkan_wrapper/vulkan_wrapper.h"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define VK_CHECK(x)                                 \
    do {                                            \
        VkResult err = x;                           \
        if (err) {                                  \
            LOGE("Detected Vulkan error: %d", err); \
            abort();                                \
        }                                           \
    } while (0)

static const std::vector<const char *> kRequiredExtensions = {
        "VK_KHR_surface",
        "VK_KHR_android_surface"
};

extern "C"
JNIEXPORT void JNICALL
Java_dev_sebaubuntu_athena_utils_VkUtils_getVkInfo(
        JNIEnv *env, jobject thiz, jobject vkInfoList) {
    if (!IsVulkanSupported()) {
        LOGE("Vulkan not supported");
        return;
    }

    VkInstance instance;

    VkApplicationInfo appInfo{};
    appInfo.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
    appInfo.pApplicationName = "Athena";
    appInfo.applicationVersion = VK_MAKE_VERSION(1, 0, 0);
    appInfo.pEngineName = "No Engine";
    appInfo.engineVersion = VK_MAKE_VERSION(1, 0, 0);
    appInfo.apiVersion = VK_API_VERSION_1_0;

    VkInstanceCreateInfo createInfo{};
    createInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    createInfo.pApplicationInfo = &appInfo;
    createInfo.enabledExtensionCount = (uint32_t) kRequiredExtensions.size();
    createInfo.ppEnabledExtensionNames = kRequiredExtensions.data();
    createInfo.pApplicationInfo = &appInfo;

    createInfo.enabledLayerCount = 0;
    createInfo.pNext = nullptr;

    VK_CHECK(vkCreateInstance(&createInfo, nullptr, &instance));

    uint32_t deviceCount = 0;
    VK_CHECK(vkEnumeratePhysicalDevices(instance, &deviceCount, nullptr));

    if (deviceCount <= 0) {
        LOGI("No Vulkan device found");
        return;
    }

    std::vector<VkPhysicalDevice> devices(deviceCount);
    VK_CHECK(vkEnumeratePhysicalDevices(instance, &deviceCount, devices.data()));

    auto vkPhysicalDevicesClass = env->GetObjectClass(vkInfoList);
    JNI_CHECK(env);
    auto addDeviceMethodId = env->GetMethodID(
            vkPhysicalDevicesClass,
            "addDevice",
            "(JJJJJLjava/lang/String;)Z");
    JNI_CHECK(env);

    VkPhysicalDeviceProperties deviceProperties;
    for (const auto &device: devices) {
        vkGetPhysicalDeviceProperties(device, &deviceProperties);
        env->CallBooleanMethod(
                vkInfoList, addDeviceMethodId,
                static_cast<jlong>(deviceProperties.apiVersion),
                static_cast<jlong>(deviceProperties.driverVersion),
                static_cast<jlong>(deviceProperties.vendorID),
                static_cast<jlong>(deviceProperties.deviceID),
                static_cast<jlong>(deviceProperties.deviceType),
                env->NewStringUTF(deviceProperties.deviceName));
        JNI_CHECK(env);
    }

    vkDestroyInstance(instance, nullptr);
}
