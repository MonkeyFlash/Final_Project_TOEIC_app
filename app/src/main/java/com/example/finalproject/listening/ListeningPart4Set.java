package com.example.finalproject.listening;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class ListeningPart4Set {
    public String title = "", audioUrl = "", audioText = "", imageUrl = "", status = "";
    public boolean demoOnly;
    public List<ListeningSubQuestion> questions = new ArrayList<>();
    public ListeningPart4Set() {}
}
