/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "EglUtils"

#include <jni.h>
#include "jni_utils.h"
#include "logging.h"
#include "egl/EglSession.h"

static const EGLint kConfigAttribs[] = {
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
        EGL_NONE
};

static const EGLint kContextAttribs[] = {
        EGL_CONTEXT_CLIENT_VERSION, 2,
        EGL_NONE
};

static void
maybeAddGlInformation(JNIEnv *env, EglSession &eglSession, jobject eglInformationBuilder) {
    jclass eglInformationBuilderClass = withJniCheck<jclass>(env, [=]() {
        return env->FindClass("dev/sebaubuntu/athena/modules/gpu/models/EglInformation$Builder");
    });

    auto eglInformationAddGlInformationMethodId = withJniCheck<jmethodID>(env, [=]() {
        return env->GetMethodID(
                eglInformationBuilderClass,
                "addGlInformation",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    });

    // Choose a configuration
    auto eglConfig = eglSession.eglChooseConfig(kConfigAttribs);
    if (!eglConfig) {
        LOGE("Failed to choose EGL config");
        return;
    }

    // Create a context
    auto eglContext = eglSession.createEglContext(eglConfig.value(), kContextAttribs);
    if (!eglContext) {
        LOGE("Failed to create EGL context");
        return;
    }

    // Make the context current
    if (!eglSession.eglMakeCurrent(EGL_NO_SURFACE, EGL_NO_SURFACE, eglContext->getContext())) {
        LOGE("Failed to make EGL context current");
        return;
    }

    auto glVendor = eglSession.glGetString(GL_VENDOR);
    auto glRenderer = eglSession.glGetString(GL_RENDERER);
    auto glVersion = eglSession.glGetString(GL_VERSION);
    auto glExtensions = eglSession.glGetString(GL_EXTENSIONS);

    withJniCheck(env, [=]() {
        return env->CallVoidMethod(
                eglInformationBuilder, eglInformationAddGlInformationMethodId,
                glVendor ? env->NewStringUTF(glVendor) : nullptr,
                glRenderer ? env->NewStringUTF(glRenderer) : nullptr,
                glVersion ? env->NewStringUTF(glVersion) : nullptr,
                glExtensions ? env->NewStringUTF(glExtensions) : nullptr);
    });

    // Cleanup the current context
    eglSession.eglMakeCurrent(EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
}

extern "C"
jobject Java_dev_sebaubuntu_athena_modules_gpu_utils_EglUtils_getEglInformation(
        JNIEnv *env, jobject thiz) {
    jclass eglInformationBuilderClass = withJniCheck<jclass>(env, [=]() {
        return env->FindClass("dev/sebaubuntu/athena/modules/gpu/models/EglInformation$Builder");
    });

    auto eglInformationBuilderConstructorMethodId = withJniCheck<jmethodID>(env, [=]() {
        return env->GetMethodID(
                eglInformationBuilderClass,
                "<init>",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    });

    auto eglInformationBuilderBuildMethodId = withJniCheck<jmethodID>(env, [=]() {
        return env->GetMethodID(
                eglInformationBuilderClass,
                "build",
                "()Ldev/sebaubuntu/athena/modules/gpu/models/EglInformation;");
    });

    auto eglSession = EglSession::create();
    if (!eglSession) {
        LOGE("Failed to create EGL session");
        return nullptr;
    }

    const char *eglVendor = eglSession->eglQueryString(EGL_VENDOR);
    const char *eglVersion = eglSession->eglQueryString(EGL_VERSION);
    const char *eglExtensions = eglSession->eglQueryString(EGL_EXTENSIONS);
    const char *eglClientApi = eglSession->eglQueryString(EGL_CLIENT_APIS);

    auto eglInformationBuild = withJniCheck<jobject>(env, [=]() {
        return env->NewObject(
                eglInformationBuilderClass,
                eglInformationBuilderConstructorMethodId,
                eglVendor ? env->NewStringUTF(eglVendor) : nullptr,
                eglVersion ? env->NewStringUTF(eglVersion) : nullptr,
                eglExtensions ? env->NewStringUTF(eglExtensions) : nullptr,
                eglClientApi ? env->NewStringUTF(eglClientApi) : nullptr);
    });

    maybeAddGlInformation(env, *eglSession, eglInformationBuild);

    auto eglInformation = withJniCheck<jobject>(env, [=]() {
        return env->CallObjectMethod(eglInformationBuild, eglInformationBuilderBuildMethodId);
    });

    return eglInformation;
}
