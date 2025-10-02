/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "EglSession"

#include <stdexcept>
#include "EglSession.h"
#include "../logging.h"

EglSession::EglSession() {
    mDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (mDisplay == EGL_NO_DISPLAY) {
        throw std::runtime_error("Failed to get EGL display");
    }

    if (!eglInitialize(mDisplay, &major, &minor)) {
        throw std::runtime_error("Failed to initialize EGL");
    }
}

EglSession::~EglSession() {
    eglTerminate(mDisplay);
}

const char *EglSession::eglQueryString(EGLint name) {
    return ::eglQueryString(mDisplay, name);
}

std::optional<EGLConfig>
EglSession::eglChooseConfig(const EGLint *attribList) {
    EGLConfig config;
    EGLint numConfigs;
    if (!::eglChooseConfig(mDisplay, attribList, &config, 1, &numConfigs)) {
        return nullptr;
    }

    return config;
}

std::unique_ptr<EglContext>
EglSession::createEglContext(EGLConfig config, const EGLint *attribList) {
    try {
        return EglContext::create(mDisplay, config, attribList);
    } catch (std::runtime_error &error) {
        LOGE("Failed to create EGL context: %s", error.what());
        return {};
    }
}

bool EglSession::eglMakeCurrent(EGLSurface drawSurface, EGLSurface readSurface,
                                EGLContext context) {
    return ::eglMakeCurrent(mDisplay, drawSurface, readSurface, context);
}

const char *EglSession::glGetString(GLenum name) {
    return reinterpret_cast<const char *>(::glGetString(name));
}

std::unique_ptr<EglSession> EglSession::create() {
    try {
        return std::unique_ptr<EglSession>(new EglSession());
    } catch (...) {
        return nullptr;
    }
}
