/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

#include <memory>
#include <EGL/egl.h>

class EglContext {
public:
    EglContext(const EglContext &) = delete;

    ~EglContext();

    EglContext &operator=(const EglContext &) = delete;

    EGLContext getContext();

    static std::unique_ptr<EglContext>
    create(EGLDisplay eglDisplay, EGLConfig config, const EGLint *attribList);

private:
    EglContext(EGLDisplay eglDisplay, EGLConfig config, const EGLint *attribList);

    EGLDisplay mEglDisplay;
    EGLContext mEglContext;
};
