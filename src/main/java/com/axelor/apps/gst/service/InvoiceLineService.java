package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;

public interface InvoiceLineService {

	public void computeValues(Invoice invoice, InvoiceLine invoiceLine);
}
