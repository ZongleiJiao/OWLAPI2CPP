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
 * Copyright 2011, The University of Manchester
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

package org.coode.owlapi.functionalrenderer;

import static org.semanticweb.owlapi.vocab.OWLXMLVocabulary.*;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.coode.string.EscapeUtils;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLXMLVocabulary;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 13-Dec-2006<br><br>
 */
public class OWLObjectRenderer implements OWLObjectVisitor {

    private DefaultPrefixManager prefixManager;

    protected OWLOntology ontology;

    private Writer writer;

    private int pos;

    int lastNewLinePos;

    private boolean writeEnitiesAsURIs;

    private OWLObject focusedObject;

    public OWLObjectRenderer(OWLOntologyManager man, OWLOntology ontology, Writer writer) {
        this.ontology = ontology;
        this.writer = writer;
        writeEnitiesAsURIs = true;
        prefixManager = new DefaultPrefixManager();
        if (!ontology.isAnonymous()) {
            String defPrefix = ontology.getOntologyID().getOntologyIRI() + "#";
            prefixManager.setDefaultPrefix(defPrefix);
        }
        OWLOntologyFormat ontologyFormat = man.getOntologyFormat(ontology);
        if(ontologyFormat instanceof PrefixOWLOntologyFormat) {
            PrefixOWLOntologyFormat prefixFormat = (PrefixOWLOntologyFormat) ontologyFormat;
            for(String prefixName : prefixFormat.getPrefixNames()) {
                String prefix = prefixFormat.getPrefix(prefixName);
                prefixManager.setPrefix(prefixName, prefix);
            }
        }

        focusedObject = man.getOWLDataFactory().getOWLThing();
    }

    public void setPrefixManager(DefaultPrefixManager prefixManager) {
        this.prefixManager = prefixManager;
    }

    public void setFocusedObject(OWLObject focusedObject) {
        this.focusedObject = focusedObject;
    }


    public void writePrefix(String prefix, String namespace) {
        write("Prefix");
        writeOpenBracket();
        write(prefix);
        write("=");
        write("<");
        write(namespace);
        write(">");
        writeCloseBracket();
        write("\n");
    }


    public void writePrefixes() {
        for (String prefix : prefixManager.getPrefixName2PrefixMap().keySet()) {
            writePrefix(prefix, prefixManager.getPrefix(prefix));
        }
    }


    private void write(OWLXMLVocabulary v) {
        write(v.getShortName());
    }


