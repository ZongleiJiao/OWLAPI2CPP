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

package org.coode.owlapi.obo.renderer;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.owlapi.obo.parser.OBOVocabulary;
import org.semanticweb.owlapi.io.AbstractOWLRenderer;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.util.NamespaceUtil;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.VersionInfo;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * Author: Nick Drummond<br>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 17, 2008<br><br>
 * Renders OBO 1.2 flat file format.
 * OBO 1.2 is a subset of OWL, so a rendering of an arbitrary OWL ontology may be incomplete in OBO.
 * Several features are currently not implemented:
 * - Exception handling for unsupported constructs - these are currently reported in stderr
 * - axiom annotations (these might be serialisable as inline comments - although OBO parsers provide no
 * guarantees these will be roundtripped)
 * - anonymous classes/properties - it is not clear what this means in OBO
 * - datatype restrictions
 * - namespace and derived tags for relationships etc - it is not clear what this means in OBO
 * - preservation of ordering on roundtripping exercises
 * - Stanzas are ordered (classes, obj/data props then instances)
 * - Entities are ordered by ID in each Stanza type
 * - OBO tags are ordered WRT the OBO specifications
 * Additional points:
 * - cardinality is expressed as an '=' separated tag value pair in [1] and underspecified in [2]. OBOEdit 2
 * does not parse this (at the time of writing) so this has been changed to standard TVP syntax
 * - OBO 1.2 currently specifies pairwise disjointness which this renderer follows so files may get large
 * - Exceptions are caught along the way and then wrapped in an OBOStorageIncompleteException which is thrown
 * after serialisation ends
 * References:
 * [1] http://www.cs.man.ac.uk/~horrocks/obo/
 * [2] http://www.geneontology.org/GO.format.obo-1_2.shtml
 */
public class OBOFlatFileRenderer extends AbstractOWLRenderer implements OBOExceptionHandler {

    private OBORelationshipGenerator relationshipHandler;

    private SimpleShortFormProvider sfp;

    private String defaultPrefix;

    private List<OBOStorageException> exceptions = new ArrayList<OBOStorageException>();

    private NamespaceUtil nsUtil;

    private String defaultNamespace;


    protected OBOFlatFileRenderer(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        relationshipHandler = new OBORelationshipGenerator(this);
        sfp = new SimpleShortFormProvider();
    }


    @Override
	public void render(OWLOntology ontology, Writer writer) throws OWLRendererException {
        exceptions.clear();

        IRI ontologyIRI = ontology.getOntologyID().getOntologyIRI();
		if (ontologyIRI != null) {
			final String ontURIStr = ontologyIRI.toString();
			if (ontURIStr.endsWith("/")) {
				defaultNamespace = ontURIStr;
			} else {
				defaultNamespace = ontURIStr + "#";
			}
		} else {
			defaultNamespace = "urn:defaultOBONamespace:ontology"
					+ System.currentTimeMillis() + "#";
			System.err.println("WARNING: anonymous ontology saved in OBO format. Default namespace created for it.");
		}

        nsUtil = new NamespaceUtil();
        defaultPrefix = nsUtil.getPrefix(defaultNamespace);

        writeHeader(ontology, writer);
        writeStanzas(ontology, writer);

        if (!exceptions.isEmpty()) {
            throw new OBOStorageIncompleteException(exceptions);
        }
    }


    public void addException(OBOStorageException exception) {
        exceptions.add(exception);
    }


    public List<OBOStorageException> getExceptions() {
        return exceptions;
    }


