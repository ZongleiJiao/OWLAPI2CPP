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

package org.semanticweb.owlapi.model;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 14-Apr-2008<br><br>
 * </p>
 * Receives notification of ontology loading starting and finishing from a manager.
 *
 */
public interface OWLOntologyLoaderListener {

    /**
     * Called when the process of attempting to load an ontology starts.
     * @param event The loading started event that describes the ontologt that
     * is being loaded.
     */
    void startedLoadingOntology(LoadingStartedEvent event);


    /**
     * Called when the process of loading an ontology has
     * finished.  This method will be called regardless of whether the
     * ontology could be loaded or not - it merely indicates that the process
     * of attempting to load an ontology has finished.
     * @param event The loading finished event that describes the ontology that was
     * loaded.
     */
    void finishedLoadingOntology(LoadingFinishedEvent event);

    
    public static class LoadingEvent {

        private OWLOntologyID ontologyID;

        private IRI documentIRI;

        private boolean imported;


        public LoadingEvent(OWLOntologyID ontologyID, IRI documentIRI, boolean imported) {
            this.ontologyID = ontologyID;
            this.documentIRI = documentIRI;
            this.imported = imported;
        }

        /**
         * Gets the ID of the ontology being loaded.
         * @return The ontology ID.
         */
        public OWLOntologyID getOntologyID() {
            return ontologyID;
        }


        /**
         * Gets the document IRI for the ontology being loaded
         * @return The document IRI that describes where the ontology
         * was loaded from.
         */
        public IRI getDocumentIRI() {
            return documentIRI;
        }


        /**
         * Determines if the ontology was loaded because of
         * an imports statement.
         * @return <code>true</code> if the ontology was loaded
         * because it was imported by another ontology, or <code>false</code>
         * if the ontology was loaded by a direct load request on OWLOntologyManager.
         */
        public boolean isImported() {
            return imported;
        }
    }

    public static class LoadingStartedEvent extends LoadingEvent {

        public LoadingStartedEvent(OWLOntologyID ontologyID, IRI documentIRI, boolean imported) {
            super(ontologyID, documentIRI, imported);
        }
    }


    /**
     * Describes the situation when the loading process for an ontology has
     * finished.
     */
    public static class LoadingFinishedEvent extends LoadingEvent {

        private OWLOntologyCreationException ex;

        public LoadingFinishedEvent(OWLOntologyID ontologyID, IRI documentIRI, boolean imported, OWLOntologyCreationException ex) {
            super(ontologyID, documentIRI, imported);
            this.ex = ex;
        }


        /**
         * Determines if the ontology was successfully loaded.
         * @return <code>true</code> if the ontology was successfully loaded,
         * <code>false</code> if the ontology was not successfully loaded. Note
         * that an ontology being successfully loaded does not imply that any ontologies
         * that the ontology imports were successfully loaded.
         */
        public boolean isSuccessful() {
            return ex == null;
        }


        /**
         * If the ontology was not loaded successfully then this method can
         * be used to access the exception that describes why the ontology was
         * not loaded successfully.
         * @return The exception that describes why the ontology was not
         * loaded successfully, or <code>null</code> if the ontology was loaded successfully.
         */
        public OWLOntologyCreationException getException() {
            return ex;
        }
    }

}
