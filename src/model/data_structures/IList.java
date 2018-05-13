package model.data_structures;

import java.util.Iterator;

public interface IList<T extends Comparable<T>> extends Iterable<T>{

    T add(T elem);

    int size();

    T get(T elem);
    
    T get(int i);

}

