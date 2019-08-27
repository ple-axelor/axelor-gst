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
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.util.*;


import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.hsqldb.lib.IntValueHashMap;

public class InvoiceLineServiceImplGst extends InvoiceLineSupplychainService {

  @Inject private InvoiceLineService invoiceLineService;
	
  @Inject
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
    BigDecimal gstRatePercent = gstRate.divide(BigDecimal.valueOf(10));
    String gstCode = "GST";
    
    Tax tax = Beans.get(TaxRepository.class).all().filter("self.code = ?1",gstCode).fetchOne();
    if(tax != null) {
    	TaxLine taxline = Beans.get(TaxLineRepository.class).all().filter("self.value = ?1 and self.tax = ?2", gstRatePercent,tax.getId()).fetchOne();
    	productInformation.put("taxLine", taxline);
    }

    return productInformation;
  }
}
