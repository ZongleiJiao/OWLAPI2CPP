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

package org.semanticweb.owlapi.metrics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.NamedConjunctChecker;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Jul-2007<br><br>
 */
public class NumberOfClassesWithMultipleInheritance extends IntegerValuedMetric {


    public NumberOfClassesWithMultipleInheritance(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
    }


    public String getName() {
        return "Number of classes with asserted multiple inheritance";
    }


    @Override
	public Integer recomputeMetric() {
        Set<OWLClass> processed = new HashSet<OWLClass>();
        Set<OWLClass> clses = new HashSet<OWLClass>();
        NamedConjunctChecker checker = new NamedConjunctChecker();
        for (OWLOntology ont : getOntologies()) {
            for (OWLClass cls : ont.getClassesInSignature()) {
                if (processed.contains(cls)) {
                    continue;
                }
                processed.add(cls);
                int count = 0;
                for (OWLClassExpression sup : cls.getSubClasses(getOntologies())) {
                    if (checker.hasNamedConjunct(sup)) {
                        count++;
                    }
                    if (count > 1) {
                        clses.add(cls);
                        break;
                    }
                }
            }
        }
        return clses.size();
    }


    @Override
	protected boolean isMetricInvalidated(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange()) {
                if (change.getAxiom() instanceof OWLSubClassOfAxiom) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
	protected void disposeMetric() {
    }
}
