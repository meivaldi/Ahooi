package id.co.millennial.ahooi.model;

/**
 * Created by root on 03/12/18.
 */

public class Question {

    private String question, point;
    private Answer[] answer;

    public Question(String question, String point, Answer[] answer) {
        this.question = question;
        this.point = point;
        this.answer = answer;
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

    public Answer[] getAnswer() {
        return answer;
    }

    public Answer getAnswer(int i){
        return answer[i];
    }

    public void setAnswer(Answer[] answer) {
        this.answer = answer;
    }
}
