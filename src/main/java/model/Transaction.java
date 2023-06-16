package model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {

    @SerializedName("tran_id")
    private String tranId;

    @SerializedName("tran_direction")
    private String tranDirection;

    @SerializedName("tran_time")
    private LocalDateTime tranTime;

    @SerializedName("acc_nmbr")
    private String accNumber;

    @SerializedName("tran_amnt")
    private double tranAmount;

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getTranDirection() {
        return tranDirection;
    }

    public void setTranDirection(String tranDirection) {
        this.tranDirection = tranDirection;
    }

    public LocalDateTime getTranTime() {
        return tranTime;
    }

    public void setTranTime(LocalDateTime tranTime) {
        this.tranTime = tranTime;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public double getTranAmount() {
        return tranAmount;
    }

    public void setTranAmount(double tranAmount) {
        this.tranAmount = tranAmount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "tran_id='" + tranId + '\'' +
                ", tran_direction='" + tranDirection + '\'' +
                ", tran_time='" + tranTime + '\'' +
                ", acc_nmbr='" + accNumber + '\'' +
                ", tran_amnt=" + tranAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.tranAmount, tranAmount) == 0 && Objects.equals(tranId, that.tranId) && Objects.equals(tranDirection, that.tranDirection) && Objects.equals(tranTime, that.tranTime) && Objects.equals(accNumber, that.accNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tranId, tranDirection, tranTime, accNumber, tranAmount);
    }
}