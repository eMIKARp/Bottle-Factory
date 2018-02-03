
package skrzynkabutelek;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        
        Skrzynka skrzynka = new Skrzynka();
        MaszynaProdukujacaButelki maszynaProdukujaca = new MaszynaProdukujacaButelki(skrzynka);
        MaszynaZmieniajacaSkrzynki maszynaZamieniajaca = new MaszynaZmieniajacaSkrzynki(skrzynka);
        
        Thread produkcjaButelek = new Thread(maszynaProdukujaca, "Produkcja Butelek");
        Thread zamianaSkrzynek = new Thread(maszynaZamieniajaca, "Zamiana Skrzynek");
        
        produkcjaButelek.start();
        zamianaSkrzynek.start();
    }
    
}

class MaszynaProdukujacaButelki implements Runnable
{
    private Skrzynka skrzynka;
    private int i = 0;
    
    MaszynaProdukujacaButelki (Skrzynka skrzynka)
    {
        this.skrzynka = skrzynka;
    }
    
    @Override
    public void run() {
        synchronized (skrzynka)
        {
            System.out.println(Thread.currentThread().getName() + ": Zaczynam produkować butelki.");
            while (true)
            {
                while (skrzynka.czyPelna())
                {
                    try
                    {
                    System.out.println(Thread.currentThread().getName() + ": Skrzykna pełna. Proszę wymienić skrzynkę!");
                    skrzynka.wait();
                    System.out.println(Thread.currentThread().getName() + ": Skrzynka wymieniona. Wznawiam produkcje.");
                    }
                    catch (InterruptedException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                
            System.out.println(Thread.currentThread().getName() + ": Wyprodukowano " + (++i) + " kolejną butelkę");
            skrzynka.dodajButelke(new Butelka());
            skrzynka.notifyAll();
            }
        }
    }
}

class MaszynaZmieniajacaSkrzynki implements Runnable
{
    private Skrzynka skrzynka;
    
    MaszynaZmieniajacaSkrzynki (Skrzynka skrzynka)
    {
        this.skrzynka = skrzynka;
    }
    
    @Override
    public void run() {
        synchronized (skrzynka)
        {
            System.out.println(Thread.currentThread().getName() + ": Przygotowuje się do wymiany skrzynek.");
            while (true)
            {
                while (!skrzynka.czyPelna())
                {
                    try
                    {
                    System.out.println(Thread.currentThread().getName() + ": Zamiana skrzynek wstrzymana.");    
                    skrzynka.wait();
                    System.out.println(Thread.currentThread().getName() + ": Powróciłem do zamiany skrzynek.");
                    }
                    catch (InterruptedException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                skrzynka.pobierzIloscButelek();
                skrzynka.zamianaSkrzynek();
                skrzynka.pobierzIloscButelek();
                skrzynka.notifyAll();
            }
        }
    }
}

class Skrzynka
{
    private final int POJEMNOSC = 10;
    private ArrayList listaButelek = new ArrayList(POJEMNOSC);
    
    public synchronized boolean czyPelna()
    {
        if (listaButelek.size() == POJEMNOSC) return true;
        else return false;
    }
    
    public synchronized int pobierzIloscButelek()
    {
        System.out.println(Thread.currentThread().getName() + ": Aktualnie w skrzyce jest: " + this.listaButelek.size());
        return this.listaButelek.size();
    }
    
    public synchronized void dodajButelke(Butelka butelka)
    {
        listaButelek.add(butelka);
    }
    public synchronized void zamianaSkrzynek()
    {
        System.out.println(Thread.currentThread().getName() + ": Zamieniam skrzynkę na pustą. ");
        listaButelek.clear();
    }
    
}
class Butelka
{
    
}
