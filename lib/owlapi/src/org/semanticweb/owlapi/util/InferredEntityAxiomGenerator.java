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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Jul-2007<br><br>
 * Generates axioms which relate to inferred information for a specific entity.
 */
public abstract class InferredEntityAxiomGenerator<E extends OWLEntity, A extends OWLAxiom> implements InferredAxiomGenerator<A> {


    public Set<A> createAxioms(OWLOntologyManager manager, OWLReasoner reasoner) {
        Set<E> processedEntities = new HashSet<E>();
        Set<A> result = new HashSet<A>();
        for (OWLOntology ont : reasoner.getRootOntology().getImportsClosure()) {
            for (E entity : getEntities(ont)) {
                if (!processedEntities.contains(entity)) {
                    processedEntities.add(entity);
                    addAxioms(entity, reasoner, manager.getOWLDataFactory(), result);
                }
            }
        }
        return result;
    }


    /**
     * Adds inferred axioms to a results set.  The inferred axioms are generated for the specific entity.
     * @param entity The entity
     * @param reasoner The reasoner that has inferred the new axioms
     * @param dataFactory A data factory which should be used to create the new axioms
     * @param result The results set, which the new axioms should be added to.
     */
    protected abstract void addAxioms(E entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<A> result);


    /**
     * Gets the entities from the specified ontology that this generator processes
     * @param ont The ontology from which entities are to be retrieved.
     * @return A set of entities.
     */
    protected abstract Set<E> getEntities(OWLOntology ont);

    protected Set<E> getAllEntities(OWLReasoner reasoner) {
        Set<E> results = new HashSet<E>();
        for (OWLOntology ont : reasoner.getRootOntology().getImportsClosure()) {
            results.addAll(getEntities(ont));
        }
        return results;
    }


    @Override
	public String toString() {
        return getLabel();
    }
}
