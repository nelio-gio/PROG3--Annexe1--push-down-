import java.util.List;

public class Main {
    public static void main(String[] args) {

        DataRetriever retriever = new DataRetriever();

        // ── Q1 ──────────────────────────────────────
        System.out.println("=== Q1 - findInvoiceTotals() ===");
        List<InvoiceTotal> totals = retriever.findInvoiceTotals();
        for (InvoiceTotal t : totals) {
            System.out.println(t.getId() + " | " + t.getCustomerName() + " | " + t.getTotal());
        }

        // ── Q2 ──────────────────────────────────────
        System.out.println("\n=== Q2 - findConfirmedAndPaidInvoiceTotals() ===");
        List<InvoiceTotal> confirmedPaid = retriever.findConfirmedAndPaidInvoiceTotals();
        for (InvoiceTotal t : confirmedPaid) {
            System.out.println(t.getId() + " | " + t.getCustomerName() + " | " + t.getStatus() + " | " + t.getTotal());
        }

        // ── Q3 ──────────────────────────────────────
        System.out.println("\n=== Q3 - computeStatusTotals() ===");
        InvoiceStatusTotals st = retriever.computeStatusTotals();
        System.out.println("total_paid      = " + st.getTotalPaid());
        System.out.println("total_confirmed = " + st.getTotalConfirmed());
        System.out.println("total_draft     = " + st.getTotalDraft());

        // ── Q4 ──────────────────────────────────────
        System.out.println("\n=== Q4 - computeWeightedTurnover() ===");
        System.out.println(retriever.computeWeightedTurnover());

        // ── Q5-A ────────────────────────────────────
        System.out.println("\n=== Q5-A - findInvoiceTaxSummaries() ===");
        List<InvoiceTaxSummary> summaries = retriever.findInvoiceTaxSummaries();
        for (InvoiceTaxSummary s : summaries) {
            System.out.println(s.getId() + " | HT " + s.getTotalHT() + " | TVA " + s.getTotalTVA() + " | TTC " + s.getTotalTTC());
        }

        // ── Q5-B ────────────────────────────────────
        System.out.println("\n=== Q5-B - computeWeightedTurnoverTtc() ===");
        System.out.println(retriever.computeWeightedTurnoverTtc());
    }
}