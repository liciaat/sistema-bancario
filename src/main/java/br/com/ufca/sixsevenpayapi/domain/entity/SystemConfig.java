package br.com.ufca.sixsevenpayapi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "system_configs")
public class SystemConfig extends BaseEntity {

    @Column(name = "savings_interest_rate", nullable = false,precision = 5, scale = 2)
    private BigDecimal savingsInterestRate;

    @Column(name = "last_yield_processed_month")
    private YearMonth lastYieldProcessedMonth;

    protected SystemConfig() {
        super();
    }

    public SystemConfig(BigDecimal savingsInterestRate) {
        super();
        this.savingsInterestRate = savingsInterestRate;
    }

    public BigDecimal getSavingsInterestRate() {
        return savingsInterestRate;
    }

    public void setSavingsInterestRate(BigDecimal savingsInterestRate) {
        this.savingsInterestRate = savingsInterestRate;
    }

    public YearMonth getLastYieldProcessedMonth() {
        return lastYieldProcessedMonth;
    }

    public void setLastYieldProcessedMonth(YearMonth lastYieldProcessedMonth) {
        this.lastYieldProcessedMonth = lastYieldProcessedMonth;
    }
}
