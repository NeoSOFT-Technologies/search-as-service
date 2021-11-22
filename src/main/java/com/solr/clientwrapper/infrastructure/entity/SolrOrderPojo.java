package com.solr.clientwrapper.infrastructure.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SolrDocument(collection = "order")
public class SolrOrderPojo {
	@Id
	@Indexed(name = "oid", type = "long")
	private Long orderid;

	@Indexed(name = "oname", type = "string")
	private String orderName;

	@Indexed(name = "odesc", type = "string")
	private String orderDescription;

	@Indexed(name = "pname", type = "string")
	private String productName;

	@Indexed(name = "cname", type = "string")
	private String customerName;

	@Indexed(name = "cmobile", type = "string")
	private String customerMobile;

}