    private void write(String s) {
        try {
            int newLineIndex = s.indexOf('\n');
            if (newLineIndex != -1) {
                lastNewLinePos = pos + newLineIndex;
            }
            pos += s.length();
            writer.write(s);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


//    private int getIndent() {
//        return pos - lastNewLinePos - 1;
//    }
//
//
//    private void writeIndent(int indent) {
//        for (int i = 0; i < indent; i++) {
//            writeSpace();
//        }
//    }


    private void write(IRI iri) {
        String iriString = iri.toString();
        String qname = prefixManager.getPrefixIRI(iri);
        if (qname != null && !qname.equals(iriString)) {
            write(qname);
        }
        else {
            write("<");
            write(iriString);
            write(">");
        }
    }


    private void writeFullIRI(IRI iri) {
        write("<");
        write(iri.toString());
        write(">");
    }


    public void visit(OWLOntology ontology) {
        writePrefixes();
        write("\n\n");
        write(ONTOLOGY);
        write("(");
        if (!ontology.isAnonymous()) {
            writeFullIRI(ontology.getOntologyID().getOntologyIRI());
            if (ontology.getOntologyID().getVersionIRI() != null) {
                write("\n");
                writeFullIRI(ontology.getOntologyID().getVersionIRI());
            }
            write("\n");
        }
        for (OWLImportsDeclaration decl : ontology.getImportsDeclarations()) {
            write(IMPORT);
            write("(");
            writeFullIRI(decl.getIRI());
            write(")\n");
        }

        for (OWLAnnotation ontologyAnnotation : ontology.getAnnotations()) {
            ontologyAnnotation.accept(this);
            write("\n");
        }
        write("\n");

        Set<OWLAxiom> writtenAxioms = new HashSet<OWLAxiom>();

        for (OWLEntity ent : new TreeSet<OWLEntity>(ontology.getSignature())) {
            writtenAxioms.addAll(writeAxioms(ent));
        }

        List<OWLAxiom> remainingAxioms = new ArrayList<OWLAxiom>(ontology.getAxioms());
        remainingAxioms.removeAll(writtenAxioms);

        for (OWLAxiom ax : remainingAxioms) {
            ax.accept(this);
            write("\n");
        }
        write(")");

//        write("\n// ");
//        write(VersionInfo.getVersionInfo().getGeneratedByMessage());
    }


    /**
     * Writes out the axioms that define the specified entity
     * @param entity The entity
     * @return The set of axioms that was written out
     */
    public Set<OWLAxiom> writeAxioms(OWLEntity entity) {
        Set<OWLAxiom> writtenAxioms = new HashSet<OWLAxiom>();
        setFocusedObject(entity);
        writtenAxioms.addAll(writeDeclarations(entity));
        writtenAxioms.addAll(writeAnnotations(entity));
        List<OWLAxiom> axs = new ArrayList<OWLAxiom>();
        axs.addAll(entity.accept(new OWLEntityVisitorEx<Set<? extends OWLAxiom>>() {
            public Set<? extends OWLAxiom> visit(OWLClass cls) {
                return ontology.getAxioms(cls);
            }


            public Set<? extends OWLAxiom> visit(OWLObjectProperty property) {
                return ontology.getAxioms(property);
            }


            public Set<? extends OWLAxiom> visit(OWLDataProperty property) {
                return ontology.getAxioms(property);
            }


            public Set<? extends OWLAxiom> visit(OWLNamedIndividual individual) {
                return ontology.getAxioms(individual);
            }


            public Set<? extends OWLAxiom> visit(OWLDatatype datatype) {
                return ontology.getAxioms(datatype);
            }


            public Set<? extends OWLAxiom> visit(OWLAnnotationProperty property) {
                return ontology.getAxioms(property);
            }
        }));
        Collections.sort(axs);
        for (OWLAxiom ax : axs) {
            if (ax.getAxiomType().equals(AxiomType.DIFFERENT_INDIVIDUALS)) {
                continue;
            }
            if (ax.getAxiomType().equals(AxiomType.DISJOINT_CLASSES) && ((OWLDisjointClassesAxiom) ax).getClassExpressions().size() > 2) {
                continue;
            }
            ax.accept(this);
            writtenAxioms.add(ax);
            write("\n");
        }
        return writtenAxioms;
    }


    /**
     * Writes out the declaration axioms for the specified entity
     * @param entity The entity
     * @return The axioms that were written out
     */
    public Set<OWLAxiom> writeDeclarations(OWLEntity entity) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLAxiom ax : ontology.getDeclarationAxioms(entity)) {
            ax.accept(this);
            axioms.add(ax);
            write("\n");
        }
        return axioms;
    }


    /**
     * Writes of the annotation for the specified entity
     * @param entity The entity
     * @return The set of axioms that were written out
     */
    public Set<OWLAxiom> writeAnnotations(OWLEntity entity) {
        Set<OWLAxiom> annotationAssertions = new HashSet<OWLAxiom>();
        for (OWLAnnotationAxiom ax : entity.getAnnotationAssertionAxioms(ontology)) {
            ax.accept(this);
            annotationAssertions.add(ax);
            write("\n");
        }
        return annotationAssertions;
    }


    public void write(OWLXMLVocabulary v, OWLObject o) {
        write(v);
        write("(");
        o.accept(this);
        write(")");
    }


