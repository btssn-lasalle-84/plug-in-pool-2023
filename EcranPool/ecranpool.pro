QT += core gui widgets
QT += bluetooth

CONFIG += c++11

DEFINES += QT_DEPRECATED_WARNINGS

SOURCES += \
    communicationbluetooth.cpp \
    ecranpool.cpp \
    joueurs.cpp \
    main.cpp

HEADERS += \
    communicationbluetooth.h \
    ecranpool.h \
    joueurs.h

FORMS += \
    ecranpool.ui

RESOURCES += \
    ressources.qrc

CONFIG(release, debug|release):DEFINES+=QT_NO_DEBUG_OUTPUT
