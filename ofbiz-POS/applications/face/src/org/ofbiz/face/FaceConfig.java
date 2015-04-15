package org.ofbiz.face;

import javolution.util.FastMap;

import org.ofbiz.base.config.GenericConfigException;

import org.ofbiz.base.config.ResourceLoader;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

public class FaceConfig {
	public static final String module = FaceConfig.class.getName();
	public static final String FACE_CONFIG_XML_FILENAME = "face-config.xml";
	private static final FastMap<String, FaceConfigInfo> faceConfigInfos = FastMap.newInstance();
	private static Element getXmlRootElement() throws GenericConfigException {
        try {
            return ResourceLoader.getXmlRootElement(FaceConfig.FACE_CONFIG_XML_FILENAME);
        } catch (GenericConfigException e) {
            throw new GenericConfigException("Could not get JNDI XML root element", e);
        }
    }

    static {
        try {
            initialize(getXmlRootElement());
        } catch (Exception e) {
            Debug.logError(e, "Error loading JNDI config XML file " + FACE_CONFIG_XML_FILENAME, module);
        }
    }
    public static void initialize(Element rootElement) throws GenericConfigException {
        // jndi-server - jndiServerInfos
        for (Element curElement: UtilXml.childElementList(rootElement, "face-node")) {
        	FaceConfigInfo faceConfigInfo = new FaceConfigInfo(curElement);

            faceConfigInfos.putIfAbsent(faceConfigInfo.name, faceConfigInfo);
        }
    }

    public static FaceConfigInfo getFaceConfigInfo(String name) {
        return faceConfigInfos.get(name);
    }
    
    
    public static final class FaceConfigInfo {
        public final String name;
        public final String model;
        public final String event;
        public final String xmlRoot;
        public final String xmlItem;
        public final String ebsUrl;
        public final String userName;
        public final String password;

        public FaceConfigInfo(Element element) {
            this.name = element.getAttribute("name");
            this.model = element.getAttribute("model");
            this.event = element.getAttribute("event");
            this.xmlRoot = element.getAttribute("xml-root");
            this.xmlItem = element.getAttribute("xml-item");
            this.ebsUrl = element.getAttribute("ebs-url");
            this.userName = element.getAttribute("user-name");
            this.password = element.getAttribute("password");
        }
    }
}