    private void writeHeader(OWLOntology ontology, Writer writer) throws OWLRendererException {
        OBOTagValuePairList tvpList = new OBOTagValuePairList(OBOVocabulary.getHeaderTags());

        tvpList.setDefault(OBOVocabulary.DEFAULT_NAMESPACE, defaultPrefix);

        for (OWLAnnotation ax : ontology.getAnnotations()) {
            if (ax.getProperty().isComment()) {
                tvpList.addPair(OBOVocabulary.REMARK, ((OWLLiteral) ax.getValue()).getLiteral());
            }
            else {
                tvpList.visit(ax);
            }
        }

        for (OWLImportsDeclaration importDecl : ontology.getImportsDeclarations()) {
            tvpList.addPair(OBOVocabulary.IMPORT, importDecl.getIRI().toString());
        }

        Map<String, String> namespace2PrefixMap = loadUsedNamespaces(ontology);
        for (String namespace : namespace2PrefixMap.keySet()) {
            String mapping = namespace2PrefixMap.get(namespace) + " " + namespace;
            tvpList.addPair(OBOVocabulary.ID_SPACE, mapping);
        }

        // overwrite the existing values for below
        tvpList.setPair(OBOVocabulary.FORMAT_VERSION, "1.2");
        tvpList.setPair(OBOVocabulary.DATE, getTimestampFormatter().format(new Date(System.currentTimeMillis())));
        tvpList.setPair(OBOVocabulary.SAVED_BY, System.getProperty("user.name"));
        tvpList.setPair(OBOVocabulary.AUTO_GENERATED_BY, VersionInfo.getVersionInfo().toString());

        tvpList.write(writer);
    }


    private Map<String, String> loadUsedNamespaces(OWLOntology ontology) {
        for (OWLEntity entity : ontology.getSignature()) {
            String[] pair = new String[2];
            nsUtil.split(entity.getIRI().toString(), pair);
            final IRI base = IRI.create(pair[0]);
            nsUtil.getPrefix(base.toString());
        }
        return nsUtil.getNamespace2PrefixMap();
    }


    private void writeStanzas(OWLOntology ontology, Writer writer) {

        write("\n\n! ----------------------  CLASSES  -------------------------\n", writer);

        final List<OWLClass> sortedClasses = new ArrayList<OWLClass>(ontology.getClassesInSignature());
        Collections.sort(sortedClasses);
        for (OWLClass cls : sortedClasses) {
            writeTermStanza(cls, ontology, writer);
        }


        write("\n\n! ----------------------  PROPERTIES  -------------------------\n", writer);

        final List<OWLObjectProperty> objProps = new ArrayList<OWLObjectProperty>(ontology.getObjectPropertiesInSignature());
        Collections.sort(objProps);
        for (OWLObjectProperty property : objProps) {
            writeTypeDefStanza(property, ontology, writer);
        }

        final List<OWLDataProperty> dataProps = new ArrayList<OWLDataProperty>(ontology.getDataPropertiesInSignature());
        Collections.sort(dataProps);
        for (OWLDataProperty property : dataProps) {
            writeTypeDefStanza(property, ontology, writer);
        }


        write("\n\n! ----------------------  INSTANCES  -------------------------\n", writer);

        final List<OWLNamedIndividual> individuals = new ArrayList<OWLNamedIndividual>(ontology.getIndividualsInSignature());
        Collections.sort(individuals);
        for (OWLNamedIndividual individual : individuals) {
            writeInstanceStanza(individual, ontology, writer);
        }

        // scan ontology for other constructs that cannot be translated

        for (OWLClassAxiom ax : ontology.getGeneralClassAxioms()) {
            if (ax instanceof OWLSubClassOfAxiom) {
                exceptions.add(new OBOStorageException(ax, null, "Superclass GCI found in ontology cannot be translated to OBO"));
            }
            if (ax instanceof OWLEquivalentClassesAxiom) {
                exceptions.add(new OBOStorageException(ax, null, "Equivalent class GCI found in ontology cannot be translated to OBO"));
            }
            if (ax instanceof OWLDisjointClassesAxiom) {
                exceptions.add(new OBOStorageException(ax, null, "Disjoint axiom contains anonymous classes - cannot be translated to OBO"));
            }
        }


        for (SWRLRule r : ontology.getAxioms(AxiomType.SWRL_RULE)) {
            exceptions.add(new OBOStorageException(r, null, "SWRL rules cannot be translated to OBO"));
        }

    }


