QT       += core gui widgets

CONFIG += c++11

DEFINES += QT_DEPRECATED_WARNINGS

SOURCES += \
    ecranpool.cpp \
    main.cpp

HEADERS += \
    ecranpool.h

FORMS += \
    ecranpool.ui

CONFIG(release, debug|release):DEFINES+=QT_NO_DEBUG_OUTPUT

DISTFILES += \
    images/background.jpg \
    images/logo.png \
    images/pool_table.png
