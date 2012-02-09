/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, Clark & Parsia, LLC
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
 * Copyright 2011, Clark & Parsia, LLC
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

package com.clarkparsia.owlapi.explanation.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;

public class DefinitionTracker implements OWLOntologyChangeListener {

    /**
     * Mapping from entities to the number of axioms
     */
    private Map<OWLEntity, Integer> referenceCounts = new HashMap<OWLEntity, Integer>();

    private OWLOntology ontology;

	private Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

    private OWLOntologyManager manager;

    private Integer ONE = Integer.valueOf(1);


    public DefinitionTracker(OWLOntology ontology) {
        this.manager = ontology.getOWLOntologyManager();
        this.ontology = ontology;
        for (OWLOntology ont : ontology.getImportsClosure()) {
			for (OWLOntology importOnt : manager.getImportsClosure(ont)) {
//	            if (this.ontologies.add(importOnt)) {
					for (OWLAxiom axiom : importOnt.getAxioms()) {
		                addAxiom(axiom);
					}
//	            }
	        }
		}
        manager.addOntologyChangeListener(this);
    }
    
//    private void clear() {
//        axioms.clear();
//        ontologies.clear();
//        referenceCounts.clear();
//    }

	private void addAxiom(OWLAxiom axiom) {
        if (axioms.add(axiom)) {
            for (OWLEntity entity : getEntities(axiom)) {
                Integer count = referenceCounts.get(entity);
                if (count == null)
                    count = ONE;
                else
                    count = count + 1;
                referenceCounts.put(entity, count);
            }
        }
    }


    private Set<OWLEntity> getEntities(OWLObject obj) {
    	return obj.getSignature();
    }

	private void removeAxiom(OWLAxiom axiom) {
        if (axioms.remove(axiom)) {
            for (OWLEntity entity : getEntities(axiom)) {
                Integer count = referenceCounts.get(entity);
                if (count == 1)
                    referenceCounts.remove(entity);
                else
                    referenceCounts.put(entity, count - 1);
            }
        }
    }

//	public void setOntology(OWLOntology ontology) {
//		setOntologies( Collections.singleton( ontology ) );
//	}

//	public void setOntologies(Set<OWLOntology> ontologies) {
//		clear();
//		for (OWLOntology ont : ontologies) {
//			for (OWLOntology importOnt : manager.getImportsClosure(ont)) {
//	            if (this.ontologies.add(importOnt)) {
//					for (OWLAxiom axiom : importOnt.getAxioms()) {
//		                addAxiom(axiom);
//					}
//	            }
//	        }
//		}
//	}

//    public Set<OWLOntology> getOntologies() {
//    	return ontologies;
//    }

    /**
     * Checks if this entity is referred by a logical axiom in the imports
     * closure of the designated ontology.
     * @param entity entity we are searching for
     * @return <code>true</code> if there is at least one logical axiom in the
     *         imports closure of the given ontology that refers the given
     *         entity
     */
    public boolean isDefined(OWLEntity entity) {
        return entity.isBuiltIn() || referenceCounts.containsKey(entity);
    }


    /**
     * Checks if all the entities referred in the given concept are also
     * referred by a logical axiom in the imports closure of the designated
     * ontology.
     * @param classExpression description that contains the entities we are searching for
     * @return <code>true</code> if all the entities in the given description
     *         are referred by at least one logical axiom in the imports closure
     *         of the given ontology
     */
    public boolean isDefined(OWLClassExpression classExpression) {
        for (OWLEntity entity : getEntities(classExpression)) {
            if (!isDefined(entity))
                return false;
        }
        return true;
    }


    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
        for (OWLOntologyChange change : changes) {
            if (!change.isAxiomChange() || !ontology.getImportsClosure().contains(change.getOntology()))
                continue;

			final OWLAxiom axiom = change.getAxiom();

            if (change instanceof AddAxiom)
				addAxiom( axiom );
            else if (change instanceof RemoveAxiom)
				removeAxiom( axiom );
            else
                throw new UnsupportedOperationException("Unrecognized axiom change: " + change);
        }
    }
}
