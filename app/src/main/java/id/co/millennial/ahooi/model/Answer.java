package id.co.millennial.ahooi.model;

/**
 * Created by root on 03/12/18.
 */

public class Answer {

    private String answer;
    private boolean value;

    public Answer(String answer, boolean value) {
        this.answer = answer;
        this.value = value;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
