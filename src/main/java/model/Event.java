package model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event {

    private String ruleId;
    private Transaction transaction;

    public Event(String ruleId, Transaction transaction) {
        this.ruleId = ruleId;
        this.transaction = transaction;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(ruleId, event.ruleId) && Objects.equals(transaction, event.transaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, transaction);
    }

    @Override
    public String toString() {
        return "Event{" +
                "ruleId='" + ruleId + '\'' +
                ", transaction=" + transaction +
                '}';
    }
}