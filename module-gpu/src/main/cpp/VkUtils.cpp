/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "VkUtils"

#include <vector>
#include <jni.h>
#include "vulkan/VkSession.h"
#include "jni_utils.h"
#include "logging.h"

static const std::vector<const char *> kRequiredExtensions = {
        "VK_KHR_surface",
        "VK_KHR_android_surface"
};

extern "C"
JNIEXPORT jobject JNICALL
Java_dev_sebaubuntu_athena_modules_gpu_utils_VkUtils_getVkInfo(
        JNIEnv *env, jobject thiz) {
    jclass vkPhysicalDevicesClass = withJniCheck<jclass>(env, [=]() {
        return env->FindClass("dev/sebaubuntu/athena/modules/gpu/utils/VkUtils$VkPhysicalDevices");
    });
    auto vkPhysicalDevicesConstructorMethodId = withJniCheck<jmethodID>(env, [=]() {
        return env->GetMethodID(vkPhysicalDevicesClass, "<init>", "()V");
    });
    auto addDeviceMethodId = withJniCheck<jmethodID>(env, [=]() {
        return env->GetMethodID(
                vkPhysicalDevicesClass,
                "addDevice",
                "(JJJJJLjava/lang/String;)Z");
    });

    VkApplicationInfo appInfo{
            .sType = VK_STRUCTURE_TYPE_APPLICATION_INFO,
            .pApplicationName = "Athena",
            .applicationVersion = VK_MAKE_VERSION(1, 0, 0),
            .pEngineName = "No Engine",
            .engineVersion = VK_MAKE_VERSION(1, 0, 0),
            .apiVersion = VK_API_VERSION_1_0,
    };

    VkInstanceCreateInfo createInfo{
            .sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO,
            .pApplicationInfo = &appInfo,
            .enabledLayerCount = 0,
            .enabledExtensionCount = (uint32_t) kRequiredExtensions.size(),
            .ppEnabledExtensionNames = kRequiredExtensions.data(),
    };

    auto vkSession = VkSession::create(&createInfo, nullptr);
    if (!vkSession) {
        LOGE("Failed to create Vulkan session");
        return nullptr;
    }

    auto physicalDevices = vkSession->vkEnumeratePhysicalDevices();

    auto vkPhysicalDevices = env->NewObject(vkPhysicalDevicesClass,
                                            vkPhysicalDevicesConstructorMethodId);

    for (const auto &device: physicalDevices) {
        auto deviceProperties = vkSession->vkGetPhysicalDeviceProperties(device);

        withJniCheck<bool>(env, [=]() {
            return env->CallBooleanMethod(
                    vkPhysicalDevices, addDeviceMethodId,
                    static_cast<jlong>(deviceProperties.apiVersion),
                    static_cast<jlong>(deviceProperties.driverVersion),
                    static_cast<jlong>(deviceProperties.vendorID),
                    static_cast<jlong>(deviceProperties.deviceID),
                    static_cast<jlong>(deviceProperties.deviceType),
                    env->NewStringUTF(deviceProperties.deviceName));
        });
    }

    return vkPhysicalDevices;
}
