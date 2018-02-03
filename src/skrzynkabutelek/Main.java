
package skrzynkabutelek;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import static sun.misc.GThreadHelper.lock;

public class Main {

    public static void main(String[] args) {
        
        Skrzynka skrzynka = new Skrzynka();
        MaszynaProdukujacaButelki maszynaProdukujaca = new MaszynaProdukujacaButelki(skrzynka, lock, oczekiwanie);
        MaszynaZmieniajacaSkrzynki maszynaZamieniajaca = new MaszynaZmieniajacaSkrzynki(skrzynka, lock, oczekiwanie);
        
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
    private Lock lock;
    private Condition oczekiwanie;
    
    MaszynaProdukujacaButelki (Skrzynka skrzynka, Lock lock, Condition oczekiwanie)
    {
        this.skrzynka = skrzynka;
        this.lock = lock;
        this.oczekiwanie = oczekiwanie;
    }
    
    @Override
    public void run() {
        lock.lock();
        try
        {
            System.out.println(Thread.currentThread().getName() + ": Zaczynam produkować butelki.");
            while (true)
            {
                while (skrzynka.czyPelna())
                {
                    try
                    {
                    System.out.println(Thread.currentThread().getName() + ": Skrzykna pełna. Proszę wymienić skrzynkę!");
                    oczekiwanie.await();
                    System.out.println(Thread.currentThread().getName() + ": Skrzynka wymieniona. Wznawiam produkcje.");
                    }
                    catch (InterruptedException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                
            System.out.println(Thread.currentThread().getName() + ": Wyprodukowano " + (++i) + " kolejną butelkę");
            skrzynka.dodajButelke(new Butelka());
            oczekiwanie.signalAll();
            }
        }
        finally
        {
            lock.unlock();
        }
    }
}

class MaszynaZmieniajacaSkrzynki implements Runnable
{
    private Skrzynka skrzynka;
    private Lock lock;
    private Condition oczekiwanie;
    
    MaszynaZmieniajacaSkrzynki (Skrzynka skrzynka, Lock lock, Condition oczekiwanie)
    {
        this.skrzynka = skrzynka;
        this.lock = lock;
        this.oczekiwanie = oczekiwanie;
    }
    
    @Override
    public void run() {
        lock.lock();
        try 
        {
            System.out.println(Thread.currentThread().getName() + ": Przygotowuje się do wymiany skrzynek.");
            while (true)
            {
                while (!skrzynka.czyPelna())
                {
                    try
                    {
                    System.out.println(Thread.currentThread().getName() + ": Zamiana skrzynek wstrzymana.");    
                    oczekiwanie.await();
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
                oczekiwanie.signalAll();
            }
        }
        finally
        {
            lock.unlock();
        }
    }
}

class Skrzynka
{
    private final int POJEMNOSC = 10;
    private ArrayList listaButelek = new ArrayList(POJEMNOSC);
    Lock lock = new ReentrantLock();
    Condition oczekiwanie = lock.newCondition();
    
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
