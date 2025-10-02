/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#include "EglContext.h"

#include <stdexcept>

EglContext::EglContext(EGLDisplay eglDisplay, EGLConfig config, const EGLint *attribList) {
    mEglDisplay = eglDisplay;
    mEglContext = ::eglCreateContext(eglDisplay, config, EGL_NO_CONTEXT, attribList);
    if (mEglContext == EGL_NO_CONTEXT) {
        throw std::runtime_error("Failed to create EGL context");
    }
}

EglContext::~EglContext() {
    ::eglDestroyContext(mEglDisplay, mEglContext);
}

EGLContext EglContext::getContext() {
    return mEglContext;
}

std::unique_ptr<EglContext>
EglContext::create(EGLDisplay eglDisplay, EGLConfig config, const EGLint *attribList) {
    try {
        return std::unique_ptr<EglContext>(new EglContext(eglDisplay, config, attribList));
    } catch (...) {
        return nullptr;
    }
}
