package com.example.finalproject.listening;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ListeningPart2Question extends ListeningSubQuestion {
    public String title = "", audioUrl = "", audioText = "", imageUrl = "", status = "";
    public boolean demoOnly;
    public ListeningPart2Question() {}
}