    private void writeTermStanza(OWLClass cls, OWLOntology ontology, Writer writer) {
        write("\n[", writer);
        write(OBOVocabulary.TERM.getName(), writer);
        write("]\n", writer);

        // TODO is_anonymous??

        OBOTagValuePairList tvpList = new OBOTagValuePairList(OBOVocabulary.getTermStanzaTags());

        handleEntityBase(cls, ontology, tvpList);

        relationshipHandler.setClass(cls);

        for (OWLClassExpression superCls : cls.getSuperClasses(ontology)) {
            if (!superCls.isAnonymous()) {
                String superclassID = getID(superCls.asOWLClass());
                tvpList.addPair(OBOVocabulary.IS_A, superclassID);
                // TODO handle namespace and derived?
            }
            else if (superCls instanceof OWLRestriction) {
                // TODO handle datatype restrictions
                superCls.accept(relationshipHandler);
            }
            else {
                exceptions.add(new OBOStorageException(cls, superCls, "OBO format only supports named superclass or someValuesFrom restrictions"));
            }
        }

        final OWLClass owlThing = getOWLOntologyManager().getOWLDataFactory().getOWLThing();

        // if no named superclass is specified, then this must be asserted to be a subclass of owlapi:Thing
        if (!cls.equals(owlThing) && tvpList.getValues(OBOVocabulary.IS_A).isEmpty()) {
            tvpList.addPair(OBOVocabulary.IS_A, getID(owlThing));
        }

        for (OWLClassExpression equiv : cls.getEquivalentClasses(ontology)) {
            if (equiv instanceof OWLObjectIntersectionOf) {
                handleIntersection(cls, (OWLObjectIntersectionOf) equiv, tvpList);
            }
            else if (equiv instanceof OWLObjectUnionOf) {
                handleUnion(cls, (OWLObjectUnionOf) equiv, tvpList);
            }
            else if (equiv instanceof OWLRestriction) {
                /* OBO equivalence must be of the form "A and p some B and ..."
                 * if this class is equiv to a restriction, put this into an intersection with owlapi:Thing as the named class
                 */
                OWLObjectIntersectionOf intersection = getOWLOntologyManager().getOWLDataFactory().getOWLObjectIntersectionOf(owlThing, equiv);
                handleIntersection(cls, intersection, tvpList);
            }
            else {
                // TODO handle datatype restrictions
                exceptions.add(new OBOStorageException(cls, equiv, "Cannot answer equivalent class that is not intersection or union"));
            }
        }

        for (OWLClassExpression disjoint : cls.getDisjointClasses(ontology)) {
            if (!disjoint.isAnonymous()) {
                tvpList.addPair(OBOVocabulary.DISJOINT_FROM, getID(disjoint.asOWLClass()));
                // TODO handle namespace and derived
            }
            else {
                exceptions.add(new OBOStorageException(cls, disjoint, "Found anonymous disjoint class that cannot be represented in OBO"));
            }
        }

        for (OBORelationship relationship : relationshipHandler.getOBORelationships()) {
            // TODO handle datatype restrictions
            handleRelationship(relationship, tvpList);
        }

        tvpList.write(writer);
    }


    private void handleIntersection(OWLClass cls, OWLObjectIntersectionOf intersectionOf, OBOTagValuePairList tvpList) {
        for (OWLClassExpression op : intersectionOf.getOperands()) {
            if (!op.isAnonymous()) {
                tvpList.addPair(OBOVocabulary.INTERSECTION_OF, getID(op.asOWLClass()));
            }
            else {
                relationshipHandler.setClass(cls);
                op.accept(relationshipHandler);
                Set<OBORelationship> relations = relationshipHandler.getOBORelationships();
                if (!relations.isEmpty()) {
                    OBORelationship rel = relations.iterator().next();
                    tvpList.addPair(OBOVocabulary.INTERSECTION_OF, renderRestriction(rel));
                }
                else {
                    exceptions.add(new OBOStorageException(cls, op, "Found operand in intersection that cannot be represented"));
                }
            }
        }
        // TODO handle namespace
    }


