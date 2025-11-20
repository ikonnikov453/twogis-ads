package com.example.twogisads.entity;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * сущность рекламного пакета (ad_packages)
 */
public class AdPackage {
    private Integer packageId;
    private String packageName;
    private String description;
    private BigDecimal monthlyFee;

    public AdPackage() {}

    public AdPackage(Integer packageId, String packageName, String description, BigDecimal monthlyFee) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.description = description;
        this.monthlyFee = monthlyFee;
    }

    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(BigDecimal monthlyFee) { this.monthlyFee = monthlyFee; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdPackage)) return false;
        AdPackage that = (AdPackage) o;
        return Objects.equals(packageId, that.packageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageId);
    }
}
