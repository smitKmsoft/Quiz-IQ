package com.sportbvet.game.quiz.Model;

import java.io.Serializable;

public class Data implements Serializable {

    public String que;
    public Option option;
    public String Ans;

    public Data() {
    }

    public Data(String que, Option option, String ans) {
        this.que = que;
        this.option = option;
        this.Ans = ans;
    }
}
