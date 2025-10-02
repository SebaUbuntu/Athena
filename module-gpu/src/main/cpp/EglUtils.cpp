/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "EglUtils"

#include <android/log.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES/gl.h>
#include <jni.h>
#include "jni_utils.h"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C"
jobject Java_dev_sebaubuntu_athena_modules_gpu_utils_EglUtils_getEglInformation(
        JNIEnv *env, jobject thiz) {
    EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (display == EGL_NO_DISPLAY) {
        LOGE("Failed to get EGL display");
        return nullptr;
    }

    EGLint major, minor;
    if (!eglInitialize(display, &major, &minor)) {
        LOGE("Failed to initialize EGL");
        return nullptr;
    }

    LOGI("EGL version: %d.%d", major, minor);

#ifdef EGL_EXT
    // Get pointers to the required extension functions
    auto eglQueryDisplayAttribEXT = (PFNEGLQUERYDEVICEATTRIBEXTPROC) eglGetProcAddress("eglQueryDisplayAttribEXT");
    auto eglQueryDeviceStringEXT = (PFNEGLQUERYDEVICESTRINGEXTPROC) eglGetProcAddress("eglQueryDeviceStringEXT");

    if (!eglQueryDisplayAttribEXT) {
        LOGE("eglQueryDisplayAttribEXT not supported");
        return;
    }

    if (!eglQueryDeviceStringEXT) {
        LOGE("eglQueryDeviceStringEXT not supported");
        return;
    }

    EGLDeviceEXT device;
    if (!eglQueryDisplayAttribEXT(display, EGL_DEVICE_EXT, (EGLAttrib*)&device)) {
        LOGE("Failed to get EGL device from display");
        eglTerminate(display);
        return;
    }
#endif

    const char *vendor = eglQueryString(display, EGL_VENDOR);
    const char *version = eglQueryString(display, EGL_VERSION);
    const char *extensions = eglQueryString(display, EGL_EXTENSIONS);
    const char *client_api = eglQueryString(display, EGL_CLIENT_APIS);
    LOGI(
            "Device: Vendor: %s, Version: %s, Extensions: %s, Client API: %s",
            vendor ? vendor : "N/A",
            version ? version : "N/A",
            extensions ? extensions : "N/A",
            client_api ? client_api : "N/A");

    auto eglInformationClass = JNI_CHECK(env, env->FindClass(
            "dev/sebaubuntu/athena/modules/gpu/models/EglInformation"));

    auto eglInformationConstructorMethodId = JNI_CHECK(env, env->GetMethodID(
            eglInformationClass,
            "<init>",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"));

    auto eglInformation = JNI_CHECK(env, env->NewObject(
            eglInformationClass,
            eglInformationConstructorMethodId,
            vendor ? env->NewStringUTF(vendor) : nullptr,
            version ? env->NewStringUTF(version) : nullptr,
            extensions ? env->NewStringUTF(extensions) : nullptr,
            client_api ? env->NewStringUTF(client_api) : nullptr));

    eglTerminate(display);

    return eglInformation;
}
