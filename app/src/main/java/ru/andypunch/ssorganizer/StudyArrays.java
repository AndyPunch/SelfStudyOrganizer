package ru.andypunch.ssorganizer;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

public class StudyArrays {

    private static final String ZERO = "0";
    private static final String ONE = "1";
    private static final String TWO = "2";
    static final String THREE = "3";

    // use of context in non activity class
    private static Context context;

    StudyArrays(Context current) {
        context = current;
    }

    //collection with names of category of resources.
    static List<String> titleForExpLv;
    //collection with resources data.
    public static HashMap<String, List<String>> resourceData = new HashMap<String, List<String>>();

    void setExpLvTitleArray() {
        titleForExpLv = new ArrayList<>(asList(context.getResources()
                .getStringArray(R.array.wizard_Title_For_ExpLv)));
    }

    //избегаем NullPointerException
    public static void prepareExpListData() {
        resourceData.put(ZERO, new ArrayList<String>());
        resourceData.put(ONE, new ArrayList<String>());
        resourceData.put(TWO, new ArrayList<String>());
        resourceData.put(THREE, new ArrayList<String>());
    }

}
