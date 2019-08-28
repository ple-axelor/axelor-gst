package com.axelor.apps.gst.web;

import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.gst.service.InvoiceService;
import com.axelor.exception.AxelorException;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class InvoiceController {
	
	@Inject private InvoiceService service;

	public void setInvoiceData(ActionRequest request, ActionResponse response) throws AxelorException {
		Invoice invoice = request.getContext().asType(Invoice.class);

	    if (request.getContext().get("product_ids") != null) {
	      List<Integer> productIdList = (List<Integer>) request.getContext().get("product_ids");
	      int partnerId = (int) request.getContext().get("partner_id");
	      int companyId = (int) request.getContext().get("company_id");
	      Invoice invoiceObject = service.setInvoiceData(invoice, productIdList, partnerId,companyId);
	      response.setValues(invoiceObject);
	    }
	}
}
