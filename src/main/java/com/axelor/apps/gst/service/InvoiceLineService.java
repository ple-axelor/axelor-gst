package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;

public interface InvoiceLineService {

  public InvoiceLine computeValues(Invoice invoice, InvoiceLine invoiceLine);
}
