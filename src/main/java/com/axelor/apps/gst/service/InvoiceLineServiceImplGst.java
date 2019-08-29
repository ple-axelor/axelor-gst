package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.Tax;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.db.repo.TaxLineRepository;
import com.axelor.apps.account.db.repo.TaxRepository;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

public class InvoiceLineServiceImplGst implements com.axelor.apps.gst.service.InvoiceLineService {

 /* @Inject private InvoiceLineService invoiceLineService;*/

/*  @Inject
  public InvoiceLineServiceImplGst(
      CurrencyService currencyService,
      PriceListService priceListService,
      AppAccountService appAccountService,
      AnalyticMoveLineService analyticMoveLineService,
      AccountManagementAccountService accountManagementAccountService,
      PurchaseProductService purchaseProductService) {
    super(
        currencyService,
        priceListService,
        appAccountService,
        analyticMoveLineService,
        accountManagementAccountService,
        purchaseProductService);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Map<String, Object> fillProductInformation(Invoice invoice, InvoiceLine invoiceLine)
      throws AxelorException {

    Map<String, Object> productInformation = new HashMap<>();
    productInformation.putAll(super.fillProductInformation(invoice, invoiceLine));
    BigDecimal gstRate = invoiceLine.getProduct().getGstRate();
    productInformation.put("gstRate", gstRate);
    productInformation.put("hsbn", invoiceLine.getProduct().getHsbn());
    BigDecimal gstRatePercent = gstRate.divide(BigDecimal.valueOf(100));
    String gstCode = "GST";

    Tax tax = Beans.get(TaxRepository.class).all().filter("self.code = ?1", gstCode).fetchOne();
    if (tax != null) {
      TaxLine taxline =
          Beans.get(TaxLineRepository.class)
              .all()
              .filter("self.value = ?1 and self.tax = ?2", gstRatePercent, tax.getId())
              .fetchOne();
      productInformation.put("taxLine", taxline);
    }

    return productInformation;
  }*/

  @Override
  public InvoiceLine computeValues(Invoice invoice, InvoiceLine invoiceLine) {

    BigDecimal netAmount = BigDecimal.ZERO;
    BigDecimal grossAmount = BigDecimal.ZERO;
    BigDecimal igst = BigDecimal.ZERO;
    BigDecimal sgst = BigDecimal.ZERO;
    BigDecimal netAmountPercent = BigDecimal.ZERO;
        
    netAmount = invoiceLine.getPrice().multiply(invoiceLine.getQty());
    if(invoiceLine.getProduct() != null) {
    	
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
    	        (netAmount.multiply(invoiceLine.getProduct().getGstRate())).divide(BigDecimal.valueOf(100));
    	if (!(invoice.getCompany().getAddress().getState().equals(invoice.getAddress().getState()))) {
    	      igst = netAmountPercent;
    	      grossAmount = netAmount.add(igst);
    	    } else {
    	      sgst = (netAmountPercent).divide(BigDecimal.valueOf(2));
    	      grossAmount = netAmount.add(sgst).add(sgst);
    	    }
    }
    else {
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
