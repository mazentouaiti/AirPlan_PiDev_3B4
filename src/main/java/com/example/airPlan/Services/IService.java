package com.example.airPlan.Services;

import java.util.List;

public interface IService <T> {
    void add (T t );

    void update (T t) ;

    void delete (int id );

    List<T> display ();
}
