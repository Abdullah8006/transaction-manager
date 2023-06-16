package model;

import java.util.Objects;

public class EventLog {

    private String account_nmbr;
    private String rule_id;
    private Transaction triggering_transaction;

    public EventLog(String account_nmbr, String rule_id, Transaction triggering_transaction) {
        this.account_nmbr = account_nmbr;
        this.rule_id = rule_id;
        this.triggering_transaction = triggering_transaction;
    }

    @Override
    public String toString() {
        return "{" + "account_nmbr='" + account_nmbr + '\'' + ", rule_id='" + rule_id + '\'' + ", triggering_transaction=" + triggering_transaction + '}';
    }
}