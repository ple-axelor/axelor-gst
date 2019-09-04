package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.inject.Beans;
import java.math.BigDecimal;
import java.util.*;

public class InvoiceLineServiceImplGst implements com.axelor.apps.gst.service.InvoiceLineService {

  @Override
  public InvoiceLine computeValues(Invoice invoice, InvoiceLine invoiceLine) {

    BigDecimal netAmount = BigDecimal.ZERO;
    BigDecimal igst = BigDecimal.ZERO;
    BigDecimal sgst = BigDecimal.ZERO;
    BigDecimal netAmountPercent = BigDecimal.ZERO;

    netAmount = invoiceLine.getPrice().multiply(invoiceLine.getQty());
    if (invoiceLine.getProduct() != null) {

      BigDecimal gstRate = invoiceLine.getProduct().getGstRate();
      invoiceLine.setGstRate(gstRate);
      String hsbn = invoiceLine.getProduct().getHsbn();
      invoiceLine.setHsbn(hsbn);

      BigDecimal gstRatePercent = gstRate.divide(BigDecimal.valueOf(100));
      String gstCode = "GST";

      Tax tax = Beans.get(TaxRepository.class).all().filter("self.code = ?1", gstCode).fetchOne();
      if (tax != null) {
        TaxLine taxLine =
            Beans.get(TaxLineRepository.class)
                .all()
                .filter("self.value = ?1 and self.tax = ?2", gstRatePercent, tax.getId())
                .fetchOne();
        invoiceLine.setTaxLine(taxLine);
      }

      netAmountPercent =
          (netAmount.multiply(invoiceLine.getProduct().getGstRate()))
              .divide(BigDecimal.valueOf(100));
      if (!(invoice.getCompany().getAddress().getState().equals(invoice.getAddress().getState()))) {
        igst = netAmountPercent;
      } else {
        sgst = (netAmountPercent).divide(BigDecimal.valueOf(2));
      }
    } else {
      invoiceLine.setGstRate(BigDecimal.ZERO);
      invoiceLine.setHsbn(null);
      invoiceLine.setQty(BigDecimal.ONE);
    }

    invoiceLine.setIgst(igst);
    invoiceLine.setCgst(sgst);
    invoiceLine.setSgst(sgst);
    return invoiceLine;
  }
}
