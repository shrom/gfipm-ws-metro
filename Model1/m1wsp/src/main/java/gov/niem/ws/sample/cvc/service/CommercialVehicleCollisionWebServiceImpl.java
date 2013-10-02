/*
 * Copyright (c) 2012, Georgia Institute of Technology. All Rights Reserved.
 * This code was developed by Georgia Tech Research Institute (GTRI) under
 * a grant from the U.S. Dept. of Justice, Bureau of Justice Assistance.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gov.niem.ws.sample.cvc.service;

import gov.niem.ws.sample.cvc.jaxb.iepd.CommercialVehicleCollisionDocumentType;
import gov.niem.ws.sample.cvc.jaxb.msg.*;
import gov.niem.ws.sample.cvc.jaxws.CommercialVehicleCollisionPortType;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOM;

//@SchemaValidation(handler = gov.niem.ws.sample.jaxwsspr.server.handler.ErrorHandler.class)
//@javax.xml.ws.soap.MTOM(enabled=true,threshold=30000)
//@BindingType(value="http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
@MTOM
@WebService(serviceName = "CommercialVehicleCollisionWebService",
portName = "CommercialVehicleCollisionPort",
endpointInterface = "gov.niem.ws.sample.cvc.jaxws.CommercialVehicleCollisionPortType",
targetNamespace = "urn:examples.com:techniques:iepd:commercialVehicleCollision:ws:2.0",
wsdlLocation = "WEB-INF/wsdl/CommercialVehicleCollisionWebserviceImpl.wsdl")
public class CommercialVehicleCollisionWebServiceImpl implements CommercialVehicleCollisionPortType {

    gov.niem.ws.sample.cvc.jaxb.msg.ObjectFactory msgOF = new gov.niem.ws.sample.cvc.jaxb.msg.ObjectFactory();
    gov.niem.ws.sample.cvc.jaxb.iepd.ObjectFactory iepdOF = new gov.niem.ws.sample.cvc.jaxb.iepd.ObjectFactory();
    int imageCounter = 0;
    @Resource
    WebServiceContext wsContext;

    @Override
    public CommercialVehicleCollisionDocumentType getDocumentRequest(String documentFileControlID) {
//    public GetDocumentResponseType getDocument(GetDocumentRequestType parameters) {
        
        String currentMethodName = GFIPMAuthorizationProvider.getCurrentMethodName();
        if( ! GFIPMAuthorizationProvider.isServiceAuthorized(currentMethodName, wsContext) ){
            throw new RuntimeException("WSC is not authorized to invoke " + currentMethodName + " method");
        }        

        //String documentFileControlIDString = parameters.getDocumentFileControlID().getValue();
        String documentFileControlIDString = documentFileControlID;
        GetDocumentResponseType getDocumentResponse = msgOF.createGetDocumentResponseType();
        CommercialVehicleCollisionDocumentType commercialVehicleCollisionDocument = iepdOF.createCommercialVehicleCollisionDocumentType();
        commercialVehicleCollisionDocument.setDocumentFileControlID(iepdOF.createDocumentFileControlID(documentFileControlIDString));
        commercialVehicleCollisionDocument.setIncidentText(iepdOF.createIncidentText("Random text about the incident"));
        commercialVehicleCollisionDocument.getInvolvedVehicleVIN().add(iepdOF.createInvolvedVehicleVIN("29389839283923"));
        getDocumentResponse.setCommercialVehicleCollisionDocument(iepdOF.createCommercialVehicleCollisionDocument(commercialVehicleCollisionDocument));

        try {
            JAXBContext jc = JAXBContext.newInstance("gov.niem.ws.sample.cvc.jaxb");
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(msgOF.createGetDocumentResponse(getDocumentResponse), System.out);
        } catch (JAXBException ex) {
            Logger.getLogger(CommercialVehicleCollisionWebServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

//        return getDocumentResponse;
        return getDocumentResponse.getCommercialVehicleCollisionDocument().getValue();
    }

    @Override
    public String uploadPhotoRequest(Image photo) {
//    public UploadPhotoResponseType uploadPhoto(UploadPhotoRequestType parameters) {

            java.awt.Image data = photo;
            UploadPhotoResponseType uploadPhotoResponse = msgOF.createUploadPhotoResponseType();
            uploadPhotoResponse.setPhotoControlID(iepdOF.createPhotoControlID("Received image size is (width, height)  :  (" + data.getWidth(null) + ":" + data.getHeight(null)+")\n" + "Assigned image id is: " + imageCounter ));
            imageCounter++;
            return uploadPhotoResponse.getPhotoControlID().getValue();
        
//        if ((parameters.getPhoto() != null) && (parameters.getPhoto().getValue() != null)) {
//            java.awt.Image data = parameters.getPhoto().getValue();
//            UploadPhotoResponseType uploadPhotoResponse = msgOF.createUploadPhotoResponseType();
//            uploadPhotoResponse.setPhotoControlID(iepdOF.createPhotoControlID("Received image size is (width, height)  :  (" + data.getWidth(null) + ":" + data.getHeight(null)+")\n" + "Assigned image id is: " + imageCounter ));
//            imageCounter++;
//            return uploadPhotoResponse;
//        } else {
//            throw new WebServiceException("No image Received!");
//        }

    }

    @Override
    public DataHandler downloadDataRequest(Integer size) {    
//    public DownloadDataResponseType downloadData(DownloadDataRequestType parameters) {

        DownloadDataResponseType downloadDataResponseType = msgOF.createDownloadDataResponseType();
        DataHandler dataHandler = getDataHandler(size);
        JAXBElement<DataHandler> dataHandlerJAXBElement = iepdOF.createData(dataHandler);
        downloadDataResponseType.setData(dataHandlerJAXBElement);
        return downloadDataResponseType.getData().getValue();

//        int size = parameters.getSize().getValue().intValue();
//        DownloadDataResponseType downloadDataResponseType = msgOF.createDownloadDataResponseType();
//        DataHandler dataHandler = getDataHandler(size);
//        JAXBElement<DataHandler> dataHandlerJAXBElement = iepdOF.createData(dataHandler);
//        downloadDataResponseType.setData(dataHandlerJAXBElement);
//        return downloadDataResponseType;
        
    }

    /**
     * Streaming data handler
     */
    private static DataHandler getDataHandler(final int total) {
        return new DataHandler(new DataSource() {

            @Override
            public InputStream getInputStream() throws IOException {
                return new InputStream() {

                    int i;

                    @Override
                    public int read() throws IOException {
                        return i < total ? 'A' + (i++ % 26) : -1;
                    }
                };
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public String getContentType() {
                return "application/octet-stream";
            }

            @Override
            public String getName() {
                return "";
            }
        });
    }
}