    private String renderRestriction(OBORelationship rel) {
        StringBuilder sb = new StringBuilder(getID(rel.getProperty()));
        sb.append(" ");
        sb.append(getID(rel.getFiller()));
        return sb.toString();
    }


    private void handleUnion(OWLClass cls, OWLObjectUnionOf union, OBOTagValuePairList tvpList) {
        for (OWLClassExpression op : union.getOperands()) {
            if (!op.isAnonymous()) {
                tvpList.addPair(OBOVocabulary.UNION_OF, getID(op.asOWLClass()));
            }
            else {
                relationshipHandler.setClass(cls);
                op.accept(relationshipHandler);
                Set<OBORelationship> relations = relationshipHandler.getOBORelationships();
                if (!relations.isEmpty()) {
                    OBORelationship rel = relations.iterator().next();
                    tvpList.addPair(OBOVocabulary.UNION_OF, renderRestriction(rel));
                }
                else {
                    exceptions.add(new OBOStorageException(cls, op, "Found operand in union that cannot be represented"));
                }
            }
        }
    }


    private void handleRelationship(OBORelationship relationship, OBOTagValuePairList tvpList) {
        StringBuilder sb = new StringBuilder();
        sb.append(renderRestriction(relationship));
        sb.append("\n");

        if (relationship.getCardinality() >= 0) {
            sb.append(OBOVocabulary.CARDINALITY.getName());
            sb.append(":");
            sb.append(Integer.toString(relationship.getCardinality()));
            sb.append("\n");
        }
        if (relationship.getMaxCardinality() >= 0) {
            sb.append(OBOVocabulary.MAX_CARDINALITY.getName());
            sb.append(":");
            sb.append(Integer.toString(relationship.getMaxCardinality()));
            sb.append("\n");
        }
        if (relationship.getMinCardinality() >= 0) {
            sb.append(OBOVocabulary.MIN_CARDINALITY.getName());
            sb.append(":");
            sb.append(Integer.toString(relationship.getMinCardinality()));
            sb.append("\n");
        }
        tvpList.addPair(OBOVocabulary.RELATIONSHIP, sb.toString());
    }


    private <P extends OWLProperty> OBOTagValuePairList handleCommonTypeDefStanza(P property, OWLOntology ontology, Writer writer) {
        write("\n[", writer);
        write(OBOVocabulary.TYPEDEF.getName(), writer);
        write("]\n", writer);

        OBOTagValuePairList tvpList = new OBOTagValuePairList(OBOVocabulary.getTypeDefStanzaTags());
        handleEntityBase(property, ontology, tvpList);

        Set<OWLClassExpression> domains = property.getDomains(ontology);
        for (OWLClassExpression domain : domains) {
            if (!domain.isAnonymous()) {
                tvpList.addPair(OBOVocabulary.DOMAIN, getID(domain.asOWLClass()));
            }
            else {
                exceptions.add(new OBOStorageException(property, domain, "Anonymous domain that cannot be represented in OBO"));
            }
        }

        final Set<OWLPropertyExpression<?,?>> sp = property.getSuperProperties(ontology);
        for (OWLPropertyExpression<?,?> superProp : sp) {
            if (!superProp.isAnonymous()) {
                tvpList.addPair(OBOVocabulary.IS_A, getID((OWLProperty) superProp));
            }
            else {
                exceptions.add(new OBOStorageException(property, superProp, "Anonymous property in superProperty is not supported in OBO"));
            }
        }

        tvpList.setDefault(OBOVocabulary.IS_METADATA_TAG, "false"); // annotation properties only

        return tvpList;
    }


