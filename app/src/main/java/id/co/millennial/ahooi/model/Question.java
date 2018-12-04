package id.co.millennial.ahooi.model;

import java.util.List;

/**
 * Created by root on 03/12/18.
 */

public class Question {

    private String question, point;
    private List<Answer> answerList;

    public Question(String question, String point, List<Answer> answerList) {
        this.question = question;
        this.point = point;
        this.answerList = answerList;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }
}
