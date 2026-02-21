import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    // ─────────────────────────────────────────────
    // Q1 - Total par facture
    // ─────────────────────────────────────────────
    public List<InvoiceTotal> findInvoiceTotals() {
        List<InvoiceTotal> result = new ArrayList<>();
        String sql = """
                SELECT i.id, i.customer_name, i.status,
                       SUM(il.quantity * il.unit_price) AS total
                FROM invoice i
                JOIN invoice_line il ON i.id = il.invoice_id
                GROUP BY i.id, i.customer_name, i.status
                ORDER BY i.id
                """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(new InvoiceTotal(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        InvoiceStatus.valueOf(rs.getString("status")),
                        rs.getBigDecimal("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ─────────────────────────────────────────────
    // Q2 - Total des factures CONFIRMED et PAID
    // ─────────────────────────────────────────────
    public List<InvoiceTotal> findConfirmedAndPaidInvoiceTotals() {
        List<InvoiceTotal> result = new ArrayList<>();
        String sql = """
                SELECT i.id, i.customer_name, i.status,
                       SUM(il.quantity * il.unit_price) AS total
                FROM invoice i
                JOIN invoice_line il ON i.id = il.invoice_id
                WHERE i.status IN ('CONFIRMED', 'PAID')
                GROUP BY i.id, i.customer_name, i.status
                ORDER BY i.id
                """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(new InvoiceTotal(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        InvoiceStatus.valueOf(rs.getString("status")),
                        rs.getBigDecimal("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ─────────────────────────────────────────────
    // Q3 - Totaux cumulés par statut
    // ─────────────────────────────────────────────
    public InvoiceStatusTotals computeStatusTotals() {
        String sql = """
                SELECT
                    SUM(CASE WHEN i.status = 'PAID'      THEN il.quantity * il.unit_price ELSE 0 END) AS total_paid,
                    SUM(CASE WHEN i.status = 'CONFIRMED' THEN il.quantity * il.unit_price ELSE 0 END) AS total_confirmed,
                    SUM(CASE WHEN i.status = 'DRAFT'     THEN il.quantity * il.unit_price ELSE 0 END) AS total_draft
                FROM invoice i
                JOIN invoice_line il ON i.id = il.invoice_id
                """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return new InvoiceStatusTotals(
                        rs.getBigDecimal("total_paid"),
                        rs.getBigDecimal("total_confirmed"),
                        rs.getBigDecimal("total_draft")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // Q4 - Chiffre d'affaires pondéré HT
    // PAID=100%, CONFIRMED=50%, DRAFT=0%
    // ─────────────────────────────────────────────
    public Double computeWeightedTurnover() {
        String sql = """
                SELECT SUM(
                    CASE
                        WHEN i.status = 'PAID'      THEN il.quantity * il.unit_price * 1.0
                        WHEN i.status = 'CONFIRMED' THEN il.quantity * il.unit_price * 0.5
                        ELSE 0
                    END
                ) AS weighted_turnover
                FROM invoice i
                JOIN invoice_line il ON i.id = il.invoice_id
                """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("weighted_turnover");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // Q5-A - Totaux HT, TVA et TTC par facture
    // ─────────────────────────────────────────────
    public List<InvoiceTaxSummary> findInvoiceTaxSummaries() {
        List<InvoiceTaxSummary> result = new ArrayList<>();
        String sql = """
                SELECT i.id,
                    SUM(il.quantity * il.unit_price) AS total_ht,
                    SUM(il.quantity * il.unit_price)
                        * (SELECT rate / 100.0 FROM tax_config WHERE label = 'TVA STANDARD') AS total_tva,
                    SUM(il.quantity * il.unit_price)
                        * (1 + (SELECT rate / 100.0 FROM tax_config WHERE label = 'TVA STANDARD')) AS total_ttc
                FROM invoice i
                JOIN invoice_line il ON i.id = il.invoice_id
                GROUP BY i.id
                ORDER BY i.id
                """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(new InvoiceTaxSummary(
                        rs.getInt("id"),
                        rs.getBigDecimal("total_ht"),
                        rs.getBigDecimal("total_tva"),
                        rs.getBigDecimal("total_ttc")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ─────────────────────────────────────────────
    // Q5-B - Chiffre d'affaires TTC pondéré
    // ─────────────────────────────────────────────
    public BigDecimal computeWeightedTurnoverTtc() {
        String sql = """
                SELECT SUM(
                    CASE
                        WHEN i.status = 'PAID'      THEN il.quantity * il.unit_price * 1.0
                        WHEN i.status = 'CONFIRMED' THEN il.quantity * il.unit_price * 0.5
                        ELSE 0
                    END
                ) * (1 + (SELECT rate / 100.0 FROM tax_config WHERE label = 'TVA STANDARD')) AS weighted_ttc
                FROM invoice i
                JOIN invoice_line il ON i.id = il.invoice_id
                """;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getBigDecimal("weighted_ttc");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}