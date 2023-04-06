#include "ecranpool.h"
#include <QApplication>

/**
 * @file main.cpp
 * @brief Programme principal
 * @details Crée et affiche la fenêtre principale de l'application EcranPool
 * @author Benjamin GAUME
 * @version 1.0
 *
 * @param argc
 * @param argv[]
 * @return int
 */
int main(int argc, char* argv[])
{
    QApplication a(argc, argv);
    EcranPool    ecranPool;

    ecranPool.showFullScreen();

    return a.exec();
}
