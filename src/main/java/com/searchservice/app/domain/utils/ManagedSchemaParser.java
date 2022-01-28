package com.searchservice.app.domain.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.searchservice.app.domain.dto.table.SchemaInfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ManagedSchemaParser {
	private static final Logger logger = LoggerFactory.getLogger(ManagedSchemaParser.class);
	private ManagedSchemaParser() {}
	
    /**
    * Get the Document Builder
    * Get Document
    * Normalize the xml structure
    * Get all the element by the tag name
    */
	public static SchemaInfo parseManagedSchema(String filePath) {
		SchemaInfo schemaInfo = new SchemaInfo("", new HashMap<>());
		// Get Normalized DOM for XML file
		Document document = getNormalizedDOM(filePath);

		if(document!=null) {
			NodeList schemaList = document.getElementsByTagName("schema");
			for (int i = 0; i < schemaList.getLength(); i++) {
				Node schema = schemaList.item(i);
				if (schema.getNodeType() == Node.ELEMENT_NODE) {
					getSchemaInfo(schemaInfo, schema);
				}
	        }
		} else {
			logger.info("Normalized DOM obtained from the XML data is NULL. Can't process further");
		}
		return schemaInfo;
	}
	
	protected static Document getNormalizedDOM(String filePath) {
		try {
			//Get the Document Builder
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
	        // Get Document
	        Document document = builder.parse(new File(filePath));
	        // Normalize the xml structure
	        document.getDocumentElement().normalize();
			return document;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logger.error("Error occurred while using DocumentBuilder. Exception: ", e);
		}
		return null;
	}
	
	protected static SchemaInfo getSchemaInfo(SchemaInfo schemaInfo, Node schema) {
		Element schemaElement = (Element) schema;
		logger.debug("Schema Name: {}", schemaElement.getAttribute("name"));

		// Add schema name in the schemaInfo
		schemaInfo.setSchemaName(schemaElement.getAttribute("name"));
		
		// Get all the schema attributes by the tag name
		NodeList schemaAttributes = schema.getChildNodes();
		logger.debug("Total schema attributes :: {}", schemaAttributes.getLength());
		for (int j = 0; j < schemaAttributes.getLength(); j++) {
			Node detail = schemaAttributes.item(j);
			if (detail.getNodeType() == Node.ELEMENT_NODE) {
				Element detailElement = (Element) detail;
				String schemaAttributeName = detailElement.getTagName();
				String schemaAttributeValue = detailElement.getAttribute("name");
				logger.debug("\t{} : {}", schemaAttributeName, schemaAttributeValue);
				
				// Add schemaAttribute to schemaInfo
				if(!schemaInfo.getSchemaAttributes().containsKey(schemaAttributeName))
					schemaInfo.getSchemaAttributes().put(
							schemaAttributeName, 
							new ArrayList<>());
				schemaInfo.getSchemaAttributes()
						.get(schemaAttributeName)
						.add(schemaAttributeValue);
			}
		}
		return schemaInfo;
	}
}
