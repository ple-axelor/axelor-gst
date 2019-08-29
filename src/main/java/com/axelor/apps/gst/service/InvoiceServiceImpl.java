package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axelor.apps.account.db.Account;
import com.axelor.apps.account.db.FiscalPosition;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.InvoiceToolService;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.AddressRepository;
import com.axelor.apps.base.db.repo.CompanyRepository;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.base.service.AddressService;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class InvoiceServiceImpl implements InvoiceService{
	
	@Inject private InvoiceLineService invoiceLineService;
	@Inject private com.axelor.apps.gst.service.InvoiceLineService service;
	@Inject private com.axelor.apps.account.service.invoice.InvoiceService invoiceService;
	protected AccountManagementAccountService accountManagementAccountService;

	@Override
	public Invoice setInvoiceData(Invoice invoice, List<Integer> productIdList, long partnerId, long companyId) throws AxelorException {
		Company company = Beans.get(CompanyRepository.class).find(companyId);
		invoice.setCompany(company);
		Partner partner = Beans.get(PartnerRepository.class).find(partnerId);
		invoice.setPartner(partner);
		Address address = Beans.get(PartnerService.class).getInvoicingAddress(partner);
		invoice.setAddress(address);
		String addressStr = Beans.get(AddressService.class).computeAddressStr(invoice.getAddress());
		invoice.setAddressStr(addressStr);
		List<InvoiceLine> invoiceLineList = new ArrayList<>();
		FiscalPosition fiscalPosition = invoice.getPartner().getFiscalPosition();
		boolean isPurchase = InvoiceToolService.isPurchase(invoice);
		for (Integer productId : productIdList) {
			Product product = Beans.get(ProductRepository.class).find((long)productId);
			InvoiceLine invoiceLine = new InvoiceLine();
			invoiceLine.setProduct(product);
			invoiceLine.setProductName(product.getName());
			invoiceLine.setQty(BigDecimal.ONE);
			invoiceLine.setUnit(product.getUnit());
			invoiceLine.setPrice(product.getSalePrice());
			InvoiceLine invoiceLine2 = service.computeValues(invoice, invoiceLine);
			invoiceLine.setExTaxTotal(product.getSalePrice());
			/*Account account = accountManagementAccountService.getProductAccount(
			              product, company, fiscalPosition, isPurchase, invoiceLine.getFixedAssets());*/
			
			System.out.println(invoiceLine.getFixedAssets());
			/*invoiceLine.setAccount(product.getAccountManagementList().get(0).getSaleAccount());*/
			/*System.out.println(product.getAccountManagementList().get(0).getSaleAccount());*/
			invoiceLineList.add(invoiceLine);
		}
		invoice.setInvoiceLineList(invoiceLineList);
		invoice = invoiceService.compute(invoice);
		return invoice;				
	}

	
}