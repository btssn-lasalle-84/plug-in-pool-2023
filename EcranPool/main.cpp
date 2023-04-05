#include "ecranpool.h"
#include <QApplication>

int main(int argc, char* argv[])
{
    QApplication a(argc, argv);
    EcranPool    ecranPool;

    ecranPool.show();

    return a.exec();
}
