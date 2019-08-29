package com.axelor.apps.gst.service;

import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.exception.AxelorException;
import com.axelor.rpc.ActionResponse;

public interface InvoiceService {
	
	public Invoice setInvoiceData(Invoice invoice, List<Integer> productIdList, long partnerId, long companyId) throws AxelorException;
}