    private void writeTypeDefStanza(OWLObjectProperty property, OWLOntology ontology, Writer writer) {

        OBOTagValuePairList tvpList = handleCommonTypeDefStanza(property, ontology, writer);

        for (OWLClassExpression range : property.getRanges(ontology)) {
            if (!range.isAnonymous()) {
                tvpList.addPair(OBOVocabulary.RANGE, getID(range.asOWLClass()));
            }
            else {
                exceptions.add(new OBOStorageException(property, range, "Anonymous range that cannot be represented in OBO"));
            }
        }

        if (property.isAsymmetric(ontology)) {
            tvpList.addPair(OBOVocabulary.IS_ASYMMETRIC, "true");
        }
        if (property.isReflexive(ontology)) {
            tvpList.addPair(OBOVocabulary.IS_REFLEXIVE, "true");
        }
        if (property.isSymmetric(ontology)) {
            tvpList.addPair(OBOVocabulary.IS_SYMMETRIC, "true");
        }
        if (property.isTransitive(ontology)) {
            tvpList.addPair(OBOVocabulary.IS_TRANSITIVE, "true");
        }

        for (OWLObjectPropertyExpression inv : property.getInverses(ontology)) {
            if (!inv.isAnonymous()) {
                tvpList.addPair(OBOVocabulary.INVERSE, getID(inv.asOWLObjectProperty()));
            }
            else {
                exceptions.add(new OBOStorageException(property, inv, "Anonymous property in inverse is not supported in OBO"));
            }
        }

        // transitive over
        for (OWLSubPropertyChainOfAxiom ax : ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF)) {
            if (ax.getSuperProperty().equals(property)) {
                final List<OWLObjectPropertyExpression> chain = ax.getPropertyChain();
                if (chain.size() == 2 && chain.get(0).equals(property) && !chain.get(1).isAnonymous()) {
                    tvpList.addPair(OBOVocabulary.TRANSITIVE_OVER, getID(chain.get(1).asOWLObjectProperty()));
                }
                else {
                    exceptions.add(new OBOStorageException(property, ax, "Only property chains of form 'p o q -> p' supported"));
                }
            }
        }

        // TODO is RELATIONSHIP really a part of the typedef stanza?

