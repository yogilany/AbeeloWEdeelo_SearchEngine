import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class inc {

    public int numberoflinks;
    public inc(int number)
    {
        numberoflinks=number;
    }
    public synchronized int addtonumber()
    {
        numberoflinks++;
        return numberoflinks;
    }

    public int getnumber()
    {return numberoflinks;}



}
