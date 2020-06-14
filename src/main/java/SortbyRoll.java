import Entity.TTTRrecord;

import java.util.Comparator;

class Sortbyroll implements Comparator<TTTRrecord>
{
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(TTTRrecord a, TTTRrecord b)
    {
        return Double.compare(a.getTrueTime(), b.getTrueTime());
    }
} 
  