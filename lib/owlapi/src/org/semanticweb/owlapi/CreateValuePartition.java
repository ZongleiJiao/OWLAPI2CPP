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

package org.semanticweb.owlapi;/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jul-2007<br><br>
 * <p/>
 * This composite change will create a value partion - see "pattern 2" in
 * "Representing Specified Values in OWL: "value partitions" and "value sets""
 * (http://www.w3.org/TR/swbp-specified-values.)
 * <p/>
 * A value partition is an ontology design pattern which is used to represent
 * a set of closed values for a particular property.  For example the property
 * hasSize might only take values from SmallSize, MediumSize and LargeSize.  In
 * this case, the value partition is Size, and has the values SmallSize, MediumSize
 * and LargeSize.  This composite change will set hasSize to be functional and its
 * range as Size.  Size will be covered by SmallSize, MediumSize and LargeSize and
 * these classes which represent the values will be made disjoint with eachother.
 */
public class CreateValuePartition extends AbstractCompositeOntologyChange {

    private List<OWLOntologyChange> changes;

    private OWLOntology targetOntology;

    private Set<OWLClass> valuePartionClasses;

    private OWLClass valuePartitionClass;

    private OWLObjectProperty valuePartitionProperty;


    /**
     * Creates a composite change that will create a value partition.
     *
     * @param dataFactory            A data factory which can be used to create the necessary axioms
     * @param valuePartitionClass    The class which represents the value partition.
     * @param valuePartionClasses    The classes that represent the various values of the value
     *                               partition.
     * @param valuePartitionProperty the property which should be used in conjunction with the
     *                               value partition.
     * @param targetOntology         The target ontology which the axioms that are necessary to create
     *                               the value partition will be added to.
     */
    public CreateValuePartition(OWLDataFactory dataFactory, OWLClass valuePartitionClass,
                                Set<OWLClass> valuePartionClasses, OWLObjectProperty valuePartitionProperty,
                                OWLOntology targetOntology) {
        super(dataFactory);
        this.targetOntology = targetOntology;
        this.valuePartionClasses = valuePartionClasses;
        this.valuePartitionClass = valuePartitionClass;
        this.valuePartitionProperty = valuePartitionProperty;

        generateChanges();
    }


    private void generateChanges() {
        changes = new ArrayList<OWLOntologyChange>();
        // To create a value partition from a set of classes which represent the values,
        // a value partition class, a property we...

        // 1) Make the classes which represent the values, subclasses of the value partition class
        for (OWLClassExpression valuePartitionValue : valuePartionClasses) {
            changes.add(new AddAxiom(targetOntology,
                    getDataFactory().getOWLSubClassOfAxiom(valuePartitionValue, valuePartitionClass)));
        }

        // 2) Make the values disjoint
        changes.add(new AddAxiom(targetOntology, getDataFactory().getOWLDisjointClassesAxiom(valuePartionClasses)));

        // 3) Add a covering axiom to the value partition
        OWLClassExpression union = getDataFactory().getOWLObjectUnionOf(valuePartionClasses);
        changes.add(new AddAxiom(targetOntology, getDataFactory().getOWLSubClassOfAxiom(valuePartitionClass, union)));

        // 4) Make the property functional
        changes.add(new AddAxiom(targetOntology,
                getDataFactory().getOWLFunctionalObjectPropertyAxiom(valuePartitionProperty)));

        // 5) Set the range of the property to be the value partition
        changes.add(new AddAxiom(targetOntology,
                getDataFactory().getOWLObjectPropertyRangeAxiom(valuePartitionProperty,
                        valuePartitionClass)));
    }


    public List<OWLOntologyChange> getChanges() {
        return changes;
    }
}
