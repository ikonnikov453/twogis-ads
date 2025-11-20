package com.example.twogisads.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * сущность размещение (placements)
 */
public class Placement {
    private Integer placementId;
    private Integer clientId;
    private Integer packageId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyPayment;

    public Placement() {}

    public Placement(Integer placementId, Integer clientId, Integer packageId, LocalDate startDate, LocalDate endDate, BigDecimal monthlyPayment) {
        this.placementId = placementId;
        this.clientId = clientId;
        this.packageId = packageId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyPayment = monthlyPayment;
    }

    public Integer getPlacementId() { return placementId; }
    public void setPlacementId(Integer placementId) { this.placementId = placementId; }
    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }
    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getMonthlyPayment() { return monthlyPayment; }
    public void setMonthlyPayment(BigDecimal monthlyPayment) { this.monthlyPayment = monthlyPayment; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Placement)) return false;
        Placement placement = (Placement) o;
        return Objects.equals(placementId, placement.placementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placementId);
    }
}
