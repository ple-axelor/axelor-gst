package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.exception.AxelorException;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class InvoiceLineControllerGst {

  @Inject InvoiceLineService invoiceLineService;

  public void computeValues(ActionRequest request, ActionResponse response) throws AxelorException {

    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
    Invoice invoice = request.getContext().getParent().asType(Invoice.class);

    if (invoice.getAddress().getState() != null) {
      if (invoice.getCompany().getAddress() != null) {
        if (invoice.getCompany().getAddress().getState() != null) {
          if (invoice.getAddress() != null) {
            InvoiceLine invoiceLineObject = invoiceLineService.computeValues(invoice, invoiceLine);
            response.setValues(invoiceLineObject);
            /*response.setValue("igst", invoiceLine.getIgst());
            response.setValue("sgst", invoiceLine.getSgst());
            response.setValue("cgst", invoiceLine.getCgst());
            response.setValue("gstRate", invoiceLine.getGstRate());
            response.setValue("hsbn", invoiceLine.getHsbn());*/
          } else {
            throw new AxelorException(1, "Please Fill Invoice Address State");
          }
        } else {
          throw new AxelorException(1, "Please Fill Invoice Address");
        }
      } else {
        throw new AxelorException(1, "Company's State field is missing");
      }
    } else {
      throw new AxelorException(1, "Company's Address field is missing");
    }
  }
}
