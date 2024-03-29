package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.exception.AxelorException;
import java.util.List;

public interface InvoiceService {

  public Invoice setInvoiceData(
      Invoice invoice, List<Integer> productIdList, long partnerId, long companyId)
      throws AxelorException;
}
