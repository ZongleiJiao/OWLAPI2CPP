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

package org.semanticweb.owlapi.vocab;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Oct-2006<br><br>
 */
public enum OWLRDFVocabulary {

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // OWL Vocab
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    OWL_THING(Namespaces.OWL, "Thing"),

    OWL_NOTHING(Namespaces.OWL, "Nothing"),

    OWL_CLASS(Namespaces.OWL, "Class"),

    OWL_ONTOLOGY(Namespaces.OWL, "Ontology"),

    @Deprecated
    OWL_ONTOLOGY_PROPERTY(Namespaces.OWL, "OntologyProperty"),

    OWL_IMPORTS(Namespaces.OWL, "imports"),

    OWL_VERSION_IRI(Namespaces.OWL, "versionIRI"),

    OWL_VERSION_INFO(Namespaces.OWL, "versionInfo"),

    OWL_EQUIVALENT_CLASS(Namespaces.OWL, "equivalentClass"),

    OWL_OBJECT_PROPERTY(Namespaces.OWL, "ObjectProperty"),

    OWL_DATA_PROPERTY(Namespaces.OWL, "DatatypeProperty"),

    OWL_FUNCTIONAL_PROPERTY(Namespaces.OWL, "FunctionalProperty"),

    OWL_INVERSE_FUNCTIONAL_PROPERTY(Namespaces.OWL, "InverseFunctionalProperty"),

    @Deprecated
    OWL_ANTI_SYMMETRIC_PROPERTY(Namespaces.OWL, "AntisymmetricProperty"),

    OWL_ASYMMETRIC_PROPERTY(Namespaces.OWL, "AsymmetricProperty"),

    OWL_SYMMETRIC_PROPERTY(Namespaces.OWL, "SymmetricProperty"),

    OWL_RESTRICTION(Namespaces.OWL, "Restriction"),

    @Deprecated
    OWL_DATA_RESTRICTION(Namespaces.OWL, "DataRestriction"),

    @Deprecated
    OWL_OBJECT_RESTRICTION(Namespaces.OWL, "ObjectRestriction"),

    OWL_ON_PROPERTY(Namespaces.OWL, "onProperty"),

    OWL_INTERSECTION_OF(Namespaces.OWL, "intersectionOf"),

    OWL_UNION_OF(Namespaces.OWL, "unionOf"),

    OWL_ALL_VALUES_FROM(Namespaces.OWL, "allValuesFrom"),

    OWL_SOME_VALUES_FROM(Namespaces.OWL, "someValuesFrom"),

    OWL_HAS_VALUE(Namespaces.OWL, "hasValue"),

    OWL_DISJOINT_WITH(Namespaces.OWL, "disjointWith"),

    OWL_ONE_OF(Namespaces.OWL, "oneOf"),

    @Deprecated
    OWL_SELF_RESTRICTION(Namespaces.OWL, "SelfRestriction"),

    OWL_HAS_SELF(Namespaces.OWL, "hasSelf"),

    OWL_DISJOINT_UNION_OF(Namespaces.OWL, "disjointUnionOf"),

    OWL_MIN_CARDINALITY(Namespaces.OWL, "minCardinality"),

    OWL_MIN_QUALIFIED_CARDINALITY(Namespaces.OWL,  "minQualifiedCardinality"),

    OWL_CARDINALITY(Namespaces.OWL, "cardinality"),

    OWL_QUALIFIED_CARDINALITY(Namespaces.OWL, "qualifiedCardinality"),

    OWL_ANNOTATION_PROPERTY(Namespaces.OWL, "AnnotationProperty"),

    OWL_ANNOTATION(Namespaces.OWL, "Annotation"),

    @Deprecated
    OWL_DECLARED_AS(Namespaces.OWL, "declaredAs"),

    OWL_INDIVIDUAL(Namespaces.OWL, "Individual"),

    OWL_NAMED_INDIVIDUAL(Namespaces.OWL, "NamedIndividual"),

    OWL_DATATYPE(Namespaces.OWL, "Datatype"),

    RDFS_SUBCLASS_OF(Namespaces.RDFS, "subClassOf"),

    RDFS_SUB_PROPERTY_OF(Namespaces.RDFS, "subPropertyOf"),

    RDF_TYPE(Namespaces.RDF, "type"),

    RDF_NIL(Namespaces.RDF, "nil"),

    RDF_REST(Namespaces.RDF, "rest"),

    RDF_FIRST(Namespaces.RDF, "first"),

    RDF_LIST(Namespaces.RDF, "List"),

    OWL_MAX_CARDINALITY(Namespaces.OWL, "maxCardinality"),

    OWL_MAX_QUALIFIED_CARDINALITY(Namespaces.OWL, "maxQualifiedCardinality"),

    @Deprecated
    OWL_NEGATIVE_OBJECT_PROPERTY_ASSERTION(
            Namespaces.OWL, "NegativeObjectPropertyAssertion"),