    private void write(Collection<? extends OWLObject> objects) {
        if (objects.size() > 2) {
            //int indent = getIndent();
            for (Iterator<? extends OWLObject> it = objects.iterator(); it.hasNext();) {
                it.next().accept(this);
                if (it.hasNext()) {
                    write(" ");
//                    writeIndent(indent);
                }
            }
        }
        else if (objects.size() == 2) {
            Iterator<? extends OWLObject> it = objects.iterator();
            OWLObject objA = it.next();
            OWLObject objB = it.next();
            OWLObject lhs, rhs;
            if (objA.equals(focusedObject)) {
                lhs = objA;
                rhs = objB;
            }
            else {
                lhs = objB;
                rhs = objA;
            }
            lhs.accept(this);
            writeSpace();
            rhs.accept(this);
        }
        else if(objects.size() == 1) {
            objects.iterator().next().accept(this);
        }
    }


    public void writeOpenBracket() {
        write("(");
    }


    public void writeCloseBracket() {
        write(")");
    }


    public void writeSpace() {
        write(" ");
    }


    public void write(OWLAnnotation annotation) {

    }

    public void writeAnnotations(OWLAxiom ax) {
        for (OWLAnnotation anno : ax.getAnnotations()) {
            anno.accept(this);
            write(" ");
        }
    }


    public void writeAxiomStart(OWLXMLVocabulary v, OWLAxiom axiom) {
        write(v);
        writeOpenBracket();
        writeAnnotations(axiom);
    }


    public void writeAxiomEnd() {
        write(")");
    }


