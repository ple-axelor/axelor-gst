package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.generator.InvoiceGenerator;
import com.axelor.apps.account.service.invoice.generator.InvoiceLineGenerator;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.BankDetails;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.CompanyRepository;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {

  @Inject private InvoiceLineService invoiceLineService;
  @Inject private com.axelor.apps.gst.service.InvoiceLineService service;
  @Inject private com.axelor.apps.account.service.invoice.InvoiceService invoiceService;

  @Override
  @Transactional(rollbackOn = {AxelorException.class, RuntimeException.class})
  public Invoice setInvoiceData(
      Invoice invoice, List<Integer> productIdList, long partnerId, long companyId)
      throws AxelorException {

    Company company = Beans.get(CompanyRepository.class).find(companyId);
    Partner partner = Beans.get(PartnerRepository.class).find(partnerId);
    Address address = Beans.get(PartnerService.class).getInvoicingAddress(partner);
    List<InvoiceLine> invoiceLineList = new ArrayList<>();

    Partner partnerContact = null;
    if (partner.getContactPartnerSet() != null) {
      for (Partner partnerContactObject : partner.getContactPartnerSet()) {
        partnerContact = partnerContactObject;
      }
    }

    BankDetails bankdetails = null;
    if (company.getBankDetailsSet() != null) {
      for (BankDetails bankDetailsObject : company.getBankDetailsSet()) {
        bankdetails = bankDetailsObject;
      }
    }

    InvoiceGenerator invoiceGenerator =
        new InvoiceGenerator(
            InvoiceRepository.OPERATION_TYPE_CLIENT_SALE,
            company,
            partner.getPaymentCondition(),
            partner.getInPaymentMode(),
            address,
            partner,
            partnerContact,
            company.getCurrency(),
            null,
            null,
            null,
            false,
            bankdetails,
            null) {

          @Override
          public Invoice generate() throws AxelorException {
            return super.createInvoiceHeader();
          }
        };
    invoice = invoiceGenerator.generate();

    BigDecimal qty = BigDecimal.ONE;
    BigDecimal discountAmount = BigDecimal.ZERO;
    int discountTypeSelect = 0;

    for (Integer productId : productIdList) {
      Product product = Beans.get(ProductRepository.class).find((long) productId);

      InvoiceLineGenerator invoiceLineGenerator =
          new InvoiceLineGenerator(
              invoice,
              product,
              product.getName(),
              product.getSalePrice(),
              product.getSalePrice(),
              product.getSalePrice(),
              product.getDescription(),
              qty,
              product.getUnit(),
              null,
              InvoiceLineGenerator.DEFAULT_SEQUENCE,
              discountAmount,
              discountTypeSelect,
              null,
              null,
              false,
              false,
              0) {

            @Override
            public List<InvoiceLine> creates() throws AxelorException {
              InvoiceLine invoiceLine = this.createInvoiceLine();
              invoiceLine = service.computeValues(invoice, invoiceLine);
              List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();
              invoiceLines.add(invoiceLine);
              return invoiceLines;
            }
          };

      invoiceLineList.addAll(invoiceLineGenerator.creates());
    }

    invoice.setInvoiceLineList(invoiceLineList);
    invoice = invoiceService.compute(invoice);
    Beans.get(InvoiceRepository.class).save(invoice);
    return invoice;
  }
}
