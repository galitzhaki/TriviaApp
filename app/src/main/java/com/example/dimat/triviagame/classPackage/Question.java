package com.example.dimat.triviagame.classPackage;

public class Question {
    private int id;
    public String problem;
    public String answer1;
    public String answer2;
    public String answer3;
    public String answer4;
    public String imgurl;
    public int correct;

    public Question() {

    }

    public Question(int id, String problem, String answer1, String answer2, String answer3,
                   String answer4, String imgurl, int correct) {
        this.id = id;
        this.problem = problem;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.imgurl = imgurl;
        this.correct = correct;
    }
}
