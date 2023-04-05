#include "ecranpool.h"
#include "ui_ecranpool.h"

EcranPool::EcranPool(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::EcranPool)
{
    ui->setupUi(this);
}

EcranPool::~EcranPool()
{
    delete ui;
}

