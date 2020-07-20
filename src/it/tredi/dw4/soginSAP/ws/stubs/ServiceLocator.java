/**
 * ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.tredi.dw4.soginSAP.ws.stubs;

public class ServiceLocator extends org.apache.axis.client.Service implements it.tredi.dw4.soginSAP.ws.stubs.Service {

    public ServiceLocator() {
    }


    public ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ZWS_SOGIN_UPDATE_TAB
    private java.lang.String ZWS_SOGIN_UPDATE_TAB_address = "http://sapsgx.it.emea.csc.com:8043/sap/bc/srt/rfc/sap/zws_sogin_update_tab/010/zws_sogin_update_tab/zws_sogin_update_tab";

    public java.lang.String getZWS_SOGIN_UPDATE_TABAddress() {
        return ZWS_SOGIN_UPDATE_TAB_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ZWS_SOGIN_UPDATE_TABWSDDServiceName = "ZWS_SOGIN_UPDATE_TAB";

    public java.lang.String getZWS_SOGIN_UPDATE_TABWSDDServiceName() {
        return ZWS_SOGIN_UPDATE_TABWSDDServiceName;
    }

    public void setZWS_SOGIN_UPDATE_TABWSDDServiceName(java.lang.String name) {
        ZWS_SOGIN_UPDATE_TABWSDDServiceName = name;
    }

    public it.tredi.dw4.soginSAP.ws.stubs.ZWS_SOGIN_UPDATE_TAB_PortType getZWS_SOGIN_UPDATE_TAB() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ZWS_SOGIN_UPDATE_TAB_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getZWS_SOGIN_UPDATE_TAB(endpoint);
    }

    public it.tredi.dw4.soginSAP.ws.stubs.ZWS_SOGIN_UPDATE_TAB_PortType getZWS_SOGIN_UPDATE_TAB(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.tredi.dw4.soginSAP.ws.stubs.ZWS_SOGIN_UPDATE_TAB_BindingStub _stub = new it.tredi.dw4.soginSAP.ws.stubs.ZWS_SOGIN_UPDATE_TAB_BindingStub(portAddress, this);
            _stub.setPortName(getZWS_SOGIN_UPDATE_TABWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setZWS_SOGIN_UPDATE_TABEndpointAddress(java.lang.String address) {
        ZWS_SOGIN_UPDATE_TAB_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.tredi.dw4.soginSAP.ws.stubs.ZWS_SOGIN_UPDATE_TAB_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                it.tredi.dw4.soginSAP.ws.stubs.ZWS_SOGIN_UPDATE_TAB_BindingStub _stub = new it.tredi.dw4.soginSAP.ws.stubs.ZWS_SOGIN_UPDATE_TAB_BindingStub(new java.net.URL(ZWS_SOGIN_UPDATE_TAB_address), this);
                _stub.setPortName(getZWS_SOGIN_UPDATE_TABWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ZWS_SOGIN_UPDATE_TAB".equals(inputPortName)) {
            return getZWS_SOGIN_UPDATE_TAB();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:sap-com:document:sap:soap:functions:mc-style", "service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:sap-com:document:sap:soap:functions:mc-style", "ZWS_SOGIN_UPDATE_TAB"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ZWS_SOGIN_UPDATE_TAB".equals(portName)) {
            setZWS_SOGIN_UPDATE_TABEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
