package com.axelor.apps.gst.service;

import java.math.BigDecimal;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;

public class InvoiceLineServiceImpl implements InvoiceLineService{

	@Override
	public void computeValues(Invoice invoice, InvoiceLine invoiceLine) {
		BigDecimal netAmount = BigDecimal.ZERO;
	    BigDecimal grossAmount = BigDecimal.ZERO;
	    BigDecimal igst = BigDecimal.ZERO;
	    BigDecimal sgst = BigDecimal.ZERO;
	    BigDecimal netAmountPercent = BigDecimal.ZERO;
	    
	    netAmount = invoiceLine.getPrice().multiply(invoiceLine.getQty());
	    netAmountPercent = (netAmount.multiply(invoiceLine.getProduct().getGstRate())).divide(BigDecimal.valueOf(100));
	    
		if (!(invoice.getCompany().getAddress().getState().equals(invoice.getAddress().getState()))) {
			igst = netAmountPercent;
			grossAmount = netAmount.add(igst);
		} else {
			sgst = (netAmountPercent).divide(BigDecimal.valueOf(2));
			grossAmount = netAmount.add(sgst).add(sgst);
		}
		
		invoiceLine.setIgst(igst);
		invoiceLine.setCgst(sgst);
		invoiceLine.setSgst(sgst);
	}
	

}
