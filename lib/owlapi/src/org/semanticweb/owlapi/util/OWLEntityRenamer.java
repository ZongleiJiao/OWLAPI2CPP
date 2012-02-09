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

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 11-Dec-2006<br><br>
 * <p/>
 * Renames entities that have a particular IRI.  Entities with the specified
 * IRI are renamed regardless of whether they are classes, object properties,
 * data properties, individuals or data types.
 */
public class OWLEntityRenamer {

    private OWLOntologyManager owlOntologyManager;

    private Set<OWLOntology> ontologies;


    public OWLEntityRenamer(OWLOntologyManager owlOntologyManager, Set<OWLOntology> ontologies) {
        this.owlOntologyManager = owlOntologyManager;
        this.ontologies = ontologies;
    }


    /**
     * Changes a IRI for another IRI.  This creates the appropriate changes to be
     * applied in order to change a IRI.
     * @param uri The IRI to be changed
     * @param newIRI The IRI that the IRI should be changed to.
     * @return A list of ontology changes that should be applied to change the
     *         specified IRI.
     */
    public List<OWLOntologyChange> changeIRI(IRI uri, IRI newIRI) {
        Map<IRI, IRI> uriMap = new HashMap<IRI, IRI>();
        uriMap.put(uri, newIRI);
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : ontologies) {
            OWLObjectDuplicator dup = new OWLObjectDuplicator(owlOntologyManager.getOWLDataFactory(), uriMap);
            fillListWithTransformChanges(changes, getAxioms(ont, uri), ont, dup);
        }
        return changes;
    }


    /**
     * Changes the IRI of an entity for another IRI.
     * @param entity The entity whose IRI is to be changed.
     * @param newIRI The new IRI
     * @return A list of ontology changes that should be applied to change the
     *         specified entity IRI.
     */
    public List<OWLOntologyChange> changeIRI(OWLEntity entity, IRI newIRI) {
        Map<OWLEntity, IRI> iriMap = new HashMap<OWLEntity, IRI>();
        iriMap.put(entity, newIRI);
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : ontologies) {
            OWLObjectDuplicator duplicator = new OWLObjectDuplicator(iriMap, owlOntologyManager.getOWLDataFactory());
            fillListWithTransformChanges(changes, getAxioms(ont, entity), ont, duplicator);
        }
        return changes;
    }

    public List<OWLOntologyChange> changeIRI(Map<OWLEntity, IRI> entity2IRIMap) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : ontologies) {
            for (OWLEntity ent : entity2IRIMap.keySet()) {
                OWLObjectDuplicator duplicator = new OWLObjectDuplicator(entity2IRIMap, owlOntologyManager.getOWLDataFactory());
                fillListWithTransformChanges(changes, getAxioms(ont, ent), ont, duplicator);
            }
        }
        return changes;
    }

    private static Set<OWLAxiom> getAxioms(OWLOntology ont, OWLEntity entity) {
        Set<OWLAxiom> axioms = ont.getReferencingAxioms(entity);
        axioms.addAll(ont.getDeclarationAxioms(entity));
        axioms.addAll(ont.getAnnotationAssertionAxioms(entity.getIRI()));
        return axioms;
    }

    private Set<OWLAxiom> getAxioms(OWLOntology ont, IRI iri) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        axioms.addAll(ont.getReferencingAxioms(owlOntologyManager.getOWLDataFactory().getOWLClass(iri)));
        axioms.addAll(ont.getReferencingAxioms(owlOntologyManager.getOWLDataFactory().getOWLObjectProperty(iri)));
        axioms.addAll(ont.getReferencingAxioms(owlOntologyManager.getOWLDataFactory().getOWLDataProperty(iri)));
        axioms.addAll(ont.getReferencingAxioms(owlOntologyManager.getOWLDataFactory().getOWLNamedIndividual(iri)));
        axioms.addAll(ont.getReferencingAxioms(owlOntologyManager.getOWLDataFactory().getOWLDatatype(iri)));
        axioms.addAll(ont.getReferencingAxioms(owlOntologyManager.getOWLDataFactory().getOWLAnnotationProperty(iri)));
        axioms.addAll(ont.getAnnotationAssertionAxioms(iri));
        return axioms;
    }


    /**
     * Fills a list with ontology changes which will replace a set of axioms with
     * duplicated/transformed axioms.
     * @param changes A list that will be filled with ontology changes which will remove the
     * specified axioms from the specified ontology, and add the duplicated/transformed version
     * @param axioms The axioms to be duplicated/transformed
     * @param ont The ontology to which the changed should be applied
     * @param duplicator The duplicator that will do the duplicating
     */
    private static void fillListWithTransformChanges(List<OWLOntologyChange> changes, Set<OWLAxiom> axioms, OWLOntology ont, OWLObjectDuplicator duplicator) {
        for (OWLAxiom ax : axioms) {
            changes.add(new RemoveAxiom(ont, ax));
            OWLAxiom dupAx = duplicator.duplicateObject(ax);
            changes.add(new AddAxiom(ont, dupAx));
        }
    }


}
