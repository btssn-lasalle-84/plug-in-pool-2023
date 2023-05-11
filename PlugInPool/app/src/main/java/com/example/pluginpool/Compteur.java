package com.example.pluginpool;

import java.util.Timer;
import java.util.TimerTask;

public class Compteur extends Timer
{
    public static final int DUREE_TIR = 45;

    private int tempsRestant;
    private  final Manche manche;
    private TimerTask actualiser;

    public  Compteur(Manche manche)
    {
        tempsRestant = DUREE_TIR;
        this.manche = manche;

        actualiser = new TimerTask()
        {
            public void run()
            {
                if (tempsRestant > 0)
                {
                    tempsRestant -= 1;
                    manche.actualiserCompteur(tempsRestant);
                }
                else
                {
                    this.cancel();
                }
            }
        };
    }

    public void redemarrer()
    {
        this.cancel();
        this.purge();
        this.tempsRestant = DUREE_TIR;
        this.scheduleAtFixedRate(actualiser, 0, 1000);
    }
}
