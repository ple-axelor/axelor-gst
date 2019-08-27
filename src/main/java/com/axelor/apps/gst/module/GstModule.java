package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.web.InvoiceLineController;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.apps.gst.service.InvoiceLineServiceImpl;
import com.axelor.apps.gst.service.InvoiceLineServiceImplGst;
import com.axelor.apps.gst.web.InvoiceLineControllerGst;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(InvoiceLineSupplychainService.class).to(InvoiceLineServiceImplGst.class);
    bind(InvoiceLineService.class).to(InvoiceLineServiceImpl.class);
  }
}
