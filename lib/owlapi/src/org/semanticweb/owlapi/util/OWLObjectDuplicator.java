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

package org.semanticweb.owlapi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 11-Dec-2006<br><br>
 */
public class OWLObjectDuplicator implements OWLObjectVisitor, SWRLObjectVisitor {

    private OWLDataFactory dataFactory;

    private Object obj;

    private Map<OWLEntity, IRI> replacementMap;


    /**
     * Creates an object duplicator that duplicates objects using the specified
     * data factory.
     *
     * @param dataFactory The data factory to be used for the duplication.
     */
    public OWLObjectDuplicator(OWLDataFactory dataFactory) {
        this(new HashMap<OWLEntity, IRI>(), dataFactory);
    }


    /**
     * Creates an object duplicator that duplicates objects using the specified
     * data factory and uri replacement map.
     *
     * @param dataFactory       The data factory to be used for the duplication.
     * @param iriReplacementMap The map to use for the replacement of URIs.  Any uris
     *                          the appear in the map will be replaced as objects are duplicated.  This can
     *                          be used to "rename" entities.
     */
    public OWLObjectDuplicator(OWLDataFactory dataFactory, Map<IRI, IRI> iriReplacementMap) {
        this.dataFactory = dataFactory;
        this.replacementMap = new HashMap<OWLEntity, IRI>();
        for (IRI iri : iriReplacementMap.keySet()) {
            IRI repIRI = iriReplacementMap.get(iri);
            replacementMap.put(dataFactory.getOWLClass(iri), repIRI);
            replacementMap.put(dataFactory.getOWLObjectProperty(iri), repIRI);
            replacementMap.put(dataFactory.getOWLDataProperty(iri), repIRI);
            replacementMap.put(dataFactory.getOWLNamedIndividual(iri), repIRI);
            replacementMap.put(dataFactory.getOWLDatatype(iri), repIRI);
            replacementMap.put(dataFactory.getOWLAnnotationProperty(iri), repIRI);
        }
    }

    /**
     * Creates an object duplicator that duplicates objects using the specified
     * data factory and uri replacement map.
     *
     * @param dataFactory             The data factory to be used for the duplication.
     * @param entityIRIReplacementMap The map to use for the replacement of URIs.  Any uris
     *                                the appear in the map will be replaced as objects are duplicated.  This can
     *                                be used to "rename" entities.
     */
    public OWLObjectDuplicator(Map<OWLEntity, IRI> entityIRIReplacementMap, OWLDataFactory dataFactory) {
        this.dataFactory = dataFactory;
        this.replacementMap = new HashMap<OWLEntity, IRI>(entityIRIReplacementMap);
    }

    @SuppressWarnings("unchecked")
    public <O extends OWLObject> O duplicateObject(OWLObject object) {
        object.accept(this);
        return (O) obj;
    }


    protected void setLastObject(Object obj) {
        this.obj = obj;
    }


    /**
     * Given an IRI belonging to an entity, returns a IRI.  This may be the same IRI that the entity has, or
     * an alternative IRI if a replacement has been specified.
     * @param entity The entity
     * @return The IRI
     */
    private IRI getIRI(OWLEntity entity) {
        IRI replacement = replacementMap.get(entity);
        if (replacement != null) {
            return replacement;
        } else {
            return entity.getIRI();
        }
    }