    @Deprecated
    OWL_NEGATIVE_DATA_PROPERTY_ASSERTION(
            Namespaces.OWL, "NegativeDataPropertyAssertion"),


    OWL_NEGATIVE_PROPERTY_ASSERTION(Namespaces.OWL, "NegativePropertyAssertion"),

    RDFS_LABEL(Namespaces.RDFS, "label"),

    RDFS_COMMENT(Namespaces.RDFS, "comment"),

    RDFS_SEE_ALSO(Namespaces.RDFS, "seeAlso"),

    RDFS_IS_DEFINED_BY(Namespaces.RDFS, "isDefinedBy"),

    RDFS_RESOURCE(Namespaces.RDFS, "Resource"),

    RDFS_LITERAL(Namespaces.RDFS, "Literal"),

    RDF_PLAIN_LITERAL(Namespaces.RDF, "PlainLiteral"),

    RDFS_DATATYPE(Namespaces.RDFS, "Datatype"),

    OWL_TRANSITIVE_PROPERTY(Namespaces.OWL, "TransitiveProperty"),

    OWL_REFLEXIVE_PROPERTY(Namespaces.OWL, "ReflexiveProperty"),

    OWL_IRREFLEXIVE_PROPERTY(Namespaces.OWL, "IrreflexiveProperty"),

    OWL_INVERSE_OF(Namespaces.OWL, "inverseOf"),

    OWL_COMPLEMENT_OF(Namespaces.OWL, "complementOf"),

    OWL_DATATYPE_COMPLEMENT_OF(Namespaces.OWL, "datatypeComplementOf"),

    OWL_ALL_DIFFERENT(Namespaces.OWL, "AllDifferent"),

    OWL_DISTINCT_MEMBERS(Namespaces.OWL, "distinctMembers"),

    OWL_SAME_AS(Namespaces.OWL, "sameAs"),

    OWL_DIFFERENT_FROM(Namespaces.OWL, "differentFrom"),

    OWL_DEPRECATED_PROPERTY(Namespaces.OWL, "DeprecatedProperty"),

    OWL_EQUIVALENT_PROPERTY(Namespaces.OWL, "equivalentProperty"),

    OWL_DEPRECATED_CLASS(Namespaces.OWL, "DeprecatedClass"),

    OWL_DATA_RANGE(Namespaces.OWL, "DataRange"),

    RDFS_DOMAIN(Namespaces.RDFS, "domain"),

    RDFS_RANGE(Namespaces.RDFS, "range"),

    RDFS_CLASS(Namespaces.RDFS, "Class"),

    RDF_PROPERTY(Namespaces.RDF, "Property"),

    @Deprecated
    RDF_SUBJECT(Namespaces.RDF, "subject"),

    @Deprecated
    RDF_PREDICATE(Namespaces.RDF, "predicate"),

    @Deprecated
    RDF_OBJECT(Namespaces.RDF, "object"),

    @Deprecated
    OWL_SUBJECT(Namespaces.OWL, "subject"),

    @Deprecated
    OWL_PREDICATE(Namespaces.OWL, "predicate"),

    @Deprecated
    OWL_OBJECT(Namespaces.OWL, "object"),

    RDF_DESCRIPTION(Namespaces.RDF, "Description"),

    RDF_XML_LITERAL(Namespaces.RDF, "XMLLiteral"),

    OWL_PRIOR_VERSION(Namespaces.OWL, "priorVersion"),

    OWL_DEPRECATED(Namespaces.OWL, "deprecated"),

    OWL_BACKWARD_COMPATIBLE_WITH(Namespaces.OWL, "backwardCompatibleWith"),

    OWL_INCOMPATIBLE_WITH(Namespaces.OWL, "incompatibleWith"),

    OWL_OBJECT_PROPERTY_DOMAIN(Namespaces.OWL, "objectPropertyDomain"),

    @Deprecated
    OWL_DATA_PROPERTY_DOMAIN(Namespaces.OWL, "dataPropertyDomain"),

    @Deprecated
    OWL_DATA_PROPERTY_RANGE(Namespaces.OWL, "dataPropertyRange"),

    @Deprecated
    OWL_OBJECT_PROPERTY_RANGE(Namespaces.OWL, "objectPropertyRange"),

    @Deprecated
    OWL_SUB_OBJECT_PROPERTY_OF(Namespaces.OWL, "subObjectPropertyOf"),

    @Deprecated
    OWL_SUB_DATA_PROPERTY_OF(Namespaces.OWL, "subDataPropertyOf"),

    @Deprecated
    OWL_DISJOINT_DATA_PROPERTIES(Namespaces.OWL, "disjointDataProperties"),

    @Deprecated
    OWL_DISJOINT_OBJECT_PROPERTIES(Namespaces.OWL, "disjointObjectProperties"),

    OWL_PROPERTY_DISJOINT_WITH(Namespaces.OWL, "propertyDisjointWith"),

