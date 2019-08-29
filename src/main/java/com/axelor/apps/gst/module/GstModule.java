package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.service.invoice.InvoiceServiceImpl;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.apps.gst.service.InvoiceLineServiceImplGst;
import com.axelor.apps.gst.service.InvoiceService;
import com.axelor.apps.gst.service.InvoiceServiceImplGst;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {
/*    bind(InvoiceLineSupplychainService.class).to(InvoiceLineServiceImplGst.class);*/
    bind(InvoiceLineService.class).to(InvoiceLineServiceImplGst.class);
    bind(InvoiceServiceProjectImpl.class).to(InvoiceServiceImplGst.class);
    bind(InvoiceService.class).to(com.axelor.apps.gst.service.InvoiceServiceImpl.class);
  }
}
