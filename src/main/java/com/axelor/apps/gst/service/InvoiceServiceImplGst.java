package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;
import java.math.BigDecimal;

public class InvoiceServiceImplGst extends InvoiceServiceProjectImpl {

  @Inject
  public InvoiceServiceImplGst(
      ValidateFactory validateFactory,
      VentilateFactory ventilateFactory,
      CancelFactory cancelFactory,
      AlarmEngineService<Invoice> alarmEngineService,
      InvoiceRepository invoiceRepo,
      AppAccountService appAccountService,
      PartnerService partnerService,
      InvoiceLineService invoiceLineService,
      AccountConfigService accountConfigService) {
    super(
        validateFactory,
        ventilateFactory,
        cancelFactory,
        alarmEngineService,
        invoiceRepo,
        appAccountService,
        partnerService,
        invoiceLineService,
        accountConfigService);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Invoice compute(final Invoice invoice) throws AxelorException {

    super.compute(invoice);

    invoice.setNetIgst(BigDecimal.ZERO);
    invoice.setNetCgst(BigDecimal.ZERO);
    invoice.setNetSgst(BigDecimal.ZERO);

    if (invoice.getInvoiceLineList() != null) {
      for (InvoiceLine invoiceLine : invoice.getInvoiceLineList()) {
        invoice.setNetIgst(invoice.getNetIgst().add(invoiceLine.getIgst()));
        invoice.setNetCgst(invoice.getNetCgst().add(invoiceLine.getCgst()));
        invoice.setNetSgst(invoice.getNetSgst().add(invoiceLine.getSgst()));
      }
    }

    return invoice;
  }
}
