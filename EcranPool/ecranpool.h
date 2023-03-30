#ifndef ECRANPOOL_H
#define ECRANPOOL_H

#include <QWidget>

QT_BEGIN_NAMESPACE
namespace Ui { class EcranPool; }
QT_END_NAMESPACE

class EcranPool : public QWidget
{
    Q_OBJECT

public:
    EcranPool(QWidget *parent = nullptr);
    ~EcranPool();

private:
    Ui::EcranPool *ui;
};
#endif // ECRANPOOL_H
