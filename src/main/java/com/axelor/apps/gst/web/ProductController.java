package com.axelor.apps.gst.web;

import java.util.List;

import com.axelor.app.AppSettings;
import com.axelor.apps.ReportFactory;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.exception.AxelorException;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class ProductController {

	public void Report(ActionRequest request, ActionResponse response) throws AxelorException {
		
		List<Integer> ids;
	    if (request.getContext().get("_ids") == null) {
	      response.setError("Please Select Atleast One Record");
	    } else {
	      ids = (List<Integer>) request.getContext().get("_ids");
	      String ids_str = ids.toString();
	      String productId = ids_str.substring(1, ids_str.length() - 1);
	      
	      String name = "report";
	      
	      String fileLink =
	              ReportFactory.createReport(IReport.GST_PRODUCT, name + "-${date}")
	                  .addParam("productId", productId)
	                  .addParam("imagePath", AppSettings.get().getPath("file.upload.dir", "file.upload.dir"))
	                  .generate()
	                  .getFileLink();
	      response.setView(ActionView.define(name).add("html", fileLink).map());
	    }
	}
}
