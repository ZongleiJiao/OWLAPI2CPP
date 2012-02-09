/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.coode.owlapi.owlxml.renderer;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.coode.xml.XMLWriter;
import org.coode.xml.XMLWriterFactory;
import org.coode.xml.XMLWriterNamespaceManager;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.io.OWLRendererIOException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.util.VersionInfo;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.OWLXMLVocabulary;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 12-Dec-2006<br><br>
 * Writes OWL/XML.  In an OWL/XML documents written by this writer, the base is always the ontology URI, and
 * the default namespace is always the OWL namespace (http://www.w3.org/2002/07/owl#).  Unlike RDF/XML, entity
 * URIs aren't abbreviated using the XML namespace mechanism, instead they are encoded using 'prefix' elements.
 */
public class OWLXMLWriter {

    /**
     * String comparator that takes length into account before natural ordering.
     * XXX stateless, might be used through a singleton
     */
    private static final class StringLengthComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            int diff = o1.length() - o2.length();
            if (diff != 0) {
                return diff;
            }
            return o1.compareTo(o2);
        }
    }


    private XMLWriter writer;

    private Map<String, String> iriPrefixMap = new TreeMap<String, String>(new StringLengthComparator());

    public OWLXMLWriter(Writer writer, OWLOntology ontology) {
        XMLWriterNamespaceManager nsm = new XMLWriterNamespaceManager(Namespaces.OWL.toString());
        nsm.setPrefix("xsd", Namespaces.XSD.toString());
        nsm.setPrefix("rdf", Namespaces.RDF.toString());
        nsm.setPrefix("rdfs", Namespaces.RDFS.toString());
        nsm.setPrefix("xml", Namespaces.XML.toString());
        String base = Namespaces.OWL.toString();
        if (ontology != null && !ontology.isAnonymous()) {
            base = ontology.getOntologyID().getOntologyIRI().toString();
        }
        this.writer = XMLWriterFactory.getInstance().createXMLWriter(writer, nsm, base);
    }

    public Map<String, String> getIRIPrefixMap() {
        return iriPrefixMap;
    }

    public XMLWriterNamespaceManager getNamespaceManager() {
        return writer.getNamespacePrefixes();
    }

    /**
     * A convenience method to write a prefix.
     * @param prefixName The name of the prefix (e.g.  owl: is the prefix name for the OWL prefix)
     * @param iri The prefix iri
     */
    public void writePrefix(String prefixName, String iri) throws IOException {
        writer.writeStartElement(OWLXMLVocabulary.PREFIX.getURI().toString());
        if (prefixName.endsWith(":")) {
            String attName = prefixName.substring(0, prefixName.length() - 1);
            writer.writeAttribute(OWLXMLVocabulary.NAME_ATTRIBUTE.getURI().toString(), attName);
        }
        else {
            writer.writeAttribute(OWLXMLVocabulary.NAME_ATTRIBUTE.getURI().toString(), prefixName);
        }
        writer.writeAttribute(OWLXMLVocabulary.IRI_ATTRIBUTE.getURI().toString(), iri);
        writer.writeEndElement();
        iriPrefixMap.put(iri, prefixName);
    }

    /**
     * Gets an IRI attribute value for a full IRI.  If the IRI has a prefix that coincides with
     * a written prefix then the compact IRI will be returned, otherwise the full IRI will be returned.
     * @param iri The IRI
     * @return Either the compact version of the IRI or the full IRI.
     */
    public String getIRIString(URI iri) {
        String fullIRI = iri.toString();
        for (String prefixName : iriPrefixMap.keySet()) {
            if (fullIRI.startsWith(prefixName)) {
                StringBuilder sb = new StringBuilder();
                sb.append(iriPrefixMap.get(prefixName));
                sb.append(fullIRI.substring(prefixName.length()));
                return sb.toString();
            }
        }
        return fullIRI;
    }


    public void startDocument(OWLOntology ontology) throws OWLRendererException {
        try {
            writer.startDocument(OWLXMLVocabulary.ONTOLOGY.toString());
            if (!ontology.isAnonymous()) {
                writer.writeAttribute(Namespaces.OWL + "ontologyIRI", ontology.getOntologyID().getOntologyIRI().toString());
                if (ontology.getOntologyID().getVersionIRI() != null) {
                    writer.writeAttribute(Namespaces.OWL + "versionIRI", ontology.getOntologyID().getVersionIRI().toString());
                }
            }
        }
        catch (IOException e) {
            throw new OWLRendererIOException(e);
        }
    }


    public void endDocument() {
        try {
            writer.endDocument();
            writer.writeComment(VersionInfo.getVersionInfo().getGeneratedByMessage());
        }
        catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public void writeStartElement(OWLXMLVocabulary name) {
        try {
            writer.writeStartElement(name.getURI().toString());
        }
        catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public void writeEndElement() {
        try {
            writer.writeEndElement();
        }
        catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }


    /**
     * Writes a datatype attributed (used on Literal elements).  The full datatype IRI is written out
     * @param datatype The datatype
     */
    public void writeDatatypeAttribute(OWLDatatype datatype) {
        try {
            writer.writeAttribute(OWLXMLVocabulary.DATATYPE_IRI.getURI().toString(), datatype.getIRI().toString());
        }
        catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

    public void writeNodeIDAttribute(NodeID nodeID) {
        try {
            writer.writeAttribute(OWLXMLVocabulary.NODE_ID.getURI().toString(), nodeID.toString());
        }
        catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

    public void writeIRIAttribute(IRI iri) {
        try {
            String attName = OWLXMLVocabulary.IRI_ATTRIBUTE.getURI().toString();
            String value = iri.toString();
            if (value.startsWith(writer.getXMLBase())) {
                writer.writeAttribute(attName, value.substring(writer.getXMLBase().length(), value.length()));
            }
            else {
                String val = getIRIString(iri.toURI());
                if (!val.equals(iri.toString())) {
                    writer.writeAttribute(OWLXMLVocabulary.ABBREVIATED_IRI_ATTRIBUTE.getURI().toString(), val);
                }
                else {
                    writer.writeAttribute(attName, val);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Writes an IRI element for a given IRI
     * @param iri The IRI to be written as an element.  If the IRI can be abbreviated
     * then an AbbreviatedIRI element will be written
     * @throws IOException
     */
    public void writeIRIElement(IRI iri) {
        try {
            String iriString = iri.toString();
            if (iriString.startsWith(writer.getXMLBase())) {
                writeStartElement(OWLXMLVocabulary.IRI_ELEMENT);
                writeTextContent(iriString.substring(writer.getXMLBase().length(), iriString.length()));
                writeEndElement();
            }
            else {
                String val = getIRIString(iri.toURI());
                if (!val.equals(iriString)) {
                    writeStartElement(OWLXMLVocabulary.ABBREVIATED_IRI_ELEMENT);
                    writer.writeTextContent(val);
                    writeEndElement();
                }
                else {
                    writeStartElement(OWLXMLVocabulary.IRI_ELEMENT);
                    writer.writeTextContent(val);
                    writeEndElement();
                }
            }
        }
        catch (IOException e) {
            throw new OWLRuntimeException(e);
        }

    }

    public void writeLangAttribute(String lang) {
        try {
            writer.writeAttribute(Namespaces.XML + "lang", lang);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void writeCardinalityAttribute(int cardinality) {
        try {
            writer.writeAttribute(OWLXMLVocabulary.CARDINALITY_ATTRIBUTE.getURI().toString(), Integer.toString(cardinality));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void writeTextContent(String text) {
        try {
            writer.writeTextContent(text);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void writeFacetAttribute(OWLFacet facet) {
        try {
            writer.writeAttribute(OWLXMLVocabulary.DATATYPE_FACET.getURI().toString(), facet.getIRI().toString());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void writeAnnotationURIAttribute(URI uri) {
        try {
            writer.writeAttribute(OWLXMLVocabulary.ANNOTATION_URI.toString(), uri.toString());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
