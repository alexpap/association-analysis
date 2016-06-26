package m112.di.uoa.gr;

/**
 * Created by ppetrou on 26/6/2016.
 */
public class RuleElement {
    private int[] head;
    private int[] body;
    private double rule_confidence;

    public RuleElement(int[] head, int[] body, double rule_confidence) {
        this.head = head;
        this.body = body;
        this.rule_confidence = rule_confidence;
    }

    public int[] getHead() {
        return head;
    }

    public void setHead(int[] head) {
        this.head = head;
    }

    public int[] getBody() {
        return body;
    }

    public void setBody(int[] body) {
        this.body = body;
    }

    public double getRule_confidence() {
        return rule_confidence;
    }

    public void setRule_confidence(double rule_confidence) {
        this.rule_confidence = rule_confidence;
    }
}
