/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

#pragma once

#include <GLES/gl.h>
#include <EGL/egl.h>
#include <memory>
#include <optional>
#include "EglContext.h"

class EglSession {
public:
    EglSession(const EglSession &) = delete;

    ~EglSession();

    EglSession &operator=(const EglSession &) = delete;

    const char *eglQueryString(EGLint name);

    std::optional<EGLConfig> eglChooseConfig(const EGLint *attribList);

    std::unique_ptr<EglContext>
    createEglContext(EGLConfig config, const EGLint *attribList);

    bool eglMakeCurrent(EGLSurface drawSurface, EGLSurface readSurface, EGLContext context);

    const char *glGetString(GLenum name);

    static std::unique_ptr<EglSession> create();

private:
    EglSession();

    EGLDisplay mDisplay = nullptr;
    EGLint major = 0, minor = 0;
};
