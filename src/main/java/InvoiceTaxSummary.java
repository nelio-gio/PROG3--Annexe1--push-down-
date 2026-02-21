import java.math.BigDecimal;

public class InvoiceTaxSummary {
    private int id;
    private BigDecimal totalHT;
    private BigDecimal totalTVA;
    private BigDecimal totalTTC;

    public InvoiceTaxSummary(int id, BigDecimal totalHT, BigDecimal totalTVA, BigDecimal totalTTC) {
        this.id = id;
        this.totalHT = totalHT;
        this.totalTVA = totalTVA;
        this.totalTTC = totalTTC;
    }

    public int getId()              {
        return id;
    }
    public BigDecimal getTotalHT()  {
        return totalHT;
    }
    public BigDecimal getTotalTVA() {
        return totalTVA;
    }
    public BigDecimal getTotalTTC() {
        return totalTTC;
    }
}