        tvpList.write(writer);
    }


    private void writeTypeDefStanza(OWLDataProperty property, OWLOntology ontology, Writer writer) {

        OBOTagValuePairList tvpList = handleCommonTypeDefStanza(property, ontology, writer);

        for (OWLDataRange range : property.getRanges(ontology)) {
            if (range.isDatatype()) {
                tvpList.addPair(OBOVocabulary.RANGE, range.asOWLDatatype().getIRI().toString());
            }
            else {
                exceptions.add(new OBOStorageException(property, range, "Complex data range cannot be represented in OBO"));
            }
        }

        // TODO is RELATIONSHIP really a part of the typedef stanza?

        tvpList.write(writer);
    }

    private void writeInstanceStanza(OWLNamedIndividual individual, OWLOntology ontology, Writer writer) {
        write("\n[", writer);
        write(OBOVocabulary.INSTANCE.getName(), writer);
        write("]\n", writer);

        OBOTagValuePairList tvpList = new OBOTagValuePairList(OBOVocabulary.getInstanceStanzaTags());
        if (individual.isAnonymous()) {
            tvpList.setDefault(OBOVocabulary.IS_ANONYMOUS, "true");
        }
        handleEntityBase(individual, ontology, tvpList);

        for (OWLClassExpression type : individual.getTypes(ontology)) {
            if (!type.isAnonymous()) {
                tvpList.addPair(OBOVocabulary.INSTANCE_OF, getID(type.asOWLClass()));
            }
            else {
                exceptions.add(new OBOStorageException(individual, type, "Complex types cannot be represented in OBO"));
            }
        }

        Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objPropAssertions = individual.getObjectPropertyValues(ontology);
        for (OWLObjectPropertyExpression p : objPropAssertions.keySet()) {
            if (!p.isAnonymous()) {
                for (OWLIndividual ind : objPropAssertions.get(p)) {
                    if (!ind.isAnonymous()) {
                        final String rel = renderRestriction(new OBORelationship(p.asOWLObjectProperty(), ind.asOWLNamedIndividual()));
                        tvpList.addPair(OBOVocabulary.PROPERTY_VALUE, rel);
                    }
                }
            }
            else {
                exceptions.add(new OBOStorageException(individual, p, "Anonymous property in assertion is not supported in OBO"));
            }
        }

        Map<OWLDataPropertyExpression, Set<OWLLiteral>> dataPropAssertions = individual.getDataPropertyValues(ontology);
        for (OWLDataPropertyExpression p : dataPropAssertions.keySet()) {
            if (!p.isAnonymous()) {
                for (OWLLiteral literal : dataPropAssertions.get(p)) {
                    final String rel = renderPropertyAssertion(p.asOWLDataProperty(), literal);
                    tvpList.addPair(OBOVocabulary.PROPERTY_VALUE, rel);
                }
            }
            else {
                // no anonymous data properties so this should never occur
                exceptions.add(new OBOStorageException(individual, p, "Anonymous property in assertion is not supported in OBO"));
            }
        }

        tvpList.write(writer);
    }


    private void handleEntityBase(OWLEntity entity, OWLOntology ontology, OBOTagValuePairList tvpList) {
        tvpList.addPair(OBOVocabulary.ID, getID(entity));
        Set<OWLAnnotation> potentialNames = new HashSet<OWLAnnotation>();
        for (OWLAnnotation annotation : entity.getAnnotations(ontology)) {
            if (annotation.getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI())) {
                potentialNames.add(annotation);
            }
            else if (annotation.getProperty().isComment()) {
                tvpList.addPair(OBOVocabulary.COMMENT, ((OWLLiteral) annotation.getValue()).getLiteral());
            }
            else {
                tvpList.visit(annotation);
            }
        }

        if (tvpList.getValues(OBOVocabulary.NAME).isEmpty()) { // one name required!!
            if (!potentialNames.isEmpty()) {
                OWLAnnotation firstLabel = potentialNames.iterator().next();
                tvpList.addPair(OBOVocabulary.NAME, ((OWLLiteral) firstLabel.getValue()).getLiteral());
                potentialNames.remove(firstLabel);
            }
            else {
                tvpList.addPair(OBOVocabulary.NAME, getID(entity));
            }
        }

        // other labels just get rendered as label
        for (OWLAnnotation anno : potentialNames) {
            tvpList.visit(anno);
        }

        final String uri = entity.getIRI().toString();
        if (!uri.startsWith(defaultNamespace)) {
            String[] pair = new String[2];
            nsUtil.split(uri, pair);
            final IRI base = IRI.create(pair[0]);
            String prefix = nsUtil.getPrefix(base.toString());
            tvpList.setDefault(OBOVocabulary.NAMESPACE, prefix);
        }
    }


    private String renderPropertyAssertion(OWLDataProperty property, OWLLiteral literal) {
        StringBuilder sb = new StringBuilder(getID(property));
        sb.append(" \"");
        sb.append(literal.getLiteral());
        sb.append("\" ");
        if (!literal.isRDFPlainLiteral()) {
            sb.append(literal.getDatatype().getIRI());
        }
        return sb.toString();
    }


    private String getID(OWLEntity entity) {
        return sfp.getShortForm(entity);
    }


    private void write(String s, Writer writer) {
        try {
            writer.write(s);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private DateFormat getTimestampFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd:MM:yyyy HH:mm");
        return sdf;
    }
}