    public void writePropertyCharacteristic(OWLXMLVocabulary v, OWLAxiom ax, OWLPropertyExpression<?,?> prop) {
        writeAxiomStart(v, ax);
        prop.accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(ASYMMETRIC_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }


    public void visit(OWLClassAssertionAxiom axiom) {
        writeAxiomStart(CLASS_ASSERTION, axiom);
        axiom.getClassExpression().accept(this);
        writeSpace();
        axiom.getIndividual().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        writeAxiomStart(DATA_PROPERTY_ASSERTION, axiom);
        axiom.getProperty().accept(this);
        writeSpace();
        axiom.getSubject().accept(this);
        writeSpace();
        axiom.getObject().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLDataPropertyDomainAxiom axiom) {
        writeAxiomStart(DATA_PROPERTY_DOMAIN, axiom);
        axiom.getProperty().accept(this);
        writeSpace();
        axiom.getDomain().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLDataPropertyRangeAxiom axiom) {
        writeAxiomStart(DATA_PROPERTY_RANGE, axiom);
        axiom.getProperty().accept(this);
        writeSpace();
        axiom.getRange().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        writeAxiomStart(SUB_DATA_PROPERTY_OF, axiom);
        axiom.getSubProperty().accept(this);
        writeSpace();
        axiom.getSuperProperty().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLDeclarationAxiom axiom) {
        writeAxiomStart(DECLARATION, axiom);
        writeEnitiesAsURIs = false;
        axiom.getEntity().accept(this);
        writeEnitiesAsURIs = true;
        writeAxiomEnd();
    }


    public void visit(OWLDifferentIndividualsAxiom axiom) {
        writeAxiomStart(DIFFERENT_INDIVIDUALS, axiom);
        write(axiom.getIndividuals());
        writeAxiomEnd();
    }


    public void visit(OWLDisjointClassesAxiom axiom) {
        writeAxiomStart(DISJOINT_CLASSES, axiom);
        write(axiom.getClassExpressions());
        writeAxiomEnd();
    }


    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        writeAxiomStart(DISJOINT_DATA_PROPERTIES, axiom);
        write(axiom.getProperties());
        writeAxiomEnd();
    }


    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        writeAxiomStart(DISJOINT_OBJECT_PROPERTIES, axiom);
        write(axiom.getProperties());
        writeAxiomEnd();
    }


    public void visit(OWLDisjointUnionAxiom axiom) {
        writeAxiomStart(DISJOINT_UNION, axiom);
        axiom.getOWLClass().accept(this);
        writeSpace();
        write(axiom.getClassExpressions());
        writeAxiomEnd();
    }


    public void visit(OWLAnnotationAssertionAxiom axiom) {
        writeAxiomStart(ANNOTATION_ASSERTION, axiom);
        axiom.getProperty().accept(this);
        writeSpace();
        axiom.getSubject().accept(this);
        writeSpace();
        axiom.getValue().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        writeAxiomStart(EQUIVALENT_CLASSES, axiom);
        write(axiom.getClassExpressions());
        writeAxiomEnd();
    }


    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        writeAxiomStart(EQUIVALENT_DATA_PROPERTIES, axiom);
        write(axiom.getProperties());
        writeAxiomEnd();
    }


    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        writeAxiomStart(EQUIVALENT_OBJECT_PROPERTIES, axiom);
        write(axiom.getProperties());
        writeAxiomEnd();
    }


    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        writePropertyCharacteristic(FUNCTIONAL_DATA_PROPERTY, axiom, axiom.getProperty());
    }


    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(FUNCTIONAL_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }


    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(INVERSE_FUNCTIONAL_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }


    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        writeAxiomStart(INVERSE_OBJECT_PROPERTIES, axiom);
        axiom.getFirstProperty().accept(this);
        writeSpace();
        axiom.getSecondProperty().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(IRREFLEXIVE_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }


    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        writeAxiomStart(NEGATIVE_DATA_PROPERTY_ASSERTION, axiom);
        axiom.getProperty().accept(this);
        writeSpace();
        axiom.getSubject().accept(this);
        writeSpace();
        axiom.getObject().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        writeAxiomStart(NEGATIVE_OBJECT_PROPERTY_ASSERTION, axiom);
        axiom.getProperty().accept(this);
        writeSpace();
        axiom.getSubject().accept(this);
        writeSpace();
        axiom.getObject().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        writeAxiomStart(OBJECT_PROPERTY_ASSERTION, axiom);
        axiom.getProperty().accept(this);
        writeSpace();
        axiom.getSubject().accept(this);
        writeSpace();
        axiom.getObject().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        writeAxiomStart(SUB_OBJECT_PROPERTY_OF, axiom);
        write(OBJECT_PROPERTY_CHAIN);
        writeOpenBracket();
        for (Iterator<OWLObjectPropertyExpression> it = axiom.getPropertyChain().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" ");
            }
        }
        writeCloseBracket();
        writeSpace();
        axiom.getSuperProperty().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        writeAxiomStart(OBJECT_PROPERTY_DOMAIN, axiom);
        axiom.getProperty().accept(this);
        writeSpace();
        axiom.getDomain().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        writeAxiomStart(OBJECT_PROPERTY_RANGE, axiom);
        axiom.getProperty().accept(this);
        writeSpace();
        axiom.getRange().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        writeAxiomStart(SUB_OBJECT_PROPERTY_OF, axiom);
        axiom.getSubProperty().accept(this);
        writeSpace();
        axiom.getSuperProperty().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(REFLEXIVE_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }


    public void visit(OWLSameIndividualAxiom axiom) {
        writeAxiomStart(SAME_INDIVIDUAL, axiom);
        write(axiom.getIndividuals());
        writeAxiomEnd();
    }


    public void visit(OWLSubClassOfAxiom axiom) {
        writeAxiomStart(SUB_CLASS_OF, axiom);
        axiom.getSubClass().accept(this);
        writeSpace();
        axiom.getSuperClass().accept(this);
        writeAxiomEnd();
    }


    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(SYMMETRIC_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }


    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        writePropertyCharacteristic(TRANSITIVE_OBJECT_PROPERTY, axiom, axiom.getProperty());
    }


    public void visit(OWLClass desc) {
        if (!writeEnitiesAsURIs) {
            write(CLASS);
            writeOpenBracket();
        }
        desc.getIRI().accept(this);
        if (!writeEnitiesAsURIs) {
            writeCloseBracket();
        }
    }


    private <R extends OWLPropertyRange, P extends OWLPropertyExpression<R, P>, F extends OWLPropertyRange> void writeRestriction(OWLXMLVocabulary v, OWLCardinalityRestriction<R, P, F> restriction) {
        write(v);
        writeOpenBracket();
        write(Integer.toString(restriction.getCardinality()));
        writeSpace();
        restriction.getProperty().accept(this);
        if (restriction.isQualified()) {
            writeSpace();
            restriction.getFiller().accept(this);
        }
        writeCloseBracket();
    }

    private void writeRestriction(OWLXMLVocabulary v, OWLQuantifiedDataRestriction restriction) {
    	writeRestriction(v, restriction.getProperty(), restriction.getFiller());
    }
    
    private void writeRestriction(OWLXMLVocabulary v, OWLQuantifiedObjectRestriction restriction) {
        writeRestriction(v, restriction.getProperty(), restriction.getFiller());
    }


    private void writeRestriction(OWLXMLVocabulary v, OWLPropertyExpression<?,?> prop, OWLObject filler) {
        write(v);
        writeOpenBracket();
        prop.accept(this);
        writeSpace();
        filler.accept(this);
        writeCloseBracket();
    }


    public void visit(OWLDataAllValuesFrom desc) {
        writeRestriction(DATA_ALL_VALUES_FROM, desc);
    }


    public void visit(OWLDataExactCardinality desc) {
        writeRestriction(DATA_EXACT_CARDINALITY, desc);
    }


    public void visit(OWLDataMaxCardinality desc) {
        writeRestriction(DATA_MAX_CARDINALITY, desc);
    }


    public void visit(OWLDataMinCardinality desc) {
        writeRestriction(DATA_MIN_CARDINALITY, desc);
    }


    public void visit(OWLDataSomeValuesFrom desc) {
        writeRestriction(DATA_SOME_VALUES_FROM, desc);
    }


    public void visit(OWLDataHasValue desc) {
        writeRestriction(DATA_HAS_VALUE, desc.getProperty(), desc.getValue());
    }


    public void visit(OWLObjectAllValuesFrom desc) {
        writeRestriction(OBJECT_ALL_VALUES_FROM, desc);
    }


    public void visit(OWLObjectComplementOf desc) {
        write(OBJECT_COMPLEMENT_OF, desc.getOperand());
    }


    public void visit(OWLObjectExactCardinality desc) {
        writeRestriction(OBJECT_EXACT_CARDINALITY, desc);
    }


    public void visit(OWLObjectIntersectionOf desc) {
        write(OBJECT_INTERSECTION_OF);
        writeOpenBracket();
        write(desc.getOperands());
        writeCloseBracket();
    }


    public void visit(OWLObjectMaxCardinality desc) {
        writeRestriction(OBJECT_MAX_CARDINALITY, desc);
    }


    public void visit(OWLObjectMinCardinality desc) {
        writeRestriction(OBJECT_MIN_CARDINALITY, desc);
    }


    public void visit(OWLObjectOneOf desc) {
        write(OBJECT_ONE_OF);
        writeOpenBracket();
        write(desc.getIndividuals());
        writeCloseBracket();
    }


    public void visit(OWLObjectHasSelf desc) {
        write(OBJECT_HAS_SELF, desc.getProperty());
    }


    public void visit(OWLObjectSomeValuesFrom desc) {
        writeRestriction(OBJECT_SOME_VALUES_FROM, desc);
    }


    public void visit(OWLObjectUnionOf desc) {
        write(OBJECT_UNION_OF);
        writeOpenBracket();
        write(desc.getOperands());
        writeCloseBracket();
    }


    public void visit(OWLObjectHasValue desc) {
        writeRestriction(OBJECT_HAS_VALUE, desc.getProperty(), desc.getValue());
    }


    public void visit(OWLDataComplementOf node) {
        write(DATA_COMPLEMENT_OF, node.getDataRange());
    }


    public void visit(OWLDataOneOf node) {
        write(DATA_ONE_OF);
        write("(");
        write(node.getValues());
        write(")");
    }


    public void visit(OWLDatatype node) {
        if (!writeEnitiesAsURIs) {
            write(DATATYPE);
            writeOpenBracket();
        }
        node.getIRI().accept(this);
        if (!writeEnitiesAsURIs) {
            writeCloseBracket();
        }
    }


    public void visit(OWLDatatypeRestriction node) {
        write(DATATYPE_RESTRICTION);
        writeOpenBracket();
        node.getDatatype().accept(this);
        for (OWLFacetRestriction restriction : node.getFacetRestrictions()) {
            writeSpace();
            restriction.accept(this);
        }
        writeCloseBracket();
    }


    public void visit(OWLFacetRestriction node) {
        write(node.getFacet().getIRI());
        writeSpace();
        node.getFacetValue().accept(this);
    }


    public void visit(OWLLiteral node) {
        write("\"");
        write(EscapeUtils.escapeString(node.getLiteral()));
        write("\"");
        if(node.hasLang()) {
            write("@");
            write(node.getLang());
        }
        else if(!node.isRDFPlainLiteral()) {
            write("^^");
            write(node.getDatatype().getIRI());
        }

    }

    public void visit(OWLDataProperty property) {
        if (!writeEnitiesAsURIs) {
            write(DATA_PROPERTY);
            writeOpenBracket();
        }
        property.getIRI().accept(this);
        if (!writeEnitiesAsURIs) {
            writeCloseBracket();
        }
    }


    public void visit(OWLObjectProperty property) {
        if (!writeEnitiesAsURIs) {
            write(OBJECT_PROPERTY);
            writeOpenBracket();
        }
        property.getIRI().accept(this);
        if (!writeEnitiesAsURIs) {
            writeCloseBracket();
        }
    }


    public void visit(OWLObjectInverseOf property) {
        write(OBJECT_INVERSE_OF);
        writeOpenBracket();
        property.getInverse().accept(this);
        writeCloseBracket();
    }


    public void visit(OWLNamedIndividual individual) {
        if (!writeEnitiesAsURIs) {
            write(NAMED_INDIVIDUAL);
            writeOpenBracket();
        }
        individual.getIRI().accept(this);
        if (!writeEnitiesAsURIs) {
            writeCloseBracket();
        }
    }

    public void visit(OWLHasKeyAxiom axiom) {
        writeAxiomStart(HAS_KEY, axiom);
        axiom.getClassExpression().accept(this);
        write(" ");
        write("(");
        for (Iterator<? extends OWLPropertyExpression<?,?>> it = axiom.getObjectPropertyExpressions().iterator(); it.hasNext();) {
            OWLPropertyExpression<?,?> prop = it.next();
            prop.accept(this);
            if (it.hasNext()) {
                write(" ");
            }
        }
        write(") (");
        for (Iterator<? extends OWLPropertyExpression<?,?>> it = axiom.getDataPropertyExpressions().iterator(); it.hasNext();) {
            OWLPropertyExpression<?,?> prop = it.next();
            prop.accept(this);
            if (it.hasNext()) {
                write(" ");
            }
        }
        write(")");
        writeAxiomEnd();
    }

    public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
        writeAxiomStart(ANNOTATION_PROPERTY_DOMAIN, axiom);
        axiom.getProperty().accept(this);
        write(" ");
        axiom.getDomain().accept(this);
        writeAxiomEnd();
    }

    public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
        writeAxiomStart(ANNOTATION_PROPERTY_RANGE, axiom);
        axiom.getProperty().accept(this);
        write(" ");
        axiom.getRange().accept(this);
        writeAxiomEnd();
    }

    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        writeAxiomStart(SUB_ANNOTATION_PROPERTY_OF, axiom);
        axiom.getSubProperty().accept(this);
        write(" ");
        axiom.getSuperProperty().accept(this);
        writeAxiomEnd();
    }

    public void visit(OWLDataIntersectionOf node) {
        write(DATA_INTERSECTION_OF);
        writeOpenBracket();
        write(node.getOperands());
        writeCloseBracket();
    }

    public void visit(OWLDataUnionOf node) {
        write(DATA_UNION_OF);
        writeOpenBracket();
        write(node.getOperands());
        writeCloseBracket();
    }

    public void visit(OWLAnnotationProperty property) {
        if (!writeEnitiesAsURIs) {
            write(ANNOTATION_PROPERTY);
            writeOpenBracket();
        }
        property.getIRI().accept(this);
        if (!writeEnitiesAsURIs) {
            writeCloseBracket();
        }
    }

    public void visit(OWLAnonymousIndividual individual) {
        write(individual.getID().toString());
    }

    public void visit(IRI iri) {
        write(iri);
    }

    public void visit(OWLAnnotation node) {
        write(ANNOTATION);
        write("(");
        for (OWLAnnotation anno : node.getAnnotations()) {
            anno.accept(this);
            write(" ");
        }
        node.getProperty().accept(this);
        write(" ");
        node.getValue().accept(this);
        write(")");
    }


    public void visit(OWLDatatypeDefinitionAxiom axiom) {
        writeAxiomStart(DATATYPE_DEFINITION, axiom);
        axiom.getDatatype().accept(this);
        writeSpace();
        axiom.getDataRange().accept(this);
        writeAxiomEnd();
    }


    public void visit(SWRLRule rule) {
        writeAxiomStart(DL_SAFE_RULE, rule);
        write(BODY);
        writeOpenBracket();
        write(rule.getBody());
        writeCloseBracket();
        write(HEAD);
        writeOpenBracket();
        write(rule.getHead());
        writeCloseBracket();
        writeAxiomEnd();
    }


    public void visit(SWRLIndividualArgument node) {
        node.getIndividual().accept(this);
    }

    public void visit(SWRLClassAtom node) {
        write(CLASS_ATOM);
        writeOpenBracket();
        node.getPredicate().accept(this);
        writeSpace();
        node.getArgument().accept(this);
        writeCloseBracket();
    }


    public void visit(SWRLDataRangeAtom node) {
        write(DATA_RANGE_ATOM);
        writeOpenBracket();
        node.getPredicate().accept(this);
        writeSpace();
        node.getArgument().accept(this);
        writeCloseBracket();
    }


    public void visit(SWRLObjectPropertyAtom node) {
        write(OBJECT_PROPERTY_ATOM);
        writeOpenBracket();
        node.getPredicate().accept(this);
        writeSpace();
        node.getFirstArgument().accept(this);
        writeSpace();
        node.getSecondArgument().accept(this);
        writeCloseBracket();
    }


    public void visit(SWRLDataPropertyAtom node) {
        write(DATA_PROPERTY_ATOM);
        writeOpenBracket();
        node.getPredicate().accept(this);
        writeSpace();
        node.getFirstArgument().accept(this);
        writeSpace();
        node.getSecondArgument().accept(this);
        writeCloseBracket();
    }


    public void visit(SWRLBuiltInAtom node) {
        write(BUILT_IN_ATOM);
        writeOpenBracket();
        node.getPredicate().accept(this);
        writeSpace();
        write(node.getArguments());
        writeCloseBracket();
    }


    public void visit(SWRLVariable node) {
        write(VARIABLE);
        writeOpenBracket();
        node.getIRI().accept(this);
        writeCloseBracket();
    }


    public void visit(SWRLLiteralArgument node) {
        node.getLiteral().accept(this);
    }


    public void visit(SWRLDifferentIndividualsAtom node) {
        write(DIFFERENT_INDIVIDUALS_ATOM);
        writeOpenBracket();
        node.getFirstArgument().accept(this);
        writeSpace();
        node.getSecondArgument().accept(this);
        writeCloseBracket();
    }


    public void visit(SWRLSameIndividualAtom node) {
        write(SAME_INDIVIDUAL_ATOM);
        writeOpenBracket();
        node.getFirstArgument().accept(this);
        writeSpace();
        node.getSecondArgument().accept(this);
        writeCloseBracket();
    }
}