    @Deprecated
    OWL_EQUIVALENT_DATA_PROPERTIES(Namespaces.OWL, "equivalentDataProperty"),

    @Deprecated
    OWL_EQUIVALENT_OBJECT_PROPERTIES(Namespaces.OWL, "equivalentObjectProperty"),

    @Deprecated
    OWL_FUNCTIONAL_DATA_PROPERTY(Namespaces.OWL, "FunctionalDataProperty"),

    @Deprecated
    OWL_FUNCTIONAL_OBJECT_PROPERTY(Namespaces.OWL, "FunctionalObjectProperty"),

    OWL_ON_CLASS(Namespaces.OWL, "onClass"),

    OWL_ON_DATA_RANGE(Namespaces.OWL, "onDataRange"),

    OWL_ON_DATA_TYPE(Namespaces.OWL, "onDatatype"),

    OWL_WITH_RESTRICTIONS(Namespaces.OWL, "withRestrictions"),

    OWL_INVERSE_OBJECT_PROPERTY_EXPRESSION(Namespaces.OWL, "inverseObjectPropertyExpression"),

    OWL_AXIOM(Namespaces.OWL, "Axiom"),

    /**
     * @deprecated
     */
    @Deprecated
    OWL_PROPERTY_CHAIN(Namespaces.OWL, "propertyChain"),

    OWL_PROPERTY_CHAIN_AXIOM(Namespaces.OWL, "propertyChainAxiom"),

    OWL_ALL_DISJOINT_CLASSES(Namespaces.OWL, "AllDisjointClasses"),

    OWL_MEMBERS(Namespaces.OWL, "members"),

    OWL_ALL_DISJOINT_PROPERTIES(Namespaces.OWL, "AllDisjointProperties"),

    OWL_TOP_OBJECT_PROPERTY(Namespaces.OWL, "topObjectProperty"),

    OWL_BOTTOM_OBJECT_PROPERTY(Namespaces.OWL, "bottomObjectProperty"),

    OWL_TOP_DATA_PROPERTY(Namespaces.OWL, "topDataProperty"),

    OWL_BOTTOM_DATA_PROPERTY(Namespaces.OWL, "bottomDataProperty"),

    OWL_HAS_KEY(Namespaces.OWL, "hasKey"),

    OWL_ANNOTATED_SOURCE(Namespaces.OWL, "annotatedSource"),

    OWL_ANNOTATED_PROPERTY(Namespaces.OWL, "annotatedProperty"),

    OWL_ANNOTATED_TARGET(Namespaces.OWL, "annotatedTarget"),

    OWL_SOURCE_INDIVIDUAL(Namespaces.OWL, "sourceIndividual"),

    OWL_ASSERTION_PROPERTY(Namespaces.OWL, "assertionProperty"),

    OWL_TARGET_INDIVIDUAL(Namespaces.OWL, "targetIndividual"),

    OWL_TARGET_VALUE(Namespaces.OWL, "targetValue");


    URI uri;

    IRI iri;

    Namespaces namespace;

    String shortName;


    OWLRDFVocabulary(Namespaces namespace, String shortName) {
        this.namespace = namespace;
        this.shortName = shortName;
        this.uri = URI.create(namespace.toString() + shortName);
        this.iri = IRI.create(namespace.toString() + shortName);
    }

    /**
     * @return The URI
     * @deprecated Use getIRI() instead.
     */
    @Deprecated
    public URI getURI() {
        return uri;
    }

    public IRI getIRI() {
        return iri;
    }

    public Namespaces getNamespace() {
        return namespace;
    }


    public String getShortName() {
        return shortName;
    }


    public static final Set<IRI> BUILT_IN_VOCABULARY_IRIS;

    static {
        BUILT_IN_VOCABULARY_IRIS = new HashSet<IRI>();
        for (OWLRDFVocabulary v : OWLRDFVocabulary.values()) {
            BUILT_IN_VOCABULARY_IRIS.add(v.getIRI());
        }
    }

    public static final Set<IRI> BUILT_IN_ANNOTATION_PROPERTY_IRIS;

    static {
        BUILT_IN_ANNOTATION_PROPERTY_IRIS = new HashSet<IRI>();
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(RDFS_LABEL.getIRI());
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(RDFS_COMMENT.getIRI());
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(OWL_VERSION_INFO.getIRI());
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(OWL_BACKWARD_COMPATIBLE_WITH.getIRI());
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(OWL_PRIOR_VERSION.getIRI());
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(RDFS_SEE_ALSO.getIRI());
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(RDFS_IS_DEFINED_BY.getIRI());
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(OWL_INCOMPATIBLE_WITH.getIRI());
        BUILT_IN_ANNOTATION_PROPERTY_IRIS.add(OWL_DEPRECATED.getIRI());
    }


    @Override
	public String toString() {
        return uri.toString();
    }
    
}
