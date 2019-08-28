package com.axelor.apps.gst.web;

import java.util.Map;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.base.db.repo.CompanyRepository;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class PartnerWizardController {

	public void invoiceView(ActionRequest request, ActionResponse response) throws AxelorException {
	
		if (request.getContext().get("productIds") == null) {
			response.setError("Please Select Atleast One Record");
		} else {
			if (request.getContext().get("partner") != null) {
				Map<?, ?> partnerMap = (Map<?, ?>) request.getContext().get("partner");
				Partner partner = Beans.get(PartnerRepository.class)
						.find(Long.parseLong(partnerMap.get("id").toString()));

				if (partner.getPartnerAddressList() != null) {
					PartnerAddress invoiceAddress = null;

					for (PartnerAddress partnerAddress : partner.getPartnerAddressList()) {
						if (partnerAddress.getIsInvoicingAddr()) {
							invoiceAddress = partnerAddress;
						}
					}

					if (invoiceAddress != null) {

						if (invoiceAddress.getAddress().getState() != null) {
							if (request.getContext().get("company") != null) {
								Map<?, ?> companyMap = (Map<?, ?>) request.getContext().get("company");
								Company company = Beans.get(CompanyRepository.class)
										.find(Long.parseLong(companyMap.get("id").toString()));

								if (company.getAddress() != null) {
									if (company.getAddress().getState() != null) {
										Long partnerId = partner.getId();
										request.getContext().put("partnerId", partnerId);
																				
										Long companyId = company.getId();
										request.getContext().put("companyId", companyId);
										response.setView(ActionView.define("Invoice").model(Invoice.class.getName())
												.add("form", "invoice-form")
												.context("product_ids", request.getContext().get("productIds"))
												.context("partner_id", request.getContext().get("partnerId"))
												.context("company_id", request.getContext().get("companyId"))
												.context("_operationTypeSelect", InvoiceRepository.OPERATION_TYPE_CLIENT_SALE)
												.context("todayDate", Beans.get(AppSupplychainService.class).getTodayDate()).map());
									} else {
										response.setError("Please fill state in company Address");
									}

								} else {
									response.setError("Please fill address in company Address");
								}
							} else {
								response.setError("Please Select Company");
							}
						} else {
							response.setError("Please fill state in invoice Address");
						}

					} else {
						response.setError("Please fill Invoice address of Partner");
					}

				} else {
					response.setError("Please Fill Address of Party");
				}

			} else {
				response.setError("Please select Party");
			}
		}
		response.setCanClose(true);
	}
}
