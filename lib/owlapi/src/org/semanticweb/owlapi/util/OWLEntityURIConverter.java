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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 25-Nov-2007<br><br>
 * <p/>
 * Performs a bulk conversion/translation of entity URIs.  This utility class
 * can be used to replace entity names with IDs for example.  The
 * entity converter is supplied with a set of ontologies and a conversion
 * strategy.  All of the entities that are referenced in the specified
 * ontologies will have their URIs converted according the specified conversion
 * strategy.
 */
public class OWLEntityURIConverter {

    private OWLOntologyManager manager;

    // The ontologies that reference the
    // entities whose names will be converted
    private Collection<OWLOntology> ontologies;

    private Map<OWLEntity, IRI> replacementMap;

    private List<OWLOntologyChange> changes;

    private Set<OWLEntity> processedEntities;

    private OWLEntityURIConverterStrategy strategy;


    /**
     * Creates a converter that will convert the URIs of entities in the specified ontologies
     * using the specified conversion strategy.
     *
     * @param manager    The manager which managers the specified ontologies.
     * @param ontologies The ontologies whose entity URIs will be converted
     * @param strategy   The conversion strategy to be used.
     */
    public OWLEntityURIConverter(OWLOntologyManager manager, Set<OWLOntology> ontologies, OWLEntityURIConverterStrategy strategy) {
        this.manager = manager;
        this.ontologies = new ArrayList<OWLOntology>(ontologies);
        this.strategy = strategy;
    }


    /**
     * Gets the changes required to perform the conversion.
     *
     * @return A list of ontology changes that should be applied in order
     *         to convert the URI of entities in the specified ontologies.
     */
    public List<OWLOntologyChange> getChanges() {
        replacementMap = new HashMap<OWLEntity, IRI>();
        processedEntities = new HashSet<OWLEntity>();
        changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : ontologies) {
            for (OWLClass cls : ont.getClassesInSignature()) {
                if (!cls.isOWLThing() && !cls.isOWLNothing()) {
                    processEntity(cls);
                }
            }
            for (OWLObjectProperty prop : ont.getObjectPropertiesInSignature()) {
                processEntity(prop);
            }
            for (OWLDataProperty prop : ont.getDataPropertiesInSignature()) {
                processEntity(prop);
            }
            for (OWLNamedIndividual ind : ont.getIndividualsInSignature()) {
                processEntity(ind);
            }
        }
        OWLObjectDuplicator dup = new OWLObjectDuplicator(replacementMap, manager.getOWLDataFactory());
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getAxioms()) {
                OWLAxiom dupAx = dup.duplicateObject(ax);
                if (!dupAx.equals(ax)) {
                    changes.add(new RemoveAxiom(ont, ax));
                    changes.add(new AddAxiom(ont, dupAx));
                }
            }
        }
        return changes;
    }

    private void processEntity(OWLEntity ent) {
        if (processedEntities.contains(ent)) {
            return;
        }
        // Add label?
        IRI rep = getTinyURI(ent);
        replacementMap.put(ent, rep);
        processedEntities.add(ent);
    }

    private IRI getTinyURI(OWLEntity ent) {
        return strategy.getConvertedIRI(ent);
    }

}