    private Set<OWLAnnotation> duplicateAxiomAnnotations(OWLAxiom axiom) {
        Set<OWLAnnotation> duplicatedAnnos = new HashSet<OWLAnnotation>();
        for(OWLAnnotation anno : axiom.getAnnotations()) {
            anno.accept(this);
            duplicatedAnnos.add((OWLAnnotation) obj);
        }
        return duplicatedAnnos;
    }

    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        axiom.getProperty().accept(this);
        obj = dataFactory.getOWLAsymmetricObjectPropertyAxiom((OWLObjectPropertyExpression) obj, duplicateAxiomAnnotations(axiom));
    }

    public void visit(OWLClassAssertionAxiom axiom) {
        axiom.getIndividual().accept(this);
        OWLIndividual ind = (OWLIndividual) obj;
        axiom.getClassExpression().accept(this);
        OWLClassExpression type = (OWLClassExpression) obj;
        obj = dataFactory.getOWLClassAssertionAxiom(type, ind, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        axiom.getSubject().accept(this);
        OWLIndividual subj = (OWLIndividual) obj;
        axiom.getProperty().accept(this);
        OWLDataPropertyExpression prop = (OWLDataPropertyExpression) obj;
        axiom.getObject().accept(this);
        OWLLiteral con = (OWLLiteral) obj;
        obj = dataFactory.getOWLDataPropertyAssertionAxiom(prop, subj, con, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLDataPropertyDomainAxiom axiom) {
        axiom.getProperty().accept(this);
        OWLDataPropertyExpression prop = (OWLDataPropertyExpression) obj;
        axiom.getDomain().accept(this);
        OWLClassExpression domain = (OWLClassExpression) obj;
        obj = dataFactory.getOWLDataPropertyDomainAxiom(prop, domain, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLDataPropertyRangeAxiom axiom) {
        axiom.getProperty().accept(this);
        OWLDataPropertyExpression prop = (OWLDataPropertyExpression) obj;
        axiom.getRange().accept(this);
        OWLDataRange range = (OWLDataRange) obj;
        obj = dataFactory.getOWLDataPropertyRangeAxiom(prop, range, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        axiom.getSubProperty().accept(this);
        OWLDataPropertyExpression subProp = (OWLDataPropertyExpression) obj;
        axiom.getSuperProperty().accept(this);
        OWLDataPropertyExpression supProp = (OWLDataPropertyExpression) obj;
        obj = dataFactory.getOWLSubDataPropertyOfAxiom(subProp, supProp, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLDeclarationAxiom axiom) {
        axiom.getEntity().accept(this);
        OWLEntity ent = (OWLEntity) obj;
        obj = dataFactory.getOWLDeclarationAxiom(ent, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLDifferentIndividualsAxiom axiom) {
        Set<OWLIndividual> inds = duplicateSet(axiom.getIndividuals());
        obj = dataFactory.getOWLDifferentIndividualsAxiom(inds, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLDisjointClassesAxiom axiom) {
        Set<OWLClassExpression> descs = duplicateSet(axiom.getClassExpressions());
        obj = dataFactory.getOWLDisjointClassesAxiom(descs, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        Set<OWLDataPropertyExpression> props = duplicateSet(axiom.getProperties());
        obj = dataFactory.getOWLDisjointDataPropertiesAxiom(props, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        Set<OWLObjectPropertyExpression> props = duplicateSet(axiom.getProperties());
        obj = dataFactory.getOWLDisjointObjectPropertiesAxiom(props, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLDisjointUnionAxiom axiom) {
        axiom.getOWLClass().accept(this);
        OWLClass cls = (OWLClass) obj;
        Set<OWLClassExpression> ops = duplicateSet(axiom.getClassExpressions());
        obj = dataFactory.getOWLDisjointUnionAxiom(cls, ops, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLAnnotationAssertionAxiom axiom) {
        axiom.getSubject().accept(this);
        OWLAnnotationSubject subject = (OWLAnnotationSubject) obj;
        axiom.getProperty().accept(this);
        OWLAnnotationProperty prop = (OWLAnnotationProperty) obj;
        axiom.getValue().accept(this);
        OWLAnnotationValue value = (OWLAnnotationValue) obj;
        obj = dataFactory.getOWLAnnotationAssertionAxiom(prop, subject, value, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        Set<OWLClassExpression> descs = duplicateSet(axiom.getClassExpressions());
        obj = dataFactory.getOWLEquivalentClassesAxiom(descs, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
        Set<OWLDataPropertyExpression> props = duplicateSet(axiom.getProperties());
        obj = dataFactory.getOWLEquivalentDataPropertiesAxiom(props, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        Set<OWLObjectPropertyExpression> props = duplicateSet(axiom.getProperties());
        obj = dataFactory.getOWLEquivalentObjectPropertiesAxiom(props, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        axiom.getProperty().accept(this);
        obj = dataFactory.getOWLFunctionalDataPropertyAxiom((OWLDataPropertyExpression) obj, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        axiom.getProperty().accept(this);
        obj = dataFactory.getOWLFunctionalObjectPropertyAxiom((OWLObjectPropertyExpression) obj, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        axiom.getProperty().accept(this);
        obj = dataFactory.getOWLInverseFunctionalObjectPropertyAxiom((OWLObjectPropertyExpression) obj, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        axiom.getFirstProperty().accept(this);
        OWLObjectPropertyExpression propA = (OWLObjectPropertyExpression) obj;
        axiom.getSecondProperty().accept(this);
        OWLObjectPropertyExpression propB = (OWLObjectPropertyExpression) obj;
        obj = dataFactory.getOWLInverseObjectPropertiesAxiom(propA, propB, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        axiom.getProperty().accept(this);
        obj = dataFactory.getOWLIrreflexiveObjectPropertyAxiom((OWLObjectPropertyExpression) obj, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        axiom.getSubject().accept(this);
        OWLIndividual ind = (OWLIndividual) obj;
        axiom.getProperty().accept(this);
        OWLDataPropertyExpression prop = (OWLDataPropertyExpression) obj;
        axiom.getObject().accept(this);
        OWLLiteral con = (OWLLiteral) obj;
        obj = dataFactory.getOWLNegativeDataPropertyAssertionAxiom(prop, ind, con, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        axiom.getSubject().accept(this);
        OWLIndividual ind = (OWLIndividual) obj;
        axiom.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        axiom.getObject().accept(this);
        OWLIndividual ind2 = (OWLIndividual) obj;
        obj = dataFactory.getOWLNegativeObjectPropertyAssertionAxiom(prop, ind, ind2, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        axiom.getSubject().accept(this);
        OWLIndividual ind = (OWLIndividual) obj;
        axiom.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        axiom.getObject().accept(this);
        OWLIndividual ind2 = (OWLIndividual) obj;
        obj = dataFactory.getOWLObjectPropertyAssertionAxiom(prop, ind, ind2, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        axiom.getSuperProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        List<OWLObjectPropertyExpression> chain = new ArrayList<OWLObjectPropertyExpression>();
        for (OWLObjectPropertyExpression p : axiom.getPropertyChain()) {
            p.accept(this);
            chain.add((OWLObjectPropertyExpression) obj);
        }
        obj = dataFactory.getOWLSubPropertyChainOfAxiom(chain, prop, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        axiom.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        axiom.getDomain().accept(this);
        OWLClassExpression domain = (OWLClassExpression) obj;
        obj = dataFactory.getOWLObjectPropertyDomainAxiom(prop, domain, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        axiom.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        axiom.getRange().accept(this);
        OWLClassExpression range = (OWLClassExpression) obj;
        obj = dataFactory.getOWLObjectPropertyRangeAxiom(prop, range, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        axiom.getSubProperty().accept(this);
        OWLObjectPropertyExpression subProp = (OWLObjectPropertyExpression) obj;
        axiom.getSuperProperty().accept(this);
        OWLObjectPropertyExpression supProp = (OWLObjectPropertyExpression) obj;
        obj = dataFactory.getOWLSubObjectPropertyOfAxiom(subProp, supProp, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        axiom.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        obj = dataFactory.getOWLReflexiveObjectPropertyAxiom(prop, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLSameIndividualAxiom axiom) {
        Set<OWLIndividual> individuals = duplicateSet(axiom.getIndividuals());
        obj = dataFactory.getOWLSameIndividualAxiom(individuals, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLSubClassOfAxiom axiom) {
        axiom.getSubClass().accept(this);
        OWLClassExpression subClass = (OWLClassExpression) obj;
        axiom.getSuperClass().accept(this);
        OWLClassExpression supClass = (OWLClassExpression) obj;
        obj = dataFactory.getOWLSubClassOfAxiom(subClass, supClass, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        axiom.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        obj = dataFactory.getOWLSymmetricObjectPropertyAxiom(prop, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        axiom.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        obj = dataFactory.getOWLTransitiveObjectPropertyAxiom(prop, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLClass desc) {
        IRI uri = getIRI(desc);
        obj = dataFactory.getOWLClass(uri);
    }


    public void visit(OWLDataAllValuesFrom desc) {
        desc.getProperty().accept(this);
        OWLDataPropertyExpression prop = (OWLDataPropertyExpression) obj;
        desc.getFiller().accept(this);
        OWLDataRange filler = (OWLDataRange) obj;
        obj = dataFactory.getOWLDataAllValuesFrom(prop, filler);
    }


    public void visit(OWLDataExactCardinality desc) {
        desc.getProperty().accept(this);
        OWLDataPropertyExpression prop = (OWLDataPropertyExpression) obj;
        desc.getFiller().accept(this);
        OWLDataRange filler = (OWLDataRange) obj;
        obj = dataFactory.getOWLDataExactCardinality(desc.getCardinality(), prop, filler);
    }


    public void visit(OWLDataMaxCardinality desc) {
        desc.getProperty().accept(this);
        OWLDataPropertyExpression prop = (OWLDataPropertyExpression) obj;
        desc.getFiller().accept(this);
        OWLDataRange filler = (OWLDataRange) obj;
        obj = dataFactory.getOWLDataMaxCardinality(desc.getCardinality(), prop, filler);
    }


    public void visit(OWLDataMinCardinality desc) {
        desc.getProperty().accept(this);
        OWLDataPropertyExpression prop = (OWLDataPropertyExpression) obj;
        desc.getFiller().accept(this);
        OWLDataRange filler = (OWLDataRange) obj;
        obj = dataFactory.getOWLDataMinCardinality(desc.getCardinality(), prop, filler);
    }


    public void visit(OWLDataSomeValuesFrom desc) {
        desc.getProperty().accept(this);
        OWLDataPropertyExpression prop = (OWLDataPropertyExpression) obj;
        desc.getFiller().accept(this);
        OWLDataRange filler = (OWLDataRange) obj;
        obj = dataFactory.getOWLDataSomeValuesFrom(prop, filler);
    }


    public void visit(OWLDataHasValue desc) {
        desc.getProperty().accept(this);
        OWLDataPropertyExpression prop = (OWLDataPropertyExpression) obj;
        desc.getValue().accept(this);
        OWLLiteral val = (OWLLiteral) obj;
        obj = dataFactory.getOWLDataHasValue(prop, val);
    }


    public void visit(OWLObjectAllValuesFrom desc) {
        desc.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        desc.getFiller().accept(this);
        OWLClassExpression filler = (OWLClassExpression) obj;
        obj = dataFactory.getOWLObjectAllValuesFrom(prop, filler);
    }


    public void visit(OWLObjectComplementOf desc) {
        desc.getOperand().accept(this);
        OWLClassExpression op = (OWLClassExpression) obj;
        obj = dataFactory.getOWLObjectComplementOf(op);
    }


    public void visit(OWLObjectExactCardinality desc) {
        desc.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        desc.getFiller().accept(this);
        OWLClassExpression filler = (OWLClassExpression) obj;
        obj = dataFactory.getOWLObjectExactCardinality(desc.getCardinality(), prop, filler);
    }


    public void visit(OWLObjectIntersectionOf desc) {
        Set<OWLClassExpression> ops = duplicateSet(desc.getOperands());
        obj = dataFactory.getOWLObjectIntersectionOf(ops);
    }


    public void visit(OWLObjectMaxCardinality desc) {
        desc.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        desc.getFiller().accept(this);
        OWLClassExpression filler = (OWLClassExpression) obj;
        obj = dataFactory.getOWLObjectMaxCardinality(desc.getCardinality(), prop, filler);
    }


    public void visit(OWLObjectMinCardinality desc) {
        desc.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        desc.getFiller().accept(this);
        OWLClassExpression filler = (OWLClassExpression) obj;
        obj = dataFactory.getOWLObjectMinCardinality(desc.getCardinality(), prop, filler);
    }


    public void visit(OWLObjectOneOf desc) {
        Set<OWLIndividual> inds = duplicateSet(desc.getIndividuals());
        obj = dataFactory.getOWLObjectOneOf(inds);
    }


    public void visit(OWLObjectHasSelf desc) {
        desc.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        obj = dataFactory.getOWLObjectHasSelf(prop);
    }


    public void visit(OWLObjectSomeValuesFrom desc) {
        desc.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        desc.getFiller().accept(this);
        OWLClassExpression filler = (OWLClassExpression) obj;
        obj = dataFactory.getOWLObjectSomeValuesFrom(prop, filler);
    }


    public void visit(OWLObjectUnionOf desc) {
        Set<OWLClassExpression> ops = duplicateSet(desc.getOperands());
        obj = dataFactory.getOWLObjectUnionOf(ops);
    }


    public void visit(OWLObjectHasValue desc) {
        desc.getProperty().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        desc.getValue().accept(this);
        OWLIndividual value = (OWLIndividual) obj;
        obj = dataFactory.getOWLObjectHasValue(prop, value);
    }


    public void visit(OWLDataComplementOf node) {
        node.getDataRange().accept(this);
        OWLDataRange dr = (OWLDataRange) obj;
        obj = dataFactory.getOWLDataComplementOf(dr);
    }


    public void visit(OWLDataOneOf node) {
        Set<OWLLiteral> vals = duplicateSet(node.getValues());
        obj = dataFactory.getOWLDataOneOf(vals);
    }


    public void visit(OWLDatatype node) {
        IRI iri = getIRI(node);
        obj = dataFactory.getOWLDatatype(iri);
    }


    public void visit(OWLDatatypeRestriction node) {
        node.getDatatype().accept(this);
        OWLDatatype dr = (OWLDatatype) obj;
        Set<OWLFacetRestriction> restrictions = new HashSet<OWLFacetRestriction>();
        for (OWLFacetRestriction restriction : node.getFacetRestrictions()) {
            restriction.accept(this);
            restrictions.add((OWLFacetRestriction) obj);
        }
        obj = dataFactory.getOWLDatatypeRestriction(dr, restrictions);
    }


    public void visit(OWLFacetRestriction node) {
        node.getFacetValue().accept(this);
        OWLLiteral val = (OWLLiteral) obj;
        obj = dataFactory.getOWLFacetRestriction(node.getFacet(), val);
    }


    public void visit(OWLLiteral node) {
        node.getDatatype().accept(this);
        OWLDatatype dt = (OWLDatatype) obj;
        if(node.hasLang()) {
            obj = dataFactory.getOWLLiteral(node.getLiteral(), node.getLang());
        }
        else {
            obj = dataFactory.getOWLLiteral(node.getLiteral(), dt);
        }
    }


    public void visit(OWLDataProperty property) {
        IRI iri = getIRI(property);
        obj = dataFactory.getOWLDataProperty(iri);
    }


    public void visit(OWLObjectProperty property) {
        IRI iri = getIRI(property);
        obj = dataFactory.getOWLObjectProperty(iri);
    }


    public void visit(OWLObjectInverseOf property) {
        property.getInverse().accept(this);
        OWLObjectPropertyExpression prop = (OWLObjectPropertyExpression) obj;
        obj = dataFactory.getOWLObjectInverseOf(prop);
    }

    public void visit(OWLNamedIndividual individual) {
        IRI iri = getIRI(individual);
        obj = dataFactory.getOWLNamedIndividual(iri);
    }


    public void visit(OWLOntology ontology) {
        // Should we duplicate ontologies here? Probably not.
        obj = ontology;
    }


    public void visit(SWRLRule rule) {
        Set<SWRLAtom> antecedents = new HashSet<SWRLAtom>();
        Set<SWRLAtom> consequents = new HashSet<SWRLAtom>();
        for (SWRLAtom atom : rule.getBody()) {
            atom.accept(this);
            antecedents.add((SWRLAtom) obj);
        }
        for (SWRLAtom atom : rule.getHead()) {
            atom.accept(this);
            consequents.add((SWRLAtom) obj);
        }
        obj = dataFactory.getSWRLRule(antecedents, consequents);
    }


    public void visit(SWRLClassAtom node) {
        node.getPredicate().accept(this);
        OWLClassExpression desc = (OWLClassExpression) obj;
        node.getArgument().accept(this);
        SWRLIArgument atom = (SWRLIArgument) obj;
        obj = dataFactory.getSWRLClassAtom(desc, atom);
    }


    public void visit(SWRLDataRangeAtom node) {
        node.getPredicate().accept(this);
        OWLDataRange rng = (OWLDataRange) obj;
        node.getArgument().accept(this);
        SWRLDArgument atom = (SWRLDArgument) obj;
        obj = dataFactory.getSWRLDataRangeAtom(rng, atom);
    }


    public void visit(SWRLObjectPropertyAtom node) {
        node.getPredicate().accept(this);
        OWLObjectPropertyExpression exp = (OWLObjectPropertyExpression) obj;
        node.getFirstArgument().accept(this);
        SWRLIArgument arg0 = (SWRLIArgument) obj;
        node.getSecondArgument().accept(this);
        SWRLIArgument arg1 = (SWRLIArgument) obj;
        obj = dataFactory.getSWRLObjectPropertyAtom(exp, arg0, arg1);
    }


    public void visit(SWRLDataPropertyAtom node) {
        node.getPredicate().accept(this);
        OWLDataPropertyExpression exp = (OWLDataPropertyExpression) obj;
        node.getFirstArgument().accept(this);
        SWRLIArgument arg0 = (SWRLIArgument) obj;
        node.getSecondArgument().accept(this);
        SWRLDArgument arg1 = (SWRLDArgument) obj;
        obj = dataFactory.getSWRLDataPropertyAtom(exp, arg0, arg1);
    }


    public void visit(SWRLBuiltInAtom node) {
        List<SWRLDArgument> atomObjects = new ArrayList<SWRLDArgument>();
        for (SWRLDArgument atomObject : node.getArguments()) {
            atomObject.accept(this);
            atomObjects.add((SWRLDArgument) obj);
        }
        obj = dataFactory.getSWRLBuiltInAtom(node.getPredicate(), atomObjects);
    }


    public void visit(SWRLDifferentIndividualsAtom node) {
        node.getFirstArgument().accept(this);
        SWRLIArgument arg0 = (SWRLIArgument) obj;
        node.getSecondArgument().accept(this);
        SWRLIArgument arg1 = (SWRLIArgument) obj;
        obj = dataFactory.getSWRLDifferentIndividualsAtom(arg0, arg1);
    }


    public void visit(SWRLSameIndividualAtom node) {
        node.getFirstArgument().accept(this);
        SWRLIArgument arg0 = (SWRLIArgument) obj;
        node.getSecondArgument().accept(this);
        SWRLIArgument arg1 = (SWRLIArgument) obj;
        obj = dataFactory.getSWRLSameIndividualAtom(arg0, arg1);
    }

    public void visit(SWRLVariable variable) {
        variable.getIRI().accept(this);
        IRI iri = (IRI) obj;
        obj = dataFactory.getSWRLVariable(iri);
    }

    public void visit(SWRLIndividualArgument node) {
        node.getIndividual().accept(this);
        OWLIndividual ind = (OWLIndividual) obj;
        obj = dataFactory.getSWRLIndividualArgument(ind);
    }


    public void visit(SWRLLiteralArgument node) {
        node.getLiteral().accept(this);
        OWLLiteral con = (OWLLiteral) obj;
        obj = dataFactory.getSWRLLiteralArgument(con);
    }

    public void visit(OWLHasKeyAxiom axiom) {
        axiom.getClassExpression().accept(this);
        OWLClassExpression ce = (OWLClassExpression) obj;
        Set<OWLPropertyExpression<?,?>> props = duplicateSet(axiom.getPropertyExpressions());
        obj = dataFactory.getOWLHasKeyAxiom(ce, props, duplicateAxiomAnnotations(axiom));
    }

    public void visit(OWLDataIntersectionOf node) {
        Set<OWLDataRange> ranges = duplicateSet(node.getOperands());
        obj = dataFactory.getOWLDataIntersectionOf(ranges);
    }

    public void visit(OWLDataUnionOf node) {
        Set<OWLDataRange> ranges = duplicateSet(node.getOperands());
        obj = dataFactory.getOWLDataUnionOf(ranges);
    }


    public void visit(OWLAnnotationProperty property) {
        obj = dataFactory.getOWLAnnotationProperty(getIRI(property));
    }

    public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
        axiom.getProperty().accept(this);
        OWLAnnotationProperty prop = (OWLAnnotationProperty) obj;
        axiom.getDomain().accept(this);
        IRI domain = (IRI) obj;
        obj = dataFactory.getOWLAnnotationPropertyDomainAxiom(prop, domain, duplicateAxiomAnnotations(axiom));
    }

    public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
        axiom.getProperty().accept(this);
        OWLAnnotationProperty prop = (OWLAnnotationProperty) obj;
        axiom.getRange().accept(this);
        IRI range = (IRI) obj;
        obj = dataFactory.getOWLAnnotationPropertyRangeAxiom(prop, range, duplicateAxiomAnnotations(axiom));
    }

    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        axiom.getSubProperty().accept(this);
        OWLAnnotationProperty sub = (OWLAnnotationProperty) obj;
        axiom.getSuperProperty().accept(this);
        OWLAnnotationProperty sup = (OWLAnnotationProperty) obj;
        obj = dataFactory.getOWLSubAnnotationPropertyOfAxiom(sub, sup, duplicateAxiomAnnotations(axiom));
    }


    public void visit(OWLAnnotation node) {
        node.getProperty().accept(this);
        OWLAnnotationProperty prop = (OWLAnnotationProperty) obj;
        node.getValue().accept(this);
        OWLAnnotationValue val = (OWLAnnotationValue) obj;
        obj = dataFactory.getOWLAnnotation(prop, val);
    }


    public void visit(OWLAnonymousIndividual individual) {
        obj = individual;
    }

    public void visit(IRI iri) {
        for(OWLEntity entity : replacementMap.keySet()) {
            if(entity.getIRI().equals(iri)) {
                obj = replacementMap.get(entity);
                return;
            }
        }
        obj = iri;
    }


    public void visit(OWLDatatypeDefinitionAxiom axiom) {
        axiom.getDatatype().accept(this);
        OWLDatatype dt = (OWLDatatype) obj;
        axiom.getDataRange().accept(this);
        OWLDataRange rng = (OWLDataRange) obj;
        obj = dataFactory.getOWLDatatypeDefinitionAxiom(dt, rng, duplicateAxiomAnnotations(axiom));
    }


    /**
     * A utility function that duplicates a set of objects.
     *
     * @param objects The set of object to be duplicated
     * @return The set of duplicated objects
     */
    @SuppressWarnings("unchecked")
    private <O extends OWLObject> Set<O> duplicateSet(Set<O> objects) {
        Set<O> dup = new HashSet<O>();
        for (O o : objects) {
            o.accept(this);
            dup.add((O) obj);
        }
        return dup;
    }
}
