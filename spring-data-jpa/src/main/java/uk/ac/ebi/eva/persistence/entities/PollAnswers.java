package uk.ac.ebi.eva.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by jorizci on 29/09/16.
 */
@Entity
@Table(name ="POLL_ANSWERS")
public class PollAnswers {

    @Id
    private Long id;

    @Column(nullable = false, length = 256)
    private String pollName;

    @Column(nullable = false, length = 256)
    private String answerText;

    private boolean answerBoolean;

    private long answerLong;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPollName() {
        return pollName;
    }

    public void setPollName(String pollName) {
        this.pollName = pollName;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isAnswerBoolean() {
        return answerBoolean;
    }

    public void setAnswerBoolean(boolean answerBoolean) {
        this.answerBoolean = answerBoolean;
    }

    public long getAnswerLong() {
        return answerLong;
    }

    public void setAnswerLong(long answerLong) {
        this.answerLong = answerLong;
    }
}
