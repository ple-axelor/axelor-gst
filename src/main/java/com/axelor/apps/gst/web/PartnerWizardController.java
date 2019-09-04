package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.gst.report.ITranslation;
import com.axelor.apps.gst.service.InvoiceService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.util.List;

public class PartnerWizardController {

  public void invoiceView(ActionRequest request, ActionResponse response) throws AxelorException {

    if (request.getContext().get("productIds") == null) {
      response.setError(I18n.get(ITranslation.RECORD_MISSING));
    } else {
      if (request.getContext().get("partner") != null) {
        Partner partner = (Partner) request.getContext().get("partner");

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
                Company company = (Company) request.getContext().get("company");

                if (company.getAddress() != null) {
                  if (company.getAddress().getState() != null) {
                    Long partnerId = partner.getId();
                    request.getContext().put("partnerId", partnerId);

                    Long companyId = company.getId();
                    request.getContext().put("companyId", companyId);
                    List<Integer> productIdList =
                        (List<Integer>) request.getContext().get("productIds");
                    Invoice invoice = request.getContext().asType(Invoice.class);
                    try {
                      Invoice invoiceObject =
                          Beans.get(InvoiceService.class)
                              .setInvoiceData(invoice, productIdList, partnerId, companyId);
                      if (invoiceObject != null) {
                        response.setView(
                            ActionView.define("Invoice")
                                .model(Invoice.class.getName())
                                .add("form", "invoice-form")
                                .add("grid", "invoice-grid")
                                .context(
                                    "_operationTypeSelect",
                                    InvoiceRepository.OPERATION_TYPE_CLIENT_SALE)
                                .context(
                                    "todayDate",
                                    Beans.get(AppSupplychainService.class).getTodayDate())
                                .context("_showRecord", String.valueOf(invoiceObject.getId()))
                                .map());
                        response.setCanClose(true);
                      }
                    } catch (Exception e) {
                      // TODO: handle exception
                      response.setFlash(e.getLocalizedMessage());
                    }

                  } else {
                    response.setError(I18n.get(ITranslation.COMPANY_STATE_MISSING));
                  }

                } else {
                  response.setError(I18n.get(ITranslation.COMPANY_ADDRESS_MISSING));
                }
              } else {
                response.setError(I18n.get(ITranslation.COMPANY_MISSING));
              }
            } else {
              response.setError(I18n.get(ITranslation.PARTNER_ADDRESS_STATE_MISSING));
            }

          } else {
            response.setError(I18n.get(ITranslation.PARTNER_INVOICE_ADDRESS_MISSING));
          }

        } else {
          response.setError(I18n.get(ITranslation.PARTNER_ADDRESS_MISSING));
        }

      } else {
        response.setError(I18n.get(ITranslation.PARTNER_MISSING));
      }
    }
  }
}
