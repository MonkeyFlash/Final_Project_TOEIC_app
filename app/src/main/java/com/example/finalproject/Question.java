package com.example.finalproject;

public class Question {

    private String question;
    private String type;
    private int part;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String correctAnswer;
    private String explanation;

    private String audioUrl;
    private String imageUrl;

    // Constructor rỗng
    public Question() {
    }

    // Constructor đầy đủ
    public Question(String question, String type, int part,
                    String optionA, String optionB,
                    String optionC, String optionD,
                    String correctAnswer, String explanation,
                    String audioUrl, String imageUrl) {

        this.question = question;
        this.type = type;
        this.part = part;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.audioUrl = audioUrl;
        this.imageUrl = imageUrl;
    }

    // Getter
    public String getQuestion() { return question; }
    public String getType() { return type; }
    public int getPart() { return part; }

    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }

    public String getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }

    public String getAudioUrl() { return audioUrl; }
    public String getImageUrl() { return imageUrl; }
}