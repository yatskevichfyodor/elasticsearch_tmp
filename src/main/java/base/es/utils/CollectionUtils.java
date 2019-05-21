package base.es.utils;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {

    public static <T> List<T> intersection(List<List<T>> lists) {
        List<T> intersection = new ArrayList<T>();
        List<T> firstList = lists.get(0);
        List<List<T>> secondaryLists = new ArrayList<>();
        for (int i = 1; i < lists.size(); i++){
            secondaryLists.add(lists.get(i));
        }

        for (T element : firstList) {
            if (secondaryLists.stream().allMatch(it -> it.contains(element))) {
                intersection.add(element);
            }
        }

        return intersection;
    }

}
