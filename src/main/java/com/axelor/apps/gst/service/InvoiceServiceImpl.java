package com.axelor.apps.gst.service;

import java.util.ArrayList;
import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
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
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;

public class InvoiceServiceImpl implements InvoiceService{
	
	@Inject private InvoiceLineService invoiceLineService;
	@Inject private com.axelor.apps.gst.service.InvoiceLineService service;
	@Inject private com.axelor.apps.account.service.invoice.InvoiceService invoiceService;

	@Override
	public Invoice setInvoiceData(Invoice invoice, List<Integer> productIdList, long partnerId, long companyId) throws AxelorException {
		Company company = Beans.get(CompanyRepository.class).find(companyId);
		invoice.setCompany(company);
		Partner partner = Beans.get(PartnerRepository.class).find(partnerId);
		invoice.setPartner(partner);
		List<PartnerAddress> partnerAddressList = partner.getPartnerAddressList();
		PartnerAddress invoiceAddress = null;
		for (PartnerAddress partnerAddress : partner.getPartnerAddressList()) {
			if (partnerAddress.getIsInvoicingAddr()) {
				invoiceAddress = partnerAddress;
			}
		}
		invoice.setAddress(invoiceAddress.getAddress());
		String addressStr = Beans.get(AddressService.class).computeAddressStr(invoice.getAddress());
		invoice.setAddressStr(addressStr);
		List<InvoiceLine> invoiceLineList = new ArrayList<>();
		for (Integer productId : productIdList) {
			Product product = Beans.get(ProductRepository.class).find((long)productId);
			InvoiceLine invoiceLine = new InvoiceLine();
			invoiceLineList.add(invoiceLine);
		}
		invoice.setInvoiceLineList(invoiceLineList);
		return invoice;				
	}

	
}
