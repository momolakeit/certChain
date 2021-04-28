package com.momo.certChain.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListUtils {
    public static List initList(List liste) {
        if (Objects.isNull(liste)) {
            return new ArrayList();
        }
        return liste;
    }
    public static <T> List<T> ajouterObjectAListe(T obj, List<T> list){
        list =initList(list);
        list.add(obj);
        return list;
    }
}
