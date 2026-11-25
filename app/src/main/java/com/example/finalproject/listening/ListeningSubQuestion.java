package com.example.finalproject.listening;

import com.google.firebase.firestore.IgnoreExtraProperties;
import java.io.Serializable;

@IgnoreExtraProperties
public class ListeningSubQuestion implements Serializable {
    public String id = "", question = "", correctAnswer = "", explanation = "";
    public String optionA = "", optionB = "", optionC = "", optionD = "";
    public int questionNumber;

    public ListeningSubQuestion() {}

    public String option(int index) {
        String value = new String[]{optionA, optionB, optionC, optionD}[index];
        return value == null ? "" : value;
    }
}
